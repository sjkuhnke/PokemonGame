package overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import entity.Entity;
import entity.PlayerCharacter;
import pokemon.Ability;
import pokemon.AbstractUI;
import pokemon.Bag;
import pokemon.Item;
import pokemon.Move;
import pokemon.Moveslot;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Pokemon.Node;
import pokemon.Pokemon.Task;

public class UI extends AbstractUI{

	public int menuNum = 0;
	public int subState = 0;
	public Entity npc;
	public int slotCol = 0;
	public int slotRow = 0;
	public int bagNum = 0;
	public int selectedBagNum = -1;
	public int currentPocket = Item.MEDICINE;
	public int bagState;
	public int sellAmt = 1;
	public Item currentItem;
	public ArrayList<Bag.Entry> currentItems;
	
	public int btX = 0;
	public int btY = 0;
	
	public boolean showMessage;
	public boolean showArea;
	public int areaCounter;
	public Pokemon currentPokemon;
	public Move currentMove;
	public String currentHeader;
	
	public int boxNum;
	public boolean isGauntlet;
	public Pokemon currentBoxP;
	public boolean release;
	public boolean gauntlet;
	
	public int dexNum = 1;
	public int dexMode;
	public int levelDexNum;
	public int tmDexNum;
	
	public int remindNum;
	
	BufferedImage transitionBuffer;
	
