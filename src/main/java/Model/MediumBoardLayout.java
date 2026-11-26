package Model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Provides the existing Medium geometry to the board construction template. */
final class MediumBoardLayout extends BoardLayoutTemplate {

    @Override
    protected Difficulty difficulty() {
        return Difficulty.MEDIUM;
    }

    @Override
    protected int gridSize() {
        return BoardGeometry.GRID_SIZE + 2;
    }

    @Override
    protected List<List<GridPoint>> trackRings() {
        List<GridPoint> innerTrack = BoardGeometry.track().stream()
                .map(point -> shift(point, 1))
                .toList();

        return List.of(perimeter(gridSize()), innerTrack);
    }

    @Override
    protected Map<PlayerColor, GridPoint> startCells() {
        return Map.of(
                PlayerColor.RED, new GridPoint(0, 0),
                PlayerColor.GREEN, new GridPoint(16, 0),
                PlayerColor.YELLOW, new GridPoint(16, 16),
                PlayerColor.BLUE, new GridPoint(0, 16));
    }

    @Override
    protected Map<PlayerColor, List<GridPoint>> arrowCells() {
        Map<PlayerColor, GridPoint> outerArrows = Map.of(
                PlayerColor.RED, new GridPoint(0, 7),
                PlayerColor.GREEN, new GridPoint(9, 0),
                PlayerColor.YELLOW, new GridPoint(16, 9),
                PlayerColor.BLUE, new GridPoint(7, 16));

        Map<PlayerColor, PlayerColor> easyColors = Map.of(
                PlayerColor.RED, PlayerColor.GREEN,
                PlayerColor.GREEN, PlayerColor.YELLOW,
                PlayerColor.YELLOW, PlayerColor.BLUE,
                PlayerColor.BLUE, PlayerColor.RED);

        Map<PlayerColor, List<GridPoint>> arrows =
                new EnumMap<>(PlayerColor.class);

        for (PlayerColor color : PlayerColor.values()) {
            PlayerColor easyColor = easyColors.get(color);
            GridPoint innerArrow = shift(
                    BoardGeometry.track().get(
                            BoardGeometry.arrowIndex(easyColor)),
                    1);

            arrows.put(color, List.of(outerArrows.get(color), innerArrow));
        }

        return arrows;
    }
}