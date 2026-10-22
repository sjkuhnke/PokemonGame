package ai;

import pokemon.Player;

/**
 * Settings for one decision of the Trainer AI: the difficulty gates and the tuning knobs.
 * <p>
 * One engine runs at every difficulty. Difficulty only changes two things, both decided in {@link #forDifficulty}:
 * <ul>
 * <li>{@link #allowVoluntarySwitch} (off for NORMAL): whether the AI may switch out on its own initiative. Pivot moves,
 * Perish Song at 1, Struggle-only and "no move does anything" turns are always allowed, whatever this says.</li>
 * <li>{@link #selectLead} (EXTREME only): whether the AI picks its lead from the player's team before turn 1.</li>
 * </ul>
 * Everything else is identical at every difficulty, including the player model ({@link #useHistoryModel}) and sacking.
 * <p>
 * Tuning: the numeric defaults below are non-final statics ({@code D_*}) so {@link Tunables} can install a whole
 * parameter vector for self-play. A config copies them when it is constructed, so changing a static affects configs
 * created afterwards, not existing ones. There is deliberately no information-mode setting: battles are open team
 * sheet, so the AI always sees the player's full sets.
 */
public class AIConfig {
	/** How many branches the simulator keeps per cell (the lead refinement uses a smaller value, see {@link #leadLite}). */
	public int branchBudget = 4;

	// Defaults read by the fields below and by Tunables (which is why they are not final).
	static double D_ALPHA = 0.7, D_TEMP_EXPLOIT = 6.0, D_SACK_RATIO = 0.8, D_SACK_MIN_GAIN = 10, D_MIN_PROB = 0.02, D_LEAD_TEMPERATURE = 10.0, D_SWITCH_TAX = 25, D_LAST_DITCH_BONUS = 20;

	// Defaults for the evaluator's style weights (material is fixed at 1.0 and anchors the scale).
	static double D_W_MATCHUP = 1, D_W_HAZARD = 1, D_W_STATUS = 1, D_W_FIELD = 1, D_W_TEMPO = 1, D_W_PENDING = 1;
	/** Weights of the evaluator's terms, see {@link EvalWeights}. */
	public EvalWeights style = new EvalWeights(1.0, D_W_MATCHUP, D_W_HAZARD, D_W_STATUS, D_W_FIELD, D_W_TEMPO, D_W_PENDING);

	/**
	 * Use the player model (what this player tends to do) to blend an exploit into the equilibrium. false = pure
	 * equilibrium; exists so self-play can A/B it. true at every difficulty.
	 */
	public boolean useHistoryModel = true;
	/**
	 * Weight of the equilibrium in {@code x = alpha * x_eq + (1 - alpha) * x_exploit} once the model is fully confident;
	 * with little data it is pulled toward 1 ({@link PlayerModel#alphaEff}). So 0.7 means at most 30% of the mix chases
	 * the read. This is the main "how hard does the AI punish habits" knob.
	 */
	public double alpha = D_ALPHA;
	/** Sharpness of the exploit softmax over the rows' payoff against the predicted player mix (payoffs are scaled by their range, so it is unit-free). */
	public double temperatureExploit = D_TEMP_EXPLOIT;

