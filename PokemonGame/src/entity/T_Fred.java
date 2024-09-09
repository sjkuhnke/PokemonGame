package entity;

import overworld.GamePanel;

public class T_Fred extends NPC_Trainer {

	public T_Fred(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/fred1");
		up1 = setup("/npc/fred2");
		left1 = setup("/npc/fred3");
		right1 = setup("/npc/fred4");
	}

}
