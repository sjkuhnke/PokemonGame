package pokemon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import pokemon.Pokemon.Task;

public class Field {
	
	public FieldEffect weather;
	public int weatherTurns;
	public FieldEffect terrain;
	public int terrainTurns;
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
		TRICK_ROOM(6, false, false),
		GRAVITY(6, false, false), 
		TAILWIND(5, false, false),
		STEALTH_ROCKS(-1, false, false),
		SPIKES(-1, false, false),
		TOXIC_SPIKES(-1, false, false),
		STICKY_WEBS(-1, false, false),
		SAFEGUARD(8, false, false),
		LUCKY_CHANT(8, false, false),
		WATER_SPORT(8, false, false),
		MUD_SPORT(8, false, false),
		;
		
		private Effect(int turns, boolean isWeather, boolean isTerrain) {
			this.turns = turns;
			this.isWeather = isWeather;
			this.isTerrain = isTerrain;
			
			String path = "/battle/" + super.toString().toLowerCase();
			path = isWeather ? path + ".gif" : path + "_terrain.png";
			image = setupImage(path);
		}
		
		public int turns;
		public boolean isWeather;
		public boolean isTerrain;
		
		private Image image;
		
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
		
		private Image setupImage(String path) {
			Image originalImage = null;

	        try {
	            originalImage = new ImageIcon(getClass().getResource(path)).getImage();
	        } catch (Exception e) {
	            try {
	                originalImage = ImageIO.read(getClass().getResourceAsStream("/items/null.png"));
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	        
	        if (isWeather) return originalImage;
	        
	        BufferedImage bufferedImage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	        Graphics2D g2d = bufferedImage.createGraphics();
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	        g2d.drawImage(originalImage, 0, 0, null);
	        g2d.dispose();

	        return bufferedImage;
		}
		
		public Image getImage() {
			return image;
		}
	}
	
	public class FieldEffect {
		public int turns;
		public Effect effect;
		public int layers;
		
		public FieldEffect(Effect effect) {
			this.effect = effect;
			turns = effect.turns;
			layers = 0;
		}
		
		@Override
		public String toString() {
			return effect.toString();
		}

		public String toLowerCaseString() {
			return effect.toString().toLowerCase();
		}

		public Color getColor() {
			switch (effect) {
			case AURORA_VEIL:
				return new Color(150, 217, 214);
			case ELECTRIC:
				return new Color(247, 208, 44);
			case GRASSY:
				return new Color(122, 199, 76);
			case GRAVITY:
				return new Color(138, 30, 106);
			case LIGHT_SCREEN:
				return new Color(249, 85, 135);
			case MUD_SPORT:
				return new Color(226, 191, 101);
			case PSYCHIC:
				return new Color(249, 85, 135);
			case RAIN:
				return new Color(99, 144, 240);
			case REFLECT:
				return new Color(249, 85, 135);
			case SAFEGUARD:
				return new Color(168, 167, 122);
			case LUCKY_CHANT:
				return new Color(188, 210, 232);
			case SANDSTORM:
				return new Color(182, 161, 54);
			case SNOW:
				return new Color(150, 217, 214);
			case SPARKLY:
				return new Color(254, 1, 77);
			case SPIKES:
				return new Color(226, 191, 101);
			case STEALTH_ROCKS:
				return new Color(182, 161, 54);
			case STICKY_WEBS:
				return new Color(166, 185, 26);
			case SUN:
				return new Color(238, 129, 48);
			case TAILWIND:
				return new Color(169, 143, 243);
			case TOXIC_SPIKES:
				return new Color(163, 62, 161);
			case TRICK_ROOM:
				return new Color(249, 85, 135);
			case WATER_SPORT:
				return new Color(99, 144, 240);
			default:
				return new Color(150, 217, 214);
			
			}
		}
	}
	
	public boolean setWeather(FieldEffect weather) {
		if (weather != null && weather.effect.isWeather) {
	        if (this.weather == null || this.weather.effect != weather.effect) {
	            Task t = Pokemon.addTask(Task.WEATHER, "The weather became " + weather.toString() + "!");
	            t.setEffect(weather);
	            this.weather = weather;
	            this.weatherTurns = weather.turns;
	            return true;
	        } else {
	            Pokemon.addTask(Task.TEXT, "The weather is already " + weather.toString() + "!");
	            return false;
	        }
	    } else {
	        Pokemon.addTask(Task.TEXT, "Invalid weather effect or weather object is null.");
	        return false;
	    }
	}
	
	public boolean setTerrain(FieldEffect terrain) {
		if (terrain != null && terrain.effect.isTerrain) {
	        if (this.terrain == null || this.terrain.effect != terrain.effect) {
	            Task t = Pokemon.addTask(Task.TERRAIN, "The terrain became " + terrain.toString() + "!");
	            t.setEffect(terrain);
	            this.terrain = terrain;
	            this.terrainTurns = terrain.turns;
	            return true;
	        } else {
	            Pokemon.addTask(Task.TEXT, "The terrain is already " + terrain.toString() + "!");
	            return false;
	        }
	    } else {
	        Pokemon.addTask(Task.TEXT, "Invalid terrain effect or terrain object is null.");
	        return false;
	    }
	}
	
	public void setEffect(FieldEffect effect) {
		if (effect.effect == Effect.TRICK_ROOM) {
			if (contains(fieldEffects, effect.effect)) {
				removeEffect(fieldEffects, effect.effect);
				Pokemon.addTask(Task.TEXT, "The twisted dimensions returned to normal!");
				return;
			}
		}
		if (!contains(fieldEffects, effect.effect)) {
			fieldEffects.add(effect);
			Pokemon.addTask(Task.TEXT, effect.toString() + " took effect!");
		} else {
			Pokemon.addTask(Task.TEXT, "But it failed!");
		}
	}
	
	public boolean setHazard(ArrayList<FieldEffect> side, FieldEffect hazard) {
		if (hazard.effect == Effect.STEALTH_ROCKS) {
			if (!contains(side, Effect.STEALTH_ROCKS)) {
				Pokemon.addTask(Task.TEXT, "Pointed rocks were scattered everywhere!");
				side.add(hazard);
				hazard.layers = 1;
				return true;
			} else {
				Pokemon.addTask(Task.TEXT, "But it failed!");
				return false;
			}
		} else if (hazard.effect == Effect.STICKY_WEBS) {
			if (!contains(side, Effect.STICKY_WEBS)) {
				Pokemon.addTask(Task.TEXT, "Sticky webs were scattered at the Pokemon's feet!");
				side.add(hazard);
				hazard.layers = 1;
			} else {
				Pokemon.addTask(Task.TEXT, "But it failed!");
				return false;
			}
			return true;
		} else if (hazard.effect == Effect.TOXIC_SPIKES) {
			int layers = getLayers(side, Effect.TOXIC_SPIKES);
			if (layers == 0) {
				Pokemon.addTask(Task.TEXT, "Poisonous Spikes were put at the Pokemon's feet!");
				side.add(hazard);
				addLayer(side, Effect.TOXIC_SPIKES);
				return true;
			} else if (layers == 1) {
				Pokemon.addTask(Task.TEXT, "Poisonous Spikes were put at the Pokemon's feet!");
				addLayer(side, Effect.TOXIC_SPIKES);
				return true;
			} else if (layers == 2) {
				Pokemon.addTask(Task.TEXT, "But it failed!");
				return false;
			}
			
		} else if (hazard.effect == Effect.SPIKES) {
			int layers = getLayers(side, Effect.SPIKES);
			if (layers == 0) {
				Pokemon.addTask(Task.TEXT, "Spikes were scattered at the Pokemon's feet!");
				side.add(hazard);
				addLayer(side, Effect.SPIKES);
				return true;
			} else if (layers == 1 || layers == 2) {
				Pokemon.addTask(Task.TEXT, "Spikes were scattered at the Pokemon's feet!");
				addLayer(side, Effect.SPIKES);
				return true;
			} else if (layers == 3) {
				Pokemon.addTask(Task.TEXT, "But it failed!");
				return false;
			}
			
		}
		return false;
	}

	public boolean contains(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) return true;
		}
		return false;
	}
	
	public boolean removeEffect(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) {
				side.remove(e);
				return true;
			}
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
	            Task t = Pokemon.addTask(Task.WEATHER, "The weather returned to normal!");
	            t.setEffect(null);
	            weather = null;
	        }
	    }
	    if (terrain != null) {
	        terrainTurns--;
	        if (terrainTurns == 0) {
	            Task t = Pokemon.addTask(Task.TERRAIN, "The terrain returned to normal!");
	            t.setEffect(null);
	            terrain = null;
	        }
	    }
	    
	    Iterator<FieldEffect> iterator = fieldEffects.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            Pokemon.addTask(Task.TEXT, effect.effect.toString() + " wore off!");
	            iterator.remove();
	        }
	    }
	    
	    iterator = playerSide.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            Pokemon.addTask(Task.TEXT, "Your " + effect.effect.toString() + " wore off!");
	            iterator.remove();
	        }
	    }
	    
	    iterator = foeSide.iterator();
	    while (iterator.hasNext()) {
	        FieldEffect effect = iterator.next();
	        if (effect.turns > 0) effect.turns--;
	        if (effect.turns == 0) {
	            Pokemon.addTask(Task.TEXT, "Foe's " + effect.effect.toString() + " wore off!");
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
	
	public ArrayList<FieldEffect> getScreens(ArrayList<FieldEffect> side) {
		ArrayList<FieldEffect> result = new ArrayList<>();
		for (FieldEffect fe : side) {
			if (fe.effect == Effect.REFLECT || fe.effect == Effect.LIGHT_SCREEN || fe.effect == Effect.AURORA_VEIL) result.add(fe);
		}
		return result;
	}
	
	/**
	 * A method solely used for checking if the AI should click Brick Break/Magic Reflect even if it isn't most damage
	 * @param side to check for screens, foe to check for magic reflect
	 * @return true if there is a screen or magic reflect up, false if not
	 */
	public boolean hasScreens(ArrayList<FieldEffect> side, Pokemon foe) {
		ArrayList<FieldEffect> result = getScreens(side);
		return !result.isEmpty() || foe.vStatuses.contains(Status.REFLECT);
	}
	
	public int getLayers(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) {
				return e.layers;
			}
		}
		return 0;
	}
	
	public void addLayer(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) {
				e.layers++;
				return;
			}
		}
	}
	
	public boolean remove(ArrayList<FieldEffect> side, Effect effect) {
		for (FieldEffect e : side) {
			if (e.effect == effect) {
				side.remove(e);
				return true;
			}
		}
		return false;
	}


}
