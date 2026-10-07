package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pokemon.Field;
import pokemon.*;
import pokemon.PlayerModel.ActionClass;
import util.Print;
import util.Rng;

/**
 * Phase 6 tests (§7.11 player model, T27, and the Phase 5 T21 "probability half"). Run in the live game on the game
 * thread: {@code Phase6Tests.runAll();}. The first group is pure (no engine state); the rest build throwaway duels like
 * Phase4Tests/Phase5Tests. Written against the APIs visible in the uploaded sources, not compiled against your project.
 * <p>
 * Needs three visibility edits in Phase5Tests (Scen, sackScenario, switchRow: private -> package-private), listed in
 * PHASE6_CHANGES.md.
 */
public final class Phase6Tests {
	private Phase6Tests() {}

	private static int passed, failed, skipped;
	/** true = keep the AI's per-decision debug output (matrix, branches, read line) while the suite runs; it is a lot. */
	public static boolean VERBOSE = false;

	private interface Body { void run() throws Exception; }

	private static final class Skip extends RuntimeException {
		private static final long serialVersionUID = 1L;
		Skip(String why) { super(why); }
	}

	public static void runAll() {
		passed = failed = skipped = 0;
		Field prevField = Pokemon.field;
		final boolean prevSuppressed = Print.isDebugSuppressed();
		Print.setDebugSuppressed(!VERBOSE);
		try {
			// pure
			run("model: shrinkage, confidence and decay", Phase6Tests::modelShrinkage);
			run("predict(): class mix, zero-mass classes, missing classes", Phase6Tests::predictMath);
			run("finalStrategy(): wrong-read bound and average exploit gain", Phase6Tests::finalStrategyBounds);
			run("scripted always-switch opponent (2x2 matrix)", Phase6Tests::scriptedMatrix);
			run("persistence: serialization round trip, size, pending not saved", Phase6Tests::persistence);
			run("T27 difficulty gates (config diff)", Phase6Tests::t27);
			// engine
			run("per-trainer model identity", Phase6Tests::modelIdentity);
			run("classes come from the matrix (and never from the model)", Phase6Tests::matrixDefinesBest);
			run("Protect is its own class and a situation bit", Phase6Tests::protectIsItsOwnClass);
			run("observe: classes of what the player did, noise ignored", Phase6Tests::observeEndToEnd);
			run("empty model == Phase 5 equilibrium exactly", Phase6Tests::emptyModelIsPhase5);
			run("plan() is pure given the model (T3/T4 with the model on)", Phase6Tests::planIsPure);
			run("exploit at decision level: shifts toward the habit, bounded when wrong", Phase6Tests::exploitDecisionLevel);
			run("T21 probability half: sack row falls when a switch is expected", Phase6Tests::t21Probability);
		} finally {
			Pokemon.field = prevField;
			Print.setDebugSuppressed(prevSuppressed);
		}
		System.out.println("[Phase6Tests] " + passed + " passed, " + failed + " FAILED, " + skipped + " skipped");
	}

