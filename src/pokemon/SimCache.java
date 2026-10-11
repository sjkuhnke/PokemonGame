package pokemon;

import java.util.HashMap;
import java.util.List;

/**
 * Phase 8 (§7.9 "caches", §10): content-keyed memo tables for the two hottest pure computations.
 * <ul>
 * <li><b>Edge cache</b>: {@code MonWeights}' full-HP 1v1 edge for an ordered pair of mons. Key = {@link #monKey}(base) of both,
 * plus the field. HP, status and stages are NOT in the key (the baseline resets them), so one entry serves a whole battle.</li>
 * <li><b>Range cache</b>: {@code Evaluator.bestRange} and {@code calcRange} for a live pair. Key = {@link #monKey}(full) of both
 * (HP, status, stages included), the move and the field.</li>
 * </ul>
 * The keys are 64-bit hashes of every input those computations read (audited against computeDamage / getSpeed / getValidMoveset).
 * Because a missed input would be a SILENT wrong answer, {@link #VERIFY} recomputes on every hit and counts mismatches; run
 * Phase8Tests with it on after any change to damage code. Cached values are immutable (a double, or a DamageRange).
 * Game thread only. Bounded: a table is cleared when it reaches MAX_ENTRIES.
 */
public final class SimCache {
	private SimCache() {}

	/** When true every hit is recomputed and compared (slow; for tests). */
	public static boolean VERIFY = false;
	public static long verifyChecked, verifyMismatches;

	private static final int MAX_ENTRIES = 1 << 15;
	private static final HashMap<Long, Double> EDGE = new HashMap<>();
	private static final HashMap<Long, DamageRange> RANGE = new HashMap<>();
	/** Stands in for "bestRange returned null" (identity compare; DamageRange.unusable() makes a new object each call). */
	private static final DamageRange NULL_MARK = DamageRange.unusable();

	public static void clear() {
		EDGE.clear();
		RANGE.clear();
	}

	public static void resetVerify() {
		verifyChecked = 0;
		verifyMismatches = 0;
	}

	// ---- hashing ----

