package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import object.*;
import overworld.*;
import util.*;
import pokemon.*;
import puzzle.Puzzle;

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
		gp.currentMap = Player.spawn[0][0];
		worldX = gp.tileSize * Player.spawn[0][1];
		worldY = gp.tileSize * Player.spawn[0][2];
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
		if ((keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) || ice) {
			if (!ice) {
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
			
			if (spriteCounter > 12 - speed) {
				spriteNum++;
				if (spriteNum > 4) {
					spriteNum = 1;
				}
				spriteCounter = 0;
				p.steps++;
				cooldown++;
			}
			if (p.steps % 129 == 0) {
				ArrayList<Pair<Egg, Integer>> eggs = new ArrayList<>();
				boolean fast = false;
				for (int i = 0; i < p.team.length; i++) {
					Pokemon po = p.team[i];
            		if (po != null) {
            			po.awardHappiness(1, false);
            			if (po instanceof Egg) eggs.add(new Pair<>(((Egg) po), i));
            			if (po.ability == Ability.FLAME_BODY || po.ability == Ability.MAGMA_ARMOR || po.ability == Ability.HEAT_COMPACTION) fast = true;
            		}
            	}
				for (Pair<Egg, Integer> pair : eggs) {
					Egg e = pair.getFirst();
					boolean hatch = e.step(fast);
					if (hatch) {
						resetSpriteNum();
						int index = pair.getSecond();
						Pokemon h = e.hatch();
						if (p.nuzlocke && h instanceof Egg) {
							gp.setTaskState();
							String message = p.isDupes(h) ? h.getName() + " Egg in slot " + (index+1) + " activates Dupes Clause!" : "you've already gotten an encounter here! Try somewhere else!";
							Task.addTask(Task.TEXT, Item.breakString("Oh... the Egg is trying to hatch, but " + message, UI.MAX_TEXTBOX));
							e.cycles = 1;
						} else {
							p.pokedex[h.id] = 2;
							p.team[index] = h;
							if (index == 0) gp.player.p.setCurrent(h);
							gp.setTaskState();
							Task.addTask(Task.TEXT, "Oh?");
							Task.addTask(Task.TEXT, h.name() + " hatched from the egg!");
							Task t = Task.addTask(Task.NICKNAME, "Would you like to nickname " + h.name() + "?", h);
							t.wipe = true; // to reset the nickname when the task gets up	
						}
						break;
					}
				}
				if (p.nursery != null) {
					p.nursery.checkForEgg(gp);
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
					snapToTile();
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
					snapToTile();
				}
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
				resetSpriteNum();
				p.repel = false;
				if (p.bag.contains(0)) {
					gp.gameState = GamePanel.USE_REPEL_STATE;
				} else {
					gp.ui.showMessage("Repel's effects wore off.");
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
		
		if (keyH.dPressed && !ice) {
			resetSpriteNum();
			gp.gameState = GamePanel.MENU_STATE;
		}
		if (keyH.sPressed || ice) {
			speed = 8;
		} else {
			speed = 4;
		}
		if (!ice) {
			for (int i = 0; i < gp.npc[1].length; i++) {
				int trainer = gp.npc[gp.currentMap][i] == null ? 0 : gp.npc[gp.currentMap][i].trainer;
				if (gp.ticks % 4 == 0 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "down") trainerSpot(gp.npc[gp.currentMap][i]);
				if (gp.ticks % 4 == 1 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "up") trainerSpot(gp.npc[gp.currentMap][i]);
				if (gp.ticks % 4 == 2 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "left") trainerSpot(gp.npc[gp.currentMap][i]);
				if (gp.ticks % 4 == 3 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "right") trainerSpot(gp.npc[gp.currentMap][i]);
			}
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
				} else if (target instanceof NPC_Dealer) {
					interactDealer(((NPC_Dealer) target));
				} else if (target instanceof NPC_Mine) {
					interactMine(((NPC_Mine) target));
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
			trySurfing(Move.SURF, true);
			trySurfing(Move.LAVA_SURF, false);
			
			// Check iTiles
			int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
			if (iTileIndex != 999) {
				InteractiveTile target = gp.iTile[gp.currentMap][iTileIndex];
				interactWith(target, iTileIndex, p.ghost);
			}
		}
		if (keyH.aPressed) {
			if (keyH.ctrlPressed) {
				resetSpriteNum();
				Item.useCalc(p.getCurrent(), null, null, true);
			} else {
				if (p.registeredItem == null) {
					gp.ui.showMessage(Item.breakString("A Key Item can be registered when you press [A], go to your bag!", UI.MAX_TEXTBOX));
				} else {
					gp.ui.useItem(p.registeredItem, true);
				}
			}
		}
	}
	
	public void useRepel() {
		p.repel = true;
		p.steps = 1;
		p.bag.remove(Item.REPEL);
		gp.ui.currentItems = p.getItems(gp.ui.currentPocket);
	}
	
	private void snapToTile() {
		int snapX = (int) Math.round(worldX * 1.0 / gp.tileSize);
		int snapY = (int) Math.round(worldY * 1.0 / gp.tileSize);
		snapX *= gp.tileSize;
		snapY *= gp.tileSize;
		this.worldX = snapX;
		this.worldY = snapY;
	}

	private void trySurfing(Move move, boolean isSurf) {
		if ((p.knowsMove(move) || p.ghost) && ((isSurf && !p.surf) || (!isSurf && !p.lavasurf))) {
			int badgeReq = p.getRequiredBadges(move);
			int result = gp.cChecker.checkTileType(this);
			List<Integer> tileList = isSurf ? gp.tileM.getWaterTiles() : gp.tileM.getLavaTiles();
			if (tileList.contains(result)) {
				if (p.badges >= badgeReq) {
					int x = 0, y = 0;
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
					
					Color c = isSurf ? new Color(50,184,255) : new Color(255,48,48);
					if (isSurf) p.surf = true; else p.lavasurf = true;
					gp.keyH.wPressed = false;
					generateParticle(x, y, c, 6, 1, 20);
					
					for (Integer i : tileList) {
						gp.tileM.tile[i].collision = false;
					}
				} else {
					String surfaceType = isSurf ? "water" : "lava";
					String moveName = move.toString();
					String badgeWord = badgeReq == 1 ? "badge" : "badges";
					String message = String.format("This %s can be crossed!\n(You need %d %s to use %s outside of battle!)", surfaceType, badgeReq, badgeWord, moveName);
					gp.ui.showMessage(Item.breakString(message, UI.MAX_TEXTBOX));
				}
			}
		}
	}

	public void resetSpriteNum() {
		spriteCounter = 8;
		spriteNum = 1;
	}

	public void interactWith(Entity target, int index, boolean override) {
		resetSpriteNum();
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
		} else if (target instanceof CasinoTable) {
			interactCasinoTable(index);
		} else if (target instanceof Locked_Door) {
			interactLockedDoor(index);
		} else if (target instanceof Fuse_Box) {
			interactFuseBox(index);
		} else if (target instanceof Snowball) {
			interactSnowball(index, override);
		} else if (target instanceof IceBlock) {
			interactIceBlock(index, override);
		} else if (target instanceof Painting) {
			interactPainting((Painting) target);
		} else if (target instanceof Statue) {
			interactStatue((Statue) target);
		} else if (target instanceof IceChunk) {
			interactIceChunk();
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
		if (p.nuzlocke && p.wholeTeamOverLevelCap()) {
			gp.wipe(true, null, null, false);
			return;
		}
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
		if (p.wiped()) return;
		if (p.nuzlocke && p.wholeTeamOverLevelCap()) {
			gp.wipe(true, null, null, false);
			return;
		}
		gp.setTaskState();
		
		Pokemon foe = gp.encounterPokemon(area, type, p.random);
		if (foe == null) {
			gp.ui.checkTasks = true;
			gp.gameState = GamePanel.PLAY_STATE;
			return;
		}
		
		Task.addStartBattleTask(-2, -1, foe, type);
	}
	
	public void startFish(String area) {
		startWild(area, 'F');
	}

	private void interactPC(NPC_PC target) {
		if (!target.isGauntlet()) {
			target.direction = "up";
		}
		p.heal();
		
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
			
			for (Item i : chest.inventory) {
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
    	JLabel levelCap = new JLabel();
    	levelCap.setText("Level Cap: " + Trainer.getLevelCap(p.badges));
    	JButton nuzlockeInfo = new JButton("Nuzlocke Info");
    	nuzlockeInfo.addActionListener(e -> p.showNuzlockeInfo());
    	
    	JTextField cheats = new JTextField();
    	cheats.addActionListener(e -> {
    		String code = cheats.getText();
    		runCode(code, cheats);
    	});
    	
    	playerInfo.add(moneyLabel);
    	playerInfo.add(badgesLabel);
    	if (p.nuzlocke) {
    		playerInfo.add(levelCap);
    		playerInfo.add(nuzlockeInfo);
    	}
    	playerInfo.add(cheats);
    	System.out.println(gp.tileM.lavaMaps.toString());
    	
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
		resetSpriteNum();
		if (face) npc.facePlayer(direction);
		if (npc.flag == -1 || !p.flag[npc.getFlagX()][npc.getFlagY()]) {
			gp.gameState = GamePanel.DIALOGUE_STATE;
			gp.ui.npc = npc;
			npc.speak(0);
			if (npc.scriptIndex >= 0) {
				SwingUtilities.invokeLater(() -> {
					gp.keyH.resetKeys(false);
					gp.setTaskState();
					gp.script.runScript(npc);
				});
			}
		} else if (npc.altDialogue != null) {
			gp.setTaskState();
			
			npc.speak(1);
		}
	}
	
	public void interactDealer(NPC_Dealer npc) {
		gp.keyH.wPressed = false;
		resetSpriteNum();
		npc.facePlayer(direction);
		
		gp.ui.npc = npc;
		
		gp.setTaskState();
		gp.keyH.resetKeys(false);
		npc.speak(0);
	}
	
	private void interactCasinoTable(int i) {
		CasinoTable table = (CasinoTable) gp.iTile[gp.currentMap][i];
		interactDealer(table.getDealer());
	}
	
	private void interactMine(NPC_Mine mine) {
		gp.keyH.wPressed = false;
		resetSpriteNum();
		mine.facePlayer(direction);
		
		gp.setTaskState();
		if (p.getMoney() >= mine.PRICE) {
			Task t = Task.addTask(Task.CONFIRM, mine, "Hi there! If you pay me $" + mine.PRICE + ", I'll head into my mine and see what I can dig up for you!\n(Warning: Will Auto-Save)", 17);
			t.ui = Task.MONEY;
		} else {
			Task.addTask(Task.DIALOGUE, mine, "I'm sorry, you don't have enough money for my expert services. Come back with at least $" + mine.PRICE + "!");
		}
	}
	
	private void interactCutTree(int i, boolean override) {
		String message = "This tree looks like it can be cut down!";
		Move m = Move.CUT;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
				gp.keyH.wPressed = false;
				Cut_Tree temp = (Cut_Tree) gp.iTile[gp.currentMap][i];
				gp.iTile[gp.currentMap][i] = new Tree_Stump(gp);
				gp.iTile[gp.currentMap][i].worldX = temp.worldX;
				gp.iTile[gp.currentMap][i].worldY = temp.worldY;
				
				generateParticle(temp);
			} else {
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactRockSmash(int i, boolean override) {
		String message = "This rock looks like it can be broken!";
		Move m = Move.ROCK_SMASH;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
				gp.keyH.wPressed = false;
				generateParticle(gp.iTile[gp.currentMap][i]);
				gp.iTile[gp.currentMap][i] = null;
			} else {
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactVines(int i, boolean override) {
		String message = "This gap looks like it can be crossed!";
		Move m = Move.VINE_CROSS;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
				gp.keyH.wPressed = false;
				Vine_Crossable temp = (Vine_Crossable) gp.iTile[gp.currentMap][i];
				gp.iTile[gp.currentMap][i] = new Vine(gp);
				gp.iTile[gp.currentMap][i].worldX = temp.worldX;
				gp.iTile[gp.currentMap][i].worldY = temp.worldY;
				generateParticle(temp);
			} else {
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactPit(int i, boolean override) {
		String message = "This pit looks deep!\nI can't even see the bottom!";
		if (gp.currentMap >= 197 && gp.currentMap <= 202) override = true;
		Move m = Move.SLOW_FALL;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
				gp.keyH.wPressed = false;
				Pit pit = (Pit) gp.iTile[gp.currentMap][i];
				gp.eHandler.teleport(pit.mapDest, pit.xDest, pit.yDest, false);
				generateParticle(pit);
			} else {
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactWhirlpool(int i, boolean override) {
		String message = "This water vortex can be crossed!";
		Move m = Move.WHIRLPOOL;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
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
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactRockClimb(int i, boolean override) {
		String message = "This wall looks like it can be scaled!";
		if (gp.inSpace()) override = true;
		Move m = Move.ROCK_CLIMB;
		if (override || p.knowsMove(m)) {
			int badgeReq = p.getRequiredBadges(m);
			if (p.badges >= badgeReq) {
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
				int finalX = (int) (rc.worldX + (rc.deltaX * gp.tileSize * inverse * rc.amt));
				int finalY = (int) (rc.worldY + (rc.deltaY * gp.tileSize * inverse * rc.amt));
				
				this.worldX = finalX;
				this.worldY = finalY;
			} else {
				String badgeWord = badgeReq == 1 ? "badge" : "badges";
				String fullMessage = String.format(
					"%s\n(You need %d %s to use %s outside of battle!)",
					message, badgeReq, badgeWord, m.toString()
				);
				gp.ui.showMessage(Item.breakString(fullMessage, UI.MAX_TEXTBOX));
			}
		} else {
			gp.ui.showMessage(message);
		}
	}
	
	private void interactStarterMachine(int i) {
		if (!p.flag[0][0]) { // Before talking to Dad first
			gp.ui.showMessage("It's a machine housing three rare Pokemon!");
		} else if (p.flag[0][0] && !p.flag[0][1]) { // After talking to Dad and before picking a starter
			gp.keyH.resetKeys(false);
			gp.gameState = GamePanel.STARTER_STATE;
		} else if (p.flag[0][1] && !p.flag[0][4]) { // After picking a starter and before the first gate
			gp.ui.showMessage(Item.breakString("There are two Pokemon still inside the machine. Wonder what Dad will do with them?", UI.MAX_TEXTBOX));
		} else if (p.flag[0][4] && p.badges < 1) { // After the first gate but before beating Gym 1
			gp.ui.showMessage(Item.breakString("There's still a Pokemon left! Dad must've given one to Scott as well!", UI.MAX_TEXTBOX));
		} else if (p.badges > 1 && !p.flag[1][0]) { // After beating Gym 1
			gp.ui.showMessage("There aren't any Pokemon inside.");
		} else { // Dialogue after beating Fred 1 but before Gym 2
			gp.ui.showMessage(Item.breakString("There aren't any Pokemon left, Dad must've given one to Scott and one to Fred.", UI.MAX_TEXTBOX));
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
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", UI.MAX_TEXTBOX));
			} else if (p.flag[1][5] && !p.flag[1][6]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][6] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		} else if (gp.currentMap == 16) { // power plant 2
			if (!p.flag[1][7]) {
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", UI.MAX_TEXTBOX));
			} else if (p.flag[1][7] && !p.flag[1][8]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][8] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		} else if (gp.currentMap == 18) { // office 2
			if (!p.flag[1][11]) {
				gp.ui.showMessage(Item.breakString("...the power seems to be getting sapped by something.", UI.MAX_TEXTBOX));
			} else if (p.flag[1][11] && !p.flag[1][12]) {
				gp.ui.showMessage("Powered on the fuse box!\nA clicking sound played!");
				p.flag[1][12] = true;
			} else {
				gp.ui.showMessage("The fuse box is whirring with power!");
			}
		} else if (gp.currentMap == 107) { // ghostly woods
			if (!p.flag[7][7]) { // before beating UP Pheromosa
				gp.ui.showMessage(Item.breakString("The machine hums with a sickly light. You can feel something - something wrong - pulsing inside it. An unseen force wards you away.", UI.MAX_TEXTBOX));
			} else if (p.flag[7][7] && !p.flag[7][8]) { // after beating UP Pheromosa but before triggering the cutscene
				p.flag[7][8] = true;
				interactNPC(gp.npc[107][13], false);
			} else { // after logic/faith cutscene
				gp.ui.showMessage(Item.breakString("The machine lies silent and broken. Its twisted energy is gone... but an eerie residue lingers in the air.", UI.MAX_TEXTBOX));
			}
		}
	}
	
	private void interactSnowball(int i, boolean override) {
		if (override || p.bag.contains(Item.SHOVEL)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage(Item.breakString("This pile of snow looks like it can be shoveled!", UI.MAX_TEXTBOX));
		}
		
	}
	
	private void interactIceBlock(int i, boolean override) {
		if (override || p.bag.contains(Item.ICE_PICK)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage(Item.breakString("This block of ice looks like it can be broken!", UI.MAX_TEXTBOX));
		}
		
	}
	
	private void interactPainting(Painting painting) {
		gp.setTaskState();
		Puzzle current = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
		if (painting.isColorPainting()) {
			if (painting.getColor() == null) {
				gp.gameState = GamePanel.PLAY_STATE;
				return;
			}
			if (current.isLocked()) {
				Task.addTask(Task.TEXT, "The painting is a glowing " + painting.getColor() + "...");
				if (current.isLost()) {
					Task.addTask(Task.TEXT, "I think I picked the wrong painting...");
				}
			} else {
				gp.ui.commandNum = 1;
				Task.addTask(Task.TEXT, "The painting is a vibrant " + painting.getColor() + "!");
				Task.addTask(Task.CONFIRM, painting, "Do you pick the " + painting.getColor() + " painting?", 8);
			}
		} else {
			if (painting.isMainPainting()) {
				if (!current.isStarted()) {
					current.sendToNext();
				} else {
					if (current.getFloor() == 195) current.update(p.getBetCurrency(true));
					if (current.isComplete()) {
						Task.addTask(Task.TEXT, "...The painting erupted in light!");
						Entity dragon = new Entity(gp, "Dragon");
						gp.ui.commandNum = 1;
						Task.addTask(Task.DIALOGUE, dragon, "Good work child...");
						Task.addTask(Task.CONFIRM, dragon, "Are you ready to move on to the next room?", 9);
					} else {
						Task.addTask(Task.TEXT, "...the painting is as still as the night.");
					}
				}
			} else if (painting.isResetPainting()) {
				if (current != null && !current.isStarted()) {
					gp.puzzleM.sendToStart(gp.puzzleM.FAITH_START);
				} else {
					gp.ui.commandNum = 1;
					Task.addTask(Task.TEXT, "...the painting is dull and empty.");
					Task t = Task.addTask(Task.CONFIRM, "Would you like to reset the gauntlet and go back to the first room?", 10);
					t.wipe = true;
				}
			} else if (painting.isBetPainting()) {
				gp.keyH.wPressed = false;
				if (painting.getColor().equals("bet")) {
					gp.gameState = GamePanel.PLAY_STATE;
					return;
				}
				if (painting.getColor().equals("bj")) {
					Task t = Task.addTask(Task.BLACKJACK, "");
					t.wipe = true;
				} else if (painting.getColor().equals("battle")) {
					if (p.getBetCurrency(true) >= Player.BET_INC) {
						if (Pokemon.sets.isEmpty()) {
							gp.ui.showMessage("Loading sets...");
							SwingUtilities.invokeLater(() -> {
								Pokemon.loadCompetitiveSets();
							});
						}
						Task t = Task.addTask(Task.ODDS, "Calculating odds...");
						t.wipe = true;
					} else {
						Task.addTask(Task.TEXT, "I'm sorry, you don't have enough orbs to bet with.");
					}
				}
			}
		}
	}
	
	private void interactStatue(Statue statue) {
		gp.setTaskState();
		Puzzle current = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
		if (statue.isReset()) {
			if (gp.currentMap == 198) {
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.I_TILE, "", 198);
				Task.addTask(Task.FLASH_OUT, "");
			} else {
				if (!current.isStarted()) {
					gp.puzzleM.sendToStart(gp.puzzleM.LOGIC_START);
				} else {
					gp.ui.commandNum = 1;
					Task.addTask(Task.TEXT, "...the statue pedestal is dull and empty.");
					Task t = Task.addTask(Task.CONFIRM, "Would you like to reset the gauntlet and go back to the first room?", 10);
					t.wipe = false;
				}
			}
		} else {
			if (!current.isStarted()) {
				current.sendToNext();
			} else {
				if (current.isComplete()) {
					Task.addTask(Task.TEXT, "...The statue came to life!");
					Entity dragon = new Entity(gp, "Dragon");
					gp.ui.commandNum = 1;
					Task.addTask(Task.DIALOGUE, dragon, "Good work child...");
					Task.addTask(Task.CONFIRM, dragon, "Are you ready to move on to the next room?", 9);
				} else {
					Task.addTask(Task.TEXT, "...the statue is as quiet as the void.");
				}
			}
		}
	}
	
	private void interactIceChunk() {
		gp.ui.showMessage(Item.breakString("The ice still whispers with the cold wind...", UI.MAX_TEXTBOX));
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
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("M1X3R")) {
			p.random = !p.random;
			String onoff = p.random ? "on!" : "off.";
			JOptionPane.showMessageDialog(null, "Randomizer mode was turned " + onoff);
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GASTLY")) {
			p.ghost = !p.ghost;
			inTallGrass = false;
			String onoff = p.ghost ? "on!" : "off.";
			JOptionPane.showMessageDialog(null, "Walk-through-walls mode was turned " + onoff);
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("LIGMA")) {
			for (Pokemon pokemon : p.team) {
				if (pokemon != null) pokemon.heal();
			}
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("BALLZ")) {
			JPanel panel = p.displayTweaker();
			JOptionPane.showMessageDialog(null, panel);
			p.invalidateNuzlocke("Used " + code);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("KANY3")) {
			p.setMoney(1000000);
			p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
			gp.aSetter.setObject();
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("Ben")) {
			p.catchPokemon(new Pokemon(238, 5, true, false));
			p.invalidateNuzlocke("Used " + code);
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
		            p.invalidateNuzlocke("Used " + code);
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
			p.invalidateNuzlocke("Used " + code);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GENN")) {
			Pokemon po = Item.displayGenerator(null);
			p.catchPokemon(po, false);
			p.invalidateNuzlocke("Used " + code);
    	    SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("ASH KETCHUP")) {
			p.trainersBeat = new boolean[Trainer.MAX_TRAINERS];
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("ASH MUSTARD")) {
			for (int i = 0; i < p.trainersBeat.length; i++) {
				p.trainersBeat[i] = true;
			}
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("exptrainer")) {
			StringBuilder result = new StringBuilder();
			for (boolean value : p.trainersBeat) {
				result.append(value).append(",");
			}
			try {
				FileWriter writer = new FileWriter("./trainers.txt");
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
				FileWriter writer = new FileWriter("./items.txt");
				writer.write(result.toString());
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("EDGEMEDADDY")) {
			p.bag.add(Item.EDGE_KIT);
			p.invalidateNuzlocke("Used " + code);
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("GIMMIE")) {
			for (int i = 0; i < p.bag.count.length; i++) {
				p.bag.count[i] = 1;
			}
			p.invalidateNuzlocke("Used " + code);
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
		} else if (code.startsWith("dicklover")) {
			String[] parts = code.split(" ");
			if (parts.length == 2) {
				try {
					int map = Integer.parseInt(parts[1]);
					gp.eHandler.teleport(map, worldX / gp.tileSize, worldY / gp.tileSize, false);
					p.invalidateNuzlocke("Used " + code);
					SwingUtilities.getWindowAncestor(cheats).dispose();
				} catch (NumberFormatException g) {
		        	JOptionPane.showMessageDialog(null, "Invalid map ID.");
		        }
			}
		} else if (code.equals("leveldown")) {
			p.current.level = Math.max(p.current.level - 1, 1);
			p.current.setStats();
			p.current.verifyHP();
			p.invalidateNuzlocke("Used " + code);
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
		int parenthesisIndex = currentMapName.indexOf('(');
		return (parenthesisIndex == -1 || currentMapName.contains("pt.")) ?
				currentMapName.trim() : currentMapName.substring(0, parenthesisIndex).trim();
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
