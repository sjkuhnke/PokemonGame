package overworld;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import entity.Entity;
import entity.PlayerCharacter;
import pokemon.Ability;
import pokemon.Encounter;
import pokemon.Item;
import pokemon.Move;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Trainer;
import pokemon.Node;

public class Main {
	public static JFrame window;
	public static GamePanel gp;

	public static void main(String[] args) {
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		gp = new GamePanel(window);
		
		window.setTitle(gp.gameTitle);
		
		showStartupMenu(window);
	}
	
	private static void showStartupMenu(JFrame window) {
		WelcomeMenu menu = new WelcomeMenu(window, gp);
		window.add(menu);
		
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void loadSave(JFrame window, String fileName, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		try {
			// Check if the directory exists, create it if not
            Path savesDirectory = Paths.get("./saves/");
            if (!Files.exists(savesDirectory)) {
                try {
					Files.createDirectories(savesDirectory);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./saves/" + fileName));
	        gp.player.p = (Player) ois.readObject();
        	for (Pokemon p : gp.player.p.getTeam()) {
	            if (p != null) p.clearVolatile();
	        }
	        gp.player.worldX = gp.player.p.getPosX();
	        gp.player.worldY = gp.player.p.getPosY();
	        gp.currentMap = gp.player.p.currentMap;
	        if (gp.player.p.surf) {
	        	for (Integer i : gp.tileM.getWaterTiles()) {
	        		gp.tileM.tile[i].collision = false;
				}
	        }
	        if (gp.player.p.lavasurf) {
	        	for (Integer i : gp.tileM.getLavaTiles()) {
	        		gp.tileM.tile[i].collision = false;
				}
	        }
	        ois.close();
	    } catch (IOException | ClassNotFoundException e) {
	        // If there's an error reading the file, create a new Player object
	        gp.player.p = new Player(gp);
	        
	        gp.player.p.resistBerries = new Item[20];
	        int count = 0;
			for (int i = 232; i < 252; i++) {
				 gp.player.p.resistBerries[count] = Item.getItem(i);
				count++;
			}
	        List<Item> berryList = Arrays.asList(gp.player.p.resistBerries);
	        Collections.shuffle(berryList);
	        gp.player.p.resistBerries = berryList.toArray(new Item[1]);
	    }
		
		PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / gp.tileSize), (int) Math.round(gp.player.worldY * 1.0 / gp.tileSize));
		window.setTitle(gp.gameTitle + " - " + PlayerCharacter.currentMapName);
		
		loadGame(window, welcomeMenu, selectedOptions);
	}
	
	private static void loadGame(JFrame window, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		window.remove(welcomeMenu);
        window.repaint();
        
		SwingUtilities.invokeLater(() -> {
			// Create a JPanel for the fade effect
		    FadingPanel fadePanel = new FadingPanel();
		    fadePanel.setBackground(Color.BLACK);
		    fadePanel.setBounds(0, 0, window.getWidth(), window.getHeight());
		    window.add(fadePanel, 0); // Add the fadePanel at the bottom layer
		    
		    if (gp.player.p.version != Player.VERSION) {
	        	gp.player.p.update(gp);
	        } else {
	        	gp.player.p.setSprites();
	        }
		    
		    gp.setupGame();
		    
		    // Create a Timer to gradually change the opacity of the fadePanel
		    Timer timer = new Timer(20, null);
		    timer.addActionListener(e -> {
		        int alpha = fadePanel.getAlpha();
		        alpha += 2; // Adjust the fading speed
		        fadePanel.setAlpha(alpha);
		        fadePanel.repaint();
	
		        if (alpha >= 95) {
		            timer.stop();
		    		
		            Path docsDirectory = Paths.get("./docs/");
					if (!Files.exists(docsDirectory)) {
		                try {
							Files.createDirectories(docsDirectory);
						} catch (IOException f) {
							f.printStackTrace();
						}
		            }
		            
		    		if (selectedOptions[0]) writeTrainers(gp);
		    		if (selectedOptions[1]) writePokemon();
		    		if (selectedOptions[2]) writeEncounters();
		    		if (selectedOptions[3]) writeMoves();
		    		if (selectedOptions[4]) writeDefensiveTypes();
		    		if (selectedOptions[5]) writeOffensiveTypes();
		    		
		    		window.add(gp);
		    		gp.requestFocusInWindow();
		    		
		    		window.pack();
		    		
		    		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    		window.addWindowListener(new WindowAdapter() {
		                @Override // implementation
		                public void windowClosing(WindowEvent e) {
		                	int option = JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?\nAny unsaved progress will be lost!", "Confirm Exit", JOptionPane.YES_NO_OPTION);
		                    if (option == JOptionPane.YES_OPTION) {
		                        // Close the application
		                        System.exit(0);
		                    }
		                }
		            });
		    		
		    		gp.eHandler.p = gp.player.p;
		    		
		    		// Update the npcs to the current game state on the current map
		    		gp.aSetter.updateNPC(gp.currentMap);
		    		
		    		if (gp.currentMap == 107 && gp.player.p.flags[28]) {
		    			gp.tileM.openGhostlyBluePortals();
		    		}
		    		
		    		gp.startGameThread();
		        }
		    });
			
			timer.start();
		});
	}

	private static void writePokemon() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt");
			
			int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE];
			int counter = 0;
			for (Pokemon p : Player.pokedex1) {
				ids[counter] = p.getID();
				counter++;
			}
			for (Pokemon p : Player.pokedex2) {
				ids[counter] = p.getID();
				counter++;
			}
			for (Pokemon p : Player.pokedex3) {
				ids[counter] = p.getID();
				counter++;
			}
			for (Pokemon p : Player.pokedex4) {
				ids[counter] = p.getID();
				counter++;
			}
			
