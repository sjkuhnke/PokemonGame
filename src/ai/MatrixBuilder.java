package ai;

import java.util.List;

import pokemon.*;
import util.Perf;

/**
 * Builds the payoff matrix of one decision: rows are the AI's actions, columns the player's, and each cell is the expected
 * evaluation of the position after both sides act, from the AI's point of view.
 * <p>
 * Every cell runs the full {@link BattleSimulator#simulateTurn} (the real move code, in a simulation scope), averages
 * {@link Evaluator#eval} over the resulting branches by probability, then applies the small adjustments in
 * {@link CellAdjustments} (switch tax, last-ditch priority bonus). Keeping the adjustments out of the loop leaves
 * {@code buildMatrix} as the plain "simulate, evaluate, store" pass.
 */
public final class MatrixBuilder {
	private MatrixBuilder() {}

	/** Payoff matrix {@code M[i][j]} for AI action {@code A[i]} against player action {@code P[j]}. */
	public static double[][] buildMatrix(SimState root, List<Action> A, List<Action> P, AIConfig cfg, MonWeights weights) {
		double[][] M = new double[A.size()][P.size()];
		CellAdjustments adjust = new CellAdjustments(root, P, cfg);
		for (int i = 0; i < A.size(); i++) {
			Action a = A.get(i);
			for (int j = 0; j < P.size(); j++) {
				long t0 = Perf.start();
				List<Branch> branches = BattleSimulator.simulateTurn(root, a, P.get(j), cfg);
				double v = 0;
				for (Branch br : branches) v += br.prob * Evaluator.eval(br.state, cfg.style, weights);
				M[i][j] = adjust.apply(v, a, j);
				Perf.stop(Perf.CELL, t0);
			}
		}
		return M;
	}

	/**
	 * Adjustments applied to a cell's evaluated score. Everything that depends only on the root position and the
	 * player's columns is computed once in the constructor, not per cell.
	 */
	private static final class CellAdjustments {
		private final SimState root;
		private final List<Action> P;
		private final AIConfig cfg;
		private final Pokemon aiMon, plMon;
		/** Whether plain switches pay {@link AIConfig#switchTax}: false while the foe threatens a KO on the AI's active. */
		private final boolean taxSwitches;
		/** Per player column: P(it kills the AI's active before the AI's normal-priority move lands); 0 = no last-ditch situation. */
		private final double[] ditchP;

		CellAdjustments(SimState root, List<Action> P, AIConfig cfg) {
			this.root = root;
			this.P = P;
			this.cfg = cfg;
			this.aiMon = root.ai.active();
			this.plMon = root.player.active();
			this.taxSwitches = !SackAnalysis.threatens(plMon, aiMon, root.field);
			this.ditchP = lastDitchColumns();
		}

		/** The cell's score {@code v} for AI action {@code a} against player column {@code j}, after both adjustments. */
		double apply(double v, Action a, int j) {
			v -= switchTax(a);
			v += lastDitchBonus(a, j);
			return v;
		}

		/** The flat cost of a plain switch. Pivot moves and sack rows' moves are not charged; only {@code SWITCH} rows. */
		private double switchTax(Action a) {
			return a.kind == ActionKind.SWITCH && taxSwitches ? cfg.switchTax : 0;
		}

		/** Columns where the player has a fast kill: an attack with a likely KO (at least 0.5) that outspeeds the AI's active. */
		private double[] lastDitchColumns() {
			double[] d = new double[P.size()];
			if (cfg.lastDitchBonus == 0) return d;
			for (int j = 0; j < d.length; j++) {
				Action p = P.get(j);
				if (p.kind != ActionKind.MOVE || p.move == null || p.move.cat == 2) continue;
				double ko = SackAnalysis.koChance(plMon, aiMon, p.move, root.field);
				if (ko < 0.5) continue;
				int pPrio = p.move.getPriority(plMon, root.field);
				if (plMon.getFaster(aiMon, pPrio, 0, root.field) != plMon) continue; // the AI moves first anyway: nothing to be "last ditch" about
				d[j] = ko;
			}
			return d;
		}

		/**
		 * Extra credit for a non-lethal priority move that really acts first in a column where the player has a fast
		 * kill: it gets its hit in before the AI's active goes down. A lethal priority move is already priced by the
		 * simulation, so it gets nothing here. Scales with the fraction of the foe's HP removed, so it stays a nudge
		 * inside the mixed strategy, never a forced pick.
		 */
		private double lastDitchBonus(Action a, int j) {
			if (ditchP[j] <= 0 || a.kind != ActionKind.MOVE || a.move == null) return 0;
			Action p = P.get(j);
			if (a.move.getPriority(aiMon, root.field) <= p.move.getPriority(plMon, root.field)) return 0; // this row does not act first
			try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
				DamageRange r = aiMon.calcRange(plMon, a.move, true, root.field);
				if (!r.usable || r.immune || !r.dealsDamage() || r.koProb(plMon.currentHP) >= 0.5) return 0;
				return cfg.lastDitchBonus * ditchP[j] * (r.expectedCapped(plMon.currentHP) / Math.max(1, plMon.currentHP));
			}
		}
	}
}