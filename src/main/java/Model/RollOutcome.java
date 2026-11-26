package Model;

/**
 * Result of a roll request. A rejected roll does not change the game.
 */
public final class RollOutcome {

    private final boolean accepted;
    private final DiceRoll diceRoll;
    private final boolean movePending;
    private final String message;

    private RollOutcome(boolean accepted, DiceRoll diceRoll, boolean movePending, String message) {
        this.accepted = accepted;
        this.diceRoll = diceRoll;
        this.movePending = movePending;
        this.message = message;
    }

    public static RollOutcome rejected(String message) {
        return new RollOutcome(false, null, false, message);
    }

    public static RollOutcome pending(DiceRoll roll, String message) {
        return new RollOutcome(true, roll, true, message);
    }

    public static RollOutcome noMove(DiceRoll roll, String message) {
        return new RollOutcome(true, roll, false, message);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public int getValue() {
        return diceRoll == null ? 0 : diceRoll.regularValue();
    }

    public DiceRoll getDiceRoll() {
        return diceRoll;
    }

    public boolean isMovePending() {
        return movePending;
    }

    public String getMessage() {
        return message;
    }
}
