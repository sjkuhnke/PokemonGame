package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import pokemon.DamageRange;
import pokemon.Field;
import pokemon.Move;
import pokemon.Pokemon;
import pokemon.Status;
import pokemon.Trainer;
import util.Perf;
import util.Print;

/**
 * The ONE non-recursive forced-replacement chooser. {@code Trainer.getNext2} (the real battle, every difficulty, both
 * seats in self-play) and {@code BattleSimulator.resolveReplacementsForSide} / the Eject-style switch (the matrix) both
 * call {@link #pickSlot}, so the replacement the AI plans for after a sack is the replacement the real game brings out.
 * <p>
 * <b>How a candidate is scored.</b> A replacement is the first thing that happens in the next turn, so each candidate r is
 * valued the way the payoff matrix would value it: enter r (hazards and entry abilities, through the same
 * {@code performSwitch} the simulator uses), then play one turn from there with the real simulator,
 * {@link BattleSimulator#simulateTurn}, for r's moves against the foe's moves. Each cell is the probability-weighted
 * {@link Evaluator#eval} of the branches, so a 24% chance of being KO'd is a 24% branch (a lethal roll against a survivable
 * one), not a flat -90. The candidate's value is the game value of that small matrix ({@link LeadSelection#gameValue}; the
 * single cell when it is 1x1). The best value wins. Because the currency is the matrix's own eval (material at
 * {@link MonWeights} weights, matchup, hazards, ...), there is no separate "future value" term: a mon that dies for nothing
 * costs its material, a mon that survives and chips the foe is credited for it.
 * <p>
 * <b>Budgets.</b> In the real battle (outside any {@link SimContext}) every useful move of r is played against every useful
 * move of the foe ({@link #REAL_ROWS} x {@link #REAL_COLS}, 4 x 4 at most) for every candidate. Inside the simulator
 * (this call sits in a matrix cell's KO branch) it is 1 x 1, the best-damage move against the foe's best-damage move, for
 * the top {@link #SIM_TOP} candidates by the static entry score. Same function, same currency, smaller sample.
 * <p>
 * <b>Exact pruning.</b> The value of a zero-sum block can never exceed the best row's payoff against any single column, so
 * after computing a foe move's column the candidate's upper bound is {@code min over computed columns of max over rows}. The
 * foe's moves are played best-damage first, candidates are visited in order of their first-column bound, and a candidate
 * whose bound cannot beat the best value found so far stops there: its remaining cells are never simulated. The winner is
 * the same as without pruning (up to the regret-matching solver's own small error); only cells that cannot matter are skipped.
 * <p>
 * <b>Weights.</b> Inside the simulator the weights are the decision's own ({@link SimContext#decision()}, installed by
 * {@link MatrixBuilder#buildMatrix}), the same ones every surrounding cell's eval uses; they are not recomputed per call. The
 * player's replacement needs the roles swapped, computed once per decision on first use. Outside the simulator (the real
 * battle) they are computed fresh from the private post-faint state.
 * <p>
 * <b>Static fallback.</b> {@link #pickStatic} is the old scorer: score(r) = {@link SwitchInScorer#scoreOn} minus
 * {@link #FUTURE_W} * weight(r) * hpFrac(r). It is used (a) for a call made from INSIDE a candidate's own cells (a pivot move's
 * switch, a faint during the cell), which keeps the evaluation non-recursive, (b) when the foe has no trainer (wild battle)
 * or its active is fainted, (c) when {@link #USE_CELLS} is off, and (d) if the cell path throws.
 * <p>
 * <b>Counters</b> ({@link #stats()}, reset by {@link #resetStats()}, printed by {@code SelfPlay} with the Perf report) say
 * where the time goes: picks by path, candidates and cells run or saved by pruning, and how the weights were obtained.
 * Debug output ({@code Print.debug}, real battle only) lists each candidate's value; the static path lists entry / weight.
 * Game thread only, like the rest of the battle code.
 */
public final class ReplacementChooser {
	/** Static fallback only: points of matchupScore one unit of (weight * hpFrac) is worth. Registered in {@link Tunables}; self-play found it flat. */
	public static double FUTURE_W = 8.0;

