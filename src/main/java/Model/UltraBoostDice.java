package Model;

import java.util.Random;

/** Rolls a movement multiplier from 1 to 6. */
public final class UltraBoostDice implements GameDie {

    private final Random random;

    public UltraBoostDice(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("A random source is required.");
        }
        this.random = random;
    }

    @Override
    public int roll() {
        return random.nextInt(6) + 1;
    }
}
