package object;

import overworld.GamePanel;

public class Fuse_Box extends InteractiveTile {

	
	GamePanel gp;
	
	public Fuse_Box(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/fuse01");
		up1 = setup("/interactive/fuse02");
		left1 = setup("/interactive/fuse03");
		destructible = false;
		collision = true;
	}
}
