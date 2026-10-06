package pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import util.Print;

/**
 * Phase 7 (§7.15): EXTREME lead selection. A one-shot simultaneous game: rows are the AI's selectable mons, columns are the
 * player's selectable mons ({@link Trainer#isSelectableLead}, the same predicate the preview UI's select handler uses).
 * <p>
 * Pipeline ({@link #plan}), pure and Rng-free (the only draw is {@link #choose}'s final sample):
 * <ol>
 * <li>{@link #startState}: the REAL current HP / status of both teams on a clean field with no hazards.</li>
 * <li>{@link MonWeights#compute} on that start state (§7.14.1; also breaks ties through the material term).</li>
 * <li>Static pass: for every pair, {@link BattleSimulator#simulateEntry} (both leads swapIn, speed order, NO end of turn,
 * exactly like the real battle start) then {@code Evaluator.eval}.</li>
 * <li>Refinement: the top K_ai rows x top K_player columns of the static equilibrium are replaced by the game value of a
 * lite one-turn matrix built from that entry state (captures "my lead gets countered, so I pivot", Fake Out, Sash, hazards).
 * Unrefined cells are shifted by the mean (refined - static) gap so the two scales agree.</li>
 * <li>Solve the zero-sum lead matrix, shape, sample.</li>
 * </ol>
 * The player's lead CHOICE is never an input: only the player's team. Game thread only.
 */
public final class LeadSelection {
	private LeadSelection() {}

	/** Everything {@link #choose} computes before sampling; public so tests run the exact production pipeline. */
	public static final class Plan {
		/** Team indices of the rows / columns. */
		public final List<Integer> aiSlots, playerSlots;
		/** Static entry values, and the final matrix after refinement + shift. Both [aiSlots.size()][playerSlots.size()]. */
		public final double[][] staticL, L;
		public final boolean[][] refined;
		/** Mean (refined - static) over the refined block, added to every unrefined cell. 0 when nothing was refined. */
		public final double shift;
		/** Raw equilibrium row / column strategies of L, and the shaped sampling distribution over the rows. */
		public final double[] xRaw, y, x;
		public final MonWeights weights;
		public final long millis;

		Plan(List<Integer> aiSlots, List<Integer> playerSlots, double[][] staticL, double[][] L, boolean[][] refined, double shift,
				double[] xRaw, double[] y, double[] x, MonWeights weights, long millis) {
			this.aiSlots = aiSlots;
			this.playerSlots = playerSlots;
			this.staticL = staticL;
			this.L = L;
			this.refined = refined;
			this.shift = shift;
			this.xRaw = xRaw;
			this.y = y;
			this.x = x;
			this.weights = weights;
			this.millis = millis;
		}
	}

	/** Team indices of the mons {@link Trainer#isSelectableLead} accepts, ascending. */
	public static List<Integer> selectable(Trainer t) {
		List<Integer> out = new ArrayList<>();
		for (int i = 0; i < t.team.length; i++) if (t.team[i] != null && !t.team[i].isFainted()) out.add(i);
		return out;
	}

	/** The battle-start state: real HP/status/PP of both teams (full shells), clean field, no hazards or screens, turn 0. */
	public static SimState startState(Trainer ai, Trainer player) {
		Trainer a = ai.simShell();
		Trainer p = player.simShell();
		a.getFieldEffectList().clear();
		p.getFieldEffectList().clear();
		return new SimState(new Field(), new SideState(a), new SideState(p), 0);
	}

