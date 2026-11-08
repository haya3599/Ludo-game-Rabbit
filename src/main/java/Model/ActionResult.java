package Model;

/**
 * Result of asking the current player to move a piece.
 */
public enum ActionResult {
    OK("Move completed."),
    NOT_YOUR_PIECE("You can only move your own pieces."),
    ILLEGAL_MOVE("That piece cannot make this move."),
    NO_PENDING_MOVE("Roll the dice before choosing a piece.");

    private final String message;

    ActionResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
