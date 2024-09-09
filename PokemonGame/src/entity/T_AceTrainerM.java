package entity;

import overworld.GamePanel;

public class T_AceTrainerM extends NPC_Trainer {

	public T_AceTrainerM(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/ace_trainer_m1");
		up1 = setup("/npc/ace_trainer_m2");
		left1 = setup("/npc/ace_trainer_m3");
		right1 = setup("/npc/ace_trainer_m4");
	}

}
