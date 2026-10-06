package pokemon;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import pokemon.PlayerModel.ActionClass;

/**
 * Phase 6 (§7.11). The engine half of the player model: turns a decision's state and payoff matrix into a situation
 * bucket and an action class per player column ({@link #read}), remembers what it saw ({@link #begin}), and next turn
 * works out what the player actually did from the state alone ({@link #observe}). Needs no hook in the battle UI or in
 * {@code Player.recordTurn} (that one feeds the battle-log upload): the AI diffs the player's active mon and its PP
 * between two of its own decisions.
 * <p>
 * <b>"Best" comes from the matrix, not from a type chart, and it is a band.</b> For each player column j the AI's
 * payoff against its own equilibrium mix is {@code v[j] = sum_i x_eq[i] * M[i][j]}; the player wants it low. Their best
 * stays are the non-Protect stay columns within {@link #BEST_TOL} eval points of the lowest v, their best switches the
 * switch columns within {@link #BEST_TOL} of the lowest switch v. A single winner was too brittle: two columns 0.2
 * points apart are the same decision. The equilibrium (not the blended strategy) is used so classes never depend on what
 * the model has learned.
 * <p>
 * <b>Protect</b> (see {@link #isProtectLike}) is its own class, and "a Protect move is available" is part of the
 * situation, so a scouting habit is measured over the turns the player could have protected.
 * <p>
 * Nothing here mutates battle state. {@code read} is pure given the model and the matrix; {@code begin} and
 * {@code observe} only touch the model's own per-battle bookkeeping.
 */
public final class PlayerReader {
	private PlayerReader() {}

	/** Switching counts as the better reply when the best switch beats the best stay by more than this fraction of the matrix's payoff range. */
	static final double SWITCH_MARGIN = 0.05;
	/** Columns within this many eval points (~HP points; 100 ~ one healthy mon) of the lowest AI payoff all count as "best". Placeholder; Phase 8. */
	public static final double BEST_TOL = 5.0;

	/** One decision's reading of the player: situation, class of every column, history for that situation and how much to trust it. */
	public static final class Read {
		/** Situation bucket, {@link PlayerModel#fineIndex}. */
		public final int fine;
		/** Action class of each player column, same order as the P list passed to {@link #read}. */
		public final ActionClass[] classes;
		/** Smoothed history over {@link ActionClass} for this situation. */
		public final double[] hist;
		/** Effective sample size behind {@code hist}. */
		public final double eff;
		/** {@code blendWeight(eff)}: 0 for an empty model, so everything downstream reduces to the equilibrium. */
		public final double w;
		/** Moves of the player's best non-Protect stay columns. */
		final Set<Move> bestMoves;
		/** Bit i = team slot i is one of the player's best switch columns. */
		final long bestBackMask;
		/** Bit i = team slot i had a switch column this turn. */
		final long switchColMask;

		Read(int fine, ActionClass[] classes, double[] hist, double eff, double w, Set<Move> bestMoves, long bestBackMask, long switchColMask) {
			this.fine = fine;
			this.classes = classes;
			this.hist = hist;
			this.eff = eff;
			this.w = w;
			this.bestMoves = bestMoves;
			this.bestBackMask = bestBackMask;
			this.switchColMask = switchColMask;
		}
	}

	/** What {@link #observe} concluded: the class that was recorded, and a one-line reason for the decision log. */
	public static final class Observation {
		public final ActionClass cls;
		public final String note;

		Observation(ActionClass cls, String note) {
			this.cls = cls;
			this.note = note;
		}
	}

	/**
	 * The Protect family as this engine implements it (the {@code case DETECT / PROTECT / LAVA_LAIR / OBSTRUCT /
	 * SPIKY_SHIELD / AQUA_VEIL} group in {@code Pokemon.move}): moves that give the user {@code Status.PROTECT} for the turn.
	 * Add a new variant here when you add one to that group.
	 */
	public static boolean isProtectLike(Move m) {
		return m == Move.PROTECT || m == Move.DETECT || m == Move.LAVA_LAIR || m == Move.OBSTRUCT || m == Move.SPIKY_SHIELD
				|| m == Move.AQUA_VEIL;
	}

	// ---- reading a decision ----

