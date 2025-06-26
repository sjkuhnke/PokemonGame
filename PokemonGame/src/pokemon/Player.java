package pokemon;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import entity.Entity;
import entity.NPC_Prize_Shop;
import entity.PlayerCharacter;
import overworld.GamePanel;
import overworld.UI;
import pokemon.Bag.Entry;
import util.Pair;

public class Player extends Trainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2851052666892205583L;
	
	public Pokemon[][] boxes;
	public Pokemon[] gauntletBox;
	public String[] boxLabels;
	public Pokemon[] tempTeam;
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
	public boolean visor;
	public int grustCount;
	public int scottItem;
	public int ballPurchase;
	public Item registeredItem;
	public Item[] resistBerries;
	public Item[] statBerries;
	public Item[] crystals;
	public int secondStarter;
	public Item choiceChoice;
	public Nursery nursery;
	public int coins;
	public int gamesWon;
	public int winStreak;
	public int[] blackjackStats; // 0: games played, 1: games won, 2: games pushed, 3: busts, 4: bust wins, 5: blackjacks 6: doubles 7: double wins, 8: coins won, 9: coins lost: 10: highest coin, 11: highest win streak, 12: lose streak, 13: high lose strk
	public boolean[] coinBadges;
	public HashMap<Integer, Boolean> puzzlesLocked;
	public int currentBox;
	public int version;
	private Integer id;
	public boolean amulet;
	public boolean spike;
	
	public boolean nuzlocke;
	public boolean nuzlockeStarted;
	public boolean isValidNuzlocke;
	public ArrayList<String> nuzlockeEncounters;
	public ArrayList<String> invalidReasons;
	
	public static final int MAX_BOXES = 12;
	public static final int GAUNTLET_BOX_SIZE = 4;
	public static final int VERSION = 65;
	
	public static final int MAX_POKEDEX_PAGES = 4;
	
	public static Pokemon[] pokedex1 = new Pokemon[Pokemon.POKEDEX_1_SIZE]; // regular
	public static Pokemon[] pokedex2 = new Pokemon[Pokemon.POKEDEX_METEOR_SIZE]; // shadow
	public static Pokemon[] pokedex3 = new Pokemon[Pokemon.POKEDEX_METEOR_SIZE]; // electric
	public static Pokemon[] pokedex4 = new Pokemon[Pokemon.POKEDEX_2_SIZE]; // regional
	
	public static int[] spawn = new int[] {52, 31, 41}; // map, x, y
	
	public Player(GamePanel gp) {
		super(true);
		
		setID(null);
		
		boxes = new Pokemon[MAX_BOXES][30];
		gauntletBox = new Pokemon[GAUNTLET_BOX_SIZE];
		boxLabels = setupBoxLabels();

		bag = new Bag(this);
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
		effects = new ArrayList<>();
		
		blackjackStats = new int[20];
		coinBadges = new boolean[12];
		puzzlesLocked = new HashMap<>();
		
		setupStatBerries();
        setupResistBerries();
        setupCrystals();
		
		version = VERSION;
	}

	private void setID(String saveName) {
		Random rand = saveName == null ? new Random() : new Random(saveName.hashCode());
		id = rand.nextInt();
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setupNuzlocke() {
		this.nuzlocke = true;
		this.nuzlockeStarted = false;
		this.isValidNuzlocke = true;
		this.nuzlockeEncounters = new ArrayList<>();
		invalidReasons = new ArrayList<>();
		this.bag.add(Item.INFINITE_REPEL);
		this.bag.add(Item.HEALING_PACK);
		this.bag.add(Item.POCKET_PC);
		this.bag.add(Item.RARE_CANDY_BOX);
		this.bag.add(Item.EDGE_KIT);
		this.bag.add(Item.STATUS_KIT);
		this.bag.add(Item.DAMAGE_KIT);
	}

	public boolean catchPokemon(Pokemon p, boolean nickname, Item ball) {
		if (p == null) return false;
		boolean egg = p instanceof Egg;
	    if (p == null || (p.isFainted() && !egg)) return false;
	    int index = 1;
	    Task t = null;
	    String metAt = PlayerCharacter.getMetAt();
	    if (nickname) {
	    	if (nuzlocke && !canCatchPokemonHere(metAt, p)) {
	    		String s = egg ? "receive " : "catch ";
	    		String reason = isDupes(p) ? p.name() + " is a dupe!" : "you've already gotten an encounter at " + metAt + "!";
	    		t = Task.createTask(Task.END, Item.breakString("Cannot " + s + p.name() + ", " + reason , UI.MAX_TEXTBOX));
	        	Task.insertTask(t, 0);
	    		return false;
	    	}
	    	t = Task.createTask(Task.NICKNAME, "Would you like to nickname " + p.name() + "?", p);
		    if (Pokemon.gp.gameState != GamePanel.PLAY_STATE) Task.insertTask(t, 0);
	    } else {
	    	index = 0;
	    }
	    if (!egg) pokedex[p.id] = 2;
	    p.clearVolatile();
	    p.consumeItem(null);
	    if (item == Item.HEAL_BALL) p.heal();
	    if (item == Item.FRIEND_BALL) p.happiness = Math.max(200, p.happiness);
	    p.ball = ball;
	    p.trainer = this;
	    p.metAt = metAt;
	    
	    int place = addPokemon(p);
	    if (place == 0) { // party
	    	if (nickname || egg) {
            	String s = egg ? "Received " : "Caught ";
            	t = Task.createTask(Task.END, s + p.name() + ", added to party!");
            	Task.insertTask(t, index);
            }
	    } else if (place == -1) {
	    	if (nickname || egg) {
	        	t = Task.createTask(Task.END, "Cannot catch " + p.name() + ", all boxes are full.");
	        	Task.insertTask(t, index);
	    	}
	    } else {
	    	if (nickname || egg) {
            	String s = egg ? "Received " : "Caught ";
            	t = Task.createTask(Task.END, s + p.name() + ", sent to box " + place + "!");
            	Task.insertTask(t, index);
            }
	    }
	    
	    if (nuzlocke && !egg) {
	    	removeEncounterArea(metAt, null);
	    }
	    return true;
	}
	
	public int addPokemon(Pokemon p) {
		boolean hasNull = false;
		
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
	                current = team[0];
	                return 0;
	            }
	        }
	    } else {
	    	p.heal();
	        int empty = -1;
	        for (int i = 0; i < boxes.length; i++) {
	            for (int j = 0; j < boxes[i].length; j++) {
	                if (boxes[i][j] == null) {
	                	empty = j;
	                    break;
	                }
	            }
	            if (empty >= 0) {
	                boxes[i][empty] = p;
	                return i + 1;
	            }
	        }
	    }
	    return -1;
	}

	public boolean catchPokemon(Pokemon p, boolean nickname) {
		return catchPokemon(p, nickname, Item.POKEBALL);
	}
	
	public boolean catchPokemon(Pokemon p) {
		return catchPokemon(p, true);
	}
	
	public boolean canCatchPokemonHere(String location, Pokemon p) {
		return !isDupes(p) && !this.nuzlockeEncounters.contains(location);
	}
	
	public void removeEncounterArea(String location, Pokemon p) {
		if (canCatchPokemonHere(location, p)) {
			this.nuzlockeEncounters.add(location);
		}
	}
	
	public boolean isDupes(Pokemon p) {
		if (p == null) return false;
		if (pokedex[p.id] == 2) return true;
		ArrayList<String> evoFamily = p.getEvolutionFamily();
		for (String s : evoFamily) {
			if (pokedex[Pokemon.getIDFromName(s)] == 2) return true;
		}
		return false;
	}

	public void invalidateNuzlocke(String string) {
		if (!nuzlocke) return;
		isValidNuzlocke = false;
		invalidReasons.add(string);
	}

	public void swapToFront(Pokemon pokemon, int index) {
		if (!current.isFainted()) {
			Task.addSwapOutTask(current, true);
		}
		
		if (current.ability == Ability.REGENERATOR && !current.isFainted()) {
			current.currentHP += current.getStat(0) / 3;
			current.verifyHP();
		}
		Pokemon lead = current;
		if (lead.hasStatus(Status.HEALING)) team[index].addStatus(Status.HEALING);
		lead.clearVolatile();
		if (lead.ability == Ability.ILLUSION) lead.illusion = true; // just here for calc
		this.current = pokemon;
		
		this.team[0] = pokemon;
		this.team[index] = lead;
		if (!this.current.battled) {
			numBattled++;
			this.current.battled = true;
		}
		if (!amulet && this.current.item == Item.AMULET_COIN) {
			amulet = true;
		}
		Task.addSwapInTask(current, true);
		if (this.current.hasStatus(Status.HEALING) && this.current.currentHP != this.current.getStat(0)) this.current.heal();
		Pokemon.field.switches++;
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
		if (nuzlocke && pokemon.isFainted()) return;
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
	
	public boolean knowsMove(Move m) {
		for (Pokemon p : team) {
			if (p != null && p.knowsMove(m)) return true;
		}
		return false;
	}
	
	public int getRequiredBadges(Move m) {
		switch (m) {
			case CUT: return 1;
			case ROCK_SMASH: return 2;
			case VINE_CROSS: return 3;
			case SURF: return 4;
			case SLOW_FALL: return 5;
			case WHIRLPOOL: return 6;
			case ROCK_CLIMB: return 7;
			case LAVA_SURF: return 8;
			default: return 0;
		}
	}
	
	public boolean swapRandom(Pokemon foe) {
		if (!hasValidMembers()) return false;
		Random rand = new Random();
		int index = rand.nextInt(team.length);
		while (team[index] == null || team[index].isFainted() || team[index] == current) {
			index = rand.nextInt(team.length);
		}
		current.clearVolatile();
		
		swapToFront(team[index], index);
		
		Task.addTask(Task.TEXT, current.nickname + " was dragged out!");
		current.swapIn(foe, true);
		foe.removeStatus(Status.TRAPPED);
		foe.removeStatus(Status.SPUN);
		foe.removeStatus(Status.SPELLBIND);
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
		JTextField coins = new JTextField(this.coins + "");
		JComboBox<Integer> badges = new JComboBox<>();
		JComboBox<String> starter = new JComboBox<>();
		JButton nuzlockeInfo = new JButton("Nuzlocke Info");
		JButton pokedexButton = new JButton("Pokedex");
		JButton bagButton = new JButton("Bag");
		JCheckBox[] locations = new JCheckBox[12];
		JButton importTrainers = new JButton("Import Trainers");
		JButton importItems = new JButton("Import ObjectItems");
		JCheckBox fish = new JCheckBox("Fishing Rod");
		JComboBox<Integer> grustCount = new JComboBox<>();
		
		JButton confirm = new JButton("Confirm");
		
		result.add(money);
		result.add(coins);
		
		for (int i = 0; i < 9; i++) {
			badges.addItem(i);
		}
		badges.setSelectedIndex(this.badges);
		result.add(badges);
		
		for (int i = 1; i < 8; i += 3) {
			starter.addItem(Pokemon.getName(i));
		}
		starter.setSelectedIndex(this.starter);
		result.add(starter);
		
		nuzlockeInfo.addActionListener(e -> {
			showNuzlockeInfo();
		});
		if (nuzlocke) result.add(nuzlockeInfo);
		
		pokedexButton.addActionListener(e -> {
			showPokedexModifier();
		});
		result.add(pokedexButton);
		
		bagButton.addActionListener(e -> {
			showBagModifier();
		});
		result.add(bagButton);
		
		String[][] flagDesc = {
			{"Dad First", "Starter", "Pokedex 1", "Grandma", "Avery", "Scott 1", "Second Starter", "Talk to Robin", "Letter A", "Letter B", "Letter C", "Tell Robin 1", "WH Grunt", "WH Unlock", "Rick 1", "Letter D", "Tell Robin 2", "Crook", "Lucky Egg"},
			{"Fred 1", "Researcher", "Teleport", "Ryder 1", "Fuse 1", "Rocky-E", "PP 1", "Poof-E", "PP 2", "Stanford", "TN Office", "Flamehox-E", "Office 2", "Scott", "Gyarados-E", "Fuse 2", "Stanford", "Fishing Rod", "Gym 2", "Photon Gift", "Res A Gift", "3+ E Forms", "All E Forms"},
			{"Scott 2", "Ryder 2", "Gift Magic", "Regional", "Millie 1", "Millie 2", "Fred 2", "Chained Xurkitree", "Millie 3", "TN Splinkty", "Millie 4", "Free Xurkitree", "UP Xurkitree", "Millie 5", "Gift Fossil"},
			{"Ryder 3", "Ice Master", "Ground Master", "I Unlock", "G Unlock", "I Clear", "G Clear", "Principal", "UP Cairnasaur", "Gift \"Starter\"", "Petticoat", "Valiant", "Gym 4"},
			{"Robin", "Scott 3", "Grandpa", "UP Shookwat", "Gift E/S", "Gym 5"},
			{"Arthra 1", "Arthra Talk", "Rick 2", "Fred 3", "Maxwell 1", "UP Splame", "Scott 4", "Glurg Gift", "Gym 6", "Maxwell After"},
			{"Scott Scene", "Coins", "Autosave Casino", "Magmaclang", "UP Buzzwole", "Rock Climb", "Gym 7", "Shake 1", "Shake 2", "Shake 3", "Shake 4", "Shake 5", "Shake 6", "Shake 7", "Shake 8", "Shake 9", "Shake 10", "Shake 11", "Shake 12"},
			{"Arthra Scene", "Merlin coming", "Rick 3", "Fred 4", "Maxwell 2", "Machine", "Dragowrath S1", "UP Pheromosa", "Dissemble Machine", "Faith", "Logic", "Merlin Guide", "Guard", "Awake", "Relomidel", "Relopamil", "Drago S2", "Arthra after", "Ryder Shack", "Ryder Possessed", "", "", "", ""},
			{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},			
		};
		
		JTabbedPane mainTabbedPane = new JTabbedPane();
		
		for (int badgeLevel = 0; badgeLevel < flagDesc.length; badgeLevel++) {
			JPanel badgePanel = new JPanel();
			badgePanel.setLayout(new BoxLayout(badgePanel, BoxLayout.Y_AXIS));
			
			JCheckBox[] badgeFlags = new JCheckBox[flagDesc[badgeLevel].length];
			for (int flagIndex = 0; flagIndex < flagDesc[badgeLevel].length; flagIndex++) {
				badgeFlags[flagIndex] = new JCheckBox(flagDesc[badgeLevel][flagIndex]);
				badgeFlags[flagIndex].setSelected(this.flag[badgeLevel][flagIndex]);
				
				badgePanel.add(badgeFlags[flagIndex]);
			}
			
			JScrollPane badgeScrollPane = new JScrollPane(badgePanel);
			badgeScrollPane.setPreferredSize(new Dimension(150, 200));
			badgeScrollPane.getVerticalScrollBar().setUnitIncrement(8);
			mainTabbedPane.addTab((badgeLevel + 1) + " split", badgeScrollPane);
		}
		
		result.add(mainTabbedPane);
		
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
		        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
		        String data = new String(fileBytes, StandardCharsets.UTF_8);

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
		    	byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
		        String data = new String(fileBytes, StandardCharsets.UTF_8);

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
		if (this.grustCount <= grustCount.getItemCount()) grustCount.setSelectedIndex(this.grustCount);
		result.add(grustCount);
		
		AutoCompleteDecorator.decorate(badges);
		AutoCompleteDecorator.decorate(starter);
		AutoCompleteDecorator.decorate(grustCount);
		
		confirm.addActionListener(e -> {
			this.money = Integer.parseInt(money.getText());
			this.coins = Integer.parseInt(coins.getText());
			int oldBadges = this.badges;
			this.badges = (int) badges.getSelectedItem();
			if (oldBadges != this.badges) Pokemon.gp.player.setClerkItems();
			this.starter = starter.getSelectedIndex();
			for (int i = 0; i < flagDesc.length; i++) {
				for (int j = 0; j < flagDesc[i].length; j++) {
					this.flag[i][j] = ((JCheckBox) ((JPanel) ((JScrollPane) mainTabbedPane.getComponentAt(i)).getViewport().getView()).getComponent(j)).isSelected();
				}
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
	
	public void showNuzlockeInfo() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		
		JLabel nuzlockeLabel = new JLabel("Nuzlocke:");
		JLabel nuzlockeAns = new JLabel(String.valueOf(nuzlocke));
		
		if (nuzlocke) {
			JLabel nuzlockeStartedLabel = new JLabel("Nuzlocke Started?");
			JLabel nuzlockeStartedAns = new JLabel(String.valueOf(nuzlockeStarted));
			JLabel nuzlockeValidLabel = new JLabel("Nuzlocke Is Valid?");
			JLabel nuzlockeValidAns = new JLabel(String.valueOf(isValidNuzlocke));
			
			panel.add(nuzlockeLabel);
			panel.add(nuzlockeAns);
			panel.add(nuzlockeStartedLabel);
			panel.add(nuzlockeStartedAns);
			panel.add(nuzlockeValidLabel);
			panel.add(nuzlockeValidAns);
			
			panel.add(new JLabel("_____________"));
			panel.add(new JLabel("_____________"));
			
			for (int i = 0; i < invalidReasons.size(); i++) {
				JLabel label = new JLabel("Reason #" + (i+1));
				JLabel labelAns = new JLabel(invalidReasons.get(i));
				panel.add(label);
				panel.add(labelAns);
			}
			
			panel.add(new JLabel("_____________"));
			panel.add(new JLabel("_____________"));
			
			for (int i = 0; i < nuzlockeEncounters.size(); i++) {
				JLabel label = new JLabel("#" + (i+1));
				JLabel labelAns = new JLabel(nuzlockeEncounters.get(i));
				panel.add(label);
				panel.add(labelAns);
			}
		}
		
		JOptionPane.showMessageDialog(null, panel, "Nuzlocke Info", JOptionPane.PLAIN_MESSAGE);
	}

	private void showPokedexModifier() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		int pokedex[] = this.pokedex.clone();
		
		for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
			JPanel member = new JPanel(new FlowLayout(FlowLayout.LEFT));
			member.add(new JLabel(new Pokemon(i, 5, true, false).name()));
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
		ArrayList<Move> moves = Item.getTMMoves();
		int index = moves.indexOf(move);
		if (index == -1) return false;
		Item item = Item.getItem(index + 93);
		if (bag.contains(item)) return true;
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
	
	public boolean hasFullPartyAndGauntletBox() {
		int result = getAmountSelected();
		for (Pokemon p : team) {
			if (p != null) result++;
		}
		return result >= 10;
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
		if (nursery != null) {
			for (Pokemon p : nursery.getPokemon()) {
				if (p != null) result.add(p);
			}
			if (nursery.getEgg() != null) result.add(nursery.getEgg());
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

	public ArrayList<Entry> getBalls() {
		ArrayList<Entry> result = new ArrayList<>();
		for (Item i : Item.balls) {
			if (bag.count[i.getID()] > 0) result.add(bag.new Entry(i, bag.count[i.getID()]));
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
	        	gp.ui.showMessage(Item.breakString(p.nickname + " swapped its " + old.toString() + " for a " + p.item.toString() + "!", UI.MAX_TEXTBOX));
        	} else {
        		p.item = item;
        		gp.ui.showMessage(Item.breakString(p.nickname + " was given " + item.toString() + " to hold!", UI.MAX_TEXTBOX));
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
				if (p.currentHP == p.getStat(0)) {
	        		gp.ui.showMessage(p.nickname + " is already full HP!");
	        		return;
				} else if (p.isFainted()) {
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
	    			gp.ui.showMessage("It won't have any effect.");
	        		return;	    			
	        	} else {
	        		Status temp = p.status;
	        		p.status = Status.HEALTHY;
		        	gp.ui.showMessage(Item.breakString(p.nickname + " was cured of its " + temp.getName() + "!", UI.MAX_TEXTBOX));
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
	        		if (nuzlocke) {
	        			invalidateNuzlocke("Used " + item.toString() + " on " + p.getName() + ".");
	        		}
	        		if (item == Item.REVIVE) {
	        			p.currentHP = p.getStat(0) / 2;
	        		} else {
	        			p.currentHP = p.getStat(0);
	        		}
		        	gp.ui.showMessage(p.nickname + " was revived!");
	        	}
				break;
				
			// Mints
			case LONELY_MINT:
			case ADAMANT_MINT:
			case NAUGHTY_MINT:
			case BRAVE_MINT:
			case BOLD_MINT:
			case IMPISH_MINT:
			case LAX_MINT:
			case RELAXED_MINT:
			case MODEST_MINT:
			case MILD_MINT:
			case RASH_MINT:
			case QUIET_MINT:
			case CALM_MINT:
			case GENTLE_MINT:
			case CAREFUL_MINT:
			case SASSY_MINT:
			case TIMID_MINT:
			case HASTY_MINT:
			case JOLLY_MINT:
			case NAIVE_MINT:
			case SERIOUS_MINT:
				boolean changeable = p.canUseItem(item) == 1;
	    		if (!changeable) {
	        		gp.ui.showMessage("It won't have any effect.");
	        		return;
	        	} else {
	        		String natureOld = p.nat == null ? "null" : p.getNature();
		        	p.nat = item.getNature();
		        	p.setStats();
		        	gp.ui.showMessage(Item.breakString(p.nickname + "'s nature was changed from " + natureOld + " to " + p.getNature() + "!", UI.MAX_TEXTBOX));
	        	}
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
			case WATER_STONE:
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
	        		Task t = Task.addTask(Task.EVO_ITEM, "", p);
	        		t.counter = p.getEvolved(item);
	        		Task.addTask(Task.CLOSE, "");
	        	}
				break;
				
			// Ability Capsule
			case ABILITY_CAPSULE:
				boolean swappable = p.canUseItem(item) == 1;
	    		if (!swappable) {
	        		gp.ui.showMessage(Item.breakString(p.nickname + " only has one ability, it won't have any effect.", UI.MAX_TEXTBOX));
	        		return;
	        	} else {
	        		Ability oldAbility = p.ability;
	        		p.abilitySlot = 1 - p.abilitySlot;
	        		p.setAbility();
	        		gp.ui.showMessage(Item.breakString(p.nickname + "'s ability was swapped from " + oldAbility + " to " + p.ability + "!", UI.MAX_TEXTBOX));
	        	}
				break;
				
			// Ability Patch
			case ABILITY_PATCH:
				boolean swappable2 = p.canUseItem(item) == 1;
	    		if (!swappable2) {
	        		gp.ui.showMessage(Item.breakString(p.nickname + " doesn't have a hidden ability, it won't have any effect.", UI.MAX_TEXTBOX));
	        		return;
	        	} else {
	        		Ability oldAbility = p.ability;
	        		if (p.abilitySlot == 2) {
	        			p.abilitySlot = 0;
	        		} else {
	        			p.abilitySlot = 2;
	        		}
	        		p.setAbility();
	        		gp.ui.showMessage(Item.breakString(p.nickname + "'s ability was changed from " + oldAbility + " to " + p.ability + "!", UI.MAX_TEXTBOX));
	        	}
				break;
				
			// Bottle Caps
			case RUSTY_BOTTLE_CAP:
			case BOTTLE_CAP:
			case GOLD_BOTTLE_CAP:
				if (item == Item.BOTTLE_CAP) {
	        		gp.ui.currentPokemon = p;
	        		gp.ui.currentHeader = "Which IV to max out?";
	        		gp.ui.moveOption = -1;
	        		gp.ui.showIVOptions = true;
	        		return;
	        	} else if (item == Item.RUSTY_BOTTLE_CAP) {
	        		gp.ui.currentPokemon = p;
	        		gp.ui.currentHeader = "Which IV to set to 0?";
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
	        		gp.ui.showMessage(p.nickname + " successfully edged!");
	        	} else {
	        		gp.ui.showMessage(p.nickname + " is already edged.");
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
			case BUG_CRYSTAL:
			case DARK_CRYSTAL:
			case DRAGON_CRYSTAL:
			case ELECTRIC_CRYSTAL:
			case FIGHTING_CRYSTAL:
			case FIRE_CRYSTAL:
			case FLYING_CRYSTAL:
			case GALACTIC_CRYSTAL:
			case GHOST_CRYSTAL:
			case GRASS_CRYSTAL:
			case GROUND_CRYSTAL:
			case ICE_CRYSTAL:
			case LIGHT_CRYSTAL:
			case MAGIC_CRYSTAL:
			case POISON_CRYSTAL:
			case PSYCHIC_CRYSTAL:
			case ROCK_CRYSTAL:
			case STEEL_CRYSTAL:
			case WATER_CRYSTAL:
				PType type = PType.values()[item.getID() - 284];
				int[] optimalIVs = Pokemon.determineOptimalIVs(type);
				if (arrayEquals(optimalIVs, p.ivs)) {
					gp.ui.showMessage(Item.breakString(p.nickname + " already has the optimized IVs for the " + type.toString() + " power!", UI.MAX_TEXTBOX));
					return;
				} else {
					gp.ui.showMessage(Item.breakString(p.nickname + "'s IVs were optimized to the " + type.toString() + " power!", UI.MAX_TEXTBOX));
					p.ivs = optimalIVs;
					p.setStats();
				}
				break;
			case DAMAGE_KIT:
				if (p.currentHP > 1) {
	        		p.currentHP--;
				}
				return;
			case STATUS_KIT:
        		gp.ui.currentPokemon = p;
        		gp.ui.currentHeader = "Which status to inflict?";
        		gp.ui.moveOption = -1;
        		gp.ui.showStatusOptions = true;
				return;
			default:
				return;
			}
		}
		bag.remove(item);
		gp.ui.currentItems = getItems(gp.ui.currentPocket);
	}

	private boolean arrayEquals(int[] a1, int[] a2) {
		if (a1.length != a2.length) return false;
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) return false;
		}
		return true;
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
		while (starter >= 3) starter--;
		updateBoxes();
		updateBerries();
		updateNames();
		if (id == null) setID(gp.player.currentSave);
		if (blackjackStats == null) blackjackStats = new int[20];
		if (coinBadges == null) coinBadges = new boolean[12];
		if (puzzlesLocked == null) puzzlesLocked = new HashMap<>();
		version = VERSION;
	}

	private void updateBag() {
		Item[] items = Item.values();
		if (bag.itemList != null && items.length == bag.itemList.length && bag.p != null) return;
		
		int[] counts = bag.count;
		bag = new Bag(this);
		for (int i = 0; i < counts.length; i++) {
			if (i < bag.count.length) bag.count[i] = counts[i];
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
			if (p != null && !p.battled && p.item == Item.EXP_SHARE && !p.isFainted()) {
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
			
			Pokemon test = new Pokemon(i, 5, true, false);
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
		} else if (flag[1][2] && !flag[2][3]) {
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
	
	private void updateBerries() {
		if (statBerries == null) setupStatBerries();
		if (resistBerries == null || arrayContains(resistBerries, null)) setupResistBerries();
		if (crystals == null) setupCrystals();
	}

	private boolean arrayContains(Object[] array, Object object) {
		for (Object i : array) {
			if (i == object) return true;
		}
		return false;
	}

	public void setupStatBerries() {
		statBerries = new Item[7];
        int count = 0;
		for (int i = 275; i < 282; i++) {
			statBerries[count] = Item.getItem(i);
			count++;
		}
        List<Item> berryList = new ArrayList<>(Arrays.asList(statBerries));
        Collections.shuffle(berryList);
        statBerries = berryList.toArray(new Item[1]);
	}
	
	public void setupResistBerries() {
		resistBerries = new Item[20];
        int count = 0;
		for (int i = 232; i < 252; i++) {
			resistBerries[count] = Item.getItem(i);
			count++;
		}
        List<Item> berryList = new ArrayList<>(Arrays.asList(resistBerries));
        Collections.shuffle(berryList);
        resistBerries = berryList.toArray(new Item[1]);
	}
	
	public void setupCrystals() {
		crystals = new Item[19];
        int count = 0;
		for (int i = 285; i < 304; i++) {
			crystals[count] = Item.getItem(i);
			count++;
		}
        List<Item> crystalList = new ArrayList<>(Arrays.asList(crystals));
        Collections.shuffle(crystalList);
        crystals = crystalList.toArray(new Item[1]);
	}

	public void purchaseItem(UI ui, Item item, Pair<Item, Integer> p, int mode, Entity npc) {
		switch(mode) {
		case 0: // regular shop/market
			if (this.buy(item)) {
				if (item.isTM()) {
					ui.npc.inventory.remove(item);
				}
				if (item.isBall()) {
					if (++ballPurchase >= 10) {
						ballPurchase = 0;
						ui.premier++;
					};
				}
			} else {
				ui.showMessage("Not enough money!");
			}
			break;
		case 1: // prize shop for coins
			if (this.coins >= p.getSecond()) {
				this.bag.add(p.getFirst());
				this.coins -= p.getSecond();
			} else {
				ui.showMessage("Not enough coins!");
			}
			break;
		case 2: // prize shop for wins
			if (this.gamesWon >= p.getSecond()) {
				this.bag.add(p.getFirst());
				this.gamesWon -= p.getSecond();
				if (p.getFirst().isTM()) {
					NPC_Prize_Shop ps = (NPC_Prize_Shop) ui.npc;
					ps.winItems.remove(p);
				}
			} else {
				ui.showMessage("Not enough total wins!");
			}
		}
		
	}

	public int getMaxCoins() {
		return Math.min(999, coins);
	}
	
	public int getMaxBet() {
		return Math.min(100, coins);
	}
	
	public ArrayList<Pokemon> getOrderedTeam() {
		ArrayList<Pokemon> result = new ArrayList<>();
		for (Pokemon p : team) {
			if (p == null) continue;
			result.add(p);
		}
		
		return result;
	}

	public int getMaxParlayBet(boolean gauntlet, int activeBets) {
		if (activeBets == 0) return getMaxBet();
		
		return Math.min(Math.max(1, getBetCurrency(gauntlet) / activeBets), 100);
	}

	public void deleteInvalidMoves() {
		Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/deletedmoves.txt"));
		HashMap<Move, Move> moves = new HashMap<>();
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] moveS = line.split("\\|");
			Move m1 = Move.valueOf(moveS[0]);
			Move m2 = moveS.length > 1 ? Move.valueOf(moveS[1]) : null;
			moves.put(m1, m2);
		}
		
		for (Pokemon p : getAllPokemon()) {
			for (int i = 0; i < p.moveset.length; i++) {
				Moveslot m = p.moveset[i];
				if (m != null && moves.containsKey(m.move)) {
					Move newM = moves.get(m.move);
					p.moveset[i] = newM == null ? null : new Moveslot(moves.get(m.move));
				}
			}
			p.shiftMoveset();
		}
		
		scanner.close();
		
	}
	
	public void updateNames() {
		Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/names.txt"));
		ArrayList<String> names = new ArrayList<>();
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] namesArray = line.split("\\|");
			names.add(namesArray[0].trim());
		}
		
		for (Pokemon p : getAllPokemon()) {
			boolean notNicknamed = p.name.equals(p.nickname);
			p.setName(p.getName());
			if (notNicknamed && names.contains(p.nickname)) {
				p.nickname = p.name;
			}
		}
		
		scanner.close();
		
	}
	
	public Player clone(GamePanel gp) {
		Player newPlayer = new Player(gp);
		Pokemon[] newTeam = new Pokemon[this.team.length];
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i] != null) newTeam[i] = this.team[i].clone();
		}
		
