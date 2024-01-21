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
	
	private JLabel[] userCards;
	private JLabel[] foeCards;
	private JLabel deckIcon;
	private JButton hitButton;
	private JButton standButton;
	private JButton leaveButton;
	private JLabel coinText;
	private JLabel currentBetText;
	
	private boolean inGame;
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
		
		this.setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
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
			deck[i] = new Card(i);
		}
		
	}


	private class Card {
		int rank;
		int suit;
		BufferedImage image;
		
		public Card(int value) {
			rank = value % 13;
			suit = value % 4;
			
			BufferedImage image = null;
			String imageName = rank + "_" + suit;
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/cards/" + imageName + ".png"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.image = image;
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
}
