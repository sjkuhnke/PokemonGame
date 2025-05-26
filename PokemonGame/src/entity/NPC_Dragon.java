package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class NPC_Dragon extends NPC_Block {
	
	private BufferedImage[] images;
	private static final int SIZE = 5;
	private int ticks = 0;
	private boolean awake;
	private int id;
	
	public NPC_Dragon(GamePanel gp, String name, int id, double scriptIndex, int flag) {
		super(gp, name, new String[] {""}, scriptIndex, flag, "");
		this.id = id;
		this.awake = false;
		getImages();
	}
	
	public void getImages() {
		images = new BufferedImage[SIZE];
		for (int i = 0; i < SIZE; i++) {
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
			
			image = awake ? images[spriteNum] : images[0];
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
		
		if (awake) {
			ticks++;
			if (ticks >= 12) {
				ticks = 0;
				spriteNum++;
				if (spriteNum > SIZE - 1) {
					spriteNum = 1;
				}
			}
		}
	}

}
