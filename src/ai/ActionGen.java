package ai;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pokemon.*;

/**
 * Generates the two action lists a decision's payoff matrix is built from: the AI's rows ({@link #genAIActions}) and the
 * player's columns ({@link #genPlayerActions}).
 * <p>
 * <b>Moves.</b> Rows that provably do nothing this turn are dropped first ({@link #usefulRows}), so they neither cost
 * matrix cells nor split probability mass.
 * <p>
 * <b>Switches.</b> The AI's switch rows come from {@link #benchPlan}: the top few type-matchup "answers" plus the
 * "sack" candidates (low-value mons worth throwing in front of a hit, never pruned). Whether the AI may switch
 * voluntarily at all depends on difficulty ({@link #switchRowsAllowed}; NORMAL only on a {@link #deadTurn}, a Perish
 * count of 1, or Struggle-only). {@code benchPlan} keeps the answers/sacks split because the sack guard needs to know
 * which rows are sack-only. Sack candidates honor {@code cfg.enableSacking}.
 * <p>
 * <b>Player columns.</b> The player gets their moves, a few switch targets, and pivot moves paired with those targets.
 * Their sack column is only included while the AI actually threatens their active ({@link #playerSwitchColumns}).
 */
public final class ActionGen {
	/** {@link #deadTurn}'s "did anything change" threshold, in eval points. */
	private static final double DEAD_EPS = 1.0;

	private ActionGen() {}

	// ---- action lists ----

	/** The AI's rows: useful moves, pivot moves paired with each switch target, and (when allowed) plain switches. */
	public static List<Action> genAIActions(SimState root, AIConfig cfg, MonWeights weights) {
		List<Action> A = new ArrayList<>();
		Pokemon self = root.ai.active();
		Pokemon foe = root.player.active();
		List<Move> validMoves = usefulRows(self, foe, root.field, self.getValidMoveset());
		boolean canSwitch = self.trainer.canSwitch(foe);

		List<Integer> switchTargets = canSwitch ? benchPlan(root, cfg, weights).all() : java.util.Collections.emptyList();
		Ability foeAbility = canSwitch ? foe.getAbility(root.field) : null;

		for (Move m : validMoves) {
			// A pivot move whose switch-in is already covered by explicit MOVE_THEN_SWITCH rows below is NOT also added
			// plain: a plain pivot's post-move replacement is picked by ReplacementChooser (the same function the real
			// battle uses), not by a switch the AI planned in the matrix, so the explicit rows are the only way the AI
			// chooses the target.
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

	/** The player's columns, with weights computed here. The player's switches are considered at every difficulty. */
	public static List<Action> genPlayerActions(SimState root, AIConfig cfg) {
		return genPlayerActions(root, cfg, MonWeights.compute(root));
	}

	/** The player's columns, given the decision's weights (so the player's sack column does not recompute them). */
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

	// ---- row filter ----

	/**
	 * Drops rows that provably do nothing this turn: Fake Out / First Impression / Dream Eater and friends when
	 * {@code calcRange} says unusable (damaging moves only: for those {@code usable=false} always means "cannot be used
	 * now"), Sleep Talk / Snore while awake, and hazard moves that {@code isHazardUseful} rejects. Never returns an empty
	 * list: if everything would be dropped the unfiltered list is returned (the simulation then plays it out as a failed
	 * move).
	 */
	public static List<Move> usefulRows(Pokemon self, Pokemon foe, Field field, List<Move> valid) {
		List<Move> out = new ArrayList<>(valid.size());
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			for (Move m : valid) {
				if ((m == Move.SLEEP_TALK || m == Move.SNORE) && self.status != Status.ASLEEP) continue;
				if (m.isHazard() && !self.isHazardUseful(m, foe, field)) continue;
				if (m.cat != 2 && !SimCache.calcRange(self, foe, m, true, field).usable) continue;
				out.add(m);
			}
		}
		return out.isEmpty() ? valid : out;
	}

	// ---- voluntary switching (the NORMAL gate) ----

	/**
	 * Whether the AI's plain switch rows are in the matrix this turn. HARD and EXTREME always allow them. NORMAL allows them
	 * only when its Perish count is 1, it has only Struggle, or the turn is dead ({@link #deadTurn}).
	 */
	public static boolean switchRowsAllowed(SimState root, AIConfig cfg, MonWeights weights) {
		if (cfg.allowVoluntarySwitch) return true; // HARD / EXTREME
		Pokemon self = root.ai.active();
		if (self.perishCount == 1) return true;
		if (self.getValidMoveset().isEmpty()) return true; // Struggle-only
		return deadTurn(root, cfg);
	}

	/**
	 * A dead turn: none of the AI's moves improves the position against the active foe (each move is simulated against a
	 * passing foe and compared with the AI passing). Deliberately checks only the active foe, with no look at a foe that
	 * might switch in.
	 */
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

	/** Clicking a status move does not count as a real action for a Choice holder that is not locked yet (it would lock it into the status move). */
	private static boolean choiceLockSkip(Pokemon self, Move m, Pokemon foe, Ability foeAbility, Field field) {
		return m.cat == 2 && self.getItem(field) != null && self.getItem(field).isChoiceItem()
				&& !m.isMagicBounceEffected(self, foe, foeAbility, m.accuracy);
	}

	private static double evalAfter(SimState root, Action a, Action p, AIConfig cfg) {
		List<Branch> branches = BattleSimulator.simulateTurn(root, a, p, cfg);
		double v = 0;
		for (Branch br : branches) v += br.prob * Evaluator.eval(br.state, cfg.style, null, null);
		return v;
	}

	// ---- the AI's bench: answers and sacks ----

	/** The AI's switch targets, split into the capped top-N "answers" and the never-pruned sack candidates. */
	public static final class BenchPlan {
		final List<Integer> answers;
		public final List<Integer> sacks;

		BenchPlan(List<Integer> answers, List<Integer> sacks) {
			this.answers = answers;
			this.sacks = sacks;
		}

		/** Answers first, then sacks not already in answers. */
		List<Integer> all() {
			Set<Integer> u = new LinkedHashSet<>(answers);
			u.addAll(sacks);
			return new ArrayList<>(u);
		}

		/** Sack candidates that are NOT also answers: the only rows the sack guard may drop. */
		public Set<Integer> sackOnly() {
			Set<Integer> s = new LinkedHashSet<>(sacks);
			s.removeAll(answers);
			return s;
		}
	}

	/**
	 * Sack candidates: alive bench mons whose {@link #valueOf value} is strictly less than {@code sackRatio * value(active)},
	 * lowest two. Empty when {@code cfg.enableSacking} is false or the active is worth nothing.
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

	/**
	 * The AI's switch targets: the best {@code maxAISwitchRows - 2} alive bench mons by type matchup against the foe's
	 * active (a mon resistant or immune to the foe's types ranks high, so it is naturally included), plus the sack candidates.
	 */
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

		List<Integer> answers = new ArrayList<>();
		for (int idx : alive) {
			if (answers.size() >= n) break;
			answers.add(idx);
		}
		return new BenchPlan(answers, sackCandidates(root, cfg, weights)); // sacks are never pruned
	}

