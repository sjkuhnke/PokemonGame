package entity;

import overworld.GamePanel;

public class T_MagicianF extends NPC_Trainer {

	public T_MagicianF(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/magician_f1");
		up1 = setup("/npc/magician_f2");
		left1 = setup("/npc/magician_f3");
		right1 = setup("/npc/magician_f4");
	}

}
