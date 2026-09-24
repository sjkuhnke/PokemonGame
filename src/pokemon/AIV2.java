package pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import overworld.GamePanel;
import ui.SimBattleUI;
import util.Pair;
import util.Print;

/**
 * §5/§12 Phase 3. The overhauled engine, active behind {@link TrainerAI.Config#defaultAI} (or a
 * per-trainer {@code Trainer.ai}) for NORMAL and HARD. EXTREME gets HARD's in-battle behavior
 * this phase; lead selection (§7.15) is Phase 7, so EXTREME's {@code decide()} runs identically
 * to HARD's until then.
 * <p>
 * This phase's simplifications relative to the full spec (each is a deliberately deferred later
 * phase, not an oversight - see PHASE3_CHANGES.md):
 * <ul>
 * <li>{@code predict()} (§7.11) is un-implemented: no player model yet, so {@code y_hat} is just
 * the raw equilibrium {@code solveZeroSum(M).y}.</li>
 * <li>{@code finalStrategy()} (§7.11) is likewise just the raw equilibrium {@code x_eq} - no
 * exploit blending, since that needs the player model above.</li>
 * <li>Tier 0/1 pruning, switch-in caching and the {@code chooseReplacement} unification
 * (§7.14.3) are Phase 5/8 per BattleSimulator's existing {@code cheapReplacementSlot} note.</li>
 * </ul>
 * <p>
 * Debug output: set {@link #AI_DEBUG} for a one-line-per-action probability dump (via {@link
 * Print#debug}, same channel as everything else, so {@code SelfPlay}'s existing {@code
 * Print.setDebugSuppressed} silences it automatically outside {@code cfg.aiTrace}). Set {@link
 * #AI_DEBUG_MATRIX} too for the full payoff matrix - verbose, off by default even when {@link
 * #AI_DEBUG} is on.
 */
public final class AIV2 implements TrainerAI {
	public static final AIV2 INSTANCE = new AIV2();

	/** One line per action + its sampling probability, and the chosen action. */
	public static boolean AI_DEBUG = false;
	/** Full A x P payoff matrix. Verbose - leave off unless you're chasing a specific decision. */
	public static boolean AI_DEBUG_MATRIX = true;
	/** Per-action resulting branch state (HP/status/stages/fainted) vs one fixed player action. See logBranchDetail. */
	public static boolean AI_DEBUG_BRANCHES = true;

	private AIV2() {}

	@Override
	public String getName() {
		return "AI_V2 (Phase 3)";
	}

	@Override
	public MoveDecision decide(Pokemon self, Pokemon foe, boolean first, int difficulty) {
		AIConfig cfg = difficulty == Player.NORMAL ? AIConfig.normal() : AIConfig.hard();

		// ---- §7.13 forced pre-checks (kept from existing code) ----
		if (self.script && Pokemon.field.turns == 0) {
			if (Pokemon.gp.currentMap == 160) return new MoveDecision(self.getStatusMove());
			return new MoveDecision(self.moveset[0].move);
		}

		ArrayList<Move> validMoves = self.getValidMoveset();
		boolean canSwitch = self.trainer.canSwitch(foe);
		if (validMoves.isEmpty()) {
			// Struggle-only: swaps at every difficulty if possible (T29). Target comes from the
			// matrix restricted to SWITCH rows (sack candidates included); if trapped, Struggle.
			if (canSwitch) {
				int slot = pickStruggleSwitch(self, foe, cfg);
				return new MoveDecision(Move.GROWL, Status.SWAP, slot + 1);
			}
			return new MoveDecision(Move.STRUGGLE);
		}
		// Status.RECHARGE/CHARGING/SEMI_INV/LOCKED/ENCORED mid-sequence: getValidMoveset() is
		// already expected to have narrowed validMoves down to the forced move (existing
		// behavior, untouched this phase) - the matrix still lets SWITCH compete when legal,
		// since genAIActions/switchRowsAllowed don't special-case those statuses.

		// ---- §5 decide() pipeline ----
		SimState root = SimState.snapshot(self, foe);
		MonWeights weights = MonWeights.compute(root);

		List<Action> A = ActionGen.genAIActions(root, cfg, weights);
		List<Action> P = ActionGen.genPlayerActions(root, cfg);
		if (P.isEmpty()) P = Collections.singletonList(Action.PASS);
		if (A.isEmpty()) return new MoveDecision(Move.STRUGGLE); // defensive; validMoves was non-empty above

		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, weights);
		logMatrix(self, A, P, M);
		logEvalBreakdown(self, root, cfg, weights);
		logBranchDetail(self, root, A, P, cfg);

