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
		
		walkable = true;
		down2 = setup("/npc/rick1_1");
		up2 = setup("/npc/rick2_1");
		left2 = setup("/npc/rick3_1");
		right2 = setup("/npc/rick4_1");
		down3 = down1;
		up3 = up1;
		left3 = left1;
		right3 = right1;
		down4 = setup("/npc/rick1_2");
		up4 = setup("/npc/rick2_2");
		left4 = setup("/npc/rick3_2");
		right4 = setup("/npc/rick4_2");
	}

}