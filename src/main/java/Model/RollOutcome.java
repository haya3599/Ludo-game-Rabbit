package Model;

/**
 * Result of a roll request. A rejected roll does not change the game.
 */
public final class RollOutcome {

    private final boolean accepted;
    private final int value;
    private final boolean movePending;
    private final String message;

    private RollOutcome(boolean accepted, int value, boolean movePending, String message) {
        this.accepted = accepted;
        this.value = value;
        this.movePending = movePending;
        this.message = message;
    }

    public static RollOutcome rejected(String message) {
        return new RollOutcome(false, 0, false, message);
    }

    public static RollOutcome pending(int value, String message) {
        return new RollOutcome(true, value, true, message);
    }

    public static RollOutcome passed(int value, String message) {
        return new RollOutcome(true, value, false, message);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public int getValue() {
        return value;
    }

    public boolean isMovePending() {
        return movePending;
    }

    public String getMessage() {
        return message;
    }
}
