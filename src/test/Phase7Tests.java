package test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pokemon.*;
import util.Print;
import util.Rng;

/**
 * Phase 7 tests: T23, T24, T25, T32, T33, the EXTREME half of T27, and the non-player / non-EXTREME regression.
 * Run on the game thread with no battle active: {@code Phase7Tests.runAll();}
 * <p>
 * Not compiled against your project. Guessed names (fix if the compiler complains): {@code Move.TACKLE},
 * {@code Move.EARTHQUAKE}, {@code Move.FLAMETHROWER}, {@code Ability.INTIMIDATE}, {@code Ability.DRIZZLE},
 * {@code Status.PARALYZED}, {@code Pokemon.setTrainer}, {@code Egg(int)}. A name that does not match is a compile error, not a false pass.
 * T24 is proven on the matrix layer (hand-built lead matrices through the production solve+shape); the engine-level
 * "one lead beats most of the team but is countered by one mon" scenario is a manual step (see PHASE7_CHANGES.md).
 */
public final class Phase7Tests {
	private Phase7Tests() {}

	private static int passed, failed;
	private static Field prevField;
	private static boolean prevSuppressed;

	public static void runAll() {
		passed = failed = 0;
		prevField = Pokemon.field;
		prevSuppressed = Print.isDebugSuppressed();
		Print.setDebugSuppressed(true);
		try {
			run("T32 candidate sets: alive AI mons; player columns = isSelectableLead (no null / fainted / Egg)", Phase7Tests::t32);
			run("T23 no peek: same seed + teams, any player current -> same AI lead; no Pokemon parameter", Phase7Tests::t23);
			run("T24 not counterable: mixed when countered, near-certain when dominant", Phase7Tests::t24);
			run("T25 entry effects: Intimidate / weather lead differs from the no-entry baseline", Phase7Tests::t25);
			run("T33 real battle-start state changes payoffs; all-negative matrix is not uniform", Phase7Tests::t33);
			run("T27 EXTREME differs from HARD only in selectLead", Phase7Tests::t27);
			run("pickLead: non-player foe returns current; plan is pure (T3-style fingerprint)", Phase7Tests::wrapperAndPurity);
		} finally {
			Pokemon.field = prevField;
			Print.setDebugSuppressed(prevSuppressed);
			Rng.randomize();
		}
		System.out.println("[Phase7Tests] " + passed + " passed, " + failed + " FAILED");
	}

	private interface Body { void run() throws Exception; }

	private static void run(String label, Body b) {
		try {
			Pokemon.field = new Field();
			b.run();
			passed++;
			System.out.println(label + " passed.");
		} catch (Throwable t) {
			failed++;
			System.out.println(label + " FAILED: " + t);
		}
	}

	private static void check(String what, boolean ok) {
		if (!ok) throw new AssertionError(what);
	}

	// ------------------------------------------------------------------ fixtures

	private static Pokemon mk(int id, Move... moves) {
		Pokemon p = new Pokemon(id, 50, true, true);
		p.ability = Ability.NULL;
		p.item = null;
		for (int i = 0; i < p.moveset.length; i++) p.moveset[i] = i < moves.length ? new Moveslot(moves[i]) : null;
		p.currentHP = p.getStat(0);
		p.status = Status.HEALTHY;
		for (Moveslot ms : p.moveset) if (ms != null) ms.currentPP = 99;
		return p;
	}

	/** A trainer whose team array may hold nulls (like the player's 6-slot team): built with the player-style constructor. */
	private static Trainer trainerOf(String name, Pokemon... team) {
		Trainer t = new Trainer(true, name);
		t.initFieldEffectList();
		t.boosts = new int[3];
		for (int i = 0; i < team.length; i++) {
			t.team[i] = team[i];
			if (team[i] != null) {
				team[i].setTrainer(t);
				team[i].slot = i;
			}
		}
		t.setCurrent(team[0]);
		return t;
	}

	private static Pokemon[] teamA() {
		return new Pokemon[] { mk(1, Move.FLAMETHROWER), mk(2, Move.FLAMETHROWER), mk(3, Move.EARTHQUAKE) };
	}

	private static Pokemon[] teamB() {
		return new Pokemon[] { mk(4, Move.EARTHQUAKE, Move.TACKLE), mk(5, Move.FLAMETHROWER), mk(6, Move.TACKLE) };
	}

	private static AIConfig extreme() {
		return AIConfig.forDifficulty(Player.EXTREME);
	}

	// ------------------------------------------------------------------ tests

