package pokemon;

import java.awt.Color;
import java.util.ArrayList;

import entity.Entity;
import object.TemplateParticle;
import overworld.GamePanel;
import pokemon.Field.FieldEffect;

public class Task {
	public static final int DOWN = 0;
	public static final int UP = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int TEXT = 0;
	public static final int DAMAGE = 1;
	public static final int ABILITY = 2;
	public static final int STAT = 3;
	public static final int SWAP_IN = 4;
	public static final int SWAP_OUT = 5;
	public static final int FAINT = 6;
	public static final int END = 7;
	public static final int PARTY = 8;
	public static final int STATUS = 9;
	public static final int CATCH = 10;
	public static final int NICKNAME = 11;
	public static final int EXP = 12;
	public static final int LEVEL_UP = 13;
	public static final int MOVE = 14;
	public static final int EVO = 15;
	public static final int WEATHER = 16;
	public static final int TERRAIN = 17;
	public static final int CLOSE = 18;
	public static final int GIFT = 19;
	public static final int REMIND = 20;
	public static final int FOSSIL = 21;
	public static final int HP = 22;
	public static final int EVO_ITEM = 23;
	public static final int SPRITE = 24;
	public static final int CONFIRM = 25; // counter: 0 = gauntlet at mt. splinkty
	public static final int TELEPORT = 26;
	public static final int SEMI_INV = 27;
	public static final int FLASH_IN = 28;
	public static final int FLASH_OUT = 29;
	public static final int ITEM = 30;
	public static final int UPDATE = 31;
	public static final int TURN = 32; // counter: 0 = down, 1 = up, 2 = left, 3 = right, wipe = setDirection()
	public static final int FLAG = 33; // start: x finish: y
	public static final int REGIONAL_TRADE = 34;
	public static final int BATTLE = 35; // counter: trainer, start: id (optional)
	public static final int SPOT = 36;
	public static final int START_BATTLE = 37; // counter: trainer
	public static final int DIALOGUE = 38;
	public static final int SPEAK = 39;
	public static final int ITEM_SUM = 40;
	public static final int SHAKE = 41;
	public static final int MOVE_CAMERA = 42; // start: 0 for X, 1 for Y, finish: offsetX/offsetY to end at
	public static final int MOVE_NPC = 43; // start: 0 for X, 1 for Y, finish: TILE COORDINATE to end at, wipe: yes/no to have camera follow the npc
	public static final int SLEEP = 44; // counter: frames to sleep for
	public static final int BLACKJACK = 45;
	public static final int MUSHROOM = 46;
	public static final int COIN = 47;
	public static final int EVO_INFO = 48;
	public static final int ODDS = 49;
	public static final int BET_BATTLE = 50;
	public static final int GAME_STATE = 51; // counter: what game state to swtich to
	private static final int SHINY = 52;
	public static final int PAYOUT = 53;
	public static final int PARLAY = 54;
	public static final int INTERACTIVE = 55;
	public static final int TYPES = 56;
	public static final int EVOLUTION = 57;
	public static final int PARTICLE = 58;
	public static final int RESET = 59;
	public static final int I_TILE = 60; // counter: what iTile map to set up
	public static final int AWAKE = 61;
	
	public static GamePanel gp;
	
	public int type;
	public String message;
	public int counter; // map for teleport
	
	public boolean wipe; // cooldown for teleport, visible/not for semi_inv
	
	public int start; // x for teleport
	public int finish; // y for teleport
	public Pokemon p; // the pokemon taking damage, or announcing an ability, or being sent out
	public Pokemon foe;
	public Ability ability;
	public Status status;
	public Move move;
	public FieldEffect fe;
	public Item item;
	public PType[] types;
	
	public Entity e;
	public Trainer[] trainers;
	public Color color;
	public ArrayList<TemplateParticle> particleList;
	
	public Task(int type, String message) {
		this(type, message, null);
	}

	public Task(int type, String message, Pokemon p) {
		this.message = message;
		this.type = type;
		if (type == ITEM) {
			this.counter = 1;
		} else {
			this.counter = 50;
		}
		setPokemon(p);
		start = -1;
	}
	
	public void setPokemon(Pokemon p) {
		this.p = p;
	}
	
	public void setFinish(int f) {
		this.finish = f;
	}
	
	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	
	public void setWipe(boolean wipe) {
		this.wipe = wipe;
	}
	
	public void setMove(Move m) {
		this.move = m;
	}
	
	public void setEffect(FieldEffect fe) {
		this.fe = fe;
	}
	
	public String toString() {
		try {
			return message.substring(0, 6) + "... [" + getTypeString() + "]";
		} catch (StringIndexOutOfBoundsException e) {
			//e.printStackTrace();
			return "\"\" [" + getTypeString() + "]";
		}
	}
	
