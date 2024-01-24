package Overworld;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import Swing.Player;

public class BlackjackPanel extends JPanel {

	private GamePanel gp;
	private Player p;
	private Card[] deck;
	
	private JLabel[] userCardIcons;
	private JLabel[] foeCardIcons;
	private JLabel deckIcon;
	private JButton hitButton;
	private JButton standButton;
	private JButton startButton;
	private JButton leaveButton;
	private JLabel coinText;
	private JLabel currentBetText;
	
	private boolean inGame;
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
		currentBetText.setText("Bet: " + bet + " coins");
		p.coins -= bet;
		coinText.setText(p.coins + " coins");
	}
	
	private void endGame() {
		hitButton.setVisible(false);
		standButton.setVisible(false);
		startButton.setVisible(true);
		currentBetText.setText("Bet: -- coins");
		coinText.setText(p.coins + " coins");
	}
	
	private void resetDeck() {
		shuffleDeck();
		currentIndex = 0;
	}
	
	private void initializeFrame() {
		this.setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
		hitButton = new JButton("Hit");
		standButton = new JButton("Stand");
		leaveButton = new JButton("Leave");
		startButton = new JButton("Start");
		currentBetText = new JLabel("Bet: -- coins");
		coinText = new JLabel(p.coins + " coins");
		userCardIcons = new JLabel[5];
		foeCardIcons = new JLabel[5];
		deckIcon = new JLabel(getDeckSize() + "");
		deckIcon.setIcon(new ImageIcon("/cards/deck"));
		
		leaveButton.addActionListener(e -> {
			SwingUtilities.getWindowAncestor(this).dispose();
			gp.keyH.resume();
		});
		leaveButton.setBounds(null);
		this.add(leaveButton);
		
		startButton.addActionListener(e -> {
			if (p.coins > 0) {
		        // Input a bet
		        String betInput = JOptionPane.showInputDialog(this, "Enter your bet (between 10 and " + Math.min(p.coins, MAX_BET) + " coins):");

		        // Check if the user clicked cancel or entered an empty string
		        if (betInput != null && !betInput.trim().isEmpty()) {
		            try {
		                int bet = Integer.parseInt(betInput);

		                // Check if the bet is within the allowed range
		                if (bet >= 10 && bet <= Math.min(p.coins, MAX_BET)) {
		                    startGame(bet);
		                } else {
		                    JOptionPane.showMessageDialog(this, "Invalid bet. Please enter a value between 10 and " + Math.min(p.coins, MAX_BET) + ".");
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

	private void winGame(int amt) {
		JOptionPane.showMessageDialog(this, "You won " + amt + " coins!");
		p.coins += amt;
	}
	
	private int getHandTotal(Card[] hand) {
		return 0;
	}
	
	private Card dealCard() {
		return deck[currentIndex++];
	}


	private class Card {
		int rank;
		int suit;
		BufferedImage image;
		
		public Card(int value, BlackjackPanel bjp) {
			rank = value % 13;
			suit = value % 4;
			
			
			
			this.image = bjp.getImage("/cards/" + rank + "_" + suit);
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
}
