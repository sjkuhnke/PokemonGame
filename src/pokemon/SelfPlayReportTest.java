package pokemon;

/** Standalone checks for SelfPlayReport (no game needed): {@code java pokemon.SelfPlayReportTest}. */
public final class SelfPlayReportTest {
	private static int failed;

	private static void check(String name, boolean ok) {
		if (!ok) failed++;
		System.out.println((ok ? "PASS  " : "FAIL  ") + name);
	}

	public static void main(String[] args) {
		SelfPlayReport r = new SelfPlayReport("A", "B");
		for (int i = 0; i < 60; i++) r.addBattle(0, false, 10, 1_000_000, 5, 400_000, 2_000_000, 5, 900_000);
		for (int i = 0; i < 30; i++) r.addBattle(1, false, 14, 1_000_000, 7, 300_000, 2_000_000, 7, 800_000);
		for (int i = 0; i < 10; i++) r.addBattle(-1, true, 300, 1_000_000, 150, 300_000, 2_000_000, 150, 800_000);
		r.addCrash("boom");
		check("counts", r.battles == 100 && r.winsA == 60 && r.winsB == 30 && r.draws == 10 && r.timeouts == 10 && r.crashes == 1);
		check("score counts a draw as half (0.65)", Math.abs(r.scoreA() - 0.65) < 1e-9);
		double[] ci = r.wilson95();
		check("wilson 95% CI brackets the score and is sane", ci[0] < 0.65 && ci[1] > 0.65 && ci[0] > 0.54 && ci[1] < 0.75);
		check("max think tracks the largest single decision", r.maxNanosA == 400_000 && r.maxNanosB == 900_000);
		check("avg ms per decision", Math.abs(SelfPlayReport.avgMs(r.nanosA, r.decisionsA) - 100.0 / 2010) < 1e-9);
		check("empty report is NaN, not a crash", Double.isNaN(new SelfPlayReport("A", "B").scoreA()));
		System.out.println(r);
		System.out.println(failed == 0 ? "ALL PASS" : failed + " FAILED");
		if (failed != 0) System.exit(1);
	}
}