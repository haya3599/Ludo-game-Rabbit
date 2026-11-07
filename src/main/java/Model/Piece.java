package Model;

/**
 * One of the four pieces owned by a player. Pieces begin in the castle.
 */
public final class Piece {

    public static final int TRACK_LENGTH = 52;
    public static final int HOME_LENGTH = 6;
    /** Last legal progress: the sixth home-lane square. */
    public static final int MAX_PROGRESS = TRACK_LENGTH + HOME_LENGTH - 1;

    private final Player owner;
    private final int index;
    private PieceLocation location = PieceLocation.CASTLE;
    private int progress = -1;

    Piece(Player owner, int index) {
        this.owner = owner;
        this.index = index;
    }

    public Player getOwner() {
        return owner;
    }

    public int getIndex() {
        return index;
    }

    public PieceLocation getLocation() {
        return location;
    }

    public int getProgress() {
        return progress;
    }

    public int getTrackIndex() {
        if (location != PieceLocation.TRACK) {
            throw new IllegalStateException("Piece is not on the main track.");
        }
        return (owner.getColor().getStartIndex() + progress) % TRACK_LENGTH;
    }

    public int getHomeIndex() {
        if (location != PieceLocation.HOME) {
            throw new IllegalStateException("Piece is not on the home lane.");
        }
        return progress - TRACK_LENGTH;
    }

    /**
     * Places the piece on its route. Progress 0 is the start square.
     * Used by movement rules and by tests that need a known position.
     */
    void placeAtProgress(int progress) {
        if (progress < 0 || progress > MAX_PROGRESS) {
            throw new IllegalArgumentException("Progress out of range: " + progress);
        }
        this.progress = progress;
        this.location = progress < TRACK_LENGTH ? PieceLocation.TRACK : PieceLocation.HOME;
    }

    void exitCastleToStart() {
        placeAtProgress(0);
    }
}
