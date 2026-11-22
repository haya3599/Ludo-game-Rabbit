package Model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiceCreatorTest {

    @Test
    void regularCreatorMakesTheExistingDiceImplementation() {
        DiceCreator creator = new RegularDiceCreator();
        assertInstanceOf(Dice.class, creator.createDice());
    }

    @Test
    void boostCreatorMakesBoostDice() {
        DiceCreator creator = new BoostDiceCreator();
        assertInstanceOf(BoostDice.class, creator.createDice());
    }

    @Test
    void ultraBoostCreatorMakesUltraBoostDice() {
        DiceCreator creator = new UltraBoostDiceCreator();
        assertInstanceOf(UltraBoostDice.class, creator.createDice());
    }

    @Test
    void regularDiceRollsValuesFromOneToSix() {
        assertRollsCoverRange(Dice.random(new Random(7)), Set.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void boostDiceRollsMultipliersFromOneToThree() {
        assertRollsCoverRange(new BoostDice(new Random(7)), Set.of(1, 2, 3));
    }

    @Test
    void ultraBoostDiceRollsMultipliersFromOneToSix() {
        assertRollsCoverRange(new UltraBoostDice(new Random(7)), Set.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void scriptedDiceKeepsItsSequenceAndExhaustionBehavior() {
        GameDie dice = Dice.scripted(6, 1, 3);

        assertEquals(6, dice.roll());
        assertEquals(1, dice.roll());
        assertEquals(3, dice.roll());
        assertThrows(IllegalStateException.class, dice::roll);
    }

    @Test
    void diceRejectNullRandomSources() {
        assertThrows(IllegalArgumentException.class, () -> Dice.random(null));
        assertThrows(IllegalArgumentException.class, () -> new BoostDice(null));
        assertThrows(IllegalArgumentException.class, () -> new UltraBoostDice(null));
    }

    private static void assertRollsCoverRange(GameDie dice, Set<Integer> expected) {
        Set<Integer> values = new HashSet<>();
        for (int attempt = 0; attempt < 300; attempt++) {
            values.add(dice.roll());
        }
        assertEquals(expected, values);
    }
}
