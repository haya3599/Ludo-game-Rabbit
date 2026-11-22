package Model;

public final class RegularDiceCreator extends DiceCreator {

    @Override
    public Dice createDice() {
        return Dice.random();
    }
}
