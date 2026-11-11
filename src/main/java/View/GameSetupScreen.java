
package View;

import Model.GameConfig;
import Model.PlayerColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class GameSetupScreen extends JPanel {

    private static final Color NAVY = new Color(24, 32, 72);
    private static final Color IVORY = new Color(255, 250, 240);
    private static final Color GOLD = new Color(211, 169, 83);
    private static final Color SOFT_GOLD = new Color(245, 225, 178);
    private static final Color MUTED = new Color(185, 190, 207);

    private static final PlayerColor[] COLORS = {
            PlayerColor.RED,
            PlayerColor.BLUE,
            PlayerColor.GREEN,
            PlayerColor.YELLOW
    };

    private final JTextField[] nameFields = new JTextField[4];
    private final PlayerColor[] selectedColors = COLORS.clone();
    private final JToggleButton[][] colorButtons =
            new JToggleButton[4][4];
    private final JPanel[] playerRows = new JPanel[4];
    private final JButton[] countButtons = new JButton[3];

    private final JLabel errorLabel = new JLabel(" ");

    private int playerCount = 2;
    private Runnable onBack = () -> {};
    private Runnable onStart = () -> {};

    public GameSetupScreen() {
        setLayout(new GridBagLayout());
        setBackground(NAVY);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(14, 15, 14, 15));
        content.setPreferredSize(new Dimension(900, 740));

        // HEADER
        JLabel edition = new JLabel(
                "L U D O   /   T H E   R O Y A L   E D I T I O N"
        );
        edition.setFont(new Font("Segoe UI", Font.BOLD, 13));
        edition.setForeground(GOLD);
        edition.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("Prepare Your Game");
        title.setFont(new Font("Georgia", Font.BOLD, 46));
        title.setForeground(IVORY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Choose your players, claim your colors, and let the game begin."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        content.add(edition);
        content.add(Box.createVerticalStrut(9));
        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(9));

        JSeparator line = new JSeparator();
        line.setForeground(new Color(91, 99, 131));
        line.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );
        content.add(line);
        content.add(Box.createVerticalStrut(9));

        // DIFFICULTY
        content.add(sectionLabel("01", "GAME MODE"));
        content.add(Box.createVerticalStrut(9));

        JPanel difficulty = new JPanel(
                new GridLayout(1, 3, 14, 0)
        );
        difficulty.setOpaque(false);
        difficulty.setAlignmentX(LEFT_ALIGNMENT);
        difficulty.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 48)
        );
        difficulty.setPreferredSize(new Dimension(850, 48));

        JButton easy = new RoyalButton(
                "Easy  ✓",
                SOFT_GOLD,
                GOLD,
                new Color(255, 237, 191)
        );

        JButton medium = new RoyalButton(
                "Medium  ·  Soon",
                new Color(48, 57, 91),
                new Color(88, 95, 120),
                new Color(48, 57, 91)
        );

        JButton hard = new RoyalButton(
                "Hard  ·  Soon",
                new Color(48, 57, 91),
                new Color(88, 95, 120),
                new Color(48, 57, 91)
        );

        medium.setEnabled(false);
        hard.setEnabled(false);

        difficulty.add(easy);
        difficulty.add(medium);
        difficulty.add(hard);

        content.add(difficulty);
        content.add(Box.createVerticalStrut(8));

        // PLAYER COUNT
        JPanel countRow = new JPanel(new BorderLayout());
        countRow.setOpaque(false);
        countRow.setAlignmentX(LEFT_ALIGNMENT);
        countRow.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, 52)
);
countRow.setPreferredSize(new Dimension(850, 48));

        countRow.add(
                sectionLabel("02", "YOUR PLAYERS"),
                BorderLayout.WEST
        );

        JPanel countOptions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 9, 0)
        );
        countOptions.setOpaque(false);
        countOptions.setPreferredSize(new Dimension(192, 44));

        for (int i = 0; i < 3; i++) {
            final int count = i + 2;

            JButton button = new CountGlowButton(count);

            button.setFont(
                    new Font("Segoe UI", Font.BOLD, 15)
            );
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setRolloverEnabled(true);
            button.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );
            button.setPreferredSize(new Dimension(55, 42));
            button.setMargin(new Insets(0, 0, 0, 0));

            button.addActionListener(event -> {
                playerCount = count;
                refreshCountButtons();
                refreshPlayerRows();
            });

            countButtons[i] = button;
            countOptions.add(button);
        }

        countRow.add(countOptions, BorderLayout.EAST);
        content.add(countRow);
        content.add(Box.createVerticalStrut(9));

        // PLAYER CARDS
        JPanel playersContainer = new JPanel();
        playersContainer.setOpaque(false);
        playersContainer.setLayout(
                new BoxLayout(
                        playersContainer,
                        BoxLayout.Y_AXIS
                )
        );
        playersContainer.setAlignmentX(LEFT_ALIGNMENT);

        for (int i = 0; i < 4; i++) {
            playerRows[i] = createPlayerRow(i);
            playersContainer.add(playerRows[i]);
            playersContainer.add(Box.createVerticalStrut(7));
        }

        content.add(playersContainer);
        content.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel(
                "Choose any color — selecting another player's color swaps them automatically."
        );
        hint.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        hint.setForeground(MUTED);
        hint.setAlignmentX(LEFT_ALIGNMENT);

        content.add(hint);
        content.add(Box.createVerticalStrut(8));

        errorLabel.setForeground(
                new Color(255, 166, 166)
        );
        errorLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(errorLabel);

        content.add(Box.createVerticalStrut(8));

        // ACTIONS
        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 14, 0)
        );
        actions.setOpaque(false);
        actions.setAlignmentX(LEFT_ALIGNMENT);
        actions.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 55)
        );

        JButton back = new RoyalButton(
                "Back",
                new Color(40, 49, 85),
                new Color(108, 111, 137),
                new Color(57, 66, 105)
        );
        back.setForeground(IVORY);

        JButton start = new RoyalButton(
                "Start Game  →",
                new Color(224, 178, 76),
                new Color(181, 130, 40),
                new Color(245, 199, 99)
        );

        back.setPreferredSize(new Dimension(150, 50));
        start.setPreferredSize(new Dimension(250, 50));

        back.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );
        start.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        back.addActionListener(event -> onBack.run());
        start.addActionListener(event -> onStart.run());

        actions.add(back);
        actions.add(start);

        content.add(actions);

