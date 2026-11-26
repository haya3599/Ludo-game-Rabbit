package Model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardGeometryTest {

    @Test
    void easyTrackHasFiftyTwoDistinctSquares() {
        List<GridPoint> track = BoardGeometry.track();
        assertEquals(52, track.size());
        assertEquals(52, new HashSet<>(track).size());
        for (GridPoint cell : track) {
            assertTrue(onBoard(cell));
        }
    }

    @Test
    void eachColorHasSixHomeSquaresOutsideTheMainTrack() {
        Set<GridPoint> track = new HashSet<>(BoardGeometry.track());
        for (PlayerColor color : PlayerColor.values()) {
            List<GridPoint> home = BoardGeometry.homeLane(color);
            assertEquals(6, home.size());
            assertEquals(6, new HashSet<>(home).size());
            for (GridPoint cell : home) {
                assertTrue(onBoard(cell));
                assertFalse(track.contains(cell), color + " home overlaps the track at " + cell);
            }
            assertEquals(4, BoardGeometry.castleSlots(color).size());
        }
    }

    @Test
    void startSquaresAreThirteenApart() {
        assertEquals(0, PlayerColor.RED.getStartIndex());
        assertEquals(13, PlayerColor.GREEN.getStartIndex());
        assertEquals(26, PlayerColor.YELLOW.getStartIndex());
        assertEquals(39, PlayerColor.BLUE.getStartIndex());
        assertNotNull(BoardGeometry.startingColor(0));
        assertEquals(PlayerColor.RED, BoardGeometry.startingColor(0));
        assertEquals(50, BoardGeometry.arrowIndex(PlayerColor.RED));
    }

    @Test
    void everyArrowLeadsStraightIntoItsHomeLane() {
        for (PlayerColor color : PlayerColor.values()) {
            Piece piece = new Player("Player", color).getPieces().get(0);
            piece.placeAtProgress(Piece.HOME_ENTRY_PROGRESS - 2);
            MovementRules.apply(piece, 1);
            GridPoint arrow = BoardGeometry.cellFor(piece);
            assertEquals(BoardGeometry.track().get(BoardGeometry.arrowIndex(color)), arrow);
            MovementRules.apply(piece, 1);
            GridPoint home = BoardGeometry.cellFor(piece);
            assertEquals(PieceLocation.HOME, piece.getLocation());
            assertEquals(0, piece.getHomeIndex());
            assertEquals(1, Math.abs(arrow.column() - home.column())
                    + Math.abs(arrow.row() - home.row()), color.toString());
            assertEquals(BoardGeometry.homeLane(color).get(0), home);
        }
    }

    @Test
    void fourStarsOccupyTheUncoloredSquaresInTheReference() {
        var expected = java.util.Map.of(
                8, new GridPoint(2, 8), 21, new GridPoint(6, 2),
                34, new GridPoint(12, 6), 47, new GridPoint(8, 12));
        int stars = 0;
        for (int index = 0; index < Piece.TRACK_LENGTH; index++) {
            if (BoardGeometry.starColor(index) == null) continue;
            stars++;
            assertEquals(expected.get(index), BoardGeometry.track().get(index));
            assertEquals(null, BoardGeometry.startingColor(index));
            assertTrue(BoardGeometry.isProtected(index));
        }
        assertEquals(4, stars);
    }

    private static boolean onBoard(GridPoint cell) {
        return cell.column() >= 0 && cell.column() < BoardGeometry.GRID_SIZE
                && cell.row() >= 0 && cell.row() < BoardGeometry.GRID_SIZE;
    }
}
