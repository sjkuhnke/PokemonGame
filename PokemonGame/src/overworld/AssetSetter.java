package overworld;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import entity.*;
import object.*;
import pokemon.Item;
import pokemon.Trainer;

public class AssetSetter {

	GamePanel gp;
	int index;
	int objIndex;
	private int iIndex;
	private int berryIndex;
	private int sBerryIndex;
	
	public ArrayList<Entity> clerks = new ArrayList<>();
	public HashMap<Item, Integer> itemMap = new HashMap<>();
	
	private static final int MILLIE_UP = -27;
	private static final int ROBIN_UP = -26;
	private static final int CHEF = -25;
//	private static final int UP_PHEROMOSA = -24;
//	private static final int UP_BUZZWOLE = -23;
//	private static final int UP_SPLAME = -22;
//	private static final int UP_SHOOKWAT = -21;
//	private static final int UP_CAIRNASAUR = -20;
	private static final int UP_XURKITREE = -19;
	private static final int RYDER_UP = -18;
	private static final int RYDER_DOWN = -17;
	private static final int ALAKAZAM = -16;
	private static final int KLARA = -15;
	private static final int AVERY = -14;
	private static final int GRANDMOTHER = -13;
	private static final int RESEARCHER = -12;
	private static final int PROFESSOR = -11;
	private static final int BLOCK_DOWN = -10;
	private static final int BLOCK_UP = -9;
	private static final int BLOCK_LEFT = -8;
	private static final int BLOCK_RIGHT = -7;
	
	private static final int NPC_PC_GAUNTLET = -6;

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
	
	private static final int ACTOR_DOWN = 100;
	private static final int ACTOR_UP = 101;
	private static final int ACTOR_LEFT = 102;
	private static final int ACTOR_RIGHT = 103;
	private static final int ACTRESS_DOWN = 104;
	private static final int ACTRESS_UP = 105;
	private static final int ACTRESS_LEFT = 106;
	private static final int ACTRESS_RIGHT = 107;
	private static final int ATHLETE_DOWN = 108;
	private static final int ATHLETE_UP = 109;
	private static final int ATHLETE_LEFT = 110;
	private static final int ATHLETE_RIGHT = 111;
	private static final int BIRD_KEEPER_DOWN = 112;
	private static final int BIRD_KEEPER_UP = 113;
	private static final int BIRD_KEEPER_LEFT = 114;
	private static final int BIRD_KEEPER_RIGHT = 115;
	private static final int BLACK_BELT_DOWN = 116;
	private static final int BLACK_BELT_UP = 117;
	private static final int BLACK_BELT_LEFT = 118;
	private static final int BLACK_BELT_RIGHT = 119;
	private static final int BUG_CATCHER_DOWN = 120;
	private static final int BUG_CATCHER_UP = 121;
	private static final int BUG_CATCHER_LEFT = 122;
	private static final int BUG_CATCHER_RIGHT = 123;
	private static final int BURGLAR_DOWN = 124;
	private static final int BURGLAR_UP = 125;
	private static final int BURGLAR_LEFT = 126;
	private static final int BURGLAR_RIGHT = 127;
	private static final int EXPLORER_DOWN = 128;
	private static final int EXPLORER_UP = 129;
	private static final int EXPLORER_LEFT = 130;
	private static final int EXPLORER_RIGHT = 131;
	private static final int FISHERMAN_DOWN = 132;
	private static final int FISHERMAN_UP = 133;
	private static final int FISHERMAN_LEFT = 134;
	private static final int FISHERMAN_RIGHT = 135;
	private static final int GENTLEMAN_DOWN = 136;
	private static final int GENTLEMAN_UP = 137;
	private static final int GENTLEMAN_LEFT = 138;
	private static final int GENTLEMAN_RIGHT = 139;
	private static final int HIKER_DOWN = 140;
	private static final int HIKER_UP = 141;
	private static final int HIKER_LEFT = 142;
	private static final int HIKER_RIGHT = 143;
	private static final int LADY_DOWN = 144;
	private static final int LADY_UP = 145;
	private static final int LADY_LEFT = 146;
	private static final int LADY_RIGHT = 147;
	private static final int MAGICIAN_F_DOWN = 148;
	private static final int MAGICIAN_F_UP = 149;
	private static final int MAGICIAN_F_LEFT = 150;
	private static final int MAGICIAN_F_RIGHT = 151;
	private static final int MAGICIAN_M_DOWN = 152;
	private static final int MAGICIAN_M_UP = 153;
	private static final int MAGICIAN_M_LEFT = 154;
	private static final int MAGICIAN_M_RIGHT = 155;
	private static final int MANIAC_DOWN = 156;
	private static final int MANIAC_UP = 157;
	private static final int MANIAC_LEFT = 158;
	private static final int MANIAC_RIGHT = 159;
	private static final int PICKNICKER_DOWN = 160;
	private static final int PICKNICKER_UP = 161;
	private static final int PICKNICKER_LEFT = 162;
	private static final int PICKNICKER_RIGHT = 163;
	private static final int POSTMAN_DOWN = 164;
	private static final int POSTMAN_UP = 165;
	private static final int POSTMAN_LEFT = 166;
	private static final int POSTMAN_RIGHT = 167;
	private static final int PSYCHIC_DOWN = 168;
	private static final int PSYCHIC_UP = 169;
	private static final int PSYCHIC_LEFT = 170;
	private static final int PSYCHIC_RIGHT = 171;
	private static final int STUDENT_F_DOWN = 172;
	private static final int STUDENT_F_UP = 173;
	private static final int STUDENT_F_LEFT = 174;
	private static final int STUDENT_F_RIGHT = 175;
	private static final int STUDENT_M_DOWN = 176;
	private static final int STUDENT_M_UP = 177;
	private static final int STUDENT_M_LEFT = 178;
	private static final int STUDENT_M_RIGHT = 179;
	private static final int SWIMMER_F_DOWN = 180;
	private static final int SWIMMER_F_UP = 181;
	private static final int SWIMMER_F_LEFT = 182;
	private static final int SWIMMER_F_RIGHT = 183;
	private static final int SWIMMER_M_DOWN = 184;
	private static final int SWIMMER_M_UP = 185;
	private static final int SWIMMER_M_LEFT = 186;
	private static final int SWIMMER_M_RIGHT = 187;
	private static final int YOUNGSTER_DOWN = 188;
	private static final int YOUNGSTER_UP = 189;
	private static final int YOUNGSTER_LEFT = 190;
	private static final int YOUNGSTER_RIGHT = 191;
	private static final int ACE_TRAINER_F_DOWN = 192;
	private static final int ACE_TRAINER_F_UP = 193;
	private static final int ACE_TRAINER_F_LEFT = 194;
	private static final int ACE_TRAINER_F_RIGHT = 195;
	private static final int ACE_TRAINER_M_DOWN = 196;
	private static final int ACE_TRAINER_M_UP = 197;
	private static final int ACE_TRAINER_M_LEFT = 198;
	private static final int ACE_TRAINER_M_RIGHT = 199;
	private static final int ASTRONOMER_DOWN = 200;
	private static final int ASTRONOMER_UP = 201;
	private static final int ASTRONOMER_LEFT = 202;
	private static final int ASTRONOMER_RIGHT = 203;
//	private static final int T = 204;
//	private static final int T = 205;
//	private static final int T = 206;
//	private static final int T = 207;
//	private static final int T = 208;
//	private static final int T = 209;
	
