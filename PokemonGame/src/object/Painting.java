package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class Painting extends InteractiveTile {
	
	String color;
	boolean isColor;
	
	public Painting(GamePanel gp, String color) {
		super(gp);
		this.color = color;
		this.isColor = color == null;
		down1 = setup("/interactive/painting");
		down2 = setup("/interactive/red");
		down3 = setup("/interactive/orange");
		down4 = setup("/interactive/yellow");
		up1 = setup("/interactive/green");
		up2 = setup("/interactive/blue");
		up3 = setup("/interactive/purple");
		up4 = setup("/interactive/reset");
		left1 = setup("/interactive/bj_painting");
		left2 = setup("/interactive/battle_painting");
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
			
			if (color == null) System.out.println(String.format("%d, %d", worldX / gp.tileSize, worldY / gp.tileSize));
			
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
			case "reset":
				image = up4;break;
			case "bj":
				image = left1;break;
			case "battle":
				image = left2;break;
			}
			
			int width = image.getWidth() * gp.scale;
			int wOffset = (width - gp.tileSize) / 2;
			
			int height = image.getHeight() * gp.scale;
			int hOffset = height - gp.tileSize;
			
			g2.drawImage(image, screenX - wOffset, screenY - hOffset, width, height, null);
		}
	}
	
	public boolean isColorPainting() {
		return this.isColor;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return this.color;
	}

	public boolean isMainPainting() {
		return this.color.equals("main");
	}
	
	public boolean isResetPainting() {
		return this.color.equals("reset");
	}
	
	public boolean isBetPainting() {
		return this.color.equals("bet") || this.color.equals("bj") || this.color.equals("battle");
	}

}
