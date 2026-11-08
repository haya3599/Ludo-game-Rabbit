package View;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Message used for screens that are scheduled after Iteration 1.
 */
public final class ComingSoon {

    public static final String MESSAGE = "Coming in a future iteration";

    private ComingSoon() {
    }

    public static void show(Component parent) {
        JOptionPane.showMessageDialog(
                parent,
                MESSAGE,
                "Ludo Educational Game",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
