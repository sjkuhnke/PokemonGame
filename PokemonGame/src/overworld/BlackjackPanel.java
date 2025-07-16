package overworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import pokemon.CompoundIcon;
import pokemon.Player;
import puzzle.Puzzle;
import pokemon.CompoundIcon.Axis;

public class BlackjackPanel extends JPanel {

	private GamePanel gp;
	private Player p;
	private Card[] deck;
	
	private JLabel userCardIcons;
	private JLabel foeCardIcons;
	private JLabel deckIcon;
	private JButton hitButton;
	private JButton standButton;
	private JButton doubleButton;
	private JButton startButton;
	private JButton leaveButton;
	private JLabel coinText;
	private JLabel currentBetText;
	private JLabel winStreakText;
	private JLabel gamesWonText;
	private JButton statsButton;
	private JSpinner betSpinner;
	private SpinnerNumberModel spinnerModel;
	
	private int currentIndex;
	private Card[] userCards;
	private Card[] foeCards;
	private int bet;
	private int lastBet;
	
	private boolean gauntlet;
	private String currency;
	
	private Image backgroundImage;
	
	private static final int MAX_HAND_SIZE = 7;
	private static final int DECK_SIZE = 52;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public BlackjackPanel(GamePanel gp, boolean gauntlet) {
		this.gp = gp;
		this.gauntlet = gauntlet;
		currency = gauntlet ? "orbs" : "coins";
		p = gp.player.p;
		deck = new Card[DECK_SIZE];
		initializeDeck();
		shuffleDeck();
		
		userCards = new Card[MAX_HAND_SIZE];
		foeCards = new Card[MAX_HAND_SIZE];
		
		initializeFrame();
	}

	private void shuffleDeck() {
		Random random = new Random();
		for (int i = 0; i < DECK_SIZE; i++) {
			int swapIndex = random.nextInt(DECK_SIZE);
			Card old = deck[i];
			deck[i] = deck[swapIndex];
			deck[swapIndex] = old;
		}
	}

	private void initializeDeck() {
		for (int i = 0; i < DECK_SIZE; i++) {
			deck[i] = new Card(i, this);
		}
	}
	
	private void startGame() {
		if (p.blackjackStats[10] < p.coins) p.blackjackStats[10] = p.coins;
		if (getDeckSize() <= 20) {
			resetDeck();
			JOptionPane.showMessageDialog(null, "Deck reshuffled!");
		}
		hitButton.setVisible(true);
		standButton.setVisible(true);
		doubleButton.setVisible(true);
		startButton.setVisible(false);
		leaveButton.setVisible(false);
		statsButton.setVisible(false);
		currentBetText.setText("Bet: " + bet + " " + currency);
		p.addBetCurrency(gauntlet, -bet);
		coinText.setText(p.getBetCurrency(gauntlet) + " " + currency);
		p.blackjackStats[0]++;
		
		userCards[getHandSize(userCards)] = dealCard();
		foeCards[getHandSize(foeCards)] = dealCard();
		
		userCards[getHandSize(userCards)] = dealCard();
		Card foeCard2 = dealCard();
		foeCard2.setVisible(false);
		foeCards[getHandSize(foeCards)] = foeCard2;
		
		userCardIcons.setIcon(createHandIcon(userCards));
		foeCardIcons.setIcon(createHandIcon(foeCards));
		userCardIcons.setVisible(true);
		foeCardIcons.setVisible(true);
		
		// check for blackjacks
		boolean playerBJ = isBlackjack(userCards);
		boolean dealerBJ = isBlackjack(foeCards);
		boolean offeredInsurance = false;
		boolean insurance = false;
		int insuranceBet = bet / 2;
		
		if (!playerBJ && foeCards[0].getRank() == 1 && bet / 2 > 0) { // dealer shows ace
			offeredInsurance = true;
			p.blackjackStats[14]++;
			if (insuranceBet > 0 && p.getBetCurrency(gauntlet) >= insuranceBet) {
				int choice = JOptionPane.showConfirmDialog(
					null,
					"Dealer shows an Ace.\nWould you like insurance?\n(Costs half your bet)",
					"Insurance",
					JOptionPane.YES_NO_OPTION
				);
				if (choice == JOptionPane.YES_OPTION) {
					insurance = true;
					p.addBetCurrency(gauntlet, -insuranceBet);
					p.blackjackStats[15]++;
					p.blackjackStats[17] -= insuranceBet;
				}
			}
		}
		
		if (playerBJ || dealerBJ) {
			foeCards[1].setVisible(true);
			foeCardIcons.setIcon(createHandIcon(foeCards));
			
			if (playerBJ && dealerBJ) {
				JOptionPane.showMessageDialog(null, "Both you and the dealer have Blackjack! It's a push.");
				
				p.addBetCurrency(gauntlet, bet);
				p.blackjackStats[2]++;
				p.blackjackStats[5]++;
				endGame();
				return;
			} else if (playerBJ) {
				JOptionPane.showMessageDialog(null, "Blackjack! You win 3:2 payout!");
				winGame((int)(bet * 2.5));
				p.blackjackStats[5]++;
				endGame();
				return;
			} else {
				if (insurance) {
					int insuranceWin = insuranceBet * 2;
					JOptionPane.showMessageDialog(null, "Insurance pays out!");
					winGame(insuranceWin + bet);
					p.blackjackStats[16]++;
					p.blackjackStats[17] += insuranceWin;
					endGame();
					return;
				}
				JOptionPane.showMessageDialog(null, "Dealer has Blackjack, you lose.");
				loseGame();
				if (offeredInsurance) p.blackjackStats[18]++;
				endGame();
				return;
			}
		}
	}
	
