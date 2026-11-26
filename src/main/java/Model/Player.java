package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * A participant. Each player owns four pieces that start in the castle.
 */
public final class Player {

    public static final int PIECE_COUNT = 4;

    private final String name;
    private final PlayerColor color;
    private final BoardGeometry boardGeometry;
    private final List<Piece> pieces = new ArrayList<>();

    public Player(String name, PlayerColor color) {
        this(name, color, BoardGeometry.easy());
    }

    Player(String name, PlayerColor color, BoardGeometry boardGeometry) {
        this.name = name;
        this.color = color;
        this.boardGeometry = boardGeometry;
        for (int index = 0; index < PIECE_COUNT; index++) {
            pieces.add(new Piece(this, index));
        }
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    BoardGeometry getBoardGeometry() {
        return boardGeometry;
    }

    public List<Piece> getPieces() {
        return List.copyOf(pieces);
    }

    public boolean isFinished() {
        return pieces.stream().allMatch(Piece::isFinished);
    }
}
