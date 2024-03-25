package Overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Entity.Entity;
import Entity.NPC_PC;
import Entity.NPC_Pokemon;
import Entity.PlayerCharacter;
import Obj.InteractiveTile;
import Obj.ItemObj;
import tile.TileManager;
import Swing.Battle;
import Swing.Battle.BattleCloseListener;
import Swing.Field;
import Swing.Item;
import Swing.PBox;
import Swing.Pokemon;
import Swing.TextPane;

public class GamePanel extends JPanel implements Runnable, BattleCloseListener {

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
	public static final int MAX_FLAGS = 30;
	
	public KeyHandler keyH = new KeyHandler();
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
	private volatile boolean inBattle;
	public boolean mapOpen = false;
	public int ticks;

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
			if (!inBattle) {
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
	}
	
	public void update() {
		player.update();
		
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		tileM.draw(g2);
		
		for (int i = 0; i < npc[1].length; i++) {
			if (npc[currentMap][i] != null) {
				npc[currentMap][i].draw(g2);
			}
		}
		
		for (int i = 0; i < obj[1].length; i++) {
			if (obj[currentMap][i] != null) {
				obj[currentMap][i].draw(g2);
			}
		}
		
		for (int i = 0; i < iTile[1].length; i++) {
			if (iTile[currentMap][i] != null) {
				iTile[currentMap][i].draw(g2);
			}
		}
		
		player.draw(g2);
		
		g2.dispose();
	}
	
	public Battle startBattle(int trainer) {
	    // Create the Battle instance and set the window listener to save on close
		if (trainer == -1) return null;
		if (player.p.trainersBeat[trainer]) return null;
		if (player.p.wiped()) return null;
		inBattle = true;
		keyH.pause();
		setSlots();
		
		if (Main.trainers[trainer].getMoney() == 500) {
			for (Pokemon member : player.p.team) {
				if (member != null) member.heal();
			}
		}
		
		Battle panel = new Battle(player, Main.trainers[trainer], trainer, this, -1, -1, -1, null);
		//panel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); JFrame exclusive
        //panel.setBattleCloseListener(this);
		removePanel();
		addBattle(panel);
		
        panel.setVisible(true);
        
        return panel;
	}
	
	public void startBattle(int trainer, int id) {
		Battle frame = startBattle(trainer);
		if (frame != null) frame.staticPokemonID = id;
	}
	
	public void startWild(int area, int x, int y, String type) {
	    // Create the Battle instance and set the window listener to save on close
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle panel = new Battle(player, null, -1, this, area, x, y, type);
		//panel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); JFrame exclusive
		removePanel();
		addBattle(panel);
		
		panel.setVisible(true);
	}
	
	public void startFish(int area, int x, int y) {
	    // Create the Battle instance and set the window listener to save on close
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle panel = new Battle(player, null, -1, this, area, x, y, null);
		//panel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); JFrame exclusive
		removePanel();
		addBattle(panel);
		
		panel.setVisible(true);
	}
	
	public void openBox(NPC_PC target) {
	    // Create the Battle instance and set the window listener to save on close
	    keyH.pause();

	    PBox box = new PBox(player, keyH, target.isGauntlet());
	    box.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            keyH.resume();
	        }
	    });

	    box.setVisible(true);
	}
	
	public void openMap() {
		keyH.pause();
		mapOpen = true;
		
		PMap map = new PMap(this);
		map.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	keyH.resume();
	        	mapOpen = false;
	        }
	    });
		
		map.setVisible(true);
	}


	public void endBattle(int trainer, int id) {
		removePanel();
		
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
				teamTemp[i].setType();
				
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
		player.p.current = player.p.team[0];
		
		Pokemon.field = new Field();
		
		addGamePanel();
	}
	
	private void addBattle(Battle battle) {
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
//		        if (alpha % 4 == 0) {
//		    		fadePanel.setBackground(Color.RED);
//		    	} else {
//		    		fadePanel.setBackground(Color.GREEN);
//		    	}
		        alpha += 2; // Adjust the fading speed
		        fadePanel.setAlpha(alpha);
		        fadePanel.repaint();
	
		        if (alpha >= 90) {
		        	removePanel();
		            timer.stop();
		    		
		            // Add the Battle
		    	    Main.window.getContentPane().add(battle);

		    	    // Set focus on the Battle
		    	    battle.requestFocusInWindow();

		    	    // Repaint the JFrame to reflect the changes
		    	    Main.window.revalidate();
		    	    Main.window.repaint();
		        }
		    });
			
			timer.start();
		});
	}
	
	private void removePanel() {
		// Remove all existing components from the JFrame
	    Main.window.getContentPane().removeAll();
	}
	
	private void addGamePanel() {
		Main.window.add(this);
		
		this.requestFocusInWindow();

	    // Repaint the JFrame to reflect the changes
	    Main.window.revalidate();
	    Main.window.repaint();
		
		inBattle = false;
		keyH.resume();
	}
	
	@Override
	public void onBattleClosed(int trainer, int id) {
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
				teamTemp[i].setType();
				
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
		player.p.current = player.p.team[0];
		
		Pokemon.field = new Field();
		
		inBattle = false;
		keyH.resume();
	}

	public void setupGame() {
		aSetter.setNPC();
		aSetter.setObject();
		aSetter.setInteractiveTile(currentMap);
		Pokemon.console = new TextPane();
		Pokemon.console.setScrollPane(new JScrollPane());
		Pokemon.field = new Field();
		checkSpin = true;
	}
	
	public void setSlots() {
		for (int i = 0; i < 6; i++) {
			if (player.p.team[i] != null) player.p.team[i].slot = i;
		}
	}


}