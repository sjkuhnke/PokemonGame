package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pokemon.*;
import pokemon.Field.Effect;
import test.Phase4Tests;

/**
 * Phase 8 addendum: delayed effects the one-turn simulation cannot see (Yawn, sleep, Future Sight).
 * <p>
 * <b>Run order.</b> {@link #diagnose()} first, with the Evaluator changes in place: it switches the new terms OFF
 * ({@code W_SLEEP = 0}, {@code FUTURE_SIGHT_HOLD = 0}), which reproduces the AI as it was, and prints the Yawn / Future
 * Sight rows next to a "do nothing" row. Then {@link #runAll()} with the terms on. {@link #dump()} prints the full
 * mixed strategy for a few scenarios so the click rate can be read by eye.
 * <p>
 * Fixtures come from {@code Phase4Tests} ({@code mk}, {@code duel}), which are package-private statics.
 */
public final class Phase8DelayedEffectTests {
	private Phase8DelayedEffectTests() {}

	private static int passed, failed;

	private interface Body { void run() throws Exception; }

	public static void runAll() {
		passed = failed = 0;
		Field prevField = Pokemon.field;
		try {
			run("Yawn: DROWSY after turn 1, ASLEEP after turn 2 (simulator)", Phase8DelayedEffectTests::yawnTwoTurns);
			run("canFallAsleep (sleep() on a clone): each gate, real mon untouched", Phase8DelayedEffectTests::sleepGates);
			run("eval prices DROWSY (and sleep immunity gates it)", Phase8DelayedEffectTests::evalDrowsy);
			run("eval prices an ASLEEP active mon", Phase8DelayedEffectTests::evalAsleep);
			run("Future Sight: queued after the move, priced by pendingDelayedDamage", Phase8DelayedEffectTests::futureSightPending);
			run("Future Sight into an immune target is worth nothing", Phase8DelayedEffectTests::futureSightImmune);
			run("matrix: Yawn row beats a do-nothing row", Phase8DelayedEffectTests::yawnRowBeatsPass);
			run("matrix: Future Sight row beats a do-nothing row", Phase8DelayedEffectTests::futureSightRowBeatsPass);
			run("matrix: Yawn into a sleep-immune foe is not rewarded", Phase8DelayedEffectTests::yawnIntoImmune);
			run("DROWSY does not follow a mon out on a switch, and lands if it stays", Phase8DelayedEffectTests::drowsyDoesNotFollowSwitch);
		} finally {
			Pokemon.field = prevField;
		}
		System.out.println("[Phase8 delayed effects] passed " + passed + ", failed " + failed);
		if (failed > 0) throw new AssertionError(failed + " delayed-effect test(s) failed");
	}

	// ------------------------------------------------------------------ harness

	private static void run(String name, Body b) {
		try {
			b.run();
			passed++;
			System.out.println("  PASS  " + name);
		} catch (Throwable t) {
			failed++;
			System.out.println("  FAIL  " + name + "  ->  " + t);
		}
	}

	private static void check(String what, boolean ok) {
		if (!ok) throw new AssertionError(what);
	}

	private static void close(String what, double actual, double expected, double tol) {
		if (Math.abs(actual - expected) > tol) throw new AssertionError(what + ": expected ~" + expected + ", got " + actual);
	}

	private static Branch top(List<Branch> bs) {
		Branch best = bs.get(0);
		for (Branch b : bs) if (b.prob > best.prob) best = b;
		return best;
	}

	private static Field.FieldEffect effect(Effect e) {
		return Pokemon.field.new FieldEffect(e);
	}

	/** AI mon (species 1) with the given moves against a foe (species 4) with the given moves. */
	private static Phase4Tests.Duel duel(Move[] aiMoves, Move... foeMoves) {
		return Phase4Tests.duel(Phase4Tests.mk(1, aiMoves), Phase4Tests.mk(4, foeMoves));
	}

	private static List<Branch> sim(SimState root, Action a, Action p) {
		return BattleSimulator.simulateTurn(root, a, p, AIConfig.hard());
	}

	private static double eval(SimState s, MonWeights w) {
		return Evaluator.eval(s, AIConfig.hard().style, w);
	}

