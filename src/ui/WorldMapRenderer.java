package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import overworld.MapNode;
import overworld.WorldMapData;
import overworld.WorldMapLayout;

/**
 * Draws the world map: your pixel-art background (worldmap_bg.png) with the connected graph of
 * towns, sites and routes drawn over it at runtime, then the selection brackets, player pin,
 * name tooltip and legend. Pure Graphics2D with no dependency on GamePanel/UI.
 */
public class WorldMapRenderer {

	/** Lets the host UI supply its own window frame (UI.drawSubWindow). */
	public interface WindowDrawer {
		void draw(int x, int y, int w, int h);
	}

	/** Classpath location of the background, next to /maps and /tiles. */
	public static final String BACKGROUND_PATH = "/worldmap/worldmap.png";

	private static final Color OUTLINE = new Color(12, 14, 22);
	private static final Color BAR = new Color(255, 255, 255);
	private static final Color ROUTE = new Color(255, 244, 178);
	private static final Color TOWN = new Color(255, 255, 255);
	private static final Color TOWN_INNER = new Color(150, 158, 172);
	private static final Color SITE = new Color(255, 204, 64);
	private static final Color SITE_INNER = new Color(176, 112, 24);
	private static final Color WARP = new Color(228, 52, 48);
	private static final Color PLAYER = new Color(255, 230, 0);

	private final int w, h;
	private final WorldMapGraph graph;
	private BufferedImage background;

	public WorldMapRenderer(int screenW, int screenH) {
		this.w = screenW;
		this.h = screenH;
		WorldMapLayout.loadOverrides();
		this.graph = new WorldMapGraph();
		try {
			InputStream in = getClass().getResourceAsStream(BACKGROUND_PATH);
			if (in != null) {
				background = ImageIO.read(in);
				in.close();
			}
		} catch (Exception e) {
			background = null;
		}
	}

	public WorldMapGraph getGraph() {
		return graph;
	}

	public void setBackground(BufferedImage img) {
		this.background = img;
	}

	public void draw(Graphics2D g2, int selected, int playerNode, boolean[] locations, boolean hasTeleporter,
			int tick, WindowDrawer win) {
		Font baseFont = g2.getFont();
		Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		AffineTransform oldTx = g2.getTransform();

		drawBackground(g2);

		MapNode sel = WorldMapData.get(selected);
		MapNode here = WorldMapData.get(playerNode);

		// Graph, cursor and pin are laid out in layout space (768x576) and scaled to the screen.
		g2.scale(w / (double) WorldMapData.LAYOUT_W, h / (double) WorldMapData.LAYOUT_H);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		drawGraph(g2, locations);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (sel != null) drawBrackets(g2, graph.bounds(sel.id), tick);
		if (here != null) drawPlayerIcon(g2, graph.bounds(here.id), tick);
		g2.setComposite(AlphaComposite.SrcOver);
		g2.setTransform(oldTx);

		g2.setFont(baseFont.deriveFont(24F));
		if (sel != null) drawTooltip(g2, sel, here != null && here.id == sel.id,
				hasTeleporter && sel.isUnlocked(locations), win);
		g2.setFont(baseFont.deriveFont(20F));
		drawLegend(g2, here != null, hasTeleporter, win);

		g2.setFont(baseFont);
		if (oldAA != null) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
	}

