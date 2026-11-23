package Model;

/** Board difficulty selected before a match. */
public enum Difficulty {
    EASY("Easy", null, null),
    MEDIUM("Medium", "Boost", new BoostDiceCreator()),
    HARD("Hard", "UltraBoost", new UltraBoostDiceCreator());

    private final String displayName;
    private final String multiplierDieName;
    private final DiceCreator multiplierCreator;

    Difficulty(String displayName, String multiplierDieName, DiceCreator multiplierCreator) {
        this.displayName = displayName;
        this.multiplierDieName = multiplierDieName;
        this.multiplierCreator = multiplierCreator;
    }

    /** The multiplier die label, or null for Easy. */
    public String getMultiplierDieName() {
        return multiplierDieName;
    }

    GameDie createMultiplierDie() {
        return multiplierCreator == null ? null : multiplierCreator.createDice();
    }

    public boolean isBoardAvailable() {
        return this != HARD;
    }

    public String getDisplayName() {
        return displayName;
    }
}
