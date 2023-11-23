package Overworld;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Swing.Player;
import Swing.Pokemon;
import Swing.Pokemon.Node;
import Swing.TextPane;
import Swing.Trainer;
import Swing.Ability;
import Swing.Battle.JGradientButton;
import Swing.Encounter;
import Swing.Item;
import Swing.Move;
import Swing.Moveslot;
import Swing.PType;

public class Main {
	public static Trainer[] trainers;
	public static ArrayList<Trainer> modifiedTrainers;

	public static void main(String[] args) {

		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Pokemon Game");
		
		setTrainers();
		
		GamePanel gamePanel = new GamePanel();
		
		showStartupMenu(window, gamePanel);
	}
	
	

	private static void showStartupMenu(JFrame window, GamePanel gp) {
		WelcomeMenu menu = new WelcomeMenu(window, gp);
		window.add(menu);
		
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void loadSave(JFrame window, GamePanel gamePanel, String fileName, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
	        gamePanel.player.p = (Player) ois.readObject();
	        for (Pokemon p : gamePanel.player.p.team) {
	            if (p != null) p.clearVolatile();
	        }
	        gamePanel.player.worldX = gamePanel.player.p.getPosX();
	        gamePanel.player.worldY = gamePanel.player.p.getPosY();
	        gamePanel.currentMap = gamePanel.player.p.currentMap;
	        ois.close();
	    } catch (IOException | ClassNotFoundException e) {
	        // If there's an error reading the file, create a new Player object
	        gamePanel.player.p = new Player(gamePanel);

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
	                gamePanel.player.p.starter = finalChoice;
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
	        
	        Pokemon.console = new TextPane();
	        Pokemon.console.setScrollPane(new JScrollPane());
	        
	        // Add the chosen starter to the player's team
	        if (gamePanel.player.p.starter <= 0) gamePanel.player.p.starter = (int)(Math.random() * options.length) + 1;
	        if (gamePanel.player.p.starter == 1) {
	            gamePanel.player.p.catchPokemon(new Pokemon(1, 5, true, false));
	        } else if (gamePanel.player.p.starter == 2) {
	            gamePanel.player.p.catchPokemon(new Pokemon(4, 5, true, false));
	        } else if (gamePanel.player.p.starter == 3) {
	            gamePanel.player.p.catchPokemon(new Pokemon(7, 5, true, false));
	        }
	        
	        //gamePanel.player.p.bag.add(new Item(22), 999);
	    }
		
		loadGame(window, gamePanel, welcomeMenu, selectedOptions);
	}
	
	private static void loadGame(JFrame window, GamePanel gamePanel, WelcomeMenu welcomeMenu, boolean[] selectedOptions) {
		window.remove(welcomeMenu);
        window.repaint();
        
		SwingUtilities.invokeLater(() -> {
			// Create a JPanel for the fade effect
		    FadingPanel fadePanel = new FadingPanel();
		    fadePanel.setBackground(Color.BLACK);
		    fadePanel.setBounds(0, 0, window.getWidth(), window.getHeight());
		    window.add(fadePanel, 0); // Add the fadePanel at the bottom layer
		    
		    modifyTrainers(gamePanel);
		    gamePanel.setupGame();
		    gamePanel.startGameThread();
		    
		    // Create a Timer to gradually change the opacity of the fadePanel
		    Timer timer = new Timer(20, null);
		    timer.addActionListener(e -> {
		        float alpha = fadePanel.getAlpha();
		        alpha += 0.02f; // Adjust the fading speed
		        fadePanel.setAlpha(alpha);
		        fadePanel.repaint();
	
		        if (alpha >= 0.95f) {
		            timer.stop();
		    		
		    		if (selectedOptions[0]) writeTrainers();
		    		if (selectedOptions[1]) writePokemon();
		    		if (selectedOptions[2]) writeEncounters();
		    		if (selectedOptions[3]) writeMoves();
		    		
		    		window.add(gamePanel);
		    		gamePanel.requestFocusInWindow();
		    		
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
		    		
		    		gamePanel.eHandler.p = gamePanel.player.p;
		        }
		    });
			
			timer.start();
		});
	}

