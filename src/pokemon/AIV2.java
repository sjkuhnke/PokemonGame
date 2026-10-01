package pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
 * <li>Tier 0/1 pruning and switch-in caching are Phase 8. (The {@code chooseReplacement} unification of §7.14.3 landed
 * in Phase 5: {@link ReplacementChooser}, shared with the real battle.)</li>
 * </ul>
 * <p>
 * Phase 5 (sacking, §7.14): {@link #plan} runs the whole matrix pipeline including the SACK_MIN_GAIN guard, and
 * {@link #decide} labels a chosen switch row as a sack (P(target faints) >= 0.5) with a replacement plan. {@code y_hat}
 * is still the raw equilibrium y until Phase 6's player model, so the label reflects the equilibrium, not a prediction.
 * <p>
 * Debug output: set {@link #AI_DEBUG} for a one-line-per-action probability dump (via {@link
 * Print#debug}, same channel as everything else, so {@code SelfPlay}'s existing {@code
 * Print.setDebugSuppressed} silences it automatically outside {@code cfg.aiTrace}). Set {@link
 * #AI_DEBUG_MATRIX} too for the full payoff matrix - verbose, off by default even when {@link
 * #AI_DEBUG} is on.
 */
public final class AIV2 implements TrainerAI {
	public static final AIV2 INSTANCE = new AIV2(true);
	/** Phase 5 self-play baseline: identical to INSTANCE but the AI never generates sack candidates (cfg.enableSacking=false). */
	public static final AIV2 NO_SACK = new AIV2(false);

	/** One line per action + its sampling probability, and the chosen action. */
	public static boolean AI_DEBUG = true;
	/** Full A x P payoff matrix. Verbose - leave off unless you're chasing a specific decision. */
	public static boolean AI_DEBUG_MATRIX = true;
	/** Per-action resulting branch state (HP/status/stages/fainted) vs one fixed player action. See logBranchDetail. */
	public static boolean AI_DEBUG_BRANCHES = true;

	private final boolean sackingEnabled;

	private AIV2(boolean sackingEnabled) {
		this.sackingEnabled = sackingEnabled;
	}

	@Override
	public String getName() {
		return sackingEnabled ? "AI_V2 (Phase 5)" : "AI_V2 (Phase 5, no sack)";
	}

	/** Everything decide() computes before sampling; package-private so tests run the exact production pipeline. */
	public static final class Plan {
		public final List<Action> A, P;
		public final double[][] M;
		public final MonWeights weights;
		/** Sack-only slots (sack candidates that are not also answers); empty when the AI cannot switch or sacking is off. */
		public final Set<Integer> sackOnly;
		/** ALL sack candidates (§7.14.2's ActionGen.BenchPlan.sacks), including ones that are ALSO top-N answers. This
		 * is what decides the [Sack: ...] label (chosen row switches into ANY sack candidate) - sackOnly above is
		 * narrower (only what the min-gain guard actually judges) and stays separate for that reporting. */
		public final Set<Integer> sacks;
		/** Min-gain guard result, null when there were no sack-only rows to judge. A/M above are already filtered. */
		public final SackAnalysis.Filtered guard;
		public final Solver.Result eq;
		public final double[] x;

		Plan(List<Action> A, List<Action> P, double[][] M, MonWeights weights, Set<Integer> sackOnly, Set<Integer> sacks,
				SackAnalysis.Filtered guard, Solver.Result eq, double[] x) {
			this.A = A;
			this.P = P;
			this.M = M;
			this.weights = weights;
			this.sackOnly = sackOnly;
			this.sacks = sacks;
			this.guard = guard;
			this.eq = eq;
			this.x = x;
		}
	}

	/** Rows, columns, payoff matrix, SACK_MIN_GAIN guard, equilibrium and shaped strategy for one decision. No RNG is drawn. Null if there are no rows. */
	public static Plan plan(SimState root, AIConfig cfg) {
		MonWeights weights = MonWeights.compute(root);
		List<Action> A = ActionGen.genAIActions(root, cfg, weights);
		List<Action> P = ActionGen.genPlayerActions(root, cfg, weights);
		if (P.isEmpty()) P = Collections.singletonList(Action.PASS);
		if (A.isEmpty()) return null;

		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, weights);

		Set<Integer> sackOnly = Collections.emptySet();
		Set<Integer> sacks = Collections.emptySet();
		SackAnalysis.Filtered guard = null;
		if (cfg.enableSacking && root.ai.active().trainer.canSwitch(root.player.active())) {
			ActionGen.BenchPlan bp = ActionGen.benchPlan(root, cfg, weights);
			sackOnly = bp.sackOnly();
			sacks = new java.util.LinkedHashSet<>(bp.sacks);
			if (!sacks.isEmpty()) {
				boolean[] threat = SackAnalysis.threatColumns(root, P);
				if (!sackOnly.isEmpty()) {
					guard = SackAnalysis.applyMinGain(A, M, threat, sackOnly, cfg.sackMinGain);
					A = guard.A;
					M = guard.M;
				}
				// Second layer: no sack that only returns the same mon to the same position (see SackAnalysis.dropSameMonReturns).
				SackAnalysis.Filtered sm = SackAnalysis.dropSameMonReturns(root, A, M, P, threat, sacks, cfg, guard);
				if (sm != null) {
					guard = sm;
					A = sm.A;
					M = sm.M;
				}
			}
		}

		// predict()/finalStrategy() (§7.11): no player model yet, see class doc.
		Solver.Result eq = Solver.solveZeroSum(M);
		double[] x = Shaper.shape(eq.x, cfg);
		return new Plan(A, P, M, weights, sackOnly, sacks, guard, eq, x);
	}

	@Override
	public MoveDecision decide(Pokemon self, Pokemon foe, boolean first, int difficulty) {
		AIConfig cfg = difficulty == Player.NORMAL ? AIConfig.normal() : AIConfig.hard();
		cfg.enableSacking = sackingEnabled;

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
		Plan plan = plan(root, cfg);
		if (plan == null) return new MoveDecision(Move.STRUGGLE); // defensive; validMoves was non-empty above
		List<Action> A = plan.A;
		List<Action> P = plan.P;
		double[] x = plan.x;

		logMatrix(self, plan);
		logEvalBreakdown(self, root, cfg, plan.weights);
		logBranchDetail(self, root, A, P, cfg, plan.eq.y, plan.weights);
		logSack(self, root, plan, cfg);

		int chosen = Shaper.sample(x);
		Action chosenAct = A.get(chosen);
		SackAnalysis.Label label = chosenAct.kind == ActionKind.MOVE ? null : SackAnalysis.classify(root, chosenAct, P, plan.eq.y, cfg, plan.sacks);
		logDecision(self, A, x, chosenAct, x[chosen], plan.sacks);
		logSackChosen(self, chosenAct, x[chosen], label);
		report(self, A, x, chosenAct, x[chosen], label);

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
		List<Action> P = ActionGen.genPlayerActions(root, cfg, weights);
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
	private void report(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb, SackAnalysis.Label label) {
		if (Pokemon.gp == null || Pokemon.gp.gameState != GamePanel.SIM_BATTLE_STATE) return;
		SimBattleUI ui = Pokemon.gp.simBattleUI;
		boolean slotOne = ui.p1Moves == null && ui.p1Switch == null;

		double moveMass = 0;
		for (int i = 0; i < A.size(); i++) if (A.get(i).kind != ActionKind.SWITCH) moveMass += x[i];

		if (chosenAct.kind == ActionKind.SWITCH || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			String rsn = reason(self, chosenAct, chosenProb, label);
			Pair<Pokemon, String> p = new Pair<>(self, rsn);
			if (slotOne) ui.p1Switch = p; else ui.p2Switch = p;
		}
		if (chosenAct.kind == ActionKind.MOVE || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			double pct = moveMass > 0 ? (chosenProb / moveMass) * 100.0 : chosenProb * 100.0;
			Pair<Pokemon, Double> p = new Pair<>(self, pct);
			if (slotOne) ui.p1Moves = p; else ui.p2Moves = p;
		}
	}

	/** §7.14.4 UI/debug reason: the sack line when the chosen switch is labeled a sack, else the plain matrix line. */
	static String reason(Pokemon self, Action chosenAct, double chosenProb, SackAnalysis.Label label) {
		Pokemon target = self.trainer.team[chosenAct.slot];
		if (label != null && label.sack) {
			// Replacement plan is only meaningful when the target actually tends to faint (SackAnalysis.SACK_LABEL_P);
			// otherwise there is no "free replacement" to plan for, so the segment is left out rather than shown as "?".
			String replSegment = label.replacementSlot >= 0
					? String.format(Locale.ROOT, " | replacement plan: %s", self.trainer.team[label.replacementSlot])
					: "";
			return String.format(Locale.ROOT, "[Sack: %s for %s%s | P(target faints) = %.0f%%]\n",
					target, self, replSegment, label.pTargetFaints * 100);
		}
		return String.format(Locale.ROOT, "[Matrix: switch to %s %.0f%%]\n", target, chosenProb * 100);
	}

	/** §7.14.4 debug: sack-only candidates with weighted values, and each judged row's payoff gap vs Stay (mean over threat columns). Off unless AI_DEBUG. */
	/**
	 * Phase 5 (§7.14.4) sack-candidate summary, plus a per-column breakdown for each row the min-gain guard actually
	 * dropped: the matrix payoff and the best Stay payoff it was compared against (same figures "gain" is built from),
	 * and - re-simulated, not read off the matrix, since the matrix only holds the final weighted eval score - whether
	 * the target faints in that column and, when it survives, how much HP it keeps. This is what a dropped row's own
	 * numbers look like, since a dropped row's cells aren't in the post-filter plan.A/plan.M at all.
	 */
	private void logSack(Pokemon self, SimState root, Plan plan, AIConfig cfg) {
		if (!AI_DEBUG || plan.sacks.isEmpty()) return;
		Pokemon[] team = root.ai.bench();
		int activeIdx = -1;
		for (int i = 0; i < team.length; i++) if (team[i] == root.ai.active()) activeIdx = i;
		StringBuilder sb = new StringBuilder("[AIV2] sack candidates for ").append(self).append(String.format(Locale.ROOT,
				" (active value %.2f, sackRatio via cfg):%n", ActionGen.valueOf(plan.weights.ai, team, activeIdx)));
		for (int slot : plan.sacks) {
			sb.append(String.format(Locale.ROOT, "    slot %d %s value %.2f (%s)%n", slot, team[slot], ActionGen.valueOf(plan.weights.ai, team, slot),
					plan.sackOnly.contains(slot) ? "sack-only: min-gain guard judges its rows" : "also a top-N answer: guard does not judge it"));
		}
		if (plan.guard == null) {
			Print.debug(sb.toString());
			return;
		}
		for (Action a : plan.guard.dropped) {
			String why = plan.guard.dropReason.get(a);
			sb.append(why == null ? "    DROPPED by min-gain guard: " : "    DROPPED by same-mon-return rule: ").append(a.label());
			if (why != null) sb.append(" (").append(why).append(')');
			sb.append('\n');
		}
		for (int i = 0; i < plan.guard.gain.length; i++) {
			if (Double.isNaN(plan.guard.gain[i])) continue;
			Action orig = plan.guard.originalA.get(i);
			boolean dropped = plan.guard.dropped.contains(orig);
			sb.append(String.format(Locale.ROOT, "    %s %s gain vs best Stay (mean over threat columns, or over ALL columns when there is none): %.1f (minGain %.1f)%n",
					dropped ? "DROPPED" : "KEPT   ", orig.label(), plan.guard.gain[i], cfg.sackMinGain));
		}
		for (Action a : plan.guard.dropped) {
			int origRow = plan.guard.originalA.indexOf(a);
			if (origRow < 0) continue; // defensive; should always be found
			Pokemon target = team[a.slot];
			sb.append("    breakdown for ").append(a.label()).append(" (target ").append(target).append("):\n");
			SackAnalysis.ColumnOutcome[] outcomes = SackAnalysis.columnOutcomes(root, a, plan.P, cfg);
			for (int j = 0; j < plan.P.size(); j++) {
				double m = plan.guard.originalM[origRow][j];
				double stayJ = plan.guard.stay[j];
				SackAnalysis.ColumnOutcome co = outcomes[j];
				String hp = Double.isNaN(co.avgHpFracAlive) ? "-" : String.format(Locale.ROOT, "%.0f%%", co.avgHpFracAlive * 100);
				sb.append(String.format(Locale.ROOT, "        %-22s%s row=%7.1f  stay=%7.1f  diff=%7.1f  P(faints)=%3.0f%%  HP if alive=%s%n",
						trunc(plan.P.get(j).label(), 21), plan.guard.threat[j] ? "*" : " ", m, stayJ, m - stayJ, co.pFaint * 100, hp));
			}
		}
		Print.debug(sb.toString());
	}

	/** Row label with a "[sack]" tag when the row switches (or pivot-switches) into a sack candidate. */
	private static String rowLabel(Action a, Set<Integer> sacks) {
		boolean sackRow = (a.kind == ActionKind.SWITCH || a.kind == ActionKind.MOVE_THEN_SWITCH) && sacks != null && sacks.contains(a.slot);
		return sackRow ? a.label() + " [sack]" : a.label();
	}

	/** The chosen row when it is labeled a sack: the same text the sim UI gets, plus the row's sampling probability. Debug only. */
	private void logSackChosen(Pokemon self, Action chosenAct, double chosenProb, SackAnalysis.Label label) {
		if (!AI_DEBUG || label == null || !label.sack) return;
		Print.debug(String.format(Locale.ROOT, "[AIV2] SACK CHOSEN (%s, %.1f%%): %s", chosenAct.label(), chosenProb * 100, reason(self, chosenAct, chosenProb, label)));
	}

	/** One line per action and its sampling probability, plus the pick. Guarded by AI_DEBUG so the string building is skipped entirely when off. */
	private void logDecision(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb, Set<Integer> sacks) {
		if (!AI_DEBUG) return;
		StringBuilder sb = new StringBuilder();
		sb.append("[AIV2] ").append(self).append(" (turn ").append(Pokemon.field.turns).append(")\n");
		Integer[] order = new Integer[A.size()];
		for (int i = 0; i < order.length; i++) order[i] = i;
		java.util.Arrays.sort(order, (i, j) -> Double.compare(x[j], x[i])); // highest probability first
		for (int idx : order) {
			sb.append(String.format(Locale.ROOT, "    %-34s %5.1f%%\n", rowLabel(A.get(idx), sacks), x[idx] * 100));
		}
		sb.append(String.format(Locale.ROOT, "  -> %s (%.1f%%)\n", rowLabel(chosenAct, sacks), chosenProb * 100));
		Print.debug(sb.toString());
	}

	/** Full A x P payoff matrix. Verbose - off unless AI_DEBUG_MATRIX is also set. */
	private void logMatrix(Pokemon self, Plan plan) {
		if (!AI_DEBUG || !AI_DEBUG_MATRIX) return;
		List<Action> A = plan.A, P = plan.P;
		double[][] M = plan.M;
		StringBuilder sb = new StringBuilder();
		sb.append("[AIV2] payoff matrix for ").append(self).append(":\n");
		sb.append(String.format(Locale.ROOT, "%-30s", ""));
		for (Action p : P) sb.append(String.format(Locale.ROOT, "%12s", trunc(p.label(), 11)));
		sb.append('\n');
		for (int i = 0; i < A.size(); i++) {
			sb.append(String.format(Locale.ROOT, "%-30s", trunc(rowLabel(A.get(i), plan.sacks), 29)));
			for (int j = 0; j < P.size(); j++) sb.append(String.format(Locale.ROOT, "%12.1f", M[i][j]));
			sb.append('\n');
		}
		// Rows the min-gain guard removed before solving: shown with their ORIGINAL payoffs so they are not silently absent.
		if (plan.guard != null && !plan.guard.dropped.isEmpty()) {
			sb.append("  -- dropped by min-gain guard (not in the solve or in the probabilities below) --\n");
			for (Action a : plan.guard.dropped) {
				int r = plan.guard.originalA.indexOf(a);
				if (r < 0) continue;
				sb.append(String.format(Locale.ROOT, "%-30s", trunc("x " + rowLabel(a, plan.sacks), 29)));
				for (int j = 0; j < P.size(); j++) sb.append(String.format(Locale.ROOT, "%12.1f", plan.guard.originalM[r][j]));
				sb.append('\n');
			}
		}
		Print.debug(sb.toString());
	}

	/**
	 * Diagnostic (AI_DEBUG_BRANCHES): for every AI action, the resulting branch(es) against player columns that explain the decision:
	 * the column the equilibrium expects most, plus (when different) the highest-expected column in which some AI row takes the foe
	 * active's life, so a KO line can be compared with a chip line term by term. Each branch carries its eval() split into terms;
	 * START is the real state before the turn, since every other line is a projection AFTER it.
	 */
	private void logBranchDetail(Pokemon self, SimState root, List<Action> A, List<Action> P, AIConfig cfg, double[] y, MonWeights w) {
		if (!AI_DEBUG || !AI_DEBUG_BRANCHES || P.isEmpty()) return;
		int col = 0;
		if (y != null) for (int j = 1; j < P.size() && j < y.length; j++) if (y[j] > y[col]) col = j;
		List<Integer> cols = new ArrayList<>();
		cols.add(col);
		int koCol = firstKoColumn(root, A, P, cfg, y);
		if (koCol >= 0 && koCol != col) cols.add(koCol);

		Pokemon a0 = root.ai.active(), f0 = root.player.active();
		StringBuilder sb = new StringBuilder("[AIV2] branch detail for ").append(self).append(":\n");
		sb.append(String.format(Locale.ROOT, "  START  self hp=%d/%d status=%s stages=%s magicReflect=%b  |  foe hp=%d/%d status=%s stages=%s magicReflect=%b\n",
				a0.currentHP, a0.getStat(0), a0.status, java.util.Arrays.toString(a0.statStages), a0.hasStatus(Status.MAGIC_REFLECT),
				f0.currentHP, f0.getStat(0), f0.status, java.util.Arrays.toString(f0.statStages), f0.hasStatus(Status.MAGIC_REFLECT)));
		for (int c : cols) {
			Action p0 = P.get(c);
			double yc = (y != null && c < y.length) ? y[c] : Double.NaN;
			sb.append(String.format(Locale.ROOT, "  -- vs player action '%s' (expected %.0f%% by the equilibrium)%s --\n", p0.label(), yc * 100,
					c == col ? "" : "; shown because an AI row takes the foe's life here"));
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
					sb.append("          ").append(evalTerms(br.state, cfg, w)).append('\n');
				}
			}
		}
		Print.debug(sb.toString());
	}

	/** Highest-expected player column in which some AI row leaves the foe's current active fainted (-1 if none). Debug only (re-simulates). */
	private static int firstKoColumn(SimState root, List<Action> A, List<Action> P, AIConfig cfg, double[] y) {
		int foeIdx = root.player.shell.indexOf(root.player.active());
		if (foeIdx < 0) return -1;
		Integer[] order = new Integer[P.size()];
		for (int j = 0; j < order.length; j++) order[j] = j;
		final double[] yy = y;
		java.util.Arrays.sort(order, (p, q) -> Double.compare(yy != null && q < yy.length ? yy[q] : 0, yy != null && p < yy.length ? yy[p] : 0));
		for (int j : order) {
			for (Action a : A) {
				for (Branch br : BattleSimulator.simulateTurn(root, a, P.get(j), cfg)) {
					Pokemon t = br.state.player.bench()[foeIdx];
					if (br.prob > 0.3 && (t == null || t.isFainted())) return j;
				}
			}
		}
		return -1;
	}

	/** Breaks eval() down into its terms at the (unmodified) root state, so a suspiciously large/uniform matrix can be traced to a specific term instead of guessed at. */
	private void logEvalBreakdown(Pokemon self, SimState root, AIConfig cfg, MonWeights weights) {
		if (!AI_DEBUG) return;
		double mat = Evaluator.material(root.ai, weights.ai) - Evaluator.material(root.player, weights.player);
		double matchup = Evaluator.matchupTerm(root, weights);
		double matchupLive = Evaluator.activeMatchup(root);
		double hazard = Evaluator.hazardPain(root.player, root.field) - Evaluator.hazardPain(root.ai, root.field);
		double status = Evaluator.benchStatusValue(root.player, root.field) - Evaluator.benchStatusValue(root.ai, root.field);
		double field = Evaluator.fieldValue(root);
		double tempo = Evaluator.tempo(root);
		double forced = Evaluator.forcedTurnPenalty(root);
		double total = Evaluator.eval(root, cfg.style, weights);
		Print.debug("[AIV2] weights ai=" + fmtW(weights.ai) + " player=" + fmtW(weights.player) + "\n");
		Print.debug(String.format(Locale.ROOT,
				"[AIV2] eval(root)=%.1f  material=%.1f matchup=%.1f (live %.1f) hazard=%.1f status=%.1f field=%.1f tempo=%.1f forced=%.1f%n",
				total, mat, matchup, matchupLive, hazard, status, field, tempo, forced));
	}

	/** One branch state's eval() split into its terms (same terms as the root breakdown), so a KO row can be compared with a chip row term by term. */
	private static String evalTerms(SimState s, AIConfig cfg, MonWeights w) {
		double total = Evaluator.eval(s, cfg.style, w);
		double mat = Evaluator.material(s.ai, w.ai) - Evaluator.material(s.player, w.player);
		double matchup = Evaluator.matchupTerm(s, w);
		double matchupLive = Evaluator.activeMatchup(s);
		double hazard = Evaluator.hazardPain(s.player, s.field) - Evaluator.hazardPain(s.ai, s.field);
		double status = Evaluator.benchStatusValue(s.player, s.field) - Evaluator.benchStatusValue(s.ai, s.field);
		double field = Evaluator.fieldValue(s);
		double tempo = Evaluator.tempo(s);
		return String.format(Locale.ROOT, "eval=%.1f  [material=%.1f matchup=%.1f (live %.1f) hazard=%.1f status=%.1f field=%.1f tempo=%.1f]",
				total, mat, matchup, matchupLive, hazard, status, field, tempo);
	}

	private static String fmtW(double[] w) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < w.length; i++) sb.append(i == 0 ? "" : ", ").append(String.format(Locale.ROOT, "%.2f", w[i]));
		return sb.append(']').toString();
	}

	private static String trunc(String s, int n) {
		return s.length() <= n ? s : s.substring(0, Math.max(0, n - 1)) + "\u2026";
	}
}