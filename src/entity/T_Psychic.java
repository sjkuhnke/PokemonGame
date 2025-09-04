package entity;

import overworld.GamePanel;

public class T_Psychic extends NPC_Trainer {

	public T_Psychic(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/psychic1");
		up1 = setup("/npc/psychic2");
		left1 = setup("/npc/psychic3");
		right1 = setup("/npc/psychic4");
	}

}
