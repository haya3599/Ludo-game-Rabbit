package Model;

import java.util.Random;

public final class UltraBoostDiceCreator extends DiceCreator {

    @Override
    public UltraBoostDice createDice() {
        return new UltraBoostDice(new Random());
    }
}
