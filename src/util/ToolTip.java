package util;

import overworld.GamePanel;

public class ToolTip {
	public String key;
	public String label;
	public int[] controlIndex;
	
	public ToolTip(GamePanel gp, String label, String separator, int... controlIndex) {
		this.controlIndex = controlIndex;
		this.label = label;
		
		StringBuilder keyBuilder = new StringBuilder();
		for (int i = 0; i < controlIndex.length; i++) {
			keyBuilder.append("[").append(gp.config.getKeyName(gp.config.keys[controlIndex[i]])).append("]");
			if (i < controlIndex.length - 1) {
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
