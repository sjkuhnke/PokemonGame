package Swing;

import java.util.Random;

public class Trainer {
	private String name;
	private Pokemon[] team;
	private int money;
	Item item;
	int flagIndex;
	Pokemon current;
	
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
	}
	
	public Trainer(boolean placeholder) {
		if (placeholder) {
			name = "[SELECT]";
			this.team = null;
			this.money = 0;
		}
	}

	public Pokemon[] getTeam() {
		return team;
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
	
	public boolean swapRandom(Pokemon foe, Player me, boolean announce, boolean baton) {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		if (baton) team[index].statStages = current.statStages;
		Pokemon newCurrent = current;
		
		newCurrent.clearVolatile();
		current = team[index];
		//this.team[currentIndex] = team[index];
		
		if (newCurrent.ability == Ability.REGENERATOR) {
			newCurrent.currentHP += newCurrent.getStat(0) / 3;
			newCurrent.verifyHP();
		}
		
    	Pokemon.console.write(current.nickname, true, 16);
    	if (announce) {
    		Pokemon.console.writeln(" was dragged out!", false, 16);
    	} else {
    		Pokemon.console.writeln(" was sent out!", false, 16);
    	}
		current.swapIn(foe, me, true);
		return true;
		
	}
	
	public boolean swapRandom(Pokemon foe, Player me) {
		return swapRandom(foe, me, true, false);
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

	public Pokemon getSwap(Pokemon foe, Move m, boolean baton) {
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
	
	public Pokemon swapOut(Pokemon foe, Player me, Move m, boolean baton) {
		Pokemon result = getSwap(foe, m, baton);
		if (result != current) {
			int[] oldStats = current.statStages;
			swap(current, result);
			if (baton) result.statStages = oldStats;
			result.swapIn(me.getCurrent(), me, true);
			me.getCurrent().vStatuses.remove(Status.TRAPPED);
			me.getCurrent().vStatuses.remove(Status.SPUN);
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
		if (p.ability == Ability.ILLUMINATION && (type == PType.GHOST || type == PType.DARK || type == PType.LIGHT || type == PType.GALACTIC)) multiplier *= 0.5;
		if (p.ability == Ability.FLASH_FIRE && type == PType.FIRE) multiplier = 0;
		if (p.ability == Ability.FRIENDLY_GHOST && type == PType.GHOST) multiplier = 0;
		if (p.ability == Ability.GALACTIC_AURA && (type == PType.ICE || type == PType.PSYCHIC)) multiplier *= 0.5;
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
	
	public void swap(Pokemon oldP, Pokemon newP) {
		Pokemon.console.writeln("\n" + name + " withdrew " + oldP.nickname + "!", false, 16);
		if (oldP.ability == Ability.REGENERATOR) {
			oldP.currentHP += current.getStat(0) / 3;
			oldP.verifyHP();
		}
		if (oldP.vStatuses.contains(Status.HEALING)) newP.vStatuses.add(Status.HEALING);
		if (oldP.vStatuses.contains(Status.WISH)) newP.vStatuses.add(Status.WISH);
		oldP.clearVolatile();
		this.current = newP;
		Pokemon.console.write(name + " sent out ", false, 16);
		Pokemon.console.write(current.nickname, true, 16);
		Pokemon.console.writeln("!", false, 16);
		if (this.current.vStatuses.contains(Status.HEALING) && this.current.currentHP != this.current.getStat(0)) this.current.heal();
	}
}
