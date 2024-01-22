package Swing;

import javax.swing.border.EmptyBorder;

import Entity.PlayerCharacter;
import Overworld.GamePanel;
import Swing.Field.Effect;
import Swing.Field.FieldEffect;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Battle extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943929896582623587L;
	private JPanel playerPanel;
	private JGradientButton[] moveButtons;
	private Pokemon foe;
	private Trainer foeTrainer;
	private JProgressBar healthBar;
	private JProgressBar expBar;
	private JLabel currentHPLabel;
	private JLabel slashLabel;
	private JLabel maxHPLabel;
	private JLabel foeText;
	private JProgressBar foeHealthBar;
	public static Player me;
//	private JTextField idInput; // debug
//	private JTextField levelInput; // debug
	private JLabel currentText;
	private JGradientButton[] party;
	private JLabel partySprites[]; 
	public Field field;
	public JLabel userSprite;
	public JLabel foeSprite;
	public JLabel weather;
	public JLabel terrain;
	private JPanel foePartyPanel;
	
	private JButton catchButton;
//	private JButton addButton; // debug
	//private JButton healButton;
	private JButton runButton;
	private JLabel userStatus;
	private JLabel foeStatus;
	
	private JRadioButton pokeballButton;
	private JRadioButton greatballButton;
	private JRadioButton ultraballButton;
	private JLabel pokeballLabel;
	private JLabel greatballLabel;
	private JLabel ultraballLabel;
	private JProgressBar[] partyHP;
//	private ButtonGroup time;
//	private JRadioButton morning;
//	private JRadioButton day;
//	private JRadioButton night;
//	private JRadioButton standard;
//	private JRadioButton rdbtnFishing;
//	private JRadioButton rdbtnSurfing;
//	private JRadioButton rdbtnHeadbutt;
	//private JComboBox<Trainer> trainerSelect;
	//private Trainer[] trainers;
	private JButton infoButton;
	private JButton calcButton;
//	private JButton exitButton; // debug
	private int trainerIndex;
	private JLabel caughtIndicator;
	public int staticPokemonID = -1;
	
	private BattleCloseListener battleCloseListener;
	private TextPane console;
	

	public Battle(PlayerCharacter playerCharacter, Trainer foeT, int trainerIndex, GamePanel gp, int area, int x, int y, String type) {
		me = playerCharacter.p;
		this.trainerIndex = trainerIndex;
		int faintIndex = 0;
		while (me.getCurrent().isFainted()) {
			me.swap(me.team[++faintIndex], faintIndex);
		}
		
		setTitle("Battle");
		
		foe = new Pokemon(10, 5, false, false);
	    
	    field = new Field();
	    Pokemon.field = field;
		
	    // Initializing panel
	    setResizable(false);
        setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
        setBounds(0, 0, gp.screenWidth, gp.screenHeight);


        playerPanel = new JPanel();
        playerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        playerPanel.setOpaque(false); // Make the panel transparent
        playerPanel.setLayout(null);

        // Set the content pane to the playerPanel
        setContentPane(playerPanel);

        // Set the location of the Box in the center of the screen
        setLocationRelativeTo(null);
        
        // Decide between trainer or wild encounter
 		if (foeT != null) {
 			foeTrainer = foeT;
 			foe = foeTrainer.getTeam()[0];
 			JOptionPane.showMessageDialog(null, "\nYou are challenged by " + foeTrainer.toString() + "!\n" + foeTrainer.toString() + " sends out " + foeTrainer.getCurrent().name + "!");
 		} else {
 			foe = encounterPokemon(area, x, y, me.random, type);
 		}

        // Initialize frame
        initialize(playerCharacter, gp);
		
		Pokemon fasterInit = me.getCurrent().getFaster(foe, 0, 0);
		Pokemon slowerInit = fasterInit == me.getCurrent() ? foe : me.getCurrent();
		fasterInit.swapIn(slowerInit, me, true);
		slowerInit.swapIn(fasterInit, me, true);
		
		updateField(field);
		displayParty();
	}

	private void initialize(PlayerCharacter pl, GamePanel gp) {
		// Initializing current elements
		
//		idInput = createJTextField(2, 650, 310, 27, 20);
//		levelInput = createJTextField(2, 690, 310, 27, 20);
		
		/*
		 * Set text area
		 */
		console = new TextPane();
		
		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setBounds(10, 340, 610, 190);
		
		console.setScrollPane(scrollPane);
		
		playerPanel.add(scrollPane);
		Pokemon.console = console;
		
		/*
		 * Set current movebuttons
		 */
		moveButtons = new JGradientButton[4];
		int[] xPositions = {196, 295, 196, 295};
		int[] yPositions = {247, 247, 280, 280};
		for (int i = 0; i < moveButtons.length; i++) {
			final int index = i;
			moveButtons[i] = new JGradientButton("");
			moveButtons[i].setBounds(xPositions[i], yPositions[i], 99, 33);
			playerPanel.add(moveButtons[i]);
			
			moveButtons[i].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            JOptionPane.showMessageDialog(null, me.getCurrent().moveset[index].move.getMoveSummary(me.getCurrent(), foe), "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        } else {
			        	if (me.getCurrent().moveset[index].currentPP == 0 && !me.getCurrent().movesetEmpty()) {
		        			JOptionPane.showMessageDialog(null, "No more PP remaining!");
		        			return;
		        		}
			        	if (!me.getCurrent().moveUsable(me.getCurrent().moveset[index].move) && !me.getCurrent().movesetEmpty()) {
		        			JOptionPane.showMessageDialog(null, me.getCurrent().moveset[index].move + " cannot be used!");
		        			return;
		        		}
			        	Move move = me.getCurrent().moveset[index].move;
		        		if (me.getCurrent().movesetEmpty()) move = Move.STRUGGLE;
		        		
			        	if (foe.trainerOwned()) {
			        		turn(me.getCurrent(), foe, move, foe.bestMove(me.getCurrent(), false, foeTrainer), pl, gp);
			        	} else {
			        		turn(me.getCurrent(), foe, move, foe.randomMove(), pl, gp);
			        	}
			        }
			    }
			});
		}
		
		/*
		 * Set battled status
		 */
		me.clearBattled();
		for (Pokemon p : me.team) {
			if (p != null) p.clearVolatile();
		}
		me.getCurrent().battled = true;
		
		/*
		 * Initialize status icons
		 */
		userStatus = new JLabel("");
		userStatus.setHorizontalAlignment(SwingConstants.CENTER);
		userStatus.setFont(new Font("Tahoma", Font.PLAIN, 8));
		userStatus.setText(me.getCurrent().status.getName());
		userStatus.setForeground(me.getCurrent().status.getTextColor());
		userStatus.setBackground(me.getCurrent().status.getColor());
		userStatus.setBounds(223, 179, 21, 20);
		userStatus.setVisible(true);
		userStatus.setOpaque(true);
		playerPanel.add(userStatus);
		
		foeStatus = new JLabel("");
		foeStatus.setHorizontalAlignment(SwingConstants.CENTER);
		foeStatus.setFont(new Font("Tahoma", Font.PLAIN, 8));
		foeStatus.setText(foe.status.getName());
		foeStatus.setForeground(foe.status.getTextColor());
		foeStatus.setBackground(foe.status.getColor());
		foeStatus.setBounds(563, 37, 21, 20);
		foeStatus.setVisible(true);
		foeStatus.setOpaque(true);
		playerPanel.add(foeStatus);
		
		/*
		 * Initialize caught indicator
		 */
		caughtIndicator = new JLabel(new ImageIcon(getIcon("/icons/ball.png")));
		caughtIndicator.setBounds(539, 39, 16, 16);

	    if (foeTrainer == null && pl.p.pokedex[foe.id] == 2) {
	    	caughtIndicator.setVisible(true);
	    } else {
	    	caughtIndicator.setVisible(false);
	    }
	    
	    playerPanel.add(caughtIndicator);
		
		/*
		 * Set Pokeball buttons and labels
		 */
		setBallComps(pl);
		
		/*
		 * Set current and foe
		 */
		userSprite = new JLabel("");
		userSprite.setBounds(223, 0, 200, 200);
		
		foeSprite = new JLabel("");
		foeSprite.setBounds(563, 60, 200, 200);
		
		userSprite.setVisible(true);
		foeSprite.setVisible(true);
		playerPanel.add(userSprite);
		playerPanel.add(foeSprite);
		
		updateCurrent(pl);
		updateFoe();
		
		/*
		 * Set weather and terrain area
		 */
		weather = new JLabel("");
		weather.setBounds(413, 10, 100, 200);
		weather.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		terrain = new JLabel("");
		terrain.setBounds(413, 230, 100, 75);
		terrain.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		playerPanel.add(weather);
		playerPanel.add(terrain);
		
		/*
		 * Set slash label
		 */
        slashLabel = new JLabel("/");
		slashLabel.setHorizontalAlignment(SwingConstants.CENTER);
		slashLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		slashLabel.setBounds(286, 222, 21, 14);
		playerPanel.add(slashLabel);
		
		/*
		 * Set bars
		 */
		healthBar = new JProgressBar(0, me.getCurrent().getStat(0));
		healthBar.setBackground(UIManager.getColor("Button.darkShadow"));
		healthBar.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		expBar = new JProgressBar(0, me.getCurrent().expMax);
		expBar.setForeground(new Color(0, 128, 255));
		
		currentHPLabel = new JLabel(me.getCurrent().getCurrentHP() + "");
		currentHPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentHPLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		currentHPLabel.setBounds(243, 222, 46, 14);
		playerPanel.add(currentHPLabel);
		
		maxHPLabel = new JLabel(me.getCurrent().getStat(0) + "");
		maxHPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		maxHPLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		maxHPLabel.setBounds(303, 222, 46, 14);
		playerPanel.add(maxHPLabel);
		
		updateBars(false);
		
		/*
		 * Set buttons
		 */
