package object;

import entity.Entity;
import entity.NPC_Dealer;
import overworld.GamePanel;

public class CasinoTable extends InteractiveTile {

	private Entity parent;

	public CasinoTable(GamePanel gp, Entity parent) {
		super(gp);
		this.gp = gp;
		this.parent = parent;
		
		down1 = setup("/interactive/invisible");
		collision = false;
	}

	public NPC_Dealer getDealer() {
		return (NPC_Dealer) parent;
	}

}
