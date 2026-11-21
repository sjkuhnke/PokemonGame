package ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import overworld.MapNode;
import overworld.WorldMapData;

/**
 * The runtime-drawn connected graph: an icon rectangle for every node (boxes for towns and sites,
 * bars for routes) plus orthogonal connector rectangles between connected nodes, like the Town Map in
 * HeartGold/SoulSilver. Pure geometry, derived from MapNode anchors and WorldMapData's connections;
 * call rebuild() after moving an anchor.
 */
public class WorldMapGraph {

	public static final int TOWN_SIZE = 24;
	public static final int POI_SIZE = 16;
	public static final int ROUTE_THICKNESS = 10;
	public static final int CONNECTOR = 4;

	private final int n = WorldMapData.NODES.length;
	public final Rectangle[] icons = new Rectangle[n];
	/** Every connector segment as a rectangle (already includes thickness). */
	public final List<Rectangle> connectors = new ArrayList<Rectangle>();

	public WorldMapGraph() {
		rebuild();
	}

	public Rectangle bounds(int id) {
		return id >= 0 && id < n ? icons[id] : null;
	}

	public void rebuild() {
		for (MapNode node : WorldMapData.NODES) icons[node.id] = iconFor(node);
		connectors.clear();
		for (int[] e : WorldMapData.edges()) connect(e[0], e[1]);
	}

	private Rectangle iconFor(MapNode node) {
		int x = node.anchorX, y = node.anchorY;
		switch (node.type) {
		case TOWN:
			return centered(x, y, TOWN_SIZE, TOWN_SIZE);
		case POI:
			return centered(x, y, POI_SIZE, POI_SIZE);
		default: {
			// Routes are bars lying along the direction of their connections.
			int hs = 0, vs = 0, axial = Integer.MAX_VALUE;
			int[] nb = node.neighbors;
			for (int id : nb) {
				MapNode o = WorldMapData.get(id);
				hs += Math.abs(o.anchorX - x);
				vs += Math.abs(o.anchorY - y);
			}
			boolean horizontal = hs > vs * 1.3, vertical = vs > hs * 1.3;
			if (!horizontal && !vertical) return centered(x, y, 14, 14);
			for (int id : nb) {
				MapNode o = WorldMapData.get(id);
				int d = horizontal ? Math.abs(o.anchorX - x) : Math.abs(o.anchorY - y);
				if (d > 0) axial = Math.min(axial, d);
			}
			int len = axial == Integer.MAX_VALUE ? 28 : Math.max(24, Math.min(48, axial * 6 / 10));
			return horizontal ? centered(x, y, len, ROUTE_THICKNESS) : centered(x, y, ROUTE_THICKNESS, len);
		}
		}
	}

	private static Rectangle centered(int cx, int cy, int w, int h) {
		return new Rectangle(cx - w / 2, cy - h / 2, w, h);
	}

	/** Picks the cheapest orthogonal path (straight, L, or Z) that avoids other icons. */
	private void connect(int a, int b) {
		MapNode A = WorldMapData.get(a), B = WorldMapData.get(b);
		int ax = A.anchorX, ay = A.anchorY, bx = B.anchorX, by = B.anchorY;
		int[][][] options;
		if (ax == bx || ay == by) {
			options = new int[][][] { { { ax, ay }, { bx, by } } };
		} else {
			int mx = (ax + bx) / 2, my = (ay + by) / 2;
			options = new int[][][] {
					{ { ax, ay }, { bx, ay }, { bx, by } },
					{ { ax, ay }, { ax, by }, { bx, by } },
					{ { ax, ay }, { mx, ay }, { mx, by }, { bx, by } },
					{ { ax, ay }, { ax, my }, { bx, my }, { bx, by } } };
		}
		int[][] best = null;
		double bestCost = Double.MAX_VALUE;
		for (int[][] path : options) {
			double cost = 0;
			for (int i = 0; i + 1 < path.length; i++) {
				cost += Math.abs(path[i + 1][0] - path[i][0]) + Math.abs(path[i + 1][1] - path[i][1]);
				Rectangle seg = segment(path[i], path[i + 1]);
				for (int k = 0; k < n; k++) {
					if (k == a || k == b) continue;
					Rectangle ic = new Rectangle(icons[k]);
					ic.grow(4, 4);
					if (ic.intersects(seg)) cost += 500;
				}
			}
			cost += 12 * (path.length - 2); // prefer fewer bends
			if (cost < bestCost) {
				bestCost = cost;
				best = path;
			}
		}
		for (int i = 0; i + 1 < best.length; i++) connectors.add(segment(best[i], best[i + 1]));
	}

	private static Rectangle segment(int[] p, int[] q) {
		int x0 = Math.min(p[0], q[0]), x1 = Math.max(p[0], q[0]);
		int y0 = Math.min(p[1], q[1]), y1 = Math.max(p[1], q[1]);
		int t = CONNECTOR;
		return new Rectangle(x0 - t / 2, y0 - t / 2, (x1 - x0) + t, (y1 - y0) + t);
	}
}