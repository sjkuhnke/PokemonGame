package entity;

import overworld.GamePanel;

public class T_Arthra extends NPC_Trainer {

	public T_Arthra(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/arthra1");
		up1 = setup("/npc/arthra2");
		left1 = setup("/npc/arthra3");
		right1 = setup("/npc/arthra4");
	}

}