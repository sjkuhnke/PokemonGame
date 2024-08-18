package pokemon;

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

import overworld.GamePanel;
import pokemon.Bag.Entry;
import pokemon.Pokemon.Task;

public class Player extends Trainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2851052666892205583L;
	
	public Pokemon[][] boxes;
	public Pokemon[] gauntletBox;
	public String[] boxLabels;
	private int numBattled;
	private int posX;
	private int posY;
	public Bag bag;
	public int badges;
	public int starter;
	public int[] pokedex;
	public int currentMap;
	public boolean[] trainersBeat;
	public boolean[][] itemsCollected;
	public boolean[][] flag;
	
	/**
	 * This field is deprecated and should not be used in new code.
	 * Use {@link #flag} instead.
	 *
	 * @deprecated This field has been replaced by {@link #flag} which supports a two-dimensional boolean array.
	 */
	@Deprecated
	public boolean[] flags;
	public boolean[] locations;
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
	
	public static final int MAX_BOXES = 12;
	public static final int GAUNTLET_BOX_SIZE = 4;
	public static final int VERSION = 32;
	
	public static final int MAX_POKEDEX_PAGES = 4;
	
	public static Pokemon[] pokedex1 = new Pokemon[Pokemon.POKEDEX_1_SIZE]; // regular
	public static Pokemon[] pokedex2 = new Pokemon[Pokemon.POKEDEX_METEOR_SIZE]; // shadow
	public static Pokemon[] pokedex3 = new Pokemon[Pokemon.POKEDEX_METEOR_SIZE]; // electric
	public static Pokemon[] pokedex4 = new Pokemon[Pokemon.POKEDEX_2_SIZE]; // regional
	
	public static int[] spawn = new int[] {52, 31, 41}; // map, x, y
	
	public Player(GamePanel gp) {
		super(true);
		boxes = new Pokemon[MAX_BOXES][30];
		gauntletBox = new Pokemon[GAUNTLET_BOX_SIZE];
		boxLabels = setupBoxLabels();

		bag = new Bag();
		currentMap = spawn[0];
		posX = spawn[1];
		posY = spawn[2];
		
		starter = -1;
		secondStarter = -1;
		pokedex = new int[Pokemon.MAX_POKEMON + 1];
		flags = new boolean[50];
		flag = new boolean[10][GamePanel.MAX_FLAG];
		locations = new boolean[12]; // NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
		itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
		trainersBeat = new boolean[MAX_TRAINERS];
		locations[0] = true;
		
		version = VERSION;
	}

	public void catchPokemon(Pokemon p, Item ball) {
	    if (p.isFainted()) return;
	    boolean hasNull = false;
	    Pokemon.addTask(Task.NICKNAME, "Would you like to nickname " + p.name + "?", p);
	    pokedex[p.id] = 2;
	    p.clearVolatile();
	    p.consumeItem(null);
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
	
	public void catchPokemon(Pokemon p) {
		catchPokemon(p, Item.POKEBALL);
	}


	public void swapToFront(Pokemon pokemon, int index) {
		if (!current.isFainted()) {
			Pokemon.addSwapOutTask(current);
		}
		
		if (current.ability == Ability.REGENERATOR && !current.isFainted()) {
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
		Pokemon.addSwapInTask(current, current.currentHP);
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
	
	public void elevate(Pokemon pokemon) {
		int expAmt = pokemon.expMax - pokemon.exp;
    	pokemon.exp += expAmt;
    	while (pokemon.exp >= pokemon.expMax) {
    		if (pokemon.happiness < 255 && pokemon.happinessCap > 2) pokemon.awardHappiness(-3, false);
            // Pokemon has leveled up, check for evolution
            pokemon.levelUp(this);
        }
    	pokemon.fainted = false;
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
		if (flag == null) {
			flag = new boolean[10][GamePanel.MAX_FLAG];
			updateLegacyFlags();
		} else {
			boolean[][] temp = flag.clone();
			flag = new boolean[10][GamePanel.MAX_FLAG];
			for (int i = 0; i < temp.length; i++) {
				for (int j = 0; j < temp[1].length; j++) {
					flag[i][j] = temp[i][j];
				}
			}
		}
	}
	
	private void updateLegacyFlags() {
		// TODO: map flags to flag indices
		secondStarter--;
		//flags = null;
	}
	
	private void updateTrainers() {
		if (trainersBeat != null) {
			boolean[] temp = trainersBeat.clone();
			trainersBeat = new boolean[MAX_TRAINERS];
			for (int i = 0; i < temp.length; i++) {
				trainersBeat[i] = temp[i];
			}
		} else {
			trainersBeat = new boolean[MAX_TRAINERS];
		}
	}
	
	private void updatePokedex() {
		int[] temp = pokedex.clone();
		pokedex = new int[Pokemon.MAX_POKEMON + 1];
		for (int i = 0; i < temp.length; i++) {
			pokedex[i] = temp[i];
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
		JCheckBox[] flags = new JCheckBox[50]; // TODO: replace with flag
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
				"First Gate", "Scott 1", "Rick 1", "TN in Office", "Scott 2", "Fred 2", "Key A SC", "Key B SC", "Clear Room A", "Clear Room B", // 9
				"Gift Starter", "Gift Dog", "Gift Magic", "Gift Ancient", "Gift \"Starter\"", "Fred 3", "Talk to Grandpa", "Gym 5", "Gift E/S", // 18
				"Rick 2", "Maxwell 1", "Scott 4", "Gift Glurg", "Coins Gotten", "Autosave Warn", "Magmaclang", "MSJ TN", "Fred 4", // 27
				"Maxwell 2", "Zurroaratr", "Fishing Rod", "Got Surf", "Exp. Share", "Lucky Egg", "Rick 3", "Triwandoliz", "Diftery" // 36
				
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
		
		for (int i = 0; i < counts.length; i++) {
			final int index = i;
			JPanel member = new JPanel(new FlowLayout(FlowLayout.LEFT));
			member.add(new JLabel(Item.getItem(i).toString()));
			
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
		if (move == null || !move.isTM()) return false;
		for (int i = 93; i < 200; i++) {
			if (bag.contains(i) && Item.getItem(i).getMove() == move) return true;
		}
		return false;
	}

	public int getDexShowing(Pokemon[] pokedex) {
		int result = -1;
		for (int i = pokedex.length - 1; i >= 0; i--) {
			if (this.pokedex[pokedex[i].id] != 0) {
				result = i;
				break;
			}
		}
		result += 3;
		return Math.min(result, pokedex.length - 1);
	}

	public int getAmountSelected() {
		int result = 0;
		for (Pokemon p : gauntletBox) {
			if (p != null) result++;
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
		for (Pokemon p : gauntletBox) {
			if (p != null) result.add(p);
		}
		return result;
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
			if (ball.count <= 0) ball = null;
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

	public void moveItem(int moveIndex, int moveAboveIndex) {
		int itemID = bag.itemList[moveIndex];
		
		if (moveIndex > moveAboveIndex) {
			for (int i = moveIndex; i > moveAboveIndex; i--) {
				bag.itemList[i] = bag.itemList[i - 1];
			}
			bag.itemList[moveAboveIndex] = itemID;
		} else if (moveIndex < moveAboveIndex) {
			for (int i = moveIndex; i < moveAboveIndex - 1; i++) {
				bag.itemList[i] = bag.itemList[i + 1];
			}
			bag.itemList[moveAboveIndex - 1] = itemID;
		} else {
			throw new IllegalStateException("moveIndex can't equal moveAboveIndex");
		}
	}

	public void sellItem(Item item, int amt) {
		bag.count[item.getID()] -= amt;
		money += amt * item.getSell();
	}

	public void useItem(Pokemon p, Item item, GamePanel gp) {
		if (item.getPocket() == Item.HELD_ITEM || item.getPocket() == Item.BERRY) {
			if (p.item != null) {
				Item old = p.item;
				bag.add(old);
        		p.item = item;
	        	gp.ui.showMessage(p.nickname + " swapped its " + old.toString() + " for\na " + p.item.toString() + "!");
        	} else {
        		p.item = item;
        		gp.ui.showMessage(p.nickname + " was given " + item.toString() + " to hold!");
        	}
			gp.ui.selectedBagNum = -1;
		} else {
			switch (item) {
			// Potions
			case POTION:
			case SUPER_POTION:
			case HYPER_POTION:
			case MAX_POTION:
			case FULL_RESTORE:
				if (p.currentHP == p.getStat(0) || p.isFainted()) {
	        		gp.ui.showMessage(p.nickname + " is already full HP!");
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
	        		gp.ui.showMessage(p.nickname + " was healed by " + difference + " HP!");
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
	        		gp.ui.showMessage(p.nickname + " is already healthy!");
	        		return;
	        	} else {
	        		Status temp = p.status;
	        		p.status = Status.HEALTHY;
		        	gp.ui.showMessage(Item.breakString(p.nickname + " was cured of its " + temp.getName() + "!", 45));
	        	}
				break;
				
			// Revives
			case REVIVE:
			case MAX_REVIVE:
				if (!p.isFainted()) {
	        		gp.ui.showMessage(p.nickname + " isn't fainted!");
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
	        	p.setStats();
	        	gp.ui.showMessage(p.nickname + "'s nature was changed from " + natureOld + " to\n" + p.getNature() + "!");
				break;
				
			// Euphorian Gem
			case EUPHORIAN_GEM:
				if (p.happiness >= 255) {
	        		gp.ui.showMessage(p.nickname + " is already as happy as can be!");
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
			case FIRE_STONE:
			case LEAF_STONE:
			case PETTICOAT_GEM:
			case VALIANT_GEM:
			case RAZOR_CLAW:
				boolean eligible = item.getEligible(p.id);
				if (!eligible) {
	        		gp.ui.showMessage(p.nickname + " isn't compatible with " + item.toString() + ".");
	        		return;
	        	} else {
	        		gp.gameState = GamePanel.RARE_CANDY_STATE;
	        		Task t = Pokemon.addTask(Task.EVO_ITEM, "", p);
	        		t.evo = new Pokemon(p.getEvolved(item), p);
	        		Pokemon.addTask(Task.CLOSE, "");
	        	}
				break;
				
			// Ability Capsule
			case ABILITY_CAPSULE:
				boolean swappable = p.canUseItem(item) == 1;
	    		if (!swappable) {
	        		gp.ui.showMessage(Item.breakString(p.nickname + " only has one ability, it won't have any effect.", 40));
	        		return;
	        	} else {
	        		Ability oldAbility = p.ability;
	        		p.abilitySlot = 1 - p.abilitySlot;
	        		p.setAbility();
	        		gp.ui.showMessage(Item.breakString(p.nickname + "'s ability was swapped from " + oldAbility + " to " + p.ability + "!", 40));
	        	}
				break;
				
			// Ability Patch
			case ABILITY_PATCH:
				boolean swappable2 = p.canUseItem(item) == 1;
	    		if (!swappable2) {
	        		gp.ui.showMessage(Item.breakString(p.nickname + " doesn't have a hidden ability, it won't have any effect.", 40));
	        		return;
	        	} else {
	        		Ability oldAbility = p.ability;
	        		if (p.abilitySlot == 2) {
	        			p.abilitySlot = 0;
	        		} else {
	        			p.abilitySlot = 2;
	        		}
	        		p.setAbility();
	        		gp.ui.showMessage(Item.breakString(p.nickname + "'s ability was changed from " + oldAbility + " to " + p.ability + "!", 40));
	        	}
				break;
				
			// Bottle Caps
			case BOTTLE_CAP:
			case GOLD_BOTTLE_CAP:
				if (item == Item.BOTTLE_CAP) {
	        		gp.ui.currentPokemon = p;
	        		gp.ui.currentHeader = "Which IV to max out?";
	        		gp.ui.moveOption = -1;
	        		gp.ui.showIVOptions = true;
	        		return;
	        	} else {
	        		gp.ui.showMessage(p.nickname + "'s IVs were maxed out!");
	        		p.ivs = new int[] {31, 31, 31, 31, 31, 31};
	        		p.setStats();
	        	}
				break;
					
			// Edge Kit
			case EDGE_KIT:
				if (p.expMax - p.exp != 1) {
	        		p.exp = p.expMax - 1;
	        		gp.ui.showMessage(p.nickname + " successfully EDGED!");
	        	} else {
	        		gp.ui.showMessage(p.nickname + " is already edged :>");
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
	        			gp.ui.showMessage(p.nickname + "'s PP is already full!");
	        		}
	        		break;
	        	} else {
	        		gp.ui.currentPokemon = p;
	            	gp.ui.currentMove = null;
	            	gp.ui.currentHeader = "Select a move to restore PP:";
	            	gp.ui.moveOption = -1;
	            	gp.ui.showMoveOptions = true;
	        	}
				return;
				
			// PPs
			case PP_UP:
			case PP_MAX:
				gp.ui.currentPokemon = p;
	        	gp.ui.currentMove = null;
	        	gp.ui.currentHeader = "Select a move to increase PP:";
	        	gp.ui.moveOption = -1;
	        	gp.ui.showMoveOptions = true;
				return;
			
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
				return;
			}
		}
		bag.remove(item);
		gp.ui.currentItems = getItems(gp.ui.currentPocket);
	}

	public void update(GamePanel gp) {
		if (this.trainersBeat.length != MAX_TRAINERS) updateTrainers();
		updateItems(gp.obj.length, gp.obj[1].length);
		updateFlags();
		updatePokedex();
		updateBag();
		for (Pokemon p : getAllPokemon()) {
			if (p != null) {
				p.update();
			}
		}
		updateBoxes();
		version = VERSION;
	}
	
	private void updateBag() {
		Item[] items = Item.values();
		if (bag.itemList != null && items.length == bag.itemList.length) return;
		
		int[] counts = bag.count;
		bag = new Bag();
		for (int i = 0; i < counts.length; i++) {
			bag.count[i] = counts[i];
		}
	}
	
	private void updateBoxes() {
		if (boxes.length == MAX_BOXES) return;
		Pokemon[][] temp = boxes.clone();
		boxes = new Pokemon[MAX_BOXES][30];
		
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[1].length; j++) {
				boxes[i][j] = temp[i][j];
			}
		}
		boxLabels = setupBoxLabels();
	}

	public void setSprites() {
		for (Pokemon p : getAllPokemon()) {
			if (p != null) {
				p.setSprites();
			}
		}
	}

	public void handleExpShare() {
		for (Pokemon p : team) {
			if (p != null && !p.battled && p.item == Item.EXP_SHARE) {
				numBattled++;
				p.battled = true;
			}
		}
	}
	
	public boolean teamWouldBeFainted(int index) {
		for (Pokemon p : team) {
			if (p != null && p != team[index] && !p.isFainted()) {
				return false;
			}
		}
		return true;
	}

	public void shiftTeamForward(int index) {
		Pokemon[] teamTemp = team.clone();
    	for (int i = index + 1; i < team.length; i++) {
        	teamTemp[i - 1] = team[i];
        }
    	team = teamTemp;
    	current = team[0];
    	team[5] = null;
	}

	public int[] getDexAmounts(Pokemon[] pokedex) {
		int[] result = new int[2];
		for (int i = 0; i <= getDexShowing(pokedex); i++) {
			if (this.pokedex[pokedex[i].id] > 0) {
				result[this.pokedex[pokedex[i].id] - 1]++;
			}
		}
		return result;
	}
	
	public String[] setupBoxLabels() {
		String[] result = new String[MAX_BOXES];
		for (int i = 0; i < MAX_BOXES; i++) {
			result[i] = "Box " + (i + 1);
		}
		return result;
	}

	public static void setupPokedex() {
		for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
			int dexNo = Math.abs(Pokemon.getDexNo(i));
			if (dexNo == 0) continue;
			int dexSec = Pokemon.getDexSection(i);
			
			Pokemon test = new Pokemon(i, 5, false, false);
			test.abilitySlot = 0;
			test.setAbility(test.abilitySlot);
			
			Pokemon[] dex = null;
			if (dexSec == 0) {
				dex = pokedex1;
			} else if (dexSec == 1) {
				dex = pokedex2;
			} else if (dexSec == 2) {
				dex = pokedex3;
			} else {
				dex = pokedex4;
			}
			
			if (dex[dexNo - 1] == null) {
				dex[dexNo - 1] = test;
			} else {
				throw new IllegalStateException("Pokedex slot isn't empty when initalizing Pokedex: " + dex[dexNo - 1].getName() + " and " + test.getName() + " have the same Dex No. of " + dexNo);
			}
		}
	}
	
	public int calculatePokedexes() {
		if (!flag[0][2]) {
			return 0;
		} else if (flag[0][2] && !flag[1][2]) {
			return 2;
		} else if (flag[1][2] && !flag[2][1]) {
			return 3;
		} else {
			return 4;
		}
	}

	public String getDexTitle(int dexType) {
		String[] options = new String[] {"Xhenovian Pokedex", "Shadow Pokedex", "Electric Pokedex", "Variant Pokedex"};
		return options[dexType];
	}
	
	public Pokemon[] getDexType(int dexType) {
		switch (dexType) {
		case 0:
			return pokedex1;
		case 1:
			return pokedex2;
		case 2:
			return pokedex3;
		case 3:
			return pokedex4;
		default:
			return null;
		}
	}
	
	public int getMaxDex(int dexType) {
		switch (dexType) {
		case 0:
			return Pokemon.POKEDEX_1_SIZE;
		case 1:
		case 2:
			return Pokemon.POKEDEX_METEOR_SIZE;
		case 3:
			return Pokemon.POKEDEX_2_SIZE;
		default:
			return -1;
		}
	}

	public int calculateStars() {
		int result = 0;
		boolean allSeen = true;
		for (int i = 0; i < 4; i++) {
			Pokemon[] dex = getDexType(i);
			int[] amt = getDexAmounts(dex);
			int maxDex = getMaxDex(i);
			
			if (allSeen && amt[0] + amt[1] < maxDex) {
				allSeen = false;
			}
			if (amt[1] >= maxDex) {
				result++;
			}
		}
		
		if (allSeen) result++;
		
		return result;
	}

	public void beatGymTrainers() {
		int[] indices;
		switch (badges) {
		case 1:
			indices = new int[] {14, 15, 16};
			break;
		case 2:
			indices = new int[] {56, 57, 58, 59, 60, 61, 62, 63, 64, 65};
			break;
		case 3:
			indices = new int[] {90, 91, 92, 93};
			break;
		case 4:
			indices = new int[] {133, 134, 135, 136};
			break;
		case 5:
			indices = new int[] {186, 187, 188, 189, 190, 191, 192, 193};
			break;
		case 6:
			indices = new int[] {246, 247, 248, 249, 250, 251, 252, 253, 254};
			break;
		case 7:
			indices = new int[] {291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304};
			break;
		case 8:
			indices = new int[] {};
			System.out.println("Gym 8 isn't implemented yet for Player.beatGymTrainers()");
			break;
		default:
			indices = new int[] {};
			System.out.println(badges + " is illegal for Player.beatGymTrainers()");
			break;
		}
		for (int i = 0; i < indices.length; i++) {
			trainersBeat[indices[i]] = true;
		}
	}
}
