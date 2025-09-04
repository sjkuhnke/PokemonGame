package pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Set implements RoleAssignable {
	private int id;
	private ArrayList<Ability> abilities;
	private ArrayList<Item> items;
	private ArrayList<Nature> natures;
	private int[] ivs;
	private int role;
	
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
		p.role = role;
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
		throw new IllegalStateException("Set " + p.name() + " has an illegal ability: " + ability);
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
			System.out.println(m);
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
	
	public boolean hasRole(int role) {
	    return (this.role & role) != 0;
	}

	public void addRole(int role) {
	    this.role |= role;
	}

	public void removeRole(int role) {
	    this.role &= ~role;
	}
	
	public void setRole(int role) {
		this.role = role;
	}

	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> allMoves = new ArrayList<>();
		for (ArrayList<Move> moveSlot : moves) {
			if (moveSlot != null) {
				allMoves.addAll(moveSlot);
			}
		}
		return allMoves;
	}

	@Override
	public int[] getBaseStats() {
		return Pokemon.getBaseStats(id);
	}

	@Override
	public ArrayList<Ability> getAbilities() {
		return this.abilities;
	}

	@Override
	public ArrayList<Item> getItems() {
		return this.items;
	}

	@Override
	public ArrayList<Nature> getNatures() {
		return this.natures;
	}
}
