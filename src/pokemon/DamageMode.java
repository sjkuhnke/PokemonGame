package pokemon;

/**
 * Switches for {@code Pokemon.computeDamage} (spec section 7.3). Package-private on purpose.
 * <p>
 * The old {@code calcWithTypes(..., int mode, boolean crit, ..., boolean checkAcc)} folded five decisions into one
 * int. They are separated here so the legacy method and the new {@code calcRange} share one implementation:
 * <ul>
 * <li>{@code engine}: real-engine rules (move-fails sentinels, multi-hit, HP-capped damage, random side branches).
 * Legacy mode 0. Legacy modes +-1 are "estimate" rules (engine = false).</li>
 * <li>roll: RANDOM / MIN / MAX / AVG, or an exact value for iterating the 16 rolls.</li>
 * <li>crit: legacy rule, never, or forced (if the target cannot block it).</li>
 * <li>Chromo Beam bp doubling: random (legacy), never, or always (so a caller can mix both outcomes).</li>
 * <li>{@code checkAcc}: roll accuracy (legacy mode 0 only).</li>
 * </ul>
 */
final class DamageMode {
	static final int NOT_LEGACY = Integer.MIN_VALUE;

	static final int CRIT_LEGACY = 0, CRIT_NEVER = 1, CRIT_FORCE = 2;
	static final int CHROMO_RANDOM = 0, CHROMO_NO = 1, CHROMO_YES = 2;

	/** The old int mode, or NOT_LEGACY. When set, {@code calc(..., legacyMode)} is used so the RNG stream is consumed exactly as before. */
	final int legacyMode;
	/** The old {@code crit} argument (only read by the legacy estimate rule). */
	final boolean legacyCrit;
	final boolean checkAcc;
	/** Real-engine rules. */
	final boolean engine;
	/** Apply one-shot defensive effects the engine consumes elsewhere (Anticipation, resist berries). Legacy estimates and simulations want them. */
	final boolean predictOneShots;
	/** Multiply bp by the number of hits (legacy engine approximation). calcRange handles hits separately. */
	final boolean hitsInBp;
	/** Cap damage at the target's current HP and apply Sturdy / Focus Sash / False Swipe (legacy engine). calcRange reports these separately. */
	final boolean capAtHp;
	final RollMode rollMode;
	/** Exact roll multiplier when NOT legacy. */
	final double roll;
	final int crit;
	final int chromo;

	private DamageMode(int legacyMode, boolean legacyCrit, boolean checkAcc, boolean engine, boolean predictOneShots,
			boolean hitsInBp, boolean capAtHp, RollMode rollMode, double roll, int crit, int chromo) {
		this.legacyMode = legacyMode;
		this.legacyCrit = legacyCrit;
		this.checkAcc = checkAcc;
		this.engine = engine;
		this.predictOneShots = predictOneShots;
		this.hitsInBp = hitsInBp;
		this.capAtHp = capAtHp;
		this.rollMode = rollMode;
		this.roll = roll;
		this.crit = crit;
		this.chromo = chromo;
	}

	/** Maps the old calcWithTypes arguments. Mode 0 = engine rules; any other value = estimate rules. */
	static DamageMode legacy(int mode, boolean crit, boolean checkAcc) {
		boolean engine = mode == 0;
		RollMode rm = mode == -1 ? RollMode.MIN : mode == 1 ? RollMode.MAX : RollMode.RANDOM;
		return new DamageMode(mode, crit, engine && checkAcc, engine, !engine, engine, engine, rm, Double.NaN, CRIT_LEGACY,
				CHROMO_RANDOM);
	}

	/** Deterministic engine rules at an exact roll: what calcRange iterates. Never draws from any random stream. */
	static DamageMode det(double roll, int crit, int chromo) {
		return new DamageMode(NOT_LEGACY, false, false, true, true, false, false, RollMode.AVG, roll, crit, chromo);
	}

	boolean isLegacy() {
		return legacyMode != NOT_LEGACY;
	}

	/** True only for the legacy engine mode, the one that samples. */
	boolean random() {
		return legacyMode == 0;
	}

	/** The mode used for the self-hit branch of Magic Reflect / Possessed (the old code passed checkAcc = true there). */
	DamageMode forReflect() {
		boolean acc = checkAcc || random();
		return new DamageMode(legacyMode, legacyCrit, acc, engine, predictOneShots, hitsInBp, capAtHp, rollMode, roll, crit, chromo);
	}
}
