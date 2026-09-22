package pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import overworld.GamePanel;
import util.Print;
import util.Rng;

/**
 * Phase 1 tests: T1 (calcRange parity), simulation determinism / isolation, and the calc primitives.
 * <p>
 * Run inside the running game, on the game thread, with no battle active: {@code Phase1Tests.runAll();}
 * The standalone checks live in {@code SimContextTest} and {@code util.RngTest} ({@code java ...}, no game needed).
 * <p>
 * NOTE: written against the APIs visible in the uploaded sources (not compiled against your full project).
 * If a fixture call does not match your code, the test reports an ERROR line instead of a false PASS.
 */
public final class Phase1Tests {
	private Phase1Tests() {}

	private static int passed, failed, errors;

	// ------------------------------------------------------------------ plumbing (same shape as Phase0Tests)

	private static GamePanel gp;
	private static int prevState;
	private static Field prevField;
	private static boolean prevCreateTask, prevSuppressed;

	private static void enter() {
		gp = Pokemon.gp;
		if (gp == null) throw new IllegalStateException("Pokemon.gp is null: run inside the game");
		prevState = gp.gameState;
		prevField = Pokemon.field;
		prevCreateTask = Pokemon.createTask;
		prevSuppressed = Print.isDebugSuppressed();
		gp.gameState = GamePanel.TASK_STATE;
		Pokemon.createTask = true;
		Pokemon.field = new Field();
		Print.setDebugSuppressed(true);
		gp.ui.tasks.clear();
	}

	private static void exit() {
		gp.ui.tasks.clear();
		Pokemon.field = prevField;
		Pokemon.createTask = prevCreateTask;
		gp.gameState = prevState;
		Print.setDebugSuppressed(prevSuppressed);
		Rng.randomize();
	}

	private static void check(String name, boolean ok, String detail) {
		if (ok) passed++; else failed++;
		System.out.println((ok ? "PASS  " : "FAIL  ") + name + (detail.isEmpty() ? "" : "  [" + detail + "]"));
	}

	private static void error(String name, Throwable t) {
		errors++;
		System.out.println("ERROR " + name + "  [" + t + "]");
		t.printStackTrace(System.out);
	}

	private interface Body { void run() throws Exception; }

	private static void test(String name, Body b) {
		try {
			Pokemon.field = new Field();
			Pokemon.createTask = true;
			if (SimContext.active()) throw new IllegalStateException("a SimContext scope leaked from an earlier test");
			b.run();
		} catch (Throwable t) {
			error(name, t);
		} finally {
			gp.ui.tasks.clear();
		}
	}

	// ------------------------------------------------------------------ fixtures

	private static Pokemon mon(int id, Ability ab, Item item) {
		Pokemon p = new Pokemon(id, 50, true, true);
		p.ability = ab;
		p.item = item;
		p.currentHP = p.getStat(0);
		return p;
	}

	private static void bind(Pokemon a, Pokemon b) {
		Trainer ta = new Trainer("T-" + a.name, new Pokemon[] { a }, 0);
		Trainer tb = new Trainer("T-" + b.name, new Pokemon[] { b }, 0);
		ta.boosts = new int[3];
		tb.boosts = new int[3];
		Pokemon.field.clear(ta, tb);
	}

	/** A plain, single-hit, accurate attack: the kind T1 can compare without secondary-effect noise. */
	private static boolean plainAttack(Move m) {
		return m != null && m.isAttack() && m.basePower > 0 && m.secondary == 0 && m.accuracy >= 100 && !m.isPivotMove();
	}

	private static String fp(Pokemon p) {
		return p.currentHP + "|" + p.status + "|" + p.fainted + "|" + Arrays.toString(p.statStages) + "|" + p.lastMoveUsed;
	}

	// ------------------------------------------------------------------ entry

