package Entity;

import Overworld.GamePanel;

public class NPC_PC extends Entity {
	private boolean isGauntlet = false;

	public NPC_PC(GamePanel gp) {
		super(gp);
		
		this.direction = "down";
		
		getImage();
	}
	
	public NPC_PC(GamePanel gp, boolean isGauntlet) {
		this(gp);
		this.isGauntlet = isGauntlet;
	}
	
	public void getImage() {
		down1 = setup("/npc/pcbox");
	}

	public boolean isGauntlet() {
		return isGauntlet;
	}

}