	/**
	 * Pure. Reads the player's situation from {@code root} and the matrix, and classifies every column of {@code P}.
	 *
	 * @param M   the decision's payoff matrix (rows = AI actions as left after the sack guard, columns = {@code P})
	 * @param xEq the equilibrium mix over M's rows
	 */
	public static Read read(PlayerModel model, SimState root, List<Action> P, double[][] M, double[] xEq) {
		Pokemon ai = root.ai.active(), pl = root.player.active();
		Field field = root.field;

		boolean threatened = SackAnalysis.threatens(ai, pl, field);

		// column values against the equilibrium (AI payoff; lower is better for the player)
		int cols = P.size();
		double[] v = new double[cols];
		double lo = Double.POSITIVE_INFINITY, hi = Double.NEGATIVE_INFINITY;
		for (double[] row : M) {
			for (double x : row) {
				lo = Math.min(lo, x);
				hi = Math.max(hi, x);
			}
		}
		for (int j = 0; j < cols; j++) {
			double s = 0;
			for (int i = 0; i < xEq.length; i++) s += xEq[i] * M[i][j];
			v[j] = s;
		}

		boolean protectAvailable = false;
		double bestStayAll = Double.POSITIVE_INFINITY, bestStay = Double.POSITIVE_INFINITY, bestSwitch = Double.POSITIVE_INFINITY;
		long switchColMask = 0;
		for (int j = 0; j < cols; j++) {
			Action a = P.get(j);
			if (a.kind == ActionKind.SWITCH) {
				bestSwitch = Math.min(bestSwitch, v[j]);
				switchColMask |= 1L << a.slot;
			} else {
				bestStayAll = Math.min(bestStayAll, v[j]);
				if (isProtectLike(a.move)) protectAvailable = true;
				else bestStay = Math.min(bestStay, v[j]);
			}
		}

		Set<Move> bestMoves = EnumSet.noneOf(Move.class);
		long bestBackMask = 0;
		for (int j = 0; j < cols; j++) {
			Action a = P.get(j);
			if (a.kind == ActionKind.SWITCH) {
				if (v[j] <= bestSwitch + BEST_TOL) bestBackMask |= 1L << a.slot;
			} else if (a.move != null && !isProtectLike(a.move) && v[j] <= bestStay + BEST_TOL) {
				bestMoves.add(a.move);
			}
		}

		boolean anySwitch = switchColMask != 0;
		boolean switchBetter = anySwitch && (bestStayAll == Double.POSITIVE_INFINITY || bestStayAll - bestSwitch > SWITCH_MARGIN * (hi - lo));

		double hpFrac = pl.currentHP * 1.0 / pl.getStat(0);
		int hpBand = hpFrac < 1.0 / 3 ? 0 : hpFrac < 2.0 / 3 ? 1 : 2;
		boolean aiFaster = ai.getFaster(pl, 0, 0, field) == ai;
		int fine = PlayerModel.fineIndex(threatened, switchBetter, hpBand, aiFaster, protectAvailable);

		ActionClass[] classes = new ActionClass[cols];
		for (int j = 0; j < cols; j++) classes[j] = classOf(P.get(j), bestMoves, bestBackMask);

		PlayerModel.Estimate e = model.estimate(fine);
		return new Read(fine, classes, e.dist, e.eff, PlayerModel.blendWeight(e.eff), bestMoves, bestBackMask, switchColMask);
	}

	/** A pivot column is a stay (classed by its move); Protect-family moves are their own class; a switch is best if its slot is in the best band. */
	static ActionClass classOf(Action p, Set<Move> bestMoves, long bestBackMask) {
		if (p.kind == ActionKind.SWITCH) return (bestBackMask & (1L << p.slot)) != 0 ? ActionClass.SWITCH_BEST : ActionClass.SWITCH_OTHER;
		if (p.move == null) return ActionClass.STAY_OTHER;
		if (isProtectLike(p.move)) return ActionClass.STAY_PROTECT;
		return bestMoves.contains(p.move) ? ActionClass.STAY_BEST : ActionClass.STAY_OTHER;
	}

	// ---- remembering and observing ----

	/** Remembers the real decision-time state so the next {@link #observe} can tell what the player did. */
	public static void begin(PlayerModel model, Pokemon self, Pokemon foe, Read read) {
		PlayerModel.Pending p = new PlayerModel.Pending();
		p.ai = self.trainer;
		p.prev = foe;
		p.turn = Pokemon.field == null ? 0 : Pokemon.field.turns;
		p.fine = read.fine;
		p.bestMoves = read.bestMoves;
		p.bestBackMask = read.bestBackMask;
		p.switchColMask = read.switchColMask;
		p.pp = ppOf(foe);
		model.pending = p;
	}

