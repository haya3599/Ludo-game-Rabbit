package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Settings chosen before an Easy game starts. Difficulty is fixed for Iteration 1.
 */
public final class GameConfig {

    public static final String DIFFICULTY = "Easy";

    private final List<Slot> slots;

    public GameConfig(List<Slot> slots) {
        this.slots = new ArrayList<>();
        if (slots != null) {
            this.slots.addAll(slots);
        }
    }

    public String getDifficulty() {
        return DIFFICULTY;
    }

    public List<Slot> getSlots() {
        return Collections.unmodifiableList(new ArrayList<>(slots));  
    }

    public int getPlayerCount() {
        return slots.size();
    }

    /**
     * A name and colour collected from the setup screen. Values may be invalid;
     * {@link GameConfigValidator} decides whether a game may start.
     */
    public static final class Slot {
        private final String name;
        private final PlayerColor color;

        public Slot(String name, PlayerColor color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public PlayerColor getColor() {
            return color;
        }
    }
}