//		public Pokemon[][] boxes;
//		public Pokemon[] gauntletBox;
//		public String[] boxLabels;
		newPlayer.numBattled = numBattled;
		newPlayer.posX = posX;
		newPlayer.posY = posY;
//		public Bag bag;
		newPlayer.badges = badges;
		newPlayer.starter = starter;
//		public int[] pokedex;
		newPlayer.currentMap = currentMap;
//		public boolean[] trainersBeat;
//		public boolean[][] itemsCollected;
//		public boolean[][] flag;
//		public boolean[] locations;
		newPlayer.random = random;
		newPlayer.ghost = ghost;
		newPlayer.steps = steps;
		newPlayer.fish = fish;
		newPlayer.repel = repel;
		newPlayer.surf = surf;
		newPlayer.lavasurf = lavasurf;
		newPlayer.visor = visor;
		newPlayer.grustCount = grustCount;
		newPlayer.scottItem = scottItem;
		newPlayer.ballPurchase = ballPurchase;
//		public Item[] resistBerries;
//		public Item[] statBerries;
//		public Item[] crystals;
		newPlayer.secondStarter = secondStarter;
		newPlayer.choiceChoice = choiceChoice;
		newPlayer.coins = coins;
		newPlayer.gamesWon = gamesWon;
		newPlayer.winStreak = winStreak;
