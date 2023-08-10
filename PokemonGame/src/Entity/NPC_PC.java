package Entity;

import Overworld.GamePanel;

public class NPC_PC extends Entity {

	public NPC_PC(GamePanel gp) {
		super(gp);
		
		this.direction = "down";
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/pcbox");
	}

}
