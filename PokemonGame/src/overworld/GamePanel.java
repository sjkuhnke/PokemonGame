package overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import entity.Entity;
import entity.NPC_PC;
import entity.NPC_Pokemon;
import entity.Particle;
import entity.PlayerCharacter;
import object.InteractiveTile;
import object.ItemObj;
import pokemon.Encounter;
import pokemon.Field;
import pokemon.Item;
import pokemon.Player;
import pokemon.Pokemon;
import tile.TileManager;

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
	public static final int MAX_MAP = 200;
	public static final int MAX_FLAG = 20; // should not be >31
	
	public final String gameTitle = "Pokemon Xhenos";
	
	public KeyHandler keyH = new KeyHandler(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public EventHandler eHandler = new EventHandler(this);
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public PlayerCharacter player = new PlayerCharacter(this, keyH);
	public Entity npc[][] = new Entity[MAX_MAP][40];
	public ItemObj obj[][] = new ItemObj[MAX_MAP][35];
	public InteractiveTile iTile[][] = new InteractiveTile[MAX_MAP][55];
	public ArrayList<Entity> particleList = new ArrayList<>();
	
	public NPC_Pokemon[] grusts = new NPC_Pokemon[10];
	public boolean checkSpin = false;
	
	public TileManager tileM = new TileManager(this);
	
	public int FPS = 60;
	public int ticks;
	
	public UI ui = new UI(this);
	public BattleUI battleUI = new BattleUI(this);
	
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

	public static Map<Entity, Integer> volatileTrainers = new HashMap<>(); // TODO: remove this once aSetter.setNPC() is correctly configured to not have volatile npcs
	
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
	}
	
	private void updateEntity() {
		int index = new Random().nextInt(npc[1].length);
		
		if (npc[currentMap][index] != null) npc[currentMap][index].spinRandom();
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		if (gameState != BATTLE_STATE) {
			
			// Draw Tiles
			tileM.draw(g2);
			
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
			for (int i = 0; i < npc[1].length; i++) {
				if (npc[currentMap][i] != null) {
					npc[currentMap][i].draw(g2);
				}
			}
			
			// Draw Player
			player.draw(g2);
			
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
		
		} else {
			// Draw Battle Screen
			battleUI.draw(g2);
		}
		
		g2.dispose();
	}
	
	public void openBox(NPC_PC target) {
		keyH.resetKeys();
		
		if (player.p.boxLabels == null) {
			player.p.boxLabels = player.p.setupBoxLabels();
		}
		
		ui.isGauntlet = target.isGauntlet();
		ui.npc = target;
		if (ui.isGauntlet) ui.gauntlet = true;
		ui.boxSwapNum = -1;
		gameState = GamePanel.BOX_STATE;
	}
	
	public void openMap() {
		
		PMap map = new PMap(this);
		removePanel();
		addPanel(map, true);
	}


	public void endBattle(int trainer, int id) {
		if (trainer > -1 && !player.p.wiped() && trainer != 256) player.p.trainersBeat[trainer] = true;
		
		if (id >= 0 || trainer == 89) {
			if (id == 159) player.p.grustCount++;
			aSetter.updateNPC(currentMap);
		}

		Pokemon[] teamTemp = Arrays.copyOf(player.p.team, 6);
		for (int i = 0; i < 6; i++) {
			if (teamTemp[i] != null) {
				if (teamTemp[i].id == 237) {
					teamTemp[i].id = 150;
					teamTemp[i].nickname = teamTemp[i].nickname.equals("Kissyfishy-D") ? teamTemp[i].nickname = teamTemp[i].getName() : teamTemp[i].nickname;
					
					teamTemp[i].baseStats = teamTemp[i].getBaseStats();
					teamTemp[i].setStats();
					teamTemp[i].weight = teamTemp[i].getWeight();
					teamTemp[i].setTypes();
					teamTemp[i].setAbility(teamTemp[i].abilitySlot);
					teamTemp[i].currentHP = teamTemp[i].currentHP > teamTemp[i].getStat(0) ? teamTemp[i].getStat(0) : teamTemp[i].currentHP;
				}
				player.p.team[teamTemp[i].slot] = teamTemp[i];
				teamTemp[i].clearVolatile();
				
				if (teamTemp[i].loseItem) {
					teamTemp[i].item = null;
					teamTemp[i].loseItem = false;
				}
				if (teamTemp[i].lostItem != null) {
					teamTemp[i].item = teamTemp[i].lostItem;
					teamTemp[i].lostItem = null;
					if (teamTemp[i].item == Item.POTION) teamTemp[i].item = null;
				}
			}
		}
		player.p.setCurrent(player.p.team[0]);
		
		Pokemon.field = new Field();
		battleUI.tasks = new ArrayList<>();
		battleUI.currentTask = null;
		battleUI.tempUser = null;
		battleUI.weather = null;
		battleUI.terrain = null;
		battleUI.moveNum = 0;
		battleUI.foeFainted = 0;
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
		
		gameState = PLAY_STATE;
		
		Pokemon.field = new Field();
		Pokemon.gp = this;
		checkSpin = true;
		
		Pokemon.readTrainersFromCSV();
	}

	public Pokemon encounterPokemon(String area, char type, boolean random) {
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
	}

	public boolean determineLightOverlay() {
		return currentMap == 38 && !player.p.flag[3][8];
	}

	public void setTaskState() {
		ui.checkTasks = false;
		gameState = TASK_STATE;
	}

}