//		addButton = createJButton("ADD", new Font("Tahoma", Font.BOLD, 9), 645, 430, 75, 23);
		catchButton = createJButton("CATCH", new Font("Tahoma", Font.BOLD, 11), 638, 340, 89, 23);
//		healButton = createJButton("HEAL", new Font("Tahoma", Font.BOLD, 9), 645, 460, 75, 23);
		runButton = createJButton("RUN", new Font("Tahoma", Font.BOLD, 9), 645, 430, 75, 23);
		infoButton = createJButton("INFO", new Font("Tahoma", Font.BOLD, 9), 645, 460, 75, 23);
		calcButton = createJButton("CALC", new Font("Tahoma", Font.BOLD, 9), 645, 490, 75, 23);
//		exitButton = createJButton("EXIT", new Font("Tahoma", Font.BOLD, 9), 645, 400, 75, 23);
		
		/*
		 * @DEBUG: Used to force exit the frame
		 */
//		exitButton.addActionListener(e -> {
//			dispose();
//		});
		
		/*
		 * @DEBUG: Used to add the foe to the party
		 */
//		addButton.addActionListener(e -> {
//			if (!foe.isFainted()) {
//				me.catchPokemon(new Pokemon(foe.id, foe.getLevel(), true, false));
//				displayParty();
//				updateFoe();
//			}
//        });
		
		/*
		 * Attempts to catch the foe if !trainerOwned()
		 */
		catchButton.addActionListener(e -> {
		    if (!foe.isFainted() && !me.getCurrent().isFainted()) {
		    	if (foe.trainerOwned() && this.staticPokemonID == -1) {
		    		JOptionPane.showMessageDialog(null, "Cannot catch trainer's Pokemon!");
                    return;
		    	}
		        Random rand = new Random();
		        double ballBonus = 0;
		        
		        if (pokeballButton.isSelected()) {
		        	if (me.bag.count[1] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            console.writeln("\nUsed a Pokeball!");
		            me.bag.remove(Item.POKEBALL);
		            ballBonus = 1;
		        } else if (greatballButton.isSelected()) {
		        	if (me.bag.count[2] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            console.writeln("\nUsed a Great Ball!");
		            me.bag.remove(Item.GREAT_BALL);
		            ballBonus = 1.5;
		        } else if (ultraballButton.isSelected()) {
		        	if (me.bag.count[3] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            console.writeln("\nUsed an Ultra Ball!");
		            me.bag.remove(Item.ULTRA_BALL);
		            ballBonus = 2;
		        }
		        
		        int quotient = 3 * foe.getStat(0) - 2 * foe.currentHP;
		        double modQuotient = quotient * foe.catchRate * ballBonus;
		        double catchRate = modQuotient / (3 * foe.getStat(0));
		        
		        double statusBonus = 1;
		        if (foe.status != Status.HEALTHY) statusBonus = 1.5;
		        if (foe.status == Status.ASLEEP) statusBonus = 2;
		        
		        catchRate *= statusBonus;
		        int modifiedCatchRate = (int) Math.round(catchRate);
		        
		        int randomValue = rand.nextInt(255);
		        
		        if (randomValue <= modifiedCatchRate) {
		            //me.catchPokemon(new Pokemon(foe.id, foe.getLevel(), true, false));
		        	foe.playerOwned = true;
		        	foe.trainer = 1;
		        	me.catchPokemon(foe);
		        	updateCurrent(pl);
					updateStatus();
		        	updateBars(true);
					displayParty();
					updateFoe();
					if (me.copyBattle) copyToClipboard();
					JOptionPane.showMessageDialog(null, "You caught " + foe.name + "!");
					dispose();
		        } else {
		            console.writeln("Oh no! " + foe.name + " broke free!");
		            foe.move(me.getCurrent(),foe.randomMove(), me, null, null, false);
					foe.endOfTurn(me.getCurrent(), me);
					me.getCurrent().endOfTurn(foe, me);
					field.endOfTurn();
					updateCurrent(pl);
					updateStatus();
		        	updateBars(true);
					displayParty();
					updateFoe();
					if (me.wiped()) {
						wipe(pl, gp);
					}
		        }
		        console.writeln();
			    console.writeln("------------------------------");
		    }
		});
		
		/*
		 * @DEBUG: Used to full heal the party
		 */
//		healButton.addActionListener(e -> {
//			console.writeln();
//			for (Pokemon member : me.team) {
//				if (member != null) member.heal();
//			}
//			updateCurrent();
//			updateBars();
//			displayParty();
//			updateStatus();
//			playerPanel.repaint();
//			console.writeln();
//			console.writeln("------------------------------");
//        });
		
		/*
		 * Attempts to run from battle if foe !trainerOwned()
		 */
		runButton.addActionListener(e -> {
			if (!me.getCurrent().isFainted() && !foe.isFainted()) {
				if (foe.trainerOwned()) {
					JOptionPane.showMessageDialog(null, "What are you doing?!\nYou can't run from a trainer battle!");
					return;
	        	} else {
	        		Pokemon faster = me.getCurrent().getFaster(foe, 0, 0);
					double chance = faster == me.getCurrent() ? 1 : 0.5;
					chance = me.getCurrent().item == Item.SHED_SHELL ? 1 : chance;
					
					if (chance >= Math.random()) {
						if (me.copyBattle) copyToClipboard();
						JOptionPane.showMessageDialog(null, "Got away safely!");
						gp.keyH.resume();
						dispose();
						return;
					}
					console.writeln("\nCouldn't escape!");
	        		foe.move(me.getCurrent(),foe.randomMove(), me, null, null, false);
	        	}
				foe.endOfTurn(me.getCurrent(), me);
				me.getCurrent().endOfTurn(foe, me);
				field.endOfTurn();
			}
			updateCurrent(pl);
			updateBars(true);
			displayParty();
			updateStatus();
			if (foe.isFainted()) {
				if (me.copyBattle) copyToClipboard();
				JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
				dispose();
			}
			if (me.getCurrent().isFainted()) {
				if (me.wiped()) {
					wipe(pl, gp);
				}
				return;
			}
			console.writeln();
		    console.writeln("------------------------------");
        });
		
		infoButton.addActionListener(e -> {
			JPanel teamMemberPanel = new JPanel();
			teamMemberPanel.setLayout(new GridBagLayout());
		    
		    GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.insets = new Insets(5, 5, 5, 5); // Add space between components
		    
		    JLabel spriteLabel = new JLabel();
		    ImageIcon spriteIcon = new ImageIcon(me.getCurrent().getSprite());
		    spriteLabel.setIcon(spriteIcon);
		    
		    JLabel fSpriteLabel = new JLabel();
		    ImageIcon fSpriteIcon = new ImageIcon(foe.getSprite());
		    fSpriteLabel.setIcon(fSpriteIcon);

		    JLabel nicknameLabel;
		    nicknameLabel = new JLabel("N/A");
		    JLabel fNicknameLabel;
		    fNicknameLabel = new JLabel("N/A");
		    
		    JLabel[] statStages = new JLabel[7];
		    JPanel statPanel = new JPanel(new GridLayout(7, 1));
		    
		    JLabel[] fStatStages = new JLabel[7];
		    JPanel fStatPanel = new JPanel(new GridLayout(7, 1));
		    
		    if (this != null) {
		        nicknameLabel = new JLabel(me.getCurrent().nickname);
		        nicknameLabel.setFont(new Font(nicknameLabel.getFont().getName(), Font.BOLD, 18));
		        fNicknameLabel = new JLabel(foe.nickname);
		        fNicknameLabel.setFont(new Font(fNicknameLabel.getFont().getName(), Font.BOLD, 18));
		        
		        for (int i = 0; i < 7; i++) {
		        	String type;
		        	switch (i) {
		        	case 0:
		        		type = "Atk : ";
		        		break;
		        	case 1:
		        		type = "Def : ";
		        		break;
		        	case 2:
		        		type = "SpA : ";
		        		break;
		        	case 3:
		        		type = "SpD : ";
		        		break;
		        	case 4:
		        		type = "Spe : ";
		        		break;
		        	case 5:
		        		type = "Acc : ";
		        		break;
		        	case 6:
		        		type = "Eva : ";
		        		break;
	        		default:
	        			type = "ERROR ";
	        			break;
		        	}
		        	String amt = "";
		        	if (me.getCurrent().statStages[i] > 0) amt += "+";
		        	amt += me.getCurrent().statStages[i] + "";
		        	
		        	statStages[i] = new JLabel(type + amt);
		        	if (me.getCurrent().statStages[i] > 0) statStages[i].setForeground(Color.red.darker());
		        	if (me.getCurrent().statStages[i] < 0) statStages[i].setForeground(Color.blue.darker());
		        	statStages[i].setFont(new Font(statStages[i].getFont().getName(), Font.BOLD, 14));
		        	statStages[i].setSize(50, statStages[i].getHeight());
		        	
		        	statPanel.add(statStages[i]);
		        	
		        	String fAmt = "";
		        	if (foe.statStages[i] > 0) fAmt += "+";
		        	fAmt += foe.statStages[i] + "";
		        	
		        	fStatStages[i] = new JLabel(type + fAmt);
		        	if (foe.statStages[i] > 0) fStatStages[i].setForeground(Color.red.darker());
		        	if (foe.statStages[i] < 0) fStatStages[i].setForeground(Color.blue.darker());
		        	fStatStages[i].setFont(new Font(fStatStages[i].getFont().getName(), Font.BOLD, 14));
		        	fStatStages[i].setSize(50, fStatStages[i].getHeight());
		        	
		        	fStatPanel.add(fStatStages[i]);
		        }
		        
		    
		    JPanel idLabel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    idLabel.add(nicknameLabel);
		    idLabel.add(spriteLabel);
		    teamMemberPanel.add(idLabel, gbc);
		    gbc.gridx++;
		    
		    JPanel fIdLabel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    fIdLabel.add(fNicknameLabel);
		    fIdLabel.add(fSpriteLabel);
		    teamMemberPanel.add(fIdLabel, gbc);
		    gbc.gridx = 0;
		    gbc.gridy++;
		    
		    teamMemberPanel.add(statPanel, gbc);
		    gbc.gridx++;
		    
		    teamMemberPanel.add(fStatPanel, gbc);
		    gbc.gridx = 0;
		    gbc.gridy++;
		    
		    JPanel vStatusPanel = new JPanel();
		    vStatusPanel.setLayout(new BoxLayout(vStatusPanel, BoxLayout.Y_AXIS));
		    ArrayList<JLabel> addStatus = getStatusLabels(me.getCurrent());
		    for (JLabel l : addStatus) {
		    	vStatusPanel.add(l);
		    }
		    teamMemberPanel.add(vStatusPanel, gbc);
		    gbc.gridx++;
		    
		    JPanel fStatusPanel = new JPanel();
		    fStatusPanel.setLayout(new BoxLayout(fStatusPanel, BoxLayout.Y_AXIS));
		    ArrayList<JLabel> foeAddStatus = getStatusLabels(foe);
		    for (JLabel l : foeAddStatus) {
		    	fStatusPanel.add(l);
		    }
		    teamMemberPanel.add(fStatusPanel, gbc);
		    gbc.gridx = 0;
		    gbc.gridy++;
		    
		    JPanel effectsPanel = new JPanel();
		    effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
		    
		    JLabel fieldEffectsLabel = new JLabel("Field Effects:");
		    fieldEffectsLabel.setFont(new Font(fieldEffectsLabel.getFont().getName(), Font.BOLD, 18));
		    JPanel fieldLabel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		    fieldLabel.add(fieldEffectsLabel);
		    effectsPanel.add(fieldLabel);
		    
		    if (field.weather != null) {
		    	JPanel weatherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		    	JLabel weatherLabel = new JLabel(field.weather.toString());
		    	weatherLabel.setForeground(field.weather.getColor());
		    	JLabel turnsLabel = new JLabel(" " + field.weatherTurns + "/" + field.weather.effect.turns);
		    	weatherLabel.setFont(new Font(weatherLabel.getFont().getFontName(), Font.BOLD, 16));
		    	turnsLabel.setFont(new Font(turnsLabel.getFont().getFontName(), Font.BOLD, 16));
		    	weatherPanel.add(weatherLabel);
		    	weatherPanel.add(turnsLabel);
		    	effectsPanel.add(weatherPanel);
		    }
		    
		    if (field.terrain != null) {
		    	JPanel terrainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		    	JLabel terrainLabel = new JLabel(field.terrain.toString() + " Terrain");
		    	terrainLabel.setForeground(field.terrain.getColor());
		    	JLabel turnsLabel = new JLabel(" " + field.terrainTurns + "/" + field.terrain.effect.turns);
		    	terrainLabel.setFont(new Font(terrainLabel.getFont().getFontName(), Font.BOLD, 16));
		    	turnsLabel.setFont(new Font(turnsLabel.getFont().getFontName(), Font.BOLD, 16));
		    	terrainPanel.add(terrainLabel);
		    	terrainPanel.add(turnsLabel);
		    	effectsPanel.add(terrainPanel);
		    }
		    
		    for (FieldEffect fe : field.fieldEffects) {
		    	JPanel effectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		    	JLabel effectLabel = new JLabel(fe.toString());
		    	effectLabel.setForeground(fe.getColor());
		    	JLabel turnsLabel = new JLabel(" " + fe.turns + "/" + fe.effect.turns);
		    	effectLabel.setFont(new Font(effectLabel.getFont().getFontName(), Font.BOLD, 16));
		    	turnsLabel.setFont(new Font(turnsLabel.getFont().getFontName(), Font.BOLD, 16));
		    	effectPanel.add(effectLabel);
		    	effectPanel.add(turnsLabel);
		    	effectsPanel.add(effectPanel);
		    }
		    
		    JPanel effectSidePanel = new JPanel();
		    effectSidePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		    
	        JPanel playerSidePanel = new JPanel();
	        playerSidePanel.setLayout(new BoxLayout(playerSidePanel, BoxLayout.Y_AXIS));
	        JPanel foeSidePanel = new JPanel();
		    foeSidePanel.setLayout(new BoxLayout(foeSidePanel, BoxLayout.Y_AXIS));
		    
		    JPanel line1 = new JPanel();
		    line1.add(new JLabel("------------------------         "));
		    JPanel line2 = new JPanel();
		    line2.add(new JLabel("         ------------------------"));
		    playerSidePanel.add(line1);
		    foeSidePanel.add(line2);
		    
		    for (FieldEffect fe : field.playerSide) {
		    	JPanel effectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    	JLabel effectLabel = new JLabel(fe.toString());
		    	effectLabel.setForeground(fe.getColor());
		    	JLabel turnsLabel = new JLabel(" " + fe.turns + "/" + fe.effect.turns);
		    	if (field.getHazards(field.playerSide).contains(fe)) {
		    		int layers = field.getLayers(field.playerSide, fe.effect);
		    		int maxLayers = 1;
		    		if (fe.effect == Effect.SPIKES) maxLayers = 3;
		    		if (fe.effect == Effect.TOXIC_SPIKES) maxLayers = 2;
		    		turnsLabel.setText("( " + layers + " / " + maxLayers + " )");
		    	}
		    	effectLabel.setFont(new Font(effectLabel.getFont().getFontName(), Font.BOLD, 12));
		    	turnsLabel.setFont(new Font(turnsLabel.getFont().getFontName(), Font.BOLD, 12));
		    	effectPanel.add(effectLabel);
		    	effectPanel.add(turnsLabel);
		    	playerSidePanel.add(effectPanel);
		    }
		    
		    
		    
		    for (FieldEffect fe : field.foeSide) {
		    	JPanel effectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    	JLabel effectLabel = new JLabel("         " + fe.toString());
		    	effectLabel.setForeground(fe.getColor());
		    	JLabel turnsLabel = new JLabel(" " + fe.turns + "/" + fe.effect.turns);
		    	if (field.getHazards(field.foeSide).contains(fe)) {
		    		int layers = field.getLayers(field.foeSide, fe.effect);
		    		int maxLayers = 1;
		    		if (fe.effect == Effect.SPIKES) maxLayers = 3;
		    		if (fe.effect == Effect.TOXIC_SPIKES) maxLayers = 2;
		    		turnsLabel.setText("( " + layers + " / " + maxLayers + " )");
		    	}
		    	effectLabel.setFont(new Font(effectLabel.getFont().getFontName(), Font.BOLD, 12));
		    	turnsLabel.setFont(new Font(turnsLabel.getFont().getFontName(), Font.BOLD, 12));
		    	effectPanel.add(effectLabel);
		    	effectPanel.add(turnsLabel);
		    	foeSidePanel.add(effectPanel);
		    }
		    
		    effectSidePanel.add(playerSidePanel);
		    effectSidePanel.add(foeSidePanel);
		    
		    JPanel line3 = new JPanel();
		    line3.add(new JLabel("------------------------         "));
		    JPanel line4 = new JPanel();
		    line4.add(new JLabel("         ------------------------"));
		    playerSidePanel.add(line3);
		    foeSidePanel.add(line4);
		    
		    JPanel info = new JPanel();
		    info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		    info.add(teamMemberPanel);
		    info.add(effectsPanel);
		    info.add(effectSidePanel);
		    
            JOptionPane.showMessageDialog(null, info, "Battle Info", JOptionPane.PLAIN_MESSAGE);
		    }
		});
		
		calcButton.addActionListener(e -> {
			me.bag.bag[200].useCalc(me, null);
		});
		
		/*
		 * Sets party buttons and adds a switching function actionListener to each button
		 */
		party = new JGradientButton[5];
		partyHP = new JProgressBar[5];
		partySprites = new JLabel[5];
		for (int i = 0; i < 5; i++) {
			party[i] = new JGradientButton("");
			partyHP[i] = new JProgressBar(0, 100);
			partySprites[i] = new JLabel("");
			final int index = i + 1;
			
			party[i].addActionListener(e -> {
				if (me.getCurrent().isTrapped()) {
	        		JOptionPane.showMessageDialog(null, "You are trapped and cannot switch!");
	                return;
				}
				boolean swapping = me.getCurrent().vStatuses.contains(Status.SWITCHING);
				
				Move move = foe.trainerOwned() ? foe.bestMove(me.current, false, foeTrainer) : foe.randomMove();
				Pokemon oldCurrent = me.getCurrent().clone();
				
				boolean foeCanMove = true;
    			if (!me.getCurrent().isFainted() && foeTrainer != null && foeTrainer.getCurrent().vStatuses.contains(Status.SWAP)) { // AI wants to swap out
    				Pokemon faster = me.getCurrent().getFaster(foe, 0, 0);
    				if (me.getCurrent() == faster) {
    					me.swap(me.team[index], index);
    					me.getCurrent().swapIn(foe, me, true);
    					
    					foe = foeTrainer.getSwap(oldCurrent, move);
        				if (foe != foeTrainer.getCurrent()) {
        					foeTrainer.swap(foeTrainer.getCurrent(), foe);
        					foe.swapIn(me.getCurrent(), me, true);
        					foeCanMove = false;
        				}
    				} else {
    					foe = foeTrainer.getSwap(oldCurrent, move);
        				if (foe != foeTrainer.getCurrent()) {
        					foeTrainer.swap(foeTrainer.getCurrent(), foe);
        					foe.swapIn(me.getCurrent(), me, true);
        					foeCanMove = false;
        				}
        				
        				me.swap(me.team[index], index);
        				me.getCurrent().swapIn(foe, me, true);
    				}
    				
    			} else {
    				me.swap(me.team[index], index);
    				me.getCurrent().swapIn(foe, me, true);
    			}
				updateField(field);
				foe.vStatuses.remove(Status.TRAPPED);
				foe.vStatuses.remove(Status.SPUN);
				healthBar.setMaximum(me.getCurrent().getStat(0));
				healthBar.setValue(me.getCurrent().currentHP);
				if (healthBar.getPercentComplete() > 0.5) {
					healthBar.setForeground(new Color(0, 255, 0));
	            } else if (healthBar.getPercentComplete() <= 0.5 && healthBar.getPercentComplete() > 0.25) {
	            	healthBar.setForeground(new Color(255, 255, 0));
	            } else {
	            	healthBar.setForeground(new Color(255, 0, 0));
	            }
				if (!me.team[index].isFainted() && !swapping) {
	        		if (foeTrainer != null) {
	        			if (foeCanMove) {
	        				foe.move(me.getCurrent(), move, me, foeTrainer.getTeam(), foeTrainer, false);
	        				foe = foeTrainer.getCurrent();
	        				if (foe.vStatuses.contains(Status.SWITCHING)) {
		        	        	foeTrainer.swapRandom(me.getCurrent(), me, false, foe.lastMoveUsed == Move.BATON_PASS);
		        	        	foe = foeTrainer.current;
		        	        }
	        			}
	        		} else {
	        			foe.move(me.getCurrent(), move, me, null, null, false);
		        	}
					foe.endOfTurn(me.getCurrent(), me);
					me.getCurrent().endOfTurn(foe, me);
					field.endOfTurn();
				}
				updateCurrent(pl);
				updateBars(true);
				updateField(field);
				updateFoe();
				displayParty();
				updateStatus();
				if (foe.isFainted()) {
					if (foeTrainer != null && foeTrainer.getTeam() != null) {
						if (foeTrainer.hasNext()) {
							foe = foeTrainer.next(me.getCurrent());
							console.writeln("\n" + foeTrainer.toString() + " sends out " + foeTrainer.getCurrent().name + "!");
							foe.swapIn(me.getCurrent(), me, true);
							updateField(field);
							updateFoe();
							updateCurrent(pl);
							updateBars(false);
							me.clearBattled();
							me.getCurrent().battled = true;
							
						} else {
							if (me.copyBattle) copyToClipboard();
							// Show the prompt with the specified text
							me.money += foeTrainer.getMoney();
				            JOptionPane.showMessageDialog(null, foeTrainer.toString() + " was defeated!\nWon $" + foeTrainer.getMoney() + "!");
				            if (foeTrainer.getMoney() == 500 && me.badges < 8) {
				            	me.badges++;
				            	for (Pokemon p : me.team) {
				            		if (p != null) p.awardHappiness(15, true);
				            	}
				            }
				            me.updateHappinessCaps();
				            if (foeTrainer.item != null) {
				            	me.bag.add(foeTrainer.item);
				            }
				            if (foeTrainer.flagIndex != 0) {
				            	me.flags[foeTrainer.flagIndex] = true;
				            }

				            // Close the current Battle JFrame
				            dispose();
						}
					} else {
						if (me.copyBattle) copyToClipboard();
						JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
						dispose();
					}
				}
				console.writeln();
			    console.writeln("------------------------------");
			    if (me.wiped()) {
					wipe(pl, gp);
				}
	        });
			party[i].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        if (SwingUtilities.isRightMouseButton(e)) {
			        	JPanel teamMemberPanel = me.team[index].showSummary(me, false, null, null);
			            JOptionPane.showMessageDialog(null, teamMemberPanel, "Party member details", JOptionPane.PLAIN_MESSAGE);
			        }
			    }
			});
		}
	}
	
	private ArrayList<JLabel> getStatusLabels(Pokemon p) {
		ArrayList<JLabel> result = new ArrayList<>();
		for (Status s : p.vStatuses) {
	    	JLabel statusLabel = new JLabel(s.toString());
	    	statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 13));
	    	result.add(statusLabel);
	    }
		if (p.magCount > 0) {
			JLabel statusLabel = new JLabel("Magnet Rise");
			statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 13));
			result.add(statusLabel);
		}
		if (p.perishCount > 0) {
			JLabel statusLabel = new JLabel("Perish in " + p.perishCount);
			statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 13));
			result.add(statusLabel);
		}
		return result;
	}

	/*
	 * @DEBUG: Used for efficiently creating the JTextFields for id and level inputs
	 */