//		public int[] blackjackStats; // 0: games played, 1: games won, 2: games pushed, 3: busts, 4: bust wins, 5: blackjacks 6: doubles 7: double wins, 8: coins won, 9: coins lost: 10: highest coin, 11: highest win streak, 12: lose streak, 13: high lose strk
//		public boolean[] coinBadges;
		newPlayer.currentBox = currentBox;
		newPlayer.version = version;
		newPlayer.id = id;
		newPlayer.amulet = amulet;
		
		newPlayer.team = newTeam;
		
		return newPlayer;
	}
	
	public void swapItem(Pokemon a, Pokemon b) {
		Item temp = a.item;
		a.item = b.item;
		b.item = temp;
	}

	public void evolve(Pokemon p, int counter, GamePanel gp) {
		String oldNickname = p.nickname;
		int oldID = p.id;
		p.evolve(counter);
		
        Task text = Task.createTask(Task.EVOLUTION, oldNickname + " evolved into " + p.name() + "!", p);
        text.counter = oldID;
        text.types = new PType[] {p.type1, p.type2};
        Task.insertTask(text, 0);
        pokedex[p.id] = 2;
        if (oldID == 129) {
        	Pokemon shedinja = p.clone();
        	catchPokemon(shedinja.evolve(131), false);
        }
        
        if (oldID == 263) {
        	for (Pokemon pok : team) {
        		pok.convertToNonMeteorForm();
        		pokedex[pok.id] = 2;
        	}
        }
        
        if (p.slot == 0) {
        	gp.battleUI.userHP = p.currentHP;
        	gp.battleUI.maxUserHP = p.getStat(0);
        }
        int i = p.checkMove(1, 0);
        p.checkMove(i, p.level);
        
        ArrayList<Task> tasks = gp.getTasks();
        tasks.removeIf(task -> task.type == Task.EVO && task.p.slot == p.slot && task.counter == oldID);
        for (int j = 1; j < tasks.size(); j++) {
        	Task t = tasks.get(j);
        	if (t.message.contains(oldNickname)) {
        		t.message = t.message.replace(oldNickname, p.nickname);
        	}
        }
	}
	
	public void setupPuzzles(GamePanel gp, int map) {
		if (map >= 191 && map <= 196) {
			gp.puzzleM.setup(true);
		}
		if (map >= 197 && map <= 202) {
			gp.puzzleM.setup(false);
			if (spike) {
				gp.eHandler.toggleSpikes(map);
				spike = true;
			}
		}
	}
	
	public int getBetCurrency(boolean gauntlet) {
		return gauntlet ? bag.getCount(Item.TEMPLE_ORB) : coins;
	}
	
	public void addBetCurrency(boolean gauntlet, int amt) {
		if (gauntlet) {
			bag.add(Item.TEMPLE_ORB, amt);
		} else {
			coins += amt;
		}
	}

	public boolean hasPokemonOverLevelCap() {
		int levelCap = Trainer.getLevelCap(badges);
		for (Pokemon p : team) {
			if (p != null && p.level > levelCap) return true;
		}
		return false;
	}
}
