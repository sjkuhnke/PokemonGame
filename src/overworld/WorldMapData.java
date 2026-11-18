package overworld;

/**
 * Static data for the world map: the node table, the connection graph, and a side-effect-free
 * replacement for PMap.getLoc. GENERATED from PMap.java - edit freely afterwards.
 */
public final class WorldMapData {

	private WorldMapData() {}

	/** Size of the legacy PMap layout the rects below were taken from. */
	public static final int LAYOUT_W = 751;
	public static final int LAYOUT_H = 535;

	/** Result of resolve(): the node id (or -1) and the sub-area name (e.g. "Abandoned Tower (1B)"), or null. */
	public static final class Resolved {
		public final int id;
		public final String areaName;
		Resolved(int id, String areaName) {
			this.id = id;
			this.areaName = areaName;
		}
	}

	/** Indexed by node id. */
	public static final MapNode[] NODES = {
		new MapNode(0, "New Minnow Town", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{404, 330, 33, 31}},
				0, 0, 79, 46),
		new MapNode(1, "Route 22", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{276, 341, 129, 10}},
				-1, 0, 0, 0),
		new MapNode(2, "Bananaville Town", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{232, 330, 44, 33}},
				1, 0, 35, 45),
		new MapNode(3, "Route 23", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{246, 281, 12, 49}},
				-1, 0, 0, 0),
		new MapNode(4, "Poppy Grove", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{226, 236, 50, 45}},
				2, 4, 69, 72),
		new MapNode(5, "Route 24 pt. 1", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{148, 252, 78, 16}},
				-1, 0, 0, 0),
		new MapNode(6, "Route 24 pt. 2", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{87, 268, 75, 16}},
				-1, 0, 0, 0),
		new MapNode(7, "Gelb Forest", MapNode.Type.POI, MapNode.Biome.FOREST,
				new int[][] {{21, 258, 66, 56}},
				-1, 0, 0, 0),
		new MapNode(8, "Route 25", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{46, 182, 16, 76}},
				-1, 0, 0, 0),
		new MapNode(9, "Sicab City", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{20, 129, 68, 53}},
				3, 13, 38, 86),
		new MapNode(10, "Route 26", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{44, 63, 20, 66}},
				-1, 0, 0, 0),
		new MapNode(11, "Mt. Splinkty", MapNode.Type.POI, MapNode.Biome.MOUNTAIN,
				new int[][] {{30, 30, 50, 33}},
				-1, 0, 0, 0),
		new MapNode(12, "Route 27", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{78, 37, 64, 16}},
				-1, 0, 0, 0),
		new MapNode(13, "Kleine Village", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{142, 30, 32, 30}},
				4, 28, 76, 52),
		new MapNode(14, "Route 41", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{152, 60, 12, 27}},
				-1, 0, 0, 0),
		new MapNode(15, "Peaceful Park", MapNode.Type.POI, MapNode.Biome.FOREST,
				new int[][] {{104, 87, 110, 118}},
				5, 33, 32, 90),
		new MapNode(16, "Route 36", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{244, 191, 12, 45}, {214, 178, 42, 14}},
				-1, 0, 0, 0),
		new MapNode(17, "Route 28", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{174, 38, 65, 15}},
				-1, 0, 0, 0),
		new MapNode(18, "Electric Tunnel", MapNode.Type.POI, MapNode.Biome.TUNNEL,
				new int[][] {{239, 30, 75, 30}},
				-1, 0, 0, 0),
		new MapNode(19, "Route 29", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{314, 38, 75, 15}},
				-1, 0, 0, 0),
		new MapNode(20, "Schrice City", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{389, 25, 65, 44}},
				6, 38, 49, 55),
		new MapNode(21, "Route 40", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{244, 363, 20, 27}},
				-1, 0, 0, 0),
		new MapNode(22, "Route 30", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{414, 69, 14, 31}},
				-1, 0, 0, 0),
		new MapNode(23, "Mindagan Lake", MapNode.Type.POI, MapNode.Biome.LAKE,
				new int[][] {{301, 100, 228, 116}},
				-1, 0, 0, 0),
		new MapNode(24, "Route 31", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{414, 216, 14, 33}},
				-1, 0, 0, 0),
		new MapNode(25, "Shadow Ravine", MapNode.Type.POI, MapNode.Biome.CAVE,
				new int[][] {{314, 249, 209, 56}},
				-1, 0, 0, 0),
		new MapNode(26, "Route 42", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{414, 305, 13, 25}},
				-1, 0, 0, 0),
		new MapNode(27, "Route 32", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{523, 264, 33, 18}},
				-1, 0, 0, 0),
		new MapNode(28, "Frenco City", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{556, 248, 76, 56}},
				8, 85, 43, 76),
		new MapNode(29, "Route 33", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{598, 182, 16, 66}},
				-1, 0, 0, 0),
		new MapNode(30, "Ghostly Woods", MapNode.Type.POI, MapNode.Biome.FOREST,
				new int[][] {{564, 107, 85, 75}},
				-1, 0, 0, 0),
		new MapNode(31, "Route 34", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{560, 41, 54, 10}, {598, 51, 16, 56}},
				-1, 0, 0, 0),
		new MapNode(32, "Glurg Town", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{529, 30, 31, 30}},
				7, 109, 34, 45),
		new MapNode(33, "Route 35", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{454, 37, 75, 18}},
				-1, 0, 0, 0),
		new MapNode(34, "Route 37", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{46, 314, 16, 49}},
				-1, 0, 0, 0),
		new MapNode(35, "Rawwar City", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{20, 363, 70, 52}},
				9, 124, 21, 57),
		new MapNode(36, "Route 38", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{90, 388, 30, 13}},
				-1, 0, 0, 0),
		new MapNode(37, "Mt. St. Joseph", MapNode.Type.POI, MapNode.Biome.MOUNTAIN,
				new int[][] {{120, 360, 74, 81}},
				-1, 0, 0, 0),
		new MapNode(38, "Route 39", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{46, 415, 16, 32}},
				-1, 0, 0, 0),
		new MapNode(39, "Iron Town", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{35, 447, 39, 32}},
				10, 152, 37, 73),
		new MapNode(40, "Lava Lake", MapNode.Type.POI, MapNode.Biome.LAVA,
				new int[][] {{201, 390, 80, 74}},
				-1, 0, 0, 0),
		new MapNode(41, "Route 44", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{75, 314, 33, 13}},
				-1, 0, 0, 0),
		new MapNode(42, "Deep Chasm", MapNode.Type.POI, MapNode.Biome.CAVE,
				new int[][] {{98, 327, 31, 30}},
				-1, 0, 0, 0),
		new MapNode(43, "A.T. Path", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{649, 129, 20, 10}, {659, 87, 10, 42}},
				-1, 0, 0, 0),
		new MapNode(44, "Abandoned Tower", MapNode.Type.POI, MapNode.Biome.TOWER,
				new int[][] {{651, 64, 26, 23}},
				-1, 0, 0, 0),
		new MapNode(45, "Route 43", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{437, 338, 70, 15}},
				-1, 0, 0, 0),
		new MapNode(46, "Checkpoint Charlie", MapNode.Type.TOWN, MapNode.Biome.PLAINS,
				new int[][] {{618, 323, 68, 44}},
				11, 215, 58, 72),
		new MapNode(47, "Route 45", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{64, 68, 36, 14}},
				-1, 0, 0, 0),
		new MapNode(48, "Victory Road", MapNode.Type.ROUTE, MapNode.Biome.PATH,
				new int[][] {{507, 338, 111, 15}},
				-1, 0, 0, 0),
	};

	/** Undirected connections between node ids (derived from rect adjacency). */
	private static final int[][] EDGES = {
		{0, 1},
		{0, 26},
		{0, 45},
		{1, 2},
		{2, 3},
		{2, 21},
		{3, 4},
		{4, 5},
		{4, 16},
		{5, 6},
		{6, 7},
		{7, 8},
		{7, 34},
		{7, 41},
		{8, 9},
		{9, 10},
		{10, 11},
		{10, 47},
		{11, 12},
		{12, 13},
		{13, 14},
		{13, 17},
		{14, 15},
		{15, 16},
		{17, 18},
		{18, 19},
		{19, 20},
		{20, 22},
		{20, 33},
		{21, 40},
		{22, 23},
		{23, 24},
		{24, 25},
		{25, 26},
		{25, 27},
		{27, 28},
		{28, 29},
		{29, 30},
		{30, 31},
		{30, 43},
		{31, 32},
		{32, 33},
		{34, 35},
		{35, 36},
		{35, 38},
		{36, 37},
		{37, 42},
		{38, 39},
		{41, 42},
		{43, 44},
		{45, 48},
		{46, 48},
	};

	static {
		int[] count = new int[NODES.length];
		for (int[] e : EDGES) { count[e[0]]++; count[e[1]]++; }
		for (int i = 0; i < NODES.length; i++) NODES[i].neighbors = new int[count[i]];
		int[] fill = new int[NODES.length];
		for (int[] e : EDGES) {
			NODES[e[0]].neighbors[fill[e[0]]++] = e[1];
			NODES[e[1]].neighbors[fill[e[1]]++] = e[0];
		}
		for (int i = 0; i < NODES.length; i++) {
			if (NODES[i].id != i) throw new IllegalStateException("NODES must be indexed by id: " + i);
		}
	}

	public static MapNode get(int id) {
		return (id >= 0 && id < NODES.length) ? NODES[id] : null;
	}

	/** Same lookup as the old PMap.getLoc, but returns the area name instead of writing PlayerCharacter.currentMapName. */
	public static Resolved resolve(int map, int x, int y) {
			String name = null;
			switch (map) {
			case 0:
				if (x > 73 && y > 37) {
					name = "New Minnow Town";
					return new Resolved(0, name);
				}
				if (x <= 73 && x > 41 && y > 36) {
					name = "Route 22";
					return new Resolved(1, name);
				}
				if (x <= 41 && y > 41 && y <= 58) {
					name = "Bananaville Town";
					return new Resolved(2, name);
				}
				if (x <= 40 && y > 58) {
					name = "Bananaville Meadow";
					return new Resolved(2, name);
				}
				if (x <= 40 && y <= 41) {
					name = "Route 23";
					return new Resolved(3, name);
				}
				if (x > 40 && y <= 37) {
					name = "Route 42";
					return new Resolved(26, name);
				}
				break;
			case 1:
				name = "Bananaville Town";
				return new Resolved(2, name);
			case 2:
				name = "Bananaville Town";
				return new Resolved(2, name);
			case 3:
				name = "Route 23";
				return new Resolved(3, name);
			case 4:
				if (y <= 43) {
					name = "Route 36";
					return new Resolved(16, name);
				}
				if (x > 57) {
					name = "Poppy Grove";
					return new Resolved(4, name);
				}
				if (x <= 57) {
					name = "Route 24 (pt. 1)";
					return new Resolved(5, name);
				}
				break;
			case 5:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 6:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 7:
				name = "Warehouse";
				return new Resolved(4, name);
			case 8:
				name = "Warehouse";
				return new Resolved(4, name);
			case 9:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 10:
				name = "Route 24 (pt. 2)";
				return new Resolved(6, name);
			case 11:
				if (y <= 47) {
					if (x > 49) {
						name = "Route 24 (pt. 2)";
						return new Resolved(6, name);
					}
					if (x <= 49) {
						name = "Route 25";
						return new Resolved(8, name);
					}
				}
				if (y > 47) {
					if (x > 43) {
						name = "Route 24 (pt. 2)";
						return new Resolved(6, name);
					}
					if (x <= 43) {
						name = "Gelb Forest (1A)";
						return new Resolved(7, name);
					}
				}
			case 12:
				name = "Route 25";
				return new Resolved(8, name);
			case 13:
				if (y <= 52) {
					if (x <= 43) {
						name = "Route 26";
						return new Resolved(10, name);
					} else {
						name = "Route 45";
						return new Resolved(47, name);
					}
				}
				if (y > 52) {
					if (x <= 63) {
						name = "Sicab City";
						return new Resolved(9, name);
					} else {
						name = "Sicab River";
						return new Resolved(9, name);
					}
				}
			case 14:
				name = "Energy Plant (pt. 1)";
				return new Resolved(9, name);
			case 15:
				name = "Energy Plant (B1F)";
				return new Resolved(9, name);
			case 16:
				name = "Energy Plant (pt. 2)";
				return new Resolved(9, name);
			case 17:
				name = "Sicab Office (1A)";
				return new Resolved(9, name);
			case 18:
				name = "Sicab Office (2A)";
				return new Resolved(9, name);
			case 19:
				name = "Sicab City";
				return new Resolved(9, name);
			case 20:
				name = "Sicab City";
				return new Resolved(9, name);
			case 21:
				name = "Sicab City";
				return new Resolved(9, name);
			case 22:
				if (y <= 18) {
					name = "Route 40";
					return new Resolved(21, name);
				}
				if (y > 18) {
					name = "Lava Lake";
					return new Resolved(40, name);
				}
			case 23:
				name = "Route 40";
				return new Resolved(21, name);
			case 24:
				name = "Mt. Splinkty (1A)";
				return new Resolved(11, name);
			case 25:
				name = "Mt. Splinkty (2B)";
				return new Resolved(11, name);
			case 26:
				name = "Mt. Splinkty (3B)";
				return new Resolved(11, name);
			case 27:
				name = "Mt. Splinkty (3A)";
				return new Resolved(11, name);
			case 28:
				if (y > 57) {
					name = "Route 41";
					return new Resolved(14, name);
				}
				if (x <= 61) {
					name = "Route 27";
					return new Resolved(12, name);
				}
				if (x > 61) {
					if (y <= 31) {
						name = "Cell Park";
						return new Resolved(13, name);
					} else {
						name = "Kleine Village";
						return new Resolved(13, name);
					}	
				}
			case 29:
				name = "Kleine Village";
				return new Resolved(13, name);
			case 30:
				name = "Kleine Village";
				return new Resolved(13, name);
			case 31:
				name = "Kleine Village";
				return new Resolved(13, name);
			case 32:
				name = "Sicab City";
				return new Resolved(9, name);
			case 33:
				if (y <= 25) {
					name = "Route 28";
					return new Resolved(17, name);
				}
				if (y > 25) {
					name = "Peaceful Park";
					return new Resolved(15, name);
				}
			case 34:
				name = "Route 28";
				return new Resolved(17, name);
			case 35:
				name = "Electric Tunnel (1A)";
				return new Resolved(18, name);
			case 36:
				name = "Route 29";
				return new Resolved(19, name);
			case 37:
				name = "Route 29";
				return new Resolved(19, name);
			case 38:
				if (y <= 63) {
					name = y <= 30 ? "Icy Fields" : "Schrice City";
					return new Resolved(20, name);
				}
				if (y > 63) {
					name = "Route 30";
					return new Resolved(22, name);
				}
			case 39:
				name = "Schrice City";
				return new Resolved(20, name);
			case 40:
				name = "Schrice City";
				return new Resolved(20, name);
			case 41:
				name = "Schrice City";
				return new Resolved(20, name);
			case 42:
				name = "Schrice City";
				return new Resolved(20, name);
			case 43:
				name = "Schrice City";
				return new Resolved(20, name);
			case 44:
				name = "Schrice City";
				return new Resolved(20, name);
			case 45:
				name = "Schrice City";
				return new Resolved(20, name);
			case 46:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 47:
				name = "Bananaville Town";
				return new Resolved(2, name);
			case 48:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 49:
				name = "Kleine Village";
				return new Resolved(13, name);
			case 50:
				name = "Schrice City";
				return new Resolved(20, name);
			case 51:
				name = "New Minnow Town";
				return new Resolved(0, name);
			case 52:
				name = "New Minnow Town";
				return new Resolved(0, name);
			case 53:
				name = "Frenco City";
				return new Resolved(0, name);
			case 54:
				name = "New Minnow Town";
				return new Resolved(0, name);
			case 55:
				name = "Bananaville Town";
				return new Resolved(2, name);
			case 56:
				name = "Bananaville Town";
				return new Resolved(2, name);
			case 57:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 58:
				name = "Poppy Grove";
				return new Resolved(4, name);
			case 59:
				name = "Sicab City";
				return new Resolved(9, name);
			case 60:
				name = "Control Center";
				return new Resolved(9, name);
			case 61:
				name = "Kleine Village";
				return new Resolved(13, name);
			case 62:
				name = "Schrice City";
				return new Resolved(20, name);
			case 63:
				name = "Schrice City";
				return new Resolved(20, name);
			case 64:
				name = "Schrice City";
				return new Resolved(20, name);
			case 65:
				name = "Schrice City";
				return new Resolved(20, name);
			case 66:
				name = "Schrice City";
				return new Resolved(20, name);
			case 67:
				name = "Schrice City";
				return new Resolved(20, name);
			case 68:
				name = "Schrice City";
				return new Resolved(20, name);
			case 69:
				name = "Schrice City";
				return new Resolved(20, name);
			case 70:
				name = "Schrice City";
				return new Resolved(20, name);
			case 71:
				name = "Schrice City";
				return new Resolved(20, name);
			case 72:
				name = "Schrice City";
				return new Resolved(20, name);
			case 73:
				name = "Schrice City";
				return new Resolved(20, name);
			case 74:
				name = "Peaceful Park";
				return new Resolved(15, name);
			case 75:
				name = "Peaceful Park";
				return new Resolved(15, name);
			case 76:
				name = "Route 30";
				return new Resolved(22, name);
			case 77:
				name = "Mindagan Lake";
				return new Resolved(23, name);
			case 78:
				name = "Mindagan Cavern (1A)";
				return new Resolved(23, name);
			case 79:
				name = "Route 31";
				return new Resolved(24, name);
			case 80:
				if (y <= 41) {
					name = "Route 31";
					return new Resolved(24, name);
				}
				if (y > 41) {
					name = "Shadow Ravine (1A)";
					return new Resolved(25, name);
				}
			case 81:
				name = "Route 42";
				return new Resolved(26, name);
			case 82:
				name = "Shadow Ravine (1A)";
				return new Resolved(25, name);
			case 83:
				if (x <= 56) {
					name = "Shadow Ravine (1B)";
					return new Resolved(25, name);
				}
				if (x > 56) {
					name = "Route 32";
					return new Resolved(27, name);
				}
			case 84:
				name = "Route 32";
				return new Resolved(27, name);
			case 85:
				if (x <= 63) {
					name = "Frenco City";
					return new Resolved(28, name);
				}
				if (x > 64) {
					name = "Route 33";
					return new Resolved(29, name);
				}
			case 86:
				name = "Frenco City";
				return new Resolved(28, name);
			case 87:
				name = "Frenco City";
				return new Resolved(28, name);
			case 88:
				name = "Frenco City";
				return new Resolved(28, name);
			case 89:
				name = "Frenco City";
				return new Resolved(28, name);
			case 90:
				name = "Shadow Ravine (0A)";
				return new Resolved(25, name);
			case 91:
				name = "Frenco City";
				return new Resolved(28, name);
			case 92:
				name = "Peaceful Park";
				return new Resolved(15, name);
			case 93:
				name = "Frenco City";
				return new Resolved(28, name);
			case 94:
				name = "Frenco City";
				return new Resolved(28, name);
			case 95:
				name = "Electric Tunnel (0A)";
				return new Resolved(18, name);
			case 96:
				name = "Electric Tunnel (-1A)";
				return new Resolved(18, name);
			case 97:
				name = "Electric Tunnel (-2A)";
				return new Resolved(18, name);
			case 98:
				name = "Electric Tunnel (-3A)";
				return new Resolved(18, name);
			case 99:
				name = "Electric Tunnel (H)";
				return new Resolved(18, name);
			case 100:
				name = "Shadow Ravine (H)";
				return new Resolved(25, name);
			case 101:
				name = "Shadow Ravine (-1A)";
				return new Resolved(25, name);
			case 102:
				name = "Shadow Ravine (-2A)";
				return new Resolved(25, name);
			case 103:
				name = "Shadow Ravine (-3A)";
				return new Resolved(25, name);
			case 104:
				name = "Team Eclipse Base";
				return new Resolved(-1, name);
			case 105:
				name = "Shadow Path";
				return new Resolved(-1, name);
			case 106:
				name = "Ghostly Woods";
				return new Resolved(30, name);
			case 107:
				name = "Ghostly Woods";
				return new Resolved(30, name);
			case 108:
				name = "Route 34 (1A)";
				return new Resolved(31, name);
			case 109:
				if (x > 55) {
					name = "Route 34 (1A)";
					return new Resolved(31, name);
				}
				if (x <= 55) {
					name = "Glurg Town";
					return new Resolved(32, name);
				}
			case 110:
				name = "Route 34 (0A)";
				return new Resolved(31, name);
			case 111:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 112:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 113:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 114:
				name = "Route 35";
				return new Resolved(33, name);
			case 115:
				name = "Route 35";
				return new Resolved(33, name);
			case 116:
				name = "Schrice City";
				return new Resolved(20, name);
			case 117:
				name = "Mindagan Cavern (0A)";
				return new Resolved(23, name);
			case 118:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 119: 
				name = "Route 43";
				return new Resolved(45, name);
			case 120:
				name = "New Minnow Town";
				return new Resolved(0, name);
			case 121:
				name = "Sicab City";
				return new Resolved(9, name);
			case 122:
				name = "Sicab City";
				return new Resolved(9, name);
			case 123:
				name = "Gelb Forest (1A)";
				return new Resolved(7, name);
			case 124:
				if (y <= 49) {
					name = "Route 37";
					return new Resolved(34, name);
				}
				if (x <= 45) {
					name = "Rawwar City";
					return new Resolved(35, name);
				}
				if (x > 45) {
					name = "Route 38";
					return new Resolved(36, name);
				}
			case 125:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 126:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 127:
				name = "Casino";
				return new Resolved(35, name);
			case 128:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 129:
				name = "Prize Shop";
				return new Resolved(35, name);
			case 130:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 131:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 132:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 133:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 134:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 135:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 136:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 137:
				name = "Mt. St. Joseph (1A)";
				return new Resolved(37, name);
			case 138:
				name = "Mt. St. Joseph (2A)";
				return new Resolved(37, name);
			case 139:
				name = "Mt. St. Joseph (2B)";
				return new Resolved(37, name);
			case 140:
				name = "Mt. St. Joseph (3A)";
				return new Resolved(37, name);
			case 141:
				name = "Mt. St. Joseph (3B)";
				return new Resolved(37, name);
			case 142:
				name = "Mt. St. Joseph (4A)";
				return new Resolved(37, name);
			case 143:
				name = "Mt. St. Joseph (4B)";
				return new Resolved(37, name);
			case 144:
				if (x <= 32) {
					name = "Gelb Forest (2A)";
					return new Resolved(7, name);
				}
				if (x > 32) {
					name = "Route 44";
					return new Resolved(41, name);
				}
			case 145:
				name = "Mt. Splinkty (2A)";
				return new Resolved(11, name);
			case 146:
				name = "Mt. Splinkty (Outside)";
				return new Resolved(11, name);
			case 147:
				name = "Mt. Splinkty (H)";
				return new Resolved(11, name);
			case 148:
				name = "Mt. Splinkty (4A)";
				return new Resolved(11, name);
			case 149:
				name = "Mt. Splinkty (5A)";
				return new Resolved(11, name);
			case 150:
				name = "Shadow Cavern";
				return new Resolved(-1, name);
			case 151:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 152:
				if (x <= 55) {
					if (y <= 61) {
						name = "Route 39";
						return new Resolved(38, name);
					}
					if (y > 61) {
						name = "Iron Town";
						return new Resolved(39, name);
					}
				} else {
					if (y <= 53) {
						name = "Route 39";
						return new Resolved(38, name);
					}
					if (y > 53) {
						name = "Iron Town";
						return new Resolved(39, name);
					}
				}
				
			case 153:
				name = "Iron Town";
				return new Resolved(39, name);
			case 154:
				name = "Iron Town";
				return new Resolved(39, name);
			case 155:
				name = "Iron Town";
				return new Resolved(39, name);
			case 156:
				name = "Iron Town";
				return new Resolved(39, name);
			case 157:
				name = "Iron Town";
				return new Resolved(39, name);
			case 158:
				name = "Iron Town";
				return new Resolved(39, name);
			case 159:
				name = "Outer Space";
				return new Resolved(-1, name);
			case 160:
				name = "Outer Space";
				return new Resolved(-1, name);
			case 161:
				name = "Post Office";
				return new Resolved(4, name);
			case 162:
				name = "Research Post B";
				return new Resolved(47, name);
			case 163:
				name = "Schrice City";
				return new Resolved(20, name);
			case 164:
				name = "Schrice City";
				return new Resolved(20, name);
			case 165:
				name = "Schrice City";
				return new Resolved(20, name);
			case 166:
				name = "Icy Path";
				return new Resolved(20, name);
			case 167:
				name = "Peaceful Park";
				return new Resolved(15, name);
			case 168:
				name = "Gelb Cavern";
				return new Resolved(7, name);
			case 169:
				name = "Frenco City";
				return new Resolved(20, name);
			case 170:
				name = "Frenco City";
				return new Resolved(28, name);
			case 171:
				name = "Frenco City";
				return new Resolved(28, name);
			case 172:
				name = "Frenco City";
				return new Resolved(28, name);
			case 173:
				name = "Frenco City";
				return new Resolved(28, name);
			case 174:
				name = "Frenco City";
				return new Resolved(28, name);
			case 175:
				name = "Frenco City";
				return new Resolved(28, name);
			case 176:
				name = "Frenco City";
				return new Resolved(28, name);
			case 177:
				name = "Frenco City";
				return new Resolved(28, name);
			case 178:
				name = "Research Post A";
				return new Resolved(5, name);
			case 179:
				name = "Route 37";
				return new Resolved(34, name);
			case 180:
				name = "Route 38";
				return new Resolved(36, name);
			case 181:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 182:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 183:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 184:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 185:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 186:
				name = "Rawwar City";
				return new Resolved(35, name);
			case 187:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 188:
				name = "Glurg Town";
				return new Resolved(32, name);
			case 189:
				name = "Ghostly Woods";
				return new Resolved(30, name);
			case 190:
				name = "A.T. Path";
				return new Resolved(43, name);
			case 191:
				if (y <= 55) {
					name = "Abandoned Tower (1B)";
				} else {
					name = "Abandoned Tower (1A)";
				}
				return new Resolved(44, name);
			case 192:
				if (y <= 55) {
					name = "Abandoned Tower (2A)";
				} else {
					name = "Abandoned Tower (2B)";
				}
				return new Resolved(44, name);
			case 193:
				if (x <= 63) {
					name = "Abandoned Tower (3A)";
				} else {
					name = "Abandoned Tower (3B)";
				}
				return new Resolved(44, name);
			case 194:
				name = "Abandoned Tower (4A)";
				return new Resolved(44, name);
			case 195:
				name = "Abandoned Tower (5A)";
				return new Resolved(44, name);
			case 196:
				name = "Abandoned Tower (6A)";
				return new Resolved(44, name);
			case 197:
				if (y <= 66) {
					name = "Deep Chasm (0B)";
				} else {
					name = "Deep Chasm (0A)";
				}
				return new Resolved(42, name);
			case 198:
				name = "Deep Chasm (-1A)";
				return new Resolved(42, name);
			case 199:
				name = "Deep Chasm (-2A)";
				return new Resolved(42, name);
			case 200:
				name = "Deep Chasm (-3A)";
				return new Resolved(42, name);
			case 201:
				name = "Deep Chasm (-4A)";
				return new Resolved(42, name);
			case 202:
				name = "Deep Chasm (-5A)";
				return new Resolved(42, name);
			case 203:
				name = "Deep Chasm (-2A)";
				return new Resolved(42, name);
			case 204:
				name = "Iron Town";
				return new Resolved(39, name);
			case 205:
				name = "Route 39";
				return new Resolved(38, name);
			case 206:
				name = "Space Cavern (1A)";
				return new Resolved(-1, name);
			case 207:
				name = "Space Cavern (2A)";
				return new Resolved(-1, name);
			case 208:
				name = "Mystery Peak (1A)";
				return new Resolved(-1, name);
			case 209:
				name = "Mystery Peak (1B)";
				return new Resolved(-1, name);
			case 210:
				name = "Mindagan Cavern (0B)";
				return new Resolved(23, name);
			case 211:
				name = "Shadow Ravine (-2B)";
				return new Resolved(25, name);
			case 212:
				name = "Route 43";
				return new Resolved(45, name);
			case 213:
				name = "Victory Road";
				return new Resolved(48, name);
			case 214:
				name = "Victory Road";
				return new Resolved(48, name);
			case 215:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 216:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 217:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 218:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 219:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 220:
				name = "Checkpoint Charlie";
				return new Resolved(46, name);
			case 221:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 222:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 223:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 224:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 225:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 226:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 227:
				name = "Leviathan League";
				return new Resolved(46, name);
			case 228:
				name = "Research Post C";
				return new Resolved(20, name);
			case 229:
				name = "Research Post C";
				return new Resolved(20, name);
			case 230:
				name = "Research Post C";
				return new Resolved(20, name);
			case 231:
				name = "Research Post C";
				return new Resolved(20, name);
			case 232:
				name = "Research Post C";
				return new Resolved(20, name);
			case 233:
				name = "New Minnow Town";
				return new Resolved(0, name);
			}
			
			return new Resolved(-1, name);
		}
}