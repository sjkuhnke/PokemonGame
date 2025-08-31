package pokemon;

public enum Ability {

	ADAPTABILITY("Powers up moves of the same type."),
	AMBUSH("Gives priority to the first move when this Pokemon enters battle."),
	ANALYTIC("Boosts move power when the Pokemon moves last."),
	ANGER_POINT("Maxes highest attack after taking a critical hit."),
	ANTICIPATION("Senses the foe's supereffective moves, reducing damage from the first one if it exists."),
	INSOMNIA("Prevents the Pokemon from falling asleep."),
	BATTLE_ARMOR("This Pokemon cannot be Critical hit."),
	BEAST_BOOST("Boosts the Pokemon's highest stat every time it faints a foe."),
	BERSERK("Sharply boosts the Pokemon's highest stat when it falls below half HP."),
	BLACK_HOLE("Restores HP if hit by a LIGHT or GALACTIC move."),
	BLAZE("Powers up FIRE moves by 20%, or 50% in a pinch."),
	BRAINWASH("Forces the opponent's stat changes to have an opposite effect while the user is active."),
	CHLOROPHYLL("Boosts the Pokemon's Speed stat in SUNSHINE."),
	CLEAR_BODY("Prevents other Pokemon from lowering its stats."),
	CLOUD_NINE("Nullifies the weather on switch-in and while it's active."),
	COLD_HEART("Immune to all PSYCHIC moves."),
	COMPETITIVE("Boosts the Pokemon's Sp. Atk stat when its stats are lowered."),
	COMPOUND_EYES("The Pokemon's accuracy is boosted."),
	CONTRARY("Makes stat changes have an opposite effect."),
	CORROSION("The Pokemon can hit STEEL Pokemon with POISON moves, and can always Poison the target regardless of typing."),
	COSMIC_WARP("Twists the dimensions for 4 turns when the Pokemon enters the battle, reversing the speed order."),
	CURSED_BODY("May disable a move used on the Pokemon."),
	DEFIANT("Boosts the Pokemon's Attack stat when its stats are lowered."),
	DJINN1S_FAVOR("Raises highest attack if hit by a MAGIC move."),
	DRIZZLE("The Pokemon makes it RAIN when it enters a battle."),
	DRY_SKIN("Restores HP in RAIN or when hit by WATER moves, but reduces HP in SUN and is weaker to FIRE."),
	DROUGHT("Turns the sunlight HARSH when it enters a battle."),
	ELECTRIC_SURGE("Turns the ground into ELECTRIC TERRAIN when the Pokemon enters a battle."),
	EMPATHIC_LINK("The Pokemon gets a boost in their highest attack after the foe raises any stat."),
	ENCHANTED_DUST("Changes the foe's type to MAGIC upon entry."),
	EVERGLOW("Sets up Aurora Glow effect when the Pokemon enters battle, healing all ally LIGHT, ICE and GALACTIC types."),
	ILLUMINATION("Grants the user all the resistances from the LIGHT type."),
	FILTER("Reduces damage from supereffective attacks."),
	FLAME_BODY("Contact with the Pokemon may burn the attacker."),
	FLASH_FIRE("It powers up FIRE moves if it's hit by one."),
	FLUFFY("Halves the damage from moves that make direct contact, but doubles that of FIRE moves."),
	FORTIFY("At the end of each turn this Pokemon isn't hit by a damaging move, it gains +1 Defense (max 2 stacks)."),
	FRIENDLY_GHOST("Gives full immunity to all GHOST moves."),
	FULL_FORCE("Boosts the Attack stat by 2 stages if the Pokemon's held item is used or lost."),
	GALACTIC_AURA("Halves the damage of ICE and PSYCHIC moves."),
	GALVANIZE("NORMAL moves become ELECTRIC moves. The power of those moves is boosted a little."),
	GLACIER_AURA("Weakens the power of Physical moves."),
	GOOEY("Contact with the Pokemon lowers the attacker's Speed stat."),
	GRASSY_SURGE("Turns the ground into GRASSY TERRAIN when the Pokemon enters a battle."),
	GRAVITATION("Creates GRAVITY when the Pokemon enters a battle."),
	GUTS("Boosts the Attack stat if the Pokemon has a status condition."),
	HEAT_COMPACTION("Raises Defense and Sp. Def if hit by an FIRE move."),
	HUGE_POWER("Doubles the Pokemon's Attack stat."),
	HYDRATION("Cures the Pokemon's status conditions in RAIN."),
	HYPER_CUTTER("Prevents other Pokemon from lowering Attack stat."),
	ICE_BODY("The Pokemon gradually regains HP in SNOW."),
	ICY_SCALES("The Pokemon is protected by ice scales, which halve the damage from special moves."),
	ILLUSION("The Pokemon enters with an illusion that boosts damage and prevents non GHOST foes from escaping."),
	INNER_FOCUS("The Pokemon is protected from flinching; ignores switch-in stat-lowering Abilities."),
	INSECT_FEEDER("Restores HP if hit by a BUG move."),
	INTIMIDATE("Lowers the opposing Pokemon's Attack stat."),
	IRON_BARBS("Inflicts damage to the Pokemon on contact."),
	IRON_FIST("Boosts the power of punching moves."),
	JACKPOT(""), // TODO
	JUSTIFIED("Halves damage from DARK moves and sharply boosts Attack when hit by one."),
	KEEN_EYE("Prevents the Pokemon from losing accuracy."),
	LEVITATE("Gives full immunity to all GROUND moves."),
	LIGHTNING_ROD("Raises highest attack if hit by an ELECTRIC move."),
	MAGIC_BOUNCE("Reflects status moves instead of getting hit by them."),
	MAGIC_GUARD("The Pokemon only takes damage from attacks."),
	MAGICAL("Powers up MAGIC moves."),
	MAGMA_ARMOR("This Pokemon can't be Critical hit, and prevents the Pokemon from getting a frostbite."),
	MERCILESS("The Pokemon's attacks become critical hits if the target is poisoned, badly poisoned or paralyzed."),
	MIRROR_ARMOR("Bounces back only the stat-lowering effects that the Pokemon receives."),
	MOLD_BREAKER("Moves can be used on the target regardless of its Abilities."),
	MOODY("Raises one stat sharply and lowers another every turn."),
	MOSAIC_WINGS("Distorts type matchups to make non-super effective attacks resisted."),
	MOTOR_DRIVE("Raises Speed if hit by an ELECTRIC move."),
	MOUTHWATER("Taunts the foe on switch-in for 4 turns."),
	MOXIE("Boosts highest attack after knocking out any Pokemon."),
	MULTISCALE("Reduces damage the Pokemon takes when its HP is full."),
	MYSTIC_ABSORB("Restores HP if hit by a MAGIC move."),
	MYSTIC_RIFT("Creates a bizzare room for 5 turns when the Pokemon enters the battle, removing the effects of items."),
	NATURAL_CURE("All status conditions heal when the Pokemon switches out."),
	NO_GUARD("Ensures the Pokemon and its foe's attacks land."),
	NORMALIZE("All the Pokemon's moves become Normal type. The power of those moves is boosted."),
	OVERGROW("Powers up GRASS moves by 20%, or 50% in a pinch."),
	PARASOCIAL("The Pokemon borrows life force from anyone under its control."),
	PERISH_BODY("When hit by a contact move, the Pokemon and the attacker will faint after three turns."),
	PICKPOCKET("Steals an item from the foe on switch-in if this Pokemon is not holding an item."),
	PIXILATE("NORMAL moves become LIGHT moves. The power of those moves is boosted a little."),
	POISON_HEAL("Restores HP if the Pokemon is poisoned instead of losing HP."),
	POISON_POINT("Contact with the Pokemon may poison the attacker."),
	POISON_TOUCH("May poison a target when the Pokemon makes contact."),
	PRANKSTER("Gives priority to a status move."),
	PRESSURE("The Pokemon doubles the foe's PP usage."),
	PROTEAN("Makes the Pokemon's type to the move type it's about to use."),
	PSYCHIC_AURA("Weakens the power of Special moves."),
	PSYCHIC_SURGE("Turns the ground into PSYCHIC TERRAIN when the Pokemon enters a battle."),
	RADIANT("Lowers foe's Accuracy when this Pokemon hits with a LIGHT move."),
	RAIN_DISH("The Pokemon gradually regains HP in RAIN."),
	RATTLED("DARK, GHOST, and BUG moves scare the Pokemon and boost its Speed stat."),
	RECKLESS("Powers up moves that have recoil damage."),
	REFORGE("Restores 1/16 HP at the end of each turn."),
	REGENERATOR("Restores a little HP when withdrawn from battle."),
	REFRIGERATE("NORMAL moves become ICE moves. The power of those moves is boosted a little."),
	ROCK_HEAD("Prevents recoil damage."),
	ROUGH_SKIN("Inflicts damage to the Pokemon on contact."),
	SAND_FORCE("Boosts the power of moves in a SANDSTORM."),
	SAND_RUSH("Boosts the Pokemon's Speed stat in a SANDSTORM."),
	SAND_STREAM("The Pokemon summons a SANDSTORM when it enters a battle."),
	SAND_VEIL("When SANDSTORM is active, this Pokemon is evasive."),
	SAP_SIPPER("Raises highest attack if hit by an GRASS move."),
	SEABED_SIFTER("Clears any terrain on entry, and if a terrain was cleared, restores HP to full."),
	SERENE_GRACE("Boosts the likelihood of additional effects occurring when attacking."),
	SCALY_SKIN("Lowers the foe's Attack, and this Pokemon only takes damage from attacks."),
	SCRAPPY("The Pokemon can hit GHOST Pokemon with NORMAL and FIGHTING moves; ignores switch-in stat-lowering Abilities."),
	SHADOW_TAG("This Pokemon steps on the opposing Pokemon's shadow to prevent it from escaping."),
	SHADOW_VEIL("The Pokemon is protected from an attack once per battle, at the cost of some HP."),
	SHARP_TAIL("Boosts the power of Tail-using moves."),
	SHARPNESS("Boosts the power of Slicing moves."),
	SHED_SKIN("The Pokemon may heal its own status conditions."),
	SHEER_FORCE("Removes additional effects to increase the power of moves when attacking."),
	SHELL_ARMOR("This Pokemon cannot be Critical hit."),
	SHIELD_DUST("Blocks the added effects of attacks taken and is immune to entry hazards."),
	SIMPLE("The stat changes the Pokemon receives are doubled."),
	SKILL_LINK("Makes multi-hit moves always hit the maximum amount."),
	SLEIGHT_OF_HAND("Gives priority to MAGIC moves when at full HP."),
	SLIPSTREAM("Switches the Pokemon out when its HP becomes half or less."),
	SLUSH_RUSH("Boosts the Pokemon's Speed stat in SNOW."),
	SNIPER("Powers up moves if they become critical hits."),
	SNOW_CLOAK("When SNOW is active, this Pokemon is evasive."),
	SNOW_WARNING("The Pokemon makes it SNOW when it enters a battle."),
	SOLAR_POWER("Boosts the Sp. Atk stat in SUNNY weather, but HP decreases."),
	SOLID_ROCK("Reduces damage from supereffective attacks."),
	SPARKLY_SURGE("Turns the ground into SPARKLY TERRAIN when the Pokemon enters a battle."),
	SPEED_BOOST("Its Speed stat is gradually boosted."),
	STARBORN("Raises highest attack by 1 if Aurora Glow is active."),
	STATIC("Contact with the Pokemon may paralyze the attacker."),
	STEELWORKER("Powers up STEEL moves."),
	STICKY_HOLD("Protects the Pokemon from item theft."),
	STRONG_JAW("The Pokemon's strong jaw boosts the power of its biting moves."),
	STURDY("It cannot be knocked out with one hit. One-hit KO moves cannot knock it out, either."),
	SUPER_LUCK("Heightens the critical-hit ratios of moves."),
	SWARM("Powers up BUG moves by 20%, or 50% in a pinch."),
	SWIFT_SWIM("Boosts the Pokemon's Speed stat in RAIN."),
	SYNCHRONIZE("Passes a burn, poison, frostbite, or paralysis to the foe."),
	TALENTED("When the Pokemon enters a battle, it copies the foe's stat changes."),
	TECHNICIAN("Powers up the Pokemon's weaker moves."),
	TERRAFORGE("Raises all stats by 1 in any terrain, wears off once the terrain ends."),
	TERRIFY("Lowers the opposing Pokemon's Sp. Atk stat."),
	THICK_FAT("Boosts resistance to FIRE and ICE moves."),
	THREATENING("Lowers the opposing Pokemon's Defense stat."),
	TINTED_LENS("Powers up \"not very effective\" moves."),
	TORRENT("Powers up WATER moves by 20%, or 50% in a pinch."),
	TOUGH_CLAWS("Powers up moves that make direct contact."),
	TOXIC_DEBRIS("Puts toxic spikes on the opposing side when hit by physical moves."),
	TRACE("The Pokemon copies a foe's Ability."),
	TYPE_MASTER("Grants STAB on all moves."),
	UNAWARE("Ignores the opposing Pokemon's stat changes."),
	UNBURDEN("Doubles the Speed stat if the Pokemon's held item is used or lost."),
	UNERODIBLE("Reduces damage from GRASS, WATER, and GROUND attacks."),
	UNWAVERING("Boosts resistance to DARK and GHOST moves; ignores switch-in stat-lowering Abilities."),
	VOLT_ABSORB("Restores HP if hit by a ELECTRIC move."),
	VOLT_VORTEX("Boosts the Pokemon's speed in ELECTRIC TERRAIN."),
	WARM_HEART("Immune to all ICE moves."),
	WATER_ABSORB("Restores HP if hit by a WATER move."),
	WATER_VEIL("Prevents the Pokemon from getting a burn."),
	WEAK_ARMOR("Physical attacks to the Pokemon lower its Defense stat but sharply raise its Speed stat."),
	WHITE_HOLE("Immune to all DRAGON moves."),
	WONDER_GUARD("Only supereffective moves will hit."),
	WONDER_SKIN("The Pokemon has full immunity to other Pokemon's status moves."),
	NULL("No Ability."),
	;

	Ability(String string) {
		desc = string;
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

	public String superToString() {
		return super.toString();
	}
}
