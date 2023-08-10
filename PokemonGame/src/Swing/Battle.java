package Swing;

import javax.swing.border.EmptyBorder;

import Entity.PlayerCharacter;
import Overworld.GamePanel;
import Swing.Field.FieldEffect;

import java.awt.*;
import java.awt.event.ActionListener;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
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
	public Field field;
	public JLabel userSprite;
	public JLabel foeSprite;
	public JLabel weather;
	public JLabel terrain;
	public JRadioButton[] foeParty;
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
//	private JButton exitButton; // debug
	private int trainerIndex;
	
	private BattleCloseListener battleCloseListener;
	

	public Battle(PlayerCharacter playerCharacter, Trainer foeT, int trainerIndex, GamePanel gp, int area, int x, int y) {
		me = playerCharacter.p;
		this.trainerIndex = trainerIndex;
		int faintIndex = 0;
		while (me.getCurrent().isFainted()) {
			me.swap(me.team[++faintIndex], faintIndex);
		}
		
		setTitle("Battle");
		
		foe = new Pokemon(10, 5, false, false);
	    foe.currentHP = 0;
	    
	    field = new Field();
		
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
 			foe = encounterPokemon(area, x, y, me.random);
 		}

        // Initialize frame
        initialize(playerCharacter, gp);
		
		Pokemon fasterInit = me.getCurrent().getFaster(foe, field, 0, 0);
		Pokemon slowerInit = fasterInit == me.getCurrent() ? foe : me.getCurrent();
		fasterInit.swapIn(slowerInit, field, me);
		slowerInit.swapIn(fasterInit, field, me);
		
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
		JTextArea console = new JTextArea();
		console.setEditable(false);
		
		PrintStream printStream = new PrintStream(new CustomOutputStream(console));
		
		System.setOut(printStream);
		
		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setBounds(10, 340, 610, 190);
		playerPanel.add(scrollPane);
		
		/*
		 * Set current movebuttons
		 */
		moveButtons = new JGradientButton[4];
		int[] xPositions = {171, 270, 171, 270};
		int[] yPositions = {242, 242, 275, 275};
		for (int i = 0; i < moveButtons.length; i++) {
			final int index = i;
			moveButtons[i] = new JGradientButton("");
			moveButtons[i].setBounds(xPositions[i], yPositions[i], 89, 23);
			playerPanel.add(moveButtons[i]);
			
			moveButtons[i].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            String message = "Move: " + me.getCurrent().moveset[index].toString() + "\n";
			            message += "Type: " + me.getCurrent().moveset[index].mtype + "\n";
			            message += "BP: " + me.getCurrent().moveset[index].getbp() + "\n";
			            message += "Accuracy: " + me.getCurrent().moveset[index].accuracy + "\n";
			            message += "Category: " + me.getCurrent().moveset[index].getCategory() + "\n";
			            message += "Description: " + me.getCurrent().moveset[index].getDescription();
			            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        } else {
			        	if (foe.trainerOwned()) {
			        		turn(me.getCurrent(), foe, me.getCurrent().moveset[index], foe.bestMove(me.getCurrent(), field, false), pl, gp);
			        	} else {
			        		turn(me.getCurrent(), foe, me.getCurrent().moveset[index], foe.randomMove(), pl, gp);
			        	}
			        }
			    }
			});
		}
		
		/*
		 * Initialize status icons
		 */
		userStatus = new JLabel("");
		userStatus.setHorizontalAlignment(SwingConstants.CENTER);
		userStatus.setFont(new Font("Tahoma", Font.PLAIN, 8));
		userStatus.setText(me.getCurrent().status.getName());
		userStatus.setForeground(me.getCurrent().status.getTextColor());
		userStatus.setBackground(me.getCurrent().status.getColor());
		userStatus.setBounds(193, 179, 21, 20);
		userStatus.setVisible(true);
		userStatus.setOpaque(true);
		playerPanel.add(userStatus);
		
		foeStatus = new JLabel("");
		foeStatus.setHorizontalAlignment(SwingConstants.CENTER);
		foeStatus.setFont(new Font("Tahoma", Font.PLAIN, 8));
		foeStatus.setText(foe.status.getName());
		foeStatus.setForeground(foe.status.getTextColor());
		foeStatus.setBackground(foe.status.getColor());
		foeStatus.setBounds(543, 37, 21, 20);
		foeStatus.setVisible(true);
		foeStatus.setOpaque(true);
		playerPanel.add(foeStatus);
		
		/*
		 * Set Pokeball buttons and labels
		 */
		setBallComps(pl);
		
		/*
		 * Set current and foe
		 */
		foeParty = new JRadioButton[6];
		
		userSprite = new JLabel("");
		userSprite.setBounds(193, 10, 200, 200);
		
		foeSprite = new JLabel("");
		foeSprite.setBounds(543, 60, 200, 200);
		
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
		weather.setBounds(393, 10, 100, 200);
		weather.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		terrain = new JLabel("");
		terrain.setBounds(393, 230, 100, 75);
		terrain.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		playerPanel.add(weather);
		playerPanel.add(terrain);
		
		/*
		 * Set slash label
		 */
        slashLabel = new JLabel("/");
		slashLabel.setHorizontalAlignment(SwingConstants.CENTER);
		slashLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		slashLabel.setBounds(256, 222, 21, 14);
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
		currentHPLabel.setBounds(213, 222, 46, 14);
		playerPanel.add(currentHPLabel);
		
		maxHPLabel = new JLabel(me.getCurrent().getStat(0) + "");
		maxHPLabel.setHorizontalAlignment(SwingConstants.CENTER);
		maxHPLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		maxHPLabel.setBounds(273, 222, 46, 14);
		playerPanel.add(maxHPLabel);
		
		updateBars(false);
		
		/*
		 * Set battled status
		 */
		me.clearBattled();
		me.getCurrent().clearVolatile();
		me.getCurrent().battled = true;
		
		/*
		 * Set buttons
		 */