	private static final int DOWN = 1;
	private static final int UP = 2;
	private static final int LEFT = 4;
	private static final int RIGHT = 8;
	private static final int ALL = DOWN + UP + LEFT + RIGHT;
	
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
		gp.obj[mapNum][objIndex] = ObjSetup(23, 70, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(33, 64, Item.POTION, mapNum);
		
		gp.obj[mapNum][objIndex] = StatBerrySetup(41, 42, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = StatBerrySetup(51, 27, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = StatBerrySetup(52, 11, mapNum, 5, 10);
		
		gp.obj[mapNum][objIndex] = ObjSetup(34, 13, Item.POKEBALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(64, 9, Item.ORAN_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 38, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 19, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 31, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(72, 14, Item.POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 40, Item.PECHA_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 11, Item.ORAN_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(49, 32, Item.ORAN_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 20, Item.CHERI_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(41, 24, Item.CHESTO_BERRY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(70, 22, Item.WATER_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(71, 9, Item.RUSTY_BOTTLE_CAP, mapNum);
		
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
		gp.obj[mapNum][objIndex] = ObjSetup(76, 27, Item.DAWN_STONE, mapNum);
		
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
		//gp.obj[mapNum][objIndex] = ObjSetup(31, 8, Item.TM72, mapNum); // ice spinner (v.cross)
		
		gp.obj[mapNum][objIndex] = ObjSetup(10, 72, Item.POISON_BARB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(26, 8, Item.WIDE_LENS, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 30, Item.GANLON_BERRY, mapNum, 3, 6);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 47, Item.APICOT_BERRY, mapNum, 3, 6);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 73, Item.FOCUS_SASH, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(28, 73, Item.RED_CARD, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 34, Item.MENTAL_HERB, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(79, 25, Item.TM73, mapNum); // gyro ball
		
		gp.obj[mapNum][objIndex] = ObjSetup(71, 28, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(58, 28, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 8, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(80, 8, Item.FULL_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = StatBerrySetup(70, 35, mapNum, 5, 10);
		
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
		gp.obj[mapNum][objIndex] = ObjSetup(45, 45, Item.TM61, mapNum); // smack down
		gp.obj[mapNum][objIndex] = ObjSetup(53, 36, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(59, 41, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(43, 53, Item.RUSTY_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(62, 56, Item.REPEL, mapNum);
		
		mapNum = 18;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(49, 33, Item.ELIXIR, mapNum);
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
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(62, 69, Item.REPEL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 69, Item.IMPISH_MINT, mapNum); // impish
		
		mapNum = 27;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(65, 68, Item.ENCHANTED_AMULET, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(55, 70, Item.RUSTY_BOTTLE_CAP, mapNum);
		
		mapNum = 28;
		gp.obj[mapNum][objIndex] = ResistBerrySetup(18, 46, mapNum, 5, 15);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 43, Item.ANTIDOTE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 46, Item.TM83, mapNum); // captivate
		gp.obj[mapNum][objIndex] = ObjSetup(52, 43, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(78, 13, Item.TM89, mapNum); // acrobatics
		gp.obj[mapNum][objIndex] = ObjSetup(43, 37, Item.GOLD_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(85, 19, Item.THROAT_SPRAY, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 37, Item.DAWN_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(95, 27, Item.GREAT_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(75, 29, Item.RUSTY_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(70, 23, Item.HYPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(28, 29, Item.PP_UP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(47, 48, Item.BIG_ROOT, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(66, 24, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = StatBerrySetup(42, 26, mapNum, 5, 10);
		gp.obj[mapNum][objIndex] = StatBerrySetup(79, 19, mapNum, 5, 10);
		
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
		gp.obj[mapNum][objIndex] = ObjSetup(67, 30, Item.NEVER$MELT_ICE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 30, Item.FREEZE_HEAL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 75, Item.ABILITY_CAPSULE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 77, Item.TM48, mapNum); // scald
		gp.obj[mapNum][objIndex] = ObjSetup(54, 78, Item.ADAMANT_MINT, mapNum); // adamant
		gp.obj[mapNum][objIndex] = ObjSetup(54, 79, Item.MAX_ELIXIR, mapNum);
		gp.obj[mapNum][objIndex] = ResistBerrySetup(48, 67, mapNum, 5, 15);
		
		mapNum = 41;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(42, 46, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 43, Item.TM62, mapNum); // bug buzz
		gp.obj[mapNum][objIndex] = ObjSetup(8, 45, Item.ULTRA_BALL, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 18, Item.LOADED_DICE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(51, 46, Item.SUPER_POTION, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(56, 37, Item.TERRAIN_EXTENDER, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(54, 17, Item.SUPER_POTION, mapNum);
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
		gp.obj[mapNum][objIndex] = ObjSetup(11, 15, Item.RUSTY_BOTTLE_CAP, mapNum);
		
		mapNum = 146;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(67, 48, Item.EVIOLITE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(89, 38, Item.TM47, mapNum); // star storm
		
		mapNum = 163;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(28, 39, Item.RUSTY_BOTTLE_CAP, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 39, Item.ICE_PICK, mapNum);
		
		mapNum = 164;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(32, 38, Item.TERRAIN_EXTENDER, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(39, 38, Item.SHOVEL, mapNum);
		
		mapNum = 165;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(29, 41, Item.RAZOR_CLAW, mapNum);
		
		mapNum = 166;
		gp.obj[mapNum][objIndex] = ObjSetup(41, 45, Item.ICE_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(38, 25, Item.ICE_STONE, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(49, 44, Item.TM72, mapNum); // ice spinner (v.cross)
	}

	public void setNPC() {
		boolean[] flags = gp.player.p.flags;
		int mapNum = 0;
		
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 72, 48, "Hey! You must be the Professor's son. I've been waiting to meet you!\n"
				+ "My dad told me about you - he's Avery from Galar, so I guess that makes me... uh, someone important, too!\n"
				+ "I've heard you're already training, but let's see how we match up!\n"
				+ "Uh, no pressure though, right? Oh man, I'm already getting a headache. Let's just get this over with before I stress myself out!",
				"Ugh, I lost! But I'm not going to let it get me down... not much, anyway.\n"
				+ "You're strong, but I knew you would be. Dad always says it's okay to lose as long as you learn, right?\n"
				+ "Anyway, I'll get better. You just wait! Next time... I'm definitely beating you. Probably. Hopefully.", 0);
		
		gp.npc[mapNum][index] = NPCSetup(STUDENT_M_LEFT, 18, 18, "Studying for battles? Pfft, I don't need to. Let's go!", "Okay, so maybe I do need to study more...", 1);
		gp.npc[mapNum][index] = NPCSetup(BIRD_KEEPER_RIGHT, 23, 19, "Wings beat strength every time. Let me show you how.", "I'll fly higher next time. Count on it!", 2);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_RIGHT, 23, 27, "A lovely day for a battle, isn't it? Let's make it interesting!", "Well, that didn't go as planned. At least I've still got snacks!", 3);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_LEFT, 21, 31, "Bugs may be small, but they've got big power. Watch out!", "I guess I'll need to find some stronger bugs...", 4);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_RIGHT, 21, 14, "I'm young, but I'm ready to win! Let's do this!", "No fair! You got lucky!", 102);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 7, "", "", 175);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 8, "", "", 176);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 82, 11, "", "", 177);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 11, "", "", 178);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 16, "", "", 179);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_UP, 63, 14, "Go away! I'm not giving you this item back. It's mine!", "Fine, now make like a tree and LEAVE!", 329);
		
		if (!flags[15]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 85, 5, "The road is closed from this direction, there's a MASSIVE sinkhole on the other side of this gate.\nIf you come from Schrice City straight North of here it should be clear.", 15);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_DOWN, 60, 38, "You're gonna lose to me, just like everyone else!", "I guess I still have some growing to do...", 259);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_UP, 56, 40, "You think I'm just here to relax? I'll prove you wrong!", "I lost, but at least I've got a great view!", 260);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_RIGHT, 55, 37, "You better be quick if you wanna keep up with me!", "Alright, you've got game. I respect that.", 261);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_UP, 63, 40, "Let's enjoy the outdoors... before I take you down!", "I guess I'll stick to picnics... for now.", 262);
		
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 60, 44, "The Pokemon here are going feral over these berry trees. Come back later!", 15);
		
		gp.npc[mapNum][index] = NPCSetup(ACTRESS_UP, 68, 63, "Woah, don't interrupt my improv sesh back here!", "Maybe my Bluebunn isn't as good of an actress as me.", 266);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_LEFT, 34, 35, "I'm always in top shape. Can you say the same?", "Not bad... but this athlete never stays down for long.", 390);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_DOWN, 42, 30, "Hope you're not afraid of the cold - this battle's about to freeze you out!", "You're tougher than the frostbite I've faced... I respect that.", 389);
		
		index = 0;
		
		// Nurses/PCs
		gp.npc[1][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[1][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[5][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[5][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[19][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[19][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		
		// Clerks
		gp.npc[2][index] = NPCSetup(NPC_CLERK, 27, 39, "", "", -1);
		gp.npc[6][index] = NPCSetup(NPC_CLERK, 27, 39, "", "", -1);
		gp.npc[20][index] = NPCSetup(NPC_CLERK, 27, 39, "", "", -1);
		
		mapNum = 3;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 34, "Sorry, we are blocking the road because this gentleman here demands we look for a young trainer headed this way. You might want to ask him about it.");
		gp.npc[mapNum][index] = NPCSetup(AVERY, "Avery", 35, 39, "Oh! You must be that young trainer Scott mentioned! I'm his quite elegant father Avery!", true); // Avery
		
		mapNum = 4;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 81, 62, "Sorry, but the gym leader has been busy with mail these past few weeks, so gym battles are unavailable. You may check the post office for more details.");
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, "Grunt", 66, 80, "Yo, scram kid! This is some top secret space operation that you don't got the clearance to see!", true);
		
		gp.npc[mapNum][index] = NPCSetup(ACTOR_UP, 32, 62, "Lights, camera, battle! I'll be the star of this scene!", "That wasn't in the script... but I'll take the loss.", 18);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_UP, 23, 65, "Get ready to be swarmed!", "You're tougher than a Caterpie. I'll give you that.", 19);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_UP, 31, 68, "Age doesn't matter - my Pokemon are tough!", "You won this time, but I'm just getting started!", 20);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_UP, 34, 76, "I packed a lunch, but you're about to get served!", "I'll admit it, you've got me beat. Want a sandwich?", 21);
		gp.npc[mapNum][index] = NPCSetup(STUDENT_F_UP, 45, 76, "I always pass my tests, and I won't fail this battle!", "Guess I need to study harder next time...", 22);
		gp.npc[mapNum][index] = NPCSetup(ACE_TRAINER_F_UP, 15, 70, "You're the Professor's kid?\nWell consider this a gift from him!", "Maybe I should return these... they're just rented, after all.", 103);
		gp.npc[mapNum][index] = NPCSetup(ACTRESS_UP, 11, 70, "Watch and learn. I'm about to steal the show!", "No worries, darling, I'll be back for the encore.", 23);
		
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_RIGHT, 72, 37, "I foresaw this battle... and your loss.", "Strange... I didn't foresee this outcome...", 98);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_LEFT, 77, 34, "Don't let my peaceful vibe fool you - I'll fight hard!", "Looks like you're tougher than I thought. Good battle!", 99);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_LEFT, 76, 30, "The cold never bothered me... but it's about to bother you!", "Looks like I've hit a frozen patch... but I'll thaw out soon.", 100);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_RIGHT, 72, 28, "Strength is all that matters. Show me what you've got!", "You've earned my respect. For now.", 101);
		
		gp.npc[mapNum][index] = NPCSetup(LADY_UP, 44, 81, "Don't expect me to go easy on you just because I'm refined.", "Well, I never! I suppose you've earned that victory.", 263);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 44, 68, "In updateNPC", "", 359);
		
		mapNum = 7;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 28, 37, "You shouldn't have meddled in our plans. Time to face the consequences.", "This isn't over. It will send more.", 5);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 28, 41, "Our mission is clear, and you're in our way.", "You've delayed our plans, but we'll return stronger.", 6);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 37, 32, "Prepare to be overwhelmed by our superior tactics.", "Weakness detected. Recalibrating our approach.", 7);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 37, 36, "It commands us, and we obey. Let's battle!", "Your resistance is futile. We will prevail.", 8);
		
		mapNum = 8;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 32, 39, "You're just another obstacle to eliminate. Let's begin.", "You've won today, but the invasion continues.", 9);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 28, 41, "Intruder detected. Engage defensive protocols.", "System compromised, retreating for now.", 10);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 36, 42, "You can't stop what's already in motion.", "Your skills are noteworthy, but inadequate against us.", 11);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 36, 47, "Analyze and counter your strategies. Let's fight.", "We adapt and overcome. Expect our return.", 12);
		gp.npc[mapNum][index] = NPCSetup(RICK, 40, 48, "Ah, so you've finally arrived.\n"
				+ "I've heard about you, causing trouble for our plans left and right. It's amusing, really.\n"
				+ "But now you stand before me, and you're going to regret ever crossing Team Eclipse.\n"
				+ "You may have beaten my underlings, but I am far beyond them. Prepare yourself.",
				"Impressive... very impressive.\n"
				+ "You've managed to best me, but this isn't the end.\n"
				+ "Team Eclipse's plans are much larger than you can comprehend.\n"
				+ "Go ahead, take your victory. But know this - you haven't won anything of consequence. The real challenge is yet to come.", 13);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 28, 49, "Our technology is unmatched. Prepare to lose.", "Mission parameters have changed, but our goal remains.", 258);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Owner", 37, 38, "Oh, thank Arceus! These strange spaced-themed goons took over this warehouse.", true, 15, "Thank you so much for your help! Those guys are up to no good.");
		
		mapNum = 9;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BIRD_KEEPER_LEFT, 34, 42, "The skies are where I dominate. You won't last long up here.", "Grounded... for now. I'll soar again soon.", 14);
		gp.npc[mapNum][index] = NPCSetup(BIRD_KEEPER_RIGHT, 27, 39, "Let's see if you can keep up with my birds!", "Looks like I was blown off course today.", 15);
		gp.npc[mapNum][index] = NPCSetup(BIRD_KEEPER_LEFT, 42, 34, "Up here, you're just a speck in the wind.", "A loss is just turbulence. I'll be back in the air soon.", 16);
		gp.npc[mapNum][index] = NPCSetup(ROBIN, 38, 28, "Hey, it's you! Thanks for helping me deliver those packages last time - I'd still be sorting them out if it weren't for you!\n"
				+ "But now, I won't go easy on you. My Flying Pokemon are as fast as ever, and you're about to see just how high we can soar!\n"
				+ "Don't blink, or you might miss something! Just like when you helped me out, we've gotta be quick and efficient here!\n"
				+ "Oh no... did I forget to deliver Mrs. Foster's letter? I'll worry about it later. Let's battle!",
				"Whew! You're as fast as a gust of wind!\n"
				+ "I thought I had you, but you're even quicker than I expected! That was an awesome battle.\n"
				+ "Oh, and thanks again for the help earlier - I think I'm finally caught up on my deliveries! Probably...", 17);
		
		mapNum = 10;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(ALAKAZAM, "Alakazam", 30, 38, "Vxxxvhh...");
		gp.npc[mapNum][index] = NPCSetup(RYDER_DOWN, "Ryder", 29, 38, "Oh! Uh, mabuhay!", true);
		
