package object;

import overworld.GamePanel;

public class Tree_Stump extends InteractiveTile {

	
	GamePanel gp;
	
	public Tree_Stump(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/interactive/cut_stump");
		destructible = true;
		collision = false;
	}
}
