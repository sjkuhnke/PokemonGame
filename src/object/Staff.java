package object;

import java.awt.Graphics2D;

import overworld.GamePanel;

public class Staff extends ItemObj {
	public Staff(GamePanel gp) {
		super(gp, null);
		
		down1 = setup("/interactive/staff");
		up1 = setup("/interactive/staff1");
		
		this.setDirection("down");
	}
	
	@Override
	public void draw(Graphics2D g2) {
		super.entityDraw(g2);
	}
}
