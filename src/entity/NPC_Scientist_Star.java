package entity;

import overworld.GamePanel;
import pokemon.Item;
import ui.UI;

public class NPC_Scientist_Star extends NPC_Star {

	public NPC_Scientist_Star(GamePanel gp) {
		super(gp);
		this.setDirection("down");
		this.repair = false;
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/scientist1");
		up1 = setup("/npc/scientist2");
		left1 = setup("/npc/scientist3");
		right1 = setup("/npc/scientist4");
	}
	
	private void setDialogue() {
		dialogues[0] = Item.breakString("I can trade you some relevant items we've manufactured here in exchange for Star Pieces.", UI.MAX_TEXTBOX);
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.STAR_SHOP_STATE;
		
		gp.ui.npc = this;
	}
	
}
