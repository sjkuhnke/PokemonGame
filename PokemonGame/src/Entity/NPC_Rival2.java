package Entity;

import Overworld.GamePanel;

public class NPC_Rival2 extends Entity {

	public NPC_Rival2(GamePanel gp, String d, int t) {
		super(gp);
		
		direction = d;
		trainer = t;
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/fred1");
		up1 = setup("/npc/fred2");
	}

}
