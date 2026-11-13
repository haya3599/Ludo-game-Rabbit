
package Model;

import org.junit.jupiter.api.Test;

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
    void castleExitOnSixGrantsExtraTurn() {
        Game game = Game.start(
            players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE),
            Dice.scripted(6, 3)
        );

        Piece piece = game.getCurrentPlayer().getPieces().get(2);

        RollOutcome roll = game.roll();

        assertTrue(roll.isAccepted());
        assertTrue(roll.isMovePending());
        assertEquals(6, roll.getValue());

        assertEquals(ActionResult.OK, game.move(piece));
        assertEquals(PieceLocation.TRACK, piece.getLocation());
        assertEquals(0, piece.getProgress());

        // Rolling 6 grants an extra turn
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertEquals(1, game.getTurnNumber());
        assertFalse(game.isMovePending());

        // Extra roll: 3, move the same piece
        RollOutcome extraRoll = game.roll();

        assertTrue(extraRoll.isAccepted());
        assertTrue(extraRoll.isMovePending());
        assertEquals(3, extraRoll.getValue());

        assertEquals(ActionResult.OK, game.move(piece));
        assertEquals(3, piece.getProgress());

        // After a non-six move, the turn passes
        assertEquals("Sam", game.getCurrentPlayer().getName());
        assertEquals(2, game.getTurnNumber());
    }
    @Test
    void thirdConsecutiveSixPassesTheTurnWithoutAMove() {
            Game game = Game.start(
                    players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE),
                    Dice.scripted(6, 6, 6));

            Player noa = game.getCurrentPlayer();
            Piece firstPiece = noa.getPieces().get(0);
            Piece secondPiece = noa.getPieces().get(1);
            Piece thirdPiece = noa.getPieces().get(2);

            assertEquals(6, game.roll().getValue());
            assertEquals(ActionResult.OK, game.move(firstPiece));
            assertEquals(noa, game.getCurrentPlayer());

            assertEquals(6, game.roll().getValue());
            assertEquals(ActionResult.OK, game.move(secondPiece));
            assertEquals(noa, game.getCurrentPlayer());

            game.roll();

            assertEquals("Sam", game.getCurrentPlayer().getName());
            assertEquals(2, game.getTurnNumber());
            assertFalse(game.isMovePending());
            assertEquals(ActionResult.NO_PENDING_MOVE, game.move(thirdPiece));
            assertEquals(PieceLocation.CASTLE, thirdPiece.getLocation());
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
    void cannotMoveBeforeRolling() {
        Game game = Game.start(
                players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE),
                Dice.scripted(6));

        Piece piece = game.getCurrentPlayer().getPieces().get(0);

        ActionResult result = game.move(piece);

        assertEquals(ActionResult.NO_PENDING_MOVE, result);
        assertEquals(PieceLocation.CASTLE, piece.getLocation());
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    void playersTakeTurnsAfterSuccessfulMoves() {
        Game game = Game.start(
                players("Noa", PlayerColor.RED, "Sam", PlayerColor.BLUE),
                Dice.scripted(6, 3, 6, 2));

        Piece noaPiece = game.getPlayers().get(0).getPieces().get(0);
        Piece samPiece = game.getPlayers().get(1).getPieces().get(0);

        game.roll(); // Noa rolls 6 and leaves the castle.
        assertEquals(ActionResult.OK, game.move(noaPiece));
        assertEquals("Noa", game.getCurrentPlayer().getName());

        game.roll(); // Noa rolls 3, moves, and passes the turn.
        assertEquals(ActionResult.OK, game.move(noaPiece));
        assertEquals("Sam", game.getCurrentPlayer().getName());
        assertEquals(2, game.getTurnNumber());

        game.roll(); // Sam rolls 6 and leaves the castle.
        assertEquals(ActionResult.OK, game.move(samPiece));
        assertEquals("Sam", game.getCurrentPlayer().getName());

        game.roll(); // Sam rolls 2, moves, and passes the turn.
        assertEquals(ActionResult.OK, game.move(samPiece));
        assertEquals("Noa", game.getCurrentPlayer().getName());
        assertEquals(3, game.getTurnNumber());
    }

    private static GameConfig players(Object... nameAndColor) {
        if (nameAndColor.length % 2 != 0) {
            throw new IllegalArgumentException("Expected name and colour pairs.");
        }

        var slots = new java.util.ArrayList<GameConfig.Slot>();

        for (int index = 0; index < nameAndColor.length; index += 2) {
            slots.add(new GameConfig.Slot(
                    (String) nameAndColor[index],
                    (PlayerColor) nameAndColor[index + 1]));
        }

        return new GameConfig(List.copyOf(slots));
    }
}
