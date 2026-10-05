package pokemon;

import java.util.ArrayList;

/**
 * Phase 6 test support (lives with SelfPlay, not in the shipped decision path): fixed-habit opponents for measuring
 * whether the AI's player model actually exploits a habit. Seated as the "player" against {@link AIV2} in self-play.
 * <ul>
 * <li>{@link Style#SAFE_SWITCHER}: whenever the foe's active threatens a KO on it, switches to the replacement
 * {@link ReplacementChooser} likes best; otherwise clicks its best attack. The "always plays safe" player.</li>
 * <li>{@link Style#STAYER}: never switches, clicks its best attack. The "always stays in" player.</li>
 * </ul>
 * Deterministic: no Rng, no state.
 */
public final class ScriptedAI implements TrainerAI {
	public enum Style { SAFE_SWITCHER, STAYER }

	public static final ScriptedAI SAFE_SWITCHER = new ScriptedAI(Style.SAFE_SWITCHER);
	public static final ScriptedAI STAYER = new ScriptedAI(Style.STAYER);

	private final Style style;

	private ScriptedAI(Style style) {
		this.style = style;
	}

	@Override
	public String getName() {
		return "Scripted " + style;
	}

	@Override
	public MoveDecision decide(Pokemon self, Pokemon foe, boolean first, int difficulty) {
		ArrayList<Move> valid = self.getValidMoveset();
		if (valid.isEmpty()) return new MoveDecision(Move.STRUGGLE);

		if (style == Style.SAFE_SWITCHER && self.trainer.canSwitch(foe) && SackAnalysis.threatens(foe, self, Pokemon.field)) {
			int slot = ReplacementChooser.pickSlot(self.trainer, foe, Pokemon.field);
			if (slot >= 0) return new MoveDecision(Move.GROWL, Status.SWAP, slot + 1); // MoveDecision slot encoding is 1-based (spec §2)
		}
		Move best = bestAttack(self, foe, Pokemon.field);
		return new MoveDecision(best != null ? best : valid.get(0));
	}

	/** The damaging move with the highest expected damage against {@code foe} (first on ties), or null when nothing does damage. */
	private static Move bestAttack(Pokemon self, Pokemon foe, Field field) {
		Move best = null;
		double bestDmg = 0;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			for (Move m : self.getValidMoveset()) {
				if (m.cat == 2) continue;
				DamageRange r = self.calcRange(foe, m, true, field);
				if (!r.usable || r.immune) continue;
				double d = r.expectedCapped(foe.currentHP);
				if (d > bestDmg) {
					bestDmg = d;
					best = m;
				}
			}
		}
		return best;
	}
}