	/**
	 * Called at the start of the AI's next decision with the real {@code self} and the player's CURRENT active {@code foe}.
	 * Records the previous turn's player action when it can be told unambiguously; otherwise records nothing:
	 * <ul>
	 * <li>a different AI trainer, or {@code Field.turns} lower than at decision time: a new battle, discarded;</li>
	 * <li>same mon and no PP spent: nothing observable (flinch, sleep, or this is a second decision in the same turn);</li>
	 * <li>a move's PP dropped: a stay by that move's class (Protect family, best band, or other), even if the mon then
	 * pivoted or fainted;</li>
	 * <li>different mon, no PP spent, previous mon not fainted: a switch, best band or other (a forced phaze before the
	 * player moved looks the same, and a switch-in KO'd the same turn is classed by its replacement: rare noise);</li>
	 * <li>different mon, previous mon fainted without moving: a KO, not a decision.</li>
	 * </ul>
	 *
	 * @return what was recorded and why, or null when nothing was
	 */
	@SuppressWarnings("unchecked")
	public static Observation observe(PlayerModel model, Pokemon self, Pokemon foe) {
		PlayerModel.Pending p = model.pending;
		if (p == null) return null;
		model.pending = null; // consumed whatever happens below
		if (p.ai != self.trainer || Pokemon.field == null || Pokemon.field.turns < p.turn) return null;

		Pokemon prev = (Pokemon) p.prev;
		Set<Move> bestMoves = (Set<Move>) p.bestMoves;
		int[] now = ppOf(prev);
		int used = -1;
		for (int i = 0; i < now.length && i < p.pp.length; i++) {
			if (now[i] >= 0 && p.pp[i] >= 0 && now[i] < p.pp[i]) {
				used = i;
				break;
			}
		}

		ActionClass cls;
		String note;
		if (used >= 0) {
			Move m = prev.moveset[used].move;
			if (m != null && isProtectLike(m)) {
				cls = ActionClass.STAY_PROTECT;
				note = "used " + m + " (a Protect move)";
			} else if (m != null && bestMoves.contains(m)) {
				cls = ActionClass.STAY_BEST;
				note = "used " + m + ", in the best band " + bestMoves;
			} else {
				cls = ActionClass.STAY_OTHER;
				note = "used " + m + ", outside the best band " + bestMoves;
			}
		} else if (prev == foe) {
			return null;
		} else if (prev.isFainted()) {
			return null;
		} else {
			int idx = foe.trainer == null ? -1 : foe.trainer.indexOf(foe);
			boolean hadColumn = idx >= 0 && (p.switchColMask & (1L << idx)) != 0;
			boolean best = idx >= 0 && (p.bestBackMask & (1L << idx)) != 0;
			cls = best ? ActionClass.SWITCH_BEST : ActionClass.SWITCH_OTHER;
			note = "switched to " + foe + " (slot " + idx + "): "
					+ (best ? "in the best switch band" : hadColumn ? "had a column but outside the best band" : "had NO column this turn (pruned by ActionGen)")
					+ "; best slots " + maskToString(p.bestBackMask) + ", column slots " + maskToString(p.switchColMask);
		}
		model.record(p.fine, cls);
		return new Observation(cls, note);
	}

	private static String maskToString(long mask) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < 64; i++) {
			if ((mask & (1L << i)) != 0) sb.append(sb.length() > 1 ? "," : "").append(i);
		}
		return sb.append(']').toString();
	}

	private static int[] ppOf(Pokemon p) {
		Moveslot[] ms = p.moveset;
		int[] pp = new int[ms == null ? 0 : ms.length];
		for (int i = 0; i < pp.length; i++) pp[i] = ms[i] == null ? -1 : ms[i].currentPP;
		return pp;
	}

	// ---- debug ----

	/** One-line summary for the decision log: situation, trust, and history vs equilibrium vs prediction per class. */
	static String describe(Read r, double[] eqY, double[] yHat, double alphaEff) {
		double[] eq = PlayerModel.classMass(eqY, r.classes);
		double[] hat = PlayerModel.classMass(yHat, r.classes);
		StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "read fine=%d n=%.1f w=%.2f alphaEff=%.2f |", r.fine, r.eff, r.w, alphaEff));
		ActionClass[] all = ActionClass.values();
		for (int k = 0; k < all.length; k++) {
			sb.append(String.format(Locale.ROOT, " %s hist %.0f%% eq %.0f%% -> %.0f%%;", all[k], 100 * r.hist[k], 100 * eq[k], 100 * hat[k]));
		}
		return sb.toString();
	}
}