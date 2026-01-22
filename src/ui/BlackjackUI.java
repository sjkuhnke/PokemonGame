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
import pokemon.Task;
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
	private static final int STATE_PAYOUT = 5;
	private static final int STATE_GAME_OVER = 6;
	private static final int STATE_STATS = 7;
	
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
	
	// Payout animation
	private ArrayList<PayoutParticle> payoutParticles;
	private int payoutAmount;
	private int displayedPayout;
	private int payoutCounter;
	private String payoutText;
	private boolean payoutComplete;
	
	// Stats display
	private int statsScrollOffset;
	private static final int STATS_LINE_HEIGHT = 24;
	private static final int MAX_STATS_SCROLL = 42;
	
	private static final int DECK_SIZE = 104; // 2 decks
	
	// Layout constants
	private int cardWidth = 71;
	private int cardHeight = 96;
	private int cardSpacing = -50;
	
	public BlackjackUI(GamePanel gp, boolean gauntlet) {
		this.gp = gp;
		this.gauntlet = gauntlet;
		this.setCurrency();
		this.p = gp.player.p;
		deckImg = setup("/cards/deck", 1.0);
		
		deck = new Card[DECK_SIZE];
		initializeDeck();
		shuffleDeck();
		
		playerHands = new ArrayList<>();
		dealerHand = new Hand();
		payoutParticles = new ArrayList<>();
		
		gameState = STATE_BETTING;
		menuNum = 0;
		bet = Player.BET_INC;
		
		this.textColor = Color.WHITE;
		
		loadBackground();
	}
	
	public void updateGauntlet(boolean gauntlet) {
		if (this.gauntlet != gauntlet) {
			this.gauntlet = gauntlet;
			this.setCurrency();
			bet = lastBet = Player.BET_INC;
		}
	}
	
	private void setCurrency() {
		this.currency = gauntlet ? "orbs" : "coins";
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
		case STATE_PAYOUT:
			updatePayout();
			break;
		case STATE_GAME_OVER:
			updateGameOver();
			break;
		case STATE_STATS:
			updateStatsDisplay();
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
		
		int maxBet = p.getMaxBet(gauntlet);
		
		if (gp.keyH.leftPressed) {
			gp.keyH.leftPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			
			if (bet <= Player.BET_INC) {
				bet = Math.max(maxBet, Player.BET_INC);
			} else {
				bet -= Player.BET_INC;
				if (bet < Player.BET_INC) bet = Player.BET_INC;
			}
		}
		
		if (gp.keyH.rightPressed) {
			gp.keyH.rightPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			bet += Player.BET_INC;
			if (bet > maxBet) bet = Player.BET_INC;
		}
		
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			
			bet += Player.BET_INC * 10;
			if (bet > maxBet) {
				bet = (maxBet / Player.BET_INC) * Player.BET_INC;
				if (bet < Player.BET_INC) bet = Player.BET_INC;
			}
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			
			bet -= Player.BET_INC * 10;
			if (bet < Player.BET_INC) bet = Player.BET_INC;
		}
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			exitBlackjack();
		}
		
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			gp.playSFX(Sound.S_MENU_CON);
			gameState = STATE_STATS;
			statsScrollOffset = 0;
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
		playerHand.isBlackjack = playerBJ;
		boolean dealerBJ = isBlackjack(dealerHand);
		
		if (playerBJ || dealerBJ) {
			dealerHand.cards.get(1).setVisible(true);
			
			if (playerBJ && dealerBJ) {
				showMessage("Both of you have Blackjack!");
				p.addBetCurrency(gauntlet, bet);
				p.blackjackStats[2]++;
				p.blackjackStats[5]++;
				startPayout(0, "PUSH!");
				return;
			} else if (playerBJ) {
				showMessage("Blackjack! You win 3:2 payout!");
				int winAmount = (int)(bet * 1.5);
				winHand(playerHand, (int)(bet * 2.5));
				p.blackjackStats[5]++;
				startPayout(winAmount, "BLACKJACK!");
				return;
			} else {
				// Dealer blackjack
				if (playerHand.insurance > 0) {
					int insuranceWin = playerHand.insurance * 2;
					showMessage("Insurance pays out!");
					winHand(playerHand, insuranceWin + bet);
					p.blackjackStats[16]++;
					p.blackjackStats[17] += insuranceWin;
					startPayout(playerHand.insurance, "INSURANCE WIN!");
				} else {
					showMessage("Dealer has Blackjack, you lose.");
					loseHand(playerHand);
					if (p.blackjackStats[14] > p.blackjackStats[15]) {
						p.blackjackStats[18]++;
					}
					startPayout(-bet, "DEALER BLACKJACK");
				}
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
		
		if (currentHand.isBlackjack) {
			showMessage("Blackjack! You win 3:2 payout!");
		}
		
		if (currentHand.isStanding || currentHand.isBusted || currentHand.isBlackjack) {
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
	
	private void updateStatsDisplay() {
		// Scroll with up/down
		if (gp.keyH.upPressed) {
			gp.keyH.upPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			statsScrollOffset--;
			if (statsScrollOffset < 0) statsScrollOffset = 0;
		}
		
		if (gp.keyH.downPressed) {
			gp.keyH.downPressed = false;
			gp.playSFX(Sound.S_MENU_1);
			statsScrollOffset++;
			if (statsScrollOffset > MAX_STATS_SCROLL) statsScrollOffset = MAX_STATS_SCROLL;
		}
		
		// Close stats with D, S, or W
		if (gp.keyH.dPressed || gp.keyH.sPressed || gp.keyH.wPressed) {
			gp.keyH.dPressed = false;
			gp.keyH.sPressed = false;
			gp.keyH.wPressed = false;
			gp.playSFX(Sound.S_MENU_CAN);
			gameState = STATE_BETTING;
			statsScrollOffset = 0;
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
		// Split stats
		int rank = hand.cards.get(0).getRank();
		p.blackjackStats[19]++;
		p.blackjackStats[22 + rank]++;
		
		// Take second bet
		p.addBetCurrency(gauntlet, -hand.bet);
		
		// Create new hand with second card
		Hand newHand = new Hand();
		newHand.bet = hand.bet;
		newHand.addCard(hand.cards.remove(1));
		newHand.isSplit = true;
		
		// Deal one card to the first hand
		hand.addCard(dealCard());
		hand.isBlackjack = isBlackjack(hand);
		hand.isSplit = true;
		
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
				int totalLoss = 0;
				for (Hand hand : playerHands) {
					totalLoss += hand.bet;
				}
				startPayout(-totalLoss, "BUST!");
			}
		} else {
			Hand currentHand = playerHands.get(currentHandIndex);
			currentHand.addCard(dealCard());
			currentHand.isBlackjack = isBlackjack(currentHand);
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
			
			if (dealerTotal < 17 || isSoft17(dealerHand)) {
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
		
		int netWinnings = 0;
		for (Hand hand : playerHands) {
			if (hand.isBusted) {
				netWinnings -= hand.bet;
				if (hand.isSplit) p.blackjackStats[21]++;
				continue;
			}
			
			int playerTotal = getHandTotal(hand);
			
			if (hand.isBlackjack) {
				int winAmount = (int)(bet * 1.5);
				winHand(hand, (int)(bet * 2.5));
				p.blackjackStats[5]++;
				netWinnings += winAmount;
				if (hand.isSplit) p.blackjackStats[20]++;
			} else if (dealerBusted) {
				winHand(hand, hand.bet * 2);
				netWinnings += hand.bet;
				if (hand.isDoubled) p.blackjackStats[7]++;
				if (hand.isSplit) p.blackjackStats[20]++;
			} else if (playerTotal > dealerTotal) {
				winHand(hand, hand.bet * 2);
				netWinnings += hand.bet;
				if (hand.isDoubled) p.blackjackStats[7]++;
				if (hand.isSplit) p.blackjackStats[20]++;
			} else if (playerTotal == dealerTotal) {
				p.addBetCurrency(gauntlet, hand.bet);
				p.blackjackStats[2]++;
				if (hand.isDoubled) p.blackjackStats[7]++;
				if (hand.isSplit) p.blackjackStats[22]++;
			} else {
				loseHand(hand);
				netWinnings -= hand.bet;
				if (hand.isSplit) p.blackjackStats[21]++;
			}
		}
		
		String resultMsg = dealerBusted ? "Dealer busts with " + dealerTotal + "!" : "Dealer stands with " + dealerTotal;
		showMessage(resultMsg);
		
		String payoutLabel = netWinnings > 0 ? "YOU WIN!" : netWinnings < 0 ? "YOU LOSE" : "PUSH";
		startPayout(netWinnings, payoutLabel);
	}
	
	private void startPayout(int amount, String label) {
		payoutAmount = amount;
		displayedPayout = 0;
		payoutCounter = 0;
		payoutComplete = false;
		payoutParticles.clear();
		payoutText = label;
		gameState = STATE_PAYOUT;
		
		// Play appropriate sound
		if (amount > 0) {
			//gp.playSFX(Sound.S_MAIN_GAME_SAVE);
		} else if (amount < 0) {
			//gp.playSFX(Sound.S_DEFEAT);
		}
	}
	
	private void updatePayout() {
		payoutCounter++;
		
		// Increment displayed payout gradually
		if (!payoutComplete) {
			if (payoutAmount != 0) {
				int increment = Math.max(1, Math.abs(payoutAmount) / 30);
				if (payoutAmount > 0) {
					displayedPayout += increment;
					if (displayedPayout >= payoutAmount) {
						displayedPayout = payoutAmount;
						payoutComplete = true;
					}
				} else {
					displayedPayout -= increment;
					if (displayedPayout <= payoutAmount) {
						displayedPayout = payoutAmount;
						payoutComplete = true;
					}
				}
				
				// Spawn particles
				if (payoutCounter % 3 == 0 && payoutAmount > 0) {
					spawnPayoutParticles();
				}
				
				// Play tick sound
				if (payoutCounter % 2 == 0) {
					gp.playSFX(Sound.S_MENU_1);
				}
			} else {
				payoutComplete = true;
			}
		}
		
		// Update particles
		for (int i = payoutParticles.size() - 1; i >= 0; i--) {
			PayoutParticle particle = payoutParticles.get(i);
			particle.update();
			if (particle.isDead()) {
				payoutParticles.remove(i);
			}
		}
		
		// Move to game over after animation
		if (payoutComplete && payoutCounter > 120) {
			endGame();
		}
	}
	
	private void spawnPayoutParticles() {
		Random rand = new Random();
		int centerX = gp.screenWidth / 2;
		int centerY = gp.screenHeight / 2 - gp.tileSize * 2;
		
		for (int i = 0; i < 3; i++) {
			float angle = rand.nextFloat() * (float)(Math.PI * 2);
			float speed = 2 + rand.nextFloat() * 3;
			payoutParticles.add(new PayoutParticle(centerX, centerY, angle, speed));
		}
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
		case STATE_PAYOUT:
			drawPayoutAnimation();
			break;
		case STATE_GAME_OVER:
			drawGameOverUI();
			break;
		case STATE_STATS:
			drawStatsOverlay();
			break;
		}
		
		// Draw message
		if (messageTimer > 0) {
			drawMessage();
		}
		
		// Draw controls hint
		if (!gp.keyH.shiftPressed) {
			drawControlsHint();
		}
		
		update();
		
		// Draw tooltips
		drawKeyStrokes();
	}
	
	private void drawStatsOverlay() {
		// Semi-transparent dark overlay
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		// Stats panel
		int panelWidth = gp.tileSize * 14;
		int panelHeight = gp.screenHeight - gp.tileSize * 2;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = gp.tileSize;
		
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 240, new Color(70, 130, 180));
		
		// Title
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 36F));
		String title = "Blackjack Statistics";
		int titleX = getCenterAlignedTextX(title, gp.screenWidth / 2);
		int titleY = panelY + gp.tileSize / 2 + 10;
		drawOutlinedText(title, titleX, titleY, new Color(255, 215, 0), Color.BLACK);
		
		// Calculate stats
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
		int totalSplits = p.blackjackStats[19];
		int splitWins = p.blackjackStats[20];
		int splitLosses = p.blackjackStats[21];
		int splitPushes = p.blackjackStats[22];
		
		// Start drawing stats
		int statsX = panelX + gp.tileSize / 2;
		int[] statsY = new int[1];
		statsY[0] = titleY - (statsScrollOffset * STATS_LINE_HEIGHT);
		
		g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
		
		// Helper to draw a stat line
		class StatLine {
			void draw(String text, Color color) {
				if (statsY[0] > panelY + gp.tileSize && statsY[0] < panelY + panelHeight - 20) {
					drawOutlinedText(text, statsX, statsY[0], color, Color.BLACK);
				}
				statsY[0] += STATS_LINE_HEIGHT;
			}
		}
		StatLine line = new StatLine();
		
		// Games section
		line.draw("", Color.WHITE);
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
		line.draw("GAMES PLAYED", new Color(255, 215, 0));
		g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
		line.draw("Total games: " + games, Color.WHITE);
		if (games > 0) {
			line.draw("Total wins: " + win + " (" + String.format("%.1f%%)", win * 100.0 / games), new Color(0, 255, 0));
			line.draw("Total losses: " + lose + " (" + String.format("%.1f%%)", lose * 100.0 / games), new Color(255, 100, 100));
			line.draw("Total pushes: " + push + " (" + String.format("%.1f%%)", push * 100.0 / games), new Color(200, 200, 255));
		}
		
		// Streaks section
		line.draw("", Color.WHITE);
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
		line.draw("STREAKS", new Color(255, 215, 0));
		g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
		line.draw("Current win streak: " + p.winStreak, new Color(100, 255, 100));
		line.draw("Highest win streak: " + winStreak, new Color(100, 255, 100));
		line.draw("Current lose streak: " + loseStreak, new Color(255, 150, 150));
		line.draw("Highest lose streak: " + highLoseStreak, new Color(255, 150, 150));
		
		// Busts section
		if (games > 0) {
			line.draw("", Color.WHITE);
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
			line.draw("BUSTS", new Color(255, 215, 0));
			g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
			line.draw("Total busts: " + busts, Color.WHITE);
			if (lose > 0) {
				line.draw("% of losses from busts: " + String.format("%.1f%%", busts * 100.0 / lose), new Color(255, 200, 100));
			}
			line.draw("Bust rate: " + String.format("%.1f%%", busts * 100.0 / games), new Color(255, 200, 100));
			if (win > 0) {
				line.draw("% of wins on dealer bust: " + String.format("%.1f%%", bustWins * 100.0 / win), new Color(150, 255, 150));
			}
		}
		
		// Blackjacks section
		if (games > 0) {
			line.draw("", Color.WHITE);
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
			line.draw("BLACKJACKS", new Color(255, 215, 0));
			g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
			line.draw("Total blackjacks: " + blackjacks, new Color(255, 215, 0));
			line.draw("Blackjack rate: " + String.format("%.1f%%", blackjacks * 100.0 / games), new Color(255, 215, 0));
		}
		
		// Doubles section
		if (games > 0) {
			line.draw("", Color.WHITE);
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
			line.draw("DOUBLE DOWNS", new Color(255, 215, 0));
			g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
			line.draw("Total doubles: " + doubles, Color.WHITE);
			line.draw("Double rate: " + String.format("%.1f%%", doubles * 100.0 / games), new Color(200, 200, 255));
			if (doubles > 0) {
				line.draw("Double wins: " + doubleWins, new Color(150, 255, 150));
				line.draw("Double win rate: " + String.format("%.1f%%", doubleWins * 100.0 / doubles), new Color(150, 255, 150));
			}
		}
		
		// Splits section
		if (totalSplits > 0) {
			line.draw("", Color.WHITE);
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
			line.draw("SPLITS", new Color(255, 215, 0));
			g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
			line.draw("Total splits: " + totalSplits, Color.WHITE);
			if (games > 0) {
				line.draw("Split rate: " + String.format("%.1f%%", totalSplits * 100.0 / games), new Color(200, 200, 255));
			}
			
			int totalSplitHands = splitWins + splitLosses + splitPushes;
			if (totalSplitHands > 0) {
				line.draw("Split hand wins: " + splitWins + " (" + String.format("%.1f%%)", splitWins * 100.0 / totalSplitHands), new Color(150, 255, 150));
				line.draw("Split hand losses: " + splitLosses + " (" + String.format("%.1f%%)", splitLosses * 100.0 / totalSplitHands), new Color(255, 150, 150));
				line.draw("Split hand pushes: " + splitPushes + " (" + String.format("%.1f%%)", splitPushes * 100.0 / totalSplitHands), new Color(200, 200, 255));
			}
			
			// Most split ranks
			line.draw("", Color.WHITE);
			line.draw("Splits by rank:", new Color(200, 200, 200));
			
			// Create array of rank counts with their indices
			int[][] rankData = new int[13][2];
			for (int i = 0; i < 13; i++) {
				rankData[i][0] = i + 1; // rank
				rankData[i][1] = p.blackjackStats[23 + i]; // count
			}
			
			// Sort by count descending
			java.util.Arrays.sort(rankData, (a, b) -> Integer.compare(b[1], a[1]));
			
			// Show top 5 ranks
			int shown = 0;
			for (int i = 0; i < 13 && shown < 5; i++) {
				int rank = rankData[i][0];
				int count = rankData[i][1];
				if (count > 0) {
					String rankName = getRankName(rank);
					line.draw("  " + rankName + ": " + count + " (" + String.format("%.1f%%)", count * 100.0 / totalSplits), new Color(180, 220, 255));
					shown++;
				}
			}
		}
		
		// Currency section
		line.draw("", Color.WHITE);
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
		line.draw("CURRENCY", new Color(255, 215, 0));
		g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
		line.draw("Total " + currency + " won: " + coinWin, new Color(100, 255, 100));
		line.draw("Total " + currency + " lost: " + coinLost, new Color(255, 100, 100));
		String netSign = netCoin >= 0 ? "+" : "";
		Color netColor = netCoin >= 0 ? new Color(100, 255, 100) : new Color(255, 100, 100);
		line.draw("Net " + currency + ": " + netSign + netCoin, netColor);
		line.draw("Highest wallet: " + maxCoin, new Color(255, 215, 0));
		
		// Insurance section
		if (insuranceOffered > 0) {
			line.draw("", Color.WHITE);
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 18F));
			line.draw("INSURANCE", new Color(255, 215, 0));
			g2.setFont(gp.marumonica.deriveFont(Font.PLAIN, 16F));
			line.draw("Times offered: " + insuranceOffered, Color.WHITE);
			line.draw("Times taken: " + insuranceTaken + " (" + String.format("%.1f%%)", insuranceTaken * 100.0 / insuranceOffered), new Color(200, 200, 255));
			if (insuranceTaken > 0) {
				line.draw("Insurance wins: " + insuranceWon, new Color(150, 255, 150));
				line.draw("Insurance win rate: " + String.format("%.1f%%", insuranceWon * 100.0 / insuranceTaken), new Color(150, 255, 150));
				String profitSign = insuranceProfit >= 0 ? "+" : "";
				Color profitColor = insuranceProfit >= 0 ? new Color(100, 255, 100) : new Color(255, 100, 100);
				line.draw("Insurance profit: " + profitSign + insuranceProfit + " " + currency, profitColor);
			}
			if (insuranceDeclined > 0) {
				line.draw("Times declined: " + insuranceDeclined, new Color(200, 200, 255));
				line.draw("Missed BJ opportunities: " + missedBJ + " (" + String.format("%.1f%%)", missedBJ * 100.0 / insuranceDeclined), new Color(255, 150, 150));
				line.draw("Correct declines: " + correctDeclines + " (" + String.format("%.1f%%)", correctDeclines * 100.0 / insuranceDeclined), new Color(150, 255, 150));
			}
		}
		
		// Scroll indicator at bottom
		if (statsScrollOffset < MAX_STATS_SCROLL) {
			int arrowY = panelY + panelHeight - 30;
			int arrowX = panelX + panelWidth / 2;
			g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 24F));
			float alpha = 0.5f + (float)(Math.sin(animationCounter * 0.1) * 0.5);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			drawOutlinedText("▼", arrowX - 8, arrowY, Color.WHITE, Color.BLACK);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
		
		// Tooltips
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Scroll", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Back", "/", true, gp.config.wKey, gp.config.sKey, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
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
		int rulesY = gp.screenHeight - (int) (gp.tileSize * 1.5);
		g2.setFont(gp.marumonica.deriveFont(16F));
		drawOutlinedText("• Bets: " + Player.BET_INC + " - " + Player.MAX_BET + " " + currency, rulesX, rulesY, new Color(170, 170, 170), Color.BLACK);
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
					handLabel += " \u2193";
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
		int panelWidth = gp.tileSize * 6;
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
		
		// Arrows
		int arrowSize = 20;
		int leftArrowX = gp.screenWidth / 2 - gp.tileSize * 2;
		int arrowCenterY = betY - gp.tileSize / 3;
		
		int[] leftXPoints = {leftArrowX + arrowSize, leftArrowX, leftArrowX + arrowSize};
		int[] leftYPoints = {arrowCenterY - arrowSize / 2, arrowCenterY, arrowCenterY + arrowSize / 2};
		
		g2.setColor(Color.YELLOW);
		g2.fillPolygon(leftXPoints, leftYPoints, 3);
		
		int rightArrowX = gp.screenWidth / 2 + gp.tileSize * 2 - arrowSize;
		
		int[] rightXPoints = {rightArrowX, rightArrowX + arrowSize, rightArrowX};
		int[] rightYPoints = {arrowCenterY - arrowSize / 2, arrowCenterY, arrowCenterY + arrowSize / 2};
		
		g2.setColor(Color.YELLOW);
		g2.fillPolygon(rightXPoints, rightYPoints, 3);
		
		// Currency label
		g2.setFont(gp.marumonica.deriveFont(18F));
		String currencyLabel = currency;
		int currencyX = getCenterAlignedTextX(currencyLabel, gp.screenWidth / 2);
		int currencyY = betY + gp.tileSize / 2;
		drawOutlinedText(currencyLabel, currencyX, currencyY, Color.LIGHT_GRAY, Color.BLACK);
		
		// Range
		g2.setFont(gp.marumonica.deriveFont(14F));
		int maxBet = p.getMaxBet(gauntlet);
		if (maxBet >= Player.BET_INC) {
			String rangeText = "(" + Player.BET_INC + " - " + maxBet + ")";
			int rangeX = getCenterAlignedTextX(rangeText, gp.screenWidth / 2);
			int rangeY = currencyY + 25;
			drawOutlinedText(rangeText, rangeX, rangeY, new Color(180, 180, 180), Color.BLACK);
		}
		
		// Tooltips
		ArrayList<ToolTip> toolTips = new ArrayList<>();
		toolTips.add(new ToolTip(gp, "Adjust", "/", true, gp.config.leftKey, gp.config.rightKey));
		toolTips.add(new ToolTip(gp, "Fast Adjust", "/", true, gp.config.upKey, gp.config.downKey));
		toolTips.add(new ToolTip(gp, "Deal", "", true, gp.config.wKey));
		toolTips.add(new ToolTip(gp, "Leave", "", true, gp.config.sKey));
		toolTips.add(new ToolTip(gp, "Stats", "", true, gp.config.dKey));
		
		if (gp.keyH.shiftPressed) {
			drawToolTipBar(toolTips);
		}
	}
	
	private void drawInsuranceUI() {
		int panelWidth = gp.tileSize * 6;
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
		int startX = gp.screenWidth / 2 - spacing / 2;
		
		for (int i = 0; i < options.length; i++) {
			boolean selected = menuNum == i;
			int optionX = startX + i * spacing;
			optionX -= gp.tileSize / 4;
			
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
	
	private void drawPayoutAnimation() {
		if (messageTimer > 0) {
			return;
		}
		
		// Draw payout panel
		int panelWidth = gp.tileSize * 6;
		int panelHeight = gp.tileSize * 4;
		int panelX = (gp.screenWidth - panelWidth) / 2;
		int panelY = (gp.screenHeight - panelHeight) / 2;
		
		Color panelColor = payoutAmount > 0 ? Color.GREEN : payoutAmount < 0 ? Color.RED : Color.BLUE;
		drawPanelWithBorder(panelX, panelY, panelWidth, panelHeight, 230, panelColor);
		
		// Draw message label
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 40F));
		int labelX = getCenterAlignedTextX(payoutText, gp.screenWidth / 2);
		int labelY = panelY + gp.tileSize;
		
		// Pulse effect on label
		float scale = 1.0f + (float)(Math.sin(payoutCounter * 0.15) * 0.1);
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 40F * scale));
		labelX = getCenterAlignedTextX(payoutText, gp.screenWidth / 2);
		
		Color labelColor = payoutAmount > 0 ? Color.YELLOW : payoutAmount < 0 ? Color.ORANGE : Color.WHITE;
		drawOutlinedText(payoutText, labelX, labelY, labelColor, Color.BLACK);
		
		// Draw payout amount with counting animation
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 60F));
		String payoutText;
		if (displayedPayout > 0) {
			payoutText = "+" + displayedPayout;
		} else if (displayedPayout < 0) {
			payoutText = String.valueOf(displayedPayout);
		} else {
			payoutText = "0";
		}
		
		int payoutX = getCenterAlignedTextX(payoutText, gp.screenWidth / 2);
		int payoutY = panelY + (int)(gp.tileSize * 2.5);
		
		Color amountColor = displayedPayout > 0 ? new Color(0, 255, 0) : displayedPayout < 0 ? new Color(255, 50, 50) : Color.WHITE;
		drawOutlinedText(payoutText, payoutX, payoutY, amountColor, Color.BLACK);
		
		// Draw currency label
		g2.setFont(gp.marumonica.deriveFont(Font.BOLD, 24F));
		String currLabel = currency.toUpperCase();
		int currX = getCenterAlignedTextX(currLabel, gp.screenWidth / 2);
		int currY = payoutY + gp.tileSize;
		drawOutlinedText(currLabel, currX, currY, Color.LIGHT_GRAY, Color.BLACK);
		
		// Draw particles
		for (PayoutParticle particle : payoutParticles) {
			particle.draw(g2);
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
		this.messageTimer = 60;
	}
	
	private void exitBlackjack() {
		if (gauntlet) {
			Puzzle puzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
			puzzle.update(p.getBetCurrency(gauntlet));
			gp.gameState = GamePanel.PLAY_STATE;
		} else {
			gp.setTaskState();
			Task.addTask(Task.TEXT, "Come play again soon, okay?");
		}
	}
	
	// Inner classes
	private class PayoutParticle {
		float x, y;
		float vx, vy;
		int life;
		Color color;
		int size;
		
		public PayoutParticle(int startX, int startY, float angle, float speed) {
			this.x = startX;
			this.y = startY;
			this.vx = (float)(Math.cos(angle) * speed);
			this.vy = (float)(Math.sin(angle) * speed);
			this.life = 90;
			
			Random rand = new Random();
			// Gold/yellow colors for coins
			int colorChoice = rand.nextInt(3);
			switch(colorChoice) {
			case 0:
			color = gauntlet ? new Color(209, 19, 203) : new Color(255, 215, 0); // Gold
			break;
			case 1:
			color = gauntlet ? new Color(63, 26, 173) : new Color(255, 255, 0); // Yellow
			break;
			default:
			color = gauntlet ? new Color(99, 23, 191) : new Color(255, 200, 50); // Orange-gold
			}
			
			this.size = 8 + rand.nextInt(8);
		}
		
		public void update() {
			x += vx;
			y += vy;
			vy += 0.3f; // Gravity
			life--;
		}
		
		public boolean isDead() {
			return life <= 0;
		}
		
		public void draw(Graphics2D g2) {
			float alpha = Math.min(1.0f, life / 30.0f);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			
			// Draw coin-like circle
			g2.setColor(color);
			g2.fillOval((int)x - size/2, (int)y - size/2, size, size);
			
			// Draw shine/highlight
			g2.setColor(new Color(255, 255, 255, (int)(200 * alpha)));
			g2.fillOval((int)x - size/4, (int)y - size/2, size/3, size/3);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}
	
	private class Hand {
		ArrayList<Card> cards;
		int bet;
		boolean isBlackjack;
		boolean isStanding;
		boolean isBusted;
		boolean isDoubled;
		boolean isSplit;
		int insurance;
		
		public Hand() {
			cards = new ArrayList<>();
			bet = BlackjackUI.this.bet;
			isBlackjack = false;
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
	
	private String getRankName(int rank) {
		switch(rank) {
		case 1: return "Aces";
		case 2: return "Twos";
		case 3: return "Threes";
		case 4: return "Fours";
		case 5: return "Fives";
		case 6: return "Sixes";
		case 7: return "Sevens";
		case 8: return "Eights";
		case 9: return "Nines";
		case 10: return "Tens";
		case 11: return "Jacks";
		case 12: return "Queens";
		case 13: return "Kings";
		default: return "Unknown";
		}
	}
}