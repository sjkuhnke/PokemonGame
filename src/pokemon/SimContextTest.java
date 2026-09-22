package pokemon;

import util.Rng;

/** Standalone checks for SimContext, SimPolicy, RollMode, DamageMode and DamageRange (no game needed): {@code java pokemon.SimContextTest}. */
public final class SimContextTest {
	private static int failed;

	private static void check(String name, boolean ok) {
		if (!ok) failed++;
		System.out.println((ok ? "PASS  " : "FAIL  ") + name);
	}

	private static boolean near(double a, double b) {
		return Math.abs(a - b) < 1e-9;
	}

	public static void main(String[] args) {
		// ---- scope mechanics
		check("inactive by default", !SimContext.active() && SimContext.depth() == 0 && SimContext.policy() == SimPolicy.DEFAULT);
		SimPolicy p1 = SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.MISS);
		SimPolicy p2 = SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.HIT);
		try (SimContext.Scope a = SimContext.enter(p1)) {
			check("scope makes it active and installs the policy", SimContext.active() && SimContext.policy() == p1 && Rng.isIsolated());
			try (SimContext.Scope b = SimContext.enter(p2)) {
				check("nested scope: depth 2, inner policy", SimContext.depth() == 2 && SimContext.policy() == p2);
			}
			check("closing the inner scope restores the outer policy", SimContext.depth() == 1 && SimContext.policy() == p1 && Rng.isIsolated());
		}
		check("closing the outermost scope deactivates", !SimContext.active() && !Rng.isIsolated() && SimContext.policy() == SimPolicy.DEFAULT);

		SimContext.Scope outer = SimContext.enter();
		SimContext.Scope inner = SimContext.enter();
		boolean threw = false;
		try {
			outer.close();
		} catch (IllegalStateException e) {
			threw = true;
		}
		inner.close();
		outer.close();
		outer.close(); // idempotent
		check("out-of-order close is rejected, then clean shutdown", threw && !SimContext.active());

		boolean thrown = false;
		try {
			SimContext.run(SimPolicy.DEFAULT, () -> { throw new RuntimeException("boom"); });
		} catch (RuntimeException e) {
			thrown = true;
		}
		check("scope closes when the body throws", thrown && !SimContext.active() && !Rng.isIsolated());

		// ---- randomness isolation
		Rng.setSeed(77);
		double control = Rng.next();
		Rng.setSeed(77);
		double x1, x2;
		try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withSeed(5))) {
			x1 = Rng.next() + Rng.nextInt(1000);
		}
		check("simulating does not consume the real stream", Rng.next() == control);
		try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withSeed(5))) {
			x2 = Rng.next() + Rng.nextInt(1000);
		}
		check("same policy seed replays the same simulated draws", x1 == x2);
		Rng.randomize();

		// ---- policy rules
		SimPolicy d = SimPolicy.DEFAULT;
		check("accuracy MAJORITY: >=50 hits, <50 misses, 100 always hits, 0 never", d.accuracyHits(50) && !d.accuracyHits(49.9) && d.accuracyHits(100) && !d.accuracyHits(0));
		check("accuracy HIT / MISS force any move that can miss, but never a sure hit", d.withAccuracy(SimPolicy.Accuracy.HIT).accuracyHits(10)
				&& !d.withAccuracy(SimPolicy.Accuracy.MISS).accuracyHits(90) && d.withAccuracy(SimPolicy.Accuracy.MISS).accuracyHits(100));
		check("secondary MAJORITY: 30 no, 60 yes, 100 always", !d.secondaryProcs(30) && d.secondaryProcs(60) && d.secondaryProcs(100));
		check("secondary PROC / NO_PROC never override a certain effect", d.withSecondary(SimPolicy.Secondary.NO_PROC).secondaryProcs(100)
				&& d.withSecondary(SimPolicy.Secondary.PROC).secondaryProcs(10) && !d.withSecondary(SimPolicy.Secondary.NO_PROC).secondaryProcs(60));
		check("multi-hit count: expected value, .5 rounds down", d.multiHitCount(3.1) == 3 && d.multiHitCount(4.5) == 4 && d.multiHitCount(2.0) == 2 && d.multiHitCount(1.0) == 1);

		// ---- rolls and modes
		boolean rollsOk = near(RollMode.roll(0), 0.85) && near(RollMode.roll(15), 1.0);
		double sum = 0;
		for (int i = 0; i < 16; i++) sum += RollMode.roll(i);
		check("16 rolls run 0.85..1.00 and average 0.925 (RollMode.AVG)", rollsOk && near(sum / 16, RollMode.AVG.multiplier));
		DamageMode m0 = DamageMode.legacy(0, false, true), m1 = DamageMode.legacy(1, true, true), mm = DamageMode.legacy(-1, false, false);
		check("legacy mode 0 = engine rules, samples, may roll accuracy", m0.engine && m0.random() && m0.checkAcc && m0.hitsInBp && m0.capAtHp && !m0.predictOneShots);
		check("legacy modes +-1 = estimate rules, no accuracy roll", !m1.engine && !m1.random() && !m1.checkAcc && m1.predictOneShots && m1.rollMode == RollMode.MAX && mm.rollMode == RollMode.MIN);
		DamageMode det = DamageMode.det(0.9, DamageMode.CRIT_NEVER, DamageMode.CHROMO_NO);
		check("det mode = engine rules without any sampling", det.engine && !det.random() && !det.checkAcc && !det.isLegacy() && !det.hitsInBp && !det.capAtHp && det.forReflect().checkAcc == false);
		check("reflect branch rolls accuracy only for the sampling mode", m0.forReflect().checkAcc && !m1.forReflect().checkAcc);

		// ---- DamageRange
		// Two crit-free outcomes 40 / 60 (50% each); one hit.
		DamageRange r = DamageRange.of(new double[] { 60, 40 }, new double[] { 0.5, 0.5 }, 40, 50, 60, 0, 0.9, new double[] { 0, 1 }, 100, false, false, false);
		check("koProb steps through the outcome distribution", near(r.koProb(40), 1.0) && near(r.koProb(41), 0.5) && near(r.koProb(60), 0.5) && near(r.koProb(61), 0.0));
		check("expected = accuracy x hits x mean", near(r.expected(), 0.9 * 50) && near(r.expectedPerHit(), 50) && near(r.hits, 1));
		DamageRange sturdy = DamageRange.of(new double[] { 200 }, new double[] { 1 }, 200, 200, 200, 0, 1, new double[] { 0, 1 }, 100, true, false, false);
		check("Sturdy / Focus Sash: no KO from full HP, KO below it", near(sturdy.koProb(100), 0) && near(sturdy.koProb(99), 1));
		DamageRange swipe = DamageRange.of(new double[] { 200 }, new double[] { 1 }, 200, 200, 200, 0, 1, new double[] { 0, 1 }, 100, false, true, false);
		check("False Swipe never KOs", near(swipe.koProb(1), 0));
		// Two-hit move, 30 per hit: kills 55 HP (2 hits) but not 70.
		DamageRange two = DamageRange.of(new double[] { 30 }, new double[] { 1 }, 30, 30, 30, 0, 1, new double[] { 0, 0, 1 }, 100, false, false, false);
		check("multi-hit: hits and koProb use the hit count", near(two.hits, 2) && near(two.koProb(55), 1) && near(two.koProb(70), 0) && near(two.expected(), 60));
		DamageRange mix = DamageRange.of(new double[] { 10, 20 }, new double[] { 0.5, 0.5 }, 10, 10, 10, 0.5, 1, new double[] { 0, 0.5, 0.5 }, 100, false, false, false);
		check("hit-count distribution mixes koProb (1 hit w.p. .5, 2 hits w.p. .5)", near(mix.koProb(20), 0.5 * 0.5 + 0.5 * 1.0));
		check("flags: unusable / immune / noDamage / reflected", !DamageRange.unusable().usable && DamageRange.immune().immune && DamageRange.immune().usable
				&& DamageRange.noDamage(0.9).usable && !DamageRange.noDamage(0.9).dealsDamage() && near(DamageRange.noDamage(0.9).accuracy, 0.9)
				&& near(DamageRange.unusable().koProb(1), 0) && near(DamageRange.immune().koProb(1), 0));
		DamageRange refl = DamageRange.of(new double[] { 50 }, new double[] { 1 }, 50, 50, 50, 0, 1, new double[] { 0, 1 }, 100, false, false, true);
		check("reflected damage never KOs the target", refl.reflected && near(refl.koProb(1), 0) && !refl.dealsDamage());

		check("expectedCapped caps at HP, and at HP-1 against Sturdy / False Swipe",
				near(r.expectedCapped(45), 0.9 * 42.5) && near(r.expectedCapped(1000), r.expected()) && near(sturdy.expectedCapped(100), 99) && near(sturdy.expectedCapped(50), 50)
				&& near(swipe.expectedCapped(30), 29) && near(two.expectedCapped(50), 50) && near(two.expectedCapped(100), 60));
		System.out.println(failed == 0 ? "ALL PASS" : failed + " FAILED");
		if (failed != 0) System.exit(1);
	}
}
