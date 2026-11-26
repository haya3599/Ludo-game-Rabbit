package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Game state and turn order. Players act in the order they were added.
 * Regular sixes can grant another roll. The third consecutive six is canceled.
 * Finished players are skipped; play ends once only one player remains unfinished.
 */
public final class Game {

    private final List<Player> players;
    private final GameDie regularDie;
    private final GameDie multiplierDie;
    private final BoardGeometry boardGeometry;
    private final List<Player> finishOrder = new ArrayList<>();
    private boolean over;
    private int currentIndex;
    private int turnNumber = 1;
    private DiceRoll lastDiceRoll;
    private boolean movePending;
    private int consecutiveSixes;

    private Game(List<Player> players, GameDie regularDie, GameDie multiplierDie) {
        this.players = List.copyOf(players);
        this.boardGeometry = players.get(0).getBoardGeometry();
        if (regularDie == null
                || (boardGeometry.getDifficulty().getMultiplierDieName() != null) != (multiplierDie != null)) {
            throw new IllegalArgumentException("Dice must match the game difficulty.");
        }
        this.regularDie = regularDie;
        this.multiplierDie = multiplierDie;
    }

    public static Game start(GameConfig config) {
        return start(config, new RegularDiceCreator().createDice());
    }

    /** Starts a game with the supplied Regular die and the difficulty's multiplier die. */
    public static Game start(GameConfig config, Dice dice) {
        if (dice == null) {
            throw new IllegalArgumentException("Dice are required.");
        }
        List<Player> players = MatchSetup.createPlayers(config);
        return new Game(players, dice, config.getDifficultyLevel().createMultiplierDie());
    }

    static Game start(GameConfig config, GameDie regularDie, GameDie multiplierDie) {
        return new Game(MatchSetup.createPlayers(config), regularDie, multiplierDie);
    }

    public BoardGeometry getBoardGeometry() {
        return boardGeometry;
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
        return lastDiceRoll == null ? 0 : lastDiceRoll.regularValue();
    }

    public DiceRoll getLastDiceRoll() {
        return lastDiceRoll;
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
        int regularValue = regularDie.roll();
        int multiplierValue = multiplierDie == null ? 0 : multiplierDie.roll();
        lastDiceRoll = new DiceRoll(regularValue, multiplierValue);
        consecutiveSixes = regularValue == 6 ? consecutiveSixes + 1 : 0;
        if (consecutiveSixes == 3) {
            return passTurn("Third consecutive six. This roll is canceled.");
        }

        boolean zeroSteps = lastDiceRoll.movementSteps() == 0;
        if (legalPieces(lastDiceRoll).isEmpty()) {
            if (zeroSteps && regularValue == 6) {
                return RollOutcome.noMove(lastDiceRoll, "Zero steps. Roll again for the Regular six.");
            }
            return passTurn(zeroSteps ? "Zero steps." : "No legal move for " + getCurrentPlayer().getName() + ".");
        }
        movePending = true;
        return RollOutcome.pending(lastDiceRoll, zeroSteps
                ? "Zero steps on the board. Select a castle piece to enter."
                : "Select one of your highlighted pieces.");
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
        if (!MovementRules.isLegal(piece, lastDiceRoll)) {
            return ActionResult.ILLEGAL_MOVE;
        }

        MovementRules.apply(piece, lastDiceRoll);
        captureOpponent(piece);
        movePending = false;

        Player player = getCurrentPlayer();
        if (player.isFinished()) {
            finishOrder.add(player);
            over = finishOrder.size() == players.size() - 1;
        }
        if (!over && (lastDiceRoll.regularValue() != 6 || player.isFinished())) {
            advanceTurn();
        }
        return ActionResult.OK;
    }

    private void captureOpponent(Piece moved) {
        if (moved.getLocation() != PieceLocation.TRACK
                || boardGeometry.isProtectedSquare(moved.getTrackIndex())) {
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
        return legalPieces(lastDiceRoll);
    }

    private List<Piece> legalPieces(DiceRoll roll) {
        List<Piece> legal = new ArrayList<>();
        for (Piece piece : getCurrentPlayer().getPieces()) {
            if (MovementRules.isLegal(piece, roll)) {
                legal.add(piece);
            }
        }
        return List.copyOf(legal);
    }

    private RollOutcome passTurn(String reason) {
        advanceTurn();
        return RollOutcome.noMove(lastDiceRoll, reason + " Turn passes to " + getCurrentPlayer().getName() + ".");
    }

    private void advanceTurn() {
        consecutiveSixes = 0;
        do {
            currentIndex = (currentIndex + 1) % players.size();
        } while (getCurrentPlayer().isFinished());
        turnNumber++;
    }
}
