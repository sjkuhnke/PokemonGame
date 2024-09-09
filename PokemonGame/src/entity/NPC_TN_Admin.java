package entity;

import overworld.GamePanel;

public class NPC_TN_Admin extends NPC_GymLeader {

	public NPC_TN_Admin(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/rick");
		up1 = setup("/npc/maxwell");
	}

}