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
	public int screenshotKey = 8;
	public int speedupKey = 9;
	public int ctrlKey = 10;
	public int tooltipsKey = 11;
	public int backspaceKey = 12;
	// TODO: add extra key controls for extra overworld key items
	// TODO: make the controls screen not update your keybinds until you close the window!
	
	public final String[] keyNames;
	public int[] keys;
	public final int[] defaultKeys;
	
	public Config(GamePanel gp) {
		this.gp = gp;
		keyNames = new String[] {"Up", "Down", "Left", "Right", "Confirm", "Cancel", "Menu", "Extra",
				"Screenshot", "Speedup", "Control", "Tooltips", "Backspace"};
		keys = new int[] {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_W,
				KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_A, KeyEvent.VK_ENTER, KeyEvent.VK_TAB, KeyEvent.VK_CONTROL,
				KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SPACE};
		defaultKeys = Arrays.copyOf(keys, keys.length);
	}
	
	public void setKeybind(int controlIndex, int newKey) {
		int conflictingControl = getControlForKey(newKey);
		
		if (conflictingControl != -1 && conflictingControl != controlIndex) {
			keys[conflictingControl] = keys[controlIndex];
		}
		
		keys[controlIndex] = newKey;
	}
	
	private int getControlForKey(int key) {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == key) return i;
		}
		return -1;
	}
	
	public void resetControls() {
		keys = Arrays.copyOf(defaultKeys, defaultKeys.length);
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
				String keyString = keyNames[i].toLowerCase() + "Key=";
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
					case "extraKey": keyNum = aKey; break;
					case "screenshotKey": keyNum = screenshotKey; break;
					case "speedupKey": keyNum = speedupKey; break;
					case "ctrlKey": keyNum = ctrlKey; break;
					case "tooltipsKey": keyNum = tooltipsKey; break;
					case "backspaceKey": keyNum = backspaceKey; break;
					}
					if (keyNum >= 0) {
						setKeybind(keyNum, intValue);
					}
				} catch (NumberFormatException e) {
					System.err.println("Invalid config value: " + line);
				}
				
			}
			
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
