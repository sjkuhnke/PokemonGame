package entity;

import overworld.GamePanel;

public class NPC_Clerk extends Entity {	
	
	public NPC_Clerk(GamePanel gp) {
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
		dialogues[0] = "Welcome to the Mart!\nHow can I help you?";
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.gameState = GamePanel.SHOP_STATE;
		
		gp.ui.npc = this;
	}
}
