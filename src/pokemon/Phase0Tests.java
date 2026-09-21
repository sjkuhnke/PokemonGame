package pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import overworld.GamePanel;
import util.Print;
import util.Rng;

/**
 * Phase 0 scenario tests (T15 bug fixes, B9/B11 state hygiene) and the T1 damage-parity skeleton.
 * <p>
 * Run inside the running game, on the game thread, with no battle active: {@code Phase0Tests.runAll();}
 * Tests build throwaway mons and trainers; nothing touches saves or the player's team.
 * <p>
 * NOTE: written against the APIs visible in the uploaded sources (not compiled against your full project).
 * If a fixture call does not match your code, the test reports an ERROR line instead of a false PASS.
 */
public final class Phase0Tests {
	private Phase0Tests() {}

	private static int passed, failed, errors;

	// ------------------------------------------------------------------ plumbing

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
		gp.gameState = GamePanel.TASK_STATE; // same state Pokemon.simulateBattle runs in
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

	/** Puts each mon on its own throwaway one-mon trainer, and clears the shared field. */
	private static void bind(Pokemon a, Pokemon b) {
		Trainer ta = new Trainer("T-" + a.name, new Pokemon[] { a }, 0);
		Trainer tb = new Trainer("T-" + b.name, new Pokemon[] { b }, 0);
		ta.boosts = new int[3];
		tb.boosts = new int[3];
		Pokemon.field.clear(ta, tb);
	}

	/** First defender (by id) that takes damage from {@code m} out of {@code atk}, so type immunities can't fake a pass. */
	private static Pokemon findTarget(Pokemon atk, Move m, Ability defAbility) {
		for (int id = 1; id <= Math.min(Pokemon.MAX_POKEMON, 80); id++) {
			Pokemon d = mon(id, defAbility, null);
			bind(atk, d);
			if (atk.calcWithTypes(d, m, true, 1, false, Pokemon.field, false).getFirst() > 0) return d;
		}
		throw new IllegalStateException("no target takes damage from " + m);
	}

	private static Move firstMove(java.util.function.Predicate<Move> pred) {
		for (Move m : Move.getAllMoves()) {
			if (m != null && pred.test(m)) return m;
		}
		return null;
	}

	// ------------------------------------------------------------------ entry

	/** @return true if every test passed and none errored */
	public static boolean runAll() {
		passed = failed = errors = 0;
		enter();
		try {
			testB3ShedSkin();
			testB4MagicReflect();
			testB6Merciless();
			testB7Neuroforce();
			testB9CreateTaskRestored();
			testB11ClonesDontTouchFieldCounters();
			testB12LeechSeedBigRoot();
			testB16FangRandomBranch();
		} finally {
			exit();
		}
		System.out.println(String.format(Locale.ROOT, "Phase 0 tests: %d passed, %d failed, %d errors", passed, failed, errors));
		return failed == 0 && errors == 0;
	}

	// ------------------------------------------------------------------ T15

	private static void testB3ShedSkin() {
		test("B3", () -> {
			Pokemon p = mon(1, Ability.SHED_SKIN, null);
			Pokemon foe = mon(4, Ability.NULL, null);
			bind(p, foe);
			Rng.setSeed(3);
			int healed = 0, n = 4000;
			for (int i = 0; i < n; i++) {
				p.status = Status.PARALYZED;
				p.endOfTurn(foe);
				if (p.status == Status.HEALTHY) healed++;
				gp.ui.tasks.clear();
			}
			double rate = healed / (double) n;
			check("B3 Shed Skin cures ~1/2 of the time (was always)", Math.abs(rate - 0.5) < 0.04, String.format(Locale.ROOT, "rate=%.3f", rate));
		});
	}

