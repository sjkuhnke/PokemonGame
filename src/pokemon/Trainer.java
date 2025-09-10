package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import overworld.GamePanel;
import pokemon.Field.FieldEffect;

public class Trainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	public Pokemon[] team;
	int money;
	Item item;
	int flagIndex;
	public Pokemon current;
	public boolean update;
	public boolean staticEnc;
	public boolean catchable;
	public boolean cloned;
	public int boost;
	
	transient ArrayList<FieldEffect> effects;
	transient Item[] teamItems;
	
	public static final int MAX_TRAINERS = 550;
	public static Trainer[] trainers = new Trainer[MAX_TRAINERS];
	
	private static int levelCapIndex = 0;
	public static int[] levelCaps = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 100};
	
	public Trainer(String name, Pokemon[] team, int money) {
		this(name, team, money, null, 0, true);
	}
	
	public Trainer(String name, Pokemon[] team, Item[] items, int money) {
		this(name, team, items, money, null, 0);
	}
	
	public Trainer(String name, Pokemon[] team, Item[] items, int money, Item item) {
		this(name, team, items, money, item, 0);
	}
	
	public Trainer(String name, Pokemon[] team, Item[] items, int money, int index) {
		this(name, team, items, money, null, index);
	}
	
	public Trainer(String name, Pokemon[] team, Item[] items, int money, Item item, int index) {
		this(name, team, money, item, index, true);
		if (team.length != items.length) throw new IllegalArgumentException("Items array must be same length as team array");
		for (int i = 0; i < team.length; i++) {
			team[i].item = items[i];
			team[i].slot = i;
		}
		this.teamItems = items;
	}

	public Trainer(String name, Pokemon[] team, int money, Item item) {
		this(name, team, money, item, 0, true);
	}
	
	public Trainer(String name, Pokemon[] team, int money, int index) {
		this(name, team, money, null, index, true);
	}
	
	public Trainer(String name, Pokemon[] team, int money, Item item, int index, boolean setTrainers) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		this.flagIndex = index;
		current = team[0];
		
		if (setTrainers) {
			for (Pokemon p : team) {
				p.setTrainer(this);
			}
		}
		
		this.effects = new ArrayList<>();
	}
	
	public Trainer(boolean player) {
		if (player) {
			name = "[SELECT]";
			team = new Pokemon[6];
			this.money = 100;
		}
	}

	public Pokemon[] getTeam() {
		return team;
	}
	
	public Item getItem() {
		return item;
	}
	
	/**
	 * Should only be used for testing whether or not a trainer has a flagIndex (!= 0)
	 * 
	 */
	public int getFlagIndex() {
		return flagIndex;
	}
	
	public int getFlagX() {
		return (flagIndex >> 5) & 0xF;
	}
	
	public int getFlagY() {
		return flagIndex & 0x1F;
	}
	
	public void setMoney(int amt) {
		this.money = amt;
		if (money < 0) money = 0;
	}
	
	@Override // implementation
	public String toString() {
		return getName().replaceAll("\\s*\\d+$", "");
	}

	public boolean hasNext() {
		return getNumFainted() < team.length;
	}
	
	public Pokemon next(Pokemon other, boolean userSide) {
		current = getNext2(other);
		return current;
	}
	
	private Pokemon getNext2(Pokemon other) {
		int bestScore = Integer.MIN_VALUE;
		Pokemon next = null;
		for (Pokemon p : team) {
			if (!p.isFainted() && p != current) {
				int score = p.scorePokemon(other, null, false, 0.0, Pokemon.field);
				if (score > bestScore) {
					bestScore = score;
					next = p;
				}
			}
		}
		return next;
	}
	
	private Pokemon getNext(Pokemon other) {
		Pokemon result = null;
		//Move highestBPMove = null;
		int highestBP = Integer.MIN_VALUE;
		for (Pokemon p : team) {
			if (!p.isFainted() && p != current) {
				//System.out.println("\n" + p.name);
				for (Moveslot m : p.moveset) {
					if (m != null) {
						Move move = m.move;
						int effectiveBP = (int) (move.getbp(p, other, Pokemon.field) * getEffective(other, p, move.mtype, move, false) * ((move.mtype == p.type1 || move.mtype == p.type2) ? 1.5 : 1));
						if (effectiveBP > highestBP) {
							//highestBPMove = move;
							highestBP = effectiveBP;
							result = p;
						}
					}
				}
			}
		}
		return result;
	}

	public Pokemon getCurrent() {
		return current;
	}
	
	public int getMoney() {
		return money;
	}
	
	public boolean swapRandom(Pokemon foe) {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		Pokemon user = Pokemon.gp.gameState == GamePanel.BATTLE_STATE ? Pokemon.gp.battleUI.user :
			Pokemon.gp.gameState == GamePanel.SIM_BATTLE_STATE ? Pokemon.gp.simBattleUI.user : null;
		boolean hasUser = this.hasUser(user);
		
		swap(current, team[index], hasUser, foe);
		foe.removeStatus(Status.TRAPPED);
		foe.removeStatus(Status.SPUN);
		foe.removeStatus(Status.SPELLBIND);
		current.swapIn(foe, true);
		return true;
		
	}
	
	public boolean hasValidMembers() {
		boolean result = false;
		for (int i = 0; i < team.length; i++) {
			if (this.team[i] != null) {
				if (this.team[i] != current && !this.team[i].isFainted()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	public boolean wiped() {
		boolean result = true;
		for (int i = 0; i < team.length; i++) {
			if (this.team[i] != null && !this.team[i].isFainted()) {
				result = false;
				break;
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public int getNumFainted() {
		int result = 0;
		for (int i = 0; i < team.length; i++) {
			if (team[i] != null) {
				if (team[i].isFainted()) result++;
			}
		}
		return result;
	}

	public Pokemon getSwap(Pokemon foe, Move m) {
		PType type = null;
		if (m != Move.SPLASH && m != null) {
			type = m.mtype;
		} else if (foe.lastMoveUsed != null) {
			type = foe.lastMoveUsed.mtype;
		}
		if (m == Move.GROWL || type == null) {
			return getNext(foe);
		} else if (foe.lastMoveUsed != null || m == Move.SPLASH || hasResist(foe, type, m)) {
			Pokemon resist = getBestResist(foe, type, m);
			if (resist != current) return resist;
		} else {
			return getNext(foe);
		}
		return getNext(foe);
	}
	
	public Pokemon getBestResist(Pokemon foe, PType type, Move m) {
		Pokemon bestResist = null;
		double bestMultiplier = 1;
		
		for (Pokemon p : team) {
			if (p != current && !p.isFainted()) {
				double multiplier = getEffective(p, foe, type, m, false);
				if (multiplier < bestMultiplier) {
					bestMultiplier = multiplier;
					bestResist = p;
				}
				if (multiplier == 0.0) break;
			}
		}
		
		return bestResist == null ? current : bestResist;
	}
	
	public Pokemon swapOut(Pokemon foe, Move m, boolean baton, boolean userSide) {
		Pokemon result = getSwap(foe, m);
		if (result != current) {
			int[] oldStats = current.statStages.clone();
			ArrayList<StatusEffect> oldVStatuses = new ArrayList<>(current.vStatuses);
			int perishCount = current.perishCount;
			swap(current, result, userSide, foe);
			if (baton) {
				result.statStages = oldStats;
				result.vStatuses = oldVStatuses;
				result.removeStatus(Status.SWITCHING);
				result.removeStatus(Status.SWAP);
				result.perishCount = perishCount;
			}
			result.swapIn(foe, true);
			foe.removeStatus(Status.TRAPPED);
			foe.removeStatus(Status.SPUN);
			foe.removeStatus(Status.SPELLBIND);
		}
		return result;
	}
	
	public Pokemon swapOut2(Pokemon foe, int slot, Move m, boolean baton, boolean userSide) {
		Pokemon result = team[slot];
		if (result != current) {
			int[] oldStats = current.statStages.clone();
			ArrayList<StatusEffect> oldVStatuses = new ArrayList<>(current.vStatuses);
			int perishCount = current.perishCount;
			swap(current, result, userSide, foe);
			if (baton) {
				result.statStages = oldStats;
				result.vStatuses = oldVStatuses;
				result.removeStatus(Status.SWITCHING);
				result.removeStatus(Status.SWAP);
				result.perishCount = perishCount;
			}
			result.swapIn(foe, true);
			foe.removeStatus(Status.TRAPPED);
			foe.removeStatus(Status.SPUN);
			foe.removeStatus(Status.SPELLBIND);
		}
		return result;
	}
	
	public boolean hasResist(Pokemon foe, PType type, Move m) {
		for (Pokemon p : team) {
			if (p != current && resists(p, foe, type, m)) return true;
		}
		return false;
	}
	
	public static double getEffective(Pokemon p, Pokemon foe, PType type, Move m, boolean onlyCheckAbility) {
		double multiplier = 1;
		if (m == Move.HIDDEN_POWER || m == Move.RETURN) {
			type = foe.determineHPType();
		}
		if (type == PType.NORMAL) {
			if (foe.getAbility(Pokemon.field) == Ability.GALVANIZE) type = PType.ELECTRIC;
			if (foe.getAbility(Pokemon.field) == Ability.REFRIGERATE) type = PType.ICE;
			if (foe.getAbility(Pokemon.field) == Ability.PIXILATE) type = PType.LIGHT;
		}
		if (foe.getAbility(Pokemon.field) == Ability.NORMALIZE) type = PType.NORMAL;
		if (!onlyCheckAbility) multiplier = p.getEffectiveMultiplier(type, m, foe);
		Ability pAbility = p.getAbility(Pokemon.field);
		if (foe.getAbility(Pokemon.field) == Ability.MOLD_BREAKER) pAbility = Ability.NULL;
		if (foe.getAbility(Pokemon.field) == Ability.TINTED_LENS && multiplier < 1) multiplier *= 2;
		if (pAbility == Ability.DRY_SKIN && type == PType.WATER) multiplier = 0;
		if (pAbility == Ability.BLACK_HOLE && (type == PType.LIGHT || type == PType.GALACTIC)) multiplier = 0;
		if (pAbility == Ability.ILLUMINATION && (type == PType.GHOST || type == PType.DARK || type == PType.LIGHT || type == PType.GALACTIC)) multiplier *= 0.5;
		if (pAbility == Ability.UNERODIBLE && (type == PType.GRASS || type == PType.WATER || type == PType.GROUND)) multiplier *= 0.25;
		if (pAbility == Ability.FLASH_FIRE && type == PType.FIRE) multiplier = 0;
		if (pAbility == Ability.FRIENDLY_GHOST && type == PType.GHOST) multiplier = 0;
		if (pAbility == Ability.WARM_HEART && type == PType.ICE) multiplier = 0;
		if (pAbility == Ability.COLD_HEART && type == PType.PSYCHIC) multiplier = 0;
		if (pAbility == Ability.WHITE_HOLE && type == PType.DRAGON) multiplier = 0;
		if (pAbility == Ability.GALACTIC_AURA && (type == PType.ICE || type == PType.PSYCHIC)) multiplier *= 0.5;
		if (pAbility == Ability.UNWAVERING && (type == PType.DARK || type == PType.GHOST)) multiplier *= 0.5;
		if (pAbility == Ability.JUSTIFIED && type == PType.DARK) multiplier *= 0.5;
		if (pAbility == Ability.INSECT_FEEDER && type == PType.BUG) multiplier = 0;
		if (!p.isGrounded(Pokemon.field, pAbility) && type == PType.GROUND) multiplier = 0;
		if (p.getItem(Pokemon.field) == Item.SNOWBALL && type == PType.ICE) multiplier = 0;
		if (p.getItem(Pokemon.field) == Item.ABSORB_BULB && type == PType.WATER) multiplier = 0;
		if (p.getItem(Pokemon.field) == Item.LUMINOUS_MOSS && type == PType.WATER) multiplier = 0;
		if (p.getItem(Pokemon.field) == Item.CELL_BATTERY && type == PType.ELECTRIC) multiplier = 0;
		if (pAbility == Ability.LIGHTNING_ROD && type == PType.ELECTRIC) multiplier = 0;
		if (pAbility == Ability.MOTOR_DRIVE && type == PType.ELECTRIC) multiplier = 0;
		if (pAbility == Ability.SAP_SIPPER && type == PType.GRASS) multiplier = 0;
		if (pAbility == Ability.HEAT_COMPACTION && type == PType.FIRE) multiplier = 0;
		if (pAbility == Ability.THICK_FAT && (type == PType.FIRE || type == PType.ICE)) multiplier *= 0.5;
		if (pAbility == Ability.VOLT_ABSORB && type == PType.ELECTRIC) multiplier = 0;
		if (pAbility == Ability.WATER_ABSORB && type == PType.WATER) multiplier = 0;
		if (pAbility == Ability.MOSAIC_WINGS && multiplier == 1.0) multiplier = 0.5;
		if (pAbility == Ability.WONDER_GUARD && multiplier < 2.0) multiplier = 0;
		if (pAbility == Ability.MYSTIC_ABSORB && type == PType.MAGIC) multiplier = 0;
		if (pAbility == Ability.DJINN1S_FAVOR && type == PType.MAGIC) multiplier = 0;
		
		if (m != null && m.critChance < 0 && multiplier > 0) return 1;
		
		return multiplier;
	}
	
	public boolean resists(Pokemon p, Pokemon foe, PType type, Move m) {
		if (p.isFainted()) return false;
		return getEffective(p, foe, type, m, false) < 1;
	}
	
	private void swap(Pokemon oldP, Pokemon newP, boolean playerSide, Pokemon foe) {
		oldP.clearVolatile(foe);
		Task.addSwapOutTask(oldP, playerSide);
		if (oldP.getAbility(Pokemon.field) == Ability.REGENERATOR && !oldP.isFainted()) {
			oldP.currentHP += current.getStat(0) / 3;
			oldP.verifyHP();
		}
		if (oldP.ability == Ability.ILLUSION) oldP.illusion = true; // just here for calc
		this.current = newP;
		Task.addSwapInTask(newP, playerSide);
		Pokemon.field.switches++;
	}

	public void setCurrent(Pokemon newCurrent) {
		if (newCurrent == null) throw new NullPointerException("New Current cannot be null");
		this.current = newCurrent;
	}
	
	public void setCurrent() {
		setCurrent(team[0]);
	}

	public void heal() {
		for (Pokemon member : team) {
			if (member != null) {
				member.heal();
				member.setVisible(false);
			}
		}
	}
	
	public void setSlots() {
		for (int i = 0; i < team.length; i++) {
			if (team[i] != null) team[i].slot = i;
		}
	}

	public void setSprites() {
		for (Pokemon p : team) {
			if (p != null) p.setSprites();
		}
		
	}
	
	public static Trainer getTrainer(int i) {
		return trainers[i];
	}

	public int indexOf(Pokemon p) {
		for (int i = 0; i < team.length; i++) {
			if (p == team[i]) return i;
		}
		return -1;
	}
	
	public Trainer clone() {		
		Pokemon[] newTeam = new Pokemon[this.team.length];
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i] != null) newTeam[i] = this.team[i].clone();
		}
		Trainer result = new Trainer(this.name, newTeam, this.money, this.item, this.flagIndex, true);
		
		result.update = this.update;
		result.staticEnc = this.staticEnc;
		result.catchable = this.catchable;
		result.effects = new ArrayList<>(this.effects);
		result.cloned = true;
		
		return result;
	}
	
	public Trainer shallowClone(GamePanel gp) {
		Trainer result = new Trainer(this.name, this.team, this.money, this.item, this.flagIndex, false);
		result.update = this.update;
		if (this.effects != null) result.effects = new ArrayList<>(this.effects);
		result.cloned = true;
		
		return result;
	}
	
	public boolean hasUser(Pokemon user) {
		for (Pokemon p : team) {
			if (p == user) return true;
		}
		return false;
	}
	
	public ArrayList<FieldEffect> getFieldEffectList() {
		return this.effects;
	}

	public void initFieldEffectList() {
		this.effects = new ArrayList<>();
	}

	public ArrayList<Pokemon> getOrderedTeam() {
		ArrayList<Pokemon> result = new ArrayList<>();
		result.add(current);
		for (Pokemon p : team) {
			if (p == current || p == null) continue;
			result.add(p);
		}
		
		return result;
	}

	public void setFieldEffects(ArrayList<FieldEffect> fieldEffects) {
		this.effects = fieldEffects;
	}

	public void reset() {
		this.resetTeam();
		this.heal();
		for (int i = 0; i < team.length; i++) {
			this.team[i].item = this.teamItems[i];
		}
	}
	
	public void resetTeam() {
		Pokemon[] teamTemp = Arrays.copyOf(this.team, this.team.length);
		for (int i = 0; i < teamTemp.length; i++) {
			if (teamTemp[i] != null) {
				if (teamTemp[i].id == 237) {
					teamTemp[i].id = 150;
					teamTemp[i].setSprites();
					if (teamTemp[i].nickname.equals(teamTemp[i].name())) teamTemp[i].nickname = teamTemp[i].getName();
					teamTemp[i].setName(teamTemp[i].getName());
					
					teamTemp[i].baseStats = teamTemp[i].getBaseStats();
					teamTemp[i].setStats();
					teamTemp[i].weight = teamTemp[i].getWeight();
					teamTemp[i].setTypes();
					teamTemp[i].setAbility(teamTemp[i].abilitySlot);
					if (teamTemp[i].ability == Ability.ILLUSION) teamTemp[i].illusion = true; // just here for calc
					teamTemp[i].currentHP = teamTemp[i].currentHP > teamTemp[i].getStat(0) ? teamTemp[i].getStat(0) : teamTemp[i].currentHP;
				}
				this.team[teamTemp[i].slot] = teamTemp[i];
				teamTemp[i].clearVolatile(null);
				if (teamTemp[i].status == Status.ASLEEP) teamTemp[i].sleepCounter = (int)(Math.random() * 3) + 1;
				teamTemp[i].vStatuses.clear();
				teamTemp[i].abilityFlag = false;
				
				if (teamTemp[i].loseItem) {
					teamTemp[i].item = null;
					teamTemp[i].loseItem = false;
				}
				if (teamTemp[i].lostItem != null) {
					teamTemp[i].item = teamTemp[i].lostItem;
					teamTemp[i].lostItem = null;
					if (teamTemp[i].item == Item.POTION) teamTemp[i].item = null;
				}
			}
		}
		this.setCurrent(this.team[0]);
		
	}

	public boolean hasPokemonID(int id) {
		for (Pokemon p : team) {
			if (p != null && p.id == id) return true;
		}
		return false;
	}
	
	public static void addLevelCap(int cap) {
		levelCaps[levelCapIndex++] = cap;
	}
	
	public static int getLevelCap(int badges) {
		return levelCaps[badges];
	}
	
	public Pokemon[] getSlotOrderedTeam() {
		Pokemon[] result = new Pokemon[6];
		for (Pokemon p : team) {
			if (p != null) result[p.slot] = p;
		}
		return result;
	}

	public Trainer regeneratePokemon(int id) {
		Pokemon base = this.team[0];
		Pokemon newP = new Pokemon(id, base.level, true);
		newP.slot = 0;
		newP.setStaticIVs(true);
		this.team[0] = newP;
		Trainer result = this.clone();
		result.cloned = false;
		return result;
	}

	public Pokemon findVisiblePokemon() {
		for (Pokemon p : getOrderedTeam()) {
			if (p.isVisible()) return p;
		}
		return null;
	}

	public String getBoostString() {
		return "Gets a +" + boost + " omniboost at the start of battle!";
	}
}
