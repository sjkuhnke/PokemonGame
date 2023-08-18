package Entity;

import Overworld.GamePanel;

public class NPC_Market extends Entity {
	public NPC_Market(GamePanel gp) {
		super(gp);
		this.direction = "down";
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/clerk2");
	}
}