		mapNum = 11;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_UP, 69, 53, "It's the perfect day for a picnic... and a battle!", "Well... that put a damper on my picnic plans.", 24);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_UP, 50, 60, "Let's see if your endurance matches your strategy.", "Looks like I need to step up my training.", 25);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_UP, 59, 62, "Don't do the crime if you can't take the time. Lucky I've got plenty of time on my hands!", "You got lucky this time. I'm outta here!", 26);
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_F_UP, 76, 56, "Welcome to the grand performance! Prepare to be dazzled!", "Oh dear, looks like I've lost the trick. I'll rehearse for next time!", 27);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_DOWN, 53, 47, "I caught a rare fish! Wouldn't you like to get destroyed by it!", "Just wait until it evolves. I'll be back.", 28);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_UP, 53, 53, "My brother's a top angler! Look what he caught!", "Guess we need to hit the creek.", 29);
		gp.npc[mapNum][index] = NPCSetup(HIKER_UP, 76, 47, "Bet you're not ready for the weight of this battle.", "Even mountains can be climbed, I guess.", 30);
		
		gp.npc[mapNum][index] = NPCSetup(FRED_UP, 43, 61, "Heh, you must be pretty tough to make it this far, but don't get too full of yourself.\n"
				+ "I'll gladly put an end to your winning streak right here.\n"
				+ "You're just another weak trainer in my way.",
				"Tch, I lost? How did that happen?\n"
				+ "Wait... you know Scott? Ugh, should've figured. Maybe I should've taken you more seriously.\n"
				+ "But I just got unlucky this time. Next time, you won't be so fortunate.", 34);
		
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_LEFT, 40, 66, "You cannot escape fate. It's already decided.", "Your future is bright... but I'll be ready to change it next time.", 31);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_LEFT, 34, 67, "I've scaled icy peaks, but this battle might be the real challenge.", "Even explorers get lost sometimes. I'll find my way back.", 32, LEFT + UP + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(STUDENT_M_UP, 38, 78, "I'll show you what I've learned outside the classroom!", "You win... I guess I'll hit the books after all.", 33);
		
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE_FULL, 19, 49, "", "", -1);
		
		gp.npc[mapNum][index] = NPCSetup(LADY_RIGHT, 28, 43, "Elegance is key, but strength is where I shine.", "I've been bested, but you've gained my respect.", 35);
		gp.npc[mapNum][index] = NPCSetup(STUDENT_F_RIGHT, 15, 37, "Let's see if you've done your homework!", "You aced this one. I'll get you next time!", 36);
		gp.npc[mapNum][index] = NPCSetup(STUDENT_M_LEFT, 19, 37, "Test time! And guess what - you're failing!", "I failed the test... but I'll ace the next one!", 37);
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_RIGHT, 21, 33, "Your thoughts are clear to me... and they're full of doubt.", "You've disrupted the flow of destiny... but I'll recalibrate.", 38, DOWN + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(GENTLEMAN_RIGHT, 22, 21, "A proper duel, wouldn't you say? Let's make this dignified.", "You've bested me, and I commend you for it.", 39);
		
		mapNum = 13;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Photon", 20, 63, "Oh hello there kid, I'm a research assistant for the Professor that specializes in Electric forms of Pokemon.", true, 33, "");
		gp.npc[mapNum][index++] = null;
		gp.npc[mapNum][index++] = null;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Researcher", 88, 18, "Well hello there young man!", true);
		gp.npc[mapNum][index] = NPCSetup(SCOTT_DOWN, 28, 10, "In updateNPC", "", 55); // scott 2
		gp.npc[mapNum][index++] = null; // ryder 2
		gp.npc[mapNum][index++] = null; // alakazam
		gp.npc[mapNum][index++] = null; // ryder 2.1
		gp.npc[mapNum][index++] = null; // alakazam 2.1
		
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_LEFT, 29, 49, "Oi, buzz off brat! I'm trying to sneaky here!", "Guess I'll have to make a quick getaway.", 104);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_RIGHT, 23, 49, "You've got good timing... too bad for you it's bad luck.", "Alright, alright, I surrender... for now.", 105);
		gp.npc[mapNum][index] = NPCSetup(HIKER_LEFT, 29, 34, "Mountains don't move, and neither will I.", "A tough fight, but I'll bounce back.", 71);
		gp.npc[mapNum][index] = NPCSetup(ACTOR_LEFT, 19, 37, "Acting's all about deception. Are you ready for a plot twist?", "Bravo! You've outperformed me this time.", 72);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_RIGHT, 35, 24, "Speed and power are all that matter here!", "Pushed me to the limit there. I'll get you next time.", 73);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_RIGHT, 28, 18, "I've seen things... dark, twisted things... You won't survive.", "The spirits... they were wrong this time... but they won't be forever.", 74);
		
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_DOWN, 44, 18, "I packed snacks, but I'm hungry for a victory!", "You're strong... but I've still got a whole basket of potential!", 360);
		gp.npc[mapNum][index] = NPCSetup(ACTRESS_UP, 54, 17, "Prepare for a performance you'll never forget.", "You think you've won the role? We'll see about that.", 362);
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_M_UP, 71, 17, "You're about to see a trick you'll never forget.", "Well, that wasn't part of the act... I'll need to work on my tricks.", 363);
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_RIGHT, 59, 28, "I'll bend your mind and your Pokemon to my will.", "You've shown surprising strength... but the stars still favor me.", 361);
		gp.npc[mapNum][index] = NPCSetup(ASTRONOMER_LEFT, 79, 11, "The stars guide my team. Let's see if you can handle their cosmic power.", "The stars... must have misaligned this time.", 364);
		gp.npc[mapNum][index] = NPCSetup(ACE_TRAINER_M_DOWN, 82, 28, "My skills are legendary. Let's see if you can keep up.", "Looks like you've earned some respect... for now.", 365);
		
		mapNum = 14;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(MANIAC_LEFT, 36, 42, "Fear the shadows, for I command them. Prepare to tremble.", "You survived this... but you won't be so lucky next time.", 44);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_DOWN, 27, 30, "Every battle is a test of willpower. Let's see if yours breaks.", "Even the strongest fall... but I'll rise again.", 40);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_DOWN, 38, 38, "I collect the macabre... and your defeat will be my latest addition.", "Even in loss, I feel their presence... You'll face them again.", 41);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_RIGHT, 25, 43, "Thank goodness nobody saw me, I might be in the clear... Wait a minute, where'd you come from?", "Good thing I always have a backup plan!", 42);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_LEFT, 30, 43, "Looks like we've been caught in the act. Let's make this quick!", "You won the battle, but I'm still walking away with the loot!", 43);
		gp.npc[mapNum][index] = SetupStaticEncounter(205, 34, 31, 366, 37, "Grzzinzsttt!");
		
		mapNum = 16;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_RIGHT, 22, 29, "Fishing's all about patience... but I'm not waiting to win this!", "Looks like I'm the one who got caught this time.", 45);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_RIGHT, 26, 29, "You're about to be hooked by my strategy!", "Well, back to fishing. Maybe I'll catch a win next time.", 46);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_LEFT, 46, 29, "The water's calm, but I'm about to make some waves!", "You slipped away this time, but I'll reel you in eventually!", 47);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_RIGHT, 21, 39, "I've reeled in bigger fish than you. Let's do this!", "Guess I'll have to find a new spot to fish and train.", 48);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 62, 39, "", "", 273);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 67, 42, "", "", 274);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 72, 41, "", "", 275);
		gp.npc[mapNum][index] = SetupStaticEncounter(197, 32, 28, 367, 39, "Bzrollzfffff...");
		gp.npc[mapNum][index] = NPCSetup(STANFORD, "Stanford", 31, 28, "*growls* Um, what the hell do you want? Oh, right...", true, 41, "");
		
		mapNum = 17;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 51, 45, "Its' will is absolute. You'll comply or face defeat.", "Temporary setback. It will not be deterred.", 49);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 55, 45, "You won't be the first to try and fail. Prepare yourself.", "You may have won, but you're too late to stop us.", 50);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 51, 42, "Resistance is pointless. Team Eclipse is unstoppable.", "Defeated... but the mission goes on. You've gained nothing.", 51);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 55, 42, "You think you're a hero? Let's see how long that lasts.", "This isn't the end. Team Eclipse always has a backup plan.", 52);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 51, 39, "Unauthorized entry detected. Initiating combat sequence.", "You got lucky this time. Next time, you won't stand a chance.", 53);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 55, 39, "Our goals are beyond your understanding. Defeating me changes nothing.", "Consider this a minor setback. We'll be back stronger.", 54);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_RIGHT, null, 49, 53, "Quick! Team Nuke is taking over our office! Please help!", 42);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_LEFT, null, 57, 53, "Quick! Team Nuke is taking over our office! Please help!", 42);
		
		mapNum = 18;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, "Scott", 53, 35, "Can't... keep... fighting...", true);
		gp.npc[mapNum][index++] = null;
		gp.npc[mapNum][index] = SetupStaticEncounter(202, 53, 34, 368, 43, "Dgrughhh!");
		gp.npc[mapNum][index] = NPCSetup(ACE_TRAINER_F_RIGHT, 45, 38, "You might think you stand a chance... but that's a fantasy.", "Guess dragons aren't invincible after all. This time.", 146);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 46, 33, "That was an impressive battle! I found a rare MAGIC Pokemon, but after watching that, you'd be a better trainer. Here!", true, 66, "Enjoy all the wonders of the MAGIC type!");
		
		mapNum = 21;
		gp.npc[mapNum][index] = NPCSetup(GENTLEMAN_DOWN, 55, 60, "A fine battle calls for refinement and skill. Let's proceed with grace.", "Well played, my friend. A loss is but a lesson in disguise.", 56);
		gp.npc[mapNum][index] = NPCSetup(LADY_UP, 55, 62, "Let's keep this battle civilized, but make no mistake - we plan to win.", "A surprising result... but I won't dwell on it for long.", 57);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_DOWN, 72, 52, "Ready for a lesson from someone younger than you?", "Next time, I'll wipe that smirk off your face!", 58);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_UP, 72, 54, "We might be small, but our Pokemon pack a punch!", "I'll get stronger, just you wait!", 59);
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_F_RIGHT, 73, 57, "Let's make your chances disappear in a puff of smoke!", "You may have bested us, but magic is full of surprises.", 60);
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_M_LEFT, 75, 57, "This battle is going to be magic. Too bad you're not prepared for it.", "You're good... but I'll conjure a victory eventually.", 61);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_DOWN, 72, 46, "This is gonna hurt... but only for you.", "Defeated, but never broken.", 62);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_UP, 72, 48, "My fists are my words. Let's have a 'conversation.'", "You hit hard, but next time, I'll hit harder.", 63);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_DOWN, 54, 46, "We've been training nonstop! You don't stand a chance!", "Aw man! How did you beat me?!", 64);
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_UP, 54, 48, "We may be young, but my Pokemon have bite - literally!", "You won, but next time I'll ruin your chances!", 65);
		
		gp.npc[mapNum][index] = NPCSetup(STANFORD, 63, 39, "So you made it, huh. Seems like you're floating on your momentum right now, and I don't like it.\n"
				+ "I appreciate you actually caring about our town, but I'm giving you an ego check.\n"
				+ "Don't expect any special treatment from me. I don't care how many favors you've done - this battle is no different.\n"
				+ "Normal Pokemon don't need fancy tricks to win. We're strong, steady, and tough.\n"
				+ "So come on, let's see if you've got what it takes.",
				"Hmph... You actually beat me. Fuck.\n"
				+ "Maybe you're tougher than you look. I'll admit, you earned it, but don't let it go to your head.\n"
				+ "You saved my town, sure, but that doesn't mean I'll go easy on you next time.\n"
				+ "Take the badge and get out of here before I change my mind.\n", 66);
		
		mapNum = 22;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(LADY_DOWN, 80, 8, "You may think I'm all grace, but you'll soon learn otherwise.", "You've won this round. Enjoy it while it lasts.", 67);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_UP, 78, 12, "You're about to face your nightmares made real.", "I disappear... for now. But don't think this is over.", 68);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_DOWN, 72, 10, "You're about to find out why bugs rule the world!", "Looks like I need to evolve too...", 69);
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_M_UP, 69, 14, "I'll make your victory... disappear!", "The magic didn't work this time, but next time, I'll be the master of illusions!", 70);
		
		mapNum = 23;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 45, "Wow, there's a lot of lava over there. It's definitely not safe for any newbie trainers. If I see any of them coming this way... I swear to god...", 5);
		
		mapNum = 24;
		gp.npc[mapNum][index] = NPCSetup(MAGICIAN_F_RIGHT, 68, 63, "I'm about to cast a spell... on your defeat!", "My illusions failed me, but I'll perfect them soon enough.", 75, ALL);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_UP, 79, 59, "I've seen harsh conditions, but you're about to face the frost of defeat.", "I'll be better prepared for the next expedition.", 267);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_DOWN, 83, 55, "Prepare for an expedition into icy defeat!", "The cold got to me this time... but I'll warm up next time.", 268);
		
		mapNum = 25;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(GENTLEMAN_RIGHT, 71, 65, "May the best trainer win, but rest assured, it will be me.", "You fought with honor. I respect that greatly.", 76, LEFT + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(HIKER_RIGHT, 73, 80, "I've seen all sorts of things, I've traveled all over the world. Check out my exotic team!", "Maybe I should look under my nose for some more power. These guys weren't good enough...", 77, ALL);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_UP, 66, 87, "The spirits whisper to me... They say you're next.", "I've tasted defeat... but I'll rise again, like the restless dead.", 78);
		
		mapNum = 26;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 58, 70, "", "", 79);
		
		index = 0;
		// Nurses/PCs
		gp.npc[29][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[29][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[86][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[86][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[111][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[111][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[125][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[125][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		
		// Clerks
		gp.npc[30][index] = SetupClerk(NPC_MARKET, 31, 41, Item.REPEL, Item.POKEBALL, Item.KLEINE_BAR, Item.RUSTY_BOTTLE_CAP, Item.BOTTLE_CAP, Item.TM49, Item.TM51);
		gp.npc[40][index] = SetupClerk(NPC_MARKET, 34, 38, Item.TM12, Item.TM13, Item.TM15, Item.TM16, Item.TM23, Item.TM24);
		gp.npc[45][index] = NPCSetup(NPC_CLERK, 30, 39, "", "", -1);
		gp.npc[87][index] = NPCSetup(NPC_CLERK, 27, 39, "", "", -1);
		gp.npc[89][index] = SetupClerk(NPC_MARKET, 24, 36, Item.TM45, Item.TM50, Item.TM54, Item.TM74, Item.TM75,
                Item.TM76, Item.TM77, Item.TM79, Item.TM80, Item.TM81, Item.TM82, Item.TM95);
		gp.npc[112][index] = SetupClerk(NPC_MARKET, 31, 41, Item.MAX_ELIXIR, Item.PP_UP, Item.TM41, Item.TM42, Item.TM43, Item.TM44, Item.TM91);
		gp.npc[126][index] = NPCSetup(NPC_CLERK, 27, 39, "", "", -1);
		gp.npc[132][index] = NPCSetup(NPC_CLERK, 30, 39, "", "", -1);
		gp.npc[133][index] = SetupClerk(NPC_MARKET, 34, 38, Item.TM46, Item.TM63, Item.TM38);
		
		mapNum = 28;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(MILLIE, "Millie", 62, 46, "EEK! Wait... your eyes aren't radioactive green.", true);
		gp.npc[mapNum][index++] = null; // replace with millie_up when we have it
		gp.npc[mapNum][index] = NPCSetup(UP_XURKITREE, null, 82, 22, "Zzzzt.", true);
		gp.npc[mapNum][index] = NPCSetup(FRED_DOWN, "Fred", 82, 32, "Ugh... Can't... Move... glrglg....");
		gp.npc[mapNum][index] = NPCSetup(FRED_DOWN, 82, 32, "In updateNPC", "", 89);
		gp.npc[mapNum][index++] = null; // millie 4
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_DOWN, 12, 52, "Caught me red-handed? I'll just take your victory instead!", "Tch... Guess I can't steal a win from you.", 85);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_UP, 13, 55, "The spirits whisper... they're eager for a battle!", "The spirits... they're displeased with this outcome...", 86);
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_DOWN, 30, 50, "Prepare yourself... I've already seen how this ends.", "It's just a temporary lapse in foresight... Next time, I'll win.", 80);
		gp.npc[mapNum][index] = NPCSetup(ACTRESS_RIGHT, 50, 52, "Don't think I'll go easy on you just because I'm in the spotlight.", "Even stars lose sometimes... but I'll rise again.", 87);
		gp.npc[mapNum][index] = NPCSetup(HIKER_UP, 52, 55, "Tread lightly, or the ground will swallow you whole!", "Seems like you've found your footing.", 81);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_DOWN, 56, 46, "Time to light this battle up! You ready?", "I'll hit the track and come back even stronger.", 82);
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_LEFT, 60, 49, "You better not trample the flowers... or my Pokemon!", "Losing makes the picnic food taste a little less sweet.", 88);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_UP, 38, 36, "I fight with honor, but I won't go easy on you.", "A powerful opponent. I'll train harder for the next round.", 83);
		gp.npc[mapNum][index] = NPCSetup(ACTRESS_RIGHT, 50, 36, "It's time for the grand finale. Too bad it'll be your defeat.", "I may have lost, but my spotlight never dims.", 84);
		
		// These stay: blocking certain items (Acrobatics and Ability Capsule)
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_RIGHT, 67, 17, "Whether it's the field or the battlefield, I always come out on top!", "Hmph, not bad. But next time, I'll be unstoppable!", 95);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_DOWN, 71, 11, "I've got the energy, and my Pokemon are fired up! Let's go!", "Looks like I lost this race... but I'll train harder!", 96);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_LEFT, 75, 17, "Speed and power, that's my game! Let's see if you can keep up!", "Guess I need to pick up the pace... I'll be faster next time!", 97);
		gp.npc[mapNum][index] = NPCSetup(ACE_TRAINER_F_UP, 68, 28, "I've trained for years - are you prepared to be humbled?", "A solid victory. You've earned this one.", 386);
		
		// Possessed trainers (will clear)
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 82, 37, "..!", "...", 370, DOWN + LEFT); // normal
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 84, 23, "..!", "...", 379); // ghost
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 80, 23, "..!", "...", 381); // steel
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 80, 24, "..!", "...", 382); // dark
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 84, 24, "..!", "...", 371); // electric
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 82, 25, "..!", "...", 378); // psychic
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 87, 31, "..!", "...", 374); // fighting
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 77, 31, "..!", "...", 375); // poison
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 78, 43, "..!", "...", 372, RIGHT + DOWN); // ice
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 87, 45, "..!", "...", 377, LEFT + DOWN); // flying
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 79, 50, "..!", "...", 373, RIGHT + DOWN); // rock
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 83, 57, "..!", "...", 383); // light
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 71, 41, "..!", "...", 384, RIGHT + DOWN); // magic
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 76, 47, "..!", "...", 380, LEFT + RIGHT + UP); // dragon
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 66, 49, "..!", "...", 376, LEFT + UP); // ground
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 73, 51, "..!", "...", 385, LEFT + UP + DOWN); // galactic
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 75, 69, "", "", 202);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 78, 83, "", "", 203);
		gp.npc[mapNum][index] = NPCSetup(NPC_PC_GAUNTLET, 83, 33, "", "", -1);
		gp.npc[mapNum][index++] = null; // millie 5
		
		mapNum = 31;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_DOWN, 48, 50, "You think you can squash my team? Think again!", "Squashed me, huh? Next time, I'll sting harder!", 90);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_LEFT, 65, 61, "These little guys pack a punch, don't underestimate them!", "I may have lost, but bugs always come back.", 91, LEFT + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_DOWN, 45, 63, "Bugs may be small, but they're tougher than you think!", "Aw man, my bugs weren't strong enough!", 92, DOWN + UP + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(BUG_CATCHER_RIGHT, 38, 70, "My bugs are fast and fierce! You won't squash them easily!", "No way! I thought my bugs were unbeatable...", 93, DOWN + RIGHT);
		gp.npc[mapNum][index] = NPCSetup(MILLIE, 51, 61, "EEE! It's you again! The hero who saved our town from that spooky Ultra Paradox Pokemon!\n"
				+ "Everyone's been talking about you, and now I get to battle you! I'm sooo excited!\n"
				+ "But don't think that just because you saved us means I'll go easy on you! My Bug Pokemon have been training super hard, and we're ready to put on a show!\n"
				+ "This battle's going to be epic - just like when you saved the day! Ready for round two? Let's go!",
				"You beat me? That's, like, SO cool!\n"
				+ "But you've got to admit, my Bugs put on a good show, right?\n"
				+ "You're seriously amazing, and I'm totally not even mad. Take the badge with honor!\n"
				+ "Just promise me one thing - keep being awesome! I'll catch up to you next time, I just know it!", 94);
		
		mapNum = 32;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_DOWN, "Fisherman", 31, 40, "Back in my day, I used to be a top angler. Felt good reeling in them in, but I think I've outgrown it.", true, 49, "Look at water and press 'A' to fish!");
		
		mapNum = 33;
		index = 0;
		if (gp.player.p.badges >= 5 && !flags[19]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 62, 17, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders went to Ghostly Woods and is planning something evil.", 19);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 49, 78, "", "", 195);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 40, 84, "", "", 196);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 73, "", "", 197);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 22, 81, "", "", 198);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 25, 45, "", "", 199);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 32, 41, "", "", 200);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 41, 44, "", "", 201);
		
		gp.npc[mapNum][index] = NPCSetup(ASTRONOMER_LEFT, 71, 18, "You're about to be eclipsed by the brilliance of the universe!", "You've won this time, but the cosmos never stays the same for long.", 269);
		
		mapNum = 36;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(PICKNICKER_DOWN, 20, 45, "The sun's shining, the bugs are buzzing... and you're about to lose!", "I guess I should've focused more on training than snacking...", 106);
		gp.npc[mapNum][index] = NPCSetup(HIKER_LEFT, 39, 47, "I've seen rough terrain, but nothing rougher than what's coming for you.", "You're tougher than granite, I'll give you that.", 107);
		gp.npc[mapNum][index] = NPCSetup(STUDENT_F_DOWN, 42, 27, "This is going to be an easy A for me.", "Ugh, a bad grade. I'm not used to this!", 108);
		gp.npc[mapNum][index] = NPCSetup(PSYCHIC_RIGHT, 44, 32, "I've already foreseen this battle's outcome. Care to test fate?", "Impossible! This wasn't what I foresaw...", 109);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_DOWN, 60, 31, "You're on my line now, and I'm not letting go!", "Not my day, but the sea's full of second chances.", 110);
		gp.npc[mapNum][index] = NPCSetup(BLACK_BELT_RIGHT, 53, 44, "A true warrior never backs down from a challenge!", "Your strength is commendable... I've been bested.", 111);
		gp.npc[mapNum][index] = NPCSetup(HIKER_UP, 65, 48, "Strength isn't just in your fists, it's in your foundation.", "I'll build myself back up after this.", 112);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_LEFT, 82, 39, "I'm quick on my feet, and my Pokemon strike even faster!", "You've got moves, I'll give you that!", 113);
		gp.npc[mapNum][index] = NPCSetup(FISHERMAN_RIGHT, 67, 30, "The waves are calm, but this battle will be anything but!", "Looks like I let this one slip through the net...", 114);
		gp.npc[mapNum][index] = NPCSetup(BURGLAR_DOWN, 76, 25, "You can't stop a professional! I'll swipe this win!", "You're good... Too good for my tricks.", 115);
		gp.npc[mapNum][index] = NPCSetup(ASTRONOMER_UP, 91, 43, "The galaxy is vast... and you're about to be lost in it.", "I'll recalibrate my strategy... and the stars will guide me to victory.", 116);
		
		if (gp.player.p.badges >= 5 && !flags[19]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 15, 46, "There's some SCARY people wearing black in there somewhere. I heard one of their leaders go to Ghostly Woods and is planning something evil.", 19);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 38;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_DOWN, 53, 7, "Ah, you're here to seek my key, are you?\nI don't know what's causing this blinding light, but I can't afford to let just anyone enter my classroom. You'll need to prove your resilience first.", "You fought well - better than I expected. The Ice Master's key is yours.\nUse it wisely... those Grunts won't go down easily, but if anyone can drive them out, it's you.", 327);
		gp.npc[mapNum][index++] = null; // robin
		
		gp.npc[mapNum][index] = NPCSetup(BIRD_KEEPER_LEFT, 35, 74, "Flying high is all I know. Are you ready to fall?", "You grounded me... but I'll be back in the skies soon!", 138);
		gp.npc[mapNum][index] = NPCSetup(SWIMMER_F_RIGHT, 49, 74, "I just got an awesome TM move! Check it out!", "Fine, I'll tell you my secrets. I found it near New Minnow Town... that's all I'll say, for now.", 139, ALL);
		gp.npc[mapNum][index] = NPCSetup(SWIMMER_M_RIGHT, 46, 85, "", "", 140, ALL);
		gp.npc[mapNum][index] = NPCSetup(ATHLETE_RIGHT, 36, 84, "Guess what? I delayed my Twigzap's evolution to get a really powerful move. Wanna see?", "Guess even my secret technique wasn't good enough. Gave you a run for your money though!", 141);
		
		mapNum = 39;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[mapNum][index] = NPCSetup(RYDER_DOWN, "Ryder", 28, 41, "Howdy there! Glad to see you could make it in this weather.", true);
		gp.npc[mapNum][index] = NPCSetup(ALAKAZAM, "Alakazam", 27, 41, "Vxxxvhh...");
		
		mapNum = 41;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 42, 31, "A school is just another battlefield. Don't think you're safe here.", "Fine, you beat us. But the true challenge awaits deeper inside.", 117);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 42, 36, "We've taken over the classrooms, and no one's getting in without a fight.", "You beat us, but you'll never stop what we've set in motion.", 118);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 20, 31, "You think you can unlock those classrooms? Over my defeated Pokemon!", "It's not the door you should be worried about. It's what's waiting for you next.", 119);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 20, 36, "No one escapes from Team Eclipse's field trip. You'll be schooled in defeat.", "We've lost this battle, but the school's still ours.", 120);
		
		mapNum = 163;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 32, 43, "You made it this far, but you won't disrupt our plans in this school.", "So you've won... but going further will only lead you to more trouble.", 121);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 30, 46, "Interrupting class? That's a grave mistake. Time for detention!", "Defeated in class... how embarrassing. Don't think it ends here.", 122);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 34, 46, "You're too late to stop us from securing the tools we need. Prepare yourself!", "You think this is over just because you've won? There are more of us waiting.", 123);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 36, 40, "You're out of your league. We've taken control of the curriculum now.", "Enjoy your little victory. You're still just another stepping stone for us.", 124);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 41, 42, "This is no place for a hero. You'll learn your lesson the hard way.", "You're smarter than you look, but that won't save you underground.", 125);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 36, 45, "It's my turn now. You got lucky last time, but your streak ends here!", "This is just a setback. Our plans won't be stopped by you!", 126);
		
		mapNum = 164;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 29, 46, "You're walking into a trap. This isn't just a school anymore; it's our stronghold.", "You may have passed this test, but Team Eclipse doesn't give out easy grades.", 127);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 35, 46, "You think you can handle both of us? You're in for double the trouble!", "Ugh... I guess I couldn't carry the team. You're still not out of this yet!", 128);
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 30, 42, "Team Eclipse doesn't fight fair. You'll have to get through both of us!", "You may have beaten us, but our partners are next. They'll crush you!", 129);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 34, 42, "Ready for a lesson in teamwork? We'll show you what real coordination looks like.", "Fine, I'm out. Good luck with the rest of us though.", 130);
		gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 36, 36, "You're after the shovel, huh? You'll have to go through us to get it!", "Consider this your only win. The real battle is waiting for you next.", 131);
		gp.npc[mapNum][index] = NPCSetup(TN_UP, 37, 40, "This tool is crucial for our mission. You won't be digging your way out of this fight.", "Fine, take the shovel. It won't save you from what's coming next.", 132);
		
		mapNum = 165;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Principal", 33, 41, "Ahh! Oh hello there, you gave me quite a scare - those Team Eclipse thugs are everywhere!", true, 103, "I feel like I can finally breathe again now that those Grunts are gone. It's a relief to have the school safe once more... and it's all thanks to you!");
		
		mapNum = 43;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(HIKER_DOWN, 31, 42, "You've come for the key, huh?\nI don't mind the bright light much, it's nothing compared to the blazing heat beneath the earth's surface.\nBut these Grunts are wreaking havoc, and I won't let them win so easily.\n Show me your strength!", "You've earned it. Take the key, and with it, take on those Grunts who've infested the school. We can't let them win.", 328);
		
		mapNum = 44;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Student", 63, 64, "Sorry, but the gym's off-limits right now.", true);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_RIGHT, 60, 61, "I've conquered frozen mountains. A battle like this is nothing!", "Looks like I've found my limit this time...", 133);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_LEFT, 66, 57, "The cold sharpens my Pokemon's strength. You won't last long!", "You're tougher than an icy peak. Impressive!", 134);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_RIGHT, 60, 52, "I've braved the elements, but can you withstand my icy tactics?", "Well, that was unexpected... I guess I'll need more than just endurance.", 135);
		gp.npc[mapNum][index] = NPCSetup(EXPLORER_LEFT, 66, 46, "The frozen tundra shaped me, and now I'll break you!", "I'll return stronger... with even colder tactics!", 136);
		gp.npc[mapNum][index] = NPCSetup(GLACIUS, 63, 39, "Ah, a young challenger approaches... reminds me of when I used to brave the frozen tundra in my youth.\n"
				+ "You're here for a battle, yes? Let me tell you something. Battling is like an expedition - strategy, survival, and above all, perseverance.\n"
				+ "I heard what you accomplished at the school, and that you're the one that saved the city from that atrocious light.\n"
				+ "But let me tell you something, Ice Pokemon are cold and resilient. We've weathered storms and faced impossible odds.\n"
				+ "Can you handle the frostbite of battle, young one? Or will you be lost in the blizzard? Let's see what you're made of.",
				"Impressive. You've weathered the storm and come out stronger.\n"
				+ "Few can stand against the bitter cold of my Ice Pokemon, but you... you have the spirit of an explorer.\n"
				+ "Take the badge, a symbol of your victory and resilience.\n"
				+ "May you continue your journey with courage and face whatever challenges come your way.", 137);
		
		mapNum = 46;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Check your team's Hidden Power types here!", true);
		
		mapNum = 47;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Hello there, welcome to Bananaville's Pokemon Ranch!", true, 6, "Why have one partner when you can have two, am I right?");
		
		mapNum = 48;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 42, "", "", 142);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 42, "", "", 143);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 31, 36, "", "", 144);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 39, "", "", 145);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Employee", 24, 37, "Hello, and welcome to the Poundtown, where your dog can train and play at the same time! Feel free to look around at all of our amenities.", true, 10, "That little pup treating you alright? I bet he'll grow strong if you give it lots of love!");
		
		mapNum = 49;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Deep below ELECTRIC TUNNEL there's a secret trail called SHADOW PATH.", true, 78, "Now don't go telling the feds I gave you that. It's definitely illegal.");
		
		mapNum = 50;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "WOAOAOHAOH!!! Hehehehehe I just popped a naughty yerkocet!! Pick one of these NUTTY \"starters\" tehehee", true, 105, "YEEEOOEEOOOOeeeoooeeooeee.....");
		
		mapNum = 51;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(GRANDMOTHER, "Grandma", 31, 41, "Why what a surprise! Hello there dear, I'm glad you could visit your grandmother before you left home.", true, 3, "Glad you could check in dear, but I'm fine. You still got an adventure ahead of you."); // Dad's Mom
		
		mapNum = 52;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(PROFESSOR, "Dad", 31, 36, "Well hiya there son!", true); // Professor Dad
		gp.npc[mapNum][index] = NPCSetup(RESEARCHER, null, 31, 45, "Heh, you're definitely excited to head out there, but your Dad wanted to see you off before you start your adventure.");
		
		mapNum = 53;
		index = 0;
		gp.npc[mapNum][index] = SetupClerk(NPC_MARKET, 31, 41, Item.ORAN_BERRY, Item.CHERI_BERRY, Item.CHESTO_BERRY, Item.PECHA_BERRY, Item.RAWST_BERRY, Item.ASPEAR_BERRY,
                Item.PERSIM_BERRY, Item.LUM_BERRY, Item.LEPPA_BERRY, Item.SITRUS_BERRY, Item.WIKI_BERRY, Item.OCCA_BERRY, Item.PASSHO_BERRY, Item.WACAN_BERRY, Item.RINDO_BERRY,
                Item.YACHE_BERRY, Item.CHOPLE_BERRY, Item.KEBIA_BERRY, Item.SHUCA_BERRY, Item.COBA_BERRY, Item.PAYAPA_BERRY, Item.TANGA_BERRY, Item.CHARTI_BERRY,
                Item.KASIB_BERRY, Item.HABAN_BERRY, Item.COLBUR_BERRY, Item.BABIRI_BERRY, Item.CHILAN_BERRY, Item.ROSELI_BERRY, Item.MYSTICOLA_BERRY, Item.GALAXEED_BERRY,
                Item.LIECHI_BERRY, Item.GANLON_BERRY, Item.SALAC_BERRY, Item.PETAYA_BERRY, Item.APICOT_BERRY, Item.STARF_BERRY, Item.MICLE_BERRY, Item.CUSTAP_BERRY);
		
		mapNum = 57;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(CHEF, "Guy Eddie", 31, 41, "Eyy! What brings a scrawny kid like you into the famous Guy Eddie's culinary residence!", true, 9, "By the way, if you're ever in Rawwar City, head over to my restaurant. You won't wanna miss the Eddie special, Smoked Pie! It's gonna be a blast burn!");
		
		mapNum = 58;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(LADY_DOWN, "Ms. Plum", 33, 42, "AAAGHHHHH!! GET OUT THIEF!", true, 18, "Thank you so much for your kindness! I hope you have a great day :)");
		
		mapNum = 60;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(ASTRONOMER_DOWN, 38, 38, "Prepare to face forces beyond your understanding.", "You've navigated the stars well... but I'll see you again in the cosmos.", 147);
		gp.npc[mapNum][index] = NPCSetup(MANIAC_UP, 38, 42, "Do you hear that? The ghosts are calling for your defeat!", "This was not foreseen in the shadows...", 148);
		gp.npc[mapNum][index] = SetupStaticEncounter(210, 31, 36, 369, 46, "Graagzyaraa!!");
		
		mapNum = 77;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 18, "", "", 149);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 24, "", "", 150);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 45, 30, "", "", 151);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 52, 64, "", "", 152);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 54, 37, "", "", 153);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 54, 52, "", "", 154);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 86, 33, "", "", 155);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 67, 34, "", "", 156);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 74, 61, "", "", 157);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 32, 45, "", "", 158);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 18, 20, "", "", 159);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 18, 24, "", "", 160);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 87, 15, "", "", 161);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 41, "", "", 162);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 47, "", "", 163);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 23, 60, "", "", 164);
		
		mapNum = 80;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 33, 19, "", "", 165);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 27, 22, "", "", 166);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 28, "", "", 167);
		gp.npc[mapNum][index] = NPCSetup(SCOTT_UP, 35, 41, "", "", 185);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 41, 51, "", "", 168);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 31, 64, "", "", 169);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 75, 57, "", "", 170);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 49, 74, "", "", 171);
		
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE_FULL, 25, 11, "", "", -1);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 17, 68, "", "", 257);
		
		mapNum = 83;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 48, 64, "", "", 172);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 48, 67, "", "", 173);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 60, 70, "", "", 180);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 61, 66, "", "", 181);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 65, 65, "", "", 182);
		
		mapNum = 85;
		index = 0;
		if (!flags[16]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 58, 64, "Talk to Grandpa (bottom house all the way southwest)", 16);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 69, "", "", 218);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 85, 64, "", "", 219);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 93, 54, "", "", 220);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 83, 51, "", "", 221);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 85, 37, "", "", 222);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 88, 40, "", "", 223);
		
		if (!flags[17]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 63, 67, "EEEK! DON'T GO THIS WAY! I saw some really scary men dressed up in black go towards the woods this way. BE CAREFUL!", 17);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 88;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 75, 57, "", "", 186);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 81, 57, "", "", 187);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 85, 63, "", "", 188);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 45, 67, "", "", 189);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 41, 53, "", "", 190);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 51, 53, "", "", 191);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 41, 47, "", "", 192);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 55, 47, "", "", 193);
		gp.npc[mapNum][index] = NPCSetup(MINDY, 63, 35, "It's you. I sensed you would come.\n"
				+ "You've been quite the force for good in our world, especially after helping my daughter, Millie.\n"
				+ "She's told me all about how you saved her town from the Ultra Paradox Pokemon.\n"
				+ "That battle took a toll on you, didn't it? The mind can be both fragile and powerful, much like my Psychic Pokemon.\n"
				+ "Now, let's see how you handle the challenges of the mind. A battle isn't just about strength, but clarity and focus.\n"
				+ "Are you prepared to face your inner self?",
				"Ah... such clarity. You've grown stronger not just physically, but mentally as well.\n"
				+ "It seems Millie's faith in you was well-placed. You've faced fear, doubt, and confusion - and emerged victorious.\n"
				+ "Take the badge as a token of your mental fortitude.\n"
				+ "Continue your journey with a clear mind. There are many more battles ahead, but you'll face them with strength and wisdom.", 194);
		
		mapNum = 90;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 48, 10, "", "", 174);
		
		mapNum = 91;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 29, 45, "", "", 183);
		gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 33, 45, "", "", 184);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Thank you so much for saving me! Here, take this as a gift!", true, 16, "Love you grandson. Keep doing good things!");
		
		mapNum = 92;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		gp.npc[mapNum][index] = SetupClerk(NPC_MARKET, 22, 37, Item.ADAMANT_MINT, Item.BOLD_MINT, Item.BRAVE_MINT, Item.CALM_MINT, Item.CAREFUL_MINT, Item.IMPISH_MINT,
                Item.JOLLY_MINT, Item.MODEST_MINT, Item.QUIET_MINT, Item.SERIOUS_MINT, Item.TIMID_MINT);
		
		mapNum = 93;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Would you like to remember a move? Which Pokemon should remember?", true);
		
		mapNum = 94;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "There have been 2 meteorites that have crashed into our region.", true, 18, "You should explore the Hearts of the ELECTRIC TUNNEL and SHADOW\nRAVINE. They're really pretty.");
		
		mapNum = 104;
		index = 0;
		if (!flags[20]) {
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 47, "", "", 204);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 47, "", "", 205);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 49, "", "", 206);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 49, "", "", 207);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 51, "", "", 208);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 51, "", "", 209);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 53, "", "", 210);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 53, "", "", 211);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 55, "", "", 212);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 55, "", "", 213);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 44, 57, "", "", 214);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 49, 57, "", "", 215);
			
			gp.npc[mapNum][index] = NPCSetup(FRED_UP, 44, 62, "", "", 216);
			gp.npc[mapNum][index] = NPCSetup(MAXWELL, 41, 59, "", "", 217);
		} else {
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 47, 204), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 47, 205), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 49, 206), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 49, 207), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 51, 208), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 51, 209), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 53, 210), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 53, 211), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 55, 212), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 55, 213), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 44, 57, 214), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 49, 57, 215), mapNum);
//			
//			GamePanel.volatileTrainers.put(NPCSetup(FRED_UP, 44, 62, 216), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(MAXWELL, 41, 59, 217), mapNum);
		}
		
		
		mapNum = 107;
		index = 0;
		if (!flags[19] && gp.player.p.grustCount >= 10) {
			gp.npc[mapNum][index] = NPCSetup(RICK, 46, 60, "", "", 234);
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(RICK, 46, 60, "", "", 234), mapNum);
			index--;
		}
		
		if (gp.player.p.grustCount < 10) {
			int[] xCoords = new int[] {49, 78, 62, 61, 61, 79, 42, 75, 60, 17};
			int[] yCoords = new int[] {88, 80, 81, 76, 70, 59, 77, 44, 42, 72};
			
			for (int i = 224; i <= 233; i++) {
				if (gp.player.p.trainersBeat == null || !gp.player.p.trainersBeat[i]) {
					gp.npc[mapNum][index] = NPCSetup(GRUST, xCoords[i-224], yCoords[i-224], "", "", i);
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
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 76, 89, "", "", 235);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 67, 72, "", "", 236);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 49, 44, "", "", 237);
		
		if (!flags[21]) {
			gp.npc[mapNum][index] = NPCSetup(SCOTT_DOWN, 21, 40, "", "", 242);;
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(SCOTT_DOWN, 21, 40, "", "", 242), mapNum);
		}
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 44, 39, "I found this infant Pokemon abandoned here.", true, 22, "Please raise it with love and care!");
		
		mapNum = 110;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 35, 84, "", "", 238);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 25, 78, "", "", 239);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 20, 56, "", "", 240);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 26, 54, "", "", 241);
		
		mapNum = 113;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 55, 58, "", "", 246);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 51, 56, "", "", 247);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 44, 58, "", "", 248);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 42, 56, "", "", 249);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 44, 49, "", "", 250);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 53, 49, "", "", 251);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 62, 47, "", "", 252);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 63, 41, "", "", 253);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 54, 43, "", "", 254);
		gp.npc[mapNum][index] = NPCSetup(RAYNA, 53, 30, "", "", 255);
		
		mapNum = 115;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 69, "", "", 243);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 62, 74, "", "", 244);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 41, 72, "", "", 245);
		
		mapNum = 118;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 41, "Hi! I can revive fossils for you!", true);
		
		mapNum = 119;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(YOUNGSTER_UP, 32, 79, "Just because I'm small doesn't mean me and my moms' Pokemon won't crush you!", "Ugh, maybe I should train a little harder...", 264);
		gp.npc[mapNum][index] = NPCSetup(LADY_LEFT, 37, 76, "Prepare for a match of wit and power, darling.", "I suppose I'll have to work on our strategy. Well done.", 265);
		
		gp.npc[mapNum][index] = NPCSetup(BLOCK_LEFT, null, 23, 76, "You're not ready to fight those guys over there yet, sorry bud.", 5);
		
		mapNum = 121;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 42, "", "", 270);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 28, 37, "", "", 271);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 34, 35, "", "", 272);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 34, "You're definitely not strong enough to go up here. Also, the items on the floor are really powerful, so you can only choose one for now.");
		
		mapNum = 122;
		index = 0;
		
		mapNum = 124;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 25, 21, "", "", 276); // DH
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 24, 24, "", "", 281);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 17, 22, "", "", 280);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 22, 32, "", "", 283);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 15, 32, "", "", 277); // DI
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 12, 43, "", "", 278); // DJ
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 24, 43, "", "", 279); // DK
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 16, 39, "", "", 284);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 15, 28, "", "", 282);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 65, 72, "", "", 285);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 69, 85, "", "", 286);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 84, 84, "", "", 288);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 91, 80, "", "", 289);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 88, 79, "", "", 287);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 86, 72, "", "", 290);
		
		if (!flags[26]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 25, 82, "The gym is closed right now. Why, you ask? Because a goddamn Team Nuke member came here and KIDNAPPED one of our employees.\n\nYeah, what the hell is right! Last I saw him, he was bringing Marcus towards the volcano. I just hope that Marcus doesn't sue us...", 26);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 127;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 27, 39, "Welcome to the Blackjack table!", true);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 35, 39, "This game isn't ready yet!", true);
		
		mapNum = 128;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 55, 66, "", "", 291);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 49, 65, "", "", 292);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 56, 68, "", "", 293);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 47, 68, "", "", 294);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 52, 62, "", "", 295);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 58, 63, "", "", 296);
		gp.npc[mapNum][index] = NPCSetup(INVIS_UP, 55, 63, "", "", 297);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 58, 60, "", "", 298);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 52, 60, "", "", 299);
		gp.npc[mapNum][index] = NPCSetup(INVIS_LEFT, 59, 57, "", "", 300);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 55, 57, "", "", 301);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 49, 59, "", "", 302);
		gp.npc[mapNum][index] = NPCSetup(INVIS_RIGHT, 54, 54, "", "", 303);
		gp.npc[mapNum][index] = NPCSetup(INVIS_DOWN, 53, 56, "", "", 304);
		gp.npc[mapNum][index] = NPCSetup(MERLIN, 53, 54, "", "", 305);
		
		mapNum = 129;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 37, 44, "Hi there! Here, you can exchange coins for money!", true);
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 31, 39, "Hi there! Here, you can exchange coins for prizes!", true);
		
		mapNum = 130;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 24, 36, "Aren't you lookin like you want some SPICE!", true, 25, "ICE SPICE is my idol <3");
		
		mapNum = 131;
		index = 0;
		gp.npc[mapNum][index] = SetupClerk(NPC_MARKET, 31, 41, Item.EUPHORIAN_GEM, Item.LEAF_STONE, Item.DUSK_STONE, Item.DAWN_STONE, Item.ICE_STONE,
				Item.FIRE_STONE, Item.VALIANT_GEM, Item.PETTICOAT_GEM, Item.EVERSTONE, Item.HEAT_ROCK, Item.DAMP_ROCK, Item.SMOOTH_ROCK, Item.ICY_ROCK);
		
		mapNum = 137;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 70, 73, "", "", 306);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 72, 74, "", "", 307);
		
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 37, 46, "", "", 312);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 39, 48, "", "", 313);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 37, 50, "", "", 314);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 35, 48, "", "", 315);
		
		mapNum = 138;
		index = 0;
		if (!flags[26]) {
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 53, 73, "", "", 311);
		} else {
			gp.npc[mapNum][index++] = null;
			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 53, 73, "", "", 311), mapNum);
		}
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 55, 72, "Thank you so much for saving me!", true, 26, "It's so hot in here. How the \"hell\" do I leave???");
		
		mapNum = 139;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 29, 73, "", "", 308);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 46, 50, "", "", 309);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 30, 55, "", "", 310);
		
		mapNum = 144;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(HIKER_RIGHT, 14, 17, "", "", 316);
		gp.npc[mapNum][index] = NPCSetup(ACE_TRAINER_F_LEFT, 24, 16, "", "", 317);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 40, 40, "", "", 318);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 50, 42, "", "", 319);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 55, 44, "", "", 320);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 46, 49, "", "", 321);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 54, 49, "", "", 322);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 57, 49, "", "", 323);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 57, 80, "", "", 324);
		
		mapNum = 146;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(NPC_PC, 79, 27, "", "", -1);
		if (!flags[28]) {
			gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, null, 82, 27, "Inside there is quite the function in there. You are only allowed to bring 10 of your strongest Pokemon, and that's it. Good luck.", true);
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
		gp.npc[mapNum][index] = NPCSetup(NPC_PC_GAUNTLET, 53, 74, "", "", -1);
		if (!flags[28]) {
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 49, 77, "", "", 330);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 46, 76, "", "", 331);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 52, 76, "", "", 332);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 49, 73, "", "", 333);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 56, 67, "", "", 334);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 61, 71, "", "", 335);
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 56, 75, "", "", 336);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 42, 68, "", "", 337);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 36, 71, "", "", 338);
			gp.npc[mapNum][index] = NPCSetup(TN_UP, 42, 74, "", "", 339);
			gp.npc[mapNum][index] = NPCSetup(TN_RIGHT, 36, 65, "", "", 340);
			gp.npc[mapNum][index] = NPCSetup(TN_DOWN, 39, 59, "", "", 341);
			gp.npc[mapNum][index] = NPCSetup(TN_LEFT, 42, 65, "", "", 342);
			gp.npc[mapNum][index] = NPCSetup(RICK, 49, 62, "", "", 343);
			gp.npc[mapNum][index] = NPCSetup(FRED_DOWN, 53, 61, "", "", 344);
			gp.npc[mapNum][index] = NPCSetup(MAXWELL, 49, 56, "", "", 345);
		} else {
//			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 49, 77, 330), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 46, 76, 331), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 52, 76, 332), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 49, 73, 333), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 56, 67, 334), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 60, 71, 335), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 56, 75, 336), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 42, 68, 337), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 38, 71, 338), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_UP, 42, 74, 339), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_RIGHT, 36, 65, 340), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_DOWN, 39, 61, 341), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(TN_LEFT, 42, 65, 342), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(RICK, 49, 62, 343), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(FRED_DOWN, 53, 61, 344), mapNum);
//			GamePanel.volatileTrainers.put(NPCSetup(MAXWELL, 49, 56, 345), mapNum);
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
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 29, 16, "", "", 347);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 29, 22, "", "", 348);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 24, 24, "", "", 349);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 32, 37, "", "", 350);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 39, 38, "", "", 351);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 51, 35, "", "", 352);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_DOWN, 50, 26, "", "", 353);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_LEFT, 57, 19, "", "", 354);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 59, 27, "", "", 355);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 56, 40, "", "", 356);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_RIGHT, 48, 46, "", "", 357);
		gp.npc[mapNum][index] = NPCSetup(TRAINER_UP, 44, 54, "", "", 358);
		
		// Nurses
		gp.npc[153][index] = NPCSetup(NPC_NURSE, 31, 37, "", "", -1);
		gp.npc[153][index] = NPCSetup(NPC_PC, 36, 35, "", "", -1);
		
		// Clerks
		gp.npc[154][index] = NPCSetup(NPC_CLERK, 30, 39, "", "", -1);
		gp.npc[155][index] = SetupClerk(NPC_MARKET, 34, 38, Item.TM46, Item.TM63);
		
		mapNum = 161;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(ROBIN, "Robin", 50, 37, "AGHH!", true);
		
		mapNum = 162;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(BLOCK_DOWN, "Photon", 31, 38, "Oh hello there, welcome to PROFESSOR PHOTON's humble abode!", true);
		
		mapNum = 166;
		index = 0;
		gp.npc[mapNum][index] = SetupStaticEncounter(285, 39, 29, 388, 104, "Gzssisssss!");
		
	}

	public void setInteractiveTile(int map) {
		int mapNum = 0;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(60, 53, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(82, 59, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(33, 72, 1, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = ITileSetup(71, 10, 0, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 27, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 16, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 17, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 18, 4, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(70, 19, 4, mapNum, map);
		
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
		gp.iTile[mapNum][iIndex] = ITileSetup(86, 23, 1, mapNum, map);
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
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 33, 8, mapNum, map);
		
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
		
		mapNum = 38;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 7, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 8, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 6, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 7, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 9, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 6, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 7, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 8, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 5, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 9, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 10, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 12, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 5, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 7, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 8, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(43, 7, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 16, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 22, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(41, 27, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 11, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 15, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 17, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 23, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 26, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 27, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 8, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(61, 5, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(63, 6, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(65, 8, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 6, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 13, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(66, 18, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 26, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(67, 29, 9, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(66, 30, 9, mapNum, map);
		
		mapNum = 41;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(18, 30, 99, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupLockedDoor(44, 30, 100, mapNum, map);
		
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
		gp.iTile[mapNum][iIndex] = ITileSetup(35, 41, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(37, 41, 1, mapNum, map);
		
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
		
		mapNum = 166;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 50, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 50, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 50, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(51, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 51, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 52, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(49, 52, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 53, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(47, 54, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(48, 56, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 56, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 52, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(54, 53, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(55, 53, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 53, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 54, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 54, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(57, 55, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 55, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 56, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(46, 46, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(45, 48, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(42, 46, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 48, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 42, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 42, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 42, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 41, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 41, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 41, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(38, 33, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 31, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(37, 29, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(34, 28, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(36, 26, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(40, 25, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(44, 27, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(50, 45, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(52, 46, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(53, 48, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(56, 47, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(58, 50, 10, mapNum, map);
		gp.iTile[mapNum][iIndex] = ITileSetup(62, 49, 10, mapNum, map);
		
		gp.iTile[mapNum][iIndex] = SetupRockClimb(35, 31, 1, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(43, 30, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(49, 47, 3, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(44, 50, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(41, 57, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(62, 51, 0, 1, mapNum, map);
		gp.iTile[mapNum][iIndex] = SetupRockClimb(60, 58, 3, 1, mapNum, map);
	}

	public void updateNPC(int map) {
		boolean[] flags = gp.player.p.flags;
		boolean[][] flag = gp.player.p.flag;
		
		/**
		 * First split
		 */
		if (flag[0][2]) { // Lab helper blocking exit
			gp.npc[52][1] = null;
		}
		if (!flag[0][4] || flag[0][5]) gp.npc[0][0] = null; // Scott 1
		if (flag[0][4] && !flag[0][5]) gp.npc[0][0] = NPCSetup(SCOTT_UP, 72, 48, "Hey! You must be the Professor's son. I've been waiting to meet you!\n"
				+ "My dad told me about you - he's Avery from Galar, so I guess that makes me... uh, someone important, too!\n"
				+ "I've heard you're already training, but let's see how we match up!\n"
				+ "Uh, no pressure though, right? Oh man, I'm already getting a headache. Let's just get this over with before I stress myself out!",
				"Ugh, I lost! But I'm not going to let it get me down... not much, anyway.\n"
				+ "You're strong, but I knew you would be. Dad always says it's okay to lose as long as you learn, right?\n"
				+ "Anyway, I'll get better. You just wait! Next time... I'm definitely beating you. Probably. Hopefully.", 0);
		
		if (flag[0][5]) {
			gp.npc[3][0] = null;
			gp.npc[3][1] = null;
			if (gp.npc[55][0] == null) gp.npc[55][0] = NPCSetup(AVERY, "Avery", 31, 41, "Oh, what an unexpected visit. Quite pleasant of you to drop by, but Scott isn't here right now. Do feel free to look around."
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
			gp.npc[4][14] = NPCSetup(TN_UP, 44, 68, "Ahh! I told you not to follow me, kid. Now you're going to pay!", "Fine, go have fun in the warehouse. I don't really care anyways.", 359);
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
		
		if (flag[1][3] || flag[1][18]) {
			gp.npc[10][0] = null;
			gp.npc[10][1] = null;
		}
		
		if (flag[1][5]) { // Rocky-E
			gp.npc[14][5] = null;
		}
		if (flag[1][7]) { // Poof-E
			gp.npc[16][7] = null;
		}
		if (flag[1][9] || flag[1][15]) {
			gp.npc[16][8] = null;
		}
		
		if (flag[1][10]) {
			gp.npc[17][6] = null;
			gp.npc[17][7] = null;
		}
		
		if (flag[1][11]) {
			gp.npc[17][0] = null;
			gp.npc[17][1] = null;
			gp.npc[17][2] = null;
			gp.npc[17][3] = null;
			gp.npc[17][4] = null;
			gp.npc[17][5] = null;
			gp.npc[18][0] = null;
			if (!flag[1][13]) gp.npc[18][1] = NPCSetup(SCOTT_UP, "Scott", 54, 40, "Oh, talk about a stroke of luck! Thank goodness you showed up, that thing was really strong.", true);
			gp.npc[18][2] = null;
		}
		if (flag[1][13]) {
			gp.npc[18][1] = null;
		}
		if (flag[1][15]) {
			if (!flag[1][16]) {
				gp.npc[13][1] = NPCSetup(SCOTT_DOWN, "Scott", 14, 58, "Hey there! Stanford said he's so motivated by our help that he'll reopen the gym! Isn't that wonderful?");
				gp.npc[13][2] = NPCSetup(STANFORD, "Stanford", 15, 57, "*grumbles* Yeah, you guys are alright I suppose.", true);
			} else {
				gp.npc[13][1] = null;
				gp.npc[13][2] = null;
			}
		}
		if (flag[1][14]) { // Gyarados-E
			gp.npc[60][2] = null;
		}
		
		/**
		 * Third Split
		 */
		if (flag[1][18]) {
			if (!flag[2][0]) {
				gp.npc[13][4] = NPCSetup(SCOTT_DOWN, 28, 10, "Phew, we did it! Saving the town from that blackout was crazy!\n"
						+ "But now that things have settled down, I think it's time we test our skills again. After all, we're both growing stronger, right?\n"
						+ "Uh, not that I'm worried or anything... Okay, maybe I'm a little nervous.\n"
						+ "But you've been helping me keep it together, so thanks for that!\n"
						+ "Now, let's see who's stronger. Just... go easy on me? My head's already pounding.",
						"Argh, not again! How do you keep getting stronger?\n"
						+ "I thought after saving that whole town together, I'd have caught up to you by now... but I guess not.\n"
						+ "I've got to work harder. There's no way I'm letting you stay ahead of me forever!", 55); // scott 2
			} else {
				gp.npc[13][4] = null;
			}
			if (!flag[2][1]) {
				gp.npc[13][5] = NPCSetup(ALAKAZAM, "Alakazam", 27, 6, "Vxxxvhh...");
				gp.npc[13][6] = NPCSetup(RYDER_DOWN, "Ryder", 26, 6, "Hello my friend! Just the person I was looking for!", true);
			} else {
				gp.npc[13][5] = null;
				gp.npc[13][6] = null;
			}
		} else {
			gp.npc[13][4] = null;
		}
		if (flag[2][1]) {
			if (!flag[2][3]) {
				gp.npc[13][7] = NPCSetup(RYDER_UP, "Ryder", 89, 19, "You shouldn't be seeing this");
				gp.npc[13][8] = NPCSetup(ALAKAZAM, "Alakazam", 90, 19, "Vxxxvhh...");
			} else {
				gp.npc[13][7] = null;
				gp.npc[13][8] = null;
			}
		}
		
		if (flag[2][8] && !flag[2][9]) {
			gp.npc[26][0] = NPCSetup(TN_DOWN, 58, 70, "Hello there.", "", 79);
		} else {
			gp.npc[26][0] = null;
		}
		
		if (flag[2][4]) {
			gp.npc[28][0] = null;
			gp.npc[28][3] = null;
			gp.npc[28][1] = NPCSetup(MILLIE_UP, "Millie", 81, 36, "People say bugs are scary... This is so so much worse...", true);
			gp.npc[28][4] = NPCSetup(FRED_DOWN, 82, 32, "AGHHH!! WHAT THE FUCK IS HAPPENING?!?!??! DIE!!!!!!",
					"Ugh... where am I? What... what happened?\n"
					+ "You... it was you, wasn't it? You saved me. I... don't know what to say. Thanks, I guess.\n"
					+ "I didn't need your help, though. I could've handled it on my own. Next time we meet, I'll be ready, and I won't lose.", 89);
		} else {
			gp.npc[28][4] = null;
		}
		if (flag[2][6]) {
			gp.npc[28][4] = null;
		}
		if (flag[2][8]) {
			gp.npc[28][1] = null;
			gp.npc[28][5] = NPCSetup(MILLIE, "Millie", 82, 32, "*pant* EEEEK! HALT POSSESSED PERSON! I HAVE THE POWER OF GOD AND ANIME ON MY SIDE!!", true);
		}
		if (flag[2][10]) {
			gp.npc[28][5] = null;
		}
		if (flag[2][11]) {
			gp.npc[28][2] = SetupStaticEncounter(284, 82, 22, 387, 76, "Bzzt.");
		}
		if (flag[2][12]) {
			gp.npc[28][2] = null;
			for (int i = 19; i <= 34; i++) {
				gp.npc[28][i] = null;
			}
			gp.npc[28][38] = NPCSetup(MILLIE, "Millie", 87, 45, "You... you really did it! You are a seriously impressive trainer.", true);
		}
		if (flag[2][13]) {
			gp.npc[28][38] = null;
		}
		
		/**
		 * Fourth Split
		 */
		if (flag[3][0]) {
			gp.npc[39][2] = null;
			gp.npc[39][3] = null;
		}
		if (flag[3][1]) {
			gp.npc[38][0] = NPCSetup(EXPLORER_DOWN, "Ice Master", 53, 7, "Ah, I see you've returned. This petticoat gem... I once found it in the frozen north.", true, 106, "The ice teaches patience, but it also teaches strength.");
		}
		if (flag[3][2]) {
			gp.npc[43][0] = NPCSetup(HIKER_DOWN, "Ground Master", 31, 42, "I've been thinking. There's one more thing I can offer you. This valiant gem - found it on one of my most challenging digs.", true, 107, "It's good to see you again. You've got a fighting spirit that I admire.\nYou should come to my classes someday! I'm sure we could teach each other about our journeys.");
		}
		if (flag[3][7] && flag[3][8]) {
			gp.npc[44][0] = null;
		}
		if (flag[3][8]) { // Icy Serpant
			gp.npc[166][0] = null;
		}
		
		/**
		 * Fifth Split
		 */
		if (flag[3][12] && !flag[4][0]) {
			gp.npc[38][1] = NPCSetup(ROBIN_UP, "Robin", 62, 43, "HEY!", true);
		} else {
			gp.npc[38][1] = null;
		}
		
		/**
		 * All of this is old and should be removed/reworked
		 */
		if (flags[15]) gp.npc[0][13] = null;
		if (flags[16]) gp.npc[85][0] = null;
		if (flags[17]) gp.npc[85][7] = null;
		if (!flags[19] && gp.player.p.grustCount >= 10) {
			gp.npc[107][0] = NPCSetup(RICK, 46, 60, "", "", 234);
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
		
		gp.setRenderableNPCs();
	}
	
	public void resetNPCDirection(int map) {
		Entity[] entities = gp.npc[map];
		for (Entity e : entities) {
			if (e != null) {
				e.direction = e.defaultDirection;
			}
		}
	}
	
	private Entity SetupClerk(int type, int x, int y, Item... items) {
		Entity e = NPCSetup(type, x, y, "", "", -1);
		e.setItems(items);
		return e;
	}
	
	private Entity NPCSetup(int type, int x, int y, String dialogue, String alt, int team, int spin) {
		Entity e = NPCSetup(type, x, y, dialogue, alt, team);
		e.setSpin(spin);
		return e;
	}

	private Entity NPCSetup(int type, int x, int y, String dialogue, String alt, int team) {
		Entity result = null;
		
		String messages[] = dialogue.split("\n");
		for (int i = 0; i < messages.length; i++) {
			messages[i] = Item.breakString(messages[i], 42);
		}
		
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
			result = new NPC_Trainer(gp, "down", team, messages);
			break;
		case TRAINER_UP:
			result = new NPC_Trainer(gp, "up", team, messages);
			break;
		case TRAINER_LEFT:
			result = new NPC_Trainer(gp, "left", team, messages);
			break;
		case TRAINER_RIGHT:
			result = new NPC_Trainer(gp, "right", team, messages);
			break;
		case NPC_NURSE_FULL:
			result = new NPC_Nurse(gp, "up");
			break;
		case ROBIN:
			result = new GL_Robin(gp, "down", team, messages);
			break;
		case STANFORD:
			result = new GL_Stanford(gp, "down", team, messages);
			break;
		case MILLIE:
			result = new GL_Millie(gp, "down", team, messages);
			break;
		case GLACIUS:
			result = new GL_Glacius(gp, "down", team, messages);
			break;
		case MINDY:
			result = new GL_Mindy(gp, "down", team, messages);
			break;
		case RAYNA:
			result = new GL_Rayna(gp, "down", team, messages);
			break;
		case MERLIN:
			result = new GL_Merlin(gp, "down", team, messages);
			break;
		case NOVA:
			result = new GL_Nova(gp, "down", team, messages);
			break;
		case SCOTT_DOWN:
			result = new T_Scott(gp, "down", team, messages); // scott down
			break;
		case SCOTT_UP:
			result = new T_Scott(gp, "up", team, messages); // scott up
			break;
		case TN_DOWN:
			result = new T_TN(gp, "down", team, messages);
			break;
		case TN_UP:
			result = new T_TN(gp, "up", team, messages);
			break;
		case TN_LEFT:
			result = new T_TN(gp, "left", team, messages);
			break;
		case TN_RIGHT:
			result = new T_TN(gp, "right", team, messages);
			break;
		case FRED_DOWN:
			result = new T_Fred(gp, "down", team, messages); // fred down
			break;
		case FRED_UP:
			result = new T_Fred(gp, "up", team, messages); // fred up
			break;
		case NPC_MARKET:
			result = new NPC_Market(gp);
			break;
		case GRUST:
			result = new NPC_Pokemon(gp, 159, team, true, -1, messages);
			gp.grusts[index - 1] = ((NPC_Pokemon) result);
			break;
		case RICK:
			result = new GL_Rick(gp, "down", team, messages);
			break;
		case MAXWELL:
			result = new GL_Maxwell(gp, "down", team, messages);
			break;
		case INVIS_DOWN:
			result = new T_Invisible(gp, "down", team, messages);
			break;
		case INVIS_UP:
			result = new T_Invisible(gp, "up", team, messages);
			break;
		case INVIS_LEFT:
			result = new T_Invisible(gp, "left", team, messages);
			break;
		case INVIS_RIGHT:
			result = new T_Invisible(gp, "right", team, messages);
			break;
		case ACTOR_DOWN:
			result = new T_Actor(gp, "down", team, messages);
			break;
		case ACTOR_UP:
			result = new T_Actor(gp, "up", team, messages);
			break;
		case ACTOR_LEFT:
			result = new T_Actor(gp, "left", team, messages);
			break;
		case ACTOR_RIGHT:
			result = new T_Actor(gp, "right", team, messages);
			break;
		case ACTRESS_DOWN:
			result = new T_Actress(gp, "down", team, messages);
			break;
		case ACTRESS_UP:
			result = new T_Actress(gp, "up", team, messages);
			break;
		case ACTRESS_LEFT:
			result = new T_Actress(gp, "left", team, messages);
			break;
		case ACTRESS_RIGHT:
			result = new T_Actress(gp, "right", team, messages);
			break;
		case ATHLETE_DOWN:
			result = new T_Athlete(gp, "down", team, messages);
			break;
		case ATHLETE_UP:
			result = new T_Athlete(gp, "up", team, messages);
			break;
		case ATHLETE_LEFT:
			result = new T_Athlete(gp, "left", team, messages);
			break;
		case ATHLETE_RIGHT:
			result = new T_Athlete(gp, "right", team, messages);
			break;
		case BIRD_KEEPER_DOWN:
			result = new T_BirdKeeper(gp, "down", team, messages);
			break;
		case BIRD_KEEPER_UP:
			result = new T_BirdKeeper(gp, "up", team, messages);
			break;
		case BIRD_KEEPER_LEFT:
			result = new T_BirdKeeper(gp, "left", team, messages);
			break;
		case BIRD_KEEPER_RIGHT:
			result = new T_BirdKeeper(gp, "right", team, messages);
			break;
		case BLACK_BELT_DOWN:
			result = new T_BlackBelt(gp, "down", team, messages);
			break;
		case BLACK_BELT_UP:
			result = new T_BlackBelt(gp, "up", team, messages);
			break;
		case BLACK_BELT_LEFT:
			result = new T_BlackBelt(gp, "left", team, messages);
			break;
		case BLACK_BELT_RIGHT:
			result = new T_BlackBelt(gp, "right", team, messages);
			break;
		case BUG_CATCHER_DOWN:
			result = new T_BugCatcher(gp, "down", team, messages);
			break;
		case BUG_CATCHER_UP:
			result = new T_BugCatcher(gp, "up", team, messages);
			break;
		case BUG_CATCHER_LEFT:
			result = new T_BugCatcher(gp, "left", team, messages);
			break;
		case BUG_CATCHER_RIGHT:
			result = new T_BugCatcher(gp, "right", team, messages);
			break;
		case BURGLAR_DOWN:
			result = new T_Burglar(gp, "down", team, messages);
			break;
		case BURGLAR_UP:
			result = new T_Burglar(gp, "up", team, messages);
			break;
		case BURGLAR_LEFT:
			result = new T_Burglar(gp, "left", team, messages);
			break;
		case BURGLAR_RIGHT:
			result = new T_Burglar(gp, "right", team, messages);
			break;
		case EXPLORER_DOWN:
			result = new T_Explorer(gp, "down", team, messages);
			break;
		case EXPLORER_UP:
			result = new T_Explorer(gp, "up", team, messages);
			break;
		case EXPLORER_LEFT:
			result = new T_Explorer(gp, "left", team, messages);
			break;
		case EXPLORER_RIGHT:
			result = new T_Explorer(gp, "right", team, messages);
			break;
		case FISHERMAN_DOWN:
			result = new T_Fisherman(gp, "down", team, messages);
			break;
		case FISHERMAN_UP:
			result = new T_Fisherman(gp, "up", team, messages);
			break;
		case FISHERMAN_LEFT:
			result = new T_Fisherman(gp, "left", team, messages);
			break;
		case FISHERMAN_RIGHT:
			result = new T_Fisherman(gp, "right", team, messages);
			break;
		case GENTLEMAN_DOWN:
			result = new T_Gentleman(gp, "down", team, messages);
			break;
		case GENTLEMAN_UP:
			result = new T_Gentleman(gp, "up", team, messages);
			break;
		case GENTLEMAN_LEFT:
			result = new T_Gentleman(gp, "left", team, messages);
			break;
		case GENTLEMAN_RIGHT:
			result = new T_Gentleman(gp, "right", team, messages);
			break;
		case HIKER_DOWN:
			result = new T_Hiker(gp, "down", team, messages);
			break;
		case HIKER_UP:
			result = new T_Hiker(gp, "up", team, messages);
			break;
		case HIKER_LEFT:
			result = new T_Hiker(gp, "left", team, messages);
			break;
		case HIKER_RIGHT:
			result = new T_Hiker(gp, "right", team, messages);
			break;
		case LADY_DOWN:
			result = new T_Lady(gp, "down", team, messages);
			break;
		case LADY_UP:
			result = new T_Lady(gp, "up", team, messages);
			break;
		case LADY_LEFT:
			result = new T_Lady(gp, "left", team, messages);
			break;
		case LADY_RIGHT:
			result = new T_Lady(gp, "right", team, messages);
			break;
		case MAGICIAN_F_DOWN:
			result = new T_MagicianF(gp, "down", team, messages);
			break;
		case MAGICIAN_F_UP:
			result = new T_MagicianF(gp, "up", team, messages);
			break;
		case MAGICIAN_F_LEFT:
			result = new T_MagicianF(gp, "left", team, messages);
			break;
		case MAGICIAN_F_RIGHT:
			result = new T_MagicianF(gp, "right", team, messages);
			break;
		case MAGICIAN_M_DOWN:
			result = new T_MagicianM(gp, "down", team, messages);
			break;
		case MAGICIAN_M_UP:
			result = new T_MagicianM(gp, "up", team, messages);
			break;
		case MAGICIAN_M_LEFT:
			result = new T_MagicianM(gp, "left", team, messages);
			break;
		case MAGICIAN_M_RIGHT:
			result = new T_MagicianM(gp, "right", team, messages);
			break;
		case MANIAC_DOWN:
			result = new T_Maniac(gp, "down", team, messages);
			break;
		case MANIAC_UP:
			result = new T_Maniac(gp, "up", team, messages);
			break;
		case MANIAC_LEFT:
			result = new T_Maniac(gp, "left", team, messages);
			break;
		case MANIAC_RIGHT:
			result = new T_Maniac(gp, "right", team, messages);
			break;
		case PICKNICKER_DOWN:
			result = new T_Picknicker(gp, "down", team, messages);
			break;
		case PICKNICKER_UP:
			result = new T_Picknicker(gp, "up", team, messages);
			break;
		case PICKNICKER_LEFT:
			result = new T_Picknicker(gp, "left", team, messages);
			break;
		case PICKNICKER_RIGHT:
			result = new T_Picknicker(gp, "right", team, messages);
			break;
		case POSTMAN_DOWN:
			result = new T_Postman(gp, "down", team, messages);
			break;
		case POSTMAN_UP:
			result = new T_Postman(gp, "up", team, messages);
			break;
		case POSTMAN_LEFT:
			result = new T_Postman(gp, "left", team, messages);
			break;
		case POSTMAN_RIGHT:
			result = new T_Postman(gp, "right", team, messages);
			break;
		case PSYCHIC_DOWN:
			result = new T_Psychic(gp, "down", team, messages);
			break;
		case PSYCHIC_UP:
			result = new T_Psychic(gp, "up", team, messages);
			break;
		case PSYCHIC_LEFT:
			result = new T_Psychic(gp, "left", team, messages);
			break;
		case PSYCHIC_RIGHT:
			result = new T_Psychic(gp, "right", team, messages);
			break;
		case STUDENT_F_DOWN:
			result = new T_StudentF(gp, "down", team, messages);
			break;
		case STUDENT_F_UP:
			result = new T_StudentF(gp, "up", team, messages);
			break;
		case STUDENT_F_LEFT:
			result = new T_StudentF(gp, "left", team, messages);
			break;
		case STUDENT_F_RIGHT:
			result = new T_StudentF(gp, "right", team, messages);
			break;
		case STUDENT_M_DOWN:
			result = new T_StudentM(gp, "down", team, messages);
			break;
		case STUDENT_M_UP:
			result = new T_StudentM(gp, "up", team, messages);
			break;
		case STUDENT_M_LEFT:
			result = new T_StudentM(gp, "left", team, messages);
			break;
		case STUDENT_M_RIGHT:
			result = new T_StudentM(gp, "right", team, messages);
			break;
		case SWIMMER_F_DOWN:
			result = new T_SwimmerF(gp, "down", team, messages);
			break;
		case SWIMMER_F_UP:
			result = new T_SwimmerF(gp, "up", team, messages);
			break;
		case SWIMMER_F_LEFT:
			result = new T_SwimmerF(gp, "left", team, messages);
			break;
		case SWIMMER_F_RIGHT:
			result = new T_SwimmerF(gp, "right", team, messages);
			break;
		case SWIMMER_M_DOWN:
			result = new T_SwimmerM(gp, "down", team, messages);
			break;
		case SWIMMER_M_UP:
			result = new T_SwimmerM(gp, "up", team, messages);
			break;
		case SWIMMER_M_LEFT:
			result = new T_SwimmerM(gp, "left", team, messages);
			break;
		case SWIMMER_M_RIGHT:
			result = new T_SwimmerM(gp, "right", team, messages);
			break;
		case YOUNGSTER_DOWN:
			result = new T_Youngster(gp, "down", team, messages);
			break;
		case YOUNGSTER_UP:
			result = new T_Youngster(gp, "up", team, messages);
			break;
		case YOUNGSTER_LEFT:
			result = new T_Youngster(gp, "left", team, messages);
			break;
		case YOUNGSTER_RIGHT:
			result = new T_Youngster(gp, "right", team, messages);
			break;
		case ACE_TRAINER_F_DOWN:
			result = new T_AceTrainerF(gp, "down", team, messages);
			break;
		case ACE_TRAINER_F_UP:
			result = new T_AceTrainerF(gp, "up", team, messages);
			break;
		case ACE_TRAINER_F_LEFT:
			result = new T_AceTrainerF(gp, "left", team, messages);
			break;
		case ACE_TRAINER_F_RIGHT:
			result = new T_AceTrainerF(gp, "right", team, messages);
			break;
		case ACE_TRAINER_M_DOWN:
		case ASTRONOMER_DOWN:
			result = new T_AceTrainerM(gp, "down", team, messages);
			break;
		case ACE_TRAINER_M_UP:
		case ASTRONOMER_UP:
			result = new T_AceTrainerM(gp, "up", team, messages);
			break;
		case ACE_TRAINER_M_LEFT:
		case ASTRONOMER_LEFT:
			result = new T_AceTrainerM(gp, "left", team, messages);
			break;
		case ACE_TRAINER_M_RIGHT:
		case ASTRONOMER_RIGHT:
			result = new T_AceTrainerM(gp, "right", team, messages);
			break;
		case NPC_PC_GAUNTLET:
			result = new NPC_PC(gp, true);
			result.down1 = result.setup("/npc/gauntletbox");
			break;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		result.altDialogue = alt;
		
		index++;
		
		return result;
	}
	
	private Entity NPCSetup(int type, String name, int x, int y, String message) {
		return NPCSetup(type, name, x, y, message, -1);
	}
	
	private Entity NPCSetup(int type, String name, int x, int y, String message, int flag) {
		return NPCSetup(type, name, x, y, message, false, flag, null);
	}
	
	private Entity NPCSetup(int type, String name, int x, int y, String message, boolean a) {
		return NPCSetup(type, name, x, y, message, a, -1, null);
	}
	
	private Entity NPCSetup(int type, String name, int x, int y, String message, boolean a, int flag, String altDialogue) {
		String messages[] = message.split("\n");
		for (int i = 0; i < messages.length; i++) {
			messages[i] = Item.breakString(messages[i], 42);
		}
		Entity result = new NPC_Block(gp, name, messages, a, flag, Item.breakString(altDialogue, 44));
		
		BufferedImage image = null;
		
		switch (type) {
			case BLOCK_DOWN:
			default:
				result.setupImages("/npc/block");
				break;
			case BLOCK_UP:
				result.setupImages("/npc/block");
				result.setDirection("up");
				break;
			case BLOCK_LEFT:
				result.setupImages("/npc/block");
				result.setDirection("left");
				break;
			case BLOCK_RIGHT:
				result.setupImages("/npc/block");
				result.setDirection("right");
				break;
			case PROFESSOR:
				result.setupImages("/npc/professor");
				break;
			case GRANDMOTHER:
				result.setupImages("/npc/grandma");
				break;
			case AVERY:
				result.setupImages("/npc/avery");
				break;
			case KLARA:
				result.setupImages("/npc/klara");
				break;
			case ROBIN:
				result.setupImages("/npc/robin");
				break;
			case ROBIN_UP:
				result.setupImages("/npc/robin");
				result.setDirection("up");
				break;
			case STANFORD:
				result.setupImages("/npc/stanford");
				break;
			case MILLIE:
				result.setupImages("/npc/millie");
				break;
			case MILLIE_UP:
				result.setupImages("/npc/millie");
				result.setDirection("up");
				break;
			case TN_DOWN:
				result.setupImages("/npc/tn");
				break;
			case ALAKAZAM:
				result.setupImages("/npc/alakazam");
				break;
			case SCOTT_DOWN:
				result.setupImages("/npc/scott");
				result.setDirection("down");
				break;
			case SCOTT_UP:
				result.setupImages("/npc/scott");
				result.setDirection("up");
				break;
			case FRED_DOWN:
				result.setupImages("/npc/fred");
				result.setDirection("down");
				break;
			case FRED_UP:
				result.setupImages("/npc/fred");
				result.setDirection("down");
				break;
			case UP_XURKITREE:
				image = result.setup("/overworlds/284_0");
				break;
			case EXPLORER_DOWN:
				result.setupImages("/npc/explorer");
				result.setDirection("down");
				break;
			case HIKER_DOWN:
				result.setupImages("/npc/hiker");
				result.setDirection("down");
				break;
			case FISHERMAN_DOWN:
				result.setupImages("/npc/fisherman");
				result.setDirection("down");
				break;
			case CHEF:
				result.setupImages("/npc/chef");
				result.setDirection("down");
				break;
			case LADY_DOWN:
				result.setupImages("/npc/lady");
				result.setDirection("down");
				break;
				
		}
		
		if (image != null) result.down1 = image;
		
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
		
		itemMap.put(item, itemMap.getOrDefault(item, 0) + 1);
		
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
	
	private ItemObj StatBerrySetup(int x, int y, int mapNum, int lower, int upper) {
		if (gp.player.p.itemsCollected[mapNum][objIndex] == true || gp.player.p.statBerries == null) {
			objIndex++;
			sBerryIndex++;
			return null;
		}
		
		return ObjSetup(x, y, gp.player.p.statBerries[sBerryIndex++], mapNum, lower, upper);
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
			result.setDirection("up");
			break;
		case 9:
			result = new Snowball(gp);
			break;
		case 10:
			result = new IceBlock(gp);
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
		
		if (gp.player.p.flag[result.getFlagX()][result.getFlagY()]) {
			iIndex++;
			return null;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	private InteractiveTile SetupBarrier(int x, int y, int flag, int mapNum, int map) {
		if (mapNum != map) return null;
		InteractiveTile result = null;
		result = new GymBarrier(gp, flag);
		
		if (gp.player.p.flag[result.getFlagX()][result.getFlagY()]) {
			iIndex++;
			return null;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
	
	private NPC_Pokemon SetupStaticEncounter(int id, int x, int y, int t, int flag, String message) {
		
		String messages[] = message.split("\n");
		for (int i = 0; i < messages.length; i++) {
			messages[i] = Item.breakString(messages[i], 42);
		}
		
		NPC_Pokemon result = new NPC_Pokemon(gp, id, t, id == 159, flag, messages);
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