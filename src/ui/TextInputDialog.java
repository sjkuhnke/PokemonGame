package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import overworld.GamePanel;
import overworld.Sound;
import util.ToolTip;

public class TextInputDialog extends TitleScreen {
	private StringBuilder text;
	private String title;
	private String placeholder;
	private int maxLength;
	private boolean active;
	private boolean naming;
	private int pulseCounter;
	
	private static final int NAMING = 0;
	private static final int CANCEL = 1;
	private static final int CONFIRM = 2;
	
	public interface InputCallback {
		void onConfirm(String result);
		void onCancel();
	}
	
	private InputCallback callback;
	
	public TextInputDialog(GamePanel gp, String title, String initialText, 
		String placeholder, int maxLength, Color textColor) {
		super(gp, false);
		
		this.title = title;
		this.text = new StringBuilder(initialText != null ? initialText : "");
		this.placeholder = placeholder;
		this.maxLength = maxLength;
		this.active = false;
		this.pulseCounter = 0;
		this.textColor = textColor;
	}
	
	public void show(InputCallback callback) {
		this.callback = callback;
		this.active = true;
		this.naming = commandNum == NAMING;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isNaming() {
		return active && naming;
	}
	
	public void update() {
		if (!active) return;
		
		pulseCounter++;
		if (pulseCounter > 120) pulseCounter = 0;
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			commandNum = NAMING;
			pulseCounter = 0;
			naming = commandNum == NAMING;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			commandNum++;
			if (commandNum > CONFIRM) {
				commandNum = NAMING;
			}
			naming = commandNum == NAMING;
		}
		
		if (gp.keyH.leftPressed || gp.keyH.rightPressed) {
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			if (commandNum == CANCEL) {
				commandNum = CONFIRM;
			} else if (commandNum == CONFIRM) {
				commandNum = CANCEL;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (commandNum == CONFIRM) {
				confirm();
			} else if (commandNum == CANCEL) {
				cancel();
			}
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			if (commandNum != NAMING) {
				cancel();
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		if (!active) return;
		
		this.g2 = g2;
		
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		int panelWidth = gp.tileSize * 10;
		int panelHeight = gp.tileSize * 5;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		int fieldX = panelX + gp.tileSize / 4;
		int fieldY = panelY + (int)(gp.tileSize * 1.75);
		int fieldWidth = panelWidth - gp.tileSize / 2;
		int fieldHeight = gp.tileSize;
		boolean selected = commandNum == NAMING;
		
		if (selected) {
			drawPanelWithBorder(fieldX, fieldY, fieldWidth, fieldHeight, backgroundOpacity, textColor);
		} else {
			g2.setColor(new Color(30, 30, 30, 200));
			g2.fillRect(fieldX, fieldY, fieldWidth, fieldHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
			g2.drawRect(fieldX, fieldY, fieldWidth, fieldHeight);
		}
		
		g2.setFont(g2.getFont().deriveFont(24F));
		String displayText = text.length() == 0 ? placeholder : text.toString();
		Color textCol = text.length() == 0 ? new Color(150, 150, 150) : textColor;
		
		int textX = fieldX + 10;
		int textY = fieldY + gp.tileSize * 2/3;
		drawOutlinedText(displayText, textX, textY, textCol, Color.BLACK);
		
		if (text.length() > 0 && naming) {
			int cursorX = textX + getTextWidth(text.toString()) + gp.tileSize / 16;
			float alpha = 0.5f + (float)(Math.sin(pulseCounter * 0.15) * 0.5);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.setColor(textCol);
			g2.fillRect(cursorX, fieldY + 5, 3, fieldHeight - 10);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
		
		g2.setFont(g2.getFont().deriveFont(14F));
		String countText = text.length() + " / " + maxLength;
		int countX = fieldX + fieldWidth - getTextWidth(countText) - 10;
		int countY = fieldY + fieldHeight + gp.tileSize / 3;
		Color countColor = text.length() >= maxLength ? new Color(255, 100, 100) : new Color(150, 150, 150);
		drawOutlinedText(countText, countX, countY, countColor, Color.BLACK);
		
		// Cancel Button
		selected = commandNum == CANCEL;
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
		textCol = textColor;
		String cancelText = "Cancel";
		int buttonWidth = gp.tileSize * 3;
		int buttonHeight = (int)(gp.tileSize * 0.75);
		int buttonX = (gp.screenWidth - buttonWidth) / 2 - buttonWidth * 2/3;
		int buttonY = fieldY + gp.tileSize * 2;
		if (selected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight,
				backgroundOpacity + 50, textCol);
		} else {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight,
				backgroundOpacity - 30, new Color(100, 100, 100));
		}
		int cancelX = getCenterAlignedTextX(cancelText, buttonX + buttonWidth / 2);
		int cancelY = buttonY + (int)(gp.tileSize * 0.6);
		drawOutlinedText(cancelText, cancelX, cancelY,
			selected ? textCol : new Color(200, 200, 200), Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(16F));
		
		// Confirm Button
		selected = commandNum == CONFIRM;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String confirmText = "Confirm";
		buttonX = (gp.screenWidth - buttonWidth) / 2 + buttonWidth * 2/3;
		if (selected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight,
				backgroundOpacity + 50, textCol);
		} else {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight,
				backgroundOpacity - 30, new Color(100, 100, 100));
		}
		int confirmX = getCenterAlignedTextX(confirmText, buttonX + buttonWidth / 2);
		drawOutlinedText(confirmText, confirmX, cancelY,
			selected ? textCol : new Color(200, 200, 200), Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(16F));
		
		java.util.ArrayList<ToolTip> toolTips = new java.util.ArrayList<>();
		toolTips.add(new ToolTip(gp, "Confirm", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Cancel", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
	}
	
	public void handleKeyInput(char c) {
		if (text.length() < maxLength && !Character.isISOControl(c)) {
			text.append(c);
		}
		gp.playSFX(Sound.S_TYPE);
	}
	
	public void handleBackspace() {
		if (text.length() > 0) {
			text.deleteCharAt(text.length() - 1);
		}
		gp.playSFX(Sound.S_BACKSPACE);
	}
	
	private void confirm() {
		active = false;
		if (callback != null) {
			callback.onConfirm(text.toString());
		}
		commandNum = 0;
		gp.playSFX(Sound.S_MENU_CON);
	}
	
	private void cancel() {
		active = false;
		if (callback != null) {
			callback.onCancel();
		}
		commandNum = 0;
		gp.playSFX(Sound.S_MENU_CAN);
	}

	@Override
	public void showMessage(String message) {
		System.out.println(message);
	}
}