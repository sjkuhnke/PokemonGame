package pokemon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import overworld.GamePanel;

public class BattleRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String trainerName;
	private Pokemon[] startingTeam;
	private Map<Integer, BattleStats> battleStats;
	private long battleStartTime;
	private long battleEndTime;
	private boolean victory;
	private String gameVersion;
	
	public BattleRecord(String trainerName, Pokemon[] team) {
		this.trainerName = trainerName;
		this.battleStartTime = System.currentTimeMillis();
		this.gameVersion = GamePanel.GAME_VERSION;
		this.battleStats = new HashMap<>();
		
		this.startingTeam = new Pokemon[team.length];
		for (int i = 0; i < team.length; i++) {
			if (team[i] != null) {
				this.startingTeam[i] = team[i].clone();
				this.battleStats.put(team[i].slot, new BattleStats(team[i]));
			}
		}
	}
	
	public void recordKill(Pokemon killer, Pokemon killed) {
		BattleStats stats = battleStats.get(killer.slot);
		if (stats != null) {
			stats.incrementKills(killed);
		}
	}
	
	public void recordDeath(Pokemon deceased, Pokemon killer) {
		BattleStats stats = battleStats.get(deceased.slot);
		if (stats != null) {
			stats.setDied(true);
			stats.setKiller(killer);
		}
	}
	
	public void recordEvolution(Pokemon pokemon, int newID, String newName) {
		BattleStats stats = battleStats.get(pokemon.slot);
		if (stats != null) {
			stats.setEvolved(true);
			stats.setEvoID(newID);
			stats.setEvoName(newName);
		}
	}
	
	public void endBattle(boolean victory) {
		this.victory = victory;
		this.battleEndTime = System.currentTimeMillis();
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		if (gameVersion == null) gameVersion = GamePanel.GAME_VERSION;
		json.put("gameVersion", gameVersion);
		json.put("trainer", trainerName);
		json.put("victory", victory);
		json.put("battleStartTime", battleStartTime);
		json.put("battleEndTime", battleEndTime);
		
		JSONArray teamArray = new JSONArray();
		for (int i = 0; i < startingTeam.length; i++) {
			if (startingTeam[i] != null) {
				JSONObject pokemonJson = startingTeam[i].toJSON();
				
				BattleStats stats = battleStats.get(startingTeam[i].slot);
				if (stats != null) {
					pokemonJson.put("kills", stats.getKills());
					if (stats.getKills() > 0 && stats.killList != null) {
						JSONArray killListArray = new JSONArray();
						for (String kill : stats.killList) {
							killListArray.put(kill);
						}
						pokemonJson.put("killList", killListArray);
					}
					pokemonJson.put("died", stats.getDied());
					if (stats.getDied() && stats.getKiller() != null) {
						pokemonJson.put("killer", stats.getKiller());
					}
					pokemonJson.put("evolved", stats.getEvolved());
					if (stats.getEvolved()) {
						pokemonJson.put("evoID", stats.getEvoID());
						pokemonJson.put("evoName", stats.getEvoName());
					}
				}
				
				teamArray.put(pokemonJson);
			}
		}
		
		json.put("team", teamArray);
		return json;
	}

}
