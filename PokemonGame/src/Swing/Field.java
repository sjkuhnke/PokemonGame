package Swing;

import java.util.ArrayList;
import java.util.Iterator;

public class Field {
	
	FieldEffect weather;
	int weatherTurns;
	FieldEffect terrain;
	int terrainTurns;
	public ArrayList<FieldEffect> playerSide;
	public ArrayList<FieldEffect> foeSide;
	public ArrayList<FieldEffect> fieldEffects;
	
	public Field() {
		weather = null;
		playerSide = new ArrayList<>();
		foeSide = new ArrayList<>();
		fieldEffects = new ArrayList<>();
	}
	
	public enum Effect {
		SUN(5, true, false),
		RAIN(5, true, false),
		SANDSTORM(5, true, false),
		SNOW(5, true, false),
		GRASSY(5, false, true),
		ELECTRIC(5, false, true),
		PSYCHIC(5, false, true),
		SPARKLY(5, false, true), 
		REFLECT(5, false, false),
		LIGHT_SCREEN(5, false, false),
		AURORA_VEIL(5, false, false),
		TRICK_ROOM(5, false, false),
		GRAVITY(5, false, false), 
		TAILWIND(4, false, false),
		STEALTH_ROCKS(-1, false, false),
		SPIKES(-1, false, false),
		TOXIC_SPIKES(-1, false, false),
		STICKY_WEBS(-1, false, false),
		SAFEGUARD(5, false, false),
		WATER_SPORT(5, false, false),
		MUD_SPORT(5, false, false),
		;
		
		private Effect(int turns, boolean isWeather, boolean isTerrain) {
			this.turns = turns;
			this.isWeather = isWeather;
			this.isTerrain = isTerrain;
		}
		
		public int turns;
		public boolean isWeather;
		public boolean isTerrain;
		
		@Override
		public String toString() {
			String name = super.toString();
		    name = name.toLowerCase().replace('_', ' ');
		    String[] words = name.split(" ");
		    StringBuilder sb = new StringBuilder();
		    for (String word : words) {
		        sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		    }
		    return sb.toString().trim();
		}
	}
	
	public class FieldEffect {
		int turns;
		Effect effect;
		
		public FieldEffect(Effect effect) {
			this.effect = effect;
			turns = effect.turns;
		}
		
		@Override
		public String toString() {
			return effect.toString();
		}

		public String toLowerCaseString() {
			return effect.toString().toLowerCase();
		}
	}
	
	public void setWeather(FieldEffect weather) {
		if (weather.effect.isWeather) {
			if (this.weather == null) System.out.println("The weather became " + weather.toString() + "!");
			this.weather = weather;
			this.weatherTurns = weather.turns;
		}
	}
	
	public void setTerrain(FieldEffect terrain) {
		if (terrain.effect.isTerrain) {
			if (this.weather == null) System.out.println("The terrain became " + terrain.toString() + "!");
			this.terrain = terrain;
			this.terrainTurns = terrain.turns;
		}
	}
	
	public void setEffect(FieldEffect effect) {
		if (effect.effect == Effect.TRICK_ROOM) {
			if (fieldEffects.contains(effect)) {
				fieldEffects.remove(effect);
				System.out.println("The twisted dimensions returned to normal!");
			}
		}
		fieldEffects.add(effect);
	}

	public boolean contains(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) return true;
		}
		return false;
	}
	
	public boolean equals(FieldEffect fe, Effect e) {
		if (fe == null) return false;
		if (fe.effect == e) return true;
		return false;
	}
	
	public void endOfTurn() {
	    if (weather != null) {
	        weatherTurns--;
	        if (weatherTurns == 0) {
	            System.out.println("The weather returned to normal!");
	            weather = null;
	        }
	    }
	    if (terrain != null) {
	        terrainTurns--;
	        if (terrainTurns == 0) {
	            System.out.println("The terrain returned to normal!");
	            terrain = null;
	        }
	    }
	    
	    Iterator<FieldEffect> iterator = fieldEffects.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            System.out.println(effect.effect.toString() + " wore off!");
	            iterator.remove();
	        }
	    }
	    
	    iterator = playerSide.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            System.out.println("Your " + effect.effect.toString() + " wore off!");
	            iterator.remove();
	        }
	    }
	    
	    iterator = foeSide.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            System.out.println("Foe's " + effect.effect.toString() + " wore off!");
	            iterator.remove();
	        }
	    }
	}

	public ArrayList<FieldEffect> getHazards(ArrayList<FieldEffect> side) {
		ArrayList<FieldEffect> result = new ArrayList<>();
		for (FieldEffect fe : side) {
			if (fe.effect == Effect.STEALTH_ROCKS || fe.effect == Effect.SPIKES || fe.effect == Effect.TOXIC_SPIKES || fe.effect == Effect.STICKY_WEBS) result.add(fe);
		}
		return result;
	}


}
