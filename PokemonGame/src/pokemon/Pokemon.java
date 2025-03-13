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
import java.util.NoSuchElementException;
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
import util.Pair;

public class Pokemon implements RoleAssignable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7087373480262097882L;
	
	public static Field field;
	public static GamePanel gp;
	public static final int MAX_POKEMON = 296;
	
	public static final int POKEDEX_1_SIZE = 249;
	public static final int POKEDEX_METEOR_SIZE = 18;
	public static final int POKEDEX_2_SIZE = 9;
	
	// Database
	private static int[] dexNos = new int[MAX_POKEMON];
	private static String[] names = new String[MAX_POKEMON];
	private static PType[][] types = new PType[MAX_POKEMON][2];
	private static Ability[][] abilities = new Ability[MAX_POKEMON][3];
	private static int[][] base_stats = new int[MAX_POKEMON][6];
	private static double[] weights = new double[MAX_POKEMON];
	private static int[] catch_rates = new int[MAX_POKEMON];
	private static Node[][] movebanks = new Node[MAX_POKEMON][101];
	private static String[] entries = new String[MAX_POKEMON];
	private static boolean[][] tms = new boolean[MAX_POKEMON][107];
	
	private static ArrayList<Integer> rivalIndices = new ArrayList<>();
	
	// Competitive Sets
	public static HashMap<Integer, ArrayList<Set>> sets = new HashMap<>();
	private static ArrayList<Integer> compIDs = new ArrayList<>();
	
	// id fields
	public int id;
	private String name;
	public String nickname;
	
	// stat fields
	public int[] baseStats;
	private int[] stats;
	public int level;
	public int[] statStages;
	public int[] ivs;
	@Deprecated
	public double[] nature;
	public Nature nat;
	public double weight;
	public int catchRate;
	public int happiness;
	public Pokemon tricked;
	
	// type fields
	public PType type1;
	public PType type2;
	
	// ability fields
	public Ability ability;
	public int abilitySlot;
	
	// move fields
	public Moveslot[] moveset;
	
	// status fields
	public Status status;
	public ArrayList<StatusEffect> vStatuses;
	public transient ArrayList<FieldEffect> fieldEffects;
	
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
	public Item ball;
	
	// counter fields
	private int confusionCounter;
	private int sleepCounter;
	public int perishCount;
	public int magCount;
	public int moveMultiplier;
	private int spunCount;
	private int outCount;
	private int rollCount;
	public int metronome;
	public int disabledCount;
	private int encoreCount;
	private int tauntCount;
	private int tormentCount;
	private int healBlockCount;
	private int toxic;
	public int headbuttCrit;
	public int tailCrit;
	public int spaceEat;
	public int happinessCap;
	
	// boolean fields
	public boolean shiny;
	public boolean impressive;
	public boolean battled;
	public boolean success;
	private boolean visible;
	public boolean spriteVisible;
	public boolean consumedItem;
	public boolean illusion;
	
	// battle fields
	public Move lastMoveUsed;
	public Move disabledMove;
	public int slot;
	
	// trainer field
	public Trainer trainer;
	public transient int role;
	
	// Sprite fields
	protected transient BufferedImage sprite;
	protected transient Image frontSprite;
	private transient Image backSprite;
	protected transient BufferedImage miniSprite;
	
	public String metAt;
	
	public Pokemon(int i, int l, boolean o, boolean t) {
		if (gp != null && gp.player.p.random) i = new Random().nextInt(MAX_POKEMON) + 1;
		
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
		setMoves();
		moveMultiplier = 1;
		rollCount = 1;
		metronome = 0;
		
		status = Status.HEALTHY;
		vStatuses = new ArrayList<StatusEffect>();
		
		impressive = true;
		if (t) {
			ivs = new int[] {31, 31, 31, 31, 31, 31};
			nat = Nature.SERIOUS;
			setStats();
			currentHP = this.getStat(0);
		}
		
		if (id == 29 || id == 174 || id == 292) {
			happiness = 110;
		} else if (id == 59) {
			happiness = 0;
		} else {
			happiness = 70;
		}
		
		if (t) {
			happiness = 255;
		} else if (gp != null) {
			happiness = determineHappiness(gp.player.p);
		}
		catchRate = getCatchRate();
		happinessCap = 50;
		
		if (!t) shiny = determineShiny();
		if (t) ball = Item.POKEBALL;
		spriteVisible = true;
		
		if (!t) setSprites();
		
		this.fieldEffects = new ArrayList<>();
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
		nat = pokemon.nat;
		
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
		moveset = pokemon.moveset;
		
		status = pokemon.status;
		vStatuses = new ArrayList<StatusEffect>(pokemon.vStatuses);
		
		shiny = pokemon.shiny;
		impressive = pokemon.impressive;
		trainer = pokemon.trainer;
		lastMoveUsed = pokemon.lastMoveUsed;
		
		happiness = pokemon.happiness;
		headbuttCrit = pokemon.headbuttCrit;
		tailCrit = pokemon.tailCrit;
		spaceEat = pokemon.spaceEat;
		catchRate = getCatchRate();
		happinessCap = pokemon.happinessCap;
		item = pokemon.item;
		loseItem = pokemon.loseItem;
		lostItem = pokemon.lostItem;
		consumedItem = pokemon.consumedItem;
		ball = pokemon.ball;
		
		confusionCounter = pokemon.confusionCounter;
	    sleepCounter = pokemon.sleepCounter;
	    perishCount = pokemon.perishCount;
	    magCount = pokemon.magCount;
	    spunCount = pokemon.spunCount;
	    outCount = pokemon.outCount;
	    rollCount = pokemon.rollCount;
	    encoreCount = pokemon.encoreCount;
	    disabledCount = pokemon.disabledCount;
	    tauntCount = pokemon.tauntCount;
	    tormentCount = pokemon.tormentCount;
	    healBlockCount = pokemon.healBlockCount;
	    toxic = pokemon.toxic;
	    
	    battled = pokemon.battled;
	    success = pokemon.success;
	    illusion = pokemon.illusion;
	    
	    disabledMove = pokemon.disabledMove;
	    slot = pokemon.slot;
		
		metAt = pokemon.metAt;
		
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
	
	private boolean determineShiny() {
		if (true) return false;
		@SuppressWarnings("unused")
		Random random = new Random();
		return random.nextInt() % 512 == 0;
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public static BufferedImage getSprite(int id) {
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
	
	protected BufferedImage setSprite() {
		BufferedImage image = null;
		
		String folder = shiny ? "/shiny/" : "/sprites/";
		
		String imageName = id + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(folder + imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/000.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
	protected Image setFrontSprite() {
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
		Node[] movebank = this.getMovebank();
		for (int i = 1; i <= level && i <= movebank.length; i++) {
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
	    ArrayList<Move> validMoves = this.getValidMoveset();

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
	    
	    Pair<Pokemon, String> switchRsn = null;
	    if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
	    	if (gp.simBattleUI.p1Switch == null) {
	    		gp.simBattleUI.p1Switch = new Pair<>(null, null);
	    		switchRsn = gp.simBattleUI.p1Switch;
	    	} else {
	    		gp.simBattleUI.p2Switch = new Pair<>(null, null);
	    		switchRsn = gp.simBattleUI.p2Switch;
	    	}
	    }
	    
	    Trainer tr = this.trainer;
	    
        // Calculate and store the damage values of each move
        Map<Move, Integer> moveToDamage = new HashMap<>();

        for (Move move : validMoves) {
            int damage = calcWithTypes(foe, move, first, field);
            moveToDamage.put(move, damage);
        }
        
        // Check if there are no valid moves with non-zero PP
        if (moveToDamage.isEmpty()) {
        	// 100% chance to swap in a partner if you can only struggle
        	if (tr.hasValidMembers() && !isTrapped(foe)) {
        		String rsn = "[All valid moves have 0 PP : 100%]";
        		System.out.println(rsn);
        		if (switchRsn != null) {
    				switchRsn.setFirst(this);
        			switchRsn.setSecond(rsn);
    			}
        		this.addStatus(Status.SWAP);
        		return Move.GROWL;
        	} else {
        		return Move.STRUGGLE;
        	}
        }
        
        // Find the maximum damage value
        int maxDamage = Collections.max(moveToDamage.values());
        
        if (tr.hasValidMembers() && !isTrapped(foe)) {
        	// 25% chance to swap in a partner if they resist and you don't
        	ArrayList<Move> damagingMoveset = foe.getDamagingMoveset();
        	boolean only1Move = damagingMoveset.size() == 1;
        	Move check = only1Move ? damagingMoveset.get(0) : foe.lastMoveUsed;
        	if (check != null && check.cat != 2 && !tr.resists(this, foe, check.mtype, check)
        			&& tr.hasResist(foe, check.mtype, check)) {
        		double chance = 25;
        		if (only1Move) {
        			chance *= 2;
        		} else {
        			if (this == this.getFaster(foe, 0, 0)) chance /= 2;
            		if (this.impressive) chance /= 2;
        		}
        		if (checkSecondary((int) Math.round(chance))) {
        			String rsn = "[Partner resists : " + String.format("%.1f", chance) + "%]";
        			System.out.println(rsn);
        			if (switchRsn != null) {
        				switchRsn.setFirst(this);
            			switchRsn.setSecond(rsn);
        			}
                	this.addStatus(Status.SWAP);
                	return Move.SPLASH;
        		}
            }
        	// 70% chance to swap if all of your moves do 0 damage
        	if (maxDamage == 0 && !validMoves.equals(new ArrayList<>(Arrays.asList(new Move[] {Move.METRONOME})))) {
        		double chance = 70;
        		if (this.impressive) chance *= 0.75;
        		if (checkSecondary((int) chance)) {
        			String rsn = "[All moves do 0 damage : " + String.format("%.1f", chance) + "%]";
        			System.out.println(rsn);
        			if (switchRsn != null) {
        				switchRsn.setFirst(this);
            			switchRsn.setSecond(rsn);
        			}
        			this.addStatus(Status.SWAP);
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
        			String rsn = "[Damage i do is 1/5 or less : " + String.format("%.1f", chance) + "%]";
        			System.out.println(rsn);
        			if (switchRsn != null) {
        				switchRsn.setFirst(this);
            			switchRsn.setSecond(rsn);
        			}
        			if (validMoves.contains(Move.U$TURN)) return Move.U$TURN;
        			if (validMoves.contains(Move.VOLT_SWITCH)) return Move.VOLT_SWITCH;
        			if (validMoves.contains(Move.FLIP_TURN)) return Move.FLIP_TURN;
        			if (validMoves.contains(Move.PARTING_SHOT)) return Move.PARTING_SHOT;
        			if (validMoves.contains(Move.BATON_PASS)) return Move.BATON_PASS;
        			this.addStatus(Status.SWAP);
            		return Move.GROWL;
        		}
        	}
        	// 100% chance to swap if perishCount is 1
        	if (this.perishCount == 1) {
        		String rsn = "[Perish in 1 : 100%]";
        		System.out.println(rsn);
        		if (switchRsn != null) {
    				switchRsn.setFirst(this);
        			switchRsn.setSecond(rsn);
    			}
        		this.addStatus(Status.SWAP);
        		return Move.GROWL;
        	}
        	// 10% chance to swap if i'm leech seeded
        	if (this.hasStatus(Status.LEECHED) && checkSecondary(10)) {
        		String rsn = "[Leeched : 10%]";
        		System.out.println(rsn);
        		if (switchRsn != null) {
    				switchRsn.setFirst(this);
        			switchRsn.setSecond(rsn);
    			}
        		this.addStatus(Status.SWAP);
        		return Move.GROWL;
        	}
    		boolean moveKills = false;
    		PType type = null;
    		boolean hasResist = false;
        	for (Move m : foe.getValidMoveset()) {
        		int damage = foe.calcWithTypes(this, m, true, 0, false, field);
        		hasResist = tr.hasResist(foe, m.mtype, m);
        		if (damage >= this.currentHP) {
        			moveKills = true;
        			type = m.mtype;
        			break;
        		}
        	}
        	if (moveKills) {
        		double chance = (this.currentHP * 1.0 / this.getStat(0)) * 100;
        		chance *= 0.6;
        		if (this == this.getFaster(foe, 0, 0)) chance /= 2;
        		if (maxDamage >= foe.currentHP) chance /= 1.5;
        		if (hasResist) {
        			chance *= 1.5;
        		} else {
        			chance /= 5;
        		}
        		if (checkSecondary((int) chance)) {
        			String rsn = "[Enemy kills me : " + String.format("%.1f", chance) + "%]";
        			System.out.println(rsn);
        			if (switchRsn != null) {
        				switchRsn.setFirst(this);
            			switchRsn.setSecond(rsn);
        			}
        			this.addStatus(Status.SWAP);
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
        		if (move.hasPriority(this) || move == Move.FELL_STINGER) {
        			bestMoves.add(move);
        		}
        	}
        	if (move.cat == 2 || Move.treatAsStatus(move, this, foe)) {
        		if (move.accuracy > 100) {
        			Pokemon freshYou = this.clone();
        			freshYou.statStages = new int[freshYou.statStages.length];
        			Pokemon freshFoe = new Pokemon(1, 1, false, false);
        			int[] prevStatsF = freshYou.statStages.clone();
        			freshYou.statusEffect(freshFoe, move, null, null, false);
        			int[] currentStatsF = freshYou.statStages.clone();
        			if (!arrayEquals(prevStatsF, currentStatsF)) {
        				Pokemon you = this.clone();
            			Pokemon foeClone = foe.clone(); // shouldn't matter
            			int[] prevStats = you.statStages.clone();
            			you.statusEffect(foeClone, move, null, null, false);
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
        
        if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
        	if (gp.simBattleUI.p1Moves == null) {
        		gp.simBattleUI.p1Moves = new Pair<>(this, bestMoves);
        	} else {
        		gp.simBattleUI.p2Moves = new Pair<>(this, bestMoves);
        	}
        }
        return bestMoves.get(randomIndex);
    }

	private ArrayList<Move> modifyStatus(ArrayList<Move> bestMoves, Pokemon foe) {
		ArrayList<Move> checkForImmunity = new ArrayList<Move>(bestMoves);
		Collections.shuffle(checkForImmunity);
		for (Move m : checkForImmunity) {
			if (bestMoves.size() > 1 && Trainer.getEffective(foe, this, m.mtype, m, true) == 0 && m.accuracy <= 100) bestMoves.removeIf(m::equals);
		}
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.THUNDER_WAVE) && foe.isType(PType.GROUND)) bestMoves.removeIf(Move.THUNDER_WAVE::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.STICKY_WEB) && field.contains(foe.getFieldEffects(), Effect.STICKY_WEBS)) bestMoves.removeIf(Move.STICKY_WEB::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.STEALTH_ROCK) && field.contains(foe.getFieldEffects(), Effect.STEALTH_ROCKS)) bestMoves.removeIf(Move.STEALTH_ROCK::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FLOODLIGHT) && field.contains(foe.getFieldEffects(), Effect.FLOODLIGHT)) bestMoves.removeIf(Move.FLOODLIGHT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SPIKES) && field.getLayers(foe.getFieldEffects(), Effect.SPIKES) == 3) bestMoves.removeIf(Move.SPIKES::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TOXIC_SPIKES) && field.getLayers(foe.getFieldEffects(), Effect.TOXIC_SPIKES) == 2) bestMoves.removeIf(Move.TOXIC_SPIKES::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.DEFOG)) {
			if (field.getHazards(this.getFieldEffects()).isEmpty() && field.getScreens(foe.getFieldEffects()).isEmpty() && field.terrain == null) {
				bestMoves.removeIf(Move.DEFOG::equals);
			} else {
				bestMoves.add(Move.DEFOG);
				bestMoves.add(Move.DEFOG);
				bestMoves.add(Move.DEFOG);
			}
		}
		
		ArrayList<Move> noRepeat = Move.getNoComboMoves();
		for (Move m : noRepeat) {
			if (bestMoves.size() > 1 && bestMoves.contains(m) && noRepeat.contains(lastMoveUsed)) bestMoves.removeIf(m::equals);
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
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.WILL$O$WISP) && field.equals(field.weather, Effect.SNOW, foe)) bestMoves.removeIf(Move.WILL$O$WISP::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MOLTEN_CONSUME) && field.equals(field.weather, Effect.SNOW, foe)) bestMoves.removeIf(Move.MOLTEN_CONSUME::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FROSTBIND) && field.equals(field.weather, Effect.SUN, foe)) bestMoves.removeIf(Move.FROSTBIND::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.REFLECT) && field.contains(this.getFieldEffects(), Effect.REFLECT)) bestMoves.removeIf(Move.REFLECT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.LIGHT_SCREEN) && field.contains(this.getFieldEffects(), Effect.LIGHT_SCREEN)) bestMoves.removeIf(Move.LIGHT_SCREEN::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.AURORA_VEIL) && (field.contains(this.getFieldEffects(), Effect.AURORA_VEIL) || !field.equals(field.weather, Effect.SNOW))) bestMoves.removeIf(Move.AURORA_VEIL::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.AURORA_GLOW) && (field.contains(this.getFieldEffects(), Effect.AURORA))) bestMoves.removeIf(Move.AURORA_GLOW::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SAFEGUARD) && field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) bestMoves.removeIf(Move.SAFEGUARD::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.LUCKY_CHANT) && field.contains(this.getFieldEffects(), Effect.LUCKY_CHANT)) bestMoves.removeIf(Move.LUCKY_CHANT::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.HAZE) && arrayGreaterOrEqual(new int[] {0, 0, 0, 0, 0, 0, 0}, foe.statStages) && arrayGreaterOrEqual(statStages, new int[] {0, 0, 0, 0, 0, 0, 0})) bestMoves.removeIf(Move.HAZE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.BELLY_DRUM) && this.currentHP < this.getStat(0) / 2) bestMoves.removeIf(Move.HAZE::equals);
		
		if (bestMoves.size() > 1 && (bestMoves.contains(Move.ROOST) || bestMoves.contains(Move.SYNTHESIS) || bestMoves.contains(Move.MOONLIGHT) || bestMoves.contains(Move.MORNING_SUN) ||
				bestMoves.contains(Move.RECOVER) || bestMoves.contains(Move.SLACK_OFF) || bestMoves.contains(Move.WISH) || bestMoves.contains(Move.REST) ||
				bestMoves.contains(Move.LIFE_DEW) || bestMoves.contains(Move.STRENGTH_SAP) || bestMoves.contains(Move.SHORE_UP) || bestMoves.contains(Move.ALCHEMY))) {
			if (this.getStat(0) == this.currentHP) {
				bestMoves.removeAll(Arrays.asList(new Move[] {Move.ROOST, Move.SYNTHESIS, Move.MOONLIGHT, Move.MORNING_SUN, Move.RECOVER, Move.SLACK_OFF, Move.WISH, Move.REST, Move.LIFE_DEW, Move.STRENGTH_SAP, Move.SHORE_UP, Move.ALCHEMY}));
			}
		}
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.HEALING_WISH) && this.trainerOwned() && !this.trainer.hasValidMembers()) bestMoves.removeIf(Move.HEALING_WISH::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.LUNAR_DANCE) && this.trainerOwned() && !this.trainer.hasValidMembers()) bestMoves.removeIf(Move.LUNAR_DANCE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.BATON_PASS) && this.trainerOwned() && !this.trainer.hasValidMembers()) bestMoves.removeIf(Move.BATON_PASS::equals);
		if (bestMoves.contains(Move.TRICK) && this.getItem() == Item.RING_TARGET) bestMoves.add(Move.TRICK);
		if (bestMoves.contains(Move.SWITCHEROO) && this.getItem() == Item.RING_TARGET) bestMoves.add(Move.SWITCHEROO);
		if (bestMoves.contains(Move.TRICK_TACKLE) && this.getItem() == Item.RING_TARGET) bestMoves.add(Move.TRICK_TACKLE);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TRICK_ROOM) && field.contains(field.fieldEffects, Effect.TRICK_ROOM)) bestMoves.removeIf(Move.TRICK_ROOM::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGIC_ROOM) && field.contains(field.fieldEffects, Effect.MAGIC_ROOM)) bestMoves.removeIf(Move.MAGIC_ROOM::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TAILWIND) && field.contains(this.getFieldEffects(), Effect.TAILWIND)) bestMoves.removeIf(Move.TAILWIND::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MUD_SPORT) && field.contains(field.fieldEffects, Effect.MUD_SPORT)) bestMoves.removeIf(Move.MUD_SPORT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.WATER_SPORT) && field.contains(field.fieldEffects, Effect.WATER_SPORT)) bestMoves.removeIf(Move.WATER_SPORT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.GRAVITY) && field.contains(field.fieldEffects, Effect.GRAVITY)) bestMoves.removeIf(Move.GRAVITY::equals);
		
		if (bestMoves.size() > 1 && (bestMoves.contains(Move.AQUA_RING) || bestMoves.contains(Move.INGRAIN)) && this.hasStatus(Status.AQUA_RING)) {
			bestMoves.removeIf(Move.INGRAIN::equals);
			bestMoves.removeIf(Move.AQUA_RING::equals);
		}
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.WORRY_SEED) && foe.ability == Ability.INSOMNIA) bestMoves.removeIf(Move.WORRY_SEED::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SIMPLE_BEAM) && foe.ability == Ability.SIMPLE) bestMoves.removeIf(Move.SIMPLE_BEAM::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.GASTRO_ACID) && foe.ability == Ability.NULL) bestMoves.removeIf(Move.GASTRO_ACID::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FORESTS_CURSE) && foe.isType(PType.GRASS)) bestMoves.removeIf(Move.FORESTS_CURSE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGIC_POWDER) && foe.isType(PType.MAGIC)) bestMoves.removeIf(Move.MAGIC_POWDER::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SOAK) && foe.isType(PType.WATER)) bestMoves.removeIf(Move.SOAK::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.VENOM_DRENCH) && (foe.status != Status.POISONED || foe.status != Status.TOXIC)) bestMoves.removeIf(Move.VENOM_DRENCH::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MEAN_LOOK) && foe.hasStatus(Status.TRAPPED)) bestMoves.removeIf(Move.MEAN_LOOK::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.FOCUS_ENERGY) && this.getStatusNum(Status.CRIT_CHANCE) > 2) bestMoves.removeIf(Move.FOCUS_ENERGY::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.ENCORE) && foe.hasStatus(Status.ENCORED)) bestMoves.removeIf(Move.ENCORE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.DISABLE) && foe.disabledMove != null) bestMoves.removeIf(Move.DISABLE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.NO_RETREAT) && this.hasStatus(Status.NO_SWITCH)) bestMoves.removeIf(Move.NO_RETREAT::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MEMENTO) && ((this.currentHP * 1.0 / this.getStat(0))) > 0.25) bestMoves.removeIf(Move.MEMENTO::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.CURSE) && (this.currentHP * 1.0 / this.getStat(0)) <= 0.55 && this.isType(PType.GHOST)) bestMoves.removeIf(Move.CURSE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.PERISH_SONG) && (this.perishCount > 0 || foe.perishCount > 0)) bestMoves.removeIf(Move.PERISH_SONG::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.NIGHTMARE) && (foe.status != Status.ASLEEP || foe.hasStatus(Status.NIGHTMARE))) bestMoves.removeIf(Move.NIGHTMARE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MAGNET_RISE) && this.magCount > 0) bestMoves.removeIf(Move.MAGNET_RISE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MIRROR_MOVE) && foe.lastMoveUsed == null) bestMoves.removeIf(Move.MIRROR_MOVE::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.TELEPORT) && (this.trainer == null || !this.trainer.hasValidMembers())) bestMoves.removeIf(Move.TELEPORT::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.VANDALIZE) && foe.ability == Ability.NULL) bestMoves.removeIf(Move.VANDALIZE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.ENCORE) && foe.lastMoveUsed == null) bestMoves.removeIf(Move.ENCORE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.MIMIC) && foe.lastMoveUsed == null) bestMoves.removeIf(Move.MIMIC::equals);
		
		if (bestMoves.size() > 1 && bestMoves.contains(Move.DECK_CHANGE) && foe.hasStatus(Status.DECK_CHANGE)) bestMoves.removeIf(Move.DECK_CHANGE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.HEALING_CIRCLE) && field.contains(this.getFieldEffects(), Effect.HEALING_CIRCLE)) bestMoves.removeIf(Move.HEALING_CIRCLE::equals);
		if (bestMoves.size() > 1 && bestMoves.contains(Move.SPELLBIND) && foe.hasStatus(Status.SPELLBIND)) bestMoves.removeIf(Move.HEALING_CIRCLE::equals);
		
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
	
	private boolean isType(PType type) {
		return this.type1 == type || this.type2 == type;
	}
	
	public Node[] getMovebank() {
		return getMovebank(this.id);
	}
	
	public static Node[] getMovebank(int id) {
		return movebanks[id - 1];
	}
	
	public Node getNode() {
		return getNode(this.id, this.level);
	}
	
	public static Node getNode(int id, int level) {
		return movebanks[id - 1][level];
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String name() {
		return this instanceof Egg ? "Egg" : this.name;
	}
	
	public String getName() {
		return getName(id);
	}
	
	public static String getName(int id) {
		return names[id - 1];
	}
	
	public int getDexNo() {
		return getDexNo(id);
	}
	
	public static int getDexNo(int id) {
		return dexNos[id - 1];
	}
	
	public String getDexEntry() {
		return getDexEntry(id);
	}
	
	public static String getDexEntry(int id) {
		return entries[id - 1];
	}
	
	public void setAbility(int slot) {
		ability = abilities[id - 1][slot];
	}
	
	public void setAbility() {
		ability = abilities[id - 1][abilitySlot];
	}
	
	public Ability getAbility(int slot) {
		return abilities[id - 1][slot];
	}
	
	public boolean getLearned(int row, int col) {
		return tms[row][col];
	}
	
	public static boolean[][] getTMTable() {
		return tms;
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
		
		Task t = Task.addTask(Task.LEVEL_UP, this.nickname + " leveled up to " + this.level + "!", this);
		t.setFinish(amt);
		
		ArrayList<Task> check = gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.ui.tasks;
		checkMove(check.size(), this.level);
		this.checkEvo(player, check.size());
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

	public int checkMove(int index, int level) {
	    Node node = getNode(this.id, level);
	    while (node != null) {
	        Move move = node.data;
	        if (level != 0 && this.hasMoveAtLevel(move, 0)) {
	        	node = node.next;
	        	continue;
	        }
	        if (move != null && !this.knowsMove(move)) {
	            boolean learnedMove = false;
	            for (int i = 0; i < 4; i++) {
	                if (this.moveset[i] == null) {
	                    this.moveset[i] = new Moveslot(move);
	                    if (gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE) {
	                    	Task t = Task.createTask(Task.TEXT, this.nickname + " learned " + move.toString() + "!");
		                    Task.insertTask(t, index++);
	                    } else {
	                    	Task.addTask(Task.TEXT, this.nickname + " learned " + move.toString() + "!");
	                    }
	                    
	                    learnedMove = true;
	                    break;
	                }
	            }
	            if (!learnedMove) {
	            	Task t = Task.createTask(Task.MOVE, "", this);
            		t.setMove(move);
	            	if (gp.gameState == GamePanel.BATTLE_STATE || gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE) {
	            		Task.insertTask(t, index++);
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
	    return index;
	}


	private boolean hasMoveAtLevel(Move m, int level) {
		Node n = getNode(this.id, level);
		while (n != null) {
			if (n.data == m) return true;
			n = n.next;
		}
		return false;
	}

	private void checkEvo(Player player, int index) {
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
		} else if (id == 16 && knowsMove(Move.BULK_UP)) {
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
		} else if (id == 78 && level >= 35) {
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
		} else if (id == 102 && level >= 42) {
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
		} else if (id == 134 && happiness >= 160) {
			result = new Pokemon(id + 1, this);
		} else if (id == 135 && level >= 30) {
			result = new Pokemon(id + 1, this);
		} else if (id == 137 && level >= 20) {
			result = new Pokemon(id + 1, this);
		} else if (id == 143 && happiness >= 250) { // ?? maybe stone???? idfk
			result = new Pokemon(id + 1, this);
		} else if (id == 144 && area == 77) {
			result = new Pokemon(id + 1, this);
		} else if (id == 146 && level >= 39) {
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
		} else if (id == 243 && level >= 16) {
            result = new Pokemon(id + 1, this);
		} else if (id == 244 && level >= 36) {
            result = new Pokemon(id + 1, this);
		} else if (id == 251 && level >= 32) {
            result = new Pokemon(id + 1, this);
		} else if (id == 252 && level >= 41) {
            result = new Pokemon(id + 1, this);
		} else if (id == 254 && level >= 32) {
            result = new Pokemon(id + 1, this);
		} else if (id == 255 && level >= 41) {
            result = new Pokemon(id + 1, this);
		} else if (id == 257 && tailCrit >= 5) {
		    result = new Pokemon(id + 1, this);
		} else if (id == 259 && level >= 26) {
            result = new Pokemon(id + 1, this);
		} else if (id == 261 && spaceEat >= 25) {
		    result = new Pokemon(id + 1, this);
		} else if (id == 265 && level >= 42) {
            result = new Pokemon(id + 1, this);
		} else if (id == 267 && level >= 42) {
            result = new Pokemon(id + 1, this);
		} else if (id == 269 && level >= 7) {
            result = new Pokemon(272, this);
		} else if (id == 270 && level >= 10) {
            result = new Pokemon(id + 1, this);
		} else if (id == 272 && level >= 10) {
            result = new Pokemon(id + 1, this);
		} else if (id == 274 && level >= 33) {
            result = new Pokemon(id + 1, this);
		} else if (id == 276 && level >= 33) {
            result = new Pokemon(id + 1, this);
		} else if (id == 279 && level >= 50) {
            result = new Pokemon(id + 1, this);
		} else if (id == 282 && level >= 50) {
            result = new Pokemon(id + 1, this);
		} else if (id == 292 && happiness >= 160) {
            result = new Pokemon(id + 1, this);
		} else if (id == 293 && level >= 18) {
			result = new Pokemon(id + 1, this);
		} else if (id == 295 && level >= 28) {
            result = new Pokemon(id + 1, this);
        }
		
		if (result != null) {
			Task.addEvoTask(this, result, index);
	    }
	}

	public void setStats() {
		double HPnum = (2 * baseStats[0] + ivs[0]) * level;
		stats[0] = (int) (Math.floor(HPnum/100) + level + 10);
		if (id == 131) stats[0] = 1;
		double Atknum = (2 * baseStats[1] + ivs[1]) * level;
		stats[1] = (int) Math.floor((Math.floor(Atknum/100) + 5) * nat.getStat(0));
		double Defnum = (2 * baseStats[2] + ivs[2]) * level;
		stats[2] = (int) Math.floor((Math.floor(Defnum/100) + 5) * nat.getStat(1));
		double SpAnum = (2 * baseStats[3] + ivs[3]) * level;
		stats[3] = (int) Math.floor((Math.floor(SpAnum/100) + 5) * nat.getStat(2));
		double SpDnum = (2 * baseStats[4] + ivs[4]) * level;
		stats[4] = (int) Math.floor((Math.floor(SpDnum/100) + 5) * nat.getStat(3));
		double Spenum = (2 * baseStats[5] + ivs[5]) * level;
		stats[5] = (int) Math.floor((Math.floor(Spenum/100) + 5) * nat.getStat(4));
	}
	
	public int getStat(int type) {
		if (type > 0 && this.hasStatus(Status.DECK_CHANGE)) {
			if (type == 1) {
				type = 3;
			} else if (type == 3) {
				type = 1;
			}
		}
		return type > stats.length ? 0 : this.stats[type];
	}

	public int getBaseStat(int type) {
		return type > baseStats.length ? 0 : this.baseStats[type];
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
	    sb.append(name());
	    if (nickname != null && !nickname.equals(name())) {
	    	sb.append(" (");
		    sb.append(nickname);
		    sb.append(")");
	    }
	    return sb.toString();
	}
	
	public static int[] getBaseStats(int id) {
		return base_stats[id - 1];
	}
	
	public int[] getBaseStats() {
		return getBaseStats(id);
	}
	
	public void moveInit(Pokemon foe, Move move, boolean first) {
		Pokemon faster = first ? this : foe;
		Pokemon slower = faster == this ? foe : this;
		int[] fasterStages = faster.statStages.clone();
		int[] slowerStages = slower.statStages.clone();
		
		move(foe, move, first);
		
		if (faster.getItem() == Item.WHITE_HERB) {
			faster.handleWhiteHerb(fasterStages, slower);
		}
		
		if (faster.getItem() == Item.MIRROR_HERB) {
			faster.handleMirrorHerb(slowerStages, slower);
		}
		
		if (faster.getItem() == Item.EJECT_PACK) {
			faster.handleEjectPack(fasterStages, slower);
		}
		
		if (slower.getItem() == Item.WHITE_HERB) {
			slower.handleWhiteHerb(slowerStages, faster);
		}
		
		if (slower.getItem() == Item.MIRROR_HERB) {
			slower.handleMirrorHerb(fasterStages, faster);
		}
		
		if (slower.getItem() == Item.EJECT_PACK) {
			slower.handleEjectPack(slowerStages, faster);
		}
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
		boolean contact = move.contact;
		boolean sheer = false;
		
		Player player;
		Trainer enemy;
		Pokemon[] team = null;
		
		if (this.playerOwned()) {
			player = this.trainer instanceof Player ? (Player) this.trainer : gp.player.p;
			enemy = foe.trainer;
			team = player.team;
		} else {
			player = foe.trainer instanceof Player ? (Player) foe.trainer : gp.player.p;
			enemy = this.trainer;
			if (trainer != null) team = enemy.team;
		}
		
		if (move != this.lastMoveUsed) this.moveMultiplier = 1;
		
		if (this.getItem() == Item.METRONOME) {
			if (move == lastMoveUsed) {
				this.metronome++;
				if (this.metronome > 5) this.metronome = 5;
			} else {
				this.metronome = 0;
			}
		} else {
			this.metronome = 0;
		}
		
		if (this.hasStatus(Status.TORMENTED) && move == this.lastMoveUsed) {
			Task.addTask(Task.TEXT, this.nickname + " can't use " + move + " after the torment!");
			this.lastMoveUsed = null;
			this.impressive = false;
			this.rollCount = 1;
			this.metronome = 0;
			return;
		}
		
		if (!this.hasStatus(Status.CHARGING) && !this.hasStatus(Status.SEMI_INV) && !this.hasStatus(Status.LOCKED) &&
				!this.hasStatus(Status.ENCORED) && !this.hasStatus(Status.RECHARGE) &&
				!Move.getNoComboMoves().contains(move) && move != Move.STRUGGLE
				&& move != Move.FUSION_BOLT && move != Move.FUSION_FLARE) this.lastMoveUsed = move;
		
		if (move == Move.SUCKER_PUNCH && !first) move = Move.FAILED_SUCKER;
		
		if (move == Move.FAILED_SUCKER) this.lastMoveUsed = Move.SUCKER_PUNCH;
		
		if (this.hasStatus(Status.ENCORED)) {
			move = this.lastMoveUsed;
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
		}
		
		if (this.status == Status.ASLEEP) {
			if (this.sleepCounter > 0) {
				Task.addTask(Task.TEXT, this.nickname + " is fast asleep.");
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
					contact = move.contact;
				} else if (move == Move.SNORE) {
					
				} else {
					this.impressive = false;
					this.lastMoveUsed = null;
					this.removeStatus(Status.LOCKED);
					this.removeStatus(Status.CHARGING);
					this.removeStatus(Status.SEMI_INV);
					this.spriteVisible = true;
					this.rollCount = 1;
					this.moveMultiplier = 1;
					this.metronome = 0;
					return;
				}
			} else {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " woke up!", this);
				this.status = Status.HEALTHY;
			}
		}
		
		if (this.hasStatus(Status.CONFUSED)) {
			if (this.confusionCounter == 0) {
				this.removeStatus(Status.CONFUSED);
				Task.addTask(Task.TEXT, this.nickname + " snapped out of confusion!");
			} else {
				Task.addTask(Task.TEXT, this.nickname + " is confused!");
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
					this.removeStatus(Status.LOCKED);
					this.removeStatus(Status.CHARGING);
					this.removeStatus(Status.SEMI_INV);
					this.spriteVisible = true;
					this.lastMoveUsed = null;
					this.rollCount = 1;
					this.metronome = 0;
					this.moveMultiplier = 1;
					this.impressive = false;
					return;
				} else {
					confusionCounter--;
				}
			}
		}
		if (this.status == Status.PARALYZED && Math.random() < 0.25) {
			Task.addTask(Task.TEXT, this.nickname + " is fully paralyzed!");
			this.moveMultiplier = 1;
			this.impressive = false;
			this.removeStatus(Status.LOCKED);
			this.removeStatus(Status.CHARGING);
			this.removeStatus(Status.SEMI_INV);
			this.spriteVisible = true;
			this.lastMoveUsed = null;
			this.rollCount = 1;
			this.metronome = 0;
			return;
		}
		
		if (this.status != Status.TOXIC) toxic = 0;
		if (foe.status != Status.TOXIC) foe.toxic = 0;
		
		if (this.hasStatus(Status.FLINCHED) && this.ability != Ability.INNER_FOCUS) {
			if (foe.getItem() == Item.MENTAL_HERB) {
				Task.addTask(Task.TEXT, foe.nickname + " didn't flinch due to its Mental Herb!");
				foe.removeStatus(Status.FLINCHED);
				foe.consumeItem(this);
			} else {
				Task.addTask(Task.TEXT, this.nickname + " flinched!");
				this.removeStatus(Status.FLINCHED);
				this.moveMultiplier = 1;
				this.impressive = false;
				this.removeStatus(Status.LOCKED);
				this.removeStatus(Status.CHARGING);
				this.removeStatus(Status.SEMI_INV);
				this.spriteVisible = true;
				//this.lastMoveUsed = null;
				this.rollCount = 1;
				this.metronome = -1;
				return;
			}
		}
		if (this.hasStatus(Status.RECHARGE)) {
			Task.addTask(Task.TEXT, this.nickname + " must recharge!");
			this.moveMultiplier = 1;
			this.removeStatus(Status.RECHARGE);
			this.impressive = false;
			this.removeStatus(Status.LOCKED);
			this.removeStatus(Status.CHARGING);
			this.removeStatus(Status.SEMI_INV);
			this.spriteVisible = true;
			this.rollCount = 1;
			this.metronome = -1;
			return;
		}
		
		if (this.hasStatus(Status.TAUNTED) && move.cat == 2 && move != Move.METRONOME) {
			Task.addTask(Task.TEXT, this.nickname + " can't use " + move + " after the taunt!");
			this.lastMoveUsed = null;
			this.impressive = false;
			this.rollCount = 1;
			this.metronome = 0;
			return;
		}
		
		if (this.hasStatus(Status.HEAL_BLOCK) && move.isHealing()) {
			Task.addTask(Task.TEXT, this.nickname + " can't use " + move + " after the Heal Block!");
			this.lastMoveUsed = null;
			this.impressive = false;
			this.rollCount = 1;
			this.metronome = 0;
			return;
		}
		
		if (move == this.disabledMove) {
			Task.addTask(Task.TEXT, this.nickname + "'s " + move + " is disabled!");
			//this.lastMoveUsed = null;
			this.impressive = false;
			this.rollCount = 1;
			this.metronome = -1;
			return;
		}
		
		if (this.hasStatus(Status.MUTE) && Move.getSound().contains(move)) {
			Task.addTask(Task.TEXT, this.nickname + " can't use " + move + " after the throat chop!");
			this.lastMoveUsed = null;
			this.impressive = false;
			this.rollCount = 1;
			this.metronome = 0;
			return;
		}
		
		if (move == Move.SKULL_BASH || move == Move.SKY_ATTACK || ((move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) && !field.equals(field.weather, Effect.SUN, this))
				|| this.hasStatus(Status.CHARGING) || move == Move.BLACK_HOLE_ECLIPSE || move == Move.GEOMANCY || move == Move.METEOR_BEAM) {
			if (this.getItem() == Item.POWER_HERB) {
				announceUseMove(move);
				Task.addTask(Task.TEXT, this.nickname + " started charging up!");
				this.addStatus(Status.CHARGING);
				if (move == Move.SKULL_BASH) stat(this, 1, 1, foe);
				if (move == Move.METEOR_BEAM) stat(this, 2, 1, foe);
				if (move == Move.BLACK_HOLE_ECLIPSE) stat(this, 3, 1, foe);
				Task.addTask(Task.TEXT, this.nickname + " became fully charged due to its Power Herb!");
				this.consumeItem(foe);
			}
			if (!this.hasStatus(Status.CHARGING)) {
				announceUseMove(move);
				Task.addTask(Task.TEXT, this.nickname + " started charging up!");
				this.addStatus(Status.CHARGING);
				if (move == Move.SKULL_BASH) stat(this, 1, 1, foe);
				if (move == Move.METEOR_BEAM) stat(this, 2, 1, foe);
				if (move == Move.BLACK_HOLE_ECLIPSE) stat(this, 3, 1, foe);
				this.moveMultiplier = 1;
				this.impressive = false;
				this.rollCount = 1;
				this.metronome = -1;
				return;
			} else {
				move = this.lastMoveUsed;
				bp = move.basePower;
				acc = move.accuracy;
				secChance = move.secondary;
				moveType = move.mtype;
				critChance = move.critChance;
				contact = move.contact;
				if (!foe.hasStatus(Status.MAGIC_REFLECT)) this.removeStatus(Status.CHARGING);
			}
		}
		
		if (move == Move.DIG || move == Move.DIVE || move == Move.FLY || move == Move.BOUNCE || move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT || this.hasStatus(Status.SEMI_INV)) {
			if (this.getItem() == Item.POWER_HERB) {
				announceUseMove(move);
				if (move == Move.DIG) Task.addTask(Task.SEMI_INV, this.nickname + " burrowed underground!", this);
				if (move == Move.DIVE) Task.addTask(Task.SEMI_INV, this.nickname + " dove underwater!", this);
				if (move == Move.FLY) Task.addTask(Task.SEMI_INV, this.nickname + " flew up high!", this);
				if (move == Move.BOUNCE) Task.addTask(Task.SEMI_INV, this.nickname + " sprang up!", this);
				if (move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT) Task.addTask(Task.SEMI_INV, this.nickname + " vanished instantly!", this);
				this.addStatus(Status.SEMI_INV);
				Task.addTask(Task.TEXT, this.nickname + " became fully charged due to its Power Herb!");
				this.consumeItem(foe);
			}
			if (!this.hasStatus(Status.SEMI_INV)) {
				announceUseMove(move);
				if (move == Move.DIG) Task.addTask(Task.SEMI_INV, this.nickname + " burrowed underground!", this);
				if (move == Move.DIVE) Task.addTask(Task.SEMI_INV, this.nickname + " dove underwater!", this);
				if (move == Move.FLY) Task.addTask(Task.SEMI_INV, this.nickname + " flew up high!", this);
				if (move == Move.BOUNCE) Task.addTask(Task.SEMI_INV, this.nickname + " sprang up!", this);
				if (move == Move.PHANTOM_FORCE || move == Move.VANISHING_ACT) Task.addTask(Task.SEMI_INV, this.nickname + " vanished instantly!", this);
				this.addStatus(Status.SEMI_INV);
				this.moveMultiplier = 1;
				this.impressive = false;
				this.rollCount = 1;
				return;
			} else {
				move = this.lastMoveUsed;
				bp = move.basePower;
				acc = move.accuracy;
				secChance = move.secondary;
				moveType = move.mtype;
				critChance = move.critChance;
				contact = move.contact;
				if (!foe.hasStatus(Status.MAGIC_REFLECT)) this.removeStatus(Status.SEMI_INV);
			}
		}
		
		if (foe.hasStatus(Status.PROTECT) && (move.accuracy <= 100 || move.cat != 2) && move != Move.FEINT && move != Move.PHANTOM_FORCE
				&& move != Move.VANISHING_ACT && move != Move.MIGHTY_CLEAVE && move != Move.FUTURE_SIGHT) {
			useMove(move, foe);
			Task.addTask(Task.TEXT, foe.nickname + " protected itself!");
			if (contact) {
				if (foe.lastMoveUsed == Move.OBSTRUCT) stat(this, 1, -2, foe);
				if (foe.lastMoveUsed == Move.LAVA_LAIR) burn(false, foe);
				if (foe.lastMoveUsed == Move.SPIKY_SHIELD) {
					this.damage(this.getHPAmount(1.0/8), foe);
					Task.addTask(Task.TEXT, this.nickname + " was hurt!");
					if (this.currentHP <= 0) { // Check for kill
						this.faint(true, foe);
					}
				}
			}
			if (foe.lastMoveUsed == Move.AQUA_VEIL) {
				stat(this, 2, -1, foe);
			}
			if (move == Move.HI_JUMP_KICK) {
				this.damage(this.getStat(0) / 2.0, foe);
				Task.addTask(Task.TEXT, this.nickname + " kept going and crashed!");
				if (this.currentHP < 0) {
					this.faint(true, foe);
				}
			}
			this.impressive = false;
			this.moveMultiplier = 1;
			this.rollCount = 1;
			this.metronome = -1;
			return;
		}
		
		if (this.hasStatus(Status.LOCKED) && (lastMoveUsed == Move.OUTRAGE || lastMoveUsed == Move.PETAL_DANCE || lastMoveUsed == Move.THRASH ||
				lastMoveUsed == Move.ROLLOUT || lastMoveUsed == Move.ICE_BALL)) {
			move = this.lastMoveUsed;
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
		}
		
		if (move == Move.OUTRAGE || move == Move.PETAL_DANCE || move == Move.THRASH) {
			if (!this.hasStatus(Status.LOCKED)) {
				this.addStatus(Status.LOCKED);
				this.outCount = (int)(Math.random()*2) + 2;
			}
			this.outCount--;
		}
		
		if (id == 237) {
			Move oldMove = move;
			move = get150Move(move);
			if (move != oldMove) {
				for (Moveslot m : this.moveset) {
					if (m != null && m.move == oldMove) {
						m.currentPP--;
						if (m.currentPP == 0) {
							if (this.getItem() == Item.LEPPA_BERRY) {
								this.eatBerry(this.item, true, foe, m.move);
							} else {
								encoreCount = 0;
							}
						}
					}
				}
			}
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
		}
		
		if (move == Move.MIRROR_MOVE || move == Move.MIMIC) {
			useMove(move, foe);
			move = foe.lastMoveUsed;
			if (move == null || move == Move.MIRROR_MOVE || move == Move.MIMIC) {
				Task.addTask(Task.TEXT, "But it failed!");
				this.impressive = false;
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = 0;
				return;
			}
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
		}
		
		if (move == Move.HIDDEN_POWER) moveType = determineHPType();
		if (move == Move.RETURN) moveType = determineHPType();
		if (move == Move.WEATHER_BALL) moveType = determineWBType();
		if (move == Move.TERRAIN_PULSE) moveType = determineTPType();
		
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
		
		if (this.ability == Ability.REFRIGERATE && moveType == PType.NORMAL) {
			moveType = PType.ICE;
			bp *= 1.2;
		}
		
		if (this.ability == Ability.PROTEAN && moveType != PType.UNKNOWN) {
			if (this.type1 == moveType && this.type2 == null) {
				
			} else {
				this.type1 = moveType;
				this.type2 = null;
				Task.addAbilityTask(this);
				Task.addTask(Task.TEXT, this.nickname + "'s type was updated to " + this.type1.toString() + "!");
			}
		}
		
		if (foeAbility == Ability.MAGIC_BOUNCE && move.cat == 2 && (acc <= 100 || move == Move.WHIRLWIND || move == Move.ROAR || move == Move.STEALTH_ROCK
				|| move == Move.SPIKES || move == Move.TOXIC_SPIKES || move == Move.TOXIC || move == Move.STICKY_WEB || move == Move.YAWN
				|| move == Move.FLOODLIGHT)) {
			useMove(move, foe);
			Task.addAbilityTask(foe);
			foe.move(this, move, true);
			Task.addTask(Task.TEXT, foe.nickname + " bounced the " + move + " back!");
			return;
		}
		
		if (foe.hasStatus(Status.MAGIC_REFLECT) && (move != Move.BRICK_BREAK && move != Move.MAGIC_FANG && move != Move.PSYCHIC_FANGS)) {
			this.move(this, move, false);
			Task.addTask(Task.TEXT, move + " was reflected on itself!");
			foe.removeStatus(Status.MAGIC_REFLECT);
			return;
		}
		if (this.hasStatus(Status.POSSESSED)) {
			this.removeStatus(Status.POSSESSED);
			this.move(this, move, false);
			Task.addTask(Task.TEXT, move + " was used on itself!");
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
			Move[] moves = Move.getAllMoves();
			move = moves[new Random().nextInt(moves.length)];
			bp = move.basePower;
			acc = move.accuracy;
			secChance = move.secondary;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
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
		
		if (!foe.hasStatus(Status.SEMI_INV)) {
			if (this.ability == Ability.COMPOUND_EYES) acc *= 1.3;
			if (item == Item.WIDE_LENS) acc *= 1.1;
			if (item == Item.ZOOM_LENS && !first) acc *= 1.2;
			if (field.contains(field.fieldEffects, Effect.GRAVITY)) acc = acc * 5 / 3;
		}
		
		if (field.equals(field.weather, Effect.SUN, foe)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 50;
		}
		
		if (field.equals(field.weather, Effect.RAIN, foe)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 1000;
		}
		
		if (field.equals(field.weather, Effect.SNOW, foe)) {
			if (move == Move.BLIZZARD) acc = 1000;
		}
		
		if (move == Move.FROSTBIND && this.isType(PType.ICE)) acc = 1000;
		if (move == Move.THUNDER_WAVE && this.isType(PType.ELECTRIC)) acc = 1000;
		if (move == Move.WILL$O$WISP && this.isType(PType.FIRE)) acc = 1000;
		if (move == Move.TOXIC && this.isType(PType.POISON)) acc = 1000;
		
		if (foe.ability == Ability.WONDER_SKIN && move.cat == 2 && acc <= 100) acc = 50;
		
		if (move == Move.POP_POP) acc = 1000;
		
		if (foe.getItem() != Item.ABILITY_SHIELD &&
				(move == Move.FUSION_BOLT || move == Move.FUSION_FLARE || this.ability == Ability.MOLD_BREAKER)) {
			foeAbility = Ability.NULL;
		}
		
		if (this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD) {
			int accEv = this.statStages[5] - foe.statStages[6];
			if (move == Move.DARKEST_LARIAT || move == Move.SACRED_SWORD) accEv += foe.statStages[6];
			accEv = accEv > 6 ? 6 : accEv;
			accEv = accEv < -6 ? -6 : accEv;
			double accuracy = acc * asAccModifier(accEv);
			if ((field.equals(field.weather, Effect.SANDSTORM, this) && foeAbility == Ability.SAND_VEIL) ||
					(field.equals(field.weather, Effect.SNOW, this) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
			if (foe.getItem() == Item.BRIGHT_POWDER) accuracy *= 0.9;
			if (!hit(accuracy) || foe.hasStatus(Status.SEMI_INV) && acc <= 100) {
				useMove(move, foe);
				Task.addTask(Task.TEXT, this.nickname + "'s attack missed!");
				field.misses++;
				if (move == Move.HI_JUMP_KICK) {
					this.damage(this.getStat(0) / 2.0, foe);
					Task.addTask(Task.TEXT, this.nickname + " kept going and crashed!");
					if (this.currentHP < 0) {
						this.faint(true, foe);
					}
				}
				if (this.getItem() == Item.BLUNDER_POLICY) {
					Task.addTask(Task.TEXT, this.nickname + " used its " + item.toString() + "!");
					stat(this, 4, 2, foe);
					this.consumeItem(foe);
				}
				endMove();
				this.removeStatus(Status.LOCKED);
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return; // Check for miss
			}
		}
		
		if (move == Move.FUTURE_SIGHT) {
			useMove(move, foe);
			if (field.contains(foe.getFieldEffects(), Effect.FUTURE_SIGHT)) {
				fail();
				this.impressive	= false;
				this.moveMultiplier = 1;
				this.metronome = -1;
				return;
			} else {
				Task.addTask(Task.TEXT, this.nickname + " foresaw an attack!");
				FieldEffect effect = field.new FieldEffect(Effect.FUTURE_SIGHT);
				attackStat = this.getStat(3);
				if (this.getItem() == Item.CHOICE_SPECS) attackStat *= 1.5;
				if (this.status == Status.FROSTBITE) attackStat /= 2;
				if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN, this)) attackStat *= 1.5;
				if (this.isType(moveType)) attackStat *= 1.5;
				effect.stat = (int) attackStat;
				effect.level = this.level;
				foe.getFieldEffects().add(effect);
				return;
			}
		}
		
		int numHits = move.getNumHits(this, team);
		
		ArrayList<Task> tasks = gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.simBattleUI.tasks;
		int damageIndex = tasks.size();
		
		useMove(move, foe);
		Task msgTask = Task.getTask(damageIndex);
		String message = msgTask == null ? "" : msgTask.message;
		int hits = 0;
		boolean foeEject = false;
		for (int hit = 1; hit <= numHits; hit++) {
			if (hit > 1) bp = move.basePower;
			if (move == Move.POP_POP) hits++;
			if (foe.isFainted() || this.isFainted()) {
				Task.addTask(Task.TEXT, "Hit " + (hit - 1) + " time(s)!");
				break;
			}
			
			if (move == Move.POP_POP && this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD) {
				acc = Move.POP_POP.accuracy;
				int accEv = this.statStages[5] - foe.statStages[6];
				accEv = accEv > 6 ? 6 : accEv;
				accEv = accEv < -6 ? -6 : accEv;
				double accuracy = acc * asAccModifier(accEv);
				if ((field.equals(field.weather, Effect.SANDSTORM, this) && foeAbility == Ability.SAND_VEIL) ||
						(field.equals(field.weather, Effect.SNOW, this) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
				if (foe.getItem() == Item.BRIGHT_POWDER) accuracy *= 0.9;
				if (!hit(accuracy) || foe.hasStatus(Status.SEMI_INV) && acc <= 100) {
					Task.addTask(Task.TEXT, this.nickname + "'s attack missed!");
					field.misses++;
					if (this.getItem() == Item.BLUNDER_POLICY) {
						stat(this, 4, 2, foe);
						this.consumeItem(foe);
					}
					endMove();
					this.removeStatus(Status.LOCKED);
					this.moveMultiplier = 1;
					this.rollCount = 1;
					this.metronome = -1;
					hits--;
					continue; // Check for miss
				}
			}
			
			if (moveType == PType.FIRE && foeAbility == Ability.FLASH_FIRE && !(move.cat == 2 && acc > 100)) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, foe.nickname + "'s Fire-Type move's are boosted!");
				if (!foe.hasStatus(Status.FLASH_FIRE)) foe.addStatus(Status.FLASH_FIRE);
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return;
			}
			
			if (((moveType == PType.WATER && (foeAbility == Ability.WATER_ABSORB || foeAbility == Ability.DRY_SKIN)) || (moveType == PType.ELECTRIC && foeAbility == Ability.VOLT_ABSORB) ||
					(moveType == PType.BUG && foeAbility == Ability.INSECT_FEEDER) || ((moveType == PType.LIGHT || moveType == PType.GALACTIC) && foeAbility == Ability.BLACK_HOLE)) &&
					!(move.cat == 2 && acc > 100)) {
				if (foeAbility == Ability.BLACK_HOLE && moveType == PType.GALACTIC && this.trainerOwned()) foe.spaceEat++;
				if (foe.currentHP == foe.getStat(0)) {
					Task.addAbilityTask(foe);
					Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
					endMove();
					this.moveMultiplier = 1;
					this.rollCount = 1;
					this.metronome = -1;
					return; // Check for immunity
				} else {
					Task.addAbilityTask(foe);
					foe.heal(foe.getHPAmount(1.0/4), foe.nickname + " restored HP!");
					endMove();
					this.moveMultiplier = 1;
					this.rollCount = 1;
					this.metronome = -1;
					return;
				}
			}
			
			if (((moveType == PType.ELECTRIC && (foeAbility == Ability.MOTOR_DRIVE || foeAbility == Ability.LIGHTNING_ROD)) ||
					(moveType == PType.GRASS && foeAbility == Ability.SAP_SIPPER) || (moveType == PType.FIRE && foeAbility == Ability.HEAT_COMPACTION)) &&
					!(move.cat == 2 && acc > 100)) {
				Task.addAbilityTask(foe);
				if (foeAbility == Ability.MOTOR_DRIVE) stat(foe, 4, 1, this);
				if (foeAbility == Ability.LIGHTNING_ROD) stat(foe, 2, 1, this);
				if (foeAbility == Ability.SAP_SIPPER) stat(foe, 0, 1, this);
				if (foeAbility == Ability.HEAT_COMPACTION) {
					stat(foe, 1, 1, this);
					stat(foe, 3, 1, this);
				}
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return;
			}
			
			if (moveType == PType.GROUND && !foe.isGrounded() && move.cat != 2) {
				if (foeAbility == Ability.LEVITATE) Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return; // Check for immunity
			}
			
			if (moveType != PType.GROUND && (move.cat != 2 || move == Move.THUNDER_WAVE)) {
				if (getImmune(foe, moveType) || (moveType == PType.GHOST && foeAbility == Ability.FRIENDLY_GHOST)) {
					if (foe.getItem() == Item.RING_TARGET) {
						// Nothing
					} else if (this.ability == Ability.SCRAPPY && (moveType == PType.NORMAL || moveType == PType.FIGHTING)) {
						// Nothing: scrappy allows normal and fighting type moves to hit ghosts
					} else if (this.ability == Ability.CORROSION && moveType == PType.POISON) {
						// Nothing: corrosion allows poison moves to hit steel
					} else {
						if (move == Move.HI_JUMP_KICK) {
							this.damage(this.getStat(0) / 2.0, foe);
							Task.addTask(Task.TEXT, this.nickname + " kept going and crashed!");
							if (this.currentHP < 0) {
								this.faint(true, foe);
							}
						}
						if (foeAbility == Ability.FRIENDLY_GHOST && moveType == PType.GHOST) Task.addAbilityTask(foe);
						Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
						endMove();
						this.outCount = 0;
						this.moveMultiplier = 1;
						this.rollCount = 1;
						this.metronome = -1;
						return; // Check for immunity 
					}
				}
			}
			
			if (field.equals(field.terrain, Effect.PSYCHIC) && foe.isGrounded() && move.hasPriority(this) && move.accuracy <= 100 && move != Move.GRAVITY_PUNCH) {
				Task.addTask(Task.TEXT, foe.nickname + " is protected by the Psychic Terrain!");
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return; // Check for immunity 
			}
			
			if (foe.magCount > 0) foe.magCount--;
			
			if (move == Move.DREAM_EATER && foe.status != Status.ASLEEP) {
				Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				this.impressive = false;
				return;
			}
			
			if (move == Move.POLTERGEIST) {
				if (foe.item == null) {
					fail();
					this.impressive	= false;
					this.moveMultiplier = 1;
					this.metronome = -1;
					return;
				} else {
					Task t = Task.createTask(Task.TEXT, foe.nickname + " is about to be attacked by its " + foe.item.toString() + "!");
					Task.insertTask(Task.createTask(Task.TEXT, this.nickname + " used " + move.toString() + "!"), damageIndex++);
					Task.insertTask(t, damageIndex++);
				}
			}
			
			if (move == Move.DISENCHANT && !arrayEquals(foe.statStages, new int[7])) {
				foe.statStages = new int[7];
				Task t = Task.createTask(Task.TEXT, foe.nickname + " stat changes were eliminated!");
				Task.insertTask(Task.createTask(Task.TEXT, this.nickname + " used " + move.toString() + "!"), damageIndex++);
				Task.insertTask(t, damageIndex++);
			}
			
			if (move.cat == 2) {
				statusEffect(foe, move, player, enemy);
				this.impressive = false;
				this.moveMultiplier = 1;
				this.metronome = 0;
				return;
			}
			
			if ((moveType == PType.WATER && (foe.getItem() == Item.ABSORB_BULB || foe.getItem() == Item.LUMINOUS_MOSS))
					|| (moveType == PType.ELECTRIC && foe.getItem() == Item.CELL_BATTERY)
					|| (moveType == PType.ICE && foe.getItem() == Item.SNOWBALL)) {
				Task.addTask(Task.TEXT, foe.nickname + " used its " + foe.item.toString() + " to absorb the attack!");
				stat(foe, foe.item.getStat(), 1, this);
				foe.consumeItem(this);
				endMove();
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				this.impressive = false;
				return;
			}
			
			if (move.basePower < 0) {
				bp = determineBasePower(foe, move, first, team, true);
			}
			
			if (move == Move.ROLLOUT || move == Move.ICE_BALL) {
				if (!this.hasStatus(Status.LOCKED)) {
					this.addStatus(Status.LOCKED);
				}
				this.rollCount++;
			} else {
				this.rollCount = 1;
			}
			
			if (this.getItem() == Item.METRONOME) bp *= (1 + (this.metronome * 0.2));
			
			if (this.getItem() == Item.PROTECTIVE_PADS) contact = false;
			if (this.getItem() == Item.PUNCHING_GLOVE && move.isPunching()) {
				bp *= 1.1;
				contact = false;
			}
			
			if (moveType == PType.FIRE && this.hasStatus(Status.FLASH_FIRE)) bp *= 1.5;
			
			if (this.ability == Ability.SHEER_FORCE && move.cat != 2 && secChance > 0) {
				secChance = 0;
				sheer = true;
				bp *= 1.3;
			}
			
			if (move == Move.FUSION_BOLT && this.lastMoveUsed == Move.FUSION_FLARE) {
				bp *= 2;
				this.lastMoveUsed = move;
			}
			
			if (move == Move.FUSION_FLARE && this.lastMoveUsed == Move.FUSION_BOLT) {
				bp *= 2;
				this.lastMoveUsed = move;
			}
			
			if (this.ability == Ability.TECHNICIAN && bp <= 60) {
				bp *= 1.5;
			}
			
			if (this.ability == Ability.TOUGH_CLAWS && contact) {
				bp *= 1.3;
			}
			
			if (foeAbility == Ability.DRY_SKIN && moveType == PType.FIRE) {
				bp *= 1.25;
			}
			
			if (this.ability == Ability.IRON_FIST && move.isPunching()) {
				bp *= 1.3;
			}
			
			if (this.ability == Ability.SHARPNESS && move.isSlicing()) bp *= 1.5;
			
			if (this.ability == Ability.SHARP_TAIL && move.isTail()) bp *= 1.5;
			
			if (this.ability == Ability.STRONG_JAW && move.isBiting()) bp *= 1.5;
			
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
			
			if (move == Move.CHROMO_BEAM && checkSecondary(this.ability == Ability.SERENE_GRACE || field.equals(field.terrain, Effect.SPARKLY) ? 60 : 30)) {
				Task.insertTask(Task.createTask(Task.TEXT, this.nickname + " is going all out for this attack!"), damageIndex++);
				bp *= 2;
			}
			
			int arcane = this.getStatusNum(Status.ARCANE_SPELL);
			if (arcane != 0) {
				bp = Math.max(bp - arcane, 20);
			}
			
			if (field.equals(field.weather, Effect.SUN, foe)) {
				if (moveType == PType.WATER) bp *= 0.5;
				if (moveType == PType.FIRE) bp *= 1.5;
				if (move == Move.SOLSTICE_BLADE) bp *= 1.5;
			}
			
			if (field.equals(field.weather, Effect.RAIN, foe)) {
				if (moveType == PType.WATER) bp *= 1.5;
				if (moveType == PType.FIRE) bp *= 0.5;
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
			
			if (field.equals(field.weather, Effect.SANDSTORM, foe) && this.ability == Ability.SAND_FORCE) bp *= 1.3;
			
			if (field.equals(field.weather, Effect.RAIN, foe) || field.equals(field.weather, Effect.SNOW, foe) || field.equals(field.weather, Effect.SANDSTORM, foe)) {
				if (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE || move == Move.SOLSTICE_BLADE) bp *= 0.5;
			}
			
			if (foeAbility == Ability.SHIELD_DUST || foe.getItem() == Item.COVERT_CLOAK) secChance = 0;
			
			// Use either physical or special attack/defense
			if (move.isPhysical()) {
				attackStat = this.getStat(1);
				defenseStat = foe.getStat(2);
				if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(0);
				if (move != Move.DARKEST_LARIAT && move != Move.SACRED_SWORD && this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(1);
				if (move == Move.BODY_PRESS) attackStat = this.getStat(2) * this.asModifier(1);
				if (move == Move.FOUL_PLAY) attackStat = foe.getStat(1) * foe.asModifier(0);
				if (this.getItem() == Item.CHOICE_BAND) attackStat *= 1.5;
				if (this.status == Status.BURNED && this.ability != Ability.GUTS && move != Move.FACADE) attackStat /= 2;
				if (this.ability == Ability.GUTS && this.status != Status.HEALTHY) attackStat *= 1.5;
				if (this.ability == Ability.HUGE_POWER) attackStat *= 2;
				if (field.equals(field.weather, Effect.SNOW, this) && foe.isType(PType.ICE)) defenseStat *= 1.5;
				if (field.contains(foe.getFieldEffects(), Effect.REFLECT) || field.contains(foe.getFieldEffects(), Effect.AURORA_VEIL)) defenseStat *= 2;
			} else {
				attackStat = this.getStat(3);
				defenseStat = foe.getStat(4);
				if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(2);
				if (this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(3);
				if (foe.getItem() == Item.ASSAULT_VEST) defenseStat *= 1.5;
				if (move == Move.PSYSHOCK || move == Move.MAGIC_MISSILES) defenseStat = foe.getStat(2) * foe.asModifier(1);
				if (this.getItem() == Item.CHOICE_SPECS) attackStat *= 1.5;
				if (this.status == Status.FROSTBITE) attackStat /= 2;
				if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN, this)) attackStat *= 1.5;
				if (field.equals(field.weather, Effect.SANDSTORM, this) && foe.isType(PType.ROCK)) defenseStat *= 1.5;
				if (field.contains(foe.getFieldEffects(), Effect.LIGHT_SCREEN) || field.contains(foe.getFieldEffects(), Effect.AURORA_VEIL)) defenseStat *= 2;
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
			if (moveType == PType.ELECTRIC && this.hasStatus(Status.CHARGED)) {
				bp *= 2;
				this.removeStatus(Status.CHARGED);
			}
			
			// Load Firearms
			if (moveType == PType.STEEL && this.hasStatus(Status.LOADED)) {
				bp *= 2;
				this.removeStatus(Status.LOADED);
			}
			
			// Crit Check
			critChance += this.getStatusNum(Status.CRIT_CHANCE);
			if (this.ability == Ability.SUPER_LUCK) critChance++;
			if (item == Item.SCOPE_LENS) critChance++;
			if (this.ability == Ability.MERCILESS && (foe.status == Status.POISONED || foe.status == Status.TOXIC)) critChance = 3; 
			if (foe.ability != Ability.BATTLE_ARMOR && foe.ability != Ability.SHELL_ARMOR && !field.contains(foe.getFieldEffects(), Effect.LUCKY_CHANT) && critCheck(critChance)) {
				Task.addTask(Task.TEXT, "A critical hit!");
				field.crits++;
				if (foe.trainerOwned() && move == Move.HEADBUTT) headbuttCrit++;
				if (foe.trainerOwned() && move.isTail()) tailCrit++;
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
					if (field.equals(field.weather, Effect.SNOW, this) && foe.isType(PType.ICE)) defenseStat *= 1.5;
				}
				if (!move.isPhysical() && defenseStat > foe.getStat(4)) {
					defenseStat = foe.getStat(4);
					if (field.equals(field.weather, Effect.SANDSTORM, this) && foe.isType(PType.ROCK)) defenseStat *= 1.5;
				}
				if (foe.getItem() == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
				
				damage = calc(attackStat, defenseStat, bp, this.level);
				damage *= 1.5;
				if (this.ability == Ability.SNIPER) damage *= 1.5;
				if (foeAbility == Ability.ANGER_POINT) {
					Task.addAbilityTask(foe);
					stat(foe, 0, 12, this); }
			} else {
				if (foe.getItem() == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
				damage = calc(attackStat, defenseStat, bp, this.level);
			}
			
			if ((foeAbility == Ability.ICY_SCALES && !move.isPhysical()) || (foeAbility == Ability.MULTISCALE && foe.currentHP == foe.getStat(0))) damage /= 2;
			
			double multiplier = 1;
			// Check type effectiveness
			PType[] resist = getResistances(moveType);
			if (move == Move.FREEZE$DRY || move == Move.SKY_UPPERCUT) {
				ArrayList<PType> types = new ArrayList<>();
				for (PType type : resist) {
					types.add(type);
				}
				if (move == Move.FREEZE$DRY) types.remove(PType.WATER);
				if (move == Move.SKY_UPPERCUT) types.remove(PType.FLYING);
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
			if (move == Move.FREEZE$DRY || move == Move.SKY_UPPERCUT) {
				PType[] temp = new PType[weak.length + 1];
				for (int i = 0; i < weak.length; i++) {
					temp[i] = weak[i];
				}
				if (move == Move.FREEZE$DRY) temp[weak.length] = PType.WATER;
				if (move == Move.SKY_UPPERCUT) temp[weak.length] = PType.FLYING;
				weak = temp;
			}
			for (PType type : weak) {
				if (foe.type1 == type) multiplier *= 2;
				if (foe.type2 == type) multiplier *= 2;
			}
			
			if (foeAbility == Ability.WONDER_GUARD && multiplier <= 1 && moveType != PType.UNKNOWN) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				endMove();
				this.outCount = 0;
				this.moveMultiplier = 1;
				this.rollCount = 1;
				this.metronome = -1;
				return; // Check for immunity
			}
			
			// Night Shade etc can't be super effective/resisted
			if (move.critChance < 0) multiplier = 1;
			
			if (foeAbility == Ability.FLUFFY && moveType == PType.FIRE) multiplier *= 2;
			
			if (foeAbility == Ability.MOSAIC_WINGS && moveType != PType.UNKNOWN && multiplier == 1 && move.critChance >= 0) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, foe.nickname + " is distorting type matchups!");
				damageIndex += 3;
				if (hit == 1) damageIndex--;
				multiplier = 0.5;
			}
			
			damage *= multiplier;
			
			if (foeAbility == Ability.UNERODIBLE && (moveType == PType.GRASS || moveType == PType.WATER || moveType == PType.GROUND)) damage /= 4;
			if (foeAbility == Ability.THICK_FAT && (moveType == PType.FIRE || moveType == PType.ICE)) damage /= 2;
			if (foeAbility == Ability.UNWAVERING && (moveType == PType.DARK || moveType == PType.GHOST)) damage /= 2;
			if (foeAbility == Ability.JUSTIFIED && moveType == PType.DARK) damage /= 2;
			if (foeAbility == Ability.FLUFFY && contact) damage /= 2;
			
			if (foeAbility == Ability.PSYCHIC_AURA && move.cat == 1) damage *= 0.8;
			if (foeAbility == Ability.GLACIER_AURA && move.cat == 0) damage *= 0.8;
			if (foeAbility == Ability.GALACTIC_AURA && (moveType == PType.PSYCHIC || moveType == PType.ICE)) damage /= 2;
			
			if (multiplier > 1) {
				message += "\nIt's super effective!";
				field.superEffective++;
				if (foeAbility == Ability.SOLID_ROCK || foeAbility == Ability.FILTER) damage /= 2;
				if (item == Item.EXPERT_BELT) damage *= 1.2;
				if (foe.getItem() != null && foe.checkTypeResistBerry(moveType)) {
					Task.addTask(Task.TEXT, foe.nickname + " ate its " + foe.item.toString() + " to weaken the attack!");
					foe.consumeItem(this);
					damage /= 2;
				}
			}
			if (multiplier < 1) {
				message += "\nIt's not very effective...";
				if (ability == Ability.TINTED_LENS) damage *= 2;
			}
			
			if (this.getItem() == Item.LIFE_ORB) damage *= 1.3;
			if (this.ability == Ability.ILLUSION && this.illusion) damage *= 1.2;
			if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS || move == Move.PSYWAVE) damage = this.level;
			if (move == Move.ENDEAVOR) {
				if (foe.currentHP > this.currentHP) {
					damage = foe.currentHP - this.currentHP;
				} else { fail(); } }
			if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
			if (move == Move.DRAGON_RAGE) damage = 40;
			if (move == Move.HORN_DRILL || move == Move.SHEER_COLD || move == Move.GUILLOTINE || move == Move.FISSURE) {
				if ((move == Move.SHEER_COLD && foe.isType(PType.ICE)) || foeAbility == Ability.STURDY || foe.level > this.level) {
					if (foeAbility == Ability.STURDY) Task.addAbilityTask(foe);
					Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
					endMove();
					this.moveMultiplier = 1;
					return;
				}
				damage = foe.currentHP;
				Task.addTask(Task.TEXT, "It's a one-hit KO!");
			}
			
			if (Move.getDraining().contains(move)) {
				int healAmount = Math.min(damage, foe.currentHP);
				healAmount = Math.max((int) Math.ceil(healAmount / 2.0), 1);
				if (item == Item.BIG_ROOT) healAmount *= 1.3;
				heal(healAmount, this.nickname + " sucked HP from " + foe.nickname + "!");
			}
			
			if (!sheer && this.getItem() == Item.SHELL_BELL && this.currentHP < this.getStat(0)) {
				int healAmount = Math.min(damage, foe.currentHP);
				healAmount = Math.max((int) Math.ceil(healAmount / 4.0), 1);
				heal(healAmount, this.nickname + " recovered a little HP using its Shell Bell!");
			}
			
			damage = Math.max(damage, 1);
			
			if (this.ability == Ability.SERENE_GRACE) secChance *= 2;
			
			int recoil = 0;
			if (Move.getRecoil().contains(move) && ability != Ability.ROCK_HEAD) {
				int denom = move == Move.HEAD_SMASH ? 2 : move == Move.SUBMISSION || move == Move.TAKE_DOWN ? 4 : 3;
				recoil = Math.max(Math.floorDiv(damage, denom), 1);
				if (damage >= foe.currentHP) recoil = Math.max(Math.floorDiv(foe.currentHP, 3), 1);
				if (move == Move.STEEL_BEAM) recoil = Math.max(Math.floorDiv(this.getStat(0), 2), 1);
				if (move == Move.STRUGGLE) recoil = Math.max(Math.floorDiv(this.getStat(0), 4), 1);
			}
			
			boolean fullHP = foe.currentHP == foe.getStat(0);
			boolean sturdy = false;
			if (damage >= foe.currentHP && (move == Move.FALSE_SWIPE || foe.hasStatus(Status.ENDURE) ||
					(foe.getItem() == Item.FOCUS_BAND && checkSecondary(10)) || (fullHP && (foeAbility == Ability.STURDY || foe.getItem() == Item.FOCUS_SASH)))) {
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
				Task.addTask(Task.TEXT, damagePercentText);
				foe.damage(damage, this, move, message, damageIndex, sturdy);
			}
			
			if (!foe.isFainted() && (moveType == PType.WATER && (foe.getItem() == Item.DAMAGED_BULB || foe.getItem() == Item.DAMAGED_MOSS))
					|| (moveType == PType.ELECTRIC && foe.getItem() == Item.DAMAGED_BATTERY)
					|| (moveType == PType.ICE && foe.getItem() == Item.DAMAGED_SNOWBALL)) {
				Task.addTask(Task.TEXT, foe.nickname + " used its " + foe.item.toString() + " to boost its " + Pokemon.getStatType(foe.item.getStat() + 1, true) + "!");
				stat(foe, foe.item.getStat(), 1, this);
				foe.consumeItem(this);
			}
			if (foe.getItem() == Item.AIR_BALLOON) {
				Task.addTask(Task.TEXT, foe.nickname + "'s Air Balloon popped!");
				foe.consumeItem(this);
			}
			if (foe.getItem() == Item.ABSORB_BULB || foe.getItem() == Item.CELL_BATTERY || foe.getItem() == Item.LUMINOUS_MOSS || foe.getItem() == Item.SNOWBALL) {
				Task.addTask(Task.TEXT, foe.nickname + "'s " + foe.item.toString() + " was damaged!");
				foe.item = Item.getItem(foe.item.getHealAmount());
			}
			if (foe.ability == Ability.ILLUSION && foe.illusion) {
				Task.addTask(Task.TEXT, foe.nickname + "'s Illusion was broken!");
				foe.illusion = false;
			}
			if (this.getItem() == Item.THROAT_SPRAY && Move.getSound().contains(move)) {
				Task.addTask(Task.TEXT, this.nickname + " used its Throat Spray!");
				stat(this, 2, 1, foe);
				this.consumeItem(foe);
			}
			if (sturdy) {
				foe.currentHP = 1;
				if (fullHP && foeAbility == Ability.STURDY) Task.addAbilityTask(foe);
				if (foe.getItem() == Item.FOCUS_SASH) {
					Task.addTask(Task.TEXT, foe.nickname + " hung on using its Focus Sash!");
					foe.consumeItem(this);
				} else if (move != Move.FALSE_SWIPE) {
					Task.addTask(Task.TEXT, foe.nickname + " endured the hit!");
				}
			}
			if (foe.currentHP <= 0) { // Check for kill
				foe.faint(true, this);
				if (move == Move.FELL_STINGER) stat(this, 0, 3, foe);
				if (this.hasStatus(Status.BONDED)) {
					this.damage(this.currentHP, foe, foe.nickname + " took its attacker down with it!");
					if (this.currentHP <= 0) {
						this.faint(true, foe);
					}
				}
				if (!this.isFainted()) {
					if (this.ability == Ability.MOXIE) {
						Task.addAbilityTask(this);
						stat(this, 0, 1, foe);
					}
					if (this.ability == Ability.BEAST_BOOST) {
						Task.addAbilityTask(this);
						stat(this, getHighestStat(), 1, foe);
					}
				}
			}
			
			if (multiplier > 1 && !foe.isFainted()) {
				if (foe.getItem() == Item.WEAKNESS_POLICY) {
					Task.addTask(Task.TEXT, foe.nickname + " used its " + foe.item.toString() + "!");
					stat(foe, 0, 2, this);
					stat(foe, 2, 2, this);
					foe.consumeItem(this);
				}
				if (foe.getItem() == Item.ENIGMA_BERRY) {
					foe.eatBerry(foe.item, true, this);
				}
			}
			
			if (recoil != 0) {
				this.damage(recoil, foe, this.nickname + " was damaged by recoil!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, foe);
				}
			}
			
			if (!this.isFainted() && contact && checkSecondary(30) && this.status == Status.HEALTHY) {
				if (foeAbility == Ability.FLAME_BODY) {
					burn(false, this, foe);
				}
				if (foeAbility == Ability.STATIC && this.status == Status.HEALTHY) {
					paralyze(false, this, foe);
				}
				if (foeAbility == Ability.POISON_POINT && this.status == Status.HEALTHY) {
					poison(false, this, foe);
				}
			}
			
			if (contact) {
				if (foeAbility == Ability.ROUGH_SKIN || foeAbility == Ability.IRON_BARBS) {
					Task.addAbilityTask(foe);
					this.damage(this.getHPAmount(1.0/8), foe);
					Task.addTask(Task.TEXT, this.nickname + " was hurt!");
				}
				if (foe.getItem() == Item.ROCKY_HELMET) {
					this.damage(this.getHPAmount(1.0/8), foe);
					Task.addTask(Task.TEXT, this.nickname + " was hurt by the Rocky Helmet!");
				}
				if ((foeAbility == Ability.ROUGH_SKIN || foeAbility == Ability.IRON_BARBS || foe.getItem() == Item.ROCKY_HELMET) && this.currentHP <= 0) { // Check for kill
					this.faint(true, foe);
					if (move == Move.FELL_STINGER) stat(this, 0, 3, foe);
				}
				if (this.ability == Ability.POISON_TOUCH && foe.ability != Ability.SHIELD_DUST && foe.status == Status.HEALTHY && checkSecondary(30)) {
					foe.poison(false, this, this);
				}
				if (foeAbility == Ability.GOOEY) {
					Task.addAbilityTask(foe);
					stat(this, 4, -1, foe);
				}
				if (this.item == null && foe.getItem() == Item.STICKY_BARB) {
					Task.addTask(Task.TEXT, "The Sticky Barb clinged to " + this.nickname + "!");
					this.item = foe.item;
					if (!foe.loseItem) foe.lostItem = foe.item;
					foe.consumeItem(this);
					this.loseItem = true;
				}
			}
			
			
			if (move.isPhysical()) {
				if (foeAbility == Ability.WEAK_ARMOR) {
					Task.addAbilityTask(foe);
					stat(foe, 1, -1, this);
					stat(foe, 4, 2, this);
				}
				if (foeAbility == Ability.TOXIC_DEBRIS) {
					Task.addAbilityTask(foe);
					field.setHazard(this.getFieldEffects(), field.new FieldEffect(Effect.TOXIC_SPIKES));
				}
			}
			
			if ((moveType == PType.BUG || moveType == PType.GHOST || moveType == PType.DARK) && foeAbility == Ability.RATTLED && !foe.isFainted()) {
				Task.addAbilityTask(foe);
				stat(foe, 4, 1, this);
			}
			
			if (moveType == PType.DARK && foeAbility == Ability.JUSTIFIED && !foe.isFainted()) {
				Task.addAbilityTask(foe);
				stat(foe, 0, 2, this);
			}
			if (!sheer) {
				if ((foe.getItem() == Item.JABOCA_BERRY && move.isPhysical()) || (foe.getItem() == Item.ROWAP_BERRY && !move.isPhysical())) {
					foe.eatBerry(foe.item, true, this);
				}
				if ((foe.getItem() == Item.KEE_BERRY && move.isPhysical()) || (foe.getItem() == Item.MARANGA_BERRY && !move.isPhysical())) {
					foe.eatBerry(foe.item, true, this);
				}
				if (foe.getItem() == Item.EJECT_BUTTON && !foe.isFainted()) {
					if (foe.trainer != null && foe.trainer.hasValidMembers()) {
						foeEject = true;
						hit = numHits;
					}
				}
			}
			if (move == Move.POP_POP && hit == numHits) {
				Task.addTask(Task.TEXT, "Hit " + hits + " times!");
			} else if ((hit == numHits && hit > 1) || move == Move.BEAT_UP) {
				Task.addTask(Task.TEXT, "Hit " + hit + " times!");
			}
			
			if (first && this.getItem() == Item.KING1S_ROCK && foe.ability != Ability.SHIELD_DUST && !foe.hasStatus(Status.FLINCHED) && checkSecondary(10)) {
				foe.addStatus(Status.FLINCHED);
			}
		}
		if (move.secondary < 0) {
			primaryEffect(foe, move, foeEject);
		}
		if (checkSecondary(secChance)) {
			secondaryEffect(foe, move, first);
		}
		if (!foe.isFainted() && this.ability == Ability.RADIANT && moveType == PType.LIGHT && foe.getItem() != Item.COVERT_CLOAK && foeAbility != Ability.SHIELD_DUST) {
			Task.addAbilityTask(this);
			stat(foe, 5, -1, this, true);
		}
		
		if (!sheer && this.getItem() == Item.LIFE_ORB && !this.isFainted()) {
			this.damage(getHPAmount(1.0/10), foe);
			Task.addTask(Task.TEXT, this.nickname + " lost some of its HP!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, foe);
			}
		}
		
		if ((move == Move.VOLT_SWITCH || move == Move.FLIP_TURN || move == Move.U$TURN) && !this.isFainted()) {
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				Task.addTask(Task.TEXT, this.nickname + " went back to " + enemy.toString() + "!");
			}
			this.addStatus(Status.SWITCHING);
		}
		
		if (move == Move.SELF$DESTRUCT || move == Move.EXPLOSION || move == Move.SUPERNOVA_EXPLOSION) {
			this.damage(this.currentHP, foe, "");
			this.faint(true, foe);
		}
		if (move == Move.HYPER_BEAM || move == Move.BLAST_BURN || move == Move.FRENZY_PLANT || move == Move.GIGA_IMPACT
				|| move == Move.HYDRO_CANNON || move == Move.MAGIC_CRASH || move == Move.SWORD_OF_DAWN) {
			if (move == Move.SWORD_OF_DAWN && foe.isFainted()) {
				// Nothing: don't have to rest
			} else {
				if (this.getItem() == Item.POWER_HERB) {
					this.consumeItem(foe);
					Task.addTask(Task.TEXT, this.nickname + " used its Power Herb to regain its lost energy!");
				} else {
					this.addStatus(Status.RECHARGE);
				}
			}
		}
		
		if (!sheer && foe.getItem() == Item.RED_CARD) {
			int index = tasks.size();
			boolean result = false;
			if (this.trainerOwned() && enemy != null) {
				result = enemy.swapRandom(foe);
			} else if (this.playerOwned()) {
				result = player.swapRandom(foe);
			}
			if (result) {
				Task t = Task.createTask(Task.TEXT, foe.nickname + " held up its Red Card!");
				Task.insertTask(t, index);
				foe.consumeItem(this);
				endMove();
				this.rollCount = 1;
				this.metronome = 0;
				return;
			}
		}
		
		if (foeEject) {
			Task.addTask(Task.TEXT, foe.nickname + " switched out using its Eject Button!");
			foe.consumeItem(this);
			foe.addStatus(Status.SWITCHING);
		}
		
		if (move == Move.MAGIC_BLAST) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.getAllMoves()) {
				if (cand.mtype == PType.ROCK || cand.mtype == PType.GRASS || cand.mtype == PType.GROUND) {
					moves.add(cand);
				}
			}
			Move[] validMoves = moves.toArray(new Move[moves.size()]);
			this.move(foe, validMoves[new Random().nextInt(validMoves.length)], first);
		}
		if (move == Move.ELEMENTAL_SPARKLE) {
			ArrayList<Move> moves = new ArrayList<>();
			for (Move cand : Move.getAllMoves()) {
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
				
				if (m.currentPP == 0) {
					if (this.getItem() == Item.LEPPA_BERRY) {
						this.eatBerry(this.item, true, foe, m.move);
					} else {
						this.encoreCount = 0;
					}
				}
			}
		}
	}
	
	private void announceUseMove(Move move) {
		String msg = this.nickname + " used " + move.toString() + "!";
		if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			msg = writeMoveChance(move, msg);
		}
		Task t = Task.addTask(Task.SEMI_INV, msg, this);
		t.wipe = true;
	}

	private String writeMoveChance(Move move, String msg) {
		Boolean p1Moves = null;
		ArrayList<Move> check = null;
		if (gp.simBattleUI.p1Moves != null) {
			if (this == gp.simBattleUI.p1Moves.getFirst()) p1Moves = true;
		}
		if (gp.simBattleUI.p2Moves != null) {
			if (this == gp.simBattleUI.p2Moves.getFirst()) p1Moves = false;
		}
		
		if (p1Moves != null) {
			if (p1Moves) {
				check = gp.simBattleUI.p1Moves.getSecond();
			} else {
				check = gp.simBattleUI.p2Moves.getSecond();
			}
		}
		
		if (check == null) return msg;

		int occ = 0;
		for (Move m : check) {
			if (m == move) occ++;
		}
		int total = check.size();
		double chance = occ * 100.0 / total;
		
		msg = String.format("%s\n[%.1f", msg, chance) + "% chance]";
		
		if (p1Moves) {
			gp.simBattleUI.p1Moves = null;
		} else {
			gp.simBattleUI.p2Moves = null;
		}
		
		return msg;
	}

	private void endMove() {
		impressive = false;
		success = true;
	}
	
	private boolean fail() {
		return fail(true);
	}

	private boolean fail(boolean announce) {
		if (announce) Task.addTask(Task.TEXT, "But it failed!");
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
		if (this.getItem() == Item.OCCA_BERRY && type == PType.FIRE) return true;
		if (this.getItem() == Item.PASSHO_BERRY && type == PType.WATER) return true;
		if (this.getItem() == Item.WACAN_BERRY && type == PType.ELECTRIC) return true;
		if (this.getItem() == Item.RINDO_BERRY && type == PType.GRASS) return true;
		if (this.getItem() == Item.YACHE_BERRY && type == PType.ICE) return true;
		if (this.getItem() == Item.CHOPLE_BERRY && type == PType.FIGHTING) return true;
		if (this.getItem() == Item.KEBIA_BERRY && type == PType.POISON) return true;
		if (this.getItem() == Item.SHUCA_BERRY && type == PType.GROUND) return true;
		if (this.getItem() == Item.COBA_BERRY && type == PType.FLYING) return true;
		if (this.getItem() == Item.PAYAPA_BERRY && type == PType.PSYCHIC) return true;
		if (this.getItem() == Item.TANGA_BERRY && type == PType.BUG) return true;
		if (this.getItem() == Item.CHARTI_BERRY && type == PType.ROCK) return true;
		if (this.getItem() == Item.KASIB_BERRY && type == PType.GHOST) return true;
		if (this.getItem() == Item.HABAN_BERRY && type == PType.DRAGON) return true;
		if (this.getItem() == Item.COLBUR_BERRY && type == PType.DARK) return true;
		if (this.getItem() == Item.BABIRI_BERRY && type == PType.STEEL) return true;
		if (this.getItem() == Item.CHILAN_BERRY && type == PType.NORMAL) return true;
		if (this.getItem() == Item.ROSELI_BERRY && type == PType.LIGHT) return true;
		if (this.getItem() == Item.MYSTICOLA_BERRY && type == PType.MAGIC) return true;
		if (this.getItem() == Item.GALAXEED_BERRY && type == PType.GALACTIC) return true;
		return false;
	}
	
	private void handleWhiteHerb(int[] oldStages, Pokemon foe) {
		if (oldStages == null) return;
		int[] restore = new int[statStages.length];
		boolean herb = false;
		for (int i = 0; i < statStages.length; i++) {
			int change = Math.max(oldStages[i] - statStages[i], 0);
			if (change > 0) herb = true;
			restore[i] = change;
		}
		
		if (herb) {
			for (int i = 0; i < restore.length; i++) {
				statStages[i] += restore[i];
			}
			
			Task.addTask(Task.TEXT, this.nickname + " returned its stats to normal using its White Herb!");
			this.consumeItem(foe);
		}
	}
	
	private void handleMirrorHerb(int[] foeOldStages, Pokemon foe) {
		if (foeOldStages == null) return;
		int[] boost = new int[statStages.length];
		boolean herb = false;
		for (int i = 0; i < statStages.length; i++) {
			int change = foe.statStages[i] - foeOldStages[i];
			if (change > 0) {
				herb = true;
				boost[i] = change;
			}
		}
		
		if (herb) {
			Task.addTask(Task.TEXT, this.nickname + " used its Mirror Herb to copy " + foe.nickname + "'s stat boosts!");
			for (int i = 0; i < boost.length; i++) {
				if (boost[i] > 0) stat(this, i, boost[i], foe);
			}
			this.consumeItem(foe);
		}		
	}
	
	private void handleEjectPack(int[] oldStages, Pokemon foe) {
		if (oldStages == null) return;
		if (this.trainer == null || !this.trainer.hasValidMembers()) return;
		
		if (!this.arrayGreaterOrEqual(this.statStages, oldStages)) {
			Task.addTask(Task.TEXT, this.nickname + " switched out using its Eject Pack!");
			this.consumeItem(foe);
			this.addStatus(Status.SWITCHING);
		}
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
	            	if (p.item == Item.LUCKY_EGG) expAwarded = (int) Math.ceil(expAwarded * 1.5);
	                p.exp += expAwarded;
	                String flavor = p.item == Item.LUCKY_EGG ? "a boosted " : p.item == Item.EXP_SHARE && !p.visible ? "a shared " : "";
                	Task t = Task.addTask(Task.EXP, p.nickname + " gained " + flavor + expAwarded + " experience points!", p);
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
		if (this instanceof Egg) return;
		int deduc = 0;
		if (this.item == Item.SOOTHE_BELL && i > 0) i *= 2;
		if (this.ball == Item.LUXURY_BALL) i *= 2;
		if (!override) {
			this.happinessCap -= i;
			deduc = happinessCap < 0 ? Math.abs(happinessCap) : 0;
			this.happinessCap += deduc;
		}
		this.happiness += i;
		this.happiness -= deduc;
		this.happiness = this.happiness > 255 ? 255 : this.happiness;
	}
	
	private void primaryEffect(Pokemon foe, Move move, boolean eject) {
		if (move == Move.BRICK_BREAK || move == Move.PSYCHIC_FANGS) {
			if (field.remove(foe.getFieldEffects(), Effect.REFLECT)) Task.addTask(Task.TEXT, this.nickname + " broke " + foe.nickname + "'s Reflect!");
			if (field.remove(foe.getFieldEffects(), Effect.LIGHT_SCREEN)) Task.addTask(Task.TEXT, this.nickname + " broke " + foe.nickname + "'s Light Screen wore off!");
			if (field.remove(foe.getFieldEffects(), Effect.AURORA_VEIL)) Task.addTask(Task.TEXT, this.nickname + " broke " + foe.nickname + "'s Aurora Veil wore off!");
		} else if (move == Move.BURN_UP) {
			if (this.type1 == PType.FIRE) type1 = PType.UNKNOWN;
			if (this.type2 == PType.FIRE) type1 = null;
		} else if ((move == Move.CIRCLE_THROW || move == Move.DRAGON_TAIL) && !foe.isFainted()) {
			if (foe.trainer != null) {
				foe.trainer.swapRandom(foe);
			}
		} else if (move == Move.CLOSE_COMBAT) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
		} else if (move == Move.COVET || move == Move.THIEF) {
			if (this.item == null && foe.item != null && !(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
				Task.addTask(Task.TEXT, this.nickname + " stole the foe's " + foe.item.toString() + "!");
				this.item = foe.item;
				if (!foe.loseItem) foe.lostItem = foe.item;
				foe.consumeItem(this);
				this.loseItem = true;
				foe.metronome = 0;
			}
		} else if (move == Move.DRACO_METEOR) {
			stat(this, 2, -2, foe);
		} else if (move == Move.DRAGON_ASCENT) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
		} else if (move == Move.HAMMER_ARM) {
			stat(this, 4, -1, foe);
		} else if (move == Move.KNOCK_OFF) {
			if (foe.item != null && !(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
				if (foe.getItem() == Item.EJECT_BUTTON && eject) return;
				Item oldItem = foe.item;
				if (foe.lostItem == null) foe.lostItem = foe.item;
				Task.addTask(Task.TEXT, this.nickname + " knocked off " + foe.nickname + "'s " + oldItem.toString() + "!");
				foe.consumeItem(this);
			}
		} else if (move == Move.LEAF_STORM) {
			stat(this, 2, -2, foe);
		} else if (move == Move.METEOR_ASSAULT) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
		} else if (move == Move.OVERHEAT) {
			stat(this, 2, -2, foe);
		} else if (move == Move.PHOTON_GEYSER) {
			stat(this, 2, -2, foe);
		} else if (move == Move.SCALE_SHOT) {
			stat(this, 1, -1, foe);
			stat(this, 4, 1, foe);
		} else if (move == Move.TRICK_TACKLE) {
			if (this.tricked != foe) {
				if (!(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
					if (this.item != null || foe.item != null) {
						Task.addTask(Task.TEXT, this.nickname + " switched items with its target!");
						Item userItem = this.item;
						Item foeItem = foe.item;
						if (userItem != null) Task.addTask(Task.TEXT, foe.nickname + " obtained a " + userItem.toString() + "!");
						if (foeItem != null) Task.addTask(Task.TEXT, this.nickname + " obtained a " + foeItem.toString() + "!");
						this.item = foeItem;
						foe.item = userItem;
						if (this.item != Item.METRONOME) this.metronome = 0;
						if (foe.item != Item.METRONOME) foe.metronome = 0;
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
						if (foe.item == null && foeItem != null) {
							foe.consumeItem(this);
						}
						if (this.item == null && userItem != null) {
							this.consumeItem(foe);
						}
						this.tricked = foe;
					}
				}
			}
		} else if (move == Move.V$CREATE) {
			stat(this, 1, -1, foe);
			stat(this, 3, -1, foe);
			stat(this, 4, -1, foe);
		} else if (move == Move.WHIP_SMASH) {
			stat(this, 1, -1, foe);
		}
	}

	private void secondaryEffect(Pokemon foe, Move move, boolean first) {
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
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.ARCANE_SPELL) {
			foe.incrementStatus(Status.ARCANE_SPELL, 10);
			Task.addTask(Task.TEXT, "The spell caused " + foe.nickname + " to become weaker!");
		} else if (move == Move.ASTONISH && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.AURORA_BEAM) {
			stat(foe, 0, -1, this);
		} else if (move == Move.BEEFY_BASH) {
			foe.paralyze(false, this);
		} else if (move == Move.BIND) {
			if (!foe.hasStatus(Status.SPUN) && foe.spunCount == 0 && !foe.isFainted()) {
				foe.addStatus(Status.SPUN, this.getItem() == Item.BINDING_BAND ? 6 : 8);
				foe.spunCount = (((int) (Math.random() * 2)) + 4);
				if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
				Task.addTask(Task.TEXT, foe.nickname + " was wrapped by " + this.nickname + "!");
			}
		} else if (move == Move.BITE && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.BREAKING_SWIPE) {
			stat(foe, 0, -1, this);
		} else if (move == Move.BLAZE_KICK) {
			foe.burn(false, this);
		} else if (move == Move.BLIZZARD) {
			foe.freeze(false, this);
		} else if (move == Move.BLUE_FLARE) {
			foe.burn(false, this);
		} else if (move == Move.BODY_SLAM) {
			foe.paralyze(false, this);
		} else if (move == Move.BOLT_STRIKE) {
			foe.paralyze(false, this);
		} else if (move == Move.BOUNCE) {
			foe.paralyze(false, this);
		} else if (move == Move.BUBBLEBEAM) {
			stat(foe, 4, -1, this);
		} else if (move == Move.BUG_BITE || move == Move.PLUCK) {
			if (foe.getItem() != null && foe.item.isBerry() && !(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
				Task.addTask(Task.TEXT, this.nickname + " stole and ate " + foe.nickname + "'s " + foe.item.toString() + "!");
				eatBerry(foe.item, false, foe);
				foe.consumeItem(this);
			}
		} else if (move == Move.BUG_BUZZ) {
			stat(foe, 3, -1, this);
		} else if (move == Move.BULLDOZE) {
			stat(foe, 4, -1, this);
		} else if (move == Move.CHARGE_BEAM) {
			stat(this, 2, 1, this);
		} else if (move == Move.CONFUSION) {
			foe.confuse(false, this);
		} else if (move == Move.CORE_ENFORCER && !first) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = Ability.NULL;
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability was suppressed!");	
			}
		} else if (move == Move.CROSS_POISON) {
			foe.poison(false, this);
		} else if (move == Move.CRUNCH) {
			stat(foe, 1, -1, this);
		} else if (move == Move.DARK_PULSE && first) {
			foe.addStatus(Status.FLINCHED);
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
		} else if (move == Move.DIRE_CLAW) {
			Random random = new Random();
			int type = random.nextInt(3);
			if (type == 0) {
				foe.paralyze(false, this);
			} else if (type == 1) {
				foe.poison(false, this);
			} else {
				foe.sleep(false, this);
			}
		} else if (move == Move.DISCHARGE) {
			foe.paralyze(false, this);
		} else if (move == Move.DIZZY_PUNCH) {
			foe.confuse(false, this);
		} else if (move == Move.DIAMOND_STORM) {
			stat(this, 1, 2, foe);
		} else if (move == Move.DRAGON_RUSH && first) {
			foe.addStatus(Status.FLINCHED);
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
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.FAKE_OUT && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.UNSEEN_STRANGLE && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.FIERY_DANCE) {
			stat(this, 2, 1, foe);
		} else if (move == Move.FIERY_DANCE && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.FIRE_BLAST) {
			foe.burn(false, this);
		} else if (move == Move.FATAL_BIND) {
			this.perishCount = (this.perishCount == 0) ? 4 : this.perishCount;
			foe.perishCount = (foe.perishCount == 0) ? 4 : foe.perishCount;
		} else if (move == Move.FIRE_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.burn(false, this);
			} else if (randomNum == 1 && first) {
				foe.addStatus(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.addStatus(Status.FLINCHED);
				foe.burn(false, this);
			}
		} else if (move == Move.FIRE_PUNCH) {
			foe.burn(false, this);
		} else if (move == Move.FIRE_SPIN) {
			if (!foe.hasStatus(Status.SPUN) && foe.spunCount == 0 && !foe.isFainted()) {
				if (!foe.isType(PType.FIRE)) {
					foe.addStatus(Status.SPUN, this.getItem() == Item.BINDING_BAND ? 6 : 8);
					foe.spunCount = (((int) (Math.random() * 2)) + 4);
					if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
					Task.addTask(Task.TEXT, foe.nickname + " was trapped in a fiery vortex!");
				}
			}
		} else if (move == Move.WHIRLPOOL) {
			if (!foe.hasStatus(Status.SPUN) && foe.spunCount == 0 && !foe.isFainted()) {
				if (!foe.isType(PType.FIRE)) {
					foe.addStatus(Status.SPUN, this.getItem() == Item.BINDING_BAND ? 6 : 8);
					foe.spunCount = (((int) (Math.random() * 2)) + 4);
					if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
					Task.addTask(Task.TEXT, foe.nickname + " was trapped in a whirlpool vortex!");
				}
			}
		} else if (move == Move.WRAP) {
			if (!foe.hasStatus(Status.SPUN) && foe.spunCount == 0 && !foe.isFainted()) {
				foe.addStatus(Status.SPUN, this.getItem() == Item.BINDING_BAND ? 6 : 8);
				foe.spunCount = (((int) (Math.random() * 2)) + 4);
				if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
				Task.addTask(Task.TEXT, foe.nickname + " was wrapped by " + this.nickname + "!");
			}
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
		} else if (move == Move.SOLSTICE_BLADE) {
			stat(foe, 1, -1, this);
		} else if (move == Move.GLITTERING_TORNADO) {
			stat(foe, 5, -1, this);
		} else if (move == Move.GLITZY_GLOW) {
			stat(this, 3, 1, foe);
		} else if (move == Move.GUNK_SHOT) {
			foe.poison(false, this);
		} else if (move == Move.HEADBUTT && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.HEAT_WAVE) {
			foe.burn(false, this);
		} else if (move == Move.HEX_CLAW) {
			if (foe.disabledMove == null && foe.lastMoveUsed != null && foe.lastMoveUsed.pp > 0) {
				foe.disabledMove = foe.lastMoveUsed;
				foe.disabledCount = 3;
				Task.addTask(Task.TEXT, foe.nickname + "'s " + foe.disabledMove + " was disabled!");
				if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its disable using its Mental Herb!");
					foe.disabledMove = null;
					foe.disabledCount = 0;
					foe.consumeItem(this);
				}
			}
		} else if (move == Move.HOCUS_POCUS) {
			int randomNum = new Random().nextInt(5);
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
		} else if (move == Move.HYDRO_VORTEX) {
			boolean random = new Random().nextBoolean();
			if (random) {
				foe.confuse(false, this);
			} else {
				if (first) foe.addStatus(Status.FLINCHED);
			}
		} else if (move == Move.HYPER_FANG && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.INCINERATE) {
			if (foe.item != null && foe.item.isBerry() && !(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
				Task.addTask(Task.TEXT, this.nickname + " incinerated " + foe.nickname + "'s " + foe.item.toString() + "!");
				foe.item = null;
			}
		} else if (move == Move.ICE_BEAM) {
			foe.freeze(false, this);
		} else if (move == Move.ICE_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.freeze(false, this);
			} else if (randomNum == 1 && first) {
				foe.addStatus(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.addStatus(Status.FLINCHED);
				foe.freeze(false, this);
			}
		} else if (move == Move.ICE_PUNCH) {
			foe.freeze(false, this);
		} else if (move == Move.ICICLE_CRASH && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.ICE_SPINNER) {
			if (field.terrain != null) {
				field.terrain = null;
				field.terrainTurns = 0;
				Task t = Task.addTask(Task.TERRAIN, "The terrain returned to normal!");
				t.setEffect(null);
			}
		} else if (move == Move.ICY_WIND) {
			stat(foe, 4, -1, this);
		} else if (move == Move.INFERNO) {
			foe.burn(false, this);
		}  else if (move == Move.INFESTATION) {
			if (!foe.hasStatus(Status.SPUN) && foe.spunCount == 0 && !foe.isFainted()) {
				foe.addStatus(Status.SPUN, this.getItem() == Item.BINDING_BAND ? 6 : 8);
				foe.spunCount = (((int) (Math.random() * 2)) + 4);
				if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
				Task.addTask(Task.TEXT, foe.nickname + " was infested by " + this.nickname + "!");
			}
		} else if (move == Move.IRON_BLAST && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.IRON_HEAD && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.IRON_TAIL) {
			stat(foe, 1, -1, this);
		} else if (move == Move.JAW_LOCK) {
			if (!foe.hasStatus(Status.TRAPPED) && !foe.isFainted()) {
				foe.addStatus(Status.TRAPPED);
				Task.addTask(Task.TEXT, foe.nickname + " was trapped!");
			}
		} else if (move == Move.LAVA_PLUME) {
			foe.burn(false, this);
		} else if (move == Move.LEAF_TORNADO) {
			stat(foe, 5, -1, this);
		} else if (move == Move.LICK) {
			foe.paralyze(false, this);
		} else if (move == Move.LIGHT_BEAM) {
			stat(this, 2, 1, foe);
		} else if (move == Move.LIQUIDATION) {
			stat(foe, 1, -1, this);
		} else if (move == Move.LOW_SWEEP) {
			stat(foe, 4, -1, this);
		} else if (move == Move.LUSTER_PURGE) {
			stat(foe, 3, -1, this);
    	} else if (move == Move.STAFF_JAB) {
    		stat(foe, 0, -2, this);
		} else if (move == Move.MAGIC_CRASH) {
			int randomNum = new Random().nextInt(5);
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
			if (multiplier > 1) foe.addStatus(Status.FLINCHED);
		} else if (move == Move.MANA_PUNCH) {
			int randomNum = new Random().nextInt(8);
			if (randomNum < 7) {
				stat(this, randomNum, 1, foe);
			} else {
				if (first) foe.addStatus(Status.FLINCHED);
			}
		} else if (move == Move.METAL_CLAW) {
			stat(this, 0, 1, foe);
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
			if (this.hasStatus(Status.SPUN)) {
				this.removeStatus(Status.SPUN);
				Task.addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
			for (FieldEffect fe : field.getHazards(this.getFieldEffects())) {
				Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				this.getFieldEffects().remove(fe);
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
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.NIGHT_DAZE) {
			stat(foe, 5, -1, this);
		} else if (move == Move.PLAY_ROUGH) {
			stat(foe, 0, -1, this);
		} else if (move == Move.POISON_FANG) {
			foe.toxic(false, this);
		} else if (move == Move.POISON_JAB) {
			foe.poison(false, this);
		} else if (move == Move.POISON_STING) {
			foe.poison(false, this);
		} else if (move == Move.POWDER_SNOW) {
			foe.freeze(false, this);
		} else if (move == Move.POWER$UP_PUNCH) {
			stat(this, 0, 1, foe);
		} else if (move == Move.PSYBEAM) {
			foe.confuse(false, this);
		} else if (move == Move.PSYCHIC) {
			stat(foe, 3, -1, this);
		} else if (move == Move.PSYCHIC_NOISE) {
			if (!foe.hasStatus(Status.HEAL_BLOCK)) {
				foe.addStatus(Status.HEAL_BLOCK);
				foe.healBlockCount = 2;
				Task.addTask(Task.TEXT, foe.nickname + " was prevented from healing!");
				if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its Heal Block using its Mental Herb!");
					foe.removeStatus(Status.HEAL_BLOCK);
					foe.healBlockCount = 0;
					foe.consumeItem(this);
				}
			}
		} else if (move == Move.RAPID_SPIN) {
			stat(this, 4, 1, foe);
			if (this.hasStatus(Status.SPUN)) {
				this.removeStatus(Status.SPUN);
				Task.addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
			for (FieldEffect fe : field.getHazards(this.getFieldEffects())) {
				Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				this.getFieldEffects().remove(fe);
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
			field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS));
		} else if (move == Move.SACRED_FIRE) {
			foe.burn(false, this);
		} else if (move == Move.SAMBAL_SEAR) {
			foe.burn(false, this);
		} else if (move == Move.SCALD) {
			foe.burn(false, this);
		} else if (move == Move.SCORCHING_SANDS) {
			foe.burn(false, this);
		} else if (move == Move.SHADOW_BALL) {
			stat(foe, 3, -1, this);
		} else if (move == Move.SIGNAL_BEAM) {
			foe.confuse(false, this);
		} else if (move == Move.SILVER_WIND) {
			for (int i = 0; i < 5; ++i) {
				stat(this, i, 1, foe);
			}
		} else if (move == Move.SMACK_DOWN && !foe.hasStatus(Status.SMACK_DOWN)) {
			foe.addStatus(Status.SMACK_DOWN);
			Task.addTask(Task.TEXT, foe.nickname + " was grounded!");
		} else if (move == Move.SLUDGE_WAVE) {
			foe.poison(false, this);
		} else if (move == Move.SPOTLIGHT_RAY) {
			if (!foe.hasStatus(Status.ENCORED)) {
				foe.addStatus(Status.ENCORED);
				foe.encoreCount = 2;
				Task.addTask(Task.TEXT, foe.nickname + " was encored!");
			}
		} else if (move == Move.SUMMIT_STRIKE) {
		    stat(foe, 1, -1, this);
		    double random = Math.random();
		    if (random < 0.3 && first) {
		        foe.addStatus(Status.FLINCHED);
		    }
		} else if (move == Move.SUNNY_BURST) {
			boolean success = field.setWeather(field.new FieldEffect(Effect.SUN));
			if (success && item == Item.HEAT_ROCK) field.weatherTurns = 8;
		} else if (move == Move.NUZZLE) {
			foe.paralyze(false, this);
		} else if (move == Move.SKY_ATTACK && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.SLOW_FALL) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = Ability.LEVITATE;
				Task.addTask(Task.TEXT, foe.nickname + "'s ability was changed to Levitate!");
			}
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
				Task.addTask(Task.STATUS, Status.HEALTHY, nickname + " was cured of its burn!", this);
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
		} else if (move == Move.SPIRIT_BREAK) {
			stat(foe, 2, -1, this);
		} else if (move == Move.STEEL_WING) {
			stat(this, 1, 1, foe);
		} else if (move == Move.STRUGGLE_BUG) {
			stat(foe, 2, -1, this);
		} else if (move == Move.STOMP && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.SUPERPOWER) {
			stat(this, 0, -1, foe);
			stat(this, 1, -1, foe);
		} else if (move == Move.SWORD_SPIN) {
			stat(this, 0, 1, foe);
		} else if (move == Move.THROAT_CHOP) {
			if (!foe.hasStatus(Status.MUTE)) foe.addStatus(Status.MUTE);
		} else if (move == Move.THUNDER) {
			foe.paralyze(false, this);
		} else if (move == Move.THUNDERBOLT) {
			foe.paralyze(false, this);
		} else if (move == Move.THUNDER_FANG) {
			int randomNum = ((int) Math.random() * 3);
			if (randomNum == 0) {
				foe.paralyze(false, this);
			} else if (randomNum == 1 && first) {
				foe.addStatus(Status.FLINCHED);
			}
			 else if (randomNum == 2) {
				if (first) foe.addStatus(Status.FLINCHED);
				foe.paralyze(false, this);
			}
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
			if (this.hasStatus(Status.SPUN)) {
				this.removeStatus(Status.SPUN);
				Task.addTask(Task.TEXT, this.nickname + " was freed!");
				this.spunCount = 0;
			}
		} else if (move == Move.MAGIC_TOMB) {
			stat(foe, 0, -1, this);
			stat(foe, 2, -1, this);
		} else if (move == Move.TWINEEDLE) {
			foe.poison(false, this);
		} else if (move == Move.TWISTER && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.VENOM_SPIT) {
			foe.paralyze(false, this);
		} else if (move == Move.VENOSTEEL_CROSSCUT) {
			Random random = new Random();
			boolean type = random.nextBoolean();
			if (type) {
				foe.paralyze(false, this);
			} else {
				foe.toxic(false, this);
			}
		} else if (move == Move.VOLT_TACKLE) {
			foe.paralyze(false, this);
		} else if (move == Move.VINE_CROSS) {
			stat(foe, 4, -1, this);
		} else if (move == Move.VITRIOLIC_HEX) {
			foe.incrementStatus(Status.CRIT_CHANCE, -1);
		} else if (move == Move.WATER_PULSE) {
			foe.confuse(false, this);
		} else if (move == Move.WATER_CLAP) {
			foe.paralyze(false, this);
		} else if (move == Move.WATER_SMACK && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.SUPERCHARGED_SPLASH) {
			stat(this, 2, 3, foe);
		} else if (move == Move.VANISHING_ACT) {
			foe.confuse(false, this);
		} else if (move == Move.WATERFALL && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.SPIRIT_BREAK) {
			stat(this, 2, -1, foe);
		} else if (move == Move.ZEN_HEADBUTT && first) {
			foe.addStatus(Status.FLINCHED);
		} else if (move == Move.ROCK_SLIDE && first) {
			foe.addStatus(Status.FLINCHED);
		}  else if (move == Move.ZAP_CANNON) {
			foe.paralyze(false, this);
		} else if (move == Move.ZING_ZAP && first) {
			foe.addStatus(Status.FLINCHED);
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
	
	private void statusEffect(Pokemon foe, Move move, Player player, Trainer enemy) {
		statusEffect(foe, move, player, enemy, true);
	}

	private void statusEffect(Pokemon foe, Move move, Player player, Trainer enemy, boolean announce) {
		boolean fail = false;
		if (announce && (move == Move.ABDUCT || move == Move.TAKE_OVER)) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success) && !foe.hasStatus(Status.POSSESSED)) {
				foe.addStatus(Status.POSSESSED);
				Task.addTask(Task.TEXT, this.nickname + " posessed " + foe.nickname + "!");
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (announce && move == Move.MAGIC_REFLECT) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success) && !hasStatus(Status.MAGIC_REFLECT)) {
				this.addStatus(Status.MAGIC_REFLECT);
				Task.addTask(Task.TEXT, "A magical reflection surrounds " + this.nickname + "!");
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
		} else if (move == Move.ACID_ARMOR) {
			stat(this, 1, 2, foe, announce);
		} else if (move == Move.ACUPRESSURE) {
			if (this.hasMaxedStatStages(true)) {
				fail = fail(announce);
			} else {
				Random random = new Random();
				int stat;
				do {
					stat = random.nextInt(7);
				} while(this.statStages[stat] >= 6);
				stat(this, new Random().nextInt(7), 2, foe, announce);
			}
		} else if (move == Move.AGILITY) {
			stat(this, 4, 2, foe, announce);
		} else if (move == Move.ALCHEMY) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				if (this.item != null) {
					Item oldItem = this.item;
					if (this.lostItem == null) this.lostItem = this.item;
					heal(getHPAmount(1.0/1), this.nickname + " dissolved its " + oldItem.toString() + " to heal itself\nto full!");
					this.consumeItem(foe);
				} else {
					heal(getHPAmount(1.0/3), this.nickname + " restored HP.");
				}
			}
		} else if (move == Move.AMNESIA) {
			stat(this, 3, 2, foe, announce);
		} else if (announce && move == Move.AROMATHERAPY) {
			Task.addTask(Task.TEXT, "A soothing aroma wafted through the air!");
			Pokemon[] team = this.trainer == null ? null : this.trainer.getTeam();
			if (team == null) {
				if (this.status != Status.HEALTHY) {
					if (announce) Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " was cured of its " + this.status.getName() + "!", this);
					this.status = Status.HEALTHY;
				}
			} else {
				for (Pokemon p : team) {
					if (p != null && !p.isFainted() && p.status != Status.HEALTHY) {
						if (announce) Task.addTask(Task.STATUS, Status.HEALTHY, p.nickname + " was cured of its " + p.status.getName() + "!", this);
						p.status = Status.HEALTHY;
					}
				}
			}
		} else if (announce && move == Move.AQUA_RING) {
			if (!(this.hasStatus(Status.AQUA_RING))) {
			    this.addStatus(Status.AQUA_RING);
			    if (announce) Task.addTask(Task.TEXT, "A veil of water surrounded " + this.nickname + "!");
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.AURORA_GLOW) {
			if (!(field.contains(this.getFieldEffects(), Effect.AURORA))) {
				this.getFieldEffects().add(field.new FieldEffect(Effect.AURORA));
				if (announce) Task.addTask(Task.TEXT, "A glowing Aurora surrounded " + this.nickname + "'s team!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.AURORA_VEIL) {
			if (field.equals(field.weather, Effect.SNOW, this)) {
				if (!(field.contains(this.getFieldEffects(), Effect.AURORA_VEIL))) {
					FieldEffect a = field.new FieldEffect(Effect.AURORA_VEIL);
					if (this.getItem() == Item.LIGHT_CLAY) a.turns = 8;
					this.getFieldEffects().add(a);
					if (announce) Task.addTask(Task.TEXT, "Aurora Veil made " + this.nickname + "'s team stronger against\nPhysical and Special moves!");
				} else {
					fail = fail(announce);
				}
			} else { fail = fail(announce); }
		} else if (move == Move.AUTOTOMIZE) {
			boolean nimble = false;
			if (announce && this.statStages[4] < 6) {
				this.weight -= 220;
				this.weight = Math.max(0.1, this.weight);
				nimble = true;
			}
			stat(this, 4, 2, foe, announce);
			if (announce && nimble) Task.addTask(Task.TEXT, this.nickname + " became more nimble!");
			
		} else if (move == Move.LOAD_FIREARMS) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " upgraded its weapon!");
			stat(this, 4, 1, foe, announce);
			stat(this, 5, 1, foe, announce);
			if (!this.hasStatus(Status.LOADED)) this.addStatus(Status.LOADED);
		} else if (move == Move.BABY$DOLL_EYES) {
			stat(foe, 0, -1, this, announce);
		} else if (announce && (move == Move.BATON_PASS || move == Move.TELEPORT)) {
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				Task.addTask(Task.TEXT, this.nickname + " went back to " + enemy.toString() + "!");
				this.addStatus(Status.SWITCHING);
			} else if (this.playerOwned() && player.hasValidMembers()) {
				Task.addTask(Task.TEXT, this.nickname + " went back to you!");
				this.addStatus(Status.SWITCHING);
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.BELLY_DRUM) {
			if (this.statStages[0] < 6 && this.currentHP >= this.getStat(0) / 2) {
				stat(this, 0, 12, foe, announce);
				this.damage((this.getStat(0) * 1.0 / 2), foe);
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
			if (announce) Task.addTask(Task.TEXT, this.nickname + " became charged with power!");
			stat(this, 3, 1, foe, announce);
			if (!this.hasStatus(Status.CHARGED)) this.addStatus(Status.CHARGED);
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
			if (announce && this.isType(PType.GHOST)) {
				if (!foe.hasStatus(Status.CURSED)) {
					foe.addStatus(Status.CURSED);
					Task.addTask(Task.TEXT, foe.nickname + " was afflicted with a curse!");
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
		} else if (announce && (move == Move.DETECT || move == Move.PROTECT || move == Move.LAVA_LAIR || move == Move.OBSTRUCT || move == Move.SPIKY_SHIELD || move == Move.AQUA_VEIL)) {
			if (Move.getNoComboMoves().contains(lastMoveUsed) && success) {
				fail = fail(announce);
			} else {
				if (announce) Task.addTask(Task.TEXT, this.nickname + " protected itself!");
				this.addStatus(Status.PROTECT);
				this.lastMoveUsed = move;
			}
		} else if (announce && move == Move.DARK_VOID) {
			foe.sleep(true, this);
		} else if (announce && move == Move.DECK_CHANGE) {
			if (!foe.hasStatus(Status.DECK_CHANGE)) {
				foe.addStatus(Status.DECK_CHANGE);
			} else {
				foe.removeStatus(Status.DECK_CHANGE);
			}
			if (announce) Task.addTask(Task.TEXT, this.nickname + " swapped " + foe.nickname + "'s attacking stats!");
		} else if (move == Move.DEFENSE_CURL) {
			stat(this, 1, 1, foe, announce);
			rollCount = 2;
		} else if (announce && move == Move.DEFOG) {
			stat(foe, 6, -1, this, announce);
			for (FieldEffect fe : field.getHazards(this.getFieldEffects())) {
				if (announce) Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				this.getFieldEffects().remove(fe);
			}
			for (FieldEffect fe : field.getHazards(foe.getFieldEffects())) {
				if (announce) Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				foe.getFieldEffects().remove(fe);
			}
			for (FieldEffect fe : field.getScreens(this.getFieldEffects())) {
				if (announce) Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				this.getFieldEffects().remove(fe);
			}
			for (FieldEffect fe : field.getScreens(foe.getFieldEffects())) {
				if (announce) Task.addTask(Task.TEXT, fe.toString() + " disappeared from " + this.nickname + "'s side!");
				foe.getFieldEffects().remove(fe);
			}
			if (field.terrain != null) {
				if (announce) {
					Task t = Task.addTask(Task.TERRAIN, "The " + field.terrain.toString() + " terrain disappeared!");
					t.setEffect(null);
				}
				field.terrain = null;
			}
		} else if (announce && move == Move.DESTINY_BOND) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success)) {
				foe.addStatus(Status.BONDED);
				if (announce) Task.addTask(Task.TEXT, this.nickname + " is ready to take its attacker down with it!");
				lastMoveUsed = move;
			} else { fail = fail(announce); }
			this.impressive = false;
			this.lastMoveUsed = move;
			
		} else if (move == Move.DISABLE) {
			if (foe.disabledMove == null && foe.lastMoveUsed != null && foe.lastMoveUsed.pp > 0) {
				foe.disabledMove = foe.lastMoveUsed;
				foe.disabledCount = 4;
				Task.addTask(Task.TEXT, foe.nickname + "'s " + foe.disabledMove + " was disabled!");
				if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its disable using its Mental Herb!");
					foe.disabledMove = null;
					foe.disabledCount = 0;
					foe.consumeItem(this);
				}
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.DOUBLE_TEAM) {
			stat(this, 6, 1, foe, announce);
		} else if (move == Move.DRAGON_DANCE) {
			stat(this, 0, 1, foe, announce);
			stat(this, 4, 1, foe, announce);
		} else if (announce && move == Move.ELECTRIC_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
			this.checkSeed(foe, Item.ELECTRIC_SEED, Effect.ELECTRIC);
		} else if (announce && move == Move.ENCORE) {
			if (!foe.hasStatus(Status.ENCORED) && foe.lastMoveUsed != null) {
				foe.addStatus(Status.ENCORED);
				foe.encoreCount = 4;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " must do an encore!");
				if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cleared its encore using its Mental Herb!");
					foe.removeStatus(Status.ENCORED);
					foe.encoreCount = 0;
					foe.consumeItem(this);
				}
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.ENDURE) {
			if (!(Move.getNoComboMoves().contains(lastMoveUsed) && success)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + " braced itself!");
				this.addStatus(Status.ENDURE);
				lastMoveUsed = move;
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.FLASH) {
			stat(foe, 5, -1, this, announce);
			stat(this, 2, 1, foe, announce);
		} else if (announce && move == Move.ENTRAINMENT) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = this.ability;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s ability became " + foe.ability.toString() + "!");
				foe.swapIn(this, false);
			} else {
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			}
		} else if (move == Move.FAKE_TEARS) {
			stat(foe, 3, -2, this, announce);
		} else if (move == Move.FEATHER_DANCE) {
			stat(foe, 0, -2, this, announce);
		} else if (move == Move.FIELD_FLIP) {
			ArrayList<FieldEffect> thisEffects = this.getFieldEffects();
			ArrayList<FieldEffect> foeEffects = foe.getFieldEffects();
			boolean announcing = announce && (thisEffects.size() > 0 || foeEffects.size() > 0);
			if (announcing) {
				foe.setFieldEffects(new ArrayList<>(thisEffects));
				this.setFieldEffects(new ArrayList<>(foeEffects));
				Task.addTask(Task.TEXT, this.nickname + "'s side swapped field effects with " + foe.nickname + "'s side!");
			} else {
				if (announce) fail = fail();
			}
		} else if (move == Move.FLATTER) {
			stat(foe, 2, 2, this, announce);
			foe.confuse(false, this);
		} else if (announce && move == Move.FLOODLIGHT) {
			fail = field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.FLOODLIGHT));
		} else if (announce && move == Move.FOCUS_ENERGY) {
			this.incrementStatus(Status.CRIT_CHANCE, 2);
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is tightening its focus!");
		} else if (announce && move == Move.FORESIGHT) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			if (announce) Task.addTask(Task.TEXT, this.nickname + " identified " + foe.nickname + "!");
			stat(this, 5, 1, foe, announce);
		} else if (announce && move == Move.FORESTS_CURSE) {
			foe.type1 = PType.GRASS;
			foe.type2 = null;
			if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s type was changed to Grass!");
		} else if (announce && move == Move.FROSTBIND) {
			foe.freeze(true, this);
		} else if (announce && move == Move.GASTRO_ACID) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = Ability.NULL;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s ability was suppressed!");
			} else {
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			}
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
			this.checkSeed(foe, Item.GRASSY_SEED, Effect.GRASSY);
		} else if (announce && move == Move.GRAVITY) {
			field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		} else if (move == Move.GROWL) {
			stat(foe, 0, -1, this, announce);
		} else if (move == Move.GROWTH) {
			if (field.equals(field.weather, Effect.SUN, this)) {
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
			if (announce) Task.addTask(Task.TEXT, "All stat changes were eliminated!");
		} else if (announce && move == Move.HEAL_PULSE) {
			if (foe.currentHP == foe.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s HP is full!");
			} else {
				if (foe.hasStatus(Status.HEAL_BLOCK)) {
					fail = fail(announce);
				} else {
					foe.heal(foe.getHPAmount(1.0/2), foe.nickname + " restored HP.");
				}
			}
		} else if (announce && move == Move.HEALING_CIRCLE) {
			if (!(field.contains(this.getFieldEffects(), Effect.HEALING_CIRCLE))) {
				FieldEffect a = field.new FieldEffect(Effect.HEALING_CIRCLE);
				this.getFieldEffects().add(a);
				if (announce) Task.addTask(Task.TEXT, "A healing circle was placed under " + this.nickname + "'s feet!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.HONE_CLAWS) {
			stat(this, 0, 1, foe, announce);
			stat(this, 5, 1, foe, announce);
		} else if (move == Move.HOWL) {
			stat(this, 0, 1, foe, announce);
		} else if (announce && move == Move.HYPNOSIS) {
			foe.sleep(true, this);
		} else if (announce && (move == Move.HEALING_WISH || move == Move.LUNAR_DANCE)) {
			this.damage(this.currentHP, foe, "");
			this.faint(true, foe);
			this.addStatus(Status.HEALING);
		} else if (announce && move == Move.INGRAIN) {
			if (!(this.hasStatus(Status.AQUA_RING))) {
			    this.addStatus(Status.AQUA_RING);
			    if (announce) Task.addTask(Task.TEXT, this.nickname + " planted its roots!");
			} else {
			    fail = fail(announce);
			}
			this.addStatus(Status.NO_SWITCH);
		} else if (move == Move.IRON_DEFENSE) {
			stat(this, 1, 2, foe, announce);
		} else if (announce && move == Move.LIFE_DEW) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				heal(getHPAmount(1.0/4), this.nickname + " restored HP.");
			}
		} else if (announce && move == Move.LEECH_SEED) {
			if (foe.isType(PType.GRASS)) {
				if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				return;
			}
			if (!foe.hasStatus(Status.LEECHED)) {
				foe.addStatus(Status.LEECHED);
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " was seeded!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.LEER) {
			stat(foe, 1, -1, this, announce);
		} else if (announce && move == Move.LIGHT_SCREEN) {
			if (!(field.contains(this.getFieldEffects(), Effect.LIGHT_SCREEN))) {
				FieldEffect a = field.new FieldEffect(Effect.LIGHT_SCREEN);
				if (this.getItem() == Item.LIGHT_CLAY) a.turns = 8;
				this.getFieldEffects().add(a);
				if (announce) Task.addTask(Task.TEXT, "Light Screen made " + this.nickname + "'s team stronger against\nSpecial moves!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.LOCK$ON) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " took aim at " + foe.nickname + "!");
			stat(this, 5, 6, foe, announce);
		} else if (announce && move == Move.LOVELY_KISS) {
			foe.sleep(true, this);
		} else if (announce && move == Move.LUCKY_CHANT) {
			if (!(field.contains(this.getFieldEffects(), Effect.LUCKY_CHANT))) {
				this.getFieldEffects().add(field.new FieldEffect(Effect.LUCKY_CHANT));
				if (announce) Task.addTask(Task.TEXT, "The Lucky Chant shielded the team from critical hits!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.MAGIC_POWDER) {
			foe.type1 = PType.MAGIC;
			foe.type2 = null;
			if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s type changed to MAGIC!");
		} else if (announce && move == Move.MAGNET_RISE) {
			if (this.magCount == 0) {
				this.magCount = 5;
				if (announce) Task.addTask(Task.TEXT, this.nickname + " floated with electromagnetism!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.MEAN_LOOK) {
			if (!foe.hasStatus(Status.TRAPPED)) {
				foe.addStatus(Status.TRAPPED);
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " can no longer escape!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.MEMENTO) {
			stat(foe, 0, -2, this, announce);
			stat(foe, 2, -2, this, announce);
			this.currentHP = 0;
			this.damage(this.currentHP, foe, "");
			this.faint(true, foe);
		} else if (move == Move.METAL_SOUND) {
			stat(foe, 3, -2, this, announce);
		} else if (move == Move.MINIMIZE) {
			stat(this, 6, 2, foe, announce);
		} else if (announce && (move == Move.MORNING_SUN || move == Move.MOONLIGHT || move == Move.SYNTHESIS)) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				if (field.equals(field.weather, Effect.SUN, this)) {
					heal(getHPAmount(1.0/1.5), this.nickname + " restored HP.");
				} else if (field.equals(field.weather, Effect.RAIN, this) || field.equals(field.weather, Effect.SANDSTORM, this)
						|| field.equals(field.weather, Effect.SNOW, this)) {
					heal(getHPAmount(1.0/4), this.nickname + " restored HP.");
				} else {
					heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
				}
			}
		} else if (announce && move == Move.MUD_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.MUD_SPORT));
			if (announce) Task.addTask(Task.TEXT, "Electric's power was weakened!");
		} else if (move == Move.NASTY_PLOT) {
			stat(this, 2, 2, foe, announce);
		} else if (move == Move.NOBLE_ROAR) {
			stat(foe, 0, -1, this, announce);
			stat(foe, 2, -1, this, announce);
		} else if (move == Move.NO_RETREAT) {
			if (!(this.hasStatus(Status.NO_SWITCH))) {
			    this.addStatus(Status.NO_SWITCH);
			    stat(this, 0, 1, foe, announce);
			    stat(this, 1, 1, foe, announce);
			    stat(this, 2, 1, foe, announce);
			    stat(this, 3, 1, foe, announce);
			    stat(this, 4, 1, foe, announce);
			    if (announce) Task.addTask(Task.TEXT, this.nickname + " can no longer switch out!");
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.NIGHTMARE) {
			if (foe.status == Status.ASLEEP && !foe.hasStatus(Status.NIGHTMARE)) {
				foe.addStatus(Status.NIGHTMARE);
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " had a nightmare!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.ODOR_SLEUTH) {
			if (foe.type1 == PType.GHOST) foe.type1 = PType.NORMAL;
			if (foe.type2 == PType.GHOST) foe.type2 = PType.NORMAL;
			if (announce) Task.addTask(Task.TEXT, this.nickname + " identified " + foe.nickname + "!");
			stat(foe, 6, -1, this, announce);
		} else if (announce && move == Move.PARTING_SHOT) {
			stat(foe, 0, -1, this, announce);
			stat(foe, 2, -1, this, announce);
			if (this.trainerOwned() && enemy.hasValidMembers()) {
				Task.addTask(Task.TEXT, this.nickname + " went back to " + enemy.toString() + "!");
				this.addStatus(Status.SWITCHING);
			} else if (this.playerOwned() && player.hasValidMembers()) {
				Task.addTask(Task.TEXT, this.nickname + " went back to you!");
				this.addStatus(Status.SWITCHING);
			}
		} else if (announce && move == Move.PERISH_SONG) {
			Task.addTask(Task.TEXT, "All Pokemon hearing the song will perish in 3 turns!");
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
			this.checkSeed(foe, Item.PSYCHIC_SEED, Effect.PSYCHIC);
		} else if (announce && move == Move.POISON_GAS) {
			foe.poison(true, this);
		} else if (announce && move == Move.POISON_POWDER) {
			if (foe.isType(PType.GRASS) || foe.getItem() == Item.SAFETY_GOGGLES) {
				if (foe.getItem() == Item.SAFETY_GOGGLES) {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + " due to its Safety Goggles!");
				} else {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				}
				success = false;
				fail = true;
				return;
			}
			foe.poison(true, this);
		} else if (announce && move == Move.SHORE_UP) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				if (field.equals(field.weather, Effect.SANDSTORM, this)) {
					heal(getHPAmount(1.0/1.5), this.nickname + " restored HP.");
				} else {
					heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
				}
			}
		} else if (announce && move == Move.SOAK) {
			foe.type1 = PType.WATER;
			foe.type2 = null;
			if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s type changed to WATER!");
		} else if (announce && move == Move.STUN_SPORE) {
			if (foe.isType(PType.GRASS) || foe.getItem() == Item.SAFETY_GOGGLES) {
				if (foe.getItem() == Item.SAFETY_GOGGLES) {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + " due to its Safety Goggles!");
				} else {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				}
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
			if (this.status != Status.HEALTHY || !this.vStatuses.isEmpty()) if (announce) Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " became healthy!", this);
			this.status = Status.HEALTHY;
			this.removeBad();
			stat(this, 4, 1, foe, announce);
		} else if (move == Move.AURORA_BOOST) {
			stat(this, 1, 1, foe, announce);
			stat(this, 2, 2, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (announce && move == Move.REFLECT) {
			if (!(field.contains(this.getFieldEffects(), Effect.REFLECT))) {
				FieldEffect a = field.new FieldEffect(Effect.REFLECT);
				if (this.getItem() == Item.LIGHT_CLAY) a.turns = 8;
				this.getFieldEffects().add(a);
				if (announce) Task.addTask(Task.TEXT, "Reflect made " + this.nickname + "'s team stronger against\nPhysical moves!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.REST) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else if (this.status == Status.ASLEEP) {
				fail = fail(announce);
				return;
			} else {
				if (this.ability == Ability.INSOMNIA) {
					fail = fail(announce);
					return;
				}
				int healAmt = this.getStat(0) - this.currentHP;
				this.status = Status.HEALTHY;
				this.sleep(false, foe, move);
				this.sleepCounter = 2;
				this.removeStatus(Status.CONFUSED);
				heal(healAmt, this.nickname + " slept and became healthy!");
			}
		} else if (announce && (move == Move.RECOVER || move == Move.SLACK_OFF)) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
			}
		} else if (announce && (move == Move.ROOST)) {
			if (this.currentHP == this.getStat(0)) {
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s HP is full!");
			} else {
				heal(getHPAmount(1.0/2), this.nickname + " restored HP.");
				if (this.type1 == PType.FLYING && announce) {
					this.type1 = PType.UNKNOWN;
					this.addStatus(Status.LANDED);
					Task.addTask(Task.TEXT, this.nickname + " landed!");
				}
				if (this.type2 == PType.FLYING && announce) {
					this.type2 = PType.UNKNOWN;
					this.addStatus(Status.LANDED);
					Task.addTask(Task.TEXT, this.nickname + " landed!");
				}
			}
		} else if (move == Move.SAND_ATTACK) {
			stat(foe, 5, -1, this, announce);
		} else if (announce && move == Move.SAFEGUARD) {
			if (!(field.contains(this.getFieldEffects(), Effect.SAFEGUARD))) {
				this.getFieldEffects().add(field.new FieldEffect(Effect.SAFEGUARD));
				if (announce) Task.addTask(Task.TEXT, "A veil was added to protect the team from statuses!");
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
				
				baseStats = getBaseStats();
				setStats();
				weight = getWeight();
				int nHP = this.getStat(0);
				Task.addTask(Task.SPRITE, "", this);
				heal(nHP - oHP, nickname + " transformed into " + getName(id) + "!");
				if (nickname.equals(name())) nickname = getName();
				name = getName();
				setTypes();
				setAbility();
				if (this.playerOwned()) player.pokedex[237] = 2;
				this.swapIn(foe, false);
			} else if (id == 237) {
				stat(this, 0, 1, foe, announce);
				stat(this, 2, 1, foe, announce);
				stat(this, 5, 1, foe, announce);
			} else {
				fail = fail(announce);
			}
			

		} else if (announce && move == Move.SIMPLE_BEAM) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = Ability.SIMPLE;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s ability became Simple!");
			} else {
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			}
		} else if (announce && move == Move.SLEEP_POWDER) {
			if (foe.isType(PType.GRASS) || foe.getItem() == Item.SAFETY_GOGGLES) {
				if (foe.getItem() == Item.SAFETY_GOGGLES) {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + " due to its Safety Goggles!");
				} else {
					if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + foe.nickname + "...");
				}
				success = false;
				fail = true;
				return;
			}
			foe.sleep(true, this);
		} else if (move == Move.SHIFT_GEAR) {
			stat(this, 0, 1, foe, announce);
			stat(this, 4, 2, foe, announce);
		} else if (announce && move == Move.SING) {
			foe.sleep(true, this);
		} else if (announce && move == Move.SKILL_SWAP) {
			if (foe.getItem() == Item.ABILITY_SHIELD) {
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			} else if (this.getItem() == Item.ABILITY_SHIELD) {
				Task.addTask(Task.TEXT, this.nickname + "'s Ability Shield blocked the ability change!");
			} else {
				Ability a = foe.ability;
				foe.ability = this.ability;
				this.ability = a;
				if (announce) Task.addTask(Task.TEXT, this.nickname + " swapped abilities with " + foe.nickname + "!");
				this.swapIn(foe, false);
				foe.swapIn(this, false);
			}
		} else if (move == Move.SMOKESCREEN) {
			stat(foe, 5, -1, this, announce);
		} else if (announce && move == Move.SPARKLING_TERRAIN) {
			boolean success = field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
			if (success && item == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
			this.checkSeed(foe, Item.SPARKLY_SEED, Effect.SPARKLY);
		} else if (move == Move.SPELLBIND) {
			if (!foe.hasStatus(Status.SPELLBIND) && foe.spunCount == 0 && !foe.isFainted()) {
				foe.addStatus(Status.SPELLBIND, this.getItem() == Item.BINDING_BAND ? -2 : -1);
				foe.spunCount = (((int) (Math.random() * 2)) + 4);
				if (this.getItem() == Item.GRIP_CLAW) foe.spunCount = 7;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " was spellbound by " + this.nickname + "!");
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.SPIKES) {
			fail = field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.SPIKES));
		} else if (move == Move.SPLASH) {
			if (announce) Task.addTask(Task.TEXT, "But nothing happened!");
		} else if (announce && move == Move.STEALTH_ROCK) {
			fail = field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS));
		} else if (announce && move == Move.STICKY_WEB) {
			fail = field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.STICKY_WEBS));
		} else if (move == Move.STOCKPILE) {
			stat(this, 1, 1, foe, announce);
			stat(this, 3, 1, foe, announce);
		} else if (announce && move == Move.STRENGTH_SAP) {
			int amount = (int) (foe.getStat(1) * foe.asModifier(0));
			if (this.getItem() == Item.BIG_ROOT) amount *= 1.3;
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
			if (!(foe.hasStatus(Status.TAUNTED))) {
			    foe.addStatus(Status.TAUNTED);
			    foe.tauntCount = 4;
			    if (announce) Task.addTask(Task.TEXT, foe.nickname + " was taunted!");
			    if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its taunt using its Mental Herb!");
					foe.removeStatus(Status.TAUNTED);
					foe.tauntCount = 0;
					foe.consumeItem(this);
				}
			} else {
			    fail = fail(announce);
			}
		} else if (announce && move == Move.TORMENT) {
			if (!(foe.hasStatus(Status.TORMENTED))) {
			    foe.addStatus(Status.TORMENTED);
			    foe.tormentCount = 4;
			    if (announce) Task.addTask(Task.TEXT, foe.nickname + " was tormented!");
			    if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its torment using its Mental Herb!");
					foe.removeStatus(Status.TORMENTED);
					foe.tormentCount = 0;
					foe.consumeItem(this);
				}
			} else {
			    fail = fail(announce);
			}
		} else if (move == Move.TAIL_GLOW) {
			stat(this, 2, 3, foe, announce);
		} else if (announce && move == Move.TAILWIND) {
			if (!(field.contains(this.getFieldEffects(), Effect.TAILWIND))) {
				this.getFieldEffects().add(field.new FieldEffect(Effect.TAILWIND));
				if (announce) Task.addTask(Task.TEXT, "A strong wind blew behind the team!");
			} else {
				fail = fail(announce);
			}
		} else if (move == Move.TAIL_WHIP) {
			stat(foe, 1, -1, this, announce);
		} else if (announce && move == Move.TEETER_DANCE) {
			foe.confuse(true, this);
		} else if (announce && move == Move.THUNDER_WAVE) {
			foe.paralyze(true, this);
		} else if (move == Move.TICKLE) {
			stat(foe, 0, -1, this, announce);
			stat(foe, 1, -1, this, announce);
		} else if (move == Move.TOPSY$TURVY) {
			for (int i = 0; i < 7; i++) {
				foe.statStages[i] *= -1;
			}
			if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s stat changes were flipped!");
		} else if (announce && move == Move.TOXIC) {
			foe.toxic(true, this);
		} else if (announce && move == Move.TOXIC_SPIKES) {
			fail = field.setHazard(foe.getFieldEffects(), field.new FieldEffect(Effect.TOXIC_SPIKES));
		} else if (move == Move.TRICK || move == Move.SWITCHEROO) {
			if (!(foe.ability == Ability.STICKY_HOLD && this.ability != Ability.MOLD_BREAKER)) {
				if (this.item != null || foe.item != null) {
					if (announce) Task.addTask(Task.TEXT, this.nickname + " switched items with its target!");
					Item userItem = this.item;
					Item foeItem = foe.item;
					if (userItem != null) if (announce) Task.addTask(Task.TEXT, foe.nickname + " obtained a " + userItem.toString() + "!");
					if (foeItem != null) if (announce) Task.addTask(Task.TEXT, this.nickname + " obtained a " + foeItem.toString() + "!");
					this.item = foeItem;
					foe.item = userItem;
					if (this.item != Item.METRONOME) this.metronome = 0;
					if (foe.item != Item.METRONOME) foe.metronome = 0;
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
					if (foe.item == null && foeItem != null) {
						foe.consumeItem(this);
					}
					if (this.item == null && userItem != null) {
						this.consumeItem(foe);
					}
				} else {
					fail = fail(announce);
				}
			}
		} else if (announce && move == Move.TRICK_ROOM) {
			field.setEffect(field.new FieldEffect(Effect.TRICK_ROOM));
			this.checkSeed(foe, Item.ROOM_SERVICE, Effect.TRICK_ROOM);
		} else if (announce && move == Move.MAGIC_ROOM) {
			field.setEffect(field.new FieldEffect(Effect.MAGIC_ROOM));
		} else if (move == Move.VENOM_DRENCH) {
			if (foe.status == Status.POISONED || foe.status == Status.TOXIC) {
				stat(foe, 0, -2, this, announce);
				stat(foe, 2, -2, this, announce);
			} else {
				fail = fail(announce);
			}
		} else if (announce && move == Move.VANDALIZE) {
			if (foe.getItem() == Item.ABILITY_SHIELD) {
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			} else if (this.getItem() == Item.ABILITY_SHIELD) {
				Task.addTask(Task.TEXT, this.nickname + "'s Ability Shield blocked the ability change!");
				foe.ability = Ability.NULL;
			} else {
				this.ability = foe.ability;
				if (announce) Task.addTask(Task.TEXT, this.nickname + "'s ability became " + this.ability.toString() + "!");
				this.swapIn(this, false);
				foe.ability = Ability.NULL;
			}
		} else if (announce && move == Move.WATER_SPORT) {
			field.setEffect(field.new FieldEffect(Effect.WATER_SPORT));
			if (announce) Task.addTask(Task.TEXT, "Fire's power was weakened!");
		} else if (announce && (move == Move.WHIRLWIND || move == Move.ROAR)) {
			boolean result = false;
			if (foe.trainerOwned() && enemy != null) {
				result = enemy.swapRandom(foe);
			} else if (foe.playerOwned()) {
				result = player.swapRandom(foe);
			}
			if (!result) fail = fail(announce);
		} else if (announce && move == Move.WILL$O$WISP) {
			foe.burn(true, this);
		} else if (announce && move == Move.WISH) {
			if (field.contains(this.getFieldEffects(),Effect.WISH)) {
				fail(announce);
			} else {
				FieldEffect effect = field.new FieldEffect(Effect.WISH);
				effect.stat = this.getStat(0) / 2;
				this.getFieldEffects().add(effect);
				if (announce) Task.addTask(Task.TEXT, this.nickname + " made a wish!");
			}
		} else if (move == Move.WITHDRAW) {
			stat(this, 1, 1, foe, announce);
		} else if (announce && move == Move.WORRY_SEED) {
			if (foe.getItem() != Item.ABILITY_SHIELD) {
				foe.ability = Ability.INSOMNIA;
				if (announce) Task.addTask(Task.TEXT, foe.nickname + "'s ability became Insomnia!");
			} else {
				Task.addTask(Task.TEXT, foe.nickname + "'s Ability Shield blocked the ability change!");
			}
		} else if (announce && move == Move.YAWN) {
			if (foe.hasStatus(Status.YAWNING) || foe.hasStatus(Status.DROWSY) || foe.status == Status.ASLEEP) {
				fail(announce);
			} else {
				foe.addStatus(Status.YAWNING);
				if (announce) Task.addTask(Task.TEXT, foe.nickname + " became drowsy!");
			}
		} else if (move == Move.ROCK_POLISH) {
			stat(this, 4, 2, foe, announce);
		}
		success = !fail;
		return;
	}

	private void checkSeed(Pokemon f, Item item, Effect effect) {
		if (effect.isTerrain) {
			if (!field.equals(field.terrain, effect)) return;
		} else {
			if (!field.contains(fieldEffects, effect)) return;
		}
		
		if (this.item != item && f.item != item) return;
		
		int statRaw = item.getStat();
		if (statRaw > this.statStages.length) return;
		
		Pokemon faster = this.getFaster(f, 0, 0);
		Pokemon slower = faster == this ? f : this;
		
		int stat = Math.abs(statRaw);
		int amt = Integer.signum(statRaw);
		
		if (faster.getItem() == item) {
			Task.addTask(Task.TEXT, faster.nickname + " used its " + item.toString() + "!");
			stat(faster, stat, amt, slower);
			faster.consumeItem(slower);
		}
		if (slower.getItem() == item) {
			Task.addTask(Task.TEXT, slower.nickname + " used its " + item.toString() + "!");
			stat(slower, stat, amt, faster);
			slower.consumeItem(faster);
		}
	}

	private boolean hasMaxedStatStages(boolean checkAccEv) {
		for (int i = 0; i < (checkAccEv ? 5 : this.statStages.length); i++) {
			if (this.statStages[i] < 6) return false;
		}
		return true;
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
				if (announce) Task.addAbilityTask(p);
				stat(this, i, amt, foe, announce);
				return;
			} else if (p.ability == Ability.CLEAR_BODY && a < 0) {
				if (announce) Task.addAbilityTask(p);
				if (announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + " was not lowered!");
				return;
			} else if (p.getItem() == Item.CLEAR_AMULET && a < 0) {
				if (announce) Task.addTask(Task.TEXT, p.nickname + "'s Clear Amulet blocked the stat drop!");
				return;
			}
		}
		if (p.ability == Ability.KEEN_EYE && a < 0 && i == 5) {
			if (announce) Task.addAbilityTask(p);
			if (announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + " was not lowered!");
			return;
		} else if (p.ability == Ability.HYPER_CUTTER && a < 0 && i == 0) {
			if (announce) Task.addAbilityTask(p);
			if (announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + " was not lowered!");
			return;
		}
		boolean statBerry = false;
		int difference = 0;
		if (announce) {
			statBerry = p.checkStatBerry(i, a);
			difference = statBerry ? Math.max(-6 - p.statStages[i], a) : 0;
		}
		
		if (p.statStages[i] >= 6 && a > 0) {
			if (announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + " won't go any higher!");
		} else if (p.statStages[i] <= -6 && a < 0) {
			if (a != 12 && announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + " won't go any lower!");
		} else {
			if (announce) Task.addTask(Task.TEXT, p.nickname + "'s " + type + amount + "!");
		}
		if (foe.ability == Ability.EMPATHIC_LINK && a > 0) {
			if (announce) Task.addAbilityTask(foe);
			foe.stat(foe, 2, 1, this, announce);
		}
		p.statStages[i] += a;
		p.statStages[i] = p.statStages[i] < -6 ? -6 : p.statStages[i];
		p.statStages[i] = p.statStages[i] > 6 ? 6 : p.statStages[i];
		if (statBerry) {
			Task.addTask(Task.TEXT, p.nickname + " ate its " + p.item + " to restore its " + type + "!");
			p.stat(p, i, Math.abs(difference), foe, true);
			p.consumeItem(foe);
		}
		
		if (this != p) {
			if (p.ability == Ability.DEFIANT && foe.ability != Ability.BRAINWASH && a < 0) {
				if (announce) Task.addAbilityTask(p);
				stat(p, 0, 2, foe, announce);
			} else if (p.ability == Ability.COMPETITIVE && foe.ability != Ability.BRAINWASH && a < 0) {
				if (announce) Task.addAbilityTask(p);
				stat(p, 2, 2, foe, announce);
			}
		}
	}

	private boolean checkStatBerry(int stat, int amt) {
		if (amt >= 0) return false;
		if (this.getItem() == null) return false;
		if (!this.item.isStatBerry()) return false;
		if (this.statStages[stat] <= -6) return false;
		
		if (this.getItem() == Item.SPELON_BERRY && stat == 0) return true;
		if (this.getItem() == Item.BELUE_BERRY && stat == 1) return true;
		if (this.getItem() == Item.PAMTRE_BERRY && stat == 2) return true;
		if (this.getItem() == Item.DURIN_BERRY && stat == 3) return true;
		if (this.getItem() == Item.WATMEL_BERRY && stat == 4) return true;
		if (this.getItem() == Item.WEPEAR_BERRY && stat == 5) return true;
		if (this.getItem() == Item.BLUK_BERRY && stat == 6) return true;
		return false;
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
	
	public double asAccModifier(int accEv) {
		double numerator = 3.0;
        double denominator = 3.0;

        if (accEv < 0) {
            denominator -= accEv;
        } else if (accEv > 0) {
            numerator += accEv;
        }
        
        return numerator / denominator;
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
		if (m < 0) return false;
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
	
	public Item getItem() {
		if (field == null) return this.item;
		if (field.contains(field.fieldEffects, Effect.MAGIC_ROOM)) return null;
		return this.item;
	}
	
	public void heal() {
		this.fainted = false;
		this.clearVolatile();
		this.vStatuses.clear();
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
	            resistantTypes.add(PType.DARK);
	            resistantTypes.add(PType.MAGIC);
				break;
			case POISON:
				resistantTypes.add(PType.POISON);
	            resistantTypes.add(PType.GROUND);
	            resistantTypes.add(PType.ROCK);
	            resistantTypes.add(PType.GHOST);
	            resistantTypes.add(PType.PSYCHIC);
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
	            resistantTypes.add(PType.POISON);
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
				weakTypes.add(PType.GALACTIC);
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

	public void faint(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		this.currentHP = 0;
		this.fainted = true;
		this.battled = false;
		awardHappiness(-3, false);
		this.status = Status.HEALTHY;
		this.removeStatus(Status.LOCKED);
		this.removeStatus(Status.SWITCHING);
		this.removeStatus(Status.SPUN);
		this.removeStatus(Status.SPELLBIND);
		this.removeStatus(Status.RECHARGE);
		this.removeStatus(Status.CHARGING);
		this.removeStatus(Status.NO_SWITCH);
		this.removeStatus(Status.TRAPPED);
		this.removeStatus(Status.SEMI_INV);
		foe.removeStatus(Status.SPUN);
		foe.removeStatus(Status.SPELLBIND);
		foe.removeStatus(Status.TRAPPED);
		if (this.trainer != null && this.playerOwned()) {
			Player player = (Player) this.trainer;
			player.setBattled(player.getBattled() - 1);
		}
		if (announce) Task.addTask(Task.FAINT, this.nickname + " fainted!", this);
		foe.awardxp(getxpReward());
		field.knockouts++;
	}

	public void clearVolatile() {
		confusionCounter = 0;
		magCount = 0;
		toxic = 0;
		perishCount = 0;
		rollCount = 1;
		metronome = 0;
		encoreCount = 0;
		disabledMove = null;
		disabledCount = 0;
		spunCount = 0;
		outCount = 0;
		tauntCount = 0;
		tormentCount = 0;
		healBlockCount = 0;
		consumedItem = false;
		this.lastMoveUsed = null;
		this.tricked = null;
		this.moveMultiplier = 1;
		this.clearStatuses();
		statStages = new int[7];
		this.impressive = true;
		setTypes();
		setAbility();
		this.weight = this.getWeight();
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
	
	public int calcWithTypes(Pokemon foe, Move move, boolean first, Field field) {
		return calcWithTypes(foe, move, first, 0, false, field);
	}
	
	public int calcWithTypes(Pokemon foe, Move move, boolean first, int mode, boolean crit, Field field) {
		double attackStat;
		double defenseStat;
		int damage = 0;
		double bp = move.basePower;
		int acc = move.accuracy;
		int secChance = move.secondary;
		PType moveType = move.mtype;
		int critChance = move.critChance;
		Ability foeAbility = foe.ability;
		boolean contact = move.contact;
		
		int magicDamage = 0;
		
		if (id == 237) {
			move = get150Move(move);
			bp = move.basePower;
			acc = move.accuracy;
			moveType = move.mtype;
			critChance = move.critChance;
			contact = move.contact;
		}
		
		if (mode == 0 && (move == Move.MAGIC_BLAST || move == Move.ELEMENTAL_SPARKLE)) {
			if (move == Move.MAGIC_BLAST) {
				ArrayList<Move> moves = new ArrayList<>();
				for (Move cand : Move.getAllMoves()) {
					if (cand.mtype == PType.ROCK || cand.mtype == PType.GRASS || cand.mtype == PType.GROUND) {
						moves.add(cand);
					}
				}
				Move[] validMoves = moves.toArray(new Move[moves.size()]);
				Move pickMove = validMoves[new Random().nextInt(validMoves.length)];
				magicDamage = calcWithTypes(foe, pickMove, first, mode, crit, field);
			}
			if (move == Move.ELEMENTAL_SPARKLE) {
				ArrayList<Move> moves = new ArrayList<>();
				for (Move cand : Move.getAllMoves()) {
					if (cand.mtype == PType.FIRE || cand.mtype == PType.WATER || cand.mtype == PType.GRASS) {
						moves.add(cand);
					}
				}
				Move[] validMoves = moves.toArray(new Move[moves.size()]);
				Move pickMove = validMoves[new Random().nextInt(validMoves.length)];
				magicDamage = calcWithTypes(foe, pickMove, first, mode, crit, field);
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
		
		if (this.hasStatus(Status.MUTE) && Move.getSound().contains(move)) {
			return 0;
		}
		
		if (move == Move.SKULL_BASH || move == Move.SKY_ATTACK || ((move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE) && !field.equals(field.weather, Effect.SUN, this))
				|| move == Move.BLACK_HOLE_ECLIPSE || move == Move.GEOMANCY || move == Move.METEOR_BEAM) {
			if (this.getItem() != Item.POWER_HERB) bp *= 0.5;
		}
		
		if (this.ability == Ability.COMPOUND_EYES) acc *= 1.3;
		if (item == Item.WIDE_LENS) acc *= 1.1;
		if (item == Item.ZOOM_LENS && !first) acc *= 1.2;
		if (field.contains(field.fieldEffects, Effect.GRAVITY)) acc = acc * 5 / 3;
		
		if (field.equals(field.weather, Effect.SUN, foe)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 50;
		}
		
		if (field.equals(field.weather, Effect.RAIN, foe)) {
			if (move == Move.THUNDER || move == Move.HURRICANE) acc = 1000;
		}
		
		if (field.equals(field.weather, Effect.SNOW, foe)) {
			if (move == Move.BLIZZARD) acc = 1000;
		}
		
		if (move == Move.FROSTBIND && this.isType(PType.ICE)) acc = 1000;
		if (move == Move.WILL$O$WISP && this.isType(PType.FIRE)) acc = 1000;
		if (move == Move.THUNDER_WAVE && this.isType(PType.ELECTRIC)) acc = 1000;
		if (move == Move.TOXIC && this.isType(PType.POISON)) acc = 1000;
		
		if (foe.ability == Ability.WONDER_SKIN && move.cat == 2 && acc <= 100) acc = 50;
		
		if (foe.getItem() != Item.ABILITY_SHIELD &&
				(move == Move.FUSION_BOLT || move == Move.FUSION_FLARE || this.ability == Ability.MOLD_BREAKER)) {
			foeAbility = Ability.NULL;
		}
		
		if (mode == 0 && this.ability != Ability.NO_GUARD && foeAbility != Ability.NO_GUARD && acc < 100) {
			int accEv = this.statStages[5] - foe.statStages[6];
			if (move == Move.DARKEST_LARIAT || move == Move.SACRED_SWORD) accEv += foe.statStages[6];
			accEv = accEv > 6 ? 6 : accEv;
			accEv = accEv < -6 ? -6 : accEv;
			double accuracy = acc * asAccModifier(accEv);
			if ((field.equals(field.weather, Effect.SANDSTORM, this) && foeAbility == Ability.SAND_VEIL) ||
					(field.equals(field.weather, Effect.SNOW, this) && foeAbility == Ability.SNOW_CLOAK)) accuracy *= 0.8;
			if (!hit(accuracy)) {
				return 1;
			}
		}
		
		if (move == Move.HIDDEN_POWER) moveType = determineHPType();
		if (move == Move.RETURN) moveType = determineHPType();
		if (move == Move.WEATHER_BALL) moveType = determineWBType();
		if (move == Move.TERRAIN_PULSE) moveType = determineTPType();
		
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
		
		if (this.ability == Ability.REFRIGERATE && moveType == PType.NORMAL) {
			moveType = PType.ICE;
			bp *= 1.2;
		}
		
		if (foe.hasStatus(Status.MAGIC_REFLECT) && (move != Move.BRICK_BREAK && move != Move.MAGIC_FANG && move != Move.PSYCHIC_FANGS)) {
			damage = this.calcWithTypes(this, move, false, mode, crit, field);
			if (mode == 0) {
				return 1 - damage;
			} else {
				return damage;
			}
		}
		if (this.hasStatus(Status.POSSESSED) && this != foe) {
			damage = this.calcWithTypes(this, move, false, mode, crit, field);
			if (mode == 0) {
				return 1 - damage;
			} else {
				return damage;
			}
		}
		
		if (moveType == PType.FIRE && foeAbility == Ability.FLASH_FIRE) {
			return 0;
		}
		
		if ((moveType == PType.WATER && (foeAbility == Ability.WATER_ABSORB || foeAbility == Ability.DRY_SKIN)) || (moveType == PType.ELECTRIC && foeAbility == Ability.VOLT_ABSORB) ||
				(moveType == PType.BUG && foeAbility == Ability.INSECT_FEEDER) || ((moveType == PType.LIGHT || moveType == PType.GALACTIC) && foeAbility == Ability.BLACK_HOLE)) {
			return 0;
		}
		
		if ((moveType == PType.ELECTRIC && (foeAbility == Ability.MOTOR_DRIVE || foeAbility == Ability.LIGHTNING_ROD)) ||
				(moveType == PType.GRASS && foeAbility == Ability.SAP_SIPPER) || (moveType == PType.FIRE && foeAbility == Ability.HEAT_COMPACTION)) {
			return 0;
		}
		
		if (moveType == PType.GROUND && !foe.isGrounded(field) && move.cat != 2) {
			return 0;
		}
		
		if (moveType != PType.GROUND && (move.cat != 2 || move == Move.THUNDER_WAVE)) {
			if (getImmune(foe, moveType) || (moveType == PType.GHOST && foeAbility == Ability.FRIENDLY_GHOST)) {
				if (foe.getItem() == Item.RING_TARGET) {
					// Nothing
				} else if (this.ability == Ability.SCRAPPY && (moveType == PType.NORMAL || moveType == PType.FIGHTING)) {
					// Nothing: scrappy allows normal and fighting type moves to hit ghosts
				} else if (this.ability == Ability.CORROSION && moveType == PType.POISON) {
					// Nothing: corrosion allows poison moves to hit steel
				} else {
					return 0;
				}
			}
		}
		
		if (field.equals(field.terrain, Effect.PSYCHIC) && foe.isGrounded(field) && move.hasPriority(this) && move != Move.GRAVITY_PUNCH) {
			return 0;
		}
		
		
		if (mode == 0 && move == Move.DREAM_EATER && foe.status != Status.ASLEEP) {
			return -1;
		}
		
		if (move == Move.POLTERGEIST && foe.item == null) {
			return 0;
		}
		
		if (move.cat == 2) {
			return 0;
		}
		
		if ((moveType == PType.WATER && (foe.getItem() == Item.ABSORB_BULB || foe.getItem() == Item.LUMINOUS_MOSS))
				|| (moveType == PType.ELECTRIC && foe.getItem() == Item.CELL_BATTERY)
				|| (moveType == PType.ICE && foe.getItem() == Item.SNOWBALL)) {
			return 0;
		}
		
		if (move.basePower < 0) {
			bp = determineBasePower(foe, move, first, null, false);
		}
		
		if (this.getItem() == Item.METRONOME) bp *= (1 + (this.metronome * 0.2));
		
		if (this.getItem() == Item.PROTECTIVE_PADS) contact = false;
		if (this.getItem() == Item.PUNCHING_GLOVE && move.isPunching()) {
			bp *= 1.1;
			contact = false;
		}
		
		if (moveType == PType.FIRE && this.hasStatus(Status.FLASH_FIRE)) bp *= 1.5;
		
		if (this.ability == Ability.SHEER_FORCE && move.cat != 2 && secChance > 0) {
			bp *= 1.3;
		}
		
		if (move == Move.FUSION_BOLT && this.lastMoveUsed == Move.FUSION_FLARE) {
			bp *= 2;
		}
		
		if (move == Move.FUSION_FLARE && this.lastMoveUsed == Move.FUSION_BOLT) {
			bp *= 2;
		}
		
		if (this.ability == Ability.TECHNICIAN && bp <= 60) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.TOUGH_CLAWS && contact) {
			bp *= 1.3;
		}
		
		if (foeAbility == Ability.DRY_SKIN && moveType == PType.FIRE) {
			bp *= 1.25;
		}
		
		if (this.ability == Ability.SHARPNESS && move.isSlicing()) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.SHARP_TAIL && move.isTail()) {
			bp *= 1.5;
		}
		
		if (this.ability == Ability.IRON_FIST && move.isPunching()) {
			bp *= 1.3;
		}
		
		if (this.ability == Ability.STRONG_JAW && move.isBiting()) {
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
		
		if (mode == 0 && move == Move.CHROMO_BEAM && checkSecondary(this.ability == Ability.SERENE_GRACE || field.equals(field.terrain, Effect.SPARKLY) ? 60 : 30)) bp *= 2;
		
		int arcane = this.getStatusNum(Status.ARCANE_SPELL);
		if (arcane != 0) {
			bp = Math.max(bp - arcane, 20);
		}
		
		if (field.equals(field.weather, Effect.SUN, foe)) {
			if (moveType == PType.WATER) bp *= 0.5;
			if (moveType == PType.FIRE) bp *= 1.5;
			if (move == Move.SOLSTICE_BLADE) bp *= 1.5;
		}
		
		if (field.equals(field.weather, Effect.RAIN, foe)) {
			if (moveType == PType.WATER) bp *= 1.5;
			if (moveType == PType.FIRE) bp *= 0.5;
		}
		
		if (field.equals(field.terrain, Effect.ELECTRIC) && isGrounded(field)) {
			if (moveType == PType.ELECTRIC) bp *= 1.5;
		}
		
		if (field.equals(field.terrain, Effect.GRASSY)) {
			if (isGrounded(field) && moveType == PType.GRASS) bp *= 1.5;
			if (move == Move.EARTHQUAKE || move == Move.BULLDOZE || move == Move.MAGNITUDE) bp *= 0.5;
		}
		
		if (field.equals(field.terrain, Effect.SPARKLY) && isGrounded(field)) {
			if (moveType == PType.MAGIC) bp *= 1.5;
		}
		
		if (field.equals(field.terrain, Effect.PSYCHIC) && isGrounded(field)) {
			if (moveType == PType.PSYCHIC) bp *= 1.5;
		}
		
		if ((field.contains(field.fieldEffects, Effect.MUD_SPORT) && moveType == PType.ELECTRIC) || (field.contains(field.fieldEffects, Effect.WATER_SPORT) && moveType == PType.FIRE)) bp *= 0.33;
		
		if (field.equals(field.weather, Effect.SANDSTORM, foe) && this.ability == Ability.SAND_FORCE) bp *= 1.3;
		
		if (field.equals(field.weather, Effect.RAIN, foe) || field.equals(field.weather, Effect.SNOW, foe) || field.equals(field.weather, Effect.SANDSTORM, foe)) {
			if (move == Move.SOLAR_BEAM || move == Move.SOLAR_BLADE || move == Move.SOLSTICE_BLADE) bp *= 0.5;
		}
		
		// Use either physical or special attack/defense
		if (move.isPhysical()) {
			attackStat = this.getStat(1);
			defenseStat = foe.getStat(2);
			if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(0);
			if (move != Move.DARKEST_LARIAT && move != Move.SACRED_SWORD && this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(1);
			if (move == Move.BODY_PRESS) attackStat = this.getStat(2) * this.asModifier(1);
			if (move == Move.FOUL_PLAY) attackStat = foe.getStat(1) * foe.asModifier(0);
			if (this.getItem() == Item.CHOICE_BAND) attackStat *= 1.5;
			if (this.status == Status.BURNED && this.ability != Ability.GUTS && move != Move.FACADE) attackStat /= 2;
			if (this.ability == Ability.GUTS && this.status != Status.HEALTHY) attackStat *= 1.5;
			if (this.ability == Ability.HUGE_POWER) attackStat *= 2;
			if (field.equals(field.weather, Effect.SNOW, this) && foe.isType(PType.ICE)) defenseStat *= 1.5;
			if (field.contains(foe.getFieldEffects(), Effect.REFLECT) || field.contains(foe.getFieldEffects(), Effect.AURORA_VEIL)) defenseStat *= 2;
		} else {
			attackStat = this.getStat(3);
			defenseStat = foe.getStat(4);
			if (foeAbility != Ability.UNAWARE) attackStat *= this.asModifier(2);
			if (this.ability != Ability.UNAWARE) defenseStat *= foe.asModifier(3);
			if (mode != 0 && foe.getItem() == Item.ASSAULT_VEST) defenseStat *= 1.5;
			if (move == Move.PSYSHOCK || move == Move.MAGIC_MISSILES) defenseStat = foe.getStat(2) * foe.asModifier(1);
			if (this.getItem() == Item.CHOICE_SPECS) attackStat *= 1.5;
			if (this.status == Status.FROSTBITE) attackStat /= 2;
			if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN, this)) attackStat *= 1.5;
			if (field.equals(field.weather, Effect.SANDSTORM, this) && foe.isType(PType.ROCK)) defenseStat *= 1.5;
			if (field.contains(foe.getFieldEffects(), Effect.LIGHT_SCREEN) || field.contains(foe.getFieldEffects(), Effect.AURORA_VEIL)) defenseStat *= 2;
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
		if (moveType == PType.ELECTRIC && this.hasStatus(Status.CHARGED)) bp *= 2;
		
		// Load Firearms
		if (moveType == PType.STEEL && this.hasStatus(Status.LOADED)) bp *= 2;
		
		// Multi hit moves calc to use
		if (mode == 0) bp *= move.getNumHits(this, this.trainer.team);
		
		// Crit Check
		critChance += this.getStatusNum(Status.CRIT_CHANCE);
		if (this.ability == Ability.SUPER_LUCK) critChance++;
		if (foe.ability != Ability.BATTLE_ARMOR && foe.ability != Ability.SHELL_ARMOR && !field.contains(foe.getFieldEffects(), Effect.LUCKY_CHANT) &&
				((mode == 0 && critChance >= 1 && critCheck(critChance)) || (mode != 0 && (critChance >= 3 || (crit && critChance >= 0))))) {
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
				if (field.equals(field.weather, Effect.SNOW, this) && foe.isType(PType.ICE)) defenseStat *= 1.5;
				
			}
			if (!move.isPhysical() && defenseStat > foe.getStat(4)) {
				defenseStat = foe.getStat(4);
				if (field.equals(field.weather, Effect.SANDSTORM, this) && foe.isType(PType.ROCK)) defenseStat *= 1.5;
			}
			if (mode != 0 && foe.getItem() == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
			damage = calc(attackStat, defenseStat, bp, this.level, mode);
			damage *= 1.5;
			if (this.ability == Ability.SNIPER) damage *= 1.5;
		} else {
			if (mode != 0 && foe.getItem() == Item.EVIOLITE && foe.canEvolve()) defenseStat *= 1.5;
			damage = calc(attackStat, defenseStat, bp, this.level, mode);
		}
		
		if ((foeAbility == Ability.ICY_SCALES && !move.isPhysical()) || (foeAbility == Ability.MULTISCALE && foe.currentHP == foe.getStat(0))) damage /= 2;
		
		double multiplier = 1;
		// Check type effectiveness
		PType[] resist = getResistances(moveType);
		if (move == Move.FREEZE$DRY || move == Move.SKY_UPPERCUT) {
			ArrayList<PType> types = new ArrayList<>();
			for (PType type : resist) {
				types.add(type);
			}
			if (move == Move.FREEZE$DRY) types.remove(PType.WATER);
			if (move == Move.SKY_UPPERCUT) types.remove(PType.FLYING);
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
		if (move == Move.FREEZE$DRY || move == Move.SKY_UPPERCUT) {
			PType[] temp = new PType[weak.length + 1];
			for (int i = 0; i < weak.length; i++) {
				temp[i] = weak[i];
			}
			if (move == Move.FREEZE$DRY) temp[weak.length] = PType.WATER;
			if (move == Move.SKY_UPPERCUT) temp[weak.length] = PType.FLYING;
			weak = temp;
		}
		for (PType type : weak) {
			if (foe.type1 == type) multiplier *= 2;
			if (foe.type2 == type) multiplier *= 2;
		}
		
		if (foeAbility == Ability.MOSAIC_WINGS && moveType != PType.UNKNOWN && multiplier == 1) {
			multiplier = 0.5;
		}
		
		if (foeAbility == Ability.WONDER_GUARD && multiplier <= 1 && moveType != PType.UNKNOWN) {
			return 0;
		}
		
		if (foeAbility == Ability.FLUFFY && moveType == PType.FIRE) multiplier *= 2;
		
		damage *= multiplier;
		
		if (foeAbility == Ability.UNERODIBLE && (moveType == PType.GRASS || moveType == PType.WATER || moveType == PType.GROUND)) damage /= 4;
		if (foeAbility == Ability.THICK_FAT && (moveType == PType.FIRE || moveType == PType.ICE)) damage /= 2;
		if (foeAbility == Ability.UNWAVERING && (moveType == PType.DARK || moveType == PType.GHOST)) damage /= 2;
		if (foeAbility == Ability.JUSTIFIED && moveType == PType.DARK) damage /= 2;
		if (foeAbility == Ability.FLUFFY && move.contact) damage /= 2;
		
		if (foeAbility == Ability.PSYCHIC_AURA && move.cat == 1) damage *= 0.8;
		if (foeAbility == Ability.GLACIER_AURA && move.cat == 0) damage *= 0.8;
		if (foeAbility == Ability.GALACTIC_AURA && (moveType == PType.PSYCHIC || moveType == PType.ICE)) damage /= 2;
		
		if (multiplier > 1) {
			if (foeAbility == Ability.SOLID_ROCK || foeAbility == Ability.FILTER) damage /= 2;
			if (item == Item.EXPERT_BELT) damage *= 1.2;
			if (mode != 0 && foe.getItem() != null && foe.checkTypeResistBerry(moveType)) damage /= 2;
		}
		
		if (multiplier < 1) {
			if (ability == Ability.TINTED_LENS) damage *= 2;
		}
		
		if (mode == 0 && this.getItem() == Item.THROAT_SPRAY && Move.getSound().contains(move)) damage *= 1.5;
		
		if (this.getItem() == Item.LIFE_ORB) damage *= 1.3;
		if (this.ability == Ability.ILLUSION && this.illusion) damage *= 1.2;
		if (this.ability == Ability.ANALYTIC && !first) damage *= 1.3;
		if (move == Move.NIGHT_SHADE || move == Move.SEISMIC_TOSS || move == Move.PSYWAVE) damage = this.level;
		if (move == Move.ENDEAVOR) {
			if (foe.currentHP > this.currentHP) {
				damage = foe.currentHP - this.currentHP;
			} else { return 0; } }
		if (move == Move.SUPER_FANG) damage = foe.currentHP / 2;
		if (move == Move.DRAGON_RAGE) damage = 40;
		if (mode == 0 && (move == Move.HORN_DRILL || move == Move.SHEER_COLD || move == Move.GUILLOTINE || move == Move.FISSURE)) {
			if ((move == Move.SHEER_COLD && foe.isType(PType.ICE)) || foeAbility == Ability.STURDY || foe.level > this.level) {
				return 0;
			}
			damage = foe.currentHP;
		}
		
		damage = Math.max(damage, 1);
		

		if (mode == 0 && damage >= foe.currentHP) damage = foe.currentHP; // Check for kill
		
		if (mode == 0 && (move == Move.MAGIC_BLAST || move == Move.ELEMENTAL_SPARKLE)) damage += magicDamage;
		
		if ((move == Move.SELF$DESTRUCT || move == Move.EXPLOSION || move == Move.SUPERNOVA_EXPLOSION) && mode == 0) {
			Random rand = new Random();
		    double hpPercent = this.currentHP * 1.0 / this.getStat(0);
		    
		    if (rand.nextDouble() < (hpPercent - 0.1)) {
		        return 0;
		    }
		}
		if (move == Move.FUTURE_SIGHT) {
			if (mode == 0) {
				if (field.contains(foe.getFieldEffects(), Effect.FUTURE_SIGHT)) {
					return -1;
				} else {
					return new Random().nextInt(4) != 1 ? 0 : damage;
				}
			}
			attackStat = this.getStat(3);
			if (this.getItem() == Item.CHOICE_SPECS) attackStat *= 1.5;
			if (this.status == Status.FROSTBITE) attackStat /= 2;
			if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN, this)) attackStat *= 1.5;
			if (this.isType(moveType)) attackStat *= 1.5;
			return foe.takeFutureSight((int) attackStat, this.level, mode, this);
		}
		return damage;
	}

	public void endOfTurn(Pokemon f) {
		if (this.isFainted()) return;
		
		int[] userStages = this.statStages.clone();
		
		if (this.ability == Ability.SHED_SKIN && this.status != Status.HEALTHY) {
			int r = (int)(Math.random() * 3);
			if (r == 0) {
				Task.addAbilityTask(this);
				this.status = Status.HEALTHY;
				this.toxic = 0;
				Task.addTask(Task.STATUS, Status.HEALTHY, nickname + " became healthy!", this);
			}
		}
		
		if (this.ability == Ability.HYDRATION && field.equals(field.weather, Effect.RAIN, this) && this.status != Status.HEALTHY) {
			Task.addAbilityTask(this);
			this.status = Status.HEALTHY;
			Task.addTask(Task.STATUS, Status.HEALTHY, nickname + " became healthy!", this);
		}
		
		
		if (this.status == Status.TOXIC && toxic < 16) toxic++;
		if (this.status == Status.FROSTBITE) {
			this.damage(getHPAmount(1.0/16), f, this.nickname + " was hurt by frostbite!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		} else if (this.status == Status.BURNED) {
			if (this.ability == Ability.WATER_VEIL) {
				Task.addAbilityTask(this);
				Task.addTask(Task.STATUS, Status.HEALTHY, nickname + " became healthy!", this);
			}
			this.damage(getHPAmount(1.0/16), f, this.nickname + " was hurt by its burn!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		} else if (this.status == Status.POISONED) {
			if (this.ability == Ability.POISON_HEAL) {
				if (this.currentHP < this.getStat(0)) {
					Task.addAbilityTask(this);
					heal(getHPAmount(1.0/8), this.nickname + " restored HP!");
				}
			} else {
				this.damage(getHPAmount(1.0/8), f, this.nickname + " was hurt by poison!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}	
			}
			
		} else if (this.status == Status.TOXIC) {
			if (this.ability == Ability.POISON_HEAL) {
				if (this.currentHP < this.getStat(0)) {
					Task.addAbilityTask(this);
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
		if (this.hasStatus(Status.CURSED)) {
			this.damage(getHPAmount(1.0/4), f, this.nickname + " was hurt by the curse!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		}
		if (this.hasStatus(Status.LEECHED) && !f.isFainted()) {
			int hp = (int) getHPAmount(1.0/8);
			if (hp >= this.currentHP) hp = this.currentHP;
			this.damage(hp, f, f.nickname + " sucked health from " + this.nickname + "!");
			if (f.getItem() == Item.BIG_ROOT) hp *= 1.3;
			f.heal(hp, "");
			if (this.currentHP <= 0) {
				this.faint(true, f);
				return;
			}
			
		}
		if (this.hasStatus(Status.DROWSY)) {
			this.sleep(false, f);
			this.removeStatus(Status.DROWSY);
		}
		if (this.ability == Ability.PARASOCIAL && !f.isFainted()
				&& (f.hasStatus(Status.POSSESSED) || f.hasStatus(Status.CONFUSED) || f.status == Status.ASLEEP)) {
			int hp = (int) f.getHPAmount(1.0/8);
			if (hp >= f.currentHP) hp = f.currentHP;
			Task.addAbilityTask(this);
			f.damage(hp, this, this.nickname + " sucked health from " + f.nickname + "!");
			if (this.getItem() == Item.BIG_ROOT) hp *= 1.3;
			this.heal(hp, "");
			if (f.currentHP <= 0) {
				f.faint(true, this);
				return;
			}
			
		}
		if (this.hasStatus(Status.NIGHTMARE)) {
			if (this.status == Status.ASLEEP) {
				this.damage(getHPAmount(1.0/4), f, this.nickname + " had a nightmare!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			} else {
				this.removeStatus(Status.NIGHTMARE);
			}
		} if (this.getItem() == Item.STICKY_BARB) {
			this.damage(getHPAmount(1.0/8), f, this.nickname + " was hurt by its Sticky Barb!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
			
		} if (this.hasStatus(Status.AQUA_RING)) {
			if (this.currentHP < this.getStat(0)) {
				int hp = (int) getHPAmount(1.0/16);
				if (item == Item.BIG_ROOT) hp *= 1.3;
				heal(hp, this.nickname + " restored HP.");
			}
		} if (field.equals(field.terrain, Effect.GRASSY) && isGrounded()) {
			if (this.currentHP < this.getStat(0)) {
				heal(getHPAmount(1.0/16), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.RAIN_DISH && field.equals(field.weather, Effect.RAIN, this)) {
			if (this.currentHP < this.getStat(0)) {
				Task.addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.DRY_SKIN && field.equals(field.weather, Effect.RAIN, this)) {
			if (this.currentHP < this.getStat(0)) {
				Task.addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.ability == Ability.ICE_BODY && field.equals(field.weather, Effect.SNOW, this)) {
			if (this.currentHP < this.getStat(0)) {
				Task.addAbilityTask(this);
				heal(getHPAmount(1.0/8), this.nickname + " restored HP.");
			}
		} if (this.getItem() == Item.LEFTOVERS) {
			if (this.currentHP < this.getStat(0)) {
				heal(getHPAmount(1.0/16), this.nickname + " restored a little HP using its Leftovers!");
			}
		} if (this.ability == Ability.REFORGE) {
			if (this.currentHP < this.getStat(0)) {
				Task.addAbilityTask(this);
				heal(getHPAmount(1.0/16), this.nickname + " restored a little HP!");
			}
		} if (this.getItem() == Item.BLACK_SLUDGE) {
			if (this.isType(PType.POISON)) {
				if (this.currentHP < this.getStat(0)) {
					heal(getHPAmount(1.0/16), this.nickname + " restored a little HP using its Black Sludge!");
				}
			} else {
				this.damage(getHPAmount(1.0/16), f, this.nickname + " was hurt by its Black Sludge!");
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			}
		} if (this.hasStatus(Status.SPUN)) {
			if (this.spunCount == 0) {
				Task.addTask(Task.TEXT, this.nickname + " was freed from wrap!");
				this.removeStatus(Status.SPUN);
			} else {
				StatusEffect spin = this.getStatus(Status.SPUN);
				this.damage(getHPAmount(1.0/spin.num), f, this.nickname + " was hurt by being wrapped!");
				this.spunCount--;
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, f);
					return;
				}
			}
		} if (this.hasStatus(Status.SPELLBIND)) {
			if (this.spunCount == 0) {
				Task.addTask(Task.TEXT, this.nickname + " was freed from the spellbind!");
				this.removeStatus(Status.SPELLBIND);
			} else {
				StatusEffect spin = this.getStatus(Status.SPELLBIND);
				stat(this, 1, spin.num, f);
				stat(this, 3, spin.num, f);
				this.spunCount--;
			}
		}
		if (field.equals(field.weather, Effect.SANDSTORM, this) && !this.isType(PType.ROCK) && !this.isType(PType.STEEL) && !this.isType(PType.GROUND)
				&& this.ability != Ability.SAND_FORCE && this.ability != Ability.SAND_RUSH && this.ability != Ability.SAND_VEIL
				&& this.getItem() != Item.SAFETY_GOGGLES) {
			this.damage(getHPAmount(1.0/16), f, this.nickname + " was buffeted by the sandstorm!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		} if (this.ability == Ability.DRY_SKIN && field.equals(field.weather, Effect.SUN, this)) {
			Task.addAbilityTask(this);
			this.damage(getHPAmount(1.0/8), f, this.nickname + " was hurt!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		}
		
		if (this.ability == Ability.SOLAR_POWER && field.equals(field.weather, Effect.SUN, this) && field.weatherTurns > 1) {
			this.damage(getHPAmount(1.0/8), f, this.nickname + " was hurt!");
			if (this.currentHP <= 0) { // Check for kill
				this.faint(true, f);
				return;
			}
		}
		
		if (this.perishCount > 0) {
			this.perishCount--;
			Task.addTask(Task.TEXT, this.nickname + "'s perish count fell to " + this.perishCount + "!");
			if (this.perishCount == 0) {
				this.damage(this.currentHP, f, "");
				this.faint(true, f);
				return;
			}
		}
		if (this.hasStatus(Status.LOCKED) && this.outCount == 0 && (this.lastMoveUsed == Move.OUTRAGE || this.lastMoveUsed == Move.PETAL_DANCE || this.lastMoveUsed == Move.THRASH)) {
			this.confuse(false, f);
			this.removeStatus(Status.LOCKED);
		}
		if (this.hasStatus(Status.ENCORED) && --this.encoreCount == 0) {
			this.removeStatus(Status.ENCORED);
			Task.addTask(Task.TEXT, this.nickname + "'s encore ended!");
		}
		if (this.disabledMove != null && --this.disabledCount == 0) {
			Task.addTask(Task.TEXT, this.nickname + "'s " + disabledMove.toString() + " is no longer disabled!");
			this.disabledMove = null;
		}
		if (this.hasStatus(Status.TAUNTED) && --this.tauntCount == 0) {
			this.removeStatus(Status.TAUNTED);
			Task.addTask(Task.TEXT, this.nickname + " shook off the taunt!");
		}
		if (this.hasStatus(Status.TORMENTED) && --this.tormentCount == 0) {
			this.removeStatus(Status.TORMENTED);
			Task.addTask(Task.TEXT, this.nickname + "'s torment ended!");
		}
		if (this.hasStatus(Status.HEAL_BLOCK) && --this.healBlockCount == 0) {
			this.removeStatus(Status.HEAL_BLOCK);
			Task.addTask(Task.TEXT, this.nickname + " was freed from Heal Block!");
		}
		if (this.hasStatus(Status.LOCKED) && this.rollCount > 5) {
			this.removeStatus(Status.LOCKED);
			this.rollCount = 1;
		}
		if (this.hasStatus(Status.BONDED)) {
			this.removeStatus(Status.BONDED);
		}
		
		if (this.ability == Ability.SPEED_BOOST && !this.impressive && this.statStages[4] != 6) {
			Task.addAbilityTask(this);
			stat(this, 4, 1, f);
		}
		
		if (this.ability == Ability.MOODY) {
			Task.addAbilityTask(this);
			boolean ignoreRaise = this.hasMaxedStatStages(false);
			
			Random random = new Random();
			int raise = 0;
			if (!ignoreRaise) {
				do {
					raise = random.nextInt(5);
				} while (this.statStages[raise] >= 6);
			}
			int lower;
			int counter = 0;
			do {
				lower = random.nextInt(5);
				counter++;
			} while ((this.statStages[lower] <= -6 || (!ignoreRaise && lower == raise)) && counter < 25);
			if (!ignoreRaise) stat(this, raise, 2, f);
			stat(this, lower, -1, f);
		}
		
		if (this.getItem() == Item.FLAME_ORB && this.status == Status.HEALTHY) {
			this.burn(false, this);
		}
		if (this.getItem() == Item.TOXIC_ORB && this.status == Status.HEALTHY) {
			this.toxic(false, this);
		}
		if (this.hasStatus(Status.YAWNING)) {
			this.removeStatus(Status.YAWNING);
			this.addStatus(Status.DROWSY);
		}
		
		checkBerry(f);
		
		if ((this.status != Status.HEALTHY || this.hasStatus(Status.CONFUSED)) && this.getItem() != null && this.item.isStatusBerry()) {
			eatBerry(this.item, true, f);
		}
		
		if (this.getItem() == Item.WHITE_HERB) {
			this.handleWhiteHerb(userStages, f);
		}
		if (f.getItem() == Item.MIRROR_HERB) {
			f.handleMirrorHerb(userStages, this);
		}
		if (this.getItem() == Item.EJECT_PACK) {
			this.handleEjectPack(userStages, f);
		}
		
		this.removeStatus(Status.FLINCHED);
		this.removeStatus(Status.PROTECT);
		this.removeStatus(Status.ENDURE);
		this.removeStatus(Status.SWITCHING);
		if (this.hasStatus(Status.LANDED)) {
			if (this.type1 == PType.UNKNOWN) {
				this.type1 = PType.FLYING;
			}
			if (this.type2 == PType.UNKNOWN) {
				this.type2 = PType.FLYING;
			}
			this.removeStatus(Status.LANDED);
		}
		boolean result = this.removeStatus(Status.SWAP);
		if (result) System.out.println(this.nickname + " had swap at the end of the turn (bad)");
	}

	public int getSpeed() {
		double speed = this.getStat(5) * this.asModifier(4);
		if (this.status == Status.PARALYZED) speed *= 0.5;
		if (this.getItem() == Item.IRON_BALL) speed *= 0.5;
		if (this.getItem() == Item.CHOICE_SCARF) speed *= 1.5;
		if (this.ability == Ability.UNBURDEN && consumedItem) speed *= 2;
		if (checkAbilitySpeedBoost(this.ability)) speed *= 2;
		return (int) speed;
	}
	
	public Pokemon getFaster(Pokemon other, int thisP, int otherP) {
		int speed1 = this.getSpeed();
		if (field.contains(this.getFieldEffects(), Effect.TAILWIND)) speed1 *= 2;
		int speed2 = other.getSpeed();
		if (field.contains(other.getFieldEffects(), Effect.TAILWIND)) speed2 *= 2;
		
		if (thisP > otherP) return this;
		if (otherP > thisP) return other;
		
		if (this.getItem() == Item.LAGGING_TAIL && other.getItem() != Item.LAGGING_TAIL) return other;
		if (other.getItem() == Item.LAGGING_TAIL && this.getItem() != Item.LAGGING_TAIL) return this;
		
		Pokemon faster = speed1 > speed2 ? this : other;
		if (speed1 == speed2) {
			Random random = new Random();
			boolean isHeads = random.nextBoolean();
			faster = isHeads ? this : other;
		}
		if (field.contains(field.fieldEffects, Effect.TRICK_ROOM)) {
			faster = faster == this ? other : this;
		}

		return faster;
	}
	
	private boolean checkAbilitySpeedBoost(Ability ability) {
		if (field.equals(field.weather, Effect.SUN, this) && ability == Ability.CHLOROPHYLL) return true;
		if (field.equals(field.weather, Effect.RAIN, this) && ability == Ability.SWIFT_SWIM) return true;
		if (field.equals(field.weather, Effect.SANDSTORM, this) && ability == Ability.SAND_RUSH) return true;
		if (field.equals(field.weather, Effect.SNOW, this) && ability == Ability.SLUSH_RUSH) return true;
		if (field.equals(field.terrain, Effect.ELECTRIC) && ability == Ability.VOLT_VORTEX) return true;
		return false;
	}


	public void confuse(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.isType(PType.MAGIC)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Magic is immune to confusion)");
			return;
		}
		if (!this.hasStatus(Status.CONFUSED)) {
			this.addStatus(Status.CONFUSED);
			this.confusionCounter = (int)(Math.random() * 4) + 1;
			Task.addTask(Task.TEXT, this.nickname + " became confused!");
			if (this.getItem() == Item.PERSIM_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to cure its confusion!");
				this.removeStatus(Status.CONFUSED);
				this.confusionCounter = 0;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
		
	}
	
	public void sleep(boolean announce, Pokemon foe) {
		sleep(announce, foe, null);
	}
	
	public void sleep(boolean announce, Pokemon foe, Move move) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (isGrounded() && field.equals(field.terrain, Effect.ELECTRIC)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Electric Terrain!");
			return;
		}
		if (move != Move.REST && this.isType(PType.PSYCHIC)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Psychic is immune to sleep)");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.INSOMNIA) {
				if (announce) {
					Task.addAbilityTask(this);
					Task.addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.ASLEEP;
			this.sleepCounter = (int)(Math.random() * 3) + 1;
			Task.addTask(Task.STATUS, Status.ASLEEP, this.nickname + " fell asleep!", this);
			if (this.getItem() == Item.CHESTO_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its sleep!", this);
				this.status = Status.HEALTHY;
				this.sleepCounter = 0;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
		
	}
	
	public void paralyze(boolean announce, Pokemon foe) {
		this.paralyze(announce, foe, null);
	}
	
	public void paralyze(boolean announce, Pokemon foe, Pokemon ability) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.isType(PType.ELECTRIC)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.PARALYZED;
			Task.addAbilityTask(ability);
			Task.addTask(Task.STATUS, Status.PARALYZED, this.nickname + " was paralyzed!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				foe.paralyze(false, this, this);
			}
			if (this.getItem() == Item.CHERI_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its paralysis!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void burn(boolean announce, Pokemon foe) {
		this.burn(announce, foe, null);
	}

	public void burn(boolean announce, Pokemon foe, Pokemon ability) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.isType(PType.FIRE)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (field.equals(field.weather, Effect.SNOW, this)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Snow blocks Burn)");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.WATER_VEIL) {
				if (announce) {
					Task.addAbilityTask(this);
					Task.addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.BURNED;
			Task.addAbilityTask(ability);
			Task.addTask(Task.STATUS, Status.BURNED, this.nickname + " was burned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				foe.burn(false, this, this);
			}
			if (this.getItem() == Item.RAWST_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its burn!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void poison(boolean announce, Pokemon foe) {
		this.poison(announce, foe, null);
	}
	
	public void poison(boolean announce, Pokemon foe, Pokemon ability) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (foe.ability != Ability.CORROSION && (this.isType(PType.POISON) || this.isType(PType.STEEL))) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.POISONED;
			Task.addAbilityTask(ability);
			Task.addTask(Task.STATUS, Status.POISONED, this.nickname + " was poisoned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				foe.poison(false, this, this);
			}
			if (this.getItem() == Item.PECHA_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its poison!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void toxic(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (foe.ability != Ability.CORROSION && (this.isType(PType.POISON) || this.isType(PType.STEEL))) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (this.status == Status.HEALTHY) {
			this.status = Status.TOXIC;
			Task.addTask(Task.STATUS, Status.TOXIC, this.nickname + " was badly poisoned!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				Task.addAbilityTask(this);
				foe.toxic(false, this);
			}
			if (this.getItem() == Item.PECHA_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its poison!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void freeze(boolean announce, Pokemon foe) {
		if (this.isFainted()) return;
		if (field.contains(this.getFieldEffects(), Effect.SAFEGUARD)) {
			if (announce) Task.addTask(Task.TEXT, this.nickname + " is protected by the Safeguard!");
			return;
		}
		if (this.isType(PType.ICE)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			return;
		}
		if (field.equals(field.weather, Effect.SUN, this)) {
			if (announce) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...\n(In this game, Harsh Sunlight blocks Frostbite)");
			return;
		}
		if (this.status == Status.HEALTHY) {
			if (this.ability == Ability.MAGMA_ARMOR) {
				if (announce) {
					Task.addAbilityTask(this);
					Task.addTask(Task.TEXT, "It doesn't effect " + nickname + "...");
				}
				return;
			}
			this.status = Status.FROSTBITE;
			Task.addTask(Task.STATUS, Status.FROSTBITE, this.nickname + " was frostbitten!", this);
			if (this.ability == Ability.SYNCHRONIZE && this != foe) {
				Task.addAbilityTask(this);
				foe.freeze(false, this);
			}
			if (this.getItem() == Item.ASPEAR_BERRY || this.getItem() == Item.LUM_BERRY) {
				Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + this.item.toString() + " to cure its frostbite!", this);
				this.status = Status.HEALTHY;
				this.consumeItem(foe);
			}
		} else {
			if (announce) fail();
		}
	}
	
	public void consumeItem(Pokemon foe) {
		this.item = null;
		this.metronome = 0;
		this.consumedItem = true;
		if (this.ability == Ability.FULL_FORCE && foe != null) {
			Task.addAbilityTask(this);
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
		    if (this.item == null && this.headbuttCrit >= 0) {
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
			if (this.currentHP == this.getStat(0) && this.headbuttCrit >= 0) {
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
			bp = Math.min(160, 40 * this.moveMultiplier);
			if (this.lastMoveUsed == Move.FURY_CUTTER) {
				if (announce) this.moveMultiplier *= 2;
			}
		} else if (move == Move.GRASS_KNOT || move == Move.LOW_KICK) {
			double weight = foe.calcWeight();
			if (weight < 21.9) {
				bp = 20;
			} else if (weight >= 21.9 && weight < 55.1) {
				bp = 40;
			} else if (weight >= 55.1 && weight < 110.2) {
				bp = 60;
			} else if (weight >= 110.2 && weight < 220.4) {
				bp = 80;
			} else if (weight >= 220.4 && weight < 440.9) {
				bp = 100;
			} else if (weight >= 440.9) {
				bp = 120;
			}
		} else if (move == Move.GYRO_BALL) {
			double speedRatio = foe.getSpeed() * 1.0 / this.getSpeed();
			bp = (int) Math.min(150, (25 * speedRatio) + 1);
		} else if (move == Move.HEAT_CRASH || move == Move.HEAVY_SLAM) {
			double weightRatio = foe.calcWeight() * 1.0 / this.calcWeight();
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
				if (announce) Task.addTask(Task.TEXT, "Magnitude 4!");
			} else if (mag > 5 && mag <= 15) {
				bp = 30;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 5!");
			} else if (mag > 15 && mag <= 35) {
				bp = 50;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 6!");
			} else if (mag > 35 && mag <= 65) {
				bp = 70;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 7!");
			} else if (mag > 65 && mag <= 85) {
				bp = 90;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 8!");
			} else if (mag > 85 && mag <= 95) {
				bp = 110;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 9!");
			} else if (mag > 95 && mag <= 100) {
				bp = 150;
				if (announce) Task.addTask(Task.TEXT, "Magnitude 10!");
			}
		} else if (move == Move.RAGE) {
			bp = Math.min(160, 40 * this.moveMultiplier);
			if (this.lastMoveUsed == Move.RAGE) {
				if (announce) this.moveMultiplier *= 2;
			}
		} else if (move == Move.RISING_VOLTAGE) {
			if (field.equals(field.terrain, Effect.ELECTRIC) && foe.isGrounded()) {
				bp = 140;
			} else {
				bp = 70;
			}
		} else if (move == Move.PAYBACK) {
			if (first || this.headbuttCrit < 0) {
				bp = 50;
			} else {
				bp = 100;
			}
		} else if (move == Move.FRUSTRATION) {
			int f = this.happiness;
			f = 255 - this.happiness;
			bp = Math.max(f * 2 / 5, 1);
		} else if (move == Move.RETURN) {
			int f = this.happiness;
			bp = (int) (-3.0 / 14450 * f * f + 11.0 / 34 * f + 1);
		} else if (move == Move.REVENGE) {
			if (!first || this.headbuttCrit < 0) {
				bp = 60;
			} else {
				bp = 120;
			}
		} else if (move == Move.ROLLOUT || move == Move.ICE_BALL) {
			bp = (int) (30 * Math.pow(2, this.rollCount - 1));
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
			if (foe.status == Status.POISONED || foe.status == Status.TOXIC) {
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
	
	private void removeBad() {
		this.removeStatus(Status.CONFUSED);
		this.removeStatus(Status.CURSED);
		this.removeStatus(Status.LEECHED);
		this.removeStatus(Status.NIGHTMARE);
		this.removeStatus(Status.FLINCHED);
		this.removeStatus(Status.SPUN);
		this.removeStatus(Status.RECHARGE);
		this.removeStatus(Status.POSSESSED);
		this.removeStatus(Status.LOCKED);
		this.removeStatus(Status.TRAPPED);
		this.removeStatus(Status.ENCORED);
		this.removeStatus(Status.TAUNTED);
		this.removeStatus(Status.TORMENTED);
		if (this.getStatusNum(Status.CRIT_CHANCE) < 0) this.removeStatus(Status.CRIT_CHANCE);
		this.removeStatus(Status.NO_SWITCH);
		this.removeStatus(Status.SMACK_DOWN);
		this.removeStatus(Status.MUTE);
		this.removeStatus(Status.YAWNING);
		this.removeStatus(Status.DROWSY);
		this.removeStatus(Status.HEAL_BLOCK);
		this.removeStatus(Status.ARCANE_SPELL);
		this.removeStatus(Status.SPELLBIND);
		this.removeStatus(Status.DECK_CHANGE);
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
		this.nat = Nature.getRandomNature();
	}
	
	public String getNature() {
		return nat.toString();
	}

	public JPanel showSummary(boolean takeItem, JPanel panel) {
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
	        nameLabel = new JLabel(this.name() + " Lv. " + this.getLevel());
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
	        	String type = getStatType(i, false);
	        	stats[i] = new JLabel(type + this.getStat(i));
	        	stats[i].setFont(new Font(stats[i].getFont().getName(), Font.BOLD, 14));
	        	stats[i].setSize(50, stats[i].getHeight());
	        	
	        	if (i != 0) {
	        	    if (this.nat.getStat(i - 1) == 1.1) {
	        	        stats[i].setForeground(Color.red.darker().darker());
	        	        stats[i].setText(type + this.getStat(i) + " \u2191"); // Up arrow
	        	    } else if (this.nat.getStat(i - 1) == 0.9) {
	        	        stats[i].setForeground(Color.blue.darker().darker());
	        	        stats[i].setText(type + this.getStat(i) + " \u2193"); // Down arrow
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
	                Move move = moveset[i].move;
	                PType mtype = move.mtype;
	                if (move == Move.HIDDEN_POWER) mtype = this.determineHPType();
					if (move == Move.RETURN) mtype = this.determineHPType();
					if (move == Move.WEATHER_BALL) mtype = this.determineWBType();
					if (move == Move.TERRAIN_PULSE) mtype = this.determineTPType();
					if (move.isAttack()) {
						if (mtype == PType.NORMAL) {
							if (this.ability == Ability.GALVANIZE) mtype = PType.ELECTRIC;
							if (this.ability == Ability.REFRIGERATE) mtype = PType.ICE;
							if (this.ability == Ability.PIXILATE) mtype = PType.LIGHT;
						} else {
							if (this.ability == Ability.NORMALIZE) mtype = PType.NORMAL;
						}
					}
			        Color color = mtype.getColor();
	                moveButton.setBackground(color);
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
	    JPanel itemDescPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JLabel itemLabel = new JLabel("N/A");
	    JLabel itemDesc = new JLabel("N/A");
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
	    	    	//Item old = this.item;
					//player.bag.add(old);
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
	    	itemDesc.setText("<html>" + Item.breakString(this.item.getDesc(), Math.max(this.ability.desc.length() - 4, 50)).replace("\n", "<br>") + "</html>");
	    	teamMemberPanel.add(itemPanel);
	    	itemDescPanel.add(itemDesc);
	    	teamMemberPanel.add(itemDescPanel);
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
	
	public static int[] determineOptimalIVs(PType hpType) {
        if (hpType == PType.NORMAL || hpType == PType.UNKNOWN) {
            return new int[] {31, 31, 31, 31, 31, 31};
        }
        
        // initialization for error message
        Pokemon tempPokemon = new Pokemon(1, 5, true, false);
        int[] ivs = null;
        
        // get target index
        int targetIndex = hpType.ordinal() - 1;
        
        // find range of sums that map to target type index
        int lowerBoundSum = (targetIndex * 63) / 18;
        int upperBoundSum = ((targetIndex + 1) * 63) / 18 - 1;

        // iterate from highest possible sum to lowest
        for (int sum = upperBoundSum; sum >= lowerBoundSum; sum--) {
            // create new array
            ivs = new int[6];
            int tempSum = sum;

            // try to assign highest set of ivs
            for (int i = 5; i >= 0; i--) {
                if ((tempSum & (1 << i)) != 0) {
                    ivs[i] = 31; // max iv if bit is set
                } else {
                    ivs[i] = 30; // highest even iv to not set the bit
                }
            }

            // verification
            tempPokemon.ivs = ivs;
            if (tempPokemon.determineHPType() == hpType) {
                return ivs;
            }
            
        }
        
        // if no iv set is found (we're fucked)
        throw new IllegalStateException("The algorithm returned an array of " + Arrays.toString(ivs) + " which resulted in HP " + tempPokemon.determineHPType() + " instead of " + hpType);
    }
	
	public PType determineWBType() {
		PType result = PType.NORMAL;
		if (field.equals(field.weather, Effect.SUN, this)) result = PType.FIRE;
		if (field.equals(field.weather, Effect.RAIN, this)) result = PType.WATER;
		if (field.equals(field.weather, Effect.SNOW, this)) result = PType.ICE;
		if (field.equals(field.weather, Effect.SANDSTORM, this)) result = PType.ROCK;
		
		return result;
	}
	
	public PType determineTPType() {
		PType result = PType.NORMAL;
		if (field.equals(field.terrain, Effect.GRASSY)) result = PType.GRASS;
		if (field.equals(field.terrain, Effect.ELECTRIC)) result = PType.ELECTRIC;
		if (field.equals(field.terrain, Effect.PSYCHIC)) result = PType.PSYCHIC;
		if (field.equals(field.terrain, Effect.SPARKLY)) result = PType.MAGIC;
		
		return result;
	}

	public void swapIn(Pokemon foe, boolean hazards) {
		int[] userStages = this.statStages.clone();
		int[] foeStages = foe.statStages.clone();
		
		if (this.ability == Ability.DROUGHT && !field.equals(field.weather, Effect.SUN)) {
			Task.addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SUN));
			if (this.getItem() == Item.HEAT_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.DRIZZLE && !field.equals(field.weather, Effect.RAIN)) {
			Task.addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.RAIN));
			if (this.getItem() == Item.DAMP_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.SAND_STREAM && !field.equals(field.weather, Effect.SANDSTORM)) {
			Task.addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SANDSTORM));
			if (this.getItem() == Item.SMOOTH_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.SNOW_WARNING && !field.equals(field.weather, Effect.SNOW)) {
			Task.addAbilityTask(this);
			field.setWeather(field.new FieldEffect(Effect.SNOW));
			if (this.getItem() == Item.ICY_ROCK) field.weatherTurns = 8;
		} else if (this.ability == Ability.CLOUD_NINE && field.weather != null) {
			Task.addAbilityTask(this);
			Task t = Task.addTask(Task.WEATHER, "The weather returned to normal!");
            t.setEffect(null);
            field.weather = null;
		} else if (this.ability == Ability.GRASSY_SURGE && !field.equals(field.terrain, Effect.GRASSY)) {
			Task.addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.GRASSY));
			if (this.getItem() == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.ELECTRIC_SURGE && !field.equals(field.terrain, Effect.ELECTRIC)) {
			Task.addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.ELECTRIC));
			if (this.getItem() == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.PSYCHIC_SURGE && !field.equals(field.terrain, Effect.PSYCHIC)) {
			Task.addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.PSYCHIC));
			if (this.getItem() == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.SPARKLY_SURGE && !field.equals(field.terrain, Effect.SPARKLY)) {
			Task.addAbilityTask(this);
			field.setTerrain(field.new FieldEffect(Effect.SPARKLY));
			if (this.getItem() == Item.TERRAIN_EXTENDER) field.terrainTurns = 8;
		} else if (this.ability == Ability.GRAVITATION) {
			Task.addAbilityTask(this);
			field.setEffect(field.new FieldEffect(Effect.GRAVITY));
		} else if (this.ability == Ability.COSMIC_WARP) {
			Task.addAbilityTask(this);
			FieldEffect effect = field.new FieldEffect(Effect.TRICK_ROOM);
			effect.turns = 4;
			field.setEffect(effect);
			this.checkSeed(foe, Item.ROOM_SERVICE, Effect.TRICK_ROOM);
		} else if (this.ability == Ability.MYSTIC_RIFT) {
			Task.addAbilityTask(this);
			FieldEffect effect = field.new FieldEffect(Effect.MAGIC_ROOM);
			effect.turns = 5;
			field.setEffect(effect);
			this.checkSeed(foe, Item.ROOM_SERVICE, Effect.TRICK_ROOM);
		} else if (this.ability == Ability.EVERGLOW) {
			Task.addAbilityTask(this);
			if (!(field.contains(this.getFieldEffects(), Effect.AURORA))) {
				this.getFieldEffects().add(field.new FieldEffect(Effect.AURORA));
				Task.addTask(Task.TEXT, "A glowing Aurora surrounded " + this.nickname + "'s team!");
			}
			this.checkSeed(foe, Item.ROOM_SERVICE, Effect.TRICK_ROOM);
		} else if (this.ability == Ability.INTIMIDATE || this.ability == Ability.SCALY_SKIN) {
			Task.addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, foe.nickname + "'s Attack was not lowered!");
			} else {
				stat(foe, 0, -1, this);
			}
			if (foe.getItem() == Item.ADRENALINE_ORB) {
				Task.addTask(Task.TEXT, foe.nickname + " used its " + foe.item.toString() + " to boost its Speed!");
				stat(foe, 4, 1, this);
				foe.consumeItem(this);
			}
		} else if (this.ability == Ability.TERRIFY) {
			Task.addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, foe.nickname + "'s Sp. Atk was not lowered!");
			} else {
				stat(foe, 2, -1, this);
			}
		} else if (this.ability == Ability.THREATENING) {
			Task.addAbilityTask(this);
			if (foe.ability == Ability.INNER_FOCUS || foe.ability == Ability.SCRAPPY || foe.ability == Ability.UNWAVERING) {
				Task.addAbilityTask(foe);
				Task.addTask(Task.TEXT, foe.nickname + "'s Defense was not lowered!");
			} else {
				stat(foe, 1, -1, this);
			}
		} else if (this.ability == Ability.ILLUSION) {
			Task.addAbilityTask(this);
			Task.addTask(Task.TEXT, this.nickname + " casts a terrifying illusion!");
			this.illusion = true;
		} else if (this.ability == Ability.PICKPOCKET) {
			if (this.item == null && foe.item != null && foe.ability != Ability.STICKY_HOLD) {
				Task.addAbilityTask(this);
				Task.addTask(Task.TEXT, this.nickname + " stole the foe's " + foe.item.toString() + "!");
				this.item = foe.item;
				if (!foe.loseItem) foe.lostItem = foe.item;
				foe.consumeItem(this);
				this.loseItem = true;
			}
		} else if (this.ability == Ability.MOUTHWATER) {
			if (!foe.hasStatus(Status.TAUNTED)) {
				foe.addStatus(Status.TAUNTED);
			    foe.tauntCount = 4;
				Task.addAbilityTask(this);
				Task.addTask(Task.TEXT, foe.nickname + " was taunted!");
				if (foe.getItem() == Item.MENTAL_HERB) {
					Task.addTask(Task.TEXT, foe.nickname + " cured its taunt using its Mental Herb!");
					foe.removeStatus(Status.TAUNTED);
					foe.tauntCount = 0;
					foe.consumeItem(this);
				}
			}
		} else if (this.ability == Ability.ANTICIPATION) {
			boolean shuddered = false;
			for (Moveslot moveslot : foe.moveset) {
				if (moveslot != null) {
					Move move = moveslot.move;
					if (move != null && move.isAttack()) {
						PType mtype = move.mtype;
						if (move == Move.HIDDEN_POWER) mtype = foe.determineHPType();
						if (move == Move.RETURN) mtype = foe.determineHPType();
						if (move == Move.WEATHER_BALL) mtype = foe.determineWBType();
						if (move == Move.TERRAIN_PULSE) mtype = foe.determineTPType();
						double multiplier = getEffectiveMultiplier(mtype, move, foe);
						
						if (multiplier > 1) shuddered = true;
					}
				}
				if (shuddered) break;
			}
			if (shuddered) {
				Task.addAbilityTask(this);
				Task.addTask(Task.TEXT, nickname + " shuddered!");
			}
		} else if (this.ability == Ability.TRACE) {
			if (this.getItem() != Item.ABILITY_SHIELD) {
				Task.addAbilityTask(this);
				this.ability = foe.ability;
				Task.addTask(Task.TEXT, this.nickname + "'s ability became " + this.ability + "!");
				this.swapIn(foe, false);
			}
		} else if (this.ability == Ability.TALENTED) {
			Task.addAbilityTask(this);
			Task.addTask(Task.TEXT, this.nickname + " copied " + foe.nickname + "'s stat boosts!");
			for (int i = 0; i < 7; ++i) {
				if (foe.statStages[i] > 0) {
					stat(this, i, foe.statStages[i], foe);
				}
			}
		} else if (this.ability == Ability.PRESSURE) {
			Task.addAbilityTask(this);
			Task.addTask(Task.TEXT, this.nickname + " is exerting its Pressure!");
		} else if (this.ability == Ability.MOLD_BREAKER) {
			Task.addAbilityTask(this);
			Task.addTask(Task.TEXT, this.nickname + " breaks the mold!");
		}
		this.checkSeed(foe, Item.GRASSY_SEED, Effect.GRASSY);
		this.checkSeed(foe, Item.ELECTRIC_SEED, Effect.ELECTRIC);
		this.checkSeed(foe, Item.PSYCHIC_SEED, Effect.PSYCHIC);
		this.checkSeed(foe, Item.SPARKLY_SEED, Effect.SPARKLY);
		if (this.getItem() == Item.AIR_BALLOON) {
			Task.addTask(Task.TEXT, this.nickname + " floated on its Air Balloon!");
		}
		if (hazards) {
			if (this.currentHP < this.getStat(0) && field.contains(this.getFieldEffects(), Effect.HEALING_CIRCLE)) {
				this.heal(this.getHPAmount(1.0/4), this.nickname + " restored HP from the Healing Circle!");
			}
		}
		if (hazards && this.getItem() != Item.HEAVY$DUTY_BOOTS && this.ability != Ability.SHIELD_DUST) {
			if (field.contains(this.getFieldEffects(), Effect.STEALTH_ROCKS)) {
				double multiplier = getEffectiveMultiplier(PType.ROCK, null, null);
				double damage = (this.getHPAmount(1.0/8)) * multiplier;
				this.damage((int) Math.max(Math.floor(damage), 1), foe);
				Task.addTask(Task.TEXT, "Pointed stones dug into " + this.nickname + "!");
			}
			if (field.contains(this.getFieldEffects(), Effect.SPIKES) && this.isGrounded()) {
				double layers = field.getLayers(this.getFieldEffects(), Effect.SPIKES);
				double damage = (this.getHPAmount(1.0/8) * (layers + 1) / 2);
				this.damage((int) Math.max(Math.floor(damage), 1), foe);
				Task.addTask(Task.TEXT, this.nickname + " was hurt by Spikes!");
			}
			
			if (field.contains(this.getFieldEffects(), Effect.TOXIC_SPIKES) && this.isGrounded()) {
				int layers = field.getLayers(this.getFieldEffects(), Effect.TOXIC_SPIKES);
				if (layers == 1) this.poison(false, this);
				if (layers == 2) this.toxic(false, this);
				
				if (this.isType(PType.POISON)) {
					field.remove(this.getFieldEffects(), Effect.TOXIC_SPIKES);
					Task.addTask(Task.TEXT, "The toxic spikes disappeared from " + this.nickname + "'s feet!");
				}
			}
			
			if (field.contains(this.getFieldEffects(), Effect.STICKY_WEBS) && this.isGrounded()) {
				foe.stat(this, 4, -1, foe);
			}
			
			if (field.contains(this.getFieldEffects(), Effect.FLOODLIGHT)) {
				foe.stat(this, 6, -1, foe);
			}
			
			if (this.currentHP <= 0) {
				this.faint(true, foe);
			}
		}
		
		if (this.getItem() == Item.WHITE_HERB) {
			this.handleWhiteHerb(userStages, foe);
		}
		
		if (this.getItem() == Item.MIRROR_HERB) {
			this.handleMirrorHerb(foeStages, foe);
		}
		
		if (this.getItem() == Item.EJECT_PACK) {
			this.handleEjectPack(userStages, foe);
		}
		
		if (foe.getItem() == Item.WHITE_HERB) {
			foe.handleWhiteHerb(foeStages, this);
		}
		
		if (foe.getItem() == Item.MIRROR_HERB) {
			foe.handleMirrorHerb(userStages, this);
		}
		
		if (foe.getItem() == Item.EJECT_PACK) {
			foe.handleEjectPack(userStages, this);
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
		if ((this.ability == Ability.MAGIC_GUARD || this.ability == Ability.SCALY_SKIN) && move == null) return;
		this.currentHP -= amt;
		if (sturdy) this.currentHP = 1;
		Task t;
		if (damageIndex >= 0) {
			t = Task.createTask(Task.DAMAGE, message, this);
			t.wipe = true;
			t.evo = foe;
			Task.setTask(damageIndex, t);
		} else {
			t = Task.addTask(Task.DAMAGE, message, this);
		}
		
		t.setFinish(this.currentHP < 0 ? 0 : this.currentHP);
		
		Ability thisAbility = ability;
		
		if (this.getItem() != Item.ABILITY_SHIELD &&
				(move == Move.FUSION_BOLT || move == Move.FUSION_FLARE || foe.ability == Ability.MOLD_BREAKER)) {
			thisAbility = Ability.NULL;
		}
		
		if (move != null && currentHP > 0 && (move != Move.INCINERATE && move != Move.KNOCK_OFF && move != Move.BUG_BITE && move != Move.PLUCK &&
				move != Move.THIEF && move != Move.COVET) || thisAbility == Ability.STICKY_HOLD) this.checkBerry(foe);
	}

	private void checkBerry(Pokemon foe) {
		if (this.getItem() == null) return;
		if (this.item.getPocket() != Item.BERRY) return;
		double hpRatio = this.currentHP * 1.0 / this.getStat(0);
		if (hpRatio <= 0.25 && this.item.isPinchBerry()) {
			eatBerry(this.item, true, foe);
		} else if (hpRatio <= 0.5 && (this.getItem() == Item.ORAN_BERRY || this.getItem() == Item.SITRUS_BERRY)) {
			eatBerry(this.item, true, foe);
		} else {
			return;
		}
	}
	
	private void eatBerry(Item berry, boolean consume, Pokemon foe) {
		eatBerry(berry, consume, foe, this.lastMoveUsed);
	}
	
	private void eatBerry(Item berry, boolean consume, Pokemon foe, Move m) {
		if (berry.isBerry()) {
			if (berry == Item.WIKI_BERRY) {
				heal(getHPAmount(1.0/3), this.nickname + " ate its " + berry.toString() + " to restore HP!");
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.LIECHI_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 0, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.GANLON_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 1, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.PETAYA_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 2, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.APICOT_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 3, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.SALAC_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 4, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.STARF_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, new Random().nextInt(7), 2, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.MICLE_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 5, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.LANSAT_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				Task.addTask(Task.TEXT, this.nickname + "'s crit chance was heightened!");
				this.incrementStatus(Status.CRIT_CHANCE, 1);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.JABOCA_BERRY || berry == Item.ROWAP_BERRY) {
				foe.damage(foe.getHPAmount(1.0/8), this, this.nickname + " ate its " + berry.toString() + " to damage " + foe.nickname + "!");
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.KEE_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 1, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.MARANGA_BERRY) {
				Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + "!");
				stat(this, 3, 1, foe);
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.ENIGMA_BERRY) {
				this.heal(this.getHPAmount(1.0/2), this.nickname + " ate its " + berry.toString() + " to restore HP!");
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.ORAN_BERRY || berry == Item.SITRUS_BERRY) {
				int healAmt = (int) (berry == Item.ORAN_BERRY ? 10 : (this.getHPAmount(1.0/4)));
				heal(healAmt, this.nickname + " ate its " + berry.toString() + " to restore HP!");
				if (consume) this.consumeItem(foe);
			} else if (berry == Item.LEPPA_BERRY) {
				int index = -1;
				for (int i = 0; i < this.moveset.length; i++) {
					if (this.moveset[i].move == m) {
						index = i;
						break;
					}
				}
				if (index >= 0) {
					this.moveset[index].currentPP += 10;
					if (this.moveset[index].currentPP > this.moveset[index].maxPP) this.moveset[index].currentPP = this.moveset[index].maxPP;
					Task.addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to restore PP to " + m.toString() + "!");
				}
			}
			
			if (this.status != Status.HEALTHY || this.hasStatus(Status.CONFUSED)) {
				if ((this.status == Status.POISONED || this.status == Status.TOXIC) && (berry == Item.PECHA_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its poison!", this);
					this.status = Status.HEALTHY;
					this.consumeItem(foe);
				}
				if (this.status == Status.BURNED && (berry == Item.RAWST_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its burn!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.PARALYZED && (berry == Item.CHERI_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its paralysis!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.FROSTBITE && (berry == Item.ASPEAR_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its frostbite!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.status == Status.ASLEEP && (berry == Item.CHESTO_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.STATUS, Status.HEALTHY, this.nickname + " ate its " + berry.toString() + " to cure its sleep!", this);
					this.status = Status.HEALTHY;
					if (consume) this.consumeItem(foe);
				}
				if (this.hasStatus(Status.CONFUSED) && (berry == Item.PERSIM_BERRY || berry == Item.LUM_BERRY)) {
					Task.addTask(Task.TEXT, this.nickname + " ate its " + berry.toString() + " to cure its confusion!", this);
					this.removeStatus(Status.CONFUSED);
					if (consume) this.consumeItem(foe);
				}
			}
		}
	}
	
	private void heal(double amt, String message) {
		if (this.hasStatus(Status.HEAL_BLOCK)) return;
		Task t = Task.createTask(Task.DAMAGE, message, this);
		currentHP += amt;
		verifyHP();
		if (gp.gameState == GamePanel.BATTLE_STATE) {
			t.setFinish(currentHP);
			gp.battleUI.tasks.add(t);
		} else if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			t.setFinish(currentHP);
			gp.simBattleUI.tasks.add(t);
		}
		
	}

	public double getEffectiveMultiplier(PType mtype, Move m, Pokemon foe) {
		double multiplier = 1.0;
		
		if (getImmune(this, mtype)) {
			if (foe != null) {
				if (this.getItem() == Item.RING_TARGET) {
					// Nothing
				} else if (foe.ability == Ability.SCRAPPY && (mtype == PType.NORMAL || mtype == PType.FIGHTING)) {
					// Nothing: scrappy allows normal and fighting type moves to hit ghosts
				} else if (foe.ability == Ability.CORROSION && mtype == PType.POISON) {
					// Nothing: corrosion allows poison moves to hit steel
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		
		PType[] resistances = getResistances(mtype);
		if (m == Move.FREEZE$DRY || m == Move.SKY_UPPERCUT) {
			ArrayList<PType> types = new ArrayList<>();
			for (PType type : resistances) {
				types.add(type);
			}
			if (m == Move.FREEZE$DRY) types.remove(PType.WATER);
			if (m == Move.SKY_UPPERCUT) types.remove(PType.FLYING);
			resistances = types.toArray(new PType[0]);
		}
        for (PType resistance : resistances) {
            if (this.type1 == resistance || this.type2 == resistance) {
                multiplier /= 2;
            }
        }
        
        PType[] weaknesses = getWeaknesses(mtype);
        if (m == Move.FREEZE$DRY || m == Move.SKY_UPPERCUT) {
			PType[] temp = new PType[weaknesses.length + 1];
			for (int i = 0; i < weaknesses.length; i++) {
				temp[i] = weaknesses[i];
			}
			if (m == Move.FREEZE$DRY) temp[weaknesses.length] = PType.WATER;
			if (m == Move.SKY_UPPERCUT) temp[weaknesses.length] = PType.FLYING;
			weaknesses = temp;
		}
        for (PType weakness : weaknesses) {
            if (this.type1 == weakness || this.type2 == weakness) {
                multiplier *= 2;
            }
        }
		return multiplier;
	}
	
	public boolean isGrounded() {
		return isGrounded(field);
	}

	public boolean isGrounded(Field field) {
		boolean result = true;
		if (this.isType(PType.FLYING)) result = false;
		if (this.getItem() == Item.AIR_BALLOON) result = false;
		if (this.ability == Ability.LEVITATE) result = false;
		if (this.magCount > 0) result = false;
		if (this.hasStatus(Status.SMACK_DOWN)) result = true;
		if (this.getItem() == Item.IRON_BALL) result = true;
		if (field.contains(field.fieldEffects, Effect.GRAVITY)) result = true;
		return result;
	}
	
	public boolean hasStatus(Status status) {
		for (StatusEffect se : this.vStatuses) {
			if (se.status == status) return true;
		}
		return false;
	}
	
	public void addStatus(Status status) {
		addStatus(status, 0);
	}
	
	public void addStatus(Status status, int num) {
		this.vStatuses.add(new StatusEffect(status, num));
	}
	
	public void incrementStatus(Status status, int num) {
		StatusEffect se = this.getStatus(status);
		if (se == null) {
			addStatus(status, num);
		} else {
			se.num += num;
		}
	}
	
	public boolean removeStatus(Status status) {
		boolean worked = false;
		ArrayList<StatusEffect> result = new ArrayList<>(this.vStatuses);
		for (int i = 0; i < this.vStatuses.size(); i++) {
			StatusEffect se = this.vStatuses.get(i);
			if (se.status == status) {
				result.remove(i);
				worked = true;
			}
		}
		this.vStatuses = result;
		return worked;
	}
	
	public StatusEffect getStatus(Status status) {
		for (StatusEffect se : this.vStatuses) {
			if (se.status == status) return se;
		}
		return null;
	}
	
	public int getStatusNum(Status status) {
		StatusEffect se = getStatus(status);
		return se == null ? 0 : se.num;
	}
	
	public void clearStatuses() {
	    vStatuses.removeIf(effect -> effect.status != Status.ARCANE_SPELL);
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
        for (int i = 0; i < movebanks[id - 1].length; i++) {
        	Node n = getNode(id, i);
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
		return getCatchRate(id);
	}
	
	public static int getCatchRate(int id) {
		return catch_rates[id - 1];
	}
	
	public double calcWeight() {
		return this.getItem() == Item.FLOAT_STONE ? 0.1 : this.weight;
	}

	public int getEvolved(Item item) {
		if (item == Item.LEAF_STONE) {
			return id + 1;
		} else if (item == Item.DUSK_STONE) {
			return id + 1;
		} else if (item == Item.DAWN_STONE) {
			return id + 1;
		} else if (item == Item.ICE_STONE) {
			return id + 1;
		} else if (item == Item.VALIANT_GEM) {
			if (id == 86) {
				return id + 2;
			} else {
				return id + 1;
			}
		} else if (item == Item.PETTICOAT_GEM) {
			return id + 2;
		} else if (item == Item.FIRE_STONE) {
			return id + 1;
		} else if (item == Item.WATER_STONE) {
			return id + 1;
		} else if (item == Item.RAZOR_CLAW) {
			return id + 1;
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
	
	public ArrayList<Move> getValidMoveset() {
		ArrayList<Move> validMoves = new ArrayList<>();
		for (Moveslot moveslot : moveset) {
			if (moveslot != null) {
				Move m = moveslot.move;
				if ((hasStatus(Status.TORMENTED) && m == this.lastMoveUsed) || (hasStatus(Status.MUTE) && Move.getSound().contains(m)) || (moveslot.currentPP == 0) 
						|| (this.getItem() != null && this.item.isChoiceItem() && this.lastMoveUsed != null && this.lastMoveUsed != m) || (this.getItem() == Item.ASSAULT_VEST && m.cat == 2)
						|| (this.hasStatus(Status.LOCKED) && m != this.lastMoveUsed) || (this.hasStatus(Status.ENCORED) && m != this.lastMoveUsed)
						|| (this.hasStatus(Status.CHARGING) && m != this.lastMoveUsed) || (this.hasStatus(Status.SEMI_INV) && m != this.lastMoveUsed)
						|| hasStatus(Status.HEAL_BLOCK) && m.isHealing() || m == this.disabledMove) {
	            	// nothing: don't add
	            } else {
	            	validMoves.add(m);
	            }
			}
		}
		
		if (hasStatus(Status.TAUNTED)) validMoves.removeIf(mo -> mo != null && mo.cat == 2 && mo != Move.METRONOME);
		
		return validMoves;
	}
	
	public ArrayList<Move> getDamagingMoveset() {
		ArrayList<Move> vMoveset = getValidMoveset();
		ArrayList<Move> result = new ArrayList<>();
		
		for (Move m : vMoveset) {
			if (m.isAttack()) {
				result.add(m);
			}
		}
		return result;
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
	    clonedPokemon.nat = this.nat;
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
	    clonedPokemon.moveset = new Moveslot[this.moveset.length];
	    for (int i = 0; i < this.moveset.length; i++) {
	    	if (this.moveset[i] != null) clonedPokemon.moveset[i] = this.moveset[i].clone();
	    }
	    
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
		clonedPokemon.ball = this.ball;
	    
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
	    clonedPokemon.disabledCount = this.disabledCount;
	    clonedPokemon.tauntCount = this.tauntCount;
	    clonedPokemon.tormentCount = this.tormentCount;
	    clonedPokemon.healBlockCount = this.healBlockCount;
	    clonedPokemon.toxic = this.toxic;
	    clonedPokemon.headbuttCrit = this.headbuttCrit;
	    clonedPokemon.tailCrit = this.tailCrit;
	    clonedPokemon.spaceEat = this.spaceEat;
	    clonedPokemon.happinessCap = this.happinessCap;
	    
	    // Clone boolean fields
	    clonedPokemon.shiny = this.shiny;
	    clonedPokemon.impressive = this.impressive;
	    clonedPokemon.battled = this.battled;
	    clonedPokemon.success = this.success;
	    clonedPokemon.consumedItem = this.consumedItem;
	    clonedPokemon.illusion = this.illusion;
	    
	    // Clone battle fields
	    clonedPokemon.lastMoveUsed = this.lastMoveUsed;
	    clonedPokemon.disabledMove = this.disabledMove;
	    clonedPokemon.slot = this.slot;

	    clonedPokemon.trainer = this.trainer;
	    
	    // Clone Image fields
	    clonedPokemon.sprite = this.sprite;
	    clonedPokemon.frontSprite = this.frontSprite;
	    clonedPokemon.backSprite = this.backSprite;
	    clonedPokemon.miniSprite = this.miniSprite;
	    
	    clonedPokemon.role = this.role;
	    
	    // Trainer
	    //clonedPokemon.trainer = this.trainer.clone();
	    
	    return clonedPokemon;
	}

	public static String getStatType(int i, boolean big) {
		String type = "";
		switch (i) {
	    	case 0:
	    		type = "HP ";
	    		break;
	    	case 1:
	    		type = big ? "Attack" : "Atk ";
	    		break;
	    	case 2:
	    		type = big ? "Defense" : "Def ";
	    		break;
	    	case 3:
	    		type = big ? "Special Attack" : "SpA ";
	    		break;
	    	case 4:
	    		type = big ? "Special Defense" : "SpD ";
	    		break;
	    	case 5:
	    		type = big ? "Speed" : "Spe ";
	    		break;
	    	case 6:
	    		type = big ? "Accuracy" : "Acc ";
	    		break;
	    	case 7:
	    		type = big ? "Evasion" : "Eva ";
	    		break;
			default:
				type = "ERROR ";
				break;
		}
		return type;
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
	
	public static String getEvolveString(int id) {
		switch (id) {
		case 1: return "Twigle -> Torrged (lv. 18)";
		case 2: return "Torrged -> Tortugis (lv. 36)";
		case 4: return "Lagma -> Maguide (lv. 16)";
		case 5: return "Maguide -> Magron (lv. 36)";
		case 7: return "Lizish -> Iguaton (lv. 17)";
		case 8: return "Iguaton -> Dragave (lv. 36)";
		case 10: return "Hummingspark -> Flashclaw (lv. 18)";
		case 11: return "Flashclaw -> Majestiflash (lv. 36)";
		case 13: return "Pigo -> Pigonat (lv. 17)";
		case 14: return "Pigonat -> Pigoga (lv. 32)";
		case 16: return "Hammo -> HammyBoy (Knows Bulk Up)";
		case 17: return "HammyBoy -> Hamthorno (lv. in Shadow Ravine Heart)";
		case 19: return "Sheabear -> Dualbear (lv. 20)";
		case 20: return "Dualbear -> Spacebear (lv. 40)";
		case 22: return "Bealtle -> Centatle (lv. 18)";
		case 23: return "Centatle -> Curlatoral (lv. 32)\nCentatle -> Millistone (lv. up in Shadow Ravine)";
		case 26: return "Sapwin -> Treewin (lv. 28)";
		case 27: return "Treewin -> Winagrow (Leaf Stone)";
		case 29: return "Budew -> Roselia (160+ happiness)";
		case 30: return "Roselia -> Roserade (Dawn Stone)";
		case 32: return "Sewaddle -> Swadloon (lv. 20)";
		case 33: return "Swadloon -> Leavanny (160+ happiness)";
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
		case 59: return "Kleinowl -> Hootowl (160+ happiness)";
		case 62: return "Snom -> Frosmoth (Ice Stone)";
		case 64: return "Grondor -> Bipedice (Ice Stone)";
		case 66: return "Tricerpup -> Tricercil (lv. 48)";
		case 68: return "Spheal -> Sealeo (lv. 32)";
		case 69: return "Sealeo -> Walrein (lv. 44)";
		case 71: return "Froshrog -> Bouncerog (lv. 32)";
		case 73: return "Bugop -> Opwing (lv. 30)";
		case 75: return "Hatenna -> Hattrem (lv. 32)";
		case 76: return "Hattrem -> Hatterene (lv. 42)";
		case 78: return "Otterpor -> Psylotter (lv. 35)";
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
		case 102: return "Magwoo -> Lafloo (lv. 42)";
		case 104: return "Houndour -> Houndoom (lv. 24)";
		case 106: return "Sparkdust -> Splame (lv. 25)";
		case 108: return "Sparkitten -> Fireblion (Valiant Gem)\nSparkitten -> Flamebless (Petticoat Gem)";
		case 111: return "Shookwat -> Wattwo (lv. 30)";
		case 112: return "Wattwo -> Megawatt (lv. up in Electric Tunnel)";
		case 114: return "Elelamb -> Electroram (lv. 28)";
		case 115: return "Electroram -> Superchargo (lv. 48)";
		case 117: return "Twigzap -> Shockbranch (lv. 19)";
		case 118: return "Shockbranch -> Thunderzap (Leaf Stone)";
		case 120: return "Magie -> Cumin (lv. 30)";
		case 121: return "Cumin -> Cinneroph (250+ happiness)";
		case 123: return "Vupp -> Vinnie (lv. 30)";
		case 124: return "Vinnie -> Suvinero (250+ happiness)";
		case 126: return "Whiskie -> Whiskers (lv. 30)";
		case 127: return "Whiskers -> Whiskeroar (250+ happiness)";
		case 129: return "Nincada -> Ninjask (lv. 20)\n+ Shedinja";
		case 132: return "Sheltor -> Shelnado (Water Stone)";
		case 134: return "Lilyray -> Daray (160+ happiness)";
		case 135: return "Daray -> Spinaquata (lv. 30)";
		case 137: return "Magikarp -> Gyarados (lv. 20)";
		case 139: return "Staryu -> Starmie (Water Stone)";
		case 141: return "Ali -> Batorali (Dusk Stone)";
		case 143: return "Posho -> Shomp (250+ happiness)";
		case 144: return "Shomp -> Poshorump (lv. up in Mindagan Lake)";
		case 146: return "Binacle -> Barbaracle (lv. 39)";
		case 148: return "Durfish -> Dompster (Water Stone)";
		case 151: return "Ekans -> Arbok (lv. 22)";
		case 153: return "Zubat -> Golbat (lv. 22)";
		case 154: return "Golbat -> Crobat (160+ happiness)";
		case 156: return "Poof -> Hast (lv. 32)";
		case 158: return "Poov -> Grust (lv. 30)";
		case 160: return "Cluuz -> Zurrclu (Dusk Stone)";
		case 161: return "Zurrclu -> Zurroaratr (250+ happiness)";
		case 163: return "Timburr -> Gurdurr (lv. 25)";
		case 164: return "Gurdurr -> Conkeldurr (lv. 36)";
		case 166: return "Rhypo -> Rhynee (lv. 30)";
		case 167: return "Rhynee -> Rhypolar (lv. 50)";
		case 169: return "Diggie -> Drillatron (lv. 33)";
		case 171: return "Wormite -> Wormbot (lv. 20)";
		case 172: return "Wormbot -> Wormatron (lv. 48)";
		case 174: return "Cleffa -> Clefairy (160+ happiness)";
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
		case 195: return "Rockmite -> Stellarock (lv. in Shadow Ravine Heart)";
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
		case 221: return "Zurrclu-S -> Zurroaratr-S (250+ happiness)";
		case 223: return "Iglite-S -> Blaxer-S (lv. 16)";
		case 224: return "Blaxer-S -> Pyrator-S (lv. 36)";
		case 226: return "Ekans-S -> Arbok-S (lv. 32)";
		case 238: return "Scraggy -> Scrafty (lv. 39)";
		case 239: return "Scrafty -> Scraftagon\n(5+ Headbutt Crits in Trainer Battles)";
		case 241: return "Glimmet -> Glimmora (lv. 35)";
		case 243: return "Abra -> Kadabra (lv. 16)";
		case 244: return "Kadabra -> Alakazam (lv. 36)";
		case 247: return "Sneasel -> Weavile (Razor Claw)";
		case 249: return "Sneasel-H -> Sneasler (Razor Claw)";
		case 251: return "Solosis -> Duosion (lv. 32)";
		case 252: return "Duosion -> Reuniclus (lv. 41)";
		case 254: return "Solosis-X -> Duosion-X (lv. 32)";
		case 255: return "Duosion-X -> Reuniclus-X (lv. 41)";
		case 257: return "Seviper -> Hissimitar\n(5+ Tail Move Crits in Trainer Battles)";
		case 259: return "Gulpin -> Swalot (lv. 26)";
		case 261: return "Gulpin-X -> Swalot-X\n(25+ Galactic Moves Eaten in Trainer Battles)";
		case 263: return "Plasamp -> Genieova ()";
		case 265: return "Elgyem -> Beheeyem (lv. 42)";
		case 267: return "Elgyem-E -> Beheeyem-E (lv. 42)";
		case 269: return "Caterpie -> Metapod-X (lv. 7)";
		case 270: return "Metapod -> Butterfree (lv. 10)";
		case 272: return "Metapod-X -> Butterfree-X (lv. 10)";
		case 274: return "Bronzor -> Bronzong (lv. 33)";
		case 276: return "Bronzor-X -> Bronzong-X (lv. 33)";
		case 278: return "Capsakid -> Scovillain (Fire Stone)";
		case 279: return "Scovillain -> Kerbernero (lv. 50)";
		case 281: return "Capsakid-S -> Scovillain-S (Fire Stone)";
		case 282: return "Scovillain-S -> Kerbernero-S (lv. 50)";
		case 292: return "Azurill -> Marill (160+ happiness)";
		case 293: return "Marill -> Azumarill (lv. 18)";
		case 295: return "Shroodle -> Grafaiai (lv. 28)";
		default:
			return null;
		}
	} 

	public boolean isTrapped(Pokemon foe) {
		if (this.hasStatus(Status.CHARGING) || this.hasStatus(Status.RECHARGE) || this.hasStatus(Status.LOCKED) || this.hasStatus(Status.SEMI_INV)) {
			return true;
		}
		if (this.isType(PType.GHOST) || this.getItem() == Item.SHED_SHELL) return false;
		if (this.hasStatus(Status.SPUN) || this.hasStatus(Status.NO_SWITCH) || this.hasStatus(Status.TRAPPED) || this.hasStatus(Status.SPELLBIND)) {
			return true;
		}
		if (foe.ability == Ability.ILLUSION && foe.illusion) return true;
		if (foe.ability == Ability.SHADOW_TAG) return true;
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
				Task.addTask(Task.TEXT, this.nickname + "'s Quick Claw let it act first!");
			}
		}
		return result;
	}

	public int checkCustap(int priority, Pokemon foe) {
		int result = priority;
		if (item == Item.CUSTAP_BERRY && currentHP <= getStat(0) * 1.0 / 4) {
			result++;
			Task.addTask(Task.TEXT, this.nickname + " ate its Custap Berry and could act first!");
			consumeItem(foe);
		}
		return result;
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
			return gp.player.p;
			//throw new IllegalStateException("calling object is not player owned");
		}
	}
	
	public boolean getCapture(Pokemon foe, Entry ball, char encType) {
		Random rand = new Random();
        double ballBonus = 0;
        int catchR = foe.catchRate;
        
        Item ballType = ball.getItem();
        this.getPlayer().bag.remove(ball.getItem());
        
        switch (ballType) {
        case POKEBALL:
        case PREMIER_BALL:
        case CHERISH_BALL:
        case HEAL_BALL:
        case LUXURY_BALL:
        case FRIEND_BALL:
        	ballBonus = 1;
        	break;
        case GREAT_BALL:
        	ballBonus = 1.5;
        	break;
        case ULTRA_BALL:
        	ballBonus = 2;
        	break;
        case MASTER_BALL:
        	return true;
        case BEAST_BALL:
        	ballBonus = foe.isUltraBeast() ? 5 : 0.1;
        case QUICK_BALL:
        	ballBonus = Pokemon.field.turns == 0 ? 5 : 1;
        	break;
        case TIMER_BALL:
        	ballBonus = Math.min(1 + (0.3 * Pokemon.field.turns), 4);
        	break;
        case REPEAT_BALL:
        	ballBonus = this.getPlayer().pokedex[foe.id] == 2 ? 3.5 : 1;
        	break;
        case NEST_BALL:
        	ballBonus = 8 - (0.2 * (foe.level - 1));
        	break;
        case DUSK_BALL:
        	ballBonus = gp.tileM.isCave[gp.currentMap] ? 3 : 1;
        	break;
        case DIVE_BALL:
        	ballBonus = encType == 'F' || encType == 'S' ? 3.5 : 1;
        	break;
        case NET_BALL:
        	ballBonus = foe.isType(PType.WATER) || foe.isType(PType.BUG) ? 3.5 : 1;
        	break;
        case HEAVY_BALL:
        	ballBonus = 1;
        	if (foe.weight < 451.1) {
        		catchR += -20;
        	} else if (foe.weight >= 451.1 && foe.weight < 677.3) {
        		catchR += 20;
        	} else if (foe.weight >= 677.3 && foe.weight < 903) {
        		catchR += 30;
        	} else {
        		catchR += 40;
        	}
        	break;
        case LURE_BALL:
        	ballBonus = encType == 'F' ? 5 : 1;
        	break;
        case LOVE_BALL:
        	ballBonus = this.id == foe.id ? 8 : 2; // eventually change to being same species -> then same egg group -> then 1
        	break;
        case LEVEL_BALL:
        	if (this.level < foe.level) {
        		ballBonus = 1;
        	} else if (this.level == foe.level) {
        		ballBonus = 1.5;
        	} else if (this.level > foe.level && this.level / 2 <= foe.level) {
        		ballBonus = 2;
        	} else if (this.level / 2 > foe.level && this.level / 4 <= foe.level) {
        		ballBonus = 4;
        	} else {
        		ballBonus = 8;
        	}
        	break;
        case FAST_BALL:
        	ballBonus = foe.getBaseStat(5) >= 100 ? 4 : 1;
        	break;
        case MOON_BALL:
        	ballBonus = (foe.canUseItem(Item.DAWN_STONE) == 1 || foe.canUseItem(Item.DUSK_STONE) == 1) ? 4 : 1;
        	break;
        case DREAM_BALL:
        	ballBonus = foe.status == Status.ASLEEP ? 4 : 1;
        	break;
		default:
			break;
        
        }
        
        int quotient = 3 * foe.getStat(0) - 2 * foe.currentHP;
        double modQuotient = quotient * catchR * ballBonus;
        double catchRate = modQuotient / (3 * foe.getStat(0));
        
        double statusBonus = 1;
        if (foe.status != Status.HEALTHY) statusBonus = 1.5;
        if (foe.status == Status.ASLEEP) statusBonus = 2;
        
        catchRate *= statusBonus;
        int modifiedCatchRate = (int) Math.round(catchRate);
        
        int randomValue = rand.nextInt(255);
        
        gp.battleUI.setupBalls();
        
        return randomValue <= modifiedCatchRate;
	}

	private boolean isUltraBeast() {
		return (this.id >= 284 && this.id <= 291);
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
		} else if (item.isMint()) {
			return this.nat == item.getNature() ? 0 : 1;
		}
		switch (item) {
		case ABILITY_CAPSULE:
			if (abilitySlot == 2) return 2;
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
		for (StatusEffect s : vStatuses) {
			String add = s.toString();
			if (s.num != 0) add += " " + s.num;
	    	result.add(add);
	    }
		if (magCount > 0) {
			result.add("Magnet Rise");
		}
		if (perishCount > 0) {
			result.add("Perish in " + perishCount);
		}
		if (illusion) {
			result.add("Illusion");
		}
		if (disabledCount > 0) {
			result.add(disabledMove.toString());
			result.add("Disabled for " + disabledCount);
		}
		return result;
	}
	
	public int getHighestStat() {
		int max = this.getStat(1);
		int result = 1;
		for (int i = result + 1; i < this.stats.length; i++) {
			if (this.getStat(i) > max) {
				max = this.stats[i];
				result = i;
			}
		}
		
		return --result;
	}
	
	public void update() {
		baseStats = getBaseStats();
		if (nickname.equals(name())) nickname = getName();
		name = getName();
		setAbility();
		setTypes();
		setSprites();
		setAbility();
		if (this.nat == null) {
			double[] natureCheck = new double[5];
			for (int i = 0; i < natureCheck.length; i++) {
				natureCheck[i] = nature[i];
			}
			this.nat = Nature.getByStats(natureCheck);
		}
		if (this.ball == null) {
			this.ball = Item.POKEBALL;
		}
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
        try (Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/pokemon.csv"))) {
			for (int i = 0; i < MAX_POKEMON; i++) {
				String line = scanner.nextLine();
				String[] tokens = line.split("\\|");
				dexNos[i] = Integer.parseInt(tokens[0].trim());
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
    }
	
	public static void readMovebanksFromCSV() {
		try (Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/movebank.csv"))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.length() > 0 && line.charAt(0) == '=') {
					line = scanner.nextLine();
					int id = Integer.parseInt(line);
					scanner.nextLine();
					while (scanner.hasNextLine()) {
						line = scanner.nextLine();
						if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
							String[] moveLine = line.split("\\|");
							if (moveLine.length < 2) {
								System.out.println(id + " has an invalid move slot");
								continue;
							}
							int level = Integer.parseInt(moveLine[0].trim());
							Move move = Move.valueOf(moveLine[1]);
							setupNode(id, level, move);
						} else {
							break;
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void readTMsFromCSV() {
		try (Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/tms.csv"))) {
			int row = 0;
			while (row < MAX_POKEMON) {
				String line = scanner.nextLine();
				String[] parts = line.split(" ");
				int col = 0;
				for (int i = 0; i < parts[1].length(); i++) {
					tms[row][col++] = parts[1].charAt(i) == '1';
				}
				for (int i = 0; i < parts[0].length(); i++) {
					tms[row][col++] = parts[0].charAt(i) == '1';
				}
				row++;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void readTrainersFromCSV() {
		Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/trainers.csv"));
		
		// skip header
		if (scanner.hasNextLine()) {
	        scanner.nextLine();
	    }
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			int teamDataIndex = line.indexOf('<');
			String trainerData = line.substring(0, teamDataIndex - 1);
			String teamData = line.substring(teamDataIndex);
			
			String[] parts = trainerData.split("\\|");
			String indexS = parts[0];
			boolean staticEnc = false;
			boolean update = false;
			if (indexS.contains("*")) {
				staticEnc = true;
				update = true;
				indexS = indexS.replace("*", "");
			}
			if (indexS.contains("+")) {
				update = true;
				indexS = indexS.replace("+", "");
			}
			int index = Integer.parseInt(indexS);
			String name = parts[1];
			int money = Integer.parseInt(parts[2]);
			int flagIndex = Integer.parseInt(parts[3]);
			Item item = parts[4].equals("null") ? null : Item.valueOf(parts[4]);
			
			// pokemon team
			String[] pokemonStrings = teamData.split("><");
			Pokemon[] team = new Pokemon[pokemonStrings.length];
			Item[] items = new Item[pokemonStrings.length];
			
			for (int i = 0; i < pokemonStrings.length; i++) {
				String pokemonString = pokemonStrings[i].replace("<", "").replace(">", "");
				String[] pokemonParts = pokemonString.split("\\|");
				
				int id = Integer.parseInt(pokemonParts[0]);
				int level = Integer.parseInt(pokemonParts[1]);
				Ability ability = Ability.valueOf(pokemonParts[2]);
				Item heldItem = pokemonParts[3].equals("null") ? null : Item.valueOf(pokemonParts[3]);
				
				String[] moveStrings = pokemonParts[4].split(",");
				Moveslot[] moves = new Moveslot[4];
				PType type = null;
				for (int j = 0; j < 4; j++) {
					String moveString = j >= moveStrings.length ? "" : moveStrings[j];
					if (moveString.contains("HIDDEN_POWER")) {
						if (moveString.length() > 12) {
							type = PType.valueOf(moveString.substring(13));
						} else {
							type = PType.GALACTIC;
						}
						moves[j] = new Moveslot(Move.HIDDEN_POWER);
					} else if (moveString.contains("RETURN")) {
						if (moveString.length() > 6) {
							type = PType.valueOf(moveString.substring(7));
						} else {
							type = PType.GALACTIC;
						}
						moves[j] = new Moveslot(Move.RETURN);
					} else {
						moves[j] = moveString.isEmpty() ? null : new Moveslot(Move.valueOf(moveString));
					}
					
				}
				Pokemon pokemon = staticEnc ? new Pokemon(id, level, true) : new Pokemon(id, level, false, true);				
				pokemon.moveset = moves;
				pokemon.validateMoveset(index, name);
				
				if (type != null) {
					pokemon.ivs = Pokemon.determineOptimalIVs(type);
				}
				
				boolean abilitySet = false;
				
				for (int k = 0; k < 3; k++) {
					Ability slot = pokemon.getAbility(k);
					if (ability == slot) {
						pokemon.abilitySlot = k;
						pokemon.setAbility();
						abilitySet = true;
						break;
					}
				}
				
				pokemon.role = RoleAssignable.assignRole(pokemon);
				
				// DEBUGGING
				if (!abilitySet) {
					System.err.println("Trainer " + name + " at index " + index + " has an illegal ability on mon " + pokemon.getName() + " in slot " + i + ": " + ability);
					pokemon.abilitySlot = 0;
					pokemon.setAbility();
				}
				
				team[i] = pokemon;
				items[i] = heldItem;
			}
			
			Trainer trainer = new Trainer(name, team, items, money, item, flagIndex);
			if (Trainer.trainers[index] != null) {
				scanner.close();
				throw new IllegalStateException(Trainer.trainers[index].getName() + " already occupies index " + index + ", change " + trainer.getName() + "'s index");
			}
			Trainer.trainers[index] = trainer;
			trainer.update = update;
			
			if (name.contains("Scott") || name.contains("Fred")) {
				rivalIndices.add(index);
			}
			
			if (Trainer.bosses.stream().anyMatch(name::contains)) {
				Trainer.bossTrainers.add(trainer);
			}
		}
		
		updateRivals();
		
		scanner.close();
	}

	private void validateMoveset(int index, String name) {
		ArrayList<Move> thisMovebank = Pokemon.getMovebankAtLevel(this.id, this.level);
		ArrayList<Move> tmMoves = Item.getTMMoves();
		ArrayList<Item> tms = Item.getTMs();
		boolean[] movesValid = new boolean[this.moveset.length];
		for (int i = 0; i < this.moveset.length; i++) {
			if (this.moveset[i] == null) {
				movesValid[i] = true;
				continue;
			}
			Move m = this.moveset[i].move;
			int tmIndex = tmMoves.indexOf(m);
			if (tmIndex >= 0) {
				Item tmItem = tms.get(tmIndex);
				if (tmItem.getLearned(this)) {
					movesValid[i] = true;
					continue;
				}
			}
			if (thisMovebank.contains(m)) movesValid[i] = true;
		}
		
		boolean movesetValid = true;
		for (boolean b : movesValid) {
			if (!b) {
				movesetValid = false;
				break;
			}
		}
		int pokemonChecked = 1;
		int prevoID = this.id;
		while (!movesetValid && pokemonChecked < 3) { // check prevos
			prevoID--;
			String evolvedString = Pokemon.getEvolveString(prevoID);
			if (evolvedString != null && (evolvedString.contains(Pokemon.getName(this.id)) || evolvedString.contains(Pokemon.getName(this.id - 1)))) {
				ArrayList<Move> prevoMovebank = Pokemon.getMovebankAtLevel(prevoID, this.level);
				for (int i = 0; i < this.moveset.length; i++) {
					if (!movesValid[i]) {
						Move m = this.moveset[i].move;
						if (prevoMovebank.contains(m)) movesValid[i] = true;
					}
				}
			}
			pokemonChecked++;
			for (boolean b : movesValid) {
				if (!b) {
					movesetValid = false;
					break;
				}
			}
		}
		
		if (!movesetValid) {
			for (int i = 0; i < movesValid.length; i++) {
				if (!movesValid[i]) {
					System.err.println(index + " " + name + "'s " + this.name + " (ID " + this.id + ")'s " + this.moveset[i].move.toString()
							+ " in index " + i + " is illegal");
				}
			}
		}
		
		
	}

	private static ArrayList<Move> getMovebankAtLevel(int id, int level) {
		ArrayList<Move> forgottenMoves = new ArrayList<>();
		Node[] movebank = Pokemon.getMovebank(id);
        for (int i = 0; i <= level; i++) {
        	if (i < movebank.length) {
        		Node move = movebank[i];
        		while (move != null) {
        			if (!forgottenMoves.contains(move.data)) {
        				forgottenMoves.add(move.data);
        			}
        			move = move.next;
        		}
        	}
        }
		return forgottenMoves;
	}

	public static void updateRivals() {
		if (gp == null || gp.player.p.starter == -1) return;
		
		Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
		ArrayList<Item> itemList = new ArrayList<>(Arrays.asList(items));
		
		Item scottItem = items[gp.player.p.starter];
		
		itemList.remove(scottItem);
		itemList.remove(items[gp.player.p.secondStarter]);
		
		Item fredItem = itemList.remove(0);
		
		if (!itemList.isEmpty()) throw new IllegalStateException("itemList isn't empty, some item overlap happened");
		
		for (Integer i : rivalIndices) {
			Trainer t = Trainer.trainers[i];
			int newLength = t.team.length - 2;
			int twigleIndex = -1;
			int index = 0;
			for (Pokemon p : t.team) {
				if (p.id >= 1 && p.id <= 3) {
					twigleIndex = index;
				}
				index++;
			}
			int starterIndex = t.getName().contains("Scott") ? ((gp.player.p.starter - 1) + 3) % 3 : t.getName().contains("Fred") ? (gp.player.p.starter + 1) % 3 : -1;
			if (starterIndex == -1) System.err.println("Something went wrong with updating the rivals");
			Pokemon[] team = new Pokemon[newLength];
			
			int j;
			for (j = 0; j < twigleIndex; j++) {
				team[j] = t.team[j];
			}
			team[j++] = t.team[twigleIndex + starterIndex];
			for (int k = j + 2; k < t.team.length; k++) {
				team[j] = t.team[k];
				j++;
			}
			t.team = team;
			t.current = t.team[0];
			if (t.item == Item.CALCULATOR) {
				if (t.getName().contains("Scott")) {
					t.item = scottItem;
				} else if (t.getName().contains("Fred")) {
					t.item = fredItem;
				} else {
					throw new IllegalStateException(t.getName() + " gives a Calculator, that is reserved exclusively as a placeholder for Scott and Fred first battles for starter type boosting items.");
				}
			}
		}
	}
	
	public static void readEntiresFromCSV() {
        try (Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/entries.csv"))) {
			for (int i = 0; i < MAX_POKEMON; i++) {
				try {
					String line = scanner.nextLine();
			    	entries[i] = line.replace("#", getName(i + 1));
				} catch (NoSuchElementException e) {
					System.out.println(i);
					e.printStackTrace();
				}
			}
		}
    }
	
	public static void readEncountersFromCSV() {
		try (Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/encounters.csv"))) {
			String currentKey = null;
			ArrayList<Encounter> currentList = null;
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.length() > 0 && line.contains("|")) {
					if (currentKey != null && currentList != null) {
						Encounter.encounters.put(currentKey, currentList);
					}
					currentKey = line;
					currentList = new ArrayList<>();
				} else if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
					String[] parts = line.split("\\[");
					int id = Integer.parseInt(parts[0].trim());
					String[] rest = parts[1].split("\\]");
					String[] levels = rest[0].split(",");
					
					int minLevel = Integer.parseInt(levels[0].trim());
					int maxLevel = Integer.parseInt(levels[1].trim());
					int chance = Integer.parseInt(rest[1].replace("%", "").trim());
					double c = chance * 1.0 / 100;
					
					currentList.add(new Encounter(id, minLevel, maxLevel, c));
				} else if (line.startsWith("=")) {
					continue;
				}
			}
			
			if (currentKey != null && currentList != null) {
				Encounter.encounters.put(currentKey, currentList);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void setupNode(int id, int level, Move move) {
		Node[] movebank = getMovebank(id);
		if (movebank[level] == null) {
			movebank[level] = new Node(move);
		} else {
			movebank[level].addToEnd(new Node(move));
		}
	}
	
	public String getFormattedDexNo() {
		return this instanceof Egg ? "#???" : getFormattedDexNo(this.getDexNo());
	}

	public static String getFormattedDexNo(int n) {
		if (n == 0) return "#---";
		String result = Math.abs(n) + "";
		while (result.length() < 3) result = "0" + result;
		result = "#" + result;
		return result;
	}

	public Color getDexNoColor() {
		if (this instanceof Egg) return Color.white;
		int dexSec = getDexSection(id);
		switch (dexSec) {
		case 0:
			return Color.white;
		case 1:
			return new Color(82, 82, 82);
		case 2:
			return new Color(214, 185, 60);
		case 3:
			return new Color(134, 184, 118);
		default:
			return Color.black;
		}
	}
	
	public static int getDexSection(int id) {
		int dexNo = getDexNo(id);
		if (dexNo >= 0) {
			return 0;
		}
		String name = getName(id);
		if (name.contains("-S") || id == 213 || id == 214) {
			return 1;
		} else if (name.contains("-E") || id == 211 || id == 212) {
			return 2;
		} else {
			return 3;
		}
	}
	
	private int determineHappiness(Player p) {
		return Math.min(this.happiness + (50 * p.badges), 255);
	}
	
	public static Pokemon generateCompetitivePokemon() {
		Random rand = new Random();
		int id;
		boolean sprite = false;
		do {
			id = rand.nextInt(Pokemon.MAX_POKEMON) + 1;
			try {
				ImageIO.read(Pokemon.class.getResourceAsStream("/sprites/" + String.format("%03d", id) + ".png"));
				sprite = true;
			} catch (Exception e) {
				sprite = false;
			}
		} while (Pokemon.getEvolveString(id) != null || !sprite);
		
		Item item;
		do {
			Item[] values = Item.values();
			item = values[rand.nextInt(values.length)];
		} while (item.getPocket() != Item.HELD_ITEM && item.getPocket() != Item.BERRY);
		
		Pokemon result = new Pokemon(id, 100, false, true);
		result.item = item;
		return result;
	}

	public static Pokemon generateCompetitivePokemon(ArrayList<Pokemon> team) {
		Random rand = new Random();
		int id = 0;
		do {
			id = compIDs.get(rand.nextInt(compIDs.size()));
		} while (teamContainsID(team, id));
		
		ArrayList<Set> setOptions = sets.get(id);
		
		return setOptions.get(rand.nextInt(setOptions.size())).makePokemon();
	}

	private static boolean teamContainsID(ArrayList<Pokemon> team, int id) {
		for (Pokemon p : team) {
			if (p.id == id) return true;
		}
		return false;
	}
	
	public static int[] simulateBattle(Trainer trainer1, Trainer trainer2) {
		int[] result = new int[2];
		field = new Field();
		for (int i = 0; i < 10; i++) {
			Trainer t1 = trainer1.clone();
			Trainer t2 = trainer2.clone();
			field.clear(t1, t2);
			
			while (!t1.wiped() && !t2.wiped()) {
				Pokemon p1 = t1.getCurrent();
				Pokemon p2 = t2.getCurrent();
				boolean fFaster = p1.getFaster(p2, 0, 0) == p2;
				
				Move uMove = p1.bestMove(p2, !fFaster);
				Move fMove = p2.bestMove(p1, fFaster);
				
				int uP, fP;
				uP = uMove == null ? 0 : uMove.getPriority(p1);
				fP = fMove == null ? 0 : fMove.getPriority(p2);
				if (uMove != null && p1.ability == Ability.PRANKSTER && uMove.cat == 2) ++uP;
				if (fMove != null && p2.ability == Ability.PRANKSTER && fMove.cat == 2) ++fP;
				
				if (uMove != null && uMove.priority < 1 && uMove.hasPriority(p1)) ++uP;
				if (fMove != null && fMove.priority < 1 && fMove.hasPriority(p2)) ++fP;
				
				if (uMove != null && fMove != null && !p1.hasStatus(Status.SWAP)
						&& !p2.hasStatus(Status.SWAP)) {
					uP = p1.checkQuickClaw(uP);
					fP = p2.checkQuickClaw(fP);
				}
				if (uMove != null) uP = p1.checkCustap(uP, p2);
				if (fMove != null) fP = p2.checkCustap(fP, p1);
				
				Pokemon faster;
				Pokemon slower;
				
				if (uMove == null || fMove == null) {
					faster = uMove == null ? p1 : p2;
				} else {
					faster = p1.getFaster(p2, uP, fP);
				}
				
				slower = faster == p1 ? p2 : p1;
				
				Move fastMove = faster == p1 ? uMove : fMove;
				Move slowMove = faster == p1 ? fMove : uMove;
				
				boolean fastCanMove = true;
				boolean slowCanMove = true;
				
				if (faster.hasStatus(Status.SWAP)) {
					faster = faster.trainer.swapOut(slower, fastMove, false, false);
					fastMove = null;
					fastCanMove = false;
				}
				
				if (slower.hasStatus(Status.SWAP)) {
					slower = slower.trainer.swapOut(faster, slowMove, false, false);
					slowMove = null;
					slowCanMove = false;
				}
				
				if (slowMove != null && fastMove == Move.SUCKER_PUNCH && slowMove.cat == 2) fastMove = Move.FAILED_SUCKER;
				
				if (fastCanMove) {
					faster.moveInit(slower, fastMove, true);
					faster = faster.trainer.getCurrent();
					slower = slower.trainer.getCurrent();
				}
				
				// Check for swap
				if (faster.trainer.hasValidMembers() && fastCanMove && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
					faster = faster.trainer.swapOut(slower, null, faster.lastMoveUsed == Move.BATON_PASS, false);
				}
				// Check for swap
				if (slower.trainer.hasValidMembers() && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
					slower = slower.trainer.swapOut(faster, null, false, false);
					slowCanMove = false;
				}
				
		        if (slowCanMove) {
		        	slower.moveInit(faster, slowMove, false);
		        	faster = faster.trainer.getCurrent();
		        	slower = slower.trainer.getCurrent();
		        }
		        
		        // Check for swap
		        if (slower.trainer.hasValidMembers() && slowCanMove && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
		        	slower = slower.trainer.swapOut(faster, null, slower.lastMoveUsed == Move.BATON_PASS, false);
		        }
		    	// Check for swap
		 		if (faster.trainer.hasValidMembers() && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
		 			faster = faster.trainer.swapOut(slower, null, false, false);
		 		}
				
				if (fastMove != null || slowMove != null) {
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) faster.endOfTurn(slower);
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) slower.endOfTurn(faster);
					if (!faster.trainer.wiped() && !slower.trainer.wiped()) field.endOfTurn(faster, slower);
				}
				
				for (int j = 0; j < 2; j++) {
					Pokemon p;
					if (j == 0) {
						p = faster;
					} else {
						p = slower;
					}
					Pokemon foe = p == faster ? slower : faster;
					Pokemon next = p.trainer.getCurrent();
					while (next.isFainted()) {
						if (next.trainer.hasNext()) {
							boolean userSide = !next.trainer.hasUser(p1);
							next = next.trainer.next(foe, userSide);
							Task.addSwapInTask(next, next.currentHP, false);
							next.swapIn(foe, true);
						} else {
				            break;
						}
					}
				}
				
				for (Task t : gp.ui.tasks) {
					System.out.println(t.message);
				}
				System.out.println("--------------------");
				gp.ui.tasks.clear();
			}
			
			if (t1.wiped() && !t2.wiped()) {
				result[1]++;
				System.out.println(t2.name + " defeated " + t1.name + "!");
				System.out.println("========================================");
			}
			if (!t1.wiped() && t2.wiped()) {
				result[0]++;
				System.out.println(t1.name + " defeated " + t2.name + "!");
				System.out.println("========================================");
			}
			if (t1.wiped() && t2.wiped()) {
				System.out.println("Both " + t1.name + " and " + t2.name + " fainted!");
				System.out.println("========================================");
			}
		}
		
		return result;
	}
	
	public ArrayList<FieldEffect> getFieldEffects() {
		if (this.trainer == null) {
			if (this.fieldEffects == null) {
				throw new IllegalStateException(this.nickname + " tried fetching their own field effect list and it's null");
			}
			return this.fieldEffects;
		} else {
			if (this.trainer.getFieldEffectList() == null) {
				this.trainer.initFieldEffectList();
			}
			return this.trainer.getFieldEffectList();
		}
	}
	
	private void setFieldEffects(ArrayList<FieldEffect> fieldEffects) {
		if (this.trainer == null) {
			this.fieldEffects = fieldEffects;
		} else {
			this.trainer.setFieldEffects(fieldEffects);
		}
	}

	public static void loadCompetitiveSets() {
		Scanner scanner = new Scanner(Pokemon.class.getResourceAsStream("/info/sets.txt"));
		
		int currentID = 1;
		boolean newID = true;
		
		while (scanner.hasNextLine()) {
			String nameLine = scanner.nextLine();
			if (nameLine.trim().isEmpty()) continue;
			
			String[] nameParts = nameLine.split(" @ ");
			String name = nameParts[0].trim();
			String itemLine = nameParts.length > 1 ? nameParts[1].trim() : null;
			
			String natureLine = scanner.nextLine();
			String natures = natureLine.split(" ")[0].trim();
			
			String abilityLine = scanner.nextLine();
			String abilities = abilityLine.split(": ")[1].trim();
			
			String[] moves = new String[4];
			for (int i = 0; i < 4; i++) {
				String moveLine = scanner.nextLine();
				moves[i] = moveLine.substring(2).trim();
			}
			
			while (!name.equals(Pokemon.getName(currentID))) {
				currentID++;
				newID = true;
			}
			
			String[] abilityOptions = abilities.split("/");
			Ability[] as = new Ability[abilityOptions.length];
			for (int i = 0; i < abilityOptions.length; i++) {
				as[i] = Ability.getEnum(abilityOptions[i]);
			}
			
			String[] itemOptions = itemLine.split("/");
			Item[] items = new Item[itemOptions.length];
			for (int i = 0; i < itemOptions.length; i++) {
				items[i] = Item.getEnum(itemOptions[i]);
			}
			
			String[] natureOptions = natures.split("/");
			Nature[] ns = new Nature[natureOptions.length];
			for (int i = 0; i < natureOptions.length; i++) {
				ns[i] = Nature.getEnum(natureOptions[i]);
			}
			
			Set set = new Set(currentID);
			
			for (int i = 0; i < moves.length; i++) {
				String[] moveStrings = moves[i].split("/");
				Move[] ms = new Move[moveStrings.length];
				for (int j = 0; j < moveStrings.length; j++) {
					ms[j] = Move.getEnum(moveStrings[j]);
				}
				set.setMoves(i, ms);
			}
			
			set.setAbility(as);
			set.setItems(items);
			set.setNatures(ns);
			
			set.setRole(RoleAssignable.assignRole(set));
			
			if (newID) {
				compIDs.add(currentID);
			}
			
			ArrayList<Set> setOptions = sets.getOrDefault(currentID, new ArrayList<>());
			setOptions.add(set);
			sets.put(currentID, setOptions);
			
			if (scanner.hasNextLine()) scanner.nextLine();
		}
		
		scanner.close();
	}

	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> allMoves = new ArrayList<>();
		for (Moveslot m : this.moveset) {
			if (m != null) {
				allMoves.add(m.move);
			}
		}
		return allMoves;
	}

	@Override
	public ArrayList<Ability> getAbilities() {
		ArrayList<Ability> result = new ArrayList<>();
		result.add(this.ability);
		return result;
	}

	@Override
	public ArrayList<Item> getItems() {
		ArrayList<Item> result = new ArrayList<>();
		result.add(this.item);
		return result;
	}

	@Override
	public ArrayList<Nature> getNatures() {
		ArrayList<Nature> result = new ArrayList<>();
		result.add(this.nat);
		return result;
	}

	public static int getRandomBasePokemon(Random random) {
		int id = 0;
		do {
			id = random.nextInt(Pokemon.MAX_POKEMON) + 1;
		} while (Pokemon.getEvolveString(id - 1) != null || Pokemon.getEvolveString(id - 2) == null || Pokemon.getEvolveString(id - 2).contains(Pokemon.getName(id)) || Pokemon.getCatchRate(id) <= 5);
		
		return id;
	}

	public void shiftMoveset() {
		Moveslot[] newMoveset = new Moveslot[moveset.length];
		int index = 0;
		boolean allNull = true;
		
		for (Moveslot m : moveset) {
			if (m != null) {
				newMoveset[index++] = m;
				allNull = false;
			}
		}
		if (allNull) moveset[0] = new Moveslot(Move.PROTECT);
		
		moveset = newMoveset;
	}

	public void auroraGlow() {
		if (this.isFainted()) return;
		if (!this.isType(PType.GALACTIC) && !this.isType(PType.ICE) && !this.isType(PType.LIGHT)) return;
		if (this.currentHP == this.getStat(0)) return;
		String message = this.nickname + " restored HP from the Aurora Glow!";
		int amt = (int) this.getHPAmount(1.0/8);
		if (this.visible) {
			this.heal(amt, message);
		} else {
			Task.addTask(Task.TEXT, message);
			this.currentHP += amt;
			this.verifyHP();
		}		
	}

	public int takeFutureSight(int stat, int level, int mode, Pokemon foe) {
		ArrayList<Task> tasks = mode == 0 ? gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.simBattleUI.tasks : null;
		int damageIndex = tasks == null ? 0 : tasks.size();
		boolean immune = false;
		
		if (mode == 0) Task.addTask(Task.TEXT, this.nickname + " took the Future Sight attack!");
		Task msgTask = Task.getTask(damageIndex);
		String message = msgTask == null ? "" : msgTask.message;
		
		double attackStat = stat;
		double defenseStat = this.getStat(4);
		defenseStat *= this.asModifier(3);
		if (this.getItem() == Item.ASSAULT_VEST) defenseStat *= 1.5;
		if (field.equals(field.weather, Effect.SANDSTORM, this) && this.isType(PType.ROCK)) defenseStat *= 1.5;
		if (field.contains(this.getFieldEffects(), Effect.LIGHT_SCREEN) || field.contains(this.getFieldEffects(), Effect.AURORA_VEIL)) defenseStat *= 2;
		if (this.getItem() == Item.EVIOLITE && this.canEvolve()) defenseStat *= 1.5;
		
		int damage = this.calc(attackStat, defenseStat, Move.FUTURE_SIGHT.basePower, level, mode);
		damage = Math.max(damage, 1);
		
		double multiplier = this.getEffectiveMultiplier(Move.FUTURE_SIGHT.mtype, Move.FUTURE_SIGHT, null);
		
		if (this.ability == Ability.WONDER_GUARD && multiplier <= 1) {
			if (mode == 0) Task.addAbilityTask(this);
			immune = true; // Check for immunity
		}
		
		if (this.ability == Ability.MOSAIC_WINGS && multiplier == 1) {
			if (mode == 0) {
				Task.addAbilityTask(this);
				Task.addTask(Task.TEXT, this.nickname + " is distorting type matchups!");
				damageIndex += 3;
			}
			multiplier = 0.5;
		}
		
		damage *= multiplier;
		
		if (multiplier == 0) immune = true;
		
		if (immune) {
			if (mode == 0) Task.addTask(Task.TEXT, "It doesn't effect " + this.nickname + "...");
			damage = 0;
		} else {
			if (multiplier > 1) {
				message += "\nIt's super effective!";
				if (mode == 0) field.superEffective++;
				if (this.ability == Ability.SOLID_ROCK || this.ability == Ability.FILTER) damage /= 2;
				if (this.getItem() != null && this.checkTypeResistBerry(Move.FUTURE_SIGHT.mtype)) {
					if (mode == 0) {
						Task.addTask(Task.TEXT, this.nickname + " ate its " + this.item.toString() + " to weaken the attack!");
						this.consumeItem(this);
					}
					damage /= 2;
				}
			}
			if (multiplier < 1) {
				message += "\nIt's not very effective...";
				if (ability == Ability.TINTED_LENS) damage *= 2;
			}
			
			boolean fullHP = this.currentHP == this.getStat(0);
			boolean sturdy = false;
			if (damage >= this.currentHP && (this.hasStatus(Status.ENDURE)
					|| (this.getItem() == Item.FOCUS_BAND && checkSecondary(10))
					|| (fullHP && (this.ability == Ability.STURDY
					|| this.getItem() == Item.FOCUS_SASH)))) {
				sturdy = true;
			}
			
			// Damage foe
			if (mode == 0 && !immune) {
				int dividend = Math.min(damage, this.currentHP);
				if (sturdy) dividend--;
				double percent = dividend * 100.0 / this.getStat(0); // change dividend to damage
				String formattedPercent = String.format("%.1f", percent);
				String damagePercentText = "(" + this.nickname + " lost " + formattedPercent + "% of its HP.)";
				this.damage(damage, this, Move.FUTURE_SIGHT, message, damageIndex, sturdy);
				Task.addTask(Task.TEXT, damagePercentText);
				
				if (this.getItem() == Item.AIR_BALLOON) {
					Task.addTask(Task.TEXT, this.nickname + "'s Air Balloon popped!");
					this.consumeItem(this);
				}
				if (this.ability == Ability.ILLUSION && this.illusion) {
					Task.addTask(Task.TEXT, this.nickname + "'s Illusion was broken!");
					this.illusion = false;
				}
				if (sturdy) {
					this.currentHP = 1;
					if (fullHP && this.ability == Ability.STURDY) Task.addAbilityTask(this);
					if (this.getItem() == Item.FOCUS_SASH) {
						Task.addTask(Task.TEXT, this.nickname + " hung on using its Focus Sash!");
						this.consumeItem(this);
					}
				}
				if (this.currentHP <= 0) { // Check for kill
					this.faint(true, this);
				}
				if (multiplier > 1 && this.isFainted()) {
					if (this.getItem() == Item.WEAKNESS_POLICY) {
						Task.addTask(Task.TEXT, this.nickname + " used its " + this.item.toString() + "!");
						stat(foe, 0, 2, this);
						stat(foe, 2, 2, this);
						this.consumeItem(this);
					}
					if (this.getItem() == Item.ENIGMA_BERRY) {
						this.eatBerry(this.item, true, this);
					}
				}
			}
		}
		
		return damage;
	}

	public void takeWish(int stat) {
		this.heal(stat, this.nickname + "'s wish came true!");
	}
	
}