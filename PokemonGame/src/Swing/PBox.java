package Swing;

import javax.swing.border.EmptyBorder;

import Entity.PlayerCharacter;
import Swing.Pokemon.Node;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

public class PBox extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8943929896582623587L;
	private JPanel playerPanel;
	public static Player me;

	private JGradientButton[] boxButtons;
	private int currentBox = 1;

	public PBox(PlayerCharacter playerCharacter) {
	    me = playerCharacter.p;
	    setTitle("Box 1");
	    
	    // Initializing panel
	    setResizable(false);
	    setPreferredSize(new Dimension(768, 576));
	    setBounds(100, 100, 648, 530);
	    playerPanel = new JPanel();
	    playerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(playerPanel);
	    playerPanel.setLayout(null);

	    // Set the location of the Box in the center of the screen
	    setLocationRelativeTo(null);

		
		boxButtons = new JGradientButton[90];
		for (int i = 0; i < me.box1.length; i++) {
			boxButtons[i] = new JGradientButton("");
			boxButtons[i].setBounds(10 + (i % 6) * 90, 10 + (i / 6) * 80, 80, 50);
			boxButtons[i].setVisible(true);
			playerPanel.add(boxButtons[i]);
		}
		
		JButton nextButton = new JButton("Next");
		nextButton.setBounds(520, 460, 80, 30);
		nextButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // Display the next box
		        currentBox++;
		        if (currentBox > 3) {
		            currentBox = 1;
		        }
		        displayBox();
		        setTitle("Box " + currentBox);
		    }
		});
		playerPanel.add(nextButton);

		JButton previousButton = new JButton("Prev");
		previousButton.setBounds(10, 460, 80, 30);
		previousButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // Display the previous box
		        currentBox--;
		        if (currentBox < 1) {
		            currentBox = 3;
		        }
		        displayBox();
		        setTitle("Box " + currentBox);
		    }
		});
		playerPanel.add(previousButton);
		
		displayBox();
		
	}
	
	private void displayBox() {
	    Pokemon[] cBox;
	    if (currentBox == 1) {
	        cBox = me.box1;
	    } else if (currentBox == 2) {
	        cBox = me.box2;
	    } else {
	        cBox = me.box3;
	    }

	    for (int i = 0; i < cBox.length; i++) {
	        boxButtons[i].setText("");
	        boxButtons[i].setIcon(null);
	        if (cBox[i] != null) {
	        	ImageIcon icon = new ImageIcon(cBox[i].getSprite());
	            boxButtons[i].setIcon(icon);
	            boxButtons[i].setHorizontalAlignment(SwingConstants.CENTER);
	            boxButtons[i].setFont(new Font("Tahoma", Font.BOLD, 9));
	            boxButtons[i].setBackground(cBox[i].type1.getColor());
	        } else {
	            boxButtons[i].setText("");
	            boxButtons[i].setIcon(null);
	            boxButtons[i].setBackground(null);
	        }
	        setActionListener(boxButtons[i], cBox, i);
	        boxButtons[i].setVisible(true);
	        playerPanel.add(boxButtons[i]);
	    }
	}

	public void setActionListener(JButton b, Pokemon[] box, int index) {
		ActionListener[] actionListeners = b.getActionListeners();
	    for (ActionListener listener : actionListeners) {
	        b.removeActionListener(listener);
	    }
		
	    b.addActionListener(e -> {
	    	JPanel boxMemberPanel;
	    	if (box[index] != null) {
	    		boxMemberPanel = box[index].showSummary();
	    	} else {
	    		boxMemberPanel = new JPanel();
	    	}
            JGradientButton swapButton = new JGradientButton("Swap with a party member");
            swapButton.setBackground(new Color(0, 227, 252));
            JGradientButton moveButton = new JGradientButton("Teach Move");
            moveButton.setBackground(new Color(255, 251, 0));
            JGradientButton seeForgottenMoves = new JGradientButton("See Forgotten Moves");
            seeForgottenMoves.setBackground(new Color(252, 147, 0));
            JGradientButton releaseButton = new JGradientButton("Release");
            releaseButton.setBackground(new Color(214, 6, 17));
            swapButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Open a new panel to display the party and allow the user to select a party member to swap with
                    JPanel partyPanel = new JPanel();
                    partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
                    JLabel titleLabel = new JLabel("Select a party member to swap with:");
                    partyPanel.add(titleLabel);
                    boolean oneVisible = false;
                    for (int j = 0; j < me.team.length; j++) {
                        final int jndex = j;
                        JButton partyButton = new JGradientButton("EMPTY");
                        partyButton.setVisible(false);
                        if (me.team[j] != null) {
                            partyButton.setText(me.team[j].nickname + "  lv " + me.team[j].getLevel());
                            if (me.team[j].isFainted()) {
                                partyButton.setBackground(Color.RED);
                            } else if (me.team[j].status != Status.HEALTHY) {
                                partyButton.setBackground(Color.YELLOW);
                            } else {
                                partyButton.setBackground(Color.GREEN);
                            }
                            partyButton.setVisible(true);
                        } else {
                            if (!oneVisible) {
                                partyButton.setVisible(true);
                                oneVisible = true;
                            }
                        }
                        partyButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                // Swap the selected party member with the selected box member
                                if (jndex == 0) {
                                	if (box[index] == null) {
                                		JOptionPane.showMessageDialog(null, "You cannot remove the lead from your party.");
        	                            return;
                                	}
                                    me.current = box[index];
                                }
                                Pokemon temp = me.team[jndex];
                                if (temp != null) {
                                    temp.heal();
                                }
                                me.team[jndex] = box[index];
                                box[index] = temp;

                                // Update the display
                                SwingUtilities.getWindowAncestor(partyPanel).dispose();
                                SwingUtilities.getWindowAncestor(boxMemberPanel).dispose();
                                displayBox();
                            }

                        });
                        partyPanel.add(partyButton);
                    }
                    JScrollPane scrollPane = new JScrollPane(partyPanel);
                    scrollPane.setPreferredSize(new Dimension(300, 200));
                    JOptionPane.showMessageDialog(null, scrollPane, "Swap with a party member", JOptionPane.PLAIN_MESSAGE);
                }
            });
            moveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if (box[index] == null) {
                		JOptionPane.showMessageDialog(null, "No Pokemon to teach.");
                        return;
                	}
                    // Prompt the player to enter the name of the move they want to teach
                    String moveName = JOptionPane.showInputDialog(null, "Enter the name of the move you want to teach:");

                    // Find the move in the move database
                    Move move = Move.getMove(moveName);
                    if (move == null) {
                        JOptionPane.showMessageDialog(null, "Invalid move name.");
                        return;
                    }
                    for (int l = 0; l < box[index].moveset.length; l++) {
                    	if (move == box[index].moveset[l]) {
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
                            moveslot = new JGradientButton(box[index].moveset[j].toString());
                            moveslot.setBackground(box[index].moveset[j].mtype.getColor());
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
                                box[index].moveset[moveIndex] = move;

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
            seeForgottenMoves.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if (box[index] == null) {
                		JOptionPane.showMessageDialog(null, "No Pokemon to check.");
                        return;
                	}
                    ArrayList<Move> forgottenMoves = new ArrayList<>();
                    for (int i = 0; i < box[index].getLevel(); i++) {
                    	if (i < box[index].movebank.length) {
                    		Node move = box[index].movebank[i];
                    		while (move != null) {
                    			if (!box[index].knowsMove(move.data)) {
                    				forgottenMoves.add(move.data);
                    			}
                    			move = move.next;
                    		}
                    	}
                    }
                    if (forgottenMoves.isEmpty()) {
                        JOptionPane.showMessageDialog(boxMemberPanel, "This Pokemon has not forgotten any moves.");
                    } else {
                        String message = "This Pokemon has forgotten the following moves:\n";
                        for (Move move : forgottenMoves) {
                            message += "- " + move + "\n";
                        }
                        JOptionPane.showMessageDialog(boxMemberPanel, message);
                    }
                }
            });
            boxMemberPanel.add(swapButton);
            boxMemberPanel.add(moveButton);
            boxMemberPanel.add(seeForgottenMoves);
            boxMemberPanel.add(releaseButton);
            JOptionPane.showMessageDialog(null, boxMemberPanel, "Box member details", JOptionPane.PLAIN_MESSAGE);
	    });
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
	            moves[i] = pokemon.moveset[i].toString();
	        } else {
	            moves[i] = "";
	        }
	        buttons[i] = new JGradientButton(moves[i]);
	        buttons[i].setBackground(pokemon.moveset[i].mtype.getColor());
	        if (moves[i].equals("")) {
	            buttons[i].setEnabled(false);
	        }
	        int index = i;
	        buttons[i].addMouseListener(new MouseAdapter() {
	        	@Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			            String message = "Move: " + pokemon.moveset[index].toString() + "\n";
			            message += "Type: " + pokemon.moveset[index].mtype + "\n";
			            message += "BP: " + pokemon.moveset[index].getbp() + "\n";
			            message += "Accuracy: " + pokemon.moveset[index].accuracy + "\n";
			            message += "Category: " + pokemon.moveset[index].getCategory() + "\n";
			            message += "Description: " + pokemon.moveset[index].getDescription();
			            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
		            String message = "Move: " + move.toString() + "\n";
		            message += "Type: " + move.mtype + "\n";
		            message += "BP: " + move.getbp() + "\n";
		            message += "Accuracy: " + move.accuracy + "\n";
		            message += "Category: " + move.getCategory() + "\n";
		            message += "Description: " + move.getDescription();
		            JOptionPane.showMessageDialog(null, message, "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
	
}

