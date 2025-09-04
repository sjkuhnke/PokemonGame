package entity;

import overworld.GamePanel;

public class NPC_Market extends Entity {
	public NPC_Market(GamePanel gp) {
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
		dialogues[0] = "Well hello there Trainer.\nI have some rare goods with some crazy\ngood deals.\nInterested?";
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.SHOP_STATE;
		
		gp.ui.npc = this;
	}
}
