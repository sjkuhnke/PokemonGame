package entity;

import overworld.GamePanel;

public class GL_Maxwell extends NPC_GymLeader {

	public GL_Maxwell(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/maxwell1");
		up1 = setup("/npc/maxwell2");
		left1 = setup("/npc/maxwell3");
		right1 = setup("/npc/maxwell4");
	}

}
