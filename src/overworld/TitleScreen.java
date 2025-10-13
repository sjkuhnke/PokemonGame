package overworld;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import docs.TrainerDoc;
import entity.PlayerCharacter;
import pokemon.Player;
import pokemon.Pokemon;
import util.SaveManager;
import util.ToolTip;

public class TitleScreen extends AbstractUI {
	private Image backgroundImage;
	private Color textColor;
	private int backgroundOpacity = 180;
	
	// MENU STATES
	private static final int MAIN_MENU = 0;
	private static final int SETTINGS_MENU = 1;
	private static final int MANAGE_MENU = 2;
	private static final int SAVES_MENU = 3;
	
	private boolean showMessage;
	private int menuState = MAIN_MENU;
	private int menuNum = -1;
	
	// SAVE FILE DATA
	private ArrayList<String> saveFiles;
	private int selectedSaveIndex = 0;
	private Player previewPlayer;
	
	// DOC CHECKBOXES
	private boolean[] docOptions = new boolean[8];
	
	// MAIN MENU OPTIONS
	private static final int MAIN_CONTINUE = 0;
	private static final int MAIN_NEW_GAME = 1;
	private static final int MAIN_MANAGE = 2;
	private static final int MAIN_SETTINGS = 3;
	private static final int MAIN_SAVE_SELECT = 4;
	private static final int MAIN_DOC_START = 5;
	private static final int MAIN_DOC_END = 12;
	
	// SAVES MENU
	private int savesMenuScroll = 0;
	private static final int MAX_VISIBLE_SAVES = 8;
	
	// MANAGE MENU OPTIONS
	private static final int MANAGE_RENAME = 0;
	private static final int MANAGE_DELETE = 1;
	private static final int MANAGE_OPEN_LOCATION = 2;
	private static final int MANAGE_CANCEL = 3;
	private int manageMenuNum = 0;
	
	// ANIMATION
	private int pulseCounter = 0;
	
	public TitleScreen(GamePanel gp) {
		this.gp = gp;
		loadBackground();
		loadSaveFiles();
	}

	private void loadBackground() {
		try {
			backgroundImage = ImageIO.read(getClass().getResourceAsStream("/gen/background5.png"));
			textColor = new Color(255, 251, 4);
		} catch (IOException e) {
			e.printStackTrace();
			textColor = Color.WHITE;
		}
	}
	

	private void loadSaveFiles() {
		saveFiles = SaveManager.getSaveFiles();
		if (!saveFiles.isEmpty()) {
			loadPreviewPlayer(saveFiles.get(0));
		}
	}

	private void loadPreviewPlayer(String fileName) {
		previewPlayer = SaveManager.loadPlayer(fileName);
	}

	private void updateMainMenu() {
		pulseCounter++;
		if (pulseCounter > 120) pulseCounter = 0;
		
		int maxOptions = MAIN_DOC_END;
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			menuNum--;
			if (menuNum < 0) menuNum = maxOptions;
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			menuNum++;
			if (menuNum > maxOptions) menuNum = 0;
		}
		
