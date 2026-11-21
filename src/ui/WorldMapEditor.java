package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import overworld.MapNode;
import overworld.WorldMapData;
import overworld.WorldMapLayout;

/**
 * Dev tool (not used by the game): drag the map icons into place over your background art.
 *
 *   java ui.WorldMapEditor [output.csv]
 *
 * Mouse: click/drag an icon.  Arrows: nudge 1px (Shift = 8px).  Tab / Shift+Tab: next / previous node.
 * S: save to the CSV (default worldmap_nodes.csv in the working directory) - copy it into
 * resources/worldmap/ and the game picks it up without recompiling.
 * The editor shows the same graph the game draws, so connectors and route bars follow as you drag.
 */
public class WorldMapEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final WorldMapRenderer renderer = new WorldMapRenderer(WorldMapData.LAYOUT_W, WorldMapData.LAYOUT_H);
	private final boolean[] allUnlocked = new boolean[64];
	private final File out;
	private int selected = 0;
	private int tick;
	private String status = "";

	public WorldMapEditor(File out) {
		this.out = out;
		java.util.Arrays.fill(allUnlocked, true);
		setPreferredSize(new Dimension(WorldMapData.LAYOUT_W, WorldMapData.LAYOUT_H));
		setFocusable(true);
		MouseAdapter m = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
				int id = pick(e.getX(), e.getY());
				if (id >= 0) selected = id;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				move(selected, e.getX(), e.getY());
			}
		};
		addMouseListener(m);
		addMouseMotionListener(m);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int step = e.isShiftDown() ? 8 : 1;
				MapNode n = WorldMapData.get(selected);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT: move(selected, n.anchorX - step, n.anchorY); break;
				case KeyEvent.VK_RIGHT: move(selected, n.anchorX + step, n.anchorY); break;
				case KeyEvent.VK_UP: move(selected, n.anchorX, n.anchorY - step); break;
				case KeyEvent.VK_DOWN: move(selected, n.anchorX, n.anchorY + step); break;
				case KeyEvent.VK_TAB:
					int count = WorldMapData.NODES.length;
					selected = (selected + (e.isShiftDown() ? count - 1 : 1)) % count;
					break;
				case KeyEvent.VK_S: save(); break;
				default: break;
				}
				e.consume();
			}
		});
		new Timer(33, e -> {
			tick++;
			repaint();
		}).start();
		setFocusTraversalKeysEnabled(false);
	}

	private int pick(int x, int y) {
		int best = -1;
		long bestArea = Long.MAX_VALUE;
		for (MapNode n : WorldMapData.NODES) {
			Rectangle r = new Rectangle(renderer.getGraph().bounds(n.id));
			r.grow(6, 6);
			long area = (long) r.width * r.height;
			if (r.contains(x, y) && area < bestArea) {
				best = n.id;
				bestArea = area;
			}
		}
		return best;
	}

	void move(int id, int x, int y) {
		MapNode n = WorldMapData.get(id);
		n.setAnchor(Math.max(8, Math.min(WorldMapData.LAYOUT_W - 8, x)), Math.max(8, Math.min(WorldMapData.LAYOUT_H - 8, y)));
		renderer.getGraph().rebuild();
	}

	private void save() {
		try {
			WorldMapLayout.save(out);
			status = "Saved " + out.getAbsolutePath();
		} catch (IOException ex) {
			status = "Save failed: " + ex.getMessage();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("SansSerif", Font.BOLD, 28));
		renderer.draw(g2, selected, -1, allUnlocked, true, tick, (x, y, w, h) -> {
			g2.setColor(new Color(0, 0, 0, 215));
			g2.fillRoundRect(x, y, w, h, 25, 25);
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(4));
			g2.drawRoundRect(x + 5, y + 5, w - 10, h - 10, 15, 15);
		});
		MapNode n = WorldMapData.get(selected);
		g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, 420, 20);
		g2.setColor(Color.WHITE);
		g2.drawString(n.id + " " + n.name + " (" + n.anchorX + "," + n.anchorY + ")  S=save  " + status, 6, 14);
	}

	public static void main(String[] args) {
		final File out = new File(args.length > 0 ? args[0] : "worldmap_nodes.csv");
		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame("World map layout editor");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setContentPane(new WorldMapEditor(out));
			f.pack();
			f.setLocationRelativeTo(null);
			f.setVisible(true);
		});
	}
}