package pokemon;

public enum Ability {
	// useful int flag: 0 if not useful to nullify, 1 if usually useful, 2 if illusion flag is active, 3 for fortify counter, 4 if user has an item, 5 if user is full HP, 6 if user is above half HP
	ADAPTABILITY("Changes the STAB bonus from 1.5x to 2x.", 1),
	AMBUSH("Gives +1 priority to the first move this Pokemon uses when it enters battle.", 0),
	ANALYTIC("Boosts move power by 1.3x when the Pokemon moves last.", 1),
	ANGER_POINT("Maxes highest attack after taking a critical hit.", 1),
	ANTICIPATION("Senses the foe's supereffective moves, halving damage from the first one if it exists.", 2),
	INSOMNIA("Prevents the Pokemon from falling asleep.", 1),
	BATTLE_ARMOR("This Pokemon cannot be Critical hit.", 1),
	BEAST_BOOST("Boosts the Pokemon's highest stat every time it faints a foe.", 1),
	BERSERK("Sharply boosts the Pokemon's highest attacking stat when it falls below half HP.", 6),
	BLACK_HOLE("Restores 1/4 of max HP if hit by a LIGHT or GALACTIC move.", 1),
	BLAZE("Powers up FIRE moves by 20%, or 50% when at or below 1/3 of max HP.", 1),
	BRAINWASH("Makes all stat changes have an opposite effect while on the field.", 1),
	BULLETPROOF("Protects the Pokemon from projectile moves.", 1),
	CHLOROPHYLL("Doubles the Pokemon's Speed stat in SUNSHINE.", 1),
	CLEAR_BODY("Prevents other Pokemon from lowering its stats.", 1),
	CLOUD_NINE("Prevents weather from getting set and clears it on switch-in.", 1),
	COLD_HEART("Immune to all PSYCHIC moves.", 1),
	COMPETITIVE("Boosts the Pokemon's Sp. Atk stat by 2 when its stats are lowered.", 1),
	COMPOUND_EYES("The Pokemon's accuracy is boosted by 1.3x.", 1),
	CONTRARY("Makes stat changes have an opposite effect.", 1),
	CORROSION("The Pokemon can hit STEEL Pokemon with POISON moves, and can always Poison the target regardless of typing.", 1),
	COSMIC_WARP("Twists the dimensions for 4 turns when the Pokemon enters the battle, reversing the speed order.", 0),
	CURSED_BODY("30% chance to disable a move used on the Pokemon.", 1),
	DEFIANT("Boosts the Pokemon's Attack stat by 2 when its stats are lowered.", 1),
	DJINN1S_FAVOR("Raises highest attack by 1 if hit by a MAGIC move.", 1),
	DRIZZLE("The Pokemon makes it RAIN for 5 turns when it enters a battle.", 0),
	DRY_SKIN("Restores 1/8 HP in RAIN or 1/4 HP when hit by WATER moves, but loses 1/8 HP in SUN and is 1.25x weaker to FIRE.", 1),
	DROUGHT("Turns the sunlight HARSH for 5 turns when it enters a battle.", 0),
	ELECTRIC_SURGE("Turns the ground into ELECTRIC TERRAIN for 5 turns when the Pokemon enters a battle.", 0),
	EMPATHIC_LINK("The Pokemon gains a boost to its highest attacking stat for each stat stage the foe raises.", 1),
	ENCHANTED_DUST("Changes the foe's type to MAGIC upon entry.", 0),
	EVERGLOW("Sets up Aurora Glow for 5 turns when the Pokemon enters battle, healing all ally LIGHT, ICE and GALACTIC types.", 0),
	EVENT_HORIZON("Restores 1/3 of max HP when hit by a LIGHT move, Ultra Bursts when fainting (resetting stat changes for the field).", 1),
	ILLUMINATION("Grants the user all the resistances from the LIGHT type.", 1),
	FILTER("Halves damage from supereffective attacks.", 1),
	FLAME_BODY("Contact with the Pokemon can cause a burn 30% of the time; immune to burn.", 1),
	FLASH_FIRE("It powers up FIRE moves by 1.5x if it's hit by one.", 1),
	FLUFFY("Halves the damage from moves that make direct contact, but is 2x weaker to FIRE moves.", 1),
	FORTIFY("If this Pokemon doesn't take direct damage for the turn and doesn't protect itself, it gains +1 Defense (max 2 stacks).", 3),
	FRIENDLY_GHOST("Gives full immunity to all GHOST moves.", 1),
	FULL_FORCE("Boosts the Attack stat by 2 stages if the Pokemon's held item is used or lost.", 4),
	GALACTIC_AURA("Halves the damage of ICE and PSYCHIC moves.", 1),
	GALVANIZE("NORMAL moves become ELECTRIC moves. The power of those moves is boosted by 1.2x.", 1),
	GLACIER_AURA("Weakens the power of Physical moves.", 1),
	GLASS_GUARD("When hit by a supereffective attack, the Pokemon creates a Magic Reflect for itself.", 1),
	GOOEY("Damaging the Pokemon lowers the attacker's Speed stat.", 1),
	GRASSY_SURGE("Turns the ground into GRASSY TERRAIN for 5 turns when the Pokemon enters a battle.", 0),
	GRAVITATION("Creates GRAVITY for 5 turns when the Pokemon enters a battle.", 0),
	GUTS("Boosts the Attack stat by 1.5x if the Pokemon has a status condition.", 1),
	HEAT_COMPACTION("Raises Defense and Sp. Def if hit by an FIRE move.", 1),
	HUGE_POWER("Doubles the Pokemon's Attack stat.", 1),
	HYDRATION("Cures the Pokemon's status conditions in RAIN at the end of each turn.", 1),
	HYPER_CUTTER("Prevents other Pokemon from lowering this Pokemon's Attack stat.", 1),
	ICE_BODY("The Pokemon regains 1/8 max HP in SNOW.", 1),
	ICY_SCALES("Halves the damage from special moves.", 1),
	ILLUSION("Entering battle will boost damage by 1.2x and prevents foes from escaping until hit.", 2),
	INNER_FOCUS("The Pokemon is protected from flinching; ignores switch-in stat-lowering Abilities.", 1),
	INSECT_FEEDER("Restores 1/4 max HP if hit by a BUG move.", 1),
	INTIMIDATE("Lowers the opposing Pokemon's Attack stat.", 0),
	IRON_BARBS("Inflicts 1/8 max HP damage to the Pokemon on contact.", 1),
	IRON_FIST("Boosts the power of punching moves by 1.3x.", 1),
	JACKPOT("Rolls a dice with each attack corresponding to its power: 50% - 200% (Loaded Dice = 100% - 200%).", 1),
	JUSTIFIED("Halves damage from DARK moves and sharply boosts Attack when hit by one.", 1),
	KEEN_EYE("Prevents the Pokemon from losing accuracy and ignores opposing Pokemon's evasion stat changes.", 1),
	LEVITATE("Gives full immunity to all GROUND moves.", 1),
	LIGHTNING_ROD("Raises highest attack if hit by an ELECTRIC move.", 1),
	MAGIC_BOUNCE("Reflects status moves that target this Pokemon/Pokemon's side instead of getting hit by them.", 1),
	MAGIC_GUARD("The Pokemon only takes damage from attacks.", 1),
	MAGICAL("Powers up MAGIC moves by 1.5x.", 1),
	MAGMA_ARMOR("This Pokemon can't be Critical hit, and prevents the Pokemon from getting a frostbite.", 1),
	MAGNET_PULL("Prevents STEEL-type Pokemon from switching.", 1),
	MERCILESS("The Pokemon's attacks become critical hits if the target is poisoned, badly poisoned or paralyzed.", 1),
	MIRROR_ARMOR("Bounces back only the stat-lowering effects that the Pokemon receives.", 1),
	MOLD_BREAKER("Ignores the foe's ability when attacking.", 1),
	MOODY("Raises one stat sharply and lowers another every turn.", 1),
	MOSAIC_WINGS("Distorts type matchups to make non-super effective attacks resisted.", 1),
	MOTOR_DRIVE("Raises Speed if hit by an ELECTRIC move.", 1),
	MOUTHWATER("Taunts the foe on switch-in for 4 turns.", 0),
	MOXIE("Boosts highest attack after knocking out any Pokemon.", 1),
	MULTISCALE("Halves damage the Pokemon takes when its HP is full.", 5),
	MYSTIC_ABSORB("Restores 1/4 max HP if hit by a MAGIC move.", 1),
	MYSTIC_RIFT("Creates a bizzare room for 5 turns when the Pokemon enters the battle, removing the effects of items.", 0),
	NATURAL_CURE("All status conditions heal when the Pokemon switches out.", 1),
	NEUROFORCE("Restores 1/3 of max HP when hit by a LIGHT move, super effective moves get a 1.5x boost when used by this Pokemon.", 1),
	NEUTRALIZING_GAS("While the Pokemon is active, the effects of all abilities will be nullified.", 1),
	NO_GUARD("Ensures the Pokemon and its foe's attacks land.", 1),
	NORMALIZE("All the Pokemon's moves become Normal type. The power of their moves are boosted by 1.2x.", 0), // 0 to let the AI know it's good to remove on themselves and probably isn't good to remove on the player
	OVERGROW("Powers up GRASS moves by 20%, or 50% when at or below 1/3 of max HP.", 1),
	PARASOCIAL("The Pokemon steals 1/8 HP from an opposing confused, abducted, or asleep Pokemon.", 1),
	PERISH_BODY("When hit by a contact move, the attacker will faint after three turns.", 1),
	PICKPOCKET("Steals an item from the foe on switch-in if this Pokemon is not holding an item.", 0),
	PIXILATE("NORMAL moves become LIGHT moves. The power of those moves is boosted by 1.2x.", 1),
	POISON_HEAL("Restores 1/8 max HP if the Pokemon is poisoned instead of losing HP.", 1),
	POISON_POINT("Contact with the Pokemon can cause a poison 30% of the time.", 1),
	POISON_TOUCH("May poison a target 30% of the time when the Pokemon makes contact.", 1),
	PRANKSTER("Gives +1 priority to a status move.", 1),
	PRESSURE("The Pokemon doubles the foe's PP usage.", 1),
	PROTEAN("Changes the Pokemon's type to the move type it's about to use.", 1),
	PSYCHIC_AURA("Weakens the power of Special moves.", 1),
	PSYCHIC_SURGE("Turns the ground into PSYCHIC TERRAIN for 5 turns when the Pokemon enters a battle.", 0),
	RADIANT("Lowers foe's Accuracy when this Pokemon hits with a LIGHT move.", 1),
	RAIN_DISH("The Pokemon regains 1/8 max HP in RAIN.", 1),
	RATTLED("Getting hit by DARK, GHOST, or BUG moves boost its Speed stat.", 1),
	RECKLESS("Powers up moves that have recoil damage by 1.3x.", 1),
	REFORGE("Restores 1/16 HP at the end of each turn.", 1),
	REGENERATOR("Restores 1/3 max HP when withdrawn from battle.", 1),
	REFRIGERATE("NORMAL moves become ICE moves. The power of those moves is boosted by 1.2x.", 1),
	ROCK_HEAD("Prevents recoil damage.", 1),
	ROUGH_SKIN("Inflicts 1/8 max HP damage to the Pokemon on contact.", 1),
	SAND_FORCE("Boosts the power of moves in a SANDSTORM by 1.3x; immune to sand damage.", 1),
	SAND_RUSH("Doubles the Pokemon's Speed stat in a SANDSTORM; immune to sand damage.", 1),
	SAND_STREAM("The Pokemon summons a SANDSTORM for 5 turns when it enters a battle; immune to sand damage.", 0),
	SAND_VEIL("Opposing moves are 0.8x accurate while SANDSTORM is active; immune to sand damage.", 1),
	SAP_SIPPER("Raises highest attack if hit by an GRASS move.", 1),
	SEABED_SIFTER("Clears any terrain on entry, and if a terrain was cleared, restores HP to full.", 0),
	SERENE_GRACE("Doubles the chance of secondary effects occurring when attacking.", 1),
	SCALY_SKIN("Lowers the foe's Attack, and this Pokemon only takes damage from attacks.", 1),
	SCRAPPY("The Pokemon can hit GHOST Pokemon with NORMAL and FIGHTING moves; ignores switch-in stat-lowering Abilities.", 1),
	SHADOW_TAG("Prevents the opposing Pokemon from escaping.", 1),
	SHADOW_VEIL("The Pokemon is protected from an attack once per battle, at the cost of 1/8 max HP.", 2),
	SHARP_TAIL("Boosts the power of Tail-using moves by 1.5x.", 1),
	SHARPNESS("Boosts the power of Slicing moves by 1.5x.", 1),
	SHED_SKIN("The Pokemon may heal its own status conditions 50% of the time at the end of each turn.", 1),
	SHEER_FORCE("Removes secondary effects to increase the power of moves by 1.3x when attacking, ignores Life Orb recoil on those moves.", 1),
	SHELL_ARMOR("This Pokemon cannot be Critical hit.", 1),
	SHIELD_DUST("Blocks the secondary effects of attacks taken and is immune to entry hazards.", 1),
	SIMPLE("The stat changes the Pokemon receives are doubled.", 1),
	SKILL_LINK("Makes multi-hit moves always hit the maximum amount.", 1),
	SLEIGHT_OF_HAND("Gives +1 priority to MAGIC moves when at full HP.", 5),
	SLIPSTREAM("Switches the Pokemon out when its HP becomes half or less.", 6),
	SLUSH_RUSH("Doubles the Pokemon's Speed stat in SNOW.", 1),
	SMOKE_SESSION("Lowers the foe's Evasion stat on switch-in and on contact.", 1),
	SNIPER("Powers up moves by 1.5x if they become critical hits.", 1),
	SNOW_CLOAK("Opposing moves are 0.8x accurate while SNOW is active.", 1),
	SNOW_WARNING("The Pokemon makes it SNOW for 5 turns when it enters a battle.", 0),
	SOLAR_POWER("Boosts the Sp. Atk stat in SUNNY weather by 1.5x, but loses 1/8 max HP each turn.", 1),
	SOLID_ROCK("Halves damage from supereffective attacks.", 1),
	SOUNDPROOF("Protects the Pokemon from sound moves.", 1),
	SPARKLY_SURGE("Turns the ground into SPARKLY TERRAIN for 5 turns when the Pokemon enters a battle.", 0),
	SPEED_BOOST("Its Speed stat is boosted at the end of every active turn.", 1),
	STARBORN("Raises highest attack by 1 if Aurora Glow is active.", 1),
	STATIC("Contact with the Pokemon can cause a paralysis 30% of the time.", 1),
	STEELWORKER("Powers up STEEL moves by 1.5x.", 1),
	STICKY_HOLD("Protects the Pokemon from item theft.", 1),
	STRONG_JAW("Boosts the power of its biting moves by 1.5x.", 1),
	STURDY("It cannot be knocked out from full HP. One-hit KO moves cannot knock it out, either.", 5),
	SUPER_LUCK("Heightens the critical-hit ratios of moves from 1/20 to 1/8.", 1),
	SWARM("Powers up BUG moves by 20%, or 50% when at or below 1/3 of max HP.", 1),
	SWIFT_SWIM("Boosts the Pokemon's Speed stat in RAIN.", 1),
	SYNCHRONIZE("Passes a status condition or confusion to the foe when inflicted on it.", 1),
	TALENTED("When the Pokemon enters a battle, it copies the foe's stat boosts.", 0),
	TECHNICIAN("Powers up the Pokemon's moves 60 BP or lower by 1.5x.", 1),
	TERRAFORGE("Raises all stats by 1 in any terrain, wears off once the terrain ends.", 1),
	TERRIFY("Lowers the opposing Pokemon's Sp. Atk stat.", 0),
	THICK_FAT("halves damage from FIRE and ICE moves.", 1),
	THREATENING("Lowers the opposing Pokemon's Defense stat.", 0),
	TINTED_LENS("Powers up \"not very effective\" moves by 2x.", 1),
	TORRENT("Powers up WATER moves by 20%, or 50% when at or below 1/3 of max HP.", 1),
	TOUGH_CLAWS("Powers up moves that make direct contact by 1.3x.", 1),
	TOXIC_DEBRIS("Puts toxic spikes on the opposing side when hit by physical moves.", 1),
	TRACE("The Pokemon copies the foe's Ability on switch-in.", 0),
	TYPE_MASTER("Grants STAB on all moves.", 1),
	UNAWARE("Ignores the opposing Pokemon's stat changes.", 1),
	UNBURDEN("Doubles the Speed stat if the Pokemon's held item is used or lost.", 4),
	UNERODIBLE("Quarters damage taken from GRASS, WATER, and GROUND attacks.", 1),
	UNWAVERING("Halves damage from DARK and GHOST moves; ignores switch-in stat-lowering Abilities.", 1),
	VOLT_ABSORB("Restores 1/4 of max HP if hit by a ELECTRIC move.", 1),
	VOLT_VORTEX("Doubles the Pokemon's speed in ELECTRIC TERRAIN.", 1),
	WARM_HEART("Immune to all ICE moves.", 1),
	WATER_ABSORB("Restores 1/4 of max HP if hit by a WATER move.", 1),
	WATER_VEIL("Prevents the Pokemon from getting a burn.", 1),
	WEAK_ARMOR("Physical attacks to the Pokemon lower its Defense stat but sharply raise its Speed stat.", 1),
	WHITE_HOLE("Immune to all DRAGON moves.", 1),
	WONDER_GUARD("Only supereffective moves will hit.", 1),
	WONDER_SKIN("The Pokemon has full immunity to other Pokemon's status moves.", 1),
	NULL("No Ability.", 0),
	;

	Ability(String string, int flag) {
		desc = string;
		useful = flag;
	}
	
	@Override
	public String toString() {
		String name = super.toString();
		name = name.toLowerCase().replace('_', ' ');
		name = name.replace('1', '\'');
		String[] words = name.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		}
		return sb.toString().trim();
	}
	
	public static Ability getEnum(String string) {
		// Normalize the string
		String normalized = string.toUpperCase().replace(' ', '_');
		normalized = normalized.replace('\'', '1');
		
		try {
			return Ability.valueOf(normalized);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("No matching Move enum found for string: " + string, e);
		}
	}
	
	public String desc;
	public int useful;
	
	public String superToString() {
		return super.toString();
	}
}
