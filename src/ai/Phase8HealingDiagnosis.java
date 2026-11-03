package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pokemon.*;
import test.Phase4Tests;

/**
 * Why does the AI over-pick healing moves? Diagnosis only, no assertions about the answer (except that the breakdown tool
 * sums to eval). {@link #diagnose()} sweeps: AI faster / slower, a weak and a strong foe attack, and the AI's HP from full
 * down to 30%, with the AI holding Recover and Earthquake against a Tackle / Flamethrower foe (species 1 vs 4, via
 * {@code Phase4Tests}). For each scenario it prints
 * <ul>
 * <li>how hard the foe hits the AI and how hard Earthquake hits the foe (percent of max HP),</li>
 * <li>the matrix over the AI's two MOVE rows (the player's columns as generated, so switch columns included) and the
 * solved mixed strategy over those two rows,</li>
 * <li>the Recover cell minus the Earthquake cell against the foe's attack column, split into eval terms ({@code Evaluator.breakdown}),
 * so it is visible whether the gap comes from material, from the matchup term, or from tempo.</li>
 * </ul>
 * Change {@link #HEAL} / {@link #ATTACK} below if Recover is not the move you want to test.
 */
public final class Phase8HealingDiagnosis {
	private Phase8HealingDiagnosis() {}

	static final Move HEAL = Move.RECOVER;
	static final Move ATTACK = Move.EARTHQUAKE;

	public static void runAll() {
		Field prev = Pokemon.field;
		try {
			Phase4Tests.Duel d = Phase4Tests.duel(Phase4Tests.mk(1, HEAL, ATTACK), Phase4Tests.mk(4, Move.TACKLE));
			SimState root = d.root();
			MonWeights w = MonWeights.compute(root);
			EvalWeights style = AIConfig.hard().style;
			check("breakdown sums to eval (root)", Math.abs(sum(Evaluator.breakdown(root, style, w)) - Evaluator.eval(root, style, w)) < 1e-9);
			SimState hurt = d.root();
			hurt.ai.active().currentHP = hurt.ai.active().getStat(0) / 3;
			check("breakdown sums to eval (hurt AI)", Math.abs(sum(Evaluator.breakdown(hurt, style, w)) - Evaluator.eval(hurt, style, w)) < 1e-9);
			for (Branch b : BattleSimulator.simulateTurn(root, new Action(HEAL), new Action(Move.TACKLE), AIConfig.hard())) {
				check("breakdown sums to eval (branch)", Math.abs(sum(Evaluator.breakdown(b.state, style, w)) - Evaluator.eval(b.state, style, w)) < 1e-9);
			}
			System.out.println("[Phase8 healing] breakdown parity OK");
			bufferTests();
		} finally {
			Pokemon.field = prev;
		}
	}

	/**
	 * With the HP buffer ON: (1) the AI does not heal an unthreatened mon, (2) a row that was right without the buffer stays right
	 * (the Flamethrower rows, including the faster / 30% HP one the first version broke), (3) the buffer does not tax progress:
	 * removing HP from the foe is worth exactly what it is worth with the buffer off.
	 */
	private static void bufferTests() {
		double w0 = Evaluator.W_BUFFER;
		try {
			Evaluator.W_BUFFER = 1;
			check("buffer on, faster, weak foe, 100% HP: no heal", scenarioQuiet(true, Move.TACKLE, 1.0) <= 0.05);
			check("buffer on, faster, weak foe, 70% HP: no heal", scenarioQuiet(true, Move.TACKLE, 0.7) <= 0.05);
			check("buffer on, faster, weak foe, 50% HP: heal share <= 35%", scenarioQuiet(true, Move.TACKLE, 0.5) <= 0.35);
			check("buffer on, slower, weak foe, 50% HP: heal share <= 35%", scenarioQuiet(false, Move.TACKLE, 0.5) <= 0.35);
			check("buffer on, faster, Flamethrower, 70% HP: no heal", scenarioQuiet(true, Move.FLAMETHROWER, 0.7) <= 0.05);
			check("buffer on, faster, Flamethrower, 30% HP: no heal (the first version flipped this row)", scenarioQuiet(true, Move.FLAMETHROWER, 0.3) <= 0.05);

			Phase4Tests.Duel d = Phase4Tests.duel(Phase4Tests.mk(1, HEAL, ATTACK), Phase4Tests.mk(4, Move.TACKLE));
			SimState full = d.root(), hurt = d.root();
			hurt.player.active().currentHP = Math.max(1, hurt.player.active().getStat(0) / 3);
			MonWeights w = MonWeights.compute(full);
			EvalWeights style = AIConfig.hard().style;
			Evaluator.W_BUFFER = 0;
			double progressOff = Evaluator.eval(hurt, style, w) - Evaluator.eval(full, style, w);
			Evaluator.W_BUFFER = 1;
			double progressOn = Evaluator.eval(hurt, style, w) - Evaluator.eval(full, style, w);
			check("damaging the foe is worth the same with the buffer on (off " + progressOff + ", on " + progressOn + ")", Math.abs(progressOn - progressOff) < 1e-6);
			System.out.println("[Phase8 healing] buffer tests OK");
		} finally {
			Evaluator.W_BUFFER = w0;
		}
	}

