package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns a valid configuration into players, each with four castle pieces.
 */
public final class MatchSetup {

    private MatchSetup() {
    }

    public static List<Player> createPlayers(GameConfig config) {
        ValidationResult result = GameConfigValidator.validate(config);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getMessage());
        }
        List<Player> players = new ArrayList<>();
        for (GameConfig.Slot slot : config.getSlots()) {
            players.add(new Player(slot.getName().trim(), slot.getColor()));
        }
        return List.copyOf(players);
    }
}
