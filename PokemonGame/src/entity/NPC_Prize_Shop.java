package entity;

import java.util.ArrayList;
import java.util.List;

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
		for (int i = 0; i < this.winItems.size(); i++) {
			Pair<Item, Integer> p = this.winItems.get(i);
			if (gp.player.p.hasTM(p.getFirst().getMove())) {
				this.winItems.remove(i);
			}
		}
	}
	
	public void setPrizePokemon(List<Pair<Integer, Integer>> prizePokemon) {
		this.prizePokemon = new ArrayList<>();
		for (Pair<Integer, Integer> p : prizePokemon) {
			this.prizePokemon.add(new Pair<>(new Pokemon(p.getFirst(), 25, true, false), p.getSecond()));
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
}