	private static double scenarioQuiet(boolean aiFaster, Move foeMove, double hp) {
		java.io.PrintStream out = System.out;
		try {
			System.setOut(new java.io.PrintStream(new java.io.OutputStream() { @Override public void write(int b) {} }));
			return scenario(aiFaster, foeMove, hp, 0);
		} finally {
			System.setOut(out);
		}
	}

	/** The real-weights, full-bench sweep with the HP buffer on: compare with the first block of {@link #diagnose()}. */
	public static void diagnoseBuffer() {
		double w0 = Evaluator.W_BUFFER;
		Field prev = Pokemon.field;
		try {
			Evaluator.W_BUFFER = 1;
			System.out.println("\n================ HP BUFFER ON, real weights, full benches ================");
			for (boolean aiFaster : new boolean[] { true, false }) {
				for (Move foeMove : new Move[] { Move.TACKLE, Move.FLAMETHROWER }) {
					for (double hp : new double[] { 1.0, 0.7, 0.5, 0.3 }) scenario(aiFaster, foeMove, hp, 0);
				}
			}
		} finally {
			Evaluator.W_BUFFER = w0;
			Pokemon.field = prev;
		}
	}

	public static void diagnose() {
		Field prev = Pokemon.field;
		try {
			String[] titles = { "REAL MON WEIGHTS, full benches", "MON WEIGHTS FORCED TO 1.0 (both sides)",
					"1v1: every bench mon fainted (no foe escape, so the matchup term is not capped)" };
			for (int mode = 0; mode < 3; mode++) {
				System.out.println("\n================ " + titles[mode] + " ================");
				for (boolean aiFaster : new boolean[] { true, false }) {
					for (Move foeMove : new Move[] { Move.TACKLE, Move.FLAMETHROWER }) {
						for (double hp : new double[] { 1.0, 0.7, 0.5, 0.3 }) {
							scenario(aiFaster, foeMove, hp, mode);
						}
					}
				}
			}
		} finally {
			Pokemon.field = prev;
		}
	}

	/** Prints one scenario and returns the solved probability of the heal row. */
	private static double scenario(boolean aiFaster, Move foeMove, double hpFrac, int mode) {
		AIConfig cfg = AIConfig.hard();
		Phase4Tests.Duel d = Phase4Tests.duel(Phase4Tests.mk(1, HEAL, ATTACK), Phase4Tests.mk(4, foeMove));
		SimState root = d.root();
		Pokemon ai = root.ai.active(), foe = root.player.active();
		ai.currentHP = Math.max(1, (int) Math.round(ai.getStat(0) * hpFrac));
		ai.statStages[4] = aiFaster ? 6 : -6; // speed stage
		foe.statStages[4] = 0;
		if (mode == 2) {
			for (SideState side : new SideState[] { root.ai, root.player }) {
				for (Pokemon bp : side.bench()) {
					if (bp != null && bp != side.active()) { bp.currentHP = 0; bp.fainted = true; }
				}
			}
		}
		MonWeights w = MonWeights.compute(root);
		int aiIdx = root.ai.shell.indexOf(ai), foeIdx = root.player.shell.indexOf(foe);
		double wAi = w.ai[aiIdx], wFoe = w.player[foeIdx];
		if (mode == 1) {
			for (int i = 0; i < w.ai.length; i++) if (w.ai[i] > 0) w.ai[i] = 1.0;
			for (int i = 0; i < w.player.length; i++) if (w.player[i] > 0) w.player[i] = 1.0;
		}

		double foeHitPct, myHitPct;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			DamageRange fr = Evaluator.bestRangeRaw(foe, ai, root.field);
			DamageRange mr = Evaluator.bestRangeRaw(ai, foe, root.field);
			foeHitPct = fr == null ? 0 : 100.0 * fr.expectedCapped(ai.getStat(0)) / ai.getStat(0);
			myHitPct = mr == null ? 0 : 100.0 * mr.expectedCapped(foe.getStat(0)) / foe.getStat(0);
		}
		System.out.println(String.format(Locale.ROOT,
				"\n[heal] AI %s, foe %s, AI at %.0f%% HP | foe hits AI for %.0f%% of max HP, %s hits foe for %.0f%% of max HP | mon weights: AI active %.2f, foe active %.2f",
				aiFaster ? "FASTER" : "SLOWER", foeMove, hpFrac * 100, foeHitPct, ATTACK, myHitPct, wAi, wFoe));

