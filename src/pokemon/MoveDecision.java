package pokemon;

import java.util.Collections;
import java.util.List;

import util.Pair;

public class MoveDecision {
	public final Move move;
	public final List<Pair<Status, Integer>> statusApplications;
	MoveDecision(Move move) {
		this(move, Collections.emptyList());
	}
	MoveDecision(Move move, Status status, int value) {
		this(move, Collections.singletonList(new Pair<>(status, value)));
	}
	MoveDecision(Move move, List<Pair<Status, Integer>> statusApplications){
		this.move = move;
		this.statusApplications = statusApplications;
	}
}