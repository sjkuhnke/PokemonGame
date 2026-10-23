package ai;

import java.util.Arrays;

import util.Rng;

/** Turns a raw strategy vector into a sampling distribution, then samples it. */
final class Shaper {
	private Shaper() {}

	/**
	 * Normalizes {@code x}, zeroes entries below {@code cfg.minProb} and renormalizes, then mixes in {@code cfg.epsilon} of
	 * the uniform distribution (a deliberate chance of a random pick; 0 by default). If every entry is below the cutoff, all
	 * mass goes to the largest entry of {@code x}.
	 */
	static double[] shape(double[] x, AIConfig cfg) {
		if (x.length == 0) return x;
		double[] out = normalize(x.clone());

		for (int i = 0; i < out.length; i++) if (out[i] < cfg.minProb) out[i] = 0;
		double sum = sum(out);
		if (sum <= 0) {
			// Everything fell under the cutoff: fall back to the argmax of x before the cutoff.
			int best = argmax(x);
			out = new double[x.length];
			out[best] = 1.0;
			return out;
		}
		out = normalize(out);

		if (cfg.epsilon > 0) {
			double u = 1.0 / out.length;
			for (int i = 0; i < out.length; i++) out[i] = (1 - cfg.epsilon) * out[i] + cfg.epsilon * u;
		}
		return out;
	}

	/** Draws an index from the distribution {@code x} with one {@code Rng} draw (the only randomness in a decision). */
	static int sample(double[] x) {
		double r = Rng.next();
		double c = 0;
		for (int i = 0; i < x.length; i++) {
			c += x[i];
			if (r <= c) return i;
		}
		return x.length - 1; // floating-point slop
	}

	private static double[] normalize(double[] a) {
		double s = sum(a);
		if (s <= 0) {
			double[] u = new double[a.length];
			Arrays.fill(u, 1.0 / a.length);
			return u;
		}
		double[] out = new double[a.length];
		for (int i = 0; i < a.length; i++) out[i] = a[i] / s;
		return out;
	}

	private static double sum(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return s;
	}

	private static int argmax(double[] a) {
		int best = 0;
		for (int i = 1; i < a.length; i++) if (a[i] > a[best]) best = i;
		return best;
	}
}