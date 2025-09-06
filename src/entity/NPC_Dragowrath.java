package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class NPC_Dragowrath extends NPC_Block {
	
	BufferedImage[] images;
	static final int SIZE = 10;
	static final int COOLDOWN = 12;
	int ticks;
	
	public NPC_Dragowrath(GamePanel gp, String name, String[] message, double scriptIndex, int flag, String altDialogue) {
		super(gp, name, message, scriptIndex, flag, altDialogue);
		ticks = 0;
	}
	
	@Override
	public void getImage() {
		images = new BufferedImage[SIZE];
		for (int i = 0; i < SIZE; i++) {
			images[i] = setup("/overworlds/235_" + i);
		}
		down1 = images[0];
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
			
			image = images[spriteNum - 1];
			
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
			if (spriteNum > SIZE) {
				spriteNum = 1;
			}
		}
	}

}