	/** @return true if every test passed and none errored */
	public static boolean runAll() {
		passed = failed = errors = 0;
		enter();
		try {
			testParity();
			testRangeMatchesLegacyEstimates();
			testRangeIsPureAndFlagged();
			testCritModel();
			testMultiHit();
			testSimDeterministicAndIsolated();
			testSimPolicyForcesOutcomes();
			testTaskHelpersAreInertInSim();
		} finally {
			exit();
		}
		System.out.println(String.format(Locale.ROOT, "Phase 1 tests: %d passed, %d failed, %d errors", passed, failed, errors));
		return failed == 0 && errors == 0;
	}

	// ------------------------------------------------------------------ T1

	private static void testParity() {
		test("T1", () -> {
			List<String> mismatches = parityScan(8, 300, 0.02);
			check("T1 calcRange vs move(): every plain (attacker, defender, move) within 2% of max HP", mismatches.isEmpty(), mismatches.size() + " mismatches, see above");
		});
	}

	/**
	 * T1: for random mon pairs and their plain attacking moves, compare {@code calcRange(...).expectedCapped(fullHP)} with the mean HP
	 * lost from real {@code move()} calls on fullClones (real random rolls and crits, no SimContext), and with the legacy
	 * {@code calcWithTypes} mode-0 mean, so the numbers before and after Phase 1 can be read side by side.
	 * Prints every triple whose calcRange gap exceeds {@code tolerance} (fraction of the defender's max HP).
	 * Call {@code Phase1Tests.parity(8, 300, 0.02)} for a standalone run.
	 */
	public static List<String> parity(int monCount, int samples, double tolerance) {
		enter();
		try {
			return parityScan(monCount, samples, tolerance);
		} finally {
			exit();
		}
	}

	private static List<String> parityScan(int monCount, int samples, double tolerance) {
		List<String> mismatches = new ArrayList<>();
		Rng.setSeed(1);
		List<Pokemon> mons = new ArrayList<>();
		for (int i = 0; i < monCount; i++) mons.add(mon(Rng.nextInt(Pokemon.MAX_POKEMON) + 1, Ability.NULL, null));
		int compared = 0;
		for (Pokemon a : mons) {
			for (Pokemon d : mons) {
				if (a == d) continue;
				bind(a, d);
				for (Moveslot slot : a.moveset) {
					if (slot == null || !plainAttack(slot.move)) continue;
					Move m = slot.move;
					DamageRange r = a.calcRange(d, m, true, Pokemon.field);
					if (!r.dealsDamage()) continue;
					int hp = d.getStat(0);
					double rangeMean = r.expectedCapped(hp);
					double legacySum = 0, simSum = 0;
					for (int i = 0; i < samples; i++) {
						legacySum += Math.max(0, a.calcWithTypes(d, m, true, 0, false, Pokemon.field, false).getFirst());
						Pokemon ac = a.fullClone(), dc = d.fullClone();
						dc.currentHP = hp;
						ac.moveInit(dc, m, true);
						simSum += hp - Math.max(0, dc.currentHP);
						gp.ui.tasks.clear();
					}
					compared++;
					double gap = Math.abs(rangeMean - simSum / samples) / Math.max(1, hp);
					if (gap > tolerance) {
						mismatches.add(String.format(Locale.ROOT, "%s -> %s with %s: calcRange %.1f, move() %.1f, legacy calcWithTypes %.1f (range gap %.1f%% of max HP)",
								a.name, d.name, m, rangeMean, simSum / samples, legacySum / samples, gap * 100));
					}
				}
			}
		}
		System.out.println("T1 parity: compared " + compared + " (attacker, defender, move) triples, " + mismatches.size() + " over " + tolerance * 100 + "% of max HP");
		for (String s : mismatches) System.out.println("  MISMATCH " + s);
		return mismatches;
	}

	// ------------------------------------------------------------------ calcRange vs the legacy estimates

