package Model;

import Controller.GameplayController;
import View.BoardPanel;
import View.GameScreen;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCompletionScreenTest {

    @Test
    void finalMoveShowsScrollableRankingsAndKeepsHomeButtonAvailable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            String longName = "W".repeat(80);
            Game game = Game.start(new GameConfig(List.of(
                    new GameConfig.Slot(longName, PlayerColor.BLUE),
                    new GameConfig.Slot("Noa", PlayerColor.RED))), Dice.scripted(1));
            Player winner = game.getCurrentPlayer();
            List<Piece> pieces = winner.getPieces();
            for (Piece piece : pieces.subList(0, Player.PIECE_COUNT - 1)) {
                piece.placeAtProgress(Piece.MAX_PROGRESS);
            }
            Piece last = pieces.get(Player.PIECE_COUNT - 1);
            last.placeAtProgress(Piece.MAX_PROGRESS - 1);

            GameScreen screen = new GameScreen(game.getPlayers());
            try {
                AtomicBoolean homeSelected = new AtomicBoolean();
                screen.setOnLeave(() -> homeSelected.set(true));
                new GameplayController(game, screen);

                JButton roll = descendants(screen).filter(JButton.class::isInstance)
                        .map(JButton.class::cast).filter(button -> button.getText().endsWith("Roll Dice"))
                        .findFirst().orElseThrow();
                BoardPanel board = descendants(screen).filter(BoardPanel.class::isInstance)
                        .map(BoardPanel.class::cast).findFirst().orElseThrow();
                roll.doClick(0);
                clickPiece(board, last);

                assertTrue(game.isOver());
                assertFalse(roll.isEnabled());
                screen.setSize(1000, 700);
                layout(screen);
                JScrollPane rankings = descendants(screen).filter(JScrollPane.class::isInstance)
                        .map(JScrollPane.class::cast).findFirst().orElseThrow();
                Container rows = (Container) rankings.getViewport().getView();
                assertEquals(List.of("1.  " + longName + "  (Blue) — Finished",
                                "2.  Noa  (Red) — Remaining player"),
                        descendants(rows).filter(JLabel.class::isInstance).map(JLabel.class::cast)
                                .map(JLabel::getText).toList());

                assertTrue(rankings.getHorizontalScrollBar().isVisible());
                Rectangle firstRow = rows.getComponent(0).getBounds();
                assertTrue(rankings.getViewport().getViewRect().contains(firstRow.x, firstRow.y));
                rankings.getHorizontalScrollBar().setValue(rankings.getHorizontalScrollBar().getMaximum());
                assertTrue(rankings.getViewport().getViewRect()
                        .contains(firstRow.x + firstRow.width - 1, firstRow.y));

                JButton home = descendants(screen).filter(JButton.class::isInstance)
                        .map(JButton.class::cast).filter(button -> button.getText().equals("Back to Home"))
                        .findFirst().orElseThrow();
                Rectangle homeBounds = SwingUtilities.convertRectangle(home.getParent(), home.getBounds(), screen);
                assertTrue(new Rectangle(screen.getSize()).contains(homeBounds));
                home.doClick(0);
                assertTrue(homeSelected.get());
            } finally {
                screen.setVisible(false);
            }
        });
    }

    private static void clickPiece(BoardPanel board, Piece piece) {
        int cellSize = 40;
        int boardPadding = 12;
        int size = BoardGeometry.GRID_SIZE * cellSize + boardPadding * 2;
        board.setSize(size, size);
        GridPoint square = BoardGeometry.cellFor(piece);
        int x = boardPadding + square.column() * cellSize + cellSize / 2;
        int y = boardPadding + square.row() * cellSize + cellSize / 2;
        board.dispatchEvent(new MouseEvent(board, MouseEvent.MOUSE_CLICKED, 0, 0,
                x, y, 1, false, MouseEvent.BUTTON1));
    }

    private static Stream<Component> descendants(Container parent) {
        return Arrays.stream(parent.getComponents()).flatMap(component ->
                component instanceof Container child
                        ? Stream.concat(Stream.of(component), descendants(child))
                        : Stream.of(component));
    }

    private static void layout(Container parent) {
        parent.doLayout();
        for (Component component : parent.getComponents()) {
            if (component instanceof Container child) {
                layout(child);
            }
        }
    }
}
