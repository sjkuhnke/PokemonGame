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
	        if (scale != 1) {
	        	image = scaleImage(originalImage, scale);
	        } else {
	        	image = originalImage;
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return image;
	}
	
	public BufferedImage scaleImage(BufferedImage image, int scale) {
		// Calculate the new dimensions based on the scale
        int newWidth = image.getWidth() * scale;
        int newHeight = image.getHeight() * scale;
        
        // Create a new BufferedImage with the scaled dimensions
        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        
        // Draw the original image onto the scaled image
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return result;
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
			if (partyNum < 4 && gp.player.p.team[partyNum + 2] != null) {
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
			if (partyNum % 2 == 0 && gp.player.p.team[partyNum + 1] != null) {
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
				g2.drawImage(p.isFainted() ? p.getFaintedSprite() : p.getSprite(), x + (gp.tileSize / 4), y + (gp.tileSize / 2), null);
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
				String lvText = "Lv. " + p.level;
				g2.setColor(Color.BLACK);
				g2.drawString(lvText, getCenterAlignedTextX(lvText, x + 60), (int) (y + gp.tileSize * 2.75));
				String hpText = p.currentHP + " / " + p.getStat(0);
				g2.drawString(hpText, getCenterAlignedTextX(hpText, (int) (x + (partyWidth * 0.75) - 12)), (int) (y + gp.tileSize * 2.25));
				if (p.status != Status.HEALTHY) {
					g2.drawImage(setup("/battle/" + p.status.toString().toLowerCase(), 2), (int) (x + gp.tileSize * 2.5) + 4, (int) (y + gp.tileSize * 2.25) + 8, null);
				}
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

	public void drawSummary() {
		int x = gp.tileSize*3;
		int y = gp.tileSize / 2;
		int width = gp.tileSize*10;
		int height = gp.tileSize*11;
		
		drawSubWindow(x, y, width, height);
		
		Pokemon p = gp.player.p.team[partyNum];
		
		x += gp.tileSize;
		y += gp.tileSize;
		int startX = x;
		g2.setColor(Color.WHITE);
		
		// Name
		g2.setFont(g2.getFont().deriveFont(36F));
		g2.drawString(p.name, x, y + gp.tileSize / 2);
		
		// Item
		if (p.item != null) {
			x += gp.tileSize * 3.5;
			g2.drawString("@", x, y + gp.tileSize / 2);
			x += gp.tileSize * 0.5;
			g2.drawImage(scaleImage(p.item.getImage(), 2), x, y - gp.tileSize / 4, null);
		}
		
		// Status
		if (p.status != Status.HEALTHY) {
			x += gp.tileSize * 3.25;
			g2.drawImage(setup("/battle/" + p.status.toString().toLowerCase(), 2), x, y, null);
		}
		
		x = startX;
		y += gp.tileSize;
		int startY = y;
		
		// Sprite
		g2.drawImage(p.isFainted() ? p.getFaintedSprite() : p.getSprite(), x - 12, y, null);
		x += gp.tileSize * 2;
		g2.drawImage(setup("/battle/" + p.type1.toString().toLowerCase(), 2), x - 12, y, null);
		if (p.type2 != null) {
			g2.drawImage(setup("/battle/" + p.type2.toString().toLowerCase(), 2), x + 36, y + 36, null);
		}
		
		// Ability
		g2.setFont(g2.getFont().deriveFont(24F));
		x += gp.tileSize * 4.5;
		String abilityLabel = "Ability:";
		g2.drawString(abilityLabel, getCenterAlignedTextX(abilityLabel, x), y);
		y += gp.tileSize / 2;
		String ability = p.ability.toString();
		g2.drawString(ability, getCenterAlignedTextX(ability, x), y);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String[] abilityDesc = Item.breakString(p.ability.desc, 36).split("\n");
		y += gp.tileSize / 4;
		for (String s : abilityDesc) {
			y += gp.tileSize / 2 - 4;
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
		}
		
		// Nickname + Level
		g2.setFont(g2.getFont().deriveFont(24F));
		int barWidth = (int) (gp.tileSize * 3.75);
		int barHeight = gp.tileSize / 4;
		x = startX - gp.tileSize / 2;
		startX = x;
		y = startY;
		y += gp.tileSize * 2.5;
		String name = p.nickname + " Lv. " + p.level;
		g2.drawString(name, getCenterAlignedTextX(name, x + (barWidth / 2)), y);
		
		// Exp
		x = startX;
		y += gp.tileSize / 4;
		g2.setColor(Color.BLACK);
		g2.fillRoundRect(x, y, barWidth, barHeight, 10, 10);
		g2.setColor(new Color(210, 200, 205));
		g2.fillRoundRect(x + 3, y + 3, barWidth - 6, barHeight - 6, 10, 10);
		g2.setColor(Color.BLUE.brighter());
		double xpWidth = (p.exp * 1.0 / p.expMax) * (barWidth - 6);
		g2.fillRoundRect(x + 3, y + 3, (int) xpWidth, barHeight - 6, 2, 2);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		g2.setColor(Color.WHITE);
		y += gp.tileSize * 0.75 - 4;
		String difference = p.level < 100 ? p.expMax - p.exp + "" : "--";
		String xp = difference + " points to lv. up";
		g2.drawString(xp, getCenterAlignedTextX(xp, x + (barWidth / 2)), y);
		
		// Friendship
		g2.setFont(g2.getFont().deriveFont(24F));
		x = (int) (startX + gp.tileSize * 7);
		String friendshipLabel = "Friendship:";
		g2.drawString(friendshipLabel, getCenterAlignedTextX(friendshipLabel, x), y);
		
		y += gp.tileSize / 2;
		String happinessCap = p.happiness >= 255 ? 0 + "" : p.happinessCap + "";
        String friendship = p.happiness + " (" + happinessCap + " remaining)";
        g2.drawString(friendship, getCenterAlignedTextX(friendship, x), y);
        
        g2.setFont(g2.getFont().deriveFont(16F));
        y += gp.tileSize / 2;
        String friendshipDesc = p.getHappinessDesc();
        g2.drawString(friendshipDesc, getCenterAlignedTextX(friendshipDesc, x), y);
        
        // HP Label
        g2.setFont(g2.getFont().deriveFont(24F));
        x = startX;
        y -= gp.tileSize / 4 + 4;
        String hp = p.currentHP + " / " + p.getStat(0) + " HP";
		g2.drawString(hp, getCenterAlignedTextX(hp, x + (barWidth / 2)), y);
		
		// Stats
		y += gp.tileSize;
		for (int i = 0; i < 6; i++) {
			int sY = y;
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(Color.WHITE);
			x = startX;
			String type = Pokemon.getStatType(i);
			type += p.getStat(i);
        	
        	if (i != 0) {
        	    if (p.nature[i - 1] == 1.1) {
        	        g2.setColor(Color.red.darker());
        	        g2.drawString("\u2191", x - 15, y);
        	    } else if (p.nature[i - 1] == 0.9) {
        	    	g2.setColor(Color.blue);
        	    	g2.drawString("\u2193", x - 15, y);
        	    }
        	}
        	g2.drawString(type, x, y);
        	
        	x += gp.tileSize * 1.5;
        	y -= 3;
        	g2.setFont(g2.getFont().deriveFont(16F));
        	g2.setColor(Color.WHITE);
        	String iv = "IV: " + p.getIVs()[i];
        	g2.drawString(iv, x, y);
        	
        	x += gp.tileSize;
        	int statWidth = 3 * gp.tileSize;
        	int statHeight = gp.tileSize / 2;
        	y -= gp.tileSize / 3;
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
		
		// Nature
		x = startX;
		x += gp.tileSize * 3;
		y += gp.tileSize / 4;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		String nature = p.getNature() + " Nature";
		g2.drawString(nature, getCenterAlignedTextX(nature, x), y);
	}
}
