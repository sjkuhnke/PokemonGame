package Swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Swing.Battle.JGradientButton;
import Swing.Field.Effect;
import Swing.Field.FieldEffect;

public class Pokemon implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7087373480262097882L;
	
	// id fields
	public int id;
	public String name;
	public String nickname;
	
	// stat fields
	public int[] baseStats;
	public int[] stats;
	private int level;
	public int[] statStages;
	private int[] ivs;
	public double[] nature;
	public double weight;
	public int happiness;
	
	// type fields
	public PType type1;
	public PType type2;
	
	// ability field
	public Ability ability;
	public int abilitySlot;
	
	// move fields
	public Node[] movebank;
	public Move[] moveset;
	
	// status fields
	public Status status;
	public ArrayList<Status> vStatuses;
	
	// xp fields
	public int exp;
	public int expMax;
	
	// hp fields
	public int currentHP;
	public boolean fainted;
	
	// counter fields
	private int confusionCounter;
	private int sleepCounter;
	private int perishCount;
	private int magCount;
	public int moveMultiplier;
	private int spunCount;
	private int outCount;
	private int rollCount;
	private int encoreCount;
	private int tauntCount;
	private int tormentCount;
	private int toxic;
	
	// boolean fields
	public boolean trainerOwned;
	public boolean impressive;
	public boolean battled;
	public boolean success;
	
	// battle fields
	public Move lastMoveUsed;
	private double trainer;
	public int slot;
	
	
	public Pokemon(int i, int l, boolean o, boolean t) {
		id = i;
		name = getName();
		nickname = name;
		
		baseStats = new int[6];
		stats = new int[6];
		ivs = new int[6];
		level = l;
		statStages = new int[7];
		
		setBaseStats();
		for (int j = 0; j < 6; j++) { ivs[j] = (int) (Math.random() * 32); }
		setNature();
		getStats();
		setType();
		if (t) {
			setAbility(0);
		} else {
			abilitySlot = (int)Math.round(Math.random());
			setAbility(abilitySlot);
		}
		this.weight = setWeight();
		
		expMax = level * 2;
		exp = 0;
		
		currentHP = this.getStat(0);
		moveset = new Move[4];
		setMoveBank();
		setMoves();
		moveMultiplier = 1;
		
		status = Status.HEALTHY;
		vStatuses = new ArrayList<Status>();
		
		trainerOwned = o;
		impressive = true;
		trainer = 1;
		if (t) {
			trainer = 1.5;
			ivs = new int[] {31, 31, 31, 31, 31, 31};
		}
		
		happiness = 70;
		
	}
	
	public BufferedImage getSprite() {
		BufferedImage image = null;
		
		String imageName = id + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/sprites/" + imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/001.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
	public BufferedImage getSprite(int id) {
		BufferedImage image = null;
		
		String imageName = id + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/sprites/" + imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/001.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}


	public void setMoves() {
		int index = 0;
		for (int i = 0; i < level && i < movebank.length; i++) {
			Node node = movebank[i];
	        while (node != null) {
	            moveset[index] = node.data;
	            index++;
	            if (index >= 4) {
	                index = 0;
	            }
	            node = node.next;
	        }
		}
	}
	
	public Move randomMove() {
	    ArrayList<Move> validMoves = new ArrayList<>();

	    // Add all non-null moves to the validMoves list
	    for (Move move : moveset) {
	        if (move != null) {
	            validMoves.add(move);
	        }
	    }

	    // Pick a random move from the validMoves list
	    Random rand = new Random();
	    int index = rand.nextInt(validMoves.size());
	    return validMoves.get(index);
	}
	
	public Move bestMove(Pokemon foe, Field field, boolean first) {
	    ArrayList<Move> validMoves = new ArrayList<>();

	    // Add all non-null moves to the validMoves list
	    for (Move move : moveset) {
	        if (move != null) {
	            validMoves.add(move);
	        }
	    }

	    ArrayList<Move> statusMoves = new ArrayList<>();
	    ArrayList<Move> damagingMoves = new ArrayList<>();

	    // Split the moves into status and damaging moves
	    for (Move move : validMoves) {
	        if (move.cat == 2) {
	            statusMoves.add(move);
	        } else {
	            damagingMoves.add(move);
	        }
	    }

	    // Determine if a status move should be used
	    boolean useStatusMove = false;
	    if (!statusMoves.isEmpty()) {
	        int randomChance = (int) (Math.random() * 4); // 25% chance
	        useStatusMove = randomChance == 0;
	    }

	    if (useStatusMove && !statusMoves.isEmpty()) {
	        // If a status move should be used, randomly select one
	        int randomIndex = (int) (Math.random() * statusMoves.size());
	        return statusMoves.get(randomIndex);
	    } else {
	        // Otherwise, find the move with the highest damage
	        int maxDamage = 0;
	        ArrayList<Move> bestMoves = new ArrayList<>();

	        for (Move move : damagingMoves) {
	            int damage = calcWithTypes(foe, move, first, field);
	            if (damage > foe.currentHP) damage = foe.currentHP;
	            if (damage > maxDamage) {
	                maxDamage = damage;
	                bestMoves.clear();
	                bestMoves.add(move);
	            } else if (damage == maxDamage) {
	                bestMoves.add(move);
	            }
	        }

	        // If all valid moves have the same damage, randomly select one of them
	        if (bestMoves.isEmpty()) {
	            // Fallback: Choose a random status move
	            int randomIndex = (int) (Math.random() * statusMoves.size());
	            return statusMoves.get(randomIndex);
	        } else if (bestMoves.size() > 1) {
	            int randomIndex = (int) (Math.random() * bestMoves.size());
	            return bestMoves.get(randomIndex);
	        }

	        // Otherwise, return the move with the highest damage
	        return bestMoves.get(0);
	    }
	}



	public Pokemon(int i, Pokemon pokemon) {
		id = i;
		name = getName();
		nickname = pokemon.nickname;
		if (pokemon.nickname == pokemon.name) nickname = name;
		
		baseStats = new int[6];
		stats = new int[6];
		level = pokemon.level;
		statStages = new int[7];
		ivs = pokemon.ivs;
		nature = pokemon.nature;
		
		setBaseStats();
		getStats();
		setType();
		setAbility(pokemon.abilitySlot);
		this.weight = setWeight();
		
		this.slot = pokemon.slot;
		
		expMax = level * 2;
		exp = 0;
		
		currentHP = this.getStat(0);
		setMoveBank();
		moveset = pokemon.moveset;
		
		status = Status.HEALTHY;
		vStatuses = new ArrayList<Status>();
		
		trainerOwned = true;
		impressive = true;
		trainer = 1;
		
		happiness = pokemon.happiness;
	}
	
	public boolean isFainted() {
		return this.fainted;
	}
	
	public void setType() {
		setType(this.id);
	}
	
	public PType[] getType(int id) {
		PType[] result = new PType[2];
		Pokemon temp = new Pokemon(id, 1, false, false);
		
		result[0] = temp.type1;
		result[1] = temp.type2;
		
		return result;
	}

	public void setType(int id) {
		if (id == 1) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == 2) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ROCK;
		} else if (id == 3) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ROCK;
		} else if (id == 4) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == 5) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == 6) {
			this.type1 = PType.FIRE;
			this.type2 = PType.STEEL;
		} else if (id == 7) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == 8) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == 9) {
			this.type1 = PType.WATER;
			this.type2 = PType.FIGHTING;
		} else if (id == 10) {
			this.type1 = PType.LIGHT;
			this.type2 = PType.FLYING;
		} else if (id == 11) {
			this.type1 = PType.LIGHT;
			this.type2 = PType.FLYING;
		} else if (id == 12) {
			this.type1 = PType.LIGHT;
			this.type2 = PType.FLYING;
		} else if (id == 13) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.FLYING;
		} else if (id == 14) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.FLYING;
		} else if (id == 15) {
			this.type1 = PType.STEEL;
			this.type2 = PType.FLYING;
		} else if (id == 16) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == 17) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == 18) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.ROCK;
		} else if (id == 19) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == 20) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.MAGIC;
		} else if (id == 21) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.GALACTIC;
		} else if (id == 22) {
			this.type1 = PType.BUG;
			this.type2 = null;
		} else if (id == 23) {
			this.type1 = PType.BUG;
			this.type2 = null;
		} else if (id == 24) {
			this.type1 = PType.BUG;
			this.type2 = null;
		} else if (id == 25) {
			this.type1 = PType.BUG;
			this.type2 = PType.ROCK;
		} else if (id == 26) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == 27) {
			this.type1 = PType.GRASS;
			this.type2 = PType.FIGHTING;
		} else if (id == 28) {
			this.type1 = PType.GRASS;
			this.type2 = PType.DRAGON;
		} else if (id == 29) {
			this.type1 = PType.GRASS;
			this.type2 = PType.POISON;
		} else if (id == 30) {
			this.type1 = PType.GRASS;
			this.type2 = PType.POISON;
		} else if (id == 31) {
			this.type1 = PType.GRASS;
			this.type2 = PType.POISON;
		} else if (id == 32) {
			this.type1 = PType.BUG;
			this.type2 = PType.GRASS;
		} else if (id == 33) {
			this.type1 = PType.BUG;
			this.type2 = PType.GRASS;
		} else if (id == 34) {
			this.type1 = PType.BUG;
			this.type2 = PType.GRASS;
		} else if (id == 35) {
			this.type1 = PType.BUG;
			this.type2 = null;
		} else if (id == 36) {
			this.type1 = PType.BUG;
			this.type2 = PType.ELECTRIC;
		} else if (id == 37) {
			this.type1 = PType.BUG;
			this.type2 = PType.ELECTRIC;
		} else if (id == 38) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == 39) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == 40) {
			this.type1 = PType.GRASS;
			this.type2 = PType.LIGHT;
		} else if (id == 41) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == 42) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == 43) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == 44) {
			this.type1 = PType.WATER;
			this.type2 = PType.GRASS;
		} else if (id == 45) {
			this.type1 = PType.WATER;
			this.type2 = PType.GRASS;
		} else if (id == 46) {
			this.type1 = PType.WATER;
			this.type2 = PType.GRASS;
		} else if (id == 47) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.LIGHT;
		} else if (id == 48) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == 49) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == 50) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == 51) {
			this.type1 = PType.ROCK;
			this.type2 = PType.LIGHT;
		} else if (id == 52) {
			this.type1 = PType.ROCK;
			this.type2 = null;
		} else if (id == 53) {
			this.type1 = PType.ROCK;
			this.type2 = PType.PSYCHIC;
		} else if (id == 54) {
			this.type1 = PType.ROCK;
			this.type2 = PType.DRAGON;
		} else if (id == 55) {
			this.type1 = PType.ROCK;
			this.type2 = null;
		} else if (id == 56) {
			this.type1 = PType.ROCK;
			this.type2 = PType.DARK;
		} else if (id == 57) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.GROUND;
		} else if (id == 58) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.FLYING;
		} else if (id == 59) {
			this.type1 = PType.ICE;
			this.type2 = PType.PSYCHIC;
		} else if (id == 60) {
			this.type1 = PType.ICE;
			this.type2 = PType.PSYCHIC;
		} else if (id == 61) {
			this.type1 = PType.ICE;
			this.type2 = PType.MAGIC;
		} else if (id == 62) {
			this.type1 = PType.ICE;
			this.type2 = PType.BUG;
		} else if (id == 63) {
			this.type1 = PType.ICE;
			this.type2 = PType.BUG;
		} else if (id == 64) {
			this.type1 = PType.GROUND;
			this.type2 = PType.ICE;
		} else if (id == 65) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.ICE;
		} else if (id == 66) {
			this.type1 = PType.ROCK;
			this.type2 = PType.LIGHT;
		} else if (id == 67) {
			this.type1 = PType.ROCK;
			this.type2 = PType.FIGHTING;
		} else if (id == 68) {
			this.type1 = PType.ICE;
			this.type2 = PType.WATER;
		} else if (id == 69) {
			this.type1 = PType.ICE;
			this.type2 = PType.WATER;
		} else if (id == 70) {
			this.type1 = PType.ICE;
			this.type2 = PType.WATER;
		} else if (id == 71) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == 72) {
			this.type1 = PType.WATER;
			this.type2 = PType.LIGHT;
		} else if (id == 73) {
			this.type1 = PType.BUG;
			this.type2 = PType.DARK;
		} else if (id == 74) {
			this.type1 = PType.BUG;
			this.type2 = PType.DARK;
		} else if (id == 75) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.MAGIC;
		} else if (id == 76) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.MAGIC;
		} else if (id == 77) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.MAGIC;
		} else if (id == 78) {
			this.type1 = PType.WATER;
			this.type2 = PType.PSYCHIC;
		} else if (id == 79) {
			this.type1 = PType.WATER;
			this.type2 = PType.PSYCHIC;
		} else if (id == 80) {
			this.type1 = PType.GRASS;
			this.type2 = PType.PSYCHIC;
		} else if (id == 81) {
			this.type1 = PType.GRASS;
			this.type2 = PType.PSYCHIC;
		} else if (id == 82) {
			this.type1 = PType.PSYCHIC;
			this.type2 = null;
		} else if (id == 83) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.STEEL;
		} else if (id == 84) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.STEEL;
		} else if (id == 85) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.LIGHT;
		} else if (id == 86) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.LIGHT;
		} else if (id == 87) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.LIGHT;
		} else if (id == 88) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.FIGHTING;
		} else if (id == 89) {
			this.type1 = PType.LIGHT;
			this.type2 = PType.NORMAL;
		} else if (id == 90) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.DARK;
		} else if (id == 91) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.DARK;
		} else if (id == 92) {
			this.type1 = PType.FIRE;
			this.type2 = PType.NORMAL;
		} else if (id == 93) {
			this.type1 = PType.FIRE;
			this.type2 = PType.NORMAL;
		} else if (id == 94) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == 95) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FIGHTING;
		} else if (id == 96) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FIGHTING;
		} else if (id == 97) {
			this.type1 = PType.FIRE;
			this.type2 = PType.GHOST;
		} else if (id == 98) {
			this.type1 = PType.FIRE;
			this.type2 = PType.GROUND;
		} else if (id == 99) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FLYING;
		} else if (id == 100) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DRAGON;
		} else if (id == 101) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ROCK;
		} else if (id == 102) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ROCK;
		} else if (id == 103) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ROCK;
		} else if (id == 104) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DARK;
		} else if (id == 105) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DARK;
		} else if (id == 106) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == 107) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.FIRE;
		} else if (id == 108) {
			this.type1 = PType.FIRE;
			this.type2 = PType.LIGHT;
		} else if (id == 109) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == 110) {
			this.type1 = PType.FIRE;
			this.type2 = PType.LIGHT;
		} else if (id == 111) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == 112) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.STEEL;
		} else if (id == 113) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.STEEL;
		} else if (id == 114) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.GROUND;
		} else if (id == 115) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.GROUND;
		} else if (id == 116) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.GROUND;
		} else if (id == 117) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ELECTRIC;
		} else if (id == 118) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ELECTRIC;
		} else if (id == 119) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ELECTRIC;
		} else if (id == 120) {
			this.type1 = PType.STEEL;
			this.type2 = PType.NORMAL;
		} else if (id == 121) {
			this.type1 = PType.STEEL;
			this.type2 = PType.LIGHT;
		} else if (id == 122) {
			this.type1 = PType.STEEL;
			this.type2 = PType.MAGIC;
		} else if (id == 123) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.NORMAL;
		} else if (id == 124) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.LIGHT;
		} else if (id == 125) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.MAGIC;
		} else if (id == 126) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.NORMAL;
		} else if (id == 127) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.LIGHT;
		} else if (id == 128) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.MAGIC;
		} else if (id == 129) {
			this.type1 = PType.BUG;
			this.type2 = PType.GROUND;
		} else if (id == 130) {
			this.type1 = PType.BUG;
			this.type2 = PType.FLYING;
		} else if (id == 131) {
			this.type1 = PType.BUG;
			this.type2 = PType.GHOST;
		} else if (id == 132) {
			this.type1 = PType.WATER;
			this.type2 = PType.GHOST;
		} else if (id == 133) {
			this.type1 = PType.WATER;
			this.type2 = PType.GHOST;
		} else if (id == 134) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == 135) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == 136) {
			this.type1 = PType.WATER;
			this.type2 = PType.DRAGON;
		} else if (id == 137) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == 138) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == 139) {
			this.type1 = PType.WATER;
			this.type2 = PType.GALACTIC;
		} else if (id == 140) {
			this.type1 = PType.WATER;
			this.type2 = PType.GALACTIC;
		} else if (id == 141) {
			this.type1 = PType.DARK;
			this.type2 = PType.WATER;
		} else if (id == 142) {
			this.type1 = PType.DARK;
			this.type2 = PType.FLYING;
		} else if (id == 143) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.DRAGON;
		} else if (id == 144) {
			this.type1 = PType.WATER;
			this.type2 = PType.DRAGON;
		} else if (id == 145) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.DRAGON;
		} else if (id == 146) {
			this.type1 = PType.WATER;
			this.type2 = PType.ROCK;
		} else if (id == 147) {
			this.type1 = PType.WATER;
			this.type2 = PType.ROCK;
		} else if (id == 148) {
			this.type1 = PType.WATER;
			this.type2 = PType.GROUND;
		} else if (id == 149) {
			this.type1 = PType.WATER;
			this.type2 = PType.GROUND;
		} else if (id == 150) {
			this.type1 = PType.WATER;
			this.type2 = PType.MAGIC;
		} else if (id == 151) {
			this.type1 = PType.POISON;
			this.type2 = null;
		} else if (id == 152) {
			this.type1 = PType.POISON;
			this.type2 = null;
		} else if (id == 153) {
			this.type1 = PType.POISON;
			this.type2 = PType.FLYING;
		} else if (id == 154) {
			this.type1 = PType.POISON;
			this.type2 = PType.FLYING;
		} else if (id == 155) {
			this.type1 = PType.POISON;
			this.type2 = PType.FLYING;
		} else if (id == 156) {
			this.type1 = PType.GHOST;
			this.type2 = null;
		} else if (id == 157) {
			this.type1 = PType.GHOST;
			this.type2 = PType.STEEL;
		} else if (id == 158) {
			this.type1 = PType.GHOST;
			this.type2 = PType.DARK;
		} else if (id == 159) {
			this.type1 = PType.GHOST;
			this.type2 = PType.DARK;
		} else if (id == 160) {
			this.type1 = PType.GHOST;
			this.type2 = PType.POISON;
		} else if (id == 161) {
			this.type1 = PType.GHOST;
			this.type2 = PType.ROCK;
		} else if (id == 162) {
			this.type1 = PType.GHOST;
			this.type2 = PType.ROCK;
		} else if (id == 163) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == 164) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == 165) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == 166) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == 167) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == 168) {
			this.type1 = PType.GROUND;
			this.type2 = PType.FIGHTING;
		} else if (id == 169) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == 170) {
			this.type1 = PType.GROUND;
			this.type2 = PType.LIGHT;
		} else if (id == 171) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == 172) {
			this.type1 = PType.GROUND;
			this.type2 = PType.STEEL;
		} else if (id == 173) {
			this.type1 = PType.GROUND;
			this.type2 = PType.DRAGON;
		} else if (id == 174) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.LIGHT;
		} else if (id == 175) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.LIGHT;
		} else if (id == 176) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.LIGHT;
		} else if (id == 177) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.FLYING;
		} else if (id == 178) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.FLYING;
		} else if (id == 179) {
			this.type1 = PType.DARK;
			this.type2 = null;
		} else if (id == 180) {
			this.type1 = PType.DARK;
			this.type2 = null;
		} else if (id == 181) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == 182) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == 183) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == 184) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.GALACTIC;
		} else if (id == 185) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.GALACTIC;
		} else if (id == 186) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.GALACTIC;
		} else if (id == 187) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.LIGHT;
		} else if (id == 188) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.LIGHT;
		} else if (id == 189) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.LIGHT;
		} else if (id == 190) {
			this.type1 = PType.GALACTIC;
			this.type2 = null;
		} else if (id == 191) {
			this.type1 = PType.GALACTIC;
			this.type2 = null;
		} else if (id == 192) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.FIGHTING;
		} else if (id == 193) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.ICE;
		} else if (id == 194) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.ICE;
		} else if (id == 195) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.ROCK;
		} else if (id == 196) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.ROCK;
		} else if (id == 197) {
			this.type1 = PType.GHOST;
			this.type2 = PType.ELECTRIC;
		} else if (id == 198) {
			this.type1 = PType.GHOST;
			this.type2 = PType.ELECTRIC;
		} else if (id == 199) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.ELECTRIC;
		} else if (id == 200) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.ELECTRIC;
		} else if (id == 201) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.ELECTRIC;
		} else if (id == 202) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ELECTRIC;
		} else if (id == 203) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ELECTRIC;
		} else if (id == 204) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ELECTRIC;
		} else if (id == 205) {
			this.type1 = PType.ROCK;
			this.type2 = PType.ELECTRIC;
		} else if (id == 206) {
			this.type1 = PType.ROCK;
			this.type2 = PType.ELECTRIC;
		} else if (id == 207) {
			this.type1 = PType.ROCK;
			this.type2 = PType.ELECTRIC;
		} else if (id == 208) {
			this.type1 = PType.LIGHT;
			this.type2 = PType.ELECTRIC;
		} else if (id == 209) {
			this.type1 = PType.WATER;
			this.type2 = PType.ELECTRIC;
		} else if (id == 210) {
			this.type1 = PType.WATER;
			this.type2 = PType.ELECTRIC;
		} else if (id == 211) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.ELECTRIC;
		} else if (id == 212) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.ELECTRIC;
		} else if (id == 213) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.DARK;
		} else if (id == 214) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.DARK;
		} else if (id == 215) {
			this.type1 = PType.GROUND;
			this.type2 = PType.GHOST;
		} else if (id == 216) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.GHOST;
		} else if (id == 217) {
			this.type1 = PType.GROUND;
			this.type2 = PType.DARK;
		} else if (id == 218) {
			this.type1 = PType.GROUND;
			this.type2 = PType.DARK;
		} else if (id == 219) {
			this.type1 = PType.GROUND;
			this.type2 = PType.DARK;
		} else if (id == 220) {
			this.type1 = PType.POISON;
			this.type2 = PType.DARK;
		} else if (id == 221) {
			this.type1 = PType.POISON;
			this.type2 = PType.DARK;
		} else if (id == 222) {
			this.type1 = PType.POISON;
			this.type2 = PType.DARK;
		} else if (id == 223) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DARK;
		} else if (id == 224) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.DARK;
		} else if (id == 225) {
			this.type1 = PType.FIGHTING;
			this.type2 = PType.DARK;
		} else if (id == 226) {
			this.type1 = PType.FLYING;
			this.type2 = PType.GHOST;
		} else if (id == 227) {
			this.type1 = PType.FLYING;
			this.type2 = PType.GHOST;
		} else if (id == 228) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == 229) {
			this.type1 = PType.DARK;
			this.type2 = PType.MAGIC;
		} else if (id == 230) {
			this.type1 = PType.ICE;
			this.type2 = PType.LIGHT;
		} else if (id == 231) {
			this.type1 = PType.ICE;
			this.type2 = PType.MAGIC;
		} else if (id == 232) {
			this.type1 = PType.POISON;
			this.type2 = PType.MAGIC;
		} else if (id == 233) {
			this.type1 = PType.ICE;
			this.type2 = PType.DRAGON;
		} else if (id == 234) {
			this.type1 = PType.PSYCHIC;
			this.type2 = PType.DRAGON;
		} else if (id == 235) {
			this.type1 = PType.GALACTIC;
			this.type2 = PType.DRAGON;
		} else if (id == 236) {
			this.type1 = PType.FIRE;
			this.type2 = PType.GALACTIC;
		} else if (id == 237) {
			this.type1 = PType.WATER;
			this.type2 = PType.DRAGON;
		} else if (id == 238) {
			this.type1 = PType.DARK;
			this.type2 = PType.FIGHTING;
		}else if (id == -1) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -2) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -3) {
			this.type1 = PType.GRASS;
			this.type2 = PType.ROCK;
		} else if (id == -4) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -5) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -6) {
			this.type1 = PType.FIRE;
			this.type2 = PType.ROCK;
		} else if (id == -7) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -8) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -9) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -10) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -11) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -12) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -13) {
			this.type1 = PType.GRASS;
			this.type2 = PType.FIGHTING;
		} else if (id == -14) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == -15) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == -16) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == -17) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == -18) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == -19) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == -20) {
			this.type1 = PType.ROCK;
			this.type2 = PType.GROUND;
		} else if (id == -21) {
			this.type1 = PType.ROCK;
			this.type2 = null;
		} else if (id == -22) {
			this.type1 = PType.ROCK;
			this.type2 = null;
		} else if (id == -23) {
			this.type1 = PType.ROCK;
			this.type2 = PType.FLYING;
		} else if (id == -24) {
			this.type1 = PType.WATER;
			this.type2 = PType.DARK;
		} else if (id == -25) {
			this.type1 = PType.WATER;
			this.type2 = PType.ELECTRIC;
		} else if (id == -26) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -27) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -28) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -29) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.FLYING;
		} else if (id == -30) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.FLYING;
		} else if (id == -31) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -32) {
			this.type1 = PType.GRASS;
			this.type2 = PType.FLYING;
		} else if (id == -33) {
			this.type1 = PType.BUG;
			this.type2 = PType.POISON;
		} else if (id == -34) {
			this.type1 = PType.BUG;
			this.type2 = PType.POISON;
		} else if (id == -35) {
			this.type1 = PType.BUG;
			this.type2 = null;
		} else if (id == -36) {
			this.type1 = PType.BUG;
			this.type2 = PType.POISON;
		} else if (id == -37) {
			this.type1 = PType.GHOST;
			this.type2 = PType.POISON;
		} else if (id == -38) {
			this.type1 = PType.GHOST;
			this.type2 = null;
		} else if (id == -39) {
			this.type1 = PType.GHOST;
			this.type2 = PType.STEEL;
		} else if (id == -40) {
			this.type1 = PType.GHOST;
			this.type2 = PType.DARK;
		} else if (id == -41) {
			this.type1 = PType.GHOST;
			this.type2 = PType.DARK;
		} else if (id == -42) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -43) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.ELECTRIC;
		} else if (id == -44) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -45) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.STEEL;;
		} else if (id == -46) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -47) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -48) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == -49) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == -50) {
			this.type1 = PType.BUG;
			this.type2 = PType.FIGHTING;
		} else if (id == -51) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == -52) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == -53) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == -54) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == -55) {
			this.type1 = PType.POISON;
			this.type2 = null;
		} else if (id == -56) {
			this.type1 = PType.POISON;
			this.type2 = null;
		} else if (id == -57) {
			this.type1 = PType.POISON;
			this.type2 = PType.FLYING;
		} else if (id == -58) {
			this.type1 = PType.POISON;
			this.type2 = PType.FLYING;
		} else if (id == -59) {
			this.type1 = PType.POISON;
			this.type2 = PType.GRASS;
		} else if (id == -60) {
			this.type1 = PType.POISON;
			this.type2 = PType.GRASS;
		} else if (id == -61) {
			this.type1 = PType.WATER;
			this.type2 = PType.POISON;
		} else if (id == -62) {
			this.type1 = PType.WATER;
			this.type2 = PType.POISON;
		} else if (id == -63) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -64) {
			this.type1 = PType.WATER;
			this.type2 = PType.FIGHTING;
		} else if (id == -65) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -66) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -67) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -68) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -69) {
			this.type1 = PType.WATER;
			this.type2 = PType.DARK;
		} else if (id == -70) {
			this.type1 = PType.WATER;
			this.type2 = PType.DRAGON;
		} else if (id == -71) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == -72) {
			this.type1 = PType.GROUND;
			this.type2 = null;
		} else if (id == -73) {
			this.type1 = PType.GROUND;
			this.type2 = PType.FIGHTING;
		} else if (id == -74) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -75) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FIGHTING;
		} else if (id == -76) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FIGHTING;
		} else if (id == -77) {
			this.type1 = PType.FIRE;
			this.type2 = PType.GROUND;
		} else if (id == -78) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FLYING;
		} else if (id == -79) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DRAGON;
		} else if (id == -80) {
			this.type1 = PType.FIRE;
			this.type2 = PType.STEEL;
		} else if (id == -81) {
			this.type1 = PType.FIRE;
			this.type2 = PType.STEEL;
		} else if (id == -82) {
			this.type1 = PType.WATER;
			this.type2 = PType.FLYING;
		} else if (id == -83) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.FLYING;
		} else if (id == -84) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -85) {
			this.type1 = PType.FIRE;
			this.type2 = PType.FLYING;
		} else if (id == -86) {
			this.type1 = PType.DARK;
			this.type2 = PType.BUG;
		} else if (id == -87) {
			this.type1 = PType.DARK;
			this.type2 = PType.BUG;
		} else if (id == -88) {
			this.type1 = PType.BUG;
			this.type2 = PType.STEEL;
		} else if (id == -89) {
			this.type1 = PType.DARK;
			this.type2 = null;
		} else if (id == -90) {
			this.type1 = PType.DARK;
			this.type2 = null;
		} else if (id == -91) {
			this.type1 = PType.DARK;
			this.type2 = PType.MAGIC;
		} else if (id == -92) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -93) {
			this.type1 = PType.GHOST;
			this.type2 = PType.FIRE;
		} else if (id == -94) {
			this.type1 = PType.GHOST;
			this.type2 = PType.FIRE;
		} else if (id == -95) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.STEEL;
		} else if (id == -96) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.STEEL;
		} else if (id == -97) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == -98) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == -99) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == -100) {
			this.type1 = PType.DRAGON;
			this.type2 = null;
		} else if (id == -101) {
			this.type1 = PType.DRAGON;
			this.type2 = null;
		} else if (id == -102) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.FLYING;
		} else if (id == -103) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.FIGHTING;
		} else if (id == -104) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.FLYING;
		} else if (id == -105) {
			this.type1 = PType.DRAGON;
			this.type2 = PType.FIRE;
		} else if (id == -106) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -107) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -108) {
			this.type1 = PType.MAGIC;
			this.type2 = PType.FIRE;
		} else if (id == -109) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -110) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -111) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -112) {
			this.type1 = PType.NORMAL;
			this.type2 = null;
		} else if (id == -113) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -114) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -115) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -116) {
			this.type1 = PType.POISON;
			this.type2 = null;
		} else if (id == -117) {
			this.type1 = PType.ELECTRIC;
			this.type2 = null;
		} else if (id == -118) {
			this.type1 = PType.ROCK;
			this.type2 = null;
		} else if (id == -119) {
			this.type1 = PType.DARK;
			this.type2 = null;
		} else if (id == -120) {
			this.type1 = PType.STEEL;
			this.type2 = null;
		} else if (id == -121) {
			this.type1 = PType.FIGHTING;
			this.type2 = null;
		} else if (id == -122) {
			this.type1 = PType.DRAGON;
			this.type2 = null;
		} else if (id == -123) {
			this.type1 = PType.MAGIC;
			this.type2 = null;
		} else if (id == -124) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -125) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -126) {
			this.type1 = PType.GRASS;
			this.type2 = null;
		} else if (id == -127) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -128) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -129) {
			this.type1 = PType.FIRE;
			this.type2 = null;
		} else if (id == -130) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -131) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -132) {
			this.type1 = PType.WATER;
			this.type2 = null;
		} else if (id == -133) {
			this.type1 = PType.ELECTRIC;
			this.type2 = PType.DRAGON;
		} else if (id == -134) {
			this.type1 = PType.FIRE;
			this.type2 = PType.DRAGON;
		} else if (id == -135) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.MAGIC;
		} else if (id == -136) {
			this.type1 = PType.BUG;
			this.type2 = PType.MAGIC;
		} else if (id == -137) {
			this.type1 = PType.FLYING;
			this.type2 = PType.MAGIC;
		} else if (id == -138) {
			this.type1 = PType.NORMAL;
			this.type2 = PType.MAGIC;
		} else if (id == -139) {
			this.type1 = PType.BUG;
			this.type2 = PType.MAGIC;
		} else if (id == -140) {
			this.type1 = PType.FLYING;
			this.type2 = PType.MAGIC;
		} else if (id == -141) {
			this.type1 = PType.POISON;
			this.type2 = PType.FIRE;
		} else if (id == -142) {
			this.type1 = PType.POISON;
			this.type2 = PType.WATER;
		} else if (id == -143) {
			this.type1 = PType.POISON;
			this.type2 = PType.FIRE;
		} else if (id == -144) {
			this.type1 = PType.POISON;
			this.type2 = PType.WATER;
		}
		
	}
	
	public PType getType1(int id) {
		Pokemon test = new Pokemon(1, 1, false, false);
		test.setType(id);
		return test.type1;
	}
	
	public PType getType2(int id) {
		Pokemon test = new Pokemon(1, 1, false, false);
		test.setType(id);
		return test.type2;
	}
	
	public String getName() {
		return getName(this.id);
	}
	
	public String getName(int id) {
		if (id == 1) { return "Twigle";
		} else if (id == 2) { return "Torgged";
		} else if (id == 3) { return "Tortugis";
		} else if (id == 4) { return "Lagma";
		} else if (id == 5) { return "Maguide";
		} else if (id == 6) { return "Magron";
		} else if (id == 7) { return "Lizish";
		} else if (id == 8) { return "Iguaton";
		} else if (id == 9) { return "Dragave";
		} else if (id == 10) { return "Hummingspark";
		} else if (id == 11) { return "Flashclaw";
		} else if (id == 12) { return "Magestiflash";
		} else if (id == 13) { return "Pigo";
		} else if (id == 14) { return "Pigonat";
		} else if (id == 15) { return "Pigoga";
		} else if (id == 16) { return "Hammo";
		} else if (id == 17) { return "HammyBoy";
		} else if (id == 18) { return "Hamthorno";
		} else if (id == 19) { return "Sheabear";
		} else if (id == 20) { return "Dualbear";
		} else if (id == 21) { return "Spacebear";
		} else if (id == 22) { return "Bealtle";
		} else if (id == 23) { return "Centatle";
		} else if (id == 24) { return "Curlatoral";
		} else if (id == 25) { return "Millistone";
		} else if (id == 26) { return "Sapwin";
		} else if (id == 27) { return "Treewin";
		} else if (id == 28) { return "Winagrow";
		} else if (id == 29) { return "Budew";
		} else if (id == 30) { return "Roselia";
		} else if (id == 31) { return "Roserade";
		} else if (id == 32) { return "Sewaddle";
		} else if (id == 33) { return "Swadloon";
		} else if (id == 34) { return "Leavanny";
		} else if (id == 35) { return "Grubbin";
		} else if (id == 36) { return "Charjabug";
		} else if (id == 37) { return "Vikavolt";
		} else if (id == 38) { return "Busheep";
		} else if (id == 39) { return "Ramant";
		} else if (id == 40) { return "Bushewe";
		} else if (id == 41) { return "Bugik";
		} else if (id == 42) { return "Swordik";
		} else if (id == 43) { return "Ninjakik";
		} else if (id == 44) { return "Lotad";
		} else if (id == 45) { return "Lombre";
		} else if (id == 46) { return "Ludicolo";
		} else if (id == 47) { return "Bluebunn";
		} else if (id == 48) { return "Rocky";
		} else if (id == 49) { return "Boulder";
		} else if (id == 50) { return "Blaster";
		} else if (id == 51) { return "Crystallor";
		} else if (id == 52) { return "Carinx";
		} else if (id == 53) { return "Carinator";
		} else if (id == 54) { return "Cairnasaur";
		} else if (id == 55) { return "Pebblepup";
		} else if (id == 56) { return "Boulderoar";
		} else if (id == 57) { return "Fightorex";
		} else if (id == 58) { return "Raptorex";
		} else if (id == 59) { return "Kleinowl";
		} else if (id == 60) { return "Hootowl";
		} else if (id == 61) { return "Dualmoose";
		} else if (id == 62) { return "Snom";
		} else if (id == 63) { return "Frosmoth";
		} else if (id == 64) { return "Grondor";
		} else if (id == 65) { return "Bipedice";
		} else if (id == 66) { return "Tricerpup";
		} else if (id == 67) { return "Tricercil";
		} else if (id == 68) { return "Spheal";
		} else if (id == 69) { return "Sealeo";
		} else if (id == 70) { return "Walrein";
		} else if (id == 71) { return "Froshrog";
		} else if (id == 72) { return "Bouncerog";
		} else if (id == 73) { return "Bugop";
		} else if (id == 74) { return "Opwing";
		} else if (id == 75) { return "Hatenna";
		} else if (id == 76) { return "Hattrem";
		} else if (id == 77) { return "Hatterene";
		} else if (id == 78) { return "Otterpor";
		} else if (id == 79) { return "Psylotter";
		} else if (id == 80) { return "Florline";
		} else if (id == 81) { return "Florlion";
		} else if (id == 82) { return "Psycorb";
		} else if (id == 83) { return "Psyballs";
		} else if (id == 84) { return "Psycorborator";
		} else if (id == 85) { return "Ralts";
		} else if (id == 86) { return "Kirlia";
		} else if (id == 87) { return "Gardevoir";
		} else if (id == 88) { return "Gallade";
		} else if (id == 89) { return "Tigrette";
		} else if (id == 90) { return "Inkay";
		} else if (id == 91) { return "Malamar";
		} else if (id == 92) { return "Flameruff";
		} else if (id == 93) { return "Barkflare";
		} else if (id == 94) { return "Iglite";
		} else if (id == 95) { return "Blaxer";
		} else if (id == 96) { return "Pyrator";
		} else if (id == 97) { return "Magmaclang";
		} else if (id == 98) { return "Flamehox";
		} else if (id == 99) { return "Fireshard";
		} else if (id == 100) { return "Blastflames";
		} else if (id == 101) { return "Tiowoo";
		} else if (id == 102) { return "Magwoo";
		} else if (id == 103) { return "Lafloo";
		} else if (id == 104) { return "Houndour";
		} else if (id == 105) { return "Houndoom";
		} else if (id == 106) { return "Sparkdust";
		} else if (id == 107) { return "Splame";
		} else if (id == 108) { return "Sparkitten";
		} else if (id == 109) { return "Fireblion";
		} else if (id == 110) { return "Flamebless";
		} else if (id == 111) { return "Shookwat";
		} else if (id == 112) { return "Wattwo";
		} else if (id == 113) { return "Megawatt";
		} else if (id == 114) { return "Elelamb";
		} else if (id == 115) { return "Electroram";
		} else if (id == 116) { return "Superchargo";
		} else if (id == 117) { return "Twigzap";
		} else if (id == 118) { return "Shockbranch";
		} else if (id == 119) { return "Thunderzap";
		} else if (id == 120) { return "Magie";
		} else if (id == 121) { return "Cumin";
		} else if (id == 122) { return "Cinneroph";
		} else if (id == 123) { return "Vupp";
		} else if (id == 124) { return "Vinnie";
		} else if (id == 125) { return "Suvinero";
		} else if (id == 126) { return "Whiskie";
		} else if (id == 127) { return "Whiskers";
		} else if (id == 128) { return "Whiskeroar";
		} else if (id == 129) { return "Nincada";
		} else if (id == 130) { return "Ninjask";
		} else if (id == 131) { return "Shedinja";
		} else if (id == 132) { return "Sheltor";
		} else if (id == 133) { return "Shelnado";
		} else if (id == 134) { return "Lilyray";
		} else if (id == 135) { return "Daray";
		} else if (id == 136) { return "Spinaquata";
		} else if (id == 137) { return "Magikarp";
		} else if (id == 138) { return "Gyarados";
		} else if (id == 139) { return "Staryu";
		} else if (id == 140) { return "Starmie";
		} else if (id == 141) { return "Ali";
		} else if (id == 142) { return "Batorali";
		} else if (id == 143) { return "Posho";
		} else if (id == 144) { return "Shomp";
		} else if (id == 145) { return "Poshorump";
		} else if (id == 146) { return "Binacle";
		} else if (id == 147) { return "Barbaracle";
		} else if (id == 148) { return "Durfish";
		} else if (id == 149) { return "Dompster";
		} else if (id == 150) { return "Kissyfishy";
		} else if (id == 151) { return "Ekans";
		} else if (id == 152) { return "Arbok";
		} else if (id == 153) { return "Zubat";
		} else if (id == 154) { return "Golbat";
		} else if (id == 155) { return "Crobat";
		} else if (id == 156) { return "Poof";
		} else if (id == 157) { return "Hast";
		} else if (id == 158) { return "Poov";
		} else if (id == 159) { return "Grust";
		} else if (id == 160) { return "Cluuz";
		} else if (id == 161) { return "Zurrclu";
		} else if (id == 162) { return "Zurroaratr";
		} else if (id == 163) { return "Timburr";
		} else if (id == 164) { return "Gurdurr";
		} else if (id == 165) { return "Conkeldurr";
		} else if (id == 166) { return "Rhypo";
		} else if (id == 167) { return "Rhynee";
		} else if (id == 168) { return "Rhypolar";
		} else if (id == 169) { return "Diggie";
		} else if (id == 170) { return "Drillatron";
		} else if (id == 171) { return "Wormite";
		} else if (id == 172) { return "Wormbot";
		} else if (id == 173) { return "Wormatron";
		} else if (id == 174) { return "Cleffa";
		} else if (id == 175) { return "Clefairy";
		} else if (id == 176) { return "Clefable";
		} else if (id == 177) { return "Minishoo";
		} else if (id == 178) { return "Glittleshoo";
		} else if (id == 179) { return "Zorua";
		} else if (id == 180) { return "Zoroark";
		} else if (id == 181) { return "Droid";
		} else if (id == 182) { return "Armoid";
		} else if (id == 183) { return "Soldrota";
		} else if (id == 184) { return "Tinkie";
		} else if (id == 185) { return "Shawar";
		} else if (id == 186) { return "Shaboom";
		} else if (id == 187) { return "Dragee";
		} else if (id == 188) { return "Draga";
		} else if (id == 189) { return "Drageye";
		} else if (id == 190) { return "Blobmo";
		} else if (id == 191) { return "Nebulimb";
		} else if (id == 192) { return "Galactasolder";
		} else if (id == 193) { return "Consodust";
		} else if (id == 194) { return "Cosmocrash";
		} else if (id == 195) { return "Rockmite";
		} else if (id == 196) { return "Stellarock";
		} else if (id == 197) { return "Poof-E";
		} else if (id == 198) { return "Hast-E";
		} else if (id == 199) { return "Droid-E";
		} else if (id == 200) { return "Armoid-E";
		} else if (id == 201) { return "Soldrota-E";
		} else if (id == 202) { return "Flamehox-E";
		} else if (id == 203) { return "Fireshard-E";
		} else if (id == 204) { return "Blastflames-E";
		} else if (id == 205) { return "Rocky-E";
		} else if (id == 206) { return "Boulder-E";
		} else if (id == 207) { return "Blaster-E";
		} else if (id == 208) { return "Crystallor-E";
		} else if (id == 209) { return "Magikarp-E";
		} else if (id == 210) { return "Gyarados-E";
		} else if (id == 211) { return "Shockfang";
		} else if (id == 212) { return "Electrocobra";
		} else if (id == 213) { return "Nightrex";
		} else if (id == 214) { return "Shadowsaur";
		} else if (id == 215) { return "Durfish-S";
		} else if (id == 216) { return "Dompster-S";
		} else if (id == 217) { return "Wormite-S";
		} else if (id == 218) { return "Wormbot-S";
		} else if (id == 219) { return "Wormatron-S";
		} else if (id == 220) { return "Cluuz-S";
		} else if (id == 221) { return "Zurrclu-S";
		} else if (id == 222) { return "Zurroaratr-S";
		} else if (id == 223) { return "Iglite-S";
		} else if (id == 224) { return "Blaxer-S";
		} else if (id == 225) { return "Pyrator-S";
		} else if (id == 226) { return "Ekans-S";
		} else if (id == 227) { return "Arbok-S";
		} else if (id == 228) { return "Soarwhell";
		} else if (id == 229) { return "Diftery";
		} else if (id == 230) { return "Vorsuitex";
		} else if (id == 231) { return "Kleinyeti";
		} else if (id == 232) { return "Triwandoliz";
		} else if (id == 233) { return "Relomidel";
		} else if (id == 234) { return "Relopamil";
		} else if (id == 235) { return "Dragowrath";
		} else if (id == 236) { return "Solaroxyous";
		} else if (id == 237) { return "Kissyfishy-D";
		} else if (id == 238) { return "Scraggy";
		
		
		} else if (id == -1) { return "Leafer";
		} else if (id == -2) { return "Sticker";
		} else if (id == -3) { return "Tree-a-tar";
		} else if (id == -4) { return "Fwoochar";
		} else if (id == -5) { return "Charchar";
		} else if (id == -6) { return "Charwoo";
		} else if (id == -7) { return "Poletad";
		} else if (id == -8) { return "Tadwhirl";
		} else if (id == -9) { return "Tadtoad";
		} else if (id == -10) { return "Twigzig";
		} else if (id == -11) { return "Twanzig";
		} else if (id == -12) { return "Sapwin";
		} else if (id == -13) { return "Treewin";
		} else if (id == -14) { return "Hops";
		} else if (id == -15) { return "Bouncey";
		} else if (id == -16) { return "Hammo";
		} else if (id == -17) { return "HammyBoy";
		} else if (id == -18) { return "Rocky";
		} else if (id == -19) { return "Boulder";
		} else if (id == -20) { return "Blaster";
		} else if (id == -21) { return "Spike";
		} else if (id == -22) { return "Spiko";
		} else if (id == -23) { return "Spikoga";
		} else if (id == -24) { return "Chompee";
		} else if (id == -25) { return "Chompo";
		} else if (id == -26) { return "Duckwee";
		} else if (id == -27) { return "Duckwack";
		} else if (id == -28) { return "Duckstrike";
		} else if (id == -29) { return "Chirpus";
		} else if (id == -30) { return "Quackus";
		} else if (id == -31) { return "Crane";
		} else if (id == -32) { return "Plankik";
		} else if (id == -33) { return "Nug";
		} else if (id == -34) { return "Contrug";
		} else if (id == -35) { return "Wasp";
		} else if (id == -36) { return "Mosquitto";
		} else if (id == -37) { return "Cluuz";
		} else if (id == -38) { return "Poof";
		} else if (id == -39) { return "Hast";
		} else if (id == -40) { return "Poov";
		} else if (id == -41) { return "Grust";
		} else if (id == -42) { return "Smartwiz";
		} else if (id == -43) { return "Vinnie";
		} else if (id == -44) { return "Shookwat";
		} else if (id == -45) { return "Wattwo";
		} else if (id == -46) { return "Corry";
		} else if (id == -47) { return "Ssordy";
		} else if (id == -48) { return "Bugik";
		} else if (id == -49) { return "Swordik";
		} else if (id == -50) { return "Ninjakik";
		} else if (id == -51) { return "Karachop";
		} else if (id == -52) { return "Karapunch";
		} else if (id == -53) { return "Karakik";
		} else if (id == -54) { return "Karasword";
		} else if (id == -55) { return "Sludger";
		} else if (id == -56) { return "Gludge";
		} else if (id == -57) { return "Wing";
		} else if (id == -58) { return "Toxing";
		} else if (id == -59) { return "Gelco-G";
		} else if (id == -60) { return "Lizardo-G";
		} else if (id == -61) { return "Jorid";
		} else if (id == -62) { return "Tentarid";
		} else if (id == -63) { return "Hedgehog";
		} else if (id == -64) { return "Bulldozer";
		} else if (id == -65) { return "Daray";
		} else if (id == -66) { return "Spinaquata";
		} else if (id == -67) { return "Toree";
		} else if (id == -68) { return "Turdee";
		} else if (id == -69) { return "Ali";
		} else if (id == -70) { return "Shomp";
		} else if (id == -71) { return "Rhypo";
		} else if (id == -72) { return "Rhynee";
		} else if (id == -73) { return "Rhypolar";
		} else if (id == -74) { return "Ignite";
		} else if (id == -75) { return "Blaze";
		} else if (id == -76) { return "Inferno";
		} else if (id == -77) { return "Flamehead";
		} else if (id == -78) { return "Fireshard";
		} else if (id == -79) { return "Blastflames";
		} else if (id == -80) { return "Heater";
		} else if (id == -81) { return "Froller";
		} else if (id == -82) { return "Cloudorus";
		} else if (id == -83) { return "Stormatus";
		} else if (id == -84) { return "Creeflare";
		} else if (id == -85) { return "Flyflare";
		} else if (id == -86) { return "Tara-Z";
		} else if (id == -87) { return "Tara-X";
		} else if (id == -88) { return "Savahger";
		} else if (id == -89) { return "Show";
		} else if (id == -90) { return "Dark Zombie";
		} else if (id == -91) { return "Diftery";
		} else if (id == -92) { return "Electroball";
		} else if (id == -93) { return "Ghast";
		} else if (id == -94) { return "Flast";
		} else if (id == -95) { return "Magie";
		} else if (id == -96) { return "Cumin";
		} else if (id == -97) { return "Droid";
		} else if (id == -98) { return "Armoid";
		} else if (id == -99) { return "Soldrota";
		} else if (id == -100) { return "Dragee";
		} else if (id == -101) { return "Draga";
		} else if (id == -102) { return "Drageye";
		} else if (id == -103) { return "Soardine-A";
		} else if (id == -104) { return "Soardine-D";
		} else if (id == -105) { return "Soardine-F";
		} else if (id == -106) { return "Wando";
		} else if (id == -107) { return "Sparkdust";
		} else if (id == -108) { return "Splame";
		} else if (id == -109) { return "Tinkie";
		} else if (id == -110) { return "Shawar";
		} else if (id == -111) { return "Shaboom";
		} else if (id == -112) { return "Dogo";
		} else if (id == -113) { return "Doleaf";
		} else if (id == -114) { return "Drame";
		} else if (id == -115) { return "Daqua";
		} else if (id == -116) { return "Doxic";
		} else if (id == -117) { return "Dratt";
		} else if (id == -118) { return "Drock";
		} else if (id == -119) { return "Dokurk";
		} else if (id == -120) { return "Drotal";
		} else if (id == -121) { return "Drunch";
		} else if (id == -122) { return "Draco";
		} else if (id == -123) { return "Drakik";
		} else if (id == -124) { return "Plantro";
		} else if (id == -125) { return "Leafron";
		} else if (id == -126) { return "Planterra";
		} else if (id == -127) { return "Sparky";
		} else if (id == -128) { return "Fireball";
		} else if (id == -129) { return "Meteator";
		} else if (id == -130) { return "Taddy";
		} else if (id == -131) { return "Tarow";
		} else if (id == -132) { return "Tadagater";
		} else if (id == -133) { return "Zaparch";
		} else if (id == -134) { return "Zaflame";
		} else if (id == -135) { return "Holgor";
		} else if (id == -136) { return "Larvangor";
		} else if (id == -137) { return "Fleigor";
		} else if (id == -138) { return "Halgatoria";
		} else if (id == -139) { return "Lavatoria";
		} else if (id == -140) { return "Wingatoria";
		} else if (id == -141) { return "Gelco-F";
		} else if (id == -142) { return "Gelco-W";
		} else if (id == -143) { return "Lizardo-F";
		} else if (id == -144) { return "Lizardo-W";
		} return "";
	}
	
	public void setAbility(int i) {
		Ability[] abilities;
		
		if (id == 1) { abilities = new Ability[] {Ability.OVERGROW, Ability.ROUGH_SKIN};
		} else if (id == 2) { abilities = new Ability[] {Ability.OVERGROW, Ability.ROUGH_SKIN};
		} else if (id == 3) { abilities = new Ability[] {Ability.OVERGROW, Ability.ROUGH_SKIN};
		} else if (id == 4) { abilities = new Ability[] {Ability.BLAZE, Ability.SOLAR_POWER};
		} else if (id == 5) { abilities = new Ability[] {Ability.BLAZE, Ability.SOLAR_POWER};
		} else if (id == 6) { abilities = new Ability[] {Ability.BLAZE, Ability.SOLAR_POWER};
		} else if (id == 7) { abilities = new Ability[] {Ability.TORRENT, Ability.PROTEAN};
		} else if (id == 8) { abilities = new Ability[] {Ability.TORRENT, Ability.PROTEAN};
		} else if (id == 9) { abilities = new Ability[] {Ability.TORRENT, Ability.PROTEAN};
		} else if (id == 10) { abilities = new Ability[] {Ability.KEEN_EYE, Ability.INSECT_FEEDER};
		} else if (id == 11) { abilities = new Ability[] {Ability.KEEN_EYE, Ability.INSECT_FEEDER};
		} else if (id == 12) { abilities = new Ability[] {Ability.MAGIC_GUARD, Ability.INSECT_FEEDER};
		} else if (id == 13) { abilities = new Ability[] {Ability.INSOMNIA, Ability.INSECT_FEEDER};
		} else if (id == 14) { abilities = new Ability[] {Ability.INSOMNIA, Ability.INSECT_FEEDER};
		} else if (id == 15) { abilities = new Ability[] {Ability.MIRROR_ARMOR, Ability.TOUGH_CLAWS};
		} else if (id == 16) { abilities = new Ability[] {Ability.MOUTHWATER, Ability.THICK_FAT};
		} else if (id == 17) { abilities = new Ability[] {Ability.MOUTHWATER, Ability.THICK_FAT};
		} else if (id == 18) { abilities = new Ability[] {Ability.ROUGH_SKIN, Ability.ROCK_HEAD};
		} else if (id == 19) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ANGER_POINT};
		} else if (id == 20) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.SHIELD_DUST};
		} else if (id == 21) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.LEVITATE};
		} else if (id == 22) { abilities = new Ability[] {Ability.SWARM, Ability.COMPOUND_EYES};
		} else if (id == 23) { abilities = new Ability[] {Ability.SWARM, Ability.COMPOUND_EYES};
		} else if (id == 24) { abilities = new Ability[] {Ability.SWARM, Ability.COMPOUND_EYES};
		} else if (id == 25) { abilities = new Ability[] {Ability.ROUGH_SKIN, Ability.COMPOUND_EYES};
		} else if (id == 26) { abilities = new Ability[] {Ability.ANGER_POINT, Ability.CHLOROPHYLL};
		} else if (id == 27) { abilities = new Ability[] {Ability.ANGER_POINT, Ability.CHLOROPHYLL};
		} else if (id == 28) { abilities = new Ability[] {Ability.COMPETITIVE, Ability.CHLOROPHYLL};
		} else if (id == 29) { abilities = new Ability[] {Ability.NATURAL_CURE, Ability.POISON_POINT};
		} else if (id == 30) { abilities = new Ability[] {Ability.NATURAL_CURE, Ability.POISON_POINT};
		} else if (id == 31) { abilities = new Ability[] {Ability.NATURAL_CURE, Ability.TECHNICIAN};
		} else if (id == 32) { abilities = new Ability[] {Ability.SWARM, Ability.CHLOROPHYLL};
		} else if (id == 33) { abilities = new Ability[] {Ability.SWARM, Ability.CHLOROPHYLL};
		} else if (id == 34) { abilities = new Ability[] {Ability.SWARM, Ability.CHLOROPHYLL};
		} else if (id == 35) { abilities = new Ability[] {Ability.SWARM, Ability.SWARM};
		} else if (id == 36) { abilities = new Ability[] {Ability.STATIC, Ability.STATIC};
		} else if (id == 37) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 38) { abilities = new Ability[] {Ability.ANTICIPATION, Ability.FLUFFY};
		} else if (id == 39) { abilities = new Ability[] {Ability.MOXIE, Ability.DEFIANT};
		} else if (id == 40) { abilities = new Ability[] {Ability.SERENE_GRACE, Ability.COMPETITIVE};
		} else if (id == 41) { abilities = new Ability[] {Ability.HYPER_CUTTER, Ability.SWARM};
		} else if (id == 42) { abilities = new Ability[] {Ability.HYPER_CUTTER, Ability.JUSTIFIED};
		} else if (id == 43) { abilities = new Ability[] {Ability.SPEED_BOOST, Ability.JUSTIFIED};
		} else if (id == 44) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.RAIN_DISH};
		} else if (id == 45) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.RAIN_DISH};
		} else if (id == 46) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.RAIN_DISH};
		} else if (id == 47) { abilities = new Ability[] {Ability.MAGIC_GUARD, Ability.FLASH_FIRE};
		} else if (id == 48) { abilities = new Ability[] {Ability.STURDY, Ability.UNERODIBLE};
		} else if (id == 49) { abilities = new Ability[] {Ability.STURDY, Ability.UNERODIBLE};
		} else if (id == 50) { abilities = new Ability[] {Ability.STURDY, Ability.UNERODIBLE};
		} else if (id == 51) { abilities = new Ability[] {Ability.STURDY, Ability.FILTER};
		} else if (id == 52) { abilities = new Ability[] {Ability.SHED_SKIN, Ability.REGENERATOR};
		} else if (id == 53) { abilities = new Ability[] {Ability.SYNCHRONIZE, Ability.REGENERATOR};
		} else if (id == 54) { abilities = new Ability[] {Ability.PSYCHIC_SURGE, Ability.REGENERATOR};
		} else if (id == 55) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.TECHNICIAN};
		} else if (id == 56) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.TECHNICIAN};
		} else if (id == 57) { abilities = new Ability[] {Ability.GUTS, Ability.NO_GUARD};
		} else if (id == 58) { abilities = new Ability[] {Ability.GUTS, Ability.NO_GUARD};
		} else if (id == 59) { abilities = new Ability[] {Ability.SNOW_CLOAK, Ability.MOUTHWATER};
		} else if (id == 60) { abilities = new Ability[] {Ability.SNOW_CLOAK, Ability.ICE_BODY};
		} else if (id == 61) { abilities = new Ability[] {Ability.SNOW_CLOAK, Ability.SPARKLY_SURGE};
		} else if (id == 62) { abilities = new Ability[] {Ability.ICY_SCALES, Ability.SWARM};
		} else if (id == 63) { abilities = new Ability[] {Ability.ICY_SCALES, Ability.SWARM};
		} else if (id == 64) { abilities = new Ability[] {Ability.STURDY, Ability.TECHNICIAN};
		} else if (id == 65) { abilities = new Ability[] {Ability.GUTS, Ability.SLUSH_RUSH};
		} else if (id == 66) { abilities = new Ability[] {Ability.ROCK_HEAD, Ability.SAND_FORCE};
		} else if (id == 67) { abilities = new Ability[] {Ability.ROCK_HEAD, Ability.SAND_FORCE};
		} else if (id == 68) { abilities = new Ability[] {Ability.THICK_FAT, Ability.ICE_BODY};
		} else if (id == 69) { abilities = new Ability[] {Ability.THICK_FAT, Ability.ICE_BODY};
		} else if (id == 70) { abilities = new Ability[] {Ability.THICK_FAT, Ability.ICE_BODY};
		} else if (id == 71) { abilities = new Ability[] {Ability.TORRENT, Ability.SLUSH_RUSH};
		} else if (id == 72) { abilities = new Ability[] {Ability.TORRENT, Ability.SLUSH_RUSH};
		} else if (id == 73) { abilities = new Ability[] {Ability.TECHNICIAN, Ability.INSOMNIA};
		} else if (id == 74) { abilities = new Ability[] {Ability.TECHNICIAN, Ability.INSOMNIA};
		} else if (id == 75) { abilities = new Ability[] {Ability.ANTICIPATION, Ability.NATURAL_CURE};
		} else if (id == 76) { abilities = new Ability[] {Ability.ANTICIPATION, Ability.NATURAL_CURE};
		} else if (id == 77) { abilities = new Ability[] {Ability.ANTICIPATION, Ability.NATURAL_CURE};
		} else if (id == 78) { abilities = new Ability[] {Ability.WATER_VEIL, Ability.ANTICIPATION};
		} else if (id == 79) { abilities = new Ability[] {Ability.WATER_VEIL, Ability.SWIFT_SWIM};
		} else if (id == 80) { abilities = new Ability[] {Ability.GRASSY_SURGE, Ability.GRASSY_SURGE};
		} else if (id == 81) { abilities = new Ability[] {Ability.GRASSY_SURGE, Ability.GRASSY_SURGE};
		} else if (id == 82) { abilities = new Ability[] {Ability.PSYCHIC_SURGE, Ability.LEVITATE};
		} else if (id == 83) { abilities = new Ability[] {Ability.PSYCHIC_SURGE, Ability.LEVITATE};
		} else if (id == 84) { abilities = new Ability[] {Ability.PSYCHIC_SURGE, Ability.LEVITATE};
		} else if (id == 85) { abilities = new Ability[] {Ability.SYNCHRONIZE, Ability.NATURAL_CURE};
		} else if (id == 86) { abilities = new Ability[] {Ability.SYNCHRONIZE, Ability.NATURAL_CURE};
		} else if (id == 87) { abilities = new Ability[] {Ability.SYNCHRONIZE, Ability.NATURAL_CURE};
		} else if (id == 88) { abilities = new Ability[] {Ability.JUSTIFIED, Ability.NO_GUARD};
		} else if (id == 89) { abilities = new Ability[] {Ability.SIMPLE, Ability.NORMALIZE};
		} else if (id == 90) { abilities = new Ability[] {Ability.CONTRARY, Ability.CONTRARY};
		} else if (id == 91) { abilities = new Ability[] {Ability.CONTRARY, Ability.CONTRARY};
		} else if (id == 92) { abilities = new Ability[] {Ability.FLASH_FIRE, Ability.INTIMIDATE};
		} else if (id == 93) { abilities = new Ability[] {Ability.FLASH_FIRE, Ability.INTIMIDATE};
		} else if (id == 94) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.FLASH_FIRE};
		} else if (id == 95) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.IRON_FIST};
		} else if (id == 96) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.IRON_FIST};
		} else if (id == 97) { abilities = new Ability[] {Ability.STEELWORKER, Ability.STEELWORKER};
		} else if (id == 98) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.FLAME_BODY};
		} else if (id == 99) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.INTIMIDATE};
		} else if (id == 100) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 101) { abilities = new Ability[] {Ability.SOLID_ROCK, Ability.SOLID_ROCK};
		} else if (id == 102) { abilities = new Ability[] {Ability.SOLID_ROCK, Ability.SOLID_ROCK};
		} else if (id == 103) { abilities = new Ability[] {Ability.SOLID_ROCK, Ability.SOLID_ROCK};
		} else if (id == 104) { abilities = new Ability[] {Ability.EARLY_BIRD, Ability.FLASH_FIRE};
		} else if (id == 105) { abilities = new Ability[] {Ability.EARLY_BIRD, Ability.FLASH_FIRE};
		} else if (id == 106) { abilities = new Ability[] {Ability.SHIELD_DUST, Ability.MAGIC_GUARD};
		} else if (id == 107) { abilities = new Ability[] {Ability.SHIELD_DUST, Ability.MAGIC_GUARD};
		} else if (id == 108) { abilities = new Ability[] {Ability.ANTICIPATION, Ability.ANTICIPATION};
		} else if (id == 109) { abilities = new Ability[] {Ability.DROUGHT, Ability.DROUGHT};
		} else if (id == 110) { abilities = new Ability[] {Ability.COMPETITIVE, Ability.COMPETITIVE};
		} else if (id == 111) { abilities = new Ability[] {Ability.STATIC, Ability.VOLT_ABSORB};
		} else if (id == 112) { abilities = new Ability[] {Ability.STATIC, Ability.ELECTRIC_SURGE};
		} else if (id == 113) { abilities = new Ability[] {Ability.HUGE_POWER, Ability.ELECTRIC_SURGE};
		} else if (id == 114) { abilities = new Ability[] {Ability.STATIC, Ability.FLUFFY};
		} else if (id == 115) { abilities = new Ability[] {Ability.STATIC, Ability.FLUFFY};
		} else if (id == 116) { abilities = new Ability[] {Ability.SPEED_BOOST, Ability.FLUFFY};
		} else if (id == 117) { abilities = new Ability[] {Ability.SAP_SIPPER, Ability.VOLT_ABSORB};
		} else if (id == 118) { abilities = new Ability[] {Ability.SAP_SIPPER, Ability.VOLT_ABSORB};
		} else if (id == 119) { abilities = new Ability[] {Ability.SAP_SIPPER, Ability.VOLT_ABSORB};
		} else if (id == 120) { abilities = new Ability[] {Ability.TOUGH_CLAWS, Ability.TOUGH_CLAWS};
		} else if (id == 121) { abilities = new Ability[] {Ability.TOUGH_CLAWS, Ability.TOUGH_CLAWS};
		} else if (id == 122) { abilities = new Ability[] {Ability.TOUGH_CLAWS, Ability.TOUGH_CLAWS};
		} else if (id == 123) { abilities = new Ability[] {Ability.MOTOR_DRIVE, Ability.MOTOR_DRIVE};
		} else if (id == 124) { abilities = new Ability[] {Ability.MOTOR_DRIVE, Ability.MOTOR_DRIVE};
		} else if (id == 125) { abilities = new Ability[] {Ability.MOTOR_DRIVE, Ability.MOTOR_DRIVE};
		} else if (id == 126) { abilities = new Ability[] {Ability.GUTS, Ability.GUTS};
		} else if (id == 127) { abilities = new Ability[] {Ability.GUTS, Ability.GUTS};
		} else if (id == 128) { abilities = new Ability[] {Ability.GUTS, Ability.GUTS};
		} else if (id == 129) { abilities = new Ability[] {Ability.COMPOUND_EYES, Ability.COMPOUND_EYES};
		} else if (id == 130) { abilities = new Ability[] {Ability.SPEED_BOOST, Ability.SPEED_BOOST};
		} else if (id == 131) { abilities = new Ability[] {Ability.WONDER_GUARD, Ability.WONDER_GUARD};
		} else if (id == 132) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.REGENERATOR};
		} else if (id == 133) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.REGENERATOR};
		} else if (id == 134) { abilities = new Ability[] {Ability.UNAWARE, Ability.WATER_ABSORB};
		} else if (id == 135) { abilities = new Ability[] {Ability.UNAWARE, Ability.WATER_ABSORB};
		} else if (id == 136) { abilities = new Ability[] {Ability.DRIZZLE, Ability.WATER_ABSORB};
		} else if (id == 137) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.RATTLED};
		} else if (id == 138) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.MOXIE};
		} else if (id == 139) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.NATURAL_CURE};
		} else if (id == 140) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.NATURAL_CURE};
		} else if (id == 141) { abilities = new Ability[] {Ability.STEALTHY_PREDATOR, Ability.STRONG_JAW};
		} else if (id == 142) { abilities = new Ability[] {Ability.STEALTHY_PREDATOR, Ability.STRONG_JAW};
		} else if (id == 143) { abilities = new Ability[] {Ability.REGENERATOR, Ability.MAGIC_GUARD};
		} else if (id == 144) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.SNIPER};
		} else if (id == 145) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.MAGIC_GUARD};
		} else if (id == 146) { abilities = new Ability[] {Ability.TOUGH_CLAWS, Ability.SNIPER};
		} else if (id == 147) { abilities = new Ability[] {Ability.TOUGH_CLAWS, Ability.SNIPER};
		} else if (id == 148) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.PRANKSTER};
		} else if (id == 149) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.PRANKSTER};
		} else if (id == 150) { abilities = new Ability[] {Ability.WATER_VEIL, Ability.REGENERATOR};
		} else if (id == 151) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.SHED_SKIN};
		} else if (id == 152) { abilities = new Ability[] {Ability.INTIMIDATE, Ability.SHED_SKIN};
		} else if (id == 153) { abilities = new Ability[] {Ability.INNER_FOCUS, Ability.INNER_FOCUS};
		} else if (id == 154) { abilities = new Ability[] {Ability.INNER_FOCUS, Ability.INNER_FOCUS};
		} else if (id == 155) { abilities = new Ability[] {Ability.INNER_FOCUS, Ability.INNER_FOCUS};
		} else if (id == 156) { abilities = new Ability[] {Ability.FRIENDLY_GHOST, Ability.LEVITATE};
		} else if (id == 157) { abilities = new Ability[] {Ability.FRIENDLY_GHOST, Ability.LEVITATE};
		} else if (id == 158) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ANGER_POINT};
		} else if (id == 159) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ANGER_POINT};
		} else if (id == 160) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 161) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 162) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 163) { abilities = new Ability[] {Ability.GUTS, Ability.SHEER_FORCE};
		} else if (id == 164) { abilities = new Ability[] {Ability.GUTS, Ability.SHEER_FORCE};
		} else if (id == 165) { abilities = new Ability[] {Ability.GUTS, Ability.SHEER_FORCE};
		} else if (id == 166) { abilities = new Ability[] {Ability.RATTLED, Ability.SAND_FORCE};
		} else if (id == 167) { abilities = new Ability[] {Ability.SAND_STREAM, Ability.SAND_FORCE};
		} else if (id == 168) { abilities = new Ability[] {Ability.SAND_STREAM, Ability.SAND_FORCE};
		} else if (id == 169) { abilities = new Ability[] {Ability.SAND_RUSH, Ability.TOUGH_CLAWS};
		} else if (id == 170) { abilities = new Ability[] {Ability.SAND_FORCE, Ability.TOUGH_CLAWS};
		} else if (id == 171) { abilities = new Ability[] {Ability.SAND_VEIL, Ability.RATTLED};
		} else if (id == 172) { abilities = new Ability[] {Ability.SAND_VEIL, Ability.RATTLED};
		} else if (id == 173) { abilities = new Ability[] {Ability.SAND_VEIL, Ability.ROUGH_SKIN};
		} else if (id == 174) { abilities = new Ability[] {Ability.MAGIC_GUARD, Ability.UNAWARE};
		} else if (id == 175) { abilities = new Ability[] {Ability.MAGIC_GUARD, Ability.UNAWARE};
		} else if (id == 176) { abilities = new Ability[] {Ability.MAGIC_GUARD, Ability.UNAWARE};
		} else if (id == 177) { abilities = new Ability[] {Ability.NATURAL_CURE, Ability.SERENE_GRACE};
		} else if (id == 178) { abilities = new Ability[] {Ability.NATURAL_CURE, Ability.SERENE_GRACE};
		} else if (id == 179) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.FALSE_ILLUMINATION};
		} else if (id == 180) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.FALSE_ILLUMINATION};
		} else if (id == 181) { abilities = new Ability[] {Ability.SNIPER, Ability.SNIPER};
		} else if (id == 182) { abilities = new Ability[] {Ability.SNIPER, Ability.SNIPER};
		} else if (id == 183) { abilities = new Ability[] {Ability.SNIPER, Ability.SNIPER};
		} else if (id == 184) { abilities = new Ability[] {Ability.SPARKLY_SURGE, Ability.SPARKLY_SURGE};
		} else if (id == 185) { abilities = new Ability[] {Ability.SPARKLY_SURGE, Ability.SPARKLY_SURGE};
		} else if (id == 186) { abilities = new Ability[] {Ability.SPARKLY_SURGE, Ability.SPARKLY_SURGE};
		} else if (id == 187) { abilities = new Ability[] {Ability.MULTISCALE, Ability.MULTISCALE};
		} else if (id == 188) { abilities = new Ability[] {Ability.MULTISCALE, Ability.MULTISCALE};
		} else if (id == 189) { abilities = new Ability[] {Ability.MULTISCALE, Ability.MULTISCALE};
		} else if (id == 190) { abilities = new Ability[] {Ability.SHIELD_DUST, Ability.LEVITATE};
		} else if (id == 191) { abilities = new Ability[] {Ability.SHIELD_DUST, Ability.GRAVITATIONAL_PULL};
		} else if (id == 192) { abilities = new Ability[] {Ability.SHEER_FORCE, Ability.GRAVITATIONAL_PULL};
		} else if (id == 193) { abilities = new Ability[] {Ability.SNOW_WARNING, Ability.SLUSH_RUSH};
		} else if (id == 194) { abilities = new Ability[] {Ability.SNOW_WARNING, Ability.SLUSH_RUSH};
		} else if (id == 195) { abilities = new Ability[] {Ability.GRAVITATIONAL_PULL, Ability.HUGE_POWER};
		} else if (id == 196) { abilities = new Ability[] {Ability.GRAVITATIONAL_PULL, Ability.HUGE_POWER};
		} else if (id == 197) { abilities = new Ability[] {Ability.FRIENDLY_GHOST, Ability.LEVITATE};
		} else if (id == 198) { abilities = new Ability[] {Ability.FRIENDLY_GHOST, Ability.COMPETITIVE};
		} else if (id == 199) { abilities = new Ability[] {Ability.SNIPER, Ability.IRON_FIST};
		} else if (id == 200) { abilities = new Ability[] {Ability.SNIPER, Ability.IRON_FIST};
		} else if (id == 201) { abilities = new Ability[] {Ability.SNIPER, Ability.IRON_FIST};
		} else if (id == 202) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.STATIC};
		} else if (id == 203) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.LEVITATE};
		} else if (id == 204) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.LEVITATE};
		} else if (id == 205) { abilities = new Ability[] {Ability.STURDY, Ability.MOTOR_DRIVE};
		} else if (id == 206) { abilities = new Ability[] {Ability.STURDY, Ability.MOTOR_DRIVE};
		} else if (id == 207) { abilities = new Ability[] {Ability.STURDY, Ability.LIGHTNING_ROD};
		} else if (id == 208) { abilities = new Ability[] {Ability.CLEAR_BODY, Ability.MOTOR_DRIVE};
		} else if (id == 209) { abilities = new Ability[] {Ability.SWIFT_SWIM, Ability.RATTLED};
		} else if (id == 210) { abilities = new Ability[] {Ability.ELECTRIC_SURGE, Ability.MOTOR_DRIVE};
		} else if (id == 211) { abilities = new Ability[] {Ability.STRONG_JAW, Ability.SHED_SKIN};
		} else if (id == 212) { abilities = new Ability[] {Ability.STRONG_JAW, Ability.SHED_SKIN};
		} else if (id == 213) { abilities = new Ability[] {Ability.MOXIE, Ability.DEFIANT};
		} else if (id == 214) { abilities = new Ability[] {Ability.MOXIE, Ability.DEFIANT};
		} else if (id == 215) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.PRANKSTER};
		} else if (id == 216) { abilities = new Ability[] {Ability.FALSE_ILLUMINATION, Ability.PRANKSTER};
		} else if (id == 217) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ADAPTABILITY};
		} else if (id == 218) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ADAPTABILITY};
		} else if (id == 219) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ADAPTABILITY};
		} else if (id == 220) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 221) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 222) { abilities = new Ability[] {Ability.LEVITATE, Ability.LEVITATE};
		} else if (id == 223) { abilities = new Ability[] {Ability.FLAME_BODY, Ability.SUPER_LUCK};
		} else if (id == 224) { abilities = new Ability[] {Ability.GUTS, Ability.SUPER_LUCK};
		} else if (id == 225) { abilities = new Ability[] {Ability.GUTS, Ability.SUPER_LUCK};
		} else if (id == 226) { abilities = new Ability[] {Ability.SHED_SKIN, Ability.LIGHTNING_ROD};
		} else if (id == 227) { abilities = new Ability[] {Ability.SHED_SKIN, Ability.LIGHTNING_ROD};
		} else if (id == 228) { abilities = new Ability[] {Ability.DRIZZLE, Ability.DRIZZLE};
		} else if (id == 229) { abilities = new Ability[] {Ability.TYPE_MASTER, Ability.TYPE_MASTER};
		} else if (id == 230) { abilities = new Ability[] {Ability.SNOW_WARNING, Ability.SNOW_WARNING};
		} else if (id == 231) { abilities = new Ability[] {Ability.SLUSH_RUSH, Ability.SLUSH_RUSH};
		} else if (id == 232) { abilities = new Ability[] {Ability.PROTEAN, Ability.PROTEAN};
		} else if (id == 233) { abilities = new Ability[] {Ability.GLACIER_AURA, Ability.GLACIER_AURA};
		} else if (id == 234) { abilities = new Ability[] {Ability.PSYCHIC_AURA, Ability.PSYCHIC_AURA};
		} else if (id == 235) { abilities = new Ability[] {Ability.GALACTIC_AURA, Ability.GALACTIC_AURA};
		} else if (id == 236) { abilities = new Ability[] {Ability.DROUGHT, Ability.DROUGHT};
		} else if (id == 237) { abilities = new Ability[] {Ability.ADAPTABILITY, Ability.ADAPTABILITY};
		} else if (id == 238) { abilities = new Ability[] {Ability.MOXIE, Ability.INTIMIDATE};
		} else {
			abilities = new Ability[] {Ability.SAND_VEIL, Ability.SNOW_CLOAK};
		}
		
		ability = abilities[i];
		
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getCurrentHP() {
		return this.currentHP;
	}

	public Pokemon levelUp(Player player) {
		int oHP = this.getStat(0);
		this.exp -= this.expMax;
		++level;
		awardHappiness(5);
		System.out.println(this.nickname + " leveled Up!");
		checkMove();
		Pokemon result = this.checkEvo(player);
		expMax = this.level * 2;
		stats = this.getStats();
		int nHP = this.getStat(0);
		this.currentHP += nHP - oHP;
		if (this.level == 100) this.exp = 0;
		return result;
		
	}
	
	public void checkMove() {
	    if (this.level - 1 >= this.movebank.length) return;
	    Node node = this.movebank[this.level - 1];
	    while (node != null) {
	        Move move = node.data;
	        if (move != null && !this.knowsMove(move)) {
	            boolean learnedMove = false;
	            for (int i = 0; i < 4; i++) {
	                if (this.moveset[i] == null) {
	                    this.moveset[i] = move;
	                    System.out.println(this.nickname + " learned " + move.toString() + "!");
	                    learnedMove = true;
	                    break;
	                }
	            }
	            if (!learnedMove) {
	                int choice = this.displayMoveOptions(move);
	                if (choice == JOptionPane.CLOSED_OPTION) {
	                    System.out.println(this.nickname + " did not learn " + move.toString() + ".");
	                } else {
		                System.out.println(this.nickname + " has learned " + move.toString() + " and forgot " + this.moveset[choice] + "!");
		                this.moveset[choice] = move;
	                }
	            }
	        }
	        node = node.next;
	    }
	}


	private Pokemon checkEvo(Player player) {
		Pokemon result = null;
		int area = player.currentMap;
		if (id == 1 && level >= 18) {
			result = new Pokemon(2, this);
		} else if (id == 2 && level >= 36) {
			result = new Pokemon(3, this);
		} else if (id == 4 && level >= 16) {
			result = new Pokemon(5, this);
		} else if (id == 5 && level >= 36) {
			result = new Pokemon(6, this);
		} else if (id == 7 && level >= 16) {
			result = new Pokemon(8, this);
		} else if (id == 8 && level >= 36) {
			result = new Pokemon(9, this);
		} else if (id == 10 && level >= 16) {
			result = new Pokemon(11, this);
		} else if (id == 11 && level >= 36) {
			result = new Pokemon(id + 1, this);		
		} else if (id == 13 && level >= 17) {
			result = new Pokemon(id + 1, this);
		} else if (id == 14 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 16 && level >= 32 && knowsMove(Move.ROLLOUT)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 17 && area == -1) {
			result = new Pokemon(id + 1, this);
		} else if (id == 19 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 20 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 22 && level >= 18) {
			result = new Pokemon(id + 1, this);
		} else if (id == 23 && area == -1) {
			result = new Pokemon(25, this);
		} else if (id == 23 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 25 && level >= 28) {
			result = new Pokemon(id + 1, this);
		} else if (id == 29 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 32 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 33 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 35 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 36 && area == -2) {
			result = new Pokemon(id + 1, this);
		} else if (id == 41 && level >= 15) {
			result = new Pokemon(id + 1, this);
		} else if (id == 42 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 44 && level >= 16) {
			result = new Pokemon(id + 1, this);
		} else if (id == 48 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 49 && level >= 36 && knowsMove(Move.ROCK_BLAST)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 49 && area == -2) {
			result = new Pokemon(id + 1, this);
		} else if (id == 52 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 53 && area == -1) {
			result = new Pokemon(id + 1, this);
		} else if (id == 55 && level >= 34) {
			result = new Pokemon(id + 1, this);
		} else if (id == 57 && level >= 36) {
			result = new Pokemon(id + 1, this);
		} else if (id == 59 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 66 && level >= 48) {
			result = new Pokemon(id + 1, this);
		} else if (id == 68 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 69 && level >= 44) {
			result = new Pokemon(id + 1, this);
		} else if (id == 71 && level >= 31) {
			result = new Pokemon(id + 1, this);
		} else if (id == 73 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 75 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 76 && level >= 42) {
			result = new Pokemon(id + 1, this);
		} else if (id == 78 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 80 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 82 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 83 && level >= 52) {
			result = new Pokemon(id + 1, this);
		} else if (id == 85 && level >= 18) {
			result = new Pokemon(id + 1, this);
		} else if (id == 86 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 90 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 92 && level >= 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 94 && level >= 16) {
			result = new Pokemon(id + 1, this);
		} else if (id == 95 && level >= 36) {
			result = new Pokemon(id + 1, this);
		} else if (id == 98 && level >= 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 99 && level >= 55) {
			result = new Pokemon(id + 1, this);
		} else if (id == 101 && level >= 15) {
			result = new Pokemon(id + 1, this);
		} else if (id == 102 && level >= 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 104 && level >= 24) {
			result = new Pokemon(id + 1, this);
		} else if (id == 106 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 111 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 112 && area == -2) {
			result = new Pokemon(id + 1, this);
		} else if (id == 114 && level >= 28) {
			result = new Pokemon(id + 1, this);
		} else if (id == 115 && level >= 48) {
			result = new Pokemon(id + 1, this);
		} else if (id == 117 && level >= 19) {
			result = new Pokemon(id + 1, this);
		} else if (id == 120 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 121 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 123 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 124 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 126 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 127 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 129 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 132 && area == -3) {
			result = new Pokemon(id + 1, this);
		} else if (id == 134 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 135 && level >= 27) {
			result = new Pokemon(id + 1, this);
		} else if (id == 137 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 139 && area == -3) {
			result = new Pokemon(id + 1, this);
		} else if (id == 143 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 144 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 146 && level >= 39) {
			result = new Pokemon(id + 1, this);
		} else if (id == 148 && area == -3) {
			result = new Pokemon(id + 1, this);
		} else if (id == 151 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 153 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 154 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 156 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 158 && level >= 23) {
			result = new Pokemon(id + 1, this);
		} else if (id == 161 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 163 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 164 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 166 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 167 && level >= 50) {
			result = new Pokemon(id + 1, this);
		} else if (id == 169 && level >= 33) {
			result = new Pokemon(id + 1, this);
		} else if (id == 171 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 172 && level >= 48) {
			result = new Pokemon(id + 1, this);
		} else if (id == 174 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 179 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 181 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 182 && level >= 50) {
			result = new Pokemon(id + 1, this);
		} else if (id == 184 && level >= 26) {
			result = new Pokemon(id + 1, this);
		} else if (id == 185 && level >= 50) {
			result = new Pokemon(id + 1, this);
		} else if (id == 187 && level >= 36) {
			result = new Pokemon(id + 1, this);
		} else if (id == 188 && level >= 55) {
			result = new Pokemon(id + 1, this);
		} else if (id == 190 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 191 && level >= 60) {
			result = new Pokemon(id + 1, this);
		} else if (id == 195 && area == -1) {
			result = new Pokemon(id + 1, this);
		} else if (id == 197 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 199 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 200 && level >= 50) {
			result = new Pokemon(id + 1, this);
		} else if (id == 202 && level >= 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 203 && level >= 55) {
			result = new Pokemon(id + 1, this);
		} else if (id == 205 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 206 && area == -2) {
			result = new Pokemon(208, this);
		} else if (id == 206 && level >= 36 && knowsMove(Move.ROCK_BLAST)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 209 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 212 && area == -1) {
			result = new Pokemon(id + 1, this);
		} else if (id == 213 && area == -2) {
			result = new Pokemon(id + 1, this);
		} else if (id == 218 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 219 && level >= 45) {
			result = new Pokemon(id + 1, this);
		} else if (id == 221 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 223 && level >= 16) {
			result = new Pokemon(id + 1, this);
		} else if (id == 224 && level >= 36) {
			result = new Pokemon(id + 1, this);
		} else if (id == 226 && level >= 32) {
			result = new Pokemon(id + 1, this);
		}
					
		if (result != null) {
			boolean shouldEvolve = Battle.displayEvolution(this);
			if (shouldEvolve) {
		        int hpDif = this.getStat(0) - this.currentHP;
		        result.currentHP -= hpDif;
		        result.moveMultiplier = this.moveMultiplier;
		        System.out.println(this.nickname + " evolved into " + result.name + "!");
		        result.exp = this.exp;
		        player.pokedex[result.id] = 2;
			} else {
				return this;
			}
	    }
		return result;
		
	}

	public int[] getStats() {
		double HPnum = (2 * baseStats[0] + ivs[0]) * level;
		stats[0] = (int) (Math.floor(HPnum/100) + level + 10);
		if (id == 131) stats[0] = 1;
		double Atknum = (2 * baseStats[1] + ivs[1]) * level;
		stats[1] = (int) Math.floor((Math.floor(Atknum/100) + 5) * nature[0]);
		double Defnum = (2 * baseStats[2] + ivs[2]) * level;
		stats[2] = (int) Math.floor((Math.floor(Defnum/100) + 5) * nature[1]);
		double SpAnum = (2 * baseStats[3] + ivs[3]) * level;
		stats[3] = (int) Math.floor((Math.floor(SpAnum/100) + 5) * nature[2]);
		double SpDnum = (2 * baseStats[4] + ivs[4]) * level;
		stats[4] = (int) Math.floor((Math.floor(SpDnum/100) + 5) * nature[3]);
		double Spenum = (2 * baseStats[5] + ivs[5]) * level;
		stats[5] = (int) Math.floor((Math.floor(Spenum/100) + 5) * nature[4]);
		return stats;
	}
	
	public int getStat(int type) {
		if (type == 0) {
			return this.stats[0];
		} else if (type == 1) {
			return this.stats[1];
		} else if (type == 2) {
			return this.stats[2];
		} else if (type == 3) {
			return this.stats[3];
		} else if (type == 4) {
			return this.stats[4];
		} else if (type == 5) {
			return this.stats[5];
		} else {
			return 0;
		}
	}

	public int getBaseStat(int type) {
		if (type == 0) {
			return this.baseStats[0];
		} else if (type == 1) {
			return this.baseStats[1];
		} else if (type == 2) {
			return this.baseStats[2];
		} else if (type == 3) {
			return this.baseStats[3];
		} else if (type == 4) {
			return this.baseStats[4];
		} else if (type == 5) {
			return this.baseStats[5];
		} else if (type == 6) {
			return this.baseStats[6];
		} else {
			return 0;
		}
	}
	
	public int getBST() {
		int BST = 0;
		for (int i = 0; i <= 5; i++) {
			BST += baseStats[i];
		}
		return BST;
	}
	
	public String toString(PType[] weak) {
		StringBuilder sb = new StringBuilder();
	    sb.append("[");
	    for (int i = 0; i < weak.length; i++) {
	        sb.append(weak[i]);
	        if (i != weak.length - 1) {
	            sb.append(", ");
	        }
	    }
	    sb.append("]");
	    return sb.toString();
	}
	
	public int[] setBaseStats() {
		if (this.id == 1) { this.baseStats = new int[]{58,58,69,46,69,35};
		} else if (this.id == 2) { this.baseStats = new int[]{73,75,80,48,89,37};
		} else if (this.id == 3) { this.baseStats = new int[]{95,87,91,95,110,39};
		} else if (this.id == 4) { this.baseStats = new int[]{60,49,54,74,58,34};
		} else if (this.id == 5) { this.baseStats = new int[]{85,53,69,90,66,36};
		} else if (this.id == 6) { this.baseStats = new int[]{89,59,81,115,75,102};
		} else if (this.id == 7) { this.baseStats = new int[]{45,70,56,46,45,63};
		} else if (this.id == 8) { this.baseStats = new int[]{60,90,76,49,55,70};
		} else if (this.id == 9) { this.baseStats = new int[]{85,115,110,50,85,85};
		} else if (this.id == 10) { this.baseStats = new int[]{35,35,35,55,55,80};
		} else if (this.id == 11) { this.baseStats = new int[]{50,40,45,75,70,100};
		} else if (this.id == 12) { this.baseStats = new int[]{70,40,60,100,100,130};
		} else if (this.id == 13) { this.baseStats = new int[]{56,61,65,20,29,45};
		} else if (this.id == 14) { this.baseStats = new int[]{66,76,86,35,60,78};
		} else if (this.id == 15) { this.baseStats = new int[]{75,85,110,40,100,85};
		} else if (this.id == 16) { this.baseStats = new int[]{99,61,51,40,79,20};
		} else if (this.id == 17) { this.baseStats = new int[]{150,69,65,43,108,55};
		} else if (this.id == 18) { this.baseStats = new int[]{150,95,100,40,85,55};
		} else if (this.id == 19) { this.baseStats = new int[]{55,55,55,55,55,55};
		} else if (this.id == 20) { this.baseStats = new int[]{75,75,75,75,75,75};
		} else if (this.id == 21) { this.baseStats = new int[]{80,60,75,135,85,80};
		} else if (this.id == 22) { this.baseStats = new int[]{33,44,33,23,29,38};
		} else if (this.id == 23) { this.baseStats = new int[]{59,68,72,35,61,80};
		} else if (this.id == 24) { this.baseStats = new int[]{60,70,120,35,120,100};
		} else if (this.id == 25) { this.baseStats = new int[]{100,120,120,35,60,70};
		} else if (this.id == 26) { this.baseStats = new int[]{65,105,100,50,30,55};
		} else if (this.id == 27) { this.baseStats = new int[]{120,125,120,60,35,55};
		} else if (this.id == 28) { this.baseStats = new int[]{120,60,95,115,80,90};
		} else if (this.id == 29) { this.baseStats = new int[]{40,30,35,50,70,55};
		} else if (this.id == 30) { this.baseStats = new int[]{50,60,45,100,80,65};
		} else if (this.id == 31) { this.baseStats = new int[]{60,70,65,125,105,90};
		} else if (this.id == 32) { this.baseStats = new int[]{45,53,70,40,60,42};
		} else if (this.id == 33) { this.baseStats = new int[]{55,63,90,50,80,42};
		} else if (this.id == 34) { this.baseStats = new int[]{75,103,80,70,80,92};
		} else if (this.id == 35) { this.baseStats = new int[]{47,62,45,55,45,46};
		} else if (this.id == 36) { this.baseStats = new int[]{57,82,95,55,75,36};
		} else if (this.id == 37) { this.baseStats = new int[]{77,65,90,110,75,95};
		} else if (this.id == 38) { this.baseStats = new int[]{60,50,100,75,105,60};
		} else if (this.id == 39) { this.baseStats = new int[]{70,130,95,45,80,100};
		} else if (this.id == 40) { this.baseStats = new int[]{70,45,80,130,95,100};
		} else if (this.id == 41) { this.baseStats = new int[]{40,60,65,30,50,70};
		} else if (this.id == 42) { this.baseStats = new int[]{53,95,85,30,53,85};
		} else if (this.id == 43) { this.baseStats = new int[]{68,120,100,72,80,105};
		} else if (this.id == 44) { this.baseStats = new int[]{40,30,30,40,50,30};
		} else if (this.id == 45) { this.baseStats = new int[]{60,50,50,60,70,50};
		} else if (this.id == 46) { this.baseStats = new int[]{80,70,70,90,100,70};
		} else if (this.id == 47) { this.baseStats = new int[]{60,40,39,120,95,101};
		} else if (this.id == 48) { this.baseStats = new int[]{68,72,122,20,25,18};
		} else if (this.id == 49) { this.baseStats = new int[]{87,95,143,25,30,45};
		} else if (this.id == 50) { this.baseStats = new int[]{95,110,150,70,45,55};
		} else if (this.id == 51) { this.baseStats = new int[]{90,75,50,110,150,50};
		} else if (this.id == 52) { this.baseStats = new int[]{72,83,75,52,58,70};
		} else if (this.id == 53) { this.baseStats = new int[]{85,95,77,55,88,65};
		} else if (this.id == 54) { this.baseStats = new int[]{100,120,90,55,95,75};
		} else if (this.id == 55) { this.baseStats = new int[]{55,90,60,45,50,80};
		} else if (this.id == 56) { this.baseStats = new int[]{75,111,75,90,64,85};
		} else if (this.id == 57) { this.baseStats = new int[]{52,72,69,77,56,67};
		} else if (this.id == 58) { this.baseStats = new int[]{66,74,85,106,95,97};
		} else if (this.id == 59) { this.baseStats = new int[]{60,15,40,80,73,55};
		} else if (this.id == 60) { this.baseStats = new int[]{85,40,65,120,120,65};
		} else if (this.id == 61) { this.baseStats = new int[]{90,64,67,85,71,58};
		} else if (this.id == 62) { this.baseStats = new int[]{30,25,35,45,30,20};
		} else if (this.id == 63) { this.baseStats = new int[]{70,65,60,125,90,65};
		} else if (this.id == 64) { this.baseStats = new int[]{80,74,87,25,96,33};
		} else if (this.id == 65) { this.baseStats = new int[]{95,130,90,45,85,40};
		} else if (this.id == 66) { this.baseStats = new int[]{65,90,120,20,65,20};
		} else if (this.id == 67) { this.baseStats = new int[]{114,124,130,20,130,20};
		} else if (this.id == 68) { this.baseStats = new int[]{70,40,50,55,50,25};
		} else if (this.id == 69) { this.baseStats = new int[]{90,60,70,75,70,45};
		} else if (this.id == 70) { this.baseStats = new int[]{110,80,90,95,90,65};
		} else if (this.id == 71) { this.baseStats = new int[]{49,51,60,76,62,102};
		} else if (this.id == 72) { this.baseStats = new int[]{64,53,79,120,90,119};
		} else if (this.id == 73) { this.baseStats = new int[]{52,59,51,33,46,58};
		} else if (this.id == 74) { this.baseStats = new int[]{88,102,77,56,83,101};
		} else if (this.id == 75) { this.baseStats = new int[]{42,30,45,56,53,39};
		} else if (this.id == 76) { this.baseStats = new int[]{57,40,65,86,73,49};
		} else if (this.id == 77) { this.baseStats = new int[]{57,90,95,136,103,29};
		} else if (this.id == 78) { this.baseStats = new int[]{56,29,69,74,79,26};
		} else if (this.id == 79) { this.baseStats = new int[]{71,48,87,102,106,86};
		} else if (this.id == 80) { this.baseStats = new int[]{43,43,43,88,80,43};
		} else if (this.id == 81) { this.baseStats = new int[]{87,52,87,96,93,104};
		} else if (this.id == 82) { this.baseStats = new int[]{25,20,63,55,62,25};
		} else if (this.id == 83) { this.baseStats = new int[]{50,100,90,40,90,30};
		} else if (this.id == 84) { this.baseStats = new int[]{100,150,110,40,110,40};
		} else if (this.id == 85) { this.baseStats = new int[]{28,25,25,45,35,40};
		} else if (this.id == 86) { this.baseStats = new int[]{38,35,35,65,55,50};
		} else if (this.id == 87) { this.baseStats = new int[]{68,65,65,125,115,80};
		} else if (this.id == 88) { this.baseStats = new int[]{68,125,65,65,115,63};
		} else if (this.id == 89) { this.baseStats = new int[]{65,84,62,103,72,69};
		} else if (this.id == 90) { this.baseStats = new int[]{53,54,53,37,46,45};
		} else if (this.id == 91) { this.baseStats = new int[]{86,92,88,68,75,73};
		} else if (this.id == 92) { this.baseStats = new int[]{46,74,68,48,58,81};
		} else if (this.id == 93) { this.baseStats = new int[]{65,103,92,51,84,100};
		} else if (this.id == 94) { this.baseStats = new int[]{30,20,40,103,30,83};
		} else if (this.id == 95) { this.baseStats = new int[]{43,88,50,74,52,93};
		} else if (this.id == 96) { this.baseStats = new int[]{65,115,55,106,75,100};
		} else if (this.id == 97) { this.baseStats = new int[]{105,35,120,90,90,65};
		} else if (this.id == 98) { this.baseStats = new int[]{70,53,70,55,60,52};
		} else if (this.id == 99) { this.baseStats = new int[]{75,55,75,105,75,75};
		} else if (this.id == 100) { this.baseStats = new int[]{95,75,87,140,80,83};
		} else if (this.id == 101) { this.baseStats = new int[]{60,35,78,50,77,30};
		} else if (this.id == 102) { this.baseStats = new int[]{70,57,105,51,87,30};
		} else if (this.id == 103) { this.baseStats = new int[]{90,80,95,120,95,45};
		} else if (this.id == 104) { this.baseStats = new int[]{45,60,30,80,50,65};
		} else if (this.id == 105) { this.baseStats = new int[]{75,90,50,110,80,95};
		} else if (this.id == 106) { this.baseStats = new int[]{40,45,40,75,65,70};
		} else if (this.id == 107) { this.baseStats = new int[]{85,45,65,145,110,55};
		} else if (this.id == 108) { this.baseStats = new int[]{60,70,65,70,65,70};
		} else if (this.id == 109) { this.baseStats = new int[]{85,105,99,40,92,89};
		} else if (this.id == 110) { this.baseStats = new int[]{70,40,55,120,115,110};
		} else if (this.id == 111) { this.baseStats = new int[]{55,62,46,73,75,49};
		} else if (this.id == 112) { this.baseStats = new int[]{83,65,92,105,80,50};
		} else if (this.id == 113) { this.baseStats = new int[]{85,85,100,92,93,55};
		} else if (this.id == 114) { this.baseStats = new int[]{54,32,53,65,58,33};
		} else if (this.id == 115) { this.baseStats = new int[]{65,80,55,45,60,50};
		} else if (this.id == 116) { this.baseStats = new int[]{90,128,72,35,97,98};
		} else if (this.id == 117) { this.baseStats = new int[]{34,62,43,58,44,59};
		} else if (this.id == 118) { this.baseStats = new int[]{65,94,49,60,54,73};
		} else if (this.id == 119) { this.baseStats = new int[]{90,60,100,85,100,65};
		} else if (this.id == 120) { this.baseStats = new int[]{30,60,73,30,65,58};
		} else if (this.id == 121) { this.baseStats = new int[]{60,80,95,65,95,95};
		} else if (this.id == 122) { this.baseStats = new int[]{69,95,97,70,95,104};
		} else if (this.id == 123) { this.baseStats = new int[]{30,45,25,35,40,60};
		} else if (this.id == 124) { this.baseStats = new int[]{75,105,60,85,60,105};
		} else if (this.id == 125) { this.baseStats = new int[]{75,125,95,40,85,125};
		} else if (this.id == 126) { this.baseStats = new int[]{35,65,45,15,40,45};
		} else if (this.id == 127) { this.baseStats = new int[]{95,125,105,25,75,50};
		} else if (this.id == 128) { this.baseStats = new int[]{95,130,100,85,75,55};
		} else if (this.id == 129) { this.baseStats = new int[]{31,45,90,30,30,40};
		} else if (this.id == 130) { this.baseStats = new int[]{61,90,45,50,50,160};
		} else if (this.id == 131) { this.baseStats = new int[]{1,90,45,30,30,40};
		} else if (this.id == 132) { this.baseStats = new int[]{58,29,71,31,58,33};
		} else if (this.id == 133) { this.baseStats = new int[]{67,61,75,97,91,83};
		} else if (this.id == 134) { this.baseStats = new int[]{34,14,20,27,90,15};
		} else if (this.id == 135) { this.baseStats = new int[]{100,28,45,72,110,45};
		} else if (this.id == 136) { this.baseStats = new int[]{100,95,66,69,84,81};
		} else if (this.id == 137) { this.baseStats = new int[]{20,10,55,15,20,80};
		} else if (this.id == 138) { this.baseStats = new int[]{95,125,79,60,100,81};
		} else if (this.id == 139) { this.baseStats = new int[]{30,45,55,70,55,85};
		} else if (this.id == 140) { this.baseStats = new int[]{60,75,85,100,85,115};
		} else if (this.id == 141) { this.baseStats = new int[]{95,125,80,45,55,60};
		} else if (this.id == 142) { this.baseStats = new int[]{105,130,80,75,55,95};
		} else if (this.id == 143) { this.baseStats = new int[]{35,35,35,100,45,45};
		} else if (this.id == 144) { this.baseStats = new int[]{81,103,63,113,90,75};
		} else if (this.id == 145) { this.baseStats = new int[]{100,100,90,100,90,100};
		} else if (this.id == 146) { this.baseStats = new int[]{42,52,67,39,56,50};
		} else if (this.id == 147) { this.baseStats = new int[]{72,105,115,54,86,68};
		} else if (this.id == 148) { this.baseStats = new int[]{55,60,64,62,55,49};
		} else if (this.id == 149) { this.baseStats = new int[]{75,125,110,35,80,60};
		} else if (this.id == 150) { this.baseStats = new int[]{40,40,75,55,50,90};
		} else if (this.id == 151) { this.baseStats = new int[]{35,60,44,40,54,55};
		} else if (this.id == 152) { this.baseStats = new int[]{60,110,70,55,50,80};
		} else if (this.id == 153) { this.baseStats = new int[]{40,45,35,30,40,55};
		} else if (this.id == 154) { this.baseStats = new int[]{75,80,70,65,75,90};
		} else if (this.id == 155) { this.baseStats = new int[]{85,90,80,70,80,130};
		} else if (this.id == 156) { this.baseStats = new int[]{55,75,80,60,65,65};
		} else if (this.id == 157) { this.baseStats = new int[]{65,70,120,60,120,65};
		} else if (this.id == 158) { this.baseStats = new int[]{75,85,45,60,55,79};
		} else if (this.id == 159) { this.baseStats = new int[]{115,105,55,105,60,59};
		} else if (this.id == 160) { this.baseStats = new int[]{60,42,58,105,97,73};
		} else if (this.id == 161) { this.baseStats = new int[]{90,90,96,58,88,58};
		} else if (this.id == 162) { this.baseStats = new int[]{100,116,122,48,94,60};
		} else if (this.id == 163) { this.baseStats = new int[]{75,80,55,25,35,35};
		} else if (this.id == 164) { this.baseStats = new int[]{85,105,85,40,50,40};
		} else if (this.id == 165) { this.baseStats = new int[]{105,140,95,55,65,45};
		} else if (this.id == 166) { this.baseStats = new int[]{51,55,79,40,56,35};
		} else if (this.id == 167) { this.baseStats = new int[]{57,73,108,40,95,39};
		} else if (this.id == 168) { this.baseStats = new int[]{75,110,145,40,100,40};
		} else if (this.id == 169) { this.baseStats = new int[]{37,85,47,23,63,78};
		} else if (this.id == 170) { this.baseStats = new int[]{60,133,70,60,50,120};
		} else if (this.id == 171) { this.baseStats = new int[]{45,60,55,35,25,55};
		} else if (this.id == 172) { this.baseStats = new int[]{50,80,100,60,35,60};
		} else if (this.id == 173) { this.baseStats = new int[]{60,100,120,105,70,65};
		} else if (this.id == 174) { this.baseStats = new int[]{50,25,28,45,55,15};
		} else if (this.id == 175) { this.baseStats = new int[]{70,45,48,60,65,35};
		} else if (this.id == 176) { this.baseStats = new int[]{95,70,73,95,90,60};
		} else if (this.id == 177) { this.baseStats = new int[]{55,25,70,105,100,65};
		} else if (this.id == 178) { this.baseStats = new int[]{75,35,75,135,120,80};
		} else if (this.id == 179) { this.baseStats = new int[]{40,65,40,80,40,65};
		} else if (this.id == 180) { this.baseStats = new int[]{60,105,60,120,60,105};
		} else if (this.id == 181) { this.baseStats = new int[]{50,70,55,50,50,55};
		} else if (this.id == 182) { this.baseStats = new int[]{65,75,105,50,75,60};
		} else if (this.id == 183) { this.baseStats = new int[]{70,115,115,90,75,65};
		} else if (this.id == 184) { this.baseStats = new int[]{55,76,55,54,90,60};
		} else if (this.id == 185) { this.baseStats = new int[]{75,80,75,60,90,75};
		} else if (this.id == 186) { this.baseStats = new int[]{100,100,100,100,100,100};
		} else if (this.id == 187) { this.baseStats = new int[]{80,40,57,63,59,51};
		} else if (this.id == 188) { this.baseStats = new int[]{85,75,80,90,75,60};
		} else if (this.id == 189) { this.baseStats = new int[]{95,85,90,115,125,90};
		} else if (this.id == 190) { this.baseStats = new int[]{90,30,30,30,100,30};
		} else if (this.id == 191) { this.baseStats = new int[]{125,45,50,45,115,40};
		} else if (this.id == 192) { this.baseStats = new int[]{170,135,60,45,120,55};
		} else if (this.id == 193) { this.baseStats = new int[]{70,33,76,58,93,85};
		} else if (this.id == 194) { this.baseStats = new int[]{90,150,90,35,60,100};
		} else if (this.id == 195) { this.baseStats = new int[]{70,65,130,55,75,55};
		} else if (this.id == 196) { this.baseStats = new int[]{90,85,190,45,70,60};
		} else if (this.id == 197) { this.baseStats = new int[]{60,30,70,80,80,80};
		} else if (this.id == 198) { this.baseStats = new int[]{80,35,55,150,65,115};
		} else if (this.id == 199) { this.baseStats = new int[]{40,75,70,30,30,85};
		} else if (this.id == 200) { this.baseStats = new int[]{55,100,85,30,55,105};
		} else if (this.id == 201) { this.baseStats = new int[]{80,145,100,30,65,110};
		} else if (this.id == 202) { this.baseStats = new int[]{60,65,50,75,50,60};
		} else if (this.id == 203) { this.baseStats = new int[]{90,70,80,85,65,70};
		} else if (this.id == 204) { this.baseStats = new int[]{95,90,90,120,90,75};
		} else if (this.id == 205) { this.baseStats = new int[]{40,65,74,49,53,44};
		} else if (this.id == 206) { this.baseStats = new int[]{90,77,101,56,56,45};
		} else if (this.id == 207) { this.baseStats = new int[]{130,51,92,122,80,50};
		} else if (this.id == 208) { this.baseStats = new int[]{120,95,70,65,130,45};
		} else if (this.id == 209) { this.baseStats = new int[]{20,10,25,45,30,70};
		} else if (this.id == 210) { this.baseStats = new int[]{105,135,80,25,110,85};
		} else if (this.id == 211) { this.baseStats = new int[]{60,105,75,35,65,90};
		} else if (this.id == 212) { this.baseStats = new int[]{85,134,110,35,65,101};
		} else if (this.id == 213) { this.baseStats = new int[]{95,105,65,35,55,75};
		} else if (this.id == 214) { this.baseStats = new int[]{134,110,101,35,85,65};
		} else if (this.id == 215) { this.baseStats = new int[]{60,45,52,64,65,59};
		} else if (this.id == 216) { this.baseStats = new int[]{75,135,65,35,70,105};
		} else if (this.id == 217) { this.baseStats = new int[]{20,50,50,35,50,70};
		} else if (this.id == 218) { this.baseStats = new int[]{40,75,75,60,50,85};
		} else if (this.id == 219) { this.baseStats = new int[]{100,100,90,100,50,80};
		} else if (this.id == 220) { this.baseStats = new int[]{55,42,38,110,110,80};
		} else if (this.id == 221) { this.baseStats = new int[]{80,70,50,100,100,80};
		} else if (this.id == 222) { this.baseStats = new int[]{95,48,84,123,101,89};
		} else if (this.id == 223) { this.baseStats = new int[]{30,20,40,83,50,83};
		} else if (this.id == 224) { this.baseStats = new int[]{47,98,60,40,62,93};
		} else if (this.id == 225) { this.baseStats = new int[]{95,115,65,46,95,100};
		} else if (this.id == 226) { this.baseStats = new int[]{35,40,54,65,64,30};
		} else if (this.id == 227) { this.baseStats = new int[]{60,40,90,115,90,60};
		} else if (this.id == 228) { this.baseStats = new int[]{80,30,170,72,170,78};
		} else if (this.id == 229) { this.baseStats = new int[]{45,125,40,125,100,165};
		} else if (this.id == 230) { this.baseStats = new int[]{80,5,130,150,180,80};
		} else if (this.id == 231) { this.baseStats = new int[]{25,400,35,5,50,95};
		} else if (this.id == 232) { this.baseStats = new int[]{75,85,90,134,105,126};
		} else if (this.id == 233) { this.baseStats = new int[]{100,70,150,150,100,110};
		} else if (this.id == 234) { this.baseStats = new int[]{100,150,100,100,130,100};
		} else if (this.id == 235) { this.baseStats = new int[]{90,95,105,200,110,100};
		} else if (this.id == 236) { this.baseStats = new int[]{190,75,120,90,120,75};
		} else if (this.id == 237) { this.baseStats = new int[]{110,100,75,100,75,90};
		} else if (this.id == 238) { this.baseStats = new int[]{65,164,130,1,130,110};
		
		} else if (this.id == -1) {
			this.baseStats[0] = 35;
			this.baseStats[1] = 46;
			this.baseStats[2] = 45;
			this.baseStats[3] = 67;
			this.baseStats[4] = 70;
			this.baseStats[5] = 63;
			return this.baseStats;
		} else if (this.id == -2) {
			this.baseStats[0] = 64;
			this.baseStats[1] = 54;
			this.baseStats[2] = 85;
			this.baseStats[3] = 67;
			this.baseStats[4] = 78;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -3) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 70;
			this.baseStats[2] = 120;
			this.baseStats[3] = 90;
			this.baseStats[4] = 85;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -4) {
			this.baseStats[0] = 59;
			this.baseStats[1] = 46;
			this.baseStats[2] = 67;
			this.baseStats[3] = 62;
			this.baseStats[4] = 55;
			this.baseStats[5] = 31;
			return this.baseStats;
		} else if (this.id == -5) {
			this.baseStats[0] = 92;
			this.baseStats[1] = 51;
			this.baseStats[2] = 72;
			this.baseStats[3] = 75;
			this.baseStats[4] = 65;
			this.baseStats[5] = 45;
			return this.baseStats;
		} else if (this.id == -6) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 54;
			this.baseStats[2] = 75;
			this.baseStats[3] = 109;
			this.baseStats[4] = 130;
			this.baseStats[5] = 55;
			return this.baseStats;
		} else if (this.id == -7) {
			this.baseStats[0] = 44;
			this.baseStats[1] = 62;
			this.baseStats[2] = 46;
			this.baseStats[3] = 54;
			this.baseStats[4] = 55;
			this.baseStats[5] = 62;
			return this.baseStats;
		} else if (this.id == -8) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 79;
			this.baseStats[2] = 53;
			this.baseStats[3] = 55;
			this.baseStats[4] = 65;
			this.baseStats[5] = 78;
			return this.baseStats;
		} else if (this.id == -9) {
			this.baseStats[0] = 76;
			this.baseStats[1] = 81;
			this.baseStats[2] = 65;
			this.baseStats[3] = 106;
			this.baseStats[4] = 95;
			this.baseStats[5] = 108;
			return this.baseStats;
		} else if (this.id == -10) {
			this.baseStats[0] = 36;
			this.baseStats[1] = 63;
			this.baseStats[2] = 47;
			this.baseStats[3] = 24;
			this.baseStats[4] = 62;
			this.baseStats[5] = 58;
			return this.baseStats;
		} else if (this.id == -11) {
			this.baseStats[0] = 50;
			this.baseStats[1] = 105;
			this.baseStats[2] = 50;
			this.baseStats[3] = 54;
			this.baseStats[4] = 56;
			this.baseStats[5] = 105;
			return this.baseStats;
		} else if (this.id == -12) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 105;
			this.baseStats[2] = 100;
			this.baseStats[3] = 50;
			this.baseStats[4] = 30;
			this.baseStats[5] = 55;
			return this.baseStats;
		} else if (this.id == -13) {
			this.baseStats[0] = 120;
			this.baseStats[1] = 125;
			this.baseStats[2] = 120;
			this.baseStats[3] = 60;
			this.baseStats[4] = 35;
			this.baseStats[5] = 55;
			return this.baseStats;
		} else if (this.id == -14) {
			this.baseStats[0] = 50;
			this.baseStats[1] = 63;
			this.baseStats[2] = 41;
			this.baseStats[3] = 22;
			this.baseStats[4] = 63;
			this.baseStats[5] = 66;
			return this.baseStats;
		} else if (this.id == -15) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 89;
			this.baseStats[2] = 55;
			this.baseStats[3] = 25;
			this.baseStats[4] = 76;
			this.baseStats[5] = 95;
			return this.baseStats;
		} else if (this.id == -16) {
			this.baseStats[0] = 99;
			this.baseStats[1] = 61;
			this.baseStats[2] = 51;
			this.baseStats[3] = 40;
			this.baseStats[4] = 79;
			this.baseStats[5] = 20;
			return this.baseStats;
		} else if (this.id == -17) {
			this.baseStats[0] = 170;
			this.baseStats[1] = 64;
			this.baseStats[2] = 55;
			this.baseStats[3] = 43;
			this.baseStats[4] = 103;
			this.baseStats[5] = 55;
			return this.baseStats;
		} else if (this.id == -18) {
			this.baseStats[0] = 68;
			this.baseStats[1] = 72;
			this.baseStats[2] = 122;
			this.baseStats[3] = 20;
			this.baseStats[4] = 25;
			this.baseStats[5] = 18;
			return this.baseStats;
		} else if (this.id == -19) {
			this.baseStats[0] = 87;
			this.baseStats[1] = 95;
			this.baseStats[2] = 143;
			this.baseStats[3] = 25;
			this.baseStats[4] = 30;
			this.baseStats[5] = 45;
			return this.baseStats;
		} else if (this.id == -20) {
			this.baseStats[0] = 90;
			this.baseStats[1] = 100;
			this.baseStats[2] = 143;
			this.baseStats[3] = 100;
			this.baseStats[4] = 42;
			this.baseStats[5] = 50;
			return this.baseStats;
		} else if (this.id == -21) {
			this.baseStats[0] = 45;
			this.baseStats[1] = 40;
			this.baseStats[2] = 105;
			this.baseStats[3] = 40;
			this.baseStats[4] = 75;
			this.baseStats[5] = 40;
			return this.baseStats;
		} else if (this.id == -22) {
			this.baseStats[0] = 81;
			this.baseStats[1] = 66;
			this.baseStats[2] = 105;
			this.baseStats[3] = 40;
			this.baseStats[4] = 75;
			this.baseStats[5] = 45;
			return this.baseStats;
		} else if (this.id == -23) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 110;
			this.baseStats[2] = 105;
			this.baseStats[3] = 44;
			this.baseStats[4] = 78;
			this.baseStats[5] = 93;
			return this.baseStats;
		} else if (this.id == -24) {
			this.baseStats[0] = 35;
			this.baseStats[1] = 103;
			this.baseStats[2] = 62;
			this.baseStats[3] = 25;
			this.baseStats[4] = 30;
			this.baseStats[5] = 95;
			return this.baseStats;
		} else if (this.id == -25) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 80;
			this.baseStats[2] = 65;
			this.baseStats[3] = 80;
			this.baseStats[4] = 70;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -26) {
			this.baseStats[0] = 68;
			this.baseStats[1] = 35;
			this.baseStats[2] = 66;
			this.baseStats[3] = 45;
			this.baseStats[4] = 60;
			this.baseStats[5] = 44;
			return this.baseStats;
		} else if (this.id == -27) {
			this.baseStats[0] = 70;
			this.baseStats[1] = 40;
			this.baseStats[2] = 67;
			this.baseStats[3] = 73;
			this.baseStats[4] = 70;
			this.baseStats[5] = 80;
			return this.baseStats;
		} else if (this.id == -28) {
			this.baseStats[0] = 72;
			this.baseStats[1] = 91;
			this.baseStats[2] = 53;
			this.baseStats[3] = 80;
			this.baseStats[4] = 75;
			this.baseStats[5] = 124;
			return this.baseStats;
		} else if (this.id == -29) {
			this.baseStats[0] = 30;
			this.baseStats[1] = 36;
			this.baseStats[2] = 64;
			this.baseStats[3] = 40;
			this.baseStats[4] = 70;
			this.baseStats[5] = 72;
			return this.baseStats;
		} else if (this.id == -30) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 65;
			this.baseStats[2] = 125;
			this.baseStats[3] = 45;
			this.baseStats[4] = 68;
			this.baseStats[5] = 74;
			return this.baseStats;
		} else if (this.id == -31) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 96;
			this.baseStats[2] = 80;
			this.baseStats[3] = 65;
			this.baseStats[4] = 70;
			this.baseStats[5] = 74;
			return this.baseStats;
		} else if (this.id == -32) {
			this.baseStats[0] = 90;
			this.baseStats[1] = 90;
			this.baseStats[2] = 65;
			this.baseStats[3] = 35;
			this.baseStats[4] = 100;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -33) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 64;
			this.baseStats[2] = 78;
			this.baseStats[3] = 35;
			this.baseStats[4] = 56;
			this.baseStats[5] = 37;
			return this.baseStats;
		} else if (this.id == -34) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 80;
			this.baseStats[2] = 80;
			this.baseStats[3] = 75;
			this.baseStats[4] = 65;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -35) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 87;
			this.baseStats[2] = 71;
			this.baseStats[3] = 63;
			this.baseStats[4] = 60;
			this.baseStats[5] = 64;
			return this.baseStats;
		} else if (this.id == -36) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 73;
			this.baseStats[2] = 40;
			this.baseStats[3] = 105;
			this.baseStats[4] = 45;
			this.baseStats[5] = 97;
			return this.baseStats;
		} else if (this.id == -37) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 42;
			this.baseStats[2] = 58;
			this.baseStats[3] = 105;
			this.baseStats[4] = 97;
			this.baseStats[5] = 73;
			return this.baseStats;
		} else if (this.id == -38) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 75;
			this.baseStats[2] = 80;
			this.baseStats[3] = 60;
			this.baseStats[4] = 65;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -39) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 70;
			this.baseStats[2] = 120;
			this.baseStats[3] = 60;
			this.baseStats[4] = 120;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -40) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 80;
			this.baseStats[2] = 55;
			this.baseStats[3] = 65;
			this.baseStats[4] = 60;
			this.baseStats[5] = 64;
			return this.baseStats;
		} else if (this.id == -41) {
			this.baseStats[0] = 125;
			this.baseStats[1] = 75;
			this.baseStats[2] = 55;
			this.baseStats[3] = 120;
			this.baseStats[4] = 60;
			this.baseStats[5] = 64;
			return this.baseStats;
		} else if (this.id == -42) {
			this.baseStats[0] = 63;
			this.baseStats[1] = 32;
			this.baseStats[2] = 44;
			this.baseStats[3] = 118;
			this.baseStats[4] = 89;
			this.baseStats[5] = 90;
			return this.baseStats;
		} else if (this.id == -43) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 100;
			this.baseStats[2] = 60;
			this.baseStats[3] = 105;
			this.baseStats[4] = 60;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -44) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 62;
			this.baseStats[2] = 46;
			this.baseStats[3] = 73;
			this.baseStats[4] = 75;
			this.baseStats[5] = 49;
			return this.baseStats;
		} else if (this.id == -45) {
			this.baseStats[0] = 83;
			this.baseStats[1] = 65;
			this.baseStats[2] = 102;
			this.baseStats[3] = 115;
			this.baseStats[4] = 90;
			this.baseStats[5] = 50;
			return this.baseStats;
		} else if (this.id == -46) {
			this.baseStats[0] = 35;
			this.baseStats[1] = 65;
			this.baseStats[2] = 45;
			this.baseStats[3] = 75;
			this.baseStats[4] = 47;
			this.baseStats[5] = 63;
			return this.baseStats;
		} else if (this.id == -47) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 59;
			this.baseStats[2] = 69;
			this.baseStats[3] = 135;
			this.baseStats[4] = 71;
			this.baseStats[5] = 93;
			return this.baseStats;
		} else if (this.id == -48) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 60;
			this.baseStats[2] = 65;
			this.baseStats[3] = 30;
			this.baseStats[4] = 50;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -49) {
			this.baseStats[0] = 53;
			this.baseStats[1] = 95;
			this.baseStats[2] = 85;
			this.baseStats[3] = 30;
			this.baseStats[4] = 53;
			this.baseStats[5] = 85;
			return this.baseStats;
		} else if (this.id == -50) {
			this.baseStats[0] = 68;
			this.baseStats[1] = 120;
			this.baseStats[2] = 100;
			this.baseStats[3] = 72;
			this.baseStats[4] = 80;
			this.baseStats[5] = 105;
			return this.baseStats;
		} else if (this.id == -51) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 60;
			this.baseStats[2] = 45;
			this.baseStats[3] = 20;
			this.baseStats[4] = 75;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -52) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 105;
			this.baseStats[2] = 70;
			this.baseStats[3] = 20;
			this.baseStats[4] = 115;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -53) {
			this.baseStats[0] = 68;
			this.baseStats[1] = 92;
			this.baseStats[2] = 68;
			this.baseStats[3] = 20;
			this.baseStats[4] = 84;
			this.baseStats[5] = 118;
			return this.baseStats;
		} else if (this.id == -54) {
			this.baseStats[0] = 70;
			this.baseStats[1] = 125;
			this.baseStats[2] = 85;
			this.baseStats[3] = 20;
			this.baseStats[4] = 85;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -55) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 54;
			this.baseStats[2] = 77;
			this.baseStats[3] = 60;
			this.baseStats[4] = 72;
			this.baseStats[5] = 32;
			return this.baseStats;
		} else if (this.id == -56) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 90;
			this.baseStats[2] = 77;
			this.baseStats[3] = 95;
			this.baseStats[4] = 72;
			this.baseStats[5] = 31;
			return this.baseStats;
		} else if (this.id == -57) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 75;
			this.baseStats[2] = 40;
			this.baseStats[3] = 30;
			this.baseStats[4] = 40;
			this.baseStats[5] = 93;
			return this.baseStats;
		} else if (this.id == -58) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 102;
			this.baseStats[2] = 65;
			this.baseStats[3] = 35;
			this.baseStats[4] = 50;
			this.baseStats[5] = 107;
			return this.baseStats;
		} else if (this.id == -59) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 60;
			this.baseStats[2] = 40;
			this.baseStats[3] = 60;
			this.baseStats[4] = 55;
			this.baseStats[5] = 80;
			return this.baseStats;
		} else if (this.id == -60) {
			this.baseStats[0] = 105;
			this.baseStats[1] = 88;
			this.baseStats[2] = 60;
			this.baseStats[3] = 112;
			this.baseStats[4] = 65;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -61) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 32;
			this.baseStats[2] = 75;
			this.baseStats[3] = 55;
			this.baseStats[4] = 100;
			this.baseStats[5] = 73;
			return this.baseStats;
		} else if (this.id == -62) {
			this.baseStats[0] = 71;
			this.baseStats[1] = 65;
			this.baseStats[2] = 110;
			this.baseStats[3] = 60;
			this.baseStats[4] = 110;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -63) {
			this.baseStats[0] = 30;
			this.baseStats[1] = 30;
			this.baseStats[2] = 88;
			this.baseStats[3] = 70;
			this.baseStats[4] = 55;
			this.baseStats[5] = 45;
			return this.baseStats;
		} else if (this.id == -64) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 113;
			this.baseStats[2] = 58;
			this.baseStats[3] = 83;
			this.baseStats[4] = 25;
			this.baseStats[5] = 127;
			return this.baseStats;
		} else if (this.id == -65) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 28;
			this.baseStats[2] = 45;
			this.baseStats[3] = 72;
			this.baseStats[4] = 110;
			this.baseStats[5] = 45;
			return this.baseStats;
		} else if (this.id == -66) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 100;
			this.baseStats[2] = 76;
			this.baseStats[3] = 34;
			this.baseStats[4] = 94;
			this.baseStats[5] = 91;
			return this.baseStats;
		} else if (this.id == -67) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 40;
			this.baseStats[2] = 90;
			this.baseStats[3] = 40;
			this.baseStats[4] = 66;
			this.baseStats[5] = 34;
			return this.baseStats;
		} else if (this.id == -68) {
			this.baseStats[0] = 66;
			this.baseStats[1] = 76;
			this.baseStats[2] = 115;
			this.baseStats[3] = 54;
			this.baseStats[4] = 79;
			this.baseStats[5] = 40;
			return this.baseStats;
		} else if (this.id == -69) {
			this.baseStats[0] = 105;
			this.baseStats[1] = 125;
			this.baseStats[2] = 100;
			this.baseStats[3] = 45;
			this.baseStats[4] = 55;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -70) {
			this.baseStats[0] = 81;
			this.baseStats[1] = 103;
			this.baseStats[2] = 63;
			this.baseStats[3] = 113;
			this.baseStats[4] = 90;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -71) {
			this.baseStats[0] = 51;
			this.baseStats[1] = 55;
			this.baseStats[2] = 69;
			this.baseStats[3] = 40;
			this.baseStats[4] = 56;
			this.baseStats[5] = 35;
			return this.baseStats;
		} else if (this.id == -72) {
			this.baseStats[0] = 57;
			this.baseStats[1] = 73;
			this.baseStats[2] = 108;
			this.baseStats[3] = 40;
			this.baseStats[4] = 95;
			this.baseStats[5] = 39;
			return this.baseStats;
		} else if (this.id == -73) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 110;
			this.baseStats[2] = 110;
			this.baseStats[3] = 75;
			this.baseStats[4] = 400;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -74) {
			this.baseStats[0] = 30;
			this.baseStats[1] = 20;
			this.baseStats[2] = 40;
			this.baseStats[3] = 103;
			this.baseStats[4] = 30;
			this.baseStats[5] = 83;
			return this.baseStats;
		} else if (this.id == -75) {
			this.baseStats[0] = 43;
			this.baseStats[1] = 88;
			this.baseStats[2] = 50;
			this.baseStats[3] = 74;
			this.baseStats[4] = 52;
			this.baseStats[5] = 93;
			return this.baseStats;
		} else if (this.id == -76) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 115;
			this.baseStats[2] = 55;
			this.baseStats[3] = 106;
			this.baseStats[4] = 75;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -77) {
			this.baseStats[0] = 70;
			this.baseStats[1] = 53;
			this.baseStats[2] = 70;
			this.baseStats[3] = 55;
			this.baseStats[4] = 60;
			this.baseStats[5] = 52;
			return this.baseStats;
		} else if (this.id == -78) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 55;
			this.baseStats[2] = 75;
			this.baseStats[3] = 105;
			this.baseStats[4] = 75;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -79) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 75;
			this.baseStats[2] = 87;
			this.baseStats[3] = 140;
			this.baseStats[4] = 80;
			this.baseStats[5] = 83;
			return this.baseStats;
		} else if (this.id == -80) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 40;
			this.baseStats[2] = 105;
			this.baseStats[3] = 100;
			this.baseStats[4] = 65;
			this.baseStats[5] = 42;
			return this.baseStats;
		} else if (this.id == -81) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 90;
			this.baseStats[2] = 95;
			this.baseStats[3] = 90;
			this.baseStats[4] = 65;
			this.baseStats[5] = 82;
			return this.baseStats;
		} else if (this.id == -82) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 40;
			this.baseStats[2] = 55;
			this.baseStats[3] = 58;
			this.baseStats[4] = 95;
			this.baseStats[5] = 62;
			return this.baseStats;
		} else if (this.id == -83) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 60;
			this.baseStats[2] = 63;
			this.baseStats[3] = 105;
			this.baseStats[4] = 95;
			this.baseStats[5] = 67;
			return this.baseStats;
		} else if (this.id == -84) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 55;
			this.baseStats[2] = 60;
			this.baseStats[3] = 75;
			this.baseStats[4] = 80;
			this.baseStats[5] = 66;
			return this.baseStats;
		} else if (this.id == -85) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 59;
			this.baseStats[2] = 63;
			this.baseStats[3] = 100;
			this.baseStats[4] = 96;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -86) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 69;
			this.baseStats[2] = 80;
			this.baseStats[3] = 30;
			this.baseStats[4] = 70;
			this.baseStats[5] = 67;
			return this.baseStats;
		} else if (this.id == -87) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 110;
			this.baseStats[2] = 125;
			this.baseStats[3] = 70;
			this.baseStats[4] = 75;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -88) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 165;
			this.baseStats[2] = 105;
			this.baseStats[3] = 15;
			this.baseStats[4] = 45;
			this.baseStats[5] = 115;
			return this.baseStats;
		} else if (this.id == -89) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 65;
			this.baseStats[2] = 55;
			this.baseStats[3] = 60;
			this.baseStats[4] = 60;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -90) {
			this.baseStats[0] = 105;
			this.baseStats[1] = 110;
			this.baseStats[2] = 65;
			this.baseStats[3] = 60;
			this.baseStats[4] = 95;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -91) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 110;
			this.baseStats[2] = 75;
			this.baseStats[3] = 110;
			this.baseStats[4] = 100;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -92) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 20;
			this.baseStats[2] = 30;
			this.baseStats[3] = 120;
			this.baseStats[4] = 105;
			this.baseStats[5] = 110;
			return this.baseStats;
		} else if (this.id == -93) {
			this.baseStats[0] = 120;
			this.baseStats[1] = 35;
			this.baseStats[2] = 40;
			this.baseStats[3] = 100;
			this.baseStats[4] = 65;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -94) {
			this.baseStats[0] = 120;
			this.baseStats[1] = 60;
			this.baseStats[2] = 60;
			this.baseStats[3] = 120;
			this.baseStats[4] = 80;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -95) {
			this.baseStats[0] = 30;
			this.baseStats[1] = 60;
			this.baseStats[2] = 73;
			this.baseStats[3] = 30;
			this.baseStats[4] = 65;
			this.baseStats[5] = 58;
			return this.baseStats;
		} else if (this.id == -96) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 80;
			this.baseStats[2] = 95;
			this.baseStats[3] = 65;
			this.baseStats[4] = 95;
			this.baseStats[5] = 95;
			return this.baseStats;
		} else if (this.id == -97) {
			this.baseStats[0] = 50;
			this.baseStats[1] = 70;
			this.baseStats[2] = 55;
			this.baseStats[3] = 50;
			this.baseStats[4] = 50;
			this.baseStats[5] = 55;
			return this.baseStats;
		} else if (this.id == -98) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 75;
			this.baseStats[2] = 105;
			this.baseStats[3] = 50;
			this.baseStats[4] = 75;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -99) {
			this.baseStats[0] = 70;
			this.baseStats[1] = 115;
			this.baseStats[2] = 115;
			this.baseStats[3] = 90;
			this.baseStats[4] = 75;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -100) {
			this.baseStats[0] = 80;
			this.baseStats[1] = 40;
			this.baseStats[2] = 57;
			this.baseStats[3] = 63;
			this.baseStats[4] = 59;
			this.baseStats[5] = 51;
			return this.baseStats;
		} else if (this.id == -101) {
			this.baseStats[0] = 85;
			this.baseStats[1] = 75;
			this.baseStats[2] = 80;
			this.baseStats[3] = 90;
			this.baseStats[4] = 75;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -102) {
			this.baseStats[0] = 90;
			this.baseStats[1] = 85;
			this.baseStats[2] = 85;
			this.baseStats[3] = 100;
			this.baseStats[4] = 150;
			this.baseStats[5] = 90;
			return this.baseStats;
		} else if (this.id == -103) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 125;
			this.baseStats[2] = 105;
			this.baseStats[3] = 65;
			this.baseStats[4] = 95;
			this.baseStats[5] = 85;
			return this.baseStats;
		} else if (this.id == -104) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 85;
			this.baseStats[2] = 65;
			this.baseStats[3] = 105;
			this.baseStats[4] = 95;
			this.baseStats[5] = 125;
			return this.baseStats;
		} else if (this.id == -105) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 65;
			this.baseStats[2] = 85;
			this.baseStats[3] = 125;
			this.baseStats[4] = 95;
			this.baseStats[5] = 105;
			return this.baseStats;
		} else if (this.id == -106) {
			this.baseStats[0] = 50;
			this.baseStats[1] = 40;
			this.baseStats[2] = 50;
			this.baseStats[3] = 120;
			this.baseStats[4] = 90;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -107) {
			this.baseStats[0] = 40;
			this.baseStats[1] = 45;
			this.baseStats[2] = 40;
			this.baseStats[3] = 75;
			this.baseStats[4] = 65;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -108) {
			this.baseStats[0] = 85;
			this.baseStats[1] = 60;
			this.baseStats[2] = 65;
			this.baseStats[3] = 100;
			this.baseStats[4] = 110;
			this.baseStats[5] = 85;
			return this.baseStats;
		} else if (this.id == -109) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 76;
			this.baseStats[2] = 55;
			this.baseStats[3] = 54;
			this.baseStats[4] = 90;
			this.baseStats[5] = 60;
			return this.baseStats;
		} else if (this.id == -110) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 80;
			this.baseStats[2] = 75;
			this.baseStats[3] = 60;
			this.baseStats[4] = 90;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -111) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 100;
			this.baseStats[2] = 100;
			this.baseStats[3] = 100;
			this.baseStats[4] = 100;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -112) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 75;
			this.baseStats[2] = 70;
			this.baseStats[3] = 55;
			this.baseStats[4] = 65;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -113) {
			this.baseStats[0] = 120;
			this.baseStats[1] = 75;
			this.baseStats[2] = 100;
			this.baseStats[3] = 85;
			this.baseStats[4] = 65;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -114) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 65;
			this.baseStats[2] = 85;
			this.baseStats[3] = 120;
			this.baseStats[4] = 100;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -115) {
			this.baseStats[0] = 85;
			this.baseStats[1] = 120;
			this.baseStats[2] = 75;
			this.baseStats[3] = 100;
			this.baseStats[4] = 75;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -116) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 120;
			this.baseStats[2] = 85;
			this.baseStats[3] = 75;
			this.baseStats[4] = 65;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -117) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 75;
			this.baseStats[2] = 65;
			this.baseStats[3] = 85;
			this.baseStats[4] = 120;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -118) {
			this.baseStats[0] = 85;
			this.baseStats[1] = 100;
			this.baseStats[2] = 120;
			this.baseStats[3] = 75;
			this.baseStats[4] = 75;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -119) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 65;
			this.baseStats[2] = 85;
			this.baseStats[3] = 100;
			this.baseStats[4] = 120;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -120) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 75;
			this.baseStats[2] = 120;
			this.baseStats[3] = 85;
			this.baseStats[4] = 100;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -121) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 120;
			this.baseStats[2] = 85;
			this.baseStats[3] = 65;
			this.baseStats[4] = 100;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -122) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 100;
			this.baseStats[2] = 75;
			this.baseStats[3] = 120;
			this.baseStats[4] = 65;
			this.baseStats[5] = 85;
			return this.baseStats;
		} else if (this.id == -123) {
			this.baseStats[0] = 65;
			this.baseStats[1] = 75;
			this.baseStats[2] = 75;
			this.baseStats[3] = 120;
			this.baseStats[4] = 100;
			this.baseStats[5] = 85;
			return this.baseStats;
		} else if (this.id == -124) {
			this.baseStats[0] = 57;
			this.baseStats[1] = 51;
			this.baseStats[2] = 50;
			this.baseStats[3] = 50;
			this.baseStats[4] = 52;
			this.baseStats[5] = 50;
			return this.baseStats;
		} else if (this.id == -125) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 79;
			this.baseStats[2] = 71;
			this.baseStats[3] = 55;
			this.baseStats[4] = 75;
			this.baseStats[5] = 65;
			return this.baseStats;
		} else if (this.id == -126) {
			this.baseStats[0] = 90;
			this.baseStats[1] = 95;
			this.baseStats[2] = 86;
			this.baseStats[3] = 105;
			this.baseStats[4] = 80;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -127) {
			this.baseStats[0] = 52;
			this.baseStats[1] = 53;
			this.baseStats[2] = 52;
			this.baseStats[3] = 58;
			this.baseStats[4] = 54;
			this.baseStats[5] = 62;
			return this.baseStats;
		} else if (this.id == -128) {
			this.baseStats[0] = 54;
			this.baseStats[1] = 85;
			this.baseStats[2] = 63;
			this.baseStats[3] = 63;
			this.baseStats[4] = 69;
			this.baseStats[5] = 76;
			return this.baseStats;
		} else if (this.id == -129) {
			this.baseStats[0] = 58;
			this.baseStats[1] = 90;
			this.baseStats[2] = 95;
			this.baseStats[3] = 100;
			this.baseStats[4] = 95;
			this.baseStats[5] = 77;
			return this.baseStats;
		} else if (this.id == -130) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 67;
			this.baseStats[2] = 65;
			this.baseStats[3] = 51;
			this.baseStats[4] = 40;
			this.baseStats[5] = 52;
			return this.baseStats;
		} else if (this.id == -131) {
			this.baseStats[0] = 75;
			this.baseStats[1] = 99;
			this.baseStats[2] = 86;
			this.baseStats[3] = 56;
			this.baseStats[4] = 45;
			this.baseStats[5] = 61;
			return this.baseStats;
		} else if (this.id == -132) {
			this.baseStats[0] = 95;
			this.baseStats[1] = 80;
			this.baseStats[2] = 76;
			this.baseStats[3] = 128;
			this.baseStats[4] = 60;
			this.baseStats[5] = 75;
			return this.baseStats;
		} else if (this.id == -133) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 115;
			this.baseStats[2] = 85;
			this.baseStats[3] = 120;
			this.baseStats[4] = 105;
			this.baseStats[5] = 125;
			return this.baseStats;
		} else if (this.id == -134) {
			this.baseStats[0] = 110;
			this.baseStats[1] = 85;
			this.baseStats[2] = 120;
			this.baseStats[3] = 155;
			this.baseStats[4] = 80;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -135) {
			this.baseStats[0] = 70;
			this.baseStats[1] = 50;
			this.baseStats[2] = 90;
			this.baseStats[3] = 100;
			this.baseStats[4] = 90;
			this.baseStats[5] = 52;
			return this.baseStats;
		} else if (this.id == -136) {
			this.baseStats[0] = 50;
			this.baseStats[1] = 50;
			this.baseStats[2] = 100;
			this.baseStats[3] = 80;
			this.baseStats[4] = 100;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -137) {
			this.baseStats[0] = 60;
			this.baseStats[1] = 55;
			this.baseStats[2] = 90;
			this.baseStats[3] = 55;
			this.baseStats[4] = 80;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -138) {
			this.baseStats[0] = 90;
			this.baseStats[1] = 55;
			this.baseStats[2] = 80;
			this.baseStats[3] = 195;
			this.baseStats[4] = 100;
			this.baseStats[5] = 90;
			return this.baseStats;
		} else if (this.id == -139) {
			this.baseStats[0] = 100;
			this.baseStats[1] = 70;
			this.baseStats[2] = 125;
			this.baseStats[3] = 120;
			this.baseStats[4] = 100;
			this.baseStats[5] = 95;
			return this.baseStats;
		} else if (this.id == -140) {
			this.baseStats[0] = 130;
			this.baseStats[1] = 90;
			this.baseStats[2] = 110;
			this.baseStats[3] = 80;
			this.baseStats[4] = 110;
			this.baseStats[5] = 100;
			return this.baseStats;
		} else if (this.id == -141) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 60;
			this.baseStats[2] = 40;
			this.baseStats[3] = 60;
			this.baseStats[4] = 55;
			this.baseStats[5] = 80;
			return this.baseStats;
		} else if (this.id == -143) {
			this.baseStats[0] = 105;
			this.baseStats[1] = 88;
			this.baseStats[2] = 60;
			this.baseStats[3] = 112;
			this.baseStats[4] = 65;
			this.baseStats[5] = 70;
			return this.baseStats;
		} else if (this.id == -142) {
			this.baseStats[0] = 55;
			this.baseStats[1] = 60;
			this.baseStats[2] = 40;
			this.baseStats[3] = 60;
			this.baseStats[4] = 55;
			this.baseStats[5] = 80;
			return this.baseStats;
		} else if (this.id == -144) {
			this.baseStats[0] = 105;
			this.baseStats[1] = 88;
			this.baseStats[2] = 60;
			this.baseStats[3] = 112;
			this.baseStats[4] = 65;
			this.baseStats[5] = 70;
			return this.baseStats;
		}
		return this.baseStats;
	}

	
	public void move(Pokemon foe, Move move, Player player, Field field, Pokemon[] team, boolean first) {
		if (this.fainted || foe.fainted) return;

		double attackStat;
		double defenseStat;
		int damage = 0;
		int bp = move.basePower;
		int acc = move.accuracy;
		int secChance = move.secondary;
		PType moveType = move.mtype;
		int critChance = move.critChance;
		
		if (!this.vStatuses.contains(Status.CHARGING) && !this.vStatuses.contains(Status.SEMI_INV) && !this.vStatuses.contains(Status.LOCKED) &&
				!this.vStatuses.contains(Status.ENCORED) && move != Move.MAGIC_REFLECT && move != Move.TAKE_OVER && move != Move.ABDUCT &&
				move != Move.DESTINY_BOND && move != Move.DETECT && move != Move.PROTECT && move != Move.MOLTEN_LAIR && move != Move.OBSTRUCT &&
				move != Move.SPIKY_SHIELD) this.lastMoveUsed = move;
		
		if (move == Move.FAILED_SUCKER) this.lastMoveUsed = Move.SUCKER_PUNCH;
		if (this.vStatuses.contains(Status.ENCORED)) move = this.lastMoveUsed;
		
		if (this.vStatuses.contains(Status.CONFUSED)) {
			if (this.confusionCounter == 0) {
				this.vStatuses.remove(Status.CONFUSED);
		        System.out.print("\n" + this.nickname + " snapped out of confusion!");
			} else {
				System.out.print("\n" + this.nickname + " is confused!");
				if (Math.random() < 1.0/3.0) {
			        // user hits themselves
					System.out.println("\n" + this.nickname + " hit itself in confusion!");
					attackStat = this.getStat(1);
					defenseStat = this.getStat(2);
					attackStat *= this.asModifier(0);
					defenseStat *= this.asModifier(1);
					damage = calc(attackStat, defenseStat, 40, this.level);
					this.currentHP -= damage;
					if (this.currentHP <= 0) {
						this.faint(true, player, foe);
						foe.awardxp((int) Math.ceil(this.level * trainer), player);
					}
					confusionCounter--;
					endMove();
					this.vStatuses.remove(Status.LOCKED);
					this.vStatuses.remove(Status.CHARGING);
					return;
				} else {
					confusionCounter--;
				}
			}
		}
		if (this.status == Status.PARALYZED && Math.random() < 0.25) {
			System.out.println("\n" + this.nickname + " is fully paralyzed!");
			this.moveMultiplier = 0;
			this.impressive = false;
			this.vStatuses.remove(Status.LOCKED);
			this.vStatuses.remove(Status.CHARGING);
			return;
		}
		
		if (this.status == Status.ASLEEP) {
			if (this.sleepCounter > 0) {
				System.out.println("\n" + this.nickname + " is fast asleep.");
				this.sleepCounter--;
				if (move == Move.SLEEP_TALK) {
					System.out.println("\n" + this.nickname + " used " + move + "!");
					ArrayList<Move> moves = new ArrayList<>();
					for (Move m : this.moveset) {
						if (m != null && m != Move.SLEEP_TALK) moves.add(m);
					}
					move = moves.get(new Random().nextInt(moves.size()));
					bp = move.basePower;
					acc = move.accuracy;
					secChance = move.secondary;
					moveType = move.mtype;
					critChance = move.critChance;
				} else {
					this.impressive = false;
					this.vStatuses.remove(Status.LOCKED);
					this.vStatuses.remove(Status.CHARGING);
					return;
				}
			} else {
				System.out.println("\n" + this.nickname + " woke up!");
				this.status = Status.HEALTHY;
			}
		}
		
		if (this.status != Status.TOXIC) toxic = 0;
		if (foe.status != Status.TOXIC) foe.toxic = 0;
		
		if (this.vStatuses.contains(Status.FLINCHED) && this.ability != Ability.INNER_FOCUS) {
			System.out.println("\n" + this.nickname + " flinched!");
			this.vStatuses.remove(Status.FLINCHED);
			this.impressive = false;
			this.vStatuses.remove(Status.LOCKED);
			this.vStatuses.remove(Status.CHARGING);
			return;
		}
		if (this.vStatuses.contains(Status.RECHARGE)) {
			System.out.println("\n" + this.nickname + " must recharge!");
			this.vStatuses.remove(Status.RECHARGE);
			this.impressive = false;
			this.vStatuses.remove(Status.LOCKED);
			this.vStatuses.remove(Status.CHARGING);
			return;
		}
		if (move == Move.SKULL_BASH || move == Move.SKY_ATTACK || ((move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) && !field.equals(field.weather, Effect.SUN)) || this.vStatuses.contains(Status.CHARGING) || move == Move.BLACK_HOLE_ECLIPSE || move == Move.GEOMANCY) {
			if (!this.vStatuses.contains(Status.CHARGING)) {
				System.out.println("\n" + this.nickname + " used " + move + "!");
				System.out.println(this.nickname + " started charging up!");
				this.vStatuses.add(Status.CHARGING);
				if (move == Move.SKULL_BASH) stat(this, 1, 1);
				if (move == Move.BLACK_HOLE_ECLIPSE) stat(this, 3, 1);
				this.impressive = false;
				return;
			} else {
				move = this.lastMoveUsed;
				this.vStatuses.remove(Status.CHARGING);
			}
		}
		
		if (move == Move.DIG || move == Move.DIVE || move == Move.FLY || move == Move.BOUNCE || move == Move.PHANTOM_FORCE || this.vStatuses.contains(Status.SEMI_INV)) {
			if (!this.vStatuses.contains(Status.SEMI_INV)) {
				System.out.println("\n" + this.nickname + " used " + move + "!");
				if (move == Move.DIG) System.out.println(this.nickname + " burrowed underground!");
				if (move == Move.DIVE) System.out.println(this.nickname + " dove underwater!");
				if (move == Move.FLY) System.out.println(this.nickname + " flew up high!");
				if (move == Move.BOUNCE) System.out.println(this.nickname + " sprang up!");
				if (move == Move.PHANTOM_FORCE) System.out.println(this.nickname + " vanished instantly!");
				this.vStatuses.add(Status.SEMI_INV);
				this.impressive = false;
				return;
			} else {
				move = this.lastMoveUsed;
				this.vStatuses.remove(Status.SEMI_INV);
			}
		}
		
		if (foe.vStatuses.contains(Status.PROTECT) && (move.accuracy <= 100 || move.cat != 2) && move != Move.FEINT) {
			System.out.println("\n" + this.nickname + " used " + move + "!");
			System.out.println(foe.nickname + " protected itself!");
			if (move.contact) {
				if (foe.lastMoveUsed == Move.OBSTRUCT) stat(this, 1, -2);
				if (foe.lastMoveUsed == Move.MOLTEN_LAIR) burn(false, foe);
				if (foe.lastMoveUsed == Move.SPIKY_SHIELD && this.ability != Ability.MAGIC_GUARD) {
					this.currentHP -= Math.max(this.getStat(0) / 8, 1);
					System.out.println(this.nickname + " was hurt!");
					if (this.currentHP <= 0) { // Check for kill
						this.faint(true, player, foe);
						foe.awardxp((int) Math.ceil(this.level * this.trainer), player);
					}
				}
			}
			this.impressive = false;
			return;
		}
		
		if (this.vStatuses.contains(Status.LOCKED) && (lastMoveUsed == Move.OUTRAGE || lastMoveUsed == Move.PETAL_DANCE || lastMoveUsed == Move.THRASH ||
				lastMoveUsed == Move.ROLLOUT || lastMoveUsed == Move.ICE_BALL)) {
			move = lastMoveUsed;
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (move == Move.OUTRAGE || move == Move.PETAL_DANCE || move == Move.THRASH) {
			if (!this.vStatuses.contains(Status.LOCKED)) {
				this.vStatuses.add(Status.LOCKED);
				this.outCount = (int)(Math.random()*2) + 2;
			}
			this.outCount--;
		}
		if (move == Move.ROLLOUT || move == Move.ICE_BALL) {
			if (!this.vStatuses.contains(Status.LOCKED)) {
				this.vStatuses.add(Status.LOCKED);
				this.rollCount = 0;
			}
			this.rollCount++;
		}
		
		if (id == 237) {
			move = get150Move(move);
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (move == Move.MIRROR_MOVE) {
			System.out.print("\n" + this.nickname + " used Mirror Move!");
			move = foe.lastMoveUsed;
			if (move == null) {
				System.out.println("\nBut it failed!");
				this.impressive = false;
				return;
			}
		}
		if (foe.vStatuses.contains(Status.REFLECT) && (move != Move.BRICK_BREAK && move != Move.MAGIC_FANG && move != Move.PSYCHIC_FANGS)) {
			this.move(this, move, player, field, team, false);
			System.out.println(move + " was reflected on itself!");
			foe.vStatuses.remove(Status.REFLECT);
			return;
		}
		if (this.vStatuses.contains(Status.POSESSED)) {
			this.vStatuses.remove(Status.POSESSED);
			this.move(this, move, player, field, team, false);
			System.out.println(move + " was used on itself!");
			return;
		}
		if (move == Move.FAILED_SUCKER) {
			System.out.println("\n" + this.nickname + " used Sucker Punch!");
			fail();
			this.impressive = false;
			return;
		}
		if (move == Move.METRONOME) {
			System.out.print("\n" + this.nickname + " used " + move + "!");
			Move[] moves = Move.values();
			move = moves[new Random().nextInt(moves.length)];
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		if (move == Move.SLEEP_TALK) {
			System.out.println("\n" + this.nickname + " used " + move + "!");
			fail();
			this.impressive = false;
			return;
		}
		
		if ((move == Move.FIRST_IMPRESSION || move == Move.BELCH || move == Move.UNSEEN_STRANGLE || move == Move.FAKE_OUT) && !this.impressive) {
			System.out.print("\n" + this.nickname + " used " + move + "!");
			fail();
			return;
		}
		
		if (this.ability == Ability.COMPOUND_EYES) acc *= 1.3;
		if (field.contains(field.fieldEffects, Effect.GRAVITY)) acc = acc * 5 / 3;
		
		if (field.equals(field.weather, Effect.SUN)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 50;
		}
		
		if (field.equals(field.weather, Effect.RAIN)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 1000;
		}
		
		if (field.equals(field.weather, Effect.SNOW)) {
			if (move == Move.BLIZZARD) acc = 1000;
		}
		
		if (move == Move.TOXIC && (this.type1 == PType.POISON || this.type2 == PType.POISON)) acc = 1000;
		
		if (this.ability != Ability.NO_GUARD && foe.ability != Ability.NO_GUARD) {
			int accEv = this.statStages[5] - foe.statStages[6];
			if (move == Move.DARKEST_LARIAT || move == Move.SACRED_SWORD) accEv += foe.statStages[6];
			accEv = accEv > 6 ? 6 : accEv;
			accEv = accEv < -6 ? -6 : accEv;
			double accuracy = acc * asAccModifier(accEv);
			if ((field.equals(field.weather, Effect.SANDSTORM) && foe.ability == Ability.SAND_VEIL) ||
					(field.equals(field.weather, Effect.SNOW) && foe.ability == Ability.SNOW_CLOAK)) accuracy *= 0.8;
			if (!hit(accuracy) || foe.vStatuses.contains(Status.SEMI_INV)) {
				System.out.println("\n" + this.nickname + " used " + move + "!");
				System.out.println(this.nickname + "'s attack missed!");
				if (move == Move.HI_JUMP_KICK) {
					this.currentHP -= this.getStat(0) / 2;
					System.out.println(this.nickname + " kept going and crashed!");
					if (this.currentHP < 0) {
						this.faint(true, player, foe);
						foe.awardxp((int) Math.ceil(this.level * trainer), player);
					}
				}
				endMove();
				this.vStatuses.remove(Status.LOCKED);
				return; // Check for miss
			}
		}
		
		if (move == Move.HIDDEN_POWER) moveType = determineHPType();
		if (move == Move.WEATHER_BALL) moveType = determineWBType(field);
		if (move == Move.TERRAIN_PULSE) moveType = determineTPType(field);
		
		int numHits = move.getNumHits(team);
		System.out.println("\n" + this.nickname + " used " + move + "!");
		for (int hit = 1; hit <= numHits; hit++) {
			if (hit > 1) bp = move.basePower;
			if (foe.isFainted() || this.isFainted()) {
				System.out.println("Hit " + (hit - 1) + " times!");
				break;
			}
			
			if (moveType == PType.FIRE && foe.ability == Ability.FLASH_FIRE) {
				System.out.println("[" + foe.nickname + "'s Flash Fire]: " + foe.nickname + "'s Fire-Type move's are boosted!");
				foe.vStatuses.add(Status.FLASH_FIRE);
				endMove();
				return;
			}
			
			if ((moveType == PType.WATER && foe.ability == Ability.WATER_ABSORB) || (moveType == PType.ELECTRIC && foe.ability == Ability.VOLT_ABSORB) ||
					(moveType == PType.BUG && foe.ability == Ability.INSECT_FEEDER)) {
				if (foe.currentHP == foe.getStat(0)) {
					System.out.println("[" + foe.nickname + "'s " + foe.ability.toString() + "]: " + "It doesn't effect " + foe.nickname + "...");
					endMove();
					return; // Check for immunity
				} else {
					System.out.println(foe.nickname + " restored HP!");
					foe.currentHP += foe.getStat(0) / 4;
					foe.verifyHP();
					endMove();
					return;
				}
			}
			
			if ((moveType == PType.ELECTRIC && (foe.ability == Ability.MOTOR_DRIVE || foe.ability == Ability.LIGHTNING_ROD)) ||
					(moveType == PType.GRASS && foe.ability == Ability.SAP_SIPPER)) {
				System.out.print("[" + foe.nickname + "'s " + foe.ability.toString() + "]: ");
				if (foe.ability == Ability.MOTOR_DRIVE) stat(foe, 4, 1);
				if (foe.ability == Ability.LIGHTNING_ROD) stat(foe, 2, 1);
				if (foe.ability == Ability.SAP_SIPPER) stat(foe, 0, 1);
				endMove();
				return;
			}
			
			if (moveType == PType.GROUND && !foe.isGrounded(field, foe)) {
				if (foe.ability == Ability.LEVITATE) System.out.print("[" + foe.nickname + "'s Levitate]: ");
				System.out.println("It doesn't effect " + foe.nickname + "...");
				endMove();
				return; // Check for immunity
			}
			
			if (moveType != PType.GROUND && (move.cat != 2 || move == Move.THUNDER_WAVE)) {
				if (getImmune(foe, moveType) || (moveType == PType.GHOST && foe.ability == Ability.FRIENDLY_GHOST)) {
					if (foe.ability == Ability.FRIENDLY_GHOST) System.out.print("[" + foe.nickname + "'s Friendly Ghost]: ");
					System.out.println("It doesn't effect " + foe.nickname + "...");
					endMove();
					return; // Check for immunity 
				}
			}
			
			if (foe.magCount > 0) foe.magCount--;
			
			
			if (move == Move.DREAM_EATER && foe.status != Status.ASLEEP) {
				System.out.println("It doesn't effect " + foe.nickname + "...");
				endMove();
				return;
			}
			
			if (this.ability == Ability.PROTEAN && this.type1 == getType(this.id)[0] && this.type2 == getType(this.id)[1]) {
				if (this.type1 == moveType && this.type2 == null) {
					
				} else {
					this.type1 = moveType;
					this.type2 = null;
					System.out.println(this.nickname + "'s type was updated to " + this.type1.toString() + "!");
				}
			}
			
			if (move.cat == 2) {
				statusEffect(foe, move, player, field, team);
				this.impressive = false;
				return;
			}
			
			if (move.basePower < 0) {
				bp = determineBasePower(foe, move, first, field, team);
			}
			
			if (moveType == PType.FIRE && this.vStatuses.contains(Status.FLASH_FIRE)) bp *= 1.5;
			if (this.ability == Ability.NORMALIZE) {
				moveType = PType.NORMAL;
				bp *= 1.2;
			}
			
			if (this.ability == Ability.SHEER_FORCE && move.cat != 2 && secChance > 0) {
				secChance = 0;
				bp *= 1.3;
			}
			
			if (this.ability == Ability.TECHNICIAN && bp <= 60) {
				bp *= 1.5;
			}
			
			if (this.ability == Ability.TOUGH_CLAWS && move.contact) {
				bp *= 1.3;
			}
			
			if (this.ability == Ability.IRON_FIST && (move == Move.BULLET_PUNCH || move == Move.COMET_PUNCH || move == Move.DRAIN_PUNCH
					|| move == Move.FIRE_PUNCH || move == Move.ICE_PUNCH || move == Move.MACH_PUNCH || move == Move.POWER_UP_PUNCH || move == Move.SHADOW_PUNCH
					|| move == Move.SKY_UPPERCUT || move == Move.THUNDER_PUNCH || move == Move.METEOR_MASH || move == Move.PLASMA_FISTS || move == Move.SUCKER_PUNCH
					|| move == Move.DYNAMIC_PUNCH)) {
				bp *= 1.3;
			}
			
			if (this.ability == Ability.STRONG_JAW && (move == Move.BITE || move == Move.CRUNCH || move == Move.FIRE_FANG || move == Move.HYPER_FANG
					|| move == Move.ICE_FANG || move == Move.JAW_LOCK || move == Move.POISON_FANG || move == Move.PSYCHIC_FANGS || move == Move.THUNDER_FANG)) {
				bp *= 1.5;
			}
			
			if (moveType == PType.GRASS && this.ability == Ability.OVERGROW && this.currentHP <= this.getStat(0) / 3) {
				bp *= 1.5;
			} else if (moveType == PType.FIRE && this.ability == Ability.BLAZE && this.currentHP <= this.getStat(0) / 3) {
				bp *= 1.5;
			} else if (moveType == PType.WATER && this.ability == Ability.TORRENT && this.currentHP <= this.getStat(0) / 3) {
				bp *= 1.5;
			} else if (moveType == PType.BUG && this.ability == Ability.SWARM && this.currentHP <= this.getStat(0) / 3) {
				bp *= 1.5;
			}
			
			if (field.equals(field.weather, Effect.SUN)) {
				if (move.mtype == PType.WATER) bp *= 0.5;
				if (move.mtype == PType.FIRE) bp *= 1.5;
			}
			
			if (field.equals(field.weather, Effect.RAIN)) {
				if (move.mtype == PType.WATER) bp *= 1.5;
				if (move.mtype == PType.FIRE) bp *= 0.5;
				if (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) bp *= 0.5;
			}
			
			if ((field.contains(field.fieldEffects, Effect.MUD_SPORT) && move.mtype == PType.ELECTRIC) || (field.contains(field.fieldEffects, Effect.WATER_SPORT) && move.mtype == PType.FIRE)) bp *= 0.33;
			
			if (field.equals(field.weather, Effect.SANDSTORM) && this.ability == Ability.SAND_FORCE && (move.mtype == PType.ROCK || move.mtype == PType.GROUND || move.mtype == PType.STEEL)) bp *= 1.3;
			
			if ((field.equals(field.weather, Effect.RAIN) || field.equals(field.weather, Effect.SNOW) || field.equals(field.weather, Effect.SANDSTORM)) && (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE)) bp *= 0.5;
			
			if (foe.ability == Ability.SHIELD_DUST) secChance = 0;
			
			// Use either physical or special attack/defense
			if (move.isPhysical()) {
				attackStat = this.getStat(1);
				defenseStat = foe.getStat(2);
				if (foe.ability != Ability.UNAWARE) attackStat *= this.asModifier(0);
				if (move != Move.DARKEST_LARIAT && move != Move.SACRED_SWORD && this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(1);
				if (move == Move.BODY_PRESS) attackStat = this.getStat(2) * this.asModifier(1);
				if (move == Move.FOUL_PLAY) attackStat = foe.getStat(1) * foe.asModifier(0);
				if (this.status == Status.BURNED && this.ability != Ability.GUTS && move != Move.FACADE) attackStat /= 2;
				if (this.ability == Ability.GUTS && this.status != Status.HEALTHY) attackStat *= 1.5;
				if (this.ability == Ability.HUGE_POWER) attackStat *= 2;
				if (field.equals(field.weather, Effect.SNOW) && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) defenseStat *= 1.5;
			} else {
				attackStat = this.getStat(3);
				defenseStat = foe.getStat(4);
				if (foe.ability != Ability.UNAWARE) attackStat *= this.asModifier(2);
				if (this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(3);
				if (move == Move.PSYSHOCK) defenseStat = foe.getStat(2) * foe.asModifier(1);
				if (this.status == Status.FROSTBITE) attackStat /= 2;
				if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN)) attackStat *= 1.5;
				if (field.equals(field.weather, Effect.SANDSTORM) && (foe.type1 == PType.ROCK || foe.type2 == PType.ROCK)) defenseStat *= 1.5;
			}
			
			// Crit Check
			if (this.vStatuses.contains(Status.FOCUS_ENERGY)) critChance += 2;
			if (this.ability == Ability.SUPER_LUCK) critChance++;
			if (critCheck(critChance)) {
				System.out.println("A critical hit!");
				if (move.isPhysical() && attackStat < this.getStat(1)) {
					attackStat = this.getStat(1);
					if (this.status == Status.BURNED) attackStat /= 2;
				}
				if (!move.isPhysical() && attackStat < this.getStat(3)) {
					attackStat = this.getStat(3);
					if (this.status == Status.FROSTBITE) attackStat /= 2;
				}
				if (move.isPhysical() && defenseStat > foe.getStat(2)) defenseStat = foe.getStat(2);
				if (!move.isPhysical() && defenseStat > foe.getStat(4)) defenseStat = foe.getStat(4);
				damage = calc(attackStat, defenseStat, bp, this.level);
				damage *= 1.5;
				if (this.ability == Ability.SNIPER) damage *= 1.5;
				if (foe.ability == Ability.ANGER_POINT) {
					System.out.print("[" + foe.nickname + "'s Anger Point]: ");
					stat(foe, 0, 12); }
			} else {
				damage = calc(attackStat, defenseStat, bp, this.level);
			}
			
			// Stab
			if (moveType == this.type1 || moveType == this.type2 || this.ability == Ability.TYPE_MASTER) {
				if (ability == Ability.ADAPTABILITY) {
					damage *= 2;
				} else {
					damage *= 1.5;
				}
			}
			
			if (moveType == PType.STEEL && this.ability == Ability.STEELWORKER) damage *= 1.5;
			
			// Charged
			if (moveType == PType.ELECTRIC && this.vStatuses.contains(Status.CHARGED)) {
				damage *= 2;
				this.vStatuses.remove(Status.CHARGED);
			}
			
			if ((foe.ability == Ability.ICY_SCALES && !move.isPhysical()) || (foe.ability == Ability.MULTISCALE && foe.currentHP == foe.getStat(0))) damage /= 2;
			
			// Screens
			if (this.trainerOwned) {
				if (move.isPhysical() && field.contains(field.foeSide, Effect.REFLECT)) damage /= 2;
				if (!move.isPhysical() && field.contains(field.foeSide, Effect.LIGHT_SCREEN)) damage /= 2;
			} else {
				if (move.isPhysical() && field.contains(field.playerSide, Effect.REFLECT)) damage /= 2;
				if (!move.isPhysical() && field.contains(field.playerSide, Effect.LIGHT_SCREEN)) damage /= 2;
			}
			
			double multiplier = 1;
			// Check type effectiveness
			PType[] resist = getResistances(moveType);
			if (move == Move.FREEZE_DRY) {
				ArrayList<PType> types = new ArrayList<>();
				for (PType type : resist) {
					types.add(type);
				}
				types.remove(PType.WATER);
				resist = types.toArray(new PType[0]);
			}
			
			for (PType type : resist) {
				if (foe.type1 == type) multiplier /= 2;
				if (foe.type2 == type) multiplier /= 2;
			}
			
			if (foe.ability == Ability.FALSE_ILLUMINATION) {
				PType[] lightResist = new PType[]{PType.GHOST, PType.GALACTIC, PType.LIGHT};
				for (PType type : lightResist) {
					if (moveType == type) multiplier /= 2;
				}
			}
			
			// Check type effectiveness
			PType[] weak = getWeaknesses(moveType);
			if (move == Move.FREEZE_DRY) {
				PType[] temp = new PType[weak.length + 1];
				for (int i = 0; i < weak.length; i++) {
					temp[i] = weak[i];
				}
				temp[weak.length] = PType.WATER;
				weak = temp;
			}
			for (PType type : weak) {
				if (foe.type1 == type) multiplier *= 2;
				if (foe.type2 == type) multiplier *= 2;
			}
			
			if (foe.ability == Ability.WONDER_GUARD && multiplier <= 1) {
				System.out.println("\n" + this.nickname + " used " + move + "!");
				System.out.println("It doesn't effect " + foe.nickname + "...");
				endMove();
				return; // Check for immunity
			}
			
			if (foe.ability == Ability.FLUFFY && move.mtype == PType.FIRE) multiplier *= 2;
			
			damage *= multiplier;
			
			if (foe.ability == Ability.UNERODIBLE && (moveType == PType.GRASS || moveType == PType.WATER || moveType == PType.GROUND)) damage /= 4;
			if (foe.ability == Ability.THICK_FAT && (moveType == PType.FIRE || moveType == PType.ICE)) damage /= 2;
			if (foe.ability == Ability.FLUFFY && move.contact) damage /= 2;
			
			if (foe.ability == Ability.PSYCHIC_AURA && move.cat == 1) damage *= 0.8;
			if (foe.ability == Ability.GLACIER_AURA && move.cat == 0) damage *= 0.8;
			if (foe.ability == Ability.GALACTIC_AURA && (move.mtype == PType.PSYCHIC || move.mtype == PType.ICE)) damage /= 2;
			
			if (multiplier > 1) {
				System.out.println("It's super effective!");
				if (foe.ability == Ability.SOLID_ROCK || foe.ability == Ability.FILTER) damage /= 2;
			}
			if (multiplier < 1) System.out.println("It's not very effective...");
			
			if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS || move == Move.PSYWAVE) damage = this.level;
			if (move == Move.ENDEAVOR) {
				if (foe.currentHP > this.currentHP) {
					damage = foe.currentHP - this.currentHP;
				} else { fail(); } }
			if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
			if (move == Move.DRAGON_RAGE) damage = 40;
			if (move == Move.HORN_DRILL || move == Move.SHEER_COLD || move == Move.GUILLOTINE || move == Move.FISSURE) {
				if ((move == Move.SHEER_COLD && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) || foe.ability == Ability.STURDY) {
					System.out.println("It doesn't effect " + foe.nickname + "...");
					endMove();
					return;
				}
				damage = foe.currentHP;
				System.out.println("It's a one-hit KO!");
			}
			
			if (move == Move.ABSORB || move == Move.DREAM_EATER || move == Move.GIGA_DRAIN || move == Move.MEGA_DRAIN || move == Move.LEECH_LIFE || move == Move.DRAIN_PUNCH || move == Move.DRAINING_KISS || move == Move.HORN_LEECH || move == Move.PARABOLIC_CHARGE) {
				int healAmount;
				if (damage >= foe.currentHP) {
					healAmount = move == Move.DRAINING_KISS ? Math.max((int) Math.ceil(foe.currentHP / 1.333333333333), 1) : Math.max((int) Math.ceil(foe.currentHP / 2.0), 1);
				} else {
					healAmount = move == Move.DRAINING_KISS ? Math.max((int) Math.ceil(damage / 1.333333333333), 1) : Math.max((int) Math.ceil(damage / 2.0), 1);
				}
				
				this.currentHP += healAmount;
				if (this.currentHP > this.getStat(0)) this.currentHP = this.getStat(0);
				System.out.println(this.nickname + " sucked HP from " + foe.nickname + "!");
			}
			
			damage = Math.max(damage, 1);
			
			if (this.ability == Ability.SERENE_GRACE) secChance *= 2;
			
			int recoil = 0;
			if ((move == Move.BRAVE_BIRD || move == Move.FLARE_BLITZ || move == Move.HEAD_SMASH || move == Move.TAKE_DOWN || move == Move.VOLT_TACKLE ||
					move == Move.ROCK_WRECKER || move == Move.GENESIS_SUPERNOVA || move == Move.LIGHT_OF_RUIN || move == Move.SUBMISSION || move == Move.WAVE_CRASH ||
					move == Move.STEEL_BEAM) && (ability != Ability.ROCK_HEAD && ability != Ability.MAGIC_GUARD)) {
				recoil = Math.max(Math.floorDiv(damage, 3), 1);
				if (damage >= foe.currentHP) recoil = Math.max(Math.floorDiv(foe.currentHP, 3), 1);
				if (move == Move.STEEL_BEAM) recoil = this.getStat(0) / 2;
			}
			
			boolean fullHP = foe.currentHP == foe.getStat(0);
			
			// Damage foe
			//int dividend = Math.min(damage, foe.currentHP);
			foe.currentHP -= damage;
			double percent = damage * 100.0 / foe.getStat(0); // change damage to dividend
			String formattedPercent = String.format("%.1f", percent);
			System.out.println("(" + foe.nickname + " lost " + formattedPercent + "% of its HP.)");
			if (foe.currentHP <= 0 && (move == Move.FALSE_SWIPE || foe.vStatuses.contains(Status.ENDURE) || (fullHP && foe.ability == Ability.STURDY))) {
				foe.currentHP = 1;
				if (foe.ability == Ability.STURDY) System.out.print("[" + foe.name + "'s Sturdy]: ");
				if (move != Move.FALSE_SWIPE) System.out.println(foe.name + " endured the hit!");
			}
			if (foe.currentHP <= 0) { // Check for kill
				foe.faint(true, player, this);
				if (move == Move.FELL_STINGER) stat(this, 0, 3);
				this.awardxp((int) Math.ceil(foe.level * foe.trainer), player);
				if (this.vStatuses.contains(Status.BONDED)) {
					System.out.println(foe.nickname + " took its attacker down with it!");
					this.faint(true, player, foe);
				}
				if (this.ability == Ability.MOXIE) {System.out.print("[" + this.nickname + "'s Moxie]: "); stat(this, 0, 1); }
			}
			
			if (recoil != 0) {
				System.out.println(this.nickname + " was damaged by recoil!");
				this.currentHP -= recoil;
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, player, foe);
					foe.awardxp((int) Math.ceil(this.level * trainer), player);
				}
			}
			
			if (move.contact && checkSecondary(30)) {
				if (foe.ability == Ability.FLAME_BODY) {
					System.out.print("[" + foe.nickname + "'s Flame Body]: ");
					burn(false, this);
				}
				if (foe.ability == Ability.STATIC) {
					System.out.print("[" + foe.nickname + "'s Static]: ");
					paralyze(false, this);
				}
				if (foe.ability == Ability.POISON_POINT) {
					System.out.print("[" + foe.nickname + "'s Poison Point]: ");
					poison(false, this);
				}
			}
			
			if (move.contact && (foe.ability == Ability.ROUGH_SKIN || foe.ability == Ability.IRON_BARBS)) {
				this.currentHP -= Math.max(this.getStat(0) / 8, 1);
				System.out.println(this.nickname + " was hurt!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, player, foe);
					if (move == Move.FELL_STINGER) stat(this, 0, 3);
					foe.awardxp((int) Math.ceil(this.level * this.trainer), player);
				}
			}
			
			if ((moveType == PType.BUG || moveType == PType.GHOST || moveType == PType.DARK) && foe.ability == Ability.RATTLED && !foe.isFainted()) {
				System.out.print("[" + foe.nickname + "'s " + foe.ability.toString() + "]: ");
				stat(foe, 4, 1);
			}
			
			if (moveType == PType.DARK && foe.ability == Ability.JUSTIFIED && !foe.isFainted()) {
				System.out.print("[" + foe.nickname + "'s " + foe.ability.toString() + "]: ");
				stat(foe, 0, 1);
			}
			
			if (hit == numHits && hit > 1) System.out.println("Hit " + hit + " times!");
		}
		
		if (!foe.isFainted() && checkSecondary(secChance)) {
			secondaryEffect(foe, move, field, first);
		}
		
		if (move == Move.VOLT_SWITCH || move == Move.FLIP_TURN || move == Move.U_TURN) {
			this.vStatuses.add(Status.SWITCHING);
		}
		
		
		if (move == Move.SELF_DESTRUCT || move == Move.EXPLOSION || move == Move.SUPERNOVA_EXPLOSION) {
			this.faint(true, player, foe);
			foe.awardxp((int) Math.ceil(this.level * trainer), player);
		}
		if (move == Move.HYPER_BEAM || move == Move.BLAST_BURN || move == Move.FRENZY_PLANT || move == Move.GIGA_IMPACT || move == Move.HYDRO_CANNON || move == Move.MAGIC_CRASH) this.vStatuses.add(Status.RECHARGE);
		if (move == Move.MAGIC_BLAST) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.values()) {
				if (cand.mtype == PType.ROCK || cand.mtype == PType.GRASS || cand.mtype == PType.GROUND) {
					moves.add(cand);
				}
			}
			Move[] validMoves = moves.toArray(new Move[moves.size()]);
			move(foe, validMoves[new Random().nextInt(validMoves.length)], player, field, team, first);
		}
		if (move == Move.ELEMENTAL_SPARKLE) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.values()) {
				if (cand.mtype == PType.FIRE || cand.mtype == PType.WATER || cand.mtype == PType.GRASS) {
					moves.add(cand);
				}
			}
			Move[] validMoves = moves.toArray(new Move[moves.size()]);
			move(foe, validMoves[new Random().nextInt(validMoves.length)], player, field, team, first);
		}
		endMove();
		return;
	}

	private void endMove() {
		impressive = false;
		success = true;
	}

	private boolean fail() {
		System.out.println("But it failed!");
		success = false;
		return true;
	}

	public void awardxp(int amt, Player player) {
	    if (this.fainted) return;
	    if (!this.trainerOwned) return;

	    ArrayList<Pokemon> teamCopy = new ArrayList<>(Arrays.asList(player.getTeam()));
	    int numBattled = player.getBattled();
	    if (numBattled == 0) return;
	    int expPerPokemon = amt / numBattled;
	    int remainingExp = amt % numBattled;

	    // Award experience points to each battled Pokemon
	    for (Pokemon p : teamCopy) {
	        if (p != null && p.battled) {
	            int expAwarded = expPerPokemon;
	            if (remainingExp > 0) {
	                expAwarded++;
	                remainingExp--;
	            }
	            if (p.level < 100) {
	                p.exp += expAwarded;
	                System.out.println(p.nickname + " gained " + expAwarded + " experience points!");
	            }
	            while (p.exp >= p.expMax) {
	                // Pokemon has leveled up, check for evolution
	                Pokemon evolved = p.levelUp(player);
	                if (evolved != null) {
	                    // Update the player's team with the evolved Pokemon
	                    int index = Arrays.asList(player.getTeam()).indexOf(p);
	                    player.team[index] = evolved;
	                    if (index == 0) player.current = evolved;
	                    evolved.checkMove();
	                    p = evolved;
	                }
	            }
	        }
	    }
	}



	public void awardHappiness(int i) {
		this.happiness += i;
		this.happiness = this.happiness > 255 ? 255 : this.happiness;
	}

	private void secondaryEffect(Pokemon foe, Move move, Field field, boolean first) {
		if (move == Move.ACID) {
			stat(foe, 3, -1);
		} else if (move == Move.ACID_SPRAY) {
			stat(foe, 3, -2);
		} else if (move == Move.ACROBATICS && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ANCIENT_POWER) {
			for (int i = 0; i < 5; ++i) {
				stat(this, i, 1);
			}
		} else if (move == Move.AIR_SLASH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ASTONISH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.AURORA_BEAM) {
			stat(foe, 0, -1);
		} else if (move == Move.BEAT_UP) {
			if (foe.status == Status.HEALTHY) {
				foe.status = Status.FROSTBITE;
				System.out.println(foe.nickname + " is bleeding!");
			}
		} else if (move == Move.BEEFY_BASH) {
			foe.paralyze(false, this);
//		} else if (move == Move.BIG_BULLET) {
//			foe.paralyze(false, this);
		} else if (move == Move.BIND) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				System.out.println(foe.nickname + " was wrapped by " + this.nickname + "!");
			}
		} else if (move == Move.BITE && first) {
			foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.BLACK_HOLE) {
//			stat(foe, 5, -1);
//		} else if (move == Move.BLAST_FLAME) {
//			foe.burn(false, this);
		} else if (move == Move.BLAZE_KICK) {
			foe.burn(false, this);
//		} else if (move == Move.BLAZING_SWORD) {
//			foe.burn(false, this);
		} else if (move == Move.BLIZZARD) {
			foe.freeze(false, field);
		} else if (move == Move.BLUE_FLARE) {
			foe.burn(false, this);
		} else if (move == Move.BODY_SLAM) {
			foe.paralyze(false, this);
		} else if (move == Move.BOLT_STRIKE) {
			foe.paralyze(false, this);
//		} else if (move == Move.BOULDER_CRUSH && first) {
//			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.BOUNCE) {
			foe.paralyze(false, this);
		} else if (move == Move.BUBBLEBEAM) {
			stat(foe, 4, -1);
		} else if (move == Move.BUG_BUZZ) {
			stat(foe, 3, -1);
		} else if (move == Move.BURN_UP) {
			if (this.type1 == PType.FIRE) type1 = PType.UNKNOWN; 
			if (this.type2 == PType.FIRE) type2 = null;
		} else if (move == Move.BULLDOZE) {
			stat(foe, 4, -1);
		} else if (move == Move.CHARGE_BEAM) {
			stat(this, 2, 1);
		} else if (move == Move.CLOSE_COMBAT) {
			stat(this, 1, -1);
			stat(this, 3, -1);
		} else if (move == Move.CONFUSION) {
			foe.confuse(false);
		} else if (move == Move.CORE_ENFORCER) {
			stat(foe, 0, -1);
			stat(foe, 2, -1);
//		} else if (move == Move.CONSTRICT) {
//			stat(foe, 4, -1);
		} else if (move == Move.CROSS_POISON) {
			foe.poison(false, this);
		} else if (move == Move.CRUNCH) {
			stat(foe, 1, -1);
		} else if (move == Move.DARK_PULSE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.DESOLATE_VOID) {
			Random random = new Random();
			boolean isHeads = random.nextBoolean();
			if (isHeads) {
				foe.paralyze(false, this);
			} else {
				foe.sleep(false);
			}
		} else if (move == Move.DISCHARGE) {
			foe.paralyze(false, this);
		} else if (move == Move.DIAMOND_STORM) {
			stat(this, 1, 1);
//		} else if (move == Move.DOUBLE_SLICE) {
//			if (foe.status == Status.HEALTHY) {
//				foe.status = Status.BLEEDING;
//				System.out.println(foe.nickname + " is bleeding!");
//			}
		} else if (move == Move.DRACO_METEOR) {
			stat(this, 2, -2);
		} else if (move == Move.DRAGON_RUSH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.DRAGON_BREATH) {
			foe.paralyze(false, this);
		} else if (move == Move.DYNAMIC_PUNCH) {
			foe.confuse(false);
		} else if (move == Move.EARTH_POWER) {
			stat(foe, 3, -1);
		} else if (move == Move.EMBER) {
			foe.burn(false, this);
		} else if (move == Move.ENERGY_BALL) {
			stat(foe, 3, -1);
		} else if (move == Move.EXTRASENSORY && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.FAKE_OUT && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.UNSEEN_STRANGLE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.FIERY_DANCE) {
			stat(this, 2, 1);
		} else if (move == Move.FIRE_BLAST) {
			foe.burn(false, this);
		} else if (move == Move.FATAL_BIND) {
			foe.perishCount = (foe.perishCount == 0) ? 3 : foe.perishCount;
//		} else if (move == Move.FIRE_DASH) {
//			foe.burn(false, this);
		} else if (move == Move.FIRE_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.burn(false, this);
			} else if (randomNum == 1 && first) {
				foe.vStatuses.add(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.vStatuses.add(Status.FLINCHED);
				foe.burn(false, this);
			}
		} else if (move == Move.FIRE_PUNCH) {
			foe.burn(false, this);
		} else if (move == Move.FIRE_SPIN) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				if (foe.type1 != PType.FIRE && foe.type2 != PType.FIRE) {
					foe.vStatuses.add(Status.SPUN);
					foe.spunCount = (((int) (Math.random() * 4)) + 2);
					System.out.println(foe.nickname + " was trapped in a fiery vortex!");
				}
			}
		} else if (move == Move.WHIRLPOOL) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				if (foe.type1 != PType.WATER && foe.type2 != PType.WATER) {
					foe.vStatuses.add(Status.SPUN);
					foe.spunCount = (((int) (Math.random() * 4)) + 2);
					System.out.println(foe.nickname + " was trapped in a whirlpool vortex!");
				}
			}
		} else if (move == Move.WRAP) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				System.out.println(foe.nickname + " was wrapped by " + this.nickname + "!");
			}
//		} else if (move == Move.FIRE_TAIL) {
//			foe.burn(false, this);
		} else if (move == Move.FLAME_BURST) {
			foe.burn(false, this);
			this.confuse(false);
		} else if (move == Move.FLAME_CHARGE) {
			stat(this, 4, 1);
		} else if (move == Move.FLAME_WHEEL) {
			foe.burn(false, this);
		} else if (move == Move.FLAMETHROWER) {
			foe.burn(false, this);
		} else if (move == Move.FLARE_BLITZ) {
			foe.burn(false, this);
		} else if (move == Move.FLASH_CANNON) {
			stat(foe, 3, -1);
		} else if (move == Move.FLASH_RAY) {
			stat(foe, 5, -1);
		} else if (move == Move.FORCE_PALM) {
			foe.paralyze(false, this);
		} else if (move == Move.FREEZE_DRY) {
			foe.freeze(false, field);
		} else if (move == Move.FREEZING_GLARE) {
			foe.freeze(false, field);
		} else if (move == Move.GLACIATE) {
			stat(foe, 4, -1);
		} else if (move == Move.GLITTERING_SWORD) {
			stat(foe, 1, -1);
		} else if (move == Move.GLITTERING_TORNADO) {
			stat(foe, 5, -1);
		} else if (move == Move.GLITZY_GLOW) {
			stat(foe, 3, 1);
		} else if (move == Move.GUNK_SHOT) {
			foe.poison(false, this);
		} else if (move == Move.HAMMER_ARM) {
			stat(this, 4, -1);
		} else if (move == Move.HEADBUTT && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.HEAT_WAVE) {
			foe.burn(false, this);
		} else if (move == Move.HURRICANE) {
			foe.confuse(false);
		} else if (move == Move.HYPER_FANG && first) {
			foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.INJECT) {
//			foe.poison(false, this);
		} else if (move == Move.ICE_BEAM) {
			foe.freeze(false, field);
		} else if (move == Move.ICE_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.freeze(false, field);
			} else if (randomNum == 1 && first) {
				foe.vStatuses.add(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.vStatuses.add(Status.FLINCHED);
				foe.freeze(false, field);
			}
		} else if (move == Move.ICE_PUNCH) {
			foe.freeze(false, field);
		} else if (move == Move.ICICLE_CRASH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ICE_SPINNER) {
			if (field.terrain != null) {
				field.terrain = null;
				field.terrainTurns = 0;
				System.out.println("The terrain returned to normal!");
			}
		} else if (move == Move.ICY_WIND) {
			stat(foe, 4, -1);
		} else if (move == Move.INFERNO) {
			foe.burn(false, this);
		}  else if (move == Move.INFESTATION) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				System.out.println(foe.nickname + " was infested by " + this.nickname + "!");
			}
		} else if (move == Move.IRON_BLAST && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.IRON_HEAD && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.IRON_TAIL) {
			stat(foe, 1, -1);
		} else if (move == Move.JAW_LOCK) {
			foe.vStatuses.add(Status.LOCKED);
			System.out.println(foe.nickname + " was trapped!");
		} else if (move == Move.LAVA_PLUME) {
			foe.burn(false, this);
//		} else if (move == Move.LEAF_KOBE) {
//			foe.paralyze(false, this);
//		} else if (move == Move.LEAF_PULSE) {
//			stat(foe, 5, -1);
//			int randomNum = ((int) Math.round(Math.random()));
//			if (randomNum == 0) {
//				foe.sleep(false);
//			}
		} else if (move == Move.LEAF_STORM) {
			stat(this, 2, -2);
		} else if (move == Move.LEAF_TORNADO) {
			stat(foe, 5, -1);
		} else if (move == Move.LICK) {
			foe.paralyze(false, this);
		} else if (move == Move.LIGHT_BEAM) {
			stat(this, 2, 1);
//		} else if (move == Move.LIGHTNING_HEADBUTT) {
//			int randomNum = ((int) Math.random() * 3);
//			if (randomNum == 0) {
//				foe.paralyze(false, this);
//			} else if (randomNum == 1 && first) {
//				foe.vStatuses.add(Status.FLINCHED);
//			}
//			 else if (randomNum == 2) {
//				if (first) foe.vStatuses.add(Status.FLINCHED);
//				foe.paralyze(false, this);
//			}
		} else if (move == Move.LIQUIDATION) {
			stat(this, 1, -1);
		} else if (move == Move.LOW_SWEEP) {
			stat(foe, 4, -1);
		} else if (move == Move.LUSTER_PURGE) {
			stat(foe, 3, -1);
//			} else if (move == Move.MACHETE_STAB && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			System.out.println(foe.nickname + " is bleeding!");
		} else if (move == Move.MAGIC_CRASH) {
			int randomNum = ((int) Math.random() * 5);
			switch (randomNum) {
			case 0:
				foe.burn(false, this);
				break;
			case 1:
				foe.sleep(false);
				break;
			case 2:
				foe.paralyze(false, this);
				break;
			case 3:
				foe.poison(false, this);
				break;
			case 4:
				if (foe.status == Status.HEALTHY) {
					foe.status = Status.FROSTBITE;
					System.out.println(foe.nickname + " is Frostbitten!");
				}
				break;
			default:
				return;
			}
		} else if (move == Move.MAGIC_FANG && first) {
			double multiplier = 1;
			// Check type effectiveness
			PType[] resist = getResistances(move.mtype);
			for (PType type : resist) {
				if (foe.type1 == type) multiplier /= 2;
				if (foe.type2 == type) multiplier /= 2;
			}
			
			// Check type effectiveness
			PType[] weak = getWeaknesses(move.mtype);
			for (PType type : weak) {
				if (foe.type1 == type) multiplier *= 2;
				if (foe.type2 == type) multiplier *= 2;
			}
			if (multiplier > 1) foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.MEGA_KICK) {
//			foe.paralyze(false, this); 
//		} else if (move == Move.MEGA_PUNCH) {
//			foe.paralyze(false, this);
//		} else if (move == Move.MEGA_SWORD) {
//			foe.paralyze(false, this);
		} else if (move == Move.METAL_CLAW) {
			stat(this, 0, 1);
		} else if (move == Move.METEOR_ASSAULT) {
			stat(this, 1, -1);
			stat(this, 3, -1);
		} else if (move == Move.METEOR_MASH) {
			stat(this, 0, 1);
		} else if (move == Move.MIRROR_SHOT) {
			stat(this, 5, -1);
		} else if (move == Move.MIST_BALL) {
			stat(this, 2, -1);
		} else if (move == Move.MOLTEN_STEELSPIKE) {
			foe.burn(false, this);
		} else if (move == Move.MOLTEN_CONSUME) {
			foe.burn(false, this);
		} else if (move == Move.MOONBLAST) {
			stat(foe, 2, -1);
		} else if (move == Move.MUD_BOMB) {
			stat(foe, 5, -1);
		} else if (move == Move.MUD_SHOT) {
			stat(this, 4, -1);
		} else if (move == Move.MUD_SLAP) {
			stat(foe, 5, -1);
		} else if (move == Move.MUDDY_WATER) {
			stat(this, 5, -1);
		} else if (move == Move.MYSTICAL_FIRE) {
			stat(this, 2, -1);
		} else if (move == Move.NEEDLE_ARM && first) {
			foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.NEEDLE_SPRAY) {
//			int randomNum = ((int) Math.round(Math.random()));
//			if (randomNum == 0) {
//				foe.paralyze(false, this);
//			} else {
//				foe.poison(false, this);
//			}
		} else if (move == Move.NIGHT_DAZE) {
			stat(this, 5, -1);
		} else if (move == Move.OVERHEAT) {
			stat(this, 2, -2);
//		} else if (move == Move.POISON_BALL) {
//			foe.poison(false, this);
		} else if (move == Move.PHOTON_GEYSER) {
			stat(this, 2, -2);
		} else if (move == Move.PLAY_ROUGH) {
			stat(foe, 0, -1);
		} else if (move == Move.POISON_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.toxic(false, this);
			} else if (randomNum == 1 && first) {
				foe.vStatuses.add(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.vStatuses.add(Status.FLINCHED);
				foe.poison(false, this);
			}
		} else if (move == Move.POISON_JAB) {
			foe.poison(false, this);
//		} else if (move == Move.POISON_PUNCH) {
//			foe.poison(false, this);
		} else if (move == Move.POISON_STING) {
			foe.poison(false, this);
//		} else if (move == Move.POISONOUS_WATER) {
//			foe.poison(false, this);
		} else if (move == Move.POWER_UP_PUNCH) {
			stat(this, 0, 1);
		} else if (move == Move.PSYBEAM) {
			foe.confuse(false);
		} else if (move == Move.PSYCHIC) {
			stat(foe, 3, -1);
		} else if (move == Move.RAPID_SPIN) {
			stat(this, 4, 1);
			if (this.vStatuses.contains(Status.SPUN)) {
				this.vStatuses.remove(Status.SPUN);
				System.out.println(this.nickname + " was freed!");
				this.spunCount = 0;
			}
			ArrayList<FieldEffect> side = this.trainerOwned ? field.playerSide : field.foeSide;
			for (FieldEffect fe : field.getHazards(side)) {
				System.out.println(fe.toString() + " disappeared from " + this.nickname + "'s side!");
				side.remove(fe);
			}
		} else if (move == Move.RAZOR_SHELL) {
			stat(foe, 1, -1);
		} else if (move == Move.ROCK_CLIMB) {
			foe.confuse(false);
		} else if (move == Move.ROCK_SMASH) {
			stat(foe, 1, -1);
		} else if (move == Move.ROCK_TOMB) {
			stat(foe, 4, -1);
//		} else if (move == Move.ROCKET) {
//			foe.paralyze(false, this);
//		} else if (move == Move.ROOT_CRUSH) {
//			foe.paralyze(false, this);
		} else if (move == Move.SACRED_FIRE) {
			foe.burn(false, this);
		} else if (move == Move.SCALD) {
			foe.burn(false, this);
		} else if (move == Move.SCORCHING_SANDS) {
			foe.burn(false, this);
		} else if (move == Move.SHADOW_BALL) {
			stat(foe, 3, -1);
		} else if (move == Move.SILVER_WIND) {
			for (int i = 0; i < 5; ++i) {
				stat(this, i, 1);
			}
		} else if (move == Move.SMACK_DOWN && !foe.vStatuses.contains(Status.SMACK_DOWN)) {
			foe.vStatuses.add(Status.SMACK_DOWN);
			System.out.println(foe.nickname + " was grounded!");
		} else if (move == Move.SLUDGE_WAVE) {
			foe.poison(false, this);
		} else if (move == Move.NUZZLE) {
			foe.paralyze(false, this);
//		} else if (move == Move.SHURIKEN && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			System.out.println(foe.nickname + " is bleeding!");
		} else if (move == Move.SKY_ATTACK && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SLOW_FALL) {
			this.ability = Ability.LEVITATE;
			System.out.println(this.nickname + "'s ability was changed to Levitate!");
		} else if (move == Move.SLUDGE) {
			foe.poison(false, this);
		} else if (move == Move.SLUDGE_BOMB) {
			foe.poison(false, this);
		} else if (move == Move.SMOG) {
			foe.poison(false, this);
		} else if (move == Move.SNARL) {
			stat(foe, 2, -1);
		} else if (move == Move.SPACE_BEAM) {
			stat(foe, 3, -1);
		} else if (move == Move.SPARK) {
			foe.paralyze(false, this);
		} else if (move == Move.SPARKLING_ARIA) {
			if (status == Status.BURNED) {
				status = Status.HEALTHY;
				System.out.println(nickname + " was cured of its burn!");
			}
		} else if (move == Move.SPARKLY_SWIRL) {
			for (int i = 0; i < 5; ++i) {
				stat(foe, i, -1);
			}
		} else if (move == Move.SPECTRAL_THIEF) {
			for (int i = 0; i < 7; ++i) {
				if (foe.statStages[i] > 0) {
					stat(this, i, foe.statStages[i]);
					foe.statStages[i] = 0;
				}
			}
//		} else if (move == Move.SPIKE_JAB) {
//			foe.poison(false, this);
		} else if (move == Move.SPIRIT_BREAK) {
			stat(foe, 2, -1);
		} else if (move == Move.STEEL_WING) {
			stat(this, 1, 1);
//		} else if (move == Move.STING && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			System.out.println(foe.nickname + " is bleeding!");
		} else if (move == Move.STRUGGLE_BUG) {
			stat(foe, 2, -1);
		} else if (move == Move.STOMP && first) {
			foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.STRONG_ARM) {
//			int randomNum = ((int) Math.random() * 3);
//			if (randomNum == 0) {
//				foe.paralyze(false, this);
//			} else if (randomNum == 1 && first) {
//				foe.vStatuses.add(Status.FLINCHED);
//			}
//			 else if (randomNum == 2) {
//				if (first) foe.vStatuses.add(Status.FLINCHED);
//				foe.paralyze(false, this);
//			}
//		} else if (move == Move.SUPER_CHARGE && first) {
//			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SUPERPOWER) {
			stat(this, 0, -1);
			stat(this, 1, -1);
//		} else if (move == Move.SWEEP_KICK) {
//			stat(foe, 0, -1);
		} else if (move == Move.SWORD_SPIN) {
			stat(this, 0, 1);
//		} else if (move == Move.SWORD_STAB && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			System.out.println(foe.nickname + " is bleeding!");
		} else if (move == Move.THUNDER) {
			foe.paralyze(false, this);
		} else if (move == Move.THUNDERBOLT) {
			foe.paralyze(false, this);
		} else if (move == Move.THUNDER_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.paralyze(false, this);
			} else if (randomNum == 1 && first) {
				foe.vStatuses.add(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.vStatuses.add(Status.FLINCHED);
				foe.paralyze(false, this);
			}
//		} else if (move == Move.THUNDER_KICK) {
//			foe.paralyze(false, this);
		} else if (move == Move.THUNDER_PUNCH) {
			foe.paralyze(false, this);
		} else if (move == Move.THUNDERSHOCK) {
			foe.paralyze(false, this);
		} else if (move == Move.TRI_ATTACK) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.burn(false, this);
			} else if (randomNum == 1) {
				foe.paralyze(false, this);
			}
			 else if (randomNum == 2) {
				foe.freeze(false, field);
			}
		} else if (move == Move.TORNADO_SPIN) {
			stat(this, 4, 1);
			stat(this, 5, 1);
			if (this.vStatuses.contains(Status.SPUN)) {
				this.vStatuses.remove(Status.SPUN);
				System.out.println(this.nickname + " was freed!");
				this.spunCount = 0;
			}
		} else if (move == Move.TWINKLE_TACKLE) {
			stat(foe, 0, -1);
			stat(foe, 2, -1);
		} else if (move == Move.TWINEEDLE) {
			foe.poison(false, this);
		} else if (move == Move.SCALE_SHOT) {
			stat(this, 1, -1);
			stat(this, 4, 1);
		} else if (move == Move.TWISTER && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.VOLT_TACKLE) {
			foe.paralyze(false, this);
		} else if (move == Move.V_CREATE) {
			stat(this, 1, -1);
			stat(this, 3, -1);
			stat(this, 4, -1);
		} else if (move == Move.VINE_CROSS) {
			stat(foe, 4, -1);
		} else if (move == Move.WATER_PULSE) {
			foe.confuse(false);
		} else if (move == Move.WATER_CLAP) {
			foe.paralyze(false, this);
		} else if (move == Move.WATER_SMACK && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SUPERCHARGED_SPLASH) {
			stat(this, 2, 1);
		} else if (move == Move.WATERFALL && first) {
			foe.vStatuses.add(Status.FLINCHED);
//		} else if (move == Move.WOOD_FANG && first) {
//			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ZEN_HEADBUTT && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ROCK_SLIDE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		}  else if (move == Move.ZAP_CANNON) {
			foe.paralyze(false, this);
		} else if (move == Move.ZING_ZAP && first) {
			foe.vStatuses.add(Status.FLINCHED);
		}
	}

	private boolean checkSecondary(int secondary) {
		double chance = (int) (Math.random()*100 + 1);
		if (chance <= secondary) {
			return true;
		} else {
			return false;
		}
	}

	private void statusEffect(Pokemon foe, Move move, Player player, Field field, Pokemon[] team) {
		boolean fail = false;
		if (move == Move.ABDUCT) {
			if (this.lastMoveUsed != Move.ABDUCT) {
				foe.vStatuses.add(Status.POSESSED);
				System.out.println(this.nickname + " posessed " + foe.nickname + "!");
			} else { fail = fail(); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (move == Move.ACID_ARMOR) {
			stat(this, 1, 2);
		} else if (move == Move.AGILITY) {
			stat(this, 4, 2);
		} else if (move == Move.AMNESIA) {
			stat(this, 3, 2);
		} else if (move == Move.AROMATHERAPY) {
			System.out.println("A soothing aroma wafted through the air!");
			if (team == null) {
				if (this.status != Status.HEALTHY) {
					System.out.println(this.nickname + " was cured of its " + this.status.getName() + "!");
					this.status = Status.HEALTHY;
				}
			} else {
				for (Pokemon p : team) {
					if (p != null && p.status != Status.HEALTHY) {
						System.out.println(p.nickname + " was cured of its " + p.status.getName() + "!");
						p.status = Status.HEALTHY;
					}
				}
			}
		} else if (move == Move.AQUA_RING) {
			if (!(this.vStatuses.contains(Status.AQUA_RING))) {
			    this.vStatuses.add(Status.AQUA_RING);
			} else {
			    fail = fail();
			}
		} else if (move == Move.AURORA_VEIL) {
			if (field.equals(field.weather, Effect.SNOW)) {
				if (this.trainerOwned) {
					if (!(field.contains(field.playerSide, Effect.AURORA_VEIL))) {
						field.playerSide.add(field.new FieldEffect(Effect.AURORA_VEIL));
					} else {
						fail = fail();
					}
				} else {
					if (!(field.contains(field.foeSide, Effect.AURORA_VEIL))) {
						field.foeSide.add(field.new FieldEffect(Effect.AURORA_VEIL));
					} else {
						fail = fail();
					}
				}
			} else { fail = fail(); }
		} else if (move == Move.AUTOMOTIZE) {
			stat(this, 4, 2);
		} else if (move == Move.LOAD_FIREARMS) {
			if (!(this.vStatuses.contains(Status.AUTO))) {
				this.vStatuses.add(Status.AUTO);
				System.out.println(this.nickname + " upgraded its weapon!");
			} else {
				System.out.println("But it failed!");
			}
		} else if (move == Move.BABY_DOLL_EYES) {
			stat(foe, 0, -1);
		} else if (move == Move.BATON_PASS || move == Move.TELEPORT) {
			this.vStatuses.add(Status.SWITCHING);
		} else if (move == Move.BULK_UP) {
			stat(this, 0, 1);
			stat(this, 1, 1);
		} else if (move == Move.CALM_MIND) {
			stat(this, 2, 1);
			stat(this, 3, 1);
		} else if (move == Move.CAPTIVATE) {
			stat(foe, 2, -2);
		} else if (move == Move.CHARGE) {
			System.out.println(this.nickname + " became charged with power!");
			this.vStatuses.add(Status.CHARGED);
			stat(this, 3, 1);
		} else if (move == Move.CHARM) {
			stat(foe, 0, -2);
		} else if (move == Move.COIL) {
			stat(this, 0, 1);
			stat(this, 1, 1);
			stat(this, 5, 1);
		} else if (move == Move.CONFUSE_RAY) {
			foe.confuse(true);
		} else if (move == Move.COSMIC_POWER) {
			stat(this, 1, 1);
			stat(this, 3, 1);
		} else if (move == Move.COTTON_GUARD) {
			stat(this, 1, 3);
		} else if (move == Move.CURSE) {
			if (!foe.vStatuses.contains(Status.CURSED)) {
				foe.vStatuses.add(Status.CURSED);
				System.out.println(foe.nickname + " was afflicted with a curse!");
				this.currentHP -= (this.getStat(0) / 2);
				if (this.currentHP <= 0) {
					this.faint(true, player, foe);
					foe.awardxp((int) Math.ceil(this.level * trainer), player);
				}
			} else {
				fail = fail();
			}
		} else if (move == Move.DETECT || move == Move.PROTECT || move == Move.MOLTEN_LAIR || move == Move.OBSTRUCT || move == Move.SPIKY_SHIELD) {
			if ((lastMoveUsed == Move.DETECT || lastMoveUsed == Move.PROTECT || lastMoveUsed == Move.MOLTEN_LAIR || lastMoveUsed == Move.OBSTRUCT ||
					lastMoveUsed == Move.SPIKY_SHIELD) && success) {
				fail = fail();
			} else {
				this.vStatuses.add(Status.PROTECT);
				this.lastMoveUsed = move;
			}
		} else if (move == Move.DEFENSE_CURL) {
			stat(this, 1, 1);
		} else if (move == Move.DEFOG) {
			stat(foe, 6, -1);
			ArrayList<FieldEffect> side = this.trainerOwned ? field.playerSide : field.foeSide;
			for (FieldEffect fe : field.getHazards(side)) {
				System.out.println(fe.toString() + " disappeared from " + this.nickname + "'s side!");
				side.remove(fe);
			}
			side = foe.trainerOwned ? field.playerSide : field.foeSide;
			for (FieldEffect fe : field.getHazards(side)) {
				System.out.println(fe.toString() + " disappeared from " + this.nickname + "'s side!");
				side.remove(fe);
			}
		} else if (move == Move.DESTINY_BOND) {
			if (this.lastMoveUsed != Move.DESTINY_BOND) {
				foe.vStatuses.add(Status.BONDED);
				System.out.println(this.nickname + " is ready to take its attacker down with it!");
			} else { fail = fail(); }
			this.impressive = false;
			this.lastMoveUsed = move;
			
//		} else if (move == Move.DISAPPEAR) {
//			stat(this, 6, 2);
		} else if (move == Move.DOUBLE_TEAM) {
			stat(this, 6, 1);
		} else if (move == Move.DRAGON_DANCE) {
			stat(this, 0, 1);
			stat(this, 4, 1);
		} else if (move == Move.ELECTRIC_TERRAIN) {
			field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
		} else if (move == Move.ENCORE) {
			if (!foe.vStatuses.contains(Status.ENCORED) && foe.lastMoveUsed != null) {
				foe.vStatuses.add(Status.ENCORED);
				foe.encoreCount = 3;
			} else {
				fail = fail();
			}
		} else if (move == Move.ENDURE) {
			if (!this.vStatuses.contains(Status.ENDURE)) {
				this.vStatuses.add(Status.ENDURE);
			} else {
				fail = fail();
			}
		} else if (move == Move.FLASH) {
			stat(foe, 5, -1);
			stat(this, 2, 1);
		} else if (move == Move.ENTRAINMENT) {
			foe.ability = this.ability;
			System.out.println(foe.nickname + "'s ability became " + foe.ability.toString() + "!");
			foe.swapIn(this, field, player);
		} else if (move == Move.FAKE_TEARS) {
			stat(foe, 3, -2);
		} else if (move == Move.FEATHER_DANCE) {
			stat(foe, 0, -2);
		} else if (move == Move.FLATTER) {
			stat(foe, 2, 2);
			foe.confuse(false);
		} else if (move == Move.FOCUS_ENERGY) {
			if (!this.vStatuses.contains(Status.FOCUS_ENERGY)) {
				this.vStatuses.add(Status.FOCUS_ENERGY);
				System.out.println(this.nickname + " is tightening its focus!");
			} else {
				fail = fail();
			}
		} else if (move == Move.FORESIGHT) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			System.out.println(this.nickname + " identified " + foe.nickname + "!");
			stat(this, 5, 1);
		} else if (move == Move.FORESTS_CURSE) {
			foe.type1 = PType.GRASS;
			foe.type2 = null;
			System.out.println(foe.nickname + "'s type was changed to Grass!");
		} else if (move == Move.GASTRO_ACID) {
			foe.ability = Ability.NULL;
			System.out.println(foe.nickname + "'s ability was supressed!");
		} else if (move == Move.GEOMANCY) {
			stat(this, 2, 2);
			stat(this, 3, 2);
			stat(this, 4, 2);
		} else if (move == Move.GLARE) {
			foe.paralyze(true, this);
		} else if (move == Move.GLITTER_DANCE) {
			stat(this, 2, 1);
			stat(this, 4, 1);
		} else if (move == Move.GRASS_WHISTLE) {
			foe.sleep(true);
		} else if (move == Move.GRASSY_TERRAIN) {
			field.setTerrain(field.new FieldEffect(Effect.GRASSY));
		} else if (move == Move.GRAVITY) {
			field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		} else if (move == Move.GROWL) {
			stat(foe, 0, -1);
		} else if (move == Move.GROWTH) {
			if (field.equals(field.weather, Effect.SUN)) {
				stat(this, 0, 2);
				stat(this, 2, 2);
			} else {
				stat(this, 0, 1);
				stat(this, 2, 1);
			}
		} else if (move == Move.SNOWSCAPE) {
			field.setWeather(field.new FieldEffect(Effect.SNOW));
		} else if (move == Move.HARDEN) {
			stat(this, 1, 1);
		} else if (move == Move.HAZE) {
			this.statStages = new int[7];
			foe.statStages = new int[7];
			System.out.println("All stat changes were eliminated!");
		} else if (move == Move.HEAL_PULSE) {
			if (foe.currentHP == foe.getStat(0)) {
				System.out.println(foe.nickname + "'s HP is full!");
			} else {
				foe.currentHP += (foe.getStat(0) / 2);
				if (foe.currentHP > foe.getStat(0)) foe.currentHP = foe.getStat(0);
				System.out.println(foe.nickname + " restored HP.");
			}
		} else if (move == Move.HONE_CLAWS) {
			stat(this, 0, 1);
			stat(this, 5, 1);
		} else if (move == Move.HOWL) {
			stat(this, 0, 1);
		} else if (move == Move.HYPNOSIS) {
			foe.sleep(true);
//		} else if (move == Move.IGNITE) {
//			foe.burn(true);
		} else if (move == Move.INGRAIN) {
			if (!(this.vStatuses.contains(Status.AQUA_RING))) {
			    this.vStatuses.add(Status.AQUA_RING);
			} else {
			    fail = fail();
			}
			this.vStatuses.add(Status.NO_SWITCH);
		} else if (move == Move.IRON_DEFENSE) {
			stat(this, 1, 2);
		} else if (move == Move.LIFE_DEW) {
			if (this.currentHP == this.getStat(0)) {
				System.out.println(this.nickname + "'s HP is full!");
			} else {
				this.currentHP += (this.getStat(0) / 4);
				if (this.currentHP > this.getStat(0)) this.currentHP = this.getStat(0);
				System.out.println(this.nickname + " restored HP.");
			}
		} else if (move == Move.LEECH_SEED) {
			if (foe.type1 == PType.GRASS || foe.type2 == PType.GRASS) {
				System.out.println("It doesn't effect " + foe.nickname + "...");
				return;
			}
			if (!foe.vStatuses.contains(Status.LEECHED)) {
				foe.vStatuses.add(Status.LEECHED);
				System.out.println(foe.nickname + " was seeded!");
			} else {
				fail = fail();
			}
		} else if (move == Move.LEER) {
			stat(foe, 1, -1);
		} else if (move == Move.LIGHT_SCREEN) {
			if (this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.LIGHT_SCREEN))) {
					field.playerSide.add(field.new FieldEffect(Effect.LIGHT_SCREEN));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.LIGHT_SCREEN))) {
					field.foeSide.add(field.new FieldEffect(Effect.LIGHT_SCREEN));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.LOCK_ON) {
			System.out.println(this.nickname + " took aim at " + foe.nickname + "!\n");
			stat(this, 5, 6);
		} else if (move == Move.LOVELY_KISS) {
			foe.sleep(true);
		} else if (move == Move.MAGIC_POWDER) {
			foe.type1 = PType.MAGIC;
			foe.type2 = null;
			System.out.println(foe.nickname + "'s type changed to MAGIC!");
		} else if (move == Move.MAGIC_REFLECT) {
			if (this.lastMoveUsed != Move.MAGIC_REFLECT) {
				this.vStatuses.add(Status.REFLECT);
			} else { fail = fail(); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (move == Move.MAGNET_RISE) {
			if (this.magCount == 0) {
				this.magCount = 5;
				System.out.println(this.nickname + " floated with electromagnetism!");
			} else {
				fail = fail();
			}
		} else if (move == Move.MEAN_LOOK) {
			foe.vStatuses.add(Status.TRAPPED);
			System.out.println(foe.nickname + " can no longer escape!");
		} else if (move == Move.MEMENTO) {
			stat(foe, 0, -2);
			stat(foe, 2, -2);
			this.currentHP = 0;
			this.faint(true, player, foe);
			foe.awardxp((int) Math.ceil(this.level * trainer), player);
		} else if (move == Move.MINIMIZE) {
			stat(this, 6, 2);
		} else if (move == Move.MORNING_SUN || move == Move.MOONLIGHT || move == Move.SYNTHESIS) {
			if (this.currentHP == this.getStat(0)) {
				System.out.println(this.nickname + "'s HP is full!");
			} else {
				if (field.equals(field.weather, Effect.SUN)) {
					this.currentHP += (this.getStat(0) / 1.5);
				} else if (field.equals(field.weather, Effect.RAIN) || field.equals(field.weather, Effect.SANDSTORM) || field.equals(field.weather, Effect.SNOW)) {
					this.currentHP += (this.getStat(0) / 4);
				} else {
					this.currentHP += (this.getStat(0) / 2);
				}
				if (this.currentHP > this.getStat(0)) this.currentHP = this.getStat(0);
				System.out.println(this.nickname + " restored HP.");
			}
		} else if (move == Move.MUD_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.MUD_SPORT));
			System.out.println(this.nickname + " electric's power was weakened!");
		} else if (move == Move.NASTY_PLOT) {
			stat(this, 2, 2);
		} else if (move == Move.NOBLE_ROAR) {
			stat(foe, 0, -1);
			stat(foe, 2, -1);
		} else if (move == Move.NO_RETREAT) {
			if (!(this.vStatuses.contains(Status.NO_SWITCH))) {
			    this.vStatuses.add(Status.NO_SWITCH);
			    stat(this, 0, 1);
			    stat(this, 1, 1);
			    stat(this, 2, 1);
			    stat(this, 3, 1);
			    stat(this, 4, 1);
			    System.out.println(this.nickname + " can no longer switch out!");
			} else {
			    fail = fail();
			}
		} else if (move == Move.NIGHTMARE) {
			if (foe.status == Status.ASLEEP && !foe.vStatuses.contains(Status.NIGHTMARE)) {
				foe.vStatuses.add(Status.NIGHTMARE);
				System.out.println(foe.nickname + " had a nightmare!");
			} else {
				fail = fail();
			}
		} else if (move == Move.ODOR_SLEUTH) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			System.out.println(this.nickname + " identified " + foe.nickname + "!");
			stat(foe, 6, -1);
		} else if (move == Move.PERISH_SONG) {
			this.perishCount = (this.perishCount == 0) ? 3 : this.perishCount;
			foe.perishCount = (foe.perishCount == 0) ? 3 : foe.perishCount;
		} else if (move == Move.PLAY_NICE) {
			stat(foe, 0, -1);
		} else if (move == Move.SPARKLING_WATER) {
			stat(this, 3, 1);
		} else if (move == Move.WATER_FLICK) {
			stat(foe, 0, -1);
		} else if (move == Move.PSYCHIC_TERRAIN) {
			field.setTerrain(field.new FieldEffect(Effect.PSYCHIC));
		} else if (move == Move.POISON_GAS) {
			foe.poison(true, this);
		} else if (move == Move.STUN_SPORE) {
			foe.paralyze(true, this);
		} else if (move == Move.QUIVER_DANCE) {
			stat(this, 2, 1);
			stat(this, 3, 1);
			stat(this, 4, 1);
		} else if (move == Move.SHELL_SMASH) {
			stat(this, 1, -1);
			stat(this, 3, -1);
			stat(this, 0, 2);
			stat(this, 2, 2);
			stat(this, 4, 2);
		} else if (move == Move.RAIN_DANCE) {
			field.setWeather(field.new FieldEffect(Effect.RAIN));
		} else if (move == Move.REBOOT) {
			if (this.status != Status.HEALTHY || !this.vStatuses.isEmpty()) System.out.println(this.nickname + " became healthy!");
			this.status = Status.HEALTHY;
			removeBad(this.vStatuses);
			stat(this, 4, 1);
		} else if (move == Move.RED_NOSE_BOOST) {
			stat(this, 1, 1);
			stat(this, 2, 2);
			stat(this, 3, 1);
		} else if (move == Move.REFLECT) {
			if (this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.REFLECT))) {
					field.playerSide.add(field.new FieldEffect(Effect.REFLECT));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.REFLECT))) {
					field.foeSide.add(field.new FieldEffect(Effect.REFLECT));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.REST) {
			if (this.currentHP == this.getStat(0)) {
				System.out.println(this.nickname + "'s HP is full!");
			} else if (this.status == Status.ASLEEP) {
				fail = fail();
				return;
			} else {
				this.currentHP = this.getStat(0);
				this.status = Status.HEALTHY;
				if (this.ability == Ability.INSOMNIA) {
					fail = fail();
					return;
				}
				this.sleep(false);
				this.sleepCounter = 2;
				if (this.ability == Ability.EARLY_BIRD) this.sleepCounter /= 2;
				this.vStatuses.remove(Status.CONFUSED);
				System.out.println(this.nickname + " slept and became healthy!");
			}
		} else if (move == Move.ROOST || move == Move.RECOVER || move == Move.SLACK_OFF) {
			if (this.currentHP == this.getStat(0)) {
				System.out.println(this.nickname + "'s HP is full!");
			} else {
				this.currentHP += (this.getStat(0) / 2);
				if (this.currentHP > this.getStat(0)) this.currentHP = this.getStat(0);
				System.out.println(this.nickname + " restored HP.");
			}
		} else if (move == Move.SAND_ATTACK) {
			stat(foe, 5, -1);
		} else if (move == Move.SAFEGUARD) {
			if (this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.SAFEGUARD))) {
					field.playerSide.add(field.new FieldEffect(Effect.SAFEGUARD));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.SAFEGUARD))) {
					field.foeSide.add(field.new FieldEffect(Effect.SAFEGUARD));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.SANDSTORM) {
			field.setWeather(field.new FieldEffect(Effect.SANDSTORM));
		} else if (move == Move.SCARY_FACE) {
			stat(foe, 4, -2);
		} else if (move == Move.SCREECH) {
			stat(foe, 1, -2);
		} else if (move == Move.SEA_DRAGON) {
			int oHP = this.getStat(0);
			id = 237;
			nickname = getName();
			
			setBaseStats();
			getStats();
			weight = setWeight();
			int nHP = this.getStat(0);
			this.currentHP += nHP - oHP;
			setType();
			setAbility(abilitySlot);

		} else if (move == Move.SLEEP_POWDER) {
			foe.sleep(true);
		} else if (move == Move.SHIFT_GEAR) {
			stat(this, 0, 1);
			stat(this, 4, 2);
		} else if (move == Move.SMOKESCREEN) {
			stat(foe, 5, -1);
//		} else if (move == Move.STARE) {
//			stat(foe, 0, 1);
//			if (!foe.vStatuses.contains(Status.CONFUSED)) {
//				foe.confuse();
//			}
		} else if (move == Move.SPARKLING_TERRAIN) {
			field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
		} else if (move == Move.SPIKES) {
			if (!this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.SPIKES))) {
					field.playerSide.add(field.new FieldEffect(Effect.SPIKES));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.SPIKES))) {
					field.foeSide.add(field.new FieldEffect(Effect.SPIKES));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.SPLASH) {
			System.out.println("But nothing happened!");
		} else if (move == Move.STEALTH_ROCK) {
			if (!this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.STEALTH_ROCKS))) {
					field.playerSide.add(field.new FieldEffect(Effect.STEALTH_ROCKS));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.STEALTH_ROCKS))) {
					field.foeSide.add(field.new FieldEffect(Effect.STEALTH_ROCKS));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.STICKY_WEB) {
			if (!this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.STICKY_WEBS))) {
					field.playerSide.add(field.new FieldEffect(Effect.STICKY_WEBS));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.STICKY_WEBS))) {
					field.foeSide.add(field.new FieldEffect(Effect.STICKY_WEBS));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.STOCKPILE) {
			stat(this, 1, 1);
			stat(this, 3, 1);
		} else if (move == Move.STRENGTH_SAP) {
			int amount = (int) (foe.getStat(1) * foe.asModifier(0));
			stat(foe, 0, -1);
			this.currentHP += amount;
			if (this.currentHP > this.getStat(0)) this.currentHP = this.getStat(0);
			System.out.println(this.nickname + " restored HP.");
		}  else if (move == Move.STRING_SHOT) {
			stat(foe, 4, -2);
		} else if (move == Move.SUNNY_DAY) {
			field.setWeather(field.new FieldEffect(Effect.SUN));
		} else if (move == Move.SUPERSONIC) {
			foe.confuse(true);
		} else if (move == Move.SWAGGER) {
			stat(foe, 0, 2);
			foe.confuse(false);
		} else if (move == Move.SWEET_KISS) {
			foe.confuse(true);
		} else if (move == Move.SWEET_SCENT) {
			stat(foe, 6, -2);
		} else if (move == Move.TAIL_GLOW) {
			stat(this, 2, 3);
		} else if (move == Move.TAILWIND) {
			if (this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.TAILWIND))) {
					field.playerSide.add(field.new FieldEffect(Effect.TAILWIND));
					System.out.println("A strong wind blew behind your team!");
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.TAILWIND))) {
					field.foeSide.add(field.new FieldEffect(Effect.TAILWIND));
					System.out.println("A strong wind blew behind the opposing team!");
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.TAIL_WHIP) {
			stat(foe, 1, -1);
		} else if (move == Move.TAKE_OVER) {
			if (this.lastMoveUsed != Move.TAKE_OVER) {
				foe.vStatuses.add(Status.POSESSED);
				System.out.println(this.nickname + " posessed " + foe.nickname + "!");
			} else { fail = fail(); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (move == Move.TEETER_DANCE) {
			foe.confuse(true);
		} else if (move == Move.THUNDER_WAVE) {
			foe.paralyze(true, this);
		} else if (move == Move.TOPSY_TURVY) {
			for (int i = 0; i < 7; i++) {
				foe.statStages[i] *= -1;
			}
			System.out.println(foe.nickname + "'s stat changes were flipped!");
		} else if (move == Move.TOXIC) {
			foe.toxic(true, this);
		} else if (move == Move.TOXIC_SPIKES) {
			if (!this.trainerOwned) {
				if (!(field.contains(field.playerSide, Effect.TOXIC_SPIKES))) {
					field.playerSide.add(field.new FieldEffect(Effect.TOXIC_SPIKES));
				} else {
					fail = fail();
				}
			} else {
				if (!(field.contains(field.foeSide, Effect.TOXIC_SPIKES))) {
					field.foeSide.add(field.new FieldEffect(Effect.TOXIC_SPIKES));
				} else {
					fail = fail();
				}
			}
		} else if (move == Move.TRICK_ROOM) {
			field.setEffect(field.new FieldEffect(Effect.TRICK_ROOM));
		} else if (move == Move.VENOM_DRENCH) {
			if (foe.status == Status.POISONED) {
				stat(foe, 0, -2);
				stat(foe, 2, -2);
			}
		} else if (move == Move.WATER_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.WATER_SPORT));
			System.out.println(this.nickname + " fire's power was weakened!");
		} else if (move == Move.WILL_O_WISP) {
			foe.burn(true, this);
		} else if (move == Move.WITHDRAW) {
			stat(this, 1, 1);
		} else if (move == Move.WORRY_SEED) {
			foe.ability = Ability.INSOMNIA;
			System.out.println(foe.nickname + "'s ability became Insomnia!");
		} if (move == Move.ROCK_POLISH) {
			stat(this, 4, 2);
		}
		success = !fail;
		return;
	}
	
	public void verifyHP() {
		if (currentHP > this.getStat(0)) currentHP = this.getStat(0);
	}

	private void stat(Pokemon p, int i, int amt) throws IllegalArgumentException {
		if (amt == 0) throw new IllegalArgumentException("Stat change amount cannot be 0");
		int a = amt;
		if (p.ability == Ability.SIMPLE) a *= 2;
		if (p.ability == Ability.CONTRARY) a *= -1;
		String type = "";
		if (i == 0) type = "Attack";
		if (i == 1) type = "Defense";
		if (i == 2) type = "Special Attack";
		if (i == 3) type = "Special Defense";
		if (i == 4) type = "Speed";
		if (i == 5) type = "Accuracy";
		if (i == 6) type = "Evasion";
		String amount = "";
		if (a == 1) amount = " rose";
		if (a == -1) amount = " fell";
		if (a == 2) amount = " Sharply rose";
		if (a == -2) amount = " Harshly fell";
		if (a > 2) amount = " Drastically rose";
		if (a < -2) amount = " Dramatically fell";
		
		if (this != p) {
			if (p.ability == Ability.MIRROR_ARMOR && a < 0) {
				System.out.print("[" + p.nickname + "'s Mirror Armor]: ");
				stat(this, i, amt);
				return;
			} else if (p.ability == Ability.DEFIANT && a < 0) {
				stat(p, 0, 2);
			} else if (p.ability == Ability.COMPETITIVE && a < 0) {
				stat(p, 2, 2);
			} else if (p.ability == Ability.CLEAR_BODY && a < 0) {
				System.out.println("[" + p.nickname + "'s Clear Body]: " + this.nickname + "'s " + type + " was not lowered!");
				return;
			} else if (p.ability == Ability.KEEN_EYE && a < 0 && i == 5) {
				System.out.println("[" + p.nickname + "'s Keen Eye]: " + this.nickname + "'s " + type + " was not lowered!");
				return;
			} else if (p.ability == Ability.HYPER_CUTTER && a < 0 && i == 0) {
				System.out.println("[" + p.nickname + "'s Hyper Cutter]: " + this.nickname + "'s " + type + " was not lowered!");
				return;
			}
		}
		p.statStages[i] += a;
		if (p.statStages[i] > 6 && a > 0) {
			p.statStages[i] = 6;
			if (a != 12) System.out.println(p.nickname + "'s " + type + " won't go any higher!");
		} else if (p.statStages[i] < -6 && a < 0){
			p.statStages[i] = -6;
			if (a != 12) System.out.println(p.nickname + "'s " + type + " won't go any lower!");
		} else {
			System.out.println(p.nickname + "'s " + type + amount + "!");
		}
		
		
	}

	public double asModifier(int index) {
		double modifier = 1;
		if (index <= 4) {
			switch(this.statStages[index]) {
			case -6:
				modifier = 2.0/8.0;
				break;
			case -5:
				modifier = 2.0/7.0;
				break;
			case -4:
				modifier = 2.0/6.0;
				break;
			case -3:
				modifier = 2.0/5.0;
				break;
			case -2:
				modifier = 2.0/4.0;
				break;
			case -1:
				modifier = 2.0/3.0;
				break;
			case 0:
				modifier = 2.0/2.0;
				break;
			case 1:
				modifier = 3.0/2.0;
				break;
			case 2:
				modifier = 4.0/2.0;
				break;
			case 3:
				modifier = 5.0/2.0;
				break;
			case 4:
				modifier = 6.0/2.0;
				break;
			case 5:
				modifier = 7.0/2.0;
				break;
			case 6:
				modifier = 8.0/2.0;
				break;
			default:
				modifier = 1;
				break;
			}
		}
		return modifier;
	}
	
	public double asAccModifier(int value) {
		double modifier = 1;
		switch(value) {
		case -6:
			modifier = 3.0/9.0;
			break;
		case -5:
			modifier = 3.0/8.0;
			break;
		case -4:
			modifier = 3.0/7.0;
			break;
		case -3:
			modifier = 3.0/6.0;
			break;
		case -2:
			modifier = 3.0/5.0;
			break;
		case -1:
			modifier = 3.0/4.0;
			break;
		case 0:
			modifier = 3.0/3.0;
			break;
		case 1:
			modifier = 4.0/3.0;
			break;
		case 2:
			modifier = 5.0/3.0;
			break;
		case 3:
			modifier = 6.0/3.0;
			break;
		case 4:
			modifier = 7.0/3.0;
			break;
		case 5:
			modifier = 8.0/3.0;
			break;
		case 6:
			modifier = 9.0/3.0;
			break;
		default:
			modifier = 1;
			break;
		}
		return modifier;
	}

	private boolean getImmune(Pokemon p, PType type) {
		switch(type) {
        case NORMAL: 
        	if (p.type1 == PType.GHOST) return true;
        	if (p.type2 == PType.GHOST) return true;
        	return false;
        case ROCK: 
            return false;
		case BUG:
			return false;
		case DARK:
			return false;
		case DRAGON:
			if (p.type1 == PType.MAGIC) return true;
        	if (p.type2 == PType.MAGIC) return true;
			return false;
		case ELECTRIC:
			if (p.type1 == PType.GROUND) return true;
        	if (p.type2 == PType.GROUND) return true;
            return false;
		case FIGHTING:
			if (p.type1 == PType.GHOST) return true;
        	if (p.type2 == PType.GHOST) return true;
            return false;
		case FIRE:
			if (p.type1 == PType.GALACTIC) return true;
        	if (p.type2 == PType.GALACTIC) return true;
			return false;
		case FLYING:
			return false;
		case GHOST:
			if (p.type1 == PType.NORMAL) return true;
        	if (p.type2 == PType.NORMAL) return true;
            return false;
		case GRASS:
			return false;
		case GROUND:
			if (p.type1 == PType.FLYING) return true;
        	if (p.type2 == PType.FLYING) return true;
			return false;
		case MAGIC:
        	return false;
		case POISON:
			if (p.type1 == PType.STEEL) return true;
        	if (p.type2 == PType.STEEL) return true;
			return false;
		case STEEL:
			return false;
		case WATER:
			return false;
		case LIGHT:
			if (p.type1 == PType.GRASS) return true;
        	if (p.type2 == PType.GRASS) return true;
			return false;
		case PSYCHIC:
			if (p.type1 == PType.DARK) return true;
        	if (p.type2 == PType.DARK) return true;
			return false;
		case ICE:
			return false;
		case GALACTIC:
			return false;
		default:
			return false;
		}
		
	}

	private boolean hit(double d) {
		double hitChance = (int) (Math.random()*100 + 1);
		int acc = (int) Math.round(d);
		if (hitChance <= acc) {
			return true;
		} else {
			return false;
		}
	}

	private boolean critCheck(int m) {
		  int critChance = (int)(Math.random()*100);
		  int baseCrit;
		  if (m == 1) {
		    baseCrit = 13;
		  } else if (m == 2) {
		    baseCrit = 25;
		  } else if (m >= 3) {
			  return true;
		  } else {
			  baseCrit = 5;
		  }
		  if (critChance <= baseCrit) {
		    return true;
		  } else {
		    return false;
		  }
		}
	
	public void heal() {
		this.fainted = false;
		this.clearVolatile();
		this.currentHP = this.getStat(0);
		this.status = Status.HEALTHY;
		System.out.println(this.nickname + " healed!");
	}
	
	public PType[] getResistances(PType type) {
	    ArrayList<PType> resistantTypes = new ArrayList<>();
	    switch(type) {
	        case NORMAL: 
	        	resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.STEEL);
	            break;
	        case ROCK: 
	            resistantTypes.add(PType.FIGHTING);
	            resistantTypes.add(PType.GROUND);
	            resistantTypes.add(PType.STEEL);
	            break;
			case BUG:
				resistantTypes.add(PType.FIRE);
	            resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.FLYING);
	            resistantTypes.add(PType.STEEL);
	            resistantTypes.add(PType.GALACTIC);
	            break;
			case DARK:
				resistantTypes.add(PType.FIGHTING);
	            resistantTypes.add(PType.DARK);
	            resistantTypes.add(PType.GALACTIC);
	            break;
			case DRAGON:
				resistantTypes.add(PType.STEEL);
	            break;
			case ELECTRIC:
				resistantTypes.add(PType.ELECTRIC);
	            resistantTypes.add(PType.GRASS);
	            resistantTypes.add(PType.DRAGON);
	            resistantTypes.add(PType.GHOST);
	            break;
			case FIGHTING:
				resistantTypes.add(PType.GALACTIC);
	            resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.FLYING);
	            resistantTypes.add(PType.MAGIC);
	            resistantTypes.add(PType.PSYCHIC);
	            break;
			case FIRE:
				resistantTypes.add(PType.FIRE);
	            resistantTypes.add(PType.WATER);
	            resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.DRAGON);
	            resistantTypes.add(PType.MAGIC);
	            break;
			case FLYING:
				resistantTypes.add(PType.ELECTRIC);
	            resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.STEEL);
	            resistantTypes.add(PType.ICE);
	            break;
			case GHOST:
				resistantTypes.add(PType.DARK);
				resistantTypes.add(PType.LIGHT);
	            break;
			case GRASS:
				resistantTypes.add(PType.FIRE);
	            resistantTypes.add(PType.GRASS);
	            resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.FLYING);
	            resistantTypes.add(PType.BUG);
	            resistantTypes.add(PType.DRAGON);
	            resistantTypes.add(PType.STEEL);
	            break;
			case GROUND:
				resistantTypes.add(PType.GRASS);
	            resistantTypes.add(PType.BUG);
	            resistantTypes.add(PType.GALACTIC);
				break;
			case MAGIC:
				resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.FIGHTING);
	            resistantTypes.add(PType.MAGIC);
				break;
			case POISON:
				resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.GROUND);
	            resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.GHOST);
				break;
			case STEEL:
				resistantTypes.add(PType.FIRE);
	            resistantTypes.add(PType.WATER);
	            resistantTypes.add(PType.ELECTRIC);
	            resistantTypes.add(PType.STEEL);
	            resistantTypes.add(PType.MAGIC);
				break;
			case WATER:
				resistantTypes.add(PType.WATER);
	            resistantTypes.add(PType.GRASS);
	            resistantTypes.add(PType.DRAGON);
				break;
			case LIGHT:
				resistantTypes.add(PType.FIRE);
				resistantTypes.add(PType.WATER); // ???
				resistantTypes.add(PType.STEEL);
				resistantTypes.add(PType.LIGHT);
				resistantTypes.add(PType.BUG);
				break;
			case PSYCHIC:
				resistantTypes.add(PType.PSYCHIC);
				resistantTypes.add(PType.MAGIC);
				break;
			case ICE:
				resistantTypes.add(PType.FIRE);
				resistantTypes.add(PType.WATER);
				resistantTypes.add(PType.ICE);
				resistantTypes.add(PType.STEEL);
				break;
			case GALACTIC:
				resistantTypes.add(PType.ICE);
				resistantTypes.add(PType.ROCK);
				resistantTypes.add(PType.STEEL);
				resistantTypes.add(PType.LIGHT);
				break;
			default:
				break;
	    }
	    PType[] toReturn = new PType[resistantTypes.size()];
	    return resistantTypes.toArray(toReturn);
	}
	
	public PType[] getWeaknesses(PType type) {
	    ArrayList<PType> weakTypes = new ArrayList<>();
	    switch(type) {
	        case NORMAL:
	        	weakTypes.add(PType.MAGIC);
	            break;
	        case ROCK: 
	            weakTypes.add(PType.FIRE);
	            weakTypes.add(PType.ICE);
	            weakTypes.add(PType.FLYING);
	            weakTypes.add(PType.BUG);
	            weakTypes.add(PType.GALACTIC);
	            break;
			case BUG:
				weakTypes.add(PType.GRASS);
	            weakTypes.add(PType.PSYCHIC);
	            weakTypes.add(PType.DARK);
	            weakTypes.add(PType.LIGHT);
	            break;
			case DARK:
				weakTypes.add(PType.MAGIC);
				weakTypes.add(PType.PSYCHIC);
	            weakTypes.add(PType.GHOST);
	            break;
			case DRAGON:
				weakTypes.add(PType.DRAGON);
	            break;
			case ELECTRIC:
				weakTypes.add(PType.WATER);
				weakTypes.add(PType.STEEL);
	            weakTypes.add(PType.FLYING);
	            break;
			case FIGHTING:
				weakTypes.add(PType.NORMAL);
	            weakTypes.add(PType.ROCK);
	            weakTypes.add(PType.ICE);
	            weakTypes.add(PType.DARK);
	            weakTypes.add(PType.STEEL);
	            break;
			case FIRE:
				weakTypes.add(PType.GRASS);
				weakTypes.add(PType.ICE);
	            weakTypes.add(PType.BUG);
	            weakTypes.add(PType.STEEL);
	            break;
			case FLYING:
				weakTypes.add(PType.GRASS);
				weakTypes.add(PType.FIGHTING);
	            weakTypes.add(PType.BUG);
	            weakTypes.add(PType.GALACTIC);
	            break;
			case GHOST:
				weakTypes.add(PType.ELECTRIC);
				weakTypes.add(PType.GHOST);
				weakTypes.add(PType.PSYCHIC);
	            break;
			case GRASS:
				weakTypes.add(PType.WATER);
	            weakTypes.add(PType.ROCK);
	            weakTypes.add(PType.GROUND);
	            weakTypes.add(PType.LIGHT);
	            break;
			case GROUND:
				weakTypes.add(PType.ELECTRIC);
	            weakTypes.add(PType.FIRE);
	            weakTypes.add(PType.POISON);
	            weakTypes.add(PType.ROCK);
	            weakTypes.add(PType.STEEL);
				break;
			case MAGIC:
				weakTypes.add(PType.NORMAL);
	            weakTypes.add(PType.STEEL);
	            weakTypes.add(PType.DRAGON);
				break;
			case POISON:
				weakTypes.add(PType.GRASS);
				weakTypes.add(PType.WATER);
				weakTypes.add(PType.MAGIC);
				break;
			case STEEL:
				weakTypes.add(PType.ICE);
				weakTypes.add(PType.ROCK);
	            weakTypes.add(PType.LIGHT);
				break;
			case WATER:
				weakTypes.add(PType.FIRE);
	            weakTypes.add(PType.ROCK);
	            weakTypes.add(PType.GROUND);
				break;
			case LIGHT:
				weakTypes.add(PType.ICE);
				weakTypes.add(PType.GHOST);
				weakTypes.add(PType.DARK);
				weakTypes.add(PType.GALACTIC);
				break;
			case PSYCHIC:
				weakTypes.add(PType.FIGHTING);
				weakTypes.add(PType.POISON);
				break;
			case ICE:
				weakTypes.add(PType.GRASS);
				weakTypes.add(PType.GROUND);
				weakTypes.add(PType.FLYING);
				weakTypes.add(PType.DRAGON);
				break;
			case GALACTIC:
				weakTypes.add(PType.FIRE);
				weakTypes.add(PType.FIGHTING);
				weakTypes.add(PType.GROUND);
				break;
			default:
				break;
	    }
	    PType[] toReturn = new PType[weakTypes.size()];
	    return weakTypes.toArray(toReturn);
	}
	
	private void setMoveBank() {
		switch(this.id) {
		case 1:
			movebank = new Node[18];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.ABSORB);
			movebank[10] = new Node(Move.RAZOR_LEAF);
			movebank[14] = new Node(Move.SAND_ATTACK);
			movebank[17] = new Node(Move.HEADBUTT);
			break;
		case 2:
			movebank = new Node[31];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.ABSORB);
			movebank[10] = new Node(Move.RAZOR_LEAF);
			movebank[14] = new Node(Move.SAND_ATTACK);
			movebank[17] = new Node(Move.HEADBUTT);
			movebank[20] = new Node(Move.SLEEP_POWDER);
			movebank[24] = new Node(Move.MEGA_DRAIN);
			movebank[27] = new Node(Move.MAGNITUDE);
			movebank[30] = new Node(Move.ROCK_TOMB);
			break;
		case 3:
		    movebank = new Node[75];
		    movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.ABSORB);
			movebank[10] = new Node(Move.HIDDEN_POWER);
			movebank[14] = new Node(Move.SAND_ATTACK);
			movebank[17] = new Node(Move.HEADBUTT);
			movebank[20] = new Node(Move.SLEEP_POWDER);
			movebank[24] = new Node(Move.MEGA_DRAIN);
			movebank[27] = new Node(Move.MAGNITUDE);
			movebank[30] = new Node(Move.ROCK_TOMB);
			movebank[35] = new Node(Move.ROCKFALL_FRENZY);
			movebank[39] = new Node(Move.EARTHQUAKE);
			movebank[41] = new Node(Move.POWER_GEM);
			movebank[44] = new Node(Move.DRAGON_TAIL);
			movebank[49] = new Node(Move.LEAF_STORM);
			movebank[52] = new Node(Move.EARTH_POWER);
			movebank[56] = new Node(Move.SPIKY_SHIELD);
			movebank[64] = new Node(Move.HEAD_SMASH);
			movebank[74] = new Node(Move.FRENZY_PLANT);
		    break;
		case 4:
		    movebank = new Node[14];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.LEER);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.SLAM);
		    break;
		case 5:
		    movebank = new Node[36];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.LEER);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.SLAM);
		    movebank[16] = new Node(Move.HARDEN);
		    movebank[19] = new Node(Move.FLAME_WHEEL);
		    movebank[23] = new Node(Move.HEADBUTT);
		    movebank[26] = new Node(Move.MUD_BOMB);
		    movebank[31] = new Node(Move.FLAMETHROWER);
		    movebank[35] = new Node(Move.IRON_DEFENSE);
		    break;
		case 6:
		    movebank = new Node[75];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.LEER);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.SLAM);
		    movebank[16] = new Node(Move.HARDEN);
		    movebank[19] = new Node(Move.FLAME_WHEEL);
		    movebank[23] = new Node(Move.HEADBUTT);
		    movebank[26] = new Node(Move.MUD_BOMB);
		    movebank[31] = new Node(Move.FLAMETHROWER);
		    movebank[35] = new Node(Move.IRON_DEFENSE);
		    movebank[35].next = new Node(Move.MOLTEN_STEELSPIKE);
		    movebank[40] = new Node(Move.LAVA_PLUME);
		    movebank[45] = new Node(Move.PSYBEAM);
		    movebank[50] = new Node(Move.AURA_SPHERE);
		    movebank[54] = new Node(Move.ERUPTION);
		    movebank[59] = new Node(Move.EARTH_POWER);
		    movebank[69] = new Node(Move.HYPER_BEAM);
		    movebank[74] = new Node(Move.STEEL_BEAM);
		    break;
		case 7:
			movebank = new Node[15];
		    movebank[0] = new Node(Move.SCRATCH);
		    movebank[0].next = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.WATER_GUN);
		    movebank[10] = new Node(Move.BUBBLEBEAM);
		    movebank[14] = new Node(Move.MUD_SPORT);
		    break;
		case 8:
		    movebank = new Node[32];
		    movebank[0] = new Node(Move.SCRATCH);
		    movebank[0].next = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.WATER_GUN);
		    movebank[10] = new Node(Move.BUBBLEBEAM);
		    movebank[14] = new Node(Move.MUD_SPORT);
		    movebank[15] = new Node(Move.BITE);
		    movebank[18] = new Node(Move.MEAN_LOOK);
		    movebank[22] = new Node(Move.SLAM);
		    movebank[25] = new Node(Move.AQUA_JET);
		    movebank[31] = new Node(Move.DRAGON_TAIL);
		    break;
		case 9:
			movebank = new Node[80];
		    movebank[0] = new Node(Move.SCRATCH);
		    movebank[0].next = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.WATER_GUN);
		    movebank[10] = new Node(Move.BUBBLEBEAM);
		    movebank[14] = new Node(Move.MUD_SPORT);
		    movebank[15] = new Node(Move.BITE);
		    movebank[18] = new Node(Move.MEAN_LOOK);
		    movebank[22] = new Node(Move.SLAM);
		    movebank[25] = new Node(Move.AQUA_JET);
		    movebank[31] = new Node(Move.DRAGON_TAIL);
		    movebank[35] = new Node(Move.CHANNELING_BLOW);
		    movebank[37] = new Node(Move.LIQUIDATION);
		    movebank[40] = new Node(Move.HAMMER_ARM);
		    movebank[45] = new Node(Move.DRAGON_RUSH);
		    movebank[49] = new Node(Move.BULK_UP);
		    movebank[53] = new Node(Move.CLOSE_COMBAT);
		    movebank[58] = new Node(Move.GIGA_IMPACT);
		    movebank[65] = new Node(Move.WAVE_CRASH);
		    movebank[71] = new Node(Move.OUTRAGE);
		    movebank[79] = new Node(Move.HYDRO_CANNON);
		    break;
		case 10:
		    movebank = new Node[17];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[16] = new Node(Move.WHIRLWIND);
		    break;
		case 11:
		    movebank = new Node[33];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[16] = new Node(Move.WHIRLWIND);
		    movebank[21] = new Node(Move.TWISTER);
		    movebank[24] = new Node(Move.LIGHT_BEAM);
		    movebank[26] = new Node(Move.MIRROR_MOVE);
		    movebank[29] = new Node(Move.GLITTER_DANCE);
		    movebank[32] = new Node(Move.AGILITY);
		    break;
		case 12:
		    movebank = new Node[70];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[16] = new Node(Move.WHIRLWIND);
		    movebank[21] = new Node(Move.TWISTER);
		    movebank[24] = new Node(Move.LIGHT_BEAM);
		    movebank[26] = new Node(Move.MIRROR_MOVE);
		    movebank[29] = new Node(Move.GLITTER_DANCE);
		    movebank[32] = new Node(Move.AGILITY);
		    movebank[36] = new Node(Move.DAZZLING_GLEAM);
		    movebank[41] = new Node(Move.ROOST);
		    movebank[46] = new Node(Move.SUNNY_DAY);
		    movebank[51] = new Node(Move.MORNING_SUN);
		    movebank[55] = new Node(Move.HURRICANE);
		    movebank[62] = new Node(Move.SOLAR_BEAM);
		    movebank[69] = new Node(Move.PHOTON_GEYSER);
		    break;
		case 13:
		    movebank = new Node[15];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.SAND_ATTACK);
		    movebank[8] = new Node(Move.LEER);
		    movebank[12] = new Node(Move.PECK);
		    movebank[14] = new Node(Move.AIR_CUTTER);
		    break;
		case 14:
		    movebank = new Node[30];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.SAND_ATTACK);
		    movebank[8] = new Node(Move.LEER);
		    movebank[12] = new Node(Move.PECK);
		    movebank[14] = new Node(Move.AIR_CUTTER);
		    movebank[16] = new Node(Move.FURY_ATTACK);
		    movebank[19] = new Node(Move.WING_ATTACK);
		    movebank[23] = new Node(Move.AGILITY);
		    movebank[26] = new Node(Move.SPIKES);
		    movebank[29] = new Node(Move.METAL_CLAW);
		    break;
		case 15:
		    movebank = new Node[70];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.SAND_ATTACK);
		    movebank[8] = new Node(Move.LEER);
		    movebank[12] = new Node(Move.PECK);
		    movebank[14] = new Node(Move.AIR_CUTTER);
		    movebank[16] = new Node(Move.FURY_ATTACK);
		    movebank[19] = new Node(Move.WING_ATTACK);
		    movebank[23] = new Node(Move.AGILITY);
		    movebank[26] = new Node(Move.SPIKES);
		    movebank[29] = new Node(Move.METAL_CLAW);
		    movebank[31] = new Node(Move.METAL_SOUND);
		    movebank[34] = new Node(Move.STEEL_WING);
		    movebank[37] = new Node(Move.DRILL_PECK);
		    movebank[42] = new Node(Move.ROOST);
		    movebank[46] = new Node(Move.TAKE_DOWN);
		    movebank[52] = new Node(Move.NIGHT_SLASH);
		    movebank[56] = new Node(Move.GYRO_BALL);
		    movebank[62] = new Node(Move.EXTREME_SPEED);
		    movebank[69] = new Node(Move.BRAVE_BIRD);
		    break;
		case 16:
		    movebank = new Node[32];
		    movebank[0] = new Node(Move.POUND);
		    movebank[2] = new Node(Move.GROWL);
		    movebank[4] = new Node(Move.LEER);
		    movebank[8] = new Node(Move.DOUBLE_SLAP);
		    movebank[13] = new Node(Move.HEADBUTT);
		    movebank[18] = new Node(Move.MACH_PUNCH);
		    movebank[22] = new Node(Move.BITE);
		    movebank[26] = new Node(Move.STOCKPILE);
		    movebank[29] = new Node(Move.ICE_BALL);
		    movebank[31] = new Node(Move.ROLLOUT);
		    break;
		case 17:
		    movebank = new Node[70];
		    movebank[0] = new Node(Move.POUND);
		    movebank[2] = new Node(Move.GROWL);
		    movebank[4] = new Node(Move.LEER);
		    movebank[8] = new Node(Move.DOUBLE_SLAP);
		    movebank[13] = new Node(Move.HEADBUTT);
		    movebank[18] = new Node(Move.MACH_PUNCH);
		    movebank[22] = new Node(Move.BITE);
		    movebank[26] = new Node(Move.STOCKPILE);
		    movebank[29] = new Node(Move.ICE_BALL);
		    movebank[31] = new Node(Move.ROLLOUT);
		    movebank[33] = new Node(Move.CRUNCH);
		    movebank[36] = new Node(Move.TAKE_DOWN);
		    movebank[42] = new Node(Move.ZEN_HEADBUTT);
		    movebank[49] = new Node(Move.SUPER_FANG);
		    movebank[52] = new Node(Move.PSYBEAM);
		    movebank[56] = new Node(Move.METAL_CLAW);
		    movebank[59] = new Node(Move.GIGA_IMPACT);
		    movebank[64] = new Node(Move.ENDEAVOR);
		    movebank[69] = new Node(Move.EARTHQUAKE);
		    break;
		case 18:
		    movebank = new Node[80];
		    movebank[0] = new Node(Move.POUND);
		    movebank[2] = new Node(Move.GROWL);
		    movebank[4] = new Node(Move.LEER);
		    movebank[8] = new Node(Move.DOUBLE_SLAP);
		    movebank[13] = new Node(Move.HEADBUTT);
		    movebank[18] = new Node(Move.MACH_PUNCH);
		    movebank[22] = new Node(Move.BITE);
		    movebank[26] = new Node(Move.STOCKPILE);
		    movebank[29] = new Node(Move.ICE_BALL);
		    movebank[31] = new Node(Move.ROLLOUT);
		    movebank[35] = new Node(Move.ROCK_BLAST);
		    movebank[39] = new Node(Move.STEALTH_ROCK);
		    movebank[42] = new Node(Move.ZEN_HEADBUTT);
		    movebank[44] = new Node(Move.ROCK_SLIDE);
		    movebank[49] = new Node(Move.SUPER_FANG);
		    movebank[52] = new Node(Move.HEAD_SMASH);
		    movebank[54] = new Node(Move.GIGA_IMPACT);
		    movebank[56] = new Node(Move.SWORDS_DANCE);
		    movebank[59] = new Node(Move.STONE_EDGE);
		    movebank[61] = new Node(Move.EARTHQUAKE);
		    movebank[66] = new Node(Move.SUPERPOWER);
		    movebank[70] = new Node(Move.AMNESIA);
		    movebank[74] = new Node(Move.VOLT_TACKLE);
		    movebank[79] = new Node(Move.PLASMA_FISTS);
		    break;
		case 19:
		    movebank = new Node[20];
		    movebank[0] = new Node(Move.DOUBLE_KICK);
		    movebank[2] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.HEADBUTT);
		    movebank[9] = new Node(Move.MACH_PUNCH);
		    movebank[12] = new Node(Move.FEINT);
		    movebank[14] = new Node(Move.DETECT);
		    movebank[18] = new Node(Move.LOW_KICK);
		    movebank[19] = new Node(Move.BABY_DOLL_EYES);
		    break;
		case 20:
		    movebank = new Node[40];
		    movebank[0] = new Node(Move.DOUBLE_KICK);
		    movebank[2] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.HEADBUTT);
		    movebank[9] = new Node(Move.MACH_PUNCH);
		    movebank[12] = new Node(Move.FEINT);
		    movebank[14] = new Node(Move.DETECT);
		    movebank[18] = new Node(Move.LOW_KICK);
		    movebank[19] = new Node(Move.BABY_DOLL_EYES);
		    movebank[19].next = new Node(Move.DOUBLE_HIT);
		    movebank[22] = new Node(Move.FEINT_ATTACK);
		    movebank[24] = new Node(Move.HYPNOSIS);
		    movebank[27] = new Node(Move.PSYBEAM);
		    movebank[29] = new Node(Move.SWIFT);
		    movebank[31] = new Node(Move.FIRE_PUNCH);
		    movebank[31].next = new Node(Move.THUNDER_PUNCH);
		    movebank[31].next.next = new Node(Move.ICE_PUNCH);
		    movebank[33] = new Node(Move.SPARKLE_STRIKE);
		    movebank[35] = new Node(Move.TAKE_DOWN);
		    movebank[37] = new Node(Move.HI_JUMP_KICK);
		    movebank[39] = new Node(Move.PSYCHIC);
		    break;
		case 21:
		    movebank = new Node[70];
		    movebank[0] = new Node(Move.DOUBLE_KICK);
		    movebank[2] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.HEADBUTT);
		    movebank[9] = new Node(Move.MACH_PUNCH);
		    movebank[12] = new Node(Move.FEINT);
		    movebank[14] = new Node(Move.DETECT);
		    movebank[18] = new Node(Move.LOW_KICK);
		    movebank[19] = new Node(Move.BABY_DOLL_EYES);
		    movebank[19].next = new Node(Move.DOUBLE_HIT);
		    movebank[22] = new Node(Move.FEINT_ATTACK);
		    movebank[24] = new Node(Move.HYPNOSIS);
		    movebank[27] = new Node(Move.PSYBEAM);
		    movebank[29] = new Node(Move.SWIFT);
		    movebank[31] = new Node(Move.FIRE_PUNCH);
		    movebank[31].next = new Node(Move.THUNDER_PUNCH);
		    movebank[31].next.next = new Node(Move.ICE_PUNCH);
		    movebank[33] = new Node(Move.SPARKLE_STRIKE);
		    movebank[35] = new Node(Move.TAKE_DOWN);
		    movebank[37] = new Node(Move.HI_JUMP_KICK);
		    movebank[39] = new Node(Move.PSYCHIC);
		    movebank[39].next = new Node(Move.DESOLATE_VOID);
		    movebank[43] = new Node(Move.GALAXY_BLAST);
		    movebank[46] = new Node(Move.COSMIC_POWER);
		    movebank[49] = new Node(Move.ABDUCT);
		    movebank[54] = new Node(Move.MAGIC_CRASH);
		    movebank[58] = new Node(Move.BLIZZARD);
		    movebank[63] = new Node(Move.GENESIS_SUPERNOVA);
		    movebank[69] = new Node(Move.HYPER_BEAM);
		    break;
		case 22:
		    movebank = new Node[15];
		    movebank[0] = new Node(Move.BUG_BITE);
		    movebank[9] = new Node(Move.PROTECT);
		    movebank[14] = new Node(Move.HIDDEN_POWER);
		    break;
		case 23:
		    movebank = new Node[32];
		    movebank[0] = new Node(Move.BUG_BITE);
		    movebank[9] = new Node(Move.PROTECT);
		    movebank[14] = new Node(Move.HIDDEN_POWER);
		    movebank[17] = new Node(Move.FOCUS_ENERGY);
		    movebank[17].next = new Node(Move.POISON_STING);
		    movebank[19] = new Node(Move.SLAM);
		    movebank[24] = new Node(Move.PIN_MISSILE);
		    movebank[29] = new Node(Move.AQUA_TAIL);
		    movebank[31] = new Node(Move.IRON_TAIL);
		    break;
		case 24:
			movebank = new Node[60];
			movebank[0] = new Node(Move.BUG_BITE);
		    movebank[9] = new Node(Move.PROTECT);
		    movebank[14] = new Node(Move.HIDDEN_POWER);
		    movebank[17] = new Node(Move.FOCUS_ENERGY);
		    movebank[17].next = new Node(Move.POISON_STING);
		    movebank[19] = new Node(Move.SLAM);
		    movebank[24] = new Node(Move.PIN_MISSILE);
		    movebank[29] = new Node(Move.AQUA_TAIL);
		    movebank[31] = new Node(Move.IRON_TAIL);
		    movebank[33] = new Node(Move.TOXIC_SPIKES);
		    movebank[36] = new Node(Move.POISON_JAB);
		    movebank[39] = new Node(Move.ENDEAVOR);
		    movebank[45] = new Node(Move.FELL_STINGER);
		    movebank[49] = new Node(Move.QUIVER_DANCE);
		    movebank[59] = new Node(Move.FATAL_BIND);
			break;
		case 25:
			movebank = new Node[65];
			movebank[0] = new Node(Move.BUG_BITE);
		    movebank[9] = new Node(Move.PROTECT);
		    movebank[14] = new Node(Move.HIDDEN_POWER);
		    movebank[17] = new Node(Move.FOCUS_ENERGY);
		    movebank[17].next = new Node(Move.POISON_STING);
		    movebank[19] = new Node(Move.SLAM);
		    movebank[24] = new Node(Move.PIN_MISSILE);
		    movebank[29] = new Node(Move.AQUA_TAIL);
		    movebank[31] = new Node(Move.IRON_TAIL);
		    movebank[31].next = new Node(Move.ROCK_POLISH);
		    movebank[33] = new Node(Move.STEALTH_ROCK);
		    movebank[35] = new Node(Move.ROCK_SLIDE);
		    movebank[39] = new Node(Move.X_SCISSOR);
		    movebank[43] = new Node(Move.BULLDOZE);
		    movebank[44] = new Node(Move.SANDSTORM);
		    movebank[49] = new Node(Move.FLAIL);
		    movebank[54] = new Node(Move.ROCK_WRECKER);
		    movebank[59] = new Node(Move.EARTHQUAKE);
		    movebank[64] = new Node(Move.FATAL_BIND);
			break;
		case 26:
			movebank = new Node[24];
			movebank[0] = new Node(Move.POUND);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.ABSORB);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.ROOT_KICK);
			movebank[12] = new Node(Move.LEAF_TORNADO);
			movebank[15] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.ROCK_THROW);
			movebank[20] = new Node(Move.INGRAIN);
			movebank[23] = new Node(Move.ROCK_TOMB);
			break;
		case 27:
			movebank = new Node[70];
			movebank[0] = new Node(Move.POUND);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.ABSORB);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.ROOT_KICK);
			movebank[12] = new Node(Move.LEAF_TORNADO);
			movebank[15] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.ROCK_THROW);
			movebank[20] = new Node(Move.INGRAIN);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.DOUBLE_HIT);
			movebank[30] = new Node(Move.MACH_PUNCH);
			movebank[34] = new Node(Move.STEALTH_ROCK);
			movebank[38] = new Node(Move.HAMMER_ARM);
			movebank[42] = new Node(Move.SUBMISSION);
			movebank[44] = new Node(Move.EARTHQUAKE);
			movebank[46] = new Node(Move.POWER_WHIP);
			movebank[49] = new Node(Move.ICE_PUNCH);
			movebank[49].next = new Node(Move.THUNDER_PUNCH);
			movebank[52] = new Node(Move.SUPERPOWER);
			movebank[54] = new Node(Move.SPIKY_SHIELD);
			movebank[58] = new Node(Move.GLITTERING_SWORD);
			movebank[62] = new Node(Move.GRASSY_TERRAIN);
			movebank[69] = new Node(Move.OUTRAGE);
			break;
		case 28:
			movebank = new Node[80];
			movebank[0] = new Node(Move.DRAGON_RUSH);
			movebank[0].addToEnd(new Node(Move.LEAF_STORM));
			movebank[0].addToEnd(new Node(Move.GIGA_IMPACT));
			movebank[0].addToEnd(new Node(Move.FLAMETHROWER));
			movebank[0].addToEnd(new Node(Move.POUND));
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.ABSORB);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.ROOT_KICK);
			movebank[12] = new Node(Move.LEAF_TORNADO);
			movebank[15] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.ROCK_THROW);
			movebank[20] = new Node(Move.INGRAIN);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[49] = new Node(Move.DRAGON_PULSE);
			movebank[59] = new Node(Move.PETAL_DANCE);
			movebank[69] = new Node(Move.THUNDER);
			movebank[69].addToEnd(new Node(Move.HYPER_BEAM));
			movebank[79] = new Node(Move.DRACO_METEOR);
			break;
		case 29:
			movebank = new Node[16];
			movebank[0] = new Node(Move.ABSORB);
			movebank[3] = new Node(Move.GROWTH);
			movebank[6] = new Node(Move.WATER_SPORT);
			movebank[9] = new Node(Move.STUN_SPORE);
			movebank[12] = new Node(Move.MEGA_DRAIN);
			movebank[15] = new Node(Move.WORRY_SEED);
			break;
		case 30:
			movebank = new Node[50];
			movebank[0] = new Node(Move.ABSORB);
			movebank[3] = new Node(Move.GROWTH);
			movebank[6] = new Node(Move.WATER_SPORT);
			movebank[6].addToEnd(new Node(Move.POISON_STING));
			movebank[15] = new Node(Move.LEECH_SEED);
			movebank[18] = new Node(Move.MAGICAL_LEAF);
			movebank[21] = new Node(Move.GRASS_WHISTLE);
			movebank[24] = new Node(Move.GIGA_DRAIN);
			movebank[27] = new Node(Move.TOXIC_SPIKES);
			movebank[30] = new Node(Move.SWEET_SCENT);
			movebank[33] = new Node(Move.INGRAIN);
			movebank[36] = new Node(Move.PETAL_BLIZZARD);
			movebank[39] = new Node(Move.TOXIC);
			movebank[42] = new Node(Move.AROMATHERAPY);
			movebank[45] = new Node(Move.SYNTHESIS);
			movebank[49] = new Node(Move.PETAL_DANCE);
			break;
		case 31:
			movebank = new Node[1];
			movebank[0] = new Node(Move.GRASSY_TERRAIN);
			movebank[0].addToEnd(new Node(Move.MAGICAL_LEAF));
			movebank[0].addToEnd(new Node(Move.MEGA_DRAIN));
			movebank[0].addToEnd(new Node(Move.POISON_STING));
			movebank[0].addToEnd(new Node(Move.SWEET_SCENT));
			movebank[0].addToEnd(new Node(Move.VENOM_DRENCH));
			movebank[0].addToEnd(new Node(Move.WEATHER_BALL));
			break;
		case 32:
			movebank = new Node[15];
			movebank[0] = new Node(Move.STRING_SHOT);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[7] = new Node(Move.BUG_BITE);
			movebank[14] = new Node(Move.RAZOR_LEAF);
			break;
		case 33:
			movebank = new Node[33];
			movebank[0] = new Node(Move.STRING_SHOT);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[7] = new Node(Move.BUG_BITE);
			movebank[14] = new Node(Move.RAZOR_LEAF);
			movebank[19] = new Node(Move.PROTECT);
			movebank[24] = new Node(Move.GRASS_WHISTLE);
			movebank[32] = new Node(Move.STRUGGLE_BUG);
			break;
		case 34:
			movebank = new Node[50];
			movebank[0] = new Node(Move.SLASH);
			movebank[0].addToEnd(new Node(Move.FALSE_SWIPE));
			movebank[0].addToEnd(new Node(Move.RAZOR_LEAF));
			movebank[0].addToEnd(new Node(Move.PIN_MISSILE));
			movebank[21] = new Node(Move.STRUGGLE_BUG);
			movebank[28] = new Node(Move.FELL_STINGER);
			movebank[31] = new Node(Move.CROSS_POISON);
			movebank[35] = new Node(Move.LEAF_BLADE);
			movebank[38] = new Node(Move.X_SCISSOR);
			movebank[42] = new Node(Move.ENTRAINMENT);
			movebank[45] = new Node(Move.SWORDS_DANCE);
			movebank[49] = new Node(Move.LEAF_STORM);
			break;
		case 35:
			movebank = new Node[25];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD_SLAP);
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[14] = new Node(Move.BITE);
			movebank[19] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.STICKY_WEB);
			break;
		case 36:
			movebank = new Node[64];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD_SLAP);
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[14] = new Node(Move.BITE);
			movebank[19] = new Node(Move.CHARGE);
			movebank[22] = new Node(Move.SPARK);
			movebank[28] = new Node(Move.STICKY_WEB);
			movebank[35] = new Node(Move.X_SCISSOR);
			movebank[42] = new Node(Move.CRUNCH);
			movebank[49] = new Node(Move.DIG);
			movebank[56] = new Node(Move.IRON_DEFENSE);
			movebank[63] = new Node(Move.DISCHARGE);
			break;
		case 37:
			movebank = new Node[64];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD_SLAP);
			movebank[0].addToEnd(new Node(Move.THUNDERBOLT));
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[14] = new Node(Move.BITE);
			movebank[19] = new Node(Move.CHARGE);
			movebank[22] = new Node(Move.SPARK);
			movebank[28] = new Node(Move.STICKY_WEB);
			movebank[35] = new Node(Move.BUG_BUZZ);
			movebank[42] = new Node(Move.GUILLOTINE);
			movebank[49] = new Node(Move.FLY);
			movebank[56] = new Node(Move.AGILITY);
			movebank[63] = new Node(Move.ZAP_CANNON);
			break;
		case 38:
			movebank = new Node[55];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.LEAFAGE);
			movebank[8] = new Node(Move.ENDURE);
			movebank[11] = new Node(Move.ODOR_SLEUTH);
			movebank[14] = new Node(Move.PROTECT);
			movebank[18] = new Node(Move.ROUND);
			movebank[22] = new Node(Move.SLACK_OFF);
			movebank[25] = new Node(Move.CHARM);
			movebank[28] = new Node(Move.ENDEAVOR);
			movebank[31] = new Node(Move.COTTON_GUARD);
			movebank[35] = new Node(Move.LEAF_TORNADO);
			movebank[39] = new Node(Move.LEECH_SEED);
			movebank[42] = new Node(Move.GIGA_DRAIN);
			movebank[44] = new Node(Move.ROLLOUT);
			movebank[54] = new Node(Move.LEAF_STORM);
			break;
		case 39:
			movebank = new Node[60];
			movebank[0] = new Node(Move.POWER_WHIP);
			movebank[0].addToEnd(new Node(Move.HORN_LEECH));
			movebank[0].addToEnd(new Node(Move.FLAME_CHARGE));
			movebank[14] = new Node(Move.STOMP);
			movebank[15] = new Node(Move.HEADBUTT);
			movebank[17] = new Node(Move.FRUSTRATION);
			movebank[24] = new Node(Move.RETURN);
			movebank[28] = new Node(Move.TAKE_DOWN);
			movebank[31] = new Node(Move.COTTON_GUARD);
			movebank[34] = new Node(Move.POWER_WHIP);
			movebank[38] = new Node(Move.HORN_LEECH);
			movebank[42] = new Node(Move.GIGA_IMPACT);
			movebank[45] = new Node(Move.DRILL_RUN);
			movebank[49] = new Node(Move.HEAD_SMASH);
			movebank[59] = new Node(Move.SYNTHESIS);
			break;
		case 40:
			movebank = new Node[60];
			movebank[0] = new Node(Move.DAZZLING_GLEAM);
			movebank[0].addToEnd(new Node(Move.DRAINING_KISS));
			movebank[0].addToEnd(new Node(Move.LIGHT_SCREEN));
			movebank[0].addToEnd(new Node(Move.SUNNY_DAY));
			movebank[14] = new Node(Move.SWIFT);
			movebank[17] = new Node(Move.SYNTHESIS);
			movebank[19] = new Node(Move.GRASSY_TERRAIN);
			movebank[24] = new Node(Move.SOLAR_BEAM);
			movebank[28] = new Node(Move.GLITTER_DANCE);
			movebank[31] = new Node(Move.COTTON_GUARD);
			movebank[34] = new Node(Move.ENERGY_BALL);
			movebank[38] = new Node(Move.GLITZY_GLOW);
			movebank[42] = new Node(Move.GIGA_DRAIN);
			movebank[45] = new Node(Move.PHOTON_GEYSER);
			movebank[47] = new Node(Move.EXTRASENSORY);
			movebank[54] = new Node(Move.EARTH_POWER);
			movebank[59] = new Node(Move.LEAF_STORM);
			break;
		case 41:
			movebank = new Node[30];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[19] = new Node(Move.QUIVER_DANCE);
			movebank[29] = new Node(Move.STICKY_WEB);
			break;
		case 42:
			movebank = new Node[35];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[14] = new Node(Move.SWORD_SPIN);
			movebank[18] = new Node(Move.BUG_BITE);
			movebank[22] = new Node(Move.SLASH);
			movebank[26] = new Node(Move.SACRED_SWORD);
			movebank[34] = new Node(Move.X_SCISSOR);
			break;
		case 43:
			movebank = new Node[55];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[14] = new Node(Move.SWORD_SPIN);
			movebank[18] = new Node(Move.BUG_BITE);
			movebank[22] = new Node(Move.SLASH);
			movebank[24] = new Node(Move.SPEEDY_SHURIKEN);
			movebank[24].addToEnd(new Node(Move.MACHETE_JAB));
			movebank[24].addToEnd(new Node(Move.PISTOL_POP));
			movebank[24].addToEnd(new Node(Move.SACRED_SWORD));
			movebank[29] = new Node(Move.U_TURN);
			movebank[34] = new Node(Move.AGILITY);
			movebank[41] = new Node(Move.FIRST_IMPRESSION);
			movebank[44] = new Node(Move.DETECT);
			movebank[49] = new Node(Move.NO_RETREAT);
			movebank[54] = new Node(Move.GUILLOTINE);
			break;
		case 44:
			movebank = new Node[12];
			movebank[0] = new Node(Move.ASTONISH);
			movebank[1] = new Node(Move.GROWL);
			movebank[2] = new Node(Move.ABSORB);
			movebank[5] = new Node(Move.WATER_GUN);
			movebank[8] = new Node(Move.HAZE);
			movebank[11] = new Node(Move.MEGA_DRAIN);
			break;
		case 45:
			movebank = new Node[50];
			movebank[0] = new Node(Move.ASTONISH);
			movebank[0].addToEnd(new Node(Move.TEETER_DANCE));
			movebank[0].addToEnd(new Node(Move.FAKE_OUT));
			movebank[1] = new Node(Move.GROWL);
			movebank[2] = new Node(Move.ABSORB);
			movebank[5] = new Node(Move.WATER_GUN);
			movebank[8] = new Node(Move.HAZE);
			movebank[11] = new Node(Move.MEGA_DRAIN);
			movebank[15] = new Node(Move.FURY_SWIPES);
			movebank[19] = new Node(Move.BUBBLEBEAM);
			movebank[24] = new Node(Move.GIGA_DRAIN);
			movebank[29] = new Node(Move.RAIN_DANCE);
			movebank[39] = new Node(Move.ZEN_HEADBUTT);
			movebank[44] = new Node(Move.ENERGY_BALL);
			movebank[49] = new Node(Move.HYDRO_PUMP);
			break;
		case 46:
			movebank = new Node[1];
			movebank[0] = new Node(Move.TEETER_DANCE);
			movebank[0].addToEnd(new Node(Move.FAKE_OUT));
			movebank[0].addToEnd(new Node(Move.FLAIL));
			movebank[0].addToEnd(new Node(Move.HAZE));
			movebank[0].addToEnd(new Node(Move.BUBBLEBEAM));
			movebank[0].addToEnd(new Node(Move.GIGA_DRAIN));
			movebank[0].addToEnd(new Node(Move.RAIN_DANCE));
			movebank[0].addToEnd(new Node(Move.ZEN_HEADBUTT));
			movebank[0].addToEnd(new Node(Move.ENERGY_BALL));
			movebank[0].addToEnd(new Node(Move.HYDRO_PUMP));
			break;
		case 47:
			movebank = new Node[70];
			movebank[0] = new Node(Move.FLASH);
			movebank[0].next = new Node(Move.DOUBLE_KICK);
			movebank[4] = new Node(Move.SWIFT);
			movebank[7] = new Node(Move.BABY_DOLL_EYES);
			movebank[12] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.DRAINING_KISS);
			movebank[19] = new Node(Move.MAGIC_REFLECT);
			movebank[22] = new Node(Move.MOONLIGHT);
			movebank[25] = new Node(Move.CHARM);
			movebank[28] = new Node(Move.MAGIC_POWDER);
			movebank[29] = new Node(Move.SPARKLING_TERRAIN);
			movebank[32] = new Node(Move.LUSTER_PURGE);
			movebank[34] = new Node(Move.SWEET_KISS);
			movebank[37] = new Node(Move.SPARKLY_SWIRL);
			movebank[39] = new Node(Move.GLITZY_GLOW);
			movebank[41] = new Node(Move.TRI_ATTACK);
			movebank[43] = new Node(Move.MAGIC_BLAST);
			movebank[44] = new Node(Move.FIERY_DANCE);
			movebank[49] = new Node(Move.GLITTER_DANCE);
			movebank[52] = new Node(Move.MAGIC_TOMB);
			movebank[54] = new Node(Move.PHOTON_GEYSER);
			movebank[59] = new Node(Move.PRISMATIC_LASER);
			movebank[64] = new Node(Move.OVERHEAT);
			movebank[69] = new Node(Move.GEOMANCY);
			break;
		case 48:
			movebank = new Node[21];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.MUD_SPORT);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[20] = new Node(Move.MAGNITUDE);
			break;
		case 49:
			movebank = new Node[36];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.MUD_SPORT);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[20] = new Node(Move.MAGNITUDE);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.ROLLOUT);
			movebank[28] = new Node(Move.SLAM);
			movebank[31] = new Node(Move.SELF_DESTRUCT);
			movebank[35] = new Node(Move.ROCK_BLAST);
			break;
		case 50:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.MUD_SPORT);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[20] = new Node(Move.MAGNITUDE);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.ROLLOUT);
			movebank[28] = new Node(Move.SLAM);
			movebank[31] = new Node(Move.SELF_DESTRUCT);
			movebank[35] = new Node(Move.ROCK_BLAST);
			movebank[39] = new Node(Move.FIRE_BLAST);
			movebank[42] = new Node(Move.STONE_EDGE);
			movebank[45] = new Node(Move.IRON_DEFENSE);
			movebank[49] = new Node(Move.EARTHQUAKE);
			movebank[53] = new Node(Move.EXPLOSION);
			movebank[59] = new Node(Move.SUPERPOWER);
			movebank[69] = new Node(Move.HYPER_BEAM);
			break;
		case 51:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.MUD_SPORT);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[20] = new Node(Move.MAGNITUDE);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.ROLLOUT);
			movebank[28] = new Node(Move.SLAM);
			movebank[31] = new Node(Move.SELF_DESTRUCT);
			movebank[35] = new Node(Move.ROCK_BLAST);
			movebank[35].next = new Node(Move.DAZZLING_GLEAM);
			movebank[39] = new Node(Move.SHELL_SMASH);
			movebank[42] = new Node(Move.GLITTER_DANCE);
			movebank[44] = new Node(Move.FLASH_CANNON);
			movebank[48] = new Node(Move.PLAY_ROUGH);
			movebank[51] = new Node(Move.REFLECT);
			movebank[54] = new Node(Move.DIAMOND_STORM);
			movebank[59] = new Node(Move.GLITTERING_SWORD);
			movebank[69] = new Node(Move.LIGHT_OF_RUIN);
			break;
		case 52:
			movebank = new Node[28];
			movebank[0] = new Node(Move.HARDEN);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[5] = new Node(Move.SCREECH);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[10] = new Node(Move.BIND);
			movebank[14] = new Node(Move.ROCK_THROW);
			movebank[18] = new Node(Move.LOW_SWEEP);
			movebank[21] = new Node(Move.PSYCHO_CUT);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.SLAM);
			break;
		case 53:
			movebank = new Node[50];
			movebank[0] = new Node(Move.HARDEN);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[5] = new Node(Move.SCREECH);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[10] = new Node(Move.BIND);
			movebank[14] = new Node(Move.ROCK_THROW);
			movebank[18] = new Node(Move.LOW_SWEEP);
			movebank[21] = new Node(Move.PSYCHO_CUT);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.SLAM);
			movebank[29] = new Node(Move.ROCK_POLISH);
			movebank[34] = new Node(Move.PSYCHIC_FANGS);
			movebank[37] = new Node(Move.CURSE);
			movebank[41] = new Node(Move.SKULL_BASH);
			movebank[45] = new Node(Move.IRON_TAIL);
			movebank[49] = new Node(Move.EARTHQUAKE);
			break;
		case 54:
			movebank = new Node[65];
			movebank[0] = new Node(Move.HARDEN);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[5] = new Node(Move.SCREECH);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[10] = new Node(Move.BIND);
			movebank[14] = new Node(Move.ROCK_THROW);
			movebank[18] = new Node(Move.LOW_SWEEP);
			movebank[21] = new Node(Move.PSYCHO_CUT);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.SLAM);
			movebank[29] = new Node(Move.SHADOW_CLAW);
			movebank[32] = new Node(Move.DRAGON_RAGE);
			movebank[36] = new Node(Move.PSYCHIC_FANGS);
			movebank[40] = new Node(Move.SKULL_BASH);
			movebank[44] = new Node(Move.IRON_TAIL);
			movebank[48] = new Node(Move.PSYCHIC_TERRAIN);
			movebank[54] = new Node(Move.PHOTON_GEYSER);
			movebank[58] = new Node(Move.HEAD_SMASH);
			movebank[62] = new Node(Move.OUTRAGE);
			movebank[64] = new Node(Move.EARTHQUAKE);
			break;
		case 55:
			movebank = new Node[31];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.ROCK_THROW);
			movebank[8] = new Node(Move.SLAM);
			movebank[11] = new Node(Move.BITE);
			movebank[14] = new Node(Move.ROCK_POLISH);
			movebank[18] = new Node(Move.HOWL);
			movebank[20] = new Node(Move.ROCK_BLAST);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			movebank[26] = new Node(Move.PLAY_ROUGH);
			movebank[30] = new Node(Move.THUNDER_FANG);
			break;
		case 56:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.ROCK_THROW);
			movebank[8] = new Node(Move.SLAM);
			movebank[11] = new Node(Move.BITE);
			movebank[14] = new Node(Move.ROCK_POLISH);
			movebank[18] = new Node(Move.HOWL);
			movebank[20] = new Node(Move.ROCK_BLAST);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			movebank[26] = new Node(Move.PLAY_ROUGH);
			movebank[30] = new Node(Move.THUNDER_FANG);
			movebank[33] = new Node(Move.DARK_PULSE);
			movebank[36] = new Node(Move.TAKE_DOWN);
			movebank[40] = new Node(Move.POWER_GEM);
			movebank[44] = new Node(Move.MAGIC_FANG);
			movebank[48] = new Node(Move.ACCELEROCK);
			movebank[52] = new Node(Move.THROAT_CHOP);
			movebank[54] = new Node(Move.FIRE_BLAST);
			movebank[58] = new Node(Move.SHADOW_BALL);
			movebank[62] = new Node(Move.PSYCHIC_FANGS);
			movebank[66] = new Node(Move.PLASMA_FISTS);
			movebank[69] = new Node(Move.JAW_LOCK);
			break;
		case 57:
			movebank = new Node[36];
			movebank[0] = new Node(Move.KARATE_CHOP);
			movebank[2] = new Node(Move.DOUBLE_KICK);
			movebank[4] = new Node(Move.MUD_SLAP);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[14] = new Node(Move.REVENGE);
			movebank[19] = new Node(Move.ANCIENT_POWER);
			movebank[25] = new Node(Move.MAGIC_BLAST);
			movebank[29] = new Node(Move.DETECT);
			movebank[33] = new Node(Move.ACROBATICS);
			movebank[35] = new Node(Move.EARTH_POWER);
			break;
		case 58:
			movebank = new Node[70];
			movebank[0] = new Node(Move.KARATE_CHOP);
			movebank[2] = new Node(Move.DOUBLE_KICK);
			movebank[4] = new Node(Move.MUD_SLAP);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[14] = new Node(Move.REVENGE);
			movebank[19] = new Node(Move.ANCIENT_POWER);
			movebank[25] = new Node(Move.MAGIC_BLAST);
			movebank[29] = new Node(Move.DETECT);
			movebank[33] = new Node(Move.ACROBATICS);
			movebank[35] = new Node(Move.EARTH_POWER);
			movebank[35].next = new Node(Move.AIR_SLASH);
			movebank[39] = new Node(Move.HI_JUMP_KICK);
			movebank[43] = new Node(Move.DRILL_PECK);
			movebank[46] = new Node(Move.FOCUS_BLAST);
			movebank[49] = new Node(Move.AEROBLAST);
			movebank[52] = new Node(Move.SHADOW_BALL);
			movebank[56] = new Node(Move.DRAGON_PULSE);
			movebank[59] = new Node(Move.CALM_MIND);
			movebank[62] = new Node(Move.BRAVE_BIRD);
			movebank[66] = new Node(Move.DAZZLING_GLEAM);
			movebank[69] = new Node(Move.MAGIC_BLAST);
			break;
		case 59:
			movebank = new Node[30];
			movebank[0] = new Node(Move.POWDER_SNOW);
			movebank[0].addToEnd(new Node(Move.MEAN_LOOK));
			movebank[0].addToEnd(new Node(Move.CONFUSION));
			movebank[0].addToEnd(new Node(Move.GLARE));
			movebank[9] = new Node(Move.GUST);
			movebank[29] = new Node(Move.NIGHT_SHADE);
			break;
		case 60:
			movebank = new Node[60];
			movebank[0] = new Node(Move.PECK);
			movebank[14] = new Node(Move.ICE_SHARD);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[21] = new Node(Move.SCARY_FACE);
			movebank[24] = new Node(Move.SNOWSCAPE);
			movebank[28] = new Node(Move.NIGHT_SHADE);
			movebank[32] = new Node(Move.MIST_BALL);
			movebank[36] = new Node(Move.ICE_BEAM);
			movebank[39] = new Node(Move.PSYCHIC);
			movebank[44] = new Node(Move.BLIZZARD);
			movebank[49] = new Node(Move.AURORA_VEIL);
			movebank[59] = new Node(Move.LUSTER_PURGE);
			break;
		case 61:
			movebank = new Node[60];
			movebank[0] = new Node(Move.DOUBLE_HIT);
			movebank[0].addToEnd(new Node(Move.LEER));
			movebank[4] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.POWDER_SNOW);
			movebank[14] = new Node(Move.HORN_LEECH);
			movebank[19] = new Node(Move.RED_NOSE_BOOST);
			movebank[22] = new Node(Move.ICE_SHARD);
			movebank[24] = new Node(Move.MAGIC_BLAST);
			movebank[29] = new Node(Move.LIGHT_BEAM);
			movebank[32] = new Node(Move.SNOWSCAPE);
			movebank[34] = new Node(Move.TAKE_DOWN);
			movebank[39] = new Node(Move.AURORA_VEIL);
			movebank[44] = new Node(Move.BLIZZARD);
			movebank[49] = new Node(Move.TWINKLE_TACKLE);
			movebank[54] = new Node(Move.SPARKLING_TERRAIN);
			movebank[59] = new Node(Move.TRICK_ROOM);
			break;
		case 62:
			movebank = new Node[1];
			movebank[0] = new Node(Move.POWDER_SNOW);
			movebank[0].addToEnd(new Node(Move.STRUGGLE_BUG));
			break;
		case 63:
			movebank = new Node[50];
			movebank[0] = new Node(Move.POWDER_SNOW);
			movebank[0].addToEnd(new Node(Move.STRUGGLE_BUG));
			movebank[0].addToEnd(new Node(Move.ICY_WIND));
			movebank[0].addToEnd(new Node(Move.CAPTIVATE));
			movebank[3] = new Node(Move.STUN_SPORE);
			movebank[7] = new Node(Move.INFESTATION);
			movebank[11] = new Node(Move.HAZE);
			movebank[15] = new Node(Move.DEFOG);
			movebank[20] = new Node(Move.AURORA_BEAM);
			movebank[23] = new Node(Move.SNOWSCAPE);
			movebank[31] = new Node(Move.BUG_BUZZ);
			movebank[35] = new Node(Move.AURORA_VEIL);
			movebank[39] = new Node(Move.BLIZZARD);
			movebank[44] = new Node(Move.TAILWIND);
			movebank[49] = new Node(Move.QUIVER_DANCE);
			break;
		case 64:
			movebank = new Node[40];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[2] = new Node(Move.MUD_SPORT);
			movebank[5] = new Node(Move.ICE_BALL);
			movebank[8] = new Node(Move.DEFENSE_CURL);
			movebank[11] = new Node(Move.HEADBUTT);
			movebank[15] = new Node(Move.BULLDOZE);
			movebank[18] = new Node(Move.ICE_FANG);
			movebank[21] = new Node(Move.MAGNITUDE);
			movebank[25] = new Node(Move.HAZE);
			movebank[29] = new Node(Move.EARTHQUAKE);
			movebank[39] = new Node(Move.ICICLE_CRASH);
			break;
		case 65:
			movebank = new Node[65];
			movebank[0] = new Node(Move.ICICLE_SPEAR);
			movebank[0].addToEnd(new Node(Move.MACH_PUNCH));
			movebank[2] = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.POWDER_SNOW);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[11] = new Node(Move.HEADBUTT);
			movebank[13] = new Node(Move.LOW_KICK);
			movebank[15] = new Node(Move.FLASH_RAY);
			movebank[19] = new Node(Move.ICE_SHARD);
			movebank[23] = new Node(Move.VITAL_THROW);
			movebank[26] = new Node(Move.AURORA_BEAM);
			movebank[30] = new Node(Move.SNOWSCAPE);
			movebank[34] = new Node(Move.ICE_PUNCH);
			movebank[37] = new Node(Move.SUBMISSION);
			movebank[44] = new Node(Move.CLOSE_COMBAT);
			movebank[49] = new Node(Move.ICE_SPINNER);
			movebank[59] = new Node(Move.FISSURE);
			movebank[64] = new Node(Move.PLAY_ROUGH);
			break;
		case 66:
			movebank = new Node[40];
			movebank[0] = new Node(Move.HEADBUTT);
			movebank[0].next = new Node(Move.BEAT_UP);
			movebank[4] = new Node(Move.FEINT_ATTACK);
			movebank[7] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.DRILL_RUN);
			movebank[19] = new Node(Move.HEAD_SMASH);
			movebank[24] = new Node(Move.SPIKY_SHIELD);
			movebank[29] = new Node(Move.STEALTH_ROCK);
			movebank[39] = new Node(Move.BODY_PRESS);
			break;
		case 67:
			movebank = new Node[70];
			movebank[0] = new Node(Move.HEADBUTT);
			movebank[0].next = new Node(Move.BEAT_UP);
			movebank[4] = new Node(Move.FEINT_ATTACK);
			movebank[7] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.DRILL_RUN);
			movebank[19] = new Node(Move.HEAD_SMASH);
			movebank[24] = new Node(Move.SPIKY_SHIELD);
			movebank[29] = new Node(Move.STEALTH_ROCK);
			movebank[39] = new Node(Move.BODY_PRESS);
			movebank[47] = new Node(Move.MACH_PUNCH);
			movebank[47].next = new Node(Move.STONE_EDGE);
			movebank[49] = new Node(Move.SUPERPOWER);
			movebank[54] = new Node(Move.PLASMA_FISTS);
			movebank[59] = new Node(Move.NO_RETREAT);
			movebank[69] = new Node(Move.METEOR_ASSAULT);
			break;
		case 68:
			movebank = new Node[31];
			movebank[0] = new Node(Move.DEFENSE_CURL);
			movebank[0].addToEnd(new Node(Move.POWDER_SNOW));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.WATER_GUN));
			movebank[4] = new Node(Move.ROLLOUT);
			movebank[8] = new Node(Move.ENCORE);
			movebank[12] = new Node(Move.ICE_BALL);
			movebank[16] = new Node(Move.BRINE);
			movebank[20] = new Node(Move.AURORA_BEAM);
			movebank[25] = new Node(Move.BODY_SLAM);
			movebank[30] = new Node(Move.REST);
			movebank[30].next = new Node(Move.SNORE);
			break;
		case 69:
			movebank = new Node[43];
			movebank[0] = new Node(Move.DEFENSE_CURL);
			movebank[0].addToEnd(new Node(Move.POWDER_SNOW));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.WATER_GUN));
			movebank[4] = new Node(Move.ROLLOUT);
			movebank[8] = new Node(Move.ENCORE);
			movebank[12] = new Node(Move.ICE_BALL);
			movebank[16] = new Node(Move.BRINE);
			movebank[20] = new Node(Move.AURORA_BEAM);
			movebank[25] = new Node(Move.BODY_SLAM);
			movebank[30] = new Node(Move.REST);
			movebank[30].next = new Node(Move.SNORE);
			movebank[31] = new Node(Move.SWAGGER);
			movebank[37] = new Node(Move.SNOWSCAPE);
			movebank[42] = new Node(Move.BLIZZARD);
			break;
		case 70:
			movebank = new Node[60];
			movebank[0] = new Node(Move.DEFENSE_CURL);
			movebank[0].addToEnd(new Node(Move.POWDER_SNOW));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.WATER_GUN));
			movebank[4] = new Node(Move.ROLLOUT);
			movebank[8] = new Node(Move.ENCORE);
			movebank[12] = new Node(Move.ICE_BALL);
			movebank[16] = new Node(Move.BRINE);
			movebank[20] = new Node(Move.AURORA_BEAM);
			movebank[25] = new Node(Move.BODY_SLAM);
			movebank[30] = new Node(Move.REST);
			movebank[30].next = new Node(Move.SNORE);
			movebank[31] = new Node(Move.SWAGGER);
			movebank[37] = new Node(Move.SNOWSCAPE);
			movebank[42] = new Node(Move.BLIZZARD);
			movebank[43] = new Node(Move.ICE_FANG);
			movebank[49] = new Node(Move.SHEER_COLD);
			movebank[54] = new Node(Move.PSYCHIC_FANGS);
			movebank[59] = new Node(Move.HYDRO_PUMP);
			break;
		case 71:
			movebank = new Node[30];
			movebank[0] = new Node(Move.BUBBLE);
			movebank[2] = new Node(Move.POUND);
			movebank[4] = new Node(Move.LEER);
			movebank[6] = new Node(Move.POWDER_SNOW);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.MAGIC_REFLECT);
			movebank[14] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.LIGHT_BEAM);
			movebank[19] = new Node(Move.ICE_SHARD);
			movebank[23] = new Node(Move.AQUA_JET);
			movebank[26] = new Node(Move.BOUNCE);
			movebank[29] = new Node(Move.DAZZLING_GLEAM);
			break;
		case 72:
			movebank = new Node[60];
			movebank[0] = new Node(Move.BUBBLE);
			movebank[2] = new Node(Move.POUND);
			movebank[4] = new Node(Move.LEER);
			movebank[6] = new Node(Move.POWDER_SNOW);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.MAGIC_REFLECT);
			movebank[14] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.LIGHT_BEAM);
			movebank[19] = new Node(Move.ICE_SHARD);
			movebank[23] = new Node(Move.AQUA_JET);
			movebank[26] = new Node(Move.BOUNCE);
			movebank[29] = new Node(Move.DAZZLING_GLEAM);
			movebank[30] = new Node(Move.AURORA_BEAM);
			movebank[35] = new Node(Move.HYDRO_PUMP);
			movebank[39] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[43] = new Node(Move.SPARKLING_ARIA);
			movebank[46] = new Node(Move.SHEER_COLD);
			movebank[51] = new Node(Move.HURRICANE);
			movebank[54] = new Node(Move.BLIZZARD);
			movebank[59] = new Node(Move.MOONBLAST);
			break;
		case 73:
			movebank = new Node[35];
			movebank[0] = new Node(Move.INFESTATION);
			movebank[0].next = new Node(Move.FLASH);
			movebank[3] = new Node(Move.PAYBACK);
			movebank[6] = new Node(Move.SLASH);
			movebank[9] = new Node(Move.PURSUIT);
			movebank[16] = new Node(Move.BUG_BITE);
			movebank[20] = new Node(Move.FEINT_ATTACK);
			movebank[29] = new Node(Move.MOONLIGHT);
			movebank[34] = new Node(Move.LEECH_LIFE);
			break;
		case 74:
			movebank = new Node[60];
			movebank[0] = new Node(Move.INFESTATION);
			movebank[0].next = new Node(Move.FLASH);
			movebank[3] = new Node(Move.PAYBACK);
			movebank[6] = new Node(Move.SLASH);
			movebank[9] = new Node(Move.PURSUIT);
			movebank[16] = new Node(Move.BUG_BITE);
			movebank[20] = new Node(Move.FEINT_ATTACK);
			movebank[29] = new Node(Move.MOONLIGHT);
			movebank[34] = new Node(Move.LEECH_LIFE);
			movebank[19] = new Node(Move.NIGHT_SLASH);
			movebank[22] = new Node(Move.BOUNCE);
			movebank[24] = new Node(Move.FURY_CUTTER);
			movebank[29] = new Node(Move.SLASH);
			movebank[32] = new Node(Move.QUIVER_DANCE);
			movebank[35] = new Node(Move.TWINEEDLE);
			movebank[39] = new Node(Move.NIGHT_DAZE);
			movebank[43] = new Node(Move.X_SCISSOR);
			movebank[48] = new Node(Move.EXTREME_SPEED);
			movebank[54] = new Node(Move.DARKEST_LARIAT);
			movebank[59] = new Node(Move.FIRST_IMPRESSION);
			break;
		case 75:
			movebank = new Node[30];
			movebank[0] = new Node(Move.CONFUSION);
			movebank[0].next = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[9] = new Node(Move.MAGIC_POWDER);
			movebank[14] = new Node(Move.AROMATHERAPY);
			movebank[16] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[24] = new Node(Move.HEAL_PULSE);
			movebank[29] = new Node(Move.SPARKLY_SWIRL);
			break;
		case 76:
			movebank = new Node[37];
			movebank[0] = new Node(Move.CONFUSION);
			movebank[0].next = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[9] = new Node(Move.MAGIC_POWDER);
			movebank[14] = new Node(Move.AROMATHERAPY);
			movebank[16] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[24] = new Node(Move.HEAL_PULSE);
			movebank[29] = new Node(Move.SPARKLY_SWIRL);
			movebank[31] = new Node(Move.BRUTAL_SWING);
			movebank[34] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.CALM_MIND);
			break;
		case 77:
			movebank = new Node[70];
			movebank[0] = new Node(Move.CONFUSION);
			movebank[0].next = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[9] = new Node(Move.MAGIC_POWDER);
			movebank[14] = new Node(Move.AROMATHERAPY);
			movebank[16] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[24] = new Node(Move.HEAL_PULSE);
			movebank[29] = new Node(Move.SPARKLY_SWIRL);
			movebank[31] = new Node(Move.BRUTAL_SWING);
			movebank[34] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.CALM_MIND);
			movebank[41] = new Node(Move.MAGIC_REFLECT);
			movebank[44] = new Node(Move.PSYCHO_CUT);
			movebank[48] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[51] = new Node(Move.PSYCHIC);
			movebank[54] = new Node(Move.HEALING_WISH);
			movebank[57] = new Node(Move.MAGIC_TOMB);
			movebank[60] = new Node(Move.SPARKLING_TERRAIN);
			movebank[64] = new Node(Move.MORNING_SUN);
			movebank[69] = new Node(Move.MAGIC_CRASH);
			break;
		case 78:
			movebank = new Node[26];
			movebank[0] = new Node(Move.POUND);
			movebank[2] = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.WATER_GUN);
			movebank[6] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.BABY_DOLL_EYES);
			movebank[10] = new Node(Move.DETECT);
			movebank[14] = new Node(Move.AQUA_RING);
			movebank[16] = new Node(Move.RAZOR_SHELL);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.CONFUSE_RAY);
			break;
		case 79:
			movebank = new Node[60];
			movebank[0] = new Node(Move.POUND);
			movebank[2] = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.WATER_GUN);
			movebank[6] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.BABY_DOLL_EYES);
			movebank[10] = new Node(Move.DETECT);
			movebank[14] = new Node(Move.AQUA_RING);
			movebank[16] = new Node(Move.RAZOR_SHELL);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.CONFUSE_RAY);
			movebank[30] = new Node(Move.HYPNOSIS);
			movebank[35] = new Node(Move.PSYCHIC);
			movebank[39] = new Node(Move.MAGIC_TOMB);
			movebank[42] = new Node(Move.LIGHT_SCREEN);
			movebank[45] = new Node(Move.HYDRO_PUMP);
			movebank[49] = new Node(Move.SOLAR_BEAM);
			movebank[54] = new Node(Move.BLIZZARD);
			movebank[59] = new Node(Move.SHADOW_BALL);
			break;
		case 80:
			movebank = new Node[39];
			movebank[0] = new Node(Move.VINE_WHIP);
			movebank[2] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.LEER);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[11] = new Node(Move.DETECT);
			movebank[15] = new Node(Move.LEECH_SEED);
			movebank[18] = new Node(Move.MAGICAL_LEAF);
			movebank[23] = new Node(Move.LEAF_TORNADO);
			movebank[28] = new Node(Move.PSYBEAM);
			movebank[33] = new Node(Move.LIGHT_SCREEN);
			movebank[38] = new Node(Move.LEAF_STORM);
			break;
		case 81:
			movebank = new Node[70];
			movebank[0] = new Node(Move.VINE_WHIP);
			movebank[2] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.LEER);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[11] = new Node(Move.DETECT);
			movebank[15] = new Node(Move.LEECH_SEED);
			movebank[18] = new Node(Move.MAGICAL_LEAF);
			movebank[23] = new Node(Move.LEAF_TORNADO);
			movebank[28] = new Node(Move.PSYBEAM);
			movebank[33] = new Node(Move.LIGHT_SCREEN);
			movebank[38] = new Node(Move.LEAF_STORM);
			movebank[39] = new Node(Move.PSYCHIC);
			movebank[42] = new Node(Move.REFLECT);
			movebank[44] = new Node(Move.GRASS_WHISTLE);
			movebank[49] = new Node(Move.PSYCHIC_TERRAIN);
			movebank[53] = new Node(Move.ENERGY_BALL);
			movebank[58] = new Node(Move.TWINKLE_TACKLE);
			movebank[62] = new Node(Move.SPIRIT_BREAK);
			movebank[69] = new Node(Move.PETAL_DANCE);
			break;
		case 82:
			movebank = new Node[25];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[4] = new Node(Move.GLARE);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[24] = new Node(Move.DREAM_EATER);
			break;
		case 83:
			movebank = new Node[50];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[4] = new Node(Move.GLARE);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[24] = new Node(Move.DREAM_EATER);
			movebank[31] = new Node(Move.DOUBLE_HIT);
			movebank[34] = new Node(Move.MAGNET_BOMB);
			movebank[38] = new Node(Move.IRON_DEFENSE);
			movebank[41] = new Node(Move.ZEN_HEADBUTT);
			movebank[46] = new Node(Move.EXTRASENSORY);
			movebank[49] = new Node(Move.BULLET_PUNCH);
			break;
		case 84:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[4] = new Node(Move.GLARE);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[24] = new Node(Move.DREAM_EATER);
			movebank[31] = new Node(Move.DOUBLE_HIT);
			movebank[34] = new Node(Move.MAGNET_BOMB);
			movebank[38] = new Node(Move.IRON_DEFENSE);
			movebank[41] = new Node(Move.ZEN_HEADBUTT);
			movebank[46] = new Node(Move.EXTRASENSORY);
			movebank[49] = new Node(Move.BULLET_PUNCH);
			movebank[51] = new Node(Move.HAMMER_ARM);
			movebank[56] = new Node(Move.DRILL_RUN);
			movebank[60] = new Node(Move.PSYCHO_CUT);
			movebank[66] = new Node(Move.SKULL_BASH);
			movebank[69] = new Node(Move.EXPLOSION);
			break;
		case 85:
			movebank = new Node[18];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[0].next = new Node(Move.GROWL);
			movebank[0].next.next = new Node(Move.FLASH);
			movebank[2] = new Node(Move.DOUBLE_TEAM);
			movebank[5] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.HYPNOSIS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.DRAINING_KISS);
			movebank[17] = new Node(Move.PSYBEAM);
			break;
		case 86:
			movebank = new Node[28];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[0].next = new Node(Move.GROWL);
			movebank[0].next.next = new Node(Move.FLASH);
			movebank[2] = new Node(Move.DOUBLE_TEAM);
			movebank[5] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.HYPNOSIS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.DRAINING_KISS);
			movebank[17] = new Node(Move.PSYBEAM);
			movebank[22] = new Node(Move.LIFE_DEW);
			movebank[27] = new Node(Move.CHARM);
			break;
		case 87:
			movebank = new Node[65];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[0].next = new Node(Move.GROWL);
			movebank[0].next.next = new Node(Move.FLASH);
			movebank[2] = new Node(Move.DOUBLE_TEAM);
			movebank[5] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.HYPNOSIS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.DRAINING_KISS);
			movebank[17] = new Node(Move.PSYBEAM);
			movebank[22] = new Node(Move.LIFE_DEW);
			movebank[27] = new Node(Move.CHARM);
			movebank[29] = new Node(Move.DAZZLING_GLEAM);
			movebank[34] = new Node(Move.CALM_MIND);
			movebank[41] = new Node(Move.PSYCHIC);
			movebank[44] = new Node(Move.HEAL_PULSE);
			movebank[48] = new Node(Move.DREAM_EATER);
			movebank[54] = new Node(Move.MOONBLAST);
			movebank[59] = new Node(Move.FUTURE_SIGHT);
			movebank[64] = new Node(Move.BLACK_HOLE_ECLIPSE);
			break;
		case 88:
			movebank = new Node[65];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.NIGHT_SLASH));
			movebank[0].addToEnd(new Node(Move.PSYCHIC));
			movebank[0].addToEnd(new Node(Move.CALM_MIND));
			movebank[0].addToEnd(new Node(Move.FURY_CUTTER));
			movebank[2] = new Node(Move.DOUBLE_TEAM);
			movebank[5] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.HYPNOSIS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.DRAINING_KISS);
			movebank[17] = new Node(Move.PSYBEAM);
			movebank[19] = new Node(Move.AERIAL_ACE);
			movebank[22] = new Node(Move.FALSE_SWIPE);
			movebank[27] = new Node(Move.PROTECT);
			movebank[29] = new Node(Move.SLASH);
			movebank[34] = new Node(Move.SWORDS_DANCE);
			movebank[41] = new Node(Move.PSYCHO_CUT);
			movebank[48] = new Node(Move.HEAL_PULSE);
			movebank[55] = new Node(Move.SOLAR_BLADE);
			movebank[62] = new Node(Move.CLOSE_COMBAT);
			break;
		case 89:
			movebank = new Node[65];
			movebank[0] = new Node(Move.CHARGE_BEAM);
			movebank[0].addToEnd(new Node(Move.FORESIGHT));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.TAIL_GLOW));
			movebank[9] = new Node(Move.LIGHT_BEAM);
			movebank[14] = new Node(Move.FLASH_CANNON);
			movebank[19] = new Node(Move.THUNDERBOLT);
			movebank[24] = new Node(Move.CONFUSE_RAY);
			movebank[29] = new Node(Move.PLAY_ROUGH);
			movebank[34] = new Node(Move.GLITZY_GLOW);
			movebank[39] = new Node(Move.VOLT_TACKLE);
			movebank[44] = new Node(Move.MINIMIZE);
			movebank[49] = new Node(Move.HYPER_BEAM);
			movebank[54] = new Node(Move.THUNDER);
			movebank[59] = new Node(Move.TRI_ATTACK);
			movebank[64] = new Node(Move.MOONBLAST);
			break;
		case 90:
			movebank = new Node[27];
			movebank[0] = new Node(Move.PECK);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.HYPNOSIS);
			movebank[5] = new Node(Move.WRAP);
			movebank[8] = new Node(Move.PAYBACK);
			movebank[11] = new Node(Move.PLUCK);
			movebank[14] = new Node(Move.PSYBEAM);
			movebank[17] = new Node(Move.SWAGGER);
			movebank[20] = new Node(Move.SLASH);
			movebank[23] = new Node(Move.NIGHT_SLASH);
			movebank[26] = new Node(Move.PSYCHO_CUT);
			break;
		case 91:
			movebank = new Node[47];
			movebank[0] = new Node(Move.REVERSAL);
			movebank[0].next = new Node(Move.PECK);
			movebank[0].next.next = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.HYPNOSIS);
			movebank[5] = new Node(Move.WRAP);
			movebank[8] = new Node(Move.PAYBACK);
			movebank[11] = new Node(Move.PLUCK);
			movebank[14] = new Node(Move.PSYBEAM);
			movebank[17] = new Node(Move.SWAGGER);
			movebank[20] = new Node(Move.SLASH);
			movebank[23] = new Node(Move.NIGHT_SLASH);
			movebank[26] = new Node(Move.PSYCHO_CUT);
			movebank[32] = new Node(Move.THROAT_CHOP);
			movebank[36] = new Node(Move.FOUL_PLAY);
			movebank[41] = new Node(Move.TOPSY_TURVY);
			movebank[46] = new Node(Move.SUPERPOWER);
			break;
		case 92:
			movebank = new Node[31];
			movebank[0] = new Node(Move.EMBER);
			movebank[2] = new Node(Move.TACKLE);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[8] = new Node(Move.HEADBUTT);
			movebank[10] = new Node(Move.WILL_O_WISP);
			movebank[14] = new Node(Move.BITE);
			movebank[16] = new Node(Move.FLAME_WHEEL);
			movebank[19] = new Node(Move.BODY_SLAM);
			movebank[22] = new Node(Move.METAL_CLAW);
			movebank[24] = new Node(Move.FIRE_FANG);
			movebank[30] = new Node(Move.FLAMETHROWER);
			break;
		case 93:
			movebank = new Node[65];
			movebank[0] = new Node(Move.EMBER);
			movebank[2] = new Node(Move.TACKLE);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[8] = new Node(Move.HEADBUTT);
			movebank[10] = new Node(Move.WILL_O_WISP);
			movebank[14] = new Node(Move.BITE);
			movebank[16] = new Node(Move.FLAME_WHEEL);
			movebank[19] = new Node(Move.BODY_SLAM);
			movebank[22] = new Node(Move.METAL_CLAW);
			movebank[24] = new Node(Move.FIRE_FANG);
			movebank[30] = new Node(Move.FLAMETHROWER);
			movebank[35] = new Node(Move.FLASH_CANNON);
			movebank[39] = new Node(Move.VOLT_TACKLE);
			movebank[43] = new Node(Move.FLARE_BLITZ);
			movebank[49] = new Node(Move.AGILITY);
			movebank[54] = new Node(Move.SUNNY_DAY);
			movebank[59] = new Node(Move.MORNING_SUN);
			movebank[64] = new Node(Move.GIGA_IMPACT);
			break;
		case 94:
			movebank = new Node[15];
			movebank[0] = new Node(Move.WILL_O_WISP);
			movebank[14] = new Node(Move.EMBER);
			break;
		case 95:
			movebank = new Node[32];
			movebank[0] = new Node(Move.WILL_O_WISP);
			movebank[14] = new Node(Move.EMBER);
			movebank[15] = new Node(Move.FLAME_WHEEL);
			movebank[20] = new Node(Move.FURY_SWIPES);
			movebank[25] = new Node(Move.MACH_PUNCH);
			movebank[28] = new Node(Move.BRICK_BREAK);
			movebank[31] = new Node(Move.BLAZE_KICK);
			break;
		case 96:
			movebank = new Node[60];
			movebank[0] = new Node(Move.WILL_O_WISP);
			movebank[14] = new Node(Move.EMBER);
			movebank[15] = new Node(Move.FLAME_WHEEL);
			movebank[20] = new Node(Move.FURY_SWIPES);
			movebank[25] = new Node(Move.MACH_PUNCH);
			movebank[28] = new Node(Move.BRICK_BREAK);
			movebank[31] = new Node(Move.BLAZE_KICK);
			movebank[35] = new Node(Move.FIRE_FANG);
			movebank[39] = new Node(Move.WAKE_UP_SLAP);
			movebank[44] = new Node(Move.CROSS_POISON);
			movebank[48] = new Node(Move.CLOSE_COMBAT);
			movebank[54] = new Node(Move.EARTHQUAKE);
			movebank[59] = new Node(Move.FLARE_BLITZ);
			break;
		case 97:
			movebank = new Node[65];
			movebank[0] = new Node(Move.EMBER);
			movebank[4] = new Node(Move.METAL_SOUND);
			movebank[9] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.FLAMETHROWER);
			movebank[19] = new Node(Move.IRON_BLAST);
			movebank[24] = new Node(Move.WILL_O_WISP);
			movebank[29] = new Node(Move.EARTH_POWER);
			movebank[34] = new Node(Move.HEX);
			movebank[39] = new Node(Move.AUTOMOTIZE);
			movebank[44] = new Node(Move.GYRO_BALL);
			movebank[49] = new Node(Move.EXPLOSION);
			movebank[54] = new Node(Move.SHADOW_BALL);
			movebank[59] = new Node(Move.STEEL_BEAM);
			movebank[64] = new Node(Move.FIRE_BLAST);
			break;
		case 98:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.EMBER);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.MUD_SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL_O_WISP);
			movebank[32] = new Node(Move.DETECT);
			break;
		case 99:
			movebank = new Node[53];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.EMBER);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.MUD_SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL_O_WISP);
			movebank[32] = new Node(Move.DETECT);
			movebank[34] = new Node(Move.WING_ATTACK);
			movebank[37] = new Node(Move.FIRE_FANG);
			movebank[41] = new Node(Move.AIR_SLASH);
			movebank[44] = new Node(Move.DRAGON_BREATH);
			movebank[49] = new Node(Move.FLAMETHROWER);
			movebank[52] = new Node(Move.DRAGON_PULSE);
			break;
		case 100:
			movebank = new Node[85];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.EMBER);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.MUD_SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL_O_WISP);
			movebank[32] = new Node(Move.DETECT);
			movebank[34] = new Node(Move.WING_ATTACK);
			movebank[37] = new Node(Move.FIRE_FANG);
			movebank[41] = new Node(Move.AIR_SLASH);
			movebank[44] = new Node(Move.DRAGON_BREATH);
			movebank[49] = new Node(Move.FLAMETHROWER);
			movebank[52] = new Node(Move.DRAGON_PULSE);
			movebank[54] = new Node(Move.IRON_TAIL);
			movebank[57] = new Node(Move.DRAGON_RUSH);
			movebank[62] = new Node(Move.FIRE_BLAST);
			movebank[69] = new Node(Move.ERUPTION);
			movebank[74] = new Node(Move.DRACO_METEOR);
			movebank[84] = new Node(Move.HYPER_BEAM);
			break;
		case 101:
			movebank = new Node[15];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[5] = new Node(Move.WILL_O_WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			break;
		case 102:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[5] = new Node(Move.WILL_O_WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			movebank[14].next = new Node(Move.ROCK_POLISH);
			movebank[16] = new Node(Move.SCREECH);
			movebank[19] = new Node(Move.RECOVER);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.PROTECT);
			movebank[27] = new Node(Move.BURN_UP);
			movebank[29] = new Node(Move.ROCK_BLAST);
			movebank[32] = new Node(Move.LAVA_PLUME);
			break;
		case 103:
			movebank = new Node[65];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[5] = new Node(Move.WILL_O_WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			movebank[14].next = new Node(Move.ROCK_POLISH);
			movebank[16] = new Node(Move.SCREECH);
			movebank[19] = new Node(Move.RECOVER);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.PROTECT);
			movebank[27] = new Node(Move.BURN_UP);
			movebank[29] = new Node(Move.ROCK_BLAST);
			movebank[32] = new Node(Move.LAVA_PLUME);
			movebank[34] = new Node(Move.FIRE_SPIN);
			movebank[38] = new Node(Move.INFERNO);
			movebank[41] = new Node(Move.MOLTEN_CONSUME);
			movebank[44] = new Node(Move.OVERHEAT);
			movebank[48] = new Node(Move.EARTH_POWER);
			movebank[51] = new Node(Move.MOLTEN_LAIR);
			movebank[56] = new Node(Move.POWER_GEM);
			movebank[61] = new Node(Move.ERUPTION);
			movebank[64] = new Node(Move.HYPER_BEAM);
			break;
		case 104:
			movebank = new Node[20];
			movebank[0] = new Node(Move.LEER);
			movebank[0].next = new Node(Move.EMBER);
			movebank[3] = new Node(Move.HOWL);
			movebank[7] = new Node(Move.SMOG);
			movebank[12] = new Node(Move.ROAR);
			movebank[15] = new Node(Move.BITE);
			movebank[19] = new Node(Move.ODOR_SLEUTH);
			break;
		case 105:
			movebank = new Node[65];
			movebank[0] = new Node(Move.LEER);
			movebank[0].next = new Node(Move.EMBER);
			movebank[3] = new Node(Move.HOWL);
			movebank[7] = new Node(Move.SMOG);
			movebank[12] = new Node(Move.ROAR);
			movebank[15] = new Node(Move.BITE);
			movebank[19] = new Node(Move.ODOR_SLEUTH);
			movebank[25] = new Node(Move.BEAT_UP);
			movebank[29] = new Node(Move.FIRE_FANG);
			movebank[34] = new Node(Move.FEINT_ATTACK);
			movebank[40] = new Node(Move.INCINERATE);
			movebank[44] = new Node(Move.FOUL_PLAY);
			movebank[49] = new Node(Move.FLAMETHROWER);
			movebank[55] = new Node(Move.CRUNCH);
			movebank[59] = new Node(Move.NASTY_PLOT);
			movebank[64] = new Node(Move.INFERNO);
			break;
		case 106:
			movebank = new Node[24];
			movebank[0] = new Node(Move.DOUBLE_TEAM);
			movebank[0].addToEnd(new Node(Move.MAGIC_POWDER));
			movebank[0].addToEnd(new Node(Move.EMBER));
			movebank[0].addToEnd(new Node(Move.WILL_O_WISP));
			movebank[4] = new Node(Move.SMOKESCREEN);
			movebank[7] = new Node(Move.CONFUSION);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[13] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.SWIFT);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			break;
		case 107:
			movebank = new Node[70];
			movebank[0] = new Node(Move.DOUBLE_TEAM);
			movebank[0].addToEnd(new Node(Move.MAGIC_POWDER));
			movebank[0].addToEnd(new Node(Move.EMBER));
			movebank[0].addToEnd(new Node(Move.WILL_O_WISP));
			movebank[4] = new Node(Move.SMOKESCREEN);
			movebank[7] = new Node(Move.CONFUSION);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[13] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.SWIFT);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.PSYBEAM);
			movebank[28] = new Node(Move.FLAMETHROWER);
			movebank[34] = new Node(Move.MAGIC_BLAST);
			movebank[39] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[42] = new Node(Move.TRI_ATTACK);
			movebank[44] = new Node(Move.SPARKLING_TERRAIN);
			movebank[49] = new Node(Move.FIRE_BLAST);
			movebank[53] = new Node(Move.MAGIC_REFLECT);
			movebank[59] = new Node(Move.SPACIAL_REND);
			movebank[64] = new Node(Move.HYPER_BEAM);
			movebank[69] = new Node(Move.BLUE_FLARE);
			break;
		case 108:
			movebank = new Node[35];
			movebank[0] = new Node(Move.GROWL);
			movebank[2] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.FLASH);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.QUICK_ATTACK);
			movebank[24] = new Node(Move.DRAINING_KISS);
			movebank[34] = new Node(Move.GLITZY_GLOW);
			break;
		case 109:
			movebank = new Node[55];
			movebank[0] = new Node(Move.FLAME_CHARGE);
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[0].addToEnd(new Node(Move.ROAR));
			movebank[0].addToEnd(new Node(Move.PLAY_NICE));
			movebank[4] = new Node(Move.HOWL);
			movebank[9] = new Node(Move.TAKE_DOWN);
			movebank[14] = new Node(Move.HYPER_FANG);
			movebank[19] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.AGILITY);
			movebank[29] = new Node(Move.PLAY_ROUGH);
			movebank[31] = new Node(Move.FIRE_FANG);
			movebank[36] = new Node(Move.HEAT_CRASH);
			movebank[39] = new Node(Move.DRILL_RUN);
			movebank[43] = new Node(Move.SWORDS_DANCE);
			movebank[49] = new Node(Move.SUNSTEEL_STRIKE);
			movebank[54] = new Node(Move.FLARE_BLITZ);
			break;
		case 110:
			movebank = new Node[55];
			movebank[0] = new Node(Move.CHARM);
			movebank[0].addToEnd(new Node(Move.SLAM));
			movebank[0].addToEnd(new Node(Move.ROAR));
			movebank[0].addToEnd(new Node(Move.PLAY_NICE));
			movebank[4] = new Node(Move.CONFUSE_RAY);
			movebank[9] = new Node(Move.MYSTICAL_FIRE);
			movebank[14] = new Node(Move.LIGHT_BEAM);
			movebank[19] = new Node(Move.FIRE_SPIN);
			movebank[24] = new Node(Move.AGILITY);
			movebank[29] = new Node(Move.PLAY_ROUGH);
			movebank[31] = new Node(Move.FLAMETHROWER);
			movebank[36] = new Node(Move.GLITZY_GLOW);
			movebank[39] = new Node(Move.EARTH_POWER);
			movebank[43] = new Node(Move.GLITTER_DANCE);
			movebank[49] = new Node(Move.MOONBLAST);
			movebank[54] = new Node(Move.OVERHEAT);
			break;
		case 111:
			movebank = new Node[25];
			movebank[0] = new Node(Move.THUNDERSHOCK);
			movebank[3] = new Node(Move.CHARGE);
			movebank[7] = new Node(Move.WRAP);
			movebank[12] = new Node(Move.THUNDER_WAVE);
			movebank[16] = new Node(Move.SLAM);
			movebank[24] = new Node(Move.SPARK);
			break;
		case 112:
			movebank = new Node[60];
			movebank[0] = new Node(Move.THUNDERSHOCK);
			movebank[3] = new Node(Move.CHARGE);
			movebank[7] = new Node(Move.WRAP);
			movebank[12] = new Node(Move.THUNDER_WAVE);
			movebank[16] = new Node(Move.SLAM);
			movebank[24] = new Node(Move.SPARK);
			movebank[29] = new Node(Move.DOUBLE_HIT);
			movebank[33] = new Node(Move.GYRO_BALL);
			movebank[36] = new Node(Move.IRON_DEFENSE);
			movebank[43] = new Node(Move.CURSE);
			movebank[46] = new Node(Move.MAGNET_RISE);
			movebank[49] = new Node(Move.THUNDER_PUNCH);
			movebank[54] = new Node(Move.BULLET_PUNCH);
			movebank[59] = new Node(Move.VOLT_TACKLE);
			break;
		case 113:
			movebank = new Node[60];
			movebank[0] = new Node(Move.THUNDER_PUNCH);
			movebank[0].addToEnd(new Node(Move.CLOSE_COMBAT));
			movebank[0].addToEnd(new Node(Move.TAKE_DOWN));
			movebank[0] = new Node(Move.GYRO_BALL);
			movebank[4] = new Node(Move.IRON_DEFENSE);
			movebank[8] = new Node(Move.NIGHT_SLASH);
			movebank[14] = new Node(Move.THUNDERBOLT);
			movebank[24] = new Node(Move.SUPERPOWER);
			movebank[31] = new Node(Move.VOLT_TACKLE);
			movebank[34] = new Node(Move.AUTOMOTIZE);
			break;
		case 114:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 115:
			movebank = new Node[47];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			movebank[27] = new Node(Move.MAGNITUDE);
			movebank[31] = new Node(Move.SPARK);
			movebank[36] = new Node(Move.TWINKLE_TACKLE);
			movebank[40] = new Node(Move.ELECTRIC_TERRAIN);
			movebank[46] = new Node(Move.COTTON_GUARD);
			break;
		case 116:
			movebank = new Node[65];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			movebank[27] = new Node(Move.MAGNITUDE);
			movebank[31] = new Node(Move.SPARK);
			movebank[36] = new Node(Move.TWINKLE_TACKLE);
			movebank[40] = new Node(Move.ELECTRIC_TERRAIN);
			movebank[46] = new Node(Move.COTTON_GUARD);
			movebank[47] = new Node(Move.VOLT_TACKLE);
			movebank[50] = new Node(Move.EXTREME_SPEED);
			movebank[54] = new Node(Move.SKULL_BASH);
			movebank[59] = new Node(Move.EARTHQUAKE);
			movebank[64] = new Node(Move.MAGNET_RISE);
			break;
		case 117:
			movebank = new Node[30];
			movebank[0] = new Node(Move.BRANCH_POKE);
			movebank[0].next = new Node(Move.THUNDERSHOCK);
			movebank[19] = new Node(Move.STRENGTH_SAP);
			movebank[29] = new Node(Move.GRASS_KNOT);
			break;
		case 118:
			movebank = new Node[30];
			movebank[0] = new Node(Move.BRANCH_POKE);
			movebank[0].next = new Node(Move.THUNDERSHOCK);
			movebank[0].next.next = new Node(Move.FORESTS_CURSE);
			movebank[9] = new Node(Move.GRASSY_TERRAIN);
			movebank[9].next = new Node(Move.ELECTRIC_TERRAIN);
			movebank[14] = new Node(Move.LEECH_SEED);
			movebank[24] = new Node(Move.NEEDLE_ARM);
			movebank[26] = new Node(Move.THUNDER_WAVE);
			movebank[29] = new Node(Move.THUNDER_PUNCH);
			break;
		case 119:
			movebank = new Node[50];
			movebank[0] = new Node(Move.BRANCH_POKE);
			movebank[0].addToEnd(new Node(Move.THUNDERSHOCK));
			movebank[0].addToEnd(new Node(Move.INGRAIN));
			movebank[0].addToEnd(new Node(Move.SLEEP_POWDER));
			movebank[0].addToEnd(new Node(Move.STUN_SPORE));
			movebank[0].addToEnd(new Node(Move.FORESTS_CURSE));
			movebank[0].addToEnd(new Node(Move.GRASS_KNOT));
			movebank[9] = new Node(Move.HORN_LEECH);
			movebank[14] = new Node(Move.LEAF_TORNADO);
			movebank[19] = new Node(Move.PARABOLIC_CHARGE);
			movebank[24] = new Node(Move.GIGA_DRAIN);
			movebank[29] = new Node(Move.THUNDERBOLT);
			movebank[34] = new Node(Move.AROMATHERAPY);
			movebank[39] = new Node(Move.THUNDER);
			movebank[44] = new Node(Move.LEAF_STORM);
			movebank[49] = new Node(Move.NASTY_PLOT);
			break;
		case 120:
			movebank = new Node[20];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.QUICK_ATTACK);
			movebank[9] = new Node(Move.GROWL);
			movebank[11] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.GYRO_BALL);
			movebank[19] = new Node(Move.METAL_SOUND);
			break;
		case 121:
			movebank = new Node[55];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.SPIKY_SHIELD));
			movebank[9] = new Node(Move.METAL_CLAW);
			movebank[14] = new Node(Move.SNARL);
			movebank[18] = new Node(Move.SLAM);
			movebank[19] = new Node(Move.FLASH);
			movebank[24] = new Node(Move.ROLLOUT);
			movebank[29] = new Node(Move.FLASH_CANNON);
			movebank[39] = new Node(Move.BULLET_PUNCH);
			movebank[49] = new Node(Move.HONE_CLAWS);
			movebank[54] = new Node(Move.TWINKLE_TACKLE);
			break;
		case 122:
			movebank = new Node[60];
			movebank[0] = new Node(Move.FLASH_CANNON);
			movebank[0].addToEnd(new Node(Move.MAGIC_BLAST));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.SPIKY_SHIELD));
			movebank[4] = new Node(Move.METAL_CLAW);
			movebank[9] = new Node(Move.SNARL);
			movebank[14] = new Node(Move.MAGIC_FANG);
			movebank[19] = new Node(Move.TAKE_DOWN);
			movebank[24] = new Node(Move.IRON_HEAD);
			movebank[29] = new Node(Move.IRON_BLAST);
			movebank[34] = new Node(Move.GIGA_IMPACT);
			movebank[39] = new Node(Move.SPARKLE_STRIKE);
			movebank[44] = new Node(Move.HONE_CLAWS);
			movebank[49] = new Node(Move.HEAVY_SLAM);
			movebank[54] = new Node(Move.MAGIC_CRASH);
			movebank[59] = new Node(Move.STEEL_BEAM);
			break;
		case 123:
			movebank = new Node[15];
			movebank[0] = new Node(Move.NUZZLE);
			movebank[4] = new Node(Move.QUICK_ATTACK);
			movebank[5] = new Node(Move.TAIL_WHIP);
			movebank[7] = new Node(Move.CHARGE);
			movebank[9] = new Node(Move.SPARK);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			break;
		case 124:
			movebank = new Node[60];
			movebank[0] = new Node(Move.HOWL);
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[2] = new Node(Move.QUICK_ATTACK);
			movebank[4] = new Node(Move.TAIL_WHIP);
			movebank[8] = new Node(Move.SPARK);
			movebank[11] = new Node(Move.THUNDER_WAVE);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			movebank[17] = new Node(Move.NUZZLE);
			movebank[20] = new Node(Move.ROAR);
			movebank[24] = new Node(Move.WHIP_SMASH);
			movebank[28] = new Node(Move.THUNDER_PUNCH);
			movebank[32] = new Node(Move.PLAY_ROUGH);
			movebank[36] = new Node(Move.ZEN_HEADBUTT);
			movebank[42] = new Node(Move.TWINKLE_TACKLE);
			movebank[47] = new Node(Move.SPARKLE_STRIKE);
			movebank[54] = new Node(Move.VOLT_TACKLE);
			movebank[59] = new Node(Move.PLASMA_FISTS);
			break;
		case 125:
			movebank = new Node[60];
			movebank[0] = new Node(Move.VOLT_TACKLE);
			movebank[0].addToEnd(new Node(Move.BOUNCE));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[4] = new Node(Move.EXTREME_SPEED);
			movebank[9] = new Node(Move.ACROBATICS);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			movebank[19] = new Node(Move.SKY_ATTACK);
			movebank[24] = new Node(Move.WHIP_SMASH);
			movebank[29] = new Node(Move.THUNDER_PUNCH);
			movebank[34] = new Node(Move.GIGA_IMPACT);
			movebank[39] = new Node(Move.ZEN_HEADBUTT);
			movebank[44] = new Node(Move.TWINKLE_TACKLE);
			movebank[49] = new Node(Move.SPARKLE_STRIKE);
			movebank[54] = new Node(Move.MAGIC_CRASH);
			movebank[59] = new Node(Move.PLASMA_FISTS);
			break;
		case 126:
			movebank = new Node[15];
			movebank[0] = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.QUICK_ATTACK);
			movebank[5] = new Node(Move.TAIL_WHIP);
			movebank[7] = new Node(Move.CHARM);
			movebank[9] = new Node(Move.FURY_SWIPES);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			break;
		case 127:
			movebank = new Node[60];
			movebank[0] = new Node(Move.GROWL);
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[4] = new Node(Move.TAIL_WHIP);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[12] = new Node(Move.MACH_PUNCH);
			movebank[16] = new Node(Move.TAKE_DOWN);
			movebank[19] = new Node(Move.DOUBLE_KICK);
			movebank[22] = new Node(Move.ROAR);
			movebank[24] = new Node(Move.BEEFY_BASH);
			movebank[27] = new Node(Move.CRUNCH);
			movebank[30] = new Node(Move.BULK_UP);
			movebank[34] = new Node(Move.SEISMIC_TOSS);
			movebank[37] = new Node(Move.HAMMER_ARM);
			movebank[41] = new Node(Move.ICE_PUNCH);
			movebank[47] = new Node(Move.TWINKLE_TACKLE);
			movebank[54] = new Node(Move.SKY_UPPERCUT);
			movebank[59] = new Node(Move.PLASMA_FISTS);
			break;
		case 128:
			movebank = new Node[60];
			movebank[0] = new Node(Move.HAMMER_ARM);
			movebank[0].addToEnd(new Node(Move.PLAY_ROUGH));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.BULK_UP));
			movebank[4] = new Node(Move.DYNAMIC_PUNCH);
			movebank[9] = new Node(Move.CIRCLE_THROW);
			movebank[14] = new Node(Move.POWER_UP_PUNCH);
			movebank[19] = new Node(Move.SKY_UPPERCUT);
			movebank[24] = new Node(Move.BEEFY_BASH);
			movebank[29] = new Node(Move.AURA_SPHERE);
			movebank[34] = new Node(Move.SUPERPOWER);
			movebank[39] = new Node(Move.CALM_MIND);
			movebank[44] = new Node(Move.TWINKLE_TACKLE);
			movebank[49] = new Node(Move.SPARKLE_STRIKE);
			movebank[54] = new Node(Move.MAGIC_TOMB);
			movebank[59] = new Node(Move.PLASMA_FISTS);
			break;
		case 129:
			movebank = new Node[30];
			movebank[0] = new Node(Move.SAND_ATTACK);
			movebank[0].next = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.HARDEN);
			movebank[9] = new Node(Move.FALSE_SWIPE);
			movebank[14] = new Node(Move.MUD_SLAP);
			movebank[20] = new Node(Move.ABSORB);
			movebank[24] = new Node(Move.METAL_CLAW);
			movebank[29] = new Node(Move.DIG);
			break;
		case 130:
			movebank = new Node[57];
			movebank[0] = new Node(Move.DOUBLE_TEAM);
			movebank[0].addToEnd(new Node(Move.SCREECH));
			movebank[0].addToEnd(new Node(Move.BATON_PASS));
			movebank[0].addToEnd(new Node(Move.AERIAL_ACE));
			movebank[0].addToEnd(new Node(Move.METAL_CLAW));
			movebank[0].addToEnd(new Node(Move.SAND_ATTACK));
			movebank[0].addToEnd(new Node(Move.SCRATCH));
			movebank[4] = new Node(Move.HARDEN);
			movebank[9] = new Node(Move.FALSE_SWIPE);
			movebank[14] = new Node(Move.MUD_SLAP);
			movebank[20] = new Node(Move.FURY_CUTTER);
			movebank[22] = new Node(Move.ABSORB);
			movebank[28] = new Node(Move.BUG_BITE);
			movebank[35] = new Node(Move.FURY_SWIPES);
			movebank[42] = new Node(Move.LEECH_LIFE);
			movebank[49] = new Node(Move.SLASH);
			movebank[56] = new Node(Move.SWORDS_DANCE);
			break;
		case 131:
			movebank = new Node[57];
			movebank[0] = new Node(Move.SHADOW_CLAW);
			movebank[0].addToEnd(new Node(Move.METAL_CLAW));
			movebank[0].addToEnd(new Node(Move.SAND_ATTACK));
			movebank[0].addToEnd(new Node(Move.SCRATCH));
			movebank[4] = new Node(Move.HARDEN);
			movebank[9] = new Node(Move.FALSE_SWIPE);
			movebank[14] = new Node(Move.MUD_SLAP);
			movebank[22] = new Node(Move.ABSORB);
			movebank[28] = new Node(Move.SHADOW_SNEAK);
			movebank[35] = new Node(Move.FURY_SWIPES);
			movebank[42] = new Node(Move.LEECH_LIFE);
			movebank[49] = new Node(Move.PHANTOM_FORCE);
			movebank[56] = new Node(Move.SWORDS_DANCE);
			break;
		case 132:
			movebank = new Node[35];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[4] = new Node(Move.PROTECT);
			movebank[7] = new Node(Move.BUBBLE);
			movebank[10] = new Node(Move.RAPID_SPIN);
			movebank[12] = new Node(Move.ASTONISH);
			movebank[15] = new Node(Move.AQUA_RING);
			movebank[19] = new Node(Move.NIGHT_SHADE);
			movebank[24] = new Node(Move.WATER_PULSE);
			movebank[34] = new Node(Move.SHELL_SMASH);
			break;
		case 133:
			movebank = new Node[55];
			movebank[0] = new Node(Move.GUST);
			movebank[0].addToEnd(new Node(Move.LEAF_TORNADO));
			movebank[0].addToEnd(new Node(Move.AQUA_JET));
			movebank[0].addToEnd(new Node(Move.SHADOW_SNEAK));
			movebank[4] = new Node(Move.WHIRLPOOL);
			movebank[7] = new Node(Move.RAIN_DANCE);
			movebank[9] = new Node(Move.AURA_SPHERE);
			movebank[13] = new Node(Move.HEX);
			movebank[16] = new Node(Move.BRINE);
			movebank[19] = new Node(Move.VACUUM_WAVE);
			movebank[24] = new Node(Move.SHADOW_BALL);
			movebank[28] = new Node(Move.GLITTERING_TORNADO);
			movebank[33] = new Node(Move.SPARKLING_ARIA);
			movebank[39] = new Node(Move.LIQUIDATION);
			movebank[44] = new Node(Move.AEROBLAST);
			movebank[49] = new Node(Move.FOCUS_BLAST);
			movebank[54] = new Node(Move.HURRICANE);
			break;
		case 134:
			movebank = new Node[15];
			movebank[0] = new Node(Move.RAPID_SPIN);
			movebank[0].next = new Node(Move.LEER);
			movebank[0].next.next = new Node(Move.WATER_GUN);
			movebank[14] = new Node(Move.DIVE);
			break;
		case 135:
			movebank = new Node[26];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.LEER);
			movebank[6] = new Node(Move.DOUBLE_SLAP);
			movebank[9] = new Node(Move.WATER_GUN);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[14] = new Node(Move.SUPERSONIC);
			movebank[18] = new Node(Move.HEADBUTT);
			movebank[22] = new Node(Move.WING_ATTACK);
			movebank[25] = new Node(Move.WATER_PULSE);
			break;
		case 136:
			movebank = new Node[60];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.LEER);
			movebank[6] = new Node(Move.DOUBLE_SLAP);
			movebank[9] = new Node(Move.WATER_GUN);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[14] = new Node(Move.SUPERSONIC);
			movebank[18] = new Node(Move.HEADBUTT);
			movebank[22] = new Node(Move.WING_ATTACK);
			movebank[25] = new Node(Move.WATER_PULSE);
			movebank[26] = new Node(Move.TWISTER);
			movebank[28] = new Node(Move.GUST);
			movebank[31] = new Node(Move.GLITTERING_TORNADO);
			movebank[35] = new Node(Move.SCREECH);
			movebank[39] = new Node(Move.LEAF_TORNADO);
			movebank[44] = new Node(Move.HURRICANE);
			movebank[48] = new Node(Move.AGILITY);
			movebank[52] = new Node(Move.WAVE_CRASH);
			movebank[59] = new Node(Move.OUTRAGE);
			break;
		case 137:
			movebank = new Node[25];
			movebank[0] = new Node(Move.SPLASH);
			movebank[14] = new Node(Move.TACKLE);
			movebank[24] = new Node(Move.FLAIL);
			break;
		case 138:
			movebank = new Node[52];
			movebank[0] = new Node(Move.SPLASH);
			movebank[14] = new Node(Move.TACKLE);
			movebank[19] = new Node(Move.BITE);
			movebank[22] = new Node(Move.ICE_FANG);
			movebank[25] = new Node(Move.CRUNCH);
			movebank[27] = new Node(Move.RAIN_DANCE);
			movebank[31] = new Node(Move.AQUA_TAIL);
			movebank[35] = new Node(Move.DRAGON_DANCE);
			movebank[39] = new Node(Move.WATERFALL);
			movebank[43] = new Node(Move.HURRICANE);
			movebank[47] = new Node(Move.THRASH);
			movebank[51] = new Node(Move.HYDRO_PUMP);
			break;
		case 139:
			movebank = new Node[45];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[3] = new Node(Move.FLASH);
			movebank[6] = new Node(Move.PSYWAVE);
			movebank[9] = new Node(Move.WATER_GUN);
			movebank[12] = new Node(Move.SWIFT);
			movebank[15] = new Node(Move.SPACE_BEAM);
			movebank[19] = new Node(Move.CONFUSE_RAY);
			movebank[22] = new Node(Move.LIGHT_BEAM);
			movebank[26] = new Node(Move.BUBBLEBEAM);
			movebank[30] = new Node(Move.LIGHT_SCREEN);
			movebank[35] = new Node(Move.PSYCHIC);
			movebank[39] = new Node(Move.RECOVER);
			movebank[44] = new Node(Move.HYDRO_PUMP);
			break;
		case 140:
			movebank = new Node[50];
			movebank[0] = new Node(Move.FLASH);
			movebank[0].addToEnd(new Node(Move.FORESIGHT));
			movebank[0].addToEnd(new Node(Move.LUSTER_PURGE));
			movebank[0].addToEnd(new Node(Move.MOONLIGHT));
			movebank[0].addToEnd(new Node(Move.SWIFT));
			movebank[0].addToEnd(new Node(Move.TRI_ATTACK));
			movebank[0].addToEnd(new Node(Move.MAGIC_CRASH));
			movebank[0].addToEnd(new Node(Move.COSMIC_POWER));
			movebank[0].addToEnd(new Node(Move.GRAVITY));
			movebank[0].addToEnd(new Node(Move.STAR_STORM));
			movebank[0].addToEnd(new Node(Move.SPARKLY_SWIRL));
			movebank[0].addToEnd(new Node(Move.LIGHT_SCREEN));
			movebank[0].addToEnd(new Node(Move.PSYCHIC));
			movebank[0].addToEnd(new Node(Move.RECOVER));
			movebank[0].addToEnd(new Node(Move.HYDRO_PUMP));
			movebank[0].addToEnd(new Node(Move.MOONBLAST));
			movebank[44] = new Node(Move.CORE_ENFORCER);
			movebank[49] = new Node(Move.DESOLATE_VOID);
			break;
		case 141:
			movebank = new Node[40];
			movebank[0] = new Node(Move.BITE);
			movebank[0].next = new Node(Move.AQUA_JET);
			movebank[2] = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SCARY_FACE);
			movebank[9] = new Node(Move.SLAM);
			movebank[13] = new Node(Move.JAW_LOCK);
			movebank[16] = new Node(Move.FEINT_ATTACK);
			movebank[20] = new Node(Move.BEAT_UP);
			movebank[24] = new Node(Move.CRUNCH);
			movebank[27] = new Node(Move.LIQUIDATION);
			movebank[31] = new Node(Move.ICE_FANG);
			movebank[31].next = new Node(Move.FIRE_FANG);
			movebank[31].next.next = new Node(Move.THUNDER_FANG);
			movebank[39] = new Node(Move.PSYCHIC_FANGS);
			break;
		case 142:
			movebank = new Node[65];
			movebank[0] = new Node(Move.EXTREME_SPEED);
			movebank[0].next = new Node(Move.DRAGON_PULSE);
			movebank[4] = new Node(Move.MAGIC_FANG);
			movebank[9] = new Node(Move.FOUL_PLAY);
			movebank[14] = new Node(Move.ICE_FANG);
			movebank[14].next = new Node(Move.FIRE_FANG);
			movebank[14].next.next = new Node(Move.THUNDER_FANG);
			movebank[19] = new Node(Move.SUCKER_PUNCH);
			movebank[24] = new Node(Move.PSYCHIC_FANGS);
			movebank[29] = new Node(Move.THROAT_CHOP);
			movebank[34] = new Node(Move.SKY_ATTACK);
			movebank[39] = new Node(Move.DARKEST_LARIAT);
			movebank[44] = new Node(Move.DRAGON_CLAW);
			movebank[49] = new Node(Move.HURRICANE);
			movebank[54] = new Node(Move.DRAGON_RUSH);
			movebank[59] = new Node(Move.BRAVE_BIRD);
			movebank[64] = new Node(Move.OUTRAGE);
			break;
		case 143:
			movebank = new Node[25];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].addToEnd(new Node(Move.MAGIC_REFLECT));
			movebank[0].addToEnd(new Node(Move.SPARKLING_TERRAIN));
			movebank[0].addToEnd(new Node(Move.GLITTERING_TORNADO));
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.MAGIC_BLAST);
			movebank[11] = new Node(Move.TOPSY_TURVY);
			movebank[19] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[22] = new Node(Move.SPARKLE_STRIKE);
			movebank[24] = new Node(Move.MAGIC_CRASH);
			break;
		case 144:
			movebank = new Node[58];
			movebank[0] = new Node(Move.BITE);
			movebank[2] = new Node(Move.SLAM);
			movebank[4] = new Node(Move.RAGE);
			movebank[9] = new Node(Move.DRAGON_BREATH);
			movebank[11] = new Node(Move.BUBBLEBEAM);
			movebank[14] = new Node(Move.AQUA_JET);
			movebank[17] = new Node(Move.CRUNCH);
			movebank[22] = new Node(Move.ICE_FANG);
			movebank[22].next = new Node(Move.FIRE_FANG);
			movebank[22].next.next = new Node(Move.THUNDER_FANG);
			movebank[27] = new Node(Move.PSYCHIC_FANGS);
			movebank[29] = new Node(Move.DRAGON_TAIL);
			movebank[32] = new Node(Move.DRAGON_PULSE);
			movebank[35] = new Node(Move.LIQUIDATION);
			movebank[39] = new Node(Move.DRAGON_RUSH);
			movebank[43] = new Node(Move.MAGIC_FANG);
			movebank[47] = new Node(Move.DRAGON_DARTS);
			movebank[49] = new Node(Move.HYPER_BEAM);
			movebank[57] = new Node(Move.OUTRAGE);
			break;
		case 145:
			movebank = new Node[75];
			movebank[0] = new Node(Move.OUTRAGE);
			movebank[0].next = new Node(Move.HYPER_BEAM);
			movebank[0].next.next = new Node(Move.GLITTERING_TORNADO);
			movebank[4] = new Node(Move.DRAGON_PULSE);
			movebank[5] = new Node(Move.BITE);
			movebank[8] = new Node(Move.DRAGON_BREATH);
			movebank[10] = new Node(Move.ICE_BEAM);
			movebank[13] = new Node(Move.TRI_ATTACK);
			movebank[16] = new Node(Move.MEGAHORN);
			movebank[19] = new Node(Move.STEEL_WING);
			movebank[20] = new Node(Move.CRUNCH);
			movebank[24] = new Node(Move.PSYCHIC_FANGS);
			movebank[27] = new Node(Move.SKY_ATTACK);
			movebank[33] = new Node(Move.IRON_BLAST);
			movebank[36] = new Node(Move.SPARKLE_STRIKE);
			movebank[43] = new Node(Move.MAGIC_FANG);
			movebank[47] = new Node(Move.DRAGON_DARTS);
			movebank[53] = new Node(Move.MAGIC_CRASH);
			movebank[59] = new Node(Move.SPACIAL_REND);
			movebank[69] = new Node(Move.DRACO_METEOR);
			movebank[74] = new Node(Move.WAVE_CRASH);
			break;
		case 146:
			movebank = new Node[40];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0] = new Node(Move.MUD_SLAP);
			movebank[3] = new Node(Move.WITHDRAW);
			movebank[7] = new Node(Move.WATER_GUN);
			movebank[11] = new Node(Move.FURY_CUTTER);
			movebank[15] = new Node(Move.FURY_SWIPES);
			movebank[19] = new Node(Move.ANCIENT_POWER);
			movebank[23] = new Node(Move.ROCK_POLISH);
			movebank[27] = new Node(Move.SLASH);
			movebank[31] = new Node(Move.HONE_CLAWS);
			movebank[35] = new Node(Move.RAZOR_SHELL);
			movebank[39] = new Node(Move.SHELL_SMASH);
			break;
		case 147:
			movebank = new Node[60];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0] = new Node(Move.MUD_SLAP);
			movebank[3] = new Node(Move.WITHDRAW);
			movebank[7] = new Node(Move.WATER_GUN);
			movebank[11] = new Node(Move.FURY_CUTTER);
			movebank[15] = new Node(Move.FURY_SWIPES);
			movebank[19] = new Node(Move.ANCIENT_POWER);
			movebank[23] = new Node(Move.ROCK_POLISH);
			movebank[27] = new Node(Move.SLASH);
			movebank[31] = new Node(Move.HONE_CLAWS);
			movebank[35] = new Node(Move.RAZOR_SHELL);
			movebank[39] = new Node(Move.SHELL_SMASH);
			movebank[41] = new Node(Move.LIQUIDATION);
			movebank[47] = new Node(Move.CROSS_CHOP);
			movebank[53] = new Node(Move.STONE_EDGE);
			movebank[59] = new Node(Move.WAVE_CRASH);
			break;
		case 148:
			movebank = new Node[45];
			movebank[0] = new Node(Move.MUD_SPORT);
			movebank[0].next = new Node(Move.FLASH);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[5] = new Node(Move.MUD_SLAP);
			movebank[8] = new Node(Move.BRINE);
			movebank[11] = new Node(Move.MUD_SHOT);
			movebank[14] = new Node(Move.FLASH_RAY);
			movebank[18] = new Node(Move.MUD_BOMB);
			movebank[23] = new Node(Move.FEINT_ATTACK);
			movebank[27] = new Node(Move.LIQUIDATION);
			movebank[32] = new Node(Move.LUSTER_PURGE);
			movebank[38] = new Node(Move.MUDDY_WATER);
			movebank[44] = new Node(Move.DARK_PULSE);
			break;
		case 149:
			movebank = new Node[65];
			movebank[0] = new Node(Move.MAGIC_FANG);
			movebank[0].next = new Node(Move.FEINT_ATTACK);
			movebank[4] = new Node(Move.FELL_STINGER);
			movebank[7] = new Node(Move.LIQUIDATION);
			movebank[10] = new Node(Move.NIGHT_SLASH);
			movebank[14] = new Node(Move.HYPER_FANG);
			movebank[19] = new Node(Move.TAKE_DOWN);
			movebank[23] = new Node(Move.BRUTAL_SWING);
			movebank[27] = new Node(Move.WAVE_CRASH);
			movebank[31] = new Node(Move.SHADOW_CLAW);
			movebank[34] = new Node(Move.EARTHQUAKE);
			movebank[37] = new Node(Move.PLAY_ROUGH);
			movebank[41] = new Node(Move.SUCKER_PUNCH);
			movebank[45] = new Node(Move.DARKEST_LARIAT);
			movebank[48] = new Node(Move.MAGIC_CRASH);
			movebank[54] = new Node(Move.PHANTOM_FORCE);
			movebank[59] = new Node(Move.TWINKLE_TACKLE);
			movebank[64] = new Node(Move.UNSEEN_STRANGLE);
			break;
		case 150:
			movebank = new Node[60];
			movebank[0] = new Node(Move.SPLASH);
			movebank[0].addToEnd(new Node(Move.SWEET_KISS));
			movebank[0].addToEnd(new Node(Move.LOVELY_KISS));
			movebank[4] = new Node(Move.DRAINING_KISS);
			movebank[6] = new Node(Move.DOUBLE_SLAP);
			movebank[11] = new Node(Move.WATER_GUN);
			movebank[15] = new Node(Move.AQUA_RING);
			movebank[19] = new Node(Move.FLASH);
			movebank[24] = new Node(Move.AQUA_JET);
			movebank[28] = new Node(Move.PSYBEAM);
			movebank[31] = new Node(Move.SEA_DRAGON);
			movebank[35] = new Node(Move.SPARKLING_WATER);
			movebank[39] = new Node(Move.WATER_FLICK);
			movebank[43] = new Node(Move.WATER_SMACK);
			movebank[47] = new Node(Move.WATER_CLAP);
			movebank[51] = new Node(Move.WATER_KICK);
			movebank[55] = new Node(Move.SUPERCHARGED_SPLASH);
			movebank[59] = new Node(Move.DEEP_SEA_BUBBLE);
			break;
		case 151:
			movebank = new Node[28];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.POISON_STING);
			movebank[8] = new Node(Move.BITE);
			movebank[11] = new Node(Move.GLARE);
			movebank[16] = new Node(Move.SCREECH);
			movebank[19] = new Node(Move.ACID);
			movebank[24] = new Node(Move.STOCKPILE);
			movebank[27] = new Node(Move.ACID_SPRAY);
			break;
		case 152:
			movebank = new Node[63];
			movebank[0] = new Node(Move.ICE_FANG);
			movebank[0].addToEnd(new Node(Move.THUNDER_FANG));
			movebank[0].addToEnd(new Node(Move.FIRE_FANG));
			movebank[0].addToEnd(new Node(Move.WRAP));
			movebank[0].addToEnd(new Node(Move.LEER));
			movebank[0].addToEnd(new Node(Move.POISON_STING));
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[0].addToEnd(new Node(Move.GLARE));
			movebank[0].addToEnd(new Node(Move.SCREECH));
			movebank[19] = new Node(Move.ACID);
			movebank[26] = new Node(Move.STOCKPILE);
			movebank[31] = new Node(Move.ACID_SPRAY);
			movebank[38] = new Node(Move.MUD_BOMB);
			movebank[43] = new Node(Move.GASTRO_ACID);
			movebank[47] = new Node(Move.BELCH);
			movebank[50] = new Node(Move.HAZE);
			movebank[55] = new Node(Move.COIL);
			movebank[62] = new Node(Move.GUNK_SHOT);
			break;
		case 153:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 154:
			movebank = new Node[69];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			movebank[26] = new Node(Move.AIR_SLASH);
			movebank[33] = new Node(Move.CRUNCH);
			movebank[40] = new Node(Move.HAZE);
			movebank[47] = new Node(Move.VENOSHOCK);
			movebank[54] = new Node(Move.CONFUSE_RAY);
			movebank[61] = new Node(Move.BRAVE_BIRD);
			movebank[68] = new Node(Move.LEECH_LIFE);
			break;
		case 155:
			movebank = new Node[69];
			movebank[0] = new Node(Move.CROSS_POISON);
			movebank[0].addToEnd(new Node(Move.TAILWIND));
			movebank[0].addToEnd(new Node(Move.TOXIC));
			movebank[0].addToEnd(new Node(Move.SCREECH));
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.WING_ATTACK);
			movebank[26] = new Node(Move.BITE);
			movebank[33] = new Node(Move.CRUNCH);
			movebank[39] = new Node(Move.CROSS_POISON);
			movebank[43] = new Node(Move.HAZE);
			movebank[47] = new Node(Move.VENOSHOCK);
			movebank[54] = new Node(Move.CONFUSE_RAY);
			movebank[61] = new Node(Move.BRAVE_BIRD);
			movebank[68] = new Node(Move.LEECH_LIFE);
			break;
		case 156:
			movebank = new Node[23];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[6] = new Node(Move.SCARY_FACE);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.PLAY_NICE);
			movebank[18] = new Node(Move.MOONLIGHT);
			movebank[22] = new Node(Move.HEX);
			break;
		case 157:
			movebank = new Node[55];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[6] = new Node(Move.SCARY_FACE);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.PLAY_NICE);
			movebank[18] = new Node(Move.MOONLIGHT);
			movebank[22] = new Node(Move.HEX);
			movebank[24] = new Node(Move.IRON_DEFENSE);
			movebank[28] = new Node(Move.SPIKE_CANNON);
			movebank[30] = new Node(Move.SHADOW_BALL);
			movebank[33] = new Node(Move.TRI_ATTACK);
			movebank[36] = new Node(Move.DREAM_EATER);
			movebank[39] = new Node(Move.IRON_TAIL);
			movebank[45] = new Node(Move.GYRO_BALL);
			movebank[54] = new Node(Move.DESTINY_BOND);
			break;
		case 158:
			movebank = new Node[23];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.PURSUIT);
			movebank[6] = new Node(Move.LICK);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.FAKE_TEARS);
			movebank[18] = new Node(Move.NASTY_PLOT);
			movebank[22] = new Node(Move.HEX);
			break;
		case 159:
			movebank = new Node[55];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.PURSUIT);
			movebank[6] = new Node(Move.LICK);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.FAKE_TEARS);
			movebank[18] = new Node(Move.NASTY_PLOT);
			movebank[22] = new Node(Move.HEX);
			movebank[22].next = new Node(Move.SUCKER_PUNCH);
			movebank[26] = new Node(Move.SHADOW_BALL);
			movebank[29] = new Node(Move.FEINT_ATTACK);
			movebank[32] = new Node(Move.MINIMIZE);
			movebank[35] = new Node(Move.CURSE);
			movebank[39] = new Node(Move.NIGHT_SLASH);
			movebank[43] = new Node(Move.NIGHTMARE);
			movebank[49] = new Node(Move.TAKE_OVER);
			movebank[54] = new Node(Move.DESTINY_BOND);
			break;
		case 160:
			movebank = new Node[40];
			movebank[0] = new Node(Move.SMOKESCREEN);
			movebank[0].next = new Node(Move.SMOG);
			movebank[3] = new Node(Move.POISON_GAS);
			movebank[7] = new Node(Move.ACID);
			movebank[11] = new Node(Move.HEX);
			movebank[15] = new Node(Move.NIGHT_SHADE);
			movebank[19] = new Node(Move.SLUDGE);
			movebank[23] = new Node(Move.MINIMIZE);
			movebank[28] = new Node(Move.TOXIC_SPIKES);
			movebank[34] = new Node(Move.SLUDGE_BOMB);
			movebank[39] = new Node(Move.SHADOW_BALL);
			break;
		case 161:
			movebank = new Node[50];
			movebank[0] = new Node(Move.ROCK_POLISH);
			movebank[0].next = new Node(Move.ROCK_TOMB);
			movebank[5] = new Node(Move.FORCE_PALM);
			movebank[9] = new Node(Move.HEX);
			movebank[12] = new Node(Move.WILL_O_WISP);
			movebank[15] = new Node(Move.ROCK_BLAST);
			movebank[19] = new Node(Move.BULK_UP);
			movebank[22] = new Node(Move.NIGHT_SLASH);
			movebank[25] = new Node(Move.BULLET_PUNCH);
			movebank[29] = new Node(Move.ROCK_SLIDE);
			movebank[32] = new Node(Move.SHADOW_SNEAK);
			movebank[36] = new Node(Move.SMART_STRIKE);
			movebank[43] = new Node(Move.SHADOW_BALL);
			movebank[49] = new Node(Move.PHANTOM_FORCE);
			break;
		case 162:
			movebank = new Node[70];
			movebank[0] = new Node(Move.ROCK_POLISH);
			movebank[0].addToEnd(new Node(Move.ROCK_BLAST));
			movebank[0].addToEnd(new Node(Move.FORCE_PALM));
			movebank[0].addToEnd(new Node(Move.WILL_O_WISP));
			movebank[0].addToEnd(new Node(Move.ROCK_SLIDE));
			movebank[0].addToEnd(new Node(Move.BULK_UP));
			movebank[0].addToEnd(new Node(Move.NIGHT_SLASH));
			movebank[0].addToEnd(new Node(Move.BULLET_PUNCH));
			movebank[0].addToEnd(new Node(Move.STONE_EDGE));
			movebank[0].addToEnd(new Node(Move.SMART_STRIKE));
			movebank[0].addToEnd(new Node(Move.SHADOW_BALL));
			movebank[49] = new Node(Move.PHANTOM_FORCE);
			movebank[64] = new Node(Move.SUPERPOWER);
			movebank[69] = new Node(Move.ROCK_WRECKER);
			break;
		case 163:
			movebank = new Node[24];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.LOW_KICK);
			movebank[7] = new Node(Move.ROCK_THROW);
			movebank[11] = new Node(Move.FOCUS_ENERGY);
			movebank[15] = new Node(Move.BULK_UP);
			movebank[19] = new Node(Move.ROCK_SLIDE);
			movebank[23] = new Node(Move.SLAM);
			break;
		case 164:
			movebank = new Node[60];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.LOW_KICK);
			movebank[7] = new Node(Move.ROCK_THROW);
			movebank[11] = new Node(Move.FOCUS_ENERGY);
			movebank[15] = new Node(Move.BULK_UP);
			movebank[19] = new Node(Move.ROCK_SLIDE);
			movebank[23] = new Node(Move.SLAM);
			movebank[29] = new Node(Move.SCARY_FACE);
			movebank[35] = new Node(Move.DYNAMIC_PUNCH);
			movebank[41] = new Node(Move.HAMMER_ARM);
			movebank[47] = new Node(Move.STONE_EDGE);
			movebank[53] = new Node(Move.SUPERPOWER);
			movebank[59] = new Node(Move.REVERSAL);
			break;
		case 165:
			movebank = new Node[60];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.LOW_KICK);
			movebank[7] = new Node(Move.ROCK_THROW);
			movebank[11] = new Node(Move.FOCUS_ENERGY);
			movebank[15] = new Node(Move.BULK_UP);
			movebank[19] = new Node(Move.ROCK_SLIDE);
			movebank[23] = new Node(Move.SLAM);
			movebank[29] = new Node(Move.SCARY_FACE);
			movebank[35] = new Node(Move.DYNAMIC_PUNCH);
			movebank[41] = new Node(Move.HAMMER_ARM);
			movebank[47] = new Node(Move.STONE_EDGE);
			movebank[53] = new Node(Move.SUPERPOWER);
			movebank[59] = new Node(Move.REVERSAL);
			break;
		case 166:
			movebank = new Node[15];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			break;
		case 167:
			movebank = new Node[55];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			movebank[29] = new Node(Move.STOMP);
			movebank[34] = new Node(Move.HORN_DRILL);
			movebank[38] = new Node(Move.MAGNITUDE);
			movebank[41] = new Node(Move.STONE_EDGE);
			movebank[45] = new Node(Move.BODY_PRESS);
			movebank[49] = new Node(Move.ENDEAVOR);
			movebank[54] = new Node(Move.GIGA_IMPACT);
			break;
		case 168:
			movebank = new Node[75];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			movebank[29] = new Node(Move.STOMP);
			movebank[34] = new Node(Move.HORN_DRILL);
			movebank[38] = new Node(Move.MAGNITUDE);
			movebank[41] = new Node(Move.STONE_EDGE);
			movebank[45] = new Node(Move.BODY_PRESS);
			movebank[49] = new Node(Move.ENDEAVOR);
			movebank[54] = new Node(Move.GIGA_IMPACT);
			movebank[54].next = new Node(Move.EARTHQUAKE);
			movebank[59] = new Node(Move.ROCK_BLAST);
			movebank[63] = new Node(Move.SCORCHING_SANDS);
			movebank[65] = new Node(Move.HAMMER_ARM);
			movebank[69] = new Node(Move.SUPERPOWER);
			movebank[74] = new Node(Move.HEAVY_SLAM);
			break;
		case 169:
			movebank = new Node[30];
			movebank[0] = new Node(Move.DIG);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.PURSUIT);
			movebank[7] = new Node(Move.SAND_ATTACK);
			movebank[11] = new Node(Move.MAGNITUDE);
			movebank[14] = new Node(Move.METAL_CLAW);
			movebank[18] = new Node(Move.SANDSTORM);
			movebank[19] = new Node(Move.ROCK_THROW);
			movebank[24] = new Node(Move.HEADBUTT);
			movebank[29] = new Node(Move.LOW_KICK);
			break;
		case 170:
			movebank = new Node[65];
			movebank[0] = new Node(Move.DIG);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.PURSUIT);
			movebank[7] = new Node(Move.SAND_ATTACK);
			movebank[11] = new Node(Move.MAGNITUDE);
			movebank[14] = new Node(Move.METAL_CLAW);
			movebank[18] = new Node(Move.SANDSTORM);
			movebank[19] = new Node(Move.ROCK_THROW);
			movebank[24] = new Node(Move.HEADBUTT);
			movebank[29] = new Node(Move.LOW_KICK);
			movebank[33] = new Node(Move.DRILL_RUN);
			movebank[35] = new Node(Move.GLITZY_GLOW);
			movebank[38] = new Node(Move.STONE_EDGE);
			movebank[43] = new Node(Move.SPIRIT_BREAK);
			movebank[48] = new Node(Move.EARTHQUAKE);
			movebank[51] = new Node(Move.PHOTON_GEYSER);
			movebank[54] = new Node(Move.PLAY_ROUGH);
			movebank[57] = new Node(Move.PLASMA_FISTS);
			movebank[60] = new Node(Move.FISSURE);
			movebank[64] = new Node(Move.DARKEST_LARIAT);
			break;
		case 171:
			movebank = new Node[17];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SAND_ATTACK);
			movebank[12] = new Node(Move.WRAP);
			movebank[16] = new Node(Move.MUD_SHOT);
			break;
		case 172:
			movebank = new Node[43];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SAND_ATTACK);
			movebank[12] = new Node(Move.WRAP);
			movebank[16] = new Node(Move.MUD_SHOT);
			movebank[19] = new Node(Move.DIG);
			movebank[25] = new Node(Move.AURORA_BEAM);
			movebank[29] = new Node(Move.IRON_TAIL);
			movebank[32] = new Node(Move.LIGHT_BEAM);
			movebank[38] = new Node(Move.EARTHQUAKE);
			movebank[42] = new Node(Move.FLASH_CANNON);
			break;
		case 173:
			movebank = new Node[70];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SAND_ATTACK);
			movebank[12] = new Node(Move.WRAP);
			movebank[16] = new Node(Move.MUD_SHOT);
			movebank[19] = new Node(Move.DIG);
			movebank[25] = new Node(Move.AURORA_BEAM);
			movebank[29] = new Node(Move.IRON_TAIL);
			movebank[32] = new Node(Move.LIGHT_BEAM);
			movebank[38] = new Node(Move.EARTHQUAKE);
			movebank[42] = new Node(Move.FLASH_CANNON);
			movebank[47] = new Node(Move.FISSURE);
			movebank[52] = new Node(Move.GYRO_BALL);
			movebank[56] = new Node(Move.DRAGON_PULSE);
			movebank[59] = new Node(Move.FIRE_BLAST);
			movebank[64] = new Node(Move.FATAL_BIND);
			movebank[69] = new Node(Move.DRACO_METEOR);
			break;
		case 174:
			movebank = new Node[20];
			movebank[0] = new Node(Move.SPLASH);
			movebank[0].next = new Node(Move.POUND);
			movebank[0].next = new Node(Move.FLASH);
			movebank[3] = new Node(Move.LOVELY_KISS);
			movebank[7] = new Node(Move.SWEET_KISS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[15] = new Node(Move.ENCORE);
			movebank[19] = new Node(Move.CHARM);
			break;
		case 175:
			movebank = new Node[56];
			movebank[0] = new Node(Move.LOVELY_KISS);
			movebank[0].addToEnd(new Node(Move.SWEET_KISS));
			movebank[0].addToEnd(new Node(Move.FLASH_RAY));
			movebank[0].addToEnd(new Node(Move.ENCORE));
			movebank[0].addToEnd(new Node(Move.CHARM));
			movebank[0].addToEnd(new Node(Move.POUND));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[3] = new Node(Move.BABY_DOLL_EYES);
			movebank[7] = new Node(Move.MINIMIZE);
			movebank[11] = new Node(Move.SPACE_BEAM);
			movebank[15] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.METRONOME);
			movebank[23] = new Node(Move.MOONLIGHT);
			movebank[27] = new Node(Move.GRAVITY);
			movebank[31] = new Node(Move.COMET_PUNCH);
			movebank[35] = new Node(Move.METEOR_MASH);
			movebank[39] = new Node(Move.COSMIC_POWER);
			movebank[43] = new Node(Move.MOONBLAST);
			movebank[47] = new Node(Move.HEALING_WISH);
			movebank[51] = new Node(Move.SPACIAL_REND);
			movebank[55] = new Node(Move.CORE_ENFORCER);
			break;
		case 176:
			movebank = new Node[1];
			movebank[0] = new Node(Move.LOVELY_KISS);
			movebank[0].addToEnd(new Node(Move.SWEET_KISS));
			movebank[0].addToEnd(new Node(Move.FLASH_RAY));
			movebank[0].addToEnd(new Node(Move.ENCORE));
			movebank[0].addToEnd(new Node(Move.BABY_DOLL_EYES));
			movebank[0].addToEnd(new Node(Move.MINIMIZE));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.LIFE_DEW));
			movebank[0].addToEnd(new Node(Move.METRONOME));
			movebank[0].addToEnd(new Node(Move.MOONLIGHT));
			movebank[0].addToEnd(new Node(Move.GRAVITY));
			movebank[0].addToEnd(new Node(Move.COMET_PUNCH));
			movebank[0].addToEnd(new Node(Move.HEALING_WISH));
			movebank[0].addToEnd(new Node(Move.POUND));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[0].addToEnd(new Node(Move.SPACIAL_REND));
			movebank[0].addToEnd(new Node(Move.METEOR_MASH));
			movebank[0].addToEnd(new Node(Move.COSMIC_POWER));
			movebank[0].addToEnd(new Node(Move.MOONBLAST));
			break;
		case 177:
			movebank = new Node[55];
			movebank[0] = new Node(Move.BABY_DOLL_EYES);
			movebank[0].next = new Node(Move.WING_ATTACK);
			movebank[3] = new Node(Move.CALM_MIND);
			movebank[7] = new Node(Move.WISH);
			movebank[11] = new Node(Move.FLASH);
			movebank[13] = new Node(Move.FLASH_RAY);
			movebank[16] = new Node(Move.SWEET_KISS);
			movebank[19] = new Node(Move.CHARM);
			movebank[23] = new Node(Move.SAFEGUARD);
			movebank[26] = new Node(Move.AIR_SLASH);
			movebank[29] = new Node(Move.SWIFT);
			movebank[33] = new Node(Move.DRAINING_KISS);
			movebank[38] = new Node(Move.MOONBLAST);
			movebank[44] = new Node(Move.MAGIC_BLAST);
			movebank[49] = new Node(Move.TRI_ATTACK);
			movebank[54] = new Node(Move.GLITTER_DANCE);
			break;
		case 178:
			movebank = new Node[65];
			movebank[0] = new Node(Move.GLITTERING_TORNADO);
			movebank[0].next = new Node(Move.MIRROR_SHOT);
			movebank[9] = new Node(Move.GLITTER_DANCE);
			movebank[12] = new Node(Move.FEATHER_DANCE);
			movebank[15] = new Node(Move.WISH);
			movebank[17] = new Node(Move.FLASH);
			movebank[19] = new Node(Move.SWIFT);
			movebank[23] = new Node(Move.SWEET_KISS);
			movebank[27] = new Node(Move.CALM_MIND);
			movebank[30] = new Node(Move.CHARM);
			movebank[33] = new Node(Move.AIR_SLASH);
			movebank[36] = new Node(Move.CHARGE_BEAM);
			movebank[42] = new Node(Move.MAGIC_BLAST);
			movebank[49] = new Node(Move.FIERY_DANCE);
			movebank[53] = new Node(Move.TRI_ATTACK);
			movebank[59] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[64] = new Node(Move.MOONBLAST);
			break;
		case 179:
			movebank = new Node[28];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.TORMENT);
			movebank[7] = new Node(Move.HONE_CLAWS);
			movebank[11] = new Node(Move.FURY_SWIPES);
			movebank[14] = new Node(Move.MAGIC_POWDER);
			movebank[19] = new Node(Move.TAUNT);
			movebank[22] = new Node(Move.SPARKLE_STRIKE);
			movebank[24] = new Node(Move.FEINT_ATTACK);
			movebank[27] = new Node(Move.FAKE_TEARS);
			break;
		case 180:
			movebank = new Node[70];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.TORMENT);
			movebank[7] = new Node(Move.HONE_CLAWS);
			movebank[11] = new Node(Move.FURY_SWIPES);
			movebank[14] = new Node(Move.MAGIC_POWDER);
			movebank[19] = new Node(Move.TAUNT);
			movebank[22] = new Node(Move.SPARKLE_STRIKE);
			movebank[24] = new Node(Move.FEINT_ATTACK);
			movebank[27] = new Node(Move.FAKE_TEARS);
			movebank[29] = new Node(Move.NIGHT_SLASH);
			movebank[31] = new Node(Move.MAGIC_REFLECT);
			movebank[33] = new Node(Move.AGILITY);
			movebank[37] = new Node(Move.U_TURN);
			movebank[41] = new Node(Move.SPIRIT_BREAK);
			movebank[45] = new Node(Move.NIGHT_DAZE);
			movebank[51] = new Node(Move.NASTY_PLOT);
			movebank[57] = new Node(Move.FOUL_PLAY);
			movebank[62] = new Node(Move.MAGIC_TOMB);
			movebank[69] = new Node(Move.GLITTERING_SWORD);
			break;
		case 181:
			movebank = new Node[25];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK_ON);
			break;
		case 182:
			movebank = new Node[48];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK_ON);
			movebank[24].next = new Node(Move.IRON_DEFENSE);
			movebank[31] = new Node(Move.TAKE_DOWN);
			movebank[39] = new Node(Move.REBOOT);
			movebank[47] = new Node(Move.PISTOL_POP);
			break;
		case 183:
			movebank = new Node[65];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK_ON);
			movebank[24].next = new Node(Move.IRON_DEFENSE);
			movebank[31] = new Node(Move.TAKE_DOWN);
			movebank[39] = new Node(Move.REBOOT);
			movebank[47] = new Node(Move.PISTOL_POP);
			movebank[49] = new Node(Move.IRON_HEAD);
			movebank[49].next = new Node(Move.LOAD_FIREARMS);
			movebank[51] = new Node(Move.IRON_BLAST);
			movebank[53] = new Node(Move.SHIFT_GEAR);
			movebank[55] = new Node(Move.IRON_HEAD);
			movebank[57] = new Node(Move.MAGIC_BLAST);
			movebank[59] = new Node(Move.SHIFT_GEAR);
			movebank[64] = new Node(Move.PRISMATIC_LASER);
			break;
		case 184:
			movebank = new Node[23];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.GUST);
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.HYPNOSIS);
			movebank[22] = new Node(Move.DREAM_EATER);
			break;
		case 185:
			movebank = new Node[48];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.GUST);
			movebank[4] = new Node(Move.SPARK);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.HYPNOSIS);
			movebank[22] = new Node(Move.DREAM_EATER);
			movebank[26] = new Node(Move.AQUA_JET);
			movebank[29] = new Node(Move.SPARKLE_STRIKE);
			movebank[33] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.MOONLIGHT);
			movebank[39] = new Node(Move.MAGIC_REFLECT);
			movebank[43] = new Node(Move.TAKE_DOWN);
			movebank[47] = new Node(Move.MAGIC_TOMB);
			break;
		case 186:
			movebank = new Node[70];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.GUST);
			movebank[4] = new Node(Move.SPARK);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.HYPNOSIS);
			movebank[22] = new Node(Move.DREAM_EATER);
			movebank[26] = new Node(Move.AQUA_JET);
			movebank[29] = new Node(Move.SPARKLE_STRIKE);
			movebank[33] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.MOONLIGHT);
			movebank[39] = new Node(Move.MAGIC_REFLECT);
			movebank[43] = new Node(Move.TAKE_DOWN);
			movebank[47] = new Node(Move.MAGIC_TOMB);
			movebank[49] = new Node(Move.DESOLATE_VOID);
			movebank[54] = new Node(Move.COMET_CRASH);
			movebank[59] = new Node(Move.MAGIC_CRASH);
			movebank[64] = new Node(Move.GALAXY_BLAST);
			movebank[69] = new Node(Move.SPACIAL_REND);
			break;
		case 187:
			movebank = new Node[33];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.THUNDER_WAVE);
			movebank[7] = new Node(Move.THUNDERSHOCK);
			movebank[12] = new Node(Move.DRAGON_RAGE);
			movebank[15] = new Node(Move.GLITTERING_TORNADO);
			movebank[18] = new Node(Move.LIGHT_BEAM);
			movebank[21] = new Node(Move.WATER_PULSE);
			movebank[23] = new Node(Move.SLAM);
			movebank[25] = new Node(Move.TWISTER);
			movebank[29] = new Node(Move.DRAGON_BREATH);
			movebank[32] = new Node(Move.LUSTER_PURGE);
			break;
		case 188:
			movebank = new Node[53];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.THUNDER_WAVE);
			movebank[7] = new Node(Move.THUNDERSHOCK);
			movebank[12] = new Node(Move.DRAGON_RAGE);
			movebank[15] = new Node(Move.GLITTERING_TORNADO);
			movebank[18] = new Node(Move.LIGHT_BEAM);
			movebank[21] = new Node(Move.WATER_PULSE);
			movebank[23] = new Node(Move.SLAM);
			movebank[25] = new Node(Move.TWISTER);
			movebank[29] = new Node(Move.DRAGON_BREATH);
			movebank[32] = new Node(Move.GLITZY_GLOW);
			movebank[35] = new Node(Move.DRAGON_TAIL);
			movebank[38] = new Node(Move.GLITZY_GLOW);
			movebank[41] = new Node(Move.SCALE_SHOT);
			movebank[45] = new Node(Move.DRAGON_PULSE);
			movebank[49] = new Node(Move.DRAGON_RUSH);
			movebank[52] = new Node(Move.HYDRO_PUMP);
			break;
		case 189:
			movebank = new Node[75];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.THUNDER_WAVE);
			movebank[7] = new Node(Move.THUNDERSHOCK);
			movebank[12] = new Node(Move.DRAGON_RAGE);
			movebank[15] = new Node(Move.GLITTERING_TORNADO);
			movebank[18] = new Node(Move.LIGHT_BEAM);
			movebank[21] = new Node(Move.WATER_PULSE);
			movebank[23] = new Node(Move.SLAM);
			movebank[25] = new Node(Move.TWISTER);
			movebank[29] = new Node(Move.DRAGON_BREATH);
			movebank[32] = new Node(Move.GLITZY_GLOW);
			movebank[35] = new Node(Move.DRAGON_TAIL);
			movebank[38] = new Node(Move.GLITZY_GLOW);
			movebank[41] = new Node(Move.SCALE_SHOT);
			movebank[45] = new Node(Move.DRAGON_PULSE);
			movebank[49] = new Node(Move.DRAGON_RUSH);
			movebank[52] = new Node(Move.HYDRO_PUMP);
			movebank[54] = new Node(Move.PRISMATIC_LASER);
			movebank[59] = new Node(Move.SPACIAL_REND);
			movebank[61] = new Node(Move.DRAGON_DANCE);
			movebank[63] = new Node(Move.SKY_ATTACK);
			movebank[66] = new Node(Move.SPIRIT_BREAK);
			movebank[69] = new Node(Move.MAGIC_TOMB);
			movebank[72] = new Node(Move.LIGHT_OF_RUIN);
			movebank[74] = new Node(Move.HYPER_BEAM);
			break;
		case 190:
			movebank = new Node[25];
			movebank[0] = new Node(Move.COSMIC_POWER);
			movebank[0].addToEnd(new Node(Move.SPLASH));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.VACUUM_WAVE));
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY_TURVY);
			break;
		case 191:
			movebank = new Node[60];
			movebank[0] = new Node(Move.COSMIC_POWER);
			movebank[0].addToEnd(new Node(Move.SPLASH));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.VACUUM_WAVE));
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY_TURVY);
			movebank[39] = new Node(Move.COMET_PUNCH);
			movebank[43] = new Node(Move.BULK_UP);
			movebank[47] = new Node(Move.DESOLATE_VOID);
			movebank[51] = new Node(Move.AURA_SPHERE);
			movebank[55] = new Node(Move.METEOR_MASH);
			movebank[59] = new Node(Move.DRAIN_PUNCH);
			break;
		case 192:
			movebank = new Node[80];
			movebank[0] = new Node(Move.COSMIC_POWER);
			movebank[0].addToEnd(new Node(Move.SPLASH));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.VACUUM_WAVE));
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY_TURVY);
			movebank[39] = new Node(Move.COMET_PUNCH);
			movebank[43] = new Node(Move.BULK_UP);
			movebank[47] = new Node(Move.DESOLATE_VOID);
			movebank[51] = new Node(Move.AURA_SPHERE);
			movebank[55] = new Node(Move.METEOR_MASH);
			movebank[59] = new Node(Move.DRAIN_PUNCH);
			movebank[59].next = new Node(Move.STAR_STRUCK_ARCHER);
			movebank[59].next.next = new Node(Move.CLOSE_COMBAT);
			movebank[64] = new Node(Move.NO_RETREAT);
			movebank[69] = new Node(Move.METEOR_ASSAULT);
			movebank[79] = new Node(Move.GENESIS_SUPERNOVA);
			break;
		case 193:
			movebank = new Node[45];
			movebank[0] = new Node(Move.SNOWSCAPE);
			movebank[0].addToEnd(new Node(Move.SPLASH));
			movebank[0].addToEnd(new Node(Move.POWDER_SNOW));
			movebank[0].addToEnd(new Node(Move.HAZE));
			movebank[9] = new Node(Move.ICY_WIND);
			movebank[14] = new Node(Move.ICE_BALL);
			movebank[19] = new Node(Move.COSMIC_POWER);
			movebank[24] = new Node(Move.SPACE_BEAM);
			movebank[29] = new Node(Move.FREEZE_DRY);
			movebank[44] = new Node(Move.SHEER_COLD);
			break;
		case 194:
			movebank = new Node[55];
			movebank[0] = new Node(Move.COMET_CRASH);
			movebank[0].addToEnd(new Node(Move.BRAVE_BIRD));
			movebank[0].addToEnd(new Node(Move.HEAD_SMASH));
			movebank[0].addToEnd(new Node(Move.TAKE_DOWN));
			movebank[9] = new Node(Move.COMET_PUNCH);
			movebank[11] = new Node(Move.MACH_PUNCH);
			movebank[14] = new Node(Move.ICE_PUNCH);
			movebank[14].next = new Node(Move.FIRE_PUNCH);
			movebank[14].next.next = new Node(Move.THUNDER_PUNCH);
			movebank[18] = new Node(Move.BLIZZARD);
			movebank[24] = new Node(Move.ICE_BALL);
			movebank[29] = new Node(Move.GIGA_IMPACT);
			movebank[39] = new Node(Move.ICE_SPINNER);
			movebank[44] = new Node(Move.COMET_CRASH);
			movebank[49] = new Node(Move.ICICLE_CRASH);
			movebank[54] = new Node(Move.METEOR_ASSAULT);
			break;
		case 195:
			movebank = new Node[50];
			movebank[0] = new Node(Move.DEFENSE_CURL);
			movebank[2] = new Node(Move.ANCIENT_POWER);
			movebank[5] = new Node(Move.ROLLOUT);
			movebank[7] = new Node(Move.TACKLE);
			movebank[11] = new Node(Move.COMET_PUNCH);
			movebank[14] = new Node(Move.GRAVITY);
			movebank[18] = new Node(Move.ROCK_BLAST);
			movebank[23] = new Node(Move.DESOLATE_VOID);
			movebank[26] = new Node(Move.SMACK_DOWN);
			movebank[33] = new Node(Move.ROCK_SLIDE);
			movebank[39] = new Node(Move.METEOR_MASH);
			movebank[49] = new Node(Move.POWER_GEM);
			break;
		case 196:
			movebank = new Node[60];
			movebank[0] = new Node(Move.FORCE_PALM);
			movebank[0].next = new Node(Move.SMACK_DOWN);
			movebank[0].next.next = new Node(Move.WAKE_UP_SLAP);
			movebank[4] = new Node(Move.ROLLOUT);
			movebank[8] = new Node(Move.SANDSTORM);
			movebank[12] = new Node(Move.COMET_PUNCH);
			movebank[16] = new Node(Move.GRAVITY);
			movebank[23] = new Node(Move.MACH_PUNCH);
			movebank[26] = new Node(Move.ROCK_BLAST);
			movebank[30] = new Node(Move.DESOLATE_VOID);
			movebank[35] = new Node(Move.ROCK_SLIDE);
			movebank[39] = new Node(Move.METEOR_MASH);
			movebank[43] = new Node(Move.COSMIC_POWER);
			movebank[49] = new Node(Move.STONE_EDGE);
			movebank[59] = new Node(Move.POWER_GEM);
			break;
		case 197:
			movebank = new Node[24];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THUNDER_WAVE);
			movebank[4] = new Node(Move.CONFUSE_RAY);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[11] = new Node(Move.THUNDERSHOCK);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.WILL_O_WISP);
			movebank[23] = new Node(Move.HEX);
			break;
		case 198:
			movebank = new Node[55];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THUNDER_WAVE);
			movebank[4] = new Node(Move.CONFUSE_RAY);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[11] = new Node(Move.THUNDERSHOCK);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.WILL_O_WISP);
			movebank[23] = new Node(Move.HEX);
			movebank[24] = new Node(Move.SHADOW_PUNCH);
			movebank[24].next = new Node(Move.THUNDER_PUNCH);
			movebank[27] = new Node(Move.MAGNET_RISE);
			movebank[30] = new Node(Move.SHADOW_BALL);
			movebank[34] = new Node(Move.TRI_ATTACK);
			movebank[37] = new Node(Move.HEADBUTT);
			movebank[39] = new Node(Move.ELECTRO_BALL);
			movebank[43] = new Node(Move.PHANTOM_FORCE);
			movebank[49] = new Node(Move.VOLT_SWITCH);
			movebank[54] = new Node(Move.THUNDER);
			break;
		case 199:
			movebank = new Node[22];
			movebank[0] = new Node(Move.MAGNET_RISE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[5] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.MACH_PUNCH);
			movebank[11] = new Node(Move.KARATE_CHOP);
			movebank[15] = new Node(Move.NUZZLE);
			movebank[18] = new Node(Move.SPARK);
			movebank[21] = new Node(Move.SHIFT_GEAR);
			break;
		case 200:
			movebank = new Node[47];
			movebank[0] = new Node(Move.MAGNET_RISE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[5] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.MACH_PUNCH);
			movebank[11] = new Node(Move.KARATE_CHOP);
			movebank[15] = new Node(Move.NUZZLE);
			movebank[18] = new Node(Move.SPARK);
			movebank[21] = new Node(Move.SHIFT_GEAR);
			movebank[24] = new Node(Move.BRICK_BREAK);
			movebank[28] = new Node(Move.COMET_PUNCH);
			movebank[31] = new Node(Move.BULLET_PUNCH);
			movebank[35] = new Node(Move.THUNDER_PUNCH);
			movebank[35].next = new Node(Move.FIRE_PUNCH);
			movebank[35].next.next = new Node(Move.ICE_PUNCH);
			movebank[39] = new Node(Move.DRAIN_PUNCH);
			movebank[43] = new Node(Move.SUCKER_PUNCH);
			movebank[46] = new Node(Move.VOLT_TACKLE);
			break;
		case 201:
			movebank = new Node[60];
			movebank[0] = new Node(Move.MAGNET_RISE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[5] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.MACH_PUNCH);
			movebank[11] = new Node(Move.KARATE_CHOP);
			movebank[15] = new Node(Move.NUZZLE);
			movebank[18] = new Node(Move.SPARK);
			movebank[21] = new Node(Move.SHIFT_GEAR);
			movebank[24] = new Node(Move.BRICK_BREAK);
			movebank[28] = new Node(Move.COMET_PUNCH);
			movebank[31] = new Node(Move.BULLET_PUNCH);
			movebank[35] = new Node(Move.THUNDER_PUNCH);
			movebank[35].next = new Node(Move.FIRE_PUNCH);
			movebank[35].next.next = new Node(Move.ICE_PUNCH);
			movebank[39] = new Node(Move.DRAIN_PUNCH);
			movebank[43] = new Node(Move.SUCKER_PUNCH);
			movebank[46] = new Node(Move.VOLT_TACKLE);
			movebank[49] = new Node(Move.PLASMA_FISTS);
			movebank[53] = new Node(Move.DYNAMIC_PUNCH);
			movebank[56] = new Node(Move.CLOSE_COMBAT);
			movebank[59] = new Node(Move.METEOR_ASSAULT);
			break;
		case 202:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.THUNDERSHOCK);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.EMBER);
			movebank[13] = new Node(Move.FLAME_CHARGE);
			movebank[18] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.FLAME_WHEEL);
			movebank[28] = new Node(Move.THUNDER_WAVE);
			movebank[32] = new Node(Move.CHARGE_BEAM);
			break;
		case 203:
			movebank = new Node[53];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.THUNDERSHOCK);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.EMBER);
			movebank[13] = new Node(Move.FLAME_CHARGE);
			movebank[18] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.FLAME_WHEEL);
			movebank[28] = new Node(Move.THUNDER_WAVE);
			movebank[32] = new Node(Move.CHARGE_BEAM);
			movebank[34] = new Node(Move.WING_ATTACK);
			movebank[37] = new Node(Move.FLAMETHROWER);
			movebank[41] = new Node(Move.AIR_SLASH);
			movebank[44] = new Node(Move.DRAGON_PULSE);
			movebank[49] = new Node(Move.THUNDERBOLT);
			movebank[52] = new Node(Move.OVERHEAT);
			break;
		case 204:
			movebank = new Node[75];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.THUNDERSHOCK);
			movebank[8] = new Node(Move.SLAM);
			movebank[10] = new Node(Move.EMBER);
			movebank[13] = new Node(Move.FLAME_CHARGE);
			movebank[18] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.FLAME_WHEEL);
			movebank[28] = new Node(Move.THUNDER_WAVE);
			movebank[32] = new Node(Move.CHARGE_BEAM);
			movebank[34] = new Node(Move.WING_ATTACK);
			movebank[37] = new Node(Move.FLAMETHROWER);
			movebank[41] = new Node(Move.AIR_SLASH);
			movebank[44] = new Node(Move.DRAGON_PULSE);
			movebank[49] = new Node(Move.THUNDERBOLT);
			movebank[52] = new Node(Move.OVERHEAT);
			movebank[54] = new Node(Move.FIRE_BLAST);
			movebank[57] = new Node(Move.DRAGON_RUSH);
			movebank[62] = new Node(Move.THUNDER);
			movebank[69] = new Node(Move.ZAP_CANNON);
			movebank[74] = new Node(Move.FUSION_BOLT);
			movebank[74].next = new Node(Move.FUSION_FLARE);
			break;
		case 205:
			movebank = new Node[26];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[25] = new Node(Move.NUZZLE);
			break;
		case 206:
			movebank = new Node[36];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.SMACK_DOWN);
			movebank[28] = new Node(Move.SELF_DESTRUCT);
			movebank[30] = new Node(Move.DISCHARGE);
			movebank[32] = new Node(Move.MAGNITUDE);
			movebank[35] = new Node(Move.ROCK_BLAST);
			break;
		case 207:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.SMACK_DOWN);
			movebank[28] = new Node(Move.SELF_DESTRUCT);
			movebank[30] = new Node(Move.DISCHARGE);
			movebank[32] = new Node(Move.MAGNITUDE);
			movebank[35] = new Node(Move.ROCK_BLAST);
			movebank[37] = new Node(Move.THUNDERBOLT);
			movebank[41] = new Node(Move.ROCK_SLIDE);
			movebank[44] = new Node(Move.IRON_DEFENSE);
			movebank[48] = new Node(Move.POWER_GEM);
			movebank[54] = new Node(Move.AURA_SPHERE);
			movebank[58] = new Node(Move.FIRE_BLAST);
			movebank[62] = new Node(Move.ZAP_CANNON);
			movebank[69] = new Node(Move.HYPER_BEAM);
			break;
		case 208:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.SMACK_DOWN);
			movebank[28] = new Node(Move.SELF_DESTRUCT);
			movebank[30] = new Node(Move.DISCHARGE);
			movebank[32] = new Node(Move.MAGNITUDE);
			movebank[35] = new Node(Move.ROCK_BLAST);
			movebank[35].next = new Node(Move.GLITZY_GLOW);
			movebank[38] = new Node(Move.THUNDERBOLT);
			movebank[41] = new Node(Move.PARABOLIC_CHARGE);
			movebank[45] = new Node(Move.GLITTER_DANCE);
			movebank[49] = new Node(Move.COMET_CRASH);
			movebank[51] = new Node(Move.REFLECT);
			movebank[54] = new Node(Move.PRISMATIC_LASER);
			movebank[58] = new Node(Move.OVERHEAT);
			movebank[62] = new Node(Move.GLITTERING_SWORD);
			movebank[69] = new Node(Move.LIGHT_OF_RUIN);
			break;
		case 209:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 210:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 211:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 212:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 213:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 214:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 215:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 216:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 217:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 218:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 219:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 220:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 221:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 222:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 223:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 224:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 225:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 226:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 227:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 228:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 229:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 230:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 231:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 232:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 233:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
		case 234:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 235:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 236:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY_DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 238:
			movebank = new Node[55];
			movebank[0] = new Node(Move.LEER);
			movebank[0].next = new Node(Move.LOW_KICK);
			movebank[3] = new Node(Move.PAYBACK);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[11] = new Node(Move.SAND_ATTACK);
			movebank[15] = new Node(Move.FACADE);
			movebank[19] = new Node(Move.PROTECT);
			movebank[23] = new Node(Move.BEAT_UP);
			movebank[27] = new Node(Move.SCARY_FACE);
			movebank[31] = new Node(Move.BRICK_BREAK);
			movebank[35] = new Node(Move.SWAGGER);
			movebank[39] = new Node(Move.CRUNCH);
			movebank[43] = new Node(Move.HI_JUMP_KICK);
			movebank[47] = new Node(Move.SWORDS_DANCE);
			movebank[51] = new Node(Move.HEAD_SMASH);
			movebank[54] = new Node(Move.CLOSE_COMBAT);
			break;
		}
		
	}
	
	public class Node implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Move data;
	    public Node next;

	    public Node(Move data) { this.data = data; }

		public void addToEnd(Node node) {
			Node n = this;
			while (n.next != null) {
				n = n.next;
			}
			n.next = node;
			
		}
	}

	public void faint(boolean announce, Player player, Pokemon foe) {
		this.currentHP = 0;
		this.fainted = true;
		this.battled = false;
		awardHappiness(-3);
		this.vStatuses.remove(Status.LOCKED);
		this.vStatuses.remove(Status.SPUN);
		this.vStatuses.remove(Status.RECHARGE);
		this.vStatuses.remove(Status.CHARGING);
		this.vStatuses.remove(Status.NO_SWITCH);
		this.vStatuses.remove(Status.TRAPPED);
		foe.vStatuses.remove(Status.SPUN);
		foe.vStatuses.remove(Status.TRAPPED);
		if (player != null && this.trainerOwned) {
			player.setBattled(player.getBattled() - 1);
		}
		if (announce) System.out.println("\n" + this.nickname + " fainted!");
	}

	public void clearVolatile() {
		confusionCounter = 0;
		magCount = 0;
		toxic = 0;
		this.lastMoveUsed = null;
		this.moveMultiplier = 1;
		this.vStatuses.clear();
		statStages = new int[7];
		this.impressive = true;
		setType();
		setAbility(this.abilitySlot);
		if (this.ability == Ability.NATURAL_CURE) this.status = Status.HEALTHY;
		
	}
	
	public int calc(double attackStat, double defenseStat, int bp, int level) {
		double num = 2* (double) level / 5 + 2;
		double stat = attackStat / defenseStat / 50;
		double damageDouble = Math.floor(num * bp * stat);
		damageDouble += 2;
		
		Random roll = new Random();
		double rollAmt = roll.nextInt(16);
		rollAmt += 85;
		rollAmt /= 100;
		
		// Roll
		damageDouble *= rollAmt;
		// Convert to integer
		int damage = (int) Math.floor(damageDouble);
		return damage;
	}
	
	public int calcWithTypes(Pokemon foe, Move move, boolean first, Field field) {
		double attackStat;
		double defenseStat;
		int damage = 0;
		int bp = move.basePower;
		
		if (move.accuracy <= 100 && move.cat != 2) {
			if (getImmune(foe, move.mtype)) return 0; // Check for immunity
		} else if (move.accuracy > 100 && move.cat != 2) {
			if (getImmune(foe, move.mtype)) return 0; // Check for immunity
		}
		if (move.cat != 2 && move.mtype == PType.GROUND && foe.magCount > 0) return 0; // Check for immunity
		
		if (move == Move.DREAM_EATER && foe.status != Status.ASLEEP) return 0;
		
		if (move.basePower < 0) bp = determineBasePower(foe, move, first, field, null);
		
		//if (this.vStatuses.contains(Status.AUTO) && (move == Move.BIG_BULLET || move == Move.GUNSHOT || move == Move.ROCKET)) bp *= 2;
		// Use either physical or special attack/defense
		if (move.isPhysical()) {
			attackStat = this.getStat(1);
			defenseStat = foe.getStat(2);
			attackStat *= this.asModifier(0);
			defenseStat *= foe.asModifier(1);
			if (this.status == Status.BURNED) attackStat /= 2;
		} else {
			attackStat = this.getStat(3);
			defenseStat = foe.getStat(4);
			attackStat *= this.asModifier(2);
			defenseStat *= foe.asModifier(3);
			if (this.status == Status.FROSTBITE) attackStat /= 2;
		}
		
		damage = calc(attackStat, defenseStat, bp, this.level);
		
		// Stab
		if (move.mtype == this.type1) damage *= 1.5;
		if (move.mtype == this.type2) damage *= 1.5;
		
		// Charged
		if (move.mtype == PType.ELECTRIC && this.vStatuses.contains(Status.CHARGED)) damage *= 2;
		
		double multiplier = 1;
		// Check type effectiveness
		PType[] resist = getResistances(move.mtype);
		for (PType type : resist) {
			if (foe.type1 == type) multiplier /= 2;
			if (foe.type2 == type) multiplier /= 2;
		}
		
		// Check type effectiveness
		PType[] weak = getWeaknesses(move.mtype);
		for (PType type : weak) {
			if (foe.type1 == type) multiplier *= 2;
			if (foe.type2 == type) multiplier *= 2;
		}
		
		damage *= multiplier;
		
		if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS) damage = this.level;
		//if (move == Move.FIRE_DASH) damage = this.currentHP;
		if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
		if (move == Move.DRAGON_RAGE) damage = 40;
		
		damage = Math.max(damage, 1);
		
		return damage;
	}

	public void endOfTurn(Pokemon f, Player player, Field field) {
		if (this.isFainted()) return;
		if (this.status == Status.TOXIC && toxic < 16) toxic++;
		if (this.status == Status.FROSTBITE && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max(this.getStat(0) / 16, 1);
			System.out.println("\n" + this.nickname + " was hurt by frostbite!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		} else if (this.status == Status.BURNED && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max(this.getStat(0) / 16, 1);
			System.out.println("\n" + this.nickname + " was hurt by its burn!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		} else if (this.status == Status.POISONED && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max(this.getStat(0) / 8, 1);
			System.out.println("\n" + this.nickname + " was hurt by poison!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		} else if (this.status == Status.TOXIC && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max((this.getStat(0) / 16) * toxic, 1);
			System.out.println("\n" + this.nickname + " was hurt by poison!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		}
		if (this.vStatuses.contains(Status.CURSED) && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max(this.getStat(0) / 4, 1);
			System.out.println("\n" + this.nickname + " was hurt by the curse!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		}
		if (this.vStatuses.contains(Status.LEECHED) && this.ability != Ability.MAGIC_GUARD) {
			int hp = Math.max(this.getStat(0) / 8, 1);
			if (hp >= this.currentHP) hp = this.currentHP;
			if (f.currentHP > f.getStat(0)) f.currentHP = f.getStat(0);
			this.currentHP -= hp;
			System.out.println("\n" + f.nickname + " sucked health from " + this.nickname + "!");
			f.currentHP += hp;
			if (this.currentHP <= 0) {
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
			
		}
		if (this.vStatuses.contains(Status.NIGHTMARE) && this.ability != Ability.MAGIC_GUARD) {
			if (this.status == Status.ASLEEP) {
				this.currentHP -= Math.max(this.getStat(0) / 4, 1);
				System.out.println("\n" + this.nickname + " had a nightmare!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, player, f);
					f.awardxp((int) Math.ceil(this.level * this.trainer), player);
				}
			} else {
				this.vStatuses.remove(Status.NIGHTMARE);
			}
		} if (this.vStatuses.contains(Status.AQUA_RING)) {
			if (this.currentHP < this.getStat(0)) {
				this.currentHP += Math.max(this.getStat(0) / 16, 1);
				if (this.currentHP > this.getStat(0)) {
					this.currentHP = this.getStat(0);
				}
				System.out.println("\n" + this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.RAIN_DISH && field.equals(field.weather, Effect.RAIN)) {
			if (this.currentHP < this.getStat(0)) {
				this.currentHP += Math.max(this.getStat(0) / 16, 1);
				if (this.currentHP > this.getStat(0)) {
					this.currentHP = this.getStat(0);
				}
				System.out.println("\n" + this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.ICE_BODY && field.equals(field.weather, Effect.SNOW)) {
			if (this.currentHP < this.getStat(0)) {
				this.currentHP += Math.max(this.getStat(0) / 16, 1);
				if (this.currentHP > this.getStat(0)) {
					this.currentHP = this.getStat(0);
				}
				System.out.println("\n" + this.nickname + " restored HP.");
			}
		} if (this.vStatuses.contains(Status.SPUN)) {
			if (this.spunCount == 0) {
				System.out.println("\n" + this.nickname + " was freed from wrap!");
				this.vStatuses.remove(Status.SPUN);
			} else {
				if (this.ability != Ability.MAGIC_GUARD) {
					this.currentHP -= Math.max(this.getStat(0) / 16, 1);
					System.out.println("\n" + this.nickname + " was hurt by being wrapped!");
				}
				this.spunCount--;
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, player, f);
					f.awardxp((int) Math.ceil(this.level * this.trainer), player);
				}
			}
		}
		if (field.equals(field.weather, Effect.SANDSTORM) && this.type1 != PType.ROCK && this.type1 != PType.STEEL && this.type1 != PType.GROUND
				&& this.ability != Ability.SAND_FORCE && this.ability != Ability.SAND_RUSH && this.ability != Ability.SAND_VEIL && this.type2 != PType.ROCK 
				&& this.type2 != PType.STEEL && this.type2 != PType.GROUND && this.ability != Ability.MAGIC_GUARD) {
			this.currentHP -= Math.max(this.getStat(0) / 16, 1);
			System.out.println("\n" + this.nickname + " was buffeted by the sandstorm!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
		}
		
		if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN) && field.weatherTurns > 1) {
			this.currentHP -= Math.max(this.getStat(0) / 8, 1);
			System.out.println("\n" + this.nickname + " was hurt!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
		}
		
		if (this.perishCount > 0) {
			this.perishCount--;
			System.out.println("\n" + this.nickname + "'s perish count fell to " + this.perishCount + "!");
			if (this.perishCount == 0) {
				this.faint(true, player, f);
				f.awardxp((int) Math.ceil(this.level * this.trainer), player);
			}
		}
		if (this.vStatuses.contains(Status.LOCKED) && this.outCount == 0 && (this.lastMoveUsed == Move.OUTRAGE || this.lastMoveUsed == Move.PETAL_DANCE || this.lastMoveUsed == Move.THRASH)) {
			this.confuse(false);
			this.vStatuses.remove(Status.LOCKED);
		}
		if (this.vStatuses.contains(Status.ENCORED) && --this.encoreCount == 0) {
			this.vStatuses.remove(Status.ENCORED);
			System.out.println(this.nickname + "'s encore ended!");
		}
		if (this.vStatuses.contains(Status.TAUNTED) && --this.tauntCount == 0) {
			this.vStatuses.remove(Status.TAUNTED);
			System.out.println(this.nickname + "shook off the taunt!");
		}
		if (this.vStatuses.contains(Status.TORMENTED) && --this.tormentCount == 0) {
			this.vStatuses.remove(Status.TORMENTED);
			System.out.println(this.nickname + "'s torment ended!");
		}
		if (this.vStatuses.contains(Status.LOCKED) && this.rollCount == 5) {
			this.vStatuses.remove(Status.LOCKED);
		}
		if (this.vStatuses.contains(Status.BONDED)) {
			this.vStatuses.remove(Status.BONDED);
		}
		
		if (this.ability == Ability.SPEED_BOOST) {
			if (this.statStages[4] != 6) stat(this, 4, 1);
		}
		
		if (this.ability == Ability.SHED_SKIN && this.status != Status.HEALTHY) {
			int r = (int)(Math.random() * 3);
			if (r == 0) {
				this.status = Status.HEALTHY;
				System.out.println("[" + this.nickname + "'s Shed Skin]: " + this.nickname + " became healthy!");
			}
		}
		
		this.vStatuses.remove(Status.FLINCHED);
		this.vStatuses.remove(Status.PROTECT);
		this.vStatuses.remove(Status.ENDURE);
		
	}

	public int getSpeed() {
		double speed = this.getStat(5) * this.asModifier(4);
		if (this.status == Status.PARALYZED) speed *= 0.5;
		return (int) speed;
	}
	
	public Pokemon getFaster(Pokemon other, Field field, int thisP, int otherP) {
		int speed1 = this.getSpeed();
		if (field.contains(field.playerSide, Effect.TAILWIND)) speed1 *= 2;
		if (checkAbilitySpeedBoost(this.ability, field)) speed1 *= 2;
		int speed2 = other.getSpeed();
		if (field.contains(field.foeSide, Effect.TAILWIND)) speed2 *= 2;
		if (checkAbilitySpeedBoost(other.ability, field)) speed2 *= 2;
		Pokemon faster = speed1 > speed2 ? this : other;
		if (speed1 == speed2) {
			Random random = new Random();
			boolean isHeads = random.nextBoolean();
			faster = isHeads ? this : other;
		}
		if (field.contains(field.fieldEffects, Effect.TRICK_ROOM)) {
			faster = faster == this ? other : this;
		}
		faster = otherP > thisP ? other : faster;
		faster = thisP > otherP ? this : faster;
		return faster;
	}
	
	private boolean checkAbilitySpeedBoost(Ability ability, Field field) {
		if (field.equals(field.weather, Effect.SUN) && ability == Ability.CHLOROPHYLL) return true;
		if (field.equals(field.weather, Effect.RAIN) && ability == Ability.SWIFT_SWIM) return true;
		if (field.equals(field.weather, Effect.SANDSTORM) && ability == Ability.SAND_RUSH) return true;
		if (field.equals(field.weather, Effect.SNOW) && ability == Ability.SLUSH_RUSH) return true;
		return false;
	}


	public void confuse(boolean announce) {
		if (!this.vStatuses.contains(Status.CONFUSED)) {
			this.vStatuses.add(Status.CONFUSED);
			this.confusionCounter = (int)(Math.random() * 4) + 1;
			System.out.println(this.nickname + " became confused!");
		} else {
			if (announce) fail();
		}
		
	}
	
	public void sleep(boolean announce) {
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.INSOMNIA) {
				if (announce) System.out.println("[" + this.nickname + "'s Insomnia]: It doesn't effect " + this.nickname + "..."); 
				return;
			}
			this.status = Status.ASLEEP;
			this.sleepCounter = (int)(Math.random() * 3) + 1;
			if (this.ability == Ability.EARLY_BIRD) this.sleepCounter /= 2;
			System.out.println(this.nickname + " fell asleep!");
		} else {
			if (announce) fail();
		}
		
	}
	
	public void paralyze(boolean announce, Pokemon foe) {
		if (this.type1 == PType.ELECTRIC || this.type2 == PType.ELECTRIC) {
			if (announce) System.out.println("It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.PARALYZED;
			System.out.println(this.nickname + " was paralyzed!");
			if (this.ability == Ability.SYNCHRONIZE) foe.paralyze(false, this);
		} else {
			if (announce) fail();
		}
	}
	
	public void burn(boolean announce, Pokemon foe) {
		if (this.type1 == PType.FIRE || this.type2 == PType.FIRE) {
			if (announce) System.out.println("It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.WATER_VEIL) {
				if (announce) System.out.print("[" + this.nickname + "'s Water Veil]: It doesn't effect " + this.nickname);
				return;
			}
			this.status = Status.BURNED;
			if (this.ability == Ability.SYNCHRONIZE) foe.burn(false, this);
			System.out.println(this.nickname + " was burned!");
		} else {
			if (announce) fail();
		}
	}
	
	public void poison(boolean announce, Pokemon foe) {
		if (this.type1 == PType.POISON || this.type2 == PType.POISON) {
			if (announce) System.out.println("It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.POISONED;
			System.out.println(this.nickname + " was poisoned!");
			if (this.ability == Ability.SYNCHRONIZE) foe.poison(false, this);
		} else {
			if (announce) fail();
		}
	}
	
	public void toxic(boolean announce, Pokemon foe) {
		if (this.type1 == PType.POISON || this.type2 == PType.POISON) {
			if (announce) System.out.println("It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.TOXIC;
			System.out.println(this.nickname + " was badly poisoned!");
			if (this.ability == Ability.SYNCHRONIZE) foe.toxic(false, this);
		} else {
			if (announce) fail();
		}
	}
	
	public void freeze(boolean announce, Field field) {
		if (this.type1 == PType.ICE || this.type2 == PType.ICE) {
			if (announce) System.out.println("It doesn't effect " + this.nickname + "...");
			return;
		}
		if (field.equals(field.weather, Effect.SUN)) {
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.FROSTBITE;
			System.out.println(this.nickname + " was frostbitten!");
		} else {
			if (announce) fail();
		}
	}
	
	private int determineBasePower(Pokemon foe, Move move, boolean first, Field field, Pokemon[] team) {
		int bp = 0;
		if (move == Move.BRINE) {
			if (foe.currentHP / foe.getStat(0) > 0.5) {
				bp = 65;
			} else {
				bp = 130;
			}
		} else if (move == Move.COMET_CRASH) {
			if (this.currentHP == this.getStat(0)) {
				bp = 160;
			} else {
				bp = 80;
			}
		} else if (move == Move.ELECTRO_BALL) {
			double speedRatio = foe.getSpeed() * 1.0 / this.getSpeed();
			if (speedRatio > 1) {
				bp = 40;
			} else if (speedRatio >= 0.501 && speedRatio <= 1) {
				bp = 60;
			} else if (speedRatio >= 0.3334 && speedRatio < 0.501) {
				bp = 80;
			} else if (speedRatio >= 0.2501 && speedRatio < 0.3334) {
				bp = 120;
			} else if (speedRatio < 0.2501) {
				bp = 150;
			}
		} else if (move == Move.ERUPTION) {
			double hpRatio = this.currentHP * 1.0 / this.getStat(0);
			hpRatio *= 150;
			bp = Math.max((int) hpRatio, 1);
		} else if (move == Move.EXPANDING_FORCE) {
			if (field.equals(field.terrain, Effect.PSYCHIC) && this.isGrounded(field, foe)) {
				bp = 120;
			} else {
				bp = 80;
			}
		} else if (move == Move.FACADE) {
			if (this.status == Status.HEALTHY) {
				bp = 70;
			} else {
				bp = 140;
			}
		} else if (move == Move.FLAIL || move == Move.REVERSAL) {
			double hpRatio = this.currentHP / this.getStat(0);
			if (hpRatio >= 0.6875) {
				bp = 20;
			} else if (hpRatio >= 0.3542 && hpRatio < 0.6875) {
				bp = 40;
			} else if (hpRatio >= 0.2083 && hpRatio < 0.3542) {
				bp = 80;
			} else if (hpRatio >= 0.1042 && hpRatio < 0.2083) {
				bp = 100;
			} else if (hpRatio >= 0.0417 && hpRatio < 0.1042) {
				bp = 150;
			} else if (hpRatio < 0.0417) {
				bp = 200;
			}
		} else if (move == Move.FURY_CUTTER) {
			if (this.lastMoveUsed == Move.FURY_CUTTER) {
				this.moveMultiplier *= 2;
			}
			bp = Math.min(160, 20 * this.moveMultiplier);
		} else if (move == Move.GRASS_KNOT || move == Move.LOW_KICK) {
			if (foe.weight < 21.9) {
				bp = 20;
			} else if (foe.weight >= 21.9 && foe.weight < 55.1) {
				bp = 40;
			} else if (foe.weight >= 55.1 && foe.weight < 110.2) {
				bp = 60;
			} else if (foe.weight >= 110.2 && foe.weight < 220.4) {
				bp = 80;
			} else if (foe.weight >= 220.4 && foe.weight < 440.9) {
				bp = 100;
			} else if (foe.weight >= 440.9) {
				bp = 120;
			}
		} else if (move == Move.GYRO_BALL) {
			double speedRatio = foe.getSpeed() * 1.0 / this.getSpeed();
			bp = (int) Math.min(150, (25 * speedRatio) + 1);
		} else if (move == Move.HEAT_CRASH || move == Move.HEAVY_SLAM) {
			double weightRatio = foe.weight / this.weight;
			if (weightRatio > 0.5) {
				bp = 40;
			} else if (weightRatio >= 0.3335 && weightRatio <= 0.5) {
				bp = 60;
			} else if (weightRatio >= 0.2501 && weightRatio <= 0.3334) {
				bp = 80;
			} else if (weightRatio >= 0.2001 && weightRatio <= 0.25) {
				bp = 100;
			} else if (weightRatio <= 0.2) {
				bp = 120;
			}
		} else if (move == Move.HEX) {
			if (foe.status == Status.HEALTHY) {
				bp = 65;
			} else {
				bp = 130;
			}
		} else if (move == Move.MAGNITUDE) {
			int mag = (int) (Math.random()*100 + 1);
			if (mag <= 5) {
				bp = 10;
				System.out.println("Magnitude 4!");
			} else if (mag <= 15) {
				bp = 30;
				System.out.println("Magnitude 5!");
			} else if (mag <= 35) {
				bp = 50;
				System.out.println("Magnitude 6!");
			} else if (mag <= 65) {
				bp = 70;
				System.out.println("Magnitude 7!");
			} else if (mag <= 85) {
				bp = 90;
				System.out.println("Magnitude 8!");
			} else if (mag <= 95) {
				bp = 110;
				System.out.println("Magnitude 9!");
			} else if (mag <= 100) {
				bp = 150;
				System.out.println("Magnitude 10!");
			}
		} else if (move == Move.RAGE) {
			if (this.lastMoveUsed == Move.RAGE) {
				this.moveMultiplier *= 2;
			}
			bp = Math.min(160, 20 * this.moveMultiplier);
		} else if (move == Move.PAYBACK) {
			if (first) {
				bp = 40;
			} else {
				bp = 80;
			}
		} else if (move == Move.RETURN || move == Move.FRUSTRATION) {
			int f = this.happiness;
			if (move == Move.FRUSTRATION) f = 255 - f;
			bp = Math.max(f * 5 / 2, 1);
		} else if (move == Move.REVENGE) {
			if (this.getSpeed() > foe.getSpeed()) {
				bp = 60;
			} else {
				bp = 120;
			}
		} else if (move == Move.ROLLOUT) {
			bp = (int) (30 * Math.pow(2, this.rollCount-1));
		} else if (move == Move.WAKE_UP_SLAP) {
			bp = 60;
			if (foe.status == Status.ASLEEP) {
				bp = 120;
				foe.sleepCounter = 0;
			}
		} else if (move == Move.VENOSHOCK) {
			if (foe.status == Status.POISONED) {
				bp = 130;
			} else {
				bp = 65;
			}
		} else if (move == Move.WATER_SPOUT) {
			double hpRatio = this.currentHP * 1.0 / this.getStat(0);
			hpRatio *= 150;
			bp = Math.max((int) hpRatio, 1);
		} else if (move == Move.WEATHER_BALL) {
			bp = field.weather != null ? 100 : 50;
			
		} else if (move == Move.TERRAIN_PULSE) {
			bp = field.terrain != null ? 100 : 50;
		}
		
		return bp;
	}
	
	private void removeBad(ArrayList<Status> stati) {
		stati.remove(Status.CONFUSED);
		stati.remove(Status.CURSED);
		stati.remove(Status.LEECHED);
		stati.remove(Status.NIGHTMARE);
		stati.remove(Status.FLINCHED);
		stati.remove(Status.SPUN);
	}

	public boolean knowsMove(Move move) {
		for (Move m : moveset) {
			if (m == move) {
				return true;
			}
		}
		return false;
	}
	
	public boolean trainerOwned() {
		return this.trainer == 1.5;
	}

	public int[] getIVs() {
		return ivs;
	}
	
	private void setNature() {
		nature = new double[] {1,1,1,1,1,-1};
		Random random = new Random();
        int increaseIndex = random.nextInt(5);
        int decreaseIndex = random.nextInt(5);
        
        if (increaseIndex == decreaseIndex) {
        	nature[5] = increaseIndex;
        } else {
        	nature[decreaseIndex] = 0.9; // Decreased stat multiplied by 0.9
            nature[increaseIndex] = 1.1; // Increased stat multiplied by 1.1
        }
	}
	
	public String getNature() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < nature.length; i++) {
			s.append(nature[i]);
			if (i != nature.length - 1) s.append(",");
		}
		String ns = s.toString();
		String natureString;
		switch (ns) {
		case "1.0,1.0,1.0,1.0,1.0,0.0":
			natureString = "Hardy";
			break;
		case "1.0,1.0,1.0,1.0,1.0,1.0":
			natureString = "Docile";
			break;
		case "1.0,1.0,1.0,1.0,1.0,2.0":
			natureString = "Bashful";
			break;
		case "1.0,1.0,1.0,1.0,1.0,3.0":
			natureString = "Quirky";
			break;
		case "1.0,1.0,1.0,1.0,1.0,4.0":
			natureString = "Serious";
			break;
		case "1.1,0.9,1.0,1.0,1.0,-1.0":
			natureString = "Lonely";
			break;
		case "1.1,1.0,0.9,1.0,1.0,-1.0":
			natureString = "Adamant";
			break;
		case "1.1,1.0,1.0,0.9,1.0,-1.0":
			natureString = "Naughty";
			break;
		case "1.1,1.0,1.0,1.0,0.9,-1.0":
			natureString = "Brave";
			break;
		case "0.9,1.1,1.0,1.0,1.0,-1.0":
			natureString = "Bold";
			break;
		case "1.0,1.1,0.9,1.0,1.0,-1.0":
			natureString = "Impish";
			break;
		case "1.0,1.1,1.0,0.9,1.0,-1.0":
			natureString = "Lax";
			break;
		case "1.0,1.1,1.0,1.0,0.9,-1.0":
			natureString = "Relaxed";
			break;
		case "0.9,1.0,1.1,1.0,1.0,-1.0":
			natureString = "Modest";
			break;
		case "1.0,0.9,1.1,1.0,1.0,-1.0":
			natureString = "Mild";
			break;
		case "1.0,1.0,1.1,0.9,1.0,-1.0":
			natureString = "Rash";
			break;
		case "1.0,1.0,1.1,1.0,0.9,-1.0":
			natureString = "Quiet";
			break;
		case "0.9,1.0,1.0,1.1,1.0,-1.0":
			natureString = "Calm";
			break;
		case "1.0,0.9,1.0,1.1,1.0,-1.0":
			natureString = "Gentle";
			break;
		case "1.0,1.0,0.9,1.1,1.0,-1.0":
			natureString = "Careful";
			break;
		case "1.0,1.0,1.0,1.1,0.9,-1.0":
			natureString = "Sassy";
			break;
		case "0.9,1.0,1.0,1.0,1.1,-1.0":
			natureString = "Timid";
			break;
		case "1.0,0.9,1.0,1.0,1.1,-1.0":
			natureString = "Hasty";
			break;
		case "1.0,1.0,0.9,1.0,1.1,-1.0":
			natureString = "Jolly";
			break;
		case "1.0,1.0,1.0,0.9,1.1,-1.0":
			natureString = "Naive";
			break;
		default:
			natureString = ns;
			break;
		}
			
//		natureString += " (";
//		int increasedStat, decreasedStat;
//		increasedStat = decreasedStat = -1;
//		for (int i = 0; i < nature.length; i++) {
//			if (nature[i] == 1.1) increasedStat = i;
//			if (nature[i] == 0.9) decreasedStat = i;
//		}
//		if (increasedStat != -1 || decreasedStat != -1) {
//			natureString += "+" + getStatName(increasedStat) + ",-" + getStatName(decreasedStat);
//		} else {
//			natureString += "Neutral";
//		}
//		natureString += ")";
		
		return natureString;
	}



	public JPanel showSummary() {
	    JPanel teamMemberPanel = new JPanel();
	    teamMemberPanel.setLayout(new BoxLayout(teamMemberPanel, BoxLayout.Y_AXIS));
	    
	    JLabel spriteLabel = new JLabel();
	    ImageIcon spriteIcon = new ImageIcon(this.getSprite());
	    spriteLabel.setIcon(spriteIcon);

	    JLabel nameLabel, nicknameLabel, abilityLabel, abilityDescLabel, natureLabel, hpLabel, statusLabel;
	    nameLabel = nicknameLabel = abilityLabel = abilityDescLabel = natureLabel = hpLabel = statusLabel = new JLabel("N/A");
	    JLabel[] stats = new JLabel[6];
	    JLabel[] ivs = new JLabel[6];
	    JProgressBar[] bars = new JProgressBar[6];
	    JPanel labelPanel = new JPanel(new GridLayout(6, 2));
	    JPanel barPanel = new JPanel(new GridLayout(6, 1));
	    JPanel statsPanel = new JPanel(new GridLayout(1, 2));
	    JGradientButton type1B, type2B;
	    JPanel movesPanel = new JPanel(new GridLayout(2, 2));
	    type1B = new JGradientButton("");
	    type2B = new JGradientButton("");
	    if (this != null) {
	        nameLabel = new JLabel(this.name + " Lv. " + this.getLevel());
	        nameLabel.setForeground(this.type1.getColor().darker());
	        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 16));
	        nicknameLabel = new JLabel(this.nickname);
	        nicknameLabel.setFont(new Font(nicknameLabel.getFont().getName(), Font.BOLD, 18));
	        hpLabel = new JLabel(this.currentHP + " / " + this.getStat(0) + " HP");
	        hpLabel.setFont(new Font(hpLabel.getFont().getName(), Font.BOLD, 14));
	        type1B.setText(this.type1.toString());
	        type1B.setBackground(this.type1.getColor());
	        if (this.type2 != null) {
	            type2B.setText(this.type2.toString());
	            type2B.setBackground(this.type2.getColor());
	        }
	        
	        for (int i = 0; i < 6; i++) {
	        	String type;
	        	switch (i) {
	        	case 0:
	        		type = "HP ";
	        		break;
	        	case 1:
	        		type = "Atk ";
	        		break;
	        	case 2:
	        		type = "Def ";
	        		break;
	        	case 3:
	        		type = "SpA ";
	        		break;
	        	case 4:
	        		type = "SpD ";
	        		break;
	        	case 5:
	        		type = "Spe ";
	        		break;
        		default:
        			type = "ERROR ";
        			break;
	        	}
	        	stats[i] = new JLabel(type + this.stats[i]);
	        	stats[i].setFont(new Font(stats[i].getFont().getName(), Font.BOLD, 14));
	        	stats[i].setSize(50, stats[i].getHeight());
	        	
	        	if (i != 0) {
	        		if (this.nature[i-1] == 1.1) stats[i].setForeground(Color.red.darker().darker());
	    	        if (this.nature[i-1] == 0.9) stats[i].setForeground(Color.blue.darker().darker());
	        	}
	        	
	        	statsPanel.add(stats[i]);
	        	
	        	ivs[i] = new JLabel("IV: " + this.getIVs()[i]);
	        	
	        	labelPanel.add(stats[i]);
	        	labelPanel.add(ivs[i]);
	        	
	        	bars[i] = new JProgressBar();
	        	bars[i].setMaximum(200);
	        	bars[i].setValue(this.getBaseStat(i));
	        	bars[i].setString(this.getBaseStat(i) + "");
	        	bars[i].setUI(new CustomProgressBarUI(Color.BLACK));
	        	bars[i].setStringPainted(true);
	        	bars[i].setForeground(getColor(this.getBaseStat(i)));
	        	
	        	barPanel.add(bars[i]);
	        	
	        }
	        
	        abilityLabel = new JLabel("Ability: " + this.ability.toString());
	        abilityDescLabel = new JLabel(this.ability.desc);
	        abilityLabel.setFont(new Font(hpLabel.getFont().getName(), Font.BOLD, 14));
	        natureLabel = new JLabel(this.getNature() + " Nature");

	        for (int i = 0; i < 4; i++) {
	        	JButton moveButton = new JGradientButton("");
	            if (moveset[i] != null) {
	                moveButton.setText(moveset[i].toString());
	                moveButton.setBackground(moveset[i].mtype.getColor());
	                int index = i;
	                moveButton.addActionListener(e -> {
	                	String message = "Move: " + moveset[index].toString() + "\n";
			            message += "Type: " + moveset[index].mtype + "\n";
			            message += "BP: " + moveset[index].getbp() + "\n";
			            message += "Accuracy: " + moveset[index].accuracy + "\n";
			            message += "Category: " + moveset[index].getCategory() + "\n";
			            message += "Description: " + moveset[index].getDescription();
			            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
	                });
	            }
	            movesPanel.add(moveButton);
	        }
	        statusLabel = (this.isFainted()) ? new JLabel("Status: FAINTED") : new JLabel("Status: " + this.status.toString());
	        if (!this.isFainted() && this.status == Status.HEALTHY) {
	            statusLabel.setForeground(Color.GREEN.darker());
	        } else if (this.isFainted()) {
	            statusLabel.setForeground(Color.RED.darker());
	        } else {
	            statusLabel.setForeground(this.status.getColor());
	        }
	    }
	    
	    JPanel nicknameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    nicknameLabelPanel.add(nicknameLabel);
	    teamMemberPanel.add(nicknameLabelPanel);

	    JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    nameLabelPanel.add(nameLabel);
	    teamMemberPanel.add(nameLabelPanel);
	    
	    JPanel spriteLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    nameLabelPanel.add(spriteLabel);
	    teamMemberPanel.add(spriteLabelPanel);

	    JPanel hpLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    hpLabelPanel.add(hpLabel);
	    teamMemberPanel.add(hpLabelPanel);

	    JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    typesPanel.add(type1B);
	    typesPanel.add(type2B);
	    teamMemberPanel.add(typesPanel);
	    
	    JPanel abilityLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    abilityLabelPanel.add(abilityLabel);
	    teamMemberPanel.add(abilityLabelPanel);
	    
	    JPanel abilityDescLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    abilityDescLabelPanel.add(abilityDescLabel);
	    teamMemberPanel.add(abilityDescLabelPanel);

	    JPanel natureLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    natureLabelPanel.add(natureLabel);
	    teamMemberPanel.add(natureLabelPanel);
	    
	    statsPanel.add(labelPanel);
	    statsPanel.add(barPanel);
	    teamMemberPanel.add(statsPanel);
	    
	    teamMemberPanel.add(movesPanel);
	    
	    JPanel statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    statusLabelPanel.add(statusLabel);
	    teamMemberPanel.add(statusLabelPanel);
	    
	    teamMemberPanel.add(Box.createHorizontalGlue());

	    return teamMemberPanel;
	}
	
	public JPanel getDexSummary(int id, int pokedex) {
	    JPanel dexPanel = new JPanel();
	    dexPanel.setLayout(new BoxLayout(dexPanel, BoxLayout.Y_AXIS));
	    
	    JLabel spriteLabel = new JLabel();
	    ImageIcon spriteIcon = new ImageIcon(getSprite(id));
	    spriteLabel.setIcon(spriteIcon);

	    JLabel dexNo, nameLabel, abilityLabel, bst;
	    nameLabel = abilityLabel = bst = dexNo = new JLabel("???");
	    JProgressBar[] bars = new JProgressBar[6];
	    JPanel barPanel = new JPanel(new GridLayout(6, 1));
	    JLabel[] abilitiesDesc = new JLabel[4];
	    JGradientButton type1B, type2B;
	    JPanel movesPanel = new JPanel();
	    type1B = new JGradientButton("");
	    type2B = new JGradientButton("");
	    if (this != null) {
	        nameLabel = new JLabel(getName(id));
	        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 16));
	        
	        PType type1 = getType1(id);
	        PType type2 = getType2(id);
	        type1B.setText(type1.toString());
	        type1B.setBackground(type1.getColor());
	        if (type2 != null) {
	            type2B.setText(type2.toString());
	            type2B.setBackground(type2.getColor());
	        }
	        
	        
	        Pokemon test = new Pokemon(1, 1, false, false);
	        test.id = id;
	        test.setBaseStats();
	        
	        for (int i = 0; i < 6; i++) {
	        	String type;
	        	switch (i) {
	        	case 0:
	        		type = "HP ";
	        		break;
	        	case 1:
	        		type = "Atk ";
	        		break;
	        	case 2:
	        		type = "Def ";
	        		break;
	        	case 3:
	        		type = "SpA ";
	        		break;
	        	case 4:
	        		type = "SpD ";
	        		break;
	        	case 5:
	        		type = "Spe ";
	        		break;
        		default:
        			type = "ERROR ";
        			break;
	        	}
	        	
	        	bars[i] = new JProgressBar();
	        	bars[i].setMaximum(200);
	        	bars[i].setValue(test.getBaseStat(i));
	        	bars[i].setString(type + ": " + test.getBaseStat(i) + "");
	        	bars[i].setUI(new CustomProgressBarUI(Color.BLACK));
	        	bars[i].setStringPainted(true);
	        	bars[i].setForeground(getColor(test.getBaseStat(i)));
	        	
	        	barPanel.add(bars[i]);
	        	
	        	bst = new JLabel("TOTAL : " + test.getBST());
	        	bst.setFont(new Font(bst.getFont().getName(), Font.BOLD, 14));
	        	
	        }
	        
	        dexNo = new JLabel("No. " + id);
	        dexNo.setFont(new Font(dexNo.getFont().getName(), Font.BOLD, 18));
	        
	        abilityLabel = new JLabel("Abilities:");
	        abilityLabel.setFont(new Font(abilityLabel.getFont().getName(), Font.ITALIC, 18));

			for (int i = 0; i < 4; i++) {
				if (i % 2 == 1) {
					JLabel abDesc = new JLabel(test.ability.desc);
					abilitiesDesc[i] = abDesc;
				} else {
					test.setAbility(i / 2);
					JLabel abLab = new JLabel(test.ability.toString());
					abLab.setFont(new Font(abLab.getFont().getName(), Font.BOLD, 14));
					abilitiesDesc[i] = abLab;
				}
			}
			
			test.setMoveBank();
			ArrayList<Move> moves = new ArrayList<>();
			ArrayList<Integer> moveLevels = new ArrayList<>();
			if (test.movebank != null) {
	            for (int i = 0; i < test.movebank.length; i++) {
            		Node move = test.movebank[i];
            		while (move != null) {
            			moves.add(move.data);
            			moveLevels.add(i + 1);
            			move = move.next;
            		}
	            }
	    	}
			
			
		    movesPanel.setLayout(new GridLayout(moves.size(), 1));

		    for (int i = 0; i < moves.size(); i++) {
		        Move move = moves.get(i);
		        int level = moveLevels.get(i); // Get the level for the corresponding move

		        JButton moveButton = new JGradientButton("");
		        if (move != null) {
		            String levelText = String.format("%3d ", level); // Format the level text with padding
		            moveButton.setText(levelText + move.toString());
		            moveButton.setHorizontalAlignment(SwingConstants.LEFT);
		            moveButton.setBackground(move.mtype.getColor());
		            moveButton.addActionListener(f -> {
		                String message = "Move: " + move.toString() + "\n";
		                message += "Type: " + move.mtype + "\n";
		                message += "BP: " + move.getbp() + "\n";
		                message += "Accuracy: " + move.accuracy + "\n";
		                message += "Category: " + move.getCategory() + "\n";
		                message += "Description: " + move.getDescription();
		                JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
		            });
		        }
		        movesPanel.add(moveButton);
		    }
	    }
	    
	    if (pokedex == 0) {
	    	nameLabel.setText("???");
	    	spriteLabel.setIcon(null);
	    	type1B.setText("???");
	    	type1B.setBackground(Color.WHITE);
	    	type2B.setText("???");
	    	type2B.setBackground(Color.WHITE);
	    }
	    
	    JPanel dexNoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    dexNoPanel.add(dexNo);
	    dexPanel.add(dexNoPanel);

	    JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    nameLabelPanel.add(nameLabel);
	    dexPanel.add(nameLabelPanel);
	    
	    JPanel spriteLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    nameLabelPanel.add(spriteLabel);
	    dexPanel.add(spriteLabelPanel);

	    JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    typesPanel.add(type1B);
	    typesPanel.add(type2B);
	    dexPanel.add(typesPanel);

	    JPanel abilityLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    abilityLabelPanel.add(abilityLabel);
	    dexPanel.add(abilityLabelPanel);
	    for (JLabel j : abilitiesDesc) {
	    	JPanel abilitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    	if (pokedex < 2) {
	    		j.setText("???");
	    		if (j.getFont().getSize() != 14) abilitiesPanel.add(j);
	    	} else {
	    		abilitiesPanel.add(j);
	    	}
	    	dexPanel.add(abilitiesPanel);
	    }
	    
	    if (pokedex == 2) {
	    	dexPanel.add(barPanel);
	    	JPanel bstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    bstPanel.add(bst);
		    dexPanel.add(bstPanel);
		    
		    // Wrap the movesPanel in a JScrollPane
		    JScrollPane movesScrollPane = new JScrollPane(movesPanel);

		    // Set preferred size of the JScrollPane to make it scrollable
		    movesScrollPane.setPreferredSize(new Dimension(100, 200));
		    movesScrollPane.getVerticalScrollBar().setUnitIncrement(12); // Adjust the value as needed

		    dexPanel.add(movesScrollPane);
	    }

	    dexPanel.add(Box.createHorizontalGlue());

	    return dexPanel;
	}
	
	public static Color getColor(int value) {
        if (value <= 50) {
            return fadeBetweenColors(Color.RED, Color.YELLOW, value / 50.0f);
        } else if (value <= 100) {
            return fadeBetweenColors(Color.YELLOW, Color.GREEN, (value - 50) / 50.0f);
        } else if (value <= 150) {
            return fadeBetweenColors(Color.GREEN, new Color(0, 175, 255), (value - 100) / 50.0f);
        } else {
            return Color.BLUE;
        }
    }

    private static Color fadeBetweenColors(Color startColor, Color endColor, float percentage) {
        int r = (int) (startColor.getRed() + percentage * (endColor.getRed() - startColor.getRed()));
        int g = (int) (startColor.getGreen() + percentage * (endColor.getGreen() - startColor.getGreen()));
        int b = (int) (startColor.getBlue() + percentage * (endColor.getBlue() - startColor.getBlue()));
        return new Color(r, g, b);
    }

	private PType determineHPType() {
		int sum = 0;
		for (int i = 0; i < 6; i++) {
			int iv = this.ivs[i];
			int bit = iv % 2 == 0 ? 0 : 1;
			sum += bit << i;
		}
		int index = (sum * 18) / 63;
		PType[] types = PType.values();
		return types[++index];
	}
	
	private PType determineWBType(Field field) {
		PType result = PType.NORMAL;
		if (field.equals(field.weather, Effect.SUN)) result = PType.FIRE;
		if (field.equals(field.weather, Effect.RAIN)) result = PType.WATER;
		if (field.equals(field.weather, Effect.SNOW)) result = PType.ICE;
		if (field.equals(field.weather, Effect.SANDSTORM)) result = PType.ROCK;
		
		return result;
	}
	
	private PType determineTPType(Field field) {
		PType result = PType.NORMAL;
		if (field.equals(field.terrain, Effect.GRASSY)) result = PType.GRASS;
		if (field.equals(field.terrain, Effect.ELECTRIC)) result = PType.ELECTRIC;
		if (field.equals(field.terrain, Effect.PSYCHIC)) result = PType.PSYCHIC;
		if (field.equals(field.terrain, Effect.SPARKLY)) result = PType.MAGIC;
		
		return result;
	}




	public void swapIn(Pokemon foe, Field field, Player me) {
		if (this.ability == Ability.DROUGHT) { field.setWeather(field.new FieldEffect(Effect.SUN));
		} else if (this.ability == Ability.DRIZZLE) { field.setWeather(field.new FieldEffect(Effect.RAIN));
		} else if (this.ability == Ability.SAND_STREAM) { field.setWeather(field.new FieldEffect(Effect.SANDSTORM));
		} else if (this.ability == Ability.SNOW_WARNING) { field.setWeather(field.new FieldEffect(Effect.SNOW));
		
		} else if (this.ability == Ability.GRASSY_SURGE) { field.setTerrain(field.new FieldEffect(Effect.GRASSY));
		} else if (this.ability == Ability.ELECTRIC_SURGE) { field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
		} else if (this.ability == Ability.PSYCHIC_SURGE) { field.setTerrain(field.new FieldEffect(Effect.PSYCHIC));
		} else if (this.ability == Ability.SPARKLY_SURGE) { field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
		
		} else if (this.ability == Ability.GRAVITATIONAL_PULL) { field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		
		} else if (this.ability == Ability.INTIMIDATE) { System.out.print("[" + this.nickname + "'s Intimidate]: "); stat(foe, 0, -1);
		} else if (this.ability == Ability.MOUTHWATER) { foe.vStatuses.add(Status.TAUNTED);
		} else if (this.ability == Ability.REGENERATOR) {
			this.currentHP += this.getStat(0);
			verifyHP();
		} else if (this.ability == Ability.ANTICIPATION) {
			boolean shuddered = false;
			for (Move move : foe.moveset) {
				if (shuddered) break;
				if (move != null) {
					int multiplier = getEffectiveMultiplier(move.mtype, this);
					
					if (multiplier > 1) shuddered = true;
				}
			}
			if (shuddered) System.out.println("[" + this.nickname + "'s Anticipation]: " + this.nickname + " shuddered!");
		}
		if (me.pokedex[this.id] < 1) me.pokedex[this.id] = 1;
		
	}
	
	private int getEffectiveMultiplier(PType mtype, Pokemon pokemon) {
		int multiplier = 1;
		
		if (getImmune(pokemon, mtype)) {
			multiplier = 0;
		} else {
			PType[] resistances = getResistances(mtype);
	        for (PType resistance : resistances) {
	            if (pokemon.type1 == resistance || pokemon.type2 == resistance) {
	                multiplier /= 2;
	            }
	        }
	        
	        PType[] weaknesses = getWeaknesses(mtype);
	        for (PType weakness : weaknesses) {
	            if (pokemon.type1 == weakness || pokemon.type2 == weakness) {
	                multiplier *= 2;
	            }
	        }
		}
		return multiplier;
	}

	private boolean isGrounded(Field field, Pokemon foe) {
		boolean result = true;
		if (this.type1 == PType.FLYING || this.type2 == PType.FLYING) result = false;
		if (this.ability == Ability.LEVITATE) result = false;
		if (foe.magCount > 0) result = false;
		if (this.vStatuses.contains(Status.SMACK_DOWN)) result = true;
		if (field.contains(field.fieldEffects, Effect.GRAVITY)) result = true;
		return result;
	}
	
	public int displayMoveOptions(Move move) {
		Pokemon pokemon = this;
	    String[] moves = new String[4];
	    JGradientButton[] buttons = new JGradientButton[4];
	    JPanel panel = new JPanel();
	    int[] choice = new int[1];
	    choice[0] = -1;
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    JLabel label = new JLabel(pokemon.nickname + " wants to learn " + move.toString() + ".");
	    JLabel label2 = new JLabel("Select a move to replace:");
	    JGradientButton learnButton = new JGradientButton(move.toString());
	    learnButton.setBackground(move.mtype.getColor());
	    panel.add(label);
	    panel.add(learnButton);
	    panel.add(label2);
	    for (int i = 0; i < 4; i++) {
	        if (pokemon.moveset[i] != null) {
	            moves[i] = pokemon.moveset[i].toString();
	        } else {
	            moves[i] = "";
	        }
	        buttons[i] = new JGradientButton(moves[i]);
	        if (pokemon.moveset[i] != null) buttons[i].setBackground(pokemon.moveset[i].mtype.getColor());
	        
	        if (moves[i].equals("")) {
	            buttons[i].setEnabled(false);
	        }
	        int index = i;
	        buttons[i].addMouseListener(new MouseAdapter() {
	        	@Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            String message = "Move: " + pokemon.moveset[index].toString() + "\n";
			            message += "Type: " + pokemon.moveset[index].mtype + "\n";
			            message += "BP: " + pokemon.moveset[index].getbp() + "\n";
			            message += "Accuracy: " + pokemon.moveset[index].accuracy + "\n";
			            message += "Category: " + pokemon.moveset[index].getCategory() + "\n";
			            message += "Description: " + pokemon.moveset[index].getDescription();
			            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        } else {
			        	choice[0] = index;
		                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor((JButton) e.getSource());
		                dialog.dispose();
			        }
			    }
	        });
	        panel.add(buttons[i]);
	    }
	    learnButton.addMouseListener(new MouseAdapter() {
        	@Override
		    public void mouseClicked(MouseEvent e) {
		    	if (SwingUtilities.isRightMouseButton(e)) {
		            String message = "Move: " + move.toString() + "\n";
		            message += "Type: " + move.mtype + "\n";
		            message += "BP: " + move.getbp() + "\n";
		            message += "Accuracy: " + move.accuracy + "\n";
		            message += "Category: " + move.getCategory() + "\n";
		            message += "Description: " + move.getDescription();
		            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
		        } else {
		        	choice[0] = JOptionPane.CLOSED_OPTION;
	                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor((JButton) e.getSource());
	                dialog.dispose();
		        }
		    }
        });

	    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
	    JDialog dialog = optionPane.createDialog("Learn New Move");
	    dialog.setVisible(true);
	    int result = choice[0];
	    return result == JOptionPane.CLOSED_OPTION ? JOptionPane.CLOSED_OPTION : choice[0];
	}
	
	private Move get150Move(Move move) {
		if (move == Move.SPARKLING_WATER) return Move.SPARKLING_ARIA;
		if (move == Move.WATER_FLICK) return Move.FLAMETHROWER;
		if (move == Move.WATER_SMACK) return Move.DARKEST_LARIAT;
		if (move == Move.WATER_CLAP) return Move.DRAGON_DARTS;
		if (move == Move.WATER_KICK) return Move.HI_JUMP_KICK;
		if (move == Move.SUPERCHARGED_SPLASH) return Move.THUNDER;
		if (move == Move.DEEP_SEA_BUBBLE) return Move.DRACO_METEOR;
		return move;
	}
	
	public double setWeight() {
		if (id == 1) { return 52.5;
		} else if (id == 2) { return 310.5;
		} else if (id == 3) { return 476.0;
		} else if (id == 4) { return 13.4;
		} else if (id == 5) { return 75.6;
		} else if (id == 6) { return 125.2;
		} else if (id == 7) { return 9.5;
		} else if (id == 8) { return 45.0;
		} else if (id == 9) { return 195.6;
		} else if (id == 10) { return 2.0;
		} else if (id == 11) { return 5.0;
		} else if (id == 12) { return 30.5;
		} else if (id == 13) { return 5.5;
		} else if (id == 14) { return 40.9;
		} else if (id == 15) { return 129.2;
		} else if (id == 16) { return 95.6;
		} else if (id == 17) { return 445.9;
		} else if (id == 18) { return 205.0;
		} else if (id == 19) { return 65.7;
		} else if (id == 20) { return 65.8;
		} else if (id == 21) { return 90.9;
		} else if (id == 22) { return 0.9;
		} else if (id == 23) { return 2.0;
		} else if (id == 24) { return 50.7;
		} else if (id == 25) { return 90.5;
		} else if (id == 26) { return 100.5;
		} else if (id == 27) { return 399.9;
		} else if (id == 28) { return 412.5;
		} else if (id == 29) { return 2.6;
		} else if (id == 30) { return 4.4;
		} else if (id == 31) { return 32.0;
		} else if (id == 32) { return 5.5;
		} else if (id == 33) { return 16.1;
		} else if (id == 34) { return 45.2;
		} else if (id == 35) { return 9.7;
		} else if (id == 36) { return 23.1;
		} else if (id == 37) { return 99.2;
		} else if (id == 38) { return 15.8;
		} else if (id == 39) { return 87.7;
		} else if (id == 40) { return 55.6;
		} else if (id == 41) { return 6.0;
		} else if (id == 42) { return 45.5;
		} else if (id == 43) { return 90.1;
		} else if (id == 44) { return 5.7;
		} else if (id == 45) { return 71.7;
		} else if (id == 46) { return 121.3;
		} else if (id == 47) { return 7.3;
		} else if (id == 48) { return 104.4;
		} else if (id == 49) { return 256.2;
		} else if (id == 50) { return 337.2;
		} else if (id == 51) { return 370.5;
		} else if (id == 52) { return 140.3;
		} else if (id == 53) { return 173.2;
		} else if (id == 54) { return 252.9;
		} else if (id == 55) { return 40.6;
		} else if (id == 56) { return 190.0;
		} else if (id == 57) { return 62.5;
		} else if (id == 58) { return 190.3;
		} else if (id == 59) { return 4.5;
		} else if (id == 60) { return 89.5;
		} else if (id == 61) { return 100.0;
		} else if (id == 62) { return 8.4;
		} else if (id == 63) { return 92.6;
		} else if (id == 64) { return 135.6;
		} else if (id == 65) { return 208.1;
		} else if (id == 66) { return 95.4;
		} else if (id == 67) { return 900.9;
		} else if (id == 68) { return 87.1;
		} else if (id == 69) { return 193.1;
		} else if (id == 70) { return 332.0;
		} else if (id == 71) { return 9.9;
		} else if (id == 72) { return 25.9;
		} else if (id == 73) { return 7.5;
		} else if (id == 74) { return 18.5;
		} else if (id == 75) { return 7.5;
		} else if (id == 76) { return 10.6;
		} else if (id == 77) { return 11.2;
		} else if (id == 78) { return 16.6;
		} else if (id == 79) { return 41.0;
		} else if (id == 80) { return 13.1;
		} else if (id == 81) { return 115.5;
		} else if (id == 82) { return 75.0;
		} else if (id == 83) { return 175.0;
		} else if (id == 84) { return 275.0;
		} else if (id == 85) { return 14.6;
		} else if (id == 86) { return 44.5;
		} else if (id == 87) { return 106.7;
		} else if (id == 88) { return 114.6;
		} else if (id == 89) { return 250.4;
		} else if (id == 90) { return 7.7;
		} else if (id == 91) { return 103.6;
		} else if (id == 92) { return 21.0;
		} else if (id == 93) { return 93.6;
		} else if (id == 94) { return 10.0;
		} else if (id == 95) { return 145.6;
		} else if (id == 96) { return 195.0;
		} else if (id == 97) { return 165.3;
		} else if (id == 98) { return 290.2;
		} else if (id == 99) { return 301.3;
		} else if (id == 100) { return 345.6;
		} else if (id == 101) { return 28.9;
		} else if (id == 102) { return 199.3;
		} else if (id == 103) { return 450.9;
		} else if (id == 104) { return 23.8;
		} else if (id == 105) { return 77.2;
		} else if (id == 106) { return 3.5;
		} else if (id == 107) { return 5.5;
		} else if (id == 108) { return 16.0;
		} else if (id == 109) { return 295.5;
		} else if (id == 110) { return 125.0;
		} else if (id == 111) { return 11.0;
		} else if (id == 112) { return 56.1;
		} else if (id == 113) { return 65.9;
		} else if (id == 114) { return 40.1;
		} else if (id == 115) { return 76.0;
		} else if (id == 116) { return 91.3;
		} else if (id == 117) { return 3.6;
		} else if (id == 118) { return 13.4;
		} else if (id == 119) { return 245.9;
		} else if (id == 120) { return 50.5;
		} else if (id == 121) { return 125.7;
		} else if (id == 122) { return 130.6;
		} else if (id == 123) { return 8.1;
		} else if (id == 124) { return 25.1;
		} else if (id == 125) { return 26.0;
		} else if (id == 126) { return 35.6;
		} else if (id == 127) { return 100.9;
		} else if (id == 128) { return 145.1;
		} else if (id == 129) { return 12.1;
		} else if (id == 130) { return 26.5;
		} else if (id == 131) { return 2.6;
		} else if (id == 132) { return 9.5;
		} else if (id == 133) { return 65.7;
		} else if (id == 134) { return 9.9;
		} else if (id == 135) { return 41.8;
		} else if (id == 136) { return 129.5;
		} else if (id == 137) { return 22.0;
		} else if (id == 138) { return 518.1;
		} else if (id == 139) { return 76.1;
		} else if (id == 140) { return 176.4;
		} else if (id == 141) { return 410.9;
		} else if (id == 142) { return 476.0;
		} else if (id == 143) { return 21.0;
		} else if (id == 144) { return 835.9;
		} else if (id == 145) { return 954.5;
		} else if (id == 146) { return 68.3;
		} else if (id == 147) { return 211.6;
		} else if (id == 148) { return 6.8;
		} else if (id == 149) { return 67.9;
		} else if (id == 150) { return 10.6;
		} else if (id == 151) { return 15.2;
		} else if (id == 152) { return 143.3;
		} else if (id == 153) { return 16.5;
		} else if (id == 154) { return 121.3;
		} else if (id == 155) { return 165.3;
		} else if (id == 156) { return 0.2;
		} else if (id == 157) { return 200.5;
		} else if (id == 158) { return 3.4;
		} else if (id == 159) { return 20.7;
		} else if (id == 160) { return 0.5;
		} else if (id == 161) { return 199.1;
		} else if (id == 162) { return 600.3;
		} else if (id == 163) { return 27.6;
		} else if (id == 164) { return 88.2;
		} else if (id == 165) { return 191.8;
		} else if (id == 166) { return 49.5;
		} else if (id == 167) { return 295.1;
		} else if (id == 168) { return 489.1;
		} else if (id == 169) { return 29.0;
		} else if (id == 170) { return 55.1;
		} else if (id == 171) { return 40.3;
		} else if (id == 172) { return 100.6;
		} else if (id == 173) { return 185.3;
		} else if (id == 174) { return 6.6;
		} else if (id == 175) { return 16.5;
		} else if (id == 176) { return 88.2;
		} else if (id == 177) { return 12.9;
		} else if (id == 178) { return 75.1;
		} else if (id == 179) { return 27.6;
		} else if (id == 180) { return 178.8;
		} else if (id == 181) { return 99.9;
		} else if (id == 182) { return 149.9;
		} else if (id == 183) { return 199.9;
		} else if (id == 184) { return 10.0;
		} else if (id == 185) { return 100.0;
		} else if (id == 186) { return 292.3;
		} else if (id == 187) { return 50.2;
		} else if (id == 188) { return 103.4;
		} else if (id == 189) { return 247.2;
		} else if (id == 190) { return 100.2;
		} else if (id == 191) { return 759.2;
		} else if (id == 192) { return 1000.5;
		} else if (id == 193) { return 90.3;
		} else if (id == 194) { return 378.2;
		} else if (id == 195) { return 632.5;
		} else if (id == 196) { return 1000.5;
		} else if (id == 197) { return 1.0;
		} else if (id == 198) { return 45.2;
		} else if (id == 199) { return 94.9;
		} else if (id == 200) { return 144.9;
		} else if (id == 201) { return 194.9;
		} else if (id == 202) { return 97.4;
		} else if (id == 203) { return 301.3;
		} else if (id == 204) { return 345.0;
		}else if (id == 205) { return 94.4;
		} else if (id == 206) { return 246.3;
		} else if (id == 207) { return 357.4;
		} else if (id == 208) { return 770.5;
		} else if (id == 209) { return 16.5;
		} else if (id == 210) { return 510.9;
		} else if (id == 211) { return 102.6;
		} else if (id == 212) { return 237.6;
		} else if (id == 213) { return 145.8;
		} else if (id == 214) { return 394.5;
		} else if (id == 215) { return 2.5;
		} else if (id == 216) { return 94.2;
		} else if (id == 217) { return 5.6;
		} else if (id == 218) { return 46.6;
		} else if (id == 219) { return 165.2;
		} else if (id == 220) { return 0.4;
		} else if (id == 221) { return 36.5;
		} else if (id == 222) { return 80.5;
		} else if (id == 223) { return 19.4;
		} else if (id == 224) { return 78.5;
		} else if (id == 225) { return 205.3;
		} else if (id == 226) { return 3.9;
		} else if (id == 227) { return 17.5;
		} else if (id == 228) { return 800.4;
		} else if (id == 229) { return 20.6;
		} else if (id == 230) { return 100.9;
		} else if (id == 231) { return 2.3;
		} else if (id == 232) { return 86.5;
		} else if (id == 233) { return 750.5;
		} else if (id == 234) { return 705.2;
		} else if (id == 235) { return 956.4;
		} else if (id == 236) { return 999.9;
		} else if (id == 237) { return 895.4;
		} else if (id == 238) { return 26.0;
		} else {
			return 0;
		}
	}

	public int getEvolved(int itemid) {
		if (itemid == 20) {
			return id + 1;
		} else if (itemid == 21) {
			return id + 1;
		} else if (itemid == 22) {
			return id + 1;
		} else if (itemid == 23) {
			return id + 1;
		} else if (itemid == 24) {
			if (id == 86) {
				return id + 2;
			} else {
				return id + 1;
			}
		} else if (itemid == 25) {
			return id + 2;
		} else {
			return 10;
		}
	}





//	private String getStatName(int stat) {
//		switch(stat) {
//		case 0:
//			return "Atk";
//		case 1:
//			return "Def";
//		case 2:
//			return "SpA";
//		case 3:
//			return "SpD";
//		case 4:
//			return "Spe";
//		default:
//			return "null";
//		}
//	}
}
