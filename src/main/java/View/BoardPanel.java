package View;

import Model.BoardGeometry;
import Model.GridPoint;
import Model.Piece;
import Model.Player;
import Model.PlayerColor;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Draws the Easy board from {@link BoardGeometry}. It does not decide whether
 * a move is legal; the controller reports which pieces may be selected.
 */
public final class BoardPanel extends JPanel {

    private List<Player> players = List.of();
    private List<Piece> highlights = List.of();
    private Consumer<Piece> onPieceSelected = piece -> { };
    private int originX;
    private int originY;
    private int cell;

    public BoardPanel() {
        setPreferredSize(new Dimension(780, 780));
        setBackground(new Color(247, 242, 233));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                Piece piece = pieceAt(event.getX(), event.getY());
                if (piece != null) {
                    onPieceSelected.accept(piece);
                }
            }
        });
    }

    public void setPlayers(List<Player> players) {
        this.players = List.copyOf(players);
        repaint();
    }

    public void setHighlights(List<Piece> highlights) {
        this.highlights = List.copyOf(highlights);
        repaint();
    }

    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        this.onPieceSelected = onPieceSelected;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        updateLayout();
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Warm pearl canvas, no black gutters.
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 253, 247),
                getWidth(), getHeight(), new Color(232, 220, 199)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        int side = cell * BoardGeometry.GRID_SIZE;
        g2.setColor(new Color(159, 120, 66, 30));
        g2.fillRoundRect(originX - 7, originY + 7, side + 14, side + 10, 24, 24);
        g2.setPaint(new GradientPaint(originX, originY, new Color(251, 247, 238),
                originX + side, originY + side, new Color(227, 211, 184)));
        g2.fillRoundRect(originX - 5, originY - 5, side + 10, side + 10, 18, 18);
        g2.setColor(new Color(182, 145, 89));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(originX - 5, originY - 5, side + 10, side + 10, 18, 18);
        paintYards(g2);
        paintTrack(g2);
        paintHomeLanes(g2);
        paintCenter(g2);
        paintPieces(g2);
        g2.dispose();
    }

    private void updateLayout() {
        int available = Math.max(0, Math.min(getWidth(), getHeight()) - 24);
        cell = Math.max(1, available / BoardGeometry.GRID_SIZE);
        int used = cell * BoardGeometry.GRID_SIZE;
        originX = (getWidth() - used) / 2;
        originY = (getHeight() - used) / 2;
    }

    /** Four richly colored enamel courts set into an ivory and brass board. */
    private void paintYards(Graphics2D g2) {
        for (PlayerColor color : PlayerColor.values()) {
            GridPoint origin = BoardGeometry.yardOrigin(color);
            int x = originX + origin.column() * cell;
            int y = originY + origin.row() * cell;
            int size = cell * BoardGeometry.YARD_SIZE;
            Color accent = royal(color);
            Graphics2D c = (Graphics2D) g2.create();
            c.translate(x, y);
            c.scale(size / 360.0, size / 360.0);
            c.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Porcelain outer frame, metallic inset, jewel-tone lacquer.
            c.setColor(new Color(106, 76, 40, 38));
            c.fillRoundRect(7, 12, 347, 345, 30, 30);
            c.setPaint(new GradientPaint(0, 0, new Color(255, 251, 241),
                    360, 360, new Color(213, 190, 150)));
            c.fillRoundRect(3, 3, 354, 354, 28, 28);
            c.setColor(new Color(166, 124, 66));
            c.setStroke(new BasicStroke(3));
            c.drawRoundRect(5, 5, 350, 350, 27, 27);
            c.setPaint(new GradientPaint(20, 15, mix(accent, Color.WHITE, .28),
                    320, 350, mix(accent, new Color(38, 31, 26), .24)));
            c.fillRoundRect(15, 15, 330, 330, 23, 23);
            c.setColor(new Color(255, 235, 185, 190));
            c.setStroke(new BasicStroke(2.5f));
            c.drawRoundRect(20, 20, 320, 320, 18, 18);
            c.setColor(new Color(255, 246, 220, 65));
            c.setStroke(new BasicStroke(1f));
            c.drawRoundRect(30, 30, 300, 300, 14, 14);

            // Subtle radial filigree, not a castle or empty square.
            c.setColor(new Color(255, 239, 200, 55));
            c.setStroke(new BasicStroke(1.3f));
            for (int i = 0; i < 12; i++) {
                double angle = Math.PI * i / 6.0;
                int dx = (int)(49 * Math.cos(angle));
                int dy = (int)(49 * Math.sin(angle));
                c.drawOval(180 + dx - 42, 180 + dy - 42, 84, 84);
            }
            c.setColor(new Color(255, 240, 204, 90));
            c.drawOval(123, 123, 114, 114);
            c.setColor(new Color(242, 210, 148));
            c.fillOval(171, 171, 18, 18);
            c.setColor(new Color(255, 251, 230));
            c.fillOval(176, 175, 5, 5);

            // Four recessed, glassy docking sockets aligned with BoardGeometry.
            for (int px : new int[]{90, 270}) {
                for (int py : new int[]{90, 270}) {
                    c.setColor(new Color(38, 28, 21, 75));
                    c.fillOval(px - 35, py - 32, 70, 70);
                    c.setPaint(new GradientPaint(px - 29, py - 29,
                            new Color(246, 218, 154), px + 29, py + 29,
                            new Color(126, 84, 36)));
                    c.fillOval(px - 33, py - 34, 66, 66);
                    c.setPaint(new GradientPaint(px - 24, py - 26,
                            mix(accent, Color.WHITE, .30), px + 23, py + 25,
                            mix(accent, new Color(36, 29, 25), .55)));
                    c.fillOval(px - 27, py - 28, 54, 54);
                    c.setColor(new Color(255, 245, 209, 135));
                    c.setStroke(new BasicStroke(1.5f));
                    c.drawArc(px - 24, py - 25, 48, 48, 40, 120);
                }
            }
            // Engraved brass corner flourishes.
            c.setColor(new Color(255, 222, 156, 210));
            c.setStroke(new BasicStroke(2.4f));
            for (int sx : new int[]{1, -1}) {
                for (int sy : new int[]{1, -1}) {
                    int xx = sx == 1 ? 38 : 322;
                    int yy = sy == 1 ? 38 : 322;
                    c.drawLine(xx, yy, xx + sx * 20, yy);
                    c.drawLine(xx, yy, xx, yy + sy * 20);
                    c.fillOval(xx - 3, yy - 3, 6, 6);
                }
            }
            c.dispose();
        }
    }

    private static Color royal(PlayerColor color) {
        return switch (color) {
            case GREEN -> new Color(24, 105, 76);
            case YELLOW -> new Color(184, 137, 38);
            case RED -> new Color(145, 32, 56);
            case BLUE -> new Color(27, 73, 145);
        };
    }

    private static Color mix(Color a, Color b, double t) {
        return new Color((int)(a.getRed() * (1-t) + b.getRed() * t),
                (int)(a.getGreen() * (1-t) + b.getGreen() * t),
                (int)(a.getBlue() * (1-t) + b.getBlue() * t));
    }

    private void paintTrack(Graphics2D g2) {
        List<GridPoint> track = BoardGeometry.track();
        for (int index = 0; index < track.size(); index++) {
            PlayerColor start = BoardGeometry.startingColor(index);
            Color fill = start == null ? new Color(246, 238, 217) : royal(start);
            fillCell(g2, track.get(index), fill, new Color(194, 152, 77));
            if (start != null) {
                Color marker = start == PlayerColor.YELLOW ? new Color(29, 24, 22) : new Color(255, 231, 167);
                drawMarker(g2, track.get(index), "★", marker);
            }
            PlayerColor arrow = BoardGeometry.arrowColor((index + 1) % track.size());
            if (arrow != null) {
                drawArrow(g2, track.get(index), arrow);
            }
        }
    }

    private void paintHomeLanes(Graphics2D g2) {
        for (PlayerColor color : PlayerColor.values()) {
            List<GridPoint> lane = BoardGeometry.homeLane(color);
            for (int index = 0; index < lane.size(); index++) {
                Color fill = index == lane.size() - 1 ? royal(color) : mix(royal(color), Color.WHITE, .28);
                fillCell(g2, lane.get(index), fill, new Color(194, 152, 77));
            }
        }
    }

    /** Four enamel triangles and a crowned gold medallion at the finish. */
    private void paintCenter(Graphics2D g2) {
        // The full 3x3 central square (not only the single middle cell).
        // BoardGeometry stays unchanged; this is purely a visual overlay.
        int x = originX + 6 * cell;
        int y = originY + 6 * cell;
        int size = 3 * cell;
        int cx = x + size / 2;
        int cy = y + size / 2;

        Graphics2D c = (Graphics2D) g2.create();
        c.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        c.setColor(new Color(83, 53, 24, 65));
        c.fillRect(x + 2, y + 3, size, size);

        Color[] colors = {
            royal(PlayerColor.GREEN), royal(PlayerColor.YELLOW),
            royal(PlayerColor.BLUE), royal(PlayerColor.RED)
        };
        int[][] edges = {
            {x, y, x + size, y},
            {x + size, y, x + size, y + size},
            {x + size, y + size, x, y + size},
            {x, y + size, x, y}
        };
        for (int i = 0; i < 4; i++) {
            Path2D triangle = new Path2D.Double();
            triangle.moveTo(edges[i][0], edges[i][1]);
            triangle.lineTo(edges[i][2], edges[i][3]);
            triangle.lineTo(cx, cy);
            triangle.closePath();
            c.setPaint(new GradientPaint(edges[i][0], edges[i][1],
                    mix(colors[i], Color.WHITE, .25), cx, cy,
                    mix(colors[i], Color.BLACK, .25)));
            c.fill(triangle);
        }

        float border = Math.max(1.5f, size * .012f);
        c.setStroke(new BasicStroke(border));
        c.setColor(new Color(248, 217, 144));
        c.drawLine(x, y, x + size, y + size);
        c.drawLine(x + size, y, x, y + size);
        c.drawRect(x, y, size - 1, size - 1);

        int radius = Math.max(12, (int) (size * .18));
        c.setColor(new Color(45, 26, 17, 80));
        c.fillOval(cx - radius + 2, cy - radius + 4, 2 * radius, 2 * radius);
        c.setPaint(new GradientPaint(cx - radius, cy - radius,
                new Color(255, 246, 199), cx + radius, cy + radius,
                new Color(159, 103, 33)));
        c.fillOval(cx - radius, cy - radius, 2 * radius, 2 * radius);
        int inner = radius - Math.max(3, size / 30);
        c.setPaint(new GradientPaint(cx - inner, cy - inner,
                new Color(42, 41, 37), cx + inner, cy + inner,
                new Color(11, 19, 22)));
        c.fillOval(cx - inner, cy - inner, inner * 2, inner * 2);

        Graphics2D crown = (Graphics2D) c.create();
        crown.translate(cx, cy + size * .012);
        crown.scale(size / 210.0, size / 210.0);
        Path2D shape = new Path2D.Double();
        shape.moveTo(-17, -7);
        shape.lineTo(-20, -16);
        shape.lineTo(-10, -11);
        shape.lineTo(-6, -20);
        shape.lineTo(0, -11);
        shape.lineTo(6, -20);
        shape.lineTo(10, -11);
        shape.lineTo(20, -16);
        shape.lineTo(17, -7);
        shape.lineTo(14, 9);
        shape.lineTo(-14, 9);
        shape.closePath();
        crown.setPaint(new GradientPaint(-14, -18, new Color(255, 246, 182),
                16, 10, new Color(194, 127, 39)));
        crown.fill(shape);
        crown.setColor(new Color(255, 236, 158));
        crown.setStroke(new BasicStroke(1.3f));
        crown.draw(shape);
        crown.setPaint(new GradientPaint(-15, 8, new Color(255, 241, 176),
                15, 15, new Color(158, 98, 31)));
        crown.fillRoundRect(-17, 7, 34, 7, 3, 3);
        for (int jewelX : new int[]{-9, 0, 9}) {
            crown.setColor(jewelX == 0 ? new Color(173, 40, 59)
                    : new Color(32, 118, 117));
            crown.fillOval(jewelX - 2, -3, 4, 4);
        }
        crown.dispose();
        c.dispose();
    }

    private void paintPieces(Graphics2D g2) {
        Map<GridPoint, List<Piece>> grouped = groupPieces();
        for (List<Piece> stack : grouped.values()) {
            for (int index = 0; index < stack.size(); index++) {
                paintPiece(g2, stack.get(index), centerOf(stack, index));
            }
        }
    }

    /** Rounded, polished 3-D chess pawn with a golden base and spherical head. */
    private void paintPiece(Graphics2D g2, Piece piece, Point center) {
        Color base = royal(piece.getOwner().getColor());
        Graphics2D p = (Graphics2D) g2.create();
        p.translate(center.x, center.y);
        p.scale(cell / 60.0, cell / 60.0);
        p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        p.setColor(new Color(73, 51, 31, 80));
        p.fillOval(-20, 19, 43, 12);
        p.setPaint(new GradientPaint(-19, 13, new Color(255, 238, 187),
                19, 27, new Color(153, 101, 44)));
        p.fillRoundRect(-19, 14, 38, 13, 9, 9);
        p.setColor(new Color(129, 83, 37));
        p.setStroke(new BasicStroke(1.4f));
        p.drawRoundRect(-19, 14, 38, 13, 9, 9);
        Path2D body = new Path2D.Double();
        body.moveTo(-7, -9);
        body.curveTo(-5, 0, -12, 9, -15, 16);
        body.curveTo(-8, 21, 8, 21, 15, 16);
        body.curveTo(12, 9, 5, 0, 7, -9);
        body.closePath();
        p.setPaint(new GradientPaint(-13, -8, mix(base, Color.WHITE, .58),
                16, 16, mix(base, new Color(47, 35, 31), .32)));
        p.fill(body);
        p.setColor(new Color(245, 212, 151));
        p.setStroke(new BasicStroke(1.8f));
        p.draw(body);
        p.setPaint(new GradientPaint(-12, -28, mix(base, Color.WHITE, .75),
                12, -8, mix(base, new Color(43, 29, 26), .30)));
        p.fillOval(-12, -29, 24, 24);
        p.setColor(new Color(248, 220, 157));
        p.setStroke(new BasicStroke(1.8f));
        p.drawOval(-12, -29, 24, 24);
        p.setColor(new Color(255, 255, 255, 170));
        p.fillOval(-7, -24, 7, 5);
        if (highlights.contains(piece)) {
            p.setColor(new Color(255, 207, 78, 90));
            p.fillOval(-27, -35, 54, 68);
            p.setColor(new Color(205, 146, 54));
            p.setStroke(new BasicStroke(3.1f));
            p.drawRoundRect(-25, -34, 50, 66, 15, 15);
        }
        p.dispose();
    }

    private void fillCell(Graphics2D g2, GridPoint point, Color fill, Color border) {
        int x = originX + point.column() * cell + 2;
        int y = originY + point.row() * cell + 2;
        int size = Math.max(1, cell - 4);
        int arc = Math.max(5, cell / 6);
        g2.setColor(new Color(125, 93, 53, 45));
        g2.fillRoundRect(x + 1, y + 3, size, size, arc, arc);
        g2.setPaint(new GradientPaint(x, y, mix(fill, Color.WHITE, .36),
                x + size, y + size, mix(fill, new Color(111, 81, 54), .14)));
        g2.fillRoundRect(x, y, size, size, arc, arc);
        g2.setColor(new Color(177, 141, 86, 155));
        g2.setStroke(new BasicStroke(Math.max(1f, cell*.023f)));
        g2.drawRoundRect(x, y, size, size, arc, arc);
        if (size > 12) {
            g2.setColor(new Color(255, 255, 255, 145));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x + 3, y + 3, size - 6, size - 6, arc, arc);
        }
    }

    private void drawMarker(Graphics2D g2, GridPoint point, String text, Color color) {
        g2.setColor(color);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, cell / 2)));
        int width = g2.getFontMetrics().stringWidth(text);
        int x = originX + point.column() * cell + (cell - width) / 2;
        int y = originY + point.row() * cell + (cell + g2.getFontMetrics().getAscent()) / 2 - 2;
        g2.drawString(text, x, y);
    }

    private void drawArrow(Graphics2D g2, GridPoint point, PlayerColor color) {
        int cx = originX + point.column() * cell + cell / 2;
        int cy = originY + point.row() * cell + cell / 2;
        int arm = Math.max(4, cell / 5);
        Path2D arrow = new Path2D.Double();
        switch (color) {
            case RED -> {
                arrow.moveTo(cx, cy - arm);
                arrow.lineTo(cx - arm, cy + arm);
                arrow.lineTo(cx + arm, cy + arm);
            }
            case GREEN -> {
                arrow.moveTo(cx + arm, cy);
                arrow.lineTo(cx - arm, cy - arm);
                arrow.lineTo(cx - arm, cy + arm);
            }
            case YELLOW -> {
                arrow.moveTo(cx, cy + arm);
                arrow.lineTo(cx - arm, cy - arm);
                arrow.lineTo(cx + arm, cy - arm);
            }
            case BLUE -> {
                arrow.moveTo(cx - arm, cy);
                arrow.lineTo(cx + arm, cy - arm);
                arrow.lineTo(cx + arm, cy + arm);
            }
        }
        arrow.closePath();
        g2.setColor(awt(color).darker());
        g2.fill(arrow);
    }

    private Map<GridPoint, List<Piece>> groupPieces() {
        Map<GridPoint, List<Piece>> grouped = new LinkedHashMap<>();
        for (Piece piece : allPieces()) {
            grouped.computeIfAbsent(BoardGeometry.cellFor(piece), ignored -> new ArrayList<>()).add(piece);
        }
        return grouped;
    }

    private Point centerOf(List<Piece> stack, int index) {
        Piece piece = stack.get(index);
        GridPoint point = BoardGeometry.cellFor(piece);
        int baseX = originX + point.column() * cell + cell / 2;
        int baseY = originY + point.row() * cell + cell / 2;
        if (stack.size() == 1) {
            return new Point(baseX, baseY);
        }
        int spread = Math.max(3, cell / 6);
        int offset = (index - (stack.size() - 1) / 2) * spread;
        return new Point(baseX + offset, baseY + (index % 2 == 0 ? -offset / 2 : offset / 2));
    }

    private Piece pieceAt(int mouseX, int mouseY) {
        updateLayout();
        Map<GridPoint, List<Piece>> grouped = groupPieces();
        Piece closest = null;
        double best = Double.MAX_VALUE;
        double limit = cell * 0.42;
        for (List<Piece> stack : grouped.values()) {
            for (int index = 0; index < stack.size(); index++) {
                Point center = centerOf(stack, index);
                double distance = center.distance(mouseX, mouseY);
                if (distance <= limit && distance < best) {
                    best = distance;
                    closest = stack.get(index);
                }
            }
        }
        return closest;
    }

    private List<Piece> allPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (Player player : players) {
            pieces.addAll(player.getPieces());
        }
        return pieces;
    }

    private static Color awt(PlayerColor color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    private static Color lighten(Color color) {
        return new Color(
                Math.min(255, color.getRed() + 110),
                Math.min(255, color.getGreen() + 110),
                Math.min(255, color.getBlue() + 90));
    }
}