	private static void testB4MagicReflect() {
		test("B4", () -> {
			Pokemon atk = mon(1, Ability.NULL, null);
			Pokemon def = findTarget(atk, Move.BRICK_BREAK, Ability.NULL);
			bind(atk, def);
			def.addStatus(Status.MAGIC_REFLECT, 0);
			int atkHp = atk.currentHP, defHp = def.currentHP;
			atk.moveInit(def, Move.BRICK_BREAK, true);
			check("B4 Brick Break breaks Magic Reflect instead of being reflected",
					!def.hasStatus(Status.MAGIC_REFLECT) && atk.currentHP == atkHp && def.currentHP < defHp,
					"atkHP " + atkHp + "->" + atk.currentHP + ", defHP " + defHp + "->" + def.currentHP);

			// control: an ordinary move is still reflected
			Pokemon atk2 = mon(1, Ability.NULL, null);
			Pokemon def2 = findTarget(atk2, Move.HEADBUTT, Ability.NULL);
			bind(atk2, def2);
			def2.addStatus(Status.MAGIC_REFLECT, 0);
			int a2 = atk2.currentHP, d2 = def2.currentHP;
			atk2.moveInit(def2, Move.HEADBUTT, true);
			check("B4 control: Headbutt is reflected onto the user", def2.currentHP == d2 && atk2.currentHP < a2,
					"atkHP " + a2 + "->" + atk2.currentHP + ", defHP " + d2 + "->" + def2.currentHP);
		});
	}

	private static void testB6Merciless() {
		test("B6", () -> {
			Pokemon atk = mon(1, Ability.MERCILESS, null);
			Pokemon def = findTarget(atk, Move.HEADBUTT, Ability.NULL);
			bind(atk, def);
			// mode 1 = max roll, crit=false: only an automatic crit (crit chance >= 3) can change the number
			def.status = Status.HEALTHY;
			int healthy = atk.calcWithTypes(def, Move.HEADBUTT, true, 1, false, Pokemon.field, false).getFirst();
			def.status = Status.PARALYZED;
			int para = atk.calcWithTypes(def, Move.HEADBUTT, true, 1, false, Pokemon.field, false).getFirst();
			check("B6 calcWithTypes: Merciless auto-crits a PARALYZED target (matches move())", para > healthy * 1.3,
					"healthy=" + healthy + " paralyzed=" + para);

			Pokemon plain = mon(1, Ability.NULL, null);
			bind(plain, def);
			int plainPara = plain.calcWithTypes(def, Move.HEADBUTT, true, 1, false, Pokemon.field, false).getFirst();
			check("B6 control: non-Merciless attacker is unaffected by paralysis", plainPara == healthy, "plain=" + plainPara + " healthy=" + healthy);
		});
	}

	private static void testB7Neuroforce() {
		test("B7", () -> {
			Move light = firstMove(m -> m.mtype == PType.LIGHT && m.isAttack() && m.basePower > 0 && m.accuracy >= 100 && m.secondary == 0);
			if (light == null) { check("B7 (skipped: no plain Light attack found)", true, ""); return; }
			Pokemon atk = mon(1, Ability.NULL, null);
			Pokemon def = findTarget(atk, light, Ability.NULL);
			def.ability = Ability.NEUROFORCE;
			bind(atk, def);
			int calc = atk.calcWithTypes(def, light, true, 1, false, Pokemon.field, false).getFirst();
			int hp = def.currentHP;
			atk.moveInit(def, light, true);
			check("B7 Neuroforce is immune to Light in BOTH calcWithTypes and move()", calc == 0 && def.currentHP == hp,
					"move=" + light + " calc=" + calc + " defHP " + hp + "->" + def.currentHP);
		});
	}

