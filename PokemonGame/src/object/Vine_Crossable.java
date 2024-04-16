package object;

import java.awt.Color;

import overworld.GamePanel;

public class Vine_Crossable extends InteractiveTile {

	
	GamePanel gp;
	
	public Vine_Crossable(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/npc/vine_crossable");
		destructible = false;
		collision = true;
	}
	
	@Override
	public Color getParticleColor() {
		return new Color(16, 116, 1);
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