		// predict()/finalStrategy() (§7.11): no player model this phase, see class doc.
		Solver.Result eq = Solver.solveZeroSum(M);
		double[] x = Shaper.shape(eq.x, cfg);

		int chosen = Shaper.sample(x);
		Action chosenAct = A.get(chosen);
		logDecision(self, A, x, chosenAct, x[chosen]);
		report(self, A, x, chosenAct, x[chosen]);

		return toMoveDecision(chosenAct);
	}

	/** Action -> MoveDecision, converting the 0-based Action.slot into MoveDecision's 1-based/negative switch encoding (same conventions as Pokemon.resolveDecision). */
	private MoveDecision toMoveDecision(Action act) {
		switch (act.kind) {
		case MOVE:
			return new MoveDecision(act.move);
		case SWITCH:
			return new MoveDecision(Move.GROWL, Status.SWAP, act.slot + 1);
		case MOVE_THEN_SWITCH:
			return new MoveDecision(act.move, Status.TEMP_SWITCHING, -(act.slot + 1));
		default:
			throw new IllegalStateException("Unknown ActionKind: " + act.kind);
		}
	}

	/** Struggle-only forced swap: pick the best SWITCH-only cell by the matrix, restricted to candidateBench. */
	private int pickStruggleSwitch(Pokemon self, Pokemon foe, AIConfig cfg) {
		SimState root = SimState.snapshot(self, foe);
		MonWeights weights = MonWeights.compute(root);
		List<Integer> targets = ActionGen.candidateBench(root, cfg, weights);

		if (targets.isEmpty()) {
			Pokemon[] team = self.trainer.team;
			for (int i = 0; i < team.length; i++) {
				if (team[i] != null && !team[i].isFainted() && team[i] != self) return i;
			}
			return 0;
		}

		List<Action> A = new ArrayList<>();
		for (int slot : targets) A.add(new Action(slot));
		List<Action> P = ActionGen.genPlayerActions(root, cfg);
		if (P.isEmpty()) P = Collections.singletonList(Action.PASS);

		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, weights);
		Solver.Result eq = Solver.solveZeroSum(M);
		int best = 0;
		for (int i = 1; i < eq.x.length; i++) if (eq.x[i] > eq.x[best]) best = i;
		return A.get(best).slot;
	}

	/**
	 * §7.12 reporting: derives simBattleUI.p1Moves/p2Moves (moves only, renormalized) and
	 * p1Switch/p2Switch reasons from x, mirroring the pre-overhaul "fill p1 slot if empty,
	 * else p2" pattern so the existing sim-UI probability displays keep working unmodified.
	 */
	private void report(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb) {
		if (Pokemon.gp == null || Pokemon.gp.gameState != GamePanel.SIM_BATTLE_STATE) return;
		SimBattleUI ui = Pokemon.gp.simBattleUI;
		boolean slotOne = ui.p1Moves == null && ui.p1Switch == null;

		double moveMass = 0;
		for (int i = 0; i < A.size(); i++) if (A.get(i).kind != ActionKind.SWITCH) moveMass += x[i];

		if (chosenAct.kind == ActionKind.SWITCH || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			String rsn = String.format("[Matrix: switch to %s %.0f%%]\n", self.trainer.team[chosenAct.slot], chosenProb * 100);
			Pair<Pokemon, String> p = new Pair<>(self, rsn);
			if (slotOne) ui.p1Switch = p; else ui.p2Switch = p;
		}
		if (chosenAct.kind == ActionKind.MOVE || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			double pct = moveMass > 0 ? (chosenProb / moveMass) * 100.0 : chosenProb * 100.0;
			Pair<Pokemon, Double> p = new Pair<>(self, pct);
			if (slotOne) ui.p1Moves = p; else ui.p2Moves = p;
		}
	}

	/** One line per action and its sampling probability, plus the pick. Guarded by AI_DEBUG so the string building is skipped entirely when off. */
	private void logDecision(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb) {
		if (!AI_DEBUG) return;
		StringBuilder sb = new StringBuilder();
		sb.append("[AIV2] ").append(self).append(" (turn ").append(Pokemon.field.turns).append(")\n");
		Integer[] order = new Integer[A.size()];
		for (int i = 0; i < order.length; i++) order[i] = i;
		java.util.Arrays.sort(order, (i, j) -> Double.compare(x[j], x[i])); // highest probability first
		for (int idx : order) {
			sb.append(String.format(Locale.ROOT, "    %-28s %5.1f%%\n", A.get(idx).label(), x[idx] * 100));
		}
		sb.append(String.format(Locale.ROOT, "  -> %s (%.1f%%)\n", chosenAct.label(), chosenProb * 100));
		Print.debug(sb.toString());
	}

	/** Full A x P payoff matrix. Verbose - off unless AI_DEBUG_MATRIX is also set. */
	private void logMatrix(Pokemon self, List<Action> A, List<Action> P, double[][] M) {
		if (!AI_DEBUG || !AI_DEBUG_MATRIX) return;
		StringBuilder sb = new StringBuilder();
		sb.append("[AIV2] payoff matrix for ").append(self).append(":\n");
		sb.append(String.format(Locale.ROOT, "%-24s", ""));
		for (Action p : P) sb.append(String.format(Locale.ROOT, "%12s", trunc(p.label(), 11)));
		sb.append('\n');
		for (int i = 0; i < A.size(); i++) {
			sb.append(String.format(Locale.ROOT, "%-24s", trunc(A.get(i).label(), 23)));
			for (int j = 0; j < P.size(); j++) sb.append(String.format(Locale.ROOT, "%12.1f", M[i][j]));
			sb.append('\n');
		}
		Print.debug(sb.toString());
	}

	/**
	 * Diagnostic: for every AI action, dumps the resulting branch(es) against ONE fixed player
	 * action (P.get(0)) - HP, status, stat stages, fainted, for both sides. Off by default
	 * (AI_DEBUG_BRANCHES); this exists specifically to check whether two differently-labeled
	 * actions that get identical matrix payoffs are actually producing identical post-turn
	 * states (a real bug) or just coincidentally identical eval() numbers from different states.
	 */
	private void logBranchDetail(Pokemon self, SimState root, List<Action> A, List<Action> P, AIConfig cfg) {
		if (!AI_DEBUG || !AI_DEBUG_BRANCHES || P.isEmpty()) return;
		Action p0 = P.get(0);
		StringBuilder sb = new StringBuilder("[AIV2] branch detail for ").append(self)
				.append(" vs player action '").append(p0.label()).append("':\n");
		for (Action a : A) {
			List<Branch> branches = BattleSimulator.simulateTurn(root, a, p0, cfg);
			sb.append(String.format(Locale.ROOT, "  %-24s -> %d branch(es)\n", a.label(), branches.size()));
			for (Branch br : branches) {
				Pokemon aiMon = br.state.ai.active();
				Pokemon foeMon = br.state.player.active();
				sb.append(String.format(Locale.ROOT,
						"      p=%.2f  self hp=%d/%d status=%s stages=%s fainted=%b  |  foe hp=%d/%d status=%s fainted=%b\n",
						br.prob,
						aiMon == null ? -1 : aiMon.currentHP, aiMon == null ? -1 : aiMon.getStat(0),
						aiMon == null ? "?" : aiMon.status, aiMon == null ? "?" : java.util.Arrays.toString(aiMon.statStages),
						aiMon != null && aiMon.fainted,
						foeMon == null ? -1 : foeMon.currentHP, foeMon == null ? -1 : foeMon.getStat(0),
						foeMon == null ? "?" : foeMon.status, foeMon != null && foeMon.fainted));
			}
		}
		Print.debug(sb.toString());
	}

	/** Breaks eval() down into its terms at the (unmodified) root state, so a suspiciously large/uniform matrix can be traced to a specific term instead of guessed at. */
	private void logEvalBreakdown(Pokemon self, SimState root, AIConfig cfg, MonWeights weights) {
		if (!AI_DEBUG) return;
		double mat = Evaluator.material(root.ai, weights.ai) - Evaluator.material(root.player, weights.player);
		double matchup = Evaluator.activeMatchup(root);
		double hazard = Evaluator.hazardPain(root.player, root.field) - Evaluator.hazardPain(root.ai, root.field);
		double status = Evaluator.benchStatusValue(root.player, root.field) - Evaluator.benchStatusValue(root.ai, root.field);
		double field = Evaluator.fieldValue(root);
		double tempo = Evaluator.tempo(root);
		double forced = Evaluator.forcedTurnPenalty(root);
		double total = Evaluator.eval(root, cfg.style, weights.ai, weights.player);
		Print.debug(String.format(Locale.ROOT,
				"[AIV2] eval(root)=%.1f  material=%.1f matchup=%.1f hazard=%.1f status=%.1f field=%.1f tempo=%.1f forced=%.1f%n",
				total, mat, matchup, hazard, status, field, tempo, forced));
	}

	private static String trunc(String s, int n) {
		return s.length() <= n ? s : s.substring(0, Math.max(0, n - 1)) + "\u2026";
	}
}