	private static void testB9CreateTaskRestored() {
		test("B9", () -> {
			Pokemon ally = mon(1, Ability.NULL, null);
			Pokemon other = mon(2, Ability.NULL, null);
			Pokemon foe = mon(4, Ability.NULL, null);
			new Trainer("T-ally", new Pokemon[] { ally, other }, 0).boosts = new int[3];
			Trainer tf = new Trainer("T-foe", new Pokemon[] { foe }, 0);
			tf.boosts = new int[3];
			Pokemon.field.clear(ally.trainer, tf);

			Pokemon.createTask = false;
			ally.evaluateSwitchInScore(foe, Pokemon.field);
			check("B9 simulateSwitchIn (via evaluateSwitchInScore) restores createTask=false", !Pokemon.createTask, "createTask=" + Pokemon.createTask);

			Pokemon.createTask = false;
			ally.analyzeMoveEffect(foe, Move.GROWL, true, Pokemon.field, 0, null, Move.HEADBUTT, 50.0);
			check("B9 analyzeMoveEffect restores createTask=false", !Pokemon.createTask, "createTask=" + Pokemon.createTask);

			Pokemon.createTask = true;
			ally.evaluateSwitchInScore(foe, Pokemon.field);
			ally.analyzeMoveEffect(foe, Move.GROWL, true, Pokemon.field, 0, null, Move.HEADBUTT, 50.0);
			check("B9 createTask stays true when it was true", Pokemon.createTask, "");
		});
	}

	private static void testB11ClonesDontTouchFieldCounters() {
		test("B11", () -> {
			List<Move> moves = new ArrayList<>();
			for (Move m : Move.getAllMoves()) {
				if (m != null && m.isAttack() && m.basePower > 0 && m.secondary == 0 && m.accuracy >= 100 && !m.isPivotMove()) moves.add(m);
				if (moves.size() >= 12) break;
			}
			Pokemon atk = mon(1, Ability.NULL, null);
			Pokemon def = mon(4, Ability.NULL, null);
			bind(atk, def);

			// clones: the AI's copies must leave the shared field counters alone
			Field f = new Field();
			Pokemon.field = f;
			for (Move m : moves) {
				for (int i = 0; i < 10; i++) {
					Pokemon a = atk.fullClone();
					Pokemon d = def.fullClone();
					a.moveInit(d, m, true);
					gp.ui.tasks.clear();
				}
			}
			int cloned = f.crits + f.misses + f.superEffective;
			check("B11 clone move() leaves field.crits/misses/superEffective at 0", cloned == 0,
					"crits=" + f.crits + " misses=" + f.misses + " superEffective=" + f.superEffective);

			// control: real (non-cloned) mons DO count, so the guard isn't just disabling the counters
			Field g = new Field();
			Pokemon.field = g;
			for (Move m : moves) {
				for (int i = 0; i < 10; i++) {
					Pokemon a = mon(1, Ability.NULL, null);
					Pokemon d = mon(4, Ability.NULL, null);
					bind(a, d);
					Pokemon.field = g;
					a.moveInit(d, m, true);
					gp.ui.tasks.clear();
				}
			}
			int real = g.crits + g.misses + g.superEffective;
			check("B11 control: non-cloned mons still count (betting-sim parlay stats keep working)", real > 0, "total=" + real);
			// Player recorders (recordTurn/DamageDealt/DamageTaken/PPUse/Kill/Death) are guarded the same way;
			// covering them needs a Player-owned mon, which this harness deliberately does not build.
		});
	}

	private static void testB12LeechSeedBigRoot() {
		test("B12", () -> {
			int[] ids = { 1, 2 };
			double[] ratio = new double[2];
			for (int k = 0; k < 2; k++) {
				Pokemon victim = mon(ids[0], Ability.NULL, null);
				Pokemon drainer = mon(4, Ability.NULL, k == 0 ? Item.BIG_ROOT : null);
				bind(victim, drainer);
				victim.addStatus(Status.LEECHED, 0);
				drainer.currentHP = Math.max(1, drainer.getStat(0) / 2);
				int dBefore = drainer.currentHP, vBefore = victim.currentHP;
				victim.endOfTurn(drainer);
				int dmg = vBefore - victim.currentHP, healed = drainer.currentHP - dBefore;
				ratio[k] = dmg <= 0 ? Double.NaN : healed / (double) dmg;
			}
			check("B12 Leech Seed heals 1.3x the damage with Big Root", Math.abs(ratio[0] - 1.3) < 0.15, String.format(Locale.ROOT, "heal/damage=%.2f", ratio[0]));
			check("B12 control: without Big Root it heals 1.0x", Math.abs(ratio[1] - 1.0) < 0.15, String.format(Locale.ROOT, "heal/damage=%.2f", ratio[1]));
		});
	}

