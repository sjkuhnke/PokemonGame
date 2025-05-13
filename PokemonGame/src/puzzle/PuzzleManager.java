package puzzle;

import overworld.*;

public class PuzzleManager {
	public Puzzle[] faithPuzzles;
	public Puzzle[] logicPuzzles;
	
	public GamePanel gp;
	
	public PuzzleManager(GamePanel gp) {
		faithPuzzles = new Puzzle[5];
		faithPuzzles[0] = new CatchingPuzzle(gp, 191);
	}
	
	public void setup(boolean faith) {
		Puzzle[] puzzles = faith ? faithPuzzles : logicPuzzles;
		for (Puzzle p : puzzles) {
			if (p != null) p.setup();
		}
	}
}
