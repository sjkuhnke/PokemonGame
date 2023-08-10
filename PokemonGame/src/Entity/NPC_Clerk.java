package Entity;

import Overworld.GamePanel;

public class NPC_Clerk extends Entity {
	public NPC_Clerk(GamePanel gp) {
		super(gp);
		this.direction = "down";
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/clerk");
	}
}
