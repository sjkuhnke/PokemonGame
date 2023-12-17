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
			int id = rand.nextInt(225);
			encounters.add(new Encounter(id + 1, 2, 5, 1));
		} else {
			if (area == 0 && type.equals("Standard") && x <= 73 && y > 38) { // route 22
				encounters.add(new Encounter(13, 2, 2, 0.26));
				encounters.add(new Encounter(16, 2, 4, 0.18));
				encounters.add(new Encounter(22, 2, 3, 0.22));
				encounters.add(new Encounter(26, 2, 4, 0.10));
				encounters.add(new Encounter(29, 3, 3, 0.24));
			} else if (area == 0 && type.equals("Standard") && x <= 73 && y <= 38) { // route 23
				encounters.add(new Encounter(10, 2, 3, 0.18));
				encounters.add(new Encounter(13, 3, 4, 0.24));
				encounters.add(new Encounter(16, 4, 4, 0.24));
				encounters.add(new Encounter(32, 2, 3, 0.20));
				encounters.add(new Encounter(38, 3, 5, 0.06));
				encounters.add(new Encounter(41, 2, 3, 0.08));
			} else if (area == 0 && type.equals("Standard") && x > 73) { // route 42
				encounters.add(new Encounter(171, 2, 2, 0.20));
				encounters.add(new Encounter(217, 2, 3, 0.16));
				encounters.add(new Encounter(223, 2, 2, 0.13));
				encounters.add(new Encounter(166, 2, 3, 0.17));
				encounters.add(new Encounter(179, 2, 3, 0.11));
				encounters.add(new Encounter(129, 2, 2, 0.15));
				encounters.add(new Encounter(143, 2, 2, 0.08));
			} else if (area == 0 && type.equals("Surfing")) { // route 42
				encounters.add(new Encounter(68, 28, 31, 0.20));
				encounters.add(new Encounter(71, 27, 30, 0.25));
				encounters.add(new Encounter(132, 28, 31, 0.25));
				encounters.add(new Encounter(215, 28, 31, 0.30));
			} else if (area == 0 && type.equals("Fishing")) { // route 42
				encounters.add(new Encounter(132, 11, 12, 0.25));
				encounters.add(new Encounter(137, 10, 11, 0.15));
				encounters.add(new Encounter(141, 12, 14, 0.10));
				encounters.add(new Encounter(146, 11, 12, 0.10));
				encounters.add(new Encounter(215, 10, 12, 0.40));
			} else if (area == 4 && y >= 43 && type.equals("Standard")) { // route 24
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
			} else if (area == 11 && type.equals("Fishing")) { // gelb forest
				encounters.add(new Encounter(90, 15, 18, 0.15));
				encounters.add(new Encounter(132, 15, 17, 0.20));
				encounters.add(new Encounter(137, 15, 16, 0.35));
				encounters.add(new Encounter(146, 16, 17, 0.30));
			} else if (area == 11 && type.equals("Surfing")) { // gelb forest
				encounters.add(new Encounter(44, 13, 15, 0.70));
				encounters.add(new Encounter(71, 16, 17, 0.20));
				encounters.add(new Encounter(132, 15, 16, 0.10));
			} else if (area == 11 && y < 55 && type.equals("Standard")) { // route 25
				encounters.add(new Encounter(16, 12, 12, 0.20));
				encounters.add(new Encounter(19, 13, 13, 0.20));
				encounters.add(new Encounter(38, 11, 13, 0.15));
				encounters.add(new Encounter(151, 11, 13, 0.20));
				encounters.add(new Encounter(80, 11, 13, 0.10));
				encounters.add(new Encounter(163, 11, 11, 0.10));
				encounters.add(new Encounter(166, 10, 12, 0.05));
			} else if (area == 13 && type.equals("Fishing")) { // sicab city
				encounters.add(new Encounter(137, 15, 18, 0.35));
				encounters.add(new Encounter(90, 15, 17, 0.25));
				encounters.add(new Encounter(146, 15, 16, 0.20));
				encounters.add(new Encounter(148, 15, 17, 0.10));
				encounters.add(new Encounter(139, 15, 17, 0.10));
			} else if (area == 13 && type.equals("Surfing")) { // sicab city
				encounters.add(new Encounter(139, 20, 20, 0.50));
				encounters.add(new Encounter(134, 20, 20, 0.25));
				encounters.add(new Encounter(78, 20, 20, 0.20));
				encounters.add(new Encounter(141, 20, 20, 0.05));
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
			} else if (area == 16 && type.equals("Fishing")) { // energy plant B
				encounters.add(new Encounter(137, 15, 15, 0.40));
				encounters.add(new Encounter(209, 15, 15, 0.15));
				encounters.add(new Encounter(148, 13, 17, 0.45));
			} else if (area == 16 && type.equals("Surfing")) { // energy plant B
				encounters.add(new Encounter(137, 25, 25, 0.20));
				encounters.add(new Encounter(138, 25, 25, 0.20));
				encounters.add(new Encounter(209, 25, 25, 0.30));
				encounters.add(new Encounter(210, 25, 25, 0.30));
			} else if (area == 22 && type.equals("Standard")) { // route 40
				encounters.add(new Encounter(10, 2, 3, 0.15));
				encounters.add(new Encounter(94, 2, 2, 0.20));
				encounters.add(new Encounter(73, 2, 3, 0.06));
				encounters.add(new Encounter(52, 3, 3, 0.02));
				encounters.add(new Encounter(104, 2, 3, 0.20));
				encounters.add(new Encounter(101, 2, 3, 0.12));
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
			} else if (area == 24 && type.equals("Standard")) { // Mt. Splinkty 1A
				encounters.add(new Encounter(48, 17, 19, 0.5));
				encounters.add(new Encounter(52, 18, 19, 0.15));
				encounters.add(new Encounter(153, 18, 19, 0.25));
				encounters.add(new Encounter(156, 18, 19, 0.10));
			} else if (area == 24 && type.equals("Fishing")) { // Mt. Splinkty 1A
				encounters.add(new Encounter(90, 17, 19, 0.15));
				encounters.add(new Encounter(137, 16, 18, 0.10));
				encounters.add(new Encounter(148, 18, 20, 0.10));
				encounters.add(new Encounter(143, 18, 20, 0.25));
				encounters.add(new Encounter(150, 20, 20, 0.40));
			} else if (area == 24 && type.equals("Surfing")) { // Mt. Splinkty 1A
				encounters.add(new Encounter(139, 20, 20, 0.60));
				encounters.add(new Encounter(132, 20, 20, 0.25));
				encounters.add(new Encounter(143, 18, 19, 0.15));
			} else if (area == 25 && type.equals("Standard")) { // Mt. Splinkty 2B
				encounters.add(new Encounter(48, 18, 19, 0.30));
				encounters.add(new Encounter(52, 19, 20, 0.25));
				encounters.add(new Encounter(156, 18, 19, 0.10));
				encounters.add(new Encounter(153, 17, 19, 0.15));
				encounters.add(new Encounter(95, 18, 19, 0.10));
				encounters.add(new Encounter(66, 19, 19, 0.10));
			} else if (area == 26 && type.equals("Standard")) { // Mt. Splinkty 3B
				encounters.add(new Encounter(156, 16, 19, 0.4));
				encounters.add(new Encounter(153, 17, 19, 0.6));
			} else if (area == 27 && type.equals("Standard")) { // Mt. Splinkty 3A
				encounters.add(new Encounter(73, 17, 19, 0.8));
				encounters.add(new Encounter(160, 16, 20, 0.2));
			} else if (area == 28 && y < 57 && type.equals("Standard")) { // Route 27
				encounters.add(new Encounter(35, 17, 19, 0.15));
				encounters.add(new Encounter(32, 16, 18, 0.1));
				encounters.add(new Encounter(45, 18, 20, 0.1));
				encounters.add(new Encounter(92, 19, 19, 0.15));
				encounters.add(new Encounter(174, 16, 17, 0.2));
				encounters.add(new Encounter(171, 16, 18, 0.05));
				encounters.add(new Encounter(179, 18, 19, 0.05));
				encounters.add(new Encounter(177, 18, 19, 0.05));
				encounters.add(new Encounter(75, 16, 18, 0.15));
			} else if (area == 28 && y >= 57 && type.equals("Fishing")) { // Route 41
				encounters.add(new Encounter(68, 16, 18, 0.25));
				encounters.add(new Encounter(134, 15, 17, 0.25));
				encounters.add(new Encounter(137, 16, 18, 0.45));
				encounters.add(new Encounter(141, 19, 20, 0.05));
			} else if (area == 28 && y >= 57 && type.equals("Surfing")) { // Route 41
				encounters.add(new Encounter(139, 20, 20, 0.50));
				encounters.add(new Encounter(134, 20, 20, 0.25));
				encounters.add(new Encounter(78, 20, 20, 0.20));
				encounters.add(new Encounter(141, 20, 20, 0.05));
			} else if (area == 28 && y >= 57 && type.equals("Standard")) { // Route 41
				encounters.add(new Encounter(71, 25, 28, 0.15));
				encounters.add(new Encounter(108, 26, 28, 0.20));
				encounters.add(new Encounter(14, 25, 28, 0.11));
				encounters.add(new Encounter(152, 26, 29, 0.15));
				encounters.add(new Encounter(61, 25, 29, 0.10));
				encounters.add(new Encounter(64, 26, 29, 0.15));
				encounters.add(new Encounter(80, 25, 28, 0.14));
			} else if (area == 4 && y < 43 && type.equals("Standard")) { // Route 36
				encounters.add(new Encounter(22, 12, 13, 0.2));
				encounters.add(new Encounter(41, 13, 14, 0.2));
				encounters.add(new Encounter(57, 15, 15, 0.15));
				encounters.add(new Encounter(82, 14, 15, 0.1));
				encounters.add(new Encounter(94, 16, 17, 0.15));
				encounters.add(new Encounter(101, 15, 17, 0.15));
				encounters.add(new Encounter(108, 18, 19, 0.05));
			} else if (area == 4 && y < 43 && type.equals("Fishing")) { // Route 36
				encounters.add(new Encounter(90, 16, 18, 0.20));
				encounters.add(new Encounter(134, 15, 17, 0.25));
				encounters.add(new Encounter(137, 16, 18, 0.45));
				encounters.add(new Encounter(139, 16, 16, 0.10));
			} else if (area == 4 && y < 43 && type.equals("Surfing")) { // Route 36
				encounters.add(new Encounter(139, 20, 20, 0.50));
				encounters.add(new Encounter(134, 20, 20, 0.25));
				encounters.add(new Encounter(78, 20, 20, 0.20));
				encounters.add(new Encounter(141, 20, 20, 0.05));
			} else if (area == 33 && y < 22 && type.equals("Standard")) { // Route 28
				encounters.add(new Encounter(26, 19, 20, 0.10));
				encounters.add(new Encounter(14, 18, 19, 0.10));
				encounters.add(new Encounter(36, 20, 20, 0.15));
				encounters.add(new Encounter(114, 18, 20, 0.15));
				encounters.add(new Encounter(117, 15, 16, 0.40));
				encounters.add(new Encounter(123, 16, 18, 0.05));
				encounters.add(new Encounter(129, 17, 18, 0.05));
			} else if (area == 35 && type.equals("Standard")) { // Electric Tunnel 01
				encounters.add(new Encounter(153, 19, 20, 0.15));
				encounters.add(new Encounter(114, 18, 19, 0.10));
				encounters.add(new Encounter(123, 17, 18, 0.10));
				encounters.add(new Encounter(197, 18, 19, 0.15));
				encounters.add(new Encounter(199, 17, 19, 0.13));
				encounters.add(new Encounter(202, 20, 20, 0.12));
				encounters.add(new Encounter(205, 18, 19, 0.25));
			} else if (area == 36 && type.equals("Standard")) { // Route 29
				encounters.add(new Encounter(11, 20, 22, 0.12));
				encounters.add(new Encounter(29, 18, 21, 0.12));
				encounters.add(new Encounter(47, 21, 21, 0.19));
				encounters.add(new Encounter(75, 19, 22, 0.10));
				encounters.add(new Encounter(86, 19, 22, 0.08));
				encounters.add(new Encounter(114, 19, 21, 0.04));
				encounters.add(new Encounter(117, 16, 18, 0.25));
				encounters.add(new Encounter(62, 18, 20, 0.06));
				encounters.add(new Encounter(59, 19, 20, 0.04));
			} else if (area == 38 && y <= 31 && type.equals("Standard")) { // Icy Fields
				encounters.add(new Encounter(59, 22, 22, 0.25));
				encounters.add(new Encounter(61, 22, 22, 0.15));
				encounters.add(new Encounter(62, 22, 22, 0.20));
				encounters.add(new Encounter(64, 22, 22, 0.20));
				encounters.add(new Encounter(71, 22, 22, 0.12));
				encounters.add(new Encounter(158, 22, 22, 0.08));
			} else if (area == 38 && y > 61 && type.equals("Standard")) { // Route 30
				encounters.add(new Encounter(11, 24, 25, 0.15));
				encounters.add(new Encounter(126, 22, 24, 0.20));
				encounters.add(new Encounter(47, 24, 24, 0.25));
				encounters.add(new Encounter(82, 23, 25, 0.15));
				encounters.add(new Encounter(89, 25, 25, 0.25));
			} else if (area == 38 && y > 61 && type.equals("Fishing")) { // Route 30
				encounters.add(new Encounter(90, 19, 19, 0.10));
				encounters.add(new Encounter(137, 16, 18, 0.25));
				encounters.add(new Encounter(134, 16, 18, 0.30));
				encounters.add(new Encounter(132, 18, 19, 0.10));
				encounters.add(new Encounter(146, 18, 19, 0.15));
				encounters.add(new Encounter(71, 19, 20, 0.10));
			} else if (area == 38 && y > 61 && type.equals("Surfing")) { // Route 30
				encounters.add(new Encounter(139, 20, 20, 0.20));
				encounters.add(new Encounter(68, 20, 20, 0.35));
				encounters.add(new Encounter(134, 20, 20, 0.20));
				encounters.add(new Encounter(78, 20, 20, 0.10));
				encounters.add(new Encounter(141, 20, 20, 0.05));
				encounters.add(new Encounter(71, 20, 20, 0.10));
			} else if (area == 33 && y > 32 && type.equals("Standard")) { // Peaceful Park
				encounters.add(new Encounter(23, 23, 25, 0.09)); // centatle
				encounters.add(new Encounter(26, 25, 26, 0.05)); // sapwin
				encounters.add(new Encounter(30, 24, 25, 0.05)); // roselia
				encounters.add(new Encounter(33, 23, 25, 0.06)); // swadloon
				encounters.add(new Encounter(62, 22, 24, 0.13)); // snom
				encounters.add(new Encounter(120, 20, 25, 0.10)); // magie
				encounters.add(new Encounter(123, 20, 25, 0.10)); // vupp
				encounters.add(new Encounter(126, 20, 25, 0.10)); // whiskie
				encounters.add(new Encounter(130, 23, 25, 0.02)); // ninjask
				encounters.add(new Encounter(89, 24, 25, 0.15)); // tigrette
				encounters.add(new Encounter(92, 23, 26, 0.12)); // flameruff
				encounters.add(new Encounter(118, 24, 25, 0.03)); // shockbranch
			} else if (area == 33 && y > 32 && type.equals("Fishing")) { // Peaceful Park
				encounters.add(new Encounter(137, 15, 18, 0.35));
				encounters.add(new Encounter(90, 19, 23, 0.25));
				encounters.add(new Encounter(146, 20, 22, 0.20));
				encounters.add(new Encounter(148, 21, 24, 0.10));
				encounters.add(new Encounter(139, 20, 23, 0.10));
			} else if (area == 33 && y > 32 && type.equals("Surfing")) { // Peaceful Park
				encounters.add(new Encounter(139, 20, 20, 0.50));
				encounters.add(new Encounter(134, 20, 20, 0.25));
				encounters.add(new Encounter(78, 20, 20, 0.20));
				encounters.add(new Encounter(141, 20, 20, 0.05));
			} else if (area == 77 && type.equals("Surfing")) { // Mindagan Lake
				encounters.add(new Encounter(68, 20, 25, 0.08));
				encounters.add(new Encounter(71, 20, 25, 0.15));
				encounters.add(new Encounter(78, 20, 25, 0.18));
				encounters.add(new Encounter(134, 20, 25, 0.13));
				encounters.add(new Encounter(135, 20, 25, 0.05));
				encounters.add(new Encounter(139, 20, 25, 0.21));
				encounters.add(new Encounter(143, 20, 25, 0.20));
			} else if (area == 77 && type.equals("Fishing")) { // Mindagan Lake
				encounters.add(new Encounter(90, 20, 25, 0.08));
				encounters.add(new Encounter(132, 20, 25, 0.08));
				encounters.add(new Encounter(137, 20, 25, 0.15));
				encounters.add(new Encounter(141, 20, 25, 0.15));
				encounters.add(new Encounter(143, 20, 25, 0.10));
				encounters.add(new Encounter(146, 20, 25, 0.07));
				encounters.add(new Encounter(148, 20, 25, 0.12));
				encounters.add(new Encounter(150, 20, 25, 0.25));
			} else if (area == 78 && type.equals("Standard")) { // Mindagan Cavern 1A
				encounters.add(new Encounter(175, 26, 30, 0.12));
				encounters.add(new Encounter(177, 26, 30, 0.13));
				encounters.add(new Encounter(184, 24, 28, 0.25));
				encounters.add(new Encounter(187, 25, 30, 0.25));
				encounters.add(new Encounter(190, 24, 28, 0.10));
				encounters.add(new Encounter(195, 26, 30, 0.15));
			} else if (area == 80 && y <= 41 && type.equals("Standard")) { // Route 31
				encounters.add(new Encounter(33, 25, 28, 0.14)); 
				encounters.add(new Encounter(42, 27, 29, 0.15));
				encounters.add(new Encounter(73, 25, 28, 0.20));
				encounters.add(new Encounter(158, 25, 28, 0.16));
				encounters.add(new Encounter(55, 26, 28, 0.25));
				encounters.add(new Encounter(66, 28, 29, 0.10));
			} else if (area == 80 && y <= 41 && type.equals("Fishing")) { // Route 31
				encounters.add(new Encounter(90, 22, 25, 0.20));
				encounters.add(new Encounter(132, 22, 25, 0.10));
				encounters.add(new Encounter(146, 22, 25, 0.20));
				encounters.add(new Encounter(141, 24, 26, 0.25));
				encounters.add(new Encounter(215, 22, 24, 0.25));
			} else if (area == 80 && y <= 41 && type.equals("Surfing")) { // Route 31
				encounters.add(new Encounter(135, 22, 24, 0.20));
				encounters.add(new Encounter(132, 24, 25, 0.15));
				encounters.add(new Encounter(143, 22, 25, 0.20));
				encounters.add(new Encounter(141, 24, 26, 0.20));
				encounters.add(new Encounter(215, 22, 24, 0.25));
			} else if (area == 83 && x <= 57 && type.equals("Standard")) { // Shadow Ravine 1A
				encounters.add(new Encounter(172, 25, 29, 0.15));
				encounters.add(new Encounter(218, 25, 29, 0.25));
				encounters.add(new Encounter(220, 26, 28, 0.08));
				encounters.add(new Encounter(224, 27, 29, 0.17));
				encounters.add(new Encounter(226, 26, 28, 0.35));
			} else if (area == 90 && type.equals("Standard")) { // Shadow Ravine 0
				encounters.add(new Encounter(169, 26, 28, 0.25));
				encounters.add(new Encounter(158, 26, 28, 0.10));
				encounters.add(new Encounter(218, 25, 29, 0.25));
				encounters.add(new Encounter(220, 26, 28, 0.08));
				encounters.add(new Encounter(224, 27, 29, 0.17));
				encounters.add(new Encounter(226, 26, 28, 0.15));
			} else if (area == 83 && x > 57 && type.equals("Standard")) { // Route 32
				encounters.add(new Encounter(11, 26, 28, 0.15));
				encounters.add(new Encounter(14, 27, 29, 0.15));
				encounters.add(new Encounter(73, 26, 28, 0.15));
				encounters.add(new Encounter(82, 26, 28, 0.20));
				encounters.add(new Encounter(57, 27, 28, 0.15));
				encounters.add(new Encounter(105, 26, 28, 0.20));
			} else if (area == 83 && x > 57 && type.equals("Fishing")) { // Route 32
				encounters.add(new Encounter(90, 24, 27, 0.20));
				encounters.add(new Encounter(132, 24, 27, 0.10));
				encounters.add(new Encounter(146, 24, 27, 0.20));
				encounters.add(new Encounter(141, 27, 29, 0.25));
				encounters.add(new Encounter(215, 25, 27, 0.25));
			} else if (area == 83 && x > 57 && type.equals("Surfing")) { // Route 32
				encounters.add(new Encounter(135, 24, 26, 0.20));
				encounters.add(new Encounter(132, 24, 27, 0.15));
				encounters.add(new Encounter(143, 24, 27, 0.20));
				encounters.add(new Encounter(141, 27, 29, 0.20));
				encounters.add(new Encounter(215, 25, 27, 0.25));
			} else if (area == 95 && type.equals("Standard")) { // Electric 0
				encounters.add(new Encounter(124, 32, 35, 0.15));
				encounters.add(new Encounter(198, 32, 35, 0.20));
				encounters.add(new Encounter(200, 32, 35, 0.20));
				encounters.add(new Encounter(202, 30, 34, 0.20));
				encounters.add(new Encounter(203, 35, 36, 0.05));
				encounters.add(new Encounter(206, 32, 35, 0.20));
			} else if ((area == 95 || area == 98) && (type.equals("Surfing") || type.equals("Fishing"))) { // Electric 0/Electric -3
				encounters.add(new Encounter(209, 15, 15, 0.80));
				encounters.add(new Encounter(210, 35, 35, 0.20));
			} else if ((area == 96 || area == 99) && type.equals("Standard")) { // Electric -1/Electric H
				encounters.add(new Encounter(53, 35, 38, 0.25));
				encounters.add(new Encounter(198, 35, 38, 0.21));
				encounters.add(new Encounter(200, 35, 38, 0.19));
				encounters.add(new Encounter(203, 35, 38, 0.10));
				encounters.add(new Encounter(206, 32, 35, 0.25));
			} else if (area == 97 && type.equals("Standard")) { // Electric -2
				encounters.add(new Encounter(170, 39, 43, 0.20));
				encounters.add(new Encounter(172, 38, 42, 0.20));
				encounters.add(new Encounter(198, 39, 43, 0.13));
				encounters.add(new Encounter(200, 39, 43, 0.12));
				encounters.add(new Encounter(203, 39, 43, 0.20));
				encounters.add(new Encounter(206, 39, 43, 0.15));
			} else if (area == 98 && type.equals("Standard")) { // Electric -3
				encounters.add(new Encounter(198, 42, 46, 0.40));
				encounters.add(new Encounter(200, 42, 46, 0.20));
				encounters.add(new Encounter(203, 42, 46, 0.20));
				encounters.add(new Encounter(206, 42, 46, 0.20));
			} else if (area == 101 || area == 100 && type.equals("Standard")) { // Shadow Ravine -1/Shadow Ravine H
				encounters.add(new Encounter(170, 33, 36, 0.20));
				encounters.add(new Encounter(74, 33, 36, 0.20));
				encounters.add(new Encounter(218, 32, 35, 0.25));
				encounters.add(new Encounter(220, 33, 36, 0.10));
				encounters.add(new Encounter(224, 32, 35, 0.10));
				encounters.add(new Encounter(227, 32, 36, 0.15));
			} else if (area == 101 && type.equals("Fishing")) { // Shadow Ravine -1
				encounters.add(new Encounter(215, 29, 32, 0.75));
				encounters.add(new Encounter(216, 35, 40, 0.25));
			} else if (area == 101 && type.equals("Surfing")) { // Shadow Ravine -1
				encounters.add(new Encounter(215, 29, 32, 0.90));
				encounters.add(new Encounter(216, 35, 40, 0.10));
			} else if (area == 102 && type.equals("Standard")) { // Shadow Ravine -2
				encounters.add(new Encounter(170, 35, 37, 0.25));
				encounters.add(new Encounter(218, 34, 36, 0.25));
				encounters.add(new Encounter(220, 34, 37, 0.30));
				encounters.add(new Encounter(225, 36, 36, 0.05));
				encounters.add(new Encounter(227, 34, 37, 0.15));
			} else if (area == 102 && type.equals("Lava")) { // Shadow Ravine -2
				encounters.add(new Encounter(223, 30, 30, 0.20));
				encounters.add(new Encounter(224, 40, 40, 0.70));
				encounters.add(new Encounter(225, 50, 50, 0.10));
			} else if (area == 103 && type.equals("Standard")) { // Shadow Ravine -3
				encounters.add(new Encounter(219, 50, 55, 0.25));
				encounters.add(new Encounter(220, 46, 49, 0.20));
				encounters.add(new Encounter(221, 49, 53, 0.15));
				encounters.add(new Encounter(222, 55, 55, 0.05));
				encounters.add(new Encounter(225, 49, 55, 0.25));
				encounters.add(new Encounter(227, 50, 54, 0.10));
			} else if (area == 105 && type.equals("Standard")) { // Shadow Path
				encounters.add(new Encounter(198, 40, 45, 0.11));
				encounters.add(new Encounter(200, 40, 45, 0.10));
				encounters.add(new Encounter(203, 40, 45, 0.10));
				encounters.add(new Encounter(206, 40, 45, 0.04));
				encounters.add(new Encounter(207, 40, 45, 0.05));
				encounters.add(new Encounter(208, 40, 45, 0.05));
				
				encounters.add(new Encounter(218, 40, 44, 0.12));
				encounters.add(new Encounter(219, 45, 45, 0.02));
				encounters.add(new Encounter(220, 40, 45, 0.10));
				encounters.add(new Encounter(221, 40, 45, 0.06));
				encounters.add(new Encounter(222, 40, 45, 0.02));
				encounters.add(new Encounter(225, 40, 45, 0.11));
				encounters.add(new Encounter(227, 40, 45, 0.10));
				
				encounters.add(new Encounter(212, 45, 45, 0.01));
				encounters.add(new Encounter(214, 45, 45, 0.01));
			} else if (area == 103 && type.equals("Lava")) { // Shadow Ravine -3
				encounters.add(new Encounter(225, 55, 55, 1.0));
			} else if (area == 85 && type.equals("Standard")) { // Route 33
				encounters.add(new Encounter(20, 34, 38, 0.15));
				encounters.add(new Encounter(80, 34, 38, 0.11));
				encounters.add(new Encounter(93, 35, 39, 0.16));
				encounters.add(new Encounter(105, 35, 39, 0.13));
				encounters.add(new Encounter(107, 35, 39, 0.20));
				encounters.add(new Encounter(164, 34, 39, 0.25));
			} else if (area == 85 && type.equals("Surfing")) { // Route 33
				encounters.add(new Encounter(79, 35, 38, 0.30));
				encounters.add(new Encounter(133, 34, 38, 0.40));
				encounters.add(new Encounter(134, 37, 39, 0.15));
				encounters.add(new Encounter(136, 37, 39, 0.15));
			} else if (area == 85 && type.equals("Fishing")) { // Route 33
				encounters.add(new Encounter(137, 15, 25, 0.25));
				encounters.add(new Encounter(138, 30, 35, 0.10));
				encounters.add(new Encounter(141, 30, 35, 0.35));
				encounters.add(new Encounter(148, 20, 30, 0.30));
			} else if (area == 107 && x < 88 && y >= 17 && type.equals("Standard")) { // Ghostly Woods
				encounters.add(new Encounter(24, 36, 39, 0.05));
				encounters.add(new Encounter(27, 36, 39, 0.07));
				encounters.add(new Encounter(159, 37, 40, 0.10));
				encounters.add(new Encounter(160, 35, 38, 0.12));
				encounters.add(new Encounter(161, 37, 40, 0.02));
				encounters.add(new Encounter(74, 36, 39, 0.05));
				encounters.add(new Encounter(76, 36, 39, 0.02));
				encounters.add(new Encounter(152, 36, 39, 0.12));
				encounters.add(new Encounter(154, 35, 38, 0.10));
				encounters.add(new Encounter(157, 36, 39, 0.06));
				encounters.add(new Encounter(180, 37, 38, 0.07));
				encounters.add(new Encounter(220, 35, 39, 0.09));
				encounters.add(new Encounter(221, 37, 40, 0.02));
				encounters.add(new Encounter(216, 40, 40, 0.05));
				encounters.add(new Encounter(227, 36, 39, 0.06));
			} else if (area == 110 && type.equals("Standard")) { // Route 34
				encounters.add(new Encounter(53, 40, 42, 0.17));
				encounters.add(new Encounter(64, 37, 41, 0.13));
				encounters.add(new Encounter(65, 41, 44, 0.02));
				encounters.add(new Encounter(56, 40, 43, 0.12));
				encounters.add(new Encounter(67, 41, 44, 0.10));
				encounters.add(new Encounter(167, 40, 43, 0.15));
				encounters.add(new Encounter(182, 40, 43, 0.14));
				encounters.add(new Encounter(195, 40, 42, 0.17));
			} else if (area == 110 && type.equals("Lava")) { // Route 34
				encounters.add(new Encounter(98, 40, 40, 1.0));
			} else if (area == 115 && type.equals("Standard")) { // Route 35
				encounters.add(new Encounter(86, 40, 42, 0.15));
				encounters.add(new Encounter(87, 37, 41, 0.05));
				encounters.add(new Encounter(89, 41, 44, 0.14));
				encounters.add(new Encounter(12, 40, 43, 0.13));
				encounters.add(new Encounter(38, 41, 44, 0.14));
				encounters.add(new Encounter(108, 40, 43, 0.21));
				encounters.add(new Encounter(121, 40, 43, 0.09));
				encounters.add(new Encounter(127, 40, 42, 0.09));
			} else if (area == 117 && type.equals("Standard")) { // Mindagan Cavern 0
				encounters.add(new Encounter(175, 26, 30, 0.12));
				encounters.add(new Encounter(177, 26, 30, 0.13));
				encounters.add(new Encounter(184, 24, 28, 0.25));
				encounters.add(new Encounter(187, 25, 30, 0.25));
				encounters.add(new Encounter(190, 24, 28, 0.10));
				encounters.add(new Encounter(195, 26, 30, 0.15));
			}
			
			
		}
		
		double total = 0;
		for (Encounter e : encounters) {
			total += e.encounterChance;
		}
		if (!String.format("%.2f", total).equals("1.00")) System.out.println("Encounters do not add up to 100: area " + area + ", instead " + total);
		return encounters;
	}

}
