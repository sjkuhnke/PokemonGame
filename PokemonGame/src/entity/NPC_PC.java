package entity;

import overworld.GamePanel;

public class NPC_PC extends Entity {
	private boolean isGauntlet = false;

	public NPC_PC(GamePanel gp) {
		super(gp, null);
		
		this.setDirection("down");
		
		getImage();
	}
	
	public NPC_PC(GamePanel gp, boolean isGauntlet) {
		this(gp);
		this.isGauntlet = isGauntlet;
	}
	
	public void getImage() {
		down1 = setup("/npc/pcbox");
		up1 = setup("/npc/pcbox1");
	}

	public boolean isGauntlet() {
		return isGauntlet;
	}

}
