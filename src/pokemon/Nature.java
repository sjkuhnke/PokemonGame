package pokemon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum Nature {
	ADAMANT(1.1,1.0,0.9,1.0,1.0),
	BASHFUL(1.0,1.0,1.0,1.0,1.0),
    BOLD(0.9,1.1,1.0,1.0,1.0),
    BRAVE(1.1,1.0,1.0,1.0,0.9),
    CALM(0.9,1.0,1.0,1.1,1.0),
    CAREFUL(1.0,1.0,0.9,1.1,1.0),
    DOCILE(1.0,1.0,1.0,1.0,1.0),
    GENTLE(1.0,0.9,1.0,1.1,1.0),
    HARDY(1.0,1.0,1.0,1.0,1.0),
    HASTY(1.0,0.9,1.0,1.0,1.1),
    IMPISH(1.0,1.1,0.9,1.0,1.0),
    JOLLY(1.0,1.0,0.9,1.0,1.1),
    LAX(1.0,1.1,1.0,0.9,1.0),
    LONELY(1.1,0.9,1.0,1.0,1.0),
    MILD(1.0,0.9,1.1,1.0,1.0),
    MODEST(0.9,1.0,1.1,1.0,1.0),
    NAIVE(1.0,1.0,1.0,0.9,1.1),
    NAUGHTY(1.1,1.0,1.0,0.9,1.0),
    QUIET(1.0,1.0,1.1,1.0,0.9),
    QUIRKY(1.0,1.0,1.0,1.0,1.0),
    RASH(1.0,1.0,1.1,0.9,1.0),
    RELAXED(1.0,1.1,1.0,1.0,0.9),
    SASSY(1.0,1.0,1.0,1.1,0.9),
    SERIOUS(1.0,1.0,1.0,1.0,1.0),
    TIMID(0.9,1.0,1.0,1.0,1.1);
	
	private double[] stats;
	
	private static final Map<String, Nature> STAT_TO_NATURE = new HashMap<>();
	private static final Map<String, Nature> NAME_TO_NATURE = new HashMap<>();
	
	static {
		for (Nature nature : values()) {
			STAT_TO_NATURE.put(Arrays.toString(nature.stats), nature);
			NAME_TO_NATURE.put(nature.toString(), nature);
		}
	}

	Nature(double... stats) {
		this.stats = stats;
	}
	
	public double[] getStats() {
		return this.stats;
	}
	
	public double getStat(int index) {
		return this.stats[index];
	}
	
	public static Nature getRandomNature() {
		Random rand = new Random();
		Nature[] values = values();
		return values[rand.nextInt(values.length)];
	}
	
	public static Nature getRandomNature(long seed) {
		Random rand = new Random(seed);
		Nature[] values = values();
		return values[rand.nextInt(values.length)];
	}
	
	@Override
	public String toString() {
		String name = super.toString().toLowerCase();
	    StringBuilder sb = new StringBuilder();
	    sb.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1));
	    return sb.toString();
	}
	
	public static Nature getEnum(String string) {
		// Normalize the string
	    String normalized = string.toUpperCase();
	    
	    try {
	        return Nature.valueOf(normalized);
	    } catch (IllegalArgumentException e) {
	        throw new IllegalStateException("No matching Move enum found for string: " + string, e);
	    }
	}
	
	public static Nature getByName(String name) {
		return NAME_TO_NATURE.get(name);
	}
	
	public static Nature getByStats(double[] stats) {
		return STAT_TO_NATURE.get(Arrays.toString(stats));
	}
	
	public boolean raisesStat(int stat) {
		return stats[stat] == 1.1;
	}
	
	public boolean raisesAtk() {
		return raisesStat(0);
	}
	
	public boolean raisesDef() {
		return raisesStat(1);
	}
	
	public boolean raisesSpA() {
		return raisesStat(2);
	}
	
	public boolean raisesSpD() {
		return raisesStat(3);
	}
	
	public boolean raisesSpe() {
		return raisesStat(4);
	}
	
	public boolean lowersStat(int stat) {
		return stats[stat] == 0.9;
	}
	
	public boolean lowersAtk() {
		return lowersStat(0);
	}
	
	public boolean lowersDef() {
		return lowersStat(1);
	}
	
	public boolean lowersSpA() {
		return lowersStat(2);
	}
	
	public boolean lowersSpD() {
		return lowersStat(3);
	}
	
	public boolean lowersSpe() {
		return lowersStat(4);
	}
}
