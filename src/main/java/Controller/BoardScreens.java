package Controller;

import Model.Dice;
import Model.Game;
import Model.GameConfig;
import View.GameScreen;

/**
 * Starts an Easy game and connects the board to dice rolling and movement.
 */
public final class BoardScreens {

    private BoardScreens() {
    }

    public static void register(AppController app) {
        app.setGameStartHandler(config -> open(app, config));
    }

    private static void open(AppController app, GameConfig config) {
        Game game = Game.start(config, Dice.random());
        GameScreen screen = new GameScreen(game.getPlayers());
        new GameplayController(game, screen);
        screen.setOnLeave(() -> app.show(ScreenId.HOME));
        app.register(ScreenId.BOARD, screen);
        app.show(ScreenId.BOARD);
    }
}
