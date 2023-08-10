package Swing;

import java.awt.Color;

public enum Status {
	BURNED("BRN", new Color(230, 9, 15), Color.BLACK),
	PARALYZED("PRZ", new Color(219, 216, 15), Color.BLACK),
	ASLEEP("SLP", new Color(105, 105, 102), Color.WHITE),
	POISONED("PSN", new Color(68, 2, 161), Color.WHITE),
	HEALTHY("", new Color(255, 255, 255), Color.BLACK),
	FROSTBITE("FRS", new Color(150, 217, 214), Color.BLACK),
	TOXIC("TOX", new Color(68, 2, 161), Color.WHITE),
	CONFUSED("CNF", new Color(32, 37, 61), Color.WHITE),
	CURSED("CRS", new Color(30, 32, 41), Color.WHITE),
	LEECHED("LCH", new Color(21, 143, 40), Color.BLACK),
	NIGHTMARE("NGT", new Color(0, 0, 0), Color.WHITE), 
	FLINCHED("FLN", new Color(32, 37, 61), Color.WHITE),
	AQUA_RING("AQR", new Color(84, 117, 247), Color.BLACK), 
	CHARGED("CRG", new Color(219, 216, 15), Color.BLACK),
	SPUN("SPN", new Color(32, 37, 61), Color.WHITE),
	RECHARGE("SPN", new Color(32, 37, 61), Color.WHITE),
	POSESSED("SPN", new Color(32, 37, 61), Color.WHITE),
	CHARGING("CRG", new Color(219, 216, 15), Color.BLACK),
	LOCKED("LCK", new Color(219, 216, 15), Color.BLACK),
	REFLECT("RFL", new Color(219, 216, 15), Color.BLACK),
	AUTO("AUT", new Color(219, 216, 15), Color.BLACK),
	BONDED("AUT", new Color(219, 216, 15), Color.BLACK), 
	TRAPPED("AUT", Color.BLACK, Color.BLACK),
	PROTECT("AUT",Color.BLACK,Color.BLACK),
	SEMI_INV("AUT",Color.BLACK,Color.BLACK),
	ENCORED("AUT",Color.BLACK,Color.BLACK),
	TAUNTED("AUT",Color.BLACK,Color.BLACK),
	TORMENTED("AUT",Color.BLACK,Color.BLACK),
	ENDURE("AUT",Color.BLACK,Color.BLACK),
	FOCUS_ENERGY("AUT",Color.BLACK,Color.BLACK),
	FLASH_FIRE("AUT",Color.BLACK,Color.BLACK),
	NO_SWITCH("AUT",Color.BLACK,Color.BLACK),
	SMACK_DOWN("AUT",Color.BLACK,Color.BLACK),
	SWITCHING("AUT",Color.BLACK,Color.BLACK),
	;
	
	Status(String name, Color color, Color textColor) {
		this.name = name;
		this.color = color;
		this.textColor = textColor;
	}
	
	private String name;
	private Color color;
	private Color textColor;
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Color getTextColor() {
		return textColor;
	}
}
