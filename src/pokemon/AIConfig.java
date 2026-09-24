package pokemon;

/**
 * §6/§8. Difficulty gates and tuning knobs. Phase 2 added only branchBudget; Phase 3 adds the
 * fields it actually reads (§0 rule 5 - no drive-by additions): allowVoluntarySwitch,
 * deadTurnForcesSwitch, sackRatio, maxPlayerSwitchCols, maxAISwitchRows, minProb, epsilon,
 * style, useFullSim. Still NOT added: selectLead (Phase 7), useHistoryModel/infoMode (Phase 6),
 * temperature/alpha (Phase 6's finalStrategy exploit-blending - Phase 3's predict()/
 * finalStrategy() are the un-blended equilibrium only, see AIV2).
 * <p>
 * Phase 5 adds sackMinGain (the §7.14.2 min-gain guard) and enableSacking (developer A/B switch for the
 * "HARD with sacking vs HARD without" self-play measurement; NOT a difficulty gate, identical for NORMAL and HARD).
 */
public class AIConfig {
	public int branchBudget = 4;

	/** NORMAL=false (pivot moves and Perish-in-1 still allowed); HARD/EXTREME=true. */
	public boolean allowVoluntarySwitch = true;
	/** NORMAL dead turn: force a swap instead of letting the matrix decide. Default false per §7.13.1. */
	public boolean deadTurnForcesSwitch = false;
	/** A sack candidate must be worth < sackRatio * value(active), §7.14. */
	public double sackRatio = 0.8;
	/**
	 * Phase 5 (§7.14.2 SACK_MIN_GAIN): a sack row (a switch / pivot-switch to a sack-only candidate) must beat the best
	 * plain Stay row by at least this many eval points, averaged over the threat columns, or it is dropped before solving.
	 * eval is roughly HP points (100 ~ one healthy mon). Placeholder; tuned via self-play in Phase 8.
	 */
	public double sackMinGain = 10;
	/**
	 * Phase 5: false removes the AI's own sack candidates (and so the guard/classification). Exists only so self-play can
	 * compare sacking on/off; every difficulty leaves it true.
	 */
	public boolean enableSacking = true;
	public int maxPlayerSwitchCols = 3;
	public int maxAISwitchRows = 5;
	/** shape() cutoff: entries below this are zeroed and the rest renormalized. */
	public double minProb = 0.02;
	/** shape() mistake rate; 0 this phase (difficulty-tuned mistakes are a Phase 6+ knob). */
	public double epsilon = 0.0;
	public EvalWeights style = EvalWeights.BALANCED;
	/** Tier 2 (full move() sim) always on this phase; Tier 0/1 fast paths are Phase 8. */
	public boolean useFullSim = true;

	public static AIConfig normal() {
		AIConfig c = new AIConfig();
		c.allowVoluntarySwitch = false;
		return c;
	}

	public static AIConfig hard() {
		return new AIConfig();
	}
}