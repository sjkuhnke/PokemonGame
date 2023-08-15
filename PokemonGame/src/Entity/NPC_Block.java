package Entity;

import Overworld.GamePanel;

public class NPC_Block extends Entity {
	
	String message;
	boolean fish;
	
	public NPC_Block(GamePanel gp, String message, boolean fish) {
		super(gp);
		this.direction = "down";
		this.message = message;
		
		this.fish = fish;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
}
