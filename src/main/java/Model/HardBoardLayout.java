package Model;

import java.util.List;
import java.util.Map;

/** Provides the existing Hard geometry to the board construction template. */
final class HardBoardLayout extends BoardLayoutTemplate {

    @Override
    protected Difficulty difficulty() {
        return Difficulty.HARD;
    }

    @Override
    protected int gridSize() {
        return BoardGeometry.GRID_SIZE + 4;
    }

    @Override
    protected List<List<GridPoint>> trackRings() {
        List<GridPoint> middleTrack =
                perimeter(BoardGeometry.GRID_SIZE + 2).stream()
                        .map(point -> shift(point, 1))
                        .toList();

        List<GridPoint> innerTrack = BoardGeometry.track().stream()
                .map(point -> shift(point, 2))
                .toList();

        return List.of(perimeter(gridSize()), middleTrack, innerTrack);
    }

    @Override
    protected Map<PlayerColor, GridPoint> startCells() {
        return Map.of(
                PlayerColor.RED, new GridPoint(0, 0),
                PlayerColor.GREEN, new GridPoint(18, 0),
                PlayerColor.YELLOW, new GridPoint(18, 18),
                PlayerColor.BLUE, new GridPoint(0, 18));
    }

    @Override
    protected Map<PlayerColor, List<GridPoint>> arrowCells() {
        return Map.of(
                PlayerColor.RED, List.of(
                        new GridPoint(0, 7),
                        new GridPoint(1, 8),
                        new GridPoint(2, 9)),
                PlayerColor.GREEN, List.of(
                        new GridPoint(11, 0),
                        new GridPoint(10, 1),
                        new GridPoint(9, 2)),
                PlayerColor.YELLOW, List.of(
                        new GridPoint(18, 11),
                        new GridPoint(17, 10),
                        new GridPoint(16, 9)),
                PlayerColor.BLUE, List.of(
                        new GridPoint(7, 18),
                        new GridPoint(8, 17),
                        new GridPoint(9, 16)));
    }
}