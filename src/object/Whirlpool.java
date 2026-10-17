package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import overworld.GamePanel;

public class Whirlpool extends InteractiveTile {
	GamePanel gp;
	BufferedImage[] images;
	int ticks;
	
	public Whirlpool(GamePanel gp, int x, int y) {
		super(gp);
		this.gp = gp;
		
		destructible = false;
		collision = true;
		images = new BufferedImage[6];
		
		setCoords(x, y);
	}
	
	public void setCoords(int x, int y) {
		this.worldX = gp.tileSize * x;
		this.worldY = gp.tileSize * y;
	}
	
	public void setupImages(String imageName) {
		for (int i = 0; i < 6; i++) {
			try {
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imageName + "_" + i + ".png"));
				images[i] = image;
			} catch (Exception e) {
				System.out.println(imageName);
				e.printStackTrace();
			}
		}
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
			
			image = images[spriteNum];
			g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
		}
		
		ticks++;
		if (ticks >= 4) {
			ticks = 0;
			spriteNum++;
			if (spriteNum > 5) {
				spriteNum = 0;
			}
		}
	}
}
