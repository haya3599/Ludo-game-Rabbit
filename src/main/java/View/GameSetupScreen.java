
package View;

import Model.GameConfig;
import Model.PlayerColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class GameSetupScreen extends JPanel {

    private static final Color NAVY = new Color(29, 13, 52);
    private static final Color IVORY = new Color(255, 243, 207);
    private static final Color GOLD = new Color(236, 187, 91);
    private static final Color SOFT_GOLD = new Color(255, 224, 151);
    private static final Color MUTED = new Color(218, 197, 218);

    private static final PlayerColor[] COLORS = {
            PlayerColor.RED,
            PlayerColor.BLUE,
            PlayerColor.GREEN,
            PlayerColor.YELLOW
    };

    private static final BufferedImage RABBIT_LOGO = loadBrandImage("rabbit_logo.png");
    private static final BufferedImage ROYAL_RABBIT = loadBrandImage("royal_rabbit.png");

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
        content.setBorder(new EmptyBorder(22, 15, 14, 15));
        content.setPreferredSize(new Dimension(900, 740));

        // HEADER
        JLabel edition = new JLabel(
                "L U D O   A C A D E M Y   /   R O Y A L   E D I T I O N"
        );
        edition.setFont(new Font("Segoe UI", Font.BOLD, 13));
        edition.setForeground(GOLD);
        edition.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new RoyalTitleLabel("PREPARE YOUR GAME");
        title.setFont(new Font("Georgia", Font.BOLD, 43));
        title.setForeground(SOFT_GOLD);
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
        line.setForeground(new Color(236, 187, 91, 150));
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
                "Easy",
                SOFT_GOLD,
                GOLD,
                new Color(255, 237, 191)
        );

        JButton medium = new RoyalButton(
                "Medium  ·  Soon",
                new Color(62, 33, 86),
                new Color(169, 118, 80),
                new Color(62, 33, 86)
        );

        JButton hard = new RoyalButton(
                "Hard  ·  Soon",
                new Color(62, 33, 86),
                new Color(169, 118, 80),
                new Color(62, 33, 86)
        );

        medium.setEnabled(false);
        hard.setEnabled(false);

        easy.setForeground(new Color(42, 21, 62));
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
                new Color(60, 33, 82),
                new Color(236, 187, 91),
                new Color(35, 19, 61)
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
        // Dark ink on the light-gold primary action, for strong contrast.
        start.setForeground(new Color(42, 21, 62));

        back.addActionListener(event -> onBack.run());
        start.addActionListener(event -> onStart.run());

        actions.add(back);
        actions.add(start);

        content.add(actions);

// Reserve enough space for every visible player and the action buttons.
// Never force BoxLayout to compress the controls.

