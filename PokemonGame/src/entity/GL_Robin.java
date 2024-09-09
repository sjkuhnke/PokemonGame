package entity;

import overworld.GamePanel;

public class GL_Robin extends NPC_GymLeader {

	public GL_Robin(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/robin1");
		up1 = setup("/npc/robin2");
		left1 = setup("/npc/robin3");
		right1 = setup("/npc/robin4");
	}

}
