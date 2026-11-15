package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Easy-game state and turn order. Players act in the order they were added.
 * A six grants another roll unless the player has finished. Finished players
 * are skipped; play ends once only one player remains unfinished.
 */
public final class Game {

    private final List<Player> players;
    private final Dice dice;
    private final List<Player> finishOrder = new ArrayList<>();
    private boolean over;
    private int currentIndex;
    private int turnNumber = 1;
    private int lastRoll;
    private boolean movePending;
    private int pendingRoll;

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

    public boolean isOver() {
        return over;
    }

    /** Finish order followed by the remaining player once the game ends. */
    public List<Player> getRankings() {
        List<Player> rankings = new ArrayList<>(finishOrder);
        if (over) {
            for (Player player : players) {
                if (!rankings.contains(player)) rankings.add(player);
            }
        }
        return List.copyOf(rankings);
    }

    public RollOutcome roll() {
        if (over) {
            return RollOutcome.rejected("The game has ended.");
        }
        if (movePending) {
            return RollOutcome.rejected("Finish the current move before rolling again.");
        }
        int value = dice.roll();
        lastRoll = value;
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
        if (over) {
            return ActionResult.GAME_OVER;
        }
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
        captureOpponent(piece);
        movePending = false;

        Player player = getCurrentPlayer();
        if (player.isFinished()) {
            finishOrder.add(player);
            over = finishOrder.size() == players.size() - 1;
        }
        if (!over && (pendingRoll != 6 || player.isFinished())) {
            advanceTurn();
        }
        return ActionResult.OK;
    }

    private void captureOpponent(Piece moved) {
        if (moved.getLocation() != PieceLocation.TRACK
                || BoardGeometry.isProtected(moved.getTrackIndex())) {
            return;
        }
        List<Piece> opponents = new ArrayList<>();
        for (Player player : players) {
            if (player == moved.getOwner()) continue;
            for (Piece piece : player.getPieces()) {
                if (piece.getLocation() == PieceLocation.TRACK
                        && piece.getTrackIndex() == moved.getTrackIndex()) {
                    opponents.add(piece);
                }
            }
        }
        if (opponents.size() == 1) {
            opponents.get(0).returnToCastle();
        }
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
        do {
            currentIndex = (currentIndex + 1) % players.size();
        } while (getCurrentPlayer().isFinished());
        turnNumber++;
    }
}
