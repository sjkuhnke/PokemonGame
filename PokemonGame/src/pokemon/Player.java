package pokemon;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import overworld.GamePanel;
import overworld.Main;
import pokemon.Bag.Entry;
import pokemon.Battle.JGradientButton;
import pokemon.Pokemon.Task;

public class Player extends Trainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2851052666892205583L;
	
	public Pokemon[][] boxes;
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
	public int coins;
	public int gamesWon;
	public int winStreak;
	public int currentBox;
	public int version;
	
	public static final int MAX_BOXES = 10;
	public static final int VERSION = 1;
	
	public Player(GamePanel gp) {
		super(true);
		boxes = new Pokemon[MAX_BOXES][30];

		bag = new Bag();
		posX = 79;
		posY = 46;
		
		itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
		locations[0] = true;
		bag.add(Item.CALCULATOR);
		
		version = VERSION;
	}
	
	public void catchPokemon(Pokemon p) {
	    if (p.isFainted()) return;
	    boolean hasNull = false;
	    Pokemon.addTask(Task.NICKNAME, "Would you like to nickname " + p.name + "?", p);
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
	                Pokemon.addTask(Task.END, "Caught " + p.nickname + ", added to party!");
	                current = team[0];
	                break;
	            }
	        }
	    } else {
	    	p.heal();
	        int index = -1;
	        for (int i = 0; i < boxes.length; i++) {
	            for (int j = 0; j < boxes[i].length; j++) {
	                if (boxes[i][j] == null) {
	                    index = j;
	                    break;
	                }
	            }
	            if (index >= 0) {
	                boxes[i][index] = p;
	                Pokemon.addTask(Task.END, "Caught " + p.nickname + ", sent to box " + (i + 1) + "!");
	                return;  // Exit the method after catching the Pokemon
	            }
	        }
	        Pokemon.addTask(Task.END, "Cannot catch " + p.nickname + ", all boxes are full.");
	    }
	}


	public void swapToFront(Pokemon pokemon, int index) {
		if (!current.isFainted()) {
			Pokemon.addSwapOutTask(current);
		}
		
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
		Pokemon.addSwapInTask(current);
		if (this.current.vStatuses.contains(Status.HEALING) && this.current.currentHP != this.current.getStat(0)) this.current.heal();
	}
	
	public void swap(int a, int b) {
		Pokemon temp = team[a];
		team[a] = team[b];
		team[b] = temp;
		
		if (current == team[a]) {
			current = team[b];
		} else if (current == team[b]) {
			current = team[a];
		}
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
		
		swapToFront(team[index], index);
		
		Pokemon.addTask(Task.TEXT, current.nickname + " was dragged out!");
		current.swapIn(foe, true);
		return true;
		
	}
	
	private void updateFlags() {
		boolean[] tempFlag = flags.clone();
		flags = new boolean[GamePanel.MAX_FLAGS];
		for (int i = 0; i < tempFlag.length; i++) {
			flags[i] = tempFlag[i];
		}
	}
	
	private void updateTrainers() {
		boolean[] temp = trainersBeat.clone();
		trainersBeat = new boolean[Main.trainers.length];
		for (int i = 0; i < temp.length; i++) {
			trainersBeat[i] = temp[i];
		}
	}

	private void updateItems(int x, int y) {
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
		for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                if (boxes[i][j] != null) {
                    boxes[i][j].happinessCap += 50;
                }
            }
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
		for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                if (boxes[i][j] != null) {
                    result.add(boxes[i][j]);
                }
            }
        }
		return result;
	}
	
	public boolean teamIsSelected() {
		for (Pokemon p : team) {
			if (p != null && !p.isSelected()) return false;
		}
		return true;
	}
	
	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}

	public ArrayList<Entry> getItems(int pocket) {
		ArrayList<Entry> result = new ArrayList<>();
		for (Entry i : bag.getItems()) {
			if (i.getItem().getPocket() == pocket) {
				result.add(i);
			}
		}
		return result;
	}

	public Entry getBall(Entry ball) {
		if (ball != null) {
			ball.count = bag.count[ball.getItem().getID()];
			return ball;
		}
		for (int i = 1; i < 4; i++) {
			if (bag.count[i] > 0) return bag.new Entry(Item.getItem(i), bag.count[i]);
		}
		return ball;
	}
	
	public ArrayList<Entry> getBalls() {
		ArrayList<Entry> result = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			if (bag.count[i] > 0) result.add(bag.new Entry(Item.getItem(i), bag.count[i]));
		}
		return result;
	}

	public void moveItem(Item item, Item addAbove) {
		bag.bag.remove(item);
		int index = bag.bag.indexOf(addAbove);
		bag.bag.add(index, item);
	}

	public void sellItem(Item item, int amt) {
		bag.count[item.getID()] -= amt;
		money += amt * item.getSell();
	}

	public void useItem(Pokemon p, Item item, GamePanel gp) {
		switch (item) {
		// Potions
		case POTION:
		case SUPER_POTION:
		case HYPER_POTION:
		case MAX_POTION:
		case FULL_RESTORE:
			if (p.currentHP == p.getStat(0) || p.isFainted()) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		int difference = 0;
        		int healAmt = item.getHealAmount();
        		if (healAmt > 0) {
        			difference = Math.min(healAmt, p.getStat(0) - p.currentHP);
        			p.currentHP += healAmt;
        		} else {
        			difference = p.getStat(0) - p.currentHP;
        			p.currentHP = p.getStat(0);
        			if (item == Item.FULL_RESTORE) p.status = Status.HEALTHY;
        		}
        		gp.ui.showMessage(p.nickname + " was healed by " + difference + " HP");
	        	p.verifyHP();
        	}
			break;
			
		// Status Healers
		case AWAKENING:
		case BURN_HEAL:
		case FREEZE_HEAL:
		case PARALYZE_HEAL:
		case FULL_HEAL:
		case KLEINE_BAR:
		case ANTIDOTE:
			Status target = item.getStatus();
			target = target == null && p.status != Status.HEALTHY ? p.status : target;
	        target = p.status == Status.TOXIC && target == Status.POISONED ? Status.TOXIC : target;

    		if (p.status != target || p.isFainted()) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		Status temp = p.status;
        		p.status = Status.HEALTHY;
	        	gp.ui.showMessage(Item.breakString(p.nickname + " was cured of its " + temp.getName(), 45));
        	}
			break;
			
		// Revives
		case REVIVE:
		case MAX_REVIVE:
			if (!p.isFainted()) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		p.fainted = false;
        		if (item == Item.REVIVE) {
        			p.currentHP = p.getStat(0) / 2;
        		} else {
        			p.currentHP = p.getStat(0);
        		}
	        	gp.ui.showMessage(p.nickname + " was revived!");
        	}
			break;
			
		// Mints
		case ADAMANT_MINT:
		case BOLD_MINT:
		case BRAVE_MINT:
		case CALM_MINT:
		case CAREFUL_MINT:
		case IMPISH_MINT:
		case JOLLY_MINT:
		case MODEST_MINT:
		case QUIET_MINT:
		case SERIOUS_MINT:
		case TIMID_MINT:
			double nature[];
        	switch (item.getID()) {
        	case 29:
        		nature = new double[] {1.1,1.0,0.9,1.0,1.0,-1.0};
        		break;
        	case 30:
        		nature = new double[] {0.9,1.1,1.0,1.0,1.0,-1.0};
        		break;
        	case 31:
        		nature = new double[] {1.1,1.0,1.0,1.0,0.9,-1.0};
        		break;
        	case 32:
        		nature = new double[] {0.9,1.0,1.0,1.1,1.0,-1.0};
        		break;
        	case 33:
        		nature = new double[] {1.0,1.0,0.9,1.1,1.0,-1.0};
        		break;
        	case 34:
        		nature = new double[] {1.0,1.1,0.9,1.0,1.0,-1.0};
        		break;
        	case 35:
        		nature = new double[] {1.0,1.0,0.9,1.0,1.1,-1.0};
        		break;
        	case 36:
        		nature = new double[] {0.9,1.0,1.1,1.0,1.0,-1.0};
        		break;
        	case 37:
        		nature = new double[] {1.0,1.0,1.1,1.0,0.9,-1.0};
        		break;
        	case 38:
        		nature = new double[] {1.0,1.0,1.0,1.0,1.0,4.0};
        		break;
        	case 39:
        		nature = new double[] {0.9,1.0,1.0,1.0,1.1,-1.0};
        		break;
    		default:
    			nature = null;
    			break;
        	}
    		
    		String natureOld = p.getNature();
        	p.nature = nature;
        	p.stats = p.getStats();
        	gp.ui.showMessage(p.nickname + "'s nature was changed from " + natureOld + " to " + p.getNature() + "!");
			break;
			
		// Euphorian Gem
		case EUPHORIAN_GEM:
			if (p.happiness >= 255) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		if (p.item == Item.SOOTHE_BELL) {
        			p.awardHappiness(50, true);
        		} else {
        			p.awardHappiness(100, true);
        		}
        		gp.ui.showMessage(p.nickname + " looked happier!");
        	}
			break;
			
		// Evo Items
		case DAWN_STONE:
		case DUSK_STONE:
		case ICE_STONE:
		case LEAF_STONE:
		case PETTICOAT_GEM:
		case VALIANT_GEM:
			boolean eligible = item.getEligible(p.id);
			if (!eligible) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		boolean shouldEvolve = Battle.displayEvolution(p);
    			if (shouldEvolve) {
    				Pokemon result = new Pokemon(p.getEvolved(item.getID()), p);
    		        int hpDif = p.getStat(0) - p.currentHP;
    		        result.currentHP -= hpDif;
    		        gp.ui.showMessage(p.nickname + " evolved into " + result.name + "!");
    		        result.exp = p.exp;
    		        int index = Arrays.asList(getTeam()).indexOf(p);
    		        team[index] = result;
    		        if (index == 0) setCurrent(result);
    		        result.checkMove();
                    pokedex[result.id] = 2;
    			} else {
    				gp.ui.showMessage(p.nickname + " did not evolve.");
    			}
        	}
			break;
			
			
		// Ability Capsule
		case ABILITY_CAPSULE:
			boolean swappable = p.canUseItem(item) == 1;
    		if (!swappable) {
        		gp.ui.showMessage("It won't have any effect.");
        		return;
        	} else {
        		Ability oldAbility = p.ability;
        		p.abilitySlot = 1 - p.abilitySlot;
        		p.setAbility(p.abilitySlot);
        		gp.ui.showMessage(Item.breakString(p.nickname + "'s ability was swapped from " + oldAbility + " to " + p.ability + "!", 45));
        	}
			break;
			
		// Bottle Caps
		case BOTTLE_CAP:
		case GOLD_BOTTLE_CAP:
			if (item == Item.BOTTLE_CAP) {
        		JPanel ivPanel = new JPanel();
        		ivPanel.setLayout(new GridLayout(6, 1));
        		int[] ivs = p.getIVs();
        		for (int k = 0; k < 6; k++) {
        			final int kndex = k;
        			JButton iv = new JButton();
    	        	String type = Pokemon.getStatType(k);
        			iv.setText(type + ": " + ivs[k]);
        			final String finalType = type;
        			
        			iv.addActionListener(h -> {
        				gp.ui.showMessage(p.nickname + "'s " + finalType + "IV was maxed out!");
        				p.ivs[kndex] = 31;
        				p.stats = p.getStats();
        			});
        			
        			ivPanel.add(iv);
        		}
        		JOptionPane.showMessageDialog(null, ivPanel, "Which IV?", JOptionPane.PLAIN_MESSAGE);
        	} else {
        		gp.ui.showMessage(p.nickname + "'s IVs were maxed out!");
        		p.ivs = new int[] {31, 31, 31, 31, 31, 31};
        		p.stats = p.getStats();
        	}
			break;
				
		// Edge Kit
		case EDGE_KIT:
			if (p.expMax - p.exp != 1) {
        		p.exp = p.expMax - 1;
        		gp.ui.showMessage(p.nickname + " successfully EDGED!");
        	} else {
        		gp.ui.showMessage("It won't have any effect.");
        	}
			return;
		
		// Elixirs
		case ELIXIR:
		case MAX_ELIXIR:
			if (item == Item.MAX_ELIXIR) {
        		boolean work = false;
        		for (Moveslot m : p.moveset) {
        			if (m != null && m.currentPP != m.maxPP) {
        				work = true;
        				break;
        			}
        		}
        		if (work) {
	        		for (Moveslot m : p.moveset) {
	        			if (m != null) m.currentPP = m.maxPP;
	        		}
	        		gp.ui.showMessage(p.nickname + "'s PP was restored!");
        		} else {
        			gp.ui.showMessage("It won't have any effect.");
        			return;
        		}
        	} else {
        		JPanel movePanel = new JPanel();
        	    movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
        		for (Moveslot m : p.moveset) {
        			if (m != null) {
    	        		JGradientButton move = new JGradientButton("<html><center>" + m.move.toString() + "<br>" + " " + m.showPP() + "</center></html>");
    	        		move.setFont(new Font(move.getFont().getName(), Font.PLAIN, 13));
    	        		move.setBackground(m.move.mtype.getColor());
    	        		move.setForeground(m.getPPColor());
    	        		move.addMouseListener(new MouseAdapter() {
	        	        	@Override
	        			    public void mouseClicked(MouseEvent e) {
	        			    	if (SwingUtilities.isRightMouseButton(e)) {
	        			            JOptionPane.showMessageDialog(null, m.move.getMoveSummary(p, null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
	        			        } else {
	        			        	if (m.currentPP != m.maxPP) {
	        			        		m.currentPP = m.maxPP;
    	        			        	gp.ui.showMessage(m.move.toString() + "'s PP was restored!");
	        			        	} else {
	        			        		gp.ui.showMessage("It won't have any effect.");
	        			        		return;
	        			        	}
	        			        }
	        			    }
	        	        });
	        	        movePanel.add(move);
        			}
	        		
	            }
        		JOptionPane.showMessageDialog(null, movePanel, "Select a move to restore PP:", JOptionPane.PLAIN_MESSAGE);
        	}
			break;
			
		// PPs
		case PP_UP:
		case PP_MAX:
			break;
		
		case RARE_CANDY:
			break;
		
		// TMS/HMS
		case HM01:
		case HM02:
		case HM03:
		case HM04:
		case HM05:
		case HM06:
		case HM07:
		case HM08:
		case TM01:
		case TM02:
		case TM03:
		case TM04:
		case TM05:
		case TM06:
		case TM07:
		case TM08:
		case TM09:
		case TM10:
		case TM11:
		case TM12:
		case TM13:
		case TM14:
		case TM15:
		case TM16:
		case TM17:
		case TM18:
		case TM19:
		case TM20:
		case TM21:
		case TM22:
		case TM23:
		case TM24:
		case TM25:
		case TM26:
		case TM27:
		case TM28:
		case TM29:
		case TM30:
		case TM31:
		case TM32:
		case TM33:
		case TM34:
		case TM35:
		case TM36:
		case TM37:
		case TM38:
		case TM39:
		case TM40:
		case TM41:
		case TM42:
		case TM43:
		case TM44:
		case TM45:
		case TM46:
		case TM47:
		case TM48:
		case TM49:
		case TM50:
		case TM51:
		case TM52:
		case TM53:
		case TM54:
		case TM55:
		case TM56:
		case TM57:
		case TM58:
		case TM59:
		case TM60:
		case TM61:
		case TM62:
		case TM63:
		case TM64:
		case TM65:
		case TM66:
		case TM67:
		case TM68:
		case TM69:
		case TM70:
		case TM71:
		case TM72:
		case TM73:
		case TM74:
		case TM75:
		case TM76:
		case TM77:
		case TM78:
		case TM79:
		case TM80:
		case TM81:
		case TM82:
		case TM83:
		case TM84:
		case TM85:
		case TM86:
		case TM87:
		case TM88:
		case TM89:
		case TM90:
		case TM91:
		case TM92:
		case TM93:
		case TM94:
		case TM95:
		case TM96:
		case TM97:
		case TM98:
		case TM99:
			int learnable = p.canUseItem(item);
			if (learnable == 0) {
        		gp.ui.showMessage("" + p.nickname + " can't learn " + item.getMove() + "!");
        	} else if (learnable == 2) {
        		gp.ui.showMessage("" + p.nickname + " already knows " + item.getMove() + "!");
        	} else {
        		boolean learnedMove = false;
	            for (int k = 0; k < 4; k++) {
	                if (p.moveset[k] == null) {
	                	p.moveset[k] = new Moveslot(item.getMove());
	                	gp.ui.showMessage(p.nickname + " learned " + item.getMove() + "!");
	                    learnedMove = true;
	                    break;
	                }
	            }
	            if (!learnedMove) {
	            	gp.ui.currentPokemon = p;
	            	gp.ui.currentMove = item.getMove();
	            	gp.ui.moveOption = -1;
	            	gp.ui.showMoveOptions = true;
	            }
        	}
			return;
		default:
			break;
		}
		bag.remove(item);
		gp.ui.currentItems = getItems(gp.ui.currentPocket);
	}

	public void update(GamePanel gp) {
		updateTrainers();
		updateItems(gp.obj.length, gp.obj[1].length);
		updateFlags();
		for (Pokemon p : getAllPokemon()) {
			if (p != null) {
				p.update();
			}
		}
		version = VERSION;
	}
	
	public void setSprites() {
		for (Pokemon p : getAllPokemon()) {
			if (p != null) {
				p.setSprites();
			}
		}
	}
}