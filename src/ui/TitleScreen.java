package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

import docs.TrainerDoc;
import entity.PlayerCharacter;
import overworld.GamePanel;
import overworld.Main;
import overworld.PMap;
import overworld.Sound;
import pokemon.Player;
import pokemon.Pokemon;
import util.SaveManager;
import util.ToolTip;

public class TitleScreen extends AbstractUI {
	private Image backgroundImage;
	public TextInputDialog textInputDialog;
	
	// MENU STATES
	private static final int MAIN_MENU = 0;
	private static final int SETTINGS_MENU = 1;
	private static final int MANAGE_MENU = 2;
	private static final int SAVES_MENU = 3;
	private static final int SORT_SAVES_MENU = 4;
	private static final int NEW_GAME_MENU = 5;
	
	private boolean showMessage;
	private int menuState = MAIN_MENU;
	private int menuNum;
	
	// NEW GAME
	private String saveFileName = "";
	private int newGameMenuNum;
	public boolean nuzlockeMode;
	public int difficultyLevel;
	public boolean banShedinja;
	public boolean banBatonPass;
	public boolean allowRevives;
	public boolean buyableRevives;
	public int levelCapBonus;
	
	// SAVE FILE DATA
	private ArrayList<String> saveFiles;
	private int selectedSaveIndex = 0;
	private Player previewPlayer;
	
	// DOC CHECKBOXES
	public boolean[] docOptions = new boolean[8];
	public boolean generateExcel = false;
	
	// DELETE CONFIRM FIELDS
	private boolean showDeleteConfirm;
	private String fileToDelete;
	private int deleteConfirmNum;
	
	// MAIN MENU OPTIONS
	private static final int MAIN_CONTINUE = 0;
	private static final int MAIN_NEW_GAME = 1;
	private static final int MAIN_MANAGE = 2;
	private static final int MAIN_SETTINGS = 3;
	private static final int MAIN_SAVE_SELECT = 4;
	private static final int MAIN_DOC_START = 5;
	private static final int MAIN_DOC_END = 12;
	private static final int MAIN_EXCEL_TOGGLE = 13;
	
	// NEW GAME MENU OPTIONS
	private static final int NG_SAVE_NAME = 0;
	private static final int NG_NUZLOCKE_TOGGLE = 1;
	private static final int NG_DIFFICULTY = 2;
	private static final int NG_BAN_SHEDINJA = 3;
	private static final int NG_BAN_BATON_PASS = 4;
	private static final int NG_ALLOW_REVIVES = 5;
	private static final int NG_BUYABLE_REVIVES = 6;
	private static final int NG_LEVEL_CAP = 7;
	private static final int NG_START = 8;
	
	// NEW GAME SCROLL
	private int newGameMenuScroll = 0;
	private static final int MAX_VISIBLE_NG_OPTIONS = 5;
	
	// SAVES MENU
	private int savesMenuScroll = 0;
	private static final int MAX_VISIBLE_SAVES = 8;
	
	// SAVE SORT OPTIONS
	private static final int SORT_NEW_OLD = 0;
	private static final int SORT_OLD_NEW = 1;
	private static final int SORT_A_Z = 2;
	private static final int SORT_Z_A = 3;
	private static final int SORT_REFRESH = 4;
	private static final int SORT_BACK = 5;
	private int sortMenuNum = 0;
	
	// MANAGE MENU OPTIONS
	private static final int MANAGE_RENAME = 0;
	private static final int MANAGE_EXPORT = 1;
	private static final int MANAGE_OPEN_LOCATION = 2;
	private static final int MANAGE_DELETE = 3;
	private static final int MANAGE_CANCEL = 4;
	private int manageMenuNum = 0;
	
	public TitleScreen(GamePanel gp, boolean load) {
		this.gp = gp;
		
		if (load) {
			loadBackground();
			loadSaveFiles();
		}
	}

	private void loadBackground() {
		try {
			backgroundImage = ImageIO.read(getClass().getResourceAsStream("/gen/background1.png"));
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
	
	private void sortSaves(int sortType) {
		switch (sortType) {
		case SORT_NEW_OLD:
			saveFiles.sort((a, b) -> {
				long aTime = new File(SaveManager.getSavePath(a).toString()).lastModified();
				long bTime = new File(SaveManager.getSavePath(b).toString()).lastModified();
				return Long.compare(bTime, aTime);
			});
			break;
		case SORT_OLD_NEW:
			saveFiles.sort((a, b) -> {
				long aTime = new File(SaveManager.getSavePath(a).toString()).lastModified();
				long bTime = new File(SaveManager.getSavePath(b).toString()).lastModified();
				return Long.compare(aTime, bTime);
			});
			break;
		case SORT_A_Z:
			saveFiles.sort(String.CASE_INSENSITIVE_ORDER);
			break;
		case SORT_Z_A:
			saveFiles.sort(Comparator.reverseOrder());
			break;
		case SORT_REFRESH:
			loadSaveFiles();
			break;
		}
		
		if (selectedSaveIndex >= saveFiles.size()) {
			selectedSaveIndex = Math.max(0, saveFiles.size() - 1);
		}
		if (!saveFiles.isEmpty()) {
			loadPreviewPlayer(saveFiles.get(selectedSaveIndex));
		}
	}

	private void loadPreviewPlayer(String fileName) {
		previewPlayer = SaveManager.loadPlayer(fileName);
	}

	private void updateMainMenu() {
		int maxOption = MAIN_EXCEL_TOGGLE;
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			if (menuNum == MAIN_EXCEL_TOGGLE) { // for toggle going to left column
				menuNum = ((MAIN_DOC_END - MAIN_DOC_START) / 2) + MAIN_DOC_START;
			} else if (menuNum == ((MAIN_DOC_END - MAIN_DOC_START) / 2) + MAIN_DOC_START + 1) { // for top checkbox on the right column
				menuNum = MAIN_SAVE_SELECT;
			} else {
				menuNum = getNextValidOption(menuNum, maxOption, true);
			}
		}
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			if (menuNum == ((MAIN_DOC_END - MAIN_DOC_START) / 2) + MAIN_DOC_START) { // for bottom left column checkbox to the toggle
				menuNum = MAIN_EXCEL_TOGGLE;
			} else {
				menuNum = getNextValidOption(menuNum, maxOption, false);
			}
		}
		
		if (menuNum >= MAIN_DOC_START && menuNum <= MAIN_DOC_END) {
			int docIndex = menuNum - MAIN_DOC_START;
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (docOptions[docIndex]) {
					gp.playSFX(Sound.S_MENU_CAN);
				} else {
					gp.playSFX(Sound.S_MENU_CON);
				}
				docOptions[docIndex] = !docOptions[docIndex];
			}
			if (gp.keyH.leftPressed && docIndex >= docOptions.length / 2) {
				gp.keyH.leftPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				menuNum -= docOptions.length / 2;
			}
			if (gp.keyH.rightPressed && docIndex < docOptions.length / 2) {
				gp.keyH.rightPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				menuNum += docOptions.length / 2;
			}
		}
		
