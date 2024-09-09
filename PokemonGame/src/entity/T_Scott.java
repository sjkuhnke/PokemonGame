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
	}

}
