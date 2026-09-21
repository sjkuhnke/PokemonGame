package pokemon;

/**
 * A Trainer AI engine. Lets the legacy AI and the overhauled AI run side by side
 * (self-play, A/B flags). Pokemon.bestMove2 dispatches here.
 */
public interface TrainerAI {

	/**
	 * Decide this Pokemon's action for the turn. Must keep the slot-encoding conventions of
	 * Pokemon.MoveDecision (see spec section 2) and must not change any real battle state.
	 *
	 * @param difficulty Player.NORMAL / Player.HARD / Player.EXTREME
	 */
	Pokemon.MoveDecision decide(Pokemon self, Pokemon foe, boolean first, int difficulty);

	String getName();

	/** Engine used by a Pokemon: its trainer's own {@code ai}, else the global default. */
	static TrainerAI forPokemon(Pokemon p) {
		if (p.trainer != null && p.trainer.ai != null) return p.trainer.ai;
		return Config.defaultAI;
	}

	/** Global default. Stays LegacyAI until the new engine ships behind a flag (Phase 3). */
	final class Config {
		public static volatile TrainerAI defaultAI = LegacyAI.INSTANCE;

		private Config() {}
	}
}