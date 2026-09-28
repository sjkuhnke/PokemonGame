package pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Phase 5 (§7.14.2 min-gain guard, §7.14.4 classification). A sack is a switch (or pivot-switch) into a low-value bench
 * mon to spare the current active - whether or not that mon survives the incoming hit. Surviving, chipping the threat,
 * or using a support move on the way out is still a valid reason to send it in; nothing here requires it to faint. This
 * class only (1) finds the player's threat columns, (2) drops sack-only rows that do not clearly beat staying in, and
 * (3) labels the chosen row afterwards for reporting. It never adds a rule that picks a sack.
 * <p>
 * "Sack-only row" (the guard's scope) means a row whose target is a sack candidate that is NOT also one of the top-N
 * answers ({@link ActionGen.BenchPlan#sackOnly}). A row into a mon that is a real answer is never touched by the guard;
 * it exists to stop a throwaway from picking up mixed probability for nothing.
 * <p>
 * The {@link Label} produced afterwards is broader than the guard's scope: it labels ANY switch into ANY sack candidate
 * ({@link ActionGen.BenchPlan#sacks}, sack-only or not) as a sack for reporting, regardless of whether the target ends
 * up fainting. {@link Label#pTargetFaints} stays purely informational - it decides only whether a replacement plan is
 * worth printing (a target that usually survives has no "free replacement" to plan for), never whether the label fires.
 */
public final class SackAnalysis {
	/** A player move is a "threat" when accuracy * P(KO) against the AI's active is at least this (§7.14.2 "about 0.3"). */
	public static final double THREAT_KO = 0.3;
	/** P(target faints) at or above this is worth reporting a replacement plan for (§7.14.4). Informational only - it
	 * no longer decides {@link Label#sack}, which now follows purely from what kind of row was chosen. */
	public static final double SACK_LABEL_P = 0.5;

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
			double g;
			if (nThreat > 0) {
				double sum = 0;
				for (int j = 0; j < nCols; j++) if (threat[j]) sum += M[i][j] - stay[j];
				g = sum / nThreat;
			} else {
				// No player move threatens an outright KO this turn - the guard's safety check (§7.14.2's "don't
				// throw a mon away for nothing") has no acute crisis to weigh against, so it judges the sack
				// against the OVERALL matchup instead of reflexively vetoing it: the mean gain over every player
				// column, not just threat ones. A genuinely bad sack still gets dropped here (its mean gain stays
				// below minGain); a genuinely good one - a low-value mon in a clearly bad long-run matchup - can
				// now survive without needing an imminent one-hit-KO to justify it. Previously this branch was a
				// hard -Infinity (always drop), which was closer to a blanket ban on sacking outside OHKO
				// emergencies than the safety check the spec describes - see PHASE5_CHANGES.md.
				double sum = 0;
				for (int j = 0; j < nCols; j++) sum += M[i][j] - stay[j];
				g = nCols > 0 ? sum / nCols : Double.NEGATIVE_INFINITY;
			}
			gain[i] = g;
			if (g >= minGain) keep.add(i);
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
		/** True when the chosen row switches (or pivot-switches) into ANY sack candidate - independent of whether the
		 * target ends up fainting. Set directly from the row and the candidate set the caller supplies; classify()
		 * does not re-derive candidacy from simulated outcomes. */
		public boolean sack;
		/** P(the switched-in target is fainted at the end of the turn) over the predicted player distribution.
		 * Informational only (see class doc) - does not gate {@link #sack}. */
		double pTargetFaints;
		/** Team index of the most likely replacement in the branches where the target fainted. -1 when the target
		 * doesn't tend to faint (pTargetFaints below SACK_LABEL_P) - there's no meaningful "free replacement" to
		 * report in that case, so callers should omit the replacement-plan line rather than print "unknown". */
		public int replacementSlot = -1;
	}

	/**
	 * Labels {@code chosen} a sack purely from whether it's a switch/pivot-switch into {@code sackCandidates} (ANY sack
	 * candidate, not just sack-ONLY rows - a candidate that's also a top-N answer still counts as a sack when chosen).
	 * Then, ONLY to decide whether a replacement plan is worth reporting, re-simulates the row against every column
	 * with weight in y (the predicted player distribution; the raw equilibrium y until Phase 6's model) to see how
	 * often the target actually faints. One simulateTurn per column, only for the chosen row, so this stays cheap.
	 */
	public static Label classify(SimState root, Action chosen, List<Action> P, double[] y, AIConfig cfg, Set<Integer> sackCandidates) {
		Label L = new Label();
		L.sack = (chosen.kind == ActionKind.SWITCH || chosen.kind == ActionKind.MOVE_THEN_SWITCH) && sackCandidates.contains(chosen.slot);
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
		if (L.pTargetFaints >= SACK_LABEL_P) {
			double bestW = -1;
			for (Map.Entry<Integer, Double> e : plan.entrySet()) {
				if (e.getValue() > bestW) {
					bestW = e.getValue();
					L.replacementSlot = e.getKey();
				}
			}
		}
		return L;
	}
}