package pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Set {
	private int id;
	private ArrayList<Ability> abilities;
	private ArrayList<Item> items;
	private ArrayList<Nature> natures;
	private int[] ivs;
	
	private ArrayList<Move>[] moves;
	
	private static final Random RANDOM = new Random();
	
	@SuppressWarnings("unchecked")
	public Set(int id) {
		this.id = id;
		this.ivs = new int[] {31, 31, 31, 31, 31, 31};
		this.moves = new ArrayList[4];
	}
	
	public Pokemon makePokemon() {
		Pokemon p = new Pokemon(id, 100, false, true);
		p.item = getItem();
		Ability ability = getAbility();
		int abilitySlot = getAbilitySlot(p, ability);
		p.abilitySlot = abilitySlot;
		p.setAbility();
		p.nat = getNature();
		
		Moveslot[] moveslot = new Moveslot[4];
		for (int i = 0; i < moveslot.length; i++) {
			moveslot[i] = new Moveslot(getMove(i));
			moveslot[i].maxPP();
			moveslot[i].currentPP = moveslot[i].maxPP;
		}
		p.moveset = moveslot;
		p.ivs = this.ivs;
		return p;
	}
	
	private int getAbilitySlot(Pokemon p, Ability ability) {
		for (int k = 0; k < 3; k++) {
			Ability slot = p.getAbility(k);
			if (ability == slot) {
				return k;
			}
		}
		
		// DEBUGGING
		System.err.println("Set " + p.name() + " has an illegal ability: " + ability);
		return 2;
	}

	public void setIVs(PType type) {
		if (type == null) return;
		this.ivs = Pokemon.determineOptimalIVs(type);
	}
	
	public void setItems(Item... items) {
		this.items = new ArrayList<>(Arrays.asList(items));
	}
	
	public void setNatures(Nature... natures) {
		this.natures = new ArrayList<>(Arrays.asList(natures));
	}
	
	public void setMoves(int index, Move... moves) {
		this.moves[index] = new ArrayList<>(Arrays.asList(moves));
	}
	
	public void setAbility(Ability... abilities) {
		this.abilities = new ArrayList<>(Arrays.asList(abilities));
	}
	
	public Item getItem() {
		if (this.items.size() == 1) return this.items.get(0);
		return this.items.get(RANDOM.nextInt(this.items.size()));
	}
	
	public Move getMove(int index) {
		if (this.moves[index].size() == 1) return this.moves[index].get(0);
		Move m = this.moves[index].get(RANDOM.nextInt(this.moves[index].size()));
		if (m.id >= Move.HP_ROCK.id && m.id <= Move.RETURN_GALACTIC.id) {
			PType type = m.mtype;
			setIVs(type);
			m = m.id <= Move.HP_GALACTIC.id ? Move.HIDDEN_POWER : Move.RETURN;
		}
		return m;
	}
	
	public Nature getNature() {
		if (this.natures.size() == 1) return this.natures.get(0);
		return this.natures.get(RANDOM.nextInt(this.natures.size()));
	}
	
	public Ability getAbility() {
		if (this.abilities.size() == 1) return this.abilities.get(0);
		return this.abilities.get(RANDOM.nextInt(this.abilities.size()));
	}
	
	@Override
	public String toString() {
	    return "Set{id=" + id +
           ", items=" + items +
           ", natures=" + natures +
           ", abilities=" + abilities +
           ", ivs=" + Arrays.toString(ivs) +
           ", moves=" + Arrays.deepToString(moves) +
           '}';
	}

	public ArrayList<Move> getMoves() {
		ArrayList<Move> allMoves = new ArrayList<>();
		for (ArrayList<Move> moveSlot : moves) {
			if (moveSlot != null) {
				allMoves.addAll(moveSlot);
			}
		}
		return allMoves;
	}
	
	public void validate() {
		Pokemon temp = new Pokemon(id, 100, false, true);
		String pokemonName = temp.name();
		
		// Validate abilities
		Ability[] validAbilities = new Ability[3];
		for (int k = 0; k < 3; k++) {
			validAbilities[k] = temp.getAbility(k);
		}
		
		ArrayList<Ability> invalidAbilities = new ArrayList<>();
		for (Ability ability : this.abilities) {
			boolean isValid = false;
			for (Ability validAbility : validAbilities) {
				if (ability == validAbility) {
					isValid = true;
					break;
				}
			}
			if (!isValid) {
				invalidAbilities.add(ability);
			}
		}
		
		if (!invalidAbilities.isEmpty()) {
			System.err.println("\nWARNING: " + pokemonName + " has invalid abilities: " + invalidAbilities);
			System.err.println("  Valid abilities are: " + Arrays.toString(validAbilities));
		}
		
		// Validate moves
		ArrayList<Move> allMoves = getMoves();
		boolean[] movesValid = Pokemon.validateMoveset(this.id, 100, allMoves);
		ArrayList<Move> invalidMoves = new ArrayList<>();
		for (int b = 0; b < allMoves.size(); b++) {
			if (!movesValid[b]) {
				invalidMoves.add(allMoves.get(b));
			}
		}
		
		if (!invalidMoves.isEmpty()) {
			System.err.println("\nWARNING: " + this + " has invalid moves: " + invalidMoves);
		}
	}
}
