package entity;

import overworld.GamePanel;
import pokemon.Pokemon;
import pokemon.Pokemon.Task;

public class NPC_Block extends Entity {
	
	boolean more;
	
	public NPC_Block(GamePanel gp, String name, String[] message, boolean more, int flag, String altDialogue) {
		super(gp, name);
		this.setDirection("down");;
		
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
			Pokemon.addTask(Task.DIALOGUE, this, dialogues[dialogueIndex]);
		}
	}
}
