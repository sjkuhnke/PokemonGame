package Swing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import Overworld.GamePanel;
import Overworld.Main;

public class Player implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2851052666892205583L;
	public Pokemon[] team;
	public Pokemon[] box1, box2, box3;
	public int money;
	public Pokemon current;
	private int numBattled;
	private int posX;
	private int posY;
	public Bag bag;
	public int badges;
	public int starter;
	public int[] pokedex = new int[241];
	public int currentMap;
	public boolean[] trainersBeat = new boolean[Main.trainers.length];
	public boolean[][] itemsCollected;
	public boolean[] flags = new boolean[GamePanel.maxFlags];
	public boolean[] locations = new boolean[12]; // NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
	public boolean random = false;
	public boolean ghost = false;
	public int steps;
	public boolean fish;
	public boolean repel;
	public boolean surf;
	public boolean lavasurf;
	public int grustCount;
	
	public Player(GamePanel gp) {
		team = new Pokemon[6];
		box1 = new Pokemon[30];
		box2 = new Pokemon[30];
		box3 = new Pokemon[30];
		money = 100;
		current = null;
		bag = new Bag();
		posX = 90;
		posY = 46;
		
		itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
		locations[0] = true;
		bag.add(Item.CALCULATOR);
	}
	
	public Pokemon getCurrent() {
		return this.current;
	}
	
	public void catchPokemon(Pokemon p) {
	    if (p.isFainted()) return;
	    boolean hasNull = false;
	    p.setNickname();
	    pokedex[p.id] = 2;
	    p.clearVolatile();
	    for (int i = 0; i < team.length; i++) {
	        if (team[i] == null) {
	            hasNull = true;
	            break;
	        }
	    }
	    if (hasNull) {
	        for (int i = 0; i < team.length; i++) {
	            if (team[i] == null) {
	                team[i] = p;
	                p.slot = i;
                	Pokemon.console.write("Caught ", false, 16);
                	Pokemon.console.write(p.nickname, true, 16);
                	Pokemon.console.writeln(", added to party!", false, 16);
	                current = team[0];
	                break;
	            }
	        }
	    } else {
	    	p.heal();
	        int index = -1;
	        Pokemon[][] boxes = {box1, box2, box3};  // Array of box references
	        for (int i = 0; i < boxes.length; i++) {
	            for (int j = 0; j < boxes[i].length; j++) {
	                if (boxes[i][j] == null) {
	                    index = j;
	                    break;
	                }
	            }
	            if (index >= 0) {
	                boxes[i][index] = p;
                	Pokemon.console.write("Caught ", false, 16);
                	Pokemon.console.write(p.nickname, true, 16);
                	Pokemon.console.writeln(", sent to box " + (i+1) + "!", false, 16);
	                return;  // Exit the method after catching the Pokemon
	            }
	        }
        	Pokemon.console.write("Cannot catch ", false, 16);
        	Pokemon.console.write(p.nickname, true, 16);
        	Pokemon.console.writeln(", all boxes are full.", false, 16);
	    }
	}


	public void swap(Pokemon pokemon, int index) {
		Pokemon.console.writeln("\n" + current.nickname + ", come back!", false, 16);
		if (current.ability == Ability.REGENERATOR) {
			current.currentHP += current.getStat(0) / 3;
			current.verifyHP();
		}
		Pokemon lead = current;
		if (lead.vStatuses.contains(Status.HEALING)) team[index].vStatuses.add(Status.HEALING);
		if (lead.vStatuses.contains(Status.WISH)) team[index].vStatuses.add(Status.WISH);
		lead.clearVolatile();
		this.current = pokemon;
		this.team[0] = pokemon;
		this.team[index] = lead;
		if (!this.current.battled) {
			numBattled++;
			this.current.battled = true;
		}
		Pokemon.console.write("Go ", false, 16);
		Pokemon.console.write(current.nickname, true, 16);
		Pokemon.console.writeln("!", false, 16);
		if (this.current.vStatuses.contains(Status.HEALING) && this.current.currentHP != this.current.getStat(0)) this.current.heal();
		
	}
	
	public int getBattled() {
		return numBattled;
	}
	
	public void setBattled(int battled) {
		numBattled = battled;
	}
	
	public void clearBattled() {
		for (Pokemon p : team) {
			if (p != null) p.battled = false;
		}
		numBattled = 1;
	}

	public Pokemon[] getTeam() {
		return team;
	}

	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int x) {
		posX = x;
	}

	public int getPosY() {
		return posY;
	}
	
	public void setPosY(int y) {
		posY = y;
	}
	
	public Bag getBag() {
		return bag;
	}
	
	public boolean elevate(Pokemon pokemon) {
		boolean result = false;
		int expAmt = pokemon.expMax - pokemon.exp;
    	pokemon.exp += expAmt;
    	while (pokemon.exp >= pokemon.expMax) {
    		if (pokemon.happiness < 255 && pokemon.happinessCap > 2) pokemon.awardHappiness(-3, false);
            // Pokemon has leveled up, check for evolution
            Pokemon evolved = pokemon.levelUp(this);
            if (evolved != null && evolved != pokemon) {
                // Update the player's team with the evolved Pokemon
            	int index = Arrays.asList(this.getTeam()).indexOf(pokemon);
                this.team[index] = evolved;
                if (index == 0) this.current = evolved;
                evolved.checkMove();
                pokemon = evolved;
                result = true;
            }
        }
    	pokemon.fainted = false;
    	JOptionPane.showMessageDialog(null, pokemon.nickname + " was elevated to " + pokemon.getLevel());
    	return result;
		
	}
	
	public boolean buy(Item item) {
		if (item.getCost() > money) return false;
		money -= item.getCost();
		bag.add(item);
		return true;
	}
	
	public boolean buy(Item item, int amt) {
		if (item.getCost() * amt > money) return false;
		money -= item.getCost() * amt;
		bag.add(item, amt);
		return true;
	}

	public boolean hasValidMembers() {
		boolean result = false;
		for (int i = 0; i < 6; i++) {
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
		for (int i = 0; i < 6; i++) {
			if (this.team[i] != null && !this.team[i].isFainted()) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean hasMove(Move m) {
		boolean result = false;
		if (m == Move.CUT && badges < 1) return false;
		if (m == Move.ROCK_SMASH && badges < 2) return false;
		if (m == Move.VINE_CROSS && badges < 3) return false;
		if (m == Move.SURF && badges < 4) return false;
		if (m == Move.SLOW_FALL && badges < 5) return false;
		if (m == Move.WHIRLPOOL && badges < 6) return false;
		if (m == Move.ROCK_CLIMB && badges < 7) return false;
		if (m == Move.LAVA_SURF && badges < 8) return false;
		for (Pokemon p : team) {
			if (p != null) {
				if (p.knowsMove(m)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	public boolean swapRandom(Pokemon foe) {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		
		swap(team[index], index);
		
		Pokemon.console.write(current.nickname, true, 16);
		Pokemon.console.writeln(" was dragged out!", false, 16);
		current.swapIn(foe, this);
		return true;
		
	}
	
	public void updateFlags() {
		boolean[] tempFlag = flags.clone();
		flags = new boolean[GamePanel.maxFlags];
		for (int i = 0; i < tempFlag.length; i++) {
			flags[i] = tempFlag[i];
		}
	}
	
	public void updateTrainers() {
		boolean[] temp = trainersBeat.clone();
		trainersBeat = new boolean[Main.trainers.length];
		for (int i = 0; i < temp.length; i++) {
			trainersBeat[i] = temp[i];
		}
	}

	public void updateItems(int x, int y) {
		boolean[][] tempObj = itemsCollected.clone();
		itemsCollected = new boolean[x][y];
		for (int i = 0; i < tempObj.length; i++) {
			for (int j = 0; j < tempObj[1].length; j++) {
				itemsCollected[i][j] = tempObj[i][j];
			}
		}
	}

	public void updateHappinessCaps() {
		for (Pokemon p : team) {
			if (p != null) p.happinessCap = 50;
		}
		for (Pokemon p : box1) {
			if (p != null) p.happinessCap = 50;
		}
		for (Pokemon p : box2) {
			if (p != null) p.happinessCap = 50;
		}
		for (Pokemon p : box3) {
			if (p != null) p.happinessCap = 50;
		}
		
	}

}
