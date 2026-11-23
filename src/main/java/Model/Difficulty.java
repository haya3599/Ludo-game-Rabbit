package Model;

/** Board difficulty selected before a match. */
public enum Difficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    public boolean isBoardAvailable() {
        return this != HARD;
    }

    public String getDisplayName() {
        return displayName;
    }
}
