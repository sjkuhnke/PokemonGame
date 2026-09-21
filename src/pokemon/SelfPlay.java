package pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import overworld.GamePanel;
import ui.BattleUI;
import util.Print;
import util.Rng;

/**
 * Phase 0 self-play runner: plays K headless Trainer-vs-Trainer battles between two TrainerAI engines
 * and reports win rate, turns, think time, crashes and (optionally) seed reproducibility.
 * <p>
 * The turn loop deliberately mirrors {@code Pokemon.simulateBattle} (the betting sim), which resolves
 * turns in TASK_STATE with tasks going to {@code gp.ui.tasks}. Run it on the game thread with no battle
 * on screen, e.g. from a debug key. It blocks until finished (or {@code maxSeconds}), so expect the game
 * to freeze while it runs. Legacy think time is ~1s/decision, so start with a small battle count.
 */
public final class SelfPlay {
	private SelfPlay() {}

	// ------------------------------------------------------------------ config

	public interface TeamSource {
		/** One matchup: [0] plays seat 1, [1] plays seat 2. Called with Rng already seeded for this pair. */
		Trainer[] next(int pairIndex, Random sched);
		String describe();
	}

	public static final class Config {
		public int battles = 200;
		public long baseSeed = 20260921L;
		public int maxTurns = 300;
		public double maxSeconds = 0;          // 0 = no limit
		public TrainerAI aiA = LegacyAI.INSTANCE;
		public TrainerAI aiB = LegacyAI.INSTANCE;
		public int difficultyA = Player.HARD;
		public int difficultyB = Player.HARD;
		public TeamSource teams;               // null = storyTrainers()
		public boolean swapSeats = true;       // each matchup is played twice, engines swapped, to cancel team strength
		public int determinismSample = 3;      // replay this many of the first battles and compare; 0 = off
		public int progressEvery = 10;
		public boolean aiTrace = false;        // true = keep Print.debug output (slow, huge log)
	}

	public static final class BattleResult {
		public int winner = -1;                // 0 = seat 1, 1 = seat 2, -1 = draw / timeout
		public boolean timedOut;
		public int turns;
		public long nanos1, nanos2, maxNanos1, maxNanos2;
		public int decisions1, decisions2;
		public long checksum;
		public Throwable error;
	}

	// ------------------------------------------------------------------ team sources

	/** Real trainers from Trainer.trainers (skips script/scripted-encounter trainers). */
	public static TeamSource storyTrainers() {
		return new TeamSource() {
			private List<Trainer> pool;

			@Override
			public Trainer[] next(int pairIndex, Random sched) {
				if (pool == null) pool = buildPool();
				if (pool.size() < 2) throw new IllegalStateException("Need at least 2 usable trainers, found " + pool.size());
				int a = sched.nextInt(pool.size());
				int b = sched.nextInt(pool.size() - 1);
				if (b >= a) b++;
				return new Trainer[] { pool.get(a), pool.get(b) };
			}

			@Override
			public String describe() {
				return "story trainers";
			}
		};
	}

	static List<Trainer> buildPool() {
		List<Trainer> pool = new ArrayList<>();
		for (Trainer t : Trainer.trainers) {
			if (t == null || t.team == null || t.team.length == 0 || t.current == null) continue;
			if (t.current.script) continue; // scripted encounters behave differently (forced hits/misses)
			pool.add(t);
		}
		return pool;
	}

	/** Random competitive teams, same generator the betting sim uses. */
	public static TeamSource randomCompetitive(final int teamSize) {
		return new TeamSource() {
			@Override
			public Trainer[] next(int pairIndex, Random sched) {
				return new Trainer[] { make("Trainer A", 1), make("Trainer B", 2) };
			}

			private Trainer make(String name, int index) {
				ArrayList<Pokemon> team = new ArrayList<>();
				ArrayList<Item> items = new ArrayList<>();
				for (int i = 0; i < teamSize; i++) {
					Pokemon p = Pokemon.generateCompetitivePokemon(team);
					team.add(p);
					items.add(p.item);
				}
				return new Trainer(name, team.toArray(new Pokemon[0]), items.toArray(new Item[0]), 0, index);
			}

			@Override
			public String describe() {
				return "random competitive x" + teamSize;
			}
		};
	}

