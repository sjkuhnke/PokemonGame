package ai;

import java.util.Set;

import pokemon.Pokemon;
import pokemon.Trainer;

/** One side's state inside a {@link SimState}: a trainer "shell" (a copy-on-write copy of the real trainer, see {@code Trainer.simShell}). */
public class SideState {
	public Trainer shell;

	public SideState(Trainer shell) {
		this.shell = shell;
	}

	/** The mon currently out. */
	public Pokemon active() {
		return shell.current;
	}

	/** The whole team array (including the active mon and fainted ones), indexed by team slot. */
	public Pokemon[] bench() {
		return shell.team;
	}

	public Pokemon slot(int i) {
		return shell.team[i];
	}

	/** Copy-on-write fork of just this side, delegating to {@code Trainer.simShell}: the active mon and the named slots are cloned, the rest are shared. */
	public SideState forkTouched(Set<Integer> touchedSlots) {
		return new SideState(shell.simShell(touchedSlots));
	}
}