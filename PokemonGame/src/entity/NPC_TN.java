package entity;

import overworld.GamePanel;

public class NPC_TN extends NPC_Trainer {

	public NPC_TN(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/tn1");
		up1 = setup("/npc/tn2");
		left1 = setup("/npc/tn3");
		right1 = setup("/npc/tn4");
	}

}