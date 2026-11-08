package Model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceTest {

    @Test
    void rollsStayBetweenOneAndSix() {
        Dice dice = Dice.random(new Random(7));
        for (int attempt = 0; attempt < 300; attempt++) {
            int value = dice.roll();
            assertTrue(value >= 1 && value <= 6, "roll was " + value);
        }
    }

    @Test
    void scriptedRollsAreDeterministic() {
        Dice dice = Dice.scripted(1, 6, 3);
        assertEquals(1, dice.roll());
        assertEquals(6, dice.roll());
        assertEquals(3, dice.roll());
    }

    @Test
    void valuesOutsideTheDieAreRejected() {
        Dice dice = Dice.scripted(0);
        assertThrows(IllegalStateException.class, dice::roll);
    }
    @Test
void valuesAboveSixAreRejected() {
    Dice dice = Dice.scripted(7);
    assertThrows(IllegalStateException.class, dice::roll);
}
}
