package entity;

import overworld.GamePanel;

public class T_TN extends NPC_Trainer {

	public T_TN(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/tn1");
		up1 = setup("/npc/tn2");
		left1 = setup("/npc/tn3");
		right1 = setup("/npc/tn4");
	}

}