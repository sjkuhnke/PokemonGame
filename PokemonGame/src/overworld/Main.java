package overworld;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import entity.Entity;
import entity.PlayerCharacter;
import pokemon.Ability;
import pokemon.Encounter;
import pokemon.Item;
import pokemon.Move;
import pokemon.Moveslot;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Trainer;
import pokemon.JGradientButton;
import pokemon.Node;

public class Main {
	public static Trainer[] trainers;
	public static ArrayList<Trainer> modifiedTrainers;
	public static JFrame window;
	public static GamePanel gp;
	
	public static ArrayList<String> bosses = new ArrayList<String>(Arrays.asList("Scott", "Fred", "Rick", "Leader", "Maxwell", "Arthra", "Robin", "Stanford", "Millie", "Glacius", "Mindy", "Rayna", "Merlin", "Nova"));

	public static void main(String[] args) {
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Pokemon Game");
		
		gp = new GamePanel(window);
		
		showStartupMenu(window);
	}
	
	

	private static void showStartupMenu(JFrame window) {
		WelcomeMenu menu = new WelcomeMenu(window, gp);
		window.add(menu);
		
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void loadSave(JFrame window, String fileName, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		try {
			// Check if the directory exists, create it if not
            Path savesDirectory = Paths.get("./saves/");
            if (!Files.exists(savesDirectory)) {
                try {
					Files.createDirectories(savesDirectory);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./saves/" + fileName));
	        gp.player.p = (Player) ois.readObject();
        	for (Pokemon p : gp.player.p.getTeam()) {
	            if (p != null) p.clearVolatile();
	        }
	        gp.player.worldX = gp.player.p.getPosX();
	        gp.player.worldY = gp.player.p.getPosY();
	        gp.currentMap = gp.player.p.currentMap;
	        if (gp.player.p.surf) {
	        	for (Integer i : gp.tileM.getWaterTiles()) {
	        		gp.tileM.tile[i].collision = false;
				}
	        }
	        if (gp.player.p.lavasurf) {
	        	for (Integer i : gp.tileM.getLavaTiles()) {
	        		gp.tileM.tile[i].collision = false;
				}
	        }
	        ois.close();
	    } catch (IOException | ClassNotFoundException e) {
	        // If there's an error reading the file, create a new Player object
	        gp.player.p = new Player(gp);

	        String[] options = {"Twigle", "Lagma", "Lizish"};
	        JPanel optionPanel = new JPanel(new GridLayout(1, options.length));

	        int index = 0;
	        for (int i = 1; i < 8; i += 3) {
	            Pokemon dummy = new Pokemon(i, 1, false, false);
	            JGradientButton optionButton = new JGradientButton(options[index]);
	            ImageIcon spriteIcon = new ImageIcon(dummy.getSprite(i));
	            optionButton.setIcon(spriteIcon);
	            optionButton.setBackground(dummy.type1.getColor());

	            // Add an ActionListener to the button to handle user selection
	            int finalChoice = index + 1; // To make the variable effectively final
	            optionButton.addActionListener(f -> {
	                // Record the selected choice and close the JOptionPane
	                gp.player.p.starter = finalChoice;
	                JComponent component = (JComponent) f.getSource();
	                SwingUtilities.getWindowAncestor(component).dispose();
	            });

	            optionPanel.add(optionButton);
	            index++;
	        }

	        // Show the JOptionPane with the custom option panel
	        JOptionPane.showOptionDialog(
	            null, 
	            optionPanel, 
	            "Choose your starter Pokemon", 
	            JOptionPane.DEFAULT_OPTION, 
	            JOptionPane.QUESTION_MESSAGE, 
	            null, 
	            null, 
	            null
	        );
	        
	        // Add the chosen starter to the player's team
	        if (gp.player.p.starter <= 0) gp.player.p.starter = (int)(Math.random() * options.length) + 1;
	        if (gp.player.p.starter == 1) {
	            gp.player.p.catchPokemon(new Pokemon(1, 5, true, false));
	        } else if (gp.player.p.starter == 2) {
	            gp.player.p.catchPokemon(new Pokemon(4, 5, true, false));
	        } else if (gp.player.p.starter == 3) {
	            gp.player.p.catchPokemon(new Pokemon(7, 5, true, false));
	        }
	        Random random = new Random();
	        int secondStarter = -1;
	        do {
	        	secondStarter = random.nextInt(3) + 1;
	        } while (secondStarter == gp.player.p.starter);
	        gp.player.p.secondStarter = secondStarter;
	        int secondItem = -1;
	        do {
	        	secondItem = random.nextInt(3) + 1;
	        } while (secondItem == gp.player.p.secondStarter);
	        gp.player.p.scottItem = secondItem;
	        
	        gp.player.p.resistBerries = new Item[20];
	        int count = 0;
			for (int i = 232; i < 252; i++) {
				 gp.player.p.resistBerries[count] = Item.getItem(i);
				count++;
			}
	        List<Item> berryList = Arrays.asList(gp.player.p.resistBerries);
	        Collections.shuffle(berryList);
	        gp.player.p.resistBerries = berryList.toArray(new Item[1]);
	    }
		
		PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / 48), (int) Math.round(gp.player.worldY * 1.0 / 48));
		window.setTitle("Pokemon Game - " + PlayerCharacter.currentMapName);
		
		loadGame(window, welcomeMenu, selectedOptions);
	}
	
	private static void loadGame(JFrame window, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		window.remove(welcomeMenu);
        window.repaint();
        
		SwingUtilities.invokeLater(() -> {
			// Create a JPanel for the fade effect
		    FadingPanel fadePanel = new FadingPanel();
		    fadePanel.setBackground(Color.BLACK);
		    fadePanel.setBounds(0, 0, window.getWidth(), window.getHeight());
		    window.add(fadePanel, 0); // Add the fadePanel at the bottom layer
		    
		    gp.setupGame();
		    setTrainers();
		    modifyTrainers(gp);
		    gp.startGameThread();
		    
		    if (gp.player.p.version != Player.VERSION) {
	        	gp.player.p.update(gp);
	        } else {
	        	gp.player.p.setSprites();
	        }
		    
		    // Create a Timer to gradually change the opacity of the fadePanel
		    Timer timer = new Timer(20, null);
		    timer.addActionListener(e -> {
		        int alpha = fadePanel.getAlpha();
		        alpha += 2; // Adjust the fading speed
		        fadePanel.setAlpha(alpha);
		        fadePanel.repaint();
	
		        if (alpha >= 95) {
		            timer.stop();
		    		
		            Path docsDirectory = Paths.get("./docs/");
					if (!Files.exists(docsDirectory)) {
		                try {
							Files.createDirectories(docsDirectory);
						} catch (IOException f) {
							f.printStackTrace();
						}
		            }
		            
		    		if (selectedOptions[0]) writeTrainers(gp);
		    		if (selectedOptions[1]) writePokemon();
		    		if (selectedOptions[2]) writeEncounters();
		    		if (selectedOptions[3]) writeMoves();
		    		if (selectedOptions[4]) writeDefensiveTypes();
		    		if (selectedOptions[5]) writeOffensiveTypes();
		    		
		    		window.add(gp);
		    		gp.requestFocusInWindow();
		    		
		    		window.pack();
		    		
		    		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    		window.addWindowListener(new WindowAdapter() {
		                @Override // implementation
		                public void windowClosing(WindowEvent e) {
		                	int option = JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?\nAny unsaved progress will be lost!", "Confirm Exit", JOptionPane.YES_NO_OPTION);
		                    if (option == JOptionPane.YES_OPTION) {
		                        // Close the application
		                        System.exit(0);
		                    }
		                }
		            });
		    		
		    		gp.eHandler.p = gp.player.p;
		    		
		    		if (gp.currentMap == 107 && gp.player.p.flags[28]) {
		    			gp.tileM.openGhostlyBluePortals();
		    		}
		        }
		    });
			
			timer.start();
		});
	}

	private static void modifyTrainers(GamePanel gp) {
		Item scottItem = null;
		Item fredItem = null;
		if (gp.player.p.secondStarter != 0) {
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			items[gp.player.p.secondStarter - 1] = null;
			scottItem = items[gp.player.p.scottItem - 1];
			items[gp.player.p.scottItem - 1] = null;
			fredItem = null;
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					fredItem = items[i];
					break;
				}
			}
		}
		if (gp.player.p.starter == 1) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(7, 7, false, true)}, 400, scottItem, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(5, 20, false, true)}, 400, fredItem);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(8, 22, false, true), new Pokemon(166, 22, false, true)}, 400, Item.HM02, 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(5, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Scott 3", new Pokemon[]{new Pokemon(77, 47, false, true), new Pokemon(130, 47, false, true), new Pokemon(9, 47, false, true), new Pokemon(37, 46, false, true), new Pokemon(167, 48, false, true)}, 400, 15);
			trainers[216] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(100, 56, false, true), new Pokemon(210, 56, false, true), new Pokemon(219, 57, false, true), new Pokemon(6, 57, false, true), new Pokemon(196, 56, false, true), new Pokemon(79, 58, false, true)}, 400);
			trainers[242] = new Trainer("Scott 4", new Pokemon[]{new Pokemon(77, 59, false, true), new Pokemon(130, 60, false, true), new Pokemon(189, 58, false, true), new Pokemon(9, 60, false, true), new Pokemon(37, 59, false, true), new Pokemon(168, 61, false, true)}, 400, 21);
			trainers[344] = new Trainer("Fred 4", new Pokemon[]{new Pokemon(196, 77, false, true), new Pokemon(210, 76, false, true), new Pokemon(219, 77, false, true), new Pokemon(6, 77, false, true), new Pokemon(100, 76, false, true), new Pokemon(79, 78, false, true)}, 400, 27);
			
			setAbility("Scott 1", 1, Ability.PROTEAN);
			setAbility("Fred 1", 2, Ability.DRY_SKIN);
			setAbility("Scott 2", 2, Ability.PROTEAN);
			setAbility("Fred 2", 3, Ability.DRY_SKIN);
			setAbility("Scott 3", 3, Ability.PROTEAN);
			setAbility("Fred 3", 4, Ability.DRY_SKIN);
			setAbility("Scott 4", 4, Ability.PROTEAN);
			setAbility("Fred 4", 4, Ability.DRY_SKIN);
			
			setItem("Scott 1", 1, Item.MYSTIC_WATER);
			setItem("Fred 1", 2, Item.CHARCOAL);
		}
		else if (gp.player.p.starter == 2) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(1, 7, false, true)}, 400, scottItem, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(8, 20, false, true)}, 400, fredItem);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(2, 22, false, true), new Pokemon(166, 22, false, true)}, 400, Item.HM02, 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(8, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Scott 3", new Pokemon[]{new Pokemon(77, 47, false, true), new Pokemon(130, 47, false, true), new Pokemon(3, 47, false, true), new Pokemon(37, 46, false, true), new Pokemon(167, 48, false, true)}, 400, 15);
			trainers[216] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(100, 56, false, true), new Pokemon(210, 56, false, true), new Pokemon(219, 57, false, true), new Pokemon(9, 57, false, true), new Pokemon(196, 56, false, true), new Pokemon(79, 58, false, true)}, 400);
			trainers[242] = new Trainer("Scott 4", new Pokemon[]{new Pokemon(77, 59, false, true), new Pokemon(130, 60, false, true), new Pokemon(189, 58, false, true), new Pokemon(3, 60, false, true), new Pokemon(37, 59, false, true), new Pokemon(168, 61, false, true)}, 400, 21);
			trainers[344] = new Trainer("Fred 4", new Pokemon[]{new Pokemon(196, 77, false, true), new Pokemon(210, 76, false, true), new Pokemon(219, 77, false, true), new Pokemon(9, 77, false, true), new Pokemon(100, 76, false, true), new Pokemon(79, 78, false, true)}, 400, 27);
			
			setAbility("Scott 1", 1, Ability.ROUGH_SKIN);
			setAbility("Fred 1", 2, Ability.PROTEAN);
			setAbility("Scott 2", 2, Ability.ROUGH_SKIN);
			setAbility("Fred 2", 3, Ability.PROTEAN);
			setAbility("Scott 3", 3, Ability.ROUGH_SKIN);
			setAbility("Fred 3", 4, Ability.PROTEAN);
			setAbility("Scott 4", 4, Ability.ROUGH_SKIN);
			setAbility("Fred 4", 4, Ability.PROTEAN);
			
			setItem("Scott 1", 1, Item.MIRACLE_SEED);
			setItem("Fred 1", 2, Item.MYSTIC_WATER);
		}
		else if (gp.player.p.starter == 3) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(4, 7, false, true)}, 400, scottItem, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(2, 20, false, true)}, 400, fredItem);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(5, 22, false, true), new Pokemon(166, 22, false, true)}, 400, Item.HM02, 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(2, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Scott 3", new Pokemon[]{new Pokemon(77, 47, false, true), new Pokemon(130, 47, false, true), new Pokemon(6, 47, false, true), new Pokemon(37, 46, false, true), new Pokemon(167, 48, false, true)}, 400, 15);
			trainers[216] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(100, 56, false, true), new Pokemon(210, 56, false, true), new Pokemon(219, 57, false, true), new Pokemon(3, 57, false, true), new Pokemon(196, 56, false, true), new Pokemon(79, 58, false, true)}, 400);
			trainers[242] = new Trainer("Scott 4", new Pokemon[]{new Pokemon(77, 59, false, true), new Pokemon(130, 60, false, true), new Pokemon(189, 58, false, true), new Pokemon(6, 60, false, true), new Pokemon(37, 59, false, true), new Pokemon(168, 61, false, true)}, 400, 21);
			trainers[344] = new Trainer("Fred 4", new Pokemon[]{new Pokemon(196, 77, false, true), new Pokemon(210, 76, false, true), new Pokemon(219, 77, false, true), new Pokemon(3, 77, false, true), new Pokemon(100, 76, false, true), new Pokemon(79, 78, false, true)}, 400, 27);
			
			setAbility("Scott 1", 1, Ability.DRY_SKIN);
			setAbility("Fred 1", 2, Ability.ROUGH_SKIN);
			setAbility("Scott 2", 2, Ability.DRY_SKIN);
			setAbility("Fred 2", 3, Ability.ROUGH_SKIN);
			setAbility("Scott 3", 3, Ability.DRY_SKIN);
			setAbility("Fred 3", 4, Ability.ROUGH_SKIN);
			setAbility("Scott 4", 4, Ability.DRY_SKIN);
			setAbility("Fred 4", 4, Ability.ROUGH_SKIN);
			
			setItem("Scott 1", 1, Item.CHARCOAL);
			setItem("Fred 1", 2, Item.MIRACLE_SEED);
		}
		
		setItem("Rick 1", 1, Item.ORAN_BERRY);
		setItem("Rick 1", 2, Item.MAGNET);
		setItem("Rick 1", 3, Item.BIG_ROOT);
		setItem("Rick 1", 4, Item.OCCA_BERRY);
		
		setMoveset("1 Gym Leader 1", 2, Move.MEGA_DRAIN, Move.SUPERSONIC, Move.AERIAL_ACE, Move.POISON_FANG);
		setAbility("1 Gym Leader 1", 4, Ability.LIGHTNING_ROD);
		setMoveset("1 Gym Leader 1", 5, Move.CALM_MIND, Move.WISH, Move.MAGIC_BLAST, Move.GUST);
		setMoveset("1 Gym Leader 1", 6, Move.SAND_ATTACK, Move.LEER, Move.AERIAL_ACE, Move.QUICK_ATTACK);
		
		setItem("1 Gym Leader 1", 1, Item.BRIGHT_POWDER);
		setItem("1 Gym Leader 1", 2, Item.EXPERT_BELT);
		setItem("1 Gym Leader 1", 3, Item.LEFTOVERS);
		setItem("1 Gym Leader 1", 4, Item.ORAN_BERRY);
		setItem("1 Gym Leader 1", 5, Item.ENCHANTED_AMULET);
		setItem("1 Gym Leader 1", 6, Item.SCOPE_LENS);
		
		setItem("Fred 1", 1, Item.LEFTOVERS);
		
		setItem("Scott 2", 1, Item.FOCUS_SASH);
		setItem("Scott 2", 2, Item.SITRUS_BERRY);
		setItem("Scott 2", 3, Item.PASSHO_BERRY);
		
		setMoveset("2 Gym Leader 1", 1, Move.SUPER_FANG, Move.BITE, Move.HEADBUTT, Move.BULK_UP);
		setMoveset("2 Gym Leader 1", 2, Move.POWER$UP_PUNCH, Move.FURY_SWIPES, Move.DOUBLE_TEAM, Move.CHARM);
		setMoveset("2 Gym Leader 1", 3, Move.WILL$O$WISP, Move.HEX, Move.ROUND, Move.TAUNT);
		setMoveset("2 Gym Leader 1", 4, Move.SUPER_FANG, Move.METAL_CLAW, Move.FLAME_WHEEL, Move.BODY_SLAM);
		setMoveset("2 Gym Leader 1", 6, Move.HEADBUTT, Move.HYPNOSIS, Move.FEINT_ATTACK, Move.SWIFT);
		
		setItem("2 Gym Leader 1", 1, Item.EVIOLITE);
		setItem("2 Gym Leader 1", 2, Item.FOCUS_BAND);
		setItem("2 Gym Leader 1", 3, Item.COLBUR_BERRY);
		setItem("2 Gym Leader 1", 4, Item.MUSCLE_BAND);
		setItem("2 Gym Leader 1", 5, Item.STARF_BERRY);
		setItem("2 Gym Leader 1", 6, Item.EXPERT_BELT);
		
		setItem("Fred 2", 1, Item.PASSHO_BERRY);
		setItem("Fred 2", 2, Item.AIR_BALLOON);
		setItem("Fred 2", 3, Item.EXPERT_BELT);
		setItem("Fred 2", 4, Item.ROCKY_HELMET);
		setMoveset("Fred 2", 4, Move.PSYBEAM, Move.WATER_PULSE, Move.AQUA_VEIL, Move.CONFUSE_RAY);
		
		setMoveset("3 Gym Leader 1", 1, Move.FELL_STINGER, Move.IRON_TAIL, Move.STONE_EDGE, Move.STEALTH_ROCK);
		setAbility("3 Gym Leader 1", 1, Ability.COMPOUND_EYES);
		setMoveset("3 Gym Leader 1", 2, Move.STICKY_WEB, Move.THUNDERBOLT, Move.BUG_BUZZ, Move.THUNDER_WAVE);
		setMoveset("3 Gym Leader 1", 3, Move.LEAF_BLADE, Move.X$SCISSOR, Move.FELL_STINGER, Move.SWORDS_DANCE);
		setMoveset("3 Gym Leader 1", 4, Move.FIRST_IMPRESSION, Move.U$TURN, Move.CLOSE_COMBAT, Move.ROCK_SLIDE);
		setMoveset("3 Gym Leader 1", 5, Move.BUG_BITE, Move.MOONLIGHT, Move.FEINT_ATTACK, Move.FELL_STINGER);
		setMoveset("3 Gym Leader 1", 6, Move.AURORA_BEAM, Move.BUG_BUZZ, Move.SNOWSCAPE, Move.QUIVER_DANCE);
		
		setItem("3 Gym Leader 1", 1, Item.WIKI_BERRY);
		setItem("3 Gym Leader 1", 2, Item.WISE_GLASSES);
		setItem("3 Gym Leader 1", 3, Item.COBA_BERRY);
		setItem("3 Gym Leader 1", 4, Item.LIFE_ORB);
		setItem("3 Gym Leader 1", 5, Item.LUM_BERRY);
		setItem("3 Gym Leader 1", 6, Item.HEAVY$DUTY_BOOTS);
		
		setMoveset("AB1", 1, Move.HYPNOSIS, Move.PLAY_NICE, Move.MOONLIGHT, Move.HIDDEN_POWER);
		setMoveset("AC1", 1, Move.HYPNOSIS, Move.FAKE_TEARS, Move.NASTY_PLOT, Move.SNARL);
		
		setMoveset("4 Gym Leader 1", 1, Move.FREEZE$DRY, Move.SPACE_BEAM, Move.COSMIC_POWER, Move.WHIRLPOOL);
		setMoveset("4 Gym Leader 1", 2, Move.SNOWSCAPE, Move.AURORA_VEIL, Move.BLIZZARD, Move.SCALD);
		setMoveset("4 Gym Leader 1", 3, Move.AURORA_BOOST, Move.MAGIC_BLAST, Move.ICE_SHARD, Move.BLIZZARD);
		setMoveset("4 Gym Leader 1", 4, Move.PSYCHIC, Move.ICE_BEAM, Move.FREEZE$DRY, Move.CALM_MIND);
		setMoveset("4 Gym Leader 1", 5, Move.ICE_PUNCH, Move.CLOSE_COMBAT, Move.ICE_SHARD, Move.SWORDS_DANCE);
		setMoveset("4 Gym Leader 1", 6, Move.ICE_SPINNER, Move.COMET_CRASH, Move.BRAVE_BIRD, Move.HEAD_SMASH);
		setAbility("4 Gym Leader 1", 6, Ability.SLUSH_RUSH);
		
		setItem("4 Gym Leader 1", 1, Item.ICY_ROCK);
		setItem("4 Gym Leader 1", 2, Item.LIGHT_CLAY);
		setItem("4 Gym Leader 1", 3, Item.BRIGHT_POWDER);
		setItem("4 Gym Leader 1", 4, Item.HEAVY$DUTY_BOOTS);
		setItem("4 Gym Leader 1", 5, Item.FLAME_ORB);
		setItem("4 Gym Leader 1", 6, Item.CHOICE_BAND);
		
		setMoveset("Scott 3", 1, Move.MAGIC_REFLECT, Move.PSYCHIC, Move.MAGIC_TOMB, Move.CALM_MIND);
		setMoveset("Scott 3", 2, Move.SWORDS_DANCE, Move.LEECH_LIFE, Move.ACROBATICS, Move.POISON_JAB);
		setMoveset("Scott 3", 4, Move.VOLT_SWITCH, Move.GUILLOTINE, Move.BUG_BUZZ, Move.STICKY_WEB);
		
		setItem("Scott 3", 1, Item.QUICK_CLAW);
		setItem("Scott 3", 2, Item.FOCUS_SASH);
		setItem("Scott 3", 3, Item.LIFE_ORB);
		setItem("Scott 3", 4, Item.MAGNET);
		setItem("Scott 3", 5, Item.LEFTOVERS);
		
		setMoveset("5 Gym Leader 1", 1, Move.LIGHT_SCREEN, Move.REFLECT, Move.PSYCHO_CUT, Move.KNOCK_OFF);
		setMoveset("5 Gym Leader 1", 2, Move.PSYCHO_CUT, Move.SWORDS_DANCE, Move.PLAY_ROUGH, Move.CLOSE_COMBAT);
		setMoveset("5 Gym Leader 1", 3, Move.LEECH_SEED, Move.PSYSHOCK, Move.ENERGY_BALL, Move.DETECT);
		setMoveset("5 Gym Leader 1", 4, Move.HYDRO_PUMP, Move.CONFUSE_RAY, Move.PSYSHOCK, Move.FOCUS_BLAST);
		setMoveset("5 Gym Leader 1", 5, Move.ELEMENTAL_SPARKLE, Move.MAGIC_REFLECT, Move.TRICK_ROOM, Move.PSYSHOCK);
		setMoveset("5 Gym Leader 1", 6, Move.BULLET_PUNCH, Move.IRON_DEFENSE, Move.ZEN_HEADBUTT, Move.COMET_CRASH);
		setAbility("5 Gym Leader 1", 1, Ability.BRAINWASH);
		setAbility("5 Gym Leader 1", 6, Ability.LEVITATE);
		
		setItem("5 Gym Leader 1", 1, Item.LIGHT_CLAY);
		setItem("5 Gym Leader 1", 2, Item.LIFE_ORB);
		setItem("5 Gym Leader 1", 3, Item.LEFTOVERS);
		setItem("5 Gym Leader 1", 4, Item.BLUNDER_POLICY);
		setItem("5 Gym Leader 1", 5, Item.COLBUR_BERRY);
		setItem("5 Gym Leader 1", 6, Item.WIKI_BERRY);
		
		setMoveset("Fred 3", 1, Move.IRON_BLAST, Move.DRAGON_PULSE, Move.FLAMETHROWER, Move.HURRICANE);
		setMoveset("Fred 3", 3, Move.SUCKER_PUNCH, Move.BREAKING_SWIPE, Move.EARTHQUAKE, Move.SWORDS_DANCE);
		setMoveset("Fred 3", 5, Move.COSMIC_POWER, Move.STONE_EDGE, Move.METEOR_MASH, Move.COMET_CRASH);
		setMoveset("Fred 3", 6, Move.BLIZZARD, Move.LIGHT_SCREEN, Move.HYDRO_PUMP, Move.PSYCHIC);
		
		setItem("Fred 3", 1, Item.MYSTICOLA_BERRY);
		setItem("Fred 3", 2, Item.RINDO_BERRY);
		setItem("Fred 3", 3, Item.BLACK_GLASSES);
		setItem("Fred 3", 4, Item.EXPERT_BELT);
		setItem("Fred 3", 5, Item.SITRUS_BERRY);
		setItem("Fred 3", 6, Item.WEAKNESS_POLICY);
		
		setMoveset("Maxwell 1", 1, Move.LOAD_FIREARMS, Move.PISTOL_POP, Move.FLAMETHROWER, Move.ICE_BEAM);
		setMoveset("Maxwell 1", 2, Move.TOXIC, Move.CURSE, Move.SLUDGE_WAVE, Move.FALSE_SURRENDER);
		setMoveset("Maxwell 1", 3, Move.PHANTOM_FORCE, Move.SUCKER_PUNCH, Move.BREAKING_SWIPE, Move.UNSEEN_STRANGLE);
		setMoveset("Maxwell 1", 6, Move.MAGIC_TOMB, Move.COMET_CRASH, Move.MOONLIGHT, Move.EARTHQUAKE);
		
		setItem("Maxwell 1", 1, Item.SCOPE_LENS);
		setItem("Maxwell 1", 2, Item.BLACK_SLUDGE);
		setItem("Maxwell 1", 3, Item.FOCUS_SASH);
		setItem("Maxwell 1", 4, Item.LEFTOVERS);
		setItem("Maxwell 1", 5, Item.COBA_BERRY);
		setItem("Maxwell 1", 6, Item.ENCHANTED_AMULET);
		
		setItem("Rick 2", 1, Item.LUM_BERRY);
		setItem("Rick 2", 2, Item.SITRUS_BERRY);
		setItem("Rick 2", 3, Item.SHUCA_BERRY);
		setItem("Rick 2", 4, Item.KEBIA_BERRY);
		setItem("Rick 2", 5, Item.CLEAR_AMULET);
		setItem("Rick 2", 6, Item.ENCHANTED_AMULET);
		
		setMoveset("Rick 2", 1, Move.HEAD_SMASH, Move.PLASMA_FISTS, Move.SUPERPOWER, Move.STEALTH_ROCK);
		setMoveset("Rick 2", 2, Move.STONE_EDGE, Move.TOXIC, Move.PHANTOM_FORCE, Move.PROTECT);
		setMoveset("Rick 2", 3, Move.BULLET_PUNCH, Move.U$TURN, Move.VOLT_TACKLE, Move.EARTHQUAKE);
		setMoveset("Rick 2", 4, Move.SCALD, Move.GIGA_DRAIN, Move.RAIN_DANCE, Move.ICE_BEAM);
		setMoveset("Rick 2", 5, Move.FEINT_ATTACK, Move.BUG_BITE, Move.EXTREME_SPEED, Move.AERIAL_ACE);
		setMoveset("Rick 2", 6, Move.SPIKY_SHIELD, Move.IRON_HEAD, Move.MAGIC_CRASH, Move.SPARKLE_STRIKE);
		
		setAbility("Rick 2", 4, Ability.RAIN_DISH);
		
		setMoveset("Scott 4", 1, Move.MAGIC_TOMB, Move.NUZZLE, Move.REFLECT, Move.LIGHT_SCREEN);
		setMoveset("Scott 4", 2, Move.SWORDS_DANCE, Move.LEECH_LIFE, Move.BATON_PASS, Move.PROTECT);
		setMoveset("Scott 4", 3, Move.PRISMATIC_LASER, Move.DRAGON_PULSE, Move.THUNDERBOLT, Move.REST);
		setMoveset("Scott 4", 5, Move.PHOTON_GEYSER, Move.BUG_BUZZ, Move.VOLT_SWITCH, Move.STICKY_WEB);
		setMoveset("Scott 4", 6, Move.EARTHQUAKE, Move.SPIKES, Move.SMACK_DOWN, Move.BODY_PRESS);
		
		setItem("Scott 4", 1, Item.CHILAN_BERRY);
		setItem("Scott 4", 2, Item.FOCUS_SASH);
		setItem("Scott 4", 3, Item.CHESTO_BERRY);
		setItem("Scott 4", 4, Item.LIFE_ORB);
		setItem("Scott 4", 5, Item.MAGNET);
		setItem("Scott 4", 6, Item.LEFTOVERS);
		
		setMoveset("6 Gym Leader 1", 1, Move.PSYCHIC, Move.MOONBLAST, Move.GLITTER_DANCE, Move.WILL$O$WISP);
		setMoveset("6 Gym Leader 1", 2, Move.EARTH_POWER, Move.GLITTER_DANCE, Move.MOONBLAST, Move.OVERHEAT);
		setMoveset("6 Gym Leader 1", 3, Move.SPIRIT_BREAK, Move.DRILL_RUN, Move.SWORDS_DANCE, Move.ICE_SPINNER);
		setAbility("6 Gym Leader 1", 3, Ability.TOUGH_CLAWS);
		setMoveset("6 Gym Leader 1", 4, Move.MORNING_SUN, Move.AEROBLAST, Move.PRISMATIC_LASER, Move.DEFOG);
		setMoveset("6 Gym Leader 1", 5, Move.ENERGY_BALL, Move.CHARGE_BEAM, Move.THUNDER_WAVE, Move.ICE_BEAM);
		setMoveset("6 Gym Leader 1", 6, Move.GLITTERING_SWORD, Move.ZING_ZAP, Move.SHELL_SMASH, Move.OVERHEAT);
		
		setItem("6 Gym Leader 1", 1, Item.FOCUS_SASH);
		setItem("6 Gym Leader 1", 2, Item.WHITE_HERB);
		setItem("6 Gym Leader 1", 3, Item.EXPERT_BELT);
		setItem("6 Gym Leader 1", 4, Item.HEAVY$DUTY_BOOTS);
		setItem("6 Gym Leader 1", 5, Item.TANGA_BERRY);
		setItem("6 Gym Leader 1", 6, Item.WHITE_HERB);
		
		
		setMoveset("CCA", 1, Move.METRONOME, null, null, null);
		setMoveset("CCA", 2, Move.METRONOME, null, null, null);
		setMoveset("CCA", 3, Move.METRONOME, null, null, null);
		
		setMoveset("Exp. Trainer", 1, Move.SPLASH, null, null, null);
		setMoveset("Exp. Trainer", 2, Move.SPLASH, null, null, null);
		setMoveset("Exp. Trainer", 3, Move.SPLASH, null, null, null);
		setMoveset("Exp. Trainer", 4, Move.SPLASH, null, null, null);
		setMoveset("Exp. Trainer", 5, Move.SPLASH, null, null, null);
		setMoveset("Exp. Trainer", 6, Move.SPLASH, null, null, null);
		
		setMoveset("7 Gym Leader 1", 1, Move.IRON_HEAD, Move.SPIKY_SHIELD, Move.U$TURN, Move.EARTHQUAKE);
		setAbility("7 Gym Leader 1", 1, Ability.IRON_BARBS);
		setMoveset("7 Gym Leader 1", 2, Move.DEEP_SEA_BUBBLE, Move.SEA_DRAGON, Move.SUPERCHARGED_SPLASH, Move.RAIN_DANCE);
		setMoveset("7 Gym Leader 1", 3, Move.PRISMATIC_LASER, Move.ELEMENTAL_SPARKLE, Move.GEOMANCY, Move.FIERY_DANCE);
		setMoveset("7 Gym Leader 1", 4, Move.FREEZE$DRY, Move.CALM_MIND, Move.BLUE_FLARE, Move.VANISHING_ACT);
		setMoveset("7 Gym Leader 1", 5, Move.TOXIC, Move.SPIKES, Move.ELEMENTAL_SPARKLE, Move.PSYCHIC);
		setMoveset("7 Gym Leader 1", 6, Move.SPECTRAL_THIEF, Move.FLARE_BLITZ, Move.VOLT_TACKLE, Move.SWITCHEROO);
		
		setItem("7 Gym Leader 1", 1, Item.LEFTOVERS);
		setItem("7 Gym Leader 1", 2, Item.WHITE_HERB);
		setItem("7 Gym Leader 1", 3, Item.POWER_HERB);
		setItem("7 Gym Leader 1", 4, Item.LEFTOVERS);
		setItem("7 Gym Leader 1", 5, Item.WIKI_BERRY);
		setItem("7 Gym Leader 1", 6, Item.CHOICE_BAND);
		
		setItem("Rick 3", 1, Item.SILVER_POWDER);
		setAbility("Rick 3", 1, Ability.TERRIFY, true);
		setItem("Rick 3", 2, Item.WIDE_LENS);
		setItem("Rick 3", 3, Item.AIR_BALLOON);
		setItem("Rick 3", 4, Item.CHOICE_SPECS);
		setItem("Rick 3", 5, Item.BIG_ROOT);
		setItem("Rick 3", 6, Item.POWER_HERB);
		
		setMoveset("Rick 3", 1, Move.U$TURN, Move.KNOCK_OFF, Move.MOONLIGHT, Move.FIRST_IMPRESSION);
		setMoveset("Rick 3", 2, Move.STONE_EDGE, Move.WILL$O$WISP, Move.COMET_CRASH, Move.ROCK_POLISH);
		setMoveset("Rick 3", 3, Move.BULLET_PUNCH, Move.COIL, Move.THUNDER_PUNCH, Move.EARTHQUAKE);
		setMoveset("Rick 3", 4, Move.SCALD, Move.GIGA_DRAIN, Move.U$TURN, Move.ICE_BEAM);
		setMoveset("Rick 3", 5, Move.HEAD_SMASH, Move.DRAIN_PUNCH, Move.NO_RETREAT, Move.STEALTH_ROCK);
		setMoveset("Rick 3", 6, Move.SPIKY_SHIELD, Move.IRON_HEAD, Move.MAGIC_CRASH, Move.HONE_CLAWS);
		
		setItem("Fred 4", 1, Item.LOADED_DICE);
		setAbility("Fred 4", 1, Ability.HUGE_POWER);
		setItem("Fred 4", 2, Item.WEAKNESS_POLICY);
		setItem("Fred 4", 3, Item.ROCKY_HELMET);
		setItem("Fred 4", 4, Item.SITRUS_BERRY);
		setItem("Fred 4", 5, Item.CHOICE_SCARF);
		setItem("Fred 4", 6, Item.LEFTOVERS);
		
		setMoveset("Fred 4", 1, Move.SHOOTING_STARS, Move.ROCK_BLAST, Move.DRAIN_PUNCH, Move.BULK_UP);
		setMoveset("Fred 4", 2, Move.DRAGON_DANCE, Move.WATERFALL, Move.VOLT_TACKLE, Move.BREAKING_SWIPE);
		setMoveset("Fred 4", 3, Move.GLARE, Move.BREAKING_SWIPE, Move.EARTH_POWER, Move.DARK_PULSE);
		setMoveset("Fred 4", 5, Move.ERUPTION, Move.DRACO_METEOR, Move.VOLT_SWITCH, Move.DARK_PULSE);
		setMoveset("Fred 4", 6, Move.AQUA_VEIL, Move.SCALD, Move.PSYCHIC, Move.CALM_MIND);
		
		setItem("Maxwell 2", 1, Item.BLUNDER_POLICY);
		setItem("Maxwell 2", 2, Item.CHOICE_SPECS);
		setItem("Maxwell 2", 3, Item.HEAVY$DUTY_BOOTS);
		setAbility("Maxwell 3", 3, Ability.PRANKSTER);
		setItem("Maxwell 2", 4, Item.LOADED_DICE);
		setAbility("Maxwell 3", 4, Ability.DEFIANT);
		setItem("Maxwell 2", 5, Item.COBA_BERRY);
		setItem("Maxwell 2", 6, Item.WEAKNESS_POLICY);
		
		setMoveset("Maxwell 2", 1, Move.SHIFT_GEAR, Move.POP_POP, Move.ACROBATICS, Move.EARTHQUAKE);
		setMoveset("Maxwell 2", 2, Move.DARK_PULSE, Move.STAR_STORM, Move.SLUDGE_WAVE, Move.SWITCHEROO);
		setMoveset("Maxwell 2", 3, Move.SPECTRAL_THIEF, Move.THUNDER_WAVE, Move.PARTING_SHOT, Move.UNSEEN_STRANGLE);
		setMoveset("Maxwell 2", 4, Move.SCALE_SHOT, Move.JAW_LOCK, Move.OBSTRUCT, Move.FATAL_BIND);
		setMoveset("Maxwell 2", 5, Move.STAR$STRUCK_ARROW, Move.DRAIN_PUNCH, Move.NO_RETREAT, Move.METEOR_MASH);
		setMoveset("Maxwell 2", 6, Move.TERRAIN_PULSE, Move.SPACIAL_REND, Move.SUPERNOVA_EXPLOSION, Move.MAGIC_REFLECT);
	}

	private static void setTrainers() {
		
		modifiedTrainers = new ArrayList<>();
		trainers = new Trainer[]{
				new Trainer("Scott 1", new Pokemon[]{new Pokemon(1, 100, false, true)}, 400),
				new Trainer("A", new Pokemon[]{new Pokemon(16, 5, false, true)}, new Item[]{Item.ORAN_BERRY}, 100),
				new Trainer("B", new Pokemon[]{new Pokemon(13, 4, false, true)}, new Item[]{Item.ORAN_BERRY}, 100),
				new Trainer("C", new Pokemon[]{new Pokemon(32, 5, false, true), new Pokemon(29, 4, false, true)}, new Item[]{Item.OCCA_BERRY, Item.OCCA_BERRY}, 100),
				new Trainer("D", new Pokemon[]{new Pokemon(22, 2, false, true), new Pokemon(22, 3, false, true), new Pokemon(22, 4, false, true)}, 100),
				new Trainer("TN 1", new Pokemon[]{new Pokemon(22, 9, false, true)}, new Item[]{Item.COBA_BERRY}, 100), // 5
				new Trainer("TN 2", new Pokemon[]{new Pokemon(22, 9, false, true)}, new Item[]{Item.OCCA_BERRY}, 100),
				new Trainer("TN 3", new Pokemon[]{new Pokemon(29, 10, false, true)}, new Item[]{Item.BIG_ROOT}, 100),
				new Trainer("TN 4", new Pokemon[]{new Pokemon(59, 11, false, true)}, new Item[]{Item.CHERI_BERRY}, 100),
				new Trainer("TN 5", new Pokemon[]{new Pokemon(41, 10, false, true)}, new Item[]{Item.COBA_BERRY}, 100),
				new Trainer("TN 6", new Pokemon[]{new Pokemon(73, 10, false, true)}, new Item[]{Item.RAWST_BERRY}, 100), // 10
				new Trainer("TN 7", new Pokemon[]{new Pokemon(90, 10, false, true)}, new Item[]{Item.TANGA_BERRY}, 100),
				new Trainer("TN 8", new Pokemon[]{new Pokemon(52, 12, false, true)}, new Item[]{Item.TWISTED_SPOON}, 100),
				new Trainer("Rick 1", new Pokemon[]{new Pokemon(66, 11, false, true), new Pokemon(111, 12, false, true), new Pokemon(44, 13, false, true), new Pokemon(120, 13, false, true)}, 400, Item.HM01, 2),
				new Trainer("1 Gym A", new Pokemon[]{new Pokemon(13, 11, false, true), new Pokemon(13, 12, false, true), new Pokemon(13, 13, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.ORAN_BERRY, Item.CHERI_BERRY}, 200),
				new Trainer("1 Gym B", new Pokemon[]{new Pokemon(10, 12, false, true), new Pokemon(10, 13, false, true)}, new Item[]{Item.ORAN_BERRY, Item.CHARTI_BERRY}, 200), // 15
				new Trainer("1 Gym C", new Pokemon[]{new Pokemon(13, 13, false, true), new Pokemon(10, 14, false, true)}, new Item[]{Item.SHARP_BEAK, Item.GANLON_BERRY}, 200),
				new Trainer("1 Gym Leader 1", new Pokemon[]{new Pokemon(10, 16, false, true), new Pokemon(153, 16, false, true), new Pokemon(135, 16, false, true), new Pokemon(226, 16, false, true), new Pokemon(177, 15, false, true), new Pokemon(14, 17, false, true)}, 500, Item.TM52),
				new Trainer("E", new Pokemon[]{new Pokemon(19, 16, false, true), new Pokemon(158, 16, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.ROSELI_BERRY}, 100),
				new Trainer("F", new Pokemon[]{new Pokemon(32, 8, false, true), new Pokemon(32, 9, false, true)}, new Item[]{Item.COBA_BERRY, Item.SILVER_POWDER}, 100),
				new Trainer("G", new Pokemon[]{new Pokemon(13, 10, false, true)}, new Item[]{Item.SILK_SCARF}, 100), // 20
				new Trainer("H", new Pokemon[]{new Pokemon(26, 10, false, true)}, new Item[]{Item.SALAC_BERRY}, 100),
				new Trainer("I", new Pokemon[]{new Pokemon(47, 10, false, true), new Pokemon(55, 11, false, true)}, new Item[]{Item.FOCUS_SASH, Item.RINDO_BERRY}, 100),
				new Trainer("J1", new Pokemon[]{new Pokemon(85, 15, false, true), new Pokemon(10, 16, false, true)}, new Item[]{Item.TANGA_BERRY, Item.PETAYA_BERRY}, 100),
				new Trainer("K", new Pokemon[]{new Pokemon(129, 17, false, true), new Pokemon(38, 18, false, true)}, new Item[]{Item.ORAN_BERRY, Item.LEFTOVERS}, 100),
				new Trainer("L", new Pokemon[]{new Pokemon(19, 18, false, true)}, new Item[]{Item.SITRUS_BERRY}, 100), // 25
				new Trainer("M", new Pokemon[]{new Pokemon(141, 18, false, true)}, new Item[]{Item.RINDO_BERRY}, 100),
				new Trainer("N", new Pokemon[]{new Pokemon(42, 18, false, true)}, new Item[]{Item.COBA_BERRY}, 100),
				new Trainer("O1", new Pokemon[]{new Pokemon(209, 19, false, true), new Pokemon(143, 18, false, true)}, new Item[]{Item.MAGNET, Item.FOCUS_SASH}, 100),
				new Trainer("O2", new Pokemon[]{new Pokemon(137, 19, false, true), new Pokemon(139, 18, false, true)}, new Item[]{Item.CHOICE_BAND, Item.EXPERT_BELT}, 100),
				new Trainer("P", new Pokemon[]{new Pokemon(26, 18, false, true)}, new Item[]{Item.HARD_STONE}, 100), // 30
				new Trainer("Q", new Pokemon[]{new Pokemon(90, 18, false, true), new Pokemon(82, 19, false, true)}, new Item[]{Item.BLACK_GLASSES, Item.TERRAIN_EXTENDER}, 100),
				new Trainer("R", new Pokemon[]{new Pokemon(64, 19, false, true)}, new Item[]{Item.EXPERT_BELT}, 100),
				new Trainer("S", new Pokemon[]{new Pokemon(137, 19, false, true), new Pokemon(137, 19, false, true), new Pokemon(137, 19, false, true), new Pokemon(71, 19, false, true)}, new Item[]{Item.RINDO_BERRY, Item.RINDO_BERRY, Item.RINDO_BERRY, Item.RINDO_BERRY}, 100),
				new Trainer("Fred 1", new Pokemon[]{new Pokemon(1, 7, false, true)}, 400),
				new Trainer("T", new Pokemon[]{new Pokemon(42, 16, false, true), new Pokemon(52, 17, false, true)}, new Item[]{Item.FOCUS_SASH, Item.PASSHO_BERRY}, 100), // 35
				new Trainer("U1", new Pokemon[]{new Pokemon(104, 15, false, true), new Pokemon(94, 15, false, true)}, new Item[]{Item.EXPERT_BELT, null}, 100),
				new Trainer("U2", new Pokemon[]{new Pokemon(106, 15, false, true), new Pokemon(94, 15, false, true)}, new Item[]{Item.CHILAN_BERRY, null}, 100),
				new Trainer("V", new Pokemon[]{new Pokemon(85, 17, false, true), new Pokemon(82, 16, false, true), new Pokemon(80, 17, false, true), new Pokemon(75, 18, false, true)}, new Item[]{Item.RINDO_BERRY, Item.TWISTED_SPOON, Item.ORAN_BERRY, Item.COLBUR_BERRY}, 100),
				new Trainer("W", new Pokemon[]{new Pokemon(138, 20, false, true)}, new Item[]{Item.FOCUS_BAND}, 100),
				new Trainer("EP-AA", new Pokemon[]{new Pokemon(90, 22, false, true)}, new Item[]{Item.ROSELI_BERRY}, 100), // 40
				new Trainer("EP-AB", new Pokemon[]{new Pokemon(36, 21, false, true), new Pokemon(82, 21, false, true), new Pokemon(80, 21, false, true)}, new Item[]{Item.RAWST_BERRY, Item.CHESTO_BERRY, Item.TERRAIN_EXTENDER}, 100),
				new Trainer("EP-AC", new Pokemon[]{new Pokemon(114, 20, false, true)}, new Item[]{Item.RINDO_BERRY}, 100),
				new Trainer("EP-AD", new Pokemon[]{new Pokemon(181, 22, false, true)}, new Item[]{Item.METAL_COAT}, 100),
				new Trainer("EP-AE", new Pokemon[]{new Pokemon(126, 21, false, true), new Pokemon(123, 22, false, true), new Pokemon(120, 21, false, true)}, new Item[]{Item.FOCUS_BAND, Item.FOCUS_BAND, Item.FOCUS_BAND}, 100),
				new Trainer("EP-BA", new Pokemon[]{new Pokemon(138, 21, false, true), new Pokemon(138, 21, false, true)}, new Item[] {Item.CHERI_BERRY, Item.RAWST_BERRY}, 100), // 45
				new Trainer("EP-BB", new Pokemon[]{new Pokemon(148, 22, false, true)}, new Item[]{Item.RINDO_BERRY}, 100),
				new Trainer("EP-BC", new Pokemon[]{new Pokemon(139, 22, false, true), new Pokemon(140, 22, false, true)}, new Item[]{Item.COBA_BERRY, null}, 100),
				new Trainer("EP-BD", new Pokemon[]{new Pokemon(144, 21, false, true)}, new Item[]{Item.MYSTICOLA_BERRY}, 100),
				new Trainer("TN O1", new Pokemon[]{new Pokemon(23, 19, false, true)}, new Item[]{Item.POISON_BARB}, 100),
				new Trainer("TN O2", new Pokemon[]{new Pokemon(73, 19, false, true)}, new Item[]{Item.LUM_BERRY}, 100), // 50
				new Trainer("TN O3", new Pokemon[]{new Pokemon(95, 19, false, true)}, new Item[]{Item.GALAXEED_BERRY}, 100),
				new Trainer("TN O4", new Pokemon[]{new Pokemon(104, 19, false, true)}, new Item[]{Item.SALAC_BERRY}, 100),
				new Trainer("TN O5", new Pokemon[]{new Pokemon(52, 20, false, true), new Pokemon(141, 20, false, true)}, new Item[]{Item.WHITE_HERB, Item.RAWST_BERRY}, 100),
				new Trainer("TN O6", new Pokemon[]{new Pokemon(90, 20, false, true), new Pokemon(144, 20, false, true)}, new Item[]{Item.LIFE_ORB, null}, 100, 3),
				new Trainer("Scott 2", new Pokemon[]{new Pokemon(10, 5, false, true)}, 100), // 55
				new Trainer("2 Gym A", new Pokemon[]{new Pokemon(16, 22, false, true)}, new Item[]{Item.MUSCLE_BAND}, 200),
				new Trainer("2 Gym B", new Pokemon[]{new Pokemon(16, 22, false, true)}, new Item[]{Item.RAWST_BERRY}, 200),
				new Trainer("2 Gym C", new Pokemon[]{new Pokemon(14, 20, false, true)}, new Item[]{Item.CHERI_BERRY}, 200),
				new Trainer("2 Gym D", new Pokemon[]{new Pokemon(14, 20, false, true)}, new Item[]{Item.CHARTI_BERRY}, 200),
				new Trainer("2 Gym E", new Pokemon[]{new Pokemon(20, 21, false, true)}, new Item[]{Item.CHILAN_BERRY}, 200), // 60
				new Trainer("2 Gym F", new Pokemon[]{new Pokemon(20, 21, false, true)}, new Item[]{Item.SILK_SCARF}, 200),
				new Trainer("2 Gym G", new Pokemon[]{new Pokemon(19, 22, false, true), new Pokemon(19, 23, false, true)}, new Item[]{Item.ORAN_BERRY, null}, 200),
				new Trainer("2 Gym H", new Pokemon[]{new Pokemon(19, 22, false, true), new Pokemon(19, 23, false, true)}, new Item[]{null, Item.SITRUS_BERRY}, 200),
				new Trainer("2 Gym I", new Pokemon[]{new Pokemon(16, 22, false, true)}, new Item[]{Item.WEAKNESS_POLICY}, 200),
				new Trainer("2 Gym J", new Pokemon[]{new Pokemon(14, 22, false, true)}, new Item[]{Item.SHARP_BEAK}, 200), // 65
				new Trainer("2 Gym Leader 1", new Pokemon[]{new Pokemon(16, 24, false, true), new Pokemon(126, 24, false, true), new Pokemon(179, 24, false, true), new Pokemon(92, 24, false, true), new Pokemon(89, 24, false, true), new Pokemon(20, 25, false, true)}, 500, Item.TM01),
				new Trainer("SA", new Pokemon[]{new Pokemon(151, 8, false, true)}, new Item[]{Item.ORAN_BERRY}, 100),
				new Trainer("SB", new Pokemon[]{new Pokemon(44, 9, false, true)}, new Item[]{Item.BIG_ROOT}, 100),
				new Trainer("SC", new Pokemon[]{new Pokemon(35, 9, false, true)}, new Item[]{Item.ORAN_BERRY}, 100),
				new Trainer("SD", new Pokemon[]{new Pokemon(153, 8, false, true)}, new Item[]{Item.EXPERT_BELT}, 100), // 70
				new Trainer("X", new Pokemon[]{new Pokemon(132, 24, false, true), new Pokemon(163, 25, false, true)}, new Item[]{Item.LEFTOVERS, Item.CUSTAP_BERRY}, 100),
				new Trainer("Y", new Pokemon[]{new Pokemon(11, 24, false, true), new Pokemon(134, 27, false, true)}, new Item[]{Item.CHARTI_BERRY, Item.LEFTOVERS}, 100),
				new Trainer("Z", new Pokemon[]{new Pokemon(105, 27, false, true)}, new Item[]{Item.BRIGHT_POWDER}, 100),
				new Trainer("AA", new Pokemon[]{new Pokemon(131, 29, false, true)}, new Item[]{Item.CHOICE_BAND}, 100),
				new Trainer("MS A", new Pokemon[]{new Pokemon(61, 29, false, true)}, new Item[]{Item.EXPERT_BELT}, 100), // 75
				new Trainer("MS B", new Pokemon[]{new Pokemon(92, 29, false, true), new Pokemon(160, 30, false, true)}, new Item[]{Item.PASSHO_BERRY, Item.PAYAPA_BERRY}, 100),
				new Trainer("MS C", new Pokemon[]{new Pokemon(115, 30, false, true)}, new Item[]{Item.SOFT_SAND}, 100),
				new Trainer("MS D", new Pokemon[]{new Pokemon(155, 51, false, true), new Pokemon(161, 53, false, true)}, new Item[] {Item.POWER_HERB, Item.EVIOLITE}, 100),
				new Trainer("TN MS 1", new Pokemon[]{new Pokemon(53, 30, false, true)}, new Item[]{Item.RAWST_BERRY}, 100, Item.HM03),
				new Trainer("AB", new Pokemon[]{new Pokemon(86, 28, false, true), new Pokemon(82, 30, false, true), new Pokemon(86, 29, false, true), new Pokemon(75, 30, false, true), new Pokemon(79, 30, false, true)}, new Item[]{Item.RINDO_BERRY, Item.TERRAIN_EXTENDER, Item.TANGA_BERRY, Item.COLBUR_BERRY, Item.FOCUS_BAND}, 100), // 80
				new Trainer("AC", new Pokemon[]{new Pokemon(49, 30, false, true), new Pokemon(53, 31, false, true)}, new Item[]{Item.CHOICE_BAND, Item.FOCUS_BAND}, 100),
				new Trainer("AD", new Pokemon[]{new Pokemon(98, 30, false, true), new Pokemon(102, 31, false, true), new Pokemon(105, 31, false, true)}, new Item[]{Item.PASSHO_BERRY, Item.SITRUS_BERRY, Item.APICOT_BERRY}, 100),
				new Trainer("AE", new Pokemon[]{new Pokemon(96, 37, false, true)}, new Item[]{Item.GALAXEED_BERRY}, 100),
				new Trainer("AF", new Pokemon[]{new Pokemon(110, 38, false, true)}, new Item[]{Item.PASSHO_BERRY}, 100),
				new Trainer("AG", new Pokemon[]{new Pokemon(73, 29, false, true), new Pokemon(91, 30, false, true)}, new Item[]{Item.CHOICE_BAND, Item.TANGA_BERRY}, 100), // 85
				new Trainer("AH", new Pokemon[]{new Pokemon(97, 30, false, true)}, new Item[]{Item.LUM_BERRY}, 100),
				new Trainer("AI", new Pokemon[]{new Pokemon(89, 30, false, true), new Pokemon(47, 31, false, true)}, new Item[]{Item.SALAC_BERRY, Item.LEFTOVERS}, 100),
				new Trainer("AJ", new Pokemon[]{new Pokemon(46, 30, false, true), new Pokemon(27, 31, false, true)}, new Item[]{Item.DAMP_ROCK, Item.ASSAULT_VEST}, 100),
				new Trainer("Fred 2", new Pokemon[]{new Pokemon(10, 18, false, true)}, 500),
				new Trainer("3 Gym A", new Pokemon[]{new Pokemon(33, 29, false, true), new Pokemon(73, 29, false, true), new Pokemon(33, 30, false, true), new Pokemon(34, 31, false, true)}, new Item[]{Item.COBA_BERRY, Item.FOCUS_SASH, Item.OCCA_BERRY, Item.CHOICE_BAND}, 200), // 90
				new Trainer("3 Gym B", new Pokemon[]{new Pokemon(23, 30, false, true), new Pokemon(36, 31, false, true), new Pokemon(42, 32, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.LUM_BERRY, Item.FOCUS_BAND}, 200),
				new Trainer("3 Gym C", new Pokemon[]{new Pokemon(62, 31, false, true), new Pokemon(63, 31, false, true)}, new Item[]{null, Item.CHARTI_BERRY}, 200),
				new Trainer("3 Gym D", new Pokemon[]{new Pokemon(23, 31, false, true), new Pokemon(25, 31, false, true)}, new Item[]{Item.LEFTOVERS, Item.LEFTOVERS}, 200),
				new Trainer("3 Gym Leader 1", new Pokemon[]{new Pokemon(25, 31, false, true), new Pokemon(37, 32, false, true), new Pokemon(34, 32, false, true), new Pokemon(42, 32, false, true), new Pokemon(74, 32, false, true), new Pokemon(63, 33, false, true)}, 500, Item.TM22),
				new Trainer("RR A", new Pokemon[]{new Pokemon(71, 30, false, true), new Pokemon(68, 31, false, true)}, new Item[]{Item.LIFE_ORB, Item.SILK_SCARF}, 100), // 95
				new Trainer("RR B", new Pokemon[]{new Pokemon(27, 30, false, true), new Pokemon(30, 31, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.BLACK_SLUDGE}, 100),
				new Trainer("RR C", new Pokemon[]{new Pokemon(108, 30, false, true), new Pokemon(107, 31, false, true)}, new Item[]{Item.CHARCOAL, Item.CHILAN_BERRY}, 100),
				new Trainer("CR", new Pokemon[]{new Pokemon(86, 18, false, true), new Pokemon(45, 19, false, true)}, new Item[]{Item.TWISTED_SPOON, Item.EXPERT_BELT}, 100),
				new Trainer("CS", new Pokemon[]{new Pokemon(36, 20, false, true)}, new Item[]{Item.OCCA_BERRY}, 100),
				new Trainer("CT", new Pokemon[]{new Pokemon(68, 20, false, true)}, new Item[]{Item.SITRUS_BERRY}, 100), // 100
				new Trainer("CU", new Pokemon[]{new Pokemon(42, 20, false, true)}, new Item[]{Item.FOCUS_SASH}, 100),
				new Trainer("E1", new Pokemon[]{new Pokemon(134, 5, false, true), new Pokemon(117, 6, false, true)}, new Item[]{Item.ORAN_BERRY, Item.MAGNET}, 100),
				new Trainer("K1", new Pokemon[]{new Pokemon(1, 15, false, true), new Pokemon(4, 15, false, true), new Pokemon(7, 15, false, true)}, new Item[]{Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER}, 100),
				new Trainer("AB1", new Pokemon[]{new Pokemon(156, 20, false, true)}, new Item[]{Item.LEFTOVERS}, 100),
				new Trainer("AC1", new Pokemon[]{new Pokemon(158, 20, false, true)}, new Item[]{Item.ROSELI_BERRY}, 100), // 105
				new Trainer("AG", new Pokemon[]{new Pokemon(15, 34, false, true), new Pokemon(31, 34, false, true)}, new Item[]{Item.WACAN_BERRY, Item.BLACK_SLUDGE}, 100),
				new Trainer("AH", new Pokemon[]{new Pokemon(171, 35, false, true), new Pokemon(172, 36, false, true)}, new Item[]{Item.FOCUS_BAND, Item.BRIGHT_POWDER}, 100),
				new Trainer("AI", new Pokemon[]{new Pokemon(57, 35, false, true), new Pokemon(121, 35, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.ROCKY_HELMET}, 100),
				new Trainer("AJ", new Pokemon[]{new Pokemon(83, 35, false, true), new Pokemon(76, 35, false, true), new Pokemon(80, 36, false, true), new Pokemon(87, 34, false, true)}, new Item[]{Item.TERRAIN_EXTENDER, Item.FOCUS_BAND, Item.LEFTOVERS, Item.BRIGHT_POWDER}, 100),
				new Trainer("AK", new Pokemon[]{new Pokemon(138, 35, false, true), new Pokemon(150, 36, false, true)}, new Item[]{Item.MYSTIC_WATER, Item.ROCKY_HELMET}, 100), // 110
				new Trainer("AL", new Pokemon[]{new Pokemon(160, 36, false, true), new Pokemon(164, 36, false, true), new Pokemon(34, 36, false, true)}, new Item[]{Item.KING1S_ROCK, Item.QUICK_CLAW, Item.SCOPE_LENS}, 100),
				new Trainer("AM", new Pokemon[]{new Pokemon(154, 36, false, true), new Pokemon(140, 35, false, true), new Pokemon(170, 35, false, true)}, new Item[]{Item.KING1S_ROCK, Item.WEAKNESS_POLICY, Item.CLEAR_AMULET}, 100),
				new Trainer("AN", new Pokemon[]{new Pokemon(102, 35, false, true), new Pokemon(95, 35, false, true), new Pokemon(96, 36, false, true), new Pokemon(175, 37, false, true)}, new Item[]{Item.LOADED_DICE, Item.FOCUS_BAND, Item.CHOICE_BAND, Item.COBA_BERRY}, 100),
				new Trainer("AO", new Pokemon[]{new Pokemon(132, 37, false, true), new Pokemon(143, 40, false, true), new Pokemon(135, 37, false, true), new Pokemon(133, 37, false, true)}, new Item[]{Item.WIKI_BERRY, Item.FOCUS_BAND, Item.SITRUS_BERRY, Item.WHITE_HERB}, 100),
				new Trainer("AP", new Pokemon[]{new Pokemon(127, 36, false, true), new Pokemon(164, 37, false, true), new Pokemon(182, 36, false, true), new Pokemon(180, 37, false, true)}, new Item[]{Item.BLACK_BELT, Item.COVERT_CLOAK, Item.OCCA_BERRY, Item.ROSELI_BERRY}, 100), // 115
				new Trainer("AQ", new Pokemon[]{new Pokemon(177, 38, false, true), new Pokemon(193, 39, false, true), new Pokemon(185, 37, false, true), new Pokemon(12, 37, false, true)}, new Item[]{Item.CLEAR_AMULET, Item.CHARTI_BERRY, Item.ENCHANTED_AMULET, Item.RED_CARD}, 100),
				new Trainer("TN S1", new Pokemon[]{new Pokemon(60, 36, false, true), new Pokemon(37, 37, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.ROCKY_HELMET}, 100),
				new Trainer("TN S2", new Pokemon[]{new Pokemon(31, 36, false, true), new Pokemon(74, 37, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.SILVER_POWDER}, 100),
				new Trainer("TN S3", new Pokemon[]{new Pokemon(154, 36, false, true), new Pokemon(152, 37, false, true)}, new Item[]{Item.EXPERT_BELT, Item.LUM_BERRY}, 100),
				new Trainer("TN S4", new Pokemon[]{new Pokemon(160, 36, false, true), new Pokemon(159, 37, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.ROSELI_BERRY}, 100), // 120
				new Trainer("TN S5", new Pokemon[]{new Pokemon(91, 37, false, true), new Pokemon(97, 37, false, true)}, new Item[]{Item.BLACK_GLASSES, Item.AIR_BALLOON}, 100),
				new Trainer("TN S6", new Pokemon[]{new Pokemon(146, 38, false, true), new Pokemon(225, 37, false, true)}, new Item[]{Item.RINDO_BERRY, Item.ASSAULT_VEST}, 100),
				new Trainer("TN S7", new Pokemon[]{new Pokemon(83, 37, false, true), new Pokemon(87, 38, false, true)}, new Item[]{Item.TERRAIN_EXTENDER, Item.FOCUS_BAND}, 100),
				new Trainer("TN S8", new Pokemon[]{new Pokemon(39, 38, false, true), new Pokemon(27, 38, false, true)}, new Item[]{Item.KEBIA_BERRY, Item.MUSCLE_BAND}, 100),
				new Trainer("TN S9", new Pokemon[]{new Pokemon(148, 38, false, true), new Pokemon(149, 38, false, true)}, new Item[]{Item.FOCUS_BAND, Item.BRIGHT_POWDER}, 100), // 125
				new Trainer("TN S10", new Pokemon[]{new Pokemon(164, 39, false, true), new Pokemon(144, 38, false, true)}, new Item[]{Item.QUICK_CLAW, Item.ASSAULT_VEST}, 100, 9),
				new Trainer("TN S11", new Pokemon[]{new Pokemon(159, 37, false, true), new Pokemon(97, 37, false, true)}, new Item[]{Item.ROSELI_BERRY, Item.ROCKY_HELMET}, 100),
				new Trainer("TN S12", new Pokemon[]{new Pokemon(176, 37, false, true), new Pokemon(182, 38, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.METAL_COAT}, 100),
				new Trainer("TN S13", new Pokemon[]{new Pokemon(24, 38, false, true), new Pokemon(74, 37, false, true)}, new Item[]{Item.TOXIC_ORB, Item.LUM_BERRY}, 100),
				new Trainer("TN S14", new Pokemon[]{new Pokemon(56, 37, false, true), new Pokemon(25, 38, false, true)}, new Item[]{Item.KING1S_ROCK, Item.ROCKY_HELMET}, 100), // 130
				new Trainer("TN S15", new Pokemon[]{new Pokemon(105, 39, false, true), new Pokemon(99, 38, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.CHARTI_BERRY}, 100),
				new Trainer("TN S16", new Pokemon[]{new Pokemon(141, 38, false, true), new Pokemon(142, 37, false, true)}, new Item[]{Item.CHOICE_BAND, Item.POWER_HERB}, 100, 8),
				new Trainer("4 Gym A", new Pokemon[]{new Pokemon(63, 37, false, true), new Pokemon(63, 38, false, true), new Pokemon(69, 39, false, true), new Pokemon(69, 40, false, true)}, new Item[]{Item.OCCA_BERRY, Item.CHARTI_BERRY, Item.CHESTO_BERRY, Item.WIDE_LENS}, 200),
				new Trainer("4 Gym B", new Pokemon[]{new Pokemon(65, 40, false, true)}, new Item[]{Item.CLEAR_AMULET}, 200),
				new Trainer("4 Gym C", new Pokemon[]{new Pokemon(59, 40, false, true), new Pokemon(60, 39, false, true), new Pokemon(61, 40, false, true), new Pokemon(60, 39, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.ICY_ROCK, Item.LIGHT_CLAY, Item.FOCUS_BAND}, 200), // 135
				new Trainer("4 Gym D", new Pokemon[]{new Pokemon(63, 41, false, true)}, new Item[]{Item.FOCUS_SASH}, 200),
				new Trainer("4 Gym Leader 1", new Pokemon[]{new Pokemon(193, 41, false, true), new Pokemon(69, 41, false, true), new Pokemon(61, 41, false, true), new Pokemon(60, 41, false, true), new Pokemon(65, 40, false, true), new Pokemon(194, 42, false, true)}, 500, Item.TM60),
				new Trainer("AR", new Pokemon[]{new Pokemon(15, 40, false, true), new Pokemon(60, 40, false, true), new Pokemon(15, 40, false, true), new Pokemon(12, 40, false, true), new Pokemon(178, 40, false, true), new Pokemon(203, 40, false, true)}, new Item[]{Item.WACAN_BERRY, Item.NEVER$MELT_ICE, Item.ROCKY_HELMET, Item.HEAVY$DUTY_BOOTS, Item.CHILAN_BERRY, Item.WIKI_BERRY}, 100),
				new Trainer("AS", new Pokemon[]{new Pokemon(91, 42, false, true), new Pokemon(79, 43, false, true), new Pokemon(140, 41, false, true), new Pokemon(136, 42, false, true)}, new Item[]{Item.BLACK_GLASSES, Item.WIKI_BERRY, Item.EXPERT_BELT, Item.LEFTOVERS}, 100),
				new Trainer("AT", new Pokemon[]{new Pokemon(138, 41, false, true), new Pokemon(138, 42, false, true), new Pokemon(136, 43, false, true), new Pokemon(149, 42, false, true)}, new Item[]{Item.WACAN_BERRY, Item.CHARTI_BERRY, Item.DAMP_ROCK, Item.RINDO_BERRY}, 100), // 140
				new Trainer("AU", new Pokemon[]{new Pokemon(155, 43, false, true), new Pokemon(125, 43, false, true), new Pokemon(119, 42, false, true)}, new Item[]{Item.EXPERT_BELT, Item.AIR_BALLOON, Item.ASSAULT_VEST}, 100),
				new Trainer("BVA", new Pokemon[]{new Pokemon(126, 12, false, true), new Pokemon(120, 12, false, true)}, new Item[]{Item.LOADED_DICE, null}, 100),
				new Trainer("BVB", new Pokemon[]{new Pokemon(123, 12, false, true), new Pokemon(126, 12, false, true)}, new Item[]{Item.RAWST_BERRY, null}, 100),
				new Trainer("BVC", new Pokemon[]{new Pokemon(120, 12, false, true), new Pokemon(123, 12, false, true)}, new Item[]{Item.PERSIM_BERRY, null}, 100),
				new Trainer("BVD", new Pokemon[]{new Pokemon(126, 14, false, true), new Pokemon(120, 14, false, true), new Pokemon(123, 14, false, true)}, new Item[]{Item.BRIGHT_POWDER, null, null}, 100), // 145
				new Trainer("O3", new Pokemon[]{new Pokemon(178, 28, false, true), new Pokemon(187, 29, false, true), new Pokemon(144, 28, false, true)}, new Item[]{Item.CHILAN_BERRY, Item.BABIRI_BERRY, Item.HABAN_BERRY}, 100),
				new Trainer("CCA", new Pokemon[]{new Pokemon(174, 30, false, true), new Pokemon(175, 30, false, true), new Pokemon(176, 28, false, true)}, new Item[]{Item.FOCUS_SASH, Item.FOCUS_BAND, Item.LEFTOVERS}, 100),
				new Trainer("CCB", new Pokemon[]{new Pokemon(179, 29, false, true), new Pokemon(171, 30, false, true), new Pokemon(169, 28, false, true)}, new Item[]{Item.FOCUS_BAND, Item.SOFT_SAND, Item.KING1S_ROCK}, 100),
				new Trainer("AV", new Pokemon[]{new Pokemon(69, 42, false, true), new Pokemon(69, 43, false, true), new Pokemon(70, 44, false, true)}, new Item[]{Item.LIGHT_CLAY, Item.WIDE_LENS, Item.SITRUS_BERRY}, 100),
				new Trainer("AW", new Pokemon[]{new Pokemon(45, 44, false, true), new Pokemon(72, 43, false, true), new Pokemon(46, 42, false, true)}, new Item[]{Item.KEBIA_BERRY, Item.RINDO_BERRY, Item.EXPERT_BELT}, 100), // 150
				new Trainer("AX", new Pokemon[]{new Pokemon(136, 43, false, true), new Pokemon(79, 44, false, true)}, new Item[]{Item.DAMP_ROCK, Item.LEFTOVERS}, 100),
				new Trainer("AY", new Pokemon[]{new Pokemon(91, 43, false, true), new Pokemon(147, 44, false, true)}, new Item[]{Item.FOCUS_SASH, Item.WHITE_HERB}, 100),
				new Trainer("AZ", new Pokemon[]{new Pokemon(136, 44, false, true), new Pokemon(133, 44, false, true), new Pokemon(139, 45, false, true)}, new Item[]{Item.MYSTICOLA_BERRY, Item.MYSTIC_WATER, Item.ROCKY_HELMET}, 100),
				new Trainer("BA", new Pokemon[]{new Pokemon(141, 44, false, true), new Pokemon(144, 44, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.CHOICE_SCARF}, 100),
				new Trainer("BB", new Pokemon[]{new Pokemon(143, 50, false, true), new Pokemon(143, 50, false, true), new Pokemon(143, 50, false, true), new Pokemon(143, 50, false, true), new Pokemon(143, 50, false, true), new Pokemon(145, 44, false, true)}, new Item[]{Item.FOCUS_SASH, Item.FOCUS_BAND, Item.BRIGHT_POWDER, Item.QUICK_CLAW, Item.POWER_HERB, Item.POWER_HERB}, 100), // 155
				new Trainer("BC", new Pokemon[]{new Pokemon(138, 44, false, true), new Pokemon(210, 44, false, true)}, new Item[]{Item.WACAN_BERRY, Item.AIR_BALLOON}, 100),
				new Trainer("BD", new Pokemon[]{new Pokemon(210, 44, false, true), new Pokemon(138, 44, false, true)}, new Item[]{Item.POWER_HERB, Item.LIFE_ORB}, 100),
				new Trainer("BE", new Pokemon[]{new Pokemon(210, 43, false, true), new Pokemon(119, 44, false, true), new Pokemon(113, 44, false, true)}, new Item[]{Item.MAGNET, Item.LEFTOVERS, Item.AIR_BALLOON}, 100),
				new Trainer("BF", new Pokemon[]{new Pokemon(39, 45, false, true)}, new Item[]{Item.SITRUS_BERRY}, 100),
				new Trainer("BG", new Pokemon[]{new Pokemon(40, 45, false, true)}, new Item[]{Item.TANGA_BERRY}, 100), // 160
				new Trainer("BH", new Pokemon[]{new Pokemon(160, 45, false, true), new Pokemon(161, 44, false, true), new Pokemon(160, 45, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.QUICK_CLAW, Item.ROSELI_BERRY}, 100),
				new Trainer("BI", new Pokemon[]{new Pokemon(165, 44, false, true), new Pokemon(127, 45, false, true), new Pokemon(128, 43, false, true)}, new Item[]{Item.FLAME_ORB, Item.FLAME_ORB, Item.WIKI_BERRY}, 100),
				new Trainer("BJ", new Pokemon[]{new Pokemon(167, 46, false, true), new Pokemon(170, 43, false, true), new Pokemon(172, 45, false, true)}, new Item[]{Item.SMOOTH_ROCK, Item.EXPERT_BELT, Item.EVIOLITE}, 100),
				new Trainer("BK", new Pokemon[]{new Pokemon(109, 45, false, true), new Pokemon(97, 45, false, true)}, new Item[]{Item.HEAT_ROCK, Item.LEFTOVERS}, 100),
				new Trainer("BL", new Pokemon[]{new Pokemon(195, 46, false, true), new Pokemon(219, 45, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.SMOOTH_ROCK}, 100), // 165
				new Trainer("BM", new Pokemon[]{new Pokemon(220, 47, false, true), new Pokemon(221, 45, false, true), new Pokemon(220, 47, false, true), new Pokemon(221, 46, false, true)}, new Item[]{Item.FOCUS_SASH, Item.SITRUS_BERRY, Item.ROSELI_BERRY, Item.SALAC_BERRY}, 100),
				new Trainer("BN", new Pokemon[]{new Pokemon(148, 47, false, true), new Pokemon(149, 45, false, true), new Pokemon(215, 47, false, true), new Pokemon(216, 45, false, true)}, new Item[]{Item.CHOICE_SPECS, Item.CHOICE_BAND, Item.EVIOLITE, Item.POWER_HERB}, 100),
				new Trainer("BO", new Pokemon[]{new Pokemon(12, 48, false, true)}, new Item[]{Item.BRIGHT_POWDER}, 100),
				new Trainer("BP", new Pokemon[]{new Pokemon(74, 46, false, true), new Pokemon(34, 46, false, true)}, new Item[]{Item.CLEAR_AMULET, Item.COBA_BERRY}, 100),
				new Trainer("BQ", new Pokemon[]{new Pokemon(58, 46, false, true), new Pokemon(77, 46, false, true), new Pokemon(88, 47, false, true), new Pokemon(140, 45, false, true), new Pokemon(145, 46, false, true)}, new Item[]{Item.CHOICE_SCARF, Item.CHILAN_BERRY, Item.MUSCLE_BAND, Item.PETAYA_BERRY, Item.ASSAULT_VEST}, 100), // 170
				new Trainer("BR", new Pokemon[]{new Pokemon(165, 46, false, true), new Pokemon(225, 47, false, true), new Pokemon(67, 48, false, true)}, new Item[]{Item.BLACK_GLASSES, Item.ROCKY_HELMET, Item.LEFTOVERS}, 100),
				new Trainer("BS1", new Pokemon[]{new Pokemon(56, 47, false, true), new Pokemon(50, 46, false, true)}, new Item[]{Item.FOCUS_SASH, Item.COVERT_CLOAK}, 100),
				new Trainer("BS2", new Pokemon[]{new Pokemon(93, 47, false, true), new Pokemon(51, 46, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.CUSTAP_BERRY}, 100),
				new Trainer("BT", new Pokemon[]{new Pokemon(167, 48, false, true), new Pokemon(170, 46, false, true), new Pokemon(178, 47, false, true), new Pokemon(170, 47, false, true)}, new Item[]{Item.SMOOTH_ROCK, Item.LIFE_ORB, Item.QUICK_CLAW, Item.FOCUS_SASH}, 100),
				new Trainer("BU", new Pokemon[]{new Pokemon(37, 45, false, true), new Pokemon(21, 46, false, true)}, new Item[]{Item.KASIB_BERRY, Item.CHOICE_SCARF}, 100), // 175
				new Trainer("BV", new Pokemon[]{new Pokemon(87, 46, false, true), new Pokemon(77, 45, false, true)}, new Item[]{Item.FOCUS_SASH, Item.FOCUS_BAND}, 100),
				new Trainer("BW1", new Pokemon[]{new Pokemon(96, 45, false, true), new Pokemon(43, 44, false, true)}, new Item[]{Item.GALAXEED_BERRY, Item.ASSAULT_VEST}, 100),
				new Trainer("BW2", new Pokemon[]{new Pokemon(225, 45, false, true), new Pokemon(34, 45, false, true)}, new Item[]{Item.FLAME_ORB, Item.EXPERT_BELT}, 100),
				new Trainer("BX", new Pokemon[]{new Pokemon(115, 46, false, true), new Pokemon(124, 44, false, true), new Pokemon(119, 45, false, true)}, new Item[]{Item.FOCUS_BAND, Item.ASSAULT_VEST, Item.ROCKY_HELMET}, 100),
				new Trainer("BY", new Pokemon[]{new Pokemon(157, 48, false, true), new Pokemon(25, 48, false, true), new Pokemon(46, 47, false, true)}, new Item[]{Item.LEFTOVERS, Item.ROCKY_HELMET, Item.WISE_GLASSES}, 100), // 180
				new Trainer("BZ", new Pokemon[]{new Pokemon(113, 48, false, true), new Pokemon(116, 48, false, true), new Pokemon(122, 48, false, true)}, new Item[]{Item.SHUCA_BERRY, Item.AIR_BALLOON, Item.LUM_BERRY}, 100),
				new Trainer("CA", new Pokemon[]{new Pokemon(180, 46, false, true), new Pokemon(180, 47, false, true), new Pokemon(180, 48, false, true)}, new Item[]{Item.LIFE_ORB, Item.FOCUS_BAND, Item.ROSELI_BERRY}, 100),
				new Trainer("GTN1", new Pokemon[]{new Pokemon(147, 49, false, true), new Pokemon(96, 48, false, true), new Pokemon(31, 49, false, true)}, new Item[]{Item.CHOICE_BAND, Item.CHOICE_BAND, Item.SITRUS_BERRY}, 100),
				new Trainer("GTN2", new Pokemon[]{new Pokemon(165, 49, false, true), new Pokemon(207, 48, false, true), new Pokemon(208, 48, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.AIR_BALLOON, Item.WHITE_HERB}, 100),
				new Trainer("Scott 3", new Pokemon[]{new Pokemon(10, 5, false, true)}, 500), // 185
				new Trainer("5 Gym A", new Pokemon[]{new Pokemon(83, 49, false, true), new Pokemon(83, 49, false, true), new Pokemon(83, 50, false, true)}, new Item[]{Item.QUICK_CLAW, Item.GANLON_BERRY, Item.FOCUS_BAND}, 200),
				new Trainer("5 Gym B", new Pokemon[]{new Pokemon(77, 49, false, true)}, new Item[]{Item.COLBUR_BERRY}, 200),
				new Trainer("5 Gym C", new Pokemon[]{new Pokemon(53, 48, false, true), new Pokemon(54, 49, false, true)}, new Item[]{Item.CLEAR_AMULET, Item.LUM_BERRY}, 200),
				new Trainer("5 Gym D", new Pokemon[]{new Pokemon(87, 49, false, true), new Pokemon(88, 49, false, true)}, new Item[]{Item.FOCUS_SASH, Item.QUICK_CLAW}, 200),
				new Trainer("5 Gym E", new Pokemon[]{new Pokemon(60, 49, false, true), new Pokemon(60, 50, false, true)}, new Item[]{Item.LIFE_ORB, Item.CHOICE_SCARF}, 200), // 190
				new Trainer("5 Gym F", new Pokemon[]{new Pokemon(79, 49, false, true), new Pokemon(53, 50, false, true)}, new Item[]{Item.BLUNDER_POLICY, Item.QUICK_CLAW}, 200),
				new Trainer("5 Gym G", new Pokemon[]{new Pokemon(91, 50, false, true)}, new Item[]{Item.WIKI_BERRY}, 200),
				new Trainer("5 Gym H", new Pokemon[]{new Pokemon(81, 49, false, true), new Pokemon(91, 49, false, true)}, new Item[]{Item.TERRAIN_EXTENDER, Item.TANGA_BERRY}, 200),
				new Trainer("5 Gym Leader 1", new Pokemon[]{new Pokemon(91, 51, false, true), new Pokemon(88, 51, false, true), new Pokemon(81, 51, false, true), new Pokemon(79, 51, false, true), new Pokemon(77, 51, false, true), new Pokemon(84, 52, false, true)}, 500, Item.TM11, 17),
				new Trainer("DA", new Pokemon[]{new Pokemon(33, 45, false, true), new Pokemon(34, 44, false, true), new Pokemon(34, 45, false, true)}, new Item[]{Item.WIDE_LENS, Item.EXPERT_BELT, Item.POISON_BARB}, 200), // 195
				new Trainer("DB", new Pokemon[]{new Pokemon(36, 46, false, true), new Pokemon(37, 45, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.SALAC_BERRY}, 100),
				new Trainer("DC", new Pokemon[]{new Pokemon(24, 46, false, true)}, new Item[]{Item.TOXIC_ORB}, 100),
				new Trainer("DD", new Pokemon[]{new Pokemon(25, 46, false, true)}, new Item[]{Item.HARD_STONE}, 100),
				new Trainer("DE", new Pokemon[]{new Pokemon(43, 46, false, true)}, new Item[]{Item.FOCUS_BAND}, 100),
				new Trainer("DF", new Pokemon[]{new Pokemon(30, 46, false, true), new Pokemon(31, 45, false, true)}, new Item[]{Item.EVIOLITE, Item.BRIGHT_POWDER}, 100), // 200
				new Trainer("DG", new Pokemon[]{new Pokemon(39, 45, false, true), new Pokemon(40, 45, false, true)}, new Item[]{Item.BIG_ROOT, Item.LEFTOVERS}, 100),
				new Trainer("CY", new Pokemon[]{new Pokemon(24, 43, false, true), new Pokemon(25, 43, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.LIFE_ORB}, 100),
				new Trainer("CZ", new Pokemon[]{new Pokemon(33, 45, false, true), new Pokemon(36, 46, false, true), new Pokemon(33, 46, false, true), new Pokemon(34, 44, false, true), new Pokemon(37, 44, false, true)}, new Item[]{Item.WIDE_LENS, Item.SCOPE_LENS, Item.OCCA_BERRY, Item.FOCUS_SASH, Item.WISE_GLASSES}, 100),
				new Trainer("TN B1", new Pokemon[]{new Pokemon(173, 50, false, true), new Pokemon(225, 50, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.FLAME_ORB}, 100),
				new Trainer("TN B2", new Pokemon[]{new Pokemon(219, 50, false, true), new Pokemon(96, 50, false, true)}, new Item[]{Item.FOCUS_BAND, Item.BLACK_GLASSES}, 100), // 205
				new Trainer("TN B3", new Pokemon[]{new Pokemon(74, 51, false, true), new Pokemon(180, 51, false, true)}, new Item[]{Item.CHOICE_BAND, Item.SPELL_TAG}, 100),
				new Trainer("TN B4", new Pokemon[]{new Pokemon(91, 51, false, true), new Pokemon(105, 51, false, true)}, new Item[]{Item.LEFTOVERS, Item.SALAC_BERRY}, 100),
				new Trainer("TN B5", new Pokemon[]{new Pokemon(142, 52, false, true), new Pokemon(147, 52, false, true)}, new Item[]{Item.POWER_HERB, Item.BLUNDER_POLICY}, 100),
				new Trainer("TN B6", new Pokemon[]{new Pokemon(138, 52, false, true), new Pokemon(149, 52, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.LIFE_ORB}, 100),
				new Trainer("TN B7", new Pokemon[]{new Pokemon(152, 53, false, true), new Pokemon(165, 53, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.FLAME_ORB}, 100), // 210
				new Trainer("TN B8", new Pokemon[]{new Pokemon(155, 53, false, true), new Pokemon(227, 53, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.ASSAULT_VEST}, 100),
				new Trainer("TN B9", new Pokemon[]{new Pokemon(168, 54, false, true), new Pokemon(67, 54, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.ROCKY_HELMET}, 100),
				new Trainer("TN B10", new Pokemon[]{new Pokemon(65, 54, false, true), new Pokemon(201, 54, false, true)}, new Item[]{Item.CHOICE_BAND, Item.LIFE_ORB}, 100),
				new Trainer("TN B11", new Pokemon[]{new Pokemon(207, 55, false, true), new Pokemon(210, 55, false, true)}, new Item[]{Item.AIR_BALLOON, Item.CHOICE_SCARF}, 100),
				new Trainer("TN B12", new Pokemon[]{new Pokemon(208, 55, false, true), new Pokemon(198, 55, false, true)}, new Item[]{Item.WHITE_HERB, Item.CHOICE_SCARF}, 100), // 215
				new Trainer("Fred 3", new Pokemon[]{new Pokemon(10, 5, false, true)}, 500),
				new Trainer("Maxwell 1", new Pokemon[]{new Pokemon(183, 58, false, true), new Pokemon(222, 58, false, true), new Pokemon(216, 58, false, true), new Pokemon(214, 59, false, true), new Pokemon(192, 60, false, true), new Pokemon(186, 60, false, true)}, 400, 20),
				new Trainer("CB", new Pokemon[]{new Pokemon(27, 52, false, true), new Pokemon(28, 51, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.DRAGON_FANG}, 100),
				new Trainer("CC", new Pokemon[]{new Pokemon(101, 80, false, true), new Pokemon(102, 60, false, true), new Pokemon(103, 52, false, true), new Pokemon(143, 60, false, true), new Pokemon(143, 60, false, true), new Pokemon(145, 52, false, true)}, new Item[]{Item.WEAKNESS_POLICY, Item.LOADED_DICE, Item.PASSHO_BERRY, Item.POWER_HERB, Item.QUICK_CLAW, Item.POWER_HERB}, 100),
				new Trainer("CD", new Pokemon[]{new Pokemon(209, 100, false, true), new Pokemon(119, 53, false, true)}, new Item[]{Item.CHOICE_BAND, Item.WIKI_BERRY}, 100), // 220
				new Trainer("CE", new Pokemon[]{new Pokemon(125, 52, false, true), new Pokemon(128, 52, false, true), new Pokemon(122, 52, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.CLEAR_AMULET, Item.LUM_BERRY}, 100),
				new Trainer("CF", new Pokemon[]{new Pokemon(133, 53, false, true), new Pokemon(131, 57, false, true)}, new Item[]{Item.WHITE_HERB, Item.FOCUS_SASH}, 100),
				new Trainer("CG", new Pokemon[]{new Pokemon(150, 53, false, true), new Pokemon(136, 53, false, true), new Pokemon(147, 53, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.COVERT_CLOAK, Item.MUSCLE_BAND}, 100),
				new Trainer("Grust 1", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 2", new Pokemon[]{new Pokemon(159, 65, true)}, 0), // 225
				new Trainer("Grust 3", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 4", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 5", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 6", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 7", new Pokemon[]{new Pokemon(159, 65, true)}, 0), // 230
				new Trainer("Grust 8", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 9", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Grust 10", new Pokemon[]{new Pokemon(159, 65, true)}, 0),
				new Trainer("Rick 2", new Pokemon[]{new Pokemon(67, 55, false, true), new Pokemon(162, 54, false, true), new Pokemon(113, 54, false, true), new Pokemon(46, 55, false, true), new Pokemon(74, 55, false, true), new Pokemon(122, 56, false, true)}, 400, 19),
				new Trainer("CH", new Pokemon[]{new Pokemon(51, 54, false, true), new Pokemon(97, 55, false, true), new Pokemon(18, 54, false, true), new Pokemon(54, 54, false, true)}, new Item[]{Item.RINDO_BERRY, Item.AIR_BALLOON, Item.ASSAULT_VEST, Item.CHOICE_SCARF}, 100), // 235
				new Trainer("CI", new Pokemon[]{new Pokemon(93, 55, false, true), new Pokemon(88, 54, false, true), new Pokemon(107, 55, false, true)}, new Item[]{Item.LIFE_ORB, Item.LIFE_ORB, Item.LIFE_ORB}, 100),
				new Trainer("CJ", new Pokemon[]{new Pokemon(31, 55, false, true), new Pokemon(155, 55, false, true), new Pokemon(160, 57, false, true)}, new Item[]{Item.SITRUS_BERRY, Item.POWER_HERB, Item.BRIGHT_POWDER}, 100),
				new Trainer("CK", new Pokemon[]{new Pokemon(170, 54, false, true), new Pokemon(173, 55, false, true)}, new Item[]{Item.LIFE_ORB, Item.CLEAR_AMULET}, 100),
				new Trainer("CL", new Pokemon[]{new Pokemon(208, 56, false, true)}, new Item[]{Item.AIR_BALLOON}, 100),
				new Trainer("CM", new Pokemon[]{new Pokemon(105, 55, false, true), new Pokemon(107, 56, false, true), new Pokemon(6, 56, false, true)}, new Item[]{Item.FOCUS_SASH, Item.KEBIA_BERRY, Item.SHUCA_BERRY}, 100), // 240
				new Trainer("CN", new Pokemon[]{new Pokemon(157, 56, false, true), new Pokemon(180, 57, false, true), new Pokemon(214, 57, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.FOCUS_SASH, Item.LEFTOVERS}, 100),
				new Trainer("Scott 4", new Pokemon[]{new Pokemon(10, 5, false, true)}, 500),
				new Trainer("CO", new Pokemon[]{new Pokemon(113, 57, false, true), new Pokemon(119, 58, false, true), new Pokemon(116, 57, false, true), new Pokemon(201, 57, false, true), new Pokemon(125, 57, false, true)}, new Item[]{Item.FOCUS_BAND, Item.QUICK_CLAW, Item.LIECHI_BERRY, Item.MUSCLE_BAND, Item.ENCHANTED_AMULET}, 100),
				new Trainer("CP", new Pokemon[]{new Pokemon(165, 58, false, true), new Pokemon(128, 58, false, true), new Pokemon(191, 58, false, true)}, new Item[]{Item.FLAME_ORB, Item.CLEAR_AMULET, Item.EVIOLITE}, 100),
				new Trainer("CQ", new Pokemon[]{new Pokemon(100, 58, false, true), new Pokemon(189, 58, false, true), new Pokemon(28, 58, false, true)}, new Item[]{Item.CUSTAP_BERRY, Item.HEAVY$DUTY_BOOTS, Item.WHITE_HERB}, 100), // 245
				new Trainer("6 Gym A", new Pokemon[]{new Pokemon(51, 59, false, true), new Pokemon(51, 60, false, true), new Pokemon(51, 60, false, true)}, new Item[]{Item.BRIGHT_POWDER, Item.WHITE_HERB, Item.RINDO_BERRY}, 200),
				new Trainer("6 Gym B", new Pokemon[]{new Pokemon(208, 61, false, true)}, new Item[]{Item.AIR_BALLOON}, 200),
				new Trainer("6 Gym C", new Pokemon[]{new Pokemon(124, 60, false, true), new Pokemon(127, 60, false, true), new Pokemon(121, 60, false, true)}, new Item[]{Item.FOCUS_SASH, Item.FLAME_ORB, Item.MUSCLE_BAND}, 200),
				new Trainer("6 Gym D", new Pokemon[]{new Pokemon(87, 61, false, true)}, new Item[]{Item.FOCUS_SASH}, 200),
				new Trainer("6 Gym E", new Pokemon[]{new Pokemon(12, 59, false, true), new Pokemon(12, 60, false, true)}, new Item[]{Item.SHARP_BEAK, Item.GLOWING_PRISM}, 200), // 250
				new Trainer("6 Gym F", new Pokemon[]{new Pokemon(47, 61, false, true), new Pokemon(72, 60, false, true)}, new Item[]{Item.FOCUS_SASH, Item.RINDO_BERRY}, 200),
				new Trainer("6 Gym G", new Pokemon[]{new Pokemon(89, 61, false, true), new Pokemon(170, 60, false, true)}, new Item[]{Item.SALAC_BERRY, Item.LIFE_ORB}, 200),
				new Trainer("6 Gym H", new Pokemon[]{new Pokemon(40, 60, false, true), new Pokemon(110, 60, false, true)}, new Item[]{Item.WHITE_HERB, Item.WHITE_HERB}, 200),
				new Trainer("6 Gym I", new Pokemon[]{new Pokemon(176, 61, false, true), new Pokemon(189, 60, false, true)}, new Item[]{Item.LEFTOVERS, Item.ASSAULT_VEST}, 200),
				new Trainer("6 Gym Leader 1", new Pokemon[]{new Pokemon(87, 62, false, true), new Pokemon(110, 62, false, true), new Pokemon(170, 62, false, true), new Pokemon(12, 62, false, true), new Pokemon(40, 62, false, true), new Pokemon(208, 63, false, true)}, 500, Item.TM97), // 255
				new Trainer("Exp. Trainer", new Pokemon[]{new Pokemon(137, 50, false, true), new Pokemon(137, 50, false, true), new Pokemon(137, 50, false, true), new Pokemon(137, 50, false, true), new Pokemon(137, 50, false, true), new Pokemon(137, 50, false, true)}, new Item[]{Item.TOXIC_ORB, Item.TOXIC_ORB, Item.TOXIC_ORB, Item.TOXIC_ORB, Item.TOXIC_ORB, Item.TOXIC_ORB}, 100),
				new Trainer("BP1", new Pokemon[] {new Pokemon(17, 48, false, true), new Pokemon(21, 47, false, true), new Pokemon(93, 48, false, true), new Pokemon(89, 48, false, true), new Pokemon(161, 47, false, true), new Pokemon(188, 48, false, true)}, new Item[]{Item.CHOPLE_BERRY, Item.LIFE_ORB, Item.EXPERT_BELT, Item.STARF_BERRY, Item.SPELL_TAG, Item.WIKI_BERRY}, 100),
				new Trainer("TN 9", new Pokemon[] {new Pokemon(153, 11, false, true), new Pokemon(160, 11, false, true)}, new Item[]{Item.BIG_ROOT, Item.CHERI_BERRY}, 100),
				new Trainer("B A", new Pokemon[] {new Pokemon(16, 14, false, true)}, 100),
				new Trainer("B B", new Pokemon[] {new Pokemon(30, 14, false, true)}, 100), // 260
				new Trainer("B C", new Pokemon[] {new Pokemon(57, 14, false, true)}, 100),
				new Trainer("B D", new Pokemon[] {new Pokemon(114, 14, false, true)}, 100),
				new Trainer("B E", new Pokemon[] {new Pokemon(174, 14, false, true), new Pokemon(71, 11, false, true)}, new Item[]{Item.ORAN_BERRY, Item.CHESTO_BERRY}, 100),
				new Trainer("YA", new Pokemon[]{new Pokemon(101, 9, false, true)}, new Item[]{Item.PASSHO_BERRY}, 100),
				new Trainer("YB", new Pokemon[]{new Pokemon(184, 8, false, true), new Pokemon(190, 9, false, true)}, new Item[]{Item.CHILAN_BERRY, Item.COBA_BERRY}, 100), // 265
				new Trainer("B F", new Pokemon[]{new Pokemon(47, 21, false, true)}, new Item[]{Item.CHILAN_BERRY}, 100),
				new Trainer("MS E", new Pokemon[]{new Pokemon(193, 31, false, true), new Pokemon(64, 31, false, true)}, new Item[]{Item.ROSELI_BERRY, Item.SITRUS_BERRY}, 100),
				new Trainer("MS F", new Pokemon[]{new Pokemon(60, 30, false, true), new Pokemon(72, 31, false, true)}, new Item[]{Item.ICY_ROCK, Item.POWER_HERB}, 100),
				new Trainer("B G", new Pokemon[]{new Pokemon(190, 31, false, true), new Pokemon(195, 30, false, true)}, new Item[]{Item.CHARTI_BERRY, Item.COVERT_CLOAK}, 100),
				new Trainer("SCT A", new Pokemon[]{new Pokemon(155, 47, false, true), new Pokemon(142, 48, false, true), new Pokemon(191, 49, false, true), new Pokemon(157, 48, false, true), new Pokemon(150, 49, false, true), new Pokemon(131, 49, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.POWER_HERB, Item.EVIOLITE, Item.OCCA_BERRY, Item.SITRUS_BERRY, Item.FOCUS_BAND}, 100), // 270
				new Trainer("SCT B", new Pokemon[]{new Pokemon(109, 48, false, true), new Pokemon(105, 49, false, true), new Pokemon(152, 50, false, true), new Pokemon(203, 48, false, true), new Pokemon(176, 50, false, true), new Pokemon(103, 50, false, true)}, new Item[]{Item.HEAT_ROCK, Item.WISE_GLASSES, Item.BLACK_SLUDGE, Item.CHOICE_SCARF, Item.LEFTOVERS, Item.WHITE_HERB}, 100),
				new Trainer("SCT C", new Pokemon[]{new Pokemon(173, 49, false, true), new Pokemon(178, 49, false, true), new Pokemon(168, 50, false, true), new Pokemon(170, 49, false, true), new Pokemon(216, 49, false, true), new Pokemon(207, 51, false, true)}, new Item[]{Item.WIKI_BERRY, Item.LEFTOVERS, Item.SITRUS_BERRY, Item.ASSAULT_VEST, Item.BLACK_GLASSES, Item.WEAKNESS_POLICY}, 100),
				new Trainer("EP-BE", new Pokemon[]{new Pokemon(70, 45, false, true), new Pokemon(60, 44, false, true), new Pokemon(63, 45, false, true)}, new Item[]{Item.ICY_ROCK, Item.BRIGHT_POWDER, Item.OCCA_BERRY}, 100),
				new Trainer("EP-BF", new Pokemon[]{new Pokemon(112, 66, false, true), new Pokemon(119, 65, false, true), new Pokemon(178, 65, false, true)}, new Item[]{Item.EVIOLITE, Item.WIDE_LENS, Item.CHILAN_BERRY}, 100),
				new Trainer("EP-BG", new Pokemon[]{new Pokemon(109, 66, false, true), new Pokemon(105, 67, false, true), new Pokemon(204, 65, false, true), new Pokemon(100, 65, false, true), new Pokemon(6, 66, false, true)}, new Item[]{Item.HEAT_ROCK, Item.BLUNDER_POLICY, Item.PASSHO_BERRY, Item.ROCKY_HELMET, Item.RED_CARD}, 100), // 275
				new Trainer("DH", new Pokemon[]{new Pokemon(168, 62, false, true), new Pokemon(50, 63, false, true), new Pokemon(56, 64, false, true)}, new Item[]{Item.SMOOTH_ROCK, Item.LEFTOVERS, Item.FOCUS_SASH}, 100),
				new Trainer("DI", new Pokemon[]{new Pokemon(40, 64, false, true), new Pokemon(12, 64, false, true)}, new Item[]{Item.WHITE_HERB, Item.GLOWING_PRISM}, 100),
				new Trainer("DJ", new Pokemon[]{new Pokemon(109, 65, false, true), new Pokemon(107, 65, false, true), new Pokemon(100, 65, false, true)}, new Item[]{Item.SHUCA_BERRY, Item.QUICK_CLAW, Item.FOCUS_BAND}, 100),
				new Trainer("DK", new Pokemon[]{new Pokemon(110, 65, false, true), new Pokemon(96, 65, false, true), new Pokemon(103, 65, false, true)}, new Item[]{Item.WHITE_HERB, Item.ASSAULT_VEST, Item.LEFTOVERS}, 100),
				new Trainer("DL", new Pokemon[]{new Pokemon(46, 64, false, true), new Pokemon(9, 63, false, true), new Pokemon(70, 64, false, true), new Pokemon(61, 66, false, true)}, new Item[]{Item.KEBIA_BERRY, Item.WIKI_BERRY, Item.WIDE_LENS, Item.FOCUS_BAND}, 100), // 280
				new Trainer("DM", new Pokemon[]{new Pokemon(132, 90, false, true), new Pokemon(143, 90, false, true), new Pokemon(148, 90, false, true)}, new Item[]{Item.EVIOLITE, Item.POWER_HERB, Item.RINDO_BERRY}, 100),
				new Trainer("DN", new Pokemon[]{new Pokemon(139, 75, false, true), new Pokemon(140, 65, false, true)}, new Item[]{Item.EVIOLITE, Item.COSMIC_CORE}, 100),
				new Trainer("DO", new Pokemon[]{new Pokemon(138, 63, false, true), new Pokemon(142, 63, false, true), new Pokemon(216, 64, false, true)}, new Item[]{Item.WACAN_BERRY, Item.WEAKNESS_POLICY, Item.RED_CARD}, 100),
				new Trainer("DP", new Pokemon[]{new Pokemon(150, 64, false, true), new Pokemon(150, 65, false, true), new Pokemon(150, 66, false, true)}, new Item[]{Item.LIFE_ORB, Item.MYSTICOLA_BERRY, Item.WHITE_HERB}, 100),
				new Trainer("DQ", new Pokemon[]{new Pokemon(152, 65, false, true), new Pokemon(155, 64, false, true), new Pokemon(227, 65, false, true), new Pokemon(105, 64, false, true)}, new Item[]{Item.BLACK_SLUDGE, Item.ASSAULT_VEST, Item.SPELL_TAG, Item.BLUNDER_POLICY}, 100), // 285
				new Trainer("DR", new Pokemon[]{new Pokemon(107, 64, false, true), new Pokemon(77, 65, false, true), new Pokemon(21, 65, false, true), new Pokemon(47, 66, false, true)}, new Item[]{Item.AIR_BALLOON, Item.RED_CARD, Item.COSMIC_CORE, Item.WHITE_HERB}, 100),
				new Trainer("DS", new Pokemon[]{new Pokemon(100, 65, false, true), new Pokemon(54, 65, false, true), new Pokemon(145, 64, false, true), new Pokemon(173, 65, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.STARF_BERRY, Item.ASSAULT_VEST, Item.SITRUS_BERRY}, 100),
				new Trainer("DT", new Pokemon[]{new Pokemon(103, 65, false, true), new Pokemon(58, 66, false, true), new Pokemon(18, 65, false, true)}, new Item[]{Item.STARF_BERRY, Item.STARF_BERRY, Item.CUSTAP_BERRY}, 100),
				new Trainer("DU", new Pokemon[]{new Pokemon(93, 65, false, true), new Pokemon(97, 66, false, true), new Pokemon(6, 66, false, true), new Pokemon(225, 65, false, true)}, new Item[]{Item.SILK_SCARF, Item.LIFE_ORB, Item.EXPERT_BELT, Item.BLACK_GLASSES}, 100),
				new Trainer("DV", new Pokemon[]{new Pokemon(204, 68, false, true), new Pokemon(125, 68, false, true), new Pokemon(63, 68, false, true)}, new Item[]{Item.BLUNDER_POLICY, Item.POWER_HERB, Item.WIDE_LENS}, 100), // 290
				new Trainer("7 Gym A", new Pokemon[]{new Pokemon(61, 69, false, true), new Pokemon(61, 69, false, true), new Pokemon(145, 67, false, true)}, new Item[]{Item.FOCUS_BAND, Item.WIDE_LENS, Item.POWER_HERB}, 200),
				new Trainer("7 Gym B", new Pokemon[]{new Pokemon(47, 68, false, true), new Pokemon(122, 67, false, true), new Pokemon(47, 69, false, true)}, new Item[]{Item.FOCUS_SASH, Item.POWER_HERB, Item.WHITE_HERB}, 200),
				new Trainer("7 Gym C", new Pokemon[]{new Pokemon(128, 68, false, true), new Pokemon(150, 68, false, true)}, new Item[]{Item.LIFE_ORB, Item.LIFE_ORB}, 200),
				new Trainer("7 Gym D", new Pokemon[]{new Pokemon(150, 69, false, true), new Pokemon(107, 68, false, true)}, new Item[]{Item.FOCUS_SASH, Item.CUSTAP_BERRY}, 200),
				new Trainer("7 Gym E", new Pokemon[]{new Pokemon(178, 68, false, true), new Pokemon(145, 67, false, true), new Pokemon(122, 68, false, true)}, new Item[]{Item.WEAKNESS_POLICY, Item.KEBIA_BERRY, Item.SITRUS_BERRY}, 200), // 295
				new Trainer("7 Gym F", new Pokemon[]{new Pokemon(125, 68, false, true), new Pokemon(47, 69, false, true)}, new Item[]{Item.AIR_BALLOON, Item.WHITE_HERB}, 200),
				new Trainer("7 Gym G", new Pokemon[]{new Pokemon(185, 69, false, true), new Pokemon(178, 68, false, true), new Pokemon(128, 67, false, true)}, new Item[]{Item.EVIOLITE, Item.CHILAN_BERRY, Item.STARF_BERRY}, 200),
				new Trainer("7 Gym H", new Pokemon[]{new Pokemon(145, 68, false, true), new Pokemon(150, 69, false, true)}, new Item[]{Item.LEFTOVERS, Item.ROCKY_HELMET}, 200),
				new Trainer("7 Gym I", new Pokemon[]{new Pokemon(150, 70, false, true)}, new Item[]{Item.FOCUS_BAND}, 200),
				new Trainer("7 Gym J", new Pokemon[]{new Pokemon(143, 90, false, true), new Pokemon(125, 67, false, true), new Pokemon(61, 69, false, true)}, new Item[]{Item.POWER_HERB, Item.KING1S_ROCK, Item.RED_CARD}, 200), // 300
				new Trainer("7 Gym K", new Pokemon[]{new Pokemon(186, 67, false, true), new Pokemon(77, 69, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.COLBUR_BERRY}, 200),
				new Trainer("7 Gym L", new Pokemon[]{new Pokemon(107, 67, false, true), new Pokemon(122, 68, false, true)}, new Item[]{Item.QUICK_CLAW, Item.ROCKY_HELMET}, 200),
				new Trainer("7 Gym M", new Pokemon[]{new Pokemon(125, 69, false, true), new Pokemon(122, 69, false, true), new Pokemon(128, 69, false, true)}, new Item[]{Item.FOCUS_BAND, Item.BRIGHT_POWDER, Item.QUICK_CLAW}, 200),
				new Trainer("7 Gym N", new Pokemon[]{new Pokemon(77, 69, false, true), new Pokemon(178, 69, false, true)}, new Item[]{Item.RED_CARD, Item.RED_CARD}, 200),
				new Trainer("7 Gym Leader 1", new Pokemon[]{new Pokemon(122, 70, false, true), new Pokemon(150, 70, false, true), new Pokemon(47, 70, false, true), new Pokemon(107, 70, false, true), new Pokemon(232, 70, false, true), new Pokemon(229, 71, false, true)}, 500, Item.TM03), // 305
				new Trainer("DX", new Pokemon[]{new Pokemon(125, 65, false, true), new Pokemon(212, 66, false, true), new Pokemon(119, 66, false, true), new Pokemon(204, 65, false, true)}, new Item[]{Item.TERRAIN_EXTENDER, Item.CHOICE_BAND, Item.COVERT_CLOAK, Item.WHITE_HERB}, 100),
				new Trainer("DY", new Pokemon[]{new Pokemon(196, 67, false, true), new Pokemon(168, 67, false, true)}, new Item[]{Item.ASSAULT_VEST, Item.LEFTOVERS}, 100),
				new Trainer("DZ", new Pokemon[]{new Pokemon(136, 67, false, true), new Pokemon(162, 67, false, true), new Pokemon(180, 66, false, true), new Pokemon(149, 67, false, true)}, new Item[]{Item.RED_CARD, Item.WHITE_HERB, Item.LIFE_ORB, Item.RINDO_BERRY}, 100),
				new Trainer("EA", new Pokemon[]{new Pokemon(222, 67, false, true), new Pokemon(227, 67, false, true), new Pokemon(157, 68, false, true), new Pokemon(159, 67, false, true), new Pokemon(131, 67, false, true)}, new Item[]{Item.EXPERT_BELT, Item.LEFTOVERS, Item.LEFTOVERS, Item.ROSELI_BERRY, Item.FOCUS_SASH}, 100),
				new Trainer("EB", new Pokemon[]{new Pokemon(141, 69, false, true), new Pokemon(116, 67, false, true), new Pokemon(3, 68, false, true), new Pokemon(28, 67, false, true), new Pokemon(31, 68, false, true)}, new Item[]{Item.EVIOLITE, Item.SOFT_SAND, Item.BABIRI_BERRY, Item.WHITE_HERB, Item.BLACK_SLUDGE}, 100), // 310
				new Trainer("TN MSJ 1", new Pokemon[]{new Pokemon(53, 68, false, true), new Pokemon(56, 68, false, true), new Pokemon(84, 68, false, true), new Pokemon(87, 68, false, true), new Pokemon(24, 68, false, true), new Pokemon(60, 69, false, true)}, new Item[]{Item.ROCKY_HELMET, Item.FOCUS_SASH, Item.TERRAIN_EXTENDER, Item.BRIGHT_POWDER, Item.TOXIC_ORB, Item.TWISTED_SPOON}, 100),
				new Trainer("EC", new Pokemon[]{new Pokemon(96, 66, false, true), new Pokemon(107, 66, false, true), new Pokemon(110, 66, false, true)}, new Item[]{Item.CHOICE_BAND, Item.QUICK_CLAW, Item.WHITE_HERB}, 100),
				new Trainer("ED", new Pokemon[]{new Pokemon(116, 66, false, true), new Pokemon(170, 66, false, true), new Pokemon(219, 66, false, true)}, new Item[]{Item.AIR_BALLOON, Item.WIDE_LENS, Item.ASSAULT_VEST}, 100),
				new Trainer("EE", new Pokemon[]{new Pokemon(178, 66, false, true), new Pokemon(15, 66, false, true), new Pokemon(12, 66, false, true)}, new Item[]{Item.EXPERT_BELT, Item.WACAN_BERRY, Item.WISE_GLASSES}, 100),
				new Trainer("EF", new Pokemon[]{new Pokemon(9, 66, false, true), new Pokemon(140, 66, false, true), new Pokemon(72, 66, false, true)}, new Item[]{Item.MUSCLE_BAND, Item.STARF_BERRY, Item.FOCUS_SASH}, 100), // 315
				new Trainer("ZT", new Pokemon[]{new Pokemon(48, 20, false, true), new Pokemon(118, 20, false, true)}, new Item[]{Item.WEAKNESS_POLICY, Item.LEFTOVERS}, 100),
				new Trainer("ZU", new Pokemon[]{new Pokemon(211, 16, false, true), new Pokemon(213, 16, false, true)}, new Item[]{Item.PERSIM_BERRY, Item.CHESTO_BERRY}, 100),
				new Trainer("EG", new Pokemon[]{new Pokemon(40, 70, false, true), new Pokemon(27, 71, false, true), new Pokemon(18, 70, false, true)}, new Item[]{Item.EXPERT_BELT, Item.EVIOLITE, Item.ROCKY_HELMET}, 100),
				new Trainer("EH", new Pokemon[]{new Pokemon(37, 71, false, true), new Pokemon(198, 70, false, true), new Pokemon(183, 71, false, true)}, new Item[]{Item.MAGNET, Item.CHOICE_SCARF, Item.CLEAR_AMULET}, 100),
				new Trainer("EI", new Pokemon[]{new Pokemon(194, 70, false, true), new Pokemon(70, 71, false, true), new Pokemon(65, 71, false, true), new Pokemon(39, 70, false, true)}, new Item[]{Item.CHOICE_SCARF, Item.BLUNDER_POLICY, Item.ASSAULT_VEST, Item.ROCKY_HELMET}, 100), // 320
				new Trainer("EJ", new Pokemon[]{new Pokemon(46, 72, false, true), new Pokemon(63, 71, false, true), new Pokemon(58, 72, false, true), new Pokemon(15, 71, false, true)}, new Item[]{Item.DAMP_ROCK, Item.NEVER$MELT_ICE, Item.EXPERT_BELT, Item.ASSAULT_VEST}, 100),
				new Trainer("EK", new Pokemon[]{new Pokemon(54, 72, false, true), new Pokemon(216, 71, false, true), new Pokemon(225, 72, false, true)}, new Item[]{Item.TWISTED_SPOON, Item.STARF_BERRY, Item.FLAME_ORB}, 100),
				new Trainer("EL", new Pokemon[]{new Pokemon(201, 71, false, true), new Pokemon(210, 71, false, true), new Pokemon(133, 72, false, true)}, new Item[]{Item.LIFE_ORB, Item.RED_CARD, Item.CHOICE_SCARF}, 100),
				new Trainer("EM", new Pokemon[]{new Pokemon(79, 72, false, true), new Pokemon(56, 72, false, true), new Pokemon(47, 72, false, true), new Pokemon(25, 72, false, true)}, new Item[]{Item.LEFTOVERS, Item.RED_CARD, Item.WHITE_HERB, Item.SITRUS_BERRY}, 100),
				new Trainer("Diftery", new Pokemon[]{new Pokemon(229, 75, true)}, 0, 36), // 325
				new Trainer("Triwandoliz", new Pokemon[]{new Pokemon(232, 75, true)}, 0, 35),
				new Trainer("Ice Master", new Pokemon[]{new Pokemon(59, 37, false, true), new Pokemon(60, 36, false, true), new Pokemon(64, 35, false, true), new Pokemon(69, 36, false, true)}, new Item[]{Item.FOCUS_SASH, Item.ICY_ROCK, Item.EVIOLITE, Item.LEFTOVERS}, 100, Item.PETTICOAT_GEM, 7),
				new Trainer("Ground Master", new Pokemon[]{new Pokemon(50, 36, false, true), new Pokemon(57, 35, false, true), new Pokemon(169, 36, false, true), new Pokemon(215, 37, false, true)}, new Item[]{Item.FOCUS_BAND, null, Item.EVIOLITE, Item.LIFE_ORB}, 100, Item.VALIANT_GEM, 6),
				new Trainer("Crook", new Pokemon[]{new Pokemon(104, 13, false, true), new Pokemon(85, 14, false, true), new Pokemon(171, 14, false, true)}, new Item[]{Item.ORAN_BERRY, Item.RINDO_BERRY, Item.ROCKY_HELMET}, 100, Item.EXP_SHARE, 32),
				new Trainer("TN MS 2", new Pokemon[] {new Pokemon(227, 68, false, true), new Pokemon(152, 68, false, true), new Pokemon(222, 68, false, true), new Pokemon(162, 68, false, true)}, new Item[] {Item.ROSELI_BERRY, Item.BLACK_SLUDGE, Item.CHOICE_SCARF, Item.POWER_HERB}, 100), // 330
				new Trainer("TN MS 3", new Pokemon[] {new Pokemon(225, 68, false, true), new Pokemon(96, 68, false, true), new Pokemon(105, 68, false, true), new Pokemon(17, 68, false, true)}, new Item[] {Item.FLAME_ORB, Item.ASSAULT_VEST, Item.WIDE_LENS, Item.EVIOLITE}, 100),
				new Trainer("TN MS 4", new Pokemon[] {new Pokemon(216, 68, false, true), new Pokemon(149, 68, false, true), new Pokemon(46, 68, false, true), new Pokemon(147, 68, false, true)}, new Item[] {Item.POWER_HERB, Item.POWER_HERB, Item.COBA_BERRY, Item.ROCKY_HELMET}, 100),
				new Trainer("TN MS 5", new Pokemon[] {new Pokemon(219, 68, false, true), new Pokemon(173, 68, false, true), new Pokemon(180, 68, false, true), new Pokemon(159, 68, false, true)}, new Item[] {Item.SITRUS_BERRY, Item.PASSHO_BERRY, Item.FOCUS_SASH, Item.ROSELI_BERRY}, 100),
				new Trainer("TN MS 6", new Pokemon[] {new Pokemon(207, 69, false, true), new Pokemon(51, 69, false, true), new Pokemon(212, 69, false, true), new Pokemon(54, 69, false, true)}, new Item[] {Item.CHOICE_SPECS, Item.RINDO_BERRY, Item.CHOICE_SCARF, Item.ASSAULT_VEST}, 100),
				new Trainer("TN MS 7", new Pokemon[] {new Pokemon(208, 69, false, true), new Pokemon(50, 69, false, true), new Pokemon(201, 69, false, true), new Pokemon(183, 69, false, true)}, new Item[] {Item.CHOICE_SCARF, Item.MUSCLE_BAND, Item.WHITE_HERB, Item.EXPERT_BELT}, 100), // 335
				new Trainer("TN MS 8", new Pokemon[] {new Pokemon(210, 69, false, true), new Pokemon(198, 69, false, true), new Pokemon(204, 69, false, true), new Pokemon(100, 69, false, true)}, new Item[] {Item.AIR_BALLOON, Item.LIFE_ORB, Item.WHITE_HERB, Item.WIDE_LENS}, 100),
				new Trainer("TN MS 9", new Pokemon[] {new Pokemon(113, 69, false, true), new Pokemon(214, 69, false, true), new Pokemon(119, 69, false, true), new Pokemon(28, 69, false, true)}, new Item[] {Item.METAL_COAT, Item.CHOPLE_BERRY, Item.ROCKY_HELMET, Item.YACHE_BERRY}, 100),
				new Trainer("TN MS 10", new Pokemon[] {new Pokemon(27, 69, false, true), new Pokemon(31, 69, false, true), new Pokemon(155, 69, false, true), new Pokemon(34, 69, false, true)}, new Item[] {Item.EVIOLITE, Item.POISON_BARB, Item.SHARP_BEAK, Item.POWER_HERB}, 100),
				new Trainer("TN MS 11", new Pokemon[] {new Pokemon(165, 69, false, true), new Pokemon(170, 69, false, true), new Pokemon(173, 69, false, true), new Pokemon(176, 69, false, true)}, new Item[] {Item.FLAME_ORB, Item.EXPERT_BELT, Item.YACHE_BERRY, Item.SITRUS_BERRY}, 100),
				new Trainer("TN MS 12", new Pokemon[] {new Pokemon(24, 70, false, true), new Pokemon(43, 70, false, true), new Pokemon(25, 70, false, true), new Pokemon(74, 70, false, true)}, new Item[] {Item.TOXIC_ORB, Item.SILVER_POWDER, Item.ROCKY_HELMET, Item.BLACK_GLASSES}, 100), // 340
				new Trainer("TN MS 13", new Pokemon[] {new Pokemon(84, 70, false, true), new Pokemon(145, 70, false, true), new Pokemon(142, 70, false, true), new Pokemon(141, 70, false, true)}, new Item[] {Item.ROCKY_HELMET, Item.RED_CARD, Item.LIFE_ORB, Item.EVIOLITE}, 100),
				new Trainer("TN MS 14", new Pokemon[] {new Pokemon(97, 70, false, true), new Pokemon(109, 70, false, true), new Pokemon(6, 70, false, true), new Pokemon(196, 70, false, true)}, new Item[] {Item.CHOICE_SCARF, Item.HEAT_ROCK, Item.CHOICE_SCARF, Item.CHOICE_SCARF}, 100),
				new Trainer("Rick 3", new Pokemon[]{new Pokemon(74, 74, false, true), new Pokemon(162, 74, false, true), new Pokemon(113, 74, false, true), new Pokemon(46, 74, false, true), new Pokemon(67, 73, false, true), new Pokemon(122, 75, false, true)}, 400, 34),
				new Trainer("Fred 4", new Pokemon[]{new Pokemon(1, 1, false, true)}, 500),
				new Trainer("Maxwell 2", new Pokemon[]{new Pokemon(183, 79, false, true), new Pokemon(222, 79, false, true), new Pokemon(216, 79, false, true), new Pokemon(214, 79, false, true), new Pokemon(192, 79, false, true), new Pokemon(186, 80, false, true)}, 400, 28), // 345
				new Trainer("TN Zurroaratr", new Pokemon[]{new Pokemon(162, 100, true)}, 0, 29),
				new Trainer("EN", new Pokemon[]{new Pokemon(212, 77, false, true), new Pokemon(113, 78, false, true), new Pokemon(198, 78, false, true), new Pokemon(210, 78, false, true), new Pokemon(116, 77, false, true), new Pokemon(125, 79, false, true)}, new Item[] {Item.SHUCA_BERRY, Item.CHOICE_BAND, Item.FOCUS_SASH, Item.CHOICE_SCARF, Item.LIECHI_BERRY, Item.POWER_HERB}, 100),
				new Trainer("EO", new Pokemon[]{new Pokemon(65, 77, false, true), new Pokemon(88, 78, false, true), new Pokemon(43, 77, false, true), new Pokemon(128, 78, false, true), new Pokemon(165, 78, false, true), new Pokemon(192, 79, false, true)}, new Item[] {Item.ASSAULT_VEST, Item.POWER_HERB, Item.FOCUS_SASH, Item.CLEAR_AMULET, Item.SITRUS_BERRY, Item.COBA_BERRY}, 100),
				new Trainer("EP", new Pokemon[]{new Pokemon(34, 78, false, true), new Pokemon(40, 78, false, true), new Pokemon(81, 78, false, true), new Pokemon(119, 79, false, true), new Pokemon(28, 77, false, true), new Pokemon(3, 80, false, true)}, new Item[] {Item.FOCUS_SASH, Item.WHITE_HERB, Item.PERSIM_BERRY, Item.COVERT_CLOAK, Item.YACHE_BERRY, Item.LEFTOVERS}, 100),
				new Trainer("EQ", new Pokemon[]{new Pokemon(103, 78, false, true), new Pokemon(107, 80, false, true), new Pokemon(110, 78, false, true), new Pokemon(93, 80, false, true), new Pokemon(105, 78, false, true), new Pokemon(109, 81, false, true)}, new Item[] {Item.LEFTOVERS, Item.QUICK_CLAW, Item.PASSHO_BERRY, Item.RED_CARD, Item.HEAVY$DUTY_BOOTS, Item.HEAT_ROCK}, 100), // 350
				new Trainer("ER", new Pokemon[]{new Pokemon(207, 80, false, true), new Pokemon(54, 80, false, true), new Pokemon(56, 80, false, true), new Pokemon(18, 79, false, true), new Pokemon(67, 78, false, true), new Pokemon(196, 81, false, true)}, new Item[] {Item.WEAKNESS_POLICY, Item.CHOICE_SCARF, Item.CHOPLE_BERRY, Item.ROCKY_HELMET, Item.LUM_BERRY, Item.ASSAULT_VEST}, 100),
				new Trainer("ES", new Pokemon[]{new Pokemon(31, 79, false, true), new Pokemon(152, 79, false, true), new Pokemon(155, 78, false, true), new Pokemon(24, 79, false, true), new Pokemon(242, 78, false, true), new Pokemon(222, 80, false, true)}, new Item[] {Item.FOCUS_SASH, Item.BLACK_SLUDGE, Item.HEAVY$DUTY_BOOTS, Item.TOXIC_ORB, Item.FOCUS_BAND, Item.BLACK_GLASSES}, 100),
				new Trainer("ET", new Pokemon[]{new Pokemon(122, 79, false, true), new Pokemon(183, 80, false, true), new Pokemon(15, 79, false, true), new Pokemon(84, 79, false, true), new Pokemon(201, 78, false, true), new Pokemon(6, 81, false, true)}, new Item[] {Item.POWER_HERB, Item.WEAKNESS_POLICY, Item.WACAN_BERRY, Item.CHOICE_BAND, Item.FOCUS_SASH, Item.AIR_BALLOON}, 100),
				new Trainer("EU", new Pokemon[]{new Pokemon(227, 80, false, true), new Pokemon(97, 80, false, true), new Pokemon(157, 79, false, true), new Pokemon(133, 80, false, true), new Pokemon(131, 81, false, true), new Pokemon(180, 82, false, true)}, new Item[] {Item.COLBUR_BERRY, Item.ROCKY_HELMET, Item.LEFTOVERS, Item.CHOICE_SCARF, Item.HEAVY$DUTY_BOOTS, Item.LIFE_ORB}, 100),
				new Trainer("EV", new Pokemon[]{new Pokemon(136, 80, false, true), new Pokemon(46, 81, false, true), new Pokemon(72, 79, false, true), new Pokemon(79, 80, false, true), new Pokemon(140, 80, false, true), new Pokemon(150, 82, false, true)}, new Item[] {Item.DAMP_ROCK, Item.EXPERT_BELT, Item.BLUNDER_POLICY, Item.COLBUR_BERRY, Item.COSMIC_CORE, Item.MYSTIC_WATER}, 100), // 355
				new Trainer("EW", new Pokemon[]{new Pokemon(214, 80, false, true), new Pokemon(225, 82, false, true), new Pokemon(74, 81, false, true), new Pokemon(142, 80, false, true), new Pokemon(159, 81, false, true), new Pokemon(219, 83, false, true)}, new Item[] {Item.HABAN_BERRY, Item.FLAME_ORB, Item.HEAVY$DUTY_BOOTS, Item.LIFE_ORB, Item.ROSELI_BERRY, Item.WHITE_HERB}, 100),
				new Trainer("EX", new Pokemon[]{new Pokemon(149, 82, false, true), new Pokemon(168, 81, false, true), new Pokemon(170, 81, false, true), new Pokemon(173, 82, false, true), new Pokemon(219, 82, false, true), new Pokemon(50, 83, false, true)}, new Item[] {Item.RINDO_BERRY, Item.SMOOTH_ROCK, Item.EXPERT_BELT, Item.WHITE_HERB, Item.FOCUS_BAND, Item.WEAKNESS_POLICY}, 100),
				new Trainer("EY", new Pokemon[]{new Pokemon(189, 80, false, true), new Pokemon(216, 82, false, true), new Pokemon(100, 81, false, true), new Pokemon(145, 82, false, true), new Pokemon(28, 83, false, true), new Pokemon(240, 84, false, true)}, new Item[] {Item.GLOWING_PRISM, Item.MUSCLE_BAND, Item.CHOICE_SCARF, Item.WHITE_HERB, Item.COVERT_CLOAK, Item.LUM_BERRY}, 100),
				//new Trainer("Rival 6", new Pokemon[]{new Pokemon(-132, 45, false, true), new Pokemon(-25, 33, false, true), new Pokemon(-23, 40, false, true), new Pokemon(-41, 34, false, true), new Pokemon(-78, 44, false, true), new Pokemon(-87, 38, false, true)}, 500),
//				new Trainer("8 Gym A", new Pokemon[]{new Pokemon(-100, 30, false, true), new Pokemon(-100, 30, false, true), new Pokemon(-100, 30, false, true), new Pokemon(-100, 30, false, true), new Pokemon(-100, 30, false, true), new Pokemon(-100, 30, false, true)}, 200),
//				new Trainer("8 Gym B", new Pokemon[]{new Pokemon(-101, 40, false, true), new Pokemon(-101, 40, false, true)}, 200),
//				new Trainer("8 Gym C", new Pokemon[]{new Pokemon(-70, 45, false, true), new Pokemon(-78, 42, false, true)}, 200),
//				new Trainer("8 Gym D", new Pokemon[]{new Pokemon(-122, 50, false, true)}, 200),
//				new Trainer("8 Gym E", new Pokemon[]{new Pokemon(-101, 41, false, true), new Pokemon(-100, 32, false, true), new Pokemon(-78, 39, false, true)}, 200),
//				new Trainer("8 Gym F", new Pokemon[]{new Pokemon(-122, 30, false, true), new Pokemon(-122, 35, false, true), new Pokemon(-122, 40, false, true), new Pokemon(-122, 45, false, true)}, 200),
//				new Trainer("8 Gym Leader 1", new Pokemon[]{new Pokemon(-100, 33, false, true), new Pokemon(-101, 45, false, true), new Pokemon(-79, 55, false, true), new Pokemon(-122, 50, false, true), new Pokemon(-70, 48, false, true), new Pokemon(-102, 55, false, true)}, 500),
//				new Trainer("TA", new Pokemon[]{new Pokemon(-112, 50, false, true)}, 100),
//				new Trainer("TB", new Pokemon[]{new Pokemon(-113, 50, false, true)}, 100),
//				new Trainer("TC", new Pokemon[]{new Pokemon(-114, 50, false, true)}, 100),
//				new Trainer("TD", new Pokemon[]{new Pokemon(-115, 50, false, true)}, 100),
//				new Trainer("TE", new Pokemon[]{new Pokemon(-116, 50, false, true)}, 100),
//				new Trainer("TF", new Pokemon[]{new Pokemon(-117, 50, false, true)}, 100),
//				new Trainer("TG", new Pokemon[]{new Pokemon(-118, 50, false, true)}, 100),
//				new Trainer("TH", new Pokemon[]{new Pokemon(-119, 50, false, true)}, 100),
//				new Trainer("TI", new Pokemon[]{new Pokemon(-120, 50, false, true)}, 100),
//				new Trainer("TJ", new Pokemon[]{new Pokemon(-121, 50, false, true)}, 100),
//				new Trainer("TK", new Pokemon[]{new Pokemon(-122, 50, false, true)}, 100),
//				new Trainer("TL", new Pokemon[]{new Pokemon(-123, 50, false, true)}, 100),
//				new Trainer("TA 2", new Pokemon[]{new Pokemon(-112, 80, false, true)}, 100),
//				new Trainer("TB 2", new Pokemon[]{new Pokemon(-113, 80, false, true)}, 100),
//				new Trainer("TC 2", new Pokemon[]{new Pokemon(-114, 80, false, true)}, 100),
//				new Trainer("TD 2", new Pokemon[]{new Pokemon(-115, 80, false, true)}, 100),
//				new Trainer("TE 2", new Pokemon[]{new Pokemon(-116, 80, false, true)}, 100),
//				new Trainer("TF 2", new Pokemon[]{new Pokemon(-117, 80, false, true)}, 100),
//				new Trainer("TG 2", new Pokemon[]{new Pokemon(-118, 80, false, true)}, 100),
//				new Trainer("TH 2", new Pokemon[]{new Pokemon(-119, 80, false, true)}, 100),
//				new Trainer("TI 2", new Pokemon[]{new Pokemon(-120, 80, false, true)}, 100),
//				new Trainer("TJ 2", new Pokemon[]{new Pokemon(-121, 80, false, true)}, 100),
//				new Trainer("TK 2", new Pokemon[]{new Pokemon(-122, 80, false, true)}, 100),
//				new Trainer("TL 2", new Pokemon[]{new Pokemon(-123, 80, false, true)}, 100),
//				new Trainer("TM", new Pokemon[]{new Pokemon(-92, 50, false, true), new Pokemon(-92, 50, false, true), new Pokemon(-92, 50, false, true), new Pokemon(-92, 50, false, true)}, 100),
//				new Trainer("TN", new Pokemon[]{new Pokemon(-47, 50, false, true), new Pokemon(-45, 50, false, true), new Pokemon(-37, 50, false, true), new Pokemon(-37, 50, false, true), new Pokemon(-90, 50, false, true), new Pokemon(-90, 50, false, true)}, 100),
//				new Trainer("TO", new Pokemon[]{new Pokemon(-90, 50, false, true), new Pokemon(-43, 50, false, true), new Pokemon(-37, 50, false, true), new Pokemon(-87, 50, false, true), new Pokemon(-41, 50, false, true), new Pokemon(-91, 55, false, true)}, 100),
//				new Trainer("1 Gym Leader 2", new Pokemon[]{new Pokemon(-20, 50, false, true), new Pokemon(-23, 55, false, true), new Pokemon(-20, 50, false, true), new Pokemon(-118, 58, false, true), new Pokemon(-3, 57, false, true), new Pokemon(-6, 57, false, true)}, 500),
//				new Trainer("2 Gym Leader 2", new Pokemon[]{new Pokemon(-13, 60, false, true), new Pokemon(-3, 60, false, true), new Pokemon(-11, 54, false, true), new Pokemon(-113, 61, false, true), new Pokemon(-60, 58, false, true), new Pokemon(-126, 61, false, true)}, 500),
//				new Trainer("3 Gym Leader 2", new Pokemon[]{new Pokemon(-41, 59, false, true), new Pokemon(-39, 60, false, true), new Pokemon(-41, 61, false, true), new Pokemon(-94, 64, false, true), new Pokemon(-94, 64, false, true), new Pokemon(-39, 60, false, true)}, 500),
//				new Trainer("4 Gym Leader 2", new Pokemon[]{new Pokemon(-43, 66, false, true), new Pokemon(-45, 64, false, true), new Pokemon(-47, 63, false, true), new Pokemon(-45, 65, false, true), new Pokemon(-117, 67, false, true), new Pokemon(-83, 62, false, true)}, 500),
//				new Trainer("5 Gym Leader 2", new Pokemon[]{new Pokemon(-132, 72, false, true), new Pokemon(-66, 69, false, true), new Pokemon(-9, 70, false, true), new Pokemon(-64, 70, false, true), new Pokemon(-144, 67, false, true), new Pokemon(-115, 71, false, true)}, 500),
//				new Trainer("6 Gym Leader 2", new Pokemon[]{new Pokemon(-90, 73, false, true), new Pokemon(-41, 72, false, true), new Pokemon(-90, 73, false, true), new Pokemon(-87, 74, false, true), new Pokemon(-119, 76, false, true), new Pokemon(-69, 74, false, true)}, 500),
//				new Trainer("7 Gym Leader 2", new Pokemon[]{new Pokemon(-85, 75, false, true), new Pokemon(-108, 77, false, true), new Pokemon(-129, 78, false, true), new Pokemon(-76, 76, false, true), new Pokemon(-114, 77, false, true), new Pokemon(-81, 76, false, true)}, 500),
//				new Trainer("8 Gym Leader 2", new Pokemon[]{new Pokemon(-79, 82, false, true), new Pokemon(-102, 83, false, true), new Pokemon(-102, 83, false, true), new Pokemon(-122, 85, false, true), new Pokemon(-79, 82, false, true), new Pokemon(-102, 83, false, true)}, 500),
//				new Trainer("Rival 7", new Pokemon[]{new Pokemon(-132, 90, false, true), new Pokemon(-25, 71, false, true), new Pokemon(-23, 82, false, true), new Pokemon(-41, 74, false, true), new Pokemon(-79, 88, false, true), new Pokemon(-87, 78, false, true)}, 500),
		};
		
		if (gp.player.p.trainersBeat == null) gp.player.p.trainersBeat = new boolean[Main.trainers.length];
	}
	
	private static void setMoveset(String string, int i, Move a, Move b, Move c, Move d) {
		if (gp.player.p.random) return;
		Move[] moves = new Move[] {a, b, c, d};
		Moveslot[] moveslots = new Moveslot[4];
		for (int j = 0; j < 4; j++) {
			moveslots[j] = moves[j] == null ? null : new Moveslot(moves[j]);
		}
		for (Trainer tr : trainers) {
			if (tr.getName().equals(string)) {
				tr.getTeam()[i - 1].moveset = moveslots;
				if (!modifiedTrainers.contains(tr)) modifiedTrainers.add(tr);
			}
		}
	}
	
	private static void setAbility(String string, int i, Ability a) {
		setAbility(string, i, a, false);
	}
	
	private static void setAbility(String string, int i, Ability a, boolean hidden) {
		for (Trainer tr : trainers) {
			if (tr.getName().equals(string)) {
				tr.getTeam()[i - 1].abilitySlot = hidden ? 2 : 1;
				if (!gp.player.p.random) {
					tr.getTeam()[i - 1].ability = a;
				} else {
					tr.getTeam()[i - 1].setAbility();
				}
				if (!modifiedTrainers.contains(tr)) modifiedTrainers.add(tr);
			}
		}
	}
	
	private static void setItem(String string, int i, Item a) {
		for (Trainer tr : trainers) {
			if (tr.getName().equals(string)) {
				tr.getTeam()[i - 1].item = a;
			}
		}
	}
	
	private static void writePokemon() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt");
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				writer.write("===================\n");
				String id = p.id + "";
				while (id.length() < 3) {
					id = "0" + id;
				}
				String name = id + " - " + p.name + "\n";
				writer.write(name);
				writer.write("===================\n");
				
				writer.write("Type:\n");
				String type = p.type1.toString() + " / ";
				type = p.type2 == null ? type + "None" : type + p.type2.toString();
				writer.write(type + "\n\n");
				
				writer.write("Ability:\n");
				StringBuilder abilityBuilder = new StringBuilder();
				p.setAbility(0);
				Ability ability1 = p.ability;
				abilityBuilder.append(ability1.toString()).append(" / ");
				p.setAbility(1);
				if (p.ability == ability1) {
				    abilityBuilder.append("None");
				} else {
				    abilityBuilder.append(p.ability.toString());
				}
				p.setAbility(2);
				abilityBuilder.append(" / (");
				if (p.ability == Ability.NULL || p.ability == ability1) {
				    abilityBuilder.append("None");
				} else {
				    abilityBuilder.append(p.ability.toString());
				}
				abilityBuilder.append(")\n\n");
				writer.write(abilityBuilder.toString());
				
				writer.write("Base Stats:\n");
				String stats = "";
				for (int j = 0; j < p.baseStats.length; j++) {
					stats += p.getBaseStat(j) + " " + Pokemon.getStatType(j) + "/ ";
				}
				stats += p.getBST() + " BST";
				writer.write(stats + "\n\n");
				
				writer.write("Level Up:\n");
				Node[] movebank = p.getMovebank();
				for (int j = 0; j < movebank.length; j++) {
					Node n = movebank[j];
					while (n != null) {
						String move = j + 1 + " - " + n.data.toString() + "\n";
						n = n.next;
						writer.write(move);
					}
				}
				writer.write("\n");
				if (p.canEvolve()) {
					writer.write("Evolutions:\n");
					writer.write(p.getEvolveString() + "\n\n");
				}
				
			}
			writer.close();
			//writeUnusedMoves();
			writeTypeStats();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@SuppressWarnings("unused")
	private static void writeTypeStats() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt", true);
			writer.write("Stats:\n");
			ArrayList<PType> types = new ArrayList<>(Arrays.asList(PType.values()));
			types.remove(PType.UNKNOWN);
			int[][] sums = new int[types.size()][6];
			int[] amounts = new int[types.size()];
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				for (int j = 0; j < p.baseStats.length; j++) {
					sums[types.indexOf(p.type1)][j] += p.baseStats[j];
					if (p.type2 != null) sums[types.indexOf(p.type2)][j] += p.baseStats[j];
				}
				amounts[types.indexOf(p.type1)]++;
				if (p.type2 != null) amounts[types.indexOf(p.type2)]++;
			}
			StringBuilder header = new StringBuilder();
			header.append("Type");
			for (int i = 0; i < sums[1].length; i++) {
				header.append("," + Pokemon.getStatType(i).strip());
			}
			header.append(",Amt\n");
			writer.write(header.toString());
			for (int i = 0; i < types.size(); i++) {
				StringBuilder stats = new StringBuilder();
				stats.append(types.get(i).toString());
				for (int j = 0; j < sums[1].length; j++) {
					double average = sums[i][j] * 1.0;
					average /= amounts[i];
					stats.append("," + String.format("%.1f", average));
				}
				stats.append("," + amounts[i]);
				stats.append("\n");
				writer.write(stats.toString());
			}
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void printIntArray2D(int[][] array) {
        // Iterate over each row of the 2D array
        for (int i = 0; i < array.length; i++) {
            // Iterate over each element in the current row
            for (int j = 0; j < array[i].length; j++) {
                // Print the current element followed by a space
                System.out.print(array[i][j] + " ");
            }
            // Move to the next line after printing all elements in the current row
            System.out.println();
        }
    }

	@SuppressWarnings("unused")
	private static void writeUnusedMoves() {
		try {
			FileWriter writer = new FileWriter("./docs/PokemonInfo.txt", true);
			writer.write("Unused moves:\n");
			ArrayList<Move> unused = new ArrayList<>();
			ArrayList<Move> unusedButTM = new ArrayList<>();
			Map<Pokemon, Move> sigOne = new HashMap<>();
			Map<Pokemon, Move> sigTwo = new HashMap<>();
			Map<Pokemon, Move> sigThree = new HashMap<>();
			Map<Move, Integer> moveCount = new HashMap<>();
			for (Move m : Move.values()) {
				moveCount.put(m, 0);
			}
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				ArrayList<Move> movebank = new ArrayList<>();
				Node[] pokemonMovebank = p.getMovebank();
				for (int j = 0; j < pokemonMovebank.length; j++) {
					Node n = pokemonMovebank[j];
					while (n != null) {
						movebank.add(n.data);
						n = n.next;
					}
				}

				for (Move move : movebank) {
					moveCount.put(move, moveCount.getOrDefault(move, 0) + 1);
				}
			}
			for (Map.Entry<Move, Integer> entry : moveCount.entrySet()) {
				if (entry.getValue() == 0) {
					if (entry.getKey().isTM()) {
						unusedButTM.add(entry.getKey());
					} else {
						unused.add(entry.getKey());
					}
				} else if (entry.getValue() == 3) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 3; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigThree.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 2) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 2; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigTwo.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 1) {
					for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigOne.put(p, entry.getKey());
							break;
						}
					}
				}
			}
			for (Move m : unused) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nTM Only:\n");
			for (Move m : unusedButTM) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nSignature Moves (3):\n");
			sigThree.entrySet().stream()
	        .sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
	        .forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			writer.write("\nSignature Moves (2):\n");
			sigTwo.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		
			writer.write("\nSignature Moves (1):\n");
			sigOne.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}



	private static void writeTrainers(GamePanel gp) {
		try {
			FileWriter writer = new FileWriter("./docs/TrainerInfo.txt");
			Entity[][] npc = gp.npc;
			writer.write("Trainers:\n");
			Map<String, ArrayList<Trainer>> trainerMap = new LinkedHashMap<>();
			for (int loc = 0; loc < npc.length; loc++) {
				for (int col = 0; col < npc[loc].length; col++) {
					Entity e = npc[loc][col];
					if (e == null || e.trainer < 0) continue;
					if (loc != 0 && e.trainer == 0) continue;
					PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
					String location = PlayerCharacter.currentMapName;
					Trainer tr = Main.trainers[e.trainer];
					if (trainerMap.containsKey(location)) {
						ArrayList<Trainer> list = trainerMap.get(location);
						list.add(tr);
					} else {
						ArrayList<Trainer> list = new ArrayList<>();
						list.add(tr);
						trainerMap.put(location, list);
					}
				}
			}
			
			for (Map.Entry<Entity, Integer> e : GamePanel.volatileTrainers.entrySet()) {
				PMap.getLoc(e.getValue(), e.getKey().worldX / gp.tileSize, e.getKey().worldY / gp.tileSize);
				String location = PlayerCharacter.currentMapName;
				Trainer tr = Main.trainers[e.getKey().trainer];
				if (trainerMap.containsKey(location)) {
					ArrayList<Trainer> list = trainerMap.get(location);
					list.add(tr);
				} else {
					ArrayList<Trainer> list = new ArrayList<>();
					list.add(tr);
					trainerMap.put(location, list);
				}
			}
			
			for (Map.Entry<String, ArrayList<Trainer>> e : trainerMap.entrySet()) {
				ArrayList<Trainer> trainers = e.getValue();
				String loc = e.getKey();
				while (loc.length() < 50) {
					loc += "-";
				}
				writer.write(loc + "\n");
				writer.write("\n");
				for (Trainer tr : trainers) {
					boolean hasItems = false;
					String name = "Trainer " + tr.getName();
					while (name.length() < 28) {
						name += " ";
					}
					writer.write(name);
					for (int i = 0; i < tr.getTeam().length; i++) {
						Pokemon p = tr.getTeam()[i];
						writer.write(p.name + " Lv. " + p.level);
						if (i != tr.getTeam().length - 1) writer.write(", ");
						if (!hasItems && p.item != null) hasItems = true;
					}
					if (hasItems && !tr.getName().contains("Leader") && !tr.getName().contains("Rick") && !tr.getName().contains("Scott") && !tr.getName().contains("Fred") && !tr.getName().contains("Maxwell")) {
						writer.write(" @ (");
						for (int i = 0; i < tr.getTeam().length; i++) {
							String item = tr.getTeam()[i].item == null ? "None" : tr.getTeam()[i].item.toString();
							writer.write(item);
							if (i != tr.getTeam().length - 1) writer.write(", ");
						}
						writer.write(")");
					}
					writer.write("\n");
					
					if (tr.getName().contains("Leader") || tr.getName().contains("Rick") || tr.getName().contains("Scott") || tr.getName().contains("Fred") || tr.getName().contains("Maxwell")) {
						writer.write("\n");
						writer.write(tr.getName() + "\n");
						for (Pokemon p : tr.getTeam()) {
							String pName = p.name + " (Lv. " + p.level + ")";
							if (p.item != null) {
								pName += " @ " + p.item.toString();
								while (pName.length() < 40) {
									pName += " ";
								}
							} else {
								while (pName.length() < 22) {
									pName += " ";
								}
							}
							
							pName += "/";
							writer.write(pName);
							
							String aName = "   " + p.ability.toString();
							while (aName.length() < 18) {
								aName += " ";
							}
							aName += "/";
							writer.write(aName);
							
							String mName = "   ";
							for (int i = 0; i < 4; i++) {
								if (p.moveset[i] != null) {
									mName += p.moveset[i].move.toString();
									if (i != 3) mName += ", ";
								}
							}
							writer.write(mName);
							writer.write("\n");
						}
						writer.write("\n");
					}
				}
				writer.write("\n");
			}
			
			writer.close();
			//writeTrainerUsageStats();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void writeTrainerUsageStats() {
		try {
			FileWriter writer = new FileWriter("./docs/TrainerInfo.txt", true);
			writer.write("Stats:\n");
			int[] occurences = new int[Pokemon.MAX_POKEMON + 1];
			int totalPokemon = 0;
			
			for (Trainer tr : trainers) {
				for (Pokemon p : tr.getTeam()) {
					if (p.id <= 9 && (tr.getName().contains("Scott") || tr.getName().contains("Fred"))) {
						if (p.id == 1 || p.id == 4 || p.id == 7) {
							occurences[1]++;
							occurences[4]++;
							occurences[7]++;
							totalPokemon += 3;
						} else if (p.id == 2 || p.id == 5 || p.id == 8) {
							occurences[2]++;
							occurences[5]++;
							occurences[8]++;
							totalPokemon += 3;
						} else if (p.id == 3 || p.id == 6 || p.id == 9) {
							occurences[3]++;
							occurences[6]++;
							occurences[9]++;
							totalPokemon += 3;
						}
					} else {
						occurences[p.id]++;
						totalPokemon++;
					}
				}
			}
			StringBuilder header = new StringBuilder();
			header.append("Num,Pokemon,Amt,%\n");
			writer.write(header.toString());
			for (int i = 1; i < occurences.length; i++) {
				double ratio = occurences[i] * 100.0;
				ratio /= totalPokemon;
				StringBuilder stats = new StringBuilder();
				stats.append(i);
				stats.append("," + Pokemon.getName(i));
				stats.append("," + occurences[i]);
				stats.append("," + String.format("%.2f", ratio) + "%");
				stats.append("\n");
				writer.write(stats.toString());
			}
			writer.write("\nTotal: " + totalPokemon);
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}



	private static void writeEncounters() {
		try {
			FileWriter writer = new FileWriter("./docs/WildPokemon.txt");
			
			ArrayList<Encounter> allEncounters = new ArrayList<>();
			int[] amounts = new int[Pokemon.MAX_POKEMON + 1];
			double[] chances = new double[Pokemon.MAX_POKEMON + 1];
			
			ArrayList<Encounter> encounters = Encounter.getEncounters(0, 73, 39, "Standard", "", false); // route 22
			writer.write("Route 22 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(0, 73, 38, "Standard", "", false); // route 23
			writer.write("Route 23 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(0, 74, 38, "Standard", "", false); // route 42
			writer.write("Route 42 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(0, 74, 38, "Fishing", "", false); // route 42
			writer.write("Route 42 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(0, 74, 38, "Surfing", "", false); // route 42
			writer.write("Route 42 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(4, 73, 43, "Standard", "", false); // route 24
			writer.write("Route 24 pt. 1 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(11, 44, 55, "Standard", "", false); // route 24 pt. 2
			writer.write("Route 24 pt. 2 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(11, 32, 68, "Standard", "", false); // gelb forest
			writer.write("Gelb Forest (1A) (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(11, 32, 68, "Fishing", "", false); // gelb forest
			writer.write("Gelb Forest (1A) (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(11, 32, 68, "Surfing", "", false); // gelb forest
			writer.write("Gelb Forest (1A) (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(144, 32, 68, "Standard", "", false); // gelb forest 2A
			writer.write("Gelb Forest (2A) (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(11, 42, 52, "Standard", "", false); // route 25
			writer.write("Route 25 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(13, 73, 38, "Fishing", "", false); // sicab city
			writer.write("Sicab City (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(13, 73, 38, "Surfing", "", false); // sicab city
			writer.write("Sicab City (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(14, 73, 38, "Standard", "", false); // energy plant A
			writer.write("Energy Plant A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(16, 73, 38, "Standard", "", false); // energy plant B
			writer.write("Energy Plant B (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(16, 73, 38, "Fishing", "", false); // energy plant B
			writer.write("Energy Plant B (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(16, 73, 38, "Surfing", "", false); // energy plant B
			writer.write("Energy Plant B (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(22, 73, 38, "Standard", "", false); // route 40
			writer.write("Route 40 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(13, 73, 38, "Standard", "", false); // route 26
			writer.write("Route 26 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(24, 73, 38, "Standard", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(24, 73, 38, "Fishing", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(24, 73, 38, "Surfing", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(25, 73, 38, "Standard", "", false); // mt. splinkty 2B
			writer.write("Mt. Splinkty 2B (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(26, 73, 38, "Standard", "", false); // mt. splinkty 3B
			writer.write("Mt. Splinkty 3B (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(27, 73, 38, "Standard", "", false); // mt. splinkty 3A
			writer.write("Mt. Splinkty 3A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(28, 73, 38, "Standard", "", false); // route 27
			writer.write("Route 27 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(28, 73, 57, "Fishing", "", false); // route 41
			writer.write("Route 41 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(28, 73, 57, "Surfing", "", false); // route 41
			writer.write("Route 41 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(28, 73, 57, "Standard", "", false); // route 41
			writer.write("Route 41 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(4, 73, 42, "Standard", "", false); // route 36
			writer.write("Route 36 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(4, 73, 42, "Fishing", "", false); // route 36
			writer.write("Route 36 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(4, 73, 42, "Surfing", "", false); // route 36
			writer.write("Route 36 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(33, 73, 21, "Standard", "", false); // route 28
			writer.write("Route 28 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(35, 73, 42, "Standard", "", false); // electric tunnel 01
			writer.write("Electric Tunnel 01 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(36, 73, 42, "Standard", "", false); // route 29
			writer.write("Route 29 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(38, 73, 31, "Standard", "", false); // icy fields
			writer.write("Icy Fields (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(38, 73, 62, "Standard", "", false); // route 30
			writer.write("Route 30 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(38, 73, 62, "Fishing", "", false); // route 30
			writer.write("Route 30 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(38, 73, 62, "Surfing", "", false); // route 30
			writer.write("Route 30 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(33, 0, 33, "Standard", "", false); // peaceful park
			writer.write("Peaceful Park (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(33, 0, 33, "Fishing", "", false); // peaceful park
			writer.write("Peaceful Park (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(33, 0, 33, "Surfing", "", false); // peaceful park
			writer.write("Peaceful Park (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(77, 73, 62, "Surfing", "", false); // mindagan lake
			writer.write("Mindagan Lake (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(77, 73, 62, "Fishing", "", false); // mindagan lake
			writer.write("Mindagan Lake (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(78, 73, 62, "Standard", "", false); // mindagan cavern 1A
			writer.write("Mindagan Cavern 1A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(80, 73, 41, "Standard", "", false); // route 31
			writer.write("Route 31 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(80, 73, 41, "Fishing", "", false); // route 31
			writer.write("Route 31 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(80, 73, 41, "Surfing", "", false); // route 31
			writer.write("Route 31 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(83, 57, 41, "Standard", "", false); // shadow ravine 1B
			writer.write("Shadow Ravine 1B (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(90, 73, 41, "Standard", "", false); // shadow ravine 0
			writer.write("Shadow Ravine 0 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(83, 58, 41, "Standard", "", false); // route 32
			writer.write("Route 32 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(83, 58, 41, "Fishing", "", false); // route 32
			writer.write("Route 32 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(83, 58, 41, "Surfing", "", false); // route 32
			writer.write("Route 32 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(95, 58, 41, "Standard", "", false); // Electric 0
			writer.write("Electric Tunnel 0 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(95, 58, 41, "Fishing", "", false); // Electric 0
			writer.write("Electric Tunnel 0 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(95, 58, 41, "Surfing", "", false); // Electric 0
			writer.write("Electric Tunnel 0 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(96, 58, 41, "Standard", "", false); // Electric -1
			writer.write("Electric Tunnel -1 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(97, 58, 41, "Standard", "", false); // Electric -2
			writer.write("Electric Tunnel -2 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(98, 58, 41, "Standard", "", false); // Electric -3
			writer.write("Electric Tunnel -3 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(98, 58, 41, "Fishing", "", false); // Electric -3
			writer.write("Electric Tunnel -3 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(98, 58, 41, "Surfing", "", false); // Electric -3
			writer.write("Electric Tunnel -3 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(99, 58, 41, "Standard", "", false); // Electric H
			writer.write("Electric Tunnel H (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(100, 58, 41, "Standard", "", false); // Shadow H
			writer.write("Shadow Ravine H (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(101, 58, 41, "Standard", "", false); // Shadow -1
			writer.write("Shadow Ravine -1 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(101, 58, 41, "Fishing", "", false); // Shadow -1
			writer.write("Shadow Ravine -1 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(101, 58, 41, "Surfing", "", false); // Shadow -1
			writer.write("Shadow Ravine -1 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(102, 58, 41, "Standard", "", false); // Shadow -2
			writer.write("Shadow Ravine -2 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(102, 58, 41, "Lava", "", false); // Shadow -2
			writer.write("Shadow Ravine -2 (Lava)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(103, 58, 41, "Standard", "", false); // Shadow -3
			writer.write("Shadow Ravine -3 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(103, 58, 41, "Lava", "", false); // Shadow -3
			writer.write("Shadow Ravine -3 (Lava)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(105, 58, 41, "Standard", "", false); // Shadow Path
			writer.write("Shadow Path (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(85, 58, 41, "Standard", "", false); // Route 33
			writer.write("Route 33 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(85, 58, 41, "Fishing", "", false); // Route 33
			writer.write("Route 33 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(85, 58, 41, "Surfing", "", false); // Route 33
			writer.write("Route 33 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(107, 87, 17, "Standard", "", false); // Ghostly Woods
			writer.write("Ghostly Woods (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(110, 58, 41, "Standard", "", false); // Route 34
			writer.write("Route 34 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(110, 58, 41, "Lava", "", false); // Route 34
			writer.write("Route 34 (Lava)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(115, 58, 41, "Standard", "", false); // Route 35
			writer.write("Route 35 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(117, 58, 41, "Standard", "", false); // Mindagan Cavern 0
			writer.write("Mindagan Cavern 0 (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(119, 21, 41, "Standard", "", false); // Route 43
			writer.write("Route 43 (W) (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(124, 21, 49, "Standard", "", false); // Route 37
			writer.write("Route 37 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(124, 21, 49, "Surfing", "", false); // Route 37
			writer.write("Route 37 (Surf)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(124, 21, 49, "Fishing", "", false); // Route 37
			writer.write("Route 37 (Fish)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(124, 21, 50, "Standard", "", false); // Route 38
			writer.write("Route 38 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(137, 21, 50, "Standard", "", false); // MSJ 1A
			writer.write("Mt. St. Joseph 1A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(138, 21, 50, "Standard", "", false); // MSJ 2A
			writer.write("Mt. St. Joseph 2A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(139, 21, 50, "Standard", "", false); // MSJ 2B
			writer.write("Mt. St. Joseph 2B (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(140, 21, 50, "Standard", "", false); // MSJ 3A
			writer.write("Mt. St. Joseph 3A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(141, 21, 50, "Standard", "", false); // MSJ 3B
			writer.write("Mt. St. Joseph 3B (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(142, 21, 50, "Standard", "", false); // MSJ 4A
			writer.write("Mt. St. Joseph 4A (Floor)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(137, 21, 50, "Lava", "", false); // MSJ 1A
			writer.write("Mt. St. Joseph 1A/2A/2B/3A/3B/4A (Lava)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(143, 21, 50, "Lava", "", false); // MSJ 4B
			writer.write("Mt. St. Joseph 4B (Lava)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(144, 33, 68, "Standard", "", false); // R44
			writer.write("Route 44 (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			encounters = Encounter.getEncounters(146, 33, 68, "Standard", "", false); // Mt. Splinkty Outside
			writer.write("Mt. Splinkty (Outside) (Grass)");
			writer.write(writeEncounter(encounters));
			allEncounters.addAll(encounters);
			
			for (Encounter e : allEncounters) {
				int index = e.getId();
				amounts[index]++;
				chances[index] += e.getEncounterChance();
			}
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				double average = amounts[i] == 0 ? 0.0 : chances[i] / amounts[i] * 100;
				writer.write(i + "," + Pokemon.getName(i) + "," + amounts[i] + "," + String.format("%.2f", average) + "%\n");
			}
			
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}



	private static String writeEncounter(ArrayList<Encounter> encounters) {
		String result = "===================================================================\n";
		for (Encounter e : encounters) {
			Pokemon ep = new Pokemon(e.getId(), 5, false, false);
			String percent = String.format("%.0f", e.getEncounterChance() * 100);
			boolean sameLv = e.getMinLevel() == e.getMaxLevel();
			result += percent;
			result += "% ";
			result += ep.name;
			result += " (Lv. ";
			if (sameLv) {
				result += e.getMinLevel();
			} else {
				result += e.getMinLevel();
				result += " - ";
				result += e.getMaxLevel();
			}
			result += ")\n";
		}
		result += "\n";
		return result;
		
		
	}
	
	private static void writeMoves() {
		try {
			FileWriter writer = new FileWriter("./docs/MovesInfo.txt");
			
			ArrayList<Move> moves = new ArrayList<>(Arrays.asList(Move.values()));
			Map<PType, List<Move>> movesByType = new HashMap<>();
			
			for (Move m : moves) {
				PType type = m.mtype;
				movesByType.computeIfAbsent(type, k -> new ArrayList<>()).add(m);
			}
			
			for (List<Move> typeMoves : movesByType.values()) {
				typeMoves.sort(Comparator.comparing(Move::toString));
			}
			
			List<PType> sortedTypes = new ArrayList<>(movesByType.keySet());
			sortedTypes.sort(Comparator.comparing(PType::toString));
			
			for (PType type : sortedTypes) {
				
				writer.write("\n===============\n");
	            writer.write(type.toString() + "\n");
	            writer.write("===============\n");

	            List<Move> typeMoves = movesByType.get(type);
	            for (Move m : typeMoves) {
            		String result = m.toString() + " : ";
					result += m.getCategory() + " / ";
					result += (int) m.getbp(null, null) + " / ";
					result += m.getAccuracy() + " : ";
					result += m.getDescription() + "\n";
					
					writer.write(result);
	            }				
			}
			writer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeDefensiveTypes() {
		try {
            FileWriter writer = new FileWriter("./docs/DefensiveTypings.txt");

            PType[] types = PType.values();
            ArrayList<PType> arrayListTypes = new ArrayList<>(Arrays.asList(types));
            arrayListTypes.remove(PType.UNKNOWN);
            types = arrayListTypes.toArray(new PType[1]);
            
            Pokemon test = new Pokemon(1, 5, true, false);
            Pokemon foe = new Pokemon(4, 5, true, false);

            writer.write("TYPE COMBINATIONS (Defensively)\n");
            for (PType type1 : types) {
                for (PType type2 : types) {
                	writer.write("\n===========================\n");
                    String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
                    writer.write(combination + "\n===========================\n");
                    
                    Map<PType, Double> typeEffectivenessMap = new HashMap<>();
                    foe.type1 = type1;
                    foe.type2 = type2;
                    
                    for (PType type3 : types) {
                        double multiplier = 1;
                        if (test.getImmune(foe, type3)) multiplier = 0;
                        
                        if (multiplier != 0) {
                        	// Check type effectiveness
                    		PType[] resist = test.getResistances(type3);
                    		for (PType type : resist) {
                    			if (type1 == type) multiplier /= 2;
                    			if (type2 == type) multiplier /= 2;
                    		}
                    		
                    		// Check type effectiveness
                    		PType[] weak = test.getWeaknesses(type3);
                    		for (PType type : weak) {
                    			if (type1 == type) multiplier *= 2;
                    			if (type2 == type) multiplier *= 2;
                    		}
                    		
                        }
                        typeEffectivenessMap.put(type3, multiplier);
                		
                    }
                    
                    writer.write("Immune to:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() == 0.0) {
                            writer.write(entry.getKey().toString() + "\n");
                        }
                    }
                    
                    writer.write("\n----------\nWeak to:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() >= 2.0) {
                            writer.write(entry.getKey().toString());
                            if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
                            writer.write("\n");
                        }
                    }
                    
                    writer.write("\n----------\nResists:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() < 1.0 && entry.getValue() != 0) {
                            writer.write(entry.getKey().toString());
                            if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
                            writer.write("\n");
                        }
                    }
                    
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	private static void writeOffensiveTypes() {
		try {
            FileWriter writer = new FileWriter("./docs/OffensiveTypings.txt");

            PType[] types = PType.values();
            ArrayList<PType> arrayListTypes = new ArrayList<>(Arrays.asList(types));
            arrayListTypes.remove(PType.UNKNOWN);
            types = arrayListTypes.toArray(new PType[1]);
            
            Pokemon test = new Pokemon(1, 5, true, false);
            Pokemon foe = new Pokemon(4, 5, true, false);

            writer.write("TYPE COMBINATIONS (Offensively)\n");
            for (PType type1 : types) {
                for (PType type2 : types) {
                	writer.write("\n===========================\n");
                    String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
                    writer.write(combination + "\n===========================\n");
                    
                    Map<PType, Double> typeEffectivenessMap = new HashMap<>();
                    
                    for (PType type3 : types) {
                    	foe.type1 = type3;
                        foe.type2 = null;
                        double multiplier = 1;
                        if (test.getImmune(foe, type1) && test.getImmune(foe, type2)) multiplier = 0;
                        
                        if (multiplier != 0) {
                        	// Check type effectiveness
                    		ArrayList<PType> resist1 = new ArrayList<>(Arrays.asList(test.getResistances(type1)));
                    		ArrayList<PType> resist2 = new ArrayList<>(Arrays.asList(test.getResistances(type2)));
                    		if ((resist1.contains(type3) && resist2.contains(type3)) || (test.getImmune(foe, type1) && resist2.contains(type3) || (resist1.contains(type3) && test.getImmune(foe, type2)))) multiplier /= 2;
                    		
                    		// Check type effectiveness
                    		ArrayList<PType> weak1 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type1)));
                    		ArrayList<PType> weak2 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type2)));
                    		if (weak1.contains(type3) && weak2.contains(type3)) {
                    			multiplier *= 4;
                    		} else if (weak1.contains(type3) || weak2.contains(type3)) {
                    			multiplier *= 2;
                    		}
                    		
                    		
                        }
                        typeEffectivenessMap.put(type3, multiplier);
                		
                    }
                    
                    writer.write("Deals 2x to:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() >= 2.0) {
                            writer.write(entry.getKey().toString());
                            if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
                            writer.write("\n");
                        }
                    }
                    
                    writer.write("\n----------\nDeals 1/2x to:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() < 1.0 && entry.getValue() != 0) {
                            writer.write(entry.getKey().toString());
                            if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
                            writer.write("\n");
                        }
                    }
                    
                    writer.write("\n----------\nDeals 0x to:\n\n");
                    for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
                        if (entry.getValue() == 0.0) {
                            writer.write(entry.getKey().toString() + "\n");
                        }
                    }
                    
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

}
