package entity;

import overworld.GamePanel;

public class NPC_Market extends Entity {
	public NPC_Market(GamePanel gp) {
		super(gp);
		this.direction = "down";
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/clerk2");
	}
	
	private void setDialogue() {
		dialogues[0] = "Well hello there Trainer.\nI have some rare goods with some crazy\ngood deals.\nInterested?";
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.SHOP_STATE;
		
		gp.ui.npc = this;
	}
}
