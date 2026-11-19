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
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import overworld.MapNode;
import overworld.WorldMapData;

/**
 * Draws the world map: the pixel-art region background (worldmap_bg.png), town markers, the
 * selection spotlight and brackets, the player pin, the name tooltip and the legend.
 * Pure Graphics2D with no dependency on GamePanel/UI, so it can be rendered headlessly.
 */
public class WorldMapRenderer {

	/** Lets the host UI supply its own window frame (UI.drawSubWindow). */
	public interface WindowDrawer {
		void draw(int x, int y, int w, int h);
	}

	/** Classpath location of the background */
	public static final String BACKGROUND_PATH = "/worldmap/worldmap.png";

	private static final Color OUTLINE = new Color(20, 34, 52);
	private static final Color WARP = new Color(228, 52, 48);
	private static final Color LOCKED = new Color(96, 110, 132);
	private static final Color PLAYER = new Color(255, 230, 0);

	private final int w, h;
	private BufferedImage background;

	public WorldMapRenderer(int screenW, int screenH) {
		this.w = screenW;
		this.h = screenH;
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

	public void setBackground(BufferedImage img) {
		this.background = img;
	}

	public void draw(Graphics2D g2, int selected, int playerNode, boolean[] locations, boolean hasTeleporter,
			int tick, WindowDrawer win) {
		Font baseFont = g2.getFont();
		Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

		drawBackground(g2);

		MapNode sel = WorldMapData.get(selected);
		MapNode here = WorldMapData.get(playerNode);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (sel != null) drawSpotlight(g2, sel);
		for (MapNode n : WorldMapData.NODES) {
			if (n.type == MapNode.Type.TOWN || n.isWarpDestination()) drawMarker(g2, n, n.isUnlocked(locations));
		}
		if (sel != null) drawBrackets(g2, sel, tick);
		if (here != null) drawPlayerIcon(g2, here, tick);
		g2.setComposite(AlphaComposite.SrcOver);

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

	private int sx(int layoutX) {
		return (int) Math.round(layoutX * w / (double) WorldMapData.LAYOUT_W);
	}

	private int sy(int layoutY) {
		return (int) Math.round(layoutY * h / (double) WorldMapData.LAYOUT_H);
	}

	private Rectangle bounds(MapNode n) {
		Rectangle[] rs = n.getRects(w, h);
		Rectangle b = new Rectangle(rs[0]);
		for (Rectangle r : rs) b.add(r);
		return b;
	}

	/** Dims everything except the selected node so the eye lands on it. */
	private void drawSpotlight(Graphics2D g2, MapNode n) {
		Rectangle b = bounds(n);
		b.grow(5, 5);
		Area dim = new Area(new Rectangle(0, 0, w, h));
		dim.subtract(new Area(new java.awt.geom.RoundRectangle2D.Double(b.x, b.y, b.width, b.height, 12, 12)));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
		g2.setColor(new Color(8, 16, 34));
		g2.fill(dim);
		g2.setComposite(AlphaComposite.SrcOver);
	}

	/** Poke-ball style town marker: red when the warp is unlocked, slate gray otherwise. */
	private void drawMarker(Graphics2D g2, MapNode n, boolean unlocked) {
		int cx = sx(n.anchorX), cy = sy(n.anchorY), r = 7;
		g2.setColor(OUTLINE);
		g2.fillOval(cx - r - 1, cy - r - 1, 2 * r + 2, 2 * r + 2);
		g2.setColor(unlocked ? WARP : LOCKED);
		g2.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 180);
		g2.setColor(unlocked ? Color.WHITE : new Color(196, 206, 220));
		g2.fillArc(cx - r, cy - r, 2 * r, 2 * r, 180, 180);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawLine(cx - r, cy, cx + r, cy);
		g2.setColor(unlocked ? Color.WHITE : new Color(196, 206, 220));
		g2.fillOval(cx - 3, cy - 3, 6, 6);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(1.5f));
		g2.drawOval(cx - 3, cy - 3, 6, 6);
	}

