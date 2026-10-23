package ai;

/** What kind of thing a matrix row or column represents. */
public enum ActionKind {
	/** Stay in and use a move (or pass, see {@link Action#PASS}). */
	MOVE,
	/** Switch out to a bench mon without attacking. */
	SWITCH,
	/** A pivot move (U-turn, Volt Switch, ...): attack, then switch to a planned bench mon. */
	MOVE_THEN_SWITCH
}