	private void endGame() {
		hitButton.setVisible(false);
		standButton.setVisible(false);
		doubleButton.setVisible(false);
		startButton.setVisible(true);
		leaveButton.setVisible(true);
		statsButton.setVisible(!gauntlet);
		bet = 0;
		currentBetText.setText("Bet: -- " + currency);
		coinText.setText(p.getBetCurrency(gauntlet) + " " + currency);
		gamesWonText.setText(p.gamesWon + " games won");
		winStreakText.setText("Current Win Streak: " + p.winStreak);
		
		userCards = new Card[MAX_HAND_SIZE];
		foeCards = new Card[MAX_HAND_SIZE];
		userCardIcons.setVisible(false);
		foeCardIcons.setVisible(false);
		
		gp.saveGame(gp.player.p);
	}

	private void resetDeck() {
		shuffleDeck();
		currentIndex = 0;
	}
	
	private void initializeFrame() {
        setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
        setBounds(0, 0, gp.screenWidth, gp.screenHeight);
        setOpaque(false);
        setLayout(null);
        backgroundImage = getImage("/cards/blackjack");
        
		hitButton = new JButton("Hit");
		this.add(hitButton);
		hitButton.setVisible(false);
		hitButton.setBounds(275, 240, 100, 50);
		
		standButton = new JButton("Stand");
		this.add(standButton);
		standButton.setVisible(false);
		standButton.setBounds(385, 240, 100, 50);
		
		doubleButton = new JButton("Double");
		this.add(doubleButton);
		doubleButton.setVisible(false);
		doubleButton.setBounds(495, 240, 100, 50);
		
		leaveButton = new JButton("Leave");
		leaveButton.setBounds(5, 5, 75, 30);
		this.add(leaveButton);
		leaveButton.setVisible(true);
		
		startButton = new JButton("Start");
		startButton.setBounds(300, 200, 150, 60);
		this.add(startButton);
		startButton.setVisible(true);
		
		statsButton = new JButton("Stats");
		statsButton.setBounds(335, 5, 80, 30);
		this.add(statsButton);
		statsButton.setVisible(!gauntlet);
		
		JLabel rulesLabel = new JLabel(
	        "<html><div style='color:#AAAAAA;font-size:10px;'>" +
	        "• Min bet: 10 " + currency + "<br>" +
	        "• Blackjack pays 3:2<br>" +
	        "• Insurance pays 2:1<br>" +
	        "• Dealer stands on 17+</div></html>"
	    );
	    rulesLabel.setBounds(gp.screenWidth - 200, gp.screenHeight - 150, 180, 120);
	    rulesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
	    this.add(rulesLabel);
		
		currentBetText = new JLabel("Bet: -- " + currency);
		currentBetText.setBounds(600, 30, 150, 20);
		currentBetText.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(currentBetText);
		currentBetText.setVisible(true);
		currentBetText.setForeground(Color.WHITE);
		
		coinText = new JLabel("<html><strong>" + p.getBetCurrency(gauntlet) + "</strong> " + currency + "</html>");
		coinText.setBounds(600, 5, 150, 20);
		coinText.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(coinText);
		coinText.setVisible(true);
		coinText.setForeground(Color.WHITE);
		
		gamesWonText = new JLabel(p.gamesWon + " games won");
		gamesWonText.setBounds(5, 50, 150, 20);
		gamesWonText.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(gamesWonText);
		gamesWonText.setVisible(true);
		gamesWonText.setForeground(Color.WHITE);
		
		winStreakText = new JLabel("Current Win Streak: " + p.winStreak);
		winStreakText.setBounds(5, 75, 150, 20);
		winStreakText.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(winStreakText);
		winStreakText.setVisible(true);
		winStreakText.setForeground(Color.WHITE);
		
		userCardIcons = new JLabel();
		foeCardIcons = new JLabel();
		userCardIcons.setBounds(300, 325, 500, 200);
		this.add(userCardIcons);
		userCardIcons.setVisible(false);
		foeCardIcons.setBounds(300, 5, 500, 200);
		this.add(foeCardIcons);
		foeCardIcons.setVisible(false);
		
		deckIcon = new JLabel(getDeckSize() + "");
		deckIcon.setIcon(new ImageIcon(getImage("/cards/deck")));
		this.add(deckIcon);
		deckIcon.setVisible(true);
		deckIcon.setBounds(50, 150, 160, 190);
		deckIcon.setForeground(Color.WHITE);
		
		leaveButton.addActionListener(e -> {
			exitBlackjack();
		});
		
		hitButton.addActionListener(e -> {
			hit();
		});
		
		standButton.addActionListener(e -> {
			stand();
		});
		
		doubleButton.addActionListener(e -> {
			if (p.getBetCurrency(gauntlet) >= bet) {
				p.addBetCurrency(gauntlet, -bet);
				bet *= 2;
				
				currentBetText.setText("Bet: " + bet + " " + currency);
				coinText.setText(p.getBetCurrency(gauntlet) + " " + currency);
				
				p.blackjackStats[6]++;
				
				boolean good = hit();
				if (good) stand(true);
			} else {
				JOptionPane.showMessageDialog(this, "You don't have enough " + currency + "!");
			}
		});
		
		
		startButton.addActionListener(e -> {
			if (p.getBetCurrency(gauntlet) > Player.BET_INC) {
				int maxAllowed = p.getMaxBet(gauntlet);
				spinnerModel = new SpinnerNumberModel(
					Math.max(lastBet, Player.BET_INC),
					Player.BET_INC,
					maxAllowed,
					Player.BET_INC
				);
				
				betSpinner = new JSpinner(spinnerModel);
				JSpinner.NumberEditor editor = new JSpinner.NumberEditor(betSpinner, "#");
				betSpinner.setEditor(editor);
				
				betSpinner.addChangeListener(evt -> {
					int value = (int)betSpinner.getValue();
					if (value % Player.BET_INC != 0) {
						value = (value / Player.BET_INC) * Player.BET_INC;
						betSpinner.setValue(Math.max(Player.BET_INC, value));
					}
				});
				
				JPanel panel = new JPanel();
		        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		        
		        JLabel label = new JLabel("Bet amount (" + Player.BET_INC + "-" + maxAllowed + " " + currency + "):");
		        panel.add(label);
		        
		        panel.add(Box.createRigidArea(new Dimension(0, 5)));
		        
		        panel.add(betSpinner);
		        
		        // Create custom dialog instead of JOptionPane
		        JDialog dialog = new JDialog((JFrame)null, "Place Your Bet", true);
		        dialog.setContentPane(panel);
		        
		        // Add OK/Cancel buttons
		        JPanel buttonPanel = new JPanel();
		        JButton okButton = new JButton("OK");
		        JButton cancelButton = new JButton("Cancel");
		        
		        // Set OK button as default
		        dialog.getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(ev -> {
					dialog.dispose();
					bet = (int)betSpinner.getValue();
					lastBet = bet;
					
					if (!gp.player.p.flag[6][2]) {
    					int answer = JOptionPane.showOptionDialog(null,
    						"Would you like to save the game?\n(Won't show this message again:\nWill save every time)",
    				         "Save?",
    				         JOptionPane.YES_NO_OPTION,
    				         JOptionPane.QUESTION_MESSAGE,
    				         null, null, null);
    					if (answer == JOptionPane.YES_OPTION) {
    						gp.player.p.flag[6][2] = true;
    					} else {
    						return;
    					}
    				}
					startGame();
                    gp.saveGame(gp.player.p);
				});
				
				cancelButton.addActionListener(ev -> dialog.dispose());
				
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);
				panel.add(buttonPanel);
				
				dialog.addComponentListener(new ComponentAdapter() {
		            @Override
		            public void componentShown(ComponentEvent e) {
		                SwingUtilities.invokeLater(() -> {
		                    betSpinner.requestFocusInWindow();
		                    JComponent editor = betSpinner.getEditor();
		                    if (editor instanceof JSpinner.DefaultEditor) {
		                        JTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
		                        textField.selectAll();
		                    }
		                });
		            }
		        });
		        
		        dialog.pack();
		        dialog.setLocationRelativeTo(this);
		        dialog.setVisible(true);
		        
		    } else {
		        JOptionPane.showMessageDialog(this, "You don't have enough " + currency + "!");
		    }
		});
		
