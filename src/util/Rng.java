package util;

import java.util.Random;

/**
 * Single injectable random source for battle and AI code (spec section 7.1).
 * <p>
 * Unseeded, it behaves like the old {@code new Random()} / {@code Math.random()} calls.
 * Call {@link #setSeed(long)} before a battle to make it reproducible.
 * <p>
 * {@link #asRandom()} returns one stable {@link Random} instance that shares this stream, so
 * {@code Random r = Rng.asRandom(); r.nextInt(n)} keeps working and reseeding takes effect
 * even for code that cached the reference.
 */
public final class Rng {
	private static final Random RND = new Random();
	private static volatile long seed;
	private static volatile boolean seeded;

	private Rng() {}

	/** Reseeds the shared stream; the same seed always replays the same sequence. */
	public static void setSeed(long s) {
		RND.setSeed(s);
		seed = s;
		seeded = true;
	}

	/** Returns to an unpredictable stream (normal gameplay). */
	public static void randomize() {
		RND.setSeed(new Random().nextLong());
		seeded = false;
	}

	public static boolean isSeeded() {
		return seeded;
	}

	/** The last seed passed to {@link #setSeed(long)}; only meaningful if {@link #isSeeded()}. */
	public static long getSeed() {
		return seed;
	}

	/** Uniform double in [0, 1). Drop-in for {@code Math.random()}. */
	public static double next() {
		return RND.nextDouble();
	}

	/** True with probability p (p <= 0 never, p >= 1 always). */
	public static boolean chance(double p) {
		return next() < p;
	}

	/** Uniform int in [0, n). */
	public static int nextInt(int n) {
		return RND.nextInt(n);
	}

	public static boolean nextBoolean() {
		return RND.nextBoolean();
	}

	public static double nextGaussian() {
		return RND.nextGaussian();
	}

	/** The shared stream as a java.util.Random. Do not construct your own Random in battle code. */
	public static Random asRandom() {
		return RND;
	}
}