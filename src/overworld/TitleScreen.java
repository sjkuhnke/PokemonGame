package overworld;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import docs.TrainerDoc;
import entity.PlayerCharacter;
import pokemon.Player;
import pokemon.Pokemon;
import util.SaveManager;

public class TitleScreen {
	private GamePanel gp;
	private Image backgroundImage;
	private Image icon;
	private Color textColor;
	
	// MENU STATES
	private static final int MAIN_MENU = 0;
	private static final int SETTINGS_MENU = 1;
	private static final int DOCUMENTATION_MENU = 2;
	
	private int menuState = MAIN_MENU;
	private int commandNum = -1;
	
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
	
	public TitleScreen(GamePanel gp) {
		this.gp = gp;
		loadBackground();
		loadIcon();
		loadSaveFiles();
	}

	private void loadBackground() {
		try {
			Random rand = new Random();
			int random = rand.nextInt(4) + 1;
			backgroundImage = ImageIO.read(getClass().getResourceAsStream("/gen/background" + random + ".png"));
			Color[] textColors = {
				new Color(221, 184, 188),
				new Color(247, 229, 123),
				new Color(196, 197, 83),
				new Color(0, 77, 129)
			};
			textColor = textColors[random - 1];
		} catch (IOException e) {
			e.printStackTrace();
			textColor = Color.WHITE;
		}
	}
	
