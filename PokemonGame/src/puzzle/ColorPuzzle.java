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
		super.setup();
		colors = shuffle(colors);
		InteractiveTile[] paintings = gp.iTile[floor];
		int index = 0;
		for (InteractiveTile i : paintings) {
			if (i != null) {
				Painting painting = (Painting) i;
				if (painting.isColorPainting()) painting.setColor(colors[index++]);
			}
		}
		correct = colors[random.nextInt(colors.length)];
		clues = new String[6]; // even if the word is shorter, 6 to avoid out of bounds exceptions
		
		for (int i = 0; i < clues.length; i++) {
			if (i < correct.length()) {
				clues[i] = generateLetterClue(correct.charAt(i), i);
			} else {
				clues[i] = generateFillerClue();
			}
		}
		clues = shuffle(clues);
		for (int i = 2; i <= 7; i++) {
			gp.npc[floor][i].altDialogue = clues[i - 2];
		}		
	}
	
	private String generateLetterClue(char letter, int index) {
		switch (letter) {
			case 'e': return "I'm the most common letter.";
			case 'r': return "I'm one of the most colorful consonants.";
			case 'd': return "This letter ends many past actions.";
			case 'g': return "I grow in gardens and guesses.";
			case 'u': return "Often paired with another letter, but here I stand alone.";
			case 'l': return "Tall and thin, I lean like a line.";
			case 'b': return "One or two bubbles stacked on a line.";
			case 'o': return "I'm a perfect circle, open and whole.";
			case 'y': return "I question everything.";
			case 'n': return "I am nestled in end.";
			case 'p': return "A painter's tool, or half a butterfly.";
			case 'w': return "I begin questions and weather alike.";
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
		return fillerClues[random.nextInt(fillerClues.length)];
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public void update(Object obj) {
		Painting painting = (Painting) obj;
		if (painting.getColor().equals(correct)) {
			this.isComplete = true;
		} else {
			this.isComplete = false;
			this.lost = true;
		}
		this.setLocked(true);
	}
}