	private static void testRangeMatchesLegacyEstimates() {
		test("range-vs-legacy", () -> {
			Rng.setSeed(2);
			int compared = 0, off = 0;
			String first = "";
			for (int i = 0; i < 12; i++) {
				Pokemon a = mon(Rng.nextInt(Pokemon.MAX_POKEMON) + 1, Ability.NULL, null);
				Pokemon d = mon(Rng.nextInt(Pokemon.MAX_POKEMON) + 1, Ability.NULL, null);
				if (a == d) continue;
				bind(a, d);
				for (Moveslot slot : a.moveset) {
					if (slot == null || !plainAttack(slot.move)) continue;
					DamageRange r = a.calcRange(d, slot.move, true, Pokemon.field);
					if (!r.dealsDamage() || r.critProb >= 1.0) continue;
					int lo = a.calcWithTypes(d, slot.move, true, -1, false, Pokemon.field, false).getFirst();
					int hi = a.calcWithTypes(d, slot.move, true, 1, false, Pokemon.field, false).getFirst();
					compared++;
					if (Math.abs(r.min - lo) > 0 || Math.abs(r.max - hi) > 0) {
						off++;
						if (first.isEmpty()) first = a.name + " -> " + d.name + " " + slot.move + ": range " + r.min + ".." + r.max + " vs legacy " + lo + ".." + hi;
					}
				}
			}
			check("calcRange min/max equal the legacy mode -1 / +1 estimates (same computeDamage)", compared > 0 && off == 0, compared + " compared, " + off + " differ " + first);
		});
	}

	// ------------------------------------------------------------------ purity and flags

	private static void testRangeIsPureAndFlagged() {
		test("range-pure", () -> {
			Pokemon a = mon(1, Ability.NULL, null), d = mon(4, Ability.NULL, null);
			bind(a, d);
			Rng.setSeed(5);
			double control = Rng.next();
			Rng.setSeed(5);
			String before = fp(a) + fp(d);
			for (Moveslot slot : a.moveset) if (slot != null) a.calcRange(d, slot.move, true, Pokemon.field);
			check("calcRange draws nothing from the RNG and changes no state", Rng.next() == control && (fp(a) + fp(d)).equals(before), "");
		});
		test("range-flags", () -> {
			Pokemon a = mon(1, Ability.NULL, null);
			// a status move: usable, no damage, but accuracy matters
			Move status = null;
			for (Move m : Move.getAllMoves()) if (m != null && m.cat == 2 && m.accuracy > 0 && m.accuracy < 100 && m.basePower <= 0) { status = m; break; }
			Pokemon d = mon(4, Ability.NULL, null);
			bind(a, d);
			if (status != null) {
				DamageRange r = a.calcRange(d, status, true, Pokemon.field);
				check("status move: usable, deals no damage, keeps its accuracy", r.usable && !r.dealsDamage() && r.accuracy > 0 && r.accuracy <= 1.0, status + " " + r);
			}
			// Fake Out when the user did not just come in: fails
			a.impressive = false;
			DamageRange fo = a.calcRange(d, Move.FAKE_OUT, true, Pokemon.field);
			check("Fake Out without the first-turn flag: unusable (was the -1 sentinel)", !fo.usable && fo.koProb(1) == 0, fo.toString());
			// Torment: repeating the last move is unusable
			a.addStatus(Status.TORMENTED, 0);
			a.lastMoveUsed = Move.HEADBUTT;
			DamageRange tm = a.calcRange(d, Move.HEADBUTT, true, Pokemon.field);
			check("Tormented repeat: unusable", !tm.usable, tm.toString());
			// Magic Reflect: flagged, never a KO on the target
			Pokemon a2 = mon(1, Ability.NULL, null), d2 = mon(4, Ability.NULL, null);
			bind(a2, d2);
			d2.addStatus(Status.MAGIC_REFLECT, 0);
			DamageRange rf = a2.calcRange(d2, Move.HEADBUTT, true, Pokemon.field);
			check("Magic Reflect: reflected flag set, damage figure positive, koProb 0", rf.reflected && rf.avg >= 0 && rf.koProb(1) == 0, rf.toString());
			// an immunity: search a defender that is immune to a plain Ground move via type (Flying) or any attack
			Pokemon atk = mon(1, Ability.NULL, null);
			boolean foundImmune = false;
			for (int id = 1; id <= Math.min(Pokemon.MAX_POKEMON, 120) && !foundImmune; id++) {
				Pokemon t = mon(id, Ability.NULL, null);
				bind(atk, t);
				DamageRange x = atk.calcRange(t, Move.EARTHQUAKE, true, Pokemon.field);
				int legacy = atk.calcWithTypes(t, Move.EARTHQUAKE, true, 1, false, Pokemon.field, false).getFirst();
				if (x.immune) {
					foundImmune = true;
					check("immunity: immune flag matches the legacy 0 sentinel, koProb 0", legacy == 0 && x.koProb(1) == 0 && !x.dealsDamage(), t.name + " " + x);
				}
			}
			if (!foundImmune) check("immunity (skipped: no Earthquake-immune target in ids 1..120)", true, "");
		});
	}

