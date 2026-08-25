package overworld;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import docs.AbilitiesDoc;
import docs.EncounterDoc;
import docs.ItemsDoc;
import docs.MovesDoc;
import docs.PokemonDoc;
import docs.TrainerDoc;
import docs.TypingsDoc;
import entity.PlayerCharacter;
import pokemon.Player;
import pokemon.Pokemon;
import util.Print;
import util.SaveManager;
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
					
					loader.setProgress(4, "Setting up world state...");
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
				PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / gp.tileSize), (int) Math.round(gp.player.worldY * 1.0 / gp.tileSize));
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
						if (excel) {
							PokemonDoc.writePokemonToExcel(docsDirectory);
						} else {
							PokemonDoc.writePokemonToTxt(docsDirectory);
						}
					}
					if (selectedOptions[2]) {
						loader.setProgress(88, "Generating encounter docs...");
						if (excel) {
							EncounterDoc.writeEncountersToExcel(gp, docsDirectory);
						} else {
							EncounterDoc.writeEncountersToTxt(gp, docsDirectory);
						}
					}
					if (selectedOptions[3]) {
						loader.setProgress(91, "Generating move docs...");
						if (excel) {
							MovesDoc.writeMovesToExcel(docsDirectory);
						} else {
							MovesDoc.writeMovesToTxt(docsDirectory);
						}
					}
					if (selectedOptions[4]) {
						loader.setProgress(92, "Generating ability docs...");
						if (excel) {
							AbilitiesDoc.writeAbilitiesToExcel(docsDirectory);
						} else {
							AbilitiesDoc.writeAbilitiesToTxt(docsDirectory);
						}
					}
					if (selectedOptions[5]) {
						loader.setProgress(94, "Generating item docs...");
						if (excel) {
							ItemsDoc.writeItemsToExcel(gp, docsDirectory);
						} else {
							ItemsDoc.writeItemsToTxt(gp, docsDirectory);
						}
					}
					if (selectedOptions[6]) {
						loader.setProgress(96, "Generating defensive type docs...");
						if (excel) {
							TypingsDoc.writeDefensiveTypingsToExcel(docsDirectory);
						} else {
							TypingsDoc.writeDefensiveTypingsToTxt(docsDirectory);
						}
					}
					if (selectedOptions[7]) {
						loader.setProgress(98, "Generating offensive type docs...");
						if (excel) {
							TypingsDoc.writeOffensiveTypingsToExcel(docsDirectory);
						} else {
							TypingsDoc.writeOffensiveTypingsToTxt(docsDirectory);
						}
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
					JOptionPane.showMessageDialog(window, "Error loading game: " + e.toString());
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
					Print.flush();
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
}