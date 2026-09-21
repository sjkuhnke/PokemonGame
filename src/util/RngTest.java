package util;

import java.util.Random;

/** Standalone checks for Rng (no game needed): {@code java util.RngTest}. */
public final class RngTest {
	private static int failed;

	private static void check(String name, boolean ok) {
		if (!ok) failed++;
		System.out.println((ok ? "PASS  " : "FAIL  ") + name);
	}

	public static void main(String[] args) {
		Rng.setSeed(123);
		double[] a = new double[5];
		for (int i = 0; i < a.length; i++) a[i] = Rng.next();
		Rng.setSeed(123);
		boolean same = true;
		for (double v : a) same &= v == Rng.next();
		check("same seed replays the same sequence", same);

		Rng.setSeed(7);
		double x = Rng.next();
		Rng.setSeed(7);
		double y = Rng.asRandom().nextDouble();
		check("asRandom() shares the Rng stream", x == y);

		Random cached = Rng.asRandom();
		Rng.setSeed(9);
		double c1 = cached.nextDouble();
		Rng.setSeed(9);
		check("a cached asRandom() reference follows reseeding", c1 == Rng.next());

		Rng.setSeed(5);
		int hits = 0, n = 200000;
		for (int i = 0; i < n; i++) if (Rng.chance(0.5)) hits++;
		check("chance(0.5) ~ 50% (" + hits * 100.0 / n + "%)", Math.abs(hits / (double) n - 0.5) < 0.01);

		Rng.setSeed(5);
		boolean never = true, always = true;
		for (int i = 0; i < 10000; i++) { never &= !Rng.chance(0); always &= Rng.chance(1); }
		check("chance(0) never, chance(1) always", never && always);

		Rng.setSeed(11);
		boolean inRange = true;
		for (int i = 0; i < 10000; i++) { int v = Rng.nextInt(7); inRange &= v >= 0 && v < 7; double d = Rng.next(); inRange &= d >= 0 && d < 1; }
		check("nextInt(n) in [0,n) and next() in [0,1)", inRange);

		Rng.setSeed(1);
		Rng.randomize();
		check("randomize() clears the seeded flag", !Rng.isSeeded());
		Rng.setSeed(2);
		check("setSeed() sets the seeded flag and records the seed", Rng.isSeeded() && Rng.getSeed() == 2);

		System.out.println(failed == 0 ? "ALL PASS" : failed + " FAILED");
		if (failed != 0) System.exit(1);
	}
}