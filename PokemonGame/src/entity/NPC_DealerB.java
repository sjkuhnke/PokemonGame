package entity;

import overworld.GamePanel;

public class NPC_DealerB extends NPC_Dealer {
	public NPC_DealerB(GamePanel gp, String name) {
		super(gp, name);
		this.setDirection("down");
		this.scriptIndex = 127.0;
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/dealer_b1");
		up1 = setup("/npc/dealer_b2");
		left1 = setup("/npc/dealer_b3");
		right1 = setup("/npc/dealer_b4");
	}
	
	private void setDialogue() {
		dialogues[0] = "Welcome to the Blackjack table!";
	}
	
	public void speak(int mode) {
		super.speak(mode);
	}
}
