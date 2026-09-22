package pokemon;

import java.util.Set;

/** §6. One side's state inside a SimState: a trainer shell. */
public class SideState {
	public Trainer shell;

	public SideState(Trainer shell) {
		this.shell = shell;
	}

	public Pokemon active() {
		return shell.current;
	}

	public Pokemon[] bench() {
		return shell.team;
	}

	public Pokemon slot(int i) {
		return shell.team[i];
	}

	/** Copy-on-write fork of just this side (§7.2), delegating to Trainer.simShell. */
	public SideState forkTouched(Set<Integer> touchedSlots) {
		return new SideState(shell.simShell(touchedSlots));
	}
}