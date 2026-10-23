package ai;

import java.util.Arrays;

/**
 * Regret matching for a zero-sum matrix game (the player's payoff is the negative of the AI's). No LP dependency, and
 * cheap enough for matrices of about 12 x 9. The result is the AVERAGE strategy over the iterations, which converges to an
 * equilibrium but is only approximate: it can leave small probabilities on rows that are never better (see
 * {@code AIV2.dominatedRows}), and {@code LeadSelection.gameValue} brackets the value instead of trusting it.
 */
public final class Solver {
	private static final int DEFAULT_ITERS = 500;

	private Solver() {}

	/** Row strategy {@code x} (the AI) and column strategy {@code y} (the player); each sums to 1. */
	public static final class Result {
		public final double[] x;
		public final double[] y;

		Result(double[] x, double[] y) {
			this.x = x;
			this.y = y;
		}
	}

	/** Solves with the default 500 iterations. */
	public static Result solveZeroSum(double[][] M) {
		return solveZeroSum(M, DEFAULT_ITERS);
	}

	/** {@code M[i][j]} is the AI's payoff for row i against column j. An empty matrix returns empty strategies. */
	public static Result solveZeroSum(double[][] M, int iters) {
		int n = M.length;
		int m = n == 0 ? 0 : M[0].length;
		if (n == 0 || m == 0) return new Result(new double[n], new double[m]);

		double[] Rr = new double[n], Rc = new double[m];
		double[] Sx = new double[n], Sy = new double[m];

		for (int t = 0; t < iters; t++) {
			double[] x = positivePartNormalized(Rr);
			double[] y = positivePartNormalized(Rc);

			double[] rowPay = new double[n]; // M . y : AI payoff of each pure row vs y
			for (int i = 0; i < n; i++) {
				double s = 0;
				for (int j = 0; j < m; j++) s += M[i][j] * y[j];
				rowPay[i] = s;
			}
			double xDotRowPay = 0;
			for (int i = 0; i < n; i++) xDotRowPay += x[i] * rowPay[i];

			double[] colPay = new double[m]; // -(x^T . M) : player's payoff of each pure column vs x
			for (int j = 0; j < m; j++) {
				double s = 0;
				for (int i = 0; i < n; i++) s += x[i] * M[i][j];
				colPay[j] = -s;
			}
			double yDotColPay = 0;
			for (int j = 0; j < m; j++) yDotColPay += y[j] * colPay[j];

			for (int i = 0; i < n; i++) Rr[i] += rowPay[i] - xDotRowPay;
			for (int j = 0; j < m; j++) Rc[j] += colPay[j] - yDotColPay;
			for (int i = 0; i < n; i++) Sx[i] += x[i];
			for (int j = 0; j < m; j++) Sy[j] += y[j];
		}

		double[] x = new double[n], y = new double[m];
		for (int i = 0; i < n; i++) x[i] = Sx[i] / iters;
		for (int j = 0; j < m; j++) y[j] = Sy[j] / iters;
		return new Result(x, y);
	}

	/** Regret-matching strategy: the positive part of the regrets, normalized (uniform when none is positive). */
	private static double[] positivePartNormalized(double[] R) {
		int n = R.length;
		double[] out = new double[n];
		double sum = 0;
		for (int i = 0; i < n; i++) {
			out[i] = Math.max(0, R[i]);
			sum += out[i];
		}
		if (sum <= 0) {
			Arrays.fill(out, 1.0 / n);
		} else {
			for (int i = 0; i < n; i++) out[i] /= sum;
		}
		return out;
	}
}