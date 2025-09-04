package object;

import overworld.GamePanel;

public class IceChunk extends InteractiveTile {
	GamePanel gp;
	
	public IceChunk(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/ice_chunk");
		destructible = false;
		collision = true;
	}
}
