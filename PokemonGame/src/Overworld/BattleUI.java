package Overworld;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import Swing.AbstractUI;
import Swing.Pokemon;

public class BattleUI extends AbstractUI {
	
	public Pokemon user;
	public Pokemon foe;
	
	public int subState = 0;
	public int dialogueState = DIALOGUE_FREE_STATE;
	private int dialogueCounter = 0;

	public static final int STARTING_STATE = -1;
	public static final int IDLE_STATE = 0;
	public static final int USER_TURN = 1;
	public static final int FOE_TURN = 2;
	public static final int FOE_SENDING_IN_START = 3;
	public static final int USER_SENDING_IN_START = 4;
	public static final int FOE_SENDING_IN = 5;
	public static final int USER_SENDING_IN = 6;
	
	public static final int DIALOGUE_WAITING_STATE = 0;
	public static final int DIALOGUE_STATE = 1;
	public static final int DIALOGUE_FREE_STATE = 2;
	
	public BattleUI(GamePanel gp) {
		this.gp = gp;
		
		currentDialogue = "";
		commandNum = 0;
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/marumonica.ttf");
			marumonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void showMessage(String message) {
		if (dialogueState == DIALOGUE_FREE_STATE) dialogueState = DIALOGUE_WAITING_STATE;
		currentDialogue = message;
		dialogueCounter++;
		if (dialogueCounter >= 25) {
			dialogueCounter = 0;
			dialogueState = DIALOGUE_STATE;
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(marumonica);
		g2.setColor(Color.WHITE);
		
		g2.drawImage(setup("/battle/background", 1), 0, 0, gp.screenWidth, gp.screenHeight, null);
		
		// Sub states of the battle
		if (subState == STARTING_STATE) {
			showMessage("You are challenged by " + foe.trainer.getName());
		}
		if (subState == IDLE_STATE) {
			drawIdleScreen();
		}
		if (subState == FOE_SENDING_IN_START) {
			drawFoeStart();
			showSendOutText(foe);
		}
		if (subState == USER_SENDING_IN_START) {
			drawUserStart();
			showSendOutText(user);
		}
		
		// Dialogue States
		if (dialogueState == DIALOGUE_STATE) {
			drawDialogueState();
		}
		
		drawDialogueScreen(false);
//		String print = dialogueState == DIALOGUE_WAITING_STATE ? "Dialogue Waiting" : "Dialogue";
//		System.out.println(print);
	}
	
	private void showSendOutText(Pokemon p) {
		showMessage(p.trainer.getName() + " sends out " + p.nickname + "!");
	}
	
	private void drawIdleScreen() {
		drawHPImage(user);
		drawHPImage(foe);
		drawHPBar(user);
		drawHPBar(foe);
		drawNameLabel(user);
		drawNameLabel(foe);
		drawUserSprite();
		drawFoeSprite();
		drawUserHP();
	}

	private void drawHPImage(Pokemon p) {
		if (p.playerOwned()) {
			g2.drawImage(setup("/battle/user_hp", 1), 310, 288, null);
		} else {
			g2.drawImage(setup("/battle/foe_hp", 1), 222, 40, null);
		}		
	}

	private void drawUserHP() {
		g2.setColor(Color.BLACK);
		String hpLabel = user.getCurrentHP() + " / " + user.getStat(0);
		g2.drawString(hpLabel, this.getCenterAlignedTextX(hpLabel, 490), 363);
		
	}

	private void drawDialogueState() {
		int x = gp.screenWidth - gp.tileSize;
		int y = gp.screenHeight - gp.tileSize;
		int width = gp.tileSize / 2;
		int height = gp.tileSize / 2;
		g2.setColor(Color.WHITE);
		g2.drawPolygon(new int[] {x, (x + width), (x + width / 2)}, new int[] {y, y, y + height}, 3);
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			switch(subState) {
			case STARTING_STATE:
				subState = FOE_SENDING_IN_START;
				break;
			case FOE_SENDING_IN_START:
				subState = USER_SENDING_IN_START;
				break;
			case USER_SENDING_IN_START:
				subState = IDLE_STATE;
				break;
			}
			dialogueState = DIALOGUE_FREE_STATE;
		}
		
	}
	
	private void drawFoeStart() {
		drawFoePokeball();
		if (counter >= 100) {
			counter = 0;
			subState = USER_SENDING_IN_START;
		}
	}
	
	private void drawUserStart() {
		drawFoeSprite();
		drawHPBar(foe);
		drawNameLabel(foe);
		drawUserPokeball();
		if (counter >= 100) {
			counter = 0;
			subState = IDLE_STATE;
		}
	}

	private void drawFoePokeball() {
		counter++;
		if (counter < 50) {
			g2.drawImage(setup("/items/pokeball", 2), 570, 188, null);
		} else {
			drawFoeSprite();
			drawHPBar(foe);
			drawNameLabel(foe);
		}
	}
	
	private void drawUserPokeball() {
		counter++;
		if (counter < 50) {
			g2.drawImage(setup("/items/pokeball", 2), 133, 348, null);
		} else {
			drawUserSprite();
			drawHPBar(user);
			drawNameLabel(user);
			drawUserHP();
		}
	}
	
	private void drawFoeSprite() {
		g2.drawImage(getSprite(foe), 505, 70, null);
	}
	
	private void drawUserSprite() {
		g2.drawImage(getSprite(user), 70, 235, null);
	}

	private void drawHPBar(Pokemon p) {
		double hpRatio = p.currentHP / p.getStat(0);
		g2.setColor(getHPBarColor(hpRatio));
		int x;
		int y;
		int width = (int) (hpRatio * 120);
		int height = 8;
		if (p.playerOwned()) {
			x = 426;
			y = 330;
		} else {
			x = 319;
			y = 82;
		}
		g2.fillRect(x, y, width, height);
	}
	
	private void drawNameLabel(Pokemon p) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int x;
		int y;
		int levelX;
		int levelY;
		String name = p.nickname;
		
		if (p.playerOwned()) {
			x = getRightAlignedTextX(name, 490);
			y = 318;
			levelX = 524;
			levelY = 318;
		} else {
			x = getRightAlignedTextX(name, 383);
			y = 70;
			levelX = 415;
			levelY = 70;
		}
		g2.drawString(p.nickname, x, y);
		g2.drawString(p.level + "", levelX, levelY);
	}
	
