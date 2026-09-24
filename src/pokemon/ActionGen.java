package pokemon;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * §7.4 genAIActions/genPlayerActions (Phase 4: + §9 usefulRows filter), §7.13.1 switchRowsAllowed/deadTurn, §7.14.2
 * candidateBench/sackCandidates. Phase 5: {@link #benchPlan} keeps the answers/sacks split (the min-gain guard needs to
 * know which rows are sack-only), sack candidates honor {@code cfg.enableSacking}, and the player's sack column is only
 * kept while the AI actually threatens the player's active.
 */
public final class ActionGen {
	/** deadTurn's "did anything change" threshold (§7.13.1). */
	private static final double DEAD_EPS = 1.0;

	private ActionGen() {}

	// ---- §7.4 ----

	public static List<Action> genAIActions(SimState root, AIConfig cfg, MonWeights weights) {
		List<Action> A = new ArrayList<>();
		Pokemon self = root.ai.active();
		Pokemon foe = root.player.active();
		List<Move> validMoves = usefulRows(self, foe, root.field, self.getValidMoveset());
		boolean canSwitch = self.trainer.canSwitch(foe);

		List<Integer> switchTargets = canSwitch ? benchPlan(root, cfg, weights).all() : java.util.Collections.emptyList();
		Ability foeAbility = canSwitch ? foe.getAbility(root.field) : null;

		for (Move m : validMoves) {
			// A pivot move whose switch-in is already covered by explicit MOVE_THEN_SWITCH rows
			// below is NOT also added plain: a plain pivot's post-move replacement is the forced-replacement
			// chooser (ReplacementChooser, Phase 5: the same function the real battle uses), not a switch
			// the AI planned in the matrix - so the explicit rows are the only way the AI picks the target.
			boolean pivotCoveredBySwitchRows = canSwitch && !switchTargets.isEmpty()
					&& m.isPivotMove() && self.isUsefulPivot(foe, foeAbility, m);
			if (!pivotCoveredBySwitchRows) A.add(new Action(m));
			if (pivotCoveredBySwitchRows) {
				for (int slot : switchTargets) A.add(new Action(m, slot));
			}
		}

		if (canSwitch) {
			boolean voluntaryOK = switchRowsAllowed(root, cfg, weights);
			if (voluntaryOK) {
				for (int slot : switchTargets) A.add(new Action(slot));
			}
		}
		return A;
	}

	/** considerPlayerSwitches is true at every difficulty per §7.4; infoMode FULL only this phase (REVEALED_ONLY is Phase 6). */
	public static List<Action> genPlayerActions(SimState root, AIConfig cfg) {
		return genPlayerActions(root, cfg, MonWeights.compute(root));
	}

	/** Phase 5: takes the decision's weights so the player sack column does not recompute them. */
	public static List<Action> genPlayerActions(SimState root, AIConfig cfg, MonWeights weights) {
		List<Action> P = new ArrayList<>();
		Pokemon player = root.player.active();
		Pokemon aiMon = root.ai.active();
		List<Move> validMoves = usefulRows(player, aiMon, root.field, player.getValidMoveset());
		for (Move m : validMoves) P.add(new Action(m));

		if (player.trainer.canSwitch(aiMon)) {
			List<Integer> switchCols = playerSwitchColumns(root, cfg, weights);
			for (int slot : switchCols) P.add(new Action(slot));
			Ability aiAbility = aiMon.getAbility(root.field);
			for (Move m : validMoves) {
				if (m.isPivotMove() && player.isUsefulPivot(aiMon, aiAbility, m)) {
					for (int slot : switchCols) P.add(new Action(m, slot));
				}
			}
		}
		return P;
	}

	// ---- §9 row filter (Phase 4) ----

