package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.imageio.ImageIO;

import overworld.GamePanel;
import pokemon.*;

public class Entity {
	
	public int worldX, worldY;
	public int speed;
	
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2, surf1, surf2, surf3, surf4, up3, up4, down3, down4, left3, left4, right3, right4;
	public String direction;
	public String defaultDirection;
	public GamePanel gp;
	
	public String name;

	public int spriteCounter = 0;
	public int spriteNum = 1;
	public boolean walkable;
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
	public int solidAreaDefaultX, solidAreaDefaultY;
	public boolean collisionOn = false;
	public boolean inTallGrass;
	
	public int trainer = -1;
	public boolean collision = true;
	public String dialogues[] = new String[20];
	int dialogueIndex = 0;
	public String altDialogue;
	public ArrayList<Item> inventory = new ArrayList<>();
	
	// Particle fields
	Color color;
	int size;
	int maxLife;
	
	public int height;
	
	public int flag = -1;
	private int spin;
	public boolean more;
	
	public Entity(GamePanel gp, String name) {
		this.gp = gp;
		height = gp.tileSize;
		this.name = name;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
		this.defaultDirection = direction;
	}
	
	public BufferedImage setup(String imageName) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/overworlds/invis.png"));
				if (!(this instanceof NPC_Pokemon)) {
					System.out.println(imageName + ".png is null!");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return image;
	}

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (worldX + gp.tileSize*2 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize*2 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*2 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*2 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			switch(direction) {
			case "up":
				if (spriteNum == 1) image = up1;
				if (spriteNum == 2) image = up2;
				if (spriteNum == 3) image = up3;
				if (spriteNum == 4) image = up4;
				break;
			case "down":
				if (spriteNum == 1) image = down1;
				if (spriteNum == 2) image = down2;
				if (spriteNum == 3) image = down3;
				if (spriteNum == 4) image = down4;
				break;
			case "left":
				if (spriteNum == 1) image = left1;
				if (spriteNum == 2) image = left2;
				if (spriteNum == 3) image = left3;
				if (spriteNum == 4) image = left4;
				break;
			case "right":
				if (spriteNum == 1) image = right1;
				if (spriteNum == 2) image = right2;
				if (spriteNum == 3) image = right3;
				if (spriteNum == 4) image = right4;
				break;
			}
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
		
	}
	
	public void speak(int mode) {
		if (mode == -1) {
			if (dialogues[dialogueIndex] == null) {
				dialogueIndex = 0;
			}
			gp.ui.currentDialogue = dialogues[dialogueIndex];
			dialogueIndex++;
		} else if (mode == 0) {
			if (dialogues[dialogueIndex] == null) {
				dialogueIndex = 0;
			}
			gp.setTaskState();
			Task.addTask(Task.DIALOGUE, this, dialogues[dialogueIndex]);
			dialogueIndex++;
		} else if (mode == 1) {
			gp.setTaskState();
			String[] altDialogues = altDialogue.split("\n");
			for (String s : altDialogues) {
				Task.addTask(Task.DIALOGUE, this, Item.breakString(s, 48));
			}
		}
	}
	
	public void setItems(boolean sort, Item... items) {
		inventory.clear();
		
		if (sort) Arrays.sort(items, Comparator.comparingInt(Item::getID));
		
		for (Item item : items) {
			if (!item.isTM() || !gp.player.p.hasTM(item.getMove())) inventory.add(item);
		}
	}
	
	public Color getParticleColor() {
		return null;
	}
	public int getParticleSize() {
		return 0;
	}
	public int getParticleSpeed() {
		return 0;
	}
	public int getParticleMaxLife() {
		return 0;
	}
	public void generateParticle(Entity generator) {
		Color color = generator.getParticleColor();
		int size = generator.getParticleSize();
		int speed = generator.getParticleSpeed();
		int maxLife = generator.getParticleMaxLife();
		
		for (int i = 0; i < 5; i++) {
			Particle p = new Particle(gp, generator, color, size, speed, maxLife);
			gp.particleList.add(p);
		}
	}
	public void generateParticle(int x, int y, Color color, int size, int speed, int maxLife) {
		for (int i = 0; i < 5; i++) {
			Particle p = new Particle(gp, x, y, color, size, speed, maxLife);
			gp.particleList.add(p);
		}
	}
	
	public int getFlagX() {
		return (flag >> 5) & 0xF;
	}
	
	public int getFlagY() {
		return flag & 0x1F;
	}
	
	public void setDialogue(String[] message) {
		for (int i = 0; i < message.length; i++) {
			dialogues[i] = message[i];
		}
	}
	
	public void setupImages(String name) {
		BufferedImage down = setup(name + "1");
		BufferedImage up = setup(name + "2");
		BufferedImage left = setup(name + "3");
		BufferedImage right = setup(name + "4");
		
		down1 = down;
		up1 = up;
		left1 = left;
		right1 = right;
	}
	
	public void setupImages(String name, boolean walkable) {
		down1 = setup(name + "1");
		up1 = setup(name + "2");
		left1 = setup(name + "3");
		right1 = setup(name + "4");
		
		this.walkable = walkable;
		down2 = setup(name + "1_1");
		up2 = setup(name + "2_1");
		left2 = setup(name + "3_1");
		right2 = setup(name + "4_1");
		down3 = setup(name + "1");
		up3 = setup(name + "2");
		left3 = setup(name + "3");
		right3 = setup(name + "4");
		down4 = setup(name + "1_2");
		up4 = setup(name + "2_2");
		left4 = setup(name + "3_2");
		right4 = setup(name + "4_2");
	}

	public void setSpin(int spin) {
		this.spin = spin;
	}
	
	public void spinRandom() {
		if (spin == 0) return;
		
		ArrayList<String> directions = new ArrayList<>();
		
		if ((spin & 1) != 0) directions.add("down");
		if ((spin & 2) != 0) directions.add("up");
		if ((spin & 4) != 0) directions.add("left");
		if ((spin & 8) != 0) directions.add("right");
		
		if (!directions.isEmpty()) {
			int index = new Random().nextInt(directions.size());
			String direction = directions.get(index);
			this.direction = direction;
		}
	}

	public void facePlayer(String playerDirection) {
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

	public void setName() {
		if (trainer < 0 || name != null) return;
		Trainer tr = Trainer.getTrainer(trainer);
		String name = tr.toString();
		String[] words = name.split(" ");
		if (words.length == 1) {
			this.name = words[0];
			return;
		}
		if (words[1].equals("Master")) {
			this.name = name;
			return;
		}
		this.name = words[words.length - 1];
		return;
	}
	
	public int getWorldY() {
		return worldY;
	}

	public String getName() {
		if (trainer > 0) {
			String[] words = Trainer.getTrainer(trainer).toString().split(" ");
			return words[words.length - 1];
		} else {
			return name;
		}
	}
}
