package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import entity.PlayerCharacter;
import overworld.GamePanel;
import overworld.PMap;
import pokemon.Task;

public abstract class Puzzle {
	protected int floor;
	protected boolean isComplete;
	protected String area;
	GamePanel gp;
	protected Random random;
	protected int[] nextLoc;
	protected boolean lost;
	protected boolean started;
	
	public Puzzle(GamePanel gp, int floor) {
		this.gp = gp;
		this.floor = floor;
		this.isComplete = false;
		this.lost = false;
		this.started = false;
		PMap.getLoc(floor, 0, 0);
		this.area = PlayerCharacter.currentMapName;
		this.random = new Random();
	}
	
	public void setup() {
		this.started = true;
	}
	
	public void reset() {
		this.isComplete = false;
		this.lost = false;
		this.started = false;
	}
	
	public abstract void update(Object obj);
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean isLost() {
		return lost;
	}
	
	public boolean isLocked() {
		Boolean result = gp.player.p.puzzlesLocked.get(floor);
		return result == null ? false : result;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setLocked(boolean locked) {
		gp.player.p.puzzlesLocked.put(floor, locked);
	}
	
	public int getFloor() {
		return floor;
	}
	
	protected String[] shuffle(String[] input) {
		List<String> list = new ArrayList<>(Arrays.asList(input));
        Collections.shuffle(list, random);
        return list.toArray(new String[1]);
	}
	
	protected String[][] shuffle(String[][] input) {
		List<String[]> list = new ArrayList<>(Arrays.asList(input));
        Collections.shuffle(list, random);
        return list.toArray(new String[1][]);
	}
	
	public void setNextLocation(int x, int y) {
		nextLoc = new int[] {x, y};
	}

	public void sendToNext() {
		Task t = Task.addTask(Task.TELEPORT, "");
		t.counter = floor + 1;
		t.start = nextLoc[0];
		t.finish = nextLoc[1];
	}
}