	/** false = always use {@link #pickStatic} (the old scorer). A kill switch in case the cell path is too slow or misbehaves. */
	public static boolean USE_CELLS = true;
	/** false = evaluate every cell of every candidate (for A/B checks that pruning changes nothing). */
	public static boolean PRUNE = true;
	/** Real battle: how many of r's moves / the foe's moves are played against each other (best by expected damage when capped). */
	public static int REAL_ROWS = 4, REAL_COLS = 4;
	/** Inside the simulator: the same, kept to the single best-damage move each. */
	public static int SIM_ROWS = 1, SIM_COLS = 1;
	/** Candidates evaluated by cells: all of them in the real battle, the top few by static entry score inside the simulator. */
	public static int REAL_TOP = 6, SIM_TOP = 2;
	/** Value of a candidate that faints on entry (hazards): finite and far below any real eval. */
	private static final double ENTRY_FAINT_VALUE = -1e6;

	/** > 0 while a candidate's cells are being played; a pickSlot call made then takes the static path (non-recursive). */
	private static final ThreadLocal<int[]> NEST = ThreadLocal.withInitial(() -> new int[1]);

	// ---- counters (plain longs: game thread only, always on, negligible cost) ----
	/** All calls; calls decided by cells in the real battle / in the simulator; static picks from a nested call / for any other reason. */
	public static long picks, cellPicksReal, cellPicksSim, staticNested, staticOther;
	/** Candidates that were entered; candidates whose cells were cut short by pruning; cells simulated; cells skipped by pruning. */
	public static long candidatesEntered, candidatesPruned, cellsRun, cellsSaved;
	/** Weights: computed fresh, taken from the decision, or the decision's swapped-role set computed (once per decision at most). */
	public static long weightsFresh, weightsFromDecision, weightsSwapped;

	private ReplacementChooser() {}

	public static void resetStats() {
		picks = cellPicksReal = cellPicksSim = staticNested = staticOther = 0;
		candidatesEntered = candidatesPruned = cellsRun = cellsSaved = 0;
		weightsFresh = weightsFromDecision = weightsSwapped = 0;
	}

	/** One line: where the replacement chooser's work went since the last {@link #resetStats()}. */
	public static String stats() {
		return String.format(Locale.ROOT,
				"ReplacementChooser: picks=%d (cells real=%d sim=%d; static nested=%d other=%d) candidates=%d (pruned %d) cells run=%d saved=%d weights: fresh=%d decision=%d swapped=%d",
				picks, cellPicksReal, cellPicksSim, staticNested, staticOther, candidatesEntered, candidatesPruned, cellsRun, cellsSaved,
				weightsFresh, weightsFromDecision, weightsSwapped);
	}

	/** Team index of the best alive non-current member of {@code side} against {@code foe}, or -1 if there is none. The foe's side is {@code foe.trainer}. */
	public static int pickSlot(Trainer side, Pokemon foe, Field field) {
		return pickSlot(side, foe.trainer, foe, field, null);
	}

	/** As above with the foe's side given explicitly and which side is the AI unknown (weights are then always computed fresh). */
	public static int pickSlot(Trainer side, Trainer foeSide, Pokemon foe, Field field) {
		return pickSlot(side, foeSide, foe, field, null);
	}

	/**
	 * As above. The simulator passes its own shells (a shared bench mon's {@code trainer} pointer can name an older shell, so
	 * {@code foe.trainer} is not trusted there) and whether {@code side} is the AI's, which lets the decision's weights be
	 * reused. {@code sideIsAi == null} means unknown.
	 */
	public static int pickSlot(Trainer side, Trainer foeSide, Pokemon foe, Field field, Boolean sideIsAi) {
		long t0 = Perf.start();
		try {
			picks++;
			List<Integer> cands = candidates(side);
			if (cands.isEmpty()) return -1;
			if (cands.size() == 1) return cands.get(0);
			boolean nested = NEST.get()[0] > 0;
			boolean cells = USE_CELLS && !nested && foeSide != null && field != null && foe != null && !foe.isFainted();
			if (cells) {
				try {
					int slot = pickByCells(side, foeSide, foe, field, cands, sideIsAi);
					if (slot >= 0) return slot;
				} catch (RuntimeException e) {
					// A replacement must never crash the battle: fall back to the old scorer and say why.
					Print.debug("[ReplacementChooser] cell path failed, using the static scorer: " + e + "\n");
				}
				staticOther++;
			} else if (nested) {
				staticNested++;
			} else {
				staticOther++;
			}
			return pickStatic(side, foe, field, sideIsAi);
		} finally {
			Perf.stop(Perf.PICK, t0);
		}
	}

