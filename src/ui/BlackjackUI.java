package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import overworld.*;
import pokemon.Player;
import puzzle.Puzzle;
import util.ToolTip;

public class BlackjackUI extends AbstractUI {
	private Player p;
	private Card[] deck;
	
	private int currentIndex;
	private ArrayList<Hand> playerHands;
	private int currentHandIndex;
	private Hand dealerHand;
	private int bet;
	private int lastBet;
	
	private boolean gauntlet;
	private String currency;
	
	private Image backgroundImage;
	private BufferedImage deckImg;
	
	// Game states
	private static final int STATE_BETTING = 0;
	private static final int STATE_INITIAL_DEAL = 1;
	private static final int STATE_INSURANCE = 2;
	private static final int STATE_PLAYER_TURN = 3;
	private static final int STATE_DEALER_TURN = 4;
	private static final int STATE_GAME_OVER = 5;
	
	private int gameState;
	
	// Menu states
	private int menuNum;
	private static final int MENU_HIT = 0;
	private static final int MENU_STAND = 1;
	private static final int MENU_DOUBLE = 2;
	private static final int MENU_SPLIT = 3;
	
	// Animation
	private int animationCounter;
	private boolean waitingForInput;
	private String messageText;
	private int messageTimer;
	
	private static final int MAX_HAND_SIZE = 7;
	private static final int DECK_SIZE = 104; // 2 decks
	
	// Layout constants
	private int cardWidth = 71;
	private int cardHeight = 96;
	private int cardSpacing = -50;
	
	public BlackjackUI(GamePanel gp, boolean gauntlet) {
		this.gp = gp;
		this.gauntlet = gauntlet;
		this.currency = gauntlet ? "orbs" : "coins";
		this.p = gp.player.p;
		deckImg = setup("/cards/deck", 1.0);
		
		deck = new Card[DECK_SIZE];
		initializeDeck();
		shuffleDeck();
		
		playerHands = new ArrayList<>();
		dealerHand = new Hand();
		
		gameState = STATE_BETTING;
		menuNum = 0;
		bet = 10;
		
		this.textColor = Color.WHITE;
		
		loadBackground();
	}

	private void loadBackground() {
		try {
			backgroundImage = ImageIO.read(getClass().getResourceAsStream("/cards/blackjack.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			deck[i] = new Card(i % 52, this);
		}
	}
	
	public void update() {
		if (messageTimer > 0) {
			messageTimer--;
			return;
		}
		
		switch (gameState) {
		case STATE_BETTING:
			updateBetting();
			break;
		case STATE_INITIAL_DEAL:
			updateInitialDeal();
			break;
		case STATE_INSURANCE:
			updateInsurance();
			break;
		case STATE_PLAYER_TURN:
			updatePlayerTurn();
			break;
		case STATE_DEALER_TURN:
			updateDealerTurn();
			break;
		case STATE_GAME_OVER:
			updateGameOver();
			break;
		}
		
		animationCounter++;
	}
	
	private void updateBetting() {
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			if (p.getBetCurrency(gauntlet) >= Player.BET_INC) {
				gp.playSFX(Sound.S_MENU_CON);
				placeBet();
			} else {
				gp.playSFX(Sound.S_MENU_CAN);
				showMessage("You don't have enough " + currency + "!");
			}
		}
		
		if (gp.keyH.leftPressed && bet > Player.BET_INC) {
			gp.keyH.leftPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			bet -= Player.BET_INC;
			if (bet < Player.BET_INC) bet = Player.BET_INC;
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			int maxBet = p.getMaxBet(gauntlet);
			if (bet < maxBet) {
				gp.playSFX(Sound.S_MENU_1);
				bet += Player.BET_INC;
				if (bet > maxBet) bet = maxBet;
			}
		}
		
		if (gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			exitBlackjack();
		}
	}
	
	private void placeBet() {
		if (bet < Player.BET_INC) bet = Player.BET_INC;
		lastBet = bet;
		
		if (p.blackjackStats[10] < p.coins) p.blackjackStats[10] = p.coins;
		if (getDeckSize() <= 20) {
			resetDeck();
			showMessage("Deck reshuffled!");
		}
		
		p.addBetCurrency(gauntlet, -bet);
		p.blackjackStats[0]++;
		
		playerHands.clear();
		playerHands.add(new Hand());
		currentHandIndex = 0;
		dealerHand = new Hand();
		
		gameState = STATE_INITIAL_DEAL;
		animationCounter = 0;
		
		if (!gp.player.p.flag[6][2]) {
			gp.player.p.flag[6][2] = true;
		}
		gp.saveGame(gp.player.p, true);
	}
	
	private void updateInitialDeal() {
		if (animationCounter == 10) {
			playerHands.get(0).addCard(dealCard());
		} else if (animationCounter == 20) {
			dealerHand.addCard(dealCard());
		} else if (animationCounter == 30) {
			playerHands.get(0).addCard(dealCard());
		} else if (animationCounter == 40) {
			Card hiddenCard = dealCard();
			hiddenCard.setVisible(false);
			dealerHand.addCard(hiddenCard);
			
			// Check for insurance
			if (!isBlackjack(playerHands.get(0)) && dealerHand.cards.get(0).getRank() == 1 && bet / 2 > 0) {
				gameState = STATE_INSURANCE;
				menuNum = 0;
			} else {
				checkInitialBlackjacks();
			}
		}
	}
	
	private void updateInsurance() {
		if (gp.keyH.leftPressed || gp.keyH.rightPressed) {
			gp.keyH.leftPressed = false;
			gp.keyH.rightPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			menuNum = 1 - menuNum;
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_CON);
			
			int insuranceBet = bet / 2;
			if (menuNum == 1 && p.getBetCurrency(gauntlet) >= insuranceBet) {
				// Take insurance
				p.addBetCurrency(gauntlet, -insuranceBet);
				p.blackjackStats[15]++;
				p.blackjackStats[17] -= insuranceBet;
				playerHands.get(0).insurance = insuranceBet;
			}
			p.blackjackStats[14]++;
			
			checkInitialBlackjacks();
		}
	}
	