		if (menuNum == MAIN_EXCEL_TOGGLE) {
			if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
				gp.keyH.wPressed = false;
				gp.keyH.leftPressed = false;
				gp.keyH.rightPressed = false;
				if (generateExcel) {
					gp.playSFX(Sound.S_MENU_CAN);
				} else {
					gp.playSFX(Sound.S_MENU_CON);
				}
				generateExcel = !generateExcel;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (menuNum > MAIN_CONTINUE) {
				gp.playSFX(Sound.S_MENU_CON);
			}
			handleMainMenuSelection();
		}
	}

	private void handleMainMenuSelection() {
		switch (menuNum) {
		case MAIN_CONTINUE:
			gp.playSFX(Sound.S_MENU_START);
			if (saveFiles.isEmpty()) {
				showMessage("No save files found!");
			} else {
				Main.loadGame(saveFiles.get(selectedSaveIndex));
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
		menuState = NEW_GAME_MENU;
		newGameMenuNum = 0;
		nuzlockeMode = false;
		difficultyLevel = 1;
		banShedinja = false;
		banBatonPass = false;
		allowRevives = true;
		buyableRevives = false;
		levelCapBonus = 0;
		newGameMenuScroll = 0;
	}
	
	private void updateSavesMenu() {
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
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
			gp.playSFX(Sound.S_MENU_1);
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
			gp.playSFX(Sound.S_MENU_CON);
			menuState = MAIN_MENU;
			menuNum = MAIN_SAVE_SELECT;
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			menuState = MAIN_MENU;
			menuNum = MAIN_SAVE_SELECT;
		}
		
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			gp.playSFX(Sound.S_MENU_CON);
			menuState = SORT_SAVES_MENU;
			sortMenuNum = 0;
		}
	}
	
	private void updateManageMenu() {
		if (showDeleteConfirm || (textInputDialog != null && textInputDialog.isActive())) {
			return;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			manageMenuNum--;
			if (manageMenuNum < 0) manageMenuNum = MANAGE_CANCEL;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			manageMenuNum++;
			if (manageMenuNum > MANAGE_CANCEL) manageMenuNum = 0;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_CON);
			handleManageMenuSelection();
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			menuState = MAIN_MENU;
			manageMenuNum = 0;
		}
	}
	
	private void updateDeleteConfirm() {
		if (gp.keyH.leftPressed || gp.keyH.rightPressed) {
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			deleteConfirmNum = 1 - deleteConfirmNum;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (deleteConfirmNum == 1) {
				// Yes - Delete
				gp.playSFX(Sound.S_MENU_CON);
				try {
					SaveManager.deleteSave(fileToDelete);
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
					showMessage("Error deleting save file!");
				}
			} else {
				// No - Cancel
				gp.playSFX(Sound.S_MENU_CAN);
			}
			showDeleteConfirm = false;
			fileToDelete = null;
			menuState = MAIN_MENU;
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			showDeleteConfirm = false;
			fileToDelete = null;
		}
	}
	
	private void updateSortSavesMenu() {
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			sortMenuNum--;
			if (sortMenuNum < 0) sortMenuNum = SORT_BACK;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			sortMenuNum++;
			if (sortMenuNum > SORT_BACK) sortMenuNum = 0;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			if (sortMenuNum != SORT_BACK) {
				sortSaves(sortMenuNum);
			}
			menuState = SAVES_MENU;
			sortMenuNum = 0;
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			menuState = SAVES_MENU;
			sortMenuNum = 0;
		}
	}

	private void handleManageMenuSelection() {
		String selectedFile = saveFiles.get(selectedSaveIndex);

		switch (manageMenuNum) {
		case MANAGE_RENAME:
			handleRenameSave(selectedFile);
			break;
		case MANAGE_EXPORT:
			handleExportBattleHistory(selectedFile);
			break;
		case MANAGE_OPEN_LOCATION:
			SaveManager.showSaveInExplorer(selectedFile);
			break;
		case MANAGE_DELETE:
			handleDeleteSave(selectedFile);
			break;
		case MANAGE_CANCEL:
			menuState = MAIN_MENU;
			break;
		}
	}

	private void handleExportBattleHistory(String selectedFile) {
		HashMap<String, Pokemon[]> battleHistory = previewPlayer.getTrainerDatabase();
		if (battleHistory.isEmpty()) {
			showMessage("No history to export!");
			return;
		}
		
		try {
			Path battleDir = SaveManager.getDocsDirectory().getParent().resolve("battle");
			Files.createDirectories(battleDir);
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
			String fileName = "battle_history_" + timestamp + ".json";
			
			File battleFile = new File(battleDir.toFile(), fileName);
			FileWriter writer = new FileWriter(battleFile);
			writer.write(previewPlayer.exportBattleHistory().toString(2));
			SaveManager.showInExplorer(battleFile);
			showMessage("Battle data exported successfully!");
			writer.close();
		} catch (IOException e) {
			showMessage("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleRenameSave(String oldName) {
		String baseName = oldName.replace(".dat", "");
		
		textInputDialog = new TextInputDialog(gp, "Rename Save", baseName, "Enter new name", 30, textColor);
		
		textInputDialog.show(new TextInputDialog.InputCallback() {
			@Override
			public void onConfirm(String newName) {
				if (newName == null || newName.trim().isEmpty()) {
					showMessage("Name cannot be empty!");
					return;
				}
				
				newName = sanitizeFileName(newName.trim());
				
				if (newName.isEmpty()) {
					showMessage("Invalid file name. Please use valid characters.");
					return;
				}
				
				try {
					SaveManager.renameSave(oldName, newName + ".dat");
					loadSaveFiles();
					showMessage("Save file renamed successfully!");
				} catch (FileAlreadyExistsException e) {
					showMessage("File with the specified name already exists.");
				} catch (IOException e) {
					e.printStackTrace();
					showMessage("Error renaming file.");
				}
			}
			
			@Override
			public void onCancel() {
				// just return
			}
		});
	}
	
	private void handleDeleteSave(String fileName) {
		fileToDelete = fileName;
		showDeleteConfirm = true;
		deleteConfirmNum = 0;
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setFont(gp.marumonica);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		pulseCounter++;
		if (pulseCounter > 120) pulseCounter = 0;
		
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
		case NEW_GAME_MENU:
			drawNewGameMenu();
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
		case SORT_SAVES_MENU:
			drawSortSavesMenu();
			break;
		}
		
		if (showDeleteConfirm) {
			drawDeleteConfirmWindow();
		}
		
		if (textInputDialog != null && textInputDialog.isActive()) {
			textInputDialog.draw(g2);
		}
		
		if (textInputDialog != null && textInputDialog.isActive()) {
			textInputDialog.update();
		}
		
		if (showMessage) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			drawDialogueScreen(true);
			drawToolTips("OK", null, "OK", "OK");
		}
		
		if (!gp.keyH.shiftPressed) {
			drawControlsHint();
		}
		
		drawKeyStrokes();
	}

	private void showMessageControls() {
		if (gp.keyH.wPressed || gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			showMessage = false;
		}
	}

	private void drawMainMenu() {		
		g2.setColor(textColor);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
		
		int previewX = gp.screenWidth - gp.tileSize * 7;
		int previewY = gp.tileSize * 5;
		drawSavePreview(previewX, previewY, gp.tileSize * 6);
		
		int buttonX = gp.tileSize / 2;
		int buttonY = gp.tileSize;
		int buttonSpacing = gp.tileSize;
		
		boolean hasSaves = !saveFiles.isEmpty();
		
		drawMenuButton("Continue", buttonX, buttonY, menuNum == MAIN_CONTINUE, hasSaves);
		buttonY += buttonSpacing;
		drawMenuButton("New Game", buttonX, buttonY, menuNum == MAIN_NEW_GAME);
		buttonY += buttonSpacing;
		drawMenuButton("Manage Save", buttonX, buttonY, menuNum == MAIN_MANAGE, hasSaves);
		buttonY += buttonSpacing;
		drawMenuButton("Settings", buttonX, buttonY, menuNum == MAIN_SETTINGS);
		
		buttonY += buttonSpacing * 2;
		drawSaveSelector(g2, buttonX, buttonY, menuNum == MAIN_SAVE_SELECT, hasSaves);
		
		buttonY += buttonSpacing;
		drawDocumentationSection(g2, buttonX, buttonY);
		
		if (menuNum >= 0) {
			drawToolTips("Select", null, null, null);
		}
		
		updateMainMenu();
	}
	
	@Override
	public boolean isValidOption(int option) {
		switch (option) {
		case MAIN_MENU:
		case MAIN_MANAGE:
		case MAIN_SAVE_SELECT:
			return !saveFiles.isEmpty();
		default:
			return true;
		}
	}
	
	private void updateNewGameMenu() {
		if (textInputDialog == null || !textInputDialog.isActive()) {
			int maxOption = NG_START;
			
			if (gp.keyH.upPressed) {
				gp.keyH.upPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				newGameMenuNum--;
				if (newGameMenuNum < 0) newGameMenuNum = maxOption;
				
				// skip disabled options
				if (!nuzlockeMode && (newGameMenuNum >= NG_BAN_SHEDINJA && newGameMenuNum <= NG_LEVEL_CAP)) {
					newGameMenuNum = NG_DIFFICULTY;
				}
				// skip buyable revives if allow revives is off
				if (newGameMenuNum == NG_BUYABLE_REVIVES && !allowRevives) {
					newGameMenuNum = NG_ALLOW_REVIVES;
				}
				// update scroll position
				if (newGameMenuNum < newGameMenuScroll) {
					newGameMenuScroll = newGameMenuNum;
				}
				// ensure start button is always reachable
				if (newGameMenuNum == NG_LEVEL_CAP) {
					newGameMenuScroll = NG_LEVEL_CAP - MAX_VISIBLE_NG_OPTIONS;
				}
			}
			
			if (gp.keyH.downPressed) {
				gp.keyH.downPressed = false;
				gp.playSFX(Sound.S_MENU_1);
				newGameMenuNum++;
				if (newGameMenuNum > maxOption) {
					newGameMenuNum = 0;
					newGameMenuScroll = 0;
				}
				
				// skip disabled options
				if (!nuzlockeMode && (newGameMenuNum >= NG_BAN_SHEDINJA && newGameMenuNum <= NG_LEVEL_CAP)) {
					newGameMenuNum = NG_START;
				}
				// skip buyable revives if allow revives is off
				if (newGameMenuNum == NG_BUYABLE_REVIVES && !allowRevives) {
					newGameMenuNum = NG_LEVEL_CAP;
				}
				// update scroll position
				if (newGameMenuNum >= newGameMenuScroll + MAX_VISIBLE_NG_OPTIONS && newGameMenuNum <= NG_LEVEL_CAP) {
					newGameMenuScroll = newGameMenuNum - MAX_VISIBLE_NG_OPTIONS;
				}
				// ensure start button is always reachable
				if (newGameMenuNum != NG_START && isLastScrollableOption(newGameMenuNum)) {
					newGameMenuNum = NG_START;
				}
			}
			
			switch (newGameMenuNum) {
			case NG_SAVE_NAME:
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					gp.playSFX(Sound.S_MENU_1);
					textInputDialog = new TextInputDialog(gp, "Name Save", saveFileName, "Enter save file name", 30, textColor);
					
					textInputDialog.show(new TextInputDialog.InputCallback() {
						@Override
						public void onConfirm(String newName) {			
							newName = sanitizeFileName(newName.trim());
							saveFileName = newName;
						}
						
						@Override
						public void onCancel() {
							// just return
						}
					});
				}
				break;
			case NG_NUZLOCKE_TOGGLE:
				if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.leftPressed = false;
					gp.keyH.rightPressed = false;
					if (nuzlockeMode) {
						gp.playSFX(Sound.S_MENU_CAN);
					} else {
						gp.playSFX(Sound.S_MENU_CON);
					}
					nuzlockeMode = !nuzlockeMode;
				}
				break;
				
			case NG_DIFFICULTY:
				if (gp.keyH.leftPressed) {
					gp.keyH.leftPressed = false;
					gp.playSFX(Sound.S_MENU_1);
					difficultyLevel--;
					if (difficultyLevel < 0) difficultyLevel = 2;
				}
				if (gp.keyH.rightPressed) {
					gp.keyH.rightPressed = false;
					gp.playSFX(Sound.S_MENU_1);
					difficultyLevel++;
					if (difficultyLevel > 2) difficultyLevel = 0;
				}
				break;
				
			case NG_BAN_SHEDINJA:
				if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.leftPressed = false;
					gp.keyH.rightPressed = false;
					if (banShedinja) {
						gp.playSFX(Sound.S_MENU_CAN);
					} else {
						gp.playSFX(Sound.S_MENU_CON);
					}
					banShedinja = !banShedinja;
				}
				break;
			case NG_BAN_BATON_PASS:
				if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.leftPressed = false;
					gp.keyH.rightPressed = false;
					if (banBatonPass) {
						gp.playSFX(Sound.S_MENU_CAN);
					} else {
						gp.playSFX(Sound.S_MENU_CON);
					}
					banBatonPass = !banBatonPass;
				}
				
			case NG_ALLOW_REVIVES:
				if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.leftPressed = false;
					gp.keyH.rightPressed = false;
					if (allowRevives) {
						gp.playSFX(Sound.S_MENU_CAN);
						buyableRevives = false;
					} else {
						gp.playSFX(Sound.S_MENU_CON);
					}
					allowRevives = !allowRevives;
				}
				break;
				
			case NG_BUYABLE_REVIVES:
				if (gp.keyH.wPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
					gp.keyH.wPressed = false;
					gp.keyH.leftPressed = false;
					gp.keyH.rightPressed = false;
					if (buyableRevives) {
						gp.playSFX(Sound.S_MENU_CAN);
					} else {
						gp.playSFX(Sound.S_MENU_CON);
					}
					buyableRevives = !buyableRevives;
				}
				
			case NG_LEVEL_CAP:
				if (gp.keyH.leftPressed) {
					gp.keyH.leftPressed = false;
					gp.playSFX(Sound.S_MENU_1);
					levelCapBonus--;
					if (levelCapBonus < 0) levelCapBonus = 5;
				}
				if (gp.keyH.rightPressed) {
					gp.keyH.rightPressed = false;
					gp.playSFX(Sound.S_MENU_1);
					levelCapBonus++;
					if (levelCapBonus > 5) levelCapBonus = 0;
				}
				break;
				
			case NG_START:
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					gp.playSFX(Sound.S_MENU_START);
					handleNewGameStart();
				}
				break;
			}
			
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				gp.playSFX(Sound.S_MENU_CAN);
				menuState = MAIN_MENU;
				menuNum = MAIN_NEW_GAME;
			}
		}
	}
	
	private boolean isLastScrollableOption(int option) {
		for (int i = option; i < NG_START; i++) {
			if (isOptionVisible(i)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isOptionVisible(int option) {
		if (!nuzlockeMode && (option >= NG_BAN_SHEDINJA && option <= NG_LEVEL_CAP)) {
			return false;
		}
		if (!allowRevives && option == NG_BUYABLE_REVIVES) {
			return false;
		}
		return true;
	}
	
	private int getTotalOptions() {
		int result = 0;
		for (int i = 0; i < NG_START - 1; i++) {
			if (isOptionVisible(i)) result++;
		}
		return result;
	}

	private void handleNewGameStart() {
		String fileName = saveFileName.trim();
		if (fileName.isEmpty()) {
			fileName = "player";
		}
		
		fileName = sanitizeFileName(fileName);
		fileName = getUniqueSaveFileName(fileName);
		fileName += ".dat";
		
		Main.loadGame(fileName);
	}

	private String sanitizeFileName(String name) {
		// replace the following invalid Windows filename characters: < > : " / \ | ? *
		return name.replaceAll("[<>:\"/\\\\|?*]", "");
	}
	
	private String getUniqueSaveFileName(String baseName) {
		String testName = baseName;
		int counter = 1;
		
		while (saveFiles.contains(testName + ".dat")) {
			testName = baseName + " (" + counter + ")";
			counter++;
		}
		
		return testName;
	}
	
	private void drawNewGameMenu() {
		drawOverlay();
		
		int panelWidth = gp.tileSize * 13;
		int panelHeight = gp.tileSize * 11;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		String title = "New Game";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		int scrollableX = panelX + gp.tileSize / 4;
		int scrollableY = panelY + (int)(gp.tileSize * 1.25);
		int scrollableWidth = panelWidth - gp.tileSize / 2;
		int scrollableHeight = panelHeight - gp.tileSize * 3;
		
		drawPanelWithBorder(scrollableX, scrollableY, scrollableWidth, scrollableHeight, 50, new Color(100, 100, 100));
		
		int contentX = scrollableX + gp.tileSize / 4;
		int contentY = scrollableY - calculateScrollOffset();
		g2.setClip(scrollableX + 3, scrollableY + 3, scrollableWidth - 6, scrollableHeight - 6);
		
		contentY += gp.tileSize * 0.75;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
		boolean selected = newGameMenuNum == NG_SAVE_NAME;
		drawOutlinedText("Save File Name:", contentX, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		contentY += gp.tileSize / 3;
		
		int fieldWidth = panelWidth - gp.tileSize;
		int fieldHeight = gp.tileSize;
		if (selected) {
			drawPanelWithBorder(contentX, contentY, fieldWidth, fieldHeight, backgroundOpacity, textColor);
		} else {
			g2.setColor(new Color(30, 30, 30, 200));
			g2.fillRect(contentX, contentY, fieldWidth, fieldHeight);
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(100, 100, 100));
			g2.drawRect(contentX, contentY, fieldWidth, fieldHeight);
		}
		
		g2.setFont(g2.getFont().deriveFont(24F));
		int textX = contentX + gp.tileSize / 4;
		int textY = contentY + gp.tileSize * 3 / 4;
		
		String displayText = saveFileName.isEmpty() ? "(empty - will use 'player')" : saveFileName;
		Color textCol = saveFileName.isEmpty() ? new Color(150, 150, 150) : (selected ? textColor : new Color(200, 200, 200));
		drawOutlinedText(displayText, textX, textY, textCol, Color.BLACK);
		
		contentY += gp.tileSize;
		
		contentY += gp.tileSize / 2;
		g2.setColor(new Color(100, 100, 100));
		g2.drawLine(contentX, contentY, contentX + panelWidth - gp.tileSize, contentY);		
		contentY += (int)(gp.tileSize * 0.75);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
		selected = newGameMenuNum == NG_NUZLOCKE_TOGGLE;
		drawOutlinedText("Nuzlocke Mode:", contentX, contentY,selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		drawToggleSwitch(contentX + gp.tileSize * 4, contentY - gp.tileSize / 4, nuzlockeMode, selected);
		
		int tipX = contentX + (int)(gp.tileSize * 6.5);
		if (selected) {
			int tipY = contentY - gp.tileSize / 4;
			g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
			drawOutlinedText("Standard Hardcore Nuzlocke rules apply.", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			tipY += gp.tileSize / 4;
			drawOutlinedText("(1 Encounter per location w/ Dupes Clause,", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			tipY += gp.tileSize / 4;
			drawOutlinedText("Permadeath, Enforced Level Caps, QoL Items)", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
		}
		
		contentY += gp.tileSize * 3 / 4;
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
		drawOutlinedText("Game Settings:", contentX, contentY, new Color(180, 180, 180), Color.BLACK);
		contentY += gp.tileSize * 2 / 3;
		
		g2.setFont(g2.getFont().deriveFont(20F));
		selected = newGameMenuNum == NG_DIFFICULTY;
		drawOutlinedText("Difficulty:", contentX + gp.tileSize / 4, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		drawSelector(contentX + gp.tileSize * 3, contentY, Player.DIFFICULTIES[difficultyLevel], selected);
		
		if (selected) {
			int tipY = contentY;
			g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
			String diffDesc = "";
			switch (difficultyLevel) {
			case 0: // Normal
				tipY -= gp.tileSize / 8;
				drawOutlinedText("Advanced Move AI, Advanced Switch-In AI,", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				diffDesc = "Minimal Switch-Out AI";
				break;
			case 1: // Hard
				tipY -= gp.tileSize / 8;
				drawOutlinedText("Advanced Move AI, Advanced Switch-In AI,", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				diffDesc = "Advanced Switch-Out AI";
				break;
			case 2: // Extreme
				tipY -= gp.tileSize / 4;
				drawOutlinedText("Advanced Move AI, Advanced Switch-In AI,", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				drawOutlinedText("Advanced Switch-Out AI,", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				diffDesc = "Both player and AI pick lead at battle start";
				break;
			}
			drawOutlinedText(diffDesc, tipX, tipY, new Color(180, 180, 180), Color.BLACK);
		}
		
		contentY += gp.tileSize * 3 / 4;
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
		Color nuzColor = nuzlockeMode ? new Color(180, 180, 180) : new Color(100, 100, 100);
		drawOutlinedText("Nuzlocke Settings:", contentX, contentY, nuzColor, Color.BLACK);
		
		if (!nuzlockeMode) {
			g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
			contentY += gp.tileSize / 3;
			drawOutlinedText("(Enable Nuzlocke Mode to access these settings)", contentX + gp.tileSize / 4, contentY, new Color(120, 120, 120), Color.BLACK);
			contentY += gp.tileSize / 3;
		} else {
			contentY += gp.tileSize * 2 / 3;
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			selected = newGameMenuNum == NG_BAN_SHEDINJA;
			drawOutlinedText("Ban Shedinja:", contentX + gp.tileSize / 4, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
			
			drawToggleSwitch(contentX + gp.tileSize * 4, contentY - gp.tileSize / 4, banShedinja, selected);
			
			if (selected) {
				int tipY = contentY - gp.tileSize / 8;
				g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
				drawOutlinedText("Shedinja will not appear when evolving", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				drawOutlinedText("Nincada, and is not be catchable in the wild", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			}
			
			contentY += (int)(gp.tileSize * 0.75);
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			selected = newGameMenuNum == NG_BAN_BATON_PASS;
			drawOutlinedText("Ban Baton Pass:", contentX + gp.tileSize / 4, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
			
			drawToggleSwitch(contentX + gp.tileSize * 4, contentY - gp.tileSize / 4, banBatonPass, selected);
			
			if (selected) {
				int tipY = contentY - gp.tileSize / 8;
				g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
				drawOutlinedText("Baton Pass will be unavailable for all Pokemon.", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				drawOutlinedText("Prevents powerful setup sweep strategies.", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			}
			
			contentY += (int)(gp.tileSize * 0.75);
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			selected = newGameMenuNum == NG_ALLOW_REVIVES;
			drawOutlinedText("Allow Revives:", contentX + gp.tileSize / 4, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
			
			drawToggleSwitch(contentX + gp.tileSize * 4, contentY - gp.tileSize / 4, allowRevives, selected);
			
			if (selected) {
				int tipY = contentY;
				g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
				drawOutlinedText("Revives can be used on fainted Pokemon", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			}
			
			contentY += (int)(gp.tileSize * 0.75);
			
			if (allowRevives) {
				g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
				selected = newGameMenuNum == NG_BUYABLE_REVIVES;
				
				drawOutlinedText("\u2022 Buyable Revives:", contentX + gp.tileSize / 4, contentY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
				
				drawToggleSwitch(contentX + gp.tileSize * 4, contentY - gp.tileSize / 4, buyableRevives, selected);
				
				if (selected) {
					int tipY = contentY - gp.tileSize / 8;
					g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
					drawOutlinedText("Revives can be purchased from shops.", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
					tipY += gp.tileSize / 4;
					drawOutlinedText("Otherwise, only overworld revives are available.", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				}
				
				contentY += (int)(gp.tileSize * 0.75);
			}
			
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			selected = newGameMenuNum == NG_LEVEL_CAP;
			drawOutlinedText("Level Cap Bonus:", contentX + gp.tileSize / 4, contentY,selected ? textColor : new Color(200, 200, 200), Color.BLACK);
			
			String levelCapText = "+" + levelCapBonus;
			drawSelector(contentX + gp.tileSize * 4, contentY, levelCapText, selected);
			
			if (selected) {
				int tipY = contentY;
				tipX += gp.tileSize;
				tipY -= gp.tileSize / 8;
				g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 14F));
				drawOutlinedText("A set number to increase", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
				tipY += gp.tileSize / 4;
				drawOutlinedText("every level cap by", tipX, tipY, new Color(180, 180, 180), Color.BLACK);
			}
		}
		
		g2.setClip(null);
		
		int totalOptions = getTotalOptions();
		int effectiveTotal = totalOptions;
		
		if (effectiveTotal > MAX_VISIBLE_NG_OPTIONS) {
			int scrollbarX = panelX + panelWidth - gp.tileSize / 4;
			int scrollbarY = scrollableY;
			int scrollbarWidth = 8;
			int scrollbarHeight = scrollableHeight;
			
			g2.setColor(new Color(60, 60, 60));
			g2.fillRect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight);
			
			float thumbHeight = scrollbarHeight * ((float)MAX_VISIBLE_NG_OPTIONS / effectiveTotal);
			float maxScroll = effectiveTotal - MAX_VISIBLE_NG_OPTIONS;
			float thumbY = scrollbarY + (scrollbarHeight - thumbHeight) * (newGameMenuScroll / maxScroll);
			
			g2.setColor(textColor);
			g2.fillRect(scrollbarX, (int)thumbY, scrollbarWidth, (int)thumbHeight);
		}
		
		contentY = panelY + panelHeight - (int)(gp.tileSize * 1.25);
		int buttonWidth = gp.tileSize * 3;
		int buttonHeight = (int)(gp.tileSize * 0.75);
		int buttonX = (gp.screenWidth - buttonWidth) / 2;
		
		selected = newGameMenuNum == NG_START;
		if (selected) {
			drawPanelWithBorder(buttonX, contentY, buttonWidth, buttonHeight, backgroundOpacity + 50, textColor);
		} else {
			drawPanelWithBorder(buttonX, contentY, buttonWidth, buttonHeight, backgroundOpacity - 30, new Color(100, 100, 100));
		}
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String startText = "Start";
		int startX = getCenterAlignedTextX(startText, gp.screenWidth / 2);
		int startY = contentY + (int)(gp.tileSize * 0.55);
		drawOutlinedText(startText, startX, startY, selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(16F));
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		
		toolTips.add(new ToolTip(gp, "Navigate", "/", true, gp.config.upKey, gp.config.downKey));
		if (newGameMenuNum == NG_SAVE_NAME) {
			toolTips.add(new ToolTip(gp, "Edit", "", true, gp.config.wKey));
		} else if (newGameMenuNum == NG_START) {
			toolTips.add(new ToolTip(gp, "Start", "", true, gp.config.wKey));
		} else if (newGameMenuNum == NG_DIFFICULTY || newGameMenuNum == NG_LEVEL_CAP) {
			toolTips.add(new ToolTip(gp, "Adjust", "/", true, gp.config.leftKey, gp.config.rightKey));
		} else {
			toolTips.add(new ToolTip(gp, "Toggle", "", true, gp.config.wKey));
		}
		toolTips.add(new ToolTip(gp, "Back", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
		
		updateNewGameMenu();
	}
	
	private int calculateScrollOffset() {
		int offset = 0;
		
		for (int i = 0; i < newGameMenuScroll; i++) {
			if (i == NG_SAVE_NAME) offset += (int)(gp.tileSize * 1.4);
			else if (i == NG_NUZLOCKE_TOGGLE) offset += (int)(gp.tileSize * 0.5);
			else if (i == NG_DIFFICULTY) offset += gp.tileSize;
			else if (!nuzlockeMode && i >= NG_BAN_SHEDINJA && i <= NG_LEVEL_CAP) continue;
			else if (!allowRevives && i == NG_BUYABLE_REVIVES) continue;
			else offset += (int)(gp.tileSize * 0.75);
		}
		
		return offset;
	}

	private void drawSavesMenu() {		
		drawOverlay();
		
		int panelWidth = (int) (gp.tileSize * 13);
		int panelHeight = gp.tileSize * 9;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize * 3 / 2;
		
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
			
			File saveFile = new File(SaveManager.getSavePath(fileName).toString());
			long lastModified = saveFile.lastModified();
			SimpleDateFormat sdf = new SimpleDateFormat("M/d");
			String dateStr = sdf.format(new Date(lastModified));
			
			g2.setFont(g2.getFont().deriveFont(14F));
			int dateX = itemX + listWidth - getTextWidth(dateStr) - 10;
			Color dateColor = isSelected ? new Color(200, 200, 100) : new Color(150, 150, 150);
			drawOutlinedText(dateStr, dateX, textY, dateColor, Color.BLACK);
			g2.setFont(g2.getFont().deriveFont(18F));
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
			drawSavePreview(previewX, previewY, gp.tileSize * 6);
		}
		
		g2.setFont(g2.getFont().deriveFont(18F));
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Navigate", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Select", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Sort", "", true, gp.config.aKey));
		toolTips.add(new ToolTip(gp, "Cancel", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}

		updateSavesMenu();
	}
	
	private void drawSortSavesMenu() {
		drawOverlay();
		
		int panelWidth = gp.tileSize * 6;
		int panelHeight = gp.tileSize * 7;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		String title = "Sort Saves?";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		int dividerY = titleY + gp.tileSize / 4;
		g2.drawLine(panelX + gp.tileSize, dividerY, panelX + panelWidth - gp.tileSize, dividerY);
		
		String[] sortOptions = {"New → Old", "Old → New", "A → Z", "Z → A", "Refresh", "Back"};
		//int optionX = panelX + gp.tileSize / 2;
		int optionY = dividerY + (int)(gp.tileSize * 0.75);
		int optionSpacing = (int)(gp.tileSize * 0.75);
		
		g2.setFont(g2.getFont().deriveFont(24F));
		int centerX = panelX + panelWidth / 2;
		
		for (int i = 0; i < sortOptions.length; i++) {
			boolean isSelected = i == sortMenuNum;
			int currentY = optionY + i * optionSpacing;
			
			String text = sortOptions[i];
			int textX = getCenterAlignedTextX(text, centerX);
			
			Color textCol = isSelected ? textColor : new Color(200, 200, 200);
			drawOutlinedText(text, textX, currentY, textCol, Color.BLACK);
			
			if (isSelected) {
				g2.drawString(">", panelX + gp.tileSize, currentY - 2);
				g2.drawString("<", panelX + panelWidth - gp.tileSize, currentY - 2);
			}
		}
		
		g2.setFont(g2.getFont().deriveFont(16F));
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Navigate", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Select", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Cancel", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
		
		updateSortSavesMenu();
	}
	
	private void drawSavePreview(int x, int y, int width) {
		if (previewPlayer == null) return;
		
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
		
		contentY += iconSize * 2 + gp.tileSize;
		
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
		
		int badgeBoxWidth = (int)(gp.tileSize * 4.5);
		int badgeBoxHeight = (int)(gp.tileSize * 2.25);
		int badgeX = x + width - badgeBoxWidth;
		int badgeY = y + height - badgeBoxHeight;
		drawBadgesWindow(badgeX, badgeY, badgeBoxWidth, badgeBoxHeight, previewPlayer, gp, false);
	}
	
	private void drawManageMenu() {		
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
		
		String[] options = {"Rename", "Export Battle History", "Open Location", "Delete", "Cancel"};
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
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Navigate", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Select", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Cancel", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
		
		updateManageMenu();
	}
	
	private void drawDeleteConfirmWindow() {
		int panelWidth = gp.tileSize * 6;
		int panelHeight = gp.tileSize * 5;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 255, textColor);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String title = "Delete Save?";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, textColor, Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(18F));
		String warningText = "Are you sure you want to delete";
		int warningX = getCenterAlignedTextX(warningText, gp.screenWidth / 2);
		int warningY = panelY + (int)(gp.tileSize * 1.75);
		drawOutlinedText(warningText, warningX, warningY, new Color(200, 200, 200), Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
		int fileX = getCenterAlignedTextX(fileToDelete, gp.screenWidth / 2);
		int fileY = warningY + gp.tileSize / 2;
		drawOutlinedText(fileToDelete, fileX, fileY, new Color(255, 100, 100), Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(18F));
		String warningText2 = "This cannot be undone!";
		int warning2X = getCenterAlignedTextX(warningText2, gp.screenWidth / 2);
		int warning2Y = fileY + gp.tileSize / 2;
		drawOutlinedText(warningText2, warning2X, warning2Y, new Color(200, 200, 200), Color.BLACK);
		
		// Cancel Button
		boolean selected = deleteConfirmNum == 0;
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
		Color textCol = textColor;
		String cancelText = "Cancel";
		int buttonWidth = gp.tileSize * 2;
		int buttonHeight = (int)(gp.tileSize * 0.75);
		int buttonX = (gp.screenWidth - buttonWidth) / 2 - buttonWidth * 2/3;
		int buttonY = panelY + panelHeight - gp.tileSize;
		if (selected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity + 50, textCol);
		} else {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity - 30, new Color(100, 100, 100));
		}
		int cancelX = getCenterAlignedTextX(cancelText, buttonX + buttonWidth / 2);
		int cancelY = buttonY + (int)(gp.tileSize * 0.6);
		drawOutlinedText(cancelText, cancelX, cancelY, selected ? textCol : new Color(200, 200, 200), Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(16F));
		
		// Confirm Button
		selected = deleteConfirmNum == 1;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
		String confirmText = "Confirm";
		buttonX = (gp.screenWidth - buttonWidth) / 2 + buttonWidth * 2/3;
		if (selected) {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity + 50, textCol);
		} else {
			drawPanelWithBorder(buttonX, buttonY, buttonWidth, buttonHeight, backgroundOpacity - 30, new Color(100, 100, 100));
		}
		int confirmX = getCenterAlignedTextX(confirmText, buttonX + buttonWidth / 2);
		drawOutlinedText(confirmText, confirmX, cancelY,
			selected ? textCol : new Color(200, 200, 200), Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(16F));
		
		updateDeleteConfirm();
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
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
		
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
		drawOutlinedText(text, x, y, color, Color.BLACK);
	}
	
	private void drawMenuButton(String text, int x, int y, boolean selected) {
		drawMenuButton(text, x, y, selected, true);
	}
	
	private void drawMenuButton(String text, int x, int y, boolean selected, boolean enabled) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int backgroundX = x - gp.tileSize / 4;
		int backgroundY = y - gp.tileSize * 5 / 8;
		int backgroundWidth = gp.tileSize * 5;
		int backgroundHeight = (int) (gp.tileSize * 0.75);
		
		if (!enabled) {
			drawPanelWithBorder(backgroundX, backgroundY, backgroundWidth, backgroundHeight, backgroundOpacity - 30, new Color(60, 60, 60));
			Color textCol = new Color(100, 100, 100);
			drawOutlinedText(text, x, y, textCol, Color.BLACK);
		} else if (selected) {
			drawPanelWithBorder(backgroundX, backgroundY, backgroundWidth, backgroundHeight, backgroundOpacity + 30, textColor);
			
			int glowInset = 4;
			g2.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
			g2.fillRect(backgroundX + glowInset, backgroundY + glowInset, backgroundWidth - glowInset * 2, backgroundHeight - glowInset * 2);
			
			drawOutlinedText(text, x, y, textColor, Color.BLACK);
		} else {
			drawPanelWithBorder(backgroundX, backgroundY, backgroundWidth, backgroundHeight, backgroundOpacity - 50, new Color(100, 100, 100));
			Color textCol = new Color(200, 200, 200);
			drawOutlinedText(text, x, y, textCol, Color.BLACK);
		}
	}
	
	private void drawSaveSelector(Graphics2D g2, int x, int y, boolean selected, boolean enabled) {
		g2.setFont(g2.getFont().deriveFont(24F));
		
		String saveText = saveFiles.isEmpty() ? "No Saves Available" : saveFiles.get(selectedSaveIndex);
		
		int boxWidth = gp.tileSize * 5;
		int boxHeight = gp.tileSize / 2;
		int boxX = x - gp.tileSize / 4;
		int boxY = y - boxHeight;
		
		if (!enabled) {
			drawPanelWithBorder(boxX, boxY, boxWidth, boxHeight, backgroundOpacity - 30, new Color(60, 60, 60));
		} else if (selected) {
			drawPanelWithBorder(boxX, boxY, boxWidth, boxHeight, backgroundOpacity, textColor);
		} else {
			drawPanelWithBorder(boxX, boxY, boxWidth, boxHeight, backgroundOpacity - 50, new Color(100, 100, 100));
		}
		
		g2.setFont(g2.getFont().deriveFont(18F));
		drawOutlinedText("Save File:", x, y - boxHeight - 5, new Color(200, 200, 200), Color.BLACK);
		
		g2.setFont(g2.getFont().deriveFont(20F));
		Color textCol = !enabled ? new Color(100, 100, 100) : selected ? textColor : new Color(180, 180, 180);
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
		boolean inDocumentation = menuNum >= MAIN_DOC_START && menuNum <= MAIN_EXCEL_TOGGLE;
		drawOutlinedText("Generate Documentation:", x, y, inDocumentation ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		y += gp.tileSize / 2;
		g2.setFont(g2.getFont().deriveFont(18F));
		
		String[] docNames = {
			"Trainers", "Pokemon", "Encounters", "Moves",
			"Abilities", "Items", "Defensive Types", "Offensive Types"
		};
		boolean[] docCanBeExcel = {
			true, false, false, false,
			false, false, false, false
		};
		
		int col1X = x;
		int col2X = gp.tileSize * 4;
		int rowHeight = gp.tileSize * 2 / 3;
		
		for (int i = 0; i < docNames.length; i++) {
			int docX = (i < 4) ? col1X : col2X;
			int docY = y + ((i % 4) * rowHeight);
			int docCommandNum = MAIN_DOC_START + i;
			
			boolean isSelected = menuNum == docCommandNum;
			boolean isExcel = generateExcel && docCanBeExcel[i];
			Color checkedColor = isExcel ? new Color(0, 200, 100) : new Color(200, 200, 200);
			
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
				g2.setColor(checkedColor.darker());
				g2.fillRect(checkboxX + 2, checkboxY + 2, checkboxSize - 4, checkboxSize - 4);
				
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2));
				int checkX = checkboxX + checkboxSize / 4;
				int checkY = checkboxY + checkboxSize / 2;
				g2.drawLine(checkX, checkY, checkX + checkboxSize / 4, checkY + checkboxSize / 4);
				g2.drawLine(checkX + checkboxSize / 4, checkY + checkboxSize / 4, checkX + checkboxSize / 2, checkY - checkboxSize / 4);
			}
			
			Color labelColor = isSelected ? textColor : checkedColor;
			drawOutlinedText(docNames[i], docX + checkboxSize + 8, docY, labelColor, Color.BLACK);
		}
		
		int toggleX = (int) (gp.tileSize * 2.5);
		int toggleY = (int) (y + gp.tileSize * 2.5);
		drawExcelToggle(toggleX, toggleY);
	}
	
	private void drawExcelToggle(int x, int y) {
		boolean selected = menuNum == MAIN_EXCEL_TOGGLE;
		int toggleX = x;
		int toggleY = y - gp.tileSize / 6;
		
		g2.setFont(g2.getFont().deriveFont(16F));
		String label = "Excel:";
		drawOutlinedText(label, toggleX - gp.tileSize, toggleY + gp.tileSize / 6 + 5,
			selected ? textColor : new Color(200, 200, 200), Color.BLACK);
		
		drawToggleSwitch(toggleX, toggleY, generateExcel, selected);
	}

	private void drawSettingsMenu() {
		super.drawSettings();
		
		if (settingsState == 0) {
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				gp.playSFX(Sound.S_MENU_CAN);
				settingsState = 0;
				menuState = MAIN_MENU;
			}
		} else if (settingsState == 1) {
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				gp.playSFX(Sound.S_MENU_CAN);
				boolean changed = gp.config.setKeys();
				commandNum = 0;
				settingsState = 0;
				if (changed) showMessage("Keybinds successfully updated!");
			}
		} else if (settingsState == 2) {
			if (gp.keyH.sPressed || gp.keyH.dPressed) {
				gp.keyH.sPressed = false;
				gp.keyH.dPressed = false;
				gp.playSFX(Sound.S_MENU_CAN);
				commandNum = 4;
				settingsState = 0;
				difficultyNum = 0;
			}
		}
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
	
	@Override
	public void handleKeyInput(char keyChar) {
		if (nickname.length() < 30) {
			nickname.append(keyChar);
		}
	}

	public void handleBackspace() {
		if (nickname.length() > 0) {
			nickname.deleteCharAt(nickname.length() - 1);
		}
	}
}
