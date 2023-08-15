package Swing;

import java.util.ArrayList;
import java.util.Random;

public class Encounter {
	private int id;
	private int minLevel;
	private int maxLevel;
	private double encounterChance;
	
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

	public static ArrayList<Encounter> getEncounters(int area, int x, int y, String type, String time, boolean random) {
		ArrayList<Encounter> encounters = new ArrayList<>();
		if (random) {
			Random rand = new Random();
			int id = rand.nextInt(197);
			encounters.add(new Encounter(id, 2, 5, 1));
		} else {
			if (area == 0 && type.equals("Standard") && y > 38) { // route 22
				encounters.add(new Encounter(13, 2, 2, 0.26));
				encounters.add(new Encounter(16, 2, 4, 0.18));
				encounters.add(new Encounter(22, 2, 3, 0.22));
				encounters.add(new Encounter(26, 2, 4, 0.10));
				encounters.add(new Encounter(29, 3, 3, 0.24));
			} else if (area == 0 && type.equals("Standard") && y <= 38) { // route 23
				encounters.add(new Encounter(10, 2, 3, 0.18));
				encounters.add(new Encounter(13, 3, 4, 0.24));
				encounters.add(new Encounter(16, 4, 4, 0.24));
				encounters.add(new Encounter(32, 2, 3, 0.20));
				encounters.add(new Encounter(38, 3, 5, 0.06));
				encounters.add(new Encounter(41, 2, 3, 0.08));
			} else if (area == 4 && type.equals("Standard")) { // route 24
				encounters.add(new Encounter(19, 5, 5, 0.10));
				encounters.add(new Encounter(29, 3, 5, 0.10));
				encounters.add(new Encounter(44, 3, 5, 0.15));
				encounters.add(new Encounter(35, 3, 5, 0.15));
				encounters.add(new Encounter(38, 4, 6, 0.05));
				encounters.add(new Encounter(48, 4, 6, 0.25));
				encounters.add(new Encounter(73, 4, 5, 0.05));
				encounters.add(new Encounter(85, 3, 5, 0.10));
				encounters.add(new Encounter(114, 4, 6, 0.05));
			} else if (area == 11 && x > 43 && y >= 55 && type.equals("Standard")) { // route 24 pt. 2
				encounters.add(new Encounter(10, 8, 9, 0.30));
				encounters.add(new Encounter(55, 10, 10, 0.03));
				encounters.add(new Encounter(75, 8, 10, 0.12));
				encounters.add(new Encounter(90, 9, 10, 0.10));
				encounters.add(new Encounter(174, 8, 9, 0.05));
				encounters.add(new Encounter(62, 8, 8, 0.05));
				encounters.add(new Encounter(151, 9, 10, 0.20));
				encounters.add(new Encounter(22, 9, 10, 0.15));
			} else if (area == 11 && x <= 43 && y >= 55 && type.equals("Standard")) { // gelb forest
				encounters.add(new Encounter(22, 12, 14, 0.15));
				encounters.add(new Encounter(26, 13, 17, 0.12));
				encounters.add(new Encounter(29, 12, 16, 0.08));
				encounters.add(new Encounter(30, 15, 16, 0.02));
				encounters.add(new Encounter(32, 12, 16, 0.10));
				encounters.add(new Encounter(35, 14, 17, 0.05));
				encounters.add(new Encounter(44, 13, 15, 0.12));
				encounters.add(new Encounter(47, 15, 17, 0.03));
				encounters.add(new Encounter(38, 14, 18, 0.08));
				encounters.add(new Encounter(41, 12, 14, 0.04));
				encounters.add(new Encounter(42, 15, 16, 0.01));
				encounters.add(new Encounter(82, 14, 17, 0.01));
				encounters.add(new Encounter(129, 12, 15, 0.05));
				encounters.add(new Encounter(160, 14, 17, 0.05));
				encounters.add(new Encounter(153, 16, 17, 0.09));
			} else if (area == 11 && y < 55 && type.equals("Standard")) { // route 25
				encounters.add(new Encounter(16, 12, 12, 0.25));
				encounters.add(new Encounter(19, 13, 13, 0.20));
				encounters.add(new Encounter(38, 11, 13, 0.15));
				encounters.add(new Encounter(151, 11, 13, 0.20));
				encounters.add(new Encounter(80, 11, 13, 0.05));
				encounters.add(new Encounter(163, 11, 11, 0.05));
				encounters.add(new Encounter(166, 10, 12, 0.05));
			} else if (area == 14 && type.equals("Standard")) { // energy plant A
				encounters.add(new Encounter(111, 12, 14, 0.50));
				encounters.add(new Encounter(123, 12, 12, 0.30));
				encounters.add(new Encounter(181, 15, 15, 0.15));
				encounters.add(new Encounter(199, 15, 15, 0.05));
			} else if (area == 16 && type.equals("Standard")) { // energy plant B
				encounters.add(new Encounter(111, 12, 14, 0.40));
				encounters.add(new Encounter(123, 12, 12, 0.10));
				encounters.add(new Encounter(163, 15, 15, 0.45));
				encounters.add(new Encounter(205, 15, 15, 0.05));
			} else if (area == 22 && type.equals("Standard")) { // route 40
				encounters.add(new Encounter(10, 2, 3, 0.15));
				encounters.add(new Encounter(94, 2, 2, 0.20));
				encounters.add(new Encounter(73, 2, 3, 0.06));
				encounters.add(new Encounter(52, 3, 3, 0.02));
				encounters.add(new Encounter(104, 2, 3, 0.24));
				encounters.add(new Encounter(163, 2, 3, 0.08));
				encounters.add(new Encounter(166, 3, 3, 0.05));
				encounters.add(new Encounter(174, 2, 2, 0.20));
			} else if (area == 13 && type.equals("Standard")) { // route 26
				encounters.add(new Encounter(26, 14, 18, 0.20));
				encounters.add(new Encounter(19, 16, 18, 0.10));
				encounters.add(new Encounter(47, 15, 15, 0.15));
				encounters.add(new Encounter(48, 14, 18, 0.15));
				encounters.add(new Encounter(80, 15, 17, 0.15));
				encounters.add(new Encounter(85, 15, 17, 0.20));
				encounters.add(new Encounter(106, 16, 17, 0.05));
			} else if (area == 6 && type.equals("Standard")) {
				encounters.add(new Encounter(10, 2, 3, 0.4));
				encounters.add(new Encounter(12, 3, 3, 0.2));
				encounters.add(new Encounter(33, 2, 3, 0.3));
			}
		}
		
		
		return encounters;
	}

}