	/** Rows [first, PASS] against the player's generated columns; returns {M, columns}. */
	private static Object[] twoRowMatrix(SimState root, Move first, MonWeights w) {
		AIConfig cfg = AIConfig.hard();
		List<Action> A = new ArrayList<>();
		A.add(new Action(first));
		A.add(Action.PASS);
		List<Action> P = ActionGen.genPlayerActions(root, cfg, w);
		return new Object[] { MatrixBuilder.buildMatrix(root, A, P, cfg, w), P };
	}

	@SuppressWarnings("unchecked")
	private static void requireBeatsPass(String label, Object[] mp, double margin) {
		double[][] M = (double[][]) mp[0];
		List<Action> P = (List<Action>) mp[1];
		int moveCols = 0;
		for (int j = 0; j < P.size(); j++) {
			if (P.get(j).kind != ActionKind.MOVE) continue;
			moveCols++;
			check(label + " vs column " + describe(P.get(j)) + ": row " + M[0][j] + " should beat pass " + M[1][j] + " by > " + margin,
					M[0][j] > M[1][j] + margin);
		}
		check("at least one foe move column", moveCols > 0);
	}

	private static String describe(Action a) {
		if (a == Action.PASS) return "PASS";
		return a.kind + (a.move != null ? " " + a.move : "") + (a.kind == ActionKind.SWITCH || a.kind == ActionKind.MOVE_THEN_SWITCH ? " slot " + a.slot : "");
	}

	// ------------------------------------------------------------------ simulator behavior (should already pass)

	private static void yawnTwoTurns() {
		Phase4Tests.Duel d = duel(new Move[] { Move.YAWN, Move.TACKLE }, Move.TACKLE);
		SimState root = d.root();
		Branch b1 = top(sim(root, new Action(Move.YAWN), new Action(Move.TACKLE)));
		Pokemon foe1 = b1.state.player.active();
		check("after turn 1 the foe is DROWSY (YAWNING became DROWSY at end of turn)", foe1.hasStatus(Status.DROWSY));
		check("not asleep yet", foe1.status != Status.ASLEEP);
		check("real foe untouched", !d.f.hasStatus(Status.DROWSY) && !d.f.hasStatus(Status.YAWNING));

		Branch b2 = top(sim(b1.state, Action.PASS, Action.PASS));
		Pokemon foe2 = b2.state.player.active();
		check("after turn 2 the foe is ASLEEP", foe2.status == Status.ASLEEP);
		check("DROWSY consumed", !foe2.hasStatus(Status.DROWSY));
	}

	/** Evaluator.canFallAsleep runs the real sleep() on a clone: right answer per gate, and the real mon is never touched. */
	private static void sleepGates() {
		String[] names = { "healthy", "burned", "insomnia", "psychic", "safeguard", "electric terrain", "lum berry" };
		boolean[] expected = { true, false, false, false, false, false, false };
		for (int v = 0; v < names.length; v++) {
			Phase4Tests.Duel d = duel(new Move[] { Move.TACKLE }, Move.TACKLE);
			Pokemon foe = d.f;
			switch (v) {
			case 1: foe.status = Status.BURNED; break;
			case 2: foe.ability = Ability.INSOMNIA; break;
			case 3: foe.type1 = PType.PSYCHIC; foe.type2 = null; break;
			case 4: d.pl.getFieldEffectList().add(effect(Effect.SAFEGUARD)); break;
			case 5:
				// setTerrain announces itself through Task.addTask, which throws outside a battle state unless a sim scope is open.
				final Field tf = d.field;
				SimContext.run(SimPolicy.DEFAULT, () -> tf.setTerrain(tf.new FieldEffect(Effect.ELECTRIC)));
				check("setup: Electric Terrain is the field's terrain", d.field.equals(d.field.terrain, Effect.ELECTRIC));
				break;
			case 6: foe.item = Item.LUM_BERRY; break;
			default: break;
			}
			Field before = Pokemon.field;
			boolean got = Evaluator.canFallAsleep(foe, d.field);
			check(names[v] + ": expected " + expected[v] + ", got " + got, got == expected[v]);
			check(names[v] + ": the real mon is untouched", foe.status == (v == 1 ? Status.BURNED : Status.HEALTHY) && foe.sleepCounter == 0);
			check(names[v] + ": the held item is untouched", foe.item == (v == 6 ? Item.LUM_BERRY : null));
			check(names[v] + ": Pokemon.field restored", Pokemon.field == before);
		}
	}

