package Swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import Overworld.GamePanel;
import Overworld.Main;

public class Player extends Trainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2851052666892205583L;
	
	public Pokemon[] box1, box2, box3;
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
	public boolean[] flags = new boolean[GamePanel.MAX_FLAGS];
	public boolean[] locations = new boolean[12]; // NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
	public boolean random = false;
	public boolean ghost = false;
	public int steps;
	public boolean fish;
	public boolean repel;
	public boolean surf;
	public boolean lavasurf;
	public int grustCount;
	public int scottItem;
	public Item[] resistBerries;
	public int secondStarter;
	public Item choiceChoice;
	public boolean copyBattle;
	public int coins;
	public int gamesWon;
	public int winStreak;
	
	public Player(GamePanel gp) {
		super(true);
		box1 = new Pokemon[30];
		box2 = new Pokemon[30];
		box3 = new Pokemon[30];

		bag = new Bag();
		posX = 79;
		posY = 46;
		
		itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
		locations[0] = true;
		bag.add(Item.CALCULATOR);
	}
	
	public void catchPokemon(Pokemon p) {
	    if (p.isFainted()) return;
	    boolean hasNull = false;
	    p.setNickname();
	    pokedex[p.id] = 2;
	    p.clearVolatile();
	    p.consumeItem();
	    p.trainer = this;
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
                evolved.checkMove(this);
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
		current.swapIn(foe, true);
		return true;
		
	}
	
	public void updateFlags() {
		boolean[] tempFlag = flags.clone();
		flags = new boolean[GamePanel.MAX_FLAGS];
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
			if (p != null) p.happinessCap += 50;
		}
		for (Pokemon p : box1) {
			if (p != null) p.happinessCap += 50;
		}
		for (Pokemon p : box2) {
			if (p != null) p.happinessCap += 50;
		}
		for (Pokemon p : box3) {
			if (p != null) p.happinessCap += 50;
		}
		
	}

	public JPanel displayTweaker() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		
		JTextField money = new JTextField(this.money + "");
		JComboBox<Integer> badges = new JComboBox<>();
		JComboBox<Pokemon> starter = new JComboBox<>();
		JButton pokedexButton = new JButton("Pokedex");
		JButton bagButton = new JButton("Bag");
		JCheckBox[] flags = new JCheckBox[GamePanel.MAX_FLAGS];
		JCheckBox[] locations = new JCheckBox[12];
		JButton importTrainers = new JButton("Import Trainers");
		JButton importItems = new JButton("Import ObjectItems");
		JCheckBox fish = new JCheckBox("Fishing Rod");
		JComboBox<Integer> grustCount = new JComboBox<>();
		
		JButton confirm = new JButton("Confirm");
		
		result.add(money);
		
		for (int i = 0; i < 9; i++) {
			badges.addItem(i);
		}
		badges.setSelectedIndex(this.badges);
		result.add(badges);
		
		for (int i = 1; i < 8; i += 3) {
			starter.addItem(new Pokemon(i, 5, true, false));
		}
		starter.setSelectedIndex(this.starter - 1);
		result.add(starter);
		
		pokedexButton.addActionListener(e -> {
			showPokedexModifier();
		});
		result.add(pokedexButton);
		
		bagButton.addActionListener(e -> {
			showBagModifier();
		});
		result.add(bagButton);
		
		String[] flagDesc = new String[] {
				"First Gate", "Scott 1", "Rick 1", "TN in Office", "Scott 2", "Fred 2", "Key A SC", "Key B SC",
				"Clear Room A", "Clear Room B", "Gift Starter", "Gift Dog", "Gift Magic", "Gift Ancient", "Gift \"Starter\"",
				"Fred 3", "Talk to Grandpa", "Gym 5", "Gift E/S", "Rick 2", "Maxwell 1", "Scott 4", "Gift Glurg", "Coins Gotten",
				"Autosave Warn", "Magmaclang", "MSJ TN"
		};
		JPanel flagsPanel = new JPanel();
		flagsPanel.setLayout(new BoxLayout(flagsPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < flagDesc.length; i++) {
		    flags[i] = new JCheckBox(flagDesc[i]);
		    flags[i].setSelected(this.flags[i]);
		    flagsPanel.add(flags[i]);
		}
		JScrollPane flagsPane = new JScrollPane(flagsPanel);
		flagsPane.setPreferredSize(new Dimension(150, 100));
		flagsPane.getVerticalScrollBar().setUnitIncrement(8);
		result.add(flagsPane);
		// NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
		String[] locDesc = new String[] {
				"NMT", "BVT", "PG", "SC", "KV", "PP", "SRC", "GT", "FC", "RC", "IT", "CC" 
		};
		JPanel locationsPanel = new JPanel();
		locationsPanel.setLayout(new BoxLayout(locationsPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < 12; i++) {
		    locations[i] = new JCheckBox(locDesc[i]);
		    locations[i].setSelected(this.locations[i]);
		    locationsPanel.add(locations[i]);
		}
		JScrollPane locationsPane = new JScrollPane(locationsPanel);
		locationsPane.setPreferredSize(new Dimension(150, 100));
		locationsPane.getVerticalScrollBar().setUnitIncrement(8);
		result.add(locationsPane);
		
		importTrainers.addActionListener(e -> {
			String filePath = "./docs/trainers.txt";

		    try {
		        String data = Files.readString(Paths.get(filePath));

		        String[] entries = data.split(",");
		        for (int i = 0; i < trainersBeat.length; i++) {
		        	trainersBeat[i] = Boolean.parseBoolean(entries[i]);
		        }
		    } catch (IOException ex) {
		    	JOptionPane.showMessageDialog(null, "Error reading from trainers.txt,\nmake sure it exists in\nthe docs folder and was\nexported correctly!");
		    }
		});
		
		importItems.addActionListener(e -> {
			String filePath = "./docs/items.txt";

		    try {
		        String data = Files.readString(Paths.get(filePath));

		        String[] rows = data.split("\n");
		        int numRows = rows.length;
		        int numCols = rows[0].split(",").length;

		        for (int i = 0; i < numRows; i++) {
		            String[] values = rows[i].split(",");
		            for (int j = 0; j < numCols; j++) {
		                this.itemsCollected[i][j] = Boolean.parseBoolean(values[j]);
		            }
		        }
		    } catch (IOException ex) {
		    	JOptionPane.showMessageDialog(null, "Error reading from items.txt,\nmake sure it exists in\nthe docs folder and was\nexported correctly!");
		    }
		});
		
		result.add(importTrainers);
		result.add(importItems);
		
		fish.setSelected(this.fish);
		result.add(fish);
		
		for (int i = 0; i < 11; i++) {
			grustCount.addItem(i);
		}
		grustCount.setSelectedIndex(this.grustCount);
		result.add(grustCount);
		
		AutoCompleteDecorator.decorate(badges);
		AutoCompleteDecorator.decorate(starter);
		AutoCompleteDecorator.decorate(grustCount);
		
		confirm.addActionListener(e -> {
			this.money = Integer.parseInt(money.getText());
			this.badges = (int) badges.getSelectedItem();
			this.starter = starter.getSelectedIndex() + 1;
			for (int i = 0; i < flagDesc.length; i++) {
				this.flags[i] = flags[i].isSelected();
			}
			for (int i = 0; i < locations.length; i++) {
				this.locations[i] = locations[i].isSelected();
			}
			this.fish = fish.isSelected();
			this.grustCount = grustCount.getSelectedIndex();
			SwingUtilities.getWindowAncestor(result).dispose();
		});
		result.add(confirm);
		
		return result;
	}

	private void showPokedexModifier() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		int pokedex[] = this.pokedex.clone();
		
		for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
			JPanel member = new JPanel(new FlowLayout(FlowLayout.LEFT));
			member.add(new JLabel(new Pokemon(i, 5, true, false).name));
			JRadioButton[] buttons = new JRadioButton[3];
			String[] labels = new String[] {"0", "S", "C"};
			ButtonGroup buttonLayout = new ButtonGroup();
			for (int j = 0; j < 3; j++) {
				buttons[j] = new JRadioButton(labels[j]);
				buttonLayout.add(buttons[j]);
				member.add(buttons[j]);
				
				buttons[j].setActionCommand(Integer.toString(i));
				
				buttons[j].addActionListener(e -> {
					pokedex[Integer.parseInt(((AbstractButton) e.getSource()).getActionCommand())] = Arrays.asList(buttons).indexOf(e.getSource());
				});
			}
			
			buttons[this.pokedex[i]].setSelected(true);
			
			panel.add(member);
		}
		
		JScrollPane scrollPanel = new JScrollPane(panel);
		scrollPanel.setPreferredSize(new Dimension(300, 300));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(8);
		result.add(scrollPanel);
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(e -> {
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				this.pokedex[i] = pokedex[i];
			}
			SwingUtilities.getWindowAncestor(result).dispose();
		});
		result.add(confirmButton);
		JOptionPane.showMessageDialog(null, result);
	}
	
	private void showBagModifier() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JTextField[] counts = new JTextField[bag.count.length];
		Item[] items = Item.values();
		
		for (int i = 0; i < counts.length; i++) {
			final int index = i;
			JPanel member = new JPanel(new FlowLayout(FlowLayout.LEFT));
			member.add(new JLabel(items[i].toString()));
			
			counts[i] = new JTextField(bag.count[i] + "");
			counts[i].setPreferredSize(new Dimension(40, counts[i].getPreferredSize().height));
			counts[i].addFocusListener(new FocusAdapter() {
				@Override
		    	public void focusGained(FocusEvent e) {
		        	counts[index].selectAll();
		    	}
			});
			member.add(counts[i]);
			
			panel.add(member);
		}
		
		JScrollPane scrollPanel = new JScrollPane(panel);
		scrollPanel.setPreferredSize(new Dimension(300, 300));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(8);
		result.add(scrollPanel);
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(e -> {
			for (int i = 0; i < counts.length; i++) {
				int count = Integer.parseInt(counts[i].getText().trim());
				if (count > 0) bag.add(Item.getItem(i));
				if (count < 1) bag.bag[i] = null;
				bag.count[i] = count;
			}
			SwingUtilities.getWindowAncestor(result).dispose();
		});
		result.add(confirmButton);
		JOptionPane.showMessageDialog(null, result);
		
	}

	public boolean hasTM(Move move) {
		if (!move.isTM()) return false;
		for (int i = 93; i < 200; i++) {
			if (bag.contains(i) && Item.getItem(i).getMove() == move) return true;
		}
		return false;
	}

	public int getDexShowing() {
		int result = pokedex.length;
		for (int i = pokedex.length - 1; i >= 0; i--) {
			if (pokedex[i] != 0) {
				result = i;
				break;
			}
		}
		result = result >= pokedex.length - 1 ? pokedex.length : result + 3;
		return result;
	}

	public int getAmountSelected() {
		int result = 0;
		ArrayList<Pokemon> allPokemon = getAllPokemon();
		for (Pokemon p : allPokemon) {
			if (p.isSelected()) result++;
		}
		return result;
	}

	public ArrayList<Pokemon> getAllPokemon() {
		ArrayList<Pokemon> result = new ArrayList<>();
		for (Pokemon p : team) {
			if (p != null) result.add(p);
		}
		for (Pokemon p : box1) {
			if (p != null) result.add(p);
		}
		for (Pokemon p : box2) {
			if (p != null) result.add(p);
		}
		for (Pokemon p : box3) {
			if (p != null) result.add(p);
		}
		return result;
	}
	
	public boolean teamIsSelected() {
		for (Pokemon p : team) {
			if (p != null && !p.isSelected()) return false;
		}
		return true;
	}
	
	public void heal() {
		for (Pokemon member : team) {
			if (member != null) member.heal();
		}
	}

}
