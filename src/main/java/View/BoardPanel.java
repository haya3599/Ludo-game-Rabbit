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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
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
        setPreferredSize(new Dimension(680, 680));
        setBackground(new Color(244, 232, 208));
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
        paintYards(g2);
        paintTrack(g2);
        paintHomeLanes(g2);
        paintCenter(g2);
        paintPieces(g2);
        g2.dispose();
    }

    private void updateLayout() {
        int available = Math.min(getWidth(), getHeight()) - 20;
        cell = Math.max(12, available / BoardGeometry.GRID_SIZE);
        int used = cell * BoardGeometry.GRID_SIZE;
        originX = (getWidth() - used) / 2;
        originY = (getHeight() - used) / 2;
    }

    private void paintYards(Graphics2D g2) {
        for (PlayerColor color : PlayerColor.values()) {
            GridPoint origin = BoardGeometry.yardOrigin(color);
            int x = originX + origin.column() * cell;
            int y = originY + origin.row() * cell;
            int size = cell * BoardGeometry.YARD_SIZE;
            Color paint = awt(color);
            g2.setColor(paint);
            g2.fillRoundRect(x + 4, y + 4, size - 8, size - 8, 28, 28);
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRoundRect(x + 18, y + 18, size - 36, size - 36, 22, 22);
            g2.setColor(UiKit.INK);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, cell / 2)));
            String name = color.getDisplayName();
            int textWidth = g2.getFontMetrics().stringWidth(name);
            g2.drawString(name, x + (size - textWidth) / 2, y + size / 2 + cell / 5);
        }
    }

    private void paintTrack(Graphics2D g2) {
        List<GridPoint> track = BoardGeometry.track();
        for (int index = 0; index < track.size(); index++) {
            PlayerColor start = BoardGeometry.startingColor(index);
            Color fill = start == null ? new Color(255, 252, 246) : awt(start);
            fillCell(g2, track.get(index), fill, new Color(90, 78, 60));
            if (start != null) {
                Color marker = start == PlayerColor.YELLOW ? UiKit.INK : Color.WHITE;
                drawMarker(g2, track.get(index), "#", marker);
            }
            PlayerColor arrow = BoardGeometry.arrowColor(index);
            if (arrow != null) {
                drawArrow(g2, track.get(index), arrow);
            }
        }
    }

    private void paintHomeLanes(Graphics2D g2) {
        for (PlayerColor color : PlayerColor.values()) {
            List<GridPoint> lane = BoardGeometry.homeLane(color);
            for (int index = 0; index < lane.size(); index++) {
                Color fill = index == lane.size() - 1 ? awt(color) : lighten(awt(color));
                fillCell(g2, lane.get(index), fill, awt(color).darker());
            }
        }
    }

    private void paintCenter(Graphics2D g2) {
        int x = originX + 7 * cell;
        int y = originY + 7 * cell;
        Path2D diamond = new Path2D.Double();
        diamond.moveTo(x + cell / 2.0, y + 4);
        diamond.lineTo(x + cell - 4, y + cell / 2.0);
        diamond.lineTo(x + cell / 2.0, y + cell - 4);
        diamond.lineTo(x + 4, y + cell / 2.0);
        diamond.closePath();
        g2.setColor(UiKit.GOLD);
        g2.fill(diamond);
        g2.setColor(UiKit.NAVY);
        g2.setStroke(new BasicStroke(1.6f));
        g2.draw(diamond);
    }

    private void paintPieces(Graphics2D g2) {
        Map<GridPoint, List<Piece>> grouped = groupPieces();
        for (List<Piece> stack : grouped.values()) {
            for (int index = 0; index < stack.size(); index++) {
                paintPiece(g2, stack.get(index), centerOf(stack, index));
            }
        }
    }

    private void paintPiece(Graphics2D g2, Piece piece, Point center) {
        int diameter = Math.max(12, (int) (cell * 0.68));
        int x = center.x - diameter / 2;
        int y = center.y - diameter / 2;
        Color color = awt(piece.getOwner().getColor());
        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillOval(x + 2, y + 3, diameter, diameter);
        g2.setColor(color);
        g2.fillOval(x, y, diameter, diameter);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x, y, diameter, diameter);
        if (highlights.contains(piece)) {
            g2.setColor(new Color(255, 214, 64));
            g2.setStroke(new BasicStroke(3.2f));
            g2.drawOval(x - 3, y - 3, diameter + 6, diameter + 6);
        }
    }

    private void fillCell(Graphics2D g2, GridPoint point, Color fill, Color border) {
        int x = originX + point.column() * cell + 2;
        int y = originY + point.row() * cell + 2;
        int size = cell - 4;
        g2.setColor(fill);
        g2.fillRoundRect(x, y, size, size, 8, 8);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, size, size, 8, 8);
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
