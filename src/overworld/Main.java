package overworld;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
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

import docs.TrainerDoc;
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
import util.SaveManager;
import pokemon.Node;
import pokemon.Nursery.EggGroup;
import ui.LoadingScreen;

public class Main {
	public static JFrame window;
	public static GamePanel gp;
	public static final String gameTitle = "Pokemon Xhenos";
	public static final BufferedImage[] icons = new BufferedImage[5];

	public static void main(String[] args) {
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setTitle(gameTitle);
		
		loadIcon(window, 4);
		
		gp = new GamePanel(window);
		gp.setGameState(GamePanel.LOADING_STATE);
		
		window.add(gp);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		System.out.println("Max heap: " + max + " MB");
		
		performInitialLoad();
	}
	
	private static void performInitialLoad() {
		LoadingScreen loader = gp.loadingScreen;
		
		try {
			gp.setupGamePanel();
			
			loader.setProgress(25, "Loading Pokemon data...");
			Pokemon.readInfoFromCSV();
			
			loader.setProgress(35, "Loading movebanks...");
			Pokemon.readMovebanksFromCSV();
			
			loader.setProgress(45, "Loading entries...");
			Pokemon.readEntriesFromCSV();
			
			loader.setProgress(50, "Loading encounters...");
			Pokemon.readEncountersFromCSV();
			
			loader.setProgress(60, "Loading TMs...");
			Pokemon.readTMsFromCSV();
			
			loader.setProgress(80, "Setting up Pokedex...");
			Player.setupPokedex();
			
			loader.setProgress(100, "Complete!");
			
			gp.playMusic(Sound.M_MENU_2);
			gp.setGameState(GamePanel.TITLE_STATE);
			gp.keyH.resetKeys(false);
		} catch (Exception e) {
			e.printStackTrace();
		    StringWriter sw = new StringWriter();
		    e.printStackTrace(new PrintWriter(sw));
		    JOptionPane.showMessageDialog(null, "Error during initial load:\n" + sw.toString());
		}
	}

