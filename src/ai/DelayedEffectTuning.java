package ai;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import pokemon.*;

/**
 * Self-play drivers for the Phase 8 addendum constants (sleep / Yawn / Future Sight, and the HP buffer). Call these from the
 * game thread, the same place you call {@link Tuner#run}. Needs the nine new lines in {@link Tunables}.
 * <p>
 * Order: {@link #confirmFeatures} (are the features at least harmless, and does the signal show up at all?), then
 * {@link #tuneSleep}, then {@link #tuneBuffer}, then {@link #confirmAgainstDefaults} on fresh seeds before adopting anything.
 * Self-play only measures strength against this same engine, so for the HP buffer (whose point is player-facing behavior)
 * read a result as "does it hurt", not "does it help"; keep {@code Phase8HealingDiagnosis.diagnoseBuffer()} as the check that
 * it does what it should.
 */
public final class DelayedEffectTuning {
	private DelayedEffectTuning() {}

	public static final Move[] SLEEP_MOVES = { Move.YAWN, Move.FUTURE_SIGHT, Move.REST };
	public static final Move[] HEAL_MOVES = { Move.RECOVER, Move.REST, Move.WISH };

	// ------------------------------------------------------------------ team source

	/**
	 * Matchups from {@code base} in which some mon on either side knows one of {@code moves}. Without this, most random teams
	 * never use the moves being tested and the effect drowns in noise. Rejection sampling: it just asks {@code base} for the
	 * next matchup (the generator advances the seeded Rng, so each try differs) until one qualifies, so the result is still
	 * reproducible from the seed. If {@code maxTries} fail, the last matchup is used and counted in {@link #describe()}.
	 */
	public static final class FilteredTeams implements SelfPlay.TeamSource {
		private final SelfPlay.TeamSource base;
		private final Move[] moves;
		private final int maxTries;
		private int hits, fellThrough;

		public FilteredTeams(SelfPlay.TeamSource base, int maxTries, Move... moves) {
			this.base = base;
			this.maxTries = maxTries;
			this.moves = moves;
		}

		@Override
		public Trainer[] next(int pairIndex, Random sched) {
			Trainer[] m = null;
			for (int i = 0; i < maxTries; i++) {
				m = base.next(pairIndex, sched);
				if (has(m[0]) || has(m[1])) {
					hits++;
					return m;
				}
			}
			fellThrough++;
			return m;
		}

		private boolean has(Trainer t) {
			if (t == null || t.team == null) return false;
			for (Pokemon p : t.team) {
				if (p == null || p.moveset == null) continue;
				for (Moveslot ms : p.moveset) {
					if (ms == null || ms.move == null) continue;
					for (Move x : moves) if (ms.move == x) return true;
				}
			}
			return false;
		}

		@Override
		public String describe() {
			return base.describe() + " filtered to matchups containing " + Arrays.toString(moves) + " (" + hits + " found, " + fellThrough + " fell through)";
		}
	}

	public static SelfPlay.TeamSource teamsWith(Move... moves) {
		return new FilteredTeams(SelfPlay.randomCompetitive(6), 400, moves);
	}

	// ------------------------------------------------------------------ vectors

	/** The defaults with the named constants overridden: {@code vector("Evaluator.W_BUFFER", 1.0, "Evaluator.W_SLEEP", 0.0)}. */
	public static double[] vector(Object... nameValue) {
		double[] v = Tunables.defaults();
		List<Tunables.Param> ps = Tunables.all();
		for (int k = 0; k < nameValue.length; k += 2) {
			String name = (String) nameValue[k];
			double val = ((Number) nameValue[k + 1]).doubleValue();
			int idx = -1;
			for (int i = 0; i < ps.size(); i++) if (ps.get(i).name.equals(name)) idx = i;
			if (idx < 0) throw new IllegalArgumentException("not registered in Tunables: " + name);
			v[idx] = val;
		}
		return v;
	}

	// ------------------------------------------------------------------ A/B

	/** Plays {@code battles} seat-swapped battles of vector {@code a} (engine A) against {@code b} and prints A's score with its interval. */
	public static SelfPlayReport ab(String label, double[] a, double[] b, int battles, long seed, SelfPlay.TeamSource teams) {
		SelfPlay.Config c = new SelfPlay.Config();
		c.battles = battles;
		c.baseSeed = seed;
		c.teams = teams;
		c.paramsA = a;
		c.paramsB = b;
		c.determinismSample = 2;
		c.progressEvery = 100;
		System.out.println("\n[ab] " + label + "   teams: " + teams.describe());
		SelfPlayReport r = SelfPlay.run(c);
		double[] ci = r.wilson95();
		System.out.println("[ab] teams after the run: " + teams.describe());
		System.out.println(String.format(Locale.ROOT, "[ab] %s: first vector scores %.1f%%  (95%% CI %.1f%% - %.1f%%) over %d battles",
				label, 100 * r.scoreA(), 100 * ci[0], 100 * ci[1], r.battles));
		System.out.println(ci[0] > 0.5 ? "[ab]   -> significantly better" : ci[1] < 0.5 ? "[ab]   -> significantly WORSE" : "[ab]   -> no significant difference");
		return r;
	}

