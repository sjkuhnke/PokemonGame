package pokemon;

/** The pre-overhaul AI: forwards to the untouched {@code Pokemon.legacyBestMove}. */
public final class LegacyAI implements TrainerAI {
	public static final LegacyAI INSTANCE = new LegacyAI();

	private LegacyAI() {}

	@Override
	public MoveDecision decide(Pokemon self, Pokemon foe, boolean first, int difficulty) {
		return self.legacyBestMove(foe, first, difficulty);
	}

	@Override
	public String getName() {
		return "Legacy";
	}
}