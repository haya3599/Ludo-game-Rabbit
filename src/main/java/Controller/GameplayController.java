
package Controller;

import Model.ActionResult;
import Model.Game;
import Model.Piece;
import Model.Player;
import Model.RollOutcome;
import View.GameScreen;

import javax.swing.Timer;
import java.util.List;

/**
 * Connects the Roll Dice button and piece clicks to the game rules.
 */
public final class GameplayController {

    private final Game game;
    private final GameScreen screen;

    private int displayedTurnNumber = -1;
    private Timer transitionTimer;
    private boolean transitioning = false;

    public GameplayController(Game game, GameScreen screen) {
        this.game = game;
        this.screen = screen;

        screen.setOnRoll(this::roll);
        screen.setOnPieceSelected(this::selectPiece);

        show("Roll the dice.");
    }

    private void roll() {
        if (transitioning) {
            return;
        }

        Player previousPlayer = game.getCurrentPlayer();
        int previousTurn = game.getTurnNumber();

        RollOutcome outcome = game.roll();

        if (!outcome.isAccepted()) {
            screen.setMessage(outcome.getMessage());
            return;
        }

        int rolledValue = game.getLastRoll();

        if (game.getTurnNumber() != previousTurn) {
            delayTransition(
                previousPlayer,
                previousTurn,
                rolledValue,
                outcome.getMessage()
            );
        } else {
            show(outcome.getMessage());
        }
    }

    private void selectPiece(Piece piece) {
        if (transitioning) {
            return;
        }

        Player previousPlayer = game.getCurrentPlayer();
        int previousTurn = game.getTurnNumber();
        int previousRoll = game.getLastRoll();

        ActionResult result = game.move(piece);

        if (result != ActionResult.OK) {
            screen.setMessage(result.getMessage());
            return;
        }

        if (game.getTurnNumber() != previousTurn) {
            delayTransition(
                previousPlayer,
                previousTurn,
                previousRoll,
                "Turn completed."
            );
        } else {
            show("Continue playing.");
        }
    }

    private void delayTransition(
            Player previousPlayer,
            int previousTurn,
            int diceValue,
            String message) {

        transitioning = true;

        if (transitionTimer != null) {
            transitionTimer.stop();
        }

        // Keep the previous player and dice visible together.
        screen.setTurnNumber(previousTurn);
        screen.setCurrentPlayer(previousPlayer);
        screen.setDiceValue(diceValue);
        screen.setMessage(message);

        screen.setRollEnabled(false);
        screen.setHighlights(List.of());
        screen.refreshBoard();

        transitionTimer = new Timer(1000, event -> {
            transitionTimer = null;

            // Update player, turn and dice together.
            displayedTurnNumber = -1;
            show("Roll the dice.");

            transitioning = false;
        });

        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }

    private void show(String message) {
        int currentTurn = game.getTurnNumber();

        boolean turnChanged =
                currentTurn != displayedTurnNumber;

        screen.setTurnNumber(currentTurn);
        screen.setCurrentPlayer(game.getCurrentPlayer());

        if (turnChanged) {
            screen.setDiceValue(0);
        } else {
            screen.setDiceValue(game.getLastRoll());
        }

        displayedTurnNumber = currentTurn;

        screen.setRollEnabled(!game.isMovePending());
        screen.setHighlights(game.getLegalPieces());
        screen.refreshBoard();
        screen.setMessage(message);
    }
}