	/**
	 * Step 1. (a) Sleep + Future Sight terms on vs off, on teams that contain Yawn / Future Sight / Rest. (b) HP buffer on vs off,
	 * on teams that contain Recover / Rest / Wish. Use 400 for a smoke run, 1000+ for a real answer.
	 */
	public static void confirmFeatures(int battles) {
		long seed = 20261102L;
		double[] sleepOff = vector("Evaluator.W_SLEEP", 0.0, "Evaluator.FUTURE_SIGHT_HOLD", 0.0, "Evaluator.W_BUFFER", 0.0);
		double[] sleepOn = vector("Evaluator.W_BUFFER", 0.0);
		ab("sleep + Future Sight ON vs OFF (buffer off in both)", sleepOn, sleepOff, battles, seed, teamsWith(SLEEP_MOVES));

		double[] bufOff = vector("Evaluator.W_BUFFER", 0.0);
		double[] bufOn = vector("Evaluator.W_BUFFER", 1.0);
		ab("HP buffer ON vs OFF", bufOn, bufOff, battles, seed + 1, teamsWith(HEAL_MOVES));
	}

	/**
	 * Buffer ON vs OFF only (the sleep half of {@link #confirmFeatures} does not change when the buffer does): once on teams that
	 * contain Recover / Rest / Wish, once on plain random teams as the regression check. Run this before {@link #tuneBuffer}.
	 */
	public static void confirmBuffer(int battles) {
		double[] on = vector("Evaluator.W_BUFFER", 1.0), off = vector("Evaluator.W_BUFFER", 0.0);
		ab("HP buffer ON vs OFF, heal teams", on, off, battles, 20261102L + 1, teamsWith(HEAL_MOVES));
		ab("HP buffer ON vs OFF, plain random teams", on, off, battles, 20261102L + 2, SelfPlay.randomCompetitive(6));
	}

	// ------------------------------------------------------------------ tuning

	private static Tuner.Settings settings(SelfPlay.TeamSource teams, double maxSeconds, long seed) {
		Tuner.Settings s = new Tuner.Settings();
		s.teams = teams;
		s.maxSeconds = maxSeconds;
		s.baseSeed = seed;
		s.chunk = 40;
		s.minBattles = 300;
		s.maxBattles = 900;
		s.futilityAfter = 500;
		s.z = 2.5;
		s.passes = 1;
		s.stepFrac = 0.25;
		return s;
	}

	/** Step 2: tunes the sleep / Future Sight constants with the buffer OFF, on teams that use those moves. Returns the vector. */
	public static double[] tuneSleep(double maxSeconds) {
		Tuner.Settings s = settings(teamsWith(SLEEP_MOVES), maxSeconds, 20261110L);
		s.only = Arrays.asList("Evaluator.SLEEP_TURN_VALUE", "Evaluator.SLEEP_HOLD", "Evaluator.DROWSY_LAND", "Evaluator.FUTURE_SIGHT_HOLD");
		s.start = vector("Evaluator.W_BUFFER", 0.0);
		double[] r = Tuner.run(s);
		System.out.println("[tuner] teams: " + s.teams.describe());
		return r;
	}

	/**
	 * Step 3: tunes the HP-buffer constants with the buffer ON (W_BUFFER starts at 1.0, otherwise every candidate is identical to
	 * the incumbent). {@code from} is the vector from {@link #tuneSleep} (or null for the defaults).
	 */
	public static double[] tuneBuffer(double maxSeconds, double[] from) {
		Tuner.Settings s = settings(teamsWith(HEAL_MOVES), maxSeconds, 20261120L);
		s.only = Arrays.asList("Evaluator.W_BUFFER", "Evaluator.BUFFER_DISCOUNT", "Evaluator.BUFFER_TURNS", "Evaluator.BUFFER_SAFETY", "Evaluator.BUFFER_FLOOR");
		double[] start = from == null ? Tunables.defaults() : from.clone();
		start[indexOf("Evaluator.W_BUFFER")] = 1.0;
		s.start = start;
		double[] r = Tuner.run(s);
		System.out.println("[tuner] teams: " + s.teams.describe());
		return r;
	}

	/**
	 * Step 4: a tuned vector against the shipped defaults on FRESH seeds, once on the filtered teams and once on plain random teams
	 * (the second is the regression check: the tuned values must not cost strength where the moves rarely appear).
	 */
	public static void confirmAgainstDefaults(double[] tuned, int battles) {
		long seed = 20261201L;
		ab("tuned vs defaults, sleep teams", tuned, null, battles, seed, teamsWith(SLEEP_MOVES));
		ab("tuned vs defaults, heal teams", tuned, null, battles, seed + 1, teamsWith(HEAL_MOVES));
		ab("tuned vs defaults, plain random teams", tuned, null, battles, seed + 2, SelfPlay.randomCompetitive(6));
	}

	private static int indexOf(String name) {
		List<Tunables.Param> ps = Tunables.all();
		for (int i = 0; i < ps.size(); i++) if (ps.get(i).name.equals(name)) return i;
		throw new IllegalArgumentException("not registered in Tunables: " + name);
	}
}