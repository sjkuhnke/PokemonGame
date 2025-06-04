package overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import entity.*;
import object.*;
import pokemon.*;
import puzzle.*;
import tile.*;

public class GamePanel extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1898610723315381969L;
	
	public JFrame window;
	
	// SETTINGS
	final int originalTileSize = 16;
	public final int scale = 3; // 3, 16, 12
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 16; // 48
	public final int maxScreenRow = 12; // 36
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	public final int maxWorldCol = 100;
	public final int maxWorldRow = 100;
	public int currentMap = 0;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	public static final int MAX_MAP = 220;
	public static final int MAX_FLAG = 24; // should not be >31
	
	public int offsetX;
	public int offsetY;
	
	public final String gameTitle = "Pokemon Xhenos";
	
	public KeyHandler keyH = new KeyHandler(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public EventHandler eHandler = new EventHandler(this);
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public PlayerCharacter player = new PlayerCharacter(this, keyH);
	public Script script;
	public Entity npc[][] = new Entity[MAX_MAP][40];
	public ItemObj obj[][] = new ItemObj[MAX_MAP][40];
	public InteractiveTile iTile[][] = new InteractiveTile[MAX_MAP][90];
	public ArrayList<Entity> particleList = new ArrayList<>();
	
	public CopyOnWriteArrayList<Entity> renderableNPCs = new CopyOnWriteArrayList<>();
	public ArrayList<TreasureChest> chests = new ArrayList<>();
	
	public NPC_Pokemon[] grusts = new NPC_Pokemon[10];
	public boolean checkSpin = false;
	public PuzzleManager puzzleM = new PuzzleManager(this);
	
	public TileManager tileM = new TileManager(this);
	
	public int FPS = 60;
	public int ticks;
	
	public UI ui = new UI(this);
	public BattleUI battleUI = new BattleUI(this);
	public SimBattleUI simBattleUI = new SimBattleUI(this);
	
	public int gameState;

	public static final int PLAY_STATE = 1;
	public static final int DIALOGUE_STATE = 2;
	public static final int BATTLE_STATE = 3;
	public static final int MENU_STATE = 4;
	public static final int TRANSITION_STATE = 5;
	public static final int SHOP_STATE = 6;
	public static final int NURSE_STATE = 7;
	public static final int BOX_STATE = 8;
	public static final int TASK_STATE = 10;
	public static final int START_BATTLE_STATE = 11;
	public static final int USE_ITEM_STATE = 12;
	public static final int USE_REPEL_STATE = 13;
	public static final int RARE_CANDY_STATE = 14;
	public static final int DEX_NAV_STATE = 15;
	public static final int STARTER_STATE = 16;
	public static final int LETTER_STATE = 17;
	public static final int PRIZE_STATE = 18;
	public static final int SIM_BATTLE_STATE = 19;
	public static final int SIM_START_BATTLE_STATE = 20;
	public static final int COIN_STATE = 21;
	public static final int STAR_SHOP_STATE = 22;

	public GamePanel(JFrame window) {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
		
		this.window = window;
		
		Pokemon.readInfoFromCSV();
		Pokemon.readMovebanksFromCSV();
		Pokemon.readEntiresFromCSV();
		Pokemon.readEncountersFromCSV();
		Pokemon.readTMsFromCSV();
		
		Player.setupPokedex();
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		while (gameThread != null) {
			drawInterval = 1000000000/FPS;
			update();
			
			repaint();
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime /= 1000000;
				
				if (remainingTime < 0) remainingTime = 0;
				
				Thread.sleep((long)remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update() {
		if (gameState == PLAY_STATE) {
			ticks++;
			if (ticks >= 12) {
				ticks = 0;
			}
			player.update();
			if (ticks % 5 == 0) {
				updateEntity();
			}
		}
		if (keyH.tabPressed) {
			FPS = 120;
		} else {
			FPS = 60;
		}
		if (keyH.ctrlPressed && keyH.shiftPressed) {
			if (gameState == BATTLE_STATE) {
				System.out.println("------------------------------------------");
				System.out.println("Current Task: " + battleUI.currentTask);
				System.out.println(battleUI.tasks.toString());
			} else if (gameState == SIM_BATTLE_STATE) {
				System.out.println("------------------------------------------");
				System.out.println("Current Task: " + simBattleUI.currentTask);
				System.out.println(simBattleUI.tasks.toString());
			} else {
				System.out.println("------------------------------------------");
				System.out.println("Current Task: " + ui.currentTask);
				System.out.println(ui.tasks.toString());
			}
		}
	}
	
	private void updateEntity() {
		int index = new Random().nextInt(npc[1].length);
		
		if (npc[currentMap][index] != null) npc[currentMap][index].spinRandom();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		if (gameState != BATTLE_STATE && gameState != SIM_BATTLE_STATE) {
			
			// Draw Tiles
			tileM.draw(g2, false);
			
			// Draw Items
			for (int i = 0; i < obj[1].length; i++) {
				if (obj[currentMap][i] != null) {
					obj[currentMap][i].draw(g2);
				}
			}
			
			// Draw Interactive Tiles
			for (int i = 0; i < iTile[1].length; i++) {
				if (iTile[currentMap][i] != null) {
					iTile[currentMap][i].draw(g2);
				}
			}
			
			// Draw NPCs
			for (Entity npc : renderableNPCs) {
				npc.draw(g2);
			}
			
			tileM.draw(g2, true);
			
			// Draw Particles
			for (int i = 0; i < particleList.size(); i++) {
				Particle p = (Particle) particleList.get(i);
				if (p != null) {
					if (p.alive) {
						p.draw(g2);
					} else {
						particleList.remove(i);
					}
				}
			}
			
			// Draw UI
			ui.draw(g2);
			
			// Draw Tooltips
			drawOverworldToolTips(g2);
		
		} else if (gameState == SIM_BATTLE_STATE) {
			// Draw Sim Battle Screen
			simBattleUI.draw(g2);
		} else {
			// Draw Battle Screen
			battleUI.draw(g2);
		}
		
		g2.dispose();
	}
	
	public void openBox(NPC_PC target) {
		keyH.resetKeys(false);
		
		if (player.p.boxLabels == null) {
			player.p.boxLabels = player.p.setupBoxLabels();
		}
		
		ui.isGauntlet = target.isGauntlet();
		ui.npc = target;
		ui.gauntlet = ui.isGauntlet;
		ui.boxSwapNum = -1;
		gameState = GamePanel.BOX_STATE;
	}
	
	public void openMap() {
		
		PMap map = new PMap(this);
		removePanel();
		addPanel(map, true);
	}
	
	public void wipe(Pokemon user, Pokemon foe) {
		wipe(false, user, foe);
	}
	
	public void wipe(boolean overLevelCap, Pokemon user, Pokemon foe) {
		if (!overLevelCap) {
			endBattle(-1, -1);
			user.getPlayer().setMoney(user.getPlayer().getMoney() - 500);
			user.trainer.heal();
			if (foe.trainer != null) {
				foe.trainer.reset();
			}
		}
		setTaskState();
		Task t = Task.addTask(Task.TELEPORT, "");
		t.counter = Player.spawn[0];
		t.start = Player.spawn[1];
		t.finish = Player.spawn[2];
		t.wipe = false;
		if (overLevelCap) Task.addTask(Task.TEXT, "You have at least 1 team member that is over the level cap of " + Trainer.getLevelCap(player.p.badges) + "!");
		
		Item.useCalc(player.p.current, null, null, false);
	}

	public void endBattle(int trainer, int id) {
		player.resetSpriteNum();
		boolean wiped = player.p.wiped();
		if (!wiped) {
			if (trainer > -1 && trainer != 256) {
				player.p.trainersBeat[trainer] = true;
				
				Trainer t = Trainer.getTrainer(trainer);
				
				if (t.getFlagIndex() != 0) {
		        	player.p.flag[t.getFlagX()][t.getFlagY()] = true;
		        }
				
				if (t.getItem() != null) {
					Item item = t.getItem();
					setTaskState();
					Task task = Task.addTask(Task.ITEM, "");
					task.item = item;
					if (item == Item.TEMPLE_ORB) {
						task.counter = 10;
					}
				}
				
				if (trainer > -1 && t.update) {
					if (id == 159) player.p.grustCount++;
					aSetter.updateNPC(currentMap);
				}
				
				if (id == 234 && !player.p.flag[7][16]) {
					if (t.getCurrent().isFainted()) { // player killed the faith dragon
						player.p.trainersBeat[trainer] = false;
						player.p.flag[7][15] = false;
						t.reset();
						setTaskState();
						Task.addTask(Task.DIALOGUE, npc[196][1], "Finn... you failed...");
						Task.addTask(Task.DIALOGUE, npc[196][1], "Oh Arceus, what do we do?!");
						int[] loc = puzzleM.FAITH_START;
						Task task = Task.addTask(Task.TELEPORT, "");
						task.counter = loc[0];
						task.start = loc[1];
						task.finish = loc[2];
						puzzleM.doReset(true);
						Task.addTask(Task.TURN, player, "", Task.UP);
					} else {
						player.p.tempTeam = player.p.team;
						player.p.team = new Pokemon[6];
						player.p.team[0] = t.getCurrent();
						player.p.setCurrent();
						player.interactNPC(npc[196][2], false);
					}
				}
				if (id == 233 && !player.p.flag[7][16]) {
					if (t.getCurrent().isFainted()) { // player killed the logic dragon
						player.p.trainersBeat[trainer] = false;
						player.p.flag[7][14] = false;
						t.reset();
						setTaskState();
						Task.addTask(Task.DIALOGUE, npc[202][1], "Finn... you failed...");
						Task.addTask(Task.DIALOGUE, npc[202][1], "Oh Arceus, what do we do?!");
						int[] loc = puzzleM.LOGIC_START;
						Task task = Task.addTask(Task.TELEPORT, "");
						task.counter = loc[0];
						task.start = loc[1];
						task.finish = loc[2];
						puzzleM.doReset(false);
						Task.addTask(Task.TURN, player, "", Task.UP);
					} else {
						player.p.tempTeam = player.p.team;
						player.p.team = new Pokemon[6];
						player.p.team[0] = t.getCurrent();
						player.p.setCurrent();
						player.interactNPC(npc[202][2], false);
					}
				}
			} else if (trainer < 0) {
				if (player.p.nuzlocke) player.p.removeEncounterArea(PlayerCharacter.getMetAt());
			}
		}
		if (id == 235) {
			player.interactNPC(npc[currentMap][2], false);
		}

		player.p.resetTeam();
		player.p.amulet = false;
		
		Pokemon.field = new Field();
		battleUI.tasks = new ArrayList<>();
		battleUI.currentTask = null;
		battleUI.tempUser = null;
		battleUI.tempFoe = null;
		battleUI.weather = null;
		battleUI.terrain = null;
		battleUI.moveNum = 0;
		battleUI.foeFainted = 0;
	}
	
	public void endSim() {
		Pokemon.field = new Field();
		simBattleUI.tasks = new ArrayList<>();
		simBattleUI.currentTask = null;
		simBattleUI.tempUser = null;
		simBattleUI.tempFoe = null;
		simBattleUI.weather = null;
		simBattleUI.terrain = null;
		simBattleUI.moveNum = 0;
		simBattleUI.foeFainted = 0;
	}
	
	public void addPanel(JPanel panel, boolean animate) {
		if (animate) {
			SwingUtilities.invokeLater(() -> {
				// Create a JPanel for the fade effect
			    FadingPanel fadePanel = new FadingPanel();
			    fadePanel.setBackground(Color.BLACK);
			    fadePanel.setBounds(0, 0, window.getWidth(), window.getHeight());
			    window.add(fadePanel, 0);
			    
			    // Create a Timer to gradually change the opacity of the fadePanel
			    Timer timer = new Timer(20, null);

			    timer.addActionListener(e -> {
			    	fadePanel.setBackground(new Color(fadePanel.getBackground().getRed() + 5, fadePanel.getBackground().getGreen() + 5, fadePanel.getBackground().getBlue() + 5));
			        int alpha = fadePanel.getAlpha();
			        alpha += 2; // Adjust the fading speed
			        fadePanel.setAlpha(alpha);
			        fadePanel.repaint();
		
			        if (alpha >= 90) {
			        	removePanel();
			            timer.stop();
			    		
			            addPanel(panel);
			        }
			    });
				
				timer.start();
			});
		} else {
			addPanel(panel);
		}
	}
	
	public void addPanel(JPanel panel) {
		// Add the Battle
	    Main.window.getContentPane().add(panel);

	    // Set focus on the Battle
	    panel.requestFocusInWindow();

	    // Repaint the JFrame to reflect the changes
	    Main.window.revalidate();
	    Main.window.repaint();
	}
	
	public void removePanel() {
		// Remove all existing components from the JFrame
	    Main.window.getContentPane().removeAll();
	}
	
	public void addGamePanel() {
		keyH.resetKeys();
		Main.window.add(this);
		
		this.requestFocusInWindow();

	    // Repaint the JFrame to reflect the changes
	    Main.window.revalidate();
	    Main.window.repaint();
		
	}

	public void setupGame() {
		aSetter.setNPC();
		aSetter.setObject();
		aSetter.setInteractiveTile(currentMap);
		
		script = new Script(this);
		
		gameState = PLAY_STATE;
		
		Pokemon.field = new Field();
		Pokemon.gp = this;
		Task.gp = this;
		checkSpin = true;
		
		Pokemon.readTrainersFromCSV();
		
		Pokemon test = new Pokemon(1, 1, true, false);
		test.trainer = player.p.clone(this);
		test.trainer.team[0] = test;
		test.trainer.current = test;
		Item.useCalc(test, null, null, false);
	}

	public Pokemon encounterPokemon(String area, char type, boolean random) {
		if (currentMap == 149) return null;
	    ArrayList<Encounter> encounters = Encounter.getEncounters(area, type, random);

	    // Calculate the total encounter chance for the route
	    double totalChance = 0.0;
	    for (Encounter encounter : encounters) {
	        totalChance += encounter.getEncounterChance();
	    }

	    // Randomly select an encounter based on the Pokemon's encounter chance
	    double rand = Math.random() * totalChance;
	    for (Encounter encounter : encounters) {
	        rand -= encounter.getEncounterChance();
	        if (rand < 0) {
	            // Randomly generate a level within the level range
	            int level = (int) (Math.random() * (encounter.getMaxLevel() - encounter.getMinLevel() + 1) + encounter.getMinLevel());
	            return new Pokemon(encounter.getId(), level, false, false);
	        }
	    }

	    // If no encounter was selected, return null
	    JOptionPane.showMessageDialog(null, "No encounters available for this combination.");
	    return new Pokemon(10, 5, false, false);
	}
	
	private void drawOverworldToolTips(Graphics2D g2) {
		if (!keyH.shiftPressed || gameState != PLAY_STATE) return;
		int x = 0;
		int y = (int) (tileSize * 7.5);
		int width = tileSize * 4;
		int height = (int) (tileSize * 1.5);
		
		ui.drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		x += tileSize / 2;
		y += tileSize;
		
		g2.drawString("[Ctrl]+[A] Calc", x, y);
		
		width = tileSize * 12;
		x -= tileSize / 2;
		y += tileSize / 2;
		
		ui.drawSubWindow(x, y, width, height);
		
		x += tileSize / 2;
		y += tileSize;
		
		g2.drawString("[\u2190][\u2191][\u2192][\u2193] Move    [TAB] Speedup    [ENTER] Screenshot", x, y);
		
		String aText = player.p.fish ? "Fish" : null;
		ui.drawToolTips("Talk", aText, "Run", "Menu");
		
		x = (int) (screenWidth - tileSize * 4.25);
		y = 0;
		width = (int) (tileSize * 4.25);
		ui.drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(28F));
		
		x += tileSize / 2;
		y += tileSize;
		
		double xCoord = player.worldX * 1.0 / tileSize;
		double yCoord = player.worldY * 1.0 / tileSize;
		
		g2.drawString(String.format("X: %.2f, Y: %.2f", xCoord, yCoord), x, y);
		
		x -= tileSize * 1.25;
		y += tileSize / 2;
		width = (int) (tileSize * 5);
		ui.drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		
		x += tileSize / 2;
		y += tileSize;
		
		g2.drawString(String.format("Map: %s (%d)", TileManager.mapNames[currentMap],currentMap), x, y);
	}
	
	public boolean determineLightOverlay() {
		return (currentMap == 38 || currentMap == 166) && !player.p.flag[3][8];
	}
	
	public boolean determineMachineLight() {
		if (player.p.flag[6][6] && !player.p.flag[7][4]) {
			if (currentMap == 128) return false;
			int mult = player.p.visor ? 2 : 1;
			if (currentMap == 13 || currentMap == 28 || currentMap == 146 || currentMap == 149) { // in splinkty 5A or outside splinkty
				ui.fog.setIntensity(0.75 * mult);
			} else if (currentMap == 24 || currentMap == 25 || currentMap == 26 || currentMap == 145 || currentMap == 27 || currentMap == 148) { // in splinkty rest
				ui.fog.setIntensity(1 * mult);
			} else {
				ui.fog.setIntensity(2 * mult);
			}
			return true;
		} else {
			return false;
		}
	}

	public void setTaskState() {
		ui.checkTasks = false;
		gameState = TASK_STATE;
	}

	public void setRenderableNPCs() {
		renderableNPCs.clear();
		for (int i = 0; i < npc[1].length; i++) {
			if (npc[currentMap][i] != null) {
				renderableNPCs.add(npc[currentMap][i]);
			}
		}
		for (TreasureChest chest : chests) {
			if (chest.map == currentMap) renderableNPCs.add(chest);
		}
		
		renderableNPCs.add(player);
		renderableNPCs.sort(Comparator.comparingInt(Entity::getWorldY));
	}
	
	public void saveGame() {
		Path savesDirectory = Paths.get("./saves/");
        if (!Files.exists(savesDirectory)) {
            try {
				Files.createDirectories(savesDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./saves/" + player.currentSave))) {
        	player.p.setPosX(player.worldX);
        	player.p.setPosY(player.worldY);
        	player.p.currentMap = currentMap;
            oos.writeObject(player.p);
            oos.close();
        } catch (IOException ex) {
        	JOptionPane.showMessageDialog(null, "Error writing object to file: " + ex.getMessage());
        }
	}
	
	public ArrayList<Task> getTasks() {
		switch(gameState) {
		case BATTLE_STATE:
			return battleUI.tasks;
		case SIM_BATTLE_STATE:
			return simBattleUI.tasks;
		}
		return ui.tasks;
	}
	
	public AbstractUI getActiveUI() {
		if (gameState == BATTLE_STATE) return battleUI;
		if (gameState == SIM_BATTLE_STATE) return simBattleUI;
		return ui;
	}

	public BattleUI getBattleUI() {
		if (gameState == BATTLE_STATE) return battleUI;
		if (gameState == SIM_BATTLE_STATE) return simBattleUI;
		return simBattleUI;
	}

	public boolean canFly() {
		boolean canFly = true;
		if (!player.p.flag[1][2]) canFly = false;
		if (!tileM.canFly[currentMap]) canFly = false;
		if (currentMap == 18 && !player.p.flag[1][13]) canFly = false; // Sicab Office Scott
		if (currentMap == 28 && !player.p.flag[2][10]) canFly = false; // Kleine Village Cage Critter
		if (currentMap == 44 && player.p.flag[3][12] && !player.p.flag[4][0]) canFly = false; // After beating Glacius but still needing to do Robin cutscene
		if (currentMap == 113 && player.p.flag[5][8] && !player.p.flag[6][0]) canFly = false; // After beating Rayna but still needing to do Scott cutscene
		if (currentMap == 128 && player.p.flag[6][6] && !player.p.flag[7][0]) canFly = false; // After beating Merlin but still needing to do Arthra cutscene
		return canFly;
	}

	public int getEffectiveBadges(Pokemon foe) {
		return foe.trainerOwned() && foe.trainer.getMoney() == 500 ? player.p.badges + 1 : player.p.badges;
	}

}