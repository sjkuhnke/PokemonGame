package ai;

import pokemon.Move;

/**
 * One action of one side: a plain move, a plain switch, or a pivot move followed by a switch. These are the rows (AI)
 * and columns (player) of the payoff matrix. Immutable.
 * <p>
 * {@code slot} is always a 0-based team index, not the game's 1-based/negative {@code MoveDecision} encoding; that
 * conversion happens where an Action becomes a MoveDecision ({@code AIV2.toMoveDecision}).
 * <p>
 * {@link #PASS} is a MOVE action with a null move. The simulator treats it as "this side does nothing", so it is the
 * probe {@code ActionGen.deadTurn} uses ("AI passes, player passes" against "AI moves, player passes"). It is never part
 * of a real row or column set.
 */
public class Action {
	public final ActionKind kind;
	public final Move move;	// MOVE, MOVE_THEN_SWITCH
	public final int slot;		// SWITCH, MOVE_THEN_SWITCH; -1 otherwise

	/** "Nothing happens but end-of-turn effects": a MOVE with no move. See the class doc. */
	public static final Action PASS = new Action((Move) null);

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

	/** Short text for logs and the debug matrix, e.g. "Flamethrower", "Switch->2", "U-turn->Switch->1". */
	public String label() {
		switch (kind) {
			case MOVE: return move == null ? "Pass" : move.toString();
			case SWITCH: return "Switch->" + slot;
			case MOVE_THEN_SWITCH: return move.toString() + "->Switch->" + slot;
			default: return "?";
		}
	}

	@Override
	public String toString() { return label(); }
}