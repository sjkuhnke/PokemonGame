package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pokemon.Field.Effect;
import pokemon.*;

/**
 * Phase 5 tests (§11 T18-T22, T26, plus the pieces they rest on). Run from the game thread like Phase4Tests:
 * {@code Phase5Tests.runAll();}. Reuses Phase4Tests' scenario builders (mk / Duel).
 * <p>
 * Engine-free tests (pure math, synthetic matrices) run anywhere the classes load. Scenario tests build small teams and
 * SKIP (with a reason) instead of failing when the species/moves at hand cannot produce the precondition, per Phase 4's
 * precedent - a SKIP is "could not set this position up", never a pass.
 * <p>
 * T21 is matrix form only: the "player model expects a switch" half needs Phase 6's predict(); until then it asserts the
 * payoff structure the model will act on (the sack's advantage over Stay is larger against an attack than a switch).
 * <p>
 * {@link #selfPlayComparison} is the manual acceptance run (HARD with sacking vs HARD without); it needs the live game
 * (Pokemon.gp), so it is not part of runAll.
 */
public final class Phase5Tests {
	private Phase5Tests() {}

	private static final int[] FOE_IDS = { 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34, 37 };
	private static final Move[] STRONG = { Move.EARTHQUAKE, Move.SURF, Move.FLAMETHROWER, Move.ICE_BEAM, Move.THUNDERBOLT,
			Move.PSYCHIC, Move.SHADOW_BALL, Move.SLUDGE_BOMB, Move.STONE_EDGE, Move.CLOSE_COMBAT };

	private static int passed, failed, skipped;

	private interface Body { void run() throws Exception; }

	private static final class Skip extends RuntimeException {
		private static final long serialVersionUID = 1L;
		Skip(String why) { super(why); }
	}

	public static void runAll() {
		passed = failed = skipped = 0;
		Field prevField = Pokemon.field;
		try {
			// pure / engine-free
			run("uniqueBonuses (only-answer rule)", Phase5Tests::uniqueBonuses);
			run("min-gain guard on synthetic matrices", Phase5Tests::minGainGuard);
			run("replacement score: future value + finite faint score", Phase5Tests::replacementScorePure);
			// engine
			run("T26 mon weights", Phase5Tests::t26);
			run("utility: hazard remover only counts while hazards are up", Phase5Tests::utilityRemover);
			run("SwitchInScorer: faint-on-entry is finite, chooser avoids it", Phase5Tests::scorerFinite);
			run("T22 replacement consistency (sim == real == chooser)", Phase5Tests::t22);
			run("T18 sack scenario", Phase5Tests::t18);
			run("T19 sack needs a value gap", Phase5Tests::t19);
			run("T20 no sack without a threat", Phase5Tests::t20);
			run("T21 sack vs switch column (matrix form)", Phase5Tests::t21);
			run("enableSacking=false removes sack candidates", Phase5Tests::sackingOff);
		} finally {
			Pokemon.field = prevField;
		}
		System.out.println("[Phase5Tests] " + passed + " passed, " + failed + " FAILED, " + skipped + " skipped");
	}

	private static void run(String label, Body b) {
		try {
			b.run();
			passed++;
			System.out.println(label + " passed.");
		} catch (Skip s) {
			skipped++;
			System.out.println(label + " SKIPPED: " + s.getMessage());
		} catch (Throwable t) {
			failed++;
			System.out.println(label + " FAILED: " + t);
		}
	}

	private static void check(String what, boolean ok) {
		if (!ok) throw new AssertionError(what);
	}

	private static void close(String what, double actual, double expected, double tol) {
		if (Math.abs(actual - expected) > tol) throw new AssertionError(what + ": expected ~" + expected + ", got " + actual);
	}

	// ------------------------------------------------------------------ scenario building

