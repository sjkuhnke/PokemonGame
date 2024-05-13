package overworld;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pokemon.Item;
import pokemon.Pokemon;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, sPressed, wPressed, dPressed, aPressed, tabPressed, shiftPressed, ctrlPressed;
	
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
		} else if (gp.gameState == GamePanel.NURSE_STATE) {
			nurseState(code);
		} else if (gp.gameState == GamePanel.BATTLE_STATE) {
			battleState(code);
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			upPressed = false;
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
			rightPressed = false;
		}
		if (code == KeyEvent.VK_D) {
			dPressed = false;
		}
		if (code == KeyEvent.VK_S) {
			sPressed = false;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = false;
		}
		if (code == KeyEvent.VK_A) {
			aPressed = false;
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
				if ((gp.battleUI.foe.trainerOwned() && gp.battleUI.commandNum > 1) || ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.commandNum >= 0)) {
					gp.battleUI.commandNum -= 2;
				}
			}
			if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
				if (gp.battleUI.commandNum < 2) {
					gp.battleUI.commandNum += 2;
				}
			}
			if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
				if (gp.battleUI.commandNum >= 0) {
					if (gp.battleUI.commandNum % 2 == 1) {
						gp.battleUI.commandNum--;
					}
				} else {
					if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
						int index = gp.battleUI.balls.indexOf(gp.battleUI.ball);
						if (--index < 0) {
							index = gp.battleUI.balls.size() - 1;
						}
						gp.battleUI.ball = gp.battleUI.balls.get(index);
					}
				}
			}
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
				if (gp.battleUI.commandNum >= 0) {
					if (gp.battleUI.commandNum % 2 == 0) {
						gp.battleUI.commandNum++;
					}
				} else {
					if ((!gp.battleUI.foe.trainerOwned() || gp.battleUI.staticID >= 0) && gp.battleUI.balls.size() > 1) {
						int index = gp.battleUI.balls.indexOf(gp.battleUI.ball);
						if (++index >= gp.battleUI.balls.size()) {
							index = 0;
						}
						gp.battleUI.ball = gp.battleUI.balls.get(index);
					}
				}
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
				gp.ui.currentTask = null;
			}
		}
	}
	
	private void menuState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			if (gp.ui.subState == 0) {
				gp.gameState = GamePanel.PLAY_STATE;
			}
			if (gp.ui.subState > 1 && gp.ui.subState < 7 && gp.ui.bagState == 0) { // Menus for handling the 7 top menu options
				gp.ui.subState = 0;
				gp.ui.partySelectedNum = -1;
				gp.ui.selectedBagNum = -1;
				gp.ui.partyNum = 0;
				gp.ui.commandNum = 0;
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
					if (gp.ui.sellAmt > gp.ui.currentItems.get(gp.ui.bagNum).getMaxSell()) gp.ui.sellAmt = 1;
				} else {
					if (gp.ui.bagNum > 0) {
						gp.ui.bagNum--;
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
					if (gp.ui.sellAmt < 1) gp.ui.sellAmt = gp.ui.currentItems.get(gp.ui.bagNum).getMaxSell();
				} else {
					if (gp.ui.bagNum < gp.ui.currentItems.size() - 1) {
						gp.ui.bagNum++;
					}
				}
			} else if (gp.ui.subState == 4) { // save
				gp.ui.commandNum = 1 - gp.ui.commandNum;
			}
		}
		if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_J) {
			if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 2) { // sell
					int max = gp.ui.currentItems.get(gp.ui.bagNum).getMaxSell();
					gp.ui.sellAmt -= max > 10 ? 10 : 1;
					if (gp.ui.sellAmt < 1) gp.ui.sellAmt += max;
				} else if (gp.ui.bagState == 0) {
					gp.ui.currentPocket--;
					if (gp.ui.currentPocket < Item.MEDICINE) {
						gp.ui.currentPocket = Item.BERRY;
					}
					gp.ui.bagNum = 0;
					gp.ui.selectedBagNum = -1;
					gp.ui.currentItems = gp.player.p.getItems(gp.ui.currentPocket);
				}
			}
		}
		if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_L) {
			if (gp.ui.subState == 3) { // bag
				if (gp.ui.bagState == 2) { // sell
					int max = gp.ui.currentItems.get(gp.ui.bagNum).getMaxSell();
					gp.ui.sellAmt += max > 10 ? 10 : 1;
					if (gp.ui.sellAmt > max) gp.ui.sellAmt -= max;
				} else if (gp.ui.bagState == 0) {
					gp.ui.currentPocket++;
					if (gp.ui.currentPocket > Item.BERRY) {
						gp.ui.currentPocket = Item.MEDICINE;
					}
					gp.ui.bagNum = 0;
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
					} else {
						gp.ui.partyNum = 0;
						gp.ui.showBoxParty = false;
					}
				} else if (gp.ui.boxSwapNum >= 0) {
					gp.ui.boxSwapNum = -1;
				} else {
					gp.gameState = GamePanel.PLAY_STATE;
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
				Item.useCalc(gp.player.p, cBox);
			} else {
				aPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_D) {
			if (!gp.ui.showBoxSummary && !gp.ui.release) {
				gp.ui.partyNum = 0;
				gp.ui.showBoxParty = !gp.ui.showBoxParty;
			} else {
				dPressed = true;
			}
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
			if (gp.ui.currentTask == null) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				dPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_S) {
			if (gp.ui.currentTask == null) {
				gp.gameState = GamePanel.PLAY_STATE;
			} else {
				sPressed = true;
			}
		}
		
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
	}
	
	public void resetKeys() {
		upPressed = false;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		sPressed = false;
		wPressed = false;
		dPressed = false;
		aPressed = false;
		ctrlPressed = false;
	}

}
