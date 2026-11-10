package docs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Embeds each distinct image into the workbook once and hands back the same
 * picture index afterwards. POI's wb.addPicture(...) never dedupes, so without
 * this the same sprite/icon would be stored once per trainer, encounter row,
 * and Pokemon card.
 *
 * Keys are caller-chosen strings that uniquely identify the image, e.g.
 *   ImageCache.spriteKey(id, shiny)  -> "sprite:4" / "shiny:4"
 *   "item:" + item.name()            -> "item:POTION"
 *   "type:" + type.name()            -> "type:FIRE"
 *   "types:" + t1 + "/" + t2         -> combined two-icon image
 *   "npc:" + trainerId               -> trainer overworld sprite
 * The Supplier only runs on a cache miss, so expensive lookups and PNG
 * encoding are skipped for repeats.
 */
public class ImageCache {
	private final Workbook wb;
	private final Map<String, Integer> indices = new HashMap<>();

	public ImageCache(Workbook wb) {
		this.wb = wb;
	}

	public static String spriteKey(int id, boolean shiny) {
		return (shiny ? "shiny:" : "sprite:") + id;
	}

	/**
	 * @return the workbook picture index, or -1 if the supplier returned null
	 *         or the image couldn't be encoded (callers should just skip it).
	 */
	public int getOrAdd(String key, Supplier<BufferedImage> source) {
		Integer cached = indices.get(key);
		if (cached != null) return cached;

		BufferedImage image = source.get();
		if (image == null) {
			indices.put(key, -1);
			return -1;
		}
		try {
			byte[] bytes = DocUtils.imageToBytes(image, "png");
			int idx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			indices.put(key, idx);
			return idx;
		} catch (IOException e) {
			e.printStackTrace();
			indices.put(key, -1);
			return -1;
		}
	}

	public int size() {
		return indices.size();
	}
}