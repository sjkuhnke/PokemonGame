package docs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pokemon.Item;
import pokemon.Move;

/**
 * Maps TM/HM items (and the TM names in tm_locations.txt) to the move they
 * teach, so TM names can link to the Moves sheet / TM Locations sheet.
 *
 * The move is found from the item's NAME (e.g. "TM24 Thunderbolt" or
 * "Thunderbolt"). If Item has a direct accessor for its TM move, replace the
 * body of moveFor(Item) with it and the rest keeps working.
 */
final class TMMoves {

	static final int FIRST_TM_ITEM = 93;
	static final int LAST_TM_ITEM = 199;

	private static Map<String, Move> movesByName;
	private static List<Item> tmItems;

	private TMMoves() {}

	/** The TM/HM items, in the same order as the TM Compatibility columns. */
	static List<Item> tmItems() {
		if (tmItems == null) {
			List<Item> list = new ArrayList<>();
			for (int id = FIRST_TM_ITEM; id <= LAST_TM_ITEM; id++) list.add(Item.getItem(id));
			tmItems = list;
		}
		return tmItems;
	}

	/** The move a TM/HM item teaches, or null if it can't be worked out. */
	static Move moveFor(Item tm) {
		return tm == null ? null : moveForText(tm.toString());
	}

	/** Parses "TM24 Thunderbolt", "TM24: Thunderbolt" or "Thunderbolt" into a Move. */
	static Move moveForText(String text) {
		if (text == null) return null;
		String name = text.replaceFirst("(?i)^\\s*(TM|HM)\\s*\\d+\\s*[:\\-–.]?\\s*", "");
		if (name.isEmpty()) return null;
		return moves().get(norm(name));
	}

	/**
	 * Finds the TM item a tm_locations.txt label refers to. Tries an exact
	 * (normalized) name match first, then prefix/suffix matches.
	 */
	static Item itemForText(String label) {
		String n = norm(label);
		if (n.length() < 3) return null;
		for (Item i : tmItems()) {
			if (norm(i.toString()).equals(n)) return i;
		}
		for (Item i : tmItems()) {
			String in = norm(i.toString());
			if (in.startsWith(n) || in.endsWith(n) || n.startsWith(in) || n.endsWith(in)) return i;
		}
		return null;
	}

	private static Map<String, Move> moves() {
		if (movesByName == null) {
			Map<String, Move> map = new HashMap<>();
			for (Move m : Move.values()) map.put(norm(m.toString()), m);
			movesByName = map;
		}
		return movesByName;
	}

	private static String norm(String s) {
		return s == null ? "" : s.toLowerCase().replaceAll("[^a-z0-9]", "");
	}
}