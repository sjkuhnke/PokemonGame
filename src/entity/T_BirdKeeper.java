package entity;

import overworld.GamePanel;

public class T_BirdKeeper extends NPC_Trainer {

	public T_BirdKeeper(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/bird_keeper1");
		up1 = setup("/npc/bird_keeper2");
		left1 = setup("/npc/bird_keeper3");
		right1 = setup("/npc/bird_keeper4");
	}

}