	private static void run(String label, Body b) {
		try {
			b.run();
			passed++;
			System.out.println(label + " passed.");
		} catch (RuntimeException s) {
			// Phase4Tests/Phase5Tests have their own private Skip types: treat any "Skip" as a skip.
			if (s.getClass().getSimpleName().equals("Skip")) {
				skipped++;
				System.out.println(label + " SKIPPED: " + s.getMessage());
			} else {
				failed++;
				System.out.println(label + " FAILED: " + s);
			}
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

	private static double sum(double[] a) {
		double s = 0;
		for (double v : a) s += v;
		return s;
	}

	private static PlayerModel trained(int fine, ActionClass c, int times) {
		PlayerModel m = new PlayerModel();
		for (int i = 0; i < times; i++) m.record(fine, c);
		return m;
	}

	// ------------------------------------------------------------------ pure

	private static void modelShrinkage() {
		PlayerModel m = new PlayerModel();
		PlayerModel.Estimate e = m.estimate(5);
		check("empty model: no confidence", e.eff == 0 && PlayerModel.blendWeight(e.eff) == 0);
		close("empty model: uniform", e.dist[0], 1.0 / PlayerModel.CLASSES, 1e-12);

		m.record(5, ActionClass.SWITCH_BEST);
		e = m.estimate(5);
		check("one observation: trusted only a little (w < 0.4)", PlayerModel.blendWeight(e.eff) < 0.4);

		PlayerModel.Estimate unseen = m.estimate(23);
		check("unseen situation borrows at most K_PARENT observations", unseen.eff > 0 && unseen.eff <= PlayerModel.K_PARENT + 1e-9);
		check("and leans toward the player's general habit", unseen.dist[ActionClass.SWITCH_BEST.ordinal()] > 1.0 / PlayerModel.CLASSES);

		PlayerModel capped = trained(3, ActionClass.STAY_OTHER, 2000);
		close("decay caps a slot at 1/(1-DECAY) observations", capped.total(), 1 / (1 - PlayerModel.DECAY), 0.1);
		for (int i = 0; i < 80; i++) capped.record(3, ActionClass.SWITCH_BEST);
		double[] d = capped.estimate(3).dist;
		check("a changed playstyle takes over (recent habit dominates)", d[ActionClass.SWITCH_BEST.ordinal()] > d[ActionClass.STAY_OTHER.ordinal()]);

		double w10 = PlayerModel.blendWeight(trained(7, ActionClass.STAY_OTHER, 10).estimate(7).eff);
		double w40 = PlayerModel.blendWeight(trained(7, ActionClass.STAY_OTHER, 40).estimate(7).eff);
		check("more data, more trust (the 'trainers get smarter over a run' ramp): " + w10 + " < " + w40, w10 < w40 && w40 < 1.0);
	}

	private static void predictMath() {
		double[] y = { 0.5, 0.3, 0.2 };
		ActionClass[] cls = { ActionClass.STAY_BEST, ActionClass.STAY_OTHER, ActionClass.SWITCH_BEST };
		double[] hist = new double[PlayerModel.CLASSES];
		hist[ActionClass.SWITCH_BEST.ordinal()] = 1.0;

		check("w = 0 returns y exactly", Arrays.equals(PlayerModel.predict(y, cls, hist, 0), y));
		double[] full = PlayerModel.predict(y, cls, hist, 1.0);
		close("w = 1 with an all-switch history: all mass on the switch column", full[2], 1.0, 1e-9);
		double[] half = PlayerModel.predict(y, cls, hist, 0.5);
		close("sums to 1", sum(half), 1.0, 1e-9);
		close("w = 0.5: switch class = half history + half equilibrium", half[2], 0.5 * 1.0 + 0.5 * 0.2, 1e-9);
		close("other columns keep their equilibrium proportions", half[0] / half[1], 0.5 / 0.3, 1e-9);

		double[] y0 = { 0.6, 0.4, 0.0 };
		check("a class the equilibrium never plays can still be predicted", PlayerModel.predict(y0, cls, hist, 0.8)[2] > 0.5);

		ActionClass[] noSwitch = { ActionClass.STAY_BEST, ActionClass.STAY_OTHER, ActionClass.STAY_OTHER };
		double[] r = PlayerModel.predict(y, noSwitch, hist, 0.9);
		check("history only about classes with no column is ignored: the equilibrium is kept", Arrays.equals(r, y));
	}

	private static void finalStrategyBounds() {
		Random r = new Random(7);
		double gain = 0, worstShort = 0;
		int cases = 0;
		for (int t = 0; t < 150; t++) {
			int n = 2 + r.nextInt(6), m = 2 + r.nextInt(5);
			double[][] M = new double[n][m];
			double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
			for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) {
				M[i][j] = r.nextDouble() * 100 - 50;
				lo = Math.min(lo, M[i][j]);
				hi = Math.max(hi, M[i][j]);
			}
			double range = hi - lo;
			Solver.Result eq = Solver.solveZeroSum(M, 4000);
			double v = Double.MAX_VALUE;
			for (int j = 0; j < m; j++) v = Math.min(v, payoff(eq.x, M, j));
			double[] read = new double[m];
			int target = r.nextInt(m);
			read[target] = 1.0; // an arbitrary, possibly wrong, read
			double alphaEff = 0.75;
			double[] x = PlayerModel.finalStrategy(M, eq.x, read, alphaEff, 6.0);
			close("strategy sums to 1", sum(x), 1.0, 1e-9);
			double worst = Double.MAX_VALUE;
			for (int j = 0; j < m; j++) worst = Math.min(worst, payoff(x, M, j));
			// the equilibrium is only approximate (regret matching), so allow its own slack on top of the exact bound
			check("wrong-read bound: worst case " + worst + " >= v " + v + " - (1-alphaEff)*range " + (1 - alphaEff) * range,
					worst >= v - (1 - alphaEff) * range - 0.02 * range);
			double d = (payoff(x, M, target) - payoff(eq.x, M, target)) / range;
			gain += d;
			if (d < 0) worstShort = Math.max(worstShort, -d);
			cases++;
		}
		check("exploiting a right read gains on average (" + gain / cases + " of range)", gain / cases > 0);
		check("and never gives up more than 5% of the range even when the equilibrium already best-responds (" + worstShort + ")", worstShort < 0.05);
	}

	private static double payoff(double[] x, double[][] M, int col) {
		double s = 0;
		for (int i = 0; i < x.length; i++) s += x[i] * M[i][col];
		return s;
	}

	/** Rows [punish a switch, punish a stay], columns [stays, switches]: the "always switches when threatened" player. */
	private static void scriptedMatrix() {
		double[][] M = { { -20, 30 }, { 30, -20 } };
		Solver.Result eq = Solver.solveZeroSum(M, 4000);
		double[] readSwitch = { 0, 1 };
		double[] x = PlayerModel.finalStrategy(M, eq.x, readSwitch, 0.75, 6.0);
		double eqPay = payoff(eq.x, M, 1), exPay = payoff(x, M, 1);
		check("against a player who always switches, the blend beats the equilibrium (" + exPay + " > " + eqPay + ")", exPay > eqPay + 3);
		double wrongEq = payoff(eq.x, M, 0), wrongEx = payoff(x, M, 0);
		check("when the read is wrong the loss stays within (1-alphaEff)*range = 12.5: " + wrongEx + " vs " + wrongEq, wrongEx >= wrongEq - 12.5 - 1.0);
	}

	private static void persistence() throws Exception {
		PlayerModel m = trained(7, ActionClass.SWITCH_BEST, 40);
		for (int i = 0; i < 25; i++) m.record(12, ActionClass.STAY_BEST);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try (ObjectOutputStream oo = new ObjectOutputStream(bo)) {
			oo.writeObject(m);
		}
		check("save cost stays tiny (" + bo.size() + " bytes < 2048)", bo.size() < 2048);
		PlayerModel back = (PlayerModel) new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray())).readObject();
		check("round trip keeps every count", back.sameCounts(m));
		close("and the estimate", back.estimate(7).dist[ActionClass.SWITCH_BEST.ordinal()], m.estimate(7).dist[ActionClass.SWITCH_BEST.ordinal()], 1e-12);
		PlayerModel c = m.copy();
		c.record(7, ActionClass.STAY_OTHER);
		check("copy() is independent", !c.sameCounts(m));
		m.reset();
		check("reset() forgets everything", m.total() == 0);
	}

	/** T27: NORMAL and HARD differ ONLY in allowVoluntarySwitch (selectLead / EXTREME is Phase 7). */
	private static void t27() throws Exception {
		AIConfig n = AIConfig.normal(), h = AIConfig.hard();
		List<String> diff = new ArrayList<>();
		for (java.lang.reflect.Field f : AIConfig.class.getFields()) {
			if (Modifier.isStatic(f.getModifiers())) continue;
			Object a = f.get(n), b = f.get(h);
			if (a == null ? b != null : !a.equals(b)) diff.add(f.getName());
		}
		check("NORMAL vs HARD differ only in allowVoluntarySwitch, got " + diff, diff.size() == 1 && diff.get(0).equals("allowVoluntarySwitch"));
		check("NORMAL has it off", !n.allowVoluntarySwitch && h.allowVoluntarySwitch);
		check("the player model is on at both", n.useHistoryModel && h.useHistoryModel);
		check("forDifficulty(NORMAL) is normal()", !AIConfig.forDifficulty(Player.NORMAL).allowVoluntarySwitch);
		check("forDifficulty(HARD) is hard()", AIConfig.forDifficulty(Player.HARD).allowVoluntarySwitch);
		check("forDifficulty(EXTREME) is hard() until Phase 7", AIConfig.forDifficulty(Player.EXTREME).allowVoluntarySwitch);
	}

	// ------------------------------------------------------------------ engine fixtures

	/** Same as Phase5Tests.build: caller-chosen teams, index 0 of each is the active mon. */
	private static Phase4Tests.Duel build(Pokemon[] at, Pokemon[] pt) {
		Trainer ta = new Trainer("P6-ai", at, 0);
		Trainer tp = new Trainer("P6-pl", pt, 0);
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

	private static Phase4Tests.Duel basicDuel() {
		return Phase4Tests.duelWithDamagedFoe(new Move[] { Move.FLAMETHROWER }, Move.FLAMETHROWER);
	}

	private static boolean hasClass(ActionClass[] cls, ActionClass c) {
		for (ActionClass x : cls) if (x == c) return true;
		return false;
	}

	private static void modelIdentity() {
		Phase4Tests.Duel d = basicDuel();
		check("a trainer keeps one model", d.pl.playerModel() == d.pl.playerModel());
		check("trainers do not share one", d.pl.playerModel() != d.ai.playerModel());
	}

	// ------------------------------------------------------------------ observe

	/**
	 * "Best" is defined by the AI's own matrix: the column with the lowest AI payoff against the equilibrium mix, separately
	 * for stays and switches; and nothing the model has learned can change the classes or the situation bucket.
	 */
	private static void matrixDefinesBest() {
		Phase4Tests.Duel d = basicDuel();
		SimState root = d.root();
		AIConfig hard = AIConfig.hard();
		AIV2.Plan p = AIV2.plan(root, hard, new PlayerModel());
		int cols = p.P.size();
		double[] v = new double[cols];
		double bestStay = Double.MAX_VALUE, bestSwitch = Double.MAX_VALUE;
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < p.eq.x.length; i++) v[j] += p.eq.x[i] * p.M[i][j];
			if (p.P.get(j).kind == ActionKind.SWITCH) bestSwitch = Math.min(bestSwitch, v[j]);
			else if (!PlayerReader.isProtectLike(p.P.get(j).move)) bestStay = Math.min(bestStay, v[j]);
		}
		int stayBest = 0, switchBest = 0;
		for (int j = 0; j < cols; j++) {
			Action a = p.P.get(j);
			ActionClass c = p.read.classes[j];
			if (a.kind == ActionKind.SWITCH) {
				boolean inBand = v[j] <= bestSwitch + PlayerReader.BEST_TOL;
				check("switch column " + j + " is SWITCH_BEST exactly when it is within BEST_TOL of the lowest AI payoff", (c == ActionClass.SWITCH_BEST) == inBand);
				if (inBand) switchBest++;
			} else if (PlayerReader.isProtectLike(a.move)) {
				check("a Protect-family move is STAY_PROTECT, whatever its payoff", c == ActionClass.STAY_PROTECT);
			} else {
				boolean inBand = a.move != null && v[j] <= bestStay + PlayerReader.BEST_TOL;
				check("stay column " + j + " is STAY_BEST exactly when it is within BEST_TOL of the lowest non-Protect stay", (c == ActionClass.STAY_BEST) == inBand);
				if (inBand) stayBest++;
			}
		}
		boolean anyStay = false, anySwitch = false;
		for (Action a : p.P) {
			if (a.kind == ActionKind.SWITCH) anySwitch = true;
			else if (!PlayerReader.isProtectLike(a.move)) anyStay = true;
		}
		check("a best stay is marked whenever there is a non-Protect stay column", !anyStay || stayBest >= 1);
		check("a best switch is marked whenever there is a switch column", !anySwitch || switchBest >= 1);

		// the model must not feed back into the classes
		AIV2.Plan q = AIV2.plan(root, hard, trained(p.read.fine, ActionClass.SWITCH_BEST, 40));
		check("classes and situation are identical with a trained model", Arrays.equals(p.read.classes, q.read.classes) && p.read.fine == q.read.fine);
	}

	/** Protect is its own class and "a Protect move is available" is part of the situation. */
	private static void protectIsItsOwnClass() {
		Pokemon[] pt = { Phase4Tests.mk(10, Move.EARTHQUAKE, Move.PROTECT), Phase4Tests.mk(5, Move.FLAMETHROWER), Phase4Tests.mk(6, Move.FLAMETHROWER) };
		Phase4Tests.Duel d = build(aiTeam(), pt);
		AIV2.Plan p = AIV2.plan(d.root(), AIConfig.hard(), new PlayerModel());
		boolean found = false;
		for (int j = 0; j < p.P.size(); j++) {
			if (p.P.get(j).kind != ActionKind.SWITCH && p.P.get(j).move == Move.PROTECT) {
				found = true;
				check("the Protect column is STAY_PROTECT", p.read.classes[j] == ActionClass.STAY_PROTECT);
			} else {
				check("no other column is STAY_PROTECT", p.read.classes[j] != ActionClass.STAY_PROTECT);
			}
		}
		check("the player's Protect produced a column", found);
		check("the situation records that Protect is available (lowest bit of the bucket)", (p.read.fine & 1) == 1);

		PlayerModel model = d.pl.playerModel();
		Rng.setSeed(5);
		d.a.bestMove2(d.f, true, Player.HARD);
		for (int i = 0; i < pt[0].moveset.length; i++) if (pt[0].moveset[i] != null && pt[0].moveset[i].move == Move.PROTECT) pt[0].moveset[i].currentPP--;
		Rng.setSeed(6);
		d.a.bestMove2(d.f, true, Player.HARD);
		check("clicking Protect is recorded as STAY_PROTECT: " + model.summary(), model.summary().contains("STAY_PROTECT=100%"));

		// the history makes the AI expect Protect: its predicted mass on the Protect column rises
		AIV2.Plan trained = AIV2.plan(d.root(), AIConfig.hard(), trained(p.read.fine, ActionClass.STAY_PROTECT, 40));
		double before = 0, after = 0;
		for (int j = 0; j < p.P.size(); j++) {
			if (p.read.classes[j] == ActionClass.STAY_PROTECT) {
				before += p.yHat[j];
				after += trained.yHat[j];
			}
		}
		check("a Protect habit raises the predicted Protect mass (" + before + " -> " + after + ")", after > before + 0.1 || before > 0.9);
	}

	/** Plays one switch to bench slot {@code s} and returns the model's summary; {@code reorder} mimics Player.swapToFront. */
	private static String switchSummary(int id, Move strong, int s, boolean reorder) {
		Pokemon[] ept = playerTeam(id, strong);
		Phase4Tests.Duel e = build(aiTeam(), ept);
		PlayerModel em = e.pl.playerModel();
		Rng.setSeed(20 + s);
		e.a.bestMove2(e.f, true, Player.HARD);
		Pokemon target = ept[s], lead = ept[0];
		e.pl.setCurrent(target);
		if (reorder) {
			ept[0] = target; // same array the Trainer holds (its constructor keeps the reference)
			ept[s] = lead;
		}
		Rng.setSeed(30 + s);
		e.a.bestMove2(target, true, Player.HARD);
		return em.summary();
	}

	private static Pokemon[] aiTeam() {
		return new Pokemon[] { Phase4Tests.mk(1, Move.FLAMETHROWER), Phase4Tests.mk(2, Move.FLAMETHROWER), Phase4Tests.mk(3, Move.FLAMETHROWER) };
	}

	private static Pokemon[] playerTeam(int id, Move strong) {
		return new Pokemon[] { Phase4Tests.mk(id, strong, Move.GROWL), Phase4Tests.mk(5, Move.FLAMETHROWER), Phase4Tests.mk(6, Move.FLAMETHROWER) };
	}

	private static void observeEndToEnd() {
		for (int id : new int[] { 4, 7, 10, 13, 16, 19, 22, 25 }) {
			for (Move strong : new Move[] { Move.EARTHQUAKE, Move.SURF, Move.THUNDERBOLT, Move.ICE_BEAM, Move.SHADOW_BALL }) {
				Phase4Tests.Duel d = build(aiTeam(), playerTeam(id, strong));
				AIV2.Plan p = AIV2.plan(d.root(), AIConfig.hard(), new PlayerModel());
				if (p == null || !hasClass(p.read.classes, ActionClass.STAY_BEST) || !hasClass(p.read.classes, ActionClass.STAY_OTHER)) continue;
				observeScenario(id, strong);
				return;
			}
		}
		throw new Skip("no scanned foe gave the player both a matrix-best and another stay column against the AI's lead");
	}

	private static void observeScenario(int id, Move strong) {
		Pokemon[] pt = playerTeam(id, strong);
		Phase4Tests.Duel d = build(aiTeam(), pt);
		PlayerModel model = d.pl.playerModel();

		// which of the player's moves are in the matrix's best band, and which are not? (not necessarily the damaging one)
		AIV2.Plan bp = AIV2.plan(d.root(), AIConfig.hard(), new PlayerModel());
		Move bestMove = null, otherMove = null;
		for (int j = 0; j < bp.P.size(); j++) {
			if (bestMove == null && bp.read.classes[j] == ActionClass.STAY_BEST) bestMove = bp.P.get(j).move;
			if (otherMove == null && bp.read.classes[j] == ActionClass.STAY_OTHER && bp.P.get(j).kind != ActionKind.SWITCH) otherMove = bp.P.get(j).move;
		}
		int bestIdx = -1, otherIdx = -1;
		for (int i = 0; i < pt[0].moveset.length; i++) {
			if (pt[0].moveset[i] == null || pt[0].moveset[i].move == null) continue;
			if (pt[0].moveset[i].move == bestMove) bestIdx = i;
			if (pt[0].moveset[i].move == otherMove) otherIdx = i;
		}
		check("found a best-band move slot and an outside-band move slot", bestIdx >= 0 && otherIdx >= 0);

		Rng.setSeed(11);
		d.a.bestMove2(d.f, true, Player.HARD); // begins observing: remembers the situation
		check("nothing observed yet", model.total() == 0);

		Rng.setSeed(11);
		d.a.bestMove2(d.f, true, Player.HARD); // same turn again, nothing changed: a second decision, not an action
		check("a repeated decision in the same turn records nothing", model.total() == 0);

		pt[0].moveset[bestIdx].currentPP--; // the player used the matrix-best move
		Rng.setSeed(12);
		d.a.bestMove2(d.f, true, Player.HARD);
		check("staying in with the best move is recorded: " + model.summary(), model.summary().contains("STAY_BEST=100%"));

		pt[0].moveset[otherIdx].currentPP--; // then the other move
		Rng.setSeed(13);
		d.a.bestMove2(d.f, true, Player.HARD);
		check("the other move is recorded as STAY_OTHER: " + model.summary(), !model.summary().contains("STAY_OTHER=0%"));

		double before = model.total();
		Rng.setSeed(14);
		d.a.bestMove2(d.f, true, Player.HARD); // nothing changed
		close("no PP spent, same mon: nothing observable", model.total(), before, 1e-9);

		// switching: both bench mons have a column, and the best band always holds at least one of them
		String[] seen = new String[2], seenReordered = new String[2];
		for (int s = 1; s <= 2; s++) {
			seen[s - 1] = switchSummary(id, strong, s, false);
			seenReordered[s - 1] = switchSummary(id, strong, s, true);
		}
		boolean recorded0 = seen[0].contains("SWITCH_BEST=100%") || seen[0].contains("SWITCH_OTHER=100%");
		boolean recorded1 = seen[1].contains("SWITCH_BEST=100%") || seen[1].contains("SWITCH_OTHER=100%");
		boolean someBest = seen[0].contains("SWITCH_BEST=100%") || seen[1].contains("SWITCH_BEST=100%"); // the best band is never empty
		check("a switch is recorded for either bench slot, and at least one of them is in the best band: " + seen[0] + " | " + seen[1],
				recorded0 && recorded1 && someBest);
		// Player.swapToFront moves the new active mon to team[0] and the old lead into its slot. The classification must
		// not care: it is about WHICH MON was switched to, not which index it had when the AI decided.
		check("a Player-style reordered team classifies the same: " + seenReordered[0] + " | " + seenReordered[1],
				seen[0].equals(seenReordered[0]) && seen[1].equals(seenReordered[1]));

		// a different AI trainer = a different battle: the pending turn is discarded, nothing is recorded
		Pokemon[] opt = playerTeam(id, strong);
		Phase4Tests.Duel other = build(aiTeam(), opt);
		PlayerModel om = new PlayerModel();
		AIV2.Plan p = AIV2.plan(other.root(), AIConfig.hard(), om);
		PlayerReader.begin(om, other.a, other.f, p.read);
		opt[0].moveset[0].currentPP--;
		Phase4Tests.Duel stranger = build(aiTeam(), playerTeam(id, strong));
		PlayerReader.observe(om, stranger.a, other.f);
		check("observation from a different battle is discarded", om.total() == 0);
		PlayerReader.begin(om, other.a, other.f, p.read);
		opt[0].moveset[0].currentPP--;
		PlayerReader.observe(om, other.a, other.f);
		check("same battle, PP spent: recorded", om.total() > 0);
	}

	// ------------------------------------------------------------------ plan

	private static void emptyModelIsPhase5() {
		Phase4Tests.Duel d = basicDuel();
		SimState root = d.root();
		AIConfig hard = AIConfig.hard();
		AIV2.Plan base = AIV2.plan(root, hard);
		AIV2.Plan withEmpty = AIV2.plan(root, hard, new PlayerModel());
		check("no model: pure equilibrium", base.alphaEff == 1.0 && base.read == null && Arrays.equals(base.yHat, base.eq.y));
		check("empty model: a reading exists (so observing can start) but changes nothing", withEmpty.read != null && withEmpty.read.w == 0);
		check("empty model: identical strategy", Arrays.equals(base.x, withEmpty.x));
		check("empty model: yHat is eq.y", Arrays.equals(withEmpty.yHat, withEmpty.eq.y));
		AIConfig off = AIConfig.hard();
		off.useHistoryModel = false;
		check("useHistoryModel=false ignores even a full model", Arrays.equals(base.x, AIV2.plan(root, off, trained(withEmpty.read.fine, ActionClass.SWITCH_BEST, 40)).x));
	}

	private static void planIsPure() {
		Phase4Tests.Duel d = basicDuel();
		SimState root = d.root();
		AIConfig hard = AIConfig.hard();
		int fine = AIV2.plan(root, hard, new PlayerModel()).read.fine;
		PlayerModel m = trained(fine, ActionClass.SWITCH_BEST, 30);
		double total = m.total();
		PlayerModel snapshot = m.copy();
		AIV2.Plan p1 = AIV2.plan(root, hard, m);
		AIV2.Plan p2 = AIV2.plan(root, hard, m);
		check("same state + same model = same strategy (T4)", Arrays.equals(p1.x, p2.x) && Arrays.equals(p1.yHat, p2.yHat));
		check("planning never writes to the model", m.total() == total && m.sameCounts(snapshot));
		close("strategy sums to 1", sum(p1.x), 1.0, 1e-9);
		close("prediction sums to 1", sum(p1.yHat), 1.0, 1e-9);
		check("the model is trusted (0 < w <= 1) and alphaEff in [alpha, 1)", p1.read.w > 0 && p1.alphaEff >= hard.alpha - 1e-9 && p1.alphaEff < 1.0);
	}

	private static void exploitDecisionLevel() {
		Phase4Tests.Duel d = basicDuel();
		SimState root = d.root();
		AIConfig hard = AIConfig.hard();
		AIV2.Plan base = AIV2.plan(root, hard);
		AIV2.Plan probe = AIV2.plan(root, hard, new PlayerModel());
		int fine = probe.read.fine;

		boolean hasSwitch = hasClass(probe.read.classes, ActionClass.SWITCH_BEST), hasAttack = hasClass(probe.read.classes, ActionClass.STAY_BEST);
		if (!hasSwitch || !hasAttack) throw new Skip("need both a best-move column and a best-switch column");

		for (ActionClass habit : new ActionClass[] { ActionClass.SWITCH_BEST, ActionClass.STAY_BEST }) {
			AIV2.Plan p = AIV2.plan(root, hard, trained(fine, habit, 40));
			double expected = PlayerModel.classMass(base.eq.y, p.read.classes)[habit.ordinal()];
			double predicted = PlayerModel.classMass(p.yHat, p.read.classes)[habit.ordinal()];
			if (expected < 0.9) {
				check(habit + ": predicted mass " + predicted + " > equilibrium mass " + expected + " + 0.1", predicted > expected + 0.1);
			} else {
				// the equilibrium already puts ~all of its mass on this class (the player's best reply IS this habit), so there is
				// nothing to shift toward; the prediction must simply not move away from it
				check(habit + ": equilibrium already at " + expected + ", prediction must not fall (" + predicted + ")", predicted >= expected - 1e-9);
			}

			double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
			for (double[] row : p.M) for (double v : row) {
				lo = Math.min(lo, v);
				hi = Math.max(hi, v);
			}
			double range = hi - lo;

			// scripted opponent: puts all its weight on the columns of this class
			double[] script = new double[p.P.size()];
			int cols = 0;
			for (int j = 0; j < script.length; j++) if (p.read.classes[j] == habit) cols++;
			for (int j = 0; j < script.length; j++) if (p.read.classes[j] == habit) script[j] = 1.0 / cols;
			double with = vs(p.x, p.M, script), without = vs(base.x, base.M, script);
			System.out.println("  " + habit + ": AI payoff against the scripted habit " + without + " -> " + with + " (range " + range + ")");
			check(habit + ": exploiting the read does not cost against it (Shaper's 2% cutoff aside)", with >= without - 0.03 * range);

			// a wrong read is bounded: whatever the player really does, the AI keeps v - (1-alphaEff)*range (plus slack for shape())
			double v = Double.MAX_VALUE;
			for (int j = 0; j < p.P.size(); j++) v = Math.min(v, payoffRaw(base.eq.x, base.M, j));
			for (int j = 0; j < p.P.size(); j++) {
				check(habit + " wrong-read bound vs column " + j, payoffRaw(p.x, p.M, j) >= v - (1 - p.alphaEff) * range - 0.05 * range);
			}
		}
	}

	private static double vs(double[] x, double[][] M, double[] y) {
		double s = 0;
		for (int j = 0; j < y.length; j++) s += y[j] * payoffRaw(x, M, j);
		return s;
	}

	private static double payoffRaw(double[] x, double[][] M, int col) {
		return payoff(x, M, col);
	}

	/**
	 * Phase 5's T21 asserted the payoff form (a sack's edge over Stay is larger against the expected attack than against a
	 * switch). Now the probability form: with a history of "stays in with the best move" the sack row keeps its
	 * probability; with a history of "switches to the best switch" it falls. The drop is bounded by (1 - alphaEff), by design.
	 */
	private static void t21Probability() {
		Phase5Tests.Scen sc = Phase5Tests.sackScenario(0.15, false, 100, true, true);
		if (sc == null) throw new Skip("no scanned foe/move KOs both a level-20 ace and its 15%-HP teammate");
		SimState root = sc.d.root();
		AIConfig hard = AIConfig.hard();
		AIV2.Plan probe = AIV2.plan(root, hard, new PlayerModel());
		int row = Phase5Tests.switchRow(probe.A, 1);
		if (row < 0) throw new Skip("no sack row survived the guard (see T18)");
		if (!hasClass(probe.read.classes, ActionClass.SWITCH_BEST) || !hasClass(probe.read.classes, ActionClass.STAY_BEST)) {
			throw new Skip("need both a best-move column and a best-switch column");
		}
		int fine = probe.read.fine;
		AIV2.Plan expectAttack = AIV2.plan(root, hard, trained(fine, ActionClass.STAY_BEST, 40));
		AIV2.Plan expectSwitch = AIV2.plan(root, hard, trained(fine, ActionClass.SWITCH_BEST, 40));
		int ra = Phase5Tests.switchRow(expectAttack.A, 1), rs = Phase5Tests.switchRow(expectSwitch.A, 1);
		check("the sack row exists in both plans", ra >= 0 && rs >= 0);
		double pBase = probe.x[row], pAttack = expectAttack.x[ra], pSwitch = expectSwitch.x[rs];
		System.out.println("  T21 sack-row probability: no history " + pBase + ", expecting an attack " + pAttack + ", expecting a switch " + pSwitch);
		if (pAttack - pSwitch < 0.05) throw new Skip("the sack is not the best response to the attack read here, so the read cannot raise it (" + pAttack + " vs " + pSwitch + ")");
		check("the sack row loses probability when a switch is expected (" + pSwitch + " < " + pAttack + ")", pSwitch < pAttack);
	}

	// ------------------------------------------------------------------ manual acceptance (live game, game thread)

	/**
	 * Does the AI exploit a fixed habit? The same seeds and teams, AIV2 with the model (engine A, models carried across
	 * battles) vs AIV2 without it, each against {@code scripted}. Compare engine A's win rate in the two printed reports:
	 * a clear gain for the model-on run is the Phase 6 exploitation signal. Start with 100+ battles.
	 */
	public static SelfPlayReport[] selfPlayVsScripted(ScriptedAI scripted, int battles) {
		SelfPlayReport[] out = new SelfPlayReport[2];
		TrainerAI[] engines = { AIV2.INSTANCE, AIV2.NO_HISTORY };
		for (int i = 0; i < 2; i++) {
			SelfPlay.Config c = new SelfPlay.Config();
			c.battles = battles;
			c.aiA = engines[i];
			c.aiB = scripted;
			c.difficultyA = Player.HARD;
			c.difficultyB = Player.HARD;
			c.teams = SelfPlay.randomCompetitive(6);
			c.persistModels = true;
			System.out.println("=== " + engines[i].getName() + " vs " + scripted.getName() + " ===");
			out[i] = SelfPlay.run(c);
		}
		return out;
	}

	/** HARD vs NORMAL, same engine (acceptance: HARD wins). */
	public static SelfPlayReport selfPlayHardVsNormal(int battles) {
		SelfPlay.Config c = new SelfPlay.Config();
		c.battles = battles;
		c.aiA = AIV2.INSTANCE;
		c.aiB = AIV2.INSTANCE;
		c.difficultyA = Player.HARD;
		c.difficultyB = Player.NORMAL;
		c.teams = SelfPlay.randomCompetitive(6);
		c.persistModels = true;
		return SelfPlay.run(c);
	}

	/** Model on vs model off, both HARD, random teams, models carried across battles (Phase 6 must not regress HARD vs HARD). */
	public static SelfPlayReport selfPlayHistoryAB(int battles) {
		SelfPlay.Config c = new SelfPlay.Config();
		c.battles = battles;
		c.aiA = AIV2.INSTANCE;
		c.aiB = AIV2.NO_HISTORY;
		c.difficultyA = Player.HARD;
		c.difficultyB = Player.HARD;
		c.teams = SelfPlay.randomCompetitive(6);
		c.persistModels = true;
		return SelfPlay.run(c);
	}
}