package ai;

import pokemon.Move;
import util.Rng;

public final class SimContext {
	private static final class State {
		int depth;
		SimPolicy policy = SimPolicy.DEFAULT;
		Integer forcedSwitchIndex;
		Move forcedMetronome;
		final java.util.ArrayDeque<Scope> open = new java.util.ArrayDeque<>();
	}
	private static final ThreadLocal<State> TL = ThreadLocal.withInitial(State::new);

	private SimContext() {}

	public static void forceNextSwitch(int i) { TL.get().forcedSwitchIndex = i; }
	public static Integer consumeForcedSwitch() { State s = TL.get(); Integer i = s.forcedSwitchIndex; s.forcedSwitchIndex = null; return i; }
	public static void forceNextMetronome(Move m) { TL.get().forcedMetronome = m; }
	public static Move consumeForcedMetronome() { State s = TL.get(); Move m = s.forcedMetronome; s.forcedMetronome = null; return m; }

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
		st.open.clear(); st.depth = 0; st.policy = SimPolicy.DEFAULT;
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