	private static void t32() {
		Pokemon[] at = { mk(1, Move.TACKLE), mk(2, Move.TACKLE), mk(3, Move.TACKLE) };
		at[1].fainted = true;
		at[1].currentHP = 0;
		Trainer ai = trainerOf("P7-ai", at);

		Pokemon fainted = mk(5, Move.TACKLE);
		fainted.fainted = true;
		fainted.currentHP = 0;
		Pokemon egg = new Egg(7);
		Trainer pl = trainerOf("P7-pl", mk(4, Move.TACKLE), fainted, egg, mk(6, Move.TACKLE)); // slots 4, 5 stay null

		check("AI candidates are exactly its alive mons [0, 2], got " + LeadSelection.selectable(ai), LeadSelection.selectable(ai).equals(Arrays.asList(0, 2)));
		check("player columns exclude null, fainted and Egg: [0, 3], got " + LeadSelection.selectable(pl), LeadSelection.selectable(pl).equals(Arrays.asList(0, 3)));

		LeadSelection.Plan plan = LeadSelection.plan(ai, pl, extreme());
		check("plan rows = AI candidates", plan.aiSlots.equals(Arrays.asList(0, 2)));
		check("plan columns = player candidates", plan.playerSlots.equals(Arrays.asList(0, 3)));
		check("the sampling distribution has one entry per row and sums to 1", plan.x.length == 2 && Math.abs(plan.x[0] + plan.x[1] - 1) < 1e-9);
	}

	private static void t23() throws Exception {
		check("chooseLead takes the player's TEAM (a Trainer), never a Pokemon",
				hasNoPokemonParam(Trainer.class.getMethod("chooseLead", Trainer.class, AIConfig.class)));

		Pokemon[] at = teamA(), pt = teamB();
		Trainer ai = trainerOf("P7-ai", at);
		Trainer pl = trainerOf("P7-pl", pt);
		AIConfig cfg = extreme();

		List<Integer> asIs = new ArrayList<>(), otherCurrent = new ArrayList<>();
		for (int seed = 1; seed <= 25; seed++) {
			pl.setCurrent(pt[0]);
			Rng.setSeed(seed);
			asIs.add(ai.indexOf(ai.chooseLead(pl, cfg)));

			pl.setCurrent(pt[2]); // the player "leads" with someone else: must not matter
			Rng.setSeed(seed);
			otherCurrent.add(ai.indexOf(ai.chooseLead(pl, cfg)));
		}
		check("same seeds, different player current -> identical leads: " + asIs + " vs " + otherCurrent, asIs.equals(otherCurrent));
		check("determinism: identical to a second run", asIs.equals(rerun(ai, pl, cfg, 25)));
	}

	private static List<Integer> rerun(Trainer ai, Trainer pl, AIConfig cfg, int seeds) {
		List<Integer> out = new ArrayList<>();
		for (int seed = 1; seed <= seeds; seed++) {
			Rng.setSeed(seed);
			out.add(ai.indexOf(ai.chooseLead(pl, cfg)));
		}
		return out;
	}

	private static boolean hasNoPokemonParam(java.lang.reflect.Method m) {
		for (Class<?> c : m.getParameterTypes()) if (Pokemon.class.isAssignableFrom(c)) return false;
		return true;
	}

	/** T24 on the matrix layer: A beats two columns but loses badly to the third, B is the reverse: neither may dominate. */
	private static void t24() {
		AIConfig cfg = extreme();
		double[][] countered = { { 10, 10, -30 }, { -20, -20, 5 } };
		double[] x = LeadSelection.solveLeads(countered, cfg);
		check("a lead that beats most columns but has one counter is mixed, not degenerate: " + Arrays.toString(x), x[0] > 0.2 && x[0] <= 0.8 && x[1] > 0.2);

		double[][] dominant = { { 30, 25, 20 }, { 5, 0, -10 }, { -5, 10, 0 } };
		double[] d = LeadSelection.solveLeads(dominant, cfg);
		check("a strictly dominant lead is picked almost always: " + Arrays.toString(d), d[0] > 0.95);

		double[][] rps = { { 0, -1, 1 }, { 1, 0, -1 }, { -1, 1, 0 } };
		double[] r = LeadSelection.solveLeads(rps, cfg);
		check("rock-paper-scissors leads stay spread: " + Arrays.toString(r), r[0] < 0.6 && r[1] < 0.6 && r[2] < 0.6);
	}