	private void checkInitialBlackjacks() {
		Hand playerHand = playerHands.get(0);
		boolean playerBJ = isBlackjack(playerHand);
		boolean dealerBJ = isBlackjack(dealerHand);
		
		if (playerBJ || dealerBJ) {
			dealerHand.cards.get(1).setVisible(true);
			
			if (playerBJ && dealerBJ) {
				showMessage("Both you and the dealer have Blackjack! It's a push.");
				p.addBetCurrency(gauntlet, bet);
				p.blackjackStats[2]++;
				p.blackjackStats[5]++;
				endGame();
				return;
			} else if (playerBJ) {
				showMessage("Blackjack! You win 3:2 payout!");
				winHand(playerHand, (int)(bet * 2.5));
				p.blackjackStats[5]++;
				endGame();
				return;
			} else {
				// Dealer blackjack
				if (playerHand.insurance > 0) {
					int insuranceWin = playerHand.insurance * 2;
					showMessage("Insurance pays out!");
					winHand(playerHand, insuranceWin + bet);
					p.blackjackStats[16]++;
					p.blackjackStats[17] += insuranceWin;
				} else {
					showMessage("Dealer has Blackjack, you lose.");
					loseHand(playerHand);
					if (p.blackjackStats[14] > p.blackjackStats[15]) {
						p.blackjackStats[18]++;
					}
				}
				endGame();
				return;
			}
		}
		
		// No blackjacks, start player turn
		gameState = STATE_PLAYER_TURN;
		menuNum = 0;
		waitingForInput = true;
	}
	