	/** false for NORMAL (voluntary switching off, see the class doc); true for HARD and EXTREME. */
	public boolean allowVoluntarySwitch = true;
	/** NORMAL on a dead turn (nothing useful to click): force a swap instead of letting the matrix decide. Off by default. */
	public boolean deadTurnForcesSwitch = false;
	/** A sack candidate must be worth less than {@code sackRatio * value(active)} to be considered a sack. */
	public double sackRatio = D_SACK_RATIO;
	/**
	 * A sack row (a switch or pivot-switch into a sack-only candidate) must beat the best plain "stay and attack" row by at
	 * least this many eval points, averaged over the player's threat columns (over all columns when none threatens), or it
	 * is dropped before solving. Eval is roughly HP points (100 is about one healthy mon).
	 */
	public double sackMinGain = D_SACK_MIN_GAIN;
	/**
	 * false removes the AI's own sack candidates (and so the min-gain guard and the sack labels). Exists only so self-play can
	 * compare sacking on and off; every difficulty leaves it true.
	 */
	public boolean enableSacking = true;
	/** Cap on how many player switch columns the matrix includes. */
	public int maxPlayerSwitchCols = 4;
	/** Cap on how many AI switch rows the matrix includes. */
	public int maxAISwitchRows = 5;
	/** {@code Shaper.shape} cutoff: strategy entries below this probability are zeroed and the rest renormalized. */
	public double minProb = D_MIN_PROB;
	/** {@code Shaper.shape} mistake rate (chance of a random pick). 0 = off. */
	public double epsilon = 0.0;
	/** Always true: every cell runs the full simulator; there is no cheaper approximation. */
	public boolean useFullSim = true;
	/** EXTREME only: the AI picks its own lead from the player's team before turn 1 (see {@link LeadSelection}). */
	public boolean selectLead = false;
	/** Lead selection: how many top AI rows get a one-turn game value on top of the static lead matrix. */
	public int leadRefineKAi = 2;
	/** Lead selection: how many top player columns get a one-turn game value on top of the static lead matrix. */
	public int leadRefineKPlayer = 2;
	/** Lead selection: softmax temperature of the AI's lead choice (higher = closer to always the best lead). */
	public double leadTemperature = D_LEAD_TEMPERATURE;
	/**
	 * Flat eval cost charged to every plain switch row. Without it the AI switched about 15% of its turns without gaining
	 * anything measurable, because the one-turn evaluation undervalues the cost of giving up a free attack. Waived while the
	 * foe threatens a KO on the AI's active (see {@link MatrixBuilder}).
	 */
	public double switchTax = D_SWITCH_TAX;
	/**
	 * Extra credit for a non-lethal priority move that acts first against a player attack that outspeeds and likely KOs
	 * the AI's active: the last-ditch hit. Eval points per full foe HP removed. See {@link MatrixBuilder}.
	 */
	public double lastDitchBonus = D_LAST_DITCH_BONUS;

	/** NORMAL: {@link #allowVoluntarySwitch} off. */
	public static AIConfig normal() {
		AIConfig c = new AIConfig();
		c.allowVoluntarySwitch = false;
		return c;
	}

	/** HARD: the full engine. */
	public static AIConfig hard() {
		return new AIConfig();
	}

	/**
	 * The cheaper config the lead refinement builds each one-turn matrix with: no sacking, no history, smaller branch budget
	 * and switch caps. The tuning values are copied from this config.
	 */
	public AIConfig leadLite() {
		AIConfig c = new AIConfig();
		c.branchBudget = 2;
		c.useHistoryModel = false;
		c.enableSacking = false;
		c.allowVoluntarySwitch = allowVoluntarySwitch;
		c.deadTurnForcesSwitch = deadTurnForcesSwitch;
		c.maxAISwitchRows = Math.min(maxAISwitchRows, 3);
		c.maxPlayerSwitchCols = 1;
		c.sackRatio = sackRatio;
		c.sackMinGain = sackMinGain;
		c.alpha = alpha;
		c.temperatureExploit = temperatureExploit;
		c.minProb = minProb;
		c.epsilon = 0;
		c.style = style;
		c.useFullSim = useFullSim;
		c.switchTax = switchTax;
		c.lastDitchBonus = lastDitchBonus;
		return c;
	}

	/**
	 * The one place difficulty is decided: NORMAL gets {@link #normal()}, everything else {@link #hard()}, and EXTREME
	 * additionally selects its lead. In battle EXTREME plays exactly like HARD.
	 */
	public static AIConfig forDifficulty(int difficulty) {
		AIConfig c = difficulty == Player.NORMAL ? normal() : hard();
		c.selectLead = difficulty == Player.EXTREME;
		return c;
	}
}