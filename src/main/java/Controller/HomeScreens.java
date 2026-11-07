package Controller;

import View.HomeScreen;

/**
 * Connects the home screen to navigation.
 */
public final class HomeScreens {

    private HomeScreens() {
    }

    public static void register(AppController app) {
        HomeScreen home = new HomeScreen();
        home.setOnNewGame(() -> app.show(ScreenId.SETUP));
        app.register(ScreenId.HOME, home);
    }
}
