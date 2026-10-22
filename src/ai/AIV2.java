package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ai.PlayerReader.Read;
import ai.SackAnalysis.Filtered;
import ai.Solver.Result;
import overworld.GamePanel;
import pokemon.*;
import ui.SimBattleUI;
import util.Pair;
import util.Print;

/**
 * The Trainer AI engine, used by every trainer unless {@code Trainer.ai} says otherwise ({@link TrainerAI.Config#defaultAI}).
 * One engine runs at every difficulty; {@link AIConfig#forDifficulty} is the only place difficulty is decided.
 * <p>
 * It is hard because it plays a mixed strategy against what the player is likely to do, not because it cheats: it never
 * reads the player's chosen action, only their sets (which are open) and their past habits.
 * <p>
 * <b>One decision</b> ({@link #plan}, then {@link #decide} samples from it):
 * <ol>
 * <li>{@link MonWeights} values every mon on both teams.</li>
 * <li>{@link ActionGen} lists the AI's actions (moves, switches, pivot-switches, sacks) and the player's.</li>
 * <li>{@link MatrixBuilder} simulates every pair of actions and scores the resulting positions.</li>
 * <li>{@link SackAnalysis} drops sack rows that do not clearly beat staying in.</li>
 * <li>Rows that another row beats in every column are removed ({@link #dominatedRows}), and the {@link Solver} finds the
 * equilibrium of what is left.</li>
 * <li>With a {@link PlayerModel} that has seen the player, the AI predicts the player's mix from their habits and blends
 * an exploit into the equilibrium ({@code x = alphaEff * x_eq + (1 - alphaEff) * x_exploit}).</li>
 * <li>{@link Shaper} cuts negligible probabilities, and {@link Shaper#sample} picks the action.</li>
 * </ol>
 * With an empty model, no model, or {@code useHistoryModel == false}, the result is exactly the equilibrium. The model
 * lives on the player ({@code Trainer.playerModel()}, saved with {@code Player}) and is read and updated only in
 * {@link #decide}; {@link #plan} is pure given the model's counts.
 * <p>
 * Forced situations are handled in {@link #decide} before the matrix: the scripted opening of a scripted encounter, and
 * Struggle-only turns (swap if possible, else Struggle).
 * <p>
 * <b>Debug output:</b> set {@link #AI_DEBUG} for a probability dump per decision (via {@link Print#debug}, so
 * {@code SelfPlay}'s {@code Print.setDebugSuppressed} silences it and also skips the work). With it on,
 * {@link #AI_DEBUG_MATRIX} adds the payoff matrix and {@link #AI_DEBUG_BRANCHES} adds per-branch detail.
 */
public final class AIV2 implements TrainerAI {
	public static final AIV2 INSTANCE = new AIV2(true, true);
	/** Self-play baseline: identical to INSTANCE but the AI never generates sack candidates ({@code cfg.enableSacking = false}). */
	public static final AIV2 NO_SACK = new AIV2(false, true);
	/** Self-play baseline: identical to INSTANCE but pure equilibrium, no player model ({@code cfg.useHistoryModel = false}). */
	public static final AIV2 NO_HISTORY = new AIV2(true, false);

	/** Master switch: one line per action with its sampling probability, and the chosen action. */
	public static boolean AI_DEBUG = true;
	/** Full payoff matrix. Verbose; only printed while {@link #AI_DEBUG} is on. */
	public static boolean AI_DEBUG_MATRIX = true;
	/** Resulting branch states (HP, status, stages, fainted) per action against one fixed player action. Only printed while {@link #AI_DEBUG} is on. See {@link #logBranchDetail}. */
	public static boolean AI_DEBUG_BRANCHES = true;

	private final boolean sackingEnabled;
	private final boolean historyEnabled;

	/** Decisions are serialized: the AI reads and writes shared static state (the tunable constants, the caches). */
	private static final Object AI_LOCK = new Object();

	/** Diagnostics run only when someone will see them; they re-simulate, so they are skipped entirely when output is suppressed. */
	private static boolean dbg() {
		return AI_DEBUG && !Print.isDebugSuppressed();
	}

