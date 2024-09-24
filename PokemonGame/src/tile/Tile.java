package tile;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Tile {
	
	public BufferedImage image;
	public BufferedImage mask;
	public boolean collision = false;
	public String collisionDirection;
	public Rectangle collisionArea;
	public boolean drawAbove;

}
