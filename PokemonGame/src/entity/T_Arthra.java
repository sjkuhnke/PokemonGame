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
		
		walkable = true;
		down2 = setup("/npc/arthra1_1");
		up2 = setup("/npc/arthra2_1");
		left2 = setup("/npc/arthra3_1");
		right2 = setup("/npc/arthra4_1");
		down3 = down1;
		up3 = up1;
		left3 = left1;
		right3 = right1;
		down4 = setup("/npc/arthra1_2");
		up4 = setup("/npc/arthra2_2");
		left4 = setup("/npc/arthra3_2");
		right4 = setup("/npc/arthra4_2");
	}

}
