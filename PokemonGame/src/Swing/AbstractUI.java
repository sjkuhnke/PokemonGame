package Swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Overworld.GamePanel;

public abstract class AbstractUI {
	
	public GamePanel gp;
	public Graphics2D g2;
	public String currentDialogue;
	public int commandNum;
	public int partyNum;
	public int partySelectedNum = -1;
	public Font marumonica;
	public int counter = 0;

	public abstract void showMessage(String message);
	
	public abstract void draw(Graphics2D g2);
	
	public void drawDialogueScreen(boolean above) {
		// WINDOW
		int x;
		int y;
		int width;
		int height;
		if (above) {
			x = gp.tileSize*2;
			y = gp.tileSize/2;
			width = gp.screenWidth - (gp.tileSize*4);
			height = gp.tileSize * 4;
		} else {
			x = 0;
			y = gp.screenHeight - (gp.tileSize*4);
			width = gp.screenWidth;
			height = gp.tileSize * 4;
		}
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,28F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}

	public void drawSubWindow(int x, int y, int width, int height) {
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
	
	public int getRightAlignedTextX(String text, int rightX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = rightX - length; // Calculate the x-coordinate for positioning
	    return x;
	}
	
	public int getCenterAlignedTextX(String text, int centerX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = centerX - length / 2; // Calculate the x-coordinate for center alignment
	    return x;
	}

	public void drawParty() {
		int x = gp.tileSize*3;
		int y = gp.tileSize;
		int width = gp.tileSize*10;
		int height = gp.tileSize*10;
		
		drawSubWindow(x, y, width, height);

		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			if (partyNum > 1) {
				partyNum -= 2;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			if (partyNum < 4) {
				partyNum += 2;
			}
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			if (partyNum % 2 != 0) {
				partyNum--;
			}
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			if (partyNum % 2 == 0) {
				partyNum++;
			}
		}
		
		x += gp.tileSize / 2;
		y += gp.tileSize / 2;
		int startX = x;
		int partyWidth = (int) (gp.tileSize * 4.5);
		int partyHeight = gp.tileSize * 3;
		
		for (int i = 0; i < gp.player.p.team.length; i++) {
			Pokemon p = gp.player.p.team[i];
			if (p != null) {
				Color background;
				if (p.isFainted()) {
					background = new Color(200, 0, 0, 200);
				} else if (p.status == Status.HEALTHY) {
					background = new Color(0, 220, 0, 200);
				} else {
					background = new Color(200, 200, 0, 200);
				}
				if (partySelectedNum == i) background = new Color(100, 100, 220, 200);
				if (partyNum == i) background = background.brighter();
				g2.setColor(background);
				g2.fillRoundRect(x + 12, y + 12, partyWidth - 12, partyHeight - 12, 15, 15);
				g2.setColor(background.brighter());
				g2.drawRoundRect(x + 12, y + 12, partyWidth - 12, partyHeight - 12, 15, 15);
				if (partyNum == i) {
					g2.setColor(Color.RED);
					g2.drawRoundRect(x + 8, y + 8, partyWidth - 4, partyHeight - 4, 18, 18);
				}
				g2.drawImage(p.getSprite(), x + (gp.tileSize / 4), y + (gp.tileSize / 2), null);
				if (p.item != null) {
					g2.drawImage(p.item.getImage(), x + (gp.tileSize / 4) + 8, y + 84, null);
				}
				g2.setColor(Color.BLACK);
				g2.setFont(g2.getFont().deriveFont(24F));
				g2.drawString(p.nickname, getCenterAlignedTextX(p.nickname, (int) (x + (partyWidth * 0.75) - 12)), y + gp.tileSize);
				int barX = (x + gp.tileSize * 2) - 4;
				int barY = (int) (y + (gp.tileSize * 1.25));
				int barWidth = (int) (gp.tileSize * 2.5);
				int barHeight = gp.tileSize / 3;
				g2.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
				double hpRatio = p.currentHP * 1.0 / p.getStat(0);
				g2.setColor(new Color(195, 190, 195));
				g2.fillRoundRect(barX + 3, barY + 3, barWidth - 6, barHeight - 6, 8, 8);
				g2.setColor(getHPBarColor(hpRatio));
				g2.fillRoundRect(barX + 3, barY + 3, (int) (hpRatio * (barWidth - 6)), barHeight - 6, 8, 8);
				
			}
			if (i % 2 == 0) {
				x += partyWidth;
			} else {
				x = startX;
				y += partyHeight;
			}
		}
	}
	
	public Color getHPBarColor(double hpRatio) {
		if (hpRatio < 0.25) {
			return Color.red;
		} else if (hpRatio <= 0.5) {
			return Color.yellow;
		} else {
			return Color.green;
		}
	}
}
