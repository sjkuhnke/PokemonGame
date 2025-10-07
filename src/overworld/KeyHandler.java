package overworld;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import pokemon.Bag.SortType;
import pokemon.Item;
import pokemon.Pokemon;
import pokemon.Task;
import util.SaveManager;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, sPressed, wPressed, dPressed, aPressed, tabPressed, shiftPressed, ctrlPressed, kUpPressed, kDownPressed, kLeftPressed, kRightPressed, kSPressed, kWPressed, kDPressed, kAPressed;
	public boolean hotkey1Pressed, hotkey2Pressed, hotkey3Pressed, hotkey4Pressed, hotkey5Pressed;
	
	GamePanel gp;
	Config config;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (config == null) config = gp.config;
		
		if (gp.ui.waitingForKey && gp.ui.settingsState == 1) {
			handleRebind(code);
			return;
		}
		
		handleKeyStrokes(code);
		
		if (gp.ui.showMessage) {
			messageState(code);
		} else if (gp.gameState == GamePanel.PLAY_STATE) {
			playState(code);
		} else if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			dialogueState(code);
		} else if (gp.gameState == GamePanel.MENU_STATE) {
			menuState(code);
		} else if (gp.gameState == GamePanel.SHOP_STATE) {
			shopState(code);
		} else if (gp.gameState == GamePanel.STAR_SHOP_STATE) {
			starShopState(code);
		} else if (gp.gameState == GamePanel.PRIZE_STATE) {
			prizeState(code);
		} else if (gp.gameState == GamePanel.NURSE_STATE) {
			nurseState(code);
		} else if (gp.gameState == GamePanel.BATTLE_STATE) {
			battleState(code);
		} else if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			simBattleState(code);
		} else if (gp.gameState == GamePanel.USE_ITEM_STATE) {
			useItemState(code);
		} else if (gp.gameState == GamePanel.USE_REPEL_STATE) {
			useRepelState(code);
		} else if (gp.gameState == GamePanel.RARE_CANDY_STATE) {
			useRareCandyState(code);
		} else if (gp.gameState == GamePanel.TASK_STATE) {
			taskState(code);
		} else if (gp.gameState == GamePanel.BOX_STATE) {
			boxState(code);
		} else if (gp.gameState == GamePanel.DEX_NAV_STATE) {
			dexNavState(code);
		} else if (gp.gameState == GamePanel.LETTER_STATE) {
			letterState(code);
		} else if (gp.gameState == GamePanel.STARTER_STATE) {
			starterMachineState(code);
		} else if (gp.gameState == GamePanel.COIN_STATE) {
			coinState(code);
		}
		
		if (!gp.ui.showMessage) {
			if (code == config.keys[config.upKey]) {
				upPressed = true;
			}
			if (code == config.keys[config.downKey]) {
				downPressed = true;
			}
			if (code == config.keys[config.leftKey]) {
				leftPressed = true;
			}
			if (code == config.keys[config.rightKey]) {
				rightPressed = true;
			}
			
			if (gp.battleUI.nicknaming == 1) {
				if (code == config.keys[config.backspaceKey]) {
					gp.battleUI.handleBackspace();
				} else {
					char c = e.getKeyChar();
					if (c != KeyEvent.CHAR_UNDEFINED) {
						gp.battleUI.handleKeyInput(c);
						downPressed = false;
					}
				}
			} else if (gp.ui.nicknaming == 1) {
				if (code == config.keys[config.backspaceKey]) {
					gp.ui.handleBackspace();
				} else {
					char c = e.getKeyChar();
					if (c != KeyEvent.CHAR_UNDEFINED) {
						gp.ui.handleKeyInput(c);
						downPressed = false;
					}
				}
			}
		}
		
		if (code == config.keys[config.speedupKey]) {
			tabPressed = true;
		}
		
		if (code == config.keys[config.tooltipsKey]) {
			shiftPressed = true;
		}
		
		if (code == config.keys[config.ctrlKey]) {
			ctrlPressed = true;
		}
		
		if (code == config.keys[config.screenshotKey]) {
            takeScreenshot();
            resetKeys(true);
        }
		
		if (code == gp.config.keys[gp.config.hotkey1]) hotkey1Pressed = true;
		if (code == gp.config.keys[gp.config.hotkey2]) hotkey2Pressed = true;
		if (code == gp.config.keys[gp.config.hotkey3]) hotkey3Pressed = true;
		if (code == gp.config.keys[gp.config.hotkey4]) hotkey4Pressed = true;
		if (code == gp.config.keys[gp.config.hotkey5]) hotkey5Pressed = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (config == null) config = gp.config;
		
		if (code == config.keys[config.upKey]) {
			upPressed = false;
			kUpPressed = false;
		}
		if (code == config.keys[config.downKey]) {
			downPressed = false;
			kDownPressed = false;
		}
		if (code == config.keys[config.leftKey]) {
			leftPressed = false;
			kLeftPressed = false;
		}
		if (code == config.keys[config.rightKey]) {
			rightPressed = false;
			kRightPressed = false;
		}
		if (code == config.keys[config.dKey]) {
			dPressed = false;
			kDPressed = false;
		}
		if (code == config.keys[config.sKey]) {
			sPressed = false;
			kSPressed = false;
		}
		if (code == config.keys[config.wKey]) {
			wPressed = false;
			kWPressed = false;
		}
		if (code == config.keys[config.aKey]) {
			aPressed = false;
			kAPressed = false;
		}
		if (code == config.keys[config.speedupKey]) {
			tabPressed = false;
		}
		if (code == config.keys[config.tooltipsKey]) {
			shiftPressed = false;
		}
		if (code == config.keys[config.ctrlKey]) {
			ctrlPressed = false;
		}
		
		if (code == gp.config.keys[gp.config.hotkey1]) hotkey1Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey2]) hotkey2Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey3]) hotkey3Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey4]) hotkey4Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey5]) hotkey5Pressed = false;
	}
	
	private void handleRebind(int newKey) {
		gp.config.setKeybind(gp.ui.rebindingControl, newKey);
		
		gp.ui.waitingForKey = false;
		gp.ui.rebindingControl = -1;
	}
	
	private void playState(int code) {
		if (code == config.keys[config.upKey]) {
			upPressed = true;
		}
		if (code == config.keys[config.downKey]) {
			downPressed = true;
		}
		if (code == config.keys[config.leftKey]) {
			leftPressed = true;
		}
		if (code == config.keys[config.rightKey]) {
			rightPressed = true;
		}
		if (code == config.keys[config.dKey]) {
			dPressed = true;
		}
		if (code == config.keys[config.sKey]) {
			sPressed = true;
		}
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
	}
	
	private void battleState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
		
		if (code == config.keys[config.dKey]) {
			dPressed = true;
		}
		if (gp.battleUI.subState == BattleUI.IDLE_STATE) {
			if (code == config.keys[config.upKey]) {
				if (gp.battleUI.commandNum > 1 || (gp.battleUI.shouldDrawCatchWindow() && !gp.battleUI.showFoeSummary)) {
					gp.battleUI.commandNum -= 2;
				}
			}
			if (code == config.keys[config.downKey]) {
				if (gp.battleUI.commandNum < 2 && !gp.battleUI.showFoeSummary) {
					gp.battleUI.commandNum += 2;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (gp.battleUI.commandNum >= 0) {
					if (!gp.battleUI.showFoeSummary) {
						if (gp.battleUI.commandNum % 2 == 1) {
							gp.battleUI.commandNum--;
						}
					} else {
						leftPressed = true;
					}
				} else {
					if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
						if (--gp.battleUI.ballIndex < 0) {
							gp.battleUI.ballIndex = gp.battleUI.balls.size() - 1;
						}
					}
				}
			}
			if (code == config.keys[config.rightKey]) {
				if (gp.battleUI.commandNum >= 0) {
					if (!gp.battleUI.showFoeSummary) {
						if (gp.battleUI.commandNum % 2 == 0) {
							gp.battleUI.commandNum++;
						}
					} else {
						rightPressed = true;
					}
				} else {
					if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
						if (++gp.battleUI.ballIndex >= gp.battleUI.balls.size()) {
							gp.battleUI.ballIndex = 0;
						}
					}
				}
			}
			if (code == config.keys[config.sKey]) {
				sPressed = true;
			}
		} else if (gp.battleUI.subState == BattleUI.MOVE_SELECTION_STATE) {
			if (code == config.keys[config.sKey]) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
				gp.battleUI.showMoveSummary = false;
			}
			if (code == config.keys[config.upKey]) {
				if (gp.battleUI.moveNum > 1) {
					gp.battleUI.moveNum -= 2;
				}
			}
			if (code == config.keys[config.downKey]) {
				if (gp.battleUI.moveNum < 2 && gp.battleUI.user.moveset[gp.battleUI.moveNum + 2] != null) {
					gp.battleUI.moveNum += 2;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (gp.battleUI.moveNum % 2 == 1) {
					gp.battleUI.moveNum--;
				}
			}
			if (code == config.keys[config.rightKey]) {
				if (gp.battleUI.moveNum % 2 == 0 && gp.battleUI.user.moveset[gp.battleUI.moveNum + 1] != null) {
					gp.battleUI.moveNum++;
				}
			}
		} else if (gp.battleUI.subState == BattleUI.PARTY_SELECTION_STATE) {
			if (code == config.keys[config.sKey] && gp.battleUI.cancellableParty) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.battleUI.subState == BattleUI.INFO_STATE) {
			if (code == config.keys[config.sKey]) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.battleUI.subState == BattleUI.SUMMARY_STATE) {
			if (code == config.keys[config.sKey]) {
				if (gp.battleUI.moveSummaryNum == -1) {
					gp.battleUI.subState = BattleUI.PARTY_SELECTION_STATE;
				} else {
					gp.battleUI.moveSummaryNum = -1;
					gp.battleUI.moveSwapNum = -1;
				}
				
			}
		}
	}
	
	private void simBattleState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
		
		if (code == config.keys[config.dKey]) {
			dPressed = true;
		}
		
		if (code == config.keys[config.sKey]) {
			sPressed = true;
		}
		
		if (gp.simBattleUI.subState == SimBattleUI.IDLE_STATE) {
			if (code == config.keys[config.upKey]) {
				if (gp.simBattleUI.commandNum > 1 && !gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays) {
					gp.simBattleUI.commandNum -= 2;
				}
			}
			if (code == config.keys[config.downKey]) {
				if (gp.simBattleUI.commandNum < 2 && !gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays) {
					gp.simBattleUI.commandNum += 2;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (!gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays && gp.simBattleUI.commandNum >= 0 && gp.simBattleUI.commandNum % 2 == 1) {
					gp.simBattleUI.commandNum--;
				} else {
					leftPressed = true;
				}
			}
			if (code == config.keys[config.rightKey]) {
				if (!gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays && gp.simBattleUI.commandNum >= 0 && gp.simBattleUI.commandNum % 2 == 0) {
					gp.simBattleUI.commandNum++;
				} else {
					rightPressed = true;
				}
			}
		} else if (gp.simBattleUI.subState == BattleUI.MOVE_SELECTION_STATE) {
			if (code == config.keys[config.sKey]) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
				gp.simBattleUI.showMoveSummary = false;
			}
			if (code == config.keys[config.upKey]) {
				if (gp.simBattleUI.moveNum > 1) {
					gp.simBattleUI.moveNum -= 2;
				}
			}
			if (code == config.keys[config.downKey]) {
				if (gp.simBattleUI.moveNum < 2 && gp.simBattleUI.user.moveset[gp.simBattleUI.moveNum + 2] != null) {
					gp.simBattleUI.moveNum += 2;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (gp.simBattleUI.moveNum % 2 == 1) {
					gp.simBattleUI.moveNum--;
				}
			}
			if (code == config.keys[config.rightKey]) {
				if (gp.simBattleUI.moveNum % 2 == 0 && gp.simBattleUI.user.moveset[gp.simBattleUI.moveNum + 1] != null) {
					gp.simBattleUI.moveNum++;
				}
			}
		} else if (gp.simBattleUI.subState == BattleUI.PARTY_SELECTION_STATE) {
			if (code == config.keys[config.sKey] && gp.simBattleUI.cancellableParty) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.simBattleUI.subState == BattleUI.INFO_STATE) {
			if (code == config.keys[config.sKey]) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
				sPressed = false;
			}
		} else if (gp.simBattleUI.subState == BattleUI.SUMMARY_STATE) {
			if (code == config.keys[config.sKey]) {
				if (gp.simBattleUI.moveSummaryNum == -1) {
					gp.simBattleUI.subState = BattleUI.PARTY_SELECTION_STATE;
				} else {
					gp.simBattleUI.moveSummaryNum = -1;
					gp.simBattleUI.moveSwapNum = -1;
				}
				
			}
		}
	}
	
	private void dialogueState(int code) {
		if (code == config.keys[config.wKey] || code == config.keys[config.sKey]) {
			gp.gameState = GamePanel.PLAY_STATE;
		}
	}
	
	private void messageState(int code) {
		if (gp.ui.messageSkippable() && (code == config.keys[config.wKey] || code == config.keys[config.sKey])) {
			gp.ui.showMessage = false;
			if (gp.gameState == GamePanel.USE_ITEM_STATE) {
				gp.ui.goBackInBag();
			} else if (gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE) {
				if (gp.ui.currentTask != null && gp.ui.currentTask.type != Task.SHAKE) {
					gp.ui.currentTask = null;
				}
			}
		}
		if ((gp.gameState == GamePanel.BOX_STATE || (gp.gameState == GamePanel.MENU_STATE && gp.ui.subState == 2))
				&& code == config.keys[config.dKey]) {
			gp.ui.showMessage = false;
		}
	}
	
	private void menuState(int code) {
		if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
			if (gp.ui.subState == 0) {
				gp.gameState = GamePanel.PLAY_STATE;
			}
			if (gp.ui.subState > 1 && gp.ui.subState < 8 && gp.ui.bagState == 0) { // Menus for handling the 7 top menu options
				if ((gp.ui.subState == 2 || gp.ui.subState == 3) && code == config.keys[config.dKey]) {
					dPressed = true;
				} else {
					if (gp.ui.subState != 7) { // settings
						gp.ui.settingsState = 0;
						gp.ui.subState = 0;
						gp.ui.partySelectedNum = -1;
						gp.ui.selectedBagNum = -1;
						gp.ui.partyNum = 0;
						gp.ui.commandNum = 0;
						gp.ui.partySelectedItem = -1;
					} else {
						if (code == config.keys[config.dKey]) dPressed = true;
						if (code == config.keys[config.sKey]) sPressed = true;
					}
				}
			} else if (gp.ui.subState == 8) { // Pokemon summary move info screen
				if (code == config.keys[config.sKey] && gp.ui.nicknaming < 0) {
					if (gp.ui.moveSummaryNum == -1) {
						gp.ui.subState = 2;
					} else {
						gp.ui.moveSwapNum = -1;
						gp.ui.moveSummaryNum = -1;
					}
				} else if (code == config.keys[config.dKey]) {
					dPressed = true;
				}
			} else if (gp.ui.subState == 1) { // Pokedex
				sPressed = true;
			} else if (gp.ui.bagState > 0) { // Bag option menu screen
				gp.ui.bagState = 0;
				gp.ui.commandNum = 0;
				gp.ui.sellAmt = 1;
			}
			
		}
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
		
		if (code == config.keys[config.upKey]) {
			if (gp.ui.subState == 0) { // options top
				gp.ui.menuNum--;
				if (gp.ui.menuNum < 0) {
					gp.ui.menuNum = gp.ui.maxMenuNum-1;
				}
			} else if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 1) { // item options
					if (gp.ui.commandNum > 0) {
						gp.ui.commandNum--;
					}
				} else if (gp.ui.bagState == 2) { // sell
					gp.ui.sellAmt++;
					if (gp.ui.sellAmt > gp.ui.currentItems.get(gp.ui.bagNum[gp.ui.currentPocket - 1]).getMaxSell()) gp.ui.sellAmt = 1;
				} else if (gp.ui.bagState == 4) { // sort
					if (gp.ui.commandNum > 0) {
						gp.ui.commandNum--;
					}
				} else if (gp.ui.bagState == 0 || gp.ui.bagState == 1 || gp.ui.bagState == 3) {
					int amt = ctrlPressed ? 5 : 1;
					gp.ui.bagNum[gp.ui.currentPocket - 1] -= amt;
					if (gp.ui.bagNum[gp.ui.currentPocket - 1] <= 0) {
						gp.ui.bagNum[gp.ui.currentPocket - 1] = 0;
					}
				}
			} else if (gp.ui.subState == 4) { // save
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			} else if (gp.ui.subState == 7 && gp.ui.settingsState == 0) { // settings
				gp.ui.commandNum--;
				if (gp.ui.commandNum < 0) {
					gp.ui.commandNum = gp.ui.maxSettingsNum;
				}
			}
		}
		if (code == config.keys[config.downKey]) {
			if (gp.ui.subState == 0) { // options top
				gp.ui.menuNum++;
				if (gp.ui.menuNum > gp.ui.maxMenuNum-1) {
					gp.ui.menuNum = 0;
				}
			} else if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 1) { // item options
					if (gp.ui.commandNum < 2) {
						gp.ui.commandNum++;
					}
				} else if (gp.ui.bagState == 2) { // sell
					gp.ui.sellAmt--;
					if (gp.ui.sellAmt < 1) gp.ui.sellAmt = gp.ui.currentItems.get(gp.ui.bagNum[gp.ui.currentPocket - 1]).getMaxSell();
				} else if (gp.ui.bagState == 4) { // sort
					if (gp.ui.commandNum < SortType.getMaxSortOptions(gp.ui.currentPocket) - 1) {
						gp.ui.commandNum++;
					}
				} else if (gp.ui.bagState == 0 || gp.ui.bagState == 1 || gp.ui.bagState == 3) {
					int amt = ctrlPressed ? 5 : 1;
					if (gp.ui.currentItems.size() > 0) {
						gp.ui.bagNum[gp.ui.currentPocket - 1] += amt;
						if (gp.ui.bagNum[gp.ui.currentPocket - 1] >= gp.ui.currentItems.size() - 1) {
							gp.ui.bagNum[gp.ui.currentPocket - 1] = gp.ui.currentItems.size() - 1;
						}
					}
				}
			} else if (gp.ui.subState == 4) { // save
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			} else if (gp.ui.subState == 7 && gp.ui.settingsState == 0) { // settings
				gp.ui.commandNum++;
				if (gp.ui.commandNum > gp.ui.maxSettingsNum) {
					gp.ui.commandNum = 0;
				}
			}
		}
		if (code == config.keys[config.leftKey]) {
			if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 2) { // sell
					int max = gp.ui.currentItems.get(gp.ui.bagNum[gp.ui.currentPocket - 1]).getMaxSell();
					gp.ui.sellAmt -= max > 10 ? 10 : 1;
					if (gp.ui.sellAmt < 1) gp.ui.sellAmt += max;
				} else if (gp.ui.bagState == 0) {
					gp.ui.currentPocket--;
					if (gp.ui.currentPocket < Item.MEDICINE) {
						gp.ui.currentPocket = Item.KEY_ITEM;
					}
					gp.ui.selectedBagNum = -1;
					gp.ui.currentItems = gp.player.p.getItems(gp.ui.currentPocket);
				}
			} else if (gp.ui.subState == 7 && gp.ui.settingsState == 0) { // settings
				if (gp.ui.commandNum == 0 && gp.music.volumeScale > 0) {
					gp.music.volumeScale--;
					gp.music.checkVolume();
				} else if (gp.ui.commandNum == 1 && gp.sfx.volumeScale > 0) {
					gp.sfx.volumeScale--;
					gp.sfx.checkVolume();
				}
			}
		}
		if (code == config.keys[config.rightKey]) {
			if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 2) { // sell
					int max = gp.ui.currentItems.get(gp.ui.bagNum[gp.ui.currentPocket - 1]).getMaxSell();
					gp.ui.sellAmt += max > 10 ? 10 : 1;
					if (gp.ui.sellAmt > max) gp.ui.sellAmt -= max;
				} else if (gp.ui.bagState == 0) {
					gp.ui.currentPocket++;
					if (gp.ui.currentPocket > Item.KEY_ITEM) {
						gp.ui.currentPocket = Item.MEDICINE;
					}
					gp.ui.selectedBagNum = -1;
					gp.ui.currentItems = gp.player.p.getItems(gp.ui.currentPocket);
				}
			} else if (gp.ui.subState == 7 && gp.ui.settingsState == 0) { // settings
				if (gp.ui.commandNum == 0 && gp.music.volumeScale < 5) {
					gp.music.volumeScale++;
					gp.music.checkVolume();
				} else if (gp.ui.commandNum == 1 && gp.sfx.volumeScale < 5) {
					gp.sfx.volumeScale++;
					gp.sfx.checkVolume();
				}
			}
		}
	}
	
	private void shopState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
			}
			if (code == config.keys[config.upKey] || code == config.keys[config.downKey]) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
		if (gp.ui.subState > 0) {
			if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
				if (gp.ui.premier > 0) {
					gp.setTaskState();
					Task.addTask(Task.TEXT, "Thanks for being a loyal customer! Here, this is on us!");
					String itemName = gp.ui.premier > 1 ? "s" : "";
					Task t = Task.addTask(Task.ITEM, "You got " + gp.ui.premier + " " + Item.PREMIER_BALL.toString() + itemName + "!", gp.ui.premier);
					t.item = Item.PREMIER_BALL;
					gp.ui.premier = 0;
				}
				gp.ui.subState = 0;
				gp.ui.currentDialogue = gp.ui.npc.dialogues[0];
			}
			if (code == config.keys[config.upKey]) {
				if (gp.ui.slotRow > 0) {
					gp.ui.slotRow--;
				}
			}
			if (code == config.keys[config.downKey]) {
				if (gp.ui.slotRow < UI.MAX_SHOP_ROW) {
					gp.ui.slotRow++;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (gp.ui.slotCol > 0) {
					gp.ui.slotCol--;
				}
			}
			if (code == config.keys[config.rightKey]) {
				if (gp.ui.slotCol < UI.MAX_SHOP_COL - 1) {
					gp.ui.slotCol++;
				}
			}
		}
	}
	
	private void starShopState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
			}
			if (code == config.keys[config.upKey]) {
				gp.ui.commandNum--;
				if (gp.ui.commandNum < 0) {
					gp.ui.commandNum = 2;
				}
			}
			if (code == config.keys[config.downKey]) {
				gp.ui.commandNum++;
				if (gp.ui.commandNum > 2) {
					gp.ui.commandNum = 0;
				}
			}
		}
		if (gp.ui.subState > 0) {
			if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
				gp.ui.subState = 0;
				gp.ui.currentDialogue = gp.ui.npc.dialogues[0];
			}
			if (code == config.keys[config.upKey]) {
				upPressed = true;
			}
			if (code == config.keys[config.downKey]) {
				downPressed = true;
			}
			if (code == config.keys[config.leftKey]) {
				leftPressed = true;
			}
			if (code == config.keys[config.rightKey]) {
				rightPressed = true;
			}
		}
	}
	

	private void prizeState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == config.keys[config.upKey] || code == config.keys[config.downKey]) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		} else {
			if (code == config.keys[config.sKey]) {
				gp.ui.subState = 0;
				gp.ui.currentDialogue = gp.ui.npc.dialogues[0];
			}
			if (code == config.keys[config.dKey]) {
				gp.ui.subState++;
				if (gp.ui.subState > 3) gp.ui.subState = 1;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == config.keys[config.aKey]) {
				gp.ui.subState--;
				if (gp.ui.subState < 1) gp.ui.subState = 3;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == config.keys[config.upKey]) {
				if (gp.ui.slotRow > 0) {
					gp.ui.slotRow--;
				}
			}
			if (code == config.keys[config.downKey]) {
				int row = gp.ui.subState == 3 ? 2 : UI.MAX_SHOP_ROW;
				if (gp.ui.slotRow < row) {
					gp.ui.slotRow++;
				}
			}
			if (code == config.keys[config.leftKey]) {
				if (gp.ui.slotCol > 0) {
					gp.ui.slotCol--;
				}
			}
			if (code == config.keys[config.rightKey]) {
				int col = gp.ui.subState == 3 ? 5 : UI.MAX_SHOP_COL - 1;
				if (gp.ui.slotCol < col) {
					gp.ui.slotCol++;
				}
			}
		}
	}
	
	private void boxState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (code == config.keys[config.sKey]) {
			if (gp.ui.nicknaming < 0) {
				if (gp.ui.release) {
					gp.ui.release = false;
				} else if (gp.ui.showBoxSummary) {
					if (gp.ui.moveSummaryNum >= 0) {
						gp.ui.moveSummaryNum = -1;
						gp.ui.moveSwapNum = -1;
					} else {
						gp.ui.showBoxSummary = false;
					}
				} else if (gp.ui.showBoxParty) {
					if (gp.ui.partySelectedNum >= 0) {
						gp.ui.partySelectedNum = -1;
					} else if (gp.ui.itemSwapP != null) {
						gp.ui.itemSwapP = null;
					} else {
						gp.ui.partySelectedItem = -1;
						gp.ui.partyNum = 0;
						gp.ui.showBoxParty = false;
					}
				} else if (gp.ui.boxSwapNum >= 0) {
					gp.ui.boxSwapNum = -1;
				} else if (gp.ui.itemSwapP != null) {
					gp.ui.itemSwapP = null;
				} else {
					gp.gameState = GamePanel.PLAY_STATE;
					gp.ui.npc.direction = "down";
					gp.ui.boxNum = 0;
					gp.ui.partySelectedNum = -1;
					gp.ui.boxSwapNum = -1;
				}
			}
		}
		
		if (code == config.keys[config.aKey]) {
			if (ctrlPressed) {
				ctrlPressed = false;
				Pokemon[] cBox = gp.ui.gauntlet ? gp.player.p.gauntletBox : gp.player.p.boxes[gp.player.p.currentBox];
				Item.useCalc(gp.player.p.getCurrent(), cBox, null, true);
			} else {
				aPressed = true;
			}
		}
		
		if (code == config.keys[config.dKey]) {
			dPressed = true;
		}
	}
	
	private void nurseState(int code) {
		if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.subState = 0;
			gp.ui.commandNum = 0;
		}
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == config.keys[config.upKey] || code == config.keys[config.downKey]) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
	}
	
	private void useItemState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
			if (gp.ui.showMoveOptions || gp.ui.showIVOptions || gp.ui.showStatusOptions) {
				gp.ui.moveOption = -1;
				gp.ui.showIVOptions = false;
				gp.ui.showMoveOptions = false;
				gp.ui.showStatusOptions = false;
			} else {
				gp.ui.goBackInBag();
			}
		}
	}
	
	private void useRepelState(int code) {
		if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.commandNum = 0;
		}
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
		if (code == config.keys[config.upKey] || code == config.keys[config.downKey]) {
			gp.ui.commandNum = 1 - gp.ui.commandNum;
		}
	}
	
	private void useRareCandyState(int code) {
		if (code == config.keys[config.dKey] || code == config.keys[config.sKey]) {
			if (gp.ui.currentTask == null) {
				gp.ui.goBackInBag();
			}
		}
		
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		
	}
	
	private void taskState(int code) {
		if (code == config.keys[config.dKey]) {
			if (gp.ui.currentTask == null && gp.ui.tasks.isEmpty() && gp.ui.checkTasks) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				dPressed = true;
			}
		}
		
		if (code == config.keys[config.sKey]) {
			if (gp.ui.currentTask == null && gp.ui.tasks.isEmpty() && gp.ui.checkTasks) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				sPressed = true;
			}
		}
		
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
		
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
	}
	
	private void dexNavState(int code) {
		if (code == config.keys[config.sKey]) {
			gp.ui.goBackInBag();
		}
	}
	
	private void letterState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		if (code == config.keys[config.sKey]) {
			gp.ui.goBackInBag();
		}
	}
	
	private void starterMachineState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		if (code == config.keys[config.sKey]) {
			if (gp.ui.starterConfirm) {
				gp.ui.starterConfirm = false;
				gp.ui.commandNum = 0;
			} else {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.starter = 0;
			}
		}
	}
	
	private void coinState(int code) {
		if (code == config.keys[config.wKey]) {
			wPressed = true;
		}
		if (code == config.keys[config.sKey]) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.sellAmt = 1;
		}
	}
	
	public void resetKeys() {
		resetKeys(true);
	}   
	
	// only reset shift if it's resetting keys while opening a new seperate UI (like calc)
	public void resetKeys(boolean resetShift) {
		upPressed = false;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		sPressed = false;
		wPressed = false;
		dPressed = false;
		aPressed = false;
		ctrlPressed = false;
		if (resetShift) shiftPressed = false;
		kSPressed = false;
		kWPressed = false;
		kDPressed = false;
		kAPressed = false;
		hotkey1Pressed = false;
		hotkey2Pressed = false;
		hotkey3Pressed = false;
		hotkey4Pressed = false;
		hotkey5Pressed = false;
	}
	
	 private void takeScreenshot() {
        // Create a BufferedImage from the JPanel's graphics
        BufferedImage screenshot = new BufferedImage(gp.getWidth(), gp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        gp.paint(screenshot.getGraphics());

        SaveManager.saveScreenshot(screenshot);

        // Copy to clipboard
        copyImageToClipboard(screenshot);
    }

    private void copyImageToClipboard(BufferedImage image) {
    	// Convert to a compatible format
        BufferedImage compatibleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = compatibleImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Copy to clipboard
        TransferableImage transferableImage = new TransferableImage(compatibleImage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(transferableImage, null);
    }

    private static class TransferableImage implements Transferable {
        private Image image;

        public TransferableImage(Image image) {
            this.image = image;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
    
    private void handleKeyStrokes(int code) {
    	if (code == config.keys[config.upKey]) {
			kUpPressed = true;
		}
		if (code == config.keys[config.downKey]) {
			kDownPressed = true;
		}
		if (code == config.keys[config.leftKey]) {
			kLeftPressed = true;
		}
		if (code == config.keys[config.rightKey]) {
			kRightPressed = true;
		}
		if (code == config.keys[config.dKey]) {
			kDPressed = true;
		}
		if (code == config.keys[config.sKey]) {
			kSPressed = true;
		}
		if (code == config.keys[config.wKey]) {
			kWPressed = true;
		}
		if (code == config.keys[config.aKey]) {
			kAPressed = true;
		}
    }
}