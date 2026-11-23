package Model;

/** The raw die faces from one roll. A multiplier value of zero means no multiplier die was rolled. */
public record DiceRoll(int regularValue, int multiplierValue) {

    public DiceRoll {
        if (regularValue < 1 || regularValue > 6 || multiplierValue < 0 || multiplierValue > 6) {
            throw new IllegalArgumentException("Die value outside its supported range.");
        }
    }

    public boolean hasMultiplier() {
        return multiplierValue != 0;
    }

    public int movementSteps() {
        return hasMultiplier() ? regularValue * (multiplierValue - 1) : regularValue;
    }
}
