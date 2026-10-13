package pokemon;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Phase 8 (tuning half): registry of the placeholder constants the earlier phases left for self-play tuning, and a way to
 * install a whole parameter vector at once. The constants are plain non-final statics in their own classes (drop the
 * {@code final}; see PHASE8_CHANGES_PART2.md); this class finds them by reflection, so no call site changes.
 * <p>
 * A vector is a {@code double[]} in {@link #all()} order. {@link #apply(double[])} writes it into the statics; null means the
 * defaults captured when this class first loaded (so load it before anything has mutated them). SelfPlay applies a seat's
 * vector right before that seat decides, which is how two engines with different constants play each other in one process.
 * The SimCache tables hold no tunable-dependent values (edges and damage ranges only), so switching vectors never needs a clear.
 * Game thread only.
 */
public final class Tunables {
	private Tunables() {}

	public static final class Param {
		public final String name;
		public final double lo, hi, def;
		/** Multiplicative steps (positive scale-free weights) vs additive steps (fractions). */
		public final boolean log;
		private final java.lang.reflect.Field f;

		Param(Class<?> owner, String field, double lo, double hi, boolean log) throws ReflectiveOperationException {
			this.name = owner.getSimpleName() + "." + field;
			this.lo = lo;
			this.hi = hi;
			this.log = log;
			this.f = owner.getDeclaredField(field);
			this.f.setAccessible(true);
			if (Modifier.isFinal(f.getModifiers())) {
				throw new IllegalStateException(name + " is still final: remove 'final' (PHASE8_CHANGES_PART2.md, edit T1)");
			}
			this.def = f.getDouble(null);
		}

		public double get() {
			try {
				return f.getDouble(null);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		void set(double v) {
			try {
				f.setDouble(null, v);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		public double clamp(double v) {
			return Math.max(lo, Math.min(hi, v));
		}
	}

	private static final List<Param> ALL;

	static {
		List<Param> l = new ArrayList<>();
		try {
			l.add(new Param(Evaluator.class, "ESCAPE_COST", 5, 80, false));
			l.add(new Param(Evaluator.class, "HURT_ANSWER_PENALTY", 10, 120, false));
			l.add(new Param(Evaluator.class, "ENDURE_FRAC", 0.2, 0.9, false));
			l.add(new Param(Evaluator.class, "CHARGE_HOLD", 0.3, 1.0, false));
			l.add(new Param(MonWeights.class, "UNIQUE_W", 0.1, 2.0, true));
			l.add(new Param(MonWeights.class, "GAP", 0.1, 0.6, false));
			l.add(new Param(MonWeights.class, "UTILITY_MAX", 0.1, 1.2, false));
			l.add(new Param(MonWeights.class, "NORM_K", 0.1, 1.5, true));
			l.add(new Param(MonWeights.class, "NORM_SD_FLOOR", 0.3, 3.0, true));
			l.add(new Param(ReplacementChooser.class, "FUTURE_W", 1, 30, true));
			l.add(new Param(AIConfig.class, "D_ALPHA", 0.4, 0.95, false));
			l.add(new Param(AIConfig.class, "D_TEMP_EXPLOIT", 2, 12, true));
			l.add(new Param(AIConfig.class, "D_SACK_RATIO", 0.4, 1.0, false));
			l.add(new Param(AIConfig.class, "D_SACK_MIN_GAIN", 0, 40, false));
			l.add(new Param(AIConfig.class, "D_MIN_PROB", 0.0, 0.1, false));
			l.add(new Param(AIConfig.class, "D_LEAD_TEMPERATURE", 3, 25, true));
			l.add(new Param(AIConfig.class, "D_W_MATCHUP", 0.2, 3.0, true));
			l.add(new Param(AIConfig.class, "D_W_HAZARD", 0.2, 3.0, true));
			l.add(new Param(AIConfig.class, "D_W_STATUS", 0.2, 3.0, true));
			l.add(new Param(AIConfig.class, "D_W_FIELD", 0.2, 3.0, true));
			l.add(new Param(AIConfig.class, "D_W_TEMPO", 0.2, 3.0, true));
			l.add(new Param(AIConfig.class, "D_W_PENDING", 0.2, 3.0, true));
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Tunables: " + e, e);
		}
		ALL = Collections.unmodifiableList(l);
	}

	public static List<Param> all() {
		return ALL;
	}

	/** The values the statics had when this class loaded. */
	public static double[] defaults() {
		double[] v = new double[ALL.size()];
		for (int i = 0; i < v.length; i++) v[i] = ALL.get(i).def;
		return v;
	}

	/** Installs a vector (null = defaults). */
	public static void apply(double[] v) {
		for (int i = 0; i < ALL.size(); i++) ALL.get(i).set(v == null ? ALL.get(i).def : v[i]);
	}

	/** Only the entries that differ from the defaults. */
	public static String describe(double[] v) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ALL.size(); i++) {
			Param p = ALL.get(i);
			if (Math.abs(v[i] - p.def) < 1e-12) continue;
			sb.append(String.format(Locale.ROOT, "%s=%.4g (default %.4g)%n", p.name, v[i], p.def));
		}
		return sb.length() == 0 ? "(all defaults)\n" : sb.toString();
	}
}