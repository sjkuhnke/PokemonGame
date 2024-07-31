package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
	Pokemon current;
	
	public static final int MAX_TRAINERS = 400;
	public static Trainer[] trainers = new Trainer[MAX_TRAINERS];
	
	public static ArrayList<Trainer> bossTrainers = new ArrayList<>();
	public static ArrayList<String> bosses = new ArrayList<String>(Arrays.asList("Scott", "Fred", "Rick", "Leader", "Maxwell", "Arthra", "Robin", "Stanford", "Millie", "Glacius", "Mindy", "Rayna", "Merlin", "Nova"));
	
	public Trainer(String name, Pokemon[] team, int money) {
		this(name, team, money, null, 0);
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
		this(name, team, money, item, index);
		if (team.length != items.length) throw new IllegalArgumentException("Items array must be same length as team array");
		for (int i = 0; i < team.length; i++) {
			team[i].item = items[i];
		}
	}

	public Trainer(String name, Pokemon[] team, int money, Item item) {
		this(name, team, money, item, 0);
	}
	
	public Trainer(String name, Pokemon[] team, int money, int index) {
		this(name, team, money, null, index);
	}
	
	public Trainer(String name, Pokemon[] team, int money, Item item, int index) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		this.flagIndex = index;
		current = team[0];
		
		for (Pokemon p : team) {
			p.setTrainer(this);
		}
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
	
	public int getFlagIndex() {
		return flagIndex;
	}
	
	public void setMoney(int amt) {
		this.money = amt;
		if (money < 0) money = 0;
	}
	
	@Override // implementation
	public String toString() {
		if (getName() != "[SELECT]") {
			if (!getName().contains("Leader") && !getName().contains("Rival")) return getName() + " trainer";
			return getName();
		} else {
			return getName();
		}
	}

	public boolean hasNext() {
		return getNumFainted() < team.length;
	}
	
	public Pokemon next(Pokemon other) {
		current = getNext(other);
		return current;
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
						int effectiveBP = (int) (move.getbp(p, other) * other.getEffectiveMultiplier(move.mtype) * ((move.mtype == p.type1 || move.mtype == p.type2) ? 1.5 : 1));
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
	
	public boolean swapRandom(Pokemon foe, Player me) {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		swap(current, team[index]);
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
		} else if (foe.lastMoveUsed != null || m == Move.SPLASH || hasResist(type)) {
			for (Pokemon p : team) {
				if (p != current && resists(p, type)) {
					return p;
				}
			}
		} else {
			return getNext(foe);
		}
		return getNext(foe);
	}
	
	public Pokemon swapOut(Pokemon foe, Move m, boolean baton) {
		Pokemon result = getSwap(foe, m);
		if (result != current) {
			int[] oldStats = current.statStages.clone();
			ArrayList<Status> oldVStatuses = new ArrayList<>(current.vStatuses);
			int perishCount = current.perishCount;
			int magCount = current.magCount;
			swap(current, result);
			if (baton) {
				result.statStages = oldStats;
				result.vStatuses = oldVStatuses;
				result.vStatuses.remove(Status.SWITCHING);
				result.vStatuses.remove(Status.SWAP);
				result.perishCount = perishCount;
				result.magCount = magCount;
			}
			Pokemon.gp.battleUI.foeStatus = result.status;
			result.swapIn(foe, true);
			foe.vStatuses.remove(Status.TRAPPED);
			foe.vStatuses.remove(Status.SPUN);
		}
		return result;
	}
	
	public boolean hasResist(PType type) {
		for (Pokemon p : team) {
			if (p != current && resists(p, type)) return true;
		}
		return false;
	}
	
	public boolean resists(Pokemon p, PType type) {
		if (p.isFainted()) return false;
		double multiplier = p.getEffectiveMultiplier(type);
		if (p.ability == Ability.DRY_SKIN && type == PType.WATER) multiplier = 0;
		if (p.ability == Ability.BLACK_HOLE && (type == PType.DARK || type == PType.LIGHT || type == PType.GALACTIC)) multiplier = 0;
		if (p.ability == Ability.ILLUMINATION && (type == PType.GHOST || type == PType.DARK || type == PType.LIGHT || type == PType.GALACTIC)) multiplier *= 0.5;
		if (p.ability == Ability.FLASH_FIRE && type == PType.FIRE) multiplier = 0;
		if (p.ability == Ability.FRIENDLY_GHOST && type == PType.GHOST) multiplier = 0;
		if (p.ability == Ability.GALACTIC_AURA && (type == PType.ICE || type == PType.PSYCHIC)) multiplier *= 0.5;
		if (p.ability == Ability.UNWAVERING && (type == PType.DARK || type == PType.GHOST)) multiplier *= 0.5;
		if (p.ability == Ability.INSECT_FEEDER && type == PType.BUG) multiplier = 0;
		if (p.ability == Ability.LEVITATE && type == PType.GROUND) multiplier = 0;
		if (p.ability == Ability.LIGHTNING_ROD && type == PType.ELECTRIC) multiplier = 0;
		if (p.ability == Ability.MOTOR_DRIVE && type == PType.ELECTRIC) multiplier = 0;
		if (p.ability == Ability.SAP_SIPPER && type == PType.GRASS) multiplier = 0;
		if (p.ability == Ability.THICK_FAT && (type == PType.FIRE || type == PType.ICE)) multiplier *= 0.5;
		if (p.ability == Ability.VOLT_ABSORB && type == PType.ELECTRIC) multiplier = 0;
		if (p.ability == Ability.WATER_ABSORB && type == PType.WATER) multiplier = 0;
		if (p.ability == Ability.WONDER_GUARD && multiplier < 2.0) multiplier = 0;
		
		return multiplier < 1;
	}
	
	private void swap(Pokemon oldP, Pokemon newP) {
		Pokemon.addSwapOutTask(oldP);
		if (oldP.ability == Ability.REGENERATOR && !oldP.isFainted()) {
			oldP.currentHP += current.getStat(0) / 3;
			oldP.verifyHP();
		}
		if (oldP.vStatuses.contains(Status.HEALING)) newP.vStatuses.add(Status.HEALING);
		if (oldP.vStatuses.contains(Status.WISH)) newP.vStatuses.add(Status.WISH);
		oldP.clearVolatile();
		this.current = newP;
		Pokemon.addSwapInTask(newP, newP.currentHP);
		if (this.current.vStatuses.contains(Status.HEALING) && this.current.currentHP != this.current.getStat(0)) this.current.heal();
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
}
