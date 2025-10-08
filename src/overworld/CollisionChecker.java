package overworld;

import java.awt.Rectangle;

import entity.*;
import tile.*;

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

	    int entityLeftCol = entityLeftWorldX / gp.tileSize;
	    int entityRightCol = entityRightWorldX / gp.tileSize;
	    int entityTopRow = entityTopWorldY / gp.tileSize;
	    int entityBottomRow = entityBottomWorldY / gp.tileSize;

	    int tileNum1, tileNum2;

	    tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	    tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	    
	    if (gp.tileM.tile[tileNum1] instanceof IceTile || gp.tileM.tile[tileNum2] instanceof IceTile) {
	    	entity.ice = true;
	    } else {
	    	entity.ice = false;
	    }

	    int delta = entity.speed / 2;

	    switch (entity.direction) {
	        case "up":
	            entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
	            tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	            tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	            checkCollision(tileNum1, entityLeftCol, entityTopRow, entity, "up", delta);
	            checkCollision(tileNum2, entityRightCol, entityTopRow, entity, "up", delta);
	            break;
	        case "down":
	            entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
	            tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	            tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	            checkCollision(tileNum1, entityLeftCol, entityBottomRow, entity, "down", delta);
	            checkCollision(tileNum2, entityRightCol, entityBottomRow, entity, "down", delta);
	            break;
	        case "left":
	            entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
	            tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	            tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	            checkCollision(tileNum1, entityLeftCol, entityTopRow, entity, "left", delta);
	            checkCollision(tileNum2, entityLeftCol, entityBottomRow, entity, "left", delta);
	            break;
	        case "right":
	            entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
	            tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	            tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	            checkCollision(tileNum1, entityRightCol, entityTopRow, entity, "right", delta);
	            checkCollision(tileNum2, entityRightCol, entityBottomRow, entity, "right", delta);
	            break;
	    }
	}
	
	public void checkTallGrass(Entity entity) {
		int entityCenterWorldX = entity.worldX + entity.solidArea.x + (entity.solidArea.width / 2);
	    int entityCenterWorldY = entity.worldY + entity.solidArea.y + (entity.solidArea.height / 2);
	    int entityCenterCol = entityCenterWorldX / gp.tileSize;
	    int entityCenterRow = entityCenterWorldY / gp.tileSize;
	    
	    int centerTileNum = gp.tileM.mapTileNum[gp.currentMap][entityCenterCol][entityCenterRow];
	    
	    if (gp.tileM.tile[centerTileNum] instanceof GrassTile ||
	    	gp.tileM.tile[centerTileNum] instanceof CaveTile) {
	    	entity.inTallGrass = true;
	    } else {
	    	entity.inTallGrass = false;
	    }
	}

	private void checkCollision(int tileNum, int col, int row, Entity entity, String direction, int delta) {
	    Tile tile = gp.tileM.tile[tileNum];
	    if (tile.collision) {
	        // Get the collision rectangle in world coordinates
	        Rectangle tileCollisionArea = new Rectangle(
	            col * gp.tileSize + tile.collisionArea.x,
	            row * gp.tileSize + tile.collisionArea.y,
	            tile.collisionArea.width,
	            tile.collisionArea.height
	        );

	        Rectangle entityArea = new Rectangle(
	            entity.worldX + entity.solidArea.x,
	            entity.worldY + entity.solidArea.y,
	            entity.solidArea.width,
	            entity.solidArea.height
	        );

	        boolean touching = isTouching(entityArea, tileCollisionArea, direction, delta);
	        if (touching && (tile.collisionDirection.equals("all") || !tile.collisionDirection.equals(direction))) {
	            entity.collisionOn = true;
	            entity.ice = false;
	        }
	    }
	}

	private boolean isTouching(Rectangle r1, Rectangle r2, String direction, int delta) {
	    int r1Bottom = r1.y + r1.height;
	    int r1Top = r1.y;
	    int r1Left = r1.x;
	    int r1Right = r1.x + r1.width;

	    int r2Bottom = r2.y + r2.height;
	    int r2Top = r2.y;
	    int r2Left = r2.x;
	    int r2Right = r2.x + r2.width;

	    switch (direction) {
	        case "up":
	            return (r1Top - delta <= r2Bottom && r1Top >= r2Bottom && r1Right > r2Left && r1Left < r2Right);
	        case "down":
	            return (r1Bottom + delta >= r2Top && r1Bottom <= r2Top && r1Right > r2Left && r1Left < r2Right);
	        case "left":
	            return (r1Left - delta <= r2Right && r1Left >= r2Right && r1Bottom > r2Top && r1Top < r2Bottom);
	        case "right":
	            return (r1Right + delta >= r2Left && r1Right <= r2Left && r1Bottom > r2Top && r1Top < r2Bottom);
	        default:
	            return false;
	    }
	}
	
	public int checkObject(Entity entity) {
		int index = -1;
		
		for (int i = 0; i < gp.obj[1].length; i++) {
			if (gp.obj[gp.currentMap][i] != null) {
				
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				
				gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x;
				gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y;
				
				switch(entity.direction) {
				case "up":
					entity.solidArea.y -= entity.speed;
					if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
						entity.collisionOn = true;
						entity.ice = false;
						index = i;
					}
					break;
				case "down":
					entity.solidArea.y += entity.speed;
					if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
						entity.collisionOn = true;
						entity.ice = false;
						index = i;
					}
					break;
				case "left":
					entity.solidArea.x -= entity.speed;
					if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
						entity.collisionOn = true;
						entity.ice = false;
						index = i;
					}
					break;
				case "right":
					entity.solidArea.x += entity.speed;
					if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
						entity.collisionOn = true;
						entity.ice = false;
						index = i;
					}
					break;
				}
				
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].solidAreaDefaultX;
				gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].solidAreaDefaultY;
				
			}
		}
		
		return index;
	}
	
	public int checkEntity(Entity entity, Entity[][] target) {
	    int index = 999;

	    for (int i = 0; i < target[1].length; i++) {
	        if (target[gp.currentMap][i] != null) {
	            Rectangle entityRange = new Rectangle(entity.worldX + entity.solidArea.x, entity.worldY + entity.solidArea.y, entity.solidArea.width, entity.solidArea.height);
	            int targetX = target[gp.currentMap][i].worldX;
	            if (target[gp.currentMap][i] instanceof NPC_Clerk || target[gp.currentMap][i] instanceof NPC_Ball) targetX += gp.tileSize;
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

	            if (entityRange.intersects(targetRange) && target[gp.currentMap][i].collision) {
	                entity.collisionOn = true;
	                entity.ice = false;
	                index = i;
	            }
	        }
	    }
	    return index;
	}

	public boolean checkTrainer(Entity entity, Entity target, int trainer) {
		if (!(target instanceof NPC_Trainer)) return false;
		if (trainer >= gp.player.p.trainersBeat.length) return false;
		if (trainer < 0) return false;
		if (gp.player.p.trainersBeat[trainer]) return false;
	    int visionRange = 4 * gp.tileSize;
	    boolean result = false;

	    if ((target instanceof NPC_Trainer) && target != null) {
	        Rectangle entityRange = new Rectangle(entity.worldX + entity.solidArea.x, entity.worldY + entity.solidArea.y, entity.solidArea.width, entity.solidArea.height);
	        Rectangle trainerRange = new Rectangle(target.worldX, target.worldY, target.solidArea.width, target.solidArea.height);

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
	
	public int checkTileType(Entity entity) {
		int[] coords = new int[2];
				
		switch(entity.direction) {
		case "up":
			coords[0] = entity.worldX;
			coords[1] = entity.worldY - gp.tileSize;
			break;
		case "down":
			coords[0] = entity.worldX;
			coords[1] = entity.worldY + gp.tileSize;
			break;
		case "left":
			coords[0] = entity.worldX - gp.tileSize;
			coords[1] = entity.worldY;
			break;
		case "right":
			coords[0] = entity.worldX + gp.tileSize;
			coords[1] = entity.worldY;
			break;
		}
		
		double xD = coords[0];
		xD /= gp.tileSize;
		int x = (int) Math.round(xD);
		
		double yD = coords[1];
		yD /= gp.tileSize;
		int y = (int) Math.round(yD);
		
		int result = gp.tileM.mapTileNum[gp.currentMap][x][y];
		
		return result;
			
	}




}
