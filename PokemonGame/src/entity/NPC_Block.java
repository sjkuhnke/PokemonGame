package entity;

import overworld.GamePanel;
import pokemon.*;

public class NPC_Block extends Entity {
	
	public NPC_Block(GamePanel gp, String name, String[] message, double scriptIndex, int flag, String altDialogue) {
		super(gp, name);
		this.setDirection("down");
		
		this.flag = flag;
		this.scriptIndex = scriptIndex;
		this.altDialogue = altDialogue;
		
		getImage();
		setDialogue(message);
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
	
	public void speak(int mode) {
		if (scriptIndex < 0 || mode != 0) {
			super.speak(mode);
		} else {
			gp.setTaskState();
			if (!dialogues[dialogueIndex].isEmpty()) {
				Task.addTask(Task.DIALOGUE, this, dialogues[dialogueIndex]);
			}
		}
	}
}
