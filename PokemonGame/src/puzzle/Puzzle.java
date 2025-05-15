package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	public abstract void update(Object obj);
	public abstract boolean checkCompletion();
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public int getFloor() {
		return floor;
	}
	
	protected String[] shuffle(String[] input) {
		List<String> list = new ArrayList<>(Arrays.asList(input));
        Collections.shuffle(list, gp.puzzleM.GAUNTLET_RNG);
        return list.toArray(new String[1]);
	}
}
