package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import entity.Entity;
import overworld.GamePanel;

public class Nursery implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum EggGroup implements Serializable {
		UNDISCOVERED(),
		MONSTER(),
		HUMAN_LIKE(),
		WATER_1(),
		WATER_2(),
		WATER_3(),
		BUG(),
		MINERAL(),
		FLYING(),
		AMORPHOUS(),
		FIELD(),
		FAIRY(),
		GRASS(),
		DRAGON(),
		ALIEN(),
		;
	}
	
	private Pokemon[] pokemon;
	private int size;
	private int state;
	private Egg egg;
	
	public static final int NOT_FULL = -1;
	public static final int SAME_SPECIES = 0;
	public static final int DIFFERENT_SPECIES = 1;
	public static final int NOT_COMPATIBLE = 2;
	public static final int EGG = 3;
	
	public Nursery() {
		pokemon = new Pokemon[2];
		state = NOT_FULL;
	}
	
	public boolean deposit(Pokemon p) {
		if (isFull()) return false;
		
		p.heal();
		pokemon[size++] = p;
		
		determineCompatibility();
		return true;
	}
	
	private void determineCompatibility() {
		if (isFull()) {
			boolean sameSpecies = false;
			ArrayList<String> evoFamily = pokemon[0].getEvolutionFamily();
			for (String s : evoFamily) {
				if (Pokemon.getIDFromName(s) == pokemon[1].id) {
					sameSpecies = true;
					break;
				}
			}
			if (sameSpecies) {
				if (!Pokemon.getEggGroup(pokemon[0].id).contains(EggGroup.UNDISCOVERED)) {
					state = SAME_SPECIES;
				} else {
					state = NOT_COMPATIBLE;
				}
			} else {
				if (pokemon[0].isCompatible(pokemon[1])) {
					state = DIFFERENT_SPECIES;
				} else {
					state = NOT_COMPATIBLE;
				}
			}
		} else {
			state = NOT_FULL;
		}
	}

	public Pokemon withdraw(Pokemon p) {
		for (int i = 0; i < pokemon.length; i++) {
			if (p == pokemon[i]) {
				Pokemon result = pokemon[i];
				pokemon[i] = null;
				size--;
				shift();
				state = NOT_FULL;
				return result;
			}
		}
		
		System.out.println("Pokemon " + p + " was not present in the nursery"); // should never happen
		return null;
	}
	
	private void shift() {
		Pokemon[] newArray = new Pokemon[pokemon.length];
		int index = 0;
		
		for (int i = 0; i < pokemon.length; i++) {
			if (pokemon[i] != null) {
				newArray[index++] = pokemon[i];
			}
		}
		
		pokemon = newArray;
	}
	
	public boolean isFull() {
		return size >= pokemon.length;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public int getSize() {
		return size;
	}
	
	public Pokemon[] getPokemon() {
		return pokemon;
	}
	
	public void interactOutside(Entity npc) {
		Task.addTask(Task.DIALOGUE, npc, "Hello Finn!");
		if (state != EGG) {
			Task.addTask(Task.DIALOGUE, npc, "Ah yes, about your Pokemon.");
			Task.addTask(Task.DIALOGUE, npc, "...");
		}
		switch (state) {
		case NOT_FULL:
			Task.addTask(Task.DIALOGUE, npc, "We can hold up to 2 Pokemon - talk to my daughter inside to leave us with a Pokemon!");
			break;
		case SAME_SPECIES:
			Task.addTask(Task.DIALOGUE, npc, "The two seem to get along all right.");
			break;
		case DIFFERENT_SPECIES:
			Task.addTask(Task.DIALOGUE, npc, "The two don't really seem to like each other very much.");
			break;
		case NOT_COMPATIBLE:
			Task.addTask(Task.DIALOGUE, npc, "The two prefer to play with other Pokemon more than with each other.");
			break;
		case EGG:
			Task.addTask(Task.DIALOGUE, npc, "Your Pokemon made an egg! I couldn't believe it with my own eyes!");
			Task.addTask(Task.DIALOGUE, npc, "Here - I have no use for it, hatch it and see what's inside!");
			Task.addTask(Task.GIFT, "", egg);
			egg = null;
			npc.setDirection("down");
			determineCompatibility();
			break;
		}
	}
	
	public void interactInside(Entity npc) {
		Task.addTask(Task.DIALOGUE, npc, "Good day, Finn.");
		if (state != NOT_FULL && state != EGG) {
			Task.addTask(Task.DIALOGUE, npc, "Ah yes, about your Pokemon.");
			Task.addTask(Task.DIALOGUE, npc, "...");
			Task.addTask(Task.DIALOGUE, npc, "Your " + pokemon[0].getNickname() + " and your " + pokemon[1].getNickname() + " are both doing well.");
		}
		switch (state) {
		case NOT_FULL:
			Task.addTask(Task.DIALOGUE, npc, "We can hold up to 2 Pokemon. Would you like to deposit a Pokemon?");
			Task.addTask(Task.NURSERY_DEPOSIT, npc, "");
			break;
		case SAME_SPECIES:
		case DIFFERENT_SPECIES:
		case NOT_COMPATIBLE:
			Task.addTask(Task.NURSERY_WITHDRAW, npc, "Would you like to withdraw one of them?");
			break;
		case EGG:
			Task.addTask(Task.DIALOGUE, npc, "My Dad was looking to talk to you, please talk to him outside.");
			break;
		}
	}
	
	public void checkForEgg(GamePanel gp) {
		if (state == NOT_FULL || state == NOT_COMPATIBLE || state == EGG) return;
		int chance = 0;
		switch (state) {
		case SAME_SPECIES:
			chance = 50;
			break;
		case DIFFERENT_SPECIES:
			chance = 25;
			break;
		}
		
		if (new Random().nextInt(100) < chance) {
			createEgg(gp);
			state = EGG;
		}
	}

	private void createEgg(GamePanel gp) {
		Pokemon parent1 = pokemon[0];
		Pokemon parent2 = pokemon[1];

		// species (id) is random between the 2 parents unless one is holding destiny knot
		// eggs are created like this: new Egg(id, cycles (can just default to 3 for now, maybe different species could have different cycles down the road))
		// ability is 80% selected parent's (the one picked above) ability if it's a regular ability (abilitySlot < 2) or 60% hidden ability, 20% another ability slot if abilitySlot == 2
		// nature is random unless one of the parents are holding an everstone
		// ivs are either 3 passed down picked from either parent, or 5 passed down from the one holding a destiny knot
		// whichever parent is selected will pass down its ball to the egg as well
		
		boolean p1DestinyKnot = parent1.item == Item.DESTINY_KNOT;
		boolean p2DestinyKnot = parent2.item == Item.DESTINY_KNOT;
		Pokemon parent = (p1DestinyKnot && !p2DestinyKnot) ? parent1 :
						(!p1DestinyKnot && p2DestinyKnot) ? parent2 :
						Math.random() < 0.5 ? parent1 : parent2;
		
		int id = parent.getBabyStage();
		
		int abilitySlot = parent.abilitySlot;
		double r = Math.random();
		int ability;
		if (abilitySlot == 2) {
			if (r < 0.6) ability = 2;
			else ability = (Math.random() < 0.5) ? 0 : 1;
		} else {
			ability = (r < 0.8) ? abilitySlot : (abilitySlot == 0 ? 1 : 0);
		}
		
		Nature nature;
		if (parent1.item == Item.EVERSTONE) {
			nature = parent1.nat;
		} else if (parent2.item == Item.EVERSTONE) {
			nature = parent2.nat;
		} else {
			nature = Nature.getRandomNature();
		}
		
		egg = new Egg(id);
		
		int[] ivs = egg.ivs.clone();
		boolean useDK = p1DestinyKnot || p2DestinyKnot;
		int inheritedIVs = useDK ? 5 : 3;
		
		List<Integer> stats = new ArrayList<>(Arrays.asList(new Integer[] {0, 1, 2, 3, 4, 5}));
		Collections.shuffle(stats);
		for (int i = 0; i < inheritedIVs; i++) {
			int statIndex = stats.get(i);
			Pokemon from = Math.random() < 0.5 ? parent1 : parent2;
			ivs[statIndex] = from.ivs[statIndex];
		}
		
		Item ball = parent.ball;
		
		egg.ivs = ivs;
		egg.abilitySlot = ability;
		egg.setAbility();
		egg.nat = nature;
		egg.ball = ball;
		
		gp.npc[152][0].setDirection("left");
	}
	
	public boolean hasEgg() {
		return state == EGG;
	}
	
	public Egg getEgg() {
		return egg;
	}
}
