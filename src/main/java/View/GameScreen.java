
package View;

import Model.Piece;
import Model.Player;
import Model.PlayerColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public final class GameScreen extends JPanel {

    private static final Color DARK = new Color(23, 9, 39);
    private static final Color PURPLE = new Color(57, 20, 76);
    private static final Color GOLD = new Color(245, 190, 92);
    private static final Color LIGHT_GOLD = new Color(255, 226, 158);
    private static final Color SOFT = new Color(225, 205, 230);

    private final BoardPanel board = new BoardPanel();

    private final JLabel turnLabel = new JLabel("Turn 1");
    private final JLabel playerLabel = new JLabel("Player 1");
    private final JPanel colorChip = new JPanel();
    private final JLabel diceCaption = new JLabel("Latest Roll");
    private final DiceFacePanel diceFace = new DiceFacePanel();
    private final JLabel timeLabel = new JLabel("00:00");
    private final JTextArea message = new JTextArea("Roll the dice.");

    private final JButton rollButton = new RoyalButton("⚄  Roll Dice", true);
    private final JButton leaveButton = new RoyalButton("Back to Home", false);

    private final Timer clock;
    private final long startedAt = System.currentTimeMillis();

    private Player displayedPlayer;
    private Runnable onRoll = () -> {};
    private Runnable onLeave = () -> {};

    public GameScreen(List<Player> players) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        board.setPlayers(players);

        // Board: original BoardPanel, unchanged.
        JPanel boardArea = new JPanel(new GridBagLayout());
        boardArea.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        boardArea.add(board, gbc);

        // Royal right panel.
        JPanel side = new RoyalSidePanel();
        side.setOpaque(false);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(12, 25, 15, 25));
        side.setPreferredSize(new Dimension(390, 0));

        JComponent emblem = createEmblem();
        emblem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mode = new JLabel("EASY GAME");
        mode.setFont(new Font("Serif", Font.BOLD, 19));
        mode.setForeground(LIGHT_GOLD);
        center(mode);

        turnLabel.setFont(new Font("Serif", Font.BOLD, 43));
        turnLabel.setForeground(LIGHT_GOLD);
        center(turnLabel);

        JLabel playerCaption = caption("Current Player");
        JLabel rollCaption = diceCaption;
        JLabel elapsedCaption = caption("Elapsed Time");

        diceCaption.setForeground(LIGHT_GOLD);
        diceCaption.setFont(new Font("SansSerif", Font.BOLD, 14));
        center(diceCaption);

        // Player information without rectangular plaque.
        JPanel playerRow = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 4));
        playerRow.setOpaque(false);

        colorChip.setPreferredSize(new Dimension(19, 19));
        colorChip.setBorder(BorderFactory.createLineBorder(GOLD));

        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        playerLabel.setForeground(Color.WHITE);

        playerRow.add(colorChip);
        playerRow.add(playerLabel);

        JPanel playerDecoration = decoratedRow(playerRow, 275, 45);

        // Original DiceFacePanel remains interactive through controller.
        JPanel diceFrame = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = quality(graphics);
                int w = getWidth();
                int h = getHeight();

                g.setPaint(new GradientPaint(
                        0, 0, LIGHT_GOLD,
                        0, h, new Color(170, 105, 34)));
                g.fillRoundRect(0, 0, w, h, 23, 23);
                g.setColor(new Color(255, 221, 130, 90));
                g.setStroke(new BasicStroke(2.0f));
                g.drawRoundRect(1, 1, w - 3, h - 3, 23, 23);

                g.setColor(new Color(38, 14, 56));
                g.fillRoundRect(4, 4, w - 8, h - 8, 18, 18);
                g.dispose();
            }
        };
        diceFrame.setOpaque(false);
        diceFrame.setBorder(new EmptyBorder(8, 8, 8, 8));
        diceFrame.add(diceFace, BorderLayout.CENTER);
        fixed(diceFrame, 104, 104);

        // Timer with a fine ornamental divider, not a rectangle.
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 29));
        timeLabel.setForeground(LIGHT_GOLD);

        JLabel clockIcon = new JLabel("◷");
        clockIcon.setFont(new Font("Serif", Font.BOLD, 27));
        clockIcon.setForeground(GOLD);

        JPanel timerRow = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 9, 3));
        timerRow.setOpaque(false);
        timerRow.add(clockIcon);
        timerRow.add(timeLabel);

        JPanel timerDecoration = decoratedRow(timerRow, 265, 48);

        // Action button.
        rollButton.setFont(new Font("SansSerif", Font.BOLD, 19));
        fixed(rollButton, 305, 52);
        rollButton.addActionListener(e -> onRoll.run());

        message.setEditable(false);
        message.setOpaque(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(new Font("SansSerif", Font.PLAIN, 13));
        message.setForeground(SOFT);
        message.setRows(2);
        fixed(message, 290, 24);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Compact, predictable layout: no vertical glue between controls.
        // This keeps every action inside the royal frame at normal window sizes.
        side.add(emblem);
        side.add(Box.createVerticalStrut(3));
        side.add(royalTitle(mode, 285, 38));
        side.add(Box.createVerticalStrut(3));
        side.add(ornament(220, 12));
        side.add(turnLabel);
        side.add(Box.createVerticalStrut(5));

        side.add(playerCaption);
        side.add(Box.createVerticalStrut(2));
        side.add(playerDecoration);
        side.add(Box.createVerticalStrut(6));

        side.add(rollCaption);
        side.add(Box.createVerticalStrut(4));
        side.add(diceFrame);
        side.add(Box.createVerticalStrut(6));

        side.add(elapsedCaption);
        side.add(Box.createVerticalStrut(3));
        side.add(timerDecoration);
        side.add(Box.createVerticalStrut(7));

        side.add(rollButton);
        side.add(Box.createVerticalStrut(4));
        side.add(message);

        // Small royal back button, positioned at the upper-left of the game area.
        // Its custom icon is painted in Java, so no extra image file is needed.
        leaveButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        leaveButton.setIcon(new CurvedBackIcon());
        leaveButton.setIconTextGap(5);
        leaveButton.setPreferredSize(new Dimension(148, 34));
        leaveButton.addActionListener(e -> onLeave.run());

        // Keep the right panel dedicated to game information.
        side.add(Box.createVerticalGlue());
        side.add(Box.createVerticalStrut(10));

        // Overlay the back button over the EMPTY top-left margin.
        // BoardPanel remains centered and its mouse interactions are preserved.
        JPanel boardWithBack = new JPanel(null) {
            @Override
            public void doLayout() {
                boardArea.setBounds(0, 0, getWidth(), getHeight());
                leaveButton.setBounds(4, 4, 148, 34);
                boardArea.doLayout();
            }
        };
        boardWithBack.setOpaque(false);
        boardWithBack.add(boardArea);
        boardWithBack.add(leaveButton);
        boardWithBack.setComponentZOrder(leaveButton, 0);

        JPanel mainArea = new JPanel(new BorderLayout(12, 0));
        mainArea.setOpaque(false);
        mainArea.add(boardWithBack, BorderLayout.CENTER);
        mainArea.add(side, BorderLayout.EAST);
        add(mainArea, BorderLayout.CENTER);

        if (players != null && !players.isEmpty()) {
            setCurrentPlayer(players.get(0));
        }

        clock = new Timer(1000, e -> updateClock());
        clock.setInitialDelay(0);
        clock.start();
    }

    private JComponent createEmblem() {
        final BufferedImage original;
        BufferedImage loaded = null;
        try {
            URL url = GameScreen.class.getResource("/team_rabbit_royal.png");
            if (url != null) loaded = javax.imageio.ImageIO.read(url);
        } catch (java.io.IOException ex) {
            System.err.println("Could not load royal logo: " + ex.getMessage());
        }
        original = loaded;

        JComponent emblem = new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                if (original != null) {
                    double scale = Math.min((getWidth() - 4.0) / original.getWidth(),
                            (getHeight() - 4.0) / original.getHeight());
                    int w = (int) Math.round(original.getWidth() * scale);
                    int h = (int) Math.round(original.getHeight() * scale);
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;
                    g.drawImage(original, x, y, w, h, null);
                } else {
                    g.setColor(LIGHT_GOLD);
                    g.setFont(new Font("Serif", Font.BOLD, 22));
                    String title = "TEAM RABBIT";
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(title, (getWidth() - fm.stringWidth(title)) / 2,
                            getHeight() / 2);
                }
                g.dispose();
            }
        };
        emblem.setOpaque(false);
        fixed(emblem, 340, 142);
        return emblem;
    }

    // Fine golden lines and diamond ends.
    private static JComponent ornament(int width, int height) {
        JComponent decoration = new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = quality(graphics);
                int w = getWidth();
                int cy = getHeight() / 2;

                g.setColor(new Color(247, 192, 91, 175));
                g.setStroke(new BasicStroke(1.1f));
                g.drawLine(15, cy, w / 2 - 17, cy);
                g.drawLine(w / 2 + 17, cy, w - 15, cy);

                drawDiamond(g, w / 2, cy, 6);
                drawDiamond(g, 10, cy, 3);
                drawDiamond(g, w - 10, cy, 3);
                g.dispose();
            }
        };
        fixed(decoration, width, height);
        return decoration;
    }

    private static JPanel decoratedRow(
            JComponent content, int width, int height) {

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = quality(graphics);
                int w = getWidth();
                int h = getHeight();

                g.setColor(new Color(245, 190, 92, 190));
                g.setStroke(new BasicStroke(1.3f));

                // Two fine curved lines instead of a box.
                QuadCurve2D upper = new QuadCurve2D.Double(
                        18, 3, w / 2.0, 1, w - 18, 3);
                QuadCurve2D lower = new QuadCurve2D.Double(
                        18, h - 3, w / 2.0, h - 1,
                        w - 18, h - 3);

                g.draw(upper);
                g.draw(lower);

                drawDiamond(g, 12, h / 2, 4);
                drawDiamond(g, w - 12, h / 2, 4);
                g.dispose();
            }
        };
        panel.setOpaque(false);
        panel.add(content, BorderLayout.CENTER);
        fixed(panel, width, height);
        return panel;
    }

    private static void drawDiamond(
            Graphics2D g, int x, int y, int size) {
        Path2D p = new Path2D.Double();
        p.moveTo(x, y - size);
        p.lineTo(x + size, y);
        p.lineTo(x, y + size);
        p.lineTo(x - size, y);
        p.closePath();
        g.fill(p);
    }

    // Golden royal nameplate with pointed ornamental ends.
    private static JPanel royalTitle(JLabel title, int width, int height) {
        JPanel plate = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g = quality(graphics);
                int w = getWidth(), h = getHeight();
                Path2D shape = new Path2D.Double();
                shape.moveTo(24, 3);
                shape.lineTo(w - 24, 3);
                shape.quadTo(w - 7, h / 2.0, w - 24, h - 3);
                shape.lineTo(24, h - 3);
                shape.quadTo(7, h / 2.0, 24, 3);
                shape.closePath();
                g.setPaint(new GradientPaint(0, 0, new Color(255, 236, 172),
                        0, h, new Color(205, 130, 35)));
                g.fill(shape);
                g.setColor(new Color(255, 238, 172));
                g.setStroke(new BasicStroke(1.5f));
                g.draw(shape);
                g.setColor(new Color(112, 47, 63, 120));
                g.drawLine(35, h - 8, w - 35, h - 8);
                drawDiamond(g, 11, h / 2, 5);
                drawDiamond(g, w - 11, h / 2, 5);
                g.dispose();
            }
        };
        plate.setOpaque(false);
        title.setForeground(new Color(49, 18, 61));
        plate.add(title, BorderLayout.CENTER);
        fixed(plate, width, height);
        return plate;
    }

    private static final class RoyalSidePanel extends JPanel {
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = quality(graphics);
            int w = getWidth(), h = getHeight();
            g.setPaint(new GradientPaint(0, 0, new Color(69, 21, 86),
                    w, h, new Color(28, 8, 47)));
            g.fillRoundRect(3, 4, w - 6, h - 8, 43, 43);
            g.setStroke(new BasicStroke(3.2f));
            g.setColor(new Color(230, 158, 52));
            g.drawRoundRect(3, 4, w - 7, h - 9, 43, 43);
            g.setStroke(new BasicStroke(1.2f));
            g.setColor(new Color(255, 226, 144));
            g.drawRoundRect(8, 9, w - 17, h - 19, 39, 39);
            g.setColor(new Color(243, 186, 77, 115));
            g.drawRoundRect(16, 17, w - 33, h - 35, 30, 30);

            // Mirrored baroque scrollwork: top and bottom corners.
            for (int sx : new int[]{1, -1}) {
                for (int sy : new int[]{1, -1}) {
                    Graphics2D c = (Graphics2D) g.create();
                    c.translate(sx == 1 ? 0 : w, sy == 1 ? 0 : h);
                    c.scale(sx, sy);
                    c.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND));
                    c.setColor(new Color(255, 205, 100));
                    Path2D scroll = new Path2D.Double();
                    scroll.moveTo(24, 95);
                    scroll.curveTo(10, 73, 17, 39, 43, 25);
                    scroll.curveTo(62, 13, 84, 24, 83, 42);
                    scroll.curveTo(82, 58, 60, 60, 54, 47);
                    c.draw(scroll);
                    Path2D inner = new Path2D.Double();
                    inner.moveTo(27, 72);
                    inner.curveTo(23, 48, 40, 30, 59, 33);
                    inner.curveTo(69, 34, 72, 45, 64, 49);
                    c.draw(inner);
                    c.dispose();
                }
            }
            g.setColor(new Color(255, 214, 116));
            drawDiamond(g, w / 2, 7, 7);
            drawDiamond(g, w / 2, h - 7, 7);
            g.dispose();
        }
    }

    // Gold curved arrow, matching the royal theme.
    // A clearly readable left-pointing curved return arrow.
    private static final class CurvedBackIcon implements Icon {
        @Override public int getIconWidth() { return 25; }
        @Override public int getIconHeight() { return 20; }

        @Override
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g = quality(graphics);
            g.translate(x, y);
            g.setColor(LIGHT_GOLD);
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            // The tail begins at the right, bends upward, and points LEFT.
            Path2D shaft = new Path2D.Double();
            shaft.moveTo(22, 16);
            shaft.curveTo(22, 5, 13, 5, 5, 10);
            g.draw(shaft);

            // Distinct arrowhead at the leftmost end.
            Path2D head = new Path2D.Double();
            head.moveTo(11, 3);
            head.lineTo(4, 10);
            head.lineTo(12, 14);
            g.draw(head);
            g.dispose();
        }
    }

    private static final class RoyalButton extends JButton {
        private final boolean primary;

        RoyalButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
            setForeground(primary ? DARK : LIGHT_GOLD);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = quality(graphics);
            int w = getWidth();
            int h = getHeight();

            Color top = primary
                    ? new Color(255, 230, 156)
                    : new Color(80, 32, 99);
            Color bottom = primary
                    ? new Color(222, 154, 48)
                    : new Color(37, 13, 59);

            if (!isEnabled()) {
                top = new Color(130, 112, 135);
                bottom = new Color(83, 68, 93);
            } else if (getModel().isPressed()) {
                Color tmp = top;
                top = bottom;
                bottom = tmp;
            }

            g.setPaint(new GradientPaint(
                    0, 0, top, 0, h, bottom));
            g.fillRoundRect(7, 3, w - 14, h - 6, h, h);

            g.setColor(GOLD);
            g.setStroke(new BasicStroke(1.6f));
            g.drawRoundRect(7, 3, w - 15, h - 7, h, h);

            if (primary) {
                drawDiamond(g, 7, h / 2, 5);
                drawDiamond(g, w - 7, h / 2, 5);
            }

            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static Graphics2D quality(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(SOFT);
        center(label);
        return label;
    }

    private static void center(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private static void fixed(
            JComponent component, int width, int height) {
        Dimension size = new Dimension(width, height);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = quality(graphics);
        int w = getWidth();
        int h = getHeight();

        g.setPaint(new GradientPaint(
                0, 0, DARK,
                w, h, PURPLE));
        g.fillRect(0, 0, w, h);

        // Subtle golden waves.
        g.setColor(new Color(232, 171, 80, 75));
        g.setStroke(new BasicStroke(1.3f));
        g.drawArc(-230, -180, 800, 380, 190, 150);
        g.drawArc(-200, -160, 830, 380, 190, 150);
        g.drawArc(-250, h - 150, 760, 350, 45, 130);

        g.dispose();
    }

    // Public API: preserved for the existing controller.

    public void setTurnNumber(int turnNumber) {
        turnLabel.setText("Turn " + turnNumber);
    }

    public void setCurrentPlayer(Player player) {
        if (player == null) return;

        if (displayedPlayer != player) {
            setDiceValue(0);
            displayedPlayer = player;
        }

        playerLabel.setText(player.getName());

        PlayerColor color = player.getColor();
        colorChip.setBackground(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue()));
        colorChip.setToolTipText(color.getDisplayName());
    }

    public void setDiceValue(int diceValue) {
        diceFace.setValue(diceValue);
        diceCaption.setText(diceValue == 0
                ? "Latest Roll"
                : "Latest Roll: " + diceValue);
    }

    public void setMessage(String text) {
        message.setText(text == null ? " " : text);
    }

    public void setRollEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
    }

    public void setHighlights(List<Piece> pieces) {
        board.setHighlights(pieces);
    }

    public void refreshBoard() {
        board.repaint();
    }

    public void setOnRoll(Runnable onRoll) {
        this.onRoll = onRoll == null ? () -> {} : onRoll;
    }

    public void setOnPieceSelected(
            Consumer<Piece> onPieceSelected) {
        board.setOnPieceSelected(onPieceSelected);
    }

    public void setOnLeave(Runnable onLeave) {
        this.onLeave = onLeave == null ? () -> {} : onLeave;
    }

    private void updateClock() {
        long seconds = Math.max(0,
                (System.currentTimeMillis() - startedAt) / 1000);
        timeLabel.setText(String.format(
                "%02d:%02d", seconds / 60, seconds % 60));
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (clock != null) {
            if (visible) clock.start();
            else clock.stop();
        }
    }

    @Override
    public void removeNotify() {
        if (clock != null) clock.stop();
        super.removeNotify();
    }
}
