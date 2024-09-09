package entity;

import overworld.GamePanel;

public class T_Invisible extends NPC_Trainer {

	public T_Invisible(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
		
		getImage();
	}
	
	public void getImage() {
		down1 = setup("/npc/invisible1");
		up1 = setup("/npc/invisible2");
		left1 = setup("/npc/invisible3");
		right1 = setup("/npc/invisible4");
	}

}
