package Obj;

import Overworld.GamePanel;

public class Vine_Crossable extends InteractiveTile {

	
	GamePanel gp;
	
	public Vine_Crossable(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/npc/vine_crossable");
		destructible = false;
		collision = true;
	}
}
