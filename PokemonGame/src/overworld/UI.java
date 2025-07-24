package overworld;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.SwingUtilities;

import entity.*;
import object.*;
import pokemon.*;
import pokemon.Bag.SortType;
import puzzle.Puzzle;
import util.Pair;

public class UI extends AbstractUI {

	public int menuNum = 0;
	public int subState = 0;
	public int slotCol = 0;
	public int slotRow = 0;
	public int bagNum[] = new int[Item.KEY_ITEM];
	public int selectedBagNum = -1;
	public int currentPocket = Item.MEDICINE;
	public int bagState;
	public int sellAmt = 1;
	public Item currentItem;
	public ArrayList<Bag.Entry> currentItems;
	
	public int btX = 0;
	public int btY = 0;
	
	public boolean showMessage;
	public boolean above;
	public boolean showArea;
	public int areaCounter;
	public Pokemon currentPokemon;
	public Move currentMove;
	public String currentHeader;
	
	public int boxNum;
	public int pSelectBox;
	public boolean isGauntlet;
	public Pokemon currentBoxP;
	public boolean release;
	
	public int dexNum[] = new int[4];
	public int dexMode;
	public int dexType;
	public int levelDexNum;
	public int tmDexNum;
	public int starAmt;
	public int premier;
	public Item tmCheck;
	public boolean wasAPressed;
	public BufferedImage[] silhouettes = new BufferedImage[Pokemon.MAX_POKEMON];
	
	public int remindNum;
	public boolean drawFlash;
	public Color flashColor;
	
	public int starter;
	public boolean starterConfirm;
	public boolean addItem;
	
	// Light Distortion fields
	public boolean drawLightOverlay;
	private List<BufferedImage> lightFrames;
	private int lightFrameIndex;
	private long lightLastFrameTime;
	
	// Space GIF fields
	private List<BufferedImage> spaceFrames;
	private int spaceFrameIndex = 0;
	private int spaceFrameCounter;
	
	// diagonal cutscene fields
	private double errorX;
	private double errorY;
	private int startX;
	private int startY;
	private boolean assignedStart;
	
	private ArrayList<ArrayList<Encounter>> encounters;
	
	BufferedImage interactIcon;
	BufferedImage transitionBuffer;
	
	private BufferedImage[] bagIcons;
	
	private String[][][] letter = new String[2][][];
	private int currentLetter;
	private int maxLine = 13;
	public int pageNum = 0;
	
	public Fog fog;
	public boolean drawLight;
	
	public Font currentFont;
	
	private Random rand = new Random();
	
	public static final int MAX_SHOP_COL = 11;
	public static final int MAX_SHOP_ROW = 4;
	
	public static final int ITEMS = 0;
	public static final int MAX_TEXTBOX = 43;
	
	public UI(GamePanel gp) {
		this.gp = gp;
		
		currentDialogue = "";
		commandNum = 0;
		tasks = new ArrayList<>();
		
		ballIcon = setup("/icons/ball", 1);
		
		String[] message = loadText("/messages/letter1.txt").split("\n");
		message = breakMessage(message, 53);
		letter[0] = splitMessage(message);
		message = loadText("/messages/letter2.txt").split("\n");
		message = breakMessage(message, 73);
		letter[1] = splitMessage(message);
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/marumonica.ttf");
			marumonica = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getResourceAsStream("/font/creattion.ttf");
			creattion = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getResourceAsStream("/font/monsier-la-doulaise.ttf");
			monsier = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getResourceAsStream("/font/cryptik.ttf");
			cryptik = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getResourceAsStream("/font/consolas.ttf");
			consolas = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getResourceAsStream("/font/kids-sketchy.ttf");
			kids = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		bagIcons = new BufferedImage[7];
		for (int i = 0; i < 7; i++) {
			String imageName = Item.getPocketName(i + 1).toLowerCase().replace(' ', '_');
			bagIcons[i] = setup("/menu/" + imageName, 2);
		}
		
		lightFrames = extractFrames("/battle/light.gif");
		spaceFrames = extractFrames("/gen/space.gif");
		interactIcon = setup("/interactive/interact", 3);
		fog = new Fog(gp);
	}

	@Override
	public void showMessage(String text) {
		above = true;
		if (gp.gameState == GamePanel.PLAY_STATE) {
			gp.gameState = GamePanel.DIALOGUE_STATE;
		} else {
			showMessage = true;
		}
		currentDialogue = text;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(marumonica);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		
		if (drawLightOverlay) {
			float opacity = gp.currentMap == 38 ? 0.7f : 1.4f;
			opacity /= gp.player.p.visor ? 4 : 1;
			opacity = Math.min(opacity, 1.0f);
			drawLightDistortion(opacity);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
		
		if (drawLight) {
			fog.draw(g2);
		}
		
		if (drawFlash) {
			drawFlash(0);
		}
		
		if (showArea) {
			drawAreaName();
		}
		
		if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			drawDialogueScreen(true);
			drawToolTips("OK", null, null, null);
		}
		
		if (gp.gameState == GamePanel.DEX_NAV_STATE) {
			drawDexNav();
		}
		
		if (gp.gameState == GamePanel.LETTER_STATE) {
			drawLetterState();
		}
		
		if (gp.gameState == GamePanel.STARTER_STATE) {
			drawStarterState();
		}
		
		if (gp.gameState == GamePanel.MENU_STATE) {
			drawMenuScreen();
		}
		
		if (gp.gameState == GamePanel.TRANSITION_STATE) {
			drawTransition();
		}
		
		if (gp.gameState == GamePanel.SHOP_STATE) {
			drawShopScreen();
		}
		
		if (gp.gameState == GamePanel.STAR_SHOP_STATE) {
			drawStarShopScreen();
		}
		
		if (gp.gameState == GamePanel.PRIZE_STATE) {
			drawPrizeScreen((NPC_Prize_Shop) npc);
		}
		
		if (gp.gameState == GamePanel.COIN_STATE) {
			drawCoinState();
		}
		
		if (gp.gameState == GamePanel.NURSE_STATE) {
			drawNurseScreen();
		}
		
		if (gp.gameState == GamePanel.START_BATTLE_STATE) {
			drawBattleStartTransition(false);
		}
		
		if (gp.gameState == GamePanel.SIM_START_BATTLE_STATE) {
			drawBattleStartTransition(true);
		}
		
		if (gp.gameState == GamePanel.USE_ITEM_STATE) {
			useItem();
		}
		
		if (gp.gameState == GamePanel.RARE_CANDY_STATE) {
			rareCandyState();
		}
		
		if (gp.gameState == GamePanel.USE_REPEL_STATE) {
			drawRepelScreen();
		}
		
		if (gp.gameState == GamePanel.BOX_STATE) {
			drawBoxScreen();
		}
		
		if (gp.gameState == GamePanel.TASK_STATE) {
			taskState();
		}
		
		if (showMoveOptions) {
			showMoveOptions();
		}
		
		if (showIVOptions) {
			showIVOptions();
		}
		
		if (showStatusOptions) {
			showStatusOptions();
		}
		
		if (showBoxParty) {
			drawParty(null);
			String sText = partySelectedNum >= 0 || itemSwapP != null ? "Deselect" : "Close";
			drawToolTips("Info", "Swap", sText, "Close");
			drawBoxToolTips(true);
		}
		
		if (showBoxSummary) {
			drawSummary(currentBoxP, null);
		}
		
		if (showMessage) {
			drawDialogueScreen(above);
			if (messageSkippable()) {
				drawToolTips("OK", null, null, null);
			}
		}
		drawKeyStrokes();
	}

	private void drawTask() {
		if (currentTask == null) return;
		switch(currentTask.type) {
		case Task.LEVEL_UP:
		case Task.EVOLUTION:
		case Task.TEXT:
			showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
			break;
		case Task.MOVE:
			currentMove = currentTask.move;
			currentPokemon = currentTask.p;
			showMoveOptions = true;
			break;
		case Task.EVO_ITEM:
			gp.player.p.evolve(currentTask.p, currentTask.counter, gp);
			currentTask = null;
			break;
		case Task.EVO:
			drawEvolution(currentTask);
			drawToolTips("OK", null, null, null);
			break;
		case Task.CLOSE:
			if (tasks.size() != 0) {
				tasks.add(currentTask);
				currentTask = null;
				return;
			}
			gp.gameState = GamePanel.MENU_STATE;
			currentItems = gp.player.p.getItems(currentPocket);
			bagState = 0;
			currentTask = null;
			break;
		case Task.GIFT:
			// currentTask.wipe = gift is egg
			boolean result = gp.player.p.catchPokemon(currentTask.p, !currentTask.wipe);
			currentTask.p.item = currentTask.item;
			if (!currentTask.wipe && result) setNicknaming(true);
			currentTask = null;
			break;
		case Task.NICKNAME:
			currentDialogue = currentTask.message;
			drawDialogueScreen(true);
			setNickname(currentTask.p, false);
			if (currentTask.wipe) {
				currentTask.wipe = false;
				setNicknaming(true);
			}
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					currentTask.p.nickname = nickname.toString().trim();
					nickname = new StringBuilder();
					if (currentTask.p.nickname == null || currentTask.p.nickname.trim().isEmpty()) currentTask.p.nickname = currentTask.p.name();
					nicknaming = -1;
					currentTask = null;
				}
				drawToolTips("OK", null, null, null);
			}
			break;
		case Task.END:
			showMessage(currentTask.message);
			break;
		case Task.PARTY: // move reminder party
			drawPartyTask();
			break;
		case Task.REMIND:
			drawDialogueScreen(true);
			drawMoveReminder(currentTask.p);
			if (currentTask != null) currentDialogue = currentTask.message;
			break;
		case Task.HP:
			currentDialogue = currentTask.message;
			drawDialogueScreen(true);
			drawHiddenPowerScreen(gp.player.p.team);
			break;
		case Task.FOSSIL:
			currentDialogue = Item.breakString(currentTask.message, MAX_TEXTBOX);
			drawDialogueScreen(true);
			drawFossilScreen(gp.player.p.bag);
			break;
		case Task.CONFIRM:
			currentDialogue = Item.breakString(currentTask.message, MAX_TEXTBOX);
			drawDialogueScreen(true);
			drawConfirmWindow(currentTask.counter);
			break;
		case Task.TELEPORT:
			gp.gameState = GamePanel.TRANSITION_STATE;
			break;
		case Task.FLASH_IN:
			drawFlash(1);
			break;
		case Task.FLASH_OUT:
			drawFlash(-1);
			break;
		case Task.ITEM:
			drawItem();
			break;
		case Task.UPDATE:
			gp.aSetter.updateNPC(gp.currentMap);
			currentTask = null;
			break;
		case Task.TURN:
			Entity target = currentTask.e == null ? gp.npc[currentTask.start][currentTask.finish] : currentTask.e;
			String direction;
			switch(currentTask.counter) {
			case 0:
				direction = "down";
				break;
			case 1:
				direction = "up";
				break;
			case 2:
				direction = "left";
				break;
			case 3:
				direction = "right";
				break;
			default:
				System.out.println(currentTask.counter + " isn't a direction for Task.TURN");
				direction = "down";
				break;
			}
			if (currentTask.wipe) {
				target.setDirection(direction);
			} else {
				target.direction = direction;
			}
			currentTask = null;
			break;
		case Task.FLAG:
			gp.player.p.flag[currentTask.start][currentTask.finish] = true;
			currentTask = null;
			break;
		case Task.REGIONAL_TRADE:
			drawRegionalTrade();
			break;
		case Task.BATTLE:
			int trainer = currentTask.counter;
			int id = currentTask.start;
			currentTask = null;
			if (id > 0) {
				Task.addStartBattleTask(trainer, id, 'G');
			} else {
				Task.addStartBattleTask(trainer);
			}
			break;
		case Task.SPOT:
			drawSpot();
			break;
		case Task.START_BATTLE:
			if (currentTask.wipe) {
				startSim();
			} else {
				startBattle();
			}
			break;
		case Task.DIALOGUE: // for the npc speaking
			g2.setFont(getFont(currentTask.counter));
			if (currentTask.e.name == null) {
				currentTask.type = Task.TEXT;
				return;
			}
			showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
			drawNameLabel(above);
			break;
		case Task.SPEAK: // for the player speaking
			g2.setFont(getFont(currentTask.counter));
			showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
			above = false;
			drawNameLabel(above);
			break;
		case Task.ITEM_SUM:
			drawItemSum();
			break;
		case Task.SHAKE:
			if (!currentTask.message.isEmpty()) {
				showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
				if (currentTask.e != null) drawNameLabel(true);
			}
			drawShake();
			break;
		case Task.MOVE_CAMERA:
			drawCameraMove();
			break;
		case Task.MOVE_NPC:
			drawNPCMove();
			break;
		case Task.SLEEP:
			drawSleep();
			break;
		case Task.BLACKJACK:
			gp.keyH.resetKeys();
			if (!currentTask.wipe) Task.addTask(Task.TEXT, "Come play again soon, okay?");
			// Remove all existing components from the JFrame
		    Main.window.getContentPane().removeAll();

		    // Create and add the BlackjackPanel
		    BlackjackPanel bjPanel = new BlackjackPanel(gp, currentTask.wipe);
		    Main.window.getContentPane().add(bjPanel);

		    // Set focus on the BlackjackPanel
		    bjPanel.requestFocusInWindow();

		    // Repaint the JFrame to reflect the changes
		    Main.window.revalidate();
		    Main.window.repaint();
		    
