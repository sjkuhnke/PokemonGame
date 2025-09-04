package entity;

import overworld.GamePanel;

public class T_Hiker extends NPC_Trainer {

	public T_Hiker(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/hiker1");
		up1 = setup("/npc/hiker2");
		left1 = setup("/npc/hiker3");
		right1 = setup("/npc/hiker4");
	}

}
