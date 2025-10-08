package overworld;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Config {
	GamePanel gp;
	
	// Config file path
	private static final String CONFIG_FILE = "config.txt";
	
	// Keybind indices
	public int upKey = 0;
	public int downKey = 1;
	public int leftKey = 2;
	public int rightKey = 3;
	public int wKey = 4;
	public int sKey = 5;
	public int dKey = 6;
	public int aKey = 7;
	public int calcKey = 8;
	public int hotkey1 = 9;
	public int hotkey2 = 10;
	public int hotkey3 = 11;
	public int hotkey4 = 12;
	public int hotkey5 = 13;
	public int screenshotKey = 14;
	public int speedupKey = 15;
	public int ctrlKey = 16;
	public int tooltipsKey = 17;
	public int backspaceKey = 18;
	
	public final String[] keyNames;
	public int[] keys;
	public final int[] defaultKeys;
	public int[] pendingKeys;
	
	public Config(GamePanel gp) {
		this.gp = gp;
		keyNames = new String[] {"Up", "Down", "Left", "Right", "Confirm", "Cancel", "Menu", "Swap", "Calc",
				"Hotkey 1", "Hotkey 2", "Hotkey 3", "Hotkey 4", "Hotkey 5",
				"Screenshot", "Speedup", "Control", "Tooltips", "Backspace",
				};
		keys = new int[] {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_W,
				KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_A, KeyEvent.VK_C,
				KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
				KeyEvent.VK_ENTER, KeyEvent.VK_TAB, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SPACE,
				};
		defaultKeys = Arrays.copyOf(keys, keys.length);
	}
	
	public void setKeybind(int controlIndex, int newKey) {
		int conflictingControl = getControlForKey(newKey);
		
		if (conflictingControl != -1 && conflictingControl != controlIndex) {
			pendingKeys[conflictingControl] = pendingKeys[controlIndex];
		}
		
		pendingKeys[controlIndex] = newKey;
	}
	
	private int getControlForKey(int key) {
		for (int i = 0; i < pendingKeys.length; i++) {
			if (pendingKeys[i] == key) return i;
		}
		return -1;
	}
	
	public void resetControls() {
		pendingKeys = Arrays.copyOf(defaultKeys, defaultKeys.length);
	}
	
	public boolean setKeys() {
		boolean changed = !Arrays.equals(keys, pendingKeys);
		keys = Arrays.copyOf(pendingKeys, pendingKeys.length);
		return changed;
	}
	
	public void saveConfig() {
		try {
			Path configPath = util.SaveManager.getDocsDirectory().getParent().resolve(CONFIG_FILE);
			BufferedWriter bw = new BufferedWriter(new FileWriter(configPath.toFile()));
			
			// Audio settings
			bw.write("music=" + gp.music.volumeScale);
			bw.newLine();
			bw.write("sfx=" + gp.sfx.volumeScale);
			bw.newLine();
			
			// Keybinds
			for (int i = 0; i < keys.length; i++) {
				String keyString = keyNames[i].toLowerCase().replace(" ", "_") + "Key=";
				bw.write(keyString + keys[i]);
				bw.newLine();
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig() {
		try {
			Path configPath = util.SaveManager.getDocsDirectory().getParent().resolve(CONFIG_FILE);
			File configFile = configPath.toFile();
			
			if (!configFile.exists()) {
				saveConfig();
				return;
			}
			
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			String line;
			
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("=");
				if (parts.length != 2) continue;
				
				String key = parts[0].trim();
				String value = parts[1].trim();
				
				try {
					int intValue = Integer.parseInt(value);
					int keyNum = -1;
					
					switch (key) {
					case "music":
						gp.music.volumeScale = intValue;
						break;
					case "sfx":
						gp.sfx.volumeScale = intValue;
						break;
					case "upKey": keyNum = upKey; break;
					case "downKey": keyNum = downKey; break;
					case "leftKey": keyNum = leftKey; break;
					case "rightKey": keyNum = rightKey; break;
					case "confirmKey": keyNum = wKey; break;
					case "cancelKey": keyNum = sKey; break;
					case "menuKey": keyNum = dKey; break;
					case "swapKey": keyNum = aKey; break;
					case "screenshotKey": keyNum = screenshotKey; break;
					case "speedupKey": keyNum = speedupKey; break;
					case "ctrlKey": keyNum = ctrlKey; break;
					case "tooltipsKey": keyNum = tooltipsKey; break;
					case "backspaceKey": keyNum = backspaceKey; break;
					case "hotkey_1Key": keyNum = hotkey1; break;
					case "hotkey_2Key": keyNum = hotkey2; break;
					case "hotkey_3Key": keyNum = hotkey3; break;
					case "hotkey_4Key": keyNum = hotkey4; break;
					case "hotkey_5Key": keyNum = hotkey5; break;
					case "calcKey": keyNum = calcKey; break;
					}
					if (keyNum >= 0) {
						if (pendingKeys == null) {
							pendingKeys = Arrays.copyOf(keys, keys.length);
						}
						setKeybind(keyNum, intValue);
					}
				} catch (NumberFormatException e) {
					System.err.println("Invalid config value: " + line);
				}
			}
			setKeys();
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getKeyName(int keyCode) {
		String keyText = KeyEvent.getKeyText(keyCode);
		switch (keyText) {
		case "Up": return "\u2191";
		case "Down": return "\u2193";
		case "Left": return "\u2190";
		case "Right": return "\u2192";
		default: return keyText;
		}
	}
}
