package overworld;

import java.awt.Rectangle;

/**
 * One location on the world map (a town, route, or point of interest).
 * Rects and anchors are in screen space (WorldMapData.LAYOUT_W x LAYOUT_H, the size of worldmap_bg.png).
 *
 * GENERATED data lives in WorldMapData; edit freely afterwards.
 */
public class MapNode {

	public enum Type { TOWN, ROUTE, POI }

	/** Dominant terrain (derived from the real tiles). */
	public enum Biome { PLAINS, PATH, FOREST, MOUNTAIN, LAKE, CAVE, LAVA, TUNNEL, TOWER }

	/** id used by getLoc / resolve (0..48). */
	public final int id;
	public final String name;
	public final Type type;
	public final Biome biome;
	/** Bounding rect(s) of the node's footprint on the background: {x, y, w, h}. */
	public final int[][] rects;
	/** Index into player.p.locations that unlocks warping here, or -1 if not warpable. */
	public final int warpFlag;
	public final int warpMap, warpCol, warpRow;
	/** Where the marker / player pin sits (a building for towns, a path tile for routes). */
	public final int anchorX, anchorY;
	/** The real tile region this node was built from; regionMap is -1 for hand-painted nodes. */
	public final int regionMap, regionX, regionY, regionW, regionH, contentTiles;
	/** Terrain mix as percentages, ordered like WorldMapData.TERRAIN. */
	public final int[] terrain;
	/** ids of directly connected nodes (filled in by WorldMapData). */
	public int[] neighbors = new int[0];

	public MapNode(int id, String name, Type type, Biome biome, int[][] rects,
			int warpFlag, int warpMap, int warpCol, int warpRow,
			int anchorX, int anchorY,
			int regionMap, int regionX, int regionY, int regionW, int regionH, int contentTiles,
			int[] terrain) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.biome = biome;
		this.rects = rects;
		this.warpFlag = warpFlag;
		this.warpMap = warpMap;
		this.warpCol = warpCol;
		this.warpRow = warpRow;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.regionMap = regionMap;
		this.regionX = regionX;
		this.regionY = regionY;
		this.regionW = regionW;
		this.regionH = regionH;
		this.contentTiles = contentTiles;
		this.terrain = terrain;
	}

	public boolean isWarpDestination() {
		return warpFlag >= 0;
	}

	/** True if the player has unlocked this warp (does NOT check gp.canFly()). */
	public boolean isUnlocked(boolean[] locations) {
		return warpFlag >= 0 && warpFlag < locations.length && locations[warpFlag];
	}

	/** Rects scaled from layout space to the given screen size. */
	public Rectangle[] getRects(int screenW, int screenH) {
		double sx = screenW / (double) WorldMapData.LAYOUT_W;
		double sy = screenH / (double) WorldMapData.LAYOUT_H;
		Rectangle[] out = new Rectangle[rects.length];
		for (int i = 0; i < rects.length; i++) {
			int[] r = rects[i];
			int x = (int) Math.round(r[0] * sx), y = (int) Math.round(r[1] * sy);
			int x2 = (int) Math.round((r[0] + r[2]) * sx), y2 = (int) Math.round((r[1] + r[3]) * sy);
			out[i] = new Rectangle(x, y, x2 - x, y2 - y);
		}
		return out;
	}

	/** Marker anchor in layout space. */
	public int[] anchor() {
		return new int[] { anchorX, anchorY };
	}
}