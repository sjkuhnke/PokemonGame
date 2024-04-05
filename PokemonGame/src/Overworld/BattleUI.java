package Overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Swing.Ability;
import Swing.AbstractUI;
import Swing.Field;
import Swing.Move;
import Swing.Moveslot;
import Swing.Pokemon;
import Swing.Pokemon.Task;
import Swing.Status;

public class BattleUI extends AbstractUI {
	
	public int index;
	
	public Pokemon user;
	public Pokemon foe;
	public int userHP;
	public int foeHP;
	public int tempUserHP;
	public int tempFoeHP;
	
	public int subState = 0;
	public int dialogueState = DIALOGUE_FREE_STATE;
	public int moveNum = 0;
	private int dialogueCounter = 0;
	private int abilityCounter = 0;
	private int cooldownCounter = 0;
	private int hpCounter = 0;
	private Pokemon currentAbilityHost;
	private Ability currentAbility;
	public Task currentTask;
	public ArrayList<Task> tasks;
	private Field field;
	
	public boolean cancellableParty;

	public static final int STARTING_STATE = -1;
	public static final int IDLE_STATE = 0;
	public static final int TASK_STATE = 1;
	public static final int MOVE_SELECTION_STATE = 2;
	public static final int PARTY_SELECTION_STATE = 3;
	public static final int COOLDOWN_STATE = 9;
	public static final int END_STATE = 10;
	
	public static final int DIALOGUE_WAITING_STATE = 0;
	public static final int DIALOGUE_STATE = 1;
	public static final int DIALOGUE_FREE_STATE = 2;
	
