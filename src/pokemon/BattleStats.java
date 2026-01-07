package pokemon;

import java.io.Serializable;
import java.util.ArrayList;

public class BattleStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int kills;
	public ArrayList<String> killList;
	private boolean died;
	private String killer;
	private boolean evolved;
	private int evoID;
	private String evoName;
	private int switchIns;
	private int turns;
	
	public BattleStats(Pokemon p) {
		this.kills = 0;
		this.killList = new ArrayList<>();
		this.died = false;
		this.evolved = false;
		this.evoID = -1;
		this.evoName = null;
	}
	
	public void incrementKills(Pokemon killed) {
		kills++;
		killList.add(getPokemonString(killed));
	}
	public int getKills() { return kills; }
	
	public void setDied(boolean died) { this.died = died; }
	public boolean getDied() { return died; }
	public void setKiller(Pokemon killer) { this.killer = getPokemonString(killer); }
	public String getKiller() { return this.killer; }
	
	public void setEvolved(boolean evolved) { this.evolved = evolved; }
	public boolean getEvolved() { return this.evolved; }
	
	public void setEvoID(int id) { this.evoID = id; }
	public int getEvoID() { return this.evoID; }
	
	public void setEvoName(String name) { this.evoName = name; }
	public String getEvoName() { return evoName; }
	
	public void incrementSwitchIns() { switchIns++; }
	public int getSwitchIns() { return this.switchIns; }
	
	public void incrementTurns() { turns++; }
	public int getTurns() { return this.turns; }
	
	private String getPokemonString(Pokemon p) {
		if (p == null) return null;
		return p.getName() + " [" + (p.slot + 1) + "]";
	}
}
