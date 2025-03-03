package entity;

import overworld.GamePanel;
import pokemon.Item;

public class NPC_Ball extends Entity {	
	
	public NPC_Ball(GamePanel gp) {
		super(gp, null);
		this.setDirection("down");
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/mart");
		right1 = setup("/npc/mart"); 
	}
	
	private void setDialogue() {
		dialogues[0] = "Hello there, I sell special Pokeballs!\nInterested in any today?";
	}
	
	@Override
	public void setItems(boolean sort, Item... items) {
		inventory.clear();
		
		inventory.add(Item.HEAL_BALL);
		inventory.add(Item.DUSK_BALL);
		inventory.add(Item.LUXURY_BALL);
		inventory.add(Item.NEST_BALL);
		inventory.add(Item.NET_BALL);
		
		if (gp.player.p.badges >= 1) {
			inventory.add(Item.DIVE_BALL);
		}
		if (gp.player.p.badges >= 2) {
			inventory.add(Item.QUICK_BALL);
			inventory.add(Item.TIMER_BALL);
		}
		if (gp.player.p.badges >= 3) {
			inventory.add(Item.REPEAT_BALL);
		}
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.SHOP_STATE;
		
		gp.ui.npc = this;
	}
}
