package util;

import overworld.GamePanel;

public class ToolTip {
	public GamePanel gp;
	public String key;
	public String label;
	public boolean shorten;
	public int[] controlIndex;
	
	public ToolTip(GamePanel gp, String label, String separator, boolean shorten, int... controlIndex) {
		this.gp = gp;
		this.controlIndex = controlIndex;
		this.shorten = shorten;
		this.label = label;
		
		int[] keys = new int[controlIndex.length];
		for (int i = 0; i < controlIndex.length; i++) {
			keys[i] = gp.config.keys[controlIndex[i]];
		}
		buildKey(separator, shorten, keys);
	}
	
	public void buildKey(String separator, boolean shorten, int... keys) {
		StringBuilder keyBuilder = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			String keyName = gp.config.getKeyName(keys[i]);
			
			if (shorten) keyName = shortenKeyName(keyName);
			keyBuilder.append("[").append(keyName).append("]");
			if (i < keys.length - 1) {
				keyBuilder.append(separator);
			}
		}
		this.key = keyBuilder.toString();
	}
	
	private String shortenKeyName(String keyName) {
		String[] words = keyName.split("[\\s-]+");
		StringBuilder shortened = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			shortened.append(word.substring(0, Math.min(3, word.length())));
			if (i < words.length - 1) {
				shortened.append(" ");
			}
		}
		return shortened.toString();
	}

	@Override
	public String toString() {
		return this.key + " " + this.label;
	}
}
