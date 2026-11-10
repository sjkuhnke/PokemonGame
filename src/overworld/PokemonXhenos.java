package overworld;
import java.awt.Desktop;
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
import docs.MasterDoc;
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

public class PokemonXhenos {
	public static JFrame window;
	public static GamePanel gp;
	public static final String gameTitle = "Pokemon Xhenos";
	public static final BufferedImage[] icons = new BufferedImage[5];
	public static boolean closer;

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
	            URL iconURL = PokemonXhenos.class.getResource("/gen/icon" + image + ".png");

	            if (iconURL != null) {
	                icon = ImageIO.read(iconURL);
	            }

	            icons[image - 1] = icon;
	        }

	        if (icon != null) {
	            window.setIconImage(icon);
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
		boolean masterExcel = gp.titleScreen.masterExcel;
		boolean nuzlocke = gp.titleScreen.nuzlockeMode;
		int difficulty = gp.titleScreen.difficultyLevel;
		boolean banShedinja = gp.titleScreen.banShedinja;
	    boolean banBatonPass = gp.titleScreen.banBatonPass;
	    boolean allowDryPass = gp.titleScreen.allowDryPass;
	    boolean allowRevives = gp.titleScreen.allowRevives;
	    boolean buyableRevives = gp.titleScreen.buyableRevives;
	    int levelCapBonus = gp.titleScreen.levelCapBonus;
	    int shinyClause = gp.titleScreen.shinyClause;
		
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
					if (nuzlocke) gp.player.p.setupNuzlocke(banShedinja, banBatonPass, allowDryPass, allowRevives, buyableRevives, levelCapBonus, shinyClause);
					gp.player.p.difficulty = difficulty;
					gp.player.p.flag[0][23] = true;
				}
				loader.setProgress(6, "Saving Config...");
				gp.config.saveConfig();
				
				loader.setProgress(7, "Loading map data...");
				PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / gp.tileSize), (int) Math.round(gp.player.worldY * 1.0 / gp.tileSize));
				window.setTitle(PokemonXhenos.gameTitle + " - " + PlayerCharacter.currentMapName);
				
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
				
				if (hasAnyChecked(selectedOptions) || masterExcel) {
					Path docsDirectory = SaveManager.getDocsDirectory();
					
					// Doc generation owns 65%-98% of the bar. If both the master
					// workbook and TXT docs are requested, TXT takes the first slice.
					final int DOCS_START = 65;
					final int DOCS_END = 98;
					int txtEnd = masterExcel ? 80 : DOCS_END;
					
					String[] docLabels = {"trainer", "Pokemon", "encounter", "move", "ability", "item", "defensive type", "offensive type"};
					int totalTxt = 0;
					for (boolean b : selectedOptions) if (b) totalTxt++;
					int doneTxt = 0;
					
					for (int d = 0; d < selectedOptions.length; d++) {
						if (!selectedOptions[d]) continue;
						int pct = DOCS_START + (txtEnd - DOCS_START) * doneTxt / Math.max(1, totalTxt);
						loader.setProgress(pct, "Generating " + docLabels[d] + " docs...\n(" + (doneTxt + 1) + " of " + totalTxt + ")");
						switch (d) {
						case 0: TrainerDoc.writeTrainersToTxt(gp, docsDirectory); break;
						case 1: PokemonDoc.writePokemonToTxt(docsDirectory); break;
						case 2: EncounterDoc.writeEncountersToTxt(gp, docsDirectory); break;
						case 3: MovesDoc.writeMovesToTxt(docsDirectory); break;
						case 4: AbilitiesDoc.writeAbilitiesToTxt(docsDirectory); break;
						case 5: ItemsDoc.writeItemsToTxt(gp, docsDirectory); break;
						case 6: TypingsDoc.writeDefensiveTypingsToTxt(docsDirectory); break;
						case 7: TypingsDoc.writeOffensiveTypingsToTxt(docsDirectory); break;
						}
						doneTxt++;
					}
					
					if (masterExcel) {
						loader.setProgress(txtEnd, "Generating master Excel doc...");
						MasterDoc.generate(gp, docsDirectory, loader::setProgress, txtEnd, DOCS_END);
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
					Print.setSaveFile(fileName);
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
		if (closer) return;
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
					GamePanel.audioExec.execute(Sound::disposeAll);
					gp.titleScreen.previewExecutor.shutdown();
					System.exit(0);
				}
			}
		});
		closer = true;
	}

	private static boolean hasAnyChecked(boolean[] options) {
		for (boolean b : options) {
			if (b) return true;
		}
		return false;
	}	
}