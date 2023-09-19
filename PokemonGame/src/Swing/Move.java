package Swing;

import java.util.ArrayList;

public enum Move {
	ABDUCT(0,100,0,0,2,0,PType.GALACTIC,"Abducts the foe and forces their next move to be used on themselves. Can be used once every other turn",false,1),
	ABSORB(20,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt to foe",false,1),
	ACCELEROCK(40,100,0,0,0,1,PType.ROCK,"Always goes first",true,1),
	ACID(40,100,10,0,1,0,PType.POISON,"% chance to lower foe's Sp.Def by 1",false,1),
	ACID_ARMOR(0,1000,0,0,2,0,PType.POISON,"Raises user's Defense by 2",false,1),
	ACID_SPRAY(40,100,100,0,1,0,PType.POISON,"% chance to lower foe's Sp.Def by 2",false,1),
	ACROBATICS(90,90,30,0,0,0,PType.FLYING,"% chance of causing foe to flinch",true,1),
	AERIAL_ACE(60,1000,0,0,0,0,PType.FLYING,"This attack always hits",true,1),
	AEROBLAST(100,95,0,1,1,0,PType.FLYING,"Boosted Crit Rate",false,1),
	AGILITY(0,1000,0,0,2,0,PType.FLYING,"Raises user's Speed by 2",false,1),
	AIR_CUTTER(55,95,0,1,1,0,PType.FLYING,"Boosted Crit Rate",false,1),
	AIR_SLASH(75,95,30,0,1,0,PType.FLYING,"% chance of causing foe to flinch",false,1),
	AMNESIA(0,1000,0,0,2,0,PType.PSYCHIC,"Raises user's Sp.Def by 2",false,1),
	ANCIENT_POWER(60,100,10,0,1,0,PType.ROCK,"% chance to raise all of the user's stats by 1",false,1),
	AQUA_JET(40,100,0,0,0,1,PType.WATER,"Always goes first",true,1),
	AQUA_RING(0,1000,0,0,2,0,PType.WATER,"Restores a small amount of HP at the end of every turn",false,1),
	AQUA_TAIL(90,90,0,0,0,0,PType.WATER,"A normal attack",true,1),
	AROMATHERAPY(0,1000,0,0,2,0,PType.GRASS,"Cures team of any status conditions",false,1),
	//ASSURANCE(50,100,0,0,0,0,PType.DARK,"A normal attack",false,1),
	ASTONISH(30,100,30,0,0,0,PType.GHOST,"% chance of causing foe to flinch",true,1),
	AURORA_VEIL(0,1000,0,0,2,0,PType.ICE,"Can only be used in SNOW, reduces both physical and special damage recieved for 5 turns",false,1),
	AURA_SPHERE(90,1000,0,0,1,0,PType.FIGHTING,"This attack always hits",false,1),
	//AUTO_SHOT(0,1000,0,0,2,0,PType.STEEL,"Causes all of user's \"Shooting\" moves to hit twice",false,1),
	AURORA_BEAM(75,100,10,0,1,0,PType.ICE,"% chance to lower foe's Attack by 1",false,1),
	AUTOMOTIZE(0,1000,0,0,2,0,PType.STEEL,"Raises user's Speed by 2",false,1),
	BABY$DOLL_EYES(0,100,0,0,2,1,PType.LIGHT,"",false,1),
	//BAWL(0,100,0,0,2,0,PType.DARK,"Lowers foe's Attack by 2",false,1),
	BEAT_UP(10,100,0,0,0,0,PType.DARK,"Attacks once per healthy Pokemon on your team",false,1),
	BEEFY_BASH(100,85,50,0,0,-1,PType.FIGHTING,"% chance to paralyze foe, moves last",true,1),
	BELCH(120,100,0,0,1,0,PType.POISON,"Only works on the first turn out",false,1),
	//BIG_BULLET(70,90,30,0,0,0,PType.STEEL,"% chance to Paralyze foe",false,1),
	BIND(15,85,100,0,0,0,PType.NORMAL,"",true,1),
	BITE(60,100,30,0,0,0,PType.DARK,"% chance of causing foe to flinch",true,1),
	BITTER_MALICE(75,100,30,0,1,0,PType.GHOST,"% chance to Frostbite foe",false,1),
	//BLACK_HOLE(90,90,100,0,1,0,PType.DARK,"% chance of lowering foe's Accuracy by 1",false,1),
	BLACK_HOLE_ECLIPSE(140,100,100,0,1,0,PType.GALACTIC,"",false,1),
	BLAZE_KICK(90,100,10,1,0,0,PType.FIRE,"",true,1),
	BLAST_BURN(150,90,0,0,1,0,PType.FIRE,"User must rest after using this move",false,1), // recharge
	//BLAST_FLAME(100,100,100,0,1,0,PType.FIRE,"% chance to Burn foe",false,1),
	//BLAZING_SWORD(90,90,50,0,0,0,PType.FIRE,"% chance to Burn foe",false,1),
	//BLIND(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Accuracy by 2",false,1),
	BLIZZARD(110,70,20,0,1,0,PType.ICE,"",false,1),
	BLUE_FLARE(130,85,20,0,1,0,PType.FIRE,"% chance to Burn foe",false,1),
	BODY_PRESS(80,100,0,0,0,0,PType.FIGHTING,"",true,1),
	BODY_SLAM(85,100,30,0,0,0,PType.NORMAL,"% chance to Paralyze foe",true,1),
	BOLT_STRIKE(130,85,20,0,0,0,PType.ELECTRIC,"% chance to Paralyze foe",true,1),
	//BOULDER_CRUSH(85,80,50,0,0,0,PType.ROCK,"% chance of causing foe to flinch",false,1),
	//BOULDER_SLAM(70,100,0,0,0,0,PType.ROCK,"A normal attack",false,1),
	BOUNCE(85,85,30,0,0,0,PType.FLYING,"% chance to Paralyze foe",true,1),
	//BRANCH_WHACK(50,95,0,0,0,0,PType.ROCK,"A normal attack",false,1),
	BRANCH_POKE(40,100,0,0,0,0,PType.GRASS,"",true,1),
	BRAVE_BIRD(120,100,0,0,0,0,PType.FLYING,"User takes 1/3 of damage inflicted",true,1), // recoil
	BREAKING_SWIPE(60,100,100,0,0,0,PType.DRAGON,"",true,1),
	BRICK_BREAK(75,100,100,0,0,0,PType.FIGHTING,"Breaks Screen effects",true,1),
	BRINE(-1,100,0,0,1,0,PType.WATER,"Damage is doubled if foe is below 50% HP",false,1), 
	BRUTAL_SWING(60,100,0,0,0,0,PType.DARK,"A normal attack",true,1),
	BUBBLE(20,100,0,0,1,0,PType.WATER,"A normal attack",false,1),
	BUBBLEBEAM(65,100,10,0,1,0,PType.WATER,"% to lower foe's Speed by 1",false,1),
	BUG_BITE(60,100,0,0,0,0,PType.BUG,"A normal attack",true,1),
	BUG_BUZZ(90,100,10,0,1,0,PType.BUG,"% chance to lower foe's Sp.Def by 1",false,1),
	BULK_UP(0,1000,0,0,2,0,PType.FIGHTING,"Raises user's Attack and Defense by 1",false,1),
	BULLDOZE(60,100,100,0,0,0,PType.GROUND,"",false,1),
	BULLET_PUNCH(40,100,0,0,0,1,PType.STEEL,"Always goes first",true,1),
	BURN_UP(130,100,100,0,0,0,PType.FIRE,"",false,1),
	//BUZZ(0,100,0,0,2,0,PType.BUG,"Confuses foe",false,1),
	CALM_MIND(0,1000,0,0,2,0,PType.MAGIC,"Raises user's Sp.Atk and Sp.Def by 1",false,1),
	CHANNELING_BLOW(65,100,0,3,0,0,PType.FIGHTING,"",true,1),
	CHARGE(0,1000,0,0,2,0,PType.ELECTRIC,"User's next electric-type attack damage is doubled. Raises user's Sp.Def by 1",false,1),
	CHARGE_BEAM(50,90,50,0,1,0,PType.ELECTRIC,"",false,1),
	CHARM(0,100,0,0,2,0,PType.LIGHT,"Lowers foe's Attack by 2",false,1),
	CIRCLE_THROW(60,90,100,0,0,-6,PType.FIGHTING,"",true,1),
	//CHOMP(70,100,30,0,0,0,PType.DARK,"% chance to lower foe's Speed by 1",false,1),
	CLOSE_COMBAT(120,100,100,0,0,0,PType.FIGHTING,"Lowers user's Defense and Sp.Def by 1",true,1),
	COIL(0,1000,0,0,2,0,PType.POISON,"Raises user's Atk, Def, and Acc by 1",false,1),
	COMET_CRASH(-1,90,0,0,0,0,PType.GALACTIC,"Damage is doubled if user's HP is full",true,1),
	COMET_PUNCH(55,100,0,0,0,0,PType.GALACTIC,"A normal attack",true,1),
	CONFUSE_RAY(0,100,0,0,2,0,PType.GHOST,"Confuses foe",false,1),
	CONFUSION(50,100,10,0,1,0,PType.PSYCHIC,"% chance to Confuse foe",false,1),
	//CONSTRICT(10,100,50,0,0,0,PType.NORMAL,"% chance to lower foe's Speed by 1",false,1),
	CORE_ENFORCER(100,100,10,0,1,0,PType.GALACTIC,"",false,1),
	COSMIC_POWER(0,1000,0,0,2,0,PType.GALACTIC,"Raises user's Def and Sp.Def by 1",false,1),
	COTTON_GUARD(0,1000,0,0,2,0,PType.GRASS,"Raises user's Defense by 3",false,1),
	//COUNTER(-1,100,0,0,0,-5,PType.FIGHTING,"",false,1),
	CROSS_CHOP(100,80,0,1,0,0,PType.FIGHTING,"",true,1),
	CROSS_POISON(90,100,10,1,0,0,PType.POISON,"% chance to Poison foe, boosted Crit rate",true,1),
	CRUNCH(80,100,30,0,0,0,PType.DARK,"% chance to lower foe's Defense by 1",true,1),
	CURSE(0,100,0,0,2,0,PType.GHOST,"User loses half of its total HP. In exchance, foe takes 1/4 of its max HP at the end of every turn",false,1),
	DARK_PULSE(80,100,30,0,1,0,PType.DARK,"% chance of causing foe to flinch",false,1),
	DARKEST_LARIAT(85,100,0,0,0,0,PType.DARK,"",true,1),
	DAZZLING_GLEAM(80,100,0,0,1,0,PType.LIGHT,"",false,1),
	DEEP_SEA_BUBBLE(100,100,0,0,1,0,PType.WATER,"A normal attack. Turns into Draco Meteor when used by Kissyfishy-D",false,1),
	//DARK_VOID(0,80,0,0,2,0,PType.DARK,"Foe falls asleep",false,1),
	DEFENSE_CURL(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Defense by 1",false,1),
	DEFOG(0,1000,0,0,2,0,PType.FLYING,"",false,1),
	DESOLATE_VOID(65,85,50,0,1,0,PType.GALACTIC,"",false,1),
	DESTINY_BOND(0,1000,0,0,2,1,PType.GHOST,"Always goes first; can't be used twice in a row. If foe knocks out user the same turn, foe faints as well",false,1),
	//DISAPPEAR(0,1000,50,0,2,0,PType.GHOST,"% chance to Confuse foe; raises user's Evasion by 2",false,1),
	DETECT(0,1000,0,0,2,4,PType.FIGHTING,"",false,1),
	DIAMOND_STORM(100,95,50,0,0,0,PType.FIGHTING,"",false,1),
	DIG(80,100,0,0,0,0,PType.GROUND,"",true,1),
	DISCHARGE(80,100,30,0,1,0,PType.ELECTRIC,"% chance to Paralyze foe",false,1),
	DIVE(80,100,0,0,0,0,PType.WATER,"A normal attack",true,1),
	//DOUBLE_BLAST(-1,60,30,0,1,0,PType.NORMAL,"% chance to Confuse foe",false,1),
	//DOUBLE$EDGE(120,100,0,0,0,0,PType.NORMAL,"User takes 1/3 of damage inflicted",false,1),
	DOUBLE_HIT(35,90,0,0,0,0,PType.NORMAL,"Attacks twice",true,1),
	//DOUBLE_JET(-1,85,0,0,0,0,PType.WATER,"Attacks 2-5 times",false,1),
	DOUBLE_KICK(30,100,0,0,0,0,PType.FIGHTING,"Attacks twice",true,1),
	//DOUBLE_PUNCH(-1,85,0,0,0,0,PType.FIGHTING,"Attacks twice",false,1),
	DOUBLE_SLAP(15,85,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,1),
	//DOUBLE_SLICE(-1,80,15,0,0,0,PType.STEEL,"% to cause foe to Bleed; attacks twice",false,1),
	DOUBLE_TEAM(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Evasion by 1",false,1),
	DRACO_METEOR(130,90,100,0,1,0,PType.DRAGON,"% to lower user's Sp.Atk by 2",false,1),
	DRAGON_BREATH(60,100,30,0,1,0,PType.DRAGON,"% chance to Paralyze foe",false,1),
	DRAGON_CLAW(80,100,0,1,0,0,PType.DRAGON,"Boosted Crit rate",true,1),
	DRAGON_DANCE(0,1000,0,0,2,0,PType.DRAGON,"Raises user's Attack and Speed by 1",false,1),
	DRAGON_DARTS(50,100,0,0,0,0,PType.DRAGON,"",false,1),
	DRAGON_PULSE(85,100,0,0,1,0,PType.DRAGON,"A normal attack",false,1),
	DRAGON_RAGE(0,100,0,0,1,0,PType.DRAGON,"Always does 40 HP damage",false,1),
	DRAGON_RUSH(100,75,20,0,0,0,PType.DRAGON,"% chance of causing foe to flinch",true,1),
	DRAGON_TAIL(60,90,100,0,0,-6,PType.DRAGON,"",true,1),
	DRAIN_PUNCH(75,100,0,0,0,0,PType.FIGHTING,"",true,1),
	DRAINING_KISS(50,100,0,0,1,0,PType.LIGHT,"",true,1),
	DREAM_EATER(100,100,0,0,1,0,PType.PSYCHIC,"Only works if target is asleep. Heals 50% of damage dealt to foe",false,1),
	DRILL_PECK(80,100,0,1,0,0,PType.FLYING,"Boosted Crit rate",true,1),
	DRILL_RUN(80,95,0,1,0,0,PType.GROUND,"Boosted Crit rate",true,1),
	DUAL_CHOP(40,90,0,0,0,0,PType.DRAGON,"",true,1),
	DYNAMIC_PUNCH(100,50,100,0,0,0,PType.FIGHTING,"% chance to confuse foe",false,1),
	EARTH_POWER(90,100,10,0,1,0,PType.GROUND,"% chance to lower foe's Sp.Def by 1",false,1),
	EARTHQUAKE(100,100,0,0,0,0,PType.GROUND,"A normal attack",false,1),
	ELECTRIC_TERRAIN(0,1000,0,0,2,0,PType.ELECTRIC,"Sets the terrain to ELECTRIC for 5 turns",false,1),
	ELECTRO_BALL(-1,100,0,0,1,0,PType.ELECTRIC,"Power is higher the faster the user is than the target",false,1),
	//ELECTROEXPLOSION(300,100,0,0,1,0,PType.ELECTRIC,"User faints. Bypasses Ground's immunity to Electric",false,1),
	ELEMENTAL_SPARKLE(45,90,0,0,1,0,PType.MAGIC,"",false,1),
	EMBER(40,100,10,0,1,0,PType.FIRE,"% chance to Burn foe",false,1),
	ENCORE(0,100,0,0,2,0,PType.NORMAL,"",false,1),
	ENDEAVOR(0,100,0,0,0,0,PType.NORMAL,"",true,1),
	ENDURE(0,1000,0,0,2,4,PType.NORMAL,"",false,1),
	ENERGY_BALL(90,100,10,0,1,0,PType.GRASS,"",false,1),
	ENTRAINMENT(0,100,0,0,2,0,PType.NORMAL,"Changes foe's ability to the user's",false,1),
	ERUPTION(-1,100,0,0,1,0,PType.FIRE,"Power is higher the more HP the user has",false,1),
	EXPANDING_FORCE(-1,100,0,0,1,0,PType.PSYCHIC,"Power is stronger if the terrain is PSYCHIC",false,1),
	EXPLOSION(250,100,0,0,0,0,PType.NORMAL,"User faints",false,1),
	EXTRASENSORY(80,100,10,0,1,0,PType.PSYCHIC,"",false,1),
	METRONOME(-1,1000,0,0,1,0,PType.MAGIC,"Selects a random move!",false,1),
	EXTREME_SPEED(80,100,0,0,0,2,PType.NORMAL,"Always goes first",true,1),
	FAKE_OUT(40,100,100,0,0,3,PType.NORMAL,"",true,1),
	FAKE_TEARS(0,100,0,0,2,0,PType.DARK,"Lowers foe's Sp.Def by 2",false,1),
	FAILED_SUCKER(0,100,0,0,0,0,PType.DARK,"If you're seeing this, something went horribly wrong",false,1),
	FALSE_SURRENDER(80,1000,0,0,0,0,PType.DARK,"",true,1),
	FATAL_BIND(70,85,100,0,0,0,PType.BUG,"Causes foe to faint in 3 turns",true,1),
	FEATHER_DANCE(0,100,0,0,2,0,PType.FLYING,"Lowers foe's Attack by 2",false,1),
	FEINT(30,100,0,0,0,2,PType.NORMAL,"",false,1),
	FEINT_ATTACK(60,1000,0,0,0,0,PType.DARK,"This attack always hits",true,1),
	FALSE_SWIPE(40,100,0,0,0,0,PType.NORMAL,"Always leaves the foe with at least 1 HP",true,1),
	FELL_STINGER(50,100,0,0,0,0,PType.BUG,"",true,1),
	FIERY_DANCE(80,100,50,0,2,0,PType.FIRE,"",false,1),
	FIRE_BLAST(120,85,10,0,1,0,PType.FIRE,"% chance to Burn foe",false,1),
	//FIRE_CHARGE(75,90,10,0,0,0,PType.FIRE,"% of flinching and/or Burning foe",false,1),
	//FIRE_DASH(0,100,100,0,0,0,PType.FIRE,"% to Burn foe, user faints. Damage equals user's remaining HP",false,1),
	FIRE_FANG(65,95,10,0,0,0,PType.FIRE,"% of flinching and/or Burning foe",true,1),
	FIRE_PUNCH(75,100,10,0,0,0,PType.FIRE,"% to Burn foe",true,1),
	FIRE_SPIN(35,85,100,0,1,0,PType.FIRE,"% to spin foe for 2-5 turns. While foe is spun, it takes 1/16 HP in damage, and cannot switch",false,1),
	//FIRE_TAIL(85,90,10,0,0,0,PType.FIRE,"% to Burn foe",false,1),
	//FIREBALL(-1,100,10,0,1,0,PType.FIRE,"% chance to Burn foe, damage is doubled if foe is Burned",false,1),
	FIRST_IMPRESSION(90,100,0,0,0,1,PType.BUG,"Always attacks first, fails after the first turn a user is out in battle",true,1),
	FISSURE(0,30,0,0,0,0,PType.GROUND,"",false,1),
	FLAIL(-1,100,0,0,0,0,PType.NORMAL,"Power is higher the lower HP the user has",true,1),
	FLAME_BURST(70,100,0,0,1,0,PType.FIRE,"A normal attack",false,1),
	FLAME_CHARGE(50,100,100,0,0,0,PType.FIRE,"",true,1),
	FLAME_WHEEL(70,100,10,0,0,0,PType.FIRE,"% to Burn foe",true,1),
	FLAMETHROWER(90,100,10,0,1,0,PType.FIRE,"% to Burn foe",false,1),
	FLARE_BLITZ(120,100,10,0,0,0,PType.FIRE,"% to Burn foe, user takes 1/3 of damage inflicted",true,1),
	FLASH(0,100,0,0,2,0,PType.LIGHT,"Lowers foe's Accuracy by 1, and raises user's Sp.Atk by 1",false,1),
	FLASH_CANNON(80,100,10,0,1,0,PType.STEEL,"% chance to lower foe's Sp.Def by 1",false,1),
	FLASH_RAY(40,100,50,0,1,0,PType.LIGHT,"",false,1),
	FLATTER(0,100,100,0,2,0,PType.DARK,"Confuses foe, and raises their Sp.Atk by 2",false,1),
	FLY(100,100,0,0,0,0,PType.FLYING,"A normal attack",true,1),
	FOCUS_BLAST(120,70,0,0,1,0,PType.FIGHTING,"",false,1),
	FOCUS_ENERGY(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	FORCE_PALM(60,100,30,0,0,0,PType.FIGHTING,"",true,1),
	FORESIGHT(0,1000,0,0,2,0,PType.MAGIC,"Indentifies foe, replacing their Ghost typing with Normal if they have it. It also raises user's Accuracy by 1 stage",false,1),
	FORESTS_CURSE(0,100,0,0,2,0,PType.GRASS,"",false,1),
	FOUL_PLAY(95,100,0,0,0,0,PType.DARK,"",true,1),
	FREEZE$DRY(70,100,10,0,1,0,PType.ICE,"",false,1),
	FREEZING_GLARE(90,100,20,0,1,0,PType.PSYCHIC,"",false,1),
	FRENZY_PLANT(150,90,0,0,1,0,PType.GRASS,"User must rest after using this move",false,1),
	FRUSTRATION(-1,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	FURY_ATTACK(15,85,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,1),
	FURY_CUTTER(-1,95,0,0,0,0,PType.BUG,"Power increases the more times this move is used in succession",true,1),
	FURY_SWIPES(18,80,0,0,0,0,PType.NORMAL,"Attacks 2-5 times",true,1),
	FUSION_BOLT(100,100,0,0,0,0,PType.ELECTRIC,"",false,1),
	FUSION_FLARE(100,100,0,0,1,0,PType.FIRE,"",false,1),
	//GALAXY_ATTACK(115,90,30,0,0,0,PType.MAGIC,"% chance to inflict the foe with a random Status condition",false,1),
	GALAXY_BLAST(90,100,0,0,1,0,PType.GALACTIC,"",false,1),
	GASTRO_ACID(0,100,0,0,2,0,PType.POISON,"",false,1),
	GENESIS_SUPERNOVA(120,95,0,0,1,0,PType.GALACTIC,"",false,1),
	GEOMANCY(0,1000,0,0,2,0,PType.LIGHT,"",false,1),
	GIGA_DRAIN(75,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt to foe",false,1),
	//GIGA_HIT(110,75,50,0,0,0,PType.FIGHTING,"% chance to Paralyze foe",false,1),
	GIGA_IMPACT(150,90,0,0,0,0,PType.NORMAL,"User must rest after using this move",true,1),
	GLACIATE(65,95,100,0,1,0,PType.ICE,"",false,1),
	GLARE(0,100,0,0,2,0,PType.NORMAL,"Paralyzes foe",false,1),
	GLITTER_DANCE(0,1000,0,0,2,0,PType.LIGHT,"Raises user's Sp.Atk and Speed by 1",false,1),
	GLITTERING_SWORD(95,100,20,0,0,0,PType.LIGHT,"",true,1),
	GLITTERING_TORNADO(55,100,30,0,1,0,PType.LIGHT,"",false,1),
	GLITZY_GLOW(80,100,30,0,1,0,PType.LIGHT,"",false,1),
	GRASS_KNOT(-1,100,0,0,1,0,PType.GRASS,"A normal attack",true,1),
	GRASS_WHISTLE(0,55,0,0,2,0,PType.GRASS,"",false,1),
	GRASSY_TERRAIN(0,1000,0,0,2,0,PType.GRASS,"Sets the terrain to GRASSY for 5 turns",false,1),
	//GRASS_PUNCH(80,100,0,0,0,0,PType.GRASS,"A normal attack",false,1),
	GRAVITY(0,1000,0,0,2,0,PType.GALACTIC,"Sets GRAVITY for 5 turns",false,1),
	GROWL(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Attack by 1",false,1),
	GROWTH(0,1000,0,0,2,0,PType.GRASS,"Raises user's Attack and Sp.Atk by 1",false,1),
	GUILLOTINE(0,30,0,0,0,0,PType.NORMAL,"",true,1),
	GUNK_SHOT(120,70,30,0,1,0,PType.POISON,"% chance to Poison foe",false,1),
	//GUNSHOT(70,60,0,2,0,0,PType.STEEL,"25% chance to Crit. If it Crits, foe is Bleeding",false,1),
	GUST(40,100,0,0,1,0,PType.FLYING,"A normal attack",false,1),
	GYRO_BALL(-1,100,0,0,0,0,PType.STEEL,"The lower the user's speed compared to the foe, the more power",true,1),
	SNOWSCAPE(0,1000,0,0,2,0,PType.ICE,"Sets the weather to SNOW for 5 turns",false,1),
	HAMMER_ARM(100,90,100,0,0,0,PType.FIGHTING,"% chance to lower user's speed by 1",true,1),
	HARDEN(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Defense by 1",false,1),
	HAZE(0,1000,0,0,2,0,PType.ICE,"Clears all stat changes on the field",false,1),
	HEAD_SMASH(150,80,0,0,0,0,PType.ROCK,"User takes 1/3 of damage inflicted",true,1),
	HEADBUTT(70,100,30,0,0,0,PType.NORMAL,"% chance of causing foe to flinch",true,1),
	HEAL_PULSE(0,1000,0,0,2,0,PType.PSYCHIC,"Heals foe by 50% HP",false,1),
	HEALING_WISH(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	HEAT_CRASH(-1,100,0,0,0,0,PType.FIRE,"",true,1),
	HEAT_WAVE(95,90,10,0,1,0,PType.FIRE,"% to Burn foe",false,1),
	HEAVY_SLAM(-1,100,0,0,0,0,PType.STEEL,"",true,1),
	HEX(-1,100,0,0,1,0,PType.GHOST,"",false,1),
	HI_JUMP_KICK(130,90,0,0,0,0,PType.FIGHTING,"If this attack misses, user takes 50% of its max HP",true,1),
	HIDDEN_POWER(60,100,0,0,1,0,PType.NORMAL,"",false,1),
	HONE_CLAWS(0,1000,0,0,2,0,PType.DARK,"Raises user's Attack and Accuracy by 1",false,1),
	HORN_ATTACK(65,100,0,0,0,0,PType.NORMAL,"A normal attack",false,1), // recharge
	HORN_DRILL(0,30,0,0,0,0,PType.NORMAL,"If this move hits, it always K.Os foe",true,1),
	HORN_LEECH(75,100,0,0,0,0,PType.GRASS,"",true,1),
	HOWL(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack by 1",false,1),
	HURRICANE(110,70,30,0,1,0,PType.FLYING,"",false,1),
	HYDRO_CANNON(150,90,0,0,1,0,PType.WATER,"User must rest after using this move",false,1),
	HYDRO_PUMP(110,80,0,0,1,0,PType.WATER,"A normal attack",false,1),
	HYPER_BEAM(150,90,0,0,1,0,PType.NORMAL,"User must rest after using this move",false,1),
	HYPER_FANG(80,90,10,0,0,0,PType.NORMAL,"% of causing foe to flinch",true,1),
	HYPER_VOICE(90,100,0,0,1,0,PType.NORMAL,"A normal attack",true,1),
	HYPNOSIS(0,60,0,0,2,0,PType.PSYCHIC,"Causes foe to sleep",false,1),
	ICE_BALL(-1,90,0,0,0,0,PType.ICE,"",true,1),
	ICE_BEAM(90,100,10,0,1,0,PType.ICE,"",false,1),
	ICE_FANG(65,95,10,0,0,0,PType.ICE,"",true,1),
	ICE_PUNCH(75,100,20,0,0,0,PType.ICE,"",true,1),
	ICE_SHARD(40,100,0,0,0,1,PType.ICE,"Always goes first",false,1),
	ICE_SPINNER(80,100,100,0,0,0,PType.ICE,"",true,1),
	ICICLE_CRASH(85,90,30,0,0,0,PType.ICE,"",false,1),
	ICICLE_SPEAR(25,100,0,0,0,0,PType.ICE,"",false,1),
	ICY_WIND(55,95,100,0,1,0,PType.ICE,"",false,1),
	//IGNITE(0,75,0,0,2,0,PType.FIRE,"Burns foe",false,1),
	INCINERATE(60,100,100,0,1,0,PType.FIRE,"% to lower foe's Sp. Def by 1",false,1),
	INFERNO(100,50,100,0,1,0,PType.FIRE,"",false,1),
	INFESTATION(20,100,100,0,1,0,PType.BUG,"",true,1),
	INGRAIN(0,1000,0,0,2,0,PType.GRASS,"",false,1),
	//INJECT(55,100,100,0,0,0,PType.BUG,"% to Poison foe, heals 50% of damage dealt",false,1),
	IRON_BLAST(85,90,30,0,1,0,PType.STEEL,"",false,1),
	IRON_DEFENSE(0,1000,0,0,2,0,PType.STEEL,"Raises user's Defense by 2",false,1),
	IRON_HEAD(80,100,30,0,0,0,PType.STEEL,"% of causing foe to flinch",true,1),
	IRON_TAIL(100,75,30,0,0,0,PType.STEEL,"% of lowering foe's Defense by 1",true,1),
	JAW_LOCK(80,100,100,0,0,0,PType.DARK,"",true,1),
	KARATE_CHOP(50,100,0,1,0,0,PType.FIGHTING,"Boosted Crit rate",true,1),
	LAVA_PLUME(80,100,30,0,1,0,PType.FIRE,"% to Burn foe",false,1),
	//LEAF_BALL(75,95,0,0,1,0,PType.GRASS,"A normal attack",false,1),
	LEAF_BLADE(90,100,0,1,0,0,PType.GRASS,"Boosted Crit rate",true,1),
	//LEAF_GUST(50,100,0,0,1,0,PType.GRASS,"A normal attack",false,1),
	//LEAF_KOBE(75,90,100,0,0,0,PType.GRASS,"% to Paralyze foe",false,1),
	//LEAF_PULSE(85,75,100,0,1,0,PType.GRASS,"% to lower foe's accuracy, 50% to cause foe to fall asleep",false,1),
	//LEAF_SLAP(30,100,0,0,0,0,PType.GRASS,"A normal attack",false,1),
	LEAF_STORM(130,90,100,0,1,0,PType.GRASS,"% to lower user's Sp.Atk by 2",false,1),
	LEAF_TORNADO(65,90,50,0,1,0,PType.GRASS,"% to lower foe's Accuracy by 1",false,1),
	LEAFAGE(40,100,0,0,0,0,PType.GRASS,"",false,1),
	LEECH_LIFE(80,100,0,0,0,0,PType.BUG,"Heals 50% of damage dealt",true,1), // recoil
	LEECH_SEED(0,90,0,0,2,0,PType.GRASS,"At the end of every turn, user steals 1/8 of foe's max HP",false,1),
	LEER(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 1",false,1),
	LICK(20,100,30,0,0,0,PType.GHOST,"% to Paralyze foe",true,1),
	//LIGHTNING_HEADBUTT(90,95,30,0,0,0,PType.ELECTRIC,"% of Paralysis and/or causing foe to flinch. User takes 1/3 of damage dealt as recoil",false,1),
	LIFE_DEW(0,1000,0,0,2,0,PType.WATER,"Restores 25% HP",false,1),
	LIGHT_BEAM(60,100,20,0,1,0,PType.LIGHT,"",false,1),
	LIGHT_OF_RUIN(140,90,0,0,1,0,PType.LIGHT,"",false,1),
	LIGHT_SCREEN(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	LIQUIDATION(85,100,20,0,0,0,PType.WATER,"",true,1),
	LOAD_FIREARMS(0,100,0,0,2,0,PType.STEEL,"",false,1),
	LOCK$ON(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Accuracy by 6",false,1),
	LOVELY_KISS(0,75,0,0,2,0,PType.NORMAL,"",false,1),
	LOW_KICK(-1,100,0,0,0,0,PType.FIGHTING,"Damage is based on how heavy foe is",true,1),
	LOW_SWEEP(65,100,100,0,0,0,PType.FIGHTING,"",true,1),
	LUNAR_DANCE(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	LUSTER_PURGE(70,100,50,0,1,0,PType.LIGHT,"",false,1),
	MACH_PUNCH(40,100,0,0,0,1,PType.FIGHTING,"Always goes first",true,1),
	MACHETE_JAB(75,80,100,0,0,0,PType.STEEL,"% to lower foe's Attack by 1",true,1),
	MAGIC_BLAST(30,100,0,0,1,0,PType.MAGIC,"A random Rock, Ground or Grass move is also used",false,1),
	MAGIC_CRASH(110,80,100,0,0,0,PType.MAGIC,"% to inflict foe with a random Status condition. User must rest after using",true,1),
	MAGIC_FANG(70,95,75,0,0,0,PType.MAGIC,"% to flinch foe if this move is Super-Effective against it",true,1),
	MAGIC_POWDER(0,100,0,0,2,0,PType.MAGIC,"",false,1),
	MAGIC_REFLECT(0,1000,0,0,2,0,PType.MAGIC,"Foe's next attack will be reflected against them. Can be used every other turn",false,1),
	MAGIC_TOMB(90,100,0,0,1,0,PType.MAGIC,"A normal attack",true,1),
	MAGICAL_LEAF(60,1000,0,0,1,0,PType.GRASS,"This move will never miss",false,1),
	MAGNET_BOMB(60,1000,0,0,0,0,PType.STEEL,"",false,1),
	MAGNET_RISE(0,1000,0,0,2,0,PType.STEEL,"User will float for 5 turns, causing it to be immune to all Ground-type attacks",false,1),
	MAGNITUDE(-1,100,0,0,0,0,PType.GROUND,"A random Magnitude between 4-10 will be used, corresponding to its power",false,1),
	MEAN_LOOK(0,100,0,0,2,0,PType.NORMAL,"",false,1),
	MEGA_DRAIN(40,100,0,0,1,0,PType.GRASS,"Heals 50% of damage dealt",false,1),
	MEGAHORN(120,85,0,0,0,0,PType.BUG,"",true,1),
	MEMENTO(0,100,0,0,2,0,PType.DARK,"",false,1),
	METAL_CLAW(50,95,10,0,0,0,PType.STEEL,"",true,1),
	//MEGA_KICK(70,100,60,0,0,0,PType.FIGHTING,"% to Paralyze foe",true,1),
	//MEGA_PUNCH(70,100,60,0,0,0,PType.FIGHTING,"% to Paralyze foe",true,1),
	//MEGA_SWORD(70,100,60,0,0,0,PType.STEEL,"% to Paralyze foe",false,1),
	METAL_SOUND(0,100,0,0,2,0,PType.STEEL,"Lowers foe's Sp.Def by 2",false,1),
	METEOR_ASSAULT(120,100,100,0,0,0,PType.GALACTIC,"",false,1),
	METEOR_MASH(90,90,20,0,0,0,PType.STEEL,"",true,1),
	MINIMIZE(0,1000,0,0,2,0,PType.GHOST,"Raises user's Evasion by 2",false,1),
	MIRROR_SHOT(65,85,30,0,1,0,PType.STEEL,"",false,1),
	MIRROR_MOVE(0,1000,0,0,1,0,PType.FLYING,"Uses the move last used by the foe, fails if foe hasn't used a move yet",false,1),
	MIST_BALL(70,100,50,0,1,0,PType.PSYCHIC,"",false,1),
	MOLTEN_CONSUME(50,100,100,0,0,1,PType.FIRE,"% chance to Burn foe",true,1),
	MOLTEN_LAIR(0,100,0,0,2,0,PType.FIRE,"",false,1),
	MOLTEN_STEELSPIKE(100,90,30,0,1,0,PType.STEEL,"",false,1),
	MOONBLAST(95,100,30,0,1,0,PType.LIGHT,"",false,1),
	MOONLIGHT(0,1000,0,0,2,0,PType.LIGHT,"Restores 1/2 of user's max HP",false,1),
	MORNING_SUN(0,1000,0,0,2,0,PType.LIGHT,"",false,1),
	MUD_BOMB(65,85,30,0,1,0,PType.GROUND,"% to lower foe's Accuracy by 1",false,1),
	MUD_SHOT(55,95,100,0,1,0,PType.GROUND,"",false,1),
	MUD$SLAP(20,100,100,0,1,0,PType.GROUND,"% to lower foe's Accuracy by 1",false,1),
	MUD_SPORT(0,1000,0,0,2,0,PType.GROUND,"",false,1),
	MUDDY_WATER(90,85,30,0,1,0,PType.WATER,"",false,1),
	MYSTICAL_FIRE(75,100,100,0,1,0,PType.FIRE,"",false,1),
	NASTY_PLOT(0,1000,0,0,2,0,PType.DARK,"",false,1),
	NEEDLE_ARM(90,100,30,0,1,0,PType.GRASS,"",true,1),
	//NEEDLE_SPRAY(55,95,10,0,0,0,PType.POISON,"% to Poison or Paralyze foe",false,1),
	//NIBBLE(10,100,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	NIGHT_DAZE(85,95,40,0,0,0,PType.DARK,"",false,1),
	NIGHT_SHADE(0,100,0,0,1,0,PType.GHOST,"Deals damage equal to user's level",false,1),
	NIGHT_SLASH(70,100,0,1,0,0,PType.DARK,"Boosted Crit rate",true,1),
	NIGHTMARE(0,100,0,0,2,0,PType.GHOST,"Foe loses 1/4 of max HP each turn; wears off when foe wakes up",false,1),
	NO_RETREAT(0,1000,0,0,2,0,PType.FIGHTING,"",false,1),
	NOBLE_ROAR(0,100,0,0,2,0,PType.NORMAL,"",false,1),
	NUZZLE(20,100,100,0,0,0,PType.ELECTRIC,"",true,1),
	OBSTRUCT(0,1000,0,0,2,4,PType.DARK,"",false,1),
	ODOR_SLEUTH(0,1000,0,0,2,0,PType.NORMAL,"Indentifies foe, replacing their Ghost typing with Normal if they have it. It also lowers foe's Evasion by 1",false,1),
	OUTRAGE(120,100,0,0,0,0,PType.DRAGON,"User is locked into this move for 2-3 turns, Confuses user when the effect is done",true,1),
	OVERHEAT(140,90,100,0,1,0,PType.FIRE,"% to lower user's Sp.Atk by 2",false,1),
	PARABOLIC_CHARGE(65,100,0,0,1,0,PType.ELECTRIC,"",false,1),
	PAYBACK(-1,100,0,0,0,0,PType.DARK,"",true,1),
	PECK(35,100,0,0,0,0,PType.FLYING,"A normal attack",true,1),
	PERISH_SONG(0,1000,0,0,2,0,PType.GHOST,"All Pokemon hearing this song will faint in 3 turns",false,1),
	PETAL_BLIZZARD(90,100,0,0,0,0,PType.GRASS,"",false,1),
	PETAL_DANCE(120,100,0,0,1,0,PType.GRASS,"",true,1),
	PHANTOM_FORCE(100,100,0,0,0,0,PType.GHOST,"",true,1),
	PHOTON_GEYSER(130,90,100,0,1,0,PType.LIGHT,"",false,1),
	PIN_MISSILE(25,95,0,0,0,0,PType.BUG,"",false,1),
	PISTOL_POP(110,70,0,0,0,0,PType.STEEL,"",false,1),
	PLASMA_FISTS(100,100,0,0,0,0,PType.ELECTRIC,"",true,1),
	PLAY_NICE(0,100,0,0,2,0,PType.NORMAL,"",false,1),
	PLAY_ROUGH(90,90,10,0,0,0,PType.LIGHT,"",true,1),
	PLUCK(60,100,0,0,0,0,PType.FLYING,"",true,1),
	//PHASE_SHIFT(0,1000,0,0,2,0,PType.MAGIC,"Switches user's type to Magic and the type of the move that the foe just used",false,1),
	//POISON_BALL(65,80,100,0,0,0,PType.POISON,"% to Poison foe",false,1),
	POISON_FANG(50,100,50,0,0,0,PType.POISON,"% to Poison and/or flinch foe",true,1),
	POISON_GAS(0,80,0,0,2,0,PType.POISON,"Poisons foe",true,1),
	POISON_JAB(80,100,30,0,0,0,PType.POISON,"% to Poison foe",true,1),
	//POISON_POWDER(0,75,0,0,2,0,PType.POISON,"Poisons foe",false,1),
	POISON_STING(15,100,30,0,0,0,PType.POISON,"% chance to Poison foe",false,1),
	POISON_TAIL(85,100,10,1,0,0,PType.POISON,"% chance to Poison foe. Boosted crit rate",false,1),
	POP_POP(70,80,0,0,0,0,PType.STEEL,"",false,1),
	//POISONOUS_WATER(95,85,30,0,1,0,PType.POISON,"% chance to Poison foe",false,1),
	//POKE(10,100,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	POUND(40,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	POWDER_SNOW(40,100,50,0,1,0,PType.ICE,"",false,1),
	POWER_GEM(80,100,0,0,1,0,PType.ROCK,"",false,1),
	POWER_WHIP(120,85,0,0,0,0,PType.GRASS,"",true,1),
	POWER$UP_PUNCH(40,100,100,0,0,0,PType.FIGHTING,"",true,1),
	PRISMATIC_LASER(100,100,0,0,1,0,PType.LIGHT,"",false,1),
	PROTECT(0,1000,0,0,2,4,PType.NORMAL,"",false,1),
	PSYBEAM(65,100,10,0,1,0,PType.PSYCHIC,"",false,1),
	PSYCHIC(90,100,10,0,1,0,PType.PSYCHIC,"",false,1),
	PSYCHIC_FANGS(85,100,100,0,0,0,PType.PSYCHIC,"",true,1),
	PSYCHIC_TERRAIN(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	PSYCHO_CUT(70,100,0,1,0,0,PType.PSYCHIC,"Boosted Crit rate",false,1),
	PSYSHOCK(80,100,0,0,1,0,PType.PSYCHIC,"",false,1),
	PSYWAVE(0,100,0,0,1,0,PType.PSYCHIC,"",false,1),
	//PUNCH(40,90,0,0,0,0,PType.FIGHTING,"A normal attack",false,1),
	PURSUIT(40,100,0,0,0,0,PType.DARK,"A normal attack",true,1),
	QUICK_ATTACK(40,100,0,0,0,1,PType.NORMAL,"Always attacks first",true,1),
	QUIVER_DANCE(0,1000,0,0,2,0,PType.BUG,"",false,1),
	RAGE(-1,100,0,0,0,0,PType.NORMAL,"Power increases the more times this move is used in succession",true,1),
	RAIN_DANCE(0,1000,0,0,2,0,PType.WATER,"",false,1),
	RAPID_SPIN(20,100,100,0,0,0,PType.NORMAL,"% to raise user's Speed by 1, and frees user of being Spun",true,1),
	RAZOR_LEAF(55,95,0,1,0,0,PType.GRASS,"Boosted Crit rate",false,1),
	RAZOR_SHELL(75,95,50,0,0,0,PType.WATER,"",true,1),
	REBOOT(0,1000,0,0,2,0,PType.STEEL,"Clears user or any Status condition, and raises user's Speed by 1",false,1),
	RECOVER(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	RED$NOSE_BOOST(0,1000,0,0,2,0,PType.MAGIC,"",false,1),
	REFLECT(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	REST(0,1000,0,0,2,0,PType.PSYCHIC,"",false,1),
	RETURN(-1,100,0,0,0,0,PType.NORMAL,"",true,1),
	REVENGE(-1,100,0,0,0,0,PType.FIGHTING,"Power is doubled if user is slower than foe",true,1),
	REVERSAL(-1,100,0,0,2,0,PType.FIGHTING,"",true,1),
	ROAR(0,1000,0,0,2,-6,PType.NORMAL,"",false,1),
	//ROCK_BLADE(80,100,0,1,0,0,PType.ROCK,"Boosted Crit rate",false,1),
	ROCK_BLAST(25,90,0,0,0,0,PType.ROCK,"Hits 2-5 times",false,1),
	ROCK_POLISH(0,1000,0,0,2,0,PType.ROCK,"Raises user's Speed by 2",false,1),
	ROCK_SLIDE(75,90,30,0,0,0,PType.ROCK,"% of causing foe to flinch",false,1),
	ROCK_THROW(50,90,0,0,0,0,PType.ROCK,"A normal attack",false,1),
	ROCK_TOMB(60,95,100,0,0,0,PType.ROCK,"% to lower foe's Speed by 1",false,1),
	ROCK_WRECKER(150,90,0,0,0,0,PType.ROCK,"User takes 1/3 of damage dealt as recoil",false,1),
	ROCKFALL_FRENZY(75,95,100,0,1,0,PType.ROCK,"",false,1),
	//ROCKET(120,75,50,0,0,0,PType.STEEL,"% to Paralyze foe",false,1),
	ROLLOUT(-1,90,0,0,0,0,PType.ROCK,"Attacks up to 5 times, damage doubles each time. While active, user cannot switch out",true,1),
	ROOST(0,1000,0,0,2,0,PType.FLYING,"Restores 1/2 of user's max HP",false,1),
	//ROOT_CRUSH(105,90,100,0,0,0,PType.ROCK,"% to Paralyze foe",false,1),
	ROOT_KICK(60,95,0,0,0,0,PType.ROCK,"A normal attack",true,1),
	ROUND(60,100,0,0,1,0,PType.NORMAL,"",false,1),
	SACRED_FIRE(100,95,50,0,0,0,PType.FIRE,"",false,1),
	SACRED_SWORD(90,100,0,0,0,0,PType.FIGHTING,"",true,1),
	SAFEGUARD(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	SAND_ATTACK(0,100,0,0,2,0,PType.GROUND,"Lowers foe's Accuracy by 1",false,1),
	SANDSTORM(0,1000,0,0,2,0,PType.ROCK,"",false,1),
	SCALD(80,100,30,0,1,0,PType.WATER,"",false,1),
	SCALE_SHOT(25,90,100,0,2,0,PType.DRAGON,"",false,1),
	SCARY_FACE(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Speed by 2",false,1),
	SCORCHING_SANDS(70,100,30,0,1,0,PType.GROUND,"",false,1),
	SCRATCH(40,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	SCREECH(0,85,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 2",false,1),
	SEA_DRAGON(0,1000,0,0,2,0,PType.MAGIC,"",false,1),
	SEISMIC_TOSS(0,100,0,0,0,0,PType.FIGHTING,"Damage dealt is equal to the user's level",true,1),
	SELF$DESTRUCT(200,100,0,0,0,0,PType.NORMAL,"User faints",false,1),
	SHADOW_BALL(80,100,30,0,1,0,PType.GHOST,"% to lower foe's Sp.Def by 1",false,1),
	SHADOW_CLAW(80,100,0,1,0,0,PType.GHOST,"",true,1),
	SHADOW_PUNCH(80,1000,0,0,0,0,PType.GHOST,"",true,1), // TODO: give to Poov line
	SHADOW_SNEAK(40,100,0,0,0,1,PType.GHOST,"Always attacks first",true,1),
	SHEER_COLD(0,30,0,0,1,0,PType.ICE,"",false,1),
	//SHELL_BASH(70,100,0,1,0,0,PType.NORMAL,"User takes 1/3 of damage dealt as recoil",false,1),
	SHELL_SMASH(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack, Sp.Atk, and Speed by 2, at the cost of lowering its Defense and Sp.Def by 1",false,1),
	SHIFT_GEAR(0,1000,0,0,2,0,PType.STEEL,"",false,1),
	//SHOCK(15,100,100,0,1,0,PType.ELECTRIC,"% to Paralyze foe",false,1),
	SHOCK_WAVE(60,1000,0,0,1,0,PType.ELECTRIC,"This attack never misses",false,1),
	SILVER_WIND(60,100,10,0,1,0,PType.BUG,"",false,1),
	//SHURIKEN(75,85,50,0,0,0,PType.STEEL,"% to cause foe to Bleed",false,1),
	SKULL_BASH(100,100,100,0,0,0,PType.NORMAL,"% to raise the user's Defense by 1, user must charge on the first turn",true,1),
	SKY_ATTACK(140,90,30,1,0,0,PType.FLYING,"% chance to flinch. User must charge up on the first turn, attacks on the second. Boosted Crit rate",false,1),
	SKY_UPPERCUT(85,90,0,0,0,0,PType.FIGHTING,"A normal attack",true,1),
	SLACK_OFF(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	SLAM(80,75,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	//SLAP(20,100,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	SLASH(70,100,0,1,0,0,PType.NORMAL,"Boosted Crit rate",true,1),
	SLEEP_POWDER(0,75,0,0,2,0,PType.GRASS,"Foe falls asleep",false,1),
	SLUDGE(75,100,30,0,1,0,PType.POISON,"% to Poison foe",false,1),
	SLUDGE_BOMB(90,100,30,0,1,0,PType.POISON,"% to Poison foe",false,1),
	SLUDGE_WAVE(95,100,10,0,1,0,PType.POISON,"",false,1),
	SMACK_DOWN(50,100,100,0,0,0,PType.ROCK,"",false,1),
	SMART_STRIKE(70,1000,0,0,0,0,PType.STEEL,"",false,1),
	//SMASH(70,90,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	SMOG(20,70,50,0,1,0,PType.POISON,"% to Poison foe",false,1),
	SMOKESCREEN(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's accuracy by 1",false,1),
	SNARL(55,95,100,0,1,0,PType.DARK,"",false,1),
	SNORE(50,100,0,0,1,0,PType.NORMAL,"",false,1),
	SOLAR_BEAM(120,100,0,0,1,0,PType.GRASS,"User must charge up on the first turn, attacks on the second",false,1),
	SOLAR_BLADE(125,100,0,0,0,0,PType.GRASS,"",true,1),
	SPACE_BEAM(60,100,30,0,1,0,PType.GALACTIC,"",false,1),
	SPACIAL_REND(100,95,0,1,1,0,PType.GALACTIC,"",false,1),
	SPARK(65,100,30,0,0,0,PType.ELECTRIC,"% to Paralyze foe",true,1),
	SPARKLE_STRIKE(80,1000,0,0,0,0,PType.MAGIC,"",true,1),
	SPARKLING_ARIA(90,100,100,0,1,0,PType.WATER,"",false,1),
	SPARKLING_TERRAIN(0,1000,0,0,2,0,PType.MAGIC,"",false,1),
	SPARKLING_WATER(0,1000,0,0,2,0,PType.WATER,"Raises Sp.Def by 1. Turns into Sparkling Aria when used by Kissyfishy-D",false,1),
	SPARKLY_SWIRL(70,100,10,0,1,0,PType.MAGIC,"",false,1),
	SPECTRAL_THIEF(90,100,100,0,0,0,PType.GHOST,"",true,1),
	SPEEDY_SHURIKEN(40,100,0,0,0,1,PType.STEEL,"",false,1),
	SPIKE_CANNON(20,100,0,0,0,0,PType.NORMAL,"",false,1),
	//SPIKE_JAB(55,80,100,0,0,0,PType.POISON,"% to Poison foe",false,1),
	//SPIKE_SHOT(-1,100,0,0,0,0,PType.POISON,"Attacks 2-5 times",false,1),
	SPIKES(0,1000,0,0,2,0,PType.GROUND,"",false,1),
	//SPIKE_SLAM(65,90,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	SPIKY_SHIELD(0,1000,0,0,2,4,PType.GRASS,"",false,1),
	SPIRIT_BREAK(75,100,100,0,0,0,PType.LIGHT,"",true,1),
	SPLASH(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	STAR_STORM(110,85,0,0,1,0,PType.GALACTIC,"A normal attack",false,1),
	STAR_STRUCK_ARCHER(75,85,0,3,0,0,PType.GALACTIC,"",false,1),
	STEALTH_ROCK(0,1000,0,0,2,0,PType.ROCK,"",false,1),
	STEEL_BEAM(140,95,100,0,1,0,PType.STEEL,"",false,1),
	STEEL_WING(70,90,10,0,0,0,PType.STEEL,"",true,1),
	STICKY_WEB(0,1000,0,0,2,0,PType.BUG,"",false,1),
	STOCKPILE(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	//STARE(0,100,0,0,2,0,PType.NORMAL,"Confuses foe, but raises foe's Attack by 1",false,1), // recoil
	//STING(55,100,100,0,0,0,PType.BUG,"% to make foe Bleed",false,1),
	STOMP(65,100,30,0,0,0,PType.NORMAL,"% of causing foe to flinch",true,1),
	STONE_EDGE(100,80,0,1,0,0,PType.ROCK,"Boosted Crit rate",false,1),
	STRENGTH_SAP(0,100,0,0,2,0,PType.GRASS,"",false,1),
	STRENGTH(80,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	STRING_SHOT(0,100,0,0,2,0,PType.BUG,"Lowers foe's Speed by 2",false,1),
	STRUGGLE_BUG(50,100,100,0,1,0,PType.BUG,"",false,1),
	//STRONG_ARM(90,85,30,0,0,0,PType.FIGHTING,"% chance of Paralyzing and/or causing foe to flinch",false,1),
	STUN_SPORE(0,75,0,0,2,0,PType.GRASS,"",false,1),
	SUBMISSION(80,90,0,0,0,0,PType.FIGHTING,"",true,1),
	SUCKER_PUNCH(80,100,0,0,0,2,PType.DARK,"Always attacks first. Fails if foe didn't use an attacking move",true,1),
	SUNNY_DAY(0,1000,0,0,2,0,PType.FIRE,"",false,1),
	SUNNY_DOOM(80,100,0,0,1,0,PType.LIGHT,"If this attack faints foe, causes weather to turn SUNNY",false,1),
	SUNSTEEL_STRIKE(100,100,0,0,0,0,PType.STEEL,"",true,1),
	//SUPER_CHARGE(90,50,100,0,0,0,PType.ELECTRIC,"% of causing foe to flinch, user takes 1/3 of damage dealt as recoil",false,1),
	SUPER_FANG(0,90,0,0,0,0,PType.NORMAL,"Halves foe's remaining HP",true,1),
	SUPERCHARGED_SPLASH(10,100,50,0,1,0,PType.WATER,"% chance to raise user's Sp.Atk by 1. Turns into Thunder when used by Kissyfishy-D",false,1),
	SUPERNOVA_EXPLOSION(200,100,0,0,1,0,PType.GALACTIC,"",false,1),
	SUPERPOWER(120,100,100,0,0,0,PType.FIGHTING,"% of lowering user's Attack and Defense by 1",true,1),
	SUPERSONIC(0,55,0,0,2,0,PType.NORMAL,"Confuses foe",false,1),
	SWAGGER(0,85,0,0,2,0,PType.NORMAL,"Confuses foe, but raises foe's Attack by 2",false,1),
	//SWEEP_KICK(60,95,100,0,0,0,PType.FIGHTING,"% to lower foe's Attack by 1",false,1),
	SWEET_KISS(0,75,0,0,2,0,PType.LIGHT,"",false,1),
	SWEET_SCENT(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	SWIFT(60,1000,0,0,1,0,PType.MAGIC,"This attack never misses",false,1),
	//SWORD_SLASH(75,90,0,1,0,0,PType.STEEL,"Boosted Crit rate",false,1), // recoil
	//SWORD_SLICE(65,85,0,1,0,0,PType.STEEL,"Boosted Crit rate. If it Crits, foe is Bleeding",false,1),
	SWORD_SPIN(50,95,100,0,0,0,PType.STEEL,"% to raise user's Attack by 1",false,1),
	//SWORD_STAB(95,60,100,0,0,0,PType.STEEL,"% to cause foe to Bleed",false,1),
	SWORDS_DANCE(0,1000,0,0,2,0,PType.NORMAL,"Raises user's Attack by 2",false,1),
	SYNTHESIS(0,1000,0,0,2,0,PType.GRASS,"Restores 1/2 of user's max HP",false,1),
	TACKLE(50,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	//TAIL_WHACK(90,85,0,0,0,0,PType.NORMAL,"A normal attack",false,1),
	TAIL_GLOW(0,1000,0,0,2,0,PType.BUG,"",false,1),
	TAIL_WHIP(0,100,0,0,2,0,PType.NORMAL,"Lowers foe's Defense by 1",false,1),
	TAILWIND(0,1000,0,0,2,0,PType.FLYING,"",false,1),
	TAKE_DOWN(90,85,0,0,0,0,PType.NORMAL,"User takes 1/3 of damage dealt as recoil",true,1),
	TAKE_OVER(0,100,0,0,2,0,PType.GHOST,"Foe's next attack is used on itself. Can be used once every other turn",false,1),
	TAUNT(0,100,0,0,2,0,PType.DARK,"",false,1),
	TEETER_DANCE(0,100,0,0,2,0,PType.NORMAL,"",false,1),
	TELEPORT(0,1000,0,0,2,-6,PType.PSYCHIC,"",false,1),
	THRASH(120,100,0,0,0,0,PType.NORMAL,"",true,1),
	THROAT_CHOP(80,100,100,0,0,0,PType.DARK,"",true,1),
	THUNDER(120,70,30,0,1,0,PType.ELECTRIC,"% of Paralyzing foe",false,1),
	THUNDER_FANG(65,95,10,0,0,0,PType.ELECTRIC,"% of Paralyzing and/or flinching foe",true,1),
	//THUNDER_KICK(80,90,10,0,0,0,PType.ELECTRIC,"% of Paralyzing foe",true,1),
	THUNDER_PUNCH(75,100,10,0,0,0,PType.ELECTRIC,"% of Paralyzing foe",true,1),
	THUNDER_WAVE(0,90,0,0,2,0,PType.ELECTRIC,"Paralyzes foe",false,1),
	THUNDERBOLT(90,100,10,0,1,0,PType.ELECTRIC,"% of Paralyzing foe",false,1), // recoil
	THUNDERSHOCK(40,100,10,0,1,0,PType.ELECTRIC,"% of Paralyzing foe",false,1),
	TOPSY$TURVY(0,1000,0,0,2,0,PType.DARK,"",false,1),
	TORMENT(0,100,0,0,2,0,PType.DARK,"",false,1),
	//TIDAL_WAVE(-1,100,0,0,1,0,PType.WATER,"Picks a random tide level from the time of day. Morning = 90, Day = 50, and Evening = 130 Base Power",false,1),
	TORNADO_SPIN(60,95,100,0,0,0,PType.FIGHTING,"% to raise user's Speed and Accuracy by 1, and frees user of being Spun",true,1),
	TOXIC(0,90,0,0,2,0,PType.POISON,"Badly poisons foe",false,1),
	TOXIC_SPIKES(0,1000,0,0,2,0,PType.POISON,"",false,1),
	TRI_ATTACK(80,100,20,0,1,0,PType.NORMAL,"",false,1),
	TRICK_ROOM(0,1000,0,0,2,-7,PType.PSYCHIC,"",false,1),
	TWINKLE_TACKLE(85,90,20,0,0,0,PType.MAGIC,"",true,1),
	TWINEEDLE(25,100,30,0,0,0,PType.BUG,"",false,1),
	TWISTER(40,100,10,0,1,0,PType.DRAGON,"% of causing foe to flinch",false,1),
	UNSEEN_STRANGLE(60,100,100,0,0,0,PType.DARK,"",true,1),
	U$TURN(70,100,0,0,0,0,PType.BUG,"",true,1),
	VACUUM_WAVE(40,100,0,0,1,1,PType.FIGHTING,"",false,1),
	V$CREATE(180,95,100,0,0,0,PType.FIRE,"",true,1),
	VENOM_DRENCH(0,100,0,0,2,0,PType.POISON,"",false,1),
	VENOM_SPIT(40,100,100,0,1,0,PType.POISON,"",false,1), // TODO
	VENOSHOCK(-1,100,0,0,1,0,PType.POISON,"",false,1),
	VISE_GRIP(55,100,0,0,0,0,PType.NORMAL,"",true,1),
	VINE_WHIP(45,100,0,0,0,0,PType.GRASS,"A normal attack",true,1),
	VOLT_SWITCH(70,100,0,0,1,0,PType.ELECTRIC,"",false,1),
	VITAL_THROW(60,1000,0,0,0,-1,PType.FIGHTING,"This attack never misses, but goes last",false,1),
	VOLT_TACKLE(120,100,10,0,0,0,PType.ELECTRIC,"% to Paralyze foe. User takes 1/3 of damage dealt as recoil",true,1),
	WAKE$UP_SLAP(-1,100,0,0,0,0,PType.FIGHTING,"If foe is asleep, power is doubled, but the foe wakes up",true,1),
	WATER_CLAP(20,100,20,0,0,0,PType.WATER,"% to Paralyze foe. Turns into Dragon Darts when used by Kissyfishy-D",true,1),
	WATER_FLICK(0,100,0,0,2,0,PType.WATER,"Lowers foe's Attack by 1. Turns into Flamethrower when used by Kissyfishy-D",false,1),
	WATER_KICK(75,100,0,0,0,0,PType.WATER,"A normal attack. Turns into Hi Jump Kick when used by Kissyfishy-D",true,1),
	WATER_SMACK(40,95,30,0,0,0,PType.WATER,"% chance of causing foe to flinch. Turns into Darkest Lariat when used by Kissyfishy-D",true,1),
	WATER_SPOUT(-1,100,0,0,1,0,PType.WATER,"",false,1),
	WATER_SPORT(0,1000,0,0,2,0,PType.WATER,"",false,1),
	WAVE_CRASH(120,100,0,0,0,0,PType.WATER,"",false,1),
	WATER_GUN(40,100,0,0,1,0,PType.WATER,"A normal attack",false,1),
	//WATER_JET(50,100,0,0,0,1,PType.WATER,"Always attacks first",false,1),
	WATER_PULSE(60,100,30,0,1,0,PType.WATER,"% to Confuse foe",false,1),
	WATERFALL(80,100,10,0,0,0,PType.WATER,"% of causing foe to flinch",true,1),
	WEATHER_BALL(-1,100,0,0,1,0,PType.NORMAL,"",false,1),
	WHIP_SMASH(120,100,0,0,0,0,PType.NORMAL,"A normal attack",true,1),
	WHIRLPOOL(35,85,100,0,1,0,PType.WATER,"% to spin foe for 2-5 turns. While foe is spun, it takes 1/16 HP in damage, and cannot switch",false,1),
	WHIRLWIND(0,1000,0,0,2,-6,PType.FLYING,"",false,1),
	WILL$O$WISP(0,85,0,0,2,0,PType.FIRE,"Burns foe",false,1),
	WING_ATTACK(60,100,0,0,0,0,PType.FLYING,"A normal attack",true,1),
	WISH(0,1000,0,0,2,0,PType.NORMAL,"",false,1),
	WITHDRAW(0,1000,0,0,2,0,PType.WATER,"",false,1),
	WORRY_SEED(0,100,0,0,2,0,PType.GRASS,"",false,1),
	//WOOD_FANG(50,100,50,0,0,0,PType.ROCK,"% to cause foe to flinch",false,1), // recoil
	WRAP(15,90,100,0,0,0,PType.NORMAL,"% to spin foe for 2-5 turns. While foe is spun, it takes 1/16 HP in damage, and cannot switch",true,1),
	//WRING_OUT(-1,100,0,0,0,0,PType.NORMAL,"Attack's power is greater the more HP the foe has",true,1),
	X$SCISSOR(80,100,0,1,0,0,PType.BUG,"Boosted Crit rate",true,1),
	ZAP_CANNON(120,50,100,0,1,0,PType.ELECTRIC,"",false,1),
	//ZAP(20,100,0,0,0,0,PType.ELECTRIC,"A normal attack",false,1),
	ZEN_HEADBUTT(80,90,30,0,0,0,PType.PSYCHIC,"% of causing foe to flinch",true,1),
	ZING_ZAP(80,100,30,0,0,0,PType.ELECTRIC,"",true,1),
	
	TERRAIN_PULSE(-1,100,0,0,1,0,PType.NORMAL,"",false,1),
	FACADE(-1,100,0,0,0,0,PType.NORMAL,"",true,1),
	SLEEP_TALK(-1,100,0,0,2,0,PType.NORMAL,"",false,1),
	CAPTIVATE(-1,100,0,0,2,0,PType.NORMAL,"",false,1),
	BATON_PASS(-1,100,0,0,2,0,PType.NORMAL,"",false,1),
	FLIP_TURN(60,100,0,0,0,0,PType.WATER,"",true,1),
	CUT(55,95,0,3,0,0,PType.NORMAL,"This move always crits",true,1),
	ROCK_SMASH(40,100,100,0,0,0,PType.FIGHTING,"% to lower foe's Defense by 1",true,1),
	VINE_CROSS(70,95,100,0,0,0,PType.GRASS,"% chance to lower foe's Speed by 1",false,1),
	SURF(90,100,0,0,1,0,PType.WATER,"A normal attack",false,1),
	SLOW_FALL(75,90,100,0,1,0,PType.PSYCHIC,"% chance to change user's ability to LEVITATE",false,1),
	ROCK_CLIMB(80,95,20,0,1,0,PType.ROCK,"% chance to confuse foe",false,1),
	LAVA_SURF(90,100,0,0,1,0,PType.FIRE,"A normal attack",false,1),
	BOOSTED_PURSUIT(80,100,0,0,0,0,PType.DARK,"If you're seeing this something went wrong",true,1),
	
	ABYSSAL_CHOP(-1,90,50,0,0,0,PType.DRAGON,"% chance to paralyse foe. Damage is doulbed if foe is paralyzed",true,1),
	SUMMIT_STRIKE(70,95,100,0,0,0,PType.FIGHTING,"% to lower foe's Defense Stats by one stage. 30% to flinch foe",true,1),
	
	STRUGGLE(40,1000,0,0,0,0,PType.UNKNOWN,"",true,1),
	
	;
	
	public static Move getMove(String moveName) {
	    for (Move move : Move.values()) {
	        if (move.toString().equalsIgnoreCase(moveName)) {
	            return move;
	        }
	    }
	    return null;
	}

	public int accuracy;
	
	public int basePower;
	
	public int cat;
	
	public int critChance;
	private String desc;
	public PType mtype;
	public int priority;
	public int secondary;
	public boolean contact;
	public boolean userStat;
	public int pp;
	Move(int bp, int acc, int sec, int crit, int cat, int p, PType type, String desc, boolean contact, int pp){
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
	}
	public String getbp() {
		if (basePower == -1) return "Varies";
		return basePower + "";
	}
	public String getCategory() {
		if (cat == 0) return "Physical";
		if (cat == 1) return "Special";
		else {
			return "Status";
		}
	}

	public String getDescription() {
		if (this.secondary > 0) {
            return secondary + desc;
        } else {
        	return desc;
        }
		
	}
	
	public String getDescriptor() {
		String message = "Move: " + toString() + "\n";
        message += "Type: " + mtype + "\n";
        message += "BP: " + getbp() + "\n";
        message += "Accuracy: " + getAccuracy() + "\n";
        message += "Category: " + getCategory() + "\n";
        message += "Description: " + getDescription();
        
        return message;
	}
	
	public boolean isAttack() {
		return cat != 2;
	}

	public boolean isPhysical() {
		return cat == 0;
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
	
	public int getNumHits(Pokemon[] team) {
		if (this == Move.DOUBLE_SLAP || this == Move.FURY_ATTACK ||this == Move.FURY_SWIPES || this == Move.ICICLE_SPEAR ||
				this == Move.PIN_MISSILE || this == Move.ROCK_BLAST|| this == Move.SCALE_SHOT || this == Move.SPIKE_CANNON) {
			int randomNum = (int) (Math.random() * 100) + 1; // Generate a random number between 1 and 100 (inclusive)
	        if (randomNum <= 35) {
	            return 2; // 2 hits with 35% probability
	        } else if (randomNum <= 70) {
	            return 3; // 3 hits with 35% probability
	        } else if (randomNum <= 85) {
	            return 4; // 4 hits with 15% probability
	        } else {
	            return 5; // 5 hits with 15% probability
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
				if (p != null && !p.isFainted()) {
					result++;
				}
			}
			return result;
		} else {
			return 1;
		}
	}
	public boolean isHMmove() {
		if (this == Move.CUT || this == Move.ROCK_SMASH || this == Move.VINE_CROSS || this == Move.SURF || this == Move.SLOW_FALL || this == Move.FLY
			|| this == Move.ROCK_CLIMB || this == Move.LAVA_SURF) {
			return true;
		}
		return false;
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
		
		return result;
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
		result.add(SLASH);
		result.add(SOLAR_BLADE);
		result.add(X$SCISSOR);
		result.add(CROSS_POISON);
		result.add(CUT);
		result.add(RAZOR_LEAF);
		
		if (result.contains(this)) {
			return true;
		}
		return false;
	}
	
	public boolean isTM() {
		for (int i = 93; i < 200; i++) {
			Item test = new Item(i);
			if (test.getMove() == this) {
				return true;
			}
		}
		return false;
	}

}