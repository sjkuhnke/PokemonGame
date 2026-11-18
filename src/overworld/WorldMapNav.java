package overworld;

/**
 * Keyboard cursor movement for the world map: pressing a direction jumps to the best node
 * in that direction (nearest, with a penalty for sideways drift). Pure logic, no UI deps.
 */
public final class WorldMapNav {

	private WorldMapNav() {}

	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

	/** How much sideways offset counts against a candidate compared to forward distance. */
	private static final double PERP_WEIGHT = 2.0;

	/**
	 * Returns the node id the cursor should move to, or {@code from} if nothing lies that way.
	 * Only nodes inside a 45-degree cone in the pressed direction count; among them the closest
	 * wins, with sideways drift penalized. (Every node is still reachable from every other.)
	 */
	public static int move(int from, int dir) {
		MapNode a = WorldMapData.get(from);
		if (a == null) return from;
		int[] ap = a.anchor();
		int best = from;
		double bestScore = Double.MAX_VALUE;
		for (MapNode c : WorldMapData.NODES) {
			if (c.id == from) continue;
			int[] cp = c.anchor();
			int dx = cp[0] - ap[0], dy = cp[1] - ap[1];
			int primary, perp;
			switch (dir) {
			case UP:    primary = -dy; perp = Math.abs(dx); break;
			case DOWN:  primary = dy;  perp = Math.abs(dx); break;
			case LEFT:  primary = -dx; perp = Math.abs(dy); break;
			default:    primary = dx;  perp = Math.abs(dy); break;
			}
			if (primary <= 0 || perp > primary) continue;
			double score = primary + PERP_WEIGHT * perp;
			if (score < bestScore) {
				bestScore = score;
				best = c.id;
			}
		}
		return best;
	}
}