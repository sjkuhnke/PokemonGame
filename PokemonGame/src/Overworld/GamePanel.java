package Overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Entity.Entity;
import Entity.PlayerCharacter;
import tile.TileManager;
import Swing.Battle;
import Swing.Battle.BattleCloseListener;
import Swing.PBox;
import Swing.Pokemon;

public class GamePanel extends JPanel implements Runnable, BattleCloseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1898610723315381969L;
	
	// SETTINGS
	final int originalTileSize = 16;
	final int scale = 3;
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	public final int maxWorldCol = 100;
	public final int maxWorldRow = 100;
	public final int maxMap = 20;
	public int currentMap = 0;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	KeyHandler keyH = new KeyHandler();
	public AssetSetter aSetter = new AssetSetter(this);
	public EventHandler eHandler = new EventHandler(this);
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public PlayerCharacter player = new PlayerCharacter(this,keyH);
	public Entity npc[][] = new Entity[maxMap][90];
	
	TileManager tileM = new TileManager(this);
	
	int FPS = 60;
	private volatile boolean inBattle;
	public int ticks;

	
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
		
		player.draw(g2);
		
		g2.dispose();
	}
	
	public void startBattle(int trainer) {
	    // Create the Battle instance and set the window listener to save on close
		if (trainer == -1) return;
		if (player.p.trainersBeat[trainer]) return;
		if (player.p.wiped()) return;
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle frame = new Battle(player, Main.trainers[trainer], trainer, this, -1, -1, -1);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBattleCloseListener(this);
        frame.setVisible(true);
	}
	
	public void startWild(int area, int x, int y) {
	    // Create the Battle instance and set the window listener to save on close
		inBattle = true;
		keyH.pause();
		setSlots();
		
		Battle frame = new Battle(player, null, -1, this, area, x, y);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setBattleCloseListener(this);
        frame.setVisible(true);
	}
	
	public void openBox() {
	    // Create the Battle instance and set the window listener to save on close
	    keyH.pause();

	    PBox box = new PBox(player);
	    box.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            keyH.resume();
	        }
	    });

	    box.setVisible(true);
	}


	@Override
	public void onBattleClosed(int trainer) {
		inBattle = false;
		keyH.resume();
		if (trainer > -1 && !player.p.wiped()) player.p.trainersBeat[trainer] = true;
		Pokemon[] teamTemp = Arrays.copyOf(player.p.team, 6);
		for (int i = 0; i < 6; i++) {
			if (teamTemp[i] != null) {
				if (teamTemp[i].id == 237) {
					teamTemp[i].id = 150;
					teamTemp[i].name = teamTemp[i].getName();
					
					teamTemp[i].setBaseStats();
					teamTemp[i].getStats();
					teamTemp[i].weight = teamTemp[i].setWeight();
					teamTemp[i].setType();
					teamTemp[i].setAbility(teamTemp[i].abilitySlot);
					teamTemp[i].currentHP = teamTemp[i].currentHP > teamTemp[i].getStat(0) ? teamTemp[i].getStat(0) : teamTemp[i].currentHP;
				}
				player.p.team[teamTemp[i].slot] = teamTemp[i];
				if (teamTemp[i].slot == 0) player.p.current = player.p.team[0];
				teamTemp[i].setType();
			}
		}
	}

	public void setupGame() {
		aSetter.setNPC();
	}
	
	public void setSlots() {
		for (int i = 0; i < 6; i++) {
			if (player.p.team[i] != null) player.p.team[i].slot = i;
		}
	}


}
