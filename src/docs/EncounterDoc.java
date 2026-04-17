package docs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import entity.PlayerCharacter;
import overworld.GamePanel;
import overworld.PMap;
import overworld.Script;
import pokemon.Encounter;
import pokemon.Pokemon;

public class EncounterDoc {
	public static void writeEncounters(GamePanel gp, Path dir) {
	    try {
	        Path outPath = dir.resolve("WildPokemon.txt");
	        FileWriter writer = new FileWriter(outPath.toFile());

	        Map<String, ArrayList<GiftEncounter>> giftMap = buildGiftLocationMap(gp);

	        ArrayList<Encounter> allEncounters = new ArrayList<>();
	        int[] amounts = new int[Pokemon.MAX_POKEMON + 1];
	        double[] chances = new double[Pokemon.MAX_POKEMON + 1];

	        // Track which locations had wild encounters, so we can write
	        // gift-only locations separately afterward
	        Set<String> writtenLocations = new LinkedHashSet<>();

	        for (Map.Entry<String, ArrayList<Encounter>> e : Encounter.encounters.entrySet()) {
	            String[] parts = e.getKey().split("\\|");
	            String area = parts[0];
	            String type = parts[1];

	            String typeName;
	            switch (type) {
	                case "G": typeName = "Standard"; break;
	                case "F": typeName = "Fish";     break;
	                case "S": typeName = "Surf";     break;
	                case "L": typeName = "Lava";     break;
	                default:  typeName = "Unknown";  break;
	            }

	            writer.write(area + " (" + typeName + ")\n");
	            writer.write(writeEncounter(e.getValue()));
	            allEncounters.addAll(e.getValue());
	            writtenLocations.add(area);

	            // Append gifts for this area inline, after its wild encounters
	            if (giftMap.containsKey(area)) {
	            	writer.write("-------------------------------------------------------------------\n");
	                writer.write(writeGiftSection(giftMap.get(area)));
	            }
	            writer.write("===================================================================\n");
	        }

	        // Write gift-only locations that had no wild encounters
	        for (Map.Entry<String, ArrayList<GiftEncounter>> e : giftMap.entrySet()) {
	            if (!writtenLocations.contains(e.getKey())) {
	                writer.write(e.getKey() + " (Gift Only)\n");
	                writer.write("===================================================================\n");
	                writer.write(writeGiftSection(e.getValue()));
	                writer.write("===================================================================\n");
	            }
	        }

	        for (Encounter e : allEncounters) {
	            int index = e.getId();
	            amounts[index]++;
	            chances[index] += e.getEncounterChance();
	        }
	        
	        writer.write("ID,species,amt,avg");
	        for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
	            double average = amounts[i] == 0 ? 0.0 : chances[i] / amounts[i] * 100;
	            writer.write(i + "," + Pokemon.getName(i) + "," + amounts[i] + "," + String.format("%.2f", average) + "%\n");
	        }

	        writer.close();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}

	private static String writeGiftSection(ArrayList<GiftEncounter> gifts) {
	    StringBuilder sb = new StringBuilder();
	    int gift = 0;
	    for (GiftEncounter g : gifts) {
	    	if (gift > 0) sb.append("-------------------------------------------------------------------\n");
	        sb.append("GIFT:\n");
	        switch (g.type) {
	            case FIXED:
	                sb.append("  Gift: ");
	                for (int i = 0; i < g.possibleIds.length; i++) {
	                    if (i > 0) sb.append(" / ");
	                    sb.append(Pokemon.getName(g.possibleIds[i]));
	                }
	                sb.append("\n");
	                break;
	            case TABLE:
	                sb.append("  Possible Pokemon (").append(g.possibleIds.length).append(" options):\n");
	                for (int id : g.possibleIds) {
	                    sb.append("    - ").append(Pokemon.getName(id)).append("\n");
	                }
	                break;
	            case UNREGISTERED_BASE:
	                sb.append("  Pool: Random unregistered non-legendary base Pokemon\n");
	                break;
	        }
	        if (g.level > 0) sb.append("  Level: ").append(g.level).append("\n");
	        if (g.notes != null && !g.notes.isEmpty()) sb.append("  Notes: ").append(g.notes).append("\n");
	        gift++;
	    }
	    return sb.toString();
	}

	private static String writeEncounter(ArrayList<Encounter> encounters) {
		String result = "===================================================================\n";
		for (Encounter e : encounters) {
			Pokemon ep = new Pokemon(e.getId(), 5, false, false);
			String percent = String.format("%.0f", e.getEncounterChance() * 100);
			boolean sameLv = e.getMinLevel() == e.getMaxLevel();
			result += percent;
			result += "% ";
			result += ep.name();
			result += " (Lv. ";
			if (sameLv) {
				result += e.getMinLevel();
			} else {
				result += e.getMinLevel();
				result += " - ";
				result += e.getMaxLevel();
			}
			result += ")\n";
		}
		return result;
	}
	
	private static Map<String, ArrayList<GiftEncounter>> buildGiftLocationMap(GamePanel gp) {
	    Map<String, ArrayList<GiftEncounter>> giftMap = new LinkedHashMap<>();
	    for (GiftEncounter g : Script.giftEncounters) {
	        int mapNum = g.coordinates[0];
	        int tileX = g.coordinates[1];
	        int tileY = g.coordinates[2];
	        PMap.getLoc(mapNum, tileX, tileY);
	        String location = PlayerCharacter.currentMapName;
	        giftMap.computeIfAbsent(location, k -> new ArrayList<>()).add(g);
	    }
	    return giftMap;
	}
}
