package pokemon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import pokemon.Field.Effect;
import util.Pair;

public enum Item {
	REPEL(0,10,5,new Color(0, 92, 5),Item.OTHER,null,"Prevents wild Pokemon encounters for 200 steps."),
	POKEBALL(1,10,5,new Color(176, 0, 12),Item.BALLS,null,"A device for catching wild Pokemon. It's thrown like a ball at a Pokemon, comfortably encapsulating its target."),
	GREAT_BALL(2,25,12,new Color(0, 0, 148),Item.BALLS,null,"A good, high-performance Poke Ball that provides a higher success rate for catching Pokemon than a standard Poke Ball."),
	ULTRA_BALL(3,50,25,new Color(148, 171, 0),Item.BALLS,null,"An ultra high-performance Poke Ball that provides a higher success rate for catching Pokemon than a Great Ball."),
	MASTER_BALL(350,0,10000,Color.BLACK,Item.BALLS,null,"The very best Poke Ball with the ultimate level of performance. With it, you will catch any wild Pokemon without fail."),
	TEMPLE_BALL(385,0,0,Color.BLACK,Item.BALLS,null,"A rare and ancient Poke Ball filled with mystique, you can catch any wild Pokemon without fail."),
	CHERISH_BALL(351,0,1000,Color.BLACK,Item.BALLS,null,"A quite rare Poke Ball made to commemorate a special occasion of some sort."),
	PREMIER_BALL(352,0,5,Color.BLACK,Item.BALLS,null,"A somewhat rare Poke Ball made to commemorate a special occasion of some sort."),
	HEAL_BALL(353,15,7,Color.BLACK,Item.BALLS,null,"A remedial Poke Ball that fully heals a Pokemon caught with it."),
	DUSK_BALL(354,20,10,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon in dark places, such as caves or buildings."),
	QUICK_BALL(355,50,25,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective at catching Pokemon when used first thing in a battle."),
	TIMER_BALL(356,50,25,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that becomes more effective at catching Pokemon the more turns that are taken in battle."),
	REPEAT_BALL(357,50,25,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching a Pokemon of a species that you've caught before."),
	LUXURY_BALL(358,150,75,Color.BLACK,Item.BALLS,null,"A particularly comfortable Poke Ball that makes a wild Pokemon quickly grow friendlier after being caught."),
	NEST_BALL(359,30,15,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that becomes more effective the lower the level of the wild Pokemon."),
	DIVE_BALL(360,50,25,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon in or on the water."),
	NET_BALL(361,50,25,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Water- or Bug-type Pokemon."),
	HEAVY_BALL(362,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that becomes more effective the heavier the Pokemon is."),
	LURE_BALL(363,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon in or on the water."),
	FRIEND_BALL(364,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that makes a wild Pokemon more friendly toward you immediately after it's caught."),
	LOVE_BALL(365,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon of the same species as yours."),
	LEVEL_BALL(366,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that becomes more effective the lower the level of the Pokemon compared to your own Pokemon."),
	FAST_BALL(367,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon that are very quick."),
	MOON_BALL(368,0,250,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon that can be evolved using a Dawn or Dusk Stone."),
	DREAM_BALL(369,0,500,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is more effective when catching Pokemon that are asleep."),
	BEAST_BALL(370,0,1000,Color.BLACK,Item.BALLS,null,"A somewhat different Poke Ball that is only effective at catching Ultra Paradox Pokemon."),
	POTION(4,25,12,new Color(124, 0, 219),Item.MEDICINE,null,"Restores 20 HP to a Pokemon."),
	SUPER_POTION(5,60,30,new Color(140, 24, 8),Item.MEDICINE,null,"Restores 60 HP to a Pokemon."),
	HYPER_POTION(6,150,75,new Color(255, 0, 191),Item.MEDICINE,null,"Restores 200 HP."),
	MAX_POTION(7,200,100,new Color(0, 21, 255),Item.MEDICINE,null,"Restores a Pokemon's HP to full."),
	FULL_RESTORE(8,300,150,new Color(255, 196, 0),Item.MEDICINE,null,"Restores a Pokemon's HP to full and cures any status conditions."),
	ANTIDOTE(9,10,5,new Color(157, 0, 255),Item.MEDICINE,null,"Cures a Pokemon of Poison."),
	AWAKENING(10,10,5,new Color(63, 83, 92),Item.MEDICINE,null,"Cures a Pokemon of Sleep."),
	BURN_HEAL(11,10,5,new Color(133, 15, 19),Item.MEDICINE,null,"Cures a Pokemon of Burn."),
	PARALYZE_HEAL(12,10,5,new Color(176, 158, 0),Item.MEDICINE,null,"Cures a Pokemon of Paralysis."),
	FREEZE_HEAL(13,10,5,new Color(0, 170, 189),Item.MEDICINE,null,"Cures a Pokemon of Frostbite."),
	FULL_HEAL(14,100,50,new Color(255, 247, 0),Item.MEDICINE,null,"Cures a Pokemon of any status condition."),
	KLEINE_BAR(15,40,20,new Color(0, 55, 255),Item.MEDICINE,null,"Cures a Pokemon of any status condition."),
	REVIVE(16,500,250,new Color(219, 194, 0),Item.MEDICINE,null,"Recovers a Pokemon from fainting with 50% HP."),
	MAX_REVIVE(17,1000,500,new Color(219, 194, 0),Item.MEDICINE,null,"Recovers a Pokemon from fainting with full HP."),
	RARE_CANDY(18,0,600,new Color(124, 54, 255),Item.MEDICINE,null,"Elevates a Pokemon by 1 level."),
	ELIXIR(40,200,100,new Color(230, 146, 78),Item.MEDICINE,null,"Restores PP of a selected move."),
	MAX_ELIXIR(41,250,200,new Color(246, 255, 120),Item.MEDICINE,null,"Restores PP of all moves on a Pokemon."),
	PP_UP(42,2500,1000,new Color(150, 51, 156),Item.MEDICINE,null,"Increases max PP of a selected move by 20%."),
	PP_MAX(43,0,1500,new Color(142, 230, 21),Item.MEDICINE,null,"Increases max PP of a selected move by its max PP, which is 60%."),
	TEMPLE_ORB(386,0,0,Color.BLACK,Item.OTHER,null,"A rare orb used by an ancient diety as currency for gambling."),
	RUSTY_BOTTLE_CAP(282,300,150,Color.BLACK,Item.OTHER,null,"Lowers an IV of choosing to 0."),
	BOTTLE_CAP(27,1000,500,new Color(192, 192, 192),Item.OTHER,null,"Maxes out an IV of choosing."),
	GOLD_BOTTLE_CAP(28,0,1000,new Color(255, 215, 0),Item.OTHER,null,"Maxes out all IVs of a Pokemon."),
	EUPHORIAN_GEM(19,500,250,new Color(138, 237, 255),Item.OTHER,null,"Grants a Pokemon 100 friendship points."),
	LEAF_STONE(20,1000,500,new Color(0, 120, 20),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	FIRE_STONE(264,1000,500,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	WATER_STONE(283,1000,500,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	DUSK_STONE(21,1000,500,new Color(64, 64, 64),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	DAWN_STONE(22,1000,500,new Color(0, 176, 179),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	ICE_STONE(23,1000,500,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	VALIANT_GEM(24,2000,500,new Color(72, 75, 219),Item.OTHER,null,"Grants Masculine energy to a Pokemon, evolving them into their male evolution."),
	PETTICOAT_GEM(25,2000,500,new Color(204, 61, 140),Item.OTHER,null,"Grants Feminine energy to a Pokemon, evolving them into their female evolution."),
	RAZOR_CLAW(265,1000,500,new Color(176, 244, 245),Item.OTHER,null,"Evolves a certain species of Pokemon."),
	ABILITY_CAPSULE(26,5000,1250,new Color(102, 7, 143),Item.OTHER,null,"Swaps a Pokemon's ability with its other possible ability."),
	ABILITY_PATCH(91,10000,1500,Color.BLACK,Item.OTHER,null,"A patch that can be used to change the regular Ability of a Pokemon to a rarer Ability."),
	LONELY_MINT(311,500,150,Color.BLACK,Item.MEDICINE,Nature.LONELY,"Changes a Pokemon's nature to +Atk, -Def"),
	ADAMANT_MINT(29,2500,1000,new Color(113, 84, 255),Item.MEDICINE,Nature.ADAMANT,"Changes a Pokemon's nature to +Atk, -SpA"),
	NAUGHTY_MINT(312,500,150,Color.BLACK,Item.MEDICINE,Nature.NAUGHTY,"Changes a Pokemon's nature to +Atk, -SpD"),
	BRAVE_MINT(31,1000,250,new Color(113, 84, 255),Item.MEDICINE,Nature.BRAVE,"Changes a Pokemon's nature to +Atk, -Spe"),
	BOLD_MINT(30,1750,750,new Color(113, 84, 255),Item.MEDICINE,Nature.BOLD,"Changes a Pokemon's nature to +Def, -Atk"),
	IMPISH_MINT(34,1750,750,new Color(113, 84, 255),Item.MEDICINE,Nature.IMPISH,"Changes a Pokemon's nature to +Def, -SpA"),
	LAX_MINT(313,750,200,Color.BLACK,Item.MEDICINE,Nature.LAX,"Changes a Pokemon's nature to +Def, -SpD"),
	RELAXED_MINT(314,1000,250,Color.BLACK,Item.MEDICINE,Nature.RELAXED,"Changes a Pokemon's nature to +Def, -Spe"),
	MODEST_MINT(36,2250,1000,new Color(113, 84, 255),Item.MEDICINE,Nature.MODEST,"Changes a Pokemon's nature to +SpA, -Atk"),
	MILD_MINT(315,500,150,Color.BLACK,Item.MEDICINE,Nature.MILD,"Changes a Pokemon's nature to +SpA, -Def"),
	RASH_MINT(316,500,150,Color.BLACK,Item.MEDICINE,Nature.RASH,"Changes a Pokemon's nature to +SpA, -SpD"),
	QUIET_MINT(37,1000,250,new Color(113, 84, 255),Item.MEDICINE,Nature.QUIET,"Changes a Pokemon's nature to +SpA, -Spe"),
	CALM_MINT(32,1500,500,new Color(113, 84, 255),Item.MEDICINE,Nature.CALM,"Changes a Pokemon's nature to +SpD, -Atk"),
	GENTLE_MINT(317,750,200,Color.BLACK,Item.MEDICINE,Nature.GENTLE,"Changes a Pokemon's nature to +SpD, -Def"),
	CAREFUL_MINT(33,1250,500,new Color(113, 84, 255),Item.MEDICINE,Nature.CAREFUL,"Changes a Pokemon's nature to +SpD, -SpA"),
	SASSY_MINT(318,1000,250,Color.BLACK,Item.MEDICINE,Nature.SASSY,"Changes a Pokemon's nature to +SpD, -Spe"),
	TIMID_MINT(39,2500,1250,new Color(113, 84, 255),Item.MEDICINE,Nature.TIMID,"Changes a Pokemon's nature to +Spe, -Atk"),
	HASTY_MINT(319,2000,900,Color.BLACK,Item.MEDICINE,Nature.HASTY,"Changes a Pokemon's nature to +Spe, -Def"),
	JOLLY_MINT(35,2500,1250,new Color(113, 84, 255),Item.MEDICINE,Nature.JOLLY,"Changes a Pokemon's nature to +Spe, -SpA"),
	NAIVE_MINT(320,2000,900,Color.BLACK,Item.MEDICINE,Nature.NAIVE,"Changes a Pokemon's nature to +Spe, -SpD"),
	SERIOUS_MINT(38,1250,300,new Color(113, 84, 255),Item.MEDICINE,Nature.SERIOUS,"Changes a Pokemon's nature to Neutral"),
	THUNDER_SCALES_FOSSIL(45,5000,1000,new Color(201, 169, 81),Item.OTHER,null,"A fossil of an ancient Pokemon that lived in a desert. It appears to have an electric charge ridden in the scales."),
	DUSK_SCALES_FOSSIL(46,5000,1000,new Color(45, 47, 51),Item.OTHER,null,"A fossil of an ancient Pokemon that lived in a forest. It appears to give off a dark energy within the scales."),
	CHOICE_BAND(47,0,0,new Color(224, 152, 159),Item.HELD_ITEM,null,"A curious headband that boosts the holder's Attack stat but only allows the use of a single move."),
	CHOICE_SCARF(48,0,0,new Color(133, 172, 220),Item.HELD_ITEM,null,"A curious scarf that boosts the holder's Speed stat but only allows the use of a single move."),
	CHOICE_SPECS(49,0,0,new Color(238, 236, 100),Item.HELD_ITEM,null,"A pair of curious glasses that boost the holder's Sp. Atk stat but only allow the use of a single move."),
	LEFTOVERS(50,0,0,new Color(227, 96, 91),Item.HELD_ITEM,null,"An item that restores the user's HP gradually throughout a battle."),
	BLACK_SLUDGE(51,0,0,new Color(144, 138, 169),Item.HELD_ITEM,null,"If the holder is a Poison type, this sludge will gradually restore its HP. It damages any other type."),
	EVIOLITE(52,0,0,new Color(186, 141, 190),Item.HELD_ITEM,null,"A mysterious evolutionary lump that boosts the Defense and Sp. Def stats when held by a Pokemon that can still evolve."),
	EVERSTONE(53,100,0,new Color(179, 200, 210),Item.HELD_ITEM,null,"A Pokemon holding this peculiar stone is prevented from evolving; can't be stolen or removed."),
	DESTINY_KNOT(382,0,0,Color.BLACK,Item.HELD_ITEM,null,"An item that controls the destiny of the species of 2 Pokemon breeding."),
	EXP_SHARE(89,0,0,Color.BLACK,Item.HELD_ITEM,null,"The holder gets a share of a battle's Exp. Points without battling."),
	LUCKY_EGG(90,0,0,Color.BLACK,Item.HELD_ITEM,null,"An egg filled with happiness that earns the holder extra Exp. Points."),
	DAMP_ROCK(54,750,0,new Color(37, 99, 179),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to rain, the rain will persist for longer than usual."),
	HEAT_ROCK(55,750,0,new Color(209, 68, 61),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to harsh sunlight, the sunlight will persist for longer than usual."),
	SMOOTH_ROCK(56,600,0,new Color(166, 124, 90),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to a sandstorm, the storm will persist for longer than usual."),
	ICY_ROCK(57,600,0,new Color(130, 200, 232),Item.HELD_ITEM,null,"A rock that when the holder changes the weather to snow, the snow will persist for longer than usual."),
	ASSAULT_VEST(58,0,0,new Color(194, 80, 84),Item.HELD_ITEM,null,"An offensive vest boosts the holder's Sp. Def stat but prevents the use of status moves."),
	BRIGHT_POWDER(59,0,0,new Color(214, 234, 206),Item.HELD_ITEM,null,"A glittery powder that casts a tricky glare which lowers the accuracy of opposing Pokemon's moves."),
	EXPERT_BELT(60,0,0,new Color(94, 90, 97),Item.HELD_ITEM,null,"A well-worn belt that slightly boosts the power of the holder's supereffective moves."),
	METRONOME(345,0,0,Color.BLACK,Item.HELD_ITEM,null,"Boosts the power of a move that's used repeatedly. Once the chain is broken, the move's power returns to normal."),
	TERRAIN_EXTENDER(61,0,0,new Color(212, 228, 229),Item.HELD_ITEM,null,"A held item that extends the duration of terrain caused by the holder's move or Ability."),
	ABILITY_SHIELD(325,0,0,Color.BLACK,Item.HELD_ITEM,null,"This cute and rather unique-looking shield protects the holder from having its Ability changed by others."),
	LIFE_ORB(62,0,0,new Color(184, 72, 144),Item.HELD_ITEM,null,"An orb that boosts the power of the holder's moves, but at the cost of some HP."),
	FLAME_ORB(63,0,0,new Color(225, 3, 3),Item.HELD_ITEM,null,"A bizarre orb that gives off heat when touched and will afflict the holder with a burn during battle."),
	TOXIC_ORB(64,0,0,new Color(148, 112, 172),Item.HELD_ITEM,null,"A bizarre orb that exudes toxins when touched and will badly poison the holder during battle."),
	FROST_ORB(387,0,0,new Color(148, 112, 172),Item.HELD_ITEM,null,"A bizarre orb that gives off extreme cold when touched and will frostbite the holder during battle."),
	ROCKY_HELMET(65,0,0,new Color(241, 188, 27),Item.HELD_ITEM,null,"If another Pokemon makes direct contact with the holder, that Pokemon will be damaged."),
	PROTECTIVE_PADS(342,0,0,Color.BLACK,Item.HELD_ITEM,null,"These pads protect the holder from effects triggered by making direct contact with another Pokemon."),
	PUNCHING_GLOVE(343,0,0,Color.BLACK,Item.HELD_ITEM,null,"This protective glove boosts the power of the holder's punching moves and prevents direct contact with targets."),
	LIGHT_CLAY(66,0,0,new Color(183, 216, 126),Item.HELD_ITEM,null,"An item that when the holder uses protective moves like Light Screen or Reflect, their effects will last longer than usual."),
	SHELL_BELL(346,0,0,Color.BLACK,Item.HELD_ITEM,null,"The holder restores 25% of the damage dealt with an attack back to itself."),
	SOOTHE_BELL(67,0,0,new Color(198, 199, 202),Item.HELD_ITEM,null,"The comforting chime of this bell calms the holder, making it friendly."),
	LOADED_DICE(68,0,0,new Color(144, 232, 16),Item.HELD_ITEM,null,"This loaded dice always rolls a good number, and holding one can ensure that the holder's multistrike moves hit more times."),
	SCOPE_LENS(69,0,0,new Color(192, 183, 54),Item.HELD_ITEM,null,"A lens for scoping out weak points. It boosts the holder's critical-hit ratio."),
	WIDE_LENS(70,0,0,new Color(74, 162, 195),Item.HELD_ITEM,null,"A magnifying lens that slightly boosts the accuracy of the holder's moves."),
	ZOOM_LENS(349,0,0,Color.BLACK,Item.HELD_ITEM,null,"When the holder acts after its target, its move will be more accurate than usual."),
	QUICK_CLAW(71,0,0,new Color(213, 189, 105),Item.HELD_ITEM,null,"This light, sharp claw lets the holder move first occasionally."),
	BIG_ROOT(72,0,0,new Color(208, 161, 91),Item.HELD_ITEM,null,"This root boosts the amount of HP the holder restores to itself when it uses HP-stealing moves."),
	CLEAR_AMULET(73,0,0,new Color(204, 204, 243),Item.HELD_ITEM,null,"This clear, sparkling amulet protects the holder from having its stats lowered by moves used against it or by other Pokemon's Abilities."),
	COVERT_CLOAK(74,0,0,new Color(101, 136, 160),Item.HELD_ITEM,null,"This hooded cloak conceals the holder, tricking the eyes of its enemies and protecting it from the additional effects of moves."),
	SAFETY_GOGGLES(321,0,0,Color.BLACK,Item.HELD_ITEM,null,"These goggles protect the holder from both weather-related damage and powder."),
	HEAVY$DUTY_BOOTS(75,0,0,new Color(97, 97, 97),Item.HELD_ITEM,null,"These boots protect the holder from the effects of entry hazards set on the battlefield."),
	FOCUS_BAND(76,0,0,new Color(232, 80, 80),Item.HELD_ITEM,null,"When the holder is hit with a move that should knock it out, it may be able to endure with 1 HP."),
	KING1S_ROCK(77,0,0,new Color(224, 206, 58),Item.HELD_ITEM,null,"It may cause the target to flinch whenever the holder successfully inflicts damage on them with an attack."),
	SHED_SHELL(78,0,0,new Color(243, 241, 140),Item.HELD_ITEM,null,"Hard and sturdy, this discarded carapace enables the holder to switch out of battle without fail."),
	MUSCLE_BAND(79,0,0,new Color(225, 200, 50),Item.HELD_ITEM,null,"This headband exudes strength, slightly boosting the power of the holder's physical moves."),
	WISE_GLASSES(80,0,0,new Color(92, 105, 117),Item.HELD_ITEM,null,"This thick pair of glasses slightly boosts the power of the holder's special moves."),
	BLACK_BELT(201,0,0,PType.FIGHTING.getColor(),Item.HELD_ITEM,null,"A belt that helps with focus and boosts the power of the holder's Fighting-type moves."),
	BLACK_GLASSES(202,0,0,PType.DARK.getColor(),Item.HELD_ITEM,null,"A pair of shady- looking glasses that boost the power of the holder's Dark-type moves."),
	CHARCOAL(203,0,0,PType.FIRE.getColor(),Item.HELD_ITEM,null,"A combustible fuel that boosts the power of the holder's Fire-type moves."),
	COSMIC_CORE(204,0,0,PType.GALACTIC.getColor(),Item.HELD_ITEM,null,"A mysterious core from the heart of the cosmos that boosts the power of Galactic-type moves."),
	DRAGON_FANG(205,0,0,PType.DRAGON.getColor(),Item.HELD_ITEM,null,"This hard, sharp fang boosts the power of the holder's Dragon-type moves."),
	ENCHANTED_AMULET(206,0,0,PType.MAGIC.getColor(),Item.HELD_ITEM,null,"An ancient amulet with mystical properties that boosts the power of Magic-type moves."),
	GLOWING_PRISM(207,0,0,PType.LIGHT.getColor(),Item.HELD_ITEM,null,"A radiant prism that boosts the power of Light-type moves."),
	HARD_STONE(208,0,0,PType.ROCK.getColor(),Item.HELD_ITEM,null,"A durable stone that boosts the power of the holder's Rock-type moves."),
	MAGNET(209,0,0,PType.ELECTRIC.getColor(),Item.HELD_ITEM,null,"A powerful magnet that boosts the power of the holder's Electric-type moves."),
	METAL_COAT(210,0,0,PType.STEEL.getColor(),Item.HELD_ITEM,null,"A special metallic coating that boosts the power of the holder's Steel-type moves."),
	MIRACLE_SEED(211,0,0,PType.GRASS.getColor(),Item.HELD_ITEM,null,"A seed imbued with life-force that boosts the power of the holder's Grass-type moves."),
	MYSTIC_WATER(212,0,0,PType.WATER.getColor(),Item.HELD_ITEM,null,"A teardrop-shaped gem boosts the power of the holder's Water-type moves."),
	NEVER$MELT_ICE(213,0,0,PType.ICE.getColor(),Item.HELD_ITEM,null,"A heat-repelling piece of ice that boosts the power of the holder's Ice- type moves."),
	POISON_BARB(214,0,0,PType.POISON.getColor(),Item.HELD_ITEM,null,"A small poisonous barb boosts the power of the holder's Poison-type moves."),
	SHARP_BEAK(215,0,0,PType.FLYING.getColor(),Item.HELD_ITEM,null,"A long, sharp beak that boosts the power of the holder's Flying-type moves."),
	SILK_SCARF(216,0,0,PType.NORMAL.getColor(),Item.HELD_ITEM,null,"A sumptuous scarf that boosts the power of the holder's Normal-type moves."),
	SILVER_POWDER(217,0,0,PType.BUG.getColor(),Item.HELD_ITEM,null,"A pile of shiny silver powder that boosts the power of the holder's Bug-type moves."),
	SOFT_SAND(218,0,0,PType.GROUND.getColor(),Item.HELD_ITEM,null,"A loose, silky sand that boosts the power of the holder's Ground-type moves."),
	SPELL_TAG(219,0,0,PType.GHOST.getColor(),Item.HELD_ITEM,null,"A sinister, eerie tag that boosts the power of the holder's Ghost-type moves."),
	TWISTED_SPOON(220,0,0,PType.PSYCHIC.getColor(),Item.HELD_ITEM,null,"This spoon is imbued with telekinetic energy and boosts the power of the holder's Psychic-type moves."),
	CLEANSE_TAG(371,0,0,Color.BLACK,Item.HELD_ITEM,null,"Helps keep wild Pokemon away when the holder is the head of the party."),
	AMULET_COIN(339,0,0,Color.BLACK,Item.HELD_ITEM,null,"This coin doubles any prize money received as long as the holder joins the battle at least once."),
	FLOAT_STONE(322,0,0,Color.BLACK,Item.HELD_ITEM,null,"This very light stone reduces the weight of the holder."),
	IRON_BALL(323,0,0,Color.BLACK,Item.HELD_ITEM,null,"A ball of steel that lowers Speed and allows GROUND moves to hit FLYING and levitating holders."),
	LAGGING_TAIL(324,0,0,Color.BLACK,Item.HELD_ITEM,null,"A tremendously heavy item that makes the holder move slower than usual."),
	BINDING_BAND(340,0,0,Color.BLACK,Item.HELD_ITEM,null,"This band boosts the damage of the binding effect caused by binding moves used by the holder."),
	GRIP_CLAW(341,0,0,Color.BLACK,Item.HELD_ITEM,null,"When the holder uses moves that deal damage over several turns, such as Bind or Wrap, their effects will last longer than usual."),
	RING_TARGET(344,0,0,Color.BLACK,Item.HELD_ITEM,null,"When held, moves that would normally have no effect due to abilities or type matchups will still hit the holder."),
	STICKY_BARB(347,0,0,Color.BLACK,Item.HELD_ITEM,null,"A clingy barb damages the holder every turn and will latch on to Pokemon that make direct contact with the holder."),
	UTILITY_UMBRELLA(348,0,0,Color.BLACK,Item.HELD_ITEM,null,"This sturdy umbrella protects the holder from the effects of all weather."),
	FOCUS_SASH(81,0,400,new Color(232, 80, 80),Item.HELD_ITEM,null,"If the holder has full HP and it is hit with a move that should knock it out, it will endure with 1 HP, but only once."),
	AIR_BALLOON(82,0,300,new Color(232, 72, 72),Item.HELD_ITEM,null,"This balloon makes the holder float in the air. If the holder is hit with an attack, the balloon will burst."),
	EJECT_BUTTON(331,0,500,Color.BLACK,Item.HELD_ITEM,null,"If the holder is damaged by an attack, it will be switched out of battle to be replaced by another Pokemon of your choosing on your team."),
	EJECT_PACK(332,0,500,Color.BLACK,Item.HELD_ITEM,null,"If the holder's stats are lowered, it will be switched out of battle to be replaced by another Pokemon of your choosing on your team."),
	POWER_HERB(83,0,50,new Color(253, 80, 77),Item.HELD_ITEM,null,"A herb that allows the holder to immediately use a move that normally requires a turn to charge or recharge, but only once."),
	WHITE_HERB(84,0,50,new Color(242, 242, 242),Item.HELD_ITEM,null,"A herb that will restore any lowered stat in battle, but only once."),
	MENTAL_HERB(92,0,50,Color.BLACK,Item.HELD_ITEM,null,"An item to be held by a Pokemon. The holder shakes off move-binding effects, flinching, and possession to move freely. It can be used only once."),
	MIRROR_HERB(333,0,50,Color.BLACK,Item.HELD_ITEM,null,"This herb will allow the holder to mirror an opponent's stat increases to boost its own stats - but only once."),
	ELECTRIC_SEED(334,2,250,Color.BLACK,Item.HELD_ITEM,null,"If the terrain becomes Electric Terrain, the holder will use this seed to boost its own Defense stat."),
	GRASSY_SEED(335,2,250,Color.BLACK,Item.HELD_ITEM,null,"If the terrain becomes Grassy Terrain, the holder will use this seed to boost its own Defense stat."),
	PSYCHIC_SEED(336,2,250,Color.BLACK,Item.HELD_ITEM,null,"If the terrain becomes Psychic Terrain, the holder will use this seed to boost its own Sp. Def stat."),
	SPARKLY_SEED(337,2,250,Color.BLACK,Item.HELD_ITEM,null,"If the terrain becomes Sparkly Terrain, the holder will use this seed to boost its own Sp. Def stat."),
	WEAKNESS_POLICY(85,0,400,new Color(215, 233, 195),Item.HELD_ITEM,null,"If the holder is hit with a move it's weak to, this policy sharply boosts its Attack and Sp. Atk stats."),
	BLUNDER_POLICY(86,0,400,new Color(239, 239, 178),Item.HELD_ITEM,null,"If the holder misses with a move due to low accuracy, this policy will sharply boost its Speed stat."),
	ROOM_SERVICE(338,0,100,Color.BLACK,Item.HELD_ITEM,null,"If Trick Room takes effect, this item will lower the holder's Speed stat."),
	RED_CARD(87,0,250,new Color(216, 35, 22),Item.HELD_ITEM,null,"If the holder is damaged by an attack, the mysterious power of this card can remove the attacker from the battle."),
	THROAT_SPRAY(88,0,150,new Color(96, 120, 168),Item.HELD_ITEM,null,"If the holder uses a sound-based move, this throat spray will boost its Sp. Atk stat."),
	ADRENALINE_ORB(326,1,75,Color.BLACK,Item.HELD_ITEM,null,"This orb will boost the Speed of the Pokemon by 1 stage when it is intimidated, threatened or terrified."),
	ABSORB_BULB(327,3,320,Color.BLACK,Item.HELD_ITEM,372,"This single-use bulb will absorb any Water-type attack and boost the Sp. Atk stat of the holder. Any other attack will damage the item."),
	CELL_BATTERY(328,3,275,Color.BLACK,Item.HELD_ITEM,373,"This single-use battery will absorb any Electric-type attack and boost the Attack stat of the holder. Any other attack will damage the item."),
	LUMINOUS_MOSS(329,3,335,Color.BLACK,Item.HELD_ITEM,374,"This single-use moss will absorb any Water-type attack and boost the Sp. Def stat of the holder. Any other attack will damage the item."),
	SNOWBALL(330,3,300,Color.BLACK,Item.HELD_ITEM,375,"This single-use ball of snow will absorb any Ice-type attack and boost the Attack stat of the holder. Any other attack will damage the item."),
	DAMAGED_BULB(372,1,25,Color.BLACK,Item.HELD_ITEM,null,"This single-use bulb boosts the Sp. Atk stat if the holder is damaged by a Water-type attack."),
	DAMAGED_BATTERY(373,1,30,Color.BLACK,Item.HELD_ITEM,null,"This single-use battery boosts the Attack stat if the holder is damaged by an Electric-type attack."),
	DAMAGED_MOSS(374,1,25,Color.BLACK,Item.HELD_ITEM,null,"This single-use moss boosts the Sp. Def stat if the holder is damaged by a Water-type move."),
	DAMAGED_SNOWBALL(375,1,30,Color.BLACK,Item.HELD_ITEM,null,"This single-use ball of snow boosts the Attack stat if the holder is damaged by an Ice-type move."),
	HM01(93,0,0,Color.BLACK,Item.TMS,Move.CUT,"Teaches a Pokemon this move."),
	HM02(94,0,0,Color.BLACK,Item.TMS,Move.ROCK_SMASH,"Teaches a Pokemon this move."),
	HM03(95,0,0,Color.BLACK,Item.TMS,Move.VINE_CROSS,"Teaches a Pokemon this move."),
	HM04(96,0,0,Color.BLACK,Item.TMS,Move.SURF,"Teaches a Pokemon this move."),
	HM05(97,0,0,Color.BLACK,Item.TMS,Move.SLOW_FALL,"Teaches a Pokemon this move."),
	HM06(98,0,0,Color.BLACK,Item.TMS,Move.WHIRLPOOL,"Teaches a Pokemon this move."),
	HM07(99,0,0,Color.BLACK,Item.TMS,Move.ROCK_CLIMB,"Teaches a Pokemon this move."),
	HM08(100,0,0,Color.BLACK,Item.TMS,Move.LAVA_SURF,"Teaches a Pokemon this move."),
	TM01(101,0,0,Color.BLACK,Item.TMS,Move.SUPER_FANG,"Teaches a Pokemon this move."),
	TM02(102,0,0,Color.BLACK,Item.TMS,Move.RADIO_BURST,"Teaches a Pokemon this move."),
	TM03(103,0,0,Color.BLACK,Item.TMS,Move.ARCANE_SPELL,"Teaches a Pokemon this move."),
	TM04(104,0,0,Color.BLACK,Item.TMS,Move.CALM_MIND,"Teaches a Pokemon this move."),
	TM05(105,0,0,Color.BLACK,Item.TMS,Move.BODY_SLAM,"Teaches a Pokemon this move."),
	TM06(106,0,0,Color.BLACK,Item.TMS,Move.SHADOW_BALL,"Teaches a Pokemon this move."),
	TM07(107,0,0,Color.BLACK,Item.TMS,Move.FOCUS_BLAST,"Teaches a Pokemon this move."),
	TM08(108,0,0,Color.BLACK,Item.TMS,Move.BULK_UP,"Teaches a Pokemon this move."),
	TM09(109,0,0,Color.BLACK,Item.TMS,Move.LEAF_BLADE,"Teaches a Pokemon this move."),
	TM10(110,0,0,Color.BLACK,Item.TMS,Move.ICE_BEAM,"Teaches a Pokemon this move."),
	TM11(111,0,0,Color.BLACK,Item.TMS,Move.PSYSHOCK,"Teaches a Pokemon this move."),
	TM12(112,750,0,Color.BLACK,Item.TMS,Move.PROTECT,"Teaches a Pokemon this move."),
	TM13(113,5000,0,Color.BLACK,Item.TMS,Move.BATON_PASS,"Teaches a Pokemon this move."),
	TM14(114,0,0,Color.BLACK,Item.TMS,Move.TAUNT,"Teaches a Pokemon this move."),
	TM15(115,1000,0,Color.BLACK,Item.TMS,Move.GIGA_IMPACT,"Teaches a Pokemon this move."),
	TM16(116,1000,0,Color.BLACK,Item.TMS,Move.HYPER_BEAM,"Teaches a Pokemon this move."),
	TM17(117,0,0,Color.BLACK,Item.TMS,Move.SOLAR_BEAM,"Teaches a Pokemon this move."),
	TM18(118,0,0,Color.BLACK,Item.TMS,Move.IRON_HEAD,"Teaches a Pokemon this move."),
	TM19(119,0,0,Color.BLACK,Item.TMS,Move.PHOTON_GEYSER,"Teaches a Pokemon this move."),
	TM20(120,0,0,Color.BLACK,Item.TMS,Move.EARTHQUAKE,"Teaches a Pokemon this move."),
	TM21(121,0,0,Color.BLACK,Item.TMS,Move.THROAT_CHOP,"Teaches a Pokemon this move."),
	TM22(122,0,0,Color.BLACK,Item.TMS,Move.FELL_STINGER,"Teaches a Pokemon this move."),
	TM23(123,800,0,Color.BLACK,Item.TMS,Move.WEATHER_BALL,"Teaches a Pokemon this move."),
	TM24(124,900,0,Color.BLACK,Item.TMS,Move.TERRAIN_PULSE,"Teaches a Pokemon this move."),
	TM25(125,0,0,Color.BLACK,Item.TMS,Move.THUNDERBOLT,"Teaches a Pokemon this move."),
	TM26(126,0,0,Color.BLACK,Item.TMS,Move.HIDDEN_POWER,"Teaches a Pokemon this move."),
	TM27(127,0,0,Color.BLACK,Item.TMS,Move.DRAIN_PUNCH,"Teaches a Pokemon this move."),
	TM28(128,0,0,Color.BLACK,Item.TMS,Move.FLAME_CHARGE,"Teaches a Pokemon this move."),
	TM29(129,0,0,Color.BLACK,Item.TMS,Move.LIQUIDATION,"Teaches a Pokemon this move."),
	TM30(130,0,0,Color.BLACK,Item.TMS,Move.U$TURN,"Teaches a Pokemon this move."),
	TM31(131,0,0,Color.BLACK,Item.TMS,Move.FALSE_SWIPE,"Teaches a Pokemon this move."),
	TM32(132,0,0,Color.BLACK,Item.TMS,Move.ZING_ZAP,"Teaches a Pokemon this move."),
	TM33(133,0,0,Color.BLACK,Item.TMS,Move.PSYCHIC_FANGS,"Teaches a Pokemon this move."),
	TM34(134,0,0,Color.BLACK,Item.TMS,Move.MAGIC_TOMB,"Teaches a Pokemon this move."),
	TM35(135,0,0,Color.BLACK,Item.TMS,Move.FLAMETHROWER,"Teaches a Pokemon this move."),
	TM36(136,0,0,Color.BLACK,Item.TMS,Move.SLUDGE_BOMB,"Teaches a Pokemon this move."),
	TM37(137,0,0,Color.BLACK,Item.TMS,Move.ROCK_TOMB,"Teaches a Pokemon this move."),
	TM38(138,4000,0,Color.BLACK,Item.TMS,Move.BLIZZARD,"Teaches a Pokemon this move."),
	TM39(139,0,0,Color.BLACK,Item.TMS,Move.PSYCHIC,"Teaches a Pokemon this move."),
	TM40(140,0,0,Color.BLACK,Item.TMS,Move.FACADE,"Teaches a Pokemon this move."),
	TM41(141,1000,0,Color.BLACK,Item.TMS,Move.REFLECT,"Teaches a Pokemon this move."),
	TM42(142,1000,0,Color.BLACK,Item.TMS,Move.LIGHT_SCREEN,"Teaches a Pokemon this move."),
	TM43(143,0,0,Color.BLACK,Item.TMS,Move.SPOTLIGHT_RAY,"Teaches a Pokemon this move."),
	TM44(144,1000,0,Color.BLACK,Item.TMS,Move.LIGHT_SPEED,"Teaches a Pokemon this move."),
	TM45(145,1000,0,Color.BLACK,Item.TMS,Move.WILL$O$WISP,"Teaches a Pokemon this move."),
	TM46(146,4000,0,Color.BLACK,Item.TMS,Move.FIRE_BLAST,"Teaches a Pokemon this move."),
	TM47(147,0,0,Color.BLACK,Item.TMS,Move.STAR_STORM,"Teaches a Pokemon this move."),
	TM48(148,0,0,Color.BLACK,Item.TMS,Move.SCALD,"Teaches a Pokemon this move."),
	TM49(149,1500,0,Color.BLACK,Item.TMS,Move.REST,"Teaches a Pokemon this move."),
	TM50(150,1250,0,Color.BLACK,Item.TMS,Move.TOXIC,"Teaches a Pokemon this move."),
	TM51(151,400,0,Color.BLACK,Item.TMS,Move.SLEEP_TALK,"Teaches a Pokemon this move."),
	TM52(152,0,0,Color.BLACK,Item.TMS,Move.AERIAL_ACE,"Teaches a Pokemon this move."),
	TM53(153,0,0,Color.BLACK,Item.TMS,Move.VOLT_SWITCH,"Teaches a Pokemon this move."),
	TM54(154,750,0,Color.BLACK,Item.TMS,Move.THUNDER_WAVE,"Teaches a Pokemon this move."),
	TM55(155,0,0,Color.BLACK,Item.TMS,Move.DISENCHANT,"Teaches a Pokemon this move."),
	TM56(156,0,0,Color.BLACK,Item.TMS,Move.TRICK_TACKLE,"Teaches a Pokemon this move."),
	TM57(157,0,0,Color.BLACK,Item.TMS,Move.DEFOG,"Teaches a Pokemon this move."),
	TM58(158,0,0,Color.BLACK,Item.TMS,Move.DRAGON_PULSE,"Teaches a Pokemon this move."),
	TM59(159,0,0,Color.BLACK,Item.TMS,Move.BODY_PRESS,"Teaches a Pokemon this move."),
	TM60(160,0,0,Color.BLACK,Item.TMS,Move.FREEZE$DRY,"Teaches a Pokemon this move."),
	TM61(161,0,0,Color.BLACK,Item.TMS,Move.SCORCHING_SANDS,"Teaches a Pokemon this move."),
	TM62(162,0,0,Color.BLACK,Item.TMS,Move.BUG_BUZZ,"Teaches a Pokemon this move."),
	TM63(163,4000,0,Color.BLACK,Item.TMS,Move.THUNDER,"Teaches a Pokemon this move."),
	TM64(164,1500,0,Color.BLACK,Item.TMS,Move.CLOSE_COMBAT,"Teaches a Pokemon this move."),
	TM65(165,0,0,Color.BLACK,Item.TMS,Move.SHADOW_CLAW,"Teaches a Pokemon this move."),
	TM66(166,10000,0,Color.BLACK,Item.TMS,Move.DRACO_METEOR,"Teaches a Pokemon this move."),
	TM67(167,2000,0,Color.BLACK,Item.TMS,Move.BREAKING_SWIPE,"Teaches a Pokemon this move."),
	TM68(168,0,0,Color.BLACK,Item.TMS,Move.LIGHT_DRAIN,"Teaches a Pokemon this move."),
	TM69(169,0,0,Color.BLACK,Item.TMS,Move.ROCK_POLISH,"Teaches a Pokemon this move."),
	TM70(170,1500,0,Color.BLACK,Item.TMS,Move.HYDRO_PUMP,"Teaches a Pokemon this move."),
	TM71(171,0,0,Color.BLACK,Item.TMS,Move.STONE_EDGE,"Teaches a Pokemon this move."),
	TM72(172,0,0,Color.BLACK,Item.TMS,Move.ICE_SPINNER,"Teaches a Pokemon this move."),
	TM73(173,0,0,Color.BLACK,Item.TMS,Move.GYRO_BALL,"Teaches a Pokemon this move."),
	TM74(174,900,0,Color.BLACK,Item.TMS,Move.SUNNY_DAY,"Teaches a Pokemon this move."),
	TM75(175,900,0,Color.BLACK,Item.TMS,Move.RAIN_DANCE,"Teaches a Pokemon this move."),
	TM76(176,700,0,Color.BLACK,Item.TMS,Move.SNOWSCAPE,"Teaches a Pokemon this move."),
	TM77(177,700,0,Color.BLACK,Item.TMS,Move.SANDSTORM,"Teaches a Pokemon this move."),
	TM78(178,800,0,Color.BLACK,Item.TMS,Move.SWORDS_DANCE,"Teaches a Pokemon this move."),
	TM79(179,300,0,Color.BLACK,Item.TMS,Move.GRASSY_TERRAIN,"Teaches a Pokemon this move."),
	TM80(180,300,0,Color.BLACK,Item.TMS,Move.ELECTRIC_TERRAIN,"Teaches a Pokemon this move."),
	TM81(181,300,0,Color.BLACK,Item.TMS,Move.PSYCHIC_TERRAIN,"Teaches a Pokemon this move."),
	TM82(182,300,0,Color.BLACK,Item.TMS,Move.SPARKLING_TERRAIN,"Teaches a Pokemon this move."),
	TM83(183,0,0,Color.BLACK,Item.TMS,Move.CAPTIVATE,"Teaches a Pokemon this move."),
	TM84(184,0,0,Color.BLACK,Item.TMS,Move.DARK_PULSE,"Teaches a Pokemon this move."),
	TM85(185,0,0,Color.BLACK,Item.TMS,Move.POWER_GEM,"Teaches a Pokemon this move."),
	TM86(186,0,0,Color.BLACK,Item.TMS,Move.X$SCISSOR,"Teaches a Pokemon this move."),
	TM87(187,0,0,Color.BLACK,Item.TMS,Move.POISON_JAB,"Teaches a Pokemon this move."),
	TM88(188,0,0,Color.BLACK,Item.TMS,Move.GALAXY_BLAST,"Teaches a Pokemon this move."),
	TM89(189,0,0,Color.BLACK,Item.TMS,Move.ACROBATICS,"Teaches a Pokemon this move."),
	TM90(190,0,0,Color.BLACK,Item.TMS,Move.IRON_BLAST,"Teaches a Pokemon this move."),
	TM91(191,1000,0,Color.BLACK,Item.TMS,Move.TRI$ATTACK,"Teaches a Pokemon this move."),
	TM92(192,0,0,Color.BLACK,Item.TMS,Move.METEOR_ASSAULT,"Teaches a Pokemon this move."),
	TM93(193,0,0,Color.BLACK,Item.TMS,Move.EARTH_POWER,"Teaches a Pokemon this move."),
	TM94(194,0,0,Color.BLACK,Item.TMS,Move.HURRICANE,"Teaches a Pokemon this move."),
	TM95(195,0,0,Color.BLACK,Item.TMS,Move.TRICK_ROOM,"Teaches a Pokemon this move."),
	TM96(196,0,0,Color.BLACK,Item.TMS,Move.ENERGY_BALL,"Teaches a Pokemon this move."),
	TM97(197,1000,0,Color.BLACK,Item.TMS,Move.RADIANT_BREAK,"Teaches a Pokemon this move."),
	TM98(198,0,0,Color.BLACK,Item.TMS,Move.FLIP_TURN,"Teaches a Pokemon this move."),
	TM99(199,0,0,Color.BLACK,Item.TMS,Move.RETURN,"Teaches a Pokemon this move."),
	INFINITE_REPEL(393,0,0,Color.BLACK,Item.KEY_ITEM,true,"Toggles infinite repel"),
	HEALING_PACK(388,0,0,Color.BLACK,Item.KEY_ITEM,true,"Heals your team to full!"),
	POCKET_PC(389,0,0,Color.BLACK,Item.KEY_ITEM,true,"Opens the PC from anywhere (if not in a gauntlet!)"),
	RARE_CANDY_BOX(390,0,0,Color.BLACK,Item.KEY_ITEM,true,"Elevates a Pokemon by 1 level"),
	EDGE_KIT(44,0,0,new Color(232, 52, 54),Item.KEY_ITEM,true,"Leaves your Pokemon with 1 xp point before leveling up!"),
	STATUS_KIT(391,0,0,Color.BLACK,Item.KEY_ITEM,true,"Inflicts a status of your choosing (if not immune to that status)"),
	DAMAGE_KIT(392,0,0,Color.BLACK,Item.KEY_ITEM,true,"Damages a Pokemon by 1 HP (won't cause it to faint)"),
	CALCULATOR(200,0,0,Color.BLACK,Item.KEY_ITEM,true,"Calculates damage simulating a battle"),
	DEX_NAV(263,0,0,Color.BLACK,Item.KEY_ITEM,true,"Shows wild Pokemon nearby"),
	CHERI_BERRY(221,10,2,new Color(232, 96, 80),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to free itself from paralysis."),
	CHESTO_BERRY(222,10,2,new Color(144, 112, 224),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to wake itself from sleep."),
	PECHA_BERRY(223,10,2,new Color(248, 192, 152),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to lift the effects of being poisoned from itself."),
	RAWST_BERRY(224,10,2,new Color(144, 208, 208),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to cure itself of a burn."),
	ASPEAR_BERRY(225,10,2,new Color(240, 224, 80),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to thaw itself from a frostbite."),
	LUM_BERRY(226,100,10,new Color(144, 216, 72),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to cure itself of any status condition it may have."),
	PERSIM_BERRY(227,10,2,new Color(224, 152, 112),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to cure itself of confusion."),
	LEPPA_BERRY(228,75,10,new Color(200, 72, 48),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to restore 10 PP to a move."),
	ORAN_BERRY(229,25,5,new Color(80, 160, 240),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to restore 10 HP to itself."),
	SITRUS_BERRY(230,50,15,new Color(248, 232, 104),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to restore 25% of HP to itself."),
	WIKI_BERRY(231,75,20,new Color(144, 112, 224),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to restore 33% HP should it find itself in a pinch."),
	SPELON_BERRY(275,40,10,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Attack is lowered, that stat will be restored."),
	BELUE_BERRY(276,50,10,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Defense is lowered, that stat will be restored."),
	PAMTRE_BERRY(277,60,15,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Sp. Atk is lowered, that stat will be restored."),
	DURIN_BERRY(278,50,10,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Sp. Def is lowered, that stat will be restored."),
	WATMEL_BERRY(279,40,10,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Speed is lowered, that stat will be restored."),
	WEPEAR_BERRY(280,60,15,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Accuracy is lowered, that stat will be restored."),
	BLUK_BERRY(281,30,5,Color.BLACK,Item.BERRY,null,"If a Pokemon holds one of these Berries and its' Evasion is lowered, that stat will be restored."),
	OCCA_BERRY(232,50,10,PType.FIRE.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Fire-type move, the power of that move will be weakened."),
	PASSHO_BERRY(233,50,10,PType.WATER.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Water-type move, the power of that move will be weakened."),
	WACAN_BERRY(234,50,10,PType.ELECTRIC.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Electric-type move, the power of that move will be weakened."),
	RINDO_BERRY(235,50,10,PType.GRASS.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Grass-type move, the power of that move will be weakened."),
	YACHE_BERRY(236,50,10,PType.ICE.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Ice-type move, the power of that move will be weakened."),
	CHOPLE_BERRY(237,50,10,PType.FIGHTING.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Fighting-type move, the power of that move will be weakened."),
	KEBIA_BERRY(238,50,10,PType.POISON.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Poison-type move, the power of that move will be weakened."),
	SHUCA_BERRY(239,50,10,PType.GROUND.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Ground-type move, the power of that move will be weakened."),
	COBA_BERRY(240,50,10,PType.FLYING.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Flying-type move, the power of that move will be weakened."),
	PAYAPA_BERRY(241,50,10,PType.PSYCHIC.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Psychic-type move, the power of that move will be weakened."),
	TANGA_BERRY(242,50,10,PType.BUG.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Bug-type move, the power of that move will be weakened."),
	CHARTI_BERRY(243,50,10,PType.ROCK.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Rock-type move, the power of that move will be weakened."),
	KASIB_BERRY(244,50,10,PType.GHOST.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Ghost-type move, the power of that move will be weakened."),
	HABAN_BERRY(245,50,10,PType.DRAGON.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Dragon-type move, the power of that move will be weakened."),
	COLBUR_BERRY(246,50,10,PType.DARK.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Dark-type move, the power of that move will be weakened."),
	BABIRI_BERRY(247,50,10,PType.STEEL.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Steel-type move, the power of that move will be weakened."),
	CHILAN_BERRY(248,50,10,PType.NORMAL.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Normal-type move, the power of that move will be weakened."),
	ROSELI_BERRY(249,50,10,PType.LIGHT.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Light-type move, the power of that move will be weakened."),
	MYSTICOLA_BERRY(250,50,10,PType.MAGIC.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Magic-type move, the power of that move will be weakened."),
	GALAXEED_BERRY(251,50,10,PType.GALACTIC.getColor(),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a supereffective Galactic-type move, the power of that move will be weakened."),
	LIECHI_BERRY(252,150,30,new Color(248, 224, 160),Item.BERRY,null,"If a Pokemon holds one of these Berries, its Attack stat will be boosted should it find itself in a pinch."),
	GANLON_BERRY(253,75,15,new Color(152, 144, 200),Item.BERRY,null,"If a Pokemon holds one of these Berries, its Defense stat will be boosted should it find itself in a pinch."),
	SALAC_BERRY(254,200,40,new Color(120, 184, 112),Item.BERRY,null,"If a Pokemon holds one of these Berries, its Speed stat will be boosted should it find itself in a pinch."),
	PETAYA_BERRY(255,150,30,new Color(240, 160, 120),Item.BERRY,null,"If a Pokemon holds one of these Berries, its Sp. Atk stat will be boosted should it find itself in a pinch."),
	APICOT_BERRY(256,75,15,new Color(104, 128, 184),Item.BERRY,null,"If a Pokemon holds one of these Berries, its Sp. Def stat will be boosted should it find itself in a pinch."),
	STARF_BERRY(257,500,200,new Color(184, 232, 152),Item.BERRY,null,"If a Pokemon holds one of these Berries, one of its stats will be sharply boosted should it find itself in a pinch."),
	MICLE_BERRY(258,200,40,new Color(64, 200, 64),Item.BERRY,null,"If a Pokemon holds one of these Berries, its accuracy will be boosted should it find itself in a pinch."),
	LANSAT_BERRY(376,25,10,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holds one of these Berries, its critical-hit ratio will be boosted should it find itself in a pinch."),
	CUSTAP_BERRY(259,200,40,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to act faster in a pinch, but only for the next move."),
	JABOCA_BERRY(377,50,20,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a physical move, the attacker will also take damage."),
	ROWAP_BERRY(378,50,20,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a special move, the attacker will also take damage."),
	KEE_BERRY(379,100,40,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a physical move, the holder's Defense stat will be boosted."),
	MARANGA_BERRY(380,100,40,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holding this Berry is hit with a special move, the holder's Sp. Def stat will be boosted."),
	ENIGMA_BERRY(381,50,25,new Color(220, 96, 70),Item.BERRY,null,"If a Pokemon holds one of these Berries, it will be able to regain half of its HP if it is hit with a supereffective move."),
	PACKAGE_A(260,0,0,Color.BLACK,Item.KEY_ITEM,false,"A large industrial juicer, designed to make berries into healthy juice quickly and effectively. Belongs to one Ms. Plum."),
	PACKAGE_B(261,0,0,Color.BLACK,Item.KEY_ITEM,false,"Contains a new state of the art frying pan, meant for a master chef. It's said to be for a Guy Eddie."),
	PACKAGE_C(262,0,0,Color.BLACK,Item.KEY_ITEM,false,"Inside is a bunch of dog toys, and the address says it's for a place called \"Poundtown\"."),
	PACKAGE_D(266,0,0,Color.BLACK,Item.KEY_ITEM,false,"A Drednaw stapler, for an old friend of Robin's. Sent all the way from Galar, the deliver address is for the local warehouse."),
	WAREHOUSE_KEY(267,0,0,Color.BLACK,Item.KEY_ITEM,false,"A key that unlocks Poppy Grove's Warehouse."),
	FISHING_ROD(268,0,0,Color.BLACK,Item.KEY_ITEM,true,"Use when looking at water to fish!"),
	WIRE_CUTTERS(269,0,0,Color.BLACK,Item.KEY_ITEM,false,"Heavy duty wire cutters that can cut through any wire, big or small. A favorite for electricians."),
	VISOR(270,0,0,Color.BLACK,Item.KEY_ITEM,true,"A rainbow pair of shades that blocks 90% of harsh light."),
	ICE_KEY(271,0,0,Color.BLACK,Item.KEY_ITEM,false,"A key that unlocks Ice Master's Classroom."),
	GROUND_KEY(272,0,0,Color.BLACK,Item.KEY_ITEM,false,"A key that unlocks Ground Master's Classroom."),
	SHOVEL(273,0,0,Color.BLACK,Item.KEY_ITEM,false,"A handy tool that can shovel snowballs to clear them."),
	ICE_PICK(274,0,0,Color.BLACK,Item.KEY_ITEM,false,"A cold metal pickaxe used for breaking blocks of ice."),
	LETTER(284,0,0,Color.BLACK,Item.KEY_ITEM,true,"A letter from your mother delivered by Robin. It's of urgent matter."),
	ROCK_CRYSTAL(285,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Rock energy. Changes a Pokemon's hidden power."),
	FIRE_CRYSTAL(286,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Fire energy. Changes a Pokemon's hidden power."),
	WATER_CRYSTAL(287,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Water energy. Changes a Pokemon's hidden power."),
	GRASS_CRYSTAL(288,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Grass energy. Changes a Pokemon's hidden power."),
	ICE_CRYSTAL(289,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Ice energy. Changes a Pokemon's hidden power."),
	ELECTRIC_CRYSTAL(290,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Electric energy. Changes a Pokemon's hidden power."),
	FIGHTING_CRYSTAL(291,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Fighting energy. Changes a Pokemon's hidden power."),
	POISON_CRYSTAL(292,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Poison energy. Changes a Pokemon's hidden power."),
	GROUND_CRYSTAL(293,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Ground energy. Changes a Pokemon's hidden power."),
	FLYING_CRYSTAL(294,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Flying energy. Changes a Pokemon's hidden power."),
	PSYCHIC_CRYSTAL(295,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Psychic energy. Changes a Pokemon's hidden power."),
	BUG_CRYSTAL(296,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Bug energy. Changes a Pokemon's hidden power."),
	GHOST_CRYSTAL(297,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Ghost energy. Changes a Pokemon's hidden power."),
	DRAGON_CRYSTAL(298,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Dragon energy. Changes a Pokemon's hidden power."),
	STEEL_CRYSTAL(299,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Steel energy. Changes a Pokemon's hidden power."),
	DARK_CRYSTAL(300,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Dark energy. Changes a Pokemon's hidden power."),
	LIGHT_CRYSTAL(301,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Light energy. Changes a Pokemon's hidden power."),
	MAGIC_CRYSTAL(302,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Magic energy. Changes a Pokemon's hidden power."),
	GALACTIC_CRYSTAL(303,4000,1000,Color.BLACK,Item.OTHER,null,"A sharp crystal exuding Galactic energy. Changes a Pokemon's hidden power."),
	TINY_MUSHROOM(304,0,0,Color.BLACK,Item.OTHER,null,"A small and rare mushroom. It is sought after by collectors."), // 300
	BIG_MUSHROOM(305,0,0,Color.BLACK,Item.OTHER,null,"A large and rare mushroom. It is sought after by collectors."), // 1500
	NUGGET(306,0,500,Color.BLACK,Item.OTHER,null,"A nugget of pure gold that gives off a lustrous gleam. It can be sold at a high price."),
	BIG_NUGGET(307,0,2000,Color.BLACK,Item.OTHER,null,"A big nugget of pure gold that gives off a lustrous gleam. It can be sold at a very high price."),
	STAR_PIECE(308,0,0,Color.BLACK,Item.OTHER,null,"A small shard of a beautiful gem that gives off a distinctly red sparkle. It can be sold at a high price."),
	RELIC_GOLD(309,0,2500,Color.BLACK,Item.OTHER,null,"A gold coin used by an ancient civilization about 3,000 years ago."),
	RELIC_SILVER(310,0,650,Color.BLACK,Item.OTHER,null,"A silver coin used by an ancient civilization about 3,000 years ago."),
	FAITH_CORE(383,0,0,Color.BLACK,Item.KEY_ITEM,false,"A fragment of ancient energy, pulsing with quiet warmth. Said to embody the undying spirit of belief."),
	LOGIC_CORE(384,0,0,Color.BLACK,Item.KEY_ITEM,false,"A shard of crystallized thought, sharp and cold to the touch. It hums with the relentless force of reason."),
	;
	
	private int id;
	private Color color;
	private int pocket;
	private Move move;
	private Nature nature;
	private int cost;
	private int sell;
	private int healAmount;
	private boolean usable;
	private String desc;
	private BufferedImage image;
	private BufferedImage image2;
	private BufferedImage backImage;
	private Image greyImage;
	private Image greyImage2;
	
	public static final int MEDICINE = 1;
    public static final int OTHER = 2;
    public static final int BALLS = 3;
    public static final int TMS = 4;
    public static final int HELD_ITEM = 5;
    public static final int BERRY = 6;
    public static final int KEY_ITEM = 7;
    
    public static Item[] itemTable = setupItemTable();
    public static Item[] mints;
    public static Item[] balls;
    public static int mintID = 0;
    public static int ballID = 0;
    
    private static JPanel calc;
    private static JFrame calcFrame;
    private static JComboBox<Pokemon> userMons;
    private static JComboBox<Pokemon> foeMons;
    private static JSpinner userLevel;
    private static Field field;
    private static JButton okButton;
    
    private static int userGen;
    private static int foeGen;
	
	Item(int id, int cost, int sell, Color color, int pocket, Object o, String desc) {
		this.id = id;
		this.cost = cost;
		this.sell = sell;
		this.color = color;
		this.pocket = pocket;
		if (o instanceof Move) {
			this.move = (Move) o;
		} else if (o instanceof Nature) {
			this.nature = (Nature) o;
		} else if (o instanceof Integer) {
			this.healAmount = (Integer) o;
		} else if (pocket == KEY_ITEM) {
			this.usable = (boolean) o;
		}
		this.desc = desc;
		
		String path = "/items/";
		path += isTM() ? "tm_" + getMove().mtype.toString().toLowerCase() : super.toString().toLowerCase();
		if (isMint()) path = path.replace("_mint", "");
		image = setupImage(path + ".png");
		image2 = scaleImage(image, 2);
		if (isBall()) backImage = setupBackImage();
		
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
		
		if (isMint()) addToMintTable();
		if (pocket == Item.BALLS) addToBallsTable();
	}

	BufferedImage setupBackImage() {
		Image image = image2;
		
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D graphics = bufferedImage.createGraphics();

	    // Flip the image horizontally by drawing it with negative width
	    graphics.drawImage(image, image.getWidth(null), 0, -image.getWidth(null), image.getHeight(null), null);
	    graphics.dispose();

	    return bufferedImage;
	}
	
	public Image setupGreyImage(BufferedImage image) {
		ImageFilter grayFilter = new GrayFilter(true, 25);

        ImageProducer producer = new FilteredImageSource(image.getSource(), grayFilter);
        Image grayImage = Toolkit.getDefaultToolkit().createImage(producer);
        
        return grayImage;
	}

	private void addToMintTable() {
		if (mints == null) mints = new Item[21];
		mints[mintID++] = this;
	}
	
	private void addToBallsTable() {
		if (balls == null) balls = new Item[25];
		balls[ballID++] = this;
	}

	private static Item[] setupItemTable() {
		Item[] values = Item.values();
		Item[] result = new Item[values.length];
		for (int i = 0; i < values.length; i++) {
			if (result[values[i].id] != null) throw new IllegalStateException("2 items have the same ID: " + result[values[i].id] + " and " + values[i] + ".");
			result[values[i].id] = values[i];
		}
		return result;
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
	
	public BufferedImage scaleImage(BufferedImage image, int scale) {
		// Calculate the new dimensions based on the scale
        int newWidth = image.getWidth() * scale;
        int newHeight = image.getHeight() * scale;
        
        // Create a new BufferedImage with the scaled dimensions
        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        
        // Draw the original image onto the scaled image
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return result;
	}

	public int getCost() { return cost; }
	
	public int getSell() { return sell; }

	public int getID() { return id; }
	
	public Move getMove() { return move; }
	
	public Nature getNature() { return nature; }
	
	public int getPocket() { return pocket; }
	
	public Color getColor() {
		if (getMove() != null) return getMove().mtype.getColor();
		return color;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public BufferedImage getImage() { return image; }
	public BufferedImage getImage2() { return image2; }
	public BufferedImage getBackImage() { return backImage; }
	
	public Image getGreyImage() {
		if (greyImage == null) greyImage = setupGreyImage(image);
		return greyImage;
	}
	
	public Image getGreyImage2() {
		if (greyImage2 == null) greyImage2 = setupGreyImage(image2);
		return greyImage2;
	}
	
	public static Item getItem(int id) {
		return itemTable[id];
	}
	
	public static ArrayList<Item> getTMs() {
		ArrayList<Item> result = new ArrayList<>();

		for (int i = 93; i <= 199; i++) {
            result.add(Item.getItem(i));
        }
		
		return result;
	}
	
	public static ArrayList<Move> getTMMoves() {
		ArrayList<Move> result = new ArrayList<>();
		ArrayList<Item> tms = getTMs();
		
		for (Item i : tms) {
			result.add(i.getMove());
		}
		
		return result;
	}
	
	@Override
	public String toString() {
	    String result = "";
	    String name = super.toString();
	    if (this.isTM()) {
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
	        result = result.replace("Pc", "PC");
	    }

	    return result;
	}
	
	public static Item getEnum(String string) {
		// Normalize the string
	    String normalized = string.toUpperCase().replace('\'', '1').replace('-', '$').replace(' ', '_');
	    
	    try {
	        return Item.valueOf(normalized);
	    } catch (IllegalArgumentException e) {
	        throw new IllegalStateException("No matching Item enum found for string: " + string, e);
	    }
	}

	public int getHealAmount() {
		return healAmount;
	}
	
	public boolean getLearned(Pokemon p) {
		return getLearned(p.id);
	}
	
	public boolean getLearned(int id) {
		int index1 = id - 1;
		int index2 = this.id - 93;
		
		//writeTMCSV(tm);
		
		return Pokemon.getLearned(index1, index2);
		
	}
	
	@SuppressWarnings("unused")
	private void writeTMCSV(boolean[][] tm) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("./tms.csv"))) {
			int r = 1;
	        for (boolean[] row : tm) {
	            // Write TMs (indexes 7 to 106 inclusive)
	            for (int i = 8; i <= 106; i++) {
	                writer.write(row[i] ? '1' : '0');
	            }
	            writer.write(" "); // Space between TMs and HMs

	            // Write HMs (indexes 0 to 7 inclusive)
	            for (int i = 0; i <= 7; i++) {
	                writer.write(row[i] ? '1' : '0');
	            }
	            writer.write(" "); // Space between HMs and Pokemon name

	            // Write Pokemon name
	            writer.write(Pokemon.getName(r++));
	            writer.newLine(); // Move to the next line for the next Pokemon
	        }
	        writer.newLine();
	        for (int i = 101; i < 200; i++) {
	        	writer.write(Item.getItem(i).toString());
	        	writer.newLine();
	        }
	        writer.newLine();
	        for (int i = 93; i < 101; i++) {
	        	writer.write(Item.getItem(i).toString());
	        	writer.newLine();
	        }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	public Status getStatus() {
		if (this == ANTIDOTE) return Status.POISONED;
		else if (this == AWAKENING) return Status.ASLEEP;
		else if (this == BURN_HEAL) return Status.BURNED;
		else if (this == PARALYZE_HEAL) return Status.PARALYZED;
		else if (this == FREEZE_HEAL) return Status.FROSTBITE;
		else if (this == FULL_HEAL || this == KLEINE_BAR) return null;
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
		} else if (this == Item.FIRE_STONE) {
			check = new int[] {278, 281};
		} else if (this == Item.WATER_STONE) {
			check = new int[] {132, 139, 148};
		} else if (this == Item.RAZOR_CLAW) {
			check = new int[] {247, 249};
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

	public static void useCalc(Pokemon p, Pokemon[] box, Pokemon f, boolean display) {
		if (p == null) return;
		if (Pokemon.gp != null) {
			Pokemon.gp.keyH.resetKeys();
		}
		Trainer pl = p.trainer;
		if (calc == null) {
			calc = new JPanel();
		    calc.setLayout(new GridBagLayout());
		    
		    SpinnerModel levelModel = new SpinnerNumberModel(50, 1, 100, 1);
		    SpinnerModel foeLevelModel = new SpinnerNumberModel(50, 1, 100, 1);
		    
		    GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.insets = new Insets(5, 5, 5, 5); // Add space between components
	        
	        userMons = new JComboBox<>();
	        userLevel = new JSpinner(levelModel);
	        
	        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)userLevel.getEditor();
	        JTextField textField = editor.getTextField();
	        textField.addFocusListener( new FocusAdapter() {
	            public void focusGained(final FocusEvent e) {
	                SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                        JTextField tf = (JTextField)e.getSource();
	                        tf.selectAll();
	                    }
	                });
	            }
	        });
	        
	        JLabel[] userStatLabels = new JLabel[6];
	        @SuppressWarnings("unchecked")
			JComboBox<Integer>[] userStages = new JComboBox[6];
	        JButton userCurrentHP = new JButton();
	        JLabel userHPP = new JLabel();
	        JLabel userSpeed = new JLabel();
	        JGradientButton[] userMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
	        JLabel[] userDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
	        JCheckBox critCheck = new JCheckBox("Crit");
	        for (Pokemon pokemon : pl.getOrderedTeam()) {
	        	Pokemon add = pokemon.clone();
	        	if (!(pl instanceof Player)) {
	        		add.setCalcNickname();
	        	}
	    		if (!(pokemon instanceof Egg)) {
	    			userMons.addItem(add.clone());
	    			
	    			if (pokemon.id == 150) {
		        		Pokemon kD = pokemon.clone();
		        		int oHP = kD.getStat(0);
						kD.id = 237;
						kD.setName(kD.getName());
						if (kD.nickname == kD.name()) kD.nickname = kD.getName();
						
						kD.baseStats = kD.getBaseStats();
						kD.setStats();
						kD.weight = kD.getWeight();
						int nHP = kD.getStat(0);
						kD.currentHP += nHP - oHP;
						kD.setTypes();
						kD.setSprites();
						kD.setAbility(kD.abilitySlot);
						userMons.addItem(kD);
		        	}
	    		}
	        }
	        if (box != null) {
				for (Pokemon q : box) {
					if (q != null && !(q instanceof Egg)) {
						userMons.addItem(q.clone());
					}
				}
			}
	        if (box != null) {
	        	Player player = (Player) pl;
	        	if (player.gauntletBox != null && !Pokemon.gp.ui.gauntlet) {
	        		for (Pokemon q : player.gauntletBox) {
	    				if (q != null && !(q instanceof Egg)) {
	    					userMons.addItem(q.clone());
	    				}
	    			}
	        	}
	        }
	        
	        AutoCompleteDecorator.decorate(userMons);
	        
	        foeMons = new JComboBox<>();
	        JSpinner foeLevel = new JSpinner(foeLevelModel);
	        
	        JSpinner.DefaultEditor fEditor = (JSpinner.DefaultEditor)foeLevel.getEditor();
	        JTextField fTextField = fEditor.getTextField();
	        fTextField.addFocusListener(new FocusAdapter() {
	            public void focusGained(final FocusEvent e) {
	                SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                        JTextField tf = (JTextField)e.getSource();
	                        tf.selectAll();
	                    }
	                });
	            }
	        });
	        
	        JLabel[] foeStatLabels = new JLabel[6];
	        @SuppressWarnings("unchecked")
			JComboBox<Integer>[] foeStages = new JComboBox[6];
	        JButton foeCurrentHP = new JButton();
	        JLabel foeHPP = new JLabel();
	        JLabel foeSpeed = new JLabel();
	        JGradientButton[] foeMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
	        JLabel[] foeDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
	        JCheckBox fCritCheck = new JCheckBox("Crit");
	        for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
	        	foeMons.addItem(new Pokemon(k, 50, false, true));
	        }
	        for (int i = 0; i < Trainer.trainers.length; i++) {
	        	Trainer tr = Trainer.trainers[i];
	        	if (tr != null && !Pokemon.gp.player.p.trainersBeat[i]) {
	        		Pokemon[] newTeam = new Pokemon[tr.getTeam().length];
	        		for (int j = 0; j < tr.getTeam().length; j++) {
	        			Pokemon po = tr.getTeam()[j];
	            		Pokemon add = po.clone();
	            		add.setCalcNickname();
	            		foeMons.addItem(add);
	            		newTeam[j] = add;
	            	}
	        		Trainer newT = tr.shallowClone(Pokemon.gp);
	        		for (Pokemon po : newTeam) {
	        			po.trainer = newT;
	        		}
	        	}
	        }
	        AutoCompleteDecorator.decorate(foeMons);
	        
	        JComboBox<Ability> userAbility = new JComboBox<>(Ability.values());
	        JComboBox<Ability> foeAbility = new JComboBox<>(Ability.values());
	        AutoCompleteDecorator.decorate(userAbility);
	        AutoCompleteDecorator.decorate(foeAbility);
	        
	        JButton infoButton = new JButton("   Info   ");
	        JButton fInfoButton = new JButton("   Info   ");
	        
	        ArrayList<Item> items = new ArrayList<>();
	        items.add(null);
	        for (Item item : Item.values()) {
	        	if (item.getPocket() == Item.HELD_ITEM || item.getPocket() == Item.BERRY) {
	        		items.add(item);
	        	}
	        }
	        
	        infoButton.addActionListener(e -> {
	        	JOptionPane.showMessageDialog(null, ((Pokemon) userMons.getSelectedItem()).showSummary(field, null), "Pokemon details", JOptionPane.PLAIN_MESSAGE);
	        });
	        
	        fInfoButton.addActionListener(e -> {
	        	Pokemon foe = (Pokemon) foeMons.getSelectedItem();
	        	if (foe.getSprite() == null) foe.setSprites();
	        	JOptionPane.showMessageDialog(null, ((Pokemon) foeMons.getSelectedItem()).showSummary(field, null), "Pokemon details", JOptionPane.PLAIN_MESSAGE);
	        });
	        
	        JComboBox<Item> userItem = new JComboBox<>((Item[]) items.toArray(new Item[1]));
	        JComboBox<Item> foeItem = new JComboBox<>((Item[]) items.toArray(new Item[1]));
	        
	        field = Pokemon.field.clone();
	        
	        AutoCompleteDecorator.decorate(userItem);
	        AutoCompleteDecorator.decorate(foeItem);
	        
	        userMons.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
	        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	            userLevel.setValue(userCurrent.getLevel());
	            updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
	        
	        foeMons.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
	        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	foeLevel.setValue(foeCurrent.getLevel());
	        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        });
	        
	        userLevel.addFocusListener(new FocusAdapter() {
				@Override // implementation
		    	public void focusGained(FocusEvent e) {
					JTextField textField = ((JSpinner.DefaultEditor) userLevel.getEditor()).getTextField();
					textField.selectAll();
		    	}
			});
	        
			foeLevel.addFocusListener(new FocusAdapter() {
				@Override // implementation
		    	public void focusGained(FocusEvent e) {
					JTextField textField = ((JSpinner.DefaultEditor) foeLevel.getEditor()).getTextField();
					textField.selectAll();
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
	        	userStatLabels[i] = new JLabel(userC.getStat(i) + "");
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
	        			updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        			updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        			if (index == 5) userSpeed.setText((current.getSpeed(field)) + "");
	        		});
	        		statsPanel.add(userStages[i]);
	        	}
	        	
	        	if (i == 0) {
	        		double percent = userC.currentHP * 100.0 / userC.getStat(0);
	        		userHPP.setText(String.format("%.1f", percent) + "%");
	        		statsPanel.add(userHPP);
	        	} else if (i == 5) {
	        		userSpeed.setText((userC.getSpeed(field)) + "");
	        		statsPanel.add(userSpeed);
	        	} else {
	        		statsPanel.add(blank);
	        	}
	        	
	        	
	        }
	        
	        JPanel fStatsPanel = new JPanel(new GridLayout(6, 3));
	        for (int i = 0; i < 6; i++) {
	        	foeStatLabels[i] = new JLabel(foeC.getStat(i) + "");
	        	Integer[] stages = new Integer[] {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6};
	        	foeStages[i] = new JComboBox<Integer>(stages);
	        	if (i != 0) foeStages[i].setSelectedIndex(foeC.statStages[i - 1] + 6);
	        	JLabel blank = new JLabel("");
	        	fStatsPanel.add(foeStatLabels[i]);
	        	if (i == 0) {
	        		foeCurrentHP.setText(foeC.currentHP + "");
	        		fStatsPanel.add(foeCurrentHP);
	        	} else {
	        		int index = i;
	        		foeStages[i].addActionListener(e -> {
	        			Pokemon current = ((Pokemon) foeMons.getSelectedItem());
	        			Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
	        			int amt = (int) foeStages[index].getSelectedItem();
	        			current.statStages[index - 1] = amt;
	        			updateMoves(current, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        			updateMoves(userCurrent, userMoves, userDamage, current, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        			if (index == 5) foeSpeed.setText((current.getSpeed(field)) + "");
	        		});
	        		fStatsPanel.add(foeStages[i]);
	        	}
	        	
	        	if (i == 0) {
	        		double percent = foeC.currentHP * 100.0 / foeC.getStat(0);
	        		foeHPP.setText(String.format("%.1f", percent) + "%");
	        		fStatsPanel.add(foeHPP);
	        	} else if (i == 5) {
	        		foeSpeed.setText((foeC.getSpeed(field)) + "");
	        		fStatsPanel.add(foeSpeed);
	        	} else {
	        		fStatsPanel.add(blank);
	        	}
	        	
	        }
	        
	        userLevel.setValue(userC.getLevel());
	        updateMoves(userC, userMoves, userDamage, foeC, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        
	        foeLevel.setValue(foeC.getLevel());
	        updateMoves(foeC, foeMoves, foeDamage, userC, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	    	
			userLevel.addChangeListener(l -> {
				Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
		    	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	updatePokemonLevel(userLevel, userCurrent, foeCurrent, true);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
			
			foeLevel.addChangeListener(l -> {
				Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
		    	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	updatePokemonLevel(foeLevel, userCurrent, foeCurrent, false);
	        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        });
	        
	        userItem.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
	        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	Item item = (Item) userItem.getSelectedItem();
	        	if (item != null) {
	        		item = field.contains(field.fieldEffects, Effect.MAGIC_ROOM) ? Item.REPEL : item;
	        	}
	        	userCurrent.item = item;
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
	        
	        foeItem.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
	        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	Item item = (Item) foeItem.getSelectedItem();
	        	if (item != null) {
	        		item = field.contains(field.fieldEffects, Effect.MAGIC_ROOM) ? Item.REPEL : item;
	        	}
	        	foeCurrent.item = item;
	        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        });
	        
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
	        
	        calc.add(userAbility, gbc);
	        gbc.gridx++;
	        calc.add(foeAbility, gbc);
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
				updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	        });
	        
	        fCritCheck.addActionListener(e -> {
	        	Pokemon current = ((Pokemon) userMons.getSelectedItem());
				Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
				updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
	        
	        userAbility.addActionListener(l -> {
				Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
				Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
				userCurrent.ability = (Ability) userAbility.getSelectedItem();
				updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
			});
			
			foeAbility.addActionListener(l -> {
				Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
				Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
				foeCurrent.ability = (Ability) foeAbility.getSelectedItem();
				updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
			});
	        
	        JPanel infoButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        infoButtonPanel.add(infoButton);
	        infoButtonPanel.add(critCheck);
	        calc.add(infoButtonPanel, gbc);
	        gbc.gridx++;
	        
	        JPanel fInfoButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        fInfoButtonPanel.add(fInfoButton);
	        fInfoButtonPanel.add(fCritCheck);
	        calc.add(fInfoButtonPanel, gbc);
	        gbc.gridx = 0;
	        gbc.gridy++;
	        
	        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        JButton addButton = new JButton("Add");
	        addButtonPanel.add(addButton);
	        JButton moreButton = new JButton("More");
	        addButtonPanel.add(moreButton);
	        
	        JPanel fAddButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        JButton fAddButton = new JButton("Add");
	        fAddButtonPanel.add(fAddButton);
	        JButton fMoreButton = new JButton("More");
	        fAddButtonPanel.add(fMoreButton);
	        
	        gbc.gridx = 0;
	        gbc.gridwidth = 1;
	        calc.add(addButtonPanel, gbc);

	        gbc.gridx = 1;
	        gbc.gridwidth = 1;
	        calc.add(fAddButtonPanel, gbc);
	        
	        addButton.addActionListener(l -> {
	        	Pokemon result = displayGenerator((Pokemon) userMons.getSelectedItem());
	        	if (result != null) {
	        		result.nickname = String.format("%s %d", "Generated", ++userGen);
	        		userMons.insertItemAt(result, 0);
	        	}
	        });
	        
	        fAddButton.addActionListener(l -> {
	        	Pokemon result = displayGenerator((Pokemon) foeMons.getSelectedItem());
	        	if (result != null) {
	        		result.nickname = String.format("%s %d", "Generated", ++foeGen);
	        		foeMons.insertItemAt(result, 0);
	        	}
	        });
	        
	        moreButton.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
				Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	moreButton(userCurrent, field);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
	        
	        fMoreButton.addActionListener(l -> {
	        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
				Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
	        	moreButton(foeCurrent, field);
	        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP, critCheck.isSelected(), userAbility, userItem, field);
	            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, foeCurrentHP, foeHPP, fCritCheck.isSelected(), foeAbility, foeItem, field);
	        });
	        
	        calcFrame = new JFrame("Damage Calculator");
            calcFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            calcFrame.setLayout(new BorderLayout());

            // Panel to hold calculator and button
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(calc, BorderLayout.CENTER);

            // Create "OK" button
            okButton = new JButton("OK");
            okButton.addActionListener(e -> calcFrame.dispose()); // Close when clicked

            // Make OK button the default button for Enter key
            JRootPane rootPane = calcFrame.getRootPane();
            rootPane.setDefaultButton(okButton);

            // Add button to bottom of the frame
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            calcFrame.add(contentPanel);
            calcFrame.pack();
            calcFrame.setLocationRelativeTo(Pokemon.gp); // Center on screen
		}
		
		if (calc != null) {
			field = Pokemon.field.clone();
			ActionListener[] listeners = userMons.getActionListeners();
			for (ActionListener al : listeners) {
			    userMons.removeActionListener(al);
			}
			
			userMons.removeAllItems();
			ArrayList<Pokemon> addList = new ArrayList<>();
			for (Pokemon pokemon : pl.getOrderedTeam()) {
	        	Pokemon add = pokemon.clone();
	        	if (!(pl instanceof Player)) {
	        		add.setCalcNickname();
	        	}
	        	if (!(pokemon instanceof Egg)) {
	        		addList.add(add);
	        		
		    		if (pokemon.id == 150) {
		        		Pokemon kD = pokemon.clone();
		        		int oHP = kD.getStat(0);
						kD.id = 237;
						kD.setName(kD.getName());
						if (kD.nickname == kD.name()) kD.nickname = kD.getName();
						
						kD.baseStats = kD.getBaseStats();
						kD.setStats();
						kD.weight = kD.getWeight();
						int nHP = kD.getStat(0);
						kD.currentHP += nHP - oHP;
						kD.setTypes();
						kD.setSprites();
						kD.setAbility(kD.abilitySlot);
						addList.add(kD);
		        	}
	        	}
	        }
	        if (box != null) {
				for (Pokemon q : box) {
					if (q != null && !(q instanceof Egg)) {
						addList.add(q.clone());
					}
				}
			}
	        if (box != null) {
	        	Player player = (Player) pl;
	        	if (player.gauntletBox != null && !Pokemon.gp.ui.gauntlet) {
	        		for (Pokemon q : player.gauntletBox) {
	    				if (q != null && !(q instanceof Egg)) {
	    					addList.add(q.clone());
	    				}
	    			}
	        	}
	        }
	        Trainer newT = pl.shallowClone(Pokemon.gp);
	        for (Pokemon po : addList) {
	        	po.trainer = newT;
	        	userMons.addItem(po);
	        }
	        
	        Pokemon userC = (Pokemon) userMons.getSelectedItem();
	        userLevel.setValue(userC.getLevel());
	        
	        for (ActionListener al : listeners) {
	            userMons.addActionListener(al);
	        }
	        
	        userMons.setSelectedIndex(0);
		}
        
        if (f != null) {
        	if (f.trainerOwned()) {
        		Pokemon clone = f.trainer.getTeam()[0].clone();
        		clone.setCalcNickname();
        		int index = getPokemonIndex(clone, foeMons);
            	boolean remove = index >= 0;
            	if (!remove) index = 0;
            	
            	Pokemon[] team = f.trainer.getTeam();
            	int currentIndex = -1;
            	Pokemon[] newTeam = new Pokemon[f.trainer.getTeam().length];
            	for (int i = 0; i < team.length; i++) {
            		Pokemon updatedMon = team[i].clone();
            		updatedMon.setCalcNickname();
            		foeMons.insertItemAt(updatedMon, index + i + 1);
            		if (remove) foeMons.removeItemAt(index + i);
            		
            		if (currentIndex < 0 && team[i].equals(f)) {
            			currentIndex = index + i;
            			if (!remove) currentIndex++;
            		}
            		newTeam[i] = updatedMon;
            	}
            	Trainer newT = f.trainer.shallowClone(Pokemon.gp);
        		for (Pokemon po : newTeam) {
        			po.trainer = newT;
        		}
            	
            	if (currentIndex >= 0) {
            		foeMons.setSelectedItem(foeMons.getItemAt(currentIndex));
            	}
            } else {
            	foeMons.setSelectedIndex(0);
        	}
        }
        
        if (display) {
        	calcFrame.setLocationRelativeTo(Pokemon.gp); // Center on screen
        	// Make OK button the default button for Enter key
            JRootPane rootPane = calcFrame.getRootPane();
            rootPane.setDefaultButton(okButton);
        	calcFrame.setVisible(true);
        }
		
	}

	private static int getPokemonIndex(Pokemon p, JComboBox<Pokemon> mons) {
		for (int i = 0; i < mons.getItemCount(); i++) {
			Pokemon current = mons.getItemAt(i);
			if (p.equals(current)) {
				return i;
			}
		}
		return -1;
		
	}

	private static void updateMoves(Pokemon current, JGradientButton[] moves, JLabel[] damages, Pokemon foe, JLabel[] statLabels, JComboBox<Integer>[] stages,
			JLabel speed, JButton currentHP, JLabel HPP, boolean crit, JComboBox<Ability> currentAbility, JComboBox<Item> currentItem, Field field) {
        for (int k = 0; k < moves.length; k++) {
        	if (current.moveset[k] != null) {
        		moves[k].setText(current.moveset[k].move.toString());
        		Move move = current.moveset[k].move;
        		PType mtype = move.mtype;
        		if (move == Move.HIDDEN_POWER) mtype = current.determineHPType();
				if (move == Move.RETURN) mtype = current.determineHPType();
				if (move == Move.WEATHER_BALL) mtype = current.determineWBType(field);
				if (move == Move.TERRAIN_PULSE) mtype = current.determineTPType(field);
				if (move.isAttack()) {
					if (mtype == PType.NORMAL) {
						if (current.ability == Ability.GALVANIZE) mtype = PType.ELECTRIC;
						if (current.ability == Ability.REFRIGERATE) mtype = PType.ICE;
						if (current.ability == Ability.PIXILATE) mtype = PType.LIGHT;
					}
					if (current.ability == Ability.NORMALIZE) mtype = PType.NORMAL;
				}
		        Color color = mtype.getColor();
		        moves[k].setSolid(false);
        		moves[k].setBackground(color);
        		moves[k].setFont(new Font("Arial", Font.BOLD, 12));
        		moves[k].setPreferredSize(new Dimension(120, 26));
        		if (!current.getValidMoveset().contains(move)) {
        			moves[k].setSolid(true);
        			moves[k].setBackground(Color.GRAY);
        			moves[k].setFont(new Font("Arial", Font.PLAIN, 12));
        		}
        		int minDamage = current.calcWithTypes(foe, current.moveset[k].move, current.getFaster(foe, 0, 0) == current, -1, crit, field);
        		int maxDamage = current.calcWithTypes(foe, current.moveset[k].move, current.getFaster(foe, 0, 0) == current, 1, crit, field);
        		double minDamageD = minDamage * 1.0 / foe.getStat(0);
        		minDamageD *= 100;
        		String formattedMinD = String.format("%.1f", minDamageD);
        		double maxDamageD = maxDamage * 1.0 / foe.getStat(0);
        		maxDamageD *= 100;
        		String formattedMaxD = String.format("%.1f", maxDamageD);
        		damages[k].setText(formattedMinD + "% - " + formattedMaxD + "%");
        		boolean crittable = crit && current.moveset[k].move.cat != 2 && current.moveset[k].move.critChance >= 0;
        		damages[k].setForeground(crittable ? Color.RED : Color.BLACK);
        		damages[k].setFont(crittable ? new Font("Arial", Font.BOLD, 12) : new Font("Arial", Font.PLAIN, 12));
        	} else {
        		moves[k].setText("[NO MOVE]");
        		moves[k].setBackground(null);
        		damages[k].setText("0% - 0%");
        		damages[k].setForeground(Color.BLACK);
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
	    	                JOptionPane.showMessageDialog(null, current.moveset[kndex].move.getMoveSummary(current, foe, field), "Move Description", JOptionPane.INFORMATION_MESSAGE);
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
			    			if (selectedMove.isCalcHiddenPowerReturn()) {
			    				int cHP = current.currentHP;
			    				current.ivs = Pokemon.determineOptimalIVs(selectedMove.mtype);
			    				current.setStats();
			    				int nHP = current.getStat(0);
			    				current.currentHP += nHP - cHP;
			    			}
			    		}
			    		updateMoves(current, moves, damages, foe, statLabels, stages, speed, currentHP, HPP, crit, currentAbility, currentItem, field);
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
			if (i == 5) speed.setText(current.getSpeed(field) + "");
		}

        currentAbility.setSelectedItem(current.ability);
        if (current.item == Item.REPEL) {
        	Item item = (Item) currentItem.getSelectedItem();
        	if (item != null) {
        		item = field.contains(field.fieldEffects, Effect.MAGIC_ROOM) ? Item.REPEL : item;
        	}
        	current.item = item;
        } else {
        	currentItem.setSelectedItem(current.item);
        }
        if (calcFrame != null) calcFrame.pack();
	}
	
	private static void updatePokemonLevel(JSpinner spinner, Pokemon userCurrent, Pokemon foeCurrent, boolean isUser) {
		Pokemon target = isUser ? userCurrent : foeCurrent;
		
		try {
			int level = (int) spinner.getValue();
			target.level = (level >= 1 && level <= 100) ? level : 50;
		} catch (NumberFormatException e) {
			target.level = 50;
		}
		
		int oHP = target.getStat(0);
		target.setStats();
		int nHP = target.getStat(0);
		target.currentHP += nHP - oHP;
		target.verifyHP();
		
		if (!isUser && !foeCurrent.toString().contains("(")) {
			target.setMoves();
		}
	}
	
	private static void moreButton(Pokemon p, Field f) {        
		JDialog dialog = new JDialog((Frame) null, "Edit Pokemon", true);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Label at the top
        JLabel title = new JLabel("Editing: " + p.toString());
        title.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridwidth = 1; // Reset gridwidth

        // Weather ComboBox
        gbc.gridy++;
        panel.add(new JLabel("Weather:"), gbc);
        JComboBox<Effect> weatherBox = new JComboBox<>(new Effect[]{Effect.SUN, Effect.RAIN, Effect.SANDSTORM, Effect.SNOW});
        weatherBox.insertItemAt(null, 0);
        weatherBox.setSelectedItem(f.weather == null ? null : f.weather.effect);
        AutoCompleteDecorator.decorate(weatherBox);
        gbc.gridx = 1;
        panel.add(weatherBox, gbc);

        // Terrain ComboBox
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Terrain:"), gbc);
        JComboBox<Effect> terrainBox = new JComboBox<>(new Effect[]{Effect.GRASSY, Effect.ELECTRIC, Effect.PSYCHIC, Effect.SPARKLY});
        terrainBox.insertItemAt(null, 0);
        terrainBox.setSelectedItem(f.terrain == null ? null : f.terrain.effect);
        AutoCompleteDecorator.decorate(terrainBox);
        gbc.gridx = 1;
        panel.add(terrainBox, gbc);
        
        JPanel fieldEffectsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints effectGbc = new GridBagConstraints();
        effectGbc.insets = new Insets(5, 10, 5, 10);
        effectGbc.anchor = GridBagConstraints.CENTER;

        int cols = 3; // how many checkboxes per row

        @SuppressWarnings("unchecked")
        Pair<Effect, JCheckBox>[] fieldEffects = new Pair[] {
            new Pair<>(Effect.GRAVITY, null),
            new Pair<>(Effect.TRICK_ROOM, null),
            new Pair<>(Effect.MAGIC_ROOM, null),
            new Pair<>(Effect.WATER_SPORT, null),
            new Pair<>(Effect.MUD_SPORT, null),
        };

        for (int i = 0; i < fieldEffects.length; i++) {
            Pair<Effect, JCheckBox> pair = fieldEffects[i];
            Effect effect = pair.getFirst();
            JCheckBox checkBox = new JCheckBox(effect.toString());
            pair.setSecond(checkBox);
            checkBox.setSelected(f.contains(f.fieldEffects, effect));
            
            effectGbc.gridx = i % cols;
            effectGbc.gridy = i / cols;
            fieldEffectsPanel.add(checkBox, effectGbc);
        }
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(fieldEffectsPanel, gbc);
        gbc.gridwidth = 1;

        // Type 1 and Type 2 ComboBoxes
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Type 1:"), gbc);
        JComboBox<PType> type1Box = new JComboBox<>(PType.values());
        type1Box.setSelectedItem(p.type1);
        AutoCompleteDecorator.decorate(type1Box);
        gbc.gridx = 1;
        panel.add(type1Box, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Type 2:"), gbc);
        JComboBox<PType> type2Box = new JComboBox<>(PType.values());
        type2Box.insertItemAt(null, 0); // Allow null for Type 2
        type2Box.setSelectedItem(p.type2);
        AutoCompleteDecorator.decorate(type2Box);
        gbc.gridx = 1;
        panel.add(type2Box, gbc);

        // Status ComboBox
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Status:"), gbc);
        JComboBox<Status> statusBox = new JComboBox<>(Status.getNonVolStatuses().toArray(new Status[0]));
        statusBox.insertItemAt(Status.HEALTHY, 0);
        statusBox.setSelectedItem(p.status);
        AutoCompleteDecorator.decorate(statusBox);
        gbc.gridx = 1;
        panel.add(statusBox, gbc);
        
        JPanel sideFieldEffectsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JPanel statusEffectsPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Add JCheckBoxes for each field effect
        @SuppressWarnings("unchecked")
		Pair<Object, JCheckBox>[] effect = new Pair[] {
				new Pair<Effect, JCheckBox>(Effect.REFLECT, null),
				new Pair<Effect, JCheckBox>(Effect.LIGHT_SCREEN, null),
				new Pair<Effect, JCheckBox>(Effect.AURORA_VEIL, null),
				new Pair<Effect, JCheckBox>(Effect.LUCKY_CHANT, null),
				new Pair<Effect, JCheckBox>(Effect.TAILWIND, null),
				new Pair<Status, JCheckBox>(Status.MAGIC_REFLECT, null),
				new Pair<Status, JCheckBox>(Status.POSSESSED, null),
				new Pair<Status, JCheckBox>(Status.MUTE, null),
				new Pair<Status, JCheckBox>(Status.MAGNET_RISE, null),
				new Pair<Status, JCheckBox>(Status.TAUNTED, null),
				new Pair<Status, JCheckBox>(Status.TORMENTED, null),
				new Pair<Status, JCheckBox>(Status.SMACK_DOWN, null),
				new Pair<Status, JCheckBox>(Status.HEAL_BLOCK, null),
				new Pair<Status, JCheckBox>(Status.DECK_CHANGE, null),
				new Pair<Status, JCheckBox>(Status.FLASH_FIRE, null),
				new Pair<Status, JCheckBox>(Status.CHARGED, null),
				new Pair<Status, JCheckBox>(Status.LOADED, null),};
        
        for (int i = 0; i < effect.length; i++) {
            Pair<Object, JCheckBox> pair = effect[i];
            Object first = pair.getFirst();
            JCheckBox checkBox = new JCheckBox(first.toString());
            pair.setSecond(checkBox);

            if (first instanceof Effect) {
                checkBox.setSelected(f.contains(p.getFieldEffects(), (Effect) first));
                sideFieldEffectsPanel.add(checkBox);
            } else {
                checkBox.setSelected(p.hasStatus((Status) first));
                statusEffectsPanel.add(checkBox);
            }
        }
        
        JScrollPane sideScroll = new JScrollPane(sideFieldEffectsPanel);
        sideScroll.setPreferredSize(new Dimension(300, 100));
        sideScroll.getVerticalScrollBar().setUnitIncrement(8);
        sideScroll.setBorder(BorderFactory.createTitledBorder("Field Effects"));
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(sideScroll, gbc);
        
        JScrollPane statusScroll = new JScrollPane(statusEffectsPanel);
        statusScroll.setPreferredSize(new Dimension(300, 125));
        statusScroll.getVerticalScrollBar().setUnitIncrement(8);
        statusScroll.setBorder(BorderFactory.createTitledBorder("Status Effects"));
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(statusScroll, gbc);
        
        // Create "Fields" panel with a titled border
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fieldsGbc = new GridBagConstraints();
        fieldsGbc.insets = new Insets(2, 5, 2, 5);
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy = 0;
        fieldsGbc.anchor = GridBagConstraints.WEST;

        // ===== Weight =====
        fieldsPanel.add(new JLabel("Weight:"), fieldsGbc);
        JTextField weightField = new JTextField(String.valueOf(p.weight), 10);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(weightField, fieldsGbc);

        // ===== Current HP =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Current HP:"), fieldsGbc);
        JTextField hpField = new JTextField(String.valueOf(p.currentHP), 10);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(hpField, fieldsGbc);

        // ===== Move Multiplier =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Move Multiplier:"), fieldsGbc);
        JComboBox<Integer> moveMultBox = new JComboBox<>(new Integer[]{1, 2, 4});
        moveMultBox.setSelectedItem(p.moveMultiplier);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(moveMultBox, fieldsGbc);

        // ===== Roll Count =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Roll Count:"), fieldsGbc);
        JComboBox<Integer> rollBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        rollBox.setSelectedItem(p.rollCount);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(rollBox, fieldsGbc);

        // ===== Metronome =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Metronome:"), fieldsGbc);
        JComboBox<Integer> metroBox = new JComboBox<>(new Integer[]{-1, 0, 1, 2, 3, 4, 5});
        metroBox.setSelectedItem(p.metronome);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(metroBox, fieldsGbc);
        
        // ===== Last Move Used =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Last Used Move:"), fieldsGbc);
        JComboBox<Move> lastBox = new JComboBox<>(Move.values());
        lastBox.insertItemAt(null, 0);
        lastBox.setSelectedItem(p.lastMoveUsed);
        AutoCompleteDecorator.decorate(lastBox);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(lastBox, fieldsGbc);
        
        // ===== Choice Move =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Choice Move:"), fieldsGbc);
        JComboBox<Move> choiceBox = new JComboBox<>(Move.values());
        choiceBox.insertItemAt(null, 0);
        choiceBox.setSelectedItem(p.choiceMove);
        AutoCompleteDecorator.decorate(choiceBox);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(choiceBox, fieldsGbc);

        // ===== Disabled Move =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Disabled Move:"), fieldsGbc);
        JComboBox<Move> disabledBox = new JComboBox<>(Move.values());
        disabledBox.insertItemAt(null, 0);
        disabledBox.setSelectedItem(p.disabledMove);
        AutoCompleteDecorator.decorate(disabledBox);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(disabledBox, fieldsGbc);

        // ===== Illusion =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Illusion:"), fieldsGbc);
        JCheckBox illusionBox = new JCheckBox();
        illusionBox.setSelected(p.illusion);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(illusionBox, fieldsGbc);
        
        // ===== Consumed Item =====
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsPanel.add(new JLabel("Consumed Item:"), fieldsGbc);
        JCheckBox consumedBox = new JCheckBox();
        consumedBox.setSelected(p.consumedItem);
        fieldsGbc.gridx = 1;
        fieldsPanel.add(consumedBox, fieldsGbc);

        // 🔹 Status Effects Panel (Nested inside "Fields")
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status Effects"));
        GridBagConstraints statusGbc = new GridBagConstraints();
        statusGbc.insets = new Insets(2, 5, 2, 5);
        statusGbc.gridx = 0;
        statusGbc.gridy = 0;
        statusGbc.anchor = GridBagConstraints.WEST;

        // ===== CRIT_CHANCE =====
        statusPanel.add(new JLabel("Crit Chance:"), statusGbc);
        JTextField critChanceField = new JTextField(String.valueOf(p.getStatusNum(Status.CRIT_CHANCE)), 5);
        statusGbc.gridx = 1;
        statusPanel.add(critChanceField, statusGbc);

        // ===== ARCANE_SPELL =====
        statusGbc.gridx = 0;
        statusGbc.gridy++;
        statusPanel.add(new JLabel("Arcane Spell:"), statusGbc);
        JTextField arcaneField = new JTextField(String.valueOf(p.getStatusNum(Status.ARCANE_SPELL)), 5);
        statusGbc.gridx = 1;
        statusPanel.add(arcaneField, statusGbc);

        // Add the status panel inside the fields panel
        fieldsGbc.gridx = 0;
        fieldsGbc.gridy++;
        fieldsGbc.gridwidth = 2;  // Make it span both columns
        fieldsPanel.add(statusPanel, fieldsGbc);
        
        JScrollPane fieldsScroll = new JScrollPane(fieldsPanel);
        fieldsScroll.setPreferredSize(new Dimension(300, 150));
        fieldsScroll.getVerticalScrollBar().setUnitIncrement(8);
        fieldsScroll.setBorder(BorderFactory.createTitledBorder("Fields"));

        // Add "Fields" panel to the main panel
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;  // Span across both columns in main layout
        panel.add(fieldsScroll, gbc);
        
        // Apply and Cancel Buttons
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            // Update Field and Pokemon with the selected values
        	Effect weather = (Field.Effect) weatherBox.getSelectedItem();
        	f.weather = weather == null ? null : f.new FieldEffect(weather);
        	Effect terrain = (Field.Effect) terrainBox.getSelectedItem();
            f.terrain = terrain == null ? null : f.new FieldEffect(terrain);
            
            for (int i = 0; i < fieldEffects.length; i++) {
            	Pair<Effect, JCheckBox> pair = fieldEffects[i];
                Effect first = pair.getFirst();
                JCheckBox second = pair.getSecond();
                
            	if (second.isSelected()) {
            		if (!f.contains(f.fieldEffects, first)) f.fieldEffects.add(f.new FieldEffect(first));
            	} else {
            		f.remove(f.fieldEffects, first);
            	}
            }
            
            p.type1 = (PType) type1Box.getSelectedItem();
            p.type2 = (PType) type2Box.getSelectedItem();
            p.status = (Status) statusBox.getSelectedItem();
            
            for (int i = 0; i < effect.length; i++) {
            	Pair<Object, JCheckBox> pair = effect[i];
                Object first = pair.getFirst();
                JCheckBox second = pair.getSecond();
                
                if (first instanceof Effect) {
                	Effect ef = (Effect) first;
                	if (second.isSelected()) {
                		if (!f.contains(p.getFieldEffects(), ef)) p.getFieldEffects().add(f.new FieldEffect(ef));
                	} else {
                		f.remove(p.getFieldEffects(), ef);
                	}
                } else {
                    Status st = (Status) first;
                    if (second.isSelected()) {
                    	if (!p.hasStatus(st)) p.addStatus(st);
                    } else {
                    	p.removeStatus(st);
                    }
                }
            }
            
            try {
                p.weight = Math.max(0.1, Math.min(9999.9, Double.parseDouble(weightField.getText())));
            } catch (NumberFormatException ex) {
                p.weight = 0.1;
            }

            try {
                p.currentHP = Math.max(1, Math.min(p.getStat(0), Integer.parseInt(hpField.getText())));
            } catch (NumberFormatException ex) {
                p.currentHP = 1;
            }

            p.moveMultiplier = (Integer) moveMultBox.getSelectedItem();
            p.rollCount = (Integer) rollBox.getSelectedItem();
            p.metronome = (Integer) metroBox.getSelectedItem();
            p.lastMoveUsed = (Move) lastBox.getSelectedItem();
            p.choiceMove = (Move) choiceBox.getSelectedItem();
            p.disabledMove = (Move) disabledBox.getSelectedItem();
            p.illusion = illusionBox.isSelected();
            p.consumedItem = consumedBox.isSelected();

            try {
                p.removeStatus(Status.CRIT_CHANCE);
                int crit = Integer.parseInt(critChanceField.getText());
                if (crit != 0) p.addStatus(Status.CRIT_CHANCE, crit);
            } catch (NumberFormatException ex) {
                p.removeStatus(Status.CRIT_CHANCE);
            }

            try {
                p.removeStatus(Status.ARCANE_SPELL);
                int arcane = Integer.parseInt(arcaneField.getText());
                if (arcane > 0) p.addStatus(Status.ARCANE_SPELL, arcane);
            } catch (NumberFormatException ex) {
                p.removeStatus(Status.ARCANE_SPELL);
            }
            
            SwingUtilities.getWindowAncestor(panel).dispose();
        });
        panel.add(applyButton, gbc);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}

	public static Pokemon displayGenerator(Pokemon p) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		
		BiConsumer<String, JComponent> addLabeledInput = (labelText, input) -> {
	        JPanel panel = new JPanel(new GridLayout(1, 2));
	        panel.add(new JLabel(labelText));
	        panel.add(input);
	        result.add(panel);
	    };
		
		JComboBox<Pokemon> nameInput = new JComboBox<Pokemon>();
		for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
        	nameInput.addItem(new Pokemon(k, 50, false, true));
        }
		if (p != null) {
			if (p.getEvolveString() != null) {
				nameInput.setSelectedIndex(p.id);
			} else {
				nameInput.setSelectedIndex(p.id - 1);
			}
		}
		addLabeledInput.accept("Pokemon", nameInput);
		
		JComboBox<Integer> levelInput = new JComboBox<>(IntStream.rangeClosed(1, 100).boxed().toArray(Integer[]::new));
		if (p != null) levelInput.setSelectedItem(p.level);
		addLabeledInput.accept("Level", levelInput);
		
		Ability[] abilityOptions = new Ability[3];
		for (int i = 0; i < 3; i++) {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.setAbility(i);
			abilityOptions[i] = test.ability;
		}
		JComboBox<Ability> abilityInput = new JComboBox<>(abilityOptions);
		if (p != null) abilityInput.setSelectedIndex(p.abilitySlot);
		addLabeledInput.accept("Ability", abilityInput);
		
		JComboBox<Nature> natures = new JComboBox<>(Nature.values());
		if (p != null) {
			natures.setSelectedItem(p.nat);
		} else {
			natures.setSelectedItem(Nature.SERIOUS);
		}
		addLabeledInput.accept("Nature", natures);
		
		@SuppressWarnings("unchecked")
		JComboBox<Integer>[] ivInputs = new JComboBox[6];
		Integer[] ivOptions = IntStream.rangeClosed(0, 31).boxed().toArray(Integer[]::new);
		int index = 0;
		for (String ivLabel : new String[]{"HP", "Atk", "Def", "SpA", "SpD", "Spe"}) {
	        JComboBox<Integer> ivInput = new JComboBox<>(ivOptions);
	        if (p != null) {
	        	ivInput.setSelectedIndex(p.ivs[index]);
	        } else {
	        	ivInput.setSelectedIndex(31);
	        }
	        addLabeledInput.accept(ivLabel, ivInput);
	        ivInputs[index++] = ivInput;
	    }
		
		JComboBox<Integer> happiness = new JComboBox<>(IntStream.rangeClosed(0, 255).boxed().toArray(Integer[]::new));
		if (p != null) {
			happiness.setSelectedIndex(p.happiness);
		} else {
			happiness.setSelectedIndex(70);
		}
	    addLabeledInput.accept("Happiness", happiness);
		
	    JTextField happinessCapInput = new JTextField(p == null ? "50" : "" + p.happinessCap, 5);
	    addLabeledInput.accept("Happiness Cap", happinessCapInput);

	    JTextField expInput = new JTextField(p == null ? "2" : "" + (p.expMax - p.exp), 5);
	    addLabeledInput.accept("Exp Remaining", expInput);
		
		AutoCompleteDecorator.decorate(nameInput);
		AutoCompleteDecorator.decorate(levelInput);
		AutoCompleteDecorator.decorate(abilityInput);
		AutoCompleteDecorator.decorate(natures);
		AutoCompleteDecorator.decorate(happiness);
		
		JPanel movesPanel = new JPanel(new GridLayout(2, 2));
		@SuppressWarnings("unchecked")
		JComboBox<Move>[] moveInputs = new JComboBox[4];
		Move[] movebank = Move.values();
		boolean pNull = p == null;
		Pokemon testMoves = pNull ? (Pokemon) nameInput.getSelectedItem() : p;
		if (pNull) {
			testMoves.level = 1;
			testMoves.setMoves();
		}
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
			if (p == null) {
				test.level = (Integer) levelInput.getSelectedItem();
				test.setMoves();
				for (int j = 0; j < 4; j++) {
					moveInputs[j].setSelectedItem(Move.STRUGGLE);
					Moveslot m = test.moveset[j];
					Move m1 = m == null ? null : m.move;
					moveInputs[j].setSelectedItem(m1);
				}
			}
			for (int i = 0; i < 3; i++) {
				test.setAbility(i);
				abilityInput.addItem(test.ability);
			}
		});
		
		levelInput.addActionListener(f -> {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.level = (Integer) levelInput.getSelectedItem();
			if (p == null) {
				test.setMoves();
				for (int j = 0; j < 4; j++) {
					moveInputs[j].setSelectedItem(Move.STRUGGLE);
					Moveslot m = test.moveset[j];
					Move m1 = m == null ? null : m.move;
					moveInputs[j].setSelectedItem(m1);
				}
			}
			test.expMax = test.setExpMax();
			expInput.setText(test.expMax + "");
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
		final Pokemon[] resultPokemon = new Pokemon[1];
		generate.addActionListener(e -> {
			try {
				int id = ((Pokemon) nameInput.getSelectedItem()).id;
				int level = (Integer) levelInput.getSelectedItem();
				int ability = abilityInput.getSelectedIndex();
				int expRemaining = Integer.parseInt(expInput.getText());
				int happinessCap = Integer.parseInt(happinessCapInput.getText());
				Nature nature = (Nature) natures.getSelectedItem();
				int[] ivs = new int[6];
				for (int i = 0; i < 6; i++) {
					ivs[i] = (Integer) ivInputs[i].getSelectedItem();
				}
				Move[] moveset = new Move[4];
				for (int i = 0; i < 4; i++) {
					moveset[i] = (Move) moveInputs[i].getSelectedItem();
				}
				
				Pokemon generated = new Pokemon(id, level, true, false);
				generated.abilitySlot = ability;
				generated.setAbility(generated.abilitySlot);
				generated.nat = nature;
				generated.ivs = ivs;
				generated.setStats();
				int exp = generated.expMax - expRemaining;
				if (exp < 0 || exp >= generated.expMax) {
					JOptionPane.showMessageDialog(null, "Exp is not in the range [0, " + generated.expMax + "]");
					return;
				} else {
					generated.exp = exp;
				}
				generated.happinessCap = happinessCap;
				
				for (int i = 0; i < 4; i++) {
					generated.moveset[i] = moveset[i] == null ? null : new Moveslot(moveset[i]);
				}
				generated.happiness = (Integer) happiness.getSelectedItem();
				generated.heal();
				resultPokemon[0] = generated;
				SwingUtilities.getWindowAncestor(result).dispose();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Invalid inputs: " + ex.getMessage());
				ex.printStackTrace();
			}
			
		});
		result.add(generate);
		
		JOptionPane.showMessageDialog(null, result);
		
		return resultPokemon[0];
	}
	
	public boolean isBall() {
		return pocket == BALLS;
	}

	public boolean isTM() {
		return getMove() != null;
	}
	
	public boolean isMint() {
		return getNature() != null;
	}

	public boolean isEvoItem() {
		return (getID() >= 20 && getID() < 26) || (this == Item.FIRE_STONE || this == Item.WATER_STONE || this == Item.RAZOR_CLAW);
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
		return pocket == Item.BERRY;
	}
	
	public boolean isStatusBerry() {
		return (getID() >= 221 && getID() <= 227);
	}
	
	public boolean isPinchBerry() {
		return (getID() == 231 || (getID() >= 252 && getID() <= 259) || getID() == 376);
	}
	
	public boolean isTreasure() {
		return (getID() >= 304 && getID() <= 310);
	}

	public boolean isStatBerry() {
		return (getID() >= 275 && getID() <= 281);
	}
	
	public boolean isUsable() {
		if (isBall()) return false;
		if (pocket == KEY_ITEM) return usable;
		if (pocket != OTHER) return true;
		if (isTreasure() || isFossil()) {
			return false;
		}
		return true;
	}
	
	public boolean isSellable(int amt) {
		if (pocket == HELD_ITEM) {
			if (getSell() == 0 && amt <= 1) return false;
		}
		return true;
	}

	private boolean isFossil() {
		return (getID() == 45 || getID() == 46);
	}
	
	public static String breakString(String input, int maxChar) {
	    if (input == null || maxChar <= 0) {
	        return null;
	    }

	    StringBuilder result = new StringBuilder();
	    StringBuilder currentLine = new StringBuilder();
	    int currentLength = 0;

	    // split on spaces and tabs but not \n
	    for (String word : input.split("[ \\t]+")) {
	        // split the word if it contains \n
	        String[] parts = word.split("\n", -1);

	        for (int i = 0; i < parts.length; i++) {
	            String part = parts[i];

	            // check if need to wrap before adding this part
	            if (currentLength + part.length() > maxChar) {
	                result.append(currentLine.toString().trim()).append("\n");
	                currentLine.setLength(0);
	                currentLength = 0;
	            }

	            currentLine.append(part);
	            currentLength += part.length();

	            // if this part was followed by a \n break the line
	            if (i < parts.length - 1) {
	                result.append(currentLine.toString().trim()).append("\n");
	                currentLine.setLength(0);
	                currentLength = 0;
	            } else {
	                currentLine.append(" ");
	                currentLength += 1;
	            }
	        }
	    }

	    // append any leftover line
	    if (currentLine.length() > 0) {
	        result.append(currentLine.toString().trim());
	    }

	    return result.toString();
	}

	
	public static String getPocketName(int pocket) {
		switch(pocket) {
		case MEDICINE:
			return "Medicine";
		case OTHER:
			return "Other";
		case BALLS:
			return "Balls";
		case TMS:
			return "TMs";
		case HELD_ITEM:
			return "Held Items";
		case BERRY:
			return "Berries";
		case KEY_ITEM:
			return "Key Items";
		default:
			return "getPocketName() doesn't have a case for " + pocket;
		}
	}
	
	public String superToString() {
		return super.toString();
	}

	public int getStat() {
		if (this == ROOM_SERVICE) return -4;
		if (this == GRASSY_SEED || this == ELECTRIC_SEED) return 1;
		if (this == PSYCHIC_SEED || this == SPARKLY_SEED) return 3;
		if (this == SNOWBALL || this == DAMAGED_SNOWBALL) return 0;
		if (this == ADRENALINE_ORB) return 4;
		if (this == ABSORB_BULB || this == DAMAGED_BULB) return 2;
		if (this == LUMINOUS_MOSS || this == DAMAGED_MOSS) return 3;
		if (this == CELL_BATTERY || this == DAMAGED_BATTERY) return 0;
		return 10;
	}

	public boolean isTrickable() {
		return this != null
				&& this.isChoiceItem() || this == Item.RING_TARGET
				|| this == Item.FLAME_ORB || this == Item.TOXIC_ORB
				|| this == Item.FROST_ORB
				|| this == Item.LAGGING_TAIL || this == Item.IRON_BALL
				|| this == Item.STICKY_BARB;
	}
}
