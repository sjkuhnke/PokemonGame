package overworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import entity.Entity;
import pokemon.Ability;
import pokemon.AbstractUI;
import pokemon.Field;
import pokemon.Item;
import pokemon.Move;
import pokemon.Moveslot;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Status;
import pokemon.Bag.Entry;
import pokemon.Field.Effect;
import pokemon.Field.FieldEffect;
import pokemon.Pokemon.Task;

public class BattleUI extends AbstractUI {
	
	// Index of trainer and static Pokemon
	public int index;
	public int staticID;
	
	// User and foe
	public Pokemon user;
	public Pokemon foe;
	
	// Pointers for tasks
	public Pokemon tempUser;
	public int userHP;
	public int foeHP;
	public int maxUserHP;
	public Status userStatus;
	public Status foeStatus;
	public int userExp;
	public int userExpMax;
	public int userLevel;
	public Move foeMove;
	public FieldEffect weather;
	public FieldEffect terrain;
	private Pokemon currentAbilityHost;
	private Ability currentAbility;
	public int foeFainted;
	
	public Entry ball;
	public ArrayList<Entry> balls;
	
	public int subState = 0;
	public int dialogueState = DIALOGUE_FREE_STATE;
	public int moveNum = 0;
	private int dialogueCounter = 0;
	private int abilityCounter = 0;
	private int cooldownCounter = 0;
	private Field field;
	private boolean baton;
	
	public boolean cancellableParty;
	public boolean showMoveSummary;
	public boolean showFoeSummary;
	public Pokemon foeSummary;
	
	private BufferedImage battle;
	private BufferedImage userHPBar;
	private BufferedImage foeHPBar;
	private BufferedImage currentIcon;
	private BufferedImage faintedIcon;
	private BufferedImage emptyIcon;

	public static final int STARTING_STATE = -1;
	public static final int IDLE_STATE = 0;
	public static final int TASK_STATE = 1;
	public static final int MOVE_SELECTION_STATE = 2;
	public static final int PARTY_SELECTION_STATE = 3;
	public static final int SUMMARY_STATE = 4;
	public static final int INFO_STATE = 5;
	public static final int MOVE_MESSAGE_STATE = 6;
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
		
		battle = setup("/battle/background", 1);
		userHPBar = setup("/battle/user_hp", 1);
		foeHPBar = setup("/battle/foe_hp", 1);
		ballIcon = setup("/icons/ball", 1);
		currentIcon = setup("/icons/ballcurrent", 1);
		faintedIcon = setup("/icons/ballfainted", 1);
		emptyIcon = setup("/icons/empty", 1);
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
		
		g2.drawImage(battle, 0, 0, gp.screenWidth, gp.screenHeight, null);
		
		if (weather != null) {
			g2.drawImage(weather.effect.getImage(), 0, 0, gp.screenWidth, gp.screenHeight, null);
		}
		if (terrain != null) {
			g2.drawImage(terrain.effect.getImage(), 0, 0, gp.screenWidth, gp.screenHeight, null);
		}
		
		// Sub states of the battle
		if (subState == STARTING_STATE) {
			startingState();
		}
		
		if (subState == END_STATE) {
			showMessage(currentTask.message);
		}
		
		drawUser();
		drawUserParty();
		drawFoe();
		drawFoeParty();
		
		if (subState == TASK_STATE) {
			taskState();
		}
		
		if (subState == COOLDOWN_STATE) {
			cooldownState();
		}
		
		if (currentAbility != null) {
			drawAbility();
		}
		
		if (subState != IDLE_STATE && subState != MOVE_SELECTION_STATE && subState != INFO_STATE && (currentTask == null || currentTask.type != Task.MOVE)) {
			drawDialogueScreen(false);
		}
		
		if (subState == MOVE_SELECTION_STATE) {
			drawMoveSelectionScreen();
		}
		
		if (subState == PARTY_SELECTION_STATE) {
			drawPartySelectionScreen();
		}
		
		if (subState == IDLE_STATE) {
			drawIdleScreen();
		}
		
		if (subState == SUMMARY_STATE) {
			drawSummary(foe);
		}
		
		if (subState == INFO_STATE) {
			drawInfo();
		}
		
