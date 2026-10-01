package pokemon;

/**
 * §7.14.1. HP-independent, per-team-slot value weights, computed once per decision from the
 * root snapshot and never recomputed inside matrix cells. Indexed by team-array slot (not by
 * Pokemon reference), since forked/cloned SimStates give bench mons new object identities but
 * keep slot indices stable.
 * <p>
 * raw[m] = BASE_W + contribution[m] + UNIQUE_W * unique[m] + utility[m], normalized to mean 1.0 over the side's alive
 * mons and clamped to [MIN_W, MAX_W].
 * <ul>
 * <li>{@code contribution}: how much of the opposing team a mon handles, weighted by how dangerous each foe is.</li>
 * <li>{@code unique} (Phase 5): the only good answer to a dangerous foe (best edge, above 0, and more than {@link #GAP}
 * clear of the runner-up). Pure math in {@link #uniqueBonuses}.</li>
 * <li>{@code utility} (Phase 5): small capped terms for what a mon does beyond trading hits (hazards, screens, cleric,
 * Healing Wish, speed control), see {@link #utility}.</li>
 * </ul>
 * There is deliberately NO ace multiplier: a mon is valuable because the evaluation says it answers threats or does a
 * job the team needs, not because it was labeled an ace. The same weights price sacks (who is worth keeping), the
 * material term in eval, and the future-value term of {@link ReplacementChooser}.
 */
public final class MonWeights {
	private static final double BASE_T = 1.0;
	private static final double BASE_W = 1.0;
	private static final double MIN_W = 0.25, MAX_W = 3.0;
	/** Phase 5: scale of the only-answer bonus (edge units are about [-1, +1]; threat is about 1..2). Tuned in Phase 8. */
	static final double UNIQUE_W = 0.75;
	/** Phase 5: how far ahead of the runner-up a mon's edge must be to count as the only answer. Tuned in Phase 8. */
	static final double GAP = 0.3;
	/** Phase 5: cap on the summed utility terms, so utility nudges but never outweighs answering threats. */
	static final double UTILITY_MAX = 0.6;
	/**
	 * Normalization sharpness: w ~ exp(NORM_K * z), z = (raw - mean) / max(sd, NORM_SD_FLOOR). 0.5 turns a raw spread of one
	 * standard deviation into about a 1.65x weight ratio; a lone standout on a six-mon team lands near 2.5x the average.
	 * Untuned placeholder; Phase 8.
	 */
	static final double NORM_K = 0.5;
	/** Raw units are comparable across teams (contribution/unique/utility are absolute), so a near-uniform team must not have tiny noise stretched to full scale: sd is floored here. */
	static final double NORM_SD_FLOOR = 1.0;

	public final double[] ai;
	public final double[] player;
	/** AI-perspective full-HP edge[aiSlot][playerSlot] (about -1..+1; 0 for pairs not both alive at the root), the table the weights are built from. Used by {@link Evaluator#matchupTerm}. */
	public final double[][] edge;

	private MonWeights(double[] ai, double[] player, double[][] edge) {
		this.ai = ai;
		this.player = player;
		this.edge = edge;
	}

	public static MonWeights compute(SimState root) {
		double[][][] edgeOut = new double[1][][];
		double[] aiW = forSide(root.ai, root.player, root.field, edgeOut);
		double[] plW = forSide(root.player, root.ai, root.field, null);
		return new MonWeights(aiW, plW, edgeOut[0]);
	}

	/** One side's weights (mirrored formula for the other side). Also used by {@link ReplacementChooser}. */
	static double[] forSide(SideState mine, SideState theirs, Field field) {
		return forSide(mine, theirs, field, null);
	}

