package pokemon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import overworld.GamePanel;
import pokemon.Pokemon.Task;

public abstract class AbstractUI {
	
	public GamePanel gp;
	public Graphics2D g2;
	public String currentDialogue;
	public int commandNum;
	public int partyNum;
	public int moveSummaryNum = -1;
	public int moveSwapNum = -1;
	public int partySelectedNum = -1;
	public int moveOption = -1;
	public Font marumonica;
	public int counter = 0;
	public boolean showMoveOptions;
	public boolean showIVOptions;
	
	public int boxSwapNum;
	public boolean showBoxParty;
	public boolean showBoxSummary;
	
	public Task currentTask;
	public ArrayList<Task> tasks;
	
	public StringBuilder nickname = new StringBuilder();
	public int nicknaming = -1;
	
	public BufferedImage ballIcon;

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
	
	public void drawParty(Item item) {
		drawParty(gp.tileSize*3, gp.tileSize, item);
	}

	public void drawParty(int x, int y, Item item) {
		int width = gp.tileSize*10;
		int height = gp.tileSize*10;
		
		drawSubWindow(x, y, width, height);
		
		if (!showMoveOptions && !showIVOptions && !showBoxSummary && (currentTask == null || currentTask.type == Task.PARTY)) {
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
				int canUseItem = p.canUseItem(item);
				String hpText = canUseItem == -1 ? p.currentHP + " / " + p.getStat(0) : canUseItem == 0 ? "NOT ABLE" : canUseItem == 2 ? "LEARNED" : "ABLE";
				g2.drawString(hpText, getCenterAlignedTextX(hpText, (int) (x + (partyWidth * 0.75) - 12)), (int) (y + gp.tileSize * 2.25));
				if (p.status != Status.HEALTHY) {
					g2.drawImage(p.status.getImage(), (int) (x + gp.tileSize * 2.5) + 4, (int) (y + gp.tileSize * 2.25) + 8, null);
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
		
		// Item
		if (p.item != null) {
			x += gp.tileSize * 3.5;
			g2.drawString("@", x, y + gp.tileSize / 2);
			x += gp.tileSize * 0.5;
			g2.drawImage(scaleImage(p.item.getImage(), 2), x, y - gp.tileSize / 4, null);
			if (foe == null) {
				y += gp.tileSize * 1.1;
				g2.setFont(g2.getFont().deriveFont(12F));
				String itemTake = "[A] Take " + p.item.toString();
				g2.drawString(itemTake, getCenterAlignedTextX(itemTake, x + 8), y);
				y -= gp.tileSize * 1.1;
			}
		}
		
		// Status
		if (p.status != Status.HEALTHY) {
			x += gp.tileSize * 3.25;
			g2.drawImage(p.status.getImage(), x, y, null);
		}
		
		x = startX;
		y += gp.tileSize;
		int startY = y;
		
		// Sprite
		g2.drawImage(p.isFainted() ? p.getFaintedSprite() : p.getSprite(), x - 12, y, null);
		x += gp.tileSize * 2;
		g2.drawImage(p.type1.getImage2(), x - 12, y, null);
		if (p.type2 != null) {
			g2.drawImage(p.type2.getImage2(), x + 36, y + 36, null);
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
		String[] abilityDesc = Item.breakString(p.ability.desc, 32).split("\n");
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
		
		// Moves
		x += gp.tileSize * 3;
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
					g2.setColor(m.move.mtype.getColor());
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
		
		if (moveSummaryNum > -1) {
			int moveX = gp.tileSize * 3;
			int moveY = gp.tileSize / 2;
			drawMoveSummary(moveX, moveY, p, foe, p.moveset[moveSummaryNum], null);
		}
		
		if (nicknaming < 0) {
			if (gp.keyH.wPressed && moveSummaryNum == -1) {
				gp.keyH.wPressed = false;
				moveSummaryNum = 0;
			}
			
			if (gp.keyH.upPressed) {
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
			
			if (gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				if (moveSummaryNum < 3 && p.moveset[moveSummaryNum + 1] != null) {
					moveSummaryNum++;
				} else {
					moveSummaryNum = 0;
				}
			}
			
			if (moveSummaryNum < 0) {
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
				if (moveSummaryNum < 0) {
					if (p.item != null && foe == null) {
						showMessage("Took " + p.nickname + "'s " + p.item + ".");
						gp.player.p.bag.add(p.item);
						p.item = null;
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
			
			if (gp.keyH.dPressed) {
				gp.keyH.dPressed = false;
				if (foe == null) {
					nickname = new StringBuilder(p.nickname);
					setNicknaming(true);
				}
			}
			
			String wText = moveSummaryNum < 0 ? "Moves" : null;
			String aText = moveSummaryNum < 0 ? p.item == null || foe != null ? null : "Take" : "Swap";
			String dText = gp.gameState == GamePanel.BATTLE_STATE || moveSummaryNum > -1 ? null : "Name";
			drawToolTips(wText, aText, "Back", dText);
		} else {
			currentDialogue = "Change " + p.name + "'s nickname?";
			drawDialogueScreen(true);
			setNickname(p);
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					p.nickname = nickname.toString().trim();
					nickname = new StringBuilder();
					if (p.nickname == null || p.nickname.trim().isEmpty()) p.nickname = p.name;
					nicknaming = -1;
				}
				drawToolTips("OK", null, null, null);
			}
		}
	}

	public void drawMoveSummary(int x, int y, Pokemon p, Pokemon foe, Moveslot moveslot, Move move) {
		int width = gp.tileSize*10;
		int height = (int) (gp.tileSize*5.5);
		drawSubWindow(x, y, width, height, 255);
		
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
		g2.drawImage(move.mtype.getImage2(), x, y, null);
		
		x += gp.tileSize * 1.5;
		y += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString("Power: " + move.formatbp(p, foe), x, y);
		
		x += gp.tileSize * 2.5;
		g2.drawString("Acc: " + move.getAccuracy(), x, y);
		
		x = startX;
		y += gp.tileSize * 1.25;
		g2.drawString("Category", x, y);
		
		x += gp.tileSize * 2;
		y -= gp.tileSize / 2;
		g2.drawImage(scaleImage(move.getCategoryIcon(), 2), x, y, null);
		
		x += gp.tileSize * 4;
		y += gp.tileSize / 2;
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
		y += gp.tileSize * 1.25;
		String[] desc = Item.breakString(move.getDescription(), 46).split("\n");
		int offset = (int) (gp.tileSize * 0.75);
		if (desc.length > 2) {
			g2.setFont(g2.getFont().deriveFont(20F));
			desc = Item.breakString(move.getDescription(), 54).split("\n");
			offset = (int) (gp.tileSize * 0.5);
		}
		for (String s : desc) {
			g2.drawString(s, getCenterAlignedTextX(s, x), y);
			y += offset;
		}
		
	}

	public float getFontSize(String moveName, float targetWidth) {
	    float fontSize = g2.getFont().getSize2D(); // Default font size

	    FontMetrics metrics = g2.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (int) fontSize));
	    int textWidth = metrics.stringWidth(moveName);

	    // Reduce font size until the text fits within the target width
	    while (textWidth > targetWidth && fontSize > 1) {
	        fontSize -= 1; // Decrease font size
	        metrics = g2.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, (int) fontSize));
	        textWidth = metrics.stringWidth(moveName);
	    }

	    return fontSize;
	}

	public void setNickname(Pokemon p) {
		int x = gp.tileSize * 3;
		int y = gp.tileSize * 5;
		int width = gp.tileSize*10;
		int height = gp.tileSize*3;
		
		drawSubWindow(x, y, width, height);
		
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
	
	public void drawMoveOptions(Move m, Pokemon p) {
		if (p.knowsMove(m)) {
			showMoveOptions = false;
			gp.battleUI.currentTask = null;
			return;
		}
		drawMoveOptions(m, p, p.nickname + " wants to learn " + m.toString() + ":");
	}
	
	public void drawMoveOptions(Move m, Pokemon p, String header) {
		ArrayList<Move> movebankList = p.movebankAsList();
		
		int x = gp.tileSize * 4;
		int y = 0;
		int width = gp.tileSize * 8;
		int height = gp.tileSize * 5;
		if (m != null) height += gp.tileSize * 2;
		drawSubWindow(x, y, width, height);
		if (moveOption == 0) {
			drawMoveSummary(x - gp.tileSize * 1, height - gp.tileSize / 2, p, null, null, m);
		}
		int moveWidth = gp.tileSize * 10 / 3;
		int moveHeight = (int) (gp.tileSize * 1.25);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(24F));
		for (String s : Item.breakString(header, 40).split("\n")) {
			g2.drawString(s, getCenterAlignedTextX(s, (x - gp.tileSize / 2) + width / 2), y);
			y += gp.tileSize / 2;
		}
		if (m != null) {
			x += gp.tileSize * 11 / 6;
			g2.setColor(m.mtype.getColor());
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
			y += gp.tileSize * 1.5;
			x -= gp.tileSize * 11 / 6;
		}
		int startX = x;
		int max = 0;
		
		for (int i = 1; i <= p.moveset.length; i++) {
			Moveslot ms = p.moveset[i - 1];
			if (ms != null) {
				g2.setFont(g2.getFont().deriveFont(24F));
				if (!gp.player.p.hasTM(ms.move) && !movebankList.contains(ms.move)) {
		        	g2.setPaint(new GradientPaint(x, y, ms.move.mtype.getColor(), x + moveWidth, y + moveHeight, new Color(245, 225, 210)));
		        } else {
		        	g2.setColor(ms.move.mtype.getColor());	
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
		            drawMoveSummary((int) (startX - gp.tileSize * 1.5), height - gp.tileSize / 2, p, null, ms, null);
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
						Task t = Pokemon.createTask(Task.TEXT, p.nickname + " did not learn " + m.toString() + ".");
						Pokemon.insertTask(t, gp.battleUI.tasks.indexOf(gp.battleUI.currentTask) + 1);
					} else {
						Task t = Pokemon.createTask(Task.TEXT, Item.breakString(p.nickname + " learned " + m.toString() + " and forgot " + p.moveset[moveOption - 1].move.toString() + "!", 44));
						Pokemon.insertTask(t, gp.battleUI.tasks.indexOf(gp.battleUI.currentTask) + 1);
					}
				} else {
					if (moveOption == 0) {
						gp.ui.showMessage(p.nickname + " did not learn " + m.toString() + ".");
					} else {
						gp.ui.showMessage(Item.breakString(p.nickname + " learned " + m.toString() + " and forgot " + p.moveset[moveOption - 1].move.toString() + "!", 44));
					}
				}
				if (moveOption > 0) p.moveset[moveOption - 1] = new Moveslot(m);
				moveOption = -1;
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
                	newP.battled = true;
                	gp.player.p.current = newP;
                	gp.battleUI.user = newP;
                	gp.battleUI.userHP = newP.currentHP;
                	gp.battleUI.maxUserHP = newP.getStat(0);
                }
                newP.checkMove(1);
		        
		        currentTask = null;
			}
		}
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-24, y);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				Task text = Pokemon.createTask(Task.TEXT, t.p.nickname + " did not evolve.");
				Pokemon.insertTask(text, 0);
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
		drawToolTips(0, (int) (gp.tileSize * 10.5), w, a, s, d);
	}	
	
	public void drawToolTips(int x, int y, String w, String a, String s, String d) {
		if (!gp.keyH.shiftPressed) return;
		int num = 0;
		boolean sdSame = false;
		if (s != null && s.equals(d)) {
			sdSame = true;
			d = null;
		}
		
		if (w != null) num++;
		if (a != null) num++;
		if (s != null) num++;
		if (d != null) num++;
		
		int width = (int) (gp.tileSize * 1.5 * num + gp.tileSize * 2);
		if (sdSame) width += gp.tileSize;
		int height = (int) (gp.tileSize * 1.5);
		drawSubWindow(x, y, width, height);
		
		int textWidth = (int) (gp.tileSize * 1.10);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		if (w != null) {
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString("[W]", x, y);
			x += gp.tileSize * 0.75;
			g2.setFont(g2.getFont().deriveFont(getFontSize(w, textWidth)));
			g2.drawString(w, x, y);
			x += gp.tileSize * 1.15;
		}
		
		if (s != null) {
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString("[S]", x, y);
			x += gp.tileSize * 0.65;
			if (sdSame) {
				x -= gp.tileSize * 0.2;
				g2.drawString("/[D]", x, y);
				x += gp.tileSize * 0.85;
			}
			g2.setFont(g2.getFont().deriveFont(getFontSize(s, textWidth)));
			g2.drawString(s, x, y);
			x += gp.tileSize * 1.25;
		}
		
		if (a != null) {
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString("[A]", x, y);
			x += gp.tileSize * 0.65;
			g2.setFont(g2.getFont().deriveFont(getFontSize(a, textWidth)));
			g2.drawString(a, x, y);
			x += gp.tileSize * 1.25;
		}
		
		if (d != null) {
			g2.setFont(g2.getFont().deriveFont(24F));
			g2.drawString("[D]", x, y);
			x += gp.tileSize * 0.65;
			g2.setFont(g2.getFont().deriveFont(getFontSize(d, textWidth)));
			g2.drawString(d, x, y);
		}
	}
}
