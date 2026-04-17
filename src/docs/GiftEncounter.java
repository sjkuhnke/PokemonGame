package docs;

public class GiftEncounter {
	public enum GiftType { FIXED, TABLE, UNREGISTERED_BASE }
	
	public final int[] coordinates; // e.g. "Professor's Lab" or npc name
	public final GiftType type;
	public final int[] possibleIds; // empty if UNREGISTERED_BASE
	public final int level;
	public final String notes; // e.g. "Nuzlocke-aware", "Has hidden ability"

	public GiftEncounter(int[] coordinates, GiftType type, int[] possibleIds, int level, String notes) {
		this.coordinates = coordinates;
		this.type = type;
		this.possibleIds = possibleIds;
		this.level = level;
		this.notes = notes;
	}
}
