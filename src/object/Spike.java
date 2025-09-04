package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Spike extends InteractiveTile {
	
	GamePanel gp;
	
	public Spike(GamePanel gp, boolean toggle) {
		super(gp);
		
		this.gp = gp;
		down1 = setup("/interactive/spike1_1");
		down2 = setup("/interactive/spike1_2");
		up1 = setup("/interactive/spike2_1");
		up2 = setup("/interactive/spike2_2");
		
		destructible = false;
		collision = true;
		
		if (toggle) {
			this.toggle();
		} else {
			direction = "up";
		}
	}
	
	public Spike(GamePanel gp) {
		super(gp);
	}

	public void toggle() {
		if (this.spriteNum == 1) {
			this.spriteNum = 2;
			this.collision = false;
		} else if (this.spriteNum == 2) {
			this.spriteNum = 1;
			this.collision = true;
		}
	}

	@Override
	public void draw(Graphics2D g2) {	
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (worldX + gp.tileSize*3 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize*3 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*3 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*3 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			switch(direction) {
			case "down":
				if (spriteNum == 1) image = down1;
				if (spriteNum == 2) image = down2;
				break;
			case "up":
				if (spriteNum == 1) image = up1;
				if (spriteNum == 2) image = up2;
			}
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
	}

}
