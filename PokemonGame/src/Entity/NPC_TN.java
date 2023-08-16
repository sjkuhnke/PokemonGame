package Entity;

import Overworld.GamePanel;

public class NPC_TN extends Entity {

	public NPC_TN(GamePanel gp, String d, int t) {
		super(gp);
		
		direction = d;
		trainer = t;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/tn1");
		up1 = setup("/npc/tn2");
		left1 = setup("/npc/tn3");
		right1 = setup("/npc/tn4");
	}

}