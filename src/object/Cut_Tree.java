package object;

import java.awt.Color;

import overworld.GamePanel;

public class Cut_Tree extends InteractiveTile {

	
	GamePanel gp;
	
	public Cut_Tree(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/cut_tree");
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
