package entity;

import overworld.GamePanel;

public class NPC_GymLeader extends Entity {

	public NPC_GymLeader(GamePanel gp, String d, int t, String[] message) {
		super(gp);
		
		direction = d;
		trainer = t;
		
		getImage();
		setDialogue(message);
		
	}
	
	public void getImage() {
		down1 = setup("/npc/robin1");
		down2 = setup("/npc/stanford");
		up1 = setup("/npc/millie");
		up2 = setup("/npc/glacius");
		left1 = setup("/npc/mindy");
		left2 = setup("/npc/rayna");
		right1 = setup("/npc/merlin");
		right2 = setup("/npc/nova");
	}

}
