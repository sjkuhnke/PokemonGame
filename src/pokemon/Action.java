package pokemon;

/**
 * §6. One action: a plain move, a plain switch, or a pivot move followed by a switch.
 * Immutable. slot is always a 0-based team index (not the game's 1-based/negative
 * MoveDecision encoding — that conversion happens where an Action is turned into a
 * MoveDecision, in Phase 3's toMoveDecision).
 */
public class Action {
	public final ActionKind kind;
	public final Move move;	// MOVE, MOVE_THEN_SWITCH
	public final int slot;		// SWITCH, MOVE_THEN_SWITCH; -1 otherwise

	public Action(Move move) {
		this.kind = ActionKind.MOVE;
		this.move = move;
		this.slot = -1;
	}

	public Action(int slot) {
		this.kind = ActionKind.SWITCH;
		this.move = null;
		this.slot = slot;
	}

	public Action(Move move, int slot) {
		this.kind = ActionKind.MOVE_THEN_SWITCH;
		this.move = move;
		this.slot = slot;
	}

	public String label() {
		switch (kind) {
			case MOVE: return move.toString();
			case SWITCH: return "Switch->" + slot;
			case MOVE_THEN_SWITCH: return move.toString() + "->Switch->" + slot;
			default: return "?";
		}
	}

	@Override
	public String toString() { return label(); }
}