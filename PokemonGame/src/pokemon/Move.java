package pokemon;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import pokemon.Field.Effect;

public enum Move {
	ABDUCT(0,100,0,0,2,0,PType.GALACTIC,"Abducts the foe and forces their next move to be used on themselves. Can be used once every other turn, and not on the first turn out.",false,5),
	ABSORB(20,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt to foe",false,25),
	ABYSSAL_CHOP(-1,90,50,0,0,0,PType.DRAGON,"% chance to paralyse foe. Damage is doubled if foe is paralyzed",true,10),
	ACCELEROCK(40,100,0,0,0,1,PType.ROCK,"Always goes first",true,15),
	ACID(40,100,10,0,1,0,PType.POISON,"% chance to lower foe's Sp.Def by 1",false,30),
	ACID_ARMOR(0,1000,0,0,2,0,PType.POISON,"Raises user's Defense by 2",false,15),
	ACID_SPRAY(40,100,100,0,1,0,PType.POISON,"% chance to lower foe's Sp.Def by 2",false,20),
	ACROBATICS(-1,100,0,0,0,0,PType.FLYING,"Power is doubled if user doesn't have a held item",true,15),
	ACUPRESSURE(0,1000,0,0,2,0,PType.NORMAL,"Raises a random stat by 2",false,30),
	AERIAL_ACE(60,1000,0,0,0,0,PType.FLYING,"This attack always hits",true,20),
	AEROBLAST(100,95,0,1,1,0,PType.FLYING,"Boosted Crit Rate",false,5),
	AGILITY(0,1000,0,0,2,0,PType.FLYING,"Raises user's Speed by 2",false,15),
	AIR_CUTTER(55,95,0,1,1,0,PType.FLYING,"Boosted Crit Rate",false,25),
	AIR_SLASH(75,95,30,0,1,0,PType.FLYING,"% chance of causing foe to flinch",false,15),
	ALCHEMY(0,1000,0,0,2,0,PType.MAGIC,"Heals 33% HP, if user holds an item will remove it and heal 100%",false,10), // TODO
	AMNESIA(0,1000,0,0,2,0,PType.PSYCHIC,"Raises user's Sp.Def by 2",false,20),
	ANCIENT_POWER(60,100,10,0,1,0,PType.ROCK,"% chance to raise all of the user's stats by 1",false,5),
	AQUA_JET(40,100,0,0,0,1,PType.WATER,"Always goes first",true,15),
	AQUA_RING(0,1000,0,0,2,0,PType.WATER,"Restores a small amount of HP at the end of every turn",false,15),
	AQUA_TAIL(90,90,0,0,0,0,PType.WATER,"A normal attack",true,10),
	AQUA_VEIL(0,1000,0,0,2,4,PType.WATER,"User protects itself, can't be used in succession. Lowers foe's SpA by 1 if they use a move into the protect.",false,5),
	ARCANE_SPELL(90,95,100,0,1,0,PType.MAGIC,"% to lower the Base Power of all foe's moves by 10",false,10),
	AROMATHERAPY(0,1000,0,0,2,0,PType.GRASS,"Cures team of any status conditions",false,5),
	ASTONISH(30,100,30,0,0,0,PType.GHOST,"% chance of causing foe to flinch",true,15),
	AURA_SPHERE(90,1000,0,0,1,0,PType.FIGHTING,"This attack always hits",false,15),
	AURORA_BEAM(75,100,10,0,1,0,PType.ICE,"% chance to lower foe's Attack by 1",false,20),
	AURORA_BOOST(0,1000,0,0,2,0,PType.MAGIC,"Raises user's Defense and Sp.Def by 1, and user's Sp.Atk by 2",false,10),
	AURORA_GLOW(0,1000,0,0,2,0,PType.LIGHT,"Creates a field effect that heals all LIGHT, ICE and GALACTIC Pokemon on your team for 1/8 HP for 5 turns",false,15),
	AURORA_VEIL(0,1000,0,0,2,0,PType.ICE,"Can only be used in SNOW, reduces both physical and special damage on user's team recieved for 5 turns",false,10),
	AUTOTOMIZE(0,1000,0,0,2,0,PType.STEEL,"Raises user's Speed by 2",false,15),
	BABY$DOLL_EYES(0,100,0,0,2,1,PType.LIGHT,"Lowers foe's Attack by 1, moves first",false,30),
	BATON_PASS(0,1000,0,0,2,0,PType.NORMAL,"Causes user to switch and passes all stat changes with it",false,3),
	BEAT_UP(12,100,0,0,0,0,PType.DARK,"Attacks once per healthy Pokemon on your team",false,10),
	BEEFY_BASH(100,85,50,0,0,-1,PType.FIGHTING,"% chance to paralyze foe, moves last",true,5),
	BELCH(120,100,0,0,1,0,PType.POISON,"Only works on the first turn out",false,10),
	BELLY_DRUM(0,1000,0,0,2,0,PType.NORMAL,"Maximizes Attack at the cost of 1/2 HP",false,10),
	BIND(15,85,100,0,0,0,PType.NORMAL,"% to spin foe for 4-5 turns. While foe is spun, it takes 1/8 HP in damage, and cannot switch",true,20),
	BITE(60,100,30,0,0,0,PType.DARK,"% chance of causing foe to flinch",true,20),
	BITTER_MALICE(75,100,30,0,1,0,PType.GHOST,"% chance to Frostbite foe",false,10),
	BLACK_HOLE_ECLIPSE(140,100,100,0,1,0,PType.GALACTIC,"% chance to raise user's Sp.Def by 1, user must charge on the first turn",false,5),
	BLAST_BURN(150,90,0,0,1,0,PType.FIRE,"User must rest after using this move",false,5),
	BLAZE_KICK(90,100,10,1,0,0,PType.FIRE,"% chance to Burn foe",true,10),
	BLIZZARD(110,70,20,0,1,0,PType.ICE,"% chance to Frostbite foe, doesn't check accuracy in SNOW",false,5),
	BLUE_FLARE(130,85,20,0,1,0,PType.FIRE,"% chance to Burn foe",false,5),
	BODY_PRESS(80,100,0,0,0,0,PType.FIGHTING,"Uses user's Defense stat instead of Attack",true,10),
	BODY_SLAM(85,100,30,0,0,0,PType.NORMAL,"% chance to Paralyze foe",true,15),
	BOLT_STRIKE(130,85,20,0,0,0,PType.ELECTRIC,"% chance to Paralyze foe",true,5),
	BOUNCE(85,85,30,0,0,0,PType.FLYING,"% chance to Paralyze foe. Goes into the air on the first turn, and attacks on the second",true,5),
	BRANCH_POKE(40,100,0,0,0,0,PType.GRASS,"A normal attack",true,35),
	BRAVE_BIRD(120,100,0,0,0,0,PType.FLYING,"User takes 1/3 of damage inflicted",true,10),
	BREAKING_SWIPE(60,100,100,0,0,0,PType.DRAGON,"% chance to lower foe's Attack by 1",true,15),
	BRICK_BREAK(75,100,100,0,0,0,PType.FIGHTING,"% to break Screen effects",true,15),
	BRINE(-1,100,0,0,1,0,PType.WATER,"Damage is doubled if foe is below 50% HP",false,10),
	BRUTAL_SWING(60,100,0,0,0,0,PType.DARK,"A normal attack",true,20),
	BUBBLE(20,100,0,0,1,0,PType.WATER,"A normal attack",false,30),
	BUBBLEBEAM(65,100,10,0,1,0,PType.WATER,"% to lower foe's Speed by 1",false,20),
	BUG_BITE(60,100,100,0,0,0,PType.BUG,"% chance to eat foe's berry",true,20),
	BUG_BUZZ(90,100,10,0,1,0,PType.BUG,"% chance to lower foe's Sp.Def by 1",false,20),
	BULK_UP(0,1000,0,0,2,0,PType.FIGHTING,"Raises user's Attack and Defense by 1",false,10),
	BULLDOZE(60,100,100,0,0,0,PType.GROUND,"% chance to lower foe's Speed by 1",false,20),
	BULLET_PUNCH(40,100,0,0,0,1,PType.STEEL,"Always goes first",true,15),
	BULLET_SEED(25,100,0,0,0,0,PType.GRASS,"Attacks 2-5 times",false,30),
	BURN_UP(130,100,100,0,1,0,PType.FIRE,"% to cause user to lose its FIRE type if it has it",false,5),
	CALM_MIND(0,1000,0,0,2,0,PType.PSYCHIC,"Raises user's Sp.Atk and Sp.Def by 1",false,10),
	CAPTIVATE(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Sp.Atk by 2",false,10),
	CHANNELING_BLOW(65,100,0,3,0,0,PType.FIGHTING,"This move always Crits",true,15),
	CHARGE(0,1000,0,0,2,0,PType.ELECTRIC,"User's next electric-type attack damage is doubled. Raises user's Sp.Def by 1",false,20),
	CHARGE_BEAM(50,90,70,0,1,0,PType.ELECTRIC,"% chance to raise user's Sp.Atk by 1",false,10),
	CHARM(0,100,0,0,2,0,PType.LIGHT,"Lowers foe's Attack by 2",false,15),
	CHROMO_BEAM(80,100,0,0,1,0,PType.LIGHT,"30% chance to double in Base Power",false,5),
	CIRCLE_THROW(60,90,100,0,0,-6,PType.FIGHTING,"% chance to switch out foe, always moves last",true,10),
	CLOSE_COMBAT(120,100,100,0,0,0,PType.FIGHTING,"% to lower user's Defense and Sp.Def by 1",true,5),
	COIL(0,1000,0,0,2,0,PType.POISON,"Raises user's Atk, Def, and Acc by 1",false,15),
	COMET_CRASH(-1,90,0,0,0,0,PType.GALACTIC,"Damage is doubled if user's HP is full",true,5),
	COMET_PUNCH(55,100,0,0,0,0,PType.GALACTIC,"A normal attack",true,15),
	CONFUSE_RAY(0,100,0,0,2,0,PType.GHOST,"Confuses foe",false,10),
	CONFUSION(50,100,10,0,1,0,PType.PSYCHIC,"% chance to Confuse foe",false,25),
	CORE_ENFORCER(100,100,10,0,1,0,PType.GALACTIC,"% chance to lower foe's Attack and Sp.Atk by 1",false,10),
	COSMIC_POWER(0,1000,0,0,2,0,PType.GALACTIC,"Raises user's Def and Sp.Def by 1",false,10),
	COTTON_GUARD(0,1000,0,0,2,0,PType.GRASS,"Raises user's Defense by 3",false,5),
	COVET(60,100,100,0,0,0,PType.NORMAL,"% chance to steal opponents item",true,25),
	CROSS_CHOP(100,80,0,1,0,0,PType.FIGHTING,"Boosted Crit rate",true,5),
	CROSS_POISON(90,100,10,1,0,0,PType.POISON,"% chance to Poison foe, boosted Crit rate",true,10),
	CRUNCH(80,100,30,0,0,0,PType.DARK,"% chance to lower foe's Defense by 1",true,15),
	CURSE(0,1000,0,0,2,0,PType.GHOST,"User loses half of its total HP. In exchance, foe takes 1/4 of its max HP at the end of every turn",false,10),
	CUT(55,95,0,3,0,0,PType.NORMAL,"This move always Crits",true,20),
	DARK_PULSE(80,100,30,0,1,0,PType.DARK,"% chance of causing foe to flinch",false,15),
	DARK_VOID(0,80,0,0,2,0,PType.GALACTIC,"Causes foe to sleep",false,10),
	DARKEST_LARIAT(85,100,0,0,0,0,PType.DARK,"Ignores Defense and Evasion changes of foe",true,10),
	DAZZLING_GLEAM(80,100,0,0,1,0,PType.LIGHT,"A normal attack",false,10),
	DECK_CHANGE(0,100,0,0,2,0,PType.MAGIC,"Swaps the foe's Attack and SpA stats",false,5),
	DEEP_SEA_BUBBLE(100,100,0,0,1,0,PType.WATER,"A normal attack. Turns into Draco Meteor when used by Kissyfishy-D",false,5),
	DEFENSE_CURL(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Defense by 1",false,35),
	DEFOG(0,1000,0,0,2,0,PType.FLYING,"Lowers foe's Evasion by 1, clears all hazards, terrain and screens from both sides",false,15),
	DESOLATE_VOID(65,85,50,0,1,0,PType.GALACTIC,"% chance to Paralyze, Sleep or Frostbite foe",false,10),
	DESTINY_BOND(0,1000,0,0,2,1,PType.GHOST,"Always goes first; can't be used twice in a row. If foe knocks out user the same turn, foe faints as well",false,5),
	DETECT(0,1000,0,0,2,4,PType.FIGHTING,"Protects user, can't be used in succession",false,5),
	DIAMOND_STORM(100,95,50,0,1,0,PType.ROCK,"% chance to raise user's Defense by 2",false,5),
	DIG(80,100,0,0,0,0,PType.GROUND,"A two turn attack. Digs underground on the first, attacks on the second",true,15),
	DIRE_CLAW(80,100,50,0,0,0,PType.POISON,"% chance to Paralyze, Poison, or Sleep foe",true,15),
	DISCHARGE(80,100,30,0,1,0,PType.ELECTRIC,"% chance to Paralyze foe",false,15),
	DISENCHANT(70,100,0,0,1,0,PType.MAGIC,"Clears all stat changes on foe before attacking",false,15),
	DIVE(80,100,0,0,0,0,PType.WATER,"A two turn attack. Dives underwater on the first, attacks on the second",true,15),
	DIZZY_PUNCH(70,100,20,0,0,0,PType.NORMAL,"% to confuse foe",true,10),
	DOUBLE_HIT(40,100,0,0,0,0,PType.NORMAL,"Attacks twice",true,20),
	DOUBLE_KICK(30,100,0,0,0,0,PType.FIGHTING,"Attacks twice",true,30),
	DOUBLE_SLAP(15,95,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,25),
	DOUBLE_TEAM(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Evasion by 1",false,10),
	DOUBLE$EDGE(120,100,0,0,0,0,PType.NORMAL,"User takes 1/3 of damage inflicted",true,15),
	DRACO_METEOR(130,90,100,0,1,0,PType.DRAGON,"% to lower user's Sp.Atk by 2",false,5),
	DRAGON_ASCENT(120,100,100,0,0,0,PType.FLYING,"% to lower user's Defense and Sp.Def by 1",true,5),
	DRAGON_BREATH(60,100,30,0,1,0,PType.DRAGON,"% chance to Paralyze foe",false,20),
	DRAGON_CLAW(85,100,0,1,0,0,PType.DRAGON,"Boosted Crit rate",true,15),
	DRAGON_DANCE(0,1000,0,0,2,0,PType.DRAGON,"Raises user's Attack and Speed by 1",false,5),
	DRAGON_DARTS(50,100,0,0,0,0,PType.DRAGON,"Attacks twice",false,10),
	DRAGON_PULSE(85,100,0,0,1,0,PType.DRAGON,"A normal attack",false,10),
	DRAGON_RAGE(0,100,0,-1,1,0,PType.DRAGON,"Always does 40 HP damage",false,10),
	DRAGON_RUSH(100,75,20,0,0,0,PType.DRAGON,"% chance of causing foe to flinch",true,10),
	DRAGON_TAIL(60,90,100,0,0,-6,PType.DRAGON,"% chance to switch out foe, always moves last",true,10),
	DRAIN_PUNCH(75,100,0,0,0,0,PType.FIGHTING,"Heals 50% of damage dealt to foe",true,10),
	DRAINING_KISS(50,100,0,0,1,0,PType.LIGHT,"Heals 75% of damage dealt to foe",true,10),
	DREAM_EATER(100,100,0,0,1,0,PType.PSYCHIC,"Only works if target is asleep. Heals 50% of damage dealt to foe",false,15),
	DRILL_PECK(80,100,0,1,0,0,PType.FLYING,"Boosted Crit rate",true,20),
	DRILL_RUN(80,95,0,1,0,0,PType.GROUND,"Boosted Crit rate",true,15),
	DUAL_CHOP(40,90,0,0,0,0,PType.DRAGON,"Attacks twice",true,15),
	DYNAMIC_PUNCH(100,50,100,0,0,0,PType.FIGHTING,"% chance to confuse foe",true,5),
	EARTH_POWER(90,100,10,0,1,0,PType.GROUND,"% chance to lower foe's Sp.Def by 1",false,10),
	EARTHQUAKE(100,100,0,0,0,0,PType.GROUND,"A normal attack",false,10),
	ELECTRIC_TERRAIN(0,1000,0,0,2,0,PType.ELECTRIC,"Sets the terrain to ELECTRIC for 5 turns",false,10),
	ELECTRO_BALL(-1,100,0,0,1,0,PType.ELECTRIC,"Power is higher the faster the user is than the target",false,10),
	ELECTROWEB(55,95,100,0,1,0,PType.ELECTRIC,"% chance to lower foe's Speed by 1",false,15),
	ELEMENTAL_SPARKLE(45,90,0,0,1,0,PType.MAGIC,"A random Grass, Fire, or Water move is also used",false,10),
	EMBER(40,100,10,0,1,0,PType.FIRE,"% chance to Burn foe",false,25),
	ENCORE(0,100,0,0,2,0,PType.NORMAL,"Causes foe to use their last used move for 4 turns",false,5),
	ENDEAVOR(0,100,0,0,0,0,PType.NORMAL,"Sets the foe's health equal to the user's, fails if foe's health is greater than user's",true,5),
	ENDURE(0,1000,0,0,2,4,PType.NORMAL,"The user will survive on at least 1 HP from the next attack. Can't be used in succession",false,10),
	ENERGY_BALL(90,100,10,0,1,0,PType.GRASS,"% chance to lower foe's Sp.Def by 1",false,10),
	ENTRAINMENT(0,100,0,0,2,0,PType.NORMAL,"Changes foe's ability to the user's",false,15),
	ERUPTION(-1,100,0,0,1,0,PType.FIRE,"Power is higher the more HP the user has",false,5),
	EXPANDING_FORCE(-1,100,0,0,1,0,PType.PSYCHIC,"Power is 1.5x if the terrain is PSYCHIC",false,10),
	EXPLOSION(250,100,0,0,0,0,PType.NORMAL,"User faints",false,5),
	EXTRASENSORY(80,100,10,0,1,0,PType.PSYCHIC,"% chance of flinching foe",false,20),
	EXTREME_SPEED(80,100,0,0,0,2,PType.NORMAL,"Always goes first",true,5),
	FACADE(-1,100,0,0,0,0,PType.NORMAL,"Damage is doubled if the user has a status condition",true,20),
	FAILED_SUCKER(0,100,0,0,0,0,PType.DARK,"If you're seeing this, something went horribly wrong",false,1),
	FAKE_OUT(40,100,100,0,0,3,PType.NORMAL,"% chance to cause foe to flinch. Can only be used on the first turn out",true,10),
	FAKE_TEARS(0,100,0,0,2,0,PType.DARK,"Lowers foe's Sp.Def by 2",false,20),
	FALSE_SURRENDER(80,1000,0,0,0,0,PType.DARK,"This attack never misses",true,10),
	FALSE_SWIPE(40,100,0,0,0,0,PType.NORMAL,"Always leaves the foe with at least 1 HP",true,35),
	FATAL_BIND(70,85,100,0,0,0,PType.BUG,"% to cause the user and foe to faint in 3 turns",true,3),
	FEATHER_DANCE(0,100,0,0,2,0,PType.FLYING,"Lowers foe's Attack by 2",false,15),
	FEINT(30,100,0,0,0,2,PType.NORMAL,"Always goes first, and hits through protect",false,10),
	FEINT_ATTACK(60,1000,0,0,0,0,PType.DARK,"This attack always hits",true,20),
	FELL_STINGER(50,100,0,0,0,0,PType.BUG,"Raises user's attack by 3 if it faints the foe",true,15),
	FIERY_DANCE(80,100,50,0,1,0,PType.FIRE,"% chance to raise user's Sp.Atk by 1",false,10),
	FIERY_WRATH(90,100,20,0,1,0,PType.DARK,"% of flinching foe",false,10),
	FIRE_BLAST(110,85,10,0,1,0,PType.FIRE,"% chance to Burn foe",false,5),
	FIRE_FANG(65,95,10,0,0,0,PType.FIRE,"% of flinching and/or Burning foe",true,15),
	FIRE_PUNCH(75,100,20,0,0,0,PType.FIRE,"% to Burn foe",true,15),
	FIRE_SPIN(35,85,100,0,1,0,PType.FIRE,"% to spin non-FIRE foes for 4-5 turns. While foe is spun, it takes 1/8 HP in damage, and cannot switch",false,15),
	FIRST_IMPRESSION(90,100,0,0,0,1,PType.BUG,"Always attacks first, fails after the first turn a user is out in battle",true,10),
	FISSURE(0,30,0,0,0,0,PType.GROUND,"If this move hits, it always K.Os foe",false,5),
	FLAIL(-1,100,0,0,0,0,PType.NORMAL,"Power is higher the lower HP the user has",true,15),
	FLAME_BURST(70,100,0,0,1,0,PType.FIRE,"A normal attack",false,15),
	FLAME_CHARGE(50,100,100,0,0,0,PType.FIRE,"% chance to raise user's Speed by 1",true,15),
	FLAME_WHEEL(70,100,10,0,0,0,PType.FIRE,"% to Burn foe",true,20),
	FLAMETHROWER(90,100,10,0,1,0,PType.FIRE,"% to Burn foe",false,10),
	FLARE_BLITZ(120,100,10,0,0,0,PType.FIRE,"% to Burn foe, user takes 1/3 of damage inflicted",true,15),
	FLASH(0,1000,0,0,2,0,PType.LIGHT,"Lowers foe's Accuracy by 1, and raises user's Sp.Atk by 1",false,10),
	FLASH_CANNON(80,100,10,0,1,0,PType.STEEL,"% chance to lower foe's Sp.Def by 1",false,10),
	FLASH_DARTS(25,90,0,1,0,0,PType.LIGHT,"Hits 2-5 times, each hit has a boosted crit rate",false,15),
	FLASH_RAY(40,100,50,0,1,0,PType.LIGHT,"% chance to lower foe's Accuracy by 1",false,25),
	FLATTER(0,100,0,0,2,0,PType.DARK,"Confuses foe, and raises their Sp.Atk by 2",false,15),
	FLOODLIGHT(0,1000,0,0,2,0,PType.LIGHT,"Sets a floodlight on opponents side. This will cause a foe switching in to have their Evasion lowered by 1 stage",false,10),
	FLIP_TURN(60,100,0,0,0,0,PType.WATER,"Causes user to switch out after use",true,10),
	FLY(100,100,0,0,0,0,PType.FLYING,"Goes into the air on the first turn, and attacks on the second",true,5),
	FOCUS_BLAST(120,70,10,0,1,0,PType.FIGHTING,"% to lower foe's Sp.Def",false,5),
	FOCUS_ENERGY(0,1000,0,0,2,0,PType.NORMAL,"Boosts the user's crit rate by 2",false,20),
	FORCE_PALM(60,100,30,0,0,0,PType.FIGHTING,"% chance to Paralyze foe",true,10),
	FORESIGHT(0,1000,0,0,2,0,PType.MAGIC,"Indentifies foe, replacing their Ghost typing with Normal if they have it. It also raises user's Accuracy by 1 stage",false,35),
	FORESTS_CURSE(0,100,0,0,2,0,PType.GRASS,"Changes foe's type to GRASS",false,20),
	FOUL_PLAY(95,100,0,0,0,0,PType.DARK,"Uses the foe's attack stat instead of the user's",true,15),
	FREEZE$DRY(70,100,10,0,1,0,PType.ICE,"% chance to Frostbite foe, super effective against WATER types too",false,15),
	FREEZING_GLARE(90,100,30,0,1,0,PType.PSYCHIC,"% chance to Frostbite foe",false,10),
	FRENZY_PLANT(150,90,0,0,1,0,PType.GRASS,"User must rest after using this move",false,10),
	FROSTBIND(0,90,0,0,2,0,PType.ICE,"Inflicts foe with Frostbite, ICE Pokemon can't miss this move",false,10),
	FRUSTRATION(-1,100,0,0,0,0,PType.NORMAL,"Base Power is based on user's friendship: the lower, the stronger",true,20),
	FURY_ATTACK(18,95,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,20),
	FURY_CUTTER(-1,95,0,0,0,0,PType.BUG,"Power increases the more times this move is used in succession",true,10),
	FURY_SWIPES(18,95,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,15),
	FUSION_BOLT(100,100,0,0,0,0,PType.ELECTRIC,"This attack ignores the effects of the foe's ability",false,5),
	FUSION_FLARE(100,100,0,0,1,0,PType.FIRE,"This attack ignores the effects of the foe's ability",false,5),
	GALAXY_BLAST(90,100,0,0,1,0,PType.GALACTIC,"A normal attack",false,10),
	GASTRO_ACID(0,100,0,0,2,0,PType.POISON,"Supresses the foe's ability",false,20),
	GENESIS_SUPERNOVA(120,95,0,0,1,0,PType.GALACTIC,"User takes 1/3 of damage dealt as recoil",false,10),
	GEOMANCY(0,1000,0,0,2,0,PType.LIGHT,"Charges on the first turn, raises user's Sp.Atk, Sp.Def and Speed by 2 on the second",false,5),
	GIGA_DRAIN(75,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt to foe",false,10),
	GIGA_IMPACT(150,90,0,0,0,0,PType.NORMAL,"User must rest after using this move",true,5),
	GLACIATE(65,95,100,0,1,0,PType.ICE,"% chance to lower foe's Speed by 1",false,10),
	GLARE(0,100,0,0,2,0,PType.NORMAL,"Paralyzes foe",false,10),
	GLITTER_DANCE(0,1000,0,0,2,0,PType.LIGHT,"Raises user's Sp.Atk and Speed by 1",false,10),
	GLITTERING_SWORD(95,100,20,0,0,0,PType.LIGHT,"% chance to lower foe's Defense by 1",true,10),
	GLITTERING_TORNADO(55,100,30,0,1,0,PType.LIGHT,"% chance to lower foe's Accuracy by 1",false,25),
	GLITZY_GLOW(80,100,30,0,1,0,PType.LIGHT,"% chance to raise user's Sp.Def by 1",false,15),
	GRASS_KNOT(-1,100,0,0,1,0,PType.GRASS,"Damage is based on how heavy foe is",true,20),
	GRASS_WHISTLE(0,55,0,0,2,0,PType.GRASS,"Foe falls asleep",false,15),
	GRASSY_TERRAIN(0,1000,0,0,2,0,PType.GRASS,"Sets the terrain to GRASSY for 5 turns",false,10),
	GRAVITY(0,1000,0,0,2,0,PType.GALACTIC,"Sets GRAVITY for 6 turns, in which the accuracy of all Pokemon is increased",false,10),
	GRAVITY_PUNCH(40,100,0,0,0,2,PType.PSYCHIC,"Always goes first with extra priority. Still works in PSYCHIC TERRAIN.",true,5),
	GROWL(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Attack by 1",false,35),
	GROWTH(0,1000,0,0,2,0,PType.GRASS,"Raises user's Attack and Sp.Atk by 1, 2 each in the SUN",false,15),
	GUILLOTINE(0,30,0,0,0,0,PType.NORMAL,"If this move hits, it always K.Os foe",true,5),
	GUNK_SHOT(120,80,30,0,0,0,PType.POISON,"% chance to Poison foe",false,5),
	GUST(40,100,0,0,1,0,PType.FLYING,"A normal attack",false,30),
	GYRO_BALL(-1,100,0,0,0,0,PType.STEEL,"The lower the user's speed compared to the foe, the more power",true,10),
	HAMMER_ARM(100,90,100,0,0,0,PType.FIGHTING,"% chance to lower user's speed by 1",true,10),
	HARDEN(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Defense by 1",false,25),
	HAZE(0,1000,0,0,2,0,PType.ICE,"Clears all stat changes on the field",false,25),
	HEAD_SMASH(150,80,0,0,0,0,PType.ROCK,"User takes 1/2 of damage inflicted",true,5),
	HEADBUTT(70,100,30,0,0,0,PType.NORMAL,"% chance of causing foe to flinch",true,15),
	HEAL_PULSE(0,1000,0,0,2,0,PType.PSYCHIC,"Heals foe by 50% HP",false,10),
	HEALING_CIRCLE(0,1000,0,0,2,0,PType.MAGIC,"Creates a circle on the user's side that heals incoming Pokemon for 25% of their HP, lasts 8 turns",false,10),
	HEALING_WISH(0,1000,0,0,2,0,PType.PSYCHIC,"User faints. The next Pokemon sent in will be fully healed",false,5),
	HEAT_CRASH(-1,100,0,0,0,0,PType.FIRE,"Damage is based on how heavy the user is compared to the foe",true,10),
	HEAT_WAVE(95,90,10,0,1,0,PType.FIRE,"% to Burn foe",false,10),
	HEAVY_SLAM(-1,100,0,0,0,0,PType.STEEL,"Damage is based on how heavy the user is compared to the foe",true,10),
	HEX(-1,100,0,0,1,0,PType.GHOST,"Damage is doubled if the foe has a status condition",false,10),
	HEX_CLAW(75,100,100,0,0,0,PType.MAGIC,"% chance to disable the target's last used move for 3 turns",true,10), // TODO
	HI_JUMP_KICK(130,90,0,0,0,0,PType.FIGHTING,"If this attack misses, user takes 50% of its max HP",true,10),
	HIDDEN_POWER(60,100,0,0,1,0,PType.NORMAL,"The type of this move depends on the user's IVs",false,15),
	HOCUS_POCUS(70,95,20,0,1,0,PType.MAGIC,"% to inflict foe with a random Status condition",false,10),
	HONE_CLAWS(0,1000,0,0,2,0,PType.DARK,"Raises user's Attack and Accuracy by 1",false,15),
	HORN_ATTACK(65,100,0,0,0,0,PType.NORMAL,"A normal attack",true,25),
	HORN_DRILL(0,30,0,0,0,0,PType.NORMAL,"If this move hits, it always K.Os foe",true,5),
	HORN_LEECH(75,100,0,0,0,0,PType.GRASS,"Heals 50% of damage dealt to foe",true,10),
	HOWL(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack by 1",false,20),
	HURRICANE(110,70,30,0,1,0,PType.FLYING,"% chance to confuse foe, doesn't check Accuracy in RAIN",false,10),
	HYDRO_CANNON(150,90,0,0,1,0,PType.WATER,"User must rest after using this move",false,5),
	HYDRO_PUMP(110,80,0,0,1,0,PType.WATER,"A normal attack",false,5),
	HYDRO_VORTEX(90,100,40,0,1,0,PType.WATER,"% chance of confusing foe or causing foe to flinch",false,5),
	HYPER_BEAM(150,90,0,0,1,0,PType.NORMAL,"User must rest after using this move",false,5),
	HYPER_FANG(80,90,10,0,0,0,PType.NORMAL,"% of causing foe to flinch",true,15),
	HYPER_VOICE(90,100,0,0,1,0,PType.NORMAL,"A normal attack",false,15),
	HYPNOSIS(0,60,0,0,2,0,PType.PSYCHIC,"Causes foe to sleep",false,10),
	ICE_BALL(-1,90,0,0,0,0,PType.ICE,"Attacks up to 5 times, damage doubles each time. While active, user cannot switch out",true,20),
	ICE_BEAM(90,100,10,0,1,0,PType.ICE,"% chance to Frostbite foe",false,10),
	ICE_FANG(65,95,10,0,0,0,PType.ICE,"% chance to Flinch and/or Frostbite foe",true,15),
	ICE_PUNCH(75,100,20,0,0,0,PType.ICE,"% chance to Frostbite foe",true,15),
	ICE_SHARD(40,100,0,0,0,1,PType.ICE,"Always goes first",false,15),
	ICE_SPINNER(80,100,100,0,0,0,PType.ICE,"% chance to remove the terrain if there is any",true,10),
	ICICLE_CRASH(85,90,30,0,0,0,PType.ICE,"% chance to cause foe to Flinch",false,10),
	ICICLE_SPEAR(25,100,0,0,0,0,PType.ICE,"Hits 2-5 times",false,25),
	ICY_WIND(55,95,100,0,1,0,PType.ICE,"% chance to lower foe's Speed by 1",false,15),
	INCINERATE(60,100,100,0,1,0,PType.FIRE,"% to burn up foe's berry",false,15),
	INFERNO(100,50,100,0,1,0,PType.FIRE,"% chance to Burn foe",false,5),
	INFESTATION(20,100,100,0,1,0,PType.BUG,"% to spin foe for 4-5 turns. While foe is spun, it takes 1/8 HP in damage, and cannot switch",true,15),
	INGRAIN(0,1000,0,0,2,0,PType.GRASS,"Restores a small amount of HP at the end of every turn, user can't switch out",false,15),
	IRON_BLAST(85,95,30,0,1,0,PType.STEEL,"% chance to cause foe to Flinch",false,20),
	IRON_DEFENSE(0,1000,0,0,2,0,PType.STEEL,"Raises user's Defense by 2",false,10),
	IRON_HEAD(80,100,30,0,0,0,PType.STEEL,"% of causing foe to flinch",true,15),
	IRON_TAIL(100,75,30,0,0,0,PType.STEEL,"% of lowering foe's Defense by 1",true,15),
	JAW_LOCK(80,100,100,0,0,0,PType.DARK,"% to cause foe to not be able to switch",true,5),
	KARATE_CHOP(50,100,0,1,0,0,PType.FIGHTING,"Boosted Crit rate",true,20),
	KNOCK_OFF(-1,100,100,0,0,0,PType.DARK,"% to remove foe's item",true,15),
	LAVA_LAIR(0,1000,0,0,2,4,PType.FIRE,"User protects itself, can't be used in succession. Burns foe if they make contact",false,5),
	LAVA_PLUME(80,100,30,0,1,0,PType.FIRE,"% to Burn foe",false,15),
	LAVA_SURF(90,100,0,0,1,0,PType.FIRE,"A normal attack",false,10),
	LEAF_BLADE(90,100,0,1,0,0,PType.GRASS,"Boosted Crit rate",true,10),
	LEAF_STORM(130,90,100,0,1,0,PType.GRASS,"% to lower user's Sp.Atk by 2",false,5),
	LEAF_TORNADO(65,90,50,0,1,0,PType.GRASS,"% to lower foe's Accuracy by 1",false,10),
	LEAFAGE(40,100,0,0,0,0,PType.GRASS,"A normal attack",false,35),
	LEECH_LIFE(80,100,0,0,0,0,PType.BUG,"Heals 50% of damage dealt",true,10),
	LEECH_SEED(0,90,0,0,2,0,PType.GRASS,"At the end of every turn, user steals 1/8 of foe's max HP",false,5),
	LEER(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 1",false,35),
	LICK(20,100,30,0,0,0,PType.GHOST,"% to Paralyze foe",true,30),
	LIFE_DEW(0,1000,0,0,2,0,PType.WATER,"Restores 25% HP",false,15),
	LIGHT_BEAM(60,100,20,0,1,0,PType.LIGHT,"% chance to raise user's Sp.Atk by 1",false,15),
	LIGHT_DRAIN(75,100,0,0,1,0,PType.LIGHT,"Heals 50% of damage dealt",false,10),
	LIGHT_OF_RUIN(140,90,0,0,1,0,PType.LIGHT,"User takes 1/3 of damage dealt as recoil",false,5),
	LIGHT_SCREEN(0,1000,0,0,2,0,PType.PSYCHIC,"Creates a screen that halves special damage on user's team for 5 turns",false,15),
	LIGHT_SPEED(80,100,0,0,0,2,PType.LIGHT,"Always goes first",false,5),
	LIQUIDATION(85,100,20,0,0,0,PType.WATER,"% chance to lower foe's Defense by 1",true,10),
	LOAD_FIREARMS(0,1000,0,0,2,0,PType.STEEL,"Raises user's Speed and Accuracy by 1, and doubles the power of a STEEL move if used next",false,10),
	LOCK$ON(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Accuracy by 6",false,5),
	LOVELY_KISS(0,75,0,0,2,0,PType.NORMAL,"Sleeps foe",false,5),
	LOW_KICK(-1,100,0,0,0,0,PType.FIGHTING,"Damage is based on how heavy foe is",true,20),
	LOW_SWEEP(65,100,100,0,0,0,PType.FIGHTING,"% chance to lower foe's Speed by 1",true,15),
	LUCKY_CHANT(0,1000,0,0,2,0,PType.NORMAL,"Protects user's team from Critical Hits for 8 turns",false,30),
	LUNAR_DANCE(0,1000,0,0,2,0,PType.PSYCHIC,"User faints. The next Pokemon sent in will be fully healed",false,10),
	LUSTER_PURGE(70,100,50,0,1,0,PType.LIGHT,"% chance to lower foe's Sp.Def by 1",false,10),
	MACH_PUNCH(40,100,0,0,0,1,PType.FIGHTING,"Always goes first",true,15),
	MAGIC_BLAST(30,95,0,0,1,0,PType.MAGIC,"A random Rock, Ground or Grass move is also used",false,10),
	MAGIC_CRASH(110,80,100,0,0,0,PType.MAGIC,"% to inflict foe with a random Status condition. User must rest after using",true,5),
	MAGIC_FANG(70,95,75,0,0,0,PType.MAGIC,"% to flinch foe if this move is Super-Effective against it",true,10),
	MAGIC_MISSILES(25,90,0,0,1,0,PType.MAGIC,"Hits 2-5 times, does Physical damage",false,15),
	MAGIC_POWDER(0,100,0,0,2,0,PType.MAGIC,"Changes foe's type to MAGIC",false,15),
	MAGIC_REFLECT(0,1000,0,0,2,0,PType.MAGIC,"Foe's next attack will be reflected against them. Can be used every other turn, and not on the first turn out.",false,5),
	MAGIC_ROOM(0,1000,0,0,2,0,PType.MAGIC,"Removes the effects of held items for 8 turns",false,10),
	MAGIC_TOMB(90,100,0,0,1,0,PType.MAGIC,"A normal attack",true,10),
	MAGICAL_LEAF(60,1000,0,0,1,0,PType.GRASS,"This move will never miss",false,20),
	MAGICAL_CRASH(110,95,100,0,0,0,PType.MAGIC,"% to inflict foe with a random Status condition. User must rest after using",true,5),
	MAGNET_BOMB(60,1000,0,0,0,0,PType.STEEL,"This move will never miss",false,20),
	MAGNET_RISE(0,1000,0,0,2,0,PType.STEEL,"User will float for 5 turns, causing it to be immune to all Ground-type attacks",false,10),
	MAGNITUDE(-1,100,0,0,0,0,PType.GROUND,"A random Magnitude between 4-10 will be used, corresponding to its power",false,25),
	MANA_PUNCH(80,100,100,0,0,0,PType.MAGIC,"% chance to raise a random stat by 1 or flinch foe",true,15),
	MEAN_LOOK(0,100,0,0,2,0,PType.NORMAL,"Causes foe to be unable to switch",false,5),
	MEGA_DRAIN(40,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt",false,15),
	MEGAHORN(120,85,0,0,0,0,PType.BUG,"A normal attack",true,10),
	MEMENTO(0,100,0,0,2,0,PType.DARK,"User faints. Foe's Attack and Sp.Atk are lowered by 2 stages",false,5),
	METAL_CLAW(50,95,10,0,0,0,PType.STEEL,"% chance to raise user's Attack by 1",true,30),
	METAL_SOUND(0,100,0,0,2,0,PType.STEEL,"Lowers foe's Sp.Def by 2",false,30),
	METEOR_ASSAULT(120,100,100,0,0,0,PType.GALACTIC,"% to lower user's Attack and Defense by 1",false,5),
	METEOR_BEAM(120,90,100,0,1,0,PType.ROCK,"% to raise the user's Sp.Atk by 1, user must charge on the first turn",false,10),
	METEOR_MASH(90,90,20,0,0,0,PType.STEEL,"% chance to raise user's Attack by 1",true,10),
	METRONOME(0,1000,0,0,2,0,PType.MAGIC,"Uses a random move, can't be blocked by taunt",false,20),
	MIGHTY_CLEAVE(95,100,0,0,0,0,PType.ROCK,"Hits through protect",true,5),
	MIMIC(0,1000,0,0,2,0,PType.NORMAL,"Uses the move last used by the foe, fails if foe hasn't used a move yet",false,10),
	MIND_READER(0,1000,0,0,2,0,PType.PSYCHIC,"Raises user's Accuracy by 6",false,5),
	MINIMIZE(0,1000,0,0,2,0,PType.GHOST,"Raises user's Evasion by 2",false,5),
	MIRROR_MOVE(0,1000,0,0,2,0,PType.FLYING,"Uses the move last used by the foe, fails if foe hasn't used a move yet",false,15),
	MIRROR_SHOT(65,85,100,0,1,0,PType.STEEL,"% chance to lower foe's Accuracy by 1",false,10),
	MIST_BALL(70,100,50,0,1,0,PType.PSYCHIC,"% chance to lower foe's Sp.Atk by 1",false,15),
	MOLTEN_CONSUME(50,100,100,0,0,0,PType.FIRE,"% chance to Burn foe",true,5),
	MOLTEN_STEELSPIKE(100,90,30,0,1,0,PType.STEEL,"% chance to Burn foe",false,10),
	MOONBLAST(95,100,30,0,1,0,PType.LIGHT,"% chance to lower foe's Sp.Atk",false,5),
	MOONLIGHT(0,1000,0,0,2,0,PType.LIGHT,"Restores 1/2 of user's max HP, 2/3 in SUN, 1/4 in any other weather",false,5),
	MORNING_SUN(0,1000,0,0,2,0,PType.LIGHT,"Restores 1/2 of user's max HP, 2/3 in SUN, 1/4 in any other weather",false,5),
	MORTAL_SPIN(30,100,100,0,0,0,PType.POISON,"% to poison foe, and frees user of being Spun, Leech Seed, and Hazards",true,15),
	MUD_BOMB(65,85,30,0,1,0,PType.GROUND,"% to lower foe's Accuracy by 1",false,10),
	MUD_SHOT(55,95,100,0,1,0,PType.GROUND,"% chance to lower foe's Speed by 1",false,15),
	MUD_SPORT(0,1000,0,0,2,0,PType.GROUND,"Makes ELECTRIC moves deal 1/3 damage for 8 turns",false,15),
	MUD$SLAP(20,100,100,0,1,0,PType.GROUND,"% to lower foe's Accuracy by 1",false,15),
	MUDDY_WATER(90,85,30,0,1,0,PType.WATER,"% chance to lower foe's Accuracy by 1",false,10),
	MYSTICAL_FIRE(75,100,100,0,1,0,PType.FIRE,"% chance to lower foe's Sp.Atk by 1",false,10),
	NASTY_PLOT(0,1000,0,0,2,0,PType.DARK,"Raises user's Sp.Atk by 2",false,5),
	NEEDLE_ARM(90,100,30,0,0,0,PType.GRASS,"% chance to Flinch foe",true,10),
	NIGHT_DAZE(85,95,40,0,0,0,PType.DARK,"% chance to lower foe's Accuracy by 1",false,10),
	NIGHT_SHADE(0,100,-1,0,1,0,PType.GHOST,"Deals damage equal to user's level",false,15),
	NIGHT_SLASH(70,100,0,1,0,0,PType.DARK,"Boosted Crit rate",true,15),
	NIGHTMARE(0,100,0,0,2,0,PType.GHOST,"Foe loses 1/4 of max HP each turn; wears off when foe wakes up",false,15),
	NO_RETREAT(0,1000,0,0,2,0,PType.FIGHTING,"Raises all of user's stats by 1, user can't switch out. Can only be used once",false,5),
	NOBLE_ROAR(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Attack and Sp.Atk by 1",false,30),
	NUZZLE(20,100,100,0,0,0,PType.ELECTRIC,"% chance to Paralyze foe",true,10),
	OBSTRUCT(0,1000,0,0,2,4,PType.DARK,"User protects itself, can't be used in succession. Lowers foe's Defense by 2 if they make contact",false,5),
	ODOR_SLEUTH(0,1000,0,0,2,0,PType.NORMAL,"Indentifies foe, replacing their Ghost typing with Normal if they have it. It also lowers foe's Evasion by 1",false,35),
	OUTRAGE(120,100,0,0,0,0,PType.DRAGON,"User is locked into this move for 2-3 turns, Confuses user when the effect is done",true,10),
	OVERHEAT(130,90,100,0,1,0,PType.FIRE,"% to lower user's Sp.Atk by 2",false,5),
	PARABOLIC_CHARGE(65,100,0,0,1,0,PType.ELECTRIC,"Heals 50% of damage dealt to foe",false,20),
	PARTING_SHOT(0,100,0,0,2,0,PType.DARK,"Lower foe's Atk and Sp.Atk by 1, user switches out",false,5),
	PAYBACK(-1,100,0,0,0,0,PType.DARK,"Power is doubled if the user moves after foe",true,10),
	PECK(35,100,0,0,0,0,PType.FLYING,"A normal attack",true,30),
	PERISH_SONG(0,1000,0,0,2,0,PType.GHOST,"All Pokemon hearing this song will faint in 3 turns",false,5),
	PETAL_BLIZZARD(90,100,0,0,0,0,PType.GRASS,"A normal attack",false,15),
	PETAL_DANCE(120,100,0,0,1,0,PType.GRASS,"User is locked into this move for 2-3 turns, Confuses user when the effect is done",true,10),
	PHANTOM_FORCE(100,100,0,0,0,0,PType.GHOST,"A two turn attack. User vanishes on the first, and attacks on the second. Hits through protect.",true,10),
	PHOTON_GEYSER(130,90,100,0,1,0,PType.LIGHT,"% to lower user's Sp.Atk by 2",false,5),
	PIN_MISSILE(25,95,0,0,0,0,PType.BUG,"Hits 2-5 times",false,15),
	PISTOL_POP(110,70,0,0,0,0,PType.STEEL,"A normal attack",false,5),
	PLASMA_FISTS(100,100,0,0,0,0,PType.ELECTRIC,"A normal attack",true,10),
	PLAY_NICE(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Attack by 1",false,25),
	PLAY_ROUGH(90,90,10,0,0,0,PType.LIGHT,"% chance to lower foe's Attack by 1",true,10),
	PLUCK(60,100,100,0,0,0,PType.FLYING,"% chance to eat foe's berry",true,20),
	POISON_FANG(50,100,50,0,0,0,PType.POISON,"% to Toxic foe",true,15),
	POISON_GAS(0,80,0,0,2,0,PType.POISON,"Poisons foe",true,30),
	POISON_JAB(80,100,30,0,0,0,PType.POISON,"% to Poison foe",true,20),
	POISON_POWDER(0,75,0,0,2,0,PType.POISON,"Poisons foe",false,35),
	POISON_STING(15,100,30,0,0,0,PType.POISON,"% chance to Poison foe",false,35),
	POISON_TAIL(85,100,10,1,0,0,PType.POISON,"% chance to Poison foe. Boosted crit rate",true,15),
	POLTERGEIST(110,90,0,0,0,0,PType.GHOST,"Fails if the target isn't holding an item",false,5),
	POP_POP(70,80,0,0,0,0,PType.STEEL,"Attacks twice, seperate accuracy checks for each hit",false,5),
	POUND(40,100,0,0,0,0,PType.NORMAL,"A normal attack",true,30),
	POWDER_SNOW(40,100,50,0,1,0,PType.ICE,"% chance to Frostbite foe",false,25),
	POWER_GEM(80,100,0,0,1,0,PType.ROCK,"A normal attack",false,20),
	POWER_WHIP(120,85,0,0,0,0,PType.GRASS,"A normal attack",true,10),
	POWER$UP_PUNCH(40,100,100,0,0,0,PType.FIGHTING,"% chance to raise user's Attack by 1",true,10),
	PRISMATIC_LASER(100,100,0,0,1,0,PType.LIGHT,"A normal attack",false,10),
	PROTECT(0,1000,0,0,2,4,PType.NORMAL,"Protects user, can't be used in succession",false,10),
	PSYBEAM(65,100,10,0,1,0,PType.PSYCHIC,"% chance to confuse foe",false,20),
	PSYCHIC(90,100,10,0,1,0,PType.PSYCHIC,"% chance to lower foe's Sp.Def by 1",false,10),
	PSYCHIC_FANGS(85,100,100,0,0,0,PType.PSYCHIC,"% to break Screen effects",true,10),
	PSYCHIC_NOISE(75,100,100,0,1,0,PType.PSYCHIC,"% to inflict foe with the Heal Block effect",false,10),
	PSYCHIC_TERRAIN(0,1000,0,0,2,0,PType.PSYCHIC,"Changes the terrain to PSYCHIC for 5 turns",false,15),
	PSYCHO_CUT(70,100,0,1,0,0,PType.PSYCHIC,"Boosted Crit rate",false,20),
	PSYSHOCK(80,100,0,0,1,0,PType.PSYCHIC,"Uses foe's Defense instead of Sp.Def in damage calculation",false,15),
	PSYWAVE(0,100,0,0,1,0,PType.PSYCHIC,"Deals damage equal to user's level",false,15),
	QUICK_ATTACK(40,100,0,0,0,1,PType.NORMAL,"Always attacks first",true,15),
	QUICK_SPELL(40,100,0,0,1,1,PType.MAGIC,"Always attacks first",false,15),
	QUIVER_DANCE(0,1000,0,0,2,0,PType.BUG,"Raises user's Sp.Atk, Sp.Def and Speed by 1 stage",false,10),
	RADIANT_BREAK(75,100,100,0,0,0,PType.LIGHT,"% chance to lower foe's Sp.Atk by 1",true,15),
	RADIO_BURST(40,100,0,0,1,1,PType.GALACTIC,"Always goes first",false,25),
	RAGE(-1,100,0,0,0,0,PType.NORMAL,"Power increases the more times this move is used in succession",true,20),
	RAIN_DANCE(0,1000,0,0,2,0,PType.WATER,"Changes the weather to RAIN for 5 turns",false,5),
	RAINBOW_FLASH(50,100,0,0,1,1,PType.LIGHT,"Always goes first",false,10),
	RAPID_SPIN(50,100,100,0,0,0,PType.NORMAL,"% to raise user's Speed by 1, and frees user of being Spun, Leech Seed, and Hazards",true,35),
	RAZOR_LEAF(55,95,0,1,0,0,PType.GRASS,"Boosted Crit rate",false,25),
	RAZOR_SHELL(75,95,50,0,0,0,PType.WATER,"% chance to lower foe's Defense by 1",true,10),
	REBOOT(0,1000,0,0,2,0,PType.STEEL,"Clears user of any Status condition, and raises user's Speed by 1",false,10),
	RECOVER(0,1000,0,0,2,0,PType.NORMAL,"Restores 1/2 of user's max HP",false,5),
	REFLECT(0,1000,0,0,2,0,PType.PSYCHIC,"Creates a screen that halves physical damage on user's team for 5 turns",false,20),
	REST(0,1000,0,0,2,0,PType.PSYCHIC,"Restores user's HP to full and clears any status conditions, and user falls asleep for 2 turns",false,5),
	RETURN(-1,100,0,0,0,0,PType.NORMAL,"Type is user's Hidden Power type, and the power is up to 70 (based on friendship)",true,15),
	REVENGE(-1,100,0,0,0,0,PType.FIGHTING,"Power is doubled if user is slower than foe",true,10),
	REVERSAL(-1,100,0,0,0,0,PType.FIGHTING,"Deals more damage the lower the user's HP is",true,15),
	RISING_VOLTAGE(-1,100,0,0,1,0,PType.ELECTRIC,"Power is 2x if the terrain is ELECTRIC and foe is grounded",false,20),
	ROAR(0,1000,0,0,2,-6,PType.NORMAL,"Forces foe to switch out into a random teammate, always goes last",false,20),
	ROCK_BLAST(25,90,0,0,0,0,PType.ROCK,"Hits 2-5 times",false,10),
	ROCK_CLIMB(80,95,20,0,0,0,PType.ROCK,"% chance to confuse foe",true,10),
	ROCK_POLISH(0,1000,0,0,2,0,PType.ROCK,"Raises user's Speed by 2",false,20),
	ROCK_SLIDE(75,90,30,0,0,0,PType.ROCK,"% of causing foe to flinch",false,10),
	ROCK_SMASH(40,100,100,0,0,0,PType.FIGHTING,"% to lower foe's Defense by 1",true,30),
	ROCK_THROW(50,90,0,0,0,0,PType.ROCK,"A normal attack",false,15),
	ROCK_TOMB(60,95,100,0,0,0,PType.ROCK,"% to lower foe's Speed by 1",false,15),
	ROCK_WRECKER(150,90,0,0,0,0,PType.ROCK,"User takes 1/3 of damage dealt as recoil",false,5),
	ROCKFALL_FRENZY(75,95,100,0,1,0,PType.ROCK,"% to set up Stealth Rocks on foe's side",false,10),
	ROLLOUT(-1,90,0,0,0,0,PType.ROCK,"Attacks up to 5 times, damage doubles each time. While active, user cannot switch out",true,20),
	ROOST(0,1000,0,0,2,0,PType.FLYING,"Restores 1/2 of user's max HP and causes user to lose its FLYING type for the rest of the turn",false,5),
	ROOT_KICK(60,95,0,0,0,0,PType.ROCK,"A normal attack",true,20),
	ROUND(60,100,0,0,1,0,PType.NORMAL,"A normal attack",false,15),
	SACRED_FIRE(100,95,50,0,0,0,PType.FIRE,"% chance to Burn foe",false,5),
	SACRED_SWORD(90,100,0,0,0,0,PType.FIGHTING,"Ignores Defense and Evasion changes of foe",true,10),
	SAFEGUARD(0,1000,0,0,2,0,PType.NORMAL,"Protects user's team from Status effects for 8 turns",false,20),
	SAMBAL_SEAR(80,100,30,0,1,0,PType.GRASS,"% chance to Burn foe",false,15),
	SAND_ATTACK(0,100,0,0,2,0,PType.GROUND,"Lowers foe's Accuracy by 1",false,20),
	SANDSTORM(0,1000,0,0,2,0,PType.ROCK,"Changes the weather to SANDSTORM for 5 turns",false,10),
	SCALD(80,100,30,0,1,0,PType.WATER,"% chance to Burn foe",false,15),
	SCALE_SHOT(25,90,100,0,0,0,PType.DRAGON,"% chance to lower user's Defense by 1 and raise user's Speed by 1. Hits 2-5 times",false,20),
	SCARY_FACE(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Speed by 2",false,10),
	SCORCHING_SANDS(70,100,30,0,1,0,PType.GROUND,"% chance to burn foe",false,15),
	SCRATCH(40,100,0,0,0,0,PType.NORMAL,"A normal attack",true,35),
	SCREECH(0,85,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 2",false,35),
	SEA_DRAGON(0,1000,0,0,2,0,PType.MAGIC,"Transforms Kissyfishy into Kissyfishy-D: raised Atk, SpAtk, Acc by 1 if Kissyfishy-D",false,5),
	SEED_BOMB(80,100,0,0,0,0,PType.GRASS,"A normal attack",false,15),
	SEISMIC_TOSS(0,100,-1,0,0,0,PType.FIGHTING,"Damage dealt is equal to the user's level",true,20),
	SELF$DESTRUCT(200,100,0,0,0,0,PType.NORMAL,"User faints",false,5),
	SHADOW_BALL(80,100,30,0,1,0,PType.GHOST,"% to lower foe's Sp.Def by 1",false,15),
	SHADOW_CLAW(80,100,0,1,0,0,PType.GHOST,"Boosted crit rate",true,15),
	SHADOW_PUNCH(80,1000,0,0,0,0,PType.GHOST,"This attack never misses",true,20),
	SHADOW_SNEAK(40,100,0,0,0,1,PType.GHOST,"Always attacks first",true,15),
	SHEER_COLD(0,30,0,0,1,0,PType.ICE,"If this move hits, it always K.Os foe. Doesn't effect ICE types",false,5),
	SHELL_SMASH(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack, Sp.Atk, and Speed by 2, at the cost of lowering its Defense and Sp.Def by 1",false,5),
	SHIFT_GEAR(0,1000,0,0,2,0,PType.STEEL,"Raises user's Attack by 1 and Speed by 2",false,10),
	SHOCK_WAVE(60,1000,0,0,1,0,PType.ELECTRIC,"This attack never misses",false,20),
	SHOOTING_STARS(20,100,0,0,0,0,PType.GALACTIC,"Hits 2-5 times",false,10),
	SHORE_UP(0,1000,0,0,2,0,PType.GROUND,"Restores 1/2 of user's total HP, 2/3 in SANDSTORM",false,5),
	SIGNAL_BEAM(75,100,10,0,1,0,PType.BUG,"% chance to confuse foe",false,15),
	SILVER_WIND(60,100,10,0,1,0,PType.BUG,"% chance to raise all of user's stats",false,5),
	SIMPLE_BEAM(0,100,0,0,2,0,PType.NORMAL,"Changes foe's ability to SIMPLE",false,10),
	SING(0,55,0,0,2,0,PType.NORMAL,"Foe falls asleep",false,15),
	SKILL_SWAP(0,100,0,0,2,0,PType.PSYCHIC,"Swaps user and foe's abilities",false,10),
	SKULL_BASH(100,100,100,0,0,0,PType.NORMAL,"% to raise the user's Defense by 1, user must charge on the first turn",true,10),
	SKY_ATTACK(140,90,30,1,0,0,PType.FLYING,"% chance to flinch. User must charge up on the first turn, attacks on the second. Boosted Crit rate",false,5),
	SKY_UPPERCUT(85,90,0,0,0,0,PType.FIGHTING,"Super effective against FLYING types too",true,15),
	SLACK_OFF(0,1000,0,0,2,0,PType.NORMAL,"Restores 1/2 of user's total HP",false,5),
	SLAM(80,75,0,0,0,0,PType.NORMAL,"A normal attack",true,25),
	SLASH(70,100,0,1,0,0,PType.NORMAL,"Boosted Crit rate",true,20),
	SLEEP_POWDER(0,75,0,0,2,0,PType.GRASS,"Foe falls asleep",false,10),
	SLEEP_TALK(0,100,0,0,2,0,PType.NORMAL,"Picks a random move of the user's to use if the user is sleeping",false,10),
	SLOW_FALL(75,90,100,0,1,0,PType.PSYCHIC,"% chance to change foe's ability to LEVITATE",false,15),
	SLUDGE(65,100,30,0,1,0,PType.POISON,"% to Poison foe",false,15),
	SLUDGE_BOMB(90,100,30,0,1,0,PType.POISON,"% to Poison foe",false,10),
	SLUDGE_WAVE(95,100,10,0,1,0,PType.POISON,"% chance to Poison foe",false,10),
	SMACK_DOWN(50,100,100,0,0,0,PType.ROCK,"% to ground foe, meaning they can be hit by GROUND moves",false,15),
	SMART_STRIKE(70,1000,0,0,0,0,PType.STEEL,"This attack never misses",false,10),
	SMOG(20,70,50,0,1,0,PType.POISON,"% to Poison foe",false,30),
	SMOKESCREEN(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's accuracy by 1",false,20),
	SNARL(55,95,100,0,1,0,PType.DARK,"% chance to lower foe's Sp.Atk",false,15),
	SNORE(85,100,0,0,1,0,PType.NORMAL,"This attack only works if the user is asleep",false,25),
	SNOWSCAPE(0,1000,0,0,2,0,PType.ICE,"Sets the weather to SNOW for 5 turns",false,10),
	SOAK(0,100,0,0,2,0,PType.WATER,"Changes foe's type to WATER",false,15),
	SOLAR_BEAM(120,100,0,0,1,0,PType.GRASS,"User must charge up on the first turn, attacks on the second. No charge in SUN",false,10),
	SOLAR_BLADE(125,100,0,0,0,0,PType.GRASS,"User must charge up on the first turn, attacks on the second. No charge in SUN",true,10),
	SOLSTICE_BLADE(95,100,20,0,0,0,PType.LIGHT,"% chance to lower foe's Defense by 1, 1.5x damage in SUN and 0.5x damage in other weathers",false,10),
	SPACE_BEAM(60,100,30,0,1,0,PType.GALACTIC,"% chance to lower foe's Sp.Def",false,20),
	SPACIAL_REND(100,95,0,1,1,0,PType.GALACTIC,"Boosted Crit rate",false,5),
	SPARK(65,100,30,0,0,0,PType.ELECTRIC,"% to Paralyze foe",true,20),
	SPARKLE_STRIKE(80,1000,0,0,0,0,PType.MAGIC,"This attack never misses",true,15),
	SPARKLING_ARIA(90,100,100,0,1,0,PType.WATER,"% to cure user of its Burn if it has one",false,10),
	SPARKLING_TERRAIN(0,1000,0,0,2,0,PType.MAGIC,"Sets the terrain to SPARKLY for 5 turns",false,10),
	SPARKLING_WATER(0,1000,0,0,2,0,PType.WATER,"Raises Sp.Def by 2. Turns into Sparkling Aria when used by Kissyfishy-D",false,15),
	SPARKLY_SWIRL(70,100,10,0,1,0,PType.MAGIC,"% chance to lower all of foe's stats by 1",false,15),
	SPECTRAL_THIEF(90,100,100,0,0,0,PType.GHOST,"% to steal any stat boosts foe has",true,10),
	SPELLBIND(0,100,0,0,2,0,PType.MAGIC,"Traps non-MAGIC foe for 4-5 turns, lowers foe's Def and SpD every turn",false,10), // TODO
	SPIKE_CANNON(25,100,0,0,0,0,PType.NORMAL,"Hits 2-5 times",false,15),
	SPIKES(0,1000,0,0,2,0,PType.GROUND,"Lays spikes on the opponents side. Depending on the layers laid (1-3) will damage any grounded foe (1/8, 1/6, 1/4) upon switch-in",false,20),
	SPIKY_SHIELD(0,1000,0,0,2,4,PType.GRASS,"User protects itself, can't be used in succession. Damages foe if they make contact",false,10),
	SPIRIT_BREAK(75,100,100,0,0,0,PType.LIGHT,"% chance to lower foe's Sp.Atk by 1",true,15),
	SPLASH(0,1000,0,0,2,0,PType.NORMAL,"This attack does nothing",false,40),
	SPOTLIGHT_RAY(80,100,100,0,1,0,PType.LIGHT,"% to encore foe for 2 turns",false,5),
	STAFF_JAB(75,90,100,0,0,0,PType.BUG,"% to lower foe's Attack by 2",true,15),
	STAR_STORM(110,85,0,0,1,0,PType.GALACTIC,"A normal attack",false,5),
	STAR$STRUCK_ARROW(75,85,0,3,0,0,PType.GALACTIC,"This move always crits",false,10),
	STEALTH_ROCK(0,1000,0,0,2,0,PType.ROCK,"Lays floating rocks on opponents side. Will damage any foe switching in (depending on their type weakness/resistance to ROCK) for a corresponding amount (1/2-1/32)",false,20),
	STEEL_BEAM(140,95,100,0,1,0,PType.STEEL,"% chance to deal 1/2 of user's HP in recoil",false,5),
	STEEL_WING(70,90,10,0,0,0,PType.STEEL,"% chance to raise user's Defense by 1",true,25),
	STICKY_WEB(0,1000,0,0,2,0,PType.BUG,"Lays webs on opponents side. This will cause a grounded foe switching in to have their Speed lowered by 1 stage",false,10),
	STOCKPILE(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Defense and Sp.Def by 1",false,20),
	STOMP(65,100,30,0,0,0,PType.NORMAL,"% of causing foe to flinch",true,20),
	STONE_EDGE(100,80,0,1,0,0,PType.ROCK,"Boosted Crit rate",false,5),
	STORED_POWER(-1,100,0,0,1,0,PType.PSYCHIC,"+20 BP for each stat boost",false,10),
	STORM_THROW(60,100,0,3,0,0,PType.FIGHTING,"This move always Crits",true,10),
	STRENGTH(80,100,0,0,0,0,PType.NORMAL,"A normal attack",true,10),
	STRENGTH_SAP(0,100,0,0,2,0,PType.GRASS,"Heals user an HP amount equal to foe's Attack stat; lowers foe's Attack by 1",false,10),
	STRING_SHOT(0,100,0,0,2,0,PType.BUG,"Lowers foe's Speed by 2",false,25),
	STRUGGLE(40,1000,0,0,0,0,PType.UNKNOWN,"Deals 25% of user's max HP as recoil",true,1),
	STRUGGLE_BUG(50,100,100,0,1,0,PType.BUG,"% chance to lower foe's Sp.Atk by 1",false,20),
	STUN_SPORE(0,75,0,0,2,0,PType.GRASS,"Paralyzes foe",false,25),
	SUBMISSION(80,90,0,0,0,0,PType.FIGHTING,"User takes 1/4 of damage inflicted as recoil",true,20),
	SUCKER_PUNCH(70,100,0,0,0,2,PType.DARK,"Always attacks first. Fails if foe didn't use an attacking move",true,5),
	SUMMIT_STRIKE(70,95,100,0,0,0,PType.FIGHTING,"% to lower foe's Defense by one stage. 30% to flinch foe",true,15),
	SUNNY_BURST(80,100,100,0,1,0,PType.LIGHT,"% to turn weather to SUNNY",false,5),
	SUNNY_DAY(0,1000,0,0,2,0,PType.FIRE,"Changes the weather to SUNNY for 5 turns",false,5),
	SUNNY_DOOM(80,100,0,0,1,0,PType.LIGHT,"If this attack faints foe, causes weather to turn SUNNY",false,5),
	SUNSTEEL_STRIKE(100,100,0,0,0,0,PType.STEEL,"A normal attack",true,5),
	SUPER_FANG(0,90,0,0,0,0,PType.NORMAL,"Halves foe's remaining HP",true,10),
	SUPERCHARGED_SPLASH(10,100,75,0,1,0,PType.WATER,"% chance to raise user's Sp.Atk by 2. Turns into Thunder when used by Kissyfishy-D",false,15),
	SUPERNOVA_EXPLOSION(200,100,0,0,1,0,PType.GALACTIC,"User faints",false,5),
	SUPERPOWER(120,100,100,0,0,0,PType.FIGHTING,"% of lowering user's Attack and Defense by 1",true,5),
	SUPERSONIC(0,55,0,0,2,0,PType.NORMAL,"Confuses foe",false,20),
	SURF(90,100,0,0,1,0,PType.WATER,"A normal attack",false,10),
	SWAGGER(0,100,0,0,2,0,PType.NORMAL,"Confuses foe, but raises foe's Attack by 2",false,15),
	SWEET_KISS(0,75,0,0,2,0,PType.LIGHT,"Confuses foe",false,10),
	SWEET_SCENT(0,1000,0,0,2,0,PType.NORMAL,"Lowers foe's Evasion by 2",false,20),
	SWIFT(60,1000,0,0,1,0,PType.MAGIC,"This attack never misses",false,20),
	SWITCHEROO(0,100,0,0,2,0,PType.DARK,"Swaps the user's and foe's held items",false,10),
	SWORD_OF_DAWN(150,90,0,0,0,0,PType.LIGHT,"User must rest unless this attack KOs foe",false,5),
	SWORD_SPIN(50,95,100,0,0,0,PType.STEEL,"% to raise user's Attack by 1",false,10),
	SWORDS_DANCE(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack by 2",false,5),
	SYNTHESIS(0,1000,0,0,2,0,PType.GRASS,"Restores 1/2 of user's max HP, 2/3 in SUN, 1/4 in any other weather",false,5),
	TACKLE(50,100,0,0,0,0,PType.NORMAL,"A normal attack",true,25),
	TAIL_GLOW(0,1000,0,0,2,0,PType.BUG,"Raises user's Sp.Atk by 3",false,5),
	TAIL_WHIP(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 1",false,35),
	TAILWIND(0,1000,0,0,2,0,PType.FLYING,"Doubles Speed for user's team for 5 turns",false,10),
	TAKE_DOWN(90,85,0,0,0,0,PType.NORMAL,"User takes 1/4 of damage dealt as recoil",true,20),
	TAKE_OVER(0,100,0,0,2,0,PType.GHOST,"Foe's next attack is used on itself. Can be used once every other turn, and not on the first turn out.",false,5),
	TAUNT(0,100,0,0,2,0,PType.DARK,"Causes foe to only be able to use attacking moves for 4 turns",false,20),
	TEETER_DANCE(0,100,0,0,2,0,PType.NORMAL,"Confuses foe",false,20),
	TELEPORT(0,1000,0,0,2,-6,PType.PSYCHIC,"User switches out, bringing in a teammate for free. This always moves last",false,10),
	TERRAIN_PULSE(-1,100,0,0,1,0,PType.NORMAL,"Type changes to match the current terrain, double BP if type of the move changes",false,10),
	THIEF(60,100,100,0,0,0,PType.DARK,"% chance to steal opponents item",true,25),
	THRASH(120,100,0,0,0,0,PType.NORMAL,"User is locked into this move for 2-3 turns, Confuses user when the effect is done",true,10),
	THROAT_CHOP(80,100,100,0,0,0,PType.DARK,"% to cause foe to not be able to use any sound-based moves",true,10),
	THUNDER(110,70,30,0,1,0,PType.ELECTRIC,"% of Paralyzing foe, doesn't check accuracy in RAIN",false,5),
	THUNDER_FANG(65,95,10,0,0,0,PType.ELECTRIC,"% of Paralyzing and/or flinching foe",true,15),
	THUNDER_PUNCH(75,100,20,0,0,0,PType.ELECTRIC,"% of Paralyzing foe",true,15),
	THUNDER_WAVE(0,90,0,0,2,0,PType.ELECTRIC,"Paralyzes foe, ELECTRIC Pokemon can't miss this move",false,15),
	THUNDERBOLT(90,100,10,0,1,0,PType.ELECTRIC,"% of Paralyzing foe",false,10),
	THUNDERSHOCK(40,100,10,0,1,0,PType.ELECTRIC,"% of Paralyzing foe",false,30),
	TICKLE(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Attack and Defense by 1",false,20),
	TOPSY$TURVY(0,1000,0,0,2,0,PType.DARK,"Inverses every stat change the foe has",false,20),
	TORMENT(0,100,0,0,2,0,PType.DARK,"Causes foe to not be able to use the same move in succession for 4 turns",false,15),
	TORNADO_SPIN(60,95,100,0,0,0,PType.FIGHTING,"% to raise user's Speed and Accuracy by 1, and frees user of being Spun",true,15),
	TOXIC(0,90,0,0,2,0,PType.POISON,"Badly poisons foe, POISON Pokemon can't miss this move",false,5),
	TOXIC_SPIKES(0,1000,0,0,2,0,PType.POISON,"Lays poisonous spikes on opponents side. 1 layer will cause any non-grounded foe switching in to be Poisoned, 2 = Toxic. POISON-types swtiching in removes them",false,10),
	TRI$ATTACK(80,100,20,0,1,0,PType.NORMAL,"% chance to either Burn, Paralyze or Frostbite foe",false,10),
	TRICK(0,100,0,0,2,0,PType.MAGIC,"Swaps the user's and foe's held items",false,10),
	TRICK_ROOM(0,1000,0,0,2,-7,PType.PSYCHIC,"Speed order is reversed for 6 turns",false,5),
	TRICK_TACKLE(90,90,100,0,0,0,PType.MAGIC,"% chance to swap user and foe's item",true,15),
	TWINEEDLE(25,100,50,0,0,0,PType.BUG,"% chance to Poison foe",false,20),
	TWINKLE_TACKLE(85,90,20,0,0,0,PType.MAGIC,"% chance to lower foe's Attack and Sp.Atk by 1",true,10),
	TWISTER(40,100,10,0,1,0,PType.DRAGON,"% of causing foe to flinch",false,20),
	U$TURN(70,100,0,0,0,0,PType.BUG,"Causes user to switch out after use",true,15),
	UNSEEN_STRANGLE(60,100,100,0,0,3,PType.DARK,"% to cause foe to flinch and will always go first, only works on user's first turn out",true,5),
	V$CREATE(180,95,100,0,0,0,PType.FIRE,"% chance to lower user's Defense, Sp.Def, and Speed by 1",true,5),
	VACUUM_WAVE(40,100,0,0,1,1,PType.FIGHTING,"Always goes first",false,30),
	VANDALIZE(0,100,0,0,2,0,PType.NORMAL,"Steals the foe's ability",false,10),
	VANISHING_ACT(100,100,30,0,1,0,PType.MAGIC,"% to confuse foe, a two turn attack. User vanishes on the first, and attacks on the second. Hits through protect.",false,10),
	VENOM_DRENCH(0,100,0,0,2,0,PType.POISON,"Lowers a Poisoned/Badly Poisoned foe's Attack and Sp.Atk by 2",false,20),
	VENOM_SPIT(40,100,100,0,0,1,PType.POISON,"% to Paralyze foe; always goes first",false,5),
	VENOSHOCK(-1,100,0,0,1,0,PType.POISON,"Damage is doubled if foe is Poisoned or Badly Poisoned",false,10),
	VENOSTEEL_CROSSCUT(100,90,20,1,0,0,PType.STEEL,"% to Paralyze or Badly Poison foe, boosted crit rate",true,10),
	VINE_CROSS(70,95,100,0,0,0,PType.GRASS,"% chance to lower foe's Speed by 1",false,15),
	VINE_WHIP(45,100,0,0,0,0,PType.GRASS,"A normal attack",true,25),
	VISE_GRIP(55,100,0,0,0,0,PType.NORMAL,"A normal attack",true,30),
	VITAL_THROW(60,1000,0,0,0,-1,PType.FIGHTING,"This attack never misses, but goes last",false,10),
	VITRIOLIC_HEX(60,100,100,0,1,0,PType.MAGIC,"% chance to decrease foe's crit stage by 1",false,20),
	VOLT_SWITCH(70,100,0,0,1,0,PType.ELECTRIC,"Causes user to switch out after use",false,10),
	VOLT_TACKLE(120,100,10,0,0,0,PType.ELECTRIC,"% to Paralyze foe. User takes 1/3 of damage dealt as recoil",true,5),
	WAKE$UP_SLAP(-1,100,0,0,0,0,PType.FIGHTING,"If foe is asleep, power is doubled, but the foe wakes up",true,10),
	WATER_CLAP(20,100,100,0,0,0,PType.WATER,"% to Paralyze foe. Turns into Dragon Darts when used by Kissyfishy-D",true,15),
	WATER_FLICK(0,100,0,0,2,0,PType.WATER,"Lowers foe's Attack by 2. Turns into Flamethrower when used by Kissyfishy-D",false,20),
	WATER_GUN(40,100,0,0,1,0,PType.WATER,"A normal attack",false,25),
	WATER_KICK(130,90,0,0,0,0,PType.WATER,"A normal attack. Turns into Hi Jump Kick when used by Kissyfishy-D",true,15),
	WATER_PULSE(60,100,30,0,1,0,PType.WATER,"% to Confuse foe",false,20),
	WATER_SMACK(40,95,50,0,0,0,PType.WATER,"% chance of causing foe to flinch. Turns into Darkest Lariat when used by Kissyfishy-D",true,10),
	WATER_SPORT(0,1000,0,0,2,0,PType.WATER,"Makes FIRE moves deal 1/3 damage for 8 turns",false,15),
	WATER_SPOUT(-1,100,0,0,1,0,PType.WATER,"Power is higher the more HP the user has",false,5),
	WATERFALL(80,100,10,0,0,0,PType.WATER,"% of causing foe to flinch",true,10),
	WAVE_CRASH(120,100,0,0,0,0,PType.WATER,"User takes 1/3 of damage dealt as recoil",true,10),
	WEATHER_BALL(-1,100,0,0,1,0,PType.NORMAL,"Type changes to match the current weather, double BP if type of the move changes",false,10),
	WHIP_SMASH(100,100,100,0,0,0,PType.NORMAL,"% to lower user's Defense by 1",true,5),
	WHIRLPOOL(35,95,100,0,1,0,PType.WATER,"% to spin non-WATER foes for 4-5 turns. While foe is spun, it takes 1/8 HP in damage, and cannot switch",false,15),
	WHIRLWIND(0,1000,0,0,2,-6,PType.FLYING,"Forces foe to switch out to a random teammate, always goes last",false,20),
	WILL$O$WISP(0,85,0,0,2,0,PType.FIRE,"Burns foe, FIRE Pokemon can't miss this move",false,15),
	WING_ATTACK(60,100,0,0,0,0,PType.FLYING,"A normal attack",true,35),
	WISH(0,1000,0,0,2,0,PType.NORMAL,"Heals a Pokemon by 50% of the user's max HP the next turn",false,10),
	WITHDRAW(0,1000,0,0,2,0,PType.WATER,"Raises user's Defense by 1",false,35),
	WOOD_HAMMER(120,100,0,0,0,0,PType.GRASS,"User takes 1/3 of damage inflicted",true,15),
	WORRY_SEED(0,100,0,0,2,0,PType.GRASS,"Changes foe's ability to INSOMNIA",false,10),
	WRAP(15,90,100,0,0,0,PType.NORMAL,"% to spin foe for 4-5 turns. While foe is spun, it takes 1/8 HP in damage, and cannot switch",true,20),
	X$SCISSOR(80,100,0,1,0,0,PType.BUG,"Boosted Crit rate",true,15),
	YAWN(0,1000,0,0,2,0,PType.NORMAL,"Causes foe to fall asleep next turn",false,10),
	ZAP_CANNON(120,50,100,0,1,0,PType.ELECTRIC,"% chance to Paralyze foe",false,5),
	ZEN_HEADBUTT(80,90,30,0,0,0,PType.PSYCHIC,"% of causing foe to flinch",true,15),
	ZING_ZAP(80,100,30,0,0,0,PType.ELECTRIC,"% of causing foe to flinch",true,15),
	
	HP_ROCK(60,100,0,0,1,0,PType.ROCK,"Calc only move",false,15),
	HP_FIRE(60,100,0,0,1,0,PType.FIRE,"Calc only move",false,15),
	HP_WATER(60,100,0,0,1,0,PType.WATER,"Calc only move",false,15),
	HP_GRASS(60,100,0,0,1,0,PType.GRASS,"Calc only move",false,15),
	HP_ICE(60,100,0,0,1,0,PType.ICE,"Calc only move",false,15),
	HP_ELECTRIC(60,100,0,0,1,0,PType.ELECTRIC,"Calc only move",false,15),
	HP_FIGHTING(60,100,0,0,1,0,PType.FIGHTING,"Calc only move",false,15),
	HP_POISON(60,100,0,0,1,0,PType.POISON,"Calc only move",false,15),
	HP_GROUND(60,100,0,0,1,0,PType.GROUND,"Calc only move",false,15),
	HP_FLYING(60,100,0,0,1,0,PType.FLYING,"Calc only move",false,15),
	HP_PSYCHIC(60,100,0,0,1,0,PType.PSYCHIC,"Calc only move",false,15),
	HP_BUG(60,100,0,0,1,0,PType.BUG,"Calc only move",false,15),
	HP_GHOST(60,100,0,0,1,0,PType.GHOST,"Calc only move",false,15),
	HP_DRAGON(60,100,0,0,1,0,PType.DRAGON,"Calc only move",false,15),
	HP_STEEL(60,100,0,0,1,0,PType.STEEL,"Calc only move",false,15),
	HP_DARK(60,100,0,0,1,0,PType.DARK,"Calc only move",false,15),
	HP_LIGHT(60,100,0,0,1,0,PType.LIGHT,"Calc only move",false,15),
	HP_MAGIC(60,100,0,0,1,0,PType.MAGIC,"Calc only move",false,15),
	HP_GALACTIC(60,100,0,0,1,0,PType.GALACTIC,"Calc only move",false,15),
	
	RETURN_ROCK(70,100,0,0,0,0,PType.ROCK,"Calc only move",true,15),
	RETURN_FIRE(70,100,0,0,0,0,PType.FIRE,"Calc only move",true,15),
	RETURN_WATER(70,100,0,0,0,0,PType.WATER,"Calc only move",true,15),
	RETURN_GRASS(70,100,0,0,0,0,PType.GRASS,"Calc only move",true,15),
	RETURN_ICE(70,100,0,0,0,0,PType.ICE,"Calc only move",true,15),
	RETURN_ELECTRIC(70,100,0,0,0,0,PType.ELECTRIC,"Calc only move",true,15),
	RETURN_FIGHTING(70,100,0,0,0,0,PType.FIGHTING,"Calc only move",true,15),
	RETURN_POISON(70,100,0,0,0,0,PType.POISON,"Calc only move",true,15),
	RETURN_GROUND(70,100,0,0,0,0,PType.GROUND,"Calc only move",true,15),
	RETURN_FLYING(70,100,0,0,0,0,PType.FLYING,"Calc only move",true,15),
	RETURN_PSYCHIC(70,100,0,0,0,0,PType.PSYCHIC,"Calc only move",true,15),
	RETURN_BUG(70,100,0,0,0,0,PType.BUG,"Calc only move",true,15),
	RETURN_GHOST(70,100,0,0,0,0,PType.GHOST,"Calc only move",true,15),
	RETURN_DRAGON(70,100,0,0,0,0,PType.DRAGON,"Calc only move",true,15),
	RETURN_STEEL(70,100,0,0,0,0,PType.STEEL,"Calc only move",true,15),
	RETURN_DARK(70,100,0,0,0,0,PType.DARK,"Calc only move",true,15),
	RETURN_LIGHT(70,100,0,0,0,0,PType.LIGHT,"Calc only move",true,15),
	RETURN_MAGIC(70,100,0,0,0,0,PType.MAGIC,"Calc only move",true,15),
	RETURN_GALACTIC(70,100,0,0,0,0,PType.GALACTIC,"Calc only move",true,15),
	;
	
	public static Move moveOfType(PType type) {
		for (Move m : values()) {
			if (m.mtype == type) return m;
		}
		return Move.STRUGGLE;
	}
	
	public int id;
	public int accuracy;
	public int basePower;
	public int cat;
	public boolean contact;
	public int critChance;
	private String desc;
	public PType mtype;
	public int pp;
	public int priority;
	public int secondary;
	
	private static int globalID = 0;
	public static final int DUMMY_MOVES_AMOUNT = 38;
	
	public static Move[] getAllMoves() {
		Move[] allMoves = values();
		
		int validMoveCount = allMoves.length - DUMMY_MOVES_AMOUNT;
		Move[] validMoves = new Move[validMoveCount];
		
		System.arraycopy(allMoves, 0, validMoves, 0, validMoveCount);
		
		return validMoves;
	}
	
	Move(int bp, int acc, int sec, int crit, int cat, int p, PType type, String desc, boolean contact, int pp) {
		this.basePower = bp;
		this.accuracy = acc;
		this.secondary = sec;
		this.cat = cat;
		this.critChance = crit;
		this.mtype = type;
		this.priority = p;
		this.desc = desc;
		this.contact = contact;
		this.pp = pp;
		
		setID();
	}
	
	private void setID() {
		this.id = globalID++;
	}

	public String formatbp(Pokemon user, Pokemon foe) {
		double bp = getbp(user, foe);
		if (bp == 0 || this == Move.MAGNITUDE) return "--";
		return String.format("%.0f", bp);
	}
	
	public String getAccuracy() {
		String result = "";
		if (accuracy > 100) {
			result += "--";
		} else {
			result += accuracy;
		}
		
		return result;
	}
	
	public double getbp(Pokemon user, Pokemon foe) {
		PType type = mtype;
		if (user != null) {
			if (this == Move.HIDDEN_POWER) type = user.determineHPType();
			if (this == Move.RETURN) type = user.determineHPType();
			if (this == Move.WEATHER_BALL) type = user.determineWBType();
			if (this == Move.TERRAIN_PULSE) type = user.determineTPType();
		}
		if (basePower == -1) {
			if (this == Move.STORED_POWER && foe == null) return 20;
			boolean faster = user == null || foe == null ? true : user.getFaster(foe, 0, 0) == user;
			if (user == null || user.headbuttCrit < 0) {
				if (this == Move.ELECTRO_BALL || this == Move.FLAIL || this == Move.REVERSAL || this == Move.GRASS_KNOT || this == Move.LOW_KICK
						|| this == Move.GYRO_BALL || this == Move.HEAT_CRASH || this == Move.HEAVY_SLAM || this == Move.RETURN || this == Move.FRUSTRATION) return 0;
				user = new Pokemon(1, 1, false, false);
			}
			if (foe == null) foe = new Pokemon(1, 1, false, false);
			double bp = user.determineBasePower(foe, this, faster, null, false);
			if (type == PType.NORMAL) {
				if (user.ability == Ability.GALVANIZE || user.ability == Ability.REFRIGERATE || user.ability == Ability.PIXILATE) bp *= 1.2;
			} else {
				if (user.ability == Ability.NORMALIZE) bp *= 1.2;
			}
			return bp;
		}
		if (user != null) {
			double bp = basePower;
			if (type == PType.NORMAL) {
				if (user.ability == Ability.GALVANIZE || user.ability == Ability.REFRIGERATE || user.ability == Ability.PIXILATE) bp *= 1.2;
			} else {
				if (user.ability == Ability.NORMALIZE) bp *= 1.2;
			}
			if (user.item == Item.METRONOME && this == user.lastMoveUsed) bp *= (1 + ((user.metronome) * 0.2));
			return bp;
		}
		return basePower;
	}

	public String getCategory() {
		if (cat == 0) return "Physical";
		if (cat == 1) return "Special";
		else {
			return "Status";
		}
	}

	public BufferedImage getCategoryIcon() {
		BufferedImage image = null;
		
		String imageName = getCategory().toLowerCase();
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/icons/" + imageName + ".png"));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/minsprites/000.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
	
	public String getDescription() {
		if (this.secondary > 0) {
            return secondary + desc;
        } else {
        	return desc;
        }
		
	}
	
	public JPanel getMoveSummary() {
		return getMoveSummary(null, null);
	}
	
	public JPanel getMoveSummary(Pokemon user, Pokemon foe) {
	    JPanel result = new JPanel();
	    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

	    // Move Name
	    JLabel nameLabel = new JLabel(toString());
	    nameLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
	    PType type = mtype;
	    if (this == Move.HIDDEN_POWER) type = user.determineHPType();
		if (this == Move.RETURN) type = user.determineHPType();
		if (this == Move.WEATHER_BALL) type = user.determineWBType();
		if (this == Move.TERRAIN_PULSE) type = user.determineTPType();
		if (this.isAttack()) {
			if (type == PType.NORMAL) {
				if (user.ability == Ability.GALVANIZE) type = PType.ELECTRIC;
				if (user.ability == Ability.REFRIGERATE) type = PType.ICE;
				if (user.ability == Ability.PIXILATE) type = PType.LIGHT;
			} else {
				if (user.ability == Ability.NORMALIZE) type = PType.NORMAL;
			}
		}
        Color color = type.getColor();
	    JGradientButton typeButton = new JGradientButton(type.toString());
	    typeButton.setBackground(color);

	    // Category
	    JLabel categoryLabel = new JLabel("Cat");
	    JLabel categoryIconLabel = new JLabel(getScaledIcon(1.5));

	    // Power
	    JLabel powerLabel = new JLabel("Power");
	    JLabel powerValueLabel = new JLabel(formatbp(user, foe));

	    // Accuracy
	    JLabel accuracyLabel = new JLabel("Acc");
	    JLabel accuracyValueLabel = new JLabel(getAccuracy());

	    // PP
	    JLabel ppLabel = new JLabel("PP");
	    JLabel ppValueLabel = new JLabel(String.valueOf(pp));

	    // Description
	    JLabel descriptionLabel = new JLabel();
	    String text = Item.breakString(getDescription(), 65);
	    text = text.replace("\n", "<br>");
	    descriptionLabel.setText("<html>" + text + "</html>");
	    descriptionLabel.setOpaque(false);
	    descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
	    descriptionLabel.setHorizontalTextPosition(SwingConstants.LEFT);

	    // Create a panel for the move summary
	    JPanel moveSummaryPanel = new JPanel();
	    moveSummaryPanel.setLayout(new GridLayout(6, 2));
	    moveSummaryPanel.add(nameLabel);
	    moveSummaryPanel.add(typeButton);
	    moveSummaryPanel.add(categoryLabel);
	    moveSummaryPanel.add(categoryIconLabel);
	    moveSummaryPanel.add(powerLabel);
	    moveSummaryPanel.add(powerValueLabel);
	    moveSummaryPanel.add(accuracyLabel);
	    moveSummaryPanel.add(accuracyValueLabel);
	    moveSummaryPanel.add(ppLabel);
	    moveSummaryPanel.add(ppValueLabel);

	    // Add the move summary panel to the result panel
	    result.add(moveSummaryPanel);

	    // Add the description area to the result panel
	    JPanel descriptionArea = new JPanel();
	    descriptionArea.add(descriptionLabel);
	    descriptionArea.setBackground(new Color(225, 225, 225));
	    result.add(descriptionArea);

	    return result;
	}
	public int getNumHits(Pokemon user, Pokemon[] team) {
		if (this == Move.DOUBLE_SLAP || this == Move.FURY_ATTACK ||this == Move.FURY_SWIPES || this == Move.ICICLE_SPEAR ||
				this == Move.PIN_MISSILE || this == Move.ROCK_BLAST|| this == Move.SCALE_SHOT || this == Move.SPIKE_CANNON ||
				this == Move.SHOOTING_STARS || this == Move.BULLET_SEED || this == Move.FLASH_DARTS || this == Move.MAGIC_MISSILES) {
			int randomNum = (int) (Math.random() * 100) + 1; // Generate a random number between 1 and 100 (inclusive)
			if (user.item == Item.LOADED_DICE) {
				if (randomNum <= 50) {
					return 4;
				} else {
					return 5;
				}
			} else {
				if (randomNum <= 35) {
		            return 2; // 2 hits with 35% probability
		        } else if (randomNum <= 70) {
		            return 3; // 3 hits with 35% probability
		        } else if (randomNum <= 85) {
		            return 4; // 4 hits with 15% probability
		        } else {
		            return 5; // 5 hits with 15% probability
		        }
			}
	        
		} else if (this == Move.DOUBLE_KICK || this == Move.DRAGON_DARTS || this == Move.DUAL_CHOP || this == Move.DOUBLE_HIT || this == Move.TWINEEDLE || this == Move.POP_POP) {
			return 2;
		} else if (this == Move.BEAT_UP) {
			int result = 0;
			if (team == null) {
				result++;
				team = new Pokemon[1];
			}
			for (Pokemon p : team) {
				if (p != null && !p.isFainted() && p.status == Status.HEALTHY) {
					result++;
				}
			}
			return result;
		} else {
			return 1;
		}
	}
	public ImageIcon getScaledIcon(double scale) {
		ImageIcon originalSprite = new ImageIcon(getCategoryIcon());
		Image originalImage = originalSprite.getImage();
		
		int scaledWidth = (int) (originalImage.getWidth(null) * scale);
		int scaledHeight = (int) (originalImage.getHeight(null) * scale);
		
		Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		
		return scaledIcon;
	}
	
	public boolean isAttack() {
		return cat != 2;
	}
	
	public boolean isHMmove() {
		if (this == Move.CUT || this == Move.ROCK_SMASH || this == Move.VINE_CROSS || this == Move.SURF || this == Move.SLOW_FALL || this == Move.FLY
			|| this == Move.ROCK_CLIMB || this == Move.LAVA_SURF) {
			return true;
		}
		return false;
	}
	
	public boolean isPhysical() {
		return cat == 0;
	}
	
	public boolean isSlicing() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(AERIAL_ACE);
		result.add(AIR_CUTTER);
		result.add(AIR_SLASH);
		result.add(FURY_CUTTER);
		result.add(LEAF_BLADE);
		result.add(NIGHT_SLASH);
		result.add(PSYCHO_CUT);
		result.add(RAZOR_SHELL);
		result.add(SACRED_SWORD);
		result.add(GLITTERING_SWORD);
		result.add(SWORD_SPIN);
		result.add(SLASH);
		result.add(SOLAR_BLADE);
		result.add(X$SCISSOR);
		result.add(CROSS_POISON);
		result.add(CUT);
		result.add(RAZOR_LEAF);
		result.add(VENOSTEEL_CROSSCUT);
		
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public boolean isPunching() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(BULLET_PUNCH);
		result.add(COMET_PUNCH);
		result.add(DRAIN_PUNCH);
		result.add(FIRE_PUNCH);
		result.add(ICE_PUNCH);
		result.add(MACH_PUNCH);
		result.add(POWER$UP_PUNCH);
		result.add(SHADOW_PUNCH);
		result.add(SKY_UPPERCUT);
		result.add(THUNDER_PUNCH);
		result.add(METEOR_MASH);
		result.add(PLASMA_FISTS);
		result.add(SUCKER_PUNCH);
		result.add(DYNAMIC_PUNCH);
		result.add(MANA_PUNCH);
		
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public boolean isTail() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(AQUA_TAIL);
		result.add(DRAGON_TAIL);
		result.add(IRON_TAIL);
		result.add(POISON_TAIL);
		result.add(DRAGON_RUSH);
		result.add(SLAM);
		result.add(WRAP);
		result.add(VENOSTEEL_CROSSCUT);
		
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public static ArrayList<Move> getNoComboMoves() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(DETECT);
		result.add(PROTECT);
		result.add(LAVA_LAIR);
		result.add(OBSTRUCT);
		result.add(SPIKY_SHIELD);
		result.add(ENDURE);
		result.add(ABDUCT);
		result.add(TAKE_OVER);
		result.add(MAGIC_REFLECT);
		result.add(DESTINY_BOND);
		result.add(AQUA_VEIL);
		
		return result;
	}
	
	public static ArrayList<Move> getSound() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(Move.BUG_BUZZ);
		result.add(Move.GRASS_WHISTLE);
		result.add(Move.GROWL);
		result.add(Move.HOWL);
		result.add(Move.METAL_SOUND);
		result.add(Move.NOBLE_ROAR);
		result.add(Move.PERISH_SONG);
		result.add(Move.ROAR);
		result.add(Move.ROUND);
		result.add(Move.SCREECH);
		result.add(Move.SNORE);
		result.add(Move.SNARL);
		result.add(Move.SPARKLING_ARIA);
		result.add(Move.SPARKLING_WATER);
		result.add(Move.SUPERSONIC);
		result.add(Move.HYPER_VOICE);
		result.add(Move.PARTING_SHOT);
		result.add(Move.SING);
		result.add(Move.BELCH);
		result.add(Move.PSYCHIC_NOISE);
		return result;
	}
	
	public boolean isTM() {
		return Item.getTMMoves().contains(this);
	}
	@Override // implementation
	public String toString() {
	    String name = super.toString();
	    name = name.replace('$', '-'); // Replace '$' with '-'
	    name = name.toLowerCase().replace('_', ' '); // Convert underscores to spaces
	    String[] words = name.split(" ");
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < words.length; i++) {
	        String word = words[i];
	        if (word.contains("-")) {
	            String[] hyphenWords = word.split("-");
	            for (int j = 0; j < hyphenWords.length; j++) {
	                sb.append(Character.toUpperCase(hyphenWords[j].charAt(0)))
	                  .append(hyphenWords[j].substring(1));
	                if (j < hyphenWords.length - 1) {
	                    sb.append('-');
	                }
	            }
	        } else {
	            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
	        }
	        if (i < words.length - 1) { // Check if there's a next word
	            char nextChar = name.charAt(name.indexOf(word) + word.length());
	            if (nextChar == ' ' || nextChar == '-') {
	                sb.append(nextChar); // Keep the space or hyphen
	            } else {
	                sb.append(" "); // Add a space if the next character is not space or hyphen
	            }
	        }
	    }
	    return sb.toString().trim();
	}
	
	public static Move getEnum(String string) {
		// Normalize the string
	    String normalized = string.toUpperCase().replace('-', '$').replace(' ', '_');
	    
	    try {
	        return Move.valueOf(normalized);
	    } catch (IllegalArgumentException e) {
	        throw new IllegalStateException("No matching Move enum found for string: " + string, e);
	    }
	}

	public static ArrayList<Move> getRecoil() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(Move.BRAVE_BIRD);
		result.add(Move.FLARE_BLITZ);
		result.add(Move.HEAD_SMASH);
		result.add(Move.TAKE_DOWN);
		result.add(Move.VOLT_TACKLE);
		result.add(Move.ROCK_WRECKER);
		result.add(Move.GENESIS_SUPERNOVA);
		result.add(Move.LIGHT_OF_RUIN);
		result.add(Move.SUBMISSION);
		result.add(Move.WAVE_CRASH);
		result.add(Move.STEEL_BEAM);
		result.add(Move.DOUBLE$EDGE);
		result.add(Move.STRUGGLE);
		result.add(Move.WOOD_HAMMER);
		return result;
	}

	public boolean hasPriority(Pokemon p) {
		return this.priority >= 1 || (this.cat == 2 && p.ability == Ability.PRANKSTER) ||
			((this.mtype == PType.MAGIC || p.lastMoveUsed == Move.VANISHING_ACT) && p.ability == Ability.SLEIGHT_OF_HAND && p.currentHP == p.getStat(0)) ||
			(p.impressive && p.ability == Ability.AMBUSH);
	}
	
	public int getPriority(Pokemon p) {
		// TODO Auto-generated method stub
		return this.priority;
	}
	
	public String superToString() {
		return super.toString();
	}

	public boolean isBiting() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(BITE);
		result.add(CRUNCH);
		result.add(FIRE_FANG);
		result.add(HYPER_FANG);
		result.add(ICE_FANG);
		result.add(JAW_LOCK);
		result.add(POISON_FANG);
		result.add(PSYCHIC_FANGS);
		result.add(THUNDER_FANG);
		result.add(LEECH_LIFE);
		result.add(MAGIC_FANG);

		
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public boolean isBinding() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(JAW_LOCK);
		result.add(FIRE_SPIN);
		result.add(WHIRLPOOL);
		result.add(BIND);
		result.add(WRAP);
		result.add(INFESTATION);
		result.add(SPELLBIND);	
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public static ArrayList<Move> getDraining() {
		ArrayList<Move> result = new ArrayList<>();
		result.add(GIGA_DRAIN);
		result.add(LEECH_LIFE);
		result.add(HORN_LEECH);
		result.add(LIGHT_DRAIN);
		result.add(MEGA_DRAIN);
		result.add(PARABOLIC_CHARGE);
		result.add(DREAM_EATER);
		result.add(ABSORB);
		result.add(DRAIN_PUNCH);
		
		return result;
	}
	
	public boolean isHealing() {
		ArrayList<Move> result = getDraining();
		result.add(LIFE_DEW);
		result.add(MOONLIGHT);
		result.add(MORNING_SUN);
		result.add(RECOVER);
		result.add(REST);
		result.add(ROOST);
		result.add(SHORE_UP);
		result.add(SLACK_OFF);
		result.add(SYNTHESIS);
		result.add(STRENGTH_SAP);
		result.add(WISH);
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public boolean isCalcHiddenPowerReturn() {
        return this.superToString().contains("HP") || this.superToString().contains("RETURN_");
    }
	
	public static boolean treatAsStatus(Move m, Pokemon me, Pokemon foe) {
		// covert cloak or shield dust make secondary 0
		// sparkly terrain + grounded or serene grace make secondary 2x
		double effectiveness = Trainer.getEffective(foe, me, m.mtype, m, false);
		if (effectiveness == 0) return false;
		int sec = m.secondary;
		if (foe.item == Item.COVERT_CLOAK) sec = 0;
		if (foe.ability == Ability.SHIELD_DUST && me.ability != Ability.MOLD_BREAKER) sec = 0;
		if (Pokemon.field.equals(Pokemon.field.terrain, Effect.SPARKLY) && me.isGrounded()) sec *= 2;
		if (me.ability == Ability.SERENE_GRACE) sec *= 2;
		if (m == Move.MAGIC_FANG && effectiveness < 2) return false;
		
		// *** unimplemented moves primarily used for secondary effects: Circle Throw, Dragon Tail, Fatal Bind, Spectral Thief ***
		if (sec > 0) {
			if (m.secondary == 100) {
				if (m.isBinding() && !foe.hasStatus(Status.SPUN) && !foe.hasStatus(Status.TRAPPED)) {
					return true;
				}
				// Foe lowering moves (same as self boosting)
				if (m == Move.BREAKING_SWIPE || m == Move.ACID_SPRAY || m == Move.BULLDOZE || m == Move.ELECTROWEB || m == Move.GLACIATE || m == Move.ICY_WIND || m == Move.LOW_SWEEP ||
						m == Move.MUD_SHOT || m == Move.MUD$SLAP || m == Move.MYSTICAL_FIRE || m == Move.ROCK_SMASH || m == Move.ROCK_TOMB || m == Move.SNARL ||
						m == Move.SPIRIT_BREAK || m == Move.STAFF_JAB || m == Move.STRUGGLE_BUG || m == Move.SUMMIT_STRIKE || m == Move.VINE_CROSS) {
					return true;
				}
				if (foe.ability != Ability.STICKY_HOLD || me.ability == Ability.MOLD_BREAKER) {
					if (m == Move.BUG_BITE || m == Move.PLUCK || m == Move.INCINERATE) return true;
				}
				if (Pokemon.field.hasScreens(foe.getFieldEffects(), foe)) {
					if (m == Move.BRICK_BREAK || m == Move.PSYCHIC_FANGS) return true;
				}
				// Foe inflicting status moves (potentially add checks for if they're already statused)
				if (m == Move.MAGIC_CRASH || m == Move.MOLTEN_CONSUME || m == Move.NUZZLE || m == Move.MORTAL_SPIN || m == Move.VENOM_SPIT) {
					return true;
				}
				// Spin moves
				if (m == Move.RAPID_SPIN && !Pokemon.field.getHazards(me.getFieldEffects()).isEmpty()) {
					return true;
				}
				// Self boosting moves (potentially add AI later if they're already at +6 in the stat by sending them through the other stat boosting check block in Pokemon.bestMove())
				if (m == Move.FLAME_CHARGE || m == Move.POWER$UP_PUNCH || m == Move.SCALE_SHOT || m == Move.SWORD_SPIN || m == Move.TORNADO_SPIN) {
					return true;
				}
				// Ability changing moves
				if (m == Move.SLOW_FALL && me.ability != Ability.LEVITATE) {
					return true;
				}
				if (m == Move.SMACK_DOWN && !foe.isGrounded()) {
					return true;
				}
				if (m == Move.SPARKLING_ARIA && me.status == Status.BURNED) {
					return true;
				}
				if (m == Move.ROCKFALL_FRENZY && !Pokemon.field.contains(foe.getFieldEffects(), Effect.STEALTH_ROCKS)) {
					return true;
				}
				
				return false;
			}
			return new Random().nextInt(100) < sec;
		} else {
			return false;
		}
	}

}