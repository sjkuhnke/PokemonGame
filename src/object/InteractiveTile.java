package object;

import entity.Entity;
import overworld.GamePanel;

public class InteractiveTile extends Entity {

	
	GamePanel gp;
	public boolean destructible = false;
	
	public InteractiveTile(GamePanel gp) {
		super(gp, null);
		this.gp = gp;
		this.direction = "down";
	}
}
