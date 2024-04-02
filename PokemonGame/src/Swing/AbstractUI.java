package Swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Overworld.GamePanel;

public abstract class AbstractUI {
	
	public GamePanel gp;
	public Graphics2D g2;
	public String currentDialogue;
	public int commandNum;
	public Font marumonica;
	public int counter = 0;

	public abstract void showMessage(String message);
	
	public abstract void draw(Graphics2D g2);
	
	public void drawDialogueScreen(boolean above) {
		// WINDOW
		int x;
		int y;
		int width;
		int height;
		if (above) {
			x = gp.tileSize*2;
			y = gp.tileSize/2;
			width = gp.screenWidth - (gp.tileSize*4);
			height = gp.tileSize * 4;
		} else {
			x = 0;
			y = gp.screenHeight - (gp.tileSize*4);
			width = gp.screenWidth;
			height = gp.tileSize * 4;
		}
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,28F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}

	public void drawSubWindow(int x, int y, int width, int height) {
		Color background = new Color(0, 0, 0, 200);
		g2.setColor(background);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		Color border = new Color(255, 255, 255);
		g2.setColor(border);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
		
	}
	
	public int getTextX(String text) {
	    FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = (gp.screenWidth - length) / 2; // Calculate the x-coordinate for centering
	    return x;
	}
	
	public BufferedImage setup(String imageName, int scale) {
	    BufferedImage image = null;
	    
	    try {
	        // Load the original image
	        BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
	        
	        // Calculate the new dimensions based on the scale
	        int newWidth = originalImage.getWidth() * scale;
	        int newHeight = originalImage.getHeight() * scale;
	        
	        // Create a new BufferedImage with the scaled dimensions
	        image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	        
	        // Draw the original image onto the scaled image
	        Graphics2D g2d = image.createGraphics();
	        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
	        g2d.dispose();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return image;
	}
	
	public int getRightAlignedTextX(String text, int rightX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = rightX - length; // Calculate the x-coordinate for positioning
	    return x;
	}
	
	public int getCenterAlignedTextX(String text, int centerX) {
		FontMetrics metrics = g2.getFontMetrics(); // Assuming g2 is your Graphics2D object
	    int length = metrics.stringWidth(text); // Calculate the width of the text
	    int x = centerX - length / 2; // Calculate the x-coordinate for center alignment
	    return x;
	}
}
