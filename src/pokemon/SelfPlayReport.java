package pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Accumulates self-play results for two engines (A vs B) and prints a summary. No game dependencies. */
public final class SelfPlayReport {
	public final String nameA;
	public final String nameB;

	public int battles;          // battles that finished without an exception
	public int winsA, winsB;
	public int draws;            // both wiped, or timed out
	public int timeouts;         // subset of draws
	public int crashes;
	public long turns;
	public long decisionsA, decisionsB;
	public long nanosA, nanosB, maxNanosA, maxNanosB;
	public int determinismChecked, determinismMismatches;
	public boolean stoppedEarly;
	public long wallNanos;
	public final List<String> errors = new ArrayList<>();

	public SelfPlayReport(String nameA, String nameB) {
		this.nameA = nameA;
		this.nameB = nameB;
	}

	/** @param winner 0 = A won, 1 = B won, -1 = draw/timeout */
	public void addBattle(int winner, boolean timedOut, int battleTurns,
			long nA, int dA, long maxA, long nB, int dB, long maxB) {
		battles++;
		if (winner == 0) winsA++;
		else if (winner == 1) winsB++;
		else draws++;
		if (timedOut) timeouts++;
		turns += battleTurns;
		nanosA += nA; decisionsA += dA; maxNanosA = Math.max(maxNanosA, maxA);
		nanosB += nB; decisionsB += dB; maxNanosB = Math.max(maxNanosB, maxB);
	}

	public void addCrash(String description) {
		crashes++;
		if (errors.size() < 5) errors.add(description);
	}

	/** Score for A: win = 1, draw = 0.5. NaN if no battles finished. */
	public double scoreA() {
		return battles == 0 ? Double.NaN : (winsA + 0.5 * draws) / battles;
	}

	/** 95% Wilson interval for {@link #scoreA()} (draws counted as half a win; approximate). */
	public double[] wilson95() {
		if (battles == 0) return new double[] { Double.NaN, Double.NaN };
		double n = battles, p = scoreA(), z = 1.96, z2 = z * z;
		double denom = 1 + z2 / n;
		double centre = (p + z2 / (2 * n)) / denom;
		double half = z * Math.sqrt(p * (1 - p) / n + z2 / (4 * n * n)) / denom;
		return new double[] { Math.max(0, centre - half), Math.min(1, centre + half) };
	}

	public static double avgMs(long nanos, long decisions) {
		return decisions == 0 ? 0 : nanos / 1e6 / decisions;
	}

	public String progressLine(int done, int total) {
		return String.format(Locale.ROOT, "[self-play] %d/%d  %s %d - %d %s  draws %d  crashes %d",
				done, total, nameA, winsA, winsB, nameB, draws, crashes);
	}

	@Override
	public String toString() {
		double[] ci = wilson95();
		StringBuilder sb = new StringBuilder();
		sb.append("==================== SELF-PLAY REPORT ====================\n");
		sb.append(String.format(Locale.ROOT, "%s vs %s%s\n", nameA, nameB, stoppedEarly ? "  (stopped early: time limit)" : ""));
		sb.append(String.format(Locale.ROOT, "battles finished: %d   crashes: %d   wall time: %.1fs\n", battles, crashes, wallNanos / 1e9));
		sb.append(String.format(Locale.ROOT, "%s wins: %d   %s wins: %d   draws: %d (timeouts: %d)\n", nameA, winsA, nameB, winsB, draws, timeouts));
		sb.append(String.format(Locale.ROOT, "%s score: %.1f%%  (95%% CI %.1f%% - %.1f%%)\n", nameA, scoreA() * 100, ci[0] * 100, ci[1] * 100));
		sb.append(String.format(Locale.ROOT, "avg turns/battle: %.1f\n", battles == 0 ? 0.0 : turns * 1.0 / battles));
		sb.append(String.format(Locale.ROOT, "think time/decision  %s: avg %.1f ms, max %.1f ms   %s: avg %.1f ms, max %.1f ms\n",
				nameA, avgMs(nanosA, decisionsA), maxNanosA / 1e6, nameB, avgMs(nanosB, decisionsB), maxNanosB / 1e6));
		if (determinismChecked > 0) {
			sb.append(String.format(Locale.ROOT, "determinism: %d/%d replayed battles identical%s\n",
					determinismChecked - determinismMismatches, determinismChecked,
					determinismMismatches > 0 ? "  <-- NOT reproducible from seed" : ""));
		}
		for (String e : errors) sb.append("crash: ").append(e).append('\n');
		sb.append("==========================================================");
		return sb.toString();
	}
}