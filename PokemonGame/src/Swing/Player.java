package Swing;

import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JOptionPane;

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
	public int[] pokedex = new int[239];
	public int currentMap;
	public boolean[] trainersBeat = new boolean[Main.trainers.length];
	public boolean[] flags = new boolean[10];
	public boolean random = false;
	public boolean ghost = false;
	public int steps;
	
	public Player() {
		team = new Pokemon[6];
		box1 = new Pokemon[30];
		box2 = new Pokemon[30];
		box3 = new Pokemon[30];
		money = 0;
		current = null;
		bag = new Bag();
		posX = 90;
		posY = 46;
	}
	
	public Pokemon getCurrent() {
		return this.current;
	}
	
	public void catchPokemon(Pokemon p) {
	    if (p.isFainted()) return;
	    boolean hasNull = false;
	    p.nickname = JOptionPane.showInputDialog(null, "Would you like to nickname " + p.name + "?");
	    if (p.nickname == null || p.nickname.isBlank()) p.nickname = p.name;
	    while (p.nickname.length() > 12) {
	    	JOptionPane.showMessageDialog(null, "Nickname must be no greater than 12 characters.");
	    	p.nickname = JOptionPane.showInputDialog(null, "Would you like to nickname " + p.name + "?");
	    	if (p.nickname == null || p.nickname.isBlank()) p.nickname = p.name;
	    }
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
	                System.out.println("Caught " + p.nickname + ", added to party!");
	                current = team[0];
	                break;
	            }
	        }
	    } else {
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
	                System.out.println("Caught " + p.nickname + ", sent to box " + (i+1) + "!");
	                return;  // Exit the method after catching the Pokémon
	            }
	        }
	        System.out.println("Cannot catch " + p.nickname + ", all boxes are full");
	    }
	}


	public void swap(Pokemon pokemon, int index) {
		System.out.println("\n" + current.nickname + ", come back!");
		Pokemon lead = current;
		lead.clearVolatile();
		this.current = pokemon;
		this.team[0] = pokemon;
		this.team[index] = lead;
		if (!this.current.battled) {
			numBattled++;
			this.current.battled = true;
		}
		System.out.println("Go " + current.nickname + "!");
		
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
	
	public void elevate(Pokemon pokemon) {
		int expAmt = pokemon.expMax - pokemon.exp;
    	pokemon.exp += expAmt;
    	while (pokemon.exp >= pokemon.expMax) {
            // Pokemon has leveled up, check for evolution
            Pokemon evolved = pokemon.levelUp(this);
            if (evolved != null) {
                // Update the player's team with the evolved Pokemon
            	int index = Arrays.asList(this.getTeam()).indexOf(pokemon);
                this.team[index] = evolved;
                if (index == 0) this.current = evolved;
                evolved.checkMove();
                pokemon = evolved;
            }
        }
    	pokemon.fainted = false;
    	JOptionPane.showMessageDialog(null, pokemon.nickname + " was elevated to " + pokemon.getLevel());
		
	}
	
	public boolean buy(Item item) {
		if (item.getCost() > money) return false;
		money -= item.getCost();
		bag.add(item);
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

}
