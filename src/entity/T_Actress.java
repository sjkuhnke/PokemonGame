package entity;

import overworld.GamePanel;

public class T_Actress extends NPC_Trainer {

	public T_Actress(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/actress1");
		up1 = setup("/npc/actress2");
		left1 = setup("/npc/actress3");
		right1 = setup("/npc/actress4");
	}

}
