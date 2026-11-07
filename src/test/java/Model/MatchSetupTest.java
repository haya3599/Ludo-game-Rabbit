package Model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchSetupTest {

    @Test
    void initializationCreatesFourCastlePiecesPerPlayer() {
        GameConfig config = new GameConfig(List.of(
                new GameConfig.Slot(" Noa ", PlayerColor.RED),
                new GameConfig.Slot("Sam", PlayerColor.BLUE),
                new GameConfig.Slot("Gadi", PlayerColor.GREEN)));

        List<Player> players = MatchSetup.createPlayers(config);

        assertEquals(3, players.size());
        assertEquals("Noa", players.get(0).getName());
        for (Player player : players) {
            assertEquals(Player.PIECE_COUNT, player.getPieces().size());
            for (Piece piece : player.getPieces()) {
                assertEquals(PieceLocation.CASTLE, piece.getLocation());
                assertEquals(player, piece.getOwner());
            }
        }
    }

    @Test
    void invalidConfigurationIsNotStarted() {
        GameConfig config = new GameConfig(List.of(
                new GameConfig.Slot("", PlayerColor.YELLOW)));

        assertThrows(IllegalArgumentException.class, () -> MatchSetup.createPlayers(config));
    }
}
