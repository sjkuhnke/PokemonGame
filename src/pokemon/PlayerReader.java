package pokemon;

import java.util.List;
import java.util.Locale;

import pokemon.PlayerModel.ActionClass;

/**
 * Phase 6 (§7.11). The engine half of the player model: turns a decision's state and payoff matrix into a situation
 * bucket and an action class per player column ({@link #read}), remembers what it saw ({@link #begin}), and next turn
 * works out what the player actually did from the state alone ({@link #observe}). Needs no hook in the battle UI or in
 * {@code Player.recordTurn} (that one feeds the battle-log upload): the AI diffs the player's active mon and its PP
 * between two of its own decisions.
 * <p>
 * <b>"Best" comes from the matrix, not from a type chart.</b> For each player column j the AI's payoff against its own
 * equilibrium mix is {@code v[j] = sum_i x_eq[i] * M[i][j]}; the player wants it low. Their best stay is the stay column
 * (any move or pivot column) with the lowest v, their best switch the switch column with the lowest v. So "best" means
 * what the full simulation (damage, speed, items, KO chances, status, setup) says is their best reply, and a player who
 * keeps picking it is playing the AI's own idea of good play. The equilibrium (not the blended strategy) is used so
 * classes never depend on what the model has learned.
 * <p>
 * Nothing here mutates battle state. {@code read} is pure given the model and the matrix; {@code begin} and
 * {@code observe} only touch the model's own per-battle bookkeeping.
 */
public final class PlayerReader {
	private PlayerReader() {}

	/** Switching counts as the better reply when the best switch beats the best stay by more than this fraction of the matrix's payoff range. */
	static final double SWITCH_MARGIN = 0.05;

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
		/** The move of the player's best stay column, or null. */
		final Move bestMove;
		/** Team index of the player's best switch column, or -1. */
		final int bestBack;

		Read(int fine, ActionClass[] classes, double[] hist, double eff, double w, Move bestMove, int bestBack) {
			this.fine = fine;
			this.classes = classes;
			this.hist = hist;
			this.eff = eff;
			this.w = w;
			this.bestMove = bestMove;
			this.bestBack = bestBack;
		}
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
		int bestStay = -1, bestSwitch = -1; // first column wins ties
		for (int j = 0; j < cols; j++) {
			if (P.get(j).kind == ActionKind.SWITCH) {
				if (bestSwitch < 0 || v[j] < v[bestSwitch]) bestSwitch = j;
			} else {
				if (bestStay < 0 || v[j] < v[bestStay]) bestStay = j;
			}
		}
		boolean switchBetter = bestSwitch >= 0 && (bestStay < 0 || v[bestStay] - v[bestSwitch] > SWITCH_MARGIN * (hi - lo));
		Move bestMove = bestStay >= 0 ? P.get(bestStay).move : null;
		int bestBack = bestSwitch >= 0 ? P.get(bestSwitch).slot : -1;

		double hpFrac = pl.currentHP * 1.0 / pl.getStat(0);
		int hpBand = hpFrac < 1.0 / 3 ? 0 : hpFrac < 2.0 / 3 ? 1 : 2;
		boolean aiFaster = ai.getFaster(pl, 0, 0, field) == ai;
		int fine = PlayerModel.fineIndex(threatened, switchBetter, hpBand, aiFaster);

		ActionClass[] classes = new ActionClass[cols];
		for (int j = 0; j < cols; j++) classes[j] = classOf(P.get(j), bestMove, bestBack);

		PlayerModel.Estimate e = model.estimate(fine);
		return new Read(fine, classes, e.dist, e.eff, PlayerModel.blendWeight(e.eff), bestMove, bestBack);
	}

	/** A pivot column is a stay (it is classed by its move); a plain switch by whether it goes to the best switch column's mon. */
	static ActionClass classOf(Action p, Move bestMove, int bestBack) {
		if (p.kind == ActionKind.SWITCH) return p.slot == bestBack ? ActionClass.SWITCH_BEST : ActionClass.SWITCH_OTHER;
		return p.move != null && p.move == bestMove ? ActionClass.STAY_BEST : ActionClass.STAY_OTHER;
	}

	// ---- remembering and observing ----

	/** Remembers the real decision-time state so the next {@link #observe} can tell what the player did. */
	public static void begin(PlayerModel model, Pokemon self, Pokemon foe, Read read) {
		PlayerModel.Pending p = new PlayerModel.Pending();
		p.ai = self.trainer;
		p.prev = foe;
		p.turn = Pokemon.field == null ? 0 : Pokemon.field.turns;
		p.fine = read.fine;
		p.bestMove = read.bestMove;
		p.bestBack = read.bestBack;
		p.pp = ppOf(foe);
		model.pending = p;
	}

	/**
	 * Called at the start of the AI's next decision with the real {@code self} and the player's CURRENT active {@code foe}.
	 * Records the previous turn's player action when it can be told unambiguously; otherwise records nothing:
	 * <ul>
	 * <li>a different AI trainer, or {@code Field.turns} lower than at decision time: a new battle, discarded;</li>
	 * <li>same mon and no PP spent: nothing observable (flinch, sleep, or this is a second decision in the same turn);</li>
	 * <li>a move's PP dropped: a stay by that move's class, even if the mon then pivoted or fainted;</li>
	 * <li>different mon, no PP spent, previous mon not fainted: a switch, by whether it went to the best back (a forced
	 * phaze before the player moved looks the same, and a switch-in KO'd the same turn is classed by its replacement:
	 * rare noise in the best-back vs other split only);</li>
	 * <li>different mon, previous mon fainted without moving: a KO, not a decision.</li>
	 * </ul>
	 */
	public static ActionClass observe(PlayerModel model, Pokemon self, Pokemon foe) {
		PlayerModel.Pending p = model.pending;
		if (p == null) return null;
		model.pending = null; // consumed whatever happens below
		if (p.ai != self.trainer || Pokemon.field == null || Pokemon.field.turns < p.turn) return null;

		Pokemon prev = (Pokemon) p.prev;
		int[] now = ppOf(prev);
		int used = -1;
		for (int i = 0; i < now.length && i < p.pp.length; i++) {
			if (now[i] >= 0 && p.pp[i] >= 0 && now[i] < p.pp[i]) {
				used = i;
				break;
			}
		}

		ActionClass cls;
		if (used >= 0) {
			Move m = prev.moveset[used].move;
			cls = m != null && m == p.bestMove ? ActionClass.STAY_BEST : ActionClass.STAY_OTHER;
		} else if (prev == foe) {
			return null;
		} else if (prev.isFainted()) {
			return null;
		} else {
			int idx = foe.trainer == null ? -1 : foe.trainer.indexOf(foe);
			cls = idx >= 0 && idx == p.bestBack ? ActionClass.SWITCH_BEST : ActionClass.SWITCH_OTHER;
		}
		model.record(p.fine, cls);
		return cls;
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