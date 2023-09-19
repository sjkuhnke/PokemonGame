package Swing;

import java.awt.Color;
import java.io.Serializable;

public class Moveslot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int currentPP;
	public int maxPP;
	public Move move;

	public Moveslot(Move m) {
		currentPP = m.pp;
		maxPP = m.pp;
		move = m;
	}
	
	private double getPPRatio() {
		double num = currentPP * 1.0;
		return num / maxPP;
	}

	public String showPP() {
		return currentPP + " / " + maxPP;
	}
	
	public Color getPPColor() {
		double ratio = getPPRatio();
		if (ratio <= 0.25) {
			return new Color(184, 58, 0);
		} else if (ratio <= 0.5) {
			return new Color(130, 115, 1);
		} else {
			return Color.black;
		}
	}
}
