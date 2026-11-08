package Model;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * A regular six-sided step die. Tests use {@link #scripted(int...)} so results
 * are deterministic. A scripted value outside 1 to 6 is rejected.
 */
public final class Dice {

    private final Random random;
    private final Queue<Integer> script;

    private Dice(Random random, Queue<Integer> script) {
        this.random = random;
        this.script = script;
    }

    public static Dice random() {
        return new Dice(new Random(), null);
    }

    public static Dice random(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("A random source is required.");
        }
        return new Dice(random, null);
    }

    public static Dice scripted(int... values) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int value : values) {
            queue.add(value);
        }
        return new Dice(new Random(0), queue);
    }

    public int roll() {
        int value;
        if (script != null) {
            if (script.isEmpty()) {
                throw new IllegalStateException("No scripted dice values remain.");
            }
            value = script.remove();
        } else {
            value = random.nextInt(6) + 1;
        }
        if (value < 1 || value > 6) {
            throw new IllegalStateException("Dice produced a value outside 1 to 6: " + value);
        }
        return value;
    }
}
