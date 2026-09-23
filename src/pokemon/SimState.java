package pokemon;

import java.util.HashSet;
import java.util.Set;

/** §6/§7.2. A start-of-turn (or start-of-next-turn) battle state, safe to mutate. */
public class SimState {
	public Field field;
	public SideState ai;
	public SideState player;
	public int turn;

	public SimState(Field field, SideState ai, SideState player, int turn) {
		this.field = field;
		this.ai = ai;
		this.player = player;
		this.turn = turn;
	}

	/**
	 * One clone pass per decision (§7.2). self/foe must be the REAL, currently-active
	 * Pokemon. Leaves every real object (teams, field, trainer.current) untouched — see
	 * Phase2Tests.fingerprintReal / T3.
	 */
	public static SimState snapshot(Pokemon self, Pokemon foe) {
		Field fieldClone = Pokemon.field.clone();
		SideState ai = new SideState(self.trainer.simShell());
		SideState player = new SideState(foe.trainer.simShell());
		return new SimState(fieldClone, ai, player, Pokemon.field.turns);
	}

	/**
	 * Per-cell copy-on-write fork (§7.2). touchedAiSlots/touchedPlayerSlots are 0-based
	 * team indices that this cell's action(s) name explicitly (e.g. a SWITCH target); the
	 * active slot on each side is always included even if not listed. field is always
	 * cloned (cheap; many moves/abilities mutate it). Untouched bench slots keep the SAME
	 * Pokemon reference as `this` and must not be mutated by the caller.
	 */
	public SimState fork(Set<Integer> touchedAiSlots, Set<Integer> touchedPlayerSlots) {
		Field fieldClone = this.field.clone();
		SideState aiFork = this.ai.forkTouched(touchedAiSlots);
		SideState playerFork = this.player.forkTouched(touchedPlayerSlots);
		return new SimState(fieldClone, aiFork, playerFork, this.turn);
	}

	/** Convenience: fork with no extra touched slots beyond the two actives. */
	public SimState fork() {
		return fork(new HashSet<>(), new HashSet<>());
	}

	/**
	 * Cheap, deterministic hash over everything that matters for "is this state
	 * meaningfully different" (mergeSimilar) and for T3 invariance checks when pointed at
	 * real, non-shell state. Order-independent per side would be nicer, but team[] order
	 * is stable within one battle, so a straight fold is fine.
	 */
	public long fingerprint() {
		long h = 1125899906842597L; // arbitrary odd seed
		h = fold(h, field.turns);
		h = fold(h, field.weather == null ? -1 : field.weather.effect.ordinal());
		h = fold(h, field.weatherTurns);
		h = fold(h, field.terrain == null ? -1 : field.terrain.effect.ordinal());
		h = fold(h, field.terrainTurns);
		for (Field.FieldEffect fe : field.fieldEffects) h = fold(h, fe.effect.ordinal() * 1000 + fe.turns);
		h = foldSide(h, ai);
		h = foldSide(h, player);
		return h;
	}

	private static long foldSide(long h, SideState side) {
		h = fold(h, side.shell.indexOf(side.shell.current));
		for (Field.FieldEffect fe : side.shell.getFieldEffectList()) h = fold(h, fe.effect.ordinal() * 1000 + fe.turns + fe.layers);
		for (Pokemon p : side.shell.team) {
			if (p == null) continue;
			h = fold(h, p.id);
			h = fold(h, p.currentHP);
			h = fold(h, p.fainted ? 1 : 0);
			h = fold(h, p.status == Status.HEALTHY ? -1 : p.status.ordinal());
			for (int s : p.statStages) h = fold(h, s);
			h = fold(h, p.perishCount);
		}
		return h;
	}

	private static long fold(long h, long v) {
		h ^= v + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2);
		return h;
	}
}