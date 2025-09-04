package entity;

import overworld.GamePanel;

public class T_Fisherman extends NPC_Trainer {

	public T_Fisherman(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/fisherman1");
		up1 = setup("/npc/fisherman2");
		left1 = setup("/npc/fisherman3");
		right1 = setup("/npc/fisherman4");
	}

}
