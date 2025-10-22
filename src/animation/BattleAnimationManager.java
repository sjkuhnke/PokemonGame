package animation;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

public class BattleAnimationManager {
	private static BattleAnimationManager instance;
	private Map<String, BattleAnimation> animations;
	private BattleAnimation defaultAnimation;
	
	private BattleAnimationManager() {
		animations = new HashMap<>();
		loadAnimations();
		createDefaultAnimation();
	}
	
	public static BattleAnimationManager getInstance() {
		if (instance == null) instance = new BattleAnimationManager();
		return instance;
	}
	
	private void loadAnimations() {
		try {
			Gson gson = new Gson();
			InputStreamReader reader = new InputStreamReader(BattleAnimationManager.class.getResourceAsStream("/animation/animations.json"), "UTF-8");
			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String moveName = entry.getKey();
				BattleAnimation anim = parseAnimation(entry.getValue().getAsJsonObject());
				animations.put(moveName.toLowerCase(), anim);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BattleAnimation parseAnimation(JsonObject json) {
		BattleAnimation anim = new BattleAnimation(json.get("type").getAsString());
		JsonArray frames = json.getAsJsonArray("frames");
		
		for (JsonElement frameElement : frames) {
			JsonObject frameJson = frameElement.getAsJsonObject();
			AnimationFrame frame = new AnimationFrame(
				AnimationFrame.Type.valueOf(frameJson.get("type").getAsString()),
				AnimationFrame.Target.valueOf(frameJson.get("target").getAsString()),
				frameJson.get("delay").getAsInt(),
				frameJson.get("duration").getAsInt()
			);
			
			JsonObject props = frameJson.getAsJsonObject("properties");
			for (Map.Entry<String, JsonElement> prop : props.entrySet()) {
				frame.setProperty(prop.getKey(), parseProperty(prop.getValue()));
			}
			
			anim.addFrame(frame);
		};
		
		return anim;
	}

	private Object parseProperty(JsonElement element) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive prim = element.getAsJsonPrimitive();
			if (prim.isNumber()) {
				return prim.getAsDouble();
			} else if (prim.isBoolean()) {
				return prim.getAsBoolean();
			} else {
				return prim.getAsString();
			}
		}
		return element.toString();
	}
	
	private void createDefaultAnimation() {
		// Simple slide forward animation as fallback
		defaultAnimation = new BattleAnimation("default");
		AnimationFrame slideForward = new AnimationFrame(
			AnimationFrame.Type.MOVE, 
			AnimationFrame.Target.ATTACKER, 
			0, 200
		);
		slideForward.setProperty("offsetX", 0.3);
		slideForward.setProperty("easing", "decel");
		
		AnimationFrame slideBack = new AnimationFrame(
			AnimationFrame.Type.MOVE,
			AnimationFrame.Target.ATTACKER,
			200, 200
		);
		slideBack.setProperty("offsetX", 0.0);
		slideBack.setProperty("easing", "linear");
		
		defaultAnimation.addFrame(slideForward);
		defaultAnimation.addFrame(slideBack);
	}
	
	public BattleAnimation getAnimation(String moveName) {
		return animations.getOrDefault(moveName.toLowerCase(), defaultAnimation);
	}
}
