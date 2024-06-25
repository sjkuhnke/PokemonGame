package object;

import overworld.GamePanel;

public class Vine extends InteractiveTile {

	
	GamePanel gp;
	
	public Vine(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/vines");
		destructible = false;
		collision = false;
	}
}