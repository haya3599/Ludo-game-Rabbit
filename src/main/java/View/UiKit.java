package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Shared colours and button styling for the Swing screens.
 */
public final class UiKit {

    public static final Color NAVY = new Color(24, 32, 72);
    public static final Color PLUM = new Color(112, 48, 96);
    public static final Color GOLD = new Color(245, 197, 66);
    public static final Color CREAM = new Color(255, 248, 235);
    public static final Color INK = new Color(32, 36, 48);
    public static final Color DANGER = new Color(164, 36, 46);
    public static final Color CARD = new Color(255, 252, 247);

    private UiKit() {
    }

    public static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 1, true),
                new EmptyBorder(12, 18, 12, 18)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }
    
public static JButton royalButton(
        String text,
        Color background,
        Color foreground,
        boolean primary) {

    JButton button = new JButton(text);

    button.setFont(new Font(
            Font.SANS_SERIF,
            Font.BOLD,
            primary ? 17 : 14
    ));

    button.setBackground(background);
    button.setForeground(foreground);
    button.setFocusPainted(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setOpaque(false);

    button.setBorder(new EmptyBorder(
            primary ? 13 : 10,
            primary ? 28 : 18,
            primary ? 13 : 10,
            primary ? 28 : 18
    ));

    button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    );

    button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

    button.addChangeListener(event -> button.repaint());

    return button;
}

}
