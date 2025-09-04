package pokemon;

public class StatusEffect {
	
	public Status status;
	public int num;
	public Move move;
	
	public StatusEffect(Status status, int num, Move move) {
		this.status = status;
		this.num = num;
		this.move = move;
	}
	
	@Override
	public String toString() {
		return status.toString();
	}

	public String getSpunName() {
		switch(move) {
		case WRAP:
			return "being wrapped";
		case BIND:
			return "being bound";
		case WHIRLPOOL:
		case FIRE_SPIN:
		case MAGMA_STORM:
			return "the vortex";
		case INFESTATION:
			return "being infested";
		default:
			return "being spun";
		}
	}
}
