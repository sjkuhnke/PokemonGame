package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Painting extends InteractiveTile {
	
	String color;
	
	public Painting(GamePanel gp, String color) {
		super(gp);
		this.color = color;
		down1 = setup("/interactive/painting");
		down2 = setup("/interactive/red");
		down3 = setup("/interactive/orange");
		down4 = setup("/interactive/yellow");
		up1 = setup("/interactive/green");
		up2 = setup("/interactive/blue");
		up3 = setup("/interactive/purple");
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
			
			switch(color) {
			case "main":
				image = down1;break;
			case "red":
				image = down2;break;
			case "orange":
				image = down3;break;
			case "yellow":
				image = down4;break;
			case "green":
				image = up1;break;
			case "blue":
				image = up2;break;
			case "purple":
				image = up3;break;
			}
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
	}
	
	public boolean isColorPainting() {
		return !this.color.equals("main");
	}

	public void setColor(String color) {
		this.color = color;
	}

}
