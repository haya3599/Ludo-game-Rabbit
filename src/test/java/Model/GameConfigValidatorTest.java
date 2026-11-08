package Model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameConfigValidatorTest {

    @Test
    void validPlayerCountsAreAccepted() {
        assertTrue(GameConfigValidator.validate(config(2)).isValid());
        assertTrue(GameConfigValidator.validate(config(3)).isValid());
        assertTrue(GameConfigValidator.validate(config(4)).isValid());
    }

    @Test
    void invalidPlayerCountsAreRejected() {
        assertFalse(GameConfigValidator.validate(config(0)).isValid());
        assertFalse(GameConfigValidator.validate(config(1)).isValid());
        assertFalse(GameConfigValidator.validate(config(5)).isValid());
        assertTrue(GameConfigValidator.validate(config(1)).getMessage().contains("2, 3, or 4"));
    }

    @Test
    void emptyNamesAreRejected() {
        GameConfig config = new GameConfig(List.of(
                new GameConfig.Slot("   ", PlayerColor.RED),
                new GameConfig.Slot("Noa", PlayerColor.BLUE)));

        ValidationResult result = GameConfigValidator.validate(config);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Player 1 needs a name."));
    }

    @Test
    void duplicateColorsAreRejected() {
        GameConfig config = new GameConfig(List.of(
                new GameConfig.Slot("Noa", PlayerColor.RED),
                new GameConfig.Slot("Sam", PlayerColor.RED)));

        ValidationResult result = GameConfigValidator.validate(config);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Red is selected more than once"));
    }
    
    @Test
    void missingPlayerIsRejectedWithoutCrashing() {
        List<GameConfig.Slot> slots = new ArrayList<>();
        slots.add(new GameConfig.Slot("Tareza", PlayerColor.RED));
        slots.add(null);

        ValidationResult result =
                GameConfigValidator.validate(new GameConfig(slots));

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Player 2 needs a name."));
        assertTrue(result.getMessage().contains("Player 2 needs a color."));
    }

    private static GameConfig config(int playerCount) {
        PlayerColor[] colors = PlayerColor.values();
        List<GameConfig.Slot> slots = new ArrayList<>();
        for (int index = 0; index < playerCount; index++) {
            PlayerColor color = colors[index % colors.length];
            if (playerCount <= colors.length) {
                color = colors[index];
            }
            slots.add(new GameConfig.Slot("Player " + (index + 1), color));
        }
        return new GameConfig(slots);
    }
}
