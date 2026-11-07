package Controller;

import View.MainFrame;
import View.PlaceholderScreen;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * Application coordinator. Feature screens register themselves and the
 * controller decides which screen is visible.
 */
public final class AppController {

    private final MainFrame frame;

    private AppController() {
        this.frame = new MainFrame();
    }

    public static void launch() {
        AppController app = new AppController();
        ScreenRegistry.registerAll(app);
        app.showInitialScreen();
        app.frame.setVisible(true);
    }

    public void register(ScreenId id, JComponent screen) {
        frame.addOrReplace(id.name(), screen);
    }

    public void show(ScreenId id) {
        if (!frame.hasScreen(id.name())) {
            JOptionPane.showMessageDialog(
                    frame,
                    "This screen is not part of the current build.",
                    "Ludo Educational Game",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        frame.showScreen(id.name());
    }

    public MainFrame getFrame() {
        return frame;
    }

    private void showInitialScreen() {
        if (frame.hasScreen(ScreenId.HOME.name())) {
            frame.showScreen(ScreenId.HOME.name());
        } else if (frame.hasScreen(ScreenId.SETUP.name())) {
            frame.showScreen(ScreenId.SETUP.name());
        } else {
            frame.addOrReplace("PLACEHOLDER", new PlaceholderScreen());
            frame.showScreen("PLACEHOLDER");
        }
    }
}
