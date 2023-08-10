package Entity;

import Overworld.GamePanel;

public class NPC_Trainer extends Entity {

	public NPC_Trainer(GamePanel gp, String d, int t) {
		super(gp);
		
		direction = d;
		trainer = t;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/trainer1");
		up1 = setup("/npc/trainer2");
		left1 = setup("/npc/trainer3");
		right1 = setup("/npc/trainer4");
	}

}
