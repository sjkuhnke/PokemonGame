package pokemon;

import java.util.EnumSet;
import java.util.Set;

import pokemon.Field.Effect;

/**
 * §7.5 / §9 (Phase 4): which secondary effects are worth BRANCHING on (status or flinch), and the effective
 * proc chance the simulator should branch with. Everything else with a secondary effect keeps SimPolicy's
 * MAJORITY behavior (procs at >= 50%), which is the "expected value" stand-in the spec allows for stat drops and
 * the like: those move {@code matchupScore} by a stage or two, not a status/turn.
 * <p>
 * The two sets below are GENERATED from {@code Pokemon.secondaryEffect}: a case group goes in STATUS if its body
 * calls {@code foe.paralyze/burn/freeze/poison/toxic/sleep/confuse}, and in FLINCH_ONLY if the only such call is
 * {@code foe.flinch()}. If you add a move with a status/flinch secondary, add it here, or re-run
 * tools/gen_secondary_kinds.py (see PHASE4_CHANGES.md), which regenerates both sets from Pokemon.java.
 */
public final class SecondaryKinds {
	private SecondaryKinds() {}

	static final Set<Move> STATUS = EnumSet.of(
			Move.ABYSSAL_CHOP, Move.BEEFY_BASH, Move.BITTER_MALICE, Move.BLAZE_KICK, Move.BLIZZARD, Move.BLUE_FLARE,
			Move.BODY_SLAM, Move.BOLT_STRIKE, Move.BOUNCE, Move.CONFUSION, Move.CROSS_POISON, Move.DESOLATE_VOID,
			Move.DIRE_CLAW, Move.DISCHARGE, Move.DIZZY_PUNCH, Move.DRAGON_BREATH, Move.DYNAMIC_PUNCH, Move.EMBER,
			Move.FIRE_BLAST, Move.FIRE_FANG, Move.FIRE_PUNCH, Move.FLAMETHROWER, Move.FLAME_WHEEL, Move.FLARE_BLITZ,
			Move.FORCE_PALM, Move.FREEZING_GLARE, Move.GUNK_SHOT, Move.HEAT_WAVE, Move.HOCUS_POCUS, Move.HURRICANE,
			Move.HYDRO_VORTEX, Move.ICE_BEAM, Move.ICE_FANG, Move.ICE_PUNCH, Move.INFERNO, Move.LAVA_PLUME,
			Move.LAVA_SURF, Move.LICK, Move.MAGICAL_CRASH, Move.MOLTEN_CONSUME, Move.MOLTEN_STEELSPIKE,
			Move.MORTAL_SPIN, Move.NUZZLE, Move.POISON_FANG, Move.POISON_JAB, Move.POISON_STING, Move.POISON_TAIL,
			Move.POWDER_SNOW, Move.PSYBEAM, Move.ROCK_CLIMB, Move.SACRED_FIRE, Move.SAMBAL_SEAR, Move.SCALD,
			Move.SCORCHING_SANDS, Move.SIGNAL_BEAM, Move.SLUDGE, Move.SLUDGE_BOMB, Move.SLUDGE_WAVE, Move.SMOG,
			Move.SNOW_PLUME, Move.SPARK, Move.THUNDER, Move.THUNDERBOLT, Move.THUNDERSHOCK, Move.THUNDER_FANG,
			Move.THUNDER_PUNCH, Move.TWINEEDLE, Move.VANISHING_ACT, Move.VENOM_SPIT, Move.VENOSTEEL_CROSSCUT,
			Move.VOLT_TACKLE, Move.WATER_CLAP, Move.WATER_PULSE, Move.ZAP_CANNON);

	static final Set<Move> FLINCH_ONLY = EnumSet.of(
			Move.AIR_SLASH, Move.ASTONISH, Move.BITE, Move.DARK_PULSE, Move.DRAGON_RUSH, Move.EXTRASENSORY,
			Move.FAKE_OUT, Move.FIERY_WRATH, Move.HEADBUTT, Move.HYPER_FANG, Move.ICICLE_CRASH, Move.IRON_BLAST,
			Move.IRON_HEAD, Move.MAGIC_FANG, Move.MANA_PUNCH, Move.NEEDLE_ARM, Move.ROCK_SLIDE, Move.SKY_ATTACK,
			Move.STOMP, Move.TWISTER, Move.UNSEEN_STRANGLE, Move.WATERFALL, Move.WATER_SMACK, Move.ZEN_HEADBUTT,
			Move.ZING_ZAP);

	public static boolean isStatusOrFlinch(Move m) {
		return STATUS.contains(m) || FLINCH_ONLY.contains(m);
	}

	static boolean isFlinchOnly(Move m) {
		return FLINCH_ONLY.contains(m);
	}

	/**
	 * Effective % chance that {@code move}'s secondary effect procs against {@code defender}. Mirrors the
	 * secChance chain in {@code Pokemon.move()}: Sheer Force zeroes it, Shield Dust / Covert Cloak zero it,
	 * Serene Grace and Sparkly Terrain (grounded user) each double it.
	 */
	public static int effectiveChance(Pokemon attacker, Pokemon defender, Move move, Field field) {
		int c = move.getSecondaryChance();
		if (c <= 0 || move.cat == 2) return 0;
		if (attacker.getAbility(field) == Ability.SHEER_FORCE) return 0;
		if (defender.getAbility(field) == Ability.SHIELD_DUST || defender.getItem(field) == Item.COVERT_CLOAK) return 0;
		if (attacker.getAbility(field) == Ability.SERENE_GRACE) c *= 2;
		if (field.equals(field.terrain, Effect.SPARKLY) && attacker.isGrounded()) c *= 2;
		return Math.min(c, 100);
	}
}
