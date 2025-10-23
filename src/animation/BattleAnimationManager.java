package animation;

import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.*;

import pokemon.Move;

public class BattleAnimationManager {
	private static BattleAnimationManager instance;
	private Map<String, BattleAnimation> animations;
	private Map<String, BufferedImage> spriteCache;
	private BattleAnimation defaultAnimation;
	
	private BattleAnimationManager() {
		animations = new HashMap<>();
		spriteCache = new HashMap<>();
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
			
			if (frame.type == AnimationFrame.Type.EFFECT) {
				String spriteName = (String) frame.getProperty("sprite");
				if (spriteName != null && !spriteCache.containsKey(spriteName)) {
					loadSprite(spriteName);
				}
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
	
	private void loadSprite(String spriteName) {
		try {
			BufferedImage sprite = ImageIO.read(getClass().getResourceAsStream("/animation/" + spriteName + ".png"));
			spriteCache.put(spriteName, sprite);
		} catch (Exception e) {
			System.err.println("Failed to load sprite: " + spriteName);
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(String spriteName) {
		if (!spriteCache.containsKey(spriteName)) {
			loadSprite(spriteName);
		}
		return spriteCache.get(spriteName);
	}
	
	private void createDefaultAnimation() {
		defaultAnimation = new BattleAnimation("default");
		AnimationFrame shake = new AnimationFrame(
			AnimationFrame.Type.SHAKE, 
			AnimationFrame.Target.DEFENDER, 
			0, 300
		);
		shake.setProperty("intensity", 3);
		defaultAnimation.addFrame(shake);
	}
	
	public BattleAnimation getAnimation(Move move) {
		if (move == null) return defaultAnimation;
		
		String moveName = move.name().toLowerCase();
		
		// 1) specific move name
		if (animations.containsKey(moveName)) {
			return animations.get(moveName);
		}
		
		// 2) category + type
		String categoryType = move.getCategory().toLowerCase() + "_" + move.mtype.name().toLowerCase();
		if (animations.containsKey(categoryType)) {
			return animations.get(categoryType);
		}
		
		// 3) category only
		String category = move.getCategory().toLowerCase();
		if (animations.containsKey(category)) {
			return animations.get(category);
		}
		
		return defaultAnimation;
	}
}
