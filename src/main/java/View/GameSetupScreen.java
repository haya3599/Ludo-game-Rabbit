package View;

import Model.GameConfig;
import Model.PlayerColor;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects the player count, names, and colours for an Easy game.
 * Validation is performed by the controller before the game starts.
 */
public final class GameSetupScreen extends JPanel {

    private static final PlayerColor[] DEFAULT_COLORS = {
            PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW
    };

    private final JComboBox<Integer> playerCount = new JComboBox<>(new Integer[]{2, 3, 4});
    private final JTextField[] nameFields = new JTextField[4];
    private final JComboBox<PlayerColor>[] colorChoices = newColorChoices();
    private final JPanel[] rows = new JPanel[4];
    private final JLabel errorLabel = new JLabel(" ");
    private Runnable onBack = () -> { };
    private Runnable onStart = () -> { };

    public GameSetupScreen() {
        setLayout(new GridBagLayout());
        setBackground(UiKit.NAVY);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UiKit.CARD);
        card.setBorder(new EmptyBorder(28, 32, 24, 32));
        card.setPreferredSize(new Dimension(640, 560));

        JLabel title = new JLabel("Game Setup");
        title.setFont(new Font(Font.SERIF, Font.BOLD, 32));
        title.setForeground(UiKit.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Difficulty is fixed to Easy for this iteration.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(new Color(70, 74, 90));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        countRow.setOpaque(false);
        countRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel countLabel = new JLabel("Players");
        countLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        countRow.add(countLabel);
        countRow.add(Box.createHorizontalStrut(12));
        playerCount.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        countRow.add(playerCount);

        JPanel playerRows = new JPanel();
        playerRows.setLayout(new BoxLayout(playerRows, BoxLayout.Y_AXIS));
        playerRows.setOpaque(false);
        playerRows.setAlignmentX(LEFT_ALIGNMENT);
        for (int index = 0; index < 4; index++) {
            rows[index] = playerRow(index);
            playerRows.add(rows[index]);
            playerRows.add(Box.createVerticalStrut(8));
        }

        errorLabel.setForeground(UiKit.DANGER);
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton back = UiKit.button("Back", Color.WHITE, UiKit.INK);
        JButton start = UiKit.button("Start Easy Game", UiKit.GOLD, UiKit.INK);
        back.addActionListener(event -> onBack.run());
        start.addActionListener(event -> onStart.run());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        actions.add(back);
        actions.add(start);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(countRow);
        card.add(Box.createVerticalStrut(14));
        card.add(playerRows);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(actions);
        add(card);

        playerCount.addActionListener(event -> showSelectedRows());
        showSelectedRows();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public GameConfig readConfig() {
        int count = (Integer) playerCount.getSelectedItem();
        List<GameConfig.Slot> slots = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            slots.add(new GameConfig.Slot(
                    nameFields[index].getText(),
                    (PlayerColor) colorChoices[index].getSelectedItem()));
        }
        return new GameConfig(slots);
    }

    public void showError(String message) {
        errorLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
    }

    public void clearError() {
        errorLabel.setText(" ");
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(245, 197, 66, 40));
        g2.fillOval(-40, getHeight() - 180, 240, 240);
        g2.setColor(new Color(46, 125, 50, 50));
        g2.fillOval(getWidth() - 180, -40, 200, 200);
        g2.dispose();
    }

    private void showSelectedRows() {
        int count = (Integer) playerCount.getSelectedItem();
        for (int index = 0; index < rows.length; index++) {
            rows[index].setVisible(index < count);
        }
        clearError();
        revalidate();
        repaint();
    }

    private JPanel playerRow(int index) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel label = new JLabel("Player " + (index + 1));
        label.setPreferredSize(new Dimension(70, 32));
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        nameFields[index] = new JTextField("Player " + (index + 1));
        nameFields[index].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        nameFields[index].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 206, 196)),
                new EmptyBorder(6, 8, 6, 8)));

        colorChoices[index].setSelectedItem(DEFAULT_COLORS[index]);
        colorChoices[index].setPreferredSize(new Dimension(140, 32));
        row.add(label, BorderLayout.WEST);
        row.add(nameFields[index], BorderLayout.CENTER);
        row.add(colorChoices[index], BorderLayout.EAST);
        return row;
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<PlayerColor>[] newColorChoices() {
        JComboBox<PlayerColor>[] choices = new JComboBox[4];
        for (int index = 0; index < choices.length; index++) {
            JComboBox<PlayerColor> combo = new JComboBox<>(PlayerColor.values());
            combo.setRenderer(new ColorRenderer());
            combo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            choices[index] = combo;
        }
        return choices;
    }

    private static final class ColorRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PlayerColor color) {
                label.setText(color.getDisplayName());
                if (!isSelected) {
                    label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                }
            }
            return label;
        }
    }
}
