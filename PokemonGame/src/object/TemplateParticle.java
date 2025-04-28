package object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.Entity;
import overworld.GamePanel;

public class TemplateParticle extends Entity {
	
	Color baseColor;
	BufferedImage[] templates;
	int currentTemplate;
	double angle;
	double radius;
	double radiusSpeed;
	double angleSpeed;
	double initialAngleSpeed;
	int centerX, centerY;
	int maxLife;
	int life;
	public boolean alive;
	public int spawnDelay;
	public boolean started = false;
	
	public TemplateParticle(GamePanel gp, Entity generator, Color baseColor, String path) {
		this(gp, generator.worldX + gp.tileSize / 2, generator.worldY + gp.tileSize / 2, baseColor, path);
	}

	public TemplateParticle(GamePanel gp, int x, int y, Color baseColor, String path) {
		super(gp, null);
		
		centerX = x;
		centerY = y;
		this.baseColor = baseColor;
		
		speed = 5;
		maxLife = 60;
		life = maxLife;
		alive = true;
		
		angle = Math.random() * Math.PI * 2;
		radius = 0;
		radiusSpeed = speed + Math.random();
		initialAngleSpeed = 0.05 + Math.random() * 0.05;
		angleSpeed = initialAngleSpeed;
		
		int size = 3;
		templates = new BufferedImage[size];
		for (int i = 0; i < size; i++) {
			templates[i] = setup("/particles/" + path + "0" + (i+1));
		}
		
		currentTemplate = 2;
	}
	
	public void update() {
		if (!started) {
			spawnDelay--;
			if (spawnDelay <= 0) {
				started = true;
			} else {
				return;
			}
		}
		
		life--;
		
		radius += radiusSpeed;
		angleSpeed = (initialAngleSpeed * (1.0 / (1.0 + radius / 50.0)));
		angle += angleSpeed;
		
		worldX = (int) (centerX + radius * Math.cos(angle)) - 4;
		worldY = (int) (centerY + radius * Math.sin(angle)) - 4;
		
		if (life < maxLife * 2 / 3) currentTemplate = 1;
		if (life < maxLife * 1 / 3) currentTemplate = 0;
		
		if (life <= 0) {
			alive = false;
		}
	}
	
	public void draw(Graphics2D g2, float alpha) {
		update();
		if (!alive || !started) return;
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		BufferedImage img = templates[currentTemplate];
		if (img == null) return;
		
		Color drawColor = new Color(
				baseColor.getRed(),
				baseColor.getGreen(),
				baseColor.getBlue(),
				//(int) Math.min(255, Math.max(0, Math.min(alpha, life * 1.0 / maxLife) * 255))
				255
		);
		
		double cycle = (System.currentTimeMillis() / 100) % 3;
		if (cycle == 1) drawColor = drawColor.brighter();
		else if (cycle == 2) drawColor = drawColor.darker();
		
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				int pixel = img.getRGB(i, j);
				if ((pixel & 0x00FFFFFF) == 0x00FFFFFF) {
					g2.setColor(drawColor);
					g2.fillRect(screenX + (i*gp.scale), screenY + (j*gp.scale), gp.scale, gp.scale);
				}
			}
		}
	}

}