	public static void loadIcon(JFrame window, int image) {
		try {
			BufferedImage icon = null;
			if (icons[image - 1] != null) {
				icon = icons[image - 1];
			} else {
				URL iconURL = Main.class.getResource("/gen/icon" + image + ".png");
				if (iconURL != null) {
			        icon = ImageIO.read(iconURL);
				}
				icons[image - 1] = icon;
			}
		    if (icon != null) {
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
	
	public static void loadGame(String fileName, String playerName) {
		gp.setGameState(GamePanel.LOADING_STATE);
		gp.loadingScreen.reset();
		
		// SETTINGS
		boolean[] selectedOptions = gp.titleScreen.docOptions;
		boolean excel = gp.titleScreen.generateExcel;
		boolean nuzlocke = gp.titleScreen.nuzlockeMode;
		int difficulty = gp.titleScreen.difficultyLevel;
		boolean banShedinja = gp.titleScreen.banShedinja;
	    boolean banBatonPass = gp.titleScreen.banBatonPass;
	    boolean allowRevives = gp.titleScreen.allowRevives;
	    boolean buyableRevives = gp.titleScreen.buyableRevives;
	    int levelCapBonus = gp.titleScreen.levelCapBonus;
		
		new Thread(() -> {
			try {
				LoadingScreen loader = gp.loadingScreen;
				
				loader.setProgress(0, "Loading save file...");
				Player player = SaveManager.loadPlayer(fileName);
				
				if (player != null) {
					gp.player.p = player;
					
					loader.setProgress(2, "Clearing volatile statuses...");
					for (Pokemon p : gp.player.p.getTeam()) {
						if (p != null) {
							p.clearVolatile(null);
							p.vStatuses.clear();
						}
					}
					
					loader.setProgress(4, "Settign up world state...");
					gp.player.worldX = gp.player.p.getPosX();
					gp.player.worldY = gp.player.p.getPosY();
					gp.currentMap = gp.player.p.currentMap;
					
					if (gp.player.p.visor) gp.player.setupPlayerImages(true);
				} else {
					loader.setProgress(5, "Creating new player...");
					gp.player.p = new Player(gp, playerName);
					if (nuzlocke) gp.player.p.setupNuzlocke(banShedinja, banBatonPass, allowRevives, buyableRevives, levelCapBonus);
					gp.player.p.difficulty = difficulty;
					gp.player.p.flag[0][23] = true;
				}
				loader.setProgress(6, "Saving Config...");
				gp.config.excel = excel;
				gp.config.saveConfig();
				
				loader.setProgress(7, "Loading map data...");
				PMap.getLoc(gp.currentMap,
					(int) Math.round(gp.player.worldX * 1.0 / gp.tileSize),
					(int) Math.round(gp.player.worldY * 1.0 / gp.tileSize));
				window.setTitle(Main.gameTitle + " - " + PlayerCharacter.currentMapName);
				
				loader.setProgress(8, "Updating player version...");
				if (gp.player.p.version != Player.VERSION) {
					gp.player.p.update(gp);
				} else {
					gp.player.p.setSprites();
				}
				
				loader.setProgress(9, "Setting up game...");
				gp.setupGame();
				
				if (gp.player.p.surf) {
					loader.setProgress(64, "Setting up water tiles for Surfing...");
					for (Integer i : gp.tileM.getWaterTiles()) {
						gp.tileM.tile[i].collision = false;
					}
				}
				if (gp.player.p.lavasurf) {
					loader.setProgress(64, "Setting up lava tiles for Lava Surfing...");
					for (Integer i : gp.tileM.getLavaTiles()) {
						gp.tileM.tile[i].collision = false;
					}
				}
				
				if (hasAnyChecked(selectedOptions)) {
					Path docsDirectory = SaveManager.getDocsDirectory();
					
					if (selectedOptions[0]) {
						loader.setProgress(65, "Generating trainer docs...\n(Be patient... this may take a few minutes!)");
						if (excel) {
							TrainerDoc.writeTrainersToExcel(gp, docsDirectory);
						} else {
							TrainerDoc.writeTrainersToTxt(gp, docsDirectory);
						}		    			
					}
					if (selectedOptions[1]) {
						loader.setProgress(85, "Generating Pokemon docs...");
						writePokemon(docsDirectory);
					}
					if (selectedOptions[2]) {
						loader.setProgress(88, "Generating encounter docs...");
						writeEncounters(docsDirectory);
					}
					if (selectedOptions[3]) {
						loader.setProgress(91, "Generating move docs...");
						writeMoves(docsDirectory);
					}
					if (selectedOptions[4]) {
						loader.setProgress(92, "Generating ability docs...");
						writeAbilities(docsDirectory);
					}
					if (selectedOptions[5]) {
						loader.setProgress(94, "Generating item docs...");
						writeItems(gp, docsDirectory);
					}
					if (selectedOptions[6]) {
						loader.setProgress(96, "Generating defensive type docs...");
						writeDefensiveTypes(docsDirectory);
					}
					if (selectedOptions[7]) {
						loader.setProgress(98, "Generating offensive type docs...");
						writeOffensiveTypes(docsDirectory);
					}
					
					
					
					loader.setProgress(99, "Opening docs folder...");
					try {
						Desktop.getDesktop().open(docsDirectory.toFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				loader.setProgress(100, "Starting game...");
				gp.playMusic(Sound.M_MENU_1);
				Thread.sleep(300);
				
				// final setup
				SwingUtilities.invokeLater(() -> {
					setupWindowCloseHandler();
					gp.player.name = gp.player.p.getName();
					gp.eHandler.p = gp.player.p;
					gp.aSetter.updateNPC(gp.currentMap);
					gp.player.p.setupPuzzles(gp, gp.currentMap);
					gp.player.currentSave = fileName;
					gp.setGameState(GamePanel.PLAY_STATE);
				});
				
			} catch (Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(window, "Error loading game: " + e.getMessage());
					gp.setGameState(GamePanel.TITLE_STATE);
				});
			}
		}).start();
	}

	private static void setupWindowCloseHandler() {
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option = JOptionPane.showConfirmDialog(window,
					"Are you sure you want to exit?\nAny unsaved progress will be lost!",
					"Confirm Exit",
					JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					if (gp.gameState == GamePanel.BATTLE_STATE) {
						gp.saveScum("Save scummed in battle against " +
							(gp.battleUI.foe.trainerOwned() ?
								gp.battleUI.foe.trainer.getName() :
								"a wild " + gp.battleUI.foe.getName()));
					}
					Sound.disposeAll();
					System.exit(0);
				}
			}
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
	            	String acc = " " + m.getAccuracy(null, null, Pokemon.field);
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
						for (Item it : i.inventory) {
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
