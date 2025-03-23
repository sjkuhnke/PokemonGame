package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import object.Cut_Tree;
import object.Fuse_Box;
import object.IceBlock;
import object.InteractiveTile;
import object.ItemObj;
import object.Locked_Door;
import object.Pit;
import object.Rock_Climb;
import object.Rock_Smash;
import object.Snowball;
import object.Starter_Machine;
import object.TreasureChest;
import object.Tree_Stump;
import object.Vine;
import object.Vine_Crossable;
import object.Whirlpool;
import overworld.GamePanel;
import overworld.KeyHandler;
import overworld.Main;
import overworld.PMap;
import util.Pair;
import pokemon.*;

public class PlayerCharacter extends Entity {
	
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	public Player p;

	public String currentSave;
	
	public PType dexType; // for testing
	public Pokemon[] newDex; // for testing

	private int cooldown;
	
	public static String currentMapName;
	
	public PlayerCharacter(GamePanel gp, KeyHandler keyH) {
		super(gp, null);
		this.keyH = keyH;
		
		screenX = gp.screenWidth / 2 - (gp.tileSize/2);
		screenY = gp.screenHeight / 2 - (gp.tileSize/2);
		
		solidArea = new Rectangle(8, 16, 32, 32);
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		
		name = "Finn";
		walkable = true;
		
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		gp.currentMap = Player.spawn[0];
		worldX = gp.tileSize * Player.spawn[1];
		worldY = gp.tileSize * Player.spawn[2];
		speed = 4;
		direction = "down";
	}
	public void getPlayerImage() {
		setupPlayerImages(false);
		
		surf1 = setup("/player/surf1");
		surf2 = setup("/player/surf2");
		surf3 = setup("/player/surf3");
		surf4 = setup("/player/surf4");
	}
	
	public void update() {
		if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
			spriteCounter++;
			if (keyH.upPressed) {
				direction = "up";
			} else if (keyH.downPressed) {
				direction = "down";
			} else if (keyH.leftPressed) {
				direction = "left";
			} else if (keyH.rightPressed) {
				direction = "right";
			}
			
			collisionOn = false;
			if (!p.ghost) gp.cChecker.checkTile(this);
			
			if (!p.ghost) gp.cChecker.checkEntity(this, gp.npc);
			
			if (!p.ghost) gp.cChecker.checkObject(this);
			
			if (!p.ghost) gp.cChecker.checkEntity(this, gp.iTile);
			
			if (!collisionOn) {
				switch(direction) {
				case "up":
					worldY -= speed;
					gp.renderableNPCs.sort(Comparator.comparingInt(Entity::getWorldY));
					break;
				case "down":
					worldY += speed;
					gp.renderableNPCs.sort(Comparator.comparingInt(Entity::getWorldY));
					break;
				case "left":
					worldX -= speed;
					break;
				case "right":
					worldX += speed;
					break;
				}
			} else if (speed == 8) {
				speed = 4;
				return;
			}
			
			if (spriteCounter > 8) {
				spriteNum++;
				if (spriteNum > 4) {
					spriteNum = 1;
				}
				spriteCounter = 0;
				p.steps++;
				cooldown++;
			}
			if (gp.ticks % 4 == 0 && (inTallGrass || p.surf || p.lavasurf) && !p.repel && cooldown > 2) {
				Random r = new Random();
				int random = r.nextInt(p.current != null && p.current.item == Item.CLEANSE_TAG ? 300 : 150);
				if (random < speed) {
					cooldown = 0;
					char type = 'G';
					if (p.surf) type = 'S';
					if (p.lavasurf) type = 'L';
					startWild(currentMapName, type);
				}
			}
			if (p.steps == 202 && p.repel) {
				p.repel = false;
				if (p.bag.contains(0)) {
					gp.gameState = GamePanel.USE_REPEL_STATE;
				} else {
					gp.ui.showMessage("Repel's effects wore off.");
				}
			}
			if (p.steps % 129 == 0) {
				ArrayList<Pair<Egg, Integer>> eggs = new ArrayList<>();
				boolean fast = false;
				for (int i = 0; i < p.team.length; i++) {
					Pokemon po = p.team[i];
            		if (po != null && !po.isFainted()) {
            			po.awardHappiness(1, false);
            			if (po instanceof Egg) eggs.add(new Pair<>(((Egg) po), i));
            			if (po.ability == Ability.FLAME_BODY || po.ability == Ability.MAGMA_ARMOR || po.ability == Ability.HEAT_COMPACTION) fast = true;
            		}
            	}
				for (Pair<Egg, Integer> pair : eggs) {
					Egg e = pair.getFirst();
					boolean hatch = e.step(fast);
					if (hatch) {
						int index = pair.getSecond();
						Pokemon h = e.hatch();
						p.pokedex[h.id] = 2;
						p.team[index] = h;
						if (index == 0) gp.player.p.setCurrent(h);
						gp.setTaskState();
						Task.addTask(Task.TEXT, "Oh?");
						Task.addTask(Task.TEXT, h.name() + " hatched from the egg!");
						Task t = Task.addTask(Task.NICKNAME, "Would you like to nickname " + h.name() + "?", h);
						t.wipe = true; // to reset the nickname when the task gets up
						break;
					}
				}
				p.steps++;
			}
			
			if (p.surf) {
				double surfXD =  worldX / (1.0 * gp.tileSize);
				double surfYD =  worldY / (1.0 * gp.tileSize);
				int surfX = (int) Math.round(surfXD);
				int surfY = (int) Math.round(surfYD);
				switch(direction) {
				case "up":
					surfY = (int) Math.ceil(surfYD);
					break;
				case "down":
					break;
				case "left":
					break;
				case "right":
					break;
				}
				if (!gp.tileM.getWaterTiles().contains(gp.tileM.mapTileNum[gp.currentMap][surfX][surfY])) {
					p.surf = false;
					for (Integer i : gp.tileM.getWaterTiles()) {
						gp.tileM.tile[i].collision = true;
					}
					int snapX = (int) Math.round(worldX * 1.0 / gp.tileSize);
					int snapY = (int) Math.round(worldY * 1.0 / gp.tileSize);
					snapX *= gp.tileSize;
					snapY *= gp.tileSize;
					this.worldX = snapX;
					this.worldY = snapY;
				}
			}
			if (p.lavasurf) {
				double surfXD =  worldX / (1.0 * gp.tileSize);
				double surfYD =  worldY / (1.0 * gp.tileSize);
				int surfX = (int) Math.round(surfXD);
				int surfY = (int) Math.round(surfYD);
				switch(direction) {
				case "up":
					surfY = (int) Math.ceil(surfYD);
					break;
				case "down":
					break;
				case "left":
					break;
				case "right":
					break;
				}
				if (!gp.tileM.getLavaTiles().contains(gp.tileM.mapTileNum[gp.currentMap][surfX][surfY])) {
					p.lavasurf = false;
					for (Integer i : gp.tileM.getLavaTiles()) {
						gp.tileM.tile[i].collision = true;
					}
					int snapX = (int) Math.round(worldX * 1.0 / gp.tileSize);
					int snapY = (int) Math.round(worldY * 1.0 / gp.tileSize);
					snapX *= gp.tileSize;
					snapY *= gp.tileSize;
					this.worldX = snapX;
					this.worldY = snapY;
				}
			}
			String currentMap = currentMapName;
			PMap.getLoc(gp.currentMap, (int) Math.round(worldX * 1.0 / gp.tileSize), (int) Math.round(worldY * 1.0 / gp.tileSize));
			if (!currentMap.equals(currentMapName)) gp.ui.showAreaName();
			Main.window.setTitle(gp.gameTitle + " - " + currentMapName);
		} else {
			resetSpriteNum();
		}
		
