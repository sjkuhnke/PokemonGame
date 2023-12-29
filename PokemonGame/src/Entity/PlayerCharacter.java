package Entity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.VerticalLayout;

import Obj.Cut_Tree;
import Obj.InteractiveTile;
import Obj.Pit;
import Obj.Rock_Climb;
import Obj.Rock_Smash;
import Obj.Tree_Stump;
import Obj.Vine;
import Obj.Vine_Crossable;
import Obj.Whirlpool;
import Overworld.GamePanel;
import Overworld.KeyHandler;
import Overworld.Main;
import Overworld.PMap;
import Swing.Bag.Entry;
import Swing.Battle.JGradientButton;
import Swing.Battle;
import Swing.Item;
import Swing.Move;
import Swing.Moveslot;
import Swing.PType;
import Swing.Player;
import Swing.Pokemon;
import Swing.Pokemon.Node;
import Swing.Status;

public class PlayerCharacter extends Entity {
	
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	public Player p;

	public boolean cross = false;

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
		worldX = gp.tileSize * 79;
		worldY = gp.tileSize * 46;
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
				keyH.pause();
				p.repel = false;
				if (p.bag.contains(0)) {
					int option = JOptionPane.showOptionDialog(null,
							"Repel's effects wore off.\nWould you like to use another?",
				            "Repel",
				            JOptionPane.YES_NO_OPTION,
				            JOptionPane.QUESTION_MESSAGE,
				            null, null, null);
					if (option == JOptionPane.YES_OPTION) {
						useRepel();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Repel's effects wore off.");
				}
				keyH.resume();
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
			PMap.getLoc(gp.currentMap, (int) Math.round(worldX * 1.0 / gp.tileSize), (int) Math.round(worldY * 1.0 / gp.tileSize));
			Main.window.setTitle("Pokemon Game - " + currentMapName);
		}
		if (gp.currentMap == 107 && gp.checkSpin && gp.ticks == 4 && new Random().nextInt(3) == 0) {
			int index = new Random().nextInt(10);
			if (gp.grusts[index] != null) gp.grusts[index].turnRandom();
		}
		