		if (menuNum >= MAIN_DOC_START && menuNum <= MAIN_DOC_END) {
			int docIndex = menuNum - MAIN_DOC_START;
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				docOptions[docIndex] = !docOptions[docIndex];
			}
			if (gp.keyH.leftPressed && docIndex >= docOptions.length / 2) {
				gp.keyH.leftPressed = false;
				menuNum -= docOptions.length / 2;
			}
			if (gp.keyH.rightPressed && docIndex < docOptions.length / 2) {
				gp.keyH.rightPressed = false;
				menuNum += docOptions.length / 2;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			handleMainMenuSelection();
		}
	}

	private void handleMainMenuSelection() {
		switch (menuNum) {
		case MAIN_CONTINUE:
			if (saveFiles.isEmpty()) {
				showMessage("No save files found!");
			} else {
				Main.loadGame(saveFiles.get(selectedSaveIndex), docOptions, false);
			}
			break;
		case MAIN_NEW_GAME:
			handleNewGame();
			break;
		case MAIN_MANAGE:
			if (saveFiles.isEmpty()) {
				showMessage("No save files to manage!");
			} else {
				menuState = MANAGE_MENU;
				manageMenuNum = 0;
			}
			break;
		case MAIN_SETTINGS:
			menuState = SETTINGS_MENU;
			settingsState = 0;
			break;
		case MAIN_SAVE_SELECT:
			if (!saveFiles.isEmpty()) {
				menuState = SAVES_MENU;
				savesMenuScroll = 0;
			}
		}
	}

	private void handleNewGame() {
		String save = null;
		boolean nuzlocke = false;
		
		String input = JOptionPane.showInputDialog(gp,
			"Enter a new save file name (A-Z, 0-9, _, and - are permitted, ≤ 20 characters):");
		
		if (input == null) return;
		
		save = input.trim();
		
		if (!isValidFileName(save)) {
			showMessage("Invalid file name. Please use only A-Z, 0-9, underscores (_), and hyphens (-), and make sure it's ≤ 20 characters.");
			return;
		}
		
		if (saveFiles.contains(save + ".dat")) {
			showMessage("Save file already exists! Select it and press 'Continue'.");
			return;
		}
		
		int nuzlockeChoice = JOptionPane.showConfirmDialog(gp,
			"Enable Nuzlocke Mode?",
			"Nuzlocke",
			JOptionPane.YES_NO_OPTION);
		nuzlocke = (nuzlockeChoice == JOptionPane.YES_OPTION);
		
		save += ".dat";
		Main.loadGame(save, docOptions, nuzlocke);
	}
	
	private void updateSavesMenu() {
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			selectedSaveIndex--;
			if (selectedSaveIndex < 0) selectedSaveIndex = saveFiles.size() - 1;
			
			if (selectedSaveIndex < savesMenuScroll) {
				savesMenuScroll = selectedSaveIndex;
			} else if (selectedSaveIndex >= savesMenuScroll + MAX_VISIBLE_SAVES) {
				savesMenuScroll = selectedSaveIndex - MAX_VISIBLE_SAVES + 1;
			}
			
			loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			selectedSaveIndex++;
			if (selectedSaveIndex >= saveFiles.size()) selectedSaveIndex = 0;
			
			if (selectedSaveIndex < savesMenuScroll) {
				savesMenuScroll = 0;
			} else if (selectedSaveIndex >= savesMenuScroll + MAX_VISIBLE_SAVES) {
				savesMenuScroll = selectedSaveIndex - MAX_VISIBLE_SAVES + 1;
			}
			
			loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			menuState = MAIN_MENU;
			menuNum = MAIN_SAVE_SELECT;
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			menuState = MAIN_MENU;
			menuNum = MAIN_SAVE_SELECT;
		}
	}
	
	private void updateManageMenu() {
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			manageMenuNum--;
			if (manageMenuNum < 0) manageMenuNum = MANAGE_CANCEL;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			manageMenuNum++;
			if (manageMenuNum > MANAGE_CANCEL) manageMenuNum = 0;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			handleManageMenuSelection();
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			menuState = MAIN_MENU;
			manageMenuNum = 0;
		}
	}

	private void handleManageMenuSelection() {
		String selectedFile = saveFiles.get(selectedSaveIndex);

		switch (manageMenuNum) {
		case MANAGE_RENAME:
			handleRenameSave(selectedFile);
			break;
		case MANAGE_DELETE:
			handleDeleteSave(selectedFile);
			break;
		case MANAGE_OPEN_LOCATION:
			SaveManager.showInExplorer(selectedFile);
			break;
		case MANAGE_CANCEL:
			menuState = MAIN_MENU;
			break;
		}
	}

	private void handleRenameSave(String oldName) {
		String newName = JOptionPane.showInputDialog(gp, "Enter the new file name:");
		
		if (newName == null || !isValidFileName(newName)) {
			showMessage("Invalid file name. Please use only alphanumeric characters, underscores, and hyphens.");
			return;
		}
		
		try {
			SaveManager.renameSave(oldName, newName + ".dat");
			loadSaveFiles();
			showMessage("Save file renamed successfully!");
		} catch (FileAlreadyExistsException e) {
			showMessage("File with the specified name already exists. Please choose a different name.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleDeleteSave(String fileName) {
		int confirm = JOptionPane.showConfirmDialog(gp,
			"Are you sure you want to delete " + fileName + "?",
			"Confirm Deletion",
			JOptionPane.YES_NO_OPTION);
		
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				SaveManager.deleteSave(fileName);
				loadSaveFiles();
				if (selectedSaveIndex >= saveFiles.size() && selectedSaveIndex > 0) {
					selectedSaveIndex--;
				}
				if (!saveFiles.isEmpty()) {
					loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
				}
				showMessage("Save file deleted successfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		menuState = MAIN_MENU;
	}
	
	private boolean isValidFileName(String fileName) {
		return fileName.matches("[a-zA-Z0-9_\\-]+") &&
				fileName.length() <= 20 &&
				!fileName.trim().isEmpty();
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setFont(gp.marumonica);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}
		
		if (showMessage) {
			showMessageControls();
		}
		
		switch(menuState) {
		case MAIN_MENU:
			drawMainMenu();
			break;
		case SETTINGS_MENU:
			drawSettingsMenu();
			break;
		case SAVES_MENU:
			drawSavesMenu();
			break;
		case MANAGE_MENU:
			drawManageMenu();
			break;
		}
		
		if (showMessage) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			drawDialogueScreen(true);
			drawToolTips("OK", null, "OK", "OK");
		}
		
		drawKeyStrokes();
	}

	private void showMessageControls() {
		if (gp.keyH.wPressed || gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			showMessage = false;
		}
	}

	private void drawMainMenu() {		
		g2.setColor(textColor);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		
		int previewX = gp.screenWidth - gp.tileSize * 7;
		int previewY = gp.tileSize * 5;
		drawSavePreviewCompact(previewX, previewY, gp.tileSize * 6);
		
		int buttonX = gp.tileSize / 2;
		int buttonY = gp.tileSize;
		int buttonSpacing = gp.tileSize;
		
		drawMenuButton(g2, "Continue", buttonX, buttonY, menuNum == MAIN_CONTINUE);
		buttonY += buttonSpacing;
		drawMenuButton(g2, "New Game", buttonX, buttonY, menuNum == MAIN_NEW_GAME);
		buttonY += buttonSpacing;
		drawMenuButton(g2, "Manage Save", buttonX, buttonY, menuNum == MAIN_MANAGE);
		buttonY += buttonSpacing;
		drawMenuButton(g2, "Settings", buttonX, buttonY, menuNum == MAIN_SETTINGS);
		
		buttonY += buttonSpacing * 2;
		drawSaveSelector(g2, buttonX, buttonY, menuNum == MAIN_SAVE_SELECT);
		
		buttonY += buttonSpacing;
		drawDocumentationSection(g2, buttonX, buttonY);
		
		drawControlsHint();
		
		updateMainMenu();
	}
	
	private void drawSavesMenu() {
		pulseCounter++;
		if (pulseCounter > 120) pulseCounter = 0;
		
		drawOverlay();
		
		int panelWidth = gp.tileSize * 12;
		int panelHeight = gp.tileSize * 10;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		String title = "Select Save File";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		int listX = panelX + gp.tileSize / 4;
		int listY = panelY + (int)(gp.tileSize * 1.75);
		int listWidth = panelWidth / 2 - gp.tileSize / 2;
		int itemHeight = (int)(gp.tileSize * 0.8);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		
		for (int i = 0; i < Math.min(MAX_VISIBLE_SAVES, saveFiles.size()); i++) {
			int fileIndex = savesMenuScroll + i;
			if (fileIndex >= saveFiles.size()) break;
			
			String fileName = saveFiles.get(fileIndex);
			boolean isSelected = fileIndex == selectedSaveIndex;
			
			int itemX = listX;
			int itemY = listY + i * itemHeight;
			
			if (isSelected) {
				drawPanelWithBorder(itemX, itemY, listWidth, itemHeight - 5, backgroundOpacity + 30, textColor);
			} else {
				g2.setColor(new Color(40, 40, 40, 180));
				g2.fillRect(itemX, itemY, listWidth, itemHeight - 5);
			}
			
			Color fileColor = isSelected ? textColor : new Color(200, 200, 200);
			int textY = itemY + itemHeight / 2 + 5;
			drawOutlinedText(fileName, itemX + 10, textY, fileColor, Color.BLACK);
		}
		
		if (saveFiles.size() > MAX_VISIBLE_SAVES) {
			int scrollbarX = listX + listWidth + 5;
			int scrollbarY = listY;
			int scrollbarWidth = 8;
			int scrollbarHeight = MAX_VISIBLE_SAVES * itemHeight - 5;
			
			g2.setColor(new Color(60, 60, 60));
			g2.fillRect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight);
			
			float thumbHeight = scrollbarHeight * ((float)MAX_VISIBLE_SAVES / saveFiles.size());
			float thumbY = scrollbarY + (scrollbarHeight - thumbHeight) *
				((float)savesMenuScroll / (saveFiles.size() - MAX_VISIBLE_SAVES));
			
			g2.setColor(textColor);
			g2.fillRect(scrollbarX, (int)thumbY, scrollbarWidth, (int)thumbHeight);
		}
		
		if (previewPlayer != null) {
			int previewX = panelX + panelWidth / 2 + gp.tileSize / 4;
			int previewY = listY;
			drawSavePreviewCompact(previewX, previewY, panelWidth / 2 - gp.tileSize / 2);
		}
		
		g2.setFont(g2.getFont().deriveFont(18F));
		ToolTip navigate = new ToolTip(gp, "Navigate", "/", false, gp.config.upKey, gp.config.downKey);
		ToolTip select = new ToolTip(gp, "Select", "", false, gp.config.wKey);
		ToolTip cancel = new ToolTip(gp, "Cancel", "/", false, gp.config.sKey, gp.config.dKey);
		String instructions = String.format("%s  •  %s  •  %s", navigate, select, cancel);
		
		int instrX = getCenterAlignedTextX(instructions, gp.screenWidth / 2);
		int instrY = panelY + panelHeight - gp.tileSize / 3;
		drawOutlinedText(instructions, instrX, instrY, new Color(180, 180, 180), Color.BLACK);
		
		updateSavesMenu();
	}
	
	private void drawSavePreviewCompact(int x, int y, int width) {
		int height = gp.tileSize * 6;
		
		g2.setColor(new Color(20, 20, 20, 200));
		g2.fillRect(x, y, width, height);
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(100, 100, 100));
		g2.drawRect(x, y, width, height);
		
		int contentX = x + gp.tileSize / 8;
		int contentY = y + gp.tileSize / 4;
		
		int iconSize = 80;
		for (int i = 0; i < 6; i++) {
			Pokemon p = previewPlayer.team[i];
			if (p != null) {
				Image sprite = TrainerDoc.getCachedSprite(p);
				if (sprite != null) {
					int spriteX = contentX + (i % 3) * (width / 3);
					int spriteY = contentY + (i / 3) * iconSize;
					g2.drawImage(sprite, spriteX, spriteY, iconSize, iconSize, null);
				}
			}
		}
		
		contentY += iconSize * 2 + gp.tileSize / 2;
		
		g2.setFont(g2.getFont().deriveFont(18F));
		PMap.getLoc(previewPlayer.currentMap,
			(int) Math.round(previewPlayer.getPosX() * 1.0 / gp.tileSize),
			(int) Math.round(previewPlayer.getPosY() * 1.0 / gp.tileSize));
		drawOutlinedText("Location:", contentX, contentY, new Color(150, 150, 150), Color.BLACK);
		contentY += gp.tileSize / 3;
		g2.setFont(g2.getFont().deriveFont(16F));
		drawOutlinedText(PlayerCharacter.currentMapName, contentX, contentY, textColor, Color.BLACK);
		
		contentY += gp.tileSize / 2;
		
		File saveFile = new File(SaveManager.getSavePath(saveFiles.get(selectedSaveIndex)).toString());
		long lastModified = saveFile.lastModified();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String lastModifiedStr = sdf.format(new Date(lastModified));
		g2.setFont(g2.getFont().deriveFont(14F));
		drawOutlinedText("Modified:", contentX, contentY, new Color(150, 150, 150), Color.BLACK);
		contentY += gp.tileSize / 3;
		drawOutlinedText(lastModifiedStr, contentX, contentY, new Color(180, 180, 180), Color.BLACK);
	}
	
	private void drawManageMenu() {
		pulseCounter++;
		if (pulseCounter > 120) pulseCounter = 0;
		
		drawOverlay();
		
		int panelWidth = gp.tileSize * 6;
		int panelHeight = gp.tileSize * 6;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String title = "Manage Save";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		String fileName = saveFiles.get(selectedSaveIndex);
		int fileX = getCenterAlignedTextX(fileName, gp.screenWidth / 2);
		int fileY = titleY + gp.tileSize * 2 / 3;
		drawOutlinedText(fileName, fileX, fileY, new Color(200, 200, 200), Color.BLACK);
		
		String[] options = {"Rename", "Delete", "Open Location", "Cancel"};
		int optionX = panelX + gp.tileSize / 2;
		int optionY = (int) (panelY + gp.tileSize * 2.5);
		int optionSpacing = (int)(gp.tileSize * 0.8);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		
		for (int i = 0; i < options.length; i++) {
			boolean isSelected = i == manageMenuNum;
			int currentY = optionY + i * optionSpacing;
			
			int bgX = optionX - gp.tileSize / 4;
			int bgY = currentY - gp.tileSize / 2;
			int bgWidth = panelWidth - gp.tileSize / 2;
			int bgHeight = (int)(gp.tileSize * 0.65);
			
			if (isSelected) {
				drawPanelWithBorder(bgX, bgY, bgWidth, bgHeight, 
					backgroundOpacity + 30, textColor);
			} else {
				g2.setColor(new Color(40, 40, 40, 150));
				g2.fillRect(bgX, bgY, bgWidth, bgHeight);
			}
			
			Color textCol = isSelected ? textColor : new Color(200, 200, 200);
			drawOutlinedText(options[i], optionX, currentY, textCol, Color.BLACK);
		}
		
		g2.setFont(g2.getFont().deriveFont(16F));
		ToolTip navigate = new ToolTip(gp, "Navigate", "/", false, gp.config.upKey, gp.config.downKey);
		ToolTip select = new ToolTip(gp, "Select", "", false, gp.config.wKey);
		ToolTip cancel = new ToolTip(gp, "Cancel", "/", false, gp.config.sKey, gp.config.dKey);
		String instructions = String.format("%s  •  %s  •  %s", navigate, select, cancel);
		int instrX = getCenterAlignedTextX(instructions, gp.screenWidth / 2);
		int instrY = panelY + panelHeight - gp.tileSize / 4;
		drawOutlinedText(instructions, instrX, instrY, new Color(180, 180, 180), Color.BLACK);
		
		updateManageMenu();
	}
	
	private void drawOverlay() {
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
	}
	
	private void drawControlsHint() {
		int hintX = gp.tileSize / 2;
		int hintY = gp.screenHeight - gp.tileSize * 3 / 4;
		
		float alpha = 0.5f + (float)(Math.sin(pulseCounter * 0.05) * 0.3);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		g2.setFont(g2.getFont().deriveFont(20F));
		
		ToolTip tooltipKey = new ToolTip(gp, "", "", false, gp.config.tooltipsKey);
		String hintText = "Hold " + tooltipKey.toString() + " to view controls";
		
		drawGlowingText(hintText, hintX, hintY, textColor, 2);
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	private void drawGlowingText(String text, int x, int y, Color color, int glowSize) {
		for (int i = glowSize; i > 0; i--) {
			float alpha = 0.1f * (glowSize - i + 1) / glowSize;
			g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha*255)));
			for (int dx = -i; dx <= i; dx++) {
				for (int dy = -i; dy <= i; dy++) {
					if (dx * dx + dy * dy <= i * i) {
						g2.drawString(text, x + dx, y + dy);
					}
				}
			}
		}
		
		g2.setColor(color);
		g2.drawString(text, x, y);
	}
	
	private void drawMenuButton(Graphics2D g2, String text, int x, int y, boolean selected) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int backgroundX = x - gp.tileSize / 4;
		int backgroundY = y - gp.tileSize * 5 / 8;
		int backgroundWidth = gp.tileSize * 5;
		int backgroundHeight = (int) (gp.tileSize * 0.75);
		
		if (selected) {
			drawPanelWithBorder(backgroundX, backgroundY, backgroundWidth, backgroundHeight, backgroundOpacity + 30, textColor);
			
			int glowInset = 4;
			g2.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
			g2.fillRect(backgroundX + glowInset, backgroundY + glowInset, backgroundWidth - glowInset * 2, backgroundHeight - glowInset * 2);
		} else {
			drawPanelWithBorder(backgroundX, backgroundY, backgroundWidth, backgroundHeight, backgroundOpacity - 50, new Color(100, 100, 100));
		}
		
		Color textCol = selected ? textColor : new Color(200, 200, 200);
		drawOutlinedText(text, x, y, textCol, Color.BLACK);
	}
	
	private void drawPanelWithBorder(int x, int y, int width, int height, int opacity, Color borderColor) {
		g2.setColor(new Color(0, 0, 0, Math.min(255, opacity)));
		g2.fillRect(x, y, width, height);
		
		g2.setStroke(new BasicStroke(3));
		g2.setColor(borderColor);
		g2.drawRect(x, y, width, height);
		
		g2.setColor(new Color(255, 255, 255, 30));
		g2.drawLine(x + 2, y + 2, x + width - 2, y + 2);
		g2.drawLine(x + 2, y + 2, x + 2, y + height - 2);
	}
	
	private void drawSaveSelector(Graphics2D g2, int x, int y, boolean selected) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		String saveText = saveFiles.isEmpty() ? "No Saves Available" : saveFiles.get(selectedSaveIndex);
		
		int boxWidth = gp.tileSize * 5;
		int boxHeight = gp.tileSize / 2;
		int boxX = x - gp.tileSize / 4;
		int boxY = y - boxHeight;
		
		if (selected) {
			drawPanelWithBorder(boxX, boxY, boxWidth, boxHeight, backgroundOpacity, textColor);
		} else {
			drawPanelWithBorder(boxX, boxY, boxWidth, boxHeight, backgroundOpacity - 50, new Color(100, 100, 100));
		}
		
		g2.setFont(g2.getFont().deriveFont(18F));
		drawOutlinedText("Save File:", x, y - boxHeight - 5, new Color(200, 200, 200), Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		Color textCol = selected ? textColor : new Color(180, 180, 180);
		FontMetrics fm = g2.getFontMetrics();
		int textX = boxX + (boxWidth - fm.stringWidth(saveText)) / 2;
		int textY = boxY + (boxHeight + fm.getAscent()) / 2 - 2;
		drawOutlinedText(saveText, textX, textY, textCol, Color.BLACK);
		
		if (selected && !saveFiles.isEmpty()) {
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			
			float arrowAlpha = 0.6f + (float)(Math.sin(pulseCounter * 0.1) * 0.4);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, arrowAlpha));
			
			String arrow = "▼";
			int arrowX = boxX + boxWidth + 10;
			drawOutlinedText(arrow, arrowX, textY, textColor, Color.BLACK);

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			g2.setFont(g2.getFont().deriveFont(14F));
			ToolTip wTip = new ToolTip(gp, "", "", false, gp.config.wKey);
			String hint = "Press " + wTip + " to view all saves";
			int hintX = x;
			int hintY = y + gp.tileSize / 3;
			drawOutlinedText(hint, hintX, hintY, new Color(150, 150, 150), Color.BLACK);
		}
	}
	
	private void drawDocumentationSection(Graphics2D g2, int x, int y) {
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
		boolean inDocumentation = menuNum >= MAIN_DOC_START && menuNum <= MAIN_DOC_END;
		drawOutlinedText("Generate Documentation:", x, y, inDocumentation ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		y += gp.tileSize / 2;
		g2.setFont(g2.getFont().deriveFont(18F));
		
		String[] docNames = {
			"Trainers", "Pokemon", "Encounters", "Moves",
			"Abilities", "Items", "Defensive Types", "Offensive Types"
		};
		
		int col1X = x;
		int col2X = gp.tileSize * 4;
		int rowHeight = gp.tileSize * 2 / 3;
		
		for (int i = 0; i < docNames.length; i++) {
			int docX = (i < 4) ? col1X : col2X;
			int docY = y + ((i % 4) * rowHeight);
			int docCommandNum = MAIN_DOC_START + i;
			
			boolean isSelected = menuNum == docCommandNum;
			
			int checkboxSize = gp.tileSize / 4;
			int checkboxX = docX;
			int checkboxY = docY - checkboxSize;
			
			if (isSelected) {
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.YELLOW);
				g2.drawRect(checkboxX - 2, checkboxY - 2, checkboxSize + 4, checkboxSize + 4);
			}
			
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.WHITE);
			g2.drawRect(checkboxX, checkboxY, checkboxSize, checkboxSize);
			
			if (docOptions[i]) {
				g2.setColor(new Color(0, 200, 100));
				g2.fillRect(checkboxX + 2, checkboxY + 2, checkboxSize - 4, checkboxSize - 4);
				
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2));
				int checkX = checkboxX + checkboxSize / 4;
				int checkY = checkboxY + checkboxSize / 2;
				g2.drawLine(checkX, checkY, checkX + checkboxSize / 4, checkY + checkboxSize / 4);
				g2.drawLine(checkX + checkboxSize / 4, checkY + checkboxSize / 4, checkX + checkboxSize / 2, checkY - checkboxSize / 4);
			}
			
			Color labelColor = isSelected ? textColor : new Color(200, 200, 200);
			drawOutlinedText(docNames[i], docX + checkboxSize + 8, docY, labelColor, Color.BLACK);
		}
	}
	
	private void drawSettingsMenu() {
		super.drawSettings();
		
		if (settingsState == 0) {
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				gp.config.saveConfig();
				settingsState = 0;
				menuState = MAIN_MENU;
			}
		} else if (settingsState == 1) {
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				boolean changed = gp.config.setKeys();
				commandNum = 0;
				settingsState = 0;
				if (changed) showMessage("Keybinds successfully updated!");
			}
		}
	}
	
	private void drawOutlinedText(String text, int x, int y, Color fillColor, Color outlineColor) {
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

	@Override
	public void showMessage(String message) {
		showMessage = true;
		currentDialogue = message;
	}
	
	@Override
	public void drawSubWindow(int x, int y, int width, int height, int opacity) {
		drawPanelWithBorder(x, y, width, height, opacity, textColor);
	}
}
