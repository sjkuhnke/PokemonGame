package entity;

import overworld.GamePanel;

public class NPC_Rival extends NPC_Trainer {

	public NPC_Rival(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
	}
	
	public void getImage() {
		down1 = setup("/npc/scott1");
		up1 = setup("/npc/scott2");
	}

}