//		addButton = createJButton("ADD", new Font("Tahoma", Font.BOLD, 9), 645, 430, 75, 23);
		catchButton = createJButton("CATCH", new Font("Tahoma", Font.BOLD, 11), 638, 340, 89, 23);
//		healButton = createJButton("HEAL", new Font("Tahoma", Font.BOLD, 9), 645, 460, 75, 23);
		runButton = createJButton("RUN", new Font("Tahoma", Font.BOLD, 9), 645, 460, 75, 23);
		infoButton = createJButton("INFO", new Font("Tahoma", Font.BOLD, 9), 645, 490, 75, 23);
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
		    	if (foe.trainerOwned()) {
		    		JOptionPane.showMessageDialog(null, "Cannot catch trainer's Pokemon!");
                    return;
		    	}
		        Random rand = new Random();
		        double catchRate = 0;
		        double statusBonus = 1;
		        
		        //if (foe.id == 103 || foe.id == 104 || foe.id == 105 || foe.id == 91 || foe.id == 133 || foe.id == 134 ||foe.id == 135 || foe.id == 136 || foe.id == 137 ||foe.id == 138 || foe.id == 139 || foe.id == 140) statusBonus = 0.25; // if legendary
		        
		        if (pokeballButton.isSelected()) {
		        	if (me.bag.count[1] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            catchRate = 1.0 / 6.0;
		            if (foe.currentHP <= (foe.getStat(0) / 4)) {
		                catchRate = 0.5;
		            } else if (foe.currentHP <= (foe.getStat(0) / 2)) {
		                catchRate = 1.0 / 4.0;
		            }
		            if (foe.status != Status.HEALTHY) {
		                statusBonus *= 2.0;
		            }
		            System.out.println("\nUsed a Pokeball!");
		            me.bag.remove(new Item(1));
		        } else if (greatballButton.isSelected()) {
		        	if (me.bag.count[2] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            catchRate = 1.0 / 5.0;
		            if (foe.currentHP <= (foe.getStat(0) / 4)) {
		                catchRate = 0.75;
		            } else if (foe.currentHP <= (foe.getStat(0) / 2)) {
		                catchRate = 1.0 / 3.0;
		            }
		            if (foe.status != Status.HEALTHY) {
		                statusBonus *= 2.0;
		            }
		            System.out.println("\nUsed a Great Ball!");
		            me.bag.remove(new Item(2));
		        } else if (ultraballButton.isSelected()) {
		        	if (me.bag.count[3] <= 0) {
		        		JOptionPane.showMessageDialog(null, "No balls remaining!");
                        return;
		        	}
		            catchRate = 1.0 / 3.0;
		            if (foe.currentHP <= (foe.getStat(0) / 4)) {
		                catchRate = 1.0;
		            } else if (foe.currentHP <= (foe.getStat(0) / 2)) {
		                catchRate = 2.0 / 3.0;
		            }
		            if (foe.status != Status.HEALTHY) {
		                statusBonus *= 2.0;
		            }
		            System.out.println("\nUsed an Ultra Ball!");
		            me.bag.remove(new Item(3));
		        }
		        
		        double randomValue = rand.nextDouble();
		        double modifiedCatchRate = catchRate * statusBonus;
		        if (randomValue <= modifiedCatchRate) {
		            //me.catchPokemon(new Pokemon(foe.id, foe.getLevel(), true, false));
		        	foe.trainerOwned = true;
		        	me.catchPokemon(foe);
		        	updateCurrent(pl);
					updateStatus();
		        	updateBars(true);
					displayParty();
					updateFoe();
					JOptionPane.showMessageDialog(null, "You caught " + foe.name + "!");
					dispose();
		        } else {
		            System.out.println("Oh no! " + foe.name + " broke free!");
		            if (foeTrainer != null) {
		            	foe.move(me.getCurrent(),foe.randomMove(), me, field, foeTrainer.getTeam(), false);
		            } else {
		            	foe.move(me.getCurrent(),foe.randomMove(), me, field, null, false);
		            }
					foe.endOfTurn(me.getCurrent(), me, field);
					me.getCurrent().endOfTurn(foe, me, field);
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
		        System.out.println();
			    System.out.println("------------------------------");
		    }
		});
		
		/*
		 * @DEBUG: Used to full heal the party
		 */
