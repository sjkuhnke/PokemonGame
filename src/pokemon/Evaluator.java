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

	/**
	 * Matchup credit is capped by what the foe can escape to: when I am winning the active duel, the foe can switch to its best
	 * answer on the bench at the price of the free hit it takes, so the credit is at most (that answer's edge against my active)
	 * + this cost. Without the cap the evaluator books "I kill the foe's active before it acts" (about +90) as an asset that no
	 * real opponent leaves standing, and a real KO (material for the dead mon, but the credit resets against the replacement)
	 * scores no better than a chip that leaves the credit in place. Eval points (same scale as matchupScore, about -90..+90).
	 */
	static final double ESCAPE_COST = 30;
	/** A wounded bench mon is a worse answer: its edge is worsened by this many points per missing fraction of its HP. */
	static final double HURT_ANSWER_PENALTY = 60;
	/** A hit that would be lethal but is endured (Sturdy / Focus Sash at full HP, False Swipe) counts as at most this fraction of the target's HP in the matchup: it takes a second hit. */
	static final double ENDURE_FRAC = 0.5;
	/** Fraction of a telegraphed charge that still lands: the foe can switch, Protect, or KO the charger first. Placeholder; tune in self-play. */
	static final double CHARGE_HOLD = 0.75;

	private Evaluator() {}

	public static double eval(SimState s, EvalWeights style, double[] aiWeights, double[] playerWeights) {
		return evalCore(s, style, aiWeights, playerWeights, null);
	}

	/** Eval with the decision's {@link MonWeights}: mon weights plus the escape-capped matchup (see {@link #ESCAPE_COST}). */
	public static double eval(SimState s, EvalWeights style, MonWeights w) {
		return evalCore(s, style, w.ai, w.player, w);
	}

	private static double evalCore(SimState s, EvalWeights style, double[] aiWeights, double[] playerWeights, MonWeights ctx) {
		if (s.player.shell.wiped()) return WIN;
		if (s.ai.shell.wiped()) return -WIN;

		double v = style.wMaterial * (material(s.ai, aiWeights) - material(s.player, playerWeights));
		v += style.wMatchup * matchupTerm(s, ctx);
		v += style.wHazard * (hazardPain(s.player, s.field) - hazardPain(s.ai, s.field));
		v += style.wStatus * (benchStatusValue(s.player, s.field) - benchStatusValue(s.ai, s.field));
		v += style.wField * fieldValue(s);
		v += style.wTempo * tempo(s);
		v += forcedTurnPenalty(s);
		v += chargeCredit(s.ai, s.player, s.field, playerWeights) - chargeCredit(s.player, s.ai, s.field, aiWeights);
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
	public static double activeMatchup(SimState s) {
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

	/**
	 * The matchup term of eval: {@link #activeMatchup} (live: current HP, stages, status), capped at the foe's best escape:
	 * min(live, min over the foe's alive bench of (the AI active's full-HP edge against that mon * 90, worsened for a wounded mon)
	 * + {@link #ESCAPE_COST}). The cap applies at every sign of live, so the term is monotone in the live matchup: a slightly
	 * winning duel is never scored below a losing one. With no context, or no foe bench, it is the live matchup. The AI's own escape
	 * from a bad matchup is deliberately not credited (it keeps the pressure to switch out of one).
	 */
	public static double matchupTerm(SimState s, MonWeights ctx) {
		double live = activeMatchup(s);
		if (ctx == null || ctx.edge == null) return live;
		Pokemon aiMon = s.ai.active(), plMon = s.player.active();
		if (aiMon == null || plMon == null) return live;
		int ai = s.ai.shell.indexOf(aiMon), pl = s.player.shell.indexOf(plMon);
		if (ai < 0 || pl < 0 || ai >= ctx.edge.length) return live;
		double best = Double.POSITIVE_INFINITY;
		Pokemon[] foeTeam = s.player.bench();
		for (int j = 0; j < foeTeam.length && j < ctx.edge[ai].length; j++) {
			Pokemon p = foeTeam[j];
			if (j == pl || p == null || p.isFainted()) continue;
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			best = Math.min(best, ctx.edge[ai][j] * 90 + (1 - hpFrac) * HURT_ANSWER_PENALTY);
		}
		if (best == Double.POSITIVE_INFINITY) return live; // the foe has nothing to escape to
		return Math.min(live, best + ESCAPE_COST);
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
		double f = Math.min(1.0, r.expectedCapped(hp) / hp);
		// A lethal hit that is endured (Focus Sash / Sturdy at full HP, False Swipe) is not a KO: expectedCapped stops it at hp - 1,
		// which would read as ~100%. Count it as needing a second hit. Chip that removes the full-HP condition restores the credit.
		if (r.endures(hp) && Math.max(r.max, r.avg * r.hits) >= hp) f = Math.min(f, ENDURE_FRAC);
		return f;
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
	public static double fieldValue(SimState s) {
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
	public static double tempo(SimState s) {
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
	public static double pendingValue(SimState s, double[] aiWeights, double[] playerWeights) {
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
	
	/** Value of the attack a charging mon releases next turn, in the same units as material. */
	private static double chargeCredit(SideState atkSide, SideState defSide, Field f, double[] defW) {
	    Pokemon atk = atkSide.active(), def = defSide.active();
	    if (atk == null || def == null || atk.isFainted() || def.isFainted()) return 0;
	    if (!atk.hasStatus(Status.CHARGING) && !atk.hasStatus(Status.SEMI_INV)) return 0;
	    Move m = atk.lastMoveUsed;
	    if (m == null) return 0;

	    double hold = CHARGE_HOLD;
	    DamageRange out, back;
	    try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
	        out  = atk.calcRange(def, m, true, f);
	        back = bestRange(def, atk, f);                       // what the foe threatens in the meantime
	    }
	    // If the foe moves first next turn and can KO the charger, the beam never leaves.
	    if (back != null && def.getFaster(atk, 0, 0, f) == def)
	        hold *= 1 - Math.min(1.0, back.accuracy * back.koProb(atk.currentHP));

	    double dmg = out.expectedCapped(def.currentHP);
	    int idx = defSide.shell.indexOf(def);
	    double w = (defW != null && idx >= 0 && idx < defW.length) ? defW[idx] : 1.0;
	    return 100.0 * dmg / def.getStat(0) * w * hold;
	}
}