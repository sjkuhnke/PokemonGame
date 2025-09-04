package entity;

import overworld.GamePanel;

public class GL_Glacius extends NPC_GymLeader {

	public GL_Glacius(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/glacius1");
		up1 = setup("/npc/glacius2");
		left1 = setup("/npc/glacius3");
		right1 = setup("/npc/glacius4");
	}

}