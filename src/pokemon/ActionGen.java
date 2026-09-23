package pokemon;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * §7.4 genAIActions/genPlayerActions, §7.13.1 switchRowsAllowed/deadTurn, §7.14.2
 * candidateBench/sackCandidates (v1 - see class-level notes below and PHASE3_CHANGES.md).
 */
final class ActionGen {
	/** deadTurn's "did anything change" threshold (§7.13.1). */
	private static final double DEAD_EPS = 1.0;

	private ActionGen() {}

	// ---- §7.4 ----

	static List<Action> genAIActions(SimState root, AIConfig cfg, MonWeights weights) {
		List<Action> A = new ArrayList<>();
		Pokemon self = root.ai.active();
		Pokemon foe = root.player.active();
		ArrayList<Move> validMoves = self.getValidMoveset();
		for (Move m : validMoves) A.add(new Action(m));

		if (self.trainer.canSwitch(foe)) {
			List<Integer> switchTargets = candidateBench(root, cfg, weights);
			boolean voluntaryOK = switchRowsAllowed(root, cfg, weights);
			if (voluntaryOK) {
				for (int slot : switchTargets) A.add(new Action(slot));
			}
			Ability foeAbility = foe.getAbility(root.field);
			for (Move m : validMoves) {
				if (m.isPivotMove() && self.isUsefulPivot(foe, foeAbility, m)) {
					for (int slot : switchTargets) A.add(new Action(m, slot));
				}
			}
		}
		return A;
	}

	/** considerPlayerSwitches is true at every difficulty per §7.4; infoMode FULL only this phase (REVEALED_ONLY is Phase 6). */
	static List<Action> genPlayerActions(SimState root, AIConfig cfg) {
		List<Action> P = new ArrayList<>();
		Pokemon player = root.player.active();
		Pokemon aiMon = root.ai.active();
		ArrayList<Move> validMoves = player.getValidMoveset();
		for (Move m : validMoves) P.add(new Action(m));

		if (player.trainer.canSwitch(aiMon)) {
			List<Integer> switchCols = playerSwitchColumns(root, cfg);
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

	// ---- §7.13.1 ----

	static boolean switchRowsAllowed(SimState root, AIConfig cfg, MonWeights weights) {
		if (cfg.allowVoluntarySwitch) return true; // HARD / EXTREME
		Pokemon self = root.ai.active();
		if (self.perishCount == 1) return true;
		if (self.getValidMoveset().isEmpty()) return true; // Struggle-only
		return deadTurn(root, cfg);
	}

	/** Deliberately checks only the active foe (no back-check) - preserves the existing hasRealAction contract. */
	static boolean deadTurn(SimState root, AIConfig cfg) {
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

	/** Mirrors the existing skip rule at Pokemon.legacyBestMove ~line 649: don't count clicking a status move as a real action for an unlocked Choice holder. */
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

	// ---- §7.14.2 (v1: no SACK_MIN_GAIN guard yet - see class doc) ----

	static List<Integer> sackCandidates(SimState root, AIConfig cfg, MonWeights weights) {
		Pokemon[] team = root.ai.bench();
		int activeIdx = indexOf(team, root.ai.active());
		double activeValue = valueOf(weights.ai, team, activeIdx);

		List<Integer> c = new ArrayList<>();
		for (int i = 0; i < team.length; i++) {
			if (i == activeIdx || team[i] == null || team[i].isFainted()) continue;
			double v = valueOf(weights.ai, team, i);
			if (v < cfg.sackRatio * activeValue) c.add(i);
		}
		c.sort((i, j) -> Double.compare(valueOf(weights.ai, team, i), valueOf(weights.ai, team, j)));
		return c.size() > 2 ? new ArrayList<>(c.subList(0, 2)) : c;
	}

	static List<Integer> candidateBench(SimState root, AIConfig cfg, MonWeights weights) {
		Pokemon[] team = root.ai.bench();
		Pokemon foeActive = root.player.active();
		int activeIdx = indexOf(team, root.ai.active());

		List<Integer> alive = new ArrayList<>();
		for (int i = 0; i < team.length; i++) {
			if (i != activeIdx && team[i] != null && !team[i].isFainted()) alive.add(i);
		}
		int n = Math.max(0, cfg.maxAISwitchRows - 2);
		alive.sort((i, j) -> Double.compare(cheapMatchup(team[j], foeActive, root.field), cheapMatchup(team[i], foeActive, root.field)));

		Set<Integer> result = new LinkedHashSet<>();
		for (int idx : alive) {
			if (result.size() >= n) break;
			result.add(idx);
		}
		// any bench mon resistant/immune to the foe's likely move types is already ranked highly by
		// cheapMatchup and so is naturally included by the topN cut above (§7.14.2's third clause).
		result.addAll(sackCandidates(root, cfg, weights)); // never pruned
		return new ArrayList<>(result);
	}

	/** Keeps the player's top matchup-answers plus their lowest-weighted-value bench mon (a human sacks too, §7.14.2). */
	private static List<Integer> playerSwitchColumns(SimState root, AIConfig cfg) {
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
		if (!alive.isEmpty()) {
			MonWeights weights = MonWeights.compute(root);
			int lowest = alive.get(0);
			double lowestVal = valueOf(weights.player, team, lowest);
			for (int idx : alive) {
				double v = valueOf(weights.player, team, idx);
				if (v < lowestVal) {
					lowestVal = v;
					lowest = idx;
				}
			}
			if (!result.contains(lowest)) result.add(lowest);
		}
		return result;
	}

	private static double valueOf(double[] w, Pokemon[] team, int i) {
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