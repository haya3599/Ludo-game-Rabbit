package Controller;

/**
 * Registers the screens that belong to the current build.
 * Feature branches add their own registration calls here.
 */
public final class ScreenRegistry {

    private ScreenRegistry() {
    }

    public static void registerAll(AppController app) {
        HomeScreens.register(app);
        SetupScreens.register(app);
        BoardScreens.register(app);
    }
}
