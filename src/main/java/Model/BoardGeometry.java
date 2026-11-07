package Model;

import java.util.List;
import java.util.Map;

/**
 * Easy-board geometry used by movement and by the Swing view.
 *
 * <p>The course images were not stored in this repository. This class follows
 * the written Easy specification: a 15 by 15 cross, a clockwise 52-square main
 * track, and six home-lane squares for each colour. Start squares are 13
 * apart. Red begins at the bottom-left of the cross, then Green, Yellow, and
 * Blue. The arrow square is the last main-track square of a colour's route,
 * immediately before that colour would return to its start. The next six steps
 * are the home lane. Verify these coordinates against the SRS figures if the
 * course later supplies them in the repository.
 */
public final class BoardGeometry {

    public static final int GRID_SIZE = 15;
    public static final int YARD_SIZE = 6;

    private static final List<GridPoint> TRACK = List.of(
            new GridPoint(6, 13), new GridPoint(6, 12), new GridPoint(6, 11), new GridPoint(6, 10), new GridPoint(6, 9),
            new GridPoint(5, 8), new GridPoint(4, 8), new GridPoint(3, 8), new GridPoint(2, 8), new GridPoint(1, 8), new GridPoint(0, 8),
            new GridPoint(0, 7), new GridPoint(0, 6),
            new GridPoint(1, 6), new GridPoint(2, 6), new GridPoint(3, 6), new GridPoint(4, 6), new GridPoint(5, 6),
            new GridPoint(6, 5), new GridPoint(6, 4), new GridPoint(6, 3), new GridPoint(6, 2), new GridPoint(6, 1), new GridPoint(6, 0),
            new GridPoint(7, 0), new GridPoint(8, 0),
            new GridPoint(8, 1), new GridPoint(8, 2), new GridPoint(8, 3), new GridPoint(8, 4), new GridPoint(8, 5),
            new GridPoint(9, 6), new GridPoint(10, 6), new GridPoint(11, 6), new GridPoint(12, 6), new GridPoint(13, 6), new GridPoint(14, 6),
            new GridPoint(14, 7), new GridPoint(14, 8),
            new GridPoint(13, 8), new GridPoint(12, 8), new GridPoint(11, 8), new GridPoint(10, 8), new GridPoint(9, 8),
            new GridPoint(8, 9), new GridPoint(8, 10), new GridPoint(8, 11), new GridPoint(8, 12), new GridPoint(8, 13), new GridPoint(8, 14),
            new GridPoint(7, 14), new GridPoint(6, 14)
    );

    private static final Map<PlayerColor, List<GridPoint>> HOME_LANES = Map.of(
            PlayerColor.RED, List.of(
                    new GridPoint(7, 13), new GridPoint(7, 12), new GridPoint(7, 11),
                    new GridPoint(7, 10), new GridPoint(7, 9), new GridPoint(7, 8)),
            PlayerColor.GREEN, List.of(
                    new GridPoint(1, 7), new GridPoint(2, 7), new GridPoint(3, 7),
                    new GridPoint(4, 7), new GridPoint(5, 7), new GridPoint(6, 7)),
            PlayerColor.YELLOW, List.of(
                    new GridPoint(7, 1), new GridPoint(7, 2), new GridPoint(7, 3),
                    new GridPoint(7, 4), new GridPoint(7, 5), new GridPoint(7, 6)),
            PlayerColor.BLUE, List.of(
                    new GridPoint(13, 7), new GridPoint(12, 7), new GridPoint(11, 7),
                    new GridPoint(10, 7), new GridPoint(9, 7), new GridPoint(8, 7))
    );

    private static final Map<PlayerColor, List<GridPoint>> CASTLE_SLOTS = Map.of(
            PlayerColor.RED, List.of(new GridPoint(1, 10), new GridPoint(4, 10), new GridPoint(1, 13), new GridPoint(4, 13)),
            PlayerColor.GREEN, List.of(new GridPoint(1, 1), new GridPoint(4, 1), new GridPoint(1, 4), new GridPoint(4, 4)),
            PlayerColor.YELLOW, List.of(new GridPoint(10, 1), new GridPoint(13, 1), new GridPoint(10, 4), new GridPoint(13, 4)),
            PlayerColor.BLUE, List.of(new GridPoint(10, 10), new GridPoint(13, 10), new GridPoint(10, 13), new GridPoint(13, 13))
    );

    private static final Map<PlayerColor, GridPoint> YARD_ORIGINS = Map.of(
            PlayerColor.GREEN, new GridPoint(0, 0),
            PlayerColor.YELLOW, new GridPoint(9, 0),
            PlayerColor.RED, new GridPoint(0, 9),
            PlayerColor.BLUE, new GridPoint(9, 9)
    );

    private BoardGeometry() {
    }

    public static List<GridPoint> track() {
        return TRACK;
    }

    public static List<GridPoint> homeLane(PlayerColor color) {
        return HOME_LANES.get(color);
    }

    public static List<GridPoint> castleSlots(PlayerColor color) {
        return CASTLE_SLOTS.get(color);
    }

    public static GridPoint yardOrigin(PlayerColor color) {
        return YARD_ORIGINS.get(color);
    }

    public static PlayerColor startingColor(int trackIndex) {
        for (PlayerColor color : PlayerColor.values()) {
            if (color.getStartIndex() == trackIndex) {
                return color;
            }
        }
        return null;
    }

    public static int arrowIndex(PlayerColor color) {
        return (color.getStartIndex() + Piece.TRACK_LENGTH - 1) % Piece.TRACK_LENGTH;
    }

    public static PlayerColor arrowColor(int trackIndex) {
        for (PlayerColor color : PlayerColor.values()) {
            if (arrowIndex(color) == trackIndex) {
                return color;
            }
        }
        return null;
    }

    public static GridPoint cellFor(Piece piece) {
        PlayerColor color = piece.getOwner().getColor();
        return switch (piece.getLocation()) {
            case CASTLE -> castleSlots(color).get(piece.getIndex());
            case TRACK -> TRACK.get(piece.getTrackIndex());
            case HOME -> homeLane(color).get(piece.getHomeIndex());
        };
    }
}
