package entity;

import overworld.GamePanel;

public class NPC_Dealer extends Entity {
	public NPC_Dealer(GamePanel gp, String name) {
		super(gp, name);
		this.setDirection("right");
		this.scriptIndex = 127.1;
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/dealer1");
		up1 = setup("/npc/dealer2");
		left1 = setup("/npc/dealer3");
		right1 = setup("/npc/dealer4");
	}
	
	private void setDialogue() {
		dialogues[0] = "Welcome to Sports Betting - Pokemon Edition!";
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.script.runScript(this);
	}
}
