package entity;

import overworld.GamePanel;

public class T_StudentF extends NPC_Trainer {

	public T_StudentF(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/student_f1");
		up1 = setup("/npc/student_f2");
		left1 = setup("/npc/student_f3");
		right1 = setup("/npc/student_f4");
	}

}
