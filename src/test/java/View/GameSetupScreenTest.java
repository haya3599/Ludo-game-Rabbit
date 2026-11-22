package View;

import Model.Dice;
import Model.Difficulty;
import Model.Game;
import Model.Piece;
import Model.PlayerColor;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameSetupScreenTest {

    @Test
    void clickingADifficultyStartsTheCorrespondingBoardAndPreservesPlayerDetails() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            GameSetupScreen setup = new GameSetupScreen();
            List<JTextField> names = components(setup).filter(JTextField.class::isInstance)
                    .map(JTextField.class::cast).toList();
            names.get(0).setText("Noa");
            button(setup, "3"::equals).doClick(0);
            components(setup).filter(JToggleButton.class::isInstance).map(JToggleButton.class::cast)
                    .filter(button -> "Blue".equals(button.getToolTipText())).findFirst().orElseThrow().doClick(0);

            AtomicReference<Game> started = new AtomicReference<>();
            setup.setOnStart(() -> started.set(Game.start(setup.readConfig(), Dice.scripted(6))));

            assertEquals(Difficulty.EASY, setup.readConfig().getDifficultyLevel());
            JButton easy = button(setup, "Easy"::equals);
            JButton medium = button(setup, "Medium"::equals);
            JButton hard = button(setup, "Hard"::equals);
            JButton start = button(setup, text -> text.startsWith("Start Game"));

            assertTrue(easy.isSelected());
            assertFalse(hard.isEnabled());
            medium.doClick(0);
            assertTrue(medium.isSelected());
            assertFalse(easy.isSelected());
            start.doClick(0);

            Game game = started.get();
            assertEquals(Difficulty.MEDIUM, game.getBoardGeometry().getDifficulty());
            assertEquals(3, game.getPlayers().size());
            assertEquals("Noa", game.getPlayers().get(0).getName());
            assertEquals(PlayerColor.BLUE, game.getPlayers().get(0).getColor());
            assertEquals(PlayerColor.RED, game.getPlayers().get(1).getColor());
            GameScreen screen = new GameScreen(game.getPlayers(), game.getBoardGeometry());
            try {
                assertTrue(components(screen).filter(JLabel.class::isInstance)
                        .map(JLabel.class::cast).anyMatch(label -> "MEDIUM GAME".equals(label.getText())));
                BoardPanel board = components(screen).filter(BoardPanel.class::isInstance)
                        .map(BoardPanel.class::cast).findFirst().orElseThrow();
                board.setSize(600, 600);
                Graphics2D graphics = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB).createGraphics();
                try {
                    board.paint(graphics);
                } finally {
                    graphics.dispose();
                }
                var piece = game.getCurrentPlayer().getPieces().get(0);
                var selected = new AtomicReference<Piece>();
                screen.setOnPieceSelected(selected::set);
                // First castle slot of the blue player, column 2 and row 11 on the Medium board.
                int cell = (600 - 24) / 17;
                int origin = (600 - cell * 17) / 2;
                board.dispatchEvent(new MouseEvent(board, MouseEvent.MOUSE_CLICKED, 0L, 0,
                        origin + 2 * cell + cell / 2, origin + 11 * cell + cell / 2, 1, false));
                assertSame(piece, selected.get());
            } finally {
                screen.setVisible(false);
            }

            easy.doClick(0);
            start.doClick(0);
            assertEquals(Difficulty.EASY, started.get().getBoardGeometry().getDifficulty());
        });
    }

    private static JButton button(Container root, Predicate<String> textMatches) {
        return components(root).filter(JButton.class::isInstance).map(JButton.class::cast)
                .filter(button -> textMatches.test(button.getText())).findFirst().orElseThrow();
    }

    private static Stream<Component> components(Container root) {
        return Stream.of(root.getComponents()).flatMap(child -> child instanceof Container container
                ? Stream.concat(Stream.of(child), components(container)) : Stream.of(child));
    }
}
