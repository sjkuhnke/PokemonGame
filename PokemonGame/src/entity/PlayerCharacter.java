package entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.VerticalLayout;

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
import overworld.BlackjackPanel;
import overworld.GamePanel;
import overworld.KeyHandler;
import overworld.Main;
import overworld.PMap;
import pokemon.Item;
import pokemon.Move;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Trainer;
import pokemon.JGradientButton;
import pokemon.Pokemon.Task;

public class PlayerCharacter extends Entity {
	
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	public Player p;

	public String currentSave;

	private int cooldown;
	
	public static String currentMapName;
	
	private BufferedImage up3, up4, down3, down4, left3, left4, right3, right4;
	
	public PlayerCharacter(GamePanel gp, KeyHandler keyH) {
		super(gp, null);
		this.keyH = keyH;
		
		screenX = gp.screenWidth / 2 - (gp.tileSize/2);
		screenY = gp.screenHeight / 2 - (gp.tileSize/2);
		
		solidArea = new Rectangle(8, 16, 32, 32);
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		
		name = "Finn";
		
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
				int random = r.nextInt(150);
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
				for (Pokemon p : p.team) {
            		if (p != null) p.awardHappiness(1, false);
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
		}
		
		gp.eHandler.checkEvent();
		
		if (gp.currentMap == 107 && gp.checkSpin && gp.ticks % 4 == 0 && new Random().nextInt(3) == 0) {
			int index = new Random().nextInt(10);
			if (gp.grusts[index] != null) gp.grusts[index].turnRandom();
		}
		
		if (keyH.dPressed) {
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
				if (target instanceof NPC_Nurse) {
					interactNurse(target);
				} else if (target instanceof NPC_Clerk || target instanceof NPC_Market) {
					interactClerk(target);
				} else if (target instanceof NPC_Block) {
					interactNPC((NPC_Block) target);
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
				if (target instanceof Cut_Tree) {
					interactCutTree(iTileIndex);
				} else if (target instanceof Rock_Smash) {
					interactRockSmash(iTileIndex);
				} else if (target instanceof Vine_Crossable) {
					interactVines(iTileIndex);
				} else if (target instanceof Pit) {
					interactPit(iTileIndex);
				} else if (target instanceof Whirlpool) {
					interactWhirlpool(iTileIndex);
				} else if (target instanceof Rock_Climb) {
					interactRockClimb(iTileIndex);
				} else if (target instanceof Starter_Machine) {
					interactStarterMachine(iTileIndex);
				} else if (target instanceof Locked_Door) {
					interactLockedDoor(iTileIndex);
				} else if (target instanceof Fuse_Box) {
					interactFuseBox(iTileIndex);
				} else if (target instanceof Snowball) {
					interactSnowball(iTileIndex);
				} else if (target instanceof IceBlock) {
					interactIceBlock(iTileIndex);
				}
			}
		}
		if (keyH.aPressed) {
			if (keyH.ctrlPressed) {
				keyH.ctrlPressed = false;
				Item.useCalc(p, null, null);
			} else {
				if (p.fish) {
					int result = gp.cChecker.checkTileType(this);
					if (result == 3 || (result >= 24 && result <= 36) || (result >= 313 && result <= 324)) {
						startWild(currentMapName, 'F');
					}
				}
			}
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
			
			Pokemon.addTrainerTask(entity, id, foe, !(entity instanceof NPC_GymLeader));
			
			// Heal if Gym Leader
			if (Trainer.getTrainer(trainer).getMoney() == 500) {
				p.heal();
			}
		} else if (entity.altDialogue != null) {
			gp.setTaskState();
			
			entity.speak(2);
		}
	}
	
	public void startWild(String area, char type) {
		gp.setTaskState();
		
		Pokemon foe = gp.encounterPokemon(area, type, p.random);
		Pokemon.addStartBattleTask(-2, -1, foe);
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
		
		Task t = Pokemon.addTask(Task.ITEM, "You found " + count + " " + itemName + "!");
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
				Task t = Pokemon.addTask(Task.ITEM, "You found 1 " + i.toString() + "!");
				t.item = i;
			}
			
