package puzzle;

import object.InteractiveTile;
import object.Painting;
import overworld.GamePanel;
import pokemon.Item;

public class GamblingPuzzle extends Puzzle {
	
	private String[] bets = new String[] {"bj", "bj", "battle", "battle"};
	
	public GamblingPuzzle(GamePanel gp, int floor) {
		super(gp, floor);
	}

	@Override
	public void setup() {
		bets = shuffle(bets);
		InteractiveTile[] paintings = gp.iTile[floor];
		int index = 0;
		for (InteractiveTile i : paintings) {
			if (i != null) {
				Painting painting = (Painting) i;
				if (painting.isBetPainting()) painting.setColor(bets[index++]);
			}
		}
	}
	
	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public void update(Object obj) {
		int amt = (Integer) obj;
		if (isLocked()) {
			if (amt <= 0) lost = true;
			if (amt >= 100) isComplete = true;
		}
	}
	
	@Override
	public void sendToNext() {
		super.sendToNext();
		gp.player.p.bag.remove(Item.TEMPLE_ORB, 100);
	}

}
