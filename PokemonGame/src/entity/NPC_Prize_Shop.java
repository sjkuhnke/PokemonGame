package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import overworld.GamePanel;
import pokemon.Item;
import pokemon.Pokemon;
import util.Pair;

public class NPC_Prize_Shop extends Entity {
	
	public ArrayList<Pair<Item, Integer>> coinItems;
	public ArrayList<Pair<Item, Integer>> winItems;
	public ArrayList<Pair<Pokemon, Integer>> prizePokemon;
	
	public NPC_Prize_Shop(GamePanel gp) {
		super(gp, null);
		this.setDirection("down");
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/market1");
		up1 = setup("/npc/market2");
		left1 = setup("/npc/market3");
		right1 = setup("/npc/market4");
	}
	
	private void setDialogue() {
		dialogues[0] = "Hi there! Here, you can exchange coins for\nprizes!";
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.PRIZE_STATE;
		
		gp.ui.npc = this;
	}

	public void setCoinItems(List<Pair<Item, Integer>> coinItems) {
		this.coinItems = new ArrayList<>(coinItems);
	}
	
	public void setWinItems(List<Pair<Item, Integer>> winItems) {
		this.winItems = new ArrayList<>(winItems);
		for (int i = 0; i < winItems.size(); i++) {
			Pair<Item, Integer> p = winItems.get(i);
			if (gp.player.p.hasTM(p.getFirst().getMove())) {
				this.winItems.remove(p);
			}
		}
	}
	
	public void setPrizePokemon(List<Pair<Integer, Integer>> prizePokemon) {
		this.prizePokemon = new ArrayList<>();
		for (Pair<Integer, Integer> p : prizePokemon) {
			this.prizePokemon.add(new Pair<>(new Pokemon(p.getFirst(), getPrizeLevel(), true, false), p.getSecond()));
		}
	}

	public ArrayList<?> getInventory(int subState) {
		switch(subState) {
		case 1: return coinItems;
		case 2: return winItems;
		case 3: return prizePokemon;
		}
		return null;
	}
	
	@Override
	public void setItems(boolean sort, Item... items) {
		this.setCoinItems(getCoinItems());
		this.setWinItems(getWinItems());
		this.setPrizePokemon(getPrizePokemon());
	}
	
	public List<Pair<Item, Integer>> getCoinItems() {
		int available = 0;
		int badges = gp.player.p.badges;
		if (badges > 4) available += 3; // air balloon, eject button and eject pack
		if (badges > 3) available += 3; // weakness policy, blunder policy, red card
		if (badges > 2) available++; // focus sash
		if (badges > 1) available += 3; // white herb, power herb, throat spray
		if (badges > 0) available += 3; // mental herb, mirror herb, room service
		
		List<Pair<Item, Integer>> items = Arrays.asList(
			// coin items
			new Pair<>(Item.MENTAL_HERB, 50),
			new Pair<>(Item.MIRROR_HERB, 50),
			new Pair<>(Item.ROOM_SERVICE, 100),
			new Pair<>(Item.WHITE_HERB, 50),
			new Pair<>(Item.POWER_HERB, 50),
			new Pair<>(Item.THROAT_SPRAY, 150),
			new Pair<>(Item.FOCUS_SASH, 100),
			new Pair<>(Item.BLUNDER_POLICY, 200),
			new Pair<>(Item.WEAKNESS_POLICY, 200),
			new Pair<>(Item.RED_CARD, 250),
			new Pair<>(Item.AIR_BALLOON, 250),
			new Pair<>(Item.EJECT_BUTTON, 300),
			new Pair<>(Item.EJECT_PACK, 300)
		);
		
		ArrayList<Pair<Item, Integer>> result = new ArrayList<>();
		
		for (int i = 0; i < available; i++) {
			result.add(items.get(i));
		}
		
		result.sort(Comparator.comparing(Pair<Item, Integer>::getSecond).thenComparing(pair -> pair.getFirst().getID()));
		
		return result;
	}
	
	public List<Pair<Item, Integer>> getWinItems() {
		int available = 0;
		int badges = gp.player.p.badges;
		if (badges > 5) available++;
		if (badges > 4) available++;
		if (badges > 3) available++;
		if (badges > 2) available++;
		if (badges > 1) available++;
		if (badges > 0) available++;
		
		List<Pair<Item, Integer>> items = Arrays.asList(
			// win TMs
			new Pair<>(Item.TM67, 10),
			new Pair<>(Item.TM39, 25),
			new Pair<>(Item.TM96, 40),
			new Pair<>(Item.TM21, 60),
			new Pair<>(Item.TM70, 75),
			new Pair<>(Item.TM94, 100)
		);
		
		ArrayList<Pair<Item, Integer>> result = new ArrayList<>();
		
		for (int i = 0; i < available; i++) {
			result.add(items.get(i));
		}
		
		return result;
	}
	
	public List<Pair<Integer, Integer>> getPrizePokemon() {
		int badges = gp.player.p.badges;
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
		ArrayList<Integer> pokemon = new ArrayList<>(   Arrays.asList(150, 143, 195, 184, 187, 190, 263));
		ArrayList<Integer> winStreaks = new ArrayList<>(Arrays.asList(3,   4,   6,   7,   8,   9,   10));
		if (badges > 5) {
			result.add(new Pair<Integer, Integer>(pokemon.get(0), winStreaks.get(0)));
			result.add(new Pair<Integer, Integer>(pokemon.get(1), winStreaks.get(1)));
		}
		Random random = new Random(gp.aSetter.generateSeed(gp.player.p.getID(), this.worldX / gp.tileSize, this.worldY / gp.tileSize, (int) this.scriptIndex));
		ArrayList<Integer> bank = new ArrayList<>();
		for (int i = 0; i < badges + 1; i++) {
			int id = 0;
			do {
				id = Pokemon.getRandomBasePokemon(random);
			} while (bank.contains(id) || (badges > 5 && (pokemon.contains(id))));
			bank.add(id);
			result.add(new Pair<Integer, Integer>(id, 5));
		}
		
		if (badges > 5) {
			for (int i = 2; i < pokemon.size(); i++) {
				result.add(new Pair<Integer, Integer>(pokemon.get(i), winStreaks.get(i)));
			}
		}
		
		return result;
	}

	public int getPrizeLevel() {
		return Math.min((gp.player.p.badges + 1) * 10, 25);
	}
}
