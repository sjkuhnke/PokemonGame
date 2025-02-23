package pokemon;

public class StatusEffect {
	
	public Status status;
	public int num;
	
	public StatusEffect(Status status, int num) {
		this.status = status;
		this.num = num;
	}
	
	@Override
	public String toString() {
		return status.toString();
	}
}
