package pokemon;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Deterministic description of what one move does to one target (spec sections 6 and 7.3).
 * Replaces the sentinel ints of {@code calcWithTypes} (-1 fails, 0 no damage, 1 missed) with flags.
 * <p>
 * Damage figures are <b>per hit</b> and are not capped at the target's HP. {@code min / avg / max} are the
 * non-crit rolls (0.85 .. 1.00); the crit mixture (and Chromo Beam's bp doubling) lives in the outcome
 * distribution used by {@link #koProb(double)} and {@link #expected()}.
 * <p>
 * Multi-hit: {@link #hits} is the expected hit count. {@code koProb} treats the hits of one use as fully
 * correlated (n times one per-hit outcome). That over-states variance a little; it is documented in the spec
 * follow-ups for Phase 4.
 * <p>
 * Accuracy is <b>not</b> folded into {@code koProb}; branch on {@link #accuracy} separately.
 */
public final class DamageRange {
	/** False when the move fails or cannot be used (replaces the -1 sentinel; also Torment / Disable / Heal Block / Mute). */
	public final boolean usable;
	/** Type / ability / item immunity (replaces the 0 sentinel). */
	public final boolean immune;
	/** Damage would land on the attacker (Magic Reflect / Possessed); the figures are the damage the attacker would take. */
	public final boolean reflected;
	public final double min, avg, max;
	/** Chance of a crit, 0 when the target blocks crits. */
	public final double critProb;
	/** Effective accuracy in [0, 1]; 1.0 for never-miss moves; 0 if the target is semi-invulnerable and the move can miss. */
	public final double accuracy;
	/** Expected number of hits (1 for single-hit moves). */
	public final double hits;

	private final double[] vals;      // per-hit outcomes, ascending
	private final double[] weights;   // matching probabilities, sum to 1
	private final double[] hitProb;   // hitProb[n] = P(exactly n hits)
	private final int foeMaxHp;
	private final boolean endureAtFull;
	private final boolean neverKills;

	private DamageRange(boolean usable, boolean immune, boolean reflected, double min, double avg, double max, double critProb,
			double accuracy, double hits, double[] vals, double[] weights, double[] hitProb, int foeMaxHp,
			boolean endureAtFull, boolean neverKills) {
		this.usable = usable;
		this.immune = immune;
		this.reflected = reflected;
		this.min = min;
		this.avg = avg;
		this.max = max;
		this.critProb = critProb;
		this.accuracy = accuracy;
		this.hits = hits;
		this.vals = vals;
		this.weights = weights;
		this.hitProb = hitProb;
		this.foeMaxHp = foeMaxHp;
		this.endureAtFull = endureAtFull;
		this.neverKills = neverKills;
	}

	private static final double[] NONE = new double[0];

	static DamageRange unusable() {
		return new DamageRange(false, false, false, 0, 0, 0, 0, 0, 0, NONE, NONE, new double[] { 1.0 }, 0, false, false);
	}

	static DamageRange immune() {
		return new DamageRange(true, true, false, 0, 0, 0, 0, 0, 0, NONE, NONE, new double[] { 1.0 }, 0, false, false);
	}

	/** Usable, deals no direct damage (status moves, Counter family). Accuracy still matters for status moves. */
	static DamageRange noDamage(double accuracy) {
		return new DamageRange(true, false, false, 0, 0, 0, 0, accuracy, 1, NONE, NONE, new double[] { 0.0, 1.0 }, 0, false, false);
	}

	/**
	 * @param outcomes per-hit damage values (any order)
	 * @param probs    matching probabilities (must sum to about 1)
	 * @param hitProb  hitProb[n] = P(n hits)
	 */
	static DamageRange of(double[] outcomes, double[] probs, double min, double avg, double max, double critProb, double accuracy,
			double[] hitProb, int foeMaxHp, boolean endureAtFull, boolean neverKills, boolean reflected) {
		Integer[] order = new Integer[outcomes.length];
		for (int i = 0; i < order.length; i++) order[i] = i;
		Arrays.sort(order, Comparator.comparingDouble(i -> outcomes[i]));
		double[] v = new double[order.length], w = new double[order.length];
		for (int i = 0; i < order.length; i++) {
			v[i] = outcomes[order[i]];
			w[i] = probs[order[i]];
		}
		double expHits = 0;
		for (int n = 0; n < hitProb.length; n++) expHits += n * hitProb[n];
		return new DamageRange(true, false, reflected, min, avg, max, critProb, accuracy, expHits, v, w, hitProb.clone(), foeMaxHp,
				endureAtFull, neverKills);
	}

	/** True if the move can put damage on the target: usable, not immune, not reflected, and some outcome is above zero. */
	public boolean dealsDamage() {
		return usable && !immune && !reflected && vals.length > 0 && vals[vals.length - 1] > 0;
	}

	/** Mean damage of one hit over the crit / bonus mixture (before accuracy and hit count). */
	public double expectedPerHit() {
		double s = 0;
		for (int i = 0; i < vals.length; i++) s += vals[i] * weights[i];
		return s;
	}

	/** Accuracy x expected hits x mean per-hit damage. Not capped at the target's HP: cap with {@code Math.min(hp, ...)} at the call site. */
	public double expected() {
		return accuracy * hits * expectedPerHit();
	}

	/**
	 * Expected damage actually dealt to a target with {@code hp} HP: accuracy x sum over hit counts of E[min(n x outcome, limit)],
	 * where the limit is {@code hp}, or {@code hp - 1} if the target survives a lethal hit (Sturdy / Focus Sash at full HP, False Swipe).
	 */
	public double expectedCapped(double hp) {
		if (!usable || immune || reflected || vals.length == 0 || hp <= 0) return 0;
		double limit = (neverKills || (endureAtFull && hp >= foeMaxHp)) ? hp - 1 : hp;
		double total = 0;
		for (int n = 1; n < hitProb.length; n++) {
			if (hitProb[n] <= 0) continue;
			double e = 0;
			for (int i = 0; i < vals.length; i++) e += Math.min(n * vals[i], limit) * weights[i];
			total += hitProb[n] * e;
		}
		return accuracy * total;
	}

	/**
	 * P(one use of the move deals at least {@code hp} damage), given it hits. Over the 16 rolls, mixed with the crit
	 * (and Chromo Beam) outcomes. 0 for a move that cannot put damage on the target, for False Swipe, and for a
	 * Sturdy / Focus Sash target at full HP.
	 */
	public double koProb(double hp) {
		if (!usable || immune || reflected || neverKills || vals.length == 0) return 0;
		if (hp <= 0) return 1;
		if (endureAtFull && hp >= foeMaxHp) return 0;
		double p = 0;
		for (int n = 1; n < hitProb.length; n++) {
			if (hitProb[n] <= 0) continue;
			double need = hp / n;
			double q = 0;
			for (int i = vals.length - 1; i >= 0 && vals[i] >= need; i--) q += weights[i];
			p += hitProb[n] * q;
		}
		return Math.min(1.0, p);
	}

	@Override
	public String toString() {
		if (!usable) return "DamageRange[unusable]";
		if (immune) return "DamageRange[immune]";
		return String.format(Locale.ROOT, "DamageRange[%s%.1f/%.1f/%.1f per hit, crit %.0f%%, acc %.0f%%, hits %.2f, expected %.1f]",
				reflected ? "reflected " : "", min, avg, max, critProb * 100, accuracy * 100, hits, expected());
	}
}