		if (subState == MOVE_MESSAGE_STATE) {
			showMessage(currentDialogue);
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
		currentAbility = null;
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
		if (currentTask == null) {
			abilityCounter = 0;
			currentAbility = null;
			return;
		}
		
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
		switch (currentTask.type) {
		case Task.TEXT:
			String message = currentTask.message.contains("\n") ? currentTask.message : Item.breakString(currentTask.message, 55);
			showMessage(message);
			break;
		case Task.DAMAGE:
			if (!currentTask.p.isVisible()) {
				currentTask = null;
				return;
			}
			currentDialogue = currentTask.message;
			if (currentTask.wipe) currentTask.evo.spriteVisible = true;
			if (currentTask.p.playerOwned()) {
				if (userHP > currentTask.finish) userHP--;
				if (userHP < currentTask.finish) userHP++;
				if (userHP == currentTask.finish) {
					endTask();
				}
			} else {
				if (foeHP > currentTask.finish) foeHP--;
				if (foeHP < currentTask.finish) foeHP++;
				if (foeHP == currentTask.finish) {
					endTask();
				}
			}
			break;
		case Task.ABILITY:
			currentAbilityHost = currentTask.p;
			currentAbility = currentTask.ability;
			break;
		case Task.SWAP_IN:
			currentDialogue = currentTask.message;
			if (currentTask.p.playerOwned()) {
				user = currentTask.p;
				userHP = tempUser == null ? user.currentHP : tempUser.currentHP;
				maxUserHP = tempUser == null ? user.getStat(0) : tempUser.getStat(0);
				userStatus = tempUser == null ? user.status : tempUser.status;
				userExp = tempUser == null ? user.exp : tempUser.exp;
				userExpMax = tempUser == null ? user.expMax : tempUser.expMax;
				userLevel = tempUser == null ? user.level : tempUser.level;
				drawUserPokeball(true);
			} else {
				foe = currentTask.p;
				foeHP = currentTask.start == -1 ? foe.currentHP : currentTask.start;
				foeStatus = currentTask.status;
				drawFoePokeball(true);
			}
			if (counter >= 100) {
				counter = 0;
				if (currentTask.p.playerOwned()) moveNum = 0;
				tempUser = null;
				currentTask = null;
			}
			break;
		case Task.SWAP_OUT:
			currentDialogue = currentTask.message;
			if (currentTask.p.playerOwned()) {
				drawUserPokeball(false);
			} else if (currentTask.p.trainerOwned()) {
				drawFoePokeball(false);
			} else {
				counter++;
			}
			if (counter >= 50) {
				counter = 0;
				endTask();
			}
			break;
		case Task.FAINT:
			currentDialogue = currentTask.message;
			counter++;
			if (counter >= 75) {
				currentTask.p.setVisible(false);
				if (foe.trainerOwned()) foeFainted = foe.trainer.getNumFainted();
			}
			if (counter >= 100) {
				counter = 0;
				if (currentTask.p == user && hasAlive()) {
					Pokemon.addTask(Task.PARTY, "");
				}
				endTask();
			}
			break;
		case Task.END:
			if (tasks.size() != 0 && !currentTask.wipe) {
				tasks.add(currentTask);
				currentTask = null;
				return;
			}
			if (currentTask.wipe) wipe();
			subState = END_STATE;
			break;
		case Task.PARTY:
			currentDialogue = "";
			cancellableParty = false;
			baton = currentTask.wipe;
			subState = PARTY_SELECTION_STATE;
			break;
		case Task.STATUS:
			showMessage(currentTask.message);
			if (currentTask.p.playerOwned()) {
				userStatus = currentTask.status;
			} else {
				foeStatus = currentTask.status;
			}
			break;
		case Task.CATCH:
			drawFoePokeball(false, currentTask.item);
			if (counter >= 75) {
				counter = 0;
				if (currentTask.wipe) {
					user.getPlayer().catchPokemon(foe, currentTask.item);
					setNicknaming(true);
				} else {
					currentTask.p.setVisible(true);
					Pokemon.addTask(Task.TEXT, "Oh no, " + foe.name + " broke free!");
					turn(null, foe.randomMove());
					ball = gp.player.p.getBall(ball);
					balls = gp.player.p.getBalls();
				}
				currentTask = null;
			}
			break;
		case Task.NICKNAME:
			currentDialogue = currentTask.message;
			setNickname();
			if (nicknaming == 0) {
				if (gp.keyH.wPressed) {
					gp.keyH.wPressed = false;
					foe.nickname = nickname.toString().trim();
					nickname = new StringBuilder();
					if (foe.nickname == null || foe.nickname.trim().isEmpty()) foe.nickname = foe.name;
					nicknaming = -1;
					currentTask = null;
				}
			}
			break;
		case Task.EXP:
			currentDialogue = currentTask.message;
			if (currentTask.p.isVisible()) {
				if (userExp < currentTask.finish) userExp++;
				if (userExp == currentTask.finish) {
					endTask();
				}
			} else {
				endTask();
			}
			break;
		case Task.LEVEL_UP:
			currentDialogue = currentTask.message;
			if (currentTask.p.isVisible()) {
				userLevel++;
				userExp = user.exp;
				userExpMax = user.expMax;
				userHP += currentTask.finish;
				maxUserHP += currentTask.finish;
			}
			endTask();
			break;
		case Task.MOVE:
			drawMoveOptions(currentTask.move, currentTask.p);
			break;
		case Task.EVO:
			drawEvolution(currentTask);
			break;
		case Task.WEATHER:
			showMessage(currentTask.message);
			weather = currentTask.fe;
			break;
		case Task.TERRAIN:
			showMessage(currentTask.message);
			terrain = currentTask.fe;
			break;
		case Task.SPRITE:
			foe.setSprites();
			currentTask = null;
			break;
		case Task.SEMI_INV:
			showMessage(Item.breakString(currentTask.message, 55));
			currentTask.p.spriteVisible = currentTask.wipe;
			break;
		}
	}
	
