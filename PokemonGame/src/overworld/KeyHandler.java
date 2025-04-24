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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import pokemon.Item;
import pokemon.Pokemon;
import pokemon.Task;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, sPressed, wPressed, dPressed, aPressed, tabPressed, shiftPressed, ctrlPressed, kUpPressed, kDownPressed, kLeftPressed, kRightPressed, kSPressed, kWPressed, kDPressed, kAPressed;
	
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
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
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				upPressed = true;
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				downPressed = true;
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				leftPressed = true;
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				rightPressed = true;
			}
			
			if (gp.battleUI.nicknaming == 1) {
				if (code == KeyEvent.VK_BACK_SPACE) {
					gp.battleUI.handleBackspace();
				} else {
					char c = e.getKeyChar();
					if (c != KeyEvent.CHAR_UNDEFINED) {
						gp.battleUI.handleKeyInput(c);
						downPressed = false;
					}
				}
			} else if (gp.ui.nicknaming == 1) {
				if (code == KeyEvent.VK_BACK_SPACE) {
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
		
		if (code == KeyEvent.VK_TAB) {
			tabPressed = true;
		}
		
		if (code == KeyEvent.VK_SHIFT) {
			shiftPressed = true;
		}
		
		if (code == KeyEvent.VK_CONTROL) {
			ctrlPressed = true;
		}
		
		if (code == KeyEvent.VK_ENTER) {
            takeScreenshot();
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			upPressed = false;
			kUpPressed = false;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			downPressed = false;
			kDownPressed = false;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
			leftPressed = false;
			kLeftPressed = false;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
			rightPressed = false;
			kRightPressed = false;
		}
		if (code == KeyEvent.VK_D) {
			dPressed = false;
			kDPressed = false;
		}
		if (code == KeyEvent.VK_S) {
			sPressed = false;
			kSPressed = false;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = false;
			kWPressed = false;
		}
		if (code == KeyEvent.VK_A) {
			aPressed = false;
			kAPressed = false;
		}
		if (code == KeyEvent.VK_TAB) {
			tabPressed = false;
		}
		if (code == KeyEvent.VK_SHIFT) {
			shiftPressed = false;
		}
		if (code == KeyEvent.VK_CONTROL) {
			ctrlPressed = false;
		}
	}
	
	private void playState(int code) {
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			upPressed = true;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			dPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			sPressed = true;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			aPressed = true;
		}
	}
	
	private void battleState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (code == KeyEvent.VK_A) {
			aPressed = true;
		}
		
		if (code == KeyEvent.VK_D) {
			dPressed = true;
		}
		if (gp.battleUI.subState == BattleUI.IDLE_STATE) {
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if ((gp.battleUI.foe.trainerOwned() && gp.battleUI.commandNum > 1) || ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.commandNum >= 0) && !gp.battleUI.showFoeSummary) {
					gp.battleUI.commandNum -= 2;
					if (gp.battleUI.commandNum < 0 && gp.battleUI.aura) {
						gp.battleUI.commandNum += 2;
					}
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.battleUI.commandNum < 2 && !gp.battleUI.showFoeSummary) {
					gp.battleUI.commandNum += 2;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (!gp.battleUI.showFoeSummary) {
					if (gp.battleUI.commandNum >= 0) {
						if (gp.battleUI.commandNum % 2 == 1) {
							gp.battleUI.commandNum--;
						}
					} else {
						if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
							if (--gp.battleUI.ballIndex < 0) {
								gp.battleUI.ballIndex = gp.battleUI.balls.size() - 1;
							}
						}
					}
				} else {
					leftPressed = true;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (!gp.battleUI.showFoeSummary) {
					if (gp.battleUI.commandNum >= 0) {
						if (gp.battleUI.commandNum % 2 == 0) {
							gp.battleUI.commandNum++;
						}
					} else {
						if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
							if (++gp.battleUI.ballIndex >= gp.battleUI.balls.size()) {
								gp.battleUI.ballIndex = 0;
							}
						}
					}
				} else {
					rightPressed = true;
				}
			}
			if (code == KeyEvent.VK_S) {
				sPressed = true;
			}
		} else if (gp.battleUI.subState == BattleUI.MOVE_SELECTION_STATE) {
			if (code == KeyEvent.VK_S) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
				gp.battleUI.showMoveSummary = false;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if (gp.battleUI.moveNum > 1) {
					gp.battleUI.moveNum -= 2;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.battleUI.moveNum < 2 && gp.battleUI.user.moveset[gp.battleUI.moveNum + 2] != null) {
					gp.battleUI.moveNum += 2;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (gp.battleUI.moveNum % 2 == 1) {
					gp.battleUI.moveNum--;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (gp.battleUI.moveNum % 2 == 0 && gp.battleUI.user.moveset[gp.battleUI.moveNum + 1] != null) {
					gp.battleUI.moveNum++;
				}
			}
		} else if (gp.battleUI.subState == BattleUI.PARTY_SELECTION_STATE) {
			if (code == KeyEvent.VK_S && gp.battleUI.cancellableParty) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.battleUI.subState == BattleUI.INFO_STATE) {
			if (code == KeyEvent.VK_S) {
				gp.battleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.battleUI.subState == BattleUI.SUMMARY_STATE) {
			if (code == KeyEvent.VK_S) {
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
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (code == KeyEvent.VK_A) {
			aPressed = true;
		}
		
		if (code == KeyEvent.VK_D) {
			dPressed = true;
		}
		
		if (code == KeyEvent.VK_S) {
			sPressed = true;
		}
		
		if (gp.simBattleUI.subState == SimBattleUI.IDLE_STATE) {
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if (gp.simBattleUI.commandNum > 1 && !gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays) {
					gp.simBattleUI.commandNum -= 2;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.simBattleUI.commandNum < 2 && !gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays) {
					gp.simBattleUI.commandNum += 2;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (!gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays && gp.simBattleUI.commandNum >= 0 && gp.simBattleUI.commandNum % 2 == 1) {
					gp.simBattleUI.commandNum--;
				} else {
					leftPressed = true;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (!gp.simBattleUI.showFoeSummary && !gp.simBattleUI.showParlays && gp.simBattleUI.commandNum >= 0 && gp.simBattleUI.commandNum % 2 == 0) {
					gp.simBattleUI.commandNum++;
				} else {
					rightPressed = true;
				}
			}
		} else if (gp.simBattleUI.subState == BattleUI.MOVE_SELECTION_STATE) {
			if (code == KeyEvent.VK_S) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
				gp.simBattleUI.showMoveSummary = false;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if (gp.simBattleUI.moveNum > 1) {
					gp.simBattleUI.moveNum -= 2;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.simBattleUI.moveNum < 2 && gp.simBattleUI.user.moveset[gp.simBattleUI.moveNum + 2] != null) {
					gp.simBattleUI.moveNum += 2;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (gp.simBattleUI.moveNum % 2 == 1) {
					gp.simBattleUI.moveNum--;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (gp.simBattleUI.moveNum % 2 == 0 && gp.simBattleUI.user.moveset[gp.simBattleUI.moveNum + 1] != null) {
					gp.simBattleUI.moveNum++;
				}
			}
		} else if (gp.simBattleUI.subState == BattleUI.PARTY_SELECTION_STATE) {
			if (code == KeyEvent.VK_S && gp.simBattleUI.cancellableParty) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
			}
		} else if (gp.simBattleUI.subState == BattleUI.INFO_STATE) {
			if (code == KeyEvent.VK_S) {
				gp.simBattleUI.subState = BattleUI.IDLE_STATE;
				sPressed = false;
			}
		} else if (gp.simBattleUI.subState == BattleUI.SUMMARY_STATE) {
			if (code == KeyEvent.VK_S) {
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
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.PLAY_STATE;
		}
	}
	
	private void messageState(int code) {
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_S) {
			gp.ui.showMessage = false;
			if (gp.gameState == GamePanel.USE_ITEM_STATE) {
				gp.gameState = GamePanel.MENU_STATE;
				gp.ui.subState = 3;
				gp.ui.bagState = 0;
				gp.ui.commandNum = 0;
			} else if (gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE) {
				if (gp.ui.currentTask != null && gp.ui.currentTask.type != Task.SHAKE) {
					gp.ui.currentTask = null;
				}
			}
		}
	}
	
	private void menuState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			if (gp.ui.subState == 0) {
				gp.gameState = GamePanel.PLAY_STATE;
			}
			if (gp.ui.subState > 1 && gp.ui.subState < 7 && gp.ui.bagState == 0) { // Menus for handling the 7 top menu options
				if ((gp.ui.subState == 2 || gp.ui.subState == 3) && code == KeyEvent.VK_D) {
					dPressed = true;
				} else {
					gp.ui.subState = 0;
					gp.ui.partySelectedNum = -1;
					gp.ui.selectedBagNum = -1;
					gp.ui.partyNum = 0;
					gp.ui.commandNum = 0;
					gp.ui.partySelectedItem = -1;
				}
			} else if (gp.ui.subState == 7) { // Pokemon summary move info screen
				if (code == KeyEvent.VK_S && gp.ui.nicknaming < 0) {
					if (gp.ui.moveSummaryNum == -1) {
						gp.ui.subState = 2;
					} else {
						gp.ui.moveSwapNum = -1;
						gp.ui.moveSummaryNum = -1;
					}
				} else if (code == KeyEvent.VK_D) {
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
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			aPressed = true;
		}
		
		int maxCommandNum = 0;
		switch(gp.ui.subState) {
		case 0:
			maxCommandNum = 6;
		}
		
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			if (gp.ui.subState == 0) { // options top
				gp.ui.menuNum--;
				if (gp.ui.menuNum < 0) {
					gp.ui.menuNum = maxCommandNum;
				}
			} else if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 1) { // item options
					if (gp.ui.commandNum > 0) {
						gp.ui.commandNum--;
					}
				} else if (gp.ui.bagState == 2) { // sell
					gp.ui.sellAmt++;
					if (gp.ui.sellAmt > gp.ui.currentItems.get(gp.ui.bagNum[gp.ui.currentPocket - 1]).getMaxSell()) gp.ui.sellAmt = 1;
				} else {
					int amt;
					if (ctrlPressed) {
						amt = 5;
					} else {
						amt = 1;
					}
					gp.ui.bagNum[gp.ui.currentPocket - 1] -= amt;
					if (gp.ui.bagNum[gp.ui.currentPocket - 1] <= 0) {
						gp.ui.bagNum[gp.ui.currentPocket - 1] = 0;
					}
				}
			} else if (gp.ui.subState == 4) { // save
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			if (gp.ui.subState == 0) { // options top
				gp.ui.menuNum++;
				if (gp.ui.menuNum > maxCommandNum) {
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
				} else {
					int amt;
					if (ctrlPressed) {
						amt = 5;
					} else {
						amt = 1;
					}
					if (gp.ui.currentItems.size() > 0) {
						gp.ui.bagNum[gp.ui.currentPocket - 1] += amt;
						if (gp.ui.bagNum[gp.ui.currentPocket - 1] >= gp.ui.currentItems.size() - 1) {
							gp.ui.bagNum[gp.ui.currentPocket - 1] = gp.ui.currentItems.size() - 1;
						}
					}
				}
			} else if (gp.ui.subState == 4) { // save
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
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
			}
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
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
			}
		}
	}
	
	private void shopState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
		if (gp.ui.subState > 0) {
			if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
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
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if (gp.ui.slotRow > 0) {
					gp.ui.slotRow--;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.ui.slotRow < UI.MAX_SHOP_ROW) {
					gp.ui.slotRow++;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (gp.ui.slotCol > 0) {
					gp.ui.slotCol--;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (gp.ui.slotCol < UI.MAX_SHOP_COL - 1) {
					gp.ui.slotCol++;
				}
			}
		}
	}
	
	private void starShopState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				gp.ui.commandNum--;
				if (gp.ui.commandNum < 0) {
					gp.ui.commandNum = 2;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				gp.ui.commandNum++;
				if (gp.ui.commandNum > 2) {
					gp.ui.commandNum = 0;
				}
			}
		}
		if (gp.ui.subState > 0) {
			if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
				gp.ui.subState = 0;
				gp.ui.currentDialogue = gp.ui.npc.dialogues[0];
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				upPressed = true;
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				downPressed = true;
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				leftPressed = true;
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				rightPressed = true;
			}
		}
	}
	

	private void prizeState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
				gp.gameState = GamePanel.PLAY_STATE;
				gp.ui.subState = 0;
				gp.ui.commandNum = 0;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		} else {
			if (code == KeyEvent.VK_S) {
				gp.ui.subState = 0;
				gp.ui.currentDialogue = gp.ui.npc.dialogues[0];
			}
			if (code == KeyEvent.VK_D) {
				gp.ui.subState++;
				if (gp.ui.subState > 3) gp.ui.subState = 1;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == KeyEvent.VK_A) {
				gp.ui.subState--;
				if (gp.ui.subState < 1) gp.ui.subState = 3;
				gp.ui.slotRow = 0;
				gp.ui.slotCol = 0;
			}
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
				if (gp.ui.slotRow > 0) {
					gp.ui.slotRow--;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				int row = gp.ui.subState == 3 ? 2 : UI.MAX_SHOP_ROW;
				if (gp.ui.slotRow < row) {
					gp.ui.slotRow++;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (gp.ui.slotCol > 0) {
					gp.ui.slotCol--;
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				int col = gp.ui.subState == 3 ? 5 : UI.MAX_SHOP_COL - 1;
				if (gp.ui.slotCol < col) {
					gp.ui.slotCol++;
				}
			}
		}
	}
	
	private void boxState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (code == KeyEvent.VK_S) {
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
		
		if (code == KeyEvent.VK_A) {
			if (ctrlPressed) {
				ctrlPressed = false;
				Pokemon[] cBox = gp.ui.gauntlet ? gp.player.p.gauntletBox : gp.player.p.boxes[gp.player.p.currentBox];
				Item.useCalc(gp.player.p.getCurrent(), cBox, null, true);
			} else {
				aPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_D) {
			dPressed = true;
		}
	}
	
	private void nurseState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.subState = 0;
			gp.ui.commandNum = 0;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
	}
	
	private void useItemState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			if (gp.ui.showMoveOptions || gp.ui.showIVOptions) {
				gp.ui.moveOption = -1;
				gp.ui.showIVOptions = false;
				gp.ui.showMoveOptions = false;
			} else {
				gp.gameState = GamePanel.MENU_STATE;
				gp.ui.subState = 3;
				gp.ui.bagState = 0;
				gp.ui.commandNum = 0;
			}
		}
	}
	
	private void useRepelState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.commandNum = 0;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I || code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			gp.ui.commandNum = 1 - gp.ui.commandNum;
		}
	}
	
	private void useRareCandyState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			if (gp.ui.currentTask == null) {
				gp.gameState = GamePanel.MENU_STATE;
				gp.ui.currentItems = gp.player.p.getItems(gp.ui.currentPocket);
				gp.ui.bagState = 0;
			}
		}
		
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
	}
	
	private void taskState(int code) {
		if (code == KeyEvent.VK_D) {
			if (gp.ui.currentTask == null && gp.ui.tasks.isEmpty() && gp.ui.checkTasks) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				dPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_S) {
			if (gp.ui.currentTask == null && gp.ui.tasks.isEmpty() && gp.ui.checkTasks) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				sPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_A) {
			aPressed = true;
		}
		
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
	}
	
	private void dexNavState(int code) {
		if (code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.MENU_STATE;
		}
	}
	
	private void letterState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.MENU_STATE;
			gp.ui.pageNum = 0;
		}
	}
	
	private void starterMachineState(int code) {
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_S) {
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
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_S) {
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
	}
	
	 private void takeScreenshot() {
        try {
            // Create a BufferedImage from the JPanel's graphics
            BufferedImage screenshot = new BufferedImage(gp.getWidth(), gp.getHeight(), BufferedImage.TYPE_INT_ARGB);
            gp.paint(screenshot.getGraphics());

            // Save to a file
            String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
            Path screenshotsDirectory = Paths.get("./screenshots/");
            if (!Files.exists(screenshotsDirectory)) {
                Files.createDirectories(screenshotsDirectory);
            }
            File screenshotFile = new File(screenshotsDirectory.toFile(), fileName);
            ImageIO.write(screenshot, "png", screenshotFile);

            // Copy to clipboard
            copyImageToClipboard(screenshot);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
    
    private void handleKeyStrokes(int code) {
    	if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			kUpPressed = true;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			kDownPressed = true;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
			kLeftPressed = true;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
			kRightPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			kDPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			kSPressed = true;
		}
		if (code == KeyEvent.VK_W) {
			kWPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			kAPressed = true;
		}
    }
}