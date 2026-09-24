package pokemon;

import java.util.Arrays;

/**
 * §7.10. Regret matching for a zero-sum bimatrix game (player's payoff = -AI's payoff). No LP
 * dependency, cheap enough for matrices bounded at ~12x9 (§10 size budget). The smooth/"human"
 * quantal-response variant (temperature-weighted softmax instead of positive-part
 * normalization) is part of §7.11's player-model exploitation, which Phase 3 doesn't wire up
 * yet (predict() just returns the raw equilibrium y this phase) - so only the plain variant is
 * implemented here.
 */
public final class Solver {
	private static final int DEFAULT_ITERS = 500;

	private Solver() {}

	public static final class Result {
		public final double[] x;
		public final double[] y;

		Result(double[] x, double[] y) {
			this.x = x;
			this.y = y;
		}
	}

	public static Result solveZeroSum(double[][] M) {
		return solveZeroSum(M, DEFAULT_ITERS);
	}

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