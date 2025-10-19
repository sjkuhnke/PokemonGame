package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pokemon.*;
import pokemon.Field.Effect;
import util.Pair;

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

        assertEquals(1, use);
    }

    @Test
    public void testSecondaryBlockedByCovertCloak() {
        Move flamethrower = Move.FLAMETHROWER; // has burn chance
        foe.item = Item.COVERT_CLOAK;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertEquals(0, use);
    }

    @Test
    public void testShieldDustBlocks() {
        Move flamethrower = Move.FLAMETHROWER;
        foe.ability = Ability.SHIELD_DUST;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertEquals(0, use);
    }
    
    @Test
    public void testFlamethrower() {
        Move flamethrower = Move.FLAMETHROWER;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertEquals(1, use);
    }
    
    @Test
    public void testMagicReflectOnFirstTurn() {
    	int use = user.isUsefulEffect(foe, Move.MAGIC_REFLECT, true, field, 0);
    	assertEquals(0, use);
    }

    @Test
    public void testSereneGraceDoublesChance() {
        Move flamethrower = Move.FLAMETHROWER;
        user.ability = Ability.SERENE_GRACE;

        int use = user.isUsefulEffect(foe, flamethrower, false, field, 50);

        assertEquals(1, use);
    }

    @Test
    public void testFoeImmuneBlocksSecondary() {
        Move thunderbolt = Move.THUNDERBOLT;
        foe.type1 = PType.GROUND; // immune

        int use = user.isUsefulEffect(foe, thunderbolt, false, field, 0);

       	assertEquals(0, use);
    }

    @Test
    public void testRapidSpinRemovesHazards() {
        field.setHazard(user.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS)); // fake hazard on user’s side

        Move spin = Move.RAPID_SPIN;
        int use = user.isUsefulEffect(foe, spin, false, field, 10);

        assertEquals(1, use);
    }
    
    @Test
    public void testQuiverDance() {
        Move spin = Move.QUIVER_DANCE;
        int use = user.isUsefulEffect(foe, spin, false, field, 0);

       	assertEquals(1, use);
    }

    @Test
    public void testKnockOffRemovesItem() {
        foe.item = Item.LEFTOVERS;

        Move knockOff = Move.KNOCK_OFF;
        int use = user.isUsefulEffect(foe, knockOff, false, field, 20);

        assertEquals(1, use);
        
        foe.item = null;
        use = user.isUsefulEffect(foe, knockOff, false, field, 15);
        
        assertEquals(0, use);
    }

    @Test
    public void testBellyDrumHpDropNotUseful() {
        user.currentHP = 1;
        Move drum = Move.BELLY_DRUM;

        int use = user.isUsefulEffect(foe, drum, false, field, 10);

        assertEquals(0, use);
        
        user.currentHP = user.getStat(0);
        use = user.isUsefulEffect(foe, drum, false, field, 10);

        assertEquals(1, use);
    }
    
    @Test
    public void testRainDance() {
        Move spin = Move.RAIN_DANCE;
        int use = user.isUsefulEffect(foe, spin, false, field, 10);

        assertEquals(1, use);
    }
    
    @Test
    public void testMirrorArmor() {
    	foe.ability = Ability.MIRROR_ARMOR;
    	int use = user.isUsefulEffect(foe, Move.BABY$DOLL_EYES, true, field, 0);
    	
    	assertEquals(0, use);
    }
    
    @Test
    public void testScreens() {
    	int use1 = user.isUsefulEffect(foe, Move.REFLECT, false, field, 0);
    	int use2 = user.isUsefulEffect(foe, Move.LIGHT_SCREEN, false, field, 0);
    	
    	assertEquals(use1, 1);
    	assertEquals(use2, 1);
    }
    
    @Test
    public void testPsychicFangsBreakScreens() {
        foe.getFieldEffects().add(field.new FieldEffect(Effect.LIGHT_SCREEN)); // screen on foe's side

        Move fangs = Move.PSYCHIC_FANGS;
        int use = user.isUsefulEffect(foe, fangs, false, field, 30);

        assertEquals(1, use);
    }
    
    @Test
    public void testWhirlwind() {
    	Pokemon foe2 = new Pokemon(10, 5, true, false);
    	Pokemon[] team = new Pokemon[] {foe, foe2};
    	foe2.trainer = new Trainer("test", team, 0);
    	int use = user.isUsefulEffect(foe, Move.WHIRLWIND, false, field, 0);
    	
    	assertEquals(1, use);
    }
    
    @Test
    public void testTrickWithFoeNegativeItem() {
    	foe.item = Item.CHOICE_BAND;
    	int use = user.isUsefulEffect(foe, Move.TRICK, false, field, 0);
    	
    	assertEquals(0, use);
    }
    
    @Test
    public void testRecoilMoves() {
    	user.currentHP = 1;
    	int score = user.scoreMove(Move.FLARE_BLITZ, foe, field, false, Move.SCRATCH, new Pair<Integer, Double>(4, 30.0));
    	
    	assertTrue("Flare Blitz should have a positive score", score > 0);
    }
    
    @Test
    public void testHealingWish() {
    	int use = user.isUsefulEffect(foe, Move.HEALING_WISH, false, field, 0);
    	
    	assertEquals(0, use);
    }
    
    @Test
    public void testAromatherapy() {
    	int use = user.isUsefulEffect(foe, Move.AROMATHERAPY, false, field, 0);
    	
    	assertEquals(0, use);
    }
}
