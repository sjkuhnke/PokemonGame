package pokemon;

import java.util.List;
import java.util.Random;

import util.Print;
import util.Rng;

/**
 * Phase 8 (tuning half): coordinate ascent over {@link Tunables} using seat-swapped self-play.
 * <p>
 * For each parameter, a candidate vector (the incumbent with that one value stepped up, then down) plays the incumbent in
 * chunks of battles. Both engines are {@code AIV2.INSTANCE}; the only difference is the parameter vector installed before each
 * seat decides ({@code SelfPlay.playBattle}'s pv1/pv2). After each chunk a Wilson interval on the candidate's score decides:
 * lower bound above 0.5 = accept (and try the same direction again, up to 3 times); upper bound below 0.5 = reject; neither
 * by maxBattles = reject (inconclusive keeps the incumbent). Pairs of teams are seeded, so every test is reproducible.
 * <p>
 * Statistics are the limit, not speed: a 5-point edge needs well over a thousand battles to show. Treat a short run as a
 * screen for large effects and a long overnight run (maxBattles in the thousands) as the real tuning. Self-play measures
 * strength against this same engine, not against a human; keep the player-facing probability dumps as the sanity check.
 */
public final class Tuner {
	private Tuner() {}

	public static final class Settings {
		/** Battles played between interval checks; rounded up to a whole matchup (two seatings). */
		public int chunk = 30;
		/** Per candidate; reaching it without a decision rejects the candidate. */
		public int maxBattles = 300;
		public int passes = 1;
		public int difficulty = Player.HARD;
		public int maxTurns = 300;
		/** One-sided about 95%. Raise for fewer false accepts. */
		public double z = 1.64;
		public long baseSeed = 20261010L;
		public SelfPlay.TeamSource teams = SelfPlay.randomCompetitive(6);
		/** Whole-run wall budget, 0 = none. */
		public double maxSeconds = 0;
		/** Starting vector, null = Tunables.defaults(). */
		public double[] start;
		/** Fraction: multiplicative step is (1 + stepFrac); additive step is stepFrac * (hi - lo). */
		public double stepFrac = 0.25;
		/** Names from Tunables.all() to tune, null = all. */
		public List<String> only;
		// no accept/reject before this many battles
		public int minBattles = 200;
		// after this many battles...
		public int futilityAfter = 600;
		// ...stop if even the optimistic bound is below this
		public double futilityUpper = 0.55;
	}

	private static final class Tally {
		double score;
		int n;
	}

	/** Returns the tuned vector (also printed). Blocks the game thread. */
	public static double[] run(Settings s) {
		if (Pokemon.gp == null) throw new IllegalStateException("Pokemon.gp is null: run inside the game (game thread)");
		final boolean prevSup = Print.isDebugSuppressed();
		final boolean wasSeeded = Rng.isSeeded();
		final long prevSeed = Rng.getSeed();
		Print.setDebugSuppressed(true);
		long deadline = s.maxSeconds > 0 ? System.nanoTime() + (long) (s.maxSeconds * 1e9) : Long.MAX_VALUE;
		int[] cursor = { 0 };
		double[] best = s.start == null ? Tunables.defaults() : s.start.clone();
		try {
			List<Tunables.Param> ps = Tunables.all();
			search:
			for (int pass = 0; pass < s.passes; pass++) {
				for (int i = 0; i < ps.size(); i++) {
					Tunables.Param p = ps.get(i);
					if (s.only != null && !s.only.contains(p.name)) continue;
					boolean moved = false;
					for (int dir = 1; dir >= -1 && !moved; dir -= 2) {
						for (int rep = 0; rep < 3; rep++) {
							if (System.nanoTime() > deadline) break search;
							double v = step(p, best[i], dir, s.stepFrac);
							if (Math.abs(v - best[i]) < 1e-12) break;
							double[] cand = best.clone();
							cand[i] = v;
							Tally t = new Tally();
							int verdict = test(s, cand, best, cursor, t, deadline);
							double[] w = wilson(t.score, Math.max(1, t.n), s.z);
							System.out.printf("[tuner] pass %d %s %.4g -> %.4g : score %.1f%% over %d (CI %.1f-%.1f%%) %s%n", pass + 1, p.name,
									best[i], v, 100 * t.score / Math.max(1, t.n), t.n, 100 * w[0], 100 * w[1],
									verdict > 0 ? "ACCEPT" : verdict < 0 ? "reject" : "inconclusive, keep");
							if (verdict > 0) {
								best = cand;
								System.out.println("[tuner] incumbent now:\n" + Tunables.describe(best));
								moved = true;
							} else {
								break;
							}
						}
					}
				}
			}
		} finally {
			Tunables.apply(null);
			Print.setDebugSuppressed(prevSup);
			if (wasSeeded) Rng.setSeed(prevSeed); else Rng.randomize();
		}
		System.out.println("[tuner] result:\n" + Tunables.describe(best));
		return best;
	}

