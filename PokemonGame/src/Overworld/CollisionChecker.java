package Overworld;

import java.awt.Rectangle;

import Entity.Entity;
import Entity.NPC_Clerk;
import Entity.NPC_Nurse;
import Entity.NPC_Trainer;
import tile.GrassTile;

public class CollisionChecker {
	
	GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize;
		int entityRightCol = entityRightWorldX/gp.tileSize;
		int entityTopRow = entityTopWorldY/gp.tileSize;
		int entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		int tileNum1, tileNum2;
		
		tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	    if (gp.tileM.tile[tileNum1] instanceof GrassTile || gp.tileM.tile[tileNum2] instanceof GrassTile) {
	        entity.inTallGrass = true;
	    } else {
	        entity.inTallGrass = false;
	    }
		
		switch(entity.direction) {
		case "up":
			entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
			if (gp.tileM.tile[tileNum1].collision && 
					(gp.tileM.tile[tileNum1].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum1].collisionDirection))) {
                entity.collisionOn = true;
            } else if (gp.tileM.tile[tileNum2].collision &&
                (gp.tileM.tile[tileNum2].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum2].collisionDirection))) {
                entity.collisionOn = true;
            }
            break;
		case "down":
	        entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
	        tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	        tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	        if (gp.tileM.tile[tileNum1].collision &&
	            (gp.tileM.tile[tileNum1].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum1].collisionDirection))) {
	            entity.collisionOn = true;
	        } else if (gp.tileM.tile[tileNum2].collision && !gp.tileM.tile[tileNum2].collisionDirection.equals("up") &&
	            (gp.tileM.tile[tileNum2].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum2].collisionDirection))) {
	            entity.collisionOn = true;
	        }
	        break;

	    case "left":
	        entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
	        tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	        tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	        if (gp.tileM.tile[tileNum1].collision &&
	            (gp.tileM.tile[tileNum1].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum1].collisionDirection))) {
	            entity.collisionOn = true;
	        } else if (gp.tileM.tile[tileNum2].collision && !gp.tileM.tile[tileNum2].collisionDirection.equals("right") &&
	            (gp.tileM.tile[tileNum2].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum2].collisionDirection))) {
	            entity.collisionOn = true;
	        }
	        break;

	    case "right":
	        entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
	        tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	        tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	        if (gp.tileM.tile[tileNum1].collision &&
	            (gp.tileM.tile[tileNum1].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum1].collisionDirection))) {
	            entity.collisionOn = true;
	        } else if (gp.tileM.tile[tileNum2].collision && !gp.tileM.tile[tileNum2].collisionDirection.equals("left") &&
	            (gp.tileM.tile[tileNum2].collisionDirection.equals("all") || !entity.direction.equals(gp.tileM.tile[tileNum2].collisionDirection))) {
	            entity.collisionOn = true;
	        }
	        break;
		}
	}
	
	public int checkEntity(Entity entity, Entity[][] target) {
	    int index = 999;

	    for (int i = 0; i < target[1].length; i++) {
	        if (target[gp.currentMap][i] != null) {
	            Rectangle entityRange = new Rectangle(entity.worldX + entity.solidArea.x, entity.worldY + entity.solidArea.y, entity.solidArea.width, entity.solidArea.height);
	            int targetX = target[gp.currentMap][i].worldX;
	            if (target[gp.currentMap][i] instanceof NPC_Clerk) targetX += gp.tileSize;
	            int targetY = target[gp.currentMap][i].worldY;
	            if (target[gp.currentMap][i] instanceof NPC_Nurse) targetY += gp.tileSize;
	            Rectangle targetRange = new Rectangle(targetX + target[gp.currentMap][i].solidArea.x, targetY + target[gp.currentMap][i].solidArea.y, target[gp.currentMap][i].solidArea.width, target[gp.currentMap][i].solidArea.height);

	            switch (entity.direction) {
	                case "up":
	                    entityRange.y -= entity.speed;
	                    break;
	                case "down":
	                    entityRange.y += entity.speed;
	                    break;
	                case "left":
	                    entityRange.x -= entity.speed;
	                    break;
	                case "right":
	                    entityRange.x += entity.speed;
	                    break;
	            }

	            if (entityRange.intersects(targetRange)) {
	                entity.collisionOn = true;
	                index = i;
	            }
	        }
	    }
	    return index;
	}

	public boolean checkTrainer(Entity entity, Entity target) {
	    int visionRange = 4 * gp.tileSize;
	    boolean result = false;

	    if (target instanceof NPC_Trainer && target != null) {
	        Rectangle entityRange = new Rectangle(entity.worldX + entity.solidArea.x, entity.worldY + entity.solidArea.y, entity.solidArea.width, entity.solidArea.height);
	        Rectangle trainerRange = new Rectangle(target.worldX + target.solidArea.x, target.worldY + target.solidArea.y, target.solidArea.width, target.solidArea.height);

	        switch (target.direction) {
	            case "up": {
	                // Check for collision tiles within the vision range
	                int range = 0;
	                for (int row = trainerRange.y / gp.tileSize; row >= (trainerRange.y - visionRange) / gp.tileSize; row--) {
	                    int tileNum = gp.tileM.mapTileNum[gp.currentMap][trainerRange.x / gp.tileSize][row];
	                    if (gp.tileM.tile[tileNum].collision) {
	                        break;
	                    }
	                    range++;
	                }
	                
	                range *= gp.tileSize;
	                trainerRange.y -= range;
	                trainerRange.height += range;

	                break;
	            }
	            case "down": {
	                // Check for collision tiles within the vision range
	                int range = 0;
	                for (int row = trainerRange.y / gp.tileSize; row <= (trainerRange.y + trainerRange.height + visionRange) / gp.tileSize; row++) {
	                    int tileNum = gp.tileM.mapTileNum[gp.currentMap][trainerRange.x / gp.tileSize][row];
	                    if (gp.tileM.tile[tileNum].collision) {
	                        break;
	                    }
	                    range++;
	                }
	                
	                range *= gp.tileSize;
	                trainerRange.height += range;

	                break;
	            }
	            case "left": {
	                // Check for collision tiles within the vision range
	                int range = 0;
	                for (int col = trainerRange.x / gp.tileSize; col >= (trainerRange.x - visionRange) / gp.tileSize; col--) {
	                    int tileNum = gp.tileM.mapTileNum[gp.currentMap][col][trainerRange.y / gp.tileSize];
	                    if (gp.tileM.tile[tileNum].collision) {
	                        break;
	                    }
	                    range++;
	                }
	                
	                range *= gp.tileSize;
	                trainerRange.x -= range;
	                trainerRange.width += range;

	                break;
	            }
	            case "right": {
	                // Check for collision tiles within the vision range
	                int range = 0;
	                for (int col = trainerRange.x / gp.tileSize; col <= (trainerRange.x + trainerRange.width + visionRange) / gp.tileSize; col++) {
	                    int tileNum = gp.tileM.mapTileNum[gp.currentMap][col][trainerRange.y / gp.tileSize];
	                    if (gp.tileM.tile[tileNum].collision) {
	                        break;
	                    }
	                    range++;
	                }

	                range *= gp.tileSize;
	                trainerRange.width += range;
	                
	                break;
	            }
	        }

	        if (entityRange.intersects(trainerRange)) {
	            result = true;
	        }
	    }
	    return result;
	}




}
