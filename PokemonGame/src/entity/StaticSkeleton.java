package entity;

public class StaticSkeleton {
	
	private int id;
	private int x;
	private int y;
	private int trainer;
	private String text;
	public boolean nuzlocke;
	
	public StaticSkeleton(int id, int x, int y, int trainer, String text) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.trainer = trainer;
		this.text = text;
	}
	
	public int id() {
		return id;
	}
	
	public int x() {
		return nuzlocke ? 49 : x;
	}
	
	public int y() {
		return nuzlocke ? 45 : y;
	}
	
	public int t() {
		return trainer;
	}
	
	public String d() {
		return text;
	}
}
