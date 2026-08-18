package entity;

import overworld.GamePanel;

public class NPC_Investigator extends NPC_Block {
	
	private String patrolDirection = "right";
	private int resumeDelay = 0;
	
	public NPC_Investigator(GamePanel gp, String name, String[] message, double scriptIndex, int flag, String altDialogue) {
		super(gp, name, message, scriptIndex, flag, altDialogue);
		
		direction = patrolDirection;
		speed = 2;
		
		setPatrolBounds(81 * gp.tileSize, 83 * gp.tileSize);
	}
	
	public void getImage() {
		down1 = setup("/npc/investigator1");
		up1 = setup("/npc/investigator2");
		left1 = setup("/npc/investigator3");
		right1 = setup("/npc/investigator4");
		
		walkable = true;
		down2 = setup("/npc/investigator1_1");
		up2 = setup("/npc/investigator2_1");
		left2 = setup("/npc/investigator3_1");
		right2 = setup("/npc/investigator4_1");
		down3 = down1;
		up3 = up1;
		left3 = left1;
		right3 = right1;
		down4 = setup("/npc/investigator1_2");
		up4 = setup("/npc/investigator2_2");
		left4 = setup("/npc/investigator3_2");
		right4 = setup("/npc/investigator4_2");
	}
	
	@Override
	public void setAction() {
		if (!direction.equals("left") && !direction.equals("right")) {
			if (resumeDelay < 60) {
				resumeDelay++;
				return;
			}
			direction = patrolDirection;
			resumeDelay = 0;
		}
		updatePatrolDirection();
		patrolDirection = direction;
	}
	
	public void update() {
		setAction();
		
		collisionOn = false;
		gp.cChecker.checkTile(this);
		gp.cChecker.checkObject(this);
		gp.cChecker.checkEntity(this, gp.npc);
		gp.cChecker.checkPlayer(this);
		
		if (!collisionOn) {
			switch(direction) {
			case "left": worldX -= speed;break;
			case "right": worldX += speed;break;
			}
		}
		
		if (resumeDelay == 0) spriteCounter++;
		if (spriteCounter > 8) {
			spriteNum++;
			if (spriteNum > 4) spriteNum = 1;
			spriteCounter = 0;
		}
	}
	
	@Override
	public void facePlayer(String playerDirection) {
		super.facePlayer(playerDirection);
		spriteNum = 1; // reset walking phase
	}
}
