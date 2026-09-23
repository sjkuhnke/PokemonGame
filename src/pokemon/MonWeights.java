package pokemon;

/**
 * §7.14.1 v1. HP-independent, per-team-slot value weights, computed once per decision from the
 * root snapshot and never recomputed inside matrix cells. Indexed by team-array slot (not by
 * Pokemon reference), since forked/cloned SimStates give bench mons new object identities but
 * keep slot indices stable.
 * <p>
 * v1 scope (per PHASE2_CHANGES.md's own precedent of flagging deferred work): only {@code
 * contribution} is implemented. {@code unique} (only-answer bonus), {@code utility} (hazard
 * setter/absorber/etc.) and {@code aceMultiplier} are explicitly Phase 5 per §7.14.1 and the
 * spec's "Finalize computeMonWeights" task there - implementing them now would risk diverging
 * from Phase 5's real definition of GAP/UNIQUE_W without seeing it exercised by the sacking
 * tests (T18-T21) that are Phase 5's acceptance criteria.
 */
public final class MonWeights {
	private static final double BASE_T = 1.0;
	private static final double BASE_W = 1.0;
	private static final double MIN_W = 0.25, MAX_W = 3.0;

	public final double[] ai;
	public final double[] player;

	private MonWeights(double[] ai, double[] player) {
		this.ai = ai;
		this.player = player;
	}

	public static MonWeights compute(SimState root) {
		double[] aiW = computeSide(root.ai, root.player, root.field);
		double[] plW = computeSide(root.player, root.ai, root.field);
		return new MonWeights(aiW, plW);
	}

	private static double[] computeSide(SideState mine, SideState theirs, Field field) {
		Pokemon[] myTeam = mine.bench();
		Pokemon[] theirTeam = theirs.bench();
		int n = myTeam.length, m = theirTeam.length;

		double[][] edge = new double[n][m];
		for (int i = 0; i < n; i++) {
			if (myTeam[i] == null || myTeam[i].isFainted()) continue;
			for (int j = 0; j < m; j++) {
				if (theirTeam[j] == null || theirTeam[j].isFainted()) continue;
				edge[i][j] = baseEdge(myTeam[i], theirTeam[j], field);
			}
		}

		double[] threat = new double[m];
		for (int j = 0; j < m; j++) {
			if (theirTeam[j] == null || theirTeam[j].isFainted()) continue;
			double sum = 0;
			int count = 0;
			for (int i = 0; i < n; i++) {
				if (myTeam[i] == null || myTeam[i].isFainted()) continue;
				sum += Math.max(0, -edge[i][j]);
				count++;
			}
			threat[j] = BASE_T + (count > 0 ? sum / count : 0);
		}

		double[] raw = new double[n];
		for (int i = 0; i < n; i++) {
			if (myTeam[i] == null || myTeam[i].isFainted()) continue;
			double contribution = 0;
			for (int j = 0; j < m; j++) {
				if (theirTeam[j] == null || theirTeam[j].isFainted()) continue;
				contribution += threat[j] * squash(edge[i][j]);
			}
			// unique[]/utility[]/aceMultiplier deferred to Phase 5, see class doc.
			raw[i] = BASE_W + contribution;
		}
		return normalize(raw, myTeam);
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

	private static double[] normalize(double[] raw, Pokemon[] team) {
		double sum = 0;
		int alive = 0;
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) continue;
			sum += raw[i];
			alive++;
		}
		double mean = alive > 0 ? sum / alive : 1.0;
		double[] out = new double[raw.length];
		for (int i = 0; i < raw.length; i++) {
			if (team[i] == null || team[i].isFainted()) {
				out[i] = 0;
				continue;
			}
			double w = mean > 0 ? raw[i] / mean : 1.0;
			out[i] = Math.max(MIN_W, Math.min(MAX_W, w));
		}
		return out;
	}
}