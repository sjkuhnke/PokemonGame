package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import pokemon.*;
import pokemon.Field.Effect;

public class IsUsefulSecondaryTest {

    private Pokemon user;
    private Pokemon foe;
    private Field field;

    @Before
    public void setUp() {
        // minimal Pokemon setup
        field = new Field();
        Pokemon.field = field;
        user = new Pokemon(1, 5, false, true); // the AI's Pokemon
        foe = new Pokemon(7, 5, true, false); // the "player's" Pokemon
    }

    @Test
    public void testStatusMovePoisonsFoe() {
        Move toxic = Move.TOXIC;
        foe.status = Status.HEALTHY;

        int use = user.isUsefulEffect(foe, toxic, false, field, 10);

        assertTrue("Toxic should be use == 1 when foe is healthy", use == 1);
    }

    @Test
    public void testSecondaryBlockedByCovertCloak() {
        Move flamethrower = Move.FLAMETHROWER; // has burn chance
        foe.item = Item.COVERT_CLOAK;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertFalse("Flamethrower burn blocked by Covert Cloak", use == 1);
    }

    @Test
    public void testShieldDustBlocks() {
        Move flamethrower = Move.FLAMETHROWER;
        foe.ability = Ability.SHIELD_DUST;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertFalse("Flamethrower burn blocked by Shield Dust", use == 1);
    }
    
    @Test
    public void testFlamethrower() {
        Move flamethrower = Move.FLAMETHROWER;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertTrue("Flamethrower should be use == 1", use == 1);
    }

    @Test
    public void testSereneGraceDoublesChance() {
        Move flamethrower = Move.FLAMETHROWER;
        user.ability = Ability.SERENE_GRACE;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertTrue("Serene Grace should allow Flamethrower to be use == 1", use == 1);
    }

    @Test
    public void testFoeImmuneBlocksSecondary() {
        Move thunderbolt = Move.THUNDERBOLT;
        foe.type1 = PType.GROUND; // immune

        int use = user.isUsefulEffect(foe, thunderbolt, false, field, 0);

        assertFalse("Thunderbolt vs Ground immune foe should be not use == 1", use == 1);
    }

    @Test
    public void testRapidSpinRemovesHazards() {
        field.setHazard(user.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS)); // fake hazard on user’s side

        Move spin = Move.RAPID_SPIN;
        int use = user.isUsefulEffect(foe, spin, false, field, 10);

        assertTrue("Rapid Spin should be use == 1 when hazards exist", use == 1);
    }
    
    @Test
    public void testQuiverDance() {
        Move spin = Move.QUIVER_DANCE;
        int use = user.isUsefulEffect(foe, spin, false, field, 0);

        assertTrue("Quiver Dance should be use == 1 if would give a boost", use == 1);
    }

    @Test
    public void testKnockOffRemovesItem() {
        foe.item = Item.LEFTOVERS;

        Move knockOff = Move.KNOCK_OFF;
        int use = user.isUsefulEffect(foe, knockOff, false, field, 20);

        assertTrue("Knock Off should be use == 1 if foe has item", use == 1);
    }

    @Test
    public void testBellyDrumHpDropNotUseful() {
        user.currentHP = 1;
        Move drum = Move.BELLY_DRUM;

        int use = user.isUsefulEffect(foe, drum, false, field, 10);

        assertFalse("Belly Drum self-HP loss should not count as use == 1", use == 1);
    }
    
    @Test
    public void testRainDance() {
        Move spin = Move.RAIN_DANCE;
        int use = user.isUsefulEffect(foe, spin, false, field, 10);

        assertTrue("Rain Dance should be use == 1 on no weather", use == 1);
    }
    
    @Test
    public void testMirrorArmor() {
    	foe.ability = Ability.MIRROR_ARMOR;
    	int use = user.isUsefulEffect(foe, Move.BABY$DOLL_EYES, true, field, 0);
    	
    	assertFalse("Baby Doll Eyes shouldn't be use == 1 on Mirror Armor", use == 1);
    }
    
    @Test
    public void testScreens() {
    	int use1 = user.isUsefulEffect(foe, Move.REFLECT, false, field, 0);
    	int use2 = user.isUsefulEffect(foe, Move.LIGHT_SCREEN, false, field, 0);
    	
    	assertTrue("Reflect and Light Screen should be use == 1", use1 == 1 && use2 == 1);
    }
    
    @Test
    public void testPsychicFangsBreakScreens() {
        foe.getFieldEffects().add(field.new FieldEffect(Effect.LIGHT_SCREEN)); // screen on foe's side

        Move fangs = Move.PSYCHIC_FANGS;
        int use = user.isUsefulEffect(foe, fangs, false, field, 30);

        assertTrue("Psychic Fangs should be use == 1 when screens exist", use == 1);
    }
    
    @Test
    public void testWhirlwind() {
    	Pokemon foe2 = new Pokemon(10, 5, true, false);
    	Pokemon[] team = new Pokemon[] {foe, foe2};
    	foe2.trainer = new Trainer("test", team, 0);
    	int use = user.isUsefulEffect(foe, Move.WHIRLWIND, false, field, 0);
    	
    	assertTrue("Whirlwind should be use == 1 if foe has backup team members", use == 1);
    }
    
    @Test
    public void testHazardsAgainstMagicBounce() {
    	foe.ability = Ability.MAGIC_BOUNCE;
    	int use = user.isUsefulEffect(foe, Move.STEALTH_ROCK, false, field, 0);
    	
    	assertFalse("Hazards shouldn't be use == 1 if foe has Magic Bounce", use == 1);
    }
}
