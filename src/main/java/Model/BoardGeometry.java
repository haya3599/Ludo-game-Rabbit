package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Immutable board geometry and piece routes for one match. */
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

    private static final BoardGeometry EASY = createEasy();

    private final Difficulty difficulty;
    private final int gridSize;
    private final List<GridPoint> track;
    private final Map<PlayerColor, List<GridPoint>> homeLanes;
    private final Map<PlayerColor, List<GridPoint>> castleSlots;
    private final Map<PlayerColor, GridPoint> yardOrigins;
    private final Map<PlayerColor, List<Integer>> routes = new EnumMap<>(PlayerColor.class);
    private final Map<Integer, PlayerColor> starts = new HashMap<>();
    private final Map<Integer, Arrow> arrows = new HashMap<>();
    private final Map<Integer, SpecialSquare> specialSquares = new HashMap<>();

    private BoardGeometry(Difficulty difficulty, int gridSize, List<List<GridPoint>> rings,
                          Map<PlayerColor, GridPoint> startCells, Map<PlayerColor, List<GridPoint>> arrowCells,
                          Map<PlayerColor, List<GridPoint>> homeLanes, Map<PlayerColor, List<GridPoint>> castleSlots,
                          Map<PlayerColor, GridPoint> yardOrigins) {
        this.difficulty = difficulty;
        this.gridSize = gridSize;
        this.track = rings.stream().flatMap(List::stream).toList();
        this.homeLanes = Map.copyOf(homeLanes);
        this.castleSlots = Map.copyOf(castleSlots);
        this.yardOrigins = Map.copyOf(yardOrigins);

        for (PlayerColor color : PlayerColor.values()) {
            GridPoint entry = startCells.get(color);
            starts.put(track.indexOf(entry), color);
            List<GridPoint> exits = arrowCells.get(color);
            GridPoint innerArrow = exits.get(exits.size() - 1);
            GridPoint firstHome = homeLanes.get(color).get(0);
            int dx = firstHome.column() - innerArrow.column();
            int dy = firstHome.row() - innerArrow.row();
            List<Integer> route = new ArrayList<>();
            int offset = 0;
            for (int ringIndex = 0; ringIndex < rings.size(); ringIndex++) {
                List<GridPoint> ring = rings.get(ringIndex);
                GridPoint exit = exits.get(ringIndex);
                int first = ring.indexOf(entry);
                int last = ring.indexOf(exit);
                if (first < 0 || last < 0) {
                    throw new IllegalArgumentException("Start or arrow does not connect to the track.");
                }
                int steps = Math.floorMod(last - first, ring.size());
                for (int step = 0; step <= steps; step++) {
                    route.add(offset + (first + step) % ring.size());
                }
                entry = new GridPoint(exit.column() + dx, exit.row() + dy);
                arrows.put(offset + last, new Arrow(color, entry));
                offset += ring.size();
            }
            routes.put(color, List.copyOf(route));
        }
    }

    static BoardGeometry forDifficulty(Difficulty difficulty, Random random) {
        return switch (difficulty) {
            case EASY -> EASY;
            case MEDIUM -> createMedium(random);
            case HARD -> throw new IllegalArgumentException("The Hard board layout is unavailable.");
        };
    }

    private static BoardGeometry createEasy() {
        Map<PlayerColor, GridPoint> starts = new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, List<GridPoint>> arrows = new EnumMap<>(PlayerColor.class);
        for (PlayerColor color : PlayerColor.values()) {
            starts.put(color, TRACK.get(color.getStartIndex()));
            arrows.put(color, List.of(TRACK.get(arrowIndex(color))));
        }
        BoardGeometry board = new BoardGeometry(Difficulty.EASY, GRID_SIZE, List.of(TRACK), starts, arrows,
                HOME_LANES, CASTLE_SLOTS, YARD_ORIGINS);
        for (int index = 0; index < TRACK.size(); index++) {
            if (starColor(index) != null) {
                board.specialSquares.put(index, SpecialSquare.STAR);
            }
        }
        return board;
    }

    private static BoardGeometry createMedium(Random random) {
        // Ludo Project, p. 2: a 64-square perimeter surrounds the 15 by 15 inner board.
        int gridSize = GRID_SIZE + 2;
        Map<PlayerColor, GridPoint> starts = Map.of(
                PlayerColor.RED, new GridPoint(0, 0), PlayerColor.GREEN, new GridPoint(16, 0),
                PlayerColor.YELLOW, new GridPoint(16, 16), PlayerColor.BLUE, new GridPoint(0, 16));
        Map<PlayerColor, GridPoint> outerArrows = Map.of(
                PlayerColor.RED, new GridPoint(0, 7), PlayerColor.GREEN, new GridPoint(9, 0),
                PlayerColor.YELLOW, new GridPoint(16, 9), PlayerColor.BLUE, new GridPoint(7, 16));

        // The Medium figure assigns the inner yards clockwise from red at the top left.
        Map<PlayerColor, PlayerColor> easyColors = Map.of(
                PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.YELLOW,
                PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.BLUE, PlayerColor.RED);
        Map<PlayerColor, List<GridPoint>> arrows = new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, List<GridPoint>> homeLanes = new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, List<GridPoint>> castleSlots = new EnumMap<>(PlayerColor.class);
        Map<PlayerColor, GridPoint> yards = new EnumMap<>(PlayerColor.class);
        for (PlayerColor color : PlayerColor.values()) {
            PlayerColor easyColor = easyColors.get(color);
            arrows.put(color, List.of(outerArrows.get(color), inset(TRACK.get(arrowIndex(easyColor)))));
            homeLanes.put(color, HOME_LANES.get(easyColor).stream().map(BoardGeometry::inset).toList());
            castleSlots.put(color, CASTLE_SLOTS.get(easyColor).stream().map(BoardGeometry::inset).toList());
            yards.put(color, inset(YARD_ORIGINS.get(easyColor)));
        }
        List<GridPoint> innerTrack = TRACK.stream().map(BoardGeometry::inset).toList();
        BoardGeometry board = new BoardGeometry(Difficulty.MEDIUM, gridSize, List.of(perimeter(gridSize), innerTrack),
                starts, arrows, homeLanes, castleSlots, yards);
        board.placeSpecialSquares(random);
        return board;
    }

    private static GridPoint inset(GridPoint point) {
        return new GridPoint(point.column() + 1, point.row() + 1);
    }

    private static List<GridPoint> perimeter(int gridSize) {
        List<GridPoint> cells = new ArrayList<>();
        int last = gridSize - 1;
        for (int i = 0; i < last; i++) cells.add(new GridPoint(i, 0));
        for (int i = 0; i < last; i++) cells.add(new GridPoint(last, i));
        for (int i = last; i > 0; i--) cells.add(new GridPoint(i, last));
        for (int i = last; i > 0; i--) cells.add(new GridPoint(0, i));
        return cells;
    }

    private void placeSpecialSquares(Random random) {
        List<Integer> available = new ArrayList<>();
        for (int index = 0; index < track.size(); index++) {
            if (!starts.containsKey(index) && !arrows.containsKey(index)) {
                available.add(index);
            }
        }
        Collections.shuffle(available, random);
        List<SpecialSquare> required = new ArrayList<>(Collections.nCopies(8, SpecialSquare.STAR));
        for (SpecialSquare square : List.of(SpecialSquare.EASY_QUESTION, SpecialSquare.MEDIUM_QUESTION,
                SpecialSquare.HARD_QUESTION, SpecialSquare.SURPRISE)) {
            required.addAll(Collections.nCopies(2, square));
        }
        for (int i = 0; i < required.size(); i++) {
            specialSquares.put(available.get(i), required.get(i));
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<GridPoint> getTrack() {
        return track;
    }

    public List<GridPoint> getHomeLane(PlayerColor color) {
        return homeLanes.get(color);
    }

    public List<GridPoint> getCastleSlots(PlayerColor color) {
        return castleSlots.get(color);
    }

    public GridPoint getYardOrigin(PlayerColor color) {
        return yardOrigins.get(color);
    }

    public PlayerColor getStartingColor(int trackIndex) {
        return starts.get(trackIndex);
    }

    public Arrow getArrow(int trackIndex) {
        return arrows.get(trackIndex);
    }

    public SpecialSquare getSpecialSquare(int trackIndex) {
        return specialSquares.get(trackIndex);
    }

    boolean isProtectedSquare(int trackIndex) {
        SpecialSquare square = getSpecialSquare(trackIndex);
        return square == SpecialSquare.STAR || square == SpecialSquare.SURPRISE
                || (difficulty == Difficulty.MEDIUM && starts.containsKey(trackIndex));
    }

    int getTrackIndex(PlayerColor color, int progress) {
        return routes.get(color).get(progress);
    }

    int getHomeEntryProgress(PlayerColor color) {
        return routes.get(color).size();
    }

    int getMaxProgress(PlayerColor color) {
        return getHomeEntryProgress(color) + getHomeLane(color).size() - 1;
    }

    public record Arrow(PlayerColor color, GridPoint destination) { }

    public enum SpecialSquare {
        STAR, EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION, SURPRISE
    }

    // The original static accessors describe the Iteration 1 Easy board.
    public static BoardGeometry easy() {
        return EASY;
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
        return (color.getStartIndex() + Piece.HOME_ENTRY_PROGRESS - 1) % Piece.TRACK_LENGTH;
    }

    public static PlayerColor starColor(int trackIndex) {
        for (PlayerColor color : PlayerColor.values()) {
            if ((color.getStartIndex() + 8) % Piece.TRACK_LENGTH == trackIndex) {
                return color;
            }
        }
        return null;
    }

    /** Only star squares protect pieces; the # start markers do not. */
    public static boolean isProtected(int trackIndex) {
        return starColor(trackIndex) != null;
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
        Player owner = piece.getOwner();
        BoardGeometry geometry = owner.getBoardGeometry();
        PlayerColor color = owner.getColor();
        return switch (piece.getLocation()) {
            case CASTLE -> geometry.getCastleSlots(color).get(piece.getIndex());
            case TRACK -> geometry.getTrack().get(piece.getTrackIndex());
            case HOME -> geometry.getHomeLane(color).get(piece.getHomeIndex());
        };
    }
}
