package test;

import java.util.Arrays;
import java.util.List;

import pokemon.Field;
import pokemon.MonWeights;
import pokemon.Moveslot;
import pokemon.Perf;
import pokemon.Pokemon;
import pokemon.SelfPlay;
import pokemon.SimCache;
import pokemon.SimState;
import pokemon.Trainer;

/**
 * Phase 8 tests (performance half). In-game, game thread, Trainer.trainers populated, like Phase 5-7.
 * runAll(): cloneFidelity, cacheTransparency, selfPlayVerify(6). timing(n) is a profile, not a pass/fail test.
 */
public final class Phase8Tests {
	private Phase8Tests() {}

	public static void runAll() {
		run("cloneFidelity", Phase8Tests::cloneFidelity);
		run("cacheTransparency", Phase8Tests::cacheTransparency);
		run("selfPlayVerify", () -> selfPlayVerify(6));
	}

	private static void run(String name, Runnable r) {
		try {
			r.run();
			System.out.println("Phase8 " + name + " passed.");
		} catch (Throwable t) {
			System.out.println("Phase8 " + name + " FAILED: " + t);
			t.printStackTrace(System.out);
		}
	}

	private static void check(boolean ok, String msg) {
		if (!ok) throw new AssertionError(msg);
	}

	/** The light clone() constructor must copy exactly what the old (sprite-loading) path produced, and be much cheaper. */
	static void cloneFidelity() {
		List<Trainer> pool = SelfPlay.buildPool();
		check(!pool.isEmpty(), "no trainers");
		Pokemon sample = null;
		for (Pokemon p : pool.get(0).team) {
			if (p == null) continue;
			sample = p;
			Pokemon c = p.clone();
			check(c != p && c.cloned && c.spriteVisible, "clone flags");
			check(c.id == p.id && c.level == p.level && c.currentHP == p.currentHP && c.status == p.status, "basic fields " + p);
			check(c.item == p.item && c.ability == p.ability && c.type1 == p.type1 && c.type2 == p.type2, "item/ability/types " + p);
			check(c.trainer == p.trainer, "trainer ref");
			for (int i = 0; i < 6; i++) check(c.getStat(i) == p.getStat(i), "stat " + i);
			check(c.statStages != p.statStages && Arrays.equals(c.statStages, p.statStages), "stages");
			check(c.vStatuses.size() == p.vStatuses.size(), "vStatuses");
			check(c.moveset != p.moveset && c.moveset.length == p.moveset.length, "moveset array");
			for (int i = 0; i < p.moveset.length; i++) {
				Moveslot a = p.moveset[i], b = c.moveset[i];
				check((a == null) == (b == null), "moveslot null " + i);
				if (a != null) check(a != b && a.move == b.move && a.currentPP == b.currentPP, "moveslot " + i);
			}
		}
		check(sample != null, "empty team");
		int n = 2000;
		long t0 = System.nanoTime();
		for (int i = 0; i < n; i++) sample.clone();
		double us = (System.nanoTime() - t0) / 1e3 / n;
		System.out.printf("Phase8: Pokemon.clone() = %.1f us each%n", us);
		check(us < 500, "clone() is still slow (" + us + " us): is the sprite-loading constructor still in the path?");
	}

	/** Cold and warm caches must give identical weights, and VERIFY must find nothing. */
	static void cacheTransparency() {
		List<Trainer> pool = SelfPlay.buildPool();
		check(pool.size() >= 2, "need 2 trainers");
		Field prev = Pokemon.field;
		boolean prevVerify = SimCache.VERIFY;
		try {
			Trainer a = pool.get(0).clone(), b = pool.get(1).clone();
			if (a.boosts == null) a.boosts = new int[3];
			if (b.boosts == null) b.boosts = new int[3];
			Pokemon.field = new Field();
			Pokemon.field.clear(a, b);
			SimState s = SimState.snapshot(a.getCurrent(), b.getCurrent());

			SimCache.clear();
			MonWeights cold = MonWeights.compute(s);
			MonWeights warm = MonWeights.compute(s);
			check(Arrays.equals(cold.ai, warm.ai) && Arrays.equals(cold.player, warm.player), "warm != cold");

			SimCache.clear();
			SimCache.resetVerify();
			SimCache.VERIFY = true;
			MonWeights.compute(s);
			MonWeights.compute(s); // second call hits and is recomputed + compared
			check(SimCache.verifyMismatches == 0, "edge cache mismatches: " + SimCache.verifyMismatches);
			check(SimCache.verifyChecked > 0, "VERIFY never compared anything");
		} finally {
			SimCache.VERIFY = prevVerify;
			Pokemon.field = prev;
		}
	}

	/** Plays a few seeded battles with every cache hit re-verified; also reports the self-play determinism check. */
	public static void selfPlayVerify(int battles) {
		SimCache.clear();
		SimCache.resetVerify();
		boolean prev = SimCache.VERIFY;
		SimCache.VERIFY = true;
		try {
			SelfPlay.Config c = new SelfPlay.Config();
			c.battles = battles;
			c.teams = SelfPlay.randomCompetitive(3);
			c.determinismSample = 2;
			System.out.println(SelfPlay.run(c));
		} finally {
			SimCache.VERIFY = prev;
		}
		System.out.println("Phase8: cache VERIFY compared " + SimCache.verifyChecked + " hits, mismatches " + SimCache.verifyMismatches);
		check(SimCache.verifyChecked > 0, "VERIFY never compared anything");
		check(SimCache.verifyMismatches == 0, "cache mismatches: " + SimCache.verifyMismatches + " (see System.err; a key is missing an input)");
	}

	/** Profile, not a pass/fail test: n self-play battles with Perf on. Read the per-decision times in the report and the Perf rows. */
	public static void timing(int battles) {
		SimCache.clear();
		Perf.reset();
		Perf.ON = true;
		try {
			SelfPlay.Config c = new SelfPlay.Config();
			c.battles = battles;
			c.teams = SelfPlay.randomCompetitive(6);
			c.determinismSample = 0;
			System.out.println(SelfPlay.run(c));
			System.out.println(Perf.report());
		} finally {
			Perf.ON = false;
		}
	}
}