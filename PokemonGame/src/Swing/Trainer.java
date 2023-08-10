package Swing;

public class Trainer {
	private String name;
	private Pokemon[] team;
	private int money;
	int currentIndex;
	Item item;
	int flagIndex;
	
	public Trainer(String name, Pokemon[] team, int money) {
		this.name = name;
		this.team = team;
		this.money = money;
		currentIndex = 0;
	}
	
	public Trainer(String name, Pokemon[] team, int money, Item item) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.item = item;
		currentIndex = 0;
	}
	
	public Trainer(String name, Pokemon[] team, int money, int index) {
		this.name = name;
		this.team = team;
		this.money = money;
		this.flagIndex = index;
		currentIndex = 0;
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
		return team[++currentIndex];
	}
	
	public Pokemon getCurrent() {
		return team[currentIndex];
	}
	
	public int getMoney() {
		return money;
	}
}
