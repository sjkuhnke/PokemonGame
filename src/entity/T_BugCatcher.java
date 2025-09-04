package entity;

import overworld.GamePanel;

public class T_BugCatcher extends NPC_Trainer {

	public T_BugCatcher(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/bug_catcher1");
		up1 = setup("/npc/bug_catcher2");
		left1 = setup("/npc/bug_catcher3");
		right1 = setup("/npc/bug_catcher4");
	}

}