	/** Same as Phase4Tests.duel but with caller-chosen teams (index 0 of each is the active mon). */
	private static Phase4Tests.Duel build(Pokemon[] at, Pokemon[] pt) {
		Trainer ta = new Trainer("P5-ai", at, 0);
		Trainer tp = new Trainer("P5-pl", pt, 0);
		ta.boosts = new int[3];
		tp.boosts = new int[3];
		Field field = new Field();
		Pokemon.field = field;
		field.clear(ta, tp);
		ta.setCurrent(at[0]);
		tp.setCurrent(pt[0]);
		Phase4Tests.Duel d = new Phase4Tests.Duel();
		d.ai = ta; d.pl = tp; d.a = at[0]; d.f = pt[0]; d.field = field;
		return d;
	}

	private static Field.FieldEffect effect(Effect e, int layers) {
		Field.FieldEffect fe = Pokemon.field.new FieldEffect(e);
		fe.layers = layers;
		return fe;
	}

	private static boolean dealsDamage(Pokemon atk, Pokemon def, Move m, Field field) {
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			DamageRange r = atk.calcRange(def, m, true, field);
			return r.usable && !r.immune && r.dealsDamage();
		}
	}

	private static Pokemon[] foeTeam(Pokemon foe) {
		return new Pokemon[] { foe, Phase4Tests.mk(5, Move.FLAMETHROWER), Phase4Tests.mk(6, Move.FLAMETHROWER) };
	}

	private static final class Scen {
		final Phase4Tests.Duel d;
		@SuppressWarnings("unused")
		final Move foeMove;
		Scen(Phase4Tests.Duel d, Move foeMove) { this.d = d; this.foeMove = foeMove; }
	}

	/**
	 * AI: ace (species 1, level 20, full HP), scrub (bench slot 1, hp fraction scrubHp; a twin of the ace when twin), answer
	 * (slot 2, healthy, level 50). Player: a foe of foeLevel with one strong move, plus two bench mons. kill=true needs the
	 * foe's move to KO both the ace and the scrub (>= 0.9); kill=false needs the foe NOT to threaten the ace at all.
	 * First (foe species, move) that fits, or null.
	 */
	private static Scen sackScenario(double scrubHp, boolean twin, int foeLevel, boolean kill) {
		for (int id : FOE_IDS) {
			for (Move mv : STRONG) {
				Pokemon ace = Phase4Tests.mk(1, 20, Move.FLAMETHROWER);
				Pokemon scrub = twin ? Phase4Tests.mk(1, 20, Move.FLAMETHROWER) : Phase4Tests.mk(2, 20, Move.FLAMETHROWER);
				Pokemon answer = Phase4Tests.mk(3, 50, Move.FLAMETHROWER);
				scrub.currentHP = Math.max(1, (int) Math.round(scrub.getStat(0) * scrubHp));
				Pokemon foe = Phase4Tests.mk(id, foeLevel, mv);
				Phase4Tests.Duel d = build(new Pokemon[] { ace, scrub, answer }, foeTeam(foe));
				boolean ok;
				if (kill) {
					ok = SackAnalysis.koChance(foe, ace, mv, d.field) >= 0.9 && SackAnalysis.koChance(foe, scrub, mv, d.field) >= 0.9;
				} else {
					ok = !SackAnalysis.threatens(foe, ace, d.field);
				}
				if (ok) return new Scen(d, mv);
			}
		}
		return null;
	}

	private static int switchRow(List<Action> A, int slot) {
		for (int i = 0; i < A.size(); i++) if (A.get(i).kind == ActionKind.SWITCH && A.get(i).slot == slot) return i;
		return -1;
	}

	// ------------------------------------------------------------------ pure tests

	private static void uniqueBonuses() {
		boolean[] all3 = { true, true, true };
		boolean[] one = { true };
		double[] threat = { 1.5 };

		double[] r = MonWeights.uniqueBonuses(new double[][] { { 0.8 }, { 0.1 }, { -0.5 } }, threat, all3, one);
		close("clear best answer gets the foe's threat", r[0], 1.5, 1e-9);
		close("runner-up gets nothing", r[1], 0, 1e-9);
		close("non-answer gets nothing", r[2], 0, 1e-9);

		r = MonWeights.uniqueBonuses(new double[][] { { 0.8 }, { 0.7 }, { -0.5 } }, threat, all3, one);
		check("two comparable answers -> nobody is unique", r[0] == 0 && r[1] == 0 && r[2] == 0);

		r = MonWeights.uniqueBonuses(new double[][] { { -0.2 }, { -0.4 }, { -0.9 } }, threat, all3, one);
		check("best of a bad lot is not an answer (edge must be > 0)", r[0] == 0);

		r = MonWeights.uniqueBonuses(new double[][] { { 0.9 }, { 0.1 } }, threat, new boolean[] { true, false }, one);
		check("a lone alive mon has no runner-up, so no bonus", r[0] == 0);

		r = MonWeights.uniqueBonuses(new double[][] { { 0.9 }, { 0.1 } }, threat, new boolean[] { true, true }, new boolean[] { false });
		check("fainted foes are ignored", r[0] == 0 && r[1] == 0);

		double[] two = { 1.0, 2.0 };
		r = MonWeights.uniqueBonuses(new double[][] { { 0.9, 0.0 }, { 0.1, 0.8 } }, two, new boolean[] { true, true }, new boolean[] { true, true });
		close("mon 0 uniquely handles foe 0", r[0], 1.0, 1e-9);
		close("mon 1 uniquely handles foe 1", r[1], 2.0, 1e-9);
	}

	private static List<Action> rows(Action... a) { return new ArrayList<>(Arrays.asList(a)); }

	private static void minGainGuard() {
		Action m1 = new Action(Move.FLAMETHROWER), m2 = new Action(Move.GROWL);
		Action sack = new Action(1), answer = new Action(2), pivotSack = new Action(Move.FLAMETHROWER, 1);
		Set<Integer> sackOnly = new HashSet<>(Arrays.asList(1));
		boolean[] threat = { true, false }; // column 0 = a KO threat, column 1 = a non-threat

		// A: sack beats Stay by 30 in the threat column -> kept.
		List<Action> A = rows(m1, m2, sack, answer);
		double[][] M = { { -50, 0 }, { -60, -5 }, { -20, -30 }, { -10, -10 } };
		SackAnalysis.Filtered f = SackAnalysis.applyMinGain(A, M, threat, sackOnly, 10);
		check("A: large gain keeps the sack row", f.A.size() == 4 && f.dropped.isEmpty());
		close("A: gain is row minus best Stay in the threat column", f.gain[2], 30, 1e-9);
		check("A: non-sack rows carry NaN gain", Double.isNaN(f.gain[0]) && Double.isNaN(f.gain[3]));

		// B: gain 5 < 10 -> the sack (and the pivot-sack) rows go, answer + moves stay, rows stay aligned with M.
		A = rows(m1, m2, sack, answer, pivotSack);
		M = new double[][] { { -50, 0 }, { -60, -5 }, { -45, -30 }, { -10, -10 }, { -46, -20 } };
		f = SackAnalysis.applyMinGain(A, M, threat, sackOnly, 10);
		check("B: sack and pivot-sack rows dropped", f.dropped.size() == 2 && f.A.size() == 3);
		check("B: answer row is never judged", f.A.contains(answer));
		check("B: M rows stay aligned with A", f.M.length == 3 && f.M[2][0] == -10 && f.M[0][0] == -50);

		// C: no threat column at all -> every sack row is dropped, however good it looks (T20's rule).
		A = rows(m1, m2, sack, answer);
		M = new double[][] { { 0, 0 }, { 0, 0 }, { 500, 500 }, { -10, -10 } };
		f = SackAnalysis.applyMinGain(A, M, new boolean[] { false, false }, sackOnly, 10);
		check("C: no threat -> sack row dropped", f.dropped.size() == 1 && f.A.size() == 3 && !f.A.contains(sack));

		// D: nothing sack-only -> untouched.
		A = rows(m1, m2, sack, answer);
		M = new double[][] { { -50, 0 }, { -60, -5 }, { -45, -30 }, { -10, -10 } };
		f = SackAnalysis.applyMinGain(A, M, threat, new HashSet<Integer>(), 10);
		check("D: empty sackOnly changes nothing", f.A == A && f.M == M && f.dropped.isEmpty());

		// E: no plain MOVE row to compare with -> keep everything rather than guess.
		A = rows(sack, answer);
		M = new double[][] { { -45, -30 }, { -10, -10 } };
		f = SackAnalysis.applyMinGain(A, M, threat, sackOnly, 10);
		check("E: no Stay row -> keep all", f.A.size() == 2);
	}

	private static void replacementScorePure() {
		check("ENTRY_FAINT_SCORE is finite, far from int overflow", SwitchInScorer.ENTRY_FAINT_SCORE > Integer.MIN_VALUE / 2);
		check("ENTRY_FAINT_SCORE is below any matchupScore (about -90..+90)", SwitchInScorer.ENTRY_FAINT_SCORE < -100);
		close("combine subtracts weight*hp*FUTURE_W", ReplacementChooser.combine(30, 1.0, 1.0), 30 - ReplacementChooser.FUTURE_W, 1e-9);
		double cheap = ReplacementChooser.combine(30, 0.5, 1.0), dear = ReplacementChooser.combine(30, 2.0, 1.0);
		check("equal entry quality: the less valuable mon is spent first", cheap > dear);
		check("a much better entry still beats a cheaper mon", ReplacementChooser.combine(60, 2.0, 1.0) > ReplacementChooser.combine(20, 0.25, 1.0));
	}

	// ------------------------------------------------------------------ engine tests

	private static void t26() {
		Phase4Tests.Duel d = build(
				new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), Phase4Tests.mk(2, Move.FLAMETHROWER), Phase4Tests.mk(3, Move.FLAMETHROWER) },
				foeTeam(Phase4Tests.mk(4, Move.FLAMETHROWER)));
		MonWeights w = MonWeights.compute(d.root());

		boolean clamped = false;
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			sum += w.ai[i];
			if (w.ai[i] <= 0.25 + 1e-9 || w.ai[i] >= 3.0 - 1e-9) clamped = true;
		}
		if (clamped) throw new Skip("a weight sits on its clamp, so the mean is not exactly 1 (" + Arrays.toString(w.ai) + ")");
		close("weights average 1.0 per side", sum / 3, 1.0, 1e-9);

		// HP does not change a weight.
		double[] before = w.ai.clone();
		d.ai.team[1].currentHP = 1;
		MonWeights w2 = MonWeights.compute(d.root());
		for (int i = 0; i < 3; i++) close("HP-independent weight [" + i + "]", w2.ai[i], before[i], 1e-9);

		// Fainted mons are excluded (weight 0) and the rest re-average to 1.
		d.ai.team[2].currentHP = 0;
		d.ai.team[2].fainted = true;
		MonWeights w3 = MonWeights.compute(d.root());
		close("fainted mon has weight 0", w3.ai[2], 0, 1e-12);
		if (w3.ai[0] > 0.25 + 1e-9 && w3.ai[0] < 3.0 - 1e-9 && w3.ai[1] > 0.25 + 1e-9 && w3.ai[1] < 3.0 - 1e-9) {
			close("survivors re-average to 1.0", (w3.ai[0] + w3.ai[1]) / 2, 1.0, 1e-9);
		}

		// A mon that answers the foe outweighs one (same species) that answers nothing.
		for (Move mv : STRONG) {
			Pokemon foe = Phase4Tests.mk(4, Move.FLAMETHROWER);
			Pokemon x = Phase4Tests.mk(2, mv), y = Phase4Tests.mk(2);
			Phase4Tests.Duel e = build(new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), x, y }, foeTeam(foe));
			if (!dealsDamage(x, foe, mv, e.field)) continue;
			MonWeights we = MonWeights.compute(e.root());
			check("mon with a damaging answer (" + mv + ") outweighs the same species with no moves: " + Arrays.toString(we.ai),
					we.ai[1] > we.ai[2]);
			return;
		}
		throw new Skip("no scanned move damages the foe");
	}

	private static void utilityRemover() {
		Pokemon foe = Phase4Tests.mk(4, Move.FLAMETHROWER);
		Phase4Tests.Duel d = build(new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), Phase4Tests.mk(2, Move.DEFOG), Phase4Tests.mk(2) }, foeTeam(foe));
		MonWeights none = MonWeights.compute(d.root());
		close("no hazards up: a Defog user is worth the same as the same species with no moves", none.ai[1], none.ai[2], 1e-9);

		d.ai.getFieldEffectList().add(effect(Effect.STEALTH_ROCKS, 1));
		MonWeights up = MonWeights.compute(d.root());
		check("hazards on our side: the remover outweighs the non-remover (" + Arrays.toString(up.ai) + ")", up.ai[1] > up.ai[2]);
	}

	private static void scorerFinite() {
		Phase4Tests.Duel d = build(
				new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), Phase4Tests.mk(2, Move.FLAMETHROWER), Phase4Tests.mk(3, Move.FLAMETHROWER) },
				foeTeam(Phase4Tests.mk(4, Move.FLAMETHROWER)));
		Pokemon cand = d.ai.team[1];
		d.ai.getFieldEffectList().add(effect(Effect.STEALTH_ROCKS, 1));
		cand.currentHP = 1;
		if (cand.getItem(d.field) == Item.HEAVY$DUTY_BOOTS || cand.getAbility(d.field) == Ability.MAGIC_GUARD) throw new Skip("candidate ignores Stealth Rock");

		check("1 HP into Stealth Rock -> ENTRY_FAINT_SCORE, not the old sentinel",
				cand.evaluateSwitchInScore(d.f, d.field) == SwitchInScorer.ENTRY_FAINT_SCORE);
		check("real candidate untouched", cand.currentHP == 1 && !cand.isFainted());
		int pick = ReplacementChooser.pickSlot(d.ai, d.f, d.field);
		check("the chooser does not pick the mon that dies on entry (picked " + pick + ")", pick >= 0 && pick != 1);
	}

	private static void t22() {
		int variants = 0;
		for (int variant = 0; variant < 3; variant++) {
			for (int id : FOE_IDS) {
				Pokemon foe = Phase4Tests.mk(id, Move.FLAMETHROWER);
				Phase4Tests.Duel d = build(
						new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), Phase4Tests.mk(2, Move.FLAMETHROWER), Phase4Tests.mk(3, Move.FLAMETHROWER),
								Phase4Tests.mk(7, Move.FLAMETHROWER) },
						foeTeam(foe));
				if (variant == 1) d.ai.getFieldEffectList().add(effect(Effect.STEALTH_ROCKS, 1)); // entry hazards on the replacement
				if (variant == 2) { // the foe is already hurt and burned: the chooser must score against the POST-turn foe
					foe.currentHP = Math.max(1, foe.getStat(0) / 3);
					foe.status = Status.BURNED;
				}
				d.a.currentHP = 0;
				d.a.fainted = true;

				SimState root = SimState.snapshot(d.a, d.f);
				List<Branch> bs = BattleSimulator.simulateTurn(root, Action.PASS, Action.PASS, AIConfig.hard());
				check("one branch for a no-op turn", bs.size() == 1);
				SideState after = bs.get(0).state.ai;
				int simSlot = after.shell.indexOf(after.active());

				int direct = ReplacementChooser.pickSlot(d.ai, d.f, d.field);
				Pokemon real = d.ai.next(d.f, false); // the real battle's forced-replacement path (Trainer.getNext2)
				int realSlot = d.ai.indexOf(real);

				check("foe " + id + " variant " + variant + ": sim replacement " + simSlot + " == real " + realSlot + " == chooser " + direct,
						simSlot == realSlot && realSlot == direct);
				variants++;
			}
		}
		check("ran scenarios", variants > 0);
	}

	private static void t18() {
		Scen sc = sackScenario(0.15, false, 100, true);
		if (sc == null) throw new Skip("no scanned foe/move KOs both a level-20 ace and its 15%-HP teammate");
		SimState root = sc.d.root();
		AIConfig hard = AIConfig.hard();
		MonWeights w = MonWeights.compute(root);
		if (!SackAnalysis.threatens(sc.d.f, sc.d.a, sc.d.field)) throw new Skip("foe does not threaten the ace");
		if (!ActionGen.benchPlan(root, hard, w).sacks.contains(1)) {
			throw new Skip("the low-HP teammate is not a sack candidate (weights " + Arrays.toString(w.ai) + ") - tune the scenario's levels/HP");
		}

		AIV2.Plan p = AIV2.plan(root, hard);
		int row = switchRow(p.A, 1);
		check("a switch row into the sack candidate exists after the min-gain guard" + (p.guard != null ? " (guard dropped " + p.guard.dropped + ", gains " + Arrays.toString(p.guard.gain) + ")" : ""), row >= 0);
		check("HARD: the sack row gets the majority of the probability (x=" + Arrays.toString(p.x) + ")", p.x[row] > 0.5);

		Action chosen = p.A.get(row);
		SackAnalysis.Label label = SackAnalysis.classify(root, chosen, p.P, p.eq.y, hard);
		check("classified as a sack (P(target faints) = " + label.pTargetFaints + ")", label.sack);
		check("replacement plan is a different, alive teammate (slot " + label.replacementSlot + ")", label.replacementSlot >= 0 && label.replacementSlot != 1);

		AIConfig normal = AIConfig.normal();
		if (ActionGen.deadTurn(root, normal)) throw new Skip("dead turn unlocks switching in NORMAL for this position");
		AIV2.Plan pn = AIV2.plan(root, normal);
		boolean plainSwitch = false;
		for (Action a : pn.A) if (a.kind == ActionKind.SWITCH) plainSwitch = true;
		check("NORMAL never plain-switches", !plainSwitch);
	}

	private static void t19() {
		Scen sc = sackScenario(1.0, true, 100, true);
		if (sc == null) throw new Skip("no scanned foe/move KOs the level-20 twins");
		SimState root = sc.d.root();
		MonWeights w = MonWeights.compute(root);
		ActionGen.BenchPlan plan = ActionGen.benchPlan(root, AIConfig.hard(), w);
		double activeV = ActionGen.valueOf(w.ai, root.ai.bench(), 0);
		double scrubV = ActionGen.valueOf(w.ai, root.ai.bench(), 1);
		check("twin teammate is worth >= 0.8x the active (" + scrubV + " vs " + activeV + ")", scrubV >= AIConfig.hard().sackRatio * activeV);
		check("so the twin is NOT a sack candidate", !plan.sacks.contains(1));
		check("and nothing is sack-only for the guard to judge", plan.sackOnly().isEmpty());
		// Note: with a 2-mon bench the twin can still appear as an ordinary ANSWER row; whether the AI then stays in depends on
		// speed order (a slower AI loses the same either way), so the spec's "sack probability ~ 0" is asserted at the
		// candidate level here, not on the mixed strategy.
	}

	private static void t20() {
		Scen sc = sackScenario(0.15, false, 5, false);
		if (sc == null) throw new Skip("every scanned foe/move threatens the ace even at level 5");
		SimState root = sc.d.root();
		AIV2.Plan p = AIV2.plan(root, AIConfig.hard());
		int row = switchRow(p.A, 1);
		check("no threat: the sack row is gone or has ~0 probability (row " + row + (row >= 0 ? ", x=" + p.x[row] : "") + ")", row < 0 || p.x[row] < 0.1);
		if (p.sackOnly.contains(1)) check("no threat: a sack-only row is always dropped", row < 0);
		// Stay carries the probability mass
		double stayMass = 0;
		for (int i = 0; i < p.A.size(); i++) if (p.A.get(i).kind == ActionKind.MOVE) stayMass += p.x[i];
		check("Stay rows hold most of the probability (" + stayMass + ")", stayMass > 0.5);
	}

	private static void t21() {
		Scen sc = sackScenario(0.15, false, 100, true);
		if (sc == null) throw new Skip("no scanned foe/move KOs both a level-20 ace and its 15%-HP teammate");
		SimState root = sc.d.root();
		AIConfig hard = AIConfig.hard();
		MonWeights w = MonWeights.compute(root);
		if (!ActionGen.benchPlan(root, hard, w).sacks.contains(1)) throw new Skip("teammate is not a sack candidate");
		AIV2.Plan p = AIV2.plan(root, hard);
		int row = switchRow(p.A, 1);
		if (row < 0) throw new Skip("no sack row survived the guard (see T18)");

		boolean[] threat = SackAnalysis.threatColumns(root, p.P);
		double minAttackGain = Double.POSITIVE_INFINITY, maxSwitchGain = Double.NEGATIVE_INFINITY;
		for (int j = 0; j < p.P.size(); j++) {
			double stay = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < p.A.size(); i++) if (p.A.get(i).kind == ActionKind.MOVE && p.A.get(i).move != null) stay = Math.max(stay, p.M[i][j]);
			double gain = p.M[row][j] - stay;
			if (threat[j]) minAttackGain = Math.min(minAttackGain, gain);
			else if (p.P.get(j).kind == ActionKind.SWITCH) maxSwitchGain = Math.max(maxSwitchGain, gain);
		}
		if (minAttackGain == Double.POSITIVE_INFINITY || maxSwitchGain == Double.NEGATIVE_INFINITY) throw new Skip("need both an attack column and a switch column");
		check("sack's edge over Stay is larger against the expected attack (" + minAttackGain + ") than against a switch (" + maxSwitchGain + ")",
				minAttackGain > maxSwitchGain);
		// Phase 6: once predict() exists, assert the sack row's PROBABILITY falls sharply when y_hat expects the switch.
	}

	private static void sackingOff() {
		Scen sc = sackScenario(0.15, false, 100, true);
		if (sc == null) throw new Skip("no scanned foe/move KOs both a level-20 ace and its 15%-HP teammate");
		SimState root = sc.d.root();
		MonWeights w = MonWeights.compute(root);
		AIConfig off = AIConfig.hard();
		off.enableSacking = false;
		check("enableSacking=false -> no sack candidates", ActionGen.sackCandidates(root, off, w).isEmpty());
		AIV2.Plan p = AIV2.plan(root, off);
		check("and no guard ran", p.guard == null && p.sackOnly.isEmpty());
	}

	// ------------------------------------------------------------------ manual acceptance

	/**
	 * Phase 5 acceptance: HARD with sacking (engine A) vs HARD without sack candidates (engine B) over random competitive
	 * 6v6 teams, seats swapped. Needs the live game (Pokemon.gp) on the game thread; start with 50-100 battles and read
	 * A's win rate and the think time per decision. A win rate above ~50% with a sensible margin is the acceptance signal.
	 */
	public static SelfPlayReport selfPlayComparison(int battles) {
		SelfPlay.Config c = new SelfPlay.Config();
		c.battles = battles;
		c.aiA = AIV2.INSTANCE;
		c.aiB = AIV2.NO_SACK;
		c.difficultyA = Player.HARD;
		c.difficultyB = Player.HARD;
		c.teams = SelfPlay.randomCompetitive(6);
		return SelfPlay.run(c);
	}
}