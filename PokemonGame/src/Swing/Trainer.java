package Swing;

import java.util.Random;

public class Trainer {
	private String name;
	private Pokemon[] team;
	private int money;
	int currentIndex;
	Item item;
	int flagIndex;
	Pokemon current;
	
	public Trainer(String name, Pokemon[] team, int money) {
		this.name = name;
		this.team = team;
		this.money = money;
		currentIndex = 0;
		current = team[0];
	}

	public Trainer(String name, Pokemon[] team, int money, Item item) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		currentIndex = 0;
		current = team[0];
	}
	
	public Trainer(String name, Pokemon[] team, int money, int index) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.flagIndex = index;
		currentIndex = 0;
		current = team[0];
	}
	
	public Trainer(String name, Pokemon[] team, int money, Item item, int index) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		this.flagIndex = index;
		currentIndex = 0;
		current = team[0];
	}
	
	public Trainer(boolean placeholder) {
		if (placeholder) {
			name = "[SELECT]";
			this.team = null;
			this.money = 0;
			currentIndex = 0;
		}
	}

	public Pokemon[] getTeam() {
		return team;
	}
	
	@Override // implementation
	public String toString() {
		if (name != "[SELECT]") {
			if (!name.contains("Leader") && !name.contains("Rival")) return name + " trainer";
			return name;
		} else {
			return name;
		}
	}

	public boolean hasNext() {
		return currentIndex < team.length - 1;
	}
	
	public Pokemon next() {
		Pokemon result = team[++currentIndex];
		while (result == null) {
			result = team[++currentIndex];
		}
		current = result;
		return result;
	}
	
	public Pokemon getCurrent() {
		return current;
	}
	
	public int getMoney() {
		return money;
	}
	
	public boolean swapRandom() {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		Pokemon newCurrent = current;
		
		newCurrent.clearVolatile();
		current = team[index];
		this.team[currentIndex] = team[index];
		this.team[index] = newCurrent;
		
		System.out.println(current.nickname + " was dragged out!");
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
}
