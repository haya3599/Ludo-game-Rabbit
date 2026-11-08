package Model;

/**
 * Iteration 1 movement. A castle piece leaves only on a roll of 6 and stops on
 * the coloured start square. A piece already on the route moves that many
 * steps. Steps that pass the colour's arrow enter its six-square home lane.
 * A move that would pass the final home square is illegal.
 *
 * <p>Capture, blocks, protection, and the extra turn after a 6 are not applied
 * here. Those rules belong to a later iteration.
 */
public final class MovementRules {

    private MovementRules() {
    }

    public static boolean isLegal(Piece piece, int dice) {
        if (piece == null || dice < 1 || dice > 6) {
            return false;
        }
        if (piece.getLocation() == PieceLocation.CASTLE) {
            return dice == 6;
        }
        return piece.getProgress() + dice <= Piece.MAX_PROGRESS;
    }

    public static void apply(Piece piece, int dice) {
        if (!isLegal(piece, dice)) {
            throw new IllegalArgumentException("Illegal move.");
        }
        if (piece.getLocation() == PieceLocation.CASTLE) {
            piece.exitCastleToStart();
            return;
        }
        piece.placeAtProgress(piece.getProgress() + dice);
    }
}