	static long fold(long h, long v) {
		h ^= v + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2);
		return h;
	}

	/** Weather, terrain and every global field effect (turn counters excluded: they never change damage). */
	public static long fieldKey(Field f) {
		long h = 0x9E37L;
		h = fold(h, f.weather == null ? -1 : f.weather.effect.ordinal());
		h = fold(h, f.terrain == null ? -1 : f.terrain.effect.ordinal());
		long s = 0;
		for (Field.FieldEffect fe : f.fieldEffects) s += fold(7, fe.effect.ordinal());
		return fold(h, s);
	}

	/** The side effects that change damage or speed (screens, Lucky Chant, Tailwind) on p's own side. Order-independent. */
	private static long sideKey(Pokemon p) {
		List<Field.FieldEffect> list;
		try {
			list = p.getFieldEffects();
		} catch (IllegalStateException e) {
			return 0;
		}
		long s = 0;
		for (Field.FieldEffect fe : list) {
			Field.Effect e = fe.effect;
			if (e == Field.Effect.REFLECT || e == Field.Effect.LIGHT_SCREEN || e == Field.Effect.AURORA_VEIL
					|| e == Field.Effect.LUCKY_CHANT || e == Field.Effect.TAILWIND) {
				s += fold(0x5EED, e.ordinal());
			}
		}
		return s;
	}

	/**
	 * Everything calcRange / getValidMoveset / getSpeed read from a mon. {@code full == false} is the "baseline" key (no HP,
	 * status or stat stages: MonWeights resets those). Add a line here when damage code starts reading a new field.
	 */
	public static long monKey(Pokemon p, boolean full) {
		long h = 0x51ED270B1A2B3C4DL;
		h = fold(h, p.id);
		h = fold(h, p.level);
		for (int i = 0; i < 6; i++) h = fold(h, p.getStat(i));
		h = fold(h, p.type1 == null ? -1 : p.type1.ordinal());
		h = fold(h, p.type2 == null ? -1 : p.type2.ordinal());
		h = fold(h, p.ability == null ? -1 : p.ability.ordinal());
		h = fold(h, p.item == null ? -1 : p.item.ordinal());
		h = fold(h, Double.doubleToLongBits(p.weight));
		h = fold(h, p.happiness);
		for (int iv : p.ivs) h = fold(h, iv);
		boolean beatUp = false;
		for (Moveslot ms : p.moveset) {
			if (ms == null || ms.move == null) {
				h = fold(h, -1);
			} else {
				h = fold(h, ms.move.ordinal() * 2L + (ms.currentPP > 0 ? 1 : 0));
				if (ms.move == Move.BEAT_UP) beatUp = true;
			}
		}
		h = fold(h, p.disabledMove == null ? -1 : p.disabledMove.ordinal());
		h = fold(h, p.lastMoveUsed == null ? -1 : p.lastMoveUsed.ordinal());
		h = fold(h, p.choiceMove == null ? -1 : p.choiceMove.ordinal());
		h = fold(h, (p.impressive ? 1 : 0) | (p.illusion ? 2 : 0) | (p.abilityFlag ? 4 : 0) | (p.consumedItem ? 8 : 0) | (p.script ? 16 : 0));
		h = fold(h, p.metronome);
		h = fold(h, p.moveMultiplier);
		h = fold(h, p.rollCount);
		for (StatusEffect se : p.vStatuses) {
			h = fold(h, se.status.ordinal());
			h = fold(h, se.num);
			h = fold(h, se.move == null ? -1 : se.move.ordinal());
		}
		h = fold(h, sideKey(p));
		if (beatUp && p.trainer != null && p.trainer.team != null) { // hit count depends on the party
			for (Pokemon q : p.trainer.team) h = fold(h, q == null || q.isFainted() ? -1 : (q.status == null ? 0 : q.status.ordinal() + 1));
		}
		if (full) {
			h = fold(h, p.currentHP);
			h = fold(h, p.status == null ? -1 : p.status.ordinal());
			for (int s : p.statStages) h = fold(h, s);
		}
		return h;
	}

	// ---- edge cache ----

	static long edgeKey(long mKey, long qKey, long fKey) {
		return fold(fold(fold(0xED6EL, mKey), qKey), fKey);
	}

	static Double edgeGet(long k) {
		return EDGE.get(k);
	}

	static void edgePut(long k, double v) {
		if (EDGE.size() >= MAX_ENTRIES) EDGE.clear();
		EDGE.put(k, v);
	}

	static void verifyEdge(double cached, double fresh) {
		verifyChecked++;
		if (Math.abs(cached - fresh) > 1e-9) {
			verifyMismatches++;
			if (verifyMismatches <= 5) System.err.println("[SimCache] EDGE MISMATCH cached=" + cached + " fresh=" + fresh);
		}
	}

	// ---- range cache ----

	private static void rangePut(long k, DamageRange r) {
		if (RANGE.size() >= MAX_ENTRIES) RANGE.clear();
		RANGE.put(k, r);
	}

	/** Cached {@code Evaluator.bestRangeRaw}. May return null (nothing lands), like the raw method. */
	public static DamageRange bestRange(Pokemon a, Pokemon d, Field f) {
		long k = fold(fold(fold(0xB357L, monKey(a, true)), monKey(d, true)), fieldKey(f));
		DamageRange hit = RANGE.get(k);
		if (hit != null && !VERIFY) {
			Perf.count(Perf.RANGE_HIT);
			return hit == NULL_MARK ? null : hit;
		}
		Perf.count(Perf.RANGE_MISS);
		DamageRange r = Evaluator.bestRangeRaw(a, d, f);
		if (hit != null) verifyRange(hit == NULL_MARK ? null : hit, r, "bestRange");
		rangePut(k, r == null ? NULL_MARK : r);
		return r;
	}

	/** Cached {@code a.calcRange(d, m, first, f)}. */
	public static DamageRange calcRange(Pokemon a, Pokemon d, Move m, boolean first, Field f) {
		long k = fold(fold(fold(fold(fold(0xCA1CL, monKey(a, true)), monKey(d, true)), fieldKey(f)), m.ordinal()), first ? 1 : 0);
		DamageRange hit = RANGE.get(k);
		if (hit != null && !VERIFY) {
			Perf.count(Perf.RANGE_HIT);
			return hit;
		}
		Perf.count(Perf.RANGE_MISS);
		DamageRange r = a.calcRange(d, m, first, f);
		if (hit != null) verifyRange(hit, r, "calcRange");
		rangePut(k, r);
		return r;
	}

	private static void verifyRange(DamageRange c, DamageRange f, String what) {
		verifyChecked++;
		boolean same = (c == null) == (f == null);
		if (same && c != null) {
			same = c.usable == f.usable && c.immune == f.immune && c.reflected == f.reflected && c.min == f.min && c.avg == f.avg
					&& c.max == f.max && c.accuracy == f.accuracy && c.hits == f.hits && c.critProb == f.critProb
					&& c.expectedPerHit() == f.expectedPerHit();
		}
		if (!same) {
			verifyMismatches++;
			if (verifyMismatches <= 5) System.err.println("[SimCache] " + what + " MISMATCH cached=" + c + " fresh=" + f);
		}
	}
}