	/**
	 * Drops rows that provably do nothing this turn, so they neither cost matrix cells nor split probability mass:
	 * Fake Out / First Impression / Dream Eater and friends when {@code calcRange} says unusable (damaging moves only:
	 * for those {@code usable=false} always means "cannot be used now"), Sleep Talk / Snore while awake, and hazard moves
	 * that {@code isHazardUseful} rejects. Never returns an empty list: if everything would be dropped the unfiltered
	 * list is returned (the sim then plays it out as a failed move).
	 */
	public static List<Move> usefulRows(Pokemon self, Pokemon foe, Field field, List<Move> valid) {
		List<Move> out = new ArrayList<>(valid.size());
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			for (Move m : valid) {
				if ((m == Move.SLEEP_TALK || m == Move.SNORE) && self.status != Status.ASLEEP) continue;
				if (m.isHazard() && !self.isHazardUseful(m, foe, field)) continue;
				if (m.cat != 2 && !self.calcRange(foe, m, true, field).usable) continue;
				out.add(m);
			}
		}
		return out.isEmpty() ? valid : out;
	}

	// ---- §7.13.1 ----

	public static boolean switchRowsAllowed(SimState root, AIConfig cfg, MonWeights weights) {
		if (cfg.allowVoluntarySwitch) return true; // HARD / EXTREME
		Pokemon self = root.ai.active();
		if (self.perishCount == 1) return true;
		if (self.getValidMoveset().isEmpty()) return true; // Struggle-only
		return deadTurn(root, cfg);
	}

	/** Deliberately checks only the active foe (no back-check) - preserves the existing hasRealAction contract. */
	public static boolean deadTurn(SimState root, AIConfig cfg) {
		double baseline = evalAfter(root, Action.PASS, Action.PASS, cfg);
		Pokemon self = root.ai.active();
		Pokemon foe = root.player.active();
		Ability foeAbility = foe.getAbility(root.field);
		for (Move m : self.getValidMoveset()) {
			if (choiceLockSkip(self, m, foe, foeAbility, root.field)) continue;
			double after = evalAfter(root, new Action(m), Action.PASS, cfg);
			if (after - baseline > DEAD_EPS) return false;
		}
		return true;
	}

	/** Mirrors the pre-overhaul skip rule (legacy AI removed in Phase 4): don't count clicking a status move as a real action for an unlocked Choice holder. */
	private static boolean choiceLockSkip(Pokemon self, Move m, Pokemon foe, Ability foeAbility, Field field) {
		return m.cat == 2 && self.item != null && self.getItem(field).isChoiceItem()
				&& !m.isMagicBounceEffected(self, foe, foeAbility, m.accuracy);
	}

	private static double evalAfter(SimState root, Action a, Action p, AIConfig cfg) {
		List<Branch> branches = BattleSimulator.simulateTurn(root, a, p, cfg);
		double v = 0;
		for (Branch br : branches) v += br.prob * Evaluator.eval(br.state, cfg.style, null, null);
		return v;
	}

	// ---- §7.14.2 ----

	/** candidateBench split: the capped top-N "answers", and the never-pruned sack candidates. */
	public static final class BenchPlan {
		final List<Integer> answers;
		public final List<Integer> sacks;

		BenchPlan(List<Integer> answers, List<Integer> sacks) {
			this.answers = answers;
			this.sacks = sacks;
		}

		/** answers first, then sacks not already in answers. */
		List<Integer> all() {
			Set<Integer> u = new LinkedHashSet<>(answers);
			u.addAll(sacks);
			return new ArrayList<>(u);
		}

		/** Sack candidates that are NOT also answers: the only rows the min-gain guard may drop. */
		public Set<Integer> sackOnly() {
			Set<Integer> s = new LinkedHashSet<>(sacks);
			s.removeAll(answers);
			return s;
		}
	}

	/**
	 * valueOf(m) = weight * hpFrac (§7.14.1). Candidates: alive bench mons worth strictly less than
	 * sackRatio * value(active), lowest two. Empty when {@code cfg.enableSacking} is false or the active is worth nothing.
	 */
	public static List<Integer> sackCandidates(SimState root, AIConfig cfg, MonWeights weights) {
		if (!cfg.enableSacking) return new ArrayList<>();
		Pokemon[] team = root.ai.bench();
		int activeIdx = indexOf(team, root.ai.active());
		double activeValue = valueOf(weights.ai, team, activeIdx);
		if (activeValue <= 0) return new ArrayList<>();

		List<Integer> c = new ArrayList<>();
		for (int i = 0; i < team.length; i++) {
			if (i == activeIdx || team[i] == null || team[i].isFainted()) continue;
			double v = valueOf(weights.ai, team, i);
			if (v < cfg.sackRatio * activeValue) c.add(i);
		}
		c.sort((i, j) -> Double.compare(valueOf(weights.ai, team, i), valueOf(weights.ai, team, j)));
		return c.size() > 2 ? new ArrayList<>(c.subList(0, 2)) : c;
	}

	public static BenchPlan benchPlan(SimState root, AIConfig cfg, MonWeights weights) {
		Pokemon[] team = root.ai.bench();
		Pokemon foeActive = root.player.active();
		int activeIdx = indexOf(team, root.ai.active());

		List<Integer> alive = new ArrayList<>();
		for (int i = 0; i < team.length; i++) {
			if (i != activeIdx && team[i] != null && !team[i].isFainted()) alive.add(i);
		}
		int n = Math.max(0, cfg.maxAISwitchRows - 2);
		alive.sort((i, j) -> Double.compare(cheapMatchup(team[j], foeActive, root.field), cheapMatchup(team[i], foeActive, root.field)));

		// any bench mon resistant/immune to the foe's likely move types is already ranked highly by
		// cheapMatchup and so is naturally included by the topN cut (§7.14.2's third clause).
		List<Integer> answers = new ArrayList<>();
		for (int idx : alive) {
			if (answers.size() >= n) break;
			answers.add(idx);
		}
		return new BenchPlan(answers, sackCandidates(root, cfg, weights)); // sacks are never pruned
	}

	/** answers ∪ sack candidates (§7.14.2). */
	static List<Integer> candidateBench(SimState root, AIConfig cfg, MonWeights weights) {
		return benchPlan(root, cfg, weights).all();
	}

	/**
	 * Keeps the player's top matchup-answers, plus (Phase 5, §7.14.2) their lowest-weighted-value bench mon as a sack
	 * column when the AI threatens the player's active and that mon is worth less than sackRatio * value(active): a human
	 * sacks too, and predicting it lets the AI punish it. No threat, no sack column.
	 */
	private static List<Integer> playerSwitchColumns(SimState root, AIConfig cfg, MonWeights weights) {
		Pokemon[] team = root.player.bench();
		Pokemon aiActive = root.ai.active();
		int activeIdx = indexOf(team, root.player.active());

		List<Integer> alive = new ArrayList<>();
		for (int i = 0; i < team.length; i++) {
			if (i != activeIdx && team[i] != null && !team[i].isFainted()) alive.add(i);
		}
		alive.sort((i, j) -> Double.compare(cheapMatchup(team[j], aiActive, root.field), cheapMatchup(team[i], aiActive, root.field)));

		List<Integer> result = new ArrayList<>();
		int cap = Math.max(1, cfg.maxPlayerSwitchCols);
		for (int idx : alive) {
			if (result.size() >= cap) break;
			result.add(idx);
		}
		if (!alive.isEmpty() && SackAnalysis.threatens(aiActive, root.player.active(), root.field)) {
			double activeVal = valueOf(weights.player, team, activeIdx);
			int lowest = alive.get(0);
			double lowestVal = valueOf(weights.player, team, lowest);
			for (int idx : alive) {
				double v = valueOf(weights.player, team, idx);
				if (v < lowestVal) {
					lowestVal = v;
					lowest = idx;
				}
			}
			if (lowestVal < cfg.sackRatio * activeVal && !result.contains(lowest)) result.add(lowest);
		}
		return result;
	}

	/** weight * hpFrac (§7.14.1); 0 for a missing slot. */
	public static double valueOf(double[] w, Pokemon[] team, int i) {
		if (i < 0) return 0;
		Pokemon p = team[i];
		double hpFrac = p.currentHP * 1.0 / p.getStat(0);
		double weight = (w != null && i < w.length) ? w[i] : 1.0;
		return weight * hpFrac;
	}

	/** Cheap type-matchup proxy for ranking candidate switch-ins (not evaluateSwitchInScore - that's the recursive path §7.14.3 retires). */
	private static double cheapMatchup(Pokemon mine, Pokemon foe, Field field) {
		if (mine == null || foe == null) return Double.NEGATIVE_INFINITY;
		double eff = 1.0 - Trainer.getEffective(mine, foe, foe.type1, null, false);
		if (foe.type2 != null) eff += 1.0 - Trainer.getEffective(mine, foe, foe.type2, null, false);
		return eff;
	}

	private static int indexOf(Pokemon[] team, Pokemon p) {
		for (int i = 0; i < team.length; i++) if (team[i] == p) return i;
		return -1;
	}
}