		    currentTask = null;
		    break;
		case Task.MUSHROOM:
			drawMushroom(gp.player.p.bag);
			break;
		case Task.COIN:
			break;
		case Task.EVO_INFO:
			drawEvoInfoParty();
			break;
		case Task.ODDS:
			boolean print = false;
			if (currentTask.counter > 0) {
				currentTask.counter -= 10;
				showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
			} else {
				ArrayList<Pokemon> t1Team = new ArrayList<>();
				ArrayList<Item> t1Items = new ArrayList<>();
				ArrayList<Pokemon> t2Team = new ArrayList<>();
				ArrayList<Item> t2Items = new ArrayList<>();
				
				int teamSize = 3;
				
				for (int i = 0; i < teamSize; i++) {
					Pokemon p = Pokemon.generateCompetitivePokemon(t1Team);
					p.setSprites();
					t1Team.add(p);
					t1Items.add(p.item);
				}
				for (int i = 0; i < teamSize; i++) {
					Pokemon p = Pokemon.generateCompetitivePokemon(t2Team);
					p.setSprites();
					t2Team.add(p);
					t2Items.add(p.item);
				}
				
				Trainer ut = new Trainer("Trainer A", t1Team.toArray(new Pokemon[1]), t1Items.toArray(new Item[1]), 0, 1);
				Trainer ft = new Trainer("Trainer B", t2Team.toArray(new Pokemon[1]), t2Items.toArray(new Item[1]), 0, 2);
				
				int[] odds = Pokemon.simulateBattle(ut, ft);
				int[] adjustedOdds = adjustSimulatedOdds(odds);
				//tasks.clear();
				
				if (print) {
					System.out.println("Average Crits: " + String.format("%.1f", Pokemon.field.crits * 1.0 / 10));
					System.out.println("Average Misses: " + String.format("%.1f", Pokemon.field.misses * 1.0 / 10));
					System.out.println("Average Super Effective Hits: " + String.format("%.1f", Pokemon.field.superEffective * 1.0 / 10));
					System.out.println("Average Switches: " + String.format("%.1f", Pokemon.field.switches * 1.0 / 10));
					System.out.println("Average Knockouts: " + String.format("%.1f", Pokemon.field.knockouts * 1.0 / 10));
					System.out.println("Average Turns: " + String.format("%.1f", Pokemon.field.turns * 1.0 / 10));
					
					System.out.println("Trainer A games won: " + odds[0]);
					System.out.println("Trainer B games won: " + odds[1]);
				}
				
				ArrayList<Pair<Double, String>> parlay = new ArrayList<>(Arrays.asList(
					new Pair<>(Pokemon.field.crits * 1.0 / 10, "crits"),
					new Pair<>(Pokemon.field.misses * 1.0 / 10, "misses"),
					new Pair<>(Pokemon.field.superEffective * 1.0 / 10, "super effective hits"),
					new Pair<>(Pokemon.field.switches * 1.0 / 10, "switches"),
					new Pair<>(Pokemon.field.knockouts * 1.0 / 10, "knockouts"),
					new Pair<>(Pokemon.field.turns * 1.0 / 10, "total turns")
				));
				
				System.out.println("------------------");
				
				for (Pair<Double, String> p : parlay) {
					double avg = p.getFirst();
					double randomizedLine = randomizeParlayLine(avg, p.getSecond());
					p.setFirst(randomizedLine);
					if (print) System.out.println(String.format("%s line: %.1f (was %.1f)", 
			                   p.getSecond(), randomizedLine, avg));
				}
				gauntlet = currentTask.wipe;
				gp.simBattleUI.gauntlet = currentTask.wipe;
				
				int winStreak = gp.player.p.winStreak;
				gp.player.p.winStreak = 0;
				gp.player.p.addBetCurrency(gauntlet, -1);
				gp.saveGame(gp.player.p);
				gp.player.p.winStreak = winStreak;
				gp.player.p.addBetCurrency(gauntlet, 1);
				
				parlays = new int[MAX_PARLAYS];
				sheetFilled = false;
				gp.simBattleUI.parlaySheet = parlay;
				
				Task t = Task.addTask(Task.BET_BATTLE, "");
				t.p = ut.getCurrent();
				t.foe = ft.getCurrent();
				t.trainers = new Trainer[] {ut, ft};
				t.start = adjustedOdds[0];
				t.finish = adjustedOdds[1];
				
				gp.simBattleUI.weather = null;
				gp.simBattleUI.terrain = null;
				currentTask = null;
			}
			break;
		case Task.BET_BATTLE:
			showMessage = false;
			drawBetBattle();
			break;
		case Task.GAME_STATE:
			gp.gameState = currentTask.counter;
			currentTask = null;
			break;
		case Task.INTERACTIVE:
			gp.player.interactWith(currentTask.e, currentTask.counter, true);
			currentTask = null;
			break;
		case Task.PARTICLE:
			drawParticles();
			break;
		case Task.RESET:
			gp.puzzleM.reset(currentTask.wipe);
			currentTask = null;
			break;
		case Task.I_TILE:
			gp.aSetter.setInteractiveTile(currentTask.counter);
			currentTask = null;
			break;
		case Task.AWAKE:
			((NPC_Dragon) currentTask.e).awake();
			currentTask = null;
			break;
		case Task.NURSERY_DEPOSIT:
			drawNurseryDeposit();
			break;
		case Task.NURSERY_WITHDRAW:
			drawNurseryWithdraw();
			break;
		case Task.SPACE:
			drawSpace();
			break;
		}
	}

	private Font getFont(int type) {
		Font font;
		switch (type) {
		case 1:
			font = cryptik;
			break;
		case 2:
			font = consolas;
			break;
		default:
			font = marumonica;
			break;
		}
		return font;
	}
	
	public boolean messageSkippable() {
		return !(currentTask != null && currentTask.type == Task.SPACE);
	}

	private void drawParticles() {
		currentTask.counter--;
		
		float alpha = Math.max(0, currentTask.counter / (float)currentTask.start);

		for (TemplateParticle p : currentTask.particleList) {
			if (p.alive || !p.started) {
				p.draw(g2, alpha);
			}
		}
		
		if (currentTask.counter <= 0) {
			currentTask = null;
		}
	}

	private void drawBetBattle() {
		Trainer ut = currentTask.trainers[0];
		Trainer ft = currentTask.trainers[1];
		
		int[] odds = new int[] {currentTask.start, currentTask.finish};
		
		drawSheetWindow();
		drawCoinWindow();
		
		drawParlayBetWindow();
		
		drawPartySummary(ut, gp.tileSize / 2, 1);
		drawPartySummary(ft, (int) (gp.tileSize * 8.5), 2);
		
		if (starterConfirm) { // spinner for how much to bet
			int x = (int) (commandNum == 1 ? gp.tileSize / 2 : gp.tileSize * 8.5);
			drawBattleBets(x, ut, ft, odds);
			drawBattleBetHeader(x, commandNum == 1 ? "A" : "B");
			return;
		}
		
		if (showParlays) {
			drawParlaySheet(true);
			drawParlayPayout();
			return;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (commandNum > 0) {
				starterConfirm = true;
			}
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			if (commandNum == 1) {
				commandNum = 2;
			} else if (commandNum == 2) {
				commandNum = 1;
			}
		}
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			if (commandNum == 0) {
				commandNum = 1;
			} else if (commandNum == 1) {
				commandNum = 2;
			} else if (commandNum == 2) {
				commandNum = 1;
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			int prevBet = parlayBet;
			int activeBets = getActiveBets();
			parlayBet += Player.BET_INC;
			if (parlayBet > gp.player.p.getMaxParlayBet(gauntlet, activeBets)) parlayBet = 10;
			
			coinColor = Color.WHITE;
			
			int diff = (parlayBet - prevBet);
			gp.player.p.addBetCurrency(gauntlet, -(diff * activeBets));
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			int prevBet = parlayBet;
			int activeBets = getActiveBets();
			parlayBet -= Player.BET_INC;
			if (parlayBet < Player.BET_INC) parlayBet = gp.player.p.getMaxParlayBet(gauntlet, activeBets);
			
			coinColor = Color.WHITE;
			
			int diff = (prevBet - parlayBet);
			gp.player.p.addBetCurrency(gauntlet, diff * activeBets);
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			commandNum = 0;
		}
		
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			showParlays = true;
		}
		drawToolTips(commandNum > 0 ? "Bet" : null, "Parlay", commandNum > 0 ? "Back" : null, null);
	}

	private int getActiveBets() {
		int result = 0;
	    
	    // Count the number of lines the player has bet on
	    for (int i = 0; i < parlays.length; i++) {
	        if (parlays[i] != 0) result++;
	    }
		return result;
	}

	private void drawParlayBetWindow() {
		int x = (int) (gp.tileSize * 6.75);
		int y = 0;
		int width = (int) (gp.tileSize * 2.5);
		int height = (int) (gp.tileSize * 3.25);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize * 2;
		y += 4;
		g2.setFont(g2.getFont().deriveFont(30F));
		g2.drawString(parlayBet + "", x, y);
		
		int y2 = y += gp.tileSize / 4;
		int width2 = gp.tileSize / 2;
		int height2 = gp.tileSize / 2;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		
		y2 = y -= gp.tileSize * 1.5;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		
		x += gp.tileSize / 4;
		y -= gp.tileSize / 8;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString("Parlay Bet", getCenterAlignedTextX("Parlay Bet", x), y);
	}
	
	private void drawParlayPayout() {
		int activeBets = getActiveBets();
		if (activeBets < 2) return; // No payout if less than 2 bets are active

	    int x = gp.tileSize * 11;
	    int y = (int) (gp.tileSize * 2.5);
	    int width = gp.tileSize * 5;
	    int height = gp.tileSize * 7;

	    drawSubWindow(x, y, width, height);

	    x += gp.tileSize / 2;
	    y += gp.tileSize;

	    int lineSpacing = gp.tileSize;

	    g2.setFont(g2.getFont().deriveFont(24F));
	    g2.setColor(Color.WHITE);
	    g2.drawString("Net Payouts:", x, y);
	    y += lineSpacing;
	    
	    int amtPaid = parlayBet * activeBets;

	    for (int correct = 2; correct <= activeBets; correct++) {
	        int payout = SimBattleUI.calculateParlayPayout(parlays, null, null, parlayBet, correct);
	        int netPayout = payout - amtPaid;

	        // Display correct bets count and corresponding payout
	        String payoutText = correct + " correct: " + (netPayout >= 0 ? "+" : "") + netPayout + " " + getBetCurrencyName(gauntlet);
	        g2.drawString(payoutText, x, y);
	        y += lineSpacing;
	    }
		
	}

	private void drawSheetWindow() {
		int x = gp.tileSize / 2;
		int y = 2;
		int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 1.1);
		
		drawSubWindow(x, y, width, height);
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		
		g2.setFont(g2.getFont().deriveFont(28F));
		g2.setColor(sheetFilled ? Color.GREEN : Color.GRAY);
		
		String sheetText = "[A] Parlay Sheet";
		g2.drawString(sheetText, x, y);
	}

	private void drawCoinWindow() {
		int x = (int) (gp.tileSize * 10.5);
		int y = 2;
		int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 1.1);
		
		drawSubWindow(x, y, width, height);
		x += width - gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		
		g2.setFont(g2.getFont().deriveFont(28F));
		g2.setColor(coinColor);
		
		String coinText = gp.player.p.getBetCurrency(gauntlet) + " " + getBetCurrencyName(gauntlet);
		g2.drawString(coinText, getRightAlignedTextX(coinText, x), y);
	}

	private void drawPartySummary(Trainer t, int x, int commandNum) {		
		int y = (int) (gp.tileSize * 1.25);
		int width = gp.tileSize * 7;
		int height = (int) (gp.tileSize * 10.5);
		
		drawSubWindow(x, y, width, height);
		
		if (this.commandNum == commandNum) {
			g2.setColor(Color.RED);
			g2.drawRoundRect(x - 3, y - 3, width + 6, height + 6, 35, 35);
		}
		
		g2.setColor(Color.WHITE);
		
		int pHeight = (int) (gp.tileSize * 3.25);
		int startX = x + gp.tileSize / 2;
		int startY = y + gp.tileSize / 2;
		x = startX;
		y = startY;
		for (Pokemon p : t.getTeam()) {
			g2.drawImage(p.getSprite(), x, y, null);
			x += gp.tileSize * 2;
			g2.setFont(g2.getFont().deriveFont(20F));
			y += gp.tileSize / 2;
			g2.drawString(p.getName(), x, y);
			y += gp.tileSize / 2;
			g2.drawString("@ " + p.item, x, y);
			int startX2 = x;
			x += gp.tileSize * 3.5;
			y -= gp.tileSize / 2;
			g2.drawImage(p.item.getImage(), x, y, null);
			x = startX2;
			y += gp.tileSize;
			g2.drawString(p.ability.toString(), x, y);
			x = startX;
			y += gp.tileSize / 2;
			g2.setFont(g2.getFont().deriveFont(16F));
			for (Moveslot m : p.moveset) {
				String moveString = m == null ? "" : "- " + m.move.toString();
				g2.drawString(moveString, x, y);
				y += gp.tileSize / 3;
			}
			
			x = startX + gp.tileSize * 4;
			startY += pHeight;
			y = (int) (startY - gp.tileSize * 1.5);
			
			g2.drawImage(p.type1.getImage(), x, y, null);
			x += gp.tileSize * 0.75;
			if (p.type2 != null) {
				g2.drawImage(p.type2.getImage(), x, y, null);
			}
			
			y += gp.tileSize;
			
			String nature = p.getNature() + " Nature";
			g2.drawString(nature, getCenterAlignedTextX(nature, x), y);
			
			x = startX;
			y = startY;
		}
		
	}
	
	private void drawBattleBetHeader(int x, String trainer) {
		x = x + gp.tileSize;
		int y = (int) (gp.tileSize * 2.5);
		int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 1.25);
		
		drawSubWindow(x, y, width, height, 250);
		
		x += width / 2;
		y += gp.tileSize * 0.9;
		
		g2.setFont(g2.getFont().deriveFont(32F));
		String text = "Bet on Trainer " + trainer;
		g2.drawString(text, getCenterAlignedTextX(text, x), y);
	}
	
	private void drawBattleBets(int x, Trainer ut, Trainer ft, int[] odds) {
		x = (int) (x + gp.tileSize * 2);
		int y = (int) (gp.tileSize * 3.75);
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize * 0.25;
		y += gp.tileSize * 0.65;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int americanOdds = calculateOdds(odds, commandNum - 1);
		
		g2.drawString(SimBattleUI.formatOdds(americanOdds), x, y);
		
		x += gp.tileSize;
		y += gp.tileSize * 1.35;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		battleBet = battleBet > gp.player.p.getBetCurrency(gauntlet) ? gp.player.p.getBetCurrency(gauntlet) : battleBet;
		g2.drawString(battleBet + "", x, y);
		
		int y2 = y += gp.tileSize / 4;
		int width2 = gp.tileSize / 2;
		int height2 = gp.tileSize / 2;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		
		y2 = y -= gp.tileSize * 1.5;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		
		x -= gp.tileSize;
		y += gp.tileSize * 2.5;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int payout = SimBattleUI.calculatePayout(battleBet, commandNum - 1, odds);
		
		g2.drawString("Payout: +" + payout, x, y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			
			Task t = Task.addTask(Task.START_BATTLE, "");
			t.wipe = true;
			t.trainers = new Trainer[] {ut, ft};
			t.start = odds[0];
			t.finish = odds[1];
			gp.simBattleUI.choice = commandNum;
			commandNum = 0;
			areaCounter = 0;
			showParlays = false;
			starterConfirm = false;
			currentTask = null;
			
			gp.player.p.addBetCurrency(gauntlet, -battleBet);
			
			gp.simBattleUI.simOdds = odds;
			gp.simBattleUI.odds = new int[] { calculateOdds(odds, 0), calculateOdds(odds, 1) };
			gp.simBattleUI.battleBet = battleBet;
			gp.simBattleUI.parlayBet = parlayBet;
			gp.simBattleUI.parlays = parlays.clone();
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			battleBet += Player.BET_INC;
			if (battleBet > gp.player.p.getMaxBet(gauntlet)) battleBet = Player.BET_INC;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			battleBet -= Player.BET_INC;
			if (battleBet < Player.BET_INC) battleBet = gp.player.p.getMaxBet(gauntlet);
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			int max = gp.player.p.getMaxBet(gauntlet);
			battleBet -= max > Player.BET_INC * 10 ? Player.BET_INC * 10 : Player.BET_INC;
			if (battleBet < Player.BET_INC) battleBet += max;
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			int max = gp.player.p.getMaxBet(gauntlet);
			battleBet += max > Player.BET_INC * 10 ? Player.BET_INC * 10 : Player.BET_INC;
			if (battleBet > max) battleBet -= max;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			starterConfirm = false;
		}
		
		drawToolTips(null, null, "Close", null);
	}

	public static int calculateOdds(int[] odds, int choice) {
		int totalGames = odds[0] + odds[1];

	    // If a trainer won 0 games, return max underdog odds
	    if (odds[choice] == 0) return +10000;
	    // If a trainer won all games, return max favorite odds
	    if (odds[choice] == totalGames) return -10000;

	    double probability = (double) odds[choice] / totalGames;
	    int americanOdds;

	    if (probability > 0.5) {
	        americanOdds = (int) (- (probability / (1 - probability) * 100));
	    } else {
	        americanOdds = (int) ((1 - probability) / probability * 100);
	    }

	    // Cap the odds within reasonable bounds
	    if (americanOdds > 10000) americanOdds = 10000;
	    if (americanOdds < -10000) americanOdds = -10000;

	    return americanOdds;
	}
	
	public static int[] adjustSimulatedOdds(int[] originalOdds) {
		int totalGames = originalOdds[0] + originalOdds[1];
		Random rand = new Random();
	    
	    // Scale up to 100 games (10x)
	    double scaledTeam1Wins = originalOdds[0] * 10.0;
	    double scaledTeam2Wins = originalOdds[1] * 10.0;
	    double scaledTotal = totalGames * 10.0;
	    
	    // Apply randomness (0.85x to 1.15x multiplier)
	    double randomFactor = 0.85 + rand.nextDouble() * 0.3; // Range: 0.85-1.15
	    scaledTeam1Wins *= randomFactor;
	    scaledTeam2Wins *= (2.0 - randomFactor); // Ensure total stays ~100
	    
	    // Clamp to avoid extremes (min 1 win, max 99 wins per team)
	    scaledTeam1Wins = Math.max(1, Math.min(scaledTeam1Wins, scaledTotal - 1));
	    scaledTeam2Wins = Math.max(1, Math.min(scaledTeam2Wins, scaledTotal - 1));
	    
	    // Normalize to 100 games (fix rounding errors from randomness)
	    double normalizedTotal = scaledTeam1Wins + scaledTeam2Wins;
	    scaledTeam1Wins = (scaledTeam1Wins / normalizedTotal) * 100;
	    scaledTeam2Wins = (scaledTeam2Wins / normalizedTotal) * 100;
	    
	    // Apply probability compression (sigmoid-like curve)
	    double compressExponent = 0.7; // Tweakable (0.5 = aggressive, 0.8 = mild)
	    double team1Prob = scaledTeam1Wins / 100.0;
	    double team2Prob = scaledTeam2Wins / 100.0;
	    
	    // Compress probabilities toward 50%
	    if (team1Prob < 0.5) {
	        team1Prob = 0.5 * Math.pow(team1Prob / 0.5, compressExponent);
	    } else {
	        team1Prob = 1 - 0.5 * Math.pow((1 - team1Prob) / 0.5, compressExponent);
	    }
	    
	    if (team2Prob < 0.5) {
	        team2Prob = 0.5 * Math.pow(team2Prob / 0.5, compressExponent);
	    } else {
	        team2Prob = 1 - 0.5 * Math.pow((1 - team2Prob) / 0.5, compressExponent);
	    }
	    
	    // Normalize to ensure probabilities sum to 1
	    double sum = team1Prob + team2Prob;
	    team1Prob /= sum;
	    team2Prob /= sum;
	    
	    // Convert back to win counts (out of 100)
	    int adjustedTeam1Wins = (int) Math.round(team1Prob * 100);
	    int adjustedTeam2Wins = (int) Math.round(team2Prob * 100);
	    
	    // Ensure we never return 0-100 or 100-0
	    adjustedTeam1Wins = Math.max(1, Math.min(99, adjustedTeam1Wins));
	    adjustedTeam2Wins = Math.max(1, Math.min(99, adjustedTeam2Wins));
	    
	    return new int[]{adjustedTeam1Wins, adjustedTeam2Wins};
	}
	
	public static double randomizeParlayLine(double originalAvg, String statName) {
	    Random rand = new Random();
	    
	    // 1. Scale randomness - bigger averages get more variance
	    double variance = 0.5 + (originalAvg * 0.2);
	    
	    // 2. Apply adjusted randomness (bell curve distribution)
	    double randomized = originalAvg + (rand.nextGaussian() * variance);
	    
	    // 3. Convert to nearest 0.5 increment
	    double result = Math.floor(randomized) + 0.5; // Always rounds down to X.5
	    
	    // 4. Special case: If original was exactly X.0 and we want ±0.5 variation
	    if (originalAvg % 1 == 0) {
	        result = originalAvg + (rand.nextBoolean() ? 0.5 : -0.5);
	    }
	    
	    // 5. Enforce stat-specific minimums (all as .5 values)
	    switch (statName) {
	        case "turns":
	            result = Math.max(3.5, result);
	            break;
	        case "knockouts":
	            result = Math.max(1.5, result);
	            break;
	        default:
	            result = Math.max(0.5, result); // Global minimum
	    }
	    
	    return result;
	}

	private void drawSleep() {
		if (currentTask.counter <= 0) {
			currentTask = null;
		} else {
			currentTask.counter--;
		}
		
	}

	private void drawNPCMove() {
		boolean moveX = currentTask.start % 2 == 0;
		Entity target = currentTask.e;
		int pos = moveX ? target.worldX : target.worldY;
		
		int direction = Integer.signum(currentTask.finish - pos);
		boolean finished = (direction > 0 && pos >= currentTask.finish) ||
						   (direction < 0 && pos <= currentTask.finish) ||
						   (direction == 0);
		
		if (finished) {
			target.spriteNum = 1;
			gp.setRenderableNPCs();
			currentTask = null;
		} else {
			if (moveX) {
				target.worldX += direction * currentTask.counter;
				if (currentTask.wipe) gp.offsetX -= direction * currentTask.counter;
			} else {
				target.worldY += direction * currentTask.counter;
				if (currentTask.wipe) gp.offsetY -= direction * currentTask.counter;
			}
			if (target.walkable) {
				target.spriteCounter++;
				if (target.spriteCounter > (target instanceof PlayerCharacter ? 16 / currentTask.counter : 5)) {
					target.spriteNum++;
					if (target.spriteNum > 4) {
						target.spriteNum = 1;
					}
					target.spriteCounter = 0;
				}
			}
		}
		
	}

	private void drawCameraMove() {
		if (currentTask.wipe) { // diagonal
			//System.out.printf("Frame %d: X=%d, Y=%d\n", counter, gp.offsetX, gp.offsetY);
			int totalFrames = currentTask.counter;
			if (!assignedStart) {
				startX = gp.offsetX;
				startY = gp.offsetY;
				assignedStart = true;
			}
			int distanceX = currentTask.start - startX;
			int distanceY = currentTask.finish - startY;

			double rateX = (double) distanceX / totalFrames;
			double rateY = (double) distanceY / totalFrames;
			
			errorX += rateX;
			errorY += rateY;
			
			int moveX = (int) errorX;
			int moveY = (int) errorY;
			
			gp.offsetX += moveX;
			gp.offsetY += moveY;
			
			errorX -= moveX;
			errorY -= moveY;

			counter++;

			if (counter >= totalFrames) {
			    gp.offsetX = currentTask.start;
			    gp.offsetY = currentTask.finish;
			    counter = 0;
			    errorX = 0;
			    errorY = 0;
			    assignedStart = false;
			    currentTask = null;
			}

		} else { // cardinal
			boolean moveX = currentTask.start % 2 == 0;
			int offset = moveX ? gp.offsetX : gp.offsetY;
			
			int direction = Integer.signum(currentTask.finish - offset);
			
			boolean finished = (direction > 0 && offset >= currentTask.finish) ||
							   (direction < 0 && offset <= currentTask.finish) ||
							   (direction == 0);
			
			if (finished) {
				currentTask = null;
			} else {
				if (moveX) {
					gp.offsetX += direction * currentTask.counter;
				} else {
					gp.offsetY += direction * currentTask.counter;
				}
			}
		}
	}

	private void drawShake() {
		if (currentTask.start == -1) {
			currentTask.start = gp.offsetX;
			currentTask.finish = gp.offsetY;
		}
		counter += 1;
		int maxShake = (int) ((currentTask.counter - counter) / 4.0);
	    maxShake = Math.max(0, Math.min(maxShake, currentTask.counter/2));

	    gp.offsetX = rand.nextInt(2 * maxShake + 1) - maxShake + currentTask.start;
	    gp.offsetY = rand.nextInt(2 * maxShake + 1) - maxShake + currentTask.finish;
		if (counter >= currentTask.counter - 1) {
			counter = 0;
			gp.offsetX = currentTask.start;
			gp.offsetY = currentTask.finish;
			showMessage = false;
			currentTask = null;
		}
	}

	private void startBattle() {
		gp.player.p.setSlots();
		gp.keyH.resetKeys(false);
		
		transitionBuffer = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
		gp.gameState = GamePanel.START_BATTLE_STATE;
		
		// Make sure the front Pokemon isn't fainted
		int index = 0;
		Pokemon user = gp.player.p.getCurrent();
		while (user.isFainted() || (gp.player.p.nuzlocke && user.isOverLevelCap(gp.player.p.badges))) {
			gp.player.p.swapToFront(gp.player.p.team[++index], index);
			user = gp.player.p.getCurrent();
		}
		
		gp.player.p.clearBattled();
		gp.player.p.amulet = user.item == Item.AMULET_COIN;
		user.battled = true;
		gp.battleUI.user = user;
		gp.battleUI.foe = currentTask.p;
		gp.battleUI.index = currentTask.counter;
		gp.battleUI.staticID = currentTask.start;
		gp.battleUI.partyNum = 0;
		gp.battleUI.encType = currentTask.message.charAt(0);
		
		if (currentTask.p.trainer != null) {
			currentTask.p.trainer.setSprites();
		}
		
		currentTask = null;
	}
	
	private void startSim() {
		gp.keyH.resetKeys(false);
		
		transitionBuffer = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
		gp.gameState = GamePanel.SIM_START_BATTLE_STATE;
		
		int choice = gp.simBattleUI.choice - 1;
		
		gp.simBattleUI.user = currentTask.trainers[choice].getCurrent();
		gp.simBattleUI.foe = currentTask.trainers[1 - choice].getCurrent();
		
		gp.simBattleUI.user.setSprites();
		gp.simBattleUI.foe.setSprites();
		
//		System.out.println("Odds of " + currentTask.trainers[0].getName() + " winning: " + currentTask.start);
//		System.out.println("Odds of " + currentTask.trainers[1].getName() + " winning: " + currentTask.finish);
		
		currentTask = null;
	}
	
	private void drawNurseryDeposit() {
		drawParty(null);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			
			if (gp.player.p.team[partyNum] instanceof Egg) {
				showMessage("We can't take Eggs here! Only hatched Pokemon!");
                return;
			}
			if (partyNum == 0) {
            	if (gp.player.p.team[partyNum + 1] == null) {
            		showMessage("That's your last Pokemon!");
                    return;
            	}
            }
            if (gp.player.p.teamWouldBeFainted(partyNum)) {
            	showMessage("That's your last Pokemon!");
                return;
            }
			
			Pokemon p = gp.player.p.team[partyNum];
			gp.player.p.team[partyNum] = null;
            gp.player.p.shiftTeamForward(partyNum);
            
            if (partyNum == 0) gp.player.p.setCurrent();
            
			gp.player.p.nursery.deposit(p);
			Task.addTask(Task.TEXT, "Deposited " + p.getNickname() + "!");
			if (!gp.player.p.nursery.isFull()) {
				Task.addTask(Task.DIALOGUE, npc, "We can hold up to 2 Pokemon. Would you like to deposit a Pokemon?");
				Task.addTask(Task.NURSERY_DEPOSIT, "");
			}
			currentTask = null;
			partyNum = 0;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
			gp.gameState = GamePanel.PLAY_STATE;
			partyNum = 0;
		}
	}
	
	private void drawNurseryWithdraw() {
		currentDialogue = currentTask.message;
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 3;
		int y = gp.tileSize * 5;
		int width = gp.tileSize * 10;
		int height = gp.tileSize * 6;
		drawSubWindow(x, y, width, height);
		
		Pokemon[] pokemon = gp.player.p.nursery.getPokemon();
		for (int i = 0; i < pokemon.length; i++) {
			Pokemon p = pokemon[i];
			if (p != null) {
				int pX = x + gp.tileSize / 2;
				int pY = y + gp.tileSize / 4;
				int pWidth = gp.tileSize * 9;
				int pHeight = (int) (gp.tileSize * 2.5);
				drawSubWindow(pX, pY, pWidth, pHeight);
				
				if (p.getSprite() == null) {
					p.setSprites();
				}
				
				if (p.item != null) {
					g2.drawImage(p.item.getImage2(), (int) (pX + pWidth - gp.tileSize * 1.5), (int) (pY + gp.tileSize * 0.75), null);
				}
				
				g2.setFont(g2.getFont().deriveFont(32F));
				pX += gp.tileSize / 2;
				pY += gp.tileSize / 2;
				
				if (commandNum == i) g2.drawString(">", pX, pY + gp.tileSize);
				
				pX += gp.tileSize / 2;
				g2.drawImage(p.getSprite(), pX, pY, null);
				pX += gp.tileSize * 2;
				pY += gp.tileSize;
				String label = p.getNickname() + " Lv. " + p.getLevel();
				g2.setFont(g2.getFont().deriveFont(getFontSize(label, pWidth - gp.tileSize * 4)));
				g2.drawString(label, pX, pY);
				
				y += pHeight + gp.tileSize / 2;
			}
		}
		
		if (gp.keyH.upPressed || gp.keyH.downPressed) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			commandNum = 1 - commandNum;
			if (pokemon[commandNum] == null) {
				commandNum = 0;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			Pokemon withdraw = pokemon[commandNum];
			gp.player.p.addPokemon(gp.player.p.nursery.withdraw(withdraw));
			Task.addTask(Task.TEXT, "Withdrew " + withdraw.getNickname() + "!");
			if (!gp.player.p.nursery.isEmpty()) {
				Task.addTask(Task.NURSERY_WITHDRAW, npc, "Would you like to withdraw a Pokemon?");
			}
			currentTask = null;
			commandNum = 0;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
			gp.gameState = GamePanel.PLAY_STATE;
			commandNum = 0;
		}
	}
	
	private void drawSpot() {
		Entity t = currentTask.e;
		
		int screenX = t.worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = t.worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;
		
		g2.drawImage(interactIcon, screenX - 3, screenY - gp.tileSize - 16, null);
		
		counter++;
		
		if (counter > 30) {
			counter = 0;
			currentTask = null;
		}
		
	}
	
	private void drawNameLabel(boolean above) {
		int x;
		int y;
		int width;
		int height;
		if (above) {
			x = gp.tileSize*2;
			y = (int) (gp.tileSize * 4.5);
		} else {
			x = gp.tileSize*10;
			y = gp.screenHeight - (gp.tileSize*6);
		}
		width = gp.tileSize*4;
		height = (int) (gp.tileSize * 1.5);
		
		String name = currentTask.e.getName();
		
		drawSubWindow(x, y, width, height);
		y += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString(name, getCenterAlignedTextX(name, x + width / 2), y);
	}

	private void drawLightDistortion(float alpha) {
		long currentTime = System.currentTimeMillis();
		int totalFrames = lightFrames.size();
		
		if (currentTime - lightLastFrameTime > 75) {
			lightFrameIndex = (lightFrameIndex + 1) % totalFrames;
			lightLastFrameTime = currentTime;
		}
		
		
		BufferedImage current = lightFrames.get(lightFrameIndex);
		BufferedImage next = lightFrames.get((lightFrameIndex + 1) % totalFrames);
		
		drawImageWithAlpha(current, alpha);
		
		drawImageWithAlpha(next, alpha);
	}

	private void drawImageWithAlpha(BufferedImage image, float alpha) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.drawImage(image, 0, 0, gp.screenWidth, gp.screenHeight, null);
	}

	private void drawRegionalTrade() {
		int[] acceptedIndices = new int[] {254, 255, 256,  261, 262,  272, 273,  276, 277};
		int[] tradeIndices = new int[] {251, 252, 253,  259, 260,  270, 271,  274, 275};
		
		drawParty(null);
		
		int x = gp.tileSize*3;
		int y = 0;
		int width = gp.tileSize*10;
		int height = gp.tileSize;
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		String text = "Select a Xhenovain form:";
		g2.drawString(text, x, y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			Pokemon p = gp.player.p.team[partyNum];
			int trade = -1;
			for (int i = 0; i < acceptedIndices.length; i++) {
				if (acceptedIndices[i] == p.id) {
					trade = i;
					break;
				}
			}
			if (trade >= 0) {
				Pokemon tr = new Pokemon(tradeIndices[trade], p.level, true, false);
				Task t = Task.addTask(Task.CONFIRM, "Would you like to trade " + p.nickname + " for my " + tr.name() + "?", tr);
				t.counter = 1;
				currentTask = null;
			} else {
				Task.addTask(Task.TEXT, "That's not a Xhenovian form! Do you have any to show me?");
				Task.addTask(Task.REGIONAL_TRADE, "");
				currentTask = null;
			}
			currentTask = null;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
		}
		
		drawToolTips("OK", null, "Back", null);
	}

	private void drawItem() {
		BufferedImage image = null;
		if (!currentTask.wipe) { // showing item
			if (currentTask.message.isEmpty()) {
				String count = currentTask.counter > 1 ? currentTask.counter + " " : "a ";
				String plural = currentTask.counter > 1 ? "s" : "";
				currentDialogue = currentTask.item.isTM() ? "Obtained " + currentTask.item.toString() + "!" : "You got " + count + currentTask.item.toString() + plural + "!";
			} else {
				currentDialogue = currentTask.message;
			}
			image = currentTask.item.getImage2();
			if (!gp.player.p.bag.contains(currentTask.item)) {
				Task t = Task.createTask(Task.ITEM, "");
				t.item = currentTask.item;
				t.wipe = true;
				t.counter = currentTask.counter;
				Task.insertTask(t, 0);
			}
			if (!addItem) {
				gp.player.p.bag.add(currentTask.item, currentTask.counter);
				addItem = true;
			}
		} else { // showing what pocket i put it in
			String descriptor = !currentTask.item.isTM() ? "the " : "";
			String itemName = currentTask.item.toString();
			if (currentTask.counter > 1 && itemName.contains("Berry")) itemName = itemName.replace("Berry", "Berries");
			String message = "Put " + descriptor + itemName + " in the " + Item.getPocketName(currentTask.item.getPocket()) + " pocket!";
			currentDialogue = Item.breakString(message, MAX_TEXTBOX);
			image = bagIcons[currentTask.item.getPocket() - 1];
		}
		drawDialogueScreen(true);
		int x = gp.tileSize * 12;
		int y = (int) (gp.tileSize * 2.5);
		int width = (int) (gp.tileSize * 1.5);
		int height = (int) (gp.tileSize * 1.5);
		drawSubWindow(x, y, width, height);
		x += gp.tileSize / 4;
		y += gp.tileSize / 4;
		g2.drawImage(image, x, y, null);
		
		if (gp.keyH.wPressed || gp.keyH.sPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			addItem = false;
			currentTask = null;
		}
	}

	private void drawFlash(int i) {
		if (flashColor == null) {
			flashColor = currentTask.color == null ? Color.WHITE : currentTask.color;
		}
		if (i == 0) {
			g2.setColor(flashColor);
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		} else {
			counter += i;
			int alpha = flashColor.getAlpha();
			alpha /= 25;
			g2.setColor(new Color(flashColor.getRed(),flashColor.getGreen(),flashColor.getBlue(),Math.abs(counter * alpha)));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
			if (i > 0) {
				if (counter >= 25) {
					counter = 0;
					currentTask = null;
					drawFlash = true;
				}
			} else if (i < 0) {
				if (counter <= 0) {
					counter = 0;
					currentTask = null;
					flashColor = null;
					drawFlash = false;
				}
			}
		}
		
	}

	private void drawConfirmWindow(int type) {
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		if (currentTask.e != null && currentTask.e.name != null) drawNameLabel(true);
		x += gp.tileSize;
		y += gp.tileSize;
		String yes = type == 7 ? "Faith" : "Yes";
		g2.drawString(yes, x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				switch (type) {
				case 0: // gauntlet at top of splinkty
					Task.addTask(Task.DIALOGUE, gp.npc[146][1], "Don't screw this up, Finn.");
					currentTask = null;
					Task t = Task.addTask(Task.TELEPORT, "");
					t.counter = 149;
					t.start = 49;
					t.finish = 76;
					break;
				case 1: // regional trade confirm
					Task.addTask(Task.TEXT, "You traded for " + currentTask.p.name() + "!");
					gp.player.p.team[partyNum] = currentTask.p;
					gp.player.p.pokedex[currentTask.p.id] = 2;
					currentTask.p.trainer = gp.player.p;
					currentTask.p.metAt = "???";
					currentTask = null;
					partyNum = 0;
					break;
				case 2: // kleine village gauntlet
					currentTask = null;
					gp.player.p.flag[2][4] = true;
					gp.eHandler.teleport(28, 82, 36, false);
					break;
				case 3: // casino blackjack table
					Task.addTask(Task.BLACKJACK, "");
					currentTask = null;
					break;
				case 4: // casino battle betting
					above = false;
					if (Pokemon.sets.isEmpty()) {
						showMessage("Loading sets...");
						SwingUtilities.invokeLater(() -> {
							Pokemon.loadCompetitiveSets();
						});
					}
					Task.addTask(Task.ODDS, "Calculating odds...");
					currentTask = null;
					break;
				case 5: // guy eddie
					t = Task.addTask(Task.TELEPORT, "");
					t.counter = 124;
					t.start = 36;
					t.finish = 79;
					t = Task.addTask(Task.TURN, gp.player, "");
					t.counter = 0;
					if (!gp.player.p.flag[0][19]) {
						t = Task.addTask(Task.FLAG, "");
						t.start = 0;
						t.finish = 19;
						Task.addTask(Task.DIALOGUE, npc, "Make it in one piece okay squirt? Haha - yeah, that's portal technology!");
						if (!gp.player.p.flag[1][2]) {
							Task.addTask(Task.DIALOGUE, npc, "I'm sure you'll get your hands on it one day. It's great!");
						} else {
							Task.addTask(Task.DIALOGUE, npc, "You have it too? Wow kiddo, you must be special!");
							Task.addTask(Task.DIALOGUE, npc, "... ah, Professor's kid. Makes sense!");
						}
						Task.addTask(Task.DIALOGUE, npc, "Anyhow mate, this town is home to Merlin, the Magic Gym Leader.");
						Task.addTask(Task.DIALOGUE, npc, "He's far too strong to challenge right now, so most stuff is going to be blocked off for you.");
						Task.addTask(Task.DIALOGUE, npc, "My famous restaurant is right behind you! I'll be in there when you want to go home!");
						Task.addTask(Task.FLASH_IN, "");
						Task.addTask(Task.UPDATE, "");
						Task.addTask(Task.FLASH_OUT, "");
					}
					currentTask = null;
					break;
				case 6: // shell bell trade
					if (gp.player.p.bag.count[Item.EUPHORIAN_GEM.getID()] >= 2) {
						Task.addTask(Task.DIALOGUE, npc, "Awesome, thank you man! Here you go!");
						Task ta = Task.addTask(Task.ITEM, "");
						ta.item = Item.SHELL_BELL;
						gp.player.p.bag.remove(Item.EUPHORIAN_GEM, 2);
					} else {
						Task.addTask(Task.DIALOGUE, npc, "Hey! You don't have enough Euphorian Gems!");
						Task.addTask(Task.DIALOGUE, npc, "Sorry man, not this time. Let me know if we have a deal!");
					}
					currentTask = null;
					break;
				case 7: // faith
					gp.player.p.flag[7][9] = true;
					gp.player.p.flag[7][10] = false;
					gp.script.runScript(gp.npc[107][13]);
					currentTask = null;
					break;
				case 8: // picking color painting
					Painting painting = (Painting) currentTask.e;
					Puzzle p = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
					p.update(painting);
					currentTask = null;
					break;
				case 9: // checking main painting
					p = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
					p.sendToNext();
					currentTask = null;
					break;
				case 10: // reset gauntlet
					if (gp.currentMap == 194 || gp.currentMap == 195) {
						gp.player.p.bag.remove(Item.TEMPLE_ORB, Math.min(250, gp.player.p.bag.getCount(Item.TEMPLE_ORB)));
					}
					int[] loc = currentTask.wipe ? gp.puzzleM.FAITH_START : gp.puzzleM.LOGIC_START;
					gp.puzzleM.sendToStart(loc);
					gp.puzzleM.doReset(currentTask.wipe);
					Task.addTask(Task.TURN, gp.player, "", Task.UP);
					currentTask = null;
					break;
				case 11: // abandoned tower entrance
					if (!gp.player.p.flag[7][12]) { // player hasn't done the cutscene with Merlin yet
						gp.player.p.flag[7][12] = true;
						gp.npc[191][1].worldY = 83 * gp.tileSize;
						gp.npc[191][1].direction = "up";
						Task.addTask(Task.TURN, gp.player, "", Task.DOWN);
						Task.addNPCMoveTask('y', 77 * gp.tileSize, gp.npc[191][1], false, 4);
						Task.addTask(Task.DIALOGUE, gp.npc[191][1], "This is where your path truly begins, Finn.");
						Task.addTask(Task.SLEEP, "", 10);
						Task.addTask(Task.TURN, gp.player, "", Task.RIGHT);
						Task.addTask(Task.SLEEP, "", 5);
						Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.player, false, 2);
						Task.addTask(Task.TURN, gp.player, "", Task.DOWN);
						Task.addNPCMoveTask('y', (int) (73.75 * gp.tileSize), gp.npc[191][1], false, 2);
						Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
						Task.addTask(Task.SLEEP, "", 30);
						Task.addTask(Task.TURN, gp.npc[191][1], "", Task.RIGHT);
						Task.addTask(Task.DIALOGUE, gp.npc[191][1], "You and I, we're in this together.");
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.DIALOGUE, gp.npc[191][1], "But remember: Faith is not blind. It is a choice - made again and again, even in the darkness.");
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.TURN, gp.npc[191][1], "", Task.UP);
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.DIALOGUE, gp.npc[191][1], "Let's do this.");
					}
					t = Task.addTask(Task.TELEPORT, "");
					t.counter = 191;
					t.start = 49;
					t.finish = 47;
					gp.puzzleM.doReset(true);
					currentTask = null;
					break;
				case 12: // deep chasm entrance
					if (!gp.player.p.flag[7][12]) { // player hasn't done the cutscene with Merlin yet
						gp.player.p.flag[7][12] = true;
						gp.npc[197][1].worldY = 82 * gp.tileSize;
						gp.npc[197][1].direction = "up";
						Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
						Task.addNPCMoveTask('y', 77 * gp.tileSize, gp.npc[197][1], false, 4);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
						Task.addNPCMoveTask('x', 47 * gp.tileSize, gp.npc[197][1], false, 4);
						Task.addTask(Task.DIALOGUE, gp.npc[197][1], "This is where your path truly begins, Finn.");
						Task.addTask(Task.SLEEP, "", 10);
						Task.addTask(Task.TURN, gp.player, "", Task.RIGHT);
						Task.addTask(Task.SLEEP, "", 5);
						Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.player, false, 2);
						Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
						Task.addNPCMoveTask('x', 48 * gp.tileSize, gp.npc[197][1], false, 4);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
						Task.addNPCMoveTask('y', (int) (75.75 * gp.tileSize), gp.npc[197][1], false, 4);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
						Task.addNPCMoveTask('x', 50 * gp.tileSize, gp.npc[197][1], false, 2);
						Task.addTask(Task.SLEEP, "", 10);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
						Task.addTask(Task.SLEEP, "", 30);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
						Task.addTask(Task.DIALOGUE, gp.npc[197][1], "You and I, we're in this together.");
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.DIALOGUE, gp.npc[197][1], "The mind can be a weapon... or a prison, Finn. Trust in your wit - but trust in yourself more.");
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
						Task.addTask(Task.SLEEP, "", 15);
						Task.addTask(Task.DIALOGUE, gp.npc[197][1], "Let's do this.");
					}
					t = Task.addTask(Task.TELEPORT, "");
					t.counter = 197;
					t.start = 51;
					t.finish = 63;
					gp.puzzleM.doReset(false);
					currentTask = null;
					break;
				}
			}
		}
		y += gp.tileSize;
		String no = type == 7 ? "Logic" : "No";
		g2.drawString(no, x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (type != 7) {
					currentTask = null;
					gp.gameState = GamePanel.PLAY_STATE;
				}
				switch (type) {
				case 0:
					showMessage("Hurry up Finn, This is urgent!");
					tasks.clear();
					break;
				case 2:
					showMessage("Come back when you're ready!");
					tasks.clear();
					break;
				case 1:
					showMessage("That's okay! Take your time, I get it.");
					break;
				case 7: // logic
					gp.player.p.flag[7][10] = true;
					gp.player.p.flag[7][9] = false;
					gp.script.runScript(gp.npc[107][13]);
					currentTask = null;
					break;
				}
				commandNum = 0;
			}
		}
		
		if (gp.keyH.sPressed && type != 7) {
			gp.keyH.sPressed = false;
			currentTask = null;
			tasks.clear();
		}
		
		if (gp.keyH.upPressed || gp.keyH.downPressed) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			commandNum = 1 - Math.abs(commandNum);
		}
		
		drawToolTips("OK", null, "Back", null);
	}

	private void drawFossilScreen(Bag bag) {
		int x = gp.tileSize * 2;
		int y = (int) (gp.tileSize * 4.5);
		int width = gp.tileSize * 12;
		int height = gp.tileSize * 5;
		
		drawSubWindow(x, y, width, height);
		
		boolean hasTSF = bag.contains(Item.THUNDER_SCALES_FOSSIL);
		boolean hasDSF = bag.contains(Item.DUSK_SCALES_FOSSIL);
		
		x += gp.tileSize;
		y += gp.tileSize * 1.5;
		
		int buttonWidth = gp.tileSize * 5;
		int buttonHeight = gp.tileSize * 2;
		
		Item item = Item.THUNDER_SCALES_FOSSIL;
		
		Color color = hasTSF ? item.getColor() : Color.gray;
		g2.setColor(color);
		g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 10, 10);
		
		if (commandNum == 0) {
			g2.setColor(Color.RED);
			g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 10, 10);
		}
		
		int startY = y;
		
		y += gp.tileSize * 0.75;
		
		g2.setColor(Color.WHITE);
		String text = item.toString();
		g2.drawString(text, getCenterAlignedTextX(text, x + buttonWidth / 2), y);
		
		y += gp.tileSize * 0.75;
		String amt = "x" + bag.count[item.getID()];
		g2.drawString(amt, getCenterAlignedTextX(amt, x + buttonWidth / 2), y);
		
		x += buttonWidth;
		x += gp.tileSize / 2;
		
		item = Item.DUSK_SCALES_FOSSIL;
		y = startY;
		
		color = hasDSF ? item.getColor() : Color.gray;
		g2.setColor(color);
		g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 10, 10);
		
		if (commandNum == 1) {
			g2.setColor(Color.RED);
			g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 10, 10);
		}
		
		y += gp.tileSize * 0.75;
		
		g2.setColor(Color.WHITE);
		text = item.toString();
		g2.drawString(text, getCenterAlignedTextX(text, x + buttonWidth / 2), y);
		
		y += gp.tileSize * 0.75;
		amt = "x" + bag.count[item.getID()];
		g2.drawString(amt, getCenterAlignedTextX(amt, x + buttonWidth / 2), y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (commandNum == 0) {
				resurrectFossil(npc, bag, 211, hasTSF, Item.THUNDER_SCALES_FOSSIL);
			} else if (commandNum == 1) {
				resurrectFossil(npc, bag, 213, hasDSF, Item.DUSK_SCALES_FOSSIL);
			}
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			commandNum = 0;
			currentTask = null;
		}
		
		if (gp.keyH.leftPressed || gp.keyH.rightPressed) {
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			commandNum = 1 - commandNum;
		}
	}
	
	private void resurrectFossil(Entity npc, Bag bag, int fossilID, boolean hasFossil, Item fossil) {
		if (hasFossil) {
			Pokemon p = null;
			if (gp.player.p.nuzlocke) {
				Task.addTask(Task.DIALOGUE, npc, "Carefully extracting DNA... transplanting into bio-incubation capsule...");
				Task.addTask(Task.DIALOGUE, npc, "Success! Now it's an egg, and it'll hatch under your care.");
				p = new Egg(fossilID);
			} else {
				String dialogue = fossil == Item.THUNDER_SCALES_FOSSIL ? "Spark levels stable..." : "Darkness levels normalized...";
				Task.addTask(Task.DIALOGUE, npc, "Activating genetic reconstruction... " + dialogue);
				Task.addTask(Task.DIALOGUE, npc, "Resurrection complete! The ancient Pokemon awakens once more!");
				p = new Pokemon(fossilID, 20, true, false);
			}
			
			Task.addTask(Task.GIFT, "", p);
			bag.remove(fossil);
			commandNum = 0;
		} else {
			Task.addTask(Task.DIALOGUE, npc, "You don't have any " + fossil.toString() + "s for me to work with!");
			Task.addTask(Task.FOSSIL, npc, currentTask.message);
		}
		currentTask = null;
	}

	private void drawHiddenPowerScreen(Pokemon[] team) {
		int x = gp.tileSize * 4;
		int y = (int) (gp.tileSize * 4.5);
		int width = gp.tileSize * 8;
		int height = gp.tileSize * 5;
		
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75 + 4;
		
		int textHeight = (int) (gp.tileSize * 0.75);
		
		for (Pokemon p : team) {
			if (p != null) {
				String message = "";
    			message += p.nickname;
    			//if (!p.nickname.equals(p.name())) message += (" (" + p.name() + ")");
    			message += " : ";
    			PType type = p.determineHPType();
    			message += type;
    			
    			g2.drawString(message, x, y);
    			g2.drawImage(type.getImage(), (int) (x + gp.tileSize * 6.5), y - gp.tileSize / 2 + 4, null);
    			
    			y += textHeight;
			}
		}		
		
		if (gp.keyH.wPressed || gp.keyH.sPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			currentTask = null;
		}
		
		drawToolTips("OK", null, "OK", null);
		
	}

	private void drawMoveReminder(Pokemon p) {
		int x = gp.tileSize / 2;
		int y = gp.tileSize * 2;
		int width = (int) (gp.tileSize * 6.5);
		int height = gp.tileSize * 9;
		
		int sumX = x + width - gp.tileSize;
		int sumY = (int) (gp.tileSize * 3.5);

		ArrayList<Move> forgottenMoves = new ArrayList<>();
		Node[] movebank = p.getMovebank();
        for (int i = 0; i <= p.getLevel(); i++) {
        	if (i < movebank.length) {
        		Node move = movebank[i];
        		while (move != null) {
        			if (!p.knowsMove(move.data) && !forgottenMoves.contains(move.data)) {
        				forgottenMoves.add(move.data);
        			}
        			move = move.next;
        		}
        	}
        }
        if (forgottenMoves.isEmpty()) {
            Task.addTask(Task.TEXT, "This Pokemon has not forgotten any moves.");
            currentTask = null;
            partyNum = 0;
            return;
        }
        
        drawSubWindow(x, y, width, height);
        
        x += gp.tileSize;
        y += gp.tileSize / 2;
        int moveWidth = gp.tileSize * 4;
        int moveHeight = (int) (gp.tileSize * 0.75);
        
        for (int i = remindNum; i < remindNum + 9; i++) {
        	g2.setColor(Color.WHITE);
			if (i == remindNum) {
				g2.drawString(">", (x - gp.tileSize / 2) - 2, y + gp.tileSize / 2);
			}
			
			if (i < forgottenMoves.size()) {
				Move move = forgottenMoves.get(i);
				g2.setColor(move.mtype.getColor());
				g2.fillRoundRect(x, y, moveWidth, moveHeight, 10, 10);
				y += gp.tileSize * 0.6;
				String moveString = move.toString();
				g2.setColor(Color.BLACK);
				g2.setFont(g2.getFont().deriveFont(getFontSize(moveString, moveWidth + 16)));
				g2.drawString(moveString, getCenterAlignedTextX(moveString, x + (moveWidth / 2)), y);
				y += gp.tileSize / 3;
			}
        }
        
        Move m = forgottenMoves.get(remindNum);
		
		drawMoveSummary(sumX, sumY, p, null, null, m);
		
		// Down Arrow
		if (remindNum + 9 < forgottenMoves.size()) {
			int x2 = sumX - 4;
			int y2 = height + gp.tileSize;
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		}
		// Up Arrow
		if (remindNum != 0) {
			int x2 = sumX - 4;
			int y2 = (int) (gp.tileSize * 2.5);
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			boolean learnedMove = false;
            for (int k = 0; k < 4; k++) {
                if (p.moveset[k] == null) {
                	p.moveset[k] = new Moveslot(m);
                	Task.addTask(Task.TEXT, p.nickname + " learned " + m + "!");
                    learnedMove = true;
                    break;
                }
            }
            if (!learnedMove) {
            	Task t = Task.addTask(Task.MOVE, "", p);
    			t.move = m;
            }
			currentTask = null;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			Task.addTask(Task.PARTY, "Teach # a move?", Task.REMIND);
			currentTask = null;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			int amt = gp.keyH.ctrlPressed ? 5 : 1;
			remindNum -= amt;
			if (remindNum < 0) {
				remindNum = 0;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			int amt = gp.keyH.ctrlPressed ? 5 : 1;
			remindNum += amt;
			if (remindNum > forgottenMoves.size() - 1) {
				remindNum = forgottenMoves.size() - 1;
			}
		}
		
		drawToolTips("OK", null, "Back", null);
	}

	private void drawPartyTask() {
		drawParty(currentTask.move, null);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			Pokemon p = gp.player.p.team[partyNum];
			boolean skip = false;
			if (currentTask.move != null) {
				int move = p.canLearnMove(currentTask.move);
				if (move == 0) {
					Task.addTask(Task.TEXT, p.nickname + " can't learn " + currentTask.move + "!");
					skip = true;
				} else if (move == 2) {
					Task.addTask(Task.TEXT, p.nickname + " already knows " + currentTask.move + "!");
					skip = true;
				} else if (move == -1) {
					Task.addTask(Task.TEXT, "Error: currentTask.move was null");
					skip = true;
				}
			}
			if (!skip) {
				String message = currentTask.message.replace("#", p.nickname);
				Task t = Task.addTask(currentTask.counter, message, p); // pass in the Task type and Message in the Task.PARTY creation
				t.setMove(currentTask.move);
			}
			remindNum = 0;
			currentTask = null;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
		}
		
		drawToolTips("OK", null, "Back", null);
	}
	
	private void drawEvoInfoParty() {
		drawParty(null);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			Pokemon p = gp.player.p.team[partyNum];
			switch(p.id) {
			case 238:
			case 239: // scraggy
				Task.addTask(Task.DIALOGUE, currentTask.e, "Hmm... yes, I see...");
				int headbuttCrits = p.headbuttCrit;
				if (headbuttCrits >= 5) {
					if (p.id == 239) {
						Task.addTask(Task.DIALOGUE, currentTask.e, "Your " + p.nickname + " looks ready to evolve! "
							+ "It has crit its Headbutts " + headbuttCrits + " times!");
					} else {
						Task.addTask(Task.DIALOGUE, currentTask.e, "Your " + p.nickname + " has full potential to evolve, but it's too young right now. "
							+ "Once it becomes a Scrafty, it might evolve again!");
					}
				} else if (headbuttCrits >= 1) {
					Task.addTask(Task.DIALOGUE, currentTask.e, "You've made great progress as a trainer with you and your " + p.nickname + ". "
							+ "So far, you've crit Headbutts " + headbuttCrits + " time(s)!");
				} else {
					Task.addTask(Task.DIALOGUE, currentTask.e, "Your Pokemon will need to reach 5 Headbutt crits in Trainer battles to reach its "
							+ "full potential. Focus on that if you want your " + p.nickname + " to reach its maximum power!");
				}
				break;
			case 261: // gulpin-x
				Task.addTask(Task.DIALOGUE, currentTask.e, "Ah! A Xhenovian Gulpin, in the flesh! Can I take a look?");
				Task.addTask(Task.DIALOGUE, currentTask.e, "...");
				Task.addTask(Task.DIALOGUE, currentTask.e, "You see, these Pokemon occur here naturally to protect our region from extraterrestrial "
						+ "forces. Once they swallow enough space matter, they'll evolve!");
				if (p.spaceEat < 25) {
					Task.addTask(Task.DIALOGUE, currentTask.e, "Your " + p.nickname + " will need to eat " + (25 - p.spaceEat) + " more Galactic-type "
							+ "attack(s) in Trainer battles in order to reach its full potential.");
					Task.addTask(Task.DIALOGUE, currentTask.e, "This can be done by switching into an incoming Galactic move to take the hit, as "
							+ "all Xhenovian Gulpins' will eat it right up!");
				} else {
					Task.addTask(Task.DIALOGUE, currentTask.e, "Wow! Your " + p.nickname + " has eaten a lot of space matter! It looks ready to "
							+ "evolve!");
				}
				break;
			case 257: // seviper
				Task.addTask(Task.DIALOGUE, currentTask.e, "Ah, a Seviper! Y'know, the Professor is surprised that Seviper doesn't have a "
						+ "regional form here, just an exclusive evolution!");
				if (p.tailCrit < 5) {
					Task.addTask(Task.DIALOGUE, currentTask.e, "Although, yours will need to hit a few more Critical hits with its tail "
							+ "in order to unlock its full potential. " + (5 - p.tailCrit) + ", to be exact.");
				} else {
					Task.addTask(Task.DIALOGUE, currentTask.e, "Woah, and yours looks like its ready to evolve! Once you level it up, "
							+ "it should evolve!");
				}
				break;
			default:
				Task.addTask(Task.DIALOGUE, npc, "I'm sorry, that's not a Pokemon I specialize in.");
				Task.addTask(Task.DIALOGUE, npc, "I look for Pokemon that need to reach certain criteria in battle other than the standard level-up, "
						+ "such as Headbutt crits or Tail move crits in Trainer battles.");
				Task.addTask(Task.DIALOGUE, npc, "If you have any of those Pokemon and want to check how close they are to evolving, show them to me!");
				break;
			}
			Task.addTask(Task.EVO_INFO, currentTask.e, "");
			currentTask = null;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
		}
		
		drawToolTips("OK", null, "Back", null);
		
	}
	
	private void drawPokedex() {
		int x = 0;
		int y = 0;
		int width = gp.tileSize * 7;
		int height = gp.tileSize * 12;
		
		drawSubWindow(x, y, width, height);
		
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		drawSubWindow(x, y, width, (int) (gp.tileSize * 1.25));
		y += gp.tileSize * 5 / 6;
		
		String dexTitle = gp.player.p.getDexTitle(dexType);
		g2.drawString(dexTitle, getCenterAlignedTextX(dexTitle, x + width / 2), y);
		
		g2.setFont(g2.getFont().deriveFont(24F));		
		x += gp.tileSize / 2;
		y += gp.tileSize / 2;
		int startX = x;
		int textHeight = (int) (gp.tileSize * 0.75);
		
		Pokemon[] dex = gp.player.p.getDexType(dexType);
		dex = gp.player.getPokemonOfType(dex);
		int maxShow = gp.player.p.getDexShowing(dex);
		for (int i = dexNum[dexType]; i < dexNum[dexType] + 11; i++) {
			int textY = y + gp.tileSize / 2;
			if (i == dexNum[dexType]) {
				g2.drawString(">", x, textY);
			}
			x += gp.tileSize / 3;
			if (i <= maxShow) {
				int mode = gp.player.p.pokedex[dex[i].id];
				if (mode == 2) {
					g2.drawImage(ballIcon, x, y + 8, null);
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedDexNo(dex[i].getDexNo()) + " " + Pokemon.getName(dex[i].id), x, textY);
					x += gp.tileSize * 4;
					g2.drawImage(Pokemon.getType1(dex[i].id).getImage(), x, y + 4, null);
					x += gp.tileSize / 2;
					if (Pokemon.getType2(dex[i].id) != null) g2.drawImage(Pokemon.getType2(dex[i].id).getImage(), x, y + 4, null);
				} else if (mode == 1) {
					g2.setColor(Color.DARK_GRAY);
					int circleDiameter = 16;
					g2.fillOval(x, y + 8, circleDiameter, circleDiameter);
					g2.setColor(Color.WHITE);
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedDexNo(dex[i].getDexNo()) + " " + Pokemon.getName(dex[i].id), x, textY);
				} else {
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedDexNo(dex[i].getDexNo()) + " " + "---", x, textY);
				}
				x = startX;
				y += textHeight;
			}
		}
		
		int mode = gp.player.p.pokedex[dex[dexNum[dexType]].id];
		ArrayList<Move> levelMoveList = new ArrayList<>();
		ArrayList<String> levelLevelList = new ArrayList<>();
		ArrayList<Item> tmList = new ArrayList<>();
		Pokemon test = new Pokemon(dex[dexNum[dexType]].id, 5, false, false);
		test.abilitySlot = 0;
		test.headbuttCrit = -1;
		test.setAbility(test.abilitySlot);
		if (mode == 2) {
			Node[] movebank = test.getMovebank();
			if (movebank != null) {
			    for (int i = 0; i < movebank.length; i++) {
			        Node move = movebank[i];
			        while (move != null) {
			        	levelMoveList.add(move.data);
			        	levelLevelList.add(i == 0 ? "Evo." : i + "");
			            move = move.next;
			        }
			    }
			}
			
			for (int i = 93; i < 200; i++) {
				Item testItem = Item.getItem(i);
		    	if (testItem.getLearned(test)) {
		    		tmList.add(testItem);
		    	}
			}
		}
		drawDexSummary(test, mode, levelMoveList, levelLevelList, tmList);
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			if (dexMode == 0) {
				int amt;
				if (gp.keyH.ctrlPressed) {
					amt = 5;
				} else {
					amt = 1;
				}
				dexNum[dexType] -= amt;
				if (dexNum[dexType] <= 0) {
					dexNum[dexType] = 0;
				}
				levelDexNum = 0;
				tmDexNum = 0;
			} else if (dexMode == 1) {
				if (levelDexNum > 0) {
					levelDexNum--;
				}
			} else if (dexMode == 2) {
				if (tmDexNum > 0) {
					tmDexNum--;
				}
			}
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			if (dexMode == 0) {
				int amt;
				if (gp.keyH.ctrlPressed) {
					amt = 5;
				} else {
					amt = 1;
				}
				dexNum[dexType] += amt;
				if (dexNum[dexType] >= maxShow) {
					dexNum[dexType] = maxShow;					
				}
				levelDexNum = 0;
				tmDexNum = 0;
			} else if (dexMode == 1) {
				if (levelDexNum < levelMoveList.size() - 1) {
					levelDexNum++;
				}
			} else if (dexMode == 2) {
				if (tmDexNum < tmList.size() - 1) {
					tmDexNum++;
				}
			}
		}
		if (gp.keyH.rightPressed || gp.keyH.wPressed) {
			gp.keyH.rightPressed = false;
			gp.keyH.wPressed = false;
			if (mode == 2 && dexMode < 2 && !(dexMode == 1 && tmList.size() == 0)) dexMode++;
		}
		if (gp.keyH.leftPressed || gp.keyH.sPressed) {
			gp.keyH.leftPressed = false;
			if (dexMode > 0) {
				dexMode--;
			} else if (gp.keyH.sPressed) {
				subState = 0;
			}
			gp.keyH.sPressed = false;
		}
		
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			int max = starAmt >= 5 ? 4 : gp.player.p.calculatePokedexes();
			dexType = (dexType + 1) % max;
			levelDexNum = 0;
			tmDexNum = 0;
		}
		
		int infoX = 0;
		int infoY = (int) (gp.screenHeight - gp.tileSize * 2.5);
		int infoWidth = width;
		int infoHeight = (int) (gp.tileSize * 2.5);
		
		drawSubWindow(infoX, infoY, infoWidth, infoHeight, 255);
		
		int infoTextX = infoX + gp.tileSize / 2;
		int infoTextY = infoY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(32F));
		int[] amounts = gp.player.p.getDexAmounts(dex);
		int caughtAmt = amounts[1];
		int seenAmt = amounts[0] + caughtAmt;
		String caughtString = "Caught: " + caughtAmt;
		String seenString = "Seen: " + seenAmt;
		if (caughtAmt >= dex.length) {
			g2.setPaint(new GradientPaint(infoTextX, infoTextY, new Color(255, 215, 0), infoTextX + gp.tileSize, infoTextY - gp.tileSize, new Color(245, 225, 210)));
		}
		g2.drawString(caughtString, infoTextX, infoTextY);
		
		g2.setColor(Color.WHITE);
		infoTextY += gp.tileSize;
		if (seenAmt >= dex.length) {
			g2.setPaint(new GradientPaint(infoTextX, infoTextY, new Color(192, 192, 192), infoTextX + gp.tileSize, infoTextY - gp.tileSize, new Color(245, 225, 210)));
		}
		g2.drawString(seenString, infoTextX, infoTextY);
		
		infoTextX += gp.tileSize * 3.2;
		infoTextY -= gp.tileSize / 2;
		g2.setFont(g2.getFont().deriveFont(40F));
		int starWidth = (gp.tileSize * 2 / 3) - 4;
		for (int i = 0; i < starAmt; i++) {
			// Create a GradientPaint for each star individually
		    int starX = infoTextX + (i * starWidth);
		    GradientPaint starPaint = new GradientPaint(
		        starX + 6, infoTextY - 6, new Color(255, 215, 0), // Start point and color
		        starX + starWidth - 10, infoTextY - starWidth + 10, new Color(245, 225, 210) // End point and color
		    );
		    g2.setPaint(starPaint);
		    g2.drawString('\u2605' + "", starX, infoTextY);
		}
		
		String wText = dexMode == 0 ? "Moves": null;
		drawToolTips(wText, "Switch", "Back", "Back");
	}

	private void drawDexSummary(Pokemon p, int mode, ArrayList<Move> levelMoveList, ArrayList<String> levelLevelList, ArrayList<Item> tmList) {
		if (mode == 0) return;
		int x = gp.tileSize * 7;
		int y = 0;
		int width = gp.tileSize * 9;
		int height = (int) (mode == 2 ? gp.tileSize * 12 : gp.tileSize * 4.5);
		
		drawSubWindow(x, y, width, height);
		
		// ID
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setColor(p.getDexNoColor());
		g2.setFont(g2.getFont().deriveFont(20F));
		if (gp.keyH.shiftPressed) {
			g2.drawString(Pokemon.getFormattedDexNo(p.getID()), x, y);
		} else {
			g2.drawString(Pokemon.getFormattedDexNo(p.getDexNo()), x, y);
		}
		
		g2.setColor(Color.WHITE);
		
		// Name
		x += gp.tileSize / 2;
		y += gp.tileSize / 4;
		int startX = x;
		g2.setFont(g2.getFont().deriveFont(36F));
		g2.drawString(p.name(), x, y + gp.tileSize / 2);
		
		x = startX;
		y += gp.tileSize;
		int startY = y;
		
		// Sprite
		g2.drawImage(p.getSprite(), x - 12, y, null);
		x += gp.tileSize * 2;
		g2.drawImage(p.type1.getImage2(), x - 12, y, null);
		if (p.type2 != null) {
			g2.drawImage(p.type2.getImage2(), x + 36, y + 36, null);
		}
		
		// Abilities
		g2.setFont(g2.getFont().deriveFont(28F));
		x += gp.tileSize * 3.75;
		y -= gp.tileSize;
		String abilityLabel = "Abilities:";
		g2.drawString(abilityLabel, getCenterAlignedTextX(abilityLabel, x), y);
		g2.setFont(g2.getFont().deriveFont(24F));
		y += gp.tileSize * 0.75;
		String ability = mode == 2 ? p.ability.toString() : "???";
		g2.drawString(ability, getCenterAlignedTextX(ability, x), y);
		
		if (mode == 1) return;
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String[] abilityDesc = Item.breakString(p.ability.desc, 26).split("\n");
		for (String s : abilityDesc) {
			y += gp.tileSize / 2 - 4;
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
		}
		
		g2.setFont(g2.getFont().deriveFont(24F));
		y += gp.tileSize * 0.75;
		p.setAbility(1);
		String ability2 = p.ability.toString();
		g2.drawString(ability2, getCenterAlignedTextX(ability2, x), y);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String[] abilityDesc2 = Item.breakString(p.ability.desc, 26).split("\n");
		for (String s : abilityDesc2) {
			y += gp.tileSize / 2 - 4;
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
		}
		
		g2.setFont(g2.getFont().deriveFont(28F));
		y += gp.tileSize * 0.5;
		String hAbilityLabel = "-----------------";
		g2.drawString(hAbilityLabel, getCenterAlignedTextX(hAbilityLabel, x), y);
		g2.setFont(g2.getFont().deriveFont(24F));
		y += gp.tileSize * 0.5;
		p.setAbility(2);
		String ability3;
		if (p.ability == Ability.NULL) {
			ability3 = "N/A";
			g2.drawString(ability3, getCenterAlignedTextX(ability3, x), y);
		} else {
			ability3 = p.ability.toString();
			g2.drawString(ability3, getCenterAlignedTextX(ability3, x), y);
			
			g2.setFont(g2.getFont().deriveFont(16F));
			String[] abilityDesc3 = Item.breakString(p.ability.desc, 26).split("\n");
			for (String s : abilityDesc3) {
				y += gp.tileSize / 2 - 4;
				g2.drawString(s, getCenterAlignedTextX(s, x), y);
			}
		}
		
		// Stats
		startX -= gp.tileSize / 2;
		y = (int) (startY + gp.tileSize * 2.5);
		for (int i = 0; i < 6; i++) {
			int sY = y;
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(Color.WHITE);
			x = startX;
			String type = Pokemon.getStatType(i, false);
        	g2.drawString(type, x, y);
        	
        	g2.setFont(g2.getFont().deriveFont(16F));
        	x += gp.tileSize * 0.75;
        	int statWidth = 3 * gp.tileSize;
        	int statHeight = gp.tileSize / 2;
        	y -= gp.tileSize / 2 - 4;
        	g2.setColor(Color.BLACK);
        	g2.fillRect(x, y, statWidth, statHeight);
        	g2.setColor(Color.WHITE);
        	g2.fillRect(x + 2, y + 2, statWidth - 4, statHeight - 4);
        	double bar = Math.min(p.getBaseStat(i) * 1.0 / 200, 1.0);
        	g2.setColor(Pokemon.getColor(p.getBaseStat(i)));
        	g2.fillRect(x + 2, y + 2, (int) ((statWidth - 4) * bar), statHeight - 4);
        	x += 4;
        	y += (gp.tileSize / 2) - 4;
        	g2.setColor(Color.BLACK);
        	g2.drawString(p.getBaseStat(i) + "", x, y);
        	
        	y = sY + gp.tileSize / 2;
		}
		g2.setFont(g2.getFont().deriveFont(20F));
		g2.setColor(Color.WHITE);
		x = startX;
		y += gp.tileSize / 4;
		String totalText = "Total: " + p.getBST();
		g2.drawString(totalText, getRightAlignedTextX(totalText, x + 180), y - 12);
		
		// Evolutions
		int moveStartY = y + gp.tileSize;
		y += gp.tileSize / 6;
		String evolve = p.getEvolveString();
		if (evolve == null) evolve = "Does not evolve";
		String[] evolves = evolve.split("\n");
		for (String s : evolves) {
			g2.drawString(s, x, y);
			y += gp.tileSize / 3 + 2;
		}
		
		// Level up moves
		y = moveStartY;
		g2.drawString("Level Up Moves: ", x, y);
		y += 8;
		int moveWidth = gp.tileSize * 4;
		int moveHeight = gp.tileSize / 2;
		Move m = null;
		for (int i = levelDexNum; i < levelDexNum + 5; i++) {
	        if (i == levelDexNum && dexMode == 1) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x, y, moveWidth, moveHeight, 8, 8);
	            m = levelMoveList.get(i);
	        }
	        if (i < levelMoveList.size()) {
	        	g2.setColor(levelMoveList.get(i).mtype.getColor());
		        g2.fillRoundRect(x, y, moveWidth, moveHeight, 8, 8);
		        g2.setColor(Color.BLACK);
		        g2.drawString(levelLevelList.get(i) + " " + levelMoveList.get(i).toString(), x + 8, y + 19);
		        y += gp.tileSize * 0.6;
	        }
		}
		
		// TM moves
		x += gp.tileSize * 4.25;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(20F));
		y = moveStartY;
		g2.drawString("TM/HM Moves: ", x, y);
		y += 8;
		
		for (int i = tmDexNum; i < tmDexNum + 5; i++) {
			if (i == tmDexNum && dexMode == 2) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x, y, moveWidth, moveHeight, 8, 8);
	            m = tmList.get(i).getMove();
	        }
			if (i < tmList.size()) {
				g2.setColor(tmList.get(i).getMove().mtype.getColor());
		        g2.fillRoundRect(x, y, moveWidth, moveHeight, 8, 8);
		        g2.setColor(Color.BLACK);
		        g2.drawString(tmList.get(i).toString(), x + 8, y + 19);
			}
	        y += gp.tileSize * 0.6;
		}
		
		int moveSumX = gp.tileSize * 6;
		int moveSumY = gp.tileSize * 2;
		if (dexMode > 0) drawMoveSummary(moveSumX, moveSumY, p, null, null, m);
	}

	private void drawBoxScreen() {
		int cBoxIndex = gauntlet ? -1 : gp.player.p.currentBox;
		Pokemon[][] boxes = gp.player.p.boxes;
		Pokemon[] cBox = gauntlet ? gp.player.p.gauntletBox : boxes[cBoxIndex];
		Pokemon[] dBox = pSelectBox >= 0 ? boxes[pSelectBox] : gp.player.p.gauntletBox;
		
		int x = (int) (gp.tileSize * 1.75);
		int y = gp.tileSize;
		int width = (int) (gp.tileSize * 12.75);
		int height = gp.tileSize * 11;
		drawSubWindow(x, y, width, height);
		
		int headX = x;
		int headY = 0;
		int headWidth = width;
		int headHeight = gp.tileSize;
		drawSubWindow(headX, headY, headWidth, headHeight);
		
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(28F));
		int headTextY = (int) (headY + gp.tileSize * 0.75);
		if (!gauntlet) {
			g2.drawString("<", headX + gp.tileSize / 2, headTextY);
			g2.drawString(">", headWidth + headX - gp.tileSize / 2 - 8, headTextY);
		} else if (!isGauntlet) {
			int x2 = headX + gp.tileSize / 2;
			int y2 = headTextY - gp.tileSize / 2;
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
			x2 = headWidth + headX - gp.tileSize / 2 - gp.tileSize / 3;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		}
		String boxText = gauntlet ? "Gauntlet Box" : gp.player.p.boxLabels[cBoxIndex];
		int centerX = headX + (headWidth / 2);
		g2.drawString(boxText, getCenterAlignedTextX(boxText, centerX), headTextY);
		int highlightWidth = gauntlet ? gp.tileSize * 2 : getHighlightWidth(boxText);
		if (boxNum < 0) {
			g2.drawRoundRect(centerX - highlightWidth, (int) (headTextY - gp.tileSize / 2), highlightWidth * 2, (int) (gp.tileSize * 0.6), 10, 10);
		}
		
		int startX = x + gp.tileSize / 2;
		int startY = y + gp.tileSize / 2;
		int spriteWidth = gp.tileSize * 2;
		int spriteHeight = spriteWidth;
		int cX = startX;
		int cY = startY;
		for (int i = 0; i < 30; i++) {
			if (i == boxSwapNum && cBoxIndex == pSelectBox) {
				g2.setColor(new Color(100, 100, 220, 200));
				g2.fillRoundRect(cX - 2, cY - 2, spriteWidth - 10, spriteHeight - 10, 10, 10);
				g2.setColor(g2.getColor().darker());
				g2.drawRoundRect(cX - 2, cY - 2, spriteWidth - 10, spriteHeight - 10, 10, 10);
			} else if (tmCheck != null && i < cBox.length && cBox[i] != null) {
				int canUse = cBox[i].canUseItem(tmCheck);
				Color backgroundColor = canUse == 0 ? new Color(210, 35, 15, 200) : canUse == 1 ? new Color(15, 210, 40, 200) : new Color(230, 220, 50, 200);
				g2.setColor(backgroundColor);
				g2.fillRoundRect(cX - 2, cY - 2, spriteWidth - 10, spriteHeight - 10, 10, 10);
				g2.setColor(g2.getColor().darker());
				g2.drawRoundRect(cX - 2, cY - 2, spriteWidth - 10, spriteHeight - 10, 10, 10);
			}
			
			if (i < cBox.length && cBox[i] != null) {
				g2.drawImage(cBox[i].getSprite(), cX, cY, null);
				int itemX = cX + 4;
				int itemY = cY + 56;
				if (!showBoxParty && (cBox[i] == itemSwapP || (itemSwapP != null && i == boxNum))) {
					drawItemSelectBackground(itemX - 6, itemY - 6);
				}
				if (cBox[i].item != null) {
					g2.drawImage(cBox[i].item.getImage(), itemX, itemY, null);
				}
				if (cBox[i] == itemSwapP) {
					drawItemSelectBorder(itemX - 6, itemY - 6);
				}
			}
			
			// draw type icon when checking TMs for Hidden Power/Return
			if (i < cBox.length && cBox[i] != null && (tmCheck == Item.TM26 || tmCheck == Item.TM99)) {
				int iconX = cX + 56;
				int iconY = cY + 56;
				g2.drawImage(cBox[i].determineHPType().getImage(), iconX, iconY, null);
			}
			
			g2.setColor(Color.WHITE);
			if (i == boxNum) {
				g2.drawRoundRect(cX - 8, cY - 8, spriteWidth, spriteHeight, 10, 10);
			}
			
			if (i >= Player.GAUNTLET_BOX_SIZE && gauntlet) {
				g2.setColor(Color.DARK_GRAY);
				g2.fillRect(cX, cY, spriteWidth, spriteHeight);
			}
			
			if ((i + 1) % 6 == 0) {
				cX = startX;
				cY += spriteHeight;
			} else {
				cX += spriteWidth;
			}
		}
		
		if (boxSwapNum >= 0 || partySelectedNum >= 0) {
			Pokemon selected = boxSwapNum >= 0 ? dBox[boxSwapNum] : gp.player.p.team[partySelectedNum];
			int selectX = 0;
			int selectY = 0;
			int selectWidth = gp.tileSize * 2;
			int selectHeight = gp.tileSize * 2;
			
			drawSubWindow(selectX, selectY, selectWidth, selectHeight);
			
			selectX += 8;
			selectY += 8;
			selectWidth -= 16;
			selectHeight -= 16;
			
			g2.setColor(new Color(100, 100, 220, 200));
			g2.fillRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
			g2.setColor(g2.getColor().darker());
			g2.drawRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
			
			if (selected != null) {
				g2.drawImage(selected.getSprite(), selectX, selectY, null);
				if (selected.item != null) {
					g2.drawImage(selected.item.getImage(), selectX + 4, selectY + 56, null);
				}
			}
		}
		
		if (itemSwapP != null) {
			int selectX = gp.tileSize;
			int selectY = gp.tileSize * 2;
			int selectWidth = gp.tileSize;
			int selectHeight = gp.tileSize;
			
			Item item = itemSwapP.item;
			
			drawSubWindow(selectX, selectY, selectWidth, selectHeight);
			
			selectX += 8;
			selectY += 8;
			selectWidth -= 16;
			selectHeight -= 16;
			
			g2.setColor(new Color(100, 100, 220, 200));
			g2.fillRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
			g2.setColor(g2.getColor().darker());
			g2.drawRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
			
			if (item != null) {
				g2.drawImage(item.getImage(), selectX + 4, selectY + 4, null);
			}
		}
		
		if (tmCheck != null) {
			int selectX = (int) (gp.tileSize * 14.25);
			int selectY = 0;
			drawTMCheckWindow(selectX, selectY);
		}
		
		if (!showBoxSummary && !showBoxParty && !release && nicknaming < 0) {
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (boxNum >= 0) {
					currentBoxP = cBox[boxNum];
				} else if (!gauntlet) {
					nickname = new StringBuilder(gp.player.p.boxLabels[cBoxIndex]);
					setNicknaming(true);
					return;
				}
				if (currentBoxP != null) {
					if (!gp.keyH.ctrlPressed) {
						showBoxSummary = true;
					} else {
						gp.keyH.ctrlPressed = false;
						release = true;
						commandNum = 1;
					}
				}
			}
			
			if (gp.keyH.aPressed && boxNum >= 0) {
				gp.keyH.aPressed = false;
				if (boxSwapNum == boxNum && cBoxIndex == pSelectBox) {
					// withdraw
					if (cBox[boxNum] != null) {
	    				int nullIndex = -1;
	    				for (int i = 0; i < gp.player.p.team.length; i++) {
	    					if (gp.player.p.team[i] == null) {
	    						nullIndex = i;
	    						break;
	    					}
	    				}
	    				if (nullIndex == -1) {
	    					partyNum = 0;
	    					showBoxParty = true;
	    					return;
	    				} else {
	    					gp.player.p.team[nullIndex] = cBox[boxNum];
	    					cBox[boxNum] = null;
	    					boxSwapNum = -1;
	    				}
	    			}
				} else if (partySelectedNum >= 0) {
					if (cBox[boxNum] == null) {
						if (partySelectedNum == 0 || gp.player.p.team[partySelectedNum - 1] != null) { // depositing lead or it's not the last pokemon in your party
							if (partySelectedNum == 0) {
                            	if (gp.player.p.team[partySelectedNum + 1] == null) {
                            		showMessage("That's your last Pokemon!");
    	                            return;
                            	}
                            }
                            if (gp.player.p.teamWouldBeFainted(partySelectedNum)) {
                            	showMessage("That's your last Pokemon!");
	                            return;
                            }
						}
					}
					if (cBox[boxNum] instanceof Egg && partySelectedNum == 0 && gp.player.p.teamWouldBeFainted(partySelectedNum)) {
						showMessage("That's your last Pokemon!");
                        return;
					}
					Pokemon temp = gp.player.p.team[partySelectedNum];
					if (temp != null) {
                        temp.heal();
                    }
					gp.player.p.team[partySelectedNum] = cBox[boxNum];
					cBox[boxNum] = temp;
					
					if (gp.player.p.team[partySelectedNum] == null) {
                    	gp.player.p.shiftTeamForward(partySelectedNum);
                    }
					
					if (partySelectedNum == 0) gp.player.p.setCurrent();
					
					partySelectedNum = -1;
				} else if (boxSwapNum >= 0) {
					Pokemon temp = dBox[boxSwapNum];
					dBox[boxSwapNum] = cBox[boxNum];
					cBox[boxNum] = temp;
					boxSwapNum = -1;
				} else {
					boxSwapNum = boxNum;
					pSelectBox = cBoxIndex;
				}
			}
			
			if (gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				if (boxNum >= 0) {
					boxNum -= 6;
				} else if (!isGauntlet) {
					gauntlet = !gauntlet;
				}
			}
			
			if (gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				if (gauntlet) {
					if (boxNum < 0) boxNum = 0;
				} else {
					if (boxNum < 24) {
						boxNum += 6;
					}
				}
			}
			
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				if (boxNum < 0) {
					if (!gauntlet) {
						gp.player.p.currentBox--;
				        if (gp.player.p.currentBox < 0) {
				        	gp.player.p.currentBox = Player.MAX_BOXES - 1;
				        }
					}
				} else {
					if (boxNum % 6 != 0) {
						boxNum--;
					}
				}
			}
			
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				if (boxNum < 0) {
					if (!gauntlet) {
						gp.player.p.currentBox++;
				        if (gp.player.p.currentBox >= Player.MAX_BOXES) {
				        	gp.player.p.currentBox = 0;
				        }
					}
				} else {
					if (gauntlet) {
						if (boxNum < Player.GAUNTLET_BOX_SIZE - 1) {
							boxNum++;
						}
					} else {
						if ((boxNum + 1) % 6 != 0) {
							boxNum++;
						}
					}
				}
			}
		}
		
		if (showBoxParty) {
			if (gp.keyH.wPressed && !showBoxSummary) {
				gp.keyH.wPressed = false;
				currentBoxP = gp.player.p.team[partyNum];
				if (currentBoxP != null) {
					showBoxSummary = true;
				}
			}
			
			if (gp.keyH.aPressed && !showBoxSummary) {
				gp.keyH.aPressed = false;
				if (partySelectedNum == partyNum) {
					// deposit
	            	int index = -1;
	                for (int i = 0; i < cBox.length; i++) {
	                	if (cBox[i] == null) {
	                		index = i;
	                		break;
	                	}
	                }
	                if (index == -1) {
	                	showMessage("Box is full!");
	                	return;
	                } else if ((partyNum == 0 && gp.player.p.team[1] == null) || gp.player.p.teamWouldBeFainted(partyNum)) {
                		showMessage("That's your last Pokemon!");
                		return;
                	} else {
	                	Pokemon temp = gp.player.p.team[partyNum];
                        if (temp != null) {
                            temp.heal();
                        }
                        gp.player.p.team[partyNum] = null;
                        cBox[index] = temp;
                        
                        gp.player.p.shiftTeamForward(partyNum);
                        if (gp.player.p.team[partyNum] == null) partyNum--;
	                }
	                partySelectedNum = -1;
				} else if (boxSwapNum >= 0) {
					if (dBox[boxSwapNum] == null) {
						if (partyNum == 0 || gp.player.p.team[partyNum - 1] != null) { // depositing lead or it's not the last pokemon in your party
							if (partyNum == 0) {
                            	if (gp.player.p.team[partyNum + 1] == null) {
                            		showMessage("That's your last Pokemon!");
    	                            return;
                            	}
                            }
                            if (gp.player.p.teamWouldBeFainted(partyNum)) {
                            	showMessage("That's your last Pokemon!");
	                            return;
                            }
						}
					}
					
					if (dBox[boxSwapNum] instanceof Egg && partyNum == 0 && gp.player.p.teamWouldBeFainted(partyNum)) {
						showMessage("That's your last Pokemon!");
                        return;
					}
					
					Pokemon temp = gp.player.p.team[partyNum];
					if (temp != null) {
                        temp.heal();
                    }
					gp.player.p.team[partyNum] = dBox[boxSwapNum];
					dBox[boxSwapNum] = temp;
					
					if (gp.player.p.team[partyNum] == null) {
                    	gp.player.p.shiftTeamForward(partyNum);
                    }
					
					if (partyNum == 0) gp.player.p.setCurrent();
					
					boxSwapNum = -1;
				} else if (partySelectedNum >= 0) {
					gp.player.p.swap(partySelectedNum, partyNum);
					partySelectedNum = -1;
				} else {
					partySelectedNum = partyNum;
				}
			}
		}
		
		if (release) {
			if (gp.keyH.upPressed || gp.keyH.downPressed) {
				gp.keyH.upPressed = false;
				gp.keyH.downPressed = false;
				commandNum = 1 - commandNum;
			}
			
			currentDialogue = "Are you sure you want to release " + currentBoxP.nickname + "?";
			drawDialogueScreen(true);
			
			int rX = gp.tileSize * 11;
			int rY = gp.tileSize * 4;
			int rWidth = gp.tileSize * 3;
			int rHeight = (int) (gp.tileSize * 2.5);
			drawSubWindow(rX, rY, rWidth, rHeight);
			rX += gp.tileSize;
			rY += gp.tileSize;
			g2.drawString("Yes", rX, rY);
			if (commandNum == 0) {
				g2.drawString(">", rX-24, rY);
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					release = false;
					cBox[boxNum] = null;
					showMessage(currentBoxP.nickname + " was released!\nBye bye, " + currentBoxP.nickname + "!");
					commandNum = 0;
				}
			}
			rY += gp.tileSize;
			g2.drawString("No", rX, rY);
			if (commandNum == 1) {
				g2.drawString(">", rX-24, rY);
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					release = false;
					commandNum = 0;
				}
			}
			
			drawToolTips("OK", null, "Back", "Back");
		}
		
		if (gp.keyH.dPressed) {
			if (!showBoxSummary && !release && nicknaming < 0) { // in idle box/party
				gp.keyH.dPressed = false;
				if (gp.keyH.ctrlPressed) {
					if (!showBoxParty) { // party is closed
						if (itemSwapP == null) { // no pokemon has been selected to swap items yet
							itemSwapP = cBox[boxNum];
						} else {
							Pokemon swap = cBox[boxNum];
							if (swap != null && itemSwapP != swap) {
								gp.player.p.swapItem(itemSwapP, swap);
								itemSwapP = null;
							}
						}
					} else { // party is open
						if (itemSwapP == null) {
							itemSwapP = gp.player.p.team[partyNum];
						} else {
							Pokemon swap = gp.player.p.team[partyNum];
							if (swap != null && itemSwapP != swap) {
								gp.player.p.swapItem(itemSwapP, swap);
								itemSwapP = null;
							}
						}
					}
				} else {
					showBoxParty = !showBoxParty;
					partyNum = 0;
				}
			}
		}
		
		if (nicknaming >= 0 && !showBoxSummary && !gauntlet) {
			currentDialogue = "Change box's name?";
			drawDialogueScreen(true);
			setNickname(null, false);
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					String name = nickname.toString().trim();
					nickname = new StringBuilder();
					if (name == null || name.trim().isEmpty()) name = "Box " + (cBoxIndex + 1);
					gp.player.p.boxLabels[cBoxIndex] = name;
					nicknaming = -1;
				}
				drawToolTips("OK", null, null, null);
			}
		}
		
		drawBoxToolTips(false);
	}

	private void drawTMCheckWindow(int selectX, int selectY) {
		int selectWidth = (int) (gp.tileSize * 1.75);
		int selectHeight = (int) (gp.tileSize * 1.75);
		
		Item tm = tmCheck;
		
		drawSubWindow(selectX, selectY, selectWidth, selectHeight);
		
		selectX += 8;
		selectY += 8;
		selectWidth -= 16;
		selectHeight -= 16;
		
		g2.setColor(new Color(230, 220, 50, 200));
		g2.fillRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
		g2.setColor(g2.getColor().darker());
		g2.drawRoundRect(selectX, selectY, selectWidth, selectHeight, 10, 10);
		
		g2.drawImage(tm.getImage2(), selectX + 10, selectY - 4, null);
		selectY += gp.tileSize + 12;
		selectX += selectWidth / 2;
		String text = tm.getMove().toString();
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(Math.min(getFontSize(text, selectWidth + 15), 16)));
		g2.drawString(text, getCenterAlignedTextX(text, selectX), selectY);
	}

	private int getHighlightWidth(String boxText) {
		float fontSize = g2.getFont().getSize2D(); // Default font size

	    FontMetrics metrics = g2.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (int) fontSize));
	    int textWidth = metrics.stringWidth(boxText);
	    
	    return (int) (textWidth * 0.75);
	}

	private void taskState() {
		if (currentTask == null) {
			if (tasks.size() > 0) {
				currentTask = tasks.remove(0);
			} else if (checkTasks) {
				gp.gameState = GamePanel.PLAY_STATE;
				return;
			}
		}
		
		drawTask();
	}

	private void rareCandyState() {
		drawParty(null);
		drawItemUsingScreen();
		
		if (currentTask == null && tasks.size() > 0) {
			currentTask = tasks.remove(0);
		}
		
		if (currentTask == null) {
			if (currentItem == Item.RARE_CANDY && gp.player.p.bag.count[Item.RARE_CANDY.getID()] <= 0) {
				gp.gameState = GamePanel.MENU_STATE;
				currentItems = gp.player.p.getItems(currentPocket);
				bagState = 0;
				return;
			} else if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				useRareCandy(gp.player.p.team[partyNum]);
			}
			if (tasks.size() == 0) {
				drawToolTips("Use", null, "Back", "Back");
			} else {
				drawToolTips("OK", null, null, null);
			}
		} else {
			drawTask();
		}
	}

	private void showMoveOptions() {
		if (currentMove == null) {
			drawMoveOptions(null, currentPokemon, currentHeader);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (moveOption > 0) {
					if (currentItem == Item.ELIXIR) {
						Moveslot m = currentPokemon.moveset[moveOption - 1];
						if (m.currentPP != m.maxPP) {
			        		m.currentPP = m.maxPP;
				        	showMessage(m.move.toString() + "'s PP was restored!");
				        	gp.player.p.bag.remove(currentItem);
				        	moveOption = -1;
			        		currentItems = gp.player.p.getItems(currentPocket);
			        	} else {
			        		showMessage("It won't have any effect.");
			        	}
					} else if (currentItem == Item.PP_UP || currentItem == Item.PP_MAX) {
						Moveslot m = currentPokemon.moveset[moveOption - 1];
						if (m.maxPP < (m.move.pp * 8 / 5)) {
			    			if (currentItem == Item.PP_UP) { // PP Up
			    				m.ppUp();
			    				showMessage(m.move.toString() + "'s PP was increased!");
			    			} else { // PP Max
			    				m.maxPP();
			    				showMessage(m.move.toString() + "'s PP was maxed!");
			    			}
				        	gp.player.p.bag.remove(currentItem);
				        	moveOption = -1;
			        		currentItems = gp.player.p.getItems(currentPocket);
			    		} else {
			    			showMessage("It won't have any effect.");
			    		}
					}
					showMoveOptions = false;
				}
			}
		} else {
			drawMoveOptions(currentMove, currentPokemon);
		}
		drawToolTips("OK", null, null, null);
	}
	
	private void showIVOptions() {
		drawIVOptions(currentPokemon, currentHeader);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (currentItem == Item.BOTTLE_CAP) {
				if (moveOption >= 0) {
					if (currentPokemon.ivs[moveOption] < 31) {
						currentPokemon.ivs[moveOption] = 31;
						currentPokemon.setStats();
			        	showMessage(Item.breakString(currentPokemon + "'s " + Pokemon.getStatType(moveOption, true) + " IV was maxed out!", MAX_TEXTBOX));
			        	gp.player.p.bag.remove(currentItem);
		        		currentItems = gp.player.p.getItems(currentPocket);
		        	} else {
		        		showMessage("It won't have any effect.");
		        	}
					showIVOptions = false;
					moveOption = -1;
				}
			} else if (currentItem == Item.RUSTY_BOTTLE_CAP) {
				if (moveOption >= 0) {
					if (currentPokemon.ivs[moveOption] > 0) {
						currentPokemon.ivs[moveOption] = 0;
						currentPokemon.setStats();
						currentPokemon.verifyHP();
			        	showMessage(Item.breakString(currentPokemon + "'s " + Pokemon.getStatType(moveOption, true) + " IV was set to 0!", MAX_TEXTBOX));
			        	gp.player.p.bag.remove(currentItem);
		        		currentItems = gp.player.p.getItems(currentPocket);
		        	} else {
		        		showMessage("It won't have any effect.");
		        	}
					showIVOptions = false;
					moveOption = -1;
				}
			}
		}
	}
	
	public void drawIVOptions(Pokemon p, String header) {
		int x = (int) (gp.tileSize * 5.5);
		int y = gp.tileSize * 2;
		int width = gp.tileSize * 5;
		int height = gp.tileSize * 8;
		drawSubWindow(x, y, width, height);
		
		int ivWidth = gp.tileSize * 4;
		int ivHeight = (int) (gp.tileSize * 7 / 8);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(header, x, y);
		y += gp.tileSize / 2;
		
		for (int i = 0; i < p.ivs.length; i++) {
			int iv = p.ivs[i];
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRoundRect(x, y, ivWidth, ivHeight, 10, 10);
			g2.setColor(Color.BLACK);
	        String text = Pokemon.getStatType(i, false) + ": " + iv;
	        g2.drawString(text, getCenterAlignedTextX(text, x + ivWidth / 2), y + gp.tileSize / 2);
	        if (moveOption == i) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x - 2, y - 2, ivWidth + 4, ivHeight + 4, 10, 10);
	        }
	        y += gp.tileSize;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			moveOption--;
			if (moveOption < 0) {
				moveOption = 5;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			moveOption++;
			if (moveOption > 5) {
				moveOption = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	private void showStatusOptions() {
		drawStatusOptions(currentPokemon, currentHeader);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (moveOption >= 0) {
				currentPokemon.status = Status.HEALTHY;
				Status status = Status.getNonVolStatuses().get(moveOption);
				switch(status) {
				case BURNED:
					if (currentPokemon.isType(PType.FIRE)) {
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.burn(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was burned!");
					}
					break;
				case ASLEEP:
					if (currentPokemon.isType(PType.PSYCHIC) && !gp.player.p.bag.contains(Item.TM49)) { // doesn't have rest
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.sleep(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was slept!");
					}
					break;
				case PARALYZED:
					if (currentPokemon.isType(PType.ELECTRIC)) {
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.paralyze(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was paralyzed!");
					}
					break;
				case FROSTBITE:
					if (currentPokemon.isType(PType.ICE)) {
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.freeze(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was frostbitten!");
					}
					break;
				case POISONED:
					if (currentPokemon.isType(PType.POISON) || currentPokemon.isType(PType.STEEL)) {
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.poison(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was poisoned!");
					}
					break;
				case TOXIC:
					if (currentPokemon.isType(PType.POISON) || currentPokemon.isType(PType.STEEL)) {
						showMessage("It won't have any effect.");
					} else {
						currentPokemon.toxic(false, null);
						if (currentPokemon.status == status) showMessage(currentPokemon + " was badly poisoned!");
					}
					break;
				default:
					break;
				}
				showStatusOptions = false;
				moveOption = -1;
			}
		}
	}
	
	public void drawStatusOptions(Pokemon p, String header) {
		int x = (int) (gp.tileSize * 5.5);
		int y = gp.tileSize * 2;
		int width = gp.tileSize * 5;
		int height = gp.tileSize * 8;
		drawSubWindow(x, y, width, height);
		
		int ivWidth = gp.tileSize * 4;
		int ivHeight = (int) (gp.tileSize * 7 / 8);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(header, x, y);
		y += gp.tileSize / 2;
		
		ArrayList<Status> statuses = Status.getNonVolStatuses();
		
		for (int i = 0; i < statuses.size(); i++) {
			Status status = statuses.get(i);
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(status.getColor());
			g2.fillRoundRect(x, y, ivWidth, ivHeight, 10, 10);
			g2.setColor(status.getTextColor());
	        String text = status.getName();
	        g2.drawString(text, getCenterAlignedTextX(text, x + ivWidth / 2), y + gp.tileSize / 2);
	        if (moveOption == i) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x - 2, y - 2, ivWidth + 4, ivHeight + 4, 10, 10);
	        }
	        y += gp.tileSize;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			moveOption--;
			if (moveOption < 0) {
				moveOption = statuses.size() - 1;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			moveOption++;
			if (moveOption >= statuses.size()) {
				moveOption = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	private void useRareCandy(Pokemon pokemon) {
		if (pokemon == null) return;
        if (pokemon.getLevel() == 100) {
            showMessage("It won't have any effect.");
            return;
        }
        gp.player.p.elevate(pokemon);
        if (currentItem == Item.RARE_CANDY) gp.player.p.bag.remove(Item.RARE_CANDY);
	}

	private void useItem() {
		drawItemUsingScreen();
		drawParty(currentItem);
		if (gp.keyH.wPressed && !showMoveOptions && !showIVOptions && !showStatusOptions) {
			if (currentItem == Item.RARE_CANDY || currentItem == Item.RARE_CANDY_BOX) {
				gp.gameState = GamePanel.RARE_CANDY_STATE;
			} else {
				gp.keyH.wPressed = false;
				gp.player.p.useItem(gp.player.p.team[partyNum], currentItem, gp);	
			}
		}
		
		drawToolTips("Use", null, "Back", "Back");
	}
	
	public void useItem(Item item, boolean wasAPressed) {
		this.wasAPressed = wasAPressed;
		boolean inGauntlet = !gp.canFly();
		if (item == Item.REPEL) {
			if (!gp.player.p.repel) {
				gp.player.useRepel();
		    } else {
		    	showMessage("It won't have any effect.");
		    }
			bagState = 0;
		} else if (item == Item.CALCULATOR) {
			Item.useCalc(gp.player.p.getCurrent(), null, null, true);
		} else if (item == Item.DEX_NAV) {
			encounters = Encounter.getAllEncounters();
			gp.gameState = GamePanel.DEX_NAV_STATE;
		} else if (item == Item.FISHING_ROD) {
			int result = gp.cChecker.checkTileType(gp.player);
			List<Integer> tileList = gp.tileM.getWaterTiles();
			if (tileList.contains(result)) {
				gp.gameState = GamePanel.PLAY_STATE;
				bagState = 0;
				subState = 0;
				gp.player.startFish(PlayerCharacter.currentMapName);
			} else {
				showMessage("Can't use now!");
			}
		} else if (item == Item.VISOR) {
			if (gp.player.p.visor) {
				showMessage("You took off the visor.");
			} else {
				showMessage("You put on the visor!");
			}
			bagState = 0;
			gp.player.p.visor = !gp.player.p.visor;
			gp.player.setupPlayerImages(gp.player.p.visor);
			drawLight = gp.determineMachineLight();
		} else if (item == Item.LETTER) {
			currentFont = monsier;
			currentLetter = 0;
			gp.gameState = GamePanel.LETTER_STATE;
		} else if (item == Item.LETTER_2) {
			currentFont = kids;
			currentLetter = 1;
			gp.gameState = GamePanel.LETTER_STATE;
		} else if (item == Item.HEALING_PACK) {
			if (inGauntlet) {
				boolean flag = gp.player.p.flag[1][2];
				showMessage("Can't use this " + (flag ? "now!" : "yet!"));
			} else {
				gp.player.p.heal();
				showMessage("Your Pokemon were restored to full health!");
			}
			bagState = 0;
		} else if (item == Item.POCKET_PC) {
			if (inGauntlet) {
				boolean flag = gp.player.p.flag[1][2];
				showMessage("Can't use this " + (flag ? "now!" : "yet!"));
			} else {
				bagState = 0;
				NPC_PC pc = new NPC_PC(gp);
				gp.openBox(pc);
			}
		} else if (item == Item.INFINITE_REPEL) {
			gp.player.p.steps = gp.player.p.repel ? 1 : 5000;
			gp.player.p.repel = !gp.player.p.repel;
			String onoff = gp.player.p.repel ? "on!" : "off.";
			showMessage("Repel mode was turned " + onoff);
			bagState = 0;
		} else if (!item.isUsable()) {
			// do nothing
		} else {
			currentItem = item;
			gp.gameState = GamePanel.USE_ITEM_STATE;
		}
	}
	
	public void goBackInBag() {
		if (wasAPressed) {
			gp.gameState = GamePanel.PLAY_STATE;
		} else {
			gp.gameState = GamePanel.MENU_STATE;
			currentItems = gp.player.p.getItems(currentPocket);
			subState = 3;
			bagState = 0;
			commandNum = 0;
			pageNum = 0;
		}
	}

	private void drawItemUsingScreen() {
		int x = gp.tileSize*3;
		int y = 0;
		int width = gp.tileSize*10;
		int height = gp.tileSize;
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		String option1 = currentPocket == Item.BERRY || currentPocket == Item.HELD_ITEM ? "Give " : "Use ";
		g2.drawString(option1 + currentItem.toString(), x, y);
		
		x = gp.tileSize * 12;
		y = gp.tileSize / 4;
		g2.drawImage(currentItem.getImage(), x, y, null);
	}

	public void drawMenuScreen() {
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int x = gp.tileSize * 10;
		int y = gp.tileSize;
		int width = gp.tileSize * 5;
		int height = gp.tileSize * 10;
		
		if (!gp.player.p.flag[0][1]) height -= gp.tileSize;
		if (!gp.player.p.flag[0][2]) height -= gp.tileSize * 2;
		
		if (subState == 0) drawSubWindow(x, y, width, height);
		
		switch(subState) {
		case 0:
			optionsTop(x, y);
			break;
		case 1:
			drawPokedex();
			break;
		case 2:
			showParty();
			break;
		case 3:
			showBag();
			break;
		case 4:
			saveGame();
			break;
		case 5:
			gp.gameState = GamePanel.PLAY_STATE;
			subState = 0;
			gp.player.showPlayer();
			break;
		case 6:
			gp.openMap();
			break;
		case 7:
			drawSummary(null);
		}
	}

	private void saveGame() {
		currentDialogue = "Would you like to save the game?";
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Yes", x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				subState = 0;
				commandNum = 0;
				
				gp.saveGame(gp.player.p);
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 0;
				commandNum = 0;
			}
		}
		
		gp.keyH.wPressed = false;
		
		drawToolTips("OK", null, "Back", "Back");
	}

	public void optionsTop(int x, int y) {
		String text = "Menu";
		
		int textX = (int) (getTextX(text) + gp.tileSize * 4.5);
		int textY = y + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		
		// Pokedex
		if (gp.player.p.flag[0][2]) {
			text = "Pokedex";
			textX = x + gp.tileSize * 2;
			textY += gp.tileSize;
			g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
			g2.drawString(text, textX, textY);
		}
		if (menuNum == 0) {
			if (gp.player.p.flag[0][2]) {
				g2.drawString(">", textX- (25 + gp.tileSize), textY);
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					subState = 1;
					starAmt = gp.player.p.calculateStars();
				}
			} else {
				menuNum++;
			}
		}
		
		// Party
		if (gp.player.p.flag[0][1]) {
			text = "Party";
			textY += gp.tileSize;
			g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
			g2.drawString(text, textX, textY);
		}
		if (menuNum == 1) {
			if (gp.player.p.flag[0][1]) {
				g2.drawString(">", textX- (25 + gp.tileSize), textY);
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.downPressed = false;
					partyNum = 0;
					subState = 2;
				}
			} else {
				menuNum++;
			}
		}
		
		// Bag
		text = "Bag";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 2) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (gp.player.p.flag[7][20] && !gp.player.p.flag[7][21]) {
					gp.gameState = GamePanel.PLAY_STATE;
					showMessage("Can't open that right now!");
					return;
				}
				currentItems = gp.player.p.getItems(currentPocket);
				subState = 3;
			}
		}
		
		// Save
		text = "Save";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 3) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 4;
			}
		}
		
		// Player
		text = "Player";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 4) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 5;
			}
		}
		
		// Map
		if (gp.player.p.flag[0][2]) {
			text = "Map";
			textY += gp.tileSize;
			g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
			g2.drawString(text, textX, textY);
		}
		if (menuNum == 5) {
			if (gp.player.p.flag[0][2]) {
				g2.drawString(">", textX- (25 + gp.tileSize), textY);
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					subState = 6;
				}
			} else {
				menuNum++;
			}
		}
		
		// Back
		text = "Back";
		textY += gp.tileSize * 2;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 6) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				subState = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	private void showParty() {
		drawParty(null);
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			if (partySelectedNum == -1) {
				partySelectedNum = partyNum;
			} else {
				if (partySelectedNum != partyNum) {
					gp.player.p.swap(partySelectedNum, partyNum);
				}
				partySelectedNum = -1;
			}
		}
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			if (partySelectedItem == -1) {
				partySelectedItem = partyNum;
			} else {
				if (partySelectedItem != partyNum) {
					gp.player.p.swapItem(gp.player.p.team[partySelectedItem], gp.player.p.team[partyNum]);
				}
				partySelectedItem = -1;
			}
			
			
		}
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			subState = 7;
		}
		
		drawToolTips("Info", "Swap", "Back", "Item");
	}
	
	private void showBag() {
		int x = gp.tileSize * 6;
		int y = 0;
		int width = (int) (gp.tileSize * 9.25);
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		int startX = x;
		int startY = y + height;
		x += 18;
		y += gp.tileSize / 3;
		for (int i = 0; i < 7; i++) {
			g2.drawImage(bagIcons[i], x, y, null);
			if (currentPocket - 1 == i) {
				g2.drawRoundRect(x, y, gp.tileSize, gp.tileSize, 10, 10);
			}
			x += gp.tileSize * 1.25;
		}
		
		x = startX;
		String pocketName = Item.getPocketName(currentPocket);
		g2.drawString(pocketName, getCenterAlignedTextX(pocketName, x + (width / 2)), (int) (y + gp.tileSize * 1.75));
		
		y = startY;
		height = (int) (gp.tileSize * 9.5);
		
		drawSubWindow(x, y, width, height);
		
		int descX = gp.tileSize / 2;
		int descY = (int) (gp.tileSize * 2.5);
		int descWidth = (int) (gp.tileSize * 5.5);
		int descHeight = gp.tileSize * 8;
		drawSubWindow(descX, descY, descWidth, descHeight);
		int textX = descX + 20;
		int textY = descY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		if (currentItems.size() > 0) {
			if (bagNum[currentPocket - 1] >= currentItems.size()) bagNum[currentPocket - 1]--;
			String desc = currentItems.get(bagNum[currentPocket - 1]).getItem().getDesc();
			for (String line : Item.breakString(desc, 23).split("\n")) {
				g2.drawString(line, textX, textY);
				textY += 32;
			}
		}
		if (tmCheck != null) {
			int selectX = (int) (gp.tileSize * 4.25);
			int selectY = (int) (gp.tileSize * 0.75);
			drawTMCheckWindow(selectX, selectY);
		}
		
		g2.setFont(g2.getFont().deriveFont(32F));
		
		x += gp.tileSize;
		y += gp.tileSize / 2;
		for (int i = bagNum[currentPocket - 1]; i < bagNum[currentPocket - 1] + 9; i++) {
			g2.setColor(Color.WHITE);
			if (i == bagNum[currentPocket - 1]) {
				g2.drawString(">", (x - gp.tileSize / 2) - 2, y + gp.tileSize / 2);
			}
			if (i < currentItems.size()) {
				Bag.Entry current = currentItems.get(i);
				
				g2.drawImage(current.getItem().getImage(), x - 4, y, null);
				y += gp.tileSize / 2;
				String itemString = current.getItem().toString();
				if (currentPocket != Item.TMS && currentPocket != Item.KEY_ITEM) itemString += " x " + current.getCount();
				
				if (currentPocket == Item.KEY_ITEM && current.getItem() == gp.player.p.registeredItem) {
					int starX = x + gp.tileSize * 6;
					int starY = y;
					int starWidth = (gp.tileSize * 2 / 3) - 4;
					g2.setFont(g2.getFont().deriveFont(40F));
				    GradientPaint starPaint = new GradientPaint(
				        starX + 6, starY - 6, new Color(255, 215, 0), // Start point and color
				        starX +  - 10, starY - starWidth + 10, new Color(245, 225, 210) // End point and color
				    );
				    g2.setPaint(starPaint);
				    g2.drawString('\u2605' + "", starX, starY + 2);
				    g2.setFont(g2.getFont().deriveFont(32F));
				}
				
				if (i == selectedBagNum) {
					g2.setColor(Color.BLUE.brighter());
				}
				
				g2.drawString(itemString, x + gp.tileSize / 2, y);
				y += gp.tileSize / 2;
				if (currentPocket == Item.TMS && tmCheck == current.getItem()) {
					int borderX = x - 8;
					int borderY = y - gp.tileSize - 4;
					int borderWidth = getTextWidth(itemString) + 4;
					int borderHeight = (int) (gp.tileSize * 0.75);
					g2.setPaint(new GradientPaint(borderX,borderY,new Color(255,215,0),borderX+borderWidth,borderY+borderWidth,new Color(255,255,210)));
					g2.drawRoundRect(borderX, borderY, borderWidth, borderHeight, 25, 25);
				}
			}
		}
		
		g2.setColor(Color.WHITE);
		// Down Arrow
		if (bagNum[currentPocket - 1] + 9 < currentItems.size()) {
			int x2 = gp.tileSize * 5 + width;
			int y2 = (int) (height + (gp.tileSize * 1.5));
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		}
		// Up Arrow
		if (bagNum[currentPocket - 1] != 0 && bagState != 2) {
			int x2 = gp.tileSize * 5 + width;
			int y2 = gp.tileSize * 3;
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		}
		// Subwindow for item options
		if (!showMoveOptions) {
			if (gp.keyH.aPressed) {
				gp.keyH.aPressed = false;
				if (bagState == 0 && gp.keyH.ctrlPressed) {
					gp.keyH.ctrlPressed = false;
					bagState = 4;
				} else {
					if (selectedBagNum == -1) {
						selectedBagNum = bagNum[currentPocket - 1];
					} else if (selectedBagNum == bagNum[currentPocket - 1]) {
						selectedBagNum = -1;
					} else {
						gp.player.p.moveItem(indexOf(gp.player.p.bag.itemList, currentItems.get(selectedBagNum).getItem().getID()), indexOf(gp.player.p.bag.itemList, currentItems.get(bagNum[currentPocket - 1]).getItem().getID()));
						currentItems = gp.player.p.getItems(currentPocket);
						selectedBagNum = -1;
						if (bagNum[currentPocket - 1] > 0) bagNum[currentPocket - 1]--;
					}
				}
			}
			if (bagState == 0 && !currentItems.isEmpty() && gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				bagState = 1;
			}
			if (bagState == 0 && gp.keyH.dPressed && currentPocket == Item.TMS) {
				gp.keyH.dPressed = false;
				Item tm = currentItems.get(bagNum[currentPocket - 1]).getItem();
				if (tmCheck == null || tmCheck != tm) {
					tmCheck = tm;
				} else {
					tmCheck = null;
				}
			}
		}
		
		if (bagState == 1) {
			drawItemOptions();
		} else if (bagState == 2) {
			drawSellOptions();
		} else if (bagState == 3) {
			drawMoveSummary(gp.tileSize / 2, (int) (gp.tileSize * 6), null, null, null, currentItems.get(bagNum[currentPocket - 1]).getItem().getMove());
		} else if (bagState == 4) {
			drawSortOptions();
		}
		
		if (gp.keyH.shiftPressed && bagState == 0) {
			int toolX = 0;
			int toolY = gp.tileSize * 9;
			int toolWidth = gp.tileSize * 4;
			int toolHeight = (int) (gp.tileSize * 1.5);
			
			drawSubWindow(toolX, toolY, toolWidth, toolHeight);
			
			toolX += gp.tileSize / 2;
			toolY += gp.tileSize;
			
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString("[Ctrl] + [A] Sort", toolX, toolY);
		}		
		
		String dText = (bagState == 0 && currentPocket == Item.TMS) ? "Check" : "Back";
		drawToolTips("OK", "Swap", "Back", dText);
	}
	
	private int indexOf(int[] array, int target) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == target) {
				return i;
			}
		}
		return -1;
	}

	private void drawItemOptions() {
		int x = gp.tileSize * 12;
		int y = (int) (gp.tileSize * 2.5);
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize;
		
		Bag.Entry entryItem = currentItems.get(bagNum[currentPocket - 1]);
		
		String option1 = currentPocket == Item.BERRY || currentPocket == Item.HELD_ITEM ? "Give" : "Use";
		if (!entryItem.getItem().isUsable()) g2.setColor(Color.GRAY);
		g2.drawString(option1, x, y);
		g2.setColor(Color.WHITE);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				currentItem = entryItem.getItem();
				useItem(currentItem, false);
			}
		}
		y += gp.tileSize;
		String option2 = currentPocket == Item.TMS ? "Info" : currentPocket == Item.KEY_ITEM ? "Reg" : "Sell";
		if (!entryItem.getItem().isSellable(entryItem.getCount())) g2.setColor(Color.GRAY);
		g2.drawString(option2, x, y);
		g2.setColor(Color.WHITE);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (!entryItem.getItem().isSellable(entryItem.getCount())) {
					// do nothing
				} else {
					if (currentPocket == Item.KEY_ITEM) {
						if (gp.player.p.registeredItem == entryItem.getItem()) {
							gp.player.p.registeredItem = null;
							showMessage(entryItem.getItem().toString() + " was unregistered for [A].");
						} else {
							gp.player.p.registeredItem = entryItem.getItem();
							showMessage(entryItem.getItem().toString() + " was registered for [A]!");
						}
						commandNum = 0;
						bagState = 0;
					} else {
						bagState = currentPocket == Item.TMS ? 3 : currentPocket == Item.KEY_ITEM ? 1 : 2;
					}
				}
			}
		}
		y += gp.tileSize;
		g2.drawString("Back", x, y);
		if (commandNum == 2) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				bagState = 0;
				commandNum = 0;
			}
		}
	}
	
	private void drawSellOptions() {
		int x = gp.tileSize * 12;
		int y = (int) (gp.tileSize * 1.5);
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize * 1.25;
		y += gp.tileSize * 2;
		g2.drawString(sellAmt + "", x, y);
		
		int y2 = y += gp.tileSize / 4;
		int width2 = gp.tileSize / 2;
		int height2 = gp.tileSize / 2;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		
		y2 = y -= gp.tileSize * 1.5;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		
		x -= gp.tileSize;
		y += gp.tileSize * 2.5;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString("+$" + currentItems.get(bagNum[currentPocket - 1]).getItem().getSell() * sellAmt, x, y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			showMessage("Sold " + sellAmt + " " + currentItems.get(bagNum[currentPocket - 1]).getItem().toString() + " for $" + sellAmt * currentItems.get(bagNum[currentPocket - 1]).getItem().getSell());
			gp.player.p.sellItem(currentItems.get(bagNum[currentPocket - 1]).getItem(), sellAmt);
			currentItems = gp.player.p.getItems(currentPocket);
			bagState = 0;
			commandNum = 0;
			sellAmt = 1;
		}
	}
	
	private void drawSortOptions() {
		SortType[] sortOptions = SortType.getSortOptions(currentPocket);
		int sortChoices = sortOptions.length;
		
		int x = gp.tileSize * 6;
		int y = (int) (gp.tileSize * 2.5);
		int width = (int) (gp.tileSize * 9.25);
		int height = (int) (gp.tileSize * 2 + (sortChoices * (gp.tileSize * 0.75)));
		drawSubWindow(x, y, width, height, 255);
		
		int centerX = x + (width/2);
		y += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(32F));
		
		String title = "Sort Pocket?";
		g2.drawString(title, getCenterAlignedTextX(title, centerX), y);
		
		y += gp.tileSize / 4;
		g2.drawLine(x + gp.tileSize, y, x + width - gp.tileSize, y);
		
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		for (int i = 0; i < sortChoices; i++) {
			SortType option = sortOptions[i];
			g2.drawString(option.getLabel(), getCenterAlignedTextX(option.getLabel(), centerX), y);
			if (i == commandNum) {
				g2.drawString(">", x + gp.tileSize, y - 2);
				g2.drawString("<", x + width - gp.tileSize, y - 2);
			}
			y += gp.tileSize * 0.75;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (sortOptions[commandNum] != SortType.BACK) {
				gp.player.p.bag.sortPocket(currentPocket, sortOptions[commandNum]);
				currentItems = gp.player.p.getItems(currentPocket);
			}
			commandNum = 0;
			bagState = 0;
		}
	}
	
	private void drawCoinState() {
		final int coinWorth = 10;
		currentDialogue = "You have: " + gp.player.p.coins + " coins\nThe exchange rate is: 1 coin for $" + coinWorth;
		
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 12;
		int y = (int) (gp.tileSize * 4.5);
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize * 1.25;
		y += gp.tileSize * 2;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString(sellAmt + "", x, y);
		
		int y2 = y += gp.tileSize / 4;
		int width2 = gp.tileSize / 2;
		int height2 = gp.tileSize / 2;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		
		y2 = y -= gp.tileSize * 1.5;
		g2.fillPolygon(new int[] {x, (x + width2), (x + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		
		x -= gp.tileSize;
		y += gp.tileSize * 2.5;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		final int money = coinWorth * sellAmt;
		
		g2.drawString("+$" + money, x, y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.gameState = GamePanel.PLAY_STATE;
			if (sellAmt > 0) {
				showMessage("Cashed in " + sellAmt + " coins for $" + money + "!");
				gp.player.p.coins -= sellAmt;
				gp.player.p.setMoney(gp.player.p.getMoney() + money);
				sellAmt = 1;
			} else {
				showMessage("Come back soon!");
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			sellAmt++;
			if (sellAmt > gp.player.p.getMaxCoins()) sellAmt = 0;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			sellAmt--;
			if (sellAmt < 0) sellAmt = gp.player.p.getMaxCoins();
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			int max = gp.player.p.getMaxCoins();
			sellAmt -= max > 10 ? 10 : 1;
			if (sellAmt < 0) sellAmt += max;
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			int max = gp.player.p.getMaxCoins();
			sellAmt += max > 10 ? 10 : 1;
			if (sellAmt > max) sellAmt -= max;
		}
		
	}
	
	public void drawTransition() {
		counter++;
		Color base = currentTask != null && currentTask.color != null ? currentTask.color : Color.BLACK;
		g2.setColor(new Color(base.getRed(),base.getGreen(),base.getBlue(),counter*5));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		if (counter == 50) {
			counter = 0;
			int prevMap = gp.currentMap;
			if (currentTask != null && currentTask.type == Task.TELEPORT) {
				gp.gameState = GamePanel.TASK_STATE;
				gp.currentMap = currentTask.counter;
				gp.player.worldX = gp.tileSize * currentTask.start;
				gp.player.worldY = gp.tileSize * currentTask.finish;
				//gp.player.worldY -= gp.tileSize / 4;
				gp.player.spriteNum = 1;
				gp.eHandler.previousEventX = gp.player.worldX;
				gp.eHandler.previousEventY = gp.player.worldY;
				gp.player.p.currentMap = currentTask.counter;
				gp.eHandler.canTouchEvent = !currentTask.wipe;
				currentTask = null;
			} else {				
				gp.currentMap = gp.eHandler.tempMap;
				gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
				gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
				gp.player.worldY -= gp.tileSize / 4;
				gp.eHandler.previousEventX = gp.player.worldX;
				gp.eHandler.previousEventY = gp.player.worldY;
				gp.player.p.currentMap = gp.eHandler.tempMap;
				gp.eHandler.canTouchEvent = !gp.eHandler.tempCooldown;
				
			}
			warpPlayer(prevMap, gp.currentMap);
		}
	}
	
	private void warpPlayer(int prevMap, int newMap) {
		gp.player.spriteNum = 1;
		gp.offsetX = 0;
		gp.offsetY = 0;
		drawFlash = false;
		flashColor = null;
		gp.player.p.surf = false;
		gp.player.p.lavasurf = false;
		
		gp.aSetter.updateNPC(newMap);
		if (prevMap != newMap) gp.aSetter.resetNPCDirection(newMap);
		gp.aSetter.setInteractiveTile(newMap);
		
		String currentMap = PlayerCharacter.currentMapName;
		PMap.getLoc(newMap, (int) Math.round(gp.player.worldX * 1.0 / 48), (int) Math.round(gp.player.worldY * 1.0 / 48));
		Main.window.setTitle(gp.gameTitle + " - " + PlayerCharacter.currentMapName);
		if (!currentMap.equals(PlayerCharacter.currentMapName)) showAreaName();
		
		if (tasks.isEmpty()) {
			gp.gameState = GamePanel.PLAY_STATE;
		} else {
			gp.gameState = GamePanel.TASK_STATE;
		}
	}
	
	private void drawBattleStartTransition(boolean sim) {
		int width = gp.tileSize * 2;
		int height = gp.tileSize * 2;
		counter++;
		Graphics2D transitionGraphics = transitionBuffer.createGraphics();
		transitionGraphics.setColor(Color.BLACK);
		transitionGraphics.fillRect(btX, btY, width, height);
	    transitionGraphics.dispose();

	    g2.drawImage(transitionBuffer, 0, 0, null);

		btX += width;
		if (btX >= gp.screenWidth) {
			btX = 0;
			btY += height;
			if (btY >= gp.screenHeight) {
				btY = 0;
				counter = 0;
				showArea = false;
				gp.battleUI.commandNum = 0;
				if (sim) {
					gp.simBattleUI.subState = SimBattleUI.STARTING_STATE;
					gp.gameState = GamePanel.SIM_BATTLE_STATE;
				} else {
					if (!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) {
						gp.battleUI.setupBalls();
					}
					gp.battleUI.subState = BattleUI.STARTING_STATE;
					gp.gameState = GamePanel.BATTLE_STATE;
				}
			}
		}
	}
	
	public void drawNurseScreen() {
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Yes", x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				showMessage("Your Pokemon were restored\nto full health!");
				gp.player.p.heal();
				subState = 0;
				commandNum = 0;
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				showMessage("Come again soon!");
				subState = 0;
				commandNum = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
		
		gp.keyH.wPressed = false;
	}
	
	public void drawShopScreen() {
		switch(subState) {
		case 0: shopSelect(); break;
		case 1: shopBuy(); break;
		}
		gp.keyH.wPressed = false;
	}
	
	public void shopSelect() {
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Buy", x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 1;
			}
		}
		y += gp.tileSize;
		g2.drawString("Exit", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				showMessage("Come again soon!");
				subState = 0;
				commandNum = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	public void shopBuy() {
		drawInventory(npc.inventory, ITEMS);
	}
	
	private void drawInventory(ArrayList<?> inventory, int type) {
		int x = (int) (gp.tileSize * 1.5);
		int y = gp.tileSize;
		int width = (int) (gp.tileSize * 12.75) + 5;
		int height = gp.tileSize * 6;
		
		boolean items = type == ITEMS;
		boolean prize = type >= 1 && type <= 3;
		
		drawSubWindow(x, y, width, height);
		
		final int slotXstart = x + 20;
		final int slotYstart = y + 20;
		int slotX = slotXstart;
		int slotY = slotYstart;
		int slotSize = gp.tileSize + 5;
		
		for (int i = 0; i < inventory.size(); i++) {
			@SuppressWarnings("unchecked")
			Item item = items ? (Item) inventory.get(i) : prize ? ((Pair<Item, Integer>) inventory.get(i)).getFirst() : null;
			
			g2.drawImage(item.getImage2(), slotX, slotY, null);
			
			slotX += slotSize;
			
			if ((i + 1) % MAX_SHOP_COL == 0) {
				slotX = slotXstart;
				slotY += slotSize;
			}
		}
		
		int cursorX = slotXstart + (slotSize * slotCol);
		int cursorY = slotYstart + (slotSize * slotRow);
		int cursorWidth = gp.tileSize;
		int cursorHeight = gp.tileSize;
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
		
		int dFrameX = x;
		int dFrameY = y + height;
		int dFrameWidth = width;
		int dFrameHeight = (int) (gp.tileSize * 3.5);
		
		int textX = dFrameX + 20;
		int textY = (int) (dFrameY + gp.tileSize * 0.8);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int itemIndex = getItemIndexOnSlot(MAX_SHOP_COL);
		
		if (itemIndex < inventory.size()) {
			@SuppressWarnings("unchecked")
			Item current = items ? (Item) inventory.get(itemIndex) : prize ? ((Pair<Item, Integer>) inventory.get(itemIndex)).getFirst() : null;
			drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
			g2.drawString(current.toString(), textX, textY);
			
			String price = "";
			if (items) {
				price = "$" + current.getCost();
			} else if (prize) {
				@SuppressWarnings("unchecked")
				Pair<Item, Integer> pair = (Pair<Item, Integer>) inventory.get(itemIndex);
				String currency = type == 1 ? " Coins" : " Total Wins";
				price = pair.getSecond() + currency;
			}
			g2.drawString(price, getRightAlignedTextX(price, dFrameX + dFrameWidth - gp.tileSize / 2), textY);
			
			
			int amtX = dFrameX + dFrameWidth - gp.tileSize * 4;
			int amtY = dFrameY + dFrameHeight;
			int amtWidth = gp.tileSize * 4;
			int amtHeight = gp.tileSize;
			
			drawSubWindow(amtX, amtY, amtWidth, amtHeight);
			int amtTextX = amtX + 20;
			int amtTextY = (int) (amtY + gp.tileSize * 0.75);
			g2.drawString("You have: " + gp.player.p.bag.count[current.getID()], amtTextX, amtTextY);
			
			textY += 38;
			g2.setFont(g2.getFont().deriveFont(24F));
			String desc = current.isTM() ? current.getMove().getDescription() : current.getDesc();
			
			for (String line : Item.breakString(desc, 58).split("\n")) {
				g2.drawString(line, textX, textY);
				textY += 32;
			}
			
			if (gp.keyH.wPressed) {
				Item i = items ? current : null;
				@SuppressWarnings("unchecked")
				Pair<Item, Integer> p = prize ? (Pair<Item, Integer>) inventory.get(itemIndex) : null;
				gp.player.p.purchaseItem(this, i, p, type, npc);
			}
		}
		
		if (items) {
			int moneyX = x + width - gp.tileSize * 3;
			int moneyY = y - gp.tileSize;
			int moneyWidth = gp.tileSize * 3;
			int moneyHeight = gp.tileSize;
			
			drawSubWindow(moneyX, moneyY, moneyWidth, moneyHeight);
			textX = moneyX + 20;
			textY = (int) (moneyY + gp.tileSize * 0.75);
			g2.setFont(g2.getFont().deriveFont(32F));
			g2.drawString("$" + gp.player.p.getMoney(), textX, textY);
		}
		
		drawToolTips("Buy", prize ? "Prev" : null, "Back", prize ? "Next" : "Back");
	}

	private int getItemIndexOnSlot(int maxCol) {
		int itemIndex = slotCol + (slotRow*maxCol);
		return itemIndex;
	}
	
	private void drawPrizeScreen(NPC_Prize_Shop ps) {
		if (subState <= 0) {
			shopSelect();
		} else if (subState < 3) {
			drawInventory(ps.getInventory(subState), subState);
			drawPrizeHeader(gp.player.p);
		} else {
			drawPokemonPrize(ps.prizePokemon);
			drawPrizeHeader(gp.player.p);
		}
		gp.keyH.wPressed = false;
	}
	
	private void drawPokemonPrize(ArrayList<Pair<Pokemon, Integer>> inventory) {
		int x = gp.tileSize * 2;
		int y = gp.tileSize;
		int width = gp.tileSize * 12;
		int height = gp.tileSize * 6;
		
		drawSubWindow(x, y, width, height);
		
		final int slotXstart = x + 20;
		final int slotYstart = y + 20;
		int slotX = slotXstart;
		int slotY = slotYstart;
		
		int slotSize = 88;
		
		for (int i = 0; i < inventory.size(); i++) {
			g2.drawImage(inventory.get(i).getFirst().getSprite(), slotX, slotY, null);
			
			slotX += slotSize;
			
			if ((i + 1) % 6 == 0) {
				slotX = slotXstart;
				slotY += slotSize;
			}
		}
		
		int cursorX = slotXstart + (slotSize * slotCol);
		int cursorY = slotYstart + (slotSize * slotRow);
		int cursorWidth = 82;
		int cursorHeight = 82;
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
		
		int dFrameX = x;
		int dFrameY = y + height;
		int dFrameWidth = width;
		int dFrameHeight = (int) (gp.tileSize * 3.5);
		
		int textX = dFrameX + 20;
		int textY = (int) (dFrameY + gp.tileSize * 0.8);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int itemIndex = getItemIndexOnSlot(6);
		
		if (itemIndex < inventory.size()) {
			Pair<Pokemon, Integer> current = inventory.get(itemIndex);
			Pokemon p = current.getFirst();
			drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
			g2.drawString(Pokemon.getFormattedDexNo(p.id) + " - " + p.name(), textX, textY);
			
			String price = current.getSecond() + " win streak";
			g2.drawString(price, getRightAlignedTextX(price, dFrameX + dFrameWidth - gp.tileSize / 2), textY);
			
			textY += gp.tileSize / 2;
			g2.drawImage(p.type1.getImage2(), textX, textY, null);
			textX += gp.tileSize * 1.5;
			if (p.type2 != null) {
				g2.drawImage(p.type2.getImage2(), textX, textY, null);
			}
			
			if (gp.keyH.wPressed) {
				if (gp.player.p.winStreak >= current.getSecond()) {
					gp.setTaskState();
					setNicknaming(true);
					gp.player.p.catchPokemon(p);
					
					checkTasks = true;
					subState = 0;
					commandNum = 0;
					slotRow = 0;
					slotCol = 0;
					
					NPC_Prize_Shop np = (NPC_Prize_Shop) npc;
					np.prizePokemon.remove(itemIndex);
					np.prizePokemon.add(itemIndex, new Pair<Pokemon, Integer>(new Pokemon(p.id, np.getPrizeLevel(), true, false), current.getSecond()));
					gp.player.p.winStreak -= current.getSecond();
				} else {
					showMessage("Not enough wins in a row!");
				}
			}
		}
		drawToolTips("Buy", "Prev", "Back", "Next");
	}

	private void drawPrizeHeader(Player p) {
		int startX = gp.tileSize * 3;
		int startY = 0;
		int width = gp.tileSize * 3;
		int height = gp.tileSize;
		int gap = gp.tileSize / 2;
		
		String labels[] = new String[] {" Coins", " Games Won", " Win Streak"};
		
		int x = startX;
		int y = startY;
		
		for (int i = 1; i <= 3; i++) {
			drawSubWindow(x, y, width, height);
			int textX = x + 16;
			int textY = (int) (y + gp.tileSize * 0.75);
			
			int amt = 0;
			switch(i) {
			case 1:
				amt = p.coins;
				break;
			case 2:
				amt = p.gamesWon;
				break;
			case 3:
				amt = p.winStreak;
				break;
			}
			String label = amt + labels[i - 1];
			g2.setFont(g2.getFont().deriveFont(32F));
			g2.setFont(g2.getFont().deriveFont(getFontSize(label, width)));
			g2.drawString(label, textX, textY);
			
			if (i == subState) {
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(5));
				g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
			}
			
			x += width + gap;
		}
	}
	
	private void drawMushroom(Bag bag) {
		int x = (int) (gp.tileSize * 5.5);
		int y = gp.tileSize * 5;
		int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 6.25);
		
		int seed = gp.aSetter.generateFlagSeed(gp.player.p.getID(), currentTask.e.worldX / gp.tileSize, currentTask.e.worldY / gp.tileSize, gp.currentMap, gp.player.p.flag);
		Random random = new Random(seed);
		
		currentDialogue = currentTask.message;
		drawDialogueScreen(true);
		drawNameLabel(true);
		drawSubWindow(x, y, width, height);
		
		boolean hasTM = bag.contains(Item.TINY_MUSHROOM);
		boolean hasBM = bag.contains(Item.BIG_MUSHROOM);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		
		int buttonWidth = gp.tileSize * 4;
		int buttonHeight = gp.tileSize * 2;
		
		Item item = Item.TINY_MUSHROOM;
		
		Color color = hasTM ? Color.white : Color.gray;
		g2.setColor(color);
		g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
		
		if (commandNum == 0) {
			g2.setColor(Color.RED);
			g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
		}
		
		int startX = x;
		int startY = y;
		
		x += gp.tileSize / 4;
		g2.drawImage(item.getImage2(), x, y, null);
		
		x += gp.tileSize;
		y += gp.tileSize * 0.75;
		g2.setColor(Color.BLACK);
		String text = "x " + bag.count[item.getID()];
		g2.drawString(text, x, y);
		
		y += gp.tileSize * 0.75;
		int price1 = randomPrice(random, 10, 1000);
		String amt = "+$" + price1;
		g2.drawString(amt, getCenterAlignedTextX(amt, x + buttonWidth / 2), y);
		
		item = Item.BIG_MUSHROOM;
		x = startX;
		y = startY;
		
		y += buttonHeight + gp.tileSize / 2;
		
		color = hasBM ? Color.white : Color.gray;
		g2.setColor(color);
		g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
		
		if (commandNum == 1) {
			g2.setColor(Color.RED);
			g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
		}
		
		x += gp.tileSize / 4;
		g2.drawImage(item.getImage2(), x, y, null);
		
		x += gp.tileSize;
		y += gp.tileSize * 0.75;
		g2.setColor(Color.BLACK);
		text = "x " + bag.count[item.getID()];
		g2.drawString(text, x, y);
		
		y += gp.tileSize * 0.75;
		int price2 = randomPrice(random, 50, 4000);
		amt = "+$" + price2;
		g2.drawString(amt, getCenterAlignedTextX(amt, x + buttonWidth / 2), y);
		
		int moneyX = gp.tileSize * 13;
		int moneyY = gp.tileSize * 11;
		int moneyWidth = gp.tileSize * 3;
		int moneyHeight = gp.tileSize;
		
		drawSubWindow(moneyX, moneyY, moneyWidth, moneyHeight);
		int textX = moneyX + 20;
		int textY = (int) (moneyY + gp.tileSize * 0.75);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString("$" + gp.player.p.getMoney(), textX, textY);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (commandNum == 0) {
				if (hasTM) {
					bag.remove(Item.TINY_MUSHROOM);
					gp.player.p.setMoney(gp.player.p.getMoney() + price1);
				} else {
					Task.addTask(Task.DIALOGUE, currentTask.e, "You don't have any Tiny Mushrooms for your boi?!");
					Task.addTask(Task.MUSHROOM, currentTask.e, currentTask.message);
					currentTask = null;
				}
			} else if (commandNum == 1) {
				if (hasBM) {
					bag.remove(Item.BIG_MUSHROOM);
					gp.player.p.setMoney(gp.player.p.getMoney() + price2);
				} else {
					Task.addTask(Task.DIALOGUE, currentTask.e, "I NEEEED a BIG SHROOM!!");
					Task.addTask(Task.MUSHROOM, currentTask.e, currentTask.message);
					currentTask = null;
				}
			}
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			Task.addTask(Task.DIALOGUE, currentTask.e, "You BETTER go fetch Daddy some kush!");
			commandNum = 0;
			currentTask = null;
		}
		
		if (gp.keyH.upPressed || gp.keyH.downPressed) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			commandNum = 1 - commandNum;
		}
		drawToolTips("Sell", null, "Back", null);
	}
	
	public void drawStarShopScreen() {
		switch(subState) {
		case 0: starShopSelect(); break;
		case 1: drawStarShop(npc); break;
		case 2: drawStarRepair(new Item[] {Item.ABSORB_BULB, Item.CELL_BATTERY, Item.LUMINOUS_MOSS, Item.SNOWBALL}); break;
		}
		gp.keyH.wPressed = false;
	}
	
	public void starShopSelect() {
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Trade", x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 1;
				remindNum = 0;
			}
		}
		y += gp.tileSize;
		g2.drawString("Repair", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 2;
				remindNum = 0;
			}
		}
		y += gp.tileSize;
		g2.drawString("Exit", x, y);
		if (commandNum == 2) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				showMessage("Come back when you find more Star Pieces!");
				subState = 0;
				commandNum = 0;
				remindNum = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	private void drawStarShop(Entity e) {
		int x = gp.tileSize * 3;
		int y = (int) (gp.tileSize * 1.5);
		int width = gp.tileSize * 10;
		int height = (int) (gp.tileSize * 5.5);
		
		int maxCol = 6;
		
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 4;
		y += gp.tileSize / 2;
		
		int itemSize = gp.tileSize + 5;
		int gap = 20;
		
		int startX = x;
		
		for (int i = 0; i < e.inventory.size(); i++) {
			x += gap;
			int itemX = x;
			int itemY = y;
			Item item = e.inventory.get(i);
			
			if (remindNum == i) {
				g2.setColor(Color.RED);
				g2.drawRoundRect(x - 8, y - 8, itemSize + 16, itemSize + 16, 20, 20);
			}
			
			g2.drawImage(item.getImage2(), x, y, null);
			
			x += gp.tileSize * 0.75;
			y += gp.tileSize * 0.75;
			
			g2.drawImage(Item.STAR_PIECE.getImage(), x - 4, y, null);
			x += gp.tileSize * 0.4;
			y += 18;
			
			g2.setColor(Color.WHITE);
			g2.setFont(g2.getFont().deriveFont(16F));
			g2.drawString("" + item.getCost(), x - 4, y);
			
			x = itemX + itemSize;
			y = itemY;
			if ((i + 1) % maxCol == 0) {
				x = startX;
				y += itemSize + gap;
			}
		}
		
		int moneyX = gp.tileSize * 3;
		int moneyY = gp.tileSize / 2;
		int moneyWidth = gp.tileSize * 3;
		int moneyHeight = gp.tileSize;
		
		drawSubWindow(moneyX, moneyY, moneyWidth, moneyHeight);
		moneyX += 8;
		g2.drawImage(Item.STAR_PIECE.getImage2(), moneyX, moneyY, null);
		moneyX += gp.tileSize * 0.85;
		moneyY += gp.tileSize * 0.75;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString("" + gp.player.p.bag.count[Item.STAR_PIECE.getID()], moneyX, moneyY);
		
		int dFrameX = gp.tileSize * 2;
		int dFrameY = (int) (gp.tileSize * 1.5 + height);
		int dFrameWidth = gp.tileSize * 12;
		int dFrameHeight = (int) (gp.tileSize * 3.5);
		
		int textX = dFrameX + 20;
		int textY = (int) (dFrameY + gp.tileSize * 0.8);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		Item current = e.inventory.get(remindNum);
		drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
		g2.drawString(current.toString(), textX, textY);
		
		int priceX = getRightAlignedTextX("$     ", dFrameX + dFrameWidth - gp.tileSize / 2);
		g2.drawImage(Item.STAR_PIECE.getImage2(), priceX, (int) (textY - gp.tileSize * 0.75), null);
		String price = String.valueOf(current.getCost());
		priceX += gp.tileSize * 0.9;
		g2.drawString(price, priceX, textY);
			
		int amtX = dFrameX + dFrameWidth - gp.tileSize * 4;
		int amtY = dFrameY + dFrameHeight;
		int amtWidth = gp.tileSize * 4;
		int amtHeight = gp.tileSize;
			
		drawSubWindow(amtX, amtY, amtWidth, amtHeight);
		int amtTextX = amtX + 20;
		int amtTextY = (int) (amtY + gp.tileSize * 0.75);
		g2.drawString("You have: " + gp.player.p.bag.count[current.getID()], amtTextX, amtTextY);
			
		textY += 38;
		g2.setFont(g2.getFont().deriveFont(24F));
		String desc = current.isTM() ? current.getMove().getDescription() : current.getDesc();
		
		for (String line : Item.breakString(desc, 58).split("\n")) {
			g2.drawString(line, textX, textY);
			textY += 32;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (gp.player.p.bag.count[Item.STAR_PIECE.getID()] >= current.getCost()) {
				showMessage("Thank you! Here's 1 " + current.toString() + "!");
				gp.player.p.bag.add(current);
				gp.player.p.bag.remove(Item.STAR_PIECE, current.getCost());
			} else {
				showMessage("You don't have enough Star Pieces!");
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			if (remindNum - maxCol >= 0) {
				remindNum -= maxCol;
			}
		}
		if (gp.keyH.downPressed) {
			if (remindNum + maxCol < e.inventory.size()) {
				remindNum += maxCol;
			}
			gp.keyH.downPressed = false;
		}
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			if (remindNum % maxCol != 0) {
				remindNum--;
			}
		}
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			if (remindNum % maxCol + 1 != maxCol && remindNum + 1 < e.inventory.size()) {
				remindNum++;
			}
		}
		drawToolTips("Buy", null, "Back", "Back");
	}
	
	private void drawStarRepair(Item[] inventory) {
		int x = gp.tileSize * 4;
		int y = (int) (gp.tileSize * 1.5);
		int width = gp.tileSize * 8;
		int height = (int) (gp.tileSize * 5.5);
		
		drawSubWindow(x, y, width, height);
		
		y += gp.tileSize / 2;
		
		int itemSize = gp.tileSize + 5;
		int gap = 32;
		
		for (int i = 0; i < inventory.length; i++) {
			x += gap;
			int itemX = x;
			int itemY = y;
			Item item = inventory[i];
			
			if (remindNum == i) {
				g2.setColor(Color.RED);
				g2.drawRoundRect(x - 12, y - 12, itemSize + 24, itemSize + 24, 20, 20);
			}
			
			boolean afford = gp.player.p.bag.contains(item.getID() + 45);
			g2.drawImage(afford ? item.getImage2() : item.getGreyImage2(), x, y, null);
			
			x -= 4;
			y += gp.tileSize * 0.75;
			Item costItem = Item.getItem(item.getID() + 45);
			g2.drawImage(afford ? costItem.getImage() : costItem.getGreyImage(), x, y, gp);
			x += 24;
			y += 18;
			g2.setColor(Color.WHITE);
			g2.setFont(g2.getFont().deriveFont(16F));
			g2.drawString("" + 1, x - 4, y);
			
			x += 16;
			y -= 18;
			
			g2.drawImage(Item.STAR_PIECE.getImage(), x - 4, y, null);
			x += gp.tileSize * 0.4;
			y += 18;
			
			g2.setColor(Color.WHITE);
			g2.setFont(g2.getFont().deriveFont(16F));
			g2.drawString("" + (item.getCost() - 1), x - 4, y);
			
			x = itemX + itemSize;
			y = itemY;
		}
		
		int moneyX = gp.tileSize * 4;
		int moneyY = gp.tileSize / 2;
		int moneyWidth = gp.tileSize * 3;
		int moneyHeight = gp.tileSize;
		
		drawSubWindow(moneyX, moneyY, moneyWidth, moneyHeight);
		moneyX += 8;
		g2.drawImage(Item.STAR_PIECE.getImage2(), moneyX, moneyY, null);
		moneyX += gp.tileSize * 0.85;
		moneyY += gp.tileSize * 0.75;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString("" + gp.player.p.bag.count[Item.STAR_PIECE.getID()], moneyX, moneyY);
		
		Item current = inventory[remindNum];
		int cost = current.getCost() - 1;
		Item costItem = Item.getItem(current.getID() + 45);
		
		int costMoneyX = gp.tileSize * 9;
		int costMoneyY = gp.tileSize / 4;
		int costMoneyWidth = moneyWidth;
		int costMoneyHeight = (int) (gp.tileSize * 1.25);
		
		drawSubWindow(costMoneyX, costMoneyY, costMoneyWidth, costMoneyHeight);
		costMoneyX += gp.tileSize * 2.5;
		costMoneyY += gp.tileSize * 0.9;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		String costItemAmt = String.valueOf(gp.player.p.bag.count[costItem.getID()]);
		g2.drawString(costItemAmt, getRightAlignedTextX(costItemAmt, costMoneyX), costMoneyY);
		costMoneyX -= gp.tileSize * 2.25;
		costMoneyY -= gp.tileSize * 0.75;
		g2.drawImage(costItem.getImage2(), costMoneyX, costMoneyY, null);
		
		int dFrameX = gp.tileSize * 2;
		int dFrameY = (int) (gp.tileSize * 1.5 + height);
		int dFrameWidth = gp.tileSize * 12;
		int dFrameHeight = (int) (gp.tileSize * 3.5);
		
		int textX = dFrameX + 20;
		int textY = (int) (dFrameY + gp.tileSize * 0.8);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
		g2.drawString(current.toString(), textX, textY);
		
		int priceX = getRightAlignedTextX("$     ", dFrameX + dFrameWidth - gp.tileSize / 2);
		textY += gp.tileSize / 4;
		g2.drawImage(Item.STAR_PIECE.getImage2(), priceX, (int) (textY - gp.tileSize * 0.75), null);
		String price = String.valueOf(cost);
		priceX += gp.tileSize * 0.9;
		g2.drawString(price, priceX, textY);
		
		priceX -= gp.tileSize * 2;
		g2.drawImage(costItem.getImage2(), priceX, (int) (textY - gp.tileSize * 0.75), null);
		price = String.valueOf(1);
		priceX += gp.tileSize * 0.9;
		g2.drawString(price, priceX, textY);
		
		int amtX = dFrameX + dFrameWidth - gp.tileSize * 4;
		int amtY = dFrameY + dFrameHeight;
		int amtWidth = gp.tileSize * 4;
		int amtHeight = gp.tileSize;
			
		drawSubWindow(amtX, amtY, amtWidth, amtHeight);
		int amtTextX = amtX + 20;
		int amtTextY = (int) (amtY + gp.tileSize * 0.75);
		g2.drawString("You have: " + gp.player.p.bag.count[current.getID()], amtTextX, amtTextY);
			
		textY += 38;
		g2.setFont(g2.getFont().deriveFont(24F));
		String desc = current.isTM() ? current.getMove().getDescription() : current.getDesc();
		
		for (String line : Item.breakString(desc, 58).split("\n")) {
			g2.drawString(line, textX, textY);
			textY += 32;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (gp.player.p.bag.contains(costItem)) {
				if (gp.player.p.bag.count[Item.STAR_PIECE.getID()] >= cost) {
					showMessage(Item.breakString("Thank you! Here's your " + costItem.toString() + " repaired into a " + current.toString() + "!", 42));
					gp.player.p.bag.add(current);
					gp.player.p.bag.remove(Item.STAR_PIECE, cost);
					gp.player.p.bag.remove(costItem);
				} else {
					showMessage("You don't have enough Star Pieces to repair your " + costItem.toString() + "!");
				}
			} else {
				showMessage("You don't have a " + costItem.toString() + " to repair!");
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
		}
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			if (remindNum != 0) {
				remindNum--;
			}
		}
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			if (remindNum + 1 < inventory.length) {
				remindNum++;
			}
		}
		drawToolTips("Repair", null, "Back", "Back");
	}
	
	private int randomPrice(Random random, int min, int max) {
		double ratio = Math.pow(random.nextDouble(), 2);
		return min + (int) ((max - min) * ratio);
	}

	public void drawRepelScreen() {
		currentDialogue = "Repel's effect wore off!\nWould you like to use another?";
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		drawSubWindow(x, y, width, height);
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Yes", x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				gp.player.useRepel();
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				gp.gameState = GamePanel.PLAY_STATE;
				commandNum = 0;
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}

	public void showAreaName() {
		showArea = true;
		areaCounter = 0;
	}
	
	private void drawAreaName() {
		areaCounter++;
		int x = 0;
		int y = 0;
		int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 1.5);
		drawSubWindow(x, y, width, height);
		x += gp.tileSize / 2;
		y += gp.tileSize;
		String message = PlayerCharacter.currentMapName;
		if (message.length() < 15) {
			g2.setFont(g2.getFont().deriveFont(32F));
		} else if (message.length() < 18) {
			g2.setFont(g2.getFont().deriveFont(28F));
		} else {
			g2.setFont(g2.getFont().deriveFont(24F));
		}
		
		g2.drawString(PlayerCharacter.currentMapName, x, y);
		
		if (areaCounter >= 100) {
			showArea = false;
			areaCounter = 0;
		}
	}
	
	private void drawDexNav() {		
		int x = gp.tileSize;
		int y = 0;
		int winX = x;
		int winY = y;
		int width = gp.tileSize * 14;
		int spriteWidth = 80;
		int spriteHeight = 80;
		
		String[] labels = new String[] {"Standard", "Fishing", "Surfing", "Lava Surf"};
		int index = 0;
		int totalHeight = 0;
		
		for (ArrayList<Encounter> es : encounters) {
			int size = es.size();
			if (size > 0) {
				int height = ((((size - 1) / 8) + 1) * 2) * gp.tileSize;
				height += gp.tileSize / 2;
				totalHeight += height;
			}
		}
		
		if (totalHeight > 0) {
			y = (gp.screenHeight / 2) - (totalHeight / 2);
		} else {
			int height = gp.tileSize * 4;
			drawSubWindow(winX, gp.screenHeight / 2 - height / 2, width, height);
			String message = "NO ENCOUNTERS AVAILABLE";
			g2.setFont(g2.getFont().deriveFont(48F));
			g2.drawString(message, getCenterAlignedTextX(message, gp.screenWidth / 2), gp.screenHeight / 2);
		}
		
		for (ArrayList<Encounter> es : encounters) {
			boolean all = true;
			int size = es.size();
			if (size == 0) {
				index++;
				continue;
			}
			int height = ((((size - 1) / 8) + 1) * 2) * gp.tileSize;
			height += gp.tileSize / 2;
			drawSubWindow(winX, y, width, height);
			String label = labels[index];
			
			int labelX = winX + width / 2;
			y += 30;
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString(label, getCenterAlignedTextX(label, labelX), y);
			
			x = winX + gp.tileSize / 3;
			winY = y;
			
			for (int i = 0; i < es.size(); i++) {
				Encounter e = es.get(i);
				int id = e.getId();
				Pokemon p = new Pokemon(id, 5, false, false);
				if (gp.player.p.pokedex[id] == 2) {
					g2.drawImage(p.getSprite(), x, y, null);
				} else {
					all = false;
					g2.drawImage(getSilhouette(p), x, y, null);
				}
				g2.setFont(g2.getFont().deriveFont(16F));
				g2.drawString(String.format("%.0f%%", e.getEncounterChance() * 100), x + 60, y + 75);
				if ((i + 1) % 8 == 0) {
					x = winX + gp.tileSize / 3;
					y += spriteHeight;
				} else {
					x += spriteWidth;
				}
			}
			if (all) {
				int windowY = winY - 30;
				g2.setPaint(new GradientPaint(winX+5,windowY+5,new Color(255,215,0),winX+5+width-10,windowY+5+height-10,new Color(255,255,210)));
				g2.drawRoundRect(winX+5, windowY+5, width-10, height-10, 25, 25);
				int starX = winX + width - gp.tileSize;
				int starY = windowY + gp.tileSize;
				int starWidth = (gp.tileSize * 2 / 3) - 4;
				g2.setFont(g2.getFont().deriveFont(40F));
			    GradientPaint starPaint = new GradientPaint(
			        starX + 6, starY - 6, new Color(255, 215, 0), // Start point and color
			        starX +  - 10, starY - starWidth + 10, new Color(245, 225, 210) // End point and color
			    );
			    g2.setPaint(starPaint);
			    g2.drawString('\u2605' + "", starX, starY);
			}
			
			y = winY + height - gp.tileSize * 2 / 3;
			x = winX;
			index++;
		}
		
		drawToolTips(null, null, "Back", null);
	}
	
	private BufferedImage getSilhouette(Pokemon p) {
		if (silhouettes[p.id - 1] != null) return silhouettes[p.id - 1];
		BufferedImage sprite = p.getSprite();
		int width = sprite.getWidth();
	    int height = sprite.getHeight();
	    BufferedImage silhouette = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    
	    // Define the light grey color
	    int lightGrey = new Color(211, 211, 211).getRGB(); // Light grey color with RGB (211, 211, 211)
	    
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
	            int pixel = sprite.getRGB(x, y);
	            // Check if the pixel is transparent
	            if ((pixel >> 24) == 0x00) {
	                // Copy the transparent pixel to the silhouette
	                silhouette.setRGB(x, y, pixel);
	            } else {
	                // Set the non-transparent pixel to light grey
	                silhouette.setRGB(x, y, lightGrey);
	            }
	        }
	    }
	    
	    return silhouette;
	}
	
	private void drawLetterState() {
		int x = gp.tileSize * 2;
		int y = gp.tileSize / 2;
		int width = gp.screenWidth - x * 2;
		int height = gp.screenHeight - y * 2;
		
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRoundRect(x - 4, y - 4, width + 8, height + 8, 15, 15);
		
		g2.setColor(new Color(245, 234, 221));
		g2.fillRoundRect(x, y, width, height, 15, 15);
		
		int lineX = x + gp.tileSize / 2;
		int lineY = y + gp.tileSize;
		int lineWidth = width - gp.tileSize;
		int lineHeight = (int) (gp.tileSize * 0.75);
		g2.setColor(new Color(63, 67, 114, 150));
		
		for (int i = 0; i < 13; i++) {
			g2.drawLine(lineX, lineY, lineX + lineWidth, lineY);
			lineY += lineHeight;
		}
		
		int textX = x + (int) (gp.tileSize * 0.75);
		int textY = (int) (y + gp.tileSize * 0.9);
		
		g2.setFont(currentFont);
		g2.setColor(Color.BLACK);
		
		if (currentItem == Item.LETTER && pageNum == 0) {
			g2.setFont(g2.getFont().deriveFont(24F));
			String date = "9 /29/ 41";
			int dateX = (int) (textX + width * 0.9);
			g2.drawString(date, getRightAlignedTextX(date, dateX), textY - gp.tileSize / 4);
		}
		
		g2.setFont(g2.getFont().deriveFont(30F));		
		String[] message = letter[currentLetter][pageNum];
		
		for (String s : message) {
			g2.drawString(s, textX, textY);
			textY += lineHeight;
		}
		g2.setFont(marumonica);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			pageNum++;
			if (pageNum > letter.length - 1) {
				pageNum = 0;
				gp.gameState = GamePanel.MENU_STATE;
			}
		}
		
		drawToolTips("Next", null, "Back", null);
	}
	
	private String loadText(String path) {
		String str = "";
		StringBuffer buf = new StringBuffer();
		InputStream is = getClass().getResourceAsStream(path);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is != null) {
				while ((str = reader.readLine()) != null) {
					buf.append(str + "\n");
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	private String[] breakMessage(String[] message, int length) {
		ArrayList<String> result = new ArrayList<>();
		for (String s : message) {
			String[] m = Item.breakString(s, length).split("\n");
			for (String s1 : m) {
				result.add(s1);
			}
		}
		return result.toArray(new String[1]);
	}
	
	private String[][] splitMessage(String[] message) {
		int numPages = (int) Math.ceil((double) message.length / maxLine);
		
		String[][] result = new String[numPages][];

		for (int i = 0; i < numPages; i++) {
			int start = i * maxLine;
			int finish = Math.min(start + maxLine, message.length);
			
			String[] page = new String[finish - start];
			for (int j = 0; j < page.length; j++) {
				page[j] = message[start + j];
			}
			result[i] = page;
		}
		
		return result;
	}

	private void drawStarterState() {
		int x = gp.tileSize;
		int y = gp.tileSize * 2;
		int width = gp.tileSize * 4;
		int height = gp.tileSize * 8;
		
		int gap = gp.tileSize;
		
		Pokemon[] starters = new Pokemon[] {new Pokemon(1, 5, true, false), new Pokemon(4, 5, true, false), new Pokemon(7, 5, true, false)};
		
		for (int i = 0; i < 3; i++) {
			int startX = x;
			int startY = y;
			Pokemon p = starters[i];
			
			drawSubWindow(x, y, width, height, 240);
			
			if (starter == i) {
				g2.setColor(p.type1.getColor());
				g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
			}
			
			x += gp.tileSize / 3;
			y += gp.tileSize / 3;
			g2.drawImage(p.type1.getImage2(), x, y, null);
			
			x += width - gp.tileSize * 2 / 3;
			y += gp.tileSize / 3;
			g2.setFont(g2.getFont().deriveFont(20F));
			String no = Pokemon.getFormattedDexNo(p.getDexNo());
			g2.drawString(no, getRightAlignedTextX(no, x), y);
			
			x = startX;
			y = startY;
			
			x += gp.tileSize / 2;
			
			g2.drawImage(p.getFrontSprite(), x, y, null);
			
			y += gp.tileSize * 4;
			g2.setFont(g2.getFont().deriveFont(24F));
			String label = p.getName() + " lv 5";
			g2.drawString(label, getCenterAlignedTextX(label, startX + width / 2), y);
			
			y += gp.tileSize * 0.75;
			g2.setFont(g2.getFont().deriveFont(20F));
			label = "The Pokemon Pokemon";
			g2.drawString(label, getCenterAlignedTextX(label, startX + width / 2), y);
			
			y += gp.tileSize / 2;
			g2.setFont(g2.getFont().deriveFont(14F));
			label = p.getDexEntry();
			String[] entryLines = Item.breakString(label, 26).split("\n");
			for (String s : entryLines) {
				g2.drawString(s, getCenterAlignedTextX(s, startX + width / 2), y);
				y += gp.tileSize / 3;
			}
			
			x = startX + width + gap;
			y = startY;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (starterConfirm) {
				if (commandNum == 0) {
					gp.player.p.flag[0][1] = true;
					gp.player.p.starter = starter;
					gp.setTaskState();
					Task.addTask(Task.GIFT, "", starters[starter]);
					starterConfirm = false;
					
			        int secondStarter = -1;
			        do {
			        	secondStarter = rand.nextInt(3);
			        } while (secondStarter == gp.player.p.starter);
			        gp.player.p.secondStarter = secondStarter;
			        
			        Pokemon.updateRivals();
				} else {
					starterConfirm = false;
					commandNum = 0;
				}
			} else {
				starterConfirm = true;
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			commandNum = 1 - commandNum;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			commandNum = 1 - commandNum;
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			starter = starterConfirm ? starter : (starter + 2) % 3;
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			starter = starterConfirm ? starter : (starter + 1) % 3;
		}
		
		if (starterConfirm) {
			currentDialogue = "Are you sure you want to choose " + starters[starter] + "?";
			drawDialogueScreen(true);
			
			int x2 = gp.tileSize * 11;
			int y2 = gp.tileSize * 4;
			int width2 = gp.tileSize * 3;
			int height2 = (int) (gp.tileSize * 2.5);
			drawSubWindow(x2, y2, width2, height2);
			
			x2 += gp.tileSize;
			y2 += gp.tileSize;
			g2.drawString("Yes", x2, y2);
			if (commandNum == 0) {
				g2.drawString(">", x2-24, y2);
			}
			
			y2 += gp.tileSize;
			g2.drawString("No", x2, y2);
			if (commandNum == 1) {
				g2.drawString(">", x2-24, y2);
			}
		}
		
		drawToolTips("Pick", null, "Back", null);
	}

	private void drawBoxToolTips(boolean party) {
		if (!gp.keyH.shiftPressed || showBoxSummary || (showBoxParty && !party) || release) return;
		int x = 0;
		int y = gp.tileSize * 9;
		int width = (int) (party ? gp.tileSize * 3.5 : gp.tileSize * 10.5);
		int height = (int) (gp.tileSize * 1.5);
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		x += gp.tileSize / 2;
		y += gp.tileSize;
		
		String toolTips = party ? "[Ctrl]+[D] Item" : "[Ctrl]+[W] Release  [Ctrl]+[A] Calc  [Ctrl]+[D] Item";
		g2.drawString(toolTips, x, y);
		
		if (party) return;
		
		String wText;
		if (boxNum >= 0) {
			wText = "Info";
		} else {
			wText = "Name";
		}
		
		drawToolTips(wText, "Swap", "Back", "Party");
	}
	
	private ArrayList<BufferedImage> extractFrames(String path) {
		ArrayList<BufferedImage> frames = new ArrayList<>();
		
		try {
			ImageInputStream stream = ImageIO.createImageInputStream(getClass().getResourceAsStream(path));
			
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(stream);
				
				int numFrames = reader.getNumImages(true);
				
				for (int i = 0; i < numFrames; i++) {
					BufferedImage frame = reader.read(i);
					frames.add(frame);
				}
			}
			
			stream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return frames;
	}

	public boolean tasksContainsTask(int type) {
		if (currentTask == null && checkTasks) return false;
		if (currentTask != null && currentTask.type == type) return true;
		for (Task t : tasks) {
			if (t.type == type) return true;
		}
		return false;
	}
	
	private void drawItemSum() {
		HashMap<Item, Integer> itemCounts = new HashMap<>();
		
		TreasureChest chest = (TreasureChest) currentTask.e;
		
		for (Item i : chest.items) {
			itemCounts.put(i, itemCounts.getOrDefault(i, 0) + 1);
		}
		
		int itemCount = itemCounts.size();
		
		int x = gp.tileSize * 3;
		int y = itemCount > 10 ? 0 : gp.tileSize / 2;
		int width = gp.screenWidth - x*2;
		int height;
		
		int itemX = x + gp.tileSize;
		int itemY = (int) (y + gp.tileSize * 1.5);
		int textHeight = gp.tileSize;
		
		height = (int) (itemCount * textHeight + gp.tileSize * 1.25);
		if (itemCount > 9) height -= (gp.tileSize / 2) * (itemCount % 9);
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(48F));
		String text = "You got:";
		g2.drawString(text, getCenterAlignedTextX(text, gp.screenWidth / 2), itemY);
		
		itemY += gp.tileSize / 2;
		
		g2.setFont(g2.getFont().deriveFont(32F));
		
		for (Map.Entry<Item, Integer> e : itemCounts.entrySet()) {
			g2.drawImage(e.getKey().getImage(), itemX, itemY, null);
			itemY += gp.tileSize / 2;
			String itemString = e.getValue() + " x " + e.getKey().toString();
			g2.drawString(itemString, (int) (itemX + gp.tileSize * 0.75), itemY);
			itemY += gp.tileSize / 3;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			currentTask = null;
		}
		
		drawToolTips(null, null, "Close", null);
	}
	
	private void drawSpace() {
		drawSpaceBackground();
		
		if (currentTask.start > 0) {
			Color base = currentTask.color;
			g2.setColor(new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha() / currentTask.counter * counter));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}
		
		if (currentTask.e != null && currentTask.message != null && !currentTask.message.isEmpty()) {
			g2.setFont(getFont(currentTask.finish));
			showMessage(Item.breakString(currentTask.message, MAX_TEXTBOX));
			above = true;
			drawNameLabel(above);
		}
		
		counter++;
		if (counter > currentTask.counter) {
			counter = 0;
			if (currentTask.start > 0 && currentTask.wipe) {
				int prevMap = gp.currentMap;
				gp.currentMap = currentTask.start;
				gp.player.worldX = currentTask.start == 160 ? 41 * gp.tileSize : currentTask.start == 152 ? 67 * gp.tileSize : 50 * gp.tileSize;
				gp.player.worldY = currentTask.start == 160 ? 15 * gp.tileSize : currentTask.start == 152 ? 75 * gp.tileSize : 50 * gp.tileSize;
				gp.player.direction = "down";
				warpPlayer(prevMap, gp.currentMap);
			}
			currentDialogue = null;
			showMessage = false;
			currentTask = null;
		}
	}
	
	private void drawSpaceBackground() {
		if (spaceFrames == null || spaceFrames.isEmpty()) return;
		
		BufferedImage currentFrame = spaceFrames.get(spaceFrameIndex);
		g2.drawImage(currentFrame, 0, 0, gp.screenWidth, gp.screenHeight, null);
		
		spaceFrameCounter++;
		if (spaceFrameCounter >= 6) {
			spaceFrameCounter = 0;
			spaceFrameIndex = (spaceFrameIndex + 1) % spaceFrames.size();
		}
	}
}
