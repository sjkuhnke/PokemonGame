package entity;

import overworld.GamePanel;

public class GL_Ryder extends NPC_GymLeader {

	public GL_Ryder(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/ryder1");
		up1 = setup("/npc/ryder2");
		left1 = setup("/npc/ryder3");
		right1 = setup("/npc/ryder4");
	}

}
