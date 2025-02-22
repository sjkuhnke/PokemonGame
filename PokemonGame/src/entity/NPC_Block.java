package entity;

import overworld.GamePanel;
import pokemon.*;

public class NPC_Block extends Entity {
	
	public NPC_Block(GamePanel gp, String name, String[] message, boolean more, int flag, String altDialogue) {
		super(gp, name);
		this.setDirection("down");
		
		this.flag = flag;
		this.more = more;
		this.altDialogue = altDialogue;
		
		getImage();
		setDialogue(message);
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
	
	public void speak(int mode) {
		if (!more || mode != 0) {
			super.speak(mode);
		} else {
			gp.setTaskState();
			if (!dialogues[dialogueIndex].isEmpty()) {
				Task.addTask(Task.DIALOGUE, this, dialogues[dialogueIndex]);
			}
		}
	}
}
