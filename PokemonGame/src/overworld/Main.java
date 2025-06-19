package overworld;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import docs.TrainerDoc;
import entity.Entity;
import entity.PlayerCharacter;
import object.ItemObj;
import object.TreasureChest;
import pokemon.Ability;
import pokemon.Egg;
import pokemon.Encounter;
import pokemon.Item;
import pokemon.Move;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Trainer;
import util.SaveManager;
import pokemon.Node;
import pokemon.Nursery.EggGroup;

public class Main {
	public static JFrame window;
	public static GamePanel gp;

	public static void main(String[] args) {
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		loadIcon(4);
		
		gp = new GamePanel(window);
		
		window.setTitle(gp.gameTitle);
		
		showStartupMenu(window);
	}
	
	private static void loadIcon(int image) {
		try {
		    URL iconURL = Main.class.getResource("/gen/icon" + image + ".png");
		    if (iconURL != null) {
		        Image icon = ImageIO.read(iconURL);
		        window.setIconImage(icon);
		        
		        if (icon != null && System.getProperty("os.name").toLowerCase().contains("mac")) {
		            try {
		                // Reflectively load Apple EAWT Application class (safe even if not on mac)
		                Class<?> appClass = Class.forName("com.apple.eawt.Application");
		                Object appInstance = appClass.getMethod("getApplication").invoke(null);
		                appClass.getMethod("setDockIconImage", Image.class).invoke(appInstance, icon);
		            } catch (Exception e) {
		                System.out.println("Unable to set dock icon on macOS: " + e.getMessage());
		            }
		        }
		    } else {
		        System.out.println("Icon resource not found!");
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private static void showStartupMenu(JFrame window) {
		WelcomeMenu menu = new WelcomeMenu(window, gp);
		window.add(menu);
		
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void loadSave(JFrame window, String fileName, WelcomeMenu welcomeMenu, boolean[] selectedOptions, boolean nuzlocke) {
		Player player = SaveManager.loadPlayer(fileName);
		if (player != null) {
			gp.player.p = player;
			
        	for (Pokemon p : gp.player.p.getTeam()) {
	            if (p != null) {
	            	p.clearVolatile();
	            	p.vStatuses.clear();
	            }
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
	        if (gp.player.p.visor) gp.player.setupPlayerImages(true);

	    } else {
	        // If there's an error reading the file, create a new Player object
	        gp.player.p = new Player(gp);
	        if (nuzlocke) gp.player.p.setupNuzlocke();
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
		    		
		            Path docsDirectory = SaveManager.getDocsDirectory();
		            
		    		if (selectedOptions[0]) {
		    			writeTrainers(gp, docsDirectory);
		    			TrainerDoc.writeTrainersToExcel(gp, docsDirectory);
		    		}
		    		if (selectedOptions[1]) writePokemon(docsDirectory);
		    		if (selectedOptions[2]) writeEncounters(docsDirectory);
		    		if (selectedOptions[3]) writeMoves(docsDirectory);
		    		if (selectedOptions[4]) writeDefensiveTypes(docsDirectory);
		    		if (selectedOptions[5]) writeOffensiveTypes(docsDirectory);
		    		if (selectedOptions[6]) writeItems(gp, docsDirectory);
		    		if (selectedOptions[7]) writeAbilities(docsDirectory);
		    		
		    		window.add(gp);
		    		gp.requestFocusInWindow();
		    		
		    		window.pack();
		    		
		    		if (hasAnyChecked(selectedOptions)) {
		    			try {
							Desktop.getDesktop().open(docsDirectory.toFile());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
		    		}
		    		
		    		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    		window.addWindowListener(new WindowAdapter() {
		                @Override // implementation
		                public void windowClosing(WindowEvent e) {
		                	int option = JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?\nAny unsaved progress will be lost!", "Confirm Exit", JOptionPane.YES_NO_OPTION);
		                    if (option == JOptionPane.YES_OPTION) {
		                    	if (gp.gameState == GamePanel.BATTLE_STATE) {
		                    		gp.saveScum("Save scummed in battle against " + (gp.battleUI.foe.trainerOwned() ? gp.battleUI.foe.trainer.getName() : "a wild " + gp.battleUI.foe.getName()));
		                    	}
		                        // Close the application
		                        System.exit(0);
		                    }
		                }
		            });
		    		
		    		gp.eHandler.p = gp.player.p;
		    		
		    		// Update the npcs to the current game state on the current map
		    		gp.aSetter.updateNPC(gp.currentMap);
		    		
		    		gp.player.p.setupPuzzles(gp, gp.currentMap);
		    		
		    		gp.startGameThread();
		        }
		    });
			
			timer.start();
		});
	}

	private static boolean hasAnyChecked(boolean[] options) {
		for (boolean b : options) {
			if (b) return true;
		}
		return false;
	}

	private static void writePokemon(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			
			int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE + 2];
			int counter = 0;
			for (Pokemon p : Player.pokedex1) {
				ids[counter] = p.getID();
				counter++;
				if (p.getID() == 150) {
					ids[counter++] = 237;
				}
				if (p.getID() == 290) {
					ids[counter++] = 291;
				}
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
				writer.write(String.format("%-13s| %d\n", "Egg Cycles", Egg.computeEggCycles(p.catchRate)));

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
			header.append("ID   Name               ");
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
	                rowBuilder.append(canLearn ? "Y     " : "N     ");
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
	
	private static void writeTrainers(GamePanel gp, Path dir) {
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
					if (tr.toString().equals(tr.getTeam()[0].name())) {
	                    trainerName.append(" (");
	                    for (int i = 0; i < 6; i++) {
	                        trainerName.append(String.valueOf(tr.getTeam()[0].ivs[i]));
	                        if (i < 5) trainerName.append(", ");
	                    }
	                    trainerName.append(")");
	                }
					Entity corresponding = trainerNPCMap.get(tr);
					trainerName.append(String.format(" | X: %d, Y: %d, Facing: %s", corresponding.worldX / gp.tileSize, corresponding.worldY / gp.tileSize, corresponding.direction));
					trainerName.append(corresponding.isSpin() ? "*" : "");
					
					trainerName.append("\n");
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

	private static void writeEncounters(Path dir) {
		try {
			Path outPath = dir.resolve("WildPokemon.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			
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
		result += "===================================================================\n";
		return result;
	}
	
	private static void writeMoves(Path dir) {
		try {
			Path outPath = dir.resolve("MovesInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			
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
				
				writer.write("=============================================================================================================\n");
	            writer.write("                                                  " + type.toString() + "\n");
	            writer.write("=============================================================================================================\n");

	            List<Move> typeMoves = movesByType.get(type);
	            for (Move m : typeMoves) {
	            	String move = " " + m.toString();
	            	while (move.length() < 20) {
	            		move += " ";
	            	}
	            	String cat = " " + m.getCategory();
	            	while (cat.length() < 10) {
	            		cat += " ";
	            	}
	            	String bp = " " + m.formatbp(null, null, Pokemon.field);
	            	while (bp.length() < 5) {
	            		bp += " ";
	            	}
	            	String acc = " " + m.getAccuracy();
	            	while (acc.length() < 5) {
	            		acc += " ";
	            	}
	            	String pp = " " + m.pp + " PP ";
	            	while (pp.length() < 7) {
	            		pp += " ";
	            	}
					writer.write(String.format("%s|%s|%s|%s|%s: %s\n", move, cat, bp, acc, pp, m.getDescription()));
	            }
			}
			
			writer.write("\n=================================\n");
			writer.write("IVs | Hidden Power Type");
			writer.write("\n=================================\n");
			
			Pokemon test = new Pokemon(1, 5, true, false);
			
			for (int i = 0; i < 64; i++) {
				int[] ivs = new int[6];
				for (int j = 0; j < 6; j++) {
					ivs[j] = (i & (1 << j)) != 0 ? 31 : 30;
				}
				
				test.ivs = ivs;
				PType type = test.determineHPType();
				writer.write(String.format("%s | %s\n", Arrays.toString(test.ivs), type.toString()));
			}
			
			writer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeDefensiveTypes(Path dir) {
		try {
			Path outPath = dir.resolve("DefensiveTypings.txt");
            FileWriter writer = new FileWriter(outPath.toFile());

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
	
	private static void writeOffensiveTypes(Path dir) {
		try {
			Path outPath = dir.resolve("OffensiveTypings.txt");
            FileWriter writer = new FileWriter(outPath.toFile());

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
	
	private static void writeItems(GamePanel gp, Path dir) {
		try {
			Path outPath = dir.resolve("ItemsInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			writer.write("--------------------------------------\n");
			writer.write("Items Info:\n");
			writer.write("Item | Pocket | Buy | Sell | Description");
			writer.write("\n--------------------------------------\n");
			Item[] allItems = Item.values();
			for (Item i : allItems) {
				String item = i.toString();
				while (item.length() < 22) {
					item += " ";
				}
				String pocket = Item.getPocketName(i.getPocket());
				while(pocket.length() < 10) {
					pocket += " ";
				}
				int cost = i.getCost();
				int s = i.getSell();
				String buy = cost == 0 ? "--" : "$" + cost;
				while(buy.length() < 5) {
					buy += " ";
				}
				String sell = s == 0 ? "--" : "$" + s;
				while(sell.length() < 5) {
					sell += " ";
				}
				String desc = i.getDesc();
				writer.write(String.format("%s | %s | %s | %s | %s\n", item, pocket, buy, sell, desc));
			}
			
			// TM Locations section
			writer.write("\n--------------------------------------\n");
			writer.write("TM Locations:\n");
			writer.write("--------------------------------------\n");
			try (Scanner scanner = new Scanner(Main.class.getResourceAsStream("/info/tm_locations.txt"))) {
				while (scanner.hasNextLine()) {
					writer.write(scanner.nextLine() + "\n");
				}
			} catch (Exception ex) {
				writer.write("Could not load TM Locations file.\n");
				ex.printStackTrace();
			}
			
			boolean[][] tempItemsCollected = gp.player.p.itemsCollected.clone();
			gp.player.p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
			gp.aSetter.setObject();
			ItemObj[][] items = gp.obj;
			writer.write("\n--------------------------------------\n");
			writer.write("Overworld Items:\n");
			writer.write("(Note: the Variable Items:\n");
			writer.write("- Nature Mints\n");
			writer.write("- Type Resist Berries\n");
			writer.write("- Stat Restoring Berries\n");
			writer.write("- Treasure Chest Items\n");
			writer.write("will show for only the save file you generated these docs for)");
			writer.write("\n--------------------------------------\n");
			Map<String, ArrayList<ItemObj>> itemsMap = new LinkedHashMap<>();
			for (int loc = 0; loc < items.length; loc++) {
				for (int col = 0; col < items[loc].length; col++) {
					ItemObj e = items[loc][col];
					if (e == null) continue;
					PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
					String location = PlayerCharacter.currentMapName;
					if (itemsMap.containsKey(location)) {
						ArrayList<ItemObj> list = itemsMap.get(location);
						list.add(e);
					} else {
						ArrayList<ItemObj> list = new ArrayList<>();
						list.add(e);
						itemsMap.put(location, list);
					}
				}
			}
			
			for (Map.Entry<String, ArrayList<ItemObj>> e : itemsMap.entrySet()) {
				ArrayList<ItemObj> list = e.getValue();
				String loc = e.getKey();
				while (loc.length() < 50) {
					loc += "-";
				}
				writer.write("\n\n" + loc + "\n");
				
				for (ItemObj i : list) {
					writer.write("\n");
					
					int x = i.worldX / gp.tileSize;
					int y = i.worldY / gp.tileSize;
					
					boolean chest = i instanceof TreasureChest;
					String itemString = chest ? "Treasure Chest" : i.item.toString();
					
					writer.write(String.format("%s (%d, %d)", itemString, x, y));
					
					if (chest) {
						ArrayList<Item> chestItems = ((TreasureChest) i).items;
						for (Item it : chestItems) {
							String label = "\n  [";
							label += it + "]";
							while (label.length() < 26) label += " ";
							label += "|";
							
							writer.write(label);
						}
					}
					
				}
			}
			writer.write("\n");
			
			writer.close();
			
			// cleanup
			gp.player.p.itemsCollected = tempItemsCollected;
			gp.aSetter.setObject();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeAbilities(Path dir) {
		try {
			Path outPath = dir.resolve("AbilitiesInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			writer.write("--------------------------------------\n");
			writer.write("Abilities Info:\n");
			writer.write("--------------------------------------\n");
			Ability[] allAbilities = Ability.values();
			for (Ability a : allAbilities) {
				String ability = a.toString();
				while (ability.length() < 18) {
					ability += " ";
				}
				String desc = a.desc;
				writer.write(String.format("%s | %s\n", ability, desc));
			}
			
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