	private void drawIdleScreen() {
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawActionScreen(user);
		drawCalcWindow();
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Item.useCalc(user.getPlayer(), null);
		}
		drawCatchWindow();
		if (!showFoeSummary) drawToolTips("OK", null, null, "Foe");
	}

	private void drawUser() {
		if (!user.isVisible()) return;
		drawHPImage(user);
		drawHPBar(user, userHP, maxUserHP);
		drawNameLabel(user);
		if (user.spriteVisible) drawUserSprite();
		drawUserHP();
		drawStatus(user);
		drawTypes(user);
		drawExpBar();
	}

	private void drawFoe() {
		if (!foe.isVisible()) return;
		drawHPImage(foe);
		drawHPBar(foe, foeHP, foe.getStat(0));
		drawNameLabel(foe);
		if (foe.spriteVisible) drawFoeSprite();
		drawStatus(foe);
		drawTypes(foe);
		drawCaughtIndicator();
	}

	private void drawCaughtIndicator() {
		if (foe.trainerOwned() || user.getPlayer().pokedex[foe.id] != 2) return;
		g2.drawImage(ballIcon, 454, 50, null);
	}
	
	private void drawUserParty() {
		int x = 10;
		int y = 10;
		int width = 20;
		for (Pokemon p : user.getPlayer().getTeam()) {
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
	
	private void drawFoeParty() {
		if (!foe.trainerOwned()) return;
		int x = gp.screenWidth - 130;
		int y = 10;
		int width = 20;
		
		int yellowIndex = foeFainted;
		for (int i = 0; i < 6; i++) {
		   BufferedImage image = ballIcon;

		    if (i < foe.trainer.getTeam().length) {
	            if (i < yellowIndex) {
	                image = faintedIcon;
	            }
	            if (i == yellowIndex && foe.isVisible()) {
	            	image = currentIcon;
	            }
		    } else {
		    	image = emptyIcon;
		    }
		    g2.drawImage(image, x, y, null);
            x += width;
		}
	}

	private void drawStatus(Pokemon p) {
		if (p.playerOwned()) {
			if (userStatus != Status.HEALTHY) g2.drawImage(userStatus.getImage(), 339, 326, null);
		} else {
			if (foeStatus != Status.HEALTHY) g2.drawImage(foeStatus.getImage(), 232, 78, null);
		}
	}

	private void drawTypes(Pokemon p) {
		PType[] types = Pokemon.getTypes(p.id);
		if (p.playerOwned()) {
			g2.drawImage(types[0].getImage(), 340, 298, null);
			if (types[1] != null) g2.drawImage(types[1].getImage(), 364, 298, null);
		} else {
			g2.drawImage(types[0].getImage(), 232, 50, null);
			if (types[1] != null) g2.drawImage(types[1].getImage(), 256, 50, null);
		}
	}

	private void drawHPImage(Pokemon p) {
		if (p.playerOwned()) {
			g2.drawImage(userHPBar, 302, 280, null);
		} else {
			g2.drawImage(foeHPBar, 222, 39, null);
		}
	}

	private void drawUserHP() {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		String hpLabel = userHP + " / " + maxUserHP;
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
					gp.endBattle(index, staticID);
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
	
	private void setStartingTasks() {
		field = new Field();
	    Pokemon.field = field;
	    userHP = user.currentHP;
	    maxUserHP = user.getStat(0);
	    foeHP = foe.currentHP;
		
		if (foe.trainerOwned() && staticID == -1) {
			Pokemon.addSwapInTask(foe);
			foeFainted = foe.trainer.getNumFainted();
		}
		if (staticID == 162) {
			Pokemon.addTask(Task.TEXT, foe.nickname + "'s aura is radiating!");
	    	for (int i = 0; i < 5; i++) {
	    		foe.stat(foe, i, 1, new Pokemon(1, 1, false, false));
	    	}
	    }
	    Pokemon.addSwapInTask(user);
	    Pokemon fasterInit = user.getFaster(foe, 0, 0);
		Pokemon slowerInit = fasterInit == user ? foe : user;
		fasterInit.swapIn(slowerInit, true);
		slowerInit.swapIn(fasterInit, true);
	}

	private void drawFoePokeball(boolean arriving, Item ballType) {
		counter++;
		if (arriving) {
			if (counter < 50) {
				if (arriving) g2.drawImage(ballType.getImage2(), 570, 188, null);
			} else {
				foe.setVisible(true);
				foe.spriteVisible = true;
			}
		} else {
			currentTask.p.setVisible(false);
			g2.drawImage(ballType.getImage2(), 570, 188, null);
		}
	}
	
	private void drawFoePokeball(boolean arriving) {
		drawFoePokeball(arriving, Item.POKEBALL);
	}
	
	private void drawUserPokeball(boolean arriving) {
		counter++;
		if (arriving) {
			if (counter < 50) {
				g2.drawImage(Item.POKEBALL.getImage2(), 133, 348, null);
			} else {
				user.setVisible(true);
				user.spriteVisible = true;
			}
		} else {
			currentTask.p.setVisible(false);
			g2.drawImage(Item.POKEBALL.getImage2(), 133, 348, null);
		}
	}
	
	private void drawFoeSprite() {
		g2.drawImage(foe.getFrontSprite(), 515, 70, null);
	}
	
	private void drawUserSprite() {
		g2.drawImage(user.getBackSprite(), 80, 235, null);
	}

	private void drawHPBar(Pokemon p, double amt, double maxHP) {
		double hpRatio = amt / maxHP;
		g2.setColor(getHPBarColor(hpRatio));
		int x;
		int y;
		int width = amt == 0 ? 0 : Math.max((int) (hpRatio * 120), 1);
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
	
	private void drawExpBar() {
		double xpRatio = userExp * 1.0 / userExpMax;
		int x = 386;
		int y = 370;
		int width = (int) (xpRatio * 160);
		int height = 5;
		g2.setColor(new Color(30, 60, 200, 200));
		g2.fillRect(x, y, width, height);
		
	}
	
	private void drawNameLabel(Pokemon p) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int x;
		int y;
		int levelX;
		int levelY;
		String name = p.nickname;
		g2.setFont(g2.getFont().deriveFont(getFontSize(name, gp.tileSize * 2.5F)));
		
		if (p.playerOwned()) {
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
		g2.drawString(p.nickname, x, y);
		int level = p.playerOwned() ? userLevel : p.level;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(level + "", levelX, levelY);
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
			if (gp.keyH.wPressed && !showFoeSummary) {
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
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.resetKeys();
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
		g2.drawString("RUN", x + 30, y + 50);
		if (commandNum == 3) {
			g2.drawRoundRect(x, y, width, height, 10, 10);
			if (gp.keyH.wPressed && !showFoeSummary) {
				gp.keyH.wPressed = false;
				subState = TASK_STATE;
				if (foe.trainerOwned()) {
					Pokemon.addTask(Task.TEXT, "No! There's no running from a trainer battle!");
				} else {
					Pokemon faster = user.getFaster(foe, 0, 0);
					boolean isFaster = faster == user || user.item == Item.SHED_SHELL;
					
					if (isFaster || new Random().nextBoolean()) {
						Pokemon.addTask(Task.END, "Got away safely!");
					} else {
						Pokemon.addTask(Task.TEXT, "Couldn't escape!");
						foeMove = foe.randomMove();
						turn(null, foeMove);
					}
				}
			}
		}
		
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			if (foeSummary == null && !showFoeSummary) foeSummary = foe;
			if (showFoeSummary) foeSummary = null;
			moveSummaryNum = -1;
			showFoeSummary = !showFoeSummary;
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
				if (moveSummaryNum < 0) {
					int currentIndex = foeSummary.trainer.indexOf(foeSummary);
					currentIndex = (currentIndex - 1 + foeSummary.trainer.team.length) % foeSummary.trainer.team.length;
					foeSummary = foeSummary.trainer.team[currentIndex];
				}
			}
			if (gp.keyH.rightPressed) {
				gp.keyH.rightPressed = false;
				if (moveSummaryNum < 0) {
					int currentIndex = foeSummary.trainer.indexOf(foeSummary);
					currentIndex = (currentIndex + 1) % foeSummary.trainer.team.length;
					foeSummary = foeSummary.trainer.team[currentIndex];
				}
			}
		}
	}
	
	private void drawFoeSummaryParty() {
		if (!foe.trainerOwned()) return;
		int windowWidth = gp.tileSize * 3;
		
		int x = gp.screenWidth / 2 - windowWidth / 2;
		int y = 0;
		int width = 20;
		int height = gp.tileSize / 2;
		
		Color background = new Color(0, 0, 0, 200);
		g2.setColor(background);
		g2.fillRoundRect(x, y, windowWidth, height, 10, 10);
		
		Color border = new Color(255, 255, 255);
		g2.setColor(border);
		g2.setStroke(new BasicStroke(2));
		g2.drawRoundRect(x+3, y+3, windowWidth-6, height-6, 8, 8);
		
		g2.setStroke(new BasicStroke(5));
		
		x += gp.tileSize / 4;
		y += 4;
		
		int yellowIndex = foeSummary.trainer.indexOf(foeSummary);
		for (int i = 0; i < 6; i++) {
		   BufferedImage image = ballIcon;

		    if (i < foe.trainer.getTeam().length) {
	            if (foeSummary.trainer.team[i].isFainted()) {
	                image = faintedIcon;
	            }
	            if (i == yellowIndex && foe.isVisible()) {
	            	image = currentIcon;
	            }
		    } else {
		    	image = emptyIcon;
		    }
		    g2.drawImage(image, x, y, null);
            x += width;
		}
		
	}

	private void drawMoveSelectionScreen() {
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawMoves();
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Item.useCalc(user.getPlayer(), null);
		}
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			showMoveSummary = !showMoveSummary;
		}
		drawCalcWindow();
		drawToolTips("OK", null, "Back", "Info");
	}
	
	private void drawCalcWindow() {
		g2.setFont(g2.getFont().deriveFont(24F));
		int x = gp.screenWidth - (gp.tileSize * 2);
		int y = (int) (gp.screenHeight - (gp.tileSize * 5.5));
		int width = gp.tileSize * 2;
		int height = (int) (gp.tileSize * 1.5);
		
		drawSubWindow(x, y, width, height, 125);
		x += gp.tileSize / 4;
		y += gp.tileSize / 4;
		g2.drawImage(Item.CALCULATOR.getImage2(), x, y, null);
		
		x += gp.tileSize;
		y += gp.tileSize * 0.75;
		g2.drawString("[A]", x, y);
		
	}
	
	private void drawCatchWindow() {
		if (foe.trainerOwned() && staticID < 0) return;
		g2.setFont(g2.getFont().deriveFont(24F));
		int x = gp.screenWidth - (gp.tileSize * 4);
		int y = (int) (gp.screenHeight - (gp.tileSize * 5.5));
		int width = gp.tileSize * 2;
		int height = (int) (gp.tileSize * 1.5);
		
		drawSubWindow(x, y, width, height, 125);
		x += gp.tileSize / 2;
		y += gp.tileSize / 4;
		if (ball != null) {
			g2.drawImage(scaleImage(ball.getItem().getImage(), 2), x, y, null);
			g2.drawString(ball.getCount() + "", getRightAlignedTextX(ball.getCount() + "", x + 44), y + 42);
		}
		if (commandNum < 0) {
			g2.drawRoundRect(x, y, gp.tileSize, gp.tileSize, 12, 12);
			if (gp.keyH.wPressed) {
				gp.keyH.wPressed = false;
				if (ball != null) {
					Pokemon.addTask(Task.TEXT, "You threw a " + ball.getItem().toString() + "!");
					Task t = Pokemon.addTask(Task.CATCH, "", foe);
					t.item = ball.getItem();
					t.setWipe(user.getCapture(foe, ball));
					subState = TASK_STATE;
				}
			}
		}
		g2.setColor(Color.WHITE);
		y += gp.tileSize * 0.75;
		if (balls.size() > 1) {
			g2.drawString("<", x - gp.tileSize / 4, y);
			g2.drawString(">", (int) (x + (gp.tileSize * 1.25) - 7), y);
		}
	}

	private void drawPartySelectionScreen() {
		drawParty(null);
		String sText = cancellableParty ? "Back" : null;
		drawToolTips("Info", "Swap", sText, null);
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
		        g2.setColor(moves[i].getPPColor());
		        String text = moves[i].move.toString();
		        g2.drawString(text, getCenterAlignedTextX(text, (x + width / 2)), y + 30);
		        String pp = showMoveSummary ? moves[i].move.cat == 2 ? "Status" : moves[i].move.mtype.effectiveness(foe) : moves[i].currentPP + " / " + moves[i].maxPP;
		        g2.drawString(pp, getCenterAlignedTextX(pp, (x + width / 2)), y + 55);
		        if (moveNum == i) {
		            g2.setColor(Color.WHITE);
		            g2.drawRoundRect(x, y, width, height, 10, 10);
		        }
		    }
		}
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			
			if (moves[moveNum].currentPP == 0 && !user.movesetEmpty()) {
				subState = MOVE_MESSAGE_STATE;
    			showMessage("No more PP remaining!");
    			return;
    		}
        	if (!user.moveUsable(moves[moveNum].move) && !user.movesetEmpty()) {
        		subState = MOVE_MESSAGE_STATE;
    			showMessage(moves[moveNum].move + " cannot be used!");
    			return;
    		}
        	Move move = moves[moveNum].move;
    		if (user.movesetEmpty()) move = Move.STRUGGLE;
			
        	foeMove = foe.trainerOwned() ? foe.bestMove(user, user.getFaster(foe, 0, 0) == foe) : foe.randomMove();
        	
        	showMoveSummary = false;
        	turn(move, foeMove);
        }
		if (showMoveSummary) {
        	drawMoveSummary(gp.tileSize * 3, (int) (gp.tileSize * 2.5), user, foe, moves[moveNum], null);
        }
	}
	
	public void turn(Move uMove, Move fMove) {
		// Priority stuff
		int uP, fP;
		uP = uMove == null ? 0 : uMove.priority;
		fP = fMove == null ? 0 : fMove.priority;
		if (uMove != null && user.ability == Ability.PRANKSTER && uMove.cat == 2) ++uP;
		if (fMove != null && foe.ability == Ability.PRANKSTER && fMove.cat == 2) ++fP;
		
		if (uMove != null && uMove.priority < 1 && uMove.hasPriority(user)) ++uP;
		if (fMove != null && fMove.priority < 1 && fMove.hasPriority(foe)) ++fP;
		
		if (uMove != null && fMove != null) {
			uP = user.checkQuickClaw(uP);
			if (!foe.vStatuses.contains(Status.SWAP)) fP = foe.checkQuickClaw(fP);
		}
		if (uMove != null) uP = user.checkCustap(uP, foe);
		if (fMove != null) fP = foe.checkCustap(fP, user);
		
		Pokemon faster;
		Pokemon slower;
		
		if (uMove == null || fMove == null) {
			faster = uMove == null ? user : foe;
		} else {
			faster = user.getFaster(foe, uP, fP);
		}
		
		slower = faster == user ? foe : user;
		
		boolean foeCanMove = true;
		
		if (faster == user) { // player Pokemon is faster
			if (slower.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				slower = foe.trainer.swapOut(user, fMove, false);
				foeMove = null;
				foeCanMove = false;
			}
			
			if (fMove != null && uMove == Move.SUCKER_PUNCH && fMove.cat == 2) uMove = Move.FAILED_SUCKER;
			faster.move(slower, uMove, true);
			faster = faster.trainer.getCurrent();
			if (slower.trainer != null) slower = slower.trainer.getCurrent();
			
			// Check for swap (player)
			if (user.trainer.hasValidMembers() && hasAlive() && faster.vStatuses.contains(Status.SWITCHING)) {
				Task t = Pokemon.addTask(Task.PARTY, "");
				t.wipe = faster.lastMoveUsed == Move.BATON_PASS;
				subState = TASK_STATE;
	        	return;
			}
			
	        if (!(foe.trainer != null && slower != foe.trainer.getCurrent()) && foeCanMove) {
	        	slower.move(faster, fMove, false);
	        	faster = faster.trainer.getCurrent();
	        	if (foe.trainer != null) slower = foe.trainer.getCurrent();
	        }
	        
	        // Check for swap (AI)
	        if (foe.trainer != null && foe.trainer.hasValidMembers() && foeCanMove && slower.vStatuses.contains(Status.SWITCHING)) {
	        	slower = foe.trainer.swapOut(faster, null, slower.lastMoveUsed == Move.BATON_PASS);
	        }
		} else { // enemy Pokemon is faster
			if (faster.vStatuses.contains(Status.SWAP)) { // AI wants to swap out
				faster = foe.trainer.swapOut(slower, fMove, false);
				foeMove = null;
				foeCanMove = false;
			}
			
			if (uMove != null && fMove == Move.SUCKER_PUNCH && uMove.cat == 2) fMove = Move.FAILED_SUCKER;
			if (foeCanMove) {
				faster.move(slower, fMove, true);
				if (faster.trainer != null) faster = faster.trainer.getCurrent();
				slower = slower.trainer.getCurrent();
				foeMove = null;
			}
			// Check for swap (AI)
	        if (foe.trainer != null && foe.trainer.hasValidMembers() && foeCanMove && faster.vStatuses.contains(Status.SWITCHING)) {
	        	faster = foe.trainer.swapOut(slower, null, faster.lastMoveUsed == Move.BATON_PASS);
	        }
			
	        if (slower == user.trainer.getCurrent()) {
	        	slower.move(faster, uMove, false);
	        	if (faster.trainer != null) faster = faster.trainer.getCurrent();
				slower = slower.trainer.getCurrent();
	        }
	        // Check for swap
	        if (user.trainer.hasValidMembers() && hasAlive() && slower.vStatuses.contains(Status.SWITCHING)) {
	        	Task t = Pokemon.addTask(Task.PARTY, "");
	        	t.wipe = slower.lastMoveUsed == Move.BATON_PASS;
	        	subState = TASK_STATE;
	        	return;
			}
		}
	    subState = TASK_STATE;
		if (uMove != null || fMove != null) {
			if (hasAlive()) faster.endOfTurn(slower);
			if (hasAlive()) slower.endOfTurn(faster);
			if (hasAlive()) field.endOfTurn();
		}
		Pokemon next = foe.trainer == null ? foe : foe.trainer.getCurrent();
		while (next.isFainted()) {
			if (foe.trainer != null) {
				if (foe.trainer.hasNext()) {
					next = foe.trainer.next(user);
					Pokemon.addSwapInTask(next, next.currentHP);
					next.swapIn(user, true);
					user.getPlayer().clearBattled();
					user.battled = true;
				} else {
					user.getPlayer().setMoney(user.getPlayer().getMoney() + foe.trainer.getMoney());
					String message = foe.trainer.getName() + " was defeated!\nWon $" + foe.trainer.getMoney() + "!";
					if (foe.trainer.getItem() != null) {
		            	user.getPlayer().bag.add(foe.trainer.getItem());
		            	message += "\nYou were given " + foe.trainer.getItem().toString() + "!";
		            }
					if (foe.trainer.getFlagIndex() != 0) {						
		            	user.getPlayer().flag[foe.trainer.getFlagX()][foe.trainer.getFlagY()] = true;
//		            	if (flag == 6) { TODO replace with flags and items in 4th gym split
//		            		message += "\nObtained A Key!";
//		            	} else if (flag == 7) {
//		            		message += "\nObtained B Key!";
//		            	}
		            }
					Pokemon.addTask(Task.END, message);
		            if (foe.trainer.getMoney() == 500 && user.getPlayer().badges < 8) {
		            	user.getPlayer().badges++;
		            	user.getPlayer().beatGymTrainers();
		            	for (Pokemon p : user.trainer.team) {
		            		if (p != null) p.awardHappiness(15, true);
		            	}
		            	user.getPlayer().updateHappinessCaps();
		            	for (Entity clerk : gp.aSetter.clerks) {
		            		clerk.setItems(gp.player.getItems());
		            	}
		            }
		            break;
				}
			} else if (!user.getPlayer().wiped()) {
				subState = TASK_STATE;
				Pokemon.addTask(Task.END, foe.name + " was defeated!");
				break;
			}
		}
	    if (user.getPlayer().wiped()) {
	    	Pokemon.addTask(Task.TEXT, "You have no more Pokemon that can fight!\nYou lost $500!");
			Task t = Pokemon.addTask(Task.END, "");
			t.setWipe(true);
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
		gp.endBattle(-1, -1);
		user.getPlayer().setMoney(user.getPlayer().getMoney() - 500);
		gp.eHandler.teleport(Player.spawn[0], Player.spawn[1], Player.spawn[2], false);
		user.trainer.heal();
		if (foe.trainer != null) {
			foe.trainer.heal();
			foe.trainer.setCurrent(foe.trainer.getTeam()[0]);
		}
	}
	
	@Override
	public void drawParty(Item item) {
		super.drawParty(item);
		currentTask = null;
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Pokemon select = gp.player.p.team[partyNum];
			if (select.isFainted()) {
				currentDialogue = select.nickname + " has no energy to battle!";
			} else if (select == select.trainer.getCurrent()) {
				currentDialogue = select.nickname + " is already out!";
			} else if (cancellableParty && user.isTrapped()) {
        		currentDialogue = "You are trapped and cannot switch!";
			} else {
				subState = TASK_STATE;
				if (user.isFainted()) foeMove = null;
				if (cancellableParty && !user.isFainted()) {
					foeMove = foe.trainerOwned() ? foe.bestMove(user, user.getFaster(foe, 0, 0) == foe) : foe.randomMove();
				}
				tempUser = gp.player.p.team[partyNum].clone();
				if (baton) {
					gp.player.p.team[partyNum].statStages = user.statStages.clone();
					gp.player.p.team[partyNum].vStatuses = new ArrayList<>(user.vStatuses);
					gp.player.p.team[partyNum].vStatuses.remove(Status.SWITCHING);
					gp.player.p.team[partyNum].vStatuses.remove(Status.SWAP);
					gp.player.p.team[partyNum].magCount = user.magCount;
					gp.player.p.team[partyNum].perishCount = user.perishCount;
				}
				gp.player.p.swapToFront(gp.player.p.team[partyNum], partyNum);
				gp.player.p.getCurrent().swapIn(foe, true);
				user = gp.player.p.getCurrent();
				partyNum = 0;
				moveNum = 0;
				commandNum = 0;
				dialogueCounter = 0;
				baton = false;
				turn(null, foeMove);
				foeMove = null;
			}
		}
		if (gp.keyH.wPressed) {
			gp.keyH.wPressed = false;
			subState = SUMMARY_STATE;
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
	
	private void drawInfo() {
		int x = gp.tileSize * 3;
		int y = gp.tileSize / 2;
		int width = gp.tileSize * 10;
		int height = (int) (gp.tileSize * 10.5);
		drawSubWindow(x, y, width, height, 255);
		
		x += gp.tileSize / 2;
		y += gp.tileSize;
		int userX = x;
		int startY = y;
		g2.setFont(g2.getFont().deriveFont(24F));
		g2.drawString(user.nickname, x, y);
		
		y += gp.tileSize / 4;
		g2.drawImage(user.type1.getImage(), x, y, null);
		x += gp.tileSize / 2 + 4;
		if (user.type2 != null) g2.drawImage(user.type2.getImage(), x, y, null);
		
		x = userX;
		y = startY;
		x = gp.tileSize * 8 - 68;
		x -= gp.tileSize / 2;
		y -= gp.tileSize / 2;
		g2.drawImage(user.getSprite(), x, y, null);
		
		g2.setColor(Color.WHITE);
		x += 80 + gp.tileSize / 2;
		y += gp.tileSize / 2;
		int foeX = x;
		int foeY = y;
		g2.drawString(foe.nickname, x, y);
		
		y += gp.tileSize / 4;
		g2.drawImage(foe.type1.getImage(), x, y, null);
		x += gp.tileSize / 2 + 4;
		if (foe.type2 != null) g2.drawImage(foe.type2.getImage(), x, y, null);
		
		x = foeX;
		y = foeY;
		x = (gp.tileSize * 3 + width) - 80;
		x -= gp.tileSize / 2;
		y -= gp.tileSize / 2;
		g2.drawImage(foe.getSprite(), x, y, null);
		
		x = userX;
		y = (int) (startY + gp.tileSize * 1.5);
		startY = y;
		g2.setFont(g2.getFont().deriveFont(20F));
        
        for (int i = 2; i < 16; i++) {
        	g2.setColor(Color.WHITE);
        	String type = Pokemon.getStatType(i / 2);
        	int stage;
        	if (i % 2 == 0) {
        		stage = user.statStages[i / 2 - 1];
        	} else {
        		stage = foe.statStages[i / 2 - 1];
        	}
        	String amt = stage > 0 ? "+" + stage : stage + "";
        	
        	if (stage > 0) g2.setColor(Color.red.darker());
        	if (stage < 0) g2.setColor(Color.blue);
        	
        	g2.drawString(type + ": " + amt, x, y);
        	if (i % 2 == 0) {
        		x = foeX;
        	} else {
        		x = userX;
        		y += gp.tileSize / 2;
        	}
        }
        
        x = userX + gp.tileSize * 3;
        int endY = y;
        y = startY;
	    ArrayList<String> addVStatus = user.getStatusLabels();
	    for (String s : addVStatus) {
        	g2.drawString(s, x, y);
        	y += gp.tileSize / 3;
        }
        
        x = foeX + gp.tileSize * 2;
        y = startY;
        addVStatus = foe.getStatusLabels();
        for (String s : addVStatus) {
        	g2.drawString(s, x, y);
        	y += gp.tileSize / 3;
        }
        
        y = endY;
        g2.setFont(g2.getFont().deriveFont(24F));
        String fieldLabel = "Field Effects:";
        int middleX = gp.tileSize * 3 + width / 2;
        g2.drawString(fieldLabel, getCenterAlignedTextX(fieldLabel, middleX), y);
        
        y += gp.tileSize * 0.75;
	    
	    if (field.weather != null) {
	    	String weather = field.weather.toString();
	    	String turns = field.weatherTurns + "/" + field.weather.effect.turns;
	    	g2.setColor(field.weather.getColor());
	    	g2.drawString(weather, getCenterAlignedTextX(weather, middleX - gp.tileSize), y);
	    	g2.setColor(Color.WHITE);
	    	g2.drawString(turns, getCenterAlignedTextX(turns, middleX + gp.tileSize), y);
	    	y += gp.tileSize / 2;
	    }
	    
	    if (field.terrain != null) {
	    	String terrain = field.terrain.toString();
	    	String turns = field.terrainTurns + "/" + field.terrain.effect.turns;
	    	g2.setColor(field.terrain.getColor());
	    	g2.drawString(terrain, getCenterAlignedTextX(terrain, middleX - gp.tileSize), y);
	    	g2.setColor(Color.WHITE);
	    	g2.drawString(turns, getCenterAlignedTextX(turns, middleX + gp.tileSize), y);
	    	y += gp.tileSize / 2;
	    }
	    
	    for (FieldEffect fe : field.fieldEffects) {
	    	String effect = fe.toString();
	    	String turns = fe.turns + "/" + fe.effect.turns;
	    	g2.setColor(fe.getColor());
	    	g2.drawString(effect, getCenterAlignedTextX(effect, middleX - gp.tileSize), y);
	    	g2.setColor(Color.WHITE);
	    	g2.drawString(turns, getCenterAlignedTextX(turns, middleX + gp.tileSize), y);
	    	y += gp.tileSize / 2;
	    }
	    
	    x = userX;
	    y += gp.tileSize / 2;
	    startY = y;
	    
	    for (FieldEffect fe : field.playerSide) {
	    	String effect = fe.toString();
	    	String turns = fe.turns + "/" + fe.effect.turns;
	    	if (field.getHazards(field.playerSide).contains(fe)) {
	    		int layers = field.getLayers(field.playerSide, fe.effect);
	    		int maxLayers = 1;
	    		if (fe.effect == Effect.SPIKES) maxLayers = 3;
	    		if (fe.effect == Effect.TOXIC_SPIKES) maxLayers = 2;
	    		turns = "( " + layers + " / " + maxLayers + " )";
	    	}
	    	g2.setColor(fe.getColor());
	    	g2.drawString(effect, x, y);
	    	g2.setColor(Color.WHITE);
	    	g2.drawString(turns, x + gp.tileSize * 3, y);
	    	y += gp.tileSize / 2;
	    }
	    
	    x = foeX;
	    y = startY;

	    for (FieldEffect fe : field.foeSide) {
	    	String effect = fe.toString();
	    	String turns = fe.turns + "/" + fe.effect.turns;
	    	if (field.getHazards(field.foeSide).contains(fe)) {
	    		int layers = field.getLayers(field.foeSide, fe.effect);
	    		int maxLayers = 1;
	    		if (fe.effect == Effect.SPIKES) maxLayers = 3;
	    		if (fe.effect == Effect.TOXIC_SPIKES) maxLayers = 2;
	    		turns = "( " + layers + " / " + maxLayers + " )";
	    	}
	    	g2.setColor(fe.getColor());
	    	g2.drawString(effect, x, y);
	    	g2.setColor(Color.WHITE);
	    	g2.drawString(turns, x + gp.tileSize * 3, y);
	    	y += gp.tileSize / 2;
	    }
	    
	    drawToolTips(null, null, "Back", null);
	}

	private void startingState() {
		if (foe.trainerOwned() && staticID == -1) {
			showMessage("You are challenged by " + foe.trainer.getName() + "!");
			foeStatus = foe.status;
		} else {
			foe.setVisible(true);
			foeStatus = foe.status;
			foeHP = foe.currentHP;
			showMessage("A wild " + foe.nickname + " appeared!");
		}
	}

}
