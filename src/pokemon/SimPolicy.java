package pokemon;

/**
 * How a simulation resolves the random parts of {@code move()} / {@code endOfTurn()} / {@code swapIn()} (spec 7.1).
 * Immutable. Activate with {@link SimContext#enter(SimPolicy)}.
 * <p>
 * Only the outcomes that change decisions are policy-driven: accuracy, damage roll, crits, secondary effects and
 * multi-hit counts. Every other draw the engine makes (sleep length, confusion length, random moves, ...) comes
 * from an isolated stream seeded with {@link #seed}: deterministic, never the real battle's stream.
 */
public final class SimPolicy {
	/** MAJORITY: hits when accuracy >= 50% (the rule scripted battles already use). HIT / MISS force the outcome of any move that can miss. */
	public enum Accuracy { MAJORITY, HIT, MISS }

	/**
	 * Secondary effects with a chance below 100%. MAJORITY: procs when chance >= 50%. PROC / NO_PROC force it.
	 * Effects with a 100% chance always happen, whatever the mode. (Expected-value handling arrives in Phase 4.)
	 */
	public enum Secondary { MAJORITY, PROC, NO_PROC }

	public static final SimPolicy DEFAULT = new SimPolicy(Accuracy.MAJORITY, Secondary.MAJORITY, 0.925, 0x5EEDL);

	public final Accuracy accuracy;
	public final Secondary secondary;
	/** Damage roll multiplier used when a sim calls {@code calc}: the mean of the 16 rolls by default. */
	public final double damageRoll;
	/** Seed of the isolated stream for draws the policy does not cover. */
	public final long seed;

	private SimPolicy(Accuracy accuracy, Secondary secondary, double damageRoll, long seed) {
		this.accuracy = accuracy;
		this.secondary = secondary;
		this.damageRoll = damageRoll;
		this.seed = seed;
	}

	public SimPolicy withAccuracy(Accuracy a) {
		return new SimPolicy(a, secondary, damageRoll, seed);
	}

	public SimPolicy withSecondary(Secondary s) {
		return new SimPolicy(accuracy, s, damageRoll, seed);
	}

	public SimPolicy withDamageRoll(double r) {
		return new SimPolicy(accuracy, secondary, r, seed);
	}

	public SimPolicy withSeed(long s) {
		return new SimPolicy(accuracy, secondary, damageRoll, s);
	}

	/** @param accPercent effective accuracy in percent (0..100+) */
	public boolean accuracyHits(double accPercent) {
		if (accPercent >= 100) return true;
		if (accPercent <= 0) return false;
		switch (accuracy) {
		case HIT: return true;
		case MISS: return false;
		default: return accPercent >= 50;
		}
	}

	/** @param chance secondary-effect chance in percent */
	public boolean secondaryProcs(int chance) {
		if (chance >= 100) return true;
		if (chance <= 0) return false;
		switch (secondary) {
		case PROC: return true;
		case NO_PROC: return false;
		default: return chance >= 50;
		}
	}

	/** Deterministic hit count for a multi-hit move: the expected count, rounding a .5 down (3.1 -> 3, 4.5 -> 4). */
	public int multiHitCount(double expectedHits) {
		return (int) Math.ceil(expectedHits - 0.5);
	}
}
