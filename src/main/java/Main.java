import Controller.AppController;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Ludo Educational Game.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            installLookAndFeel();
            AppController.launch();
        });
    }

    private static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException exception) {
            // The default look and feel remains usable.
        }
    }
}
