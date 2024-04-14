package overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import entity.Entity;
import entity.NPC_PC;
import entity.NPC_Pokemon;
import entity.PlayerCharacter;
import object.InteractiveTile;
import object.ItemObj;
import pokemon.Encounter;
import pokemon.Field;
import pokemon.Item;
import pokemon.PBox;
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
	final int scale = 3; // 3, 16, 12
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 16; // 48
	public final int maxScreenRow = 12; // 36
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	public final int maxWorldCol = 100;
	public final int maxWorldRow = 100;
	public final int maxMap = 200;
	public int currentMap = 0;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	public static final int MAX_FLAGS = 40;
	
	public KeyHandler keyH = new KeyHandler(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public EventHandler eHandler = new EventHandler(this);
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public PlayerCharacter player = new PlayerCharacter(this, keyH);
	public Entity npc[][] = new Entity[maxMap][20];
	public ItemObj obj[][] = new ItemObj[maxMap][35];
	public InteractiveTile iTile[][] = new InteractiveTile[maxMap][55];
	
	public NPC_Pokemon[] grusts = new NPC_Pokemon[10];
	public boolean checkSpin = false;
	
	public TileManager tileM = new TileManager(this);
	
	int FPS = 60;
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
	public static final int PARTY_STATE = 8;
	public static final int BAG_STATE = 9;
	public static final int SAVE_STATE = 10;
	public static final int PLAYER_STATE = 11;
	public static final int START_BATTLE_STATE = 12;
	public static final int USE_ITEM_STATE = 13;
	public static final int USE_RARE_CANDY_STATE = 14;

	public static Map<Entity, Integer> volatileTrainers = new HashMap<>();
	
	public GamePanel(JFrame window) {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		
		this.window = window;
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
			player.update();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		if (gameState != BATTLE_STATE) {
			
			// Draw Tiles
			tileM.draw(g2);
			
			// Draw NPCs
			for (int i = 0; i < npc[1].length; i++) {
				if (npc[currentMap][i] != null) {
					npc[currentMap][i].draw(g2);
				}
			}
			
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
			
			// Draw Player
			player.draw(g2);
			
			// Draw UI
			ui.draw(g2);
		
		} else {
			// Draw Battle Screen
			battleUI.draw(g2);
		}
		
		g2.dispose();
	}
	
	public void startBattle(int trainer) {
		if (trainer == -1) return;
		if (player.p.trainersBeat[trainer]) return;
		if (player.p.wiped()) return;
		
		Pokemon foe = Main.trainers[trainer].getCurrent();
		
		// Heal if Gym Leader
		if (Main.trainers[trainer].getMoney() == 500) {
			player.p.heal();
		}
		
		startBattle(trainer, -1, foe);
	}
	
	public void startBattle(int trainer, int id, Pokemon foe) {	
		setSlots();
		
		ui.transitionBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		gameState = START_BATTLE_STATE;
		
		// Make sure the front Pokemon isn't fainted
		int index = 0;
		Pokemon user = player.p.getCurrent();
		while (user.isFainted()) {
			player.p.swapToFront(player.p.team[++index], index);
			user = player.p.getCurrent();
		}
		
		player.p.clearBattled();
		user.battled = true;
		battleUI.user = user;
		battleUI.foe = foe;
		battleUI.index = trainer;
		battleUI.staticID = id;
	}
	
	public void startBattle(int trainer, int id) {
		Pokemon foe = Main.trainers[trainer].getCurrent();
		startBattle(trainer, id, foe);
	}
	
	public void startWild(int area, int x, int y, String type) {
		Pokemon foe = encounterPokemon(area, x, y, player.p.random, type);
		startBattle(-2, -1, foe);
	}
	
	public void startFish(int area, int x, int y) {
		startWild(area, x, y, "Fishing");
	}
	
	public void openBox(NPC_PC target) {
	    // Create the Battle instance and set the window listener to save on close

		keyH.resetKeys();
	    PBox box = new PBox(this, keyH, target.isGauntlet());
	    removePanel();
	    addPanel(box, true);
	}
	
	public void openMap() {
		
		PMap map = new PMap(this);
		removePanel();
		addPanel(map, true);
	}


	public void endBattle(int trainer, int id) {
		
		if (trainer > -1 && !player.p.wiped() && trainer != 256) player.p.trainersBeat[trainer] = true;
		if (id == 159) {
			player.p.grustCount++;
			aSetter.updateNPC(107);
		}
		if (id == 232) npc[148][0] = null;
		if (id == 229) npc[150][0] = null;
		Pokemon[] teamTemp = Arrays.copyOf(player.p.team, 6);
		for (int i = 0; i < 6; i++) {
			if (teamTemp[i] != null) {
				if (teamTemp[i].id == 237) {
					teamTemp[i].id = 150;
					teamTemp[i].nickname = teamTemp[i].nickname.equals("Kissyfishy-D") ? teamTemp[i].nickname = teamTemp[i].getName() : teamTemp[i].nickname;
					
					teamTemp[i].setBaseStats();
					teamTemp[i].getStats();
					teamTemp[i].weight = teamTemp[i].setWeight();
					teamTemp[i].setType();
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
	}
	
	public void setSlots() {
		for (int i = 0; i < 6; i++) {
			if (player.p.team[i] != null) player.p.team[i].slot = i;
		}
	}

	public Pokemon encounterPokemon(int area, int x, int y, boolean random, String type) {
	    ArrayList<Encounter> encounters = Encounter.getEncounters(area, x, y, type, "D", random);

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

}