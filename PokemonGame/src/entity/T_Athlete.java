package entity;

import overworld.GamePanel;

public class T_Athlete extends NPC_Trainer {

	public T_Athlete(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/athlete1");
		up1 = setup("/npc/athlete2");
		left1 = setup("/npc/athlete3");
		right1 = setup("/npc/athlete4");
	}

}
