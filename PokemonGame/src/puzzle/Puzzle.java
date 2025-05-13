package puzzle;

import entity.PlayerCharacter;
import overworld.GamePanel;
import overworld.PMap;

public abstract class Puzzle {
	protected int floor;
	protected boolean isComplete;
	protected String area;
	GamePanel gp;
	
	public Puzzle(GamePanel gp, int floor) {
		this.gp = gp;
		this.floor = floor;
		this.isComplete = false;
		PMap.getLoc(floor, 0, 0);
		this.area = PlayerCharacter.currentMapName;
	}
	
	public abstract void setup();
	public abstract void reset();
	public abstract void update();
	public abstract boolean checkCompletion();
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public int getFloor() {
		return floor;
	}
}
