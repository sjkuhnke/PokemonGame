package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Spike_Switch extends Spike {
	
	GamePanel gp;
	
	public Spike_Switch(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		down1 = setup("/interactive/spike_switch1");
		down2 = setup("/interactive/spike_switch2");
		
		destructible = false;
		collision = false;
	}
	
	@Override
	public void toggle() {
		if (this.spriteNum == 1) {
			this.spriteNum = 2;
		} else if (this.spriteNum == 2) {
			this.spriteNum = 1;
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
			
			if (spriteNum == 1) image = down1;
			if (spriteNum == 2) image = down2;
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
	}

}
