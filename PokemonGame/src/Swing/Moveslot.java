package Swing;

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
	
	public double getPPRatio() {
		return currentPP / maxPP;
	}
}
