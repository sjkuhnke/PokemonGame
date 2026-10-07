package pokemon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * Phase 6 (§7.11). What the Trainer AI has learned about HOW a player plays: not which moves they own (the AI always
 * sees the full sets, like the player sees the AI's), but their habits - when the simulation says switching is their
 * best reply, do they switch or stay in; do they pick the move/switch-in the simulation likes best or something else.
 * The AI uses it to hedge less or more against switching than the plain equilibrium says, so trainers get harder the
 * more of the player's run they have seen. "Best" is always the AI's own payoff matrix against its equilibrium mix
 * (see {@link PlayerReader}), never a type-chart proxy.
 * <p>
 * <b>Storage.</b> A fixed table of {@code (48 fine + 4 coarse + 1 global) situation slots x 5 action classes} floats
 * (about 1.2 KB serialized), kept on {@code Player} and saved with it. It cannot grow: every observation decays the
 * slots it touches by {@link #DECAY}, so a slot holds at most {@code 1 / (1 - DECAY)} observations' worth and old
 * habits fade if the player changes style.
 * <p>
 * <b>Confidence.</b> The estimate for a situation is shrunk toward its coarse bucket, which is shrunk toward the global
 * habit ({@link #estimate}); {@link #blendWeight} turns the effective sample size into how much the AI trusts it. An
 * empty model has weight 0 and every function here then returns the plain equilibrium unchanged.
 * <p>
 * This class has no engine dependencies on purpose: the engine glue (situations, classifying columns, watching what the
 * player did) is {@link PlayerReader}. Everything static here is pure and deterministic (no Rng).
 */
public final class PlayerModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * What the player did on a turn, as far as the AI can hedge on it. "Best" = the player column with the lowest AI payoff
	 * against the AI's equilibrium mix, i.e. the reply the full simulation likes most for them. A pivot move counts as a
	 * stay (classed by its move). "Best" is a band, not a single winner: every column within
	 * {@link PlayerReader#BEST_TOL} eval points of the lowest counts, so near-ties are all best. The order is part of the
	 * saved layout: append, never reorder.
	 */
	public enum ActionClass {
		/** Stayed in and used a move from the player's best stay columns (Protect-family moves excluded, see STAY_PROTECT). */
		STAY_BEST,
		/** Stayed in and used any other non-Protect move. */
		STAY_OTHER,
		/** Switched to a mon from the player's best switch columns. */
		SWITCH_BEST,
		/** Switched to any other mon (including one the AI had no column for). */
		SWITCH_OTHER,
		/**
		 * Stayed in and used a Protect-family move. Its own class because it is a different decision (scouting, stalling,
		 * burning a move) whose meaning comes from the situation it was clicked in, notably whether the AI threatened
		 * anything that turn, which is already part of the bucket.
		 */
		STAY_PROTECT
	}

	public static final int CLASSES = ActionClass.values().length;
	/**
	 * 2 (AI threatens the player's active) x 2 (the simulation says switching is the player's better reply) x 3 (player
	 * active's HP band) x 2 (AI outspeeds) x 2 (the player has a Protect-family move available, so a Protect habit is
	 * measured per turn it COULD protect and not diluted by mons without one).
	 */
	public static final int FINE = 48;
	/** The (threatened, switchBetter) pair: fine / 12. */
	public static final int COARSE = 4;
	static final int COARSE_BASE = FINE;
	static final int GLOBAL = FINE + COARSE;
	static final int SLOTS = GLOBAL + 1;

	/** §7.11's K: observations at which the AI trusts the history as much as the equilibrium. Placeholder, Phase 8. */
	public static final double K_BLEND = 6.0;
	/** Pseudo-observations a bucket borrows from its parent (fine from coarse, coarse from global). Placeholder, Phase 8. */
	public static final double K_PARENT = 4.0;
	/** Per-observation forgetting of the slots an observation touches. 0.97 caps a slot at about 33 observations. Placeholder, Phase 8. */
	public static final double DECAY = 0.97;

	private float[] counts = new float[SLOTS * CLASSES];

	/** Per-battle bookkeeping for {@link PlayerReader}; never serialized (a battle that ends mid-turn just forgets it). */
	transient Pending pending;

	/** What the AI saw when it last decided: just enough to say, next turn, what the player did. Typed Object so this class stays engine-free. */
	static final class Pending {
		Object ai;          // the AI's Trainer (a new trainer = a new battle)
		Object prev;        // the player's active Pokemon at decision time
		int turn;           // Field.turns at decision time (it going DOWN means a new battle)
		int fine;           // situation bucket at decision time
		Object bestMoves;   // the moves of the player's best stay columns (a Set of Move), never null
		// WHICH MONS, by identity, never slot numbers: Player.swapToFront reorders team[] when the player switches (the
		// active mon always moves to slot 0), so a slot index remembered from decision time points at a different mon by
		// the time the switch is observed. Trainer.swap (the AI) does not reorder, which is why only a real player hit this.
		Object bestBackMons; // identity Set<Pokemon>: the mons of the player's best switch columns
		Object switchColMons; // identity Set<Pokemon>: every mon that had a switch column this turn
		int[] pp;           // prev's per-move-slot PP, -1 for an empty slot
	}

	public PlayerModel() {}

	// ---- situation index ----

	public static int fineIndex(boolean threatened, boolean switchBetter, int hpBand, boolean aiFaster, boolean protectAvailable) {
		if (hpBand < 0 || hpBand > 2) throw new IllegalArgumentException("hpBand " + hpBand);
		return ((((threatened ? 1 : 0) * 2 + (switchBetter ? 1 : 0)) * 3 + hpBand) * 2 + (aiFaster ? 1 : 0)) * 2 + (protectAvailable ? 1 : 0);
	}

	// ---- recording ----

	/** Records one observed player action in a situation. Decays, then increments, the fine slot, its coarse slot and the global slot. */
	public void record(int fine, ActionClass c) {
		checkFine(fine);
		bump(fine, c);
		bump(COARSE_BASE + fine / 12, c);
		bump(GLOBAL, c);
	}

	private void bump(int slot, ActionClass c) {
		int b = slot * CLASSES;
		for (int k = 0; k < CLASSES; k++) counts[b + k] *= DECAY;
		counts[b + c.ordinal()] += 1f;
	}

	/** Observations' worth of data in total (the global slot). 0 for a model that has seen nothing. */
	public double total() {
		return slotTotal(GLOBAL);
	}

	private double slotTotal(int slot) {
		double s = 0;
		for (int k = 0; k < CLASSES; k++) s += counts[slot * CLASSES + k];
		return s;
	}

	// ---- estimate ----

	public static final class Estimate {
		/** Smoothed distribution over {@link ActionClass}, sums to 1. Uniform when there is no data at all. */
		public final double[] dist;
		/** Effective sample size behind it (fine count plus what it borrows from coarse and global, at most {@link #K_PARENT} each level). */
		public final double eff;

		Estimate(double[] dist, double eff) {
			this.dist = dist;
			this.eff = eff;
		}
	}

	/**
	 * Hierarchical shrinkage: global habit -> coarse bucket -> fine bucket. Each level is
	 * {@code (own counts + K_PARENT * parent) / (own n + K_PARENT)}, and its effective sample size is
	 * {@code own n + K_PARENT * parentEff / (parentEff + K_PARENT)}. That is what lets a bucket the player has rarely hit
	 * still lean on what they do in general, with a confidence that stays low until the bucket itself fills in.
	 */
	public Estimate estimate(int fine) {
		checkFine(fine);
		double ng = slotTotal(GLOBAL);
		if (ng <= 1e-9) {
			double[] u = new double[CLASSES];
			Arrays.fill(u, 1.0 / CLASSES);
			return new Estimate(u, 0);
		}
		double[] pg = new double[CLASSES];
		for (int k = 0; k < CLASSES; k++) pg[k] = counts[GLOBAL * CLASSES + k] / ng;
		double eg = ng;

		int c = COARSE_BASE + fine / 12;
		double nc = slotTotal(c);
		double[] pc = blend(c, nc, pg);
		double ec = nc + K_PARENT * eg / (eg + K_PARENT);

		double nf = slotTotal(fine);
		double[] pf = blend(fine, nf, pc);
		double ef = nf + K_PARENT * ec / (ec + K_PARENT);
		return new Estimate(pf, ef);
	}

	private double[] blend(int slot, double n, double[] parent) {
		double[] out = new double[CLASSES];
		for (int k = 0; k < CLASSES; k++) out[k] = (counts[slot * CLASSES + k] + K_PARENT * parent[k]) / (n + K_PARENT);
		return out;
	}

	/** How far the AI trusts the history over the equilibrium: {@code eff / (eff + K_BLEND)}, 0 with no data. */
	public static double blendWeight(double eff) {
		return eff <= 0 ? 0 : eff / (eff + K_BLEND);
	}

	/** §7.11's alpha, shrunk toward 1 (pure equilibrium) when the model is unsure: {@code 1 - (1 - alpha) * w}. */
	public static double alphaEff(double alpha, double w) {
		double a = Math.max(0, Math.min(1, alpha));
		return 1.0 - (1.0 - a) * Math.max(0, Math.min(1, w));
	}

	// ---- prediction (§7.11 predict) ----

	/** Total equilibrium mass per action class over the columns. */
	public static double[] classMass(double[] y, ActionClass[] cls) {
		double[] m = new double[CLASSES];
		for (int j = 0; j < y.length; j++) m[cls[j].ordinal()] += y[j];
		return m;
	}

	/**
	 * y_hat: the class mix {@code w * history + (1 - w) * equilibrium}, spread back over the columns of each class in
	 * proportion to the equilibrium within the class (uniformly when the equilibrium gives that class no mass, so a habit
	 * the equilibrium never plays can still be predicted). Classes with no column in this matrix are dropped and the
	 * history renormalized over the rest. {@code w == 0}, or no usable history, returns {@code y} unchanged.
	 */
	public static double[] predict(double[] y, ActionClass[] cls, double[] hist, double w) {
		double[] out = y.clone();
		int n = y.length;
		if (n == 0 || w <= 0) return out;
		w = Math.min(1.0, w);

		double ySum = 0;
		for (double v : y) ySum += v;
		if (ySum <= 0) return out;

		double[] mass = classMass(y, cls);
		boolean[] present = new boolean[CLASSES];
		int[] count = new int[CLASSES];
		for (int j = 0; j < n; j++) {
			present[cls[j].ordinal()] = true;
			count[cls[j].ordinal()]++;
		}
		double hSum = 0;
		for (int k = 0; k < CLASSES; k++) if (present[k]) hSum += hist[k];
		if (hSum <= 1e-12) return out;

		double[] cp = new double[CLASSES];
		for (int k = 0; k < CLASSES; k++) {
			if (present[k]) cp[k] = w * (hist[k] / hSum) + (1 - w) * (mass[k] / ySum);
		}
		double total = 0;
		for (int j = 0; j < n; j++) {
			int k = cls[j].ordinal();
			double share = mass[k] > 1e-12 ? y[j] / mass[k] : 1.0 / count[k];
			out[j] = cp[k] * share;
			total += out[j];
		}
		if (total <= 0) return y.clone();
		for (int j = 0; j < n; j++) out[j] /= total;
		return out;
	}

	// ---- final strategy (§7.11 finalStrategy) ----

	/**
	 * {@code alphaEff * x_eq + (1 - alphaEff) * x_exploit}, where x_exploit is a softmax over the rows' payoff against
	 * y_hat (payoffs scaled by their range, so {@code temperatureExploit} means the same for every matrix; large = near
	 * best-response). Against ANY player strategy the result is worth at least the equilibrium value minus
	 * {@code (1 - alphaEff) * (range of M)}: that is the bound on what a wrong read can cost, and why alphaEff shrinks
	 * toward 1 when the model has little data.
	 */
	public static double[] finalStrategy(double[][] M, double[] xEq, double[] yHat, double alphaEff, double temperatureExploit) {
		int n = xEq.length;
		if (n == 0 || alphaEff >= 1.0 - 1e-12) return xEq.clone();
		double[] pay = new double[n];
		double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < n; i++) {
			double s = 0;
			for (int j = 0; j < yHat.length; j++) s += M[i][j] * yHat[j];
			pay[i] = s;
			max = Math.max(max, s);
			min = Math.min(min, s);
		}
		double range = max - min;
		if (range < 1e-9) return xEq.clone(); // every row is worth the same against the read: nothing to exploit
		double[] ex = new double[n];
		double sum = 0;
		for (int i = 0; i < n; i++) {
			ex[i] = Math.exp(temperatureExploit * (pay[i] - max) / range);
			sum += ex[i];
		}
		double[] out = new double[n];
		for (int i = 0; i < n; i++) out[i] = alphaEff * xEq[i] + (1 - alphaEff) * ex[i] / sum;
		return out;
	}

	// ---- utilities ----

	public PlayerModel copy() {
		PlayerModel c = new PlayerModel();
		System.arraycopy(counts, 0, c.counts, 0, counts.length);
		return c;
	}

	public boolean sameCounts(PlayerModel o) {
		return o != null && Arrays.equals(counts, o.counts);
	}

	/** Forget everything (e.g. a new game on the same save object). */
	public void reset() {
		Arrays.fill(counts, 0f);
		pending = null;
	}

	/** One line: observations seen and the player's global habit. */
	public String summary() {
		double n = total();
		if (n <= 1e-9) return "no observations";
		StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "n=%.1f ", n));
		ActionClass[] all = ActionClass.values();
		for (int k = 0; k < CLASSES; k++) {
			sb.append(k == 0 ? "" : " ").append(all[k]).append(String.format(Locale.ROOT, "=%.0f%%", 100 * counts[GLOBAL * CLASSES + k] / n));
		}
		return sb.toString();
	}

	/**
	 * Multi-line readout of what the AI has learned, for a debug key or the console: the player's overall habit, then each
	 * of the four coarse situations with how many observations' worth of data it holds and the split of what they did.
	 * "Better" is the simulation's verdict for the player. The interesting rows: how often a player switches when
	 * switching IS better (plays it safe / plays it right) and when it is NOT (over-switches), and the same for staying.
	 */
	public String report() {
		String[] names = { "not threatened, staying is better", "not threatened, switching is better", "threatened, staying is better", "threatened, switching is better" };
		String[] shortName = { "best move", "other move", "best switch", "other switch", "protect" };
		StringBuilder sb = new StringBuilder("Player habits (" + summary() + ")\n");
		for (int c = 0; c < COARSE; c++) {
			double n = slotTotal(COARSE_BASE + c);
			sb.append(String.format(Locale.ROOT, "  %-36s n=%5.1f  ", names[c], n));
			if (n < 1e-9) {
				sb.append("-");
			} else {
				for (int k = 0; k < CLASSES; k++) {
					sb.append(String.format(Locale.ROOT, "%s %3.0f%%%s", shortName[k], 100 * counts[(COARSE_BASE + c) * CLASSES + k] / n, k < CLASSES - 1 ? " | " : ""));
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private static void checkFine(int fine) {
		if (fine < 0 || fine >= FINE) throw new IllegalArgumentException("fine bucket " + fine);
	}

	/** A save written by a build with a different table layout loads as an empty model instead of crashing. */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (counts == null || counts.length != SLOTS * CLASSES) counts = new float[SLOTS * CLASSES];
	}
}