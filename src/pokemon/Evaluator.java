package pokemon;

import pokemon.Field.Effect;

/**
 * §7.8. Turns a {@link SimState} into a signed "roughly HP-points" value from the AI's
 * perspective (100 ~ one full healthy mon). Called once per branch inside
 * {@link MatrixBuilder#buildMatrix} and again, restricted to the AI's own moves, inside
 * {@link ActionGen#deadTurn}.
 * <p>
 * Known v1 simplifications, flagged per §0 rather than hidden (see PHASE3_CHANGES.md):
 * <ul>
 * <li>{@code hazardPain} multiplies {@code calcHazardTeamValue} by the effect's layer count;
 * the real method does not itself scale by layers (Spikes/Toxic Spikes stack), so this is an
 * external approximation until Phase 4/5 revisit hazard scoring properly.</li>
 * <li>{@code benchStatusValue} uses flat per-status points instead of reusing
 * {@code scoreInflictedStatus} (which needs a specific incoming-move context that doesn't
 * generalize to "any bench mon vs an unknown future opponent"), and is not yet Natural-Cure
 * aware (§9 checklist item, deferred to Phase 4).</li>
 * <li>{@code tempo}/{@code forcedTurnPenalty} use flat constants rather than move-specific
 * recharge/lock durations.</li>
 * </ul>
 */
final class Evaluator {
	static final double WIN = 1000;
	static final double ALIVE_BONUS = 20;

	private Evaluator() {}

	static double eval(SimState s, EvalWeights style, double[] aiWeights, double[] playerWeights) {
		if (s.player.shell.wiped()) return WIN;
		if (s.ai.shell.wiped()) return -WIN;

		double v = style.wMaterial * (material(s.ai, aiWeights) - material(s.player, playerWeights));
		v += style.wMatchup * activeMatchup(s);
		v += style.wHazard * (hazardPain(s.player, s.field) - hazardPain(s.ai, s.field));
		v += style.wStatus * (benchStatusValue(s.player, s.field) - benchStatusValue(s.ai, s.field));
		v += style.wField * fieldValue(s);
		v += style.wTempo * tempo(s);
		v += forcedTurnPenalty(s);
		return v;
	}

	/** material(side) = Sum over alive mons: 100 * hpFrac * monWeight, plus ALIVE_BONUS * aliveCount (§7.8). */
	static double material(SideState side, double[] weights) {
		Pokemon[] team = side.bench();
		double total = 0;
		int alive = 0;
		for (int i = 0; i < team.length; i++) {
			Pokemon p = team[i];
			if (p == null || p.isFainted()) continue;
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			double w = (weights != null && i < weights.length) ? weights[i] : 1.0;
			total += 100.0 * hpFrac * w;
			alive++;
		}
		return total + ALIVE_BONUS * alive;
	}

