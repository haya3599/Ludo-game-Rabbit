package View;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Rounded button for the Game Setup screen only. */
public final class RoyalButton extends JButton {
    private final Color fill;
    private final Color outline;
    private final Color hoverFill;

    public RoyalButton(String text, Color fill, Color outline, Color hoverFill) {
        super(text);
        this.fill = fill;
        this.outline = outline;
        this.hoverFill = hoverFill;
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        setForeground(UiKit.NAVY);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color background = !isEnabled() ? new Color(242, 239, 233)
                : getModel().isPressed() ? fill.darker()
                : getModel().isRollover() ? hoverFill : fill;
        g.setColor(background);
        g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
        g.setColor(isEnabled() ? outline : new Color(218, 210, 196));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
        g.dispose();
        super.paintComponent(graphics);
    }
}
