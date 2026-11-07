package Controller;

import Model.GameConfigValidator;
import Model.ValidationResult;
import View.GameSetupScreen;

/**
 * Opens the setup screen and refuses to start a game until the settings are valid.
 */
public final class SetupScreens {

    private SetupScreens() {
    }

    public static void register(AppController app) {
        GameSetupScreen screen = new GameSetupScreen();
        screen.setOnBack(() -> app.show(ScreenId.HOME));
        screen.setOnStart(() -> startIfValid(app, screen));
        app.register(ScreenId.SETUP, screen);
    }

    private static void startIfValid(AppController app, GameSetupScreen screen) {
        var config = screen.readConfig();
        ValidationResult result = GameConfigValidator.validate(config);
        if (!result.isValid()) {
            screen.showError(result.getMessage());
            return;
        }
        screen.clearError();
        app.onGameConfigured(config);
    }
}
