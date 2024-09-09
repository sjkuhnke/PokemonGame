package entity;

import overworld.GamePanel;

public class T_AceTrainerF extends NPC_Trainer {

	public T_AceTrainerF(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/ace_trainer_f1");
		up1 = setup("/npc/ace_trainer_f2");
		left1 = setup("/npc/ace_trainer_f3");
		right1 = setup("/npc/ace_trainer_f4");
	}

}