	/**
	 * bestRange(ai->player) and bestRange(player->ai), fed as expected-damage fractions into
	 * the existing matchupScore formula (unchanged constants) - matches §7.8's "myR.frac,
	 * foeR.frac" naming: expectedCapped/hp, not just the max roll.
	 */
	static double activeMatchup(SimState s) {
		Pokemon aiMon = s.ai.active(), plMon = s.player.active();
		if (aiMon == null || plMon == null || aiMon.isFainted() || plMon.isFainted()) return 0;

		DamageRange myR, foeR;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			myR = bestRange(aiMon, plMon, s.field);
			foeR = bestRange(plMon, aiMon, s.field);
		}
		boolean iAmFaster = aiMon.getFaster(plMon, 0, 0, s.field) == aiMon;
		double myFrac = fracOf(myR, plMon.currentHP);
		double foeFrac = fracOf(foeR, aiMon.currentHP);
		return aiMon.matchupScore((int) Math.round(myFrac * 100), foeFrac * 100, iAmFaster);
	}

	/** Attacker's best valid move against defender, ranked by expected-capped damage. Null if nothing lands. */
	static DamageRange bestRange(Pokemon attacker, Pokemon defender, Field field) {
		DamageRange best = null;
		double bestScore = -1;
		for (Move m : attacker.getValidMoveset()) {
			DamageRange r = attacker.calcRange(defender, m, true, field);
			if (!r.usable || r.immune) continue;
			double score = r.expectedCapped(defender.currentHP);
			if (score > bestScore) {
				bestScore = score;
				best = r;
			}
		}
		return best;
	}

	private static double fracOf(DamageRange r, double hp) {
		if (r == null || hp <= 0) return 0;
		return Math.min(1.0, r.expectedCapped(hp) / hp);
	}

	/** hazardPain(side) = Sum over active hazard effects on side's own field: calcHazardTeamValue, scaled by layers (v1, see class doc). */
	static double hazardPain(SideState side, Field field) {
		Pokemon anchor = side.active();
		if (anchor == null) return 0;
		double total = 0;
		for (Field.FieldEffect fe : side.shell.getFieldEffectList()) {
			Move hazardMove = anchor.hazardMoveForEffect(fe.effect);
			if (hazardMove == null) continue;
			int layers = Math.max(1, fe.layers);
			total += anchor.calcHazardTeamValue(hazardMove, side.shell, field) * layers;
		}
		return total;
	}

	/** benchStatusValue = Sum over alive BENCH mons (active handled via activeMatchup): status penalty * hpFrac. */
	static double benchStatusValue(SideState side, Field field) {
		Pokemon active = side.active();
		double total = 0;
		for (Pokemon p : side.bench()) {
			if (p == null || p.isFainted() || p == active) continue;
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			total += statusPenalty(p.status) * hpFrac;
		}
		return total;
	}

	/**
	 * Flat per-status points (v1; see class doc for why this doesn't reuse scoreInflictedStatus).
	 * Not yet Natural Cure aware - §9 checklist item for Phase 4.
	 */
	private static double statusPenalty(Status status) {
		if (status == null || status == Status.HEALTHY) return 0;
		switch (status) {
		case BURNED: return 25;
		case FROSTBITE: return 25;
		case PARALYZED: return 25;
		case POISONED: return 15;
		case TOXIC: return 25;
		case ASLEEP: return 40;
		default: return 0;
		}
	}

	/** fieldValue(s) = net weather value (AI perspective, already nets both sides) + AI screens - player screens. */
	static double fieldValue(SimState s) {
		Pokemon aiAnchor = s.ai.active(), plAnchor = s.player.active();
		if (aiAnchor == null || plAnchor == null) return 0;

		double v = 0;
		if (s.field.weather != null) {
			v += aiAnchor.scoreWeatherValue(s.field.weather.effect, plAnchor);
		}
		for (Field.FieldEffect fe : s.ai.shell.getFieldEffectList()) {
			if (isScreen(fe.effect)) v += aiAnchor.scoreScreenValue(fe.effect, plAnchor, null);
		}
		for (Field.FieldEffect fe : s.player.shell.getFieldEffectList()) {
			if (isScreen(fe.effect)) v -= plAnchor.scoreScreenValue(fe.effect, aiAnchor, null);
		}
		return v;
	}

	private static boolean isScreen(Effect e) {
		return e == Effect.REFLECT || e == Effect.LIGHT_SCREEN || e == Effect.AURORA_VEIL;
	}

	/** tempo(s) = small bonus for holding initiative (faster, or foe recharging/trapped); negative if not. */
	static double tempo(SimState s) {
		Pokemon a = s.ai.active(), p = s.player.active();
		if (a == null || p == null || a.isFainted() || p.isFainted()) return 0;
		boolean aiFaster = a.getFaster(p, 0, 0, s.field) == a;
		double v = aiFaster ? 5 : -5;
		if (p.hasStatus(Status.RECHARGE) || p.hasStatus(Status.TRAPPED)) v += 5;
		if (a.hasStatus(Status.RECHARGE)) v -= 5;
		return v;
	}

	/** forcedTurnPenalty(s): a mon locked into recharge/charging/a locked move costs/gains a turn. */
	static double forcedTurnPenalty(SimState s) {
		double v = 0;
		Pokemon a = s.ai.active(), p = s.player.active();
		if (a != null && !a.isFainted() && isForced(a)) v -= 15;
		if (p != null && !p.isFainted() && isForced(p)) v += 15;
		return v;
	}

	private static boolean isForced(Pokemon p) {
		return p.hasStatus(Status.RECHARGE) || p.hasStatus(Status.CHARGING) || p.hasStatus(Status.LOCKED);
	}
}