	/** As above; when {@code edgeOut} is non-null, edgeOut[0] receives this side's edge table (mine x theirs). */
	private static double[] forSide(SideState mine, SideState theirs, Field field, double[][][] edgeOut) {
		Pokemon[] myTeam = mine.bench();
		Pokemon[] theirTeam = theirs.bench();
		int n = myTeam.length, m = theirTeam.length;

		boolean[] myAlive = new boolean[n], theirAlive = new boolean[m];
		for (int i = 0; i < n; i++) myAlive[i] = myTeam[i] != null && !myTeam[i].isFainted();
		for (int j = 0; j < m; j++) theirAlive[j] = theirTeam[j] != null && !theirTeam[j].isFainted();

		double[][] edge = new double[n][m];
		for (int i = 0; i < n; i++) {
			if (!myAlive[i]) continue;
			for (int j = 0; j < m; j++) {
				if (!theirAlive[j]) continue;
				edge[i][j] = baseEdge(myTeam[i], theirTeam[j], field);
			}
		}

		if (edgeOut != null) edgeOut[0] = edge;

		double[] threat = new double[m];
		for (int j = 0; j < m; j++) {
			if (!theirAlive[j]) continue;
			double sum = 0;
			int count = 0;
			for (int i = 0; i < n; i++) {
				if (!myAlive[i]) continue;
				sum += Math.max(0, -edge[i][j]);
				count++;
			}
			threat[j] = BASE_T + (count > 0 ? sum / count : 0);
		}

		double[] unique = uniqueBonuses(edge, threat, myAlive, theirAlive);

		double[] raw = new double[n];
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			for (int i = 0; i < n; i++) {
				if (!myAlive[i]) continue;
				double contribution = 0;
				for (int j = 0; j < m; j++) {
					if (!theirAlive[j]) continue;
					contribution += threat[j] * squash(edge[i][j]);
				}
				raw[i] = BASE_W + contribution + UNIQUE_W * unique[i] + utility(myTeam[i], mine, theirs, field);
			}
		}
		return normalize(raw, myTeam);
	}

	/**
	 * Pure. unique[i] = sum over alive foes j of threat[j], for every foe j where mon i has the best edge, that edge is
	 * above 0 (it actually beats the foe), and it is more than {@link #GAP} ahead of the second-best alive mon. A side
	 * with a single alive mon has no runner-up, so nothing is unique.
	 */
	public static double[] uniqueBonuses(double[][] edge, double[] threat, boolean[] myAlive, boolean[] theirAlive) {
		int n = edge.length;
		double[] out = new double[n];
		if (n == 0) return out;
		int m = edge[0].length;
		for (int j = 0; j < m; j++) {
			if (!theirAlive[j]) continue;
			int bestI = -1;
			double best = Double.NEGATIVE_INFINITY, second = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < n; i++) {
				if (!myAlive[i]) continue;
				double e = edge[i][j];
				if (e > best) {
					second = best;
					best = e;
					bestI = i;
				} else if (e > second) {
					second = e;
				}
			}
			if (bestI < 0 || second == Double.NEGATIVE_INFINITY) continue;
			if (best > 0 && best - second > GAP) out[bestI] += threat[j];
		}
		return out;
	}

	/**
	 * Small additive value for jobs a mon does beyond trading hits. Each term needs its move AND a situation where it
	 * matters right now (a remover only counts while hazards are on the mon's own side, a cleric only while a teammate
	 * is statused, ...). Summed and capped at {@link #UTILITY_MAX}. Caller holds a SimContext scope.
	 */
	static double utility(Pokemon m, SideState mine, SideState theirs, Field field) {
		if (m == null || m.moveset == null) return 0;
		double u = 0;

		Pokemon foeAnchor = firstAlive(theirs);
		if (foeAnchor != null) {
			for (Moveslot ms : m.moveset) {
				if (ms == null || ms.move == null || !ms.move.isHazard()) continue;
				if (m.isHazardUseful(ms.move, foeAnchor, field)) {
					u += 0.2;
					break;
				}
			}
		}

		if (has(m, Move.RAPID_SPIN, Move.DEFOG)) {
			for (Field.FieldEffect fe : mine.shell.getFieldEffectList()) {
				if (m.hazardMoveForEffect(fe.effect) != null) {
					u += 0.2;
					break;
				}
			}
		}

		if (has(m, Move.REFLECT, Move.LIGHT_SCREEN, Move.AURORA_VEIL)) u += 0.1;
		if (has(m, Move.TRICK_ROOM, Move.TAILWIND)) u += 0.1;

		boolean cleric = has(m, Move.HEAL_BELL, Move.AROMATHERAPY);
		boolean wisher = has(m, Move.HEALING_WISH, Move.LUNAR_DANCE);
		if (cleric || wisher) {
			for (Pokemon p : mine.bench()) {
				if (p == null || p == m || p.isFainted()) continue;
				boolean statused = p.status != null && p.status != Status.HEALTHY;
				boolean hurt = p.currentHP * 1.0 / p.getStat(0) < 0.6;
				if (cleric && statused) u += 0.15;
				if (wisher && (statused || hurt)) u += 0.1;
				if ((cleric && statused) || (wisher && (statused || hurt))) break;
			}
		}
		return Math.min(UTILITY_MAX, u);
	}

	private static boolean has(Pokemon p, Move... moves) {
		for (Moveslot ms : p.moveset) {
			if (ms == null || ms.move == null) continue;
			for (Move x : moves) if (ms.move == x) return true;
		}
		return false;
	}

	private static Pokemon firstAlive(SideState side) {
		Pokemon a = side.active();
		if (a != null && !a.isFainted()) return a;
		for (Pokemon p : side.bench()) if (p != null && !p.isFainted()) return p;
		return null;
	}

	/** Bounded contribution multiplier from a raw edge score. */
	private static double squash(double e) {
		return Math.tanh(e);
	}

	/**
	 * matchupScore at full HP, no stages, no status (§7.14.1), normalized by /90 to land roughly
	 * in [-1, +1] the way the spec's edge[] is defined.
	 */
	private static double baseEdge(Pokemon m, Pokemon q, Field field) {
		Pokemon mBase = baseline(m);
		Pokemon qBase = baseline(q);
		DamageRange mine, theirs;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			mine = Evaluator.bestRange(mBase, qBase, field);
			theirs = Evaluator.bestRange(qBase, mBase, field);
		}
		double myFrac = mine == null ? 0 : Math.min(1.0, mine.expectedCapped(qBase.getStat(0)) / qBase.getStat(0));
		double foeFrac = theirs == null ? 0 : Math.min(1.0, theirs.expectedCapped(mBase.getStat(0)) / mBase.getStat(0));
		boolean faster = mBase.getFaster(qBase, 0, 0, field) == mBase;
		return mBase.matchupScore((int) Math.round(myFrac * 100), foeFrac * 100, faster) / 90.0;
	}

	/**
	 * Full HP, no status, no stat stages, current item/ability (§7.14.1's "1v1 matchup at full
	 * HP ... no stages, no status"). Uses fullClone() (Phase 0/2 precedent) then resets the
	 * volatile bits. Confirmed: the engine's default/healthy state is {@code Status.HEALTHY},
	 * not {@code null}.
	 */
	private static Pokemon baseline(Pokemon p) {
		Pokemon c = p.fullClone();
		c.currentHP = c.getStat(0);
		c.status = Status.HEALTHY;
		java.util.Arrays.fill(c.statStages, 0);
		return c;
	}

	/**
	 * z-score + exponential, then rescale to mean 1.0 over the alive mons, then clamp to [MIN_W, MAX_W].
	 * <ul>
	 * <li>Sign-safe: raw can be negative on a struggling team (BASE_W + contributions that go below 0). Ratio-to-mean broke
	 * there (mean &lt;= 0 gave a flat 1.0 or inverted order); z-scores do not care about sign.</li>
	 * <li>Scale-safe: the previous offset-from-mean (1 + raw - mean) put raw-unit differences straight into a [0.25, 3.0]
	 * window, so any raw spread over about 0.75 pinned nearly every mon to a clamp (four at 0.25 and one at 3.0 in real
	 * logs), erasing the differences between the weaker mons and with them the price of a sack.</li>
	 * <li>Monotone in raw, so ordering is preserved and the Defog/utility bonuses still lift a mon.</li>
	 * <li>Averages exactly 1.0 before the clamp (T26). With NORM_K = 0.5 the clamp is rarely reached.</li>
	 * <li>One alive mon: weight 1.0. Fainted mons: 0.</li>
	 * </ul>
	 */
	private static double[] normalize(double[] raw, Pokemon[] team) {
		double sum = 0;
		int alive = 0;
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) continue;
			sum += raw[i];
			alive++;
		}
		double[] out = new double[raw.length];
		if (alive == 0) return out;
		double mean = sum / alive;
		double var = 0;
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) continue;
			var += (raw[i] - mean) * (raw[i] - mean);
		}
		double sd = Math.max(Math.sqrt(var / alive), NORM_SD_FLOOR);

		double[] e = new double[raw.length];
		double eSum = 0;
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) continue;
			e[i] = Math.exp(NORM_K * (raw[i] - mean) / sd);
			eSum += e[i];
		}
		double eMean = eSum / alive;
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) {
				out[i] = 0;
				continue;
			}
			out[i] = alive == 1 ? 1.0 : Math.max(MIN_W, Math.min(MAX_W, e[i] / eMean));
		}
		return out;
	}
}