	public BattleUI(GamePanel gp) {
		this.gp = gp;
		
		currentDialogue = "";
		commandNum = 0;
		tasks = new ArrayList<>();
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/marumonica.ttf");
			marumonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void showMessage(String message) {
		if (dialogueState != DIALOGUE_STATE) {
			if (dialogueState == DIALOGUE_FREE_STATE) {
				dialogueState = DIALOGUE_WAITING_STATE;
				currentDialogue = message;
			} else {
				dialogueCounter++;
				if (dialogueCounter >= 25) {
					dialogueCounter = 0;
					dialogueState = DIALOGUE_STATE;
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		this.g2 = g2;
		
		g2.setFont(marumonica);
		g2.setColor(Color.WHITE);
		
		g2.drawImage(setup("/battle/background", 1), 0, 0, gp.screenWidth, gp.screenHeight, null);
		
		// Sub states of the battle
		if (subState == STARTING_STATE) {
			showMessage("You are challenged by " + foe.trainer.getName() + "!");
		}
		
		if (subState == END_STATE) {
			showMessage(currentTask.message);
		}
		
		drawUser();
		drawFoe();
		
		if (subState == TASK_STATE) {
			taskState();
		}
		
		if (subState == COOLDOWN_STATE) {
			cooldownState();
		}
		
		if (currentAbility != null) {
			drawAbility();
		}
		
		if (subState != IDLE_STATE && subState != MOVE_SELECTION_STATE) {
			drawDialogueScreen(false);
		}
		
		if (subState == IDLE_STATE) {
			drawIdleScreen();
		}
		
		if (subState == MOVE_SELECTION_STATE) {
			drawMoveSelectionScreen();
		}
		
		if (subState == PARTY_SELECTION_STATE) {
			drawPartySelectionScreen();
		}
		
		// Dialogue States
		if (dialogueState == DIALOGUE_STATE) {
			drawDialogueState();
		}
	}
	
	private void endTask() {
		subState = COOLDOWN_STATE;
	}

	private void cooldownState() {
		currentTask = null;
		cooldownCounter++;
		if (cooldownCounter >= 25) {
			cooldownCounter = 0;
			subState = TASK_STATE;
		}
	}

	private void taskState() {
		if (!tasks.isEmpty() && currentTask == null) {
			currentTask = tasks.remove(0);
		}
		
		if (currentTask != null) {
			drawTask();
		}
		
		if (tasks.isEmpty() && currentTask == null) {
			subState = IDLE_STATE;
		}
	}

	private void drawAbility() {
		if (currentTask.type == Task.ABILITY) abilityCounter++;
		
		int x = gp.screenWidth - (4 * gp.tileSize);
		int y = gp.screenHeight - (6 * gp.tileSize);
		int width = gp.tileSize * 4;
		int height = gp.tileSize * 2;
		
		drawSubWindow(x, y, width, height);
		x += gp.tileSize * 2;
		y += gp.tileSize * 3 / 4;
		String topText = currentAbilityHost.nickname + "'s";
		g2.drawString(topText, this.getCenterAlignedTextX(topText, x), y);
		y += gp.tileSize * 3 / 4;
		String bottomText = currentAbility.toString() + ":";
		g2.drawString(bottomText, this.getCenterAlignedTextX(bottomText, x), y);
		
		if (abilityCounter == 25) {
			abilityCounter = 0;
			currentTask = null;
		}
	}

	private void drawTask() {
		if (currentTask.type == Task.TEXT) {
			showMessage(currentTask.message);
		} else if (currentTask.type == Task.DAMAGE) {
			currentDialogue = currentTask.message;
			hpCounter++;
			if (currentTask.p.playerOwned()) {
				if (userHP > currentTask.finish) userHP--;
				if (userHP < currentTask.finish) userHP++;
				if (userHP == currentTask.finish) {
					if (hpCounter >= 50) {
						hpCounter = 0;
						endTask();
					}
					
				}
			} else {
				if (foeHP > currentTask.finish) foeHP--;
				if (foeHP < currentTask.finish) foeHP++;
				if (foeHP == currentTask.finish) {
					if (hpCounter >= 50) {
						hpCounter = 0;
						endTask();
					}
				}
			}
		} else if (currentTask.type == Task.ABILITY) {
			currentAbilityHost = currentTask.p;
			currentAbility = currentTask.ability;
		} else if (currentTask.type == Task.SWAP_IN) {
			currentDialogue = currentTask.message;
			if (currentTask.p.playerOwned()) {
				user = currentTask.p;
				userHP = tempUserHP == 0 ? user.currentHP : tempUserHP;
				drawUserPokeball(true);
			} else {
				foe = currentTask.p;
				foeHP = tempFoeHP == 0 ? foe.currentHP : tempFoeHP;
				drawFoePokeball(true);
			}
			if (counter >= 100) {
				counter = 0;
				tempUserHP = 0;
				tempFoeHP = 0;
				currentTask = null;
			}
		} else if (currentTask.type == Task.SWAP_OUT) {
			currentDialogue = currentTask.message;
			if (currentTask.p.playerOwned()) {
				drawUserPokeball(false);
			} else {
				drawFoePokeball(false);
			}
			if (counter >= 100) {
				counter = 0;
				endTask();
			}
		} else if (currentTask.type == Task.FAINT) {
			currentDialogue = currentTask.message;
			counter++;
			if (counter >= 75) {
				currentTask.p.setVisible(false);
			}
			if (counter >= 100) {
				counter = 0;
				if (currentTask.p == user) {
					Pokemon.addTask(Task.PARTY, "");
				}
				endTask();
			}
		} else if (currentTask.type == Task.END) {
			subState = END_STATE;
		} else if (currentTask.type == Task.PARTY) {
			currentDialogue = "";
			cancellableParty = false;
			subState = PARTY_SELECTION_STATE;
		}
	}
	
	private void drawIdleScreen() {
		drawFoe();
		drawUser();
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawActionScreen(user);
	}

	private void drawUser() {
		if (!user.isVisible()) return;
		drawHPImage(user);
		drawHPBar(user, userHP);
		drawNameLabel(user);
		drawUserSprite();
		drawUserHP(userHP);	
	}

	private void drawFoe() {
		if (!foe.isVisible()) return;
		drawHPImage(foe);
		drawHPBar(foe, foeHP);
		drawNameLabel(foe);
		drawFoeSprite();
	}

	private void drawHPImage(Pokemon p) {
		if (p.playerOwned()) {
			g2.drawImage(setup("/battle/user_hp", 1), 302, 280, null);
		} else {
			g2.drawImage(setup("/battle/foe_hp", 1), 222, 39, null);
		}		
	}

	private void drawUserHP(int hp) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		String hpLabel = hp + " / " + user.getStat(0);
		g2.drawString(hpLabel, this.getCenterAlignedTextX(hpLabel, 490), 362);
		
	}

	private void drawDialogueState() {
		int x = gp.screenWidth - gp.tileSize;
		int y = gp.screenHeight - gp.tileSize;
		int width = gp.tileSize / 2;
		int height = gp.tileSize / 2;
		g2.setColor(Color.WHITE);
		g2.fillPolygon(new int[] {x, (x + width), (x + width / 2)}, new int[] {y, y, y + height}, 3);
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			switch(subState) {
			case STARTING_STATE:
			    setStartingTasks();
				subState = TASK_STATE;
				break;
			case END_STATE:
				if (tasks.isEmpty()) {
					user.setVisible(false);
					gp.endBattle(index, -1);
					gp.gameState = GamePanel.PLAY_STATE;
				}
			}
			dialogueState = DIALOGUE_FREE_STATE;
			currentDialogue = "";
			currentTask = null;
			currentAbility = null;
		}
	}
	
	private void setStartingTasks() {
		field = new Field();
	    Pokemon.field = field;
	    userHP = user.currentHP;
	    foeHP = foe.currentHP;
		
		Pokemon.addSwapInTask(foe);
	    Pokemon.addSwapInTask(user);
	    Pokemon fasterInit = user.getFaster(foe, 0, 0);
		Pokemon slowerInit = fasterInit == user ? foe : user;
		fasterInit.swapIn(slowerInit, true);
		slowerInit.swapIn(fasterInit, true);
	}

	private void drawFoePokeball(boolean arriving) {
		counter++;
		if (counter < 50) {
			if (arriving) g2.drawImage(setup("/items/pokeball", 2), 570, 188, null);
		} else {
			if (arriving) {
				foe.setVisible(true);
			} else {
				currentTask.p.setVisible(false);
				g2.drawImage(setup("/items/pokeball", 2), 570, 188, null);
			}
		}
	}
	
	private void drawUserPokeball(boolean arriving) {
		counter++;
		if (counter < 50) {
			if (arriving) g2.drawImage(setup("/items/pokeball", 2), 133, 348, null);
		} else {
			if (arriving) {
				user.setVisible(true);
			} else {
				currentTask.p.setVisible(false);
				g2.drawImage(setup("/items/pokeball", 2), 133, 348, null);
			}
		}
	}
	
	private void drawFoeSprite() {
		g2.drawImage(getSprite(foe), 515, 70, null);
	}
	
	private void drawUserSprite() {
		g2.drawImage(getSprite(user), 80, 235, null);
	}

	private void drawHPBar(Pokemon p, double amt) {
		double hpRatio = amt / p.getStat(0);
		g2.setColor(getHPBarColor(hpRatio));
		int x;
		int y;
		int width = (int) (hpRatio * 120);
		int height = 8;
		if (p.playerOwned()) {
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
	
	private void drawNameLabel(Pokemon p) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int x;
		int y;
		int levelX;
		int levelY;
		String name = p.nickname;
		
		if (p.playerOwned()) {
			x = getRightAlignedTextX(name, 490);
			y = 318;
			levelX = 523;
			levelY = 318;
		} else {
			x = getRightAlignedTextX(name, 383);
			y = 70;
			levelX = 416;
			levelY = 70;
		}
		g2.drawString(p.nickname, x, y);
		g2.drawString(p.level + "", levelX, levelY);
	}

	@SuppressWarnings("unused") // DEBUG
	private String getStateName() {
		switch (subState) {
		case STARTING_STATE: return "Starting State";
		case IDLE_STATE: return "Idle State";
		case TASK_STATE: return "Task State";
		case MOVE_SELECTION_STATE: return "Move Select State";
		case PARTY_SELECTION_STATE: return "Party Select State";
		case COOLDOWN_STATE: return "Cooldown State";
		case END_STATE: return "End State";
		default: return "Not added to getStateName() yet";
		}
		
	}
	
	@SuppressWarnings("unused") // DEBUG
	private String getDialogueStateName() {
		switch (dialogueState) {
		case DIALOGUE_FREE_STATE: return "Dialogue Free State";
		case DIALOGUE_WAITING_STATE: return "Dialogue Waiting State";
		case DIALOGUE_STATE: return "Dialogue State";
		default: return "Not added to getDialogueStateName() yet";
		}
		
	}
	
	private Image getSprite(Pokemon p) {
		Image originalSprite = p.getSprite();
		
		int scaledWidth = originalSprite.getWidth(null) * 2;
		int scaledHeight = originalSprite.getHeight(null) * 2;
		
		Image scaledImage = originalSprite.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		
		if (p.playerOwned()) scaledImage = flipHorizontal(scaledImage);
		
		return scaledImage;
	}

	private Image flipHorizontal(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = bufferedImage.createGraphics();

	    // Flip the image horizontally by drawing it with negative width
	    graphics.drawImage(image, image.getWidth(null), 0, -image.getWidth(null), image.getHeight(null), null);
	    graphics.dispose();

	    return bufferedImage;
	}
	
	private void drawActionScreen(Pokemon p) {
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
		g2.drawString("FIGHT", x + 30, y + 50);
		if (commandNum == 0) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				subState = MOVE_SELECTION_STATE;
			}
		}
		
		x += width + gp.tileSize;
		g2.setColor(Color.GREEN.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("PARTY", x + 30, y + 50);
		if (commandNum == 1) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				currentDialogue = "";
				cancellableParty = true;
				subState = PARTY_SELECTION_STATE;
			}
		}
		
		x = gp.tileSize * 7;
		y += height + 16;
		g2.setColor(Color.YELLOW.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("BAG", x + 30, y + 50);
		if (commandNum == 2) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
		}
		
		x += width + gp.tileSize;
		g2.setColor(Color.BLUE.darker());
		g2.fillRoundRect(x, y, width, height, 10, 10);
		g2.setColor(Color.WHITE);
		g2.drawString("RUN", x + 30, y + 50);
		if (commandNum == 3) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
		}
	}
	
	private void drawMoveSelectionScreen() {
		drawFoe();
		drawUser();
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawMoves();
	}
	
	private void drawPartySelectionScreen() {
		drawParty();
	}

	private void drawActionBackground(Pokemon p, int x, int y, int width, int height) {
		Color background = new Color(175, 175, 175);
		g2.setColor(background);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		Color border = p.type1.getColor();
		g2.setColor(border);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
	}
	
	private void drawMoves() {
		int x = gp.tileSize * 6;
		int y = gp.screenHeight - (gp.tileSize * 4);
		int width = gp.screenWidth - x;
		int height = gp.tileSize * 4;
		
		drawActionBackground(user, x, y, width, height);
		
		x += gp.tileSize;
		y += gp.tileSize / 4;
		width = (int) (gp.tileSize * 3.5);
		height = (int) (gp.tileSize * 1.5);
		g2.setFont(g2.getFont().deriveFont(24F));
		Moveslot[] moves = user.moveset;
		for (int i = 0; i < moves.length; i++) {
			if (moves[i] != null) {
		        if (i == 2) {
		            x = gp.tileSize * 7;
		            y += height + 16;
		        } else if (i != 0) {
		            x += width + gp.tileSize;
		        }

		        g2.setColor(moves[i].move.mtype.getColor());
		        g2.fillRoundRect(x, y, width, height, 10, 10);
		        g2.setColor(Color.BLACK);
		        String text = moves[i].move.toString();
		        g2.drawString(text, getCenterAlignedTextX(text, (x + width / 2)), y + 30);
		        g2.setColor(moves[i].getPPColor());
		        String pp = moves[i].currentPP + " / " + moves[i].maxPP;
		        g2.drawString(pp, getCenterAlignedTextX(pp, (x + width / 2)), y + 55);
		        if (moveNum == i) {
		            g2.setColor(Color.WHITE);
		            g2.drawRoundRect(x, y, width, height, 10, 10);
		            if (gp.keyH.wPressed) {
		            	turn(moves[i].move, foe.trainerOwned() ? foe.bestMove(user, user.getFaster(foe, 0, 0) == foe) : foe.randomMove());
		            }
		        }
		    }
		}
	}
	
	public void turn(Move uMove, Move fMove) {
		if (user.isFainted() || foe.isFainted()) return; // TODO: maybe let the move method handle this?

		// Priority stuff
		int uP, fP;
		uP = uMove.priority;
		fP = fMove.priority;
		if (user.ability == Ability.PRANKSTER && uMove.cat == 2) ++uP;
		if (foe.ability == Ability.PRANKSTER && fMove.cat == 2) ++fP;
		
		if (user.ability == Ability.STEALTHY_PREDATOR && user.impressive) ++uP;
		if (foe.ability == Ability.STEALTHY_PREDATOR && foe.impressive) ++fP;
		
		uP = user.checkQuickClaw(uP);
		fP = foe.checkQuickClaw(fP);
		uP = user.checkCustap(uP);
		fP = foe.checkCustap(fP);
		
		Pokemon faster = user.getFaster(foe, uP, fP);
		
		Pokemon slower = faster == user ? foe : user;
		
		boolean foeCanMove = true;
		
		if (faster == user) { // player Pokemon is faster
			if (slower.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				slower = foe.trainer.swapOut(user, fMove, false);
				tempFoeHP = slower.currentHP;
				foeCanMove = false;
			}
			
			if (uMove == Move.SUCKER_PUNCH && fMove.cat == 2) uMove = Move.FAILED_SUCKER;
			faster.move(slower, uMove, true);
			
			// Check for swap (player)
			if (user.trainer.hasValidMembers() && faster.vStatuses.contains(Status.SWITCHING)) {
				Pokemon.addTask(Task.PARTY, ""); // TODO: update faster with new Pokemon sent in
				//tempUserHP = faster.currentHP;
			}
			
			if (fMove == Move.SUCKER_PUNCH) fMove = Move.FAILED_SUCKER; // TODO: make move method check if you're faster and if not make Sucker Punch fail (move() has an argument for this)
	        if (!(foe.trainer != null && slower != foe.trainer.getCurrent()) && foeCanMove) {
	        	slower.move(faster, fMove, false);
	        	slower = foe.trainer.getCurrent();
	        }
	        
	        // Check for swap (AI)
	        if (foe.trainer != null && foe.trainer.hasValidMembers() && foeCanMove && slower.vStatuses.contains(Status.SWITCHING)) {
	        	slower = foe.trainer.swapOut(faster, null, slower.lastMoveUsed == Move.BATON_PASS);
	        	tempFoeHP = slower.currentHP;
	        }
		} else { // enemy Pokemon is faster
			if (faster.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				faster = foe.trainer.swapOut(slower, fMove, false);
				tempFoeHP = faster.currentHP;
				foeCanMove = false;
			}
			if (fMove == Move.SUCKER_PUNCH && fMove.cat == 2) fMove = Move.FAILED_SUCKER; // TODO: make move method check if you're faster and if not make Sucker Punch fail (move() has an argument for this)
			if (foeCanMove) {
				faster.move(slower, fMove, true);
				if (foe.trainer != null) faster = foe.trainer.getCurrent();
			}
			// Check for swap (AI)
	        if (foe.trainer != null && foe.trainer.hasValidMembers() && foeCanMove && faster.vStatuses.contains(Status.SWITCHING)) {
	        	faster = foe.trainer.swapOut(slower, null, faster.lastMoveUsed == Move.BATON_PASS);
	        	tempFoeHP = faster.currentHP;
	        }
			
	        if (slower == user.trainer.getCurrent()) slower.move(faster, uMove, false);
	        // Check for swap
	        if (user.trainer.hasValidMembers() && faster.vStatuses.contains(Status.SWITCHING)) {
	        	Pokemon.addTask(Task.PARTY, ""); // TODO: fix, update slower with new Pokemon sent in
	        	//tempUserHP = slower.currentHP;
			}
		}
	    subState = TASK_STATE;
		if (foe.trainer != null) {
			foe = foe.trainer.getCurrent();
		}
		if (hasAlive()) faster.endOfTurn(slower);
		if (hasAlive()) slower.endOfTurn(faster);
		if (hasAlive()) field.endOfTurn();
		Pokemon next = foe;
		while (next.isFainted()) {
			if (foe.trainer != null) {
				if (foe.trainer.hasNext()) {
					next = foe.trainer.next(user);
					Pokemon.addSwapInTask(next);
					next.swapIn(user, true);
					user.getPlayerTrainer().clearBattled();
					user.battled = true;
					
				} else {
					user.getPlayerTrainer().setMoney(user.getPlayerTrainer().getMoney() + foe.trainer.getMoney());
					Pokemon.addTask(Task.END, foe.trainer.getName() + " was defeated!\nWon $" + foe.trainer.getMoney() + "!");
		            if (foe.trainer.getMoney() == 500 && user.getPlayerTrainer().badges < 8) {
		            	user.getPlayerTrainer().badges++;
		            	for (Pokemon p : user.trainer.team) {
		            		if (p != null) p.awardHappiness(15, true);
		            	}
		            	user.getPlayerTrainer().updateHappinessCaps();
		            }
		            if (foe.trainer.getItem() != null) {
		            	user.getPlayerTrainer().bag.add(foe.trainer.getItem());
		            	Pokemon.addTask(Task.END, "\nYou were given " + foe.trainer.getItem().toString() + "!");
		            }
		            if (foe.trainer.getFlagIndex() != 0) {
		            	user.getPlayerTrainer().flags[foe.trainer.getFlagIndex()] = true;
		            }
		            break;
				}
			} else {
				showMessage(foe.name + " was defeated!");
				break;
			}
		}
	    if (user.getPlayerTrainer().wiped()) {
			wipe();
		}
	}
	
	private boolean hasAlive() {
		if (!foe.isFainted()) return true;
		
		if (foe.trainer != null && foe.trainer.getTeam() != null) {
			if (foe.trainer.hasNext()) return true;
		}

		return false;
	}
	
	private void wipe() {
		user.getPlayerTrainer().setMoney(user.getPlayerTrainer().getMoney() - 500);
		Pokemon.addTask(Task.END, "You have no more Pokemon that can fight!");
		Pokemon.addTask(Task.END, "You lost $500!");
		gp.eHandler.teleport(0, 79, 46, false);
		user.trainer.heal();
		if (foe.trainer != null) {
			foe.trainer.heal();
			foe.trainer.setCurrent(foe.trainer.getTeam()[0]);
		}
	}
	
	@Override
	public void drawParty() {
		super.drawParty();
		currentTask = null;
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Pokemon select = gp.player.p.team[partyNum];
			if (select.isFainted()) {
				currentDialogue = select.nickname + " has no energy to battle!";
			} else if (select == select.trainer.getCurrent()) {
				currentDialogue = select.nickname + " is already out!";
			} else {
				subState = TASK_STATE;
				gp.player.p.swapToFront(gp.player.p.team[partyNum], partyNum);
				gp.player.p.getCurrent().swapIn(foe, true);
				partyNum = 0;
				dialogueCounter = 0;
			}
		}
		if (!currentDialogue.equals("")) {
			drawDialogueScreen(false);
			dialogueCounter++;
			if (dialogueCounter >= 50) {
				dialogueCounter = 0;
				currentDialogue = "";
			}
		}
	}

}
