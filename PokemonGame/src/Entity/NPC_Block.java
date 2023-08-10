package Entity;

import Overworld.GamePanel;

public class NPC_Block extends Entity {
	
	String message;
	
	public NPC_Block(GamePanel gp, String message) {
		super(gp);
		this.direction = "down";
		this.message = message;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
}
