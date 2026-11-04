package test;

import java.util.ArrayList;
import java.util.List;

import ai.*;
import pokemon.*;

/**
 * Focus Sash / Sturdy / Endure vs multi-hit moves, and calc-vs-simulator agreement.
 * <p>
 * Part A is pure math on {@link DamageRange#of} (no engine, no species): a Sash or Sturdy at full HP only stops a SINGLE-hit
 * use, Endure / False Swipe ({@code neverKills}) stop every hit.
 * Part B runs the real calc ({@link SimCache#calcRange}) and the real simulator ({@link BattleSimulator#simulateTurn}) over a
 * sweep of defenders and checks they agree whenever the calc is decisive (koChance >= 0.95 or <= 0.05).
 * Part C is a debug-key helper that runs {@link AIV2#plan} on a real position so you can read the matrix.
 * <p>
 * Call {@link #runAll()} from the game thread (Pokemon.gp must exist, like the other PhaseNTests).
 * Adjust if needed: {@code Pokemon#addStatus(Status.ENDURE)} must be reachable from package {@code test}.
 */
public final class SashMultiHitTests {
	private SashMultiHitTests() {}

	private static int passed, failed;

	private static void check(String name, boolean ok, String detail) {
		if (ok) passed++; else failed++;
		System.out.println((ok ? "PASS  " : "FAIL  ") + name + (detail.isEmpty() ? "" : "  [" + detail + "]"));
	}

	private static boolean approx(double a, double b) {
		return Math.abs(a - b) < 1e-9;
	}

	public static void runAll() {
		passed = 0;
		failed = 0;
		try {
			partA();
			partB();
		} catch (Throwable t) {
			failed++;
			System.out.println("ERROR SashMultiHitTests: " + t);
			t.printStackTrace(System.out);
		}
		System.out.println("SashMultiHitTests: " + passed + " passed, " + failed + " failed");
	}

	// ====================================================================== Part A: pure DamageRange math

	private static final int HP = 282;
	private static final double[] SKILL_LINK = { 0, 0, 0, 0, 0, 1.0 };
	private static final double[] SPREAD = { 0, 0, 0.35, 0.35, 0.15, 0.15 };

	/** 16 equally likely per-hit rolls from lo to hi, no crit, 100% accuracy, target max HP = HP. */
	private static DamageRange range(double lo, double hi, double[] hitProb, boolean sash, boolean neverKills) {
		double[] v = new double[16], w = new double[16];
		for (int i = 0; i < 16; i++) {
			v[i] = lo + (hi - lo) * i / 15.0;
			w[i] = 1.0 / 16;
		}
		return DamageRange.of(v, w, lo, (lo + hi) / 2, hi, 0, 1.0, hitProb, HP, sash, neverKills, false);
	}

	static void partA() {
		// 1. Skill Link (5 hits) vs a Sash at full HP: the Sash only stops hit 1, so koProb equals the no-Sash value.
		DamageRange sash = range(52, 62, SKILL_LINK, true, false);
		DamageRange plain = range(52, 62, SKILL_LINK, false, false);
		check("A1 skill link: sash koProb == no-sash koProb", approx(sash.koProb(HP), plain.koProb(HP)) && sash.koProb(HP) > 0.3,
				"sash=" + sash.koProb(HP) + " plain=" + plain.koProb(HP));
		check("A1 skill link: endures() is false", !sash.endures(HP), "");
		check("A1 skill link: expectedCapped unchanged by the sash", approx(sash.expectedCapped(HP), plain.expectedCapped(HP)),
				sash.expectedCapped(HP) + " vs " + plain.expectedCapped(HP));

		// 2. Single hit: the Sash still works (unchanged behaviour), but only at full HP.
		DamageRange one = range(300, 320, new double[] { 0, 1.0 }, true, false);
		check("A2 single hit: sash blocks the KO at full HP", one.koProb(HP) == 0 && one.endures(HP), "ko=" + one.koProb(HP));
		check("A2 single hit: expectedCapped leaves 1 HP", approx(one.expectedCapped(HP), HP - 1), "" + one.expectedCapped(HP));
		check("A2 single hit: below full HP the sash does nothing", one.koProb(200) == 1.0 && !one.endures(200), "ko=" + one.koProb(200));

		// 3. 2-5 hit spread: minimum is 2 hits, so the Sash never fully blocks it.
		DamageRange spreadSash = range(52, 62, SPREAD, true, false);
		DamageRange spreadPlain = range(52, 62, SPREAD, false, false);
		check("A3 spread: sash koProb == no-sash koProb (> 0)",
				approx(spreadSash.koProb(HP), spreadPlain.koProb(HP)) && spreadSash.koProb(HP) > 0,
				spreadSash.koProb(HP) + " vs " + spreadPlain.koProb(HP));
		check("A3 spread: endures() is false", !spreadSash.endures(HP), "");

		// 4. Mixed 1-or-2 hits: only the 1-hit outcome is blocked.
		DamageRange mixSash = range(290, 300, new double[] { 0, 0.5, 0.5 }, true, false);
		DamageRange mixPlain = range(290, 300, new double[] { 0, 0.5, 0.5 }, false, false);
		check("A4 mixed 1/2 hits: sash removes exactly the 1-hit half", approx(mixSash.koProb(HP), 0.5) && approx(mixPlain.koProb(HP), 1.0),
				mixSash.koProb(HP) + " / " + mixPlain.koProb(HP));

		// 5. neverKills (Endure / False Swipe) stops EVERY hit, including all 5 of a Skill Link move.
		DamageRange endure = range(52, 62, SKILL_LINK, false, true);
		check("A5 endure/neverKills vs 5 hits: koProb 0, endures", endure.koProb(HP) == 0 && endure.endures(HP), "ko=" + endure.koProb(HP));
		check("A5 endure/neverKills vs 5 hits: expectedCapped <= hp-1", endure.expectedCapped(HP) <= HP - 1 + 1e-9, "" + endure.expectedCapped(HP));
	}