		statsButton.addActionListener(e -> {
			int games = p.blackjackStats[0];
			int win = p.blackjackStats[1];
			int push = p.blackjackStats[2];
			int lose = games - win - push;
			int busts = p.blackjackStats[3];
			int bustWins = p.blackjackStats[4];
			int blackjacks = p.blackjackStats[5];
			int doubles = p.blackjackStats[6];
			int doubleWins = p.blackjackStats[7];
			int coinWin = p.blackjackStats[8];
			int coinLost = p.blackjackStats[9];
			int netCoin = coinWin - coinLost;
			if (p.blackjackStats[10] < p.coins) p.blackjackStats[10] = p.coins;
			int maxCoin = p.blackjackStats[10];
			int winStreak = p.blackjackStats[11];
			int loseStreak = p.blackjackStats[12];
			int highLoseStreak = p.blackjackStats[13];
			int insuranceOffered = p.blackjackStats[14];
			int insuranceTaken = p.blackjackStats[15];
			int insuranceDeclined = insuranceOffered - insuranceTaken;
			int missedBJ = p.blackjackStats[18];
			int correctDeclines = insuranceDeclined - missedBJ;
			int insuranceWon = p.blackjackStats[16];
			int insuranceProfit = p.blackjackStats[17];
			
			String stats = "Blackjack Stats:\n\n";
			stats += "Total games played: " + games + "\n\n";
			stats += "Total wins: " + win + ", Win %: " + String.format("%.2f\n", win * 100.0 / games);
			stats += "Total losses: " + lose + ", Lose %: " + String.format("%.2f\n", lose * 100.0 / games);
			stats += "Total pushes: " + push + ", Push %: " + String.format("%.2f\n\n", push * 100.0 / games);
			
			stats += "Win streak: " + p.winStreak + "\n";
			stats += "Highest win streak: " + winStreak + "\n";
			stats += "Lose streak: " + loseStreak + "\n";
			stats += "Highest lose streak: " + highLoseStreak + "\n\n";
			
			stats += "Total busts: " + busts + "\n";
			stats += "Percentage of losses that are busts: " + String.format("%.2f\n", busts * 100.0 / lose);
			stats += "Bust %: " + String.format("%.2f\n", busts * 100.0 / games);
			stats += "Percentage of wins on Dealer busts: " + String.format("%.2f\n\n", bustWins * 100.0 / win);
			
			stats += "Total blackjacks: " + blackjacks + "\n";
			stats += "Blackjack %: " + String.format("%.2f\n\n", blackjacks * 100.0 / games);
			
			stats += "Total doubles: " + doubles + "\n";
			stats += "Double %: " + String.format("%.2f\n", doubles * 100.0 / games);
			stats += "Total double wins: " + doubleWins + "\n";
			stats += "Double win %: " + String.format("%.2f\n\n", doubleWins * 100.0 / doubles);
			
			stats += "Total coins won: " + coinWin + "\n";
			stats += "Total coins lost: " + coinLost + "\n";
			stats += "Net coins: " + (netCoin >= 0 ? "+" + netCoin : netCoin) + "\n";
			stats += "Highest wallet amount: " + maxCoin + "\n\n";
			
			stats += "Insurance Times Offered: " + insuranceOffered + "\n";
			stats += "Insurance Take Rate: " + String.format("%.1f%%\n", (insuranceTaken * 100.0 / insuranceOffered));
			stats += "Insurance Win Rate: " + String.format("%.1f%%\n", (insuranceWon * 100.0 / insuranceTaken));
			stats += "Insurance Net Profit: " + (insuranceProfit >= 0 ? "+" : "") + insuranceProfit + " " + currency + "\n\n";
			
			stats += "Insurance Times Declined: " + insuranceDeclined + "\n";
			stats += "Missed BJ opporunities: " + missedBJ + " (" + 
		               String.format("%.1f%%)\n", (missedBJ * 100.0 / insuranceDeclined));
			stats += "Correct declines: " + correctDeclines + " (" +
					String.format("%.1f%%)\n", (correctDeclines * 100.0 / insuranceDeclined));
			
			JTextArea textArea = new JTextArea(stats);
		    textArea.setEditable(false);
		    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		    textArea.setLineWrap(true);
		    textArea.setWrapStyleWord(true);
		    
		    // Create a JScrollPane with the text area
		    JScrollPane scrollPane = new JScrollPane(textArea);
		    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		    scrollPane.setPreferredSize(new Dimension(500, 400));
		    
		    // Set unit increment to 16 pixels
		    JScrollBar vertical = scrollPane.getVerticalScrollBar();
		    vertical.setUnitIncrement(16);
		    
		    // Show in a JOptionPane
		    JOptionPane.showMessageDialog(
		        null,
		        scrollPane,
		        "Blackjack Statistics",
		        JOptionPane.PLAIN_MESSAGE
		    );
		});
	}
		
	private boolean hit() {
		int handSize = getHandSize(userCards);
		if (handSize < MAX_HAND_SIZE) {
			doubleButton.setVisible(false);
			userCards[handSize] = dealCard();
			userCardIcons.setIcon(createHandIcon(userCards));
			int handTotal = getHandTotal(userCards);
			if (handTotal > 21) {
				JOptionPane.showMessageDialog(null, "Player busts with a hand total of " + handTotal + "!");
				JOptionPane.showMessageDialog(null, "Dealer won " + bet * 2 + " " + currency + "!");
				p.blackjackStats[3]++;
				loseGame();
				endGame();
				return false;
			}
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Can't hold any more cards!");
			return false;
		}
	}
	
	private void stand() {
		stand(false);
	}
	
	private void stand(boolean dbl) {
		int playerTotal = getHandTotal(userCards);
		int dealerTotal = getHandTotal(foeCards);
		int handSize = getHandSize(foeCards);
		foeCards[1].setVisible(true);
		foeCardIcons.setIcon(createHandIcon(foeCards));
		while (dealerTotal < 17 && handSize < MAX_HAND_SIZE) {
			Card card = dealCard();
			foeCards[handSize] = card;
			foeCardIcons.setIcon(createHandIcon(foeCards));
			JOptionPane.showMessageDialog(null, "Dealer hit and was dealt a \n" + card.getRankString() + " of " + card.getSuitString() + "!");
			dealerTotal = getHandTotal(foeCards);
			handSize = getHandSize(foeCards);
		}
		if (dealerTotal <= 21) {
			JOptionPane.showMessageDialog(null, "Dealer stood and had a hand total of " + dealerTotal + "!\nPlayer stood and had a hand total of " + playerTotal + "!");
			if (playerTotal > dealerTotal) {
				winGame(bet * 2);
				if (dbl) p.blackjackStats[7]++;
			} else if (playerTotal == dealerTotal) {
				JOptionPane.showMessageDialog(null, "It's a push. Bet was returned.");
				p.addBetCurrency(gauntlet, bet);
				p.blackjackStats[2]++;
				if (dbl) p.blackjackStats[7]++;
			} else {
				JOptionPane.showMessageDialog(null, "Dealer won " + bet * 2 + " " + currency + "!");
				loseGame();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Dealer busts with a hand total of " + dealerTotal + "!");
			p.blackjackStats[4]++;
			winGame(bet * 2);
		}
		endGame();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null);
	}
	
	private int getDeckSize() {
		return deck.length - currentIndex;
	}
	
	private int getHandSize(Card[] hand) {
		for (int i = 0; i < hand.length; i++) {
	        if (hand[i] == null) {
	            return i;
	        }
	    }
	    return hand.length;
	}

	private void winGame(int amt) {
		JOptionPane.showMessageDialog(this, "You won " + amt + " " + currency + "!");
		p.addBetCurrency(gauntlet, amt);
		p.winStreak++;
		p.gamesWon++;
		p.blackjackStats[1]++;
		p.blackjackStats[8] += amt / 2;
		if (p.coins > p.blackjackStats[10]) p.blackjackStats[10] = p.coins;
		if (p.winStreak > p.blackjackStats[11]) p.blackjackStats[11] = p.winStreak;
		p.blackjackStats[12] = 0;
	}
	
	private void loseGame() {
		p.winStreak = 0;
		p.blackjackStats[9] += bet;
		p.blackjackStats[12]++;
		if (p.blackjackStats[12] > p.blackjackStats[13]) p.blackjackStats[13] = p.blackjackStats[12];
	}
	
	private int getHandTotal(Card[] hand) {
	    int total = 0;
	    int numAces = 0;

	    for (Card card : hand) {
	        if (card != null) {
	            int rank = card.getRank();

	            // For face cards (Jack, Queen, King)
	            if (rank >= 11 && rank <= 13) {
	                total += 10;
	            } else if (rank == 1) { // Ace
	                total += 11; // Initially count Ace as 11
	                numAces++;
	            } else {
	                total += rank;
	            }
	        }
	    }

	    // Adjust total if it exceeds 21 and there are Aces
	    while (total > 21 && numAces > 0) {
	        total -= 10; // Change one Ace from 11 to 1
	        numAces--;
	    }

	    return total;
	}
	
	private Card dealCard() {
		Card result = deck[currentIndex++];
		deckIcon.setText(getDeckSize() + "");
		result.setVisible(true);
		return result;
	}


	private class Card {
		int rank;
		int suit;
		BufferedImage image;
		boolean visible;
		BlackjackPanel bjp;
		
		public Card(int value, BlackjackPanel bjp) {
			rank = (value % 13) + 1;
			suit = (value % 4) + 1;
			visible = true;
			
			this.image = bjp.getImage("/cards/" + rank + "_" + suit);
			
			this.bjp = bjp;
		}

		public int getRank() {
			return rank;
		}

		@SuppressWarnings("unused")
		public int getSuit() {
			return suit;
		}

		public BufferedImage getImage() {
			return image;
		}
		
		public void setVisible(boolean visible) {
			this.visible = visible;
			if (this.visible) {
				this.image = bjp.getImage("/cards/" + rank + "_" + suit);
			} else {
				this.image = bjp.getImage("/cards/hidden");
			}
		}
		
		public String getRankString() {
			switch(rank) {
			case 1:
				return "Ace";
			case 2:
				return "Two";
			case 3:
				return "Three";
			case 4:
				return "Four";
			case 5:
				return "Five";
			case 6:
				return "Six";
			case 7:
				return "Seven";
			case 8:
				return "Eight";
			case 9:
				return "Nine";
			case 10:
				return "Ten";
			case 11:
				return "Jack";
			case 12:
				return "Queen";
			case 13:
				return "King";
			default:
				return "N/A";
			}
		}
		
		public String getSuitString() {
			switch(suit) {
			case 1:
				return "Spades";
			case 2:
				return "Clubs";
			case 3:
				return "Hearts";
			case 4:
				return "Diamonds";
			default:
				return "N/A";
			}
		}
		
	}
	
	private boolean isBlackjack(Card[] hand) {
		return getHandSize(hand) == 2 && getHandTotal(hand) == 21;
	}
	
	public BufferedImage getImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public Icon createHandIcon(Card[] hand) {
		Icon currentIcon = null;
		int handLength = getHandSize(hand);
		if (handLength > 0) currentIcon = new ImageIcon(hand[0].getImage());
		for (int i = 1; i < handLength; i++) {
			CompoundIcon icon = new CompoundIcon(Axis.X_AXIS, -100, CompoundIcon.RIGHT, CompoundIcon.CENTER, currentIcon, new ImageIcon(hand[i].getImage()));
			currentIcon = icon;
		}
    	return currentIcon;
	}
	
	private void exitBlackjack() {
		Main.window.remove(this);
		Main.window.add(gp);
		
		if (gauntlet) {
			Puzzle puzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
			puzzle.update(p.getBetCurrency(gauntlet));
		}
		
		gp.requestFocusInWindow();

	    // Repaint the JFrame to reflect the changes
	    Main.window.revalidate();
	    Main.window.repaint();
		
	}
}
