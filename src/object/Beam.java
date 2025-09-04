package object;

import java.awt.Graphics2D;

import overworld.GamePanel;

public class Beam extends InteractiveTile {

	
	GamePanel gp;
	int type;
	int ticks;
	
	public Beam(GamePanel gp, int type) {
		super(gp);
		this.gp = gp;
		this.type = type;
		String pathType = "_" + type;
		down1 = setup("/interactive/beam1" + pathType);
		down2 = setup("/interactive/beam2" + pathType);
		down3 = setup("/interactive/beam3" + pathType);
		down4 = setup("/interactive/beam2" + pathType);
		
		destructible = false;
		collision = true;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		
		ticks++;
		if (ticks >= 24) {
			ticks = 0;
			spriteNum++;
			if (spriteNum > 4) {
				spriteNum = 1;
			}
		}
	}
}
