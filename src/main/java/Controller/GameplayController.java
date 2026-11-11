package Controller;

import Model.ActionResult;
import Model.Game;
import Model.Piece;
import Model.RollOutcome;
import View.GameScreen;

/**
 * Connects the Roll Dice button and piece clicks to the game rules.
 */
public final class GameplayController {

    private final Game game;
    private final GameScreen screen;

    public GameplayController(Game game, GameScreen screen) {
        this.game = game;
        this.screen = screen;
        screen.setOnRoll(this::roll);
        screen.setOnPieceSelected(this::selectPiece);
        show("Roll the dice.");
    }

    private void roll() {
        RollOutcome outcome = game.roll();
        if (!outcome.isAccepted()) {
            screen.setMessage(outcome.getMessage());
            return;
        }
        show(outcome.getMessage());
    }

    private void selectPiece(Piece piece) {
        ActionResult result = game.move(piece);
        if (result != ActionResult.OK) {
            screen.setMessage(result.getMessage());
            return;
        }

        String playerName = game.getCurrentPlayer().getName();
        if (game.getCurrentPlayer() == piece.getOwner()) {
            show(playerName + " rolled a 6 and can roll again.");
        } else {
            show("Turn passes to " + playerName + ".");
        }
    }
    
    private void show(String message) {
        screen.setTurnNumber(game.getTurnNumber());
        screen.setCurrentPlayer(game.getCurrentPlayer());
        screen.setDiceValue(game.getLastRoll());
        screen.setRollEnabled(!game.isMovePending());
        screen.setHighlights(game.getLegalPieces());
        screen.refreshBoard();
        screen.setMessage(message);
    }
}
