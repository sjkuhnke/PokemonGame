package overworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Optional per-node position overrides, so icons can be moved to match edits to the background art
 * without recompiling. Reads /worldmap/worldmap_nodes.csv from the classpath if it exists.
 *
 * File format, one node per line (anything after the third comma is ignored; # starts a comment):
 *     id,x,y,name
 * where x,y is the icon center in screen pixels (768 x 576). Written by WorldMapEditor.
 */
public final class WorldMapLayout {

	private WorldMapLayout() {}

	public static final String RESOURCE = "/worldmap/worldmap_nodes.csv";
	private static boolean loaded;

	/** Applies the classpath CSV once; silently does nothing if the file isn't there. */
	public static synchronized void loadOverrides() {
		if (loaded) return;
		loaded = true;
		try (InputStream in = WorldMapLayout.class.getResourceAsStream(RESOURCE)) {
			if (in != null) apply(new BufferedReader(new InputStreamReader(in, "UTF-8")));
		} catch (IOException e) {
			// keep the defaults from WorldMapData
		}
	}

	public static void apply(BufferedReader r) throws IOException {
		String line;
		while ((line = r.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) continue;
			String[] p = line.split(",");
			if (p.length < 3) continue;
			try {
				MapNode n = WorldMapData.get(Integer.parseInt(p[0].trim()));
				if (n != null) n.setAnchor(Integer.parseInt(p[1].trim()), Integer.parseInt(p[2].trim()));
			} catch (NumberFormatException e) {
				// skip malformed line
			}
		}
	}

	public static void save(File f) throws IOException {
		try (PrintWriter w = new PrintWriter(f, "UTF-8")) {
			w.println("# id,x,y,name  (icon centers in 768x576 screen pixels; written by WorldMapEditor)");
			for (MapNode n : WorldMapData.NODES) {
				w.println(n.id + "," + n.anchorX + "," + n.anchorY + "," + n.name);
			}
		}
	}
}