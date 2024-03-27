package Overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Entity.Entity;
import Entity.PlayerCharacter;

public class UI {

	GamePanel gp;
	Graphics2D g2;
	Font arial_40, arial_80B;
	int messageCounter = 0;
	public String currentDialogue = "";
	public int commandNum = 0;
	public int menuNum = 0;
	public int subState = 0;
	public int counter = 0;
	public Entity npc;
	
	
	public UI(GamePanel gp) {
		this.gp = gp;
		
		arial_40 = new Font("Arial", Font.PLAIN, 40);
		arial_80B = new Font("Arial", Font.BOLD, 80);
	}
	
	public void showMessage(String text) {
		gp.gameState = GamePanel.DIALOGUE_STATE;
		gp.ui.currentDialogue = text;
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(arial_40);
		g2.setColor(Color.WHITE);
		
		if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			drawDialogueScreen();
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
			break;
		case 4:
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
		}
		
		// Save
		text = "Save";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 3) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
		}
		
		// Player
		text = "Player";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 4) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
		}
		
		// Map
		text = "Map";
		textY += gp.tileSize;
		g2.drawImage(setup("/menu/" + text.toLowerCase(), 2), textX - gp.tileSize, textY - gp.tileSize / 2, null);
		g2.drawString(text, textX, textY);
		if (menuNum == 5) {
			g2.drawString(">", textX- (25 + gp.tileSize), textY);
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
	
	public void drawDialogueScreen() {
		// WINDOW
		int x = gp.tileSize*2;
		int y = gp.tileSize/2;
		int width = gp.screenWidth - (gp.tileSize*4);
		int height = gp.tileSize * 4;
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,28F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}

	private void drawSubWindow(int x, int y, int width, int height) {
		Color background = new Color(0, 0, 0, 200);
		g2.setColor(background);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		Color border = new Color(255, 255, 255);
		g2.setColor(border);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
		
	}
	
	public int getTextX(String text) {
	    FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = (gp.screenWidth - length) / 2; // Calculate the x-coordinate for centering
	    return x;
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
	
	public void drawShopScreen() {
		switch(subState) {
		case 0: tradeSelect(); break;
		case 1: tradeBuy(); break;
		case 2: tradeSell(); break;
		}
		gp.keyH.wPressed = false;
	}
	
	public void tradeSelect() {
		drawDialogueScreen();
		
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
		}
		y += gp.tileSize;
		g2.drawString("Sell", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
		}
		y += gp.tileSize;
		g2.drawString("Leave", x, y);
		if (commandNum == 2) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				showMessage("Come again soon!");
				gp.ui.subState = 0;
				commandNum = 0;
			}
		}
	}
	
	public void tradeBuy() {
		
	}
	
	public void tradeSell() {
		
		
	}
}