	/** Animated corner brackets around the selected node's footprint. */
	private void drawBrackets(Graphics2D g2, MapNode n, int tick) {
		Rectangle b = bounds(n);
		int pad = 5 + (int) Math.round(Math.sin(tick * 0.15) * 1.5);
		int x0 = b.x - pad, y0 = b.y - pad, x1 = b.x + b.width + pad, y1 = b.y + b.height + pad;
		int arm = Math.max(6, Math.min(14, Math.min(b.width, b.height) / 2 + 4));
		for (int pass = 0; pass < 2; pass++) {
			g2.setColor(pass == 0 ? OUTLINE : Color.WHITE);
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

	/** Map-pin marker whose tip rests above the node's marker, bobbing gently. */
	private void drawPlayerIcon(Graphics2D g2, MapNode n, int tick) {
		int ax = sx(n.anchorX), ay = sy(n.anchorY) - 8;
		int bob = (int) Math.round(Math.sin(tick * 0.12) * 3);
		int tipY = ay + bob;
		Polygon pin = new Polygon(new int[] { ax - 8, ax + 8, ax }, new int[] { tipY - 14, tipY - 14, tipY }, 3);
		g2.setColor(PLAYER);
		g2.fillOval(ax - 10, tipY - 32, 20, 20);
		g2.fillPolygon(pin);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawOval(ax - 10, tipY - 32, 20, 20);
		g2.fillOval(ax - 3, tipY - 25, 6, 6);
	}

	private void drawTooltip(Graphics2D g2, MapNode n, boolean playerHere, boolean warpReady, WindowDrawer win) {
		String line2 = playerHere ? "You are here" : warpReady ? "Warp available" : null;
		FontMetrics fm = g2.getFontMetrics();
		int textW = fm.stringWidth(n.name);
		if (line2 != null) textW = Math.max(textW, fm.stringWidth(line2) * 3 / 4);
		int boxW = textW + 36;
		int boxH = (line2 == null ? 40 : 64);

		Rectangle b = bounds(n);
		int cx = b.x + b.width / 2;
		int x = Math.max(8, Math.min(w - boxW - 8, cx - boxW / 2));
		// Above the node (clear of the player pin), unless that would leave the top of the screen.
		int y = b.y - boxH - 44;
		if (y < 8) y = b.y + b.height + 12;
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
		int lw = (int) (w * 0.40), lh = (int) (h * 0.17);
		int x = w - lw - 14, y = h - lh - 14;
		win.draw(x, y, lw, lh);
		g2.setFont(g2.getFont().deriveFont(20F));
		int row1 = y + lh / 4 + 10, row2 = y + lh * 3 / 4 + 4;
		// player pin sample
		g2.setColor(PLAYER);
		g2.fillOval(x + 22, row1 - 22, 16, 16);
		g2.fillPolygon(new int[] { x + 24, x + 36, x + 30 }, new int[] { row1 - 12, row1 - 12, row1 + 2 }, 3);
		g2.setColor(OUTLINE);
		g2.drawOval(x + 22, row1 - 22, 16, 16);
		g2.setColor(Color.WHITE);
		g2.drawString(knowsPlayer ? "= YOU ARE HERE" : "= YOU ARE ???", x + 56, row1);
		// warp marker sample
		MapNode dummy = null;
		int cx = x + 30, cy = row2 - 7, r = 7;
		g2.setColor(OUTLINE);
		g2.fillOval(cx - r - 1, cy - r - 1, 2 * r + 2, 2 * r + 2);
		g2.setColor(WARP);
		g2.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 180);
		g2.setColor(Color.WHITE);
		g2.fillArc(cx - r, cy - r, 2 * r, 2 * r, 180, 180);
		g2.setColor(OUTLINE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawLine(cx - r, cy, cx + r, cy);
		g2.setColor(Color.WHITE);
		g2.drawString(hasTeleporter ? "= CAN WARP HERE" : "= VISITED HERE", x + 56, row2);
	}
}