	private String getTypeString() {
		switch(type) {
		case TEXT: return "TEXT";
		case DAMAGE: return "DAMAGE";
		case ABILITY: return "ABILITY";
		case STAT: return "STAT";
		case SWAP_IN: return "SWAP_IN";
		case SWAP_OUT: return "SWAP_OUT";
		case FAINT: return "FAINT";
		case END: return "END";
		case PARTY: return "PARTY";
		case STATUS: return "STATUS";
		case CATCH: return "CATCH";
		case NICKNAME: return "NICKNAME";
		case EXP: return "EXP";
		case LEVEL_UP: return "LEVEL_UP";
		case MOVE: return "MOVE";
		case EVO: return "EVO";
		case WEATHER: return "WEATHER";
		case TERRAIN: return "TERRAIN";
		case CLOSE: return "CLOSE";
		case GIFT: return "GIFT";
		case REMIND: return "REMIND";
		case FOSSIL: return "FOSSIL";
		case HP: return "HP";
		case EVO_ITEM: return "EVO_ITEM";
		case SPRITE: return "SPRITE";
		case CONFIRM: return "CONFIRM";
		case TELEPORT: return "TELEPORT";
		case SEMI_INV: return "SEMI_INV";
		case FLASH_IN: return "FLASH_IN";
		case FLASH_OUT: return "FLASH_OUT";
		case ITEM: return "ITEM";
		case UPDATE: return "UPDATE";
		case TURN: return "TURN";
		case FLAG: return "FLAG";
		case REGIONAL_TRADE: return "REGIONAL_TRADE";
		case BATTLE: return "BATTLE";
		case SPOT: return "SPOT";
		case START_BATTLE: return "START_BATTLE";
		case DIALOGUE: return "DIALOGUE";
		case SPEAK: return "SPEAK";
		case ITEM_SUM: return "ITEM_SUM";
		case SHAKE: return "SHAKE";
		case MOVE_CAMERA: return "MOVE_CAMERA";
		case MOVE_NPC: return "MOVE_NPC";
		default:
			return "getTypeString() doesn't have a case for this type";
		}
	}
	
	public static Task createTask(int type, String string) {
		return createTask(type, string, null);
	}
	
	public static Task createTask(int type, String string, Pokemon p) {
		return new Task(type, string, p);
	}
	
	public static Task addTask(int type, String string) {
		return addTask(type, string, null);
	}
	
	public static Task addTask(int type, Status status, String string, Pokemon p) {
		Task t = addTask(type, string, p);
		t.status = status;
		return t;
	}
	
	public static Task addTask(int type, String string, Pokemon p) {
		if (gp != null && gp.gameState == GamePanel.BATTLE_STATE) {
			Task t = createTask(type, string, p);
			gp.battleUI.tasks.add(t);
			gp.battleUI.checkTasks = true;
			return t;
		} else if (gp != null && gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			Task t = createTask(type, string, p);
			gp.simBattleUI.tasks.add(t);
			gp.simBattleUI.checkTasks = true;
			return t;
		} else if (gp != null && (gp.gameState == GamePanel.RARE_CANDY_STATE || gp.gameState == GamePanel.TASK_STATE)) {
			Task t = createTask(type, string, p);
			if (type == GIFT) {
				// t.wipe = gift is egg
				t.wipe = p instanceof Egg ? true : false;
			}
			gp.ui.tasks.add(t);
			gp.ui.checkTasks = true;
			return t;
		} else {
			if (type == Task.TEXT) {
				gp.ui.showMessage(string);
			}
			System.out.println("GameState wasn't Task or Rare Candy state, it was: " + gp.gameState);
			return null;
		}
	}
	
	public static Task addTask(int type, String string, int counter) {
		Task t = addTask(type, string);
		t.counter = counter;
		return t;
	}
	
	public static Task addTask(int type, Entity e, String string) {
		Task t = addTask(type, string);
		t.e = e;
		return t;
	}
	
	public static Task addTask(int type, Entity e, String string, int counter) {
		Task t = addTask(type, string);
		t.e = e;
		t.counter = counter;
		return t;
	}
	
	public static void addEvoTask(Pokemon p, int result, int index) {
		Task t = createTask(Task.EVO, p.nickname + " is evolving!\nDo you want to evolve your " + p.nickname + "?", p);
		t.counter = result;
		insertTask(t, index);
	}
	
	public static void addTrainerTask(Entity t, int id, Pokemon foe, boolean spot) {
		if (spot) {
			Task task = addTask(Task.SPOT, "");
			task.e = t;
		}
		for (int i = 0; i < t.dialogues.length; i++) {
			if (t.dialogues[i] != null) {
				 addTask(Task.DIALOGUE, t, t.dialogues[i]);
			}
		}
		addStartBattleTask(t.trainer, id, 'G');
	}
	
