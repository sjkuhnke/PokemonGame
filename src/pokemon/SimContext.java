package pokemon;

import util.Rng;

/**
 * Simulation scope (spec 7.1). While any scope is open, battle code that would touch the outside world must not:
 * <ul>
 * <li>{@code Task} creates nothing and touches no UI task list;</li>
 * <li>player recorders, pokedex / save-scum hooks, shared field counters and sim-UI hooks stay untouched;</li>
 * <li>randomness is resolved by the active {@link SimPolicy}; every other draw comes from an isolated stream
 * (see {@link Rng#pushIsolated(long)}), so simulating never consumes the real battle's stream.</li>
 * </ul>
 * Scopes nest (a depth counter). The outermost scope seeds the isolated stream, so the same state and policy give the
 * same result every time. Game thread only, like the rest of the battle code.
 *
 * <pre>
 * try (SimContext.Scope s = SimContext.enter(policy)) {
 *     mon.move(foe, move, true, false);
 * }
 * </pre>
 */
public final class SimContext {
	private static int depth;
	private static SimPolicy policy = SimPolicy.DEFAULT;
	private static Integer forcedSwitchIndex;

	private SimContext() {}
	
	/**
	 * Phase 2 (spec 7.7): steer Trainer.swapRandom's next pick to this team index instead of
	 * drawing from Rng. Used to fan a random-target move (Whirlwind, Roar, Dragon Tail, Circle
	 * Throw, Red Card) out into one branch per eligible candidate. Consumed (cleared) by the
	 * first swapRandom call that runs after this is set, whether or not it ends up using it.
	 * Game thread only.
	 */
	public static void forceNextSwitch(int teamIndex) {
		forcedSwitchIndex = teamIndex;
	}

	/** Consumes and returns the forced index, or null if none is set. */
	static Integer consumeForcedSwitch() {
		Integer i = forcedSwitchIndex;
		forcedSwitchIndex = null;
		return i;
	}

	public static boolean active() {
		return depth > 0;
	}

	public static int depth() {
		return depth;
	}

	/** The active policy, or {@link SimPolicy#DEFAULT} when no scope is open. */
	public static SimPolicy policy() {
		return policy;
	}

	public static Scope enter() {
		return enter(SimPolicy.DEFAULT);
	}

	public static Scope enter(SimPolicy p) {
		if (p == null) throw new IllegalArgumentException("policy is null");
		Scope s = new Scope(policy, depth);
		if (depth == 0) Rng.pushIsolated(p.seed);
		depth++;
		policy = p;
		return s;
	}
	
	public static void run(SimPolicy p, Runnable r) {
		try (Scope s = enter(p)) {
			r.run();
		}
	}

	/** Closes the scope it opened. Idempotent; must be closed innermost-first. */
	public static final class Scope implements AutoCloseable {
		private final SimPolicy previous;
		private final int depthBefore;
		private boolean closed;

		private Scope(SimPolicy previous, int depthBefore) {
			this.previous = previous;
			this.depthBefore = depthBefore;
		}

		@Override
		public void close() {
			if (closed) return;
			if (depth != depthBefore + 1) {
				throw new IllegalStateException("SimContext scopes closed out of order (depth " + depth + ", expected " + (depthBefore + 1) + ")");
			}
			closed = true;
			depth = depthBefore;
			policy = previous;
			if (depth == 0) Rng.popIsolated();
		}
	}
}
