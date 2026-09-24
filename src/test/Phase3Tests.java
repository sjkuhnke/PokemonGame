package test;

import java.util.List;

import pokemon.Move;
import pokemon.*;
import util.Rng;

/**
 * §11/§12 Phase 3 acceptance: T4, T6, T7, T8, T12, T13, T16, T17, T28, T29, T30, T31.
 * <p>
 * Run in-game (Trainer.trainers must be populated), same as Phase2Tests - e.g. from a debug key:
 * <pre>
 *     Phase3Tests.runAll();
 * </pre>
 * T7 is pure matrix math, no game state needed. Everything else is built from {@link
 * AITestHarness}, which reuses {@link SelfPlay#buildPool()} (the same "skip scripted encounters"
 * trainer pool SelfPlay itself uses) rather than hand-authoring Pokemon, since this roster's
 * moves/items/species are heavily custom and I don't have your exact enum names to hand-author
 * against. Each scenario builder scans the pool at runtime for a mon/matchup with the needed
 * *property* (a Poison-type damaging move, an item where isChoiceItem() is true, a KO'ing
 * priority move, ...) instead of naming a specific mon or move. Two of them (T6, T8) are
 * genuinely roster-dependent and will throw a clear "couldn't find X in the pool" exception if
 * your current roster doesn't happen to contain a matching combination - if that happens, tell
 * me what's available (or the specific mon/move/item names you want used) and I'll hand-target
 * them instead of scanning.
 */
public final class Phase3Tests {
	private Phase3Tests() {}

	public static void runAll() {
		run("T7", Phase3Tests::t7_solverSanity);
		run("T4", Phase3Tests::t4_determinism);
		run("T6", Phase3Tests::t6_poisonFighting);
		run("T8", Phase3Tests::t8_twoLethalMovesRedundantPriority);
		run("T12", Phase3Tests::t12_perishCounter1);
		run("T13", Phase3Tests::t13_noMoveDoesAnything);
		run("T16", Phase3Tests::t16_difficultyGate);
		run("T17", Phase3Tests::t17_perishExceptionInNormal);
		run("T28", Phase3Tests::t28_deadTurnUnlocksSwitching);
		run("T29", Phase3Tests::t29_struggleOnlySwap);
		run("T30", Phase3Tests::t30_normalKeepsFighting);
		run("T31", Phase3Tests::t31_choiceLockIntoStatus);
	}

	private static void run(String label, Runnable test) {
		try {
			test.run();
			System.out.println(label + " passed.");
		} catch (Throwable t) {
			System.out.println(label + " FAILED: " + t);
		}
	}

	// ---------------------------------------------------------------------
	// T7: Solver sanity (§7.10). Fully standalone - no Pokemon/Trainer/Field needed.
	// ---------------------------------------------------------------------

	static void t7_solverSanity() {
		double[][] pennies = { { 1, -1 }, { -1, 1 } };
		Solver.Result rp = Solver.solveZeroSum(pennies, 2000);
		assertClose("pennies x[0]", rp.x[0], 0.5, 0.05);
		assertClose("pennies x[1]", rp.x[1], 0.5, 0.05);
		assertClose("pennies y[0]", rp.y[0], 0.5, 0.05);
		assertClose("pennies y[1]", rp.y[1], 0.5, 0.05);

		double[][] rps = { { 0, -1, 1 }, { 1, 0, -1 }, { -1, 1, 0 } };
		Solver.Result rr = Solver.solveZeroSum(rps, 3000);
		for (int i = 0; i < 3; i++) {
			assertClose("rps x[" + i + "]", rr.x[i], 1.0 / 3, 0.05);
			assertClose("rps y[" + i + "]", rr.y[i], 1.0 / 3, 0.05);
		}

		double[][] dominated = { { 1, 2 }, { -5, -4 } };
		Solver.Result rd = Solver.solveZeroSum(dominated, 2000);
		assertTrue("dominated row weight ~0", rd.x[1] < 0.05);

		double[][] dominant = { { 3, 3 }, { 1, 2 } };
		Solver.Result rdo = Solver.solveZeroSum(dominant, 2000);
		assertTrue("dominant row weight ~1", rdo.x[0] > 0.95);
	}

	// ---------------------------------------------------------------------
	// T4: Determinism.
	// ---------------------------------------------------------------------