	public static void addStartBattleTask(int trainer) {
		addStartBattleTask(trainer, -1, 'G');
	}
	
	public static void addStartBattleTask(int trainer, int id, char encType) {
		Pokemon foe = Trainer.getTrainer(trainer).getCurrent();
		addStartBattleTask(trainer, id, foe, encType);
	}
	
	public static void addStartBattleTask(int trainer, int id, Pokemon foe, char encType) {
		Task t = addTask(Task.START_BATTLE, String.valueOf(encType), foe);
		t.counter = trainer;
		t.start = id;
	}
	
	public static void addAbilityTask(Pokemon p) {
		if (p == null) return;
		Task t = addTask(Task.ABILITY, "[" + p.nickname + "'s " + p.ability + "]:", p);
		t.setAbility(p.ability);
	}
	
	public static void addTypeTask(String message, Pokemon p) {
		if (p == null) return;
		Task t = addTask(Task.TYPES, message, p);
		t.types = new PType[] {p.type1, p.type2};
	}
	
	public static void addSwapInTask(Pokemon p, boolean playerSide) {
		if (gp.gameState != GamePanel.BATTLE_STATE && gp.gameState != GamePanel.SIM_BATTLE_STATE) return;
		String message = p.playerOwned() ? "Go! " + p.nickname + "!" : p.trainer.toString() + " sends out " + p.nickname + "!";
		Task t = addTask(Task.SWAP_IN, message, p);
		if (t != null) {
			t.wipe = playerSide;
			t.foe = p.clone();
			if (p.shiny) {
				addTask(Task.SHINY, "", p);
			}
		}
	}
	
	public static void addSwapOutTask(Pokemon p, boolean playerSide) {
		String message = p.playerOwned() ? p.nickname + ", come back!" : p.trainer.toString() + " withdrew " + p.nickname + "!";
		if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			String rsn = "";
			if (gp.simBattleUI.p1Switch != null) {
				if (gp.simBattleUI.p1Switch.getFirst() == p) {
					rsn = gp.simBattleUI.p1Switch.getSecond();
					gp.simBattleUI.p2Switch = null;
				}
			}
			if (gp.simBattleUI.p2Switch != null) {
				if (gp.simBattleUI.p2Switch.getFirst() == p) {
					rsn = gp.simBattleUI.p2Switch.getSecond();
					gp.simBattleUI.p2Switch = null;
				}
			}
			
			message = String.format("%s\n%s", message, rsn);
		}
		Task t = addTask(Task.SWAP_OUT, message, p);
		if (t != null) {
			t.wipe = playerSide;
		}
	}
	
	public static void setTask(int index, Task task) {
		ArrayList<Task> tasks = gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.simBattleUI.tasks;
		if (tasks.size() == 0) return;
		tasks.set(index, task);
	}
	
	public static Task getTask(int index) {
		ArrayList<Task> tasks = gp.gameState == GamePanel.BATTLE_STATE ? gp.battleUI.tasks : gp.simBattleUI.tasks;
		if (tasks.size() == 0) return null;
		return tasks.get(index);
	}
	
	public static void insertTask(Task t, int index) {
		if (gp.gameState == GamePanel.BATTLE_STATE) {
			gp.battleUI.tasks.add(index, t);
		} else if (gp.gameState == GamePanel.SIM_BATTLE_STATE) {
			gp.simBattleUI.tasks.add(index, t);
		} else {
			gp.ui.tasks.add(index, t);
		}
	}
	
	public static void addCameraMoveTask(char m, int dest, int frameInc) {
		Task t = addTask(MOVE_CAMERA, "");
		t.start = m == 'x' ? 0 : 1;
		t.finish = dest;
		t.counter = frameInc;
	}
	
	public static void addDiagCameraMoveTask(int destX, int destY, int frames) {
		Task t = addTask(MOVE_CAMERA, "");
		t.start = destX;
		t.finish = destY;
		t.counter = frames;
		t.wipe = true;
	}
	
	public static void addNPCMoveTask(char m, int dest, Entity target, boolean follow, int frameInc) {
		Task t = addTask(MOVE_NPC, target, "");
		t.start = m == 'x' ? 0 : 1;
		t.finish = dest;
		t.counter = frameInc;
		t.wipe = follow;
	}
	
	public static void addParticleTask(Entity npc, String path, Color color, int amt) {
		Task t = addTask(PARTICLE, npc, path);
		t.counter = 180;
		t.start = t.counter;
		t.color = color;
		t.particleList = new ArrayList<>();
		
		for (int i = 0; i < amt; i++) {
			TemplateParticle p = new TemplateParticle(gp, t.e, t.color, t.message);
			p.spawnDelay = i;
			t.particleList.add(p);
		}
	}
}