	// ====================================================================== Part B: calc vs simulator

	private enum Guard { NONE, SASH, ENDURE }

	private static Pokemon mk(int id, int level, Ability ab, Item item, Move... moves) {
		Pokemon p = new Pokemon(id, level, true, true);
		p.ability = ab;
		p.item = item;
		for (int i = 0; i < p.moveset.length; i++) p.moveset[i] = i < moves.length ? new Moveslot(moves[i]) : null;
		p.currentHP = p.getStat(0);
		p.status = Status.HEALTHY;
		for (Moveslot ms : p.moveset) if (ms != null) ms.currentPP = 99;
		return p;
	}

	/** AI = the defender (slot 0), player = the attacker. Fresh Field installed as Pokemon.field. */
	private static final class Setup {
		Pokemon def, atk;
		Field field;
		SimState root;
	}

	private static Setup setup(int defId, int defLevel, Guard g, Ability atkAbility, Move move) {
		Pokemon d = mk(defId, defLevel, Ability.NULL, g == Guard.SASH ? Item.FOCUS_SASH : null, Move.FLAMETHROWER);
		Pokemon a = mk(4, 100, atkAbility, null, move);
		Pokemon[] at = { d, mk(2, 50, Ability.NULL, null, Move.FLAMETHROWER), mk(3, 50, Ability.NULL, null, Move.FLAMETHROWER) };
		Pokemon[] pt = { a, mk(5, 50, Ability.NULL, null, Move.FLAMETHROWER), mk(6, 50, Ability.NULL, null, Move.FLAMETHROWER) };
		Trainer ta = new Trainer("SMH-ai", at, 0);
		Trainer tp = new Trainer("SMH-pl", pt, 0);
		ta.boosts = new int[3];
		tp.boosts = new int[3];
		Field field = new Field();
		Pokemon.field = field;
		field.clear(ta, tp);
		ta.setCurrent(d);
		tp.setCurrent(a);
		if (g == Guard.ENDURE) d.addStatus(Status.ENDURE);
		Setup s = new Setup();
		s.def = d;
		s.atk = a;
		s.field = field;
		s.root = SimState.snapshot(d, a);
		return s;
	}