	// ------------------------------------------------------------------ crits

	private static void testCritModel() {
		test("crit-table", () -> {
			check("critProbability matches what critCheck rolls: 6% / 14% / 51% / sure, never for stage < 0",
					Pokemon.critProbability(-1, false) == 0 && Pokemon.critProbability(0, false) == 0.06 && Pokemon.critProbability(1, false) == 0.14
							&& Pokemon.critProbability(2, false) == 0.51 && Pokemon.critProbability(3, false) == 1.0 && Pokemon.critProbability(4, false) == 1.0, "");
			check("critProbability under a scripted battle: only sure crits", Pokemon.critProbability(2, true) == 0 && Pokemon.critProbability(3, true) == 1.0, "");
		});
		test("crit-stage-shared", () -> {
			Move m = null;
			Pokemon probeDef = mon(4, Ability.NULL, null);
			for (Move c : Move.getAllMoves()) {
				if (plainAttack(c) && c.critChance == 0) { m = c; break; }
			}
			if (m == null) { check("crit stage (skipped: no plain stage-0 attack found)", true, ""); return; }
			double[] p = new double[4];
			Ability[] abs = { Ability.NULL, Ability.SUPER_LUCK, Ability.NULL, Ability.SUPER_LUCK };
			Item[] its = { null, null, Item.SCOPE_LENS, Item.SCOPE_LENS };
			for (int i = 0; i < 4; i++) {
				Pokemon a = mon(1, abs[i], its[i]);
				Pokemon d = mon(4, Ability.NULL, null);
				bind(a, d);
				p[i] = a.calcRange(d, m, true, Pokemon.field).critProb;
			}
			check("move() and calcRange share one crit stage: base 6%, Super Luck or Scope Lens 14%, both 51%",
					near(p[0], 0.06) && near(p[1], 0.14) && near(p[2], 0.14) && near(p[3], 0.51), Arrays.toString(p) + " with " + m + " vs " + probeDef.name);
		});
	}

	private static boolean near(double a, double b) {
		return Math.abs(a - b) < 1e-9;
	}

	// ------------------------------------------------------------------ multi-hit

	private static void testMultiHit() {
		test("multi-hit", () -> {
			Pokemon plain = mon(1, Ability.NULL, null), link = mon(1, Ability.SKILL_LINK, null), dice = mon(1, Ability.NULL, Item.LOADED_DICE);
			Pokemon foe = mon(4, Ability.NULL, null);
			bind(plain, foe);
			check("expected hits: Double Slap 3.1, Skill Link 5, Loaded Dice 4.5, Double Kick 2, single-hit 1",
					near(Move.DOUBLE_SLAP.expectedHits(plain, null), 3.1) && near(Move.DOUBLE_SLAP.expectedHits(link, null), 5.0)
							&& near(Move.DOUBLE_SLAP.expectedHits(dice, null), 4.5) && near(Move.DOUBLE_KICK.expectedHits(plain, null), 2.0)
							&& near(Move.HEADBUTT.expectedHits(plain, null), 1.0), "");
			int[] counts = new int[6];
			Rng.setSeed(31);
			for (int i = 0; i < 20000; i++) counts[Move.DOUBLE_SLAP.getNumHits(plain, null)]++;
			check("getNumHits samples 35/35/15/15 (through Rng now, not Math.random)", Math.abs(counts[2] / 20000.0 - 0.35) < 0.02 && Math.abs(counts[3] / 20000.0 - 0.35) < 0.02
					&& Math.abs(counts[4] / 20000.0 - 0.15) < 0.02 && Math.abs(counts[5] / 20000.0 - 0.15) < 0.02, Arrays.toString(counts));
			Rng.setSeed(32);
			int a1 = Move.DOUBLE_SLAP.getNumHits(plain, null) * 10 + Move.DOUBLE_SLAP.getNumHits(plain, null);
			Rng.setSeed(32);
			int a2 = Move.DOUBLE_SLAP.getNumHits(plain, null) * 10 + Move.DOUBLE_SLAP.getNumHits(plain, null);
			check("getNumHits replays from a seed", a1 == a2, "");
			int inSim;
			try (SimContext.Scope s = SimContext.enter()) {
				inSim = Move.DOUBLE_SLAP.getNumHits(plain, null);
			}
			check("getNumHits inside a simulation is the expected count (3), no draw", inSim == 3, "got " + inSim);
			DamageRange r = plain.calcRange(foe, Move.DOUBLE_SLAP, true, Pokemon.field);
			check("calcRange carries the expected hit count", !r.dealsDamage() || near(r.hits, 3.1), r.toString());
		});
	}

