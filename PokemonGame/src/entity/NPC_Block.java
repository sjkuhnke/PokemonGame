package entity;

import overworld.GamePanel;
import pokemon.Pokemon;
import pokemon.Pokemon.Task;

public class NPC_Block extends Entity {
	
	boolean more;
	public int flag;
	
	public NPC_Block(GamePanel gp, String[] message, int flag, boolean more, String altDialogue) {
		super(gp);
		this.direction = "down";
		
		this.flag = flag;
		this.more = more;
		this.altDialogue = altDialogue;
		
		getImage();
		setDialog(message);
	}
	
	public void setDialog(String[] message) {
		for (int i = 0; i < message.length; i++) {
			dialogues[i] = message[i];
		}
	}
	
	public void getImage() {
		down1 = setup("/npc/block");
	}
	
	public void speak(int mode) {
		if (!more || mode != 0) {
			super.speak(mode);
		} else {
			gp.gameState = GamePanel.TASK_STATE;
			Pokemon.addTask(Task.TEXT, dialogues[dialogueIndex]);
		}
	}
}
