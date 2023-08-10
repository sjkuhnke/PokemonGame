package Entity;

import Overworld.GamePanel;

public class NPC_GymLeader extends Entity {

	public NPC_GymLeader(GamePanel gp, String d, int t) {
		super(gp);
		
		direction = d;
		trainer = t;
		
		getImage();
		
	}
	
	public void getImage() {
		down1 = setup("/npc/gymleader");
	}

}
