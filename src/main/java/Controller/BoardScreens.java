package Controller;

import Model.GameConfig;
import Model.MatchSetup;
import Model.Player;
import View.GameScreen;

import java.util.List;

/**
 * Opens the Easy board after a valid setup. Piece movement is connected by the
 * dice-movement feature; this screen already draws whatever positions the model holds.
 */
public final class BoardScreens {

    private BoardScreens() {
    }

    public static void register(AppController app) {
        app.setGameStartHandler(config -> open(app, config));
    }

    private static void open(AppController app, GameConfig config) {
        List<Player> players = MatchSetup.createPlayers(config);
        GameScreen screen = new GameScreen(players);
        screen.setMessage("The board is ready. Rolling and movement are connected in the dice-movement feature.");
        screen.setOnRoll(() -> screen.setMessage(
                "The Roll Dice button is on the board. Movement rules are added in the next feature."));
        screen.setOnLeave(() -> app.show(ScreenId.HOME));
        app.register(ScreenId.BOARD, screen);
        app.show(ScreenId.BOARD);
    }
}
