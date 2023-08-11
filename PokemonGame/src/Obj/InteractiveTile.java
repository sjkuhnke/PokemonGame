package Obj;

import Entity.Entity;
import Overworld.GamePanel;

public class InteractiveTile extends Entity {

	
	GamePanel gp;
	public boolean destructible = false;
	
	public InteractiveTile(GamePanel gp) {
		super(gp);
		this.gp = gp;
		this.direction = "down";
	}
}
