package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Easy-game state and turn order.
 * Players act in the order they were added.
 * A successful move after rolling 6 grants an extra roll.
 * A third consecutive 6 ends the turn immediately, before any move.
 * After a successful non-six move, the turn passes.
 */
public final class Game {

    private final List<Player> players;
    private final Dice dice;
    private int currentIndex;
    private int turnNumber = 1;
    private int lastRoll;
    private boolean movePending;
    private int pendingRoll;
    private int consecutiveSixes;

    private Game(List<Player> players, Dice dice) {
        this.players = List.copyOf(players);
        this.dice = dice;
    }

    public static Game start(GameConfig config, Dice dice) {
        if (dice == null) {
            throw new IllegalArgumentException("Dice are required.");
        }
        return new Game(MatchSetup.createPlayers(config), dice);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentIndex);
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public boolean isMovePending() {
        return movePending;
    }

    public RollOutcome roll() {
        if (movePending) {
            return RollOutcome.rejected("Finish the current move before rolling again.");
        }
        int value = dice.roll();
        lastRoll = value;
        consecutiveSixes = value == 6 ? consecutiveSixes + 1 : 0;

        if (consecutiveSixes == 3) {
            advanceTurn();
            return RollOutcome.passed(value,
                    "Third consecutive six. Turn passes to " + getCurrentPlayer().getName() + ".");
        }

        if (legalPieces(value).isEmpty()) {
            String waiting = getCurrentPlayer().getName();
            advanceTurn();
            return RollOutcome.passed(value,
                    "No legal move for " + waiting + ". Turn passes to " + getCurrentPlayer().getName() + ".");
        }
        movePending = true;
        pendingRoll = value;
        return RollOutcome.pending(value, "Select one of your highlighted pieces.");
    }

    public ActionResult move(Piece piece) {
        if (!movePending) {
            return ActionResult.NO_PENDING_MOVE;
        }
        if (piece == null || piece.getOwner() != getCurrentPlayer()) {
            return ActionResult.NOT_YOUR_PIECE;
        }
        if (!MovementRules.isLegal(piece, pendingRoll)) {
            return ActionResult.ILLEGAL_MOVE;
        }
        MovementRules.apply(piece, pendingRoll);
        movePending = false;
        if (pendingRoll != 6) {
            advanceTurn();
        }
        return ActionResult.OK;
    }

    public List<Piece> getLegalPieces() {
        if (!movePending) {
            return List.of();
        }
        return legalPieces(pendingRoll);
    }

    private List<Piece> legalPieces(int diceValue) {
        List<Piece> legal = new ArrayList<>();
        for (Piece piece : getCurrentPlayer().getPieces()) {
            if (MovementRules.isLegal(piece, diceValue)) {
                legal.add(piece);
            }
        }
        return List.copyOf(legal);
    }

    private void advanceTurn() {
        currentIndex = (currentIndex + 1) % players.size();
        turnNumber++;
        consecutiveSixes = 0;
    }
}