	// ------------------------------------------------------------------ eval terms

	private static void evalDrowsy() {
		Phase4Tests.Duel d = duel(new Move[] { Move.TACKLE }, Move.TACKLE);
		SimState base = d.root();
		MonWeights w = MonWeights.compute(base);
		double v0 = eval(base, w);
		double full = Evaluator.SLEEP_TURN_VALUE * Evaluator.SLEEP_MEAN_TURNS * Evaluator.DROWSY_LAND * Evaluator.SLEEP_HOLD;

		SimState foeDrowsy = d.root();
		foeDrowsy.player.active().addStatus(Status.DROWSY);
		close("DROWSY foe at full HP is worth the drowsy sleep value to the AI", eval(foeDrowsy, w) - v0, full, 0.5);

		SimState aiDrowsy = d.root();
		aiDrowsy.ai.active().addStatus(Status.DROWSY);
		close("DROWSY on the AI's own mon costs the same", eval(aiDrowsy, w) - v0, -full, 0.5);

		SimState half = d.root();
		half.player.active().addStatus(Status.DROWSY);
		half.player.active().currentHP = half.player.active().getStat(0) / 2;
		SimState halfPlain = d.root();
		halfPlain.player.active().currentHP = halfPlain.player.active().getStat(0) / 2;
		check("a wounded foe's sleep is worth less than a healthy foe's",
				eval(half, w) - eval(halfPlain, w) < full - 1);

		SimState immune = d.root();
		immune.player.active().addStatus(Status.DROWSY);
		immune.player.active().ability = Ability.INSOMNIA;
		SimState immunePlain = d.root();
		immunePlain.player.active().ability = Ability.INSOMNIA;
		close("Insomnia foe: DROWSY is worth nothing", eval(immune, w) - eval(immunePlain, w), 0, 0.01);

		SimState yawning = d.root();
		yawning.player.active().addStatus(Status.YAWNING);
		close("YAWNING counts like DROWSY", eval(yawning, w) - v0, full, 0.5);
	}

	/** The gap behind everything else: calcRange ignores a sleeping attacker, so only this term can value a landed sleep. */
	private static void evalAsleep() {
		Phase4Tests.Duel d = duel(new Move[] { Move.TACKLE }, Move.TACKLE);
		SimState base = d.root();
		MonWeights w = MonWeights.compute(base);
		double v0 = eval(base, w);
		double expected = Evaluator.SLEEP_TURN_VALUE * Evaluator.SLEEP_MEAN_TURNS * Evaluator.SLEEP_HOLD;

		SimState asleep = d.root();
		asleep.player.active().status = Status.ASLEEP;
		asleep.player.active().sleepCounter = 2;
		close("ASLEEP foe", eval(asleep, w) - v0, expected, 0.5);

		SimState waking = d.root();
		waking.player.active().status = Status.ASLEEP;
		waking.player.active().sleepCounter = 0;
		close("ASLEEP with no turns left is worth nothing", eval(waking, w) - v0, 0, 0.5);

		SimState talker = d.root();
		talker.player.active().status = Status.ASLEEP;
		talker.player.active().sleepCounter = 2;
		talker.player.active().moveset[1] = new Moveslot(Move.SLEEP_TALK);
		talker.player.active().moveset[1].currentPP = 99;
		close("Sleep Talk user: half", eval(talker, w) - v0, expected * Evaluator.SLEEP_TALK_FACTOR, 0.5);
	}

