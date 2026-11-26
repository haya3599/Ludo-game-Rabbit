package Model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Defines the shared construction process for Medium and Hard boards.
 * Subclasses provide the difficulty-specific geometry.
 */
abstract class BoardLayoutTemplate {

    final BoardGeometry build(Random random) {
        int offset = (gridSize() - BoardGeometry.GRID_SIZE) / 2;

        Map<PlayerColor, List<GridPoint>> homeLanes =
                new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, List<GridPoint>> castleSlots =
                new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, GridPoint> yards =
                new EnumMap<>(PlayerColor.class);

        Map<PlayerColor, PlayerColor> easyColors = Map.of(
                PlayerColor.RED, PlayerColor.GREEN,
                PlayerColor.GREEN, PlayerColor.YELLOW,
                PlayerColor.YELLOW, PlayerColor.BLUE,
                PlayerColor.BLUE, PlayerColor.RED);

        for (PlayerColor color : PlayerColor.values()) {
            PlayerColor easyColor = easyColors.get(color);

            homeLanes.put(color, BoardGeometry.homeLane(easyColor).stream()
                    .map(point -> shift(point, offset)).toList());

            castleSlots.put(color, BoardGeometry.castleSlots(easyColor).stream()
                    .map(point -> shift(point, offset)).toList());

            yards.put(color,
                    shift(BoardGeometry.yardOrigin(easyColor), offset));
        }

        BoardGeometry board = new BoardGeometry(
                difficulty(),
                gridSize(),
                trackRings(),
                startCells(),
                arrowCells(),
                homeLanes,
                castleSlots,
                yards);

        board.placeSpecialSquares(random);
        return board;
    }

    protected abstract Difficulty difficulty();

    protected abstract int gridSize();

    protected abstract List<List<GridPoint>> trackRings();

    protected abstract Map<PlayerColor, GridPoint> startCells();

    protected abstract Map<PlayerColor, List<GridPoint>> arrowCells();

    protected static GridPoint shift(GridPoint point, int offset) {
        return new GridPoint(
                point.column() + offset,
                point.row() + offset);
    }

    protected static List<GridPoint> perimeter(int gridSize) {
        List<GridPoint> cells = new ArrayList<>();
        int last = gridSize - 1;

        for (int i = 0; i < last; i++) {
            cells.add(new GridPoint(i, 0));
        }
        for (int i = 0; i < last; i++) {
            cells.add(new GridPoint(last, i));
        }
        for (int i = last; i > 0; i--) {
            cells.add(new GridPoint(i, last));
        }
        for (int i = last; i > 0; i--) {
            cells.add(new GridPoint(0, i));
        }

        return cells;
    }
}