	private static double koChance(Setup s, Move move) {
		SimCache.clear();
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			DamageRange r = SimCache.calcRange(s.atk, s.def, move, true, s.field);
			return r.koProb(s.def.currentHP) * r.accuracy;
		}
	}

	/** P(the defender, slot 0 of the AI side, is fainted after the player uses {@code move} and the AI passes). */
	private static double simFaintProb(Setup s, Move move) {
		List<Branch> bs = BattleSimulator.simulateTurn(s.root, Action.PASS, new Action(move), AIConfig.hard());
		double p = 0;
		for (Branch b : bs) if (b.state.ai.bench()[0].isFainted()) p += b.prob;
		return p;
	}

	static void partB() {
		final Move move = Move.SHOOTING_STARS;
		final int[] levels = { 30, 45, 60, 80, 100 };
		final int maxId = Math.min(Pokemon.MAX_POKEMON, 25);
		final Ability[] atkAbilities = { Ability.SKILL_LINK, Ability.NULL };

		Pokemon prevFieldHolder = null; // (Pokemon.field is restored below)
		Field prev = Pokemon.field;
		int decisive = 0, kos = 0, endureChecked = 0, sashEqChecked = 0;
		List<String> mismatches = new ArrayList<>();
		List<String> relation = new ArrayList<>();
		try {
			for (Ability atkAb : atkAbilities) {
				for (int id = 1; id <= maxId; id++) {
					for (int lvl : levels) {
						double none = koChance(setup(id, lvl, Guard.NONE, atkAb, move), move);
						Setup sSash = setup(id, lvl, Guard.SASH, atkAb, move);
						double sash = koChance(sSash, move);
						Setup sEnd = setup(id, lvl, Guard.ENDURE, atkAb, move);
						double end = koChance(sEnd, move);
						String tag = "id=" + id + " lvl=" + lvl + " atk=" + atkAb;

						// Relationships that must hold regardless of the sim.
						if (end != 0) relation.add("ENDURE koChance must be 0, got " + end + " " + tag);
						endureChecked++;
						if (atkAb == Ability.SKILL_LINK) {
							if (!approx(none, sash)) relation.add("skill link: sash (" + sash + ") != none (" + none + ") " + tag);
							sashEqChecked++;
						} else if (sash > none + 1e-9) {
							relation.add("sash raised koChance: " + sash + " > " + none + " " + tag);
						}

						// Calc vs sim, decisive cases only.
						for (Object[] c : new Object[][] { { Guard.NONE, none }, { Guard.SASH, sash }, { Guard.ENDURE, end } }) {
							Guard g = (Guard) c[0];
							double ko = (Double) c[1];
							if (!(ko >= 0.95 || ko <= 0.05)) continue;
							Setup s = g == Guard.SASH ? sSash : g == Guard.ENDURE ? sEnd : setup(id, lvl, Guard.NONE, atkAb, move);
							double f = simFaintProb(s, move);
							decisive++;
							if (ko >= 0.95) kos++;
							if ((ko >= 0.95 && f < 0.99) || (ko <= 0.05 && f > 0.01)) {
								mismatches.add(g + " calc ko=" + String.format("%.2f", ko) + " sim faint=" + String.format("%.2f", f) + " " + tag);
							}
						}
					}
				}
			}
		} finally {
			Pokemon.field = prev;
		}

		check("B1 relations (Endure = 0; Skill Link sash == none; sash never raises)", relation.isEmpty(),
				relation.isEmpty() ? endureChecked + " endure and " + sashEqChecked + " skill-link cases" : relation.get(0) + " (+" + (relation.size() - 1) + " more)");
		check("B2 calc and sim agree on every decisive case", mismatches.isEmpty(),
				mismatches.isEmpty() ? decisive + " decisive cases" : mismatches.size() + " mismatches, first: " + mismatches.get(0));
		for (int i = 0; i < Math.min(10, mismatches.size()); i++) System.out.println("      mismatch: " + mismatches.get(i));
		check("B3 the sweep actually exercised KOs (not vacuous)", kos >= 20, "decisive KO cases=" + kos + " of " + decisive);
	}

	// ====================================================================== Part C: debug-key helper (Soldrota repro)

	/**
	 * Run the real decision pipeline on a live position and print the rows, the guard's drops and the mix. Call it from your
	 * overworld debug key with the two trainers mid-battle (AI trainer first), or with trainers built to match the log.
	 * Expect after the fixes: Shooting Stars is flagged as a threat to a full-HP Sash holder, the stay rows are no longer
	 * ~-248, and no U-Turn/Switch row whose target faints and returns the same mon carries probability.
	 */
	public static void debugPlan(Trainer ai, Trainer player) {
		Field prev = Pokemon.field;
		try {
			SimState root = SimState.snapshot(ai.getCurrent(), player.getCurrent());
			Pokemon.field = root.field;
			AIV2.Plan p = AIV2.plan(root, AIConfig.hard());
			StringBuilder sb = new StringBuilder("\nSashMultiHitTests.debugPlan\n");
			for (int i = 0; i < p.A.size(); i++) {
				sb.append(String.format(java.util.Locale.ROOT, "  %-28s x=%5.1f%%%n", p.A.get(i).label(), p.x[i] * 100));
			}
			if (p.guard != null) for (Action a : p.guard.dropped) sb.append("  dropped by guard: ").append(a.label()).append('\n');
			sb.append("columns: ");
			for (Action pa : p.P) sb.append(pa.label()).append(" | ");
			sb.append('\n');
			for (int i = 0; i < p.A.size(); i++) {
			    sb.append(String.format(java.util.Locale.ROOT, "  %-26s", p.A.get(i).label()));
			    for (int j = 0; j < p.P.size(); j++) sb.append(String.format(java.util.Locale.ROOT, "%8.1f", p.M[i][j]));
			    sb.append('\n');
			}
			sb.append("sacks=").append(p.sacks).append(" sackOnly=").append(p.sackOnly).append('\n');
			System.out.println(sb);
		} finally {
			Pokemon.field = prev;
		}
	}
}