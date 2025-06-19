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
	
	public int cycles;
	
	public Egg(int id) {
		super(id, 1, true, false);
		
		this.nickname = "Egg";
		
		int catchRate = Pokemon.getCatchRate(this.getFinalEvolution());
		this.cycles = computeEggCycles(catchRate);
		
		setSprites();
	}
	
	public static int computeEggCycles(int catchRate) {
	    double minCycles = 2;
	    double maxCycles = 20;
	    double logMin = Math.log(3);
	    double logMax = Math.log(255);
	    double scale = (Math.log(catchRate) - logMin) / (logMax - logMin);
	    double cycles = maxCycles - scale * (maxCycles - minCycles);
	    return (int)Math.round(cycles);
	}
	
	@Override
	public void setSprites() {
		sprite = setSprite();
		frontSprite = setFrontSprite();
		miniSprite = setMiniSprite();
	}
	
	@Override
	public BufferedImage setSprite() {
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
		this.cycles -= dec;
		if (this.cycles <= 0) {
			this.cycles = 0;
			return true;
		}
		return false;
	}
	
	public int getSteps() {
		return this.cycles;
	}
	
	public Pokemon hatch() {
		String metAt = PlayerCharacter.getMetAt();
		if (gp.player.p.nuzlocke) {
			if (!gp.player.p.canCatchPokemonHere(metAt, this)) {
				return this;
			} else {
				gp.player.p.removeEncounterArea(metAt, null);
			}
		}
		Pokemon p = this.clone();
		p.setSprites();
		p.nickname = p.name();
		p.metAt = metAt;
		
		return p;
	}

	public String getHatchDesc() {
		if (cycles < 3) {
			return "This Egg looks ready to hatch at any moment!";
		} else if (cycles >= 3 && cycles < 6) {
			return "Sounds can be heard coming from inside! It will hatch soon!";
		} else if (cycles >= 6 && cycles < 11) {
			return "It appears to move occasionally. It may be close to hatching.";
		} else if (cycles >= 11 && cycles < 41) {
			return "What will hatch from this? It doesn't seem close to hatching.";
		} else {
			return "It looks like this Egg will take a long time to hatch.";
		}
	}

}
