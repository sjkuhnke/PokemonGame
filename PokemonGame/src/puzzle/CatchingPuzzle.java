package puzzle;

import overworld.*;
import pokemon.*;

public class CatchingPuzzle extends Puzzle {
	
	private Pokemon correct;
	private String[][] clues;
	
	private static String[][] clueBank = new String[][] {
		// whether or not it resists, neutral, or is weak to fighting
		{"Does it resist combat, fall to it, or deal with it normally?", "Martial force stumbles against it - fists find no purpose.", "It weathers strikes from Fighting-types like any other - no shield of spirit nor body.", "It cannot withstand the discipline of combat - fighting moves find their mark."},
		// can/can't learn earthquake
		{"Was the ground itself able to be moved by it?", "The ground has heard its cry before... the earth has shook - and answered.", "It walks lightly - the earth knows it not."},
		// can/can't learn surf
		{"Can it cross the waters, or do the waves bar its path?", "It has crossed waters deeper than doubt itself.", "Its path ends at the shore. The tide never claimed it."},
		// has a weather associated with their type or not
		{"Does the forecast heed its coming?", "The skies shift to welcome it - clouds part, or gather, or burn.", "No storm nor sunshine heeds its name. It walks apart from the winds."},
		// has a terrain associated with their type or not
		{"Does it have a deeper touch to the land?", "The earth blooms or pulses in its wake. The terrain changes - not for you, but for it.", "It walks on plain stone. The terrain knows no song for it."},
		// whether this pokemon is a physical, special or mixed attacker
		{"How does it strike? Through flesh, mind, or both?", "Its blows are felt, not seen. Steel, bone, and motion define it.", "It strikes with the seen or the unseen - I cannot say which.", "Its power is not in the flesh, but in the mind and soul."},
		// can still evolve or not
		{"Has it reached its full potential, or is it still becoming?", "It has not yet reached its final form. Faith still shapes it.", "It is complete. No more growth, only purpose."},
		// is super effective against poison, neutral against poison or weak against poison
		{"What does it do to poison offensively - ignore it or destroy it?", "It dares strike poison, but its efforts are dulled - the toxins endure its touch.", "It walks beside poison - neither friend nor foe. Its blows are met as neutral.", "It purges corruption with ease - poison flees before it."},
		// speed tier: >=100, 100 > s > 60, <= 60?
		{"Does it outpace others, crawl behind, or walk with the crowd?", "It blurs the eye and bends time — few see it coming before it is gone. It is able to hit at least 259 Speed.", "It keeps pace with fate. Neither first nor last to act, hits between 259 and 171 Speed at its maximum.", "It moves with gravity — not by haste, but by inevitability. Is unable to hit anything higher than 171 Speed."},
		// can/can't learn trick tackle
		{"Can it weave deceit into its assault - especially when Tackling the opponent?", "It strikes not only with power, but with guile - its hands take as they give.", "Its battle style is honest, perhaps too honest - Trick Tackle is beyond its means."},
	};
	
	public CatchingPuzzle(GamePanel gp, int floor) {
		super(gp, floor);
		this.clues = new String[clueBank.length][2];
	}

	@Override
	public void setup() {
		super.setup();
		correct = gp.encounterPokemon(area, 'G', false);
		setClues();
		for (int i = 0; i <= 9; i++) {
			gp.npc[floor][i].altDialogue = String.format("%s\n...\n%s", clues[i][0], clues[i][1]);
		}
	}

	private void setClues() {
		for (int i = 0; i < clueBank.length; i++) {
			clues[i][0] = clueBank[i][0];
		}
		/**
		 * 
		 */
		double fightingMultiplier = correct.getEffectiveMultiplier(PType.FIGHTING, null, null);
		clues[0][1] = fightingMultiplier < 1.0 ? clueBank[0][1] : fightingMultiplier > 1.0 ? clueBank[0][3] : clueBank[0][2];
		/**
		 * 
		 */
		boolean learnEarthquake = Item.TM20.getLearned(correct);
		clues[1][1] = learnEarthquake ? clueBank[1][1] : clueBank[1][2];
		/**
		 * 
		 */
		boolean learnSurf = Item.HM04.getLearned(correct);
		clues[2][1] = learnSurf ? clueBank[2][1] : clueBank[2][2];
		/**
		 * 
		 */
		boolean hasWeather = correct.isType(PType.FIRE) || correct.isType(PType.WATER) || correct.isType(PType.ICE) || correct.isType(PType.ROCK);
		clues[3][1] = hasWeather ? clueBank[3][1] : clueBank[3][2];
		/**
		 * 
		 */
		boolean hasTerrain = correct.isType(PType.GRASS) || correct.isType(PType.ELECTRIC) || correct.isType(PType.PSYCHIC) || correct.isType(PType.MAGIC);
		clues[4][1] = hasTerrain ? clueBank[4][1] : clueBank[4][2];
		/**
		 * 
		 */
		int attackType = Integer.compare(correct.getBaseStat(3), correct.getBaseStat(1));
		attackType += 2; // to go from [-1, 1] to [1, 3]
		clues[5][1] = clueBank[5][attackType];
		/**
		 * 
		 */
		boolean canEvolve = correct.canEvolve();
		clues[6][1] = canEvolve ? clueBank[6][1] : clueBank[6][2];
		/**
		 * 
		 */
		Pokemon test = new Pokemon(1, 5, true, false);
		test.type1 = PType.POISON;
		test.type2 = null;
		double poisonDefensiveMultiplier1 = test.getEffectiveMultiplier(correct.type1, null, null);
		double poisonDefensiveMultiplier2 = correct.type2 != null ? test.getEffectiveMultiplier(correct.type2, null, null) : 1.0;
		double poisonDefensiveMultiplier = (poisonDefensiveMultiplier1 + poisonDefensiveMultiplier2) / 2;
		int poisonDefensiveIndex = (int) (poisonDefensiveMultiplier * 2);
		clues[7][1] = clueBank[7][poisonDefensiveIndex];
		/**
		 * 
		 */
		int speed = correct.getBaseStat(5);
		int speedIndex = speed >= 100 ? 1 : speed > 60 ? 2 : 3;
		clues[8][1] = clueBank[8][speedIndex];
		
		/**
		 * 
		 */
		boolean learnTrickTackle = Item.TM56.getLearned(correct);
		clues[9][1] = learnTrickTackle ? clueBank[9][1] : clueBank[9][2];
		
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