// Reserve enough space for every visible player and the action buttons.
// Never force BoxLayout to compress the controls.

add(content);

        refreshCountButtons();
        refreshPlayerRows();
    }

    private JPanel sectionLabel(String number, String name) {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        // Prevent BoxLayout from stretching this heading into a tall blank area.
        panel.setPreferredSize(new Dimension(850, 28));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel numberLabel = new JLabel(number);
        numberLabel.setForeground(GOLD);
        numberLabel.setFont(
                new Font("Georgia", Font.BOLD, 19)
        );

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(IVORY);
        nameLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        panel.add(numberLabel);
        panel.add(nameLabel);

        return panel;
    }

    private JPanel createPlayerRow(int index) {
        JPanel row = new GlassPanel();

        row.setLayout(new BorderLayout(18, 0));
        row.setBorder(
                new EmptyBorder(9, 18, 9, 18)
        );
        row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 64)
        );
        row.setPreferredSize(new Dimension(850, 64));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel identity = new JPanel(
                new BorderLayout(12, 0)
        );
        identity.setOpaque(false);
        identity.setPreferredSize(new Dimension(145, 42));

        JLabel number = new JLabel(
                String.format("%02d", index + 1)
        );
        number.setFont(
                new Font("Georgia", Font.BOLD, 25)
        );
        number.setForeground(GOLD);

        JLabel playerLabel = new JLabel(
                "Player " + (index + 1)
        );
        playerLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );
        playerLabel.setForeground(IVORY);

        identity.add(number, BorderLayout.WEST);
        identity.add(playerLabel, BorderLayout.CENTER);

        nameFields[index] = new LuxuryNameField("Player " + (index + 1));

        JTextField field = nameFields[index];
        field.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        field.setForeground(IVORY);
        field.setOpaque(false);
        field.setCaretColor(SOFT_GOLD);
        field.setSelectionColor(new Color(112, 85, 45));
        field.setSelectedTextColor(IVORY);
        field.setBorder(new EmptyBorder(7, 17, 7, 17));

        row.add(identity, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.add(
                createColorPalette(index),
                BorderLayout.EAST
        );

        return row;
    }

    private JPanel createColorPalette(int playerIndex) {
        JPanel palette = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 4)
        );
        palette.setOpaque(false);
        palette.setPreferredSize(
                new Dimension(190, 42)
        );

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < COLORS.length; i++) {
            final PlayerColor color = COLORS[i];

            JToggleButton button = new JToggleButton() {
                @Override
                protected void paintComponent(Graphics gr) {
                    Graphics2D g = (Graphics2D) gr.create();

                    g.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    int size = 25;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;

                    Color actual = new Color(
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue()
                    );

                    if (getModel().isRollover()) {
                        g.setColor(
                                new Color(211, 169, 83, 55)
                        );
                        g.fillOval(
                                x - 5, y - 5,
                                size + 10, size + 10
                        );
                    }

                    if (isSelected()) {
                        g.setColor(GOLD);
                        g.setStroke(new BasicStroke(2.2f));
                        g.drawOval(
                                x - 4, y - 4,
                                size + 8, size + 8
                        );
                    }

                    g.setColor(actual);
                    g.fillOval(x, y, size, size);

                    if (isSelected()) {
                        g.setColor(Color.WHITE);
                        g.setStroke(
                                new BasicStroke(
                                        2.3f,
                                        BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND
                                )
                        );

                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;

                        g.drawLine(
                                cx - 6, cy,
                                cx - 1, cy + 5
                        );
                        g.drawLine(
                                cx - 1, cy + 5,
                                cx + 7, cy - 5
                        );
                    }

                    g.dispose();
                }
            };

            button.setPreferredSize(
                    new Dimension(37, 37)
            );
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setRolloverEnabled(true);
            button.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );
            button.setToolTipText(
                    color.getDisplayName()
            );

            button.addActionListener(event -> {
                PlayerColor previous =
                        selectedColors[playerIndex];

                if (previous == color) {
                    return;
                }

                // Swap colors with the current owner.
                for (int other = 0;
                     other < playerCount;
                     other++) {

                    if (other != playerIndex
                            && selectedColors[other] == color) {
                        selectedColors[other] = previous;
                        break;
                    }
                }

                selectedColors[playerIndex] = color;

                refreshColorPalettes();
                clearError();
            });

            colorButtons[playerIndex][i] = button;
            group.add(button);
            palette.add(button);
        }

        return palette;
    }

    private void refreshColorPalettes() {
        for (int player = 0; player < 4; player++) {
            for (int i = 0; i < COLORS.length; i++) {
                JToggleButton button =
                        colorButtons[player][i];

                if (button != null) {
                    button.setEnabled(true);
                    button.setSelected(
                            selectedColors[player] == COLORS[i]
                    );
                    button.repaint();
                }
            }
        }
    }

    private void refreshPlayerRows() {
        // Prevent duplicate colors when player count changes.
        for (int i = 0; i < playerCount; i++) {
            boolean duplicate = false;

            for (int j = 0; j < i; j++) {
                if (selectedColors[i] == selectedColors[j]) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                for (PlayerColor candidate : COLORS) {
                    boolean used = false;

                    for (int j = 0; j < i; j++) {
                        if (selectedColors[j] == candidate) {
                            used = true;
                            break;
                        }
                    }

                    if (!used) {
                        selectedColors[i] = candidate;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < playerRows.length; i++) {
            playerRows[i].setVisible(i < playerCount);
        }

        // Size the centered content according to the number of visible rows.
        // This prevents the count buttons and footer from being clipped.
        Container content = playerRows[0].getParent().getParent();
        if (content instanceof JPanel) {
            ((JPanel) content).setPreferredSize(
                    new Dimension(900, 600 + (playerCount - 2) * 70));
        }
        refreshColorPalettes();
        clearError();
        revalidate();
        repaint();
    }

    private void refreshCountButtons() {
        for (int i = 0; i < countButtons.length; i++) {
            JButton button = countButtons[i];
            button.setForeground(i + 2 == playerCount ? NAVY : IVORY);
            button.repaint();
        }
    }

    public void setOnBack(Runnable callback) {
        onBack = callback == null
                ? () -> {}
                : callback;
    }

    public void setOnStart(Runnable callback) {
        onStart = callback == null
                ? () -> {}
                : callback;
    }

    public GameConfig readConfig() {
        List<GameConfig.Slot> slots =
                new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            slots.add(
                    new GameConfig.Slot(
                            nameFields[i].getText(),
                            selectedColors[i]
                    )
            );
        }

        return new GameConfig(slots);
    }

    public void showError(String message) {
        errorLabel.setText(
                "<html>" +
                message.replace("\n", "<br>") +
                "</html>"
        );
    }

    public void clearError() {
        errorLabel.setText(" ");
    }

    // FULL NAVY BACKGROUND + FOUR TRANSPARENT DICE
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setColor(NAVY);
        g.fillRect(
                0, 0, getWidth(), getHeight()
        );

        int size = Math.max(
                105,
                Math.min(140, getWidth() / 11)
        );

        drawDie(
                g,
                getWidth() * 0.10,
                getHeight() * 0.24,
                size, -18, 5,
                new Color(211, 47, 54)
        );

        drawDie(
                g,
                getWidth() * 0.90,
                getHeight() * 0.24,
                size, 17, 3,
                new Color(43, 128, 225)
        );

        drawDie(
                g,
                getWidth() * 0.10,
                getHeight() * 0.80,
                size, 16, 2,
                new Color(46, 157, 86)
        );

        drawDie(
                g,
                getWidth() * 0.90,
                getHeight() * 0.80,
                size, -16, 6,
                new Color(246, 182, 42)
        );

        g.dispose();
    }

    private static void drawDie(
            Graphics2D canvas, double cx, double cy, int size,
            double angle, int value, Color color) {
        Graphics2D g = (Graphics2D) canvas.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(cx, cy);
        g.rotate(Math.toRadians(angle));

        int face = (int) (size * 0.76);
        int depth = Math.max(15, face / 5);
        int x = -face / 2 - depth / 3;
        int y = -face / 2 + depth / 3;

        // The three visible faces make a softly lit 3D cube.
        Polygon top = new Polygon(
                new int[]{x, x + depth, x + face + depth, x + face},
                new int[]{y, y - depth, y - depth, y}, 4);
        Polygon right = new Polygon(
                new int[]{x + face, x + face + depth,
                        x + face + depth, x + face},
                new int[]{y, y - depth, y + face - depth, y + face}, 4);

        g.setColor(new Color(0, 0, 0, 45));
        g.fillRoundRect(x + 11, y + 13, face + depth, face, 18, 18);

        g.setColor(tint(color, 75, 135));
        g.fillPolygon(top);
        g.setColor(tint(color, -40, 125));
        g.fillPolygon(right);
        g.setPaint(new GradientPaint(x, y, tint(color, 45, 130),
                x + face, y + face, tint(color, -15, 65)));
        g.fillRoundRect(x, y, face, face, 17, 17);

        g.setStroke(new BasicStroke(1.8f));
        g.setColor(tint(color, 70, 180));
        g.drawPolygon(top);
        g.setColor(tint(color, -10, 170));
        g.drawPolygon(right);
        g.setColor(tint(color, 85, 195));
        g.drawRoundRect(x, y, face, face, 17, 17);

        int p = face / 4;
        int r = Math.max(5, face / 15);
        int[][] dots;
        switch (value) {
            case 2: dots = new int[][]{{-p, -p}, {p, p}}; break;
            case 3: dots = new int[][]{{-p, -p}, {0, 0}, {p, p}}; break;
            case 4: dots = new int[][]{{-p, -p}, {p, -p}, {-p, p}, {p, p}}; break;
            case 5: dots = new int[][]{{-p, -p}, {p, -p}, {0, 0}, {-p, p}, {p, p}}; break;
            case 6: dots = new int[][]{{-p, -p}, {-p, 0}, {-p, p},
                    {p, -p}, {p, 0}, {p, p}}; break;
            default: dots = new int[][]{{0, 0}};
        }
        g.setColor(new Color(255, 250, 240, 220));
        for (int[] dot : dots) {
            g.fillOval(x + face / 2 + dot[0] - r,
                    y + face / 2 + dot[1] - r, r * 2, r * 2);
        }
        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(x + 16, y + 9, x + face - 20, y + 9);
        g.dispose();
    }

    private static Color tint(Color base, int delta, int alpha) {
        return new Color(
                Math.max(0, Math.min(255, base.getRed() + delta)),
                Math.max(0, Math.min(255, base.getGreen() + delta)),
                Math.max(0, Math.min(255, base.getBlue() + delta)),
                alpha);
    }

    /** Gold hover glow with a persistent gold selection. */
    private final class CountGlowButton extends JButton {
        private final int count;

        private CountGlowButton(int count) {
            super(String.valueOf(count));
            this.count = count;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            boolean selected = count == playerCount;
            boolean hover = getModel().isRollover();
            boolean active = selected || hover;
            int w = getWidth(), h = getHeight();

            if (active) {
                for (int spread = 5; spread >= 2; spread--) {
                    g.setColor(new Color(245, 197, 66, 17));
                    g.fillRoundRect(spread - 1, spread - 1,
                            w - 2 * (spread - 1), h - 2 * (spread - 1), 12, 12);
                }
            }
            Color top = selected ? new Color(255, 224, 139)
                    : hover ? new Color(109, 88, 56) : new Color(48, 57, 93);
            Color bottom = selected ? new Color(231, 172, 53)
                    : hover ? new Color(75, 64, 61) : new Color(37, 47, 82);
            g.setPaint(new GradientPaint(0, 4, top, 0, h - 4, bottom));
            g.fillRoundRect(5, 5, w - 10, h - 10, 9, 9);
            g.setStroke(new BasicStroke(active ? 1.8f : 1f));
            g.setColor(active ? new Color(255, 228, 150)
                    : new Color(111, 117, 150));
            g.drawRoundRect(5, 5, w - 11, h - 11, 9, 9);
            g.dispose();
            setForeground(selected ? NAVY : IVORY);
            super.paintComponent(graphics);
        }
    }

    /** Rounded glass name editor with a gold focus halo. */
    private static final class LuxuryNameField extends JTextField {
        private LuxuryNameField(String value) {
            super(value);
            setOpaque(false);
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent event) {
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent event) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            boolean focused = hasFocus();
            if (focused) {
                g.setColor(new Color(245, 197, 66, 28));
                g.fillRoundRect(0, 0, w, h, 19, 19);
            }
            g.setPaint(new GradientPaint(0, 2,
                    new Color(54, 64, 105), 0, h,
                    new Color(31, 40, 76)));
            g.fillRoundRect(3, 3, w - 6, h - 6, 15, 15);
            g.setColor(focused ? new Color(255, 215, 122)
                    : new Color(180, 149, 91, 135));
            g.setStroke(new BasicStroke(focused ? 1.8f : 1f));
            g.drawRoundRect(3, 3, w - 7, h - 7, 15, 15);
            g.setColor(new Color(255, 255, 255, 34));
            g.drawLine(16, 5, Math.max(16, w - 18), 5);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class GlassPanel extends JPanel {

    private GlassPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        GradientPaint glass = new GradientPaint(
                0, 0, new Color(255, 255, 255, 43),
                width, height, new Color(255, 255, 255, 13)
        );

        g.setPaint(glass);
        g.fillRoundRect(0, 0, width, height, 20, 20);

        g.setColor(new Color(245, 225, 178, 95));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(0, 0, width, height, 20, 20);

        g.dispose();
        super.paintComponent(graphics);
    }
}
}
