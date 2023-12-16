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
		this.name = name;
		this.team = team;
		this.money = money;
		current = team[0];
	}

	public Trainer(String name, Pokemon[] team, int money, Item item) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		current = team[0];
	}
	
	public Trainer(String name, Pokemon[] team, int money, int index) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.flagIndex = index;
		current = team[0];
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
		Move highestBPMove = null;
		int highestBP = Integer.MIN_VALUE;
		for (Pokemon p : team) {
			if (!p.isFainted()) {
				System.out.println("\n" + p.name);
				for (Moveslot m : p.moveset) {
					if (m != null) {
						Move move = m.move;
						int effectiveBP = (int) (move.getbp(p, other) * other.getEffectiveMultiplier(move.mtype) * ((move.mtype == p.type1 || move.mtype == p.type2) ? 1.5 : 1));
						if (effectiveBP > highestBP) {
							highestBPMove = move;
							highestBP = effectiveBP;
							result = p;
						}
						System.out.println(move + ": " + effectiveBP);
					}
				}
			}
		}
		System.out.println("----------------\n" + result.name + ": " + highestBPMove + "\n----------------");
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
		current.swapIn(foe, me);
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
}
