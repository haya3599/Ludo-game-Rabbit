package Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameFlowTest {

    @Test
    void gameStartsWithFourCastlePiecesAndTheFirstPlayer() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(1));

        assertEquals(2, game.getPlayers().size());
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertEquals(1, game.getTurnNumber());
        for (Player player : game.getPlayers()) {
            assertEquals(4, player.getPieces().size());
            for (Piece piece : player.getPieces()) {
                assertEquals(PieceLocation.CASTLE, piece.getLocation());
            }
        }
    }

    @Test
    void castleExitOnSixMovesTheSelectedPieceAndKeepsTheTurn() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(6, 3));
        Piece piece = game.getCurrentPlayer().getPieces().get(2);

        RollOutcome roll = game.roll();

        assertTrue(roll.isAccepted());
        assertTrue(roll.isMovePending());
        assertEquals(6, roll.getValue());
        assertEquals(6, game.getLastRoll());
        assertEquals(ActionResult.OK, game.move(piece));
        assertEquals(PieceLocation.TRACK, piece.getLocation());
        assertEquals(0, piece.getProgress());
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertEquals(1, game.getTurnNumber());
        assertFalse(game.isMovePending());

        RollOutcome extraRoll = game.roll();
        assertTrue(extraRoll.isAccepted());
        assertTrue(extraRoll.isMovePending());
        assertEquals(3, extraRoll.getValue());
        assertEquals(ActionResult.OK, game.move(piece));
        assertEquals(3, piece.getProgress());
        assertEquals("Sam", game.getCurrentPlayer().getName());
        assertEquals(2, game.getTurnNumber());
    }

    @Test
    void aRollOtherThanSixDoesNotReleaseACastlePiece() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(4));
        Piece piece = game.getPlayers().get(0).getPieces().get(0);

        RollOutcome roll = game.roll();

        assertTrue(roll.isAccepted());
        assertFalse(roll.isMovePending());
        assertEquals(4, roll.getValue());
        assertEquals(PieceLocation.CASTLE, piece.getLocation());
        assertEquals("Sam", game.getCurrentPlayer().getName());
        assertEquals(ActionResult.NO_PENDING_MOVE, game.move(piece));
    }

    @Test
    void repeatedRollingIsRejectedWhileAMoveIsPending() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(6, 1));

        game.roll();
        RollOutcome again = game.roll();

        assertFalse(again.isAccepted());
        assertEquals(6, game.getLastRoll());
        assertTrue(game.isMovePending());
        assertEquals("Noa", game.getCurrentPlayer().getName());
    }

    @Test
    void invalidSelectionDoesNotMoveThePieceOrEndTheTurn() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(6));
        Piece stuck = game.getCurrentPlayer().getPieces().get(3);
        stuck.placeAtProgress(Piece.MAX_PROGRESS);
        game.roll();

        ActionResult result = game.move(stuck);

        assertEquals(ActionResult.ILLEGAL_MOVE, result);
        assertEquals(Piece.MAX_PROGRESS, stuck.getProgress());
        assertTrue(game.isMovePending());
        assertEquals("Noa", game.getCurrentPlayer().getName());
    }

    @Test
    void aPlayerCannotMoveAnotherPlayersPiece() {
        Game game = Game.start(players("Noa", PlayerColor.YELLOW, "Sam", PlayerColor.GREEN), Dice.scripted(6));
        game.roll();
        Piece opponentPiece = game.getPlayers().get(1).getPieces().get(0);

        ActionResult result = game.move(opponentPiece);

        assertEquals(ActionResult.NOT_YOUR_PIECE, result);
        assertEquals(PieceLocation.CASTLE, opponentPiece.getLocation());
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertTrue(game.isMovePending());
    }

    @Test
    void legalMovementChangesTheSelectedPiece() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(3));
        Piece piece = game.getCurrentPlayer().getPieces().get(0);
        piece.placeAtProgress(2);

        RollOutcome roll = game.roll();
        assertTrue(roll.isMovePending());
        assertEquals(ActionResult.OK, game.move(piece));

        assertEquals(5, piece.getProgress());
        assertEquals(PlayerColor.RED.getStartIndex() + 5, piece.getTrackIndex());
        assertEquals("Sam", game.getCurrentPlayer().getName());
    }

    @Test
    void turnProgressionFollowsThePlayerOrder() {
        Game game = Game.start(
                players("Yara", PlayerColor.YELLOW, "Rami", PlayerColor.RED, "Gadi", PlayerColor.GREEN),
                Dice.scripted(1, 2, 3, 4));

        assertEquals("Yara", game.getCurrentPlayer().getName());
        assertEquals(1, game.getTurnNumber());

        game.roll();
        assertEquals("Rami", game.getCurrentPlayer().getName());
        assertEquals(2, game.getTurnNumber());

        game.roll();
        assertEquals("Gadi", game.getCurrentPlayer().getName());
        assertEquals(3, game.getTurnNumber());

        game.roll();
        assertEquals("Yara", game.getCurrentPlayer().getName());
        assertEquals(4, game.getTurnNumber());
    }

    @Test
    void landingOnSingleOpponentCapturesAndResetsItsRoute() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(1));
        Piece attacker = game.getPlayers().get(0).getPieces().get(0);
        Piece opponent = game.getPlayers().get(1).getPieces().get(0);
        attacker.placeAtProgress(2);
        opponent.placeAtProgress(16); // Both land at absolute track index 3.
        game.roll();
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(PieceLocation.CASTLE, opponent.getLocation());
        assertEquals(-1, opponent.getProgress());
        assertFalse(MovementRules.isLegal(opponent, 1));
        MovementRules.apply(opponent, 6);
        assertEquals(PlayerColor.BLUE.getStartIndex(), opponent.getTrackIndex());
    }

    @Test
    void starsProtectOpponentsAndPassingDoesNotCapture() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(2));
        Piece attacker = game.getPlayers().get(0).getPieces().get(0);
        Piece passed = game.getPlayers().get(1).getPieces().get(0);
        Piece protectedPiece = game.getPlayers().get(1).getPieces().get(1);
        attacker.placeAtProgress(6);
        passed.placeAtProgress(20); // Absolute index 7, crossed by the move.
        protectedPiece.placeAtProgress(21); // Star at absolute index 8.
        game.roll();
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(20, passed.getProgress());
        assertEquals(21, protectedPiece.getProgress());
        for (PlayerColor color : PlayerColor.values()) {
            assertTrue(BoardGeometry.isProtected((color.getStartIndex() + 8) % Piece.TRACK_LENGTH));
            assertFalse(BoardGeometry.isProtected(color.getStartIndex()));
        }
    }

    @Test
    void friendlyPiecesAndMultipleOpponentsAreNotCaptured() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(1));
        Piece attacker = game.getPlayers().get(0).getPieces().get(0);
        Piece friend = game.getPlayers().get(0).getPieces().get(1);
        attacker.placeAtProgress(2);
        friend.placeAtProgress(3);
        for (Piece opponent : game.getPlayers().get(1).getPieces()) opponent.placeAtProgress(16);
        game.roll();
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(3, friend.getProgress());
        for (Piece opponent : game.getPlayers().get(1).getPieces()) assertEquals(16, opponent.getProgress());
    }

    @Test
    void homeLanesOfDifferentColorsDoNotCaptureEachOther() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(1));
        Piece attacker = game.getPlayers().get(0).getPieces().get(0);
        Piece opponent = game.getPlayers().get(1).getPieces().get(0);
        attacker.placeAtProgress(Piece.HOME_ENTRY_PROGRESS - 1);
        opponent.placeAtProgress(Piece.HOME_ENTRY_PROGRESS);
        game.roll();
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(PieceLocation.HOME, opponent.getLocation());
        assertEquals(PieceLocation.HOME, attacker.getLocation());
    }

    @Test
    void gamesEndOnlyAfterAllButOnePlayerFinishAndRejectFurtherPlay() {
        for (int count = 2; count <= 4; count++) {
            var slots = new java.util.ArrayList<GameConfig.Slot>();
            PlayerColor[] colors = PlayerColor.values();
            for (int i = 0; i < count; i++) slots.add(new GameConfig.Slot("P" + i, colors[i]));
            Game game = Game.start(new GameConfig(slots), Dice.scripted(6, 6, 6));
            for (int i = 0; i < count - 1; i++) {
                Player player = game.getPlayers().get(i);
                assertEquals(player, game.getCurrentPlayer());
                for (Piece piece : player.getPieces()) piece.placeAtProgress(Piece.MAX_PROGRESS);
                Piece last = player.getPieces().get(3);
                last.placeAtProgress(Piece.MAX_PROGRESS - 6);
                assertFalse(player.isFinished());
                assertTrue(game.roll().isMovePending());
                assertEquals(ActionResult.OK, game.move(last));
                assertTrue(player.isFinished());
                assertEquals(i == count - 2, game.isOver());
            }
            assertEquals(game.getPlayers(), game.getRankings());
            int turn = game.getTurnNumber();
            assertFalse(game.roll().isAccepted());
            assertEquals(ActionResult.GAME_OVER, game.move(game.getPlayers().get(count - 1).getPieces().get(0)));
            assertEquals(turn, game.getTurnNumber());
            assertTrue(game.getLegalPieces().isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void finalRankingsFollowFinishingOrderWhenPlayersFinishInReverseTurnOrder(int count) {
        List<GameConfig.Slot> slots = players(
                "A", PlayerColor.RED, "B", PlayerColor.BLUE,
                "C", PlayerColor.GREEN, "D", PlayerColor.YELLOW).getSlots();
        Game game = Game.start(new GameConfig(slots.subList(0, count)),
                Dice.scripted(1, 1, 1, 1, 1, 1, 1, 1, 1));
        List<Player> players = game.getPlayers();
        List<Player> expectedRankings = new ArrayList<>(players);
        Collections.reverse(expectedRankings);

        for (int index = 0; index < count; index++) {
            Player player = players.get(index);
            List<Piece> pieces = player.getPieces();
            for (Piece piece : pieces.subList(0, Player.PIECE_COUNT - 1)) {
                piece.placeAtProgress(Piece.MAX_PROGRESS);
            }
            // Each later player needs one fewer turn to finish their last piece.
            Piece last = pieces.get(Player.PIECE_COUNT - 1);
            last.placeAtProgress(Piece.MAX_PROGRESS - (count - index));
            assertEquals(PieceLocation.HOME, last.getLocation());
            assertFalse(player.isFinished());
        }
        assertTrue(game.getRankings().isEmpty());

        for (int active = count; active > 1; active--) {
            for (int index = 0; index < active; index++) {
                Player player = players.get(index);
                assertEquals(player, game.getCurrentPlayer());
                assertTrue(game.roll().isMovePending());
                Piece last = player.getPieces().get(Player.PIECE_COUNT - 1);
                assertEquals(ActionResult.OK, game.move(last));
                assertEquals(index == active - 1, player.isFinished());
            }
            assertEquals(active == 2, game.isOver());
            if (active > 2) {
                assertEquals(expectedRankings.subList(0, count - active + 1), game.getRankings());
            }
        }

        assertEquals(expectedRankings, game.getRankings());
        assertFalse(players.get(0).isFinished());
    }

    @Test
    void finishedPlayersAreSkippedWhenTurnOrderWraps() {
        Game game = Game.start(players("A", PlayerColor.RED, "B", PlayerColor.BLUE, "C", PlayerColor.GREEN),
                Dice.scripted(1, 1, 1));
        Player first = game.getCurrentPlayer();
        for (Piece piece : first.getPieces()) piece.placeAtProgress(Piece.MAX_PROGRESS);
        Piece last = first.getPieces().get(0);
        last.placeAtProgress(Piece.MAX_PROGRESS - 1);
        game.roll();
        game.move(last);
        assertFalse(game.isOver());
        assertEquals("B", game.getCurrentPlayer().getName());
        game.roll();
        game.roll();
        assertEquals("B", game.getCurrentPlayer().getName());
        assertEquals(List.of(first), game.getRankings());
    }

    @Test
    void landingOnANewStarDoesNotCaptureAnOpponent() {
        Game game = Game.start(players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE), Dice.scripted(1));
        Piece attacker = game.getPlayers().get(0).getPieces().get(0);
        Piece opponent = game.getPlayers().get(1).getPieces().get(0);
        attacker.placeAtProgress(7);
        opponent.placeAtProgress(21); // Absolute index 8: the red star.
        game.roll();
        assertEquals(ActionResult.OK, game.move(attacker));
        assertEquals(8, attacker.getTrackIndex());
        assertEquals(PieceLocation.TRACK, opponent.getLocation());
        assertEquals(8, opponent.getTrackIndex());
    }

    @Test
    void hashStartSquaresAllowCapturesForEveryColor() {
        PlayerColor[] colors = PlayerColor.values();
        for (int i = 0; i < colors.length; i++) {
            PlayerColor defenderColor = colors[i];
            PlayerColor attackerColor = colors[(i + 1) % colors.length];
            Game game = Game.start(players("Attacker", attackerColor, "Defender", defenderColor), Dice.scripted(1));
            Piece attacker = game.getPlayers().get(0).getPieces().get(0);
            Piece defender = game.getPlayers().get(1).getPieces().get(0);
            int distance = Math.floorMod(defenderColor.getStartIndex() - attackerColor.getStartIndex(), Piece.TRACK_LENGTH);
            attacker.placeAtProgress(distance - 1);
            defender.placeAtProgress(0);
            game.roll();
            assertEquals(ActionResult.OK, game.move(attacker));
            assertEquals(defenderColor.getStartIndex(), attacker.getTrackIndex());
            assertEquals(PieceLocation.CASTLE, defender.getLocation());
        }
    }

    private static GameConfig players(Object... nameAndColor) {
        if (nameAndColor.length % 2 != 0) {
            throw new IllegalArgumentException("Expected name and colour pairs.");
        }
        var slots = new java.util.ArrayList<GameConfig.Slot>();
        for (int index = 0; index < nameAndColor.length; index += 2) {
            slots.add(new GameConfig.Slot((String) nameAndColor[index], (PlayerColor) nameAndColor[index + 1]));
        }
        return new GameConfig(List.copyOf(slots));
    }
}
