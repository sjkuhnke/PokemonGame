package pokemon;

/**
 * §6/§8. Difficulty gates and tuning knobs. Phase 2 added only branchBudget; Phase 3 adds the
 * fields it actually reads (§0 rule 5 - no drive-by additions): allowVoluntarySwitch,
 * deadTurnForcesSwitch, sackRatio, maxPlayerSwitchCols, maxAISwitchRows, minProb, epsilon,
 * style, useFullSim. Still NOT added: selectLead (Phase 7). infoMode (§8.2) is deliberately never added: battles are
 * open-team-sheet, the AI always sees the player's full sets, so there is no REVEALED_ONLY to configure. The solver
 * temperature (§7.10 quantal response) is not added either: Phase 6 ships the exploit blend, not a QRE solver.
 * <p>
 * Phase 6 adds useHistoryModel, alpha and temperatureExploit (§7.11) and the {@link #forDifficulty} factory.
 * <p>
 * Phase 5 adds sackMinGain (the §7.14.2 min-gain guard) and enableSacking (developer A/B switch for the
 * "HARD with sacking vs HARD without" self-play measurement; NOT a difficulty gate, identical for NORMAL and HARD).
 */
public class AIConfig {
	public int branchBudget = 4;

	/**
	 * Phase 6 (§7.11): use the player model (what this player tends to do) to blend an exploit into the equilibrium.
	 * false = pure equilibrium; exists so self-play can A/B it. true at every difficulty.
	 */
	public boolean useHistoryModel = true;
	/**
	 * Phase 6 (§8.2): weight of the equilibrium in {@code x = alpha * x_eq + (1 - alpha) * x_exploit} once the model is
	 * fully confident; with little data it is pulled toward 1 ({@link PlayerModel#alphaEff}). So 0.7 means at most 30% of
	 * the mix chases the read - the main "how hard does the AI punish habits" knob. Placeholder; Phase 8.
	 */
	public double alpha = 0.7;
	/** Phase 6: sharpness of the exploit softmax over the rows' payoff against y_hat (payoffs scaled by their range, so unit-free). Placeholder; Phase 8. */
	public double temperatureExploit = 6.0;

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
	public int maxPlayerSwitchCols = 4;
	public int maxAISwitchRows = 5;
	/** shape() cutoff: entries below this are zeroed and the rest renormalized. */
	public double minProb = 0.02;
	/** shape() mistake rate; 0 this phase (difficulty-tuned mistakes are a Phase 6+ knob). */
	public double epsilon = 0.0;
	public EvalWeights style = EvalWeights.BALANCED;
	/** Tier 2 (full move() sim) always on this phase; Tier 0/1 fast paths are Phase 8. */
	public boolean useFullSim = true;
	/** Phase 7 (§7.15, §8.1): EXTREME only. The AI picks its own lead from the player's team before turn 1. */
	public boolean selectLead = false;
	/** Phase 7: how many top rows / columns of the static lead matrix get a one-turn game value. Placeholder; Phase 8. */
	public int leadRefineKAi = 3;
	public int leadRefineKPlayer = 3;

	public static AIConfig normal() {
		AIConfig c = new AIConfig();
		c.allowVoluntarySwitch = false;
		return c;
	}

	public static AIConfig hard() {
		return new AIConfig();
	}
	
	/** Phase 7: the cheaper config the lead refinement builds each one-turn matrix with (no sacking, no history, small caps). */
	public AIConfig leadLite() {
		AIConfig c = new AIConfig();
		c.branchBudget = 2;
		c.useHistoryModel = false;
		c.enableSacking = false;
		c.allowVoluntarySwitch = allowVoluntarySwitch;
		c.deadTurnForcesSwitch = deadTurnForcesSwitch;
		c.maxAISwitchRows = Math.min(maxAISwitchRows, 4);
		c.maxPlayerSwitchCols = Math.min(maxPlayerSwitchCols, 2);
		c.sackRatio = sackRatio;
		c.sackMinGain = sackMinGain;
		c.alpha = alpha;
		c.temperatureExploit = temperatureExploit;
		c.minProb = minProb;
		c.epsilon = 0;
		c.style = style;
		c.useFullSim = useFullSim;
		return c;
	}

	/**
	 * The one place difficulty is decided: NORMAL gets {@link #normal()}, everything else {@link #hard()}. EXTREME has no
	 * in-battle difference from HARD until Phase 7 adds lead selection (the only EXTREME gate in §6).
	 */
	public static AIConfig forDifficulty(int difficulty) {
		AIConfig c = difficulty == Player.NORMAL ? normal() : hard();
		c.selectLead = difficulty == Player.EXTREME; // the second and last difficulty gate (§1.1, §8.1)
		return c;
	}
}