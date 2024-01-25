package Overworld;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import Swing.CompoundIcon;
import Swing.Player;
import Swing.CompoundIcon.Axis;

public class BlackjackPanel extends JPanel {

	private GamePanel gp;
	private Player p;
	private Card[] deck;
	
	private JLabel userCardIcons;
	private JLabel foeCardIcons;
	private JLabel deckIcon;
	private JButton hitButton;
	private JButton standButton;
	private JButton startButton;
	private JButton leaveButton;
	private JLabel coinText;
	private JLabel currentBetText;
	
	private int currentIndex;
	private Card[] userCards;
	private Card[] foeCards;
	
	private static final int MAX_BET = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BlackjackPanel(GamePanel gp) {
		this.gp = gp;
		p = gp.player.p;
		deck = new Card[52];
		initializeDeck();
		shuffleDeck();
		
		userCards = new Card[5];
		foeCards = new Card[5];
		
		initializeFrame();
	}

	private void shuffleDeck() {
		Random random = new Random();
		for (int i = 0; i < 52; i++) {
			int swapIndex = random.nextInt(52);
			Card old = deck[i];
			deck[i] = deck[swapIndex];
			deck[swapIndex] = old;
		}
	}

	private void initializeDeck() {
		for (int i = 0; i < 52; i++) {
			deck[i] = new Card(i, this);
		}
	}
	
	private void startGame(int bet) {
		hitButton.setVisible(true);
		standButton.setVisible(true);
		startButton.setVisible(false);
		leaveButton.setVisible(false);
		currentBetText.setText("Bet: " + bet + " coins");
		p.coins -= bet;
		coinText.setText(p.coins + " coins");
		
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
	}
	
	private void endGame() {
		hitButton.setVisible(false);
		standButton.setVisible(false);
		startButton.setVisible(true);
		leaveButton.setVisible(true);
		currentBetText.setText("Bet: -- coins");
		coinText.setText(p.coins + " coins");
		
		userCards = new Card[5];
		foeCards = new Card[5];
		userCardIcons.setVisible(false);
		foeCardIcons.setVisible(false);
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
        
		hitButton = new JButton("Hit");
		this.add(hitButton);
		hitButton.setVisible(false);
		hitButton.setBounds(275, 240, 100, 50);
		
		standButton = new JButton("Stand");
		this.add(standButton);
		standButton.setVisible(false);
		standButton.setBounds(385, 240, 100, 50);
		
		leaveButton = new JButton("Leave");
		leaveButton.setBounds(5, 5, 75, 30);
		this.add(leaveButton);
		leaveButton.setVisible(true);
		
		startButton = new JButton("Start");
		startButton.setBounds(300, 200, 150, 60);
		this.add(startButton);
		startButton.setVisible(true);
		
		currentBetText = new JLabel("Bet: -- coins");
		currentBetText.setBounds(600, 30, 150, 20);
		currentBetText.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(currentBetText);
		currentBetText.setVisible(true);
		
		coinText = new JLabel("<html><strong>" + p.coins + "</strong> coins</html>");
		coinText.setBounds(600, 5, 150, 20);
		coinText.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(coinText);
		coinText.setVisible(true);
		
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
		
		leaveButton.addActionListener(e -> {
			Main.window.remove(this);
			Main.window.add(gp);
			
			gp.requestFocusInWindow();

		    // Repaint the JFrame to reflect the changes
		    Main.window.revalidate();
		    Main.window.repaint();
			
			gp.keyH.resume();
		});
		
		standButton.addActionListener(e -> {
			int dealerTotal = getHandTotal(foeCards);
			int handSize = getHandSize(foeCards);
			foeCards[1].setVisible(true);
			foeCardIcons.setIcon(createHandIcon(foeCards));
			while (dealerTotal < 16 && handSize < 5) {
				Card card = dealCard();
				foeCards[handSize] = card;
				foeCardIcons.setIcon(createHandIcon(foeCards));
				JOptionPane.showMessageDialog(null, "Dealer hit and was dealt a \n" + card.getRankString() + " of " + card.getSuitString() + "!");
				dealerTotal = getHandTotal(foeCards);
				handSize = getHandSize(foeCards);
			}
			endGame();
		});
		
		
		startButton.addActionListener(e -> {
			if (p.coins > 0) {
		        // Input a bet
		        String betInput = JOptionPane.showInputDialog(this, "Enter your bet (between 1 and " + Math.min(p.coins, MAX_BET) + " coins):");

		        // Check if the user clicked cancel or entered an empty string
		        if (betInput != null && !betInput.trim().isEmpty()) {
		            try {
		                int bet = Integer.parseInt(betInput);

		                // Check if the bet is within the allowed range
		                if (bet >= 1 && bet <= Math.min(p.coins, MAX_BET)) {
		                    startGame(bet);
		                } else {
		                    JOptionPane.showMessageDialog(this, "Invalid bet. Please enter a value between 1 and " + Math.min(p.coins, MAX_BET) + ".");
		                }
		            } catch (NumberFormatException ex) {
		                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
		            }
		        }
		    } else {
		        JOptionPane.showMessageDialog(this, "You don't have enough coins!");
		    }
		});
	}
	
	private int getDeckSize() {
		return deck.length - currentIndex;
	}
	
	private int getHandSize(Card[] hand) {
		int indexOfNull = Arrays.asList(hand).indexOf(null);
	    return (indexOfNull != -1) ? indexOfNull : hand.length;
	}

	private void winGame(int amt) {
		JOptionPane.showMessageDialog(this, "You won " + amt + " coins!");
		p.coins += amt;
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
	            } else if (rank == 1) {  // Ace
	                numAces++;
	            } else {
	                total += rank;
	            }
	        }
	    }

	    // Handle Aces
	    for (int i = 0; i < numAces; i++) {
	        if (total + 11 <= 21) {
	            total += 11;
	        } else {
	            total += 1;
	        }
	    }

	    return total;
	}
	
	private Card dealCard() {
		Card result = deck[currentIndex++];
		deckIcon.setText(getDeckSize() + "");
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
}
