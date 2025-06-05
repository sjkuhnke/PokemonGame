package overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pokemon.*;
import puzzle.Puzzle;
import util.Pair;

public class SimBattleUI extends BattleUI {
	
	public ArrayList<Pair<Double, String>> parlaySheet;
	public Pair<Pokemon, ArrayList<Move>> p1Moves;
	public Pair<Pokemon, ArrayList<Move>> p2Moves;
	public Pair<Pokemon, String> p1Switch;
	public Pair<Pokemon, String> p2Switch;
	
	BufferedImage autoplayOff;
	BufferedImage autoplayOn;
	boolean autoplay;
	boolean startAutoplay;
	
	public int choice;
	public int[] simOdds;
	public int[] odds;
	public int betPayout;
	public int currentParlay = -1;
	private boolean parlayDone = true;
	private int parlayPayout = -1;
	private boolean payParlay;
	private boolean awarded;

	public SimBattleUI(GamePanel gp) {
		super(gp);
		
		autoplayOff = setup("/battle/autoplay-off", 0.75);
		autoplayOn = setup("/battle/autoplay-on", 0.75);
	}
	
	@Override
	public void showMessage(String message) {
		super.showMessage(message);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		
		if (subState != INFO_STATE && !showFoeSummary && !showParlays) {
			if (currentAbility == null && (currentTask == null || currentTask.type != Task.PARLAY)) {
				drawAutoplayWindow();
			}
			drawBetInfo();
		}
	}
	
	private void drawBetInfo() {
		int x = 12;
		int y = 56;
		
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.setColor(Color.BLACK);
		
		g2.drawString(getBetCurrencyName(gauntlet) + ": " + gp.player.p.getBetCurrency(gauntlet), x, y);
		y += 28;
		
		g2.drawString("Bet: " + battleBet, x, y);
		y += 28;
		
		g2.drawString("Payout: " + betPayout, x, y);
		
		y += 8;
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(x - 8, y, x + gp.tileSize * 3, y);
		g2.setStroke(new BasicStroke(3));
		
		y += 26;
		
		g2.drawString("Games Won: " + gp.player.p.gamesWon, x, y);
		y += 28;
		
		g2.drawString("Win Streak: " + gp.player.p.winStreak, x, y);
		
		if (currentTask != null && currentTask.type == Task.PARLAY) return;
		
		int userOdds = user.trainer.getFlagIndex() == 1 ? odds[0] : odds[1];
		int foeOdds = foe.trainer.getFlagIndex() == 1 ? odds[0] : odds[1];
		
		g2.drawString(formatOdds(userOdds), 400, 280);
		g2.drawString(formatOdds(foeOdds), 290, 30);
	}

	@Override
	protected void endTask() {
		super.endTask();
	}
	
	@Override
	protected void cooldownState() {
		super.cooldownState();
	}
	
	@Override
	protected void taskState() {
		super.taskState();
	}
	
	@Override
	protected void drawAbility() {
		super.drawAbility();
	}
	
	@Override
	protected void drawTask() {
		super.drawTask();
		
		if (currentTask != null) {
			switch (currentTask.type) {
			case Task.PAYOUT:
				currentDialogue = currentTask.message;
				switch (currentTask.start) { // 1 = win, 0 = lose, -1 = draw
				case -1:
					if (battleBet > 0) {
						betPayout = 0;
						battleBet--;
						gp.player.p.addBetCurrency(gauntlet, 1);
					} else {
						cooldownCounter++;
						if (cooldownCounter >= 125) {
							cooldownCounter = 0;
							currentTask = null;
						}
					}
					break;
				case 0:
					if (battleBet > 0 || gp.player.p.winStreak > 0) {
						betPayout = 0;
						battleBet = battleBet > 0 ? battleBet - 1 : 0;
						gp.player.p.winStreak = gp.player.p.winStreak > 0 ? gp.player.p.winStreak - 1 : 0;
					} else {
						cooldownCounter++;
						if (cooldownCounter >= 125) {
							cooldownCounter = 0;
							currentTask = null;
						}
					}
					break;
				case 1:
					if (betPayout > 0) {
						betPayout--;
						if (battleBet > 0) {
							battleBet--;
						}
						gp.player.p.addBetCurrency(gauntlet, 1);
						if (!awarded) {
							gp.player.p.gamesWon++;
							gp.player.p.winStreak++;
							awarded = true;
						}
					} else {
						cooldownCounter++;
						if (cooldownCounter >= 125) {
							cooldownCounter = 0;
							currentTask = null;
							awarded = false;
						}
					}
					break;
				}
				break;
			case Task.PARLAY:
				dialogueState = DIALOGUE_FREE_STATE;
				currentDialogue = "";
				drawParlayPayout();
				break;
			}
		}
	}
	
