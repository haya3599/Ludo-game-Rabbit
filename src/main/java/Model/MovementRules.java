package Model;

/**
 * A castle piece leaves only on a roll of 6 and stops on
 * the coloured start square. A piece already on the route moves by the calculated
 * step count. A matching arrow leads to the next inner track or to the six-square home lane.
 * A move that would pass the final home square is illegal.
 *
 * <p>Game resolves captures and protected squares after movement.
 */
public final class MovementRules {

    private MovementRules() {
    }

    public static boolean isLegal(Piece piece, int dice) {
        return isLegal(piece, dice, dice);
    }

    static boolean isLegal(Piece piece, DiceRoll roll) {
        return isLegal(piece, roll.regularValue(), roll.movementSteps());
    }

    private static boolean isLegal(Piece piece, int regularValue, int steps) {
        if (piece == null || regularValue < 1 || regularValue > 6) {
            return false;
        }
        if (piece.getLocation() == PieceLocation.CASTLE) {
            return regularValue == 6;
        }
        return steps > 0 && steps <= piece.maxProgress() - piece.getProgress();
    }

    public static void apply(Piece piece, int dice) {
        apply(piece, dice, dice);
    }

    static void apply(Piece piece, DiceRoll roll) {
        apply(piece, roll.regularValue(), roll.movementSteps());
    }

    private static void apply(Piece piece, int regularValue, int steps) {
        if (!isLegal(piece, regularValue, steps)) {
            throw new IllegalArgumentException("Illegal move.");
        }
        if (piece.getLocation() == PieceLocation.CASTLE) {
            piece.exitCastleToStart();
            return;
        }
        piece.placeAtProgress(piece.getProgress() + steps);
    }
}
