package docs;

import overworld.*;
import pokemon.*;

import entity.Entity;
import entity.PlayerCharacter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Path;

public class TrainerDoc {

	public static Map<?, ?>[] getTrainerLocationMap(GamePanel gp) {
		Entity[][] npc = gp.npc;
		Map<String, ArrayList<Trainer>> trainerMap = new LinkedHashMap<>();
		Map<Trainer, Entity> trainerNPCMap = new HashMap<>();
		for (int loc = 0; loc < npc.length; loc++) {
			for (int col = 0; col < npc[loc].length; col++) {
				Entity e = npc[loc][col];
				if (e == null || e.trainer < 0) continue;
				if (loc != 0 && e.trainer == 0) continue;
				PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
				String location = PlayerCharacter.currentMapName;
				Trainer tr = Trainer.getTrainer(e.trainer);
				if (trainerMap.containsKey(location)) {
					ArrayList<Trainer> list = trainerMap.get(location);
					list.add(tr);
				} else {
					ArrayList<Trainer> list = new ArrayList<>();
					list.add(tr);
					trainerMap.put(location, list);
				}
				trainerNPCMap.put(tr, e);
			}
		}
		
		return new Map<?, ?>[] {trainerMap, trainerNPCMap};
	}

	public static void writeTrainersToTxt(GamePanel gp, Path dir) {
		try {
			Path outPath = dir.resolve("TrainerInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			writer.write("Trainers:\n");
			writer.write("*** Note: if the Scott/Fred have 3 starters listed, regenerate docs after you picked your starter. ***\n");
			Map<?, ?>[] maps = TrainerDoc.getTrainerLocationMap(gp);
			
			@SuppressWarnings("unchecked")
			Map<String, ArrayList<Trainer>> trainerMap = (Map<String, ArrayList<Trainer>>) maps[0];
			@SuppressWarnings("unchecked")
			Map<Trainer, Entity> trainerNPCMap = (Map<Trainer, Entity>) maps[1];
			
			for (Map.Entry<String, ArrayList<Trainer>> e : trainerMap.entrySet()) {
				ArrayList<Trainer> trainers = e.getValue();
				String loc = e.getKey();
				while (loc.length() < 50) {
					loc += "-";
				}
				writer.write("\n" + loc + "\n");
				
				for (Trainer tr : trainers) {
					writer.write("\n");
					
					StringBuilder trainerName = new StringBuilder();
					trainerName.append(tr.getName());
					if (tr.staticEnc && !tr.catchable) {
						trainerName.append(" " + Arrays.toString(tr.getCurrent().ivs));
					}
					Entity corresponding = trainerNPCMap.get(tr);
					trainerName.append(String.format(" | X: %d, Y: %d, Facing: %s", corresponding.worldX / gp.tileSize, corresponding.worldY / gp.tileSize, corresponding.direction));
					trainerName.append(corresponding.isSpin() ? "*" : "");
					
					trainerName.append("\n");
					if (tr.boosts[0] > 0) {
						trainerName.append("**");
						trainerName.append(tr.getBoostString());
						trainerName.append("**\n");
					}
					writer.write(trainerName.toString());
					
					for (Pokemon p : tr.getTeam()) {
						String pName = p.name() + " (Lv. " + p.level + ")";
						if (gp.player.p.starter == -1 && (tr.getName().contains("Scott") || tr.getName().contains("Fred")) && p.id >= 1 && p.id <= 9) {
							pName = "*" + pName;
						}
						String itemString = p.item == null ? " @ None" : " @ " + p.item.toString();
						pName += itemString;
						while (pName.length() < 40) {
							pName += " ";
						}
						
						pName += "/";
						writer.write(pName);
						
						String nature = " " + p.nat.toString();
						while (nature.length() < 9) {
							nature += " ";
						}
						nature += "/";
						writer.write(nature);
						
						String aName = "   " + p.ability.toString();
						while (aName.length() < 18) {
							aName += " ";
						}
						aName += "/";
						writer.write(aName);
						
						String mName = "   ";
						for (int i = 0; i < 4; i++) {
							if (p.moveset[i] != null) {
								Move move = p.moveset[i].move;
								mName += move == Move.HIDDEN_POWER || move == Move.RETURN ? move.toString() + " " + p.determineHPType().toString() : move.toString();
								if (p.moveset[i] != null && p.moveset[i].getPPUps() > 0) {
									for (int pp = 0; pp < p.moveset[i].getPPUps(); pp++) {
										mName += "\u2191";
									}
								}
								if (i != 3) mName += ", ";
							}
						}
						writer.write(mName);
						writer.write("\n");
					}
				}
			}
			writer.write("\n");
			
			writer.close();
			//writeTrainerUsageStats(dir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void writeTrainerUsageStats(Path dir) {
		try {
			Path outPath = dir.resolve("TrainerInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("Stats:\n");
			int[] occurences = new int[Pokemon.MAX_POKEMON + 1];
			int totalPokemon = 0;
			
			for (Trainer tr : Trainer.trainers) {
				if (tr == null) break;
				for (Pokemon p : tr.getTeam()) {
					if (p.id <= 9 && (tr.getName().contains("Scott") || tr.getName().contains("Fred"))) {
						if (p.id == 1 || p.id == 4 || p.id == 7) {
							occurences[1]++;
							occurences[4]++;
							occurences[7]++;
							totalPokemon += 3;
						} else if (p.id == 2 || p.id == 5 || p.id == 8) {
							occurences[2]++;
							occurences[5]++;
							occurences[8]++;
							totalPokemon += 3;
						} else if (p.id == 3 || p.id == 6 || p.id == 9) {
							occurences[3]++;
							occurences[6]++;
							occurences[9]++;
							totalPokemon += 3;
						}
					} else {
						occurences[p.id]++;
						totalPokemon++;
					}
				}
			}
			StringBuilder header = new StringBuilder();
			header.append("Num,Pokemon,Amt,%\n");
			writer.write(header.toString());
			for (int i = 1; i < occurences.length; i++) {
				double ratio = occurences[i] * 100.0;
				ratio /= totalPokemon;
				StringBuilder stats = new StringBuilder();
				stats.append(i);
				stats.append("," + Pokemon.getName(i));
				stats.append("," + occurences[i]);
				stats.append("," + String.format("%.2f", ratio) + "%");
				stats.append("\n");
				writer.write(stats.toString());
			}
			writer.write("\nTotal: " + totalPokemon);
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}