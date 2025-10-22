package animation;

import java.util.ArrayList;
import java.util.List;

public class BattleAnimation {
	public String animationType; // slide, shake, effect, etc
	public List<AnimationFrame> frames;
	public int duration;
	
	public BattleAnimation(String type) {
		this.animationType = type;
		this.frames = new ArrayList<>();
	}
	
	public void addFrame(AnimationFrame frame) {
		frames.add(frame);
	}
	
	public List<AnimationFrame> getFrames(){
		return frames;
	}
}
