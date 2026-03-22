package entity;

import overworld.GamePanel;

public class GL_Whiskeroar extends NPC_GymLeader {

	public GL_Whiskeroar(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/overworlds/128");
		up1 = setup("/overworlds/128");
		left1 = setup("/overworlds/128");
		right1 = setup("/overworlds/128");
	}

}