//	private JTextField createJTextField(int i, int j, int k, int l, int m) {
//		JTextField newT = new JTextField();
//		newT.setColumns(i);
//		newT.setBounds(j, k, l, m);
//		newT.addFocusListener(new FocusAdapter() {
//		    @Override // implementation
//		    public void focusGained(FocusEvent e) {
//		        newT.selectAll();
//		    }
//		});
//		newT.addActionListener(new ActionListener() {
//		    public void actionPerformed(ActionEvent e) {
//		        fightMon();
//		    }
//		});
//		playerPanel.add(newT);
//		return newT;
//	}


	public Pokemon encounterPokemon(int area, int x, int y, boolean random, String type) {
	    // Create an ArrayList of PokemonEncounter objects for the route
		//String selectedEncounterType = encounterType.getSelection().getActionCommand();
		//String selectedTime = time.getSelection().getActionCommand();
	    ArrayList<Encounter> encounters = Encounter.getEncounters(area, x, y, type, "D", random);

	    // Calculate the total encounter chance for the route
	    double totalChance = 0.0;
	    for (Encounter encounter : encounters) {
	        totalChance += encounter.getEncounterChance();
	    }

	    // Randomly select an encounter based on the Pokemon's encounter chance
	    double rand = Math.random() * totalChance;
	    for (Encounter encounter : encounters) {
	        rand -= encounter.getEncounterChance();
	        if (rand < 0) {
	            // Randomly generate a level within the level range
	            int level = (int) (Math.random() * (encounter.getMaxLevel() - encounter.getMinLevel() + 1) + encounter.getMinLevel());
	            return new Pokemon(encounter.getId(), level, false, false);
	        }
	    }

	    // If no encounter was selected, return null
	    JOptionPane.showMessageDialog(null, "No encounters available for this combination.");
	    return foe;
	}

	private JButton createJButton(String string, Font font, int i, int j, int k, int l) {
		JButton newB = new JButton(string);
		newB.setFont(font);
		newB.setBounds(i, j, k, l);
		playerPanel.add(newB);
		return newB;
	}
	
	private Font getScaledFontSize(JLabel label) {
	    String text = label.getText();
	    Font originalFont = label.getFont();
	    Font newFont = originalFont;

	    int textWidth = label.getFontMetrics(originalFont).stringWidth(text);
	    int maxWidth = label.getWidth();

	    while (textWidth > maxWidth && newFont.getSize() > 1) {
	        newFont = newFont.deriveFont((float) newFont.getSize() - 1);
	        textWidth = label.getFontMetrics(newFont).stringWidth(text);
	    }

	    return newFont;
	}

	private int getScaledFontSize(JButton button) {
	    String text = button.getText();
	    Font originalFont = button.getFont();
	    Font newFont = originalFont;

	    int textWidth = button.getFontMetrics(originalFont).stringWidth(text);
	    int maxWidth = button.getWidth() - 34;

	    while (textWidth > maxWidth && newFont.getSize() > 1) {
	        newFont = newFont.deriveFont((float) newFont.getSize() - 1);
	        textWidth = button.getFontMetrics(newFont).stringWidth(text);
	    }

	    return newFont.getSize();
	}

	private void updateCurrent(PlayerCharacter pl) {
		if (currentText != null) playerPanel.remove(currentText);
		if (userSprite != null) playerPanel.remove(userSprite);
		
		currentText = new JLabel("ERROR");
		currentText.setText(me.getCurrent().nickname + "  lv " + me.getCurrent().getLevel()); 
		currentText.setHorizontalAlignment(SwingConstants.LEFT);
		currentText.setBounds(249, 179, 140, 20);
		currentText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		currentText.setFont(getScaledFontSize(currentText));
		
		userSprite.setIcon(getSprite(me.getCurrent()));
		MouseListener[] listeners = userSprite.getMouseListeners();
		for (MouseListener listener : listeners) {
			userSprite.removeMouseListener(listener);
		}
		
		userSprite.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				JPanel teamMemberPanel = me.getCurrent().showSummary(me, false, null, null);
	            JOptionPane.showMessageDialog(null, teamMemberPanel, "Party member details", JOptionPane.PLAIN_MESSAGE);
            }
		});
		
		setMoveButtons();
		
		setBallCounts(pl);
		
		playerPanel.add(currentText);
		playerPanel.add(userSprite);
		playerPanel.repaint();
		
	}

	private ImageIcon getSprite(Pokemon p) {
		ImageIcon originalSprite = new ImageIcon(p.getSprite());
		Image originalImage = originalSprite.getImage();
		
		int scaledWidth = originalImage.getWidth(null) * 2;
		int scaledHeight = originalImage.getHeight(null) * 2;
		
		Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		
		if (p.playerOwned) scaledImage = flipHorizontal(scaledImage);
		
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		
		return scaledIcon;
	}

	private Image flipHorizontal(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = bufferedImage.createGraphics();

	    // Flip the image horizontally by drawing it with negative width
	    graphics.drawImage(image, image.getWidth(null), 0, -image.getWidth(null), image.getHeight(null), null);
	    graphics.dispose();

	    return bufferedImage;
	}

	private void setMoveButtons() {
	    Moveslot[] moveset = me.getCurrent().moveset;
	    for (int i = 0; i < moveButtons.length; i++) {
	        if (moveset[i] != null) {
	        	moveButtons[i].setText(moveset[i].move.toString());
	            moveButtons[i].setFont(new Font("Tahoma", Font.BOLD, 11));
	            moveButtons[i].setFont(new Font("Tahoma", Font.PLAIN, getScaledFontSize(moveButtons[i])));
	            moveButtons[i].setForeground(moveset[i].getPPColor());
	            PType moveType = me.getCurrent().moveset[i].move == Move.HIDDEN_POWER ? me.getCurrent().determineHPType() : me.getCurrent().moveset[i].move.mtype;
	            moveButtons[i].setToolTipText(moveType.effectiveness(foe));
	            String text = moveButtons[i].getText();
	            String pp = me.getCurrent().moveset[i].currentPP + " / " + me.getCurrent().moveset[i].maxPP;
	            moveButtons[i].setText("<html><center><b>" + text + "</b><br>" + pp + "</center></html>");
	            moveButtons[i].setBackground(moveset[i].move.mtype.getColor());
	            moveButtons[i].setVisible(true);
	        } else {
	        	moveButtons[i].setText("No Move");
	        	moveButtons[i].setVisible(false);
	        }
	    }
	    playerPanel.repaint();
	}
	
	private void updateField(Field field) {
		if (field.weather != null) {
			weather.setIcon(new ImageIcon(getIcon(field.weather)));
		} else {
			weather.setIcon(null);
		}
		if (field.terrain != null) {
			terrain.setIcon(new ImageIcon(getIcon(field.terrain)));
		} else {
			terrain.setIcon(null);
		}
		repaint();
		
	}


	private Image getIcon(FieldEffect effect) {
		String path = effect.toLowerCaseString();
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/field/" + path + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
		
	}

	private void updateFoe() {
		// Foe text
		if (foeText != null) playerPanel.remove(foeText);
		if (foeHealthBar != null) playerPanel.remove(foeHealthBar);
		foeText = new JLabel("ERROR");
		foeText.setText(foe.nickname + "  lv " + foe.getLevel());
		foeText.setBounds(589, 37, 184, 20);
		foeText.setHorizontalAlignment(SwingConstants.LEFT);
		foeText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		foeText.setFont(getScaledFontSize(foeText));
		
		foeSprite.setIcon(getSprite(foe));
		MouseListener[] listeners = foeSprite.getMouseListeners();
		for (MouseListener listener : listeners) {
			foeSprite.removeMouseListener(listener);
		}
		
		foeSprite.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				JGradientButton type1 = new JGradientButton("");
				JGradientButton type2 = new JGradientButton("");
				
				type1.setText(foe.type1.toString());
				type1.setBackground(foe.type1.getColor());
				
				if (foe.type2 != null) {
					type2.setText(foe.type2.toString());
					type2.setBackground(foe.type2.getColor());
				} else {
					type2.setText("None");
					type2.setBackground(null);
				}

		        JPanel buttonPanel = new JPanel();
		        buttonPanel.add(type1);
		        buttonPanel.add(type2);

		        JOptionPane.showMessageDialog(null, buttonPanel, "Foe's Typing:", JOptionPane.PLAIN_MESSAGE);
            }
		});
		
		// Foe healthbar
		foeHealthBar = new JProgressBar(0, 100);
		foeHealthBar.setMaximum(foe.getStat(0));
		foeHealthBar.setBackground(UIManager.getColor("Button.darkShadow"));
		foeHealthBar.setValue(foe.getCurrentHP());
		if (foeHealthBar.getPercentComplete() > 0.5) {
			foeHealthBar.setForeground(new Color(0, 255, 0));
		} else if (foeHealthBar.getPercentComplete() <= 0.5 && foeHealthBar.getPercentComplete() > 0.25) {
			foeHealthBar.setForeground(new Color(255, 255, 0));
		} else {
			foeHealthBar.setForeground(new Color(255, 0, 0));
		}
		foeHealthBar.setBounds(563, 59, 146, 14);
		
		playerPanel.add(foeText);
		playerPanel.add(foeHealthBar);
		
		if (foePartyPanel != null) playerPanel.remove(foePartyPanel);
		foePartyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		foePartyPanel.setBounds(545, 0, 200, 32);

		int yellowIndex = 6;
		if (foeTrainer != null) yellowIndex = foeTrainer.getNumFainted();
		for (int i = 0; i < 6; i++) {
		    JLabel currentIcon = new JLabel(new ImageIcon(getIcon("/icons/ball.png")));

		    if (foeTrainer != null && i < foeTrainer.getTeam().length) {
	            if (i < yellowIndex) {
	                currentIcon.setIcon(new ImageIcon(getIcon("/icons/ballfainted.png")));
	            }
	            if (i == yellowIndex) {
	            	currentIcon.setIcon(new ImageIcon(getIcon("/icons/ballcurrent.png")));
	            }

	            foePartyPanel.add(currentIcon);
		    } else {
		        currentIcon.setVisible(false);
		    }
		}

		foePartyPanel.setVisible(true);
		playerPanel.add(foePartyPanel);

		
		updateStatus();
		playerPanel.repaint();
		
	}

	private void animateBar(JProgressBar bar, int value, int currentHP) {
	    int diff = value - currentHP;
	    int current = value;
	    
	    // Determine the step size for the animation
	    int step = diff > 0 ? 1 : -1;
	    // Create a Timer to animate the progress bar
	    Timer timer = new Timer(5, new ActionListener() {
	        int count = 0;
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // Update the progress bar value and repaint it
	            bar.setValue(current - step * count++);
	            bar.repaint();

	            if (bar.getPercentComplete() > 0.5) {
	                bar.setForeground(new Color(0, 255, 0));
	            } else if (bar.getPercentComplete() <= 0.5 && bar.getPercentComplete() > 0.25) {
	                bar.setForeground(new Color(255, 255, 0));
	            } else {
	                bar.setForeground(new Color(255, 0, 0));
	            }
	            // Stop the Timer when the animation is finished
	            if (count > Math.abs(diff)) {
	                ((Timer) e.getSource()).stop();

	            }
	        }
	    });
	    // Start the Timer
	    timer.start();
	}




	private void displayParty() {
		for (int i = 0; i < 5; i++) {
			party[i].setText("");
			if (me.team[i + 1] != null && !me.team[i + 1].isFainted()) {
				Pokemon p = me.team[i + 1];
				// Party buttons
				String top = p.nickname;
				String bottom = "lv. " + p.getLevel();
				party[i].setHorizontalAlignment(SwingConstants.CENTER);
				party[i].setBounds(60, 21 + (i % 5) * 54, 114, 40);
				party[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
				party[i].setText("<html><center>" + top + "<br>" + bottom + "</center></html>");
				party[i].setBackground(p.type1.getColor(), p.type2 == null ? null : p.type2.getColor());
				playerPanel.add(party[i]);
				party[i].setVisible(true);
				
				// Party HP bars
				partyHP[i].setMaximum(p.getStat(0));
				partyHP[i].setValue(p.currentHP);
				if (partyHP[i].getPercentComplete() > 0.5) {
					partyHP[i].setForeground(new Color(0, 255, 0));
				} else if (partyHP[i].getPercentComplete() <= 0.5 && partyHP[i].getPercentComplete() > 0.25) {
					partyHP[i].setForeground(new Color(255, 255, 0));
				} else {
					partyHP[i].setForeground(new Color(255, 0, 0));
				}
				partyHP[i].setBounds(60, 64 + (i % 5) * 54, 114, 7);
				playerPanel.add(partyHP[i]);
				partyHP[i].setVisible(true);
				
				// Party sprites
				partySprites[i].setBounds(0, 11 + (i % 5) * 54, 60, 60);
				
				partySprites[i].setIcon(getMiniSprite(p));
				playerPanel.add(partySprites[i]);
				partySprites[i].setVisible(true);
			} else {
				party[i].setVisible(false);
				partyHP[i].setVisible(false);
				partySprites[i].setVisible(false);
			}
		}
		
	}

	private Icon getMiniSprite(Pokemon p) {
		ImageIcon originalSprite = new ImageIcon(p.getMiniSprite());
		Image image = originalSprite.getImage();
		
		image = flipHorizontal(image);
		
		ImageIcon imageIcon = new ImageIcon(image);
		
		return imageIcon;
	}

	public void turn(Pokemon p1, Pokemon p2, Move m1, Move m2, PlayerCharacter pl, GamePanel gp) {
		if (p1.isFainted() || p2.isFainted()) return;

		int m1P, m2P;
		m1P = m1.priority;
		m2P = m2.priority;
		if (p1.ability == Ability.PRANKSTER && m1.cat == 2) ++m1P;
		if (p2.ability == Ability.PRANKSTER && m2.cat == 2) ++m2P;
		
		if (p1.ability == Ability.STEALTHY_PREDATOR && p1.impressive) ++m1P;
		if (p2.ability == Ability.STEALTHY_PREDATOR && p2.impressive) ++m2P;
		
		if (p1.item == Item.QUICK_CLAW) {
			Random rand = new Random();
			int num = rand.nextInt(10);
			if (num < 2) {
				m1P++;
				console.writeln(p1.nickname + "'s Quick Claw let it act first!");
			}
		}
		if (p2.item == Item.QUICK_CLAW) {
			Random rand = new Random();
			int num = rand.nextInt(10);
			if (num < 2) {
				m2P++;
				console.writeln(p2.nickname + "'s Quick Claw let it act first!");
			}
		}
		if (p1.item == Item.CUSTAP_BERRY && p1.currentHP <= p1.getStat(0) * 1.0 / 4) {
			m1P++;
			console.writeln(p1.nickname + " ate its Custap Berry and could act first!");
			p1.consumeItem();
		}
		if (p2.item == Item.CUSTAP_BERRY && p2.currentHP <= p2.getStat(0) * 1.0 / 4) {
			m1P++;
			console.writeln(p2.nickname + " ate its Custap Berry and could act first!");
			p2.consumeItem();
		}
		
		Pokemon faster = p1.getFaster(p2, m1P, m2P);
		
		Pokemon slower = faster == p1 ? p2 : p1;
		
		boolean foeCanMove = true;
		
		if (faster == p1) { // player Pokemon is faster
			if (slower.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				slower = foeTrainer.getSwap(me.getCurrent(), m2);
				if (slower != foeTrainer.getCurrent()) {
					foeTrainer.swap(foeTrainer.getCurrent(), slower);
					slower.swapIn(me.getCurrent(), me, true);
					me.getCurrent().vStatuses.remove(Status.TRAPPED);
					me.getCurrent().vStatuses.remove(Status.SPUN);
					foeCanMove = false;
				}
			}
			
			Pokemon[] team = me.getTeam();
			Pokemon[] enemyTeam = foeTrainer == null ? null : foeTrainer.getTeam();
			if (m1 == Move.SUCKER_PUNCH && m2.cat == 2) m1 = Move.FAILED_SUCKER;
			faster.move(slower, m1, me, team, foeTrainer, true);
			// Check for swap (player)
			if (faster.vStatuses.contains(Status.SWITCHING)) faster = getSwap(pl, faster.lastMoveUsed == Move.BATON_PASS);
			
			if (m2 == Move.SUCKER_PUNCH && m1.cat == 2) m2 = Move.FAILED_SUCKER;
	        if (!(foeTrainer != null && slower != foeTrainer.getCurrent()) && foeCanMove) {
	        	slower.move(faster, m2, me, enemyTeam, foeTrainer, false);
	        	if (foeTrainer != null) slower = foeTrainer.getCurrent();
	        }
	        
	        // Check for swap (AI)
	        if (foeCanMove && slower.vStatuses.contains(Status.SWITCHING)) {
	        	foeTrainer.swapRandom(faster, me, false, slower.lastMoveUsed == Move.BATON_PASS);
	        	slower = foeTrainer.current;
	        }
		} else { // enemy Pokemon is faster
			if (faster.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				faster = foeTrainer.getSwap(me.getCurrent(), m1);
				if (faster != foeTrainer.getCurrent()) {
					foeTrainer.swap(foeTrainer.getCurrent(), faster);
					faster.swapIn(me.getCurrent(), me, true);
					me.getCurrent().vStatuses.remove(Status.TRAPPED);
					me.getCurrent().vStatuses.remove(Status.SPUN);
					foeCanMove = false;
				}
			}
			Pokemon[] enemyTeam = me.getTeam();
			Pokemon[] team = foeTrainer == null ? null : foeTrainer.getTeam();
			if (m2 == Move.SUCKER_PUNCH && m1.cat == 2) m2 = Move.FAILED_SUCKER;
			if (foeCanMove) {
				faster.move(slower, m2, me, team, foeTrainer, true);
				if (foeTrainer != null) faster = foeTrainer.getCurrent();
			}
			// Check for swap (AI)
	        if (foeCanMove && faster.vStatuses.contains(Status.SWITCHING)) {
	        	foeTrainer.swapRandom(slower, me, false, faster.lastMoveUsed == Move.BATON_PASS);
	        	faster = foeTrainer.current;
	        }
			
			if (m1 == Move.SUCKER_PUNCH && m2.cat == 2) m1 = Move.FAILED_SUCKER;
	        if (slower == me.getCurrent()) slower.move(faster, m1, me, enemyTeam, foeTrainer, false);
	        // Check for swap
	        if (slower.vStatuses.contains(Status.SWITCHING)) slower = getSwap(pl, slower.lastMoveUsed == Move.BATON_PASS);
		}
		if (foeTrainer != null) {
			foe = foeTrainer.getCurrent();
			showFoe();
		}
        if (hasAlive()) faster.endOfTurn(slower, me);
        if (hasAlive()) slower.endOfTurn(faster, me);
        if (hasAlive()) field.endOfTurn();
		updateBars(true);
		updateCurrent(pl);
		updateStatus();
		displayParty();
		if (foe.isFainted()) {
			if (foeTrainer != null && foeTrainer.getTeam() != null) {
				if (foeTrainer.hasNext()) {
					foe = foeTrainer.next(me.getCurrent());
					console.writeln("\n" + foeTrainer.toString() + " sends out " + foeTrainer.getCurrent().name + "!");
					foe.swapIn(me.getCurrent(), me, true);
					updateField(field);
					updateFoe();
					updateCurrent(pl);
					updateBars(false);
					me.clearBattled();
					me.getCurrent().battled = true;
					
				} else {
					// Show the prompt with the specified text
					me.money += foeTrainer.getMoney();
					String message = foeTrainer.toString() + " was defeated!\nWon $" + foeTrainer.getMoney() + "!";
		            if (foeTrainer.getMoney() == 500 && me.badges < 8) {
		            	me.badges++;
		            	for (Pokemon p : me.team) {
		            		if (p != null) p.awardHappiness(15, true);
		            	}
		            	me.updateHappinessCaps();
		            }
		            if (foeTrainer.item != null) {
		            	me.bag.add(foeTrainer.item);
		            	message += "\nYou were given " + foeTrainer.item.toString() + "!";
		            }
		            if (foeTrainer.flagIndex != 0) {
		            	me.flags[foeTrainer.flagIndex] = true;
		            }
		            
		            if (me.copyBattle) copyToClipboard();
		            
		            JOptionPane.showMessageDialog(null, message);

		            // Close the current Battle JFrame
		            dispose();
				}
			} else {
				if (me.copyBattle) copyToClipboard();
				JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
				dispose();
			}
		}
		console.writeln();
	    console.writeln("------------------------------");
	    updateField(field);
	    if (me.wiped()) {
			wipe(pl, gp);
		}
	}

	private void copyToClipboard() {
		String text = console.getText();

	    // Create a StringSelection object to hold the text
	    StringSelection stringSelection = new StringSelection(text);

	    // Get the system clipboard
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	    // Set the contents of the clipboard to the StringSelection
	    clipboard.setContents(stringSelection, null);
		
	}

	private boolean hasAlive() {
		if (!foe.isFainted()) return true;
		
		if (foeTrainer != null && foeTrainer.getTeam() != null) {
			if (foeTrainer.hasNext()) return true;
		}

		return false;
	}

	private void showFoe() {
		foeSprite.setIcon(getSprite(foe));
		MouseListener[] listeners = foeSprite.getMouseListeners();
		for (MouseListener listener : listeners) {
			foeSprite.removeMouseListener(listener);
		}
		
		foeSprite.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				JGradientButton type1 = new JGradientButton("");
				JGradientButton type2 = new JGradientButton("");
				
				type1.setText(foe.type1.toString());
				type1.setBackground(foe.type1.getColor());
				
				if (foe.type2 != null) {
					type2.setText(foe.type2.toString());
					type2.setBackground(foe.type2.getColor());
				} else {
					type2.setText("None");
					type2.setBackground(null);
				}

		        JPanel buttonPanel = new JPanel();
		        buttonPanel.add(type1);
		        buttonPanel.add(type2);

		        JOptionPane.showMessageDialog(null, buttonPanel, "Foe's Typing:", JOptionPane.PLAIN_MESSAGE);
            }
		});
		
		foeText.setText(foe.nickname + "  lv " + foe.getLevel());
		foeText.setBounds(589, 37, 184, 20);
		foeText.setHorizontalAlignment(SwingConstants.LEFT);
		foeText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		foeText.setFont(getScaledFontSize(foeText));
		
	}

	private Pokemon getSwap(PlayerCharacter pl, boolean baton) {
		if (me.hasValidMembers()) {
			JPanel partyMasterPanel = new JPanel();
			partyMasterPanel.setLayout(new GridLayout(3, 2, 5, 5));
		    partyMasterPanel.setPreferredSize(new Dimension(350, 350));

		    for (int j = 0; j < 6; j++) {
		        if (me.team[j] != null && !me.team[j].isFainted() && !(me.team[j] == me.current)) {
		        	PartyPanel partyPanel = new PartyPanel(me.team[j], true);
		            final int index = j;

		            if (me.team[j] != null) {
		            	partyPanel.addMouseListener(new MouseAdapter() {
			            	public void mouseClicked(MouseEvent evt) {
			            		if (SwingUtilities.isRightMouseButton(evt)) {
			            			JOptionPane.showMessageDialog(null, me.team[index].showSummary(me, false, null, null), "Party member details", JOptionPane.PLAIN_MESSAGE);
			            		} else {
			            			if (baton) me.team[index].statStages = me.getCurrent().statStages;
					                me.swap(me.team[index], index);
					                me.getCurrent().swapIn(foe, me, true);
					                updateField(field);
					                foe.vStatuses.remove(Status.TRAPPED);
					                foe.vStatuses.remove(Status.SPUN);
					                updateBars(true);
					                SwingUtilities.getWindowAncestor(partyMasterPanel).dispose();
			            		}
			            	}
			            });
		            }
		            partyMasterPanel.add(partyPanel);
		        }
		    }

		    JPanel wrapperPanel = new JPanel(new BorderLayout());
		    wrapperPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add the desired blank border
		    wrapperPanel.add(partyMasterPanel, BorderLayout.NORTH);

		    JDialog dialog = new JDialog((Frame) null, "Choose a party member to switch out to:", true);
		    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		    dialog.setResizable(false);
		    dialog.setUndecorated(true); // Remove title bar and close button
		    dialog.addWindowListener(new WindowAdapter() {
		        @Override
		        public void windowClosing(WindowEvent e) {
		            // Do nothing when the user tries to close the dialog
		        }
		    });

		    dialog.add(wrapperPanel);
		    dialog.pack();
		    dialog.setLocationRelativeTo(null);
		    dialog.setVisible(true);

		}
		return me.getCurrent();
	    
	}
	
	private void updateStatus() {
		userStatus.setText(me.getCurrent().status.getName());
		userStatus.setBackground(me.getCurrent().status.getColor());
		
		foeStatus.setText(foe.status.getName());
		foeStatus.setBackground(foe.status.getColor());
		
		userStatus.setForeground(me.getCurrent().status.getTextColor());
		foeStatus.setForeground(foe.status.getTextColor());
		
		playerPanel.repaint();
	}

	private void updateBars(boolean animate) {
		healthBar.setMaximum(me.getCurrent().getStat(0));
		if (animate) {
			animateBar(healthBar, healthBar.getValue(), me.getCurrent().getCurrentHP());
		} else {
			healthBar.setValue(me.getCurrent().getCurrentHP());
		}
		maxHPLabel.setText(me.getCurrent().getStat(0) + "");
		currentHPLabel.setText(me.getCurrent().getCurrentHP() + "");
		if (healthBar.getPercentComplete() > 0.5) {
			healthBar.setForeground(new Color(0, 255, 0));
		} else if (healthBar.getPercentComplete() <= 0.5 && healthBar.getPercentComplete() > 0.25) {
			healthBar.setForeground(new Color(255, 255, 0));
		} else {
			healthBar.setForeground(new Color(255, 0, 0));
		}
		healthBar.setBounds(223, 201, 146, 14);
		playerPanel.add(healthBar);
			
		expBar.setMaximum(me.getCurrent().expMax);
		expBar.setValue(me.getCurrent().exp);
		expBar.setBounds(223, 216, 146, 7);
		playerPanel.add(expBar);
		
		foeHealthBar.setMaximum(foe.getStat(0));
		if (animate) animateBar(foeHealthBar, foeHealthBar.getValue(), foe.getCurrentHP());
		if (foeHealthBar.getPercentComplete() > 0.5) {
			foeHealthBar.setForeground(new Color(0, 255, 0));
		} else if (foeHealthBar.getPercentComplete() <= 0.5 && foeHealthBar.getPercentComplete() > 0.25) {
			foeHealthBar.setForeground(new Color(255, 255, 0));
		} else {
			foeHealthBar.setForeground(new Color(255, 0, 0));
		}
		playerPanel.repaint();
		
	}
	
	public static final class JGradientButton extends JButton {
	    private static final long serialVersionUID = 639680055516122456L;
	    private Color backgroundColorA;
	    private Color backgroundColorB;
	    private boolean solid = false;
	    private boolean solidGradient = false;

	    public JGradientButton(String text) {
	        super(text);
	        setContentAreaFilled(false);
	        setBackground(Color.WHITE, Color.WHITE); // Default gradient colors
	    }
	    
	    public void setSolid(boolean a) {
	    	solid = a;
	    }
	    
	    public void setSolidGradient(boolean a) {
	    	solidGradient = a;
	    }

	    // Set the background gradient colors
	    public void setBackground(Color a, Color b) {
	    	if (a == null && b == null) {
	            throw new NullPointerException("Both colors cannot be null.");
	        }
	    	
	    	if (a == null) {
	    		a = b;
	    	} else if (b == null) {
	    		b = a;
	    	}
	    	
	        this.backgroundColorA = a;
	        this.backgroundColorB = b;
	        repaint(); // Repaint to apply the new colors
	    }
	    
	    @Override
	    public void setBackground(Color c) {
	    	if (c == null) c = Color.white;
	    	this.backgroundColorA = c;
	    	this.backgroundColorB = c;
	    	repaint();
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	    	if (!solidGradient) {
	    		Color middle = solid ? backgroundColorA : Color.white;
		    	
	    		Graphics2D g2 = (Graphics2D) g.create();
		        g2.setPaint(new GradientPaint(
		                new Point(0, 0),
		                backgroundColorA,
		                new Point(0, getHeight() / 3),
		                middle));
		        g2.fillRect(0, 0, getWidth(), getHeight() / 3);
		        g2.setPaint(new GradientPaint(
		                new Point(0, getHeight() / 3),
		                middle,
		                new Point(0, getHeight()),
		                backgroundColorB));
		        g2.fillRect(0, getHeight() / 3, getWidth(), getHeight());
		        g2.dispose();
		    	
		    	super.paintComponent(g);
	    	} else {
	    		Graphics2D g2 = (Graphics2D) g;
	    		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
	    		int w = getWidth();
	    		int h = getHeight();
	    		GradientPaint gp = new GradientPaint(0, 0, backgroundColorA, w, h, new Color(245, 225, 210));
	            g2.setPaint(gp);
	            g2.fillRect(0, 0, w, h);
	            
	            super.paintComponent(g);
	    	}
	    }
	}
	
