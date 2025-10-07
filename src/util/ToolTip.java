package util;

import overworld.GamePanel;

public class ToolTip {
	public GamePanel gp;
	public String key;
	public String label;
	public int[] controlIndex;
	
	public ToolTip(GamePanel gp, String label, String separator, int... controlIndex) {
		this.gp = gp;
		this.controlIndex = controlIndex;
		this.label = label;
		
		int[] keys = new int[controlIndex.length];
		for (int i = 0; i < controlIndex.length; i++) {
			keys[i] = gp.config.keys[controlIndex[i]];
		}
		buildKey(separator, keys);
	}
	
	public void buildKey(String separator, int... keys) {
		StringBuilder keyBuilder = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			keyBuilder.append("[").append(gp.config.getKeyName(keys[i])).append("]");
			if (i < keys.length - 1) {
				keyBuilder.append(separator);
			}
		}
		this.key = keyBuilder.toString();
	}
	
	@Override
	public String toString() {
		return this.key + " " + this.label;
	}
}
