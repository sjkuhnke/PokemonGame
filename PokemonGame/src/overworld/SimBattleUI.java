package overworld;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pokemon.Ability;
import pokemon.Field;
import pokemon.Item;
import pokemon.Move;
import pokemon.Moveslot;
import pokemon.PType;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.Status;
import util.Pair;
import pokemon.Pokemon.Task;

public class SimBattleUI extends BattleUI {
	
	public ArrayList<Pair<Double, String>> parlaySheet;

	public SimBattleUI(GamePanel gp) {
		super(gp);
	}
	
	@Override
	public void showMessage(String message) {
		super.showMessage(message);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
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
	}
	
	@Override // TODO: Idle screen with odds, a next turn button, a side bets button, etc.
	protected void drawIdleScreen() {
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawCalcWindow();
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Item.useCalc(user, null, foe);
		}
		drawCatchWindow();
		drawActionScreen(user);
		String dText = foe.trainerOwned() ? "Foe" : null;
		if (!showFoeSummary) drawToolTips("OK", null, null, dText);
		
		if (gp.keyH.tabPressed) { // TODO: a key for swapping views
			gp.keyH.tabPressed = false;
			Pokemon tmp = user;
			user = foe;
			foe = tmp;
			
			userHP = user.currentHP;
			foeHP = foe.currentHP;
			maxUserHP = user.getStat(0);
			userStatus = user.status;
			foeStatus = foe.status;
			userLevel = user.level;
		}
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
	
