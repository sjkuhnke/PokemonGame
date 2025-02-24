package object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import overworld.GamePanel;
import pokemon.Item;

public class ItemObj extends Entity {

	public BufferedImage image;
	public Item item;
	public int count;
	
	public ItemObj(GamePanel gp, Item item) {
		super(gp, null);
		
		if (item != null) {
			this.item = item;
			image = item.getImage();
		}
//		try {
//			image = ImageIO.read(getClass().getResourceAsStream("/interactive/item.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	public void draw(Graphics2D g2) {
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;
		
		if (worldX + gp.tileSize + gp.offsetX > gp.player.worldX - gp.player.screenX &&
			worldX - gp.tileSize + gp.offsetX < gp.player.worldX + gp.player.screenX &&
			worldY + gp.tileSize + gp.offsetY > gp.player.worldY - gp.player.screenY &&
			worldY - gp.tileSize + gp.offsetY < gp.player.worldY + gp.player.screenY) {
			
			//g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			
			g2.setColor(Color.WHITE);
			g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
			g2.drawImage(image, screenX + 12, screenY + 12, null);
			g2.setFont(g2.getFont().deriveFont(10F));
			g2.setColor(Color.BLACK);
			if (count > 1) g2.drawString("x" + count, screenX + 36, screenY + 40);
		}
	}
}
