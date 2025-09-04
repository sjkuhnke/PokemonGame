package object;

import java.awt.Color;

import overworld.GamePanel;

public class Pit extends InteractiveTile {
	GamePanel gp;
	
	public int mapDest;
	public int xDest;
	public int yDest;
	
	public Pit(GamePanel gp, int mapDest, int xDest, int yDest) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/pit0");
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
	
	@Override
	public Color getParticleColor() {
		return new Color(132, 2, 134);
	}
	@Override
	public int getParticleSize() {
		return 6;
	}
	@Override
	public int getParticleSpeed() {
		return 1;
	}
	@Override
	public int getParticleMaxLife() {
		return 20;
	}
}