	private Color getHPBarColor(double hpRatio) {
		if (hpRatio < 0.25) {
			return Color.red;
		} else if (hpRatio <= 0.5) {
			return Color.yellow;
		} else {
			return Color.green;
		}
	}

	private String getStateName() {
		switch (subState) {
		case STARTING_STATE: return "Starting State";
		case IDLE_STATE: return "Idle State";
		case USER_TURN: return "User Turn State";
		case FOE_TURN: return "Foe Turn State";
		case FOE_SENDING_IN_START: return "Foe Sending In Start State";
		case USER_SENDING_IN_START: return "User Sending In Start State";
		case FOE_SENDING_IN: return "Foe Sending In State";
		case USER_SENDING_IN: return "User Sending In State";
		default: return "Not added to getStateName() yet";
		}
		
	}
	
	private Image getSprite(Pokemon p) {
		Image originalSprite = p.getSprite();
		
		int scaledWidth = originalSprite.getWidth(null) * 2;
		int scaledHeight = originalSprite.getHeight(null) * 2;
		
		Image scaledImage = originalSprite.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		
		if (p.playerOwned()) scaledImage = flipHorizontal(scaledImage);
		
		return scaledImage;
	}

	private Image flipHorizontal(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = bufferedImage.createGraphics();

	    // Flip the image horizontally by drawing it with negative width
	    graphics.drawImage(image, image.getWidth(null), 0, -image.getWidth(null), image.getHeight(null), null);
	    graphics.dispose();

	    return bufferedImage;
	}
	
	private int getRightAlignedTextX(String text, int rightX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = rightX - length; // Calculate the x-coordinate for positioning
	    return x;
	}
	
	private int getCenterAlignedTextX(String text, int centerX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = centerX - length / 2; // Calculate the x-coordinate for center alignment
	    return x;
	}

}
