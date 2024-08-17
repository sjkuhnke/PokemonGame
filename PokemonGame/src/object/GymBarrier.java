package object;

import overworld.GamePanel;

public class GymBarrier extends InteractiveTile {

	
	GamePanel gp;
	
	public GymBarrier(GamePanel gp) {
		this(gp, -1);
	}
	
	public GymBarrier(GamePanel gp, int flag) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/gym_barrier");
		destructible = false;
		collision = true;
		
		this.flag = flag;
	}
}
