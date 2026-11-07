package View;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * Shown only when no feature screen has been registered.
 */
public final class PlaceholderScreen extends JPanel {

    public PlaceholderScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 32, 72));
        JLabel title = new JLabel("LUDO EDUCATIONAL GAME", SwingConstants.CENTER);
        title.setForeground(new Color(245, 197, 66));
        title.setFont(new Font(Font.SERIF, Font.BOLD, 36));
        add(title, BorderLayout.CENTER);
    }
}
