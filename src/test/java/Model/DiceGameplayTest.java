package Model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiceGameplayTest {

    private static final List<GameConfig.Slot> PLAYERS = List.of(
            new GameConfig.Slot("Noa", PlayerColor.RED),
            new GameConfig.Slot("Sam", PlayerColor.BLUE));

    @Test
    void difficultyUsesTheExistingDiceFactoriesAndHardCanStart() {
        assertInstanceOf(Dice.class, new RegularDiceCreator().createDice());
        assertNull(Difficulty.EASY.createMultiplierDie());
        assertInstanceOf(BoostDice.class, Difficulty.MEDIUM.createMultiplierDie());
        assertInstanceOf(UltraBoostDice.class, Difficulty.HARD.createMultiplierDie());

        DiceRoll easy = Game.start(
                new GameConfig(PLAYERS, Difficulty.EASY)).roll().getDiceRoll();
        assertFalse(easy.hasMultiplier());
        assertEquals(easy.regularValue(), easy.movementSteps());

        DiceRoll medium = Game.start(
                new GameConfig(PLAYERS, Difficulty.MEDIUM)).roll().getDiceRoll();
        assertTrue(medium.hasMultiplier());
        assertTrue(medium.multiplierValue() >= 1
                && medium.multiplierValue() <= 3);

        Game hardGame = Game.start(new GameConfig(PLAYERS, Difficulty.HARD));
        assertEquals(Difficulty.HARD, hardGame.getBoardGeometry().getDifficulty());

        DiceRoll hard = hardGame.roll().getDiceRoll();
        assertTrue(hard.hasMultiplier());
        assertTrue(hard.regularValue() >= 1 && hard.regularValue() <= 6);
        assertTrue(hard.multiplierValue() >= 1 && hard.multiplierValue() <= 6);
        assertEquals(
                hard.regularValue() * (hard.multiplierValue() - 1),
                hard.movementSteps());
    }

    @Test
    void calculatesStepsFromTheRawFacesForBoostAndUltraBoost() {
        assertEquals(8, new DiceRoll(4, 3).movementSteps());
        assertEquals(0, new DiceRoll(5, 1).movementSteps());
        assertEquals(18, new DiceRoll(6, 4).movementSteps());
        assertEquals(30, new DiceRoll(6, 6).movementSteps());
    }

    @Test
    void zeroStepsOnANonSixPassesWithoutMovingAPiece() {
        Game game = medium(Dice.scripted(5), Dice.scripted(1));
        Piece piece = game.getCurrentPlayer().getPieces().get(0);
        piece.placeAtProgress(9);

        RollOutcome roll = game.roll();

        assertEquals(0, roll.getDiceRoll().movementSteps());
        assertFalse(roll.isMovePending());
        assertTrue(game.getLegalPieces().isEmpty());
        assertEquals(ActionResult.NO_PENDING_MOVE, game.move(piece));
        assertEquals(9, piece.getProgress());
        assertSame(game.getPlayers().get(1), game.getCurrentPlayer());
        assertEquals(2, game.getTurnNumber());
    }

    @Test
    void zeroStepsOnASixKeepsTheRollWhenAllPiecesAreOut() {
        Game game = medium(Dice.scripted(6, 6, 6), Dice.scripted(1, 1, 1));
        Player player = game.getCurrentPlayer();
        for (Piece piece : player.getPieces()) {
            piece.placeAtProgress(piece.getIndex());
        }

        for (int roll = 0; roll < 2; roll++) {
            assertFalse(game.roll().isMovePending());
            assertTrue(game.getLegalPieces().isEmpty());
            assertSame(player, game.getCurrentPlayer());
            assertEquals(1, game.getTurnNumber());
        }

        assertFalse(game.roll().isMovePending());
        assertSame(game.getPlayers().get(1), game.getCurrentPlayer());
        assertEquals(List.of(0, 1, 2, 3), player.getPieces().stream().map(Piece::getProgress).toList());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void castleExitOnARegularSixAlwaysStopsAtTheStartSquare(int multiplier) {
        Game game = medium(Dice.scripted(6), Dice.scripted(multiplier));
        Player player = game.getCurrentPlayer();
        Piece active = player.getPieces().get(0);
        active.placeAtProgress(8);
        Piece castle = player.getPieces().get(1);

        assertTrue(game.roll().isMovePending());
        assertTrue(game.getLegalPieces().contains(castle));
        assertEquals(multiplier != 1, game.getLegalPieces().contains(active));
        assertEquals(ActionResult.OK, game.move(castle));

        assertEquals(PieceLocation.TRACK, castle.getLocation());
        assertEquals(0, castle.getProgress());
        assertEquals(8, active.getProgress());
        assertSame(player, game.getCurrentPlayer());
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    void sixCalculatedStepsCanMoveAnActivePieceButDoNotReleaseCastlePiecesOrGrantAReroll() {
        Game game = medium(Dice.scripted(3), Dice.scripted(3));
        Piece active = game.getCurrentPlayer().getPieces().get(0);
        active.placeAtProgress(2);

        assertEquals(6, game.roll().getDiceRoll().movementSteps());
        assertEquals(List.of(active), game.getLegalPieces());
        assertEquals(ActionResult.OK, game.move(active));
        assertEquals(8, active.getProgress());
        assertSame(game.getPlayers().get(1), game.getCurrentPlayer());
    }

    @ParameterizedTest
    @EnumSource(names = {"EASY", "MEDIUM"})
    void thirdRegularSixCancelsOnlyThatRollAndResetsWhenTheTurnPasses(Difficulty difficulty) {
        Game game = Game.start(new GameConfig(PLAYERS, difficulty), Dice.scripted(6, 6, 6, 6, 4, 6),
                difficulty == Difficulty.MEDIUM ? Dice.scripted(1, 3, 2, 1, 2, 3) : null);
        Player first = game.getCurrentPlayer();
        Piece piece = first.getPieces().get(0);
        Piece inCastle = first.getPieces().get(1);

        assertTrue(game.roll().isMovePending());
        assertEquals(ActionResult.OK, game.move(piece));
        assertTrue(game.roll().isMovePending());
        assertEquals(ActionResult.OK, game.move(piece));
        assertSame(first, game.getCurrentPlayer());
        assertEquals(1, game.getTurnNumber());

        RollOutcome canceled = game.roll();
        assertTrue(canceled.isAccepted());
        assertEquals(6, canceled.getValue());
        assertFalse(canceled.isMovePending());
        assertTrue(game.getLegalPieces().isEmpty());
        assertEquals(difficulty == Difficulty.MEDIUM ? 12 : 6, piece.getProgress());
        assertEquals(PieceLocation.CASTLE, inCastle.getLocation());
        assertEquals(2, game.getTurnNumber());

        Player second = game.getPlayers().get(1);
        assertSame(second, game.getCurrentPlayer());
        Piece secondPiece = second.getPieces().get(0);
        assertTrue(game.roll().isMovePending());
        assertEquals(ActionResult.OK, game.move(secondPiece));
        assertTrue(game.roll().isMovePending());
        assertEquals(ActionResult.OK, game.move(secondPiece));
        assertSame(first, game.getCurrentPlayer());
        assertTrue(game.roll().isMovePending());
    }

    @Test
    void multipliedMovementMustLandExactlyAtHome() {
        Game game = medium(Dice.scripted(4), Dice.scripted(3));
        List<Piece> pieces = game.getCurrentPlayer().getPieces();
        Piece exact = pieces.get(0);
        exact.placeAtProgress(exact.maxProgress() - 8);
        Piece overshoot = pieces.get(1);
        overshoot.placeAtProgress(overshoot.maxProgress() - 7);

        assertEquals(8, game.roll().getDiceRoll().movementSteps());
        assertEquals(List.of(exact), game.getLegalPieces());
        assertEquals(ActionResult.ILLEGAL_MOVE, game.move(overshoot));
        assertEquals(ActionResult.OK, game.move(exact));
        assertTrue(exact.isFinished());
        assertFalse(overshoot.isFinished());
    }

    @Test
    void multipliedSixPassesWhenEveryMoveOvershootsHome() {
        Game game = medium(Dice.scripted(6), Dice.scripted(3));
        for (Piece piece : game.getCurrentPlayer().getPieces()) {
            piece.placeAtProgress(piece.maxProgress() - 1);
        }

        RollOutcome roll = game.roll();

        assertEquals(12, roll.getDiceRoll().movementSteps());
        assertFalse(roll.isMovePending());
        assertSame(game.getPlayers().get(1), game.getCurrentPlayer());
    }

    private static Game medium(Dice regular, Dice multiplier) {
        return Game.start(new GameConfig(PLAYERS, Difficulty.MEDIUM), regular, multiplier);
    }
}