	private AIV2(boolean sackingEnabled, boolean historyEnabled) {
		this.sackingEnabled = sackingEnabled;
		this.historyEnabled = historyEnabled;
	}

	@Override
	public String getName() {
		String n = "AI_V2 (Phase 6";
		if (!sackingEnabled) n += ", no sack";
		if (!historyEnabled) n += ", no history";
		return n + ")";
	}

	/** Everything {@link #decide} computes before sampling, public so tests run the exact production pipeline. */
	public static final class Plan {
		/** The AI's rows and the player's columns. {@code A} is already filtered by the sack guard; {@code M[i][j]} is the payoff of {@code A[i]} against {@code P[j]}. */
		public final List<Action> A, P;
		public final double[][] M;
		public final MonWeights weights;
		/** Sack-only slots (sack candidates that are not also answers); empty when the AI cannot switch or sacking is off. */
		public final Set<Integer> sackOnly;
		/** ALL sack candidates (see {@code ActionGen.BenchPlan.sacks}), including ones that are also top-N answers. This
		 * decides the sack label (the chosen row switches into ANY sack candidate); {@link #sackOnly} is narrower (only
		 * what the min-gain guard judges) and stays separate for reporting. */
		public final Set<Integer> sacks;
		/** Sack guard result (min-gain and same-mon-return rules), null when there were no sack rows to judge. A/M above are already filtered. */
		public final Filtered guard;
		/** The equilibrium: {@code eq.x} is full length (zeros on dominated rows), {@code eq.y} has one entry per player column. */
		public final Result eq;
		/** The strategy to sample from: the equilibrium after the player-model blend and shaping. Full length, zeros on dominated rows. */
		public final double[] x;
		/** The predicted player mix ({@code eq.y} itself when there is no model or no history yet). */
		public final double[] yHat;
		/** The player model's reading of this situation, null without a model. */
		public final Read read;
		/** Weight of the equilibrium in the blend (1.0 = pure equilibrium). */
		public final double alphaEff;

		Plan(List<Action> A, List<Action> P, double[][] M, MonWeights weights, Set<Integer> sackOnly, Set<Integer> sacks,
				SackAnalysis.Filtered guard, Solver.Result eq, double[] x, double[] yHat, PlayerReader.Read read, double alphaEff) {
			this.A = A;
			this.P = P;
			this.M = M;
			this.weights = weights;
			this.sackOnly = sackOnly;
			this.sacks = sacks;
			this.guard = guard;
			this.eq = eq;
			this.x = x;
			this.yHat = yHat;
			this.read = read;
			this.alphaEff = alphaEff;
		}
	}

	/** {@link #plan(SimState, AIConfig, PlayerModel)} with no player model: {@code x} is the shaped equilibrium and {@code yHat} is {@code eq.y}. */
	public static Plan plan(SimState root, AIConfig cfg) {
		return plan(root, cfg, null);
	}

	/**
	 * Rows, columns, payoff matrix, sack guard, equilibrium, player prediction and shaped strategy for one decision.
	 * No RNG is drawn and {@code model} is not modified (pure given the model's counts). Null if the AI has no rows.
	 * A null {@code model}, {@code cfg.useHistoryModel == false} or an empty model gives the plain equilibrium.
	 */
	public static Plan plan(SimState root, AIConfig cfg, PlayerModel model) {
		MonWeights weights = MonWeights.compute(root);
		List<Action> A = ActionGen.genAIActions(root, cfg, weights);
		List<Action> P = ActionGen.genPlayerActions(root, cfg, weights);
		if (P.isEmpty()) P = Collections.singletonList(Action.PASS);
		if (A.isEmpty()) return null;

		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, weights);

		// Sack guard: a sack row only survives if it clearly beats staying in.
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

		// Solve on the reduced game: rows another row beats everywhere are removed, so they can never pick up probability
		// from the solver's averaging noise or be tie-broken into by the blend. keep[r] maps a reduced row to its full index.
		boolean[] drop = dominatedRows(M);
		int n = 0;
		for (boolean d : drop) if (!d) n++;
		int[] keep = new int[n];
		double[][] Mr = new double[n][];
		for (int i = 0, r = 0; i < M.length; i++) {
			if (drop[i]) continue;
			keep[r] = i;
			Mr[r++] = M[i];
		}

