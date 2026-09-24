package pokemon;

import java.util.List;
import java.util.Locale;

/**
 * Debug output for a matrix cell: which eval TERM produced the number. Use it when a matrix-shaped test (T9/T10/T11) fails
 * and you need to know whether the simulator or the evaluator is responsible. Prints with System.out (not Print.debug), so
 * SelfPlay's debug suppression cannot hide it. Not used by the AI.
 * <pre>
 *     Phase4Debug.t9(false);   // the current T9 scenario (full-HP foe that Flamethrower OHKOs)
 *     Phase4Debug.t9(true);    // the ORIGINAL premise (foe at 1 HP), to reproduce the first failure
 *     Phase4Debug.t10();
 * </pre>
 */
final class Phase4Debug {
	private Phase4Debug() {}

	static void t9(boolean foeAtOneHp) {
		Phase4Tests.Duel d = foeAtOneHp ? Phase4Tests.boostDuel(Move.NASTY_PLOT) : Phase4Tests.ohkoDuel(Move.NASTY_PLOT);
		if (foeAtOneHp) d.f.currentHP = 1;
		explain(d, Move.NASTY_PLOT, Move.FLAMETHROWER);
	}

	static void t10() {
		explain(Phase4Tests.boostDuel(Move.NASTY_PLOT), Move.NASTY_PLOT, Move.FLAMETHROWER);
	}

	/** Prints the setup, the whole matrix with the equilibrium, then a per-branch term breakdown of (boost, attack) x every player column. */
	static void explain(Phase4Tests.Duel d, Move boost, Move attack) {
		SimState root = d.root();
		AIConfig cfg = AIConfig.hard();
		MonWeights w = MonWeights.compute(root);
		List<Action> A = ActionGen.genAIActions(root, cfg, w), P = ActionGen.genPlayerActions(root, cfg);
		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, w);

		System.out.printf(Locale.ROOT, "%n[Phase4Debug] AI %s L%d hp %d/%d spe %d | foe %s L%d hp %d/%d spe %d | AI faster: %b%n",
				d.a, d.a.level, d.a.currentHP, d.a.getStat(0), d.a.getStat(4), d.f, d.f.level, d.f.currentHP, d.f.getStat(0), d.f.getStat(4),
				d.a.getFaster(d.f, 0, 0, d.field) == d.a);
		try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT)) {
			DamageRange mine = d.a.calcRange(d.f, attack, true, d.field), theirs = d.f.calcRange(d.a, attack, true, d.field);
			System.out.printf(Locale.ROOT, "  AI %s vs foe: %s (koProb %.2f)%n  foe %s vs AI: %s (koProb %.2f)%n",
					attack, mine, mine.koProb(d.f.currentHP), attack, theirs, theirs.koProb(d.a.currentHP));
		}
		System.out.println("  weights ai=" + java.util.Arrays.toString(w.ai) + " player=" + java.util.Arrays.toString(w.player));

		double[] x = Solver.solveZeroSum(M).x;
		System.out.printf(Locale.ROOT, "%n  %-22s", "payoff (row = AI)");
		for (Action p : P) System.out.printf(Locale.ROOT, "%14s", trunc(p.label(), 13));
		System.out.printf(Locale.ROOT, "%10s%n", "x");
		for (int i = 0; i < A.size(); i++) {
			System.out.printf(Locale.ROOT, "  %-22s", trunc(A.get(i).label(), 21));
			for (int j = 0; j < P.size(); j++) System.out.printf(Locale.ROOT, "%14.1f", M[i][j]);
			System.out.printf(Locale.ROOT, "%10.3f%n", x[i]);
		}

		int b = Phase4Tests.rowOf(A, boost), atk = Phase4Tests.rowOf(A, attack);
		for (int j = 0; j < P.size(); j++) {
			System.out.println("\n  ---- column: " + P.get(j).label());
			if (b >= 0) cell(root, cfg, w, A.get(b), P.get(j));
			if (atk >= 0) cell(root, cfg, w, A.get(atk), P.get(j));
		}
	}

	private static void cell(SimState root, AIConfig cfg, MonWeights w, Action a, Action p) {
		List<Branch> bs = BattleSimulator.simulateTurn(root, a, p, cfg);
		double total = 0;
		System.out.println("    [" + a.label() + " vs " + p.label() + "]  " + bs.size() + " branch(es)");
		for (Branch br : bs) {
			SimState s = br.state;
			double mat = Evaluator.material(s.ai, w.ai) - Evaluator.material(s.player, w.player);
			double mu = Evaluator.activeMatchup(s);
			double hz = Evaluator.hazardPain(s.player, s.field) - Evaluator.hazardPain(s.ai, s.field);
			double st = Evaluator.benchStatusValue(s.player, s.field) - Evaluator.benchStatusValue(s.ai, s.field);
			double fv = Evaluator.fieldValue(s), tp = Evaluator.tempo(s), fo = Evaluator.forcedTurnPenalty(s);
			double pe = Evaluator.pendingValue(s, w.ai, w.player);
			double ev = Evaluator.eval(s, cfg.style, w.ai, w.player);
			total += br.prob * ev;
			System.out.printf(Locale.ROOT,
					"      p=%.3f eval=%7.1f | material %7.1f matchup %6.1f hazard %5.1f status %5.1f field %5.1f tempo %4.1f forced %4.1f pending %5.1f | AI %s %d/%d  foe %s %d/%d%n",
					br.prob, ev, mat, mu, hz, st, fv, tp, fo, pe,
					s.ai.active(), s.ai.active().currentHP, s.ai.active().getStat(0), s.player.active(), s.player.active().currentHP, s.player.active().getStat(0));
		}
		System.out.printf(Locale.ROOT, "      => cell value %.1f%n", total);
	}

	private static String trunc(String s, int n) {
		return s.length() <= n ? s : s.substring(0, n - 1) + "\u2026";
	}
}
