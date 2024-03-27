package Overworld;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, sPressed, wPressed, dPressed, aPressed;
	public boolean pause;
	
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (pause) return;
		int code = e.getKeyCode();
		
		if (gp.gameState == GamePanel.PLAY_STATE) {
			playState(code);
		} else if (gp.gameState == GamePanel.DIALOGUE_STATE) {
			dialogueState(code);
		} else if (gp.gameState == GamePanel.MENU_STATE) {
			menuState(code);
		} else if (gp.gameState == GamePanel.SHOP_STATE) {
			shopState(code);
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
	}
	
	public void pause() {
		pause = true;
		downPressed = upPressed = leftPressed = rightPressed = sPressed = wPressed = dPressed = aPressed = false;
	}
	public void resume() {
		pause = false;
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
	
	private void dialogueState(int code) {
		if (code == KeyEvent.VK_W) {
			gp.gameState = GamePanel.PLAY_STATE;
		}
	}
	
	private void menuState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			if (gp.ui.subState == 0) {
				gp.gameState = GamePanel.PLAY_STATE;
			}
			gp.ui.subState = 0;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		int maxCommandNum = 0;
		switch(gp.ui.subState) {
		case 0:
			maxCommandNum = 6;
		}
		
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_I) {
			gp.ui.menuNum--;
			if (gp.ui.menuNum < 0) {
				gp.ui.menuNum = maxCommandNum;
			}
		}
		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_K) {
			gp.ui.menuNum++;
			if (gp.ui.menuNum > maxCommandNum) {
				gp.ui.menuNum = 0;
			}
		}
	}
	
	private void shopState(int code) {
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_S) {
			gp.gameState = GamePanel.PLAY_STATE;
			gp.ui.subState = 0;
			gp.ui.commandNum = 0;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		
		if (gp.ui.subState == 0) {
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
	}

}
