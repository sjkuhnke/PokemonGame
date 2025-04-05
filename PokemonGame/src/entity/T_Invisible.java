package entity;

import overworld.GamePanel;

public class T_Invisible extends NPC_Trainer {

	public T_Invisible(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/trainer1");
		up1 = setup("/npc/trainer2");
		left1 = setup("/npc/trainer3");
		right1 = setup("/npc/trainer4");
	}

}
