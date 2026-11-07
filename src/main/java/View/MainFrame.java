package View;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

/**
 * The single application window. Screens are swapped with a card layout.
 */
public final class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    private final Map<String, JComponent> screens = new HashMap<>();

    public MainFrame() {
        super("Ludo Educational Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(container);
        setMinimumSize(new Dimension(1020, 740));
        setSize(1180, 820);
        setLocationRelativeTo(null);
    }

    public void addOrReplace(String id, JComponent screen) {
        JComponent previous = screens.put(id, screen);
        if (previous != null) {
            container.remove(previous);
        }
        container.add(screen, id);
        container.revalidate();
        container.repaint();
    }

    public boolean hasScreen(String id) {
        return screens.containsKey(id);
    }

    public void showScreen(String id) {
        JComponent selected = screens.get(id);
        if (selected == null) {
            return;
        }
        for (JComponent screen : screens.values()) {
            screen.setVisible(screen == selected);
        }
        container.setComponentZOrder(selected, 0);
        cards.show(container, id);
        selected.setVisible(true);
        container.revalidate();
        container.repaint();
    }
}
