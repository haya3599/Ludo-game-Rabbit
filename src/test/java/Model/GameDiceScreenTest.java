package Model;

import Controller.GameplayController;
import View.BoardPanel;
import View.DiceFacePanel;
import View.GameScreen;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameDiceScreenTest {

    @Test
    void mediumRollButtonDisplaysBothDiceAndCalculatedMovementSideBySide() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Game game = start(Difficulty.MEDIUM, 4, Dice.scripted(3));
            game.getCurrentPlayer().getPieces().get(0).placeAtProgress(2);
            GameScreen screen = new GameScreen(game.getPlayers(), game.getBoardGeometry());
            try {
                new GameplayController(game, screen);
                AtomicBoolean backClicked = new AtomicBoolean();
                screen.setOnLeave(() -> backClicked.set(true));
                JButton roll = rollButton(screen);
                JButton back = components(screen, JButton.class)
                        .filter(button -> button.getText().equals("Back to Home")).findFirst().orElseThrow();

                assertEquals(2, components(screen, DiceFacePanel.class).count());
                assertTrue(label(screen, "Regular Dice").isVisible());
                assertTrue(label(screen, "Boost").isVisible());
                roll.doClick(0);
                assertFalse(roll.isEnabled());

                JLabel regular = label(screen, "Regular Dice: 4");
                JLabel boost = label(screen, "Boost: 3");
                assertEquals("Regular Dice: 4", regular.getLabelFor().getAccessibleContext().getAccessibleName());
                assertEquals("Boost: 3", boost.getLabelFor().getAccessibleContext().getAccessibleName());
                JLabel result = label(screen, "4 × (3 − 1) = 8 steps");
                JLabel elapsed = label(screen, "Elapsed Time");
                JLabel player = label(screen, "Noa");
                JLabel turn = label(screen, "Turn 1");
                JTextArea message = components(screen, JTextArea.class).findFirst().orElseThrow();
                BoardPanel board = components(screen, BoardPanel.class).findFirst().orElseThrow();

                screen.setSize(1000, 690);
                assertLayout(screen, regular, boost, result, elapsed, player, turn, message, roll, back, board);
                screen.setSize(1160, 775);
                assertLayout(screen, regular, boost, result, elapsed, player, turn, message, roll, back, board);
                back.doClick(0);
                assertTrue(backClicked.get());
            } finally {
                screen.setVisible(false);
            }
        });
    }

    @Test
    void easyRollButtonDisplaysOnlyTheRegularDie() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Game game = start(Difficulty.EASY, 6, null);
            GameScreen screen = new GameScreen(game.getPlayers());
            try {
                new GameplayController(game, screen);
                rollButton(screen).doClick(0);

                assertEquals(1, components(screen, DiceFacePanel.class).count());
                assertTrue(label(screen, "Regular Dice: 6").isVisible());
                assertFalse(components(screen, JLabel.class).anyMatch(label -> label.getText().contains("×")));
            } finally {
                screen.setVisible(false);
            }
        });
    }

    @Test
    void zeroStepRollStaysVisibleUntilTheTurnChangesThenBothDiceClear() throws Exception {
        Game game = start(Difficulty.MEDIUM, 5, Dice.scripted(1));
        CountDownLatch nextTurn = new CountDownLatch(1);
        GameScreen[] screen = new GameScreen[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                screen[0] = new GameScreen(game.getPlayers(), game.getBoardGeometry());
                new GameplayController(game, screen[0]);
                label(screen[0], "Turn 1").addPropertyChangeListener("text", event -> {
                    if ("Turn 2".equals(event.getNewValue())) nextTurn.countDown();
                });

                JButton roll = rollButton(screen[0]);
                roll.doClick(0);
                assertTrue(label(screen[0], "Regular Dice: 5").isVisible());
                assertTrue(label(screen[0], "Boost: 1").isVisible());
                assertTrue(label(screen[0], "5 × (1 − 1) = 0 steps").isVisible());
                assertTrue(label(screen[0], "Noa").isVisible());
                assertFalse(roll.isEnabled());
            });

            assertTrue(nextTurn.await(5, TimeUnit.SECONDS));
            SwingUtilities.invokeAndWait(() -> {
                assertTrue(label(screen[0], "Sam").isVisible());
                assertTrue(label(screen[0], "Regular Dice").isVisible());
                assertTrue(label(screen[0], "Boost").isVisible());
                assertFalse(components(screen[0], JLabel.class).anyMatch(label -> label.getText().contains("×")));
                assertTrue(rollButton(screen[0]).isEnabled());
            });
        } finally {
            if (screen[0] != null) SwingUtilities.invokeAndWait(() -> screen[0].setVisible(false));
        }
    }

    private static void assertLayout(GameScreen screen, JLabel regular, JLabel boost, JLabel result,
                                     JLabel elapsed, JLabel player, JLabel turn, JTextArea message,
                                     JButton roll, JButton back, BoardPanel board) {
        layout(screen);
        Rectangle regularFace = bounds(screen, regular.getLabelFor());
        Rectangle boostFace = bounds(screen, boost.getLabelFor());
        assertEquals(regularFace.y, boostFace.y);
        assertTrue(regularFace.x + regularFace.width <= boostFace.x);
        assertTrue(regularFace.y + regularFace.height <= bounds(screen, result).y);
        assertTrue(bounds(screen, back).y + back.getHeight() <= bounds(screen, board).y);

        Rectangle available = new Rectangle(screen.getSize());
        for (Component component : List.of(regular, boost, result, elapsed, player, turn, message, roll, back)) {
            Rectangle componentBounds = bounds(screen, component);
            assertTrue(available.contains(componentBounds), () -> component + " at " + componentBounds);
        }
    }

    private static Game start(Difficulty difficulty, int regularValue, Dice multiplier) {
        GameConfig config = new GameConfig(List.of(new GameConfig.Slot("Noa", PlayerColor.RED),
                new GameConfig.Slot("Sam", PlayerColor.BLUE)), difficulty);
        return Game.start(config, Dice.scripted(regularValue), multiplier);
    }

    private static JButton rollButton(GameScreen screen) {
        List<JButton> buttons = components(screen, JButton.class)
                .filter(button -> button.getText().endsWith("Roll Dice")).toList();
        assertEquals(1, buttons.size());
        return buttons.get(0);
    }

    private static JLabel label(GameScreen screen, String text) {
        return components(screen, JLabel.class).filter(label -> text.equals(label.getText()))
                .findFirst().orElseThrow(() -> new AssertionError("Label missing: " + text));
    }

    private static Rectangle bounds(JComponent root, Component child) {
        return SwingUtilities.convertRectangle(child.getParent(), child.getBounds(), root);
    }

    private static <T extends Component> Stream<T> components(Container parent, Class<T> type) {
        return Arrays.stream(parent.getComponents()).flatMap(component -> {
            Stream<T> match = type.isInstance(component) ? Stream.of(type.cast(component)) : Stream.empty();
            return component instanceof Container child ? Stream.concat(match, components(child, type)) : match;
        });
    }

    private static void layout(Container parent) {
        parent.doLayout();
        for (Component component : parent.getComponents()) {
            if (component instanceof Container child) layout(child);
        }
    }
}
