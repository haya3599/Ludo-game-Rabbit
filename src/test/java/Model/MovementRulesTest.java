
package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementRulesTest {

    @Test
    void castleExitIsRejectedUnlessTheDieIsSix() {
        Piece piece = new Player("Noa", PlayerColor.RED).getPieces().get(0);

        assertFalse(MovementRules.isLegal(piece, 1));
        assertFalse(MovementRules.isLegal(piece, 5));
        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.apply(piece, 5));
        assertEquals(PieceLocation.CASTLE, piece.getLocation());
    }

    @Test
    void castleExitLandsOnTheStartSquareWhenTheDieIsSix() {
        Piece piece = new Player("Noa", PlayerColor.RED).getPieces().get(0);

        assertTrue(MovementRules.isLegal(piece, 6));
        MovementRules.apply(piece, 6);

        assertEquals(PieceLocation.TRACK, piece.getLocation());
        assertEquals(0, piece.getProgress());
        assertEquals(PlayerColor.RED.getStartIndex(), piece.getTrackIndex());
    }

    @Test
    void legalMovementAdvancesAlongTheRouteAndIntoTheHomeLane() {
        Piece piece = new Player("Noa", PlayerColor.GREEN).getPieces().get(1);
        piece.placeAtProgress(0);

        MovementRules.apply(piece, 3);

        assertEquals(PieceLocation.TRACK, piece.getLocation());
        assertEquals(3, piece.getProgress());
        assertEquals(PlayerColor.GREEN.getStartIndex() + 3,
                piece.getTrackIndex());

        piece.placeAtProgress(50);
        MovementRules.apply(piece, 4);

        assertEquals(PieceLocation.HOME, piece.getLocation());
        assertEquals(2, piece.getHomeIndex());
    }

    @Test
    void movementPastTheFinalHomeSquareIsRejected() {
        Piece piece = new Player("Noa", PlayerColor.BLUE).getPieces().get(0);
        piece.placeAtProgress(55);

        assertFalse(MovementRules.isLegal(piece, 6));
        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.apply(piece, 6));
        assertEquals(55, piece.getProgress());

        assertTrue(MovementRules.isLegal(piece, 2));
        MovementRules.apply(piece, 2);

        assertEquals(Piece.MAX_PROGRESS, piece.getProgress());
        assertEquals(5, piece.getHomeIndex());
    }

    // Additional test: reject invalid dice values.
    @Test
    void invalidDiceValuesAreRejected() {
        Piece piece = new Player("Noa", PlayerColor.RED).getPieces().get(0);

        assertFalse(MovementRules.isLegal(piece, 0));
        assertFalse(MovementRules.isLegal(piece, 7));

        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.apply(piece, 0));
        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.apply(piece, 7));

        assertEquals(PieceLocation.CASTLE, piece.getLocation());
        assertEquals(-1, piece.getProgress());
    }

    // Additional test: a piece at the final square cannot move again.
    @Test
    void finishedPieceCannotMoveAgain() {
        Piece piece = new Player("Noa", PlayerColor.GREEN).getPieces().get(0);
        piece.placeAtProgress(Piece.MAX_PROGRESS);

        assertFalse(MovementRules.isLegal(piece, 1));
        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.apply(piece, 1));

        assertEquals(Piece.MAX_PROGRESS, piece.getProgress());
        assertEquals(PieceLocation.HOME, piece.getLocation());
        assertEquals(5, piece.getHomeIndex());
    }
}
