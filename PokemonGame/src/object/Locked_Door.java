package object;

import overworld.GamePanel;

public class Locked_Door extends InteractiveTile {

	
	GamePanel gp;
	
	public Locked_Door(GamePanel gp, int flag) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/invisible");
		destructible = true;
		collision = true;
		
		this.flag = flag;
	}
}
