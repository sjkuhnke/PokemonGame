package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import overworld.GamePanel;
import pokemon.Item;
import pokemon.Player;
import pokemon.Task;
import util.Pair;

public class NPC_Mine extends Entity {
	
	public final int PRICE = 350;
	private final int FAIL_CHANCE = 10;
	private ArrayList<Pair<Item, Integer>> items = new ArrayList<>();
	private ArrayList<Item> inventory = new ArrayList<>();
	private Random random;
	private int totalWeight;
	private final int defaultTotal;
	private int turns;
	
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
		items.add(new Pair<>(null, 10)); // FAIL 10%
	    items.add(new Pair<>(Item.STAR_PIECE, 12));
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
	    items.add(new Pair<>(Item.FABLE_CHARGE, 4));
	    items.add(new Pair<>(Item.RELIC_SILVER, 7));
	    items.add(new Pair<>(Item.RELIC_GOLD, 3));
	    items.add(new Pair<>(Item.NUGGET, 7));
	    items.add(new Pair<>(Item.BIG_NUGGET, 4));
	    items.add(new Pair<>(Item.RAZOR_CLAW, 2));
	    
	    items.add(new Pair<>(Item.EVIOLITE, 1));
	    items.add(new Pair<>(Item.BLACK_BELT, 1));
	    items.add(new Pair<>(Item.BLACK_GLASSES, 1));
	    items.add(new Pair<>(Item.CHARCOAL, 1));
	    items.add(new Pair<>(Item.COSMIC_CORE, 1));
	    items.add(new Pair<>(Item.DRAGON_FANG, 1));
	    items.add(new Pair<>(Item.ENCHANTED_AMULET, 1));
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
	    
	    totalWeight = 0;
	    for (Pair<Item, Integer> entry : items) {
	    	totalWeight += entry.getSecond();
	    }
	}

	
	public void mine(boolean perilyte) {
		Item result = mineItem();
		while (this.inventory.isEmpty() && result == null) {
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
			endMine();
		} else {
			int sleep = random.nextInt(320);
			sleep += 20;
			Task.addTask(Task.SLEEP, "", sleep);
			int sw = random.nextInt(3);
			String speech;
			switch (sw) {
			case 0:
				speech = "Ah nice! Here's a ";break;
			case 1:
				speech = "Look! I found a ";break;
			case 2:
				speech = "What's this..? Oh, I dug up a ";break;
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
			fail.setSecond(fail.getSecond() + 1);
			totalWeight++;
			System.out.println("===============================\nTurns: " + turns);
			System.out.println("Incremented fail to " + items.get(0).getSecond() + ", totalWeight: " + totalWeight);
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
		Task.addTask(Task.DIALOGUE, this, "Just tell me when to quit so I don't accidentally lose all of your items!");
		Task t = Task.addTask(Task.FLASH_IN, "");
		t.color = Color.BLACK;
		this.resetMine();
		mine(p.hasPokemonID(229));
	}
	
	public void endMine() {
		Task.addTask(Task.SLEEP, "", 120);
		Task.addTask(Task.FLASH_OUT, "");
		if (this.inventory.isEmpty()) {
			Task.addTask(Task.DIALOGUE, this, "...In my panic, I dropped everything and ran.");
			Task.addTask(Task.DIALOGUE, this, "Sorry about that bud, I don't know what was down there but I didn't like it.");
			Task.addTask(Task.DIALOGUE, this, "You gotta be my set of eyes for scary stuff like that and tell me when to stop!");
		} else {
			String message = this.inventory.size() >= 10 ? "That was quite the haul! Here's everything you got!" : "Alright! Here's what I dug up!";
			Task.addTask(Task.DIALOGUE, this, message);
		}
		for (Item i : this.inventory) {
			Task t = Task.addTask(Task.ITEM, "");
			t.item = i;
		}
		this.resetMine();
	}
	
	private void resetMine() {
		this.inventory.clear();
		this.turns = 0;
		this.totalWeight = this.defaultTotal;
		Pair<Item, Integer> fail = items.get(0);
		fail.setSecond(FAIL_CHANCE);
	}
	
}
