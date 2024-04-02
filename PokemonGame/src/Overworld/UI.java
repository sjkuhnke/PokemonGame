package Overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import Entity.Entity;
import Entity.PlayerCharacter;
import Swing.AbstractUI;
import Swing.Item;

public class UI extends AbstractUI{

	public int menuNum = 0;
	public int subState = 0;
	public Entity npc;
	public int slotCol = 0;
	public int slotRow = 0;
	
	public int btX = 0;
	public int btY = 0;
	
	BufferedImage transitionBuffer;
	
	public static final int MAX_SHOP_COL = 9;
	public static final int MAX_SHOP_ROW = 5;
	
	
	public UI(GamePanel gp) {
		this.gp = gp;
		
		currentDialogue = "";
		commandNum = 0;
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/marumonica.ttf");
			marumonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		transitionBuffer = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
	}
	
	@Override
	public void showMessage(String text) {
		gp.gameState = GamePanel.DIALOGUE_STATE;
		gp.ui.currentDialogue = text;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(marumonica);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		
		if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			drawDialogueScreen(true);
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
			gp.keyH.wPressed = false;
			gp.player.showDex();
			break;
		case 2:
			gp.keyH.wPressed = false;
			showParty();
			break;
		case 3:
			gp.keyH.wPressed = false;
			gp.gameState = GamePanel.PLAY_STATE;
			subState = 0;
			gp.player.showBag();
			break;
		case 4:
			gp.keyH.wPressed = false;
			gp.gameState = GamePanel.PLAY_STATE;
			subState = 0;
			gp.player.saveGame();
			break;
		case 5:
			gp.keyH.wPressed = false;
			gp.gameState = GamePanel.PLAY_STATE;
			subState = 0;
			gp.player.showPlayer();
			break;
		case 6:
			gp.keyH.wPressed = false;
			gp.openMap();
			break;
		}
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
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
			}
		}
	}
	
	private void showParty() {
		int x = gp.tileSize*4;
		int y = gp.tileSize;
		int width = gp.tileSize*8;
		int height = gp.tileSize*10;
		
		drawSubWindow(x, y, width, height);
		
	}
	
	public BufferedImage setup(String imageName, int scale) {
	    BufferedImage image = null;
	    
	    try {
	        // Load the original image
	        BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
	        
	        // Calculate the new dimensions based on the scale
	        int newWidth = originalImage.getWidth() * scale;
	        int newHeight = originalImage.getHeight() * scale;
	        
	        // Create a new BufferedImage with the scaled dimensions
	        image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	        
	        // Draw the original image onto the scaled image
	        Graphics2D g2d = image.createGraphics();
	        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
	        g2d.dispose();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return image;
	}
	
	public void drawTransition() {
		counter++;
		g2.setColor(new Color(0,0,0,counter*5));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		if (counter == 50) {
			counter = 0;
			gp.gameState = GamePanel.PLAY_STATE;
			gp.currentMap = gp.eHandler.tempMap;
			gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
			gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
			gp.player.worldY -= gp.tileSize / 4;
			gp.eHandler.previousEventX = gp.player.worldX;
			gp.eHandler.previousEventY = gp.player.worldY;
			gp.player.p.currentMap = gp.eHandler.tempMap;
			
			PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / 48), (int) Math.round(gp.player.worldY * 1.0 / 48));
			Main.window.setTitle("Pokemon Game - " + PlayerCharacter.currentMapName);
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
				counter = 0;
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
				showMessage("Your Pokemon were restored\nto full health!");
				gp.player.p.heal();
				gp.ui.subState = 0;
				commandNum = 0;
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				showMessage("Come again soon!");
				gp.ui.subState = 0;
				commandNum = 0;
			}
		}
		
		gp.keyH.wPressed = false;
	}
	
	public void drawShopScreen() {
		switch(subState) {
		case 0: tradeSelect(); break;
		case 1: tradeBuy(); break;
		case 2: tradeSell(); break;
		}
		gp.keyH.wPressed = false;
	}
	
	public void tradeSelect() {
		drawDialogueScreen(true);
		
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 3.5);
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
		g2.drawString("Sell", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				subState = 2;
			}
		}
		y += gp.tileSize;
		g2.drawString("Exit", x, y);
		if (commandNum == 2) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				showMessage("Come again soon!");
				subState = 0;
				commandNum = 0;
			}
		}
	}
	
	public void tradeBuy() {
		drawInventory(npc);
	}
	
	public void tradeSell() {
		
		
	}
	
	private void drawInventory(Entity entity) {
		int x = gp.tileSize * 5;
		int y = gp.tileSize;
		int width = gp.tileSize * 6;
		int height = gp.tileSize * 5;
		
		drawSubWindow(x, y, width, height);
		
		final int slotXstart = x + 20;
		final int slotYstart = y + 20;
		int slotX = slotXstart;
		int slotY = slotYstart;
		int slotSize = (gp.tileSize / 2) + 3;
		
		for (int i = 0; i < entity.inventory.size(); i++) {
			g2.drawImage(npc.inventory.get(i).getImage(), slotX, slotY, null);
			
			slotX += slotSize;
			
			if ((i + 1) % MAX_SHOP_COL == 0) {
				slotX = slotXstart;
				slotY += slotSize;
			}
		}
		
		int cursorX = slotXstart + (slotSize * slotCol);
		int cursorY = slotYstart + (slotSize * slotRow);
		int cursorWidth = gp.tileSize / 2;
		int cursorHeight = gp.tileSize / 2;
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
		
		int dFrameX = x;
		int dFrameY = y + height;
		int dFrameWidth = width;
		int dFrameHeight = gp.tileSize * 5;
		
		int textX = dFrameX + 20;
		int textY = dFrameY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(20F));
		
		int itemIndex = getItemIndexOnSlot();
		
		if (itemIndex < entity.inventory.size()) {
			drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
			String desc = entity.inventory.get(itemIndex).getDesc().replace('\n', ' ');
			
			for (String line : Item.breakString(desc, 35).split("\n")) {
				g2.drawString(line, textX, textY);
				textY += 32;
			}
		}
		
	}

	private int getItemIndexOnSlot() {
		int itemIndex = slotCol + (slotRow*MAX_SHOP_COL);
		return itemIndex;
	}
}
