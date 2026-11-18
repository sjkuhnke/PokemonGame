package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import overworld.MapNode;
import overworld.WorldMapData;

/**
 * Draws the world map (background, routes, POIs, towns, cursor, player icon, tooltip, legend)
 * with plain Graphics2D. No dependency on GamePanel/UI, so it can be rendered headlessly.
 */
public class WorldMapRenderer {

	/** Lets the host UI supply its own window frame (UI.drawSubWindow). */
	public interface WindowDrawer {
		void draw(int x, int y, int w, int h);
	}

	private static final Color BG_TOP = new Color(52, 84, 110);
	private static final Color BG_BOTTOM = new Color(30, 48, 70);
	private static final Color OUTLINE = new Color(25, 40, 55);
	private static final Color PATH = new Color(153, 255, 102);
	private static final Color TOWN = new Color(200, 212, 235);
	private static final Color WARP = new Color(225, 55, 55);
	private static final Color PLAYER = new Color(255, 230, 0);
	private static final Color CURSOR = new Color(255, 255, 255);

	private final int w, h;
	/** Optional pixel-art background (Phase 4). Loaded from /worldmap/worldmap_bg.png if present. */
	private BufferedImage background;

	public WorldMapRenderer(int screenW, int screenH) {
		this.w = screenW;
		this.h = screenH;
		try {
			java.io.InputStream in = getClass().getResourceAsStream("/worldmap/worldmap_bg.png");
			if (in != null) {
				background = ImageIO.read(in);
				in.close();
			}
		} catch (Exception e) {
			background = null;
		}
	}

	public void setBackground(BufferedImage img) {
		this.background = img;
	}

	public void draw(Graphics2D g2, int selected, int playerNode, boolean[] locations, boolean hasTeleporter,
			int tick, WindowDrawer win) {
		Font baseFont = g2.getFont();

		drawBackground(g2);

		// Layering: routes, then POIs, then towns, so towns always sit on top of the paths into them.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		for (MapNode.Type t : new MapNode.Type[] { MapNode.Type.ROUTE, MapNode.Type.POI, MapNode.Type.TOWN }) {
			for (MapNode n : WorldMapData.NODES) {
				if (n.type == t) drawNode(g2, n, locations);
			}
		}

		MapNode sel = WorldMapData.get(selected);
		MapNode here = WorldMapData.get(playerNode);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (sel != null) drawCursor(g2, sel, tick);
		if (here != null) drawPlayerIcon(g2, here, tick);
		g2.setComposite(AlphaComposite.SrcOver);

