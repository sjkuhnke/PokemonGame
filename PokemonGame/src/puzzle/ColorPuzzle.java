package puzzle;

import object.*;
import overworld.GamePanel;

public class ColorPuzzle extends Puzzle {
	
	private String correct;
	private String[] clues;
	
	private static String[] colors = new String[] {"red", "orange", "yellow", "green", "blue", "purple"};

	public ColorPuzzle(GamePanel gp, int floor) {
		super(gp, floor);
	}

	@Override
	public void setup() {
		colors = shuffle(colors);
		InteractiveTile[] paintings = gp.iTile[floor];
		int index = 0;
		for (InteractiveTile i : paintings) {
			if (i != null) {
				Painting painting = (Painting) i;
				if (painting.isColorPainting()) painting.setColor(colors[index++]);
			}
		}
		correct = colors[gp.puzzleM.GAUNTLET_RNG.nextInt(colors.length)];
		clues = new String[6]; // even if the word is shorter, 6 to avoid out of bounds exceptions
		
		for (int i = 0; i < clues.length; i++) {
			if (i < correct.length()) {
				clues[i] = generateLetterClue(correct.charAt(i), i);
			} else {
				clues[i] = generateFillerClue();
			}
		}
		clues = shuffle(clues);
	}
	
	private String generateLetterClue(char letter, int index) {
		switch (letter) {
			case 'e': return "Echoes follow this one.";
			case 'r': return "Red begins with me.";
			case 'd': return "I'm the last note in the answer.";
			case 'g': return "I grow in gardens and guesses.";
			case 'u': return "Underneath it all lies this clue.";
			case 'l': return "I lean to the left in the middle.";
			case 'b': return "The boldest hue begins with me.";
			case 'o': return "Round and orange, I sit second.";
			case 'y': return "Why not start with me?";
			case 'n': return "Nestled near the end.";
			case 'p': return "Paintbrushes start with me.";
			default: return "A hidden letter, cloaked in mystery.";
		}
	}
	
	private String generateFillerClue() {
		String[] fillerClues = {
			"I'm just here for the vibes.",
			"My colors have all faded...",
			"Don't trust every clue you hear.",
			"I saw it once, but the memory is hazy.",
			"Beauty lies in mystery.",
			"I prefer silence to spoilers."
		};
		return fillerClues[gp.puzzleM.GAUNTLET_RNG.nextInt(fillerClues.length)];
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkCompletion() {
		// TODO Auto-generated method stub
		return false;
	}

}
