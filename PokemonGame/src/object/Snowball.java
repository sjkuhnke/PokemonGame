package object;

import java.awt.Color;

import overworld.GamePanel;

public class Snowball extends InteractiveTile {

	GamePanel gp;
	
	public Snowball(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/snowball");
		destructible = true;
		collision = true;
	}
	
	@Override
	public Color getParticleColor() {
		return new Color(173, 255, 248);
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
