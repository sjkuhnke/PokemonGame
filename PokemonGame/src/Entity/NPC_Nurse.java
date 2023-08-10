package Entity;

import Overworld.GamePanel;

public class NPC_Nurse extends Entity {
	
	public NPC_Nurse(GamePanel gp) {
		super(gp);
		this.direction = "down";
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/nurse");
	}

}
