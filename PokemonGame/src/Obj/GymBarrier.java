package Obj;

import Overworld.GamePanel;

public class GymBarrier extends InteractiveTile {

	
	GamePanel gp;
	
	public GymBarrier(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/npc/gym_barrier");
		destructible = false;
		collision = true;
	}
}
