package pokemon;

/**
 * §6. One action: a plain move, a plain switch, or a pivot move followed by a switch.
 * Immutable. slot is always a 0-based team index (not the game's 1-based/negative
 * MoveDecision encoding - that conversion happens where an Action is turned into a
 * MoveDecision, in AIV2.toMoveDecision).
 *
 * Phase 3 addition: PASS, a kind=MOVE action with a null move. This already "does nothing"
 * under BattleSimulator's existing logic (aMove ends up null -> aCanAct=false -> no switch, no
 * move touched), so no BattleSimulator/ActionKind change was needed to support it. Used only
 * internally by ActionGen.deadTurn's what-if probes ("AI: PASS, player: PASS" / "AI: MOVE m,
 * player: PASS", §7.13.1) - never added to a real genAIActions/genPlayerActions row set.
 */
public class Action {
	public final ActionKind kind;
	public final Move move;	// MOVE, MOVE_THEN_SWITCH
	public final int slot;		// SWITCH, MOVE_THEN_SWITCH; -1 otherwise

	/** Phase 3: "nothing happens but end-of-turn effects" probe action - see class doc. */
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