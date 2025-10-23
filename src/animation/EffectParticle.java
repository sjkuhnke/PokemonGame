package animation;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EffectParticle {
	private BufferedImage sprite;
	private double x, y;
	private double startX, startY;
	private double endX, endY;
	private long startTime;
	private int duration;
	private String easing;
	private double scale;
	private float opacity;
	
	public EffectParticle(BufferedImage sprite, double startX, double startY, double endX, double endY, int duration, String easing) {
		this.sprite = sprite;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.x = startX;
		this.y = startY;
		this.duration = duration;
		this.easing = easing;
		this.startTime = System.currentTimeMillis();
		this.scale = 1.0;
		this.opacity = 1.0f;
	}
	
	public boolean update() {
		long elapsed = System.currentTimeMillis() - startTime;
		if (elapsed >= duration) {
			return false;
		}
		
		double progress = Math.min(1.0, (double) elapsed / duration);
		progress = applyEasing(progress);
		
		x = startX + (endX - startX) * progress;
		y = startY + (endY - startY) * progress;
		
		if (progress > 0.7) {
			opacity = (float) (1.0 - ((progress - 0.7) / 0.3));
		}
		
		return true;
	}
	
	private double applyEasing(double t) {
		if (easing == null) return t;
		
		switch (easing.toLowerCase()) {
		case "linear": return t;
		case "decel": return 1 - Math.pow(1 - t, 2);
		case "accel": return t * t;
		case "ballistic": return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
		default: return t;
		}
	}
	
	public void draw(Graphics2D g2) {
		if (sprite == null) return;
		
		int width = (int) (sprite.getWidth() * scale);
		int height = (int) (sprite.getHeight() * scale);
		
		Composite oldComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		g2.drawImage(sprite, (int) (x - width / 2), (int) (y - height / 2), 
			width, height, null);
		
		g2.setComposite(oldComposite);
	}
}
