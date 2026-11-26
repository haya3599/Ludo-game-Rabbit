package Model;

import java.util.Random;

/** Rolls a movement multiplier from 1 to 3. */
public final class BoostDice implements GameDie {

    private final Random random;

    public BoostDice(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("A random source is required.");
        }
        this.random = random;
    }

    @Override
    public int roll() {
        return random.nextInt(3) + 1;
    }
}