			for (int i : ids) {
				Pokemon p = new Pokemon(i, 5, false, false);
				writer.write("===================\n");
				String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
				String name = dexNo + " - " + p.name;
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
					stats += p.getBaseStat(j) + " " + Pokemon.getStatType(j) + "/ ";
				}
				stats += p.getBST() + " BST";
				writer.write(stats + "\n\n");
				
				writer.write("Level Up:\n");
				Node[] movebank = p.getMovebank();
				for (int j = 0; j < movebank.length; j++) {
					Node n = movebank[j];
					while (n != null) {
						String move = j + 1 + " - " + n.data.toString() + "\n";
						n = n.next;
						writer.write(move);
					}
				}
				writer.write("\n");
				if (p.canEvolve()) {
					writer.write("Evolutions:\n");
					writer.write(p.getEvolveString() + "\n\n");
				}
				
			}
			writer.close();
			//writeUnusedMoves();
			//writeTypeStats();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@SuppressWarnings("unused")
	private static void writeTypeStats() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt", true);
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
				header.append("," + Pokemon.getStatType(i).trim());
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
	private static void writeUnusedMoves() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt", true);
			writer.write("Unused moves:\n");
			ArrayList<Move> unused = new ArrayList<>();
			ArrayList<Move> unusedButTM = new ArrayList<>();
			Map<Pokemon, Move> sigOne = new HashMap<>();
			Map<Pokemon, Move> sigTwo = new HashMap<>();
			Map<Pokemon, Move> sigThree = new HashMap<>();
			Map<Move, Integer> moveCount = new HashMap<>();
			for (Move m : Move.values()) {
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
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			writer.write("\nSignature Moves (2):\n");
			sigTwo.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		
			writer.write("\nSignature Moves (1):\n");
			sigOne.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	private static void writeTrainers(GamePanel gp) {
		try {
			FileWriter writer = new FileWriter("./docs/TrainerInfo.txt");
			Entity[][] npc = gp.npc;
			writer.write("Trainers:\n");
			writer.write("*** Note: if the Scott/Fred have 3 starters listed, regenerate docs after you picked your starter. ***\n");
			Map<String, ArrayList<Trainer>> trainerMap = new LinkedHashMap<>();
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
				}
			}
			
			for (Map.Entry<String, ArrayList<Trainer>> e : trainerMap.entrySet()) {
				ArrayList<Trainer> trainers = e.getValue();
				String loc = e.getKey();
				while (loc.length() < 50) {
					loc += "-";
				}
				writer.write(loc + "\n");
				writer.write("\n");
				for (Trainer tr : trainers) {
					boolean hasItems = false;
					String name = "Trainer " + tr.getName();
					while (name.length() < 28) {
						name += " ";
					}
					writer.write(name);
					for (int i = 0; i < tr.getTeam().length; i++) {
						Pokemon p = tr.getTeam()[i];
						writer.write(p.name + " Lv. " + p.level);
						if (i != tr.getTeam().length - 1) writer.write(", ");
						if (!hasItems && p.item != null) hasItems = true;
					}
					if (hasItems && Trainer.bosses.stream().noneMatch(tr.getName()::contains)) {
						writer.write(" @ (");
						for (int i = 0; i < tr.getTeam().length; i++) {
							String item = tr.getTeam()[i].item == null ? "None" : tr.getTeam()[i].item.toString();
							writer.write(item);
							if (i != tr.getTeam().length - 1) writer.write(", ");
						}
						writer.write(")");
					}
					writer.write("\n");
					
					if (Trainer.bosses.stream().anyMatch(tr.getName()::contains)) {
						writer.write("\n");
						writer.write(tr.getName() + "\n");
						for (Pokemon p : tr.getTeam()) {
							String pName = p.name + " (Lv. " + p.level + ")";
							if (gp.player.p.starter == -1 && (tr.getName().contains("Scott") || tr.getName().contains("Fred")) && p.id >= 1 && p.id <= 9) {
								pName = "*" + pName;
							}
							if (p.item != null) {
								pName += " @ " + p.item.toString();
								while (pName.length() < 40) {
									pName += " ";
								}
							} else {
								while (pName.length() < 22) {
									pName += " ";
								}
							}
							
							pName += "/";
							writer.write(pName);
							
							String aName = "   " + p.ability.toString();
							while (aName.length() < 18) {
								aName += " ";
							}
							aName += "/";
							writer.write(aName);
							
							String mName = "   ";
							for (int i = 0; i < 4; i++) {
								if (p.moveset[i] != null) {
									mName += p.moveset[i].move.toString();
									if (i != 3) mName += ", ";
								}
							}
							writer.write(mName);
							writer.write("\n");
						}
						writer.write("\n");
					}
				}
				writer.write("\n");
			}
			
			writer.close();
			writeTrainerUsageStats();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void writeTrainerUsageStats() {
		try {
			FileWriter writer = new FileWriter("./docs/TrainerInfo.txt", true);
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



	private static void writeEncounters() {
		try {
			FileWriter writer = new FileWriter("./docs/WildPokemon.txt");
			
			ArrayList<Encounter> allEncounters = new ArrayList<>();
			int[] amounts = new int[Pokemon.MAX_POKEMON + 1];
			double[] chances = new double[Pokemon.MAX_POKEMON + 1];
			
			for (Map.Entry<String, ArrayList<Encounter>> e : Encounter.encounters.entrySet()) {
				String[] parts = e.getKey().split("\\|");
				String area = parts[0];
				String type = parts[1];
				
				String typeName;
				switch (type) {
				case "G":
					typeName = "Standard";
					break;
				case "F":
					typeName = "Fish";
					break;
				case "S":
					typeName = "Surf";
					break;
				case "L":
					typeName = "Lava";
					break;
				default:
					typeName = "Unknown";
				}
				
				writer.write(area + " (" + typeName + ")\n");
				writer.write(writeEncounter(e.getValue()));
				allEncounters.addAll(e.getValue());
			}
			
			for (Encounter e : allEncounters) {
				int index = e.getId();
				amounts[index]++;
				chances[index] += e.getEncounterChance();
			}
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				double average = amounts[i] == 0 ? 0.0 : chances[i] / amounts[i] * 100;
				writer.write(i + "," + Pokemon.getName(i) + "," + amounts[i] + "," + String.format("%.2f", average) + "%\n");
			}
			
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}



	private static String writeEncounter(ArrayList<Encounter> encounters) {
		String result = "===================================================================\n";
		for (Encounter e : encounters) {
			Pokemon ep = new Pokemon(e.getId(), 5, false, false);
			String percent = String.format("%.0f", e.getEncounterChance() * 100);
			boolean sameLv = e.getMinLevel() == e.getMaxLevel();
			result += percent;
			result += "% ";
			result += ep.name;
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
		result += "===================================================================\n";
		return result;
	}
	
	private static void writeMoves() {
		try {
			FileWriter writer = new FileWriter("./docs/MovesInfo.txt");
			
			ArrayList<Move> moves = new ArrayList<>(Arrays.asList(Move.values()));
			Map<PType, List<Move>> movesByType = new HashMap<>();
			
			for (Move m : moves) {
				PType type = m.mtype;
				movesByType.computeIfAbsent(type, k -> new ArrayList<>()).add(m);
			}
			
			for (List<Move> typeMoves : movesByType.values()) {
				typeMoves.sort(Comparator.comparing(Move::toString));
			}
			
			List<PType> sortedTypes = new ArrayList<>(movesByType.keySet());
			sortedTypes.sort(Comparator.comparing(PType::toString));
			
			for (PType type : sortedTypes) {
				
				writer.write("\n===============\n");
	            writer.write(type.toString() + "\n");
	            writer.write("===============\n");

	            List<Move> typeMoves = movesByType.get(type);
	            for (Move m : typeMoves) {
            		String result = m.toString() + " : ";
					result += m.getCategory() + " / ";
					result += (int) m.getbp(null, null) + " / ";
					result += m.getAccuracy() + " : ";
					result += m.getDescription() + "\n";
					
					writer.write(result);
	            }				
			}
			writer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeDefensiveTypes() {
		try {
            FileWriter writer = new FileWriter("./docs/DefensiveTypings.txt");

            PType[] types = PType.values();
            ArrayList<PType> arrayListTypes = new ArrayList<>(Arrays.asList(types));
            arrayListTypes.remove(PType.UNKNOWN);
            types = arrayListTypes.toArray(new PType[1]);
            
            Pokemon test = new Pokemon(1, 5, true, false);
            Pokemon foe = new Pokemon(4, 5, true, false);

            writer.write("TYPE COMBINATIONS (Defensively)\n");
            for (PType type1 : types) {
                for (PType type2 : types) {
                	writer.write("\n===========================\n");
                    String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
                    writer.write(combination + "\n===========================\n");
                    
                    Map<PType, Double> typeEffectivenessMap = new HashMap<>();
                    foe.type1 = type1;
                    foe.type2 = type2;
                    
                    for (PType type3 : types) {
                        double multiplier = 1;
                        if (test.getImmune(foe, type3)) multiplier = 0;
                        
                        if (multiplier != 0) {
                        	// Check type effectiveness
                    		PType[] resist = test.getResistances(type3);
                    		for (PType type : resist) {
                    			if (type1 == type) multiplier /= 2;
                    			if (type2 == type) multiplier /= 2;
                    		}
                    		
                    		// Check type effectiveness
                    		PType[] weak = test.getWeaknesses(type3);
                    		for (PType type : weak) {
                    			if (type1 == type) multiplier *= 2;
                    			if (type2 == type) multiplier *= 2;
                    		}
                    		
                        }
                        typeEffectivenessMap.put(type3, multiplier);
                		
                    }
                    
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
	
	private static void writeOffensiveTypes() {
		try {
            FileWriter writer = new FileWriter("./docs/OffensiveTypings.txt");

            PType[] types = PType.values();
            ArrayList<PType> arrayListTypes = new ArrayList<>(Arrays.asList(types));
            arrayListTypes.remove(PType.UNKNOWN);
            types = arrayListTypes.toArray(new PType[1]);
            
            Pokemon test = new Pokemon(1, 5, true, false);
            Pokemon foe = new Pokemon(4, 5, true, false);

            writer.write("TYPE COMBINATIONS (Offensively)\n");
            for (PType type1 : types) {
                for (PType type2 : types) {
                	writer.write("\n===========================\n");
                    String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
                    writer.write(combination + "\n===========================\n");
                    
                    Map<PType, Double> typeEffectivenessMap = new HashMap<>();
                    
                    for (PType type3 : types) {
                    	foe.type1 = type3;
                        foe.type2 = null;
                        double multiplier = 1;
                        if (test.getImmune(foe, type1) && test.getImmune(foe, type2)) multiplier = 0;
                        
                        if (multiplier != 0) {
                        	// Check type effectiveness
                    		ArrayList<PType> resist1 = new ArrayList<>(Arrays.asList(test.getResistances(type1)));
                    		ArrayList<PType> resist2 = new ArrayList<>(Arrays.asList(test.getResistances(type2)));
                    		if ((resist1.contains(type3) && resist2.contains(type3)) || (test.getImmune(foe, type1) && resist2.contains(type3) || (resist1.contains(type3) && test.getImmune(foe, type2)))) multiplier /= 2;
                    		
                    		// Check type effectiveness
                    		ArrayList<PType> weak1 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type1)));
                    		ArrayList<PType> weak2 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type2)));
                    		if (weak1.contains(type3) && weak2.contains(type3)) {
                    			multiplier *= 4;
                    		} else if (weak1.contains(type3) || weak2.contains(type3)) {
                    			multiplier *= 2;
                    		}
                    		
                    		
                        }
                        typeEffectivenessMap.put(type3, multiplier);
                		
                    }
                    
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

}
