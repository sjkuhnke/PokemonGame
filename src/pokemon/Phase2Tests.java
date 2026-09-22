package pokemon;

import java.util.ArrayList;
import java.util.List;

import util.Print;

public class Phase2Tests {

	/** T3: fingerprint of REAL teams/field/trainer.current, for before/after comparison. */
	public static long fingerprintReal(Trainer t1, Trainer t2, Field field) {
		SideState s1 = new SideState(t1);
		SideState s2 = new SideState(t2);
		SimState probe = new SimState(field, s1, s2, field.turns);
		return probe.fingerprint();
	}

	/**
	 * T3: run simulateTurn N times from a real battle position and assert the real state's
	 * fingerprint never changes. Caller supplies the two REAL active Pokemon and a supplier
	 * of legal (aAct, pAct) pairs to try (e.g. all of self/foe's valid moves, plus one switch
	 * each if canSwitch()) — Phase 3's genAIActions/genPlayerActions will replace the
	 * hand-rolled pairs used by the smoke test below.
	 */
	public static void runInvarianceCheck(Pokemon self, Pokemon foe, List<Action> aActs, List<Action> pActs, int trials) {
		Trainer realAi = self.trainer;
		Trainer realPlayer = foe.trainer;
		long before = fingerprintReal(realAi, realPlayer, Pokemon.field);

		AIConfig cfg = new AIConfig();
		java.util.Random r = new java.util.Random(12345);
		for (int i = 0; i < trials; i++) {
			SimState root = SimState.snapshot(self, foe);
			Action a = aActs.get(r.nextInt(aActs.size()));
			Action p = pActs.get(r.nextInt(pActs.size()));
			List<Branch> branches = BattleSimulator.simulateTurn(root, a, p, cfg);
			if (branches.isEmpty()) throw new IllegalStateException("simulateTurn produced no branches for " + a + " / " + p);
		}

		long after = fingerprintReal(realAi, realPlayer, Pokemon.field);
		if (before != after) {
			throw new IllegalStateException("T3 FAILED: real state fingerprint changed (" + before + " -> " + after + ")");
		}
		Print.debug("[Phase2Tests] T3 passed over " + trials + " trials\n");
	}

	/** Smoke test: N random legal-ish cells, no exceptions, matches §12's "1000 random cells". */
	public static void runSmokeTest(Pokemon self, Pokemon foe, int trials) {
		AIConfig cfg = new AIConfig();
		java.util.Random r = new java.util.Random(777);
		int crashes = 0;
		for (int i = 0; i < trials; i++) {
			SimState root = SimState.snapshot(self, foe);
			List<Move> aMoves = self.getValidMoveset();
			List<Move> pMoves = foe.getValidMoveset();
			if (aMoves.isEmpty() || pMoves.isEmpty()) continue;
			Action a = new Action(aMoves.get(r.nextInt(aMoves.size())));
			Action p = new Action(pMoves.get(r.nextInt(pMoves.size())));
			try {
				BattleSimulator.simulateTurn(root, a, p, cfg);
			} catch (Exception e) {
				crashes++;
				Print.debug("[Phase2Tests] crash on " + a + " / " + p + ": " + e + "\n");
			}
		}
		if (crashes > 0) throw new IllegalStateException("Smoke test: " + crashes + "/" + trials + " cells crashed");
		Print.debug("[Phase2Tests] smoke test passed over " + trials + " trials\n");
	}
	
	/** Builds a plausible action list for a Pokemon: one Action per move with PP left, plus one
	 *  SWITCH per valid bench slot. Good enough for T3/smoke testing; real legal-action generation
	 *  (accounting for Taunt, Disable, choice-lock, trapping, ...) is genAIActions in Phase 3. */
	public static List<Action> buildActions(Pokemon mon) {
		List<Action> actions = new ArrayList<>();
		if (mon.moveset != null) {
			for (Moveslot ms : mon.moveset) {
				if (ms != null && ms.move != null && ms.currentPP > 0) {
					actions.add(new Action(ms.move));
				}
			}
		}
		if (mon.trainer != null && mon.trainer.team != null) {
			Pokemon[] team = mon.trainer.team;
			for (int i = 0; i < team.length; i++) {
				Pokemon p = team[i];
				if (p != null && !p.isFainted() && p != mon.trainer.current) {
					actions.add(new Action(i));
				}
			}
		}
		return actions;
	}
}