	private static void modifyTrainers(GamePanel gp) {
		if (gp.player.p.starter == 1) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(7, 7, false, true)}, 400, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(5, 20, false, true)}, 400);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(8, 22, false, true), new Pokemon(166, 22, false, true)}, 400, new Item(94), 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(5, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(99, 47, false, true), new Pokemon(210, 46, false, true), new Pokemon(219, 46, false, true), new Pokemon(6, 47, false, true), new Pokemon(79, 48, false, true)}, 400, 14);
		}
		else if (gp.player.p.starter == 2) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(1, 7, false, true)}, 400, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(8, 20, false, true)}, 400);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(2, 22, false, true), new Pokemon(166, 22, false, true)}, 400, new Item(94), 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(8, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(99, 47, false, true), new Pokemon(210, 46, false, true), new Pokemon(219, 46, false, true), new Pokemon(9, 47, false, true), new Pokemon(79, 48, false, true)}, 400, 14);
		}
		else if (gp.player.p.starter == 3) {
			trainers[0] = new Trainer("Scott 1", new Pokemon[]{new Pokemon(4, 7, false, true)}, 400, 1);
			trainers[34] = new Trainer("Fred 1", new Pokemon[]{new Pokemon(78, 18, false, true), new Pokemon(2, 20, false, true)}, 400);
			trainers[55] = new Trainer("Scott 2", new Pokemon[]{new Pokemon(130, 21, false, true), new Pokemon(5, 22, false, true), new Pokemon(166, 22, false, true)}, 400, new Item(94), 4);
			trainers[89] = new Trainer("Fred 2", new Pokemon[]{new Pokemon(98, 29, false, true), new Pokemon(210, 30, false, true), new Pokemon(2, 30, false, true), new Pokemon(79, 31, false, true)}, 400, 5);
			trainers[185] = new Trainer("Fred 3", new Pokemon[]{new Pokemon(99, 47, false, true), new Pokemon(210, 46, false, true), new Pokemon(219, 46, false, true), new Pokemon(3, 47, false, true), new Pokemon(79, 48, false, true)}, 400, 14);
		}
		
		setMoveset("1 Gym Leader 1", 2, Move.MEGA_DRAIN, Move.SUPERSONIC, Move.AERIAL_ACE, Move.POISON_FANG);
		setAbility("1 Gym Leader 1", 4, Ability.LIGHTNING_ROD);
		setMoveset("1 Gym Leader 1", 5, Move.CALM_MIND, Move.WISH, Move.MAGIC_BLAST, Move.GUST);
		setMoveset("1 Gym Leader 1", 6, Move.SAND_ATTACK, Move.LEER, Move.AERIAL_ACE, Move.QUICK_ATTACK);
		
		setMoveset("2 Gym Leader 1", 1, Move.SUPER_FANG, Move.BITE, Move.HEADBUTT, Move.BULK_UP);
		setMoveset("2 Gym Leader 1", 2, Move.POWER$UP_PUNCH, Move.FURY_SWIPES, Move.DOUBLE_TEAM, Move.CHARM);
		setMoveset("2 Gym Leader 1", 3, Move.WILL$O$WISP, Move.HEX, Move.ROUND, Move.TAUNT);
		setMoveset("2 Gym Leader 1", 4, Move.SUPER_FANG, Move.METAL_CLAW, Move.FLAME_WHEEL, Move.BODY_SLAM);
		setMoveset("2 Gym Leader 1", 6, Move.HEADBUTT, Move.HYPNOSIS, Move.FEINT_ATTACK, Move.SWIFT);
		
		setMoveset("3 Gym Leader 1", 1, Move.FELL_STINGER, Move.AQUA_TAIL, Move.IRON_TAIL, Move.STEALTH_ROCK);
		setAbility("3 Gym Leader 1", 1, Ability.COMPOUND_EYES);
		setMoveset("3 Gym Leader 1", 2, Move.STICKY_WEB, Move.THUNDERBOLT, Move.BUG_BUZZ, Move.THUNDER_WAVE);
		setMoveset("3 Gym Leader 1", 3, Move.LEAF_BLADE, Move.X$SCISSOR, Move.FELL_STINGER, Move.SWORDS_DANCE);
		setMoveset("3 Gym Leader 1", 4, Move.FIRST_IMPRESSION, Move.FELL_STINGER, Move.SACRED_SWORD, Move.AGILITY);
		setMoveset("3 Gym Leader 1", 5, Move.BUG_BITE, Move.MOONLIGHT, Move.FEINT_ATTACK, Move.FELL_STINGER);
		setMoveset("3 Gym Leader 1", 6, Move.AURORA_BEAM, Move.BUG_BUZZ, Move.SNOWSCAPE, Move.QUIVER_DANCE);
		
		setMoveset("AB1", 1, Move.HYPNOSIS, Move.PLAY_NICE, Move.MOONLIGHT, Move.HIDDEN_POWER);
		setMoveset("AC1", 1, Move.HYPNOSIS, Move.FAKE_TEARS, Move.NASTY_PLOT, Move.SNARL);
		
		setMoveset("4 Gym Leader 1", 2, Move.SNOWSCAPE, Move.AURORA_VEIL, Move.BLIZZARD, Move.SCALD);
		setAbility("4 Gym Leader 1", 2, Ability.ICE_BODY);
		setMoveset("4 Gym Leader 1", 3, Move.RED$NOSE_BOOST, Move.MAGIC_BLAST, Move.ICE_SHARD, Move.BLIZZARD);
		setMoveset("4 Gym Leader 1", 4, Move.PSYCHIC, Move.ICE_BEAM, Move.FREEZE$DRY, Move.CALM_MIND);
		setMoveset("4 Gym Leader 1", 5, Move.ICE_PUNCH, Move.CLOSE_COMBAT, Move.ICE_SHARD, Move.SWORDS_DANCE);
		setMoveset("4 Gym Leader 1", 6, Move.ICE_SPINNER, Move.COMET_CRASH, Move.BRAVE_BIRD, Move.HEAD_SMASH);
		
		setMoveset("5 Gym Leader 1", 1, Move.LIGHT_SCREEN, Move.REFLECT, Move.PSYCHO_CUT, Move.SUPERPOWER);
		setMoveset("5 Gym Leader 1", 2, Move.PSYCHO_CUT, Move.SWORDS_DANCE, Move.PLAY_ROUGH, Move.CLOSE_COMBAT);
		setMoveset("5 Gym Leader 1", 3, Move.LEECH_SEED, Move.PSYSHOCK, Move.ENERGY_BALL, Move.DETECT);
		setMoveset("5 Gym Leader 1", 4, Move.HYDRO_PUMP, Move.CONFUSE_RAY, Move.PSYSHOCK, Move.FOCUS_BLAST);
		setMoveset("5 Gym Leader 1", 5, Move.ELEMENTAL_SPARKLE, Move.MAGIC_REFLECT, Move.TRICK_ROOM, Move.PSYSHOCK);
		setMoveset("5 Gym Leader 1", 6, Move.BULLET_PUNCH, Move.IRON_DEFENSE, Move.ZEN_HEADBUTT, Move.COMET_CRASH);
		setAbility("5 Gym Leader 1", 6, Ability.LEVITATE);
		
