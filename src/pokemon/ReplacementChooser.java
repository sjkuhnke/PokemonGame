package pokemon;

import java.util.Locale;

import util.Print;

/**
 * Phase 5 (§7.14.3): the ONE non-recursive forced-replacement chooser. {@code Trainer.getNext2} (the real battle, every
 * difficulty, both seats in self-play) and {@code BattleSimulator.resolveReplacementsForSide} / the Eject-style switch
 * (the matrix) both call {@link #pickSlot}, so the replacement the AI plans for after a sack is the replacement the real
 * game brings out (T22). It replaces {@code BattleSimulator.cheapReplacementSlot} (a type-chart proxy) and the
 * {@code Integer.MIN_VALUE} best-score loop that used to live in {@code getNext2}.
 * <p>
 * score(r) = {@link SwitchInScorer#scoreOn} (matchup after entry: hazards and entry abilities applied, against the foe's
 * POST-turn active) minus {@link #FUTURE_W} * weight(r) * hpFrac(r). The second term is r's future value
 * ({@link MonWeights}, the same weights that decide who is sackable): when two answers are about equally good right now,
 * the less valuable one comes in and the more valuable one is kept for later. A candidate that faints on entry scores
 * {@link SwitchInScorer#ENTRY_FAINT_SCORE} (finite, no sentinel semantics).
 * <p>
 * {@code MonWeights.forSide} is recomputed on every call, uncached (Phase 4 note: no caching this phase - correctness
 * first, tune in Phase 8). A per-decision cache was tried and dropped: its key did not (and, short of hashing the two
 * sides' full hazard/screen lists and every bench mon's status, cannot cheaply) capture everything {@code forSide}'s
 * utility term reads, so a stale hit was a live risk - not worth it for a call this infrequent (once per faint / free
 * switch). Game thread only, like the rest of the battle code.
 */
public final class ReplacementChooser {
	/** Points of matchupScore one unit of (weight * hpFrac) is worth when ranking replacements. Tuned in Phase 8. */
	public static final double FUTURE_W = 8.0;

	private ReplacementChooser() {}

	/** Team index of the best alive non-current member of {@code side} against {@code foe}, or -1 if there is none. */
	public static int pickSlot(Trainer side, Pokemon foe, Field field) {
		Pokemon[] team = side.team;
		double[] w = futureWeights(side, foe, field);
		boolean log = !SimContext.active() && !Print.isDebugSuppressed();
		StringBuilder sb = log ? new StringBuilder("\n______________\nREPLACEMENT\n______________\n") : null;

		int best = -1;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < team.length; i++) {
			Pokemon p = team[i];
			if (p == null || p.isFainted() || p == side.current) continue;
			int entry = SwitchInScorer.scoreOn(side, i, foe, field);
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			double weight = (w != null && i < w.length) ? w[i] : 1.0;
			double s = combine(entry, weight, hpFrac);
			// Breaks the combined score into entry (SwitchInScorer, the matchup-after-entry term) and weight
			// (MonWeights.forSide, the future-value term), so a replacement that looks surprising can be traced to
			// which half drove it.
			if (log) sb.append(String.format(Locale.ROOT, "[%s: entry=%d weight=%.3f hpFrac=%.3f -> %.1f], ", p, entry, weight, hpFrac, s));
			if (s > bestScore) {
				bestScore = s;
				best = i;
			}
		}
		if (log) Print.debug(sb.append("\n\n").toString());
		return best;
	}

	static double score(Trainer side, int idx, Pokemon foe, Field field, double[] w) {
		Pokemon p = side.team[idx];
		double hpFrac = p.currentHP * 1.0 / p.getStat(0);
		double weight = (w != null && idx < w.length) ? w[idx] : 1.0;
		return combine(SwitchInScorer.scoreOn(side, idx, foe, field), weight, hpFrac);
	}

	/** Pure: entry quality minus the future value of spending this mon now. */
	public static double combine(int entryScore, double weight, double hpFrac) {
		return entryScore - FUTURE_W * weight * hpFrac;
	}

	/**
	 * side's per-slot weights in the current state, freshly computed; null when the foe has no trainer (wild battle:
	 * every weight 1). Pins the static {@link Pokemon#field} to {@code field} for the duration, the same way
	 * {@link SwitchInScorer} pins it for entry scoring: MonWeights.forSide's damage/type calls go through ordinary
	 * Pokemon methods that read the static field, not only the explicit parameter, so a caller deep inside
	 * {@link BattleSimulator#simulateTurn} (where the static field can have drifted from this specific branch's field
	 * by the time replacements are resolved) needs it pinned explicitly - a direct call from outside the simulator
	 * happens to work without this only because nothing else has touched the static field yet.
	 */
	private static double[] futureWeights(Trainer side, Pokemon foe, Field field) {
		Trainer foeTrainer = foe.trainer;
		if (foeTrainer == null || field == null) return null;
		Field prevField = Pokemon.field;
		try {
			Pokemon.field = field;
			return MonWeights.forSide(new SideState(side), new SideState(foeTrainer), field);
		} finally {
			Pokemon.field = prevField;
		}
	}
}