	public static final int MAX_SHOP_COL = 10;
	public static final int MAX_SHOP_ROW = 4;
	
	
	public UI(GamePanel gp) {
		this.gp = gp;
		
		currentDialogue = "";
		commandNum = 0;
		tasks = new ArrayList<>();
		
		ballIcon = setup("/icons/ball", 1);
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/marumonica.ttf");
			marumonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void showMessage(String text) {
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
		
		if (showArea) {
			drawAreaName();
		}
		
		if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			drawDialogueScreen(true);
			drawToolTips("OK", null, null, null);
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
		
		if (gp.gameState == GamePanel.NURSE_STATE) {
			drawNurseScreen();
		}
		
		if (gp.gameState == GamePanel.START_BATTLE_STATE) {
			drawBattleStartTransition();
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
		
		if (showBoxParty) {
			drawParty(null);
			String sText = partySelectedNum >= 0 ? "Deselect" : "Close";
			drawToolTips("Info", "Swap", sText, "Close");
		}
		
		if (showBoxSummary) {
			drawSummary(currentBoxP, null);
		}
		
		if (showMessage) {
			drawDialogueScreen(true);
			drawToolTips("OK", null, null, null);
		}
	}

	private void drawTask() {
		switch(currentTask.type) {
		case Task.LEVEL_UP:
		case Task.TEXT:
			showMessage(Item.breakString(currentTask.message, 42));
			break;
		case Task.MOVE:
			currentMove = currentTask.move;
			currentPokemon = currentTask.p;
			showMoveOptions = true;
			break;
		case Task.EVO_ITEM:
			evolveMon();
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
			gp.player.p.catchPokemon(currentTask.p);
			currentTask.p.item = currentTask.item;
			setNicknaming(true);
			currentTask = null;
			break;
		case Task.NICKNAME:
			currentDialogue = currentTask.message;
			drawDialogueScreen(true);
			setNickname(currentTask.p);
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					currentTask.p.nickname = nickname.toString().trim();
					nickname = new StringBuilder();
					if (currentTask.p.nickname == null || currentTask.p.nickname.trim().isEmpty()) currentTask.p.nickname = currentTask.p.name;
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
			drawMoveReminderParty();
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
			currentDialogue = currentTask.message;
			drawDialogueScreen(true);
			drawFossilScreen(gp.player.p.bag);
			break;
		case Task.CONFIRM:
			currentDialogue = Item.breakString(currentTask.message, 42);
			drawDialogueScreen(true);
			drawConfirmWindow(currentTask.counter);
			break;
		case Task.TELEPORT:
			gp.gameState = GamePanel.TRANSITION_STATE;
			break;
		}
	}

	private void drawConfirmWindow(int type) {
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
				switch (type) {
				case 0: // gauntlet at top of splinkty
					currentTask = null;
					gp.eHandler.teleport(149, 49, 76, false);
					break;
				}
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				currentTask = null;
				gp.gameState = GamePanel.PLAY_STATE;
				showMessage("Come back when you're ready!");
				commandNum = 0;
			}
		}
		
		if (gp.keyH.sPressed) {
			currentTask = null;
		}
		
		if (gp.keyH.upPressed || gp.keyH.downPressed) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			commandNum = 1 - commandNum;
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
				if (hasTSF) {
					currentTask = null;
					Pokemon.addTask(Task.TEXT, "Okay! I'll go revive this ancient Pokemon!");
					Pokemon.addTask(Task.GIFT, "", new Pokemon(211, 20, true, false));
					bag.remove(Item.THUNDER_SCALES_FOSSIL);
					commandNum = 0;
				} else {
					Pokemon.addTask(Task.TEXT, "You don't have any Thunder Scales Fossils for me to revive!");
					Pokemon.addTask(Task.FOSSIL, currentTask.message);
					currentTask = null;
				}
			} else if (commandNum == 1) {
				if (hasDSF) {
					currentTask = null;
					Pokemon.addTask(Task.TEXT, "Okay! I'll go revive this ancient Pokemon!");
					Pokemon.addTask(Task.GIFT, "", new Pokemon(213, 20, true, false));
					bag.remove(Item.DUSK_SCALES_FOSSIL);
					commandNum = 0;
				} else {
					Pokemon.addTask(Task.TEXT, "You don't have any Dusk Scales Fossils for me to revive!");
					Pokemon.addTask(Task.FOSSIL, currentTask.message);
					currentTask = null;
				}
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
    			if (!p.nickname.equals(p.name)) message += (" (" + p.name + ")");
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
		int height = gp.tileSize * 8;
		
		int sumX = x + width - gp.tileSize;
		int sumY = (int) (gp.tileSize * 3.25);

		ArrayList<Move> forgottenMoves = new ArrayList<>();
        for (int i = 0; i < p.getLevel(); i++) {
        	if (i < p.movebank.length) {
        		Node move = p.movebank[i];
        		while (move != null) {
        			if (!p.knowsMove(move.data)) {
        				forgottenMoves.add(move.data);
        			}
        			move = move.next;
        		}
        	}
        }
        if (forgottenMoves.isEmpty()) {
            Pokemon.addTask(Task.TEXT, "This Pokemon has not forgotten any moves.");
            currentTask = null;
            return;
        }
        
        drawSubWindow(x, y, width, height);
        
        x += gp.tileSize;
        y += gp.tileSize / 2;
        int moveWidth = gp.tileSize * 4;
        int moveHeight = (int) (gp.tileSize * 0.75);
        
        for (int i = remindNum; i < remindNum + 8; i++) {
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
				g2.drawString(moveString, getCenterAlignedTextX(moveString, x + (moveWidth / 2)), y);
				y += gp.tileSize / 3;
			}
        }
        
        Move m = forgottenMoves.get(remindNum);
		
		drawMoveSummary(sumX, sumY, p, null, null, m);
		
		// Down Arrow
		if (remindNum + 8 < forgottenMoves.size()) {
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
			Task t = Pokemon.addTask(Task.MOVE, "", p);
			t.move = m;
			currentTask = null;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			Pokemon.addTask(Task.PARTY, "");
			currentTask = null;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			if (remindNum > 0) {
				remindNum--;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			if (remindNum < forgottenMoves.size() - 1) {
				remindNum++;
			}
		}
		
		drawToolTips("OK", null, "Back", null);
	}

