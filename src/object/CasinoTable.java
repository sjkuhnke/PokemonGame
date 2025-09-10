package object;

import java.awt.Rectangle;

import entity.Entity;
import entity.NPC_Dealer;
import overworld.GamePanel;
import tile.TileManager;

public class CasinoTable extends InteractiveTile {

	private Entity parent;
	private static Rectangle TOP_THREE_FOURTHS = TileManager.collisionRectangles[TileManager.TOP_THREE_FOURTHS];

	public CasinoTable(GamePanel gp, Entity parent) {
		super(gp);
		this.gp = gp;
		this.parent = parent;
		
		down1 = setup("/interactive/invisible");
		collision = true;
		this.solidArea = TOP_THREE_FOURTHS;
	}

	public NPC_Dealer getDealer() {
		return (NPC_Dealer) parent;
	}

}