	private void drawParlayPayout() {
		drawParlaySheet(false);
		drawParlays();
		
		int x = gp.tileSize / 2;
		int y = gp.tileSize * 5;
		int width = (int) (gp.tileSize * 4.5);
		int height = (int) (gp.tileSize * 6.5);
		
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 2;
		y += gp.tileSize * 2;
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int lineHeight = (int) (gp.tileSize * 0.75);
		int[] stats = Pokemon.field.getStats();
		
		for (int i = 0; i < MAX_PARLAYS; i++) {
			g2.setColor(Color.WHITE);
			g2.drawString("Line " + (i + 1) + ":", x, y);
			
			if (i > currentParlay) {
				y += lineHeight;
				continue;
			}
			
			String text;
			Color color;
			
			if (parlays[i] == 0) {
				text = "N/A";
				color = Color.GRAY;
			} else {
				boolean correct = parlays[i] > 0 ? stats[i] > parlaySheet.get(i).getFirst() : stats[i] < parlaySheet.get(i).getFirst();
				text = correct ? "Correct!" : "Incorrect";
				color = correct ? Color.GREEN : Color.RED;
			}
			
			g2.setColor(color);
			g2.drawString(text, getRightAlignedTextX(text, (int) (x + gp.tileSize * 3.5)), y);
			
			y += lineHeight;
		}
		
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(28F));
		String string1 = "Parlay Sheet";
		g2.drawString(string1, getCenterAlignedTextX(string1, (int) (gp.tileSize * 2.75)), (int) (gp.tileSize * 5.75));
		String string2 = "Payout: " + commandNum;
		g2.drawString(string2, getCenterAlignedTextX(string2, (int) (gp.tileSize * 2.75)), (int) (gp.tileSize * 6.3));
		
