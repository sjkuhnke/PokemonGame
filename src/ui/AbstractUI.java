package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import entity.Entity;
import overworld.GamePanel;
import overworld.Sound;
import pokemon.*;
import util.Pair;
import util.ToolTip;

public abstract class AbstractUI {
	
	// CORE REFERENCES
	public GamePanel gp;
	public Graphics2D g2;

	// DIALOGUE STATE
	public String currentDialogue;
	public Color textColor;
	public int backgroundOpacity = 180;

	// MENU STATE
	public int commandNum;
	public int subState = 0;
	public int settingsState = 0;
	public int difficultyNum = 0;

	// PARTY STATE
	public int partyNum;
	public int moveSummaryNum = -1;
	public int moveSwapNum = -1;
	public int partySelectedNum = -1;
	public int partySelectedItem = -1;
	public int moveOption = -1;

	// FONTS
	public Font creattion;
	public Font monsier;
	public Font cryptik;
	public Font consolas;
	public Font kids;

	// GENERAL STATE
	public int counter = 0;
	public boolean showMoveOptions;
	public Move currentMove;
	public Item currentItem;
	public boolean showIVOptions;
	public boolean showStatusOptions;
	public boolean gauntlet;

	// CONTROL REBINDING
	public int rebindingControl = -1;
	public boolean waitingForKey = false;

	// BOX STATE
	public int boxSwapNum;
	public boolean showBoxParty;
	public boolean showBoxSummary;
	public Pokemon itemSwapP;

	// TASK SYSTEM
	public Task currentTask;
	public ArrayList<Task> tasks;
	public boolean checkTasks;

	// BETTING STATE
	public static int MAX_PARLAYS = 6;
	public boolean showParlays;
	public int battleBet = Player.BET_INC;
	public int parlayBet = Player.BET_INC;
	public int[] parlays;
	public boolean sheetFilled;

	// ANIMATION
	public int pulseCounter = 0;

	// NPC REFERENCE
	public Entity npc;

	// NICKNAME STATE
	public StringBuilder nickname = new StringBuilder();
	public int nicknaming = -1;

	// IMAGES
	public BufferedImage ballIcon;
	public Color coinColor = Color.WHITE;
	
	// IMAGE CACHES
	public static BufferedImage[] silhouettes;
	public static Image[] faintedSprites;

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
			if (this instanceof UI) {
				x = gp.tileSize*2;
				y = (int) (gp.screenHeight - (gp.tileSize*4.5));
				width = gp.screenWidth - (gp.tileSize*4);
			} else {
				x = 0;
				y = gp.screenHeight - (gp.tileSize*4);
				width = gp.screenWidth;
			}
			
