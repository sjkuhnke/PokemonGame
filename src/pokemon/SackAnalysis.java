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
		/** Rows and payoffs BEFORE filtering (§7.14.4 diagnostics: a dropped row's own cells aren't in A/M above, since
		 * those are already filtered). originalA.indexOf(droppedAction) finds a dropped row's index into originalM. */
		public final List<Action> originalA;
		public final double[][] originalM;
		/** Per player column: the best payoff among plain Stay (MOVE) rows, same figure "gain" was computed against. */
		public final double[] stay;
		/** Per player column: whether it was a threat column (§7.14.2, KO chance >= THREAT_KO) - context for "gain",
		 * which averages over threat columns when any exist, or over all columns otherwise. */
		public final boolean[] threat;
		/** Why a row was dropped, when the reason is more specific than the min-gain guard (e.g. the same-mon-return rule). */
		public final Map<Action, String> dropReason = new HashMap<>();

		public Filtered(List<Action> A, double[][] M, List<Action> dropped, double[] gain, List<Action> originalA, double[][] originalM,
				double[] stay, boolean[] threat) {
			this.A = A;
			this.M = M;
			this.dropped = dropped;
			this.gain = gain;
			this.originalA = originalA;
			this.originalM = originalM;
			this.stay = stay;
			this.threat = threat;
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
		if (keep.isEmpty() || keep.size() == nRows) return new Filtered(A, M, new ArrayList<Action>(), gain, A, M, stay, threat);

		List<Action> A2 = new ArrayList<>(keep.size());
		double[][] M2 = new double[keep.size()][];
		for (int k = 0; k < keep.size(); k++) {
			A2.add(A.get(keep.get(k)));
			M2[k] = M[keep.get(k)];
		}
		return new Filtered(A2, M2, dropped, gain, A, M, stay, threat);
	}

	// ---- same-mon-return rule (Phase 5 follow-up) ----

	/**
	 * A sack is only worth a mon if the free switch-in does something. A sack whose target faints and whose replacement is the
	 * mon that was already out puts that mon back into the position it just left, one mon poorer; it only helps if that position
	 * is better the second time, i.e. the thing that forced the sack is gone. So such a row is dropped unless:
	 * <ul>
	 * <li>resetting the active helps ({@link #resetValue} &gt; 0: it clears negative stages or bad volatiles by more than it costs in
	 * boosts), or</li>
	 * <li>the sack was spared a real KO (some player move threatens one this turn) AND that threat does not come back: after the
	 * turn the foe in play no longer threatens the returned mon (it fainted, was replaced, ...). If the same foe still threatens
	 * the same mon, the KO is merely postponed one turn at the price of a mon (a sack loop: Gulpin and Rockmite in the logs).</li>
	 * </ul>
	 * Without a KO threat this turn the second condition cannot rescue the row (there was nothing to be spared).
	 * <p>
	 * Pure filter over the (already min-gain-filtered) rows. Returns null when nothing changed, otherwise a Filtered whose
	 * A/M are the surviving rows and whose dropped/originalA/originalM also carry the earlier guard's results.
	 */
	public static Filtered dropSameMonReturns(SimState root, List<Action> A, double[][] M, List<Action> P, boolean[] threat,
			Set<Integer> sackCandidates, AIConfig cfg, Filtered prior) {
		boolean anyThreat = false;
		for (boolean b : threat) if (b) anyThreat = true;
		Pokemon active = root.ai.active();
		int activeIdx = root.ai.shell.indexOf(active);
		double reset = resetValue(active);
		if (reset > 0 || activeIdx < 0) return null;

		List<Integer> keep = new ArrayList<>();
		List<Action> dropped = new ArrayList<>();
		Map<Action, String> reasons = new HashMap<>();
		for (int i = 0; i < A.size(); i++) {
			Action a = A.get(i);
			boolean sackRow = (a.kind == ActionKind.SWITCH || a.kind == ActionKind.MOVE_THEN_SWITCH) && sackCandidates.contains(a.slot);
			double[] st = sackRow ? sameMonStats(root, a, P, cfg, activeIdx) : null; // {share returning the same mon, share of those where it is threatened again}; null/-1 = n/a
			boolean drop = false;
			String why = null;
			if (st != null && st[0] >= 0.5) {
				if (!anyThreat) {
					drop = true;
					why = String.format(java.util.Locale.ROOT,
							"same-mon return: the target faints and the replacement is %s in %.0f%% of those branches; no KO threat this turn to be spared; resetting %s gains nothing (reset value %.1f)",
							active, st[0] * 100, active, reset);
				} else if (st[1] >= 0.5) {
					drop = true;
					why = String.format(java.util.Locale.ROOT,
							"same-mon return: the target faints and the replacement is %s in %.0f%% of those branches; the KO it is spared this turn comes back (%s is threatened again in %.0f%% of them); resetting gains nothing (reset value %.1f)",
							active, st[0] * 100, active, st[1] * 100, reset);
				}
			}
			if (drop) {
				dropped.add(a);
				reasons.put(a, why);
			} else {
				keep.add(i);
			}
		}
		if (dropped.isEmpty() || keep.isEmpty()) return null;

		List<Action> A2 = new ArrayList<>(keep.size());
		double[][] M2 = new double[keep.size()][];
		for (int k = 0; k < keep.size(); k++) {
			A2.add(A.get(keep.get(k)));
			M2[k] = M[keep.get(k)];
		}
		List<Action> allDropped = new ArrayList<>();
		if (prior != null) allDropped.addAll(prior.dropped);
		allDropped.addAll(dropped);

		double[] gain, stay;
		List<Action> origA;
		double[][] origM;
		if (prior != null) {
			gain = prior.gain;
			stay = prior.stay;
			origA = prior.originalA;
			origM = prior.originalM;
		} else {
			origA = A;
			origM = M;
			gain = new double[A.size()];
			java.util.Arrays.fill(gain, Double.NaN);
			stay = new double[threat.length];
			java.util.Arrays.fill(stay, Double.NEGATIVE_INFINITY);
			for (int i = 0; i < A.size(); i++) {
				if (A.get(i).kind != ActionKind.MOVE || A.get(i).move == null) continue;
				for (int j = 0; j < threat.length; j++) stay[j] = Math.max(stay[j], M[i][j]);
			}
		}
		Filtered f = new Filtered(A2, M2, allDropped, gain, origA, origM, stay, threat);
		if (prior != null) f.dropReason.putAll(prior.dropReason);
		f.dropReason.putAll(reasons);
		return f;
	}

	/**
	 * Over the row's branches (uniform over player columns) in which its target faints: [0] = the weight share where the free
	 * replacement is the mon that was already active; [1] = of THOSE, the share where the foe in play still threatens the returned
	 * mon with a KO (threatens(), evaluated on the post-turn state). {-1, 0} when the target never faints (not a same-mon return).
	 */
	static double[] sameMonStats(SimState root, Action row, List<Action> P, AIConfig cfg, int activeIdx) {
		double faintW = 0, sameW = 0, recurW = 0;
		for (int j = 0; j < P.size(); j++) {
			for (Branch br : BattleSimulator.simulateTurn(root, row, P.get(j), cfg)) {
				Pokemon t = br.state.ai.bench()[row.slot];
				if (t == null || !t.isFainted()) continue;
				faintW += br.prob;
				if (br.state.ai.shell.indexOf(br.state.ai.active()) != activeIdx) continue;
				sameW += br.prob;
				Field prev = Pokemon.field;
				try {
					Pokemon.field = br.state.field;
					if (threatens(br.state.player.active(), br.state.ai.active(), br.state.field)) recurW += br.prob;
				} finally {
					Pokemon.field = prev;
				}
			}
		}
		if (faintW <= 1e-9) return new double[] { -1, 0 };
		return new double[] { sameW / faintW, sameW > 1e-9 ? recurW / sameW : 0 };
	}

	/**
	 * What resetting {@code m} by switching it out gains minus what it loses, in rough stat-stage units: each negative
	 * stage cleared is +1, each positive stage lost is -1, and each bad volatile that switching clears is +2 (Taunt,
	 * Encore, Torment, Disable, confusion, Leech Seed, Curse, Nightmare, Yawn/Drowsy, Heal Block, Mute, a running Perish
	 * count) plus Natural Cure on a statused mon.
	 */
	public static double resetValue(Pokemon m) {
		double v = 0;
		for (int s : m.statStages) v += -s;
		Status[] bad = { Status.TAUNTED, Status.ENCORED, Status.TORMENTED, Status.CONFUSED, Status.LEECHED, Status.CURSED,
				Status.NIGHTMARE, Status.YAWNING, Status.DROWSY, Status.HEAL_BLOCK, Status.MUTE };
		for (Status b : bad) if (m.hasStatus(b)) v += 2;
		if (m.disabledMove != null) v += 2;
		if (m.perishCount > 0) v += 2;
		if (m.status != null && m.status != Status.HEALTHY && m.getAbility(Pokemon.field) == Ability.NATURAL_CURE) v += 2;
		return v;
	}

	// ---- diagnostics (§7.14.4): per-column detail for a DROPPED sack-only row ----

	/** One player column's outcome for a specific AI row, from re-simulating it (not read off the matrix, which only
	 * holds the already-weighted eval score). */
	public static final class ColumnOutcome {
		/** P(the target - the row's switch destination - is fainted at the end of this specific column). */
		public double pFaint;
		/** Mean remaining HP fraction of the target IN THE BRANCHES WHERE IT SURVIVES. NaN if it always faints. */
		public double avgHpFracAlive;
	}

	/**
	 * Per player column, what actually happens if {@code row} (a SWITCH or MOVE_THEN_SWITCH) is played against that
	 * column: does the target faint, and if not, how much HP does it keep. One simulateTurn per column - meant for a
	 * handful of DROPPED rows in a debug log, not the hot path. Rows that don't switch don't have a "target", so this
	 * is only meaningful for SWITCH/MOVE_THEN_SWITCH.
	 */
	public static ColumnOutcome[] columnOutcomes(SimState root, Action row, List<Action> P, AIConfig cfg) {
		ColumnOutcome[] out = new ColumnOutcome[P.size()];
		int slot = row.slot;
		for (int j = 0; j < P.size(); j++) {
			ColumnOutcome co = new ColumnOutcome();
			double total = 0, faintW = 0, hpSum = 0, hpW = 0;
			for (Branch br : BattleSimulator.simulateTurn(root, row, P.get(j), cfg)) {
				total += br.prob;
				Pokemon t = br.state.ai.bench()[slot];
				if (t == null || t.isFainted()) {
					faintW += br.prob;
				} else {
					hpSum += br.prob * (t.currentHP * 1.0 / t.getStat(0));
					hpW += br.prob;
				}
			}
			co.pFaint = total > 0 ? faintW / total : 0;
			co.avgHpFracAlive = hpW > 0 ? hpSum / hpW : Double.NaN;
			out[j] = co;
		}
		return out;
	}

	// ---- classification (§7.14.4) ----

	public static final class Label {
		/** True when the chosen row switches (or pivot-switches) into ANY sack candidate - independent of whether the
		 * target ends up fainting. Set directly from the row and the candidate set the caller supplies; classify()
		 * does not re-derive candidacy from simulated outcomes. */
		public boolean sack;
		/** P(the switched-in target is fainted at the end of the turn) over the predicted player distribution.
		 * Informational only (see class doc) - does not gate {@link #sack}. */
		public double pTargetFaints;
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