	/** +1 accept, -1 reject, 0 inconclusive (treated as reject by the caller). */
	private static int test(Settings s, double[] cand, double[] inc, int[] cursor, Tally t, long deadline) {
		while (t.n < s.maxBattles) {
			play(s, cand, inc, s.chunk, cursor, t, deadline);
			if (t.n == 0) return 0;
			if (t.n < s.minBattles) continue;
			double[] w = wilson(t.score, t.n, s.z);
			if (w[0] > 0.5) return 1;
			if (w[1] < 0.5) return -1;
			if (t.n >= s.futilityAfter && w[1] < s.futilityUpper) return 0; // can't be a meaningful gain
			if (System.nanoTime() > deadline) return 0;
		}
		return 0;
	}

	private static void play(Settings s, double[] cand, double[] inc, int battles, int[] cursor, Tally t, long deadline) {
		int done = 0;
		while (done < battles && System.nanoTime() <= deadline) {
			int pair = cursor[0]++;
			Rng.setSeed(s.baseSeed + 1_000_003L * pair); // team generation is reproducible
			Trainer[] m = s.teams.next(pair, new Random(s.baseSeed + pair));
			for (int seat = 0; seat < 2; seat++) {
				long seed = s.baseSeed + 7919L * (2L * pair + seat);
				double[] pv1 = seat == 0 ? cand : inc, pv2 = seat == 0 ? inc : cand;
				SelfPlay.BattleResult r = SelfPlay.playBattle(m[0], m[1], AIV2.INSTANCE, AIV2.INSTANCE, s.difficulty, s.difficulty, seed,
						s.maxTurns, null, null, pv1, pv2);
				done++;
				if (r.error != null) {
					StackTraceElement[] st = r.error.getStackTrace();
					StringBuilder sb = new StringBuilder("[tuner] CRASH pair " + pair + " seat " + seat + " seed " + seed + ": " + r.error);
					for (int k = 0; k < Math.min(12, st.length); k++) sb.append("\n    at ").append(st[k]);
					System.out.println(sb);
					continue;
				}
				t.n++;
				if (r.winner < 0) t.score += 0.5;
				else if ((r.winner == 0) == (seat == 0)) t.score += 1.0; // the candidate sat in the winning seat
			}
		}
	}

	private static double step(Tunables.Param p, double x, int dir, double f) {
		double v = p.log ? (dir > 0 ? x * (1 + f) : x / (1 + f)) : x + dir * f * (p.hi - p.lo);
		return p.clamp(v);
	}

	/** Wilson score interval for a proportion (draws count 0.5). */
	static double[] wilson(double score, int n, double z) {
		double p = score / n, z2 = z * z, d = 1 + z2 / n;
		double c = (p + z2 / (2.0 * n)) / d;
		double h = z * Math.sqrt(p * (1 - p) / n + z2 / (4.0 * n * n)) / d;
		return new double[] { c - h, c + h };
	}
}