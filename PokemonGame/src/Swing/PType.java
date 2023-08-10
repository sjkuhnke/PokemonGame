package Swing;

import java.awt.Color;

public enum PType {
	NORMAL(new Color(168, 167, 122)),
	ROCK(new Color(182, 161, 54)),
	FIRE(new Color(238, 129, 48)),
	WATER(new Color(99, 144, 240)),
	GRASS(new Color(122, 199, 76)),
	ICE(new Color(150, 217, 214)),
	ELECTRIC(new Color(247, 208, 44)),
	FIGHTING(new Color(194, 46, 40)),
	POISON(new Color(163, 62, 161)),
	GROUND(new Color(226, 191, 101)),
	FLYING(new Color(169, 143, 243)),
	PSYCHIC(new Color(249, 85, 135)),
	BUG(new Color(166, 185, 26)),
	GHOST(new Color(115, 87, 151)),
	DRAGON(new Color(111, 53, 252)),
	STEEL(new Color(183, 183, 206)),
	DARK(new Color(112, 87, 70)),
	LIGHT(new Color(248, 248, 120)),
	MAGIC(new Color(254, 1, 77)),
	GALACTIC(new Color(138, 30, 106)),
	UNKNOWN(Color.BLUE.brighter());
	
	private Color color;

	private PType(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override // implementation
	public String toString() {
		String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}