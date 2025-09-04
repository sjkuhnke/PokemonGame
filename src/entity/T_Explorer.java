package entity;

import overworld.GamePanel;

public class T_Explorer extends NPC_Trainer {

	public T_Explorer(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/explorer1");
		up1 = setup("/npc/explorer2");
		left1 = setup("/npc/explorer3");
		right1 = setup("/npc/explorer4");
	}

}
