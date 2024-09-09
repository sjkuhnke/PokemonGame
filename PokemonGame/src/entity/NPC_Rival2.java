package entity;

import overworld.GamePanel;

public class NPC_Rival2 extends NPC_Trainer {

	public NPC_Rival2(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/fred1");
		up1 = setup("/npc/fred2");
	}

}
