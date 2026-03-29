package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class HOFMachine extends InteractiveTile {
	
	BufferedImage[] images;
	static final int SIZE = 7;
	int phase;
	
	public HOFMachine(GamePanel gp) {
		super(gp);
		this.gp = gp;
		destructible = false;
		collision = true;
		getImage();
	}
	
	public void getImage() {
		images = new BufferedImage[SIZE];
		for (int i = 0; i < SIZE; i++) {
			images[i] = setup("/interactive/machine_" + i);
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
	}

}
