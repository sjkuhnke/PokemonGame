package entity;

import overworld.GamePanel;

public class GL_Stanford extends NPC_GymLeader {

	public GL_Stanford(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/stanford1");
		up1 = setup("/npc/stanford2");
		left1 = setup("/npc/stanford3");
		right1 = setup("/npc/stanford4");
	}

}
