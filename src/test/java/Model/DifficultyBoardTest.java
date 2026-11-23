package Model;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DifficultyBoardTest {

    private static final List<GameConfig.Slot> SLOTS = List.of(
            new GameConfig.Slot("Red player", PlayerColor.RED),
            new GameConfig.Slot("Green player", PlayerColor.GREEN),
            new GameConfig.Slot("Yellow player", PlayerColor.YELLOW),
            new GameConfig.Slot("Blue player", PlayerColor.BLUE));

    @Test
    void legacyConfigurationDefaultsToEasy() {
        GameConfig config = new GameConfig(SLOTS);
        assertEquals("Easy", config.getDifficulty());
        assertSame(BoardGeometry.easy(), Game.start(config, Dice.scripted(6)).getBoardGeometry());
        assertFalse(GameConfigValidator.validate(new GameConfig(SLOTS, null)).isValid());
    }

    @Test
    void gameStartupReportsMissingHardLayoutForAnOtherwiseValidConfiguration() {
        GameConfig hard = new GameConfig(SLOTS, Difficulty.HARD);
        ValidationResult validation = GameConfigValidator.validate(hard);
        assertFalse(validation.isValid());
        assertEquals("The Hard board layout is unavailable.", validation.getMessage());

        var failure = assertThrows(IllegalArgumentException.class, () -> Game.start(hard, Dice.scripted(6)));
        assertEquals(validation.getMessage(), failure.getMessage());
    }

    @Test
    void mediumMatchesTheReferenceTrackStartsAndArrows() {
        BoardGeometry board = medium(1);
        List<GridPoint> track = board.getTrack();
        assertEquals(17, board.getGridSize());
        assertEquals(116, track.size());
        assertEquals(116, new HashSet<>(track).size());
        assertEquals(BoardGeometry.track().stream()
                .map(point -> new GridPoint(point.column() + 1, point.row() + 1)).toList(),
                track.subList(64, 116));
        for (int index = 0; index < 64; index++) {
            GridPoint point = track.get(index);
            assertTrue(point.column() == 0 || point.column() == 16 || point.row() == 0 || point.row() == 16);
            assertEquals(1, distance(point, track.get((index + 1) % 64)));
        }
        assertEquals(Map.of(0, PlayerColor.RED, 16, PlayerColor.GREEN, 32, PlayerColor.YELLOW, 48, PlayerColor.BLUE),
                markers(board, true));
        Map<GridPoint, BoardGeometry.Arrow> arrows = Map.of(
                new GridPoint(0, 7), new BoardGeometry.Arrow(PlayerColor.RED, new GridPoint(1, 7)),
                new GridPoint(1, 8), new BoardGeometry.Arrow(PlayerColor.RED, new GridPoint(2, 8)),
                new GridPoint(9, 0), new BoardGeometry.Arrow(PlayerColor.GREEN, new GridPoint(9, 1)),
                new GridPoint(8, 1), new BoardGeometry.Arrow(PlayerColor.GREEN, new GridPoint(8, 2)),
                new GridPoint(16, 9), new BoardGeometry.Arrow(PlayerColor.YELLOW, new GridPoint(15, 9)),
                new GridPoint(15, 8), new BoardGeometry.Arrow(PlayerColor.YELLOW, new GridPoint(14, 8)),
                new GridPoint(7, 16), new BoardGeometry.Arrow(PlayerColor.BLUE, new GridPoint(7, 15)),
                new GridPoint(8, 15), new BoardGeometry.Arrow(PlayerColor.BLUE, new GridPoint(8, 14)));
        assertEquals(arrows.size(), markers(board, false).size());
        arrows.forEach((cell, arrow) -> assertEquals(arrow, board.getArrow(track.indexOf(cell)), cell.toString()));
    }

    @Test
    void mediumRandomizesTheRequiredSpecialSquaresWithoutReplacingFixedMarkers() {
        BoardGeometry first = medium(1);
        BoardGeometry second = medium(2);
        Map<BoardGeometry.SpecialSquare, Integer> counts = new EnumMap<>(BoardGeometry.SpecialSquare.class);
        Set<Integer> firstLocations = new HashSet<>();
        Set<Integer> secondLocations = new HashSet<>();
        for (int index = 0; index < first.getTrack().size(); index++) {
            assertEquals(first.getArrow(index), second.getArrow(index));
            assertEquals(first.getStartingColor(index), second.getStartingColor(index));
            if (second.getSpecialSquare(index) != null) {
                secondLocations.add(index);
            }
            var square = first.getSpecialSquare(index);
            if (square != null) {
                firstLocations.add(index);
                assertNull(first.getStartingColor(index));
                assertNull(first.getArrow(index));
                counts.merge(square, 1, Integer::sum);
                assertEquals(square == BoardGeometry.SpecialSquare.STAR || square == BoardGeometry.SpecialSquare.SURPRISE,
                        first.isProtectedSquare(index));
            }
        }
        assertEquals(Map.of(BoardGeometry.SpecialSquare.STAR, 8, BoardGeometry.SpecialSquare.EASY_QUESTION, 2,
                BoardGeometry.SpecialSquare.MEDIUM_QUESTION, 2, BoardGeometry.SpecialSquare.HARD_QUESTION, 2,
                BoardGeometry.SpecialSquare.SURPRISE, 2), counts);
        assertNotEquals(firstLocations, secondLocations);
    }

    @Test
    void mediumPiecesStartInTheCorrectCastleAndFollowTheirOwnArrowsToExactHome() {
        Game game = Game.start(new GameConfig(SLOTS, Difficulty.MEDIUM), Dice.scripted(6));
        BoardGeometry board = game.getBoardGeometry();
        assertEquals(Difficulty.MEDIUM, board.getDifficulty());
        Map<PlayerColor, GridPoint> castleOrigins = Map.of(
                PlayerColor.RED, new GridPoint(1, 1), PlayerColor.GREEN, new GridPoint(10, 1),
                PlayerColor.YELLOW, new GridPoint(10, 10), PlayerColor.BLUE, new GridPoint(1, 10));
        Map<PlayerColor, GridPoint> starts = Map.of(
                PlayerColor.RED, new GridPoint(0, 0), PlayerColor.GREEN, new GridPoint(16, 0),
                PlayerColor.YELLOW, new GridPoint(16, 16), PlayerColor.BLUE, new GridPoint(0, 16));

        for (Player player : game.getPlayers()) {
            PlayerColor color = player.getColor();
            GridPoint yard = castleOrigins.get(color);
            assertEquals(yard, board.getYardOrigin(color));
            for (Piece castlePiece : player.getPieces()) {
                assertEquals(PieceLocation.CASTLE, castlePiece.getLocation());
                int column = yard.column() + 1 + 3 * (castlePiece.getIndex() % 2);
                int row = yard.row() + 1 + 3 * (castlePiece.getIndex() / 2);
                assertEquals(new GridPoint(column, row), BoardGeometry.cellFor(castlePiece));
            }

            Piece piece = player.getPieces().get(0);
            MovementRules.apply(piece, 6);
            assertEquals(starts.get(color), BoardGeometry.cellFor(piece));
            Set<Integer> visited = new HashSet<>();
            for (int progress = 0; progress < 110; progress++) {
                assertEquals(PieceLocation.TRACK, piece.getLocation());
                int index = piece.getTrackIndex();
                assertTrue(visited.add(index), "Repeated track index " + index);
                GridPoint cell = BoardGeometry.cellFor(piece);
                var arrow = board.getArrow(index);
                MovementRules.apply(piece, 1);
                GridPoint next = BoardGeometry.cellFor(piece);
                if (arrow != null && arrow.color() == color) {
                    assertEquals(arrow.destination(), next);
                    assertEquals(1, distance(cell, next));
                } else {
                    int nextIndex = index < 64 ? (index + 1) % 64 : 64 + (index - 64 + 1) % 52;
                    assertEquals(nextIndex, piece.getTrackIndex());
                }
            }
            assertEquals(PieceLocation.HOME, piece.getLocation());
            assertEquals(0, piece.getHomeIndex());
            assertEquals(6, board.getHomeLane(color).size());
            assertFalse(MovementRules.isLegal(piece, 6));
            MovementRules.apply(piece, 5);
            assertEquals(board.getHomeLane(color).get(5), BoardGeometry.cellFor(piece));
            assertTrue(piece.isFinished());
        }
    }

    @Test
    void mediumMovesCanCrossAnArrowAndStartingSquaresPreventCapture() {
        Game game = Game.start(new GameConfig(SLOTS, Difficulty.MEDIUM), Dice.scripted(2), Dice.scripted(2));
        Piece attacker = game.getCurrentPlayer().getPieces().get(0);
        Piece defender = game.getPlayers().get(1).getPieces().get(0);
        attacker.placeAtProgress(14);
        defender.exitCastleToStart();
        assertTrue(game.roll().isMovePending());
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(defender.getTrackIndex(), attacker.getTrackIndex());
        assertEquals(PieceLocation.TRACK, defender.getLocation());

        attacker.placeAtProgress(56);
        MovementRules.apply(attacker, 3);
        assertEquals(new GridPoint(2, 7), BoardGeometry.cellFor(attacker));
        attacker.placeAtProgress(108);
        MovementRules.apply(attacker, 4);
        assertEquals(PieceLocation.HOME, attacker.getLocation());
        assertEquals(2, attacker.getHomeIndex());
    }

    private static BoardGeometry medium(long seed) {
        return BoardGeometry.forDifficulty(Difficulty.MEDIUM, new Random(seed));
    }

    private static int distance(GridPoint from, GridPoint to) {
        return Math.abs(from.column() - to.column()) + Math.abs(from.row() - to.row());
    }

    private static Map<Integer, PlayerColor> markers(BoardGeometry board, boolean starts) {
        Map<Integer, PlayerColor> marks = new java.util.HashMap<>();
        for (int index = 0; index < board.getTrack().size(); index++) {
            BoardGeometry.Arrow arrow = board.getArrow(index);
            PlayerColor color = starts ? board.getStartingColor(index) : arrow == null ? null : arrow.color();
            if (color != null) {
                marks.put(index, color);
            }
        }
        return marks;
    }
}
