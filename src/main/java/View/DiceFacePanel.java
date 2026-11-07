package View;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Draws the latest regular-die result. Zero means no roll yet.
 */
public final class DiceFacePanel extends JPanel {

    private static final int[][][] PIPS = {
            {},
            {{1, 1}},
            {{0, 0}, {2, 2}},
            {{0, 0}, {1, 1}, {2, 2}},
            {{0, 0}, {0, 2}, {2, 0}, {2, 2}},
            {{0, 0}, {0, 2}, {1, 1}, {2, 0}, {2, 2}},
            {{0, 0}, {2, 0}, {0, 1}, {2, 1}, {0, 2}, {2, 2}}
    };

    private int value;

    public DiceFacePanel() {
        setPreferredSize(new Dimension(84, 84));
        setMinimumSize(new Dimension(84, 84));
        setMaximumSize(new Dimension(84, 84));
        setOpaque(false);
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        g2.setColor(new Color(250, 250, 252));
        g2.fillRoundRect(x, y, size, size, 16, 16);
        g2.setColor(new Color(40, 44, 58));
        g2.drawRoundRect(x, y, size, size, 16, 16);

        if (value < 1 || value > 6) {
            g2.drawString("—", x + size / 2 - 4, y + size / 2 + 4);
            g2.dispose();
            return;
        }

        int gap = size / 4;
        int pip = Math.max(6, size / 8);
        for (int[] spot : PIPS[value]) {
            int px = x + gap + spot[0] * gap - pip / 2;
            int py = y + gap + spot[1] * gap - pip / 2;
            g2.fillOval(px, py, pip, pip);
        }
        g2.dispose();
    }
}
