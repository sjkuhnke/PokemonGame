package object;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import overworld.GamePanel;

public class Whirlpool extends InteractiveTile {
		GamePanel gp;
		
		
		public Whirlpool(GamePanel gp, int x, int y) {
			super(gp);
			this.gp = gp;
			
			down1 = setup("/interactive/whirlpool0");
			destructible = false;
			collision = true;
			
			setCoords(x, y);
			
		}
	
		public void setCoords(int x, int y) {
			this.worldX = gp.tileSize * x;
			this.worldY = gp.tileSize * y;
		}
		
		public BufferedImage setup(String imageName) {
			BufferedImage image = null;
			
			try {
				image = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
			} catch (Exception e) {
				System.out.println(imageName);
				e.printStackTrace();
			}
			return image;
		}
}
