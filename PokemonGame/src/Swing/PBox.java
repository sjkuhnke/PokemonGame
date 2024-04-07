package Swing;

import javax.swing.border.BevelBorder;

import Entity.PlayerCharacter;
import Overworld.GamePanel;
import Overworld.KeyHandler;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import Swing.Battle.JGradientButton;
import Swing.CompoundIcon.Axis;

public class PBox extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943929896582623587L;
	public static Player me;
	public static PlayerCharacter playerCharacter;
	public GamePanel gp;
	public KeyHandler keyH;
	
	private JLabel boxLabel = new JLabel();

	private JGradientButton[] boxButtons;
	private boolean isGauntlet;

	public PBox(GamePanel gp, KeyHandler keyH, boolean isGauntlet) {
	    me = gp.player.p;
	    this.gp = gp;
	    this.keyH = keyH;
	    
	    this.isGauntlet = isGauntlet;
	    setLayout(null);
	    
	    boxLabel.setBounds(200, 500, 200, 50);
	    this.add(boxLabel);

		
		boxButtons = new JGradientButton[30];
		for (int i = 0; i < me.boxes[1].length; i++) {
			boxButtons[i] = new JGradientButton("");
			boxButtons[i].setBounds(10 + (i % 6) * 90, 10 + (i / 6) * 80, 80, 60);
			boxButtons[i].setVisible(true);
			this.add(boxButtons[i]);
		}
		
		JButton nextButton = new JButton("Next");
		nextButton.setBounds(520, 460, 80, 30);
		nextButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // Display the next box
		        me.currentBox++;
		        if (me.currentBox >= Player.MAX_BOXES) {
		            me.currentBox = 0;
		        }
		        displayBox();
		    }
		});
		this.add(nextButton);
		
		JButton partyButton = new JButton("Party");
		partyButton.setBounds(235, 440, 120, 50);
		partyButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	JPanel partyMasterPanel = new JPanel();
		    	partyMasterPanel.setLayout(new GridLayout(3, 2, 5, 5));
			    partyMasterPanel.setPreferredSize(new Dimension(350, 350));
        	    
        	    for (int j = 0; j < 6; j++) {
        	    	Pokemon p = isGauntlet && me.team[j] != null && !me.team[j].isSelected() ? null : me.team[j];
        	    	PartyPanel partyPanel = new PartyPanel(p, true);
        	        final int index = j;
        	        
        	        if (p != null) {
        	        	partyPanel.addMouseListener(new MouseAdapter() {
            	        	@Override
            	            public void mouseClicked(MouseEvent e) {
            	        		if (SwingUtilities.isLeftMouseButton(e)) {
            	        			JPanel summary = me.team[index].showSummary(me, true, partyMasterPanel, null);
                    	        	JButton depositButton = new JButton("Deposit");
                    	        	depositButton.addActionListener(f -> {
                    		            if (me.team[index] != null) {
                    		            	Pokemon[] cBox = me.boxes[me.currentBox];
                    		            	
                    		            	Pokemon head = cBox[0];
                    		            	int count = 0;
                    		            	boolean emptyFound = false;
                    		                for (Pokemon p : cBox) {
                    		                	if (p == null) {
                    		                		emptyFound = true;
                    		                		break;
                    		                	}
                    		                	if (count != 0) {
                    		                		if (p == head) break;
                    		                	}
                    		                	count++;
                    		                }
                    		                if (!emptyFound) {
                    		                	JOptionPane.showMessageDialog(null, "Box is full!");
                    		                } else if (index == 0 && index != me.team.length - 1 && me.team[index + 1] == null) {
                                        		JOptionPane.showMessageDialog(null, "That's your last Pokemon!");
                                        	} else {
                    		                	Pokemon temp = me.team[index];
                                                if (temp != null) {
                                                    temp.heal();
                                                }
                                                me.team[index] = cBox[count];
                                                cBox[count] = temp;
                                                
                                                if (me.team[index] == null) {
                                                	Pokemon[] teamTemp = me.team.clone();
                                                	for (int i = index + 1; i < me.team.length; i++) {
                                                    	teamTemp[i - 1] = me.team[i];
                                                    }
                                                	me.team = teamTemp;
                                                	me.current = me.team[0];
                                                	me.team[5] = null;
                                                }
                    		                }
                                            SwingUtilities.getWindowAncestor(partyMasterPanel).dispose();
                    		                SwingUtilities.getWindowAncestor(summary).dispose();
                    		                displayBox();
                    		            }
                    		        });
                    	            summary.add(depositButton);
                    	            JOptionPane.showMessageDialog(null, summary, "Party member details", JOptionPane.PLAIN_MESSAGE);
            	        		} else {
            	        			if (me.team[index] != null) {
                		            	Pokemon[] cBox = me.boxes[me.currentBox];
                		            	
                		            	Pokemon head = cBox[0];
                		            	int count = 0;
                		            	boolean emptyFound = false;
                		                for (Pokemon p : cBox) {
                		                	if (p == null) {
                		                		emptyFound = true;
                		                		break;
                		                	}
                		                	if (count != 0) {
                		                		if (p == head) break;
                		                	}
                		                	count++;
                		                }
                		                if (!emptyFound) {
                		                	JOptionPane.showMessageDialog(null, "Box is full!");
                		                } else if (index == 0 && index != me.team.length - 1 && me.team[index + 1] == null) {
                                    		JOptionPane.showMessageDialog(null, "That's your last Pokemon!");
                                    	} else {
                		                	Pokemon temp = me.team[index];
                                            if (temp != null) {
                                                temp.heal();
                                            }
                                            me.team[index] = cBox[count];
                                            cBox[count] = temp;
                                            
                                            if (me.team[index] == null) {
                                            	Pokemon[] teamTemp = me.team.clone();
                                            	for (int i = index + 1; i < me.team.length; i++) {
                                                	teamTemp[i - 1] = me.team[i];
                                                }
                                            	me.team = teamTemp;
                                            	me.current = me.team[0];
                                            	me.team[5] = null;
                                            }
                		                }
                                        SwingUtilities.getWindowAncestor(partyMasterPanel).dispose();
                		                displayBox();
                		            }
            	        		}
            	        	}
            	        });
        	        }
        	        partyMasterPanel.add(partyPanel);
        	    }
        	    JOptionPane.showMessageDialog(null, partyMasterPanel, "Party", JOptionPane.PLAIN_MESSAGE);
		    }
		});
		this.add(partyButton);

		JButton previousButton = new JButton("Prev");
		previousButton.setBounds(10, 460, 80, 30);
		previousButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // Display the previous box
		        me.currentBox--;
		        if (me.currentBox < 0) {
		            me.currentBox = Player.MAX_BOXES - 1;
		        }
		        displayBox();
		    }
		});
		this.add(previousButton);
		
		JButton exitButton = new JButton("X");
		exitButton.setBounds(555, 10, 60, 60);
		exitButton.addActionListener(e -> {
			gp.removePanel();
			gp.addGamePanel();
			//dispose();
		});
		this.add(exitButton);
		
		JButton calcButton = new JButton("Calc");
		calcButton.setBounds(555, 80, 60, 60);
		calcButton.addActionListener(e -> {
			Pokemon[] cBox = me.boxes[me.currentBox];
			Item.useCalc(me, cBox, isGauntlet);
		});
		this.add(calcButton);
		
		displayBox();
		
	}
	
	public void displayBox() {
		boxLabel.setText("Box " + (me.currentBox + 1));
	    Pokemon[] cBox = me.boxes[me.currentBox];

	    for (int i = 0; i < cBox.length; i++) {
	        boxButtons[i].setText("");
	        boxButtons[i].setIcon(null);
	        if (cBox[i] != null) {
	        	ImageIcon spriteIcon = isGauntlet && !cBox[i].isSelected() ? cBox[i].getFaintedIcon() : new ImageIcon(cBox[i].getMiniSprite());
	        	ImageIcon itemIcon = null;
	        	CompoundIcon icon = new CompoundIcon(Axis.Z_AXIS, 0, CompoundIcon.LEFT, CompoundIcon.BOTTOM, spriteIcon);
	        	if ((isGauntlet && cBox[i].isSelected()) || !isGauntlet) {
	        		if (cBox[i].item != null) {
	        			itemIcon = new ImageIcon(cBox[i].item.getImage());
	        			icon = new CompoundIcon(Axis.Z_AXIS, 0, CompoundIcon.LEFT, CompoundIcon.BOTTOM, spriteIcon, itemIcon);
	        		}
	        		boxButtons[i].setBackground(cBox[i].type1.getColor(), cBox[i].type2 == null ? null : cBox[i].type2.getColor());
	        	} else {
	        		boxButtons[i].setBackground(null);
	        	}
	            boxButtons[i].setIcon(icon);
	            boxButtons[i].setHorizontalAlignment(SwingConstants.CENTER);
	            boxButtons[i].setFont(new Font("Tahoma", Font.BOLD, 9));
	            
	        } else {
	            boxButtons[i].setText("");
	            boxButtons[i].setIcon(null);
	            boxButtons[i].setBackground(null);
	        }
	        MouseListener[] mouseListeners = boxButtons[i].getMouseListeners();
	        if (mouseListeners.length > 1) boxButtons[i].removeMouseListener(mouseListeners[1]);
	        setActionListener(boxButtons[i], cBox, i);
	        boxButtons[i].setVisible(true);
	        this.add(boxButtons[i]);
	    }
	}

	public void setActionListener(JButton b, Pokemon[] box, int index) {
		PBox thisBox = this;
		MouseListener[] mouseListeners = b.getMouseListeners();
		if (mouseListeners.length == 1) {
			b.addMouseListener(new MouseAdapter() {
		    	@Override
		        public void mouseClicked(MouseEvent e) {
		    		if (box[index] != null && isGauntlet && !box[index].isSelected()) {
    	    			JOptionPane.showMessageDialog(null, box[index].nickname + " isn't selected!");
    	    			return;
    	    		}
		    		if (SwingUtilities.isLeftMouseButton(e)) {
		    			JPanel boxMemberPanel;
		    	    	if (box[index] != null) {
		    	    		boxMemberPanel = box[index].showSummary(me, true, null, thisBox);
		    	    	} else {
		    	    		boxMemberPanel = new JPanel();
		    	    		boxMemberPanel.setLayout(new BoxLayout(boxMemberPanel, BoxLayout.Y_AXIS));
		    	    	}
		                JGradientButton swapButton = new JGradientButton("Swap with a party member");
		                swapButton.setBackground(new Color(0, 227, 252));
		                JGradientButton forgetButton = new JGradientButton("Forget a Move");
		                forgetButton.setBackground(new Color(26, 199, 40));
		                JGradientButton moveButton = new JGradientButton("Teach Move");
		                moveButton.setBackground(new Color(255, 251, 0));
		                JGradientButton changeNick = new JGradientButton("Change Nickname");
		                changeNick.setBackground(new Color(252, 147, 0));
		                JGradientButton seeForgottenMoves = new JGradientButton("See Forgotten Moves");
		                seeForgottenMoves.setBackground(new Color(252, 147, 0));
		                JGradientButton releaseButton = new JGradientButton("Release");
		                releaseButton.setBackground(new Color(214, 6, 17));
		                swapButton.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                        // Open a new panel to display the party and allow the user to select a party member to swap with
		                    	JPanel partyMasterPanel = new JPanel();
		                	    partyMasterPanel.setLayout(new GridLayout(3, 2, 5, 5));
		                	    partyMasterPanel.setPreferredSize(new Dimension(350, 350));
		                	    boolean oneVisible = false;
		                	    
		                	    for (int j = 0; j < 6; j++) {
		                	    	JPanel partyPanel = new PartyPanel(me.team[j], true);
		                	        final int jndex = j;
		                	        
		                            if (me.team[j] == null) {
		                            	if (!oneVisible) {
		                            		partyPanel = new JPanel();
		                            		partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));

		                            		JLabel empty = new JLabel("EMPTY");
		                            		empty.setHorizontalAlignment(SwingConstants.CENTER);
		                            		empty.setAlignmentX(Component.CENTER_ALIGNMENT);
		                            		empty.setAlignmentY(Component.CENTER_ALIGNMENT);
		                            		empty.setFont(new Font(empty.getFont().getName(), Font.BOLD, 24));

		                            		partyPanel.add(Box.createHorizontalGlue());
		                            		partyPanel.add(Box.createVerticalGlue());
		                            		partyPanel.add(empty);
		                            		partyPanel.add(Box.createHorizontalGlue());
		                            		partyPanel.add(Box.createVerticalGlue());
		                            		JPanel pp = partyPanel;
		                            		partyPanel.addMouseListener(new MouseAdapter() {
		                            			public void mouseEntered(MouseEvent evt) {
		                            				pp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		                            			}
		                            			public void mouseExited(MouseEvent evt) {
		                            				pp.setBorder(BorderFactory.createEmptyBorder());
		                            			}
		                            		});
		                                    oneVisible = true;
		                                }
		                            }
		                            if (j == 0 || me.team[j - 1] != null) {
		                            	partyPanel.addMouseListener(new MouseAdapter() {
			                                @Override
			                                public void mouseClicked(MouseEvent e) {

			                                    // Swap the selected party member with the selected box member
			                                    if (jndex == 0) {
			                                    	if (box[index] == null && me.team[jndex + 1] == null) {
			                                    		JOptionPane.showMessageDialog(null, "That's your last Pokemon!");
			            	                            return;
			                                    	}
			                                        me.current = box[index];
			                                    }
			                                    if (box[index] == null && checkFaintedTeam(jndex)) {
			                                    	JOptionPane.showMessageDialog(null, "That's your last Pokemon!");
			        	                            return;
			                                    }
			                                    Pokemon temp = me.team[jndex];
			                                    if (temp != null) {
			                                        temp.heal();
			                                    }
			                                    me.team[jndex] = box[index];
			                                    box[index] = temp;
			                                    
			                                    if (me.team[jndex] == null) {
			                                    	Pokemon[] teamTemp = me.team.clone();
			                                    	for (int i = jndex + 1; i < me.team.length; i++) {
			                                        	teamTemp[i - 1] = me.team[i];
			                                        }
			                                    	me.team = teamTemp;
			                                    	me.current = me.team[0];
			                                    	me.team[5] = null;
			                                    }
			                                    

			                                    // Update the display
			                                    SwingUtilities.getWindowAncestor(partyMasterPanel).dispose();
			                                    SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
			                                    displayBox();
			                                }
			                            });
		                            }
	                            	partyMasterPanel.add(partyPanel);
		                	    }
		                        JOptionPane.showMessageDialog(null, partyMasterPanel, "Select a party member to swap with:", JOptionPane.PLAIN_MESSAGE);
		                    }
		                });
		                forgetButton.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                    	if (box[index] == null) {
		                    		JOptionPane.showMessageDialog(null, "No Pokemon to forget a move.");
		                            return;
		                    	}

		                        // Display the moveset and allow the player to select a move to replace
		                        JPanel moveset = new JPanel();
		                        moveset.setLayout(new BoxLayout(moveset, BoxLayout.Y_AXIS));
		                        JLabel titleLabel = new JLabel("Select a move to forget:");
		                        moveset.add(titleLabel);
		                        for (int j = 0; j < box[index].moveset.length; j++) {
		                            JButton moveslot;
		                            if (box[index].moveset[j] != null) {
		                                moveslot = new JGradientButton(box[index].moveset[j].move.toString());
		                                moveslot.setBackground(box[index].moveset[j].move.mtype.getColor());
		                            } else {
		                                break;
		                            }
		                            moveslot.setVisible(true);

		                            int moveIndex = j;
		                            moveslot.addActionListener(new ActionListener() {
		                                @Override
		                                public void actionPerformed(ActionEvent e) {
		                                    // Replace the selected move with the new move
		                                	if (moveIndex == 0 && box[index].moveset[moveIndex + 1] == null) {
		                                		JOptionPane.showMessageDialog(null, "Can't forget your last move!");
		                                	} else {
		                                		int result = JOptionPane.showConfirmDialog(
		                                	            null,
		                                	            "Are you sure you want to forget " + box[index].moveset[moveIndex].move.toString() + "?",
		                                	            "Confirm Move Forget",
		                                	            JOptionPane.YES_NO_OPTION
		                                	        );

		                            	        if (result == JOptionPane.YES_OPTION) {
		                            	            box[index].moveset[moveIndex] = null;
		                            	            for (int k = moveIndex + 1; k < 4; k++) {
		                            	                box[index].moveset[k - 1] = box[index].moveset[k];
		                            	            }
		                            	            box[index].moveset[3] = null;

		                        	            // Update the display
		                        	            SwingUtilities.getWindowAncestor(moveset).dispose();
		                        	            SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
		                        	            displayBox();
		                            	        }
		                                	}
		                                }
		                            });
		                            moveset.add(moveslot);
		                        }
		                        JScrollPane scrollPane = new JScrollPane(moveset);
		                        scrollPane.setPreferredSize(new Dimension(300, 200));
		                        JOptionPane.showMessageDialog(null, scrollPane, "Forget move", JOptionPane.PLAIN_MESSAGE);
		                    }
		                });
		                moveButton.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                    	if (box[index] == null) {
		                    		JOptionPane.showMessageDialog(null, "No Pokemon to teach.");
		                            return;
		                    	}
		                    	String pass = JOptionPane.showInputDialog(null, "This function is password protected because it is a developer\ntool. Enter password: ");
		                    	if (pass != null && pass.equals("abner")) {
		                    		// Prompt the player to enter the name of the move they want to teach
		                            String moveName = JOptionPane.showInputDialog(null, "Enter the name of the move you want to teach:");

		                            // Find the move in the move database
		                            Move move = Move.getMove(moveName);
		                            if (move == null) {
		                                JOptionPane.showMessageDialog(null, "Invalid move name.");
		                                return;
		                            }
		                            for (int l = 0; l < box[index].moveset.length; l++) {
		                            	if (box[index].moveset[l] != null && move == box[index].moveset[l].move) {
		                            		JOptionPane.showMessageDialog(null, box[index].nickname + " already knows " + move.toString() + "!");
		                                    return;
		                            	}
		                            }

		                            // Add a boolean flag to keep track of whether an empty move slot has been found
		                            boolean foundEmptySlot = false;

		                            // Display the moveset and allow the player to select a move to replace
		                            JPanel moveset = new JPanel();
		                            moveset.setLayout(new BoxLayout(moveset, BoxLayout.Y_AXIS));
		                            JLabel titleLabel = new JLabel("Select a move to override:");
		                            moveset.add(titleLabel);
		                            for (int j = 0; j < box[index].moveset.length; j++) {
		                                JButton moveslot;
		                                if (box[index].moveset[j] != null) {
		                                    moveslot = new JGradientButton(box[index].moveset[j].move.toString());
		                                    moveslot.setBackground(box[index].moveset[j].move.mtype.getColor());
		                                } else {
		                                    // Only display the first empty move slot
		                                    if (!foundEmptySlot) {
		                                        moveslot = new JGradientButton("EMPTY");
		                                        foundEmptySlot = true;
		                                    } else {
		                                        // Don't display any more empty move slots
		                                        continue;
		                                    }
		                                }
		                                moveslot.setVisible(true);

		                                int moveIndex = j;
		                                moveslot.addActionListener(new ActionListener() {
		                                    @Override
		                                    public void actionPerformed(ActionEvent e) {
		                                        // Replace the selected move with the new move
		                                        box[index].moveset[moveIndex] = new Moveslot(move);

		                                        // Update the display
		                                        SwingUtilities.getWindowAncestor(moveset).dispose();
		                                        SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
		                                        displayBox();
		                                    }
		                                });
		                                moveset.add(moveslot);
		                            }
		                            JScrollPane scrollPane = new JScrollPane(moveset);
		                            scrollPane.setPreferredSize(new Dimension(300, 200));
		                            JOptionPane.showMessageDialog(null, scrollPane, "Override move", JOptionPane.PLAIN_MESSAGE);

		                    	} else {
		                    		JOptionPane.showMessageDialog(null, "Incorrect Password.");
		                    	}
		                    }
		                });
		                releaseButton.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to release this Pokemon?", "Release Pokemon", JOptionPane.YES_NO_OPTION);
		                        if (confirm == JOptionPane.YES_OPTION) {
		                            // code to release the box member
		                            box[index] = null;
		                            boxButtons[index].setText("");
		                            boxButtons[index].setIcon(null);
		                            boxButtons[index].setBackground(null);
		                            boxMemberPanel.setVisible(false);
		                            SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
		                        }
		                    }
		                });
		                changeNick.addActionListener(new ActionListener() {
		                    @Override
		                    public void actionPerformed(ActionEvent e) {
		                    	if (box[index] == null) {
		                    		JOptionPane.showMessageDialog(null, "No Pokemon to change nickname.");
		                    	} else {
		                    		box[index].setNickname();
		                    		SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
		                    	}
		                    }
		                });
		                boxMemberPanel.add(swapButton);
		                boxMemberPanel.add(forgetButton);
		                boxMemberPanel.add(moveButton);
		                boxMemberPanel.add(changeNick);
		                boxMemberPanel.add(releaseButton);
		                JOptionPane.showMessageDialog(null, boxMemberPanel, "Box member details", JOptionPane.PLAIN_MESSAGE);
		    		} else {
		    			if (box[index] != null) {
		    				int nullIndex = -1;
		    				for (int i = 0; i < me.team.length; i++) {
		    					if (me.team[i] == null) {
		    						nullIndex = i;
		    						break;
		    					}
		    				}
		    				if (nullIndex == -1) {
		    					JOptionPane.showMessageDialog(null, "Party is full!");
		    				} else {
		    					me.team[nullIndex] = box[index];
		    					box[index] = null;
	    		                displayBox();
		    				}
		    			}
		    		}
		    	}
		    });
		}
	}
	
	public static int displayMoveOptions(Pokemon pokemon, Move move) {
	    String[] moves = new String[4];
	    JGradientButton[] buttons = new JGradientButton[4];
	    JPanel panel = new JPanel();
	    int[] choice = new int[1];
	    choice[0] = -1;
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    JLabel label = new JLabel(pokemon.nickname + " wants to learn " + move.toString() + ".");
	    JLabel label2 = new JLabel("Select a move to replace:");
	    JGradientButton learnButton = new JGradientButton(move.toString());
	    learnButton.setBackground(move.mtype.getColor());
	    panel.add(label);
	    panel.add(learnButton);
	    panel.add(label2);
	    for (int i = 0; i < 4; i++) {
	        if (pokemon.moveset[i] != null) {
	            moves[i] = pokemon.moveset[i].move.toString();
	        } else {
	            moves[i] = "";
	        }
	        buttons[i] = new JGradientButton(moves[i]);
	        buttons[i].setBackground(pokemon.moveset[i].move.mtype.getColor());
	        if (moves[i].equals("")) {
	            buttons[i].setEnabled(false);
	        }
	        int index = i;
	        buttons[i].addMouseListener(new MouseAdapter() {
	        	@Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            JOptionPane.showMessageDialog(null, pokemon.moveset[index].move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
			        } else {
			        	choice[0] = index;
		                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor((JButton) e.getSource());
		                dialog.dispose();
			        }
			    }
	        });
	        panel.add(buttons[i]);
	    }
	    learnButton.addMouseListener(new MouseAdapter() {
        	@Override
		    public void mouseClicked(MouseEvent e) {
		    	if (SwingUtilities.isRightMouseButton(e)) {
		            JOptionPane.showMessageDialog(null, move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
		        } else {
		        	choice[0] = JOptionPane.CLOSED_OPTION;
	                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor((JButton) e.getSource());
	                dialog.dispose();
		        }
		    }
        });

	    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
	    JDialog dialog = optionPane.createDialog("Learn New Move");
	    dialog.setVisible(true);
	    int result = choice[0];
	    return result == JOptionPane.CLOSED_OPTION ? JOptionPane.CLOSED_OPTION : choice[0];
	}

	
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
	    JLabel label = new JLabel(pokemon.nickname + " is evolving.");
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
	
	private boolean checkFaintedTeam(int jndex) {
		for (Pokemon p : me.team) {
			if (p != null && p != me.team[jndex] && !p.isFainted()) {
				return false;
			}
		}
		return true;
	}
	
}

