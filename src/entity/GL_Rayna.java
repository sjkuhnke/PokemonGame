package entity;

import overworld.GamePanel;

public class GL_Rayna extends NPC_GymLeader {

	public GL_Rayna(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/rayna1");
		up1 = setup("/npc/rayna2");
		left1 = setup("/npc/rayna3");
		right1 = setup("/npc/rayna4");
	}

}