	static void t4_determinism() {
		AITestHarness.Scenario s1 = AITestHarness.basic1v1();
		Rng.setSeed(12345L);
		Move d1 = s1.ai.bestMove2(s1.foe, true, Player.HARD).move;

		AITestHarness.Scenario s2 = AITestHarness.basic1v1(); // same trainers, freshly reset
		Rng.setSeed(12345L);
		Move d2 = s2.ai.bestMove2(s2.foe, true, Player.HARD).move;

		assertTrue("same seed -> same chosen move (" + d1 + " vs " + d2 + ")", d1 == d2);
	}

	// ---------------------------------------------------------------------
	// T6: Poison/Fighting scenario (§1) - genuinely roster-dependent, see class doc.
	// ---------------------------------------------------------------------

	static void t6_poisonFighting() {
		AITestHarness.Scenario s = AITestHarness.poisonFightingVsGhostBackSteel();
		AIConfig cfg = AIConfig.hard(); // the hedge is a property of the matrix/solver itself, not of NORMAL's extra switch-gating
		SimState root = SimState.snapshot(s.ai, s.foe);
		MonWeights weights = MonWeights.compute(root);
		List<Action> A = ActionGen.genAIActions(root, cfg, weights);
		List<Action> P = ActionGen.genPlayerActions(root, cfg);
		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, weights);
		Solver.Result eq = Solver.solveZeroSum(M);

