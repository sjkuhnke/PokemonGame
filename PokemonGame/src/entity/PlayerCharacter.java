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
import object.InteractiveTile;
import object.Locked_Door;
import object.Pit;
import object.Rock_Climb;
import object.Rock_Smash;
import object.Starter_Machine;
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
	
	public PlayerCharacter(GamePanel gp, KeyHandler keyH) {
		super(gp);
		this.keyH = keyH;
		
		screenX = gp.screenWidth / 2 - (gp.tileSize/2);
		screenY = gp.screenHeight / 2 - (gp.tileSize/2);
		
		solidArea = new Rectangle();
		solidArea.x = 8;
		solidArea.y = 16;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.width = 32;
		solidArea.height = 32;
		
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
		up1 = setup("/player/red2");
		up2 = setup("/player/red2_1");
		down1 = setup("/player/red1");
		down2 = setup("/player/red1_1");
		left1 = setup("/player/red3");
		left2 = setup("/player/red3_1");
		right1 = setup("/player/red4");
		right2 = setup("/player/red4_1");
		
		surf1 = setup("/player/surf1");
		surf2 = setup("/player/surf2");
		surf3 = setup("/player/surf3");
		surf4 = setup("/player/surf4");
	}
	
	public void update() {
		gp.ticks++;
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
					break;
				case "down":
					worldY += speed;
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
				if (spriteNum == 1) {
					spriteNum = 2;
				}
				else if (spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
				p.steps++;
				cooldown++;
			}
			if (gp.ticks == 4 && (inTallGrass || p.surf || p.lavasurf) && !p.repel && cooldown > 2) {
				Random r = new Random();
				int random = r.nextInt(150);
				if (random < speed) {
					cooldown = 0;
					String area = "Standard";
					if (p.surf) area = "Surfing";
					if (p.lavasurf) area = "Lava";
					gp.startWild(gp.currentMap, worldX / gp.tileSize, worldY / gp.tileSize, area);
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
			
			gp.eHandler.checkEvent();
			
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
					snapY -= gp.tileSize / 2;
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
					snapY -= gp.tileSize / 2;
					this.worldX = snapX;
					this.worldY = snapY;
				}
			}
			String currentMap = currentMapName;
			PMap.getLoc(gp.currentMap, (int) Math.round(worldX * 1.0 / gp.tileSize), (int) Math.round(worldY * 1.0 / gp.tileSize));
			if (!currentMap.equals(currentMapName)) gp.ui.showAreaName();
			Main.window.setTitle(gp.gameTitle + " - " + currentMapName);
		}
		if (gp.currentMap == 107 && gp.checkSpin && gp.ticks == 4 && new Random().nextInt(3) == 0) {
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
		if (gp.ticks > 4) {
			gp.ticks = 0;
		}
		for (int i = 0; i < gp.npc[1].length; i++) {
			int trainer = gp.npc[gp.currentMap][i] == null ? 0 : gp.npc[gp.currentMap][i].trainer;
			if (gp.ticks == 0 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "down") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 1 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "up") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 2 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "left") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 3 && gp.npc[gp.currentMap][i] != null && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "right") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
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
					gp.startBattle(target.trainer);
				} else if (target instanceof NPC_GymLeader) {
					gp.startBattle(target.trainer);
				} else if (target instanceof NPC_PC) {
					gp.openBox((NPC_PC) target);
				} else if (target instanceof NPC_Pokemon) {
					gp.startBattle(target.trainer, ((NPC_Pokemon) target).id);
				}
			}
			
			// Check items
			int objIndex = gp.cChecker.checkObject(this);
			if (objIndex != -1) pickUpObject(objIndex);
			
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
				}
			}
		}
		if (keyH.aPressed) {
			if (keyH.ctrlPressed) {
				keyH.ctrlPressed = false;
				Item.useCalc(p, null);
			} else {
				if (p.fish) {
					int result = gp.cChecker.checkTileType(this);
					if (result == 3 || (result >= 24 && result <= 36) || (result >= 313 && result <= 324)) {
						gp.startWild(gp.currentMap, worldX / gp.tileSize, worldY / gp.tileSize, "Fishing");
					}
				}
			}
		}
	}

	private void pickUpObject(int objIndex) {
		gp.keyH.wPressed = false;
		gp.gameState = GamePanel.TASK_STATE;
		Item item = gp.obj[gp.currentMap][objIndex].item;
		int count = gp.obj[gp.currentMap][objIndex].count;
		String itemName = item.toString();
		if (count > 1 && itemName.contains("Berry")) itemName = itemName.replace("Berry", "Berries");
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
		npc.speak(0);
	}
	
	private void interactClerk(Entity npc) {
		gp.keyH.wPressed = false;
		gp.gameState = GamePanel.SHOP_STATE;
		npc.speak(0);
	}
	
	private void interactNPC(NPC_Block npc) {
		gp.keyH.wPressed = false;
		if (npc.flag == -1 || !p.flag[npc.getFlagX()][npc.getFlagY()]) {
			gp.gameState = GamePanel.DIALOGUE_STATE;
			npc.speak(0);
			if (npc.more) {
				SwingUtilities.invokeLater(() -> {
					gp.keyH.resetKeys();
					gp.gameState = GamePanel.TASK_STATE;
					interactBlock(npc);
				});
			}
		} else if (npc.altDialogue != null) {
			gp.gameState = GamePanel.DIALOGUE_STATE;
			npc.speak(1);
		}
		
	}
	
	private void interactBlock(NPC_Block npc) {
		if (gp.currentMap == 52) { // Professor Dad
			if (!p.flag[0][0]) {
				p.flag[0][0] = true;
				Pokemon.addTask(Task.TEXT, "Welcome to the wonderful world of Pokemon! I'm sure you're familiar with them considering your old man is a top scientist here in Xhenos.");
				Pokemon.addTask(Task.TEXT, "But, as a reminder, Pokemon are pocket monsters with unique typings and traits that coexist with us humans.");
				Pokemon.addTask(Task.TEXT, "And I think it's high time you go on a Pokemon adventure of your own, get outta town, and see what this region has to offer.");
				Pokemon.addTask(Task.TEXT, "You see that machine over there son? I've started a program to help new trainers, and you and two other bright young boys are to be the first participants.");
				Pokemon.addTask(Task.TEXT, "Inside that machine holds three Pokemon I have chosen to be the starting options. So go pick your starter!");
			} else if (p.flag[0][0] && !p.flag[0][1]) {
				Pokemon.addTask(Task.TEXT, "Go ahead, go pick your starter from my machine! The world of Xhenos awaits!");
			} else if (p.flag[0][1] && !p.flag[0][2]) {
				p.flag[0][2] = true;
				Pokemon.addTask(Task.TEXT, "You picked " + p.team[0].getName() + "? Wonderful pick! It seems very eager to get out and explore, as I'm sure you are too.");
				Pokemon.addTask(Task.TEXT, "But!");
				Pokemon.addTask(Task.TEXT, "As your Dad, I must make sure you have adequate equipment to not get lost out there.");
				Pokemon.addTask(Task.TEXT, "So first, I'm giving you this digital map, equipped with Cellular Data that will always update with where you are!");
				Pokemon.addTask(Task.TEXT, "You got the Map!");
				Pokemon.addTask(Task.TEXT, "And as your Professor, I need your help for collecting as much data about the Pokemon inhabiting our world with us!");
				Pokemon.addTask(Task.TEXT, "This little doohickey is the Neodex! In a region as unique as ours, I've had to make plenty of modifications to account for them.");
				Pokemon.addTask(Task.TEXT, "It's one of my finest inventions yet, and I even got help from Professor Oak, the greatest professor of all time!");
				Pokemon.addTask(Task.TEXT, "Instead of Rotom, it taps into a shared database that allows for identifying new forms for old Pokemon. Give it a whirl!");
				Pokemon.addTask(Task.TEXT, "You got the Pokedex!");
				Pokemon.addTask(Task.TEXT, "Oh right! Speaking of collecting data, I made a little something to help you find as many different species as you can.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.DEX_NAV;
				Pokemon.addTask(Task.TEXT, "Just open your bag and open the \"Key Items\" pocket, you should see it there. When you're near grass, it'll show you the Pokemon there!");
				Pokemon.addTask(Task.TEXT, "I would've installed it in the Neodex, but I have all of the slots reserved for special database add-ons to keep track of all the unique forms here.");
				Pokemon.addTask(Task.TEXT, "Speaking of which, as you know son, my specialty as a Professor is studying Shadow Pokemon.");
				Pokemon.addTask(Task.TEXT, "These Pokemon had their DNA changed long ago by the meteor in Shadow Ravine, which happens to be just north of here.");
				Pokemon.addTask(Task.TEXT, "They often have taken Dark and Ghost typings, and have significant changes in mentality, similar to Xhenovian Pokemon.");
				Pokemon.addTask(Task.TEXT, "Here, can I see your Pokedex for a second?");
				Pokemon.addTask(Task.TEXT, "...");
				Pokemon.addTask(Task.TEXT, "There, I added a 'Shadow Pokedex' section to your Neodex. That way, if you run into any Shadow forms near Shadow Ravine, you can record them!");
				Pokemon.addTask(Task.TEXT, "...What's that? Oh right, I should probably explain how to use the Neodex.");
				Pokemon.addTask(Task.TEXT, "You can see a Pokemon you've registered in the Neodex and their information, like their moves, abilities, typings, and much more!");
				Pokemon.addTask(Task.TEXT, "If you want to toggle which section you're in, just press the 'A' key and it will cycle through all of your sections you have installed!");
				Pokemon.addTask(Task.TEXT, "Oh and one more thing! Oh my Arceus, I can't believe I almost forgot to tell you about my favorite creation yet!");
				Pokemon.addTask(Task.TEXT, "You've always loved battling, so I made this just for you. A state of the line BATTLECALC 3000! Or you could just call it your calculator.");
				Pokemon.addTask(Task.TEXT, "It provides accurate data during a battle, just press 'A' in a battle, or 'Ctrl + A' outside, and you can check how much damage a move can do!");
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.CALCULATOR;
				Pokemon.addTask(Task.TEXT, "Now go out there and make me proud - and most importantly...");
				Pokemon.addTask(Task.TEXT, "COLLECT THAT DATA!");
				gp.aSetter.updateNPC(gp.currentMap);
			} else if (p.flag[0][1] && !p.flag[0][4]) {
				gp.ui.showMessage("There are two Pokemon still inside the machine. Wonder what Dad will do with them?");
			} else if (p.flag[0][4] && p.badges < 1) {
				gp.ui.showMessage("There's still a Pokemon left! Dad must've given one to Scott as well!");
			} else if (p.badges > 1) {
				gp.ui.showMessage("There aren't any Pokemon inside.");
			}
		} else if (gp.currentMap == 51) { // Dad's Mom
			if (!p.flag[0][3]) {
				Pokemon.addTask(Task.TEXT, "Ah, I remember my first time having a Pokemon adventure, before I had your father. I was around your age now actually. Brings back fond memories...");
				Pokemon.addTask(Task.TEXT, "...Oh, you want advice? I believe being a Pokemon trainer isn't about being the strongest, or collecting them all.");
				Pokemon.addTask(Task.TEXT, "It's about forming a life-long bond with your Pokemon, becoming partners. Friendship is key to becoming a better trainer, and a better person.");
				Pokemon.addTask(Task.TEXT, "That's how it was with your grandfather. Back in my day, I wanted to be the best there ever was.");
				Pokemon.addTask(Task.TEXT, "He helped me see the value in friendship, how Pokemon were great companions in life. He's the person who gave me Duchess' dinky old Soothe Bell.");
				Pokemon.addTask(Task.TEXT, "I miss him every day, even with my Pokemon, you and your father by my side.");
				Pokemon.addTask(Task.TEXT, "So, this is my parting gift to you, to show you the power of friendship. I'm so thankful to have been your grandmother.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.SOOTHE_BELL;
				Pokemon.addTask(Task.TEXT, "Go out there and make the world a better place, dear. I know you will.");
				p.flag[0][3] = true;
			}
		} else if (gp.currentMap == 3) {
			Pokemon.addTask(Task.TEXT, "I believe he was looking to introduce himself to you, he mentioned he was heading towards New Minnow Town.");
			Pokemon.addTask(Task.TEXT, "Please do make haste, I do hope he's okay.");
			p.flag[0][4] = true;
		} else if (gp.currentMap == 47 && p.secondStarter != -1) {
			Pokemon.addTask(Task.TEXT, "Here, we breed and house rare Pokemon to right against their extinction.");
			Pokemon.addTask(Task.TEXT, "...What's that? You have a " + Pokemon.getName(((p.starter + 1) * 3) - 2) + "?? That's insanely rare. Did you get that from the professor?");
			Pokemon.addTask(Task.TEXT, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Task t = Pokemon.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		} else if (gp.currentMap == 4) {
			if (p.flag[0][11] && !p.flag[0][12]) {
				Pokemon.addTask(Task.TEXT, "...");
				Pokemon.addTask(Task.TEXT, "What, you've got a package, pipsqueak? Pfft! You can't be older than 10... Didn't know Robin stooped that low.");
				Pokemon.addTask(Task.TEXT, "Last time I checked, the boss wasn't expecting any packages. You can't even get in here anyway, I JUST locked the door and I'm getting the hell outta here.");
				Pokemon.addTask(Task.TEXT, "So yeah, big whoop. Tell that bundle of nerves - A.K.A. your boss - to deal with it. See ya idiot!");
				Pokemon.addTask(Task.UPDATE, "");
				p.flag[0][12] = true;
			}
		} else if (gp.currentMap == 161) {
			if (!p.flag[0][7]) {
				Pokemon.addTask(Task.TEXT, "Sorry, I'm a little busy right now. As the only mailman in this entire region, I've been running ragged delivering letters across Xhenos.");
				Pokemon.addTask(Task.TEXT, "I can't afford to waste any time, even for a gym battle.");
				Pokemon.addTask(Task.TEXT, "Oh? You'll help me? Thank you, thank you, thank you! Maybe then I'll have enough time to open the gym... Here, take these!");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_A;
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_B;
				t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_C;
				p.flag[0][7] = true;
			} else if (p.flag[0][7] && !p.flag[0][11]) {
				if (p.flag[0][8] && p.flag[0][9] && p.flag[0][10]) {
					Pokemon.addTask(Task.TEXT, "Oh it's you, thanks for delivering all those packages! You're a lifesaver, and I truly thank you for being selfless.");
					Pokemon.addTask(Task.TEXT, "But... there's one last package to deliver.");
					Pokemon.addTask(Task.TEXT, "It's been sitting here for weeks for the warehouse, but I can't get in to the building and the owner isn't answering any of my calls.");
					Pokemon.addTask(Task.TEXT, "I've been far too busy to look into it though, could you look into it for me? I've seen some pretty suspicious activity going on there.");
					Pokemon.addTask(Task.TEXT, "Oh, you will? Thank you so much! Don't worry, it's just a short walk away. Once that's done, I'll have a small window where I can open the gym.");
					Task t = Pokemon.addTask(Task.ITEM, "");
					t.item = Item.PACKAGE_D;
					p.flag[0][11] = true;
					Pokemon.addTask(Task.TEXT, "Welp, the clock is ticking, and I need more caffeine.");
				} else {
					Pokemon.addTask(Task.TEXT, "The addresses are on the back of the boxes. You're doing me a massive favor, so once you're done I can give you that battle. So get to it!");
				}
			} else if (p.flag[0][11] && !p.flag[0][15]) {
				Pokemon.addTask(Task.TEXT, "Oh, hello my helper! How's it going at the warehouse? Did you manage to get in okay?");
			} else if (p.flag[0][15]) {
				Pokemon.addTask(Task.TEXT, "Oh, you've delivered my last package? I can't thank you enough, I can finally have a break from all this mail. I'll open the gym up right away, with no delay!");
				Pokemon.addTask(Task.UPDATE, "");
				p.flag[0][16] = true;
			}
		} else if (gp.currentMap == 57) {
			if (p.flag[0][7] && !p.flag[0][9]) {
				Pokemon.addTask(Task.TEXT, "Oh! That's my new frying pan! Thanks squirt, I owe you something.");
				Pokemon.addTask(Task.TEXT, "I got a little trinket I've been wanting to get rid of - I mean pass off - to a worthy kiddo like you. Consider this your appetizer!");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.FLAME_ORB;
				p.bag.remove(Item.PACKAGE_B);
				p.flag[0][9] = true;
			} else {
				Pokemon.addTask(Task.TEXT, "Be sure to check me out in Rawwar city sometime!");
			}
		} else if (gp.currentMap == 58) {
			if (p.flag[0][7] && !p.flag[0][17]) {
				if (!p.flag[0][8]) {
					Pokemon.addTask(Task.TEXT, "...Oh, is that my package?");
					Pokemon.addTask(Task.TEXT, "Thank you so much young man! Sorry for jumping at you, I'm just really unsettled right now.");
					p.bag.remove(Item.PACKAGE_A);
					p.flag[0][8] = true;
				} else {
					Pokemon.addTask(Task.TEXT, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				}
				Pokemon.addTask(Task.TEXT, "Some rampant Pokemon sniffing out berries all burst into my house, but I was just making juice.");
				Pokemon.addTask(Task.TEXT, "In the midst of all the chaos, some crook snuck in and stole my precious item right off my poor Pokemon!");
				Pokemon.addTask(Task.TEXT, "Whatever will I do to find it?");
			} else if (p.flag[0][7] && p.flag[0][17]) {
				p.flag[0][18] = true;
				Pokemon.addTask(Task.TEXT, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Pokemon.addTask(Task.TEXT, "Oh my god, my item! You found it! Wow, you really are impressive!");
				Pokemon.addTask(Task.TEXT, "You know what, since you're so kind, why don't you just keep it? It'll be better off with a stronger trainer like you.");
				Pokemon.addTask(Task.TEXT, "Here, take this for your troubles too. Really, it's the least I can do.");
				Task t = Pokemon.addTask(Task.ITEM, "Obtained a Lucky Egg!");
				t.item = Item.LUCKY_EGG;
			}
		} else if (gp.currentMap == 48) {
			if (!p.flag[0][7]) {
				Pokemon.addTask(Task.TEXT, "Feel free to look around!");
			} else {
				Pokemon.addTask(Task.TEXT, "Oh, you have a package for us? Thank you so much! I'm sure Robin greatly appreciates the help!");
				Pokemon.addTask(Task.TEXT, "Here, have a complimentary gift dog! No really, we insist.");
				
				Random dog = new Random();
				int id = dog.nextInt(3);
				id = 120 + (id * 3);
				Pokemon dogP = new Pokemon(id, 5, true, false);
				Pokemon.addTask(Task.TEXT, "You adopted a gift dog!");
				Task t = Pokemon.addTask(Task.GIFT, "", dogP);
				t.item = Item.SILK_SCARF;
				p.bag.remove(Item.PACKAGE_C);
				p.flag[0][10] = true;
			}
		} else if (gp.currentMap == 8) {
			Pokemon.addTask(Task.TEXT, "And at the worst time too, I was expecting a package all the way from Galar.");
			if (p.flag[0][14] && !p.flag[0][15]) {
				Pokemon.addTask(Task.TEXT, "What's that? You have the package for me? That must be my lucky stapler! Please hand it here, young one.");
				Pokemon.addTask(Task.TEXT, "Robin must be swamped if he's making you do all this delivery work. Is this his version of a gym puzzle?");
				Pokemon.addTask(Task.TEXT, "Heh! I'm just messing with you, you must have volunteered. Here, for your generosity and service.");
				Task t = Pokemon.addTask(Task.ITEM, "");
				t.item = Item.HM01;
				p.bag.remove(Item.PACKAGE_D);
				p.flag[0][15] = true;
			} else if (!p.flag[0][14]) {
				Pokemon.addTask(Task.TEXT, "Can you please help me get rid of these criminals? I don't have any Pokemon of my own, I won't be of much help to you I'm afraid.");
			}
		} else if (gp.currentMap == 13) {
			Pokemon.addTask(Task.TEXT, "Wait, you're his kid? Oh my goodness, your father has told me all about you. Did you come here trying to challenge the gym?");
			Pokemon.addTask(Task.TEXT, "...");
			Pokemon.addTask(Task.TEXT, "You did? Well, that's going to be a difficult task considering the city's gone into full black-out, no power or anything.");
			Pokemon.addTask(Task.TEXT, "I'd try and fix it, but the Control Center door is jammed, and that's our best shot at figuring out what's wrong.");
			Pokemon.addTask(Task.TEXT, "Most of the buildings in this city have electric doors, and they all seem to not be working.");
			Pokemon.addTask(Task.TEXT, "Actually, you know what we could try?");
			Pokemon.addTask(Task.TEXT, "We can grab enough auxillery energy to keep the Control Center door open, and then give it a go from there.");
			Pokemon.addTask(Task.TEXT, "Could you meet me there? It's straight North of this city, up Route 26 and then just a bit East.");
			Pokemon.addTask(Task.TEXT, "I'll go on up ahead to set everything up, please don't keep me waiting!");
			Pokemon.addTask(Task.TEXT, "I'm sure your father would be proud of you for helping out.");
			Pokemon.addTask(Task.FLASH_IN, "");
			Pokemon.addTask(Task.UPDATE, "");
			Pokemon.addTask(Task.FLASH_OUT, "");
			p.flag[1][1] = true;
		}
		
		if (gp.currentMap == 32 && !p.flags[30]) {
			p.flags[30] = true;
			p.fish = true;
			Pokemon.addTask(Task.TEXT, "You got a Fishing Rod!\n(Press 'A' to fish)");
		}
		if (gp.currentMap == 41 && p.flags[8] && p.flags[9] && !p.flags[31]) {
			p.flags[31] = true;
			Pokemon.addTask(Task.TEXT, "Oh you have?! Thank you so much!\nHere, take this as a reward!");
			Pokemon.addTask(Task.TEXT, "Obtained HM04 Surf!");
			p.bag.add(Item.HM04);
		}
		if (gp.currentMap == 46) {
			Pokemon.addTask(Task.HP, "Check your team's Hidden Power types here!");
		}
		if (gp.currentMap == 18 && !p.flags[12]) {
			p.flags[12] = true;
			Random gift = new Random();
			int id = 0;
			int counter = 0;
			do {
				counter++;
				id = gift.nextInt(6); // Dualmoose, Sparkdust, Posho, Kissyfishy, Minishoo, Tinkie
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
				}
			} while (p.pokedex[id] == 2 && counter < 100);
			Pokemon result = new Pokemon(id, 15, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		}
		if (gp.currentMap == 49 && !p.flags[13]) {
			p.flags[13] = true;
			Pokemon.addTask(Task.TEXT, "I encountered this very strong Pokemon, and I don't think I'm strong enough to train it. Here!");
			Random gift = new Random();
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
			Pokemon result = new Pokemon(id, 15, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		}
		if (gp.currentMap == 50 && !p.flags[14]) {
			p.flags[14] = true;
			Pokemon.addTask(Task.TEXT, "Great choice young cracka!!!!");
			Random gift = new Random();
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
				Pokemon.addTask(Task.TEXT, "Wait..... you have that\none?!?!? Shit. Well, take\nthis one instead bozo");
				Random gift2 = new Random();
				boolean sparkitten = gift2.nextBoolean();
				if (sparkitten) {
					id = 108;
				} else {
					id = 190;
				}
			}
			Pokemon result = new Pokemon(id, 25, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
		} if (gp.currentMap == 91 && !p.flags[16]) {
			p.flags[16] = true;
			Pokemon.addTask(Task.TEXT, "Obtained HM05 Slow Fall!");
			p.bag.add(Item.HM05);
			Pokemon.addTask(Task.TEXT, "Also, if you haven't yet, you should be sure to check out the bottom right house in the quadrant above. I hear he has a gift!");
		} if (gp.currentMap == 93) { // Move reminder
			Pokemon.addTask(Task.PARTY, "");
		} if (gp.currentMap == 94 && !p.flags[18]) {
			p.flags[18] = true;
			Pokemon.addTask(Task.TEXT, "One makes Pokemon surge with electricity, and another casts them in a strange shadow.");
			Pokemon.addTask(Task.TEXT, "Here's a gift of one of the Pokemon affected!");
			int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226};
			Random gift = new Random();
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.pokedex[ids[index]] == 2 && counter < 100);
			
			Pokemon result = new Pokemon(ids[index], 30, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
			
		} if (gp.currentMap == 109 && !p.flags[22]) {
			p.flags[22] = true;
			Pokemon.addTask(Task.TEXT, "Here, could you raise it for me?");
			int[] ids = new int[] {177, 179, 98};
			Random gift = new Random();
			int index = gift.nextInt(ids.length - 1);
			if (p.pokedex[ids[0]] == 2 || p.pokedex[ids[1]] == 2) {
				Pokemon.addTask(Task.TEXT, "Oh, you already have a " + Pokemon.getName(ids[index]) + "? I'll give it to someone else to look after then.");
				Pokemon.addTask(Task.TEXT, "Here, take this really rare baby Pokemon instead!");
				index = 2;
			}
			
			Pokemon result = new Pokemon(ids[index], 1, true, false);
			Pokemon.addTask(Task.TEXT, "You recieved " + result.name + "!");
			Pokemon.addTask(Task.GIFT, "", result);
			
		} if (gp.currentMap == 118) {
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
		} else if (p.badges > 1) { // After beating Gym 1 TODO add different dialogue after beating Fred 1 but before Gym 2
			gp.ui.showMessage("There aren't any Pokemon inside.");
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
			if (p.surf || p.lavasurf) image = surf2;
			break;
		case "down":
			if (spriteNum == 1) image = down1;
			if (spriteNum == 2) image = down2;
			if (p.surf || p.lavasurf) image = surf1;
			break;
		case "left":
			if (spriteNum == 1) image = left1;
			if (spriteNum == 2) image = left2;
			if (p.surf || p.lavasurf) image = surf3;
			break;
		case "right":
			if (spriteNum == 1) image = right1;
			if (spriteNum == 2) image = right2;
			if (p.surf || p.lavasurf) image = surf4;
			break;
		}
		g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	}
	
	public Item[] getItems() {
		int available = 8;
	    if (p.badges > 7) available += 2;
	    if (p.badges > 6) available ++;
	    if (p.badges > 3) available += 2;
	    if (p.badges > 2) available += 2;
	    if (p.badges > 1) available ++;
	    if (p.badges > 0) available += 2;
	    Item[] shopItems = new Item[] {Item.REPEL, Item.POKEBALL, Item.POTION, Item.ANTIDOTE, Item.AWAKENING, Item.BURN_HEAL, Item.PARALYZE_HEAL, Item.FREEZE_HEAL, // 0 badges
	    		Item.GREAT_BALL, Item.SUPER_POTION, // 1 badge
	    		Item.ELIXIR, // 2 badges
	    		Item.FULL_HEAL, Item.REVIVE, // 3 badges
	    		Item.ULTRA_BALL, Item.HYPER_POTION, // 4 badges
	    		Item.MAX_POTION, // 7 badges
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
//		} else if (code.equals("R")) {
//			for (Pokemon p : p.team) {
//				if (p != null) {
//					for (int i = 0; i < p.movebank.length; i++) {
//						Node current = p.movebank[i];
//						while (current != null) {
//							Move[] options = Move.values();
//							Random random = new Random();
//							int index = random.nextInt(options.length);
//							current.data = options[index];
//							current = current.next;
//							
//						}
//					}
//				}
//			}
//			SwingUtilities.getWindowAncestor(cheats).dispose();
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
		}
	}
}
