package test;

import java.util.Arrays;
import java.util.List;

import pokemon.AIV2;
import pokemon.Field;
import pokemon.MonWeights;
import pokemon.Moveslot;
import pokemon.Perf;
import pokemon.Player;
import pokemon.PlayerModel;
import pokemon.Pokemon;
import pokemon.SelfPlay;
import pokemon.SimCache;
import pokemon.SimState;
import pokemon.Trainer;
import pokemon.Tunables;

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
	
	public static void switchRates(int battles, boolean persistModels) {
		boolean prevSup = util.Print.isDebugSuppressed();
		boolean wasSeeded = util.Rng.isSeeded();
		long prevSeed = util.Rng.getSeed();
		util.Print.setDebugSuppressed(true);
		try {
			SelfPlay.TeamSource teams = SelfPlay.randomCompetitive(6);
			PlayerModel aboutHard = new PlayerModel(), aboutNormal = new PlayerModel();
			double score = 0; int n = 0, hardSwaps = 0, normalSwaps = 0, hardPivots = 0, normalPivots = 0;
			for (int pair = 0; n < battles; pair++) {
				util.Rng.setSeed(4242L + 1_000_003L * pair);
				Trainer[] m = teams.next(pair, new java.util.Random(4242L + pair));
				for (int seat = 0; seat < 2; seat++) {
					boolean hardFirst = seat == 0;
					int d1 = hardFirst ? Player.HARD : Player.NORMAL, d2 = hardFirst ? Player.NORMAL : Player.HARD;
					PlayerModel on1 = persistModels ? (hardFirst ? aboutHard : aboutNormal) : null;
					PlayerModel on2 = persistModels ? (hardFirst ? aboutNormal : aboutHard) : null;
					SelfPlay.BattleResult r = SelfPlay.playBattle(m[0], m[1], AIV2.INSTANCE, AIV2.INSTANCE, d1, d2, 9000L + 7919L * n, 300, on1, on2);
					if (r.error != null) continue;
					n++;
					score += r.winner < 0 ? 0.5 : ((r.winner == 0) == hardFirst ? 1 : 0);
					hardSwaps += hardFirst ? r.swaps1 : r.swaps2;     normalSwaps += hardFirst ? r.swaps2 : r.swaps1;
					hardPivots += hardFirst ? r.pivots1 : r.pivots2;  normalPivots += hardFirst ? r.pivots2 : r.pivots1;
				}
			}
			System.out.printf("HARD score %.1f%% over %d | per battle: HARD swaps %.2f pivots %.2f, NORMAL swaps %.2f pivots %.2f (persistModels=%b)%n",
					100 * score / n, n, hardSwaps / (double) n, hardPivots / (double) n, normalSwaps / (double) n, normalPivots / (double) n, persistModels);
		} finally {
			util.Print.setDebugSuppressed(prevSup);
			if (wasSeeded) util.Rng.setSeed(prevSeed); else util.Rng.randomize();
		}
	}
	
	public static void taxMechanism(int battles, double... taxes) {
		int idx = -1;
		for (int i = 0; i < Tunables.all().size(); i++) if (Tunables.all().get(i).name.equals("AIConfig.D_SWITCH_TAX")) idx = i;
		if (idx < 0) throw new IllegalStateException("D_SWITCH_TAX is not registered in Tunables");
		boolean prevSup = util.Print.isDebugSuppressed();
		boolean wasSeeded = util.Rng.isSeeded();
		long prevSeed = util.Rng.getSeed();
		util.Print.setDebugSuppressed(true);
		try {
			for (double tax : taxes) {
				double[] taxed = Tunables.defaults();
				taxed[idx] = tax;
				SelfPlay.TeamSource teams = SelfPlay.randomCompetitive(6);
				double score = 0;
				int n = 0, swT = 0, swU = 0, pvT = 0, pvU = 0, turns = 0;
				for (int pair = 0; n < battles; pair++) {
					util.Rng.setSeed(6161L + 1_000_003L * pair);
					Trainer[] m = teams.next(pair, new java.util.Random(6161L + pair));
					for (int seat = 0; seat < 2; seat++) {
						boolean taxedFirst = seat == 0;
						SelfPlay.BattleResult r = SelfPlay.playBattle(m[0], m[1], AIV2.INSTANCE, AIV2.INSTANCE, Player.HARD, Player.HARD,
								12000L + 7919L * n, 300, null, null, taxedFirst ? taxed : null, taxedFirst ? null : taxed);
						if (r.error != null) continue;
						n++;
						turns += r.turns;
						score += r.winner < 0 ? 0.5 : ((r.winner == 0) == taxedFirst ? 1 : 0);
						swT += taxedFirst ? r.swaps1 : r.swaps2;
						swU += taxedFirst ? r.swaps2 : r.swaps1;
						pvT += taxedFirst ? r.pivots1 : r.pivots2;
						pvU += taxedFirst ? r.pivots2 : r.pivots1;
					}
				}
				System.out.printf("tax %.0f vs 0: score %.1f%% over %d | per battle: taxed swaps %.2f pivots %.2f | untaxed swaps %.2f pivots %.2f | turns %.1f%n",
						tax, 100 * score / n, n, swT / (double) n, pvT / (double) n, swU / (double) n, pvU / (double) n, turns / (double) n);
			}
		} finally {
			util.Print.setDebugSuppressed(prevSup);
			if (wasSeeded) util.Rng.setSeed(prevSeed); else util.Rng.randomize();
		}
	}
	
	public static double[] withTax(double tax) {
		double[] v = Tunables.defaults();
		for (int i = 0; i < Tunables.all().size(); i++) {
			if (Tunables.all().get(i).name.equals("AIConfig.D_SWITCH_TAX")) v[i] = tax;
		}
		return v;
	}
	
	public static void replayStory(long baseSeed, int battleIndex, double[] paramsA, double[] paramsB, int difficulty) {
		boolean prevSup = util.Print.isDebugSuppressed();
		util.Print.setDebugSuppressed(true);
		try {
			SelfPlay.TeamSource src = SelfPlay.storyTrainers();
			java.util.Random sched = new java.util.Random(baseSeed);
			int pair = battleIndex / 2;
			boolean swapped = battleIndex % 2 == 1;
			Trainer[] m = null;
			for (int p = 0; p <= pair; p++) { util.Rng.setSeed(baseSeed + 1_000_003L * p); m = src.next(p, sched); }
			long seed = baseSeed + 7919L * battleIndex;
			SelfPlay.BattleResult r = SelfPlay.playBattle(m[0], m[1], AIV2.INSTANCE, AIV2.INSTANCE, difficulty, difficulty, seed, 300, null, null,
					swapped ? paramsB : paramsA, swapped ? paramsA : paramsB);
			if (r.error == null) { System.out.println("not reproduced (winner " + r.winner + ", turns " + r.turns + ")"); return; }
			StackTraceElement[] st = r.error.getStackTrace();
			System.out.println("REPRODUCED: " + r.error + " (" + st.length + " frames, turn " + r.turns + ")");
			for (int i = 0; i < Math.min(30, st.length); i++) System.out.println("    at " + st[i]);
		} finally {
			util.Print.setDebugSuppressed(prevSup);
		}
	}
}