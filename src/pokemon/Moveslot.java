package pokemon;

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
			return new Color(134, 38, 0);
		} else if (ratio <= 0.5) {
			return new Color(71, 69, 4);
		} else {
			return Color.black;
		}
	}
	
	public Moveslot clone() {
		Moveslot result = new Moveslot(this.move);
		result.maxPP = this.maxPP;
		result.currentPP = this.currentPP;
		return result;
	}

	public void ppUp() {
		this.maxPP += Math.max((this.move.pp / 5), 1);
	}
	
	public void maxPP() {
		this.maxPP = (this.move.pp * 8 / 5);
	}
}
