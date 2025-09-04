package entity;

import overworld.GamePanel;

public class T_StudentM extends NPC_Trainer {

	public T_StudentM(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/student_m1");
		up1 = setup("/npc/student_m2");
		left1 = setup("/npc/student_m3");
		right1 = setup("/npc/student_m4");
	}

}
