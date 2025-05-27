package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class NPC_Dragon extends NPC_Block {
	
	private BufferedImage[] images;
	private int size;
	private int ticks = 0;
	private boolean awake;
	private int id;
	
	public NPC_Dragon(GamePanel gp, String name, int id, double scriptIndex, int flag, int size) {
		super(gp, name, new String[] {""}, scriptIndex, flag, "");
		this.id = id;
		this.size = size;
		this.awake = false;
		getImages();
	}
	
	public void getImages() {
		images = new BufferedImage[size];
		images[0] = setup("/overworlds/" + id + "_0_1");
		images[1] = setup("/overworlds/" + id + "_0_2");
		for (int i = 2; i < size; i++) {
			images[i] = setup("/overworlds/" + id + "_" + i);
		}
	}
	
	public void awake() {
		awake = true;
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
			
			image = awake ? images[spriteNum + 1] : images[spriteNum - 1];
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
		
		ticks++;
		if (awake) {
			if (ticks >= 18) {
				ticks = 0;
				spriteNum++;
				if (spriteNum > size - 2) {
					spriteNum = 1;
				}
			}
		} else {
			if (ticks >= 90) {
				ticks = 0;
				spriteNum++;
				if (spriteNum > 2) {
					spriteNum = 1;
				}
			}
		}
	}

}