			Pokemon.addTask(Task.ITEM_SUM, chest, "");
			
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
		gp.gameState = GamePanel.SHOP_STATE;
		npc.facePlayer(direction);
		npc.speak(-1);
	}
	
	public void interactNPC(NPC_Block npc) {
		gp.keyH.wPressed = false;
		npc.facePlayer(direction);
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
	
	private void interactBlock(NPC_Block npc) {
		if (gp.currentMap == 52) { // Professor Dad
			if (!p.flag[0][0]) {
				p.flag[0][0] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "Welcome to the wonderful world of Pokemon! I'm sure you're familiar with them considering your old man is a top scientist here in Xhenos.");
				Pokemon.addTask(Task.DIALOGUE, npc, "But, as a reminder, Pokemon are pocket monsters with unique typings and traits that coexist with us humans.");
				Pokemon.addTask(Task.DIALOGUE, npc, "And I think it's high time you go on a Pokemon adventure of your own, get outta town, and see what this region has to offer.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You see that machine over there son? I've started a program to help new trainers, and you and two other bright young boys are to be the first participants.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Inside that machine holds three Pokemon I have chosen to be the starting options. So go pick your starter!");
			} else if (p.flag[0][0] && !p.flag[0][1]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Go ahead, go pick your starter from my machine! The world of Xhenos awaits!");
			} else if (p.flag[0][1] && !p.flag[0][2]) {
				p.flag[0][2] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "You picked " + p.team[0].getName() + "? Wonderful pick! It seems very eager to get out and explore, as I'm sure you are too.");
				Pokemon.addTask(Task.DIALOGUE, npc, "But!");
				Pokemon.addTask(Task.DIALOGUE, npc, "As your Dad, I must make sure you have adequate equipment to not get lost out there.");
				Pokemon.addTask(Task.DIALOGUE, npc, "So first, I'm giving you this digital map, equipped with Cellular Data that will always update with where you are!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You got the Map!");
				Pokemon.addTask(Task.DIALOGUE, npc, "And as your Professor, I need your help for collecting as much data about the Pokemon inhabiting our world with us!");
				Pokemon.addTask(Task.DIALOGUE, npc, "This little doohickey is the Neodex! In a region as unique as ours, I've had to make plenty of modifications to account for them.");
				Pokemon.addTask(Task.DIALOGUE, npc, "It's one of my finest inventions yet, and I even got help from Professor Oak, the greatest professor of all time!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Instead of Rotom, it taps into a shared database that allows for identifying new forms for old Pokemon. Give it a whirl!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You got the Pokedex!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh right! Speaking of collecting data, I made a little something to help you find as many different species as you can.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.DEX_NAV;
				Pokemon.addTask(Task.DIALOGUE, npc, "Just open your bag and open the \"Key Items\" pocket, you should see it there. When you're near grass, it'll show you the Pokemon there!");
				Pokemon.addTask(Task.DIALOGUE, npc, "I would've installed it in the Neodex, but I have all of the slots reserved for special database add-ons to keep track of all the unique forms here.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Speaking of which, as you know son, my specialty as a Professor is studying Shadow Pokemon.");
				Pokemon.addTask(Task.DIALOGUE, npc, "These Pokemon had their DNA changed long ago by the meteor in Shadow Ravine, which happens to be just north of here.");
				Pokemon.addTask(Task.DIALOGUE, npc, "They often have taken Dark and Ghost typings, and have significant changes in mentality, similar to Xhenovian Pokemon.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Here, can I see your Pokedex for a second?");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "There, I added a 'Shadow Pokedex' section to your Neodex. That way, if you run into any Shadow forms near Shadow Ravine, you can record them!");
				Pokemon.addTask(Task.DIALOGUE, npc, "...What's that? Oh right, I should probably explain how to use the Neodex.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You can see a Pokemon you've registered in the Neodex and their information, like their moves, abilities, typings, and much more!");
				Pokemon.addTask(Task.DIALOGUE, npc, "If you want to toggle which section you're in, just press the 'A' key and it will cycle through all of your sections you have installed!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh and one more thing! Oh my Arceus, I can't believe I almost forgot to tell you about my favorite creation yet!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You've always loved battling, so I made this just for you. A state of the line BATTLECALC 3000! Or you could just call it your calculator.");
				Pokemon.addTask(Task.DIALOGUE, npc, "It provides accurate data during a battle, just press 'A' in a battle, or 'Ctrl + A' outside, and you can check how much damage a move can do!");
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.CALCULATOR;
				Pokemon.addTask(Task.DIALOGUE, npc, "Now go out there and make me proud - and most importantly...");
				Pokemon.addTask(Task.DIALOGUE, npc, "COLLECT THAT DATA!");
				Pokemon.addTask(Task.UPDATE, "");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "How's it going?");
			}
		} else if (gp.currentMap == 51) { // Dad's Mom
			if (!p.flag[0][3]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Ah, I remember my first time having a Pokemon adventure, before I had your father. I was around your age now actually. Brings back fond memories...");
				Pokemon.addTask(Task.DIALOGUE, npc, "...Oh, you want advice? I believe being a Pokemon trainer isn't about being the strongest, or collecting them all.");
				Pokemon.addTask(Task.DIALOGUE, npc, "It's about forming a life-long bond with your Pokemon, becoming partners. Friendship is key to becoming a better trainer, and a better person.");
				Pokemon.addTask(Task.DIALOGUE, npc, "That's how it was with your grandfather. Back in my day, I wanted to be the best there ever was.");
				Pokemon.addTask(Task.DIALOGUE, npc, "He helped me see the value in friendship, how Pokemon were great companions in life. He's the person who gave me Duchess' dinky old Soothe Bell.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I miss him every day, even with my Pokemon, you and your father by my side.");
				Pokemon.addTask(Task.DIALOGUE, npc, "So, this is my parting gift to you, to show you the power of friendship. I'm so thankful to have been your grandmother.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.SOOTHE_BELL;
				Pokemon.addTask(Task.DIALOGUE, npc, "Go out there and make the world a better place, dear. I know you will.");
				p.flag[0][3] = true;
			}
		} else if (gp.currentMap == 3) {
			Pokemon.addTask(Task.DIALOGUE, npc, "I believe he was looking to introduce himself to you, he mentioned he was heading towards New Minnow Town.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Please do make haste, I do hope he's okay.");
			p.flag[0][4] = true;
		} else if (gp.currentMap == 47 && p.secondStarter != -1) {
			Pokemon.addTask(Task.DIALOGUE, npc, "Here, we breed and house rare Pokemon to fight against their extinction.");
			Pokemon.addTask(Task.DIALOGUE, npc, "...What's that? You have a " + Pokemon.getName(((p.starter + 1) * 3) - 2) + "?? That's insanely rare. Did you get that from the professor?");
			Pokemon.addTask(Task.DIALOGUE, npc, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Task t = Pokemon.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		} else if (gp.currentMap == 4) {
			if (p.flag[0][11] && !p.flag[0][12]) {
				p.flag[0][12] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "What, you've got a package, pipsqueak? Pfft! You can't be older than 10... Didn't know Robin stooped that low.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Last time I checked, the boss wasn't expecting any packages. You can't even get in here anyway, I JUST locked the door and I'm getting the hell outta here.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I'm running off to the woods and you'll NEVER find me in there, don't even TRY to follow me.");
				Pokemon.addTask(Task.DIALOGUE, npc, "So yeah, big whoop. Tell that bundle of nerves - A.K.A. your boss - to deal with it. See ya idiot!");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "BEAT IT!");
			}
		} else if (gp.currentMap == 161) {
			if (!p.flag[0][7]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Sorry, I'm a little busy right now. As the only mailman in this entire region, I've been running ragged delivering letters across Xhenos.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I can't afford to waste any time, even for a gym battle.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh? You'll help me? Thank you, thank you, thank you! Maybe then I'll have enough time to open the gym... Here, take these!");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_A;
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_B;
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_C;
				p.flag[0][7] = true;
			} else if (p.flag[0][7] && !p.flag[0][11]) {
				if (p.flag[0][8] && p.flag[0][9] && p.flag[0][10]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "Oh it's you, thanks for delivering all those packages! You're a lifesaver, and I truly thank you for being selfless.");
					Pokemon.addTask(Task.DIALOGUE, npc, "But... there's one last package to deliver.");
					Pokemon.addTask(Task.DIALOGUE, npc, "It's been sitting here for weeks for the warehouse, but I can't get in to the building and the owner isn't answering any of my calls.");
					Pokemon.addTask(Task.DIALOGUE, npc, "I've been far too busy to look into it though, could you look into it for me? I've seen some pretty suspicious activity going on there.");
					Pokemon.addTask(Task.DIALOGUE, npc, "Oh, you will? Thank you so much! Don't worry, it's just a short walk away. Once that's done, I'll have a small window where I can open the gym.");
					Task t = Pokemon.addTask(Task.ITEM, "");
					t.item = Item.PACKAGE_D;
					p.flag[0][11] = true;
					Pokemon.addTask(Task.DIALOGUE, npc, "Welp, the clock is ticking, and I need more caffeine.");
				} else {
					Pokemon.addTask(Task.DIALOGUE, npc, "The addresses are on the back of the boxes. You're doing me a massive favor, so once you're done I can give you that battle. So get to it!");
				}
			} else if (p.flag[0][11] && !p.flag[0][15]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, hello my helper! How's it going at the warehouse? Did you manage to get in okay?");
			} else if (p.flag[0][15]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, you've delivered my last package? I can't thank you enough, I can finally have a break from all this mail. I'll open the gym up right away, with no delay!");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
				p.flag[0][16] = true;
			}
		} else if (gp.currentMap == 57) {
			if (p.flag[0][7] && !p.flag[0][9]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh! That's my new frying pan! Thanks squirt, I owe you something.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I got a little trinket I've been wanting to get rid of - I mean pass off - to a worthy kiddo like you. Consider this your appetizer!");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.FLAME_ORB;
				p.bag.remove(Item.PACKAGE_B);
				p.flag[0][9] = true;
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Be sure to check me out in Rawwar city sometime!");
			}
		} else if (gp.currentMap == 58) {
			if (p.flag[0][7] && !p.flag[0][17]) {
				if (!p.flag[0][8]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "...Oh, is that my package?");
					Pokemon.addTask(Task.DIALOGUE, npc, "Thank you so much young man! Sorry for jumping at you, I'm just really unsettled right now.");
					p.bag.remove(Item.PACKAGE_A);
					p.flag[0][8] = true;
				} else {
					Pokemon.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				}
				Pokemon.addTask(Task.DIALOGUE, npc, "Some rampant Pokemon sniffing out berries all burst into my house, but I was just making juice.");
				Pokemon.addTask(Task.DIALOGUE, npc, "In the midst of all the chaos, some crook snuck in and stole my precious item right off my poor Pokemon!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Whatever will I do to find it?");
			} else if (p.flag[0][7] && p.flag[0][17]) {
				p.flag[0][18] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh my god, my item! You found it! Wow, you really are impressive!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You know what, since you're so kind, why don't you just keep it? It'll be better off with a stronger trainer like you.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Here, take this for your troubles too. Really, it's the least I can do.");
				Task t = Pokemon.addTask(Task.ITEM, "Obtained a Lucky Egg!");
				t.item = Item.LUCKY_EGG;
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I just got robbed! And at the worst time too, I was expecting a new juicer I was so excited for...");
				Pokemon.addTask(Task.DIALOGUE, npc, "And I heard a noise at my door thinking it was Robin with my package, but it was a criminal who burst right in!");
				Pokemon.addTask(Task.DIALOGUE, npc, "It's times like this where I wish I was still married.");
			}
		} else if (gp.currentMap == 48) {
			if (!p.flag[0][7]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Feel free to look around!");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, you have a package for us? Thank you so much! I'm sure Robin greatly appreciates the help!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Here, have a complimentary gift dog! No really, we insist.");
				
				Random dog = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = dog.nextInt(3);
				id = 120 + (id * 3);
				Pokemon dogP = new Pokemon(id, 5, true, false);
				Pokemon.addTask(Task.TEXT, "You adopted a gift dog!");
				Task t = Pokemon.addTask(Task.GIFT, "", dogP);
				t.item = Item.SILK_SCARF;
				p.bag.remove(Item.PACKAGE_C);
				p.flag[0][10] = true;
			}
		} else if (gp.currentMap == 46) {
			Pokemon.addTask(Task.HP, "Check your team's Hidden Power types here!");
		} else if (gp.currentMap == 8) {
			Pokemon.addTask(Task.DIALOGUE, npc, "And at the worst time too, I was expecting a package all the way from Galar.");
			if (p.flag[0][14] && !p.flag[0][15]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "What's that? You have the package for me? That must be my lucky stapler! Please hand it here, young one.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Robin must be swamped if he's making you do all this delivery work. Is this his version of a gym puzzle?");
				Pokemon.addTask(Task.DIALOGUE, npc, "Heh! I'm just messing with you, you must have volunteered. Here, for your generosity and service.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.HM01;
				p.bag.remove(Item.PACKAGE_D);
				p.flag[0][15] = true;
			} else if (!p.flag[0][14]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Can you please help me get rid of these criminals? I don't have any Pokemon of my own, I won't be of much help to you I'm afraid.");
			}
		} else if (gp.currentMap == 10) {
			Pokemon.addTask(Task.DIALOGUE, npc, "That's hello where I come from. I'm Ryder, adventurer extraordinaire at the ripe old age of 16.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Say, you look like a competent Pokemon trainer. Mind taking care of something for me?");
			Pokemon abra = new Pokemon(243, 15, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + abra.name + "!");
			Pokemon.addTask(Task.GIFT, "", abra);
			Pokemon.addTask(Task.DIALOGUE, npc, "Yeah, I noticed Abra seems to gain a new ability here, probably because of that new magic type I've been hearing about.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Anyways, it was pleasure doing business with you mate, I'm sure I'll see you around. I gotta see what the rest of the region has in store for me!");
			Pokemon.addTask(Task.FLASH_IN, "");
			Pokemon.addTask(Task.UPDATE, "");
			Pokemon.addTask(Task.FLASH_OUT, "");
			p.flag[1][3] = true;
		} else if (gp.currentMap == 13) {
			if (worldY / gp.tileSize >= 59) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Wait, you're his kid? Oh my goodness, your father has told me all about you. Did you come here trying to challenge the gym?");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "You did? Well, that's going to be a difficult task considering the city's gone into full black-out, no power or anything.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I'd try and fix it, but the Control Center door is jammed, and that's our best shot at figuring out what's wrong.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Most of the buildings in this city have electric doors, and they all seem to not be working.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Actually, you know what we could try?");
				Pokemon.addTask(Task.DIALOGUE, npc, "We can grab enough auxillery energy to keep the Control Center door open, and then give it a go from there.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Could you meet me there? It's located at the end of Route 45.");
				Pokemon.addTask(Task.DIALOGUE, npc, "It's up straight North of this city, and then just a bit East.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I'll go on up ahead to set everything up, please don't keep me waiting!");
				Pokemon.addTask(Task.DIALOGUE, npc, "I'm sure your father would be proud of you for helping out.");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
				p.flag[1][1] = true;
			} else if (worldY / gp.tileSize < 59 && worldY / gp.tileSize >= 51) {
				Pokemon.addTask(Task.DIALOGUE, npc, "As much as I appreciate the help, don't think I'll go easy on you. Normal types really shouldn't be underestimated.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Good luck to you both, see you guys inside.");
				p.flag[1][16] = true;
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
			} else if (worldY / gp.tileSize < 51) {
				if (worldX / gp.tileSize < 50) {
					Pokemon.addTask(Task.DIALOGUE, npc, "Wow, you beat Stanford? And you restored power to the city? You're impressive!");
					Pokemon.addTask(Task.DIALOGUE, npc, "Say, you're helping your dad do research, right? I'm sure you've come across a couple Xhenovian forms then.");
					Pokemon.addTask(Task.DIALOGUE, npc, "You seem like you love Pokemon so this might be obvious to you, but those Pokemon are variants of Pokemon from other regions.");
					Pokemon.addTask(Task.DIALOGUE, npc, "My mom's friend is actually a researcher studying the forms here, and I'm sure you guys could help each other out!");
					Pokemon.addTask(Task.DIALOGUE, npc, "She's camped out right near here, come with me, let me introduce you to her!");
					Task t = Pokemon.addTask(Task.TELEPORT, "");
					t.counter = 13;
					t.start = 88;
					t.finish = 19;
					t.wipe = false;
					p.flag[2][1] = true;
					t = Pokemon.addTask(Task.FLAG, "");
					t.start = 2;
					t.finish = 3;
					Pokemon.addTask(Task.DIALOGUE, npc, "Hello there Ryder! Who's this that you brought with?");
					Pokemon.addTask(Task.DIALOGUE, npc, "... Well, nice to meet you young man! What do I owe the pleasure of this visit to?");
					Pokemon.addTask(Task.DIALOGUE, npc, "...");
					Pokemon.addTask(Task.DIALOGUE, npc, "Yes, I do research Xhenovian forms! If you can bring me a regional form, I can trade you for its counterpart!");
					Pokemon.addTask(Task.DIALOGUE, npc, "First though, I'll have to upgrade your Pokedex to add a 'Variant' Pokedex for you to keep track of these forms");
					Pokemon.addTask(Task.DIALOGUE, npc, "... And there you go! All upgraded. Come talk to me when you have a Xhenovian Pokemon to trade!");
					Pokemon.addTask(Task.DIALOGUE, npc, "Ryder, it was great to see you as always. Take care boys!");
					t = Pokemon.addTask(Task.TURN, this, "");
					t.counter = 3;
					t = Pokemon.addTask(Task.TURN, gp.npc[13][7], "");
					t.counter = 2;
					t.start = 13;
					t.finish = 7;
					Pokemon.addTask(Task.DIALOGUE, npc, "Thanks for your patience, I just figured that this little connection would help out you both mutually.");
					Pokemon.addTask(Task.DIALOGUE, npc, "I'm gonna head out, I'll see you soon!");
					Pokemon.addTask(Task.FLASH_IN, "");
					Pokemon.addTask(Task.UPDATE, "");
					Pokemon.addTask(Task.FLASH_OUT, "");
				} else {
					if (!p.flag[2][1]) {
						Pokemon.addTask(Task.DIALOGUE, npc, "I think you might have went the wrong way...");
						Pokemon.addTask(Task.DIALOGUE, npc, "Are you looking for Mt. Splinkty? You have to go back to Route 26 and head North!");
					} else {
						Pokemon.addTask(Task.DIALOGUE, npc, "Got any Xhenovian forms to trade me?");
						Pokemon.addTask(Task.REGIONAL_TRADE, "");
					}
				}
			}
			
		} else if (gp.currentMap == 162) {
			if (!p.flag[1][2]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "You're just in time, I almost have the energy prepared.");
				Pokemon.addTask(Task.DIALOGUE, npc, "In the meantime, can we chat for a bit about what I do here?");
				Pokemon.addTask(Task.DIALOGUE, npc, "I have this research post out here in the sticks because of the close proximity to Electric Tunnel, the birthplace of another type of Pokemon.");
				Pokemon.addTask(Task.DIALOGUE, npc, "They're the Electric forms, and I've seen a lot of them migrating to Sicab City to feast on the power.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I suspect the power issues have been caused by them finishing off all of the energy there, causing the outage.");
				Pokemon.addTask(Task.DIALOGUE, npc, "When we head back to try and fix it, I figure we'll probably run into some Electric forms there.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Thankfully, your dad told me to work on an extension to record all of the new forms, and I'd like to give it to you to help me document them.");
				Pokemon.addTask(Task.DIALOGUE, npc, "So, without further ado, here's an update to your Neodex!");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "There, I added an 'Electric Pokedex' section. That way you can be prepared to take note of any new forms you see there!");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "Okay! I believe I have enough auxillary power here to open the door of the Control Center for a bit.");
				Pokemon.addTask(Task.DIALOGUE, npc, "However, I don't think you have enough time to get back there on foot. But, who says you need to walk?");
				Pokemon.addTask(Task.DIALOGUE, npc, "Here's a high-tech invention I've made so you can warp back! Let me upgrade your map real quick to integerate the tech.");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "Now you can teleport instantly to any town you've already been! Use it to get back to the city in time, and the doors to the center should be open.");
				Pokemon.addTask(Task.DIALOGUE, npc, "At least if my calculations are correct.");
				Pokemon.addTask(Task.DIALOGUE, npc, "God speed kid, and tell your dad I said hi!");
				p.flag[1][2] = true;
			} else if (p.flag[1][2] && !p.flag[1][16]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "The energy levels are getting low? Hold on, I can get them up for a little longer.");
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				Pokemon.addTask(Task.DIALOGUE, npc, "Alright, keep at it champ.");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Have you seen any new Electric forms? Can I take a look?");
			}
			
		} else if (gp.currentMap == 32 && !p.flag[1][17]) {
			p.flag[1][17] = true;
			p.fish = true;
			Pokemon.addTask(Task.DIALOGUE, npc, "Say, you look like you'd be great at fishing. Here, take this spare I got lying around. Maybe you'll fish up a Durfish!");
			Task t = Pokemon.addTask(Task.ITEM, "");
			t.item = Item.FISHING_ROD;
			Pokemon.addTask(Task.DIALOGUE, npc, "Look at water and press 'A', or use the item in your bag to fish!");
		} else if (gp.currentMap == 16) {
			if (!p.flag[1][7]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Name's Stanford, the leader of this here town, though I haven't done much work here. At least not recently.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Honestly, this town is pretty neglected and has gone to shit recently. You're like the first person I've met that's given a fuck.");
				Pokemon.addTask(Task.DIALOGUE, npc, "This stupid electric ghost is running through my team, and I'm running out of options.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I'm a Normal-type gym leader for fuck's sake! It doesn't get affected by most of my attacks!");
			} else if (p.flag[1][7] && !p.flag[1][8]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, the ghost is gone. Good job kiddo.");
				Pokemon.addTask(Task.DIALOGUE, npc, "The fuse box might be usable now, could you try turning it on?");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Thanks for the hand squirt, though don't doubt Normal types because of this, they can still be very strong.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Just not against supernatural shit, fuck whatever that was. Those ones seemed extra powerful too.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Did you notice that they all had their hidden abilities? Those are special abilities only accessible through a special item.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Tell you what, as thanks for helping, I'd like to give you this. Try it out to get a special ability of your own!");
				p.flag[1][9] = true;
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.ABILITY_PATCH;
				Pokemon.addTask(Task.DIALOGUE, npc, "That fuse box should've opened a gate in the Control Center, you should check out the situation there.");
				if (!p.flag[1][12]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "The energy field still seems to not be fully fixed yet though, and I noticed some commotion at the office.");
				}
				Pokemon.addTask(Task.DIALOGUE, npc, "But, you know what, I'm heading to the bar or something. See ya kid.");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
			}
		} else if (gp.currentMap == 18) {
			if (worldX / gp.tileSize >= 49) {
				if (!p.flag[1][11]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "Not... strong... enough...");
				} else if (p.flag[1][11] && !p.flag[1][12]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
					Pokemon.addTask(Task.DIALOGUE, npc, "Anyways, I think now that you deal with... whatever Electric Pokemon that was... that the fuse box is working. Try turning it on real quick, my head is buzzing...");
				} else if (p.flag[1][12] && !p.flag[1][13]) {
					p.flag[1][13] = true;
					Pokemon.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
					Pokemon.addTask(Task.DIALOGUE, npc, "Oh, by the way, I found something here that might be useful. Consider it as a token of appreciation...");
					Task t = Pokemon.addTask(Task.ITEM, "");
					t.item = Item.HM02;
					Pokemon.addTask(Task.DIALOGUE, npc, "It's a magic trick that makes rocks disappear! Anyways, I should get back to the gym to see if the gym leader finally came back. Presto!");
					Pokemon.addTask(Task.FLASH_IN, "");
					Pokemon.addTask(Task.UPDATE, "");
					Pokemon.addTask(Task.FLASH_OUT, "");
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
				Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
				Pokemon.addTask(Task.GIFT, "", result);
			}
		} else if (gp.currentMap == 28) {
			if (worldX / gp.tileSize <= 70) { // Millie 1
				Pokemon.addTask(Task.DIALOGUE, npc, "I'm Millie, kind of a big deal here. I've starred in several movies and TV stuff!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You've probably heard of Magikarp Jump: The Motion Picture, and Mystery Doors of the Magical Land: The Animated Series.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Anyways, yeah this isn't a shoot or scene, the trainers here are possessed or something! They just attack anything that try to enter the town.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You're going to have to fend them off back to back, and from what I've seen most of the town is infected! I'm not one to be in horror movies...");
				Pokemon.addTask(Task.DIALOGUE, npc, "There won't be any breaks in between once we go in, so you're gonna need some strong Pokemon.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I saw those evil guys by the cell tower. They messed with the PC's signal somehow, and now you can only use Pokemon in the Gauntlet Box!");
				Pokemon.addTask(Task.DIALOGUE, npc, "You might wanna put some Pokemon in there, preferably 4. If you don't have a full Gauntlet Box as well as a full team, you'll have a huge disadvantage!");
				int selected = p.getAmountSelected();
				String message = "Are you ready to fight once you come with me? We need to defend our town and there's no going back!";
				if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
					message = "You don't have 4 Pokemon selected to bring in the Gauntlet Box! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
				}
				for (String s : message.split("\n")) {
					Pokemon.addTask(Task.DIALOGUE, npc, s);
				}
				Pokemon.addTask(Task.DIALOGUE, npc, "Trust me, I had to fight off some of them, they hit hard. Especially the stunt doubles...");
				Task t = Pokemon.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?");
				t.counter = 2;
				// Millie 2
				t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 2;
				Pokemon.addTask(Task.DIALOGUE, npc, "There's this weird kid that's been in a trance blocking the way to the tower. He hasn't moved an inch, almost like he's guarding the place.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I've heard him mutter a few things. \"Toxic\", \"Get decked\", it's off-putting.");
				t = Pokemon.addTask(Task.TURN, gp.npc[28][1], "");
				t.counter = 3;
				t.start = 28;
				t.finish = 1;
				Pokemon.addTask(Task.DIALOGUE, npc, "Wait, that's your rival? Maybe he'll recognize you, try saying something that'll, I don't know, make him angry!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Y'know, like in method acting! Give it a shot.");
				t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 1;
				t = Pokemon.addTask(Task.TURN, gp.npc[28][1], "");
				t.counter = 1;
				t.start = 28;
				t.finish = 1;
				Pokemon.addTask(Task.DIALOGUE, npc, "...");
				t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 2;
				Pokemon.addTask(Task.DIALOGUE, npc, "AHHH! HE'S DEFINITELY AWAKE NOW! JEEZ, WHAT DID YOU EVEN SAY!?");
				t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 1;
				t = Pokemon.addTask(Task.FLAG, "");
				t.start = 2;
				t.finish = 5;
			} else {
				if (worldY / gp.tileSize < 37) {
					if (worldY / gp.tileSize > 33) { // Millie 3
						if (!p.flag[2][7]) {
							Pokemon.addTask(Task.DIALOGUE, npc, "It's definitely too dangerous to go back to the town, at least for now. You need to scout the area to find the source of these strange radio waves...");
							Pokemon.addTask(Task.DIALOGUE, npc, "Let me know if you figure out what's going on! Please... I'm scared...");
						} else {
							Pokemon.addTask(Task.DIALOGUE, npc, "Thank Arceus you're back... I was so petrified in fear.");
							Pokemon.addTask(Task.DIALOGUE, npc, "What did you find out? ... Oh, ... Oh my. What?!?!?");
							Pokemon.addTask(Task.DIALOGUE, npc, "There's a poor Pokemon chained up to the tower?? That is so cruel! Ack! That makes me so sick.");
							Pokemon.addTask(Task.DIALOGUE, npc, "It has to be a pretty powerful Pokemon to be able to transmit such strong radio waves to possess all of these people.");
							Pokemon.addTask(Task.DIALOGUE, npc, "Did you happen to notice what Pokemon it was?");
							Pokemon.addTask(Task.DIALOGUE, npc, "A... what? An alien spider? What??");
							Pokemon.addTask(Task.DIALOGUE, npc, "I have no idea what you're talking about, I've never heard of anything like that in my life.");
							Pokemon.addTask(Task.DIALOGUE, npc, "If it's a spider though, I'm sure we can be great friends. I love bugs!");
							Pokemon.addTask(Task.DIALOGUE, npc, "Alien bugs might take a bit more to get used to. I wonder where it came from?");
							Pokemon.addTask(Task.DIALOGUE, npc, "And I can't believe it's chained up! We have to set it free! I bet it was those space goons.");
							Pokemon.addTask(Task.DIALOGUE, npc, "Come to think of it, I did notice an evil grunt run off towards Mt. Splinkty. Maybe we can stop him?");
							Pokemon.addTask(Task.DIALOGUE, npc, "Go check out the Mountain and see if you can find the grunt, I'll stand guard here!");
							p.flag[2][8] = true;
							Pokemon.addTask(Task.FLASH_IN, "");
							Pokemon.addTask(Task.UPDATE, "");
							Pokemon.addTask(Task.FLASH_OUT, "");
						}
					} else if (worldY / gp.tileSize > 25) { // Millie 4
						Pokemon.addTask(Task.DIALOGUE, npc, "OHHH! YOU! I'm so sorry, that was a reflex, there's so many of them.");
						if (!p.flag[2][9]) {
							Pokemon.addTask(Task.DIALOGUE, npc, "Any luck finding the grunt? ..I've been holding them off okay.");
							Pokemon.addTask(Task.DIALOGUE, npc, "Like I said, before, I believe that there's a secret room that you can access on the 2nd floor, check that out if you haven't yet.");
						} else {
							Pokemon.addTask(Task.DIALOGUE, npc, "You found him? How'd it go? Are you and your Pokemon okay?");
							Pokemon.addTask(Task.DIALOGUE, npc, "You got wire cutters? Quick, let's try and cut that creature free and stop this madness!");
							Pokemon.addTask(Task.DIALOGUE, npc, "I'll try and make sure the possessed people are okay. Good luck!");
							p.flag[2][10] = true;
							Pokemon.addTask(Task.FLASH_IN, "");
							Pokemon.addTask(Task.UPDATE, "");
							Pokemon.addTask(Task.FLASH_OUT, "");
						}
					} else { // chained up xurkitree
						if (!p.flag[2][11]) {
							Pokemon.addTask(Task.TEXT, "(The mysterious Pokemon seems to be alive, though it's unable to move because it's chained up.)");
							if (!p.flag[2][9]) {
								p.flag[2][7] = true;
							} else {
								p.bag.remove(Item.WIRE_CUTTERS);
								p.flag[2][11] = true;
								Pokemon.addTask(Task.TEXT, "You used the wire cutters to set it free!");
								Pokemon.addTask(Task.FLASH_IN, "");
								Pokemon.addTask(Task.UPDATE, "");
								Pokemon.addTask(Task.FLASH_OUT, "");
								Pokemon.addTask(Task.DIALOGUE, npc, "Bzzz....Zzzzttt..... ZUZUZUURRKIII!!!");
								Pokemon.addTask(Task.DIALOGUE, npc, "(The mysterious creature seems shell-shocked, and is now lashing out at everything around it!)");
								Task t = Pokemon.addTask(Task.BATTLE, "");
								t.counter = 387;
								t.start = 284;
							}	
						}
					}
				} else if (worldY / gp.tileSize < 42) { // Player wiped, talked to her from the other side
					Pokemon.addTask(Task.DIALOGUE, npc, "Oh, there you are. What happened?");
					Pokemon.addTask(Task.DIALOGUE, npc, "They must have beaten you up... You're definitely a tough kid, but I need you back in there. Maybe be extra careful this time.");
					Task t = Pokemon.addTask(Task.TELEPORT, "");
					t.counter = 28;
					t.start = 82;
					t.finish = 36;
					t.wipe = false;
				} else { // millie 5
					p.flag[2][13] = true;
					Pokemon.addTask(Task.DIALOGUE, npc, "Even though bug Pokemon aren't as flimsy and weak as they seem, I still have a feeling that you'll be able to defeat me.");
					Pokemon.addTask(Task.DIALOGUE, npc, "I mean, you were literally able to fend off a huge alien bug and save me and my town!");
					Pokemon.addTask(Task.DIALOGUE, npc, "Have you noticed the big openings in the Earth around this area? There's lots more around this region.");
					Pokemon.addTask(Task.DIALOGUE, npc, "There's a pretty interesting myth that my mom would tell me stories of when I was younger.");
					Pokemon.addTask(Task.DIALOGUE, npc, "She said that the terrain wasn't always so rough, that it's actually the result of an ancient war that happened.");
					Pokemon.addTask(Task.DIALOGUE, npc, "According to the myth, there used to be a cult-like following of an all-powerful diety that apparently created the Pokemon and people living here!");
					Pokemon.addTask(Task.DIALOGUE, npc, "But once people discovered the existance of alien Pokemon and the Galactic type, their religion was proven obsolete by the athiests.");
					Pokemon.addTask(Task.DIALOGUE, npc, "Or at least the athiests tried to disprove it that way. As you can imagine, it didn't go over too well with the believers, erupting in the massive conflict.");
					Pokemon.addTask(Task.DIALOGUE, npc, "Apparently the destruction of the terrain was actually caused by the leaders of each group fighting, who are rumored to both be powerful Pokemon.");
					Pokemon.addTask(Task.DIALOGUE, npc, "But, as one of my favorite creators would say, that's \"just a theory\", after all.");
					Pokemon.addTask(Task.DIALOGUE, npc, "I do like thinking about it though, it's pretty interesting, wouldn't you agree?");
					Pokemon.addTask(Task.DIALOGUE, npc, "..What's that? Oh right! Silly me, I was going to give you a technique to help traverse the gaps!");
					Pokemon.addTask(Task.DIALOGUE, npc, "I just got side-tracked thinking about all of the cool films that could be made about that myth. It's a pretty popular one around here!");
					Pokemon.addTask(Task.DIALOGUE, npc, "Anyways, I'm only giving you this technique because even though you can't use it until you beat me, I know you have the skills to do it.");
					Task t = Pokemon.addTask(Task.ITEM, "");
					t.item = Item.HM03;
					Pokemon.addTask(Task.DIALOGUE, npc, "Come fight me inside when you're ready, I know it'll be an amazing battle. Maybe even one we'll turn into a film when you're a world-famous trainer someday!");
					Pokemon.addTask(Task.DIALOGUE, npc, "Good luck, my friend. You're going to need it! EEK I'm so excited!!");
					Pokemon.addTask(Task.FLASH_IN, "");
					Pokemon.addTask(Task.UPDATE, "");
					Pokemon.addTask(Task.FLASH_OUT, "");
				}
			}
		} else if (gp.currentMap == 49) {
			p.flag[2][14] = true;
			Pokemon.addTask(Task.DIALOGUE, npc, "I encountered this very strong Pokemon, and I don't think I'm strong enough to train it. Here!");
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
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 50) {
			p.flag[3][9] = true;
			Pokemon.addTask(Task.DIALOGUE, npc, "Great choice young cracka!!!!");
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
				Pokemon.addTask(Task.DIALOGUE, npc, "Wait..... you have that\none?!?!? Shit. Well, take\nthis one instead bozo");
				boolean sparkitten = gift.nextBoolean();
				if (sparkitten) {
					id = 108;
				} else {
					id = 190;
				}
			}
			Pokemon result = new Pokemon(id, 25, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 39) {
			p.flag[3][0] = true;
			Pokemon.addTask(Task.DIALOGUE, npc, "Well, I say it's weather, more like the atmospheric nonsense going outside.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Jeez it's so bright, worse than the time I stared directly at the sun.");
			Pokemon.addTask(Task.DIALOGUE, npc, "But hey Alakazam, you live and you learn. Thankfully I brought these awesome Pit Sevipers!");
			Pokemon.addTask(Task.DIALOGUE, npc, "These suckers block out all kinds of rays, heat rays, UV rays, even Confuse Rays!");
			Pokemon.addTask(Task.DIALOGUE, npc, "You look quite strained, I'll let you borrow these since I wasn't planning on sticking around long anyways.");
			Task t = Pokemon.addTask(Task.ITEM, "");
			t.item = Item.VISOR;
			Pokemon.addTask(Task.DIALOGUE, npc, "If you put those on you should be able to see a lot better outside, it's crazy out there.");
			Pokemon.addTask(Task.DIALOGUE, npc, "To be honest with you dude, I'm not even sure why it's so darn bright... and the local school is in a full blown panic about it.");
			Pokemon.addTask(Task.DIALOGUE, npc, "The classrooms are all locked and there's no sign of the teachers... come to think of it, that should probably be looked at.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Just don't think I'll be doing too much of the \"looking\", because you have my one and only pair of shades.");
			Pokemon.addTask(Task.DIALOGUE, npc, "Oh, one more thing before I leave you to save the town alone! There's a lot of Ice-type Pokemon in this area...");
			Pokemon.addTask(Task.DIALOGUE, npc, "So I'm entrusting you with one of my favorite Pokemon!");
			Pokemon.addTask(Task.DIALOGUE, npc, "I brought a couple Flamigo as travel buddies from my home region, and I'm entrusting one to help you out with the Ice-types!");
			Pokemon.addTask(Task.TEXT, "You received Flamigo!");
			Pokemon p = new Pokemon(246, 35, true, false);
			t = Pokemon.addTask(Task.GIFT, "", p);
			Pokemon.addTask(Task.DIALOGUE, npc, "...Just probably don't let it get hit by an Ice move. Anyways, toodles, and good luck kid!");
			Pokemon.addTask(Task.FLASH_IN, "");
			Pokemon.addTask(Task.UPDATE, "");
			Pokemon.addTask(Task.FLASH_OUT, "");
		} else if (gp.currentMap == 38) { // ice master/robin
			if (worldY / gp.tileSize < 30) { // ice master
				Pokemon.addTask(Task.DIALOGUE, npc, "It may help you in your journey, maybe even in ways you don't expect. Consider it a token of my thanks for your help with the school.");
				p.flag[3][10] = true;
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PETTICOAT_GEM;
			} else { // robin cutscene
				Pokemon.addTask(Task.DIALOGUE, npc, "*pant* I even had to jam your portal system just so I could track you down.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I've got an urgent letter from your mom. Don't know what's in it, but she said it was really important.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.LETTER;
				Pokemon.addTask(Task.DIALOGUE, npc, "She was pretty insistent I get this to you right away. You might want to open it as soon as you can.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Anyway, I'm just glad I finally found you. Take care of yourself, alright?");
				p.flag[4][0] = true;
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
			}
		} else if (gp.currentMap == 43) { // ground master
			Pokemon.addTask(Task.DIALOGUE, npc, "It's rare, even among explorers. Use it wisely - it could be the edge you need.");
			p.flag[3][11] = true;
			Task t = Pokemon.addTask(Task.ITEM, "");
			t.item = Item.VALIANT_GEM;
			Pokemon.addTask(Task.DIALOGUE, npc, "That gem was unearthed during a deep excavation, and I was saving it for someone special. Looks like that's you.");
		} else if (gp.currentMap == 165) { // principal
			Pokemon.addTask(Task.DIALOGUE, npc, "I don't have any Pokemon, so I wasn't sure what to do! But now that I see you're here to help... maybe there's hope.");
			if (!p.flag[3][1] && !p.flag[3][2]) { // haven't found either ground or ice master
				Pokemon.addTask(Task.DIALOGUE, npc, "Listen, this whole situation is a mess! Those Grunts have taken over the classrooms, and I can't do anything to stop them.");
				Pokemon.addTask(Task.DIALOGUE, npc, "The teachers - Ice Master and Ground Master - might be able to help, but I haven't seen them in a while.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I think Ice Master is somewhere in the fields north of town, and Ground Master... I believe he's hiding out in the city.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Find them, please!");
			} else if ((p.flag[3][1] || p.flag[3][2]) && !p.flag[3][3] && !p.flag[3][4]) { // got at least one of the keys but hasn't unlocked the classrooms yet
				String teacher = "";
				if (p.flag[3][1] && !p.flag[3][2]) teacher = "Ice Master's";
				if (p.flag[3][2] && !p.flag[3][1]) teacher = "Ground Master's";
				if (p.flag[3][1] && p.flag[3][2]) teacher = "both of their";
				Pokemon.addTask(Task.DIALOGUE, npc, "You've got " + teacher + " keys? That's amazing!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Now you can get into the classrooms and see what those Team Eclipse Grunts are up to.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I wish I could do more, but... I don't have any Pokemon, and I - I just can't face them.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Please, go and clear them out! I'll do whatever I can to help once they're gone.");
			} else if ((p.flag[3][3] || p.flag[3][4]) && (!p.flag[3][5] || !p.flag[3][6])) {
				Pokemon.addTask(Task.DIALOGUE, npc, "You've unlocked a classroom, right? Go check inside! There are Grunts taking over the school, and it's a disaster.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I know I should be the one handling this, but without Pokemon, I'm helpless! Please, clear out those rooms!");
			} else if (p.flag[3][5] && p.flag[3][6]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "You did it! You cleared both classrooms! I - I don't know how to thank you enough.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I may not be able to help in battle, but I can give you something that will aid you on your journey.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Here - take this, it's the HM for Surf. I know you'll make great use of it.");
				p.flag[3][7] = true;
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.HM04;
				if (!p.flag[3][8]) {
					Pokemon.addTask(Task.DIALOGUE, npc, "Now, go find the cause of the extreme light outside with your new tools and put a stop to it!");
				}
			}
		} else if (gp.currentMap == 44) {
			Pokemon.addTask(Task.DIALOGUE, npc, "Leader Glacius said no battles until we can get things under control in the city.");
			if (!p.flag[3][5] || !p.flag[3][6]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "You're wondering why the gym's closed, right? Well, it's not just the gym - there's trouble over at the school too.");
				Pokemon.addTask(Task.DIALOGUE, npc, "The gym's been shut down ever since the school got overrun by those Team Eclipse guys.");
				Pokemon.addTask(Task.DIALOGUE, npc, "If you're looking to challenge Glacius, you'll need to sort out what's happening at the school first.");
			} else if (p.flag[3][5] && p.flag[3][6] && !p.flag[3][7]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "I heard you cleared the whole school out of those goons! The principal mentioned he wants to thank you for everything you've done.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Might be a good idea to visit him before you come back here. He should be in his office in the school.");
			} else if (p.flag[3][7] && !p.flag[3][8]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "You did great clearing the school of those Grunts, but that bright light's still making things difficult.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Leader Glacius won't accept challengers until the light's sorted out. Maybe you can do something about it?");
			}
		} else if (gp.currentMap == 91) { // grandpa
			if (!p.flag[4][2]) {
				p.flag[4][2] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh... thank Arceus you're here. I was afraid I wouldn't make it through this.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Those thugs - Team Eclipse - stormed in, demanding something that I don't fully understand.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You beat them, right? Why are they still here? Can you do anything?");
				Task t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 0;
				Pokemon.addTask(Task.SPEAK, this, "Get out, you creeps! Leave us alone. I already destroyed your flimsy Pokemon, you want some more?");
				Pokemon.addTask(Task.SPEAK, this, "Didn't think so. Then, GET!");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
				t = Pokemon.addTask(Task.TURN, this, "");
				t.counter = 1;
				Pokemon.addTask(Task.DIALOGUE, npc, "Thank you Finn, I don't know what I would've done without you.");
				Pokemon.addTask(Task.DIALOGUE, npc, "They said something about summoning an Ultra Paradox Pokemon... I think it's some dangerous creature they're after.");
				Pokemon.addTask(Task.DIALOGUE, npc, "I heard they were headed for Peaceful Park next, something about unleashing that alien there, probably.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You might want to stop them before they cause more chaos.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh! But before you go, I have a gift for you.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Take this, it's the HM for Slow Fall. It should help you out exploring Xhenos once you take down Mindy.");
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.HM05;
				Pokemon.addTask(Task.DIALOGUE, npc, "Be careful out there. These aren't just ordinary trainers - they're after something far more dangerous than I've ever seen.");
			} else {
				Pokemon.addTask(Task.DIALOGUE, npc, "Still here, eh? You'd better hurry to Peaceful Park before those Team Eclipse thugs cause even more trouble.");
				Pokemon.addTask(Task.DIALOGUE, npc, "They were talking about summoning an Ultra Paradox Pokemon, some terrible creature. The park's just south of Kleine Village - head there quickly!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Be careful, that thing's dangerous. I know you can handle it, but don't take it lightly.");
			}
		} else if (gp.currentMap == 93) { // Move reminder
			Pokemon.addTask(Task.PARTY, "");
		} else if (gp.currentMap == 94) {
			p.flag[4][4] = true;
			Pokemon.addTask(Task.TEXT, "One makes Pokemon surge with electricity, and another casts them in a strange shadow.");
			Pokemon.addTask(Task.TEXT, "Here's a gift of one of the Pokemon affected!");
			int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226};
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.pokedex[ids[index]] == 2 && counter < 100);
			
			Pokemon result = new Pokemon(ids[index], 30, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		} else if (gp.currentMap == 107) { // arthra
			if (!p.flag[5][0]) {
				Task t = Pokemon.addTask(Task.TURN, npc, "");
				t.counter = 0;
				Pokemon.addTask(Task.SPOT, npc, "");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, didn't expect company out here. You must be here for the same reason, yeah? You've noticed these ghosts swarming the woods?");
				Pokemon.addTask(Task.DIALOGUE, npc, "Name's Arthra, by the way. Merlin's my grandfather; you'll meet him someday if you get that far. Right now, I'm trying to figure out what's causing this mess.");
				Pokemon.addTask(Task.DIALOGUE, npc, "These ghosts are sneaky, but I've got them down. They're phasing in and out like something's messing with their energy.");
				Pokemon.addTask(Task.DIALOGUE, npc, "And I'm willing to bet it's no accident.");
				Pokemon.addTask(Task.DIALOGUE, npc, "But if you're serious about this, let's see if you can keep up. I don't have time to drag around dead weight.");
				t = Pokemon.addTask(Task.BATTLE, "");
				t.counter = 392;
			} else {
				p.flag[5][1] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "But don't get too comfortable with that win. I'll make sure you regret it next time we battle.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You want to play hero here? Be my guest! If you're so great, then take care of those ghosts yourself. I've got better things to do.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Oh, and here you go, hero! You can probably make far better use of this than I ever could!");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.ABILITY_PATCH;
				Pokemon.addTask(Task.DIALOGUE, npc, "I really hate your hero attitude. You don't know shit. But like I said, have at it here.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Just don't mess it up. And keep an eye out - these ghosts aren't the only threat around here.");
				Pokemon.addTask(Task.DIALOGUE, npc, "There's something bigger going on, and I'll figure it out - no thanks to you. See you around, hero.");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
			}
		} else if (gp.currentMap == 103) { // maxwell 1
			if (!p.flag[5][4]) {
				Pokemon.addTask(Task.DIALOGUE, npc, "Thra'knash koru'dan Gzhaz... Zhar'mir vakta da'tor!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Vahl'orim Dragowrath! Zhar'kor-Gzazha... vass'dar athra!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Zharkh'nir da'kash! Gzazha, ir'thar vak'tai khar... rise'thil an'dor!");
				Pokemon.addTask(Task.DIALOGUE, npc, "Vahl'krim da'sharak... rise! KHAR DA'ZHAR GZAZHA!");
				Task t = Pokemon.addTask(Task.SHAKE, "BOOOOOOOOOOOOOOOOOOOOOM!");
				t.counter = 300;
				t = Pokemon.addTask(Task.TURN, npc, "");
				t.counter = 1;
				Pokemon.addTask(Task.SPOT, npc, "");
				Pokemon.addTask(Task.DIALOGUE, npc, "BWAHAHAHAH! The pest finally arrives! Did you really think you could follow us all the way down here and stop what's already in motion? How amusingly naive.");
				Pokemon.addTask(Task.DIALOGUE, npc, "You see, child, I am far more than just a 'trainer'. I am the vanguard of an empire – we are here to prepare this world for something beyond your comprehension.");
				Pokemon.addTask(Task.DIALOGUE, npc, "My master, Dragowrath, watches from the stars. Earth is merely the first of many worlds he'll claim, and when the Sorcerer rises, this land will be ours.");
				Pokemon.addTask(Task.DIALOGUE, npc, "But enough talk. You've come this far, so let's end this with a little... demonstration of power. Prepare yourself – my Pokemon and I will not hold back.");
				t = Pokemon.addTask(Task.BATTLE, "");
				t.counter = 217;
			} else if (!p.flag[5][9]) {
				p.flag[5][9] = true;
				Pokemon.addTask(Task.DIALOGUE, npc, "Impressive... but don't think this victory means anything. The master plan is already in place, and there's nothing you can do to stop us.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Enjoy your hollow victory while it lasts. Soon, this world will be torn apart, and you'll see just how powerless you truly are.");
				Pokemon.addTask(Task.DIALOGUE, npc, "Farewell... and enjoy what little time your precious world has left.");
				Pokemon.addTask(Task.FLASH_IN, "");
				Pokemon.addTask(Task.UPDATE, "");
				Pokemon.addTask(Task.FLASH_OUT, "");
				Task t = Pokemon.addTask(Task.SHAKE, "");
				t.counter = 100;
				
				t = Pokemon.addTask(Task.MOVE_CAMERA, "");
				t.counter = 2;
				t.start = 1;
				t.finish = -400;
				
				t = Pokemon.addTask(Task.SHAKE, "");
				t.counter = 50;
				
				t = Pokemon.addTask(Task.SLEEP, "");
				t.counter = 30;
				
				t = Pokemon.addTask(Task.MOVE_NPC, gp.npc[103][1], "");
				t.counter = 2;
				t.start = 1;
				t.finish = 45 * gp.tileSize;
				t.wipe = true;
				
				t = Pokemon.addTask(Task.TURN, gp.npc[103][1], "");
				t.counter = Task.RIGHT;
				
				t = Pokemon.addTask(Task.MOVE_NPC, gp.npc[103][1], "");
				t.counter = 2;
				t.start = 0;
				t.finish = 51 * gp.tileSize;
				t.wipe = true;
				
				t = Pokemon.addTask(Task.TURN, gp.npc[103][1], "");
				t.counter = Task.UP;
				
				t = Pokemon.addTask(Task.MOVE_NPC, gp.npc[103][1], "");
				t.counter = 2;
				t.start = 1;
				t.finish = 41 * gp.tileSize;
				t.wipe = true;
				
				t = Pokemon.addTask(Task.TURN, gp.npc[103][1], "");
				t.counter = Task.DOWN;
				
				t = Pokemon.addTask(Task.SLEEP, "");
				t.counter = 60;
				
				t = Pokemon.addTask(Task.MOVE_CAMERA, "");
				t.wipe = true;
				t.counter = 60;
				t.start = 0;
				t.finish = 0;
				Pokemon.addTask(Task.TEXT, "A powerful presense awaits...");
			}
		}
		if (gp.currentMap == 109 && !p.flags[22]) {
				p.flags[22] = true;
				Pokemon.addTask(Task.TEXT, "Here, could you raise it for me?");
				int[] ids = new int[] {177, 179, 98};
				Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int index = gift.nextInt(ids.length - 1);
				if (p.pokedex[ids[0]] == 2 || p.pokedex[ids[1]] == 2) {
					Pokemon.addTask(Task.TEXT, "Oh, you already have a " + Pokemon.getName(ids[index]) + "? I'll give it to someone else to look after then.");
					Pokemon.addTask(Task.TEXT, "Here, take this really rare baby Pokemon instead!");
					index = 2;
				}
				
				Pokemon result = new Pokemon(ids[index], 1, true, false);
				Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
				Pokemon.addTask(Task.GIFT, "", result);
				
		}
		if (gp.currentMap == 118) {
			Pokemon.addTask(Task.TEXT, "Do you have any fossils for me to resurrect?");
			Pokemon.addTask(Task.FOSSIL, "Do you have any fossils for me to resurrect?");
		} if (gp.currentMap == 127) {
			int answer = JOptionPane.showOptionDialog(null,
					"Would you like to play Blackjack?\n(Warning: Will Auto-Save)",
		            "Blackjack?",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (answer == JOptionPane.YES_OPTION) {
				// Remove all existing components from the JFrame
			    Main.window.getContentPane().removeAll();

			    // Create and add the BlackjackPanel
			    BlackjackPanel bjPanel = new BlackjackPanel(gp);
			    Main.window.getContentPane().add(bjPanel);

			    // Set focus on the BlackjackPanel
			    bjPanel.requestFocusInWindow();

			    // Repaint the JFrame to reflect the changes
			    Main.window.revalidate();
			    Main.window.repaint();
			}
		}
		if (gp.currentMap == 129 && worldY / gp.tileSize > 41) {
			if (!p.flags[23]) {
				Pokemon.addTask(Task.TEXT, "Oh, you haven't gotten any coins yet?\nHere, just this once, have some!");
				Pokemon.addTask(Task.TEXT, "You recieved 100 Coins!");
				p.coins += 100;
				p.flags[23] = true;
			} else {
				JPanel panel = new JPanel();
				JButton coinButton = new JButton("1 Coin to $25");
				coinButton.setPreferredSize(new Dimension(120, 75));
				panel.setPreferredSize(new Dimension(200, 100));
				panel.add(coinButton);
				coinButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
		            	if (SwingUtilities.isLeftMouseButton(evt)) {
		            		if (p.coins > 0) {
		            			p.coins--;
		            			p.setMoney(p.getMoney() + 25);
		            		} else {
		            			JOptionPane.showMessageDialog(null, "Not enough coins!");
		            		}
		            	} else {
		            		String input = JOptionPane.showInputDialog(null, "Enter the amount of coins to exchange:");
		                    try {
		                        int coins = Integer.parseInt(input);
		                        if (p.coins > coins && coins > 0) {
		                        	p.coins -= coins;
		                        	p.setMoney(coins * 25);
		                        } else {
		                        	JOptionPane.showMessageDialog(null, "Not enough coins!");
		                        }
		                        
		                    } catch (NumberFormatException e) {
		                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
		                    }
		            	}
		            	SwingUtilities.getWindowAncestor(panel).dispose();
		            	showPrizeMenu(panel, p.coins + " coins   $" + p.getMoney());
		            }
				});
				showPrizeMenu(panel, p.coins + " coins   $" + p.getMoney());
			}
		} if (gp.currentMap == 129 && worldY / gp.tileSize <= 41) {
			JPanel shopPanel = new JPanel();
			shopPanel.setLayout(new GridLayout(0, 3));
			Map<Pokemon, Integer> pokemonMap = new HashMap<>();
			pokemonMap.put(new Pokemon(150, 25, true, false), 3); // Kissyfishy
			pokemonMap.put(new Pokemon(143, 25, true, false), 4); // Posho
			pokemonMap.put(new Pokemon(238, 25, true, false), 5); // Scraggy
			pokemonMap.put(new Pokemon(193, 25, true, false), 5); // Consodust
			pokemonMap.put(new Pokemon(195, 25, true, false), 6); // Rockmite
			pokemonMap.put(new Pokemon(184, 25, true, false), 8); // Tinkie
			pokemonMap.put(new Pokemon(187, 25, true, false), 8); // Dragee
			pokemonMap.put(new Pokemon(190, 25, true, false), 10); // Blobmo
			pokemonMap.put(new Pokemon(232, 25, true, false), 15); // Triwandoliz
			pokemonMap.put(new Pokemon(231, 25, true, false), 20); // Kleinyeti
			JPanel pokemonPanel = new JPanel(new VerticalLayout());
			
			List<Map.Entry<Pokemon, Integer>> sortedPokemonList = pokemonMap.entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .collect(Collectors.toList());
			
			Map<Item, Integer> itemMap = new HashMap<>();
			itemMap.put(Item.FOCUS_SASH, 10);
			itemMap.put(Item.AIR_BALLOON, 25);
			itemMap.put(Item.POWER_HERB, 5);
			itemMap.put(Item.WHITE_HERB, 5);
			itemMap.put(Item.WEAKNESS_POLICY, 20);
			itemMap.put(Item.BLUNDER_POLICY, 20);
			itemMap.put(Item.RED_CARD, 25);
			itemMap.put(Item.THROAT_SPRAY, 15);
			JPanel itemPanel = new JPanel(new VerticalLayout());
			
			List<Map.Entry<Item, Integer>> sortedItemList = itemMap.entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .collect(Collectors.toList());
			
			Map<Item, Integer> tmMap = new HashMap<>();
			tmMap.put(Item.TM59, 10);
			tmMap.put(Item.TM39, 25);
			tmMap.put(Item.TM96, 40);
			tmMap.put(Item.TM21, 60);
			tmMap.put(Item.TM70, 75);
			tmMap.put(Item.TM64, 100);
			tmMap.put(Item.TM94, 150);
			JPanel tmPanel = new JPanel(new VerticalLayout());
			
			List<Map.Entry<Item, Integer>> sortedTMList = tmMap.entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .collect(Collectors.toList());
			
			for (Map.Entry<Item, Integer> e : sortedItemList) {
				JPanel iPanel = new JPanel();
		    	JGradientButton item = new JGradientButton(e.getKey().toString() + ": " + e.getValue() + " coins");
		    	item.setBackground(e.getKey().getColor());
		    	item.addMouseListener(new MouseAdapter() {
		        	@Override
				    public void mouseClicked(MouseEvent evt) {
		    	    	if (p.coins >= e.getValue()) {
		    	    		JOptionPane.showMessageDialog(null, "Purchased 1 " + e.getKey().toString() + " for " + e.getValue() + " coins");
		    	    		p.bag.add(e.getKey());
		    	    		p.coins -= e.getValue();
		    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
		    	            showPrizeMenu(shopPanel, p.coins + " coins   " + p.winStreak + " win streak   " + p.gamesWon + " wins");
		    	        } else {
		    	            JOptionPane.showMessageDialog(null, "Not enough coins!");
		    	        }
		        	}
		    	});
		    	JLabel icon = new JLabel();
		    	icon.setIcon(new ImageIcon(e.getKey().getImage()));
		    	iPanel.add(icon);
		    	iPanel.add(item);
		    	itemPanel.add(iPanel);
			}
			
			for (Map.Entry<Item, Integer> e : sortedTMList) {
				JPanel iPanel = new JPanel();
		    	JGradientButton item = new JGradientButton(e.getKey().toString() + ": " + e.getValue() + " games won");
		    	item.setBackground(e.getKey().getColor());
		    	item.addMouseListener(new MouseAdapter() {
		        	@Override
				    public void mouseClicked(MouseEvent evt) {
		    	    	if (p.gamesWon >= e.getValue()) {
		    	    		JOptionPane.showMessageDialog(null, "Purchased 1 " + e.getKey().toString() + " for " + e.getValue() + " games won");
		    	    		p.bag.add(e.getKey());
		    	    		p.gamesWon -= e.getValue();
		    	    		tmPanel.remove(iPanel);
		    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
		    	            showPrizeMenu(shopPanel, p.coins + " coins   " + p.winStreak + " win streak   " + p.gamesWon + " wins");
		    	        } else {
		    	            JOptionPane.showMessageDialog(null, "Not enough total wins!");
		    	        }
		        	}
		    	});
		    	JLabel icon = new JLabel();
		    	icon.setIcon(new ImageIcon(e.getKey().getImage()));
		    	iPanel.add(icon);
		    	iPanel.add(item);
		    	tmPanel.add(iPanel);
		    	if (e.getKey().isTM() && p.bag.contains(e.getKey().getID())) tmPanel.remove(iPanel);
			}
			
			for (Map.Entry<Pokemon, Integer> e : sortedPokemonList) {
				JPanel iPanel = new JPanel();
				iPanel.setLayout(new VerticalLayout());
		    	JGradientButton item = new JGradientButton(e.getKey().nickname + ": " + e.getValue() + " win streak");
		    	Color color2 = e.getKey().type2 == null ? e.getKey().type1.getColor() : e.getKey().type2.getColor();
		    	item.setBackground(e.getKey().type1.getColor(), color2);
		    	item.addMouseListener(new MouseAdapter() {
		        	@Override
				    public void mouseClicked(MouseEvent evt) {
		        		if (SwingUtilities.isRightMouseButton(evt)) {
				            JOptionPane.showMessageDialog(null, e.getKey().showSummary(p, false, null), "Pokemon Summary", JOptionPane.INFORMATION_MESSAGE);
			    		} else {
			    	    	if (p.winStreak >= e.getValue()) {
			    	            JOptionPane.showMessageDialog(null, "Got 1 " + e.getKey().nickname + "!");
			    	            p.catchPokemon(e.getKey());
			    	            pokemonPanel.remove(iPanel);
			    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
			    	            showPrizeMenu(shopPanel, p.coins + " coins   " + p.winStreak + " win streak   " + p.gamesWon + " wins");
			    	        } else {
			    	            JOptionPane.showMessageDialog(null, "Not enough wins in a row!");
			    	        }
			    		}
		        	}
		    	});
		    	iPanel.add(item);
		    	pokemonPanel.add(iPanel);
		    	if (p.pokedex[e.getKey().id] == 2) pokemonPanel.remove(iPanel);
			}
			
			shopPanel.add(itemPanel);
			shopPanel.add(tmPanel);
			shopPanel.add(pokemonPanel);
			showPrizeMenu(shopPanel, p.coins + " coins   " + p.winStreak + " win streak   " + p.gamesWon + " wins");
		} if (gp.currentMap == 130 && !p.flags[25]) {
			p.flags[25] = true;
			Pokemon.addTask(Task.TEXT, "Here, take this rare Pokemon!");
			Pokemon result = new Pokemon(97, 50, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		} if (gp.currentMap == 138 && !p.flags[26]) {
			p.flags[26] = true;
			Pokemon.addTask(Task.TEXT, "Here, I have a gift for you for being so kind.");
			Pokemon.addTask(Task.TEXT, "Obtained HM07 Rock Climb!");
			p.bag.add(Item.HM07);
		} if (gp.currentMap == 146 && !p.flags[28]) {
			int selected = p.getAmountSelected();
			String message = "Are you ready to fight as soon as you step into this room?";
			if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
				message = "You don't have 10 Pokemon selected to bring! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
			}
			for (String s : message.split("\n")) {
				Pokemon.addTask(Task.TEXT, s);
			}
			Task t = Pokemon.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?");
			t.counter = 0;
		}
	}

	private void showPrizeMenu(JPanel panel, String title) {
		JOptionPane.showMessageDialog(null, panel, title, JOptionPane.QUESTION_MESSAGE);
	}
	
	private void interactCutTree(int i) {
		if (p.hasMove(Move.CUT)) {
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
	
	private void interactRockSmash(int i) {
		if (p.hasMove(Move.ROCK_SMASH)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage("This rock looks like it can be broken!");
		}
		
	}
	
	private void interactVines(int i) {
		if (p.hasMove(Move.VINE_CROSS)) {
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
	
	private void interactPit(int i) {
		if (p.hasMove(Move.SLOW_FALL)) {
			gp.keyH.wPressed = false;
			Pit pit = (Pit) gp.iTile[gp.currentMap][i];
			gp.eHandler.teleport(pit.mapDest, pit.xDest, pit.yDest, false);
			generateParticle(pit);
		} else {
			gp.ui.showMessage("This pit looks deep!\nI can't even see the bottom!");
		}
		
	}
	
	private void interactWhirlpool(int i) {
		if (p.hasMove(Move.WHIRLPOOL)) {
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
	
	private void interactRockClimb(int i) {
		if (p.hasMove(Move.ROCK_CLIMB)) {
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
			gp.keyH.wPressed = false;
			gp.gameState = GamePanel.STARTER_STATE;
		} else if (p.flag[0][1] && !p.flag[0][4]) { // After picking a starter and before the first gate
			gp.ui.showMessage(Item.breakString("There are two Pokemon still inside the machine. Wonder what Dad will do with them?", 42));
		} else if (p.flag[0][4] && p.badges < 1) { // After the first gate but before beating Gym 1
			gp.ui.showMessage(Item.breakString("There's still a Pokemon left! Dad must've given one to Scott as well!", 42));
		} else if (p.badges > 1 && !p.flag[1][0]) { // After beating Gym 1
			gp.ui.showMessage("There aren't any Pokemon inside.");
		} else { // Dialogue after beating Fred 1 but before Gym 2
			gp.ui.showMessage(Item.breakString("There aren't any Pokemon left, Dad must've given one to Scott and one to Fredrick.", 42));
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
	
	private void interactSnowball(int i) {
		if (p.bag.contains(Item.SHOVEL)) {
			gp.keyH.wPressed = false;
			generateParticle(gp.iTile[gp.currentMap][i]);
			gp.iTile[gp.currentMap][i] = null;
		} else {
			gp.ui.showMessage("This pile of snow looks like it can be\nshoveled!");
		}
		
	}
	
	private void interactIceBlock(int i) {
		if (p.bag.contains(Item.ICE_PICK)) {
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
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.equals("Ben")) {
			p.catchPokemon(new Pokemon(238, 5, true, false));
			SwingUtilities.getWindowAncestor(cheats).dispose();
		} else if (code.startsWith("anyi")) {
			String[] parts = code.split(" ");
		    if (parts.length >= 3) {
		        try {
		            int id = Integer.parseInt(parts[1]);
		            int amt = Integer.parseInt(parts[2]);
		            p.bag.add(Item.getItem(id), amt);
		            SwingUtilities.getWindowAncestor(cheats).dispose();
		        } catch (NumberFormatException g) {
		            // Handle invalid input (e.g., if the entered value is not a valid integer)
		            JOptionPane.showMessageDialog(null, "Invalid item ID.");
		        }
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
			JPanel panel = Item.displayGenerator(p);
			JOptionPane.showMessageDialog(null, panel);
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
			// Create a list from the entry set of the itemMap
		    List<Map.Entry<Item, Integer>> itemList = new ArrayList<>(gp.aSetter.itemMap.entrySet());
		    itemList.removeIf(entry -> entry.getKey() == null);

		    // Sort the list based on the item name (assuming Item class has a getName() method)
		    itemList.sort((entry1, entry2) -> entry1.getKey().toString().compareToIgnoreCase(entry2.getKey().toString()));

		    // Print the sorted item list
		    for (Map.Entry<Item, Integer> entry : itemList) {
		        System.out.println(entry.getKey().toString() + "," + entry.getValue());
		    }
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
}
