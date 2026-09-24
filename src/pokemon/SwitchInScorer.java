package pokemon;

/**
 * Phase 4 (spec §7.14.3 stopgap, §15.6): the thin, NON-recursive replacement for the legacy
 * {@code Pokemon.evaluateSwitchInScore} stack. Still used by {@code Trainer.getNext2} (the real forced replacement after
 * a faint, every difficulty), {@code Trainer.pickLead} and {@code predictPlayerLeads}; Phase 5 folds it into the shared
 * {@code chooseReplacement} and Phase 7 replaces the lead uses.
 * <p>
 * score = {@code matchupScore} of the candidate against {@code foe} AFTER it has switched in (entry hazards and entry
 * abilities applied, in a private sim shell and a {@link SimContext} so no Task/UI/recorder/RNG side effects), from the
 * two sides' best expected-damage moves ({@link Evaluator#bestRange}) and who moves first. A candidate that faints on
 * entry scores {@code Integer.MIN_VALUE + 1}, as the legacy scorer did. Higher is better. The scale is
 * {@code matchupScore}'s (about -90..+90), the same scale {@code weightedRandomSelection} and the averaging in
 * {@code pickLead} already worked with.
 * <p>
 * Deliberately NOT modeled (the legacy version did, via the recursion this replaces): the hit the candidate would take
 * this turn, and follow-up switching by either side.
 */
final class SwitchInScorer {
	private SwitchInScorer() {}

	static int score(Pokemon candidate, Pokemon foe, Field field) {
		Field prevField = Pokemon.field;
		Field fc = field.clone();
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			Pokemon.field = fc;

			Trainer real = candidate.trainer;
			int idx = real == null ? -1 : real.indexOf(candidate);
			Pokemon me = idx >= 0 ? real.simShell().team[idx] : candidate.fullClone();
			Pokemon foeClone = foe.fullClone();

			me.swapIn(foeClone, true, fc);
			if (me.isFainted() || me.currentHP <= 0) return Integer.MIN_VALUE + 1;

			DamageRange myR = Evaluator.bestRange(me, foeClone, fc);
			DamageRange foeR = Evaluator.bestRange(foeClone, me, fc);
			boolean iAmFaster = me.getFaster(foeClone, 0, 0, fc) == me;
			double myFrac = Evaluator.fracOf(myR, foeClone.currentHP);
			double foeFrac = Evaluator.fracOf(foeR, me.currentHP);
			return (int) Math.round(me.matchupScore((int) Math.round(myFrac * 100), foeFrac * 100, iAmFaster));
		} finally {
			Pokemon.field = prevField;
		}
	}
}
