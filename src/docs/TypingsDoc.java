package docs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pokemon.PType;
import pokemon.Pokemon;

public class TypingsDoc {

	static Map<PType, Double> computeDefensiveMap(Pokemon test, Pokemon foe, PType[] types) {
		Map<PType, Double> map = new HashMap<>();
		for (PType type3 : types) {
			double multiplier = 1;
			if (test.getImmune(foe, type3)) {
				multiplier = 0;
			} else {
				for (PType t : test.getResistances(type3)) {
					if (foe.type1 == t) multiplier /= 2;
					if (foe.type2 == t) multiplier /= 2;
				}
				for (PType t : test.getWeaknesses(type3)) {
					if (foe.type1 == t) multiplier *= 2;
					if (foe.type2 == t) multiplier *= 2;
				}
			}
			map.put(type3, multiplier);
		}
		return map;
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeDefensiveTypes); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeDefensiveTypingsToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("DefensiveTypings.txt");
			FileWriter writer = new FileWriter(outPath.toFile());

			PType[] types = getUsableTypes();

			Pokemon test = new Pokemon(1, 5, true, false);
			Pokemon foe = new Pokemon(4, 5, true, false);

			writer.write("TYPE COMBINATIONS (Defensively)\n");
			for (PType type1 : types) {
				for (PType type2 : types) {
					writer.write("\n===========================\n");
					String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
					writer.write(combination + "\n===========================\n");

					foe.type1 = type1;
					foe.type2 = type2;
					Map<PType, Double> typeEffectivenessMap = computeDefensiveMap(test, foe, types);

					writer.write("Immune to:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() == 0.0) {
							writer.write(entry.getKey().toString() + "\n");
						}
					}

					writer.write("\n----------\nWeak to:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() >= 2.0) {
							writer.write(entry.getKey().toString());
							if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
							writer.write("\n");
						}
					}

					writer.write("\n----------\nResists:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() < 1.0 && entry.getValue() != 0) {
							writer.write(entry.getKey().toString());
							if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
							writer.write("\n");
						}
					}
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Map<PType, Double> computeOffensiveMap(Pokemon test, Pokemon foe, PType type1, PType type2, PType[] types) {
		Map<PType, Double> map = new HashMap<>();
		for (PType type3 : types) {
			foe.type1 = type3;
			foe.type2 = null;
			double multiplier = 1;
			if (test.getImmune(foe, type1) && test.getImmune(foe, type2)) {
				multiplier = 0;
			} else {
				ArrayList<PType> resist1 = new ArrayList<>(Arrays.asList(test.getResistances(type1)));
				ArrayList<PType> resist2 = new ArrayList<>(Arrays.asList(test.getResistances(type2)));
				if ((resist1.contains(type3) && resist2.contains(type3))
						|| (test.getImmune(foe, type1) && resist2.contains(type3))
						|| (resist1.contains(type3) && test.getImmune(foe, type2))) {
					multiplier /= 2;
				}

				ArrayList<PType> weak1 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type1)));
				ArrayList<PType> weak2 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type2)));
				if (weak1.contains(type3) && weak2.contains(type3)) {
					multiplier *= 4;
				} else if (weak1.contains(type3) || weak2.contains(type3)) {
					multiplier *= 2;
				}
			}
			map.put(type3, multiplier);
		}
		return map;
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeOffensiveTypes); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeOffensiveTypingsToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("OffensiveTypings.txt");
			FileWriter writer = new FileWriter(outPath.toFile());

			PType[] types = getUsableTypes();

			Pokemon test = new Pokemon(1, 5, true, false);
			Pokemon foe = new Pokemon(4, 5, true, false);

			writer.write("TYPE COMBINATIONS (Offensively)\n");
			for (PType type1 : types) {
				for (PType type2 : types) {
					writer.write("\n===========================\n");
					String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
					writer.write(combination + "\n===========================\n");

					Map<PType, Double> typeEffectivenessMap = computeOffensiveMap(test, foe, type1, type2, types);

					writer.write("Deals 2x to:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() >= 2.0) {
							writer.write(entry.getKey().toString());
							if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
							writer.write("\n");
						}
					}

					writer.write("\n----------\nDeals 1/2x to:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() < 1.0 && entry.getValue() != 0) {
							writer.write(entry.getKey().toString());
							if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
							writer.write("\n");
						}
					}

					writer.write("\n----------\nDeals 0x to:\n\n");
					for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
						if (entry.getValue() == 0.0) {
							writer.write(entry.getKey().toString() + "\n");
						}
					}
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =====================================================================
	// Shared helpers
	// =====================================================================
	static PType[] getUsableTypes() {
		PType[] types = PType.values();
		ArrayList<PType> list = new ArrayList<>(Arrays.asList(types));
		list.remove(PType.UNKNOWN);
		return list.toArray(new PType[0]);
	}

	interface Predicate1 {
		boolean test(double v);
	}

	// Builds the comma-joined cell text for one bucket (immune/weak/resist,
	// or deals-2x/deals-half/deals-0x), appending "(!!)" per-entry the same
	// way the original txt writers flagged quad weaknesses/quarter resists.
	static String joinTypes(Map<PType, Double> map, Predicate1 include, PType type1, PType type2, boolean flagExtreme) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<PType, Double> entry : map.entrySet()) {
			if (!include.test(entry.getValue())) continue;
			if (sb.length() > 0) sb.append(", ");
			sb.append(entry.getKey().toString());
			if (flagExtreme && type1 != type2 && (entry.getValue() == 4.0 || entry.getValue() == 0.25)) {
				sb.append(" (!!)");
			}
		}
		return sb.length() == 0 ? "\u2014" : sb.toString();
	}

}