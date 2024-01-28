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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	REPEL(0,10,new Color(0, 92, 5),Item.OTHER,null,"Prevents wild Pokemon encounters for 200 steps"),
	POKEBALL(1,10,new Color(176, 0, 12),Item.OTHER,null,"A standard device for capturing wild Pokemon"),
	GREAT_BALL(2,25,new Color(0, 0, 148),Item.OTHER,null,"An upgraded device for capturing wild Pokemon"),
	ULTRA_BALL(3,50,new Color(148, 171, 0),Item.OTHER,null,"A very efficient device for capturing wild Pokemon"),
	POTION(4,25,new Color(124, 0, 219),Item.MEDICINE,null,"Restores 20 HP"),
	SUPER_POTION(5,60,new Color(140, 24, 8),Item.MEDICINE,null,"Restores 60 HP"),
	HYPER_POTION(6,175,new Color(255, 0, 191),Item.MEDICINE,null,"Restores 200 HP"),
	MAX_POTION(7,250,new Color(0, 21, 255),Item.MEDICINE,null,"Restores a Pokemon's HP to full"),
	FULL_RESTORE(8,400,new Color(255, 196, 0),Item.MEDICINE,null,"Restores a Pokemon's HP to full and cures any status conditions"),
	ANTIDOTE(9,10,new Color(157, 0, 255),Item.MEDICINE,null,"Cures a Pokemon of Poison"),
	AWAKENING(10,10,new Color(63, 83, 92),Item.MEDICINE,null,"Cures a Pokemon of Sleep"),
	BURN_HEAL(11,10,new Color(133, 15, 19),Item.MEDICINE,null,"Cures a Pokemon of Burn"),
	PARALYZE_HEAL(12,10,new Color(176, 158, 0),Item.MEDICINE,null,"Cures a Pokemon of Paralysis"),
	FREEZE_HEAL(13,10,new Color(0, 170, 189),Item.MEDICINE,null,"Cures a Pokemon of Frostbite"),
	FULL_HEAL(14,100,new Color(255, 247, 0),Item.MEDICINE,null,"Cures a Pokemon of any status condition"),
	KLEINE_BAR(15,50,new Color(0, 55, 255),Item.MEDICINE,null,"Cures a Pokemon of any status condition"),
	REVIVE(16,500,new Color(219, 194, 0),Item.MEDICINE,null,"Recovers a Pokemon from fainting with 50% HP"),
	MAX_REVIVE(17,1000,new Color(219, 194, 0),Item.MEDICINE,null,"Recovers a Pokemon from fainting with full HP"),
	RARE_CANDY(18,0,new Color(124, 54, 255),Item.MEDICINE,null,"Elevates a Pokemon by 1 level"),
	EUPHORIAN_GEM(19,500,new Color(138, 237, 255),Item.OTHER,null,"Grants a Pokemon 100 friendship points"),
	LEAF_STONE(20,1000,new Color(0, 120, 20),Item.OTHER,null,"Evolves a certain species of Pokemon"),
	DUSK_STONE(21,1000,new Color(64, 64, 64),Item.OTHER,null,"Evolves a certain species of Pokemon"),
	DAWN_STONE(22,1000,new Color(0, 176, 179),Item.OTHER,null,"Evolves a certain species of Pokemon"),
	ICE_STONE(23,1000,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of Pokemon"),
	VALIANT_GEM(24,2000,new Color(72, 75, 219),Item.OTHER,null,"Grants Masculine energy to a Pokemon, evolving them into their male evolution"),
	PETTICOAT_GEM(25,2000,new Color(204, 61, 140),Item.OTHER,null,"Grants Feminine energy to a Pokemon, evolving them into their female evolution"),
	ABILITY_CAPSULE(26,2500,new Color(102, 7, 143),Item.OTHER,null,"Swaps a Pokemon's ability with its other possible ability"),
	BOTTLE_CAP(27,1000,new Color(192, 192, 192),Item.OTHER,null,"Maxes out an IV of choosing"),
	GOLD_BOTTLE_CAP(28,0,new Color(255, 215, 0),Item.OTHER,null,"Maxes out all IVs of a Pokemon"),
	ADAMANT_MINT(29,2500,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Atk, -SpA"),
	BOLD_MINT(30,1750,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Def, -Atk"),
	BRAVE_MINT(31,1000,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Atk, -Spe"),
	CALM_MINT(32,1500,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +SpD, -Atk"),
	CAREFUL_MINT(33,1250,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +SpD, -SpA"),
	IMPISH_MINT(34,1750,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Def, -SpA"),
	JOLLY_MINT(35,2500,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Spe, -SpA"),
	MODEST_MINT(36,2250,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +SpA, -Atk"),
	QUIET_MINT(37,1000,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +SpA, -Spe"),
	SERIOUS_MINT(38,1250,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to Neutral"),
	TIMID_MINT(39,2500,new Color(113, 84, 255),Item.MEDICINE,null,"Changes a Pokemon's nature  to +Spe, -Atk"),
	ELIXIR(40,200,new Color(230, 146, 78),Item.MEDICINE,null,"Restores PP of a selected move"),
	MAX_ELIXIR(41,250,new Color(246, 255, 120),Item.MEDICINE,null,"Restores PP of all moves on a Pokemon"),
	PP_UP(42,2500,new Color(150, 51, 156),Item.MEDICINE,null,"Increases max PP of a selected move by 20%"),
	PP_MAX(43,0,new Color(142, 230, 21),Item.MEDICINE,null,"Increases max PP of a selected move by its max PP, which is 160%"),
	EDGE_KIT(44,0,new Color(232, 52, 54),Item.OTHER,null,"Edges ;) your Pokemon until they're about to bust :{D"),
	THUNDER_SCALES_FOSSIL(45,5000,new Color(201, 169, 81),Item.OTHER,null,"A fossil of an ancient Pokemon that lived in a desert. It appears to have an electric charge ridden in the scales."),
	DUSK_SCALES_FOSSIL(46,5000,new Color(45, 47, 51),Item.OTHER,null,"A fossil of an ancient Pokemon that lived in a forest. It appears to give off a dark energy within  the scales."),
	CHOICE_BAND(47,0,new Color(224, 152, 159),Item.HELD_ITEM,null,"A curious headband that boosts the holder's Attack stat but only allows the use of a single move."),
	CHOICE_SCARF(48,0,new Color(133, 172, 220),Item.HELD_ITEM,null,"A curious scarf that boosts the holder's Speed stat but only allows the use of a single move."),
	CHOICE_SPECS(49,0,new Color(238, 236, 100),Item.HELD_ITEM,null,"A pair of curious glasses that boost the holder's Sp. Atk stat but only allow the use of a single move."),
	LEFTOVERS(50,0,new Color(227, 96, 91),Item.HELD_ITEM,null,"An item that restores the user's HP gradually throughout a battle."),
	BLACK_SLUDGE(51,0,new Color(144, 138, 169),Item.HELD_ITEM,null,"If the holder is a Poison type, this sludge will gradually restore its HP. It damages any other type."),
	EVIOLITE(52,0,new Color(186, 141, 190),Item.HELD_ITEM,null,"A mysterious evolutionary lump that boosts the Defense and Sp. Def stats when held by a Pokemon that can still evolve."),
	EVERSTONE(53,100,new Color(179, 200, 210),Item.HELD_ITEM,null,"A Pokemon holding this peculiar stone is prevented from evolving."),
	DAMP_ROCK(54,750,new Color(37, 99, 179),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to rain, the rain will persist for longer than usual."),
	HEAT_ROCK(55,750,new Color(209, 68, 61),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to harsh sunlight, the sunlight will persist for longer than usual."),
	SMOOTH_ROCK(56,600,new Color(166, 124, 90),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to a sandstorm, the storm will persist for longer than usual."),
	ICY_ROCK(57,600,new Color(130, 200, 232),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to snow, the snow will persist for longer than usual."),
	ASSAULT_VEST(58,0,new Color(194, 80, 84),Item.HELD_ITEM,null,"An offensive vest boosts the holder's Sp. Def stat but prevents the use of status moves."),
	BRIGHT_POWDER(59,0,new Color(214, 234, 206),Item.HELD_ITEM,null,"A glittery powder that casts a tricky glare which lowers the accuracy of opposing Pokemon's moves."),
	EXPERT_BELT(60,0,new Color(94, 90, 97),Item.HELD_ITEM,null,"A well-worn belt that slightly boosts the power of the holder's supereffective moves."),
	TERRAIN_EXTENDER(61,0,new Color(212, 228, 229),Item.HELD_ITEM,null,"A held item that extends the duration of terrain caused by the holder's move or Ability."),
	LIFE_ORB(62,0,new Color(184, 72, 144),Item.HELD_ITEM,null,"An orb that boosts the power of the holder's moves, but at the cost of some HP."),
	FLAME_ORB(63,0,new Color(225, 3, 3),Item.HELD_ITEM,null,"A bizarre orb that gives off heat when touched and will afflict the holder with a burn during battle."),
	TOXIC_ORB(64,0,new Color(148, 112, 172),Item.HELD_ITEM,null,"A bizarre orb that exudes toxins when touched and will badly poison the holder during battle."),
	ROCKY_HELMET(65,0,new Color(241, 188, 27),Item.HELD_ITEM,null,"If another Pokemon makes direct contact with the holder, that Pokemon will be damaged."),
	LIGHT_CLAY(66,0,new Color(183, 216, 126),Item.HELD_ITEM,null,"An item that when the holder uses protective moves like Light Screen or Reflect, their effects will last longer than usual."),
	SOOTHE_BELL(67,0,new Color(198, 199, 202),Item.HELD_ITEM,null,"The comforting chime of this bell calms the holder, making it friendly."),
	LOADED_DICE(68,0,new Color(144, 232, 16),Item.HELD_ITEM,null,"This loaded dice always rolls a good number, and holding one can ensure that the holder's multistrike moves hit more times."),
	SCOPE_LENS(69,0,new Color(192, 183, 54),Item.HELD_ITEM,null,"A lens for scoping out weak points. It boosts the holder's critical-hit ratio."),
	WIDE_LENS(70,0,new Color(74, 162, 195),Item.HELD_ITEM,null,"A magnifying lens that slightly boosts the accuracy of the holder's moves."),
	QUICK_CLAW(71,0,new Color(213, 189, 105),Item.HELD_ITEM,null,"This light, sharp claw lets the holder move first occasionally."),
	BIG_ROOT(72,0,new Color(208, 161, 91),Item.HELD_ITEM,null,"This root boosts the amount of HP the holder restores to itself when it uses HP-stealing moves."),
	CLEAR_AMULET(73,0,new Color(204, 204, 243),Item.HELD_ITEM,null,"This clear, sparkling amulet protects the holder from having its stats lowered by moves used against it or by other Pokemon's Abilities."),
	COVERT_CLOAK(74,0,new Color(101, 136, 160),Item.HELD_ITEM,null,"This hooded cloak conceals the holder, tricking the eyes of its enemies and protecting it from the additional effects of moves."),
	HEAVY$DUTY_BOOTS(75,0,new Color(97, 97, 97),Item.HELD_ITEM,null,"These boots protect the holder from the effects of entry hazards set on the battlefield."),
	FOCUS_BAND(76,0,new Color(232, 80, 80),Item.HELD_ITEM,null,"When the holder is hit with a move that should knock it out, it may be able to endure with 1 HP."),
	KING1S_ROCK(77,0,new Color(224, 206, 58),Item.HELD_ITEM,null,"It may cause the target to flinch whenever the holder successfully inflicts damage on them with an attack."),
	SHED_SHELL(78,0,new Color(243, 241, 140),Item.HELD_ITEM,null,"Hard and sturdy, this discarded carapace enables the holder to switch out of battle without fail."),
	MUSCLE_BAND(79,0,new Color(225, 200, 50),Item.HELD_ITEM,null,"This headband exudes strength, slightly boosting the power of the holder's physical moves."),
	WISE_GLASSES(80,0,new Color(92, 105, 117),Item.HELD_ITEM,null,"This thick pair of glasses slightly boosts the power of the holder's special moves."),
	FOCUS_SASH(81,0,new Color(232, 80, 80),Item.HELD_ITEM,null,"If the holder has full HP and it is hit with a move that should knock it out, it will endure with 1 HP—but only once."),
	AIR_BALLOON(82,0,new Color(232, 72, 72),Item.HELD_ITEM,null,"This balloon makes the holder float in the air. If the holder is hit with an attack, the balloon will burst."),
	POWER_HERB(83,0,new Color(253, 80, 77),Item.HELD_ITEM,null,"A herb that allows the holder to immediately use a move that normally requires a turn to charge—but only once."),
	WHITE_HERB(84,0,new Color(242, 242, 242),Item.HELD_ITEM,null,"A herb that will restore any lowered stat in battle—but only once."),
	WEAKNESS_POLICY(85,0,new Color(215, 233, 195),Item.HELD_ITEM,null,"If the holder is hit with a move it's weak to, this policy sharply boosts its Attack and Sp. Atk stats."),
	BLUNDER_POLICY(86,0,new Color(239, 239, 178),Item.HELD_ITEM,null,"If the holder misses with a move due to low accuracy, this policy will sharply boost its Speed stat."),
	RED_CARD(87,0,new Color(216, 35, 22),Item.HELD_ITEM,null,"If the holder is damaged by an attack, the mysterious power of this card can remove the attacker from the battle."),
	THROAT_SPRAY(88,0,new Color(96, 120, 168),Item.HELD_ITEM,null,"If the holder uses a sound-based move, this throat spray will boost its Sp. Atk stat."),
	NULL89(89,0,Color.BLACK,Item.HELD_ITEM,null,""),
	NULL90(90,0,Color.BLACK,Item.HELD_ITEM,null,""),
	NULL91(91,0,Color.BLACK,Item.OTHER,null,""),
	NULL92(92,0,Color.BLACK,Item.OTHER,null,""),
	HM01(93,0,Color.BLACK,Item.TMS,Move.CUT,"Teaches a Pokemon this move."),
	HM02(94,0,Color.BLACK,Item.TMS,Move.ROCK_SMASH,"Teaches a Pokemon this move."),
	HM03(95,0,Color.BLACK,Item.TMS,Move.VINE_CROSS,"Teaches a Pokemon this move."),
	HM04(96,0,Color.BLACK,Item.TMS,Move.SURF,"Teaches a Pokemon this move."),
	HM05(97,0,Color.BLACK,Item.TMS,Move.SLOW_FALL,"Teaches a Pokemon this move."),
	HM06(98,0,Color.BLACK,Item.TMS,Move.WHIRLPOOL,"Teaches a Pokemon this move."),
	HM07(99,0,Color.BLACK,Item.TMS,Move.ROCK_CLIMB,"Teaches a Pokemon this move."),
	HM08(100,0,Color.BLACK,Item.TMS,Move.LAVA_SURF,"Teaches a Pokemon this move."),
	TM01(101,0,Color.BLACK,Item.TMS,Move.SUPER_FANG,"Teaches a Pokemon this move."),
	TM02(102,0,Color.BLACK,Item.TMS,Move.DRAGON_CLAW,"Teaches a Pokemon this move."),
	TM03(103,0,Color.BLACK,Item.TMS,Move.ELEMENTAL_SPARKLE,"Teaches a Pokemon this move."),
	TM04(104,0,Color.BLACK,Item.TMS,Move.CALM_MIND,"Teaches a Pokemon this move."),
	TM05(105,0,Color.BLACK,Item.TMS,Move.BODY_SLAM,"Teaches a Pokemon this move."),
	TM06(106,0,Color.BLACK,Item.TMS,Move.SHADOW_BALL,"Teaches a Pokemon this move."),
	TM07(107,0,Color.BLACK,Item.TMS,Move.FOCUS_BLAST,"Teaches a Pokemon this move."),
	TM08(108,0,Color.BLACK,Item.TMS,Move.BULK_UP,"Teaches a Pokemon this move."),
	TM09(109,0,Color.BLACK,Item.TMS,Move.LEAF_BLADE,"Teaches a Pokemon this move."),
	TM10(110,0,Color.BLACK,Item.TMS,Move.ICE_BEAM,"Teaches a Pokemon this move."),
	TM11(111,0,Color.BLACK,Item.TMS,Move.PSYSHOCK,"Teaches a Pokemon this move."),
	TM12(112,750,Color.BLACK,Item.TMS,Move.PROTECT,"Teaches a Pokemon this move."),
	TM13(113,5000,Color.BLACK,Item.TMS,Move.BATON_PASS,"Teaches a Pokemon this move."),
	TM14(114,0,Color.BLACK,Item.TMS,Move.TAUNT,"Teaches a Pokemon this move."),
	TM15(115,1000,Color.BLACK,Item.TMS,Move.GIGA_IMPACT,"Teaches a Pokemon this move."),
	TM16(116,1000,Color.BLACK,Item.TMS,Move.HYPER_BEAM,"Teaches a Pokemon this move."),
	TM17(117,0,Color.BLACK,Item.TMS,Move.SOLAR_BEAM,"Teaches a Pokemon this move."),
	TM18(118,0,Color.BLACK,Item.TMS,Move.IRON_HEAD,"Teaches a Pokemon this move."),
	TM19(119,0,Color.BLACK,Item.TMS,Move.PHOTON_GEYSER,"Teaches a Pokemon this move."),
	TM20(120,0,Color.BLACK,Item.TMS,Move.EARTHQUAKE,"Teaches a Pokemon this move."),
	TM21(121,0,Color.BLACK,Item.TMS,Move.THROAT_CHOP,"Teaches a Pokemon this move."),
	TM22(122,0,Color.BLACK,Item.TMS,Move.FELL_STINGER,"Teaches a Pokemon this move."),
	TM23(123,800,Color.BLACK,Item.TMS,Move.WEATHER_BALL,"Teaches a Pokemon this move."),
	TM24(124,900,Color.BLACK,Item.TMS,Move.TERRAIN_PULSE,"Teaches a Pokemon this move."),
	TM25(125,0,Color.BLACK,Item.TMS,Move.THUNDERBOLT,"Teaches a Pokemon this move."),
	TM26(126,0,Color.BLACK,Item.TMS,Move.HIDDEN_POWER,"Teaches a Pokemon this move."),
	TM27(127,0,Color.BLACK,Item.TMS,Move.DRAIN_PUNCH,"Teaches a Pokemon this move."),
	TM28(128,0,Color.BLACK,Item.TMS,Move.FLAME_CHARGE,"Teaches a Pokemon this move."),
	TM29(129,0,Color.BLACK,Item.TMS,Move.LIQUIDATION,"Teaches a Pokemon this move."),
	TM30(130,0,Color.BLACK,Item.TMS,Move.U$TURN,"Teaches a Pokemon this move."),
	TM31(131,0,Color.BLACK,Item.TMS,Move.FALSE_SWIPE,"Teaches a Pokemon this move."),
	TM32(132,0,Color.BLACK,Item.TMS,Move.ZING_ZAP,"Teaches a Pokemon this move."),
	TM33(133,0,Color.BLACK,Item.TMS,Move.PSYCHIC_FANGS,"Teaches a Pokemon this move."),
	TM34(134,0,Color.BLACK,Item.TMS,Move.MAGIC_TOMB,"Teaches a Pokemon this move."),
	TM35(135,0,Color.BLACK,Item.TMS,Move.FLAMETHROWER,"Teaches a Pokemon this move."),
	TM36(136,0,Color.BLACK,Item.TMS,Move.SLUDGE_BOMB,"Teaches a Pokemon this move."),
	TM37(137,0,Color.BLACK,Item.TMS,Move.ROCK_TOMB,"Teaches a Pokemon this move."),
	TM38(138,1500,Color.BLACK,Item.TMS,Move.BLIZZARD,"Teaches a Pokemon this move."),
	TM39(139,0,Color.BLACK,Item.TMS,Move.PSYCHIC,"Teaches a Pokemon this move."),
	TM40(140,0,Color.BLACK,Item.TMS,Move.FACADE,"Teaches a Pokemon this move."),
	TM41(141,1000,Color.BLACK,Item.TMS,Move.REFLECT,"Teaches a Pokemon this move."),
	TM42(142,1000,Color.BLACK,Item.TMS,Move.LIGHT_SCREEN,"Teaches a Pokemon this move."),
	TM43(143,1000,Color.BLACK,Item.TMS,Move.DAZZLING_GLEAM,"Teaches a Pokemon this move."),
	TM44(144,1000,Color.BLACK,Item.TMS,Move.PLAY_ROUGH,"Teaches a Pokemon this move."),
	TM45(145,1000,Color.BLACK,Item.TMS,Move.WILL$O$WISP,"Teaches a Pokemon this move."),
	TM46(146,1500,Color.BLACK,Item.TMS,Move.FIRE_BLAST,"Teaches a Pokemon this move."),
	TM47(147,0,Color.BLACK,Item.TMS,Move.STAR_STORM,"Teaches a Pokemon this move."),
	TM48(148,0,Color.BLACK,Item.TMS,Move.SCALD,"Teaches a Pokemon this move."),
	TM49(149,1500,Color.BLACK,Item.TMS,Move.REST,"Teaches a Pokemon this move."),
	TM50(150,1250,Color.BLACK,Item.TMS,Move.TOXIC,"Teaches a Pokemon this move."),
	TM51(151,400,Color.BLACK,Item.TMS,Move.SLEEP_TALK,"Teaches a Pokemon this move."),
	TM52(152,0,Color.BLACK,Item.TMS,Move.AERIAL_ACE,"Teaches a Pokemon this move."),
	TM53(153,0,Color.BLACK,Item.TMS,Move.VOLT_SWITCH,"Teaches a Pokemon this move."),
	TM54(154,750,Color.BLACK,Item.TMS,Move.THUNDER_WAVE,"Teaches a Pokemon this move."),
	TM55(155,0,Color.BLACK,Item.TMS,Move.MAGIC_BLAST,"Teaches a Pokemon this move."),
	TM56(156,0,Color.BLACK,Item.TMS,Move.SPARKLE_STRIKE,"Teaches a Pokemon this move."),
	TM57(157,0,Color.BLACK,Item.TMS,Move.CHARGE_BEAM,"Teaches a Pokemon this move."),
	TM58(158,0,Color.BLACK,Item.TMS,Move.DRAGON_PULSE,"Teaches a Pokemon this move."),
	TM59(159,0,Color.BLACK,Item.TMS,Move.BRICK_BREAK,"Teaches a Pokemon this move."),
	TM60(160,0,Color.BLACK,Item.TMS,Move.FREEZE$DRY,"Teaches a Pokemon this move."),
	TM61(161,0,Color.BLACK,Item.TMS,Move.SMACK_DOWN,"Teaches a Pokemon this move."),
	TM62(162,0,Color.BLACK,Item.TMS,Move.BUG_BUZZ,"Teaches a Pokemon this move."),
	TM63(163,1500,Color.BLACK,Item.TMS,Move.THUNDER,"Teaches a Pokemon this move."),
	TM64(164,1500,Color.BLACK,Item.TMS,Move.CLOSE_COMBAT,"Teaches a Pokemon this move."),
	TM65(165,0,Color.BLACK,Item.TMS,Move.SHADOW_CLAW,"Teaches a Pokemon this move."),
	TM66(166,2000,Color.BLACK,Item.TMS,Move.DRACO_METEOR,"Teaches a Pokemon this move."),
	TM67(167,2000,Color.BLACK,Item.TMS,Move.OUTRAGE,"Teaches a Pokemon this move."),
	TM68(168,0,Color.BLACK,Item.TMS,Move.FLASH,"Teaches a Pokemon this move."),
	TM69(169,0,Color.BLACK,Item.TMS,Move.ROCK_POLISH,"Teaches a Pokemon this move."),
	TM70(170,1500,Color.BLACK,Item.TMS,Move.HYDRO_PUMP,"Teaches a Pokemon this move."),
	TM71(171,0,Color.BLACK,Item.TMS,Move.STONE_EDGE,"Teaches a Pokemon this move."),
	TM72(172,0,Color.BLACK,Item.TMS,Move.ICE_SPINNER,"Teaches a Pokemon this move."),
	TM73(173,0,Color.BLACK,Item.TMS,Move.GYRO_BALL,"Teaches a Pokemon this move."),
	TM74(174,900,Color.BLACK,Item.TMS,Move.SUNNY_DAY,"Teaches a Pokemon this move."),
	TM75(175,900,Color.BLACK,Item.TMS,Move.RAIN_DANCE,"Teaches a Pokemon this move."),
	TM76(176,700,Color.BLACK,Item.TMS,Move.SNOWSCAPE,"Teaches a Pokemon this move."),
	TM77(177,700,Color.BLACK,Item.TMS,Move.SANDSTORM,"Teaches a Pokemon this move."),
	TM78(178,800,Color.BLACK,Item.TMS,Move.SWORDS_DANCE,"Teaches a Pokemon this move."),
	TM79(179,300,Color.BLACK,Item.TMS,Move.GRASSY_TERRAIN,"Teaches a Pokemon this move."),
	TM80(180,300,Color.BLACK,Item.TMS,Move.ELECTRIC_TERRAIN,"Teaches a Pokemon this move."),
	TM81(181,300,Color.BLACK,Item.TMS,Move.PSYCHIC_TERRAIN,"Teaches a Pokemon this move."),
	TM82(182,300,Color.BLACK,Item.TMS,Move.SPARKLING_TERRAIN,"Teaches a Pokemon this move."),
	TM83(183,0,Color.BLACK,Item.TMS,Move.CAPTIVATE,"Teaches a Pokemon this move."),
	TM84(184,0,Color.BLACK,Item.TMS,Move.DARK_PULSE,"Teaches a Pokemon this move."),
	TM85(185,0,Color.BLACK,Item.TMS,Move.ROCK_SLIDE,"Teaches a Pokemon this move."),
	TM86(186,0,Color.BLACK,Item.TMS,Move.X$SCISSOR,"Teaches a Pokemon this move."),
	TM87(187,0,Color.BLACK,Item.TMS,Move.POISON_JAB,"Teaches a Pokemon this move."),
	TM88(188,0,Color.BLACK,Item.TMS,Move.GALAXY_BLAST,"Teaches a Pokemon this move."),
	TM89(189,0,Color.BLACK,Item.TMS,Move.ACROBATICS,"Teaches a Pokemon this move."),
	TM90(190,0,Color.BLACK,Item.TMS,Move.IRON_BLAST,"Teaches a Pokemon this move."),
	TM91(191,1000,Color.BLACK,Item.TMS,Move.TRI$ATTACK,"Teaches a Pokemon this move."),
	TM92(192,0,Color.BLACK,Item.TMS,Move.COMET_CRASH,"Teaches a Pokemon this move."),
	TM93(193,0,Color.BLACK,Item.TMS,Move.EARTH_POWER,"Teaches a Pokemon this move."),
	TM94(194,0,Color.BLACK,Item.TMS,Move.HURRICANE,"Teaches a Pokemon this move."),
	TM95(195,500,Color.BLACK,Item.TMS,Move.TRICK_ROOM,"Teaches a Pokemon this move."),
	TM96(196,0,Color.BLACK,Item.TMS,Move.ENERGY_BALL,"Teaches a Pokemon this move."),
	TM97(197,0,Color.BLACK,Item.TMS,Move.SPIRIT_BREAK,"Teaches a Pokemon this move."),
	TM98(198,0,Color.BLACK,Item.TMS,Move.FLIP_TURN,"Teaches a Pokemon this move."),
	TM99(199,0,Color.BLACK,Item.TMS,Move.RETURN,"Teaches a Pokemon this move."),
	CALCULATOR(200,0,Color.BLACK,Item.OTHER,null,"Calculates damage simulating a battle"),
	BLACK_BELT(201,0,PType.FIGHTING.getColor(),Item.HELD_ITEM,null,"A belt that helps with focus and boosts the power of the holder's Fighting-type moves."),
	BLACK_GLASSES(202,0,PType.DARK.getColor(),Item.HELD_ITEM,null,"A pair of shady- looking glasses that boost the power of the holder's Dark-type moves."),
	CHARCOAL(203,0,PType.FIRE.getColor(),Item.HELD_ITEM,null,"A combustible fuel that boosts the power of the holder's Fire-type moves."),
	COSMIC_CORE(204,0,PType.GALACTIC.getColor(),Item.HELD_ITEM,null,"A mysterious core from the heart of the cosmos that boosts the power of Galactic-type moves."),
	DRAGON_FANG(205,0,PType.DRAGON.getColor(),Item.HELD_ITEM,null,"This hard, sharp fang boosts the power of the holder's Dragon-type moves."),
	ENCHANTED_AMULET(206,0,PType.MAGIC.getColor(),Item.HELD_ITEM,null,"An ancient amulet with mystical properties that boosts the power of Magic-type moves."),
	GLOWING_PRISM(207,0,PType.LIGHT.getColor(),Item.HELD_ITEM,null,"A radiant prism that boosts the power of Light-type moves."),
	HARD_STONE(208,0,PType.ROCK.getColor(),Item.HELD_ITEM,null,"A durable stone that boosts the power of the holder's Rock-type moves."),
	MAGNET(209,0,PType.ELECTRIC.getColor(),Item.HELD_ITEM,null,"A powerful magnet that boosts the power of the holder's Electric-type moves."),
	METAL_COAT(210,0,PType.STEEL.getColor(),Item.HELD_ITEM,null,"A special metallic coating that boosts the power of the holder's Steel-type moves."),
	MIRACLE_SEED(211,0,PType.GRASS.getColor(),Item.HELD_ITEM,null,"A seed imbued with life-force that boosts the power of the holder's Grass-type moves."),
	MYSTIC_WATER(212,0,PType.WATER.getColor(),Item.HELD_ITEM,null,"A teardrop-shaped gem boosts the power of the holder's Water-type moves."),
	NEVER$MELT_ICE(213,0,PType.ICE.getColor(),Item.HELD_ITEM,null,"A heat-repelling piece of ice that boosts the power of the holder's Ice- type moves."),
	POISON_BARB(214,0,PType.POISON.getColor(),Item.HELD_ITEM,null,"A small poisonous barb boosts the power of the holder's Poison-type moves."),
	SHARP_BEAK(215,0,PType.FLYING.getColor(),Item.HELD_ITEM,null,"A long, sharp beak that boosts the power of the holder's Flying-type moves."),
	SILK_SCARF(216,0,PType.NORMAL.getColor(),Item.HELD_ITEM,null,"A sumptuous scarf that boosts the power of the holder's Normal-type moves."),
	SILVER_POWDER(217,0,PType.BUG.getColor(),Item.HELD_ITEM,null,"A pile of shiny silver powder that boosts the power of the holder's Bug-type moves."),
	SOFT_SAND(218,0,PType.GROUND.getColor(),Item.HELD_ITEM,null,"A loose, silky sand that boosts the power of the holder's Ground-type moves."),
	SPELL_TAG(219,0,PType.GHOST.getColor(),Item.HELD_ITEM,null,"A sinister, eerie tag that boosts the power of the holder's Ghost-type moves."),
	TWISTED_SPOON(220,0,PType.PSYCHIC.getColor(),Item.HELD_ITEM,null,"This spoon is imbued with telekinetic energy and boosts the power of the holder's Psychic-type moves."),
	CHERI_BERRY(221,10,new Color(232, 96, 80),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to free itself from paralysis."),
	CHESTO_BERRY(222,10,new Color(144, 112, 224),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to wake itself from sleep."),
	PECHA_BERRY(223,10,new Color(248, 192, 152),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to lift the effects of being poisoned from itself."),
	RAWST_BERRY(224,10,new Color(144, 208, 208),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to cure itself of a burn."),
	ASPEAR_BERRY(225,10,new Color(240, 224, 80),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to thaw itself from a frostbite."),
	LUM_BERRY(226,100,new Color(144, 216, 72),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to cure itself of any status condition it may have."),
	PERSIM_BERRY(227,10,new Color(224, 152, 112),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to cure itself of confusion."),
	LEPPA_BERRY(228,75,new Color(200, 72, 48),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to restore 10 PP to a move."),
	ORAN_BERRY(229,25,new Color(80, 160, 240),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to restore 10 HP to itself."),
	SITRUS_BERRY(230,50,new Color(248, 232, 104),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to restore 25% of HP to itself."),
	WIKI_BERRY(231,75,new Color(144, 112, 224),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to restore 33% HP should it find itself in a pinch."),
	OCCA_BERRY(232,50,PType.FIRE.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Fire-type move, the power of that move will be weakened."),
	PASSHO_BERRY(233,50,PType.WATER.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Water-type move, the power of that move will be weakened."),
	WACAN_BERRY(234,50,PType.ELECTRIC.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Electric-type move, the power of that move will be weakened."),
	RINDO_BERRY(235,50,PType.GRASS.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Rindo-type move, the power of that move will be weakened."),
	YACHE_BERRY(236,50,PType.ICE.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Ice-type move, the power of that move will be weakened."),
	CHOPLE_BERRY(237,50,PType.FIGHTING.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Fighting-type move, the power of that move will be weakened."),
	KEBIA_BERRY(238,50,PType.POISON.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Poison-type move, the power of that move will be weakened."),
	SHUCA_BERRY(239,50,PType.GROUND.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Ground-type move, the power of that move will be weakened."),
	COBA_BERRY(240,50,PType.FLYING.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Flying-type move, the power of that move will be weakened."),
	PAYAPA_BERRY(241,50,PType.PSYCHIC.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Psychic-type move, the power of that move will be weakened."),
	TANGA_BERRY(242,50,PType.BUG.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Bug-type move, the power of that move will be weakened."),
	CHARTI_BERRY(243,50,PType.ROCK.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Rock-type move, the power of that move will be weakened."),
	KASIB_BERRY(244,50,PType.GHOST.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Ghost-type move, the power of that move will be weakened."),
	HABAN_BERRY(245,50,PType.DRAGON.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Dragon-type move, the power of that move will be weakened."),
	COLBUR_BERRY(246,50,PType.DARK.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Dark-type move, the power of that move will be weakened."),
	BABIRI_BERRY(247,50,PType.STEEL.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Steel-type move, the power of that move will be weakened."),
	CHILAN_BERRY(248,50,PType.NORMAL.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Normal-type move, the power of that move will be weakened."),
	ROSELI_BERRY(249,50,PType.LIGHT.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Light-type move, the power of that move will be weakened."),
	MYSTICOLA_BERRY(250,50,PType.MAGIC.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Magic-type move, the power of that move will be weakened."),
	GALAXEED_BERRY(251,50,PType.GALACTIC.getColor(),Item.BERRY,null,"If a Pokémon holding this Berry is hit with a supereffective Galactic-type move, the power of that move will be weakened."),
	LIECHI_BERRY(252,150,new Color(248, 224, 160),Item.BERRY,null,"If a Pokémon holds one of these Berries, its Attack stat will be boosted should it find itself in a pinch."),
	GANLON_BERRY(253,75,new Color(152, 144, 200),Item.BERRY,null,"If a Pokémon holds one of these Berries, its Defense stat will be boosted should it find itself in a pinch."),
	SALAC_BERRY(254,200,new Color(120, 184, 112),Item.BERRY,null,"If a Pokémon holds one of these Berries, its Speed stat will be boosted should it find itself in a pinch."),
	PETAYA_BERRY(255,150,new Color(240, 160, 120),Item.BERRY,null,"If a Pokémon holds one of these Berries, its Sp. Atk stat will be boosted should it find itself in a pinch."),
	APICOT_BERRY(256,75,new Color(104, 128, 184),Item.BERRY,null,"If a Pokémon holds one of these Berries, its Sp. Def stat will be boosted should it find itself in a pinch."),
	STARF_BERRY(257,500,new Color(184, 232, 152),Item.BERRY,null,"If a Pokémon holds one of these Berries, one of its stats will be sharply boosted should it find itself in a pinch."),
	MICLE_BERRY(258,200,new Color(64, 200, 64),Item.BERRY,null,"If a Pokémon holds one of these Berries, its accuracy will be boosted should it find itself in a pinch."),
	CUSTAP_BERRY(259,200,new Color(220, 96, 70),Item.BERRY,null,"If a Pokémon holds one of these Berries, it will be able to act faster should it find itself in a pinch—but only for the next move it uses."),
	NULL260(260,0,Color.BLACK,Item.BERRY,null,""),
	;
	
	private int id;
	private Color color;
	private int pocket;
	private Move move;
	private int cost;
	private int healAmount;
	private String desc;
	private BufferedImage image;
	
	public static final int MEDICINE = 1;
    public static final int OTHER = 2;
    public static final int TMS = 3;
    public static final int HELD_ITEM = 4;
    public static final int BERRY = 5;
	
	Item(int id, int cost, Color color, int pocket, Move move, String desc) {
		this.id = id;
		this.cost = cost;
		this.color = color;
		this.pocket = pocket;
		this.move = move;
		this.desc = desc;
		
		String path = "/items/";
		path += isTM() ? "tm_" + getMove().mtype.toString().toLowerCase() : super.toString().toLowerCase();
		if (isMint()) path = path.replace("_mint", "");
		image = setupImage(path + ".png");
		
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
	
	private BufferedImage setupImage(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/items/null.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}

	public int getCost() { return cost; }

	public int getID() { return id; }
	
	public Move getMove() { return move; }
	
	public int getPocket() { return pocket; }
	
	public Color getColor() {
		if (getMove() != null) return getMove().mtype.getColor();
		return color;
	}
	
	public String getDesc() {
		return Item.breakString(desc, 65);
	}
	
	public BufferedImage getImage() { return image; }
	
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
	    	name = name.replace('$', '-');
	        name = name.replace('1', '\'');
	        name = name.toLowerCase().replace('_', ' ');

	        StringBuilder sb = new StringBuilder();
	        boolean capitalizeNext = true; // Flag to capitalize the next word
	        for (char c : name.toCharArray()) {
	            if (c == ' ' || c == '-') {
	                sb.append(c); // Keep the space or hyphen
	                capitalizeNext = true;
	            } else {
	                if (capitalizeNext) {
	                    sb.append(Character.toUpperCase(c));
	                    capitalizeNext = false;
	                } else {
	                    sb.append(c);
	                }
	            }
	        }
	        result = sb.toString().trim();
	        result = result.replace("Pp", "PP");
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
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true },
				{true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 016
				{true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
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
				{false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true },
				{false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true }, // 227
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
		else if (id == 14 || id == 15) return null;
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

	public void useCalc(Player p, Pokemon[] box) {
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
        JCheckBox critCheck = new JCheckBox("Crit");
        for (int k = 0; k < p.team.length; k++) {
        	if (p.team[k] != null) {
        		userMons.addItem(p.team[k].clone());
        		if (p.team[k].id == 150) {
            		Pokemon kD = p.team[k].clone();
            		int oHP = kD.getStat(0);
    				kD.id = 237;
    				if (kD.nickname == kD.name) kD.nickname = kD.getName();
    				
    				kD.setBaseStats();
    				kD.getStats();
    				kD.weight = kD.setWeight();
    				int nHP = kD.getStat(0);
    				kD.currentHP += nHP - oHP;
    				kD.setType();
    				kD.setAbility(kD.abilitySlot);
    				userMons.addItem(kD);
            	}
        	}
        }
        if (box != null) {
			for (Pokemon q : box) {
				if (q != null) userMons.addItem(q.clone());
			}
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
        JCheckBox fCritCheck = new JCheckBox("Crit");
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
        
        JLabel abilityLabel = new JLabel(((Pokemon) userMons.getSelectedItem()).ability.toString());
        JLabel fAbilityLabel = new JLabel(((Pokemon) foeMons.getSelectedItem()).ability.toString());
        JButton infoButton = new JButton("   Info   ");
        JButton fInfoButton = new JButton("   Info   ");
        
        ArrayList<Item> items = new ArrayList<>();
        Item[] allItems = Item.values();
        items.add(null);
        for (int i = 47; i < 89; i++) {
        	items.add(allItems[i]);
        }
        for (int i = 201; i < 259; i++) {
        	items.add(allItems[i]);
        }
        
        abilityLabel.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, ((Pokemon) userMons.getSelectedItem()).ability.desc);
            }
		});
        
        fAbilityLabel.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, ((Pokemon) foeMons.getSelectedItem()).ability.desc);
            }
		});
        
        infoButton.addActionListener(e -> {
        	JOptionPane.showMessageDialog(null, ((Pokemon) userMons.getSelectedItem()).showSummary(p, false, null, null), "Pokemon details", JOptionPane.PLAIN_MESSAGE);
        });
        
        fInfoButton.addActionListener(e -> {
        	JOptionPane.showMessageDialog(null, ((Pokemon) foeMons.getSelectedItem()).showSummary(p, false, null, null), "Pokemon details", JOptionPane.PLAIN_MESSAGE);
        });
        
        JComboBox<Item> userItem = new JComboBox<>((Item[]) items.toArray(new Item[1]));
        JComboBox<Item> foeItem = new JComboBox<>((Item[]) items.toArray(new Item[1]));
        
        userItem.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	userCurrent.item = (Item) userItem.getSelectedItem();
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        });
        
        foeItem.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	foeCurrent.item = (Item) foeItem.getSelectedItem();
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
        });
        
        AutoCompleteDecorator.decorate(userItem);
        AutoCompleteDecorator.decorate(foeItem);
        
        userMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
            userLevel.setText(userCurrent.getLevel() + "");
            updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        });
        
        foeMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	foeLevel.setText(foeCurrent.getLevel() + "");
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
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
        	foeCurrent.verifyHP();
        	foeCurrent.setMoves();
        	
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
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
        			updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
        			updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
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
        			updateMoves(current, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), abilityLabel, foeItem);
        			updateMoves(userCurrent, userMoves, userDamage, current, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), fAbilityLabel, userItem);
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
        updateMoves(userC, userMoves, userDamage, foeC, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
        
        foeLevel.setText(foeC.getLevel() + "");
        updateMoves(foeC, foeMoves, foeDamage, userC, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        
        calc.add(statsPanel, gbc);
        gbc.gridx++;
        calc.add(fStatsPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel abilityLabelPanel = new JPanel();
        abilityLabelPanel.add(infoButton);
        abilityLabelPanel.add(abilityLabel);
        
        JPanel fAbilityLabelPanel = new JPanel();
        fAbilityLabelPanel.add(fAbilityLabel);
        fAbilityLabelPanel.add(fInfoButton);
        calc.add(abilityLabelPanel, gbc);
        gbc.gridx++;
        calc.add(fAbilityLabelPanel, gbc);
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
        
        calc.add(userItem, gbc);
        gbc.gridx++;
        calc.add(foeItem, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        critCheck.addActionListener(e -> {
        	Pokemon current = ((Pokemon) userMons.getSelectedItem());
			Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
			updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
        });
        
        fCritCheck.addActionListener(e -> {
        	Pokemon current = ((Pokemon) userMons.getSelectedItem());
			Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
			updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        });
        
        JPanel resetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	for (int i = 0; i < 4; i++) {
        		uCurrent.moveset[i] = p.team[userMons.getSelectedIndex()].moveset[i];
        	}
        	updateMoves(uCurrent, userMoves, userDamage, fCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), abilityLabel, userItem);
        });
        resetButtonPanel.add(resetButton);
        resetButtonPanel.add(critCheck);
        calc.add(resetButtonPanel, gbc);
        gbc.gridx++;
        
        JPanel fResetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton fResetButton = new JButton("Reset");
        fResetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	fCurrent.setMoves();
        	updateMoves(fCurrent, foeMoves, foeDamage, uCurrent, foeStatLabels, foeStages, foeSpeed, null, null, fCritCheck.isSelected(), fAbilityLabel, foeItem);
        });
        fResetButtonPanel.add(fResetButton);
        fResetButtonPanel.add(fCritCheck);
        calc.add(fResetButtonPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JOptionPane.showMessageDialog(null, calc, "Damage Calculator", JOptionPane.PLAIN_MESSAGE);
		
	}
	
	private void updateMoves(Pokemon current, JGradientButton[] moves, JLabel[] damages, Pokemon foe, JLabel[] statLabels, JComboBox<Integer>[] stages,
			JLabel speed, JButton currentHP, JLabel HPP, boolean crit, JLabel currentAbility, JComboBox<Item> currentItem) {
        for (int k = 0; k < moves.length; k++) {
        	if (current.moveset[k] != null) {
        		moves[k].setText(current.moveset[k].move.toString());
        		moves[k].setBackground(current.moveset[k].move.mtype.getColor());
        		if (current.moveset[k].move == Move.HIDDEN_POWER) moves[k].setBackground(current.determineHPType().getColor());
        		int minDamage = current.calcWithTypes(foe, current.moveset[k].move, current.getFaster(foe, 0, 0) == current, -1, crit);
        		int maxDamage = current.calcWithTypes(foe, current.moveset[k].move, current.getFaster(foe, 0, 0) == current, 1, crit);
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
			    		updateMoves(current, moves, damages, foe, statLabels, stages, speed, currentHP, HPP, crit, currentAbility, currentItem);
			    	}
			    }
            });    		
        }
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
        currentAbility.setText(current.ability.toString());
        currentItem.setSelectedItem(current.item);
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

	public boolean isMint() {
		return getID() >= 29 && getID() <= 39;
	}

	public boolean isTM() {
		return getMove() != null;
	}

	public boolean isEvoItem() {
		return getID() >= 20 && getID() < 26;
	}

	public boolean isStatusHealer() {
		return (getID() > 8 && getID() < 16) || (getID() > 220 && getID() < 227);
	}

	public boolean isPotion() {
		return getHealAmount() != 0;
	}
	
	public boolean isChoiceItem() {
		return (getID() >= 47 && getID() <= 49);
	}
	
	public boolean isResistBerry() {
		return (getID() >= 232 && getID() <= 251);
	}
	
	public boolean isBerry() {
		return (getID() >= 221 && getID() <= 259);
	}
	
	public boolean isPinchBerry() {
		return (getID() == 231 || (getID() >= 252 && getID() <= 259));
	}
	
	public static String breakString(String input, int maxChar) {
        if (input == null || maxChar <= 0) {
            return null; // Or handle this case according to your requirements
        }

        StringBuilder result = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();
        int currentLength = 0;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                // If adding the current character exceeds the limit, start a new line
                if (currentLength + currentLine.length() > maxChar) {
                    result.append(currentLine.toString().trim()).append("\n");
                    currentLine.setLength(0);
                    currentLength = 0;
                } else {
                    currentLine.append(c);
                    currentLength++;
                }
            } else {
                currentLine.append(c);
                currentLength++;
            }
        }

        // Append the remaining part if any
        if (currentLine.length() > 0) {
            result.append(currentLine.toString().trim());
        }

        return result.toString();
    }
}
