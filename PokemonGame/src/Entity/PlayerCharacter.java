package Entity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Obj.Cut_Tree;
import Obj.Rock_Smash;
import Obj.Tree_Stump;
import Obj.Vine;
import Obj.Vine_Crossable;
import Overworld.GamePanel;
import Overworld.KeyHandler;
import Overworld.Main;
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
	public boolean surf = false;
	
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
		worldX = gp.tileSize * 90;
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
			
			gp.cChecker.checkEntity(this, gp.npc);
			
			gp.cChecker.checkObject(this);
			
			gp.cChecker.checkEntity(this, gp.iTile);
			
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
			}
			if (spriteCounter % 4 == 0 && (inTallGrass || surf) && !p.repel) {
				Random r = new Random();
				int random = r.nextInt(150);
				if (random < speed) {
					String area = "Standard";
					if (surf) area = "Surfing";
					gp.startWild(gp.currentMap, worldX / gp.tileSize, worldY / gp.tileSize, area);
				}
			}
			if (p.steps == 202 && p.repel) {
				keyH.pause();
				JOptionPane.showMessageDialog(null, "Repel's effects wore off.");
				keyH.resume();
				p.repel = false;
			}
			if (p.steps % 129 == 0) {
				for (Pokemon p : p.team) {
            		if (p != null) p.awardHappiness(1);
            	}
				p.steps++;
			}
			
			gp.eHandler.checkEvent();
			
			if (surf) {
				double surfXD =  worldX / 48.0;
				int surfX = (int) Math.round(surfXD);
				
				double surfYD =  worldY / 48.0;
				int surfY = (int) Math.round(surfYD);
				if (!gp.tileM.getWaterTiles().contains(gp.tileM.mapTileNum[gp.currentMap][surfX][surfY])) {
					surf = false;
					for (Integer i : gp.tileM.getWaterTiles()) {
						gp.tileM.tile[i].collision = true;
					}
				}
			}
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
		if (gp.ticks > 3) {
			gp.ticks = 0;
		}
		for (int i = 0; i < gp.npc[1].length; i++) {
			int trainer = gp.npc[gp.currentMap][i] == null ? 0 : gp.npc[gp.currentMap][i].trainer;
			if (gp.ticks == 0 && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "down") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 1 && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "up") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 2 && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "left") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
			if (gp.ticks == 3 && gp.cChecker.checkTrainer(this, gp.npc[gp.currentMap][i], trainer) && gp.npc[gp.currentMap][i].direction == "right") gp.startBattle(gp.npc[gp.currentMap][i].trainer);
		}
		if (keyH.wPressed) {
			int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_Nurse) interactNurse();
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_Clerk) interactClerk();
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_Market) interactMarket();
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_Block) interactBlock((NPC_Block) gp.npc[gp.currentMap][npcIndex]);
			if (npcIndex != 999 && (gp.npc[gp.currentMap][npcIndex] instanceof NPC_Trainer || gp.npc[gp.currentMap][npcIndex] instanceof NPC_TN || gp.npc[gp.currentMap][npcIndex] instanceof NPC_Rival || gp.npc[gp.currentMap][npcIndex] instanceof NPC_Rival2)) gp.startBattle(gp.npc[gp.currentMap][npcIndex].trainer);
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_GymLeader) gp.startBattle(gp.npc[gp.currentMap][npcIndex].trainer);
			if (npcIndex != 999 && gp.npc[gp.currentMap][npcIndex] instanceof NPC_PC) gp.openBox();
			
			int objIndex = gp.cChecker.checkObject(this);
			if (objIndex != -1) pickUpObject(objIndex);
			
			int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
			if (iTileIndex != 999 && gp.iTile[gp.currentMap][iTileIndex] instanceof Cut_Tree) interactCutTree(iTileIndex);
			if (iTileIndex != 999 && gp.iTile[gp.currentMap][iTileIndex] instanceof Rock_Smash) interactRockSmash(iTileIndex);
			if (iTileIndex != 999 && gp.iTile[gp.currentMap][iTileIndex] instanceof Vine_Crossable) interactVines(iTileIndex);
			
			if (p.hasMove(Move.SURF) && !surf) {
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
						surf = true;
						for (Integer i : gp.tileM.getWaterTiles()) {
							gp.tileM.tile[i].collision = false;
						}
						
					}
					keyH.resume();
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
		
		p.bag.add(gp.obj[gp.currentMap][objIndex].item);
		JOptionPane.showMessageDialog(null, "You found 1 " + gp.obj[gp.currentMap][objIndex].item.toString() + "!");
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
	    			p.bag.bag[18] = new Item(18);
	    			p.bag.count[18] = 999;
	    			SwingUtilities.getWindowAncestor(cheats).dispose();
	    		} else if (code.equals("M1X3R")) {
	    			p.random = !p.random;
	    			String onoff = p.random ? "on!" : "off.";
	    			JOptionPane.showMessageDialog(null, "Randomizer mode was turned " + onoff);
	    			SwingUtilities.getWindowAncestor(cheats).dispose();
	    		} else if (code.equals("BALLZ")) {
	    			for (int i = 1; i < 4; i++) {
	    				p.bag.bag[i] = new Item(i);
		    			p.bag.count[i] = 25;
	    			}
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
	    		            p.bag.add(new Item(id), amt);
	    		            SwingUtilities.getWindowAncestor(cheats).dispose();
	    		        } catch (NumberFormatException g) {
	    		            // Handle invalid input (e.g., if the entered value is not a valid integer)
	    		            System.out.println("Invalid item ID.");
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
	    		            System.out.println("Invalid Pokemon ID/Level.");
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
	    			boolean[] temp = p.trainersBeat.clone();
	    			p.trainersBeat = new boolean[Main.trainers.length];
	    			for (int i = 0; i < temp.length; i++) {
	    				p.trainersBeat[i] = temp[i];
	    			}
	    			boolean[][] tempObj = p.itemsCollected.clone();
	    			p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
	    			for (int i = 0; i < tempObj.length; i++) {
	    				for (int j = 0; j < tempObj[1].length; j++) {
	    					p.itemsCollected[i][j] = tempObj[i][j];
	    				}
	    			}
	    			boolean[] tempFlag = p.flags.clone();
	    			p.flags = new boolean[20];
	    			for (int i = 0; i < tempFlag.length; i++) {
	    				p.flags[i] = tempFlag[i];
	    			}
	    			for (Pokemon p : p.team) {
	    				if (p != null) {
	    					p.setBaseStats();
	    					p.setAbility(p.abilitySlot);
	    					p.setMoveBank();
	    				}
	    			}
	    			for (Pokemon p : p.box1) {
	    				if (p != null) p.setBaseStats();
	    			}
	    			for (Pokemon p : p.box2) {
	    				if (p != null) p.setBaseStats();
	    			}
	    			for (Pokemon p : p.box3) {
	    				if (p != null) p.setBaseStats();
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
	    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("player.dat"))) {
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
	    Item[] shopItems = new Item[] {new Item(0), new Item(1), new Item(4), new Item(9), new Item(10), new Item(11), new Item(12), new Item(13), // 0 badges
	    		new Item(2), new Item(5), // 1 badge
	    		new Item(40), // 2 badges
	    		new Item(14), new Item(16), // 3 badges
	    		new Item(3), new Item(6), // 4 badges
	    		new Item(7), // 7 badges
	    		new Item(8), new Item(17)}; // 8 badges
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
								p.bag.add(new Item(24));
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
								p.bag.add(new Item(25));
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
					p.bag.add(new Item(96));
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
					p.catchPokemon(new Pokemon(id, 5, true, false));
				}
				if (gp.currentMap == 47 && !p.flags[10]) {
					Random starter = new Random();
					int id = starter.nextInt(3);
					while (id + 1 == p.starter) {
						id = starter.nextInt(3);
					}
					id *= 3;
					id += 1;
					Pokemon result = new Pokemon(id, 5, true, false);
					p.flags[10] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				}
				if (gp.currentMap == 18 && !p.flags[12]) {
					Random gift = new Random();
					int id = gift.nextInt(6); // Dualmoose, Sparkdust, Posho, Kissyfishy, Minishoo, Tinkie
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
					Pokemon result = new Pokemon(id, 15, true, false);
					p.flags[12] = true;
					JOptionPane.showMessageDialog(null, "You recieved " + result.name + "!");
					p.catchPokemon(result);
				}
				if (gp.currentMap == 49 && !p.flags[13]) {
					JOptionPane.showMessageDialog(null, "I encountered this very\nstrong Pokemon, and I\ndon't think I'm strong\nenough to train it. Here!");
					Random gift = new Random();
					int id = gift.nextInt(5); // Pebblepup, Fightorex, Tricerpup, Shockfang, Nightrex
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
					while (p.pokedex[id] == 2) {
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
					}
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
	            JPanel teamMemberPanel = p.team[index].showSummary();
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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		ArrayList<Entry> bag = p.getBag().getItems();
		for (Entry i : bag) {
		    JButton item = new JGradientButton("");
		    item.setText(i.getItem().toString() + " x " + i.getCount());

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
		        	if (i.getItem().getHealAmount() != 0) {
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
		        	if (i.getItem().getID() > 8 && i.getItem().getID() < 16) {
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
		        	if (i.getItem().getID() == 16 || i.getItem().getID() == 17) {
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
		        	if (i.getItem().getID() == 18) {
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
		        	if (i.getItem().getID() == 19) {
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
		        	        		p.team[index].happiness = p.team[index].happiness + 100 >= 255 ? 255 : p.team[index].happiness + 100;
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
		        	if (i.getItem().getID() >= 20 && i.getItem().getID() < 26) {
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
		        	if (i.getItem().getID() == 0) {
		        		if (!p.repel) {
		        			p.repel = true;
		        			p.steps = 1;
		        			p.bag.remove(i.getItem());
	        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
	        	        	SwingUtilities.getWindowAncestor(panel).dispose();
	        	        	showBag();
		        	    } else {
		        	    	JOptionPane.showMessageDialog(null, "It won't have any effect.");
		        	    }
		        	}
		        	
		        	// TMS/HMS
		        	if (i.getItem().getMove() != null) {
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
		        	if (i.getItem().getID() == 26) {
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
		        	if (i.getItem().getID() == 27 || i.getItem().getID() == 28) {
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
		        	if (i.getItem().getID() >= 29 && i.getItem().getID() <= 39) {
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
		        	if (i.getItem().getID() == 40 || i.getItem().getID() == 41) {
		        		JPanel partyPanel = new JPanel();
		        	    partyPanel.setLayout(new GridLayout(6, 1));
		        	    
		        	    for (int j = 0; j < 6; j++) {
		        	    	JButton party = setUpPartyButton(j);
		        	        final int index = j;
		        	        
		        	        party.addActionListener(g -> {
		        	        	if (i.getItem().getID() == 41) {
		        	        		for (Moveslot m : p.team[index].moveset) {
		        	        			if (m != null) m.currentPP = m.maxPP;
		        	        			JOptionPane.showMessageDialog(null, p.team[index].nickname + "'s PP was restored!");
		        	        			p.bag.remove(i.getItem());
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
			        	        			            JOptionPane.showMessageDialog(null, m.move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        	        			        } else {
			        	        			        	m.currentPP = m.maxPP;
			        	        			        	JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was restored!");
			        	        			        	SwingUtilities.getWindowAncestor(movePanel).dispose();
			        	        			        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
			        			        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
			        			        	        	SwingUtilities.getWindowAncestor(panel).dispose();
			        			        	        	p.bag.remove(i.getItem());
			        			        	        	showBag();
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
		        	if (i.getItem().getID() == 42 || i.getItem().getID() == 43) {
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
		        	        			            JOptionPane.showMessageDialog(null, m.move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
		        	        			        } else {
	        	        			        		if (m.maxPP < (m.move.pp * 8 / 5)) {
	        	        			        			if (i.getItem().getID() == 42) { // PP Up
	        	        			        				m.maxPP += Math.max((m.move.pp / 5), 1);
	        	        			        				JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was increased!");
	        	        			        			} else { // PP Max
	        	        			        				m.maxPP = (m.move.pp * 8 / 5);
	        	        			        				JOptionPane.showMessageDialog(null, m.move.toString() + "'s PP was maxed!");
	        	        			        			}
	        	        			        		} else {
	        	        			        			JOptionPane.showMessageDialog(null, "It won't have any effect.");
	        	        			        		}
		        	        			        	SwingUtilities.getWindowAncestor(movePanel).dispose();
		        	        			        	SwingUtilities.getWindowAncestor(partyPanel).dispose();
		        			        	        	SwingUtilities.getWindowAncestor(itemDesc).dispose();
		        			        	        	SwingUtilities.getWindowAncestor(panel).dispose();
		        			        	        	p.bag.remove(i.getItem());
		        			        	        	showBag();
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
		        	
		        	// Calc
		        	if (i.getItem().getID() == 200) {
		        		i.getItem().useCalc(p);
		        	}
		        	
		        });
		        
		     
		        
		        itemDesc.add(description);
		        if (i.getItem().getMove() != null) {
		        	itemDesc.add(moveButton);
		        } else {
		        	itemDesc.add(count);
		        	itemDesc.add(descLabel);
		        }
		        itemDesc.add(useButton);

		        JOptionPane.showMessageDialog(null, itemDesc, "Item details", JOptionPane.PLAIN_MESSAGE);
		    });
		    item.setBackground(i.getItem().getColor());
		    if (i.getItem().getMove() != null) item.setBackground(i.getItem().getMove().mtype.getColor());
		    panel.add(item);
		}

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setPreferredSize(new Dimension(panel.getPreferredSize().width, 300));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.add(scrollPane, BorderLayout.CENTER);

		JOptionPane.showMessageDialog(null, containerPanel, "Bag", JOptionPane.PLAIN_MESSAGE);
		keyH.resume();
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
	    if (gp.currentMap == 30) shopItems = new Item[] {new Item(0), new Item(1), new Item(15), new Item(27), new Item(149), new Item(151)};
	    if (gp.currentMap == 40) shopItems = new Item[] {new Item(112), new Item(113), new Item(115), new Item(116), new Item(123), new Item(124)};
	    if (gp.currentMap == 89) shopItems = new Item[] {new Item(145), new Item(150), new Item(154), new Item(174), new Item(175),
	    		new Item(176), new Item(177), new Item(179), new Item(180), new Item(181), new Item(182), new Item(195)};
	    if (gp.currentMap == 92) shopItems = new Item[] {new Item(29), new Item(30), new Item(31), new Item(32), new Item(33), new Item(34),
	    		new Item(35), new Item(36), new Item(37), new Item(38), new Item(39)};
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


	public JButton setUpPartyButton(int j) {
		JButton party = new JGradientButton("");
		party.setText("");
        if (p.team[j] != null) {
        	if (p.team[j].isFainted()) {
        		party = new JButton("");
            	party.setBackground(new Color(200, 0, 0));
            } else {
            	party.setBackground(p.team[j].type1.getColor());
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

	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		
		switch(direction) {
		case "up":
			if (spriteNum == 1) image = up1;
			if (spriteNum == 2) image = up2;
			if (surf) image = surf2;
			break;
		case "down":
			if (spriteNum == 1) image = down1;
			if (spriteNum == 2) image = down2;
			if (surf) image = surf1;
			break;
		case "left":
			if (spriteNum == 1) image = left1;
			if (spriteNum == 2) image = left2;
			if (surf) image = surf3;
			break;
		case "right":
			if (spriteNum == 1) image = right1;
			if (spriteNum == 2) image = right2;
			if (surf) image = surf4;
			break;
		}
		g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
	}
}
