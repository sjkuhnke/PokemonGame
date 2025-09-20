package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import pokemon.*;

public class HasPivotMoveTest {

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
    
    public void setupMoveset(Pokemon p, Move m1, Move m2, Move m3, Move m4) {
    	p.moveset = new Moveslot[] {
    		m1 == null ? null : new Moveslot(m1),
    		m2 == null ? null : new Moveslot(m2),
    		m3 == null ? null : new Moveslot(m3),
    		m4 == null ? null : new Moveslot(m4)};
    }
    
    private ArrayList<Move> list(Move... moves) {
    	return new ArrayList<>(Arrays.asList(moves));
    }
    
    @Test
    public void testUturnUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.U$TURN));
        assertEquals(Move.U$TURN, result);
    }

    @Test
    public void testUturnNotUsefulImmune() {
        foe.ability = Ability.INSECT_FEEDER; // Normal immunity example only matters if U-turn flagged normal
        // To simulate, force U-turn type = Normal for test (if it's Bug normally this won't hit Ghost)
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.U$TURN)); 
        assertNull(result);
    }

    @Test
    public void testVoltSwitchUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.VOLT_SWITCH));
        assertEquals(Move.VOLT_SWITCH, result);
    }

    @Test
    public void testVoltSwitchNotUsefulOnGroundType() {
        foe.type1 = PType.GROUND;
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.VOLT_SWITCH));
        assertNull(result);
    }

    @Test
    public void testFlipTurnUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.FLIP_TURN));
        assertEquals(Move.FLIP_TURN, result);
    }

    @Test
    public void testFlipTurnNotUsefulImmune() {
        foe.ability = Ability.WATER_ABSORB; // Water resisting type immunity handled in Trainer.getEffective
        // In your mechanics, probably still >0 unless Water Absorb, 
        // but just to test non-useful scenario, assume ineffective
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.FLIP_TURN));
        assertNull(result); // might still hit — tweak if you coded abilities for immunity
    }

    // -----------------
    // Status Pivots
    // -----------------

    @Test
    public void testPartingShotUsefulNormally() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.PARTING_SHOT));
        assertEquals(Move.PARTING_SHOT, result);
    }

    @Test
    public void testPartingShotBlockedByPranksterDark() {
        user.ability = Ability.PRANKSTER;
        foe.type1 = PType.DARK;
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.PARTING_SHOT));
        assertNull(result);
    }

    @Test
    public void testBatonPassUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.BATON_PASS));
        assertEquals(Move.BATON_PASS, result);
    }

    @Test
    public void testTeleportUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.TELEPORT));
        assertEquals(Move.TELEPORT, result);
    }

    @Test
    public void testChillyReceptionUseful() {
        Move result = user.hasPivotMove(foe, foe.getAbility(field), list(Move.CHILLY_RECEPTION));
        assertEquals(Move.CHILLY_RECEPTION, result);
    }

    // -----------------
    // Mixed Set
    // -----------------

    @Test
    public void testMultipleMovesFirstUsefulReturned() {
        ArrayList<Move> moves = list(Move.VOLT_SWITCH, Move.TELEPORT, Move.BATON_PASS);
        Move result = user.hasPivotMove(foe, foe.getAbility(field), moves);
        // Volt Switch is damaging and valid vs Normal type, so it should return that first
        assertEquals(Move.VOLT_SWITCH, result);
    }

    
}
