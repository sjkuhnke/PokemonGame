package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Torch extends InteractiveTile {
	
	GamePanel gp;
	int ticks;
	public int map;
	
	public Torch(GamePanel gp) {
		super(gp);
		this.gp = gp;
		down1 = setup("/interactive/torch1");
		down2 = setup("/interactive/torch2");
		down3 = setup("/interactive/torch0");
		
		destructible = false;
		collision = true;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		boolean dead = false;
		if (!gp.player.p.flag[7][9] && !gp.player.p.flag[7][10]) {
			dead = true;
			spriteNum = 3;
		}
		
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (worldX + gp.tileSize*3 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize*3 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*3 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*3 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			if (spriteNum == 1) image = down1;
			if (spriteNum == 2) image = down2;
			if (spriteNum == 3) image = down3;
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
		
		if (dead) return;
		
		ticks++;
		if (ticks >= 32) {
			ticks = 0;
			spriteNum++;
			if (spriteNum > 2) {
				spriteNum = 1;
			}
		}
	}
}
