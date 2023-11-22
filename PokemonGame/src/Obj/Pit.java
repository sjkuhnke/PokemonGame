package Obj;

import Overworld.GamePanel;

public class Pit extends InteractiveTile {
	GamePanel gp;
	
	public int mapDest;
	public int xDest;
	public int yDest;
	
	public Pit(GamePanel gp, int mapDest, int xDest, int yDest) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/npc/pit0");
		destructible = false;
		collision = true;
		
		this.mapDest = mapDest;
		this.xDest = xDest;
		this.yDest = yDest;
	}

	public void setCoords(int x, int y) {
		this.worldX = gp.tileSize * x;
		this.worldY = gp.tileSize * y;
	}
}
