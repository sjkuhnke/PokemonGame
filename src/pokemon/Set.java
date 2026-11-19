package pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import util.Rng;

public class Set {
	private int id;
	private ArrayList<Ability> abilities;
	private ArrayList<Item> items;
	private ArrayList<Nature> natures;
	private int level = 100;
	private int[] baseIvs = {31,31,31,31,31,31};
	
	private ArrayList<Move>[] moves;
	
	private static Random rng() { return Rng.asRandom(); }
	
	@SuppressWarnings("unchecked")
	public Set(int id) {
		this.id = id;
		this.moves = new ArrayList[4];
	}
	
	public int getLevel() { return level; }
	public void setLevel(int l) { level = Math.max(1, Math.min(100, l)); }
	public int getId() { return id; }
	public Pokemon makePokemon() { return makePokemon(level); }
	
	public Pokemon makePokemon(int level) {
		Pokemon p = new Pokemon(id, level, false, true);
		p.item = getItem();
		Ability ability = getAbility();
		p.abilitySlot = getAbilitySlot(p, ability);
		p.setAbility();
		p.nat = getNature();

		int[] ivs = baseIvs.clone(); // per-Pokemon copy; HP moves may change it below
		Moveslot[] moveslot = new Moveslot[4];
		for (int i = 0; i < moveslot.length; i++) {
			if (this.moves[i] == null) break;
			moveslot[i] = new Moveslot(getMove(i, ivs));
			moveslot[i].maxPP();
			moveslot[i].currentPP = moveslot[i].maxPP;
		}
		p.moveset = moveslot;
		p.ivs = ivs;

		p.setStats();
		p.verifyHP();
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

	/** Permanent IVs of the set (used when an Hp/Return move isn't rolled). */
	public void setIVs(int[] ivs) {
		if (ivs != null && ivs.length == 6) this.baseIvs = Arrays.copyOf(ivs, 6);
	}

	/** Kept for outside callers; sets the permanent IVs. Not used while rolling. */
	public void setIVs(PType type) {
		if (type == null) return;
		this.baseIvs = Pokemon.determineOptimalIVs(type).clone();
	}

	/** Rolls slot `index`. If it's an Hp/Return variant, writes the matching IVs into `ivs` and returns the real move. */
	public Move getMove(int index, int[] ivs) {
		ArrayList<Move> opts = this.moves[index];
		Move m = opts.size() == 1 ? opts.get(0) : opts.get(rng().nextInt(opts.size()));
		if (m.id >= Move.HP_ROCK.id && m.id <= Move.RETURN_GALACTIC.id) {
			int[] opt = Pokemon.determineOptimalIVs(m.mtype);
			System.arraycopy(opt, 0, ivs, 0, 6);
			m = m.id <= Move.HP_GALACTIC.id ? Move.HIDDEN_POWER : Move.RETURN;
		}
		return m;
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
		return this.items.get(rng().nextInt(this.items.size()));
	}
	
	public Nature getNature() {
		if (this.natures.size() == 1) return this.natures.get(0);
		return this.natures.get(rng().nextInt(this.natures.size()));
	}
	
	public Ability getAbility() {
		if (this.abilities.size() == 1) return this.abilities.get(0);
		return this.abilities.get(rng().nextInt(this.abilities.size()));
	}
	
	@Override
	public String toString() {
		return "Set{id=" + id +
		   ", items=" + items +
		   ", natures=" + natures +
		   ", abilities=" + abilities +
		   ", ivs=" + Arrays.toString(baseIvs) +
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