	private void loadIcon() {
		try {
			icon = ImageIO.read(getClass().getResourceAsStream("/gen/icon4.png"));
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public void update() {
		if (menuState == MAIN_MENU) {
			updateMainMenu();
		} else if (menuState == SETTINGS_MENU) {
			// TODO: settings handled by UI class
		} else if (menuState == DOCUMENTATION_MENU) {
			updateDocumentationMenu();
		}
	}

	private void updateMainMenu() {
		int maxOptions = MAIN_DOC_END;
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			commandNum--;
			if (commandNum < 0) commandNum = maxOptions;
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			commandNum++;
			if (commandNum > maxOptions) commandNum = 0;
		}
		
		if (commandNum == MAIN_SAVE_SELECT) {
			if (gp.keyH.leftPressed && !saveFiles.isEmpty()) {
				gp.keyH.leftPressed = false;
				selectedSaveIndex--;
				if (selectedSaveIndex < 0) selectedSaveIndex = saveFiles.size() - 1;
				loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
			}
			if (gp.keyH.rightPressed && !saveFiles.isEmpty()) {
				gp.keyH.rightPressed = false;
				selectedSaveIndex++;
				if (selectedSaveIndex >= saveFiles.size()) selectedSaveIndex = 0;
				loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
			}
		}
		
		if (commandNum >= MAIN_DOC_START && commandNum <= MAIN_DOC_END) {
			int docIndex = commandNum - MAIN_DOC_START;
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				docOptions[docIndex] = !docOptions[docIndex];
			}
			if (gp.keyH.leftPressed && docIndex >= docOptions.length / 2) {
				gp.keyH.leftPressed = false;
				commandNum -= docOptions.length / 2;
			}
			if (gp.keyH.rightPressed && docIndex < docOptions.length / 2) {
				gp.keyH.rightPressed = false;
				commandNum += docOptions.length / 2;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			handleMainMenuSelection();
		}
	}

	private void handleMainMenuSelection() {
		switch (commandNum) {
		case MAIN_CONTINUE:
			if (saveFiles.isEmpty()) {
				JOptionPane.showMessageDialog(gp, "No save files found!");
			} else {
				Main.loadGame(saveFiles.get(selectedSaveIndex), docOptions, false);
			}
			break;
		case MAIN_NEW_GAME:
			handleNewGame();
			break;
		case MAIN_MANAGE:
			if (saveFiles.isEmpty()) {
				JOptionPane.showMessageDialog(gp, "No save files to manage!");
			} else {
				handleManageSave();
			}
			break;
		case MAIN_SETTINGS:
			menuState = SETTINGS_MENU;
			gp.ui.settingsState = 0;
			gp.ui.commandNum = 0;
			break;
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
			JOptionPane.showMessageDialog(gp,
				"Invalid file name. Please use only A-Z, 0-9, underscores (_), and hyphens (-), and make sure it's ≤ 20 characters.");
			return;
		}
		
		if (saveFiles.contains(save + ".dat")) {
			JOptionPane.showMessageDialog(gp,
				"Save file already exists! Select it and press 'Continue'.");
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
	
	private void handleManageSave() {
		String selectedFile = saveFiles.get(selectedSaveIndex);
		String[] options = {"Rename", "Delete", "Open Location", "Cancel"};
		int choice = JOptionPane.showOptionDialog(gp,
			"Select an action for " + selectedFile + ":",
			"File Management",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			options,
			options[2]);
		
		switch (choice) {
		case 0:
			handleRenameSave(selectedFile);
			break;
		case 1:
			handleDeleteSave(selectedFile);
			break;
		case 2:
			SaveManager.showInExplorer(selectedFile);
			break;
		}
	}

	private void handleRenameSave(String oldName) {
		String newName = JOptionPane.showInputDialog(gp, "Enter the new file name:");
		
		if (newName == null || !isValidFileName(newName)) {
			JOptionPane.showMessageDialog(gp,
				"Invalid file name. Please use only alphanumeric characters, underscores, and hyphens.");
			return;
		}
		
		try {
			SaveManager.renameSave(oldName, newName + ".dat");
			loadSaveFiles();
		} catch (FileAlreadyExistsException e) {
			JOptionPane.showMessageDialog(gp,
				"File with the specified name already exists. Please choose a different name.");
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateDocumentationMenu() {
		// TODO
	}
	
	private boolean isValidFileName(String fileName) {
		return fileName.matches("[a-zA-Z0-9_\\-]+") &&
				fileName.length() <= 20 &&
				!fileName.trim().isEmpty();
	}
	
	public void draw(Graphics2D g2) {
		g2.setFont(gp.marumonica);
		gp.ui.g2 = g2;
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}
		
		if (menuState == MAIN_MENU) {
			drawMainMenu(g2);
		} else if (menuState == SETTINGS_MENU) {
			drawSettingsMenu(g2);
		}
		
		gp.ui.drawKeyStrokes();
	}

	private void drawMainMenu(Graphics2D g2) {
		if (icon != null) {
			int iconSize = gp.tileSize * 2;
			int iconX = gp.tileSize;
			int iconY = gp.tileSize / 2;
			g2.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
		}
		
		g2.setColor(textColor);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		String title = Main.gameTitle;
		int titleX = gp.tileSize * 4;
		int titleY = gp.tileSize * 2;
		drawOutlinedText(g2, title, titleX, titleY, textColor, Color.BLACK);
		
		int previewX = gp.screenWidth - gp.tileSize * 7;
		int previewY = gp.tileSize * 3;
		drawSavePreview(g2, previewX, previewY);
		
		int buttonX = gp.tileSize;
		int buttonY = gp.tileSize * 4;
		int buttonSpacing = gp.tileSize;
		
		drawButton(g2, "Continue", buttonX, buttonY, commandNum == MAIN_CONTINUE);
		buttonY += buttonSpacing;
		drawButton(g2, "New Game", buttonX, buttonY, commandNum == MAIN_NEW_GAME);
		buttonY += buttonSpacing;
		drawButton(g2, "Manage Save", buttonX, buttonY, commandNum == MAIN_MANAGE);
		buttonY += buttonSpacing;
		drawButton(g2, "Settings", buttonX, buttonY, commandNum == MAIN_SETTINGS);
		
		buttonY += buttonSpacing;
		drawSaveSelector(g2, buttonX, buttonY, commandNum == MAIN_SAVE_SELECT);
		
		buttonY += buttonSpacing;
		drawDocumentationSection(g2, buttonX, buttonY);
	}
	
	private void drawSavePreview(Graphics2D g2, int x, int y) {
		int width = gp.tileSize * 6;
		int height = gp.tileSize * 5;
		
		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRect(x, y, width, height);
		g2.setColor(textColor);
		g2.drawRect(x, y, width, height);
		
		if (previewPlayer == null || saveFiles.isEmpty()) {
			g2.setFont(g2.getFont().deriveFont(20F));
			drawOutlinedText(g2, "No Save File", x + gp.tileSize / 2, y + gp.tileSize, textColor, Color.BLACK);
			return;
		}
		
		int iconSize = gp.tileSize * 5/3;
		int iconX = x + gp.tileSize / 4;
		int iconY = y + gp.tileSize / 2;
		
		for (int i = 0; i < 6; i++) {
			Pokemon p = previewPlayer.team[i];
			if (p != null) {
				Image sprite = TrainerDoc.getCachedSprite(p);
				if (sprite != null) {
					g2.drawImage(sprite, iconX + (i % 3) * iconSize, iconY + (i / 3) * iconSize, iconSize, iconSize, null);
				};
			}
		}
		
		iconY += iconSize * 2 + gp.tileSize / 2;
		g2.setFont(g2.getFont().deriveFont(20F));
		PMap.getLoc(previewPlayer.currentMap,
			(int) Math.round(previewPlayer.getPosX() * 1.0 / gp.tileSize),
			(int) Math.round(previewPlayer.getPosY() * 1.0 / gp.tileSize));
		drawOutlinedText(g2, PlayerCharacter.currentMapName, iconX, iconY,
			textColor, Color.BLACK);
		
		iconY += gp.tileSize / 2;
		File saveFile = new File(SaveManager.getSavePath(saveFiles.get(selectedSaveIndex)).toString());
		long lastModified = saveFile.lastModified();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String lastModifiedStr = sdf.format(new Date(lastModified));
		g2.setFont(g2.getFont().deriveFont(16F));
		drawOutlinedText(g2, "Modified: " + lastModifiedStr, iconX, iconY,
			textColor, Color.BLACK);
	}
	
	private void drawButton(Graphics2D g2, String text, int x, int y, boolean selected) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		if (selected) {
			g2.setColor(Color.YELLOW);
			g2.drawString(">", x - gp.tileSize / 2, y);
		}
		
		drawOutlinedText(g2, text, x, y, textColor, Color.BLACK);
	}
	
	private void drawSaveSelector(Graphics2D g2, int x, int y, boolean selected) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		if (selected) {
			g2.setColor(Color.YELLOW);
			g2.drawString(">", x - gp.tileSize / 2, y);
		}
		
		String saveText = "Save File: ";
		if (saveFiles.isEmpty()) {
			saveText += "None";
		} else {
			saveText += saveFiles.get(selectedSaveIndex);
		}
		
		drawOutlinedText(g2, saveText, x, y, textColor, Color.BLACK);
		
		if (selected && !saveFiles.isEmpty()) {
			g2.setFont(g2.getFont().deriveFont(16F));
			String hint = "< Use arrow keys >";
			FontMetrics fm = g2.getFontMetrics();
			int hintX = gp.ui.getCenterAlignedTextX(saveText, x + fm.stringWidth(saveText) / 2);
			y += gp.tileSize / 2;
			drawOutlinedText(g2, hint, hintX, y, Color.LIGHT_GRAY, Color.BLACK);
		}
	}
	
	private void drawDocumentationSection(Graphics2D g2, int x, int y) {
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
		drawOutlinedText(g2, "Generate Documentation:", x, y, textColor, Color.BLACK);
		
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
			
			if (commandNum == docCommandNum) {
				g2.setColor(Color.YELLOW);
				g2.drawString(">", docX - gp.tileSize / 2, docY);
			}
			
			int checkboxSize = gp.tileSize/4;
			g2.setColor(Color.WHITE);
			g2.drawRect(docX, docY - checkboxSize, checkboxSize, checkboxSize);
			
			if (docOptions[i]) {
				g2.setColor(Color.GREEN);
				g2.fillRect(docX + 2, docY - checkboxSize + 2, checkboxSize - 4, checkboxSize - 4);
			}
			
			drawOutlinedText(g2, docNames[i], docX + checkboxSize + 8, docY, textColor, Color.BLACK);
		}
	}
	
	private void drawSettingsMenu(Graphics2D g2) {
		gp.ui.drawSettings();
	}
	
	private void drawOutlinedText(Graphics2D g2, String text, int x, int y, Color fillColor, Color outlineColor) {
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
}
