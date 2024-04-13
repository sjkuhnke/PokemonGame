package object;

import overworld.GamePanel;

public class Rock_Smash extends InteractiveTile {

	
	GamePanel gp;
	
	public Rock_Smash(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		down1 = setup("/npc/rock_smash");
		destructible = true;
		collision = true;
	}

}
