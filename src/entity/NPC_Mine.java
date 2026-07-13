package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import overworld.GamePanel;
import pokemon.Item;
import pokemon.Player;
import pokemon.Task;
import util.Pair;

public class NPC_Mine extends Entity {
	
	public final int PRICE = 1000;
	private final int FAIL_CHANCE = 15;
	private ArrayList<Pair<Item, Integer>> items = new ArrayList<>();
	private Random random;
	private int totalWeight;
	private final int defaultTotal;
	private int turns;
	
	private static final int MAX_VIEW_DEPTH = 40;
	private int viewDepth = 0;
	private boolean viewPerilyte;
	private boolean chancesDirty;
	
	private LinkedHashMap<Item, Double> chanceBuffer = new LinkedHashMap<>();
	private double failChanceBuffer;
	
	public NPC_Mine(GamePanel gp, String name) {
		super(gp, name);
		this.setDirection("down");
		
		this.random = new Random();
		
		getImage();
		setupItems();
		defaultTotal = totalWeight;
	}
	
	public void getImage() {
		down1 = setup("/npc/explorer1");
		up1 = setup("/npc/explorer2");
		left1 = setup("/npc/explorer3");
		right1 = setup("/npc/explorer4");
	}
	
	private void setupItems() {
		items.add(new Pair<>(null, 16)); // FAIL
	    items.add(new Pair<>(Item.STAR_PIECE, 15));
	    items.add(new Pair<>(Item.STICKY_BARB, 1));
	    items.add(new Pair<>(Item.LIGHT_CLAY, 2));
	    items.add(new Pair<>(Item.LAGGING_TAIL, 1));
	    items.add(new Pair<>(Item.IRON_BALL, 1));
	    items.add(new Pair<>(Item.METAL_COAT, 2));
	    items.add(new Pair<>(Item.SOFT_SAND, 3));
	    items.add(new Pair<>(Item.HARD_STONE, 3));
	    items.add(new Pair<>(Item.FLOAT_STONE, 3));
	    items.add(new Pair<>(Item.DAMP_ROCK, 2));
	    items.add(new Pair<>(Item.HEAT_ROCK, 2));
	    items.add(new Pair<>(Item.SMOOTH_ROCK, 2));
	    items.add(new Pair<>(Item.ICY_ROCK, 2));
	    items.add(new Pair<>(Item.EVERSTONE, 4));
	    items.add(new Pair<>(Item.ICE_STONE, 2));
	    items.add(new Pair<>(Item.DAWN_STONE, 2));
	    items.add(new Pair<>(Item.DUSK_STONE, 2));
	    items.add(new Pair<>(Item.VALIANT_GEM, 2));
	    items.add(new Pair<>(Item.PETTICOAT_GEM, 2));
	    items.add(new Pair<>(Item.LEAF_STONE, 2));
	    items.add(new Pair<>(Item.WATER_STONE, 2));
	    items.add(new Pair<>(Item.FIRE_STONE, 2));
	    items.add(new Pair<>(Item.THUNDER_SCALES_FOSSIL, 3));
	    items.add(new Pair<>(Item.DUSK_SCALES_FOSSIL, 3));
	    items.add(new Pair<>(Item.EUPHORIAN_GEM, 3));
	    items.add(new Pair<>(Item.BOTTLE_CAP, 4));
	    items.add(new Pair<>(Item.RUSTY_BOTTLE_CAP, 4));
	    items.add(new Pair<>(Item.GOLD_BOTTLE_CAP, 2));
	    items.add(new Pair<>(Item.FABLE_CHARGE, 5));
	    items.add(new Pair<>(Item.RELIC_SILVER, 7));
	    items.add(new Pair<>(Item.RELIC_GOLD, 3));
	    items.add(new Pair<>(Item.NUGGET, 8));
	    items.add(new Pair<>(Item.BIG_NUGGET, 4));
	    items.add(new Pair<>(Item.RAZOR_CLAW, 2));
	    
	    items.add(new Pair<>(Item.EVIOLITE, 1));
	    items.add(new Pair<>(Item.BLACK_BELT, 1));
	    items.add(new Pair<>(Item.BLACK_GLASSES, 1));
	    items.add(new Pair<>(Item.CHARCOAL, 1));
	    items.add(new Pair<>(Item.COSMIC_CORE, 3));
	    items.add(new Pair<>(Item.DRAGON_FANG, 1));
	    items.add(new Pair<>(Item.ENCHANTED_AMULET, 2));
	    items.add(new Pair<>(Item.GLOWING_PRISM, 1));
	    items.add(new Pair<>(Item.MAGNET, 1));
	    items.add(new Pair<>(Item.MIRACLE_SEED, 1));
	    items.add(new Pair<>(Item.MYSTIC_WATER, 1));
	    items.add(new Pair<>(Item.NEVER$MELT_ICE, 1));
	    items.add(new Pair<>(Item.POISON_BARB, 1));
	    items.add(new Pair<>(Item.SHARP_BEAK, 1));
	    items.add(new Pair<>(Item.SILK_SCARF, 1));
	    items.add(new Pair<>(Item.SILVER_POWDER, 1));
	    items.add(new Pair<>(Item.SPELL_TAG, 1));
	    items.add(new Pair<>(Item.TWISTED_SPOON, 1));
	    items.add(new Pair<>(Item.TEMPLE_ORB, 5));
	    
	    items.add(new Pair<>(Item.BRIGHT_POWDER, 1));
	    items.add(new Pair<>(Item.EXPERT_BELT, 1));
	    items.add(new Pair<>(Item.METRONOME, 1));
	    items.add(new Pair<>(Item.TERRAIN_EXTENDER, 1));
	    items.add(new Pair<>(Item.ABILITY_SHIELD, 1));
	    items.add(new Pair<>(Item.LIFE_ORB, 1));
	    items.add(new Pair<>(Item.FLAME_ORB, 1));
	    items.add(new Pair<>(Item.TOXIC_ORB, 1));
	    items.add(new Pair<>(Item.FROST_ORB, 1));
	    items.add(new Pair<>(Item.ROCKY_HELMET, 1));
	    items.add(new Pair<>(Item.EXP_SHARE, 1));
	    items.add(new Pair<>(Item.LUCKY_EGG, 1));
	    items.add(new Pair<>(Item.PROTECTIVE_PADS, 1));
	    items.add(new Pair<>(Item.PUNCHING_GLOVE, 1));
	    items.add(new Pair<>(Item.CLEAR_AMULET, 1));
	    
	    items.add(new Pair<>(Item.ROCK_CRYSTAL, 2));
	    items.add(new Pair<>(Item.FIRE_CRYSTAL, 2));
	    items.add(new Pair<>(Item.WATER_CRYSTAL, 2));
	    items.add(new Pair<>(Item.GRASS_CRYSTAL, 2));
	    items.add(new Pair<>(Item.ICE_CRYSTAL, 2));
	    items.add(new Pair<>(Item.ELECTRIC_CRYSTAL, 2));
	    items.add(new Pair<>(Item.FIGHTING_CRYSTAL, 2));
	    items.add(new Pair<>(Item.POISON_CRYSTAL, 2));
	    items.add(new Pair<>(Item.GROUND_CRYSTAL, 2));
	    items.add(new Pair<>(Item.FLYING_CRYSTAL, 2));
	    items.add(new Pair<>(Item.PSYCHIC_CRYSTAL, 2));
	    items.add(new Pair<>(Item.BUG_CRYSTAL, 2));
	    items.add(new Pair<>(Item.GHOST_CRYSTAL, 2));
	    items.add(new Pair<>(Item.DRAGON_CRYSTAL, 2));
	    items.add(new Pair<>(Item.STEEL_CRYSTAL, 2));
	    items.add(new Pair<>(Item.DARK_CRYSTAL, 2));
	    items.add(new Pair<>(Item.LIGHT_CRYSTAL, 2));
	    items.add(new Pair<>(Item.MAGIC_CRYSTAL, 2));
	    items.add(new Pair<>(Item.GALACTIC_CRYSTAL, 2));
	    
	    totalWeight = 0;
	    for (Pair<Item, Integer> entry : items) {
	    	totalWeight += entry.getSecond();
	    }
	}

	
	public void mine(boolean perilyte) {
		Item result = mineItem();
		while ((this.inventory.isEmpty() && result == null) || (!gp.player.p.flag[7][15] && result == Item.TEMPLE_ORB)) {
			result = mineItem();
		}
		if (result == null && perilyte && random.nextBoolean()) { // if the player has a perilyte, reroll the failure once 50% of the time
			result = mineItem();
		}
		if (result == null) {
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, this, "AHHHHHHH!");
			Task.addTask(Task.DIALOGUE, this, "What the hell is that?! We gotta get out of here!");
			this.inventory.clear();
			endMine(perilyte);
		} else {
			int sleep = random.nextInt(320);
			sleep += 20;
			if (perilyte) sleep /= 4;
			Task.addTask(Task.SLEEP, "", sleep);
			
			int sw = random.nextInt(4);
			String speech;
			switch (sw) {
			case 0:
				speech = "Ah nice! Here's a ";break;
			case 1:
				speech = "Look! I found a ";break;
			case 2:
				speech = "What's this..? Oh, I dug up a ";break;
			case 3:
				speech = "Ohoh! We got something big here! A ";break;
			default:
				speech = "";break;
			}
			Task t = Task.addTask(Task.DIALOGUE, this, speech + result.toString() + "!");
			t.ui = Task.ITEM;
			t.item = result;
			this.inventory.add(result);
			t = Task.addTask(Task.CONFIRM, this, "Do you want me to keep going?", 18);
			t.wipe = perilyte;
		}
	}

	private Item mineItem() {
		turns++;
		if (turns % 5 == 0) {
			Pair<Item, Integer> fail = items.get(0);
			fail.setSecond(fail.getSecond() + 2);
			totalWeight += 2;
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, this, "Going deeper...");
			Task.addTask(Task.SLEEP, "", 30);
		}
		int roll = random.nextInt(totalWeight);
		
		for (Pair<Item, Integer> entry : items) {
			roll -= entry.getSecond();
			if (roll < 0) {
				return entry.getFirst();
			}
		}
		return null;
	}
	
	public void startMine(Player p) {
		p.setMoney(p.getMoney() - PRICE);
		gp.saveGame(p, true);
		boolean perilyte = p.hasPokemonID(229);
		if (perilyte) Task.addTask(Task.DIALOGUE, this, "Ooh! A Perilyte in the flesh! It looks thrilled to help!");
		Task.addTask(Task.DIALOGUE, this, "Just tell me when to quit so I don't accidentally lose all of your items!");
		Task t = Task.addTask(Task.FLASH_IN, "");
		t.color = Color.BLACK;
		this.resetMine();
		mine(perilyte);
	}
	
	public void endMine(boolean perilyte) {
		Task.addTask(Task.SLEEP, "", 120);
		Task.addTask(Task.FLASH_OUT, "");
		if (this.inventory.isEmpty()) {
			Task.addTask(Task.DIALOGUE, this, "...In my panic, I dropped everything and ran.");
			Task.addTask(Task.DIALOGUE, this, "Sorry about that bud, I don't know what was down there but I didn't like it.");
			Task.addTask(Task.DIALOGUE, this, "You gotta be my set of eyes for scary stuff like that and tell me when to stop!");
			if (!perilyte) {
				Task.addTask(Task.DIALOGUE, this, "Y'know, it's not very easy to do this alone. And I need you as my set of eyes.");
				Task.addTask(Task.DIALOGUE, this, "...What? You've heard of Perilyte too? Yeah, they're extremely rare and the masters of mining. Even better than I.");
				Task.addTask(Task.DIALOGUE, this, "If you had one... I bet it would love to help me mine. Would probably be a lot faster and safer!");
				Task.addTask(Task.DIALOGUE, this, "I've seen a singular Perilyte before, and it ran from me deeper underground.");
				Task.addTask(Task.DIALOGUE, this, "Would love to see one close up! I'm sure it would be plenty of help to us here!");
			}
		} else {
			String message = this.inventory.size() >= 20 ? "That was quite the haul! Here's everything you got!" : "Alright! Here's what I dug up!";
			Task.addTask(Task.DIALOGUE, this, message);
		}
		for (Item i : this.inventory) {
			Task t = Task.addTask(Task.ITEM, "");
			t.item = i;
		}
		if (!this.inventory.isEmpty()) {
			Task.addTask(Task.ITEM_SUM, this, "");
		}
	}
	
	private void resetMine() {
		this.inventory.clear();
		this.turns = 0;
		this.totalWeight = this.defaultTotal;
		Pair<Item, Integer> fail = items.get(0);
		fail.setSecond(FAIL_CHANCE);
	}

	public void handleChanceViewerInput() {
		if (gp.keyH.wPressed) {
			viewPerilyte = !viewPerilyte;
			chancesDirty = true;
			gp.keyH.wPressed = false;
		}
		if (gp.keyH.leftPressed) {
			if (viewDepth > 0) {
				viewDepth--;
				chancesDirty = true;
			}
			gp.keyH.leftPressed = false;
		}
		if (gp.keyH.rightPressed) {
			if (viewDepth < MAX_VIEW_DEPTH) {
				viewDepth++;
				chancesDirty = true;
			}
			gp.keyH.rightPressed = false;
		}
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			Task t = Task.addTask(Task.MINE, this, "Hi there! If you pay me $" + this.PRICE + ", I'll head into my mine and see what I can dig up for you!\n(Warning: Will Auto-Save)");
			t.ui = Task.MONEY;
			gp.ui.currentTask = null;
		}
	}

	public int getViewDepth() {
		return viewDepth;
	}

	public boolean isViewPerilyte() {
		return viewPerilyte;
	}

	public static int getMaxViewDepth() {
		return MAX_VIEW_DEPTH;
	}

	/** Cached per-item probabilities at the current viewDepth/viewPerilyte. Recomputed only when dirty. */
	public LinkedHashMap<Item, Double> getChanceBuffer() {
		if (chancesDirty) recomputeChances();
		return chanceBuffer;
	}

	/** Cached probability of the "wipe everything" fail result. */
	public double getFailChance() {
		if (chancesDirty) recomputeChances();
		return failChanceBuffer;
	}

	public void recomputeChances() {
		// NOTE on the +1: items.get(0) (the fail entry) is stored at weight
		// FAIL_CHANCE (15), but setupItems() originally summed defaultTotal
		// with that entry at 16. The extra 1 of "total" weight is never
		// claimed by any entry, so random.nextInt(totalWeight) can roll a
		// value the for-loop in mineItem() never subtracts below 0 for,
		// falling through to the implicit `return null` at the end -- i.e.
		// it's a second, hidden way to fail. So the *effective* fail weight
		// is FAIL_CHANCE + 1 (=16), which is also exactly why your starting
		// fail weight "feels like" 16 even though FAIL_CHANCE is 15. Each
		// depth level then adds 2 to both the explicit fail weight and to
		// totalWeight, so this hidden +1 stays constant at every depth.
		int effectiveFailWeight = (FAIL_CHANCE + 1) + (viewDepth * 2);
		int total = defaultTotal + (viewDepth * 2);

		double f = effectiveFailWeight / (double) total;

		chanceBuffer.clear();
		for (Pair<Item, Integer> entry : items) {
			Item item = entry.getFirst();
			if (item == null) continue; // fail handled separately via failChanceBuffer
			double base = entry.getSecond() / (double) total;
			// Perilyte reroll math: on a fail, there's a 50% chance to reroll once.
			// A non-fail item can now be reached two ways: the first roll hit it
			// directly, OR the first roll failed, you got the 50% reroll, and the
			// reroll hit it. That's base * (1 + 0.5f).
			chanceBuffer.put(item, viewPerilyte ? base * (1 + 0.5 * f) : base);
		}

		// Fail can now only happen if the first roll fails AND you either don't
		// get the reroll (50%) or you do reroll and fail again (0.5f * f).
		// This is NOT simply "half the fail chance" -- it's f * (0.5 + 0.5f),
		// which approaches half of f only as f gets small.
		failChanceBuffer = viewPerilyte ? f * (0.5 + 0.5 * f) : f;

		chancesDirty = false;
	}
	
}