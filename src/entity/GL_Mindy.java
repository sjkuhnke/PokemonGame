package entity;

import overworld.GamePanel;

public class GL_Mindy extends NPC_GymLeader {

	public GL_Mindy(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/mindy1");
		up1 = setup("/npc/mindy2");
		left1 = setup("/npc/mindy3");
		right1 = setup("/npc/mindy4");
	}

}