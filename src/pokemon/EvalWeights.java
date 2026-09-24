package pokemon;

/**
 * §7.8 / §6 AIConfig.style. Weights for the terms of {@link Evaluator#eval}. Placeholder
 * values this phase (§12 Phase 3: "eval with placeholder weights"); tuned via self-play in
 * Phase 8. Trainer style profiles (aggressive/stall/balanced) arrive in Phase 6 - only
 * {@link #BALANCED} exists for now.
 */
public final class EvalWeights {
	public final double wMaterial, wMatchup, wHazard, wStatus, wField, wTempo;
	/** Phase 4: pending Wish/Healing Wish/Lunar Dance value and the action-set restriction penalty (Taunt/Disable/Encore/Torment). */
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

	public static final EvalWeights BALANCED = new EvalWeights(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
}