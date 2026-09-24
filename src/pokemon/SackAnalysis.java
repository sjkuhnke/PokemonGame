package pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Phase 5 (§7.14.2 min-gain guard, §7.14.4 classification). A sack is not a special action: it is an ordinary SWITCH (or
 * MOVE_THEN_SWITCH) row whose simulated outcome is "the target absorbs the hit and faints, and the fainted side gets a
 * free replacement". This class only (1) finds the player's threat columns, (2) drops sack rows that do not clearly beat
 * staying in, and (3) labels the chosen row afterwards for reporting. It never adds a rule that picks a sack.
 * <p>
 * "Sack row" here means a row whose target is a sack-ONLY candidate: a low-value bench mon that is not also one of the
 * top-N answers ({@link ActionGen.BenchPlan#sackOnly}). A row into a mon that is a real answer is never touched; the
 * guard exists to stop a throwaway from picking up mixed probability for nothing.
 */
public final class SackAnalysis {
	/** A player move is a "threat" when accuracy * P(KO) against the AI's active is at least this (§7.14.2 "about 0.3"). */
	static final double THREAT_KO = 0.3;
	/** P(target faints) at or above this labels the chosen row a sack (§7.14.4). */
	static final double SACK_LABEL_P = 0.5;

	private SackAnalysis() {}

	// ---- threats ----

	/** accuracy * P(one use of m KOs defender at its current HP). 0 if unusable/immune. */
	public static double koChance(Pokemon attacker, Pokemon defender, Move m, Field field) {
		if (m == null || m.cat == 2) return 0;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			DamageRange r = attacker.calcRange(defender, m, true, field);
			if (!r.usable || r.immune) return 0;
			return r.koProb(defender.currentHP) * r.accuracy;
		}
	}

	/** True if any of attacker's valid moves is a threat (>= THREAT_KO) to defender's current HP. */
	public static boolean threatens(Pokemon attacker, Pokemon defender, Field field) {
		if (attacker == null || defender == null || attacker.isFainted() || defender.isFainted()) return false;
		for (Move m : attacker.getValidMoveset()) {
			if (koChance(attacker, defender, m, field) >= THREAT_KO) return true;
		}
		return false;
	}

	/** P columns that are player moves (plain or pivot) threatening the AI's active. Switch columns and PASS are never threats. */
	public static boolean[] threatColumns(SimState root, List<Action> P) {
		boolean[] t = new boolean[P.size()];
		Pokemon aiMon = root.ai.active(), plMon = root.player.active();
		for (int j = 0; j < P.size(); j++) {
			Action p = P.get(j);
			if (p.kind == ActionKind.SWITCH || p.move == null) continue;
			t[j] = koChance(plMon, aiMon, p.move, root.field) >= THREAT_KO;
		}
		return t;
	}

	// ---- min-gain guard ----

	static boolean isSackRow(Action a, Set<Integer> sackOnly) {
		return (a.kind == ActionKind.SWITCH || a.kind == ActionKind.MOVE_THEN_SWITCH) && sackOnly.contains(a.slot);
	}

	public static final class Filtered {
		public final List<Action> A;
		public final double[][] M;
		public final List<Action> dropped;
		/** Per ORIGINAL row: mean gain over Stay in the threat columns for sack rows, NaN for every other row. */
		public final double[] gain;

		Filtered(List<Action> A, double[][] M, List<Action> dropped, double[] gain) {
			this.A = A;
			this.M = M;
			this.dropped = dropped;
			this.gain = gain;
		}
	}

	/**
	 * Pure. Stay[j] = best payoff in column j among the plain MOVE rows. A sack row is kept only if the player has at
	 * least one threat column AND its mean (row - Stay) over the threat columns is >= minGain. With no threat there is
	 * nothing to sack against, so every sack row goes (T20). Non-sack rows are never dropped; if there is no plain MOVE
	 * row to compare with, or filtering would empty the set, everything is kept.
	 */
	public static Filtered applyMinGain(List<Action> A, double[][] M, boolean[] threat, Set<Integer> sackOnly, double minGain) {
		int nRows = A.size();
		int nCols = threat.length;
		double[] gain = new double[nRows];
		java.util.Arrays.fill(gain, Double.NaN);

		double[] stay = new double[nCols];
		java.util.Arrays.fill(stay, Double.NEGATIVE_INFINITY);
		boolean anyStay = false;
		for (int i = 0; i < nRows; i++) {
			Action a = A.get(i);
			if (a.kind != ActionKind.MOVE || a.move == null) continue;
			anyStay = true;
			for (int j = 0; j < nCols; j++) stay[j] = Math.max(stay[j], M[i][j]);
		}
		int nThreat = 0;
		for (boolean b : threat) if (b) nThreat++;

		List<Integer> keep = new ArrayList<>();
		List<Action> dropped = new ArrayList<>();
		for (int i = 0; i < nRows; i++) {
			Action a = A.get(i);
			if (!isSackRow(a, sackOnly) || !anyStay) {
				keep.add(i);
				continue;
			}
			double g = 0;
			for (int j = 0; j < nCols; j++) if (threat[j]) g += M[i][j] - stay[j];
			g = nThreat > 0 ? g / nThreat : Double.NEGATIVE_INFINITY;
			gain[i] = g;
			if (nThreat > 0 && g >= minGain) keep.add(i);
			else dropped.add(a);
		}
		if (keep.isEmpty() || keep.size() == nRows) return new Filtered(A, M, new ArrayList<Action>(), gain);

		List<Action> A2 = new ArrayList<>(keep.size());
		double[][] M2 = new double[keep.size()][];
		for (int k = 0; k < keep.size(); k++) {
			A2.add(A.get(keep.get(k)));
			M2[k] = M[keep.get(k)];
		}
		return new Filtered(A2, M2, dropped, gain);
	}

	// ---- classification (§7.14.4) ----

	public static final class Label {
		public boolean sack;
		/** P(the switched-in target is fainted at the end of the turn) over the predicted player distribution. */
		public double pTargetFaints;
		/** Team index of the most likely replacement in the branches where the target fainted; -1 if it never faints. */
		public int replacementSlot = -1;
	}

	/**
	 * Re-simulates the chosen SWITCH / MOVE_THEN_SWITCH row against every column with weight in y (the predicted
	 * player distribution; the raw equilibrium y until Phase 6's model). One simulateTurn per column, only for the
	 * chosen row, so this is cheap. A plain MOVE row is never a sack.
	 */
	public static Label classify(SimState root, Action chosen, List<Action> P, double[] y, AIConfig cfg) {
		Label L = new Label();
		if (chosen.kind == ActionKind.MOVE) return L;
		int slot = chosen.slot;
		double faint = 0, total = 0;
		Map<Integer, Double> plan = new HashMap<>();
		for (int j = 0; j < P.size() && j < y.length; j++) {
			if (y[j] <= 1e-9) continue;
			for (Branch br : BattleSimulator.simulateTurn(root, chosen, P.get(j), cfg)) {
				double w = y[j] * br.prob;
				total += w;
				Pokemon t = br.state.ai.bench()[slot];
				if (t != null && t.isFainted()) {
					faint += w;
					int r = br.state.ai.shell.indexOf(br.state.ai.active());
					plan.merge(r, w, Double::sum);
				}
			}
		}
		L.pTargetFaints = total > 0 ? faint / total : 0;
		double bestW = -1;
		for (Map.Entry<Integer, Double> e : plan.entrySet()) {
			if (e.getValue() > bestW) {
				bestW = e.getValue();
				L.replacementSlot = e.getKey();
			}
		}
		L.sack = L.pTargetFaints >= SACK_LABEL_P;
		return L;
	}
}