		Solver.Result eqR = Solver.solveZeroSum(Mr);
		double[] xr = eqR.x.clone();
		equalizeDuplicates(Mr, xr, new boolean[n]); // identical rows share their mass evenly, so move order cannot matter

		// Player model: read() is computed whenever there is a model (decide() needs its situation to start observing even
		// while w is still 0); the blend only changes anything once the model has data.
		PlayerReader.Read read = model != null && cfg.useHistoryModel ? PlayerReader.read(model, root, P, Mr, xr) : null;
		double[] yHat = eqR.y;
		double[] xRaw = xr;
		double alphaEff = 1.0;
		if (read != null && read.w > 0) {
			yHat = PlayerModel.predict(eqR.y, read.classes, read.hist, read.w);
			alphaEff = PlayerModel.alphaEff(cfg.alpha, read.w);
			xRaw = PlayerModel.finalStrategy(Mr, xr, yHat, alphaEff, cfg.temperatureExploit);
		}
		double[] xs = Shaper.shape(xRaw, cfg);

		// Expand back to full length (zeros on dropped rows) so A, M and x stay aligned.
		double[] x = new double[A.size()], xeq = new double[A.size()];
		for (int r = 0; r < n; r++) {
			x[keep[r]] = xs[r];
			xeq[keep[r]] = xr[r];
		}
		Solver.Result eq = new Solver.Result(xeq, eqR.y);
		return new Plan(A, P, M, weights, sackOnly, sacks, guard, eq, x, yHat, read, alphaEff);
	}

	@Override
	public MoveDecision decide(Pokemon self, Pokemon foe, int difficulty) {
		synchronized (AI_LOCK) {
			AIConfig cfg = AIConfig.forDifficulty(difficulty);
			cfg.enableSacking = sackingEnabled;
			cfg.useHistoryModel = historyEnabled;

			// Learn what the player did last turn BEFORE anything below can return, so a forced-action turn does not
			// leave a gap. The model belongs to the player's trainer (saved with Player); null without a trainer.
			PlayerModel model = cfg.useHistoryModel && foe.trainer != null ? foe.trainer.playerModel() : null;
			if (model != null) {
				PlayerReader.Observation seen = PlayerReader.observe(model, self, foe);
				if (seen != null && dbg()) {
					Print.debug(String.format(Locale.ROOT, "[AIV2] observed the player: %s (%s) -> model %s\n", seen.cls, seen.note, model.summary()));
				}
			}

			// ---- forced situations, decided without the matrix ----
			if (self.script && Pokemon.field.turns == 0) { // scripted encounter: fixed opening move
				if (Pokemon.gp.currentMap == 160) return new MoveDecision(self.getStatusMove());
				return new MoveDecision(self.moveset[0].move);
			}

			ArrayList<Move> validMoves = self.getValidMoveset();
			boolean canSwitch = self.trainer.canSwitch(foe);
			if (validMoves.isEmpty()) {
				// Struggle-only: swaps at every difficulty if possible. The target comes from the matrix restricted to
				// switch rows (sack candidates included); if trapped, Struggle.
				if (canSwitch) {
					int slot = pickStruggleSwitch(self, foe, cfg);
					return new MoveDecision(Move.GROWL, Status.SWAP, slot + 1);
				}
				return new MoveDecision(Move.STRUGGLE);
			}
			// Mid-sequence statuses (recharge, charging, semi-invulnerable, locked, encored): getValidMoveset() has already
			// narrowed validMoves to the forced move. The matrix still lets switching compete when it is legal.

			// ---- the matrix decision ----
			SimState root = SimState.snapshot(self, foe);
			Plan plan = plan(root, cfg, model);
			if (plan == null) return new MoveDecision(Move.STRUGGLE); // defensive; validMoves was non-empty above
			if (model != null && plan.read != null) PlayerReader.begin(model, self, foe, plan.read);
			List<Action> A = plan.A;
			List<Action> P = plan.P;
			double[] x = plan.x;

			logMatrix(self, plan);
			logEvalBreakdown(self, root, cfg, plan.weights);
			logRead(self, plan);
			logBranchDetail(self, root, A, P, cfg, plan.yHat, plan.weights);
			logSack(self, root, plan, cfg);

			int chosen = Shaper.sample(x);
			Action chosenAct = A.get(chosen);
			SackAnalysis.Label label = null;
			if (chosenAct.kind != ActionKind.MOVE && (dbg() || (Pokemon.gp != null && Pokemon.gp.gameState == GamePanel.SIM_BATTLE_STATE))) {
				label = SackAnalysis.classify(root, chosenAct, P, plan.yHat, cfg, plan.sacks); // re-simulates; only needed for the log and the sim UI
			}
			logDecision(self, A, x, chosenAct, x[chosen], plan.sacks);
			logSackChosen(self, chosenAct, x[chosen], label);
			report(self, A, x, chosenAct, x[chosen], label);

			return toMoveDecision(chosenAct);
		}
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

	/** Struggle-only forced swap: the best switch row by the matrix, restricted to {@code candidateBench}. Returns a 0-based team slot. */
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
	 * Fills the sim-battle UI's probability displays: {@code simBattleUI.p1Moves/p2Moves} (moves only, renormalized) and
	 * {@code p1Switch/p2Switch} reasons from {@code x}. The first AI to report in a turn takes slot one, the second slot two.
	 * Only active in a simulated (betting) battle.
	 */
	private void report(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb, SackAnalysis.Label label) {
		if (Pokemon.gp == null || Pokemon.gp.gameState != GamePanel.SIM_BATTLE_STATE) return;
		SimBattleUI ui = Pokemon.gp.simBattleUI;
		boolean slotOne = ui.p1Moves == null && ui.p1Switch == null;

		double moveMass = 0;
		for (int i = 0; i < A.size(); i++) if (A.get(i).kind != ActionKind.SWITCH) moveMass += x[i];

		if (chosenAct.kind == ActionKind.SWITCH || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			String rsn = reason(self, chosenAct, chosenProb, label);
			Pair<Pokemon, String> p = new Pair<>(self, Item.breakString(rsn, 63));
			if (slotOne) ui.p1Switch = p; else ui.p2Switch = p;
		}
		if (chosenAct.kind == ActionKind.MOVE || chosenAct.kind == ActionKind.MOVE_THEN_SWITCH) {
			double pct = moveMass > 0 ? (chosenProb / moveMass) * 100.0 : chosenProb * 100.0;
			Pair<Pokemon, Double> p = new Pair<>(self, pct);
			if (slotOne) ui.p1Moves = p; else ui.p2Moves = p;
		}
	}

	/** UI/debug reason for a switch: the sack line when the chosen switch is labeled a sack, else the plain matrix line. */
	static String reason(Pokemon self, Action chosenAct, double chosenProb, SackAnalysis.Label label) {
		Pokemon target = self.trainer.team[chosenAct.slot];
		if (label != null && label.sack) {
			// A replacement plan is only meaningful when the target actually tends to faint (SackAnalysis.SACK_LABEL_P);
			// otherwise there is no "free replacement" to plan for, so the segment is left out rather than shown as "?".
			String replSegment = label.replacementSlot >= 0
					? String.format(Locale.ROOT, " | replacement plan: %s", self.trainer.team[label.replacementSlot])
					: "";
			return String.format(Locale.ROOT, "[Sack: %s for %s%s | P(target faints) = %.0f%%]\n",
					target, self, replSegment, label.pTargetFaints * 100);
		}
		return String.format(Locale.ROOT, "[Matrix: switch to %s %.0f%%]\n", target, chosenProb * 100);
	}

	/**
	 * Sack-candidate summary, plus a per-column breakdown for each row the sack guard actually dropped: the matrix payoff
	 * and the best Stay payoff it was compared against (the figures "gain" is built from) and, re-simulated since the
	 * matrix only holds the weighted eval score, whether the target faints in that column and how much HP it keeps if
	 * not. A dropped row's cells are not in the post-filter {@code plan.A/plan.M}, so this is where its numbers show.
	 */
	private void logSack(Pokemon self, SimState root, Plan plan, AIConfig cfg) {
		if (!dbg() || plan.sacks.isEmpty()) return;
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
		if (!dbg() || label == null || !label.sack) return;
		Print.debug(String.format(Locale.ROOT, "[AIV2] SACK CHOSEN (%s, %.1f%%): %s", chosenAct.label(), chosenProb * 100, reason(self, chosenAct, chosenProb, label)));
	}

	/** One line per action and its sampling probability, plus the pick. Debug only. */
	private void logDecision(Pokemon self, List<Action> A, double[] x, Action chosenAct, double chosenProb, Set<Integer> sacks) {
		if (!dbg()) return;
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

	/** The player model's reading: situation, trust, and per-class history / equilibrium / prediction. Silent without a model. */
	private void logRead(Pokemon self, Plan plan) {
		if (!dbg() || plan.read == null) return;
		Print.debug(String.format(Locale.ROOT, "[AIV2] %s: %s\n", self, PlayerReader.describe(plan.read, plan.eq.y, plan.yHat, plan.alphaEff)));
	}

	/** Full A x P payoff matrix. Verbose; only printed while {@link #AI_DEBUG_MATRIX} is on. */
	private void logMatrix(Pokemon self, Plan plan) {
		if (!dbg() || !AI_DEBUG_MATRIX) return;
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
		// Rows the sack guard removed before solving: shown with their ORIGINAL payoffs so they are not silently absent.
		if (plan.guard != null && !plan.guard.dropped.isEmpty()) {
			sb.append("  -- dropped by the sack guard (not in the solve or in the probabilities below) --\n");
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
	 * Diagnostic ({@link #AI_DEBUG_BRANCHES}): for every AI action, the resulting branch(es) against player columns that explain the decision:
	 * the column the equilibrium expects most, plus (when different) the highest-expected column in which some AI row takes the foe
	 * active's life, so a KO line can be compared with a chip line term by term. Each branch carries its eval() split into terms;
	 * START is the real state before the turn, since every other line is a projection AFTER it.
	 */
	private void logBranchDetail(Pokemon self, SimState root, List<Action> A, List<Action> P, AIConfig cfg, double[] y, MonWeights w) {
		if (!dbg() || !AI_DEBUG_BRANCHES || P.isEmpty()) return;
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

	/** Breaks eval() down into its terms at the (unmodified) root state, so a suspiciously large or uniform matrix can be traced to a specific term. */
	private void logEvalBreakdown(Pokemon self, SimState root, AIConfig cfg, MonWeights weights) {
		if (!dbg()) return;
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

	/**
	 * Rows weakly dominated by another row (at least as good in every column, strictly better in some). Removing them
	 * does not change the game's value, and keeps a row that is never better from picking up probability by chance.
	 * Exact duplicates are NEVER dropped (neither dominates the other): they are all kept and share their mass evenly, see
	 * {@link #equalizeDuplicates}.
	 */
	public static boolean[] dominatedRows(double[][] M) {
		final double eps = 1e-9;
		int n = M.length;
		boolean[] drop = new boolean[n];
		for (int i = 0; i < n; i++) {
			for (int k = 0; k < n; k++) {
				if (k == i) continue;
				boolean ge = true, gt = false;
				for (int j = 0; j < M[i].length && ge; j++) {
					if (M[k][j] < M[i][j] - eps) ge = false;
					else if (M[k][j] > M[i][j] + eps) gt = true;
				}
				if (ge && gt) { drop[i] = true; break; }
			}
		}
		return drop;
	}

	/** Spreads each group of identical kept rows' total mass evenly, so the order of the move list cannot matter. */
	static void equalizeDuplicates(double[][] M, double[] x, boolean[] drop) {
		final double eps = 1e-9;
		for (int i = 0; i < M.length; i++) {
			if (drop[i]) continue;
			java.util.List<Integer> group = new java.util.ArrayList<>();
			for (int k = i; k < M.length; k++) {
				if (drop[k]) continue;
				boolean same = true;
				for (int j = 0; j < M[i].length && same; j++) same = Math.abs(M[k][j] - M[i][j]) <= eps;
				if (same) group.add(k);
			}
			if (group.size() < 2 || group.get(0) != i) continue; // handle each group once, from its first member
			double total = 0;
			for (int k : group) total += x[k];
			for (int k : group) x[k] = total / group.size();
		}
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