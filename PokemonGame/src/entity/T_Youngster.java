package entity;

import overworld.GamePanel;

public class T_Youngster extends NPC_Trainer {

	public T_Youngster(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/youngster1");
		up1 = setup("/npc/youngster2");
		left1 = setup("/npc/youngster3");
		right1 = setup("/npc/youngster4");
	}

}