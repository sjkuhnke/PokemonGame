package entity;

import overworld.GamePanel;

public class NPC_GymLeader extends Entity {
	
	public NPC_GymLeader(GamePanel gp, String d, int t, String[] message) {
		super(gp, null);
		
		setDirection(d);
		trainer = t;
		
		setDialogue(message);
		
	}
}
