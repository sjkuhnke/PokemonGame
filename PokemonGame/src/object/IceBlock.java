package object;

import java.awt.Color;

import overworld.GamePanel;

public class IceBlock extends InteractiveTile {
	GamePanel gp;
	
	public IceBlock(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/ice_block");
		destructible = true;
		collision = true;
	}
	
	@Override
	public Color getParticleColor() {
		return new Color(130, 245, 255);
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