	// ------------------------------------------------------------------ tournament

	private static final class Replay {
		Trainer x, y;
		TrainerAI ai1, ai2;
		int d1, d2;
		long seed;
		BattleResult result;
	}

	public static SelfPlayReport run(Config cfg) {
		if (Pokemon.gp == null) throw new IllegalStateException("Pokemon.gp is null: run inside the game (game thread), not standalone");

		SelfPlayReport rep = new SelfPlayReport(cfg.aiA.getName(), cfg.aiB.getName() + (cfg.aiA.getName().equals(cfg.aiB.getName()) ? " (2)" : ""));
		final boolean prevSuppressed = Print.isDebugSuppressed();
		final boolean wasSeeded = Rng.isSeeded();
		final long prevSeed = Rng.getSeed();
		Print.setDebugSuppressed(!cfg.aiTrace);

		TeamSource source = cfg.teams != null ? cfg.teams : storyTrainers();
		Random sched = new Random(cfg.baseSeed); // matchmaking only; never used by battle/AI code
		List<Replay> replays = new ArrayList<>();
		long start = System.nanoTime();

		try {
			int battle = 0, pair = 0;
			outer:
			while (battle < cfg.battles) {
				Rng.setSeed(cfg.baseSeed + 1_000_003L * pair); // team generation is reproducible too
				Trainer[] m = source.next(pair, sched);
				int seatings = cfg.swapSeats ? 2 : 1;
				for (int s = 0; s < seatings && battle < cfg.battles; s++) {
					if (cfg.maxSeconds > 0 && (System.nanoTime() - start) / 1e9 > cfg.maxSeconds) {
						rep.stoppedEarly = true;
						break outer;
					}
					boolean swapped = s == 1; // swapped: engine B sits in seat 1, engine A in seat 2
					TrainerAI ai1 = swapped ? cfg.aiB : cfg.aiA;
					TrainerAI ai2 = swapped ? cfg.aiA : cfg.aiB;
					int d1 = swapped ? cfg.difficultyB : cfg.difficultyA;
					int d2 = swapped ? cfg.difficultyA : cfg.difficultyB;
					long seed = cfg.baseSeed + 7919L * battle;

					BattleResult r = playBattle(m[0], m[1], ai1, ai2, d1, d2, seed, cfg.maxTurns);

					if (r.error != null) {
						rep.addCrash("battle " + battle + " (seed " + seed + "): " + describe(r.error));
					} else {
						int winnerEngine = r.winner < 0 ? -1 : ((r.winner == 0) != swapped ? 0 : 1);
						long nA = swapped ? r.nanos2 : r.nanos1, nB = swapped ? r.nanos1 : r.nanos2;
						int dA = swapped ? r.decisions2 : r.decisions1, dB = swapped ? r.decisions1 : r.decisions2;
						long mA = swapped ? r.maxNanos2 : r.maxNanos1, mB = swapped ? r.maxNanos1 : r.maxNanos2;
						rep.addBattle(winnerEngine, r.timedOut, r.turns, nA, dA, mA, nB, dB, mB);
					}
					if (replays.size() < cfg.determinismSample) {
						Replay rp = new Replay();
						rp.x = m[0]; rp.y = m[1]; rp.ai1 = ai1; rp.ai2 = ai2; rp.d1 = d1; rp.d2 = d2; rp.seed = seed; rp.result = r;
						replays.add(rp);
					}
					battle++;
					if (cfg.progressEvery > 0 && battle % cfg.progressEvery == 0) {
						System.out.println(rep.progressLine(battle, cfg.battles));
					}
				}
				pair++;
			}

			for (Replay rp : replays) {
				if (rp.result.error != null) continue;
				BattleResult again = playBattle(rp.x, rp.y, rp.ai1, rp.ai2, rp.d1, rp.d2, rp.seed, cfg.maxTurns);
				rep.determinismChecked++;
				if (again.error != null || again.winner != rp.result.winner || again.turns != rp.result.turns
						|| again.checksum != rp.result.checksum) {
					rep.determinismMismatches++;
				}
			}
		} finally {
			rep.wallNanos = System.nanoTime() - start;
			Print.setDebugSuppressed(prevSuppressed);
			if (wasSeeded) Rng.setSeed(prevSeed); else Rng.randomize();
		}

		System.out.println(rep);
		return rep;
	}

