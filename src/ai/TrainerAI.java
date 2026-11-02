package ai;

import pokemon.Pokemon;

/**
 * A Trainer AI engine: given the two active Pokemon, picks this Pokemon's action for the turn. {@code Pokemon.bestMove2}
 * dispatches here, so engines can run side by side (self-play A/B comparisons, scripted opponents, per-trainer overrides).
 * The shipped engine is {@link AIV2}.
 */
public interface TrainerAI {

	/**
	 * Decide this Pokemon's action for the turn. Must follow the slot-encoding conventions of {@link MoveDecision} (a plain
	 * switch is a SWAP status with a 1-based slot, a pivot a TEMP_SWITCHING status with a negative one) and must not change
	 * any real battle state.
	 *
	 * @param difficulty Player.NORMAL / Player.HARD / Player.EXTREME
	 */
	MoveDecision decide(Pokemon self, Pokemon foe, int difficulty);

	/** A short name for reports and logs. Two engines that compare against each other should return different names. */
	String getName();

	/** Engine used by a Pokemon: its trainer's own {@code ai}, else the global default. */
	static TrainerAI forPokemon(Pokemon p) {
		if (p.trainer != null && p.trainer.ai != null) return p.trainer.ai;
		return Config.defaultAI;
	}

	/** Holder of the global default engine. */
	final class Config {
		public static volatile TrainerAI defaultAI = AIV2.INSTANCE;

		private Config() {}
	}
}