package entity;

//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;

import overworld.GamePanel;

public class T_Disciple extends NPC_Trainer {

	public T_Disciple(GamePanel gp, String d, int t, String[] message) {
		super(gp, d, t, message);
	}
	
	public void getImage() {
		down1 = setup("/npc/disciple1");
		up1 = setup("/npc/disciple2");
		left1 = setup("/npc/disciple3");
		right1 = setup("/npc/disciple4");
	}
	
//	@Override
//	public BufferedImage setup(String imageName) {
//		BufferedImage rawImage = super.setup(imageName);
//		
//		BufferedImage image = new BufferedImage(
//		    rawImage.getWidth(), rawImage.getHeight(),
//		    BufferedImage.TYPE_INT_ARGB // <-- important for transparency
//		);
//		Graphics2D g = image.createGraphics();
//		g.drawImage(rawImage, 0, 0, null);
//		g.dispose();
//		
//		return image;
//	}

}
