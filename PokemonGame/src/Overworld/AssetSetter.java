package Overworld;

import java.util.Random;

import Entity.Entity;
import Entity.NPC_Block;
import Entity.NPC_Clerk;
import Entity.NPC_GymLeader;
import Entity.NPC_Invisible;
import Entity.NPC_Market;
import Entity.NPC_Nurse;
import Entity.NPC_PC;
import Entity.NPC_Pokemon;
import Entity.NPC_Rival;
import Entity.NPC_Rival2;
import Entity.NPC_TN;
import Entity.NPC_TN_Admin;
import Entity.NPC_Trainer;
import Obj.Cut_Tree;
import Obj.GymBarrier;
import Obj.InteractiveTile;
import Obj.ItemObj;
import Obj.Pit;
import Obj.PitEdge;
import Obj.Rock_Climb;
import Obj.Rock_Smash;
import Obj.Tree_Stump;
import Obj.Vine;
import Obj.Vine_Crossable;
import Obj.Whirlpool;
import Obj.Whirlpool_Corner;
import Obj.Whirlpool_Side;
import Swing.Item;

public class AssetSetter {

	GamePanel gp;
	int index;
	int objIndex;
	private int iIndex;
	private int berryIndex;
	
	private static final int NPC_POKEMON = -5;
	private static final int NPC_MARKET = -4;
	private static final int NPC_NURSE_FULL = -3;
	private static final int NPC_CLERK = -2;
	private static final int NPC_PC = -1;
	private static final int NPC_NURSE = 0;
	private static final int GYM_1 = 1;
	private static final int GYM_2 = 2;
	private static final int GYM_3 = 3;
	private static final int GYM_4 = 4;
	private static final int GYM_5 = 5;
	private static final int GYM_6 = 6;
	private static final int GYM_7 = 7;
	private static final int GYM_8 = 8;
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
		gp.obj[mapNum][objIndex] = ObjSetup(27, 42, Item.BLACK_GLASSES, mapNum);
		
		mapNum = 8;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(27, 45, Item.EXPERT_BELT, mapNum);
		
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
		
		mapNum = 14;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(22, 48, Item.TM57, mapNum); // charge beam
		gp.obj[mapNum][objIndex] = ObjSetup(42, 35, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(36, 30, Item.BRAVE_MINT, mapNum); // brave
		gp.obj[mapNum][objIndex] = ObjSetup(39, 32, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(23, 47, Item.MAGNET, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 31, Item.WISE_GLASSES, mapNum);
		
		mapNum = 15;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(33, 39, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 15, Item.PARALYZE_HEAL, mapNum);

		mapNum = 16;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(44, 33, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 37, Item.ULTRA_BALL, mapNum);
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
		gp.obj[mapNum][objIndex] = ObjSetup(53, 32, Item.DAWN_STONE, mapNum);
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
		gp.obj[mapNum][objIndex] = ObjSetup(91, 57, Item.EVIOLITE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 41, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 41, Item.CLEAR_AMULET, mapNum);
		
		mapNum = 36;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(12, 52, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(21, 32, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(49, 56, Item.MODEST_MINT, mapNum); // modest
		gp.obj[mapNum][objIndex] = ObjSetup(61, 28, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 45, Item.EUPHORIAN_GEM, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(58, 51, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 52, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(68, 54, Item.PARALYZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 52, Item.REVIVE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 20, Item.TM86, mapNum); // x-scissor
		gp.obj[mapNum][objIndex] = ObjSetup(34, 47, Item.AIR_BALLOON, mapNum);
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
		
		mapNum = 51;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.SOOTHE_BELL, mapNum);
		
		mapNum = 56;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.EVERSTONE, mapNum);
		
		mapNum = 57;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.FLAME_ORB, mapNum);
		
		mapNum = 59;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.TOXIC_ORB, mapNum);
		
		mapNum = 60;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 41, Item.LEFTOVERS, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(24, 36, Item.DRAGON_FANG, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 44, Item.TM73, mapNum); // gyro ball
		gp.obj[mapNum][objIndex] = ObjSetup(32, 44, Item.MUSCLE_BAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(24, 44, Item.TM02, mapNum); // dragon claw
		
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
		gp.obj[mapNum][objIndex] = ObjSetup(12, 87, Item.LUM_BERRY, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(21, 87, mapNum, 5, 15);
		
		mapNum = 121;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(31, 39, Item.BLACK_SLUDGE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(31, 36, Item.TM36, mapNum); // sludge bomb
		gp.obj[mapNum][objIndex] = ObjSetup(26, 35, Item.CHOICE_BAND, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 35, Item.CHOICE_SPECS, mapNum);
	}

	public void setNPC() {
		if (gp.player.p.flags.length < GamePanel.MAX_FLAGS) {
			gp.player.p.updateFlags();
		}
		if (gp.player.p.trainersBeat.length < Main.trainers.length) {
			gp.player.p.updateTrainers();
		}
		boolean[] flags = gp.player.p.flags;
		int mapNum = 0;
		
		if (flags[0] && !flags[1]) {
			gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 72, 48, 0);
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(SCOTT_UP, 72, 48, 0), mapNum);
		}
		
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
		
		if (!flags[15]) {
			gp.npc[mapNum][index] = NPCSetup(85, 5, "The road is closed from this direction, there's a MASSIVE sinkhole on the other side of this gate. If you come  from Schrice City straight North of here it should be clear.");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 60, 37, 259);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 56, 40, 260);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 55, 37, 261);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 63, 40, 262);
		
