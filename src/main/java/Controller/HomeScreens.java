package Controller;

import View.HomeScreen;
import View.HowToPlayScreen;

import javax.swing.JComponent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Connects the home screen to navigation.
 */
public final class HomeScreens {

    private HomeScreens() {
    }

    public static void register(AppController app) {
        register(app::register, app::show, () -> SetupScreens.register(app));
    }

    static void register(BiConsumer<ScreenId, JComponent> register, Consumer<ScreenId> show,
                         Runnable registerSetup) {
        HomeScreen home = new HomeScreen();
        home.setOnNewGame(() -> {
            registerSetup.run();
            show.accept(ScreenId.SETUP);
        });
        home.setOnHowToPlay(() -> show.accept(ScreenId.HOW_TO_PLAY));
        register.accept(ScreenId.HOME, home);
        register.accept(ScreenId.HOW_TO_PLAY, new HowToPlayScreen(() -> show.accept(ScreenId.HOME)));
    }
}
