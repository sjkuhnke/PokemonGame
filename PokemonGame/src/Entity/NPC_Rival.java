package Entity;

import Overworld.GamePanel;

public class NPC_Rival extends NPC_Trainer {

	public NPC_Rival(GamePanel gp, String d, int t) {
		super(gp, d, t);
		
	}
	
	public void getImage() {
		down1 = setup("/npc/scott1");
		up1 = setup("/npc/scott2");
	}

}
