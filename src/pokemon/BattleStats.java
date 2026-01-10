package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BattleStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int kills;
	public ArrayList<String> killList;
	private HashMap<Move, Integer> ppUsed;
	private boolean died;
	private String killer;
	private boolean evolved;
	private int evoID;
	private String evoName;
	private int switchIns;
	private int turns;
	private double damageDealt;
	private double damageTaken;
	
	public BattleStats(Pokemon p) {
		this.kills = 0;
		this.killList = new ArrayList<>();
		this.ppUsed = new HashMap<>();
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
	
	public void recordPPUse(Move move, int amt) {
		if (move == null || amt <= 0) return;
		ppUsed.merge(move, amt, Integer::sum);
	}
	public HashMap<Move, Integer> getPPUsed() { return ppUsed; }
	
	public void recordDamageDealt(double percent) { damageDealt += percent; }
	public double getDamageDealt() { return this.damageDealt; }
	
	public void recordDamageTaken(double percent) { damageTaken += percent; }
	public double getDamageTaken() { return this.damageTaken; }
	
	private String getPokemonString(Pokemon p) {
		if (p == null) return null;
		return p.getName() + " [" + (p.slot + 1) + "]";
	}
}
