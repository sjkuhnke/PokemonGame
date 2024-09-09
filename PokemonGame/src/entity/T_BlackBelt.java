package entity;

import overworld.GamePanel;

public class T_BlackBelt extends NPC_Trainer {

	public T_BlackBelt(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/black_belt1");
		up1 = setup("/npc/black_belt2");
		left1 = setup("/npc/black_belt3");
		right1 = setup("/npc/black_belt4");
	}

}