		double poison = weightOfMove(A, eq.x, s.taggedMoveA);
		double fighting = weightOfMove(A, eq.x, s.taggedMoveB);
		System.out.println("raw equilibrium: " + s.taggedMoveA + "=" + poison + "  " + s.taggedMoveB + "=" + fighting);
		assertTrue("both moves get nonzero RAW equilibrium weight (poison=" + poison + ", fighting=" + fighting + ") "
				+ "- note this checks Solver output before Shaper.shape's minProb cutoff, since a real "
				+ "but small hedge is expected to get pruned for actual sampled play (see chat)",
				poison > 0 && fighting > 0);
	}

	/** Sums the equilibrium weight of every AI action that's a plain use of move m (ignores its MOVE_THEN_SWITCH variants, if any). */
	private static double weightOfMove(List<Action> A, double[] x, Move m) {
		double sum = 0;
		for (int i = 0; i < A.size(); i++) {
			if (A.get(i).kind == ActionKind.MOVE && A.get(i).move == m) sum += x[i];
		}
		return sum;
	}

	// ---------------------------------------------------------------------
	// T8: two lethal moves, one priority one not, AI already faster - priority buys nothing.
	// ---------------------------------------------------------------------

	static void t8_twoLethalMovesRedundantPriority() {
		AITestHarness.Scenario s = AITestHarness.twoLethalMovesDifferentPriorityAiFaster();
		int plain = 0, n = 50;
		Rng.randomize();
		for (int i = 0; i < n; i++) {
			Move chosen = s.ai.bestMove2(s.foe, true, Player.HARD).move;
			System.out.println(chosen);
			if (!chosen.hasPriority(s.ai)) plain++; // taggedMoveA = the non-priority lethal move
		}
		assertTrue("non-priority move dominates when priority is redundant (already faster)", plain > n * 0.9);
	}

	// ---------------------------------------------------------------------
	// T12: Perish counter 1 - switch dominates at every difficulty.
	// ---------------------------------------------------------------------

	static void t12_perishCounter1() {
		for (int diff : new int[] { Player.NORMAL, Player.HARD }) {
			int switches = 0, n = 200;
			Rng.randomize();
			for (int i = 0; i < n; i++) {
				AITestHarness.Scenario s = AITestHarness.perishCountNWithHealthyBench(1);
				MoveDecision d = s.ai.bestMove2(s.foe, true, diff);
				if (AITestHarness.isSwitchOrPivot(d)) switches++;
			}
			assertTrue("perishCount==1 switches dominate at difficulty " + diff, switches > n * 0.9);
		}
	}

	// ---------------------------------------------------------------------
	// T13: No move does anything to active foe (all immune).
	// ---------------------------------------------------------------------

	static void t13_noMoveDoesAnything() {
		int switches = 0, n = 300;
		Rng.randomize();
		for (int i = 0; i < n; i++) {
			AITestHarness.Scenario s = AITestHarness.allMovesImmuneVsActive();
			MoveDecision d = s.ai.bestMove2(s.foe, true, Player.HARD);
			if (AITestHarness.isSwitchOrPivot(d)) switches++;
		}
		assertTrue("HARD: switching dominates when nothing lands", switches > n * 0.7);
	}

	// ---------------------------------------------------------------------
	// T16: Difficulty gate.
	// ---------------------------------------------------------------------

	static void t16_difficultyGate() {
		int plainSwitches = 0, n = 500;
		Rng.randomize();
		for (int i = 0; i < n; i++) {
			AITestHarness.Scenario s = AITestHarness.basic1v1NotDeadTurn();
			MoveDecision d = s.ai.bestMove2(s.foe, true, Player.NORMAL);
			if (AITestHarness.isPlainSwitch(d)) plainSwitches++;
		}
		assertTrue("NORMAL never picks a plain SWITCH row outside perishCount==1/dead-turn", plainSwitches == 0);

		boolean hardSwitchedSometimes = false;
		for (int i = 0; i < 50 && !hardSwitchedSometimes; i++) {
			AITestHarness.Scenario s = AITestHarness.favorableSwitchScenario();
			MoveDecision d = s.ai.bestMove2(s.foe, true, Player.HARD);
			if (AITestHarness.isPlainSwitch(d)) hardSwitchedSometimes = true;
		}
		assertTrue("HARD selects switches when the matrix favors them", hardSwitchedSometimes);
	}

	// ---------------------------------------------------------------------
	// T17: Perish exception in NORMAL.
	// ---------------------------------------------------------------------

	static void t17_perishExceptionInNormal() {
		AITestHarness.Scenario perish1 = AITestHarness.perishCountNWithHealthyBench(1);
		MoveDecision d1 = perish1.ai.bestMove2(perish1.foe, true, Player.NORMAL);
		assertTrue("perishCount==1: NORMAL switches", AITestHarness.isSwitchOrPivot(d1));

		AITestHarness.Scenario perish2 = AITestHarness.perishCountNWithHealthyBench(2);
		MoveDecision d2 = perish2.ai.bestMove2(perish2.foe, true, Player.NORMAL);
		assertTrue("perishCount==2: NORMAL does not force-switch", !AITestHarness.isPlainSwitch(d2));
	}

	// ---------------------------------------------------------------------
	// T28: Dead turn unlocks switching in NORMAL.
	// ---------------------------------------------------------------------

	static void t28_deadTurnUnlocksSwitching() {
		AITestHarness.Scenario deadTurn = AITestHarness.trueDeadTurn();
		SimState root1 = SimState.snapshot(deadTurn.ai, deadTurn.foe);
		assertTrue("dead turn: NORMAL's action set includes SWITCH",
				ActionGen.genAIActions(root1, AIConfig.normal(), MonWeights.compute(root1)).stream()
						.anyMatch(a -> a.kind == ActionKind.SWITCH));

		AITestHarness.Scenario notDeadTurn = AITestHarness.basic1v1NotDeadTurn();
		SimState root2 = SimState.snapshot(notDeadTurn.ai, notDeadTurn.foe);
		assertTrue("not a dead turn: no SWITCH rows in NORMAL's action set",
				ActionGen.genAIActions(root2, AIConfig.normal(), MonWeights.compute(root2)).stream()
						.noneMatch(a -> a.kind == ActionKind.SWITCH));
	}

	// ---------------------------------------------------------------------
	// T29: Struggle-only swap.
	// ---------------------------------------------------------------------

	static void t29_struggleOnlySwap() {
		for (int diff : new int[] { Player.NORMAL, Player.HARD }) {
			AITestHarness.Scenario s = AITestHarness.struggleOnlyWithHealthyBench();
			MoveDecision d = s.ai.bestMove2(s.foe, true, diff);
			assertTrue("Struggle-only + canSwitch: swaps at difficulty " + diff, AITestHarness.isPlainSwitch(d));
			assertTrue("no Struggle row exists", d.move != Move.STRUGGLE);
		}
		AITestHarness.Scenario trapped = AITestHarness.struggleOnlyTrapped();
		MoveDecision dt = trapped.ai.bestMove2(trapped.foe, true, Player.HARD);
		assertTrue("trapped: Struggle", dt.move == Move.STRUGGLE);
	}

	// ---------------------------------------------------------------------
	// T30: NORMAL keeps fighting in a bad matchup.
	// ---------------------------------------------------------------------

	static void t30_normalKeepsFighting() {
		AITestHarness.Scenario s = AITestHarness.threatenedButHasWorkingMove();
		MoveDecision d = s.ai.bestMove2(s.foe, true, Player.NORMAL);
		assertTrue("NORMAL stays and fights (no SWITCH rows) with a working move", !AITestHarness.isPlainSwitch(d));
	}

	// ---------------------------------------------------------------------
	// T31: Choice-lock into a status move.
	// ---------------------------------------------------------------------

	static void t31_choiceLockIntoStatus() {
		AITestHarness.Scenario s = AITestHarness.unlockedChoiceHolderWithStatusAndAttackOptions();
		SimState root = SimState.snapshot(s.ai, s.foe);
		boolean dead = ActionGen.deadTurn(root, AIConfig.normal());
		assertTrue("deadTurn doesn't count clicking the status move as a real action "
				+ "when a real attacking option exists", !dead);
	}

	// ---------------------------------------------------------------------

	private static void assertClose(String label, double actual, double expected, double tol) {
		if (Math.abs(actual - expected) > tol) {
			throw new AssertionError(label + ": expected ~" + expected + ", got " + actual);
		}
	}

	private static void assertTrue(String label, boolean cond) {
		if (!cond) throw new AssertionError(label + " failed");
	}

	// =======================================================================
	// AITestHarness: builds scenarios from the real Trainer.trainers pool.
	// =======================================================================

	static final class AITestHarness {
		private AITestHarness() {}

		static final class Scenario {
			final Pokemon ai;
			final Pokemon foe;
			/** Scenario-specific "move under test" tags; meaning documented per builder method. Null unless that builder sets them. */
			Move taggedMoveA, taggedMoveB;

			Scenario(Pokemon ai, Pokemon foe) {
				this.ai = ai;
				this.foe = foe;
			}
		}

		// ---- pool access (reuses SelfPlay.buildPool: same "skip scripted encounters" rule) ----

		private static List<Trainer> pool() {
			List<Trainer> p = SelfPlay.buildPool();
			if (p.size() < 2) throw new IllegalStateException("Need >= 2 usable trainers in Trainer.trainers (SelfPlay.buildPool()) to build a scenario");
			return p;
		}

		private static int aliveCount(Trainer t) {
			int c = 0;
			for (Pokemon m : t.team) if (m != null && !m.isFainted()) c++;
			return c;
		}

		/** Heals, clears status/perish and restocks PP on every alive mon of a trainer. */
		private static void reset(Trainer t) {
			for (Pokemon m : t.team) {
				if (m == null) continue;
				m.fainted = false;
				m.currentHP = m.getStat(0);
				m.status = Status.HEALTHY;
				m.perishCount = 0;
				if (m.moveset != null) for (Moveslot ms : m.moveset) if (ms != null) ms.currentPP = 99;
			}
		}

		private static void zeroPP(Pokemon m) {
			if (m.moveset != null) for (Moveslot ms : m.moveset) if (ms != null) ms.currentPP = 0;
		}

		private static Scenario finish(Trainer at, Pokemon aiMon, Trainer ft, Pokemon foeMon) {
			at.setCurrent(aiMon);
			ft.setCurrent(foeMon);
			return new Scenario(aiMon, foeMon);
		}

		private static boolean hasDamagingMove(Pokemon attacker, Pokemon defender) {
			try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
				for (Move m : attacker.getValidMoveset()) {
					DamageRange r = attacker.calcRange(defender, m, true, Pokemon.field);
					if (r.usable && !r.immune && r.dealsDamage()) return true;
				}
			}
			return false;
		}

		private static double bestKoProb(Pokemon attacker, Pokemon defender) {
			double best = 0;
			try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
				for (Move m : attacker.getValidMoveset()) {
					DamageRange r = attacker.calcRange(defender, m, true, Pokemon.field);
					if (!r.usable || r.immune) continue;
					best = Math.max(best, r.koProb(defender.currentHP));
				}
			}
			return best;
		}

		private static Move findMoveOfType(Pokemon mon, PType type) {
			for (Move m : mon.getValidMoveset()) {
				if (m.mtype == type && m.cat != 2) return m;
			}
			return null;
		}

		// ---- generic (safe) scenarios ----

		static Scenario basic1v1() {
			List<Trainer> p = pool();
			Trainer t1 = p.get(0), t2 = p.get(1);
			reset(t1);
			reset(t2);
			return finish(t1, t1.current, t2, t2.current);
		}

		static Scenario basic1v1NotDeadTurn() {
			for (Trainer at : pool()) {
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							if (hasDamagingMove(aiMon, foeMon)) return finish(at, aiMon, ft, foeMon);
						}
					}
				}
			}
			throw new IllegalStateException("couldn't find a not-dead-turn pair in the trainer pool");
		}

		static Scenario allMovesImmuneVsActive() {
			for (Trainer at : pool()) {
				if (aliveCount(at) < 2) continue;
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							if (!hasDamagingMove(aiMon, foeMon)) return finish(at, aiMon, ft, foeMon);
						}
					}
				}
			}
			throw new IllegalStateException("couldn't find an all-immune pair in the trainer pool - "
					+ "tell me a specific mon/matchup and I'll hand-target it instead of scanning");
		}

		/**
		 * Like allMovesImmuneVsActive, but scans using ActionGen.deadTurn itself as the acceptance
		 * criterion instead of the "no move deals direct damage" proxy above. The two aren't the
		 * same thing: a mon with a genuinely useless-to-attack-with kit can still have a status
		 * move (a stat boost, a hazard) that deadTurn correctly recognizes as a real action, in
		 * which case allMovesImmuneVsActive's scenario wouldn't actually be a dead turn under the
		 * code being tested. T28 needs the real predicate; T13 doesn't (HARD bypasses deadTurn
		 * entirely via allowVoluntarySwitch), so it keeps using the cheaper proxy above.
		 */
		static Scenario trueDeadTurn() {
			AIConfig cfg = AIConfig.normal();
			for (Trainer at : pool()) {
				if (aliveCount(at) < 2) continue;
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							Scenario s = finish(at, aiMon, ft, foeMon);
							SimState root = SimState.snapshot(s.ai, s.foe);
							if (ActionGen.deadTurn(root, cfg)) return s;
						}
					}
				}
			}
			throw new IllegalStateException("no true dead-turn position (ActionGen.deadTurn==true) found in the pool - "
					+ "tell me a specific mon/matchup and I'll hand-target it instead of scanning");
		}

		static Scenario perishCountNWithHealthyBench(int n) {
			for (Trainer at : pool()) {
				if (aliveCount(at) < 2) continue;
				Trainer ft = pool().get(0) == at ? pool().get(1) : pool().get(0);
				reset(at);
				reset(ft);
				at.current.perishCount = n;
				return finish(at, at.current, ft, ft.current);
			}
			throw new IllegalStateException("no trainer with a healthy bench (>=2 alive) found in the pool");
		}

		static Scenario struggleOnlyWithHealthyBench() {
			for (Trainer at : pool()) {
				if (aliveCount(at) < 2) continue;
				Trainer ft = pool().get(0) == at ? pool().get(1) : pool().get(0);
				reset(at);
				reset(ft);
				zeroPP(at.current);
				return finish(at, at.current, ft, ft.current);
			}
			throw new IllegalStateException("no trainer with a healthy bench (>=2 alive) found in the pool");
		}

		static Scenario struggleOnlyTrapped() {
			Scenario s = struggleOnlyWithHealthyBench();
			for (Pokemon m : s.ai.trainer.team) {
				if (m != s.ai) {
					m.fainted = true;
					m.currentHP = 0;
				}
			}
			return s;
		}

		static Scenario favorableSwitchScenario() {
			for (Trainer at : pool()) {
				if (aliveCount(at) < 2) continue;
				for (Trainer ft : pool()) {
					if (ft == at) continue;
					for (Pokemon foeMon : ft.team) {
						if (foeMon == null) continue;
						reset(at);
						reset(ft);
						Pokemon active = at.current;
						double activeThreat = bestKoProb(foeMon, active);
						if (activeThreat < 0.5) continue;
						for (Pokemon bench : at.team) {
							if (bench == null || bench == active || bench.isFainted()) continue;
							if (bestKoProb(foeMon, bench) < activeThreat - 0.3) {
								return finish(at, active, ft, foeMon);
							}
						}
					}
				}
			}
			throw new IllegalStateException("no favorable-switch position found in the pool - "
					+ "tell me a specific mon/matchup and I'll hand-target it instead of scanning");
		}

		static Scenario threatenedButHasWorkingMove() {
			for (Trainer at : pool()) {
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							if (bestKoProb(foeMon, aiMon) >= 0.5 && hasDamagingMove(aiMon, foeMon)) {
								return finish(at, aiMon, ft, foeMon);
							}
						}
					}
				}
			}
			throw new IllegalStateException("no threatened-but-has-a-move position found in the pool");
		}

		static Scenario unlockedChoiceHolderWithStatusAndAttackOptions() {
			Item choiceItem = null;
			for (Item it : Item.values()) if (it.isChoiceItem()) { choiceItem = it; break; }
			if (choiceItem == null) throw new IllegalStateException("no Item constant with isChoiceItem()==true found");
			for (Trainer at : pool()) {
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					boolean hasStatus = false;
					for (Move m : aiMon.getValidMoveset()) if (m.cat == 2) { hasStatus = true; break; }
					if (!hasStatus) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							if (hasDamagingMove(aiMon, foeMon)) {
								aiMon.item = choiceItem;
								return finish(at, aiMon, ft, foeMon);
							}
						}
					}
				}
			}
			throw new IllegalStateException("no mon with both a status move and a working damaging move found in the pool");
		}

		// ---- roster-dependent scenarios (may legitimately not exist in a given roster) ----

		/** taggedMoveA = a Poison-type damaging move, taggedMoveB = a Fighting-type damaging move. */
		static Scenario poisonFightingVsGhostBackSteel() {
			for (Trainer at : pool()) {
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					reset(at);
					Move poisonMv = findMoveOfType(aiMon, PType.POISON);
					Move fightMv = findMoveOfType(aiMon, PType.FIGHTING);
					if (poisonMv == null || fightMv == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						for (Pokemon ghost : ft.team) {
							if (ghost == null || !ghost.isType(PType.GHOST)) continue;
							for (Pokemon steel : ft.team) {
								if (steel == null || steel == ghost || !steel.isType(PType.STEEL)) continue;
								reset(ft);
								aiMon.moveset[2] = null;
								aiMon.shiftMoveset();
								Scenario s = finish(at, aiMon, ft, ghost);
								s.taggedMoveA = poisonMv;
								s.taggedMoveB = fightMv;
								return s;
							}
						}
					}
				}
			}
			throw new IllegalStateException("no Poison+Fighting-movepool mon vs a Ghost-active/Steel-backed "
					+ "trainer found in the pool (T6, §1) - tell me the mon/team you want used and I'll target it directly");
		}

		/** taggedMoveA = the non-priority lethal move (expected winner), taggedMoveB = the priority lethal move. */
		static Scenario twoLethalMovesDifferentPriorityAiFaster() {
			for (Trainer at : pool()) {
				for (Pokemon aiMon : at.team) {
					if (aiMon == null) continue;
					for (Trainer ft : pool()) {
						if (ft == at) continue;
						if (ft.team.length <= 1) continue;
						for (Pokemon foeMon : ft.team) {
							if (foeMon == null) continue;
							reset(at);
							reset(ft);
							foeMon.currentHP = 1;
							if (aiMon.getFaster(foeMon, 0, 0, Pokemon.field) != aiMon) continue;
							Move prio = null, plain = null;
							try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
								for (Move m : aiMon.getValidMoveset()) {
									DamageRange r = aiMon.calcRange(foeMon, m, true, Pokemon.field);
									if (!r.usable || r.immune || r.koProb(1) < 0.99) continue;
									int pr = m.getPriority(aiMon, Pokemon.field);
									if (pr > 0 && prio == null) prio = m;
									if (pr == 0 && plain == null) plain = m;
								}
							}
							if (prio != null && plain != null) {
								Scenario s = finish(at, aiMon, ft, foeMon);
								s.taggedMoveA = plain;
								s.taggedMoveB = prio;
								return s;
							}
						}
					}
				}
			}
			throw new IllegalStateException("no mon with both a priority and a non-priority lethal move vs a "
					+ "1 HP slower foe found in the pool (T8) - tell me the mon/moves you want used and I'll target it directly");
		}

		// ---- decision-shape helpers ----

		static boolean isPlainSwitch(MoveDecision d) {
			return d.move == Move.GROWL && d.statusApplications.stream().anyMatch(pr -> pr.getFirst() == Status.SWAP);
		}

		static boolean isPivot(MoveDecision d) {
			return d.statusApplications.stream().anyMatch(pr -> pr.getFirst() == Status.TEMP_SWITCHING);
		}

		static boolean isSwitchOrPivot(MoveDecision d) {
			return isPlainSwitch(d) || isPivot(d);
		}
	}
}