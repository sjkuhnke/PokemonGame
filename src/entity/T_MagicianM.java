package entity;

import overworld.GamePanel;

public class T_MagicianM extends NPC_Trainer {

	public T_MagicianM(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/magician_m1");
		up1 = setup("/npc/magician_m2");
		left1 = setup("/npc/magician_m3");
		right1 = setup("/npc/magician_m4");
	}

}
