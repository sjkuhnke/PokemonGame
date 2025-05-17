package puzzle;

import java.util.HashMap;

import overworld.*;
import pokemon.*;

public class PuzzleManager {
	public Puzzle[] faithPuzzles;
	public Puzzle[] logicPuzzles;
	
	public GamePanel gp;
	
	public int[] FAITH_START = new int[] { 191, 49, 47 };
	public int[] LOGIC_START = new int[] {};
	
	public PuzzleManager(GamePanel gp) {
		this.gp = gp;
		faithPuzzles = new Puzzle[3];
		setupPuzzle(true, 0, new ColorPuzzle(gp, 191), 50, 67);
		setupPuzzle(true, 1, new CatchingPuzzle(gp, 193), 50, 50);
		setupPuzzle(true, 2, new GamblingPuzzle(gp, 195), 50, 50);
		logicPuzzles = new Puzzle[1];
	}
	
	private void setupPuzzle(boolean faith, int i, Puzzle puzzle, int x, int y) {
		Puzzle[] puzzles = faith ? faithPuzzles : logicPuzzles;
		puzzle.setNextLocation(x, y);
		puzzles[i] = puzzle;
	}

	public void setup(boolean faith) {
		Puzzle[] puzzles = faith ? faithPuzzles : logicPuzzles;
		for (Puzzle p : puzzles) {
			if (p != null) {
				p.reset();
				p.setup();
			}
		}
	}
	
	public void reset(boolean faith) {
		gp.player.p.bag.removeAll(Item.TEMPLE_BALL);
		gp.player.p.puzzlesLocked = new HashMap<>();
		setup(faith);
	}

	public Puzzle getCurrentPuzzle(int currentMap) {
		for (Puzzle p : faithPuzzles) {
			if (p != null && p.floor == currentMap) return p;
		}
		for (Puzzle p : logicPuzzles) {
			if (p != null && p.floor == currentMap) return p;
		}
		return null;
	}

	public void doReset(boolean faith) {
		Task t = Task.addTask(Task.RESET, "");
		t.wipe = true;
	}
}
