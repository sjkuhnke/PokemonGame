package entity;

import overworld.GamePanel;

public class T_Postman extends NPC_Trainer {

	public T_Postman(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/postman1");
		up1 = setup("/npc/postman2");
		left1 = setup("/npc/postman3");
		right1 = setup("/npc/postman4");
	}

}