//	private void fightMon() {
//		try {
//			if (Integer.parseInt(idInput.getText()) >= -144 && Integer.parseInt(idInput.getText()) <= 237) {
//				foe = new Pokemon(Integer.parseInt(idInput.getText()), Integer.parseInt(levelInput.getText()), false, false);
//				foe.item = Item.ORAN_BERRY;
//			} else {
//				foe = new Pokemon(10, 5, false, false);
//			}
//			updateFoe();
//		} catch (NumberFormatException e1) {
//			foe = new Pokemon(10, 5, false, false);
//			updateFoe();
//		}
//		me.clearBattled();
//		me.getCurrent().battled = true;
//	}

	
	public static boolean displayEvolution(Pokemon pokemon) {
		int option = JOptionPane.showOptionDialog(null,
				pokemon.nickname + " is evolving!\nDo you want to evolve your " + pokemon.nickname + "?",
	            "Evolution",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE,
	            null, null, null);
	    return option == JOptionPane.YES_OPTION;
	}
	public static int dogoEvo(Pokemon pokemon) {
		JGradientButton[] buttons = new JGradientButton[11];
	    JPanel panel = new JPanel();
	    int[] choice = new int[1];
	    choice[0] = -1;
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    JLabel label = new JLabel(pokemon.getName() + " is evolving.");
	    JLabel label2 = new JLabel("Select which evolution:");
	    panel.add(label);
	    panel.add(label2);
	    
	    for (int i = 0; i < 11; i++) {
	    	Pokemon evo = new Pokemon(-i - 113, 25, false, false);
	        buttons[i] = new JGradientButton(evo.name);
	        buttons[i].setBackground(evo.type1.getColor());
	        int index = -i - 113;
	        buttons[i].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                choice[0] = index;
	                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor((JButton) e.getSource());
	                dialog.dispose();
	            }
	        });
	        panel.add(buttons[i]);
	    }

	    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
	    JDialog dialog = optionPane.createDialog("Choose Evolution");
	    dialog.setVisible(true);
	    int result = choice[0];
	    return result == JOptionPane.CLOSED_OPTION ? JOptionPane.CLOSED_OPTION : choice[0];
	}
	
	private void wipe(PlayerCharacter p, GamePanel gp) {
		p.p.money -= 500;
		p.p.money = p.p.money < 0 ? 0 : p.p.money;
		JOptionPane.showMessageDialog(null, "You have no more Pokemon that can fight!\nYou lost $500!");
		dispose();
		p.worldX = gp.tileSize * 79;
		p.worldY = gp.tileSize * 46;
		gp.currentMap = 0;
		for (Pokemon pokemon : p.p.team) {
			if (pokemon != null) {
				pokemon.heal();
			}
		}
		if (foeTrainer != null) {
			for (Pokemon pokemon : foeTrainer.getTeam()) {
				pokemon.heal();
			}
			foeTrainer.current = foeTrainer.getTeam()[0];
		}
		
	}
	
	private void setBallComps(PlayerCharacter pl) {
		ButtonGroup ballType = new ButtonGroup();
		pokeballButton = new JRadioButton("");
		pokeballButton.setBounds(640, 370, 21, 23);
		pokeballButton.setSelected(true);
		playerPanel.add(pokeballButton);
		pokeballButton.setVisible(true);
		greatballButton = new JRadioButton("");
		greatballButton.setBounds(675, 370, 21, 23);
		playerPanel.add(greatballButton);
		greatballButton.setVisible(true);
		ultraballButton = new JRadioButton("");
		ultraballButton.setBounds(710, 370, 21, 23);
		playerPanel.add(ultraballButton);
		ultraballButton.setVisible(true);
		ballType.add(pokeballButton);
		ballType.add(greatballButton);
		ballType.add(ultraballButton);
		
		pokeballLabel = new JLabel("");
		pokeballLabel.setBounds(640, 385, 30, 30);
		pokeballLabel.setForeground(Color.red.brighter());
		playerPanel.add(pokeballLabel);
		
		greatballLabel = new JLabel("");
		greatballLabel.setBounds(675, 385, 30, 30);
		greatballLabel.setForeground(Color.blue.darker());
		playerPanel.add(greatballLabel);
		
		ultraballLabel = new JLabel("");
		ultraballLabel.setBounds(710, 385, 30, 30);
		ultraballLabel.setForeground(Color.yellow.darker());
		playerPanel.add(ultraballLabel);
		
		setBallCounts(pl);
	}
	
	private void setBallCounts(PlayerCharacter pl) {
		pokeballLabel.setText(pl.p.bag.count[1] + "");
		greatballLabel.setText(pl.p.bag.count[2] + "");
		ultraballLabel.setText(pl.p.bag.count[3] + "");
	}

	public void setBattleCloseListener(BattleCloseListener listener) {
        this.battleCloseListener = listener;
    }
	
	@Override
	public void dispose() {
		super.dispose();
		if (battleCloseListener != null) battleCloseListener.onBattleClosed(trainerIndex, staticPokemonID);
	}
	
	public interface BattleCloseListener {
	    void onBattleClosed(int trainer, int id);
	}
	
	public Image getIcon(String name) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(name));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/sprites/000.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
}
