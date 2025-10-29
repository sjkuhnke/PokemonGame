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

import util.SaveManager;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed, sPressed, wPressed, dPressed, aPressed, tabPressed, shiftPressed, ctrlPressed, calcPressed, escPressed, fullPressed, kUpPressed, kDownPressed, kLeftPressed, kRightPressed, kSPressed, kWPressed, kDPressed, kAPressed;
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
		
		if (gp.ui != null && gp.ui.waitingForKey && gp.ui.settingsState == 1) {
			gp.ui.handleRebind(code);
		} else if (gp.titleScreen != null && gp.titleScreen.waitingForKey && gp.titleScreen.settingsState == 1) {
			gp.titleScreen.handleRebind(code);
			return;
		}
		
		handleKeyStrokes(code);
		
		if (code == config.keys[config.dKey]) {
			dPressed = true;
		}
		
		if (code == config.keys[config.sKey]) {
			sPressed = true;
		}
		
		if (code == config.keys[config.wKey]) {
			wPressed = true;
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
		
		if (code == config.keys[config.calcKey]) {
			calcPressed = true;
		}
		
		if (code == config.keys[config.escKey]) {
			escPressed = true;
		}
		
		if (code == config.keys[config.fullKey]) {
			fullPressed = true;
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
		if (code == config.keys[config.aKey]) {
			aPressed = true;
		}
		
		handleTextInput(e);
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
		if (code == config.keys[config.calcKey]) {
			calcPressed = false;
		}
		if (code == config.keys[config.escKey]) {
			escPressed = false;
		}
		if (code == config.keys[config.fullKey]) {
			fullPressed = false;
		}
		
		if (code == gp.config.keys[gp.config.hotkey1]) hotkey1Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey2]) hotkey2Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey3]) hotkey3Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey4]) hotkey4Pressed = false;
		if (code == gp.config.keys[gp.config.hotkey5]) hotkey5Pressed = false;
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
		resetMainKeys();
		ctrlPressed = false;
		if (resetShift) shiftPressed = false;
		calcPressed = false;
		hotkey1Pressed = false;
		hotkey2Pressed = false;
		hotkey3Pressed = false;
		hotkey4Pressed = false;
		hotkey5Pressed = false;
		gp.ui.showMessage = false;
	}
	
	public void resetMainKeys() {
		sPressed = false;
		wPressed = false;
		dPressed = false;
		aPressed = false;
	}
	
	private void handleTextInput(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (gp.battleUI != null && gp.battleUI.nicknaming == 1) {
			if (code == config.keys[config.backspaceKey]) {
				gp.battleUI.handleBackspace();
			} else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED) {
					gp.battleUI.handleKeyInput(c);
					downPressed = false;
				}
			}
		} else if (gp.ui != null && gp.ui.nicknaming == 1) {
			if (code == config.keys[config.backspaceKey]) {
				gp.ui.handleBackspace();
			} else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED) {
					gp.ui.handleKeyInput(c);
					downPressed = false;
				}
			}
		} else if (gp.titleScreen != null && gp.titleScreen.textInputDialog != null && gp.titleScreen.textInputDialog.isNaming()) {
			if (code == config.keys[config.backspaceKey]) {
				gp.titleScreen.textInputDialog.handleBackspace();
			} else {
				char c = e.getKeyChar();
				if (c != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(c)) {
					gp.titleScreen.textInputDialog.handleKeyInput(c);
					downPressed = false;
				}
			}
		} else if (gp.ui != null && gp.ui.cheatCodeDialog != null && gp.ui.cheatCodeDialog.isNaming()) {
            if (code == config.keys[config.backspaceKey]) {
                gp.ui.cheatCodeDialog.handleBackspace();
            } else {
                char c = e.getKeyChar();
                if (c != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(c)) {
                    gp.ui.cheatCodeDialog.handleKeyInput(c);
                    downPressed = false;
                }
            }
        }
	}
	
	 private void takeScreenshot() {
        // Create a BufferedImage from the JPanel's graphics
        BufferedImage screenshot = gp.screen;

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
        private final Image image;

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