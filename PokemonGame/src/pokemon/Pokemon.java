package pokemon;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import overworld.GamePanel;
import pokemon.Bag.Entry;
import pokemon.Field.Effect;
import pokemon.Field.FieldEffect;

public class Pokemon implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7087373480262097882L;
	
	public static Field field;
	public static GamePanel gp;
	public static final int MAX_POKEMON = 267;
	
	// Database
	private static String[] names = new String[MAX_POKEMON];
	private static PType[][] types = new PType[MAX_POKEMON][2];
	private static Ability[][] abilities = new Ability[MAX_POKEMON][3];
	private static int[][] base_stats = new int[MAX_POKEMON][6];
	private static double[] weights = new double[MAX_POKEMON];
	private static int[] catch_rates = new int[MAX_POKEMON];
	
	// id fields
	public int id;
	public String name;
	public String nickname;
	
	// stat fields
	public int[] baseStats;
	public int[] stats;
	public int level;
	public int[] statStages;
	public int[] ivs;
	public double[] nature;
	public double weight;
	public int catchRate;
	public int happiness;
	
	// type fields
	public PType type1;
	public PType type2;
	
	// ability fields
	public Ability ability;
	public int abilitySlot;
	
	// move fields
	public Node[] movebank;
	public Moveslot[] moveset;
	
	// status fields
	public Status status;
	public ArrayList<Status> vStatuses;
	
	// xp fields
	public int exp;
	public int expMax;
	
	// hp fields
	public int currentHP;
	public boolean fainted;
	
	// item fields
	public Item item;
	public boolean loseItem;
	public Item lostItem;
	
	// counter fields
	private int confusionCounter;
	private int sleepCounter;
	public int perishCount;
	public int magCount;
	public int moveMultiplier;
	private int spunCount;
	private int outCount;
	private int rollCount;
	private int encoreCount;
	private int tauntCount;
	private int tormentCount;
	private int toxic;
	public int headbuttCrit;
	public int happinessCap;
	
	// boolean fields
	public boolean impressive;
	public boolean battled;
	public boolean success;
	private boolean visible;
	public boolean spriteVisible;
	public boolean consumedItem;
	
	// battle fields
	public Move lastMoveUsed;
	public int slot;
	
	// trainer field
	public Trainer trainer;
	
	// Sprite fields
	private transient BufferedImage sprite;
	private transient Image frontSprite;
	private transient Image backSprite;
	private transient BufferedImage miniSprite;
	
	public Pokemon(int i, int l, boolean o, boolean t) {
		id = i;
		name = getName();
		nickname = name;
		
		baseStats = new int[6];
		stats = new int[6];
		ivs = new int[6];
		level = l;
		statStages = new int[7];
		
		baseStats = getBaseStats();
		for (int j = 0; j < 6; j++) { ivs[j] = (int) (Math.random() * 32); }
		setNature();
		setStats();
		setTypes();
		if (t) {
			setAbility(0);
		} else {
			abilitySlot = (int)Math.round(Math.random());
			setAbility(abilitySlot);
		}
		this.weight = getWeight();
		
		expMax = setExpMax();
		exp = 0;
		
		currentHP = this.getStat(0);
		moveset = new Moveslot[4];
		setMoveBank();
		setMoves();
		moveMultiplier = 1;
		
		status = Status.HEALTHY;
		vStatuses = new ArrayList<Status>();
		
		impressive = true;
		if (t) {
			ivs = new int[] {31, 31, 31, 31, 31, 31};
			nature = new double[] {1.0,1.0,1.0,1.0,1.0,0.0};
			setStats();
			currentHP = this.getStat(0);
		}
		
		happiness = 70;
		if (id == 29 || id == 134 || id == 174) happiness = 110;
		catchRate = getCatchRate();
		happinessCap = 50;
		
		spriteVisible = true;
		
		if (!t) setSprites();
	}
	
	public Pokemon(int i, Pokemon pokemon) {
		id = i;
		name = getName();
		nickname = pokemon.nickname;
		if (pokemon.nickname.equals(pokemon.name)) nickname = name;
		
		baseStats = new int[6];
		stats = new int[6];
		level = pokemon.level;
		statStages = pokemon.statStages.clone();
		ivs = pokemon.ivs;
		nature = pokemon.nature;
		
		baseStats = getBaseStats();
		setStats();
		setTypes();
		this.abilitySlot = pokemon.abilitySlot;
		setAbility(abilitySlot);
		this.weight = getWeight();
		
		this.slot = pokemon.slot;
		
		expMax = setExpMax();
		exp = 0;
		
		currentHP = this.getStat(0);
		setMoveBank();
		moveset = pokemon.moveset;
		
		status = pokemon.status;
		vStatuses = new ArrayList<Status>(pokemon.vStatuses);
		
		impressive = true;
		trainer = pokemon.trainer;
		lastMoveUsed = pokemon.lastMoveUsed;
		
		happiness = pokemon.happiness;
		headbuttCrit = pokemon.headbuttCrit;
		catchRate = getCatchRate();
		happinessCap = pokemon.happinessCap;
		item = pokemon.item;
		loseItem = pokemon.loseItem;
		lostItem = pokemon.lostItem;
		consumedItem = pokemon.consumedItem;
		
		moveMultiplier = pokemon.moveMultiplier;
		
		visible = pokemon.visible;
		spriteVisible = pokemon.spriteVisible;
		
		setSprites();
	}
	
	public Pokemon(int i, int l, boolean o, boolean t, Item item) {
		this(i, l, o, t);
		this.item = item;
	}
	
	public Pokemon(int i, int l, boolean isStatic) {
		this(i, l, false, true);
		Random rand = new Random();
		for (int j = 0; j < 6; j++) { this.ivs[j] = rand.nextInt(32); }
		for (int k = 0; k < 3; k++) {
			int index = -1;
			do {
				index = rand.nextInt(6);
			} while (this.ivs[index] == 31);
			this.ivs[rand.nextInt(6)] = 31;
		}
		setNature();
		setStats();
		currentHP = this.getStat(0);
		
		abilitySlot = (int)Math.round(Math.random());
		setAbility(abilitySlot);
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public BufferedImage getSprite(int id) {
		return new Pokemon(id, 5, false, false).sprite;
	}
	
	public Image getFrontSprite() {
		return frontSprite;
	}
	
	public Image getBackSprite() {
		return backSprite;
	}
	
	public BufferedImage getMiniSprite() {
		return miniSprite;
	}
	
	public BufferedImage getMiniSprite(int id) {
		sprite = setSprite();
		return setMiniSprite();
	}
	
	private BufferedImage setSprite() {
		BufferedImage image = null;
		
		String imageName = id + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/sprites/" + imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/000.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
	private Image setFrontSprite() {
		BufferedImage originalSprite = sprite;
		
		int scaledWidth = originalSprite.getWidth(null) * 2;
		int scaledHeight = originalSprite.getHeight(null) * 2;
		
		Image scaledImage = originalSprite.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		
		return scaledImage;
	}
	
	private Image setBackSprite() {
		Image image = frontSprite;
		
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = bufferedImage.createGraphics();

	    // Flip the image horizontally by drawing it with negative width
	    graphics.drawImage(image, image.getWidth(null), 0, -image.getWidth(null), image.getHeight(null), null);
	    graphics.dispose();

	    return bufferedImage;
	}
	
	public BufferedImage setMiniSprite() {
		BufferedImage image = null;
		
		String imageName = id + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/minisprites/" + imageName + ".png"));
		} catch (Exception e) {
			image = sprite;
			
			int scaledWidth = 40;  // New width
	        int scaledHeight = 40; // New height

	        // Create a BufferedImage with transparent pixels
	        BufferedImage miniImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);

	        // Calculate the position to draw the scaled image in the center
	        int x = (60 - scaledWidth) / 2;
	        int y = (60 - scaledHeight) / 2;

	        // Draw the scaled-down sprite onto the BufferedImage
	        Graphics2D g2d = miniImage.createGraphics();
	        g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
	        g2d.dispose();

	        return miniImage;
		}
		return image;
	}

	public void setMoves() {
		int index = 0;
		moveset = new Moveslot[4];
		for (int i = 0; i < level && i < movebank.length; i++) {
			Node node = movebank[i];
	        while (node != null) {
	            moveset[index] = new Moveslot(node.data);
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
	    for (Moveslot m : moveset) {
	    	if (m != null && m.currentPP != 0) {
	    		Move move = m.move;
	    		if (move != null) {
		            validMoves.add(move);
		        }
	    	}
	    }
	    
	    if (this.vStatuses.contains(Status.TAUNTED)) {
	    	validMoves.removeIf(move -> move.cat == 2 && move != Move.METRONOME);
	    }
	    
	    if (this.vStatuses.contains(Status.TORMENTED)) {
	    	validMoves.removeIf(move -> move == this.lastMoveUsed);
	    }
	    
	    if (this.vStatuses.contains(Status.MUTE)) {
	    	validMoves.removeIf(move -> Move.getSound().contains(move));
	    }

	    // Pick a random move from the validMoves list
	    Random rand = new Random();
	    
	    if (validMoves.size() > 0) {
	    	int index = rand.nextInt(validMoves.size());
	    	return validMoves.get(index);
	    } else {
	    	return Move.STRUGGLE;
	    }	    
	}
	
	public Move bestMove(Pokemon foe, boolean first) {
	    ArrayList<Move> validMoves = this.getValidMoveset();
	    Trainer tr = this.trainer;
	    
        // Calculate and store the damage values of each move
        Map<Move, Integer> moveToDamage = new HashMap<>();

        for (Move move : validMoves) {
            int damage = calcWithTypes(foe, move, first);
            moveToDamage.put(move, damage);
        }
        
        // Check if there are no valid moves with non-zero PP
        if (moveToDamage.isEmpty()) {
        	// 100% chance to swap in a partner if you can only struggle
        	if (tr.hasValidMembers() && !isTrapped()) {
        		System.out.println("all valid moves have 0 PP : 100%");
        		this.vStatuses.add(Status.SWAP);
        		return Move.GROWL;
        	} else {
        		return Move.STRUGGLE;
        	}
        }
        
        // Find the maximum damage value
        int maxDamage = Collections.max(moveToDamage.values());
        
        if (tr.hasValidMembers() && !isTrapped()) {
        	// 25% chance to swap in a partner if they resist and you don't
        	if (foe.lastMoveUsed != null && foe.lastMoveUsed.cat != 2 && !tr.resists(this, foe.lastMoveUsed.mtype)
        			&& tr.hasResist(foe.lastMoveUsed.mtype)) {
        		double chance = 25;
        		if (this == this.getFaster(foe, 0, 0)) chance /= 2;
        		if (this.impressive) chance /= 2;
        		if (checkSecondary((int) Math.round(chance))) {
        			System.out.print("partner resists : ");
        			System.out.printf("%.1f%%\n", chance);
                	this.vStatuses.add(Status.SWAP);
                	return Move.SPLASH;
        		}
            }
        	// 70% chance to swap if all of your moves do 0 damage
        	if (maxDamage == 0 && !validMoves.equals(new ArrayList<>(Arrays.asList(new Move[] {Move.METRONOME})))) {
        		double chance = 70;
        		if (this.impressive) chance *= 0.75;
        		if (checkSecondary((int) chance)) {
        			System.out.print("all moves do 0 damage : ");
        			System.out.println(String.format("%.1f", chance) + "%");
        			this.vStatuses.add(Status.SWAP);
            		return Move.GROWL;
        		}
        	}
        	// 20% chance to swap if the most damage you do is 1/5 or less to target
        	if (maxDamage <= foe.currentHP * 1.0 / 5) {
        		double chance = 20;
        		if (this == this.getFaster(foe, 0, 0) && !validMoves.contains(Move.U$TURN) && !validMoves.contains(Move.VOLT_SWITCH) &&
        				!validMoves.contains(Move.FLIP_TURN) && !validMoves.contains(Move.PARTING_SHOT) && !validMoves.contains(Move.BATON_PASS)) chance /= 2;
        		if (this.impressive) chance /= 2;
        		if (checkSecondary((int) chance)) {
        			System.out.print("damage i do is 1/5 or less : ");
        			System.out.println(String.format("%.1f", chance) + "%");
        			if (validMoves.contains(Move.U$TURN)) return Move.U$TURN;
        			if (validMoves.contains(Move.VOLT_SWITCH)) return Move.VOLT_SWITCH;
        			if (validMoves.contains(Move.FLIP_TURN)) return Move.FLIP_TURN;
        			if (validMoves.contains(Move.PARTING_SHOT)) return Move.PARTING_SHOT;
        			if (validMoves.contains(Move.BATON_PASS)) return Move.BATON_PASS;
        			this.vStatuses.add(Status.SWAP);
            		return Move.GROWL;
        		}
        	}
        	// 100% chance to swap if perishCount is 1
        	if (this.perishCount == 1) {
        		System.out.println("perish in 1 : 100%");
        		this.vStatuses.add(Status.SWAP);
        		return Move.GROWL;
        	}
    		boolean moveKills = false;
    		PType type = null;
        	for (Moveslot m : foe.moveset) {
        		if (m != null && m.currentPP > 0) {
        			Move move = m.move;
            		int damage = foe.calcWithTypes(this, move, true, 1, false);
            		if (damage >= this.currentHP && tr.hasResist(move.mtype)) {
            			moveKills = true;
            			type = move.mtype;
            			break;
            		}
        		}
        	}
        	if (moveKills) {
        		double chance = (this.currentHP * 1.0 / this.getStat(0)) * 100;
        		chance *= 0.6;
        		if (this == this.getFaster(foe, 0, 0)) chance /= 2;
        		if (maxDamage >= foe.currentHP) chance /= 2;
        		if (checkSecondary((int) chance)) {
        			System.out.print("enemy kills me : ");
        			System.out.println(String.format("%.1f", chance) + "%");
        			this.vStatuses.add(Status.SWAP);
            		return Move.moveOfType(type);
        		}
        	}
        }
        
        
        // Filter moves based on conditions and max damage
        ArrayList<Move> bestMoves = new ArrayList<>();
        for (Map.Entry<Move, Integer> entry : moveToDamage.entrySet()) {
        	Move move = entry.getKey();
        	int damage = entry.getValue();
        	
        	if (damage >= maxDamage && (damage > 0 || validMoves.size() == 1 || (allMovesAreDamaging(moveToDamage) && maxDamage == 0)) && move.cat != 2) {
        		bestMoves.add(move);
        		bestMoves.add(move);
        		bestMoves.add(move);
        		if (move.hasPriority(this)) {
        			bestMoves.add(move);
        		}
        	}
        	if (move.cat == 2 || move == Move.NUZZLE || move == Move.SWORD_SPIN || move == Move.POWER$UP_PUNCH || move == Move.VENOM_SPIT
        			|| move == Move.FATAL_BIND || move == Move.MOLTEN_CONSUME || ((move == Move.WHIRLPOOL || move == Move.WRAP || move == Move.BIND || move == Move.FIRE_SPIN)
        					&& !foe.vStatuses.contains(Status.SPUN))) {
        		if (move.accuracy > 100) {
        			Pokemon freshYou = this.clone();
        			freshYou.statStages = new int[freshYou.statStages.length];
        			Pokemon freshFoe = new Pokemon(1, 1, false, false);
        			int[] prevStatsF = freshYou.statStages.clone();
        			freshYou.statusEffect(freshFoe, move, null, null, null, field.foeSide, field.playerSide, false);
        			int[] currentStatsF = freshYou.statStages.clone();
        			if (!arrayEquals(prevStatsF, currentStatsF)) {
        				Pokemon you = this.clone();
            			Pokemon foeClone = foe.clone(); // shouldn't matter
            			int[] prevStats = you.statStages.clone();
            			you.statusEffect(foeClone, move, null, null, null, field.foeSide, field.playerSide, false);
            			int[] currentStats = you.statStages.clone();
            			if (arrayGreaterOrEqual(prevStats, currentStats)) {
            				// nothing: don't add
            			} else {
            				bestMoves.add(move);
            				if (maxDamage < foe.currentHP / 4) {
                    			bestMoves.add(move);
                    		}
            			}
        			} else {
        				bestMoves.add(move);
            			if (maxDamage < foe.currentHP / 4) {
                			bestMoves.add(move);
                		}
        			}
        		} else {
        			bestMoves.add(move);
        			if (maxDamage < foe.currentHP / 4) {
            			bestMoves.add(move);
            		}
        		}
        	}
        	if (move == Move.SEA_DRAGON && id == 150) {
        		bestMoves.add(move);
        	}
        }
        
        bestMoves = modifyStatus(bestMoves, foe);
    	if (bestMoves.isEmpty()) { // fallback: return a random valid move
    		int randomIndex = (int) (Math.random() * validMoves.size());
            return validMoves.get(randomIndex);
    	}
        int randomIndex = (int) (Math.random() * bestMoves.size());
        return bestMoves.get(randomIndex);
    }
	
	

	private ArrayList<Move> modifyStatus(ArrayList<Move> bestMoves, Pokemon foe) {
		if (bestMoves.size() > 1 && bestMoves.contains(Move.STICKY_WEB) && field.contains(field.playerSide, Effect.STICKY_WEBS)) bestMoves.removeIf(Move.STICKY_WEB::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.STEALTH_ROCK) && field.contains(field.playerSide, Effect.STEALTH_ROCKS)) bestMoves.removeIf(Move.STEALTH_ROCK::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SPIKES) && field.getLayers(field.playerSide, Effect.SPIKES) == 3) bestMoves.removeIf(Move.SPIKES::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TOXIC_SPIKES) && field.getLayers(field.playerSide, Effect.TOXIC_SPIKES) == 2) bestMoves.removeIf(Move.TOXIC_SPIKES::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.DEFOG) && field.getHazards(field.foeSide).isEmpty() && field.getScreens(field.playerSide).isEmpty() && field.terrain == null) bestMoves.removeIf(Move.DEFOG::equals);
		
		ArrayList<Move> noRepeat = Move.getNoComboMoves();
		for (Move m : noRepeat) {
			if (bestMoves.size() > 1 && bestMoves.contains(m) && lastMoveUsed == m) bestMoves.removeIf(m::equals);
		}
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGIC_REFLECT) && this.impressive) bestMoves.removeIf(Move.MAGIC_REFLECT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.ABDUCT) && this.impressive) bestMoves.removeIf(Move.ABDUCT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TAKE_OVER) && this.impressive) bestMoves.removeIf(Move.TAKE_OVER::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SUNNY_DAY) && field.equals(field.weather, Effect.SUN)) bestMoves.removeIf(Move.SUNNY_DAY::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.RAIN_DANCE) && field.equals(field.weather, Effect.RAIN)) bestMoves.removeIf(Move.RAIN_DANCE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SANDSTORM) && field.equals(field.weather, Effect.SANDSTORM)) bestMoves.removeIf(Move.SANDSTORM::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SNOWSCAPE) && field.equals(field.weather, Effect.SNOW)) bestMoves.removeIf(Move.SNOWSCAPE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.ELECTRIC_TERRAIN) && field.equals(field.terrain, Effect.ELECTRIC)) bestMoves.removeIf(Move.ELECTRIC_TERRAIN::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.GRASSY_TERRAIN) && field.equals(field.terrain, Effect.GRASSY)) bestMoves.removeIf(Move.GRASSY_TERRAIN::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.PSYCHIC_TERRAIN) && field.equals(field.terrain, Effect.PSYCHIC)) bestMoves.removeIf(Move.PSYCHIC_TERRAIN::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SPARKLING_TERRAIN) && field.equals(field.terrain, Effect.SPARKLY)) bestMoves.removeIf(Move.SPARKLING_TERRAIN::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.REFLECT) && field.contains(field.foeSide, Effect.REFLECT)) bestMoves.removeIf(Move.REFLECT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.LIGHT_SCREEN) && field.contains(field.foeSide, Effect.LIGHT_SCREEN)) bestMoves.removeIf(Move.LIGHT_SCREEN::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.AURORA_VEIL) && (field.contains(field.foeSide, Effect.AURORA_VEIL) || !field.equals(field.weather, Effect.SNOW))) bestMoves.removeIf(Move.AURORA_VEIL::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SAFEGUARD) && field.contains(field.foeSide, Effect.SAFEGUARD)) bestMoves.removeIf(Move.SAFEGUARD::equals);
		
		if (bestMoves.size() > 1 && (bestMoves.contains(Move.ROOST) || bestMoves.contains(Move.SYNTHESIS) || bestMoves.contains(Move.MOONLIGHT) || bestMoves.contains(Move.MORNING_SUN) ||
				bestMoves.contains(Move.RECOVER) || bestMoves.contains(Move.SLACK_OFF) || bestMoves.contains(Move.WISH) || bestMoves.contains(Move.REST) ||
				bestMoves.contains(Move.LIFE_DEW) || bestMoves.contains(Move.STRENGTH_SAP))) {
			if (this.getStat(0) == this.currentHP) {
				bestMoves.removeAll(Arrays.asList(new Move[] {Move.ROOST, Move.SYNTHESIS, Move.MOONLIGHT, Move.MORNING_SUN, Move.RECOVER, Move.SLACK_OFF, Move.WISH, Move.REST, Move.LIFE_DEW, Move.STRENGTH_SAP}));
			}
		}
		
		if (bestMoves.size() > 1) bestMoves.removeIf(Move.HEALING_WISH::equals);
		if (bestMoves.size() > 1) bestMoves.removeIf(Move.LUNAR_DANCE::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TRICK_ROOM) && field.contains(field.fieldEffects, Effect.TRICK_ROOM)) bestMoves.removeIf(Move.TRICK_ROOM::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TAILWIND) && field.contains(field.foeSide, Effect.TAILWIND)) bestMoves.removeIf(Move.TAILWIND::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MUD_SPORT) && field.contains(field.fieldEffects, Effect.MUD_SPORT)) bestMoves.removeIf(Move.MUD_SPORT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.WATER_SPORT) && field.contains(field.fieldEffects, Effect.WATER_SPORT)) bestMoves.removeIf(Move.WATER_SPORT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.GRAVITY) && field.contains(field.fieldEffects, Effect.GRAVITY)) bestMoves.removeIf(Move.GRAVITY::equals);
		
		if (bestMoves.size() > 1 && (bestMoves.contains(Move.AQUA_RING) || bestMoves.contains(Move.INGRAIN)) && this.vStatuses.contains(Status.AQUA_RING)) {
			bestMoves.removeIf(Move.INGRAIN::equals);
			bestMoves.removeIf(Move.AQUA_RING::equals);
		}
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.WORRY_SEED) && foe.ability == Ability.INSOMNIA) bestMoves.removeIf(Move.WORRY_SEED::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.GASTRO_ACID) && foe.ability == Ability.NULL) bestMoves.removeIf(Move.GASTRO_ACID::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FORESTS_CURSE) && (foe.type1 == PType.GRASS || foe.type2 == PType.GRASS)) bestMoves.removeIf(Move.FORESTS_CURSE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGIC_POWDER) && (foe.type1 == PType.MAGIC || foe.type2 == PType.MAGIC)) bestMoves.removeIf(Move.MAGIC_POWDER::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.VENOM_DRENCH) && (foe.status != Status.POISONED || foe.status != Status.TOXIC)) bestMoves.removeIf(Move.VENOM_DRENCH::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MEAN_LOOK) && foe.vStatuses.contains(Status.TRAPPED)) bestMoves.removeIf(Move.MEAN_LOOK::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FOCUS_ENERGY) && this.vStatuses.contains(Status.FOCUS_ENERGY)) bestMoves.removeIf(Move.FOCUS_ENERGY::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.ENCORE) && foe.vStatuses.contains(Status.ENCORED)) bestMoves.removeIf(Move.ENCORE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.NO_RETREAT) && this.vStatuses.contains(Status.NO_SWITCH)) bestMoves.removeIf(Move.NO_RETREAT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MEMENTO) && ((this.currentHP * 1.0 / this.getStat(0))) > 0.25) bestMoves.removeIf(Move.MEMENTO::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.CURSE) && (this.currentHP * 1.0 / this.getStat(0)) <= 0.55 && (this.type1 == PType.GHOST || this.type2 == PType.GHOST)) bestMoves.removeIf(Move.CURSE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.PERISH_SONG) && (this.perishCount > 0 || foe.perishCount > 0)) bestMoves.removeIf(Move.PERISH_SONG::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.NIGHTMARE) && (foe.status != Status.ASLEEP || foe.vStatuses.contains(Status.NIGHTMARE))) bestMoves.removeIf(Move.NIGHTMARE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGNET_RISE) && this.magCount > 0) bestMoves.removeIf(Move.MAGNET_RISE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MIRROR_MOVE) && foe.lastMoveUsed == null) bestMoves.removeIf(Move.MIRROR_MOVE::equals);
		
		return bestMoves;
	}
	
	private boolean allMovesAreDamaging(Map<Move, Integer> moves) {
		for (Map.Entry<Move, Integer> e : moves.entrySet()) {
			if (e.getKey().cat == 2) return false;
		}
		return true;
	}
	
	public int getID() {
		return this.id;
	}

	public boolean isFainted() {
		return this.fainted;
	}
	
	public void setTypes() {
		setTypes(this.id);
	}
	
	private void setTypes(int id) {
		PType[] type = getTypes(id);
		type1 = type[0];
		type2 = type[1];
	}

	public static PType[] getTypes(int id) {
		return types[id - 1];
	}
	
	public static PType getType1(int id) {
		return getTypes(id)[0];
	}
	
	public static PType getType2(int id) {
		return getTypes(id)[1];
	}
	
	public String getName() {
		return getName(id);
	}
	
	public static String getName(int id) {
		return names[id - 1];
	}
	
	public void setAbility(int slot) {
		ability = abilities[id - 1][slot];
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getCurrentHP() {
		return this.currentHP;
	}

	public void levelUp(Player player) {
		int oHP = this.getStat(0);
		this.exp -= this.expMax;
		++level;
		awardHappiness(5, false);
		
		expMax = setExpMax();
		setStats();
		int nHP = this.getStat(0);
		int amt = nHP - oHP;
		currentHP += amt;
		if (this.level == 100) this.exp = 0;
		
		Task t = addTask(Task.LEVEL_UP, this.nickname + " leveled up to " + this.level + "!", this);
		t.setFinish(amt);
		
		ArrayList<Task> check = gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.ui.tasks;
		checkMove(check.size());
		this.checkEvo(player);
	}
	
	public int setExpMax() {
		if (level >= 1 && level < 20) {
			return level * 2;
		} else if (level >= 20 && level < 30) {
			return (int) (level * 2.5);
		} else if (level >= 30 && level < 40) {
			return level * 3;
		} else if (level >= 40 && level < 50) {
			return (int) (level * 3.5);
		} else if (level >= 50 && level < 60) {
			return level * 4;
		} else if (level >= 60 && level < 70) {
			return (int) (level * 4.5);
		} else if (level >= 70 && level < 80) {
			return level * 5;
		} else if (level >= 80 && level < 90) {
			return (int) (level * 5.5);
		} else if (level >= 90) {
			return level * 6;
		}
		return 0;
	}

	public void checkMove(int index) {
	    if (this.level - 1 >= this.movebank.length) return;
	    Node node = this.movebank[this.level - 1];
	    while (node != null) {
	        Move move = node.data;
	        if (move != null && !this.knowsMove(move)) {
	            boolean learnedMove = false;
	            for (int i = 0; i < 4; i++) {
	                if (this.moveset[i] == null) {
	                    this.moveset[i] = new Moveslot(move);
	                    if (gp.gameState == GamePanel.BATTLE_STATE) {
	                    	Task t = createTask(Task.TEXT, this.nickname + " learned " + move.toString() + "!");
		                    insertTask(t, index++);
	                    } else {
	                    	addTask(Task.TEXT, this.nickname + " learned " + move.toString() + "!");
	                    }
	                    
	                    learnedMove = true;
	                    break;
	                }
	            }
	            if (!learnedMove) {
	            	Task t = createTask(Task.MOVE, "", this);
            		t.setMove(move);
	            	if (gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE) {
	            		insertTask(t, index++);
	            	} else {
	            		gp.ui.currentPokemon = this;
		            	gp.ui.currentMove = move;
		            	gp.ui.showMoveOptions = true;
	            	}
	            	gp.battleUI.moveOption = -1;
	            }
	        }
	        node = node.next;
	    }
	}


	private void checkEvo(Player player) {
		if (this.item == Item.EVERSTONE) return;
		Pokemon result = null;
		int area = player.currentMap;
		if (area >= 95 && area <= 99) area = 35; // electric tunnel
		if (id == 1 && level >= 18) {
			result = new Pokemon(2, this);
		} else if (id == 2 && level >= 36) {
			result = new Pokemon(3, this);
		} else if (id == 4 && level >= 16) {
			result = new Pokemon(5, this);
		} else if (id == 5 && level >= 36) {
			result = new Pokemon(6, this);
		} else if (id == 7 && level >= 17) {
			result = new Pokemon(8, this);
		} else if (id == 8 && level >= 36) {
			result = new Pokemon(9, this);
		} else if (id == 10 && level >= 18) {
			result = new Pokemon(11, this);
		} else if (id == 11 && level >= 36) {
			result = new Pokemon(id + 1, this);		
		} else if (id == 13 && level >= 17) {
			result = new Pokemon(id + 1, this);
		} else if (id == 14 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 16 && knowsMove(Move.ROLLOUT)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 17 && area == 100) {
			result = new Pokemon(id + 1, this);
		} else if (id == 19 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 20 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 22 && level >= 18) {
			result = new Pokemon(id + 1, this);
		} else if (id == 23 && (area == 80 || area == 83 || area == 90 || (area >= 100 && area <= 103))) {
			result = new Pokemon(25, this);
		} else if (id == 23 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 26 && level >= 28) {
			result = new Pokemon(id + 1, this);
		} else if (id == 29 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 32 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 33 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 35 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 36 && area == 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 41 && level >= 15) {
			result = new Pokemon(id + 1, this);
		} else if (id == 42 && level >= 39) {
			result = new Pokemon(id + 1, this);
		} else if (id == 44 && level >= 16) {
			result = new Pokemon(id + 1, this);
		} else if (id == 48 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 49 && area == 35) {
			result = new Pokemon(id + 2, this);
		} else if (id == 49 && knowsMove(Move.ROCK_BLAST)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 52 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 53 && (area == 80 || area == 83 || area == 90 || (area >= 100 && area <= 103))) {
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
		} else if (id == 73 && level >= 30) {
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
		} else if (id == 101 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 102 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 104 && level >= 24) {
			result = new Pokemon(id + 1, this);
		} else if (id == 106 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 111 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 112 && area == 35) {
			result = new Pokemon(id + 1, this);
		} else if (id == 114 && level >= 28) {
			result = new Pokemon(id + 1, this);
		} else if (id == 115 && level >= 48) {
			result = new Pokemon(id + 1, this);
		} else if (id == 117 && level >= 19) {
			result = new Pokemon(id + 1, this);
		} else if (id == 120 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 121 && happiness >= 250) {
			result = new Pokemon(id + 1, this);
		} else if (id == 123 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 124 && happiness >= 250) {
			result = new Pokemon(id + 1, this);
		} else if (id == 126 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 127 && happiness >= 250) {
			result = new Pokemon(id + 1, this);
		} else if (id == 129 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 132 && area == 77) {
			result = new Pokemon(id + 1, this);
		} else if (id == 134 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 135 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 137 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 139 && area == 77) {
			result = new Pokemon(id + 1, this);
		} else if (id == 143 && happiness >= 250) { // ?? maybe stone???? idfk
			result = new Pokemon(id + 1, this);
		} else if (id == 144 && area == 77) {
			result = new Pokemon(id + 1, this);
		} else if (id == 146 && level >= 39) {
			result = new Pokemon(id + 1, this);
		} else if (id == 148 && area == 77) {
			result = new Pokemon(id + 1, this);
		} else if (id == 151 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 153 && level >= 22) {
			result = new Pokemon(id + 1, this);
		} else if (id == 154 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 156 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 158 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 161 && happiness >= 250) {
			result = new Pokemon(id + 1, this);
		} else if (id == 163 && level >= 25) {
			result = new Pokemon(id + 1, this);
		} else if (id == 164 && level >= 36) {
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
		} else if (id == 195 && area == 100) {
			result = new Pokemon(id + 1, this);
		} else if (id == 197 && level >= 32) {
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
		} else if (id == 206 && area == 35) {
			result = new Pokemon(208, this);
		} else if (id == 206 && knowsMove(Move.ROCK_BLAST)) {
			result = new Pokemon(id + 1, this);
		} else if (id == 209 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 211 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 213 && level >= 40) {
			result = new Pokemon(id + 1, this);
		} else if (id == 217 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 218 && level >= 45) {
			result = new Pokemon(id + 1, this);
		} else if (id == 221 && happiness >= 250) {
			result = new Pokemon(id + 1, this);
		} else if (id == 223 && level >= 16) {
			result = new Pokemon(id + 1, this);
		} else if (id == 224 && level >= 36) {
			result = new Pokemon(id + 1, this);
		} else if (id == 226 && level >= 32) {
			result = new Pokemon(id + 1, this);
		} else if (id == 238 && level >= 39) {
            result = new Pokemon(id + 1, this);
		} else if (id == 239 && headbuttCrit >= 5) {
		    result = new Pokemon(id + 1, this);
		} else if (id == 241 && level >= 35) {
            result = new Pokemon(id + 1, this);
		}
		
		if (result != null) {
			addEvoTask(this, result);
	    }
	}

	public void setStats() {
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	    sb.append(name);
	    if (!nickname.equals(name)) {
	    	sb.append(" (");
		    sb.append(nickname);
		    sb.append(")");
	    }
	    return sb.toString();
	}
	
	public int[] getBaseStats() {
		return base_stats[id - 1];
	}

	
	public void move(Pokemon foe, Move move, boolean first) {
		if (this.fainted || foe.fainted) return;
		if (move == null) return;

		double attackStat;
		double defenseStat;
		int damage = 0;
		double bp = move.basePower;
		int acc = move.accuracy;
		int secChance = move.secondary;
		PType moveType = move.mtype;
		int critChance = move.critChance;
		Ability foeAbility = foe.ability;
		
		ArrayList<FieldEffect> userSide;
		ArrayList<FieldEffect> enemySide;
		Player player;
		Trainer enemy;
		Pokemon[] team = null;
		
		if (this.playerOwned()) {
			userSide = field.playerSide;
			enemySide = field.foeSide;
			player = (Player) this.trainer;
			enemy = foe.trainer;
			team = player.team;
		} else {
			userSide = field.foeSide;
			enemySide = field.playerSide;
			player = foe.trainer instanceof Player ? (Player) foe.trainer : gp.player.p;
			enemy = this.trainer;
			if (trainer != null) team = enemy.team;
		}
		
		if (move != this.lastMoveUsed) this.moveMultiplier = 1;
		
		if (!this.vStatuses.contains(Status.CHARGING) && !this.vStatuses.contains(Status.SEMI_INV) && !this.vStatuses.contains(Status.LOCKED) &&
				!this.vStatuses.contains(Status.ENCORED) && !Move.getNoComboMoves().contains(move) && move != Move.STRUGGLE) this.lastMoveUsed = move;
		
		if (move == Move.SUCKER_PUNCH && !first) move = Move.FAILED_SUCKER;
		
		if (move == Move.FAILED_SUCKER) this.lastMoveUsed = Move.SUCKER_PUNCH;
		
		if (this.vStatuses.contains(Status.ENCORED)) {
			move = this.lastMoveUsed;
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (this.status == Status.ASLEEP) {
			if (this.sleepCounter > 0) {
				addTask(Task.TEXT, this.nickname + " is fast asleep.");
				this.sleepCounter--;
				if (move == Move.SLEEP_TALK) {
					useMove(move, foe);
					ArrayList<Move> moves = new ArrayList<>();
					for (Moveslot moveslot : this.moveset) {
						if (moveslot != null) {
							Move m = moveslot.move;
							if (m != null && m != Move.SLEEP_TALK && m != Move.REST) moves.add(m);
						}
					}
					move = moves.get(new Random().nextInt(moves.size()));
					bp = move.basePower;
					acc = move.accuracy;
					secChance = move.secondary;
					moveType = move.mtype;
					critChance = move.critChance;
				} else if (move == Move.SNORE) {
					
				} else {
					this.impressive = false;
					this.vStatuses.remove(Status.LOCKED);
					this.vStatuses.remove(Status.CHARGING);
					return;
				}
			} else {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " woke up!", this);
				this.status = Status.HEALTHY;
			}
		}
		
		if (this.vStatuses.contains(Status.CONFUSED)) {
			if (this.confusionCounter == 0) {
				this.vStatuses.remove(Status.CONFUSED);
				addTask(Task.TEXT, this.nickname + " snapped out of confusion!");
			} else {
				addTask(Task.TEXT, this.nickname + " is confused!");
				if (Math.random() < 1.0/3.0) {
			        // user hits themselves
					attackStat = this.getStat(1);
					defenseStat = this.getStat(2);
					attackStat *= this.asModifier(0);
					defenseStat *= this.asModifier(1);
					damage = calc(attackStat, defenseStat, 40, this.level);
					this.damage(damage, foe, null, this.nickname + " hit itself in confusion!", -1);
					if (this.currentHP <= 0) {
						this.faint(true, foe);
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
			addTask(Task.TEXT, this.nickname + " is fully paralyzed!");
			this.moveMultiplier = 1;
			this.impressive = false;
			this.vStatuses.remove(Status.LOCKED);
			this.vStatuses.remove(Status.CHARGING);
			return;
		}
		
		if (this.status != Status.TOXIC) toxic = 0;
		if (foe.status != Status.TOXIC) foe.toxic = 0;
		
		if (this.vStatuses.contains(Status.FLINCHED) && this.ability != Ability.INNER_FOCUS) {
			if (foe.item == Item.MENTAL_HERB) {
				addTask(Task.TEXT, foe.nickname + " didn't flinch due to its Mental Herb!");
				foe.vStatuses.remove(Status.FLINCHED);
				foe.consumeItem(this);
			} else {
				addTask(Task.TEXT, this.nickname + " flinched!");
				this.vStatuses.remove(Status.FLINCHED);
				this.moveMultiplier = 1;
				this.impressive = false;
				this.vStatuses.remove(Status.LOCKED);
				this.vStatuses.remove(Status.CHARGING);
				return;
			}
		}
		if (this.vStatuses.contains(Status.RECHARGE)) {
			addTask(Task.TEXT, this.nickname + " must recharge!");
			this.moveMultiplier = 1;
			this.vStatuses.remove(Status.RECHARGE);
			this.impressive = false;
			this.vStatuses.remove(Status.LOCKED);
			this.vStatuses.remove(Status.CHARGING);
			return;
		}
		
		if (this.vStatuses.contains(Status.TAUNTED) && move.cat == 2 && move != Move.METRONOME) {
			addTask(Task.TEXT, this.nickname + " can't use " + move + " after the taunt!");
			this.lastMoveUsed = null;
			this.impressive = false;
			return;
		}
		
		if (this.vStatuses.contains(Status.MUTE) && Move.getSound().contains(move)) {
			addTask(Task.TEXT, this.nickname + " can't use " + move + " after the throat chop!");
			this.lastMoveUsed = null;
			this.impressive = false;
			return;
		}
		
		if (move == Move.SKULL_BASH || move == Move.SKY_ATTACK || ((move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) && !field.equals(field.weather, Effect.SUN)) || this.vStatuses.contains(Status.CHARGING) || move == Move.BLACK_HOLE_ECLIPSE || move == Move.GEOMANCY) {
			if (this.item == Item.POWER_HERB) {
				announceUseMove(move);
				addTask(Task.TEXT, this.nickname + " started charging up!");
				this.vStatuses.add(Status.CHARGING);
				if (move == Move.SKULL_BASH) stat(this, 1, 1, foe);
				if (move == Move.BLACK_HOLE_ECLIPSE) stat(this, 3, 1, foe);
				addTask(Task.TEXT, this.nickname + " became fully charged due to its Power Herb!");
				this.consumeItem(foe);
			}
			if (!this.vStatuses.contains(Status.CHARGING)) {
				announceUseMove(move);
				addTask(Task.TEXT, this.nickname + " started charging up!");
				this.vStatuses.add(Status.CHARGING);
				if (move == Move.SKULL_BASH) stat(this, 1, 1, foe);
				if (move == Move.BLACK_HOLE_ECLIPSE) stat(this, 3, 1, foe);
				this.moveMultiplier = 1;
				this.impressive = false;
				return;
			} else {
				move = this.lastMoveUsed;
				bp = move.basePower;
				acc = move.accuracy;
				secChance = move.secondary;
				moveType = move.mtype;
				critChance = move.critChance;
				this.vStatuses.remove(Status.CHARGING);
			}
		}
		
		if (move == Move.DIG || move == Move.DIVE || move == Move.FLY || move == Move.BOUNCE || move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT || this.vStatuses.contains(Status.SEMI_INV)) {
			if (this.item == Item.POWER_HERB) {
				announceUseMove(move);
				if (move == Move.DIG) addTask(Task.SEMI_INV, this.nickname + " burrowed underground!", this);
				if (move == Move.DIVE) addTask(Task.SEMI_INV, this.nickname + " dove underwater!", this);
				if (move == Move.FLY) addTask(Task.SEMI_INV, this.nickname + " flew up high!", this);
				if (move == Move.BOUNCE) addTask(Task.SEMI_INV, this.nickname + " sprang up!", this);
				if (move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT) addTask(Task.SEMI_INV, this.nickname + " vanished instantly!", this);
				this.vStatuses.add(Status.SEMI_INV);
				addTask(Task.TEXT, this.nickname + " became fully charged due to its Power Herb!");
				this.consumeItem(foe);
			}
			if (!this.vStatuses.contains(Status.SEMI_INV)) {
				announceUseMove(move);
				if (move == Move.DIG) addTask(Task.SEMI_INV, this.nickname + " burrowed underground!", this);
				if (move == Move.DIVE) addTask(Task.SEMI_INV, this.nickname + " dove underwater!", this);
				if (move == Move.FLY) addTask(Task.SEMI_INV, this.nickname + " flew up high!", this);
				if (move == Move.BOUNCE) addTask(Task.SEMI_INV, this.nickname + " sprang up!", this);
				if (move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT) addTask(Task.SEMI_INV, this.nickname + " vanished instantly!", this);
				this.vStatuses.add(Status.SEMI_INV);
				this.moveMultiplier = 1;
				this.impressive = false;
				return;
			} else {
				move = this.lastMoveUsed;
				bp = move.basePower;
				acc = move.accuracy;
				secChance = move.secondary;
				moveType = move.mtype;
				critChance = move.critChance;
				this.vStatuses.remove(Status.SEMI_INV);
			}
		}
		
		if (foe.vStatuses.contains(Status.PROTECT) && (move.accuracy <= 100 || move.cat != 2) && move != Move.FEINT && move != Move.PHANTOM_FORCE && move != Move.VANISHING_ACT) {
			useMove(move, foe);
			addTask(Task.TEXT, foe.nickname + " protected itself!");
			if (move.contact) {
				if (foe.lastMoveUsed == Move.OBSTRUCT) stat(this, 1, -2, foe);
				if (foe.lastMoveUsed == Move.AQUA_VEIL) {
					stat(this, 0, -1, foe);
					stat(this, 2, -1, foe);
				}
				if (foe.lastMoveUsed == Move.LAVA_LAIR) burn(false, foe);
				if (foe.lastMoveUsed == Move.SPIKY_SHIELD && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
					this.damage(Math.max(this.getStat(0) / 8.0, 1), foe);
					addTask(Task.TEXT, this.nickname + " was hurt!");
					if (this.currentHP <= 0) { // Check for kill
						this.faint(true, foe);
					}
				}
			}
			this.impressive = false;
			this.moveMultiplier = 1;
			return;
		}
		
		if (this.vStatuses.contains(Status.LOCKED) && (lastMoveUsed == Move.OUTRAGE || lastMoveUsed == Move.PETAL_DANCE || lastMoveUsed == Move.THRASH ||
				lastMoveUsed == Move.ROLLOUT || lastMoveUsed == Move.ICE_BALL)) {
			move = this.lastMoveUsed;
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
			Move oldMove = move;
			move = get150Move(move);
			if (move != oldMove) {
				for (Moveslot m : this.moveset) {
					if (m != null && m.move == oldMove) {
						m.currentPP--;
						if (this.item == Item.LEPPA_BERRY && m.currentPP == 0) {
							m.currentPP = 10;
							addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to restore PP to " + m.move.toString() + "!");
							this.consumeItem(foe);
						}
					}
				}
			}
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (move == Move.MIRROR_MOVE) {
			useMove(move, foe);
			move = foe.lastMoveUsed;
			if (move == null) {
				addTask(Task.TEXT, "But it failed!");
				this.impressive = false;
				this.moveMultiplier = 1;
				return;
			}
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (foeAbility == Ability.MAGIC_BOUNCE && move.cat == 2 && (acc <= 100 || move == Move.WHIRLWIND || move == Move.ROAR || move == Move.STEALTH_ROCK
				|| move == Move.SPIKES || move == Move.TOXIC_SPIKES || move == Move.TOXIC || move == Move.STICKY_WEB)) {
			useMove(move, foe);
			addAbilityTask(foe);
			foe.move(this, move, true);
			addTask(Task.TEXT, foe.nickname + " bounced the " + move + " back!");
			return;
		}
		
		if (foe.vStatuses.contains(Status.REFLECT) && (move != Move.BRICK_BREAK && move != Move.MAGIC_FANG && move != Move.PSYCHIC_FANGS)) {
			this.move(this, move, false);
			addTask(Task.TEXT, move + " was reflected on itself!");
			foe.vStatuses.remove(Status.REFLECT);
			return;
		}
		if (this.vStatuses.contains(Status.POSESSED)) {
			this.vStatuses.remove(Status.POSESSED);
			this.move(this, move, false);
			addTask(Task.TEXT, move + " was used on itself!");
			return;
		}
		if (move == Move.FAILED_SUCKER) {
			useMove(Move.SUCKER_PUNCH, foe);
			fail();
			this.impressive = false;
			this.moveMultiplier = 1;
			return;
		}
		if (move == Move.METRONOME) {
			useMove(move, foe);
			Move[] moves = Move.values();
			move = moves[new Random().nextInt(moves.length)];
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		if (move == Move.SLEEP_TALK || (move == Move.SNORE && status != Status.ASLEEP)) {
			useMove(move, foe);
			fail();
			this.impressive = false;
			this.moveMultiplier = 1;
			return;
		}
		
		if ((move == Move.FIRST_IMPRESSION || move == Move.BELCH || move == Move.UNSEEN_STRANGLE || move == Move.FAKE_OUT) && !this.impressive) {
			useMove(move, foe);
			fail();
			this.moveMultiplier = 1;
			return;
		}
		
		if ((move == Move.MAGIC_REFLECT || move == Move.ABDUCT || move == Move.TAKE_OVER) && this.impressive) {
			useMove(move, foe);
			fail();
			this.impressive	= false;
			this.moveMultiplier = 1;
			return;
		}
		
		if (!foe.vStatuses.contains(Status.SEMI_INV)) {
			if (this.ability == Ability.COMPOUND_EYES) acc *= 1.3;
			if (item == Item.WIDE_LENS) acc *= 1.1;
			if (field.contains(field.fieldEffects, Effect.GRAVITY)) acc = acc * 5 / 3;
		}
		
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
		
		if (move == Move.POP_POP) acc = 1000;
		
		if (move == Move.FUSION_BOLT || move == Move.FUSION_FLARE || this.ability == Ability.MOLD_BREAKER) foeAbility = Ability.NULL;
		
		if (this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD) {
			int accEv = this.statStages[5] - foe.statStages[6];
			if (move == Move.DARKEST_LARIAT || move == Move.SACRED_SWORD) accEv += foe.statStages[6];
			accEv = accEv > 6 ? 6 : accEv;
			accEv = accEv < -6 ? -6 : accEv;
			double accuracy = acc * asAccModifier(accEv);
			if ((field.equals(field.weather, Effect.SANDSTORM) && foeAbility == Ability.SAND_VEIL) ||
					(field.equals(field.weather, Effect.SNOW) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
			if (foe.item == Item.BRIGHT_POWDER) accuracy *= 0.9;
			if (!hit(accuracy) || foe.vStatuses.contains(Status.SEMI_INV) && acc <= 100) {
				useMove(move, foe);
				addTask(Task.TEXT, this.nickname + "'s attack missed!");
				if (move == Move.HI_JUMP_KICK) {
					this.damage(this.getStat(0) / 2.0, foe);
					addTask(Task.TEXT, this.nickname + " kept going and crashed!");
					if (this.currentHP < 0) {
						this.faint(true, foe);
					}
				}
				if (this.item == Item.BLUNDER_POLICY) {
					addTask(Task.TEXT, this.nickname + " used its " + item.toString() + "!");
					stat(this, 4, 2, foe);
					this.consumeItem(foe);
				}
				endMove();
				this.vStatuses.remove(Status.LOCKED);
				this.moveMultiplier = 1;
				return; // Check for miss
			}
		}
		
		if (move == Move.HIDDEN_POWER) moveType = determineHPType();
		if (move == Move.WEATHER_BALL) moveType = determineWBType();
		if (move == Move.TERRAIN_PULSE) moveType = determineTPType();
		
		int numHits = move.getNumHits(this, team);
		int damageIndex = gp.battleUI.tasks.size();
		useMove(move, foe);
		String message = getTask(damageIndex).message;
		int hits = 0;
		for (int hit = 1; hit <= numHits; hit++) {
			if (hit > 1) bp = move.basePower;
			if (move == Move.POP_POP) hits++;
			if (foe.isFainted() || this.isFainted()) {
				addTask(Task.TEXT, "Hit " + (hit - 1) + " time(s)!");
				break;
			}
			
			if (move == Move.POP_POP && this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD) {
				acc = Move.POP_POP.accuracy;
				int accEv = this.statStages[5] - foe.statStages[6];
				accEv = accEv > 6 ? 6 : accEv;
				accEv = accEv < -6 ? -6 : accEv;
				double accuracy = acc * asAccModifier(accEv);
				if ((field.equals(field.weather, Effect.SANDSTORM) && foeAbility == Ability.SAND_VEIL) ||
						(field.equals(field.weather, Effect.SNOW) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
				if (foe.item == Item.BRIGHT_POWDER) accuracy *= 0.9;
				if (!hit(accuracy) || foe.vStatuses.contains(Status.SEMI_INV) && acc <= 100) {
					addTask(Task.TEXT, this.nickname + "'s attack missed!");
					if (this.item == Item.BLUNDER_POLICY) {
						stat(this, 4, 2, foe);
						this.consumeItem(foe);
					}
					endMove();
					this.vStatuses.remove(Status.LOCKED);
					this.moveMultiplier = 1;
					hits--;
					continue; // Check for miss
				}
			}
			
			if (moveType == PType.FIRE && foeAbility == Ability.FLASH_FIRE && !(move.cat == 2 && acc > 100)) {
				addAbilityTask(foe);
				addTask(Task.TEXT, foe.nickname + "'s Fire-Type move's are boosted!");
				if (!foe.vStatuses.contains(Status.FLASH_FIRE)) foe.vStatuses.add(Status.FLASH_FIRE);
				endMove();
				this.moveMultiplier = 1;
				return;
			}
			
			if (((moveType == PType.WATER && (foeAbility == Ability.WATER_ABSORB || foeAbility == Ability.DRY_SKIN)) || (moveType == PType.ELECTRIC && foeAbility == Ability.VOLT_ABSORB) ||
					(moveType == PType.BUG && foeAbility == Ability.INSECT_FEEDER)) && !(move.cat == 2 && acc > 100)) {
				if (foe.currentHP == foe.getStat(0)) {
					addAbilityTask(foe);
					addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
					endMove();
					this.moveMultiplier = 1;
					return; // Check for immunity
				} else {
					addAbilityTask(foe);
					foe.heal(foe.getHPAmount(1.0/4), foe.nickname + " restored HP!");
					endMove();
					this.moveMultiplier = 1;
					return;
				}
			}
			
			if (((moveType == PType.ELECTRIC && (foeAbility == Ability.MOTOR_DRIVE || foeAbility == Ability.LIGHTNING_ROD)) ||
					(moveType == PType.GRASS && foeAbility == Ability.SAP_SIPPER)) && !(move.cat == 2 && acc > 100)) {
				addAbilityTask(foe);
				if (foeAbility == Ability.MOTOR_DRIVE) stat(foe, 4, 1, this);
				if (foeAbility == Ability.LIGHTNING_ROD) stat(foe, 2, 1, this);
				if (foeAbility == Ability.SAP_SIPPER) stat(foe, 0, 1, this);
				endMove();
				this.moveMultiplier = 1;
				return;
			}
			
			if (moveType == PType.GROUND && !foe.isGrounded() && move.cat != 2) {
				if (foeAbility == Ability.LEVITATE) addAbilityTask(foe);
				addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.moveMultiplier = 1;
				return; // Check for immunity
			}
			
			if (moveType != PType.GROUND && (move.cat != 2 || move == Move.THUNDER_WAVE)) {
				if (getImmune(foe, moveType) || (moveType == PType.GHOST && foeAbility == Ability.FRIENDLY_GHOST)) {
					if (this.ability == Ability.SCRAPPY && (moveType == PType.NORMAL || moveType == PType.FIGHTING)) {
						// Nothing: scrappy allows normal and fighting type moves to hit ghosts
					} else {
						if (foeAbility == Ability.FRIENDLY_GHOST && moveType == PType.GHOST) addAbilityTask(foe);
						addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
						endMove();
						this.outCount = 0;
						this.moveMultiplier = 1;
						return; // Check for immunity 
					}
				}
			}
			
			if ((field.equals(field.terrain, Effect.PSYCHIC) && foe.isGrounded()) && ((move.priority >= 1 && acc <= 100) || (move.cat == 2 && this.ability == Ability.PRANKSTER))) {
				addTask(Task.TEXT, foe.nickname + " is protected by the Psychic Terrain!");
				endMove();
				this.moveMultiplier = 1;
				return; // Check for immunity 
			}
			
			if (foe.magCount > 0) foe.magCount--;
			
			
			if (move == Move.DREAM_EATER && foe.status != Status.ASLEEP) {
				addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.moveMultiplier = 1;
				this.impressive = false;
				return;
			}
			
			if (this.ability == Ability.PROTEAN) {
				if (this.type1 == moveType && this.type2 == null) {
					
				} else {
					this.type1 = moveType;
					this.type2 = null;
					addTask(Task.TEXT, this.nickname + "'s type was updated to " + this.type1.toString() + "!");
				}
			}
			
			if (move.cat == 2) {
				statusEffect(foe, move, player, team, enemy, userSide, enemySide);
				this.impressive = false;
				this.moveMultiplier = 1;
				return;
			}
			
			if (move.basePower < 0) {
				bp = determineBasePower(foe, move, first, team, true);
			}
			
			if (moveType == PType.FIRE && this.vStatuses.contains(Status.FLASH_FIRE)) bp *= 1.5;
			
			if (this.ability == Ability.NORMALIZE && moveType != PType.NORMAL) {
				moveType = PType.NORMAL;
				bp *= 1.2;
			}
			
			if (this.ability == Ability.PIXILATE && moveType == PType.NORMAL) {
				moveType = PType.LIGHT;
				bp *= 1.2;
			}
			
			if (this.ability == Ability.GALVANIZE && moveType == PType.NORMAL) {
				moveType = PType.ELECTRIC;
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
			
			if (foeAbility == Ability.DRY_SKIN && moveType == PType.FIRE) {
				bp *= 1.25;
			}
			
			if (this.ability == Ability.IRON_FIST && (move == Move.BULLET_PUNCH || move == Move.COMET_PUNCH || move == Move.DRAIN_PUNCH
					|| move == Move.FIRE_PUNCH || move == Move.ICE_PUNCH || move == Move.MACH_PUNCH || move == Move.POWER$UP_PUNCH || move == Move.SHADOW_PUNCH
					|| move == Move.SKY_UPPERCUT || move == Move.THUNDER_PUNCH || move == Move.METEOR_MASH || move == Move.PLASMA_FISTS || move == Move.SUCKER_PUNCH
					|| move == Move.DYNAMIC_PUNCH)) {
				bp *= 1.3;
			}
			
			if (this.ability == Ability.SHARPNESS && move.isSlicing()) bp *= 1.5;
			
			if (this.ability == Ability.STRONG_JAW && (move == Move.BITE || move == Move.CRUNCH || move == Move.FIRE_FANG || move == Move.HYPER_FANG
					|| move == Move.ICE_FANG || move == Move.JAW_LOCK || move == Move.POISON_FANG || move == Move.PSYCHIC_FANGS || move == Move.THUNDER_FANG
					|| move == Move.LEECH_LIFE || move == Move.MAGIC_FANG)) {
				bp *= 1.5;
			}
			
			if (this.ability == Ability.RECKLESS && Move.getRecoil().contains(move) && move != Move.STRUGGLE) {
				bp *= 1.3;
			}
			
			if (moveType == PType.GRASS && this.ability == Ability.OVERGROW && this.currentHP <= this.getStat(0) * 1.0 / 3) {
				bp *= 1.5;
			} else if (moveType == PType.FIRE && this.ability == Ability.BLAZE && this.currentHP <= this.getStat(0) * 1.0 / 3) {
				bp *= 1.5;
			} else if (moveType == PType.WATER && this.ability == Ability.TORRENT && this.currentHP <= this.getStat(0) * 1.0 / 3) {
				bp *= 1.5;
			} else if (moveType == PType.BUG && this.ability == Ability.SWARM && this.currentHP <= this.getStat(0) * 1.0 / 3) {
				bp *= 1.5;
			}
			
			if (move.cat == 0 && item == Item.MUSCLE_BAND) bp *= 1.1;
			if (move.cat == 1 && item == Item.WISE_GLASSES) bp *= 1.1;
			
			bp *= boostItemCheck(moveType);
			
			if (field.equals(field.weather, Effect.SUN)) {
				if (moveType == PType.WATER) bp *= 0.5;
				if (moveType == PType.FIRE) bp *= 1.5;
			}
			
			if (field.equals(field.weather, Effect.RAIN)) {
				if (moveType == PType.WATER) bp *= 1.5;
				if (moveType == PType.FIRE) bp *= 0.5;
				if (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) bp *= 0.5;
			}
			
			if (field.equals(field.terrain, Effect.ELECTRIC) && isGrounded()) {
				if (moveType == PType.ELECTRIC) bp *= 1.5;
			}
			
			if (field.equals(field.terrain, Effect.GRASSY)) {
				if (isGrounded() && moveType == PType.GRASS) bp *= 1.5;
				if (move == Move.EARTHQUAKE || move == Move.BULLDOZE || move == Move.MAGNITUDE) bp *= 0.5;
			}
			
			if (field.equals(field.terrain, Effect.SPARKLY) && isGrounded()) {
				if (moveType == PType.MAGIC) bp *= 1.5;
				secChance *= 2;
			}
			
			if (field.equals(field.terrain, Effect.PSYCHIC) && isGrounded()) {
				if (moveType == PType.PSYCHIC) bp *= 1.5;
			}
			
			if ((field.contains(field.fieldEffects, Effect.MUD_SPORT) && moveType == PType.ELECTRIC) || (field.contains(field.fieldEffects, Effect.WATER_SPORT) && moveType == PType.FIRE)) bp *= 0.33;
			
			if (field.equals(field.weather, Effect.SANDSTORM) && this.ability == Ability.SAND_FORCE && (moveType == PType.ROCK || moveType == PType.GROUND || moveType == PType.STEEL)) bp *= 1.3;
			
			if ((field.equals(field.weather, Effect.RAIN) || field.equals(field.weather, Effect.SNOW) || field.equals(field.weather, Effect.SANDSTORM)) && (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE)) bp *= 0.5;
			
			if (foeAbility == Ability.SHIELD_DUST || foe.item == Item.COVERT_CLOAK) secChance = 0;
			
			// Use either physical or special attack/defense
			if (move.isPhysical()) {
				attackStat = this.getStat(1);
				defenseStat = foe.getStat(2);
				if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(0);
				if (move != Move.DARKEST_LARIAT && move != Move.SACRED_SWORD && this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(1);
				if (move == Move.BODY_PRESS) attackStat = this.getStat(2) * this.asModifier(1);
				if (move == Move.FOUL_PLAY) attackStat = foe.getStat(1) * foe.asModifier(0);
				if (this.item == Item.CHOICE_BAND) attackStat *= 1.5;
				if (this.status == Status.BURNED && this.ability != Ability.GUTS && move != Move.FACADE) attackStat /= 2;
				if (this.ability == Ability.GUTS && this.status != Status.HEALTHY) attackStat *= 1.5;
				if (this.ability == Ability.HUGE_POWER) attackStat *= 2;
				if (field.equals(field.weather, Effect.SNOW) && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) defenseStat *= 1.5;
			} else {
				attackStat = this.getStat(3);
				defenseStat = foe.getStat(4);
				if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(2);
				if (this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(3);
				if (foe.item == Item.ASSAULT_VEST) defenseStat *= 1.5;
				if (move == Move.PSYSHOCK) defenseStat = foe.getStat(2) * foe.asModifier(1);
				if (this.item == Item.CHOICE_SPECS) attackStat *= 1.5;
				if (this.status == Status.FROSTBITE) attackStat /= 2;
				if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN)) attackStat *= 1.5;
				if (field.equals(field.weather, Effect.SANDSTORM) && (foe.type1 == PType.ROCK || foe.type2 == PType.ROCK)) defenseStat *= 1.5;
			}
			
			// Stab
			if (moveType == this.type1 || moveType == this.type2 || this.ability == Ability.TYPE_MASTER) {
				if (ability == Ability.ADAPTABILITY) {
					bp *= 2;
				} else {
					bp *= 1.5;
				}
			}
			
			if (moveType == PType.STEEL && this.ability == Ability.STEELWORKER) bp *= 1.5;
			if (moveType == PType.MAGIC && this.ability == Ability.MAGICAL) bp *= 1.5;
			
			// Charged
			if (moveType == PType.ELECTRIC && this.vStatuses.contains(Status.CHARGED)) {
				bp *= 2;
				this.vStatuses.remove(Status.CHARGED);
			}
			
			// Load Firearms
			if (moveType == PType.STEEL && this.vStatuses.contains(Status.LOADED)) {
				bp *= 2;
				this.vStatuses.remove(Status.LOADED);
			}
			
			// Crit Check
			critChance = move.critChance;
			if (this.vStatuses.contains(Status.FOCUS_ENERGY)) critChance += 2;
			if (this.ability == Ability.SUPER_LUCK) critChance++;
			if (item == Item.SCOPE_LENS) critChance++;
			if (foe.ability != Ability.BATTLE_ARMOR && foe.ability != Ability.SHELL_ARMOR && critCheck(critChance)) {
				addTask(Task.TEXT, "A critical hit!");
				if (foe.trainerOwned() && move == Move.HEADBUTT) headbuttCrit++;
				if (move.isPhysical() && attackStat < this.getStat(1)) {
					attackStat = this.getStat(1);
					if (this.status == Status.BURNED) attackStat /= 2;
				}
				if (!move.isPhysical() && attackStat < this.getStat(3)) {
					attackStat = this.getStat(3);
					if (this.status == Status.FROSTBITE) attackStat /= 2;
				}
				if (move.isPhysical() && defenseStat > foe.getStat(2)) {
					defenseStat = foe.getStat(2);
					if (field.equals(field.weather, Effect.SNOW) && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) defenseStat *= 1.5;
				}
				if (!move.isPhysical() && defenseStat > foe.getStat(4)) {
					defenseStat = foe.getStat(4);
					if (field.equals(field.weather, Effect.SANDSTORM) && (foe.type1 == PType.ROCK || foe.type2 == PType.ROCK)) defenseStat *= 1.5;
				}
				if (foe.item == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
				
				damage = calc(attackStat, defenseStat, bp, this.level);
				damage *= 1.5;
				if (this.ability == Ability.SNIPER) damage *= 1.5;
				if (foeAbility == Ability.ANGER_POINT) {
					addAbilityTask(foe);
					stat(foe, 0, 12, this); }
			} else {
				if (foe.item == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
				damage = calc(attackStat, defenseStat, bp, this.level);
			}
			
			if ((foeAbility == Ability.ICY_SCALES && !move.isPhysical()) || (foeAbility == Ability.MULTISCALE && foe.currentHP == foe.getStat(0))) damage /= 2;
			
			// Screens
			if (move.isPhysical() && field.contains(enemySide, Effect.REFLECT)) damage /= 2;
			if (!move.isPhysical() && field.contains(enemySide, Effect.LIGHT_SCREEN)) damage /= 2;
			
			double multiplier = 1;
			// Check type effectiveness
			PType[] resist = getResistances(moveType);
			if (move == Move.FREEZE$DRY) {
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
			
			if (foeAbility == Ability.ILLUMINATION) {
				PType[] lightResist = new PType[]{PType.GHOST, PType.GALACTIC, PType.LIGHT, PType.DARK};
				for (PType type : lightResist) {
					if (moveType == type) multiplier /= 2;
				}
			}
			
			// Check type effectiveness
			PType[] weak = getWeaknesses(moveType);
			if (move == Move.FREEZE$DRY) {
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
			
			if (foeAbility == Ability.WONDER_GUARD && multiplier <= 1) {
				addAbilityTask(foe);
				addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.outCount = 0;
				this.moveMultiplier = 1;
				return; // Check for immunity
			}
			
			if (foeAbility == Ability.FLUFFY && moveType == PType.FIRE) multiplier *= 2;
			
			damage *= multiplier;
			
			if (foeAbility == Ability.UNERODIBLE && (moveType == PType.GRASS || moveType == PType.WATER || moveType == PType.GROUND)) damage /= 4;
			if (foeAbility == Ability.THICK_FAT && (moveType == PType.FIRE || moveType == PType.ICE)) damage /= 2;
			if (foeAbility == Ability.UNWAVERING && (moveType == PType.DARK || moveType == PType.GHOST)) damage /= 2;
			if (foeAbility == Ability.FLUFFY && move.contact) damage /= 2;
			
			if (foeAbility == Ability.PSYCHIC_AURA && move.cat == 1) damage *= 0.8;
			if (foeAbility == Ability.GLACIER_AURA && move.cat == 0) damage *= 0.8;
			if (foeAbility == Ability.GALACTIC_AURA && (moveType == PType.PSYCHIC || moveType == PType.ICE)) damage /= 2;
			
			if (multiplier > 1) {
				message += "\nIt's super effective!";
				if (foeAbility == Ability.SOLID_ROCK || foeAbility == Ability.FILTER) damage /= 2;
				if (item == Item.EXPERT_BELT) damage *= 1.2;
				if (foe.item != null && foe.checkTypeResistBerry(moveType)) {
					addTask(Task.TEXT, foe.nickname + " ate its " + foe.item.toString() + " to weaken the attack!");
					foe.consumeItem(this);
					damage /= 2;
				}
				if (foe.item == Item.WEAKNESS_POLICY) {
					addTask(Task.TEXT, foe.nickname + " used its " + foe.item.toString() + "!");
					stat(foe, 0, 2, this);
					stat(foe, 2, 2, this);
					foe.consumeItem(this);
				}
			}
			if (multiplier < 1) {
				message += "\nIt's not very effective...";
				if (ability == Ability.TINTED_LENS) damage *= 2;
			}
			
			if (this.item == Item.LIFE_ORB) damage *= 1.3;
			if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS || move == Move.PSYWAVE) damage = this.level;
			if (move == Move.ENDEAVOR) {
				if (foe.currentHP > this.currentHP) {
					damage = foe.currentHP - this.currentHP;
				} else { fail(); } }
			if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
			if (move == Move.DRAGON_RAGE) damage = 40;
			if (move == Move.HORN_DRILL || move == Move.SHEER_COLD || move == Move.GUILLOTINE || move == Move.FISSURE) {
				if ((move == Move.SHEER_COLD && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) || foeAbility == Ability.STURDY || foe.level > this.level) {
					if (foeAbility == Ability.STURDY) addAbilityTask(foe);
					addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
					endMove();
					this.moveMultiplier = 1;
					return;
				}
				damage = foe.currentHP;
				addTask(Task.TEXT, "It's a one-hit KO!");
			}
			
			if (move == Move.ABSORB || move == Move.DREAM_EATER || move == Move.GIGA_DRAIN || move == Move.MEGA_DRAIN || move == Move.LEECH_LIFE || move == Move.DRAIN_PUNCH || move == Move.DRAINING_KISS || move == Move.HORN_LEECH || move == Move.PARABOLIC_CHARGE) {
				int healAmount;
				if (damage >= foe.currentHP) {
					healAmount = move == Move.DRAINING_KISS ? Math.max((int) Math.ceil(foe.currentHP / 1.333333333333), 1) : Math.max((int) Math.ceil(foe.currentHP / 2.0), 1);
				} else {
					healAmount = move == Move.DRAINING_KISS ? Math.max((int) Math.ceil(damage / 1.333333333333), 1) : Math.max((int) Math.ceil(damage / 2.0), 1);
				}
				if (item == Item.BIG_ROOT) healAmount *= 1.3;
				heal(healAmount, this.nickname + " sucked HP from " + foe.nickname + "!");
			}
			
			damage = Math.max(damage, 1);
			
			if (this.ability == Ability.SERENE_GRACE) secChance *= 2;
			
			int recoil = 0;
			if (Move.getRecoil().contains(move) && ability != Ability.ROCK_HEAD && ability != Ability.MAGIC_GUARD && ability != Ability.SCALY_SKIN) {
				recoil = Math.max(Math.floorDiv(damage, 3), 1);
				if (damage >= foe.currentHP) recoil = Math.max(Math.floorDiv(foe.currentHP, 3), 1);
				if (move == Move.STEEL_BEAM) recoil = (int) (this.getStat(0) * 1.0 / 2);
				if (move == Move.STRUGGLE) recoil = (int) (this.getStat(0) * 1.0 / 4);
			}
			
			boolean fullHP = foe.currentHP == foe.getStat(0);
			boolean sturdy = false;
			if (damage >= foe.currentHP && (move == Move.FALSE_SWIPE || foe.vStatuses.contains(Status.ENDURE) || (foe.item == Item.FOCUS_BAND && checkSecondary(10)) || (fullHP && (foeAbility == Ability.STURDY || foe.item == Item.FOCUS_SASH)))) {
				sturdy = true;
			}
			
			// Damage foe
			int dividend = Math.min(damage, foe.currentHP);
			if (sturdy) dividend--;
			double percent = dividend * 100.0 / foe.getStat(0); // change dividend to damage
			String formattedPercent = String.format("%.1f", percent);
			String damagePercentText = "(" + foe.nickname + " lost " + formattedPercent + "% of its HP.)";
			
			if (numHits > 1) {
				if (hit == 1) {
					foe.damage(damage, this, move, message + "\n" + damagePercentText, damageIndex, sturdy);
				} else {
					foe.damage(damage, this, move, damagePercentText, -1, sturdy);
				}
			} else {
				addTask(Task.TEXT, damagePercentText);
				foe.damage(damage, this, move, message, damageIndex, sturdy);
			}
			
			if (foe.item == Item.AIR_BALLOON) {
				addTask(Task.TEXT, foe.nickname + "'s Air Balloon popped!");
				foe.consumeItem(this);
			}
			if (this.item == Item.THROAT_SPRAY && Move.getSound().contains(move)) {
				addTask(Task.TEXT, this.nickname + " used its Throat Spray!");
				stat(this, 2, 1, foe);
				this.consumeItem(foe);
			}
			if (sturdy) {
				foe.currentHP = 1;
				if (fullHP && foeAbility == Ability.STURDY) addAbilityTask(foe);
				if (foe.item == Item.FOCUS_SASH) {
					addTask(Task.TEXT, foe.nickname + " hung on using its Focus Sash!");
					foe.consumeItem(this);
				} else if (move != Move.FALSE_SWIPE) {
					addTask(Task.TEXT, foe.name + " endured the hit!");
				}
			}
			if (foe.currentHP <= 0) { // Check for kill
				foe.faint(true, this);
				if (move == Move.FELL_STINGER) stat(this, 0, 3, foe);
				if (move == Move.SUNNY_DOOM) field.setWeather(field.new FieldEffect(Effect.SUN));
				if (this.vStatuses.contains(Status.BONDED)) {
					addTask(Task.TEXT, foe.nickname + " took its attacker down with it!");
					this.faint(true, foe);
				}
				if (this.ability == Ability.MOXIE) {
					addAbilityTask(this);
					stat(this, 0, 1, foe);
				}
			}
			
			if (recoil != 0) {
				this.damage(recoil, foe, this.nickname + " was damaged by recoil!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, foe);
				}
			}
			
			if (move.contact && checkSecondary(30) && this.status == Status.HEALTHY) {
				if (foeAbility == Ability.FLAME_BODY) {
					addAbilityTask(foe);
					burn(false, this);
				}
				if (foeAbility == Ability.STATIC && this.status == Status.HEALTHY) {
					addAbilityTask(foe);
					paralyze(false, this);
				}
				if (foeAbility == Ability.POISON_POINT && this.status == Status.HEALTHY) {
					addAbilityTask(foe);
					poison(false, this);
				}
			}
			
			if (move.contact) {
				if ((foeAbility == Ability.ROUGH_SKIN || foeAbility == Ability.IRON_BARBS) && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
					addAbilityTask(foe);
					this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), foe);
					addTask(Task.TEXT, this.nickname + " was hurt!");
				}
				if (foe.item == Item.ROCKY_HELMET && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
					this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), foe);
					addTask(Task.TEXT, this.nickname + " was hurt by the Rocky Helmet!");
				}
				if ((foeAbility == Ability.ROUGH_SKIN || foeAbility == Ability.IRON_BARBS || foe.item == Item.ROCKY_HELMET) && this.currentHP <= 0) { // Check for kill
					this.faint(true, foe);
					if (move == Move.FELL_STINGER) stat(this, 0, 3, foe);
				}
				if (this.ability == Ability.POISON_TOUCH && foe.status == Status.HEALTHY && checkSecondary(30)) {
					addAbilityTask(this);
					foe.poison(false, this);
				}
			}
			
			if (move.isPhysical()) {
				if (foeAbility == Ability.WEAK_ARMOR) {
					addAbilityTask(foe);
					stat(foe, 1, -1, this);
					stat(foe, 4, 2, this);
				}
				if (foeAbility == Ability.TOXIC_DEBRIS) {
					addAbilityTask(foe);
					field.setHazard(userSide, field.new FieldEffect(Effect.TOXIC_SPIKES));
				}
			}
			
			if ((moveType == PType.BUG || moveType == PType.GHOST || moveType == PType.DARK) && foeAbility == Ability.RATTLED && !foe.isFainted()) {
				addAbilityTask(foe);
				stat(foe, 4, 1, this);
			}
			
			if (moveType == PType.DARK && foeAbility == Ability.JUSTIFIED && !foe.isFainted()) {
				addAbilityTask(foe);
				stat(foe, 0, 1, this);
			}
			if (move == Move.POP_POP && hit == numHits) {
				addTask(Task.TEXT, "Hit " + hits + " times!");
			} else if (hit == numHits && hit > 1) {
				addTask(Task.TEXT, "Hit " + hit + " times!");
			}
			
			if (first && this.item == Item.KING1S_ROCK && !foe.vStatuses.contains(Status.FLINCHED) && checkSecondary(10)) {
				foe.vStatuses.add(Status.FLINCHED);
			}
		}
		if (checkSecondary(secChance)) {
			secondaryEffect(foe, move, first, userSide, enemySide, player, enemy);
		}
		
		if (this.item == Item.LIFE_ORB && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			this.damage(this.getStat(0) / 10, foe);
			addTask(Task.TEXT, this.nickname + " lost some of its HP!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, foe);
			}
		}
		
		if ((move == Move.VOLT_SWITCH || move == Move.FLIP_TURN || move == Move.U$TURN) && !this.isFainted()) {
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				addTask(Task.TEXT, this.nickname + " went back to " + enemy.getName() + "!");
			}
			this.vStatuses.add(Status.SWITCHING);
		}
		
		if (move == Move.SELF$DESTRUCT || move == Move.EXPLOSION || move == Move.SUPERNOVA_EXPLOSION) {
			this.faint(true, foe);
		}
		if (move == Move.HYPER_BEAM || move == Move.BLAST_BURN || move == Move.FRENZY_PLANT || move == Move.GIGA_IMPACT || move == Move.HYDRO_CANNON || move == Move.MAGIC_CRASH) {
			if (this.item == Item.POWER_HERB) {
				this.consumeItem(foe);
				addTask(Task.TEXT, this.nickname + " used its Power Herb to regain its lost energy!");
			} else {
				this.vStatuses.add(Status.RECHARGE);
			}
		}
		
		if (foe.item == Item.RED_CARD) {
			int index = gp.battleUI.tasks.size();
			boolean result = false;
			if (this.trainerOwned() && enemy != null) {
				result = enemy.swapRandom(foe, player);
			} else if (this.playerOwned()) {
				result = player.swapRandom(foe);
			}
			if (result) {
				Task t = createTask(Task.TEXT, foe.nickname + " held up its Red Card!");
				insertTask(t, index);
				foe.consumeItem(this);
				endMove();
				return;
			}
		}
		
		if (move == Move.MAGIC_BLAST) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.values()) {
				if (cand.mtype == PType.ROCK || cand.mtype == PType.GRASS || cand.mtype == PType.GROUND) {
					moves.add(cand);
				}
			}
			Move[] validMoves = moves.toArray(new Move[moves.size()]);
			this.move(foe, validMoves[new Random().nextInt(validMoves.length)], first);
		}
		if (move == Move.ELEMENTAL_SPARKLE) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.values()) {
				if (cand.mtype == PType.FIRE || cand.mtype == PType.WATER || cand.mtype == PType.GRASS) {
					moves.add(cand);
				}
			}
			Move[] validMoves = moves.toArray(new Move[moves.size()]);
			this.move(foe, validMoves[new Random().nextInt(validMoves.length)], first);
		}
		endMove();
		return;
	}

	private double getHPAmount(double fraction) {
		return Math.max(this.getStat(0) * fraction, 1);
	}

	private int getxpReward() {
		int result = (int) Math.ceil(this.level * (trainerOwned() ? 1.5 : 1.0));
		return result;
	}

	private void useMove(Move move, Pokemon foe) {
		announceUseMove(move);
		for (Moveslot m : this.moveset) {
			if (m != null && m.move == move) {
				m.currentPP -= foe.ability == Ability.PRESSURE ? 2 : 1;
				m.currentPP = m.currentPP < 0 ? 0 : m.currentPP;
			}
			if (this.item == Item.LEPPA_BERRY && m.currentPP == 0) {
				m.currentPP = 10;
				addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to restore PP to " + m.move.toString() + "!");
				this.consumeItem(foe);
			}
		}
	}
	
	private void announceUseMove(Move move) {
		Task t = addTask(Task.SEMI_INV, this.nickname + " used " + move.toString() + "!", this);
		t.wipe = true;
	}

	private void endMove() {
		impressive = false;
		success = true;
	}
	
	private boolean fail() {
		return fail(true);
	}

	private boolean fail(boolean announce) {
		if (announce) addTask(Task.TEXT, "But it failed!");
		success = false;
		return true;
	}
	
	private double boostItemCheck(PType type) {
		if (item == Item.BLACK_BELT && type == PType.FIGHTING) return 1.2;
		if (item == Item.BLACK_GLASSES && type == PType.DARK) return 1.2;
		if (item == Item.CHARCOAL && type == PType.FIRE) return 1.2;
		if (item == Item.COSMIC_CORE && type == PType.GALACTIC) return 1.2;
		if (item == Item.DRAGON_FANG && type == PType.DRAGON) return 1.2;
		if (item == Item.ENCHANTED_AMULET && type == PType.MAGIC) return 1.2;
		if (item == Item.GLOWING_PRISM && type == PType.LIGHT) return 1.2;
		if (item == Item.HARD_STONE && type == PType.ROCK) return 1.2;
		if (item == Item.MAGNET && type == PType.ELECTRIC) return 1.2;
		if (item == Item.METAL_COAT && type == PType.STEEL) return 1.2;
		if (item == Item.MIRACLE_SEED && type == PType.GRASS) return 1.2;
		if (item == Item.MYSTIC_WATER && type == PType.WATER) return 1.2;
		if (item == Item.NEVER$MELT_ICE && type == PType.ICE) return 1.2;
		if (item == Item.POISON_BARB && type == PType.POISON) return 1.2;
		if (item == Item.SHARP_BEAK && type == PType.FLYING) return 1.2;
		if (item == Item.SILK_SCARF && type == PType.NORMAL) return 1.2;
		if (item == Item.SILVER_POWDER && type == PType.BUG) return 1.2;
		if (item == Item.SOFT_SAND && type == PType.GROUND) return 1.2;
		if (item == Item.SPELL_TAG && type == PType.GHOST) return 1.2;
		if (item == Item.TWISTED_SPOON && type == PType.PSYCHIC) return 1.2;
		return 1;
	}
	
	private boolean checkTypeResistBerry(PType type) {
		if (this.item == Item.OCCA_BERRY && type == PType.FIRE) return true;
		if (this.item == Item.PASSHO_BERRY && type == PType.WATER) return true;
		if (this.item == Item.WACAN_BERRY && type == PType.ELECTRIC) return true;
		if (this.item == Item.RINDO_BERRY && type == PType.GRASS) return true;
		if (this.item == Item.YACHE_BERRY && type == PType.ICE) return true;
		if (this.item == Item.CHOPLE_BERRY && type == PType.FIGHTING) return true;
		if (this.item == Item.KEBIA_BERRY && type == PType.POISON) return true;
		if (this.item == Item.SHUCA_BERRY && type == PType.GROUND) return true;
		if (this.item == Item.COBA_BERRY && type == PType.FLYING) return true;
		if (this.item == Item.PAYAPA_BERRY && type == PType.PSYCHIC) return true;
		if (this.item == Item.TANGA_BERRY && type == PType.BUG) return true;
		if (this.item == Item.CHARTI_BERRY && type == PType.ROCK) return true;
		if (this.item == Item.KASIB_BERRY && type == PType.GHOST) return true;
		if (this.item == Item.HABAN_BERRY && type == PType.DRAGON) return true;
		if (this.item == Item.COLBUR_BERRY && type == PType.DARK) return true;
		if (this.item == Item.BABIRI_BERRY && type == PType.STEEL) return true;
		if (this.item == Item.CHILAN_BERRY && type == PType.NORMAL) return true;
		if (this.item == Item.ROSELI_BERRY && type == PType.LIGHT) return true;
		if (this.item == Item.MYSTICOLA_BERRY && type == PType.MAGIC) return true;
		if (this.item == Item.GALAXEED_BERRY && type == PType.GALACTIC) return true;
		return false;
	}

	public void awardxp(int amt) {
	    if (this.fainted) return;
	    if (!this.playerOwned()) return;
	    Player player = (Player) this.trainer;

	    ArrayList<Pokemon> teamCopy = new ArrayList<>(Arrays.asList(player.getTeam()));
	    player.handleExpShare();
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
	            	if (p.item == Item.LUCKY_EGG) expAwarded *= 1.5;
	                p.exp += expAwarded;
	                String flavor = p.item == Item.LUCKY_EGG ? "a boosted " : p.item == Item.EXP_SHARE && !p.visible ? "a shared " : "";
                	Task t = addTask(Task.EXP, p.nickname + " gained " + flavor + expAwarded + " experience points!", p);
                	t.setFinish(Math.min(p.exp, expMax));
	            }
	            while (p.exp >= p.expMax) {
	                // Pokemon has leveled up, check for evolution
	                p.levelUp(player);
	                int index = Arrays.asList(player.getTeam()).indexOf(p);
	                p = player.team[index];
	            }
	        }
	    }
	}



	public void awardHappiness(int i, boolean override) {
		int deduc = 0;
		if (this.item == Item.SOOTHE_BELL && i > 0) i *= 2;
		if (!override) {
			this.happinessCap -= i;
			deduc = happinessCap < 0 ? Math.abs(happinessCap) : 0;
			this.happinessCap += deduc;
		}
		this.happiness += i;
		this.happiness -= deduc;
		this.happiness = this.happiness > 255 ? 255 : this.happiness;
	}

	private void secondaryEffect(Pokemon foe, Move move, boolean first, ArrayList<FieldEffect> userSide, ArrayList<FieldEffect> enemySide, Player player, Trainer enemy) {
		if (move == Move.ABYSSAL_CHOP) {
		    foe.paralyze(false, this);
		} else if (move == Move.ACID) {
			stat(foe, 3, -1, this);
		} else if (move == Move.ACID_SPRAY) {
			stat(foe, 3, -2, this);
		} else if (move == Move.ANCIENT_POWER) {
			for (int i = 0; i < 5; ++i) {
				stat(this, i, 1, foe);
			}
		} else if (move == Move.AIR_SLASH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ASTONISH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.AURORA_BEAM) {
			stat(foe, 0, -1, this);
		} else if (move == Move.BEEFY_BASH) {
			foe.paralyze(false, this);
//		} else if (move == Move.BIG_BULLET) {
//			foe.paralyze(false, this);
		} else if (move == Move.BIND && !foe.isFainted()) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				addTask(Task.TEXT, foe.nickname + " was wrapped by " + this.nickname + "!");
			}
		} else if (move == Move.BITE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.BREAKING_SWIPE) {
			stat(foe, 0, -1, this);
//		} else if (move == Move.BLAST_FLAME) {
//			foe.burn(false, this);
		} else if (move == Move.BLAZE_KICK) {
			foe.burn(false, this);
//		} else if (move == Move.BLAZING_SWORD) {
//			foe.burn(false, this);
		} else if (move == Move.BLIZZARD) {
			foe.freeze(false, this);
		} else if (move == Move.BLUE_FLARE) {
			foe.burn(false, this);
		} else if (move == Move.BODY_SLAM) {
			foe.paralyze(false, this);
		} else if (move == Move.BOLT_STRIKE) {
			foe.paralyze(false, this);
		} else if (move == Move.BRICK_BREAK || move == Move.PSYCHIC_FANGS) {
			if (field.remove(enemySide, Effect.REFLECT)) addTask(Task.TEXT, foe.nickname + "'s Reflect wore off!");
			if (field.remove(enemySide, Effect.LIGHT_SCREEN)) addTask(Task.TEXT, foe.nickname + "'s Light Screen wore off!");
			if (field.remove(enemySide, Effect.AURORA_VEIL)) addTask(Task.TEXT, foe.nickname + "'s Aurora Veil wore off!");
		} else if (move == Move.BOUNCE) {
			foe.paralyze(false, this);
		} else if (move == Move.BUBBLEBEAM) {
			stat(foe, 4, -1, this);
		} else if (move == Move.BUG_BITE || move == Move.PLUCK) {
			if (foe.item != null && foe.item.isBerry()) {
				addTask(Task.TEXT, this.nickname + " stole and ate " + foe.nickname + "'s " + foe.item.toString() + "!");
				eatBerry(foe.item, false, foe);
				foe.consumeItem(this);
			}
		} else if (move == Move.BUG_BUZZ) {
			stat(foe, 3, -1, this);
		} else if (move == Move.BURN_UP) {
			if (this.type1 == PType.FIRE) type1 = PType.UNKNOWN;
			if (this.type2 == PType.FIRE) type2 = null;
		} else if (move == Move.BULLDOZE) {
			stat(foe, 4, -1, this);
		} else if (move == Move.CHARGE_BEAM) {
			stat(this, 2, 1, this);
		} else if ((move == Move.CIRCLE_THROW || move == Move.DRAGON_TAIL) && !foe.isFainted()) {
			if (foe.trainerOwned() && enemy != null) {
				enemy.swapRandom(foe, player);
			} else if (foe.playerOwned()) {
				player.swapRandom(foe);
			}
		} else if (move == Move.CLOSE_COMBAT) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
		} else if (move == Move.CONFUSION) {
			foe.confuse(false, this);
		} else if (move == Move.CORE_ENFORCER) {
			stat(foe, 0, -1, this);
			stat(foe, 2, -1, this);
//		} else if (move == Move.CONSTRICT) {
//			stat(foe, 4, -1);
		} else if (move == Move.COVET || move == Move.THIEF) {
			if (this.item == null && foe.item != null) {
				addTask(Task.TEXT, this.nickname + " stole the foe's " + foe.item.toString() + "!");
				this.item = foe.item;
				if (!foe.loseItem) foe.lostItem = foe.item;
				foe.item = null;
				this.loseItem = true;
			}
		} else if (move == Move.CROSS_POISON) {
			foe.poison(false, this);
		} else if (move == Move.CRUNCH) {
			stat(foe, 1, -1, this);
		} else if (move == Move.DARK_PULSE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.DESOLATE_VOID) {
			Random random = new Random();
			int type = random.nextInt(3);
			if (type == 0) {
				foe.paralyze(false, this);
			} else if (type == 1) {
				foe.sleep(false, this);
			} else {
				foe.freeze(false, this);
			}
		} else if (move == Move.DISCHARGE) {
			foe.paralyze(false, this);
		} else if (move == Move.DIAMOND_STORM) {
			stat(this, 1, 2, foe);
//		} else if (move == Move.DOUBLE_SLICE) {
//			if (foe.status == Status.HEALTHY) {
//				foe.status = Status.BLEEDING;
//				addTask(Task.TEXT, foe.nickname + " is bleeding!");
//			}
		} else if (move == Move.DRACO_METEOR) {
			stat(this, 2, -2, foe);
		} else if (move == Move.DRAGON_RUSH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.DRAGON_BREATH) {
			foe.paralyze(false, this);
		} else if (move == Move.DYNAMIC_PUNCH) {
			foe.confuse(false, this);
		} else if (move == Move.EARTH_POWER) {
			stat(foe, 3, -1, this);
		} else if (move == Move.ELECTROWEB) {
			stat(foe, 4, -1, this);
		} else if (move == Move.EMBER) {
			foe.burn(false, this);
		} else if (move == Move.ENERGY_BALL) {
			stat(foe, 3, -1, this);
		} else if (move == Move.EXTRASENSORY && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.FAKE_OUT && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.UNSEEN_STRANGLE && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.FIERY_DANCE) {
			stat(this, 2, 1, foe);
		} else if (move == Move.FIRE_BLAST) {
			foe.burn(false, this);
		} else if (move == Move.FATAL_BIND) {
			this.perishCount = (this.perishCount == 0) ? 4 : this.perishCount;
			foe.perishCount = (foe.perishCount == 0) ? 4 : foe.perishCount;
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
			if (!foe.vStatuses.contains(Status.SPUN) && !foe.isFainted()) {
				if (foe.type1 != PType.FIRE && foe.type2 != PType.FIRE) {
					foe.vStatuses.add(Status.SPUN);
					foe.spunCount = (((int) (Math.random() * 4)) + 2);
					addTask(Task.TEXT, foe.nickname + " was trapped in a fiery vortex!");
				}
			}
		} else if (move == Move.WHIRLPOOL) {
			if (!foe.vStatuses.contains(Status.SPUN) && !foe.isFainted()) {
				if (foe.type1 != PType.WATER && foe.type2 != PType.WATER) {
					foe.vStatuses.add(Status.SPUN);
					foe.spunCount = (((int) (Math.random() * 4)) + 2);
					addTask(Task.TEXT, foe.nickname + " was trapped in a whirlpool vortex!");
				}
			}
		} else if (move == Move.WRAP) {
			if (!foe.vStatuses.contains(Status.SPUN) && !foe.isFainted()) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				addTask(Task.TEXT, foe.nickname + " was wrapped by " + this.nickname + "!");
			}
//		} else if (move == Move.FIRE_TAIL) {
//			foe.burn(false, this);
		} else if (move == Move.FLAME_CHARGE) {
			stat(this, 4, 1, foe);
		} else if (move == Move.FLAME_WHEEL) {
			foe.burn(false, this);
		} else if (move == Move.FLAMETHROWER) {
			foe.burn(false, this);
		} else if (move == Move.FLARE_BLITZ) {
			foe.burn(false, this);
		} else if (move == Move.FLASH_CANNON) {
			stat(foe, 3, -1, this);
		} else if (move == Move.FLASH_RAY) {
			stat(foe, 5, -1, this);
		} else if (move == Move.FOCUS_BLAST) {
			stat(foe, 3, -1, this);
		} else if (move == Move.FORCE_PALM) {
			foe.paralyze(false, this);
		} else if (move == Move.FREEZE$DRY) {
			foe.freeze(false, this);
		} else if (move == Move.FREEZING_GLARE) {
			foe.freeze(false, this);
		} else if (move == Move.GLACIATE) {
			stat(foe, 4, -1, this);
		} else if (move == Move.GLITTERING_SWORD) {
			stat(foe, 1, -1, this);
		} else if (move == Move.GLITTERING_TORNADO) {
			stat(foe, 5, -1, this);
		} else if (move == Move.GLITZY_GLOW) {
			stat(this, 3, 1, foe);
		} else if (move == Move.GUNK_SHOT) {
			foe.poison(false, this);
		} else if (move == Move.HAMMER_ARM) {
			stat(this, 4, -1, foe);
		} else if (move == Move.HEADBUTT && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.HEAT_WAVE) {
			foe.burn(false, this);
		} else if (move == Move.HOCUS_POCUS) {
			int randomNum = ((int) Math.random() * 5);
			switch (randomNum) {
			case 0:
				foe.burn(false, this);
				break;
			case 1:
				foe.sleep(false, this);
				break;
			case 2:
				foe.paralyze(false, this);
				break;
			case 3:
				boolean result = new Random().nextBoolean();
				if (result) {
					foe.poison(false, this);
				} else {
					foe.toxic(false, this);
				}
				break;
			case 4:
				foe.freeze(false, this);
				break;
			default:
				return;
			}
		} else if (move == Move.HURRICANE) {
			foe.confuse(false, this);
		} else if (move == Move.HYPER_FANG && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.INCINERATE) {
			if (foe.item != null && foe.item.isBerry()) {
				addTask(Task.TEXT, this.nickname + " incinerated " + foe.nickname + "'s " + foe.item.toString() + "!");
				foe.item = null;
			}
		} else if (move == Move.ICE_BEAM) {
			foe.freeze(false, this);
		} else if (move == Move.ICE_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.freeze(false, this);
			} else if (randomNum == 1 && first) {
				foe.vStatuses.add(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.vStatuses.add(Status.FLINCHED);
				foe.freeze(false, this);
			}
		} else if (move == Move.ICE_PUNCH) {
			foe.freeze(false, this);
		} else if (move == Move.ICICLE_CRASH && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.ICE_SPINNER) {
			if (field.terrain != null) {
				field.terrain = null;
				field.terrainTurns = 0;
				Task t = addTask(Task.TERRAIN, "The terrain returned to normal!");
				t.setEffect(null);
			}
		} else if (move == Move.ICY_WIND) {
			stat(foe, 4, -1, this);
		} else if (move == Move.INFERNO) {
			foe.burn(false, this);
		}  else if (move == Move.INFESTATION) {
			if (!foe.vStatuses.contains(Status.SPUN)) {
				foe.vStatuses.add(Status.SPUN);
				foe.spunCount = (((int) (Math.random() * 4)) + 2);
				addTask(Task.TEXT, foe.nickname + " was infested by " + this.nickname + "!");
			}
		} else if (move == Move.IRON_BLAST && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.IRON_HEAD && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.IRON_TAIL) {
			stat(foe, 1, -1, this);
		} else if (move == Move.JAW_LOCK) {
			if (!foe.vStatuses.contains(Status.TRAPPED) && !foe.isFainted()) {
				foe.vStatuses.add(Status.TRAPPED);
				addTask(Task.TEXT, foe.nickname + " was trapped!");
			}
		} else if (move == Move.KNOCK_OFF) {
			if (foe.item != null) {
				Item oldItem = foe.item;
				if (foe.lostItem == null) foe.lostItem = foe.item;
				addTask(Task.TEXT, this.nickname + " knocked off " + foe.nickname + "'s " + oldItem.toString() + "!");
				foe.consumeItem(this);
			}
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
			stat(this, 2, -2, foe);
		} else if (move == Move.LEAF_TORNADO) {
			stat(foe, 5, -1, this);
		} else if (move == Move.LICK) {
			foe.paralyze(false, this);
		} else if (move == Move.LIGHT_BEAM) {
			stat(this, 2, 1, foe);
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
			stat(foe, 1, -1, this);
		} else if (move == Move.LOW_SWEEP) {
			stat(foe, 4, -1, this);
		} else if (move == Move.LUSTER_PURGE) {
			stat(foe, 3, -1, this);
    	} else if (move == Move.STAFF_JAB) {
    		stat(foe, 0, -2, this);
		} else if (move == Move.MAGIC_CRASH) {
			int randomNum = ((int) Math.random() * 5);
			switch (randomNum) {
			case 0:
				foe.burn(false, this);
				break;
			case 1:
				foe.sleep(false, this);
				break;
			case 2:
				foe.paralyze(false, this);
				break;
			case 3:
				boolean result = new Random().nextBoolean();
				if (result) {
					foe.poison(false, this);
				} else {
					foe.toxic(false, this);
				}
				break;
			case 4:
				foe.freeze(false, this);
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
			stat(this, 0, 1, foe);
		} else if (move == Move.METEOR_ASSAULT) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
		} else if (move == Move.METEOR_MASH) {
			stat(this, 0, 1, foe);
		} else if (move == Move.MIRROR_SHOT) {
			stat(this, 5, -1, foe);
		} else if (move == Move.MIST_BALL) {
			stat(foe, 2, -1, this);
		} else if (move == Move.MOLTEN_STEELSPIKE) {
			foe.burn(false, this);
		} else if (move == Move.MOLTEN_CONSUME) {
			foe.burn(false, this);
		} else if (move == Move.MORTAL_SPIN) {
			foe.poison(false, this);
			if (this.vStatuses.contains(Status.SPUN)) {
				this.vStatuses.remove(Status.SPUN);
				addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
			for (FieldEffect fe : field.getHazards(userSide)) {
				addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				userSide.remove(fe);
			}
		} else if (move == Move.MOONBLAST) {
			stat(foe, 2, -1, this);
		} else if (move == Move.MUD_BOMB) {
			stat(foe, 5, -1, this);
		} else if (move == Move.MUD_SHOT) {
			stat(foe, 4, -1, this);
		} else if (move == Move.MUD$SLAP) {
			stat(foe, 5, -1, this);
		} else if (move == Move.MUDDY_WATER) {
			stat(this, 5, -1, foe);
		} else if (move == Move.MYSTICAL_FIRE) {
			stat(foe, 2, -1, this);
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
			stat(foe, 5, -1, this);
		} else if (move == Move.OVERHEAT) {
			stat(this, 2, -2, foe);
//		} else if (move == Move.POISON_BALL) {
//			foe.poison(false, this);
		} else if (move == Move.PHOTON_GEYSER) {
			stat(this, 2, -2, foe);
		} else if (move == Move.PLAY_ROUGH) {
			stat(foe, 0, -1, this);
		} else if (move == Move.POISON_FANG) {
			foe.toxic(false, this);
		} else if (move == Move.POISON_JAB) {
			foe.poison(false, this);
//		} else if (move == Move.POISON_PUNCH) {
//			foe.poison(false, this);
		} else if (move == Move.POISON_STING) {
			foe.poison(false, this);
//		} else if (move == Move.POISONOUS_WATER) {
//			foe.poison(false, this);
		} else if (move == Move.POWDER_SNOW) {
			foe.freeze(false, this);
		} else if (move == Move.POWER$UP_PUNCH) {
			stat(this, 0, 1, foe);
		} else if (move == Move.PSYBEAM) {
			foe.confuse(false, this);
		} else if (move == Move.PSYCHIC) {
			stat(foe, 3, -1, this);
		} else if (move == Move.RAPID_SPIN) {
			stat(this, 4, 1, foe);
			if (this.vStatuses.contains(Status.SPUN)) {
				this.vStatuses.remove(Status.SPUN);
				addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
			for (FieldEffect fe : field.getHazards(userSide)) {
				addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				userSide.remove(fe);
			}
		} else if (move == Move.RAZOR_SHELL) {
			stat(foe, 1, -1, this);
		} else if (move == Move.ROCK_CLIMB) {
			foe.confuse(false, this);
		} else if (move == Move.ROCK_SMASH) {
			stat(foe, 1, -1, this);
		} else if (move == Move.ROCK_TOMB) {
			stat(foe, 4, -1, this);
		} else if (move == Move.ROCKFALL_FRENZY) {
			field.setHazard(enemySide, field.new FieldEffect(Effect.STEALTH_ROCKS));
//		} else if (move == Move.ROOT_CRUSH) {
//			foe.paralyze(false, this);
		} else if (move == Move.SACRED_FIRE) {
			foe.burn(false, this);
		} else if (move == Move.SCALD) {
			foe.burn(false, this);
		} else if (move == Move.SCORCHING_SANDS) {
			foe.burn(false, this);
		} else if (move == Move.SHADOW_BALL) {
			stat(foe, 3, -1, this);
		} else if (move == Move.SILVER_WIND) {
			for (int i = 0; i < 5; ++i) {
				stat(this, i, 1, foe);
			}
		} else if (move == Move.SMACK_DOWN && !foe.vStatuses.contains(Status.SMACK_DOWN)) {
			foe.vStatuses.add(Status.SMACK_DOWN);
			addTask(Task.TEXT, foe.nickname + " was grounded!");
		} else if (move == Move.SLUDGE_WAVE) {
			foe.poison(false, this);
		} else if (move == Move.SUMMIT_STRIKE) {
		    stat(foe, 1, -1, this);
		    double random = Math.random();
		    if (random <= 0.3 && first) {
		        foe.vStatuses.add(Status.FLINCHED);
		    }
		} else if (move == Move.NUZZLE) {
			foe.paralyze(false, this);
//		} else if (move == Move.SHURIKEN && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			addTask(Task.TEXT, foe.nickname + " is bleeding!");
		} else if (move == Move.SKY_ATTACK && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SLOW_FALL) {
			this.ability = Ability.LEVITATE;
			addTask(Task.TEXT, this.nickname + "'s ability was changed to Levitate!");
		} else if (move == Move.SLUDGE) {
			foe.poison(false, this);
		} else if (move == Move.SLUDGE_BOMB) {
			foe.poison(false, this);
		} else if (move == Move.SMOG) {
			foe.poison(false, this);
		} else if (move == Move.SNARL) {
			stat(foe, 2, -1, this);
		} else if (move == Move.SPACE_BEAM) {
			stat(foe, 3, -1, this);
		} else if (move == Move.SPARK) {
			foe.paralyze(false, this);
		} else if (move == Move.SPARKLING_ARIA) {
			if (status == Status.BURNED) {
				status = Status.HEALTHY;
				addTask(Task.STATUS, Status.HEALTHY, nickname + " was cured of its burn!", this);
			}
		} else if (move == Move.SPARKLY_SWIRL) {
			for (int i = 0; i < 5; ++i) {
				stat(foe, i, -1, this);
			}
		} else if (move == Move.SPECTRAL_THIEF) {
			for (int i = 0; i < 7; ++i) {
				if (foe.statStages[i] > 0) {
					stat(this, i, foe.statStages[i], foe);
					foe.statStages[i] = 0;
				}
			}
//		} else if (move == Move.SPIKE_JAB) {
//			foe.poison(false, this);
		} else if (move == Move.SPIRIT_BREAK) {
			stat(foe, 2, -1, this);
		} else if (move == Move.STEEL_WING) {
			stat(this, 1, 1, foe);
//		} else if (move == Move.STING && foe.status == Status.HEALTHY) {
//			foe.status = Status.BLEEDING;
//			addTask(Task.TEXT, foe.nickname + " is bleeding!");
		} else if (move == Move.STRUGGLE_BUG) {
			stat(foe, 2, -1, this);
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
			stat(this, 0, -1, foe);
			stat(this, 1, -1, foe);
//		} else if (move == Move.SWEEP_KICK) {
//			stat(foe, 0, -1);
		} else if (move == Move.SWORD_SPIN) {
			stat(this, 0, 1, foe);
		} else if (move == Move.THROAT_CHOP) {
			if (!foe.vStatuses.contains(Status.MUTE)) foe.vStatuses.add(Status.MUTE);
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
		} else if (move == Move.TRI$ATTACK) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.burn(false, this);
			} else if (randomNum == 1) {
				foe.paralyze(false, this);
			}
			 else if (randomNum == 2) {
				foe.freeze(false, this);
			}
		} else if (move == Move.TORNADO_SPIN) {
			stat(this, 4, 1, foe);
			stat(this, 5, 1, foe);
			if (this.vStatuses.contains(Status.SPUN)) {
				this.vStatuses.remove(Status.SPUN);
				addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
		} else if (move == Move.TWINKLE_TACKLE) {
			stat(foe, 0, -1, this);
			stat(foe, 2, -1, this);
		} else if (move == Move.TWINEEDLE) {
			foe.poison(false, this);
		} else if (move == Move.SCALE_SHOT) {
			stat(this, 1, -1, foe);
			stat(this, 4, 1, foe);
		} else if (move == Move.TWISTER && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.VENOM_SPIT) {
			foe.paralyze(false, this);
		} else if (move == Move.VOLT_TACKLE) {
			foe.paralyze(false, this);
		} else if (move == Move.V$CREATE) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
			stat(this, 4, -1, foe);
		} else if (move == Move.VINE_CROSS) {
			stat(foe, 4, -1, this);
		} else if (move == Move.WATER_PULSE) {
			foe.confuse(false, this);
		} else if (move == Move.WATER_CLAP) {
			foe.paralyze(false, this);
		} else if (move == Move.WATER_SMACK && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SUPERCHARGED_SPLASH) {
			stat(this, 2, 2, foe);
		} else if (move == Move.VANISHING_ACT) {
			foe.confuse(false, this);
		} else if (move == Move.WATERFALL && first) {
			foe.vStatuses.add(Status.FLINCHED);
		} else if (move == Move.SPIRIT_BREAK) {
			stat(this, 1, -1, foe);
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
	
	private void statusEffect(Pokemon foe, Move move, Player player, Pokemon[] team, Trainer enemy, ArrayList<FieldEffect> userSide, ArrayList<FieldEffect> enemySide) {
		statusEffect(foe, move, player, team, enemy, userSide, enemySide, true);
	}

	private void statusEffect(Pokemon foe, Move move, Player player, Pokemon[] team, Trainer enemy, ArrayList<FieldEffect> userSide, ArrayList<FieldEffect> enemySide,
			boolean announce) {
		boolean fail = false;
		if (announce && (move == Move.ABDUCT || move == Move.TAKE_OVER)) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success)) {
				foe.vStatuses.add(Status.POSESSED);
				addTask(Task.TEXT, this.nickname + " posessed " + foe.nickname + "!");
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (announce && move == Move.MAGIC_REFLECT) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success) && !vStatuses.contains(Status.REFLECT)) {
				this.vStatuses.add(Status.REFLECT);
				addTask(Task.TEXT, "A magical reflection surrounds " + this.nickname + "!");
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (move == Move.ACID_ARMOR) {
			stat(this, 1, 2, foe, announce);
		} else if (move == Move.AGILITY) {
			stat(this, 4, 2, foe, announce);
		} else if (move == Move.AMNESIA) {
			stat(this, 3, 2, foe, announce);
		} else if (announce && move == Move.AROMATHERAPY) {
			addTask(Task.TEXT, "A soothing aroma wafted through the air!");
			if (team == null) {
				if (this.status != Status.HEALTHY) {
					if (announce) addTask(Task.TEXT, this.nickname + " was cured of its " + this.status.getName() + "!");
					this.status = Status.HEALTHY;
				}
			} else {
				for (Pokemon p : team) {
					if (p != null && !p.isFainted() && p.status != Status.HEALTHY) {
						if (announce) addTask(Task.TEXT, p.nickname + " was cured of its " + p.status.getName() + "!");
						p.status = Status.HEALTHY;
					}
				}
			}
		} else if (announce && move == Move.AQUA_RING) {
			if (!(this.vStatuses.contains(Status.AQUA_RING))) {
			    this.vStatuses.add(Status.AQUA_RING);
			    if (announce) addTask(Task.TEXT, "A veil of water surrounded " + this.nickname + "!");
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.AURORA_VEIL) {
			if (field.equals(field.weather, Effect.SNOW)) {
				if (!(field.contains(userSide, Effect.AURORA_VEIL))) {
					FieldEffect a = field.new FieldEffect(Effect.AURORA_VEIL);
					if (this.item == Item.LIGHT_CLAY) a.turns = 8;
					userSide.add(a);
					if (announce) addTask(Task.TEXT, "Aurora Veil made " + this.nickname + "'s team stronger against Physical and Special moves!");
				} else {
					fail = fail(announce);
				}
			} else { fail = fail(announce); }
		} else if (move == Move.AUTOMOTIZE) {
			stat(this, 4, 2, foe, announce);
		} else if (move == Move.LOAD_FIREARMS) {
			if (announce) addTask(Task.TEXT, this.nickname + " upgraded its weapon!");
			stat(this, 4, 1, foe, announce);
			stat(this, 5, 1, foe, announce);
			if (!this.vStatuses.contains(Status.LOADED)) this.vStatuses.add(Status.LOADED);
		} else if (move == Move.BABY$DOLL_EYES) {
			stat(foe, 0, -1, this, announce);
		} else if (announce && (move == Move.BATON_PASS || move == Move.TELEPORT)) {
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				addTask(Task.TEXT, this.nickname + " went back to " + enemy.getName() + "!");
				this.vStatuses.add(Status.SWITCHING);
			} else if (this.playerOwned() && player.hasValidMembers()) {
				addTask(Task.TEXT, this.nickname + " went back to you!");
				this.vStatuses.add(Status.SWITCHING);
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.BULK_UP) {
			stat(this, 0, 1, foe, announce);
			stat(this, 1, 1, foe, announce);
		} else if (move == Move.CALM_MIND) {
			stat(this, 2, 1, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (move == Move.CAPTIVATE) {
			stat(foe, 2, -2, this, announce);
		} else if (move == Move.CHARGE) {
			if (announce) addTask(Task.TEXT, this.nickname + " became charged with power!");
			stat(this, 3, 1, foe, announce);
			if (!this.vStatuses.contains(Status.CHARGED)) this.vStatuses.add(Status.CHARGED);
		} else if (move == Move.CHARM) {
			stat(foe, 0, -2, this, announce);
		} else if (move == Move.COIL) {
			stat(this, 0, 1, foe, announce);
			stat(this, 1, 1, foe, announce);
			stat(this, 5, 1, foe, announce);
		} else if (announce && move == Move.CONFUSE_RAY) {
			foe.confuse(true, this);
		} else if (move == Move.COSMIC_POWER) {
			stat(this, 1, 1, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (move == Move.COTTON_GUARD) {
			stat(this, 1, 3, foe, announce);
		} else if (move == Move.CURSE) {
			if (announce && (this.type1 == PType.GHOST || this.type2 == PType.GHOST)) {
				if (!foe.vStatuses.contains(Status.CURSED)) {
					foe.vStatuses.add(Status.CURSED);
					addTask(Task.TEXT, foe.nickname + " was afflicted with a curse!");
					this.damage((this.getStat(0) * 1.0 / 2), foe);
					if (this.currentHP <= 0) {
						this.faint(true, foe);
					}
				} else {
					fail = fail(announce);
				}
			} else {
				stat(this, 0, 1, foe, announce);
				stat(this, 1, 1, foe, announce);
				stat(this, 4, -1, foe, announce);
			}
		} else if (announce && (move == Move.DETECT || move == Move.PROTECT || move == Move.LAVA_LAIR || move == Move.OBSTRUCT || move == Move.SPIKY_SHIELD)) {
			if (Move.getNoComboMoves().contains(lastMoveUsed) && success) {
				fail = fail(announce);
			} else {
				if (announce) addTask(Task.TEXT, this.nickname + " protected itself!");
				this.vStatuses.add(Status.PROTECT);
				this.lastMoveUsed = move;
			}
		} else if (announce && move == Move.DARK_VOID) {
			foe.sleep(true, this);
		} else if (move == Move.DEFENSE_CURL) {
			stat(this, 1, 1, foe, announce);
		} else if (announce && move == Move.DEFOG) {
			stat(foe, 6, -1, this, announce);
			for (FieldEffect fe : field.getHazards(userSide)) {
				if (announce) addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				userSide.remove(fe);
			}
			for (FieldEffect fe : field.getHazards(enemySide)) {
				if (announce) addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				enemySide.remove(fe);
			}
			for (FieldEffect fe : field.getScreens(userSide)) {
				if (announce) addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				userSide.remove(fe);
			}
			for (FieldEffect fe : field.getScreens(enemySide)) {
				if (announce) addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				enemySide.remove(fe);
			}
			if (field.terrain != null) {
				if (announce) {
					Task t = addTask(Task.TERRAIN, "The " + field.terrain.toString() + " terrain disappeared!");
					t.setEffect(null);
				}
				field.terrain = null;
			}
		} else if (announce && move == Move.DESTINY_BOND) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success)) {
				foe.vStatuses.add(Status.BONDED);
				if (announce) addTask(Task.TEXT, this.nickname + " is ready to take its attacker down with it!");
				lastMoveUsed = move;
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
			
//		} else if (move == Move.DISAPPEAR) {
//			stat(this, 6, 2);
		} else if (move == Move.DOUBLE_TEAM) {
			stat(this, 6, 1, foe, announce);
		} else if (move == Move.DRAGON_DANCE) {
			stat(this, 0, 1, foe, announce);
			stat(this, 4, 1, foe, announce);
		} else if (announce && move == Move.ELECTRIC_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (announce && move == Move.ENCORE) {
			if (!foe.vStatuses.contains(Status.ENCORED) && foe.lastMoveUsed != null) {
				foe.vStatuses.add(Status.ENCORED);
				foe.encoreCount = 4;
				if (announce) addTask(Task.TEXT, foe.nickname + " must do an encore!");
				if (foe.item == Item.MENTAL_HERB) {
					addTask(Task.TEXT, foe.nickname + " cleared its encore using its Mental Herb!");
					foe.vStatuses.remove(Status.ENCORED);
					foe.encoreCount = 0;
					foe.consumeItem(this);
				}
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.ENDURE) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success)) {
				if (announce) addTask(Task.TEXT, this.nickname + " braced itself!");
				this.vStatuses.add(Status.ENDURE);
				lastMoveUsed = move;
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.FLASH) {
			stat(foe, 5, -1, this, announce);
			stat(this, 2, 1, foe, announce);
		} else if (announce && move == Move.ENTRAINMENT) {
			foe.ability = this.ability;
			if (announce) addTask(Task.TEXT, foe.nickname + "'s ability became " + foe.ability.toString() + "!");
			foe.swapIn(this, false);
		} else if (move == Move.FAKE_TEARS) {
			stat(foe, 3, -2, this, announce);
		} else if (move == Move.FEATHER_DANCE) {
			stat(foe, 0, -2, this, announce);
		} else if (move == Move.FLATTER) {
			stat(foe, 2, 2, this, announce);
			foe.confuse(false, this);
		} else if (announce && move == Move.FOCUS_ENERGY) {
			if (!this.vStatuses.contains(Status.FOCUS_ENERGY)) {
				this.vStatuses.add(Status.FOCUS_ENERGY);
				if (announce) addTask(Task.TEXT, this.nickname + " is tightening its focus!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.FORESIGHT) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			if (announce) addTask(Task.TEXT, this.nickname + " identified " + foe.nickname + "!");
			stat(this, 5, 1, foe, announce);
		} else if (announce && move == Move.FORESTS_CURSE) {
			foe.type1 = PType.GRASS;
			foe.type2 = null;
			if (announce) addTask(Task.TEXT, foe.nickname + "'s type was changed to Grass!");
		} else if (announce && move == Move.FROSTBIND) {
			foe.freeze(true, this);
		} else if (announce && move == Move.GASTRO_ACID) {
			foe.ability = Ability.NULL;
			if (announce) addTask(Task.TEXT, foe.nickname + "'s ability was supressed!");
		} else if (move == Move.GEOMANCY) {
			stat(this, 2, 2, foe, announce);
			stat(this, 3, 2, foe, announce);
			stat(this, 4, 2, foe, announce);
		} else if (announce && move == Move.GLARE) {
			foe.paralyze(true, this);
		} else if (move == Move.GLITTER_DANCE) {
			stat(this, 2, 1, foe, announce);
			stat(this, 4, 1, foe, announce);
		} else if (announce && move == Move.GRASS_WHISTLE) {
			foe.sleep(true, this);
		} else if (announce && move == Move.GRASSY_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.GRASSY));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (announce && move == Move.GRAVITY) {
			field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		} else if (move == Move.GROWL) {
			stat(foe, 0, -1, this, announce);
		} else if (move == Move.GROWTH) {
			if (field.equals(field.weather, Effect.SUN)) {
				stat(this, 0, 2, foe, announce);
				stat(this, 2, 2, foe, announce);
			} else {
				stat(this, 0, 1, foe, announce);
				stat(this, 2, 1, foe, announce);
			}
		} else if (announce && move == Move.SNOWSCAPE) {
			boolean success = field.setWeather(field.new FieldEffect(Effect.SNOW));
			if (success && item == Item.ICY_ROCK) field.weatherTurns = 8;
		} else if (move == Move.HARDEN) {
			stat(this, 1, 1, foe, announce);
		} else if (announce && move == Move.HAZE) {
			this.statStages = new int[7];
			foe.statStages = new int[7];
			if (announce) addTask(Task.TEXT, "All stat changes were eliminated!");
		} else if (announce && move == Move.HEAL_PULSE) {
			if (foe.currentHP == foe.getStat(0)) {
				if (announce) addTask(Task.TEXT, foe.nickname + "'s HP is full!");
			} else {
				foe.heal(foe.getHPAmount(1.0/2), foe.nickname + " restored HP.");
			}
		} else if (move == Move.HONE_CLAWS) {
			stat(this, 0, 1, foe, announce);
			stat(this, 5, 1, foe, announce);
		} else if (move == Move.HOWL) {
			stat(this, 0, 1, foe, announce);
		} else if (announce && move == Move.HYPNOSIS) {
			foe.sleep(true, this);
		} else if (announce && (move == Move.HEALING_WISH || move == Move.LUNAR_DANCE)) {
			this.faint(true, foe);
			this.vStatuses.add(Status.HEALING);
		} else if (announce && move == Move.INGRAIN) {
			if (!(this.vStatuses.contains(Status.AQUA_RING))) {
			    this.vStatuses.add(Status.AQUA_RING);
			} else {
			    fail = fail(announce);
			}
			this.vStatuses.add(Status.NO_SWITCH);
		} else if (move == Move.IRON_DEFENSE) {
			stat(this, 1, 2, foe, announce);
		} else if (announce && move == Move.LIFE_DEW) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				heal(getHPAmount(1.0/4), this.nickname + " restored HP.");
			}
		} else if (announce && move == Move.LEECH_SEED) {
			if (foe.type1 == PType.GRASS || foe.type2 == PType.GRASS) {
				if (announce) addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				return;
			}
			if (!foe.vStatuses.contains(Status.LEECHED)) {
				foe.vStatuses.add(Status.LEECHED);
				if (announce) addTask(Task.TEXT, foe.nickname + " was seeded!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.LEER) {
			stat(foe, 1, -1, this, announce);
		} else if (announce && move == Move.LIGHT_SCREEN) {
			if (!(field.contains(userSide, Effect.LIGHT_SCREEN))) {
				FieldEffect a = field.new FieldEffect(Effect.LIGHT_SCREEN);
				if (this.item == Item.LIGHT_CLAY) a.turns = 8;
				userSide.add(a);
				if (announce) addTask(Task.TEXT, "Light Screen made " + this.nickname + "'s team stronger against Special moves!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.LOCK$ON) {
			if (announce) addTask(Task.TEXT, this.nickname + " took aim at " + foe.nickname + "!");
			stat(this, 5, 6, foe, announce);
		} else if (announce && move == Move.LOVELY_KISS) {
			foe.sleep(true, this);
		} else if (announce && move == Move.MAGIC_POWDER) {
			foe.type1 = PType.MAGIC;
			foe.type2 = null;
			if (announce) addTask(Task.TEXT, foe.nickname + "'s type changed to MAGIC!");
		} else if (announce && move == Move.MAGNET_RISE) {
			if (this.magCount == 0) {
				this.magCount = 5;
				if (announce) addTask(Task.TEXT, this.nickname + " floated with electromagnetism!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.MEAN_LOOK) {
			if (!foe.vStatuses.contains(Status.TRAPPED)) {
				foe.vStatuses.add(Status.TRAPPED);
				if (announce) addTask(Task.TEXT, foe.nickname + " can no longer escape!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.MEMENTO) {
			stat(foe, 0, -2, this, announce);
			stat(foe, 2, -2, this, announce);
			this.currentHP = 0;
			this.faint(true, foe);
		} else if (move == Move.METAL_SOUND) {
			stat(foe, 3, -2, this, announce);
		} else if (move == Move.MINIMIZE) {
			stat(this, 6, 2, foe, announce);
		} else if (announce && (move == Move.MORNING_SUN || move == Move.MOONLIGHT || move == Move.SYNTHESIS)) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				if (field.equals(field.weather, Effect.SUN)) {
					heal(getHPAmount(1.0/1.5), this.nickname + " restored HP.");
				} else if (field.equals(field.weather, Effect.RAIN) || field.equals(field.weather, Effect.SANDSTORM) || field.equals(field.weather, Effect.SNOW)) {
					heal(getHPAmount(1.0/4), this.nickname + " restored HP.");
				} else {
					heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
				}
			}
		} else if (announce && move == Move.MUD_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.MUD_SPORT));
			if (announce) addTask(Task.TEXT, "Electric's power was weakened!");
		} else if (move == Move.NASTY_PLOT) {
			stat(this, 2, 2, foe, announce);
		} else if (move == Move.NOBLE_ROAR) {
			stat(foe, 0, -1, this, announce);
			stat(foe, 2, -1, this, announce);
		} else if (move == Move.NO_RETREAT) {
			if (!(this.vStatuses.contains(Status.NO_SWITCH))) {
			    this.vStatuses.add(Status.NO_SWITCH);
			    stat(this, 0, 1, foe, announce);
			    stat(this, 1, 1, foe, announce);
			    stat(this, 2, 1, foe, announce);
			    stat(this, 3, 1, foe, announce);
			    stat(this, 4, 1, foe, announce);
			    if (announce) addTask(Task.TEXT, this.nickname + " can no longer switch out!");
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.NIGHTMARE) {
			if (foe.status == Status.ASLEEP && !foe.vStatuses.contains(Status.NIGHTMARE)) {
				foe.vStatuses.add(Status.NIGHTMARE);
				if (announce) addTask(Task.TEXT, foe.nickname + " had a nightmare!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.ODOR_SLEUTH) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			if (announce) addTask(Task.TEXT, this.nickname + " identified " + foe.nickname + "!");
			stat(foe, 6, -1, this, announce);
		} else if (announce && move == Move.PARTING_SHOT) {
			stat(foe, 0, -1, this, announce);
			stat(foe, 2, -1, this, announce);
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				addTask(Task.TEXT, this.nickname + " went back to " + enemy.getName() + "!");
				this.vStatuses.add(Status.SWITCHING);
			} else if (this.playerOwned() && player.hasValidMembers()) {
				addTask(Task.TEXT, this.nickname + " went back to you!");
				this.vStatuses.add(Status.SWITCHING);
			}
		} else if (announce && move == Move.PERISH_SONG) {
			this.perishCount = (this.perishCount == 0) ? 4 : this.perishCount;
			foe.perishCount = (foe.perishCount == 0) ? 4 : foe.perishCount;
		} else if (move == Move.PLAY_NICE) {
			stat(foe, 0, -1, this, announce);
		} else if (move == Move.SPARKLING_WATER) {
			stat(this, 3, 2, foe, announce);
		} else if (move == Move.WATER_FLICK) {
			stat(foe, 0, -2, this, announce);
		} else if (announce && move == Move.PSYCHIC_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.PSYCHIC));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (announce && move == Move.POISON_GAS) {
			foe.poison(true, this);
		} else if (announce && move == Move.STUN_SPORE) {
			if (foe.type1 == PType.GRASS || foe.type2 == PType.GRASS) {
				if (announce) addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				success = false;
				fail = true;
				return;
			}
			foe.paralyze(true, this);
		} else if (move == Move.QUIVER_DANCE) {
			stat(this, 2, 1, foe, announce);
			stat(this, 3, 1, foe, announce);
			stat(this, 4, 1, foe, announce);
		} else if (move == Move.SHELL_SMASH) {
			stat(this, 1, -1, foe, announce);
			stat(this, 3, -1, foe, announce);
			stat(this, 0, 2, foe, announce);
			stat(this, 2, 2, foe, announce);
			stat(this, 4, 2, foe, announce);
		} else if (announce && move == Move.RAIN_DANCE) {
			boolean success = field.setWeather(field.new FieldEffect(Effect.RAIN));
			if (success && item == Item.DAMP_ROCK) field.weatherTurns = 8;
		} else if (move == Move.REBOOT) {
			if (this.status != Status.HEALTHY || !this.vStatuses.isEmpty()) if (announce) addTask(Task.TEXT, this.nickname + " became healthy!");
			this.status = Status.HEALTHY;
			removeBad(this.vStatuses);
			stat(this, 4, 1, foe, announce);
		} else if (move == Move.AURORA_BOOST) {
			stat(this, 1, 1, foe, announce);
			stat(this, 2, 2, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (announce && move == Move.REFLECT) {
			if (!(field.contains(userSide, Effect.REFLECT))) {
				FieldEffect a = field.new FieldEffect(Effect.REFLECT);
				if (this.item == Item.LIGHT_CLAY) a.turns = 8;
				userSide.add(a);
				if (announce) addTask(Task.TEXT, "Reflect made " + this.nickname + "'s team stronger against Physical moves!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.REST) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else if (this.status == Status.ASLEEP) {
				fail = fail(announce);
				return;
			} else {
				int healAmt = this.getStat(0) - this.currentHP;
				this.status = Status.HEALTHY;
				if (this.ability == Ability.INSOMNIA) {
					fail = fail(announce);
					return;
				}
				this.sleep(false, foe);
				this.sleepCounter = 2;
				this.vStatuses.remove(Status.CONFUSED);
				heal(healAmt, this.nickname + " slept and became healthy!");
			}
		} else if (announce && (move == Move.ROOST || move == Move.RECOVER || move == Move.SLACK_OFF)) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
			}
		} else if (move == Move.SAND_ATTACK) {
			stat(foe, 5, -1, this, announce);
		} else if (announce && move == Move.SAFEGUARD) {
			if (!(field.contains(userSide, Effect.SAFEGUARD))) {
				userSide.add(field.new FieldEffect(Effect.SAFEGUARD));
				if (announce) addTask(Task.TEXT, "A veil was added to protect the team from Status!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.SANDSTORM) {
			boolean success = field.setWeather(field.new FieldEffect(Effect.SANDSTORM));
			if (success && item == Item.SMOOTH_ROCK) field.weatherTurns = 8;
		} else if (move == Move.SCARY_FACE) {
			stat(foe, 4, -2, this, announce);
		} else if (move == Move.SCREECH) {
			stat(foe, 1, -2, this, announce);
		} else if (move == Move.SEA_DRAGON) {
			if (id == 150 && announce) {
				int oHP = this.getStat(0);
				id = 237;
				if (nickname == name) nickname = getName();
				
				baseStats = getBaseStats();
				setStats();
				weight = getWeight();
				int nHP = this.getStat(0);
				heal(nHP - oHP, nickname + " transformed into Kissyfishy-D!");
				addTask(Task.SPRITE, "", this);
				setTypes();
				setAbility(abilitySlot);
				if (this.playerOwned()) player.pokedex[237] = 2;
			} else if (id == 237) {
				stat(this, 0, 1, foe, announce);
				stat(this, 2, 1, foe, announce);
				stat(this, 5, 1, foe, announce);
			} else {
				fail = fail(announce);
			}
			

		} else if (announce && move == Move.SLEEP_POWDER) {
			if (foe.type1 == PType.GRASS || foe.type2 == PType.GRASS) {
				if (announce) addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				success = false;
				fail = true;
				return;
			}
			foe.sleep(true, this);
		} else if (move == Move.SHIFT_GEAR) {
			stat(this, 0, 1, foe, announce);
			stat(this, 4, 2, foe, announce);
		} else if (move == Move.SMOKESCREEN) {
			stat(foe, 5, -1, this, announce);
//		} else if (move == Move.STARE) {
//			stat(foe, 0, 1);
//			if (!foe.vStatuses.contains(Status.CONFUSED)) {
//				foe.confuse();
//			}
		} else if (announce && move == Move.SPARKLING_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (announce && move == Move.SPIKES) {
			fail = field.setHazard(enemySide, field.new FieldEffect(Effect.SPIKES));
		} else if (move == Move.SPLASH) {
			if (announce) addTask(Task.TEXT, "But nothing happened!");
		} else if (announce && move == Move.STEALTH_ROCK) {
			fail = field.setHazard(enemySide, field.new FieldEffect(Effect.STEALTH_ROCKS));
		} else if (announce && move == Move.STICKY_WEB) {
			fail = field.setHazard(enemySide, field.new FieldEffect(Effect.STICKY_WEBS));
		} else if (move == Move.STOCKPILE) {
			stat(this, 1, 1, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (announce && move == Move.STRENGTH_SAP) {
			int amount = (int) (foe.getStat(1) * foe.asModifier(0));
			if (this.item == Item.BIG_ROOT) amount *= 1.3;
			heal(amount, this.nickname + " restored HP.");
			stat(foe, 0, -1, this, announce);
		}  else if (move == Move.STRING_SHOT) {
			stat(foe, 4, -2, this, announce);
		} else if (announce && move == Move.SUNNY_DAY) {
			boolean success = field.setWeather(field.new FieldEffect(Effect.SUN));
			if (success && item == Item.HEAT_ROCK) field.weatherTurns = 8;
		} else if (announce && move == Move.SUPERSONIC) {
			foe.confuse(true, this);
		} else if (move == Move.SWAGGER) {
			stat(foe, 0, 2, this, announce);
			if (announce) foe.confuse(false, this);
		} else if (announce && move == Move.SWEET_KISS) {
			foe.confuse(true, this);
		} else if (move == Move.SWEET_SCENT) {
			stat(foe, 6, -2, this, announce);
		} else if (move == Move.SWORDS_DANCE) {
			stat(this, 0, 2, foe, announce);
		} else if (announce && move == Move.TAUNT) {
			if (!(foe.vStatuses.contains(Status.TAUNTED))) {
			    foe.vStatuses.add(Status.TAUNTED);
			    foe.tauntCount = 4;
			    if (announce) addTask(Task.TEXT, foe.nickname + " was taunted!");
			    if (foe.item == Item.MENTAL_HERB) {
					addTask(Task.TEXT, foe.nickname + " cured its taunt using its Mental Herb!");
					foe.vStatuses.remove(Status.TAUNTED);
					foe.tauntCount = 4;
					foe.consumeItem(this);
				}
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.TORMENT) {
			if (!(foe.vStatuses.contains(Status.TORMENTED))) {
			    foe.vStatuses.add(Status.TORMENTED);
			    foe.tormentCount = 4;
			    if (announce) addTask(Task.TEXT, foe.nickname + " was tormented!");
			    if (foe.item == Item.MENTAL_HERB) {
					addTask(Task.TEXT, foe.nickname + " cured its torment using its Mental Herb!");
					foe.vStatuses.remove(Status.TORMENTED);
					foe.tormentCount = 0;
					foe.consumeItem(this);
				}
			} else {
			    fail = fail(announce);
			}
		} else if (move == Move.TAIL_GLOW) {
			stat(this, 2, 3, foe, announce);
		} else if (announce && move == Move.TAILWIND) {
			if (!(field.contains(userSide, Effect.TAILWIND))) {
				userSide.add(field.new FieldEffect(Effect.TAILWIND));
				if (announce) addTask(Task.TEXT, "A strong wind blew behind the team!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.TAIL_WHIP) {
			stat(foe, 1, -1, this, announce);
		} else if (announce && move == Move.TEETER_DANCE) {
			foe.confuse(true, this);
		} else if (announce && move == Move.THUNDER_WAVE) {
			foe.paralyze(true, this);
		} else if (move == Move.TOPSY$TURVY) {
			for (int i = 0; i < 7; i++) {
				foe.statStages[i] *= -1;
			}
			if (announce) addTask(Task.TEXT, foe.nickname + "'s stat changes were flipped!");
		} else if (announce && move == Move.TOXIC) {
			foe.toxic(true, this);
		} else if (announce && move == Move.TOXIC_SPIKES) {
			fail = field.setHazard(enemySide, field.new FieldEffect(Effect.TOXIC_SPIKES));
		} else if (move == Move.TRICK || move == Move.SWITCHEROO) {
			if (announce) addTask(Task.TEXT, this.nickname + " switched items with its target!");
			Item userItem = this.item;
			Item foeItem = foe.item;
			if (userItem != null) if (announce) addTask(Task.TEXT, foe.nickname + " obtained a " + userItem.toString() + "!");
			if (foeItem != null) if (announce) addTask(Task.TEXT, this.nickname + " obtained a " + foeItem.toString() + "!");
			this.item = foeItem;
			foe.item = userItem;
			if (this.lostItem == null) {
				this.lostItem = userItem;
				if (this.lostItem == null) {
					this.lostItem = Item.POTION;
				}
			}
			if (foe.lostItem == null) {
				foe.lostItem = foeItem;
				if (foe.lostItem == null) {
					foe.lostItem = Item.POTION;
				}
			}
		} else if (announce && move == Move.TRICK_ROOM) {
			field.setEffect(field.new FieldEffect(Effect.TRICK_ROOM));
		} else if (move == Move.VENOM_DRENCH) {
			if (foe.status == Status.POISONED || foe.status == Status.TOXIC) {
				stat(foe, 0, -2, this, announce);
				stat(foe, 2, -2, this, announce);
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.WATER_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.WATER_SPORT));
			if (announce) addTask(Task.TEXT, "Fire's power was weakened!");
		} else if (announce && (move == Move.WHIRLWIND || move == Move.ROAR)) {
			boolean result = false;
			if (foe.trainerOwned() && enemy != null) {
				result = enemy.swapRandom(foe, player);
			} else if (foe.playerOwned()) {
				result = player.swapRandom(foe);
			}
			if (!result) fail = fail(announce);
		} else if (announce && move == Move.WILL$O$WISP) {
			foe.burn(true, this);
		} else if (announce && move == Move.WISH) {
			if (this.vStatuses.contains(Status.WISH)) {
				fail(announce);
			} else {
				this.vStatuses.add(Status.WISH);
				if (announce) addTask(Task.TEXT, this.nickname + " made a wish!");
			}
		} else if (move == Move.WITHDRAW) {
			stat(this, 1, 1, foe, announce);
		} else if (announce && move == Move.WORRY_SEED) {
			foe.ability = Ability.INSOMNIA;
			if (announce) addTask(Task.TEXT, foe.nickname + "'s ability became Insomnia!");
		} else if (move == Move.ROCK_POLISH) {
			stat(this, 4, 2, foe, announce);
		}
		success = !fail;
		return;
	}
	
	public void verifyHP() {
		if (currentHP > this.getStat(0)) currentHP = this.getStat(0);
	}
	
	/**
	 * 
	 * @param p - Pokemon to activate the stat change on
	 * @param i - type of Stat to change (0 = Atk, 1 = Def, 2 = Sp.A, 3 = Sp.D, 4 = Spe, 5 = Acc, 6 = Eva)
	 * @param amt - amount to modify the Stat by
	 * @param foe - Pokemon causing the stat change (only matters for boosts)
	 * 
	 * @throws IllegalArgumentException if amt is 0 (invalid stat change)
	 */
	public void stat(Pokemon p, int i, int amt, Pokemon foe) throws IllegalArgumentException {
		stat(p, i, amt, foe, true);
	}

	/**
	 * 
	 * @param p - Pokemon to activate the stat change on
	 * @param i - type of Stat to change (0 = Atk, 1 = Def, 2 = Sp.A, 3 = Sp.D, 4 = Spe, 5 = Acc, 6 = Eva)
	 * @param amt - amount to modify the Stat by
	 * @param foe - Pokemon causing the stat change (only matters for boosts)
	 * @param announce - Whether to write in console
	 * 
	 * @throws IllegalArgumentException if amt is 0 (invalid stat change)
	 */
	private void stat(Pokemon p, int i, int amt, Pokemon foe, boolean announce) throws IllegalArgumentException {
		if (amt == 0) throw new IllegalArgumentException("Stat change amount cannot be 0");
		if (p.isFainted()) return;
		int a = amt;
		if (p.ability == Ability.SIMPLE) a *= 2;
		if (p.ability == Ability.CONTRARY) a *= -1;
		if (foe.ability == Ability.BRAINWASH) a *= -1;
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
		if (a == 12) amount = " was raised to the max";
		
		if (this != p) {
			if (p.ability == Ability.MIRROR_ARMOR && a < 0) {
				if (announce) addAbilityTask(p);
				stat(this, i, amt, foe);
				return;
			} else if (p.ability == Ability.DEFIANT && foe.ability != Ability.BRAINWASH && a < 0) {
				if (announce) addAbilityTask(p);
				stat(p, 0, 2, foe);
			} else if (p.ability == Ability.COMPETITIVE && foe.ability != Ability.BRAINWASH && a < 0) {
				if (announce) addAbilityTask(p);
				stat(p, 2, 2, foe);
			} else if (p.ability == Ability.CLEAR_BODY && a < 0) {
				if (announce) addAbilityTask(p);
				if (announce) addTask(Task.TEXT, p.nickname + "'s " + type + " was not lowered!");
				return;
			} else if (item == Item.CLEAR_AMULET && a < 0) {
				if (announce) addTask(Task.TEXT, p.nickname + "'s Clear Amulet blocked the stat drop!");
				return;
			} else if (p.ability == Ability.KEEN_EYE && a < 0 && i == 5) {
				if (announce) addAbilityTask(p);
				if (announce) addTask(Task.TEXT, p.nickname + "'s " + type + " was not lowered!");
				return;
			} else if (p.ability == Ability.HYPER_CUTTER && a < 0 && i == 0) {
				if (announce) addAbilityTask(p);
				if (announce) addTask(Task.TEXT, nickname + "'s " + type + " was not lowered!");
				return;
			}
		}
		if (foe.ability == Ability.EMPATHIC_LINK && a > 0) {
			if (announce) addAbilityTask(foe);
			foe.stat(foe, 2, 1, this);
		}
		if (p.statStages[i] >= 6 && a > 0) {
			if (announce) addTask(Task.TEXT, p.nickname + "'s " + type + " won't go any higher!");
		} else if (p.statStages[i] <= -6 && a < 0) {
			if (a != 12 && announce) addTask(Task.TEXT, p.nickname + "'s " + type + " won't go any lower!");
		} else {
			if (announce) addTask(Task.TEXT, p.nickname + "'s " + type + amount + "!");
			if (p.item == Item.WHITE_HERB && p.statStages[i] > -6 && a < 0) {
				if (announce) addTask(Task.TEXT, p.nickname + " returned its stats to normal using its White Herb!");
			}
		}
		p.statStages[i] += a;
		p.statStages[i] = p.statStages[i] < -6 ? -6 : p.statStages[i];
		p.statStages[i] = p.statStages[i] > 6 ? 6 : p.statStages[i];
		if (p.item == Item.WHITE_HERB && p.statStages[i] > -6 && a < 0) {
			p.statStages[i] -= a;
			p.consumeItem(foe);
		}
	}

	public double asModifier(int index) {
		double numerator = 2.0;
        double denominator = 2.0;

        int stage = this.statStages[index];
        if (stage < 0) {
            denominator -= stage;
        } else if (stage > 0) {
            numerator += stage;
        }
        
        return numerator / denominator;
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

	public boolean getImmune(Pokemon p, PType type) {
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
		for (Moveslot m : moveset) {
			if (m != null) m.currentPP = m.maxPP;
		}
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
	            resistantTypes.add(PType.FIGHTING);
	            resistantTypes.add(PType.GHOST);
	            resistantTypes.add(PType.STEEL);
	            resistantTypes.add(PType.GALACTIC);
	            break;
			case DARK:
				resistantTypes.add(PType.FIGHTING);
	            resistantTypes.add(PType.DARK);
	            resistantTypes.add(PType.LIGHT);
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
	            resistantTypes.add(PType.BUG);
	            resistantTypes.add(PType.MAGIC);
	            resistantTypes.add(PType.PSYCHIC);
	            break;
			case FIRE:
				resistantTypes.add(PType.FIRE);
	            resistantTypes.add(PType.WATER);
	            resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.DRAGON);
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
	            resistantTypes.add(PType.GALACTIC);
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
	
	public void setMoveBank() {
		if (id >= 197) {
			setMoveBank2();
			return;
		}
		switch(this.id) {
		case 1:
			movebank = new Node[18];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.RAZOR_LEAF);
			movebank[10] = new Node(Move.SAND_ATTACK);
			movebank[14] = new Node(Move.SMACK_DOWN);
			movebank[17] = new Node(Move.HEADBUTT);
			break;
		case 2:
			movebank = new Node[31];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.RAZOR_LEAF);
			movebank[10] = new Node(Move.SAND_ATTACK);
			movebank[14] = new Node(Move.SMACK_DOWN);
			movebank[17] = new Node(Move.HEADBUTT);
			movebank[20] = new Node(Move.SLEEP_POWDER);
			movebank[24] = new Node(Move.MEGA_DRAIN);
			movebank[27] = new Node(Move.MAGNITUDE);
			movebank[30] = new Node(Move.ROCK_TOMB);
			break;
		case 3:
		    movebank = new Node[80];
		    movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.WITHDRAW);
			movebank[6] = new Node(Move.RAZOR_LEAF);
			movebank[10] = new Node(Move.SAND_ATTACK);
			movebank[14] = new Node(Move.SMACK_DOWN);
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
			movebank[64] = new Node(Move.GIGA_DRAIN);
			movebank[69] = new Node(Move.HEAD_SMASH);
			movebank[79] = new Node(Move.SPIKES);
		    break;
		case 4:
		    movebank = new Node[14];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.HARDEN);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.INCINERATE);
		    break;
		case 5:
		    movebank = new Node[36];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.HARDEN);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.INCINERATE);
		    movebank[15] = new Node(Move.HEADBUTT);
		    movebank[17] = new Node(Move.FLAME_WHEEL);
		    movebank[20] = new Node(Move.IRON_DEFENSE);
		    movebank[23] = new Node(Move.TAKE_DOWN);
		    movebank[26] = new Node(Move.BULLDOZE);
		    movebank[29] = new Node(Move.LAVA_PLUME);
		    movebank[32] = new Node(Move.PLAY_ROUGH);
		    movebank[35] = new Node(Move.IRON_HEAD);
		    break;
		case 6:
		    movebank = new Node[80];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[0].next = new Node(Move.HARDEN);
		    movebank[6] = new Node(Move.EMBER);
		    movebank[10] = new Node(Move.SMOKESCREEN);
		    movebank[13] = new Node(Move.INCINERATE);
		    movebank[15] = new Node(Move.HEADBUTT);
		    movebank[17] = new Node(Move.FLAME_WHEEL);
		    movebank[20] = new Node(Move.IRON_DEFENSE);
		    movebank[23] = new Node(Move.TAKE_DOWN);
		    movebank[26] = new Node(Move.BULLDOZE);
		    movebank[29] = new Node(Move.LAVA_PLUME);
		    movebank[32] = new Node(Move.PLAY_ROUGH);
		    movebank[35] = new Node(Move.IRON_HEAD);
		    movebank[35].next = new Node(Move.MOLTEN_STEELSPIKE);
		    movebank[38] = new Node(Move.FIRE_BLAST);
		    movebank[42] = new Node(Move.HEAVY_SLAM);
		    movebank[42].next = new Node(Move.HEAT_CRASH);
		    movebank[45] = new Node(Move.EARTH_POWER);
		    movebank[50] = new Node(Move.AURA_SPHERE);
		    movebank[54] = new Node(Move.STEEL_BEAM);
		    movebank[59] = new Node(Move.FLARE_BLITZ);
		    movebank[69] = new Node(Move.ERUPTION);
		    movebank[74] = new Node(Move.STEALTH_ROCK);
		    movebank[79] = new Node(Move.SPIKES);
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
		    movebank[16] = new Node(Move.BITE);
		    movebank[18] = new Node(Move.MEAN_LOOK);
		    movebank[22] = new Node(Move.FIRE_FANG);
		    movebank[25] = new Node(Move.AQUA_JET);
		    movebank[31] = new Node(Move.DRAGON_TAIL);
		    break;
		case 9:
			movebank = new Node[85];
		    movebank[0] = new Node(Move.SCRATCH);
		    movebank[0].next = new Node(Move.GROWL);
		    movebank[6] = new Node(Move.WATER_GUN);
		    movebank[10] = new Node(Move.BUBBLEBEAM);
		    movebank[14] = new Node(Move.MUD_SPORT);
		    movebank[15] = new Node(Move.BITE);
		    movebank[18] = new Node(Move.MEAN_LOOK);
		    movebank[22] = new Node(Move.FIRE_FANG);
		    movebank[25] = new Node(Move.AQUA_JET);
		    movebank[31] = new Node(Move.DRAGON_TAIL);
		    movebank[35] = new Node(Move.CHANNELING_BLOW);
		    movebank[37] = new Node(Move.LIQUIDATION);
		    movebank[40] = new Node(Move.HAMMER_ARM);
		    movebank[45] = new Node(Move.DRAGON_RUSH);
		    movebank[49] = new Node(Move.BULK_UP);
		    movebank[53] = new Node(Move.CLOSE_COMBAT);
		    movebank[58] = new Node(Move.DOUBLE$EDGE);
		    movebank[65] = new Node(Move.WAVE_CRASH);
		    movebank[71] = new Node(Move.OUTRAGE);
		    movebank[79] = new Node(Move.SUMMIT_STRIKE);
		    movebank[84] = new Node(Move.SPIKES);
		    break;
		case 10:
		    movebank = new Node[17];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[15] = new Node(Move.WHIRLWIND);
		    break;
		case 11:
		    movebank = new Node[33];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[15] = new Node(Move.WHIRLWIND);
		    movebank[17] = new Node(Move.TWISTER);
		    movebank[19] = new Node(Move.AIR_CUTTER);
		    movebank[22] = new Node(Move.DEFOG);
		    movebank[24] = new Node(Move.LIGHT_BEAM);
		    movebank[26] = new Node(Move.MIRROR_MOVE);
		    movebank[29] = new Node(Move.GLITTER_DANCE);
		    movebank[32] = new Node(Move.AIR_SLASH);
		    break;
		case 12:
		    movebank = new Node[70];
		    movebank[0] = new Node(Move.FLASH);
		    movebank[4] = new Node(Move.LEER);
		    movebank[7] = new Node(Move.FLASH_RAY);
		    movebank[9] = new Node(Move.GUST);
		    movebank[12] = new Node(Move.QUICK_ATTACK);
		    movebank[15] = new Node(Move.WHIRLWIND);
		    movebank[17] = new Node(Move.TWISTER);
		    movebank[19] = new Node(Move.AIR_CUTTER);
		    movebank[22] = new Node(Move.DEFOG);
		    movebank[24] = new Node(Move.LIGHT_BEAM);
		    movebank[26] = new Node(Move.MIRROR_MOVE);
		    movebank[29] = new Node(Move.GLITTER_DANCE);
		    movebank[32] = new Node(Move.AIR_SLASH);
		    movebank[35] = new Node(Move.DAZZLING_GLEAM);
		    movebank[40] = new Node(Move.ROOST);
			movebank[42] = new Node(Move.PRISMATIC_LASER);
		    movebank[45] = new Node(Move.HURRICANE);
		    movebank[48] = new Node(Move.SUNNY_DAY);
		    movebank[51] = new Node(Move.MORNING_SUN);
		    movebank[54] = new Node(Move.SUNNY_DOOM);
		    movebank[59] = new Node(Move.AEROBLAST);
		    movebank[62] = new Node(Move.SOLAR_BEAM);
		    movebank[69] = new Node(Move.PHOTON_GEYSER);
		    break;
		case 13:
		    movebank = new Node[15];
		    movebank[0] = new Node(Move.TACKLE);
		    movebank[4] = new Node(Move.SAND_ATTACK);
		    movebank[8] = new Node(Move.PECK);
		    movebank[12] = new Node(Move.LEER);
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
		    movebank[22] = new Node(Move.AGILITY);
		    movebank[24] = new Node(Move.PLUCK);
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
		    movebank[22] = new Node(Move.AGILITY);
		    movebank[24] = new Node(Move.PLUCK);
		    movebank[26] = new Node(Move.SPIKES);
		    movebank[29] = new Node(Move.METAL_CLAW);
		    movebank[31] = new Node(Move.METAL_SOUND);
		    movebank[34] = new Node(Move.STEEL_WING);
		    movebank[37] = new Node(Move.DRILL_PECK);
			movebank[40] = new Node(Move.DEFOG);
		    movebank[43] = new Node(Move.ROOST);
		    movebank[46] = new Node(Move.IRON_DEFENSE);
		    movebank[49] = new Node(Move.FLY);
		    movebank[52] = new Node(Move.NIGHT_SLASH);
		    movebank[56] = new Node(Move.GYRO_BALL);
		    movebank[62] = new Node(Move.IRON_HEAD);
		    movebank[69] = new Node(Move.BRAVE_BIRD);
		    break;
		case 16:
		    movebank = new Node[32];
		    movebank[0] = new Node(Move.POUND);
		    movebank[2] = new Node(Move.GROWL);
		    movebank[4] = new Node(Move.LEER);
		    movebank[8] = new Node(Move.DOUBLE_SLAP);
		    movebank[12] = new Node(Move.HEADBUTT);
		    movebank[16] = new Node(Move.MACH_PUNCH);
		    movebank[19] = new Node(Move.BITE);
		    movebank[22] = new Node(Move.COVET);
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
		    movebank[12] = new Node(Move.HEADBUTT);
		    movebank[16] = new Node(Move.MACH_PUNCH);
		    movebank[19] = new Node(Move.BITE);
		    movebank[22] = new Node(Move.COVET);
		    movebank[26] = new Node(Move.STOCKPILE);
		    movebank[29] = new Node(Move.ICE_BALL);
		    movebank[31] = new Node(Move.ROLLOUT);
		    movebank[33] = new Node(Move.CRUNCH);
		    movebank[36] = new Node(Move.TAKE_DOWN);
		    movebank[42] = new Node(Move.ZEN_HEADBUTT);
		    movebank[49] = new Node(Move.SUPER_FANG);
		    movebank[52] = new Node(Move.BULLDOZE);
		    movebank[56] = new Node(Move.METAL_CLAW);
		    movebank[59] = new Node(Move.DOUBLE$EDGE);
		    movebank[64] = new Node(Move.ENDEAVOR);
		    movebank[69] = new Node(Move.EARTHQUAKE);
		    break;
		case 18:
		    movebank = new Node[80];
		    movebank[0] = new Node(Move.POUND);
		    movebank[2] = new Node(Move.GROWL);
		    movebank[4] = new Node(Move.LEER);
		    movebank[8] = new Node(Move.DOUBLE_SLAP);
		    movebank[12] = new Node(Move.HEADBUTT);
		    movebank[16] = new Node(Move.MACH_PUNCH);
		    movebank[19] = new Node(Move.BITE);
		    movebank[22] = new Node(Move.COVET);
		    movebank[26] = new Node(Move.STOCKPILE);
		    movebank[29] = new Node(Move.ICE_BALL);
		    movebank[31] = new Node(Move.ROLLOUT);
		    movebank[33] = new Node(Move.CRUNCH);
		    movebank[35] = new Node(Move.ROCK_BLAST);
		    movebank[39] = new Node(Move.STEALTH_ROCK);
		    movebank[42] = new Node(Move.ZEN_HEADBUTT);
		    movebank[44] = new Node(Move.ROCK_SLIDE);
		    movebank[49] = new Node(Move.SUPER_FANG);
		    movebank[52] = new Node(Move.HEAD_SMASH);
		    movebank[54] = new Node(Move.DOUBLE$EDGE);
		    movebank[56] = new Node(Move.SWORDS_DANCE);
		    movebank[59] = new Node(Move.STONE_EDGE);
		    movebank[61] = new Node(Move.EARTHQUAKE);
		    movebank[66] = new Node(Move.SUPERPOWER);
		    movebank[70] = new Node(Move.AMNESIA);
		    movebank[74] = new Node(Move.VOLT_TACKLE);
		    movebank[79] = new Node(Move.ENDEAVOR);
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
		    movebank[19] = new Node(Move.BABY$DOLL_EYES);
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
		    movebank[19] = new Node(Move.BABY$DOLL_EYES);
		    movebank[19].next = new Node(Move.COVET);
		    movebank[19].next.next = new Node(Move.MAGIC_BLAST);
		    movebank[22] = new Node(Move.ENCORE);
		    movebank[24] = new Node(Move.HYPNOSIS);
		    movebank[24].next = new Node(Move.SWIFT);
		    movebank[27] = new Node(Move.KNOCK_OFF);
		    movebank[30] = new Node(Move.SPARKLE_STRIKE);
		    movebank[33] = new Node(Move.FIRE_PUNCH);
		    movebank[33].next = new Node(Move.THUNDER_PUNCH);
		    movebank[33].next.next = new Node(Move.ICE_PUNCH);
		    movebank[35] = new Node(Move.HOCUS_POCUS);
		    movebank[37] = new Node(Move.TAKE_DOWN);
		    movebank[39] = new Node(Move.PSYCHIC);
		    movebank[39].next = new Node(Move.TRICK);
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
		    movebank[19] = new Node(Move.BABY$DOLL_EYES);
		    movebank[19].next = new Node(Move.COVET);
		    movebank[19].next.next = new Node(Move.MAGIC_BLAST);
		    movebank[22] = new Node(Move.ENCORE);
		    movebank[24] = new Node(Move.HYPNOSIS);
		    movebank[24].next = new Node(Move.SWIFT);
		    movebank[27] = new Node(Move.KNOCK_OFF);
		    movebank[30] = new Node(Move.SPARKLE_STRIKE);
		    movebank[33] = new Node(Move.FIRE_PUNCH);
		    movebank[33].next = new Node(Move.THUNDER_PUNCH);
		    movebank[33].next.next = new Node(Move.ICE_PUNCH);
		    movebank[35] = new Node(Move.HOCUS_POCUS);
		    movebank[37] = new Node(Move.TAKE_DOWN);
		    movebank[39] = new Node(Move.PSYCHIC);
		    movebank[39].next = new Node(Move.TRICK);
		    movebank[39].next.next = new Node(Move.DESOLATE_VOID);
		    movebank[42] = new Node(Move.BLIZZARD);
		    movebank[45] = new Node(Move.GALAXY_BLAST);
		    movebank[48] = new Node(Move.COSMIC_POWER);
		    movebank[51] = new Node(Move.TRI$ATTACK);
		    movebank[54] = new Node(Move.MEMENTO);
		    movebank[57] = new Node(Move.ABDUCT);
		    movebank[60] = new Node(Move.RADIO_BURST);
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
		    movebank[17].next = new Node(Move.POISON_FANG);
		    movebank[19] = new Node(Move.COIL);
		    movebank[21] = new Node(Move.THIEF);
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
		    movebank[17].next = new Node(Move.POISON_FANG);
		    movebank[19] = new Node(Move.COIL);
		    movebank[21] = new Node(Move.THIEF);
		    movebank[24] = new Node(Move.PIN_MISSILE);
		    movebank[29] = new Node(Move.AQUA_TAIL);
		    movebank[31] = new Node(Move.IRON_TAIL);
		    movebank[33] = new Node(Move.TOXIC_SPIKES);
		    movebank[36] = new Node(Move.POISON_JAB);
		    movebank[39] = new Node(Move.ENDEAVOR);
		    movebank[44] = new Node(Move.FELL_STINGER);
		    movebank[49] = new Node(Move.QUIVER_DANCE);
		    movebank[54] = new Node(Move.STICKY_WEB);
		    movebank[59] = new Node(Move.FATAL_BIND);
			break;
		case 25:
			movebank = new Node[65];
			movebank[0] = new Node(Move.BUG_BITE);
		    movebank[9] = new Node(Move.PROTECT);
		    movebank[14] = new Node(Move.HIDDEN_POWER);
		    movebank[17] = new Node(Move.FOCUS_ENERGY);
		    movebank[17].next = new Node(Move.POISON_FANG);
		    movebank[19] = new Node(Move.COIL);
		    movebank[21] = new Node(Move.THIEF);
		    movebank[24] = new Node(Move.PIN_MISSILE);
		    movebank[29] = new Node(Move.AQUA_TAIL);
		    movebank[31] = new Node(Move.IRON_TAIL);
		    movebank[31].next = new Node(Move.ROCK_POLISH);
		    movebank[33] = new Node(Move.STEALTH_ROCK);
		    movebank[35] = new Node(Move.ROCK_SLIDE);
		    movebank[39] = new Node(Move.X$SCISSOR);
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
			movebank[9] = new Node(Move.ROCK_THROW);
			movebank[12] = new Node(Move.LEAF_TORNADO);
			movebank[15] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.ROOT_KICK);
			movebank[20] = new Node(Move.INGRAIN);
			movebank[23] = new Node(Move.ROCK_TOMB);
			break;
		case 27:
			movebank = new Node[70];
			movebank[0] = new Node(Move.POUND);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.ABSORB);
			movebank[7] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.ROCK_THROW);
			movebank[12] = new Node(Move.LEAF_TORNADO);
			movebank[15] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.ROOT_KICK);
			movebank[20] = new Node(Move.INGRAIN);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.MACH_PUNCH);
			movebank[30] = new Node(Move.SUBMISSION);
			movebank[34] = new Node(Move.STEALTH_ROCK);
			movebank[38] = new Node(Move.HAMMER_ARM);
			movebank[41] = new Node(Move.KNOCK_OFF);
			movebank[44] = new Node(Move.EARTHQUAKE);
			movebank[46] = new Node(Move.POWER_WHIP);
			movebank[49] = new Node(Move.THUNDER_PUNCH);
			movebank[49].next = new Node(Move.ICE_PUNCH);
			movebank[52] = new Node(Move.SUPERPOWER);
			movebank[58] = new Node(Move.GRASSY_TERRAIN);
			movebank[62] = new Node(Move.WOOD_HAMMER);
			movebank[69] = new Node(Move.OUTRAGE);
			break;
		case 28:
			movebank = new Node[80];
			movebank[0] = new Node(Move.STEALTH_ROCK);
			movebank[0].addToEnd(new Node(Move.SPIKY_SHIELD));
			movebank[0].addToEnd(new Node(Move.DRAGON_TAIL));
			movebank[0].addToEnd(new Node(Move.GRASSY_TERRAIN));
			movebank[0].addToEnd(new Node(Move.FORESTS_CURSE));
			movebank[0].addToEnd(new Node(Move.GIGA_DRAIN));
			movebank[0].addToEnd(new Node(Move.GROWTH));
			movebank[0].addToEnd(new Node(Move.BREAKING_SWIPE));
			movebank[0].addToEnd(new Node(Move.LEAF_TORNADO));
			movebank[0].addToEnd(new Node(Move.MAGICAL_LEAF));
			movebank[0].addToEnd(new Node(Move.INGRAIN));
			movebank[0].addToEnd(new Node(Move.ROCK_TOMB));
			movebank[0].addToEnd(new Node(Move.DRAGON_PULSE));
			movebank[0].addToEnd(new Node(Move.DRAGON_RUSH));
			movebank[0].addToEnd(new Node(Move.LEAF_STORM));
			movebank[0].addToEnd(new Node(Move.FLAMETHROWER));
			movebank[0].addToEnd(new Node(Move.SYNTHESIS));
			movebank[0].addToEnd(new Node(Move.DRACO_METEOR));
			movebank[49] = new Node(Move.PETAL_DANCE);
			movebank[59] = new Node(Move.NASTY_PLOT);
			movebank[69] = new Node(Move.THUNDER);
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
			movebank[0].addToEnd(new Node(Move.GROWTH));
			movebank[0].addToEnd(new Node(Move.WATER_SPORT));
			movebank[0].addToEnd(new Node(Move.POISON_STING));
			movebank[4] = new Node(Move.EXTRASENSORY);
			movebank[9] = new Node(Move.SLEEP_POWDER);
			movebank[15] = new Node(Move.LEECH_SEED);
			movebank[18] = new Node(Move.MAGICAL_LEAF);
			movebank[20] = new Node(Move.SLUDGE);
			movebank[22] = new Node(Move.GRASS_WHISTLE);
			movebank[24] = new Node(Move.GIGA_DRAIN);
			movebank[27] = new Node(Move.TOXIC_SPIKES);
			movebank[27].next = new Node(Move.SPIKES);
			movebank[30] = new Node(Move.SWEET_SCENT);
			movebank[33] = new Node(Move.INGRAIN);
			movebank[36] = new Node(Move.SLUDGE_BOMB);
			movebank[39] = new Node(Move.TOXIC);
			movebank[42] = new Node(Move.AROMATHERAPY);
			movebank[45] = new Node(Move.SYNTHESIS);
			movebank[49] = new Node(Move.PETAL_DANCE);
			break;
		case 31:
			movebank = new Node[1];
			movebank[0] = new Node(Move.LEAF_STORM);
			movebank[0].addToEnd(new Node(Move.VENOM_DRENCH));
			movebank[0].addToEnd(new Node(Move.MEGA_DRAIN));
			movebank[0].addToEnd(new Node(Move.POISON_STING));
			movebank[0].addToEnd(new Node(Move.SWEET_SCENT));
			movebank[0].addToEnd(new Node(Move.GRASSY_TERRAIN));
			movebank[0].addToEnd(new Node(Move.SLUDGE_BOMB));
			movebank[0].addToEnd(new Node(Move.MAGICAL_LEAF));
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
			movebank = new Node[60];
			movebank[0] = new Node(Move.SLASH);
			movebank[0].addToEnd(new Node(Move.FALSE_SWIPE));
			movebank[0].addToEnd(new Node(Move.RAZOR_LEAF));
			movebank[0].addToEnd(new Node(Move.PIN_MISSILE));
			movebank[21] = new Node(Move.STRUGGLE_BUG);
			movebank[28] = new Node(Move.FELL_STINGER);
			movebank[31] = new Node(Move.CROSS_POISON);
			movebank[35] = new Node(Move.LEAF_BLADE);
			movebank[38] = new Node(Move.X$SCISSOR);
			movebank[41] = new Node(Move.ENTRAINMENT);
			movebank[44] = new Node(Move.KNOCK_OFF);
			movebank[46] = new Node(Move.SWORDS_DANCE);
			movebank[49] = new Node(Move.PSYCHO_CUT);
			movebank[54] = new Node(Move.STICKY_WEB);
			movebank[59] = new Node(Move.SOLAR_BLADE);
			break;
		case 35:
			movebank = new Node[25];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD$SLAP);
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[12] = new Node(Move.BITE);
			movebank[15] = new Node(Move.THUNDERSHOCK);
			movebank[19] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.STICKY_WEB);
			break;
		case 36:
			movebank = new Node[64];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD$SLAP);
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[12] = new Node(Move.BITE);
			movebank[15] = new Node(Move.THUNDERSHOCK);
			movebank[19] = new Node(Move.SPARK);
			movebank[22] = new Node(Move.INFESTATION);
			movebank[25] = new Node(Move.PARABOLIC_CHARGE);
			movebank[28] = new Node(Move.STICKY_WEB);
			movebank[31] = new Node(Move.ELECTROWEB);
			movebank[35] = new Node(Move.X$SCISSOR);
			movebank[42] = new Node(Move.CRUNCH);
			movebank[49] = new Node(Move.DIG);
			movebank[56] = new Node(Move.IRON_DEFENSE);
			movebank[63] = new Node(Move.DISCHARGE);
			break;
		case 37:
			movebank = new Node[64];
			movebank[0] = new Node(Move.VISE_GRIP);
			movebank[0].next = new Node(Move.MUD$SLAP);
			movebank[4] = new Node(Move.STRING_SHOT);
			movebank[9] = new Node(Move.BUG_BITE);
			movebank[12] = new Node(Move.BITE);
			movebank[15] = new Node(Move.THUNDERSHOCK);
			movebank[19] = new Node(Move.SPARK);
			movebank[22] = new Node(Move.INFESTATION);
			movebank[25] = new Node(Move.PARABOLIC_CHARGE);
			movebank[28] = new Node(Move.STICKY_WEB);
			movebank[31] = new Node(Move.ELECTROWEB);
			movebank[35] = new Node(Move.SILVER_WIND);
			movebank[38] = new Node(Move.DISCHARGE);
			movebank[40] = new Node(Move.GUILLOTINE);
			movebank[42] = new Node(Move.BUG_BUZZ);
			movebank[46] = new Node(Move.THUNDERBOLT);
			movebank[49] = new Node(Move.FLY);
			movebank[56] = new Node(Move.AGILITY);
			movebank[63] = new Node(Move.ZAP_CANNON);
			break;
		case 38:
			movebank = new Node[47];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.LEAFAGE);
			movebank[8] = new Node(Move.ENDURE);
			movebank[11] = new Node(Move.ODOR_SLEUTH);
			movebank[14] = new Node(Move.PROTECT);
			movebank[18] = new Node(Move.COVET);
			movebank[21] = new Node(Move.LEAF_TORNADO);
			movebank[23] = new Node(Move.SLACK_OFF);
			movebank[25] = new Node(Move.CHARM);
			movebank[28] = new Node(Move.ROLLOUT);
			movebank[31] = new Node(Move.COTTON_GUARD);
			movebank[35] = new Node(Move.ENDEAVOR);
			movebank[39] = new Node(Move.LEECH_SEED);
			movebank[42] = new Node(Move.GIGA_DRAIN);
			movebank[46] = new Node(Move.LEAF_STORM);
			break;
		case 39:
			movebank = new Node[60];
			movebank[0] = new Node(Move.POWER_WHIP);
			movebank[0].addToEnd(new Node(Move.HORN_LEECH));
			movebank[0].addToEnd(new Node(Move.FLAME_CHARGE));
			movebank[0].addToEnd(new Node(Move.COVET));
			movebank[14] = new Node(Move.STOMP);
			movebank[15] = new Node(Move.HEADBUTT);
			movebank[17] = new Node(Move.FRUSTRATION);
			movebank[24] = new Node(Move.RETURN);
			movebank[28] = new Node(Move.TAKE_DOWN);
			movebank[31] = new Node(Move.COTTON_GUARD);
			movebank[34] = new Node(Move.POWER_WHIP);
			movebank[38] = new Node(Move.HORN_LEECH);
			movebank[42] = new Node(Move.DOUBLE$EDGE);
			movebank[45] = new Node(Move.DRILL_RUN);
			movebank[49] = new Node(Move.HEAD_SMASH);
			movebank[54] = new Node(Move.WOOD_HAMMER);
			movebank[59] = new Node(Move.SYNTHESIS);
			break;
		case 40:
			movebank = new Node[60];
			movebank[0] = new Node(Move.DAZZLING_GLEAM);
			movebank[0].addToEnd(new Node(Move.DRAINING_KISS));
			movebank[0].addToEnd(new Node(Move.LIGHT_SCREEN));
			movebank[0].addToEnd(new Node(Move.SUNNY_DAY));
			movebank[0].addToEnd(new Node(Move.COVET));
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
			movebank = new Node[20];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[19] = new Node(Move.QUIVER_DANCE);
			break;
		case 42:
			movebank = new Node[37];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[14] = new Node(Move.SWORD_SPIN);
			movebank[18] = new Node(Move.BUG_BITE);
			movebank[22] = new Node(Move.SLASH);
			movebank[25] = new Node(Move.CROSS_CHOP);
			movebank[28] = new Node(Move.U$TURN);
			movebank[30] = new Node(Move.AGILITY);
			movebank[33] = new Node(Move.X$SCISSOR);
			movebank[36] = new Node(Move.SACRED_SWORD);
			break;
		case 43:
			movebank = new Node[50];
			movebank[0] = new Node(Move.LOW_KICK);
			movebank[2] = new Node(Move.LEER);
			movebank[4] = new Node(Move.FURY_CUTTER);
			movebank[7] = new Node(Move.KARATE_CHOP);
			movebank[11] = new Node(Move.BRUTAL_SWING);
			movebank[14] = new Node(Move.SWORD_SPIN);
			movebank[18] = new Node(Move.BUG_BITE);
			movebank[22] = new Node(Move.SLASH);
			movebank[25] = new Node(Move.CROSS_CHOP);
			movebank[28] = new Node(Move.U$TURN);
			movebank[30] = new Node(Move.AGILITY);
			movebank[33] = new Node(Move.X$SCISSOR);
			movebank[36] = new Node(Move.SACRED_SWORD);
			movebank[38] = new Node(Move.STAFF_JAB);
			movebank[41] = new Node(Move.FIRST_IMPRESSION);
			movebank[44] = new Node(Move.DETECT);
			movebank[49] = new Node(Move.NO_RETREAT);
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
			movebank[0].addToEnd(new Node(Move.FURY_SWIPES));
			movebank[1] = new Node(Move.GROWL);
			movebank[2] = new Node(Move.ABSORB);
			movebank[5] = new Node(Move.WATER_GUN);
			movebank[8] = new Node(Move.HAZE);
			movebank[11] = new Node(Move.MEGA_DRAIN);
			movebank[15] = new Node(Move.FAKE_OUT);
			movebank[19] = new Node(Move.BUBBLEBEAM);
			movebank[24] = new Node(Move.GIGA_DRAIN);
			movebank[29] = new Node(Move.RAIN_DANCE);
			movebank[34] = new Node(Move.KNOCK_OFF);
			movebank[39] = new Node(Move.ZEN_HEADBUTT);
			movebank[44] = new Node(Move.ENERGY_BALL);
			movebank[49] = new Node(Move.HYDRO_PUMP);
			break;
		case 46:
			movebank = new Node[1];
			movebank[0] = new Node(Move.TEETER_DANCE);
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[0].addToEnd(new Node(Move.FAKE_OUT));
			movebank[0].addToEnd(new Node(Move.FURY_SWIPES));
			movebank[0].addToEnd(new Node(Move.FLAIL));
			movebank[0].addToEnd(new Node(Move.HAZE));
			movebank[0].addToEnd(new Node(Move.BUBBLEBEAM));
			movebank[0].addToEnd(new Node(Move.GIGA_DRAIN));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
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
			movebank[7] = new Node(Move.BABY$DOLL_EYES);
			movebank[12] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.MAGICAL_LEAF);
			movebank[17] = new Node(Move.DRAINING_KISS);
			movebank[19] = new Node(Move.MAGIC_REFLECT);
			movebank[22] = new Node(Move.MOONLIGHT);
			movebank[24] = new Node(Move.CHARM);
			movebank[27] = new Node(Move.MAGIC_POWDER);
			movebank[27].next = new Node(Move.SPARKLING_TERRAIN);
			movebank[29] = new Node(Move.MAGIC_BLAST);
			movebank[32] = new Node(Move.LUSTER_PURGE);
			movebank[34] = new Node(Move.TRICK);
			movebank[37] = new Node(Move.SPARKLY_SWIRL);
			movebank[39] = new Node(Move.GLITZY_GLOW);
			movebank[42] = new Node(Move.HOCUS_POCUS);
			movebank[45] = new Node(Move.FIERY_DANCE);
			movebank[48] = new Node(Move.MAGIC_TOMB);
			movebank[51] = new Node(Move.GLITTER_DANCE);
			movebank[55] = new Node(Move.VANISHING_ACT);
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
			movebank[28] = new Node(Move.STEALTH_ROCK);
			movebank[31] = new Node(Move.SELF$DESTRUCT);
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
			movebank[28] = new Node(Move.STEALTH_ROCK);
			movebank[31] = new Node(Move.SELF$DESTRUCT);
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
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].addToEnd(new Node(Move.TACKLE));
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[5] = new Node(Move.MUD_SPORT);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[20] = new Node(Move.MAGNITUDE);
			movebank[22] = new Node(Move.SMACK_DOWN);
			movebank[25] = new Node(Move.ROLLOUT);
			movebank[28] = new Node(Move.STEALTH_ROCK);
			movebank[31] = new Node(Move.SELF$DESTRUCT);
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
			movebank[27] = new Node(Move.STEALTH_ROCK);
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
			movebank[27] = new Node(Move.STEALTH_ROCK);
			movebank[29] = new Node(Move.ROCK_POLISH);
			movebank[34] = new Node(Move.PSYCHIC_FANGS);
			movebank[37] = new Node(Move.CURSE);
			movebank[41] = new Node(Move.SKULL_BASH);
			movebank[45] = new Node(Move.IRON_TAIL);
			movebank[49] = new Node(Move.EARTHQUAKE);
			break;
		case 54:
			movebank = new Node[70];
			movebank[0] = new Node(Move.HARDEN);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[5] = new Node(Move.SCREECH);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[10] = new Node(Move.BIND);
			movebank[14] = new Node(Move.ROCK_THROW);
			movebank[18] = new Node(Move.LOW_SWEEP);
			movebank[21] = new Node(Move.PSYCHO_CUT);
			movebank[23] = new Node(Move.ROCK_TOMB);
			movebank[27] = new Node(Move.STEALTH_ROCK);
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
			movebank[69] = new Node(Move.EXPANDING_FORCE);
			break;
		case 55:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.ROCK_THROW);
			movebank[8] = new Node(Move.BITE);
			movebank[11] = new Node(Move.THIEF);
			movebank[14] = new Node(Move.ROCK_POLISH);
			movebank[18] = new Node(Move.HOWL);
			movebank[20] = new Node(Move.ROCK_BLAST);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			movebank[26] = new Node(Move.PLAY_ROUGH);
			movebank[30] = new Node(Move.THUNDER_FANG);
			movebank[32] = new Node(Move.KNOCK_OFF);
			break;
		case 56:
			movebank = new Node[70];
			movebank[0] = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.ROCK_THROW);
			movebank[8] = new Node(Move.BITE);
			movebank[11] = new Node(Move.THIEF);
			movebank[14] = new Node(Move.ROCK_POLISH);
			movebank[18] = new Node(Move.HOWL);
			movebank[20] = new Node(Move.ROCK_BLAST);
			movebank[23] = new Node(Move.FLAME_WHEEL);
			movebank[26] = new Node(Move.PLAY_ROUGH);
			movebank[30] = new Node(Move.THUNDER_FANG);
			movebank[32] = new Node(Move.KNOCK_OFF);
			movebank[34] = new Node(Move.STEALTH_ROCK);
			movebank[37] = new Node(Move.DARK_PULSE);
			movebank[40] = new Node(Move.POWER_GEM);
			movebank[44] = new Node(Move.MAGIC_FANG);
			movebank[48] = new Node(Move.ACCELEROCK);
			movebank[52] = new Node(Move.THROAT_CHOP);
			movebank[54] = new Node(Move.FIRE_BLAST);
			movebank[58] = new Node(Move.PSYCHIC_FANGS);
			movebank[62] = new Node(Move.PARTING_SHOT);
			movebank[66] = new Node(Move.PLASMA_FISTS);
			movebank[69] = new Node(Move.JAW_LOCK);
			break;
		case 57:
			movebank = new Node[36];
			movebank[0] = new Node(Move.KARATE_CHOP);
			movebank[2] = new Node(Move.DOUBLE_KICK);
			movebank[4] = new Node(Move.MUD$SLAP);
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
			movebank[4] = new Node(Move.MUD$SLAP);
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
			movebank[52] = new Node(Move.KNOCK_OFF);
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
			movebank[19] = new Node(Move.COVET);
			movebank[24] = new Node(Move.DEFOG);
			movebank[29] = new Node(Move.NIGHT_SHADE);
			break;
		case 60:
			movebank = new Node[60];
			movebank[0] = new Node(Move.FROSTBIND);
			movebank[14] = new Node(Move.ICE_SHARD);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[21] = new Node(Move.SCARY_FACE);
			movebank[24] = new Node(Move.SNOWSCAPE);
			movebank[28] = new Node(Move.NIGHT_SHADE);
			movebank[32] = new Node(Move.MIST_BALL);
			movebank[36] = new Node(Move.ICE_BEAM);
			movebank[39] = new Node(Move.PSYCHIC);
			movebank[42] = new Node(Move.TRICK);
			movebank[45] = new Node(Move.BLIZZARD);
			movebank[49] = new Node(Move.AURORA_VEIL);
			movebank[52] = new Node(Move.FREEZING_GLARE);
			movebank[59] = new Node(Move.EXPANDING_FORCE);
			break;
		case 61:
			movebank = new Node[60];
			movebank[0] = new Node(Move.TAKE_DOWN);
			movebank[0].addToEnd(new Node(Move.LEER));
			movebank[4] = new Node(Move.HEADBUTT);
			movebank[9] = new Node(Move.POWDER_SNOW);
			movebank[14] = new Node(Move.HORN_LEECH);
			movebank[19] = new Node(Move.AURORA_BOOST);
			movebank[22] = new Node(Move.ICE_SHARD);
			movebank[24] = new Node(Move.MAGIC_BLAST);
			movebank[27] = new Node(Move.ENCORE);
			movebank[29] = new Node(Move.LIGHT_BEAM);
			movebank[32] = new Node(Move.SNOWSCAPE);
			movebank[34] = new Node(Move.FROSTBIND);
			movebank[38] = new Node(Move.AURORA_VEIL);
			movebank[41] = new Node(Move.TRICK);
			movebank[44] = new Node(Move.BLIZZARD);
			movebank[49] = new Node(Move.TWINKLE_TACKLE);
			movebank[54] = new Node(Move.SPARKLING_TERRAIN);
			movebank[59] = new Node(Move.TRICK_ROOM);
			break;
		case 62:
			movebank = new Node[40];
			movebank[0] = new Node(Move.POWDER_SNOW);
			movebank[0].addToEnd(new Node(Move.STRUGGLE_BUG));
			movebank[39] = new Node(Move.STICKY_WEB);
			break;
		case 63:
			movebank = new Node[55];
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
			movebank[27] = new Node(Move.AURORA_VEIL);
			movebank[31] = new Node(Move.BUG_BUZZ);
			movebank[35] = new Node(Move.ICE_BEAM);
			movebank[39] = new Node(Move.BLIZZARD);
			movebank[44] = new Node(Move.TAILWIND);
			movebank[49] = new Node(Move.QUIVER_DANCE);
			movebank[54] = new Node(Move.FROSTBIND);
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
			movebank[0].next = new Node(Move.CIRCLE_THROW);
			movebank[4] = new Node(Move.FEINT_ATTACK);
			movebank[7] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.DRILL_RUN);
			movebank[19] = new Node(Move.HEAD_SMASH);
			movebank[24] = new Node(Move.SPIKY_SHIELD);
			movebank[29] = new Node(Move.STEALTH_ROCK);
			movebank[34] = new Node(Move.STORM_THROW);
			movebank[39] = new Node(Move.BODY_PRESS);
			break;
		case 67:
			movebank = new Node[70];
			movebank[0] = new Node(Move.HEADBUTT);
			movebank[0].next = new Node(Move.CIRCLE_THROW);
			movebank[4] = new Node(Move.FEINT_ATTACK);
			movebank[7] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.DRILL_RUN);
			movebank[19] = new Node(Move.HEAD_SMASH);
			movebank[24] = new Node(Move.SPIKY_SHIELD);
			movebank[29] = new Node(Move.STEALTH_ROCK);
			movebank[34] = new Node(Move.STORM_THROW);
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
			movebank[24] = new Node(Move.COVET);
			movebank[27] = new Node(Move.BODY_SLAM);
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
			movebank[24] = new Node(Move.COVET);
			movebank[27] = new Node(Move.BODY_SLAM);
			movebank[30] = new Node(Move.REST);
			movebank[30].next = new Node(Move.SNORE);
			movebank[31] = new Node(Move.FROSTBIND);
			movebank[35] = new Node(Move.SNOWSCAPE);
			movebank[39] = new Node(Move.AURORA_VEIL);
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
			movebank[24] = new Node(Move.COVET);
			movebank[27] = new Node(Move.BODY_SLAM);
			movebank[30] = new Node(Move.REST);
			movebank[30].next = new Node(Move.SNORE);
			movebank[31] = new Node(Move.FROSTBIND);
			movebank[35] = new Node(Move.SNOWSCAPE);
			movebank[39] = new Node(Move.AURORA_VEIL);
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
			movebank[4] = new Node(Move.FLASH);
			movebank[6] = new Node(Move.POWDER_SNOW);
			movebank[8] = new Node(Move.COVET);
			movebank[10] = new Node(Move.MAGIC_REFLECT);
			movebank[14] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.LIGHT_BEAM);
			movebank[19] = new Node(Move.ICE_SHARD);
			movebank[23] = new Node(Move.AQUA_JET);
			movebank[26] = new Node(Move.BOUNCE);
			movebank[29] = new Node(Move.AURORA_BEAM);
			break;
		case 72:
			movebank = new Node[60];
			movebank[0] = new Node(Move.BUBBLE);
			movebank[2] = new Node(Move.POUND);
			movebank[4] = new Node(Move.FLASH);
			movebank[6] = new Node(Move.POWDER_SNOW);
			movebank[8] = new Node(Move.COVET);
			movebank[10] = new Node(Move.MAGIC_REFLECT);
			movebank[14] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.LIGHT_BEAM);
			movebank[19] = new Node(Move.ICE_SHARD);
			movebank[23] = new Node(Move.AQUA_JET);
			movebank[26] = new Node(Move.BOUNCE);
			movebank[31] = new Node(Move.DAZZLING_GLEAM);
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
			movebank[9] = new Node(Move.THIEF);
			movebank[16] = new Node(Move.BUG_BITE);
			movebank[20] = new Node(Move.FEINT_ATTACK);
			movebank[29] = new Node(Move.SLASH);
			movebank[34] = new Node(Move.LEECH_LIFE);
			break;
		case 74:
			movebank = new Node[60];
			movebank[0] = new Node(Move.INFESTATION);
			movebank[0].next = new Node(Move.FLASH);
			movebank[3] = new Node(Move.PAYBACK);
			movebank[6] = new Node(Move.SLASH);
			movebank[9] = new Node(Move.THIEF);
			movebank[16] = new Node(Move.BUG_BITE);
			movebank[19] = new Node(Move.NIGHT_SLASH);
			movebank[22] = new Node(Move.BOUNCE);
			movebank[24] = new Node(Move.FURY_CUTTER);
			movebank[29] = new Node(Move.MOONLIGHT);
			movebank[32] = new Node(Move.QUIVER_DANCE);
			movebank[35] = new Node(Move.U$TURN);
			movebank[39] = new Node(Move.NIGHT_DAZE);
			movebank[43] = new Node(Move.X$SCISSOR);
			movebank[46] = new Node(Move.DARKEST_LARIAT);
			movebank[49] = new Node(Move.EXTREME_SPEED);
			movebank[54] = new Node(Move.KNOCK_OFF);
			movebank[59] = new Node(Move.FIRST_IMPRESSION);
			break;
		case 75:
			movebank = new Node[30];
			movebank[0] = new Node(Move.CONFUSION);
			movebank[0].next = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[6] = new Node(Move.SWIFT);
			movebank[8] = new Node(Move.MAGIC_POWDER);
			movebank[11] = new Node(Move.AROMATHERAPY);
			movebank[15] = new Node(Move.SPARKLY_SWIRL);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[23] = new Node(Move.HEAL_PULSE);
			movebank[26] = new Node(Move.TRICK);
			movebank[29] = new Node(Move.HOCUS_POCUS);
			break;
		case 76:
			movebank = new Node[37];
			movebank[0] = new Node(Move.CONFUSION);
			movebank[0].next = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[6] = new Node(Move.SWIFT);
			movebank[8] = new Node(Move.MAGIC_POWDER);
			movebank[11] = new Node(Move.AROMATHERAPY);
			movebank[15] = new Node(Move.SPARKLY_SWIRL);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[23] = new Node(Move.HEAL_PULSE);
			movebank[26] = new Node(Move.TRICK);
			movebank[29] = new Node(Move.HOCUS_POCUS);
			movebank[31] = new Node(Move.BRUTAL_SWING);
			movebank[34] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.CALM_MIND);
			break;
		case 77:
			movebank = new Node[70];
			movebank[0] = new Node(Move.NUZZLE);
			movebank[0].addToEnd(new Node(Move.CONFUSION));
			movebank[0].addToEnd(new Node(Move.PLAY_NICE));
			movebank[4] = new Node(Move.LIFE_DEW);
			movebank[6] = new Node(Move.SWIFT);
			movebank[8] = new Node(Move.MAGIC_POWDER);
			movebank[11] = new Node(Move.AROMATHERAPY);
			movebank[15] = new Node(Move.SPARKLY_SWIRL);
			movebank[19] = new Node(Move.PSYBEAM);
			movebank[23] = new Node(Move.HEAL_PULSE);
			movebank[26] = new Node(Move.TRICK);
			movebank[29] = new Node(Move.HOCUS_POCUS);
			movebank[31] = new Node(Move.BRUTAL_SWING);
			movebank[34] = new Node(Move.MAGIC_BLAST);
			movebank[36] = new Node(Move.CALM_MIND);
			movebank[41] = new Node(Move.MAGIC_REFLECT);
			movebank[44] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[48] = new Node(Move.SPARKLING_TERRAIN);
			movebank[51] = new Node(Move.PSYCHIC);
			movebank[54] = new Node(Move.MAGIC_TOMB);
			movebank[57] = new Node(Move.HEALING_WISH);
			movebank[60] = new Node(Move.VANISHING_ACT);
			movebank[64] = new Node(Move.MORNING_SUN);
			movebank[69] = new Node(Move.EXPANDING_FORCE);
			break;
		case 78:
			movebank = new Node[29];
			movebank[0] = new Node(Move.PSYWAVE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.WATER_GUN);
			movebank[7] = new Node(Move.BABY$DOLL_EYES);
			movebank[10] = new Node(Move.CONFUSION);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[15] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.AQUA_VEIL);
			movebank[20] = new Node(Move.PSYBEAM);
			movebank[25] = new Node(Move.CONFUSE_RAY);
			movebank[28] = new Node(Move.TRICK);
			break;
		case 79:
			movebank = new Node[70];
			movebank[0] = new Node(Move.PSYWAVE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.WATER_GUN);
			movebank[7] = new Node(Move.BABY$DOLL_EYES);
			movebank[10] = new Node(Move.CONFUSION);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[15] = new Node(Move.WATER_PULSE);
			movebank[17] = new Node(Move.AQUA_VEIL);
			movebank[20] = new Node(Move.PSYBEAM);
			movebank[25] = new Node(Move.CONFUSE_RAY);
			movebank[28] = new Node(Move.TRICK);
			movebank[30] = new Node(Move.HYPNOSIS);
			movebank[33] = new Node(Move.BUBBLEBEAM);
			movebank[36] = new Node(Move.PSYCHIC);
			movebank[39] = new Node(Move.MAGIC_TOMB);
			movebank[42] = new Node(Move.LIGHT_SCREEN);
			movebank[45] = new Node(Move.HYDRO_PUMP);
			movebank[49] = new Node(Move.EXPANDING_FORCE);
			movebank[54] = new Node(Move.BLIZZARD);
			movebank[59] = new Node(Move.CALM_MIND);
			movebank[69] = new Node(Move.WATER_SPOUT);
			break;
		case 80:
			movebank = new Node[39];
			movebank[0] = new Node(Move.VINE_WHIP);
			movebank[2] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.FAKE_TEARS);
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
			movebank[4] = new Node(Move.FAKE_TEARS);
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
			movebank[58] = new Node(Move.EXPANDING_FORCE);
			movebank[62] = new Node(Move.MOONBLAST);
			movebank[69] = new Node(Move.PETAL_DANCE);
			break;
		case 82:
			movebank = new Node[30];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[4] = new Node(Move.GLARE);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[24] = new Node(Move.DREAM_EATER);
			movebank[29] = new Node(Move.GRAVITY);
			break;
		case 83:
			movebank = new Node[50];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[4] = new Node(Move.GLARE);
			movebank[8] = new Node(Move.CONFUSION);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[24] = new Node(Move.DREAM_EATER);
			movebank[29] = new Node(Move.GRAVITY);
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
			movebank[29] = new Node(Move.GRAVITY);
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
			movebank[0] = new Node(Move.AURA_SPHERE);
			movebank[0].addToEnd(new Node(Move.DREAM_EATER));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[0].addToEnd(new Node(Move.HEALING_WISH));
			movebank[0].addToEnd(new Node(Move.TELEPORT));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.FLASH));
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
			movebank[41] = new Node(Move.GLITZY_GLOW);
			movebank[44] = new Node(Move.PSYCHIC);
			movebank[48] = new Node(Move.MOONBLAST);
			movebank[54] = new Node(Move.GLITTER_DANCE);
			movebank[59] = new Node(Move.BLACK_HOLE_ECLIPSE);
			movebank[64] = new Node(Move.EXPANDING_FORCE);
			break;
		case 88:
			movebank = new Node[60];
			movebank[0] = new Node(Move.TELEPORT);
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.NIGHT_SLASH));
			movebank[0].addToEnd(new Node(Move.PSYCHIC));
			movebank[0].addToEnd(new Node(Move.CALM_MIND));
			movebank[0].addToEnd(new Node(Move.FURY_CUTTER));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[2] = new Node(Move.DOUBLE_TEAM);
			movebank[5] = new Node(Move.CONFUSION);
			movebank[8] = new Node(Move.HYPNOSIS);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.DRAINING_KISS);
			movebank[17] = new Node(Move.PSYBEAM);
			movebank[19] = new Node(Move.AERIAL_ACE);
			movebank[22] = new Node(Move.FALSE_SWIPE);
			movebank[25] = new Node(Move.SLASH);
			movebank[27] = new Node(Move.TRICK);
			movebank[29] = new Node(Move.PSYCHO_CUT);
			movebank[32] = new Node(Move.SWORDS_DANCE);
			movebank[36] = new Node(Move.ZEN_HEADBUTT);
			movebank[44] = new Node(Move.CLOSE_COMBAT);
			movebank[48] = new Node(Move.KNOCK_OFF);
			movebank[53] = new Node(Move.FEINT);
			movebank[59] = new Node(Move.SOLAR_BLADE);
			break;
		case 89:
			movebank = new Node[75];
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
			movebank[59] = new Node(Move.TRI$ATTACK);
			movebank[64] = new Node(Move.MOONBLAST);
			movebank[74] = new Node(Move.EXTREME_SPEED);
			break;
		case 90:
			movebank = new Node[30];
			movebank[0] = new Node(Move.PECK);
			movebank[0].next = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.HYPNOSIS);
			movebank[5] = new Node(Move.WRAP);
			movebank[8] = new Node(Move.PAYBACK);
			movebank[11] = new Node(Move.PLUCK);
			movebank[14] = new Node(Move.PSYBEAM);
			movebank[16] = new Node(Move.SWAGGER);
			movebank[18] = new Node(Move.SLASH);
			movebank[21] = new Node(Move.THIEF);
			movebank[23] = new Node(Move.NIGHT_SLASH);
			movebank[26] = new Node(Move.PSYCHO_CUT);
			movebank[29] = new Node(Move.SWITCHEROO);
			break;
		case 91:
			movebank = new Node[60];
			movebank[0] = new Node(Move.REVERSAL);
			movebank[0].next = new Node(Move.PECK);
			movebank[0].next.next = new Node(Move.TACKLE);
			movebank[2] = new Node(Move.HYPNOSIS);
			movebank[5] = new Node(Move.WRAP);
			movebank[8] = new Node(Move.PAYBACK);
			movebank[11] = new Node(Move.PLUCK);
			movebank[14] = new Node(Move.PSYBEAM);
			movebank[16] = new Node(Move.SWAGGER);
			movebank[18] = new Node(Move.SLASH);
			movebank[21] = new Node(Move.THIEF);
			movebank[23] = new Node(Move.NIGHT_SLASH);
			movebank[26] = new Node(Move.PSYCHO_CUT);
			movebank[29] = new Node(Move.SWITCHEROO);
			movebank[32] = new Node(Move.THROAT_CHOP);
			movebank[36] = new Node(Move.FOUL_PLAY);
			movebank[41] = new Node(Move.TOPSY$TURVY);
			movebank[46] = new Node(Move.SUPERPOWER);
			movebank[49] = new Node(Move.GRAVITY);
			movebank[54] = new Node(Move.KNOCK_OFF);
			movebank[59] = new Node(Move.PARTING_SHOT);
			break;
		case 92:
			movebank = new Node[31];
			movebank[0] = new Node(Move.EMBER);
			movebank[2] = new Node(Move.TACKLE);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[8] = new Node(Move.HEADBUTT);
			movebank[10] = new Node(Move.WILL$O$WISP);
			movebank[14] = new Node(Move.BITE);
			movebank[16] = new Node(Move.FLAME_WHEEL);
			movebank[19] = new Node(Move.BODY_SLAM);
			movebank[22] = new Node(Move.METAL_CLAW);
			movebank[24] = new Node(Move.FIRE_FANG);
			movebank[27] = new Node(Move.COVET);
			movebank[30] = new Node(Move.FLAMETHROWER);
			break;
		case 93:
			movebank = new Node[75];
			movebank[0] = new Node(Move.EMBER);
			movebank[2] = new Node(Move.TACKLE);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[8] = new Node(Move.HEADBUTT);
			movebank[10] = new Node(Move.WILL$O$WISP);
			movebank[14] = new Node(Move.BITE);
			movebank[16] = new Node(Move.FLAME_WHEEL);
			movebank[19] = new Node(Move.BODY_SLAM);
			movebank[22] = new Node(Move.METAL_CLAW);
			movebank[24] = new Node(Move.FIRE_FANG);
			movebank[27] = new Node(Move.COVET);
			movebank[30] = new Node(Move.FLAMETHROWER);
			movebank[35] = new Node(Move.FLASH_CANNON);
			movebank[39] = new Node(Move.VOLT_TACKLE);
			movebank[43] = new Node(Move.FLARE_BLITZ);
			movebank[49] = new Node(Move.DOUBLE$EDGE);
			movebank[54] = new Node(Move.AGILITY);
			movebank[59] = new Node(Move.MORNING_SUN);
			movebank[64] = new Node(Move.SUNNY_DAY);
			movebank[74] = new Node(Move.EXTREME_SPEED);
			break;
		case 94:
			movebank = new Node[15];
			movebank[0] = new Node(Move.WILL$O$WISP);
			movebank[14] = new Node(Move.EMBER);
			break;
		case 95:
			movebank = new Node[32];
			movebank[0] = new Node(Move.WILL$O$WISP);
			movebank[14] = new Node(Move.EMBER);
			movebank[15] = new Node(Move.FLAME_WHEEL);
			movebank[20] = new Node(Move.FURY_SWIPES);
			movebank[25] = new Node(Move.MACH_PUNCH);
			movebank[28] = new Node(Move.BRICK_BREAK);
			movebank[31] = new Node(Move.BLAZE_KICK);
			break;
		case 96:
			movebank = new Node[60];
			movebank[0] = new Node(Move.WILL$O$WISP);
			movebank[14] = new Node(Move.EMBER);
			movebank[15] = new Node(Move.FLAME_WHEEL);
			movebank[20] = new Node(Move.FURY_SWIPES);
			movebank[25] = new Node(Move.MACH_PUNCH);
			movebank[28] = new Node(Move.BRICK_BREAK);
			movebank[31] = new Node(Move.BLAZE_KICK);
			movebank[35] = new Node(Move.KNOCK_OFF);
			movebank[39] = new Node(Move.WAKE$UP_SLAP);
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
			movebank[24] = new Node(Move.WILL$O$WISP);
			movebank[28] = new Node(Move.EARTH_POWER);
			movebank[31] = new Node(Move.HEX);
			movebank[35] = new Node(Move.AUTOMOTIZE);
			movebank[39] = new Node(Move.SWITCHEROO);
			movebank[44] = new Node(Move.FIRE_BLAST);
			movebank[49] = new Node(Move.EXPLOSION);
			movebank[54] = new Node(Move.SHADOW_BALL);
			movebank[59] = new Node(Move.STEEL_BEAM);
			movebank[64] = new Node(Move.ERUPTION);
			break;
		case 98:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.EMBER);
			movebank[8] = new Node(Move.BODY_SLAM);
			movebank[10] = new Node(Move.MUD$SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL$O$WISP);
			movebank[32] = new Node(Move.DETECT);
			break;
		case 99:
			movebank = new Node[53];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.EMBER);
			movebank[8] = new Node(Move.BODY_SLAM);
			movebank[10] = new Node(Move.MUD$SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL$O$WISP);
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
			movebank[8] = new Node(Move.BODY_SLAM);
			movebank[10] = new Node(Move.MUD$SLAP);
			movebank[13] = new Node(Move.DOUBLE_TEAM);
			movebank[18] = new Node(Move.FLAME_WHEEL);
			movebank[24] = new Node(Move.MUD_BOMB);
			movebank[28] = new Node(Move.WILL$O$WISP);
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
			movebank = new Node[20];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[5] = new Node(Move.WILL$O$WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			movebank[17] = new Node(Move.ROCK_POLISH);
			movebank[19] = new Node(Move.SCREECH);
			break;
		case 102:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.HARDEN);
			movebank[5] = new Node(Move.WILL$O$WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			movebank[17] = new Node(Move.ROCK_POLISH);
			movebank[19] = new Node(Move.SCREECH);
			movebank[19].next = new Node(Move.RECOVER);
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
			movebank[5] = new Node(Move.WILL$O$WISP);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[14] = new Node(Move.INCINERATE);
			movebank[17] = new Node(Move.ROCK_POLISH);
			movebank[19] = new Node(Move.SCREECH);
			movebank[19].next = new Node(Move.RECOVER);
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
			movebank[51] = new Node(Move.LAVA_LAIR);
			movebank[56] = new Node(Move.POWER_GEM);
			movebank[61] = new Node(Move.ERUPTION);
			movebank[64] = new Node(Move.HYPER_BEAM);
			break;
		case 104:
			movebank = new Node[22];
			movebank[0] = new Node(Move.LEER);
			movebank[0].next = new Node(Move.EMBER);
			movebank[2] = new Node(Move.HOWL);
			movebank[5] = new Node(Move.BITE);
			movebank[7] = new Node(Move.SMOG);
			movebank[10] = new Node(Move.ROAR);
			movebank[13] = new Node(Move.THUNDER_FANG);
			movebank[13].next = new Node(Move.FIRE_FANG);
			movebank[15] = new Node(Move.ODOR_SLEUTH);
			movebank[19] = new Node(Move.BEAT_UP);
			movebank[21] = new Node(Move.THIEF);
			break;
		case 105:
			movebank = new Node[65];
			movebank[0] = new Node(Move.LEER);
			movebank[0].next = new Node(Move.EMBER);
			movebank[2] = new Node(Move.HOWL);
			movebank[5] = new Node(Move.BITE);
			movebank[7] = new Node(Move.SMOG);
			movebank[10] = new Node(Move.ROAR);
			movebank[13] = new Node(Move.THUNDER_FANG);
			movebank[13].next = new Node(Move.FIRE_FANG);
			movebank[15] = new Node(Move.ODOR_SLEUTH);
			movebank[19] = new Node(Move.BEAT_UP);
			movebank[21] = new Node(Move.THIEF);
			movebank[21].next = new Node(Move.INCINERATE);
			movebank[24] = new Node(Move.FEINT_ATTACK);
			movebank[27] = new Node(Move.CRUNCH);
			movebank[31] = new Node(Move.FLAME_BURST);
			movebank[35] = new Node(Move.DARK_PULSE);
			movebank[39] = new Node(Move.FLAMETHROWER);
			movebank[42] = new Node(Move.FOUL_PLAY);
			movebank[45] = new Node(Move.SUNNY_DOOM);
			movebank[49] = new Node(Move.NASTY_PLOT);
			movebank[54] = new Node(Move.HEAT_WAVE);
			movebank[59] = new Node(Move.INFERNO);
			movebank[64] = new Node(Move.PARTING_SHOT);
			break;
		case 106:
			movebank = new Node[23];
			movebank[0] = new Node(Move.DOUBLE_TEAM);
			movebank[0].addToEnd(new Node(Move.MAGIC_POWDER));
			movebank[0].addToEnd(new Node(Move.EMBER));
			movebank[0].addToEnd(new Node(Move.WILL$O$WISP));
			movebank[4] = new Node(Move.SMOKESCREEN);
			movebank[7] = new Node(Move.CONFUSION);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[13] = new Node(Move.HYPNOSIS);
			movebank[16] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.FLAME_WHEEL);
			movebank[22] = new Node(Move.TRICK);
			break;
		case 107:
			movebank = new Node[65];
			movebank[0] = new Node(Move.DOUBLE_TEAM);
			movebank[0].addToEnd(new Node(Move.MAGIC_POWDER));
			movebank[0].addToEnd(new Node(Move.EMBER));
			movebank[0].addToEnd(new Node(Move.WILL$O$WISP));
			movebank[4] = new Node(Move.SMOKESCREEN);
			movebank[7] = new Node(Move.CONFUSION);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[13] = new Node(Move.HYPNOSIS);
			movebank[16] = new Node(Move.SWIFT);
			movebank[19] = new Node(Move.FLAME_WHEEL);
			movebank[22] = new Node(Move.TRICK);
			movebank[24] = new Node(Move.MAGIC_BLAST);
			movebank[28] = new Node(Move.FLAMETHROWER);
			movebank[30] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[34] = new Node(Move.HOCUS_POCUS);
			movebank[38] = new Node(Move.TRI$ATTACK);
			movebank[42] = new Node(Move.SPARKLING_TERRAIN);
			movebank[46] = new Node(Move.FIRE_BLAST);
			movebank[49] = new Node(Move.MAGIC_TOMB);
			movebank[53] = new Node(Move.MAGIC_REFLECT);
			movebank[56] = new Node(Move.SPACIAL_REND);
			movebank[59] = new Node(Move.BLUE_FLARE);
			movebank[64] = new Node(Move.VANISHING_ACT);
			break;
		case 108:
			movebank = new Node[32];
			movebank[0] = new Node(Move.GROWL);
			movebank[2] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.FLASH);
			movebank[8] = new Node(Move.EMBER);
			movebank[11] = new Node(Move.FLASH_RAY);
			movebank[14] = new Node(Move.QUICK_ATTACK);
			movebank[17] = new Node(Move.FLAME_WHEEL);
			movebank[17].addToEnd(new Node(Move.FLAME_BURST));
			movebank[19] = new Node(Move.DRAINING_KISS);
			movebank[21] = new Node(Move.COVET);
			movebank[23] = new Node(Move.LIGHT_BEAM);
			movebank[25] = new Node(Move.INCINERATE);
			movebank[28] = new Node(Move.FIRE_SPIN);
			movebank[31] = new Node(Move.SPIRIT_BREAK);
			movebank[31].addToEnd(new Node(Move.GLITZY_GLOW));
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
			movebank[0].addToEnd(new Node(Move.FIERY_DANCE));
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
			movebank = new Node[27];
			movebank[0] = new Node(Move.THUNDERSHOCK);
			movebank[3] = new Node(Move.CHARGE);
			movebank[7] = new Node(Move.WRAP);
			movebank[12] = new Node(Move.THUNDER_WAVE);
			movebank[16] = new Node(Move.COIL);
			movebank[20] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.SCARY_FACE);
			movebank[26] = new Node(Move.THUNDER_FANG);
			break;
		case 112:
			movebank = new Node[50];
			movebank[0] = new Node(Move.THUNDERSHOCK);
			movebank[3] = new Node(Move.CHARGE);
			movebank[7] = new Node(Move.WRAP);
			movebank[12] = new Node(Move.THUNDER_WAVE);
			movebank[16] = new Node(Move.COIL);
			movebank[20] = new Node(Move.SPARK);
			movebank[24] = new Node(Move.SCARY_FACE);
			movebank[26] = new Node(Move.THUNDER_FANG);
			movebank[29] = new Node(Move.DOUBLE_HIT);
			movebank[29].next = new Node(Move.PARABOLIC_CHARGE);
			movebank[33] = new Node(Move.DISCHARGE);
			movebank[36] = new Node(Move.MAGNET_RISE);
			movebank[41] = new Node(Move.THUNDER_PUNCH);
			movebank[44] = new Node(Move.CURSE);
			movebank[49] = new Node(Move.THUNDERBOLT);
			break;
		case 113:
			movebank = new Node[65];
			movebank[0] = new Node(Move.GYRO_BALL);
			movebank[0].addToEnd(new Node(Move.IRON_DEFENSE));
			movebank[0].addToEnd(new Node(Move.NIGHT_SLASH));
			movebank[0].addToEnd(new Node(Move.COIL));
			movebank[0].addToEnd(new Node(Move.SPARK));
			movebank[0].addToEnd(new Node(Move.DOUBLE_HIT));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[30] = new Node(Move.DYNAMIC_PUNCH);
			movebank[33] = new Node(Move.IRON_DEFENSE);
			movebank[36] = new Node(Move.MAGNET_RISE);
			movebank[39] = new Node(Move.SMART_STRIKE);
			movebank[42] = new Node(Move.BULLET_PUNCH);
			movebank[45] = new Node(Move.AUTOMOTIZE);
			movebank[49] = new Node(Move.HEAVY_SLAM);
			movebank[54] = new Node(Move.SUPERPOWER);
			movebank[59] = new Node(Move.VOLT_TACKLE);
			movebank[64] = new Node(Move.METEOR_MASH);
			break;
		case 114:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.MUD$SLAP);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[18] = new Node(Move.ENDURE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.COVET);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 115:
			movebank = new Node[47];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.MUD$SLAP);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[18] = new Node(Move.ENDURE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.COVET);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			movebank[27] = new Node(Move.HEADBUTT);
			movebank[27].next = new Node(Move.MAGNITUDE);
			movebank[31] = new Node(Move.SPARK);
			movebank[36] = new Node(Move.TWINKLE_TACKLE);
			movebank[40] = new Node(Move.ELECTRIC_TERRAIN);
			movebank[44] = new Node(Move.COTTON_GUARD);
			break;
		case 116:
			movebank = new Node[65];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.MUD$SLAP);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[18] = new Node(Move.ENDURE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.COVET);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			movebank[27] = new Node(Move.HEADBUTT);
			movebank[27].next = new Node(Move.MAGNITUDE);
			movebank[31] = new Node(Move.SPARK);
			movebank[36] = new Node(Move.TWINKLE_TACKLE);
			movebank[40] = new Node(Move.ELECTRIC_TERRAIN);
			movebank[44] = new Node(Move.COTTON_GUARD);
			movebank[47] = new Node(Move.SKULL_BASH);
			movebank[50] = new Node(Move.EXTREME_SPEED);
			movebank[54] = new Node(Move.VOLT_TACKLE);
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
			movebank = new Node[30];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].next = new Node(Move.GROWL);
			movebank[4] = new Node(Move.QUICK_ATTACK);
			movebank[9] = new Node(Move.COVET);
			movebank[11] = new Node(Move.IRON_DEFENSE);
			movebank[14] = new Node(Move.GYRO_BALL);
			movebank[19] = new Node(Move.METAL_SOUND);
			movebank[24] = new Node(Move.ROLLOUT);
			movebank[29] = new Node(Move.METAL_CLAW);
			break;
		case 121:
			movebank = new Node[55];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.SPIKY_SHIELD));
			movebank[9] = new Node(Move.METAL_CLAW);
			movebank[14] = new Node(Move.SNARL);
			movebank[19] = new Node(Move.TAKE_DOWN);
			movebank[24] = new Node(Move.ROLLOUT);
			movebank[29] = new Node(Move.FLASH);
			movebank[29].next = new Node(Move.FLASH_CANNON);
			movebank[34] = new Node(Move.PLAY_ROUGH);
			movebank[39] = new Node(Move.BULLET_PUNCH);
			movebank[44] = new Node(Move.IRON_HEAD);
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
			movebank[34] = new Node(Move.DOUBLE$EDGE);
			movebank[39] = new Node(Move.SPARKLE_STRIKE);
			movebank[44] = new Node(Move.HONE_CLAWS);
			movebank[49] = new Node(Move.HEAVY_SLAM);
			movebank[54] = new Node(Move.MAGIC_CRASH);
			movebank[59] = new Node(Move.STEEL_BEAM);
			break;
		case 123:
			movebank = new Node[25];
			movebank[0] = new Node(Move.NUZZLE);
			movebank[0].next = new Node(Move.QUICK_ATTACK);
			movebank[3] = new Node(Move.TAIL_WHIP);
			movebank[5] = new Node(Move.CHARGE);
			movebank[8] = new Node(Move.SPARK);
			movebank[11] = new Node(Move.COVET);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			movebank[19] = new Node(Move.ROAR);
			movebank[24] = new Node(Move.DISCHARGE);
			break;
		case 124:
			movebank = new Node[55];
			movebank[0] = new Node(Move.HOWL);
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[0].addToEnd(new Node(Move.QUICK_ATTACK));
			movebank[0].addToEnd(new Node(Move.TAIL_WHIP));
			movebank[9] = new Node(Move.SPARK);
			movebank[14] = new Node(Move.THUNDER_WAVE);
			movebank[19] = new Node(Move.DOUBLE_TEAM);
			movebank[24] = new Node(Move.ROAR);
			movebank[29] = new Node(Move.FLASH);
			movebank[29].next = new Node(Move.WHIP_SMASH);
			movebank[34] = new Node(Move.THUNDER_PUNCH);
			movebank[34].next = new Node(Move.PLAY_ROUGH);
			movebank[39] = new Node(Move.THUNDERBOLT);
			movebank[44] = new Node(Move.ELECTRO_BALL);
			movebank[49] = new Node(Move.VOLT_TACKLE);
			movebank[54] = new Node(Move.PLASMA_FISTS);
			break;
		case 125:
			movebank = new Node[75];
			movebank[0] = new Node(Move.VOLT_TACKLE);
			movebank[0].addToEnd(new Node(Move.BOUNCE));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[4] = new Node(Move.EXTREME_SPEED);
			movebank[9] = new Node(Move.ACROBATICS);
			movebank[14] = new Node(Move.DOUBLE_TEAM);
			movebank[19] = new Node(Move.SKY_ATTACK);
			movebank[24] = new Node(Move.WHIP_SMASH);
			movebank[29] = new Node(Move.PLAY_ROUGH);
			movebank[34] = new Node(Move.THUNDER_PUNCH);
			movebank[39] = new Node(Move.THUNDER);
			movebank[44] = new Node(Move.SPARKLE_STRIKE);
			movebank[49] = new Node(Move.ELECTRO_BALL);
			movebank[54] = new Node(Move.MAGIC_CRASH);
			movebank[74] = new Node(Move.PLASMA_FISTS);
			break;
		case 126:
			movebank = new Node[25];
			movebank[0] = new Node(Move.PLAY_NICE);
			movebank[4] = new Node(Move.QUICK_ATTACK);
			movebank[5] = new Node(Move.TAIL_WHIP);
			movebank[7] = new Node(Move.CHARM);
			movebank[9] = new Node(Move.FURY_SWIPES);
			movebank[12] = new Node(Move.DOUBLE_TEAM);
			movebank[15] = new Node(Move.POWER$UP_PUNCH);
			movebank[19] = new Node(Move.COVET);
			movebank[24] = new Node(Move.ROAR);
			break;
		case 127:
			movebank = new Node[55];
			movebank[0] = new Node(Move.GROWL);
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[0].addToEnd(new Node(Move.CRUNCH));
			movebank[0].addToEnd(new Node(Move.PLAY_NICE));
			movebank[4] = new Node(Move.TAKE_DOWN);
			movebank[9] = new Node(Move.DOUBLE_KICK);
			movebank[14] = new Node(Move.ROAR);
			movebank[19] = new Node(Move.POWER$UP_PUNCH);
			movebank[24] = new Node(Move.DRAIN_PUNCH);
			movebank[29] = new Node(Move.FLASH);
			movebank[29].next = new Node(Move.BEEFY_BASH);
			movebank[34] = new Node(Move.MACH_PUNCH);
			movebank[34].next = new Node(Move.PLAY_ROUGH);
			movebank[39] = new Node(Move.BULK_UP);
			movebank[39].next = new Node(Move.CIRCLE_THROW);
			movebank[44] = new Node(Move.HAMMER_ARM);
			movebank[49] = new Node(Move.ICE_PUNCH);
			movebank[49].next = new Node(Move.TWINKLE_TACKLE);
			movebank[54] = new Node(Move.SKY_UPPERCUT);
			break;
		case 128:
			movebank = new Node[60];
			movebank[0] = new Node(Move.HAMMER_ARM);
			movebank[0].addToEnd(new Node(Move.PLAY_ROUGH));
			movebank[0].addToEnd(new Node(Move.HOWL));
			movebank[0].addToEnd(new Node(Move.BULK_UP));
			movebank[4] = new Node(Move.DYNAMIC_PUNCH);
			movebank[9] = new Node(Move.CIRCLE_THROW);
			movebank[14] = new Node(Move.POWER$UP_PUNCH);
			movebank[19] = new Node(Move.SKY_UPPERCUT);
			movebank[24] = new Node(Move.BEEFY_BASH);
			movebank[29] = new Node(Move.AURA_SPHERE);
			movebank[34] = new Node(Move.SUPERPOWER);
			movebank[39] = new Node(Move.CALM_MIND);
			movebank[44] = new Node(Move.SPARKLE_STRIKE);
			movebank[49] = new Node(Move.VACUUM_WAVE);
			movebank[54] = new Node(Move.MAGIC_TOMB);
			movebank[59] = new Node(Move.FOCUS_BLAST);
			break;
		case 129:
			movebank = new Node[30];
			movebank[0] = new Node(Move.SAND_ATTACK);
			movebank[0].next = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.HARDEN);
			movebank[9] = new Node(Move.FALSE_SWIPE);
			movebank[14] = new Node(Move.MUD$SLAP);
			movebank[20] = new Node(Move.ABSORB);
			movebank[24] = new Node(Move.DIG);
			movebank[29] = new Node(Move.STICKY_WEB);
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
			movebank[14] = new Node(Move.MUD$SLAP);
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
			movebank[14] = new Node(Move.MUD$SLAP);
			movebank[22] = new Node(Move.THIEF);
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
			movebank = new Node[60];
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
			movebank[32] = new Node(Move.SPARKLING_ARIA);
			movebank[35] = new Node(Move.SHELL_SMASH);
			movebank[39] = new Node(Move.LIQUIDATION);
			movebank[44] = new Node(Move.AEROBLAST);
			movebank[49] = new Node(Move.FOCUS_BLAST);
			movebank[54] = new Node(Move.HURRICANE);
			movebank[59] = new Node(Move.WATER_SPOUT);
			break;
		case 134:
			movebank = new Node[20];
			movebank[0] = new Node(Move.RAPID_SPIN);
			movebank[0].next = new Node(Move.AMNESIA);
			movebank[0].next.next = new Node(Move.WATER_PULSE);
			movebank[14] = new Node(Move.DIVE);
			movebank[19] = new Node(Move.COVET);
			break;
		case 135:
			movebank = new Node[30];
			movebank[0] = new Node(Move.DOUBLE_SLAP);
			movebank[2] = new Node(Move.LEER);
			movebank[6] = new Node(Move.WING_ATTACK);
			movebank[9] = new Node(Move.WATER_PULSE);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[14] = new Node(Move.SUPERSONIC);
			movebank[18] = new Node(Move.ROOST);
			movebank[22] = new Node(Move.AIR_SLASH);
			movebank[25] = new Node(Move.HAZE);
			movebank[29] = new Node(Move.DEFOG);
			break;
		case 136:
			movebank = new Node[60];
			movebank[0] = new Node(Move.DOUBLE_SLAP);
			movebank[2] = new Node(Move.LEER);
			movebank[6] = new Node(Move.WING_ATTACK);
			movebank[9] = new Node(Move.WATER_PULSE);
			movebank[12] = new Node(Move.AQUA_RING);
			movebank[14] = new Node(Move.SUPERSONIC);
			movebank[18] = new Node(Move.ROOST);
			movebank[22] = new Node(Move.AIR_SLASH);
			movebank[25] = new Node(Move.HAZE);
			movebank[29] = new Node(Move.DEFOG);
			movebank[29].addToEnd(new Node(Move.TWISTER));
			movebank[29].addToEnd(new Node(Move.GUST));
			movebank[29].addToEnd(new Node(Move.GLITTERING_TORNADO));
			movebank[29].addToEnd(new Node(Move.LEAF_TORNADO));
			movebank[32] = new Node(Move.BREAKING_SWIPE);
			movebank[35] = new Node(Move.AQUA_JET);
			movebank[38] = new Node(Move.DRAGON_PULSE);
			movebank[41] = new Node(Move.WHIRLPOOL);
			movebank[44] = new Node(Move.HURRICANE);
			movebank[48] = new Node(Move.DRAGON_DANCE);
			movebank[52] = new Node(Move.WAVE_CRASH);
			movebank[59] = new Node(Move.OUTRAGE);
			break;
		case 137:
			movebank = new Node[40];
			movebank[0] = new Node(Move.SPLASH);
			movebank[14] = new Node(Move.TACKLE);
			movebank[24] = new Node(Move.FLAIL);
			movebank[29] = new Node(Move.BOUNCE);
			movebank[39] = new Node(Move.HYDRO_PUMP);
			break;
		case 138:
			movebank = new Node[56];
			movebank[0] = new Node(Move.SPLASH);
			movebank[14] = new Node(Move.TACKLE);
			movebank[19] = new Node(Move.BITE);
			movebank[22] = new Node(Move.ICE_FANG);
			movebank[25] = new Node(Move.CRUNCH);
			movebank[27] = new Node(Move.RAIN_DANCE);
			movebank[31] = new Node(Move.AQUA_TAIL);
			movebank[35] = new Node(Move.AERIAL_ACE);
			movebank[39] = new Node(Move.DRAGON_TAIL);
			movebank[43] = new Node(Move.THRASH);
			movebank[47] = new Node(Move.WATERFALL);
			movebank[51] = new Node(Move.BOUNCE);
			movebank[55] = new Node(Move.DRAGON_DANCE);
			break;
		case 139:
			movebank = new Node[50];
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
			movebank[30] = new Node(Move.SHOOTING_STARS);
			movebank[35] = new Node(Move.PSYCHIC);
			movebank[39] = new Node(Move.RECOVER);
			movebank[44] = new Node(Move.HYDRO_PUMP);
			movebank[49] = new Node(Move.LIGHT_SCREEN);
			break;
		case 140:
			movebank = new Node[50];
			movebank[0] = new Node(Move.FLASH);
			movebank[0].addToEnd(new Node(Move.FORESIGHT));
			movebank[0].addToEnd(new Node(Move.LUSTER_PURGE));
			movebank[0].addToEnd(new Node(Move.MOONLIGHT));
			movebank[0].addToEnd(new Node(Move.SHOOTING_STARS));
			movebank[0].addToEnd(new Node(Move.SWIFT));
			movebank[0].addToEnd(new Node(Move.TRI$ATTACK));
			movebank[0].addToEnd(new Node(Move.MAGIC_CRASH));
			movebank[0].addToEnd(new Node(Move.COSMIC_POWER));
			movebank[0].addToEnd(new Node(Move.GRAVITY));
			movebank[0].addToEnd(new Node(Move.STAR_STORM));
			movebank[0].addToEnd(new Node(Move.SPARKLY_SWIRL));
			movebank[0].addToEnd(new Node(Move.LIGHT_SCREEN));
			movebank[0].addToEnd(new Node(Move.PSYCHIC));
			movebank[0].addToEnd(new Node(Move.MOONBLAST));
			movebank[0].addToEnd(new Node(Move.HYDRO_PUMP));
			movebank[0].addToEnd(new Node(Move.RECOVER));
			movebank[44] = new Node(Move.CORE_ENFORCER);
			movebank[49] = new Node(Move.ICE_BEAM);
			break;
		case 141:
			movebank = new Node[45];
			movebank[0] = new Node(Move.BITE);
			movebank[0].next = new Node(Move.AQUA_JET);
			movebank[2] = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SCARY_FACE);
			movebank[9] = new Node(Move.SKULL_BASH);
			movebank[13] = new Node(Move.JAW_LOCK);
			movebank[16] = new Node(Move.THIEF);
			movebank[20] = new Node(Move.BEAT_UP);
			movebank[24] = new Node(Move.CRUNCH);
			movebank[27] = new Node(Move.LIQUIDATION);
			movebank[31] = new Node(Move.ICE_FANG);
			movebank[31].next = new Node(Move.FIRE_FANG);
			movebank[31].next.next = new Node(Move.THUNDER_FANG);
			movebank[39] = new Node(Move.PSYCHIC_FANGS);
			movebank[44] = new Node(Move.KNOCK_OFF);
			break;
		case 142:
			movebank = new Node[75];
			movebank[0] = new Node(Move.EXTREME_SPEED);
			movebank[0].next = new Node(Move.DRAGON_PULSE);
			movebank[0].next.next = new Node(Move.BITE);
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
			movebank[44] = new Node(Move.KNOCK_OFF);
			movebank[49] = new Node(Move.ACROBATICS);
			movebank[54] = new Node(Move.CRUNCH);
			movebank[59] = new Node(Move.DRAGON_RUSH);
			movebank[64] = new Node(Move.BRAVE_BIRD);
			movebank[69] = new Node(Move.UNSEEN_STRANGLE);
			movebank[74] = new Node(Move.OUTRAGE);
			break;
		case 143:
			movebank = new Node[35];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].addToEnd(new Node(Move.MAGIC_REFLECT));
			movebank[0].addToEnd(new Node(Move.SPARKLING_TERRAIN));
			movebank[0].addToEnd(new Node(Move.TWISTER));
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.MAGIC_BLAST);
			movebank[11] = new Node(Move.TOPSY$TURVY);
			movebank[15] = new Node(Move.SWITCHEROO);
			movebank[19] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[22] = new Node(Move.DRAGON_PULSE);
			movebank[24] = new Node(Move.MAGIC_CRASH);
			movebank[34] = new Node(Move.VANISHING_ACT);
			break;
		case 144:
			movebank = new Node[58];
			movebank[0] = new Node(Move.BITE);
			movebank[2] = new Node(Move.DRAGON_DANCE);
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
			movebank = new Node[80];
			movebank[0] = new Node(Move.OUTRAGE);
			movebank[0].addToEnd(new Node(Move.HYPER_BEAM));
			movebank[0].addToEnd(new Node(Move.TWISTER));
			movebank[0].addToEnd(new Node(Move.SWITCHEROO));
			movebank[4] = new Node(Move.DRAGON_PULSE);
			movebank[5] = new Node(Move.BITE);
			movebank[8] = new Node(Move.DRAGON_BREATH);
			movebank[10] = new Node(Move.ICE_BEAM);
			movebank[13] = new Node(Move.TRI$ATTACK);
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
			movebank[79] = new Node(Move.DRAGON_DANCE);
			break;
		case 146:
			movebank = new Node[38];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].addToEnd(new Node(Move.MUD$SLAP));
			movebank[2] = new Node(Move.WATER_GUN);
			movebank[4] = new Node(Move.WITHDRAW);
			movebank[7] = new Node(Move.FURY_CUTTER);
			movebank[10] = new Node(Move.SPIKE_CANNON);
			movebank[13] = new Node(Move.ANCIENT_POWER);
			movebank[16] = new Node(Move.THIEF);
			movebank[19] = new Node(Move.FAKE_OUT);
			movebank[21] = new Node(Move.FURY_SWIPES);
			movebank[24] = new Node(Move.RAZOR_SHELL);
			movebank[26] = new Node(Move.ROCK_POLISH);
			movebank[28] = new Node(Move.SLASH);
			movebank[31] = new Node(Move.HONE_CLAWS);
			movebank[34] = new Node(Move.SHELL_SMASH);
			movebank[37] = new Node(Move.NIGHT_SLASH);
			break;
		case 147:
			movebank = new Node[65];
			movebank[0] = new Node(Move.SWITCHEROO);
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[0].addToEnd(new Node(Move.SCRATCH));
			movebank[0].addToEnd(new Node(Move.MUD$SLAP));
			movebank[2] = new Node(Move.WATER_GUN);
			movebank[4] = new Node(Move.WITHDRAW);
			movebank[7] = new Node(Move.FURY_CUTTER);
			movebank[10] = new Node(Move.SPIKE_CANNON);
			movebank[13] = new Node(Move.ANCIENT_POWER);
			movebank[16] = new Node(Move.THIEF);
			movebank[19] = new Node(Move.FAKE_OUT);
			movebank[21] = new Node(Move.FURY_SWIPES);
			movebank[24] = new Node(Move.RAZOR_SHELL);
			movebank[26] = new Node(Move.ROCK_POLISH);
			movebank[28] = new Node(Move.SLASH);
			movebank[31] = new Node(Move.HONE_CLAWS);
			movebank[34] = new Node(Move.SHELL_SMASH);
			movebank[37] = new Node(Move.NIGHT_SLASH);
			movebank[40] = new Node(Move.ROCK_SLIDE);
			movebank[43] = new Node(Move.LIQUIDATION);
			movebank[46] = new Node(Move.CROSS_CHOP);
			movebank[49] = new Node(Move.SWORDS_DANCE);
			movebank[53] = new Node(Move.STONE_EDGE);
			movebank[59] = new Node(Move.WAVE_CRASH);
			movebank[64] = new Node(Move.FOCUS_ENERGY);
			break;
		case 148:
			movebank = new Node[50];
			movebank[0] = new Node(Move.MUD_SPORT);
			movebank[0].next = new Node(Move.FLASH);
			movebank[0].addToEnd(new Node(Move.TAIL_WHIP));
			movebank[5] = new Node(Move.MUD$SLAP);
			movebank[8] = new Node(Move.BRINE);
			movebank[11] = new Node(Move.MUD_SHOT);
			movebank[14] = new Node(Move.FLASH_RAY);
			movebank[18] = new Node(Move.MUD_BOMB);
			movebank[21] = new Node(Move.THIEF);
			movebank[24] = new Node(Move.LIFE_DEW);
			movebank[27] = new Node(Move.MUD_SPORT);
			movebank[27].next = new Node(Move.WATER_SPORT);
			movebank[30] = new Node(Move.CHARM);
			movebank[33] = new Node(Move.SWITCHEROO);
			movebank[35] = new Node(Move.LUSTER_PURGE);
			movebank[38] = new Node(Move.MUDDY_WATER);
			movebank[41] = new Node(Move.DARK_PULSE);
			movebank[44] = new Node(Move.EARTH_POWER);
			movebank[49] = new Node(Move.TOPSY$TURVY);
			break;
		case 149:
			movebank = new Node[70];
			movebank[0] = new Node(Move.MUD_SPORT);
			movebank[0].addToEnd(new Node(Move.BRINE));
			movebank[0].addToEnd(new Node(Move.MUD_SHOT));
			movebank[0].addToEnd(new Node(Move.MUDDY_WATER));
			movebank[0].addToEnd(new Node(Move.MAGIC_FANG));
			movebank[0].addToEnd(new Node(Move.FEINT_ATTACK));
			movebank[4] = new Node(Move.NIGHT_SLASH);
			movebank[9] = new Node(Move.HYPER_FANG);
			movebank[14] = new Node(Move.TAKE_DOWN);
			movebank[19] = new Node(Move.BRUTAL_SWING);
			movebank[24] = new Node(Move.LIQUIDATION);
			movebank[29] = new Node(Move.KNOCK_OFF);
			movebank[34] = new Node(Move.DRILL_RUN);
			movebank[36] = new Node(Move.AQUA_JET);
			movebank[39] = new Node(Move.PLAY_ROUGH);
			movebank[41] = new Node(Move.SUCKER_PUNCH);
			movebank[44] = new Node(Move.EARTHQUAKE);
			movebank[47] = new Node(Move.DARKEST_LARIAT);
			movebank[49] = new Node(Move.WAVE_CRASH);
			movebank[52] = new Node(Move.MAGIC_CRASH);
			movebank[54] = new Node(Move.PHANTOM_FORCE);
			movebank[59] = new Node(Move.TWINKLE_TACKLE);
			movebank[64] = new Node(Move.UNSEEN_STRANGLE);
			movebank[69] = new Node(Move.PARTING_SHOT);
			break;
		case 150:
			movebank = new Node[65];
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
			movebank[45] = new Node(Move.SEA_DRAGON);
			movebank[47] = new Node(Move.WATER_CLAP);
			movebank[51] = new Node(Move.WATER_KICK);
			movebank[55] = new Node(Move.SUPERCHARGED_SPLASH);
			movebank[59] = new Node(Move.DEEP_SEA_BUBBLE);
			movebank[64] = new Node(Move.SEA_DRAGON);
			break;
		case 151:
			movebank = new Node[25];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.POISON_STING);
			movebank[6] = new Node(Move.BITE);
			movebank[9] = new Node(Move.ACID);
			movebank[11] = new Node(Move.GLARE);
			movebank[14] = new Node(Move.MUD_BOMB);
			movebank[16] = new Node(Move.SCREECH);
			movebank[19] = new Node(Move.POISON_FANG);
			movebank[21] = new Node(Move.STOCKPILE);
			break;
		case 152:
			movebank = new Node[55];
			movebank[0] = new Node(Move.ICE_FANG);
			movebank[0].addToEnd(new Node(Move.THUNDER_FANG));
			movebank[0].addToEnd(new Node(Move.FIRE_FANG));
			movebank[0].addToEnd(new Node(Move.MAGIC_FANG));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[0].addToEnd(new Node(Move.WRAP));
			movebank[0].addToEnd(new Node(Move.LEER));
			movebank[0].addToEnd(new Node(Move.POISON_STING));
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[0].addToEnd(new Node(Move.GLARE));
			movebank[0].addToEnd(new Node(Move.SCREECH));
			movebank[19] = new Node(Move.POISON_FANG);
			movebank[21] = new Node(Move.STOCKPILE);
			movebank[24] = new Node(Move.ACID_SPRAY);
			movebank[26] = new Node(Move.GASTRO_ACID);
			movebank[28] = new Node(Move.THIEF);
			movebank[30] = new Node(Move.POISON_TAIL);
			movebank[33] = new Node(Move.DRAGON_TAIL);
			movebank[35] = new Node(Move.HAZE);
			movebank[39] = new Node(Move.COIL);
			movebank[44] = new Node(Move.SUCKER_PUNCH);
			movebank[49] = new Node(Move.VENOM_SPIT);
			movebank[54] = new Node(Move.GUNK_SHOT);
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
			movebank = new Node[62];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			movebank[26] = new Node(Move.THIEF);
			movebank[29] = new Node(Move.FIRE_FANG);
			movebank[29].addToEnd(new Node(Move.THUNDER_FANG));
			movebank[29].addToEnd(new Node(Move.ICE_FANG));
			movebank[29].addToEnd(new Node(Move.MAGIC_FANG));
			movebank[33] = new Node(Move.CRUNCH);
			movebank[36] = new Node(Move.CONFUSE_RAY);
			movebank[40] = new Node(Move.HAZE);
			movebank[44] = new Node(Move.FLY);
			movebank[47] = new Node(Move.VENOSHOCK);
			movebank[54] = new Node(Move.LEECH_LIFE);
			movebank[61] = new Node(Move.BRAVE_BIRD);
			break;
		case 155:
			movebank = new Node[62];
			movebank[0] = new Node(Move.CROSS_POISON);
			movebank[0].addToEnd(new Node(Move.TAILWIND));
			movebank[0].addToEnd(new Node(Move.TOXIC));
			movebank[0].addToEnd(new Node(Move.SCREECH));
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.WING_ATTACK);
			movebank[26] = new Node(Move.THIEF);
			movebank[29] = new Node(Move.FIRE_FANG);
			movebank[29].addToEnd(new Node(Move.THUNDER_FANG));
			movebank[29].addToEnd(new Node(Move.ICE_FANG));
			movebank[29].addToEnd(new Node(Move.MAGIC_FANG));
			movebank[33] = new Node(Move.CRUNCH);
			movebank[36] = new Node(Move.CONFUSE_RAY);
			movebank[39] = new Node(Move.CROSS_POISON);
			movebank[43] = new Node(Move.HAZE);
			movebank[47] = new Node(Move.VENOSHOCK);
			movebank[50] = new Node(Move.FLY);
			movebank[54] = new Node(Move.LEECH_LIFE);
			movebank[61] = new Node(Move.BRAVE_BIRD);
			break;
		case 156:
			movebank = new Node[30];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[6] = new Node(Move.SCARY_FACE);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.PLAY_NICE);
			movebank[18] = new Node(Move.MOONLIGHT);
			movebank[22] = new Node(Move.HEX);
			movebank[24] = new Node(Move.BABY$DOLL_EYES);
			movebank[27] = new Node(Move.LUSTER_PURGE);
			movebank[29] = new Node(Move.SHADOW_BALL);
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
			movebank[24] = new Node(Move.BABY$DOLL_EYES);
			movebank[27] = new Node(Move.LUSTER_PURGE);
			movebank[29] = new Node(Move.SHADOW_BALL);
			movebank[31] = new Node(Move.IRON_DEFENSE);
			movebank[33] = new Node(Move.BULLET_PUNCH);
			movebank[36] = new Node(Move.DREAM_EATER);
			movebank[39] = new Node(Move.SHADOW_SNEAK);
			movebank[42] = new Node(Move.TRI$ATTACK);
			movebank[45] = new Node(Move.GYRO_BALL);
			movebank[49] = new Node(Move.DESTINY_BOND);
			movebank[54] = new Node(Move.SPECTRAL_THIEF);
			break;
		case 158:
			movebank = new Node[27];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THIEF);
			movebank[6] = new Node(Move.LICK);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.FAKE_TEARS);
			movebank[18] = new Node(Move.NASTY_PLOT);
			movebank[22] = new Node(Move.HEX);
			movebank[24] = new Node(Move.SUCKER_PUNCH);
			movebank[26] = new Node(Move.FEINT_ATTACK);
			break;
		case 159:
			movebank = new Node[55];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THIEF);
			movebank[6] = new Node(Move.LICK);
			movebank[8] = new Node(Move.MEAN_LOOK);
			movebank[10] = new Node(Move.HYPNOSIS);
			movebank[13] = new Node(Move.FAKE_TEARS);
			movebank[18] = new Node(Move.NASTY_PLOT);
			movebank[22] = new Node(Move.HEX);
			movebank[24] = new Node(Move.SUCKER_PUNCH);
			movebank[26] = new Node(Move.FEINT_ATTACK);
			movebank[29] = new Node(Move.SHADOW_PUNCH);
			movebank[31] = new Node(Move.MINIMIZE);
			movebank[33] = new Node(Move.CURSE);
			movebank[36] = new Node(Move.NIGHT_SLASH);
			movebank[39] = new Node(Move.SHADOW_BALL);
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
			movebank[12] = new Node(Move.WILL$O$WISP);
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
			movebank[0].addToEnd(new Node(Move.WILL$O$WISP));
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
			movebank[26] = new Node(Move.MACH_PUNCH);
			movebank[29] = new Node(Move.SCARY_FACE);
			movebank[33] = new Node(Move.DYNAMIC_PUNCH);
			movebank[37] = new Node(Move.KNOCK_OFF);
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
			movebank[26] = new Node(Move.MACH_PUNCH);
			movebank[29] = new Node(Move.SCARY_FACE);
			movebank[33] = new Node(Move.DYNAMIC_PUNCH);
			movebank[37] = new Node(Move.KNOCK_OFF);
			movebank[41] = new Node(Move.HAMMER_ARM);
			movebank[47] = new Node(Move.STONE_EDGE);
			movebank[53] = new Node(Move.SUPERPOWER);
			movebank[59] = new Node(Move.REVERSAL);
			break;
		case 166:
			movebank = new Node[28];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			movebank[18] = new Node(Move.RAPID_SPIN);
			movebank[22] = new Node(Move.STOMP);
			movebank[27] = new Node(Move.STEALTH_ROCK);
			break;
		case 167:
			movebank = new Node[55];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			movebank[18] = new Node(Move.RAPID_SPIN);
			movebank[22] = new Node(Move.STOMP);
			movebank[27] = new Node(Move.STEALTH_ROCK);
			movebank[29] = new Node(Move.BULLDOZE);
			movebank[32] = new Node(Move.SPIKES);
			movebank[35] = new Node(Move.HORN_DRILL);
			movebank[38] = new Node(Move.MAGNITUDE);
			movebank[41] = new Node(Move.STONE_EDGE);
			movebank[45] = new Node(Move.BODY_PRESS);
			movebank[49] = new Node(Move.ENDEAVOR);
			movebank[54] = new Node(Move.DOUBLE$EDGE);
			break;
		case 168:
			movebank = new Node[75];
			movebank[0] = new Node(Move.HORN_ATTACK);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[4] = new Node(Move.GROWL);
			movebank[8] = new Node(Move.TAIL_WHIP);
			movebank[14] = new Node(Move.FURY_ATTACK);
			movebank[18] = new Node(Move.RAPID_SPIN);
			movebank[22] = new Node(Move.STOMP);
			movebank[27] = new Node(Move.STEALTH_ROCK);
			movebank[29] = new Node(Move.BULLDOZE);
			movebank[32] = new Node(Move.SPIKES);
			movebank[35] = new Node(Move.HORN_DRILL);
			movebank[38] = new Node(Move.MAGNITUDE);
			movebank[41] = new Node(Move.STONE_EDGE);
			movebank[45] = new Node(Move.BODY_PRESS);
			movebank[49] = new Node(Move.ENDEAVOR);
			movebank[54] = new Node(Move.DOUBLE$EDGE);
			movebank[54].addToEnd(new Node(Move.EARTHQUAKE));
			movebank[54].addToEnd(new Node(Move.ROCK_BLAST));
			movebank[57] = new Node(Move.AURA_SPHERE);
			movebank[59] = new Node(Move.SUBMISSION);
			movebank[61] = new Node(Move.HAMMER_ARM);
			movebank[63] = new Node(Move.SCORCHING_SANDS);
			movebank[65] = new Node(Move.HEAT_CRASH);
			movebank[69] = new Node(Move.SUPERPOWER);
			movebank[74] = new Node(Move.HEAVY_SLAM);
			break;
		case 169:
			movebank = new Node[30];
			movebank[0] = new Node(Move.DIG);
			movebank[0].next = new Node(Move.LEER);
			movebank[4] = new Node(Move.THIEF);
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
			movebank[4] = new Node(Move.THIEF);
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
			movebank[25] = new Node(Move.IRON_DEFENSE);
			movebank[29] = new Node(Move.IRON_TAIL);
			movebank[32] = new Node(Move.SPIKES);
			movebank[38] = new Node(Move.EARTHQUAKE);
			movebank[42] = new Node(Move.IRON_HEAD);
			break;
		case 173:
			movebank = new Node[70];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[6] = new Node(Move.SAND_ATTACK);
			movebank[12] = new Node(Move.WRAP);
			movebank[16] = new Node(Move.MUD_SHOT);
			movebank[19] = new Node(Move.DIG);
			movebank[25] = new Node(Move.IRON_DEFENSE);
			movebank[29] = new Node(Move.IRON_TAIL);
			movebank[32] = new Node(Move.SPIKES);
			movebank[38] = new Node(Move.EARTHQUAKE);
			movebank[42] = new Node(Move.IRON_HEAD);
			movebank[47] = new Node(Move.FISSURE);
			movebank[52] = new Node(Move.DRAGON_PULSE);
			movebank[56] = new Node(Move.EARTH_POWER);
			movebank[59] = new Node(Move.DRAGON_DANCE);
			movebank[64] = new Node(Move.FATAL_BIND);
			movebank[69] = new Node(Move.DRACO_METEOR);
			break;
		case 174:
			movebank = new Node[16];
			movebank[0] = new Node(Move.SPLASH);
			movebank[0].next = new Node(Move.POUND);
			movebank[0].next = new Node(Move.FLASH);
			movebank[2] = new Node(Move.LOVELY_KISS);
			movebank[4] = new Node(Move.SWEET_KISS);
			movebank[7] = new Node(Move.FLASH_RAY);
			movebank[11] = new Node(Move.ENCORE);
			movebank[15] = new Node(Move.CHARM);
			break;
		case 175:
			movebank = new Node[51];
			movebank[0] = new Node(Move.LOVELY_KISS);
			movebank[0].addToEnd(new Node(Move.SWEET_KISS));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[0].addToEnd(new Node(Move.FLASH_RAY));
			movebank[0].addToEnd(new Node(Move.ENCORE));
			movebank[0].addToEnd(new Node(Move.COMET_PUNCH));
			movebank[0].addToEnd(new Node(Move.CHARM));
			movebank[0].addToEnd(new Node(Move.POUND));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[2] = new Node(Move.WISH);
			movebank[5] = new Node(Move.BABY$DOLL_EYES);
			movebank[8] = new Node(Move.SPACE_BEAM);
			movebank[11] = new Node(Move.MINIMIZE);
			movebank[14] = new Node(Move.SWIFT);
			movebank[17] = new Node(Move.METRONOME);
			movebank[20] = new Node(Move.MOONLIGHT);
			movebank[23] = new Node(Move.GRAVITY);
			movebank[26] = new Node(Move.RADIO_BURST);
			movebank[29] = new Node(Move.COSMIC_POWER);
			movebank[32] = new Node(Move.STORED_POWER);
			movebank[35] = new Node(Move.MOONBLAST);
			movebank[38] = new Node(Move.METEOR_MASH);
			movebank[41] = new Node(Move.STEALTH_ROCK);
			movebank[44] = new Node(Move.CORE_ENFORCER);
			movebank[47] = new Node(Move.SPACIAL_REND);
			movebank[50] = new Node(Move.HEALING_WISH);
			break;
		case 176:
			movebank = new Node[1];
			movebank[0] = new Node(Move.LOVELY_KISS);
			movebank[0].addToEnd(new Node(Move.SWEET_KISS));
			movebank[0].addToEnd(new Node(Move.FLASH_RAY));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[0].addToEnd(new Node(Move.ENCORE));
			movebank[0].addToEnd(new Node(Move.COMET_PUNCH));
			movebank[0].addToEnd(new Node(Move.CHARM));
			movebank[0].addToEnd(new Node(Move.WISH));
			movebank[0].addToEnd(new Node(Move.BABY$DOLL_EYES));
			movebank[0].addToEnd(new Node(Move.MINIMIZE));
			movebank[0].addToEnd(new Node(Move.SWIFT));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.LIFE_DEW));
			movebank[0].addToEnd(new Node(Move.METRONOME));
			movebank[0].addToEnd(new Node(Move.MOONLIGHT));
			movebank[0].addToEnd(new Node(Move.GRAVITY));
			movebank[0].addToEnd(new Node(Move.METEOR_MASH));
			movebank[0].addToEnd(new Node(Move.STEALTH_ROCK));
			movebank[0].addToEnd(new Node(Move.STORED_POWER));
			movebank[0].addToEnd(new Node(Move.HEALING_WISH));
			movebank[0].addToEnd(new Node(Move.POUND));
			movebank[0].addToEnd(new Node(Move.FLASH));
			movebank[0].addToEnd(new Node(Move.GROWL));
			movebank[0].addToEnd(new Node(Move.DEFENSE_CURL));
			movebank[0].addToEnd(new Node(Move.SPACIAL_REND));
			movebank[0].addToEnd(new Node(Move.RADIO_BURST));
			movebank[0].addToEnd(new Node(Move.COSMIC_POWER));
			movebank[0].addToEnd(new Node(Move.MOONBLAST));
			break;
		case 177:
			movebank = new Node[55];
			movebank[0] = new Node(Move.BABY$DOLL_EYES);
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
			movebank[49] = new Node(Move.TRI$ATTACK);
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
			movebank[53] = new Node(Move.TRI$ATTACK);
			movebank[59] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[64] = new Node(Move.MOONBLAST);
			break;
		case 179:
			movebank = new Node[28];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[0].next = new Node(Move.LEER);
			movebank[3] = new Node(Move.TORMENT);
			movebank[7] = new Node(Move.HONE_CLAWS);
			movebank[11] = new Node(Move.SHADOW_SNEAK);
			movebank[14] = new Node(Move.CURSE);
			movebank[19] = new Node(Move.TAUNT);
			movebank[22] = new Node(Move.ROUND);
			movebank[24] = new Node(Move.HEX);
			movebank[27] = new Node(Move.CONFUSE_RAY);
			break;
		case 180:
			movebank = new Node[70];
			movebank[0] = new Node(Move.THIEF);
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[0].addToEnd(new Node(Move.SCRATCH));
			movebank[0].addToEnd(new Node(Move.LEER));
			movebank[3] = new Node(Move.TORMENT);
			movebank[7] = new Node(Move.HONE_CLAWS);
			movebank[11] = new Node(Move.SHADOW_SNEAK);
			movebank[14] = new Node(Move.CURSE);
			movebank[19] = new Node(Move.TAUNT);
			movebank[22] = new Node(Move.ROUND);
			movebank[24] = new Node(Move.HEX);
			movebank[27] = new Node(Move.CONFUSE_RAY);
			movebank[29] = new Node(Move.SHADOW_CLAW);
			movebank[31] = new Node(Move.AGILITY);
			movebank[33] = new Node(Move.SPIRIT_BREAK);
			movebank[37] = new Node(Move.SHADOW_BALL);
			movebank[41] = new Node(Move.KNOCK_OFF);
			movebank[45] = new Node(Move.BITTER_MALICE);
			movebank[51] = new Node(Move.HYPER_VOICE);
			movebank[57] = new Node(Move.FOUL_PLAY);
			movebank[62] = new Node(Move.NASTY_PLOT);
			movebank[69] = new Node(Move.MAGIC_TOMB);
			break;
		case 181:
			movebank = new Node[25];
			movebank[0] = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK$ON);
			break;
		case 182:
			movebank = new Node[50];
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].next = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK$ON);
			movebank[24].next = new Node(Move.IRON_DEFENSE);
			movebank[29] = new Node(Move.REBOOT);
			movebank[34] = new Node(Move.BULLET_PUNCH);
			movebank[39] = new Node(Move.IRON_HEAD);
			movebank[44] = new Node(Move.METAL_SOUND);
			movebank[49] = new Node(Move.METEOR_MASH);
			break;
		case 183:
			movebank = new Node[70];
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].next = new Node(Move.FOCUS_ENERGY);
			movebank[0].next.next = new Node(Move.SCRATCH);
			movebank[4] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.POP_POP);
			movebank[14] = new Node(Move.HEADBUTT);
			movebank[19] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.LOCK$ON);
			movebank[24].next = new Node(Move.IRON_DEFENSE);
			movebank[29] = new Node(Move.REBOOT);
			movebank[34] = new Node(Move.BULLET_PUNCH);
			movebank[39] = new Node(Move.IRON_HEAD);
			movebank[44] = new Node(Move.METAL_SOUND);
			movebank[49] = new Node(Move.METEOR_MASH);
			movebank[49].addToEnd(new Node(Move.PISTOL_POP));
			movebank[49].addToEnd(new Node(Move.LOAD_FIREARMS));
			movebank[51] = new Node(Move.FLAMETHROWER);
			movebank[53] = new Node(Move.IRON_BLAST);
			movebank[55] = new Node(Move.HYDRO_PUMP);
			movebank[57] = new Node(Move.MAGIC_BLAST);
			movebank[59] = new Node(Move.SHIFT_GEAR);
			movebank[64] = new Node(Move.PRISMATIC_LASER);
			movebank[69] = new Node(Move.STEEL_BEAM);
			break;
		case 184:
			movebank = new Node[23];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.SHOOTING_STARS);
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.SPARKLY_SWIRL);
			movebank[22] = new Node(Move.SPACE_BEAM);
			break;
		case 185:
			movebank = new Node[48];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.SHOOTING_STARS);
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.SPARKLY_SWIRL);
			movebank[22] = new Node(Move.SPACE_BEAM);
			movebank[26] = new Node(Move.ANCIENT_POWER);
			movebank[29] = new Node(Move.MAGIC_BLAST);
			movebank[31] = new Node(Move.SPARKLE_STRIKE);
			movebank[33] = new Node(Move.COMET_PUNCH);
			movebank[36] = new Node(Move.MOONLIGHT);
			movebank[39] = new Node(Move.MAGIC_REFLECT);
			movebank[41] = new Node(Move.DESOLATE_VOID);
			movebank[44] = new Node(Move.MAGIC_TOMB);
			movebank[47] = new Node(Move.GALAXY_BLAST);
			break;
		case 186:
			movebank = new Node[75];
			movebank[0] = new Node(Move.MAGIC_POWDER);
			movebank[0].next = new Node(Move.SHOOTING_STARS);
			movebank[4] = new Node(Move.SWIFT);
			movebank[9] = new Node(Move.GLITTERING_TORNADO);
			movebank[12] = new Node(Move.TAILWIND);
			movebank[14] = new Node(Move.FLASH);
			movebank[17] = new Node(Move.SPARKLY_SWIRL);
			movebank[22] = new Node(Move.SPACE_BEAM);
			movebank[26] = new Node(Move.ANCIENT_POWER);
			movebank[29] = new Node(Move.MAGIC_BLAST);
			movebank[31] = new Node(Move.SPARKLE_STRIKE);
			movebank[33] = new Node(Move.COMET_PUNCH);
			movebank[36] = new Node(Move.MOONLIGHT);
			movebank[39] = new Node(Move.MAGIC_REFLECT);
			movebank[41] = new Node(Move.TAKE_DOWN);
			movebank[44] = new Node(Move.DESOLATE_VOID);
			movebank[47] = new Node(Move.MAGIC_TOMB);
			movebank[49] = new Node(Move.MAGIC_CRASH);
			movebank[53] = new Node(Move.GALAXY_BLAST);
			movebank[56] = new Node(Move.SPACIAL_REND);
			movebank[59] = new Node(Move.COMET_CRASH);
			movebank[63] = new Node(Move.METEOR_ASSAULT);
			movebank[67] = new Node(Move.RADIO_BURST);
			movebank[71] = new Node(Move.VANISHING_ACT);
			movebank[74] = new Node(Move.SUPERNOVA_EXPLOSION);
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
			movebank[23] = new Node(Move.GLITTER_DANCE);
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
			movebank[23] = new Node(Move.GLITTER_DANCE);
			movebank[25] = new Node(Move.TWISTER);
			movebank[29] = new Node(Move.DRAGON_BREATH);
			movebank[32] = new Node(Move.LUSTER_PURGE);
			movebank[35] = new Node(Move.DRAGON_TAIL);
			movebank[38] = new Node(Move.GLITZY_GLOW);
			movebank[41] = new Node(Move.SCALE_SHOT);
			movebank[45] = new Node(Move.DRAGON_PULSE);
			movebank[49] = new Node(Move.DRAGON_RUSH);
			movebank[52] = new Node(Move.HYDRO_PUMP);
			break;
		case 189:
			movebank = new Node[75];
			movebank[0] = new Node(Move.FIERY_DANCE);
			movebank[0].next = new Node(Move.WRAP);
			movebank[0].next.next = new Node(Move.LEER);
			movebank[4] = new Node(Move.THUNDER_WAVE);
			movebank[7] = new Node(Move.THUNDERSHOCK);
			movebank[12] = new Node(Move.DRAGON_RAGE);
			movebank[15] = new Node(Move.GLITTERING_TORNADO);
			movebank[18] = new Node(Move.LIGHT_BEAM);
			movebank[21] = new Node(Move.WATER_PULSE);
			movebank[23] = new Node(Move.GLITTER_DANCE);
			movebank[25] = new Node(Move.TWISTER);
			movebank[29] = new Node(Move.DRAGON_BREATH);
			movebank[32] = new Node(Move.LUSTER_PURGE);
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
			movebank[14] = new Node(Move.RADIO_BURST);
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY$TURVY);
			break;
		case 191:
			movebank = new Node[60];
			movebank[0] = new Node(Move.COSMIC_POWER);
			movebank[0].addToEnd(new Node(Move.SPLASH));
			movebank[0].addToEnd(new Node(Move.SPACE_BEAM));
			movebank[0].addToEnd(new Node(Move.VACUUM_WAVE));
			movebank[14] = new Node(Move.RADIO_BURST);
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY$TURVY);
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
			movebank[14] = new Node(Move.RADIO_BURST);
			movebank[24] = new Node(Move.MIST_BALL);
			movebank[24].next = new Node(Move.TOPSY$TURVY);
			movebank[39] = new Node(Move.COMET_PUNCH);
			movebank[43] = new Node(Move.BULK_UP);
			movebank[47] = new Node(Move.DESOLATE_VOID);
			movebank[51] = new Node(Move.AURA_SPHERE);
			movebank[55] = new Node(Move.METEOR_MASH);
			movebank[59] = new Node(Move.DRAIN_PUNCH);
			movebank[59].next = new Node(Move.STAR$STRUCK_ARROW);
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
			movebank[29] = new Node(Move.FREEZE$DRY);
			movebank[44] = new Node(Move.SHEER_COLD);
			break;
		case 194:
			movebank = new Node[55];
			movebank[0] = new Node(Move.COMET_CRASH);
			movebank[0].addToEnd(new Node(Move.BRAVE_BIRD));
			movebank[0].addToEnd(new Node(Move.HEAD_SMASH));
			movebank[0].addToEnd(new Node(Move.TAKE_DOWN));
			movebank[0].addToEnd(new Node(Move.ICICLE_SPEAR));
			movebank[0].addToEnd(new Node(Move.SHOOTING_STARS));
			movebank[9] = new Node(Move.COMET_PUNCH);
			movebank[11] = new Node(Move.MACH_PUNCH);
			movebank[14] = new Node(Move.ICE_PUNCH);
			movebank[14].next = new Node(Move.FIRE_PUNCH);
			movebank[14].next.next = new Node(Move.THUNDER_PUNCH);
			movebank[18] = new Node(Move.BLIZZARD);
			movebank[24] = new Node(Move.ICE_BALL);
			movebank[29] = new Node(Move.DOUBLE$EDGE);
			movebank[39] = new Node(Move.ICE_SPINNER);
			movebank[44] = new Node(Move.COMET_CRASH);
			movebank[49] = new Node(Move.ICICLE_CRASH);
			movebank[54] = new Node(Move.METEOR_ASSAULT);
			break;
		case 195:
			movebank = new Node[55];
			movebank[0] = new Node(Move.WAKE$UP_SLAP);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[2] = new Node(Move.ANCIENT_POWER);
			movebank[5] = new Node(Move.ROLLOUT);
			movebank[7] = new Node(Move.FORCE_PALM);
			movebank[9] = new Node(Move.SMACK_DOWN);
			movebank[11] = new Node(Move.COMET_PUNCH);
			movebank[14] = new Node(Move.GRAVITY);
			movebank[18] = new Node(Move.ROCK_BLAST);
			movebank[21] = new Node(Move.SANDSTORM);
			movebank[23] = new Node(Move.DESOLATE_VOID);
			movebank[26] = new Node(Move.SMACK_DOWN);
			movebank[29] = new Node(Move.GRAVITY);
			movebank[33] = new Node(Move.ROCK_SLIDE);
			movebank[39] = new Node(Move.METEOR_MASH);
			movebank[42] = new Node(Move.COSMIC_POWER);
			movebank[45] = new Node(Move.SHOOTING_STARS);
			movebank[49] = new Node(Move.POWER_GEM);
			movebank[54] = new Node(Move.STONE_EDGE);
			break;
		case 196:
			movebank = new Node[75];
			movebank[0] = new Node(Move.WAKE$UP_SLAP);
			movebank[0].next = new Node(Move.DEFENSE_CURL);
			movebank[2] = new Node(Move.ANCIENT_POWER);
			movebank[5] = new Node(Move.ROLLOUT);
			movebank[7] = new Node(Move.FORCE_PALM);
			movebank[9] = new Node(Move.SMACK_DOWN);
			movebank[11] = new Node(Move.COMET_PUNCH);
			movebank[14] = new Node(Move.GRAVITY);
			movebank[18] = new Node(Move.ROCK_BLAST);
			movebank[21] = new Node(Move.SANDSTORM);
			movebank[23] = new Node(Move.DESOLATE_VOID);
			movebank[26] = new Node(Move.SMACK_DOWN);
			movebank[29] = new Node(Move.GRAVITY);
			movebank[33] = new Node(Move.ROCK_SLIDE);
			movebank[39] = new Node(Move.METEOR_MASH);
			movebank[42] = new Node(Move.SHOOTING_STARS);
			movebank[45] = new Node(Move.COSMIC_POWER);
			movebank[49] = new Node(Move.POWER_GEM);
			movebank[54] = new Node(Move.STONE_EDGE);
			movebank[59] = new Node(Move.COMET_CRASH);
			movebank[64] = new Node(Move.METEOR_ASSAULT);
			movebank[74] = new Node(Move.ROCK_WRECKER);
			break;
		}
		
	}
	
	private void setMoveBank2() {
		switch(this.id) {
		case 197:
			movebank = new Node[31];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THUNDER_WAVE);
			movebank[4] = new Node(Move.CONFUSE_RAY);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[11] = new Node(Move.THUNDERSHOCK);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.WILL$O$WISP);
			movebank[23] = new Node(Move.HEX);
			movebank[24] = new Node(Move.SHADOW_PUNCH);
			movebank[24].next = new Node(Move.THUNDER_PUNCH);
			movebank[27] = new Node(Move.MAGNET_RISE);
			movebank[30] = new Node(Move.PHANTOM_FORCE);
			break;
		case 198:
			movebank = new Node[55];
			movebank[0] = new Node(Move.NIGHT_SHADE);
			movebank[2] = new Node(Move.THUNDER_WAVE);
			movebank[4] = new Node(Move.CONFUSE_RAY);
			movebank[7] = new Node(Move.PLAY_NICE);
			movebank[11] = new Node(Move.THUNDERSHOCK);
			movebank[14] = new Node(Move.HYPNOSIS);
			movebank[18] = new Node(Move.WILL$O$WISP);
			movebank[23] = new Node(Move.HEX);
			movebank[24] = new Node(Move.SHADOW_PUNCH);
			movebank[24].next = new Node(Move.THUNDER_PUNCH);
			movebank[27] = new Node(Move.MAGNET_RISE);
			movebank[30] = new Node(Move.PHANTOM_FORCE);
			movebank[34] = new Node(Move.TRI$ATTACK);
			movebank[39] = new Node(Move.ELECTRO_BALL);
			movebank[43] = new Node(Move.SHADOW_BALL);
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
			movebank[21] = new Node(Move.AUTOMOTIZE);
			break;
		case 200:
			movebank = new Node[47];
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].next = new Node(Move.MAGNET_RISE);
			movebank[2] = new Node(Move.SCREECH);
			movebank[5] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.MACH_PUNCH);
			movebank[11] = new Node(Move.KARATE_CHOP);
			movebank[15] = new Node(Move.NUZZLE);
			movebank[18] = new Node(Move.SPARK);
			movebank[21] = new Node(Move.AUTOMOTIZE);
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
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].next = new Node(Move.FOCUS_ENERGY);
			movebank[0].next.next = new Node(Move.MAGNET_RISE);
			movebank[0].addToEnd(new Node(Move.DYNAMIC_PUNCH));
			movebank[2] = new Node(Move.SCREECH);
			movebank[5] = new Node(Move.PROTECT);
			movebank[9] = new Node(Move.MACH_PUNCH);
			movebank[11] = new Node(Move.KARATE_CHOP);
			movebank[15] = new Node(Move.NUZZLE);
			movebank[18] = new Node(Move.SPARK);
			movebank[21] = new Node(Move.AUTOMOTIZE);
			movebank[24] = new Node(Move.BRICK_BREAK);
			movebank[28] = new Node(Move.COMET_PUNCH);
			movebank[31] = new Node(Move.BULLET_PUNCH);
			movebank[35] = new Node(Move.THUNDER_PUNCH);
			movebank[35].next = new Node(Move.FIRE_PUNCH);
			movebank[35].next.next = new Node(Move.ICE_PUNCH);
			movebank[39] = new Node(Move.DRAIN_PUNCH);
			movebank[43] = new Node(Move.SUCKER_PUNCH);
			movebank[46] = new Node(Move.VOLT_TACKLE);
			movebank[49] = new Node(Move.SHIFT_GEAR);
			movebank[53] = new Node(Move.CLOSE_COMBAT);
			movebank[56] = new Node(Move.METEOR_ASSAULT);
			movebank[59] = new Node(Move.PLASMA_FISTS);
			break;
		case 202:
			movebank = new Node[33];
			movebank[0] = new Node(Move.TACKLE);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[4] = new Node(Move.THUNDERSHOCK);
			movebank[8] = new Node(Move.BODY_SLAM);
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
			movebank[8] = new Node(Move.BODY_SLAM);
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
			movebank[8] = new Node(Move.BODY_SLAM);
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
			movebank[28] = new Node(Move.SELF$DESTRUCT);
			movebank[30] = new Node(Move.DISCHARGE);
			movebank[32] = new Node(Move.MAGNITUDE);
			movebank[35] = new Node(Move.ROCK_BLAST);
			break;
		case 207:
			movebank = new Node[70];
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].addToEnd(new Node(Move.TACKLE));
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.SMACK_DOWN);
			movebank[28] = new Node(Move.SELF$DESTRUCT);
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
			movebank[0] = new Node(Move.KNOCK_OFF);
			movebank[0].addToEnd(new Node(Move.TACKLE));
			movebank[0].addToEnd(new Node(Move.CHARGE));
			movebank[2] = new Node(Move.SPARK);
			movebank[5] = new Node(Move.MAGNET_RISE);
			movebank[8] = new Node(Move.ROCK_POLISH);
			movebank[11] = new Node(Move.ROCK_THROW);
			movebank[15] = new Node(Move.ROCK_TOMB);
			movebank[19] = new Node(Move.THUNDER_PUNCH);
			movebank[22] = new Node(Move.ROLLOUT);
			movebank[25] = new Node(Move.SMACK_DOWN);
			movebank[28] = new Node(Move.PARABOLIC_CHARGE);
			movebank[30] = new Node(Move.DISCHARGE);
			movebank[32] = new Node(Move.GLITZY_GLOW);
			movebank[35] = new Node(Move.PLAY_ROUGH);
			movebank[38] = new Node(Move.THUNDERBOLT);
			movebank[41] = new Node(Move.GLITTER_DANCE);
			movebank[44] = new Node(Move.REFLECT);
			movebank[47] = new Node(Move.SHELL_SMASH);
			movebank[50] = new Node(Move.PRISMATIC_LASER);
			movebank[54] = new Node(Move.COMET_CRASH);
			movebank[58] = new Node(Move.OVERHEAT);
			movebank[61] = new Node(Move.GLITTERING_SWORD);
			movebank[64] = new Node(Move.PLASMA_FISTS);
			movebank[69] = new Node(Move.LIGHT_OF_RUIN);
			break;
		case 209:
			movebank = new Node[32];
			movebank[0] = new Node(Move.SPARK);
			movebank[14] = new Node(Move.TACKLE);
			movebank[31] = new Node(Move.BOLT_STRIKE);
			break;
		case 210:
			movebank = new Node[60];
			movebank[0] = new Node(Move.SPARK);
			movebank[14] = new Node(Move.TACKLE);
			movebank[19] = new Node(Move.THUNDER_FANG);
			movebank[22] = new Node(Move.DRAGON_RAGE);
			movebank[25] = new Node(Move.CRUNCH);
			movebank[30] = new Node(Move.ZING_ZAP);
			movebank[33] = new Node(Move.AQUA_TAIL);
			movebank[36] = new Node(Move.DRAGON_RUSH);
			movebank[39] = new Node(Move.BOUNCE);
			movebank[43] = new Node(Move.VOLT_TACKLE);
			movebank[49] = new Node(Move.COMET_CRASH);
			movebank[54] = new Node(Move.WATERFALL);
			movebank[59] = new Node(Move.DRAGON_DANCE);
			break;
		case 211:
			movebank = new Node[39];
			movebank[0] = new Node(Move.THUNDER_FANG);
			movebank[2] = new Node(Move.COIL);
			movebank[4] = new Node(Move.BITE);
			movebank[8] = new Node(Move.SLAM);
			movebank[11] = new Node(Move.DRAGON_RAGE);
			movebank[14] = new Node(Move.BREAKING_SWIPE);
			movebank[18] = new Node(Move.CRUNCH);
			movebank[21] = new Node(Move.GLARE);
			movebank[25] = new Node(Move.NUZZLE);
			movebank[29] = new Node(Move.FIRE_FANG);
			movebank[32] = new Node(Move.ZING_ZAP);
			movebank[35] = new Node(Move.ICE_FANG);
			movebank[38] = new Node(Move.POISON_FANG);
			break;
		case 212:
			movebank = new Node[60];
			movebank[0] = new Node(Move.THUNDER_FANG);
			movebank[0].addToEnd(new Node(Move.SCALE_SHOT));
			movebank[0].addToEnd(new Node(Move.DRAGON_DANCE));
			movebank[0].addToEnd(new Node(Move.DUAL_CHOP));
			movebank[2] = new Node(Move.COIL);
			movebank[4] = new Node(Move.BITE);
			movebank[8] = new Node(Move.SLAM);
			movebank[11] = new Node(Move.DRAGON_RAGE);
			movebank[14] = new Node(Move.BREAKING_SWIPE);
			movebank[18] = new Node(Move.CRUNCH);
			movebank[21] = new Node(Move.GLARE);
			movebank[25] = new Node(Move.NUZZLE);
			movebank[29] = new Node(Move.FIRE_FANG);
			movebank[32] = new Node(Move.ZING_ZAP);
			movebank[35] = new Node(Move.ICE_FANG);
			movebank[38] = new Node(Move.POISON_FANG);
			movebank[39] = new Node(Move.FATAL_BIND);
			movebank[44] = new Node(Move.DRAGON_RUSH);
			movebank[49] = new Node(Move.STONE_EDGE);
			movebank[54] = new Node(Move.VOLT_TACKLE);
			movebank[59] = new Node(Move.OUTRAGE);
			break;
		case 213:
			movebank = new Node[39];
			movebank[0] = new Node(Move.PAYBACK);
			movebank[2] = new Node(Move.BULK_UP);
			movebank[4] = new Node(Move.BREAKING_SWIPE);
			movebank[8] = new Node(Move.TAKE_DOWN);
			movebank[11] = new Node(Move.DRAGON_RAGE);
			movebank[14] = new Node(Move.SUCKER_PUNCH);
			movebank[17] = new Node(Move.SCARY_FACE);
			movebank[20] = new Node(Move.THIEF);
			movebank[23] = new Node(Move.CRUNCH);
			movebank[25] = new Node(Move.DRAGON_TAIL);
			movebank[29] = new Node(Move.FALSE_SURRENDER);
			movebank[32] = new Node(Move.JAW_LOCK);
			movebank[35] = new Node(Move.ICE_FANG);
			movebank[38] = new Node(Move.KNOCK_OFF);
			break;
		case 214:
			movebank = new Node[60];
			movebank[0] = new Node(Move.PAYBACK);
			movebank[0].addToEnd(new Node(Move.SCALE_SHOT));
			movebank[0].addToEnd(new Node(Move.DRAGON_DANCE));
			movebank[0].addToEnd(new Node(Move.DUAL_CHOP));
			movebank[2] = new Node(Move.BULK_UP);
			movebank[4] = new Node(Move.BREAKING_SWIPE);
			movebank[8] = new Node(Move.TAKE_DOWN);
			movebank[11] = new Node(Move.DRAGON_RAGE);
			movebank[14] = new Node(Move.SUCKER_PUNCH);
			movebank[17] = new Node(Move.SCARY_FACE);
			movebank[20] = new Node(Move.THIEF);
			movebank[23] = new Node(Move.CRUNCH);
			movebank[25] = new Node(Move.DRAGON_TAIL);
			movebank[29] = new Node(Move.FALSE_SURRENDER);
			movebank[32] = new Node(Move.JAW_LOCK);
			movebank[35] = new Node(Move.ICE_FANG);
			movebank[38] = new Node(Move.KNOCK_OFF);
			movebank[39] = new Node(Move.FATAL_BIND);
			movebank[44] = new Node(Move.OBSTRUCT);
			movebank[49] = new Node(Move.DRAGON_CLAW);
			movebank[54] = new Node(Move.NIGHT_SLASH);
			movebank[59] = new Node(Move.OUTRAGE);
			break;
		case 215:
			movebank = new Node[53];
			movebank[0] = new Node(Move.FLASH);
			movebank[2] = new Node(Move.FAKE_TEARS);
			movebank[5] = new Node(Move.HEX);
			movebank[8] = new Node(Move.MUD$SLAP);
			movebank[11] = new Node(Move.MUD_SHOT);
			movebank[14] = new Node(Move.FLASH_RAY);
			movebank[17] = new Node(Move.FEINT_ATTACK);
			movebank[20] = new Node(Move.MUD_BOMB);
			movebank[23] = new Node(Move.THIEF);
			movebank[26] = new Node(Move.SHADOW_SNEAK);
			movebank[29] = new Node(Move.TAIL_GLOW);
			movebank[32] = new Node(Move.SHADOW_BALL);
			movebank[35] = new Node(Move.EARTH_POWER);
			movebank[38] = new Node(Move.MUDDY_WATER);
			movebank[41] = new Node(Move.SWITCHEROO);
			movebank[44] = new Node(Move.PHANTOM_FORCE);
			movebank[47] = new Node(Move.TOPSY$TURVY);
			movebank[52] = new Node(Move.DRAGON_DANCE);
			break;
		case 216:
			movebank = new Node[70];
			movebank[0] = new Node(Move.MAGIC_FANG);
			movebank[0].addToEnd(new Node(Move.BITE));
			movebank[0].addToEnd(new Node(Move.CRUNCH));
			movebank[0].addToEnd(new Node(Move.FEINT_ATTACK));
			movebank[0].addToEnd(new Node(Move.KNOCK_OFF));
			movebank[4] = new Node(Move.FAKE_OUT);
			movebank[7] = new Node(Move.THIEF);
			movebank[11] = new Node(Move.SHADOW_SNEAK);
			movebank[14] = new Node(Move.DRAGON_CLAW);
			movebank[18] = new Node(Move.SHADOW_CLAW);
			movebank[22] = new Node(Move.TAUNT);
			movebank[22].next = new Node(Move.TORMENT);
			movebank[24] = new Node(Move.TAKE_DOWN);
			movebank[28] = new Node(Move.NIGHT_SLASH);
			movebank[30] = new Node(Move.PHANTOM_FORCE);
			movebank[35] = new Node(Move.BREAKING_SWIPE);
			movebank[39] = new Node(Move.UNSEEN_STRANGLE);
			movebank[43] = new Node(Move.DARKEST_LARIAT);
			movebank[46] = new Node(Move.SUCKER_PUNCH);
			movebank[49] = new Node(Move.OUTRAGE);
			movebank[52] = new Node(Move.MAGIC_CRASH);
			movebank[56] = new Node(Move.EARTHQUAKE);
			movebank[59] = new Node(Move.SPECTRAL_THIEF);
			movebank[64] = new Node(Move.PARTING_SHOT);
			movebank[69] = new Node(Move.DRAGON_DANCE);
			break;
		case 217:
			movebank = new Node[19];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[2] = new Node(Move.SAND_ATTACK);
			movebank[8] = new Node(Move.THIEF);
			movebank[12] = new Node(Move.WRAP);
			movebank[15] = new Node(Move.MUD_BOMB);
			movebank[18] = new Node(Move.KNOCK_OFF);
			break;
		case 218:
			movebank = new Node[42];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[2] = new Node(Move.SAND_ATTACK);
			movebank[8] = new Node(Move.THIEF);
			movebank[12] = new Node(Move.WRAP);
			movebank[15] = new Node(Move.MUD_BOMB);
			movebank[18] = new Node(Move.KNOCK_OFF);
			movebank[19] = new Node(Move.DIG);
			movebank[22] = new Node(Move.BRUTAL_SWING);
			movebank[24] = new Node(Move.SNARL);
			movebank[27] = new Node(Move.MUD$SLAP);
			movebank[33] = new Node(Move.DARK_PULSE);
			movebank[35] = new Node(Move.GLARE);
			movebank[38] = new Node(Move.FISSURE);
			movebank[41] = new Node(Move.SANDSTORM);
			break;
		case 219:
			movebank = new Node[70];
			movebank[0] = new Node(Move.POUND);
			movebank[0].next = new Node(Move.TAIL_WHIP);
			movebank[2] = new Node(Move.SAND_ATTACK);
			movebank[8] = new Node(Move.THIEF);
			movebank[12] = new Node(Move.WRAP);
			movebank[15] = new Node(Move.MUD_BOMB);
			movebank[18] = new Node(Move.KNOCK_OFF);
			movebank[19] = new Node(Move.DIG);
			movebank[22] = new Node(Move.BRUTAL_SWING);
			movebank[24] = new Node(Move.SNARL);
			movebank[27] = new Node(Move.MUD$SLAP);
			movebank[33] = new Node(Move.DARK_PULSE);
			movebank[35] = new Node(Move.GLARE);
			movebank[38] = new Node(Move.FISSURE);
			movebank[41] = new Node(Move.SANDSTORM);
			movebank[44] = new Node(Move.EARTH_POWER);
			movebank[49] = new Node(Move.EARTHQUAKE);
			movebank[53] = new Node(Move.SUCKER_PUNCH);
			movebank[56] = new Node(Move.BREAKING_SWIPE);
			movebank[59] = new Node(Move.DRAGON_PULSE);
			movebank[64] = new Node(Move.FATAL_BIND);
			movebank[69] = new Node(Move.DRACO_METEOR);
			break;
		case 220:
			movebank = new Node[40];
			movebank[0] = new Node(Move.SMOKESCREEN);
			movebank[2] = new Node(Move.SMOG);
			movebank[4] = new Node(Move.POISON_GAS);
			movebank[8] = new Node(Move.TORMENT);
			movebank[12] = new Node(Move.HEX);
			movebank[16] = new Node(Move.CURSE);
			movebank[19] = new Node(Move.SLUDGE);
			movebank[23] = new Node(Move.NASTY_PLOT);
			movebank[28] = new Node(Move.TOXIC_SPIKES);
			movebank[31] = new Node(Move.SWITCHEROO);
			movebank[34] = new Node(Move.SLUDGE_BOMB);
			movebank[39] = new Node(Move.DARK_PULSE);
			break;
		case 221:
			movebank = new Node[55];
			movebank[0] = new Node(Move.EXPLOSION);
			movebank[0].next = new Node(Move.SMOKESCREEN);
			movebank[0].next.next = new Node(Move.SWITCHEROO);
			movebank[2] = new Node(Move.SMOG);
			movebank[4] = new Node(Move.POISON_GAS);
			movebank[5] = new Node(Move.FLATTER);
			movebank[7] = new Node(Move.TORMENT);
			movebank[9] = new Node(Move.BEAT_UP);
			movebank[12] = new Node(Move.FEINT_ATTACK);
			movebank[15] = new Node(Move.CURSE);
			movebank[18] = new Node(Move.GASTRO_ACID);
			movebank[21] = new Node(Move.SLUDGE);
			movebank[23] = new Node(Move.NASTY_PLOT);
			movebank[25] = new Node(Move.TOXIC);
			movebank[28] = new Node(Move.VENOSHOCK);
			movebank[30] = new Node(Move.BELCH);
			movebank[32] = new Node(Move.THIEF);
			movebank[34] = new Node(Move.POISON_JAB);
			movebank[35] = new Node(Move.ACID_ARMOR);
			movebank[37] = new Node(Move.SHADOW_BALL);
			movebank[40] = new Node(Move.FALSE_SURRENDER);
			movebank[43] = new Node(Move.SLUDGE_BOMB);
			movebank[46] = new Node(Move.DARK_PULSE);
			movebank[49] = new Node(Move.PHANTOM_FORCE);
			movebank[54] = new Node(Move.KNOCK_OFF);
			break;
		case 222:
			movebank = new Node[65];
			movebank[0] = new Node(Move.EXPLOSION);
			movebank[0].addToEnd(new Node(Move.NASTY_PLOT));
			movebank[0].addToEnd(new Node(Move.TOXIC));
			movebank[0].addToEnd(new Node(Move.CURSE));
			movebank[0].addToEnd(new Node(Move.BELCH));
			movebank[0].addToEnd(new Node(Move.FALSE_SURRENDER));
			movebank[0].addToEnd(new Node(Move.SLUDGE_BOMB));
			movebank[0].addToEnd(new Node(Move.DARK_PULSE));
			movebank[0].addToEnd(new Node(Move.SMOKESCREEN));
			movebank[0].addToEnd(new Node(Move.SMOG));
			movebank[0].addToEnd(new Node(Move.POISON_GAS));
			movebank[0].addToEnd(new Node(Move.FLATTER));
			movebank[0].addToEnd(new Node(Move.TORMENT));
			movebank[0].addToEnd(new Node(Move.BEAT_UP));
			movebank[0].addToEnd(new Node(Move.FEINT_ATTACK));
			movebank[0].addToEnd(new Node(Move.GASTRO_ACID));
			movebank[0].addToEnd(new Node(Move.SLUDGE));
			movebank[0].addToEnd(new Node(Move.VENOSHOCK));
			movebank[0].addToEnd(new Node(Move.SHADOW_CLAW));
			movebank[0].addToEnd(new Node(Move.POISON_JAB));
			movebank[0].addToEnd(new Node(Move.ACID_ARMOR));
			movebank[0].addToEnd(new Node(Move.SHADOW_BALL));
			movebank[0].addToEnd(new Node(Move.SWITCHEROO));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[9] = new Node(Move.SLUDGE_WAVE);
			movebank[14] = new Node(Move.HYPER_VOICE);
			movebank[19] = new Node(Move.OVERHEAT);
			movebank[24] = new Node(Move.DARK_PULSE);
			movebank[29] = new Node(Move.GIGA_DRAIN);
			movebank[34] = new Node(Move.POWER_GEM);
			movebank[39] = new Node(Move.FREEZE$DRY);
			movebank[44] = new Node(Move.AURA_SPHERE);
			movebank[49] = new Node(Move.MOONBLAST);
			movebank[54] = new Node(Move.PHANTOM_FORCE);
			movebank[59] = new Node(Move.STAR_STORM);
			movebank[64] = new Node(Move.KNOCK_OFF);
			break;
		case 223:
			movebank = new Node[18];
			movebank[0] = new Node(Move.FAKE_TEARS);
			movebank[0].next = new Node(Move.WILL$O$WISP);
			movebank[4] = new Node(Move.EMBER);
			movebank[9] = new Node(Move.FIRE_SPIN);
			movebank[14] = new Node(Move.MEMENTO);
			movebank[17] = new Node(Move.KNOCK_OFF);
			break;
		case 224:
			movebank = new Node[33];
			movebank[0] = new Node(Move.FAKE_TEARS);
			movebank[0].next = new Node(Move.WILL$O$WISP);
			movebank[4] = new Node(Move.EMBER);
			movebank[9] = new Node(Move.FIRE_SPIN);
			movebank[14] = new Node(Move.MEMENTO);
			movebank[15] = new Node(Move.MACH_PUNCH);
			movebank[18] = new Node(Move.BEAT_UP);
			movebank[21] = new Node(Move.FEINT_ATTACK);
			movebank[25] = new Node(Move.BRICK_BREAK);
			movebank[28] = new Node(Move.THROAT_CHOP);
			movebank[32] = new Node(Move.KNOCK_OFF);
			break;
		case 225:
			movebank = new Node[70];
			movebank[0] = new Node(Move.FAKE_TEARS);
			movebank[0].next = new Node(Move.WILL$O$WISP);
			movebank[4] = new Node(Move.EMBER);
			movebank[9] = new Node(Move.FIRE_SPIN);
			movebank[14] = new Node(Move.MEMENTO);
			movebank[15] = new Node(Move.MACH_PUNCH);
			movebank[18] = new Node(Move.BEAT_UP);
			movebank[21] = new Node(Move.FURY_SWIPES);
			movebank[25] = new Node(Move.FEINT_ATTACK);
			movebank[28] = new Node(Move.BRICK_BREAK);
			movebank[32] = new Node(Move.KNOCK_OFF);
			movebank[35] = new Node(Move.DRAIN_PUNCH);
			movebank[37] = new Node(Move.TAUNT);
			movebank[41] = new Node(Move.CIRCLE_THROW);
			movebank[44] = new Node(Move.FOUL_PLAY);
			movebank[47] = new Node(Move.WAKE$UP_SLAP);
			movebank[49] = new Node(Move.CROSS_POISON);
			movebank[52] = new Node(Move.NIGHT_SLASH);
			movebank[54] = new Node(Move.CLOSE_COMBAT);
			movebank[59] = new Node(Move.NO_RETREAT);
			movebank[64] = new Node(Move.DARKEST_LARIAT);
			movebank[69] = new Node(Move.FOCUS_ENERGY);
			break;
		case 226:
			movebank = new Node[30];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.ASTONISH);
			movebank[0].next.next = new Node(Move.SCARY_FACE);
			movebank[3] = new Node(Move.BRUTAL_SWING);
			movebank[6] = new Node(Move.GUST);
			movebank[9] = new Node(Move.GLARE);
			movebank[12] = new Node(Move.NIGHT_SHADE);
			movebank[15] = new Node(Move.SHADOW_SNEAK);
			movebank[19] = new Node(Move.COIL);
			movebank[22] = new Node(Move.AIR_CUTTER);
			movebank[25] = new Node(Move.HEX);
			movebank[27] = new Node(Move.DEFOG);
			movebank[29] = new Node(Move.TOXIC);
			movebank[29].addToEnd(new Node(Move.THUNDER_WAVE));
			movebank[29].addToEnd(new Node(Move.WILL$O$WISP));
			break;
		case 227:
			movebank = new Node[60];
			movebank[0] = new Node(Move.WRAP);
			movebank[0].next = new Node(Move.ASTONISH);
			movebank[0].next.next = new Node(Move.SCARY_FACE);
			movebank[3] = new Node(Move.BRUTAL_SWING);
			movebank[6] = new Node(Move.GUST);
			movebank[9] = new Node(Move.GLARE);
			movebank[12] = new Node(Move.NIGHT_SHADE);
			movebank[15] = new Node(Move.SHADOW_SNEAK);
			movebank[19] = new Node(Move.COIL);
			movebank[22] = new Node(Move.AIR_CUTTER);
			movebank[25] = new Node(Move.HEX);
			movebank[27] = new Node(Move.DEFOG);
			movebank[29] = new Node(Move.TOXIC);
			movebank[29].addToEnd(new Node(Move.THUNDER_WAVE));
			movebank[29].addToEnd(new Node(Move.WILL$O$WISP));
			movebank[31] = new Node(Move.AIR_SLASH);
			movebank[33] = new Node(Move.WHIRLWIND);
			movebank[35] = new Node(Move.CURSE);
			movebank[38] = new Node(Move.BITTER_MALICE);
			movebank[42] = new Node(Move.HURRICANE);
			movebank[45] = new Node(Move.SHADOW_BALL);
			movebank[49] = new Node(Move.VENOM_SPIT);
			movebank[54] = new Node(Move.FROSTBIND);
			movebank[59] = new Node(Move.AEROBLAST);
			break;
		case 228:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 229:
			movebank = new Node[90];
			movebank[0] = new Node(Move.BEAT_UP);
			movebank[0].next = new Node(Move.SHADOW_SNEAK);
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[0].addToEnd(new Node(Move.SWITCHEROO));
			movebank[0].addToEnd(new Node(Move.THIEF));
			movebank[0].addToEnd(new Node(Move.HOCUS_POCUS));
			movebank[1] = new Node(Move.EMBER);
			movebank[3] = new Node(Move.WATER_PULSE);
			movebank[5] = new Node(Move.DISCHARGE);
			movebank[7] = new Node(Move.GIGA_DRAIN);
			movebank[9] = new Node(Move.FAKE_TEARS);
			movebank[11] = new Node(Move.BRICK_BREAK);
			movebank[13] = new Node(Move.SUCKER_PUNCH);
			movebank[15] = new Node(Move.DIVE);
			movebank[17] = new Node(Move.SNARL);
			movebank[19] = new Node(Move.MAGIC_REFLECT);
			movebank[21] = new Node(Move.NASTY_PLOT);
			movebank[23] = new Node(Move.ROCK_BLAST);
			movebank[25] = new Node(Move.GLITZY_GLOW);
			movebank[27] = new Node(Move.SHADOW_BALL);
			movebank[29] = new Node(Move.NIGHT_DAZE);
			movebank[31] = new Node(Move.SPARKLE_STRIKE);
			movebank[33] = new Node(Move.REFLECT);
			movebank[35] = new Node(Move.FLATTER);
			movebank[38] = new Node(Move.FALSE_SURRENDER);
			movebank[41] = new Node(Move.VOLT_TACKLE);
			movebank[44] = new Node(Move.COMET_CRASH);
			movebank[47] = new Node(Move.KNOCK_OFF);
			movebank[50] = new Node(Move.OBSTRUCT);
			movebank[53] = new Node(Move.FLARE_BLITZ);
			movebank[56] = new Node(Move.HONE_CLAWS);
			movebank[59] = new Node(Move.TWINKLE_TACKLE);
			movebank[62] = new Node(Move.TRI$ATTACK);
			movebank[65] = new Node(Move.GALAXY_BLAST);
			movebank[68] = new Node(Move.UNSEEN_STRANGLE);
			movebank[71] = new Node(Move.MAGIC_CRASH);
			movebank[74] = new Node(Move.PARTING_SHOT);
			movebank[79] = new Node(Move.SPECTRAL_THIEF);
			movebank[84] = new Node(Move.GLITTER_DANCE);
			movebank[89] = new Node(Move.VANISHING_ACT);
			break;
		case 230:
			movebank = new Node[26];
			movebank[0] = new Node(Move.CHARGE);
			movebank[2] = new Node(Move.THUNDERSHOCK);
			movebank[4] = new Node(Move.GROWL);
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 231:
			movebank = new Node[70];
			movebank[0] = new Node(Move.ICICLE_SPEAR);
			movebank[0].next = new Node(Move.BULLET_PUNCH);
			movebank[0].addToEnd(new Node(Move.WISH));
			movebank[0].addToEnd(new Node(Move.TWINKLE_TACKLE));
			movebank[49] = new Node(Move.SWORDS_DANCE);
			movebank[59] = new Node(Move.ICE_SHARD);
			movebank[69] = new Node(Move.ICICLE_CRASH);
			break;
		case 232:
			movebank = new Node[95];
			movebank[0] = new Node(Move.POISON_STING);
			movebank[0].next = new Node(Move.POISON_GAS);
			movebank[0].addToEnd(new Node(Move.MAGIC_POWDER));
			movebank[0].addToEnd(new Node(Move.TRICK));
			movebank[2] = new Node(Move.AQUA_TAIL);
			movebank[5] = new Node(Move.DRAGON_RUSH);
			movebank[8] = new Node(Move.POISON_TAIL);
			movebank[11] = new Node(Move.DRAGON_TAIL);
			movebank[14] = new Node(Move.SLUDGE);
			movebank[17] = new Node(Move.ACID_SPRAY);
			movebank[20] = new Node(Move.SWIFT);
			movebank[23] = new Node(Move.VENOSHOCK);
			movebank[26] = new Node(Move.TOXIC);
			movebank[29] = new Node(Move.TOXIC_SPIKES);
			movebank[32] = new Node(Move.VENOM_DRENCH);
			movebank[35] = new Node(Move.ELEMENTAL_SPARKLE);
			movebank[38] = new Node(Move.HOCUS_POCUS);
			movebank[41] = new Node(Move.SLUDGE_BOMB);
			movebank[44] = new Node(Move.MAGIC_REFLECT);
			movebank[47] = new Node(Move.ZEN_HEADBUTT);
			movebank[50] = new Node(Move.THUNDERBOLT);
			movebank[53] = new Node(Move.TRI$ATTACK);
			movebank[56] = new Node(Move.AURA_SPHERE);
			movebank[59] = new Node(Move.STORED_POWER);
			movebank[62] = new Node(Move.MAGIC_TOMB);
			movebank[65] = new Node(Move.PRISMATIC_LASER);
			movebank[68] = new Node(Move.SPIKES);
			movebank[71] = new Node(Move.RECOVER);
			movebank[74] = new Node(Move.NASTY_PLOT);
			movebank[77] = new Node(Move.VENOM_SPIT);
			movebank[80] = new Node(Move.HYPER_VOICE);
			movebank[84] = new Node(Move.GALAXY_BLAST);
			movebank[89] = new Node(Move.COIL);
			movebank[94] = new Node(Move.CORE_ENFORCER);
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
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
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
			movebank[6] = new Node(Move.BABY$DOLL_EYES);
			movebank[9] = new Node(Move.FLASH);
			movebank[12] = new Node(Move.MUD_BOMB);
			movebank[15] = new Node(Move.SHOCK_WAVE);
			movebank[21] = new Node(Move.DRAINING_KISS);
			movebank[22] = new Node(Move.HEADBUTT);
			movebank[25] = new Node(Move.THUNDER_WAVE);
			break;
		case 236:
			movebank = new Node[100];
			movebank[0] = new Node(Move.GRAVITY);
			movebank[0].next = new Node(Move.TRICK_ROOM);
			movebank[4] = new Node(Move.FLAME_CHARGE);
			movebank[7] = new Node(Move.SPACE_BEAM);
			movebank[10] = new Node(Move.LIGHT_BEAM);
			movebank[13] = new Node(Move.INCINERATE);
			movebank[16] = new Node(Move.COSMIC_POWER);
			movebank[19] = new Node(Move.WILL$O$WISP);
			movebank[22] = new Node(Move.COMET_CRASH);
			movebank[25] = new Node(Move.SUNNY_DAY);
			movebank[28] = new Node(Move.DESOLATE_VOID);
			movebank[31] = new Node(Move.DAZZLING_GLEAM);
			movebank[34] = new Node(Move.FLAME_BURST);
			movebank[37] = new Node(Move.FLASH_CANNON);
			movebank[40] = new Node(Move.STAR_STORM);
			movebank[44] = new Node(Move.LAVA_PLUME);
			movebank[48] = new Node(Move.PRISMATIC_LASER);
			movebank[52] = new Node(Move.AEROBLAST);
			movebank[56] = new Node(Move.GLITTER_DANCE);
			movebank[60] = new Node(Move.SOLAR_BEAM);
			movebank[64] = new Node(Move.GENESIS_SUPERNOVA);
			movebank[69] = new Node(Move.HEAT_WAVE);
			movebank[74] = new Node(Move.PHOTON_GEYSER);
			movebank[79] = new Node(Move.SACRED_FIRE);
			movebank[84] = new Node(Move.BLACK_HOLE_ECLIPSE);
			movebank[89] = new Node(Move.SUPERNOVA_EXPLOSION);
			movebank[94] = new Node(Move.V$CREATE);
			movebank[99] = new Node(Move.ERUPTION);
			break;
		case 237:
			movebank = new Node[65];
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
			movebank[45] = new Node(Move.SEA_DRAGON);
			movebank[47] = new Node(Move.WATER_CLAP);
			movebank[51] = new Node(Move.WATER_KICK);
			movebank[55] = new Node(Move.SUPERCHARGED_SPLASH);
			movebank[59] = new Node(Move.DEEP_SEA_BUBBLE);
			movebank[64] = new Node(Move.SEA_DRAGON);
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
		case 239:
            movebank = new Node[61];
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
            movebank[44] = new Node(Move.FOCUS_ENERGY);
            movebank[47] = new Node(Move.SWORDS_DANCE);
            movebank[54] = new Node(Move.CLOSE_COMBAT);
            movebank[60] = new Node(Move.HEAD_SMASH);
            break;
		case 240:
            movebank = new Node[75];
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
            movebank[44] = new Node(Move.FOCUS_ENERGY);
            movebank[47] = new Node(Move.SWORDS_DANCE);
            movebank[52] = new Node(Move.DRAGON_RUSH);
            movebank[54] = new Node(Move.CLOSE_COMBAT);
            movebank[59] = new Node(Move.ROOST);
            movebank[64] = new Node(Move.SUMMIT_STRIKE);
            movebank[64].next = new Node(Move.ABYSSAL_CHOP);
            movebank[74] = new Node(Move.DRAGON_DANCE);
            break;
	     case 241:
	         movebank = new Node[45];
	         movebank[0] = new Node(Move.ROCK_THROW);
	         movebank[0].addToEnd(new Node(Move.HARDEN));
	         movebank[0].addToEnd(new Node(Move.SMACK_DOWN));
	         movebank[4] = new Node(Move.ACID_SPRAY);
	         movebank[9] = new Node(Move.ANCIENT_POWER);
	         movebank[14] = new Node(Move.ROCK_POLISH);
	         movebank[17] = new Node(Move.STEALTH_ROCK);
	         movebank[21] = new Node(Move.VENOSHOCK);
	         movebank[24] = new Node(Move.SANDSTORM);
	         movebank[27] = new Node(Move.SELF$DESTRUCT);
	         movebank[30] = new Node(Move.SPIKES);
	         movebank[34] = new Node(Move.SLUDGE);
	         movebank[38] = new Node(Move.POWER_GEM);
	         movebank[41] = new Node(Move.ACID_ARMOR);
	         movebank[44] = new Node(Move.SLUDGE_WAVE);
	         break;
	     case 242:
	    	 movebank = new Node[50];
	         movebank[0] = new Node(Move.SPIKY_SHIELD);
	         movebank[0].addToEnd(new Node(Move.TOXIC_SPIKES));
	         movebank[0].addToEnd(new Node(Move.ROCK_THROW));
	         movebank[0].addToEnd(new Node(Move.HARDEN));
	         movebank[0].addToEnd(new Node(Move.SMACK_DOWN));
	         movebank[4] = new Node(Move.ACID_SPRAY);
	         movebank[9] = new Node(Move.ANCIENT_POWER);
	         movebank[14] = new Node(Move.ROCK_POLISH);
	         movebank[17] = new Node(Move.STEALTH_ROCK);
	         movebank[21] = new Node(Move.VENOSHOCK);
	         movebank[24] = new Node(Move.SANDSTORM);
	         movebank[27] = new Node(Move.SELF$DESTRUCT);
	         movebank[30] = new Node(Move.SPIKES);
	         movebank[34] = new Node(Move.SLUDGE);
	         movebank[34].next = new Node(Move.MORTAL_SPIN);
	         movebank[38] = new Node(Move.POWER_GEM);
	         movebank[41] = new Node(Move.ACID_ARMOR);
	         movebank[44] = new Node(Move.TOXIC);
	         movebank[49] = new Node(Move.SLUDGE_WAVE);
	         break;
	     case 243:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 244:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 245:
	    	 movebank = new Node[55];
             movebank[0] = new Node(Move.CONFUSION);
             movebank[0].addToEnd(new Node(Move.FLASH));
             movebank[0].addToEnd(new Node(Move.TELEPORT));
             movebank[4] = new Node(Move.PSYBEAM);
             movebank[8] = new Node(Move.SWIFT);
             movebank[12] = new Node(Move.REFLECT);
             movebank[15] = new Node(Move.ELEMENTAL_SPARKLE);
             movebank[20] = new Node(Move.PSYCHO_CUT);
             movebank[24] = new Node(Move.RECOVER);
             movebank[27] = new Node(Move.HOCUS_POCUS);
             movebank[30] = new Node(Move.PSYSHOCK);
             movebank[34] = new Node(Move.MAGIC_TOMB);
             movebank[39] = new Node(Move.PSYCHIC);
             movebank[44] = new Node(Move.SHADOW_BALL);
             movebank[49] = new Node(Move.CALM_MIND);
             movebank[54] = new Node(Move.VANISHING_ACT);
             break;
	     case 246:
	         movebank = new Node[54];
	         movebank[0] = new Node(Move.PECK);
	         //movebank[0].addToEnd(new Node(Move.COPYCAT));
	         movebank[4] = new Node(Move.DOUBLE_KICK);
	         movebank[8] = new Node(Move.DETECT);
	         movebank[11] = new Node(Move.WING_ATTACK);
	         movebank[14] = new Node(Move.FOCUS_ENERGY);
	         movebank[17] = new Node(Move.LOW_KICK);
	         movebank[20] = new Node(Move.FEINT);
	         movebank[26] = new Node(Move.PAYBACK);
	         movebank[30] = new Node(Move.HI_JUMP_KICK);
	         movebank[34] = new Node(Move.AIR_SLASH);
	         movebank[38] = new Node(Move.ROOST);
	         movebank[43] = new Node(Move.CLOSE_COMBAT);
	         movebank[47] = new Node(Move.THROAT_CHOP);
	         movebank[53] = new Node(Move.BRAVE_BIRD);
	         break;
	     case 247:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 248:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 249:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 250:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 251:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 252:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 253:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 254:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 255:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 256:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 257:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 258:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 259:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 260:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 261:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 262:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 263:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 264:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 265:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 266:
			movebank = new Node[25];
			movebank[0] = new Node(Move.ABSORB);
			movebank[0].next = new Node(Move.SUPERSONIC);
			movebank[4] = new Node(Move.ASTONISH);
			movebank[9] = new Node(Move.MEAN_LOOK);
			movebank[14] = new Node(Move.POISON_FANG);
			movebank[19] = new Node(Move.BITE);
			movebank[24] = new Node(Move.WING_ATTACK);
			break;
	     case 267:
             movebank = new Node[65];
             movebank[0] = new Node(Move.CONFUSION);
             movebank[0].addToEnd(new Node(Move.LEER));
             movebank[4] = new Node(Move.PSYWAVE);
             movebank[8] = new Node(Move.COSMIC_POWER);
             movebank[12] = new Node(Move.COMET_PUNCH);
             movebank[16] = new Node(Move.CONFUSE_RAY);
             movebank[19] = new Node(Move.HYPNOSIS);
             movebank[22] = new Node(Move.TRICK_ROOM);
             movebank[25] = new Node(Move.SPACE_BEAM);
             movebank[29] = new Node(Move.ABDUCT);
             movebank[33] = new Node(Move.RECOVER);
             movebank[36] = new Node(Move.GALAXY_BLAST);
             movebank[39] = new Node(Move.RADIO_BURST);
             movebank[43] = new Node(Move.STAR_STORM);
             movebank[47] = new Node(Move.CALM_MIND);
             movebank[51] = new Node(Move.DARK_VOID);   
             movebank[56] = new Node(Move.CORE_ENFORCER);
             movebank[64] = new Node(Move.BLACK_HOLE_ECLIPSE);                  
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
	
	public class Task {
		public static final int TEXT = 0;
		public static final int DAMAGE = 1;
		public static final int ABILITY = 2;
		public static final int STAT = 3;
		public static final int SWAP_IN = 4;
		public static final int SWAP_OUT = 5;
		public static final int FAINT = 6;
		public static final int END = 7;
		public static final int PARTY = 8;
		public static final int STATUS = 9;
		public static final int CATCH = 10;
		public static final int NICKNAME = 11;
		public static final int EXP = 12;
		public static final int LEVEL_UP = 13;
		public static final int MOVE = 14;
		public static final int EVO = 15;
		public static final int WEATHER = 16;
		public static final int TERRAIN = 17;
		public static final int CLOSE = 18;
		public static final int GIFT = 19;
		public static final int REMIND = 20;
		public static final int FOSSIL = 21;
		public static final int HP = 22;
		public static final int EVO_ITEM = 23;
		public static final int SPRITE = 24;
		public static final int CONFIRM = 25; // counter: 0 = gauntlet at mt. splinkty
		public static final int TELEPORT = 26;
		public static final int SEMI_INV = 27;
		
		public int type;
		public String message;
		public int counter; // map for teleport
		
		public boolean wipe; // cooldown for teleport, visible/not for semi_inv
		
		public int start; // x for teleport
		public int finish; // y for teleport
		public Pokemon p; // the pokemon taking damage, or announcing an ability, or being sent out
		public Pokemon evo;
		public Ability ability;
		public Status status;
		public Move move;
		public FieldEffect fe;
		public Item item;
		
		public Task(int type, String message) {
			this(type, message, null);
		}

		public Task(int type, String message, Pokemon p) {
			this.message = message;
			this.type = type;
			this.counter = 50;
			setPokemon(p);
			start = -1;
		}
		
		public void setPokemon(Pokemon p) {
			this.p = p;
		}
		
		public void setFinish(int f) {
			this.finish = f;
		}
		
		public void setAbility(Ability ability) {
			this.ability = ability;
		}
		
		public void setWipe(boolean wipe) {
			this.wipe = wipe;
		}
		
		public void setMove(Move m) {
			this.move = m;
		}
		
		public void setEffect(FieldEffect fe) {
			this.fe = fe;
		}
		
		public String toString() {
			try {
				return message.substring(0, 6) + "... [" + getTypeString() + "]";
			} catch (StringIndexOutOfBoundsException e) {
				//e.printStackTrace();
				return "\"\" [" + getTypeString() + "]";
			}
		}
		
		private String getTypeString() {
			switch(type) {
			case TEXT: return "TEXT";
			case DAMAGE: return "DAMAGE";
			case ABILITY: return "ABILITY";
			case STAT: return "STAT";
			case SWAP_IN: return "SWAP_IN";
			case SWAP_OUT: return "SWAP_OUT";
			case FAINT: return "FAINT";
			case END: return "END";
			case PARTY: return "PARTY";
			case STATUS: return "STATUS";
			case CATCH: return "CATCH";
			case NICKNAME: return "NICKNAME";
			case EXP: return "EXP";
			case LEVEL_UP: return "LEVEL_UP";
			case MOVE: return "MOVE";
			case EVO: return "EVO";
			case WEATHER: return "WEATHER";
			case TERRAIN: return "TERRAIN";
			case CLOSE: return "CLOSE";
			case GIFT: return "GIFT";
			case REMIND: return "REMIND";
			case FOSSIL: return "FOSSIL";
			case HP: return "HP";
			case EVO_ITEM: return "EVO_ITEM";
			default:
				return "getTypeString() doesn't have a case for this type";
			}
		}
	}

	public void faint(boolean announce, Pokemon foe) {
		this.currentHP = 0;
		this.fainted = true;
		this.battled = false;
		awardHappiness(-3, false);
		this.status = Status.HEALTHY;
		this.vStatuses.remove(Status.LOCKED);
		this.vStatuses.remove(Status.SWITCHING);
		this.vStatuses.remove(Status.SPUN);
		this.vStatuses.remove(Status.RECHARGE);
		this.vStatuses.remove(Status.CHARGING);
		this.vStatuses.remove(Status.NO_SWITCH);
		this.vStatuses.remove(Status.TRAPPED);
		this.vStatuses.remove(Status.SEMI_INV);
		foe.vStatuses.remove(Status.SPUN);
		foe.vStatuses.remove(Status.TRAPPED);
		if (this.trainer != null && this.playerOwned()) {
			Player player = (Player) this.trainer;
			player.setBattled(player.getBattled() - 1);
		}
		if (announce) addTask(Task.FAINT, this.nickname + " fainted!", this);
		foe.awardxp(getxpReward());
	}

	public void clearVolatile() {
		confusionCounter = 0;
		magCount = 0;
		toxic = 0;
		perishCount = 0;
		consumedItem = false;
		this.lastMoveUsed = null;
		this.moveMultiplier = 1;
		this.vStatuses.clear();
		statStages = new int[7];
		this.impressive = true;
		setTypes();
		setAbility(this.abilitySlot);
		if (this.ability == Ability.NATURAL_CURE) this.status = Status.HEALTHY;
		
	}
	
	public int calc(double attackStat, double defenseStat, double bp, int level) {
		return calc(attackStat, defenseStat, bp, level, 0);
	}
	
	public int calc(double attackStat, double defenseStat, double bp, int level, int mode) {
		double num = 2* (double) level / 5 + 2;
		double stat = attackStat / defenseStat / 50;
		double damageDouble = Math.floor(num * bp * stat);
		damageDouble += 2;
		
		Random roll = new Random();
		double rollAmt = roll.nextInt(16);
		rollAmt += 85;
		rollAmt /= 100;
		
		if (mode == -1) rollAmt = 0.85;
		if (mode == 1) rollAmt = 1.0;
		// Roll
		damageDouble *= rollAmt;
		// Convert to integer
		int damage = (int) Math.floor(damageDouble);
		return damage;
	}
	
	public int calcWithTypes(Pokemon foe, Move move, boolean first) {
		return calcWithTypes(foe, move, first, 0, false);
	}
	
	public int calcWithTypes(Pokemon foe, Move move, boolean first, int mode, boolean crit) {
		double attackStat;
		double defenseStat;
		int damage = 0;
		double bp = move.basePower;
		int acc = move.accuracy;
		int secChance = move.secondary;
		PType moveType = move.mtype;
		int critChance = move.critChance;
		Ability foeAbility = foe.ability;
		
		int magicDamage = 0;
		
		ArrayList<FieldEffect> enemySide = this.playerOwned() ? field.foeSide : field.playerSide;
		
		if (id == 237) {
			move = get150Move(move);
			bp = move.basePower;
			acc = move.accuracy;
			moveType = move.mtype;
			critChance = move.critChance;
		}
		
		if (mode == 0 && (move == Move.MAGIC_BLAST || move == Move.ELEMENTAL_SPARKLE)) {
			if (move == Move.MAGIC_BLAST) {
				ArrayList<Move> moves = new ArrayList<>();
				for (Move cand : Move.values()) {
					if (cand.mtype == PType.ROCK || cand.mtype == PType.GRASS || cand.mtype == PType.GROUND) {
						moves.add(cand);
					}
				}
				Move[] validMoves = moves.toArray(new Move[moves.size()]);
				Move pickMove = validMoves[new Random().nextInt(validMoves.length)];
				magicDamage = calcWithTypes(foe, pickMove, first, mode, crit);
			}
			if (move == Move.ELEMENTAL_SPARKLE) {
				ArrayList<Move> moves = new ArrayList<>();
				for (Move cand : Move.values()) {
					if (cand.mtype == PType.FIRE || cand.mtype == PType.WATER || cand.mtype == PType.GRASS) {
						moves.add(cand);
					}
				}
				Move[] validMoves = moves.toArray(new Move[moves.size()]);
				Move pickMove = validMoves[new Random().nextInt(validMoves.length)];
				magicDamage = calcWithTypes(foe, pickMove, first, mode, crit);
			}
		}
		
		if (mode == 0 && (move == Move.SLEEP_TALK || move == Move.SNORE)) {
			if (status != Status.ASLEEP || sleepCounter == 0) {
				return 0;
			} else {
				return 999999;
			}
		}
		
		if (mode == 0 && (move == Move.FIRST_IMPRESSION || move == Move.BELCH)) {
			if (!this.impressive) {
				return -1;
			} else {
				bp *= 2;
			}
		}
		
		if (mode == 0 && (move == Move.UNSEEN_STRANGLE || move == Move.FAKE_OUT)) {
			if (!this.impressive) {
				return -1;
			} else {
				bp *= 6;
			}
		}
		
		if (this.ability == Ability.COMPOUND_EYES) acc *= 1.3;
		if (item == Item.WIDE_LENS) acc *= 1.1;
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
		
		if (foe.ability == Ability.WONDER_SKIN && move.cat == 2 && acc <= 100) acc = 50;
		
		if (move == Move.FUSION_BOLT || move == Move.FUSION_FLARE || this.ability == Ability.MOLD_BREAKER) foeAbility = Ability.NULL;
		
		if (mode == 0 && this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD && acc < 100) {
			int accEv = this.statStages[5] - foe.statStages[6];
			if (move == Move.DARKEST_LARIAT || move == Move.SACRED_SWORD) accEv += foe.statStages[6];
			accEv = accEv > 6 ? 6 : accEv;
			accEv = accEv < -6 ? -6 : accEv;
			double accuracy = acc * asAccModifier(accEv);
			if ((field.equals(field.weather, Effect.SANDSTORM) && foeAbility == Ability.SAND_VEIL) ||
					(field.equals(field.weather, Effect.SNOW) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
			if (!hit(accuracy)) {
				return 1;
			}
		}
		
		if (move == Move.HIDDEN_POWER) moveType = determineHPType();
		if (move == Move.WEATHER_BALL) moveType = determineWBType();
		if (move == Move.TERRAIN_PULSE) moveType = determineTPType();
		
		if (moveType == PType.FIRE && foeAbility == Ability.FLASH_FIRE) {
			return 0;
		}
		
		if ((moveType == PType.WATER && (foeAbility == Ability.WATER_ABSORB || foeAbility == Ability.DRY_SKIN)) || (moveType == PType.ELECTRIC && foeAbility == Ability.VOLT_ABSORB) ||
				(moveType == PType.BUG && foeAbility == Ability.INSECT_FEEDER)) {
			return 0;
		}
		
		if ((moveType == PType.ELECTRIC && (foeAbility == Ability.MOTOR_DRIVE || foeAbility == Ability.LIGHTNING_ROD)) ||
				(moveType == PType.GRASS && foeAbility == Ability.SAP_SIPPER)) {
			return 0;
		}
		
		if (moveType == PType.GROUND && !foe.isGrounded() && move.cat != 2) {
			return 0;
		}
		
		if (moveType != PType.GROUND && (move.cat != 2 || move == Move.THUNDER_WAVE)) {
			if (getImmune(foe, moveType) || (moveType == PType.GHOST && foeAbility == Ability.FRIENDLY_GHOST)) {
				if (this.ability == Ability.SCRAPPY && (moveType == PType.NORMAL || moveType == PType.FIGHTING)) {
					// Nothing: scrappy allows normal and fighting type moves to hit ghosts
				} else {
					return 0;
				}
			}
		}
		
		if (field.equals(field.terrain, Effect.PSYCHIC) && foe.isGrounded() && move.hasPriority(this)) {
			return 0;
		}
		
		
		if (mode == 0 && move == Move.DREAM_EATER && foe.status != Status.ASLEEP) {
			return -1;
		}
		
		if (move.cat == 2) {
			return 0;
		}
		
		if (move.basePower < 0) {
			bp = determineBasePower(foe, move, first, null, false);
		}
		
		if (mode == 0 && (move == Move.KNOCK_OFF || ((move == Move.COVET || move == Move.THIEF) && this.item == null)) && foe.item != null && checkSecondary(new Random().nextInt(25))) bp *= 2;
		
		if (moveType == PType.FIRE && this.vStatuses.contains(Status.FLASH_FIRE)) bp *= 1.5;
		
		if (this.ability == Ability.NORMALIZE && moveType != PType.NORMAL) {
			moveType = PType.NORMAL;
			bp *= 1.2;
		}
		
		if (this.ability == Ability.PIXILATE && moveType == PType.NORMAL) {
			moveType = PType.LIGHT;
			bp *= 1.2;
		}
		
		if (this.ability == Ability.GALVANIZE && moveType == PType.NORMAL) {
			moveType = PType.ELECTRIC;
			bp *= 1.2;
		}
		
		if (this.ability == Ability.SHEER_FORCE && move.cat != 2 && secChance > 0) {
			bp *= 1.3;
		}
		
		if (this.ability == Ability.TECHNICIAN && bp <= 60) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.TOUGH_CLAWS && move.contact) {
			bp *= 1.3;
		}
		
		if (foeAbility == Ability.DRY_SKIN && moveType == PType.FIRE) {
			bp *= 1.25;
		}
		
		if (this.ability == Ability.SHARPNESS && move.isSlicing()) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.IRON_FIST && (move == Move.BULLET_PUNCH || move == Move.COMET_PUNCH || move == Move.DRAIN_PUNCH
				|| move == Move.FIRE_PUNCH || move == Move.ICE_PUNCH || move == Move.MACH_PUNCH || move == Move.POWER$UP_PUNCH || move == Move.SHADOW_PUNCH
				|| move == Move.SKY_UPPERCUT || move == Move.THUNDER_PUNCH || move == Move.METEOR_MASH || move == Move.PLASMA_FISTS || move == Move.SUCKER_PUNCH
				|| move == Move.DYNAMIC_PUNCH)) {
			bp *= 1.3;
		}
		
		if (this.ability == Ability.STRONG_JAW && (move == Move.BITE || move == Move.CRUNCH || move == Move.FIRE_FANG || move == Move.HYPER_FANG
				|| move == Move.ICE_FANG || move == Move.JAW_LOCK || move == Move.POISON_FANG || move == Move.PSYCHIC_FANGS || move == Move.THUNDER_FANG
				|| move == Move.LEECH_LIFE)) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.RECKLESS && Move.getRecoil().contains(move) && move != Move.STRUGGLE) {
			bp *= 1.3;
		}
		
		if (moveType == PType.GRASS && this.ability == Ability.OVERGROW && this.currentHP <= this.getStat(0) * 1.0 / 3) {
			bp *= 1.5;
		} else if (moveType == PType.FIRE && this.ability == Ability.BLAZE && this.currentHP <= this.getStat(0) * 1.0 / 3) {
			bp *= 1.5;
		} else if (moveType == PType.WATER && this.ability == Ability.TORRENT && this.currentHP <= this.getStat(0) * 1.0 / 3) {
			bp *= 1.5;
		} else if (moveType == PType.BUG && this.ability == Ability.SWARM && this.currentHP <= this.getStat(0) * 1.0 / 3) {
			bp *= 1.5;
		}
		
		if (move.cat == 0 && item == Item.MUSCLE_BAND) bp *= 1.1;
		if (move.cat == 1 && item == Item.WISE_GLASSES) bp *= 1.1;
		
		bp *= boostItemCheck(moveType);
		
		if (field.equals(field.weather, Effect.SUN)) {
			if (moveType == PType.WATER) bp *= 0.5;
			if (moveType == PType.FIRE) bp *= 1.5;
		}
		
		if (field.equals(field.weather, Effect.RAIN)) {
			if (moveType == PType.WATER) bp *= 1.5;
			if (moveType == PType.FIRE) bp *= 0.5;
			if (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) bp *= 0.5;
		}
		
		if (field.equals(field.terrain, Effect.ELECTRIC) && isGrounded()) {
			if (moveType == PType.ELECTRIC) bp *= 1.5;
		}
		
		if (field.equals(field.terrain, Effect.GRASSY)) {
			if (isGrounded() && moveType == PType.GRASS) bp *= 1.5;
			if (move == Move.EARTHQUAKE || move == Move.BULLDOZE || move == Move.MAGNITUDE) bp *= 0.5;
		}
		
		if (field.equals(field.terrain, Effect.SPARKLY) && isGrounded()) {
			if (moveType == PType.MAGIC) bp *= 1.5;
		}
		
		if (field.equals(field.terrain, Effect.PSYCHIC) && isGrounded()) {
			if (moveType == PType.PSYCHIC) bp *= 1.5;
		}
		
		if ((field.contains(field.fieldEffects, Effect.MUD_SPORT) && moveType == PType.ELECTRIC) || (field.contains(field.fieldEffects, Effect.WATER_SPORT) && moveType == PType.FIRE)) bp *= 0.33;
		
		if (field.equals(field.weather, Effect.SANDSTORM) && this.ability == Ability.SAND_FORCE && (moveType == PType.ROCK || moveType == PType.GROUND || moveType == PType.STEEL)) bp *= 1.3;
		
		if ((field.equals(field.weather, Effect.RAIN) || field.equals(field.weather, Effect.SNOW) || field.equals(field.weather, Effect.SANDSTORM)) && (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE)) bp *= 0.5;
		
		// Use either physical or special attack/defense
		if (move.isPhysical()) {
			attackStat = this.getStat(1);
			defenseStat = foe.getStat(2);
			if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(0);
			if (move != Move.DARKEST_LARIAT && move != Move.SACRED_SWORD && this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(1);
			if (move == Move.BODY_PRESS) attackStat = this.getStat(2) * this.asModifier(1);
			if (move == Move.FOUL_PLAY) attackStat = foe.getStat(1) * foe.asModifier(0);
			if (this.item == Item.CHOICE_BAND) attackStat *= 1.5;
			if (this.status == Status.BURNED && this.ability != Ability.GUTS && move != Move.FACADE) attackStat /= 2;
			if (this.ability == Ability.GUTS && this.status != Status.HEALTHY) attackStat *= 1.5;
			if (this.ability == Ability.HUGE_POWER) attackStat *= 2;
			if (field.equals(field.weather, Effect.SNOW) && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) defenseStat *= 1.5;
		} else {
			attackStat = this.getStat(3);
			defenseStat = foe.getStat(4);
			if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(2);
			if (this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(3);
			if (mode != 0 && foe.item == Item.ASSAULT_VEST) defenseStat *= 1.5;
			if (move == Move.PSYSHOCK) defenseStat = foe.getStat(2) * foe.asModifier(1);
			if (this.item == Item.CHOICE_SPECS) attackStat *= 1.5;
			if (this.status == Status.FROSTBITE) attackStat /= 2;
			if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN)) attackStat *= 1.5;
			if (field.equals(field.weather, Effect.SANDSTORM) && (foe.type1 == PType.ROCK || foe.type2 == PType.ROCK)) defenseStat *= 1.5;
		}
		
		// Stab
		if (moveType == this.type1 || moveType == this.type2 || this.ability == Ability.TYPE_MASTER || this.ability == Ability.PROTEAN) {
			if (ability == Ability.ADAPTABILITY) {
				bp *= 2;
			} else {
				bp *= 1.5;
			}
		}
		
		if (moveType == PType.STEEL && this.ability == Ability.STEELWORKER) bp *= 1.5;
		if (moveType == PType.MAGIC && this.ability == Ability.MAGICAL) bp *= 1.5;
		
		// Charged
		if (moveType == PType.ELECTRIC && this.vStatuses.contains(Status.CHARGED)) bp *= 2;
		
		// Load Firearms
		if (moveType == PType.STEEL && this.vStatuses.contains(Status.LOADED)) bp *= 2;
		
		// Multi hit moves calc to use
		if (mode == 0) bp *= move.getNumHits(this, null);
		
		// Crit Check
		if (this.vStatuses.contains(Status.FOCUS_ENERGY)) critChance += 2;
		if (this.ability == Ability.SUPER_LUCK) critChance++;
		if (foe.ability != Ability.BATTLE_ARMOR && foe.ability != Ability.SHELL_ARMOR && ((mode == 0 && critChance >= 1 && critCheck(critChance)) || (mode != 0 && (critChance >= 3 || crit)))) {
			if (move.isPhysical() && attackStat < this.getStat(1)) {
				attackStat = this.getStat(1);
				if (this.status == Status.BURNED) attackStat /= 2;
			}
			if (!move.isPhysical() && attackStat < this.getStat(3)) {
				attackStat = this.getStat(3);
				if (this.status == Status.FROSTBITE) attackStat /= 2;
			}
			if (move.isPhysical() && defenseStat > foe.getStat(2)) {
				defenseStat = foe.getStat(2);
				if (field.equals(field.weather, Effect.SNOW) && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) defenseStat *= 1.5;
				
			}
			if (!move.isPhysical() && defenseStat > foe.getStat(4)) {
				defenseStat = foe.getStat(4);
				if (field.equals(field.weather, Effect.SANDSTORM) && (foe.type1 == PType.ROCK || foe.type2 == PType.ROCK)) defenseStat *= 1.5;
			}
			if (mode != 0 && foe.item == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
			damage = calc(attackStat, defenseStat, bp, this.level, mode);
			damage *= 1.5;
			if (this.ability == Ability.SNIPER) damage *= 1.5;
		} else {
			if (mode != 0 && foe.item == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
			damage = calc(attackStat, defenseStat, bp, this.level, mode);
		}
		
		if ((foeAbility == Ability.ICY_SCALES && !move.isPhysical()) || (foeAbility == Ability.MULTISCALE && foe.currentHP == foe.getStat(0))) damage /= 2;
		
		// Screens
		if (move.isPhysical() && (field.contains(enemySide, Effect.REFLECT) || field.contains(enemySide, Effect.AURORA_VEIL))) damage /= 2;
		if (!move.isPhysical() && (field.contains(enemySide, Effect.LIGHT_SCREEN) || field.contains(enemySide, Effect.AURORA_VEIL))) damage /= 2;
		
		double multiplier = 1;
		// Check type effectiveness
		PType[] resist = getResistances(moveType);
		if (move == Move.FREEZE$DRY) {
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
		
		if (foeAbility == Ability.ILLUMINATION) {
			PType[] lightResist = new PType[]{PType.GHOST, PType.GALACTIC, PType.LIGHT, PType.DARK};
			for (PType type : lightResist) {
				if (moveType == type) multiplier /= 2;
			}
		}
		
		// Check type effectiveness
		PType[] weak = getWeaknesses(moveType);
		if (move == Move.FREEZE$DRY) {
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
		
		if (foeAbility == Ability.WONDER_GUARD && multiplier <= 1) {
			return 0;
		}
		
		if (foeAbility == Ability.FLUFFY && moveType == PType.FIRE) multiplier *= 2;
		
		damage *= multiplier;
		
		if (foeAbility == Ability.UNERODIBLE && (moveType == PType.GRASS || moveType == PType.WATER || moveType == PType.GROUND)) damage /= 4;
		if (foeAbility == Ability.THICK_FAT && (moveType == PType.FIRE || moveType == PType.ICE)) damage /= 2;
		if (foeAbility == Ability.UNWAVERING && (moveType == PType.DARK || moveType == PType.GHOST)) damage /= 2;
		if (foeAbility == Ability.FLUFFY && move.contact) damage /= 2;
		
		if (foeAbility == Ability.PSYCHIC_AURA && move.cat == 1) damage *= 0.8;
		if (foeAbility == Ability.GLACIER_AURA && move.cat == 0) damage *= 0.8;
		if (foeAbility == Ability.GALACTIC_AURA && (moveType == PType.PSYCHIC || moveType == PType.ICE)) damage /= 2;
		
		if (multiplier > 1) {
			if (foeAbility == Ability.SOLID_ROCK || foeAbility == Ability.FILTER) damage /= 2;
			if (item == Item.EXPERT_BELT) damage *= 1.2;
			if (mode != 0 && foe.item != null && foe.checkTypeResistBerry(moveType)) damage /= 2;
		}
		
		if (multiplier < 1) {
			if (ability == Ability.TINTED_LENS) damage *= 2;
		}
		
		if (mode == 0 && this.item == Item.THROAT_SPRAY && Move.getSound().contains(move)) damage *= 1.5;
		
		if (this.item == Item.LIFE_ORB) damage *= 1.3;
		if (this.ability == Ability.ANALYTIC && !first) damage *= 1.3;
		if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS || move == Move.PSYWAVE) damage = this.level;
		if (move == Move.ENDEAVOR) {
			if (foe.currentHP > this.currentHP) {
				damage = foe.currentHP - this.currentHP;
			} else { return 0; } }
		if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
		if (move == Move.DRAGON_RAGE) damage = 40;
		if (mode == 0 && (move == Move.HORN_DRILL || move == Move.SHEER_COLD || move == Move.GUILLOTINE || move == Move.FISSURE)) {
			if ((move == Move.SHEER_COLD && (foe.type1 == PType.ICE || foe.type2 == PType.ICE)) || foeAbility == Ability.STURDY || foe.level > this.level) {
				return 0;
			}
			damage = foe.currentHP;
		}
		
		damage = Math.max(damage, 1);
		

		if (mode == 0 && damage >= foe.currentHP) damage = foe.currentHP; // Check for kill
		
		if (mode == 0 && (move == Move.MAGIC_BLAST || move == Move.ELEMENTAL_SPARKLE)) damage += magicDamage;
		
		if ((move == Move.SELF$DESTRUCT || move == Move.EXPLOSION || move == Move.SUPERNOVA_EXPLOSION) && mode == 0) {
			Random rand = new Random();
			if (rand.nextInt(4) != 1) {
				return 0;
			}
		}
		return damage;
	}

	public void endOfTurn(Pokemon f) {
		if (this.isFainted()) return;
		if (this.status == Status.TOXIC && toxic < 16) toxic++;
		if (this.status == Status.FROSTBITE && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			this.damage(Math.max(this.getStat(0) * 1.0 / 16, 1), f, this.nickname + " was hurt by frostbite!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		} else if (this.status == Status.BURNED && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			this.damage(Math.max(this.getStat(0) * 1.0 / 16, 1), f, this.nickname + " was hurt by its burn!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		} else if (this.status == Status.POISONED && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			if (this.ability == Ability.POISON_HEAL) {
				if (this.currentHP < this.getStat(0)) {
					addAbilityTask(this);
					heal(getHPAmount(1.0/8), this.nickname + " restored HP!");
				}
			} else {
				this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), f, this.nickname + " was hurt by poison!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}	
			}
			
		} else if (this.status == Status.TOXIC && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			if (this.ability == Ability.POISON_HEAL) {
				if (this.currentHP < this.getStat(0)) {
					addAbilityTask(this);
					heal(getHPAmount(1.0/8), this.nickname + " restored HP!");
				}
			} else {
				this.damage(Math.max((this.getStat(0) * 1.0 / 16) * toxic, 1), f, this.nickname + " was hurt by poison!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			}
		}
		if (this.vStatuses.contains(Status.CURSED) && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			this.damage(Math.max(this.getStat(0) * 1.0 / 4, 1), f, this.nickname + " was hurt by the curse!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		}
		if (this.vStatuses.contains(Status.LEECHED) && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN && !f.isFainted()) {
			int hp = (int) Math.max(this.getStat(0) * 1.0 / 8, 1);
			if (hp >= this.currentHP) hp = this.currentHP;
			this.damage(hp, f, f.nickname + " sucked health from " + this.nickname + "!");
			if (f.item == Item.BIG_ROOT) hp *= 1.3;
			f.heal(hp, "");
			if (this.currentHP <= 0) {
				this.faint(true, f);
				return;
			}
			
		}
		if (this.ability == Ability.PARASOCIAL && f.ability != Ability.MAGIC_GUARD && f.ability != Ability.SCALY_SKIN && !f.isFainted() && (f.vStatuses.contains(Status.POSESSED) || f.status == Status.ASLEEP)) {
			int hp = (int) Math.max(f.getStat(0) * 1.0 / 8, 1);
			if (hp >= f.currentHP) hp = f.currentHP;
			addAbilityTask(this);
			f.damage(hp, this, this.nickname + " sucked health from " + f.nickname + "!");
			if (this.item == Item.BIG_ROOT) hp *= 1.3;
			this.heal(hp, "");
			if (f.currentHP <= 0) {
				f.faint(true, this);
				return;
			}
			
		}
		if (this.vStatuses.contains(Status.NIGHTMARE) && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			if (this.status == Status.ASLEEP) {
				this.damage(Math.max(this.getStat(0) * 1.0 / 4, 1), f, this.nickname + " had a nightmare!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			} else {
				this.vStatuses.remove(Status.NIGHTMARE);
			}
		} if (this.vStatuses.contains(Status.AQUA_RING)) {
			if (this.currentHP < this.getStat(0)) {
				int hp = (int) Math.max(this.getStat(0) * 1.0 / 16, 1);
				if (item == Item.BIG_ROOT) hp *= 1.3;
				heal(hp, this.nickname + " restored HP.");
			}
		} if (field.equals(field.terrain, Effect.GRASSY) && isGrounded()) {
			if (this.currentHP < this.getStat(0)) {
				heal(getHPAmount(1.0/16), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.RAIN_DISH && field.equals(field.weather, Effect.RAIN)) {
			if (this.currentHP < this.getStat(0)) {
				addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.DRY_SKIN && field.equals(field.weather, Effect.RAIN)) {
			if (this.currentHP < this.getStat(0)) {
				addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.ICE_BODY && field.equals(field.weather, Effect.SNOW)) {
			if (this.currentHP < this.getStat(0)) {
				addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.item == Item.LEFTOVERS) {
			if (this.currentHP < this.getStat(0)) {
				heal(getHPAmount(1.0/16), this.nickname + " restored a little HP using its Leftovers!");
			}
		} if (this.item == Item.BLACK_SLUDGE) {
			if (this.type1 == PType.POISON || this.type2 == PType.POISON) {
				if (this.currentHP < this.getStat(0)) {
					heal(getHPAmount(1.0/16), this.nickname + " restored a little HP using its Black Sludge!");
				}
			} else {
				if (this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
					this.damage(Math.max(this.getStat(0) * 1.0 / 16, 1), f, this.nickname + " was hurt by its Black Sludge!");
				}
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			}
		} if (this.vStatuses.contains(Status.WISH) && this.lastMoveUsed != Move.WISH) {
			if (this.currentHP < this.getStat(0)) {
				heal(getHPAmount(1.0/2), this.nickname + "'s wish came true!");
			} else {
				addTask(Task.TEXT, this.nickname + "'s HP is full!");
			}
			this.vStatuses.remove(Status.WISH);
		} if (this.vStatuses.contains(Status.SPUN)) {
			if (this.spunCount == 0) {
				addTask(Task.TEXT, this.nickname + " was freed from wrap!");
				this.vStatuses.remove(Status.SPUN);
			} else {
				if (this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
					this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), f, this.nickname + " was hurt by being wrapped!");
				}
				this.spunCount--;
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			}
		}
		if (field.equals(field.weather, Effect.SANDSTORM) && this.type1 != PType.ROCK && this.type1 != PType.STEEL && this.type1 != PType.GROUND
				&& this.ability != Ability.SAND_FORCE && this.ability != Ability.SAND_RUSH && this.ability != Ability.SAND_VEIL && this.type2 != PType.ROCK 
				&& this.type2 != PType.STEEL && this.type2 != PType.GROUND && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
			this.damage(Math.max(this.getStat(0) * 1.0 / 16, 1), f, this.nickname + " was buffeted by the sandstorm!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		} if (this.ability == Ability.DRY_SKIN && field.equals(field.weather, Effect.SUN)) {
			addAbilityTask(this);
			this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), f, this.nickname + " was hurt!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		}
		
		if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN) && field.weatherTurns > 1) {
			this.damage(Math.max(this.getStat(0) * 1.0 / 8, 1), f, this.nickname + " was hurt!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		}
		
		if (this.perishCount > 0) {
			this.perishCount--;
			addTask(Task.TEXT, this.nickname + "'s perish count fell to " + this.perishCount + "!");
			if (this.perishCount == 0) {
				this.faint(true, f);
				return;
			}
		}
		if (this.vStatuses.contains(Status.LOCKED) && this.outCount == 0 && (this.lastMoveUsed == Move.OUTRAGE || this.lastMoveUsed == Move.PETAL_DANCE || this.lastMoveUsed == Move.THRASH)) {
			this.confuse(false, f);
			this.vStatuses.remove(Status.LOCKED);
		}
		if (this.vStatuses.contains(Status.ENCORED) && --this.encoreCount == 0) {
			this.vStatuses.remove(Status.ENCORED);
			addTask(Task.TEXT, this.nickname + "'s encore ended!");
		}
		if (this.vStatuses.contains(Status.TAUNTED) && --this.tauntCount == 0) {
			this.vStatuses.remove(Status.TAUNTED);
			addTask(Task.TEXT, this.nickname + " shook off the taunt!");
		}
		if (this.vStatuses.contains(Status.TORMENTED) && --this.tormentCount == 0) {
			this.vStatuses.remove(Status.TORMENTED);
			addTask(Task.TEXT, this.nickname + "'s torment ended!");
		}
		if (this.vStatuses.contains(Status.LOCKED) && this.rollCount == 5) {
			this.vStatuses.remove(Status.LOCKED);
		}
		if (this.vStatuses.contains(Status.BONDED)) {
			this.vStatuses.remove(Status.BONDED);
		}
		
		if (this.ability == Ability.SPEED_BOOST && !this.impressive) {
			addAbilityTask(this);
			if (this.statStages[4] != 6) stat(this, 4, 1, f);
		}
		
		if (this.ability == Ability.SHED_SKIN && this.status != Status.HEALTHY) {
			int r = (int)(Math.random() * 3);
			if (r == 0) {
				addAbilityTask(this);
				addTask(Task.STATUS, Status.HEALTHY, nickname + " became healthy!", this);
			}
		}
		
		if (this.ability == Ability.HYDRATION && field.equals(field.weather, Effect.RAIN)) {
			addAbilityTask(this);
			addTask(Task.STATUS, Status.HEALTHY, nickname + " became healthy!", this);
		}
		
		if (this.item == Item.FLAME_ORB && this.status == Status.HEALTHY) {
			this.burn(false, this);
		}
		if (this.item == Item.TOXIC_ORB && this.status == Status.HEALTHY) {
			this.toxic(false, this);
		}
		
		checkBerry(f);
		
		if ((this.status != Status.HEALTHY || this.vStatuses.contains(Status.CONFUSED)) && this.item != null && this.item.isStatusBerry()) {
			eatBerry(this.item, true, f);
		}
		
		this.vStatuses.remove(Status.FLINCHED);
		this.vStatuses.remove(Status.PROTECT);
		this.vStatuses.remove(Status.ENDURE);
		this.vStatuses.remove(Status.SWITCHING);
		boolean result = this.vStatuses.remove(Status.SWAP);
		if (result) System.out.println(this.nickname + " had swap at the end of the turn (bad)");
	}

	public int getSpeed() {
		double speed = this.getStat(5) * this.asModifier(4);
		if (this.status == Status.PARALYZED) speed *= 0.5;
		if (this.item == Item.CHOICE_SCARF) speed *= 1.5;
		if (this.ability == Ability.UNBURDEN && consumedItem) speed *= 2;
		return (int) speed;
	}
	
	public Pokemon getFaster(Pokemon other, int thisP, int otherP) {
		int speed1 = this.getSpeed();
		if (field.contains(field.playerSide, Effect.TAILWIND)) speed1 *= 2;
		if (checkAbilitySpeedBoost(this.ability)) speed1 *= 2;
		int speed2 = other.getSpeed();
		if (field.contains(field.foeSide, Effect.TAILWIND)) speed2 *= 2;
		if (checkAbilitySpeedBoost(other.ability)) speed2 *= 2;
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
	
	private boolean checkAbilitySpeedBoost(Ability ability) {
		if (field.equals(field.weather, Effect.SUN) && ability == Ability.CHLOROPHYLL) return true;
		if (field.equals(field.weather, Effect.RAIN) && ability == Ability.SWIFT_SWIM) return true;
		if (field.equals(field.weather, Effect.SANDSTORM) && ability == Ability.SAND_RUSH) return true;
		if (field.equals(field.weather, Effect.SNOW) && ability == Ability.SLUSH_RUSH) return true;
		if (field.equals(field.terrain, Effect.ELECTRIC) && ability == Ability.VOLT_VORTEX) return true;
		return false;
	}


	public void confuse(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.type1 == PType.MAGIC || this.type2 == PType.MAGIC) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Magic is immune to confusion)");
			return;
		}
		if (!this.vStatuses.contains(Status.CONFUSED)) {
			this.vStatuses.add(Status.CONFUSED);
			this.confusionCounter = (int)(Math.random() * 4) + 1;
			addTask(Task.TEXT, this.nickname + " became confused!");
			if (this.item == Item.PERSIM_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to cure its confusion!");
				this.vStatuses.remove(Status.CONFUSED);
				this.confusionCounter = 0;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
		
	}
	
	public void sleep(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (isGrounded() && field.equals(field.terrain, Effect.ELECTRIC)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Electric Terrain!");
			return;
		}
		if (this.type1 == PType.PSYCHIC || this.type2 == PType.PSYCHIC) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Psychic is immune to sleep)");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.INSOMNIA) {
				if (announce) {
					addAbilityTask(this);
					addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.ASLEEP;
			this.sleepCounter = (int)(Math.random() * 3) + 1;
			addTask(Task.STATUS, Status.ASLEEP, this.nickname + " fell asleep!", this);
			if (this.item == Item.CHESTO_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its sleep!", this);
				this.status = Status.HEALTHY;
				this.sleepCounter = 0;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
		
	}
	
	public void paralyze(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.type1 == PType.ELECTRIC || this.type2 == PType.ELECTRIC) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.PARALYZED;
			addTask(Task.STATUS, Status.PARALYZED, this.nickname + " was paralyzed!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				addAbilityTask(this);
				foe.paralyze(false, this);
			}
			if (this.item == Item.CHERI_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its paralysis!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}

	public void burn(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.type1 == PType.FIRE || this.type2 == PType.FIRE) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.WATER_VEIL) {
				if (announce) {
					addAbilityTask(this);
					addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.BURNED;
			addTask(Task.STATUS, Status.BURNED, this.nickname + " was burned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				addAbilityTask(this);
				foe.burn(false, this);
			}
			if (this.item == Item.RAWST_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its burn!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void poison(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (foe.ability != Ability.CORROSION && (this.type1 == PType.POISON || this.type2 == PType.POISON || this.type1 == PType.STEEL || this.type2 == PType.STEEL)) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.POISONED;
			addTask(Task.STATUS, Status.POISONED, this.nickname + " was poisoned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				addAbilityTask(this);
				foe.poison(false, this);
			}
			if (this.item == Item.PECHA_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its poison!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void toxic(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (foe.ability != Ability.CORROSION && (this.type1 == PType.POISON || this.type2 == PType.POISON || this.type1 == PType.STEEL || this.type2 == PType.STEEL)) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.TOXIC;
			addTask(Task.STATUS, Status.TOXIC, this.nickname + " was badly poisoned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				addAbilityTask(this);
				foe.toxic(false, this);
			}
			if (this.item == Item.PECHA_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its poison!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void freeze(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		ArrayList<FieldEffect> side = this.playerOwned() ? field.playerSide : field.foeSide;
		if (field.contains(side, Effect.SAFEGUARD)) {
			if (announce) addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.type1 == PType.ICE || this.type2 == PType.ICE) {
			if (announce) addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (field.equals(field.weather, Effect.SUN)) {
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.MAGMA_ARMOR) {
				if (announce) {
					addAbilityTask(this);
					addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.FROSTBITE;
			addTask(Task.STATUS, Status.FROSTBITE, this.nickname + " was frostbitten!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				addAbilityTask(this);
				foe.freeze(false, this);
			}
			if (this.item == Item.ASPEAR_BERRY || this.item == Item.LUM_BERRY) {
				addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its frostbite!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void consumeItem(Pokemon foe) {
		this.item = null;
		this.consumedItem = true;
		if (this.ability == Ability.FULL_FORCE && foe != null) {
			addAbilityTask(this);
			stat(this, 0, 2, foe, true);
		}
	}
	
	public int determineBasePower(Pokemon foe, Move move, boolean first, Pokemon[] team, boolean announce) {
		int bp = 0;
		if (move == Move.ABYSSAL_CHOP) {
		    if (foe.status == Status.PARALYZED) {
		        bp = 140;
		    } else {
		        bp = 70;
		    }
		} else if (move == Move.ACROBATICS) {
		    if (this.item == null) {
		        bp = 110;
		    } else {
		        bp = 55;
		    }
		} else if (move == Move.BRINE) {
			if (foe.currentHP * 1.0 / foe.getStat(0) >= 0.5) {
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
			if (this.id == 1 && this.level == 1 && !trainerOwned()) bp = 80;
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
			if (field.equals(field.terrain, Effect.PSYCHIC) && isGrounded()) {
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
			double hpRatio = this.currentHP * 1.0 / this.getStat(0);
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
				if (announce) this.moveMultiplier *= 2;
			}
			bp = Math.min(160, 40 * this.moveMultiplier);
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
			double weightRatio = foe.weight * 1.0 / this.weight;
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
		} else if (move == Move.KNOCK_OFF) {
			if (foe.item == null) {
				bp = 65;
			} else {
				bp = 98;
			}
		} else if (move == Move.MAGNITUDE) {
			int mag = (int) (Math.random()*100 + 1);
			if (mag <= 5) {
				bp = 10;
				if (announce) addTask(Task.TEXT, "Magnitude 4!");
			} else if (mag > 5 && mag <= 15) {
				bp = 30;
				if (announce) addTask(Task.TEXT, "Magnitude 5!");
			} else if (mag > 15 && mag <= 35) {
				bp = 50;
				if (announce) addTask(Task.TEXT, "Magnitude 6!");
			} else if (mag > 35 && mag <= 65) {
				bp = 70;
				if (announce) addTask(Task.TEXT, "Magnitude 7!");
			} else if (mag > 65 && mag <= 85) {
				bp = 90;
				if (announce) addTask(Task.TEXT, "Magnitude 8!");
			} else if (mag > 85 && mag <= 95) {
				bp = 110;
				if (announce) addTask(Task.TEXT, "Magnitude 9!");
			} else if (mag > 95 && mag <= 100) {
				bp = 150;
				if (announce) addTask(Task.TEXT, "Magnitude 10!");
			}
		} else if (move == Move.RAGE) {
			if (this.lastMoveUsed == Move.RAGE) {
				if (announce) this.moveMultiplier *= 2;
			}
			bp = Math.min(160, 20 * this.moveMultiplier);
		} else if (move == Move.PAYBACK) {
			if (first) {
				bp = 50;
			} else {
				bp = 100;
			}
		} else if (move == Move.RETURN || move == Move.FRUSTRATION) {
			int f = this.happiness;
			if (move == Move.FRUSTRATION) f = 255 - f;
			bp = Math.max(f * 2 / 5, 1);
		} else if (move == Move.REVENGE) {
			if (this.getSpeed() > foe.getSpeed()) {
				bp = 60;
			} else {
				bp = 120;
			}
		} else if (move == Move.ROLLOUT || move == Move.ICE_BALL) {
			bp = (int) (30 * Math.pow(2, this.rollCount-1));
		} else if (move == Move.STORED_POWER) {
			int boosts = 0;
			for (int i = 0; i < this.statStages.length; i++) {
				if (this.statStages[i] > 0) boosts += this.statStages[i];
			}
			bp = 20 + (20 * boosts);
		} else if (move == Move.WAKE$UP_SLAP) {
			bp = 60;
			if (foe.status == Status.ASLEEP) {
				bp = 120;
				if (announce) foe.sleepCounter = 0;
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
		for (Moveslot moveslot : moveset) {
			if (moveslot != null) {
				Move m = moveslot.move;
				if (m == move) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean trainerOwned() {
		return this.trainer != null && !(this.trainer instanceof Player);
	}
	
	public boolean playerOwned() {
		return this.trainer != null && this.trainer instanceof Player;
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
		
		return natureString;
	}
	
	public double[] getNature(int i) {
		double[] result;
		switch (i) {
		case 0:
			result = new double[] {1.0,1.0,1.0,1.0,1.0,0.0};
			break;
		case 1:
			result = new double[] {1.0,1.0,1.0,1.0,1.0,1.0};
			break;
		case 2:
			result = new double[] {1.0,1.0,1.0,1.0,1.0,2.0};
			break;
		case 3:
			result = new double[] {1.0,1.0,1.0,1.0,1.0,3.0};
			break;
		case 4:
			result = new double[] {1.0,1.0,1.0,1.0,1.0,4.0};
			break;
		case 5:
			result = new double[] {1.1,0.9,1.0,1.0,1.0,-1.0};
			break;
		case 6:
			result = new double[] {1.1,1.0,0.9,1.0,1.0,-1.0};
			break;
		case 7:
			result = new double[] {1.1,1.0,1.0,0.9,1.0,-1.0};
			break;
		case 8:
			result = new double[] {1.1,1.0,1.0,1.0,0.9,-1.0};
			break;
		case 9:
			result = new double[] {0.9,1.1,1.0,1.0,1.0,-1.0};
			break;
		case 10:
			result = new double[] {1.0,1.1,0.9,1.0,1.0,-1.0};
			break;
		case 11:
			result = new double[] {1.0,1.1,1.0,0.9,1.0,-1.0};
			break;
		case 12:
			result = new double[] {1.0,1.1,1.0,1.0,0.9,-1.0};
			break;
		case 13:
			result = new double[] {0.9,1.0,1.1,1.0,1.0,-1.0};
			break;
		case 14:
			result = new double[] {1.0,0.9,1.1,1.0,1.0,-1.0};
			break;
		case 15:
			result = new double[] {1.0,1.0,1.1,0.9,1.0,-1.0};
			break;
		case 16:
			result = new double[] {1.0,1.0,1.1,1.0,0.9,-1.0};
			break;
		case 17:
			result = new double[] {0.9,1.0,1.0,1.1,1.0,-1.0};
			break;
		case 18:
			result = new double[] {1.0,0.9,1.0,1.1,1.0,-1.0};
			break;
		case 19:
			result = new double[] {1.0,1.0,0.9,1.1,1.0,-1.0};
			break;
		case 20:
			result = new double[] {1.0,1.0,1.0,1.1,0.9,-1.0};
			break;
		case 21:
			result = new double[] {0.9,1.0,1.0,1.0,1.1,-1.0};
			break;
		case 22:
			result = new double[] {1.0,0.9,1.0,1.0,1.1,-1.0};
			break;
		case 23:
			result = new double[] {1.0,1.0,0.9,1.0,1.1,-1.0};
			break;
		case 24:
			result = new double[] {1.0,1.0,1.0,0.9,1.1,-1.0};
			break;
		default:
			result = new double[] {};
			break;
		}
		return result;
	}


	public JPanel showSummary(Player player, boolean takeItem, JPanel panel) {
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
	    JProgressBar expBar = new JProgressBar();
	    JPanel movesPanel = new JPanel(new GridLayout(2, 2));
	    JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
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
	        	String type = getStatType(i);
	        	stats[i] = new JLabel(type + this.stats[i]);
	        	stats[i].setFont(new Font(stats[i].getFont().getName(), Font.BOLD, 14));
	        	stats[i].setSize(50, stats[i].getHeight());
	        	
	        	if (i != 0) {
	        	    if (this.nature[i - 1] == 1.1) {
	        	        stats[i].setForeground(Color.red.darker().darker());
	        	        stats[i].setText(type + this.stats[i] + " \u2191"); // Up arrow
	        	    } else if (this.nature[i - 1] == 0.9) {
	        	        stats[i].setForeground(Color.blue.darker().darker());
	        	        stats[i].setText(type + this.stats[i] + " \u2193"); // Down arrow
	        	    }
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
	                moveButton.setText(moveset[i].move.toString() + " " + moveset[i].showPP());
	                moveButton.setBackground(moveset[i].move.mtype.getColor());
	                moveButton.setForeground(moveset[i].getPPColor());
	                int index = i;
	                moveButton.addActionListener(e -> {
			            JOptionPane.showMessageDialog(null, moveset[index].move.getMoveSummary(this, null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
	        expBar = new JProgressBar(0, this.expMax);
	        expBar.setForeground(new Color(0, 128, 255));
	        expBar.setValue(exp);
	        expBar.setString(expMax - exp + " points to lv. up");
	        expBar.setStringPainted(true);
	        
	        bottomPanel.add(expBar);
	        bottomPanel.add(statusLabel);
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
	    
    	JButton takeButton = new JButton("N/A");
	    JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JLabel itemLabel = new JLabel("N/A");
	    if (this.item != null) {
	    	takeButton.setText("Take " + item.toString());
	    	itemLabel.setText(item.toString());
	    	takeButton.addActionListener(e -> {
	    		int option = JOptionPane.showOptionDialog(null,
	    				"Would you like to take " + this.nickname + "'s " + this.item + "?",
	    				"Take " + this.item + "?",
	    	            JOptionPane.YES_NO_OPTION,
	    	            JOptionPane.QUESTION_MESSAGE,
	    	            null, null, null);
	    	    if (option == JOptionPane.YES_OPTION) {
	    	    	Item old = this.item;
					player.bag.add(old);
	        		this.item = null;
	        		SwingUtilities.getWindowAncestor(teamMemberPanel).dispose();
	        		if (panel != null) SwingUtilities.getWindowAncestor(panel).dispose();
	    	    }
	    	});
	    	itemPanel.add(new JLabel(new ImageIcon(this.item.getImage())));
	    	if (takeItem) {
	    		itemPanel.add(takeButton);
	    	} else {
	    		itemPanel.add(itemLabel);
	    	}
	    	teamMemberPanel.add(itemPanel);
	    }
	    
	    JPanel happinessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    happinessPanel.add(new JLabel(getHappinessDesc()));
	    teamMemberPanel.add(happinessPanel);
	    
	    Pokemon p = this;
	    happinessPanel.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				String happinessCap = p.happiness >= 255 ? 0 + "" : p.happinessCap + "";
		        JOptionPane.showMessageDialog(null, "Happiness: " + p.happiness + " (" + happinessCap + " remaining)");
            }
		});
	    
	    teamMemberPanel.add(bottomPanel);
	    
	    teamMemberPanel.add(Box.createHorizontalGlue());

	    return teamMemberPanel;
	}
	
	public String getHappinessDesc() {
		if (happiness < 70) {
			return "It's quite wary of you.";
		} else if (happiness >= 70 && happiness < 100) {
			return "It's not used to you yet.";
		} else if (happiness >= 100 && happiness < 130) {
			return "It's warming up to you.";
		} else if (happiness >= 130 && happiness < 160) {
			return "It's quite friendly with you!";
		} else if (happiness >= 160 && happiness < 200) {
			return "It looks very happy!";
		} else if (happiness >= 200 && happiness < 250) {
			return "It really trusts you.";
		} else {
			return "It looks really happy! It loves you a lot.";
		}
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

	public PType determineHPType() {
		int sum = 0;
		for (int i = 0; i < 6; i++) {
			int iv = this.ivs[i];
			int bit = iv % 2;
			sum += bit << i;
		}
		int index = (sum * 18) / 63;
		PType[] types = PType.values();
		return types[++index];
	}
	
	private PType determineWBType() {
		PType result = PType.NORMAL;
		if (field.equals(field.weather, Effect.SUN)) result = PType.FIRE;
		if (field.equals(field.weather, Effect.RAIN)) result = PType.WATER;
		if (field.equals(field.weather, Effect.SNOW)) result = PType.ICE;
		if (field.equals(field.weather, Effect.SANDSTORM)) result = PType.ROCK;
		
		return result;
	}
	
	private PType determineTPType() {
		PType result = PType.NORMAL;
		if (field.equals(field.terrain, Effect.GRASSY)) result = PType.GRASS;
		if (field.equals(field.terrain, Effect.ELECTRIC)) result = PType.ELECTRIC;
		if (field.equals(field.terrain, Effect.PSYCHIC)) result = PType.PSYCHIC;
		if (field.equals(field.terrain, Effect.SPARKLY)) result = PType.MAGIC;
		
		return result;
	}

	public void swapIn(Pokemon foe, boolean hazards) {
		if (this.ability == Ability.DROUGHT && !field.equals(field.weather, Effect.SUN)) {
			addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SUN));
			if (this.item == Item.HEAT_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.DRIZZLE && !field.equals(field.weather, Effect.RAIN)) {
			addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.RAIN));
			if (this.item == Item.DAMP_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.SAND_STREAM && !field.equals(field.weather, Effect.SANDSTORM)) {
			addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SANDSTORM));
			if (this.item == Item.SMOOTH_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.SNOW_WARNING && !field.equals(field.weather, Effect.SNOW)) {
			addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SNOW));
			if (this.item == Item.ICY_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.CLOUD_NINE && field.weather != null) {
			addAbilityTask(this);
			Task t = addTask(Task.WEATHER, "The weather returned to normal!");
            t.setEffect(null);
            field.weather = null;
		} else if (this.ability == Ability.GRASSY_SURGE && !field.equals(field.terrain, Effect.GRASSY)) {
			addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.GRASSY));
			if (this.item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.ELECTRIC_SURGE && !field.equals(field.terrain, Effect.ELECTRIC)) {
			addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
			field.terrainTurns = 5;
		} else if (this.ability == Ability.PSYCHIC_SURGE && !field.equals(field.terrain, Effect.PSYCHIC)) {
			addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.PSYCHIC));
			if (this.item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.SPARKLY_SURGE && !field.equals(field.terrain, Effect.SPARKLY)) {
			addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
			if (this.item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.GRAVITATION) {
			addAbilityTask(this);
			field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		} else if (this.ability == Ability.INTIMIDATE || this.ability == Ability.SCALY_SKIN) {
			addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				addAbilityTask(foe);
				addTask(Task.TEXT, foe.nickname + "'s Attack was not lowered!");
			} else {
				stat(foe, 0, -1, this);
			}
		} else if (this.ability == Ability.TERRIFY) {
			addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				addAbilityTask(foe);
				addTask(Task.TEXT, foe.nickname + "'s Sp. Atk was not lowered!");
			} else {
				stat(foe, 2, -1, this);
			}
		} else if (this.ability == Ability.THREATENING) {
			addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				addAbilityTask(foe);
				addTask(Task.TEXT, foe.nickname + "'s Defense was not lowered!");
			} else {
				stat(foe, 1, -1, this);
			}
		} else if (this.ability == Ability.PICKPOCKET) {
			if (this.item == null && foe.item != null) {
				addAbilityTask(foe);
				addTask(Task.TEXT, this.nickname + " stole the foe's " + foe.item.toString() + "!");
				this.item = foe.item;
				if (!foe.loseItem) foe.lostItem = foe.item;
				foe.item = null;
				this.loseItem = true;
			}
		} else if (this.ability == Ability.MOUTHWATER) {
			if (!foe.vStatuses.contains(Status.TAUNTED)) {
				foe.vStatuses.add(Status.TAUNTED);
			    foe.tauntCount = 4;
				addAbilityTask(this);
				addTask(Task.TEXT, foe.nickname + " was taunted!");
				if (foe.item == Item.MENTAL_HERB) {
					addTask(Task.TEXT, foe.nickname + " cured its taunt using its Mental Herb!");
					foe.vStatuses.remove(Status.TAUNTED);
					foe.consumeItem(this);
				}
			}
		} else if (this.ability == Ability.ANTICIPATION) {
			boolean shuddered = false;
			for (Moveslot moveslot : foe.moveset) {
				if (moveslot != null) {
					Move move = moveslot.move;
					if (move != null) {
						double multiplier = getEffectiveMultiplier(move.mtype);
						
						if (multiplier > 1) shuddered = true;
					}
				}
				if (shuddered) break;
			}
			if (shuddered) {
				addAbilityTask(this);
				addTask(Task.TEXT, nickname + " shuddered!");
			}
		} else if (this.ability == Ability.TRACE) {
			addAbilityTask(this);
			this.ability = foe.ability;
			addTask(Task.TEXT, this.nickname + "'s ability became " + this.ability + "!");
			this.swapIn(foe, false);
		} else if (this.ability == Ability.TALENTED) {
			addAbilityTask(this);
			addTask(Task.TEXT, this.nickname + " copied " + foe.nickname + "'s stat changes!");
			for (int i = 0; i < 7; ++i) {
				if (foe.statStages[i] > 0) {
					stat(this, i, foe.statStages[i], foe);
				}
			}
		}
		if (this.item == Item.AIR_BALLOON) {
			addTask(Task.TEXT, this.nickname + " floated on its Air Balloon!");
		}
		if (hazards && this.item != Item.HEAVY$DUTY_BOOTS) {
			ArrayList<FieldEffect> side = playerOwned() ? field.playerSide : field.foeSide;
			if (field.contains(side, Effect.STEALTH_ROCKS)) {
				double multiplier = getEffectiveMultiplier(PType.ROCK);
				double damage = (this.getStat(0) / 8.0) * multiplier;
				this.damage((int) Math.max(Math.floor(damage), 1), foe);
				addTask(Task.TEXT, "Pointed stones dug into " + this.nickname + "!");
			}
			if (field.contains(side, Effect.SPIKES) && this.isGrounded() && this.ability != Ability.MAGIC_GUARD && this.ability != Ability.SCALY_SKIN) {
				double layers = field.getLayers(side, Effect.SPIKES);
				double damage = (this.getStat(0) / 8.0) * ((layers + 1) / 2);
				this.damage((int) Math.max(Math.floor(damage), 1), foe);
				addTask(Task.TEXT, this.nickname + " was hurt by Spikes!");
			}
			
			if (field.contains(side, Effect.TOXIC_SPIKES) && this.isGrounded()) {
				int layers = field.getLayers(side, Effect.TOXIC_SPIKES);
				if (layers == 1) this.poison(false, this);
				if (layers == 2) this.toxic(false, this);
				
				if (this.type1 == PType.POISON || this.type2 == PType.POISON) {
					field.remove(side, Effect.TOXIC_SPIKES);
					addTask(Task.TEXT, "The toxic spikes disappeared from " + this.nickname + "'s feet!");
				}
			}
			
			if (field.contains(side, Effect.STICKY_WEBS) && this.isGrounded()) {
				stat(this, 4, -1, foe);
			}
			
			if (this.currentHP <= 0) {
				this.faint(true, foe);
			}
		}
		if (gp.player.p.pokedex[this.id] < 1) gp.player.p.pokedex[this.id] = 1;
		
	}
	
	private void damage(double amt, Pokemon foe) {
		this.damage((int) amt, foe);
	}
	
	private void damage(int amt, Pokemon foe) {
		this.damage(amt, foe, null, "", -1);
	}
	
	private void damage(double amt, Pokemon foe, String message) {
		this.damage((int) amt, foe, null, message, -1);
	}
	
	private void damage(int amt, Pokemon foe, Move move, String message, int damageIndex) {
		this.damage(amt, foe, move, message, damageIndex, false);
	}
	
	private void damage(int amt, Pokemon foe, Move move, String message, int damageIndex, boolean sturdy) {
		this.currentHP -= amt;
		if (sturdy) this.currentHP = 1;
		Task t = null;
		if (damageIndex >= 0) {
			t = createTask(Task.DAMAGE, message, this);
			t.wipe = true;
			t.evo = foe;
			setTask(damageIndex, t);
		} else {
			t = addTask(Task.DAMAGE, message, this);
		}
		
		t.setFinish(this.currentHP < 0 ? 0 : this.currentHP);
		
		if (move != null && currentHP > 0 && move != Move.INCINERATE && move != Move.KNOCK_OFF && move != Move.BUG_BITE && move != Move.PLUCK &&
				move != Move.THIEF && move != Move.COVET) this.checkBerry(foe);
	}

	private void checkBerry(Pokemon foe) {
		if (this.item == null) return;
		if (this.item.getPocket() != Item.BERRY) return;
		double hpRatio = this.currentHP * 1.0 / this.getStat(0);
		if (hpRatio <= 0.25 && this.item.isPinchBerry()) {
			eatBerry(this.item, true, foe);
		} else if (hpRatio <= 0.5 && (this.item == Item.ORAN_BERRY || this.item == Item.SITRUS_BERRY)) {
			eatBerry(this.item, true, foe);
		} else {
			return;
		}
	}

	private void eatBerry(Item berry, boolean consume, Pokemon foe) {
		if (berry.isBerry()) {
			if (berry == Item.WIKI_BERRY) {
				heal(getHPAmount(1.0/3), this.nickname + " ate its " + berry.toString() + " to restore HP!");
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.LIECHI_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 0, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.GANLON_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 1, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.PETAYA_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 2, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.APICOT_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 3, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.SALAC_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 4, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.STARF_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, new Random().nextInt(7), 2, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.MICLE_BERRY) {
				addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 5, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.ORAN_BERRY || berry == Item.SITRUS_BERRY) {
				int healAmt = berry == Item.ORAN_BERRY ? 10 : (this.getStat(0) / 4);
				heal(healAmt, this.nickname + " ate its " + berry.toString() + " to restore HP!");
				if (consume) this.consumeItem(foe);
			}
			
			if (this.status != Status.HEALTHY || this.vStatuses.contains(Status.CONFUSED)) {
				if ((this.status == Status.POISONED || this.status == Status.TOXIC) && (berry == Item.PECHA_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its poison!", this);
					this.status = Status.HEALTHY;
					this.consumeItem(foe);
				}
				if (this.status == Status.BURNED && (berry == Item.RAWST_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its burn!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.PARALYZED && (berry == Item.CHERI_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its paralysis!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.FROSTBITE && (berry == Item.ASPEAR_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its frostbite!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.ASLEEP && (berry == Item.CHESTO_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its sleep!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.vStatuses.contains(Status.CONFUSED) && (berry == Item.PERSIM_BERRY || berry == Item.LUM_BERRY)) {
					addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + " to cure its confusion!", this);
					this.vStatuses.remove(Status.CONFUSED);
					if (consume) this.consumeItem(foe);
				}
			}
		}
	}
	
	private void heal(double amt, String message) {
		Task t = createTask(Task.DAMAGE, message, this);
		currentHP += amt;
		verifyHP();
		if (gp.gameState == GamePanel.BATTLE_STATE) {
			t.setFinish(currentHP);
			gp.battleUI.tasks.add(t);
		}
		
	}

	public double getEffectiveMultiplier(PType mtype) {
		double multiplier = 1.0;
		
		if (getImmune(this, mtype)) {
			multiplier = 0;
		} else {
			PType[] resistances = getResistances(mtype);
	        for (PType resistance : resistances) {
	            if (this.type1 == resistance || this.type2 == resistance) {
	                multiplier /= 2;
	            }
	        }
	        
	        PType[] weaknesses = getWeaknesses(mtype);
	        for (PType weakness : weaknesses) {
	            if (this.type1 == weakness || this.type2 == weakness) {
	                multiplier *= 2;
	            }
	        }
		}
		return multiplier;
	}

	private boolean isGrounded() {
		boolean result = true;
		if (this.type1 == PType.FLYING || this.type2 == PType.FLYING) result = false;
		if (this.item == Item.AIR_BALLOON) result = false;
		if (this.ability == Ability.LEVITATE) result = false;
		if (this.magCount > 0) result = false;
		if (this.vStatuses.contains(Status.SMACK_DOWN)) result = true;
		if (field.contains(field.fieldEffects, Effect.GRAVITY)) result = true;
		return result;
	}
	
	public int displayMoveOptions(Move move, Player p) {
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
	    List<Move> movebankList = this.movebankAsList();
	    for (int i = 0; i < 4; i++) {
	        if (pokemon.moveset[i] != null) {
	            moves[i] = pokemon.moveset[i].move.toString();
	        } else {
	            moves[i] = "";
	        }
	        buttons[i] = new JGradientButton(moves[i]);
	        if (pokemon.moveset[i] != null) buttons[i].setBackground(pokemon.moveset[i].move.mtype.getColor());
	        if (!p.hasTM(pokemon.moveset[i].move) && !movebankList.contains(pokemon.moveset[i].move)) {
	        	buttons[i].setSolidGradient(true);
	        }
	        
	        if (moves[i].equals("")) {
	            buttons[i].setEnabled(false);
	        }
	        int index = i;
	        buttons[i].addMouseListener(new MouseAdapter() {
	        	@Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            JOptionPane.showMessageDialog(null, moveset[index].move.getMoveSummary(pokemon, null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
		            JOptionPane.showMessageDialog(null, move.getMoveSummary(pokemon, null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
	
	public ArrayList<Move> movebankAsList() {
		ArrayList<Move> movebankList = new ArrayList<>();
        Node n = this.movebank[0];
        for (int i = 0; i < this.movebank.length; i++) {
        	n = movebank[i];
        	while (n != null) {
        		movebankList.add(n.data);
        		n = n.next;
        	}
        }
        return movebankList;
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
	
	public double getWeight() {
		return weights[id - 1];
	}
	
	public int getCatchRate() {
		return catch_rates[id - 1];
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
	
	public boolean arrayEquals(int[] arr1, int[] arr2) {
        if (arr1 == arr2) return true;
        if (arr1 == null || arr2 == null) return false;
        if (arr1.length != arr2.length) return false;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }

        return true;
    }
	
	public boolean arrayGreaterOrEqual(int[] arr1, int[] arr2) {
//        if (arr1 == arr2) return true;
//        if (arr1 == null || arr2 == null) return false;
//        if (arr1.length != arr2.length) return false;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] < arr2[i]) {
            	//System.out.println(getStatType(i + 1) + "arr1 is less than arr2: " + arr1[i] + " < " + arr2[i]);
                return false;
            }
        }
        //System.out.println(Arrays.toString(arr1) + " >= " + Arrays.toString(arr2));
        return true;
    }
	
//	private boolean arrayEquals(Pokemon[] team, Pokemon[] team2) {
//		if (team == team2) return true;
//		if (team == null || team2 == null) return false;
//		System.out.println("fix arrayequals");
//		return false;
//	}

	public boolean moveUsable(Move move) {
		return getValidMoveset().contains(move);
	}

	public boolean movesetEmpty() {
		return getValidMoveset().size() == 0;
	}
	
	private ArrayList<Move> getValidMoveset() {
		ArrayList<Move> validMoves = new ArrayList<>();
		for (Moveslot moveslot : moveset) {
			if (moveslot != null) {
				Move m = moveslot.move;
				if ((vStatuses.contains(Status.TORMENTED) && m == this.lastMoveUsed) || (vStatuses.contains(Status.MUTE) && Move.getSound().contains(m)) || (moveslot.currentPP == 0) 
						|| (this.item != null && this.item.isChoiceItem() && this.lastMoveUsed != null && this.lastMoveUsed != m) || (this.item == Item.ASSAULT_VEST && m.cat == 2)) {
	            	// nothing: don't add
	            } else {
	            	validMoves.add(m);
	            }
			}
		}
		
		if (vStatuses.contains(Status.TAUNTED)) validMoves.removeIf(mo -> mo != null && mo.cat == 2 && mo != Move.METRONOME);
		
		return validMoves;
	}

	@Override
	public Pokemon clone() {
	    Pokemon clonedPokemon = new Pokemon(1, 0, true, false);
	    
	    // Clone id fields
	    clonedPokemon.id = this.id;
	    clonedPokemon.name = this.name;
	    clonedPokemon.nickname = this.nickname;
	    
	    // Clone stat fields
	    clonedPokemon.baseStats = this.baseStats.clone();
	    clonedPokemon.stats = this.stats.clone();
	    clonedPokemon.level = this.level;
	    clonedPokemon.statStages = this.statStages.clone();
	    clonedPokemon.ivs = this.ivs.clone();
	    clonedPokemon.nature = this.nature.clone();
	    clonedPokemon.weight = this.weight;
	    clonedPokemon.catchRate = this.catchRate;
	    clonedPokemon.happiness = this.happiness;
	    
	    // Clone type fields
	    clonedPokemon.type1 = this.type1;
	    clonedPokemon.type2 = this.type2;
	    
	    // Clone ability fields
	    clonedPokemon.ability = this.ability;
	    clonedPokemon.abilitySlot = this.abilitySlot;
	    
	    // Clone move fields
	    clonedPokemon.movebank = this.movebank.clone();
	    clonedPokemon.moveset = this.moveset.clone();
	    
	    // Clone status fields
	    clonedPokemon.status = this.status;
	    clonedPokemon.vStatuses = new ArrayList<>(this.vStatuses);
	    
	    // Clone xp fields
	    clonedPokemon.exp = this.exp;
	    clonedPokemon.expMax = this.expMax;
	    
	    // Clone hp fields
	    clonedPokemon.currentHP = this.currentHP;
	    clonedPokemon.fainted = this.fainted;
	    
	    // Clone item fields
		clonedPokemon.item = this.item;
		clonedPokemon.loseItem = this.loseItem;
		clonedPokemon.lostItem = this.lostItem;
	    
	    // Clone counter fields
	    clonedPokemon.confusionCounter = this.confusionCounter;
	    clonedPokemon.sleepCounter = this.sleepCounter;
	    clonedPokemon.perishCount = this.perishCount;
	    clonedPokemon.magCount = this.magCount;
	    clonedPokemon.moveMultiplier = this.moveMultiplier;
	    clonedPokemon.spunCount = this.spunCount;
	    clonedPokemon.outCount = this.outCount;
	    clonedPokemon.rollCount = this.rollCount;
	    clonedPokemon.encoreCount = this.encoreCount;
	    clonedPokemon.tauntCount = this.tauntCount;
	    clonedPokemon.tormentCount = this.tormentCount;
	    clonedPokemon.toxic = this.toxic;
	    clonedPokemon.headbuttCrit = this.headbuttCrit;
	    clonedPokemon.happinessCap = this.happinessCap;
	    
	    // Clone boolean fields
	    clonedPokemon.impressive = this.impressive;
	    clonedPokemon.battled = this.battled;
	    clonedPokemon.success = this.success;
	    clonedPokemon.consumedItem = this.consumedItem;
	    
	    // Clone battle fields
	    clonedPokemon.lastMoveUsed = this.lastMoveUsed;
	    clonedPokemon.slot = this.slot;

	    clonedPokemon.trainer = this.trainer;
	    
	    // Clone Image fields
	    clonedPokemon.sprite = this.sprite;
	    clonedPokemon.frontSprite = this.frontSprite;
	    clonedPokemon.backSprite = this.backSprite;
	    clonedPokemon.miniSprite = this.miniSprite;
	    
	    return clonedPokemon;
	}

	public static String getStatType(int i) {
		String type = "";
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
	    	case 6:
	    		type = "Acc ";
	    		break;
	    	case 7:
	    		type = "Eva ";
	    		break;
			default:
				type = "ERROR ";
				break;
		}
		return type;
	}

	public void setNickname() {
		nickname = JOptionPane.showInputDialog(null, "Would you like to nickname " + name + "?");
	    if (nickname == null || nickname.trim().isEmpty()) nickname = name;
	    while (nickname.length() > 12) {
	    	JOptionPane.showMessageDialog(null, "Nickname must be no greater than 12 characters.");
	    	nickname = JOptionPane.showInputDialog(null, "Would you like to nickname " + name + "?");
	    	if (nickname == null || nickname.trim().isEmpty()) nickname = name;
	    }
	}
	
	public boolean canEvolve() {
		return canEvolve(this.id);
	}
	
	public boolean canEvolve(int id) {
		return getEvolveString(id) != null;
	}
	
	public String getEvolveString() {
		return getEvolveString(this.id);
	}
	
	public String getEvolveString(int id) {
		switch (id) {
		case 1: return "Twigle -> Torrged (lv. 18)";
		case 2: return "Torrged -> Tortugis (lv. 36)";
		case 4: return "Lagma -> Maguide (lv. 16)";
		case 5: return "Maguide -> Magron (lv. 36)";
		case 7: return "Lizish -> Iguaton (lv. 17)";
		case 8: return "Iguaton -> Dragave (lv. 36)";
		case 10: return "Hummingspark -> Flashclaw (lv. 17)";
		case 11: return "Flashclaw -> Majestiflash (lv. 36)";
		case 13: return "Pigo -> Pigonat (lv. 17)";
		case 14: return "Pigonat -> Pigoga (lv. 32)";
		case 16: return "Hammo -> HammyBoy (Knows Rollout)";
		case 17: return "HammyBoy -> Hamthorno (lv. up in Shadow Ravine Heart)";
		case 19: return "Sheabear -> Dualbear (lv. 20)";
		case 20: return "Dualbear -> Spacebear (lv. 40)";
		case 22: return "Bealtle -> Centatle (lv. 18)";
		case 23: return "Centatle -> Curlatoral (lv. 32)\nCentatle -> Millistone (lv. up in Shadow Ravine)";
		case 26: return "Sapwin -> Treewin (lv. 28)";
		case 27: return "Treewin -> Winagrow (Leaf Stone)";
		case 29: return "Budew -> Roselia (>= 160 happiness)";
		case 30: return "Roselia -> Roserade (Dawn Stone)";
		case 32: return "Sewaddle -> Swadloon (lv. 20)";
		case 33: return "Swadloon -> Leavanny (>= 160 happiness)";
		case 35: return "Grubbin -> Charjabug (lv. 20)";
		case 36: return "Charjabug -> Vikavolt (lv. up in Electric Tunnel)";
		case 38: return "Busheep -> Ramant (Valiant Gem)\nBusheep -> Bushewe (Petticoat Gem)";
		case 41: return "Bugik -> Swordik (lv. 15)";
		case 42: return "Swordik -> Ninjakik (lv. 39)";
		case 44: return "Lotad -> Lombre (lv. 16)";
		case 45: return "Lombre -> Ludicolo (Leaf Stone)";
		case 48: return "Rocky -> Boulder (lv. 22)";
		case 49: return "Boulder -> Blaster (Knows Rock Blast)\nBoulder -> Crystallor (lv. up in Electric Tunnel)";
		case 52: return "Carinx -> Carinator (lv. 30)";
		case 53: return "Carinator -> Cairnasaur (lv. up in Shadow Ravine)";
		case 55: return "Pebblepup -> Boulderoar (lv. 34)";
		case 57: return "Fightorex -> Raptorex (lv. 36)";
		case 59: return "Kleinowl -> Hootowl (>= 160 happiness)";
		case 62: return "Snom -> Frosmoth (Ice Stone)";
		case 64: return "Grondor -> Bipedice (Ice Stone)";
		case 66: return "Tricerpup -> Tricercil (lv. 48)";
		case 68: return "Spheal -> Sealeo (lv. 32)";
		case 69: return "Sealeo -> Walrein (lv. 44)";
		case 71: return "Froshrog -> Bouncerog (lv. 32)";
		case 73: return "Bugop -> Opwing (lv. 30)";
		case 75: return "Hatenna -> Hattrem (lv. 32)";
		case 76: return "Hattrem -> Hatterene (lv. 42)";
		case 78: return "Otterpor -> Psylotter (lv. 30)";
		case 80: return "Florline -> Florlion (lv. 40)";
		case 82: return "Psycorb -> Psyballs (lv. 32)";
		case 83: return "Psyballs -> Psycorboratr (lv. 52)";
		case 85: return "Ralts -> Kirlia (lv. 18)";
		case 86: return "Kirlia -> Gardevoir (lv. 30)\nKirlia -> Gallade (Valiant Gem)";
		case 90: return "Inkay -> Malamar (lv. 30)";
		case 92: return "Flameruff -> Barkflare (lv. 35)";
		case 94: return "Iglite -> Blaxer (lv. 16)";
		case 95: return "Blaxer -> Pyrator (lv. 36)";
		case 98: return "Flamehox -> Fireshard (lv. 35)";
		case 99: return "Fireshard -> Blastflames (lv. 55)";
		case 101: return "Tiowoo -> Magwoo (lv. 20)";
		case 102: return "Magwoo -> Lafloo (lv. 40)";
		case 104: return "Houndour -> Houndoom (lv. 24)";
		case 106: return "Sparkdust -> Splame (lv. 25)";
		case 108: return "Sparkitten -> Fireblion (Valiant Gem)\nSparkitten -> Flamebless (Petticoat Gem)";
		case 111: return "Shookwat -> Wattwo (lv. 30)";
		case 112: return "Wattwo -> Megawatt (lv. up in Electric Tunnel)";
		case 114: return "Elelamb -> Electroram (lv. 28)";
		case 115: return "Electroram -> Superchargo (lv. 48)";
		case 117: return "Twigzap -> Shockbrach (lv. 19)";
		case 118: return "Shockbranch -> Thunderzap (Leaf Stone)";
		case 120: return "Magie -> Cumin (lv. 30)";
		case 121: return "Cumin -> Cinneroph (>= 250 happiness)";
		case 123: return "Vupp -> Vinnie (lv. 30)";
		case 124: return "Vinnie -> Suvinero (>= 250 happiness)";
		case 126: return "Whiskie -> Whiskers (lv. 30)";
		case 127: return "Whiskers -> Whiskeroar (>= 250 happiness)";
		case 129: return "Nincada -> Ninjask (lv. 20)";
		case 132: return "Sheltor -> Shelnado (lv. up in Mindagan Lake)";
		case 134: return "Lilyray -> Daray (>= 160 happiness)";
		case 135: return "Daray -> Spinaquata (lv. 30)";
		case 137: return "Magikarp -> Gyarados (lv. 20)";
		case 139: return "Staryu -> Starmie (lv. up in Mindagan Lake)";
		case 141: return "Ali -> Batorali (Dusk Stone)";
		case 143: return "Posho -> Shomp (>= 250 happiness)";
		case 144: return "Shomp -> Poshorump (lv. up in Mindagan Lake)";
		case 146: return "Binacle -> Barbaracle (lv. 39)";
		case 148: return "Durfish -> Dompster (lv. up in Mindagan Lake)";
		case 151: return "Ekans -> Arbok (lv. 22)";
		case 153: return "Zubat -> Golbat (lv. 22)";
		case 154: return "Golbat -> Crobat (>= 160 happiness)";
		case 156: return "Poof -> Hast (lv. 32)";
		case 158: return "Poov -> Grust (lv. 30)";
		case 160: return "Cluuz -> Zurrclu (Dusk Stone)";
		case 161: return "Zurrclu -> Zurroaratr (>= 250 happiness)";
		case 163: return "Timburr -> Gurdurr (lv. 25)";
		case 164: return "Gurdurr -> Conkeldurr (lv. 36)";
		case 166: return "Rhypo -> Rhynee (lv. 30)";
		case 167: return "Rhynee -> Rhypolar (lv. 50)";
		case 169: return "Diggie -> Drillatron (lv. 33)";
		case 171: return "Wormite -> Wormbot (lv. 20)";
		case 172: return "Wormbot -> Wormatron (lv. 48)";
		case 174: return "Cleffa -> Clefairy (>= 160 happiness)";
		case 175: return "Clefairy -> Clefable (Dawn Stone)";
		case 177: return "Minishoo -> Glittleshoo (Dawn Stone)";
		case 179: return "Zorua -> Zoroark (lv. 30)";
		case 181: return "Droid -> Armoid (lv. 25)";
		case 182: return "Armoid -> Soldrota (lv. 50)";
		case 184: return "Tinkie -> Shawar (lv. 26)";
		case 185: return "Shawar -> Shaboom (lv. 50)";
		case 187: return "Dragee -> Draga (lv. 36)";
		case 188: return "Draga -> Drageye (lv. 55)";
		case 190: return "Blobmo -> Nebulimb (lv. 40)";
		case 191: return "Nebulimb -> Galactasolder (lv. 60)";
		case 193: return "Consodust -> Cosmocrash (Ice Stone)";
		case 195: return "Rockmite -> Stellarock (lv. up in Shadow Ravine Heart)";
		case 197: return "Poof-E -> Hast-E (lv. 32)";
		case 199: return "Droid-E -> Armoid-E (lv. 25)";
		case 200: return "Armoid-E -> Soldrota-E (lv. 50)";
		case 202: return "Flamehox-E -> Fireshard-E (lv. 35)";
		case 203: return "Fireshard-E -> Blastflames-E (lv. 55)";
		case 205: return "Rocky-E -> Boulder-E (lv. 22)";
		case 206: return "Boulder-E -> Blaster-E (Knows Rock Blast)\nBoulder-E -> Crystallor-E (lv. up in Electric Tunnel)";
		case 209: return "Magikarp-E -> Gyarados-E (lv. 20)";
		case 211: return "Shockfang -> Electrocobra (lv. 40)";
		case 213: return "Nightrex -> Shadowsaur (lv. 40)";
		case 215: return "Durfish-S -> Dompster-S (Dusk Stone)";
		case 217: return "Wormite-S -> Wormbot-S (lv. 20)";
		case 218: return "Wormbot-S -> Wormatron-S (lv. 45)";
		case 220: return "Cluuz-S -> Zurrclu-S (Dusk Stone)";
		case 221: return "Zurrclu-S -> Zurroaratr-S (>= 250 happiness)";
		case 223: return "Iglite-S -> Blaxer-S (lv. 16)";
		case 224: return "Blaxer-S -> Pyrator-S (lv. 36)";
		case 226: return "Ekans-S -> Arbok-S (lv. 32)";
		case 238: return "Scraggy -> Scrafty (lv. 39)";
		case 239: return "Scrafty -> Scraftagon (>= 5 Headbutt Crits in Trainer Battles)";
		default:
			return null;
		}
	}

	public boolean isTrapped() {
		if (this.vStatuses.contains(Status.SPUN) || this.vStatuses.contains(Status.CHARGING) || this.vStatuses.contains(Status.RECHARGE) || this.vStatuses.contains(Status.LOCKED) || this.vStatuses.contains(Status.SEMI_INV)) {
			return true;
		}
		if ((this.vStatuses.contains(Status.NO_SWITCH) || this.vStatuses.contains(Status.TRAPPED)) && this.item != Item.SHED_SHELL) return true;
		return false;
	}
	
	public ImageIcon getFaintedIcon() {
        ImageFilter grayFilter = new GrayFilter(true, 25);
        ImageFilter opacityFilter = new RGBImageFilter() {
            // Modify the alpha value to achieve opacity
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb >> 24) & 0xFF; // Extract alpha value
                if (alpha == 0) {
                    return rgb; // Leave transparent pixels unchanged
                } else {
                    return (rgb & 0x00FFFFFF) | (128 << 24); // Apply opacity to visible pixels
                }
            }
        };

        ImageProducer producer = new FilteredImageSource(getMiniSprite().getSource(), grayFilter);
        Image grayImage = Toolkit.getDefaultToolkit().createImage(producer);

        // Apply the opacity filter
        ImageProducer opacityProducer = new FilteredImageSource(grayImage.getSource(), opacityFilter);
        Image finalImage = Toolkit.getDefaultToolkit().createImage(opacityProducer);

        return new ImageIcon(finalImage);
    }
	
	public Image getFaintedSprite() {
		ImageFilter grayFilter = new GrayFilter(true, 25);
        ImageFilter opacityFilter = new RGBImageFilter() {
            // Modify the alpha value to achieve opacity
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb >> 24) & 0xFF; // Extract alpha value
                if (alpha == 0) {
                    return rgb; // Leave transparent pixels unchanged
                } else {
                    return (rgb & 0x00FFFFFF) | (128 << 24); // Apply opacity to visible pixels
                }
            }
        };

        ImageProducer producer = new FilteredImageSource(getSprite().getSource(), grayFilter);
        Image grayImage = Toolkit.getDefaultToolkit().createImage(producer);

        // Apply the opacity filter
        ImageProducer opacityProducer = new FilteredImageSource(grayImage.getSource(), opacityFilter);
        Image finalImage = Toolkit.getDefaultToolkit().createImage(opacityProducer);
        
        return finalImage;
	}

	public void setTrainer(Trainer trainer) {
		this.trainer = trainer;
		
	}

	public int checkQuickClaw(int priority) {
		int result = priority;
		if (item == Item.QUICK_CLAW) {
			Random rand = new Random();
			int num = rand.nextInt(10);
			if (num < 2) {
				result++;
				addTask(Task.TEXT, this.nickname + "'s Quick Claw let it act first!");
			}
		}
		return result;
	}

	public int checkCustap(int priority, Pokemon foe) {
		int result = priority;
		if (item == Item.CUSTAP_BERRY && currentHP <= getStat(0) * 1.0 / 4) {
			result++;
			addTask(Task.TEXT, this.nickname + " ate its Custap Berry and could act first!");
			consumeItem(foe);
		}
		return result;
	}
	
	public static Task createTask(int text, String string) {
		return createTask(text, string, null);
	}
	
	public static Task createTask(int text, String string, Pokemon p) {
		Pokemon dummy = new Pokemon(1, 1, false, false);
		return dummy.new Task(text, string, p);
	}
	
	public static Task addTask(int text, String string) {
		return addTask(text, string, null);
	}
	
	public static Task addTask(int text, Status status, String string, Pokemon p) {
		Task t = addTask(text, string, p);
		t.status = status;
		return t;
	}
	
	public static Task addTask(int text, String string, Pokemon p) {
		if (gp != null && gp.gameState == GamePanel.BATTLE_STATE) {
			Task t = createTask(text, string, p);
			gp.battleUI.tasks.add(t);
			return t;
		} else if (gp != null && (gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE)) {
			Task t = createTask(text, string, p);
			gp.ui.tasks.add(t);
			return t;
		} else {
			if (text == Task.TEXT) {
				gp.ui.showMessage(string);
			} else if (text == Task.NICKNAME) {
				p.setNickname();
			}
			return null;
		}
	}
	
	public static void addEvoTask(Pokemon p, Pokemon result) {
		Task t = addTask(Task.EVO, p.nickname + " is evolving!\nDo you want to evolve your " + p.nickname + "?", p);
		t.evo = result;
	}
	
	public void addAbilityTask(Pokemon p) {
		Task t = addTask(Task.ABILITY, "[" + p.nickname + "'s " + p.ability + "]:", p);
		t.setAbility(p.ability);
	}
	
	public static void addSwapInTask(Pokemon p, int start) {
		String message = p.playerOwned() ? "Go! " + p.nickname + "!" : p.trainer.getName() + " sends out " + p.nickname + "!";
		Task t = addTask(Task.SWAP_IN, message, p);
		if (t != null) {
			t.start = start;
			t.status = p.status;
		}
	}
	
	public static void addSwapInTask(Pokemon p) {
		if (gp.gameState != GamePanel.BATTLE_STATE) return;
		addSwapInTask(p, -1);
	}
	
	public static void addSwapOutTask(Pokemon p) {
		String message = p.playerOwned() ? p.nickname + ", come back!" : p.trainer.getName() + " withdrew " + p.nickname + "!";
		addTask(Task.SWAP_OUT, message, p);
	}
	
	public static void setTask(int index, Task task) {
		gp.battleUI.tasks.set(index, task);
	}
	
	public static Task getTask(int index) {
		return gp.battleUI.tasks.get(index);
	}
	
	public static void insertTask(Task t, int index) {
		if (gp.gameState == GamePanel.BATTLE_STATE) {
			gp.battleUI.tasks.add(index, t);
		} else {
			gp.ui.tasks.add(index, t);
		}
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Player getPlayer() {
		if (this.playerOwned()) {
			return (Player) this.trainer;
		} else {
			throw new IllegalStateException("calling object is not player owned");
		}
	}
	
	public boolean getCapture(Pokemon foe, Entry ball) {
		Random rand = new Random();
        double ballBonus = 0;
        
        if (ball.getItem() == Item.POKEBALL) {
        	this.getPlayer().bag.remove(Item.POKEBALL);
            ballBonus = 1;
        } else if (ball.getItem() == Item.GREAT_BALL) {
            this.getPlayer().bag.remove(Item.GREAT_BALL);
            ballBonus = 1.5;
        } else if (ball.getItem() == Item.ULTRA_BALL) {
        	this.getPlayer().bag.remove(Item.ULTRA_BALL);
            ballBonus = 2;
        }
        
        int quotient = 3 * foe.getStat(0) - 2 * foe.currentHP;
        double modQuotient = quotient * foe.catchRate * ballBonus;
        double catchRate = modQuotient / (3 * foe.getStat(0));
        
        double statusBonus = 1;
        if (foe.status != Status.HEALTHY) statusBonus = 1.5;
        if (foe.status == Status.ASLEEP) statusBonus = 2;
        
        catchRate *= statusBonus;
        int modifiedCatchRate = (int) Math.round(catchRate);
        
        int randomValue = rand.nextInt(255);
        
        gp.battleUI.ball = gp.player.p.getBall(ball);
        gp.battleUI.balls = gp.player.p.getBalls();
        
        return randomValue <= modifiedCatchRate;
	}
	
	/**
	 * 
	 * @param item
	 * @return -1 if N/A, 0 if false, 1 if true, and 2 if already learned
	 */
	public int canUseItem(Item item) {
		if (item == null) return -1;
		if (item.isTM()) {
			boolean learnable = item.getLearned(this);
	        boolean learned = this.knowsMove(item.getMove());
	        if (!learnable) {
	        	return 0;
	        } else if (learned) {
	        	return 2;
	        } else {
	        	return 1;
	        }
		} else if (item.isEvoItem()) {
			return item.getEligible(this.id) ? 1 : 0; 
		}
		switch (item) {
		case ABILITY_CAPSULE:
			Pokemon test = new Pokemon(id, 1, false, false);
	        test.setAbility(1 - abilitySlot);
	        return ability == test.ability ? 0 : 1;
		case ABILITY_PATCH:
			if (this.abilitySlot == 2) return 1;
			Pokemon test2 = this.clone();
			test2.setAbility(2);
			return test2.ability == this.ability || test2.ability == Ability.NULL ? 0 : 1;
		default:
			return -1;
		}
	}

	public ArrayList<String> getStatusLabels() {
		ArrayList<String> result = new ArrayList<>();
		for (Status s : vStatuses) {
	    	result.add(s.toString());
	    }
		if (magCount > 0) {
			result.add("Magnet Rise");
		}
		if (perishCount > 0) {
			result.add("Perish in " + perishCount);
		}
		return result;
	}
	
	public void update() {
		baseStats = getBaseStats();
		setAbility(abilitySlot);
		setMoveBank();
		setTypes();
		setSprites();
	}
	
	public void setSprites() {
		sprite = setSprite();
		frontSprite = setFrontSprite();
		backSprite = setBackSprite();
		miniSprite = setMiniSprite();
	}
	public static void writeInfoToCSV(String filePath) {
	    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
	        for (int i = 1; i <= MAX_POKEMON; i++) {
	            StringBuilder sb = new StringBuilder();
	            Pokemon p = new Pokemon(i, 1, false, true);
	            sb.append("|").append(padString(p.name, 13));
	            sb.append("|").append(padString(p.type1.superToString(), 8));
	            sb.append("|").append(padString(p.type2 == null ? "null" : p.type2.superToString(), 8));
	            sb.append("|").append(padString(p.ability.superToString(), 18));
	            p.setAbility(1);
	            sb.append("|").append(padString(p.ability.superToString(), 18));
	            sb.append("|").append(padString(Arrays.toString(p.baseStats), 30));
	            sb.append("|").append(padString(String.valueOf(p.weight), 6));
	            sb.append("|").append(padString(String.valueOf(p.catchRate), 3)).append("|");
	            writer.println(sb.toString());
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private static String padString(String str, int length) {
	    if (str.length() >= length) {
	        return str;
	    } else {
	        StringBuilder padded = new StringBuilder(str);
	        for (int i = str.length(); i < length; i++) {
	            padded.append(" ");
	        }
	        return padded.toString();
	    }
	}    
	
	public static void readInfoFromCSV() {
        Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/info.csv"));
        for (int i = 0; i < MAX_POKEMON; i++) {
        	String line = scanner.nextLine();
        	String[] tokens = line.split("\\|");
        	names[i] = tokens[1].trim();
        	types[i][0] = PType.valueOf(tokens[2].trim());
        	types[i][1] = tokens[3].trim().equals("null") ? null : PType.valueOf(tokens[3].trim());
        	abilities[i][0] = Ability.valueOf(tokens[4].trim());
        	abilities[i][1] = Ability.valueOf(tokens[5].trim());
        	abilities[i][2] = Ability.valueOf(tokens[6].trim());
        	String[] baseStatsStr = tokens[7].trim().replaceAll("\\[|\\]", "").split(", ");
        	for (int j = 0; j < 6; j++) {
                base_stats[i][j] = Integer.parseInt(baseStatsStr[j]);
            }
        	weights[i] = Double.parseDouble(tokens[8].trim());
        	catch_rates[i] = Integer.parseInt(tokens[9].trim());
        }
    }

	public static String getFormattedID(int id) {
		String result = id + "";
		while (result.length() < 3) result = "0" + result;
		result = "#" + result;
		return result;
	}
}