package Model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Checks player count, names, and colours before an Easy game can start.
 */
public final class GameConfigValidator {

    private GameConfigValidator() {
    }

    public static ValidationResult validate(GameConfig config) {
        if (config == null) {
            return ValidationResult.of(List.of("Game settings are missing."));
        }

        List<String> errors = new ArrayList<>();
        int count = config.getPlayerCount();
        if (count < 2 || count > 4) {
            errors.add("Choose 2, 3, or 4 players.");
        }
        
        Set<String> seenNames = new LinkedHashSet<>();
        Set<PlayerColor> seen = new LinkedHashSet<>();
        Set<PlayerColor> duplicates = new LinkedHashSet<>();
        for (int index = 0; index < config.getSlots().size(); index++) {
            GameConfig.Slot slot = config.getSlots().get(index);
            int playerNumber = index + 1;
            String name = slot == null ? null : slot.getName();
            if (name == null || name.trim().isEmpty()) {
                errors.add("Player " + playerNumber + " needs a name.");
            } else if (!seenNames.add(name.trim().toLowerCase(Locale.ROOT))) {
                errors.add("Player " + playerNumber + " needs a different name.");
            }
            PlayerColor color = slot == null ? null : slot.getColor();
            if (color == null) {
                errors.add("Player " + playerNumber + " needs a color.");
            } else if (!seen.add(color)) {
                duplicates.add(color);
            }
        }
        for (PlayerColor color : duplicates) {
            errors.add(color.getDisplayName() + " is selected more than once. Each player needs a different color.");
        }
        return ValidationResult.of(errors);
    }
}
