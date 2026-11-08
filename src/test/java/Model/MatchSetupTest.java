package Model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
        @Test
    void newGameCreatesIndependentPlayersAndPieces() {
        GameConfig config = new GameConfig(List.of(
                new GameConfig.Slot("Tareza", PlayerColor.RED),
                new GameConfig.Slot("Haya", PlayerColor.BLUE)));

        List<Player> firstGame = MatchSetup.createPlayers(config);

        // Move a piece in the first game before creating another game.
        firstGame.get(0).getPieces().get(0).exitCastleToStart();

        List<Player> secondGame = MatchSetup.createPlayers(config);

        assertEquals(2, secondGame.size());

        for (int i = 0; i < secondGame.size(); i++) {
            Player firstPlayer = firstGame.get(i);
            Player secondPlayer = secondGame.get(i);

            assertNotSame(firstPlayer, secondPlayer);
            assertEquals(firstPlayer.getName(), secondPlayer.getName());
            assertEquals(firstPlayer.getColor(), secondPlayer.getColor());
            assertEquals(4, secondPlayer.getPieces().size());

            for (int j = 0; j < 4; j++) {
                Piece newPiece = secondPlayer.getPieces().get(j);

                assertNotSame(firstPlayer.getPieces().get(j), newPiece);
                assertEquals(secondPlayer, newPiece.getOwner());
                assertEquals(PieceLocation.CASTLE, newPiece.getLocation());
                assertEquals(-1, newPiece.getProgress());
            }
        }

        // Starting the second game must not reset the first game's piece.
        assertEquals(
                PieceLocation.TRACK,
                firstGame.get(0).getPieces().get(0).getLocation());
    } 
}