//		Move[] yes = Move.values();
//		int max = 0;
//		Move result = Move.ABSORB;
//		for (int i = 0; i < yes.length; i++) {
//			int size = yes[i].toString().length();
//			if (size > max) {
//				max = size;
//				result = yes[i];
//			}
//			
//		}
//		System.out.println(max + ", " + result.toString());
	}



	private static void setTrainers() {
		modifiedTrainers = new ArrayList<>();
		trainers = new Trainer[]{
				new Trainer("Scott 1", new Pokemon[]{new Pokemon(1, 100, false, true)}, 400),
				new Trainer("A", new Pokemon[]{new Pokemon(16, 5, false, true)}, 100),
				new Trainer("B", new Pokemon[]{new Pokemon(13, 4, false, true)}, 100),
				new Trainer("C", new Pokemon[]{new Pokemon(32, 5, false, true), new Pokemon(29, 4, false, true)}, 100),
				new Trainer("D", new Pokemon[]{new Pokemon(22, 2, false, true), new Pokemon(22, 3, false, true), new Pokemon(22, 4, false, true)}, 100),
				new Trainer("TN 1", new Pokemon[]{new Pokemon(22, 9, false, true)}, 100), // 5
				new Trainer("TN 2", new Pokemon[]{new Pokemon(22, 9, false, true)}, 100),
				new Trainer("TN 3", new Pokemon[]{new Pokemon(29, 10, false, true)}, 100),
				new Trainer("TN 4", new Pokemon[]{new Pokemon(59, 11, false, true)}, 100),
				new Trainer("TN 5", new Pokemon[]{new Pokemon(41, 10, false, true)}, 100),
				new Trainer("TN 6", new Pokemon[]{new Pokemon(73, 10, false, true)}, 100), // 10
				new Trainer("TN 7", new Pokemon[]{new Pokemon(90, 10, false, true)}, 100),
				new Trainer("TN 8", new Pokemon[]{new Pokemon(52, 12, false, true)}, 100),
				new Trainer("Rick 1", new Pokemon[]{new Pokemon(66, 11, false, true), new Pokemon(111, 12, false, true), new Pokemon(44, 13, false, true), new Pokemon(120, 13, false, true)}, 400, new Item(93), 2),
				new Trainer("1 Gym A", new Pokemon[]{new Pokemon(13, 11, false, true), new Pokemon(13, 12, false, true), new Pokemon(13, 13, false, true)}, 200),
				new Trainer("1 Gym B", new Pokemon[]{new Pokemon(10, 12, false, true), new Pokemon(10, 13, false, true)}, 200), // 15
				new Trainer("1 Gym C", new Pokemon[]{new Pokemon(13, 13, false, true), new Pokemon(10, 14, false, true)}, 200),
				new Trainer("1 Gym Leader 1", new Pokemon[]{new Pokemon(10, 16, false, true), new Pokemon(153, 16, false, true), new Pokemon(135, 16, false, true), new Pokemon(226, 16, false, true), new Pokemon(177, 16, false, true), new Pokemon(14, 17, false, true)}, 500, new Item(152)),
				new Trainer("E", new Pokemon[]{new Pokemon(19, 16, false, true), new Pokemon(158, 16, false, true)}, 100),
				new Trainer("F", new Pokemon[]{new Pokemon(32, 8, false, true), new Pokemon(32, 9, false, true)}, 100),
				new Trainer("G", new Pokemon[]{new Pokemon(13, 10, false, true)}, 100), // 20
				new Trainer("H", new Pokemon[]{new Pokemon(26, 10, false, true)}, 100),
				new Trainer("I", new Pokemon[]{new Pokemon(47, 10, false, true), new Pokemon(55, 11, false, true)}, 100),
				new Trainer("J1", new Pokemon[]{new Pokemon(85, 15, false, true), new Pokemon(10, 16, false, true)}, 100),
				new Trainer("K", new Pokemon[]{new Pokemon(129, 17, false, true), new Pokemon(38, 18, false, true)}, 100),
				new Trainer("L", new Pokemon[]{new Pokemon(19, 18, false, true)}, 100), // 25
				new Trainer("M", new Pokemon[]{new Pokemon(141, 18, false, true)}, 100),
				new Trainer("N", new Pokemon[]{new Pokemon(42, 18, false, true)}, 100),
				new Trainer("O1", new Pokemon[]{new Pokemon(209, 19, false, true), new Pokemon(143, 18, false, true)}, 100),
				new Trainer("O2", new Pokemon[]{new Pokemon(137, 19, false, true), new Pokemon(139, 18, false, true)}, 100),
				new Trainer("P", new Pokemon[]{new Pokemon(26, 18, false, true)}, 100), // 30
				new Trainer("Q", new Pokemon[]{new Pokemon(90, 18, false, true), new Pokemon(82, 19, false, true)}, 100),
				new Trainer("R", new Pokemon[]{new Pokemon(64, 19, false, true)}, 100),
				new Trainer("S", new Pokemon[]{new Pokemon(137, 19, false, true), new Pokemon(137, 19, false, true), new Pokemon(137, 19, false, true), new Pokemon(71, 19, false, true)}, 100),
				new Trainer("Fred 1", new Pokemon[]{new Pokemon(1, 7, false, true)}, 400),
				new Trainer("T", new Pokemon[]{new Pokemon(42, 16, false, true), new Pokemon(52, 17, false, true)}, 100), // 35
				new Trainer("U1", new Pokemon[]{new Pokemon(104, 15, false, true), new Pokemon(94, 15, false, true)}, 100),
				new Trainer("U2", new Pokemon[]{new Pokemon(106, 15, false, true), new Pokemon(94, 15, false, true)}, 100),
				new Trainer("V", new Pokemon[]{new Pokemon(85, 17, false, true), new Pokemon(82, 16, false, true), new Pokemon(80, 17, false, true), new Pokemon(75, 18, false, true)}, 100),
				new Trainer("W", new Pokemon[]{new Pokemon(138, 20, false, true)}, 100),
				new Trainer("EP-AA", new Pokemon[]{new Pokemon(90, 22, false, true)}, 100), // 40
				new Trainer("EP-AB", new Pokemon[]{new Pokemon(36, 21, false, true), new Pokemon(82, 21, false, true), new Pokemon(80, 21, false, true)}, 100),
				new Trainer("EP-AC", new Pokemon[]{new Pokemon(114, 20, false, true)}, 100),
				new Trainer("EP-AD", new Pokemon[]{new Pokemon(181, 22, false, true)}, 100),
				new Trainer("EP-AE", new Pokemon[]{new Pokemon(126, 21, false, true), new Pokemon(123, 22, false, true), new Pokemon(120, 21, false, true)}, 100),
				new Trainer("EP-BA", new Pokemon[]{new Pokemon(138, 21, false, true), new Pokemon(138, 21, false, true)}, 100), // 45
				new Trainer("EP-BB", new Pokemon[]{new Pokemon(148, 22, false, true)}, 100),
				new Trainer("EP-BC", new Pokemon[]{new Pokemon(139, 22, false, true), new Pokemon(140, 22, false, true)}, 100),
				new Trainer("EP-BD", new Pokemon[]{new Pokemon(144, 21, false, true)}, 100),
				new Trainer("TN O1", new Pokemon[]{new Pokemon(23, 19, false, true)}, 100),
				new Trainer("TN O2", new Pokemon[]{new Pokemon(73, 19, false, true)}, 100), // 50
				new Trainer("TN O3", new Pokemon[]{new Pokemon(95, 19, false, true)}, 100),
				new Trainer("TN O4", new Pokemon[]{new Pokemon(104, 19, false, true)}, 100),
				new Trainer("TN O5", new Pokemon[]{new Pokemon(52, 20, false, true), new Pokemon(141, 20, false, true)}, 100),
				new Trainer("TN O6", new Pokemon[]{new Pokemon(90, 20, false, true), new Pokemon(144, 20, false, true)}, 100, 3),
				new Trainer("Scott 2", new Pokemon[]{new Pokemon(10, 5, false, true)}, 100), // 55
				new Trainer("2 Gym A", new Pokemon[]{new Pokemon(16, 22, false, true)}, 200),
				new Trainer("2 Gym B", new Pokemon[]{new Pokemon(16, 22, false, true)}, 200),
				new Trainer("2 Gym C", new Pokemon[]{new Pokemon(14, 20, false, true)}, 200),
				new Trainer("2 Gym D", new Pokemon[]{new Pokemon(14, 20, false, true)}, 200),
				new Trainer("2 Gym E", new Pokemon[]{new Pokemon(20, 21, false, true)}, 200), // 60
				new Trainer("2 Gym F", new Pokemon[]{new Pokemon(20, 21, false, true)}, 200),
				new Trainer("2 Gym G", new Pokemon[]{new Pokemon(19, 22, false, true), new Pokemon(19, 23, false, true)}, 200),
				new Trainer("2 Gym H", new Pokemon[]{new Pokemon(19, 22, false, true), new Pokemon(19, 23, false, true)}, 200),
				new Trainer("2 Gym I", new Pokemon[]{new Pokemon(16, 22, false, true)}, 200),
				new Trainer("2 Gym J", new Pokemon[]{new Pokemon(14, 22, false, true)}, 200), // 65
				new Trainer("2 Gym Leader 1", new Pokemon[]{new Pokemon(16, 24, false, true), new Pokemon(126, 24, false, true), new Pokemon(179, 24, false, true), new Pokemon(92, 24, false, true), new Pokemon(89, 24, false, true), new Pokemon(20, 25, false, true)}, 500, new Item(101)),
				new Trainer("SA", new Pokemon[]{new Pokemon(151, 8, false, true)}, 100),
				new Trainer("SB", new Pokemon[]{new Pokemon(44, 9, false, true)}, 100),
				new Trainer("SC", new Pokemon[]{new Pokemon(35, 9, false, true)}, 100),
				new Trainer("SD", new Pokemon[]{new Pokemon(153, 8, false, true)}, 100), // 70
				new Trainer("X", new Pokemon[]{new Pokemon(132, 24, false, true), new Pokemon(163, 25, false, true)}, 100),
				new Trainer("Y", new Pokemon[]{new Pokemon(11, 24, false, true), new Pokemon(134, 27, false, true)}, 100),
				new Trainer("Z", new Pokemon[]{new Pokemon(105, 27, false, true)}, 100),
				new Trainer("AA", new Pokemon[]{new Pokemon(131, 29, false, true)}, 100),
				new Trainer("MS A", new Pokemon[]{new Pokemon(61, 29, false, true)}, 100), // 75
				new Trainer("MS B", new Pokemon[]{new Pokemon(92, 29, false, true), new Pokemon(160, 30, false, true)}, 100),
				new Trainer("MS C", new Pokemon[]{new Pokemon(115, 30, false, true)}, 100),
				new Trainer("MS D", new Pokemon[]{new Pokemon(155, 51, false, true), new Pokemon(161, 53, false, true)}, 100),
				new Trainer("TN MS 1", new Pokemon[]{new Pokemon(53, 30, false, true)}, 100, new Item(95)),
				new Trainer("AB", new Pokemon[]{new Pokemon(86, 28, false, true), new Pokemon(82, 30, false, true), new Pokemon(86, 29, false, true), new Pokemon(75, 30, false, true), new Pokemon(79, 30, false, true)}, 100), // 80
				new Trainer("AC", new Pokemon[]{new Pokemon(49, 30, false, true), new Pokemon(53, 31, false, true)}, 100),
				new Trainer("AD", new Pokemon[]{new Pokemon(98, 30, false, true), new Pokemon(102, 31, false, true), new Pokemon(105, 31, false, true)}, 100),
				new Trainer("AE", new Pokemon[]{new Pokemon(96, 37, false, true)}, 100),
				new Trainer("AF", new Pokemon[]{new Pokemon(110, 38, false, true)}, 100),
				new Trainer("AG", new Pokemon[]{new Pokemon(73, 29, false, true), new Pokemon(91, 30, false, true)}, 100), // 85
				new Trainer("AH", new Pokemon[]{new Pokemon(97, 30, false, true)}, 100),
				new Trainer("AI", new Pokemon[]{new Pokemon(89, 30, false, true), new Pokemon(47, 31, false, true)}, 100),
				new Trainer("AJ", new Pokemon[]{new Pokemon(46, 30, false, true), new Pokemon(27, 31, false, true)}, 100),
				new Trainer("Fred 2", new Pokemon[]{new Pokemon(10, 18, false, true)}, 500),
				new Trainer("3 Gym A", new Pokemon[]{new Pokemon(33, 29, false, true), new Pokemon(73, 29, false, true), new Pokemon(33, 30, false, true), new Pokemon(34, 31, false, true)}, 200), // 90
				new Trainer("3 Gym B", new Pokemon[]{new Pokemon(23, 30, false, true), new Pokemon(36, 31, false, true), new Pokemon(42, 32, false, true)}, 200),
				new Trainer("3 Gym C", new Pokemon[]{new Pokemon(62, 31, false, true), new Pokemon(63, 31, false, true)}, 200),
				new Trainer("3 Gym D", new Pokemon[]{new Pokemon(24, 31, false, true), new Pokemon(25, 31, false, true)}, 200),
				new Trainer("3 Gym Leader 1", new Pokemon[]{new Pokemon(25, 31, false, true), new Pokemon(37, 32, false, true), new Pokemon(34, 32, false, true), new Pokemon(42, 32, false, true), new Pokemon(74, 32, false, true), new Pokemon(63, 33, false, true)}, 500, new Item(122)),
				new Trainer("RR A", new Pokemon[]{new Pokemon(71, 30, false, true), new Pokemon(68, 31, false, true)}, 100), // 95
				new Trainer("RR B", new Pokemon[]{new Pokemon(27, 30, false, true), new Pokemon(30, 31, false, true)}, 100),
				new Trainer("RR C", new Pokemon[]{new Pokemon(108, 30, false, true), new Pokemon(107, 31, false, true)}, 100),
				new Trainer("CR", new Pokemon[]{new Pokemon(86, 18, false, true), new Pokemon(45, 19, false, true)}, 100),
				new Trainer("CS", new Pokemon[]{new Pokemon(36, 20, false, true)}, 100),
				new Trainer("CT", new Pokemon[]{new Pokemon(68, 20, false, true)}, 100), // 100
				new Trainer("CU", new Pokemon[]{new Pokemon(42, 20, false, true)}, 100),
				new Trainer("E", new Pokemon[]{new Pokemon(134, 5, false, true), new Pokemon(117, 6, false, true)}, 100),
				new Trainer("K", new Pokemon[]{new Pokemon(1, 15, false, true), new Pokemon(4, 15, false, true), new Pokemon(7, 15, false, true)}, 100),
				new Trainer("AB1", new Pokemon[]{new Pokemon(156, 20, false, true)}, 100),
				new Trainer("AC1", new Pokemon[]{new Pokemon(158, 20, false, true)}, 100), // 105
				new Trainer("AG", new Pokemon[]{new Pokemon(15, 34, false, true), new Pokemon(31, 34, false, true)}, 100),
				new Trainer("AH", new Pokemon[]{new Pokemon(171, 35, false, true), new Pokemon(172, 36, false, true)}, 100),
				new Trainer("AI", new Pokemon[]{new Pokemon(57, 35, false, true), new Pokemon(121, 35, false, true)}, 100),
				new Trainer("AJ", new Pokemon[]{new Pokemon(83, 35, false, true), new Pokemon(76, 35, false, true), new Pokemon(80, 36, false, true), new Pokemon(87, 34, false, true)}, 100),
				new Trainer("AK", new Pokemon[]{new Pokemon(138, 35, false, true), new Pokemon(150, 36, false, true)}, 100), // 110
				new Trainer("AL", new Pokemon[]{new Pokemon(160, 36, false, true), new Pokemon(164, 36, false, true), new Pokemon(34, 36, false, true)}, 100),
				new Trainer("AM", new Pokemon[]{new Pokemon(154, 36, false, true), new Pokemon(140, 35, false, true), new Pokemon(170, 35, false, true)}, 100),
				new Trainer("AN", new Pokemon[]{new Pokemon(102, 35, false, true), new Pokemon(95, 35, false, true), new Pokemon(96, 36, false, true), new Pokemon(175, 37, false, true)}, 100),
				new Trainer("AO", new Pokemon[]{new Pokemon(132, 37, false, true), new Pokemon(143, 40, false, true), new Pokemon(135, 37, false, true), new Pokemon(133, 37, false, true)}, 100),
				new Trainer("AP", new Pokemon[]{new Pokemon(127, 36, false, true), new Pokemon(164, 37, false, true), new Pokemon(182, 36, false, true), new Pokemon(180, 37, false, true)}, 100), // 115
				new Trainer("AQ", new Pokemon[]{new Pokemon(177, 38, false, true), new Pokemon(193, 39, false, true), new Pokemon(185, 37, false, true), new Pokemon(12, 37, false, true)}, 100),
				new Trainer("TN S1", new Pokemon[]{new Pokemon(60, 36, false, true), new Pokemon(37, 37, false, true)}, 100),
				new Trainer("TN S2", new Pokemon[]{new Pokemon(31, 36, false, true), new Pokemon(74, 37, false, true)}, 100),
				new Trainer("TN S3", new Pokemon[]{new Pokemon(154, 36, false, true), new Pokemon(152, 37, false, true)}, 100),
				new Trainer("TN S4", new Pokemon[]{new Pokemon(160, 36, false, true), new Pokemon(159, 37, false, true)}, 100), // 120
				new Trainer("TN S5", new Pokemon[]{new Pokemon(91, 37, false, true), new Pokemon(97, 37, false, true)}, 100),
				new Trainer("TN S6", new Pokemon[]{new Pokemon(146, 38, false, true), new Pokemon(225, 37, false, true)}, 100),
				new Trainer("TN S7", new Pokemon[]{new Pokemon(83, 37, false, true), new Pokemon(87, 38, false, true)}, 100),
				new Trainer("TN S8", new Pokemon[]{new Pokemon(39, 38, false, true), new Pokemon(27, 38, false, true)}, 100),
				new Trainer("TN S9", new Pokemon[]{new Pokemon(148, 38, false, true), new Pokemon(149, 38, false, true)}, 100), // 125
				new Trainer("TN S10", new Pokemon[]{new Pokemon(164, 39, false, true), new Pokemon(144, 38, false, true)}, 100, 9),
				new Trainer("TN S11", new Pokemon[]{new Pokemon(159, 37, false, true), new Pokemon(97, 37, false, true)}, 100),
				new Trainer("TN S12", new Pokemon[]{new Pokemon(176, 37, false, true), new Pokemon(182, 38, false, true)}, 100),
				new Trainer("TN S13", new Pokemon[]{new Pokemon(24, 38, false, true), new Pokemon(74, 37, false, true)}, 100),
				new Trainer("TN S14", new Pokemon[]{new Pokemon(56, 37, false, true), new Pokemon(25, 38, false, true)}, 100), // 130
				new Trainer("TN S15", new Pokemon[]{new Pokemon(105, 39, false, true), new Pokemon(99, 38, false, true)}, 100),
				new Trainer("TN S16", new Pokemon[]{new Pokemon(141, 38, false, true), new Pokemon(142, 37, false, true)}, 100, 8),
				new Trainer("4 Gym A", new Pokemon[]{new Pokemon(63, 37, false, true), new Pokemon(63, 38, false, true), new Pokemon(69, 39, false, true), new Pokemon(69, 40, false, true)}, 200),
				new Trainer("4 Gym B", new Pokemon[]{new Pokemon(65, 40, false, true)}, 200),
				new Trainer("4 Gym C", new Pokemon[]{new Pokemon(59, 40, false, true), new Pokemon(60, 39, false, true), new Pokemon(61, 40, false, true), new Pokemon(60, 39, false, true)}, 200), // 135
				new Trainer("4 Gym D", new Pokemon[]{new Pokemon(63, 41, false, true)}, 200),
				new Trainer("4 Gym Leader 1", new Pokemon[]{new Pokemon(193, 41, false, true), new Pokemon(69, 41, false, true), new Pokemon(61, 41, false, true), new Pokemon(60, 41, false, true), new Pokemon(65, 40, false, true), new Pokemon(194, 42, false, true)}, 500, new Item(160)),
				new Trainer("AR", new Pokemon[]{new Pokemon(15, 40, false, true), new Pokemon(60, 40, false, true), new Pokemon(15, 40, false, true), new Pokemon(12, 40, false, true), new Pokemon(178, 40, false, true), new Pokemon(203, 40, false, true)}, 100),
				new Trainer("AS", new Pokemon[]{new Pokemon(91, 42, false, true), new Pokemon(79, 43, false, true), new Pokemon(140, 41, false, true), new Pokemon(136, 42, false, true)}, 100),
				new Trainer("AT", new Pokemon[]{new Pokemon(138, 41, false, true), new Pokemon(138, 42, false, true), new Pokemon(136, 43, false, true), new Pokemon(149, 42, false, true)}, 100), // 140
				new Trainer("AU", new Pokemon[]{new Pokemon(155, 43, false, true), new Pokemon(125, 43, false, true), new Pokemon(119, 42, false, true)}, 100),
				new Trainer("BVA", new Pokemon[]{new Pokemon(126, 12, false, true), new Pokemon(120, 12, false, true)}, 100),
				new Trainer("BVB", new Pokemon[]{new Pokemon(123, 12, false, true), new Pokemon(126, 12, false, true)}, 100),
				new Trainer("BVC", new Pokemon[]{new Pokemon(120, 12, false, true), new Pokemon(123, 12, false, true)}, 100),
				new Trainer("BVD", new Pokemon[]{new Pokemon(126, 14, false, true), new Pokemon(120, 14, false, true), new Pokemon(123, 14, false, true)}, 100), // 145
				new Trainer("O1", new Pokemon[]{new Pokemon(178, 28, false, true), new Pokemon(187, 29, false, true), new Pokemon(144, 28, false, true)}, 100),
				new Trainer("CCA", new Pokemon[]{new Pokemon(174, 30, false, true), new Pokemon(175, 30, false, true), new Pokemon(176, 28, false, true)}, 100),
				new Trainer("CCB", new Pokemon[]{new Pokemon(179, 29, false, true), new Pokemon(171, 30, false, true), new Pokemon(169, 28, false, true)}, 100),
				new Trainer("AV", new Pokemon[]{new Pokemon(69, 42, false, true), new Pokemon(69, 43, false, true), new Pokemon(70, 44, false, true)}, 100),
				new Trainer("AW", new Pokemon[]{new Pokemon(45, 44, false, true), new Pokemon(72, 43, false, true), new Pokemon(46, 42, false, true)}, 100), // 150
				new Trainer("AX", new Pokemon[]{new Pokemon(136, 43, false, true), new Pokemon(79, 44, false, true)}, 100),
				new Trainer("AY", new Pokemon[]{new Pokemon(91, 43, false, true), new Pokemon(147, 44, false, true)}, 100),
				new Trainer("AZ", new Pokemon[]{new Pokemon(136, 44, false, true), new Pokemon(133, 44, false, true), new Pokemon(139, 45, false, true)}, 100),
				new Trainer("BA", new Pokemon[]{new Pokemon(141, 44, false, true), new Pokemon(144, 44, false, true)}, 100),
				new Trainer("BB", new Pokemon[]{new Pokemon(143, 45, false, true), new Pokemon(143, 45, false, true), new Pokemon(143, 45, false, true), new Pokemon(143, 45, false, true), new Pokemon(143, 45, false, true), new Pokemon(145, 44, false, true)}, 100), // 155
				new Trainer("BC", new Pokemon[]{new Pokemon(138, 44, false, true), new Pokemon(210, 44, false, true)}, 100),
				new Trainer("BD", new Pokemon[]{new Pokemon(210, 44, false, true), new Pokemon(138, 44, false, true)}, 100),
				new Trainer("BE", new Pokemon[]{new Pokemon(210, 43, false, true), new Pokemon(119, 44, false, true), new Pokemon(113, 44, false, true)}, 100),
				new Trainer("BF", new Pokemon[]{new Pokemon(39, 45, false, true)}, 100),
				new Trainer("BG", new Pokemon[]{new Pokemon(40, 45, false, true)}, 100), // 160
				new Trainer("BH", new Pokemon[]{new Pokemon(160, 45, false, true), new Pokemon(161, 44, false, true), new Pokemon(160, 45, false, true)}, 100),
				new Trainer("BI", new Pokemon[]{new Pokemon(165, 44, false, true), new Pokemon(127, 45, false, true), new Pokemon(128, 43, false, true)}, 100),
				new Trainer("BJ", new Pokemon[]{new Pokemon(167, 46, false, true), new Pokemon(170, 43, false, true), new Pokemon(172, 45, false, true)}, 100),
				new Trainer("BK", new Pokemon[]{new Pokemon(109, 45, false, true), new Pokemon(97, 45, false, true)}, 100),
				new Trainer("BL", new Pokemon[]{new Pokemon(219, 45, false, true)}, 100), // 165
				new Trainer("BM", new Pokemon[]{new Pokemon(220, 47, false, true), new Pokemon(221, 45, false, true), new Pokemon(220, 47, false, true), new Pokemon(221, 46, false, true)}, 100),
				new Trainer("BN", new Pokemon[]{new Pokemon(148, 47, false, true), new Pokemon(149, 45, false, true), new Pokemon(215, 47, false, true), new Pokemon(216, 45, false, true)}, 100),
				new Trainer("BO", new Pokemon[]{new Pokemon(12, 48, false, true)}, 100),
				new Trainer("BP", new Pokemon[]{new Pokemon(74, 46, false, true), new Pokemon(34, 46, false, true)}, 100),
				new Trainer("BQ", new Pokemon[]{new Pokemon(58, 46, false, true), new Pokemon(77, 46, false, true), new Pokemon(88, 47, false, true), new Pokemon(140, 45, false, true), new Pokemon(145, 46, false, true)}, 100), // 170
				new Trainer("BR", new Pokemon[]{new Pokemon(165, 46, false, true), new Pokemon(225, 47, false, true), new Pokemon(67, 48, false, true)}, 100),
				new Trainer("BS1", new Pokemon[]{new Pokemon(56, 47, false, true), new Pokemon(50, 46, false, true)}, 100),
				new Trainer("BS2", new Pokemon[]{new Pokemon(93, 47, false, true), new Pokemon(51, 46, false, true)}, 100),
				new Trainer("BT", new Pokemon[]{new Pokemon(167, 48, false, true), new Pokemon(170, 46, false, true), new Pokemon(178, 47, false, true), new Pokemon(170, 47, false, true)}, 100),
				new Trainer("BU", new Pokemon[]{new Pokemon(37, 45, false, true), new Pokemon(21, 46, false, true)}, 100), // 175
				new Trainer("BV", new Pokemon[]{new Pokemon(87, 46, false, true), new Pokemon(77, 45, false, true)}, 100),
				new Trainer("BW1", new Pokemon[]{new Pokemon(96, 45, false, true), new Pokemon(43, 44, false, true)}, 100),
				new Trainer("BW2", new Pokemon[]{new Pokemon(225, 45, false, true), new Pokemon(34, 45, false, true)}, 100),
				new Trainer("BX", new Pokemon[]{new Pokemon(115, 46, false, true), new Pokemon(124, 44, false, true), new Pokemon(119, 45, false, true)}, 100),
				new Trainer("BY", new Pokemon[]{new Pokemon(157, 48, false, true), new Pokemon(25, 48, false, true), new Pokemon(46, 47, false, true)}, 100), // 180
				new Trainer("BZ", new Pokemon[]{new Pokemon(113, 48, false, true), new Pokemon(116, 48, false, true), new Pokemon(122, 48, false, true)}, 100),
				new Trainer("CA", new Pokemon[]{new Pokemon(180, 46, false, true), new Pokemon(180, 47, false, true), new Pokemon(180, 48, false, true)}, 100),
				new Trainer("GTN1", new Pokemon[]{new Pokemon(147, 49, false, true), new Pokemon(96, 48, false, true), new Pokemon(31, 49, false, true)}, 100),
				new Trainer("GTN2", new Pokemon[]{new Pokemon(165, 49, false, true), new Pokemon(207, 48, false, true), new Pokemon(208, 48, false, true)}, 100),
				new Trainer("Fred 3", new Pokemon[]{new Pokemon(10, 5, false, true)}, 500), // 185
				new Trainer("5 Gym A", new Pokemon[]{new Pokemon(83, 49, false, true), new Pokemon(83, 49, false, true), new Pokemon(83, 50, false, true)}, 200),
				new Trainer("5 Gym B", new Pokemon[]{new Pokemon(77, 49, false, true)}, 200),
				new Trainer("5 Gym C", new Pokemon[]{new Pokemon(53, 48, false, true), new Pokemon(54, 49, false, true)}, 200),
				new Trainer("5 Gym D", new Pokemon[]{new Pokemon(87, 49, false, true), new Pokemon(88, 49, false, true)}, 200),
				new Trainer("5 Gym E", new Pokemon[]{new Pokemon(60, 49, false, true), new Pokemon(60, 50, false, true)}, 200), // 190
				new Trainer("5 Gym F", new Pokemon[]{new Pokemon(79, 49, false, true), new Pokemon(53, 50, false, true)}, 200),
				new Trainer("5 Gym G", new Pokemon[]{new Pokemon(91, 50, false, true)}, 200),
				new Trainer("5 Gym H", new Pokemon[]{new Pokemon(81, 49, false, true), new Pokemon(91, 49, false, true)}, 200),
				new Trainer("5 Gym Leader 1", new Pokemon[]{new Pokemon(91, 51, false, true), new Pokemon(88, 51, false, true), new Pokemon(81, 51, false, true), new Pokemon(79, 51, false, true), new Pokemon(77, 51, false, true), new Pokemon(84, 52, false, true)}, 500, new Item(111)),
				new Trainer("DA", new Pokemon[]{new Pokemon(33, 45, false, true), new Pokemon(34, 44, false, true), new Pokemon(34, 45, false, true)}, 200), // 195
				new Trainer("DB", new Pokemon[]{new Pokemon(36, 46, false, true), new Pokemon(37, 45, false, true)}, 100),
				new Trainer("DC", new Pokemon[]{new Pokemon(24, 46, false, true)}, 100),
				new Trainer("DD", new Pokemon[]{new Pokemon(25, 46, false, true)}, 100),
				new Trainer("DE", new Pokemon[]{new Pokemon(43, 46, false, true)}, 100),
				new Trainer("DF", new Pokemon[]{new Pokemon(30, 46, false, true), new Pokemon(31, 45, false, true)}, 100), // 200
				new Trainer("DG", new Pokemon[]{new Pokemon(39, 45, false, true), new Pokemon(40, 45, false, true)}, 100),
				new Trainer("CY", new Pokemon[]{new Pokemon(24, 43, false, true), new Pokemon(25, 43, false, true)}, 100),
				new Trainer("CZ", new Pokemon[]{new Pokemon(33, 45, false, true), new Pokemon(36, 46, false, true), new Pokemon(33, 46, false, true), new Pokemon(34, 44, false, true), new Pokemon(37, 44, false, true)}, 100),
//				new Trainer("R", new Pokemon[]{new Pokemon(-106, 24, false, true)}, 100),
//				new Trainer("S", new Pokemon[]{new Pokemon(-92, 20, false, true), new Pokemon(-92, 20, false, true), new Pokemon(-46, 15, false, true)}, 100),
//				new Trainer("T", new Pokemon[]{new Pokemon(-75, 18, false, true), new Pokemon(-77, 23, false, true)}, 100),
//				new Trainer("U", new Pokemon[]{new Pokemon(-84, 24, false, true)}, 100),
//				new Trainer("V", new Pokemon[]{new Pokemon(-86, 25, false, true)}, 100),
//				new Trainer("W", new Pokemon[]{new Pokemon(-61, 23, false, true), new Pokemon(-55, 23, false, true)}, 100),
//				new Trainer("X", new Pokemon[]{new Pokemon(-8, 24, false, true), new Pokemon(-80, 25, false, true)}, 100),
//				new Trainer("6 Gym A", new Pokemon[]{new Pokemon(-90, 32, false, true)}, 200),
//				new Trainer("6 Gym B", new Pokemon[]{new Pokemon(-87, 30, false, true)}, 200),
//				new Trainer("6 Gym C", new Pokemon[]{new Pokemon(-89, 24, false, true), new Pokemon(-89, 24, false, true)}, 200),
//				new Trainer("6 Gym D", new Pokemon[]{new Pokemon(-86, 24, false, true), new Pokemon(-86, 27, false, true)}, 200),
//				new Trainer("6 Gym E", new Pokemon[]{new Pokemon(-69, 29, false, true)}, 200),
//				new Trainer("6 Gym F", new Pokemon[]{new Pokemon(-90, 31, false, true)}, 200),
//				new Trainer("6 Gym Leader 1", new Pokemon[]{new Pokemon(-89, 29, false, true), new Pokemon(-90, 34, false, true), new Pokemon(-87, 36, false, true)}, 500),
//				new Trainer("Y", new Pokemon[]{new Pokemon(-93, 24, false, true), new Pokemon(-89, 23, false, true)}, 100),
//				new Trainer("Z", new Pokemon[]{new Pokemon(-75, 26, false, true)}, 100),
//				new Trainer("AA", new Pokemon[]{new Pokemon(-97, 19, false, true), new Pokemon(-98, 26, false, true)}, 100),
//				new Trainer("BB", new Pokemon[]{new Pokemon(-41, 26, false, true)}, 100),
//				new Trainer("CC", new Pokemon[]{new Pokemon(-46, 25, false, true), new Pokemon(-47, 31, false, true)}, 100),
//				new Trainer("CA", new Pokemon[]{new Pokemon(-18, 10, false, true), new Pokemon(-21, 10, false, true)}, 100),
//				new Trainer("CB", new Pokemon[]{new Pokemon(-18, 21, false, true), new Pokemon(-19, 26, false, true), new Pokemon(-19, 28, false, true)}, 100),
//				new Trainer("CD", new Pokemon[]{new Pokemon(-38, 21, false, true), new Pokemon(-40, 21, false, true)}, 100),
//				new Trainer("CE", new Pokemon[]{new Pokemon(-33, 20, false, true), new Pokemon(-33, 20, false, true), new Pokemon(-34, 25, false, true)}, 100),
//				new Trainer("DD", new Pokemon[]{new Pokemon(-80, 10, false, true), new Pokemon(-74, 8, false, true)}, 100),
//				new Trainer("EE", new Pokemon[]{new Pokemon(-18, 9, false, true), new Pokemon(-18, 9, false, true), new Pokemon(-18, 9, false, true), new Pokemon(-18, 9, false, true)}, 100),
//				new Trainer("FF", new Pokemon[]{new Pokemon(-25, 25, false, true), new Pokemon(-25, 25, false, true)}, 100),
//				new Trainer("GG", new Pokemon[]{new Pokemon(-63, 23, false, true), new Pokemon(-64, 27, false, true)}, 100),
//				new Trainer("HH", new Pokemon[]{new Pokemon(-65, 23, false, true), new Pokemon(-66, 27, false, true)}, 100),
//				new Trainer("II", new Pokemon[]{new Pokemon(-69, 30, false, true)}, 100),
//				new Trainer("JJ", new Pokemon[]{new Pokemon(-68, 20, false, true), new Pokemon(-68, 25, false, true), new Pokemon(-68, 25, false, true)}, 100),
//				new Trainer("VV", new Pokemon[]{new Pokemon(-28, 36, false, true)}, 100),
//				new Trainer("Rival 4", new Pokemon[]{new Pokemon(-131, 33, false, true), new Pokemon(-25, 24, false, true), new Pokemon(-22, 29, false, true), new Pokemon(-41, 28, false, true)}, 500),
//				new Trainer("MM", new Pokemon[]{new Pokemon(-35, 12, false, true), new Pokemon(-35, 12, false, true), new Pokemon(-36, 12, false, true), new Pokemon(-36, 12, false, true)}, 100),
//				new Trainer("NN", new Pokemon[]{new Pokemon(-14, 13, false, true), new Pokemon(-29, 13, false, true), new Pokemon(-29, 14, false, true)}, 100),
//				new Trainer("7 Gym A", new Pokemon[]{new Pokemon(-75, 30, false, true), new Pokemon(-75, 30, false, true)}, 200),
//				new Trainer("7 Gym B", new Pokemon[]{new Pokemon(-78, 36, false, true)}, 200),
//				new Trainer("7 Gym C", new Pokemon[]{new Pokemon(-80, 26, false, true), new Pokemon(-81, 29, false, true)}, 200),
//				new Trainer("7 Gym D", new Pokemon[]{new Pokemon(-84, 26, false, true), new Pokemon(-85, 33, false, true)}, 200),
//				new Trainer("7 Gym Leader 1", new Pokemon[]{new Pokemon(-78, 39, false, true), new Pokemon(-85, 30, false, true), new Pokemon(-76, 41, false, true)}, 500),
//				new Trainer("Rival 5", new Pokemon[]{new Pokemon(-132, 36, false, true), new Pokemon(-25, 27, false, true), new Pokemon(-22, 32, false, true), new Pokemon(-41, 29, false, true), new Pokemon(-78, 35, false, true)}, 500),
//				new Trainer("KK", new Pokemon[]{new Pokemon(-96, 25, false, true), new Pokemon(-97, 20, false, true), new Pokemon(-98, 30, false, true)}, 100),
//				new Trainer("LL", new Pokemon[]{new Pokemon(-39, 25, false, true), new Pokemon(-41, 25, false, true), new Pokemon(-39, 30, false, true), new Pokemon(-41, 30, false, true)}, 100),
//				new Trainer("OO", new Pokemon[]{new Pokemon(-39, 30, false, true), new Pokemon(-41, 30, false, true)}, 100),
//				new Trainer("PP", new Pokemon[]{new Pokemon(-45, 33, false, true)}, 100),
//				new Trainer("Rival 6", new Pokemon[]{new Pokemon(-132, 45, false, true), new Pokemon(-25, 33, false, true), new Pokemon(-23, 40, false, true), new Pokemon(-41, 34, false, true), new Pokemon(-78, 44, false, true), new Pokemon(-87, 38, false, true)}, 500),
//				new Trainer("QQ", new Pokemon[]{new Pokemon(-64, 30, false, true), new Pokemon(-66, 30, false, true)}, 100),
//				new Trainer("RR", new Pokemon[]{new Pokemon(-78, 35, false, true)}, 100),
//				new Trainer("SS", new Pokemon[]{new Pokemon(-25, 30, false, true), new Pokemon(-25, 30, false, true), new Pokemon(-69, 28, false, true), new Pokemon(-70, 28, false, true), new Pokemon(-64, 30, false, true), new Pokemon(-66, 30, false, true)}, 100),
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
	}
	
	private static void setMoveset(String string, int i, Move a, Move b, Move c, Move d) {
		for (Trainer tr : trainers) {
			if (tr.getName().equals(string)) {
				tr.getTeam()[i - 1].moveset = new Moveslot[]{new Moveslot(a), new Moveslot(b), new Moveslot(c), new Moveslot(d)};
				if (!modifiedTrainers.contains(tr)) modifiedTrainers.add(tr);
			}
		}
		
	}
	
	private static void setAbility(String string, int i, Ability a) {
		for (Trainer tr : trainers) {
			if (tr.getName().equals(string)) {
				tr.getTeam()[i - 1].ability = a;
			}
		}
		
	}
	
	private static void writePokemon() {
		try {
			FileWriter writer = new FileWriter("PokemonInfo.txt");
			
			for (int i = 1; i < 241; i++) {
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
				p.setAbility(0);
				Ability ability1 = p.ability;
				String ability = ability1.toString() + " / ";
				p.setAbility(1);
				ability = p.ability == ability1 ? ability + "None" : ability + p.ability.toString();			
				writer.write(ability + "\n\n");
				
				writer.write("Base Stats:\n");
				String stats = "";
				for (int j = 0; j < p.baseStats.length; j++) {
					stats += p.getBaseStat(j) + " " + p.getStatType(j) + "/ ";
				}
				stats += p.getBST() + " BST";
				writer.write(stats + "\n\n");
				
				writer.write("Level Up:\n");
				for (int j = 0; j < p.movebank.length; j++) {
					Node n = p.movebank[j];
					while (n != null) {
						String move = j + 1 + " - " + n.data.toString() + "\n";
						n = n.next;
						writer.write(move);
					}
				}
				writer.write("\n");
			}
			writer.close();
			writeUnusedMoves();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private static void writeUnusedMoves() {
		try {
			FileWriter writer = new FileWriter("PokemonInfo.txt", true);
			writer.write("Unused moves:\n");
			ArrayList<Move> unused = new ArrayList<>();
			ArrayList<Move> unusedButTM = new ArrayList<>();
			Set<Move> moves = new HashSet<>(Arrays.asList(Move.values()));
			for (int i = 1; i < 241; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				ArrayList<Move> movebank = new ArrayList<>();
				for (int j = 0; j < p.movebank.length; j++) {
					Node n = p.movebank[j];
					while (n != null) {
						movebank.add(n.data);
						n = n.next;
					}
				}
				moves.removeAll(movebank);
			}
			for (Move m : moves) {
				if (m.isTM()) {
					unusedButTM.add(m);
				} else {
					unused.add(m);
				}
			}
			for (Move m : unused) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nTM Only:\n");
			for (Move m : unusedButTM) {
				writer.write(m.toString() + "\n");
			}
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}



	private static void writeTrainers() {
		try {
			FileWriter writer = new FileWriter("TrainerInfo.txt");
			
			for (Trainer tr : trainers) {
				String name = "Trainer " + tr.getName();
				while (name.length() < 28) {
					name += " ";
				}
				writer.write(name);
				for (int i = 0; i < tr.getTeam().length; i++) {
					Pokemon p = tr.getTeam()[i];
					writer.write(p.name + " Lv. " + p.level);
					if (i != tr.getTeam().length - 1) writer.write(", ");
				}
				writer.write("\n");
				
				if (tr.getName().contains("Leader") || tr.getName().contains("Rick") || tr.getName().contains("Scott") || tr.getName().contains("Fred")) {
					writer.write("\n");
					writer.write(tr.getName() + "\n");
					for (Pokemon p : tr.getTeam()) {
						String pName = p.name + " (Lv. " + p.level + ")";
						while (pName.length() < 25) {
							pName += " ";
						}
						pName += "/";
						writer.write(pName);
						
						String aName = "   " + p.ability.toString();
						while (aName.length() < 19) {
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
			
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeEncounters() {
		try {
			FileWriter writer = new FileWriter("WildPokemon.txt");
			
			ArrayList<Encounter> encounters = Encounter.getEncounters(0, 73, 39, "Standard", "", false); // route 22
			writer.write("Route 22");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(0, 73, 38, "Standard", "", false); // route 23
			writer.write("Route 23");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(0, 74, 38, "Standard", "", false); // route 42
			writer.write("Route 42");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(0, 74, 38, "Fishing", "", false); // route 42
			writer.write("Route 42");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(0, 74, 38, "Surfing", "", false); // route 42
			writer.write("Route 42");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(4, 73, 43, "Standard", "", false); // route 24
			writer.write("Route 24 pt. 1");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(11, 44, 55, "Standard", "", false); // route 24 pt. 2
			writer.write("Route 24 pt. 2");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(11, 43, 55, "Standard", "", false); // gelb forest
			writer.write("Gelb Forest");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(11, 43, 55, "Fishing", "", false); // gelb forest
			writer.write("Gelb Forest");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(11, 43, 55, "Surfing", "", false); // gelb forest
			writer.write("Gelb Forest");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(11, 73, 56, "Standard", "", false); // route 25
			writer.write("Route 25");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(13, 73, 38, "Fishing", "", false); // sicab city
			writer.write("Sicab City");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(13, 73, 38, "Surfing", "", false); // sicab city
			writer.write("Sicab City");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(14, 73, 38, "Standard", "", false); // energy plant A
			writer.write("Energy Plant A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(16, 73, 38, "Standard", "", false); // energy plant B
			writer.write("Energy Plant B");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(16, 73, 38, "Fishing", "", false); // energy plant B
			writer.write("Energy Plant B");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(16, 73, 38, "Surfing", "", false); // energy plant B
			writer.write("Energy Plant B");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(22, 73, 38, "Standard", "", false); // route 40
			writer.write("Route 40");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(13, 73, 38, "Standard", "", false); // route 26
			writer.write("Route 26");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(24, 73, 38, "Standard", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(24, 73, 38, "Fishing", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(24, 73, 38, "Surfing", "", false); // mt. splinkty 1A
			writer.write("Mt. Splinkty 1A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(25, 73, 38, "Standard", "", false); // mt. splinkty 2B
			writer.write("Mt. Splinkty 2B");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(26, 73, 38, "Standard", "", false); // mt. splinkty 3B
			writer.write("Mt. Splinkty 3B");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(27, 73, 38, "Standard", "", false); // mt. splinkty 3A
			writer.write("Mt. Splinkty 3A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(28, 73, 38, "Standard", "", false); // route 27
			writer.write("Route 27");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(28, 73, 57, "Fishing", "", false); // route 41
			writer.write("Route 41");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(28, 73, 57, "Surfing", "", false); // route 41
			writer.write("Route 41");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(28, 73, 57, "Standard", "", false); // route 41
			writer.write("Route 41");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(4, 73, 42, "Standard", "", false); // route 36
			writer.write("Route 36");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(4, 73, 42, "Fishing", "", false); // route 36
			writer.write("Route 36");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(4, 73, 42, "Surfing", "", false); // route 36
			writer.write("Route 36");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(33, 73, 21, "Standard", "", false); // route 28
			writer.write("Route 28");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(35, 73, 42, "Standard", "", false); // electric tunnel 01
			writer.write("Electric Tunnel 01");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(36, 73, 42, "Standard", "", false); // route 29
			writer.write("Route 29");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(38, 73, 31, "Standard", "", false); // icy fields
			writer.write("Icy Fields");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(38, 73, 62, "Standard", "", false); // route 30
			writer.write("Route 30");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(38, 73, 62, "Fishing", "", false); // route 30
			writer.write("Route 30");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(38, 73, 62, "Surfing", "", false); // route 30
			writer.write("Route 30");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(33, 0, 33, "Standard", "", false); // peaceful park
			writer.write("Peaceful Park");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(33, 0, 33, "Fishing", "", false); // peaceful park
			writer.write("Peaceful Park");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(33, 0, 33, "Surfing", "", false); // peaceful park
			writer.write("Peaceful Park");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(77, 73, 62, "Surfing", "", false); // mindagan lake
			writer.write("Mindagan Lake");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(77, 73, 62, "Fishing", "", false); // mindagan lake
			writer.write("Mindagan Lake");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(78, 73, 62, "Standard", "", false); // mindagan cavern 1A
			writer.write("Mindagan Cavern 1A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(80, 73, 41, "Standard", "", false); // route 31
			writer.write("Route 31");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(80, 73, 41, "Fishing", "", false); // route 31
			writer.write("Route 31");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(80, 73, 41, "Surfing", "", false); // route 31
			writer.write("Route 31");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(83, 57, 41, "Standard", "", false); // shadow ravine 1A
			writer.write("Shadow Ravine 1A");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(90, 73, 41, "Standard", "", false); // shadow ravine 0
			writer.write("Shadow Ravine 0");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(83, 58, 41, "Standard", "", false); // route 32
			writer.write("Route 32");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(83, 58, 41, "Fishing", "", false); // route 32
			writer.write("Route 32");
			writer.write(writeEncounter(encounters));
			
			encounters = Encounter.getEncounters(83, 58, 41, "Surfing", "", false); // route 32
			writer.write("Route 32");
			writer.write(writeEncounter(encounters));
			
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}



	private static String writeEncounter(ArrayList<Encounter> encounters) {
		String result = "==================================================================================================================================\n";
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
			FileWriter writer = new FileWriter("MovesInfo.txt");
			
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
					result += m.getbp(null, null) + " / ";
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

}
