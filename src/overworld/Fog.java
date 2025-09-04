package overworld;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Fog {
	
	GamePanel gp;
	BufferedImage fogFilter;
	int x;
	int y;
	int counter;
	Color[] color;
	float[] fraction;
	float alpha = 0.99f;
	int radius;
	double intensity;
	
	public Fog(GamePanel gp) {
		this.gp = gp;
		
		fogFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
		counter = 32;
		setIntensity(2);
	}
	
	public void draw(Graphics2D g2) {
		counter++;
		
		x = gp.player.screenX + (gp.tileSize/2);
		y = gp.player.screenY + (gp.tileSize/2);
		
		if (counter >= 24) {
			alpha += (Math.random() - 0.5f) * 0.05f;
			alpha = Math.max(0.8f, Math.min(alpha, 0.99f));
			fraction = new float[] { 0f, 0.2f, 1f };
			color = new Color[] {
				new Color(0.5f, 0.95f, 1f, alpha/10),  // transparent blue edge
			    new Color(0.6f, 0.9f, 1f, alpha/2.5f), // bluish mid glow
			    new Color(0.7f, 0.95f, 1f, alpha),   // bright white center
			};
			
			boolean i = new Random().nextBoolean();
			if (i) {
				if (radius < gp.screenHeight * intensity) {
					radius += Math.max(2, radius / 50);
				}
			} else {
				if (radius > 200) {
					radius -= Math.max(2, radius / 75);
				}
			}
			
			counter = 0;
		}
		
		RadialGradientPaint gPaint = new RadialGradientPaint(x, y, radius, fraction, color);
		g2.setPaint(gPaint);
		
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
	}
	
	// the higher the number, the less intense
	public void setIntensity(double intensity) {
		this.intensity = intensity;
		this.radius = (int) (500 * intensity);
	}
}
