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
 * <li>{@code hazardPain} scales {@code calcHazardTeamValue} (which values one layer) by
 * {@link #hazardLayerScale} (Phase 4: Spikes 1 : 1.33 : 2, Toxic Spikes 1 : 1.67).</li>
 * <li>{@code benchStatusValue} uses flat per-status points instead of reusing
 * {@code scoreInflictedStatus} (which needs a specific incoming-move context that doesn't
 * generalize to "any bench mon vs an unknown future opponent"). Natural Cure is handled (Phase 4).</li>
 * <li>{@code tempo}/{@code forcedTurnPenalty} use flat constants rather than move-specific
 * recharge/lock durations.</li>
 * </ul>
 */
public final class Evaluator {
	static final double WIN = 1000;
	static final double ALIVE_BONUS = 20;

	private Evaluator() {}

	public static double eval(SimState s, EvalWeights style, double[] aiWeights, double[] playerWeights) {
		if (s.player.shell.wiped()) return WIN;
		if (s.ai.shell.wiped()) return -WIN;

		double v = style.wMaterial * (material(s.ai, aiWeights) - material(s.player, playerWeights));
		v += style.wMatchup * activeMatchup(s);
		v += style.wHazard * (hazardPain(s.player, s.field) - hazardPain(s.ai, s.field));
		v += style.wStatus * (benchStatusValue(s.player, s.field) - benchStatusValue(s.ai, s.field));
		v += style.wField * fieldValue(s);
		v += style.wTempo * tempo(s);
		v += forcedTurnPenalty(s);
		v += style.wPending * pendingValue(s, aiWeights, playerWeights);
		return v;
	}

	/** material(side) = Sum over alive mons: 100 * hpFrac * monWeight, plus ALIVE_BONUS * aliveCount (§7.8). */
	public static double material(SideState side, double[] weights) {
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

	static double fracOf(DamageRange r, double hp) {
		if (r == null || hp <= 0) return 0;
		return Math.min(1.0, r.expectedCapped(hp) / hp);
	}

	/** hazardPain(side) = Sum over active hazard effects on side's own field: calcHazardTeamValue, scaled by layers (v1, see class doc). */
	public static double hazardPain(SideState side, Field field) {
		Pokemon anchor = side.active();
		if (anchor == null) return 0;
		double total = 0;
		for (Field.FieldEffect fe : side.shell.getFieldEffectList()) {
			Move hazardMove = anchor.hazardMoveForEffect(fe.effect);
			if (hazardMove == null) continue;
			total += anchor.calcHazardTeamValue(hazardMove, side.shell, field) * hazardLayerScale(hazardMove, fe.layers);
		}
		return total;
	}

	/**
	 * Phase 4: calcHazardTeamValue values ONE layer. Spikes deal 1/8, 1/6, 1/4 of max HP for 1, 2, 3 layers (relative
	 * 1 : 1.33 : 2); Toxic Spikes poison at 1 layer and badly poison at 2 (relative 1 : 1.67, matching statusPenalty's
	 * 15 : 25). Everything else is single-layer.
	 */
	public static double hazardLayerScale(Move hazardMove, int layers) {
		int n = Math.max(1, layers);
		if (hazardMove == Move.SPIKES) return n >= 3 ? 2.0 : n == 2 ? 4.0 / 3.0 : 1.0;
		if (hazardMove == Move.TOXIC_SPIKES) return n >= 2 ? 5.0 / 3.0 : 1.0;
		return 1.0;
	}

	/** benchStatusValue = Sum over alive BENCH mons (active handled via activeMatchup): status penalty * hpFrac. */
	public static double benchStatusValue(SideState side, Field field) {
		Pokemon active = side.active();
		double total = 0;
		for (Pokemon p : side.bench()) {
			if (p == null || p.isFainted() || p == active) continue;
			if (p.getAbility(field) == Ability.NATURAL_CURE) continue; // cured on switch-in (§9 Aromatherapy/Heal Bell row; Phase 3 deferral)
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			total += statusPenalty(p.status) * hpFrac;
		}
		return total;
	}

	/**
	 * Flat per-status points (v1; see class doc for why this doesn't reuse scoreInflictedStatus).
	 * Natural Cure is handled by the caller (benchStatusValue skips those mons).
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
			// weatherTurns counts down and the weather ends at 0; <= 0 while a weather is set means "indefinite".
			v += aiAnchor.scoreWeatherValue(s.field.weather.effect, plAnchor) * durationFactor(s.field.weatherTurns);
		}
		for (Field.FieldEffect fe : s.ai.shell.getFieldEffectList()) {
			if (isScreen(fe.effect)) v += aiAnchor.scoreScreenValue(fe.effect, plAnchor, null) * durationFactor(fe.turns);
		}
		for (Field.FieldEffect fe : s.player.shell.getFieldEffectList()) {
			if (isScreen(fe.effect)) v -= plAnchor.scoreScreenValue(fe.effect, aiAnchor, null) * durationFactor(fe.turns);
		}
		return v;
	}

	/** Phase 4 (§9 "duration-weighted"): a field effect with 1 turn left is worth a quarter of one with 4+. turns <= 0 = indefinite. */
	public static double durationFactor(int turnsLeft) {
		if (turnsLeft <= 0) return 1.0;
		return Math.min(1.0, turnsLeft / 4.0);
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
	public static double forcedTurnPenalty(SimState s) {
		double v = 0;
		Pokemon a = s.ai.active(), p = s.player.active();
		if (a != null && !a.isFainted() && isForced(a)) v -= 15;
		if (p != null && !p.isFainted() && isForced(p)) v += 15;
		return v;
	}

	private static boolean isForced(Pokemon p) {
		return p.hasStatus(Status.RECHARGE) || p.hasStatus(Status.CHARGING) || p.hasStatus(Status.LOCKED);
	}

	// ---- Phase 4 terms (§9): pending recovery effects and action-set restriction ----

	/** Cap so a restriction never outweighs losing a mon; it only breaks ties between otherwise similar rows. */
	private static final double RESTRICTION_MAX = 12;

	/**
	 * pendingValue = (AI side's pending recovery - player side's) - (AI's restriction cost - player's).
	 * Healing Wish / Lunar Dance: the best bench beneficiary's missing HP (plus its status for Lunar Dance and Healing
	 * Wish alike, since both cure it). Wish: the heal the active mon will get, capped at its missing HP.
	 * A Healing Wish sacrifice is a MOVE row that KOs the user; this term is what pays for it (§9).
	 */
	static double pendingValue(SimState s, double[] aiWeights, double[] playerWeights) {
		double v = pendingRecovery(s.ai, aiWeights, s.field) - pendingRecovery(s.player, playerWeights, s.field);
		v -= restrictionCost(s.ai, s.field);
		v += restrictionCost(s.player, s.field);
		return v;
	}

	public static double pendingRecovery(SideState side, double[] weights, Field field) {
		double total = 0;
		Pokemon[] team = side.bench();
		Pokemon active = side.active();
		for (Field.FieldEffect fe : side.shell.getFieldEffectList()) {
			if (fe.effect == Effect.HEALING_WISH || fe.effect == Effect.LUNAR_DANCE) {
				double best = 0;
				for (int i = 0; i < team.length; i++) {
					Pokemon p = team[i];
					if (p == null || p.isFainted() || p == active) continue;
					double missing = 1.0 - p.currentHP * 1.0 / p.getStat(0);
					double w = (weights != null && i < weights.length) ? weights[i] : 1.0;
					best = Math.max(best, 100.0 * missing * w + statusPenalty(p.status));
				}
				total += best;
			} else if (fe.effect == Effect.WISH && active != null && !active.isFainted()) {
				int max = active.getStat(0);
				double heal = Math.min(fe.stat, max - active.currentHP);
				int idx = side.shell.indexOf(active);
				double w = (weights != null && idx >= 0 && idx < weights.length) ? weights[idx] : 1.0;
				total += Math.max(0, 100.0 * heal / max * w);
			}
		}
		return total;
	}

	/**
	 * Cost of an active mon's move restrictions: RESTRICTION_MAX * (fraction of its moves it can no longer use), only
	 * while a Taunt / Disable / Encore / Torment is actually on it. Choice-item locks are excluded on purpose: Phase 3's
	 * choice-lock handling already prices those, and counting them here would double-charge.
	 */
	public static double restrictionCost(SideState side, Field field) {
		Pokemon a = side.active();
		if (a == null || a.isFainted() || a.moveset == null) return 0;
		if (!(a.hasStatus(Status.TAUNTED) || a.hasStatus(Status.ENCORED) || a.hasStatus(Status.TORMENTED) || a.disabledMove != null)) return 0;
		Item it = a.getItem(field);
		if (it != null && it.isChoiceItem()) return 0;
		int total = 0;
		for (Moveslot ms : a.moveset) if (ms != null && ms.move != null) total++;
		int valid = a.getValidMoveset().size();
		if (total == 0 || valid >= total) return 0;
		if (valid == 0) return RESTRICTION_MAX; // Struggle-only
		return RESTRICTION_MAX * (1.0 - valid * 1.0 / total);
	}
}