		if (currentParlay < MAX_PARLAYS) {
			if (!parlayDone) {
				cooldownCounter++;
				if (!awarded) {
					boolean correct = parlays[currentParlay] != 0;
					correct = correct
							? parlays[currentParlay] > 0
									? stats[currentParlay] > parlaySheet.get(currentParlay).getFirst()
									: stats[currentParlay] < parlaySheet.get(currentParlay).getFirst()
							: false;
					if (correct) {
						gp.player.p.gamesWon++;
						awarded = true;
					}
				}
				if (cooldownCounter >= 30) {
					cooldownCounter = 0;
					parlayDone = true;
					awarded = false;
				}
			} else {
				cooldownCounter++;
				if (cooldownCounter >= 60) {
					cooldownCounter = 0;
					parlayDone = false;
					awarded = false;
					currentParlay++;
				}
			}
		} else if (!payParlay) {
			if (parlayPayout == -1) {
				double[] expectedValues = {
			        parlaySheet.get(0).getFirst(),
			        parlaySheet.get(1).getFirst(),
			        parlaySheet.get(2).getFirst(),
			        parlaySheet.get(3).getFirst(),
			        parlaySheet.get(4).getFirst(),
			        parlaySheet.get(5).getFirst()
				};
				parlayPayout = SimBattleUI.calculateParlayPayout(parlays, expectedValues, Pokemon.field.getStats(), parlayBet, -1);
			}
			
			if (commandNum < parlayPayout) {
				commandNum++;
			} else {
				cooldownCounter++;
				if (cooldownCounter >= 120) {
					cooldownCounter = 0;
					parlayPayout = -1;
					payParlay = true;
				}
			}
		} else {
			if (commandNum > 0) {
				commandNum--;
				gp.player.p.addBetCurrency(gauntlet, 1);
			} else {
				cooldownCounter++;
				if (cooldownCounter >= 120) {
					commandNum = 0;
					cooldownCounter = 0;
					currentParlay = -1;
					parlayPayout = -1;
					parlayDone = true;
					payParlay = false;
					currentTask = null;
				}
			}
		}
	}

	@Override
	protected void drawIdleScreen() {
		if (autoplay && startAutoplay) {
			subState = TASK_STATE;
			turn();
			return;
		}
		startAutoplay = false;
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawCalcWindow();
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Item.useCalc(user, null, foe, true);
		}
		drawActionScreen(user);
		String dText = foe.trainerOwned() ? "Foe" : null;
		if (!showFoeSummary) drawToolTips("OK", "Calc", "Auto", dText);
	}
	
	@Override
	protected void drawUser() {
		super.drawUser();
	}
	
	@Override
	protected void drawFoe() {
		super.drawFoe();
	}
	
	@Override
	protected void drawCaughtIndicator() {
		return;
	}
	
	@Override
	protected void drawUserParty() {
		int x = 10;
		int y = 10;
		int width = 20;
		Pokemon[] team = user.trainer.getTeam();
		for (int i = 0; i < 6; i++) { // changed user.getPlayer() to user.trainer
			Pokemon p = i >= team.length ? null : team[i];
			BufferedImage image;
			if (p == null) {
				image = emptyIcon;
			} else if (p.isFainted() && !p.isVisible()) {
				image = faintedIcon;
			} else if (p.isVisible()) {
				image = currentIcon;
			} else {
				image = ballIcon;
			}
			g2.drawImage(image, x, y, null);
			x += width;
		}
	}
	
	@Override
	protected void drawFoeParty() {
		super.drawFoeParty();
	}
	
	@Override
	protected void drawStatus(Pokemon p) {
		if (p == user) { // changed p.playerOwned() to p == user
			if (userStatus != Status.HEALTHY) g2.drawImage(userStatus.getImage(), 339, 326, null);
		} else {
			if (foeStatus != Status.HEALTHY) g2.drawImage(foeStatus.getImage(), 232, 78, null);
		}
	}
	
	@Override
	protected void drawTypes(Pokemon p) {
		PType[] types = Pokemon.getTypes(p.id);
		if (p == user) { // same as above
			g2.drawImage(types[0].getImage(), 340, 298, null);
			if (types[1] != null) g2.drawImage(types[1].getImage(), 364, 298, null);
		} else {
			g2.drawImage(types[0].getImage(), 232, 50, null);
			if (types[1] != null) g2.drawImage(types[1].getImage(), 256, 50, null);
		}
	}
	
	@Override
	protected void drawHPImage(Pokemon p) {
		if (p == user) { // same as above
			g2.drawImage(userHPBar, 302, 280, null);
		} else {
			g2.drawImage(foeHPBar, 222, 39, null);
		}
	}
	
	@Override
	protected void drawUserHP() {
		super.drawUserHP();
		
	}
	
	@Override
	protected void drawDialogueState() {
		int x = gp.screenWidth - gp.tileSize;
		int y = gp.screenHeight - gp.tileSize;
		int width = gp.tileSize / 2;
		int height = gp.tileSize / 2;
		g2.setColor(Color.WHITE);
		g2.fillPolygon(new int[] {x, (x + width), (x + width / 2)}, new int[] {y, y, y + height}, 3);
		if (gp.keyH.wPressed || autoplay) {
			gp.keyH.wPressed = false;
			switch(subState) {
			case STARTING_STATE:
			    setStartingTasks();
				subState = TASK_STATE;
				break;
			case END_STATE:
				if (gauntlet) {
					Puzzle puzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
					puzzle.update(gp.player.p.getBetCurrency(gauntlet));
				}
				if (tasks.isEmpty()) {
					user.setVisible(false);
					gp.saveGame(gp.player.p);
					Pokemon.field = new Field();
					gp.gameState = GamePanel.PLAY_STATE;
				}
				break;
			case MOVE_MESSAGE_STATE:
				subState = MOVE_SELECTION_STATE;
				break;
			}
			dialogueState = DIALOGUE_FREE_STATE;
			currentDialogue = "";
			currentTask = null;
			currentAbility = null;
		}
	}
	
	@Override
	protected void setStartingTasks() {
		field = new Field();
		user.getFieldEffects().clear();
		foe.getFieldEffects().clear();
	    Pokemon.field = field;
	    userHP = user.currentHP;
	    maxUserHP = user.getStat(0);
	    foeHP = foe.currentHP;
	    maxFoeHP = foe.getStat(0);
	    aura = false;
		
	    Task.addSwapInTask(foe, false);
		foeFainted = foe.trainer.getNumFainted();
		Task.addSwapInTask(user, true);
	    Pokemon fasterInit = user.getFaster(foe, 0, 0);
		Pokemon slowerInit = fasterInit == user ? foe : user;
		fasterInit.swapIn(slowerInit, true);
		slowerInit.swapIn(fasterInit, true);
	}
	
	@Override
	protected void drawFoePokeball(boolean arriving, Item ballType) {
		super.drawFoePokeball(arriving, ballType);
	}
	
	@Override
	protected void drawFoePokeball(boolean arriving) {
		super.drawFoePokeball(arriving);
	}
	
	@Override
	protected void drawUserPokeball(boolean arriving) {
		super.drawUserPokeball(arriving);
	}
	
	@Override
	protected void drawFoeSprite() {
		super.drawFoeSprite();
	}
	
	@Override
	protected void drawUserSprite() {
		super.drawUserSprite();
	}
	
	@Override
	protected void drawHPBar(Pokemon p, double amt, double maxHP) {
		double hpRatio = amt / maxHP;
		g2.setColor(getHPBarColor(hpRatio));
		int x;
		int y;
		int width = amt == 0 ? 0 : Math.max((int) (hpRatio * 120), 1);
		int height = 8;
		if (p == user) { // same as above
			x = 426;
			y = 330;
		} else {
			x = 319;
			y = 82;
		}
		g2.fillRect(x, y, width, height);
		g2.setColor(getHPBarColor(hpRatio).darker());
		g2.fillRect(x, y, width, 3);
	}
	
	@Override
	protected void drawExpBar() {
		return;		
	}
	
	@Override
	protected void drawNameLabel(Pokemon p, int level) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int x;
		int y;
		int levelX;
		int levelY;
		String name = p == user ? userName : foeName;
		g2.setFont(g2.getFont().deriveFont(getFontSize(name, gp.tileSize * 2.5F)));
		
		if (p == user) { // same as above
			x = getRightAlignedTextX(name, 494);
			y = 318;
			levelX = 523;
			levelY = 318;
		} else {
			x = getRightAlignedTextX(name, 387);
			y = 70;
			levelX = 416;
			levelY = 70;
		}
		g2.drawString(name, x, y);
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(level + "", levelX, levelY);
	}
	
	@Override
	protected String getStateName() {
		return super.getStateName();
	}
	
	@Override
	protected String getDialogueStateName() {
		return super.getDialogueStateName();
	}
	
	@Override
	protected void drawActionScreen(Pokemon p) {
		int x = gp.tileSize * 6;
		int y = gp.screenHeight - (gp.tileSize * 4);
		int width = gp.screenWidth - x;
		int height = gp.tileSize * 4;
		
		drawActionBackground(p, x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize / 4;
		width = (int) (gp.tileSize * 3.5);
		height = (int) (gp.tileSize * 1.5);
		g2.setFont(g2.getFont().deriveFont(48F));
		
		g2.setColor(Color.RED.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("TURN", x + 30, y + 50);
		if (commandNum == 0) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.wPressed = false;
				subState = TASK_STATE;
				startAutoplay = true;
				turn();
			}
		}
		
		x += width + gp.tileSize;
		g2.setColor(Color.GREEN.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("SWITCH", x + 30, y + 50);
		if (commandNum == 1) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.wPressed = false;
				Pokemon tmp = user;
				user = foe;
				foe = tmp;
				
				userHP = user.currentHP;
				foeHP = foe.currentHP;
				maxUserHP = user.getStat(0);
				maxFoeHP = foe.getStat(0);
				userStatus = user.status;
				foeStatus = foe.status;
				userLevel = user.level;
				String temp = foeName;
				foeName = userName;
				userName = temp;
				PType[] typeTemp = foeType;
				foeType = userType;
				userType = typeTemp;
			}
		}
		
		x = gp.tileSize * 7;
		y += height + 16;
		g2.setColor(Color.YELLOW.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("INFO", x + 30, y + 50);
		if (commandNum == 2) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.wPressed = false;
				currentDialogue = "";
				subState = INFO_STATE;
			}
		}
		
		x += width + gp.tileSize;
		g2.setColor(Color.BLUE.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("SHEET", x + 30, y + 50);
		if (commandNum == 3) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.wPressed = false;
				showParlays = true;
			}
		}
		
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			if (foe.trainerOwned()) {
				if (foeSummary == null && !showFoeSummary) foeSummary = foe;
				if (showFoeSummary) foeSummary = null;
				moveSummaryNum = -1;
				showFoeSummary = !showFoeSummary;
			}
		}
		
		if (showFoeSummary) {
			drawFoeSummaryParty();
			drawSummary(foeSummary, user);
			if (gp.keyH.sPressed) {
				gp.keyH.sPressed = false;
				if (moveSummaryNum < 0) {
					foeSummary = null;
					showFoeSummary = false;
				} else {
					moveSummaryNum = -1;
				}
			}
			if (gp.keyH.leftPressed) {
				gp.keyH.leftPressed = false;
				if (foeSummary.trainer != null && moveSummaryNum < 0) {
					int currentIndex = foeSummary.trainer.indexOf(foeSummary);
					currentIndex = (currentIndex - 1 + foeSummary.trainer.team.length) % foeSummary.trainer.team.length;
					foeSummary = foeSummary.trainer.team[currentIndex];
				}
			}
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				if (foeSummary.trainer != null && moveSummaryNum < 0) {
					int currentIndex = foeSummary.trainer.indexOf(foeSummary);
					currentIndex = (currentIndex + 1) % foeSummary.trainer.team.length;
					foeSummary = foeSummary.trainer.team[currentIndex];
				}
			}
		} else if (showParlays) {
			drawParlaySheet(false);
			drawParlays();
		}
	}
	
	private void drawParlays() {
	    int x = gp.tileSize * 11;
	    int y = gp.tileSize / 2;
	    int width = gp.tileSize * 5;
		int height = (int) (gp.tileSize * 11.25);
		
		drawSubWindow(x, y, width, height);
		
		x += gp.tileSize / 3;
		y += gp.tileSize;
		
	    int lineSpacing = (int) (gp.tileSize * 1.75); // Match the parlay spacing
	    
	    String[] labels = {"crits:", "misses:", "super effective hits:", "switches:", "knockouts:", "total turns:"};
	    double[] expectedValues = {
	        parlaySheet.get(0).getFirst(),
	        parlaySheet.get(1).getFirst(),
	        parlaySheet.get(2).getFirst(),
	        parlaySheet.get(3).getFirst(),
	        parlaySheet.get(4).getFirst(),
	        parlaySheet.get(5).getFirst()
	    };
	    int[] actualValues = Pokemon.field.getStats();
	    
	    g2.setFont(g2.getFont().deriveFont(24F));
	    
	    for (int i = 0; i < labels.length; i++) {
	        g2.setColor(Color.WHITE);
	        g2.drawString(labels[i], x, y);

	        int guess = parlays[i]; // Player's guess: 1 (over), -1 (under), 0 (no guess)
	        int actual = actualValues[i];
	        double expected = expectedValues[i];

	        // Determine text color
	        if (guess == 1) { // Player guessed "over"
	            g2.setColor(actual >= expected ? Color.GREEN : Color.RED);
	        } else if (guess == -1) { // Player guessed "under"
	            g2.setColor(actual < expected ? Color.GREEN : Color.RED);
	        } else { // No guess made
	            g2.setColor(Color.GRAY);
	        }

	        g2.drawString(String.valueOf(actual), (int) (x + gp.tileSize * 3.85), y);
	        y += lineSpacing;
	    }

	}

	@Override
	protected void drawFoeSummaryParty() {
		super.drawFoeSummaryParty();
	}
	
	@Override
	protected void drawMoveSelectionScreen() {
		super.drawMoveSelectionScreen();
	}
	
	@Override
	protected void drawCalcWindow() {
		super.drawCalcWindow();
	}
	
	@Override
	protected void drawCatchWindow() {
		return;
	}
	
	protected void drawAutoplayWindow() {
		g2.setFont(g2.getFont().deriveFont(24F));
		int x = gp.screenWidth - (gp.tileSize * 4);
		int y = (int) (gp.screenHeight - (gp.tileSize * 5.5));
		int width = gp.tileSize * 2;
		int height = (int) (gp.tileSize * 1.5);
		
		drawSubWindow(x, y, width, height, 125);
		x += gp.tileSize / 4;
		y += gp.tileSize / 4;
		BufferedImage autoIcon = autoplay ? autoplayOn : autoplayOff;
		g2.drawImage(autoIcon, x, y, null);
		
		x += gp.tileSize;
		y += gp.tileSize * 0.75;
		g2.drawString("[S]", x, y);
		
		if (gp.keyH.sPressed) {
			gp.keyH.sPressed = false;
			autoplay = !autoplay;
			if (subState != IDLE_STATE) startAutoplay = true;
		}
	}
	
	@Override
	protected void drawPartySelectionScreen() {
		super.drawPartySelectionScreen();
	}
	
	@Override
	protected void drawActionBackground(Pokemon p, int x, int y, int width, int height) {
		super.drawActionBackground(p, x, y, width, height);
	}
	
	@Override
	protected void drawMoves() {
		super.drawMoves();
	}
	
	public void turn() {
		Pokemon p1 = user;
		Pokemon p2 = foe;
		boolean fFaster = p1.getFaster(p2, 0, 0) == p2;
		
		p1Moves = null;
		p2Moves = null;
		p1Switch = null;
		p2Switch = null;
		Move uMove = p1.bestMove(p2, !fFaster);
		Move fMove = p2.bestMove(p1, fFaster);
		
		int uP, fP;
		uP = uMove == null ? 0 : uMove.getPriority(p1);
		fP = fMove == null ? 0 : fMove.getPriority(p2);
		if (uMove != null && p1.ability == Ability.PRANKSTER && uMove.cat == 2) ++uP;
		if (fMove != null && p2.ability == Ability.PRANKSTER && fMove.cat == 2) ++fP;
		
		if (uMove != null && uMove.priority < 1 && uMove.hasPriority(p1)) ++uP;
		if (fMove != null && fMove.priority < 1 && fMove.hasPriority(p2)) ++fP;
		
		if (uMove != null && fMove != null && !p1.hasStatus(Status.SWAP)
				&& !p2.hasStatus(Status.SWAP)) {
			uP = p1.checkQuickClaw(uP);
			fP = p2.checkQuickClaw(fP);
		}
		if (uMove != null) uP = p1.checkCustap(uP, p2);
		if (fMove != null) fP = p2.checkCustap(fP, p1);
		
		Pokemon faster;
		Pokemon slower;
		
		if (uMove == null || fMove == null) {
			faster = uMove == null ? p1 : p2;
		} else {
			faster = p1.getFaster(p2, uP, fP);
		}
		
		slower = faster == p1 ? p2 : p1;
		
		Move fastMove = faster == p1 ? uMove : fMove;
		Move slowMove = faster == p1 ? fMove : uMove;
		
		boolean fastCanMove = true;
		boolean slowCanMove = true;
		
		if (faster.hasStatus(Status.SWAP)) {
			faster = faster.trainer.swapOut(slower, fastMove, false, faster.trainer.hasUser(user));
			fastMove = null;
			fastCanMove = false;
		}
		
		if (slower.hasStatus(Status.SWAP)) {
			slower = slower.trainer.swapOut(faster, slowMove, false, slower.trainer.hasUser(user));
			slowMove = null;
			slowCanMove = false;
		}
		
		if (slowMove != null && fastMove == Move.SUCKER_PUNCH && slowMove.cat == 2) fastMove = Move.FAILED_SUCKER;
		
		if (fastCanMove) {
			faster.moveInit(slower, fastMove, true);
			faster = faster.trainer.getCurrent();
			slower = slower.trainer.getCurrent();
		}
		
		// Check for swap
		if (faster.trainer.hasValidMembers() && fastCanMove && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
			faster = faster.trainer.swapOut(slower, null, faster.lastMoveUsed == Move.BATON_PASS, faster.trainer.hasUser(user));
		}
		// Check for swap
		if (slower.trainer.hasValidMembers() && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
			slower = slower.trainer.swapOut(faster, null, false, slower.trainer.hasUser(user));
			slowCanMove = false;
		}
		
        if (slowCanMove) {
        	slower.moveInit(faster, slowMove, false);
        	faster = faster.trainer.getCurrent();
        	slower = slower.trainer.getCurrent();
        }
        
        // Check for swap
        if (slower.trainer.hasValidMembers() && slowCanMove && !faster.trainer.wiped() && slower.hasStatus(Status.SWITCHING)) {
        	slower = slower.trainer.swapOut(faster, null, slower.lastMoveUsed == Move.BATON_PASS, slower.trainer.hasUser(user));
        }
    	// Check for swap
 		if (faster.trainer.hasValidMembers() && !slower.trainer.wiped() && faster.hasStatus(Status.SWITCHING)) {
 			faster = faster.trainer.swapOut(slower, null, false, faster.trainer.hasUser(user));
 		}
		
		if (fastMove != null || slowMove != null) {
			if (!faster.trainer.wiped() && !slower.trainer.wiped()) faster.endOfTurn(slower);
			if (!faster.trainer.wiped() && !slower.trainer.wiped()) slower.endOfTurn(faster);
			if (!faster.trainer.wiped() && !slower.trainer.wiped()) field.endOfTurn(faster, slower);
		}
		
		for (int j = 0; j < 2; j++) {
			Pokemon p;
			if (j == 0) {
				p = faster;
			} else {
				p = slower;
			}
			Pokemon foe = p == faster ? slower : faster;
			Pokemon next = p.trainer.getCurrent();
			while (next.isFainted()) {
				if (next.trainer.hasNext()) {
					boolean userSide = next.trainer.hasUser(user);
					next = next.trainer.next(foe, userSide);
					Task.addSwapInTask(next, userSide);
					next.swapIn(foe, true);
				} else {
		            break;
				}
			}
		}
		if (!faster.trainer.wiped() && slower.trainer.wiped()) {
			endSim(faster.trainer, slower.trainer);
		} else if (faster.trainer.wiped() && !slower.trainer.wiped()) {
			endSim(slower.trainer, faster.trainer);
		} else if (faster.trainer.wiped() && slower.trainer.wiped()) {
			endSim(null, null);
		}
	}
	
	private void endSim(Trainer winner, Trainer loser) {
		gp.saveGame(gp.player.p);
		
		boolean draw = winner == null || loser == null;
		boolean win = draw ? false : winner.getFlagIndex() == choice;
		
		int mode = win ? 1 : draw ? -1 : 0;
		
		int payout = draw ? battleBet : win ? calculatePayout(battleBet, choice - 1, simOdds) : -1;
		
		String line1 = draw ? "It's a draw: bet was returned!" : winner.getName() + " defeated " + loser.getName() + "!";
		String line2 = draw ? "" : win ? "You guessed correctly!\nWon " + payout + " coins!" : "You guessed incorrectly!\nLost " + battleBet + " coins.";
		
		Task t = Task.addTask(Task.PAYOUT, line1 + "\n" + line2);
		t.counter = payout;
		t.start = mode;
		
		Task.addTask(Task.PARLAY, "");
		
		Task.addTask(Task.END, "Thanks for playing!");
	}
	
	@Override
	protected boolean hasAlive() {
		return super.hasAlive();
	}
	
	@Override
	public void drawParty(Item item) {
		super.drawParty(item);
	}
	
	@Override
	protected void drawInfo() {
		super.drawInfo();
	}

	@Override
	protected void startingState() {
		showMessage("A battle is started between " + user.trainer.toString() + " and " + foe.trainer.toString() + "!");
		userStatus = user.status;
		foeStatus = foe.status;
		betPayout = calculatePayout(battleBet, choice - 1, simOdds);
	}

	public static int calculatePayout(int wager, int trainerGuess, int[] odds) {
		int americanOdds = UI.calculateOdds(odds, trainerGuess);
		
		if (americanOdds > 0) {
			return (int) Math.ceil(wager * (americanOdds / 100.0 + 1)); // Payout for underdogs
		} else {
			return (int) Math.ceil(wager * (100.0 / -americanOdds + 1)); // Payout for favorites
		}
	}

	public static String formatOdds(int americanOdds) {
		return String.format("Odds: %s%d", americanOdds >= 0 ? "+" : "", americanOdds);
	}
	
	public static int calculateParlayPayout(int[] parlayBets, double[] lines, int[] actualResults, int wager, int numCorrect) {
	    double baseMultiplier = 1.8; // Payout for individual parlays
	    double[] bonusMultipliers = {0.0, 0.0, 0.0, 0.5, 1.5, 2.0, 4.0}; // Bonus multipliers based on correct bets
	    int correctBets = 0;
	    int betsMade = 0;

	    for (int i = 0; i < parlayBets.length; i++) {
	        if (parlayBets[i] != 0) { // Player placed a bet
	            betsMade++;
	            if (lines == null) {
	            	correctBets = numCorrect > 0 ? numCorrect : 1;
	            	continue;
	            }
	            if (actualResults == null ||
	            		(parlayBets[i] > 0 && lines[i] < actualResults[i]) ||
	            		(parlayBets[i] < 0 && lines[i] > actualResults[i])) {
	                correctBets++;
	            }
	        }
	    }

	    // Base payout calculation
	    double payout = correctBets * baseMultiplier * wager;

	    // Apply bonus if applicable
	    if (betsMade > 0) {
	        payout += bonusMultipliers[correctBets] * wager;
	    }

	    return (int) Math.ceil(payout); // Round up to the nearest whole coin
	}

}
