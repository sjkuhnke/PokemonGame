package pokemon;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.PlayerCharacter;

public class Egg extends Pokemon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int steps;
	
	public Egg(int i, int steps) {
		super(i, 1, true, false);
		
		this.nickname = "Egg";
		
		this.steps = steps;
		
		setSprites();
	}
	
	@Override
	public void setSprites() {
		sprite = setSprite();
		frontSprite = setFrontSprite();
		miniSprite = setMiniSprite();
	}
	
	@Override
	protected BufferedImage setSprite() {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/sprites/egg.png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/000.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
	@Override
	public BufferedImage setMiniSprite() {
		BufferedImage image = null;
		
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/minisprites/egg.png"));
		} catch (Exception e) {
			image = sprite;
			
			int scaledWidth = 40;  // New width
	        int scaledHeight = 40; // New height

	        // Create a BufferedImage with transparent pixels
	        BufferedImage miniImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);

	        // Calculate the position to draw the scaled image in the center
	        int x = (60 - scaledWidth) / 2;
	        int y = (60 - scaledHeight) / 2;

	        // Draw the scaled-down sprite onto the BufferedImage
	        Graphics2D g2d = miniImage.createGraphics();
	        g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
	        g2d.dispose();

	        return miniImage;
		}
		return image;
	}
	
	@Override
	public boolean isFainted() {
		return true;
	}
	
	public boolean step(boolean fast) {
		int dec = fast ? 2 : 1;
		this.steps -= dec;
		if (this.steps <= 0) {
			this.steps = 0;
			return true;
		}
		return false;
	}
	
	public int getSteps() {
		return this.steps;
	}
	
	public Pokemon hatch() {
		String metAt = PlayerCharacter.getMetAt();
		if (gp.player.p.nuzlocke) {
			if (!gp.player.p.canCatchPokemonHere(metAt)) {
				return this;
			} else {
				gp.player.p.removeEncounterArea(metAt);
			}
		}
		Pokemon p = this.clone();
		p.setSprites();
		p.nickname = p.name();
		p.metAt = metAt;
		
		return p;
	}

	public String getHatchDesc() {
		if (steps < 3) {
			return "This Egg looks ready to hatch at any moment!";
		} else if (steps >= 3 && steps < 6) {
			return "Sounds can be heard coming from inside! It will hatch soon!";
		} else if (steps >= 6 && steps < 11) {
			return "It appears to move occasionally. It may be close to hatching.";
		} else if (steps >= 11 && steps < 41) {
			return "What will hatch from this? It doesn't seem close to hatching.";
		} else {
			return "It looks like this Egg will take a long time to hatch.";
		}
	}

}
