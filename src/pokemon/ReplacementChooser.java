package pokemon;

import java.util.HashMap;
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
 * Weights are computed from the two shells in the state being chosen in (fainted mons excluded), so the real battle and
 * the simulator see identical inputs. They are cached by team composition (species, stats, item, ability, moves, types,
 * fainted flags, weather) because a matrix can ask for many replacements over a handful of distinct compositions.
 * Game thread only, like the rest of the battle code.
 */
public final class ReplacementChooser {
	/** Points of matchupScore one unit of (weight * hpFrac) is worth when ranking replacements. Tuned in Phase 8. */
	public static final double FUTURE_W = 8.0;
	private static final int CACHE_MAX = 128;
	private static final HashMap<Long, double[]> cache = new HashMap<>();

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
			double s = score(side, i, foe, field, w);
			if (log) sb.append(String.format(Locale.ROOT, "[%s: %.1f], ", p, s));
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

	/** side's per-slot weights in the current state, or null when the foe has no trainer (wild battle: every weight 1). */
	private static double[] futureWeights(Trainer side, Pokemon foe, Field field) {
		Trainer foeTrainer = foe.trainer;
		if (foeTrainer == null || field == null) return null;
		long k = key(side, foeTrainer, field);
		double[] w = cache.get(k);
		if (w == null) {
			w = MonWeights.forSide(new SideState(side), new SideState(foeTrainer), field);
			if (cache.size() >= CACHE_MAX) cache.clear();
			cache.put(k, w);
		}
		return w;
	}

	private static long key(Trainer a, Trainer b, Field field) {
		long h = 1125899906842597L;
		h = foldTeam(h, a);
		h = foldTeam(h, b);
		return fold(h, field.weather == null ? -1 : field.weather.effect.ordinal());
	}

	private static long foldTeam(long h, Trainer t) {
		for (Pokemon p : t.team) {
			if (p == null) {
				h = fold(h, -7);
				continue;
			}
			h = fold(h, p.id);
			h = fold(h, p.fainted ? 1 : 0);
			for (int s = 0; s < 6; s++) h = fold(h, p.getStat(s));
			h = fold(h, p.type1 == null ? -1 : p.type1.ordinal());
			h = fold(h, p.type2 == null ? -1 : p.type2.ordinal());
			h = fold(h, p.item == null ? -1 : p.item.ordinal());
			h = fold(h, p.ability == null ? -1 : p.ability.ordinal());
			if (p.moveset != null) {
				for (Moveslot ms : p.moveset) h = fold(h, ms == null || ms.move == null ? -1 : ms.move.ordinal() * 2 + (ms.currentPP > 0 ? 1 : 0));
			}
		}
		return h;
	}

	private static long fold(long h, long v) {
		h ^= v + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2);
		return h;
	}
}