package Model;

import java.util.Random;

public final class BoostDiceCreator extends DiceCreator {

    @Override
    public BoostDice createDice() {
        return new BoostDice(new Random());
    }
}
