package ai;

import java.util.Objects;

/**
 * Weights for the terms of {@link Evaluator#eval}, held by {@link AIConfig#style}. {@code wMaterial} is fixed at 1.0 and
 * anchors the scale (the sack guard's minimum gain and the evaluator's escape cost are absolute eval points); the other
 * weights are registered in {@link Tunables}, and self-play found the engine insensitive to all of them. Equality is by
 * value, so configs can be compared.
 */
public final class EvalWeights {
	/** Weights of: material (HP and alive mons), the active matchup, entry hazards, bench status, field effects, tempo. */
	public final double wMaterial, wMatchup, wHazard, wStatus, wField, wTempo;
	/** Weight of pending recovery (Wish, Healing Wish, Lunar Dance) and the action-set restriction cost (Taunt, Disable, Encore, Torment). */
	public final double wPending;

	public EvalWeights(double wMaterial, double wMatchup, double wHazard, double wStatus, double wField, double wTempo) {
		this(wMaterial, wMatchup, wHazard, wStatus, wField, wTempo, 1.0);
	}

	public EvalWeights(double wMaterial, double wMatchup, double wHazard, double wStatus, double wField, double wTempo, double wPending) {
		this.wPending = wPending;
		this.wMaterial = wMaterial;
		this.wMatchup = wMatchup;
		this.wHazard = wHazard;
		this.wStatus = wStatus;
		this.wField = wField;
		this.wTempo = wTempo;
	}

	/** Every weight 1.0. */
	public static final EvalWeights BALANCED = new EvalWeights(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EvalWeights)) return false;
		EvalWeights e = (EvalWeights) o;
		return Double.compare(wMaterial, e.wMaterial) == 0 && Double.compare(wMatchup, e.wMatchup) == 0 && Double.compare(wHazard, e.wHazard) == 0
				&& Double.compare(wStatus, e.wStatus) == 0 && Double.compare(wField, e.wField) == 0 && Double.compare(wTempo, e.wTempo) == 0
				&& Double.compare(wPending, e.wPending) == 0;
	}
	@Override public int hashCode() { return Objects.hash(wMaterial, wMatchup, wHazard, wStatus, wField, wTempo, wPending); }
}