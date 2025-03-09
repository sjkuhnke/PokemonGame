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
					interactTrainer(target, pokemon.id, pokemon.id == 159);
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
				keyH.ctrlPressed = false;
				resetSpriteNum();
				Item.useCalc(p.getCurrent(), null, null);
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
			if (npc.more) {
				SwingUtilities.invokeLater(() -> {
					gp.keyH.resetKeys();
					gp.setTaskState();
					interactBlock(npc);
				});
			}
		} else if (npc.altDialogue != null) {
			gp.setTaskState();
			
			npc.speak(1);
		}
	}
	
	private void interactBlock(Entity npc) {
		if (gp.currentMap == 52) { // Professor Dad
			if (!p.flag[0][0]) {
				p.flag[0][0] = true;
				Task.addTask(Task.DIALOGUE, npc, "Welcome to the wonderful world of Pokemon! I'm sure you're familiar with them considering your old man is a top scientist here in Xhenos.");
				Task.addTask(Task.DIALOGUE, npc, "But, as a reminder, Pokemon are pocket monsters with unique typings and traits that coexist with us humans.");
				Task.addTask(Task.DIALOGUE, npc, "And I think it's high time you go on a Pokemon adventure of your own, get outta town, and see what this region has to offer.");
				Task.addTask(Task.DIALOGUE, npc, "You see that machine over there son? I've started a program to help new trainers, and you and two other bright young boys are to be the first participants.");
				Task.addTask(Task.DIALOGUE, npc, "Inside that machine holds three Pokemon I have chosen to be the starting options. So go pick your starter!");
			} else if (p.flag[0][0] && !p.flag[0][1]) {
				Task.addTask(Task.DIALOGUE, npc, "Go ahead, go pick your starter from my machine! The world of Xhenos awaits!");
			} else if (p.flag[0][1] && !p.flag[0][2]) {
				p.flag[0][2] = true;
				Task.addTask(Task.DIALOGUE, npc, "You picked " + p.team[0].getName() + "? Wonderful pick! It seems very eager to get out and explore, as I'm sure you are too.");
				Task.addTask(Task.DIALOGUE, npc, "But!");
				Task.addTask(Task.DIALOGUE, npc, "As your Dad, I must make sure you have adequate equipment to not get lost out there.");
				Task.addTask(Task.DIALOGUE, npc, "So first, I'm giving you this digital map, equipped with Cellular Data that will always update with where you are!");
				Task.addTask(Task.TEXT, "You got the Map!");
				Task.addTask(Task.DIALOGUE, npc, "And as your Professor, I need your help for collecting as much data about the Pokemon inhabiting our world with us!");
				Task.addTask(Task.DIALOGUE, npc, "This little doohickey is the Neodex! In a region as unique as ours, I've had to make plenty of modifications to account for them.");
				Task.addTask(Task.DIALOGUE, npc, "It's one of my finest inventions yet, and I even got help from Professor Oak, the greatest professor of all time!");
				Task.addTask(Task.DIALOGUE, npc, "Instead of Rotom, it taps into a shared database that allows for identifying new forms for old Pokemon. Give it a whirl!");
				Task.addTask(Task.TEXT, "You got the Pokedex!");
				Task.addTask(Task.DIALOGUE, npc, "Oh right! Speaking of collecting data, I made a little something to help you find as many different species as you can.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.DEX_NAV;
				Task.addTask(Task.DIALOGUE, npc, "Just open your bag and open the \"Key Items\" pocket, you should see it there. When you're near grass, it'll show you the Pokemon there!");
				Task.addTask(Task.DIALOGUE, npc, "I would've installed it in the Neodex, but I have all of the slots reserved for special database add-ons to keep track of all the unique forms here.");
				Task.addTask(Task.DIALOGUE, npc, "Speaking of which, as you know son, my specialty as a Professor is studying Shadow Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "These Pokemon had their DNA changed long ago by the meteor in Shadow Ravine, which happens to be just north of here.");
				Task.addTask(Task.DIALOGUE, npc, "They often have taken Dark and Ghost typings, and have significant changes in mentality, similar to Xhenovian Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "Here, can I see your Pokedex for a second?");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "There, I added a 'Shadow Pokedex' section to your Neodex. That way, if you run into any Shadow forms near Shadow Ravine, you can record them!");
				Task.addTask(Task.DIALOGUE, npc, "...What's that? Oh right, I should probably explain how to use the Neodex.");
				Task.addTask(Task.DIALOGUE, npc, "You can see a Pokemon you've registered in the Neodex and their information, like their moves, abilities, typings, and much more!");
				Task.addTask(Task.DIALOGUE, npc, "If you want to toggle which section you're in, just press the 'A' key and it will cycle through all of your sections you have installed!");
				Task.addTask(Task.DIALOGUE, npc, "Oh and one more thing! Oh my Arceus, I can't believe I almost forgot to tell you about my favorite creation yet!");
				Task.addTask(Task.DIALOGUE, npc, "You've always loved battling, so I made this just for you. A state of the line BATTLECALC 3000! Or you could just call it your calculator.");
				Task.addTask(Task.DIALOGUE, npc, "It provides accurate data during a battle, just press 'A' in a battle, or 'Ctrl + A' outside, and you can check how much damage a move can do!");
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.CALCULATOR;
				Task.addTask(Task.DIALOGUE, npc, "Now go out there and make me proud - and most importantly...");
				Task.addTask(Task.DIALOGUE, npc, "COLLECT THAT DATA!");
				Task.addTask(Task.UPDATE, "");
			} else if (p.flag[0][5] && !p.flag[0][21]) {
				Task.addTask(Task.DIALOGUE, npc, "Have you seen any new Shadow forms? Can I take a look?");
				Pokemon[] sDex = p.getDexType(1);
				int amt = 0;
				for (Pokemon po : sDex) {
					if (p.pokedex[po.id] == 2) amt++;
				}
				if (!p.flag[0][20] && amt >= 1) {
					Task.addTask(Task.DIALOGUE, npc, "Oh nice! You've seen " + amt + " forms!");
					Task.addTask(Task.DIALOGUE, npc, "Here son, take this for helping me out! You'd get better use out of it than me, anyways!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.AMULET_COIN;
					p.flag[0][20] = true;
				}
				if (amt >= Pokemon.POKEDEX_METEOR_SIZE) {
					Task.addTask(Task.DIALOGUE, npc, "Finn!! You did it! You finished the Shadow Pokedex I gave you!");
					Task.addTask(Task.DIALOGUE, npc, "This item is extremely rare, so please, use it wisely!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.MASTER_BALL;
					p.flag[0][21] = true;
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "How's it going?");
			}
		} else if (gp.currentMap == 51) { // Dad's Mom
			if (!p.flag[0][3]) {
				Task.addTask(Task.DIALOGUE, npc, "Ah, I remember my first time having a Pokemon adventure, before I had your father. I was around your age now actually. Brings back fond memories...");
				Task.addTask(Task.DIALOGUE, npc, "...Oh, you want advice? I believe being a Pokemon trainer isn't about being the strongest, or collecting them all.");
				Task.addTask(Task.DIALOGUE, npc, "It's about forming a life-long bond with your Pokemon, becoming partners. Friendship is key to becoming a better trainer, and a better person.");
				Task.addTask(Task.DIALOGUE, npc, "That's how it was with your grandfather. Back in my day, I wanted to be the best there ever was.");
				Task.addTask(Task.DIALOGUE, npc, "He helped me see the value in friendship, how Pokemon were great companions in life. He's the person who gave me Duchess' dinky old Soothe Bell.");
				Task.addTask(Task.DIALOGUE, npc, "I miss him every day, even with my Pokemon, you and your father by my side.");
				Task.addTask(Task.DIALOGUE, npc, "So, this is my parting gift to you, to show you the power of friendship. I'm so thankful to have been your grandmother.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.SOOTHE_BELL;
				Task.addTask(Task.DIALOGUE, npc, "Go out there and make the world a better place, dear. I know you will.");
				p.flag[0][3] = true;
			}
		} else if (gp.currentMap == 3) {
			Task.addTask(Task.DIALOGUE, npc, "I believe he was looking to introduce himself to you, he mentioned he was heading towards New Minnow Town.");
			Task.addTask(Task.DIALOGUE, npc, "Please do make haste, I do hope he's okay.");
			p.flag[0][4] = true;
		} else if (gp.currentMap == 47 && p.secondStarter != -1) {
			Task.addTask(Task.DIALOGUE, npc, "Here, we breed and house rare Pokemon to fight against their extinction.");
			Task.addTask(Task.DIALOGUE, npc, "...What's that? You have a " + Pokemon.getName(((p.starter + 1) * 3) - 2) + "?? That's insanely rare. Did you get that from the professor?");
			Task.addTask(Task.DIALOGUE, npc, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task t = Task.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		} else if (gp.currentMap == 4) {
			if (p.flag[0][11] && !p.flag[0][12]) {
				p.flag[0][12] = true;
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "What, you've got a package, pipsqueak? Pfft! You can't be older than 10... Didn't know Robin stooped that low.");
				Task.addTask(Task.DIALOGUE, npc, "Last time I checked, the boss wasn't expecting any packages. You can't even get in here anyway, I JUST locked the door and I'm getting the hell outta here.");
				Task.addTask(Task.DIALOGUE, npc, "I'm running off to the woods and you'll NEVER find me in there, don't even TRY to follow me.");
				Task.addTask(Task.DIALOGUE, npc, "So yeah, big whoop. Tell that bundle of nerves - A.K.A. your boss - to deal with it. See ya idiot!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "BEAT IT!");
			}
		} else if (gp.currentMap == 161) {
			if (!p.flag[0][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Sorry, I'm a little busy right now. As the only mailman in this entire region, I've been running ragged delivering letters across Xhenos.");
				Task.addTask(Task.DIALOGUE, npc, "I can't afford to waste any time, even for a gym battle.");
				Task.addTask(Task.DIALOGUE, npc, "Oh? You'll help me? Thank you, thank you, thank you! Maybe then I'll have enough time to open the gym... Here, take these!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_A;
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_B;
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_C;
				p.flag[0][7] = true;
			} else if (p.flag[0][7] && !p.flag[0][11]) {
				if (p.flag[0][8] && p.flag[0][9] && p.flag[0][10]) {
					Task.addTask(Task.DIALOGUE, npc, "Oh it's you, thanks for delivering all those packages! You're a lifesaver, and I truly thank you for being selfless.");
					Task.addTask(Task.DIALOGUE, npc, "But... there's one last package to deliver.");
					Task.addTask(Task.DIALOGUE, npc, "It's been sitting here for weeks for the warehouse, but I can't get in to the building and the owner isn't answering any of my calls.");
					Task.addTask(Task.DIALOGUE, npc, "I've been far too busy to look into it though, could you look into it for me? I've seen some pretty suspicious activity going on there.");
					Task.addTask(Task.DIALOGUE, npc, "Oh, you will? Thank you so much! Don't worry, it's just a short walk away. Once that's done, I'll have a small window where I can open the gym.");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.PACKAGE_D;
					p.flag[0][11] = true;
					Task.addTask(Task.DIALOGUE, npc, "Welp, the clock is ticking, and I need more caffeine.");
				} else {
					Task.addTask(Task.DIALOGUE, npc, "The addresses are on the back of the boxes. You're doing me a massive favor, so once you're done I can give you that battle. So get to it!");
				}
			} else if (p.flag[0][11] && !p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello my helper! How's it going at the warehouse? Did you manage to get in okay?");
			} else if (p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you've delivered my last package? I can't thank you enough, I can finally have a break from all this mail. I'll open the gym up right away, with no delay!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				p.flag[0][16] = true;
			}
		} else if (gp.currentMap == 57) {
			if (p.flag[0][7] && !p.flag[0][9]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh! That's my new frying pan! Thanks squirt, I owe you something.");
				Task.addTask(Task.DIALOGUE, npc, "I got a little trinket I've been wanting to get rid of - I mean pass off - to a worthy kiddo like you. Consider this your appetizer!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.FLAME_ORB;
				p.bag.remove(Item.PACKAGE_B);
				p.flag[0][9] = true;
			} else if (p.flag[0][7]) {
				if (!p.flag[0][19]) {
					Task.addTask(Task.DIALOGUE, npc, "Hey, thanks for the help squirt! Listen, I have a 5-star restaraunt in Rawwar City, you should come check it out!");
				}
				Task.addTask(Task.CONFIRM, npc, "Interested in coming to Rawwar City with me?", 5);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "What..? You've never heard of me? I have the most famous restaurant in Xhenos!");
			}
		} else if (gp.currentMap == 58) {
			if (p.flag[0][7] && !p.flag[0][17]) {
				if (!p.flag[0][8]) {
					Task.addTask(Task.DIALOGUE, npc, "...Oh, is that my package?");
					Task.addTask(Task.DIALOGUE, npc, "Thank you so much young man! Sorry for jumping at you, I'm just really unsettled right now.");
					p.bag.remove(Item.PACKAGE_A);
					p.flag[0][8] = true;
				} else {
					Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				}
				Task.addTask(Task.DIALOGUE, npc, "Some rampant Pokemon sniffing out berries all burst into my house, but I was just making juice.");
				Task.addTask(Task.DIALOGUE, npc, "In the midst of all the chaos, some crook snuck in and stole my precious item right off my poor Pokemon!");
				Task.addTask(Task.DIALOGUE, npc, "Whatever will I do to find it?");
			} else if (p.flag[0][7] && p.flag[0][17]) {
				p.flag[0][18] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Task.addTask(Task.DIALOGUE, npc, "Oh my god, my item! You found it! Wow, you really are impressive!");
				Task.addTask(Task.DIALOGUE, npc, "You know what, since you're so kind, why don't you just keep it? It'll be better off with a stronger trainer like you.");
				Task.addTask(Task.DIALOGUE, npc, "Here, take this for your troubles too. Really, it's the least I can do.");
				Task t = Task.addTask(Task.ITEM, "Obtained a Lucky Egg!");
				t.item = Item.LUCKY_EGG;
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Task.addTask(Task.DIALOGUE, npc, "I just got robbed! And at the worst time too, I was expecting a new juicer I was so excited for...");
				Task.addTask(Task.DIALOGUE, npc, "And I heard a noise at my door thinking it was Robin with my package, but it was a criminal who burst right in!");
				Task.addTask(Task.DIALOGUE, npc, "It's times like this where I wish I was still married.");
			}
		} else if (gp.currentMap == 48) {
			if (!p.flag[0][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Feel free to look around!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you have a package for us? Thank you so much! I'm sure Robin greatly appreciates the help!");
				Task.addTask(Task.DIALOGUE, npc, "Here, have a complimentary gift dog! No really, we insist.");
				
				Random dog = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = dog.nextInt(3);
				id = 120 + (id * 3);
				Pokemon dogP = new Pokemon(id, 5, true, false);
				Task.addTask(Task.TEXT, "You adopted a gift dog!");
				Task t = Task.addTask(Task.GIFT, "", dogP);
				t.item = Item.SILK_SCARF;
				p.bag.remove(Item.PACKAGE_C);
				p.flag[0][10] = true;
			}
		} else if (gp.currentMap == 46) {
			Task.addTask(Task.HP, "Check your team's Hidden Power types here!");
		} else if (gp.currentMap == 8) {
			Task.addTask(Task.DIALOGUE, npc, "And at the worst time too, I was expecting a package all the way from Galar.");
			if (p.flag[0][14] && !p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "What's that? You have the package for me? That must be my lucky stapler! Please hand it here, young one.");
				Task.addTask(Task.DIALOGUE, npc, "Robin must be swamped if he's making you do all this delivery work. Is this his version of a gym puzzle?");
				Task.addTask(Task.DIALOGUE, npc, "Heh! I'm just messing with you, you must have volunteered. Here, for your generosity and service.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM01;
				p.bag.remove(Item.PACKAGE_D);
				p.flag[0][15] = true;
			} else if (!p.flag[0][14]) {
				Task.addTask(Task.DIALOGUE, npc, "Can you please help me get rid of these criminals? I don't have any Pokemon of my own, I won't be of much help to you I'm afraid.");
			}
		} else if (gp.currentMap == 10) {
			Task.addTask(Task.DIALOGUE, npc, "That's hello where I come from. I'm Ryder, adventurer extraordinaire at the ripe old age of 16.");
			Task.addTask(Task.DIALOGUE, npc, "Say, you look like a competent Pokemon trainer. Mind taking care of something for me?");
			Pokemon abra = new Pokemon(243, 15, true, false);
			Task.addTask(Task.TEXT, "You recieved " + abra.name() + "!");
			Task.addTask(Task.GIFT, "", abra);
			Task.addTask(Task.DIALOGUE, npc, "Yeah, I noticed Abra seems to gain a new ability here, probably because of that new magic type I've been hearing about.");
			Task.addTask(Task.DIALOGUE, npc, "Anyways, it was pleasure doing business with you mate, I'm sure I'll see you around. I gotta see what the rest of the region has in store for me!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			p.flag[1][3] = true;
		} else if (gp.currentMap == 22) { // shell bell
			Task.addTask(Task.DIALOGUE, npc, "Want to know a piece of history? This lake of lava here was formerly a beautiful blue body of water!");
			Task.addTask(Task.DIALOGUE, npc, "A long long time ago, before the volcano Mt. St. Joseph was active, this area flourished with life everywhere!");
			Task.addTask(Task.DIALOGUE, npc, "Right where I'm standing used to be a vibrant beach full of shells and coral!");
			Task.addTask(Task.DIALOGUE, npc, "In fact, if you look hard enough, you might be able to still see some fragrants!");
			Task.addTask(Task.DIALOGUE, npc, "I've spent a while collecting shell pieces to make a special item for a Pokemon to hold, called a Shell Bell!");
			Task.addTask(Task.DIALOGUE, npc, "You may have heard of the item before, but this one is special! It heals your Pokemon a whopping 25% of any damage dealt!");
			Task.addTask(Task.DIALOGUE, npc, "I'm willing to part with it, but for a price. You see, my Cleffa and Azurill will only evolve when they're happy enough...");
			Task.addTask(Task.DIALOGUE, npc, "But they're far too weak to train on their own, so I'd like to use the unique item to this region, being the Euphorian Gem!");
			Task.addTask(Task.CONFIRM, "If you have 2 Euphorian Gems to give me, I'll give you this Shell Bell. Do we have a deal?");
		} else if (gp.currentMap == 11) { // fred 1
			if (npc.worldX < 39 * gp.tileSize || npc.worldY > gp.tileSize * 65) {
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				if (worldX < 39 * gp.tileSize) {
					Task.addTask(Task.TURN, this, "", Task.RIGHT);
				} else {
					Task.addTask(Task.TURN, this, "", Task.LEFT);
				}
				Task.addNPCMoveTask('x', 39 * gp.tileSize, this, false, 1);
				if (worldY < 64 * gp.tileSize) {
					Task.addTask(Task.TURN, this, "", Task.DOWN);
					Task.addNPCMoveTask('y', 64 * gp.tileSize, this, false, 1);
				}
				Task.addTask(Task.TURN, this, "", Task.LEFT);
				Task.addNPCMoveTask('x', 32 * gp.tileSize, npc, false, 6);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addNPCMoveTask('y', 65 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 33 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addNPCMoveTask('y', 64 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 34 * gp.tileSize, npc, false, 2);
				Task.addTask(Task.SLEEP, npc, "", 60);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.TURN, gp.npc[11][9], "", Task.UP);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, npc, "", 60);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addNPCMoveTask('y', 66 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, this, "", Task.DOWN);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 39 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.INTERACTIVE, gp.iTile[11][0], "", 0);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addNPCMoveTask('y', 65 * gp.tileSize, npc, false, 2);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Heh, you must be pretty tough to make it this far, but don't get too full of yourself.");
				Task.addTask(Task.DIALOGUE, npc, "I'll gladly put an end to your winning streak right here.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Back for more, huh? Go back home, bud.");
			}
			Task.addTask(Task.DIALOGUE, npc, "You're just another weak trainer in my way.");
			Task.addTask(Task.BATTLE, "", 34);
		} else if (gp.currentMap == 13) {
			if (worldY / gp.tileSize >= 59) {
				Task.addTask(Task.DIALOGUE, npc, "Wait, you're his kid? Oh my goodness, your father has told me all about you. Did you come here trying to challenge the gym?");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "You did? Well, that's going to be a difficult task considering the city's gone into full black-out, no power or anything.");
				Task.addTask(Task.DIALOGUE, npc, "I'd try and fix it, but the Control Center door is jammed, and that's our best shot at figuring out what's wrong.");
				Task.addTask(Task.DIALOGUE, npc, "Most of the buildings in this city have electric doors, and they all seem to not be working.");
				Task.addTask(Task.DIALOGUE, npc, "Actually, you know what we could try?");
				Task.addTask(Task.DIALOGUE, npc, "We can grab enough auxillery energy to keep the Control Center door open, and then give it a go from there.");
				Task.addTask(Task.DIALOGUE, npc, "Could you meet me there? It's located at the end of Route 45.");
				Task.addTask(Task.DIALOGUE, npc, "It's up straight North of this city, and then just a bit East.");
				Task.addTask(Task.DIALOGUE, npc, "I'll go on up ahead to set everything up, please don't keep me waiting!");
				Task.addTask(Task.DIALOGUE, npc, "I'm sure your father would be proud of you for helping out.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				p.flag[1][1] = true;
			} else if (worldY / gp.tileSize < 59 && worldY / gp.tileSize >= 51) {
				Task.addTask(Task.DIALOGUE, npc, "As much as I appreciate the help, don't think I'll go easy on you. Normal types really shouldn't be underestimated.");
				Task.addTask(Task.DIALOGUE, npc, "Good luck to you both, see you guys inside.");
				p.flag[1][16] = true;
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			} else if (worldY / gp.tileSize < 51) {
				if (worldX / gp.tileSize < 50) {
					Task.addTask(Task.DIALOGUE, npc, "Wow, you beat Stanford? And you restored power to the city? You're impressive!");
					Task.addTask(Task.DIALOGUE, npc, "Say, you're helping your dad do research, right? I'm sure you've come across a couple Xhenovian forms then.");
					Task.addTask(Task.DIALOGUE, npc, "You seem like you love Pokemon so this might be obvious to you, but those Pokemon are variants of Pokemon from other regions.");
					Task.addTask(Task.DIALOGUE, npc, "My mom's friend is actually a researcher studying the forms here, and I'm sure you guys could help each other out!");
					Task.addTask(Task.DIALOGUE, npc, "She's camped out right near here, come with me, let me introduce you to her!");
					Task t = Task.addTask(Task.TELEPORT, "");
					t.counter = 13;
					t.start = 88;
					t.finish = 19;
					t.wipe = false;
					p.flag[2][1] = true;
					t = Task.addTask(Task.FLAG, "");
					t.start = 2;
					t.finish = 3;
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Hello there Ryder! Who's this that you brought with?");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "... Well, nice to meet you young man! What do I owe the pleasure of this visit to?");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "...");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Yes, I do research Xhenovian forms! If you can bring me a regional form, I can trade you for its counterpart!");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "First though, I'll have to upgrade your Pokedex to add a 'Variant' Pokedex for you to keep track of these forms.");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "... And there you go! All upgraded. Come talk to me when you have a Xhenovian Pokemon to trade!");
					Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Ryder, it was great to see you as always. Take care boys!");
					t = Task.addTask(Task.TURN, this, "", Task.RIGHT);
					t = Task.addTask(Task.TURN, gp.npc[13][7], "", Task.LEFT);
					t.start = 13;
					t.finish = 7;
					Task.addTask(Task.DIALOGUE, npc, "Thanks for your patience, I just figured that this little connection would help out you both mutually.");
					Task.addTask(Task.DIALOGUE, npc, "I'm gonna head out, I'll see you soon!");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
				} else {
					if (!p.flag[2][1]) {
						Task.addTask(Task.DIALOGUE, npc, "I think you might have went the wrong way...");
						Task.addTask(Task.DIALOGUE, npc, "Are you looking for Mt. Splinkty? You have to go back to Route 26 and head North!");
					} else {
						Task.addTask(Task.DIALOGUE, npc, "Got any Xhenovian forms to trade me?");
						Task.addTask(Task.REGIONAL_TRADE, "");
					}
				}
			}
			
		} else if (gp.currentMap == 162) {
			if (!p.flag[1][2]) {
				Task.addTask(Task.DIALOGUE, npc, "You're just in time, I almost have the energy prepared.");
				Task.addTask(Task.DIALOGUE, npc, "In the meantime, can we chat for a bit about what I do here?");
				Task.addTask(Task.DIALOGUE, npc, "I have this research post out here in the sticks because of the close proximity to Electric Tunnel, the birthplace of another type of Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "They're the Electric forms, and I've seen a lot of them migrating to Sicab City to feast on the power.");
				Task.addTask(Task.DIALOGUE, npc, "I suspect the power issues have been caused by them finishing off all of the energy there, causing the outage.");
				Task.addTask(Task.DIALOGUE, npc, "When we head back to try and fix it, I figure we'll probably run into some Electric forms there.");
				Task.addTask(Task.DIALOGUE, npc, "Thankfully, your dad told me to work on an extension to record all of the new forms, and I'd like to give it to you to help me document them.");
				Task.addTask(Task.DIALOGUE, npc, "So, without further ado, here's an update to your Neodex!");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "There, I added an 'Electric Pokedex' section. That way you can be prepared to take note of any new forms you see there!");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Okay! I believe I have enough auxillary power here to open the door of the Control Center for a bit.");
				Task.addTask(Task.DIALOGUE, npc, "However, I don't think you have enough time to get back there on foot. But, who says you need to walk?");
				Task.addTask(Task.DIALOGUE, npc, "Here's a high-tech invention I've made so you can warp back! Let me upgrade your map real quick to integerate the tech.");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Now you can teleport instantly to any town you've already been! Use it to get back to the city in time, and the doors to the center should be open.");
				Task.addTask(Task.DIALOGUE, npc, "At least if my calculations are correct.");
				Task.addTask(Task.DIALOGUE, npc, "God speed kid, and tell your dad I said hi!");
				p.flag[1][2] = true;
			} else if (!p.flag[1][19]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh yes, one more thing! As thanks for helping me out here, I have a gift for you.");
				Task.addTask(Task.DIALOGUE, npc, "I have here a Pokemon that has a confirmed Electric form, but there appears to be no way to switch between forms.");
				Task.addTask(Task.DIALOGUE, npc, "It's not much use to me anymore, so I figured it should have the pleasure of going out and exploring with you!");
				p.flag[1][19] = true;
				Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = 0;
				int counter = 0;
				do {
					counter++;
					id = gift.nextInt(6); // Rocky, Magikarp, Droid, Poof, Elgyem, Flamehox
					switch (id) {
					case 0:
						id = 48;
						break;
					case 1:
						id = 137;
						break;
					case 2:
						id = 181;
						break;
					case 3:
						id = 156;
						break;
					case 4:
						id = 265;
						break;
					case 5:
						id = 98;
						break;
					}
				} while (p.pokedex[id] == 2 && counter < 100);
				Pokemon result = new Pokemon(id, 20, true, false);
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			} else if (p.flag[1][2] && !p.flag[1][16]) {
				Task.addTask(Task.DIALOGUE, npc, "The energy levels are getting low at the Control Center?");
				Task.addTask(Task.DIALOGUE, npc, "Hold on, I can get them up for a little longer.");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Alright, keep at it champ.");
			} else if (!p.flag[1][22]) {
				Task.addTask(Task.DIALOGUE, npc, "Have you seen any new Electric forms? Can I take a look?");
				Pokemon[] eDex = p.getDexType(2);
				int amt = 0;
				for (Pokemon po : eDex) {
					if (p.pokedex[po.id] == 2) amt++;
				}
				if (!p.flag[1][21] && amt >= 3) {
					Task.addTask(Task.DIALOGUE, npc, "Wow! You've already seen " + amt + " forms!");
					Task.addTask(Task.DIALOGUE, npc, "Here, I have a special Electric-type move as a gift for helping me out!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.TM32;
					p.flag[1][21] = true;
				}
				if (amt >= Pokemon.POKEDEX_METEOR_SIZE) {
					Task.addTask(Task.DIALOGUE, npc, "...Oh my god! You did it! You completed the Electric form Pokedex!");
					Task.addTask(Task.DIALOGUE, npc, "I have here an extremely rare item, use it wisely!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.MASTER_BALL;
					p.flag[1][22] = true;
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "How's life, Finn? You see your father recently?");
			}
			
		} else if (gp.currentMap == 32 && !p.flag[1][17]) {
			p.flag[1][17] = true;
			p.fish = true;
			Task.addTask(Task.DIALOGUE, npc, "Say, you look like you'd be great at fishing. Here, take this spare I got lying around. Maybe you'll fish up a Durfish!");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.FISHING_ROD;
			Task.addTask(Task.DIALOGUE, npc, "Look at water and press 'A', or use the item in your bag to fish!");
		} else if (gp.currentMap == 16) {
			if (!p.flag[1][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Name's Stanford, the leader of this here town, though I haven't done much work here. At least not recently.");
				Task.addTask(Task.DIALOGUE, npc, "Honestly, this town is pretty neglected and has gone to shit recently. You're like the first person I've met that's given a fuck.");
				Task.addTask(Task.DIALOGUE, npc, "This stupid electric ghost is running through my team, and I'm running out of options.");
				Task.addTask(Task.DIALOGUE, npc, "I'm a Normal-type gym leader for fuck's sake! It doesn't get affected by most of my attacks!");
			} else if (p.flag[1][7] && !p.flag[1][8]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, the ghost is gone. Good job kiddo.");
				Task.addTask(Task.DIALOGUE, npc, "The fuse box might be usable now, could you try turning it on?");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Thanks for the hand squirt, though don't doubt Normal types because of this, they can still be very strong.");
				Task.addTask(Task.DIALOGUE, npc, "Just not against supernatural shit, fuck whatever that was. Those ones seemed extra powerful too.");
				Task.addTask(Task.DIALOGUE, npc, "Did you notice that they all had their hidden abilities? Those are special abilities only accessible through a special item.");
				Task.addTask(Task.DIALOGUE, npc, "Tell you what, as thanks for helping, I'd like to give you this. Try it out to get a special ability of your own!");
				p.flag[1][9] = true;
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.ABILITY_PATCH;
				Task.addTask(Task.DIALOGUE, npc, "That fuse box should've opened a gate in the Control Center, you should check out the situation there.");
				if (!p.flag[1][12]) {
					Task.addTask(Task.DIALOGUE, npc, "The energy field still seems to not be fully fixed yet though, and I noticed some commotion at the office.");
				}
				Task.addTask(Task.DIALOGUE, npc, "But, you know what, I'm heading to the bar or something. See ya kid.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		} else if (gp.currentMap == 18) {
			if (worldX / gp.tileSize >= 49) {
				if (!p.flag[1][11]) {
					Task.addTask(Task.DIALOGUE, npc, "Not... strong... enough...");
				} else if (p.flag[1][11] && !p.flag[1][12]) {
					Task.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
					Task.addTask(Task.DIALOGUE, npc, "Anyways, I think now that you deal with... whatever Electric Pokemon that was... that the fuse box is working. Try turning it on real quick, my head is buzzing...");
				} else if (p.flag[1][12] && !p.flag[1][13]) {
					p.flag[1][13] = true;
					Task.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
					Task.addTask(Task.DIALOGUE, npc, "Oh, by the way, I found something here that might be useful. Consider it as a token of appreciation...");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.HM02;
					Task.addTask(Task.DIALOGUE, npc, "It's a magic trick that makes rocks disappear! Anyways, I should get back to the gym to see if the gym leader finally came back. Presto!");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
				}
			} else {
				p.flag[2][2] = true;
				Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = 0;
				int counter = 0;
				do {
					counter++;
					id = gift.nextInt(8); // Dualmoose, Sparkdust, Posho, Kissyfishy, Minishoo, Tinkie, Bronzor-X, Bluebunn
					switch (id) {
					case 0:
						id = 61;
						break;
					case 1:
						id = 106;
						break;
					case 2:
						id = 143;
						break;
					case 3:
						id = 150;
						break;
					case 4:
						id = 177;
						break;
					case 5:
						id = 184;
						break;
					case 6:
						id = 276;
						break;
					case 7:
						id = 47;
						break;
					}
				} while (p.pokedex[id] == 2 && counter < 100);
				Pokemon result = new Pokemon(id, 15, true, false);
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			}
		} else if (gp.currentMap == 28) {
			if (worldX / gp.tileSize <= 70) { // Millie 1
				Task.addTask(Task.DIALOGUE, npc, "I'm Millie, kind of a big deal here. I've starred in several movies and TV stuff!");
				Task.addTask(Task.DIALOGUE, npc, "You've probably heard of Magikarp Jump: The Motion Picture, and Mystery Doors of the Magical Land: The Animated Series.");
				Task.addTask(Task.DIALOGUE, npc, "Anyways, yeah this isn't a shoot or scene, the trainers here are possessed or something! They just attack anything that try to enter the town.");
				Task.addTask(Task.DIALOGUE, npc, "You're going to have to fend them off back to back, and from what I've seen most of the town is infected! I'm not one to be in horror movies...");
				Task.addTask(Task.DIALOGUE, npc, "There won't be any breaks in between once we go in, so you're gonna need some strong Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "I saw those evil guys by the cell tower. They messed with the PC's signal somehow, and now you can only use Pokemon in the Gauntlet Box!");
				Task.addTask(Task.DIALOGUE, npc, "You might wanna put some Pokemon in there, preferably 4. If you don't have a full Gauntlet Box as well as a full team, you'll have a huge disadvantage!");
				int selected = p.getAmountSelected();
				String message = "Are you ready to fight once you come with me? We need to defend our town and there's no going back!";
				if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
					message = "You don't have 4 Pokemon selected to bring in the Gauntlet Box! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
				}
				for (String s : message.split("\n")) {
					Task.addTask(Task.DIALOGUE, npc, s);
				}
				Task.addTask(Task.DIALOGUE, npc, "Trust me, I had to fight off some of them, they hit hard. Especially the stunt doubles...");
				Task.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?", 2);
				// Millie 2
				Task.addTask(Task.TURN, this, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, npc, "There's this weird kid that's been in a trance blocking the way to the tower. He hasn't moved an inch, almost like he's guarding the place.");
				Task.addTask(Task.DIALOGUE, npc, "I've heard him mutter a few things. \"Toxic\", \"Get decked\", it's off-putting.");
				Task t = Task.addTask(Task.TURN, gp.npc[28][1], "", Task.RIGHT);
				t.start = 28;
				t.finish = 1;
				Task.addTask(Task.DIALOGUE, npc, "Wait, that's your rival? Maybe he'll recognize you, try saying something that'll, I don't know, make him angry!");
				Task.addTask(Task.DIALOGUE, npc, "Y'know, like in method acting! Give it a shot.");
				Task.addTask(Task.TURN, this, "", Task.UP);
				t = Task.addTask(Task.TURN, gp.npc[28][1], "", Task.UP);
				t.start = 28;
				t.finish = 1;
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.TURN, this, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, npc, "AHHH! HE'S DEFINITELY AWAKE NOW! JEEZ, WHAT DID YOU EVEN SAY!?");
				Task.addTask(Task.TURN, this, "", Task.UP);
				t = Task.addTask(Task.FLAG, "");
				t.start = 2;
				t.finish = 5;
			} else {
				if (worldY / gp.tileSize < 37) {
					if (worldY / gp.tileSize > 33) { // Millie 3
						if (!p.flag[2][7]) {
							Task.addTask(Task.DIALOGUE, npc, "It's definitely too dangerous to go back to the town, at least for now. You need to scout the area to find the source of these strange radio waves...");
							Task.addTask(Task.DIALOGUE, npc, "Let me know if you figure out what's going on! Please... I'm scared...");
						} else {
							Task.addTask(Task.DIALOGUE, npc, "Thank Arceus you're back... I was so petrified in fear.");
							Task.addTask(Task.DIALOGUE, npc, "What did you find out? ... Oh, ... Oh my. What?!?!?");
							Task.addTask(Task.DIALOGUE, npc, "There's a poor Pokemon chained up to the tower?? That is so cruel! Ack! That makes me so sick.");
							Task.addTask(Task.DIALOGUE, npc, "It has to be a pretty powerful Pokemon to be able to transmit such strong radio waves to possess all of these people.");
							Task.addTask(Task.DIALOGUE, npc, "Did you happen to notice what Pokemon it was?");
							Task.addTask(Task.DIALOGUE, npc, "A... what? An alien spider? What??");
							Task.addTask(Task.DIALOGUE, npc, "I have no idea what you're talking about, I've never heard of anything like that in my life.");
							Task.addTask(Task.DIALOGUE, npc, "If it's a spider though, I'm sure we can be great friends. I love bugs!");
							Task.addTask(Task.DIALOGUE, npc, "Alien bugs might take a bit more to get used to. I wonder where it came from?");
							Task.addTask(Task.DIALOGUE, npc, "And I can't believe it's chained up! We have to set it free! I bet it was those space goons.");
							Task.addTask(Task.DIALOGUE, npc, "Come to think of it, I did notice an evil grunt run off towards Mt. Splinkty. Maybe we can stop him?");
							Task.addTask(Task.DIALOGUE, npc, "Go check out the Mountain and see if you can find the grunt, I'll stand guard here!");
							p.flag[2][8] = true;
							Task.addTask(Task.FLASH_IN, "");
							Task.addTask(Task.UPDATE, "");
							Task.addTask(Task.FLASH_OUT, "");
						}
					} else if (worldY / gp.tileSize > 25) { // Millie 4
						Task.addTask(Task.DIALOGUE, npc, "OHHH! YOU! I'm so sorry, that was a reflex, there's so many of them.");
						if (!p.flag[2][9]) {
							Task.addTask(Task.DIALOGUE, npc, "Any luck finding the grunt? ..I've been holding them off okay.");
							Task.addTask(Task.DIALOGUE, npc, "Like I said, before, I believe that there's a secret room that you can access on the 2nd floor, check that out if you haven't yet.");
						} else {
							Task.addTask(Task.DIALOGUE, npc, "You found him? How'd it go? Are you and your Pokemon okay?");
							Task.addTask(Task.DIALOGUE, npc, "You got wire cutters? Quick, let's try and cut that creature free and stop this madness!");
							Task.addTask(Task.DIALOGUE, npc, "I'll try and make sure the possessed people are okay. Good luck!");
							p.flag[2][10] = true;
							Task.addTask(Task.FLASH_IN, "");
							Task.addTask(Task.UPDATE, "");
							Task.addTask(Task.FLASH_OUT, "");
						}
					} else { // chained up xurkitree
						if (!p.flag[2][11]) {
							Task.addTask(Task.TEXT, "(The mysterious Pokemon seems to be alive, though it's unable to move because it's chained up.)");
							if (!p.flag[2][9]) {
								p.flag[2][7] = true;
							} else {
								p.bag.remove(Item.WIRE_CUTTERS);
								p.flag[2][11] = true;
								Task.addTask(Task.TEXT, "You used the wire cutters to set it free!");
								Task.addTask(Task.FLASH_IN, "");
								Task.addTask(Task.UPDATE, "");
								Task.addTask(Task.FLASH_OUT, "");
								Task.addTask(Task.DIALOGUE, npc, "Bzzz....Zzzzttt..... ZUZUZUURRKIII!!!");
								Task.addTask(Task.DIALOGUE, npc, "(The mysterious creature seems shell-shocked, and is now lashing out at everything around it!)");
								Task t = Task.addTask(Task.BATTLE, "", 387);
								t.start = 284;
							}	
						}
					}
				} else if (worldY / gp.tileSize < 42) { // Player wiped, talked to her from the other side
					Task.addTask(Task.DIALOGUE, npc, "Oh, there you are. What happened?");
					Task.addTask(Task.DIALOGUE, npc, "They must have beaten you up... You're definitely a tough kid, but I need you back in there. Maybe be extra careful this time.");
					Task t = Task.addTask(Task.TELEPORT, "");
					t.counter = 28;
					t.start = 82;
					t.finish = 36;
					t.wipe = false;
				} else { // millie 5
					p.flag[2][13] = true;
					Task.addTask(Task.DIALOGUE, npc, "Even though bug Pokemon aren't as flimsy and weak as they seem, I still have a feeling that you'll be able to defeat me.");
					Task.addTask(Task.DIALOGUE, npc, "I mean, you were literally able to fend off a huge alien bug and save me and my town!");
					Task.addTask(Task.DIALOGUE, npc, "Have you noticed the big openings in the Earth around this area? There's lots more around this region.");
					Task.addTask(Task.DIALOGUE, npc, "There's a pretty interesting myth that my mom would tell me stories of when I was younger.");
					Task.addTask(Task.DIALOGUE, npc, "She said that the terrain wasn't always so rough, that it's actually the result of an ancient war that happened.");
					Task.addTask(Task.DIALOGUE, npc, "According to the myth, there used to be a cult-like following of an all-powerful diety that apparently created the Pokemon and people living here!");
					Task.addTask(Task.DIALOGUE, npc, "But once people discovered the existance of alien Pokemon and the Galactic type, their religion was proven obsolete by the athiests.");
					Task.addTask(Task.DIALOGUE, npc, "Or at least the athiests tried to disprove it that way. As you can imagine, it didn't go over too well with the believers, erupting in the massive conflict.");
					Task.addTask(Task.DIALOGUE, npc, "Apparently the destruction of the terrain was actually caused by the leaders of each group fighting, who are rumored to both be powerful Pokemon.");
					Task.addTask(Task.DIALOGUE, npc, "But, as one of my favorite creators would say, that's \"just a theory\", after all.");
					Task.addTask(Task.DIALOGUE, npc, "I do like thinking about it though, it's pretty interesting, wouldn't you agree?");
					Task.addTask(Task.DIALOGUE, npc, "..What's that? Oh right! Silly me, I was going to give you a technique to help traverse the gaps!");
					Task.addTask(Task.DIALOGUE, npc, "I just got side-tracked thinking about all of the cool films that could be made about that myth. It's a pretty popular one around here!");
					Task.addTask(Task.DIALOGUE, npc, "Anyways, I'm only giving you this technique because even though you can't use it until you beat me, I know you have the skills to do it.");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.HM03;
					Task.addTask(Task.DIALOGUE, npc, "Come fight me inside when you're ready, I know it'll be an amazing battle. Maybe even one we'll turn into a film when you're a world-famous trainer someday!");
					Task.addTask(Task.DIALOGUE, npc, "Good luck, my friend. You're going to need it! EEK I'm so excited!!");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
				}
			}
		} else if (gp.currentMap == 49) {
			p.flag[2][14] = true;
			Task.addTask(Task.DIALOGUE, npc, "I encountered this very strong Pokemon, and I don't think I'm strong enough to train it. Here!");
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int id;
			int counter = 0;
			do {
				counter++;
				id = gift.nextInt(5); // Pebblepup, Fightorex, Tricerpup, Shockfang, Nightrex
				switch (id) {
				case 0:
					id = 55;
					break;
				case 1:
					id = 57;
					break;
				case 2:
					id = 66;
					break;
				case 3:
					id = 211;
					break;
				case 4:
					id = 213;
					break;
				}
			} while (p.pokedex[id] == 2 && counter < 50);
			Pokemon result = new Pokemon(id, 25, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 50) {
			p.flag[3][9] = true;
			Task.addTask(Task.DIALOGUE, npc, "Great choice young cracka!!!!");
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int id = gift.nextInt(3); // Otterpor, Florline, Flameruff
			switch (id) {
			case 0:
				id = 78;
				break;
			case 1:
				id = 80;
				break;
			case 2:
				id = 92;
				break;
			}
			if (p.pokedex[id] == 2) {
				Task.addTask(Task.DIALOGUE, npc, "Wait..... you have that one?!?!? Shit. Well, take this one instead bozo.");
				boolean sparkitten = gift.nextBoolean();
				if (sparkitten) {
					id = 108;
				} else {
					id = 190;
				}
			}
			Pokemon result = new Pokemon(id, 30, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 39) {
			p.flag[3][0] = true;
			Task.addTask(Task.DIALOGUE, npc, "Well, I say it's weather, more like the atmospheric nonsense going outside.");
			Task.addTask(Task.DIALOGUE, npc, "Jeez it's so bright, worse than the time I stared directly at the sun.");
			Task.addTask(Task.DIALOGUE, npc, "But hey Alakazam, you live and you learn. Thankfully I brought these awesome Pit Sevipers!");
			Task.addTask(Task.DIALOGUE, npc, "These suckers block out all kinds of rays, heat rays, UV rays, even Confuse Rays!");
			Task.addTask(Task.DIALOGUE, npc, "You look quite strained, I'll let you borrow these since I wasn't planning on sticking around long anyways.");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.VISOR;
			Task.addTask(Task.DIALOGUE, npc, "If you put those on you should be able to see a lot better outside, it's crazy out there.");
			Task.addTask(Task.DIALOGUE, npc, "To be honest with you dude, I'm not even sure why it's so darn bright... and the local school is in a full blown panic about it.");
			Task.addTask(Task.DIALOGUE, npc, "The classrooms are all locked and there's no sign of the teachers... come to think of it, that should probably be looked at.");
			Task.addTask(Task.DIALOGUE, npc, "Just don't think I'll be doing too much of the \"looking\", because you have my one and only pair of shades.");
			Task.addTask(Task.DIALOGUE, npc, "Oh, one more thing before I leave you to save the town alone! There's a lot of Ice-type Pokemon in this area...");
			Task.addTask(Task.DIALOGUE, npc, "So I'm entrusting you with one of my favorite Pokemon!");
			Task.addTask(Task.DIALOGUE, npc, "I brought a couple Flamigo as travel buddies from my home region, and I'm entrusting one to help you out with the Ice-types!");
			Task.addTask(Task.TEXT, "You received Flamigo!");
			Pokemon p = new Pokemon(246, 35, true, false);
			t = Task.addTask(Task.GIFT, "", p);
			Task.addTask(Task.DIALOGUE, npc, "...Just probably don't let it get hit by an Ice move. Anyways, toodles, and good luck kid!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		} else if (gp.currentMap == 38) { // ice master/robin
			if (worldY / gp.tileSize < 30) { // ice master
				Task.addTask(Task.DIALOGUE, npc, "It may help you in your journey, maybe even in ways you don't expect. Consider it a token of my thanks for your help with the school.");
				p.flag[3][10] = true;
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.PETTICOAT_GEM;
			} else { // robin cutscene
				Task.addTask(Task.DIALOGUE, npc, "*pant* I even had to jam your portal system just so I could track you down.");
				Task.addTask(Task.DIALOGUE, npc, "I've got an urgent letter from your mom. Don't know what's in it, but she said it was really important.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.LETTER;
				Task.addTask(Task.DIALOGUE, npc, "She was pretty insistent I get this to you right away. You might want to open it as soon as you can.");
				Task.addTask(Task.DIALOGUE, npc, "Anyway, I'm just glad I finally found you. Take care of yourself, alright?");
				p.flag[4][0] = true;
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		} else if (gp.currentMap == 43) { // ground master
			Task.addTask(Task.DIALOGUE, npc, "It's rare, even among explorers. Use it wisely - it could be the edge you need.");
			p.flag[3][11] = true;
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.VALIANT_GEM;
			Task.addTask(Task.DIALOGUE, npc, "That gem was unearthed during a deep excavation, and I was saving it for someone special. Looks like that's you.");
		} else if (gp.currentMap == 165) { // principal
			Task.addTask(Task.DIALOGUE, npc, "I don't have any Pokemon, so I wasn't sure what to do! But now that I see you're here to help... maybe there's hope.");
			if (!p.flag[3][1] && !p.flag[3][2]) { // haven't found either ground or ice master
				Task.addTask(Task.DIALOGUE, npc, "Listen, this whole situation is a mess! Those Grunts have taken over the classrooms, and I can't do anything to stop them.");
				Task.addTask(Task.DIALOGUE, npc, "The teachers - Ice Master and Ground Master - might be able to help, but I haven't seen them in a while.");
				Task.addTask(Task.DIALOGUE, npc, "I think Ice Master is somewhere in the fields north of town, and Ground Master... I believe he's hiding out in the city.");
				Task.addTask(Task.DIALOGUE, npc, "Find them, please!");
			} else if ((p.flag[3][1] || p.flag[3][2]) && !p.flag[3][3] && !p.flag[3][4]) { // got at least one of the keys but hasn't unlocked the classrooms yet
				String teacher = "";
				if (p.flag[3][1] && !p.flag[3][2]) teacher = "Ice Master's";
				if (p.flag[3][2] && !p.flag[3][1]) teacher = "Ground Master's";
				if (p.flag[3][1] && p.flag[3][2]) teacher = "both of their";
				Task.addTask(Task.DIALOGUE, npc, "You've got " + teacher + " keys? That's amazing!");
				Task.addTask(Task.DIALOGUE, npc, "Now you can get into the classrooms and see what those Team Eclipse Grunts are up to.");
				Task.addTask(Task.DIALOGUE, npc, "I wish I could do more, but... I don't have any Pokemon, and I - I just can't face them.");
				Task.addTask(Task.DIALOGUE, npc, "Please, go and clear them out! I'll do whatever I can to help once they're gone.");
			} else if ((p.flag[3][3] || p.flag[3][4]) && (!p.flag[3][5] || !p.flag[3][6])) {
				Task.addTask(Task.DIALOGUE, npc, "You've unlocked a classroom, right? Go check inside! There are Grunts taking over the school, and it's a disaster.");
				Task.addTask(Task.DIALOGUE, npc, "I know I should be the one handling this, but without Pokemon, I'm helpless! Please, clear out those rooms!");
			} else if (p.flag[3][5] && p.flag[3][6]) {
				Task.addTask(Task.DIALOGUE, npc, "You did it! You cleared both classrooms! I - I don't know how to thank you enough.");
				Task.addTask(Task.DIALOGUE, npc, "I may not be able to help in battle, but I can give you something that will aid you on your journey.");
				Task.addTask(Task.DIALOGUE, npc, "Here - take this, it's the HM for Surf. I know you'll make great use of it.");
				p.flag[3][7] = true;
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM04;
				if (!p.flag[3][8]) {
					Task.addTask(Task.DIALOGUE, npc, "Now, go find the cause of the extreme light outside with your new tools and put a stop to it!");
				}
			}
		} else if (gp.currentMap == 44) {
			Task.addTask(Task.DIALOGUE, npc, "Leader Glacius said no battles until we can get things under control in the city.");
			if (!p.flag[3][5] || !p.flag[3][6]) {
				Task.addTask(Task.DIALOGUE, npc, "You're wondering why the gym's closed, right? Well, it's not just the gym - there's trouble over at the school too.");
				Task.addTask(Task.DIALOGUE, npc, "The gym's been shut down ever since the school got overrun by those Team Eclipse guys.");
				Task.addTask(Task.DIALOGUE, npc, "If you're looking to challenge Glacius, you'll need to sort out what's happening at the school first.");
			} else if (p.flag[3][5] && p.flag[3][6] && !p.flag[3][7]) {
				Task.addTask(Task.DIALOGUE, npc, "I heard you cleared the whole school out of those goons! The principal mentioned he wants to thank you for everything you've done.");
				Task.addTask(Task.DIALOGUE, npc, "Might be a good idea to visit him before you come back here. He should be in his office in the school.");
			} else if (p.flag[3][7] && !p.flag[3][8]) {
				Task.addTask(Task.DIALOGUE, npc, "You did great clearing the school of those Grunts, but that bright light's still making things difficult.");
				Task.addTask(Task.DIALOGUE, npc, "Leader Glacius won't accept challengers until the light's sorted out. Maybe you can do something about it?");
			}
		} else if (gp.currentMap == 91) { // grandpa
			if (!p.flag[4][2]) {
				p.flag[4][2] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh... thank Arceus you're here. I was afraid I wouldn't make it through this.");
				Task.addTask(Task.DIALOGUE, npc, "Those thugs - Team Eclipse - stormed in, demanding something that I don't fully understand.");
				Task.addTask(Task.DIALOGUE, npc, "You beat them, right? Why are they still here? Can you do anything?");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, this, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.SPEAK, this, "Get out, you creeps! Leave us alone. I already destroyed your flimsy Pokemon, you want some more?");
				Task.addTask(Task.SPEAK, this, "Didn't think so. Then, GET!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.TURN, this, "", Task.UP);
				Task.addTask(Task.DIALOGUE, npc, "Thank you Finn, I don't know what I would've done without you.");
				Task.addTask(Task.DIALOGUE, npc, "They said something about summoning an Ultra Paradox Pokemon... I think it's some dangerous creature they're after.");
				Task.addTask(Task.DIALOGUE, npc, "I heard they were headed for Peaceful Park next, something about unleashing that alien there, probably.");
				Task.addTask(Task.DIALOGUE, npc, "You might want to stop them before they cause more chaos.");
				Task.addTask(Task.DIALOGUE, npc, "Oh! But before you go, I have a gift for you.");
				Task.addTask(Task.DIALOGUE, npc, "Take this, it's the HM for Slow Fall. It should help you out exploring Xhenos once you take down Mindy.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM05;
				Task.addTask(Task.DIALOGUE, npc, "Be careful out there. These aren't just ordinary trainers - they're after something far more dangerous than I've ever seen.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Still here, eh? You'd better hurry to Peaceful Park before those Team Eclipse thugs cause even more trouble.");
				Task.addTask(Task.DIALOGUE, npc, "They were talking about summoning an Ultra Paradox Pokemon, some terrible creature. The park's just south of Kleine Village - head there quickly!");
				Task.addTask(Task.DIALOGUE, npc, "Be careful, that thing's dangerous. I know you can handle it, but don't take it lightly.");
			}
		} else if (gp.currentMap == 93) { // Move reminder
			Task.addTask(Task.PARTY, "");
		} else if (gp.currentMap == 94) {
			p.flag[4][4] = true;
			Task.addTask(Task.TEXT, "One makes Pokemon surge with electricity, and another casts them in a strange shadow.");
			Task.addTask(Task.TEXT, "Here's a gift of one of the Pokemon affected!");
			int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226};
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.pokedex[ids[index]] == 2 && counter < 100);
			
			Pokemon result = new Pokemon(ids[index], 30, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 107) { // ghostly woods
			if (npc.name.equals("Arthra")) {
				if (!p.flag[5][0]) {
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addTask(Task.SPOT, npc, "");
					Task.addTask(Task.DIALOGUE, npc, "Oh, didn't expect company out here. You must be here for the same reason, yeah? You've noticed these ghosts swarming the woods?");
					Task.addTask(Task.DIALOGUE, npc, "Name's Arthra, by the way. Merlin's my grandfather; you'll meet him someday if you get that far. Right now, I'm trying to figure out what's causing this mess.");
					Task.addTask(Task.DIALOGUE, npc, "These ghosts are sneaky, but I've got them down. They're phasing in and out like something's messing with their energy.");
					Task.addTask(Task.DIALOGUE, npc, "And I'm willing to bet it's no accident.");
					Task.addTask(Task.SLEEP, "", 15);
					Task.addNPCMoveTask('y', 91 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.SLEEP, "", 10);
					Task.addTask(Task.DIALOGUE, npc, "But if you're serious about this, let's see if you can keep up. I don't have time to drag around dead weight.");
					Task.addTask(Task.BATTLE, "", 392);
				} else {
					p.flag[5][1] = true;
					Task.addTask(Task.DIALOGUE, npc, "But don't get too comfortable with that win. I'll make sure you regret it next time we battle.");
					Task.addTask(Task.DIALOGUE, npc, "You want to play hero here? Be my guest! If you're so great, then take care of those ghosts yourself. I've got better things to do.");
					Task.addTask(Task.DIALOGUE, npc, "Oh, and here you go, hero! You can probably make far better use of this than I ever could!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.ABILITY_PATCH;
					Task.addTask(Task.DIALOGUE, npc, "I really hate your hero attitude. You don't know shit. But like I said, have at it here.");
					Task.addTask(Task.DIALOGUE, npc, "Just don't mess it up. And keep an eye out - these ghosts aren't the only threat around here.");
					Task.addTask(Task.DIALOGUE, npc, "There's something bigger going on, and I'll figure it out - no thanks to you. See you around, hero.");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
				}
			} else if (npc.name.equals("Rick")) {
				Task.addNPCMoveTask('y', 55 * gp.tileSize, npc, false, 96);
				Task.addTask(Task.TURN, this, "", Task.UP);
				Task.addNPCMoveTask('y', 56 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addNPCMoveTask('x', 46 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.TURN, this, "", Task.LEFT);
				Task.addNPCMoveTask('x', 46 * gp.tileSize, this, false, 1);
				Task.addTask(Task.TURN, this, "", Task.UP);
				Task.addNPCMoveTask('y', 58 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "Well, well. Look who decided to show up - the meddling pest who can't keep their nose out of Eclipse's business.");
				Task.addTask(Task.TURN, this, "", Task.UP);
				Task.addNPCMoveTask('y', 64 * gp.tileSize, this, false, 1);
				Task.addNPCMoveTask('y', 61 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "Do you have any idea what you've done? Those ghosts were just the beginning!");
				Task.addTask(Task.DIALOGUE, npc, "You may have beaten a few, but I can summon hundreds more if I want. Once I'm done with you, Ghostly Woods will be overrun!");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', 63 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Once we're done here, not even the bravest of trainers will stand in Team Eclipse's way.");
				Task.addTask(Task.BATTLE, "", 234);
			}
		} else if (gp.currentMap == 103) { // maxwell 1
			if (!p.flag[5][4]) {
				Task.addTask(Task.DIALOGUE, npc, "Thra'knash koru'dan Gzhaz... Zhar'mir vakta da'tor!");
				Task.addTask(Task.DIALOGUE, npc, "Vahl'orim Dragowrath! Zhar'kor-Gzazha... vass'dar athra!");
				Task.addTask(Task.DIALOGUE, npc, "Zharkh'nir da'kash! Gzazha, ir'thar vak'tai khar... rise'thil an'dor!");
				Task.addTask(Task.DIALOGUE, npc, "Vahl'krim da'sharak... rise! KHAR DA'ZHAR GZAZHA!");
				Task.addTask(Task.SHAKE, "BOOOOOOOOOOOOOOOOOOOOOM!", 300);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.DIALOGUE, npc, "BWAHAHAHAH! The pest finally arrives! Did you really think you could follow us all the way down here and stop what's already in motion? How amusingly naive.");
				Task.addTask(Task.DIALOGUE, npc, "You see, child, I am far more than just a 'trainer'. I am the vanguard of an empire - we are here to prepare this world for something beyond your comprehension.");
				Task.addTask(Task.DIALOGUE, npc, "My master, Dragowrath, watches from the stars. Earth is merely the first of many worlds he'll claim, and when the Sorcerer rises, this land will be ours.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', gp.tileSize * 36, npc, false, 4);
				Task.addTask(Task.DIALOGUE, npc, "But enough talk. You've come this far, so let's end this with a little... demonstration of power. Prepare yourself - my Pokemon and I will not hold back.");
				Task.addTask(Task.BATTLE, "", 217);
			} else if (!p.flag[5][9]) {
				p.flag[5][9] = true;
				Task.addTask(Task.DIALOGUE, npc, "Impressive... but don't think this victory means anything. The master plan is already in place, and there's nothing you can do to stop us.");
				Task.addTask(Task.DIALOGUE, npc, "Enjoy your hollow victory while it lasts. Soon, this world will be torn apart, and you'll see just how powerless you truly are.");
				Task.addTask(Task.DIALOGUE, npc, "Farewell... and enjoy what little time your precious world has left.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SHAKE, "", 100);
				
				Task.addCameraMoveTask('y', -400, 2);
				
				Task.addTask(Task.SHAKE, "", 50);
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addNPCMoveTask('y', 45 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task.addTask(Task.TURN, gp.npc[103][1], "", Task.RIGHT);
				
				Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task.addTask(Task.TURN, gp.npc[103][1], "", Task.UP);
				
				Task.addNPCMoveTask('y', 41 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task t = Task.addTask(Task.TURN, gp.npc[103][1], "", Task.DOWN);
				t.wipe = true;
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addDiagCameraMoveTask(0, 0, 45);
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addTask(Task.TEXT, "A powerful presence awaits...");
			}
		} else if (gp.currentMap == 109) {
			if (worldX / gp.tileSize > 24) { // breeder
				p.flag[5][7] = true;
				Task.addTask(Task.TEXT, "Here, could you raise it for me?");
				int[] ids = new int[] {177, 179, 98};
				Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int index = gift.nextInt(ids.length - 1);
				if (p.pokedex[ids[0]] == 2 || p.pokedex[ids[1]] == 2) {
					index = 2;
				}
				
				Egg result = new Egg(ids[index], 3);
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			} else { // scott cutscene
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addNPCMoveTask('y', 40 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, this, "", Task.LEFT);
				Task.addNPCMoveTask('y', 42 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 11 * gp.tileSize, npc, false, 3);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addNPCMoveTask('y', 2052, gp.npc[109][5], false, 3);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 14 * gp.tileSize, npc, false, 4);
				
				Task.addTask(Task.DIALOGUE, npc, "You beat Rayna?! Me too! But that's not important right now. You need to listen to me, something HUGE is going on!");
				Task.addTask(Task.DIALOGUE, npc, "It's... it's Mt. St. Joseph! Something's about to happen - an eruption, but not just any eruption!");
				Task.addTask(Task.DIALOGUE, npc, "There's some kind of supernatural force messing with the volcano. It's all connected to Team Eclipse.");
				Task.addTask(Task.DIALOGUE, npc, "I don't know the exact details, but...");
				Task.addTask(Task.DIALOGUE, npc, "Well, I don't know how to tell you this, because I know you're friends with him too, but...");
				Task.addTask(Task.SPEAK, this, "What? What happened? Who are you talking about?");
				Task.addTask(Task.DIALOGUE, npc, "I just ran into Fred, and he told me Eclipse is trying to cause a catastrophe there - and he's part of it!");
				Task.addTask(Task.DIALOGUE, npc, "Can you believe that Farfetch'd nonsense? Fred... joined them!");
				Task.addTask(Task.SPEAK, this, "...");
				Task.addTask(Task.SPEAK, this, "Yeah... Scott, I already knew. I fought him in Shadow Path and bested him there.");
				Task.addTask(Task.DIALOGUE, npc, "WHAT?! Ugh, of course you did... you probably know EVERYTHING by now.");
				Task.addTask(Task.DIALOGUE, npc, "Anyway, I guess that's not the biggest news. The real problem is Mt. St. Joseph - that's where they're going to make their move.");
				Task.addTask(Task.DIALOGUE, npc, "If we don't stop them, we could be looking at a real disaster.");
				Task.addTask(Task.SPEAK, this, "Fred told you their plans? Why?");
				Task.addTask(Task.DIALOGUE, npc, "Well, I don't think he's happy with what they're doing, but he's too far gone to stop them. He said they're planning something big at the volcano.");
				Task.addTask(Task.DIALOGUE, npc, "And I - I think he knew how they're going to do it. I think they're summoning an Ultra Paradox Pokemon to cause an eruption.");
				Task.addTask(Task.DIALOGUE, npc, "It's going to be a strong enough natural disaster to destroy everything in Xhenos. I... I'm not sure how, but I...");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.SPEAK, this, "What, Scott? Spit it out!");
				Task.addTask(Task.DIALOGUE, npc, "I think I have a feeling why they want to wipe out the whole region.");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "I think they have an alien leader... and I think it wants to colonize this planet.");
				Task.addTask(Task.SPEAK, this, "WHAT?! They want to... take over all of us??");
				Task.addTask(Task.SPEAK, this, "Oh God, Scott! How do we stop them?!");
				Task.addTask(Task.DIALOGUE, npc, "I don't know, Finn! It's going to be very dangerous. You'll need to go South through Gelb Forest to Rawwar City.");
				Task.addTask(Task.DIALOGUE, npc, "But there's a big problem, you will need to cross an intense vortex of water with Whirlpool.");
				if (p.bag.contains(Item.HM06)) {
					Task.addTask(Task.SPEAK, this, "I got the Whirlpool HM, I should be fine. Thanks Scott.");
				} else {
					Task.addTask(Task.SPEAK, this, "How can I cross it?");
					Task.addTask(Task.DIALOGUE, npc, "You can cross it with the Hidden Machine Whirlpool.");
					Task.addTask(Task.DIALOGUE, npc, "Fred... Fred actually told me where it is - right next to Team Eclipse's underground base...");
					Task.addTask(Task.DIALOGUE, npc, "At the bottom of Shadow Ravine, by where that Ultra Paradox Pokemon you fought was.");
					Task.addTask(Task.SPEAK, this, "I must've missed it. Thanks for letting me know.");
				}
				Task.addTask(Task.DIALOGUE, npc, "Just... just be careful, alright? I didn't get all the details from Fred, but by the way he was talking...");
				Task.addTask(Task.DIALOGUE, npc, "Things are much worse than we realize. Please, Finn... I can't lose another friend to Eclipse.");
				Task.addTask(Task.SPEAK, this, "Don't worry, Scott. I'll stop them. You just stay safe.");
				Task.addTask(Task.DIALOGUE, npc, "Thanks, Finn... I know you can do it. Good luck.");
				Task.addTask(Task.DIALOGUE, npc, "Now, go and stop them before it's too late.");
				p.flag[6][0] = true;
			}
		} else if (gp.currentMap == 118) {
			Task.addTask(Task.DIALOGUE, npc, "Do you have any fossils for me to resurrect?");
			Task.addTask(Task.FOSSIL, "Do you have any fossils for me to resurrect?");
		} else if (gp.currentMap == 127) {
			if (worldX / gp.tileSize > 31) {
				if (p.coins > 0) {
					Task t = Task.addTask(Task.CONFIRM, "Would you like to Battle Bet?\n(Warning: Will Auto-Save)");
					t.counter = 4;
				} else {
					Task.addTask(Task.TEXT, "I'm sorry, you don't have any coins to bet with! Come back later!");
				}
			} else {
				Task t = Task.addTask(Task.CONFIRM, "Would you like to play Blackjack?\n(Warning: Will Auto-Save)");
				t.counter = 3;
			}
		} else if (gp.currentMap == 129 && worldY / gp.tileSize > 41) {
			if (!p.flag[6][1]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you haven't gotten any coins yet?");
				Task.addTask(Task.DIALOGUE, npc, "Here, it's on the house!");
				Task.addTask(Task.DIALOGUE, npc, "You received 10 Coins!");
				Task.addTask(Task.DIALOGUE, npc, "Yes, now don't go spend them all in one place. Or do! I don't care!");
				Task.addTask(Task.DIALOGUE, npc, "But come back when you get more badges. Perhaps I'll have a reward for you!");
				p.coins += 10;
				p.flag[6][1] = true;
			} else {
				boolean newBadges = false;
				for (int i = 0; i < p.badges; i++) {
					if (!p.coinBadges[i]) {
						newBadges = true;
						break;
					}
				}
				if (newBadges) {
					Task.addTask(Task.DIALOGUE, npc, "Hey hey hey, what do we have here?");
					Task.addTask(Task.DIALOGUE, npc, "Got some new badges, eh? Let me take a look!");
					String[] messages = new String[] {
						"Bested Robin in Poppy Grove! Lemme see your case real quick.",
						"Beat Stanford, huh? I got a shiny reward for you, kid!",
						"Ah, a shiny new badge! Made quick work of Millie, I see! Guess that calls for a bonus, bub.",
						"4 badges, huh?  You're halfway through the gyms, and that’s no easy feat. Here's a little something for your troubles.",
						"Bested Millie's own mom too? You took down that family no trouble! Pass me your case!",
						"What's that? You beat both Maxwell and Rayna? Wow, I really underestimated you! Here!",
						"Defeated our resident gambler Merlin? Heck yeah, I never trusted him anyways, heard he always cheated at Blackjack.",
						"Wow, all 8 badges! Can't believe you struck down the gyms! I got just the reward for a champion in the making like youse!"
					};
					for (int i = 0; i < p.badges; i++) {
						if (!p.coinBadges[i]) {
							Task.addTask(Task.DIALOGUE, npc, messages[i]);
							int coins = i > 4 ? 25 : i > 2 ? 20 : i > 0 ? 10 : 5;
							Task.addTask(Task.TEXT, "You received " + coins + " coins!");
							p.coinBadges[i] = true;
							p.coins += coins;
						}
					}
				} else {
					if (p.coins > 0) {
						Task t = Task.addTask(Task.GAME_STATE, "");
						t.counter = GamePanel.COIN_STATE;
					} else {
						Task.addTask(Task.DIALOGUE, npc, "Looks like your stash just ran dry, so I can't exchange air for anything. Come back when you're... a little richer.");
					}
					
				}
			}
		} else if (gp.currentMap == 130) {
			if (p.flag[5][8] && !p.flag[6][3]) {
				p.flag[6][3] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh hey squirt! You finally got strong enough to make it here on your own!");
				Task.addTask(Task.DIALOGUE, npc, "Here, take this rare Pokemon!");
				Pokemon result = new Pokemon(97, 50, true, false);
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			}
			Task.addTask(Task.DIALOGUE, npc, "Here, grab a seat! You and your Pokemon look like you want some SPICE!");
			for (Pokemon p : p.team) {
				if (p != null && p.type1 != PType.FIRE && p.type2 != PType.FIRE) {
					p.status = Status.BURNED;
				}
			}
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.DIALOGUE, npc, "Haha! You guys look like you really enjoyed that!");
			if (p.flag[5][8]) {
				Task.addTask(Task.DIALOGUE, npc, "Come back anytime, squirt!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Looks like that might've been too spicy for you. C'mon, let's get you back home.");
				Task t = Task.addTask(Task.TELEPORT, "");
				t.counter = 4;
				t.start = 68;
				t.finish = 63;
				
			}
		} else if (gp.currentMap == 168) { // shroom guy
			Task.addTask(Task.MUSHROOM, npc, "Gimmie, gimmie, GIMMIE!");
		} else if (gp.currentMap == 178) {
			Task.addTask(Task.DIALOGUE, npc, "I specialize in checking those strange evolution methods for your Pokemon.");
			if (!p.flag[1][20]) {
				p.flag[1][20] = true;
				Task.addTask(Task.DIALOGUE, npc, "Wait a minute... we haven't met yet, have we?");
				Task.addTask(Task.DIALOGUE, npc, "You're the Professor's kid, right? Here, he asked me to give you this if you ever passed by.");
				Random random = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = getUnregisteredBasePokemon(random);
				Egg egg = new Egg(id, 3);
				Task.addTask(Task.GIFT, "", egg);
				Task.addTask(Task.DIALOGUE, npc, "Let me know if you want to check any strange evolution progress!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Have any to show me?");
				Task.addTask(Task.EVO_INFO, npc, "");
			}
		} else if (gp.currentMap == 124) {
			p.flag[6][5] = true;
			Task.addTask(Task.DIALOGUE, npc, "The fever's fading, the mountain's quiet... Seems you handled things nicely. A true magician knows when to step in and when to let the show play out on its own.");
			Task.addTask(Task.DIALOGUE, npc, "Ah, where are my manners? The name's Merlin, Gym Leader of Rawwar City. If you've met my granddaughter, I imagine she had some... strong opinions about you.");
			Task.addTask(Task.DIALOGUE, npc, "She sees the world in black and white. Heroes and villains, tricks and truths. But magic, real magic, lies in the in-between.");
			Task.addTask(Task.DIALOGUE, npc, "You, though... You're full of surprises. And I do love a good surprise.");
			Task.addTask(Task.DIALOGUE, npc, "Here, consider this a reward for your performance.");
			Task t = Task.addTask(Task.ITEM, "Obtained HM07 Rock Climb!");
			t.item = Item.HM07;
			Task.addTask(Task.DIALOGUE, npc, "You'll need this if you want to keep climbing higher once you defeat me. Just don't let the heights get to your head.");
			Task.addTask(Task.DIALOGUE, npc, "Now then, you've come all this way. Might as well see if your magic can match mine.");
			Task.addTask(Task.DIALOGUE, npc, "Step inside when you're ready. But be warned - my tricks aren't just for show.");
			
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		} if (gp.currentMap == 146 && !p.flags[28]) {
			int selected = p.getAmountSelected();
			String message = "Are you ready to fight as soon as you step into this room?";
			if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
				message = "You don't have 10 Pokemon selected to bring! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
			}
			for (String s : message.split("\n")) {
				Task.addTask(Task.TEXT, s);
			}
			Task.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?", 0);
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
	
	public int getUnregisteredBasePokemon(Random random) {
		int id = 0;
		int counter = 0;
		do {
			id = Pokemon.getRandomBasePokemon(random);
			counter++;
		} while (p.pokedex[id] == 2 && counter < 100);
		
		return id;
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
