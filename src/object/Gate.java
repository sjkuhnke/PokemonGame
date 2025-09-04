package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Gate extends InteractiveTile {
	
	GamePanel gp;
	
	public Gate(GamePanel gp) {
		super(gp);
		this.gp = gp;
		down1 = setup("/interactive/moss_gate");
		
		destructible = false;
		collision = true;
	}
	
	@Override
	public void draw(Graphics2D g2) {		
		BufferedImage image = down1;
		int checkX = worldX + gp.tileSize*2;
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;

		if (checkX + gp.tileSize*2 + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			checkX - gp.tileSize*2 + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize*3.1 + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize*3.1 + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			int width = image.getWidth() * gp.scale;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX, screenY - hOffset, width, height, null);
		}
		
	}
}
