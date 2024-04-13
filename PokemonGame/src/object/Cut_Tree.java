package object;

import overworld.GamePanel;

public class Cut_Tree extends InteractiveTile {

	
	GamePanel gp;
	
	public Cut_Tree(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		down1 = setup("/npc/cut_tree");
		destructible = true;
		collision = true;
	}
}
