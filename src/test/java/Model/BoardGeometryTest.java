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
        assertEquals(51, BoardGeometry.arrowIndex(PlayerColor.RED));
    }

    private static boolean onBoard(GridPoint cell) {
        return cell.column() >= 0 && cell.column() < BoardGeometry.GRID_SIZE
                && cell.row() >= 0 && cell.row() < BoardGeometry.GRID_SIZE;
    }
}