	private static String describe(Throwable t) {
		StackTraceElement[] st = t.getStackTrace();
		return t + (st.length > 0 ? " at " + st[0] : "");
	}

	// ------------------------------------------------------------------ one battle

	private static void ensureBoosts(Trainer t) {
		// Trainer.clone() dereferences boosts; trainers built by hand (e.g. the betting sim) may not set it.
		if (t.boosts == null) t.boosts = new int[3];
	}

	private static long mix(long h, long v) {
		h ^= v + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2);
		return h;
	}

	/**
	 * Plays one battle to the end. Mirrors Pokemon.simulateBattle's turn loop. Seats: 1 = src1, 2 = src2.
	 * Restores gameState, Pokemon.field and Pokemon.createTask afterwards. Never throws: errors go in the result.
	 */
	public static BattleResult playBattle(Trainer src1, Trainer src2, TrainerAI ai1, TrainerAI ai2,
			int diff1, int diff2, long seed, int maxTurns) {
		BattleResult r = new BattleResult();
		final GamePanel gp = Pokemon.gp;
		final int prevState = gp.gameState;
		final Field prevField = Pokemon.field;
		final boolean prevCreateTask = Pokemon.createTask;
		long h = 1469598103934665603L;

		try {
			gp.gameState = GamePanel.TASK_STATE; // Task.addTask needs a task-capable state while createTask is true
			Pokemon.createTask = true;
			Rng.setSeed(seed);

			ensureBoosts(src1);
			ensureBoosts(src2);
			Trainer t1 = src1.clone();
			Trainer t2 = src2.clone();
			t1.ai = ai1;
			t2.ai = ai2;
			Field field = new Field();
			Pokemon.field = field;
			field.clear(t1, t2);
			gp.ui.tasks.clear();

			while (!t1.wiped() && !t2.wiped()) {
				if (r.turns >= maxTurns) {
					r.timedOut = true;
					break;
				}
				r.turns++;

				Pokemon p1 = t1.getCurrent();
				Pokemon p2 = t2.getCurrent();
				boolean fFaster = p1.getFaster(p2, 0, 0, field) == p2;

				long a = System.nanoTime();
				Move uMove = p1.resolveDecision(p1.bestMove2(p2, !fFaster, diff1));
				long b = System.nanoTime();
				Move fMove = p2.resolveDecision(p2.bestMove2(p1, fFaster, diff2));
				long c = System.nanoTime();
				r.nanos1 += b - a; r.decisions1++; r.maxNanos1 = Math.max(r.maxNanos1, b - a);
				r.nanos2 += c - b; r.decisions2++; r.maxNanos2 = Math.max(r.maxNanos2, c - b);

				h = mix(h, r.turns);
				h = mix(h, uMove == null ? -1 : uMove.ordinal());
				h = mix(h, fMove == null ? -1 : fMove.ordinal());

				int uP, fP;
				uP = uMove == null ? 0 : uMove.getPriority(p1);
				fP = fMove == null ? 0 : fMove.getPriority(p2);

				if (uMove != null && fMove != null && !p1.hasStatus(Status.SWAP) && !p2.hasStatus(Status.SWAP)) {
					uP = p1.checkQuickClaw(uP);
					fP = p2.checkQuickClaw(fP);
				}
				if (uMove != null) uP = p1.checkCustap(uP, p2);
				if (fMove != null) fP = p2.checkCustap(fP, p1);

				Pokemon faster;
				Pokemon slower;

				if (uMove == null || fMove == null) {
					faster = uMove == null ? p1 : p2;
				} else {
					faster = p1.getFaster(p2, uP, fP, field);
				}

				slower = faster == p1 ? p2 : p1;

				Move fastMove = faster == p1 ? uMove : fMove;
				Move slowMove = faster == p1 ? fMove : uMove;

				boolean fastCanMove = true;
				boolean slowCanMove = true;

				int fasterSwitchSlot = faster.hasStatus(Status.SWAP) ? faster.getStatusNum(Status.SWAP) : BattleUI.FREE_SWITCH;
				int slowerSwitchSlot = slower.hasStatus(Status.SWAP) ? slower.getStatusNum(Status.SWAP) : BattleUI.FREE_SWITCH;

				if (fasterSwitchSlot > 0) {
					faster = faster.trainer.swapOut2(slower, fasterSwitchSlot, false, false);
					fastMove = null;
					fastCanMove = false;
				}

				if (slowerSwitchSlot > 0) {
					slower = slower.trainer.swapOut2(faster, slowerSwitchSlot, false, false);
					slowMove = null;
					slowCanMove = false;
				}

				if (slowMove != null && fastMove == Move.SUCKER_PUNCH && slowMove.cat == 2) fastMove = Move.FAILED_SUCKER;

				if (fastCanMove) {
					faster.moveInit(slower, fastMove, true);
					faster = faster.trainer.getCurrent();
					slower = slower.trainer.getCurrent();
				}

				if (faster.trainer.hasValidMembers(slower) && fastCanMove && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
					faster = faster.trainer.swapOut2(slower, fasterSwitchSlot, faster.lastMoveUsed == Move.BATON_PASS, false);
				}
				if (slower.trainer.hasValidMembers(faster) && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
					slower = slower.trainer.swapOut2(faster, BattleUI.FREE_SWITCH, false, false);
					slowCanMove = false;
				}

				if (slowCanMove) {
					slower.moveInit(faster, slowMove, false);
					faster = faster.trainer.getCurrent();
					slower = slower.trainer.getCurrent();
				}

				if (slower.trainer.hasValidMembers(faster) && slowCanMove && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
					slower = slower.trainer.swapOut2(faster, BattleUI.FREE_SWITCH, slower.lastMoveUsed == Move.BATON_PASS, false);
				}
				if (faster.trainer.hasValidMembers(slower) && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
					faster = faster.trainer.swapOut2(slower, BattleUI.FREE_SWITCH, false, false);
				}

				if (fastMove != null || slowMove != null) {
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) faster.endOfTurn(slower);
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) slower.endOfTurn(faster);
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) field.endOfTurn(faster, slower);
				}

				for (int j = 0; j < 2; j++) {
					Pokemon p = j == 0 ? faster : slower;
					Pokemon foe = p == faster ? slower : faster;
					Pokemon next = p.trainer.getCurrent();
					while (next.isFainted()) {
						if (next.trainer.hasNext()) {
							boolean userSide = !next.trainer.hasUser(p1);
							next = next.trainer.next(foe, userSide);
							Task.addSwapInTask(next, false);
							next.swapIn(foe, true);
						} else {
							break;
						}
					}
				}

				h = mix(h, t1.getCurrent().currentHP);
				h = mix(h, t2.getCurrent().currentHP);
				gp.ui.tasks.clear(); // battle text is not needed; keeps memory flat
			}

			if (!r.timedOut) {
				boolean w1 = t1.wiped(), w2 = t2.wiped();
				if (w2 && !w1) r.winner = 0;
				else if (w1 && !w2) r.winner = 1;
				else r.winner = -1;
			}
		} catch (Throwable e) {
			r.error = e;
		} finally {
			r.checksum = h;
			gp.ui.tasks.clear();
			Pokemon.field = prevField;
			Pokemon.createTask = prevCreateTask;
			gp.gameState = prevState;
		}
		return r;
	}
}