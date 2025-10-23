package animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

public class StatParticle {
	private double x, y;
	private double startX, startY;
	private double endX, endY;
	private long startTime;
	private int duration;
	private boolean isBuff;
	private float opacity;
	private int size;
	
	public StatParticle(double startX, double startY, double endX, double endY, int duration, boolean isBuff) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.x = startX;
		this.y = startY;
		this.duration = duration;
		this.isBuff = isBuff;
		this.startTime = System.currentTimeMillis();
		this.opacity = 1.0f;
		this.size = (int) (Math.random() * 6 + 4); // Random size 4-10
	}
	
	public boolean update() {
		long elapsed = System.currentTimeMillis() - startTime;
		if (elapsed >= duration) {
			return false;
		}
		
		double progress = Math.min(1.0, (double) elapsed / duration);
		
		// Apply easing
		double easedProgress = 1 - Math.pow(1 - progress, 2);
		
		x = startX + (endX - startX) * easedProgress;
		y = startY + (endY - startY) * easedProgress;
		
		// Fade out
		opacity = (float) (1.0 - progress);
		
		return true;
	}
	
	public void draw(Graphics2D g2) {
		Composite oldComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		// Buff: Orange/Red gradient
		// Debuff: Blue gradient
		Color color;
		if (isBuff) {
			int red = 255;
			int green = (int) (100 + 155 * (1 - opacity));
			color = new Color(red, green, 0);
		} else {
			int blue = 255;
			int green = (int) (100 * (1 - opacity));
			color = new Color(0, green, blue);
		}
		
		g2.setColor(color);
		g2.fillOval((int) (x + size / 2), (int) (y + size / 2), size, size);
		
		g2.setComposite(oldComposite);
	}
}