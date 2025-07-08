package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class NPC_Necrozma extends NPC_Dragowrath {
	static final int SIZE = 8;
	static final int COOLDOWN = 20;
	
	public NPC_Necrozma(GamePanel gp, String name, String[] message, double scriptIndex, int flag, String altDialogue) {
		super(gp, name, message, scriptIndex, flag, altDialogue);
	}
	
	@Override
	public void getImage() {
		images = new BufferedImage[SIZE];
		for (int i = 0; i < SIZE; i++) {
			images[i] = setup("/overworlds/290_" + i);
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (worldX + gp.tileSize*2 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize*2 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*2 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*2 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			int offset = 0;
			switch(direction) {
			case "down":
				offset = 0;break;
			case "up":
				offset = 1;break;
			case "left":
				offset = 2;break;
			case "right":
				offset = 3;break;
			}
			int index = 2 * offset + (spriteNum - 1);
			image = images[index];
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
		
		ticks++;
		if (ticks >= COOLDOWN) {
			ticks = 0;
			spriteNum++;
			if (spriteNum > SIZE / 4) {
				spriteNum = 1;
			}
		}
	}

}
