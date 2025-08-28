package pokemon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum PType {
	NORMAL(new Color(168, 167, 122)),
	ROCK(new Color(182, 161, 54)),
	FIRE(new Color(238, 129, 48)),
	WATER(new Color(99, 144, 240)),
	GRASS(new Color(122, 199, 76)),
	ICE(new Color(150, 217, 214)),
	ELECTRIC(new Color(247, 208, 44)),
	FIGHTING(new Color(194, 46, 40)),
	POISON(new Color(163, 62, 161)),
	GROUND(new Color(226, 191, 101)),
	FLYING(new Color(169, 143, 243)),
	PSYCHIC(new Color(249, 85, 135)),
	BUG(new Color(166, 185, 26)),
	GHOST(new Color(115, 87, 151)),
	DRAGON(new Color(111, 53, 252)),
	STEEL(new Color(183, 183, 206)),
	DARK(new Color(112, 87, 70)),
	LIGHT(new Color(248, 248, 120)),
	MAGIC(new Color(254, 1, 77)),
	GALACTIC(new Color(138, 30, 106)),
	UNKNOWN(Color.GRAY.darker());
	
	private Color color;
	private BufferedImage image;
	private BufferedImage image2;

	private PType(Color c) {
		color = c;
		
		image = setup("/battle/" + toString().toLowerCase());
		image2 = setup("/battle/" + toString().toLowerCase() + "_2");
	}
	
	private BufferedImage setup(String imageName) {
	    BufferedImage image = null;
	    
	    try {
	        // Load the original image
	        image = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return image;
	}
	
	public Color getColor() {
		return color;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public BufferedImage getImage2() {
		return image2;
	}
	
	@Override // implementation
	public String toString() {
		String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public String effectiveness(Pokemon foe, Pokemon user, Move m) {
		double multiplier = foe.getEffectiveMultiplier(this, m, user);
		
		if (multiplier == 0) return "No Effect";
		if (multiplier > 2) return "Extremely Effective";
		if (multiplier > 1) return "Super Effective";
		if (multiplier < 0.5) return "Mostly Ineffective";
		if (multiplier < 1) return "Not Very Effective";
		return "Effective";
	}
	
	String superToString() {
		return super.toString();
	}
}