//		healButton.addActionListener(e -> {
//			System.out.println();
//			for (Pokemon member : me.team) {
//				if (member != null) member.heal();
//			}
//			updateCurrent();
//			updateBars();
//			displayParty();
//			updateStatus();
//			playerPanel.repaint();
//			System.out.println();
//			System.out.println("------------------------------");
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
	        		Pokemon faster = me.getCurrent().getFaster(foe, field, 0, 0);
					double chance = faster == me.getCurrent() ? 1 : 0.5;
					
					if (chance >= Math.random()) {
						JOptionPane.showMessageDialog(null, "Got away safely!");
						dispose();
						return;
					}
					
					System.out.println("\nCouldn't escape!");
	        		foe.move(me.getCurrent(),foe.randomMove(), me, field, null, false);
	        	}
				foe.endOfTurn(me.getCurrent(), me, field);
				me.getCurrent().endOfTurn(foe, me, field);
				field.endOfTurn();
			}
			updateCurrent(pl);
			updateBars(true);
			displayParty();
			updateStatus();
			if (foe.isFainted()) {
				JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
				dispose();
			}
			if (me.getCurrent().isFainted()) {
				if (me.wiped()) {
					wipe(pl, gp);
				}
				return;
			}
			System.out.println();
		    System.out.println("------------------------------");
        });
		
		infoButton.addActionListener(e -> {
            JPanel teamMemberPanel = me.getCurrent().showSummary();
            JOptionPane.showMessageDialog(null, teamMemberPanel, "Party member details", JOptionPane.PLAIN_MESSAGE);
		});
		
		/*
		 * Sets party buttons and adds a switching function actionListener to each button
		 */
		party = new JGradientButton[5];
		partyHP = new JProgressBar[5];
		for (int i = 0; i < 5; i++) {
			party[i] = new JGradientButton("");
			partyHP[i] = new JProgressBar(0, 100);
			final int index = i + 1;
			
			party[i].addActionListener(e -> {
				if (me.getCurrent().vStatuses.contains(Status.SPUN) || me.getCurrent().vStatuses.contains(Status.CHARGING) || me.getCurrent().vStatuses.contains(Status.RECHARGE) || me.getCurrent().vStatuses.contains(Status.LOCKED) || me.getCurrent().vStatuses.contains(Status.TRAPPED) || me.getCurrent().vStatuses.contains(Status.SEMI_INV) || me.getCurrent().vStatuses.contains(Status.NO_SWITCH)) {
	        		JOptionPane.showMessageDialog(null, "You are trapped and cannot switch!");
	                return;
				}
				boolean swapping = me.getCurrent().vStatuses.contains(Status.SWITCHING);
				
				Move move = foe.trainerOwned() ? foe.bestMove(me.current, field, false) : foe.randomMove();
				if (move == Move.PURSUIT && !me.getCurrent().isFainted()) {
					move = Move.BOOSTED_PURSUIT;
					if (foeTrainer != null) {
	        			foe.move(me.getCurrent(), move, me, field, foeTrainer.getTeam(), false);
	        		} else {
	        			foe.move(me.getCurrent(), move, me, field, null, false);
		        	}
				}
				if (move == Move.BOOSTED_PURSUIT && me.getCurrent().isFainted()) {
					foe.endOfTurn(me.getCurrent(), me, field);
					field.endOfTurn();
					updateCurrent(pl);
					updateBars(true);
					displayParty();
					updateStatus();
					System.out.println();
				    System.out.println("------------------------------");
				    if (me.wiped()) {
						wipe(pl, gp);
					}
					return;
				}
				
				me.swap(me.team[index], index);
				me.getCurrent().swapIn(foe, field, me);
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
				if (!me.team[index].isFainted() && !swapping && move != Move.BOOSTED_PURSUIT) {
	        		if (foeTrainer != null) {
	        			foe.move(me.getCurrent(), move, me, field, foeTrainer.getTeam(), false);
	        		} else {
	        			foe.move(me.getCurrent(), move, me, field, null, false);
		        	}
					foe.endOfTurn(me.getCurrent(), me, field);
					me.getCurrent().endOfTurn(foe, me, field);
					field.endOfTurn();
				}
				if (move == Move.BOOSTED_PURSUIT) {
					foe.endOfTurn(me.getCurrent(), me, field);
					me.getCurrent().endOfTurn(foe, me, field);
					field.endOfTurn();
				}
				updateCurrent(pl);
				updateBars(true);
				displayParty();
				updateStatus();
				if (foe.isFainted()) {
					if (foeTrainer != null && foeTrainer.getTeam() != null) {
						if (foeTrainer.hasNext()) {
							foe = foeTrainer.next();
							System.out.println("\n" + foeTrainer.toString() + " sends out " + foeTrainer.getCurrent().name + "!");
							foe.swapIn(me.getCurrent(), field, me);
							updateField(field);
							updateFoe();
							updateBars(false);
							me.clearBattled();
							me.getCurrent().battled = true;
							
						} else {
							// Show the prompt with the specified text
							me.money += foeTrainer.getMoney();
				            JOptionPane.showMessageDialog(null, foeTrainer.toString() + " was defeated!\nWon $" + foeTrainer.getMoney() + "!");
				            if (foeTrainer.getMoney() == 500 && me.badges < 8) {
				            	me.badges++;
				            	for (Pokemon p : me.team) {
				            		if (p != null) p.awardHappiness(5);
				            	}
				            }
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
						JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
						dispose();
					}
				}
				System.out.println();
			    System.out.println("------------------------------");
			    if (me.wiped()) {
					wipe(pl, gp);
				}
	        });
			party[i].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        if (SwingUtilities.isRightMouseButton(e)) {
			        	JPanel teamMemberPanel = me.team[index].showSummary();
			            JOptionPane.showMessageDialog(null, teamMemberPanel, "Party member details", JOptionPane.PLAIN_MESSAGE);
			        }
			    }
			});
		}
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


	public Pokemon encounterPokemon(int area, int x, int y, boolean random) {
	    // Create an ArrayList of PokemonEncounter objects for the route
		//String selectedEncounterType = encounterType.getSelection().getActionCommand();
		//String selectedTime = time.getSelection().getActionCommand();
	    ArrayList<Encounter> encounters = Encounter.getEncounters(area, x, y, "Standard", "D", random);

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

	private Font getScaledFontSize(JButton button) {
	    String text = button.getText();
	    Font originalFont = button.getFont();
	    Font newFont = originalFont;

	    int textWidth = button.getFontMetrics(originalFont).stringWidth(text);
	    int maxWidth = button.getWidth() - 30;

	    while (textWidth > maxWidth && newFont.getSize() > 1) {
	        newFont = newFont.deriveFont((float) newFont.getSize() - 1);
	        textWidth = button.getFontMetrics(newFont).stringWidth(text);
	    }

	    return newFont;
	}

	private void updateCurrent(PlayerCharacter pl) {
		if (currentText != null) playerPanel.remove(currentText);
		if (userSprite != null) playerPanel.remove(userSprite);
		
		currentText = new JLabel("ERROR");
		currentText.setText(me.getCurrent().nickname + "  lv " + me.getCurrent().getLevel()); 
		currentText.setHorizontalAlignment(SwingConstants.LEFT);
		currentText.setBounds(219, 179, 140, 20);
		currentText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		currentText.setFont(getScaledFontSize(currentText));
		
		userSprite.setIcon(getSprite(me.getCurrent()));
		
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
		
		if (p.trainerOwned) scaledImage = flipHorizontal(scaledImage);
		
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
	    Move[] moveset = me.getCurrent().moveset;
	    for (int i = 0; i < moveButtons.length; i++) {
	        if (moveset[i] != null) {
	            moveButtons[i].setText(moveset[i].toString());
	            moveButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 9));
	            moveButtons[i].setBackground(moveset[i].mtype.getColor());
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
		foeText.setBounds(569, 37, 184, 20);
		foeText.setHorizontalAlignment(SwingConstants.LEFT);
		foeText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		foeText.setFont(getScaledFontSize(foeText));
		
		foeSprite.setIcon(getSprite(foe));
		
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
		foeHealthBar.setBounds(543, 59, 146, 14);
		
		playerPanel.add(foeText);
		playerPanel.add(foeHealthBar);
		
		if (foePartyPanel != null) playerPanel.remove(foePartyPanel);
		foePartyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		foePartyPanel.setBounds(525, 0, 200, 30);

		for (int i = 0; i < 6; i++) {
		    JRadioButton currentIcon = new JRadioButton(); // Create a new JRadioButton for each iteration
		    currentIcon.setEnabled(false); // Make the JRadioButton unchangeable by the user
		    currentIcon.setSelected(true);

		    if (foeTrainer != null && i < foeTrainer.getTeam().length) {
		        if (foeTrainer.getTeam()[i] == null) {
		            currentIcon.setVisible(false);
		            System.out.print("null ");
		        } else {
		            if (foeTrainer.getTeam()[i].isFainted()) {
		                currentIcon.setBackground(Color.gray);
		            } else {
		                currentIcon.setBackground(Color.red);
		            }
		            if (foeTrainer.currentIndex == i) {
		            	currentIcon.setBackground(Color.yellow);
		            }

		            foePartyPanel.add(currentIcon);
		        }
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
				party[i].setText(me.team[i + 1].nickname + "  lv " + me.team[i + 1].getLevel());
				party[i].setHorizontalAlignment(SwingConstants.CENTER);
				party[i].setBounds(10, 21 + (i % 5) * 54, 144, 30);
				party[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
				party[i].setFont(getScaledFontSize(party[i]));
				party[i].setBackground(me.team[i + 1].type1.getColor());
				playerPanel.add(party[i]);
				party[i].setVisible(true);
				
			} else {
				party[i].setVisible(false);
			}
			if (me.team[i + 1] != null && !me.team[i + 1].isFainted()) {
				partyHP[i].setMaximum(me.team[i + 1].getStat(0));
				partyHP[i].setValue(me.team[i + 1].currentHP);
				if (partyHP[i].getPercentComplete() > 0.5) {
					partyHP[i].setForeground(new Color(0, 255, 0));
				} else if (partyHP[i].getPercentComplete() <= 0.5 && partyHP[i].getPercentComplete() > 0.25) {
					partyHP[i].setForeground(new Color(255, 255, 0));
				} else {
					partyHP[i].setForeground(new Color(255, 0, 0));
				}
				partyHP[i].setBounds(10, 54 + (i % 5) * 54, 144, 7);
				partyHP[i].setVisible(true);
				playerPanel.add(partyHP[i]);
			} else {
				partyHP[i].setVisible(false);
			}
		}
		
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
		
		Pokemon faster = p1.getFaster(p2, field, m1P, m2P);
		
		Pokemon slower = faster == p1 ? p2 : p1;
		
		if (faster == p1) {
			faster.move(slower, m1, me, field, me.getTeam(), true);
			// Check for swap
			if (faster.vStatuses.contains(Status.SWITCHING)) faster = getSwap(pl, faster.lastMoveUsed == Move.BATON_PASS);
			
	        if (foeTrainer != null) slower.move(faster, m2, me, field, foeTrainer.getTeam(), false);
	        else slower.move(faster, m2, me, field, null, false);
		} else {
			if (foeTrainer != null) { faster.move(slower, m2, me, field, foeTrainer.getTeam(), true); }
			else { faster.move(slower, m2, me, field, null, true); }
	        slower.move(faster, m1, me, field, me.getTeam(), false);
	        // Check for swap
	        if (slower.vStatuses.contains(Status.SWITCHING)) slower = getSwap(pl, slower.lastMoveUsed == Move.BATON_PASS);
		}
        faster.endOfTurn(slower, me, field);
		slower.endOfTurn(faster, me, field);
		field.endOfTurn();
		updateBars(true);
		updateCurrent(pl);
		updateStatus();
		displayParty();
		if (foe.isFainted()) {
			if (foeTrainer != null && foeTrainer.getTeam() != null) {
				if (foeTrainer.hasNext()) {
					foe = foeTrainer.next();
					System.out.println("\n" + foeTrainer.toString() + " sends out " + foeTrainer.getCurrent().name + "!");
					foe.swapIn(me.getCurrent(), field, me);
					updateField(field);
					updateFoe();
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
		            		if (p != null) p.awardHappiness(5);
		            	}
		            }
		            if (foeTrainer.item != null) {
		            	me.bag.add(foeTrainer.item);
		            	message += "\nYou were given " + foeTrainer.item.toString();
		            }
		            if (foeTrainer.flagIndex != 0) {
		            	me.flags[foeTrainer.flagIndex] = true;
		            }
		            JOptionPane.showMessageDialog(null, message);

		            // Close the current Battle JFrame
		            dispose();
				}
			} else {
				JOptionPane.showMessageDialog(null, foe.name + " was defeated!");
				dispose();
			}
		}
		System.out.println();
	    System.out.println("------------------------------");
	    updateField(field);
	    if (me.wiped()) {
			wipe(pl, gp);
		}
	}

	private Pokemon getSwap(PlayerCharacter pl, boolean baton) {
		if (me.hasValidMembers()) {
			JPanel partyPanel = new JPanel();
		    partyPanel.setLayout(new GridLayout(6, 1));

		    for (int j = 0; j < 6; j++) {
		        if (me.team[j] != null && !me.team[j].isFainted() && !(me.team[j] == me.current)) {
		            JButton party = pl.setUpPartyButton(j);
		            final int index = j;

		            party.addActionListener(g -> {
		                if (baton) me.team[index].statStages = me.getCurrent().statStages;
		                me.swap(me.team[index], index);
		                me.getCurrent().swapIn(foe, field, me);
		                updateField(field);
		                foe.vStatuses.remove(Status.TRAPPED);
		                foe.vStatuses.remove(Status.SPUN);
		                updateBars(true);
		                SwingUtilities.getWindowAncestor(partyPanel).dispose();
		            });

		            JPanel memberPanel = new JPanel(new BorderLayout());
		            memberPanel.add(party, BorderLayout.NORTH);
		            partyPanel.add(memberPanel);
		        }
		    }

		    JPanel wrapperPanel = new JPanel(new BorderLayout());
		    wrapperPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add the desired blank border
		    wrapperPanel.add(partyPanel, BorderLayout.NORTH);

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
		healthBar.setBounds(193, 201, 146, 14);
		playerPanel.add(healthBar);
			
		expBar.setMaximum(me.getCurrent().expMax);
		expBar.setValue(me.getCurrent().exp);
		expBar.setBounds(193, 216, 146, 7);
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
	
	public static final class JGradientButton extends JButton{
		/**
		 * 
		 */
		private static final long serialVersionUID = 639680055516122456L;

		public JGradientButton(String text){
	        super(text);
	        setContentAreaFilled(false);
	    }

	    @Override // implementation
	    protected void paintComponent(Graphics g){
	        Graphics2D g2 = (Graphics2D)g.create();
	        g2.setPaint(new GradientPaint(
	                new Point(0, 0), 
	                getBackground(), 
	                new Point(0, getHeight()/3), 
	                Color.WHITE));
	        g2.fillRect(0, 0, getWidth(), getHeight()/3);
	        g2.setPaint(new GradientPaint(
	                new Point(0, getHeight()/3), 
	                Color.WHITE, 
	                new Point(0, getHeight()), 
	                getBackground()));
	        g2.fillRect(0, getHeight()/3, getWidth(), getHeight());
	        g2.dispose();

	        super.paintComponent(g);
	    }
	}
	
//	private void fightMon() {
//		try {
//			if (Integer.parseInt(idInput.getText()) >= -144 && Integer.parseInt(idInput.getText()) <= 237) {
//				foe = new Pokemon(Integer.parseInt(idInput.getText()), Integer.parseInt(levelInput.getText()), false, false);
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
		dispose();
		p.p.money -= 500;
		p.p.money = p.p.money < 0 ? 0 : p.p.money;
		JOptionPane.showMessageDialog(null, "You have no more Pokemon that can fight!\nYou lost $500!");
		p.worldX = gp.tileSize * 90;
		p.worldY = gp.tileSize * 46;
		gp.currentMap = 0;
		for (Pokemon pokemon : p.p.team) {
			if (pokemon != null) {
				pokemon.heal();
			}
		}
		for (Pokemon pokemon : foeTrainer.getTeam()) {
			pokemon.heal();
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
	
//	private Trainer[] getUnbeatenTrainers() {
//		// First, create a list to store trainers that haven't been beaten
//		ArrayList<Trainer> unbeatenTrainers = new ArrayList<>();
//
//		// Iterate through all trainers and add unbeaten trainers to the list
//		for (Trainer trainer : trainers) {
//		    if (!me.trainersBeat.contains(trainer.toString())) {
//		        unbeatenTrainers.add(trainer);
//		    }
//		}
//		// Create a new array with the unbeaten trainers
//		Trainer[] unbeatenTrainersArray = unbeatenTrainers.toArray(new Trainer[0]);
//		return unbeatenTrainersArray;
//	}
	
//	private void updateTrainers() {
//		Trainer[] unbeatenTrainers = getUnbeatenTrainers();
//	    DefaultComboBoxModel<Trainer> model = new DefaultComboBoxModel<>(unbeatenTrainers);
//	    model.insertElementAt(new Trainer(true), 0); // Add placeholder at index 0
//	    trainerSelect.setModel(model);
//	    trainerSelect.setSelectedIndex(0); // Set placeholder as default selection
//	}
	
	public void setBattleCloseListener(BattleCloseListener listener) {
        this.battleCloseListener = listener;
    }
	
	@Override
	public void dispose() {
		super.dispose();
		if (battleCloseListener != null) battleCloseListener.onBattleClosed(trainerIndex);
	}
	
	public interface BattleCloseListener {
	    void onBattleClosed(int trainer);
	}
	
}
