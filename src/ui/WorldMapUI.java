package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import overworld.GamePanel;
import overworld.MapNode;
import overworld.Sound;
import overworld.WorldMapData;
import overworld.WorldMapNav;

/**
 * The world map as a menu sub-screen (UI.subState 6). Owns cursor state and input; drawing of the
 * map itself is delegated to WorldMapRenderer. Replaces the Swing PMap panel.
 */
public class WorldMapUI {

	private enum Mode { BROWSE, CONFIRM, MESSAGE }

	private final GamePanel gp;
	private final UI ui;
	private final WorldMapRenderer renderer;

	private boolean active;
	private Mode mode = Mode.BROWSE;
	private int selected;
	private int playerNode;
	private int confirmIndex; // 0 = Yes, 1 = No
	private String message;
	private int tick;
	private int framesOpen;

	public WorldMapUI(UI ui, GamePanel gp) {
		this.ui = ui;
		this.gp = gp;
		this.renderer = new WorldMapRenderer(gp.screenWidth, gp.screenHeight);
	}

	/** Call every frame while UI.subState == 6. */
	public void draw(Graphics2D g2) {
		if (!active) open();
		tick++;
		framesOpen++;

		Font baseFont = g2.getFont();
		boolean[] locations = gp.player.p.locations;
		boolean hasTeleporter = gp.player.p.flag[1][2];

		renderer.draw(g2, selected, playerNode, locations, hasTeleporter, tick,
				(x, y, w, h) -> ui.drawSubWindow(x, y, w, h));

		g2.setFont(baseFont);
		g2.setColor(Color.WHITE);

		// Ignore input for a couple of frames so the W press that opened the map can't also select a town.
		boolean acceptInput = framesOpen > 2;
		switch (mode) {
		case BROWSE:
			browse(acceptInput, locations, hasTeleporter);
			break;
		case CONFIRM:
			confirm(g2, baseFont, acceptInput);
			break;
		case MESSAGE:
			message(acceptInput);
			break;
		}
	}

	private void open() {
		int x = (int) Math.round(gp.player.worldX / (double) gp.tileSize);
		int y = (int) Math.round(gp.player.worldY / (double) gp.tileSize);
		playerNode = WorldMapData.resolve(gp.currentMap, x, y).id;
		selected = playerNode >= 0 ? playerNode : 0;
		mode = Mode.BROWSE;
		confirmIndex = 0;
		framesOpen = 0;
		clearKeys();
		active = true;
	}

	private void close() {
		active = false;
		ui.subState = 0;
		clearKeys();
	}

	private void clearKeys() {
		gp.keyH.wPressed = false;
		gp.keyH.sPressed = false;
		gp.keyH.dPressed = false;
		gp.keyH.upPressed = false;
		gp.keyH.downPressed = false;
		gp.keyH.leftPressed = false;
		gp.keyH.rightPressed = false;
	}

	private void browse(boolean acceptInput, boolean[] locations, boolean hasTeleporter) {
		MapNode sel = WorldMapData.get(selected);
		boolean warpReady = sel != null && hasTeleporter && sel.isUnlocked(locations);
		ui.drawToolTips(warpReady ? "Warp" : null, null, "Close", "Close");
		if (!acceptInput) return;

		int dir = -1;
		if (gp.keyH.upPressed) dir = WorldMapNav.UP;
		else if (gp.keyH.downPressed) dir = WorldMapNav.DOWN;
		else if (gp.keyH.leftPressed) dir = WorldMapNav.LEFT;
		else if (gp.keyH.rightPressed) dir = WorldMapNav.RIGHT;
		if (dir >= 0) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			int next = WorldMapNav.move(selected, dir);
			if (next != selected) {
				selected = next;
				gp.playSFX(Sound.S_MENU_1);
			}
			return;
		}

		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (warpReady) tryWarp(sel);
			return;
		}

		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.playSFX(Sound.S_MENU_CAN);
			close();
		}
	}

	private void tryWarp(MapNode node) {
		if (!gp.canFly()) {
			message = "A weird energy is rendering the teleporter\nobsolete here!";
			mode = Mode.MESSAGE;
			return;
		}
		confirmIndex = 0;
		mode = Mode.CONFIRM;
		gp.playSFX(Sound.S_MENU_CON);
	}

	/** Same look and controls as UI.drawConfirmWindow (Yes/No box plus the bottom prompt box). */
	private void confirm(Graphics2D g2, Font baseFont, boolean acceptInput) {
		MapNode node = WorldMapData.get(selected);
		ui.currentDialogue = "Warp to " + node.name + "?";
		ui.drawDialogueScreen(true);

		g2.setFont(baseFont);
		g2.setColor(Color.WHITE);
		int x = gp.tileSize * 11;
		int y = gp.tileSize * 4;
		int width = gp.tileSize * 3;
		int height = (int) (gp.tileSize * 2.5);
		ui.drawSubWindow(x, y, width, height);
		x += gp.tileSize;
		y += gp.tileSize;
		g2.drawString("Yes", x, y);
		if (confirmIndex == 0) g2.drawString(">", x - 24, y);
		y += gp.tileSize;
		g2.drawString("No", x, y);
		if (confirmIndex == 1) g2.drawString(">", x - 24, y);
		ui.drawToolTips("OK", null, "Back", "Back");
		if (!acceptInput) return;

		if (gp.keyH.upPressed || gp.keyH.downPressed) {
			gp.keyH.upPressed = false;
			gp.keyH.downPressed = false;
			confirmIndex = 1 - confirmIndex;
			gp.playSFX(Sound.S_MENU_1);
		} else if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (confirmIndex == 0) warp(node);
			else mode = Mode.BROWSE;
		} else if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			mode = Mode.BROWSE;
		}
	}

	private void message(boolean acceptInput) {
		ui.currentDialogue = message;
		ui.drawDialogueScreen(true);
		ui.drawToolTips("OK", null, "OK", null);
		if (!acceptInput) return;
		if (gp.keyH.wPressed || gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			mode = Mode.BROWSE;
		}
	}

	/** Same steps as the old PMap.fly() once the player answered Yes. */
	private void warp(MapNode node) {
		gp.eHandler.teleport(node.warpMap, node.warpCol, node.warpRow, false);
		for (Integer i : gp.tileM.getWaterTiles()) {
			gp.tileM.tile[i].collision = true;
		}
		for (Integer i : gp.tileM.getLavaTiles()) {
			gp.tileM.tile[i].collision = true;
		}
		close();
	}
}