	private static void futureSightPending() {
		Phase4Tests.Duel d = duel(new Move[] { Move.FUTURE_SIGHT, Move.TACKLE }, Move.TACKLE);
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		close("nothing pending at the root", Evaluator.pendingDelayedDamage(root.player, root.ai, w.player, root.field), 0, 1e-9);

		Branch fs = top(sim(root, new Action(Move.FUTURE_SIGHT), Action.PASS));
		boolean queued = false;
		for (Field.FieldEffect fe : fs.state.player.shell.getFieldEffectList()) if (fe.effect == Effect.FUTURE_SIGHT) queued = true;
		check("a FUTURE_SIGHT effect is queued on the foe's side", queued);

		double pending = Evaluator.pendingDelayedDamage(fs.state.player, fs.state.ai, w.player, fs.state.field);
		check("pending damage is positive (got " + pending + ")", pending > 0);
		check("pending damage against the AI's own side is 0", Evaluator.pendingDelayedDamage(fs.state.ai, fs.state.player, w.ai, fs.state.field) == 0);

		Branch pass = top(sim(root, Action.PASS, Action.PASS));
		double gain = eval(fs.state, w) - eval(pass.state, w);
		check("eval(Future Sight) beats eval(pass) by about the pending value (gain " + gain + ", pending " + pending + ")",
				gain > pending * 0.5);
		check("the real foe's side is untouched", d.pl.getFieldEffectList().isEmpty());
	}

	private static void futureSightImmune() {
		Phase4Tests.Duel d = duel(new Move[] { Move.FUTURE_SIGHT, Move.TACKLE }, Move.TACKLE);
		d.f.type1 = PType.DARK;
		d.f.type2 = null;
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		Branch fs = top(sim(root, new Action(Move.FUTURE_SIGHT), Action.PASS));
		close("Dark target: the hit that will land is 0", Evaluator.pendingDelayedDamage(fs.state.player, fs.state.ai, w.player, fs.state.field), 0, 1e-9);
	}

	// ------------------------------------------------------------------ matrix rows (the AI's actual choice)

	private static void yawnRowBeatsPass() {
		Phase4Tests.Duel d = duel(new Move[] { Move.YAWN, Move.TACKLE }, Move.TACKLE);
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		requireBeatsPass("Yawn", twoRowMatrix(root, Move.YAWN, w), 5.0);
	}

	private static void futureSightRowBeatsPass() {
		Phase4Tests.Duel d = duel(new Move[] { Move.FUTURE_SIGHT, Move.TACKLE }, Move.TACKLE);
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		requireBeatsPass("Future Sight", twoRowMatrix(root, Move.FUTURE_SIGHT, w), 5.0);
	}

	@SuppressWarnings("unchecked")
	private static void yawnIntoImmune() {
		Phase4Tests.Duel d = duel(new Move[] { Move.YAWN, Move.TACKLE }, Move.TACKLE);
		d.f.ability = Ability.INSOMNIA;
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		Object[] mp = twoRowMatrix(root, Move.YAWN, w);
		double[][] M = (double[][]) mp[0];
		List<Action> P = (List<Action>) mp[1];
		for (int j = 0; j < P.size(); j++) {
			if (P.get(j).kind != ActionKind.MOVE) continue;
			check("Yawn vs Insomnia, column " + describe(P.get(j)) + ": " + M[0][j] + " vs pass " + M[1][j],
					M[0][j] - M[1][j] < 1.0);
		}
	}

	/** A mon that was already DROWSY loses it by switching out (the reset in the switch-out code), and falls asleep if it stays. */
	private static void drowsyDoesNotFollowSwitch() {
		Phase4Tests.Duel d = duel(new Move[] { Move.TACKLE }, Move.TACKLE);
		SimState root = d.root();
		root.player.active().addStatus(Status.DROWSY);
		Branch out = top(sim(root, Action.PASS, new Action(1)));
		check("the mon that switched in is not DROWSY", !out.state.player.active().hasStatus(Status.DROWSY));
		Branch stay = top(sim(root, Action.PASS, Action.PASS));
		check("staying in lets the sleep land", stay.state.player.active().status == Status.ASLEEP);
	}

	// ------------------------------------------------------------------ diagnosis and dumps

