package puzzle;

import overworld.*;
import pokemon.*;

public class CatchingPuzzle extends Puzzle {
	
	private Pokemon correct;
	private String[] clues;
	
	private static String[][] clueBank = new String[][] {
		// whether or not it resists, neutral, or is weak to fighting
		{"Martial force stumbles against it - fists find no purpose.", "It weathers strikes from Fighting-types like any other - no shield of spirit nor body.", "It cannot withstand the discipline of combat - fighting moves find their mark."},
		// can/can't learn earthquake
		{"The ground has heard its cry before... the earth has shook - and answered.", "It walks lightly - the earth knows it not."},
		// can/can't learn surf
		{"It has crossed waters deeper than doubt itself.", "Its path ends at the shore. The tide never claimed it."},
		// has a weather associated with their type or not
		{"The skies shift to welcome it - clouds part, or gather, or burn.", "No storm nor stillness heeds its name. It walks apart from the winds."},
		// has a terrain associated with their type or not
		{"The earth blooms or pulses in its wake. The terrain changes - not for you, but for it.", "It walks on plain stone. The terrain knows no song for it."},
		// whether this pokemon is a physical, special or mixed attacker
		{"Its blows are felt, not seen. Steel, bone, and motion define it.", "It strikes with the seen or the unseen - I cannot say which.", "Its power is not in the flesh, but in the mind and soul."},
		// can still evolve or not
		{"It has not yet reached its final form. Faith still shapes it.", "It is complete. No more growth, only purpose."},
		// is super effective against poison, neutral against poison or weak against poison
		{"It dares strike poison, but its efforts are dulled - the toxins endure its touch.", "It walks beside poison - neither friend nor foe. Its blows are met as neutral.", "It purges corruption with ease - poison flees before it."},
	};
	
	public CatchingPuzzle(GamePanel gp, int floor) {
		super(gp, floor);
		this.clues = new String[clueBank.length];
	}

	@Override
	public void setup() {
		correct = gp.encounterPokemon(area, 'G', false);
		setClues();
		for (int i = 0; i <= 7; i++) {
			gp.npc[floor][i].altDialogue = clues[i];
		}
	}

	private void setClues() {
		/**
		 * 
		 */
		double fightingMultiplier = correct.getEffectiveMultiplier(PType.FIGHTING, null, null);
		clues[0] = fightingMultiplier < 1.0 ? clueBank[0][0] : fightingMultiplier > 1.0 ? clueBank[0][2] : clueBank[0][1];
		/**
		 * 
		 */
		boolean learnEarthquake = Item.TM20.getLearned(correct);
		clues[1] = learnEarthquake ? clueBank[1][0] : clueBank[1][1];
		/**
		 * 
		 */
		boolean learnSurf = Item.HM04.getLearned(correct);
		clues[2] = learnSurf ? clueBank[2][0] : clueBank[2][1];
		/**
		 * 
		 */
		boolean hasWeather = correct.isType(PType.FIRE) || correct.isType(PType.WATER) || correct.isType(PType.ICE) || correct.isType(PType.ROCK);
		clues[3] = hasWeather ? clueBank[3][0] : clueBank[3][1];
		/**
		 * 
		 */
		boolean hasTerrain = correct.isType(PType.GRASS) || correct.isType(PType.ELECTRIC) || correct.isType(PType.PSYCHIC) || correct.isType(PType.MAGIC);
		clues[4] = hasTerrain ? clueBank[4][0] : clueBank[4][1];
		/**
		 * 
		 */
		int attackType = Integer.compare(correct.getBaseStat(3), correct.getBaseStat(1));
		attackType++; // to go from [-1, 1] to [0, 2]
		clues[5] = clueBank[5][attackType];
		/**
		 * 
		 */
		boolean canEvolve = correct.canEvolve();
		clues[6] = canEvolve ? clueBank[6][0] : clueBank[6][1];
		/**
		 * 
		 */
		Pokemon test = new Pokemon(1, 5, true, false);
		test.type1 = PType.POISON;
		test.type2 = null;
		double poisonDefensiveMultiplier1 = test.getEffectiveMultiplier(correct.type1, null, null);
		double poisonDefensiveMultiplier2 = correct.type2 != null ? test.getEffectiveMultiplier(correct.type2, null, test) : 1.0;
		double poisonDefensiveMultiplier = (poisonDefensiveMultiplier1 + poisonDefensiveMultiplier2) / 2;
		int poisonDefensiveIndex = (int) (poisonDefensiveMultiplier * 2) - 1;
		clues[7] = clueBank[7][poisonDefensiveIndex];
		
		clues = shuffle(clues);
	}

	@Override
	public void reset() {
		super.reset();

	}

	@Override
	public void update(Object obj) {
		if (isLocked()) return;
		isComplete = ((Pokemon) obj).id == correct.id;
		setLocked(true);
	}
}
