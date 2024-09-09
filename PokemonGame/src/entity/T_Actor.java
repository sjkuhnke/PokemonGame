package entity;

import overworld.GamePanel;

public class T_Actor extends NPC_Trainer {

	public T_Actor(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/actor1");
		up1 = setup("/npc/actor2");
		left1 = setup("/npc/actor3");
		right1 = setup("/npc/actor4");
	}

}
