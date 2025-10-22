package animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationFrame {
	public enum Target { ATTACKER, DEFENDER, BOTH };
	public enum Type { MOVE, EFFECT, SHAKE, WAIT };
	
	public Type type;
	public Target target;
	public int delay; // ms before frame starts
	public int duration; // ms frame lasts
	private Map<String, Object> properties; // flex property storage
	
	public AnimationFrame(Type type, Target target, int delay, int duration) {
		this.type = type;
		this.target = target;
		this.delay = delay;
		this.duration = duration;
		this.properties = new HashMap<>();
	}
	
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	public Object getProperty(String key) {
		return properties.get(key);
	}

	public Object getPropertyOrDefault(String key, Object d) {
		return properties.containsKey(key) ? properties.get(key) : d;
	}
}
