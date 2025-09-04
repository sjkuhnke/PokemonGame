package entity;

import overworld.GamePanel;

public class T_Scott extends NPC_Trainer {

	public T_Scott(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/scott1");
		up1 = setup("/npc/scott2");
		left1 = setup("/npc/scott3");
		right1 = setup("/npc/scott4");
		
		walkable = true;
		down2 = setup("/npc/scott1_1");
		up2 = setup("/npc/scott2_1");
		left2 = setup("/npc/scott3_1");
		right2 = setup("/npc/scott4_1");
		down3 = down1;
		up3 = up1;
		left3 = left1;
		right3 = right1;
		down4 = setup("/npc/scott1_2");
		up4 = setup("/npc/scott2_2");
		left4 = setup("/npc/scott3_2");
		right4 = setup("/npc/scott4_2");
	}

}
