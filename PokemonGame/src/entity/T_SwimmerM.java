package entity;

import overworld.GamePanel;

public class T_SwimmerM extends NPC_Trainer {

	public T_SwimmerM(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/swimmer_m1");
		up1 = setup("/npc/swimmer_m2");
		left1 = setup("/npc/swimmer_m3");
		right1 = setup("/npc/swimmer_m4");
	}

}
