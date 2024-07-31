package object;

import overworld.GamePanel;

public class Starter_Machine extends InteractiveTile {

	GamePanel gp;
	
	public Starter_Machine(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		down1 = setup("/interactive/invisible");
		collision = true;
	}

}
