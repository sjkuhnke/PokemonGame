package Entity;

import Overworld.GamePanel;

public class NPC_Block extends Entity {
	
	String message;
	boolean more;
	
	public NPC_Block(GamePanel gp, String message, boolean more) {
		super(gp);
		this.direction = "down";
		this.message = message;
		
		this.more = more;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
}