// A real two-column layout reserves space for the mascot instead of
// guessing the remaining margin from the window width.
        JPanel stage = new JPanel(new BorderLayout(8, 0));
        stage.setOpaque(false);
        stage.add(content, BorderLayout.CENTER);
        stage.add(new RoyalRabbitSidePanel(), BorderLayout.EAST);
        add(stage);

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

    // The same purple-and-gold royal atmosphere used by HomeScreen.
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g.setPaint(new GradientPaint(0, 0, new Color(18, 7, 36),
                w, h, new Color(57, 21, 78)));
        g.fillRect(0, 0, w, h);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(w * .51, h * .34),
                Math.max(1, (float)(Math.max(w, h) * .55)),
                new float[]{0f, 1f},
                new Color[]{new Color(124, 58, 150, 88), new Color(36, 13, 64, 0)}));
        g.fillRect(0, 0, w, h);

        // Soft silk ribbons and fine gold strokes: deliberately behind real controls.
        Path2D silk = new Path2D.Double();
        silk.moveTo(-30, h * .20);
        silk.curveTo(w * .13, -30, w * .23, h * .12, w * .37, -25);
        g.setStroke(new BasicStroke(31f));
        g.setColor(new Color(169, 77, 183, 30));
        g.draw(silk);
        g.setStroke(new BasicStroke(1.8f));
        g.setColor(new Color(242, 176, 110, 77));
        g.draw(silk);
        Path2D right = new Path2D.Double();
        right.moveTo(w + 22, h * .10);
        right.curveTo(w * .90, h * .23, w * 1.03, h * .38, w * .94, h * .51);
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(new Color(240, 185, 100, 80));
        g.draw(right);

        // TEAM RABBIT branding, with an uncropped proportional PNG.
        if (RABBIT_LOGO != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int boxW = 63, boxH = 63;
            double ratio = Math.min(boxW / (double)RABBIT_LOGO.getWidth(),
                    boxH / (double)RABBIT_LOGO.getHeight());
            int iw = (int)Math.round(RABBIT_LOGO.getWidth() * ratio);
            int ih = (int)Math.round(RABBIT_LOGO.getHeight() * ratio);
            g.drawImage(RABBIT_LOGO, 25 + (boxW - iw)/2, 15 + (boxH - ih)/2,
                    iw, ih, null);
        } else {
            g.setColor(new Color(255, 224, 151));
            g.setFont(new Font("Georgia", Font.BOLD, 28));
            g.drawString("♛", 43, 58);
        }
        g.setFont(new Font("Georgia", Font.BOLD, 16));
        g.setColor(SOFT_GOLD);
        g.drawString("TEAM RABBIT", 98, 45);
        g.setStroke(new BasicStroke(3.4f));
        g.setColor(new Color(255, 193, 85, 45));
        g.drawLine(98, 56, 267, 56);
        g.setStroke(new BasicStroke(1.0f));
        g.setColor(new Color(255, 222, 151, 210));
        g.drawLine(98, 56, 267, 56);
        sparkle(g, 273, 56, 6);

        // Delicate side ornaments instead of four large, distracting dice.
        for (int i = 0; i < 11; i++) {
            double a = i * 2.39996;
            int x = (int)(w * .11 + 105 * Math.cos(a));
            int y = (int)(h * .57 + 205 * Math.sin(a));
            sparkle(g, x, y, i % 4 == 0 ? 5 : 2);
            int xr = (int)(w * .89 + 100 * Math.cos(a + 1));
            int yr = (int)(h * .57 + 210 * Math.sin(a + 1));
            sparkle(g, xr, yr, i % 4 == 0 ? 4 : 2);
        }
        g.setColor(new Color(236, 187, 91, 55));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new Arc2D.Double(-95, h * .52, 270, 270, -65, 220, Arc2D.OPEN));
        g.draw(new Arc2D.Double(w - 175, h * .49, 270, 270, 25, 220, Arc2D.OPEN));
        g.dispose();
    }

    /**
     * Draw the original transparent PNG directly.
     * No backdrop, dark overlay, extra glow, or alpha manipulation.
     */
    private static final class RoyalRabbitSidePanel extends JPanel {
        RoyalRabbitSidePanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(350, 520));
            setMinimumSize(new Dimension(260, 350));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (ROYAL_RABBIT == null) return;

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth();
            int h = getHeight();
            int availableW = Math.max(1, w - 14);
            int availableH = Math.max(1, Math.min(520, h - 26));
            double scale = Math.min(
                    availableW / (double) ROYAL_RABBIT.getWidth(),
                    availableH / (double) ROYAL_RABBIT.getHeight());
            int iw = Math.max(1, (int) Math.round(ROYAL_RABBIT.getWidth() * scale));
            int ih = Math.max(1, (int) Math.round(ROYAL_RABBIT.getHeight() * scale));
            int x = (w - iw) / 2;
            int y = Math.max(0, Math.min((h - ih) / 2 + 16, h - ih));

            // Respect the PNG's existing transparency.
            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(ROYAL_RABBIT, x, y, iw, ih, null);
            g.dispose();
        }
    }

    private static void sparkle(Graphics2D g, int x, int y, int radius) {
        Graphics2D z = (Graphics2D)g.create();
        z.setColor(new Color(255, 215, 131, 175));
        z.setStroke(new BasicStroke(1.2f));
        z.drawLine(x-radius, y, x+radius, y);
        z.drawLine(x, y-radius, x, y+radius);
        z.dispose();
    }

    private static BufferedImage loadBrandImage(String name) {
        try (InputStream in = GameSetupScreen.class.getResourceAsStream("/" + name)) {
            if (in != null) return ImageIO.read(in);
        } catch (IOException ignored) { }
        try { return ImageIO.read(new File("src/main/resources/" + name)); }
        catch (IOException ignored) { return null; }
    }

    /** Decorative gold laurel accents painted in Java, never font glyphs. */
    private static final class RoyalTitleLabel extends JLabel {
        RoyalTitleLabel(String text) { super(text); }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int textEnd = getInsets().left + getFontMetrics(getFont()).stringWidth(getText());
            int cy = Math.max(8, getHeight() / 2);
            // Two small botanical flourishes frame the title without covering letters.
            drawTitleLaurel(g, textEnd + 22, cy, false);
            g.dispose();
        }
        private static void drawTitleLaurel(Graphics2D g, int x, int y, boolean reverse) {
            Graphics2D z = (Graphics2D) g.create();
            z.translate(x, y);
            if (reverse) z.scale(-1, 1);
            z.setColor(new Color(236, 187, 91, 185));
            z.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            z.draw(new QuadCurve2D.Double(0, 18, 17, 0, 2, -18));
            for (int i = 0; i < 4; i++) {
                int yy = 12 - i * 9;
                int xx = 7 + (i % 2) * 5;
                z.setColor(new Color(255, 213, 127, 200));
                z.fill(new Ellipse2D.Double(xx, yy, 11, 4));
                z.fill(new Ellipse2D.Double(xx - 9, yy - 4, 10, 4));
            }
            z.dispose();
        }
    }

    /** Gold-trimmed royal button. Real JButton; listeners and enabled state stay intact. */
    private static final class RoyalButton extends JButton {
        private final Color top, edge, bottom;
        RoyalButton(String label, Color top, Color edge, Color bottom) {
            super(label);
            this.top = top;
            this.edge = edge;
            this.bottom = bottom;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(new Color(255, 243, 207));
            setFont(new Font("Segoe UI", Font.BOLD, 15));
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            Color a = isEnabled() ? top : new Color(67, 45, 84);
            Color b = isEnabled() ? bottom : new Color(40, 26, 64);
            g.setPaint(new GradientPaint(0, 3, a, 0, h, b));
            g.fillRoundRect(3, 3, w-6, h-6, 15, 15);
            g.setColor(isEnabled() ? edge : new Color(129, 96, 118));
            g.setStroke(new BasicStroke(getModel().isRollover() && isEnabled() ? 2f : 1.2f));
            g.drawRoundRect(3, 3, w-7, h-7, 15, 15);
            g.setColor(new Color(255, 229, 171, 55));
            g.drawRoundRect(6, 6, w-13, h-13, 12, 12);
            g.dispose();
            super.paintComponent(graphics);
            if ("Easy".equals(getText())) {
                Graphics2D check = (Graphics2D) graphics.create();
                check.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                check.setColor(new Color(42, 21, 62));
                check.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int textWidth = check.getFontMetrics(getFont()).stringWidth(getText());
                int cx = getWidth() / 2 + textWidth / 2 + 14;
                int cy = getHeight() / 2;
                check.drawLine(cx - 5, cy, cx - 1, cy + 4);
                check.drawLine(cx - 1, cy + 4, cx + 7, cy - 6);
                check.dispose();
            }
        }
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
            Color top = selected ? new Color(255, 235, 176)
                    : hover ? new Color(109, 88, 56) : new Color(68, 38, 91);
            Color bottom = selected ? new Color(211, 146, 56)
                    : hover ? new Color(75, 64, 61) : new Color(39, 20, 65);
            g.setPaint(new GradientPaint(0, 4, top, 0, h - 4, bottom));
            g.fillRoundRect(5, 5, w - 10, h - 10, 9, 9);
            g.setStroke(new BasicStroke(active ? 1.8f : 1f));
            g.setColor(active ? new Color(255, 228, 150)
                    : new Color(172, 125, 107));
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
                    new Color(71, 40, 95), 0, h,
                    new Color(37, 19, 64)));
            g.fillRoundRect(3, 3, w - 6, h - 6, 15, 15);
            g.setColor(focused ? new Color(255, 215, 122)
                    : new Color(180, 149, 91, 135));
            g.setStroke(new BasicStroke(focused ? 1.8f : 1f));
            g.drawRoundRect(3, 3, w - 7, h - 7, 15, 15);
            g.setColor(new Color(255, 221, 165, 45));
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
                0, 0, new Color(170, 99, 193, 55),
                width, height, new Color(77, 39, 112, 90)
        );

        g.setPaint(glass);
        g.fillRoundRect(0, 0, width, height, 20, 20);

        g.setColor(new Color(236, 187, 91, 130));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(0, 0, width, height, 20, 20);

        g.dispose();
        super.paintComponent(graphics);
    }
}
}
