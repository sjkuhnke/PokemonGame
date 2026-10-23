package ai;

import java.util.Collections;

import pokemon.DamageRange;
import pokemon.Field;
import pokemon.Pokemon;
import pokemon.Trainer;

/**
 * The entry-quality half of {@link ReplacementChooser} (the one forced-replacement function shared by the real battle and
 * the simulator): how good is this bench mon against the foe once it has been sent in?
 * <p>
 * score = {@code matchupScore} of the candidate against {@code foe} AFTER it has switched in (entry hazards and entry
 * abilities applied, in a private sim shell and a {@link SimContext} so no Task/UI/recorder/RNG side effects), from the
 * two sides' best expected-damage moves ({@link Evaluator#bestRange}) and who moves first. Higher is better. The scale
 * is {@code matchupScore}'s (about -90..+90).
 * <p>
 * A candidate that faints on entry scores the FINITE {@link #ENTRY_FAINT_SCORE}: it ranks below every survivor but is an
 * ordinary number, so averaging it never overflows or dominates. The simulator prices a fainting switch-in through
 * material and the free replacement, not through this score.
 * <p>
 * The candidate is entered on a shell of ITS OWN SIDE ({@link #scoreOn}); {@code candidate.trainer} is not trusted
 * inside the simulator, where a shared bench mon still points at the shell it was forked from and would read that
 * shell's (older) hazards.
 * <p>
 * Deliberately NOT modeled: the hit the candidate would take this turn, and follow-up switching by either side.
 */
public final class SwitchInScorer {
	/** Finite "faints on entry" score. Below any matchupScore (about -90..+90), far from int overflow. */
	public static final int ENTRY_FAINT_SCORE = -200;

	private SwitchInScorer() {}

	/** Scores {@code candidate} on its own trainer. Used by {@code Pokemon.evaluateSwitchInScore}, which only tests still call. */
	public static int score(Pokemon candidate, Pokemon foe, Field field) {
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