package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pokemon.*;
import pokemon.Field.Effect;
import pokemon.Pokemon.EffectAnalysisResult;
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

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, toxic, false, field, 10, null);

        assertEquals(1, use.targetsChecked);
    }

    @Test
    public void testSecondaryBlockedByCovertCloak() {
        Move flamethrower = Move.FLAMETHROWER; // has burn chance
        foe.item = Item.COVERT_CLOAK;

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, flamethrower, false, field, 50, null);

        assertEquals(0, use.targetsChecked);
    }

    @Test
    public void testShieldDustBlocks() {
        Move flamethrower = Move.FLAMETHROWER;
        foe.ability = Ability.SHIELD_DUST;

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, flamethrower, false, field, 50, null);

        assertEquals(0, use.targetsChecked);
    }
    
    @Test
    public void testFlamethrower() {
        Move flamethrower = Move.FLAMETHROWER;

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, flamethrower, false, field, 50, null);

        assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testMagicReflectOnFirstTurn() {
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.MAGIC_REFLECT, true, field, 0, null);
    	assertEquals(0, use.targetsChecked);
    }

    @Test
    public void testSereneGraceDoublesChance() {
        Move flamethrower = Move.FLAMETHROWER;
        user.ability = Ability.SERENE_GRACE;

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, flamethrower, false, field, 50, null);

        assertEquals(1, use.targetsChecked);
    }

    @Test
    public void testFoeImmuneBlocksSecondary() {
        Move thunderbolt = Move.THUNDERBOLT;
        foe.type1 = PType.GROUND; // immune

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, thunderbolt, false, field, 0, null);

       	assertEquals(0, use.targetsChecked);
    }

    @Test
    public void testRapidSpinRemovesHazards() {
        field.setHazard(user.getFieldEffects(), field.new FieldEffect(Effect.STEALTH_ROCKS)); // fake hazard on user’s side

        Move spin = Move.RAPID_SPIN;
        EffectAnalysisResult use = user.analyzeMoveEffect(foe, spin, false, field, 10, null);

        assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testQuiverDance() {
        Move spin = Move.QUIVER_DANCE;
        EffectAnalysisResult use = user.analyzeMoveEffect(foe, spin, false, field, 0, null);

       	assertEquals(1, use.targetsChecked);
    }

    @Test
    public void testKnockOffRemovesItem() {
        foe.item = Item.LEFTOVERS;

        Move knockOff = Move.KNOCK_OFF;
        EffectAnalysisResult use = user.analyzeMoveEffect(foe, knockOff, false, field, 20, null);

        assertEquals(1, use.targetsChecked);
        
        foe.item = null;
        use = user.analyzeMoveEffect(foe, knockOff, false, field, 15, null);
        
        assertEquals(0, use.targetsChecked);
    }

    @Test
    public void testBellyDrumHpDropNotUseful() {
        user.currentHP = 1;
        Move drum = Move.BELLY_DRUM;

        EffectAnalysisResult use = user.analyzeMoveEffect(foe, drum, false, field, 10, null);

        assertEquals(0, use.targetsChecked);
        
        user.currentHP = user.getStat(0);
        use = user.analyzeMoveEffect(foe, drum, false, field, 10, null);

        assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testRainDance() {
        Move spin = Move.RAIN_DANCE;
        EffectAnalysisResult use = user.analyzeMoveEffect(foe, spin, false, field, 10, null);

        assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testMirrorArmor() {
    	foe.ability = Ability.MIRROR_ARMOR;
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.BABY$DOLL_EYES, true, field, 0, null);
    	
    	assertEquals(0, use.targetsChecked);
    }
    
    @Test
    public void testScreens() {
    	EffectAnalysisResult use1 = user.analyzeMoveEffect(foe, Move.REFLECT, false, field, 0, null);
    	EffectAnalysisResult use2 = user.analyzeMoveEffect(foe, Move.LIGHT_SCREEN, false, field, 0, null);
    	
    	assertEquals(use1, 1);
    	assertEquals(use2, 1);
    }
    
    @Test
    public void testPsychicFangsBreakScreens() {
        foe.getFieldEffects().add(field.new FieldEffect(Effect.LIGHT_SCREEN)); // screen on foe's side

        Move fangs = Move.PSYCHIC_FANGS;
        EffectAnalysisResult use = user.analyzeMoveEffect(foe, fangs, false, field, 30, null);

        assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testWhirlwind() {
    	Pokemon foe2 = new Pokemon(10, 5, true, false);
    	Pokemon[] team = new Pokemon[] {foe, foe2};
    	foe2.trainer = new Trainer("test", team, 0);
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.WHIRLWIND, false, field, 0, null);
    	
    	assertEquals(1, use.targetsChecked);
    }
    
    @Test
    public void testTrickWithFoeNegativeItem() {
    	foe.item = Item.CHOICE_BAND;
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.TRICK, false, field, 0, null);
    	
    	assertEquals(0, use.targetsChecked);
    }
    
    @Test
    public void testRecoilMoves() {
    	user.currentHP = 1;
    	int score = user.scoreMove(Move.FLARE_BLITZ, foe, field, false, Move.SCRATCH, new Pair<Integer, Double>(4, 30.0), null);
    	
    	assertTrue("Flare Blitz should have a positive score", score > 0);
    }
    
    @Test
    public void testHealingWish() {
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.HEALING_WISH, false, field, 0, null);
    	
    	assertEquals(0, use.targetsChecked); // not useful because no teammate to give wish to
    }
    
    @Test
    public void testAromatherapy() {
    	EffectAnalysisResult use = user.analyzeMoveEffect(foe, Move.AROMATHERAPY, false, field, 0, null);
    	
    	assertEquals(0, use.targetsChecked);
    }
}
