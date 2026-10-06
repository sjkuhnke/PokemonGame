package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class ShinySparkle {
	private final float x, y, size;
	private final long life, birth;

	public ShinySparkle(float x, float y, float size, long life, long birth) {
		this.x = x; this.y = y; this.size = size; this.life = life; this.birth = birth;
	}

	/** @return false once the sparkle has finished */
	public boolean draw(Graphics2D g2, long now) {
		float t = (now - birth) / (float) life;
		if (t >= 1f) return false;

		float pulse = (float) Math.sin(Math.PI * t); // grows, then shrinks
		float r = size * pulse;
		float cy = y - 14 * t;                       // slow upward drift
		int alpha = (int) (255 * Math.min(1f, pulse * 1.5f));

		g2.setColor(new Color(255, 225, 90, alpha)); // gold outer star
		g2.fill(star(x, cy, r, r * 0.22f));
		g2.setColor(new Color(255, 255, 255, alpha)); // white-hot core
		g2.fill(star(x, cy, r * 0.55f, r * 0.14f));
		return true;
	}

	private Path2D.Float star(float cx, float cy, float outer, float inner) {
		Path2D.Float p = new Path2D.Float();
		for (int i = 0; i < 8; i++) {
			double a = Math.PI / 4 * i - Math.PI / 2;
			float rad = (i % 2 == 0) ? outer : inner;
			float px = cx + (float) Math.cos(a) * rad;
			float py = cy + (float) Math.sin(a) * rad;
			if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
		}
		p.closePath();
		return p;
	}
}