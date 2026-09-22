package pokemon;

/**
 * Internal result of {@code Pokemon.computeDamage}. Replaces the sentinel ints: the legacy pair
 * (-1 / 0 / 1 / damage) is still available through {@link #first} / {@link #second} so
 * {@code calcWithTypes} stays bit-for-bit compatible, while new code reads {@link #kind}.
 */
final class DamageResult {
	enum Kind {
		/** Damage (possibly 0 for a move that does no damage by design, see NO_DAMAGE). */
		OK,
		/** The move fails or cannot be used (Fake Out not first turn, Dream Eater on an awake foe, Torment, Disable, ...). */
		UNUSABLE,
		/** Type / ability / item immunity. */
		IMMUNE,
		/** Status move, Counter family: usable, deals no direct damage. */
		NO_DAMAGE,
		/** Legacy accuracy roll failed (engine + checkAcc only). */
		ACC_FAIL
	}

	final Kind kind;
	/** Legacy Pair first: damage, or the sentinel (-1 fails, 0 no damage, 1 accuracy miss). */
	final int first;
	/** Legacy Pair second: damage as a percent of the target's max HP. */
	final double second;
	/** Damage would land on the attacker (Magic Reflect / Possessed). */
	final boolean reflected;
	/** This particular result was a crit. */
	final boolean crit;
	/** Effective crit stage after status, abilities and items (only meaningful for OK). */
	final int critStage;
	/** The target cannot be crit (Battle Armor, Shell Armor, Magma Armor, Lucky Chant). */
	final boolean critBlocked;
	/** Sturdy or Focus Sash: survives one hit from full HP. */
	final boolean endureAtFull;
	/** False Swipe: never KOs. */
	final boolean neverKills;

	private DamageResult(Kind kind, int first, double second, boolean reflected, boolean crit, int critStage,
			boolean critBlocked, boolean endureAtFull, boolean neverKills) {
		this.kind = kind;
		this.first = first;
		this.second = second;
		this.reflected = reflected;
		this.crit = crit;
		this.critStage = critStage;
		this.critBlocked = critBlocked;
		this.endureAtFull = endureAtFull;
		this.neverKills = neverKills;
	}

	static DamageResult unusable(int legacyFirst) {
		return new DamageResult(Kind.UNUSABLE, legacyFirst, 0.0, false, false, 0, false, false, false);
	}

	static DamageResult immune() {
		return new DamageResult(Kind.IMMUNE, 0, 0.0, false, false, 0, false, false, false);
	}

	static DamageResult noDamage() {
		return new DamageResult(Kind.NO_DAMAGE, 0, 0.0, false, false, 0, false, false, false);
	}

	static DamageResult accFail() {
		return new DamageResult(Kind.ACC_FAIL, 1, 0.0, false, false, 0, false, false, false);
	}

	static DamageResult dealt(int damage, double percent, boolean crit, int critStage, boolean critBlocked,
			boolean endureAtFull, boolean neverKills) {
		return new DamageResult(Kind.OK, damage, percent, false, crit, critStage, critBlocked, endureAtFull, neverKills);
	}

	/**
	 * Same result, but the damage lands on the attacker. The legacy sampling mode (mode 0) reports it as
	 * {@code 1 - damage}, as the old code did; pass {@code false} to keep the plain damage figure.
	 */
	DamageResult reflectedOnto(boolean legacyNegate) {
		return new DamageResult(kind, legacyNegate ? 1 - first : first, second, true, crit, critStage, critBlocked, endureAtFull, neverKills);
	}
}
