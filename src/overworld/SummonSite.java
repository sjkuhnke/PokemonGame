package overworld;

import java.util.*;

import pokemon.PType;
import pokemon.Pokemon;

public final class SummonSite {
	
	public enum Mode {
		HOME,          // the map's own resident; random from the pool in nuzlocke
		RANDOM,        // uniform random from the pool
		TYPE_WEIGHTED  // random, weighted toward the sacrificed Pokemon's types (existing lab behavior)
	}
	
	public static final class Entry {
		private final int pokemonId;
		private final int trainerId;
		private final String cry;
		
		public Entry(int pokemonId, int trainerId, String cry) {
			this.pokemonId = pokemonId;
			this.trainerId = trainerId;
			this.cry = cry;
		}
		
		public int pokemonId() { return pokemonId; }
		public int trainerId() { return trainerId; }
		public String cry() { return cry; }
	}
	
	public static final Map<Integer, SummonSite> SITES = new HashMap<>();
	
	public final int map;
	public final int npcSlot;      // gp.npc[map][npcSlot]; must be a slot nothing else uses on that map
	public final int[][] spawns;   // {x, y} options; one = fixed, several = deterministic-random per save
	public final int fableCost;    // 0 = not a Fable Stone site (checkSummon ignores it)
	public final Mode mode;
	private final int homeId;
	private final Map<Integer, Entry> pool = new LinkedHashMap<>();
	
	private SummonSite(int map, int npcSlot, int[][] spawns, int fableCost, Mode mode, int homeId, List<Entry> entries) {
		this.map = map;
		this.npcSlot = npcSlot;
		this.spawns = spawns;
		this.fableCost = fableCost;
		this.mode = mode;
		this.homeId = homeId;
		for (Entry e : entries) pool.put(e.pokemonId(), e);
	}
	
	public static void register(int map, int npcSlot, int[][] spawns, int fableCost, Mode mode, int homeId, List<Entry> entries) {
		SITES.put(map, new SummonSite(map, npcSlot, spawns, fableCost, mode, homeId, entries));
	}
	
	public boolean isFableSite() {
		return fableCost > 0;
	}
	
	/** Entry for a stored pokemon ID. */
	public Entry entry(int pokemonId) {
		return pool.get(pokemonId);
	}
	
	/** Same seed -> same spot, so the summoned Pokemon is in the same place after reloading. */
	public int[] spawnFor(int seed) {
		return spawns[Math.floorMod(new Random(seed).nextInt(), spawns.length)];
	}
	
	public int pick(Random random, boolean nuzlocke, PType[] types) {
		List<Integer> ids = new ArrayList<>(pool.keySet());
		switch (mode) {
		case HOME:
			return nuzlocke ? ids.get(random.nextInt(ids.size())) : homeId;
		case TYPE_WEIGHTED: {
			if (types == null) types = new PType[0];
			int[] weights = new int[ids.size()];
			int total = 0;
			for (int i = 0; i < ids.size(); i++) {
				int w = 1;
				for (PType t : types) {
					if (t == Pokemon.getType1(ids.get(i)) || t == Pokemon.getType2(ids.get(i))) w += 3;
				}
				weights[i] = w;
				total += w;
			}
			int roll = random.nextInt(total);
			for (int i = 0; i < ids.size(); i++) {
				roll -= weights[i];
				if (roll < 0) return ids.get(i);
			}
			return ids.get(ids.size() - 1);
		}
		default:
			return ids.get(random.nextInt(ids.size()));
		}
	}
	
	public static void registerAll() {
		SITES.clear();
		
		List<Entry> quintet = Arrays.asList(
				new Entry(228, 510, "Glublublerbe... bubblava!"), // blohadel
				new Entry(229, 325, "Vrrrrlyigtt!"),               // perilyte
				new Entry(230, 511, "Jahahack! Jahacklehette!!"),  // faulette
				new Entry(231, 512, "Ohohquotta!"),                // sasquotta
				new Entry(232, 326, "Vouuudouhhh!!!"));            // hueduu
		//                        map  id   x   y
		int[][] quintetSites = { {210, 228, 54, 62}, {150, 229, 46, 66}, {127, 230, 31, 35}, {209, 231, 49, 45}, {148, 232, 50, 68} };
		for (int[] s : quintetSites) {
			register(s[0], 0, new int[][] { { s[2], s[3] } }, 5, Mode.HOME, s[1], quintet);
		}
		
		register(232, 0, new int[][] { { 31, 42 } }, 0, Mode.TYPE_WEIGHTED, -1, ultraParadoxPool(550));
		
		// Case 3: space island (map 160). Slots 0-10 are used by other NPCs, so pick a free one (>= 11).
		// TODO: fill in the real candidate spawn tiles, confirm slot 11 exists in gp.npc, confirm trainers 556-561 are free.
		register(160, 11, new int[][] { { 0, 0 } /* TODO */ }, 0, Mode.RANDOM, -1, ultraParadoxPool(556));
	}
	
	private static List<Entry> ultraParadoxPool(int firstTrainerId) {
		String[] cries = { "Zuzuzuurrkiii!", "Gzssisssss!", "Bloooghawooooooooarooo!", "Cvvshugizgi!", "Azchoozlooagh!", "Xvringzzi!" };
		List<Entry> list = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			list.add(new Entry(284 + i, firstTrainerId + i, cries[i]));
		}
		return list;
	}
}