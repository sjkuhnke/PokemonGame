package overworld;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import entity.Entity;
import entity.NPC_Block;
import entity.NPC_Clerk;
import entity.NPC_GymLeader;
import entity.NPC_Invisible;
import entity.NPC_Market;
import entity.NPC_Nurse;
import entity.NPC_PC;
import entity.NPC_Pokemon;
import entity.NPC_Rival;
import entity.NPC_Rival2;
import entity.NPC_TN;
import entity.NPC_TN_Admin;
import entity.NPC_Trainer;
import object.Cut_Tree;
import object.Fuse_Box;
import object.GymBarrier;
import object.InteractiveTile;
import object.ItemObj;
import object.Locked_Door;
import object.Pit;
import object.PitEdge;
import object.Rock_Climb;
import object.Rock_Smash;
import object.Starter_Machine;
import object.Tree_Stump;
import object.Vine;
import object.Vine_Crossable;
import object.Whirlpool;
import object.Whirlpool_Corner;
import object.Whirlpool_Side;
import pokemon.Item;
import pokemon.Pokemon;

public class AssetSetter {

	GamePanel gp;
	int index;
	int objIndex;
	private int iIndex;
	private int berryIndex;
	
	public ArrayList<Entity> clerks = new ArrayList<>();
	
	private static final int RYDER = -17;
	private static final int ALAKAZAM = -16;
	private static final int KLARA = -15;
	private static final int AVERY = -14;
	private static final int GRANDMOTHER = -13;
	private static final int RESEARCHER = -12;
	private static final int PROFESSOR = -11;
	private static final int BLOCK_DEFAULT = -10;
	
	private static final int NPC_PC_GAUNTLET = -9;