		gp.eHandler.checkEvent();
		
		if (gp.currentMap == 107 && gp.checkSpin && gp.ticks % 4 == 0 && new Random().nextInt(3) == 0) {
			int index = new Random().nextInt(10);
			if (gp.grusts[index] != null) gp.grusts[index].turnRandom();
		}
		
		if (keyH.dPressed) {
			resetSpriteNum();
			gp.gameState = GamePanel.MENU_STATE;
		}
		if (keyH.sPressed) {
			speed = 8;
		} else {
			speed = 4;
		}
		for (int i = 0; i < gp.npc[1].length; i++) {
			int trainer = gp.npc[gp.currentMap][i] == null ? 0 : gp.npc[gp.currentMap][i].trainer;
			if (gp.ticks % 4 == 0 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "down") trainerSpot(gp.npc[gp.currentMap][i]);
			if (gp.ticks % 4 == 1 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "up") trainerSpot(gp.npc[gp.currentMap][i]);
			if (gp.ticks % 4 == 2 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "left") trainerSpot(gp.npc[gp.currentMap][i]);
			if (gp.ticks % 4 == 3 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "right") trainerSpot(gp.npc[gp.currentMap][i]);
		}
		if (keyH.wPressed) {
			// Check trainers
			int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
			if (npcIndex != 999) {
				Entity target = gp.npc[gp.currentMap][npcIndex];
				resetSpriteNum();
				if (target instanceof NPC_Nurse) {
					interactNurse(target);
				} else if (target instanceof NPC_Clerk || target instanceof NPC_Market
						|| target instanceof NPC_Prize_Shop || target instanceof NPC_Star
						|| target instanceof NPC_Ball) {
					interactClerk(target);
				} else if (target instanceof NPC_Block) {
					interactNPC((NPC_Block) target, true);
				} else if (target instanceof NPC_Trainer) {
					interactTrainer(target, -1, true);
				} else if (target instanceof NPC_GymLeader) {
					interactTrainer(target, -1, true);
				} else if (target instanceof NPC_PC) {
					interactPC((NPC_PC) target);
				} else if (target instanceof NPC_Pokemon) {
					NPC_Pokemon pokemon = (NPC_Pokemon) target;
					interactTrainer(target, pokemon.id, pokemon.spin);
				}
			}
			
			// Check items
			int objIndex = gp.cChecker.checkObject(this);
			if (objIndex != -1) {
				resetSpriteNum();
				ItemObj item = (ItemObj) gp.obj[gp.currentMap][objIndex];
				if (item instanceof TreasureChest) {
					openChest((TreasureChest) item, objIndex);
				} else {
					pickUpObject(objIndex);
				}
			}
			
			if (p.hasMove(Move.SURF) && !p.surf) {
				int result = gp.cChecker.checkTileType(this);
				if (gp.tileM.getWaterTiles().contains(result)) {
					int x = 0;
					int y = 0;
					switch (direction) {
					case "down":
						worldY += gp.tileSize;
						x = worldX + gp.tileSize / 2;
						y = worldY;
						break;
					case "up":
						worldY -= gp.tileSize;
						x = worldX + gp.tileSize / 2;
						y = worldY;
						break;
					case "left":
						worldX -= gp.tileSize;
						x = worldX;
						y = worldY;
						break;
					case "right":
						worldX += gp.tileSize;
						x = worldX + gp.tileSize;
						y = worldY;
						break;
					}
					p.surf = true;
					generateParticle(x, y, new Color(50,184,255), 6, 1, 20);
					for (Integer i : gp.tileM.getWaterTiles()) {
						gp.tileM.tile[i].collision = false;
					}
				}
			}
			if (p.hasMove(Move.LAVA_SURF) && !p.lavasurf) {
				int result = gp.cChecker.checkTileType(this);
				if (gp.tileM.getLavaTiles().contains(result)) {
					int x = 0;
					int y = 0;
					switch (direction) {
					case "down":
						worldY += gp.tileSize;
						x = worldX + gp.tileSize / 2;
						y = worldY;
						break;
					case "up":
						worldY -= gp.tileSize;
						x = worldX + gp.tileSize / 2;
						y = worldY;
						break;
					case "left":
						worldX -= gp.tileSize;
						x = worldX;
						y = worldY;
						break;
					case "right":
						worldX += gp.tileSize;
						x = worldX + gp.tileSize;
						y = worldY;
						break;
					}
					p.lavasurf = true;
					generateParticle(x, y, new Color(255,48,48), 6, 1, 20);
					for (Integer i : gp.tileM.getLavaTiles()) {
						gp.tileM.tile[i].collision = false;
					}
				}
			}
			
			// Check iTiles
			int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
			if (iTileIndex != 999) {
				InteractiveTile target = gp.iTile[gp.currentMap][iTileIndex];
				interactWith(target, iTileIndex, p.ghost);
			}
		}
		if (keyH.aPressed) {
			if (keyH.ctrlPressed) {
				gp.keyH.resetKeys();
				resetSpriteNum();
				Item.useCalc(p.getCurrent(), null, null, true);
			} else {
				if (p.fish) {
					int result = gp.cChecker.checkTileType(this);
					if (result == 3 || (result >= 24 && result <= 36) || (result >= 313 && result <= 324)) {
						resetSpriteNum();
						startWild(currentMapName, 'F');
					}
				}
			}
		}
	}

	private void resetSpriteNum() {
		spriteCounter = 8;
		spriteNum = 1;
	}

	public void interactWith(Entity target, int index, boolean override) {
		if (target instanceof Cut_Tree) {
			interactCutTree(index, override);
		} else if (target instanceof Rock_Smash) {
			interactRockSmash(index, override);
		} else if (target instanceof Vine_Crossable) {
			interactVines(index, override);
		} else if (target instanceof Pit) {
			interactPit(index, override);
		} else if (target instanceof Whirlpool) {
			interactWhirlpool(index, override);
		} else if (target instanceof Rock_Climb) {
			interactRockClimb(index, override);
		} else if (target instanceof Starter_Machine) {
			interactStarterMachine(index);
		} else if (target instanceof Locked_Door) {
			interactLockedDoor(index);
		} else if (target instanceof Fuse_Box) {
			interactFuseBox(index);
		} else if (target instanceof Snowball) {
			interactSnowball(index, override);
		} else if (target instanceof IceBlock) {
			interactIceBlock(index, override);
		}
	}

	private void trainerSpot(Entity entity) {
		int trainer = entity.trainer;
		if (p.trainersBeat[trainer]) return;
		
		interactTrainer(entity, -1, false);
	}

	public void interactTrainer(Entity entity, int id, boolean turn) {
		gp.keyH.wPressed = false;
		
		int trainer = entity.trainer;
		if (trainer == -1) return;
		if (p.wiped()) return;
		if (gp.ui.tasksContainsTask(Task.DIALOGUE)) return;
		
		gp.ui.npc = entity;
		
		if (turn) entity.facePlayer(direction);
		entity.setName();
		
		if (!p.trainersBeat[trainer]) {
			gp.setTaskState();
			
			Pokemon foe = Trainer.getTrainer(trainer).getCurrent();
			
			Task.addTrainerTask(entity, id, foe, !(entity instanceof NPC_GymLeader));
			
			// Heal if Gym Leader
			if (Trainer.getTrainer(trainer).getMoney() == 500) {
				p.heal();
			}
		} else if (entity.altDialogue != null) {
			gp.setTaskState();
			
			entity.speak(1);
		}
	}
	
	public void startWild(String area, char type) {
		gp.setTaskState();
		
		Pokemon foe = gp.encounterPokemon(area, type, p.random);
		Task.addStartBattleTask(-2, -1, foe, type);
	}
	
	public void startFish(String area) {
		startWild(area, 'F');
	}

	private void interactPC(NPC_PC target) {
		if (!target.isGauntlet()) target.direction = "up";
		
		gp.openBox(target);
	}

	private void pickUpObject(int objIndex) {
		gp.keyH.wPressed = false;
		Item item = gp.obj[gp.currentMap][objIndex].item;
		int count = gp.obj[gp.currentMap][objIndex].count;
		String itemName = item.toString();
		if (count > 1 && itemName.contains("Berry")) itemName = itemName.replace("Berry", "Berries");
		
		gp.setTaskState();
		
		Task t = Task.addTask(Task.ITEM, "You found " + count + " " + itemName + "!");
		t.item = item;
		t.counter = count;
		if (gp.currentMap == 121) {
			if (gp.obj[gp.currentMap][objIndex].item == Item.CHOICE_BAND) {
				gp.player.p.itemsCollected[gp.currentMap][objIndex + 1] = true;
				gp.obj[gp.currentMap][objIndex + 1] = null;
				p.choiceChoice = Item.CHOICE_SPECS;
			}
			if (gp.obj[gp.currentMap][objIndex].item == Item.CHOICE_SPECS) {
				gp.player.p.itemsCollected[gp.currentMap][objIndex - 1] = true;
				gp.obj[gp.currentMap][objIndex - 1] = null;
				p.choiceChoice = Item.CHOICE_BAND;
			}
		}
		gp.player.p.itemsCollected[gp.currentMap][objIndex] = true;
		gp.obj[gp.currentMap][objIndex] = null;
		
	}
	
	private void openChest(TreasureChest chest, int objIndex) {
		gp.keyH.wPressed = false;
		if (chest.open) {
			gp.ui.showMessage("The chest is empty...");
		} else {
			gp.setTaskState();
			chest.open();
			
			for (Item i : chest.items) {
				Task t = Task.addTask(Task.ITEM, "You found 1 " + i.toString() + "!");
				t.item = i;
			}
			
			Task.addTask(Task.ITEM_SUM, chest, "");
			
			gp.player.p.itemsCollected[gp.currentMap][objIndex] = true;
		}
	}
	
	public void showPlayer() {
		gp.keyH.resetKeys();
		JPanel playerInfo = new JPanel();
    	playerInfo.setLayout(new GridLayout(6, 1));
    	
    	JLabel moneyLabel = new JLabel();
    	moneyLabel.setText("$" + p.getMoney());
    	JLabel badgesLabel = new JLabel();
    	badgesLabel.setText(p.badges + " Badges");
    	
    	JTextField cheats = new JTextField();
    	cheats.addActionListener(e -> {
    		String code = cheats.getText();
    		runCode(code, cheats);
    	});
    	
    	playerInfo.add(moneyLabel);
    	playerInfo.add(badgesLabel);
    	playerInfo.add(cheats);
    	
    	JOptionPane.showMessageDialog(null, playerInfo, "Player Info", JOptionPane.PLAIN_MESSAGE);
	}

	private void interactNurse(Entity npc) {
		// NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
    	if (gp.currentMap == 1) p.locations[1] = true;
    	if (gp.currentMap == 5) p.locations[2] = true;
    	if (gp.currentMap == 19) p.locations[3] = true;
    	if (gp.currentMap == 29) p.locations[4] = true;
    	if (gp.currentMap == 39) p.locations[6] = true;
    	if (gp.currentMap == 86) p.locations[8] = true;
    	if (gp.currentMap == 92) p.locations[5] = true;
    	if (gp.currentMap == 111) p.locations[7] = true;
    	if (gp.currentMap == 125) p.locations[9] = true;
    	if (gp.currentMap == 153) p.locations[10] = true;
    	
		gp.keyH.wPressed = false;
		gp.gameState = GamePanel.NURSE_STATE;
		npc.speak(-1);
	}
	
	private void interactClerk(Entity npc) {
		gp.keyH.wPressed = false;
		npc.facePlayer(direction);
		npc.speak(-1);
	}
	
	public void interactNPC(Entity npc, boolean face) {
		gp.keyH.wPressed = false;
		if (face) npc.facePlayer(direction);
		if (npc.flag == -1 || !p.flag[npc.getFlagX()][npc.getFlagY()]) {
			gp.gameState = GamePanel.DIALOGUE_STATE;
			gp.ui.npc = npc;
			npc.speak(0);
			if (npc.scriptIndex >= 0) {
				SwingUtilities.invokeLater(() -> {
					gp.keyH.resetKeys();
					gp.setTaskState();
					gp.script.runScript(npc);
				});
			}
		} else if (npc.altDialogue != null) {
			gp.setTaskState();
			
			npc.speak(1);
		}
	}
	
	private void interactCutTree(int i, boolean override) {
		if (override || p.hasMove(Move.CUT)) {
			gp.keyH.wPressed = false;
			Cut_Tree temp = (Cut_Tree) gp.iTile[gp.currentMap][i];
			gp.iTile[gp.currentMap][i] = new Tree_Stump(gp);
			gp.iTile[gp.currentMap][i].worldX = temp.worldX;
			gp.iTile[gp.currentMap][i].worldY = temp.worldY;
			
			generateParticle(temp);
		} else {
			gp.ui.showMessage("This tree looks like it can be cut down!");
		}
		
	}
	
	private void interactRockSmash(int i, boolean override) {
		if (override || p.hasMove(Move.ROCK_SMASH)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage("This rock looks like it can be broken!");
		}
		
	}
	
	private void interactVines(int i, boolean override) {
		if (override || p.hasMove(Move.VINE_CROSS)) {
			gp.keyH.wPressed = false;
			Vine_Crossable temp = (Vine_Crossable) gp.iTile[gp.currentMap][i];
			gp.iTile[gp.currentMap][i] = new Vine(gp);
			gp.iTile[gp.currentMap][i].worldX = temp.worldX;
			gp.iTile[gp.currentMap][i].worldY = temp.worldY;
			generateParticle(temp);
		} else {
			gp.ui.showMessage("This gap looks like it can be crossed!");
		}
	}
	
	private void interactPit(int i, boolean override) {
		if (override || p.hasMove(Move.SLOW_FALL)) {
			gp.keyH.wPressed = false;
			Pit pit = (Pit) gp.iTile[gp.currentMap][i];
			gp.eHandler.teleport(pit.mapDest, pit.xDest, pit.yDest, false);
			generateParticle(pit);
		} else {
			gp.ui.showMessage("This pit looks deep!\nI can't even see the bottom!");
		}
		
	}
	
	private void interactWhirlpool(int i, boolean override) {
		if (override || p.hasMove(Move.WHIRLPOOL)) {
			gp.keyH.wPressed = false;
			int offset = gp.tileSize / 2;
			int x = gp.iTile[gp.currentMap][i].worldX + offset;
			int y = gp.iTile[gp.currentMap][i].worldY + offset;
			for (int j = 0; j < 3; j++) {
				generateParticle(x, y, new Color(50,184,255), 6, 1, 20);
				x += getTileForwardX();
				y += getTileForwardY();
			}
			switch (direction) {
			case "down":
				worldY += gp.tileSize * 3.75;
				break;
			case "up":
				worldY -= gp.tileSize * 3.75;
				break;
			case "left":
				worldX -= gp.tileSize * 3.75;
				break;
			case "right":
				worldX += gp.tileSize * 3.75;
				break;
			}
		} else {
			gp.ui.showMessage("This water vortex can be crossed!");
		}
		
	}
	
	private void interactRockClimb(int i, boolean override) {
		if (override || p.hasMove(Move.ROCK_CLIMB)) {
			gp.keyH.wPressed = false;
			Rock_Climb rc = (Rock_Climb) gp.iTile[gp.currentMap][i];
			int offset = gp.tileSize / 2;
			int x = rc.worldX + offset;
			int y = rc.worldY + offset;
			for (int j = 0; j < rc.amt; j++) {
				generateParticle(x, y, new Color(112, 69, 35), 6, 1, 20);
				x += getTileForwardX();
				y += getTileForwardY();
			}
			int inverse = this.direction == rc.direction ? 1 : -1;
			this.worldX += ((rc.deltaX * gp.tileSize * inverse * rc.amt) + (gp.tileSize * 0.75 * rc.deltaX * inverse));
			this.worldY += ((rc.deltaY * gp.tileSize * inverse * rc.amt) + (gp.tileSize * 0.75 * rc.deltaY * inverse));
		} else {
			gp.ui.showMessage("This wall looks like it can be scaled!");
		}	
	}
	
	private void interactStarterMachine(int i) {
		if (!p.flag[0][0]) { // Before talking to Dad first
			gp.ui.showMessage("It's a machine housing three rare Pokemon!");
		} else if (p.flag[0][0] && !p.flag[0][1]) { // After talking to Dad and before picking a starter
			gp.keyH.resetKeys();
			gp.gameState = GamePanel.STARTER_STATE;
		} else if (p.flag[0][1] && !p.flag[0][4]) { // After picking a starter and before the first gate
			gp.ui.showMessage(Item.breakString("There are two Pokemon still inside the machine. Wonder what Dad will do with them?", 42));
		} else if (p.flag[0][4] && p.badges < 1) { // After the first gate but before beating Gym 1
			gp.ui.showMessage(Item.breakString("There's still a Pokemon left! Dad must've given one to Scott as well!", 42));
		} else if (p.badges > 1 && !p.flag[1][0]) { // After beating Gym 1
			gp.ui.showMessage("There aren't any Pokemon inside.");
		} else { // Dialogue after beating Fred 1 but before Gym 2
			gp.ui.showMessage(Item.breakString("There aren't any Pokemon left, Dad must've given one to Scott and one to Fred.", 42));
		}
		
	}
	
	private void interactLockedDoor(int i) {
		if (gp.currentMap == 4) { // Poppy Grove Warehouse
			if (!p.bag.contains(Item.WAREHOUSE_KEY)) {
				gp.ui.showMessage("The door is locked!");
			} else {
				gp.keyH.wPressed = false;
				gp.ui.showMessage("Used the warehouse key! The door unlocked!");
				generateParticle(gp.iTile[gp.currentMap][i]);
				gp.iTile[gp.currentMap][i] = null;
				p.flag[0][13] = true;
				p.bag.remove(Item.WAREHOUSE_KEY);
			}
		} else if (gp.currentMap == 13) {
			gp.ui.showMessage("...the electric door doesn't budge.");
		} else if (gp.currentMap == 41) { // Schrice City School
			if (worldX / gp.tileSize < 31) { // Ice Master
				if (!p.bag.contains(Item.ICE_KEY)) {
					gp.ui.showMessage("The door is locked!");
				} else {
					gp.keyH.wPressed = false;
					gp.ui.showMessage("Used Ice Master's Key!\nThe door unlocked!");
					generateParticle(gp.iTile[gp.currentMap][i]);
					gp.iTile[gp.currentMap][i] = null;
					p.flag[3][3] = true;
					p.bag.remove(Item.ICE_KEY);
				}
			} else { // Ground Master
				if (!p.bag.contains(Item.GROUND_KEY)) {
					gp.ui.showMessage("The door is locked!");
				} else {
					gp.keyH.wPressed = false;
					gp.ui.showMessage("Used Ground Master's Key!\nThe door unlocked!");
					generateParticle(gp.iTile[gp.currentMap][i]);
					gp.iTile[gp.currentMap][i] = null;
					p.flag[3][4] = true;
					p.bag.remove(Item.GROUND_KEY);
				}
			}
			
		}
	}
	
	private void interactFuseBox(int i) {
		if (gp.currentMap == 60) { // control center
			if (gp.iTile[gp.currentMap][i].direction == "down") { // fuse box 1
				if (!p.flag[1][4]) {
					gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
					p.flag[1][4] = true;
				} else {
					gp.ui.showMessage("The fuse box is whirring with power!");
				}
			} else { // fuse box 2
				if (!p.flag[1][15]) {
					gp.ui.showMessage("Powered on the main fuse box!\nThe Control Center erupted in power!");
					p.flag[1][15] = true;
				} else {
					gp.ui.showMessage("The fuse box is whirring with power!");
				}
			}
		} else if (gp.currentMap == 14) { // power plant 1
			if (!p.flag[1][5]) {
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", 42));
			} else if (p.flag[1][5] && !p.flag[1][6]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][6] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		} else if (gp.currentMap == 16) { // power plant 2
			if (!p.flag[1][7]) {
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", 42));
			} else if (p.flag[1][7] && !p.flag[1][8]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][8] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		} else if (gp.currentMap == 18) { // office 2
			if (!p.flag[1][11]) {
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", 42));
			} else if (p.flag[1][11] && !p.flag[1][12]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][12] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		}
	}
	
	private void interactSnowball(int i, boolean override) {
		if (override || p.bag.contains(Item.SHOVEL)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage("This pile of snow looks like it can be\nshoveled!");
		}
		
	}
	
	private void interactIceBlock(int i, boolean override) {
		if (override || p.bag.contains(Item.ICE_PICK)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage("This block of ice looks like it can be\nbroken!");
		}
		
	}
	
	private int getTileForwardX() {
		switch (direction) {
		case "down":
		case "up":
			break;
		case "left":
			return -gp.tileSize;
		case "right":
			return gp.tileSize;
		}
		return 0;
	}
	
	private int getTileForwardY() {
		switch (direction) {
		case "down":
			return gp.tileSize;
		case "up":
			return -gp.tileSize;
		case "left":
		case "right":
			break;
		}
		return 0;
	}

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			if (spriteNum == 1) image = up1;
			if (spriteNum == 2) image = up2;
			if (spriteNum == 3) image = up3;
			if (spriteNum == 4) image = up4;
			if (p.surf || p.lavasurf) image = surf2;
			break;
		case "down":
			if (spriteNum == 1) image = down1;
			if (spriteNum == 2) image = down2;
			if (spriteNum == 3) image = down3;
			if (spriteNum == 4) image = down4;
			if (p.surf || p.lavasurf) image = surf1;
			break;
		case "left":
			if (spriteNum == 1) image = left1;
			if (spriteNum == 2) image = left2;
			if (spriteNum == 3) image = left3;
			if (spriteNum == 4) image = left4;
			if (p.surf || p.lavasurf) image = surf3;
			break;
		case "right":
			if (spriteNum == 1) image = right1;
			if (spriteNum == 2) image = right2;
			if (spriteNum == 3) image = right3;
			if (spriteNum == 4) image = right4;
			if (p.surf || p.lavasurf) image = surf4;
			break;
		}
		
		int height = image.getHeight() * gp.scale;
		int offset = height - gp.tileSize;
		
		g2.drawImage(image, screenX + gp.offsetX, screenY - offset + gp.offsetY, gp.tileSize, height, null);
	}
	
	public Item[] getItems() {
		int available = 8;
	    if (p.badges > 7) available += 2;
	    if (p.badges > 5) available ++;
	    if (p.badges > 3) available += 2;
	    if (p.badges > 2) available += 2;
	    if (p.badges > 1) available ++;
	    if (p.badges > 0) available += 2;
	    Item[] shopItems = new Item[] {Item.REPEL, Item.POKEBALL, Item.POTION, Item.ANTIDOTE, Item.AWAKENING, Item.BURN_HEAL, Item.PARALYZE_HEAL, Item.FREEZE_HEAL, // 0 badges
	    		Item.GREAT_BALL, Item.SUPER_POTION, // 1 badge
	    		Item.ELIXIR, // 2 badges
	    		Item.FULL_HEAL, Item.REVIVE, // 3 badges
	    		Item.ULTRA_BALL, Item.HYPER_POTION, // 4 badges
	    		Item.MAX_POTION, // 6 badges
	    		Item.FULL_RESTORE, Item.MAX_REVIVE}; // 8 badges
	    Item[] result = new Item[available];
	    for (int i = 0; i < available; i++) {
	    	result[i] = shopItems[i];
	    }
	    return result;
	}
	
	private void runCode(String code, JTextField cheats) {
		if (code.equals("RAR3")) {
			p.bag.add(Item.RARE_CANDY);
			p.bag.count[18] = 999;
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("M1X3R")) {
			p.random = !p.random;
			String onoff = p.random ? "on!" : "off.";
			JOptionPane.showMessageDialog(null, "Randomizer mode was turned " + onoff);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GASTLY")) {
			p.ghost = !p.ghost;
			String onoff = p.ghost ? "on!" : "off.";
			JOptionPane.showMessageDialog(null, "Walk-through-walls mode was turned " + onoff);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("LIGMA")) {
			for (Pokemon pokemon : p.team) {
				if (pokemon != null) pokemon.heal();
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("BALLZ")) {
			JPanel panel = p.displayTweaker();
			JOptionPane.showMessageDialog(null, panel);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("KANY3")) {
			p.setMoney(1000000);
			p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
			gp.aSetter.setObject();
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("Ben")) {
			p.catchPokemon(new Pokemon(238, 5, true, false));
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.startsWith("dex")) {
			String[] parts = code.split(" ");
		    if (parts.length > 1) {
		        try {
		            PType type = PType.valueOf(parts[1]);
		            dexType = type;
		            SwingUtilities.getWindowAncestor(cheats).dispose();
		        } catch (IllegalArgumentException g) {
		            JOptionPane.showMessageDialog(null, "Dex Type reset");
		            dexType = null;
		        }
		        newDex = null;
		    }
		} else if (code.startsWith("Shae")) {
			String[] parts = code.split(" ");
		    if (parts.length >= 3) {
		        try {
		            int id = Integer.parseInt(parts[1]);
		            int level = Integer.parseInt(parts[2]);
		            p.catchPokemon(new Pokemon(id, level, true, false));
		            SwingUtilities.getWindowAncestor(cheats).dispose();
		        } catch (NumberFormatException g) {
		            // Handle invalid input (e.g., if the entered value is not a valid integer)
		        	JOptionPane.showMessageDialog(null, "Invalid Pokemon ID/Level.");
		        }
		    }
		} else if (code.equals("UPDATE")) {
			p.update(gp);
    	    
    	    JOptionPane.showMessageDialog(null, "Player successfully updated!");
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("HP")) {
			String message = "";
			for (Pokemon p : p.team) {
				if (p != null) {
	    			message += p.nickname + " : ";
	    			message += p.determineHPType();
	    			message += "\n";
				}
			}
			
    	    
    	    JOptionPane.showMessageDialog(null, message);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("nei")) {
			for (int i = 0; i < p.pokedex.length; i++) {
				p.pokedex[i] = 2;
			}
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GENN")) {
			Pokemon po = Item.displayGenerator(null);
			p.catchPokemon(po, false);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("ASH KETCHUP")) {
			p.trainersBeat = new boolean[Trainer.MAX_TRAINERS];
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("ASH MUSTARD")) {
			for (int i = 0; i < p.trainersBeat.length; i++) {
				p.trainersBeat[i] = true;
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("exptrainer")) {
			StringBuilder result = new StringBuilder();
			for (boolean value : p.trainersBeat) {
				result.append(value).append(",");
			}
			try {
				FileWriter writer = new FileWriter("./docs/trainers.txt");
				writer.write(result.toString());
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("expitem")) {
			StringBuilder result = new StringBuilder();

		    for (boolean[] row : p.itemsCollected) {
		        for (boolean value : row) {
		            result.append(value).append(",");
		        }
		        result.append("\n");
		    }

			try {
				FileWriter writer = new FileWriter("./docs/items.txt");
				writer.write(result.toString());
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("EDGEMEDADDY")) {
			p.bag.add(Item.EDGE_KIT);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GIMMIE")) {
			for (int i = 0; i < p.bag.count.length; i++) {
				p.bag.count[i] = 1;
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("ITEMHASH")) {
			// Ensure all items from Item.values() are present in the map with at least 0 count
			for (Item item : Item.values()) {
			    gp.aSetter.itemMap.putIfAbsent(item, 0);
			}

			// Print items in the order they appear in Item.values()
			for (Item item : Item.values()) {
			    System.out.println(item + "," + gp.aSetter.itemMap.get(item));
			}

			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("MVFX")) {
			p.deleteInvalidMoves();
			SwingUtilities.getWindowAncestor(cheats).dispose();
		}
	}

	public void setupPlayerImages(boolean visor) {
		if (visor) {
			up1 = setup("/player/redV2");
			up2 = setup("/player/redV2_1");
			up3 = setup("/player/redV2_2");
			up4 = setup("/player/redV2_3");
			down1 = setup("/player/redV1");
			down2 = setup("/player/redV1_1");
			down3 = setup("/player/redV1_2");
			down4 = setup("/player/redV1_3");
			left1 = setup("/player/redV3");
			left2 = setup("/player/redV3_1");
			left3 = setup("/player/redV3_2");
			left4 = setup("/player/redV3_3");
			right1 = setup("/player/redV4");
			right2 = setup("/player/redV4_1");
			right3 = setup("/player/redV4_2");
			right4 = setup("/player/redV4_3");
		} else {
			up1 = setup("/player/red2");
			up2 = setup("/player/red2_1");
			up3 = setup("/player/red2_2");
			up4 = setup("/player/red2_3");
			down1 = setup("/player/red1");
			down2 = setup("/player/red1_1");
			down3 = setup("/player/red1_2");
			down4 = setup("/player/red1_3");
			left1 = setup("/player/red3");
			left2 = setup("/player/red3_1");
			left3 = setup("/player/red3_2");
			left4 = setup("/player/red3_3");
			right1 = setup("/player/red4");
			right2 = setup("/player/red4_1");
			right3 = setup("/player/red4_2");
			right4 = setup("/player/red4_3");
		}
	}

	public static String getMetAt() {
		int parenthesisIndex = PlayerCharacter.currentMapName.indexOf('(');
		return (parenthesisIndex == -1 || PlayerCharacter.currentMapName.contains("pt.")) ?
				PlayerCharacter.currentMapName.trim() : PlayerCharacter.currentMapName.substring(0, parenthesisIndex).trim();
	}
	
	public void setClerkItems() {
		for (Entity clerk : gp.aSetter.clerks) {
    		clerk.setItems(true, getItems());
    	}
	}

	public Pokemon[] getPokemonOfType(Pokemon[] dex) {
		if (dexType == null) return dex;
		if (newDex != null) return newDex;
		ArrayList<Pokemon> result = new ArrayList<>();
		for (Pokemon p : Player.pokedex1) {
			if (p.type1 == dexType || p.type2 == dexType) {
				result.add(p);
			}
		}
		for (Pokemon p : Player.pokedex2) {
			if (p.type1 == dexType || p.type2 == dexType) {
				result.add(p);
			}
		}
		for (Pokemon p : Player.pokedex3) {
			if (p.type1 == dexType || p.type2 == dexType) {
				result.add(p);
			}
		}
		for (Pokemon p : Player.pokedex4) {
			if (p.type1 == dexType || p.type2 == dexType) {
				result.add(p);
			}
		}
		Pokemon[] resultArray = result.toArray(new Pokemon[1]);
		newDex = resultArray;
		return resultArray;
	}
}