		if (!flags[2]) {
			gp.npc[mapNum][index] = NPCSetup(60, 44, "The Pokemon here are going FERAL over these berry trees. Come back later!");
		} else {
			gp.npc[mapNum][index++] = null;
		}
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
		if (!flags[1]) {
			gp.npc[mapNum][index] = NPCSetup(31, 40, "I saw a boy named Scott near New Minnow town saying he was looking for a young man that looked like you. Maybe you should check it out?");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 4;
		if (!flags[2]) {
			gp.npc[mapNum][index] = NPCSetup(81, 61, "The gym is currently closed because the Leader is trying to help the Warehouse owner get rid of Team Nuke. Come back later.");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
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
//		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 15, 70, 23);
//		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 15, 70, 23);
		
		mapNum = 7;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 30, 42, 5);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 33, 42, 6);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 36, 39, 7);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 36, 42, 8);
		
		mapNum = 8;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 30, 39, 9);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 28, 41, 10);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 32, 39, 11);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 32, 45, 12);
		gp.npc[mapNum][index] = NPCSetup(RICK, 35, 41, 13);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 30, 44, 258);
		
		mapNum = 9;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 42, 14);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 27, 39, 15);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 42, 34, 16);
		gp.npc[mapNum][index] = NPCSetup(GYM_1, 38, 28, 17);
		
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
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 34, 77, 33);
		
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE_FULL, 19, 49, -1);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 43, 35);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 15, 37, 36);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 19, 37, 37);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 21, 33, 38);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 21, 39);
		
		mapNum = 13;
		index = 0;
		if (!flags[4]) {
			gp.npc[mapNum][index] = NPCSetup(15, 56, "The gym leader is stuck in the office upstairs trying to help Scott with Team Nuke. Go find Scott and help!");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 29, 49, 104);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 49, 105);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 29, 34, 71);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 19, 37, 72);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 35, 24, 73);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 18, 74);
		
		mapNum = 14;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 41, 44);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 26, 29, 40);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 39, 39, 41);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 43, 42);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 26, 48, 43);
		
		mapNum = 16;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 29, 45);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 26, 29, 46);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 46, 29, 47);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 21, 39, 48);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 62, 39, 273);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 67, 42, 274);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 72, 40, 275);
		
		mapNum = 17;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 45, 49);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 45, 50);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 44, 51);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 44, 52);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 50, 43, 53);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 56, 43, 54);
		
		if (!flags[3]) {
			gp.npc[mapNum][index] = NPCSetup(49, 53, "Quick! Team Nuke is taking over our office! Please help!");
			gp.npc[mapNum][index] = NPCSetup(57, 53, "Quick! Team Nuke is taking over our office! Please help!");
		} else {
			gp.npc[mapNum][index++] = null;
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 18;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 48, 45, 55);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 45, 38, 146);
		gp.npc[mapNum][index] = NPCSetup(46, 33, "That was an impressive battle! I found a rare MAGIC Pokemon, but after watching that, you'd be a better trainer. Here!", true);
		
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
		
		gp.npc[mapNum][index] = NPCSetup(GYM_2, 63, 39, 66);
		
		mapNum = 22;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 80, 8, 67);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 78, 12, 68);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 72, 10, 69);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 69, 14, 70);
		
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
		gp.npc[30][index] = NPCSetup(NPC_MARKET, 31, 41, -1);
		gp.npc[40][index] = NPCSetup(NPC_MARKET, 34, 38, -1);
		gp.npc[45][index] = NPCSetup(NPC_CLERK, 30, 39, -1);
		gp.npc[87][index] = NPCSetup(NPC_CLERK, 27, 39, -1);
		gp.npc[89][index] = NPCSetup(NPC_MARKET, 24, 36, -1);
		gp.npc[112][index] = NPCSetup(NPC_MARKET, 31, 41, -1);
		gp.npc[126][index] = NPCSetup(NPC_MARKET, 31, 41, -1);
		
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
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 55, 35, 84);
		
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
		gp.npc[mapNum][index] = NPCSetup(GYM_3, 51, 61, 94);
		
		mapNum = 32;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 40, "Take my spare fishing rod! Look at water and press 'A' to fish!");
		
		mapNum = 33;
		index = 0;
		if (gp.player.p.badges >= 5 && !flags[19]) {
			gp.npc[mapNum][index] = NPCSetup(62, 17, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders went to Ghostly Woods and is planning something evil.");
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
			gp.npc[mapNum][index] = NPCSetup(15, 46, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders go to Ghostly Woods and is planning something evil.");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 38;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(53, 7, "Do you have an ICE type to show me? Also, say hi to my brother in the RADIO TOWER if you haven't yet!", true);
		
		if (!flags[8] || !flags[9]) {
			gp.npc[mapNum][index] = NPCSetup(62, 41, "What is going on at the school?! Where is everybody?!?!");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 74, 138);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 74, 139);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 85, 140);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 36, 84, 141);
		
		mapNum = 43;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 42, "Do you have an GROUND type to show me? Also, say hi to my brother in the ICY FIELDS if you haven't yet!", true);
		
		mapNum = 41;
		index = 0;
		
		if (!flags[7]) {
			gp.npc[mapNum][index] = NPCSetup(13, 24, "This room is locked! Where could the key be?");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		if (!flags[6]) {
			gp.npc[mapNum][index] = NPCSetup(50, 24, "This room is locked! Where could the key be?");
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
		
		gp.npc[mapNum][index] = NPCSetup(29, 18, "Who are these people?? Have you cleared both rooms yet?", true);
		
		mapNum = 44;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 60, 61, 133);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 66, 57, 134);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 60, 52, 135);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 66, 46, 136);
		gp.npc[mapNum][index] = NPCSetup(GYM_4, 63, 39, 137);
		
		mapNum = 46;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Check your team's Hidden Power types here!", true);
		
		mapNum = 47;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Here, take this as a gift!", true);
		
		mapNum = 48;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 42, 142);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 42, 143);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 31, 36, 144);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 39, 145);
		gp.npc[mapNum][index] = NPCSetup(24, 37, "Here, take this as a gift!", true);
		
		mapNum = 49;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Deep below ELECTRIC TUNNEL there's a secret trail called SHADOW PATH.", true);
		
		mapNum = 50;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "WOAOAOHAOAHOAHOH!!! Hehehehehe I just popped a naughty yerkocet!! Pick one of these NUTTY \"starters\" teheheheheheee", true);
		
		mapNum = 53;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_MARKET, 31, 41, -1);
		
		mapNum = 60;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 42, 147);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 42, 148);
		
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
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 32, 45, 158);
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
			gp.npc[mapNum][index] = NPCSetup(58, 64, "Talk to Grandpa (bottom house all the way southwest)");
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
			gp.npc[mapNum][index] = NPCSetup(63, 67, "EEEK! DON'T GO THIS WAY! I saw some really scary men dressed up in black go towards the woods this way. BE CAREFUL!");
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
		gp.npc[mapNum][index] = NPCSetup(GYM_5, 63, 35, 194);
		
		mapNum = 90;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 48, 10, 174);
		
		mapNum = 91;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 29, 45, 183);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 33, 45, 184);
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Thank you so much for saving me! Here, take this as a gift!", true);
		
		mapNum = 92;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE, 31, 37, -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 35, 36, -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_MARKET, 22, 37, -1);
		
		mapNum = 93;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Would you like to remember a move? Which Pokemon should remember?", true);
		
		mapNum = 94;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "There have been 2 meteorites that have crashed into our region. One makes Pokemon surge with electricity, and another casts them in a strange shadow.", true);
		
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
				if (!gp.player.p.trainersBeat[i]) {
					gp.npc[mapNum][index] = NPCSetup(NPC_POKEMON, xCoords[i-224], yCoords[i-224], i);
				} else {
					gp.npc[mapNum][index++] = null;
					GamePanel.volatileTrainers.put(NPCSetup(NPC_POKEMON, xCoords[i-224], yCoords[i-224], i), mapNum);
				}
			}
		}
		
		mapNum = 109;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 76, 89, 235);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 72, 236);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 49, 44, 237);
		
		if (!flags[21]) {
			gp.npc[mapNum][index] = NPCSetup(SCOTT_DOWN, 21, 40, 242);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		gp.npc[mapNum][index] = NPCSetup(44, 39, "I found this infant Pokemon abandoned here.", true);
		
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
		gp.npc[mapNum][index] = NPCSetup(GYM_6, 53, 30, 255);
		
		mapNum = 115;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 69, 243);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 62, 74, 244);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 41, 72, 245);
		
		mapNum = 118;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(31, 41, "Hi! I can revive fossils for you!", true);
		
		mapNum = 119;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 32, 79, 264);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 37, 76, 265);
		
		if (!flags[1]) {
			gp.npc[mapNum][index] = NPCSetup(23, 76, "You're not ready to fight those guys over there yet, sorry bud.");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 121;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 42, 270);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 37, 271);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 35, 272);
		gp.npc[mapNum][index] = NPCSetup(31, 34, "You're definitely not strong enough to go up here. Also, the items on the floor are really powerful, so you can only choose one for now.");
		
		mapNum = 122;
		index = 0;
		
		mapNum = 127;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(27, 39, "Welcome to the Blackjack table!", true);
		gp.npc[mapNum][index] = NPCSetup(35, 39, "This game isn't ready yet!", true);
		
		mapNum = 128;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 51, 66, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 55, 66, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 50, 65, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 56, 65, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 47, 64, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 59, 64, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 50, 62, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 56, 62, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 50, 61, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 56, 61, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 51, 59, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 55, 58, 271);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 51, 56, 270);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 55, 56, 271);
		gp.npc[mapNum][index] = NPCSetup(GYM_7, 53, 56, 270);
		
	}
	
	public void setInteractiveTile(int map) {
		int mapNum = 0;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 53, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(82, 59, 0, mapNum, map);
		
		mapNum = 4;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(17, 63, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(16, 68, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(10, 68, 0, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(74, 43, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(75, 24, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(85, 72, 0, mapNum, map);
		
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
		
		mapNum = 16;
		iIndex = 0;
		SetupWhirlpool(mapNum, 65, 35, map);
		
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
		
		mapNum = 25;
		gp.iTile[mapNum][iIndex] = ITileSetup(73, 73, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(65, 85, 0, 1, mapNum, map);
		
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
		
		mapNum = 60;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(30, 44, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 43, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(32, 43, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(31, 42, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 70, 1, mapNum, map);
		
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
		gp.iTile[mapNum][iIndex] = SetupRockClimb(46, 42, 2, 1, mapNum, map);
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
		
	}

	public void updateNPC(int map) {
		boolean[] flags = gp.player.p.flags;
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
		if (!flags[0] || flags[1]) gp.npc[0][0] = null;
		if (flags[0] && !flags[1]) gp.npc[0][0] = NPCSetup(SCOTT_UP, 72, 48, 0);
		if (flags[1]) {
			gp.npc[3][0] = null;
			gp.npc[119][2] = null;
		}
		if (flags[2]) {
			gp.npc[4][1] = null;
			gp.npc[0][17] = null;
		}
		if (flags[3]) {
			gp.npc[17][6] = null;
			gp.npc[17][7] = null;
		}
		if (flags[4]) gp.npc[13][0] = null;
		if (flags[5]) gp.npc[28][9] = null;
		
		if (flags[7]) gp.npc[41][0] = null;
		if (flags[6]) gp.npc[41][1] = null;
		if (flags[8] && flags[9]) gp.npc[38][1] = null;
		if (flags[15]) gp.npc[0][12] = null;
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
	}
	
	private Entity NPCSetup(int type, int x, int y, int team) {
		return NPCSetup(type, x, y, team, true);
	}

	private Entity NPCSetup(int type, int x, int y, int team, boolean increase) {
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
		case GYM_1:
			result = new NPC_GymLeader(gp, "down", team);
			break;
		case GYM_2:
			result = new NPC_GymLeader(gp, "down2", team);
			break;
		case GYM_3:
			result = new NPC_GymLeader(gp, "up", team);
			break;
		case GYM_4:
			result = new NPC_GymLeader(gp, "up2", team);
			break;
		case GYM_5:
			result = new NPC_GymLeader(gp, "left", team);
			break;
		case GYM_6:
			result = new NPC_GymLeader(gp, "left2", team);
			break;
		case GYM_7:
			result = new NPC_GymLeader(gp, "right", team);
			break;
		case GYM_8:
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
			break;
		case NPC_POKEMON:
			result = new NPC_Pokemon(gp, 159, team, true); // Grust
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
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		if (increase) index++;
		
		return result;
	}
	
	private Entity NPCSetup(int x, int y, String message) {
		Entity result = new NPC_Block(gp, message, false);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		result.trainer = -1;
		
		index++;
		
		return result;
	}
	
	private Entity NPCSetup(int x, int y, String message, boolean a) {
		Entity result = new NPC_Block(gp, message, a);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		index++;
		
		return result;
	}
	
	private ItemObj ObjSetup(int x, int y, Item item, int mapNum, int lower, int upper) {
		if (gp.player.p.itemsCollected.length != gp.obj.length || gp.player.p.itemsCollected[1].length != gp.obj[1].length) {
			gp.player.p.updateItems(gp.obj.length, gp.obj[1].length);
		}
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
	            current.down1 = current.setup("/npc/whirlpool" + (i + 1));
	        } else {
	            current = new Whirlpool_Corner(gp, offsetX, offsetY);
	            current.down1 = current.setup("/npc/whirlpool" + (i + 1));
	        }
			
			gp.iTile[mapNum][iIndex] = current;
			iIndex++;
		}
		
	}
}