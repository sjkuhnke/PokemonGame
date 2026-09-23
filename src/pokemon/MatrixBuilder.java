package pokemon;

import java.util.List;

/**
 * §7.9. Correctness-first this phase: every cell runs full {@link BattleSimulator#simulateTurn}
 * (Tier 2 semantics - simulateTurn already calls the real move()/moveInit() in a sim scope, so
 * there is no separate "Tier 2 clone path" to add). Tier 0 pruning, the Tier 1 calcRange-only
 * fast path, and switch-in/damage-range memoization are explicitly deferred to Phase 8 profiling
 * work (§9, §12 Phase 8) - correct-but-slow now, tuned later, per §0's "correctness first."
 */
final class MatrixBuilder {
	private MatrixBuilder() {}

	static double[][] buildMatrix(SimState root, List<Action> A, List<Action> P, AIConfig cfg, MonWeights weights) {
		double[][] M = new double[A.size()][P.size()];
		for (int i = 0; i < A.size(); i++) {
			Action a = A.get(i);
			for (int j = 0; j < P.size(); j++) {
				Action p = P.get(j);
				List<Branch> branches = BattleSimulator.simulateTurn(root, a, p, cfg);
				double v = 0;
				for (Branch br : branches) v += br.prob * Evaluator.eval(br.state, cfg.style, weights.ai, weights.player);
				M[i][j] = v;
			}
		}
		return M;
	}
}