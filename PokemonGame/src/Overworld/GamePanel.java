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

import Entity.Entity;
import Entity.NPC_Pokemon;
import Entity.PlayerCharacter;
import Obj.InteractiveTile;
import Obj.ItemObj;
import tile.TileManager;
import Swing.Battle;
import Swing.Battle.BattleCloseListener;
import Swing.Field;
import Swing.PBox;
import Swing.Pokemon;
import Swing.TextPane;

public class GamePanel extends JPanel implements Runnable, BattleCloseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1898610723315381969L;
	
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
	public final int maxMap = 150;
	public int currentMap = 0;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	public static final int maxFlags = 30;
	
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
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
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
		
		Battle frame = new Battle(player, Main.trainers[trainer], trainer, this, -1, -1, -1, null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBattleCloseListener(this);
        frame.setVisible(true);
        
        return frame;
	}
	
	public void startBattle(int trainer, int id) {
		Battle frame = startBattle(trainer);
		frame.staticPokemonID = id;
	}
	
	public void startWild(int area, int x, int y, String type) {
	    // Create the Battle instance and set the window listener to save on close
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle frame = new Battle(player, null, -1, this, area, x, y, type);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBattleCloseListener(this);
        frame.setVisible(true);
	}
	
	public void startFish(int area, int x, int y) {
	    // Create the Battle instance and set the window listener to save on close
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle frame = new Battle(player, null, -1, this, area, x, y, null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBattleCloseListener(this);
        frame.setVisible(true);
	}
	
	public void openBox() {
	    // Create the Battle instance and set the window listener to save on close
	    keyH.pause();

	    PBox box = new PBox(player, keyH);
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


	@Override
	public void onBattleClosed(int trainer, int id) {
		if (trainer > -1 && !player.p.wiped() && trainer != 256) player.p.trainersBeat[trainer] = true;
		if (id == 159) {
			player.p.grustCount++;
			aSetter.updateNPC();
		}
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