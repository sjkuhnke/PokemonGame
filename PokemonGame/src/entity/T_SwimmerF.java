package entity;

import overworld.GamePanel;

public class T_SwimmerF extends NPC_Trainer {

	public T_SwimmerF(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/swimmer_f1");
		up1 = setup("/npc/swimmer_f2");
		left1 = setup("/npc/swimmer_f3");
		right1 = setup("/npc/swimmer_f4");
	}

}
