package pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface RoleAssignable {
	ArrayList<Move> getMoves();
	ArrayList<Ability> getAbilities();
	ArrayList<Item> getItems();
	ArrayList<Nature> getNatures();
	int[] getBaseStats();
	
	public class Role {
		public static final int HAZARDS = 1 << 0;
		public static final int HAZARD_CONTROL = 1 << 1;
		public static final int CLERIC = 1 << 2;
		public static final int STATUS = 1 << 3;
		
		public static final int WALLBREAKER = 1 << 4;
		public static final int SETUP = 1 << 5;
		public static final int PRIORITY = 1 << 6;
		
		public static final int PHYSICAL_WALL = 1 << 7;
		public static final int SPECIAL_WALL = 1 << 8;
		public static final int MIXED_WALL = 1 << 9;
		public static final int PIVOT = 1 << 10;
		
		public static final int SNOW_SETTER = 1 << 11;
		public static final int SNOW_ABUSER = 1 << 12;
		public static final int RAIN_SETTER = 1 << 13;
		public static final int RAIN_ABUSER = 1 << 14;
		public static final int SAND_SETTER = 1 << 15;
		public static final int SAND_ABUSER = 1 << 16;
		public static final int SUN_SETTER = 1 << 17;
		public static final int SUN_ABUSER = 1 << 18;
		
		public static final int TRICK_ROOM_SETTER = 1 << 19;
		public static final int TRICK_ROOM_ABUSER = 1 << 20;
		
		public static String getRoleName(int role) {
		    Map<Integer, String> roleMap = new HashMap<>();
		    roleMap.put(HAZARDS, "Hazards");
		    roleMap.put(HAZARD_CONTROL, "Hazard Control");
		    roleMap.put(CLERIC, "Cleric");
		    roleMap.put(STATUS, "Status");
		    roleMap.put(WALLBREAKER, "Wallbreaker");
		    roleMap.put(SETUP, "Setup");
		    roleMap.put(PRIORITY, "Priority");
		    roleMap.put(PHYSICAL_WALL, "Physical Wall");
		    roleMap.put(SPECIAL_WALL, "Special Wall");
		    roleMap.put(MIXED_WALL, "Mixed Wall");
		    roleMap.put(PIVOT, "Pivot");
		    roleMap.put(SNOW_SETTER, "Snow Setter");
		    roleMap.put(SNOW_ABUSER, "Snow Abuser");
		    roleMap.put(RAIN_SETTER, "Rain Setter");
		    roleMap.put(RAIN_ABUSER, "Rain Abuser");
		    roleMap.put(SAND_SETTER, "Sand Setter");
		    roleMap.put(SAND_ABUSER, "Sand Abuser");
		    roleMap.put(SUN_SETTER, "Sun Setter");
		    roleMap.put(SUN_ABUSER, "Sun Abuser");
		    roleMap.put(TRICK_ROOM_SETTER, "Trick Room Setter");
		    roleMap.put(TRICK_ROOM_ABUSER, "Trick Room Abuser");
		    
		    return roleMap.getOrDefault(role, "Unknown Role");
		}
	}
	
	@SuppressWarnings("unused")
	public static int assignRole(RoleAssignable entity) {
		int role = 0;
		ArrayList<Move> moveset = entity.getMoves();
		ArrayList<Ability> abilities = entity.getAbilities();
		ArrayList<Item> items = entity.getItems();
		ArrayList<Nature> natures = entity.getNatures();
		int[] baseStats = entity.getBaseStats();
		
		// HAZARDS
		if (moveset.contains(Move.SPIKES) || moveset.contains(Move.STEALTH_ROCK) || moveset.contains(Move.ROCKFALL_FRENZY)
				|| moveset.contains(Move.STICKY_WEB) || moveset.contains(Move.TOXIC_SPIKES) || abilities.contains(Ability.TOXIC_DEBRIS)
				|| moveset.contains(Move.FLOODLIGHT)) {
			role |= Role.HAZARDS;
		}
		
		// HAZARD CONTROL
		if (moveset.contains(Move.DEFOG) || moveset.contains(Move.RAPID_SPIN) || moveset.contains(Move.MORTAL_SPIN)
				|| abilities.contains(Ability.MAGIC_BOUNCE)) {
			role |= Role.HAZARD_CONTROL;
		}
		
		// CLERIC
		if (moveset.contains(Move.HEALING_WISH) || moveset.contains(Move.AROMATHERAPY) || moveset.contains(Move.LUNAR_DANCE)
				|| moveset.contains(Move.WISH)) {
			role |= Role.CLERIC;
		}
		
		// STATUS TODO
		
		// WALLBREAKER TODO
		
		// SETUP SWEEPER TODO
		
		// PRIORITY
		for (Move m : moveset) {
			if (m.isAttack() && (m.priority >= 1 || m.mtype == PType.MAGIC && abilities.contains(Ability.SLEIGHT_OF_HAND)
					|| abilities.contains(Ability.AMBUSH))) {
				role |= Role.PRIORITY;
				break;
			}
		}
		
		// PHYSICAL WALL TODO
		
		// SPECIAL WALL TODO
		
		// PIVOT TODO
		
		// SNOW SETTER
		if (abilities.contains(Ability.SNOW_WARNING) || moveset.contains(Move.SNOWSCAPE)) {
            role |= Role.SNOW_SETTER;
        }
		
		// SNOW ABUSER
		if (abilities.contains(Ability.SLUSH_RUSH) || abilities.contains(Ability.SNOW_CLOAK) || abilities.contains(Ability.ICE_BODY)
				|| moveset.contains(Move.AURORA_VEIL)) {
            role |= Role.SNOW_ABUSER;
        }
		
		// RAIN SETTER
		if (abilities.contains(Ability.DRIZZLE) || moveset.contains(Move.RAIN_DANCE)) {
            role |= Role.RAIN_SETTER;
        }
		
		// RAIN ABUSER
		if (abilities.contains(Ability.SWIFT_SWIM) || abilities.contains(Ability.RAIN_DISH) || abilities.contains(Ability.DRY_SKIN)
				|| (!abilities.contains(Ability.NO_GUARD) && !abilities.contains(Ability.COMPOUND_EYES) && (moveset.contains(Move.HURRICANE)
				|| moveset.contains(Move.THUNDER)))) {
            role |= Role.RAIN_ABUSER;
        }
		
		// SAND SETTER
		if (abilities.contains(Ability.SAND_STREAM) || moveset.contains(Move.SANDSTORM)) {
            role |= Role.SAND_SETTER;
        }
		
		// SNOW ABUSER
		if (abilities.contains(Ability.SAND_RUSH) || abilities.contains(Ability.SAND_VEIL)
				|| moveset.contains(Move.SHORE_UP)) {
            role |= Role.SAND_ABUSER;
        }
		
		// SUN SETTER
		if (abilities.contains(Ability.DROUGHT) || moveset.contains(Move.SUNNY_DAY)
				|| moveset.contains(Move.SUNNY_DOOM)) {
            role |= Role.SUN_SETTER;
        }
		
		// SNOW ABUSER
		if (abilities.contains(Ability.CHLOROPHYLL) || abilities.contains(Ability.SOLAR_POWER)
				|| moveset.contains(Move.SOLAR_BEAM)) {
            role |= Role.SUN_ABUSER;
        }
		
		// TRICK ROOM SETTER
		if (abilities.contains(Ability.COSMIC_WARP) || moveset.contains(Move.TRICK_ROOM)) {
            role |= Role.TRICK_ROOM_SETTER;
        }
		
		// TRICK ROOM ABUSER TODO
        
        return role;
	}
}
