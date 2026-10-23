package ai;

/** One outcome of a matrix cell: it happens with probability {@code prob} and ends in {@code state}. */
public class Branch {
	public double prob;
	public SimState state;
	/** How the simulator should resolve this branch's rolls (hit or miss, KO roll, secondary effect). Set while branching, read when the move actually runs. Null means {@code SimPolicy.DEFAULT}. */
	SimPolicy policy;
	/** True if this branch represents the move connecting (a status move that didn't miss, or a damaging hit, KO or not). Gates the Red Card / Whirlwind-family fan-out. */
	boolean likelyHit;

	public Branch(double prob, SimState state) {
		this.prob = prob;
		this.state = state;
	}
}