package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class ShinySparkle {
		private final float angle, startR, endR, spin, size;
		private final long life, birth;

		public ShinySparkle(float angle, float startR, float endR, float spin, float size, long life, long birth) {
			this.angle = angle; this.startR = startR; this.endR = endR;
			this.spin = spin; this.size = size; this.life = life; this.birth = birth;
		}

		/** @return false once the sparkle has finished */
		public boolean draw(Graphics2D g2, long now, int cx, int cy, int rx, int ry) {
			float t = (now - birth) / (float) life;
			if (t >= 1f) return false;
			float ageSec = (now - birth) / 1000f;

			// Spiral: orbit angle advances over time while the radius eases outward
			float eased = 1 - (1 - t) * (1 - t);
			float radius = startR + (endR - startR) * eased;
			double a = angle + spin * ageSec;
			float x = cx + (float) Math.cos(a) * radius * rx;
			float y = cy + (float) Math.sin(a) * radius * ry;

			float pulse = (float) Math.sin(Math.PI * t); // grows, then shrinks
			float r = size * pulse;
			int alpha = (int) (255 * Math.min(1f, pulse * 1.5f));
			double rot = spin * ageSec * 0.5;            // stars twirl as they orbit

			g2.setColor(new Color(255, 225, 90, alpha)); // gold outer star
			g2.fill(star(x, y, r, r * 0.22f, rot));
			g2.setColor(new Color(255, 255, 255, alpha)); // white-hot core
			g2.fill(star(x, y, r * 0.55f, r * 0.14f, rot));
			return true;
		}

		private Path2D.Float star(float cx, float cy, float outer, float inner, double rot) {
			Path2D.Float p = new Path2D.Float();
			for (int i = 0; i < 8; i++) {
				double a = Math.PI / 4 * i - Math.PI / 2 + rot;
				float rad = (i % 2 == 0) ? outer : inner;
				float px = cx + (float) Math.cos(a) * rad;
				float py = cy + (float) Math.sin(a) * rad;
				if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
			}
			p.closePath();
			return p;
		}
	}