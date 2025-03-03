package entity;

import overworld.GamePanel;
import pokemon.Item;
import pokemon.Task;

public class NPC_Star extends Entity {
	public NPC_Star(GamePanel gp) {
		super(gp, null);
		this.setDirection("down");
		
		getImage();
		setDialogue();
	}
	
	public void getImage() {
		down1 = setup("/npc/ace_trainer_m1");
		up1 = setup("/npc/ace_trainer_m2");
		left1 = setup("/npc/ace_trainer_m3");
		right1 = setup("/npc/ace_trainer_m4");
	}
	
	private void setDialogue() {
		dialogues[0] = Item.breakString("I've come here in search of rare Star Pieces. Have any to trade me?", 42);
	}
	
	public void speak(int mode) {
		super.speak(mode);
		gp.setTaskState();
		Task.addTask(Task.TEXT, dialogues[0]);
		Task.addTask(Task.STAR_PIECE, this, "");
		
		gp.ui.npc = this;
	}
}
