package entity;

import overworld.GamePanel;

public class T_Gentleman extends NPC_Trainer {

	public T_Gentleman(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/gentleman1");
		up1 = setup("/npc/gentleman2");
		left1 = setup("/npc/gentleman3");
		right1 = setup("/npc/gentleman4");
	}

}
