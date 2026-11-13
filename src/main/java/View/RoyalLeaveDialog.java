package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/** Compact royal-themed confirmation dialog. Returns true only on explicit Leave. */
public final class RoyalLeaveDialog {
    private static final Color GOLD = new Color(247, 190, 76);
    private static final Color LIGHT_GOLD = new Color(255, 225, 157);
    private static final Color CREAM = new Color(241, 220, 242);
    private static final Color DARK = new Color(27, 9, 43);

    private RoyalLeaveDialog() {}

    public static boolean confirm(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Royal Ludo Academy", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setBackground(new Color(0, 0, 0, 0));
        final boolean[] approved = {false};

        JPanel background = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g.setPaint(new GradientPaint(0, 0, new Color(70, 28, 88), w, h, DARK));
                g.fill(new RoundRectangle2D.Double(3, 3, w - 6, h - 6, 26, 26));
                // Restrained gold frame: no corner curls or diamonds.
                g.setColor(new Color(239, 183, 83));
                g.setStroke(new BasicStroke(1.7f));
                g.draw(new RoundRectangle2D.Double(5, 5, w - 11, h - 11, 22, 22));
                g.setColor(new Color(255, 223, 158, 90));
                g.setStroke(new BasicStroke(0.7f));
                g.draw(new RoundRectangle2D.Double(10, 10, w - 21, h - 21, 17, 17));
                g.setPaint(new GradientPaint(w / 2f - 65, 0,
                        new Color(247, 190, 76, 0), w / 2f, 0,
                        new Color(247, 190, 76, 190), true));
                g.setColor(new Color(247, 190, 76, 120));
                g.drawLine(w / 2 - 55, 105, w / 2 + 55, 105);
                g.dispose();
            }
        };
        background.setOpaque(false);
        background.setPreferredSize(new Dimension(390, 245));

        JLabel rabbit = createRabbitLabel();
        rabbit.setBounds(145, 12, 100, 100);
        background.add(rabbit);

        JLabel title = centeredLabel("LEAVE GAME?", new Font("Georgia", Font.BOLD, 21), LIGHT_GOLD);
        title.setBounds(20, 117, 350, 29);
        background.add(title);

        JLabel warning = centeredLabel("Your progress will be lost.",
                new Font("SansSerif", Font.PLAIN, 12), CREAM);
        warning.setBounds(20, 153, 350, 20);
        background.add(warning);

        JButton stay = royalButton("Keep Playing", true);
        JButton leave = royalButton("Leave Game", false);
        stay.setBounds(42, 190, 143, 34);
        leave.setBounds(205, 190, 143, 34);
        stay.addActionListener(e -> dialog.dispose());
        leave.addActionListener(e -> {
            approved[0] = true;
            dialog.dispose();
        });
        background.add(stay);
        background.add(leave);

        dialog.setContentPane(background);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().setDefaultButton(stay);
        dialog.setVisible(true);
        return approved[0];
    }

    

private static JLabel createRabbitLabel() {
    JLabel label = new JLabel() {
        private BufferedImage rabbitImage;

        {
            try (InputStream stream = RoyalLeaveDialog.class
                    .getResourceAsStream("/rabbit_logo.png")) {
                if (stream != null) {
                    rabbitImage = ImageIO.read(stream);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (rabbitImage == null) {
                return;
            }

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
            g.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            double scale = Math.min(
                    (double) getWidth() / rabbitImage.getWidth(),
                    (double) getHeight() / rabbitImage.getHeight()
            );

            int w = (int) Math.round(rabbitImage.getWidth() * scale);
            int h = (int) Math.round(rabbitImage.getHeight() * scale);

            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g.drawImage(rabbitImage, x, y, w, h, null);
            g.dispose();
        }
    };

    label.setOpaque(false);
    return label;
}



    private static JLabel centeredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private static JButton royalButton(String text, boolean gold) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color top = gold ? new Color(255, 231, 166) : new Color(87, 36, 104);
                Color bottom = gold ? new Color(224, 155, 53) : new Color(43, 15, 65);
                if (getModel().isPressed()) {
                    Color temp = top;
                    top = bottom;
                    bottom = temp;
                } else if (getModel().isRollover()) {
                    top = top.brighter();
                }
                g.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
                g.fillRoundRect(2, 2, w - 4, h - 4, 24, 24);
                g.setColor(GOLD);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(2, 2, w - 5, h - 5, 24, 24);
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(gold ? new Color(40, 13, 55) : LIGHT_GOLD);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

}
