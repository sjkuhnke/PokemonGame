package pokemon;

import java.util.Collections;

/**
 * Phase 4 (spec §7.14.3 stopgap, §15.6): the thin, NON-recursive replacement for the legacy
 * {@code Pokemon.evaluateSwitchInScore} stack. Phase 5: it is the entry-quality half of {@link ReplacementChooser}
 * (the one forced-replacement function shared by the real battle and the simulator); {@code Trainer.pickLead} and
 * {@code predictPlayerLeads} still call it directly until Phase 7 replaces the lead uses.
 * <p>
 * score = {@code matchupScore} of the candidate against {@code foe} AFTER it has switched in (entry hazards and entry
 * abilities applied, in a private sim shell and a {@link SimContext} so no Task/UI/recorder/RNG side effects), from the
 * two sides' best expected-damage moves ({@link Evaluator#bestRange}) and who moves first. Higher is better. The scale
 * is {@code matchupScore}'s (about -90..+90), the same scale {@code weightedRandomSelection} and the averaging in
 * {@code pickLead} already worked with.
 * <p>
 * Phase 5: a candidate that faints on entry scores the FINITE {@link #ENTRY_FAINT_SCORE} instead of the old
 * {@code Integer.MIN_VALUE + 1} sentinel (§7.14.3). It still ranks below every survivor, but it is an ordinary number:
 * averaging it in {@code pickLead} no longer overflows/dominates, and the simulator prices a fainting switch-in through
 * material and the free replacement, not through this score.
 * <p>
 * The candidate is entered on a shell of ITS OWN SIDE ({@link #scoreOn}); {@code candidate.trainer} is not trusted
 * inside the simulator, where a shared bench mon still points at the shell it was forked from and would read that
 * shell's (older) hazards.
 * <p>
 * Deliberately NOT modeled (the legacy version did, via the recursion this replaces): the hit the candidate would take
 * this turn, and follow-up switching by either side.
 */
public final class SwitchInScorer {
	/** Phase 5: finite "faints on entry" score. Below any matchupScore (about -90..+90), far from int overflow. */
	public static final int ENTRY_FAINT_SCORE = -200;

	private SwitchInScorer() {}

	/** Legacy entry point (evaluateSwitchInScore, pickLead, predictPlayerLeads): scores {@code candidate} on its own trainer. */
	static int score(Pokemon candidate, Pokemon foe, Field field) {
		Trainer real = candidate.trainer;
		int idx = real == null ? -1 : real.indexOf(candidate);
		if (idx >= 0) return scoreOn(real, idx, foe, field);
		return scoreEntered(null, candidate, foe, field);
	}

	/** Scores team slot {@code idx} of {@code side} (a real trainer or a sim shell) against {@code foe}. */
	static int scoreOn(Trainer side, int idx, Pokemon foe, Field field) {
		return scoreEntered(side, side.team[idx], foe, field);
	}

	private static int scoreEntered(Trainer side, Pokemon candidate, Pokemon foe, Field field) {
		Field prevField = Pokemon.field;
		Field fc = field.clone();
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			Pokemon.field = fc;

			// Copy-on-write shell: only the active slot and the candidate are cloned (hazards live on the shell's effects).
			Pokemon me;
			if (side != null) {
				int idx = side.indexOf(candidate);
				me = side.simShell(Collections.singleton(idx)).team[idx];
			} else {
				me = candidate.fullClone();
			}
			Pokemon foeClone = foe.fullClone();

			me.swapIn(foeClone, true, fc);
			if (me.isFainted() || me.currentHP <= 0) return ENTRY_FAINT_SCORE;

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