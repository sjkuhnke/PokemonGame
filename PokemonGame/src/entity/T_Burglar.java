package entity;

import overworld.GamePanel;

public class T_Burglar extends NPC_Trainer {

	public T_Burglar(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/burglar1");
		up1 = setup("/npc/burglar2");
		left1 = setup("/npc/burglar3");
		right1 = setup("/npc/burglar4");
	}

}