	private static List<Integer> candidates(Trainer side) {
		List<Integer> out = new ArrayList<>();
		Pokemon[] team = side.team;
		for (int i = 0; i < team.length; i++) {
			Pokemon p = team[i];
			if (p == null || p.isFainted() || p == side.current) continue;
			out.add(i);
		}
		return out;
	}

	// ---- cell-based scoring ----

	/** One candidate being evaluated: its entered fork, the two move lists and the columns of its payoff block computed so far. */
	private static final class Cand {
		final int idx;
		final SimState s;
		final List<Move> mine, theirs;
		final double[][] M;
		/** Columns (foe moves) computed so far, and the upper bound on the block's value that follows from them. */
		int cols;
		double ub = Double.POSITIVE_INFINITY;
		double value;
		boolean fainted, pruned;

		Cand(int idx, SimState s, List<Move> mine, List<Move> theirs) {
			this.idx = idx;
			this.s = s;
			this.mine = mine;
			this.theirs = theirs;
			this.M = new double[mine.size()][theirs.size()];
		}
	}

	private static int pickByCells(Trainer side, Trainer foeSide, Pokemon foe, Field field, List<Integer> cands, Boolean sideIsAi) {
		boolean inSim = SimContext.active();
		int rowsCap = inSim ? SIM_ROWS : REAL_ROWS;
		int colsCap = inSim ? SIM_COLS : REAL_COLS;
		int top = inSim ? SIM_TOP : REAL_TOP;
		boolean log = !inSim && !Print.isDebugSuppressed();
		AIConfig cfg = inSim ? new AIConfig().leadLite() : new AIConfig();
		MonWeights decisionW = inSim ? decisionWeights(sideIsAi) : null;

		List<Integer> order = new ArrayList<>(cands);
		if (order.size() > top) {
			// Only the top few by the cheap static entry score get the cells (the simulator's budget).
			int[] entry = new int[side.team.length];
			for (int i : order) entry[i] = SwitchInScorer.scoreOn(side, i, foe, field);
			order.sort((a, b) -> Integer.compare(entry[b], entry[a]));
			order = new ArrayList<>(order.subList(0, top));
		}
		if (inSim) cellPicksSim++;
		else cellPicksReal++;

		Field prevField = Pokemon.field;
		NEST.get()[0]++;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			// The replacing side is always the "ai" of the private state, so eval's value is from ITS point of view.
			SimState base = new SimState(field.clone(), new SideState(side.simShell(Collections.<Integer>emptySet())),
					new SideState(foeSide.simShell(Collections.<Integer>emptySet())), field.turns);
			Pokemon.field = base.field;
			MonWeights w = decisionW;
			if (w == null) {
				w = MonWeights.compute(base);
				weightsFresh++;
			}

			// Enter every candidate and play the foe's best-damage move against all of its rows (its first column).
			List<Cand> cs = new ArrayList<>();
			for (int idx : order) {
				Cand c = enter(base, idx, rowsCap, colsCap);
				candidatesEntered++;
				if (!c.fainted) runColumn(c, 0, w, cfg);
				cs.add(c);
			}
			// Most promising first (highest bound), so the best value is known early. Stable, so ties keep the static order.
			cs.sort((a, b) -> Double.compare(b.ub, a.ub));

			StringBuilder sb = log ? new StringBuilder("\n______________\nREPLACEMENT (one-turn cells)\n______________\n") : null;
			int best = -1;
			double bestValue = Double.NEGATIVE_INFINITY;
			for (Cand c : cs) {
				if (!c.fainted) {
					while (c.cols < c.theirs.size() && (!PRUNE || c.ub > bestValue)) runColumn(c, c.cols, w, cfg);
					if (c.cols < c.theirs.size()) {
						c.pruned = true; // its bound cannot beat the best so far: the rest of its cells are never played
						c.value = c.ub;
						candidatesPruned++;
						cellsSaved += (long) (c.theirs.size() - c.cols) * c.mine.size();
					} else {
						c.value = c.M.length == 1 && c.M[0].length == 1 ? c.M[0][0] : LeadSelection.gameValue(c.M);
					}
				}
				if (log) {
					if (c.fainted) sb.append(String.format(Locale.ROOT, "[%s: faints on entry], ", side.team[c.idx]));
					else if (c.pruned) sb.append(String.format(Locale.ROOT, "[%s: <= %.1f (pruned after %d of %d foe moves)], ", side.team[c.idx], c.value, c.cols, c.theirs.size()));
					else sb.append(String.format(Locale.ROOT, "[%s: value=%.1f], ", side.team[c.idx], c.value));
				}
				if (!c.pruned && c.value > bestValue) {
					bestValue = c.value;
					best = c.idx;
				}
			}
			if (log) Print.debug(sb.append("\n").append(stats()).append("\n\n").toString());
			return best;
		} finally {
			NEST.get()[0]--;
			Pokemon.field = prevField;
		}
	}

	/**
	 * The weights to evaluate cells with inside the simulator: the surrounding decision's own (the AI's), or, for the player's
	 * replacement, the same computation with the roles swapped (once per decision). Null when no decision is installed or
	 * the side is unknown: the caller then computes fresh from the private state.
	 */
	private static MonWeights decisionWeights(Boolean sideIsAi) {
		if (sideIsAi == null) return null;
		SimContext.Decision d = SimContext.decision();
		if (d == null) return null;
		if (sideIsAi) {
			weightsFromDecision++;
			return d.weights;
		}
		boolean first = !d.swappedReady();
		MonWeights w = d.swappedWeights();
		if (first) weightsSwapped++;
		else weightsFromDecision++;
		return w;
	}

	/** Enters {@code team[idx]} on a private fork (hazards, entry abilities) and lists the two sides' moves, best damage first. */
	private static Cand enter(SimState base, int idx, int rowsCap, int colsCap) {
		SimState s = base.fork(Collections.singleton(idx), Collections.<Integer>emptySet());
		Pokemon.field = s.field;
		Pokemon foeMon = s.player.active();
		BattleSimulator.performSwitch(s.ai, idx, foeMon);
		Pokemon me = s.ai.active();
		// An entry request to switch again (Eject Pack and the like) is not simulated, as in BattleSimulator.simulateEntry.
		if (me.hasStatus(Status.SWITCHING)) me.removeStatus(Status.SWITCHING);
		if (foeMon.hasStatus(Status.SWITCHING)) foeMon.removeStatus(Status.SWITCHING);
		if (me.isFainted() || me.currentHP <= 0) {
			Cand c = new Cand(idx, s, Collections.<Move>emptyList(), Collections.<Move>emptyList());
			c.fainted = true;
			c.value = ENTRY_FAINT_VALUE;
			c.ub = ENTRY_FAINT_VALUE;
			return c;
		}
		return new Cand(idx, s, topMoves(me, foeMon, s.field, rowsCap), topMoves(foeMon, me, s.field, colsCap));
	}

	/** Plays the foe's move number {@code j} against every row of the candidate and tightens its upper bound. */
	private static void runColumn(Cand c, int j, MonWeights w, AIConfig cfg) {
		Pokemon.field = c.s.field;
		double colMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < c.mine.size(); i++) {
			List<Branch> branches = BattleSimulator.simulateTurn(c.s, new Action(c.mine.get(i)), new Action(c.theirs.get(j)), cfg);
			double v = 0;
			for (Branch br : branches) v += br.prob * Evaluator.eval(br.state, cfg.style, w);
			c.M[i][j] = v;
			colMax = Math.max(colMax, v);
			cellsRun++;
		}
		// Whatever mix the candidate plays, the foe can answer with this move: the block's value is at most the best row against it.
		c.ub = Math.min(c.ub, colMax);
		c.cols = j + 1;
	}

	/**
	 * The mon's useful moves ({@link ActionGen#usefulRows}, Struggle when it has none), highest expected damage against
	 * {@code foe} first (status moves last), at most {@code cap}. Caller holds a SimContext scope.
	 */
	private static List<Move> topMoves(Pokemon self, Pokemon foe, Field field, int cap) {
		List<Move> valid = ActionGen.usefulRows(self, foe, field, ActionGen.effectiveMoves(self));
		double[] dmg = new double[valid.size()];
		for (int i = 0; i < dmg.length; i++) {
			Move m = valid.get(i);
			DamageRange r = m.cat == 2 ? null : SimCache.calcRange(self, foe, m, true, field);
			dmg[i] = (r == null || !r.usable || r.immune || !r.dealsDamage()) ? 0 : r.expectedCapped(foe.currentHP);
		}
		List<Integer> idx = new ArrayList<>();
		for (int i = 0; i < dmg.length; i++) idx.add(i);
		idx.sort((a, b) -> Double.compare(dmg[b], dmg[a]));
		List<Move> out = new ArrayList<>();
		for (int k = 0; k < Math.min(cap, idx.size()); k++) out.add(valid.get(idx.get(k)));
		return out;
	}

	// ---- static fallback (the previous scorer) ----

	private static int pickStatic(Trainer side, Pokemon foe, Field field, Boolean sideIsAi) {
		Pokemon[] team = side.team;
		double[] w = futureWeights(side, foe, field, sideIsAi);
		boolean log = !SimContext.active() && !Print.isDebugSuppressed();
		StringBuilder sb = log ? new StringBuilder("\n______________\nREPLACEMENT (static)\n______________\n") : null;

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

	/** The static combined score of one candidate (entry quality minus future value), with the weights already computed. */
	static double score(Trainer side, int idx, Pokemon foe, Field field, double[] w) {
		Pokemon p = side.team[idx];
		double hpFrac = p.currentHP * 1.0 / p.getStat(0);
		double weight = (w != null && idx < w.length) ? w[idx] : 1.0;
		return combine(SwitchInScorer.scoreOn(side, idx, foe, field), weight, hpFrac);
	}

	/** Pure (static fallback): entry quality minus the future value of spending this mon now. */
	public static double combine(int entryScore, double weight, double hpFrac) {
		return entryScore - FUTURE_W * weight * hpFrac;
	}

	/**
	 * side's per-slot weights for the static fallback. Inside the simulator with a decision installed and the side known, the
	 * decision's own weights (the same ones the surrounding cells' eval uses); otherwise freshly computed, or null when the foe
	 * has no trainer (wild battle: every weight 1). A fresh computation pins the static {@link Pokemon#field} to {@code field}
	 * for the duration, the same way {@link SwitchInScorer} pins it for entry scoring: MonWeights.forSide's damage/type calls go
	 * through ordinary Pokemon methods that read the static field, not only the explicit parameter, so a caller deep inside
	 * {@link BattleSimulator#simulateTurn} (where the static field can have drifted from this specific branch's field by the
	 * time replacements are resolved) needs it pinned explicitly.
	 */
	private static double[] futureWeights(Trainer side, Pokemon foe, Field field, Boolean sideIsAi) {
		if (sideIsAi != null && SimContext.active()) {
			SimContext.Decision d = SimContext.decision();
			if (d != null) {
				weightsFromDecision++;
				return sideIsAi ? d.weights.ai : d.weights.player;
			}
		}
		Trainer foeTrainer = foe.trainer;
		if (foeTrainer == null || field == null) return null;
		Field prevField = Pokemon.field;
		try {
			Pokemon.field = field;
			weightsFresh++;
			return MonWeights.forSide(new SideState(side), new SideState(foeTrainer), field);
		} finally {
			Pokemon.field = prevField;
		}
	}
}