	private void updatePlayerTurn() {
		Hand currentHand = playerHands.get(currentHandIndex);
		
		if (currentHand.isStanding || currentHand.isBusted) {
			moveToNextHand();
			return;
		}
		
		if (!waitingForInput) return;
		
		int maxOption = getMaxMenuOption(currentHand);
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			menuNum--;
			if (menuNum < 0) menuNum = maxOption;
			while (!isMenuOptionValid(currentHand, menuNum)) {
				menuNum--;
				if (menuNum < 0) menuNum = maxOption;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			menuNum++;
			if (menuNum > maxOption) menuNum = 0;
			while (!isMenuOptionValid(currentHand, menuNum)) {
				menuNum++;
				if (menuNum > maxOption) menuNum = 0;
			}
		}
		
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_CON);
			handlePlayerAction(menuNum);
		}
	}
	
	private int getMaxMenuOption(Hand hand) {
		if (canSplit(hand)) return MENU_SPLIT;
		if (canDouble(hand)) return MENU_DOUBLE;
		return MENU_STAND;
	}
	
	private boolean isMenuOptionValid(Hand hand, int option) {
		switch (option) {
		case MENU_HIT:
		case MENU_STAND:
			return true;
		case MENU_DOUBLE:
			return canDouble(hand);
		case MENU_SPLIT:
			return canSplit(hand);
		default:
			return false;
		}
	}
	
	private boolean canDouble(Hand hand) {
		return hand.cards.size() == 2 && p.getBetCurrency(gauntlet) >= hand.bet;
	}
	
	private boolean canSplit(Hand hand) {
		if (hand.cards.size() != 2) return false;
		if (p.getBetCurrency(gauntlet) < hand.bet) return false;
		return hand.cards.get(0).getRank() == hand.cards.get(1).getRank();
	}
	
	private void handlePlayerAction(int action) {
		Hand currentHand = playerHands.get(currentHandIndex);
		waitingForInput = false;
		
		switch (action) {
		case MENU_HIT:
			hit(currentHand);
			break;
		case MENU_STAND:
			stand(currentHand);
			break;
		case MENU_DOUBLE:
			doubleDown(currentHand);
			break;
		case MENU_SPLIT:
			split(currentHand);
			break;
		}
	}
	
	private void hit(Hand hand) {
		if (hand.cards.size() < MAX_HAND_SIZE) {
			hand.addCard(dealCard());
			int handTotal = getHandTotal(hand);
			
			if (handTotal > 21) {
				showMessage("Bust with " + handTotal + "!");
				hand.isBusted = true;
				p.blackjackStats[3]++;
				loseHand(hand);
				moveToNextHand();
			} else {
				waitingForInput = true;
				menuNum = 0;
			}
		}
	}
	
	private void stand(Hand hand) {
		hand.isStanding = true;
		moveToNextHand();
	}
	
	private void doubleDown(Hand hand) {
		p.addBetCurrency(gauntlet, -hand.bet);
		hand.bet *= 2;
		hand.isDoubled = true;
		p.blackjackStats[6]++;
		
		hand.addCard(dealCard());
		int handTotal = getHandTotal(hand);
		
		if (handTotal > 21) {
			showMessage("Bust with " + handTotal + "!");
			hand.isBusted = true;
			p.blackjackStats[3]++;
			loseHand(hand);
		} else {
			hand.isStanding = true;
		}
		
		moveToNextHand();
	}
	
	private void split(Hand hand) {
		// Take second bet
		p.addBetCurrency(gauntlet, -hand.bet);
		
		// Create new hand with second card
		Hand newHand = new Hand();
		newHand.bet = hand.bet;
		newHand.addCard(hand.cards.remove(1));
		
		// Deal one card to the first hand
		hand.addCard(dealCard());
		
		// Insert new hand after current hand
		playerHands.add(currentHandIndex + 1, newHand);
		
		waitingForInput = true;
		menuNum = 0;
	}
	
	private void moveToNextHand() {
		currentHandIndex++;
		
		if (currentHandIndex >= playerHands.size()) {
			// All hands complete, move to dealer turn
			gameState = STATE_DEALER_TURN;
			animationCounter = 0;
			
			// Check if all hands busted
			boolean allBusted = true;
			for (Hand hand : playerHands) {
				if (!hand.isBusted) {
					allBusted = false;
					break;
				}
			}
			
			if (allBusted) {
				showMessage("You busted!");
				endGame();
			}
		} else {
			playerHands.get(currentHandIndex).addCard(dealCard());
			menuNum = 0;
			waitingForInput = true;
		}
	}
	
	private void updateDealerTurn() {
		if (animationCounter == 1) {
			dealerHand.cards.get(1).setVisible(true);
		}
		
		if (animationCounter > 0 && animationCounter % 30 == 0) {
			int dealerTotal = getHandTotal(dealerHand);
			
			if ((dealerTotal < 17 || isSoft17(dealerHand)) && dealerHand.cards.size() < MAX_HAND_SIZE) {
				Card newCard = dealCard();
				dealerHand.addCard(newCard);
				showMessage("Dealer drew " + newCard.getRankString() + " of " + newCard.getSuitString());
			} else {
				resolveHands();
			}
		}
	}
	
	private void resolveHands() {
		int dealerTotal = getHandTotal(dealerHand);
		boolean dealerBusted = dealerTotal > 21;
		
		if (dealerBusted) {
			p.blackjackStats[4]++;
		}
		
		for (Hand hand : playerHands) {
			if (hand.isBusted) continue;
			
			int playerTotal = getHandTotal(hand);
			
			if (dealerBusted) {
				winHand(hand, hand.bet * 2);
				if (hand.isDoubled) p.blackjackStats[7]++;
			} else if (playerTotal > dealerTotal) {
				winHand(hand, hand.bet * 2);
				if (hand.isDoubled) p.blackjackStats[7]++;
			} else if (playerTotal == dealerTotal) {
				p.addBetCurrency(gauntlet, hand.bet);
				p.blackjackStats[2]++;
				if (hand.isDoubled) p.blackjackStats[7]++;
			} else {
				loseHand(hand);
			}
		}
		
		String resultMsg = dealerBusted ? "Dealer busts with " + dealerTotal + "!" : 
			"Dealer stands with " + dealerTotal;
		showMessage(resultMsg);
		
		endGame();
	}
	
	private void updateGameOver() {
		if (gp.keyH.wPressed || gp.keyH.sPressed || gp.keyH.dPressed) {
			gp.keyH.wPressed = false;
			gp.keyH.sPressed = false;
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			
			gameState = STATE_BETTING;
			bet = lastBet;
			menuNum = 0;
		}
	}
	
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw background
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
		} else {
			g2.setColor(new Color(0, 100, 0));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		}
		
		// Draw game info
		drawGameInfo();
		
		// Draw deck
		drawDeck();
		
		// Draw dealer hand
		drawDealerHand();
		
		// Draw player hands
		drawPlayerHands();
		
		// Draw UI based on state
		switch (gameState) {
		case STATE_BETTING:
			drawBettingUI();
			break;
		case STATE_INSURANCE:
			drawInsuranceUI();
			break;
		case STATE_PLAYER_TURN:
			drawPlayerTurnUI();
			break;
		case STATE_GAME_OVER:
			drawGameOverUI();
			break;
		}
		
		// Draw message
		if (messageTimer > 0) {
			drawMessage();
		}
		
		// Draw controls hint
		if (!gp.keyH.shiftPressed && gameState == STATE_BETTING) {
			drawControlsHint();
		}
		
		update();
		
		// Draw tooltips
		drawKeyStrokes();
	}
	
	private void drawGameInfo() {
		g2.setFont(gp.marumonica.deriveFont(18F));
		
		// Currency
		int infoX = gp.tileSize / 2;
		int infoY = gp.tileSize / 2;
		
		drawOutlinedText(p.getBetCurrency(gauntlet) + " " + currency, infoX, infoY, Color.WHITE, Color.BLACK);
		
		// Win streak
		infoY += gp.tileSize / 2;
		drawOutlinedText("Win Streak: " + p.winStreak, infoX, infoY, Color.WHITE, Color.BLACK);
		
		// Games won
		infoY += gp.tileSize / 2;
		drawOutlinedText("Games Won: " + p.gamesWon, infoX, infoY, Color.WHITE, Color.BLACK);
		
		// Current bet
		if (gameState != STATE_BETTING) {
			infoY += gp.tileSize / 2;
			int totalBet = 0;
			for (Hand hand : playerHands) {
				totalBet += hand.bet;
			}
			drawOutlinedText("Total Bet: " + totalBet + " " + currency, infoX, infoY, Color.YELLOW, Color.BLACK);
		}
		
		// Rules
		int rulesX = gp.screenWidth - gp.tileSize * 3;
		int rulesY = gp.screenHeight - gp.tileSize * 2;
		g2.setFont(gp.marumonica.deriveFont(16F));
		drawOutlinedText("• Min bet: " + Player.BET_INC + " " + currency, rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
		rulesY += gp.tileSize / 3;
		drawOutlinedText("• Blackjack pays 3:2", rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
		rulesY += gp.tileSize / 3;
		drawOutlinedText("• Insurance pays 2:1", rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
		rulesY += gp.tileSize / 3;
		drawOutlinedText("• Dealer hits soft 17", rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
		rulesY += gp.tileSize / 3;
		drawOutlinedText("• Can split any pair", rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
	}
	
	private void drawDeck() {
		int deckX = 50;
		int deckY = 150;
			
		g2.drawImage(deckImg, deckX, deckY, 160, 190, null);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 24F));
		String deckCount = String.valueOf(getDeckSize());
		int textX = deckX + 80 - getTextWidth(deckCount) / 2;
		int textY = deckY + 220;
		drawOutlinedText(deckCount, textX, textY, Color.WHITE, Color.BLACK);
	}
	
	private void drawDealerHand() {
		int centerX = gp.screenWidth / 2;
		int dealerY = gp.tileSize / 2;
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 20F));
		String label = "Dealer";
		int labelX = centerX - getTextWidth(label) / 2;
		drawOutlinedText(label, labelX, dealerY, Color.WHITE, Color.BLACK);
		
		dealerY += gp.tileSize / 2;
		
		// Draw cards
		int totalWidth = dealerHand.cards.size() * cardWidth + (dealerHand.cards.size() - 1) * cardSpacing;
		int startX = centerX - totalWidth / 2;
		
		for (int i = 0; i < dealerHand.cards.size(); i++) {
			Card card = dealerHand.cards.get(i);
			int cardX = startX + i * (cardWidth + cardSpacing);
			g2.drawImage(card.getImage(), cardX, dealerY, cardWidth, cardHeight, null);
		}
		
		// Draw total (if not hidden)
		if (gameState != STATE_BETTING && gameState != STATE_INITIAL_DEAL && 
			(dealerHand.cards.size() < 2 || dealerHand.cards.get(1).visible)) {
			int total = getHandTotal(dealerHand);
			String totalText = "Total: " + total;
			int totalX = centerX - getTextWidth(totalText) / 2;
			int totalY = dealerY + cardHeight + 25;
			drawOutlinedText(totalText, totalX, totalY, Color.WHITE, Color.BLACK);
		}
	}
	
	private void drawPlayerHands() {
		if (playerHands.isEmpty()) return;
		
		int centerX = gp.screenWidth / 2;
		int playerY = gp.screenHeight - cardHeight - gp.tileSize * 2;
		
		int margin = gp.tileSize;
		int availableWidth = gp.screenWidth - (2 * margin);
		
		int minHandsWidth = 0;
		for (int h = 0; h < playerHands.size(); h++) {
			Hand hand = playerHands.get(h);
			int handWidth = hand.cards.size() * cardWidth + (hand.cards.size() - 1) * cardSpacing;
			minHandsWidth += handWidth;
		}
		
		// Calculate dynamic hand spacing
		int dynamicHandSpacing;
		if (playerHands.size() <= 1) {
			dynamicHandSpacing = 0;
		} else {
			int remainingSpace = availableWidth - minHandsWidth;
			int gaps = playerHands.size() - 1;
			
			dynamicHandSpacing = Math.max(20, remainingSpace / gaps);
			
			dynamicHandSpacing = Math.min(dynamicHandSpacing, 120);
		}
		
		int totalHandsWidth = minHandsWidth + (playerHands.size() - 1) * dynamicHandSpacing;
		
		// If still too wide, we need to compress further
		if (totalHandsWidth > availableWidth) {
			// Reduce spacing to minimum
			dynamicHandSpacing = 20;
			totalHandsWidth = minHandsWidth + (playerHands.size() - 1) * dynamicHandSpacing;
			
			// If STILL too wide, we may need to scale down cards or overlap more
			if (totalHandsWidth > availableWidth) {
				// Calculate how much we need to compress each hand
				int excessWidth = totalHandsWidth - availableWidth;
				int compressionPerHand = excessWidth / playerHands.size();
				
				// Apply compression by reducing card spacing (more overlap)
				cardSpacing = Math.max(-60, cardSpacing - compressionPerHand);
				
				// Recalculate with compressed spacing
				minHandsWidth = 0;
				for (int h = 0; h < playerHands.size(); h++) {
					Hand hand = playerHands.get(h);
					int handWidth = hand.cards.size() * cardWidth + (hand.cards.size() - 1) * cardSpacing;
					minHandsWidth += handWidth;
				}
				totalHandsWidth = minHandsWidth + (playerHands.size() - 1) * dynamicHandSpacing;
			}
		}
		
		int startX = centerX - totalHandsWidth / 2;
		int currentX = startX;
		
		for (int h = 0; h < playerHands.size(); h++) {
			Hand hand = playerHands.get(h);
			int handWidth = hand.cards.size() * cardWidth + (hand.cards.size() - 1) * cardSpacing;
			int handCenterX = currentX + handWidth / 2;
			
			// Draw hand indicator if multiple hands
			if (playerHands.size() > 1) {
				g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 16F));
				String handLabel = "Hand " + (h + 1);
				if (h == currentHandIndex && gameState == STATE_PLAYER_TURN) {
					handLabel += " ◄";
				}
				int labelX = handCenterX - getTextWidth(handLabel) / 2;
				int labelY = playerY - 10;
				Color labelColor = h == currentHandIndex ? Color.YELLOW : Color.WHITE;
				drawOutlinedText(handLabel, labelX, labelY, labelColor, Color.BLACK);
			}
			
			// Draw cards
			for (int i = 0; i < hand.cards.size(); i++) {
				Card card = hand.cards.get(i);
				int cardX = currentX + i * (cardWidth + cardSpacing);
				g2.drawImage(card.getImage(), cardX, playerY, cardWidth, cardHeight, null);
			}
			
			// Draw hand total
			int total = getHandTotal(hand);
			String totalText = "Total: " + total;
			if (hand.isBusted) totalText += " (BUST)";
			if (hand.isStanding) totalText += " (Stand)";
			
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 16F));
			int totalX = handCenterX - getTextWidth(totalText) / 2;
			int totalY = playerY + cardHeight + 20;
			Color totalColor = hand.isBusted ? Color.RED : Color.WHITE;
			drawOutlinedText(totalText, totalX, totalY, totalColor, Color.BLACK);
			
			// Draw bet amount
			String betText = "Bet: " + hand.bet;
			int betX = handCenterX - getTextWidth(betText) / 2;
			int betY = totalY + 20;
			drawOutlinedText(betText, betX, betY, Color.YELLOW, Color.BLACK);
			
			currentX += handWidth + dynamicHandSpacing;
		}
		
		// Reset cardSpacing to default for next frame
		cardSpacing = -50;
	}
	
	private void drawBettingUI() {
		int panelWidth = gp.tileSize * 8;
		int panelHeight = gp.tileSize * 4;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, Color.YELLOW);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 32F));
		String title = "Place Your Bet";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, Color.YELLOW, Color.BLACK);
		
		// Bet amount
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 48F));
		String betText = String.valueOf(bet);
		int betX = getCenterAlignedTextX(betText, gp.screenWidth / 2);
		int betY = panelY + (int)(gp.tileSize * 2.2);
		drawOutlinedText(betText, betX, betY, Color.WHITE, Color.BLACK);
		
		// Currency label
		g2.setFont(gp.marumonica.deriveFont(18F));
		String currencyLabel = currency;
		int currencyX = getCenterAlignedTextX(currencyLabel, gp.screenWidth / 2);
		int currencyY = betY + gp.tileSize / 2;
		drawOutlinedText(currencyLabel, currencyX, currencyY, Color.LIGHT_GRAY, Color.BLACK);
		
		// Range
		g2.setFont(gp.marumonica.deriveFont(14F));
		int maxBet = p.getMaxBet(gauntlet);
		String rangeText = "(" + Player.BET_INC + " - " + maxBet + ")";
		int rangeX = getCenterAlignedTextX(rangeText, gp.screenWidth / 2);
		int rangeY = currencyY + 25;
		drawOutlinedText(rangeText, rangeX, rangeY, new Color(180, 180, 180), Color.BLACK);
		
		// Tooltips
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Adjust", "/", true, gp.config.leftKey, gp.config.rightKey));
		toolTips.add(new ToolTip(gp, "Deal", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Leave", "/", true, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
	}
	
	private void drawInsuranceUI() {
		int panelWidth = gp.tileSize * 7;
		int panelHeight = gp.tileSize * 4;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize * 3;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, Color.ORANGE);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 28F));
		String title = "Insurance?";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, Color.ORANGE, Color.BLACK);
		
		g2.setFont(gp.marumonica.deriveFont(18F));
		String text1 = "Dealer shows an Ace.";
		int text1X = getCenterAlignedTextX(text1, gp.screenWidth / 2);
		int text1Y = titleY + gp.tileSize / 2;
		drawOutlinedText(text1, text1X, text1Y, Color.WHITE, Color.BLACK);
		
		int insuranceCost = bet / 2;
		String text2 = "Insurance costs: " + insuranceCost + " " + currency;
		int text2X = getCenterAlignedTextX(text2, gp.screenWidth / 2);
		int text2Y = text1Y + 25;
		drawOutlinedText(text2, text2X, text2Y, Color.WHITE, Color.BLACK);
		
		// Options
		int optionY = text2Y + gp.tileSize;
		String[] options = {"No", "Yes"};
		int spacing = gp.tileSize * 2;
		int startX = gp.screenWidth / 2 - spacing;
		
		for (int i = 0; i < options.length; i++) {
			boolean selected = menuNum == i;
			int optionX = startX + i * spacing;
			
			if (selected) {
				g2.setColor(Color.YELLOW);
				g2.drawString(">", optionX - 20, optionY);
			}
			
			Color color = selected ? Color.YELLOW : Color.WHITE;
			drawOutlinedText(options[i], optionX, optionY, color, Color.BLACK);
		}
		
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Choose", "/", true, gp.config.leftKey, gp.config.rightKey));
		toolTips.add(new ToolTip(gp, "Confirm", "", true, gp.config.wKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
	}
	
	private void drawPlayerTurnUI() {
		Hand currentHand = playerHands.get(currentHandIndex);
		
		int panelWidth = gp.tileSize * 5;
		int panelHeight = gp.tileSize * 5;
		int panelX = gp.tileSize / 2;
		int panelY = gp.tileSize * 4;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 200, Color.CYAN);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 20F));
		String title = "Your Turn";
		if (playerHands.size() > 1) {
			title += " (Hand " + (currentHandIndex + 1) + ")";
		}
		int titleX = getCenterAlignedTextX(title, panelX + panelWidth / 2);
		int titleY = panelY + gp.tileSize * 2 / 3;
		drawOutlinedText(title, titleX, titleY, Color.CYAN, Color.BLACK);
		
		// Menu options
		String[] options = {"Hit", "Stand", "Double", "Split"};
		int optionY = titleY + gp.tileSize / 4;
		int optionX = panelX + gp.tileSize / 2;
		
		for (int i = 0; i < options.length; i++) {
			if (!isMenuOptionValid(currentHand, i)) continue;
			
			boolean selected = menuNum == i;
			optionY += gp.tileSize * 0.75;
			
			if (selected) {
				int bgX = optionX - gp.tileSize / 5;
				int bgY = optionY - gp.tileSize / 2;
				int bgWidth = panelWidth - gp.tileSize * 5/6;
				int bgHeight = gp.tileSize * 2/3;
				drawPanelWithBorder(bgX, bgY, bgWidth, bgHeight, 150, Color.YELLOW);
			}
			
			Color color = selected ? Color.YELLOW : Color.WHITE;
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 24F));
			drawOutlinedText(options[i], optionX, optionY, color, Color.BLACK);
			
			// Add explanatory text for some options
			if (i == MENU_DOUBLE) {
				g2.setFont(gp.marumonica.deriveFont(12F));
				String doubleText = "(Double bet, hit once)";
				drawOutlinedText(doubleText, optionX + 5, optionY + gp.tileSize / 3, new Color(200, 200, 200), Color.BLACK);
				optionY += gp.tileSize / 4;
			} else if (i == MENU_SPLIT) {
				g2.setFont(gp.marumonica.deriveFont(12F));
				String splitText = "(Split into two hands)";
				drawOutlinedText(splitText, optionX + 5, optionY + gp.tileSize / 3, new Color(200, 200, 200), Color.BLACK);
				optionY += gp.tileSize / 4;
			}
		}
		
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Navigate", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Select", "", true, gp.config.wKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
	}
	
	private void drawGameOverUI() {
		int panelWidth = gp.tileSize * 6;
		int panelHeight = gp.tileSize * 3;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize * 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 220, Color.GREEN);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 28F));
		String title = "Round Complete";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize;
		drawOutlinedText(title, titleX, titleY, Color.GREEN, Color.BLACK);
		
		g2.setFont(gp.marumonica.deriveFont(20F));
		String prompt = "Press any key to continue";
		int promptX = getCenterAlignedTextX(prompt, gp.screenWidth / 2);
		int promptY = titleY + gp.tileSize;
		
		float alpha = 0.5f + (float)(Math.sin(animationCounter * 0.1) * 0.5);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		drawOutlinedText(prompt, promptX, promptY, Color.WHITE, Color.BLACK);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	
	private void drawMessage() {
		if (messageText == null || messageText.isEmpty()) return;
		
		int panelWidth = gp.tileSize * 8;
		int panelHeight = gp.tileSize * 2;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.screenHeight / 2 - panelHeight / 2;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 240, Color.WHITE);
		
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 24F));
		int textX = getCenterAlignedTextX(messageText, gp.screenWidth / 2);
		int textY = panelY + panelHeight / 2 + 8;
		drawOutlinedText(messageText, textX, textY, Color.WHITE, Color.BLACK);
	}
	
	private void endGame() {
		dealerHand.cards.get(1).setVisible(true);
		gp.saveGame(gp.player.p, true);
		gameState = STATE_GAME_OVER;
		animationCounter = 0;
	}

	private void resetDeck() {
		shuffleDeck();
		currentIndex = 0;
	}
	
	private int getDeckSize() {
		return deck.length - currentIndex;
	}
	
	private int getHandTotal(Hand hand) {
		int total = 0;
		int numAces = 0;

		for (Card card : hand.cards) {
			if (card != null) {
				int rank = card.getRank();

				if (rank >= 11 && rank <= 13) {
					total += 10;
				} else if (rank == 1) {
					total += 11;
					numAces++;
				} else {
					total += rank;
				}
			}
		}

		while (total > 21 && numAces > 0) {
			total -= 10;
			numAces--;
		}

		return total;
	}
	
	private boolean isSoft17(Hand hand) {
		if (getHandTotal(hand) != 17) return false;
		
		// Check if hand contains an Ace counted as 11
		int total = 0;
		boolean hasUsableAce = false;
		
		for (Card card : hand.cards) {
			if (card != null) {
				int rank = card.getRank();
				if (rank >= 11 && rank <= 13) {
					total += 10;
				} else if (rank == 1) {
					if (total + 11 <= 21) {
						total += 11;
						hasUsableAce = true;
					} else {
						total += 1;
					}
				} else {
					total += rank;
				}
			}
		}
		
		return hasUsableAce && total == 17;
	}
	
	private Card dealCard() {
		Card result = deck[currentIndex++];
		result.setVisible(true);
		return result;
	}
	
	private boolean isBlackjack(Hand hand) {
		return hand.cards.size() == 2 && getHandTotal(hand) == 21;
	}
	
	private void winHand(Hand hand, int amt) {
		p.addBetCurrency(gauntlet, amt);
		p.winStreak++;
		p.gamesWon++;
		p.blackjackStats[1]++;
		p.blackjackStats[8] += amt / 2;
		if (p.coins > p.blackjackStats[10]) p.blackjackStats[10] = p.coins;
		if (p.winStreak > p.blackjackStats[11]) p.blackjackStats[11] = p.winStreak;
		p.blackjackStats[12] = 0;
	}
	
	private void loseHand(Hand hand) {
		p.winStreak = 0;
		p.blackjackStats[9] += hand.bet;
		p.blackjackStats[12]++;
		if (p.blackjackStats[12] > p.blackjackStats[13]) p.blackjackStats[13] = p.blackjackStats[12];
	}
	
	@Override
	public void showMessage(String message) {
		this.messageText = message;
		this.messageTimer = 90; // 1.5 seconds at 60 FPS
	}
	
	private void exitBlackjack() {
		if (gauntlet) {
			Puzzle puzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
			puzzle.update(p.getBetCurrency(gauntlet));
		}
		
		// Return to normal game state
		gp.gameState = GamePanel.PLAY_STATE;
	}
	
	// Inner classes
	private class Hand {
		ArrayList<Card> cards;
		int bet;
		boolean isStanding;
		boolean isBusted;
		boolean isDoubled;
		int insurance;
		
		public Hand() {
			cards = new ArrayList<>();
			bet = BlackjackUI.this.bet;
			isStanding = false;
			isBusted = false;
			isDoubled = false;
			insurance = 0;
		}
		
		public void addCard(Card card) {
			cards.add(card);
		}
	}

	private class Card {
		int rank;
		int suit;
		BufferedImage image;
		boolean visible;
		BlackjackUI bjui;
		
		public Card(int value, BlackjackUI bjui) {
			rank = (value % 13) + 1;
			suit = (value % 4) + 1;
			visible = true;
			
			this.image = bjui.setup("/cards/" + rank + "_" + suit, 1.0);
			this.bjui = bjui;
		}

		public int getRank() {
			return rank;
		}

		public BufferedImage getImage() {
			return image;
		}
		
		public void setVisible(boolean visible) {
			this.visible = visible;
			if (this.visible) {
				this.image = bjui.setup("/cards/" + rank + "_" + suit, 1.0);
			} else {
				this.image = bjui.setup("/cards/hidden", 1.0);
			}
		}
		
		public String getRankString() {
			switch(rank) {
			case 1: return "Ace";
			case 2: return "Two";
			case 3: return "Three";
			case 4: return "Four";
			case 5: return "Five";
			case 6: return "Six";
			case 7: return "Seven";
			case 8: return "Eight";
			case 9: return "Nine";
			case 10: return "Ten";
			case 11: return "Jack";
			case 12: return "Queen";
			case 13: return "King";
			default: return "N/A";
			}
		}
		
		public String getSuitString() {
			switch(suit) {
			case 1: return "Spades";
			case 2: return "Clubs";
			case 3: return "Hearts";
			case 4: return "Diamonds";
			default: return "N/A";
			}
		}
	}
}