	private void drawBackground(Graphics2D g2) {
		if (background != null) {
			Object old = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(background, 0, 0, w, h, null);
			if (old != null) g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, old);
			return;
		}
		g2.setPaint(new Color(41, 98, 160)); // ocean-blue fallback when the PNG isn't on the classpath
		g2.fillRect(0, 0, w, h);
	}

	// ---------------------------------------------------------------- graph

	private static void box(Graphics2D g2, Rectangle r, Color fill) {
		g2.setColor(OUTLINE);
		g2.fillRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
		g2.setColor(fill);
		g2.fillRect(r.x, r.y, r.width, r.height);
	}

	private void drawGraph(Graphics2D g2, boolean[] locations) {
		// Connectors: all outlines first, then all fills, so junctions merge into one clean shape.
		g2.setColor(OUTLINE);
		for (Rectangle r : graph.connectors) g2.fillRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
		g2.setColor(BAR);
		for (Rectangle r : graph.connectors) g2.fillRect(r.x, r.y, r.width, r.height);

		for (MapNode n : WorldMapData.NODES) {
			if (n.type == MapNode.Type.ROUTE) box(g2, graph.bounds(n.id), ROUTE);
		}
		for (MapNode n : WorldMapData.NODES) {
			if (n.type == MapNode.Type.POI) {
				Rectangle r = graph.bounds(n.id);
				boolean unlocked = n.isUnlocked(locations);
				box(g2, r, unlocked ? WARP : SITE);
				g2.setColor(unlocked ? Color.WHITE : SITE_INNER);
				g2.fillRect(r.x + r.width / 2 - 3, r.y + r.height / 2 - 3, 6, 6);
			}
		}
		for (MapNode n : WorldMapData.NODES) {
			if (n.type == MapNode.Type.TOWN) {
				Rectangle r = graph.bounds(n.id);
				boolean unlocked = n.isUnlocked(locations);
				box(g2, r, unlocked ? WARP : TOWN);
				g2.setColor(unlocked ? Color.WHITE : TOWN_INNER);
				g2.fillRect(r.x + r.width / 2 - 4, r.y + r.height / 2 - 4, 8, 8);
			}
		}
	}

	/** Animated corner brackets around the selected icon. */
	private void drawBrackets(Graphics2D g2, Rectangle b, int tick) {
		int pad = 6 + (int) Math.round(Math.sin(tick * 0.15) * 1.5);
		int x0 = b.x - pad, y0 = b.y - pad, x1 = b.x + b.width + pad, y1 = b.y + b.height + pad;
		int arm = Math.max(6, Math.min(10, Math.min(b.width, b.height) / 2 + 3));
		for (int pass = 0; pass < 2; pass++) {
			g2.setColor(pass == 0 ? OUTLINE : PLAYER);
			g2.setStroke(new BasicStroke(pass == 0 ? 7f : 3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
			corner(g2, x0, y0, arm, arm);
			corner(g2, x1, y0, -arm, arm);
			corner(g2, x0, y1, arm, -arm);
			corner(g2, x1, y1, -arm, -arm);
		}
	}

	private void corner(Graphics2D g2, int x, int y, int dx, int dy) {
		Path2D p = new Path2D.Double();
		p.moveTo(x + dx, y);
		p.lineTo(x, y);
		p.lineTo(x, y + dy);
		g2.draw(p);
	}

	/** Map-pin marker whose tip rests on top of the node's icon, bobbing gently. */
	private void drawPlayerIcon(Graphics2D g2, Rectangle b, int tick) {
		int ax = b.x + b.width / 2;
		int bob = (int) Math.round(Math.sin(tick * 0.12) * 3);
		int tipY = b.y - 4 + bob;
		Polygon pin = new Polygon(new int[] { ax - 8, ax + 8, ax }, new int[] { tipY - 14, tipY - 14, tipY }, 3);
		g2.setColor(PLAYER);
		g2.fillOval(ax - 10, tipY - 32, 20, 20);
		g2.fillPolygon(pin);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawOval(ax - 10, tipY - 32, 20, 20);
		g2.fillOval(ax - 3, tipY - 25, 6, 6);
	}

	// ---------------------------------------------------------------- tooltip + legend

	private Rectangle screenBounds(MapNode n) {
		Rectangle r = graph.bounds(n.id);
		double sx = w / (double) WorldMapData.LAYOUT_W, sy = h / (double) WorldMapData.LAYOUT_H;
		return new Rectangle((int) Math.round(r.x * sx), (int) Math.round(r.y * sy),
				(int) Math.round(r.width * sx), (int) Math.round(r.height * sy));
	}

	private void drawTooltip(Graphics2D g2, MapNode n, boolean playerHere, boolean warpReady, WindowDrawer win) {
		String line2 = playerHere ? "You are here" : warpReady ? "Warp available" : null;
		FontMetrics fm = g2.getFontMetrics();
		int textW = fm.stringWidth(n.name);
		if (line2 != null) textW = Math.max(textW, fm.stringWidth(line2) * 3 / 4);
		int boxW = textW + 36;
		int boxH = (line2 == null ? 40 : 64);

		Rectangle b = screenBounds(n);
		int cx = b.x + b.width / 2;
		int x = Math.max(8, Math.min(w - boxW - 8, cx - boxW / 2));
		// Above the icon (clear of the player pin), unless that would leave the top of the screen.
		int y = b.y - boxH - 46;
		if (y < 8) y = b.y + b.height + 14;
		if (y + boxH > h - 8) y = h - boxH - 8;

		win.draw(x, y, boxW, boxH);
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.WHITE);
		g2.drawString(n.name, x + (boxW - fm.stringWidth(n.name)) / 2, y + 28);
		if (line2 != null) {
			g2.setFont(g2.getFont().deriveFont(18F));
			g2.setColor(warpReady && !playerHere ? new Color(255, 120, 120) : PLAYER);
			FontMetrics f2 = g2.getFontMetrics();
			g2.drawString(line2, x + (boxW - f2.stringWidth(line2)) / 2, y + 52);
		}
	}

	private void drawLegend(Graphics2D g2, boolean knowsPlayer, boolean hasTeleporter, WindowDrawer win) {
		int lw = (int) (w * 0.40), lh = (int) (h * 0.235);
		int x = w - lw - 14, y = h - lh - 14;
		win.draw(x, y, lw, lh);
		g2.setFont(g2.getFont().deriveFont(20F));
		int row1 = y + lh * 2 / 9 + 8, row2 = y + lh * 5 / 9 + 4, row3 = y + lh * 8 / 9 - 8;
		// player pin sample
		g2.setColor(PLAYER);
		g2.fillOval(x + 22, row1 - 22, 16, 16);
		g2.fillPolygon(new int[] { x + 24, x + 36, x + 30 }, new int[] { row1 - 12, row1 - 12, row1 + 2 }, 3);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawOval(x + 22, row1 - 22, 16, 16);
		g2.setColor(Color.WHITE);
		g2.drawString(knowsPlayer ? "= YOU ARE HERE" : "= YOU ARE ???", x + 56, row1);
		// warp box sample
		Rectangle s = new Rectangle(x + 21, row2 - 16, 18, 18);
		box(g2, s, WARP);
		g2.setColor(Color.WHITE);
		g2.fillRect(s.x + 5, s.y + 5, 8, 8);
		g2.drawString(hasTeleporter ? "= CAN WARP HERE" : "= VISITED HERE", x + 56, row2);
		// shape key
		g2.setFont(g2.getFont().deriveFont(16F));
		int kx = x + 22;
		Rectangle t = new Rectangle(kx, row3 - 12, 14, 14);
		box(g2, t, TOWN);
		g2.setColor(Color.WHITE);
		g2.drawString("TOWN", kx + 22, row3);
		kx += 92;
		Rectangle p = new Rectangle(kx, row3 - 11, 12, 12);
		box(g2, p, SITE);
		g2.setColor(Color.WHITE);
		g2.drawString("SITE", kx + 20, row3);
		kx += 84;
		Rectangle r = new Rectangle(kx, row3 - 9, 22, 8);
		box(g2, r, ROUTE);
		g2.setColor(Color.WHITE);
		g2.drawString("ROUTE", kx + 30, row3);
	}
}