package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class E4Door extends InteractiveTile {
	
	boolean open;
	
	public E4Door(GamePanel gp, int type) {
		super(gp);
		this.gp = gp;
		down1 = setup("/interactive/e4_door" + type);
		down2 = setup("/interactive/e4_door" + (type + 1));
		
		open = false;
		destructible = false;
		collision = true;
	}
	
	@Override
	public void draw(Graphics2D g2) {		
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (worldX + gp.tileSize*3 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize*3 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*2 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*2 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			image = open ? down2 : down1;
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
	}
	
	public void open() {
		open = true;
		collision = false;
	}

}
