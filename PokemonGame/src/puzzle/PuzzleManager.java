package puzzle;

import java.util.Random;

import overworld.*;

public class PuzzleManager {
	public Puzzle[] faithPuzzles;
	public Puzzle[] logicPuzzles;
	
	public GamePanel gp;
	
	public Random GAUNTLET_RNG;
	
	public PuzzleManager(GamePanel gp) {
		faithPuzzles = new Puzzle[5];
		faithPuzzles[0] = new ColorPuzzle(gp, 191);
		faithPuzzles[1] = new CatchingPuzzle(gp, 193);
	}
	
	public void setup(boolean faith) {
		Puzzle[] puzzles = faith ? faithPuzzles : logicPuzzles;
		for (Puzzle p : puzzles) {
			if (p != null) p.setup();
		}
	}
	
	public void initGauntletSeed(int playerID, int attemptCount) {
		long seed = ((long) playerID << 3) ^ attemptCount;
		GAUNTLET_RNG = new Random(seed);
	}
}