	private static void testB16FangRandomBranch() {
		test("B16", () -> {
			// Fire Fang's secondary picks randomNum in {0,1,2}: 0 = burn, 1 = flinch (if first), 2 = both.
			// The old (int) Math.random() * 3 was always 0, so a flinch could never happen. Serene Grace doubles the proc chance.
			int flinches = 0, procs = 0, n = 1500;
			Rng.setSeed(16);
			for (int i = 0; i < n; i++) {
				Pokemon atk = mon(1, Ability.SERENE_GRACE, null);
				Pokemon def = mon(4, Ability.NULL, null);
				bind(atk, def);
				atk.moveInit(def, Move.FIRE_FANG, true);
				if (def.hasStatus(Status.FLINCHED)) flinches++;
				if (def.hasStatus(Status.FLINCHED) || def.status != Status.HEALTHY) procs++;
				gp.ui.tasks.clear();
			}
			check("B16 Fire Fang can flinch now (was impossible)", flinches > 0, "flinches=" + flinches + " procs=" + procs + " of " + n);
		});
	}

	// ------------------------------------------------------------------ T1 skeleton

	/**
	 * T1 skeleton: for random mon pairs and their plain attacking moves, compare
	 * calcWithTypes (mode 0 mean) with the mean HP loss from a real move() on fullClones.
	 * Prints every pair where the relative gap exceeds {@code tolerance} (fraction of the defender's max HP).
	 * Phase 1 will replace the calc side with calcRange; the mismatch list from this run is the baseline.
	 */
	public static List<String> damageParity(int monCount, int samples, double tolerance) {
		List<String> mismatches = new ArrayList<>();
		enter();
		try {
			Rng.setSeed(1);
			List<Pokemon> mons = new ArrayList<>();
			for (int i = 0; i < monCount; i++) {
				Pokemon p = mon(Rng.nextInt(Pokemon.MAX_POKEMON) + 1, Ability.NULL, null);
				mons.add(p);
			}
			int compared = 0;
			for (Pokemon a : mons) {
				for (Pokemon d : mons) {
					if (a == d) continue;
					bind(a, d);
					for (Moveslot slot : a.moveset) {
						if (slot == null) continue;
						Move m = slot.move;
						if (!m.isAttack() || m.basePower <= 0 || m.secondary != 0 || m.accuracy < 100 || m.isPivotMove()) continue;
						if (a.calcWithTypes(d, m, true, 1, false, Pokemon.field, false).getFirst() < 0) continue; // unusable
						double calcSum = 0, simSum = 0;
						for (int i = 0; i < samples; i++) {
							calcSum += Math.max(0, a.calcWithTypes(d, m, true, 0, false, Pokemon.field, false).getFirst());
							Pokemon ac = a.fullClone(), dc = d.fullClone();
							dc.currentHP = dc.getStat(0);
							int before = dc.currentHP;
							ac.moveInit(dc, m, true);
							simSum += before - Math.max(0, dc.currentHP);
							gp.ui.tasks.clear();
						}
						compared++;
						double gap = Math.abs(calcSum - simSum) / samples / Math.max(1, d.getStat(0));
						if (gap > tolerance) {
							mismatches.add(String.format(Locale.ROOT, "%s -> %s with %s: calc avg %.1f, move() avg %.1f (gap %.1f%% of max HP)",
									a.name, d.name, m, calcSum / samples, simSum / samples, gap * 100));
						}
					}
				}
			}
			System.out.println("T1 parity: compared " + compared + " (attacker, defender, move) triples, " + mismatches.size() + " over " + (tolerance * 100) + "% of max HP");
			for (String s : mismatches) System.out.println("  MISMATCH " + s);
		} finally {
			exit();
		}
		return mismatches;
	}
}