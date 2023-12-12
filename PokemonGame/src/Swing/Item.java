package Swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Swing.Battle.JGradientButton;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import Overworld.Main;

public enum Item {
	REPEL(0,25,new Color(0, 92, 5),Item.OTHER,null,"Prevents wild Pokemon encounters\nfor 200 steps"),
	POKEBALL(1,10,new Color(176, 0, 12),Item.OTHER,null,"A standard device for capturing\nwild Pokemon"),
	GREAT_BALL(2,25,new Color(0, 0, 148),Item.OTHER,null,"An upgraded device for capturing\nwild Pokemon"),
	ULTRA_BALL(3,50,new Color(148, 171, 0),Item.OTHER,null,"A very efficient device for\ncapturing wild Pokemon"),
	POTION(4,50,new Color(124, 0, 219),Item.OTHER,null,"Restores 20 HP"),
	SUPER_POTION(5,125,new Color(140, 24, 8),Item.OTHER,null,"Restores 60 HP"),
	HYPER_POTION(6,275,new Color(255, 0, 191),Item.OTHER,null,"Restores 200 HP"),
	MAX_POTION(7,400,new Color(0, 21, 255),Item.OTHER,null,"Restores a Pokemon's HP to full"),
	FULL_RESTORE(8,500,new Color(255, 196, 0),Item.OTHER,null,"Restores a Pokemon's HP to full\nand cures any status conditions"),
	ANTIDOTE(9,25,new Color(157, 0, 255),Item.OTHER,null,"Cures a Pokemon of Poison"),
	AWAKENING(10,25,new Color(63, 83, 92),Item.OTHER,null,"Cures a Pokemon of Sleep"),
	BURN_HEAL(11,25,new Color(133, 15, 19),Item.OTHER,null,"Cures a Pokemon of Burn"),
	PARALYZE_HEAL(12,25,new Color(176, 158, 0),Item.OTHER,null,"Cures a Pokemon of Paralysis"),
	FREEZE_HEAL(13,25,new Color(0, 170, 189),Item.OTHER,null,"Cures a Pokemon of Frostbite"),
	FULL_HEAL(14,150,new Color(255, 247, 0),Item.OTHER,null,"Cures a Pokemon of any status\ncondition"),
	RAGE_CANDY_BAR(15,50,new Color(0, 55, 255),Item.OTHER,null,"Cures a Pokemon of any status\ncondition"),
	REVIVE(16,500,new Color(219, 194, 0),Item.OTHER,null,"Recovers a Pokemon from fainting\nwith 50% HP"),
	MAX_REVIVE(17,1500,new Color(219, 194, 0),Item.OTHER,null,"Recovers a Pokemon from fainting\nwith full HP"),
	RARE_CANDY(18,0,new Color(124, 54, 255),Item.OTHER,null,"Elevates a Pokemon by 1 level"),
	EUPHORIAN_GEM(19,500,new Color(138, 237, 255),Item.OTHER,null,"Grants a Pokemon 100 friendship\npoints"),
	LEAF_STONE(20,1000,new Color(0, 120, 20),Item.OTHER,null,"Evolves a certain species of\nPokemon"),
	DUSK_STONE(21,1000,new Color(64, 64, 64),Item.OTHER,null,"Evolves a certain species of\nPokemon"),
	DAWN_STONE(22,1000,new Color(0, 176, 179),Item.OTHER,null,"Evolves a certain species of\nPokemon"),
	ICE_STONE(23,1000,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of\nPokemon"),
	VALIANT_GEM(24,2000,new Color(72, 75, 219),Item.OTHER,null,"Grants Masculine energy to\na Pokemon, evolving them\ninto their male evolution"),
	PETTICOAT_GEM(25,2000,new Color(204, 61, 140),Item.OTHER,null,"Grants Feminine energy to\na Pokemon, evolving them\ninto their female evolution"),
	ABILITY_CAPSULE(26,0,new Color(102, 7, 143),Item.OTHER,null,"Swaps a Pokemon's ability\nwith its other possible\nability"),
	BOTTLE_CAP(27,1000,new Color(192, 192, 192),Item.OTHER,null,"Maxes out an IV of choosing"),
	GOLD_BOTTLE_CAP(28,0,new Color(255, 215, 0),Item.OTHER,null,"Maxes out all IVs of a\nPokemon"),
	ADAMANT_MINT(29,2500,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Atk, -SpA"),
	BOLD_MINT(30,1750,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Def, -Atk"),
	BRAVE_MINT(31,1000,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Atk, -Spe"),
	CALM_MINT(32,1500,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +SpD, -Atk"),
	CAREFUL_MINT(33,1250,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +SpD, -SpA"),
	IMPISH_MINT(34,1750,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Def, -SpA"),
	JOLLY_MINT(35,2500,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Spe, -SpA"),
	MODEST_MINT(36,2250,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +SpA, -Atk"),
	QUIET_MINT(37,1000,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +SpA, -Spe"),
	SERIOUS_MINT(38,1250,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to Neutral"),
	TIMID_MINT(39,2500,new Color(113, 84, 255),Item.OTHER,null,"Changes a Pokemon's nature\n to +Spe, -Atk"),
	ELIXIR(40,300,new Color(230, 146, 78),Item.OTHER,null,"Restores PP of a selected\nmove"),
	MAX_ELIXIR(41,250,new Color(246, 255, 120),Item.OTHER,null,"Restores PP of all moves\non a Pokemon"),
	PP_UP(42,2500,new Color(150, 51, 156),Item.OTHER,null,"Increases max PP of a\nselected move by 20%"),
	PP_MAX(43,0,new Color(142, 230, 21),Item.OTHER,null,"Increases max PP of a\nselected move by its\nmax PP, which is 160%"),
	FLAME_ORB(44,0,new Color(232, 52, 54),Item.OTHER,null,"Burns a selected Pokemon"),
	THUNDER_SCALES_FOSSIL(45,0,new Color(201, 169, 81),Item.OTHER,null,"A fossil of an ancient Pokemon\nthat lived in a desert.\nIt appears to have an\nelectric charge ridden\nin the scales."),
	DUSK_SCALES_FOSSIL(46,0,new Color(45, 47, 51),Item.OTHER,null,"A fossil of an ancient Pokemon\nthat lived in a forest.\nIt appears to give off\na dark energy within\n the scales."),
	NULL47(47,0,Color.BLACK,Item.OTHER,null,""),
	NULL48(48,0,Color.BLACK,Item.OTHER,null,""),
	NULL49(49,0,Color.BLACK,Item.OTHER,null,""),
	NULL50(50,0,Color.BLACK,Item.OTHER,null,""),
	NULL51(51,0,Color.BLACK,Item.OTHER,null,""),
	NULL52(52,0,Color.BLACK,Item.OTHER,null,""),
	NULL53(53,0,Color.BLACK,Item.OTHER,null,""),
	NULL54(54,0,Color.BLACK,Item.OTHER,null,""),
	NULL55(55,0,Color.BLACK,Item.OTHER,null,""),
	NULL56(56,0,Color.BLACK,Item.OTHER,null,""),
	NULL57(57,0,Color.BLACK,Item.OTHER,null,""),
	NULL58(58,0,Color.BLACK,Item.OTHER,null,""),
	NULL59(59,0,Color.BLACK,Item.OTHER,null,""),
	NULL60(60,0,Color.BLACK,Item.OTHER,null,""),
	NULL61(61,0,Color.BLACK,Item.OTHER,null,""),
	NULL62(62,0,Color.BLACK,Item.OTHER,null,""),
	NULL63(63,0,Color.BLACK,Item.OTHER,null,""),
	NULL64(64,0,Color.BLACK,Item.OTHER,null,""),
	NULL65(65,0,Color.BLACK,Item.OTHER,null,""),
	NULL66(66,0,Color.BLACK,Item.OTHER,null,""),
	NULL67(67,0,Color.BLACK,Item.OTHER,null,""),
	NULL68(68,0,Color.BLACK,Item.OTHER,null,""),
	NULL69(69,0,Color.BLACK,Item.OTHER,null,""),
	NULL70(70,0,Color.BLACK,Item.OTHER,null,""),
	NULL71(71,0,Color.BLACK,Item.OTHER,null,""),
	NULL72(72,0,Color.BLACK,Item.OTHER,null,""),
	NULL73(73,0,Color.BLACK,Item.OTHER,null,""),
	NULL74(74,0,Color.BLACK,Item.OTHER,null,""),
	NULL75(75,0,Color.BLACK,Item.OTHER,null,""),
	NULL76(76,0,Color.BLACK,Item.OTHER,null,""),
	NULL77(77,0,Color.BLACK,Item.OTHER,null,""),
	NULL78(78,0,Color.BLACK,Item.OTHER,null,""),
	NULL79(79,0,Color.BLACK,Item.OTHER,null,""),
	NULL80(80,0,Color.BLACK,Item.OTHER,null,""),
	NULL81(81,0,Color.BLACK,Item.OTHER,null,""),
	NULL82(82,0,Color.BLACK,Item.OTHER,null,""),
	NULL83(83,0,Color.BLACK,Item.OTHER,null,""),
	NULL84(84,0,Color.BLACK,Item.OTHER,null,""),
	NULL85(85,0,Color.BLACK,Item.OTHER,null,""),
	NULL86(86,0,Color.BLACK,Item.OTHER,null,""),
	NULL87(87,0,Color.BLACK,Item.OTHER,null,""),
	NULL88(88,0,Color.BLACK,Item.OTHER,null,""),
	NULL89(89,0,Color.BLACK,Item.OTHER,null,""),
	NULL90(90,0,Color.BLACK,Item.OTHER,null,""),
	NULL91(91,0,Color.BLACK,Item.OTHER,null,""),
	NULL92(92,0,Color.BLACK,Item.OTHER,null,""),
	HM01(93,0,Color.BLACK,Item.OTHER,Move.CUT,"Teaches a Pokemon this move."),
	HM02(94,0,Color.BLACK,Item.OTHER,Move.ROCK_SMASH,"Teaches a Pokemon this move."),
	HM03(95,0,Color.BLACK,Item.OTHER,Move.VINE_CROSS,"Teaches a Pokemon this move."),
	HM04(96,0,Color.BLACK,Item.OTHER,Move.SURF,"Teaches a Pokemon this move."),
	HM05(97,0,Color.BLACK,Item.OTHER,Move.SLOW_FALL,"Teaches a Pokemon this move."),
	HM06(98,0,Color.BLACK,Item.OTHER,Move.WHIRLPOOL,"Teaches a Pokemon this move."),
	HM07(99,0,Color.BLACK,Item.OTHER,Move.ROCK_CLIMB,"Teaches a Pokemon this move."),
	HM08(100,0,Color.BLACK,Item.OTHER,Move.LAVA_SURF,"Teaches a Pokemon this move."),
	TM01(101,0,Color.BLACK,Item.OTHER,Move.SUPER_FANG,"Teaches a Pokemon this move."),
	TM02(102,0,Color.BLACK,Item.OTHER,Move.DRAGON_CLAW,"Teaches a Pokemon this move."),
	TM03(103,0,Color.BLACK,Item.OTHER,Move.ELEMENTAL_SPARKLE,"Teaches a Pokemon this move."),
	TM04(104,0,Color.BLACK,Item.OTHER,Move.CALM_MIND,"Teaches a Pokemon this move."),
	TM05(105,0,Color.BLACK,Item.OTHER,Move.BODY_SLAM,"Teaches a Pokemon this move."),
	TM06(106,0,Color.BLACK,Item.OTHER,Move.SHADOW_BALL,"Teaches a Pokemon this move."),
	TM07(107,0,Color.BLACK,Item.OTHER,Move.FOCUS_BLAST,"Teaches a Pokemon this move."),
	TM08(108,0,Color.BLACK,Item.OTHER,Move.BULK_UP,"Teaches a Pokemon this move."),
	TM09(109,0,Color.BLACK,Item.OTHER,Move.LEAF_BLADE,"Teaches a Pokemon this move."),
	TM10(110,0,Color.BLACK,Item.OTHER,Move.ICE_BEAM,"Teaches a Pokemon this move."),
	TM11(111,0,Color.BLACK,Item.OTHER,Move.PSYSHOCK,"Teaches a Pokemon this move."),
	TM12(112,750,Color.BLACK,Item.OTHER,Move.PROTECT,"Teaches a Pokemon this move."),
	TM13(113,5000,Color.BLACK,Item.OTHER,Move.BATON_PASS,"Teaches a Pokemon this move."),
	TM14(114,0,Color.BLACK,Item.OTHER,Move.TAUNT,"Teaches a Pokemon this move."),
	TM15(115,1000,Color.BLACK,Item.OTHER,Move.GIGA_IMPACT,"Teaches a Pokemon this move."),
	TM16(116,1000,Color.BLACK,Item.OTHER,Move.HYPER_BEAM,"Teaches a Pokemon this move."),
	TM17(117,0,Color.BLACK,Item.OTHER,Move.SOLAR_BEAM,"Teaches a Pokemon this move."),
	TM18(118,0,Color.BLACK,Item.OTHER,Move.IRON_HEAD,"Teaches a Pokemon this move."),
	TM19(119,0,Color.BLACK,Item.OTHER,Move.PHOTON_GEYSER,"Teaches a Pokemon this move."),
	TM20(120,0,Color.BLACK,Item.OTHER,Move.EARTHQUAKE,"Teaches a Pokemon this move."),
	TM21(121,0,Color.BLACK,Item.OTHER,Move.THROAT_CHOP,"Teaches a Pokemon this move."),
	TM22(122,0,Color.BLACK,Item.OTHER,Move.FELL_STINGER,"Teaches a Pokemon this move."),
	TM23(123,800,Color.BLACK,Item.OTHER,Move.WEATHER_BALL,"Teaches a Pokemon this move."),
	TM24(124,900,Color.BLACK,Item.OTHER,Move.TERRAIN_PULSE,"Teaches a Pokemon this move."),
	TM25(125,0,Color.BLACK,Item.OTHER,Move.THUNDERBOLT,"Teaches a Pokemon this move."),
	TM26(126,0,Color.BLACK,Item.OTHER,Move.HIDDEN_POWER,"Teaches a Pokemon this move."),
	TM27(127,0,Color.BLACK,Item.OTHER,Move.DRAIN_PUNCH,"Teaches a Pokemon this move."),
	TM28(128,0,Color.BLACK,Item.OTHER,Move.FLAME_CHARGE,"Teaches a Pokemon this move."),
	TM29(129,0,Color.BLACK,Item.OTHER,Move.LIQUIDATION,"Teaches a Pokemon this move."),
	TM30(130,0,Color.BLACK,Item.OTHER,Move.U$TURN,"Teaches a Pokemon this move."),
	TM31(131,0,Color.BLACK,Item.OTHER,Move.FALSE_SWIPE,"Teaches a Pokemon this move."),
	TM32(132,0,Color.BLACK,Item.OTHER,Move.ZING_ZAP,"Teaches a Pokemon this move."),
	TM33(133,0,Color.BLACK,Item.OTHER,Move.PSYCHIC_FANGS,"Teaches a Pokemon this move."),
	TM34(134,0,Color.BLACK,Item.OTHER,Move.MAGIC_TOMB,"Teaches a Pokemon this move."),
	TM35(135,0,Color.BLACK,Item.OTHER,Move.FLAMETHROWER,"Teaches a Pokemon this move."),
	TM36(136,0,Color.BLACK,Item.OTHER,Move.SLUDGE_BOMB,"Teaches a Pokemon this move."),
	TM37(137,0,Color.BLACK,Item.OTHER,Move.ROCK_TOMB,"Teaches a Pokemon this move."),
	TM38(138,1500,Color.BLACK,Item.OTHER,Move.BLIZZARD,"Teaches a Pokemon this move."),
	TM39(139,0,Color.BLACK,Item.OTHER,Move.PSYCHIC,"Teaches a Pokemon this move."),
	TM40(140,0,Color.BLACK,Item.OTHER,Move.FACADE,"Teaches a Pokemon this move."),
	TM41(141,1000,Color.BLACK,Item.OTHER,Move.REFLECT,"Teaches a Pokemon this move."),
	TM42(142,1000,Color.BLACK,Item.OTHER,Move.LIGHT_SCREEN,"Teaches a Pokemon this move."),
	TM43(143,1000,Color.BLACK,Item.OTHER,Move.DAZZLING_GLEAM,"Teaches a Pokemon this move."),
	TM44(144,1000,Color.BLACK,Item.OTHER,Move.PLAY_ROUGH,"Teaches a Pokemon this move."),
	TM45(145,1000,Color.BLACK,Item.OTHER,Move.WILL$O$WISP,"Teaches a Pokemon this move."),
	TM46(146,1500,Color.BLACK,Item.OTHER,Move.FIRE_BLAST,"Teaches a Pokemon this move."),
	TM47(147,0,Color.BLACK,Item.OTHER,Move.STAR_STORM,"Teaches a Pokemon this move."),
	TM48(148,0,Color.BLACK,Item.OTHER,Move.SCALD,"Teaches a Pokemon this move."),
	TM49(149,1500,Color.BLACK,Item.OTHER,Move.REST,"Teaches a Pokemon this move."),
	TM50(150,1250,Color.BLACK,Item.OTHER,Move.TOXIC,"Teaches a Pokemon this move."),
	TM51(151,400,Color.BLACK,Item.OTHER,Move.SLEEP_TALK,"Teaches a Pokemon this move."),
	TM52(152,0,Color.BLACK,Item.OTHER,Move.AERIAL_ACE,"Teaches a Pokemon this move."),
	TM53(153,0,Color.BLACK,Item.OTHER,Move.VOLT_SWITCH,"Teaches a Pokemon this move."),
	TM54(154,750,Color.BLACK,Item.OTHER,Move.THUNDER_WAVE,"Teaches a Pokemon this move."),
	TM55(155,0,Color.BLACK,Item.OTHER,Move.MAGIC_BLAST,"Teaches a Pokemon this move."),
	TM56(156,0,Color.BLACK,Item.OTHER,Move.SPARKLE_STRIKE,"Teaches a Pokemon this move."),
	TM57(157,0,Color.BLACK,Item.OTHER,Move.CHARGE_BEAM,"Teaches a Pokemon this move."),
	TM58(158,0,Color.BLACK,Item.OTHER,Move.DRAGON_PULSE,"Teaches a Pokemon this move."),
	TM59(159,0,Color.BLACK,Item.OTHER,Move.BRICK_BREAK,"Teaches a Pokemon this move."),
	TM60(160,0,Color.BLACK,Item.OTHER,Move.FREEZE$DRY,"Teaches a Pokemon this move."),
	TM61(161,0,Color.BLACK,Item.OTHER,Move.SMACK_DOWN,"Teaches a Pokemon this move."),
	TM62(162,0,Color.BLACK,Item.OTHER,Move.BUG_BUZZ,"Teaches a Pokemon this move."),
	TM63(163,1500,Color.BLACK,Item.OTHER,Move.THUNDER,"Teaches a Pokemon this move."),
	TM64(164,1500,Color.BLACK,Item.OTHER,Move.CLOSE_COMBAT,"Teaches a Pokemon this move."),
	TM65(165,0,Color.BLACK,Item.OTHER,Move.SHADOW_CLAW,"Teaches a Pokemon this move."),
	TM66(166,2000,Color.BLACK,Item.OTHER,Move.DRACO_METEOR,"Teaches a Pokemon this move."),
	TM67(167,2000,Color.BLACK,Item.OTHER,Move.OUTRAGE,"Teaches a Pokemon this move."),
	TM68(168,0,Color.BLACK,Item.OTHER,Move.FLASH,"Teaches a Pokemon this move."),
	TM69(169,0,Color.BLACK,Item.OTHER,Move.ROCK_POLISH,"Teaches a Pokemon this move."),
	TM70(170,1500,Color.BLACK,Item.OTHER,Move.HYDRO_PUMP,"Teaches a Pokemon this move."),
	TM71(171,0,Color.BLACK,Item.OTHER,Move.STONE_EDGE,"Teaches a Pokemon this move."),
	TM72(172,0,Color.BLACK,Item.OTHER,Move.ICE_SPINNER,"Teaches a Pokemon this move."),
	TM73(173,0,Color.BLACK,Item.OTHER,Move.GYRO_BALL,"Teaches a Pokemon this move."),
	TM74(174,900,Color.BLACK,Item.OTHER,Move.SUNNY_DAY,"Teaches a Pokemon this move."),
	TM75(175,900,Color.BLACK,Item.OTHER,Move.RAIN_DANCE,"Teaches a Pokemon this move."),
	TM76(176,700,Color.BLACK,Item.OTHER,Move.SNOWSCAPE,"Teaches a Pokemon this move."),
	TM77(177,700,Color.BLACK,Item.OTHER,Move.SANDSTORM,"Teaches a Pokemon this move."),
	TM78(178,800,Color.BLACK,Item.OTHER,Move.SWORDS_DANCE,"Teaches a Pokemon this move."),
	TM79(179,300,Color.BLACK,Item.OTHER,Move.GRASSY_TERRAIN,"Teaches a Pokemon this move."),
	TM80(180,300,Color.BLACK,Item.OTHER,Move.ELECTRIC_TERRAIN,"Teaches a Pokemon this move."),
	TM81(181,300,Color.BLACK,Item.OTHER,Move.PSYCHIC_TERRAIN,"Teaches a Pokemon this move."),
	TM82(182,300,Color.BLACK,Item.OTHER,Move.SPARKLING_TERRAIN,"Teaches a Pokemon this move."),
	TM83(183,0,Color.BLACK,Item.OTHER,Move.CAPTIVATE,"Teaches a Pokemon this move."),
	TM84(184,0,Color.BLACK,Item.OTHER,Move.DARK_PULSE,"Teaches a Pokemon this move."),
	TM85(185,0,Color.BLACK,Item.OTHER,Move.ROCK_SLIDE,"Teaches a Pokemon this move."),
	TM86(186,0,Color.BLACK,Item.OTHER,Move.X$SCISSOR,"Teaches a Pokemon this move."),
	TM87(187,0,Color.BLACK,Item.OTHER,Move.POISON_JAB,"Teaches a Pokemon this move."),
	TM88(188,0,Color.BLACK,Item.OTHER,Move.GALAXY_BLAST,"Teaches a Pokemon this move."),
	TM89(189,0,Color.BLACK,Item.OTHER,Move.ACROBATICS,"Teaches a Pokemon this move."),
	TM90(190,0,Color.BLACK,Item.OTHER,Move.IRON_BLAST,"Teaches a Pokemon this move."),
	TM91(191,1000,Color.BLACK,Item.OTHER,Move.TRI$ATTACK,"Teaches a Pokemon this move."),
	TM92(192,0,Color.BLACK,Item.OTHER,Move.COMET_CRASH,"Teaches a Pokemon this move."),
	TM93(193,0,Color.BLACK,Item.OTHER,Move.EARTH_POWER,"Teaches a Pokemon this move."),
	TM94(194,0,Color.BLACK,Item.OTHER,Move.HURRICANE,"Teaches a Pokemon this move."),
	TM95(195,500,Color.BLACK,Item.OTHER,Move.TRICK_ROOM,"Teaches a Pokemon this move."),
	TM96(196,0,Color.BLACK,Item.OTHER,Move.ENERGY_BALL,"Teaches a Pokemon this move."),
	TM97(197,0,Color.BLACK,Item.OTHER,Move.SPIRIT_BREAK,"Teaches a Pokemon this move."),
	TM98(198,0,Color.BLACK,Item.OTHER,Move.FLIP_TURN,"Teaches a Pokemon this move."),
	TM99(199,0,Color.BLACK,Item.OTHER,Move.RETURN,"Teaches a Pokemon this move."),
	CALCULATOR(200,0,Color.BLACK,Item.OTHER,null,"Calculates damage simulating\na battle"),
	;
	
	private int id;
	private Color color;
	private int pocket;
	private Move move;
	private int cost;
	private int healAmount;
	private String desc;
	
	public static final int MEDICINE = 1;
    public static final int OTHER = 2;
    public static final int TMS = 3;
	
	Item(int id, int cost, Color color, int pocket, Move move, String desc) {
		this.id = id;
		this.cost = cost;
		this.color = color;
		this.pocket = pocket;
		this.move = move;
		this.desc = desc;
		
		if (id >= 4 && id <= 8) {
			switch(id) {
				case 4:
					healAmount = 20;
					break;
				case 5:
					healAmount = 60;
					break;
				case 6:
					healAmount = 200;
					break;
				case 7:
					healAmount = -1;
					break;
				case 8:
					healAmount = -1;
					break;
				default:
					healAmount = 0;
			}
		}
	}
	
	public int getCost() { return cost; }

	public int getID() { return id; }
	
	public Move getMove() { return move; }
	
	public int getPocket() { return pocket; }
	
	public Color getColor() {
		if (getMove() != null) return getMove().mtype.getColor();
		return color;
	}
	
	public String getDesc() { return desc; }
	
	public static Item getItem(int id) {
		Item[] items = Item.values();
		return items[id];
	}
	
	public static ArrayList<Move> getTMs() {
		ArrayList<Move> result = new ArrayList<>();
		Item[] items = Item.values();

		for (int i = 93; i <= 199; i++) {
            if (i < items.length) {
                result.add(items[i].getMove());
            }
        }
		
		return result;
	}	
	
	@Override
	public String toString() {
		String result = "";
		String name = super.toString();
		if (name.contains("HM") || name.contains("TM")) {
			result = name + " " + getMove().toString();
		} else {
			name = name.toLowerCase().replace('_', ' ');
		    String[] words = name.split(" ");
		    StringBuilder sb = new StringBuilder();
		    for (String word : words) {
		    	if (word.contains("pp")) {
		    		sb.append(word.toUpperCase()).append(" ");
		    	} else {
		    		sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		    	}
		    }
		    result = sb.toString().trim();
		}
		
		return result;
	}

	public int getHealAmount() {
		return healAmount;
	}
	
	public boolean getLearned(Pokemon p) {
		int index1 = p.id - 1;
		int index2 = this.id - 93;
		boolean[][] tm = new boolean[][] {
			
			
															//     1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true }, // 001
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true }, // 003
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true },
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true }, // 005
				{false,false,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true }, 
				{true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true ,true }, // 007
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true }, // 008
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true },
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true }, // 010
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true }, // 012
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,true }, // 014
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true },
				{true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 016
				{true ,true ,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true }, // 018
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true }, // 020
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true },
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true }, // 022
				{true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,true }, // 024
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true }, // 026
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,true }, // 028
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,true }, // 030
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true }, // 032
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,true }, // 034
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
															//+1   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true }, // 036
				{true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true },
				{false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true }, // 038
				{false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true },
				{false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true }, // 040
				{true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true }, // 042
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 044
				{false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true },
				{false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 046
				{false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 048
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true }, // 050
				{true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,false,true },
				{true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true }, // 052
				{true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true },
				{true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true }, // 054
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true }, // 056
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true }, // 058
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,true },
				{false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true }, // 060
				{false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 062
				{false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,true },
				{true ,true ,false,false,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true }, // 064
				{true ,true ,false,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,true }, // 066
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,true },
				{true ,false,false,true ,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 068
				{true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true },
				{true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 070
				{false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true },
				{false,true ,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,true ,true }, // 072
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,false,true },
															//+2   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true }, // 074
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true }, // 076
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true }, // 078
				{true ,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true },
				{true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true }, // 080
				{true ,false,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true },
				{false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true }, // 082
				{false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true },
				{false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true }, // 084
				{false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true }, // 086
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true ,false,true }, // 088
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true },
				{true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true }, // 090
				{true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true },
				{true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true }, // 092
				{true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true }, // 094
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true }, // 096
				{false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true }, // 098
				{true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true }, // 100
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true }, // 102
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true },
				{false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true }, // 104
				{false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true }, // 106
				{false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true }, // 108
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true }, // 110
				{false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,false,true }, // 112
															//+3   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 114
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true }, // 116
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true }, // 118
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true }, // 120
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,false,true }, // 122
				{true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,true }, // 124
				{true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 126
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true }, // 128
				{true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 130
				{true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 132
				{true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 134
				{false,false,false,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true }, // 136
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
				{false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true }, // 138
				{false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true },
				{false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true }, // 140
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true },
				{true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,true }, // 142
				{false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true },
				{true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,false,true ,false,true ,true }, // 144
				{true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true },
				{true ,true ,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true }, // 146
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true }, // 148
				{true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,true ,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,true }, // 150
				{false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true },
				{false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true }, // 152
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true },
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true }, // 154
															//+4   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true },
				{true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true }, // 156
				{true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,true ,false,true },
				{true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true }, // 158
				{true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true }, // 159
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true }, // 161
				{true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 163
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true }, // 165
				{true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true }, // 167
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true }, // 169
				{true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 171
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 173
				{true ,true ,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,true },
				{true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true }, // 175
				{true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true },
				{true ,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true }, // 177
				{true ,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true },
				{true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true }, // 179
				{true ,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true }, // 181
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true }, // 183
				{true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true },
				{true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true }, // 185
				{true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true },
				{true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true }, // 187
				{true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true },
				{true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, // 189
				{false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,true },
				{true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,true }, // 191
				{true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,true },
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true }, // 193
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,true ,false,true }, // 195
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,true ,false,true },
															//+5   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true }, // 197
				{false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true }, // 199
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true }, // 201
				{true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 203
				{true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,true }, // 205
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true }, // 207
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false}, // 209
				{true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true },
				{true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,true }, // 211
				{true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true }, // 213
				{true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true },
				{false,true ,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,true }, // 215
				{true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true },
				{false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true }, // 217
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 219
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,true }, // 221
				{true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true }, // 223
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 225
				{false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 227
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 229
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 231
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 233
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 235
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 237
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 239
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },

		};
		
		return tm[index1][index2];
		
	}

	public Status getStatus() {
		if (id == 9) return Status.POISONED;
		else if (id == 10) return Status.ASLEEP;
		else if (id == 11) return Status.BURNED;
		else if (id == 12) return Status.PARALYZED;
		else if (id == 13) return Status.FROSTBITE;
		else if (id == 14) return null;
		else if (id == 15) return null;
		else return Status.CONFUSED; // lol
		
	}

	public boolean getEligible(int pid) {
		int[] check;
		boolean result = false;
		if (this == Item.LEAF_STONE) {
			check = new int[] {27, 45, 118};
		} else if (this == Item.DUSK_STONE) {
			check = new int[] {141, 160, 215, 220};
		} else if (this == Item.DAWN_STONE) {
			check = new int[] {30, 175, 177};
		} else if (this == Item.ICE_STONE) {
			check = new int[] {62, 64, 193};
		} else if (this == Item.VALIANT_GEM) {
			check = new int[] {38, 86, 108};
		} else if (this == Item.PETTICOAT_GEM) {
			check = new int[] {38, 108};
		} else {
			check = new int[] {};
		}
		
		for (int i = 0; i < check.length; i++) {
			if (pid == check[i]) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	public void useCalc(Player p) {
		JPanel calc = new JPanel();
	    calc.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5); // Add space between components
        
        JComboBox<Pokemon> userMons = new JComboBox<>();
        JLabel userLevel = new JLabel();
        JLabel[] userStatLabels = new JLabel[6];
        @SuppressWarnings("unchecked")
		JComboBox<Integer>[] userStages = new JComboBox[6];
        JButton userCurrentHP = new JButton();
        JLabel userHPP = new JLabel();
        JLabel userSpeed = new JLabel();
        JGradientButton[] userMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
        JLabel[] userDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
        for (int k = 0; k < p.team.length; k++) {
        	if (p.team[k] != null) userMons.addItem(p.team[k].clone());
        }
        AutoCompleteDecorator.decorate(userMons);
        
        JComboBox<Pokemon> foeMons = new JComboBox<>();
        JTextField foeLevel = new JTextField();
        JLabel[] foeStatLabels = new JLabel[6];
        @SuppressWarnings("unchecked")
		JComboBox<Integer>[] foeStages = new JComboBox[6];
        JLabel foeSpeed = new JLabel();
        JGradientButton[] foeMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
        JLabel[] foeDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
        for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
        	foeMons.addItem(new Pokemon(k, 50, false, true));
        }
        for (Trainer tr : Main.modifiedTrainers) {
        	for (Pokemon po : tr.getTeam()) {
        		Pokemon add = po.clone();
        		add.nickname = tr.getName();
        		foeMons.addItem(add);
        	}
        }
        AutoCompleteDecorator.decorate(foeMons);
        
        userMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
            userLevel.setText(userCurrent.getLevel() + "");
            updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        });
        
        foeMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	foeLevel.setText(foeCurrent.getLevel() + "");
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        
        foeLevel.addActionListener(l ->{
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	try {
        		int level = Integer.parseInt(foeLevel.getText());
    			if (level >= 1 && level <= 100) {
    				foeCurrent.level = level;
    			} else {
    				foeCurrent.level = 50;
    			}
    		} catch (NumberFormatException e1) {
    			foeCurrent.level = 50;
    		}
        	foeCurrent.stats = foeCurrent.getStats();
        	foeCurrent.setMoves();
        	
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        
		foeLevel.addFocusListener(new FocusAdapter() {
			@Override // implementation
	    	public void focusGained(FocusEvent e) {
	        	foeLevel.selectAll();
	    	}
		});
        
        Pokemon userC = ((Pokemon) userMons.getSelectedItem());
        Pokemon foeC = ((Pokemon) foeMons.getSelectedItem());
        
        calc.add(userMons, gbc);
        gbc.gridx++;
        calc.add(foeMons, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        calc.add(userLevel, gbc);
        gbc.gridx++;
        JPanel foeLevelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        foeLevelPanel.add(foeLevel);
        calc.add(foeLevelPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel statsPanel = new JPanel(new GridLayout(6, 3));
        for (int i = 0; i < 6; i++) {
        	userStatLabels[i] = new JLabel(userC.stats[i] + "");
        	Integer[] stages = new Integer[] {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6};
        	userStages[i] = new JComboBox<Integer>(stages);
        	if (i != 0) userStages[i].setSelectedIndex(userC.statStages[i - 1] + 6);
        	JLabel blank = new JLabel("");
        	statsPanel.add(userStatLabels[i]);
        	if (i == 0) {
        		userCurrentHP.setText(userC.currentHP + "");
        		statsPanel.add(userCurrentHP);
        	} else {
        		int index = i;
        		userStages[i].addActionListener(e -> {
        			Pokemon current = ((Pokemon) userMons.getSelectedItem());
        			Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        			int amt = (int) userStages[index].getSelectedItem();
        			current.statStages[index - 1] = amt;
        			updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        			updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, null, null);
        			if (index == 5) userSpeed.setText((current.getSpeed()) + "");
        		});
        		statsPanel.add(userStages[i]);
        	}
        	
        	if (i == 0) {
        		double percent = userC.currentHP * 100.0 / userC.getStat(0);
        		userHPP.setText(String.format("%.1f", percent) + "%");
        		statsPanel.add(userHPP);
        	} else if (i == 5) {
        		userSpeed.setText((userC.getSpeed()) + "");
        		statsPanel.add(userSpeed);
        	} else {
        		statsPanel.add(blank);
        	}
        	
        	
        }
        
        JPanel fStatsPanel = new JPanel(new GridLayout(6, 3));
        for (int i = 0; i < 6; i++) {
        	foeStatLabels[i] = new JLabel(foeC.stats[i] + "");
        	Integer[] stages = new Integer[] {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6};
        	foeStages[i] = new JComboBox<Integer>(stages);
        	if (i != 0) foeStages[i].setSelectedIndex(foeC.statStages[i - 1] + 6);
        	JLabel blank = new JLabel("");
        	fStatsPanel.add(foeStatLabels[i]);
        	if (i == 0) {
        		fStatsPanel.add(new JButton());
        	} else {
        		int index = i;
        		foeStages[i].addActionListener(e -> {
        			Pokemon current = ((Pokemon) foeMons.getSelectedItem());
        			Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        			int amt = (int) foeStages[index].getSelectedItem();
        			current.statStages[index - 1] = amt;
        			updateMoves(current, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        			updateMoves(userCurrent, userMoves, userDamage, current, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        			if (index == 5) foeSpeed.setText((current.getSpeed()) + "");
        		});
        		fStatsPanel.add(foeStages[i]);
        	}
        	
        	if (i == 5) {
        		foeSpeed.setText((foeC.getSpeed()) + "");
        		fStatsPanel.add(foeSpeed);
        	} else {
        		fStatsPanel.add(blank);
        	}
        	
        }
        
        userLevel.setText(userC.getLevel() + "");
        updateMoves(userC, userMoves, userDamage, foeC, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        
        foeLevel.setText(foeC.getLevel() + "");
        updateMoves(foeC, foeMoves, foeDamage, userC, foeStatLabels, foeStages, foeSpeed, null, null);
        
        calc.add(statsPanel, gbc);
        gbc.gridx++;
        calc.add(fStatsPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel userMovesPanel = new JPanel(new GridLayout(4,2));
        for (int k = 0; k < userMoves.length; k++) {
        	userMovesPanel.add(userMoves[k]);
        	userMovesPanel.add(userDamage[k]);
        }
        calc.add(userMovesPanel, gbc);
        gbc.gridx++;
        JPanel foeMovesPanel = new JPanel(new GridLayout(4,2));
        for (int k = 0; k < userMoves.length; k++) {
        	foeMovesPanel.add(foeMoves[k]);
        	foeMovesPanel.add(foeDamage[k]);
        }
        calc.add(foeMovesPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel resetButtonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	for (int i = 0; i < 4; i++) {
        		uCurrent.moveset[i] = p.team[userMons.getSelectedIndex()].moveset[i];
        	}
        	updateMoves(uCurrent, userMoves, userDamage, fCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        resetButtonPanel.add(resetButton);
        calc.add(resetButtonPanel, gbc);
        gbc.gridx++;
        
        JPanel fResetButtonPanel = new JPanel();
        JButton fResetButton = new JButton("Reset");
        fResetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	fCurrent.setMoves();
        	updateMoves(fCurrent, foeMoves, foeDamage, uCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        });
        fResetButtonPanel.add(fResetButton);
        calc.add(fResetButtonPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JOptionPane.showMessageDialog(null, calc, "Damage Calculator", JOptionPane.PLAIN_MESSAGE);
		
	}
	
	private void updateMoves(Pokemon current, JGradientButton[] moves, JLabel[] damages, Pokemon foe, JLabel[] statLabels, JComboBox<Integer>[] stages,
			JLabel speed, JButton currentHP, JLabel HPP) {
        for (int k = 0; k < moves.length; k++) {
        	if (current.moveset[k] != null) {
        		moves[k].setText(current.moveset[k].move.toString());
        		moves[k].setBackground(current.moveset[k].move.mtype.getColor());
        		if (current.moveset[k].move == Move.HIDDEN_POWER) moves[k].setBackground(current.determineHPType().getColor());
        		int minDamage = current.calcWithTypes(foe, current.moveset[k].move, false, -1);
        		int maxDamage = current.calcWithTypes(foe, current.moveset[k].move, false, 1);
        		double minDamageD = minDamage * 1.0 / foe.getStat(0);
        		minDamageD *= 100;
        		String formattedMinD = String.format("%.1f", minDamageD);
        		double maxDamageD = maxDamage * 1.0 / foe.getStat(0);
        		maxDamageD *= 100;
        		String formattedMaxD = String.format("%.1f", maxDamageD);
        		damages[k].setText(formattedMinD + "% - " + formattedMaxD + "%");
        	} else {
        		moves[k].setText("[NO MOVE]");
        		moves[k].setBackground(null);
        		damages[k].setText("0% - 0%");
        	}
    		MouseListener[] listeners = moves[k].getMouseListeners();
    		for (MouseListener listener : listeners) {
    			moves[k].removeMouseListener(listener);
    		}
    		final int kndex = k;
    		moves[k].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			    		if (current.moveset[kndex] != null) {
	    	                JOptionPane.showMessageDialog(null, current.moveset[kndex].move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
	        			}
			    	} else {
			    		Move[] allMoves = Move.values();
			    		JComboBox<Move> moveComboBox = new JComboBox<>(allMoves);
			    		
			    		AutoCompleteDecorator.decorate(moveComboBox);
			    		
			    		JPanel setMovePanel = new JPanel();
			    		setMovePanel.add(new JLabel("Select a move:"));
			            setMovePanel.add(moveComboBox);
			            
			    		int result = JOptionPane.showConfirmDialog(null, setMovePanel, "Set Move", JOptionPane.OK_OPTION);
			    		if (result == JOptionPane.OK_OPTION) {
			    			Move selectedMove = (Move) moveComboBox.getSelectedItem();
			    			current.moveset[kndex] = new Moveslot(selectedMove);
			    		}
			    		updateMoves(current, moves, damages, foe, statLabels, stages, speed, currentHP, HPP);
			    	}
			    }
            });

    		for (int i = 0; i < 6; i++) {
    			statLabels[i].setText(current.getStat(i) + "");
    			
    			if (i != 0) {
	    			ActionListener stagesListener = stages[i].getActionListeners()[0];
	    		    stages[i].removeActionListener(stagesListener);
	    		    
	    		    if (i != 0) stages[i].setSelectedIndex(current.statStages[i-1] + 6);
	    		    
	    		    stages[i].addActionListener(stagesListener);
    			}

    			if (i == 0 && currentHP != null) {
    				currentHP.setText(current.currentHP + "");
    				double percent = current.currentHP * 100.0 / current.getStat(0);
            		HPP.setText(String.format("%.1f", percent) + "%");
    			}
    			if (i == 5) speed.setText(current.getSpeed() + "");
    		}
    		
        }
	}

	public static JPanel displayGenerator(Player p) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		
		JComboBox<Pokemon> nameInput = new JComboBox<Pokemon>();
		for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
        	nameInput.addItem(new Pokemon(k, 50, false, true));
        }
		result.add(nameInput);
		
		Integer[] levels = new Integer[100];
		for (int i = 0; i < 100; i++) {
			levels[i] = i + 1;
		}
		JComboBox<Integer> levelInput = new JComboBox<>(levels);
		result.add(levelInput);
		
		Ability[] abilityOptions = new Ability[2];
		for (int i = 0; i < 2; i++) {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.setAbility(i);
			abilityOptions[i] = test.ability;
		}
		JComboBox<Ability> abilityInput = new JComboBox<>(abilityOptions);
		result.add(abilityInput);
		
		JComboBox<String> natures = new JComboBox<>();
		Pokemon temp = new Pokemon(1, 1, false, false);
		for (int i = 0; i < 25; i++) {
			temp.nature = temp.getNature(i);
			natures.addItem(temp.getNature());
		}
		result.add(natures);
		
		@SuppressWarnings("unchecked")
		JComboBox<Integer>[] ivInputs = new JComboBox[6];
		Integer[] options = new Integer[32];
		for (int i = 0; i < 32; i++) {
			options[i] = i;
		}
		for (int i = 0; i < 6; i++) {
			ivInputs[i] = new JComboBox<Integer>(options);
			ivInputs[i].setSelectedIndex(31);
			AutoCompleteDecorator.decorate(ivInputs[i]);
			result.add(ivInputs[i]);
		}
		
		Integer[] happinessOptions = new Integer[256];
		for (int i = 0; i < happinessOptions.length; i++) {
			happinessOptions[i] = i;
		}
		JComboBox<Integer> happiness = new JComboBox<>(happinessOptions);
		happiness.setSelectedIndex(70);
		result.add(happiness);
		
		AutoCompleteDecorator.decorate(nameInput);
		AutoCompleteDecorator.decorate(levelInput);
		AutoCompleteDecorator.decorate(abilityInput);
		AutoCompleteDecorator.decorate(natures);
		AutoCompleteDecorator.decorate(happiness);
		
		JPanel movesPanel = new JPanel(new GridLayout(2, 2));
		@SuppressWarnings("unchecked")
		JComboBox<Move>[] moveInputs = new JComboBox[4];
		Move[] movebank = Move.values();
		Pokemon testMoves = (Pokemon) nameInput.getSelectedItem();
		testMoves.level = 1;
		testMoves.setMoves();
		for (int i = 0; i < 4; i++) {
			moveInputs[i] = new JComboBox<Move>(movebank);
			moveInputs[i].setSelectedItem(Move.STRUGGLE);
			Moveslot m = testMoves.moveset[i];
			Move m1 = m == null ? null : m.move;
			moveInputs[i].setSelectedItem(m1);
			AutoCompleteDecorator.decorate(moveInputs[i]);
			movesPanel.add(moveInputs[i]);
		}
		
		result.add(movesPanel);
		
		nameInput.addActionListener(f -> {
			abilityInput.removeAllItems();
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.level = (Integer) levelInput.getSelectedItem();
			test.setMoves();
			for (int i = 0; i < 2; i++) {
				test.setAbility(i);
				abilityInput.addItem(test.ability);
			}
			for (int j = 0; j < 4; j++) {
				moveInputs[j].setSelectedItem(Move.STRUGGLE);
				Moveslot m = test.moveset[j];
				Move m1 = m == null ? null : m.move;
				moveInputs[j].setSelectedItem(m1);
			}
		});
		
		levelInput.addActionListener(f -> {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.level = (Integer) levelInput.getSelectedItem();
			test.setMoves();
			for (int j = 0; j < 4; j++) {
				moveInputs[j].setSelectedItem(Move.STRUGGLE);
				Moveslot m = test.moveset[j];
				Move m1 = m == null ? null : m.move;
				moveInputs[j].setSelectedItem(m1);
			}
		});
		
		JButton randomize = new JButton("RANDOMIZE");
		randomize.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseClicked(MouseEvent e) {
				Random random = new Random();
		    	if (SwingUtilities.isRightMouseButton(e)) {
		            nameInput.setSelectedIndex(random.nextInt(Pokemon.MAX_POKEMON));
		            levelInput.setSelectedIndex(random.nextInt(100));
		            happiness.setSelectedIndex(random.nextInt(255));
		            for (int i = 0; i < 4; i++) {
		            	moveInputs[i].setSelectedIndex(random.nextInt(Move.values().length));
		            }
		        }
		    	abilityInput.setSelectedIndex(random.nextInt(2));
		    	natures.setSelectedIndex(random.nextInt(25));
		    	for (int i = 0; i < 6; i++) {
		    		ivInputs[i].setSelectedIndex(random.nextInt(32));
		    	}
		    	
		    }
			
		});
		result.add(randomize);
		
		JButton generate = new JButton("GENERATE");
		generate.addActionListener(e -> {
			int id = ((Pokemon) nameInput.getSelectedItem()).id;
			int level = (Integer) levelInput.getSelectedItem();
			int ability = abilityInput.getSelectedIndex();
			String selectedNature = (String) natures.getSelectedItem();
			for (int i = 0; i < 25; i++) {
				temp.nature = temp.getNature(i);
				if (temp.getNature().equals(selectedNature)) break;
			}
			double[] nature = temp.nature;
			int[] ivs = new int[6];
			for (int i = 0; i < 6; i++) {
				ivs[i] = (Integer) ivInputs[i].getSelectedItem();
			}
			Move[] moveset = new Move[4];
			for (int i = 0; i < 4; i++) {
				moveset[i] = (Move) moveInputs[i].getSelectedItem();
			}
			
			Pokemon resultPokemon = new Pokemon(id, level, true, false);
			resultPokemon.abilitySlot = ability;
			resultPokemon.setAbility(resultPokemon.abilitySlot);
			resultPokemon.nature = nature;
			resultPokemon.ivs = ivs;
			resultPokemon.getStats();
			
			for (int i = 0; i < 4; i++) {
				resultPokemon.moveset[i] = moveset[i] == null ? null : new Moveslot(moveset[i]);
			}
			
			resultPokemon.happiness = (Integer) happiness.getSelectedItem();
			
			resultPokemon.heal();
			
			p.catchPokemon(resultPokemon);
			
		});
		result.add(generate);
		
		return result;
	}
}
