package object;

import java.awt.Color;

import overworld.GamePanel;

public class Rock_Smash extends InteractiveTile {

	
	GamePanel gp;
	
	public Rock_Smash(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		down1 = setup("/interactive/rock_smash");
		destructible = true;
		collision = true;
	}
	
	@Override
	public Color getParticleColor() {
		return new Color(65, 50, 30);
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
