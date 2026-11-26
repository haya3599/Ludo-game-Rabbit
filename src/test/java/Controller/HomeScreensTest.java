package Controller;

import View.HomeScreen;
import View.HowToPlayScreen;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HomeScreensTest {

    @Test
    void opensHowToPlayReturnsHomeAndPreservesNewGameNavigation() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CardLayout layout = new CardLayout();
            JPanel cards = new JPanel(layout);
            JPanel previousSetup = new JPanel();
            cards.add(previousSetup, ScreenId.SETUP.name());
            JPanel setup = new JPanel();
            HomeScreens.register((id, screen) -> cards.add(screen, id.name()),
                    id -> layout.show(cards, id.name()),
                    () -> {
                        cards.remove(previousSetup);
                        cards.add(setup, ScreenId.SETUP.name());
                    });

            HomeScreen home = find(cards, HomeScreen.class);
            HowToPlayScreen guide = find(cards, HowToPlayScreen.class);
            layout.show(cards, ScreenId.HOME.name());
            assertTrue(home.isVisible());

            button(home, "How to Play").doClick(0);
            assertTrue(guide.isVisible());
            assertFalse(home.isVisible());
            assertFalse(previousSetup.isVisible());

            button(guide, "Back to Home").doClick(0);
            assertTrue(home.isVisible());
            assertFalse(guide.isVisible());
            assertNull(setup.getParent());

            assertEquals(1, button(home, "GAME HISTORY").getActionListeners().length);
            assertEquals(1, button(home, "QUESTION MANAGEMENT").getActionListeners().length);
            button(home, "NEW GAME").doClick(0);
            assertTrue(setup.isVisible());
            assertSame(cards, setup.getParent());
            assertNull(previousSetup.getParent());
            assertFalse(home.isVisible());
            assertFalse(guide.isVisible());
        });
    }

    @Test
    void guideShowsRequiredSectionsAndLabelsPlannedRules() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            String text = guideText(find(new HowToPlayScreen(() -> {}), JEditorPane.class));
            for (String required : List.of(
                    "Game Objective", "Starting a Game", "Basic Dice Rules", "Moving Pieces",
                    "Captures and Protection", "Difficulty Levels", "Special Squares",
                    "Winning and Final Rankings", "2–4 players", "different colour",
                    "Choose Easy or Medium, then select Start Game", "Regular 6",
                    "grants an extra roll unless you finish all four pieces", "the third roll is canceled",
                    "must match the steps still needed", "Double Block",
                    "Easy is available now", "Medium is available now", "Boost Dice (1–3)",
                    "Hard is planned", "UltraBoost Dice",
                    "Movement = Regular Dice × (Multiplier Die − 1)", "determines castle exit",
                    "A # on a coloured track square marks", "Stars protect pieces from capture on both boards",
                    "Landing on an unprotected track square", "A player finishes",
                    "only one player is still unfinished", "that player ranks last")) {
                assertTrue(text.contains(required), () -> "The guide is missing \"" + required + "\"");
            }
            assertTrue(text.contains("Planned. A Double Block"));
            assertTrue(text.contains("Planned. Question squares (?)"));
            assertTrue(text.contains("Planned. Surprise squares"));
        });
    }

    @Test
    void homeButtonAvoidsTheArtworkAndGuideScrollsAtDifferentSizes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            HomeScreen home = new HomeScreen();
            HowToPlayScreen guide = new HowToPlayScreen(() -> {});
            JButton help = button(home, "How to Play");
            JButton back = button(guide, "Back to Home");
            JScrollPane scroll = find(guide, JScrollPane.class);
            JEditorPane text = find(guide, JEditorPane.class);

            for (Dimension size : List.of(new Dimension(1000, 690), new Dimension(1160, 775),
                    new Dimension(760, 520))) {
                home.setSize(size);
                home.dispatchEvent(new ComponentEvent(home, ComponentEvent.COMPONENT_RESIZED));
                Rectangle helpBounds = help.getBounds();
                assertPositiveAndWithin(helpBounds, size);
                assertTrue(helpBounds.x > size.width * 0.75);
                assertTrue(helpBounds.y < size.height * 0.25);
                for (Component item : home.getComponents()) {
                    if (item != help) assertFalse(helpBounds.intersects(item.getBounds()));
                }

                guide.setSize(size);
                layout(guide);
                Rectangle backBounds = SwingUtilities.convertRectangle(back.getParent(), back.getBounds(), guide);
                assertPositiveAndWithin(backBounds, size);
                assertFalse(backBounds.intersects(scroll.getBounds()));
                assertTrue(scroll.getVerticalScrollBar().isVisible());
                assertTrue(text.getWidth() <= scroll.getViewport().getExtentSize().width);
                JScrollBar vertical = scroll.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                assertTrue(scroll.getViewport().getViewPosition().y > 0);
                assertEquals(text.getHeight(), scroll.getViewport().getViewRect().getMaxY());
            }
        });
    }

    private static String guideText(JEditorPane pane) {
        try {
            return pane.getDocument().getText(0, pane.getDocument().getLength()).replaceAll("\\s+", " ").trim();
        } catch (BadLocationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static void assertPositiveAndWithin(Rectangle bounds, Dimension size) {
        assertFalse(bounds.isEmpty());
        assertTrue(new Rectangle(size).contains(bounds));
    }

    private static JButton button(Container root, String text) {
        return descendants(root).filter(JButton.class::isInstance).map(JButton.class::cast)
                .filter(button -> text.equals(button.getText())).findFirst().orElseThrow();
    }

    private static <T extends JComponent> T find(Container root, Class<T> type) {
        return descendants(root).filter(type::isInstance).map(type::cast).findFirst().orElseThrow();
    }

    private static Stream<Component> descendants(Container root) {
        return Arrays.stream(root.getComponents()).flatMap(child -> child instanceof Container container
                ? Stream.concat(Stream.of(child), descendants(container)) : Stream.of(child));
    }

    private static void layout(Container parent) {
        parent.doLayout();
        for (Component child : parent.getComponents()) {
            if (child instanceof Container container) layout(container);
        }
    }
}
