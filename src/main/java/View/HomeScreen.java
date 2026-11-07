package View;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

/**
 * Opening screen. New Game is wired by the controller. History and question
 * management stay unavailable until a later iteration.
 */
public final class HomeScreen extends JPanel {

    private final JButton newGameButton = UiKit.button("New Game", UiKit.GOLD, UiKit.INK);
    private final JButton historyButton = UiKit.button("Game History", Color.WHITE, UiKit.INK);
    private final JButton questionsButton = UiKit.button("Question Management", Color.WHITE, UiKit.INK);

    public HomeScreen() {
        setLayout(new GridBagLayout());
        setBackground(UiKit.NAVY);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 250, 242, 235));
        card.setBorder(new EmptyBorder(36, 42, 36, 42));
        card.setPreferredSize(new Dimension(460, 460));

        JLabel eyebrow = label("TEAM RABBIT", Font.SANS_SERIF, Font.BOLD, 13, new Color(112, 48, 96));
        JLabel title = label("LUDO EDUCATIONAL GAME", Font.SERIF, Font.BOLD, 32, UiKit.NAVY);
        JLabel subtitle = label("A colourful introduction to Easy Ludo.", Font.SANS_SERIF, Font.PLAIN, 15, new Color(70, 74, 90));

        configure(newGameButton);
        configure(historyButton);
        configure(questionsButton);
        historyButton.addActionListener(event -> ComingSoon.show(this));
        questionsButton.addActionListener(event -> ComingSoon.show(this));

        card.add(eyebrow);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(newGameButton);
        card.add(Box.createVerticalStrut(12));
        card.add(historyButton);
        card.add(Box.createVerticalStrut(12));
        card.add(questionsButton);
        add(card);
    }

    public void setOnNewGame(Runnable action) {
        newGameButton.addActionListener(event -> action.run());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(245, 197, 66, 50));
        g2.fillOval(-80, -60, 280, 280);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.fillOval(getWidth() - 220, getHeight() - 200, 300, 300);
        g2.setColor(new Color(180, 70, 110, 70));
        g2.fillOval(getWidth() - 140, 30, 160, 160);
        g2.dispose();
    }

    private static void configure(JButton button) {
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
    }

    private static JLabel label(String text, String font, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setFont(new Font(font, style, size));
        label.setForeground(color);
        return label;
    }
}
