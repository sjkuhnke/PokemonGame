package object;

import overworld.GamePanel;

public class Statue extends InteractiveTile {

	GamePanel gp;
	boolean reset;
	
	public Statue(GamePanel gp, boolean reset) {
		super(gp);
		this.gp = gp;
		this.reset = reset;
		down1 = setup("/interactive/statue");
		down2 = setup("/interactive/statue_reset");
		spriteNum = reset ? 2 : 1;
		this.setDirection("down");
		
		destructible = false;
		collision = true;
	}
	
	public boolean isReset() {
		return this.reset;
	}
}