		g2.setFont(baseFont.deriveFont(24F));
		if (sel != null) drawTooltip(g2, sel, here != null && here.id == sel.id,
				hasTeleporter && sel.isUnlocked(locations), win);
		g2.setFont(baseFont.deriveFont(22F));
		drawLegend(g2, here != null, hasTeleporter, win);
		g2.setFont(baseFont);
	}

	private void drawBackground(Graphics2D g2) {
		if (background != null) {
			g2.drawImage(background, 0, 0, w, h, null);
			return;
		}
		g2.setPaint(new java.awt.GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM));
		g2.fillRect(0, 0, w, h);
	}

	private Color baseColor(MapNode n, boolean[] locations) {
		switch (n.biome) {
		case PATH: return PATH;
		case FOREST: return new Color(30, 140, 50);
		case MOUNTAIN: return new Color(150, 105, 40);
		case LAKE: return new Color(0, 170, 235);
		case CAVE: return new Color(95, 95, 100);
		case LAVA: return new Color(200, 50, 20);
		case TUNNEL: return new Color(232, 220, 140);
		case TOWER: return new Color(125, 90, 50);
		default: return n.isUnlocked(locations) ? WARP : TOWN;
		}
	}

	private static Color mix(Color a, Color b, double t) {
		return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
				(int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
				(int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
	}

	private void drawNode(Graphics2D g2, MapNode n, boolean[] locations) {
		Color base = baseColor(n, locations);
		boolean shaded = n.type != MapNode.Type.ROUTE;
		for (Rectangle r : n.getRects(w, h)) {
			if (shaded && r.height >= 8) {
				g2.setPaint(new LinearGradientPaint(0, r.y, 0, r.y + r.height, new float[] { 0f, 0.5f, 1f },
						new Color[] { base, mix(base, Color.WHITE, 0.55), mix(base, Color.BLACK, 0.25) }));
			} else {
				g2.setPaint(base);
			}
			g2.fillRect(r.x, r.y, r.width, r.height);
			g2.setPaint(OUTLINE);
			g2.setStroke(new BasicStroke(2f));
			g2.drawRect(r.x, r.y, r.width, r.height);
		}
	}

	private void drawCursor(Graphics2D g2, MapNode n, int tick) {
		float pulse = 0.8f + 0.2f * (float) Math.sin(tick * 0.15);
		g2.setComposite(AlphaComposite.SrcOver);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(8f));
		for (Rectangle r : n.getRects(w, h)) {
			g2.drawRect(r.x - 4, r.y - 4, r.width + 8, r.height + 8);
		}
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
		g2.setColor(CURSOR);
		g2.setStroke(new BasicStroke(4f));
		for (Rectangle r : n.getRects(w, h)) {
			g2.drawRect(r.x - 4, r.y - 4, r.width + 8, r.height + 8);
		}
	}

	private int[] screenAnchor(MapNode n) {
		int[] a = n.anchor();
		return new int[] { (int) Math.round(a[0] * w / (double) WorldMapData.LAYOUT_W),
				(int) Math.round(a[1] * h / (double) WorldMapData.LAYOUT_H) };
	}

	/** Map-pin marker whose tip rests on the node's anchor, bobbing gently. */
	private void drawPlayerIcon(Graphics2D g2, MapNode n, int tick) {
		int[] a = screenAnchor(n);
		int bob = (int) Math.round(Math.sin(tick * 0.12) * 3);
		int tipY = a[1] + 4 + bob;
		Polygon pin = new Polygon(new int[] { a[0] - 8, a[0] + 8, a[0] }, new int[] { tipY - 14, tipY - 14, tipY }, 3);
		g2.setColor(PLAYER);
		g2.fillOval(a[0] - 10, tipY - 32, 20, 20);
		g2.fillPolygon(pin);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawOval(a[0] - 10, tipY - 32, 20, 20);
		g2.setColor(OUTLINE);
		g2.fillOval(a[0] - 3, tipY - 25, 6, 6);
	}

	private void drawTooltip(Graphics2D g2, MapNode n, boolean playerHere, boolean warpReady, WindowDrawer win) {
		String line2 = playerHere ? "You are here" : warpReady ? "Warp available" : null;
		FontMetrics fm = g2.getFontMetrics();
		int textW = fm.stringWidth(n.name);
		if (line2 != null) textW = Math.max(textW, fm.stringWidth(line2) * 3 / 4);
		int boxW = textW + 36;
		int boxH = (line2 == null ? 40 : 64);

		Rectangle[] rs = n.getRects(w, h);
		Rectangle bounds = new Rectangle(rs[0]);
		for (Rectangle r : rs) bounds.add(r);
		int cx = bounds.x + bounds.width / 2;
		int x = Math.max(8, Math.min(w - boxW - 8, cx - boxW / 2));
		// Above the node, unless it would leave the top of the screen (the player pin also lives above).
		int y = bounds.y - boxH - 44;
		if (y < 8) y = bounds.y + bounds.height + 12;
		if (y + boxH > h - 8) y = h - boxH - 8;

		win.draw(x, y, boxW, boxH);
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		g2.drawString(n.name, x + (boxW - fm.stringWidth(n.name)) / 2, y + (line2 == null ? 28 : 28));
		if (line2 != null) {
			g2.setFont(g2.getFont().deriveFont(18F));
			g2.setColor(warpReady && !playerHere ? new Color(255, 120, 120) : PLAYER);
			FontMetrics f2 = g2.getFontMetrics();
			g2.drawString(line2, x + (boxW - f2.stringWidth(line2)) / 2, y + 52);
		}
	}

	private void drawLegend(Graphics2D g2, boolean knowsPlayer, boolean hasTeleporter, WindowDrawer win) {
		int x = (int) (w * 0.42), y = (int) (h * 0.745), lw = (int) (w * 0.40), lh = (int) (h * 0.2);
		win.draw(x, y, lw, lh);
		g2.setFont(g2.getFont().deriveFont(20F));
		g2.setColor(Color.WHITE);
		int row1 = y + lh / 4 + 8, row2 = y + lh * 3 / 4 + 4;
		// player pin sample
		g2.setColor(PLAYER);
		g2.fillOval(x + 22, row1 - 22, 16, 16);
		g2.fillPolygon(new int[] { x + 24, x + 36, x + 30 }, new int[] { row1 - 12, row1 - 12, row1 + 2 }, 3);
		g2.setColor(OUTLINE);
		g2.drawOval(x + 22, row1 - 22, 16, 16);
		g2.setColor(Color.WHITE);
		g2.drawString(knowsPlayer ? "= YOU ARE HERE" : "= YOU ARE ???", x + 56, row1);
		// warp sample
		g2.setColor(WARP);
		g2.fillRect(x + 20, row2 - 18, 20, 20);
		g2.setColor(OUTLINE);
		g2.drawRect(x + 20, row2 - 18, 20, 20);
		g2.setColor(Color.WHITE);
		g2.drawString(hasTeleporter ? "= CAN WARP HERE" : "= VISITED HERE", x + 56, row2);
	}
}