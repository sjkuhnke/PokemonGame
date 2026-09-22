package pokemon;

/** §6. One outcome of a matrix cell: it happens with probability prob, ending in state. */
public class Branch {
	public double prob;
	public SimState state;
	/** Set by expandDamageBranches; consumed by runOneMoveInit. Null means SimPolicy.DEFAULT. */
	SimPolicy policy;
	/** True if this branch represents the move connecting (status move that didn't miss, or a
	 *  damaging hit, KO or not) — gates the Red Card / Whirlwind-family fan-out in runMoveAndFollowUps. */
	boolean likelyHit;

	public Branch(double prob, SimState state) {
		this.prob = prob;
		this.state = state;
	}
}