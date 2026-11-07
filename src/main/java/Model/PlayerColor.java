package Model;

/**
 * The four Ludo colours. Start indexes are 13 squares apart on the 52-square track.
 */
public enum PlayerColor {
    RED("Red", 0, 198, 40, 40),
    BLUE("Blue", 39, 21, 101, 192),
    GREEN("Green", 13, 46, 125, 50),
    YELLOW("Yellow", 26, 245, 176, 39);

    private final String displayName;
    private final int startIndex;
    private final int red;
    private final int green;
    private final int blue;

    PlayerColor(String displayName, int startIndex, int red, int green, int blue) {
        this.displayName = displayName;
        this.startIndex = startIndex;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
