package entity;

import overworld.GamePanel;

public class GL_Merlin extends NPC_GymLeader {

	public GL_Merlin(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/merlin1");
		up1 = setup("/npc/merlin2");
		left1 = setup("/npc/merlin3");
		right1 = setup("/npc/merlin4");
	}

}