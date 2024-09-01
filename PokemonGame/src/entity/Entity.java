package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import overworld.GamePanel;
import pokemon.Item;

public class Entity {
	
	public int worldX, worldY;
	public int speed;
	
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2, surf1, surf2, surf3, surf4;
	public String direction;
	GamePanel gp;

	public int spriteCounter = 0;
	public int spriteNum = 1;
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
	
	public int flag = -1;
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public BufferedImage setup(String imageName) {
		return setup(imageName, true);
	}
	
	public BufferedImage setup(String imageName, boolean setHitbox) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (setHitbox) {
			int height = image.getHeight() * gp.scale;
			int offset = height - gp.tileSize;
			
			solidArea = new Rectangle(0, 0 - offset, 48, height);
		}
		
		return image;
	}

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
			
			switch(direction) {
			case "up":
				image = up1;
				break;
			case "down":
				image = down1;
				break;
			case "left":
				image = left1;
				break;
			case "right":
				image = right1;
				break;
			case "up2":
				image = up2;
				break;
			case "down2":
				image = down2;
				break;
			case "left2":
				image = left2;
				break;
			case "right2":
				image = right2;
				break;
			}
			
			int height = image.getHeight() * gp.scale;
			int offset = height - gp.tileSize;
			
			g2.drawImage(image, screenX, screenY - offset, gp.tileSize, height, null);
		}
		
	}
	
	public void speak(int mode) {
		if (mode == 0) {
			if (dialogues[dialogueIndex] == null) {
				dialogueIndex = 0;
			}
			gp.ui.currentDialogue = dialogues[dialogueIndex];
			dialogueIndex++;
		} else if (mode == 1) {
			gp.ui.currentDialogue = altDialogue;
		}
	}
	
	public void setItems(Item... items) {
		inventory.clear();
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
}
