package object;

import overworld.GamePanel;

public class PitEdge extends Pit {

	public PitEdge(GamePanel gp, Pit pit, int mode) {
		super(gp, pit.mapDest, pit.xDest, pit.yDest);
		
		switch (mode) {
		case 0:
			this.direction = "down";
			this.down1 = setup("/interactive/pit1");
			this.setCoords((pit.worldX / gp.tileSize), ((pit.worldY - gp.tileSize) / gp.tileSize));
			break;
		case 1:
			this.direction = "left";
			this.left1 = setup("/interactive/pit2");
			this.setCoords(((pit.worldX + gp.tileSize) / gp.tileSize), (pit.worldY / gp.tileSize));
			break;
		case 2:
			this.direction = "up";
			this.up1 = setup("/interactive/pit3");
			this.setCoords((pit.worldX / gp.tileSize), ((pit.worldY + gp.tileSize) / gp.tileSize));
			break;
		case 3:
			this.direction = "right";
			this.right1 = setup("/interactive/pit4");
			this.setCoords(((pit.worldX - gp.tileSize) / gp.tileSize), (pit.worldY / gp.tileSize));
			break;
		}
	}
}
