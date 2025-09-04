package pokemon;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import entity.PlayerCharacter;

public class Encounter {
	private int id;
	private int minLevel;
	private int maxLevel;
	private double encounterChance;
	
	public static LinkedHashMap<String, ArrayList<Encounter>> encounters = new LinkedHashMap<>();
	
	public Encounter(int id, int minLevel, int maxLevel, double encounterChance) {
		this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.encounterChance = encounterChance;
	}

	public double getEncounterChance() {
		return encounterChance;
	}

	public int getId() {
		return id;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public static ArrayList<Encounter> getEncounters(String area, char type, boolean random) {
		String key = area + "|" + type;
		ArrayList<Encounter> en = encounters.get(key);
		
		if (en == null) en = new ArrayList<>();
		
		double total = 0;
		for (Encounter e : en) {
			total += e.encounterChance;
		}
		if (!String.format("%.2f", total).equals("1.00") && total > 0) System.out.println("Encounters do not add up to 100: area " + area + ", instead " + total);
		return en;
	}

	public static ArrayList<ArrayList<Encounter>> getAllEncounters() {		
		ArrayList<Encounter> encountersReg = getEncounters(PlayerCharacter.currentMapName, 'G', false);
		ArrayList<Encounter> encountersFish = getEncounters(PlayerCharacter.currentMapName, 'F', false);
		ArrayList<Encounter> encountersSurf = getEncounters(PlayerCharacter.currentMapName, 'S', false);
		ArrayList<Encounter> encountersLava = getEncounters(PlayerCharacter.currentMapName, 'L', false);
		
		ArrayList<ArrayList<Encounter>> result = new ArrayList<>(4);
		
		result.add(encountersReg);
		result.add(encountersFish);
		result.add(encountersSurf);
		result.add(encountersLava);
		
		return result;
	}
	
	@Override
	public String toString() {
		return Pokemon.getName(getId());
	}

}
