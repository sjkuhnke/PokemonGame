package entity;

import overworld.GamePanel;

public class T_Maniac extends NPC_Trainer {

	public T_Maniac(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/maniac1");
		up1 = setup("/npc/maniac2");
		left1 = setup("/npc/maniac3");
		right1 = setup("/npc/maniac4");
	}

}