			height = gp.tileSize * 4;
		}
		
		drawSubWindow(x, y, width, height);
		float fontSize = getFontSize();
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,fontSize));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}
	
	private float getFontSize() {
		String fontName = g2.getFont().getFamily();
		if (consolas != null && fontName.equalsIgnoreCase(consolas.getFamily())) {
			return 22F;
		} else {
			return 28F;
		}
	}
	
	public void drawSubWindow(int x, int y, int width, int height, int opacity) {
		Color background = new Color(0, 0, 0, opacity);
		g2.setColor(background);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		Color border = new Color(255, 255, 255);
		g2.setColor(border);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
	}

	public void drawSubWindow(int x, int y, int width, int height) {
		drawSubWindow(x, y, width, height, 200);
	}
	public void drawButton(int x, int y, char key, boolean pressed) {
		int width = gp.tileSize;
		int height = gp.tileSize;
        if (pressed) {
        	g2.setColor(new Color(255,255,255,150));
            g2.fillRoundRect(x, y, width, height, 35, 35);
    		Color background = new Color(0, 0, 0, 200);
    		g2.setColor(background);
    		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
        } else {
        	Color background = new Color(0, 0, 0, 200);
    		g2.setColor(background);
    		g2.fillRoundRect(x, y, width, height, 35, 35);
    		
    		Color border = new Color(255, 255, 255);
    		g2.setColor(border);
    		g2.setStroke(new BasicStroke(5));
    		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
        }
        x += 14;
        y += 32;
        g2.setColor(pressed ? Color.black: Color.white);
        g2.setFont(g2.getFont().deriveFont(30.F));
        g2.drawString(String.valueOf(key), x, y);
        
	}
		
	public void drawKeyStrokes() {
		if (gp == null || !gp.keyH.shiftPressed) return;
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
		
		drawButton(gp.tileSize, 0, 'W', gp.keyH.kWPressed);
		drawButton(0, gp.tileSize, 'A', gp.keyH.kAPressed);
		drawButton(gp.tileSize, gp.tileSize,'S', gp.keyH.kSPressed);
		drawButton(gp.tileSize * 2, gp.tileSize,'D', gp.keyH.kDPressed);
		
		drawButton(gp.tileSize, gp.tileSize * 2, '\u2191', gp.keyH.kUpPressed);
		drawButton(0, gp.tileSize * 3, '\u2190', gp.keyH.kLeftPressed);
		drawButton(gp.tileSize, gp.tileSize * 3,'\u2193', gp.keyH.kDownPressed);
		drawButton(gp.tileSize * 2, gp.tileSize * 3 ,'\u2192', gp.keyH.kRightPressed);		
	}
	
	public BufferedImage setup(String imageName, double scale) {
	    BufferedImage image = null;
	    
	    try {
	        // Load the original image
	        BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
	        if (scale != 1) {
	        	image = scaleImage(originalImage, scale);
	        } else {
	        	image = originalImage;
	        }
	        
	    } catch (IOException | IllegalArgumentException e) {
	    	System.out.println(imageName + " is null!");
	        e.printStackTrace();
	    }
	    
	    return image;
	}
	
	public BufferedImage scaleImage(BufferedImage image, double scale) {
		// Calculate the new dimensions based on the scale
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);
        
        // Create a new BufferedImage with the scaled dimensions
        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        
        // Draw the original image onto the scaled image
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return result;
	}
	
	public BufferedImage makeSilhouette(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage silhouette = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// Define the light grey color
		int lightGrey = new Color(211, 211, 211).getRGB(); // Light grey color with RGB (211, 211, 211)
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(x, y);
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
		drawParty(null);
	}
	
	public void drawParty(Move move, Item item) {
		drawParty(gp.tileSize*3, gp.tileSize, item, move);
	}
	
	public void drawParty(Item item) {
		drawParty(null, item);
	}

	public void drawParty(int x, int y, Item item, Move move) {
		int width = gp.tileSize*10;
		int height = gp.tileSize*10;
		
		drawSubWindow(x, y, width, height);
		
		if (!showMoveOptions && !showIVOptions && !showBoxSummary && !showStatusOptions &&
				(currentTask == null || currentTask.type == Task.PARTY || currentTask.type == Task.REGIONAL_TRADE || currentTask.type == Task.EVO_INFO ||
				currentTask.type == Task.NURSERY_DEPOSIT)) {
			int maxPartyIndex = 0;
			for (int i = 1; i < gp.player.p.team.length; i++) {
				if (gp.player.p.team[i] == null) {
					break;
				}
				maxPartyIndex++;
			}
			if (gp.gameState == GamePanel.BOX_STATE && boxSwapNum >= 0 && maxPartyIndex < 5) maxPartyIndex++;
			
			if (gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				if (partyNum > 1) {
					partyNum -= 2;
				}
			}
			
			if (gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				if (partyNum < 4 && partyNum + 2 <= maxPartyIndex) {
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
				if (partyNum % 2 == 0 && partyNum + 1 <= maxPartyIndex) {
					partyNum++;
				}
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
				boolean egg = p instanceof Egg;
				Color background;
				if (p.isFainted() && !egg) {
					background = new Color(200, 0, 0, 200);
				} else if (p.status == Status.HEALTHY) {
					background = new Color(0, 220, 0, 200);
				} else {
					background = p.status.getColor();
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
				g2.drawImage(p.isFainted() && !egg ? p.getFaintedSprite() : p.getSprite(), x + (gp.tileSize / 4), y + (gp.tileSize / 2), null);
				
				boolean drawItemSelect = partySelectedItem == i || (gp.gameState == GamePanel.BOX_STATE && itemSwapP == p);
				if (drawItemSelect || (partyNum == i && (partySelectedItem != -1 || gp.gameState == GamePanel.BOX_STATE && itemSwapP != null))) {
					drawItemSelectBackground(x + 14, y + 78);
				}
				if (p.item != null) {
					int itemX = x + (gp.tileSize / 4) + 8;
					int itemY = y + 84;
					g2.drawImage(p.item.getOutlineImage(), itemX - 2, itemY - 2, null);
					g2.drawImage(p.item.getImage(), itemX, itemY, null);
				}
				if (drawItemSelect) {
					drawItemSelectBorder(x + 14, y + 78);
				}
				
				g2.setColor(Color.BLACK);
				g2.setFont(g2.getFont().deriveFont(24F));
				g2.drawString(p.getNickname(), getCenterAlignedTextX(p.getNickname(), (int) (x + (partyWidth * 0.75) - 12)), y + gp.tileSize);
				
				if (!egg) {
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
					int canUseItem = move == null ? p.canUseItem(item) : p.canLearnMove(move);
					String hpText = canUseItem == -1 ? p.currentHP + " / " + p.getStat(0) : canUseItem == 0 ? "NOT ABLE" : canUseItem == 2 ? (item != null && item.isTM()) || move != null ? "LEARNED" : "NOT ABLE" : "ABLE";
					g2.drawString(hpText, getCenterAlignedTextX(hpText, (int) (x + (partyWidth * 0.75) - 12)), (int) (y + gp.tileSize * 2.25));
					if (p.status != Status.HEALTHY) {
						g2.drawImage(p.status.getImage(), (int) (x + gp.tileSize * 2.5) + 4, (int) (y + gp.tileSize * 2.25) + 8, null);
					}
				}
			} else {
				if (partyNum == i) {
					g2.setColor(Color.RED);
					g2.drawRoundRect(x + 8, y + 8, partyWidth - 4, partyHeight - 4, 18, 18);
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
	
	public void drawItemSelectBackground(int x, int y) {
		g2.setColor(new Color(10, 10, 10, 180));
		g2.fillOval(x, y, 36, 36);	
	}
	
	public void drawItemSelectBorder(int x, int y) {
		g2.setStroke(new BasicStroke(5));
		g2.setColor(Color.WHITE);
		g2.drawOval(x, y, 36, 36);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(new Color(120, 120, 235));
		g2.drawOval(x, y, 36, 36);
		g2.setStroke(new BasicStroke(5));
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
	
	public void drawSummary(Pokemon foe) {
		drawSummary(gp.player.p.team[partyNum], foe);
	}

	public void drawSummary(Pokemon p, Pokemon foe) {
		int x = gp.tileSize*3;
		int y = gp.tileSize / 2;
		int width = gp.tileSize*10;
		int height = gp.tileSize*11;
		
		boolean egg = p instanceof Egg;
		
		drawSubWindow(x, y, width, height);
		
		int windowX = x;
		int windowY = y;
		
		// ID
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setColor(p.getDexNoColor());
		g2.setFont(g2.getFont().deriveFont(20F));
		g2.drawString(p.getFormattedDexNo(), x, y);
		
		g2.setColor(Color.WHITE);
		
		// Ball
		x -= gp.tileSize / 8;
		y += gp.tileSize / 4;
		if (p.ball != null) g2.drawImage(p.ball.getImage(), x, y, null);
		
		// Name
		x += gp.tileSize / 8;
		x += gp.tileSize / 2;
		int startX = x;
		g2.setFont(g2.getFont().deriveFont(36F));
		g2.setFont(g2.getFont().deriveFont(getFontSize(p.name(), (float) (gp.tileSize * 4))));
		g2.drawString(p.name(), x, y + gp.tileSize / 2);
		
		// Item
		x += gp.tileSize * 3.5;
		if (p.item != null) {
			g2.drawString("@", x, y + gp.tileSize / 2);
			x += gp.tileSize * 0.5;
			g2.drawImage(scaleImage(p.item.getImage(), 2), x, y - gp.tileSize / 4, null);
			y += gp.tileSize;
			g2.setFont(g2.getFont().deriveFont(12F));
			String item = foe == null ? "[A] Take " + p.item.toString() : p.item.toString();
			g2.drawString(item, getCenterAlignedTextX(item, x + 8), y);
			x -= gp.tileSize * 0.5;
			y -= gp.tileSize;
		}
		
		// Status
		if (p.status != Status.HEALTHY) {
			x += gp.tileSize * 2.5;
			g2.drawImage(p.status.getImage(), x, y, null);
		}
		
		// Slot
		if (partyNum >= 0
				&&
				!(gp.gameState == GamePanel.BOX_STATE && gp.ui.showBoxSummary && !gp.ui.showBoxParty)) {
			int slotX = windowX + width - gp.tileSize;
			int slotY = windowY;
			drawSubWindow(slotX, slotY, gp.tileSize, gp.tileSize);
			slotX += gp.tileSize / 2;
			slotY += gp.tileSize * 0.75;
			String slotString = String.valueOf(partyNum + 1);
			g2.setFont(g2.getFont().deriveFont(30F));
			g2.drawString(slotString, getCenterAlignedTextX(slotString, slotX - 1), slotY);
		}
		
		x = startX;
		y += gp.tileSize;
		int startY = y;
		
		// Sprite
		g2.drawImage(p.isFainted() && !egg ? p.getFaintedSprite() : p.getSprite(), x - 12, y, null);
		x += gp.tileSize * 2;
		if (!egg) {
			g2.drawImage(p.type1.getImage2(), x - 12, y, null);
			if (p.type2 != null) {
				g2.drawImage(p.type2.getImage2(), x + 36, y + 36, null);
			}
		}
		
		// Ability
		g2.setFont(g2.getFont().deriveFont(24F));
		x += gp.tileSize * 4.5;
		String abilityLabel = egg ? "Hatch Status:" : "Ability:";
		g2.drawString(abilityLabel, getCenterAlignedTextX(abilityLabel, x), y);
		y += gp.tileSize / 2;
		String ability = egg ? ((Egg)p).getSteps() + " cycles remaining" : p.ability.toString();
		g2.drawString(ability, getCenterAlignedTextX(ability, x), y);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String desc = egg ? ((Egg)p).getHatchDesc() : p.ability.desc;
		String[] abilityDesc = Item.breakString(desc, 32).split("\n");
		y += gp.tileSize / 4;
		for (String s : abilityDesc) {
			y += gp.tileSize / 2 - 4;
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
		}
		
		// Nickname + Level
		int barWidth = (int) (gp.tileSize * 3.75);
		int barHeight = gp.tileSize / 4;
		g2.setFont(g2.getFont().deriveFont(24F));
		x = startX - gp.tileSize / 2;
		startX = x;
		y = startY;
		y += gp.tileSize * 2.5;
		String name = egg ? "Egg" : p.nickname + " Lv. " + p.level;
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
		String difference = egg ? "??" :  p.level < 100 ? p.expMax - p.exp + "" : "--";
		String xp = difference + " points to lv. up";
		g2.drawString(xp, getCenterAlignedTextX(xp, x + (barWidth / 2)), y);
		
		
		// Friendship
		g2.setFont(g2.getFont().deriveFont(24F));
		x = (int) (startX + gp.tileSize * 7);
		String friendshipLabel = "Friendship:";
		g2.drawString(friendshipLabel, getCenterAlignedTextX(friendshipLabel, x), y);
		
		y += gp.tileSize / 2;
		String happinessCap = p.happiness >= 255 ? 0 + "" : p.happinessCap + "";
        String friendship = egg ? "???" : p.happiness + " (" + happinessCap + " remaining)";
        g2.drawString(friendship, getCenterAlignedTextX(friendship, x), y);
        
        g2.setFont(g2.getFont().deriveFont(16F));
        y += gp.tileSize / 2;
        String friendshipDesc = egg ? "" : p.getHappinessDesc();
        g2.drawString(friendshipDesc, getCenterAlignedTextX(friendshipDesc, x), y);
        
        // HP Label
        g2.setFont(g2.getFont().deriveFont(24F));
        x = startX;
        y -= gp.tileSize / 4 + 4;
        String hp = egg ? "" : p.currentHP + " / " + p.getStat(0) + " HP";
    	g2.drawString(hp, getCenterAlignedTextX(hp, x + (barWidth / 2)), y);
    	
		// Stats
		y += gp.tileSize;
		for (int i = 0; i < 6; i++) {
			int sY = y;
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(Color.WHITE);
			x = startX;
			String type = Pokemon.getStatType(i, false);
			int stat = p.getStat(i);
			type += egg ? "??" : stat;
        	
        	if (i != 0 && !egg) {
        	    if (p.nat.getStat(i - 1) == 1.1) {
        	        g2.setColor(Color.red.darker());
        	        g2.drawString("\u2191", x - 15, y);
        	    } else if (p.nat.getStat(i - 1) == 0.9) {
        	    	g2.setColor(Color.blue);
        	    	g2.drawString("\u2193", x - 15, y);
        	    }
        	}
        	g2.drawString(type, x, y);
        	
        	x += gp.tileSize * 1.5;
        	y -= 3;
        	g2.setFont(g2.getFont().deriveFont(16F));
        	g2.setColor(Color.WHITE);
        	String iv = egg ? "??" : p.getIVs()[i] + "";
        	String siv = "IV: " + iv;
        	g2.drawString(siv, x, y);
        	
        	x += gp.tileSize;
        	int statWidth = 3 * gp.tileSize;
        	int statHeight = gp.tileSize / 2;
        	y -= gp.tileSize / 3;
        	g2.setColor(Color.BLACK);
        	g2.fillRect(x, y, statWidth, statHeight);
        	g2.setColor(Color.WHITE);
        	g2.fillRect(x + 2, y + 2, statWidth - 4, statHeight - 4);
        	int baseStat = egg ? 0 : p.getBaseStat(i);
        	double bar = Math.min(baseStat * 1.0 / 200, 1.0);
        	g2.setColor(Pokemon.getColor(p.getBaseStat(i)));
        	g2.fillRect(x + 2, y + 2, (int) ((statWidth - 4) * bar), statHeight - 4);
        	x += 4;
        	y += (gp.tileSize / 2) - 4;
        	g2.setColor(Color.BLACK);
        	String baseStatString = egg ? "??" : baseStat + "";
        	g2.drawString(baseStatString, x, y);
        	
        	y = sY + gp.tileSize / 2;
		}
		
		// Nature
		x = startX;
		y += gp.tileSize / 4;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		String nature = p.getNature() + " Nature";
		g2.drawString(nature, x, y);
		
		// Met At
		x += gp.tileSize * 3;
		int metY = y;
		if (p.metAt != null) {
			int centerX = x + (int) (gp.tileSize * 1.25);
			y -= gp.tileSize / 4;
			g2.setFont(g2.getFont().deriveFont(22F));
			String metAt = "Met at:";
			String metAtLoc = p.metAt;
			g2.drawString(metAt, getCenterAlignedTextX(metAt, centerX), y);
			
			y += gp.tileSize / 2;
			g2.setFont(g2.getFont().deriveFont(20F));
			g2.drawString(metAtLoc, getCenterAlignedTextX(metAtLoc, centerX), y);
		}
		
		// Moves
		if (!egg) {
			x += gp.tileSize * 3;
			y = metY;
			y -= gp.tileSize * 3.5;
			int moveWidth = gp.tileSize * 3;
			int moveHeight = (int) (gp.tileSize * 0.75);
			g2.setFont(g2.getFont().deriveFont(18F));
			for (int i = 0; i < 4; i++) {
				Moveslot m = p.moveset[i];
				if (m != null) {
					if (i == moveSwapNum) {
						g2.setColor(new Color(245, 225, 210));
						g2.fillRoundRect(x, y, moveWidth, moveHeight, 10, 10);
						g2.setColor(g2.getColor().darker());
						g2.drawRoundRect(x, y, moveWidth, moveHeight, 10, 10);
					} else {
						PType mtype = m.move.getType(p, Pokemon.field);
						Color color = mtype.getColor();
						g2.setColor(color);
						g2.fillRoundRect(x, y, moveWidth, moveHeight, 10, 10);
					}
					g2.setColor(Color.BLACK);
			        String text = m.move.toString();
			        g2.drawString(text, getCenterAlignedTextX(text, (x + moveWidth / 2)), y + gp.tileSize / 3);
			        g2.setColor(m.getPPColor());
			        String pp = m.currentPP + " / " + m.maxPP;
			        g2.drawString(pp, getCenterAlignedTextX(pp, (x + moveWidth / 2)), (int) (y + gp.tileSize * 0.75) - 3);
			        if (moveSummaryNum == i) {
			            g2.setColor(Color.RED);
			            g2.drawRoundRect(x - 2, y - 2, moveWidth + 4, moveHeight + 4, 10, 10);
			        }
					y += gp.tileSize;
				}
			}
		}
		
		if (moveSummaryNum > -1) {
			int moveX = gp.tileSize * 3;
			int moveY = gp.tileSize / 2;
			drawMoveSummary(moveX, moveY, p, foe, p.moveset[moveSummaryNum], null);
		}
		
		if (nicknaming < 0) {
			if (!egg && gp.keyH.wPressed && moveSummaryNum == -1) {
				gp.keyH.wPressed = false;
				moveSummaryNum = 0;
			}
			
			if (!egg && gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				moveSummaryNum--;
				if (moveSummaryNum < 0) {
					int index = 3;
					while (p.moveset[index] == null) {
						index--;
					}
					moveSummaryNum = index;
				}
			}
			
			if (!egg && gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				if (moveSummaryNum < 3 && p.moveset[moveSummaryNum + 1] != null) {
					moveSummaryNum++;
				} else {
					moveSummaryNum = 0;
				}
			}
			
			if (moveSummaryNum < 0 && !showBoxSummary && !gp.battleUI.showFoeSummary && !gp.simBattleUI.showFoeSummary) {
				if (gp.keyH.leftPressed) {
					gp.keyH.leftPressed = false;
					if (partyNum > 0) {
						partyNum--;
					} else {
						int index = 5;
						while (gp.player.p.team[index] == null) {
							index--;
						}
						partyNum = index;
					}
				}
				
				if (gp.keyH.rightPressed) {
					gp.keyH.rightPressed = false;
					if (partyNum < 5 && gp.player.p.team[partyNum + 1] != null) {
						partyNum++;
					} else {
						partyNum = 0;
					}
				}
			}
			
			if (gp.keyH.aPressed) {
				gp.keyH.aPressed = false;
				if (p.playerOwned()) {
					if (moveSummaryNum < 0) {
						if (p.item != null && foe == null) {
							gp.player.p.takeItem(p, this);
						}
					} else {
						if (moveSwapNum > -1) {
							if (moveSummaryNum != moveSwapNum) {
								Moveslot temp = p.moveset[moveSummaryNum];
								p.moveset[moveSummaryNum] = p.moveset[moveSwapNum];
								p.moveset[moveSwapNum] = temp;
							}
							moveSwapNum = -1;
						} else {
							moveSwapNum = moveSummaryNum;
						}
					}
				}
			}
			
			if (!egg && gp.keyH.dPressed) {
				gp.keyH.dPressed = false;
				if (foe == null) {
					nickname = new StringBuilder(p.nickname);
					setNicknaming(true);
				}
			}
			
			String wText = moveSummaryNum < 0 && !egg ? "Moves" : null;
			String aText = moveSummaryNum < 0 ? p.item == null || foe != null ? null : "Take" : p.playerOwned() ? "Swap" : null;
			String dText =
				gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.SIM_BATTLE_STATE || moveSummaryNum > -1
				? gp.battleUI.showFoeSummary || gp.simBattleUI.showFoeSummary
					? "Back"
					: null
				: egg
					? null
					: "Name";
			String cText = gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.SIM_BATTLE_STATE
				? "Calc"
				: null;
			
			drawToolTips(wText, aText, "Back", dText, cText);
		} else {
			currentDialogue = "Change " + p.name() + "'s nickname?";
			drawDialogueScreen(true);
			setNickname(p, false);
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					p.nickname = nickname.toString().trim();
					nickname = new StringBuilder();
					if (p.nickname == null || p.nickname.trim().isEmpty()) p.nickname = p.name();
					nicknaming = -1;
				}
				drawToolTips("OK", null, null, null);
			}
		}
	}

	public void drawMoveSummary(int x, int y, Pokemon p, Pokemon foe, Moveslot moveslot, Move move) {
		int width = gp.tileSize*10;
		int height = gp.tileSize*6;
		drawSubWindow(x, y, width, height, 255);
		char check = '\u2022';
		char bullet = '\u2023';
		
		if (moveslot != null) move = moveslot.move;
		x += gp.tileSize * 0.75;
		int startX = x;
		y += gp.tileSize * 1.25;
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		g2.setFont(g2.getFont().deriveFont(getFontSize(move.toString(), gp.tileSize * 4)));
		g2.drawString(move.toString(), x, y);
		
		x += gp.tileSize * 3.5;
		y -= gp.tileSize * 0.75;
		PType type = move.mtype;
		if (p != null && p.headbuttCrit >= 0) {
			type = move.getType(p, Pokemon.field);
		}
		g2.drawImage(type.getImage2(), x, y, null);
		
		x += gp.tileSize * 1.5;
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString("Power: " + move.formatbp(p, foe, Pokemon.field), x, y);
		
		x += gp.tileSize * 2.5;
		g2.drawString("Acc: " + move.getAccuracy(p, foe, Pokemon.field), x, y);
		
		x = startX;
		y += gp.tileSize * 1.25;
		g2.drawString("Cat", x, y);
		
		x += gp.tileSize * 1.25;
		y -= gp.tileSize / 2;
		g2.drawImage(scaleImage(move.getCategoryIcon(), 2), x, y, null);
		
		x += gp.tileSize * 2;
		int startY = y + gp.tileSize / 2;
		g2.setFont(g2.getFont().deriveFont(18F));
		if (move.contact) {
			g2.drawString(bullet + " Makes Contact", x, y);
			y += gp.tileSize / 2;
		}
		int priority = move.getPriority(p);
		if (priority != 0) {
			g2.drawString(bullet + (priority > 0 ? " +" : " ") + priority + " Priority", x, y);
			y += gp.tileSize / 2;
		}
		if (move.critChance > 0) {
			g2.drawString(bullet + " +" + move.critChance + " Crit Chance", x, y);
			y += gp.tileSize / 2;
		}
		
		x += gp.tileSize * 3;
		y = startY;
		g2.setFont(g2.getFont().deriveFont(24F));
		Color ppColor = moveslot != null ? moveslot.getPPColor() : Color.BLACK;
		if (ppColor == Color.BLACK) {
			ppColor = Color.white;
		} else {
			ppColor = ppColor.brighter();
		}
		g2.setColor(ppColor);
		String ppString = moveslot != null ? moveslot.currentPP + " / " + moveslot.maxPP + " PP" : move.pp + " PP (Max " + move.pp * 8 / 5 + ")";
		g2.drawString(ppString, x, y);
		
		g2.setColor(Color.WHITE);
		x = (int) (startX - (gp.tileSize * 0.75));
		x += gp.tileSize * 5;
		y += gp.tileSize;
		String[] desc = Item.breakString(move.getDescription(), 46).split("\n");
		int offset = (int) (gp.tileSize * 0.6);
		if (desc.length > 3) {
			g2.setFont(g2.getFont().deriveFont(20F));
			desc = Item.breakString(move.getDescription(), 56).split("\n");
			offset = (int) (gp.tileSize * 0.5);
		}
		y += (3 - desc.length) * (gp.tileSize / 4);
		for (String s : desc) {
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
			y += offset;
		}
		
		y = startY + gp.tileSize * 3;
		g2.setFont(g2.getFont().deriveFont(22F));
		StringBuilder flags = new StringBuilder();
		
		List<Pair<Boolean, String>> moveFlags = Arrays.asList(
			new Pair<>(move.isSlicing(), "Slicing"),
			new Pair<>(move.isPunching(), "Punching"),
			new Pair<>(move.isTail(), "Tail"),
			new Pair<>(Move.getNoComboMoves().contains(move), "No Combo"),
			new Pair<>(Move.getSound().contains(move), "Sound"),
			new Pair<>(move.isBiting(), "Biting"),
			new Pair<>(move.isHealing(), "Healing")
		);
		
		for (Pair<Boolean, String> flag : moveFlags) {
			if (flag.getFirst()) {
				flags.append("  ");
				flags.append(check).append(" ").append(flag.getSecond());
			}
		}
		
		if (flags.length() > 0) {
			g2.drawString(flags.toString(), getCenterAlignedTextX(flags.toString(), x), y);
		}
		
		x = startX + width - gp.tileSize * 2;
		y -= gp.tileSize;
		
		if (move.isTM() && gp.player.p.hasTM(move)) {
			g2.drawImage(Item.HM01.getImage2(), x, y, null);
		}
	}

	public float getFontSize(String text, float targetWidth) {
		float fontSize = g2.getFont().getSize2D(); // Default font size

	    FontMetrics metrics = g2.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (int) fontSize));
	    int textWidth = metrics.stringWidth(text);

	    // Reduce font size until the text fits within the target width
	    while (textWidth > targetWidth && fontSize > 1) {
	        fontSize -= 1; // Decrease font size
	        metrics = g2.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (int) fontSize));
	        textWidth = metrics.stringWidth(text);
	    }

	    return fontSize;
	}
	
	public int getTextWidth(String text) {
		float fontSize = g2.getFont().getSize2D(); // Default font size

	    FontMetrics metrics = g2.getFontMetrics(new Font(g2.getFont().getFamily(), g2.getFont().getStyle(), (int) fontSize));
	    int textWidth = metrics.stringWidth(text);
	    return textWidth;
	}

	public void setNickname(Pokemon p, boolean above) {
		int x = gp.tileSize * 3;
		int y = gp.tileSize * 5;
		int width = gp.tileSize*10;
		int height = gp.tileSize*3;
		
		drawSubWindow(x, y, width, height);
		
		int topY = above ? y - gp.tileSize * 4 : y + height;
		
		drawPokemonWindow(p, topY);
		
		g2.setFont(g2.getFont().deriveFont(36F));
		
		x += gp.tileSize / 2 + 4;
		y += gp.tileSize;
		int charWidth = 36;
		for (int i = 0; i < 12; i++) {
			String currentChar = i < nickname.length() ? nickname.charAt(i) + "" : null;
	        if (currentChar != null) g2.drawString(currentChar, x, y);
	        g2.setStroke(new BasicStroke(1));
	        g2.drawLine(x - 6, y + 5, x + 20, y + 5);
	        if (i == nickname.length() && nicknaming == 1) {
	        	int x2 = x;
	        	int y2 = y + gp.tileSize / 3;
	        	int width2 = gp.tileSize / 4;
	        	int height2 = gp.tileSize / 4;
	        	g2.fillPolygon(new int[] {x2, (x2 + width2), (x2 + width2 / 2)}, new int[] {y2 + height2, y2 + height2, y2}, 3);
	        }
	        x += charWidth;
	    }
		g2.setStroke(new BasicStroke(3));
		y += gp.tileSize * 1.5;
		x = getCenterAlignedTextX("Confirm", gp.tileSize * 3 + width / 2);
		g2.drawString("Confirm", x, y);
		if (nicknaming == 0) {
			g2.drawRoundRect(x - 8, (int) (y - gp.tileSize * 0.65), (int) (gp.tileSize * 2.3), (int) (gp.tileSize * 0.8), 12, 12);
		}
		
		if (gp.keyH.upPressed && nicknaming == 0) {
			gp.keyH.upPressed = false;
			nicknaming = 1;
		}
		
		if (gp.keyH.downPressed && nicknaming == 1) {
			gp.keyH.downPressed = false;
			nicknaming = 0;
		}
	}
	
	private void drawPokemonWindow(Pokemon p, int topY) {
		if (p == null) return;
		
		int centerX = gp.screenWidth / 2;
		int width = gp.tileSize * 4;
		int height = gp.tileSize * 4;
		int topX = centerX - width / 2;
		
		drawSubWindow(topX, topY, width, height);
		topX += gp.tileSize / 4;
		topY += gp.tileSize / 4;
		g2.drawImage(p.getFrontSprite(), topX, topY, null);
		
	}

	public void drawMoveOptions(Move m, Pokemon p) {
		if (p.knowsMove(m)) {
			showMoveOptions = false;
			gp.battleUI.currentTask = null;
			return;
		}
		drawMoveOptions(m, p, p.nickname + " wants to learn:");
	}
	
	public void drawMoveOptions(Move m, Pokemon p, String header) {
		ArrayList<Move> movebankList = Pokemon.getMovebankAtLevel(p.id, p.level);
		
		int x = gp.tileSize * 4;
		int y = 0;
		int width = gp.tileSize * 8;
		int height = (int) (gp.tileSize * 4.5);
		if (m == null) {
			y += gp.tileSize;
		} else {
			height += gp.tileSize * 1.5;
		}
		drawSubWindow(x, y, width, height);
		if (moveOption == 0) {
			drawMoveSummary(x - gp.tileSize, gp.tileSize * 6, p, null, null, m);
		}
		int moveWidth = gp.tileSize * 10 / 3;
		int moveHeight = (int) (gp.tileSize * 1.25);
		
		x += gp.tileSize / 2;
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(header, getCenterAlignedTextX(header, (x - gp.tileSize / 2) + width / 2), y);
		y += gp.tileSize / 3;
		
		if (m != null) {
			x += gp.tileSize * 11 / 6;
			PType mtype = m.getType(p, Pokemon.field);
			Color color = mtype.getColor();
			g2.setColor(color);
			g2.fillRoundRect(x, y, moveWidth, moveHeight, 10, 10);
			g2.setColor(Color.BLACK);
	        String text = m.toString();
	        g2.drawString(text, getCenterAlignedTextX(text, x + moveWidth / 2), y + gp.tileSize / 2);
	        String pp = m.pp + " / " + m.pp;
	        g2.drawString(pp, getCenterAlignedTextX(pp, (x + moveWidth / 2)), y + gp.tileSize);
	        if (moveOption == 0) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x - 2, y - 2, moveWidth + 4, moveHeight + 4, 10, 10);
	        }
			y += gp.tileSize * 5 / 3;
			x -= gp.tileSize * 11 / 6;
		}
		int startX = x;
		int max = 0;
		
		for (int i = 1; i <= p.moveset.length; i++) {
			Moveslot ms = p.moveset[i - 1];
			if (ms != null) {
				g2.setFont(g2.getFont().deriveFont(24F));
				PType mtype = ms.move.getType(p, Pokemon.field);
				Color color = mtype.getColor();
				boolean canRelearn = Move.getMoveTutorMoves().contains(ms.move) || movebankList.contains(ms.move) ||
						(gp.player.p.hasTM(ms.move) && Pokemon.getLearned(p.id - 1, Item.getTMMoves().indexOf(ms.move)));
				if (!canRelearn) {
		        	g2.setPaint(new GradientPaint(x, y, color, x + moveWidth, y + moveHeight, new Color(245, 225, 210)));
		        } else {
		        	g2.setColor(color);	
		        }
				g2.fillRoundRect(x, y, moveWidth, moveHeight, 10, 10);
				g2.setColor(Color.BLACK);
		        String text = ms.move.toString();
		        g2.drawString(text, getCenterAlignedTextX(text, x + moveWidth / 2), y + gp.tileSize / 2);
		        g2.setColor(ms.getPPColor());
		        String pp = ms.currentPP + " / " + ms.maxPP;
		        g2.drawString(pp, getCenterAlignedTextX(pp, (x + moveWidth / 2)), y + gp.tileSize);
		        if (moveOption == i) {
		            g2.setColor(Color.RED);
		            g2.drawRoundRect(x - 2, y - 2, moveWidth + 4, moveHeight + 4, 10, 10);
		            drawMoveSummary((int) (startX - gp.tileSize * 1.5), gp.tileSize * 6, p, null, ms, null);
		        }
		        if (i % 2 == 1) {
		        	x += moveWidth + gp.tileSize / 3;
		        } else {
		        	x = startX;
		        	y += gp.tileSize * 1.5;
		        }
		        max++;
			}
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			moveOption -= 2;
			if (m == null) {
				moveOption = moveOption < 1 ? 1 : moveOption;
			} else {
				moveOption = moveOption < 0 ? 0 : moveOption;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			moveOption = moveOption > 0 || m == null ? moveOption + 2 : moveOption + 1;
			if (moveOption > max) moveOption = max;
		}
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			if ((moveOption % 2 == 0 && moveOption != 0) || (moveOption == 1 && m != null)) {
				moveOption--;
			}
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			if (moveOption % 2 == 1 && moveOption < max || (moveOption == 0 && m != null)) {
				moveOption++;
			}
		}
		
		if (gp.keyH.wPressed && m != null) {
			gp.keyH.wPressed = false;
			if (moveOption >= 0) {
				if (gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.RARE_CANDY_STATE) {
					if (moveOption == 0) {
						Task t = Task.createTask(Task.TEXT, p.nickname + " did not learn " + m.toString() + ".");
						Task.insertTask(t, 0);
					} else {
						Task t = Task.createTask(Task.TEXT, p.nickname + " learned " + m.toString() + " and forgot " + p.moveset[moveOption - 1].move.toString() + "!");
						Task.insertTask(t, 0);
					}
				} else {
					if (moveOption == 0) {
						gp.ui.showMessage(p.nickname + " did not learn " + m.toString() + ".");
					} else {
						gp.ui.showMessage(Item.breakString(p.nickname + " learned " + m.toString() + " and forgot " + p.moveset[moveOption - 1].move.toString() + "!", UI.MAX_TEXTBOX));
					}
				}
				if (moveOption > 0) p.moveset[moveOption - 1] = new Moveslot(m);
				moveOption = -1;
				currentMove = null;
				showMoveOptions = false;
				currentTask = null;
			}
		}
	}
	
	public void drawEvolution(Task t) {
		currentDialogue = t.message;
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
				gp.player.p.evolve(t.p, t.counter, gp);
		        currentTask = null;
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				Task text = Task.createTask(Task.TEXT, t.p.nickname + " did not evolve.");
				Task.insertTask(text, 0);
				currentTask = null;
				commandNum = 0;
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
	}
	
	public void handleKeyInput(char keyChar) {
		if (nickname.length() < 12) {
			nickname.append(keyChar);
		}
	}

	public void handleBackspace() {
	    if (nickname.length() > 0) {
	        nickname.deleteCharAt(nickname.length() - 1);
	    }
	}

	public void setNicknaming(boolean n) {
		if (n) {
			nicknaming = 1;
		} else {
			nicknaming = 0;
		}	
	}
	
	public void drawToolTips(String w, String a, String s, String d) {
		drawToolTips(w, a, s, d, null);
	}
	
	public void drawToolTips(String w, String a, String s, String d, String c) {
		drawToolTips(0, (int) (gp.tileSize * 10.5), w, a, s, d, c);
	}
	
	public void drawToolTipBar(ArrayList<ToolTip> tips) {
		drawToolTipBar(0, (int) (gp.tileSize * 10.5), tips);
	}
	
	public void drawToolTipBar(int x, int y, ArrayList<ToolTip> tips) {
		if (tips.isEmpty()) return;
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
		g2.setColor(Color.WHITE);
		FontMetrics keyMetrics = g2.getFontMetrics();
		FontMetrics labelMetrics = g2.getFontMetrics(g2.getFont().deriveFont(20F));
		int totalWidth = gp.tileSize;
		for (int i = 0; i < tips.size(); i++) {
			ToolTip tip = tips.get(i);
			String displayLabel = truncateText(tip.label, (int) (gp.tileSize * 1.5), labelMetrics);
			int keyWidth = keyMetrics.stringWidth(tip.key);
			int labelWidth = labelMetrics.stringWidth(displayLabel);
			totalWidth += keyWidth + gp.tileSize/4 + labelWidth + gp.tileSize/4;
		}
		
		int height = (int) (gp.tileSize * 1.5);
		drawSubWindow(x, y, totalWidth, height);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		for (int i = 0; i < tips.size(); i++) {
			ToolTip tip = tips.get(i);
			
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.setColor(Color.WHITE);
			
			g2.drawString(tip.key, x, y);
			x += keyMetrics.stringWidth(tip.key) + gp.tileSize / 4;
			
			g2.setFont(g2.getFont().deriveFont(20F));
			String displayLabel = truncateText(tip.label, (int) (gp.tileSize * 1.5), labelMetrics);
			g2.drawString(displayLabel, x, y);
			x += labelMetrics.stringWidth(displayLabel) + gp.tileSize / 4;
		}
	}
	
	public String truncateText(String text, int maxWidth, FontMetrics metrics) {
		if (metrics.stringWidth(text) <= maxWidth) {
	    	return text;
	    }
	    
	    String ellipsis = "...";
	    
	    for (int i = text.length() - 1; i > 0; i--) {
	    	String truncated = text.substring(0, i) + ellipsis;
	        if (metrics.stringWidth(truncated) <= maxWidth) {
	        	return truncated;
	        }
	    }
	    
	    return ellipsis;
	}
	
	public void drawToolTips(int x, int y, String w, String a, String s, String d, String c) {
		if (!gp.keyH.shiftPressed) return;
		
		ArrayList<ToolTip> tips = new ArrayList<>();
		
		HashMap<String, ArrayList<Integer>> actionToKeys = new HashMap<>();
		
		if (w != null) {
			actionToKeys.computeIfAbsent(w, k -> new ArrayList<>()).add(gp.config.wKey);
		}
		if (a != null) {
			actionToKeys.computeIfAbsent(a, k -> new ArrayList<>()).add(gp.config.aKey);
		}
		if (s != null) {
			actionToKeys.computeIfAbsent(s, k -> new ArrayList<>()).add(gp.config.sKey);
		}
		if (d != null) {
			actionToKeys.computeIfAbsent(d, k -> new ArrayList<>()).add(gp.config.dKey);
		}
		if (c != null) {
			actionToKeys.computeIfAbsent(c, k -> new ArrayList<>()).add(gp.config.calcKey);
		}
		
		String[] orderedActions = {w, a, s, d, c};
		HashSet<String> processedActions = new HashSet<>();
		
		for (String action : orderedActions) {
			if (action != null && !processedActions.contains(action)) {
				ArrayList<Integer> keys = actionToKeys.get(action);
				
				if (keys.size() == 1) {
					tips.add(new ToolTip(gp, action, "", false, keys.get(0)));
				} else {
					int[] keyArray = keys.stream().mapToInt(Integer::intValue).toArray();
					tips.add(new ToolTip(gp, action, "/", false, keyArray));
				}
				
				processedActions.add(action);
			}
		}
		
		drawToolTipBar(x, y, tips);
	}
	
	public void drawParlaySheet(boolean editable) {
	    int x = gp.tileSize * 5;
	    int y = gp.tileSize / 2;
	    int width = gp.tileSize * 6;
	    int height = (int) (gp.tileSize * 11.25);

	    int maxBet = MAX_PARLAYS - 1;

	    drawSubWindow(x, y, width, height, 255);

	    int parlayWidth = (int) (gp.tileSize * 5.5);
	    int parlayHeight = (int) (gp.tileSize * 1.75);

	    int startX = x;

	    for (int i = 0; i < MAX_PARLAYS; i++) {
	        g2.setColor(Color.WHITE);
	        int startY = y;
	        x += width / 2;
	        y += gp.tileSize * 0.75;
	        g2.setFont(g2.getFont().deriveFont(18F));

	        Pair<Double, String> current = gp.simBattleUI.parlaySheet.get(i);
	        String bet = String.format("%.1f %s", current.getFirst(), current.getSecond());
	        g2.drawString(bet, getCenterAlignedTextX(bet, x), y);

	        y += gp.tileSize * 0.75;
	        g2.setStroke(new BasicStroke(2));
	        g2.drawLine((int) (x - gp.tileSize * 1.25), y, x - gp.tileSize, y);

	        // Determine bet color: Gray if unselected, Red if under, Green if over
	        if (parlays[i] == 0) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.fillOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
				g2.setColor(Color.WHITE);
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			} else {
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			}
			String X = "x";
			g2.drawString(X, getCenterAlignedTextX(X, x), y + 8);
			
			g2.drawLine((int) (x + gp.tileSize * 1.25), y, x + gp.tileSize, y);

	        int diff = gp.tileSize * 2;
	        x -= diff;
	        
	        // Draw Under Button
	        if (parlays[i] == -1) {
				g2.setColor(Color.RED);
				g2.fillOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
				g2.setColor(Color.WHITE);
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			} else {
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			}
			String minus = "-";
			g2.setFont(g2.getFont().deriveFont(30F));
			g2.drawString(minus, getCenterAlignedTextX(minus, x), y + 8);
			
			x += diff * 2;
			if (parlays[i] == 1) {
				g2.setColor(Color.GREEN);
				g2.fillOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
				g2.setColor(Color.WHITE);
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			} else {
				g2.drawOval(x - gp.tileSize / 2, y - gp.tileSize / 2, gp.tileSize, gp.tileSize);
			}
			String plus = "+";
			g2.drawString(plus, getCenterAlignedTextX(plus, x), y + 8);
			x -= diff;

	        // Display Cost
	        g2.setFont(g2.getFont().deriveFont(14F));
	        g2.setColor(parlays[i] != 0 ? Color.WHITE : Color.GRAY);
	        if (editable || parlays[i] != 0) {
	        	g2.drawString("-" + parlayBet + " " + getBetCurrencyName(gauntlet), (int) (x - gp.tileSize * 2.6), (int) (y - gp.tileSize * 0.75));
	        }

	        // Calculate Payout if bet is placed
	        if (parlays[i] != 0) {
	        	int payout = SimBattleUI.calculateParlayPayout(parlays, null, null, parlayBet, -1);
	            g2.setColor(Color.GREEN);
	            g2.drawString("+" + payout + " " + getBetCurrencyName(gauntlet), (int) (x + gp.tileSize * 1.6), (int) (y - gp.tileSize * 0.75));
	        }

	        g2.setStroke(new BasicStroke(3));
	        if ((editable && i == gp.ui.areaCounter) || (!editable && i == gp.simBattleUI.currentParlay)) {
	            g2.setColor(Color.RED);
	            g2.drawRoundRect(x - parlayWidth / 2, y - gp.tileSize - 6, parlayWidth, parlayHeight, 45, 45);
	        }

	        x = startX;
	        y = startY + parlayHeight;
	    }

	    if (editable) {
	        if (gp.keyH.downPressed) {
	            gp.keyH.downPressed = false;
	            if (gp.ui.areaCounter < maxBet) {
	                gp.ui.areaCounter++;
	            } else {
	                gp.ui.areaCounter = 0;
	                showParlays = false;
	            }
	        }

	        if (gp.keyH.upPressed) {
	            gp.keyH.upPressed = false;
	            if (gp.ui.areaCounter > 0) {
	                gp.ui.areaCounter--;
	            } else {
	                gp.ui.areaCounter = 0;
	                showParlays = false;
	            }
	        }

	        if (gp.keyH.leftPressed) {
	            gp.keyH.leftPressed = false;
	            if (parlays[gp.ui.areaCounter] > -1) {
	            	if (parlays[gp.ui.areaCounter] == 0) {
	            		if (gp.player.p.getBetCurrency(gauntlet) >= parlayBet) {
	                    	gp.player.p.addBetCurrency(gauntlet, -parlayBet);
	                    	parlays[gp.ui.areaCounter]--;
	    	                sheetFilled = true;
	    	                coinColor = Color.WHITE;
	                    } else {
	                    	coinColor = Color.RED;
	                    }
	            	} else {
	            		parlays[gp.ui.areaCounter]--;
	            		gp.player.p.addBetCurrency(gauntlet, parlayBet);
	            		coinColor = Color.WHITE;
	            	}
	                if (gp.ui.areaCounter < maxBet) {
	                    gp.ui.areaCounter++;
	                } else {
	                    gp.ui.areaCounter = 0;
	                    showParlays = false;
	                }
	            }
	        }

	        if (gp.keyH.rightPressed) {
	            gp.keyH.rightPressed = false;
	            if (parlays[gp.ui.areaCounter] < 1) {
	            	if (parlays[gp.ui.areaCounter] == 0) {
	            		if (gp.player.p.getBetCurrency(gauntlet) >= parlayBet) {
	            			gp.player.p.addBetCurrency(gauntlet, -parlayBet);
	                    	parlays[gp.ui.areaCounter]++;
	    	                sheetFilled = true;
	    	                coinColor = Color.WHITE;
	                    } else {
	                    	coinColor = Color.RED;
	                    }
	            	} else {
	            		parlays[gp.ui.areaCounter]++;
	            		gp.player.p.addBetCurrency(gauntlet, parlayBet);
	            		coinColor = Color.WHITE;
	            	}
	                if (gp.ui.areaCounter < maxBet) {
	                    gp.ui.areaCounter++;
	                } else {
	                    gp.ui.areaCounter = 0;
	                    showParlays = false;
	                }
	            }
	        }
	        if (gp.keyH.aPressed || gp.keyH.sPressed) {
				gp.keyH.aPressed = false;
				gp.keyH.sPressed = false;
				showParlays = false;
			}
		} else {
			if (gp.keyH.sPressed) {
				gp.keyH.sPressed = false;
				showParlays = false;
			}
	    }

	    drawToolTips(null, editable ? "Close" : null, "Close", null);
	    
	}
	
	public void drawSettings() {
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int frameX = gp.tileSize*4;
		int frameY = gp.tileSize / 2;
		int frameWidth = gp.tileSize*8;
		int frameHeight = gp.tileSize*11;
		
		switch(settingsState) {
		case 0: drawSettingsTop(frameX, frameY, frameWidth, frameHeight); break;
		case 1: drawControlsScreen(frameX-gp.tileSize*2, frameY, frameWidth+gp.tileSize*4, frameHeight); break;
		case 2: drawDifficultyScreen(frameX+gp.tileSize, frameY+gp.tileSize, frameWidth-gp.tileSize*2, frameHeight-gp.tileSize*2);
		}
	}
	
	private void drawSettingsTop(int frameX, int frameY, int frameWidth, int frameHeight) {
		drawPanelWithBorder(frameX, frameY, frameWidth, frameHeight, 220, textColor);
		
		// title
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		String text = "Settings";
		int textX = getCenterAlignedTextX(text, gp.screenWidth / 2);
		int textY = frameY + gp.tileSize;
		drawOutlinedText(text, textX, textY, textColor, Color.BLACK);
		
		// div line
		int dividerY = textY + gp.tileSize / 4;
		g2.setColor(new Color(100, 100, 100));
		g2.drawLine(frameX + gp.tileSize, dividerY, frameX + frameWidth - gp.tileSize, dividerY);
		
		int contentX = frameX + gp.tileSize;
		int contentY = dividerY + (int)(gp.tileSize * 1.5);
		int itemHeight = (int)(gp.tileSize * 1.25);
		
		// MUSIC
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		boolean selected = commandNum == 0;
		drawOutlinedText("Music", contentX, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		// music volume bar
		int barX = contentX + gp.tileSize * 3;
		int barY = contentY - gp.tileSize / 3;
		int barWidth = (int)(gp.tileSize * 3);
		int barHeight = gp.tileSize / 3;
		
		g2.setColor(new Color(30, 30, 30, 200));
		g2.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
		
		if (selected) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(textColor);
		} else {
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
		}
		g2.drawRoundRect(barX, barY, barWidth, barHeight, 10, 10);
		
		int volumeWidth = (barWidth - 6) * gp.music.volumeScale / 5;
		g2.setColor(selected ? textColor.darker() : new Color(100, 200, 100));
		g2.fillRoundRect(barX + 3, barY + 3, volumeWidth, barHeight - 6, 8, 8);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		String volumeText = gp.music.volumeScale + "/5";
		this.drawOutlinedText(volumeText, getCenterAlignedTextX(volumeText, barX + barWidth / 2), contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		if (selected) {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			float arrowAlpha = 0.6f + (float)(Math.sin(pulseCounter * 0.1) * 0.4);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, arrowAlpha));
			
			g2.setColor(Color.YELLOW);
			g2.drawString("<", barX - gp.tileSize / 2, contentY);
			g2.drawString(">", barX + barWidth + gp.tileSize / 4, contentY);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				if (gp.music.volumeScale > 0) {
					gp.playSFX(Sound.S_MENU_1);
					gp.music.volumeScale--;
					gp.music.checkVolume();
				}
			}
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				if (gp.music.volumeScale < 5) {
					gp.playSFX(Sound.S_MENU_1);
					gp.music.volumeScale++;
					gp.music.checkVolume();
				}
			}
		}
		
		// SFX
		contentY += itemHeight;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		selected = commandNum == 1;
		drawOutlinedText("SFX", contentX, contentY, 
			selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		// SFX volume bar
		barY = contentY - gp.tileSize / 3;
		
		g2.setColor(new Color(30, 30, 30, 200));
		g2.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
		
		if (selected) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(textColor);
		} else {
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
		}
		g2.drawRoundRect(barX, barY, barWidth, barHeight, 10, 10);
		
		volumeWidth = (barWidth - 6) * gp.sfx.volumeScale / 5;
		g2.setColor(selected ? textColor.darker() : new Color(100, 200, 100));
		g2.fillRoundRect(barX + 3, barY + 3, volumeWidth, barHeight - 6, 8, 8);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		volumeText = gp.sfx.volumeScale + "/5";
		this.drawOutlinedText(volumeText, getCenterAlignedTextX(volumeText, barX + barWidth / 2), contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		if (selected) {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			float arrowAlpha = 0.6f + (float)(Math.sin(pulseCounter * 0.1) * 0.4);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, arrowAlpha));
			
			g2.setColor(Color.YELLOW);
			g2.drawString("<", barX - gp.tileSize / 2, contentY);
			g2.drawString(">", barX + barWidth + gp.tileSize / 4, contentY);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				if (gp.sfx.volumeScale > 0) {
					gp.playSFX(Sound.S_MENU_1);
					gp.sfx.volumeScale--;
					gp.sfx.checkVolume();
				}
			}
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				if (gp.sfx.volumeScale < 5) {
					gp.playSFX(Sound.S_MENU_1);
					gp.sfx.volumeScale++;
					gp.sfx.checkVolume();
				}
			}
		}
		
		// div line
		contentY += itemHeight - gp.tileSize / 3;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(100, 100, 100));
		g2.drawLine(frameX + gp.tileSize, contentY - gp.tileSize / 2, frameX + frameWidth - gp.tileSize, contentY - gp.tileSize / 2);
		
		// FULLSCREEN
		contentY += gp.tileSize / 3;
		selected = commandNum == 2;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		drawOutlinedText("Fullscreen", contentX, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		int toggleX = contentX + gp.tileSize * 3;
		int toggleY = contentY - gp.tileSize / 3;
		drawToggleSwitch(toggleX, toggleY, gp.config.fullscreen, selected);
		
		if (selected && (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed)) {
			gp.keyH.wPressed = false;
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			if (gp.config.fullscreen) {
				gp.playSFX(Sound.S_MENU_CAN);
			} else {
				gp.playSFX(Sound.S_MENU_CON);
			}
			gp.toggleFullScreen();
		}
		
		// TOGGLE RUN
		contentY += itemHeight;
		selected = commandNum == 3;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		drawOutlinedText("Toggle Run", contentX, contentY, 
			selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		toggleY = contentY - gp.tileSize / 3;
		drawToggleSwitch(toggleX, toggleY, gp.config.toggleRun, selected);
		
		if (selected && (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed)) {
			gp.keyH.wPressed = false;
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			if (gp.config.toggleRun) {
				gp.playSFX(Sound.S_MENU_CAN);
			} else {
				gp.playSFX(Sound.S_MENU_CON);
			}
			gp.player.isRunning = false;
			gp.config.toggleRun = !gp.config.toggleRun;
		}
		
		// BATTLE LOG TRACKING
		contentY += itemHeight;
		selected = commandNum == 4;
		boolean inGameMenu = gp.gameState == GamePanel.MENU_STATE;
		boolean battleLogChangeable = inGameMenu && !gp.player.p.nuzlocke;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		drawOutlinedText("Battle Log", contentX, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		toggleY = contentY - gp.tileSize / 3;
		
		if (inGameMenu) {
			drawToggleSwitch(toggleX, toggleY, gp.player.p.trackBattleLogs, selected);
			
			// Draw lock icon if nuzlocke mode is active
			if (gp.player.p.nuzlocke) {
				drawLockIcon(toggleX + (int)(gp.tileSize * 2.5), toggleY - gp.tileSize / 8, selected);
			}
		} else {
			g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 24F));
			String naText = "N/A";
			int naX = toggleX + gp.tileSize / 2;
			drawOutlinedText(naText, naX, contentY, new Color(100, 100, 100), Color.BLACK);
		}
		
		if (selected && battleLogChangeable && (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed)) {
			gp.keyH.wPressed = false;
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			if (gp.player.p.trackBattleLogs) {
				gp.playSFX(Sound.S_MENU_CAN);
			} else {
				gp.playSFX(Sound.S_MENU_CON);
			}
			gp.player.p.trackBattleLogs = !gp.player.p.trackBattleLogs;
		} else if (selected && !battleLogChangeable && (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed)) {
			gp.keyH.wPressed = false;
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
		}
		
		// DIFFICULTY SELECTOR
		contentY += itemHeight;
		selected = commandNum == 5;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		drawOutlinedText("Difficulty", contentX, contentY, 
			selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		boolean difficultyChangable = gp.gameState == GamePanel.MENU_STATE;
		int selectorX = toggleX;
		String difficulty = difficultyChangable ? Player.DIFFICULTIES[gp.player.p.difficulty] : "N/A";
		Color difficultyColor = difficultyChangable ? Player.DIFFICULTY_COLORS[gp.player.p.difficulty] : new Color(100, 100, 100);
		
		// colored difficulty text
		g2.setFont(g2.getFont().deriveFont(difficultyChangable ? Font.BOLD : Font.ITALIC, 24F));
		int diffX = selectorX + gp.tileSize / 2;
		if (selected) {
			drawOutlinedText(difficulty, diffX, contentY, difficultyColor, Color.BLACK);
		} else {
			Color desaturated = new Color(
				(difficultyColor.getRed() + 100) / 2,
				(difficultyColor.getGreen() + 100) / 2,
				(difficultyColor.getBlue() + 100) / 2
			);
			drawOutlinedText(difficulty, diffX, contentY, desaturated, Color.BLACK);
		}
		
		if (selected && gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (difficultyChangable) {
				gp.playSFX(Sound.S_MENU_1);
				settingsState = 2;
				commandNum = 0;
				difficultyNum = gp.player.p.difficulty;
			} else {
				gp.playSFX(Sound.S_MENU_CAN);
			}
		}
		
		// CONTROLS
		contentY += gp.tileSize;
		selected = commandNum == 6;
		int buttonWidth = frameWidth - gp.tileSize * 2;
		int buttonHeight = (int)(gp.tileSize * 0.8);
		int buttonX = frameX + gp.tileSize;
		int buttonY = contentY - gp.tileSize / 3;
		
		if (selected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity + 30, textColor);
		} else {
			g2.setColor(new Color(40, 40, 40, 180));
			g2.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
			g2.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
		}
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26F));
		String controlsText = "Controls";
		drawOutlinedText(controlsText, getCenterAlignedTextX(controlsText, frameX + frameWidth / 2), contentY + gp.tileSize / 4, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		if (selected && gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			settingsState = 1;
			commandNum = 0;
		}
		
		int MAX_OPTIONS = 6;
		
		// Navigation
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			commandNum--;
			if (commandNum < 0) {
				commandNum = MAX_OPTIONS; // Skip unused options
			}
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			commandNum++;
			if (commandNum > MAX_OPTIONS) {
				commandNum = 0; // Skip unused options
			}
		}
		
		drawToolTips("OK", null, "Back", "Back");
	}
	
	private void drawLockIcon(int x, int y, boolean highlighted) {
		int size = gp.tileSize / 3;
		
		// Lock body (rectangle)
		int bodyX = x;
		int bodyY = y + size / 2;
		int bodyWidth = size;
		int bodyHeight = (int)(size * 0.8);
		
		Color lockColor = highlighted ? new Color(220, 200, 100) : new Color(205, 205, 205);
		
		// Draw body
		g2.setColor(lockColor.darker());
		g2.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 4, 4);
		g2.setStroke(new BasicStroke(2));
		g2.setColor(lockColor);
		g2.drawRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 4, 4);
		
		// Lock shackle (arc)
		int shackleX = bodyX + bodyWidth / 4;
		int shackleY = bodyY - size / 3;
		int shackleWidth = bodyWidth / 2;
		int shackleHeight = (int)(size * 0.6);
		
		g2.setStroke(new BasicStroke(3));
		g2.setColor(lockColor.darker());
		g2.drawArc(shackleX, shackleY, shackleWidth, shackleHeight, 0, 180);
		
		// Keyhole
		int keyholeX = bodyX + bodyWidth / 2;
		int keyholeY = bodyY + bodyHeight / 2;
		g2.setColor(new Color(45, 45, 45));
		g2.fillOval(keyholeX - 2, keyholeY - 2, 4, 4);
		g2.fillRect(keyholeX - 1, keyholeY, 2, bodyHeight / 4);
	}
	
	private void drawControlsScreen(int frameX, int frameY, int frameWidth, int frameHeight) {
		if (gp.config.pendingKeys == null) {
			gp.config.pendingKeys = Arrays.copyOf(gp.config.keys, gp.config.keys.length);
		}
		
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
		
		g2.setColor(Color.WHITE);
		String text = "Controls";
		int textX = this.getCenterAlignedTextX(text, gp.screenWidth / 2);
		int textY = frameY + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		textX = frameX + gp.tileSize;
		textY += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		ArrayList<ToolTip> keys = new ArrayList<>();
		for (int i = 0; i < gp.config.keyNames.length; i++) {
			ToolTip key = new ToolTip(gp, gp.config.keyNames[i], "", false, i);
			key.buildKey("", false, gp.config.pendingKeys[i]);
			keys.add(key);
		}
		
		int controlsPerColumn = 11;
		int totalOptions = gp.config.keyNames.length + 1;
		
		int leftX = frameX + gp.tileSize;
		int startY = frameY + gp.tileSize * 2;
		
		for (int i = 0; i < controlsPerColumn && i < keys.size(); i++) {
			int yPos = startY + (i * (int)(gp.tileSize * 0.75));
			// control name
			g2.setColor(Color.WHITE);
			g2.drawString(keys.get(i).label + ":", leftX, yPos);
			
			// key
			int keyTextX = textX + gp.tileSize * 3;
			String keyText = keys.get(i).key;
			
			if (commandNum == i && !waitingForKey) {
				g2.setColor(Color.YELLOW);
			} else if (waitingForKey && commandNum == i) {
				g2.setColor(Color.GREEN);
				keyText = "Press key...";
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			g2.drawString(keyText, keyTextX, yPos);
		}
		
		int rightX = frameX + frameWidth / 2 + gp.tileSize / 2;
		
		for (int i = controlsPerColumn; i < gp.config.keyNames.length; i++) {
			int indexInRightColumn = i - controlsPerColumn;
			int yPos = startY + (indexInRightColumn * (int) (gp.tileSize * 0.75));
			
			g2.setColor(Color.WHITE);
			g2.drawString(keys.get(i).label + ":", rightX, yPos);
			
			int keyTextX = rightX + gp.tileSize * 3;
			String keyText = keys.get(i).key;
			
			if (commandNum == i && !waitingForKey) {
				g2.setColor(Color.YELLOW);
			} else if (waitingForKey && commandNum == i) {
				g2.setColor(Color.GREEN);
				keyText = "Press key...";
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			g2.drawString(keyText, keyTextX, yPos);
		}
		
		int resetIndex = gp.config.keyNames.length;
		int resetYPos = startY + ((resetIndex - controlsPerColumn) * (int) (gp.tileSize * 0.75));
		
		if (commandNum == resetIndex) {
			g2.setColor(Color.YELLOW);
		} else {
			g2.setColor(Color.WHITE);
		}
		g2.drawString("Reset to Default", rightX, resetYPos);
		
		if (!waitingForKey) {
			if (gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				commandNum--;
				if (commandNum < 0) commandNum = totalOptions - 1;
			}
			if (gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				commandNum++;
				if (commandNum >= totalOptions) commandNum = 0;
			}
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				int currentColumn = commandNum / controlsPerColumn;
				if (currentColumn == 1) {
					int indexInColumn = commandNum % controlsPerColumn;
					commandNum = indexInColumn;
				}
			}
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				int currentColumn = commandNum / controlsPerColumn;
				if (currentColumn == 0) {
					int indexInColumn = commandNum % controlsPerColumn;
					int targetIndex = indexInColumn + controlsPerColumn;
					if (targetIndex < totalOptions) {
						commandNum = targetIndex;
					}
				}
			}
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (commandNum == resetIndex) {
					gp.config.resetControls();
				} else {
					waitingForKey = true;
					rebindingControl = commandNum;
				}
			}
			
			String wText = (commandNum == resetIndex) ? "Reset" : "Rebind";
			drawToolTips(waitingForKey ? null : wText, null, waitingForKey ? null : "Back", "Back");
		}
	}
	
	public void drawDifficultyScreen(int frameX, int frameY, int frameWidth, int frameHeight) {
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
		
		// Title
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		g2.setColor(Color.WHITE);
		String title = "Change Difficulty";
		int titleX = getCenterAlignedTextX(title, frameX + frameWidth / 2);
		int titleY = frameY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		// Divider
		int dividerY = titleY + gp.tileSize / 4;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(100, 100, 100));
		g2.drawLine(frameX + gp.tileSize / 2, dividerY, frameX + frameWidth - gp.tileSize / 2, dividerY);
		
		// Current difficulty display
		int contentY = dividerY + gp.tileSize * 2 / 3;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String currentLabel = "Current Difficulty:";
		int labelX = getCenterAlignedTextX(currentLabel, frameX + frameWidth / 2);
		drawOutlinedText(currentLabel, labelX, contentY, new Color(200, 200, 200), Color.BLACK);
		
		contentY += gp.tileSize * 2 / 3;
		String currentDifficulty = Player.DIFFICULTIES[gp.player.p.difficulty];
		Color currentColor = Player.DIFFICULTY_COLORS[gp.player.p.difficulty];
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		int currentX = getCenterAlignedTextX(currentDifficulty, frameX + frameWidth / 2);
		drawOutlinedText(currentDifficulty, currentX, contentY, currentColor, Color.BLACK);
		
		// New difficulty selector
		contentY += gp.tileSize * 1.25;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String newLabel = "New Difficulty:";
		labelX = getCenterAlignedTextX(newLabel, frameX + frameWidth / 2);
		drawOutlinedText(newLabel, labelX, contentY, commandNum == 0 ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		contentY += gp.tileSize * 3 / 4;
		String newDifficulty = Player.DIFFICULTIES[difficultyNum];
		Color newColor = Player.DIFFICULTY_COLORS[difficultyNum];
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		int selectorX = frameX + frameWidth / 2 - gp.tileSize * 3 / 2;
		
		// Draw colored difficulty text with selector styling
		int diffX = getCenterAlignedTextX(newDifficulty, frameX + frameWidth / 2);
		if (commandNum == 0) {
			int selectorWidth = gp.tileSize * 3;
			int selectorHeight = (int)(gp.tileSize * 0.75);
			int selectorY = contentY - selectorHeight + gp.tileSize / 8;
			
			g2.setColor(new Color(0, 0, 0, 150));
			g2.fillRect(selectorX, selectorY, selectorWidth, selectorHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(textColor);
			g2.drawRect(selectorX, selectorY, selectorWidth, selectorHeight);
			
			drawOutlinedText(newDifficulty, diffX, contentY, newColor, Color.BLACK);
			
			// Draw arrows
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			float arrowAlpha = 0.6f + (float)(Math.sin(pulseCounter * 0.1) * 0.4);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, arrowAlpha));
			
			g2.setColor(Color.YELLOW);
			g2.drawString("<", selectorX - gp.tileSize / 2, contentY);
			g2.drawString(">", selectorX + selectorWidth + gp.tileSize / 4, contentY);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		} else {
			Color desaturated = new Color(
				(newColor.getRed() + 100) / 2,
				(newColor.getGreen() + 100) / 2,
				(newColor.getBlue() + 100) / 2
			);
			drawOutlinedText(newDifficulty, diffX, contentY, desaturated, Color.BLACK);
		}
		
		// Nuzlocke warning if applicable
		boolean nuzlockeActive = gp.player.p.nuzlocke;
		boolean difficultyChanged = difficultyNum != gp.player.p.difficulty;
		
		if (nuzlockeActive && difficultyChanged) {
			contentY += gp.tileSize;
			
			// Warning box
			int warningX = frameX + gp.tileSize / 2;
			int warningY = contentY - gp.tileSize * 3 / 4;
			int warningWidth = frameWidth - gp.tileSize;
			int warningHeight = (int)(gp.tileSize * 1.5);
			
			// Red warning background
			g2.setColor(new Color(150, 0, 0, 180));
			g2.fillRoundRect(warningX, warningY, warningWidth, warningHeight, 15, 15);
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.RED);
			g2.drawRoundRect(warningX, warningY, warningWidth, warningHeight, 15, 15);
			
			// Warning text
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			contentY -= gp.tileSize / 8;
			String warning1 = "WARNING:";
			int warningTextX = getCenterAlignedTextX(warning1, frameX + frameWidth / 2);
			drawOutlinedText(warning1, warningTextX, contentY, Color.YELLOW, Color.BLACK);
			
			contentY += gp.tileSize / 3;
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18F));
			String warning2 = "Changing difficulty will";
			warningTextX = getCenterAlignedTextX(warning2, frameX + frameWidth / 2);
			drawOutlinedText(warning2, warningTextX, contentY, Color.WHITE, Color.BLACK);
			
			contentY += gp.tileSize / 3;
			String warning3 = "invalidate your Nuzlocke!";
			warningTextX = getCenterAlignedTextX(warning3, frameX + frameWidth / 2);
			drawOutlinedText(warning3, warningTextX, contentY, Color.WHITE, Color.BLACK);
		}
		
		// Confirm button
		contentY = frameY + frameHeight - gp.tileSize;
		boolean confirmSelected = commandNum == 1;
		boolean canConfirm = difficultyChanged;
		
		int buttonWidth = gp.tileSize * 3;
		int buttonHeight = (int)(gp.tileSize * 0.8);
		int buttonX = frameX + frameWidth / 2 - buttonWidth / 2;
		int buttonY = contentY - gp.tileSize / 3;
		
		if (!canConfirm) {
			// Grayed out button
			g2.setColor(new Color(20, 20, 20, 150));
			g2.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(60, 60, 60));
			g2.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			String confirmText = "Confirm";
			drawOutlinedText(confirmText, getCenterAlignedTextX(confirmText, frameX + frameWidth / 2), contentY + gp.tileSize / 4, new Color(100, 100, 100), Color.BLACK);
		} else if (confirmSelected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity + 30, textColor);
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			String confirmText = "Confirm";
			drawOutlinedText(confirmText, getCenterAlignedTextX(confirmText, frameX + frameWidth / 2), contentY + gp.tileSize / 4, textColor, Color.BLACK);
		} else {
			g2.setColor(new Color(40, 40, 40, 180));
			g2.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
			g2.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
			String confirmText = "Confirm";
			drawOutlinedText(confirmText, getCenterAlignedTextX(confirmText, frameX + frameWidth / 2), contentY + gp.tileSize / 4, new Color(200, 200, 200), Color.BLACK);
		}
		
		// Navigation
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			commandNum--;
			if (commandNum < 0) commandNum = canConfirm ? 1 : 0;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			commandNum++;
			if (commandNum > (canConfirm ? 1 : 0)) commandNum = 0;
		}
		
		if (commandNum == 0) {
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				difficultyNum--;
				if (difficultyNum < 0) difficultyNum = Player.DIFFICULTIES.length - 1;
			}
			
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				difficultyNum++;
				if (difficultyNum >= Player.DIFFICULTIES.length) difficultyNum = 0;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (commandNum == 1 && canConfirm) {
				gp.playSFX(Sound.S_MENU_CON);
				// Invalidate nuzlocke if active
				if (nuzlockeActive) {
					gp.player.p.invalidateNuzlocke("Changed difficulty from " + Player.DIFFICULTIES[gp.player.p.difficulty] + " to " + Player.DIFFICULTIES[difficultyNum]);
				}
				
				// Apply difficulty change
				gp.player.p.difficulty = difficultyNum;
				
				// Return to settings
				settingsState = 0;
				commandNum = 4; // Return to difficulty option
				difficultyNum = 0;
			} else if (commandNum == 0 || !canConfirm) {
				gp.playSFX(Sound.S_MENU_CAN);
			}
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			// Return to settings without saving
			settingsState = 0;
			commandNum = 4; // Return to difficulty option
			difficultyNum = 0;
		}
		
		String wText = (commandNum == 1 && canConfirm) ? "Confirm" : null;
		drawToolTips(wText, null, "Back", "Back");
	}
	
	public void drawOutlinedText(String text, int x, int y, Color fillColor, Color outlineColor) {
		g2.setColor(outlineColor);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx != 0 || dy != 0) {
					g2.drawString(text, x + dx, y + dy);
				}
			}
		}
		
		g2.setColor(fillColor);
		g2.drawString(text, x, y);
	}
	
	public void drawPanelWithBorder(int x, int y, int width, int height, int opacity, Color borderColor) {
		g2.setColor(new Color(0, 0, 0, Math.min(255, opacity)));
		g2.fillRect(x, y, width, height);
		
		g2.setStroke(new BasicStroke(3));
		g2.setColor(borderColor);
		g2.drawRect(x, y, width, height);
		
		g2.setColor(new Color(255, 255, 255, 30));
		g2.drawLine(x + 2, y + 2, x + width - 2, y + 2);
		g2.drawLine(x + 2, y + 2, x + 2, y + height - 2);
	}
	
	public void handleRebind(int newKey) {
		gp.config.setKeybind(rebindingControl, newKey);
		
		waitingForKey = false;
		rebindingControl = -1;
	}
	
	public String getBetCurrencyName(boolean gauntlet) {
		return gauntlet ? "Orbs" : "Coins";
	}
	
	public int getNextValidOption(int current, int maxOption, boolean goingUp) {
		int next = current;
		int attempts = 0;
		
		do {
			if (goingUp) {
				next--;
				if (next < 0) next = maxOption;
			} else {
				next++;
				if (next > maxOption) next = 0;
			}
			attempts++;
		} while (!isValidOption(next) && attempts <= maxOption + 1);
		
		return next;
	}
	
	public boolean isValidOption(int option) {
		return true; // isn't implemented in the abstract class
	}
	
	public void drawBadgesWindow(int x, int y, int width, int height, Player p, GamePanel gp, boolean drawBorder) {
    	if (p.badges >= 8) {
            drawFireAnimation(x, y, width, height);
        }
        
        // Draw badge container with border
    	if (drawBorder) {
    		drawPanelWithBorder(x, y, width, height, 50, p.badges >= 8 ? new Color(255, 215, 0) : new Color(150, 150, 150)); // Gold border if all badges
    	}
        
        // Draw badges in a 4x2 grid
        int badgeX = x + gp.tileSize / 2;
        int startBadgeX = badgeX;
        int badgeY = y + gp.tileSize / 4;
        int badgeSpacing = gp.tileSize;
        
        for (int i = 0; i < 8; i++) {
            if (i < p.badges) {
                BufferedImage badgeIcon = gp.ui.badgeIcons[i];
                g2.drawImage(badgeIcon, badgeX, badgeY, null);
            } else {
                // Greyed out badge
                BufferedImage badgeIcon = gp.ui.badgeIcons[i + 8];
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.drawImage(badgeIcon, badgeX, badgeY, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            
            badgeX += badgeSpacing;
            if ((i + 1) % 4 == 0) {
                badgeX = startBadgeX;
                badgeY += badgeSpacing;
            }
        }
	}
	
	public void drawFireAnimation(int x, int y, int width, int height) {
	    // Create animated fire effect around the badge box
	    float pulseProgress = pulseCounter / 120f; // Normalize to [0, 1)
	    
	    // Draw multiple layers of "flames" with varying colors and offsets
	    int numFlames = 12;
	    
	    for (int i = 0; i < numFlames; i++) {
	        float angle = (float) (i * Math.PI * 2 / numFlames + pulseProgress * Math.PI * 2);
	        float offset = (float) (Math.sin(pulseProgress * Math.PI * 4 + i) * 6 + 4);
	        
	        // Calculate flame position around the box
	        float flameCenterX = x + width / 2f + (float) Math.cos(angle) * (width / 2f + offset);
	        float flameCenterY = y + height / 2f + (float) Math.sin(angle) * (height / 2f + offset);
	        
	        // Flame color transitions: red -> orange -> yellow
	        float colorPhase = (pulseProgress + i / (float) numFlames) % 1.0f;
	        Color flameColor;
	        
	        if (colorPhase < 0.33f) {
	            // Red to Orange
	            float t = colorPhase / 0.33f;
	            flameColor = new Color(255, (int) (100 + 155 * t), 0);
	        } else if (colorPhase < 0.66f) {
	            // Orange to Yellow
	            float t = (colorPhase - 0.33f) / 0.33f;
	            flameColor = new Color(255, (int) (200 + 55 * t), (int) (50 * t));
	        } else {
	            // Yellow to Red
	            float t = (colorPhase - 0.66f) / 0.34f;
	            flameColor = new Color(255, (int) (255 - 155 * t), (int) (50 - 50 * t));
	        }
	        
	        // Size varies with pulse and position
	        float sizeMultiplier = (float) (0.5 + 0.5 * Math.sin(pulseProgress * Math.PI * 2 + i * 0.5));
	        int flameSize = (int) (8 + 4 * sizeMultiplier);
	        
	        // Draw flame with alpha for glow effect
	        float alpha = 0.3f + 0.4f * sizeMultiplier;
	        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	        
	        // Draw outer glow
	        g2.setColor(new Color(flameColor.getRed(), flameColor.getGreen(), 0, 100));
	        g2.fillOval((int) flameCenterX - flameSize, (int) flameCenterY - flameSize, 
	                    flameSize * 2, flameSize * 2);
	        
	        // Draw inner flame
	        g2.setColor(flameColor);
	        g2.fillOval((int) flameCenterX - flameSize / 2, (int) flameCenterY - flameSize / 2, 
	                    flameSize, flameSize);
	    }
	    
	    // Add inner glow effect
	    float innerGlowAlpha = 0.1f + 0.1f * (float) Math.sin(pulseProgress * Math.PI * 2);
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, innerGlowAlpha));
	    
	    GradientPaint innerGlow = new GradientPaint(
	        x + width / 2f, y, new Color(255, 200, 0, 150),
	        x + width / 2f, y + height, new Color(255, 100, 0, 50)
	    );
	    g2.setPaint(innerGlow);
	    g2.fillRoundRect(x + 4, y + 4, width - 8, height - 8, 25, 25);
	    
	    // Reset composite
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	    g2.setColor(Color.WHITE);
	}
	
	public void drawToggleSwitch(int x, int y, boolean isOn, boolean selected) {
		int toggleWidth = (int)(gp.tileSize * 1.5);
		int toggleHeight = gp.tileSize / 3;
		
		Color bgColor = isOn ? new Color(0, 150, 0) : new Color(100, 100, 100);
		g2.setColor(bgColor);
		g2.fillRoundRect(x, y, toggleWidth, toggleHeight, toggleHeight, toggleHeight);
		
		if (selected) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.YELLOW);
			g2.drawRoundRect(x - 2, y - 2, toggleWidth + 4, toggleHeight + 4, 
				toggleHeight + 4, toggleHeight + 4);
		}
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(x, y, toggleWidth, toggleHeight, toggleHeight, toggleHeight);
		
		int knobSize = toggleHeight - 4;
		int knobX = isOn ? x + toggleWidth - knobSize - 2 : x + 2;
		int knobY = y + 2;
		
		g2.setColor(Color.WHITE);
		g2.fillOval(knobX, knobY, knobSize, knobSize);
		g2.setColor(Color.DARK_GRAY);
		g2.drawOval(knobX, knobY, knobSize, knobSize);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String status = isOn ? "ON" : "OFF";
		int statusX = x + toggleWidth + 10;
		int statusY = y + toggleHeight / 2 + 6;
		Color statusColor = selected ? textColor : new Color(180, 180, 180);
		drawOutlinedText(status, statusX, statusY, statusColor, Color.BLACK);
	}

	public void drawSelector(int x, int y, String value, boolean selected) {
		int selectorWidth = gp.tileSize * 3;
		int selectorHeight = (int)(gp.tileSize * 0.5);
		int selectorY = y - selectorHeight + gp.tileSize / 8;
		
		// Background
		if (selected) {
			g2.setColor(new Color(0, 0, 0, 150));
			g2.fillRect(x, selectorY, selectorWidth, selectorHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(textColor);
			g2.drawRect(x, selectorY, selectorWidth, selectorHeight);
		}
		
		// Value
		g2.setFont(g2.getFont().deriveFont(20F));
		int valueX = getCenterAlignedTextX(value, x + selectorWidth / 2);
		int valueY = y;
		Color valueColor = selected ? textColor : new Color(200, 200, 200);
		drawOutlinedText(value, valueX, valueY, valueColor, Color.BLACK);
		
		// Arrows if selected
		if (selected) {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			float arrowAlpha = 0.6f + (float)(Math.sin(pulseCounter * 0.1) * 0.4);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, arrowAlpha));
			
			g2.setColor(Color.YELLOW);
			g2.drawString("<", x - 10, y);
			g2.drawString(">", x + selectorWidth + 5, y);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}
}