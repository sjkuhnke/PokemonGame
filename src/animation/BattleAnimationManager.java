package animation;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.*;

import pokemon.Move;
import pokemon.PType;

public class BattleAnimationManager {
	private static BattleAnimationManager instance;
	private Map<String, BattleAnimation> animations;
	private Map<String, BufferedImage> spriteCache;
	private BattleAnimation defaultAnimation;
	private BattleAnimation protectAnimation;
	
	private BattleAnimationManager() {
		animations = new HashMap<>();
		spriteCache = new HashMap<>();
		loadAnimations();
		createDefaultAnimation();
		createProtectAnimation();
	}
	
	public static BattleAnimationManager getInstance() {
		if (instance == null) instance = new BattleAnimationManager();
		return instance;
	}
	
	private void loadAnimations() {
		Gson gson = new Gson();
		
		for (int i = 0; i < PType.values().length; i++) {
			PType type = PType.values()[i];
			String spriteName = type.name().toLowerCase();
			try {
				InputStream stream = BattleAnimationManager.class.getResourceAsStream("/data/" + spriteName + ".json");
				if (stream == null) {
					System.out.println("Animation file not found: " + spriteName + ".json (skipping)");
				} else {
					InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
					JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
					
					for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
						String moveNames = entry.getKey();
						JsonObject moveJson = entry.getValue().getAsJsonObject();
						
						// Check if this move has charging/attacking phases
						if (moveJson.has("charging") && moveJson.has("attacking")) {
							// Parse both phases
							BattleAnimation chargingAnim = parseAnimation(moveJson.getAsJsonObject("charging"));
							BattleAnimation attackingAnim = parseAnimation(moveJson.getAsJsonObject("attacking"));
							
							String[] names = moveNames.split("/");
							for (String name : names) {
								String baseName = name.trim().toLowerCase();
								animations.put(baseName + "_charging", chargingAnim);
								animations.put(baseName + "_attacking", attackingAnim);
								// Also store the attacking as default for the move
								animations.put(baseName, attackingAnim);
							}
						} else {
							// Normal single-phase animation
							BattleAnimation anim = parseAnimation(moveJson);
							String[] names = moveNames.split("/");
							for (String name : names) {
								animations.put(name.trim().toLowerCase(), anim);
							}
						}
					}
					reader.close();
				}
			} catch (Exception e) {
				System.err.println("Error loading animation file: " + spriteName + ".json");
				e.printStackTrace();
			}
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
	
	private void createProtectAnimation() {
		protectAnimation = new BattleAnimation("protect");
		AnimationFrame barrier = new AnimationFrame(
			AnimationFrame.Type.EFFECT,
			AnimationFrame.Target.DEFENDER,
			0, 500
		);
		barrier.setProperty("sprite", "barrier");
		barrier.setProperty("startX", 0.0);
		barrier.setProperty("startY", 0.0);
		barrier.setProperty("endX", 0.0);
		barrier.setProperty("endY", 0.0);
		barrier.setProperty("towardsTarget", false);
		barrier.setProperty("easing", "linear");
		protectAnimation.addFrame(barrier);
		
		AnimationFrame shake = new AnimationFrame(
			AnimationFrame.Type.SHAKE,
			AnimationFrame.Target.DEFENDER,
			100, 200
		);
		shake.setProperty("intensity", 2);
		protectAnimation.addFrame(shake);
	}
	
	public BattleAnimation getProtectAnimation() {
		return protectAnimation;
	}
	
	public BattleAnimation getAnimation(Move move) {
		return getAnimation(move, null);
	}

	public BattleAnimation getAnimation(Move move, String phase) {
		if (move == null) return defaultAnimation;
		
		String moveName = move.name().toLowerCase();
		
		// if phase is specified, try to get that specific phase
		if (phase != null && !phase.isEmpty()) {
			String phasedKey = moveName + "_" + phase.toLowerCase();
			if (animations.containsKey(phasedKey)) {
				return animations.get(phasedKey);
			}
		}
		
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