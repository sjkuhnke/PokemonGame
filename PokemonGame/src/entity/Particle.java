package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import overworld.GamePanel;

public class Particle extends Entity {
	
	Color color;
	int size;
	double xd;
	double yd;
	int maxLife;
	int life;
	public boolean alive;

	public Particle(GamePanel gp, Entity generator, Color color, int size, int speed, int maxLife) {
		this(gp, generator.worldX + gp.tileSize / 2 - size / 2, generator.worldY + gp.tileSize / 2 - size / 2, color, size, speed, maxLife);
	}
	
	public Particle(GamePanel gp, int x, int y, Color color, int size, int speed, int maxLife) {
		super(gp, null);
		
		this.color = color;
		this.size = size;
		this.speed = speed;
		this.maxLife = maxLife;
		this.xd = (Math.random() * 2 - 1) * 2;
		this.yd = Math.random() * 2 - 1;
		this.life = maxLife;
		this.alive = true;
		
		worldX = x;
		worldY = y;
	}
	
	public void update() {
		life--;
		
		if (life < maxLife / 3) {
			yd++;
		}
		
		worldX += xd*speed;
		worldY += yd*speed;
		
		if (life == 0) {
			alive = false;
		}
	}
	public void draw(Graphics2D g2) {
		update();
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		g2.setColor(color);
		g2.fillRect(screenX, screenY, size, size);
		
	}

}
