package entity;

import overworld.GamePanel;

public class T_Warden extends NPC_Trainer {

	public T_Warden(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/warden1");
		up1 = setup("/npc/warden2");
		left1 = setup("/npc/warden3");
		right1 = setup("/npc/warden4");
	}

}