	/**
	 * Pure. null when the AI has no selectable mon. Draws nothing from the real Rng (the whole computation runs in one
	 * SimContext scope, so every internal draw, e.g. a speed tie, comes from the isolated stream).
	 */
	public static Plan plan(Trainer ai, Trainer player, AIConfig cfg) {
		long t0 = System.nanoTime();
		List<Integer> aiSlots = selectable(ai);
		List<Integer> pSlots = selectable(player);
		if (aiSlots.isEmpty()) return null;

		Field prevField = Pokemon.field;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			SimState start = startState(ai, player);
			Pokemon.field = start.field;
			MonWeights w = MonWeights.compute(start);
			int n = aiSlots.size(), m = pSlots.size();

			// Nothing to play against, or nothing to choose between: no matrix. (m == 0 cannot happen in a real battle.)
			if (n == 1 || m == 0) {
				double[] x = new double[n];
				int pick = 0;
				if (n > 1) for (int i = 1; i < n; i++) if (w.ai[aiSlots.get(i)] > w.ai[aiSlots.get(pick)]) pick = i;
				x[pick] = 1.0;
				return new Plan(aiSlots, pSlots, new double[n][m], new double[n][m], new boolean[n][m], 0, x.clone(), new double[m], x, w,
						(System.nanoTime() - t0) / 1_000_000);
			}

			// ---- static pass ----
			SimState[][] entry = new SimState[n][m];
			double[][] S = new double[n][m];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					entry[i][j] = BattleSimulator.simulateEntry(start, aiSlots.get(i), pSlots.get(j), true);
					S[i][j] = evalPinned(entry[i][j], cfg, w);
				}
			}

			// ---- refine the plausible region ----
			boolean[][] refined = new boolean[n][m];
			double[][] L = new double[n][m];
			double gapSum = 0;
			int gapCount = 0;
			int kA = Math.min(n, Math.max(0, cfg.leadRefineKAi));
			int kP = Math.min(m, Math.max(0, cfg.leadRefineKPlayer));
			double[][] R = new double[n][m];
			if (kA > 0 && kP > 0) {
				Solver.Result r0 = Solver.solveZeroSum(S);
				List<Integer> rows = topRows(S, r0.x, kA);
				List<Integer> cols = topCols(S, r0.y, kP);
				AIConfig lite = cfg.leadLite();
				for (int i : rows) {
					for (int j : cols) {
						double v = refinedValue(entry[i][j], lite, w);
						if (Double.isNaN(v)) continue;
						R[i][j] = v;
						refined[i][j] = true;
						gapSum += v - S[i][j];
						gapCount++;
					}
				}
			}
			double shift = gapCount > 0 ? gapSum / gapCount : 0;
			for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) L[i][j] = refined[i][j] ? R[i][j] : S[i][j] + shift;

			// ---- solve + shape ----
			Solver.Result eq = Solver.solveZeroSum(L);
			double[] x = Shaper.shape(eq.x, cfg);
			Plan plan = new Plan(aiSlots, pSlots, S, L, refined, shift, eq.x, eq.y, x, w, (System.nanoTime() - t0) / 1_000_000);
			log(ai, player, plan);
			return plan;
		} finally {
			Pokemon.field = prevField;
		}
	}

	/** Plan + the one Rng draw. The chosen mon is NOT made current here ({@link Trainer#chooseLead} does that). Null if there is no candidate. */
	public static Pokemon choose(Trainer ai, Trainer player, AIConfig cfg) {
		Plan p = plan(ai, player, cfg);
		if (p == null) return null;
		int k = Shaper.sample(p.x);
		return ai.team[p.aiSlots.get(k)];
	}

	/** Pure. Shaped row strategy for a lead matrix (solve, then {@link Shaper#shape}). Exposed so T24/T33 can test the math on hand-built matrices. */
	public static double[] solveLeads(double[][] L, AIConfig cfg) {
		return Shaper.shape(Solver.solveZeroSum(L).x, cfg);
	}

	/**
	 * Value of the zero-sum game M for the row player. {@link Solver.Result} carries no value, and the regret-matching
	 * averages are only approximate, so this returns the midpoint of the two certified bounds: what x guarantees against
	 * the best column, and what y concedes to the best row. They bracket the true value and converge together.
	 */
	public static double gameValue(double[][] M) {
		if (M.length == 0 || M[0].length == 0) return 0;
		Solver.Result r = Solver.solveZeroSum(M);
		double lower = Double.POSITIVE_INFINITY;
		for (int j = 0; j < M[0].length; j++) {
			double s = 0;
			for (int i = 0; i < M.length; i++) s += r.x[i] * M[i][j];
			lower = Math.min(lower, s);
		}
		double upper = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < M.length; i++) {
			double s = 0;
			for (int j = 0; j < M[0].length; j++) s += M[i][j] * r.y[j];
			upper = Math.max(upper, s);
		}
		return (lower + upper) / 2;
	}

	/** T25 helper: eval(entry with swapIn effects) - eval(the same pair with no entry effects applied). 0 for a lead with nothing that triggers. */
	public static double entryDelta(SimState start, int aiSlot, int playerSlot, AIConfig cfg, MonWeights w) {
		SimState with = BattleSimulator.simulateEntry(start, aiSlot, playerSlot, true);
		SimState without = BattleSimulator.simulateEntry(start, aiSlot, playerSlot, false);
		return evalPinned(with, cfg, w) - evalPinned(without, cfg, w);
	}

	// ---- internals ----

	/** Game value of one turn played from an entry state: the lite A x P matrix of the normal pipeline, solved. NaN if the AI has no row. */
	static double refinedValue(SimState entry, AIConfig lite, MonWeights w) {
		Field prev = Pokemon.field;
		try {
			Pokemon.field = entry.field;
			List<Action> A = ActionGen.genAIActions(entry, lite, w);
			if (A.isEmpty()) return Double.NaN;
			List<Action> P = ActionGen.genPlayerActions(entry, lite, w);
			if (P.isEmpty()) P = Collections.singletonList(Action.PASS);
			return gameValue(MatrixBuilder.buildMatrix(entry, A, P, lite, w));
		} finally {
			Pokemon.field = prev;
		}
	}

	private static double evalPinned(SimState s, AIConfig cfg, MonWeights w) {
		Field prev = Pokemon.field;
		try {
			Pokemon.field = s.field;
			return Evaluator.eval(s, cfg.style, w);
		} finally {
			Pokemon.field = prev;
		}
	}

	/** Rows by equilibrium mass of the static game, ties by mean static value (higher first). */
	private static List<Integer> topRows(double[][] S, double[] x, int k) {
		List<Integer> idx = new ArrayList<>();
		for (int i = 0; i < S.length; i++) idx.add(i);
		idx.sort((a, b) -> {
			int c = Double.compare(x[b], x[a]);
			return c != 0 ? c : Double.compare(mean(S[b]), mean(S[a]));
		});
		return new ArrayList<>(idx.subList(0, k));
	}

	/** Columns by equilibrium mass, ties by mean static value (lower first: the player's better columns). */
	private static List<Integer> topCols(double[][] S, double[] y, int k) {
		int m = S[0].length;
		double[] colMean = new double[m];
		for (int j = 0; j < m; j++) {
			for (double[] row : S) colMean[j] += row[j];
			colMean[j] /= S.length;
		}
		List<Integer> idx = new ArrayList<>();
		for (int j = 0; j < m; j++) idx.add(j);
		idx.sort((a, b) -> {
			int c = Double.compare(y[b], y[a]);
			return c != 0 ? c : Double.compare(colMean[a], colMean[b]);
		});
		return new ArrayList<>(idx.subList(0, k));
	}

	private static double mean(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return a.length == 0 ? 0 : s / a.length;
	}

	private static void log(Trainer ai, Trainer player, Plan p) {
		if (!AIV2.AI_DEBUG || Print.isDebugSuppressed()) return;
		StringBuilder sb = new StringBuilder("\n______________\nLEAD SELECTION (EXTREME)\n______________\n");
		sb.append(String.format(Locale.ROOT, "rows=%d cols=%d refinedShift=%.1f time=%d ms%n", p.aiSlots.size(), p.playerSlots.size(), p.shift, p.millis));
		sb.append("                    ");
		for (int j : p.playerSlots) sb.append(String.format(Locale.ROOT, "%-14.14s", player.team[j]));
		sb.append('\n');
		for (int i = 0; i < p.aiSlots.size(); i++) {
			sb.append(String.format(Locale.ROOT, "%-14.14s %5.1f%% ", ai.team[p.aiSlots.get(i)], p.x[i] * 100));
			for (int j = 0; j < p.playerSlots.size(); j++) {
				sb.append(String.format(Locale.ROOT, "%8.1f%s     ", p.L[i][j], p.refined[i][j] ? "*" : " "));
			}
			sb.append('\n');
		}
		sb.append("(* = refined by a one-turn game value; the rest are static entry evals + shift)\n\n");
		Print.debug(sb.toString());
	}
}