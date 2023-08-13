package Entity;

import Overworld.GamePanel;

public class NPC_Nurse extends Entity {
	
	public NPC_Nurse(GamePanel gp, String direction) {
		super(gp);
		this.direction = direction;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/nurse");
		up1 = setup("/npc/nurse1");
	}

}