	private void drawMoveReminderParty() {
		drawParty(null);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			Pokemon p = gp.player.p.team[partyNum];
			Pokemon.addTask(Task.REMIND, "What move would you like to teach " + p.nickname + "?", p);
			remindNum = 0;
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
		
		x += gp.tileSize / 2;
		y += gp.tileSize / 2;
		int startX = x;
		int textWidth = (int) (gp.tileSize * 0.75);
		
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int maxShow = gp.player.p.getDexShowing();
		for (int i = dexNum; i < dexNum + 12; i++) {
			int textY = y + gp.tileSize / 2;
			if (i == dexNum) {
				g2.drawString(">", x, textY);
			}
			x += gp.tileSize / 3;
			if (i <= maxShow) {
				int mode = gp.player.p.pokedex[i];
				if (mode == 2) {
					g2.drawImage(ballIcon, x, y + 8, null);
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedID(i) + " " + Pokemon.getName(i), x, textY);
					x += gp.tileSize * 4;
					g2.drawImage(Pokemon.getType1(i).getImage(), x, y + 4, null);
					x += gp.tileSize / 2;
					if (Pokemon.getType2(i) != null) g2.drawImage(Pokemon.getType2(i).getImage(), x, y + 4, null);
				} else if (mode == 1) {
					g2.setColor(Color.DARK_GRAY);
					int circleDiameter = 16;
					g2.fillOval(x, y + 8, circleDiameter, circleDiameter);
					g2.setColor(Color.WHITE);
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedID(i) + " " + Pokemon.getName(i), x, textY);
				} else {
					x += gp.tileSize / 2;
					g2.drawString(Pokemon.getFormattedID(i) + " " + "---", x, textY);
				}
				x = startX;
				y += textWidth;
			}
		}
		
		int mode = gp.player.p.pokedex[dexNum];
		ArrayList<Move> levelMoveList = new ArrayList<>();
		ArrayList<Integer> levelLevelList = new ArrayList<>();
		ArrayList<Item> tmList = new ArrayList<>();
		Pokemon test = new Pokemon(dexNum, 5, false, false);
		test.abilitySlot = 0;
		test.setAbility(test.abilitySlot);
		if (mode == 2) {
			if (test.movebank != null) {
			    for (int i = 0; i < test.movebank.length; i++) {
			        Node move = test.movebank[i];
			        while (move != null) {
			        	levelMoveList.add(move.data);
			        	levelLevelList.add(i + 1);
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
				if (dexNum > 1) {
					dexNum--;
					levelDexNum = 0;
					tmDexNum = 0;
				}
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
				if (dexNum < maxShow) {
					dexNum++;
					levelDexNum = 0;
					tmDexNum = 0;
				}
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
		
		int infoX = 0;
		int infoY = (int) (gp.screenHeight - gp.tileSize * 2.5);
		int infoWidth = width;
		int infoHeight = (int) (gp.tileSize * 2.5);
		
		drawSubWindow(infoX, infoY, infoWidth, infoHeight, 255);
		
		int infoTextX = infoX + gp.tileSize / 2;
		int infoTextY = infoY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(32F));
		int[] amounts = gp.player.p.getDexAmounts();
		int caughtAmt = amounts[1];
		int seenAmt = amounts[0] + caughtAmt;
		String caughtString = "Caught: " + caughtAmt;
		String seenString = "Seen: " + seenAmt;
		if (caughtAmt >= Pokemon.MAX_POKEMON) {
			g2.setPaint(new GradientPaint(infoTextX, infoTextY, new Color(255, 215, 0), infoTextX + gp.tileSize, infoTextY - gp.tileSize, new Color(245, 225, 210)));
		}
		g2.drawString(caughtString, infoTextX, infoTextY);
		
		g2.setColor(Color.WHITE);
		infoTextY += gp.tileSize;
		if (seenAmt >= Pokemon.MAX_POKEMON) {
			g2.setPaint(new GradientPaint(infoTextX, infoTextY, new Color(192, 192, 192), infoTextX + gp.tileSize, infoTextY - gp.tileSize, new Color(245, 225, 210)));
		}
		g2.drawString(seenString, infoTextX, infoTextY);
		
		String wText = dexMode == 0 ? "Moves": null;
		drawToolTips(wText, null, "Back", null);
	}

	private void drawDexSummary(Pokemon p, int mode, ArrayList<Move> levelMoveList, ArrayList<Integer> levelLevelList, ArrayList<Item> tmList) {
		if (mode == 0) return;
		int x = gp.tileSize * 7;
		int y = 0;
		int width = gp.tileSize * 9;
		int height = (int) (mode == 2 ? gp.tileSize * 12 : gp.tileSize * 4.5);
		
		drawSubWindow(x, y, width, height);
		
		// ID
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(20F));
		g2.drawString(Pokemon.getFormattedID(p.id), x, y);
		
		// Name
		x += gp.tileSize / 2;
		y += gp.tileSize / 4;
		int startX = x;
		g2.setFont(g2.getFont().deriveFont(36F));
		g2.drawString(p.name, x, y + gp.tileSize / 2);
		
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
			String type = Pokemon.getStatType(i);
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
		int cBoxIndex = gp.player.p.currentBox;
		Pokemon[] cBox = gauntlet ? gp.player.p.gauntletBox : gp.player.p.boxes[cBoxIndex];
		
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
		} else {
			int x2 = headX + gp.tileSize / 2;
			int y2 = headTextY - gp.tileSize / 2;
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
			x2 = headWidth + headX - gp.tileSize / 2 - gp.tileSize / 3;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		}
		String boxText = gauntlet ? "Gauntlet Box" : "Box " + (cBoxIndex + 1);
		int centerX = headX + (headWidth / 2);
		g2.drawString(boxText, getCenterAlignedTextX(boxText, centerX), headTextY);
		int highlightWidth = gauntlet ? gp.tileSize * 2 : gp.tileSize;
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
			if (i == boxSwapNum) {
				g2.setColor(new Color(100, 100, 220, 200));
				g2.fillRoundRect(cX - 8, cY - 8, spriteWidth, spriteHeight, 10, 10);
				g2.setColor(g2.getColor().darker());
				g2.drawRoundRect(cX - 8, cY - 8, spriteWidth, spriteHeight, 10, 10);
			}
			
			if (i < cBox.length && cBox[i] != null) {
				g2.drawImage(cBox[i].getSprite(), cX, cY, null);
				if (cBox[i].item != null) {
					g2.drawImage(cBox[i].item.getImage(), cX - 6, cY + 62, null);
				}
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
			Pokemon selected = boxSwapNum >= 0 ? cBox[boxSwapNum] : gp.player.p.team[partySelectedNum];
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
					g2.drawImage(selected.item.getImage(), selectX - 6, selectY + 62, null);
				}
			}
		}
		
		if (!showBoxSummary && !showBoxParty && !release) {
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (boxNum >= 0) {
					currentBoxP = cBox[boxNum];
				}
				if (currentBoxP != null) {
					if (!gp.keyH.ctrlPressed) {
						showBoxSummary = true;
					} else {
						gp.keyH.ctrlPressed = false;
						release = true;
					}
				}
			}
			
			if (gp.keyH.aPressed && boxNum >= 0) {
				gp.keyH.aPressed = false;
				if (boxSwapNum == boxNum) {
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
					Pokemon temp = cBox[boxSwapNum];
					cBox[boxSwapNum] = cBox[boxNum];
					cBox[boxNum] = temp;
					boxSwapNum = -1;
				} else {
					boxSwapNum = boxNum;
				}
			}
			
			if (gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				if (boxNum >= 0) {
					boxNum -= 6;
				} else if (!gp.ui.isGauntlet) {
					gauntlet = !gauntlet;
					boxSwapNum = -1;
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
						boxSwapNum = -1;
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
						boxSwapNum = -1;
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
	                }
	                partySelectedNum = -1;
				} else if (boxSwapNum >= 0) {
					if (cBox[boxSwapNum] == null) {
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
					
					Pokemon temp = gp.player.p.team[partyNum];
					if (temp != null) {
                        temp.heal();
                    }
					gp.player.p.team[partyNum] = cBox[boxSwapNum];
					cBox[boxSwapNum] = temp;
					
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
		
		drawBoxToolTips();
	}

	private void taskState() {
		if (currentTask == null) {
			if (tasks.size() > 0) {
				currentTask = tasks.remove(0);
			} else {
				gp.gameState = GamePanel.PLAY_STATE;
				return;
			}
		}
		
		drawTask();
	}

	private void rareCandyState() {
		drawParty(null);
		drawItemUsingScreen();
		
		if (currentTask == null) {
			if (gp.player.p.bag.count[Item.RARE_CANDY.getID()] <= 0) {
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
		
		if (currentTask == null && tasks.size() > 0) {
			currentTask = tasks.remove(0);
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
			        		currentItems = gp.player.p.getItems(currentPocket);
			        	} else {
			        		showMessage("It won't have any effect.");
			        	}
					} else if (currentItem == Item.PP_UP || currentItem == Item.PP_MAX) {
						Moveslot m = currentPokemon.moveset[moveOption - 1];
						if (m.maxPP < (m.move.pp * 8 / 5)) {
			    			if (currentItem == Item.PP_UP) { // PP Up
			    				m.maxPP += Math.max((m.move.pp / 5), 1);
			    				showMessage(m.move.toString() + "'s PP was increased!");
			    			} else { // PP Max
			    				m.maxPP = (m.move.pp * 8 / 5);
			    				showMessage(m.move.toString() + "'s PP was maxed!");
			    			}
				        	gp.player.p.bag.remove(currentItem);
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
			        	showMessage(currentPokemon + "'s " + Pokemon.getStatType(moveOption) + "IV was maxed out!");
			        	gp.player.p.bag.remove(currentItem);
		        		currentItems = gp.player.p.getItems(currentPocket);
		        	} else {
		        		showMessage("It won't have any effect.");
		        	}
					showIVOptions = false;
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
	        String text = Pokemon.getStatType(i) + ": " + iv;
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
	
	private void useRareCandy(Pokemon pokemon) {
        if (pokemon.getLevel() == 100) {
            showMessage("It won't have any effect.");
            return;
        }
        gp.player.p.elevate(pokemon);
        gp.player.p.bag.remove(Item.RARE_CANDY);
	}

	private void useItem() {
		drawItemUsingScreen();
		drawParty(currentItem);
		if (gp.keyH.wPressed && !showMoveOptions && !showIVOptions) {
			if (currentItem == Item.RARE_CANDY) {
				gp.gameState = GamePanel.RARE_CANDY_STATE;
			} else {
				gp.keyH.wPressed = false;
				gp.player.p.useItem(gp.player.p.team[partyNum], currentItem, gp);	
			}
		}
		
		drawToolTips("Use", null, "Back", "Back");
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
				
				Path savesDirectory = Paths.get("./saves/");
	            if (!Files.exists(savesDirectory)) {
	                try {
						Files.createDirectories(savesDirectory);
					} catch (IOException e) {
						showMessage("The /saves/ folder could not be created.\nIf you are playing this game in your downloads,\ntry moving it to another folder.");
					}
	            }
	            
		    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./saves/" + gp.player.currentSave))) {
	            	gp.player.p.setPosX(gp.player.worldX);
	            	gp.player.p.setPosY(gp.player.worldY);
	            	gp.player.p.currentMap = gp.currentMap;
	                oos.writeObject(gp.player.p);
	                oos.close();
	                showMessage("Game saved sucessfully!");
	            } catch (IOException ex) {
	            	showMessage("Error writing player to file:\n" + ex.getMessage());
	            }
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
		text = "Pokedex";
		textX = x + gp.tileSize * 2;
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 0) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 1;
			}
		}
		
		// Party
		text = "Party";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 1) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 2;
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
		text = "Map";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 5) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = 6;
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
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			subState = 7;
		}
		
		drawToolTips("Info", "Swap", "Back", "Back");
	}
	
	private void showBag() {
		int x = gp.tileSize * 6;
		int y = gp.tileSize / 2;
		int width = gp.tileSize * 8;
		int height = gp.tileSize;
		drawSubWindow(x, y, width, height);
		String pocketName = Item.getPocketName(currentPocket);
		g2.drawString(pocketName, this.getCenterAlignedTextX(pocketName, x + (width / 2)), (int) (y + gp.tileSize * 0.75));
		
		y += gp.tileSize;
		height = gp.tileSize * 10;
		
		drawSubWindow(x, y, width, height);
		
		int descX = gp.tileSize / 2;
		int descY = gp.tileSize * 2;
		int descWidth = (int) (gp.tileSize * 5.5);
		int descHeight = gp.tileSize * 8;
		drawSubWindow(descX, descY, descWidth, descHeight);
		int textX = descX + 20;
		int textY = descY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		if (currentItems.size() > 0) {
			if (bagNum >= currentItems.size()) bagNum--;
			String desc = currentItems.get(bagNum).getItem().getDesc();
			for (String line : Item.breakString(desc, 25).split("\n")) {
				g2.drawString(line, textX, textY);
				textY += 32;
			}
		}
		g2.setFont(g2.getFont().deriveFont(32F));
		
		x += gp.tileSize;
		y += gp.tileSize / 2;
		for (int i = bagNum; i < bagNum + 9; i++) {
			g2.setColor(Color.WHITE);
			if (i == bagNum) {
				g2.drawString(">", (x - gp.tileSize / 2) - 2, y + gp.tileSize / 2);
			}
			if (i == selectedBagNum) {
				g2.setColor(Color.BLUE.brighter());
			}
			if (i < currentItems.size()) {
				Bag.Entry current = currentItems.get(i);
				g2.drawImage(current.getItem().getImage(), x, y, null);
				y += gp.tileSize / 2;
				String itemString = current.getItem().toString();
				if (currentPocket != Item.TMS) itemString += " x " + current.getCount();
				g2.drawString(itemString, x + gp.tileSize / 2, y);
				y += gp.tileSize / 2;
			}
		}
		// Down Arrow
		if (bagNum + 9 < currentItems.size()) {
			int x2 = gp.tileSize * 5 + width;
			int y2 = height + (gp.tileSize / 2);
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2, y2, y2 + height2}, 3);
		}
		// Up Arrow
		if (bagNum != 0 && bagState != 2) {
			int x2 = gp.tileSize * 5 + width;
			int y2 = gp.tileSize * 2;
			int width2 = gp.tileSize / 2;
			int height2 = gp.tileSize / 2;
			g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
		}
		// Subwindow for item options
		if (!showMoveOptions) {
			if (gp.keyH.aPressed) {
				gp.keyH.aPressed = false;
				if (selectedBagNum == -1) {
					selectedBagNum = bagNum;
				} else if (selectedBagNum == bagNum) {
					selectedBagNum = -1;
				} else {
					gp.player.p.moveItem(currentItems.get(selectedBagNum).getItem(), currentItems.get(bagNum).getItem());
					currentItems = gp.player.p.getItems(currentPocket);
					selectedBagNum = -1;
					if (bagNum > 0) bagNum--;
				}
			}
			if (bagState == 0 && gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				bagState = 1;
			}
		}
		
		if (bagState == 1) {
			drawItemOptions();
		} else if (bagState == 2) {
			drawSellOptions();
		} else if (bagState == 3) {
			drawMoveSummary(gp.tileSize / 2, gp.tileSize * 6, null, null, null, currentItems.get(bagNum).getItem().getMove());
		}
		
		drawToolTips("OK", "Swap", "Back", "Back");
	}

	private void drawItemOptions() {
		int x = gp.tileSize * 12;
		int y = (int) (gp.tileSize * 1.5);
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize;
		String option1 = currentPocket == Item.BERRY || currentPocket == Item.HELD_ITEM ? "Give" : "Use";
		g2.drawString(option1, x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				currentItem = currentItems.get(bagNum).getItem();
				if (currentItem == Item.REPEL) {
					if (!gp.player.p.repel) {
						useRepel();
				    } else {
				    	showMessage("It won't have any effect.");
				    }
					bagState = 0;
				} else if (currentItem == Item.CALCULATOR) {
					Item.useCalc(gp.player.p, null);
				} else {
					gp.gameState = GamePanel.USE_ITEM_STATE;
				}
			}
		}
		y += gp.tileSize;
		String option2 = currentPocket == Item.TMS ? "Info" : "Sell";
		g2.drawString(option2, x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				bagState = currentPocket == Item.TMS ? 3 : 2;
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
		g2.drawString("+$" + currentItems.get(bagNum).getItem().getSell() * sellAmt, x, y);
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			showMessage("Sold " + sellAmt + " " + currentItems.get(bagNum).getItem().toString() + " for $" + sellAmt * currentItems.get(bagNum).getItem().getSell());
			gp.player.p.sellItem(currentItems.get(bagNum).getItem(), sellAmt);
			currentItems = gp.player.p.getItems(currentPocket);
			bagState = 0;
			commandNum = 0;
			sellAmt = 1;
		}
		
	}
	
	public void drawTransition() {
		counter++;
		g2.setColor(new Color(0,0,0,counter*5));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		if (counter == 50) {
			counter = 0;
			if (currentTask != null && currentTask.type == Task.TELEPORT) {
				gp.gameState = GamePanel.TASK_STATE;
				gp.currentMap = currentTask.counter;
				gp.player.worldX = gp.tileSize * currentTask.start;
				gp.player.worldY = gp.tileSize * currentTask.finish;
				gp.player.worldY -= gp.tileSize / 4;
				gp.eHandler.previousEventX = gp.player.worldX;
				gp.eHandler.previousEventY = gp.player.worldY;
				gp.player.p.currentMap = currentTask.counter;
				gp.eHandler.canTouchEvent = !currentTask.wipe;
				currentTask = null;
			} else {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.currentMap = gp.eHandler.tempMap;
				gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
				gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
				gp.player.worldY -= gp.tileSize / 4;
				gp.eHandler.previousEventX = gp.player.worldX;
				gp.eHandler.previousEventY = gp.player.worldY;
				gp.player.p.currentMap = gp.eHandler.tempMap;
				gp.eHandler.canTouchEvent = !gp.eHandler.tempCooldown;
			}
			gp.player.p.surf = false;
			gp.player.p.lavasurf = false;
			
			String currentMap = PlayerCharacter.currentMapName;
			PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / 48), (int) Math.round(gp.player.worldY * 1.0 / 48));
			Main.window.setTitle("Pokemon Game - " + PlayerCharacter.currentMapName);
			if (!currentMap.equals(PlayerCharacter.currentMapName)) showAreaName();
		}
	}
	
	private void drawBattleStartTransition() {
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
				if (!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) {
					gp.battleUI.ball = gp.player.p.getBall(gp.battleUI.ball);
					gp.battleUI.balls = gp.player.p.getBalls();
				}
				gp.battleUI.subState = BattleUI.STARTING_STATE;
				gp.gameState = GamePanel.BATTLE_STATE;
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
		drawInventory(npc);
	}
	
	private void drawInventory(Entity entity) {
		int x = gp.tileSize * 2;
		int y = gp.tileSize;
		int width = gp.tileSize * 12;
		int height = gp.tileSize * 6;
		
		drawSubWindow(x, y, width, height);
		
		final int slotXstart = x + 20;
		final int slotYstart = y + 20;
		int slotX = slotXstart;
		int slotY = slotYstart;
		int slotSize = gp.tileSize + 5;
		
		for (int i = 0; i < entity.inventory.size(); i++) {
			g2.drawImage(npc.inventory.get(i).getImage2(), slotX, slotY, null);
			
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
		int dFrameHeight = gp.tileSize * 3;
		
		int textX = dFrameX + 20;
		int textY = (int) (dFrameY + gp.tileSize * 0.8);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int itemIndex = getItemIndexOnSlot();
		
		if (itemIndex < entity.inventory.size()) {
			Item current = entity.inventory.get(itemIndex);
			drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
			g2.drawString(current.toString(), textX, textY);
			String price = "$" + current.getCost();
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
				if (gp.player.p.buy(current)) {
					if (current.isTM()) {
						entity.inventory.remove(current);
					}
				} else {
					showMessage("Not enough money!");
				}
			}
		}
		
		int moneyX = x + width - gp.tileSize * 3;
		int moneyY = y - gp.tileSize;
		int moneyWidth = gp.tileSize * 3;
		int moneyHeight = gp.tileSize;
		
		drawSubWindow(moneyX, moneyY, moneyWidth, moneyHeight);
		textX = moneyX + 20;
		textY = (int) (moneyY + gp.tileSize * 0.75);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.drawString("$" + gp.player.p.getMoney(), textX, textY);
		
		drawToolTips("Buy", null, "Back", "Back");
	}

	private int getItemIndexOnSlot() {
		int itemIndex = slotCol + (slotRow*MAX_SHOP_COL);
		return itemIndex;
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
				useRepel();
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
	
	private void useRepel() {
		gp.player.p.repel = true;
		gp.player.p.steps = 1;
		gp.player.p.bag.remove(Item.REPEL);
		currentItems = gp.player.p.getItems(currentPocket);
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
		} else if (message.length() < 19) {
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
	
	private void drawBoxToolTips() {
		if (!gp.keyH.shiftPressed || showBoxSummary || showBoxParty || release) return;
		int x = 0;
		int y = gp.tileSize * 9;
		int width = gp.tileSize * 8;
		int height = (int) (gp.tileSize * 1.5);
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		x += gp.tileSize / 2;
		y += gp.tileSize;
		
		g2.drawString("[Ctrl]+[W] Release   [Ctrl]+[A] Calc", x, y);
		
		drawToolTips("Info", "Swap", "Back", "Party");
	}
	
	private void evolveMon() {
		Task t = currentTask;
		Pokemon oldP = t.p;
		Pokemon newP = t.evo;
		int hpDif = oldP.getStat(0) - oldP.currentHP;
        newP.currentHP -= hpDif;
        newP.moveMultiplier = newP.moveMultiplier;
        Task text = Pokemon.createTask(Task.TEXT, oldP.nickname + " evolved into " + newP.name + "!");
        Pokemon.insertTask(text, 0);
        newP.exp = oldP.exp;
        gp.player.p.pokedex[newP.id] = 2;
        
        // Update player team
        int index = Arrays.asList(gp.player.p.getTeam()).indexOf(oldP);
        gp.player.p.team[index] = newP;
        if (index == 0) {
        	oldP.setVisible(false);
        	gp.player.p.setCurrent(newP);
        	gp.battleUI.user = newP;
        	gp.battleUI.userHP = newP.currentHP;
        	gp.battleUI.maxUserHP = newP.getStat(0);
        }
        newP.checkMove(1);
        
        currentTask = null;
	}
}