	// ------------------------------------------------------------------ simulation: deterministic, isolated, silent

	private static List<Move> sampleMoves() {
		List<Move> out = new ArrayList<>();
		int attacks = 0, statuses = 0;
		for (Move m : Move.getAllMoves()) {
			if (m == null || m == Move.STRUGGLE) continue;
			if (m.cat == 2 ? statuses < 40 : attacks < 80) {
				out.add(m);
				if (m.cat == 2) statuses++; else attacks++;
			}
		}
		return out;
	}

	private static void testSimDeterministicAndIsolated() {
		test("sim", () -> {
			List<Move> moves = sampleMoves();
			Pokemon atk = mon(1, Ability.NULL, null), def = mon(4, Ability.NULL, null);
			bind(atk, def);
			int mismatched = 0, unpoliced = 0, crashed = 0;
			List<String> notes = new ArrayList<>();
			String firstCrash = "";
			Rng.setSeed(77);
			double control = Rng.next();
			Rng.setSeed(77);
			Field f = Pokemon.field;
			f.crits = f.misses = f.superEffective = 0;
			gp.ui.tasks.clear();
			for (Move m : moves) {
				String[] out = new String[2];
				long draws = 0;
				try {
					for (int run = 0; run < 2; run++) {
						Pokemon ac = atk.fullClone(), dc = def.fullClone();
						ac.cloned = false; // prove SimContext alone keeps recorders, counters and tasks quiet
						dc.cloned = false;
						try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withSeed(9))) {
							ac.move(dc, m, true, false);
							if (run == 0) draws = Rng.isolatedAccesses();
						}
						out[run] = fp(ac) + "/" + fp(dc);
					}
					if (!out[0].equals(out[1])) {
						mismatched++;
						if (notes.size() < 5) notes.add(m + ": " + out[0] + " vs " + out[1]);
					}
					if (draws > 0) {
						unpoliced++;
					}
				} catch (Throwable t) {
					crashed++;
					if (firstCrash.isEmpty()) firstCrash = m + ": " + t;
				}
			}
			check("move() under SimContext gives identical results on identical clones (" + moves.size() + " moves)", mismatched == 0 && crashed == 0,
					mismatched + " differ, " + crashed + " threw " + firstCrash + " " + notes);
			check("simulating did not consume the real RNG stream", Rng.next() == control, "");
			check("no Tasks were created while simulating", gp.ui.tasks.isEmpty(), "gp.ui.tasks.size() = " + gp.ui.tasks.size());
			check("no shared field counters (crits / misses / superEffective) moved while simulating", f.crits + f.misses + f.superEffective == 0,
					"crits=" + f.crits + " misses=" + f.misses + " superEffective=" + f.superEffective);
			System.out.println("  info: " + unpoliced + " of " + moves.size() + " sampled moves drew from the isolated stream (randomness the policy does not cover yet; the Phase 4 worklist)");

			// control: outside a simulation the same code does create tasks and count stats, so the checks above can fail
			for (int i = 0; i < 20; i++) {
				Pokemon ac = mon(1, Ability.NULL, null), dc = mon(4, Ability.NULL, null);
				bind(ac, dc);
				Pokemon.field = f;
				ac.moveInit(dc, Move.HEADBUTT, true);
			}
			check("control: outside SimContext the same moves create Tasks and count stats", !gp.ui.tasks.isEmpty() && f.crits + f.misses + f.superEffective > 0,
					"tasks=" + gp.ui.tasks.size() + " counters=" + (f.crits + f.misses + f.superEffective));
		});
	}

	private static void testSimPolicyForcesOutcomes() {
		test("sim-policy", () -> {
			Move acc = null;
			Pokemon atk = mon(1, Ability.NULL, null), def = mon(4, Ability.NULL, null);
			bind(atk, def);
			for (Move m : Move.getAllMoves()) {
				if (m != null && m.isAttack() && m.basePower > 0 && m.accuracy >= 50 && m.accuracy < 100 && m.secondary == 0 && !m.isPivotMove()
						&& atk.calcRange(def, m, true, Pokemon.field).dealsDamage()) {
					acc = m;
					break;
				}
			}
			if (acc == null) { check("policy accuracy (skipped: no 50-99% accurate plain attack that hurts this pair)", true, ""); return; }
			int missHp, hitHp;
			{
				Pokemon ac = atk.fullClone(), dc = def.fullClone();
				int hp = dc.currentHP;
				try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.MISS))) {
					ac.move(dc, acc, true, false);
				}
				missHp = hp - dc.currentHP;
			}
			{
				Pokemon ac = atk.fullClone(), dc = def.fullClone();
				int hp = dc.currentHP;
				try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.HIT))) {
					ac.move(dc, acc, true, false);
				}
				hitHp = hp - dc.currentHP;
			}
			check("SimPolicy.Accuracy forces the outcome of " + acc + " (MISS: no damage, HIT: damage)", missHp == 0 && hitHp > 0, "MISS lost " + missHp + ", HIT lost " + hitHp);
			// roll: the policy roll scales calc() (0.925 default vs 1.0)
			int lowRoll, highRoll;
			try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withDamageRoll(0.85))) {
				lowRoll = atk.calc(100, 100, 80, 50, 0);
			}
			try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT.withDamageRoll(1.0))) {
				highRoll = atk.calc(100, 100, 80, 50, 0);
			}
			check("calc() in a simulation uses the policy roll and is repeatable", lowRoll < highRoll && atk.calc(100, 100, 80, 50, 1) == atk.calcRoll(100, 100, 80, 50, 1.0), lowRoll + " < " + highRoll);
		});
	}

	// ------------------------------------------------------------------ Task

	private static void testTaskHelpersAreInertInSim() {
		test("task-inert", () -> {
			Pokemon p = mon(1, Ability.NULL, null), q = mon(4, Ability.NULL, null);
			bind(p, q);
			gp.ui.tasks.clear();
			Task made, made2, anim, protect, got;
			try (SimContext.Scope s = SimContext.enter()) {
				made = Task.addTask(Task.TEXT, "hello");
				made2 = Task.addTask(Task.STATUS, Status.HEALTHY, "x", p);
				Task.addSwapInTask(p, false);
				Task.addSwapOutTask(p, false);
				Task.insertTask(new Task(Task.TEXT, "y"), 0);
				Task.setTask(0, new Task(Task.TEXT, "z"));
				got = Task.getTask(0);
				anim = Task.addMoveAnimTask(Move.HEADBUTT, "m", p, q, "attacking", null);
				protect = Task.addProtectAnimTask("p", p, q);
			}
			check("Task helpers return usable detached objects and queue nothing inside a simulation",
					made != null && made2 != null && anim != null && protect != null && got == null && gp.ui.tasks.isEmpty(), "queued " + gp.ui.tasks.size());
			Task.addTask(Task.TEXT, "outside");
			check("control: outside a simulation Task.addTask still queues (TASK_STATE)", gp.ui.tasks.size() == 1, "queued " + gp.ui.tasks.size());
		});
	}
}