	/**
	 * The AI as it was: both new terms off. Prints Yawn / Future Sight against a do-nothing row for each foe move
	 * column. Expect differences of about 0, which is the bug. Always restores the tunables.
	 */
	@SuppressWarnings("unchecked")
	public static void diagnose() {
		double w0 = Evaluator.W_SLEEP, f0 = Evaluator.FUTURE_SIGHT_HOLD;
		Field prev = Pokemon.field;
		try {
			Evaluator.W_SLEEP = 0;
			Evaluator.FUTURE_SIGHT_HOLD = 0;
			for (Move m : new Move[] { Move.YAWN, Move.FUTURE_SIGHT }) {
				Phase4Tests.Duel d = duel(new Move[] { m, Move.TACKLE }, Move.TACKLE);
				SimState root = d.root();
				MonWeights w = MonWeights.compute(root);
				Object[] mp = twoRowMatrix(root, m, w);
				double[][] M = (double[][]) mp[0];
				List<Action> P = (List<Action>) mp[1];
				System.out.println("[diagnose] terms OFF: " + m + " row minus pass row");
				for (int j = 0; j < P.size(); j++) {
					System.out.println(String.format(Locale.ROOT, "    vs %-22s %s %8.2f   pass %8.2f   diff %6.2f",
							describe(P.get(j)), m, M[0][j], M[1][j], M[0][j] - M[1][j]));
				}
			}
		} finally {
			Evaluator.W_SLEEP = w0;
			Evaluator.FUTURE_SIGHT_HOLD = f0;
			Pokemon.field = prev;
		}
	}

	/**
	 * Full decision (rows from ActionGen, solve, shape) for several scenarios; read the click rate by eye. Each scenario is
	 * printed twice: with all rows, and with only the AI's MOVE rows. The second answers "among its moves, does it click
	 * Yawn / Future Sight": a bench mon that is simply a better answer takes 100% of the first and says nothing about that.
	 * The Earthquake scenarios are the sanity check that the new values are not too generous: a strong attack should win.
	 */
	public static void dump() {
		Field prev = Pokemon.field;
		try {
			Object[][] scenarios = {
				{ "Yawn + Tackle vs Tackle", new Move[] { Move.YAWN, Move.TACKLE } },
				{ "Future Sight + Tackle vs Tackle", new Move[] { Move.FUTURE_SIGHT, Move.TACKLE } },
				{ "Yawn + Future Sight + Tackle vs Tackle", new Move[] { Move.YAWN, Move.FUTURE_SIGHT, Move.TACKLE } },
				{ "Yawn + Earthquake vs Tackle (strong attack should win)", new Move[] { Move.YAWN, Move.EARTHQUAKE } },
				{ "Future Sight + Earthquake vs Tackle (strong attack should win)", new Move[] { Move.FUTURE_SIGHT, Move.EARTHQUAKE } },
			};
			for (Object[] sc : scenarios) {
				for (boolean movesOnly : new boolean[] { false, true }) {
					dumpDecision((String) sc[0] + (movesOnly ? "  [AI move rows only]" : "  [all rows]"),
							duel((Move[]) sc[1], Move.TACKLE), movesOnly);
				}
			}
		} finally {
			Pokemon.field = prev;
		}
	}

	private static void dumpDecision(String title, Phase4Tests.Duel d, boolean movesOnly) {
		AIConfig cfg = AIConfig.hard();
		SimState root = d.root();
		MonWeights w = MonWeights.compute(root);
		List<Action> A = ActionGen.genAIActions(root, cfg, w);
		if (movesOnly) A.removeIf(a -> a.kind != ActionKind.MOVE);
		List<Action> P = ActionGen.genPlayerActions(root, cfg, w);
		double[][] M = MatrixBuilder.buildMatrix(root, A, P, cfg, w);
		Solver.Result r = Solver.solveZeroSum(M);
		double[] x = Shaper.shape(r.x, cfg);
		System.out.println("[dump] " + title);
		for (int i = 0; i < A.size(); i++) {
			StringBuilder sb = new StringBuilder(String.format(Locale.ROOT, "  %-26s p=%5.1f%%  |", describe(A.get(i)), x[i] * 100));
			for (int j = 0; j < P.size(); j++) sb.append(String.format(Locale.ROOT, " %7.1f", M[i][j]));
			System.out.println(sb);
		}
		StringBuilder cols = new StringBuilder("  columns:");
		for (Action p : P) cols.append("  ").append(describe(p));
		System.out.println(cols);
		System.out.println("  (production also runs AIV2.dominatedRows before solving; rows here are unreduced)");
	}
}