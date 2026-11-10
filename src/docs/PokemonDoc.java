package docs;

import pokemon.*;
import pokemon.Nursery.EggGroup;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import java.nio.file.Path;

public class PokemonDoc {


	/**
	 * Every species id in doc order (Pokedex 1-4, plus the alt forms 237 and 291
	 * right after 150 and 290). Shared by the TXT writer and PokemonSection.
	 */
	static int[] collectIds() {
		int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE + 2];
		int counter = 0;
		for (Pokemon p : Player.pokedex1) {
			ids[counter++] = p.getID();
			if (p.getID() == 150) ids[counter++] = 237;
			if (p.getID() == 290) ids[counter++] = 291;
		}
		for (Pokemon p : Player.pokedex2) ids[counter++] = p.getID();
		for (Pokemon p : Player.pokedex3) ids[counter++] = p.getID();
		for (Pokemon p : Player.pokedex4) ids[counter++] = p.getID();
		return ids;
	}

	public static void writePokemonToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			
			int[] ids = collectIds();
			
			for (int i : ids) {
				Pokemon p = new Pokemon(i, 5, false, false);
				writer.write("===================\n");
				String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
				String name = dexNo + " - " + p.name();
				while (name.length() < 103) {
					name = name + " ";
				}
				name = name + Pokemon.getFormattedDexNo(p.getID()).replace('#', '[') + "]\n";
				writer.write(name);
				writer.write("===================\n");
				
				writer.write("Type:\n");
				String type = p.type1.toString() + " / ";
				type = p.type2 == null ? type + "None" : type + p.type2.toString();
				writer.write(type + "\n\n");
				
				writer.write("Ability:\n");
				StringBuilder abilityBuilder = new StringBuilder();
				p.setAbility(0);
				Ability ability1 = p.ability;
				abilityBuilder.append(ability1.toString()).append(" / ");
				p.setAbility(1);
				if (p.ability == ability1) {
					abilityBuilder.append("None");
				} else {
					abilityBuilder.append(p.ability.toString());
				}
				p.setAbility(2);
				abilityBuilder.append(" / (");
				if (p.ability == Ability.NULL || p.ability == ability1) {
					abilityBuilder.append("None");
				} else {
					abilityBuilder.append(p.ability.toString());
				}
				abilityBuilder.append(")\n\n");
				writer.write(abilityBuilder.toString());
				
				writer.write("Base Stats:\n");
				String stats = "";
				for (int j = 0; j < p.baseStats.length; j++) {
					stats += p.getBaseStat(j) + " " + Pokemon.getStatType(j, false) + "/ ";
				}
				stats += p.getBST() + " BST";
				writer.write(stats + "\n\n");
				
				writer.write("Level Up:\n");
				Node[] movebank = p.getMovebank();
				for (int j = 0; j < movebank.length; j++) {
					Node n = movebank[j];
					while (n != null) {
						String level = j == 0 ? "E" : j + "";
						String move = level + " - " + n.data.toString() + "\n";
						n = n.next;
						writer.write(move);
					}
				}
				writer.write("\n");
				if (p.canEvolve()) {
					writer.write("Evolutions:\n");
					writer.write(p.getEvolveString() + "\n\n");
				}
				
				writer.write(String.format("%-13s| %s lbs\n", "Weight", p.weight));
				writer.write(String.format("%-13s| %d\n", "Catch Rate", p.catchRate));
				writer.write(String.format("%-13s| %d\n", "Egg Cycles", Egg.computeEggCycles(p.getFinalEvolution())));

				ArrayList<EggGroup> eggGroups = Pokemon.getEggGroup(i);
				String eggGroupStr = eggGroups.get(0).equals(eggGroups.get(1)) ?
					eggGroups.get(0).toString() :
					eggGroups.get(0) + ", " + eggGroups.get(1);
				writer.write(String.format("%-13s| %s\n", "Egg Group(s)", eggGroupStr));
				
				writer.write("\n");
				
			}
			writer.close();
			
			writeTMLearn(dir);
			//writeUnusedMoves(dir);
			//writeTypeStats(dir);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeTMLearn(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("TM Learnsets:\n");
			
			boolean[][] tms = Pokemon.getTMTable();
			int id = 1;
			
			StringBuilder header = new StringBuilder("====================================================================================\n");
			header.append("ID   Name			   ");
			for (int hm = 1; hm <= 8; hm++) {
				header.append(String.format("HM%02d  ", hm));
			}
			for (int tm = 1; tm <= 99; tm++) {
				header.append(String.format("TM%02d  ", tm));
			}
			header.append("\n");
			header.append("====================================================================================\n");
			writer.write(header.toString());
			
			for (boolean[] row : tms) {
				if (id % 25 == 0) {
					writer.write(header.toString());
				}
				StringBuilder rowBuilder = new StringBuilder();
				String pokemonName = Pokemon.getName(id);
				rowBuilder.append(String.format("#%03d %-20s", id, pokemonName));
				for (boolean canLearn : row) {
					rowBuilder.append(canLearn ? "Y	 " : "N	 ");
				}
				rowBuilder.append("\n");
				id++;
				writer.write(rowBuilder.toString());
			}
			writer.write("====================================================================================\n");
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void writeTypeStats(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("Stats:\n");
			ArrayList<PType> types = new ArrayList<>(Arrays.asList(PType.values()));
			types.remove(PType.UNKNOWN);
			int[][] sums = new int[types.size()][6];
			int[] amounts = new int[types.size()];
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				for (int j = 0; j < p.baseStats.length; j++) {
					sums[types.indexOf(p.type1)][j] += p.baseStats[j];
					if (p.type2 != null) sums[types.indexOf(p.type2)][j] += p.baseStats[j];
				}
				amounts[types.indexOf(p.type1)]++;
				if (p.type2 != null) amounts[types.indexOf(p.type2)]++;
			}
			StringBuilder header = new StringBuilder();
			header.append("Type");
			for (int i = 0; i < sums[1].length; i++) {
				header.append("," + Pokemon.getStatType(i, false).trim());
			}
			header.append(",Amt\n");
			writer.write(header.toString());
			for (int i = 0; i < types.size(); i++) {
				StringBuilder stats = new StringBuilder();
				stats.append(types.get(i).toString());
				for (int j = 0; j < sums[1].length; j++) {
					double average = sums[i][j] * 1.0;
					average /= amounts[i];
					stats.append("," + String.format("%.1f", average));
				}
				stats.append("," + amounts[i]);
				stats.append("\n");
				writer.write(stats.toString());
			}
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void printIntArray2D(int[][] array) {
		// Iterate over each row of the 2D array
		for (int i = 0; i < array.length; i++) {
			// Iterate over each element in the current row
			for (int j = 0; j < array[i].length; j++) {
				// Print the current element followed by a space
				System.out.print(array[i][j] + " ");
			}
			// Move to the next line after printing all elements in the current row
			System.out.println();
		}
	}

	@SuppressWarnings("unused")
	private static void writeUnusedMoves(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("Unused moves:\n");
			ArrayList<Move> unused = new ArrayList<>();
			ArrayList<Move> unusedButTM = new ArrayList<>();
			Map<Pokemon, Move> sigOne = new HashMap<>();
			Map<Pokemon, Move> sigTwo = new HashMap<>();
			Map<Pokemon, Move> sigThree = new HashMap<>();
			Map<Move, Integer> moveCount = new HashMap<>();
			for (Move m : Move.getAllMoves()) {
				moveCount.put(m, 0);
			}
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				ArrayList<Move> movebank = new ArrayList<>();
				Node[] pokemonMovebank = p.getMovebank();
				for (int j = 0; j < pokemonMovebank.length; j++) {
					Node n = pokemonMovebank[j];
					while (n != null) {
						movebank.add(n.data);
						n = n.next;
					}
				}

				for (Move move : movebank) {
					moveCount.put(move, moveCount.getOrDefault(move, 0) + 1);
				}
			}
			for (Map.Entry<Move, Integer> entry : moveCount.entrySet()) {
				if (entry.getValue() == 0) {
					if (entry.getKey().isTM()) {
						unusedButTM.add(entry.getKey());
					} else {
						unused.add(entry.getKey());
					}
				} else if (entry.getValue() == 3) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 3; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigThree.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 2) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 2; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigTwo.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 1) {
					for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigOne.put(p, entry.getKey());
							break;
						}
					}
				}
			}
			for (Move m : unused) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nTM Only:\n");
			for (Move m : unusedButTM) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nSignature Moves (3):\n");
			sigThree.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			writer.write("\nSignature Moves (2):\n");
			sigTwo.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		
			writer.write("\nSignature Moves (1):\n");
			sigOne.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}