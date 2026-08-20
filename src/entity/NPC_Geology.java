package entity;

import overworld.GamePanel;

public class NPC_Geology extends NPC_Market {

	public NPC_Geology(GamePanel gp) {
		super(gp);
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/market1");
		up1 = setup("/npc/market2");
		left1 = setup("/npc/market3");
		right1 = setup("/npc/market4");
	}
	
	private void setDialogue() {
		dialogues[0] = "You seem trustworthy...\nWould you like to look at my rock collection?";
		dialogues[1] = "I can't believe I got robbed...\nI can't trust anyone to look at my collection\nright now, sorry.";
	}
	
	@Override
	public void speak(int mode) {
		if (gp.player.p.flag[5][8]) { // player has beat gym 6
			gp.ui.currentDialogue = dialogues[0];
			gp.gameState = GamePanel.SHOP_STATE;
			
			gp.ui.npc = this;
		} else {
			gp.ui.currentDialogue = dialogues[1];
			gp.gameState = GamePanel.DIALOGUE_STATE;
			
			gp.ui.npc = this;
		}
	}
}
