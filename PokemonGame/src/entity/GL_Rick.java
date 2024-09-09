package entity;

import overworld.GamePanel;

public class GL_Rick extends NPC_GymLeader {

	public GL_Rick(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/rick1");
		up1 = setup("/npc/rick2");
		left1 = setup("/npc/rick3");
		right1 = setup("/npc/rick4");
	}

}