		if (keyH.dPressed) {
			keyH.pause();
			showMenu();
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
					interactNurse();
				} else if (target instanceof NPC_Clerk) {
					interactClerk();
				} else if (target instanceof NPC_Market) {
					interactMarket();
				} else if (target instanceof NPC_Block) {
					interactBlock((NPC_Block) gp.npc[gp.currentMap][npcIndex]);
				} else if (target instanceof NPC_Trainer || target instanceof NPC_TN || target instanceof NPC_Rival || target instanceof NPC_Rival2) {
					gp.startBattle(target.trainer);
				} else if (target instanceof NPC_GymLeader) {
					gp.startBattle(target.trainer);
				} else if (target instanceof NPC_PC) {
					gp.openBox();
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
					keyH.pause();
					int answer = JOptionPane.showConfirmDialog(null, "The water is a deep blue!\nDo you want to Surf?");
					if (answer == JOptionPane.YES_OPTION) {
						switch (direction) {
						case "down":
							worldY += gp.tileSize;
							break;
						case "up":
							worldY -= gp.tileSize;
							break;
						case "left":
							worldX -= gp.tileSize;
							break;
						case "right":
							worldX += gp.tileSize;
							break;
						}
						p.surf = true;
						for (Integer i : gp.tileM.getWaterTiles()) {
							gp.tileM.tile[i].collision = false;
						}
						
					}
					keyH.resume();
				}
			}
			if (p.hasMove(Move.LAVA_SURF) && !p.lavasurf) {
				int result = gp.cChecker.checkTileType(this);
				if (gp.tileM.getLavaTiles().contains(result)) {
					keyH.pause();
					int answer = JOptionPane.showConfirmDialog(null, "The lava is a hot, bubbly red!\nDo you want to Lava Surf?");
					if (answer == JOptionPane.YES_OPTION) {
						switch (direction) {
						case "down":
							worldY += gp.tileSize;
							break;
						case "up":
							worldY -= gp.tileSize;
							break;
						case "left":
							worldX -= gp.tileSize;
							break;
						case "right":
							worldX += gp.tileSize;
							break;
						}
						p.lavasurf = true;
						for (Integer i : gp.tileM.getLavaTiles()) {
							gp.tileM.tile[i].collision = false;
						}
						
					}
					keyH.resume();
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
				}
			}
		}
		if (keyH.aPressed) {
			if (p.fish) {
				int result = gp.cChecker.checkTileType(this);
				if (result == 3 || (result >= 24 && result <= 36) || (result >= 313 && result <= 324)) {
					gp.startWild(gp.currentMap, worldX / gp.tileSize, worldY / gp.tileSize, "Fishing");
				}
			}
		}
	}

	private void pickUpObject(int objIndex) {
		keyH.pause();
		Item item = gp.obj[gp.currentMap][objIndex].item;
		int count = gp.obj[gp.currentMap][objIndex].count;
		p.bag.add(item, count);
		JOptionPane.showMessageDialog(null, "You found " + count + " " + item.toString() + "!");
		gp.player.p.itemsCollected[gp.currentMap][objIndex] = true;
		gp.obj[gp.currentMap][objIndex] = null;
		keyH.resume();
		
	}

	private void showMenu() {
		JPanel menu = new JPanel();
	    menu.setLayout(new GridLayout(6, 1));
	    
	    JButton dex = new JGradientButton("Pokedex");
	    dex.setBackground(new Color(128, 0, 128).darker());
	    JButton party = new JGradientButton("Party");
	    party.setBackground(Color.red.darker());
	    JButton bag = new JGradientButton("Bag");
	    bag.setBackground(Color.blue.darker());
	    JButton save = new JGradientButton("Save");
	    save.setBackground(Color.green.darker());
	    JButton player = new JGradientButton("Player");
	    player.setBackground(Color.yellow.darker());
	    JButton map = new JGradientButton("Map");
	    map.setBackground(new Color(255, 165, 0).darker());
	    
	    
	    dex.addActionListener(e -> {
	    	showDex();
	    });
	    party.addActionListener(e -> {
	    	showParty();
	    });
	    bag.addActionListener(e -> {
	    	showBag();
	    });
	    save.addActionListener(e -> {
	    	saveGame();
	    });
	    player.addActionListener(e -> {
	    	JPanel playerInfo = new JPanel();
	    	playerInfo.setLayout(new GridLayout(6, 1));
	    	
	    	JLabel moneyLabel = new JLabel();
	    	moneyLabel.setText("$" + p.money);
	    	JLabel badgesLabel = new JLabel();
	    	badgesLabel.setText(p.badges + " Badges");
	    	
	    	JTextField cheats = new JTextField();
	    	cheats.addActionListener(f -> {
	    		String code = cheats.getText();
	    		
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
	    		} else if (code.equals("R")) {
	    			for (Pokemon p : p.team) {
	    				if (p != null) {
	    					for (int i = 0; i < p.movebank.length; i++) {
	    						Node current = p.movebank[i];
	    						while (current != null) {
	    							Move[] options = Move.values();
	    							Random random = new Random();
	    							int index = random.nextInt(options.length);
	    							current.data = options[index];
	    							current = current.next;
	    							
	    						}
	    					}
	    				}
	    			}
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
	    			p.money = 1000000;
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
	    		} else if (code.startsWith("ben")) {
	    			JPanel pPanel = new JPanel();
	    			pPanel.setLayout(new GridLayout(6, 1));

	        	    for (int j = 0; j < 6; j++) {
	        	    	JButton partyB = setUpPartyButton(j);
	        	        final int index = j;
	        	        
	        	        partyB.addActionListener(g -> {
	        	        	JOptionPane.showMessageDialog(null, p.team[index].nickname + " has hit " + p.team[index].headbuttCrit + " headbutt crit(s).");
	        	        });
	        	        
	        	        JPanel mPanel = new JPanel(new BorderLayout());
	        	        mPanel.add(partyB, BorderLayout.NORTH);
	        	        pPanel.add(mPanel);
	        	        
	        	    }
	        	    
	        	    JOptionPane.showMessageDialog(null, pPanel, "Party", JOptionPane.PLAIN_MESSAGE);
	    		} else if (code.equals("UPDATE")) {
	    			p.updateTrainers();
	    			p.updateItems(gp.obj.length, gp.obj[1].length);
	    			p.updateFlags();
	    			for (Pokemon p : p.team) {
	    				if (p != null) {
	    					p.setBaseStats();
	    					p.setAbility(p.abilitySlot);
	    					p.setMoveBank();
	    				}
	    			}
	    			for (Pokemon p : p.box1) {
	    				if (p != null) {
	    					p.setBaseStats();
	    					p.setAbility(p.abilitySlot);
	    					p.setMoveBank();
	    				}
	    				
	    			}
	    			for (Pokemon p : p.box2) {
	    				if (p != null) {
	    					p.setBaseStats();
	    					p.setAbility(p.abilitySlot);
	    					p.setMoveBank();
	    				}
	    			}
	    			for (Pokemon p : p.box3) {
	    				if (p != null) {
	    					p.setBaseStats();
	    					p.setAbility(p.abilitySlot);
	    					p.setMoveBank();
	    				}
	    			}
	        	    
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
	    			p.trainersBeat = new boolean[Main.trainers.length];
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
	    		}
	    	});
	    	
	    	playerInfo.add(moneyLabel);
	    	playerInfo.add(badgesLabel);
	    	playerInfo.add(cheats);
	    	
	    	JOptionPane.showMessageDialog(null, playerInfo, "Player Info", JOptionPane.PLAIN_MESSAGE);
	    });
	    map.addActionListener(e -> {
	    	SwingUtilities.getWindowAncestor(menu).dispose();
	    	gp.openMap();
	    	
	    });
	    
	    menu.add(dex);
	    menu.add(party);
	    menu.add(bag);
	    menu.add(save);
	    menu.add(player);
	    menu.add(map);
	    
	    JOptionPane.showMessageDialog(null, menu, "Menu", JOptionPane.PLAIN_MESSAGE);
	    if (!gp.mapOpen) keyH.resume();
	}

	private void saveGame() {
		keyH.pause();
		int option = JOptionPane.showOptionDialog(null,
				"Would you like to save the game?",
				"Confirm Save",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE,
	            null, null, null);
	    if (option == JOptionPane.YES_OPTION) {
	    	// Check if the directory exists, create it if not
            Path savesDirectory = Paths.get("./saves/");
            if (!Files.exists(savesDirectory)) {
                try {
					Files.createDirectories(savesDirectory);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
	    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./saves/" + currentSave))) {
            	gp.player.p.setPosX(gp.player.worldX);
            	gp.player.p.setPosY(gp.player.worldY);
            	gp.player.p.currentMap = gp.currentMap;
                oos.writeObject(gp.player.p);
                oos.close();
                JOptionPane.showMessageDialog(null, "Game saved sucessfully!");
            } catch (IOException ex) {
            	JOptionPane.showMessageDialog(null, "Error writing object to file: " + ex.getMessage());
            }
	    }
	    keyH.resume();
	}

	private void interactNurse() {
		if (keyH.wPressed) {
			keyH.pause();
			int option = JOptionPane.showOptionDialog(null,
					"Welcome to the Pokemon Center!\nDo you want to rest your Pokemon?",
					"Heal",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
		    if (option == JOptionPane.YES_OPTION) {
		    	for (Pokemon member : p.team) {
					if (member != null) member.heal();
				}
		    	JOptionPane.showMessageDialog(null, "Your Pokemon were healed to full health!");
		    	// NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
		    	if (gp.currentMap == 1) p.locations[1] = true;
		    	if (gp.currentMap == 5) p.locations[2] = true;
		    	if (gp.currentMap == 19) p.locations[3] = true;
		    	if (gp.currentMap == 29) p.locations[4] = true;
		    	if (gp.currentMap == 39) p.locations[6] = true;
		    	if (gp.currentMap == 86) p.locations[8] = true;
		    	if (gp.currentMap == 92) p.locations[5] = true;
		    	if (gp.currentMap == 111) p.locations[7] = true;
		    }
		    keyH.resume();
		}
	}
	
	private void interactClerk() {
		keyH.pause();
		
		JPanel shopPanel = new JPanel();
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
	    shopPanel.setLayout(new GridLayout((available + 1) / 2, 2));
	    for (int i = 0; i < available; i++) {
	    	JButton item = new JButton(shopItems[i].toString() + ": $" + shopItems[i].getCost());
	    	int index = i;
	    	item.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) { // Right-click
			    		if (p.buy(shopItems[index], 5)) {
		    	            JOptionPane.showMessageDialog(null, "Purchased 5 " + shopItems[index].toString() + " for $" + shopItems[index].getCost() * 5);
		    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
		    	            interactClerk();
		    	        } else {
		    	            JOptionPane.showMessageDialog(null, "Not enough money!");
		    	        }
		    	    } else { // Left-click
		    	    	if (p.buy(shopItems[index])) {
		    	            JOptionPane.showMessageDialog(null, "Purchased 1 " + shopItems[index].toString() + " for $" + shopItems[index].getCost());
		    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
		    	            interactClerk();
		    	        } else {
		    	            JOptionPane.showMessageDialog(null, "Not enough money!");
		    	        }
		    	    }
			    }
	    	});
	    	shopPanel.add(item);
	    }
		JOptionPane.showMessageDialog(null, shopPanel, "Money: $" + p.money, JOptionPane.PLAIN_MESSAGE);
		keyH.resume();
	}
	
	private void interactBlock(NPC_Block npc) {
		if (keyH.wPressed) {
			keyH.pause();
			if (!p.fish || gp.currentMap != 32) JOptionPane.showMessageDialog(null, npc.message);
			if (gp.currentMap == 32 && !p.fish) {
				p.fish = true;
				JOptionPane.showMessageDialog(null, "You got a Fishing Rod!\n(Press 'A' to fish)");			}
			if (npc.more) {
				if (!p.flags[6] && gp.currentMap == 43) {
					JPanel partyPanel = new JPanel();
					partyPanel.setLayout(new GridLayout(6, 1));
					
					for (int j = 0; j < 6; j++) {
						JButton party = setUpPartyButton(j);
						final int index = j;
						party.addActionListener(e -> {
							if (p.team[index].type1 == PType.GROUND || p.team[index].type2 == PType.GROUND) {
								p.flags[6] = true;
								JOptionPane.showMessageDialog(null, "Thank you! Here, take this as a reward!");
								JOptionPane.showMessageDialog(null, "Obtained A Key!\nGot 1 Valiant Gem!");
								p.bag.add(Item.VALIANT_GEM);
							} else {
								JOptionPane.showMessageDialog(null, "That's not a GROUND type!");
							}
							SwingUtilities.getWindowAncestor(partyPanel).dispose();
						});
						partyPanel.add(party);
					}
					JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
				}
				if (!p.flags[7] && gp.currentMap == 38) {
					JPanel partyPanel = new JPanel();
					partyPanel.setLayout(new GridLayout(6, 1));
					
					for (int j = 0; j < 6; j++) {
						JButton party = setUpPartyButton(j);
						final int index = j;
						party.addActionListener(e -> {
							if (p.team[index].type1 == PType.ICE || p.team[index].type2 == PType.ICE) {
								p.flags[7] = true;
								JOptionPane.showMessageDialog(null, "Thank you! Here, take this as a reward!");
								JOptionPane.showMessageDialog(null, "Obtained B Key!\nGot 1 Petticoat Gem!");
								p.bag.add(Item.PETTICOAT_GEM);
							} else {
								JOptionPane.showMessageDialog(null, "That's not an ICE type!");
							}
							SwingUtilities.getWindowAncestor(partyPanel).dispose();
						});
						partyPanel.add(party);
					}
					JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
				}
				if (p.flags[8] && p.flags[9] && gp.currentMap == 41 && !p.bag.contains(96)) {
					JOptionPane.showMessageDialog(null, "Oh you have?! Thank you so much!\nHere, take this as a reward!\n\nObtained HM04 Surf!");
					p.bag.add(Item.HM04);
				}
				if (gp.currentMap == 46) {
					String message = "";
	    			for (Pokemon p : p.team) {
	    				if (p != null) {
	    	    			message += p.nickname;
	    	    			if (p.nickname != p.name) message += (" (" + p.name + ")");
	    	    			message += " : ";
	    	    			message += p.determineHPType();
	    	    			message += "\n";
	    				}
	    			}
					JOptionPane.showMessageDialog(null, message, "Party Hidden Power Types", JOptionPane.PLAIN_MESSAGE);
				}
				if (gp.currentMap == 48 && !p.flags[11]) {
					Random dog = new Random();
					int id = dog.nextInt(3);
					id = 120 + (id * 3);
					p.flags[11] = true;
					JOptionPane.showMessageDialog(null, "You adopted a gift dog!");
					Pokemon dogP = new Pokemon(id, 5, true, false);
					dogP.item = Item.SILK_SCARF;
					p.catchPokemon(dogP);
				}
				if (gp.currentMap == 47 && !p.flags[10]) {
					Random starter = new Random();
					int id = starter.nextInt(3);
					while (id + 1 == p.starter) {
						id = starter.nextInt(3);
					}
					id *= 3;
					id += 1;
					Item item = null;
					switch (id) {
					case 1:
						item = Item.MIRACLE_SEED;
						break;
					case 4:
						item = Item.CHARCOAL;
						break;
					case 7:
						item = Item.MYSTIC_WATER;
						break;
					}
					Pokemon result = new Pokemon(id, 5, true, false);
					result.item = item;
					p.flags[10] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				}
				if (gp.currentMap == 18 && !p.flags[12]) {
					Random gift = new Random();
					int id = 0;
					do {
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
					} while (p.pokedex[id] == 2);
					Pokemon result = new Pokemon(id, 15, true, false);
					p.flags[12] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				}
				if (gp.currentMap == 49 && !p.flags[13]) {
					JOptionPane.showMessageDialog(null, "I encountered this very\nstrong Pokemon, and I\ndon't think I'm strong\nenough to train it. Here!");
					Random gift = new Random();
					int id;
					do {
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
					} while (p.pokedex[id] == 2);
					Pokemon result = new Pokemon(id, 15, true, false);
					p.flags[13] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				}
				if (gp.currentMap == 50 && !p.flags[14]) {
					JOptionPane.showMessageDialog(null, "Great choice young cracka!!!!");
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
						JOptionPane.showMessageDialog(null, "Wait..... you have that\none?!?!? Shit. Well, take\nthis one instead bozo");
						Random gift2 = new Random();
						boolean sparkitten = gift2.nextBoolean();
						if (sparkitten) {
							id = 108;
						} else {
							id = 190;
						}
					}
					Pokemon result = new Pokemon(id, 25, true, false);
					p.flags[14] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				} if (gp.currentMap == 91 && !p.flags[16]) {
					JOptionPane.showMessageDialog(null, "Obtained HM05 Slow Fall!");
					p.bag.add(Item.HM05);
					JOptionPane.showMessageDialog(null, "Also, if you haven't yet, you should\nbe sure to check out the bottom right\nhouse in the quadrant above.\nI hear he has a gift!");
					p.flags[16] = true;
				} if (gp.currentMap == 93) {
					JPanel partyPanel = new JPanel();
					partyPanel.setLayout(new GridLayout(6, 1));
					for (int j = 0; j < 6; j++) {
						JButton party = setUpPartyButton(j);
						final int index = j;
						party.addActionListener(e -> {
							ArrayList<Move> forgottenMoves = new ArrayList<>();
		                    for (int i = 0; i < p.team[index].getLevel(); i++) {
		                    	if (i < p.team[index].movebank.length) {
		                    		Node move = p.team[index].movebank[i];
		                    		while (move != null) {
		                    			if (!p.team[index].knowsMove(move.data)) {
		                    				forgottenMoves.add(move.data);
		                    			}
		                    			move = move.next;
		                    		}
		                    	}
		                    }
		                    if (forgottenMoves.isEmpty()) {
		                        JOptionPane.showMessageDialog(null, "This Pokemon has not forgotten any moves.");
		                    } else {
		                    	JPanel moves = new JPanel();
		                    	moves.setLayout(new GridLayout(0, 1));
		                        for (Move move : forgottenMoves) {
		                            JGradientButton moveButton = new JGradientButton(move.toString());
		                            moveButton.setBackground(move.mtype.getColor());
		                            moveButton.addMouseListener(new MouseAdapter() {
		                			    @Override
		                			    public void mouseClicked(MouseEvent e) {
		                			    	if (SwingUtilities.isRightMouseButton(e)) {
		                			            JOptionPane.showMessageDialog(null, move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
		                			        } else {
		                			        	int choice = p.team[index].displayMoveOptions(move);
					        	                if (choice == JOptionPane.CLOSED_OPTION) {
					        	                	JOptionPane.showMessageDialog(null, p.team[index].nickname + " did not learn " + move + ".");
					        	                } else {
					        	                	JOptionPane.showMessageDialog(null, p.team[index].nickname + " has learned " + move + " and forgot " + p.team[index].moveset[choice].move + "!");
					        	                	p.team[index].moveset[choice] = new Moveslot(move);
					        	                }
					        	                SwingUtilities.getWindowAncestor(moves).dispose();
		                			        }
		                			    }
		                			});
		                            moves.add(moveButton);
		                        }
		                        JOptionPane.showMessageDialog(null, moves, "Teach a Move?", JOptionPane.QUESTION_MESSAGE);
		                    }
						});
						partyPanel.add(party);
					}
					JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
				} if (gp.currentMap == 94 && !p.flags[18]) {
					JOptionPane.showMessageDialog(null, "Here's a gift of one\nof the Pokemon affected!");
					int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226};
					Random gift = new Random();
					int index = -1;
					do {
						index = gift.nextInt(ids.length);
					} while (p.pokedex[ids[index]] == 2);
					
					Pokemon result = new Pokemon(ids[index], 30, true, false);
					p.flags[18] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
					
				} if (gp.currentMap == 109 && !p.flags[22]) {
					JOptionPane.showMessageDialog(null, "Here, could you raise it for me?");
					int[] ids = new int[] {177, 179, 98};
					Random gift = new Random();
					int index = gift.nextInt(ids.length - 1);
					if (p.pokedex[ids[0]] == 2 || p.pokedex[ids[1]] == 2) {
						Pokemon temp = new Pokemon(5, 5, false, false);
						JOptionPane.showMessageDialog(null, "Oh, you already have a\n" + temp.getName(ids[index]) + "? Well, take this really\nrare Pokemon instead!");
						index = 2;
					}
					
					Pokemon result = new Pokemon(ids[index], 1, true, false);
					p.flags[22] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
					
				} if (gp.currentMap == 118) {
					JOptionPane.showMessageDialog(null, "Do you have any fossils for me to resurrect?");
					JPanel options = new JPanel();
					boolean valid = false;
					if (p.bag.contains(45)) {
						JGradientButton button = new JGradientButton(p.bag.bag[45].toString());
						button.setBackground(p.bag.bag[45].getColor());
						button.addActionListener(e -> {
							int answer = JOptionPane.showOptionDialog(null,
									"Would you like to revive a Shockfang?",
						            "Revive Fossil?",
						            JOptionPane.YES_NO_OPTION,
						            JOptionPane.QUESTION_MESSAGE,
						            null, null, null);
							if (answer == JOptionPane.YES_OPTION) {
								p.catchPokemon(new Pokemon(211, 20, true, false));
								p.bag.remove(Item.THUNDER_SCALES_FOSSIL);
								SwingUtilities.getWindowAncestor(options).dispose();
							}
						});
						valid = true;
						options.add(button);
					}
					
					if (p.bag.contains(46)) {
						JGradientButton button = new JGradientButton(p.bag.bag[46].toString());
						button.setBackground(p.bag.bag[46].getColor());
						button.addActionListener(e -> {
							int answer = JOptionPane.showOptionDialog(null,
									"Would you like to revive a Nightrex?",
						            "Revive Fossil?",
						            JOptionPane.YES_NO_OPTION,
						            JOptionPane.QUESTION_MESSAGE,
						            null, null, null);
							if (answer == JOptionPane.YES_OPTION) {
								p.catchPokemon(new Pokemon(213, 20, true, false));
								p.bag.remove(Item.DUSK_SCALES_FOSSIL);
								SwingUtilities.getWindowAncestor(options).dispose();
							}
						});
						valid = true;
						options.add(button);
					}
					
					if (valid) {
						JOptionPane.showMessageDialog(null, options, "Revive a fossil?", JOptionPane.QUESTION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "You don't have any fossils to revive!");
					}
				}
			}
		    keyH.resume();
		}
	}

	private void showParty() {
	    JPanel partyPanel = new JPanel();
	    partyPanel.setLayout(new GridLayout(6, 1));

	    for (int j = 0; j < 6; j++) {
	    	JButton party = setUpPartyButton(j);
	    	JProgressBar partyHP = setupHPBar(j);
	        final int index = j;
	        
	        party.addActionListener(e -> {
	            JPanel teamMemberPanel = p.team[index].showSummary(p, true);
	            JButton swapButton = new JButton("Swap");
		        swapButton.addActionListener(f -> {
		            if (p.team[index] != null && p.team[index] != p.current) {
		                p.swap(p.team[index], index); // Call the swap method in the Player class
		                JOptionPane.getRootFrame().dispose();
		                showParty(); // Update the party display after swapping
		            }
		        });
	            if (p.team[index] != p.current && !p.team[index].isFainted()) teamMemberPanel.add(swapButton);
	            JOptionPane.showMessageDialog(null, teamMemberPanel, "Party member details", JOptionPane.PLAIN_MESSAGE);
	        });

	        JPanel memberPanel = new JPanel(new BorderLayout());
	        memberPanel.add(party, BorderLayout.NORTH);
	        memberPanel.add(partyHP, BorderLayout.SOUTH);
	        partyPanel.add(memberPanel);
	    }

	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
	    keyH.resume();
	}
	
	private void showBag() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel medicinePanel = createPocketPanel(p, Item.MEDICINE);
		JPanel heldPanel = createPocketPanel(p, Item.HELD_ITEM);
		JPanel otherPanel = createPocketPanel(p, Item.OTHER);
		JPanel tmsPanel = createPocketPanel(p, Item.TMS);
		JPanel berryPanel = createPocketPanel(p, Item.BERRY);
		
		tabbedPane.addTab("Medicine", medicinePanel);
		tabbedPane.addTab("Held Items", heldPanel);
		tabbedPane.addTab("Other", otherPanel);
		tabbedPane.addTab("TMs", tmsPanel);
		tabbedPane.addTab("Berries", berryPanel);
		
		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JOptionPane.showMessageDialog(null, mainPanel, "Bag", JOptionPane.PLAIN_MESSAGE);
		keyH.resume();
	}
	
	private JPanel createPocketPanel(Player p, int pocket) {
		JPanel panel = new JPanel();
		panel.setLayout(new VerticalLayout());
		ArrayList<Entry> bag = p.getBag().getItems();
		for (Entry i : bag) {
			if (i.getItem().getPocket() == pocket) {
				JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				JButton item = new JGradientButton("");
			    String text = i.getItem().getMove() == null ? i.getItem().toString() + " x " + i.getCount() : i.getItem().toString();
			    item.setText(text);
			    
			    item.setPreferredSize(new Dimension(175, item.getPreferredSize().height));

			    item.addActionListener(e -> {
			        JPanel itemDesc = new JPanel();
			        itemDesc.setLayout(new BoxLayout(itemDesc, BoxLayout.Y_AXIS));
			        JLabel description = new JLabel(i.getItem().toString());
			        description.setFont(new Font(description.getFont().getName(), Font.BOLD, 16));
			        JGradientButton moveButton = null;
			        if (i.getItem().getMove() != null) {
			        	moveButton = new JGradientButton(i.getItem().getMove().toString());
			        	moveButton.setBackground(i.getItem().getMove().mtype.getColor());
		                moveButton.addActionListener(f -> {
				            JOptionPane.showMessageDialog(null, i.getItem().getMove().getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
		                });
			        }
			        JLabel count = new JLabel("Count: " + i.getCount());
			        count.setFont(new Font(count.getFont().getName(), Font.ITALIC, 16));
			        String desc = i.getItem().getDesc();
			        desc = desc.replace("\n", "<br>");
			        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
			        
			        JButton useButton = new JButton("Use");
			        useButton.addActionListener(f -> {
			        	
			        	// POTIONS
			        	if (i.getItem().isPotion()) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));

			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	    	JProgressBar partyHP = setupHPBar(j);
			        	        final int index = j;

			        	        party.addActionListener(g -> {
			        	        	if (p.team[index].currentHP == p.team[index].getStat(0) || p.team[index].isFainted()) {
			        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        	        	} else {
			        	        		int difference = 0;
			        	        		if (i.getItem().getHealAmount() > 0) {
			        	        			difference = Math.min(i.getItem().getHealAmount(), p.team[index].getStat(0) - p.team[index].currentHP);
			        	        			p.team[index].currentHP += i.getItem().getHealAmount();
			        	        		} else {
			        	        			difference = p.team[index].getStat(0) - p.team[index].currentHP;
			        	        			p.team[index].currentHP = p.team[index].getStat(0);
			        	        			if (i.getItem().getID() == 8) p.team[index].status = Status.HEALTHY;
			        	        		}
				        	        	p.team[index].verifyHP();
				        	        	p.bag.remove(i.getItem());
				        	        	JOptionPane.showMessageDialog(null, p.team[index].nickname + " was healed by " + difference + " HP");
				        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        	        	showBag();
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
			        	        partyPanel.add(memberPanel);
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// STATUS HEALERS
			        	else if (i.getItem().isStatusHealer()) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    Status target = i.getItem().getStatus();

			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	    	if (p.team[j] != null && p.team[j].status == Status.HEALTHY) party.setBackground(Color.green);
			        	    	if (p.team[j] != null && p.team[j].status != Status.HEALTHY) party.setBackground(p.team[j].status.getColor());
			        	    	if (p.team[j] != null && p.team[j].isFainted()) party.setBackground(Color.red);
			        	    	JProgressBar partyHP = setupHPBar(j);
			        	        final int index = j;
			        	        Status finalTarget = target == null && p.team[index] != null && p.team[index].status != Status.HEALTHY ? p.team[index].status : target;
			        	        final Status fFinalTarget = p.team[index] != null && p.team[index].status == Status.TOXIC && target == Status.POISONED ? Status.TOXIC : finalTarget;		        	        

			        	        party.addActionListener(g -> {
			        	        	if (p.team[index].status != fFinalTarget || p.team[index].isFainted()) {
			        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        	        	} else {
			        	        		Status temp = p.team[index].status;
			        	        		p.team[index].status = Status.HEALTHY;
				        	        	p.bag.remove(i.getItem());
				        	        	JOptionPane.showMessageDialog(null, p.team[index].nickname + " was cured of its " + temp.getName());
				        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        	        	showBag();
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
			        	        partyPanel.add(memberPanel);
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// REVIVES
			        	else if (i.getItem() == Item.REVIVE || i.getItem() == Item.MAX_REVIVE) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));

			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	    	JProgressBar partyHP = setupHPBar(j);
			        	        final int index = j;

			        	        party.addActionListener(g -> {
			        	        	if (!p.team[index].isFainted()) {
			        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        	        	} else {
			        	        		p.team[index].fainted = false;
			        	        		if (i.getItem().getID() == 16) {
			        	        			p.team[index].currentHP = p.team[index].getStat(0) / 2;
			        	        		} else {
			        	        			p.team[index].currentHP = p.team[index].getStat(0);
			        	        		}
				        	        	p.bag.remove(i.getItem());
				        	        	JOptionPane.showMessageDialog(null, p.team[index].nickname + " was revived!");
				        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        	        	showBag();
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
			        	        partyPanel.add(memberPanel);
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// RARE CANDY
			        	else if (i.getItem() == Item.RARE_CANDY) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));

			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	    	JProgressBar partyHP = setupHPBar(j);
			        	        final int index = j;
			        	        
			        	        party.addMouseListener(new MouseAdapter() {
			        			    @Override
			        			    public void mouseClicked(MouseEvent e) {
			        			    	if (SwingUtilities.isRightMouseButton(e)) { // Right-click
			        			    		if (p.team[index].getLevel() == 100) {
					        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        			    		} else {
			        			    			String input = JOptionPane.showInputDialog("Enter the number of Rare Candies to use:");
			        			                if (input != null) {
			        			                	int numCandies = 0;
			        			                	try {
			        			                		numCandies = Integer.parseInt(input);
			        			                	} catch (NumberFormatException n) {
			        			                		numCandies = 1;
			        			                	}
			        			                    
			        			                    useRareCandies(p.team[index], numCandies, i.getItem());
			        			                    SwingUtilities.getWindowAncestor(partyPanel).dispose();
							        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
							        	        	SwingUtilities.getWindowAncestor(panel).dispose();
							        	        	showBag();
			        			                }
			        			    		}
			        			    		
			        		    	    } else { // Left-click
			        		    	    	if (p.team[index].getLevel() == 100) {
					        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        		    	    	} else {
			        		    	    		p.elevate(p.team[index]);
					        	        		p.bag.remove(i.getItem());
					        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
						        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
						        	        	SwingUtilities.getWindowAncestor(panel).dispose();
						        	        	showBag();
			        		    	    	}
			        			    	}
			        			    }
			        	        });
			        	    	
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
			        	        partyPanel.add(memberPanel);
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// EUPHORIAN GEM
			        	else if (i.getItem() == Item.EUPHORIAN_GEM) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));

			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	    	JProgressBar partyHP = setupHPBar(j);
			        	        final int index = j;
			        	        
			        	        party.addActionListener(g -> {
			        	        	if (p.team[index].happiness >= 255) {
			        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        	        	} else {
			        	        		p.team[index].awardHappiness(100, true);
			        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + " looked happier!");
			        	        		p.bag.remove(i.getItem());
			        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        	        	showBag();
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
			        	        partyPanel.add(memberPanel);
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// EVOLUTION ITEMS
			        	else if (i.getItem().isEvoItem()) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	if (p.team[j] != null) {
			        	    		JButton party = setUpPartyButton(j);
				        	    	JProgressBar partyHP = setupHPBar(j);
				        	        final int index = j;
				        	        final boolean eligible = i.getItem().getEligible(p.team[j].id);
				        	        if (eligible) {
				        	        	party.setBackground(Color.GREEN);
				        	        } else {
				        	        	party.setBackground(Color.RED);
				        	        }

				        	        party.addActionListener(g -> {
				        	        	if (!eligible) {
				        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
				        	        	} else {
				        	        		boolean shouldEvolve = Battle.displayEvolution(p.team[index]);
				        	    			if (shouldEvolve) {
				        	    				Pokemon result = new Pokemon(p.team[index].getEvolved(i.getItem().getID()), p.team[index]);
				        	    		        int hpDif = p.team[index].getStat(0) - p.team[index].currentHP;
				        	    		        result.currentHP -= hpDif;
				        	    		        result.moveMultiplier = p.team[index].moveMultiplier;
				        	    		        JOptionPane.showMessageDialog(null, p.team[index].nickname + " evolved into " + result.name + "!");
				        	    		        result.exp = p.team[index].exp;
				        	    		        p.team[index] = result;
				        	    		        if (index == 0) p.current = result;
				        	                    result.checkMove();
				        	                    p.pokedex[result.id] = 2;
				        	    		        p.bag.remove(i.getItem());
				        	    			} else {
				        	    				JOptionPane.showMessageDialog(null, p.team[index].nickname + " did not evolve.");
				        	    			}
					        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        	SwingUtilities.getWindowAncestor(panel).dispose();
					        	        	showBag();
				        	        	}
				        	        });
				        	        
				        	        JPanel memberPanel = new JPanel(new BorderLayout());
				        	        memberPanel.add(party, BorderLayout.NORTH);
				        	        memberPanel.add(partyHP, BorderLayout.SOUTH);
				        	        partyPanel.add(memberPanel);
			        	    	}
			        	    }

			        	    JOptionPane.showMessageDialog(null, partyPanel, "Party", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// REPEL
			        	else if (i.getItem() == Item.REPEL) {
			        		if (useRepel()) {
			        			SwingUtilities.getWindowAncestor(itemDesc).dispose();
			        			SwingUtilities.getWindowAncestor(panel).dispose();
				            	showBag();
			        		}
			        	}
			        	
			        	// TMS/HMS
			        	else if (i.getItem().isTM()) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        boolean learnable = p.team[index] != null ? i.getItem().getLearned(p.team[index]) : false;
			        	        boolean learned = p.team[index] != null ? p.team[index].knowsMove(i.getItem().getMove()) : false;
			        	        if (!learnable) {
			        	        	party.setBackground(Color.RED.darker());
			        	        } else if (learned) {
			        	        	party.setBackground(Color.YELLOW);
			        	        } else {
			        	        	party.setBackground(Color.GREEN);
			        	        }
			        	        
			        	        party.addActionListener(g -> {
			        	        	if (!learnable) {
			        	        		JOptionPane.showMessageDialog(null, "" + p.team[index].nickname + " can't learn " + i.getItem().getMove() + "!");
			        	        	} else if (learned) {
			        	        		JOptionPane.showMessageDialog(null, "" + p.team[index].nickname + " already knows " + i.getItem().getMove() + "!");
			        	        	} else {
			        	        		boolean learnedMove = false;
			        		            for (int k = 0; k < 4; k++) {
			        		                if (p.team[index].moveset[k] == null) {
			        		                	p.team[index].moveset[k] = new Moveslot(i.getItem().getMove());
			        		                	JOptionPane.showMessageDialog(null, p.team[index].nickname + " learned " + i.getItem().getMove() + "!");
			        		                    learnedMove = true;
			        		                    break;
			        		                }
			        		            }
			        		            if (!learnedMove) {
			        		            	int choice = p.team[index].displayMoveOptions(i.getItem().getMove());
				        	                if (choice == JOptionPane.CLOSED_OPTION) {
				        	                	JOptionPane.showMessageDialog(null, p.team[index].nickname + " did not learn " + i.getItem().getMove() + ".");
				        	                } else {
				        	                	JOptionPane.showMessageDialog(null, p.team[index].nickname + " has learned " + i.getItem().getMove().toString() + " and forgot " + p.team[index].moveset[choice].move + "!");
				        	                	p.team[index].moveset[choice] = new Moveslot(i.getItem().getMove());
				        	                }
			        		            }
			        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        	        	showBag();
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Teach " + i.getItem().getMove() + "?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	// Ability Capsule
			        	else if (i.getItem() == Item.ABILITY_CAPSULE) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        if (p.team[index] != null) {
				        	        Pokemon test = new Pokemon(p.team[index].id, 1, false, false);
				        	        test.setAbility(1 - p.team[index].abilitySlot);
				        	        boolean swappable = p.team[index].ability != test.ability;
				        	        if (swappable) {
				        	        	party.setBackground(Color.GREEN);
				        	        } else {
				        	        	party.setBackground(Color.YELLOW);
				        	        }
				        	        
				        	        party.addActionListener(g -> {
				        	        	if (!swappable) {
				        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
				        	        	} else {
				        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s ability was swapped from " + p.team[index].ability + " to " + test.ability + "!");
				        	        		p.team[index].abilitySlot = 1 - p.team[index].abilitySlot;
				        	        		p.team[index].setAbility(p.team[index].abilitySlot);
				        	        		p.bag.remove(i.getItem());
				        	        		
				        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        	SwingUtilities.getWindowAncestor(panel).dispose();
					        	        	showBag();
				        	        	}
				        	        });
			        	        }
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Swap Abilities?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// Bottle Caps
			        	else if (i.getItem() == Item.BOTTLE_CAP || i.getItem() == Item.GOLD_BOTTLE_CAP) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        if (p.team[index] != null) {
				        	        
				        	        party.addActionListener(g -> {
				        	        	if (i.getItem().getID() == 27) {
				        	        		JPanel ivPanel = new JPanel();
				        	        		ivPanel.setLayout(new GridLayout(6, 1));
				        	        		int[] ivs = p.team[index].getIVs();
				        	        		for (int k = 0; k < 6; k++) {
				        	        			final int kndex = k;
				        	        			JButton iv = new JButton();
			        	        	        	String type = p.team[index].getStatType(k);
				        	        			iv.setText(type + ": " + ivs[k]);
				        	        			final String finalType = type;
				        	        			
				        	        			iv.addActionListener(h -> {
				        	        				JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s " + finalType + "IV was maxed out!");
				        	        				p.team[index].ivs[kndex] = 31;
				        	        				p.team[index].stats = p.team[index].getStats();
				        	        				p.bag.remove(i.getItem());
						        	        		
				        	        				SwingUtilities.getWindowAncestor(ivPanel).dispose();
						        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
							        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
							        	        	SwingUtilities.getWindowAncestor(panel).dispose();
							        	        	showBag();
				        	        			});
				        	        			
				        	        			ivPanel.add(iv);
				        	        		}
				        	        		JOptionPane.showMessageDialog(null, ivPanel, "Which IV?", JOptionPane.PLAIN_MESSAGE);
				        	        	} else {
				        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s IVs were maxed out!");
				        	        		p.team[index].ivs = new int[] {31, 31, 31, 31, 31, 31};
				        	        		p.team[index].stats = p.team[index].getStats();
		        	        				p.bag.remove(i.getItem());
				        	        		
				        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        	SwingUtilities.getWindowAncestor(panel).dispose();
					        	        	showBag();
				        	        	}
			        	        		
				        	        });
			        	        }
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Change IVs?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// Nature Mints
			        	else if (i.getItem().isMint()) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        if (p.team[index] != null) {
			        	        
			        	        	party.addActionListener(h -> {
			        	        		double nature[];
				        	        	switch (i.getItem().getID()) {
		    	        	        	case 29:
		    	        	        		nature = new double[] {1.1,1.0,0.9,1.0,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 30:
		    	        	        		nature = new double[] {0.9,1.1,1.0,1.0,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 31:
		    	        	        		nature = new double[] {1.1,1.0,1.0,1.0,0.9,-1.0};
		    	        	        		break;
		    	        	        	case 32:
		    	        	        		nature = new double[] {0.9,1.0,1.0,1.1,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 33:
		    	        	        		nature = new double[] {1.0,1.0,0.9,1.1,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 34:
		    	        	        		nature = new double[] {1.0,1.1,0.9,1.0,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 35:
		    	        	        		nature = new double[] {1.0,1.0,0.9,1.0,1.1,-1.0};
		    	        	        		break;
		    	        	        	case 36:
		    	        	        		nature = new double[] {0.9,1.0,1.1,1.0,1.0,-1.0};
		    	        	        		break;
		    	        	        	case 37:
		    	        	        		nature = new double[] {1.0,1.0,1.1,1.0,0.9,-1.0};
		    	        	        		break;
		    	        	        	case 38:
		    	        	        		nature = new double[] {1.0,1.0,1.0,1.0,1.0,4.0};
		    	        	        		break;
		    	        	        	case 39:
		    	        	        		nature = new double[] {0.9,1.0,1.0,1.0,1.1,-1.0};
		    	        	        		break;
		    	                		default:
		    	                			nature = null;
		    	                			break;
		    	        	        	}
			        	        		
			        	        		String natureOld = p.team[index].getNature();
				        	        	p.team[index].nature = nature;
				        	        	p.team[index].stats = p.team[index].getStats();
				        	        	JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s nature was changed from " + natureOld + " to " + p.team[index].getNature() + "!");
				        	        	
				        	        	
		        	        			p.bag.remove(i.getItem());
				        	        		
				        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        SwingUtilities.getWindowAncestor(panel).dispose();
					        	        showBag();
			        	        		
			        	        	});
			        	        	
			        	        }
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Change Nature?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// Elixir/Max Elixir
			        	else if (i.getItem() == Item.ELIXIR || i .getItem() == Item.MAX_ELIXIR) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        party.addActionListener(g -> {
			        	        	if (i.getItem().getID() == 41) {
			        	        		boolean work = false;
			        	        		for (Moveslot m : p.team[index].moveset) {
			        	        			if (m != null && m.currentPP != m.maxPP) {
			        	        				work = true;
			        	        				break;
			        	        			}
			        	        		}
			        	        		if (work) {
				        	        		for (Moveslot m : p.team[index].moveset) {
				        	        			if (m != null) m.currentPP = m.maxPP;
				        	        		}
				        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s PP was restored!");
			        			        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        	SwingUtilities.getWindowAncestor(panel).dispose();
			        	        			p.bag.remove(i.getItem());
			        	        			showBag();
			        	        		} else {
			        	        			JOptionPane.showMessageDialog(null, "It won't have any effect.");
			        	        		}
			        	        	} else {
			        	        		JPanel movePanel = new JPanel();
			        	        	    movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
			        	        		for (Moveslot m : p.team[index].moveset) {
			        	        			if (m != null) {
					        	        		JGradientButton move = new JGradientButton("<html><center>" + m.move.toString() + "<br>" + " " + m.showPP() + "</center></html>");
					        	        		move.setFont(new Font(move.getFont().getName(), Font.PLAIN, 13));
					        	        		move.setBackground(m.move.mtype.getColor());
					        	        		move.setForeground(m.getPPColor());
					        	        		move.addMouseListener(new MouseAdapter() {
				        	        	        	@Override
				        	        			    public void mouseClicked(MouseEvent e) {
				        	        			    	if (SwingUtilities.isRightMouseButton(e)) {
				        	        			            JOptionPane.showMessageDialog(null, m.move.getMoveSummary(p.team[index], null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
				        	        			        } else {
				        	        			        	if (m.currentPP != m.maxPP) {
				        	        			        		m.currentPP = m.maxPP;
					        	        			        	JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was restored!");
					        	        			        	SwingUtilities.getWindowAncestor(movePanel).dispose();
					        	        			        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        			        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        			        	        	SwingUtilities.getWindowAncestor(panel).dispose();
					        			        	        	p.bag.remove(i.getItem());
					        			        	        	showBag();
				        	        			        	} else {
				        	        			        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
				        	        			        	}
				        	        			        }
				        	        			    }
				        	        	        });
				        	        	        movePanel.add(move);
			        	        			}
				        	        		
			        		            }
			        	        		JOptionPane.showMessageDialog(null, movePanel, "Select a move to restore PP:", JOptionPane.PLAIN_MESSAGE);
			        	        	}
			        	        	
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Teach " + i.getItem().getMove() + "?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	
			        	// PP Up/PP Max
			        	else if (i.getItem() == Item.PP_UP || i .getItem() == Item.PP_MAX) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        party.addActionListener(g -> {
			        	        	JPanel movePanel = new JPanel();
		        	        	    movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
		        	        		for (Moveslot m : p.team[index].moveset) {
		        	        			if (m != null) {
				        	        		JGradientButton move = new JGradientButton("<html><center>" + m.move.toString() + "<br>" + " " + m.showPP() + "</center></html>");
				        	        		move.setFont(new Font(move.getFont().getName(), Font.PLAIN, 13));
				        	        		move.setBackground(m.move.mtype.getColor());
				        	        		move.setForeground(m.getPPColor());
				        	        		move.addMouseListener(new MouseAdapter() {
			        	        	        	@Override
			        	        			    public void mouseClicked(MouseEvent e) {
			        	        			    	if (SwingUtilities.isRightMouseButton(e)) {
			        	        			            JOptionPane.showMessageDialog(null, m.move.getMoveSummary(p.team[index], null), "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        	        			        } else {
		        	        			        		if (m.maxPP < (m.move.pp * 8 / 5)) {
		        	        			        			if (i.getItem().getID() == 42) { // PP Up
		        	        			        				m.maxPP += Math.max((m.move.pp / 5), 1);
		        	        			        				JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was increased!");
		        	        			        			} else { // PP Max
		        	        			        				m.maxPP = (m.move.pp * 8 / 5);
		        	        			        				JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was maxed!");
		        	        			        			}
		        	        			        			SwingUtilities.getWindowAncestor(movePanel).dispose();
				        	        			        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
				        			        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
				        			        	        	SwingUtilities.getWindowAncestor(panel).dispose();
				        			        	        	p.bag.remove(i.getItem());
				        			        	        	showBag();
		        	        			        		} else {
		        	        			        			JOptionPane.showMessageDialog(null, "It won't have any effect.");
		        	        			        		}
			        	        			        }
			        	        			    }
			        	        	        });
			        	        	        movePanel.add(move);
		        	        			}
			        	        		
		        		            }
		        	        		JOptionPane.showMessageDialog(null, movePanel, "Select a move to increase PP:", JOptionPane.PLAIN_MESSAGE);
			        	        	
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Teach " + i.getItem().getMove() + "?", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	// Flame Orb
//			        	else if (i.getItem() == Item.FLAME_ORB) {
//			        		JPanel partyPanel = new JPanel();
//			        	    partyPanel.setLayout(new GridLayout(6, 1));
//			        	    
//			        	    for (int j = 0; j < 6; j++) {
//			        	    	JButton party = setUpPartyButton(j);
//			        	        final int index = j;
//			        	        
//			        	        party.addActionListener(g -> {
//			        	        	if (p.team[index].status == Status.HEALTHY && p.team[index].type1 != PType.FIRE && p.team[index].type2 != PType.FIRE) {
//			        	        		p.team[index].status = Status.BURNED;
//			        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + " successfully burned!");
//			        	        		SwingUtilities.getWindowAncestor(partyPanel).dispose();
//				        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
//				        	        	SwingUtilities.getWindowAncestor(panel).dispose();
//				        	        	showBag();
//			        	        	} else {
//			        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
//			        	        	}
//			        	        });
//			        	        
//			        	        JPanel memberPanel = new JPanel(new BorderLayout());
//			        	        memberPanel.add(party, BorderLayout.NORTH);
//			        	        partyPanel.add(memberPanel);
//			        	    }
//			        	    JOptionPane.showMessageDialog(null, partyPanel, "Burn?", JOptionPane.PLAIN_MESSAGE);
//			        	}
			        	// Edge Kit
			        	else if (i.getItem() == Item.EDGE_KIT) {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        party.addMouseListener(new MouseAdapter() {
			        	        	@Override
        	        			    public void mouseClicked(MouseEvent e) {
			        	        		if (p.team[index].expMax - p.team[index].exp != 1) {
				        	        		p.team[index].exp = p.team[index].expMax - 1;
				        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + " successfully EDGED!");
				        	        	} else {
				        	        		JOptionPane.showMessageDialog(null, "It won't have any effect.");
				        	        	}
			        	        		if (SwingUtilities.isRightMouseButton(e)) {
			        	        			SwingUtilities.getWindowAncestor(partyPanel).dispose();
					        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
					        	        	SwingUtilities.getWindowAncestor(panel).dispose();
			        	        		}
			        	        	}
			        	        });
			        	        
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "EDGE?????", JOptionPane.PLAIN_MESSAGE);
			        	}
			        	// Calc
			        	else if (i.getItem() == Item.CALCULATOR) {
			        		i.getItem().useCalc(p, null);
			        	}
			        	
			        });			     
			        
			        JButton giveButton = new JButton("Give");
			        if (pocket == Item.HELD_ITEM || pocket == Item.BERRY) {
			        	giveButton.addActionListener(f -> {
			        		JPanel partyPanel = new JPanel();
			        	    partyPanel.setLayout(new GridLayout(6, 1));
			        	    
			        	    for (int j = 0; j < 6; j++) {
			        	    	JButton party = setUpPartyButton(j);
			        	        final int index = j;
			        	        
			        	        party.addActionListener(g -> {
			        	        	if (p.team[index].item != null) {
			        	        		int option = JOptionPane.showOptionDialog(null,
			        							p.team[index].nickname + " is already holding\n" + p.team[index].item.toString() + ", do you want to swap\nit for " + i.getItem().toString() + "?",
			        				            "Item Switch",
			        				            JOptionPane.YES_NO_OPTION,
			        				            JOptionPane.QUESTION_MESSAGE,
			        				            null, null, null);
			        					if (option == JOptionPane.YES_OPTION) {
			        						Item old = p.team[index].item;
			        						p.bag.add(old);
				        	        		p.team[index].item = i.getItem();
				        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + " swapped its " + old.toString() + " for " + p.team[index].item.toString() + "!");
			        					}
			        	        	} else {
			        	        		p.team[index].item = i.getItem();
			        	        		JOptionPane.showMessageDialog(null, p.team[index].nickname + " was given " + p.team[index].item.toString() + " to hold!");
			        	        	}
			        	        	p.bag.remove(i.getItem());
			        	        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
			        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
			        	        	SwingUtilities.getWindowAncestor(panel).dispose();
			        	        });
			        	        JPanel memberPanel = new JPanel(new BorderLayout());
			        	        memberPanel.add(party, BorderLayout.NORTH);
			        	        partyPanel.add(memberPanel);
			        	    }
			        	    JOptionPane.showMessageDialog(null, partyPanel, "Give item?", JOptionPane.PLAIN_MESSAGE);
			        	});
			        	
			        }
			        
			        itemDesc.add(description);
			        if (i.getItem().getMove() != null) {
			        	itemDesc.add(moveButton);
			        } else {
			        	itemDesc.add(count);
			        	itemDesc.add(descLabel);
			        }
			        if (pocket == Item.HELD_ITEM || pocket == Item.BERRY) {
			        	itemDesc.add(giveButton);
			        } else {
			        	itemDesc.add(useButton);
			        }

			        JOptionPane.showMessageDialog(null, itemDesc, "Item details", JOptionPane.PLAIN_MESSAGE);
			    });
			    item.setBackground(i.getItem().getColor());
			    if (i.getItem().getMove() != null) item.setBackground(i.getItem().getMove().mtype.getColor());
			    itemPanel.add(new JLabel(new ImageIcon(i.getItem().getImage())));
			    itemPanel.add(item);
			    panel.add(itemPanel);
			}
		}

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(200, 300));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.add(scrollPane, BorderLayout.CENTER);
		
		return containerPanel;
	}

	private void showDex() {
	    JPanel dexPanel = new JPanel();
	    dexPanel.setLayout(new GridLayout(60, 6));

	    for (int j = 1; j < 237; j++) {
	    	final int id = j;
	    	JButton mon = new JGradientButton("");
	        
	        if (p.pokedex[j] == 0) {
	        	mon.setText("???");
	        } else if (p.pokedex[j] == 1) {
		        ImageIcon sprite = new ImageIcon(p.getCurrent().getSprite(j));
		        mon.setIcon(sprite);
		        mon.setBackground(Color.yellow);
	        } else {
		        ImageIcon sprite = new ImageIcon(p.getCurrent().getSprite(j));
		        mon.setIcon(sprite);
		        mon.setBackground(Color.green);
	        }
	        
	        mon.addActionListener(e -> {
	            JPanel teamMemberPanel = p.getCurrent().getDexSummary(id, p.pokedex[id]);
	            JOptionPane.showMessageDialog(null, teamMemberPanel, "Pokemon details", JOptionPane.PLAIN_MESSAGE);
	        });

	        dexPanel.add(mon);
	    }

	    JScrollPane dexScrollPane = new JScrollPane(dexPanel);
	    dexScrollPane.setPreferredSize(new Dimension(600, 600)); // Set the preferred size of the scroll pane
	    
	    // Set the vertical and horizontal unit increments to control the scrolling speed
	    dexScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Adjust the value as needed

	    JOptionPane.showMessageDialog(null, dexScrollPane, "Pokedex", JOptionPane.PLAIN_MESSAGE);
	    keyH.resume();
	}
	
	private void interactMarket() {
		keyH.pause();
		
		JPanel shopPanel = new JPanel();
	    shopPanel.setLayout(new GridLayout(6, 1));
	    Item[] shopItems = new Item[1];
	    if (gp.currentMap == 30) { shopItems = new Item[] {Item.REPEL, Item.POKEBALL, Item.KLEINE_BAR, Item.BOTTLE_CAP, Item.TM49, Item.TM51};
	    } else if (gp.currentMap == 40) { shopItems = new Item[] {Item.TM12, Item.TM13, Item.TM15, Item.TM16, Item.TM23, Item.TM24};
	    } else if (gp.currentMap == 89) { shopItems = new Item[] {Item.TM45, Item.TM50, Item.TM54, Item.TM74, Item.TM75,
	    		Item.TM76, Item.TM77, Item.TM79, Item.TM80, Item.TM81, Item.TM82, Item.TM95};
	    } else if (gp.currentMap == 92) { shopItems = new Item[] {Item.ADAMANT_MINT, Item.BOLD_MINT, Item.BRAVE_MINT, Item.CALM_MINT, Item.CAREFUL_MINT, Item.IMPISH_MINT,
	    		Item.JOLLY_MINT, Item.MODEST_MINT, Item.QUIET_MINT, Item.SERIOUS_MINT, Item.TIMID_MINT};
	    } else if (gp.currentMap == 112) { shopItems = new Item[] {Item.MAX_ELIXIR, Item.PP_UP, Item.TM41, Item.TM42, Item.TM43, Item.TM44, Item.TM91};
	    }
	    for (int i = 0; i < shopItems.length; i++) {
	    	JGradientButton item = new JGradientButton(shopItems[i].toString() + ": $" + shopItems[i].getCost());
	    	Item curItem = shopItems[i];
	    	item.setBackground(curItem.getColor());
	    	item.addMouseListener(new MouseAdapter() {
	        	@Override
			    public void mouseClicked(MouseEvent e) {
	        		if (SwingUtilities.isRightMouseButton(e)) {
			            if (curItem.getID() > 100) JOptionPane.showMessageDialog(null, curItem.getMove().getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
		    		} else {
		    	    	if (p.buy(curItem)) {
		    	            JOptionPane.showMessageDialog(null, "Purchased 1 " + curItem.toString() + " for $" + curItem.getCost());
		    	            SwingUtilities.getWindowAncestor(shopPanel).dispose();
		    	            interactMarket();
		    	        } else {
		    	            JOptionPane.showMessageDialog(null, "Not enough money!");
		    	        }
		    		}
	        	}
	    	});
	    	shopPanel.add(item);
	    	if (curItem.getID() > 100 && p.bag.contains(curItem.getID())) shopPanel.remove(item);
	    }
		JOptionPane.showMessageDialog(null, shopPanel, "Money: $" + p.money, JOptionPane.PLAIN_MESSAGE);
		keyH.resume();
	}


	public JGradientButton setUpPartyButton(int j) {
		JGradientButton party = new JGradientButton("");
		party.setText("");
        if (p.team[j] != null) {
        	if (p.team[j].isFainted()) {
        		party = new JGradientButton("");
            	party.setBackground(new Color(200, 0, 0));
            	party.setSolid(true);
            } else {
            	party.setBackground(p.team[j].type1.getColor(), p.team[j].type2 == null ? null : p.team[j].type2.getColor());
            }
            party.setText(p.team[j].nickname + "  lv " + p.team[j].getLevel());
            party.setHorizontalAlignment(SwingConstants.CENTER);
            party.setFont(new Font("Tahoma", Font.PLAIN, 11));
            party.setVisible(true);
        } else {
            party.setVisible(false);
        }
        return party;
	}
	
	private JProgressBar setupHPBar(int j) {
		JProgressBar partyHP = new JProgressBar(0, 50);
		if (p.team[j] != null) {
            partyHP.setMaximum(p.team[j].getStat(0));
            partyHP.setValue(p.team[j].currentHP);
            if (partyHP.getPercentComplete() > 0.5) {
                partyHP.setForeground(new Color(0, 255, 0));
            } else if (partyHP.getPercentComplete() <= 0.5 && partyHP.getPercentComplete() > 0.25) {
                partyHP.setForeground(new Color(255, 255, 0));
            } else {
                partyHP.setForeground(new Color(255, 0, 0));
            }
            partyHP.setVisible(true);
        } else {
            partyHP.setVisible(false);
        }
		return partyHP;
	}
	
	private void useRareCandies(Pokemon pokemon, int numCandies, Item item) {
		boolean evolved = false;
	    for (int i = 0; i < numCandies; i++) {
	        if (pokemon.getLevel() == 100 || p.bag.count[item.getID()] <= 0 || evolved) {
	            break; // Stop using Rare Candies if level 100 or out of candies
	        }
	        evolved = p.elevate(pokemon);
	        p.bag.remove(item);
	    }
	}
	
	private void interactCutTree(int i) {
		keyH.pause();
		if (p.hasMove(Move.CUT)) {
			int option = JOptionPane.showOptionDialog(null,
					"This tree looks like it can be cut down!\nDo you want to cut it?",
		            "Cut Tree",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (option == JOptionPane.YES_OPTION) {
				Cut_Tree temp = (Cut_Tree) gp.iTile[gp.currentMap][i];
				gp.iTile[gp.currentMap][i] = new Tree_Stump(gp);
				gp.iTile[gp.currentMap][i].worldX = temp.worldX;
				gp.iTile[gp.currentMap][i].worldY = temp.worldY;
			}
		} else {
			JOptionPane.showMessageDialog(null, "This tree looks like it can be cut down!");
		}
		keyH.resume();
		
	}
	
	private void interactRockSmash(int i) {
		keyH.pause();
		if (p.hasMove(Move.ROCK_SMASH)) {
			int option = JOptionPane.showOptionDialog(null,
					"This rock looks like it can be broken!\nDo you want to use Rock Smash?",
		            "Rock Smash",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (option == JOptionPane.YES_OPTION) {
				gp.iTile[gp.currentMap][i] = null;
			}
		} else {
			JOptionPane.showMessageDialog(null, "This rock looks like it can be broken!");
		}
		keyH.resume();
		
	}
	
	private void interactVines(int i) {
		if (cross) {
			if (p.hasMove(Move.VINE_CROSS)) {
				Vine_Crossable temp = (Vine_Crossable) gp.iTile[gp.currentMap][i];
				gp.iTile[gp.currentMap][i] = new Vine(gp);
				gp.iTile[gp.currentMap][i].worldX = temp.worldX;
				gp.iTile[gp.currentMap][i].worldY = temp.worldY;
			} else {
				keyH.pause();
				JOptionPane.showMessageDialog(null, "This gap looks like it can be crossed!");
				cross = false;
				keyH.resume();
			}
		} else {
			keyH.pause();
			if (p.hasMove(Move.VINE_CROSS)) {
				int option = JOptionPane.showOptionDialog(null,
						"This gap can be crossed!\nDo you want to use Vine Cross?",
			            "Vine Cross",
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE,
			            null, null, null);
				if (option == JOptionPane.YES_OPTION) {
					Vine_Crossable temp = (Vine_Crossable) gp.iTile[gp.currentMap][i];
					gp.iTile[gp.currentMap][i] = new Vine(gp);
					gp.iTile[gp.currentMap][i].worldX = temp.worldX;
					gp.iTile[gp.currentMap][i].worldY = temp.worldY;
					cross = true;
				}
			} else {
				JOptionPane.showMessageDialog(null, "This gap looks like it can be crossed!");
				cross = false;
			}
			keyH.resume();
		}
		
	}
	
	private void interactPit(int i) {
		keyH.pause();
		if (p.hasMove(Move.SLOW_FALL)) {
			int option = JOptionPane.showOptionDialog(null,
					"This pit can be traversed!\nWould you like to use Slow Fall?",
					"Slow Fall",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (option == JOptionPane.YES_OPTION) {
				Pit pit = (Pit) gp.iTile[gp.currentMap][i];
				gp.eHandler.teleport(pit.mapDest, pit.xDest, pit.yDest, false);
			}
		} else {
			JOptionPane.showMessageDialog(null, "This pit looks deep!\nI can't even see the bottom!");
		}
		keyH.resume();
		
	}
	
	private void interactWhirlpool(int i) {
		keyH.pause();
		if (p.hasMove(Move.WHIRLPOOL)) {
			int option = JOptionPane.showOptionDialog(null,
					"This water vortex can be crossed!\nWould you like to use Whirlpool?",
					"Whirlpool",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (option == JOptionPane.YES_OPTION) {
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
			}
		} else {
			JOptionPane.showMessageDialog(null, "This water vortex can be crossed!");
		}
		keyH.resume();
		
	}
	
	private void interactRockClimb(int i) {
		keyH.pause();
		if (p.hasMove(Move.ROCK_CLIMB)) {
			int option = JOptionPane.showOptionDialog(null,
					"This wall can be scaled!\nWould you like to use Rock Climb?",
					"Rock Climb",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null, null, null);
			if (option == JOptionPane.YES_OPTION) {
				Rock_Climb rc = (Rock_Climb) gp.iTile[gp.currentMap][i];
				int inverse = this.direction == rc.direction ? 1 : -1;
				this.worldX += ((rc.deltaX * gp.tileSize * inverse * rc.amt) + (gp.tileSize * 0.75 * rc.deltaX * inverse));
				this.worldY += ((rc.deltaY * gp.tileSize * inverse * rc.amt) + (gp.tileSize * 0.75 * rc.deltaY * inverse));
			}
		} else {
			JOptionPane.showMessageDialog(null, "This wall looks like it can be scaled!");
		}
		keyH.resume();
		
	}
	
	private boolean useRepel() {
		if (!p.repel) {
			p.repel = true;
			p.steps = 1;
			p.bag.remove(Item.REPEL);
			return true;
	    } else {
	    	JOptionPane.showMessageDialog(null, "It won't have any effect.");
	    	return false;
	    }
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
}
