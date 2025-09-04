package entity;

import java.util.Random;

import overworld.GamePanel;
import pokemon.Pokemon;

public class NPC_Pokemon extends Entity {
	int id;
	boolean spin;

	public NPC_Pokemon(GamePanel gp, int id, int t, boolean spin, String[] message) {
		super(gp, Pokemon.getName(id));
		
		this.setDirection("down");
		trainer = t;
		this.id = id;
		
		this.spin = spin;
		getImage(id);
		if (spin) turnRandom();
		setDialogue(message);
		
	}
	
	public void getImage(int id) {
		String path = "/overworlds/" + id;
		down1 = setup(path + "_0");
		left1 = setup(path + "_1");
		up1 = setup(path + "_2");
		right1 = setup(path + "_3");
		down2 = setup("/overworlds/invis");
	}
	
	public void turnRandom() {
		int direction = new Random().nextInt(4);
		boolean visible = new Random().nextBoolean();
		if (visible) {
			this.spriteNum = 1;
			switch (direction) {
			case 0:
				this.direction = "down";
				break;
			case 1:
				this.direction = "left";
				break;
			case 2:
				this.direction = "up";
				break;
			case 3:
				this.direction = "right";
				break;
			}
		} else {
			this.spriteNum = 2;
			this.direction = "down";
		}
		
	}
	
	public void facePlayer(String playerDirection) {
		this.spriteNum = 1;
		switch(playerDirection) {
			case "up":
				this.direction = "down";
				break;
			case "down":
				this.direction = "up";
				break;
			case "left":
				this.direction = "right";
				break;
			case "right":
				this.direction = "left";
				break;
		}
	}

}
