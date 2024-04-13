package entity;

import overworld.GamePanel;

public class NPC_Invisible extends NPC_Trainer {

	public NPC_Invisible(GamePanel gp, String d, int t) {
		super(gp, d, t);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/invisible1");
		up1 = setup("/npc/invisible2");
		left1 = setup("/npc/invisible3");
		right1 = setup("/npc/invisible4");
	}

}
