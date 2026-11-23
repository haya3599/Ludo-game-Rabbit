package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Settings chosen before a game starts.
 */
public final class GameConfig {

    public static final String DIFFICULTY = "Easy";

    private final List<Slot> slots;
    private final Difficulty difficulty;

    public GameConfig(List<Slot> slots) {
        this(slots, Difficulty.EASY);
    }

    public GameConfig(List<Slot> slots, Difficulty difficulty) {
        this.difficulty = difficulty;
        this.slots = new ArrayList<>();
        if (slots != null) {
            this.slots.addAll(slots);
        }
    }

    public String getDifficulty() {
        return difficulty == null ? null : difficulty.getDisplayName();
    }

    public Difficulty getDifficultyLevel() {
        return difficulty;
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