	/** Answers plus sack candidates. */
	static List<Integer> candidateBench(SimState root, AIConfig cfg, MonWeights weights) {
		return benchPlan(root, cfg, weights).all();
	}

	/**
	 * Keeps the player's top matchup answers, plus their lowest-weighted-value bench mon as a sack column when the AI
	 * threatens the player's active and that mon is worth less than {@code sackRatio * value(active)}: a human sacks too,
	 * and predicting it lets the AI punish it. No threat, no sack column.
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

		// Status.MAGIC_REFLECT (the volatile on the mon; NOT the Reflect screen, Field.Effect.REFLECT) is lost when its
		// holder leaves, so a player does not throw it away without a reason. The AI attacking into it just hurts the AI,
		// and an AI break (Brick Break etc.) shows up as a real threat below. The one plausible reason to switch is
		// predicting a status move that would burn the Reflect: the player then brings in their best answer for free. So
		// with no threat: no switch columns if the AI has no usable status move, otherwise only the single best answer.
		Pokemon playerActive = root.player.active();
		if (playerActive.hasStatus(Status.MAGIC_REFLECT) && !SackAnalysis.threatens(aiActive, playerActive, root.field)) {
			boolean aiHasStatusMove = false;
			for (Move m : usefulRows(aiActive, playerActive, root.field, aiActive.getValidMoveset())) {
				if (m.cat == 2) {
					aiHasStatusMove = true;
					break;
				}
			}
			List<Integer> only = new ArrayList<>();
			if (aiHasStatusMove && !alive.isEmpty()) only.add(alive.get(0));
			return only;
		}

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

	/** A mon's worth to its team right now: {@code weight * hpFrac} (the weight is HP-independent, so this tracks HP). 0 for a missing slot. */
	public static double valueOf(double[] w, Pokemon[] team, int i) {
		if (i < 0) return 0;
		Pokemon p = team[i];
		double hpFrac = p.currentHP * 1.0 / p.getStat(0);
		double weight = (w != null && i < w.length) ? w[i] : 1.0;
		return weight * hpFrac;
	}

	/** Cheap type-matchup proxy for ranking candidate switch-ins. It only decides which bench mons get a row; the matrix prices the switch itself. */
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