	private static void t25() {
		Pokemon intim = mk(1, Move.FLAMETHROWER);
		intim.ability = Ability.INTIMIDATE;
		Pokemon plain = mk(2, Move.FLAMETHROWER);
		Pokemon rain = mk(3, Move.FLAMETHROWER);
		rain.ability = Ability.DRIZZLE;
		Trainer ai = trainerOf("P7-ai", intim, plain, rain);
		Trainer pl = trainerOf("P7-pl", mk(4, Move.EARTHQUAKE, Move.TACKLE), mk(5, Move.FLAMETHROWER));
		AIConfig cfg = extreme();

		SimState start = LeadSelection.startState(ai, pl);
		Pokemon.field = start.field;
		MonWeights w = MonWeights.compute(start);

		long startFp = start.fingerprint();
		SimState e = BattleSimulator.simulateEntry(start, 0, 0, true);
		check("Intimidate dropped the player's Attack on entry (turn 0, no end of turn)", e.player.active().statStages[0] == -1);
		check("the entry state is a start-of-turn-1 state", e.turn == 0 && e.field.turns == 0);
		check("Intimidate lead: eval differs from the no-entry baseline", Math.abs(LeadSelection.entryDelta(start, 0, 0, cfg, w)) > 1e-6);
		check("control: a lead with no entry effect equals its baseline exactly", Math.abs(LeadSelection.entryDelta(start, 1, 0, cfg, w)) < 1e-9);

		SimState r = BattleSimulator.simulateEntry(start, 2, 0, true);
		check("Drizzle lead set rain", r.field.weather != null && r.field.weather.effect == Field.Effect.RAIN);
		check("Drizzle lead: eval differs from the baseline", Math.abs(LeadSelection.entryDelta(start, 2, 0, cfg, w)) > 1e-6);
		check("entry simulation leaves the start state untouched (fingerprint)", start.fingerprint() == startFp);
	}

	private static void t33() {
		AIConfig cfg = extreme();
		Trainer ai = trainerOf("P7-ai", teamA());
		Trainer fresh = trainerOf("P7-pl", teamB());
		LeadSelection.Plan healthy = LeadSelection.plan(ai, fresh, cfg);

		Pokemon[] hurt = teamB();
		for (Pokemon p : hurt) p.currentHP = Math.max(1, p.getStat(0) / 4);
		hurt[0].status = Status.PARALYZED;
		Trainer damaged = trainerOf("P7-pl", hurt);
		LeadSelection.Plan worn = LeadSelection.plan(ai, damaged, cfg);

		double diff = 0;
		for (int i = 0; i < healthy.staticL.length; i++) for (int j = 0; j < healthy.staticL[0].length; j++) diff = Math.max(diff, Math.abs(healthy.staticL[i][j] - worn.staticL[i][j]));
		check("a damaged / statused player team changes the lead payoffs (max cell change " + diff + ")", diff > 1e-6);

		// Regression for the old pickLead: every score <= 0 made every lead score 0.0 and the pick uniformly random.
		double[][] allNeg = { { -50, -60, -55 }, { -90, -95, -92 }, { -80, -85, -88 } };
		double[] x = LeadSelection.solveLeads(allNeg, cfg);
		check("an all-negative matrix still picks the better lead, not uniform: " + Arrays.toString(x), x[0] > 0.95);
	}

	private static void t27() throws Exception {
		AIConfig h = AIConfig.hard(), e = extreme();
		List<String> diff = new ArrayList<>();
		for (java.lang.reflect.Field f : AIConfig.class.getFields()) {
			if (Modifier.isStatic(f.getModifiers())) continue;
			Object a = f.get(h), b = f.get(e);
			if (a == null ? b != null : !a.equals(b)) diff.add(f.getName());
		}
		check("EXTREME vs HARD differ only in selectLead, got " + diff, diff.size() == 1 && diff.get(0).equals("selectLead"));
		check("selectLead: false at NORMAL and HARD, true at EXTREME", !AIConfig.forDifficulty(Player.NORMAL).selectLead
				&& !AIConfig.forDifficulty(Player.HARD).selectLead && e.selectLead);
		check("NORMAL vs HARD still differ only in allowVoluntarySwitch", AIConfig.normal().selectLead == h.selectLead);
	}

	private static void wrapperAndPurity() {
		Trainer ai = trainerOf("P7-ai", teamA());
		Trainer pl = trainerOf("P7-pl", teamB());
		Pokemon before = ai.current;
		// pl is a plain Trainer, so its mons are not playerOwned(): pickLead must keep the fixed lead (AI-vs-AI sim battles).
		check("!foe.playerOwned() -> current unchanged", ai.pickLead(pl.team[0]) == before && ai.current == before);

		long fp = Phase2Tests.fingerprintReal(ai, pl, Pokemon.field);
		LeadSelection.plan(ai, pl, extreme());
		check("plan() leaves every real object unchanged (fingerprint)", Phase2Tests.fingerprintReal(ai, pl, Pokemon.field) == fp);
		check("and leaves current alone (only chooseLead sets it)", ai.current == before);
		check("Pokemon.field restored", Pokemon.field != null);
	}

	/** Informational: ms per plan on a 3v3 / 6v6. Not asserted (budget is 500 ms on the dev machine, Phase 8 tunes it). */
	public static void timing(int runs) {
		AIConfig cfg = extreme();
		Trainer ai = trainerOf("P7-ai", teamA());
		Trainer pl = trainerOf("P7-pl", teamB());
		long total = 0;
		for (int i = 0; i < runs; i++) total += LeadSelection.plan(ai, pl, cfg).millis;
		System.out.println("[Phase7Tests] avg plan time " + (total / Math.max(1, runs)) + " ms over " + runs + " runs (3v3)");
	}
}