package entity;

import overworld.GamePanel;

public class T_Lady extends NPC_Trainer {

	public T_Lady(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/lady1");
		up1 = setup("/npc/lady2");
		left1 = setup("/npc/lady3");
		right1 = setup("/npc/lady4");
	}

}
