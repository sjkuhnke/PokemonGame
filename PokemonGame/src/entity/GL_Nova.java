package entity;

import overworld.GamePanel;

public class GL_Nova extends NPC_GymLeader {

	public GL_Nova(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/nova1");
		up1 = setup("/npc/nova2");
		left1 = setup("/npc/nova3");
		right1 = setup("/npc/nova4");
	}

}