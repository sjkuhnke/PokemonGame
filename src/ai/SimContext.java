package ai;

import pokemon.Field;
import pokemon.Move;
import pokemon.Pokemon;
import util.Rng;

public final class SimContext {
	private static final class State {
		int depth;
		SimPolicy policy = SimPolicy.DEFAULT;
		Integer forcedSwitchIndex;
		Move forcedMetronome;
		Decision decision;
		final java.util.ArrayDeque<Scope> open = new java.util.ArrayDeque<>();
	}
	private static final ThreadLocal<State> TL = ThreadLocal.withInitial(State::new);

	private SimContext() {}

	public static void forceNextSwitch(int i) { TL.get().forcedSwitchIndex = i; }
	public static Integer consumeForcedSwitch() { State s = TL.get(); Integer i = s.forcedSwitchIndex; s.forcedSwitchIndex = null; return i; }
	public static void forceNextMetronome(Move m) { TL.get().forcedMetronome = m; }
	public static Move consumeForcedMetronome() { State s = TL.get(); Move m = s.forcedMetronome; s.forcedMetronome = null; return m; }

	/**
	 * The decision being simulated: its root state and the {@link MonWeights} computed from it. Set by
	 * {@link MatrixBuilder#buildMatrix} for the duration of a matrix build (and restored after), so code that runs inside a
	 * cell, such as {@link ReplacementChooser}, reads the SAME weights the cell's {@code Evaluator.eval} uses instead of
	 * recomputing them. Weights are HP-independent and slot-indexed, so they stay valid across every fork of the root.
	 * Null outside a matrix build, in particular in the real battle.
	 */
	public static final class Decision {
		public final SimState root;
		/** AI-perspective weights (and edge table), as the matrix uses them. */
		public final MonWeights weights;
		private MonWeights swapped;

		Decision(SimState root, MonWeights weights) {
			this.root = root;
			this.weights = weights;
		}

		boolean swappedReady() { return swapped != null; }

		/** The same computation with the two sides' roles swapped (the player as "ai"), computed once per decision on first use. */
		MonWeights swappedWeights() {
			if (swapped == null) {
				Field prev = Pokemon.field;
				try {
					Pokemon.field = root.field;
					swapped = MonWeights.compute(new SimState(root.field, root.player, root.ai, root.turn));
				} finally {
					Pokemon.field = prev;
				}
			}
			return swapped;
		}
	}

	/** Installs {@code d} (null clears) and returns the previous one, to be restored in a {@code finally}. */
	public static Decision setDecision(Decision d) { State st = TL.get(); Decision prev = st.decision; st.decision = d; return prev; }
	public static Decision decision() { return TL.get().decision; }

	public static boolean active() { return TL.get().depth > 0; }
	public static int depth() { return TL.get().depth; }
	public static SimPolicy policy() { return TL.get().policy; }

	public static Scope enter() { return enter(SimPolicy.DEFAULT); }

	public static Scope enter(SimPolicy p) {
		if (p == null) throw new IllegalArgumentException("policy is null");
		State st = TL.get();
		Scope s = new Scope(st, st.policy, st.depth);
		if (st.depth == 0) Rng.pushIsolated(p.seed);
		st.depth++;
		st.policy = p;
		st.open.push(s);
		return s;
	}

	public static void run(SimPolicy p, Runnable r) { try (Scope s = enter(p)) { r.run(); } }

	public static void resetForFrame() { // this thread only
		State st = TL.get();
		boolean wasActive = st.depth > 0;
		st.open.clear(); st.depth = 0; st.policy = SimPolicy.DEFAULT; st.decision = null;
		if (wasActive) Rng.popIsolated();
	}

	public static final class Scope implements AutoCloseable {
		private final State st;
		private final SimPolicy previous;
		private final int depthBefore;
		private final Throwable openedAt = new Throwable("scope opened on " + Thread.currentThread().getName());
		private boolean closed;

		private Scope(State st, SimPolicy previous, int depthBefore) {
			this.st = st; this.previous = previous; this.depthBefore = depthBefore;
		}

		@Override
		public void close() {
			if (closed) return;
			if (TL.get() != st) throw new IllegalStateException("Scope closed on a different thread than it was opened on", openedAt);
			closed = true;
			boolean ok = st.depth == depthBefore + 1;
			while (!st.open.isEmpty() && st.open.peek() != this) st.open.pop();
			if (!st.open.isEmpty()) st.open.pop();
			st.depth = depthBefore;
			st.policy = previous;
			if (st.depth == 0) Rng.popIsolated();
			if (!ok) throw new IllegalStateException("SimContext scopes closed out of order", openedAt);
		}
	}
}