		List<Action> A = new ArrayList<>();
		A.add(new Action(HEAL));
		A.add(new Action(ATTACK));
		List<Action> P = ActionGen.genPlayerActions(root, cfg, w);
		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, w);
		double[] x = Shaper.shape(Solver.solveZeroSum(M).x, cfg);

		StringBuilder cols = new StringBuilder("    columns:");
		for (Action p : P) cols.append(String.format(Locale.ROOT, " %14s", label(p)));
		System.out.println(cols);
		for (int i = 0; i < A.size(); i++) {
			StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "    %-8s p=%5.1f%% |", A.get(i).move, x[i] * 100));
			for (int j = 0; j < P.size(); j++) sb.append(String.format(Locale.ROOT, " %14.1f", M[i][j]));
			System.out.println(sb);
		}

		int col = -1;
		for (int j = 0; j < P.size(); j++) if (P.get(j).kind == ActionKind.MOVE) { col = j; break; }
		if (col < 0) return x[0];
		double[] tHeal = avgBreakdown(root, A.get(0), P.get(col), cfg, w);
		double[] tAtk = avgBreakdown(root, A.get(1), P.get(col), cfg, w);
		StringBuilder diff = new StringBuilder("    " + HEAL + " minus " + ATTACK + " vs " + label(P.get(col)) + ":");
		double total = 0;
		for (int k = 0; k < Evaluator.TERMS.length; k++) {
			double dv = tHeal[k] - tAtk[k];
			total += dv;
			if (Math.abs(dv) >= 0.05) diff.append(String.format(Locale.ROOT, "  %s %+.1f", Evaluator.TERMS[k], dv));
		}
		diff.append(String.format(Locale.ROOT, "   = %+.1f", total));
		System.out.println(diff);
		System.out.println(String.format(Locale.ROOT,
				"    matchup term, live -> after the foe-escape cap:  %s %.1f -> %.1f   %s %.1f -> %.1f",
				HEAL, liveMatchup(root, A.get(0), P.get(col), cfg), tHeal[1], ATTACK, liveMatchup(root, A.get(1), P.get(col), cfg), tAtk[1]));
		return x[0];
	}

	/** Probability-weighted breakdown of the cell (row a against column p), before the matrix's cell adjustments. */
	private static double[] avgBreakdown(SimState root, Action a, Action p, AIConfig cfg, MonWeights w) {
		double[] acc = new double[Evaluator.TERMS.length];
		for (Branch b : BattleSimulator.simulateTurn(root, a, p, cfg)) {
			double[] t = Evaluator.breakdown(b.state, cfg.style, w);
			for (int k = 0; k < acc.length; k++) acc[k] += b.prob * t[k];
		}
		return acc;
	}

	/** Probability-weighted live matchup (before the escape cap) of the cell's end states, times the style weight. */
	private static double liveMatchup(SimState root, Action a, Action p, AIConfig cfg) {
		double v = 0;
		for (Branch b : BattleSimulator.simulateTurn(root, a, p, cfg)) v += b.prob * cfg.style.wMatchup * Evaluator.activeMatchup(b.state);
		return v;
	}

	private static String label(Action a) {
		return a.kind + (a.move != null ? " " + a.move : "") + (a.kind == ActionKind.SWITCH ? " " + a.slot : "");
	}

	private static double sum(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return s;
	}

	private static void check(String what, boolean ok) {
		if (!ok) throw new AssertionError(what);
	}
}