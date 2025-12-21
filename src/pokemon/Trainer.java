package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import overworld.GamePanel;
import pokemon.Field.FieldEffect;
import ui.BattleUI;
import util.*;

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
	
	public Trainer(boolean player, String name) {
		if (player) {
			this.name = name;
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
		StringBuilder sb = new StringBuilder("______________\n");
		sb.append("FREE SWITCH\n");
		sb.append("______________\n");
		for (Pokemon p : team) {
			if (!p.isFainted() && p != current) {
				int score = p.scorePokemon(other, null, new Pair<>(0, 0.0), false, Pokemon.field, null, false);
				sb.append("[" + p + ": " + score + "], ");
				if (score > bestScore) {
					bestScore = score;
					next = p;
				}
			}
		}
		System.out.println(sb.toString() + "\n");
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
		boolean oldCloned = current.cloned;
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		Pokemon user = Pokemon.gp != null ? Pokemon.gp.gameState == GamePanel.BATTLE_STATE ? Pokemon.gp.battleUI.user :
			Pokemon.gp.gameState == GamePanel.SIM_BATTLE_STATE ? Pokemon.gp.simBattleUI.user : null : null;
		boolean hasUser = this.hasUser(user);
		
		swap(current, team[index], hasUser, foe);
		foe.removeStatus(Status.TRAPPED);
		foe.removeStatus(Status.SPUN);
		foe.removeStatus(Status.SPELLBIND);
		if (!oldCloned) current.swapIn(foe, true);
		return true;
		
	}
	
	public boolean hasValidMembers() {
		return hasValidMembers(current);
	}
	
	public boolean hasValidMembers(Pokemon current) {
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
	
	public boolean canSwitch(Pokemon foe) {
		return canSwitch(foe, current);
	}
	
	public boolean canSwitch(Pokemon foe, Pokemon current) {
		return hasValidMembers(current) && !current.isTrapped(foe);
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
	
	// only for use in the manage menu on the title screen to rename a player's save
	public void setName(String name) {
		this.name = name;
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
	
	public Pokemon swapOut2(Pokemon foe, int slot, boolean baton, boolean userSide) {
		Pokemon result = slot == BattleUI.FREE_SWITCH || slot == BattleUI.NORMAL_SWITCH ? getNext2(foe) : team[Math.abs(slot) - 1];
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
		if (!oldP.cloned) Pokemon.field.switches++;
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
	
	/**
	 * Usage: for SimBattle battle betting and for after catching a Legendary
	 * and the game needs to generate a new one when you summon another of the
	 * same species (and therefore same Trainer).
	 * <br><br>
	 * <b>NOTE:</b> The new team Pokemon will be cloned copies of the originals, but
	 * will not have the {@code cloned} tag like usual cloned Pokemon will (explicitly set).
	 * This is because the SimBattle needs to calculate how many switches
	 * would happen on average, and {@code cloned} Pokemon don't count for counting switches
	 * since the AI uses {@code cloned} Pokemon to make decisions with.
	 * 
	 * @return a cloned Trainer with a deep cloned team of Pokemon and a deep cloned {@code effects} ArrayList
	 */
	public Trainer clone() {
		Pokemon[] newTeam = new Pokemon[this.team.length];
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i] != null) {
				newTeam[i] = this.team[i].clone();
				newTeam[i].cloned = false;
			}
		}
		Trainer result = new Trainer(this.name, newTeam, this.money, this.item, this.flagIndex, true);
		
		result.update = this.update;
		result.staticEnc = this.staticEnc;
		result.catchable = this.catchable;
		result.effects = DeepClonable.deepCloneList(this.effects);
		result.cloned = true;
		
		return result;
	}
	
	public Trainer shallowClone() {
		Trainer result = new Trainer(this.name, this.team, this.money, this.item, this.flagIndex, false);
		result.update = this.update;
		if (this.effects != null) result.effects = DeepClonable.deepCloneList(this.effects);
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
				teamTemp[i].reset();
				this.team[teamTemp[i].slot] = teamTemp[i];
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
	
	public static int getLevelCap(int badges, Player player) {
		return Math.min(levelCaps[badges] + player.levelCapBonus, 100);
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

	public String getBattleName() {
		return toString();
	}

	public Pokemon pickLead(Pokemon foe) {
	    if (!foe.playerOwned()) return current;
	    
	    // Step 1: Predict which Pokemon the player is likely to lead with
	    HashMap<Pokemon, Integer> playerLeadScores = predictPlayerLeads(foe);
	    
	    // Step 2: Score each of our Pokemon against the likely player leads
	    HashMap<Pokemon, Double> aiPokemonScores = new HashMap<>();
	    
	    for (int i = 0; i < team.length; i++) {
	        Pokemon ally = team[i];
	        if (ally == null) continue;
	        
	        double totalWeightedScore = 0.0;
	        double totalWeight = 0.0;
	        
	        // Score against each potential player lead, weighted by likelihood
	        for (Map.Entry<Pokemon, Integer> entry : playerLeadScores.entrySet()) {
	            Pokemon playerMon = entry.getKey();
	            int likelihood = entry.getValue();
	            
	            if (likelihood > 0) {
	                int matchupScore = ally.scorePokemon(playerMon, null, new Pair<>(0, 0.0), 
	                                                     false, Pokemon.field, null, false);
	                totalWeightedScore += matchupScore * likelihood;
	                totalWeight += likelihood;
	            }
	        }
	        
	        double averageScore = (totalWeight > 0) ? totalWeightedScore / totalWeight : 0.0;
	        aiPokemonScores.put(ally, averageScore);
	        
	        System.out.println(ally.name + " weighted score vs predicted leads: " + 
	                         String.format("%.1f", averageScore));
	    }
	    
	    // Step 3: Use weighted random selection based on scores
	    Pokemon chosen = weightedRandomSelection(aiPokemonScores);
	    
	    current = chosen;
	    System.out.println(current.name + " chosen as lead");
	    return current;
	}

	/**
	 * Predicts which Pokemon the player is most likely to lead with by scoring
	 * each player Pokemon against our team (simulating what they might think).
	 */
	private HashMap<Pokemon, Integer> predictPlayerLeads(Pokemon foe) {
	    HashMap<Pokemon, Integer> playerScores = new HashMap<>();
	    
	    System.out.println("\n=== Predicting Player Leads ===");
	    
	    for (int i = 0; i < foe.trainer.team.length; i++) {
	        Pokemon playerMon = foe.trainer.team[i];
	        if (playerMon == null) continue;
	        
	        int totalScore = 0;
	        int count = 0;
	        
	        // Score this player Pokemon against each of our Pokemon
	        for (int j = 0; j < team.length; j++) {
	            Pokemon ourMon = team[j];
	            if (ourMon != null) {
	                int score = playerMon.scorePokemon(ourMon, null, new Pair<>(0, 0.0), 
	                                                   false, Pokemon.field, null, false);
	                totalScore += score;
	                count++;
	            }
	        }
	        
	        int avgScore = (count > 0) ? totalScore / count : 0;
	        playerScores.put(playerMon, avgScore);
	        
	        System.out.println("  " + playerMon.name + " avg score vs our team: " + avgScore);
	    }
	    
	    return playerScores;
	}

	/**
	 * Selects a Pokemon using weighted random selection where higher scores
	 * have higher probability of being chosen, but any positive-scoring Pokemon
	 * can be selected.
	 */
	private Pokemon weightedRandomSelection(HashMap<Pokemon, Double> scores) {
	    // Calculate weights (convert scores to positive weights)
	    HashMap<Pokemon, Double> weights = new HashMap<>();
	    double totalWeight = 0.0;
	    
	    // Find the minimum score to shift all scores positive if needed
	    double minScore = Double.MAX_VALUE;
	    for (double score : scores.values()) {
	        if (score < minScore) minScore = score;
	    }
	    
	    // Shift scores to be positive and square them to emphasize differences
	    double shift = (minScore < 0) ? Math.abs(minScore) + 1 : 0;
	    
	    for (Map.Entry<Pokemon, Double> entry : scores.entrySet()) {
	        double adjustedScore = entry.getValue() + shift;
	        // Square the score to make better options much more likely
	        // But add a small base weight so even weak options have a tiny chance
	        double weight = Math.pow(adjustedScore, 2) + 10.0;
	        weights.put(entry.getKey(), weight);
	        totalWeight += weight;
	    }
	    
	    // Debug output
	    System.out.println("\n=== Lead Selection Probabilities ===");
	    for (Map.Entry<Pokemon, Double> entry : weights.entrySet()) {
	        double probability = (entry.getValue() / totalWeight) * 100.0;
	        System.out.printf("%s: %.1f%%\n", entry.getKey().name, probability);
	    }
	    
	    // Weighted random selection
	    double r = Math.random() * totalWeight;
	    for (Map.Entry<Pokemon, Double> entry : weights.entrySet()) {
	        r -= entry.getValue();
	        if (r <= 0) {
	            return entry.getKey();
	        }
	    }
	    
	    // Fallback: return the highest scoring Pokemon
	    Pokemon best = null;
	    double bestScore = Double.NEGATIVE_INFINITY;
	    for (Map.Entry<Pokemon, Double> entry : scores.entrySet()) {
	        if (entry.getValue() > bestScore) {
	            bestScore = entry.getValue();
	            best = entry.getKey();
	        }
	    }
	    
	    return best != null ? best : current;
	}
}
