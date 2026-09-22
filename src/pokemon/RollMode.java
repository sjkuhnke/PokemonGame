package pokemon;

/**
 * How the damage roll (0.85 .. 1.00 in 1/100 steps) is chosen (spec section 7.3).
 * <p>
 * RANDOM is the real engine's roll. MIN / MAX are the legacy {@code mode = -1 / 1} estimates.
 * AVG is the mean of the 16 rolls (0.925) and is what a simulation uses when it must not sample.
 */
public enum RollMode {
	RANDOM(Double.NaN),
	MIN(0.85),
	MAX(1.0),
	AVG(0.925);

	/** Fixed multiplier, or NaN for RANDOM. */
	public final double multiplier;

	RollMode(double multiplier) {
		this.multiplier = multiplier;
	}

	/** The i-th of the 16 possible rolls, i in [0, 15]. Same arithmetic as {@code Pokemon.calc}. */
	public static double roll(int i) {
		double r = i;
		r += 85;
		r /= 100;
		return r;
	}
}
