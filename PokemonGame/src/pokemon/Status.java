package pokemon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public enum Status {
	BURNED("Burn", new Color(230, 9, 15), Color.BLACK),
	PARALYZED("Paralysis", new Color(219, 216, 15), Color.BLACK),
	ASLEEP("Sleep", new Color(105, 105, 102), Color.WHITE),
	POISONED("Poison", new Color(68, 2, 161), Color.WHITE),
	HEALTHY("", new Color(255, 255, 255), Color.BLACK),
	FROSTBITE("Frostbite", new Color(150, 217, 214), Color.BLACK),
	TOXIC("Toxic Poison", new Color(68, 2, 161), Color.WHITE),
	CONFUSED("CNF", new Color(32, 37, 61), Color.WHITE),
	CURSED("CRS", new Color(30, 32, 41), Color.WHITE),
	LEECHED("LCH", new Color(21, 143, 40), Color.BLACK),
	NIGHTMARE("NGT", new Color(0, 0, 0), Color.WHITE),
	FLINCHED("FLN", new Color(32, 37, 61), Color.WHITE),
	AQUA_RING("AQR", new Color(84, 117, 247), Color.BLACK), 
	SPUN("SPN", new Color(32, 37, 61), Color.WHITE),
	RECHARGE("SPN", new Color(32, 37, 61), Color.WHITE),
	POSSESSED("SPN", new Color(32, 37, 61), Color.WHITE),
	CHARGING("CRG", new Color(219, 216, 15), Color.BLACK),
	LOCKED("LCK", new Color(219, 216, 15), Color.BLACK),
	MAGIC_REFLECT("RFL", new Color(219, 216, 15), Color.BLACK),
	BONDED("AUT", new Color(219, 216, 15), Color.BLACK),
	TRAPPED("AUT", Color.BLACK, Color.BLACK),
	PROTECT("AUT",Color.BLACK,Color.BLACK),
	SEMI_INV("AUT",Color.BLACK,Color.BLACK),
	ENCORED("AUT",Color.BLACK,Color.BLACK),
	TAUNTED("AUT",Color.BLACK,Color.BLACK),
	TORMENTED("AUT",Color.BLACK,Color.BLACK),
	ENDURE("AUT",Color.BLACK,Color.BLACK),
	CRIT_CHANCE("AUT",Color.BLACK,Color.BLACK),
	FLASH_FIRE("AUT",Color.BLACK,Color.BLACK),
	NO_SWITCH("AUT",Color.BLACK,Color.BLACK),
	SMACK_DOWN("AUT",Color.BLACK,Color.BLACK),
	SWITCHING("AUT",Color.BLACK,Color.BLACK),
	HEALING("AUT",Color.BLACK,Color.BLACK),
	MUTE("AUT",Color.BLACK,Color.BLACK),
	SWAP("AUT",Color.BLACK,Color.BLACK),
	CHARGED("AUT",Color.BLACK,Color.BLACK),
	LOADED("AUT",Color.BLACK,Color.BLACK),
	YAWNING("AUT",Color.BLACK,Color.BLACK),
	DROWSY("AUT",Color.BLACK,Color.BLACK),
	LANDED("AUT",Color.BLACK,Color.BLACK),
	HEAL_BLOCK("",Color.BLACK,Color.BLACK),
	ARCANE_SPELL("",Color.BLACK,Color.BLACK),
	SPELLBIND("",Color.BLACK,Color.BLACK),
	DECK_CHANGE("",Color.BLACK,Color.BLACK),
	MAGNET_RISE("",Color.BLACK,Color.BLACK),
	;
	
	Status(String name, Color color, Color textColor) {
		this.name = name;
		this.color = color;
		this.textColor = textColor;
		
		image = loadImage("/battle/" + super.toString().toLowerCase() + ".png");
	}
	
	public BufferedImage loadImage(String imageName) {
        BufferedImage image = null;

        try {
            // Load the original image
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream(imageName));
            // Scale the image
            int newWidth = originalImage.getWidth() * 2;
            int newHeight = originalImage.getHeight() * 2;
            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();
        } catch (Exception e) {
        	try {
				image = ImageIO.read(getClass().getResourceAsStream("/items/null.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }

        return image;
    }
	
	private String name;
	private Color color;
	private Color textColor;
	private BufferedImage image;
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	@Override
	public String toString() {
		String name = super.toString();
	    name = name.toLowerCase().replace('_', ' ');
	    String[] words = name.split(" ");
	    StringBuilder sb = new StringBuilder();
	    for (String word : words) {
	        sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
	    }
	    return sb.toString().trim();
	}

	public BufferedImage getImage() {
		return image;
	}

	public static ArrayList<Status> getNonVolStatuses() {
		ArrayList<Status> result = new ArrayList<>();
		result.add(Status.BURNED);
		result.add(Status.ASLEEP);
		result.add(Status.PARALYZED);
		result.add(Status.FROSTBITE);
		result.add(Status.POISONED);
		result.add(Status.TOXIC);
		
		return result;
	}
}
