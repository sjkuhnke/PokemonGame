package pokemon;

import java.util.Locale;

/**
 * Phase 8 (§10): zero-dependency decision profiler. Off by default ({@code Perf.ON = false}); when off every hook is one
 * boolean test. Sections are INCLUSIVE and nest (simulateTurn contains calcRange, pickSlot contains monWeights.forSide, ...),
 * so do not add the rows up. Game thread only.
 *
 * Usage: Perf.reset(); Perf.ON = true; ...play...; System.out.println(Perf.report()); Perf.ON = false;
 */
public final class Perf {
	private Perf() {}

	public static boolean ON = false;

	public static final int CLONE = 0, CALC_RANGE = 1, SIM_TURN = 2, CELL = 3, W_COMPUTE = 4, W_FORSIDE = 5, PICK = 6, EVAL = 7,
			ENTRY = 8, EDGE_HIT = 9, EDGE_MISS = 10, RANGE_HIT = 11, RANGE_MISS = 12;

	private static final String[] NAMES = { "Pokemon.clone (count)", "calcRange (count)", "simulateTurn", "matrix cell", "MonWeights.compute",
			"MonWeights.forSide", "ReplacementChooser.pickSlot", "Evaluator.eval", "lead simulateEntry", "edge cache hit (count)",
			"edge cache miss (count)", "range cache hit (count)", "range cache miss (count)" };

	private static final long[] calls = new long[NAMES.length];
	private static final long[] nanos = new long[NAMES.length];

	/** Returns 0 when profiling is off, so {@link #stop} is a no-op. */
	public static long start() {
		return ON ? System.nanoTime() : 0L;
	}

	public static void stop(int id, long t0) {
		if (!ON || t0 == 0L) return;
		calls[id]++;
		nanos[id] += System.nanoTime() - t0;
	}

	public static void count(int id) {
		if (ON) calls[id]++;
	}

	public static void reset() {
		java.util.Arrays.fill(calls, 0);
		java.util.Arrays.fill(nanos, 0);
	}

	public static String report() {
		StringBuilder sb = new StringBuilder("\n===== Perf (inclusive; sections nest) =====\n");
		for (int i = 0; i < NAMES.length; i++) {
			if (calls[i] == 0) continue;
			if (nanos[i] == 0) {
				sb.append(String.format(Locale.ROOT, "%-30s calls=%d%n", NAMES[i], calls[i]));
			} else {
				sb.append(String.format(Locale.ROOT, "%-30s calls=%-8d total=%9.1f ms   avg=%9.1f us%n", NAMES[i], calls[i],
						nanos[i] / 1e6, nanos[i] / 1e3 / calls[i]));
			}
		}
		long eh = calls[EDGE_HIT], em = calls[EDGE_MISS], rh = calls[RANGE_HIT], rm = calls[RANGE_MISS];
		if (eh + em > 0) sb.append(String.format(Locale.ROOT, "edge cache hit rate  %.1f%%%n", 100.0 * eh / (eh + em)));
		if (rh + rm > 0) sb.append(String.format(Locale.ROOT, "range cache hit rate %.1f%%%n", 100.0 * rh / (rh + rm)));
		return sb.toString();
	}
}