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

        boolean useful = user.isUsefulEffect(foe, toxic, field, 10);

        assertTrue("Toxic should be useful when foe is healthy", useful);
    }

    @Test
    public void testSecondaryBlockedByCovertCloak() {
        Move flamethrower = Move.FLAMETHROWER; // has burn chance
        foe.item = Item.COVERT_CLOAK;

        boolean useful = user.isUsefulEffect(foe, flamethrower, field, 50);

        assertFalse("Flamethrower burn blocked by Covert Cloak", useful);
    }

    @Test
    public void testShieldDustBlocks() {
        Move flamethrower = Move.FLAMETHROWER;
        foe.ability = Ability.SHIELD_DUST;

        boolean useful = user.isUsefulEffect(foe, flamethrower, field, 50);

        assertFalse("Flamethrower burn blocked by Shield Dust", useful);
    }
    
    @Test
    public void testFlamethrower() {
        Move flamethrower = Move.FLAMETHROWER;

        boolean useful = user.isUsefulEffect(foe, flamethrower, field, 50);

        assertTrue("Flamethrower should be useful", useful);
    }

    @Test
    public void testSereneGraceDoublesChance() {
        Move flamethrower = Move.FLAMETHROWER;
        user.ability = Ability.SERENE_GRACE;

        boolean useful = user.isUsefulEffect(foe, flamethrower, field, 50);

        assertTrue("Serene Grace should allow Flamethrower to be useful", useful);
    }

    @Test
    public void testFoeImmuneBlocksSecondary() {
        Move thunderbolt = Move.THUNDERBOLT;
        foe.type1 = PType.GROUND; // immune

        boolean useful = user.isUsefulEffect(foe, thunderbolt, field, 0);

        assertFalse("Thunderbolt vs Ground immune foe should be not useful", useful);
    }

    @Test
    public void testRapidSpinRemovesHazards() {
        field.setHazard(user.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS)); // fake hazard on user’s side

        Move spin = Move.RAPID_SPIN;
        boolean useful = user.isUsefulEffect(foe, spin, field, 10);

        assertTrue("Rapid Spin should be useful when hazards exist", useful);
    }
    
    @Test
    public void testQuiverDance() {
        Move spin = Move.QUIVER_DANCE;
        boolean useful = user.isUsefulEffect(foe, spin, field, 0);

        assertTrue("Quiver Dance should be useful if would give a boost", useful);
    }

    @Test
    public void testKnockOffRemovesItem() {
        foe.item = Item.LEFTOVERS;

        Move knockOff = Move.KNOCK_OFF;
        boolean useful = user.isUsefulEffect(foe, knockOff, field, 20);

        assertTrue("Knock Off should be useful if foe has item", useful);
    }

    @Test
    public void testBellyDrumHpDropNotUseful() {
        user.currentHP = 1;
        Move drum = Move.BELLY_DRUM;

        boolean useful = user.isUsefulEffect(foe, drum, field, 10);

        assertFalse("Belly Drum self-HP loss should not count as useful", useful);
    }
    
    @Test
    public void testRainDance() {
        Move spin = Move.RAIN_DANCE;
        boolean useful = user.isUsefulEffect(foe, spin, field, 10);

        assertTrue("Rain Dance should be useful on no weather", useful);
    }
}
