package entity;

import overworld.GamePanel;

public class GL_Millie extends NPC_GymLeader {

	public GL_Millie(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/millie1");
		up1 = setup("/npc/millie2");
		left1 = setup("/npc/millie3");
		right1 = setup("/npc/millie4");
	}

}