	private static final int GRUST = -5;
	private static final int NPC_MARKET = -4;
	private static final int NPC_NURSE_FULL = -3;
	private static final int NPC_CLERK = -2;
	private static final int NPC_PC = -1;
	private static final int NPC_NURSE = 0;
	private static final int ROBIN = 1;
	private static final int STANFORD = 2;
	private static final int MILLIE = 3;
	private static final int GLACIUS = 4;
	private static final int MINDY = 5;
	private static final int RAYNA = 6;
	private static final int MERLIN = 7;
	private static final int NOVA = 8;
	private static final int SCOTT_UP = 9;
	private static final int SCOTT_DOWN = 10;
	private static final int FRED_UP = 11;
	private static final int FRED_DOWN = 12;
	private static final int TRAINER_UP = 13;
	private static final int TRAINER_DOWN = 14;
	private static final int TRAINER_LEFT = 15;
	private static final int TRAINER_RIGHT = 16;
	private static final int RICK = 17;
	private static final int MAXWELL = 18;
	private static final int TN_DOWN = 19;
	private static final int TN_UP = 20;
	private static final int TN_LEFT = 21;
	private static final int TN_RIGHT = 22;
	private static final int INVIS_DOWN = 23;
	private static final int INVIS_UP = 24;
	private static final int INVIS_LEFT = 25;
	private static final int INVIS_RIGHT = 26;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
		index = 0;
		objIndex = 0;
		iIndex = 0;
	}
	
	public void setObject() {
		int mapNum = 0;
		
		gp.obj[mapNum][objIndex] = ObjSetup(54, 45, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 54, Item.ANTIDOTE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 31, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 24, Item.ELIXIR, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(89, 10, Item.TM84, mapNum); // dark pulse
		gp.obj[mapNum][objIndex] = ObjSetup(79, 11, Item.BOLD_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(80, 18, Item.PP_UP, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(71, 32, Item.TM98, mapNum); // flip turn
		
		gp.obj[mapNum][objIndex] = ObjSetup(54, 40, Item.PECHA_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 36, Item.CHESTO_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(65, 40, Item.CHERI_BERRY, mapNum, 15, 30);
		
		gp.obj[mapNum][objIndex] = ObjSetup(13, 45, Item.ASPEAR_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(80, 62, Item.KING1S_ROCK, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(70, 63, Item.GLOWING_PRISM, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(34, 76, Item.FIRE_STONE, mapNum);
		
		mapNum = 4;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(86, 56, Item.TM31, mapNum); // false swipe
		gp.obj[mapNum][objIndex] = ObjSetup(39, 59, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 60, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(46, 73, Item.TM26, mapNum); // hidden power
		gp.obj[mapNum][objIndex] = ObjSetup(15, 57, Item.TM17, mapNum); // solar beam
		gp.obj[mapNum][objIndex] = ObjSetup(24, 67, Item.BLACK_BELT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 72, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 75, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(71, 29, Item.TM99, mapNum); // return (need cut)
		gp.obj[mapNum][objIndex] = ObjSetup(74, 19, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(69, 15, Item.LEPPA_BERRY, mapNum, 3, 6);
		
		gp.obj[mapNum][objIndex] = ObjSetup(70, 48, Item.PERSIM_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 80, Item.ORAN_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 67, Item.BRIGHT_POWDER, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(77, 32, Item.SHED_SHELL, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(87, 80, Item.POWER_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(74, 25, Item.FOCUS_SASH, mapNum);
		
		gp.obj[mapNum][objIndex] = ResistBerrySetup(35, 58, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(13, 78, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ObjSetup(44, 8, Item.LUM_BERRY, mapNum, 5, 10);
		
		mapNum = 7;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(39, 42, Item.BLACK_GLASSES, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 44, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 45, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 36, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 38, Item.REPEL, mapNum);
		
		mapNum = 8;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(30, 51, Item.EXPERT_BELT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(33, 50, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(27, 43, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 49, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 38, Item.REVIVE, mapNum);
		
		mapNum = 9;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(41, 34, Item.SHARP_BEAK, mapNum);
		
		mapNum = 11;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(91, 43, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(60, 54, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 50, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 64, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 63, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(53, 55, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(55, 45, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 56, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(50, 66, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 70, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 69, Item.ANTIDOTE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 75, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 78, Item.LEAF_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 69, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(26, 57, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(80, 53, Item.TM40, mapNum); // facade
		
		gp.obj[mapNum][objIndex] = ObjSetup(11, 49, Item.TM09, mapNum); // leaf blade
		gp.obj[mapNum][objIndex] = ObjSetup(32, 48, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 40, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 38, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(33, 35, Item.BURN_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 34, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 22, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 26, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 32, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(46, 41, Item.SILVER_POWDER, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(42, 37, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(13, 66, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(38, 30, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ObjSetup(14, 49, Item.ORAN_BERRY, mapNum, 15, 30);
		
		mapNum = 13;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(35, 35, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(20, 48, Item.TM37, mapNum); // rock tomb
		gp.obj[mapNum][objIndex] = ObjSetup(20, 45, Item.FREEZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 30, Item.QUIET_MINT, mapNum); // quiet
		gp.obj[mapNum][objIndex] = ObjSetup(31, 8, Item.TM72, mapNum); // ice spinner (v.cross)
		
		gp.obj[mapNum][objIndex] = ObjSetup(10, 72, Item.POISON_BARB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(26, 8, Item.WIDE_LENS, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 30, Item.GANLON_BERRY, mapNum, 3, 6);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 47, Item.APICOT_BERRY, mapNum, 3, 6);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 73, Item.FOCUS_SASH, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(28, 73, Item.RED_CARD, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 34, Item.MENTAL_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(79, 25, Item.TM73, mapNum); // gyro ball
		
		mapNum = 14;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(29, 48, Item.TM57, mapNum); // charge beam
		gp.obj[mapNum][objIndex] = ObjSetup(43, 44, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 35, Item.BRAVE_MINT, mapNum); // brave
		gp.obj[mapNum][objIndex] = ObjSetup(39, 48, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 43, Item.MAGNET, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 30, Item.WISE_GLASSES, mapNum);
		
		mapNum = 15;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(33, 39, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 15, Item.PARALYZE_HEAL, mapNum);

		mapNum = 16;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(44, 33, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 38, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(22, 27, Item.TM27, mapNum); // taunt
		gp.obj[mapNum][objIndex] = ObjSetup(26, 27, Item.TM14, mapNum); // drain punch
		gp.obj[mapNum][objIndex] = ObjSetup(46, 28, Item.BOTTLE_CAP, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(60, 41, Item.WEAKNESS_POLICY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(62, 32, Item.BLUNDER_POLICY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(72, 32, Item.FULL_RESTORE, mapNum);
		if (gp.player.p.choiceChoice != null) gp.obj[mapNum][objIndex] = ObjSetup(77, 41, gp.player.p.choiceChoice, mapNum);
		
		mapNum = 17;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(43, 38, Item.TM61, mapNum); // smack down
		gp.obj[mapNum][objIndex] = ObjSetup(53, 38, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(63, 38, Item.HYPER_POTION, mapNum);
		
		mapNum = 18;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(55, 33, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 35, Item.RARE_CANDY, mapNum);
		
		mapNum = 22;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(60, 15, Item.TM28, mapNum); // flame charge
		gp.obj[mapNum][objIndex] = ObjSetup(94, 15, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(72, 18, Item.BURN_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(82, 14, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 14, Item.RAWST_BERRY, mapNum, 15, 30);
		gp.obj[mapNum][objIndex] = ObjSetup(55, 12, Item.FOCUS_SASH, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 14, Item.POWER_HERB, mapNum);
		
		mapNum = 24;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(69, 84, Item.TM69, mapNum); // rock polish
		gp.obj[mapNum][objIndex] = ObjSetup(67, 75, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 52, Item.TM68, mapNum); // flash
		gp.obj[mapNum][objIndex] = ObjSetup(58, 50, Item.TM56, mapNum); // sparkle strike
		gp.obj[mapNum][objIndex] = ObjSetup(81, 57, Item.LIGHT_CLAY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(87, 57, Item.HEAVY$DUTY_BOOTS, mapNum);
		
		mapNum = 25;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(77, 68, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(58, 74, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 79, Item.SOFT_SAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(45, 84, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(44, 70, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 60, Item.MENTAL_HERB, mapNum);
		
		mapNum = 26;
		gp.obj[mapNum][objIndex] = ObjSetup(62, 69, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 69, Item.IMPISH_MINT, mapNum); // impish
		
		mapNum = 27;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(65, 68, Item.ENCHANTED_AMULET, mapNum);
		
		mapNum = 28;
		gp.obj[mapNum][objIndex] = ResistBerrySetup(18, 46, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 43, Item.ANTIDOTE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 46, Item.TM83, mapNum); // captivate
		gp.obj[mapNum][objIndex] = ObjSetup(52, 43, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(82, 23, Item.TM89, mapNum); // acrobatics
		gp.obj[mapNum][objIndex] = ObjSetup(43, 37, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(83, 14, Item.THROAT_SPRAY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 37, Item.DAWN_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 34, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(71, 34, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 23, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(28, 29, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(47, 48, Item.BIG_ROOT, mapNum);
		
		mapNum = 33;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(22, 6, Item.FOCUS_BAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 21, Item.FREEZE_HEAL, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(68, 11, Item.SPELL_TAG, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 11, Item.WIKI_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(73, 21, Item.RED_CARD, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(71, 24, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(75, 22, mapNum, 5, 15);
		
		gp.obj[mapNum][objIndex] = ObjSetup(25, 80, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 82, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 75, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 76, Item.LEAF_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(14, 39, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 43, Item.CAREFUL_MINT, mapNum); // careful
		gp.obj[mapNum][objIndex] = ObjSetup(42, 39, Item.TM05, mapNum); // body slam
		gp.obj[mapNum][objIndex] = ObjSetup(51, 52, Item.LIFE_ORB, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(57, 56, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(57, 78, mapNum, 5, 15);
		
		gp.obj[mapNum][objIndex] = ObjSetup(64, 70, Item.TM30, mapNum); // u-turn
		gp.obj[mapNum][objIndex] = ObjSetup(72, 69, Item.TM53, mapNum); // volt switch
		
		mapNum = 35;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(7, 56, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 62, Item.METAL_COAT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 77, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(46, 68, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 76, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(62, 73, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 78, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(91, 57, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 41, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 41, Item.CLEAR_AMULET, mapNum);
		
		mapNum = 36;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(12, 52, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 32, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(49, 56, Item.MODEST_MINT, mapNum); // modest
		gp.obj[mapNum][objIndex] = ObjSetup(61, 28, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 55, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(58, 51, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 52, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(68, 54, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 52, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 20, Item.TM86, mapNum); // x-scissor
		gp.obj[mapNum][objIndex] = ObjSetup(38, 47, Item.AIR_BALLOON, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(49, 32, Item.WHITE_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(83, 51, mapNum, 5, 15);
		
		mapNum = 38;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(16, 34, Item.SERIOUS_MINT, mapNum); // serious
		gp.obj[mapNum][objIndex] = ObjSetup(41, 34, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 7, Item.ICE_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(67, 30, Item.NEVER$MELT_ICE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 30, Item.FREEZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 75, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 77, Item.TM48, mapNum); // scald
		gp.obj[mapNum][objIndex] = ObjSetup(54, 78, Item.ADAMANT_MINT, mapNum); // adamant
		gp.obj[mapNum][objIndex] = ObjSetup(54, 79, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(48, 67, mapNum, 5, 15);
		
		mapNum = 41;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(40, 45, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 45, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(22, 45, Item.TM62, mapNum); // bug buzz
		gp.obj[mapNum][objIndex] = ObjSetup(16, 41, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 18, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 17, Item.LOADED_DICE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 29, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 17, Item.TERRAIN_EXTENDER, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 18, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(29, 42, Item.PARALYZE_HEAL, mapNum);
		
		mapNum = 56;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.EVERSTONE, mapNum);
		
		mapNum = 59;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.TOXIC_ORB, mapNum);
		
		mapNum = 60;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(39, 45, Item.LEFTOVERS, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 35, Item.DRAGON_FANG, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 43, Item.MUSCLE_BAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 36, Item.TM02, mapNum); // dragon claw
		
		mapNum = 61;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.SCOPE_LENS, mapNum);
		
		mapNum = 64;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.BLUNDER_POLICY, mapNum);
		
		mapNum = 69;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.WEAKNESS_POLICY, mapNum);
		
		mapNum = 77;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(15, 22, Item.TM29, mapNum); // liquidation
		gp.obj[mapNum][objIndex] = ObjSetup(11, 10, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(86, 11, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(90, 12, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(81, 28, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(94, 28, Item.CALM_MINT, mapNum); // calm
		gp.obj[mapNum][objIndex] = ObjSetup(69, 43, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 40, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(88, 51, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(86, 44, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(76, 62, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(85, 75, Item.DAWN_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(42, 76, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(18, 73, Item.MAX_REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(26, 63, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(7, 63, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(67, 5, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(90, 5, Item.TM04, mapNum); // calm mind
		gp.obj[mapNum][objIndex] = ObjSetup(7, 35, Item.MENTAL_HERB, mapNum);
		
		mapNum = 78;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(54, 69, Item.CALM_MINT, mapNum); // calm
		
		mapNum = 80;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(22, 11, Item.JOLLY_MINT, mapNum); // jolly
		gp.obj[mapNum][objIndex] = ObjSetup(21, 25, Item.TM90, mapNum); // iron blast
		gp.obj[mapNum][objIndex] = ObjSetup(28, 18, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 13, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 11, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(23, 34, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 55, Item.TIMID_MINT, mapNum); // timid
		gp.obj[mapNum][objIndex] = ObjSetup(27, 72, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(87, 46, Item.TM65, mapNum); // shadow claw
		gp.obj[mapNum][objIndex] = ObjSetup(15, 69, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(50, 81, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(43, 79, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(17, 30, mapNum, 5, 15);
		
		mapNum = 83;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(24, 79, Item.TIMID_MINT, mapNum); // timid
		gp.obj[mapNum][objIndex] = ObjSetup(21, 54, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(10, 56, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(33, 57, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(50, 80, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 74, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(62, 73, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(58, 63, Item.TM85, mapNum); // rock slide
		gp.obj[mapNum][objIndex] = ObjSetup(75, 50, Item.ASSAULT_VEST, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(67, 55, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(65, 49, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(75, 63, Item.STARF_BERRY, mapNum, 2, 5);
		
		mapNum = 85;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(16, 55, Item.QUIET_MINT, mapNum); // quiet
		gp.obj[mapNum][objIndex] = ObjSetup(47, 60, Item.TM55, mapNum); // magic blast
		gp.obj[mapNum][objIndex] = ObjSetup(45, 70, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 76, Item.PP_UP, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(80, 63, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(88, 59, Item.MAX_REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(72, 42, Item.PETTICOAT_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(93, 35, Item.VALIANT_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(82, 26, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(90, 28, Item.THROAT_SPRAY, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(80, 31, mapNum, 5, 15);
		
		mapNum = 90;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(56, 20, Item.DUSK_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 18, Item.TM87, mapNum); // poison jab
		gp.obj[mapNum][objIndex] = ObjSetup(55, 9, Item.CAREFUL_MINT, mapNum); // careful
		gp.obj[mapNum][objIndex] = ObjSetup(29, 8, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 37, Item.PP_MAX, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(69, 13, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 11, Item.COVERT_CLOAK, mapNum);
		
		mapNum = 95;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(44, 63, Item.TM32, mapNum); // zing zap
		gp.obj[mapNum][objIndex] = ObjSetup(56, 58, Item.TIMID_MINT, mapNum); // timid
		gp.obj[mapNum][objIndex] = ObjSetup(56, 48, Item.HYPER_POTION, mapNum);
		
		mapNum = 96;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(48, 57, Item.MAX_REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 47, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 63, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 60, Item.DUSK_STONE, mapNum);
		
		mapNum = 97;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(42, 53, Item.MODEST_MINT, mapNum); // modest
		gp.obj[mapNum][objIndex] = ObjSetup(46, 47, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 48, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 59, Item.REPEL, mapNum);
		
		mapNum = 98;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(53, 56, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(44, 62, Item.TM25, mapNum); // thunderbolt
		
		mapNum = 99;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(50, 54, Item.THUNDER_SCALES_FOSSIL, mapNum);
		
		mapNum = 100;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(54, 62, Item.DUSK_SCALES_FOSSIL, mapNum);
		
		mapNum = 101;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(47, 32, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 41, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 50, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(45, 58, Item.MAX_REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 70, Item.MAX_POTION, mapNum);
		
		mapNum = 102;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(39, 56, Item.ADAMANT_MINT, mapNum); // adamant
		gp.obj[mapNum][objIndex] = ObjSetup(37, 49, Item.TM58, mapNum); // dragon pulse
		gp.obj[mapNum][objIndex] = ObjSetup(42, 38, Item.REVIVE, mapNum);
		
		mapNum = 103;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(44, 36, Item.TM33, mapNum); // psychic fangs
		gp.obj[mapNum][objIndex] = ObjSetup(56, 42, Item.JOLLY_MINT, mapNum); // jolly
		gp.obj[mapNum][objIndex] = ObjSetup(39, 56, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 54, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 35, Item.ABILITY_CAPSULE, mapNum);
		
		mapNum = 104;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(54, 61, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 61, Item.HM06, mapNum); // whirlpool
		
		mapNum = 105;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(33, 17, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(29, 26, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 30, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 53, Item.AWAKENING, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(20, 36, Item.TM20, mapNum); // earthquake
		gp.obj[mapNum][objIndex] = ObjSetup(29, 47, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 47, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 45, Item.PP_MAX, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 67, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 86, Item.MAX_ELIXIR, mapNum);
		
		mapNum = 107;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(53, 90, Item.FULL_RESTORE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(84, 66, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 66, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 56, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(73, 26, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(60, 26, Item.BRAVE_MINT, mapNum); // brave
		gp.obj[mapNum][objIndex] = ObjSetup(50, 26, Item.SERIOUS_MINT, mapNum); // serious
		gp.obj[mapNum][objIndex] = ObjSetup(49, 35, Item.ICE_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(27, 28, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 41, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 33, Item.CALM_MINT, mapNum); // calm
		gp.obj[mapNum][objIndex] = ObjSetup(20, 32, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(17, 67, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(19, 78, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 88, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 82, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(83, 70, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(69, 51, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(69, 32, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(67, 34, Item.BOLD_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 60, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 57, Item.DAWN_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(50, 57, Item.DUSK_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 59, Item.TM06, mapNum); // shadow ball
		gp.obj[mapNum][objIndex] = ObjSetup(18, 35, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 72, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(13, 54, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(17, 37, Item.WHITE_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 63, Item.IMPISH_MINT, mapNum); // impish
		gp.obj[mapNum][objIndex] = ObjSetup(42, 70, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(47, 48, Item.PP_MAX, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 48, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 43, Item.MAX_ELIXIR, mapNum);
		
		mapNum = 109;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(79, 79, Item.TM71, mapNum); // stone edge
		gp.obj[mapNum][objIndex] = ObjSetup(87, 91, Item.BOLD_MINT, mapNum); // bold
		gp.obj[mapNum][objIndex] = ObjSetup(82, 92, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(76, 66, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 50, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(45, 32, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(68, 87, mapNum, 5, 15);
		
		mapNum = 110;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(44, 36, Item.TM08, mapNum); // bulk up
		gp.obj[mapNum][objIndex] = ObjSetup(19, 74, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 77, Item.CAREFUL_MINT, mapNum); // careful
		gp.obj[mapNum][objIndex] = ObjSetup(20, 68, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(28, 74, Item.ROCKY_HELMET, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(47, 51, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 77, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 51, Item.BURN_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(18, 69, Item.ABILITY_CAPSULE, mapNum);
		
		mapNum = 115;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(82, 71, Item.FREEZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(62, 65, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 76, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 65, Item.AIR_BALLOON, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 66, Item.CHOICE_SCARF, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 77, Item.ANTIDOTE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 82, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(77, 88, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 65, Item.TM19, mapNum); // photon geyser
		gp.obj[mapNum][objIndex] = ObjSetup(26, 70, Item.FULL_RESTORE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(29, 84, Item.MICLE_BERRY, mapNum, 2, 5);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(42, 67, mapNum, 5, 15);
		
		mapNum = 117;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(59, 76, Item.TM07, mapNum); // focus blast
		
		mapNum = 119;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(21, 65, Item.QUICK_CLAW, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(20, 79, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(42, 76, Item.HARD_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 89, Item.TWISTED_SPOON, mapNum);
		
		gp.obj[mapNum][objIndex] = ObjSetup(42, 67, Item.COSMIC_CORE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 44, Item.SITRUS_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 68, Item.RAWST_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 70, Item.CHERI_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(29, 69, Item.PECHA_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 68, Item.CHESTO_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 66, Item.PERSIM_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ObjSetup(12, 87, Item.WIKI_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(21, 87, mapNum, 5, 15);
		
		mapNum = 121;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 39, Item.BLACK_SLUDGE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 36, Item.TM36, mapNum); // sludge bomb
		gp.obj[mapNum][objIndex] = ObjSetup(26, 35, Item.CHOICE_BAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 35, Item.CHOICE_SPECS, mapNum);
		
		mapNum = 124;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(12, 15, Item.FULL_RESTORE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(18, 26, Item.FOCUS_SASH, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(17, 32, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(8, 40, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(23, 46, Item.REVIVE, mapNum);
		
		gp.obj[mapNum][objIndex] = ResistBerrySetup(48, 88, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ObjSetup(45, 76, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 84, Item.SERIOUS_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 69, Item.AIR_BALLOON, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 72, Item.THROAT_SPRAY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(84, 93, Item.ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(91, 89, Item.BURN_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(91, 83, Item.RAWST_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(84, 79, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(79, 58, Item.ADAMANT_MINT, mapNum);
		
		mapNum = 137;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(70, 67, Item.POWER_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 81, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(73, 81, Item.QUIET_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 71, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(87, 79, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(86, 62, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(44, 75, Item.HEAT_ROCK, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 75, Item.SMOOTH_ROCK, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 73, Item.DAMP_ROCK, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 74, Item.ICY_ROCK, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 52, Item.TM93, mapNum); // earth power
		
		mapNum = 138;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(35, 56, Item.WHITE_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 76, Item.BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 68, Item.MODEST_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 50, Item.BRAVE_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(48, 76, Item.RARE_CANDY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(55, 58, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 69, Item.BOTTLE_CAP, mapNum);
		
		mapNum = 139;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(45, 70, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 50, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 70, Item.MAX_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 60, Item.MAX_REVIVE, mapNum);
		
		mapNum = 140;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(62, 66, Item.TM34, mapNum); // magic tomb
		gp.obj[mapNum][objIndex] = ObjSetup(46, 60, Item.TIMID_MINT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 54, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 51, Item.TM35, mapNum); // flamethrower
		gp.obj[mapNum][objIndex] = ObjSetup(39, 43, Item.GOLD_BOTTLE_CAP, mapNum);
		
		mapNum = 141;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(23, 73, Item.TM78, mapNum); // swords dance
		gp.obj[mapNum][objIndex] = ObjSetup(23, 67, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 59, Item.RED_CARD, mapNum);
		
		mapNum = 144;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(20, 23, Item.LIECHI_BERRY, mapNum, 2, 5);
		gp.obj[mapNum][objIndex] = ObjSetup(24, 29, Item.PETAYA_BERRY, mapNum, 2, 5);
		
		mapNum = 146;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(67, 48, Item.EVIOLITE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 38, Item.TM47, mapNum); // star storm
	}

	public void setNPC() {
		boolean[] flags = gp.player.p.flags;
		int mapNum = 0;
		
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 72, 48, 0);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 18, 18, 1);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 19, 2);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 27, 3);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 21, 31, 4);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 21, 14, 102);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 7, 175);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 8, 176);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 82, 11, 177);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 11, 178);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 16, 179);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 63, 14, 329);
		
		if (!flags[15]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 85, 5, "The road is closed from this direction, there's a MASSIVE sinkhole on the other side of this gate.\nIf you come from Schrice City straight North of here it should be clear.", 15);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 60, 37, 259);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 56, 40, 260);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 55, 37, 261);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 63, 40, 262);
		
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 60, 44, "The Pokemon here are going FERAL over these berry trees. Come back later!", 15);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 68, 63, 266);
		
		index = 0;
		
		// Nurses/PCs
		gp.npc[1][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[1][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[5][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[5][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[19][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[19][index] = NPCSetup(NPC_PC, 35, 36, -1);
		
		// Clerks
		gp.npc[2][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		gp.npc[6][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		gp.npc[20][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		
		mapNum = 3;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 34, "Sorry, we are blocking the road because this gentleman here demands we look for a young trainer headed this way. You might want to ask him about it.");
		gp.npc[mapNum][index] = NPCSetup(AVERY, 35, 39, "Oh! You must be that young trainer Scott mentioned! I'm his quite elegant father Avery!", true); // Avery
		
		mapNum = 4;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 81, 61, "Sorry, but the gym leader has been busy with mail these past few weeks, so gym battles are unavailable. You may check the post office for more details.");
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 66, 79, "Yo, scram kid! This is some top secret space operation that you don’t got the clearance to see! Move it!", true);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 32, 62, 18);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 23, 65, 19); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 32, 68, 20); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 34, 76, 21); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 45, 76, 22); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 15, 70, 103);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 11, 70, 23);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 72, 37, 98);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 77, 34, 99);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 76, 30, 100);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 72, 28, 101);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 44, 81, 263);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 44, 68, 359);
		
		mapNum = 7;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 28, 37, 5);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 28, 41, 6);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 37, 32, 7);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 37, 36, 8);
		
		mapNum = 8;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 32, 39, 9);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 28, 41, 10);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 36, 42, 11);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 36, 47, 12);
		gp.npc[mapNum][index] = NPCSetup(RICK, 40, 48, 13);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 28, 49, 258);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 37, 38, "Oh, thank Arceus! These strange spaced-themed goons took over this warehouse.", 15, true, "Thank you so much for your help! Those guys are up to no good.");
		
		mapNum = 9;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 42, 14);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 27, 39, 15);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 42, 34, 16);
		gp.npc[mapNum][index] = NPCSetup(ROBIN, 38, 28, 17);
		
		mapNum = 10;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(ALAKAZAM, 30, 38, "Vxxxvhh...");
		gp.npc[mapNum][index] = NPCSetup(RYDER, 29, 38, "Oh! Uh, mabuhay!", true);
		
		mapNum = 11;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 69, 53, 24);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 50, 60, 25);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 59, 62, 26);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 76, 56, 27);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 53, 47, 28);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 53, 53, 29);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 76, 47, 30);
		
		gp.npc[mapNum][index] = NPCSetup(FRED_UP, 43, 61, 34);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 40, 66, 31);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 36, 67, 32);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 38, 78, 33);
		
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE_FULL, 19, 49, -1);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 43, 35);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 15, 37, 36);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 19, 37, 37);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 21, 33, 38);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 21, 39);
		
		mapNum = 13;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 20, 63, "Oh hello there kid, I'm a research assistant for the Professor that specializes in Electric forms of Pokemon.", 33, true, "");
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 29, 49, 104);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 49, 105);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 29, 34, 71);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 19, 37, 72);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 35, 24, 73);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 18, 74);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 44, 18, 360);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 54, 17, 362);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 71, 17, 363);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 59, 28, 361);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 79, 11, 364);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 82, 28, 365);
		
		//gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 20, 63, "", 33, true, ""); // TODO: regional trades
		
		mapNum = 14;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 36, 41, 44);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 27, 30, 40);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 38, 38, 41);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 25, 43, 42);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 30, 43, 43);
		gp.npc[mapNum][index] = SetupStaticEncounter(205, 34, 31, 366, 37);
		
		mapNum = 16;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 29, 45);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 26, 29, 46);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 46, 29, 47);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 21, 39, 48);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 62, 39, 273);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 67, 42, 274);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 72, 41, 275);
		gp.npc[mapNum][index] = SetupStaticEncounter(197, 32, 28, 367, 39);
		gp.npc[mapNum][index] = NPCSetup(STANFORD, 31, 28, "*growls* Um, what the hell do you want? Oh, right...", 41, true, "");
		
		mapNum = 17;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 45, 49);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 45, 50);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 44, 51);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 44, 52);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 43, 53);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 43, 54);
		
		if (!flags[3]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 49, 53, "Quick! Team Nuke is taking over our office! Please help!", 3);
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 57, 53, "Quick! Team Nuke is taking over our office! Please help!", 3);
		} else {
			gp.npc[mapNum][index++] = null;
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 18;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 48, 45, 55);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 45, 38, 146);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 46, 33, "That was an impressive battle! I found a rare MAGIC Pokemon, but after watching that, you'd be a better trainer. Here!", 12, true, "Enjoy all the wonders of the MAGIC type!");
		
		mapNum = 21;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 55, 60, 56);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 55, 62, 57);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 72, 52, 58);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 72, 54, 59);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 73, 57, 60);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 75, 57, 61);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 72, 46, 62);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 72, 48, 63);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 54, 46, 64);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 54, 48, 65);
		
		gp.npc[mapNum][index] = NPCSetup(STANFORD, 63, 39, 66);
		
		mapNum = 22;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 80, 8, 67);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 78, 12, 68);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 72, 10, 69);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 69, 14, 70);
		
		mapNum = 23;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 45, "Wow, there's a lot of lava over there. It's definitely not safe for any newbie trainers. If I see any of them coming this way... I swear to god...", 5);
		
		mapNum = 24;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 65, 63, 75);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 79, 59, 267);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 83, 55, 268);
		
		mapNum = 25;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 70, 65, 76);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 70, 80, 77);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 66, 87, 78);
		
		mapNum = 26;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 58, 70, 79);
		
		index = 0;
		// Nurses/PCs
		gp.npc[29][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[29][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[39][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[39][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[86][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[86][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[111][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[111][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[125][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[125][index] = NPCSetup(NPC_PC, 35, 36, -1);
		
		// Clerks
		gp.npc[30][index] = NPCSetup(NPC_MARKET, 31, 41, -1, true, Item.REPEL, Item.POKEBALL, Item.KLEINE_BAR, Item.BOTTLE_CAP, Item.TM49, Item.TM51);
		gp.npc[40][index] = NPCSetup(NPC_MARKET, 34, 38, -1, true, Item.TM12, Item.TM13, Item.TM15, Item.TM16, Item.TM23, Item.TM24);
		gp.npc[45][index] = NPCSetup(NPC_CLERK, 30, 39, -1);
		gp.npc[87][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		gp.npc[89][index] = NPCSetup(NPC_MARKET, 24, 36, -1, true, Item.TM45, Item.TM50, Item.TM54, Item.TM74, Item.TM75,
                Item.TM76, Item.TM77, Item.TM79, Item.TM80, Item.TM81, Item.TM82, Item.TM95);
		gp.npc[112][index] = NPCSetup(NPC_MARKET, 31, 41, -1, true, Item.MAX_ELIXIR, Item.PP_UP, Item.TM41, Item.TM42, Item.TM43, Item.TM44, Item.TM91);
		gp.npc[126][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		gp.npc[132][index] = NPCSetup(NPC_CLERK, 30, 39, -1);
		gp.npc[133][index] = NPCSetup(NPC_MARKET, 34, 38, -1, true, Item.TM46, Item.TM63, Item.TM38);
		
		mapNum = 28;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 12, 52, 85);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 13, 55, 86);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 30, 50, 80);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 50, 52, 87);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 52, 55, 81);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 56, 46, 82);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 60, 49, 88);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 38, 36, 83);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 50, 36, 84);
		
		if (!flags[5]) {
			gp.npc[mapNum][index] = NPCSetup(FRED_DOWN, 87, 45, 89);
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(FRED_DOWN, 87, 45, 89), mapNum);
		}
		
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 77, 26, 95);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 82, 20, 96);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 87, 26, 97);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 75, 69, 202);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 78, 83, 203);
		
		mapNum = 31;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 48, 50, 90);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 65, 61, 91);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 45, 63, 92);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 38, 70, 93);
		gp.npc[mapNum][index] = NPCSetup(MILLIE, 51, 61, 94);
		
		mapNum = 32;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 40, "Back in my day, I used to be a top angler. Felt good reeling in them in, but I think I've outgrown it.", 49, true, "Look at water and press 'A' to fish!");
		
		mapNum = 33;
		index = 0;
		if (gp.player.p.badges >= 5 && !flags[19]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 62, 17, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders went to Ghostly Woods and is planning something evil.", 19);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 49, 78, 195);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 40, 84, 196);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 73, 197);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 81, 198);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 25, 45, 199);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 32, 41, 200);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 41, 44, 201);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 71, 18, 269);
		
		mapNum = 36;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 20, 45, 106);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 39, 47, 107);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 42, 27, 108);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 44, 32, 109);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 60, 31, 110);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 53, 44, 111);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 65, 48, 112);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 82, 39, 113);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 30, 114);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 76, 25, 115);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 91, 43, 116);
		
		if (gp.player.p.badges >= 5 && !flags[19]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 15, 46, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders go to Ghostly Woods and is planning something evil.", 19);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 38;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 53, 7, 327);
		
		if (!flags[8] || !flags[9]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 62, 41, "What is going on at the school?! Where is everybody?!?!");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 74, 138);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 74, 139);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 85, 140);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 36, 84, 141);
		
		mapNum = 43;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 31, 42, 328);
		
		mapNum = 41;
		index = 0;
		
		if (!flags[7]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 13, 24, "Room B is locked! Where could Key B be?", 7);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		if (!flags[6]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 50, 24, "Room A is locked! Where could Key A be?", 6);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 51, 21, 117);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 51, 27, 118);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 12, 21, 119);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 12, 27, 120);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 14, 22, 121);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 14, 26, 122);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 19, 24, 123);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 21, 22, 124);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 25, 22, 125);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 23, 16, 126);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 32, 24, 127);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 37, 24, 128);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 40, 18, 129);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 40, 21, 130);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 36, 17, 131);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 32, 18, 132);
		
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 29, 18, "Who are these people?? Have you cleared both rooms yet?", 31, true, "Thank you for restoring peace to our wonderful school!");
		
		mapNum = 44;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 60, 61, 133);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 66, 57, 134);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 60, 52, 135);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 66, 46, 136);
		gp.npc[mapNum][index] = NPCSetup(GLACIUS, 63, 39, 137);
		
		mapNum = 46;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Check your team's Hidden Power types here!", true);
		
		mapNum = 47;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Hello there, welcome to Bananaville's Pokemon Ranch!", 6, true, "Why have one partner when you can have two, am I right?");
		
		mapNum = 48;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 42, 142);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 42, 143);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 31, 36, 144);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 39, 145);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 24, 37, "Hello, and welcome to the Poundtown, where your dog can train and play at the same time! Feel free to look around at all of our amenities.", 10, true, "That little pup treating you alright? I bet he'll grow strong if you give it lots of love!");
		
		mapNum = 49;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Deep below ELECTRIC TUNNEL there's a secret trail called SHADOW PATH.", 13, true, "Now don't go telling the feds I gave you that. It's definitely illegal.");
		
		mapNum = 50;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "WOAOAOHAOH!!! Hehehehehe I just popped a naughty yerkocet!! Pick one of these NUTTY \"starters\" tehehee", 14, true, "YEEEOOEEOOOOeeeoooeeooeee.....");
		
		mapNum = 51;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(GRANDMOTHER, 31, 41, "Why what a surprise! Hello there dear, I'm glad you could visit your grandmother before you left home.", 3, true, "Glad you could check in dear, but I'm fine. You still got an adventure ahead of you."); // Dad's Mom
		
		mapNum = 52;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(PROFESSOR, 31, 36, "Well hiya there son!", true); // Professor Dad
		gp.npc[mapNum][index] = NPCSetup(RESEARCHER, 31, 45, "Heh, you're definitely excited to head out there, but your Dad wanted to see you off before you start your adventure.");
		
		mapNum = 53;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_MARKET, 31, 41, -1, true, Item.ORAN_BERRY, Item.CHERI_BERRY, Item.CHESTO_BERRY, Item.PECHA_BERRY, Item.RAWST_BERRY, Item.ASPEAR_BERRY,
                Item.PERSIM_BERRY, Item.LUM_BERRY, Item.LEPPA_BERRY, Item.SITRUS_BERRY, Item.WIKI_BERRY, Item.OCCA_BERRY, Item.PASSHO_BERRY, Item.WACAN_BERRY, Item.RINDO_BERRY,
                Item.YACHE_BERRY, Item.CHOPLE_BERRY, Item.KEBIA_BERRY, Item.SHUCA_BERRY, Item.COBA_BERRY, Item.PAYAPA_BERRY, Item.TANGA_BERRY, Item.CHARTI_BERRY,
                Item.KASIB_BERRY, Item.HABAN_BERRY, Item.COLBUR_BERRY, Item.BABIRI_BERRY, Item.CHILAN_BERRY, Item.ROSELI_BERRY, Item.MYSTICOLA_BERRY, Item.GALAXEED_BERRY,
                Item.LIECHI_BERRY, Item.GANLON_BERRY, Item.SALAC_BERRY, Item.PETAYA_BERRY, Item.APICOT_BERRY, Item.STARF_BERRY, Item.MICLE_BERRY, Item.CUSTAP_BERRY);
		
		mapNum = 57;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Eyy! What brings a scrawny kid like you into the famous Guy Eddie’s culinary residence!", 9, true, "By the way, if you’re ever in Rawwar City, head over to my restaurant. You won’t wanna miss the Eddie special, Smoked Pie! It’s gonna be a blast burn!");
		
		mapNum = 58;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 33, 42, "AAAGHHHHH!! GET OUT THIEF!", 18, true, "Thank you so much for your kindness! I hope you have a great day :)");
		
		mapNum = 60;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 38, 38, 147);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 38, 42, 148);
		
		mapNum = 77;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 18, 149);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 24, 150);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 45, 30, 151);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 52, 64, 152);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 54, 37, 153);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 54, 52, 154);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 86, 33, 155);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 67, 34, 156);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 74, 61, 157);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 32, 45, 158);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 18, 20, 159);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 18, 24, 160);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 87, 15, 161);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 41, 162);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 47, 163);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 60, 164);
		
		mapNum = 80;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 19, 165);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 27, 22, 166);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 28, 167);
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 35, 41, 185);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 41, 51, 168);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 31, 64, 169);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 75, 57, 170);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 49, 74, 171);
		
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE_FULL, 25, 11, -1);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 17, 68, 257);
		
		mapNum = 83;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 48, 64, 172);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 48, 67, 173);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 60, 70, 180);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 61, 66, 181);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 65, 65, 182);
		
		mapNum = 85;
		index = 0;
		if (!flags[16]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 58, 64, "Talk to Grandpa (bottom house all the way southwest)", 16);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 69, 218);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 85, 64, 219);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 93, 54, 220);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 51, 221);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 85, 37, 222);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 88, 40, 223);
		
		if (!flags[17]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 63, 67, "EEEK! DON'T GO THIS WAY! I saw some really scary men dressed up in black go towards the woods this way. BE CAREFUL!", 17);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 88;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 75, 57, 186);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 81, 57, 187);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 85, 63, 188);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 45, 67, 189);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 41, 53, 190);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 51, 53, 191);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 41, 47, 192);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 55, 47, 193);
		gp.npc[mapNum][index] = NPCSetup(MINDY, 63, 35, 194);
		
		mapNum = 90;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 48, 10, 174);
		
		mapNum = 91;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 29, 45, 183);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 33, 45, 184);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Thank you so much for saving me! Here, take this as a gift!", 16, true, "Love you grandson. Keep doing good things!");
		
		mapNum = 92;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_MARKET, 22, 37, -1, true, Item.ADAMANT_MINT, Item.BOLD_MINT, Item.BRAVE_MINT, Item.CALM_MINT, Item.CAREFUL_MINT, Item.IMPISH_MINT,
                Item.JOLLY_MINT, Item.MODEST_MINT, Item.QUIET_MINT, Item.SERIOUS_MINT, Item.TIMID_MINT);
		
		mapNum = 93;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Would you like to remember a move? Which Pokemon should remember?", true);
		
		mapNum = 94;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "There have been 2 meteorites that have crashed into our region.", 18, true, "You should explore the Hearts of the ELECTRIC TUNNEL and SHADOW\nRAVINE. They're really pretty.");
		
		mapNum = 104;
		index = 0;
		if (!flags[20]) {
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 47, 204);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 47, 205);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 49, 206);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 49, 207);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 51, 208);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 51, 209);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 53, 210);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 53, 211);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 55, 212);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 55, 213);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 57, 214);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 57, 215);
			
			gp.npc[mapNum][index] = NPCSetup(FRED_UP, 44, 62, 216);
			gp.npc[mapNum][index] = NPCSetup(MAXWELL, 41, 59, 217);
		} else {
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 47, 204), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 47, 205), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 49, 206), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 49, 207), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 51, 208), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 51, 209), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 53, 210), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 53, 211), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 55, 212), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 55, 213), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 57, 214), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 57, 215), mapNum);
			
			GamePanel.volatileTrainers.put(NPCSetup(FRED_UP, 44, 62, 216), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(MAXWELL, 41, 59, 217), mapNum);
		}
		
		
		mapNum = 107;
		index = 0;
		if (!flags[19] && gp.player.p.grustCount >= 10) {
			gp.npc[mapNum][index] = NPCSetup(RICK, 46, 60, 234);
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(RICK, 46, 60, 234, false), mapNum);
		}
		
		if (gp.player.p.grustCount < 10) {
			int[] xCoords = new int[] {49, 78, 62, 61, 61, 79, 42, 75, 60, 17};
			int[] yCoords = new int[] {88, 80, 81, 76, 70, 59, 77, 44, 42, 72};
			
			for (int i = 224; i <= 233; i++) {
				if (gp.player.p.trainersBeat == null || !gp.player.p.trainersBeat[i]) {
					gp.npc[mapNum][index] = NPCSetup(GRUST, xCoords[i-224], yCoords[i-224], i);
				} else {
					gp.npc[mapNum][index++] = null;
				}
			}
		} else {
			index += 10;
		}
		if (!flags[29]) {
			//gp.npc[mapNum][index] = NPCSetup(ZURROARATR, 50, 58, 346);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		
		mapNum = 109;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 76, 89, 235);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 72, 236);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 49, 44, 237);
		
		if (!flags[21]) {
			gp.npc[mapNum][index] = NPCSetup(SCOTT_DOWN, 21, 40, 242);;
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(SCOTT_DOWN, 21, 40, 242), mapNum);
		}
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 44, 39, "I found this infant Pokemon abandoned here.", 22, true, "Please raise it with love and care!");
		
		mapNum = 110;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 84, 238);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 25, 78, 239);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 20, 56, 240);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 26, 54, 241);
		
		mapNum = 113;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 55, 58, 246);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 51, 56, 247);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 44, 58, 248);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 42, 56, 249);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 44, 49, 250);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 53, 49, 251);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 62, 47, 252);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 63, 41, 253);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 54, 43, 254);
		gp.npc[mapNum][index] = NPCSetup(RAYNA, 53, 30, 255);
		
		mapNum = 115;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 69, 243);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 62, 74, 244);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 41, 72, 245);
		
		mapNum = 118;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 41, "Hi! I can revive fossils for you!", true);
		
		mapNum = 119;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 32, 79, 264);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 37, 76, 265);
		
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 23, 76, "You're not ready to fight those guys over there yet, sorry bud.", 5);
		
		mapNum = 121;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 42, 270);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 37, 271);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 35, 272);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 34, "You're definitely not strong enough to go up here. Also, the items on the floor are really powerful, so you can only choose one for now.");
		
		mapNum = 122;
		index = 0;
		
		mapNum = 124;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 25, 21, 276); // DH
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 24, 24, 281);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 17, 22, 280);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 22, 32, 283);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 15, 32, 277); // DI
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 12, 43, 278); // DJ
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 24, 43, 279); // DK
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 16, 39, 284);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 15, 28, 282);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 65, 72, 285);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 69, 85, 286);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 84, 84, 288);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 91, 80, 289);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 79, 287);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 86, 72, 290);
		
		if (!flags[26]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 25, 82, "The gym is closed right now. Why, you ask? Because a goddamn Team Nuke member came here and KIDNAPPED one of our employees.\n\nYeah, what the hell is right! Last I saw him, he was bringing Marcus towards the volcano. I just hope that Marcus doesn't sue us...", 26);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 127;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 27, 39, "Welcome to the Blackjack table!", true);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 35, 39, "This game isn't ready yet!", true);
		
		mapNum = 128;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 55, 66, 291);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 49, 65, 292);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 56, 68, 293);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 47, 68, 294);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 52, 62, 295);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 58, 63, 296);
		gp.npc[mapNum][index] = NPCSetup(INVIS_UP, 55, 63, 297);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 58, 60, 298);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 52, 60, 299);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 59, 57, 300);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 55, 57, 301);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 49, 59, 302);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 54, 54, 303);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 53, 56, 304);
		gp.npc[mapNum][index] = NPCSetup(MERLIN, 53, 54, 305);
		
		mapNum = 129;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 37, 44, "Hi there! Here, you can exchange coins for money!", true);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 39, "Hi there! Here, you can exchange coins for prizes!", true);
		
		mapNum = 130;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 24, 36, "Aren't you lookin like you want some SPICE!", 25, true, "ICE SPICE is my idol <3");
		
		mapNum = 131;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_MARKET, 31, 41, -1, true, Item.EUPHORIAN_GEM, Item.LEAF_STONE, Item.DUSK_STONE, Item.DAWN_STONE, Item.ICE_STONE,
				Item.FIRE_STONE, Item.VALIANT_GEM, Item.PETTICOAT_GEM, Item.EVERSTONE, Item.HEAT_ROCK, Item.DAMP_ROCK, Item.SMOOTH_ROCK, Item.ICY_ROCK);
		
		mapNum = 137;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 70, 73, 306);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 72, 74, 307);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 37, 46, 312);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 39, 48, 313);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 37, 50, 314);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 35, 48, 315);
		
		mapNum = 138;
		index = 0;
		if (!flags[26]) {
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 53, 73, 311);;
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 53, 73, 311), mapNum);
		}
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 55, 72, "Thank you so much for saving me!", 26, true, "It's so hot in here. How the \"hell\" do I leave???");
		
		mapNum = 139;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 29, 73, 308);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 46, 50, 309);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 30, 55, 310);
		
		mapNum = 144;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 14, 17, 316);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 24, 16, 317);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 40, 40, 318);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 50, 42, 319);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 55, 44, 320);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 49, 321);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 54, 49, 322);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 57, 49, 323);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 57, 80, 324);
		
		mapNum = 146;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 79, 27, -1);
		if (!flags[28]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 82, 27, "Inside there is quite the function in there. You are only allowed to bring 10 of your strongest Pokemon, and that's it. Good luck.", true);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 148;
		index = 0;
		if (!flags[35] && flags[29]) {
			//gp.npc[mapNum][index] = NPCSetup(TRIWANDOLIZ, 50, 68, 326);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		
		mapNum = 149;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_PC_GAUNTLET, 53, 74, -1);
		if (!flags[28]) {
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 49, 77, 330);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 46, 76, 331);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 52, 76, 332);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 49, 73, 333);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 56, 67, 334);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 61, 71, 335);
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 56, 75, 336);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 42, 68, 337);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 36, 71, 338);
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 42, 74, 339);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 36, 65, 340);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 39, 59, 341);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 42, 65, 342);
			gp.npc[mapNum][index] = NPCSetup(RICK, 49, 62, 343);
			gp.npc[mapNum][index] = NPCSetup(FRED_DOWN, 53, 61, 344);
			gp.npc[mapNum][index] = NPCSetup(MAXWELL, 49, 56, 345);
		} else {
			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 49, 77, 330), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 46, 76, 331), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 52, 76, 332), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 49, 73, 333), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 56, 67, 334), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 60, 71, 335), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 56, 75, 336), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 42, 68, 337), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 38, 71, 338), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 42, 74, 339), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 36, 65, 340), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 39, 61, 341), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 42, 65, 342), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(RICK, 49, 62, 343), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(FRED_DOWN, 53, 61, 344), mapNum);
			GamePanel.volatileTrainers.put(NPCSetup(MAXWELL, 49, 56, 345), mapNum);
		}
		
		mapNum = 150;
		index = 0;
		if (!flags[36]) {
			//gp.npc[mapNum][index] = NPCSetup(DIFTERY, 46, 66, 325);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 152;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 16, 347);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 29, 22, 348);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 24, 24, 349);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 32, 37, 350);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 39, 38, 351);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 35, 352);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 50, 26, 353);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 57, 19, 354);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 59, 27, 355);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 56, 40, 356);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 48, 46, 357);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 44, 54, 358);
		
		// Nurses
		gp.npc[153][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[153][index] = NPCSetup(NPC_PC, 35, 36, -1);
		
		// Clerks
		gp.npc[154][index] = NPCSetup(NPC_CLERK, 30, 39, -1);
		gp.npc[155][index] = NPCSetup(NPC_MARKET, 34, 38, -1, true, Item.TM46, Item.TM63);
		
		mapNum = 161;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(ROBIN, 50, 37, "AGHH!", true);
		
		mapNum = 162;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DEFAULT, 31, 38, "Oh hello there, welcome to PROFESSOR PHOTON's humble abode!", true);
		
	}

	public void setInteractiveTile(int map) {
		int mapNum = 0;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 53, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(82, 59, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 72, 0, mapNum, map);
		
		mapNum = 4;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(17, 63, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(16, 68, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(10, 68, 0, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(74, 43, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(75, 24, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(85, 72, 0, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(66, 79, 13, mapNum, map);
		
		mapNum = 11;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 65, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(13, 40, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 35, 0, mapNum, map);
		
		SetupWhirlpool(mapNum, 15, 76, map);
		
		mapNum = 13;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(26, 7, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 9, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(26, 9, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 9, 0, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 10, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 9, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 8, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(26, 64, 34, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(19, 85, 36, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(48, 57, 36, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(15, 56, 47, mapNum, map);
		
		mapNum = 14;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 30, 8, mapNum, map);
		
		mapNum = 15;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupBarrier(27, 24, 38, mapNum, map);
		
		mapNum = 16;
		iIndex = 0;
		SetupWhirlpool(mapNum, 65, 35, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 27, 8, mapNum, map);
		
		mapNum = 18;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 40, 1, mapNum, map);
		
		mapNum = 21;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 60, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 52, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 50, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 45, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 43, 3, mapNum, map);
		
		mapNum = 22;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 13, 1, mapNum, map);
		
		mapNum = 24;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 70, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(70, 78, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(61, 43, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(44, 55, 2, 1, mapNum, map);
		SetupPit(mapNum, 49, 74, 147, 43, 49, map);
		
		mapNum = 25;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(73, 73, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(65, 85, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 79, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 77, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 77, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 76, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 75, 4, mapNum, map);
		
		mapNum = 27;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 68, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 68, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(61, 68, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(61, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 75, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 76, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 77, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 78, 4, mapNum, map);
		SetupPit(mapNum, 56, 82, 145, 39, 63, map);
		
		mapNum = 28;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(23, 42, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(23, 41, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(23, 40, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(26, 39, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 39, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(28, 39, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(29, 39, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 40, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 39, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 38, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 37, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 36, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 37, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 38, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 39, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 40, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 41, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 42, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(15, 53, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(29, 53, 0, mapNum, map);
		
		mapNum = 33;
		iIndex = 0;
		SetupWhirlpool(mapNum, 68, 61, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(66, 17, 0, mapNum, map);
		
		mapNum = 35;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(65, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(66, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(68, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(69, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(81, 63, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(82, 63, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(83, 63, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(76, 68, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(76, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(76, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(68, 75, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(69, 75, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 75, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(71, 75, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 53, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 47, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 47, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 47, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 56, 4, mapNum, map);
		
		SetupPit(mapNum, 58, 56, 95, 42, 49, map);
		
		mapNum = 52;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 37, 6, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(28, 37, 6, mapNum, map);
		
		mapNum = 60;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 40, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 40, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(36, 40, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 40, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 41, 1, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 36, 7, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 35, 8, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupBarrier(31, 38, 40, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupBarrier(31, 37, 44, mapNum, map);
		
		mapNum = 78;
		iIndex = 0;
		SetupPit(mapNum, 58, 71, 117, 61, 74, map);
		
		mapNum = 80;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(26, 15, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 48, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 49, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 50, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 53, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 53, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(37, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 54, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 55, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 59, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 59, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 58, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 57, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 55, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(71, 55, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(72, 55, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(22, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(21, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 74, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(43, 74, 4, mapNum, map);
		
		SetupPit(mapNum, 20, 61, 90, 45, 13, map); // top left
		SetupPit(mapNum, 11, 70, 90, 30, 16, map); // bottom left
		SetupPit(mapNum, 50, 64, 90, 58, 17, map); // middle
		SetupPit(mapNum, 81, 61, 90, 68, 17, map); // right
		
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(20, 59, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(13, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(81, 59, 4, mapNum, map);
		
		mapNum = 83;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(17, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(18, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(19, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 61, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 73, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 74, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 70, 4, mapNum, map);

		mapNum = 85;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(17, 76, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 67, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(75, 64, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(83, 66, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(85, 67, 0, mapNum, map);
		
		mapNum = 90;
		iIndex = 0;
		SetupPit(mapNum, 51, 13, 100, 50, 54, map); // middle
		SetupPit(mapNum, 29, 10, 101, 26, 51, map); // left
		SetupPit(mapNum, 78, 11, 101, 75, 52, map); // right
		
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 14, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 14, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 13, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 13, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 21, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 22, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 28, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 29, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(59, 30, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 17, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 17, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(73, 11, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(74, 11, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(27, 9, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(30, 14, 2, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(69, 15, 2, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(69, 12, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(60, 34, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(57, 36, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(27, 13, 3, 1, mapNum, map);
		
		mapNum = 95;
		iIndex = 0;
		SetupPit(mapNum, 56, 59, 96, 55, 51, map);
		
		mapNum = 96;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 56, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 56, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 56, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 56, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 56, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 59, 4, mapNum, map);
		SetupPit(mapNum, 48, 61, 97, 45, 58, map);
		SetupPit(mapNum, 39, 58, 97, 39, 54, map);
		
		mapNum = 97;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 43, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 44, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 50, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 49, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 53, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 53, 4, mapNum, map);
		SetupPit(mapNum, 48, 37, 98, 47, 48, map);
		
		mapNum = 101;
		iIndex = 0;
		SetupPit(mapNum, 49, 49, 102, 47, 37, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 47, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 48, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(29, 50, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 50, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 48, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 48, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 34, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 35, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 36, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(65, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(69, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(71, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 59, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 65, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 66, 4, mapNum, map);
		
		mapNum = 102;
		iIndex = 0;
		SetupPit(mapNum, 50, 57, 103, 50, 59, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(48, 41, 2, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(49, 41, 2, 1, mapNum, map);
		
		mapNum = 103;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 38, 2, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(46, 59, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(39, 58, 0, 1, mapNum, map);
		
		mapNum = 105;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 16, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 22, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 29, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 35, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 42, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 54, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 53, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 59, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 65, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 72, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 80, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 80, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(29, 39, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(25, 42, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(19, 42, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(40, 49, 3, 1, mapNum, map);
		
		mapNum = 107;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 62, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 22, 0, mapNum, map);
		
		mapNum = 109;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(77, 87, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(77, 84, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(52, 40, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(47, 38, 2, 1, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(72, 81, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(73, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(74, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 70, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 69, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 63, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(65, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 60, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 59, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(61, 57, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 57, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 52, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 51, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(64, 50, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 50, 4, mapNum, map);
		
		SetupPit(mapNum, 72, 79, 110, 23, 85, map); // bottom
		SetupPit(mapNum, 63, 67, 110, 28, 73, map); // middle
		SetupPit(mapNum, 58, 57, 110, 22, 62, map); // top
		
		mapNum = 110;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 80, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(36, 81, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(37, 81, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(38, 81, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(39, 81, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(33, 47, 3, 1, mapNum, map);
		
		mapNum = 115;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(80, 82, 1, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(81, 82, 1, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(82, 82, 1, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(45, 79, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(45, 80, 2, 2, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(30, 68, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(31, 77, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(32, 77, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(31, 84, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(32, 84, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(38, 72, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(39, 72, 3, 2, mapNum, map);
		
		mapNum = 119;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 87, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(21, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(22, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(23, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(24, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(25, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(26, 46, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(27, 46, 4, mapNum, map);
		
		mapNum = 124;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 74, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 74, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 74, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 76, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 76, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 82, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 83, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 89, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 89, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 89, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 87, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 87, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(68, 88, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(68, 89, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 79, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(72, 84, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(73, 84, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(74, 84, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(83, 89, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(83, 90, 4, mapNum, map);
		
		mapNum = 137;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(74, 80, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(79, 76, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(89, 76, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(74, 59, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(69, 52, 2, 1, mapNum, map);
		
		mapNum = 138;
		iIndex = 0;
		SetupPit(mapNum, 32, 59, 137, 37, 48, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(52, 52, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 56, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(52, 71, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(56, 74, 3, 1, mapNum, map);
		
		mapNum = 139;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(43, 72, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 69, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 65, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(61, 61, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 50, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(43, 57, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 64, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 67, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 72, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 72, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 72, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 73, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 73, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 73, 4, mapNum, map);
		
		mapNum = 140;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 50, 2, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 49, 2, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 48, 2, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 47, 2, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(78, 60, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(78, 67, 0, 1, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(32, 69, 1, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(31, 69, 1, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(30, 69, 1, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(29, 69, 1, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(71, 55, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(65, 55, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 57, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 63, 3, 1, mapNum, map);
		
		mapNum = 141;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(53, 63, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(61, 63, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 63, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 63, 3, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(64, 63, 3, 4, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(65, 77, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(52, 80, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(42, 80, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(47, 77, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(41, 75, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(26, 79, 3, 1, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 66, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 67, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 71, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 72, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 62, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 63, 4, mapNum, map);
		
		mapNum = 142;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 53, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(51, 54, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(55, 44, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(56, 44, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(59, 40, 2, 1, mapNum, map);
		
		mapNum = 144;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(25, 24, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(26, 24, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(27, 27, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(27, 28, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(32, 23, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(36, 18, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(45, 55, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(52, 53, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(59, 50, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 59, 2, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 63, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 64, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 65, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(61, 80, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 80, 4, mapNum, map);
		SetupPit(mapNum, 64, 80, 144, 11, 28, map); // lower TODO dest
		SetupPit(mapNum, 63, 67, 144, 11, 28, map); // middle TODO dest
		SetupPit(mapNum, 72, 74, 144, 11, 28, map); // upper TODO dest
		
		mapNum = 145;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 75, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(41, 74, 1, 1, mapNum, map);
		SetupPit(mapNum, 44, 62, 24, 46, 74, map);
		
		mapNum = 146;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupRockClimb(57, 43, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(57, 48, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(59, 44, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(60, 44, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(59, 49, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(60, 49, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 42, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 42, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 47, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(63, 47, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(70, 43, 2, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(70, 44, 2, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(70, 45, 2, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(74, 40, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(74, 34, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(75, 34, 3, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(80, 31, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(80, 32, 2, 2, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(87, 44, 2, 5, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(87, 43, 2, 5, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(87, 42, 2, 5, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(87, 41, 2, 5, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(87, 40, 2, 5, mapNum, map);
		
		mapNum = 147;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 59, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(47, 48, 1, 1, mapNum, map);
		
		mapNum = 149;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 68, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 67, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 66, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 62, 3, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 60, 3, mapNum, map);
		
		mapNum = 150;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 77, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 77, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 76, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 76, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 75, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 74, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 74, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 74, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 74, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 74, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 73, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(43, 72, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 72, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 72, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 71, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 71, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 70, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 69, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 69, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 68, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 68, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 68, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 67, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 66, 1, mapNum, map);
	}

	public void updateNPC(int map) {
		boolean[] flags = gp.player.p.flags;
		boolean[][] flag = gp.player.p.flag;
		// flags[0] is true after walking into first gate
		// flags[1] is true after beating Scott 1
		// flags[2] is true after beating Rick 1
		// flags[3] is true after beating all TN Grunts in the office
		// flags[4] is true after beating Scott 2
		// flags[5] is true after beating Fred 2
		// flags[6] is true after key A
		// flags[7] is true after key B
		// flags[8] is true after clearing room A
		// flags[9] is true after clearing room B
		// flags[10] is true after getting gift starter
		// flags[11] is true after getting gift dog
		// flags[12] is true after getting gift magic pokemon
		// flags[13] is true after getting gift fossil/ancient pokemon
		// flags[14] is true after getting gift "starter" pokemon
		// flags[15] is true after beating Scott 3
		// flags[16] is true after talking to Grandpa
		// flags[17] is true after beating Gym 5
		// flags[18] is true after getting gift E/S pokemon
		// flags[19] is true after beating Rick 2
		// flags[20] is true after beating Maxwell 1
		// flags[21] is true after beating Scott 4
		// flags[22] is true after getting Glurg Town gift
		// flags[23] is true after getting coins
		// flags[24] is true after being prompted that casino will auto-save
		// flags[25] is true after getting gift magmaclang
		// flags[26] is true after beating TN guy in MSJ
		// flags[27] is true after beating Fred 4
		// flags[28] is true after beating Maxwell 2 (and disbanding TN)
		// flags[29] is true after beating Zurroaratr
		// flags[30] is true after getting fishing rod
		// flags[31] is true after getting Surf
		// flags[32] is true after getting Exp. Share
		// flags[33] is true after getting Lucky Egg
		// flags[34] is true after beating Rick 3
		// flags[35] is true after fighting Triwandoliz
		// flags[36] is true after fighting Diftery
		
		/**
		 * First split
		 */
		if (flag[0][2]) { // Lab helper blocking exit
			gp.npc[52][1] = null;
		}
		if (!flag[0][4] || flag[0][5]) gp.npc[0][0] = null; // Scott 1
		if (flag[0][4] && !flag[0][5]) gp.npc[0][0] = NPCSetup(SCOTT_UP, 72, 48, 0);
		
		if (flag[0][5]) {
			gp.npc[3][0] = null;
			gp.npc[3][1] = null;
			if (gp.npc[55][0] == null) gp.npc[55][0] = NPCSetup(AVERY, 31, 41, "Oh, what an unexpected visit. Quite pleasant of you to drop by, but Scott isn't here right now. Do feel free to look around."
					+ "\nAh, I remember the days when I was in the Master Dojo, training to be a Gym Leader. I don't play dirty anymore, certainly not something an elegant Gym Leader would do."
					+ "\nScott speaks very highly of you, as a partner he can trust. I do hope you two are getting along, though I can imagine he's very clingy."
					+ "\nIt's quite a fine day here in Xhenos, reminds me of the Isle of Armor. Though it's much less humid here, thank heavens for that."
					+ "\nOh, yes I am a Psychic. But not a very good one, the best I can do is telekinesis. I come from a long bloodline of them actually."
					+ "\nIf you do notice that Scott has any psychic abilities, please inform me at once!"
					+ "\nOh, these long magnificent locks of mine? I just use a splendid shampoo imported from Galar called Weezfresh. Truly a wondrous product, you should try it."
					+ "\nOh, if you wanted to find Scott, you just missed him. You know, he used to spend most of his time with my Pokemon, like they were his friends."); // Avery
			gp.npc[23][0] = null;
			gp.npc[119][2] = null;
		}
		
		if (flag[0][12]) {
			gp.npc[4][1] = null;
			gp.npc[4][14] = NPCSetup(TN_UP, 44, 68, 359);
		} else {
			gp.npc[4][14] = null;
		}
		
		if (flag[0][13]) {
			gp.npc[4][14] = null;
		}
		
		if (flag[0][15]) {
			gp.npc[7][0] = null;
			gp.npc[7][1] = null;
			gp.npc[7][2] = null;
			gp.npc[7][3] = null;
			gp.npc[8][0] = null;
			gp.npc[8][1] = null;
			gp.npc[8][2] = null;
			gp.npc[8][3] = null;
			gp.npc[8][4] = null;
			gp.npc[8][5] = null;
			
			gp.npc[0][17] = null;
		}
		
		if (flag[0][16]) {
			gp.npc[161][0] = null;
			gp.npc[4][0] = null;
		}
		
		/**
		 * Second Split
		 */
		if (flag[1][0]) {
			gp.npc[11][7] = null;
		}
		
		if (flag[1][1]) {
			gp.npc[13][0] = null;
		}
		
		if (flag[1][3]) {
			gp.npc[10][0] = null;
			gp.npc[10][1] = null;
		}
		
		if (flag[1][5]) { // Rocky-E
			gp.npc[14][5] = null;
		}
		if (flag[1][7]) { // Poof-E
			gp.npc[16][7] = null;
		}
		if (flag[1][8]) {
			gp.npc[16][8] = null;
		}
		
		/**
		 * All of this is old and should be removed/reworked
		 */
		if (flags[3]) {
			gp.npc[17][6] = null;
			gp.npc[17][7] = null;
		}
		if (flags[4]) gp.npc[13][0] = null;
		if (flags[5]) gp.npc[28][9] = null;
		
		if (flags[7]) gp.npc[41][0] = null;
		if (flags[6]) gp.npc[41][1] = null;
		if (flags[8] && flags[9]) gp.npc[38][1] = null;
		if (flags[15]) gp.npc[0][13] = null;
		if (flags[16]) gp.npc[85][0] = null;
		if (flags[17]) gp.npc[85][7] = null;
		if (!flags[19] && gp.player.p.grustCount >= 10) {
			gp.npc[107][0] = NPCSetup(RICK, 46, 60, 234);
			index--;
		} else {
			gp.npc[107][0] = null;
		}
		if (flags[19]) {
			gp.npc[33][0] = null;
			gp.npc[36][11] = null;
		}
		
		if (!flags[19]) {
			int gIndex = 1;
			for (int i = 224; i <= 233; i++) {
				if (gp.player.p.trainersBeat[i]) {
					gp.npc[107][gIndex] = null;
				}
				gIndex++;
			}
		}
		if (flags[20]) {
			for (int i = 0; i < 14; i++) {
				gp.npc[104][i] = null;
			}
		}
		if (flags[21]) gp.npc[109][3] = null;
		
		if (map == 16 && gp.player.p.choiceChoice != null) gp.obj[map][objIndex] = ObjSetup(46, 28, gp.player.p.choiceChoice, map);
		if (flags[26]) {
			gp.npc[138][0] = null;
			gp.npc[124][15] = null;
		}
		if (flags[28]) {
			gp.npc[146][1] = null;
			for (int i = 1; i < gp.npc[1].length; i++) {
				gp.npc[149][i] = null;
			}
		}
		
		if (map == 107 && flags[28]) {
			gp.tileM.openGhostlyBluePortals();
		}
	}

	private Entity NPCSetup(int type, int x, int y, int team) {
		return NPCSetup(type, x, y, team, true);
	}
	
	private Entity NPCSetup(int type, int x, int y, int team, boolean increase) {
		return NPCSetup(type, x, y, team, increase, new Item[] {});
	}

	private Entity NPCSetup(int type, int x, int y, int team, boolean increase, Item... items) {
		Entity result = null;
		switch (type) {
		case NPC_PC:
			result = new NPC_PC(gp);
			break;
		case NPC_NURSE:
			result = new NPC_Nurse(gp, "down");
			break;
		case NPC_CLERK:
			result = new NPC_Clerk(gp);
			result.setItems(gp.player.getItems());
			clerks.add(result);
			break;
		case TRAINER_DOWN:
			result = new NPC_Trainer(gp, "down", team);
			break;
		case TRAINER_UP:
			result = new NPC_Trainer(gp, "up", team);
			break;
		case TRAINER_LEFT:
			result = new NPC_Trainer(gp, "left", team);
			break;
		case TRAINER_RIGHT:
			result = new NPC_Trainer(gp, "right", team);
			break;
		case NPC_NURSE_FULL:
			result = new NPC_Nurse(gp, "up");
			break;
		case ROBIN:
			result = new NPC_GymLeader(gp, "down", team);
			break;
		case STANFORD:
			result = new NPC_GymLeader(gp, "down2", team);
			break;
		case MILLIE:
			result = new NPC_GymLeader(gp, "up", team);
			break;
		case GLACIUS:
			result = new NPC_GymLeader(gp, "up2", team);
			break;
		case MINDY:
			result = new NPC_GymLeader(gp, "left", team);
			break;
		case RAYNA:
			result = new NPC_GymLeader(gp, "left2", team);
			break;
		case MERLIN:
			result = new NPC_GymLeader(gp, "right", team);
			break;
		case NOVA:
			result = new NPC_GymLeader(gp, "right2", team);
			break;
		case SCOTT_DOWN:
			result = new NPC_Rival(gp, "down", team); // scott down
			break;
		case SCOTT_UP:
			result = new NPC_Rival(gp, "up", team); // scott up
			break;
		case TN_DOWN:
			result = new NPC_TN(gp, "down", team);
			break;
		case TN_UP:
			result = new NPC_TN(gp, "up", team);
			break;
		case TN_LEFT:
			result = new NPC_TN(gp, "left", team);
			break;
		case TN_RIGHT:
			result = new NPC_TN(gp, "right", team);
			break;
		case FRED_DOWN:
			result = new NPC_Rival2(gp, "down", team); // fred down
			break;
		case FRED_UP:
			result = new NPC_Rival2(gp, "up", team); // fred up
			break;
		case NPC_MARKET:
			result = new NPC_Market(gp);
			result.setItems(items);
			break;
		case GRUST:
			result = new NPC_Pokemon(gp, 159, team, true, -1);
			gp.grusts[index - 1] = ((NPC_Pokemon) result);
			break;
		case RICK:
			result = new NPC_TN_Admin(gp, "down", team);
			break;
		case MAXWELL:
			result = new NPC_TN_Admin(gp, "up", team);
			break;
		case INVIS_DOWN:
			result = new NPC_Invisible(gp, "down", team);
			break;
		case INVIS_UP:
			result = new NPC_Invisible(gp, "up", team);
			break;
		case INVIS_LEFT:
			result = new NPC_Invisible(gp, "left", team);
			break;
		case INVIS_RIGHT:
			result = new NPC_Invisible(gp, "right", team);
			break;
		case NPC_PC_GAUNTLET:
			result = new NPC_PC(gp, true);
			break;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		if (increase) index++;
		
		return result;
	}
	
	private Entity NPCSetup(int type, int x, int y, String message) {
		return NPCSetup(type, x, y, message, -1);
	}
	
	private Entity NPCSetup(int type, int x, int y, String message, int flag) {
		return NPCSetup(type, x, y, message, flag, false, null);
	}
	
	private Entity NPCSetup(int type, int x, int y, String message, boolean a) {
		return NPCSetup(type, x, y, message, -1, a, null);
	}
	
	private Entity NPCSetup(int type, int x, int y, String message, int flag, boolean a, String altDialogue) {
		String messages[] = message.split("\n");
		for (int i = 0; i < messages.length; i++) {
			messages[i] = Item.breakString(messages[i], 42);
		}
		Entity result = new NPC_Block(gp, messages, flag, a, Item.breakString(altDialogue, 44));
		
		BufferedImage image;
		switch (type) {
			case BLOCK_DEFAULT:
			default:
				image = result.setup("/npc/block");
				break;
			case ROBIN:
				image = result.setup("/npc/robin");
				break;
			case STANFORD:
				image = result.setup("/npc/stanford");
				break;
			case TN_DOWN:
				image = result.setup("/npc/tn1");
				break;
			case ALAKAZAM:
				image = result.setup("/overworlds/245_0");
				break;
		}
		
		result.down1 = image;
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		index++;
		
		return result;
	}
	
	private ItemObj ObjSetup(int x, int y, Item item, int mapNum, int lower, int upper) {
		if (gp.player.p.itemsCollected[mapNum][objIndex] == true) {
			objIndex++;
			return null;
		}
		
		ItemObj result = new ItemObj(gp);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		result.item = item;
		int amt = lower;
		if (lower != upper) {
			Random random = new Random();
			amt = random.nextInt(upper - lower + 1) + lower;
		}
		result.count = amt;
		
		objIndex++;
		
		return result;
	}
	
	private ItemObj ObjSetup(int x, int y, Item item, int mapNum, int amt) {
		return ObjSetup(x, y, item, mapNum, amt, amt);
	}
	
	private ItemObj ObjSetup(int x, int y, Item item, int mapNum) {
		return ObjSetup(x, y, item, mapNum, 1);
	}
	
	private ItemObj ResistBerrySetup(int x, int y, int mapNum, int lower, int upper) {
		if (gp.player.p.itemsCollected[mapNum][objIndex] == true || gp.player.p.resistBerries == null) {
			objIndex++;
			berryIndex++;
			return null;
		}
		
		return ObjSetup(x, y, gp.player.p.resistBerries[berryIndex++], mapNum, lower, upper);
	}
	
	private InteractiveTile ITileSetup(int x, int y, int type, int mapNum, int map) {
		InteractiveTile result = null;
		if (mapNum != map) return null;
		switch (type) {
		case 0:
			result = new Cut_Tree(gp);
			break;
		case 1:
			result = new Rock_Smash(gp);
			break;
		case 2:
			result = new Tree_Stump(gp);
			break;
		case 3:
			result = new GymBarrier(gp);
			break;
		case 4:
			result = new Vine_Crossable(gp);
			break;
		case 5:
			result = new Vine(gp);
			break;
		case 6:
			result = new Starter_Machine(gp);
			break;
		case 7:
			result = new Fuse_Box(gp);
			break;
		case 8:
			result = new Fuse_Box(gp);
			result.direction = "up";
			break;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param type
	 * 0 = go down, 1 = go left, 2 = go up, 3 = go right
	 * @param amt 
	 * @return
	 */
	private InteractiveTile SetupRockClimb(int x, int y, int type, int amt, int mapNum, int map) {
		if (mapNum != map) return null;
		InteractiveTile result = null;
		result = new Rock_Climb(gp, type, amt);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	private void SetupPit(int mapNum, int x, int y, int mapDest, int xDest, int yDest, int map) {
		if (mapNum != map) return;
		Pit middle = new Pit(gp, mapDest, xDest, yDest);
		middle.setCoords(x, y);
		
		gp.iTile[mapNum][iIndex] = middle;
		iIndex++;
		
		for (int i = 0; i < 4; i++) {
			PitEdge current = new PitEdge(gp, middle, i);
			
			gp.iTile[mapNum][iIndex] = current;
			iIndex++;
		}
		
		
	}
	
	private void SetupWhirlpool(int mapNum, int x, int y, int map) {
		if (mapNum != map) return;
		Whirlpool middle = new Whirlpool(gp, x, y);
		
		gp.iTile[mapNum][iIndex] = middle;
		iIndex++;
		
		int[][] offsets = {
	            {-1, -1}, {0, -1}, {1, -1},
	            {-1, 0},           {1, 0},
	            {-1, 1},  {0, 1},  {1, 1}
	    };
		
		for (int i = 0; i < 8; i++) {
			int offsetX = x + offsets[i][0];
	        int offsetY = y + offsets[i][1];
			
			Whirlpool current;
	        if (i % 2 == 0) {
	            current = new Whirlpool_Side(gp, offsetX, offsetY);
	            current.down1 = current.setup("/interactive/whirlpool" + (i + 1));
	        } else {
	            current = new Whirlpool_Corner(gp, offsetX, offsetY);
	            current.down1 = current.setup("/interactive/whirlpool" + (i + 1));
	        }
			
			gp.iTile[mapNum][iIndex] = current;
			iIndex++;
		}
		
	}
	
	private InteractiveTile SetupLockedDoor(int x, int y, int flag, int mapNum, int map) {
		if (mapNum != map) return null;
		InteractiveTile result = null;
		result = new Locked_Door(gp, flag);
		
		if (gp.player.p.flag[result.getFlagX()][result.getFlagY()]) return null;
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	private InteractiveTile SetupBarrier(int x, int y, int flag, int mapNum, int map) {
		if (mapNum != map) return null;
		InteractiveTile result = null;
		result = new GymBarrier(gp, flag);
		
		if (gp.player.p.flag[result.getFlagX()][result.getFlagY()]) return null;
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	private NPC_Pokemon SetupStaticEncounter(int id, int x, int y, int t, int flag) {
		NPC_Pokemon result = new NPC_Pokemon(gp, id, t, id == 159, flag);
		if (gp.player.p.flag[result.getFlagX()][result.getFlagY()]) {
			index++;
			return null;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		index++;
		
		return result;
	}
}