	@Override // TODO: edit the END_STATE case for handling ending a sim
	protected void drawDialogueState() {
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
	
	@Override
	protected void setStartingTasks() {
		field = new Field();
		user.getFieldEffects().clear();
		foe.getFieldEffects().clear();
	    Pokemon.field = field;
	    userHP = user.currentHP;
	    maxUserHP = user.getStat(0);
	    foeHP = foe.currentHP;
	    aura = false;
	    weather = null;
	    terrain = null;
		
		Pokemon.addSwapInTask(foe, false);
		foeFainted = foe.trainer.getNumFainted();
	    Pokemon.addSwapInTask(user, true);
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
	protected void drawNameLabel(Pokemon p) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(24F));
		
		int x;
		int y;
		int levelX;
		int levelY;
		String name = p.nickname;
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
		g2.drawString(p.nickname, x, y);
		int level = p == user ? userLevel : p.level;
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
	
	@Override // TODO: replace buttons with buttons specified in drawIdleScreen()
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
				turn();
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
		}
	}
	
	@Override
	protected void drawFoeSummaryParty() {
		super.drawFoeSummaryParty();
	}
	
	@Override // TODO: can be behind a "view moves" button on the idle screen that just lets you view the moves
	protected void drawMoveSelectionScreen() {
		currentDialogue = "What will\n" + user.nickname + " do?";
		drawDialogueScreen(false);
		drawMoves();
		if (gp.keyH.aPressed) {
			gp.keyH.aPressed = false;
			Item.useCalc(user, null, foe);
		}
		if (gp.keyH.dPressed) {
			gp.keyH.dPressed = false;
			showMoveSummary = !showMoveSummary;
		}
		drawCalcWindow();
		drawToolTips("OK", null, "Back", "Info");
	}
	
	@Override // TODO: should they be allowed to calc?
	protected void drawCalcWindow() {
		super.drawCalcWindow();
	}
	
	@Override
	protected void drawCatchWindow() {
		return;
	}
	
	@Override
	protected void drawPartySelectionScreen() {
		super.drawPartySelectionScreen();
	}
	
	@Override
	protected void drawActionBackground(Pokemon p, int x, int y, int width, int height) {
		super.drawActionBackground(p, x, y, width, height);
	}
	
	@Override // TODO: change to just being able to view moves and not actually press any
	protected void drawMoves() {
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
		        
		        Color color = moves[i].move == Move.HIDDEN_POWER || moves[i].move == Move.RETURN ? user.determineHPType().getColor() : moves[i].move.mtype.getColor();
		        if (!user.moveUsable(moves[i].move)) color = new Color(100, 100, 100, 200);
		        g2.setColor(color);
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
	
	public void turn() {
		Pokemon p1 = user;
		Pokemon p2 = foe;
		boolean fFaster = p1.getFaster(p2, 0, 0) == p2;
		
		Move uMove = p1.bestMove(p2, !fFaster);
		Move fMove = p2.bestMove(p1, fFaster);
		
		int uP, fP;
		uP = uMove == null ? 0 : uMove.getPriority(p1);
		fP = fMove == null ? 0 : fMove.getPriority(p2);
		if (uMove != null && p1.ability == Ability.PRANKSTER && uMove.cat == 2) ++uP;
		if (fMove != null && p2.ability == Ability.PRANKSTER && fMove.cat == 2) ++fP;
		
		if (uMove != null && uMove.priority < 1 && uMove.hasPriority(p1)) ++uP;
		if (fMove != null && fMove.priority < 1 && fMove.hasPriority(p2)) ++fP;
		
		if (uMove != null && fMove != null && !p1.vStatuses.contains(Status.SWAP)
				&& !p2.vStatuses.contains(Status.SWAP)) {
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
		
		if (faster.vStatuses.contains(Status.SWAP)) {
			faster = faster.trainer.swapOut(slower, fastMove, false, faster.trainer.hasUser(user));
			fastMove = null;
			fastCanMove = false;
		}
		
		if (slower.vStatuses.contains(Status.SWAP)) {
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
		if (faster.trainer.hasValidMembers() && fastCanMove && !slower.trainer.wiped() && faster.vStatuses.contains(Status.SWITCHING)) {
			faster = faster.trainer.swapOut(slower, null, faster.lastMoveUsed == Move.BATON_PASS, faster.trainer.hasUser(user));
		}
		
        if (slowCanMove) {
        	slower.moveInit(faster, slowMove, false);
        	faster = faster.trainer.getCurrent();
        	slower = slower.trainer.getCurrent();
        }
        
        // Check for swap
        if (slower.trainer.hasValidMembers() && slowCanMove && !faster.trainer.wiped() && slower.vStatuses.contains(Status.SWITCHING)) {
        	slower = slower.trainer.swapOut(faster, null, slower.lastMoveUsed == Move.BATON_PASS, slower.trainer.hasUser(user));
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
					Pokemon.addSwapInTask(next, next.currentHP, userSide);
					next.swapIn(foe, true);
				} else {
		            break;
				}
			}
		}
		if (!faster.trainer.wiped() && slower.trainer.wiped()) {
			Pokemon.addTask(Task.END, faster.trainer.getName() + " defeated " + slower.trainer.getName() + "!");
		} else if (faster.trainer.wiped() && !slower.trainer.wiped()) {
			Pokemon.addTask(Task.END, slower.trainer.getName() + " defeated " + faster.trainer.getName() + "!");
		} else if (faster.trainer.wiped() && slower.trainer.wiped()) {
			Pokemon.addTask(Task.END, "It's a draw: bet was returned!");
		}
	}
	
	@Override
	protected boolean hasAlive() {
		return super.hasAlive();
	}
	
	@Override // TODO: this should just end the sim
	protected void wipe() {
		gp.endBattle(-1, -1);
		user.getPlayer().setMoney(user.getPlayer().getMoney() - 500);
		gp.eHandler.teleport(Player.spawn[0], Player.spawn[1], Player.spawn[2], false);
		user.trainer.heal();
		if (foe.trainer != null) {
			foe.trainer.heal();
			foe.trainer.setCurrent(foe.trainer.getTeam()[0]);
		}
	}
	
	@Override // TODO: don't have to worry about this for now
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
			} else if (cancellableParty && user.isTrapped(foe)) {
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
	
	@Override
	protected void drawInfo() {
		super.drawInfo();
	}

	@Override
	protected void startingState() {
		showMessage("A battle is started between " + user.name + " and " + foe.name + "!");
		userStatus = user.status;
		foeStatus = foe.status;
	}

}
