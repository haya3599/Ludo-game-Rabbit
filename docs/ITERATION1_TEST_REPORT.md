# Iteration 1 test report

Recorded from commands run in this workspace on 7 November 2026. Results below are from those runs, not from an assumed outcome.

## Environment

- OS: Windows
- JDK: 23.0.2 (compiler target 17)
- Maven: Apache Maven 3.9.11, run from a local install because `mvn` was not on PATH
- Branch at the time of the final test run: `integration/iteration-1`

## Automated tests

Command:

```powershell
mvn clean verify
```

Finished at 2026-11-07T17:55:50+02:00 with `BUILD SUCCESS`.

```text
Model.BoardGeometryTest    Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
Model.DiceTest             Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
Model.GameConfigValidatorTest  Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
Model.GameFlowTest         Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Model.MatchSetupTest       Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
Model.MovementRulesTest    Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
```

A later `mvn -q verify`, after the home-screen title was adjusted to stay fully visible, also exited 0. That change does not affect the model tests.

The same 24 tests had already passed on `feature/dice-movement` before integration (`BUILD SUCCESS`, 24 run, 0 failures).

### What the tests cover

| Requirement | Test |
| --- | --- |
| Dice values are from 1 to 6 | `DiceTest.rollsStayBetweenOneAndSix` (300 rolls, seed 7) and scripted 1, 6, and 3 |
| Valid player counts are accepted | `GameConfigValidatorTest.validPlayerCountsAreAccepted` for 2, 3, and 4 |
| Invalid player counts are rejected | `invalidPlayerCountsAreRejected` for 0, 1, and 5 |
| Empty names are rejected | `emptyNamesAreRejected` for a whitespace-only name |
| Duplicate colours are rejected | `duplicateColorsAreRejected` |
| Four pieces per player, starting in the castle | `MatchSetupTest.initializationCreatesFourCastlePiecesPerPlayer` and `GameFlowTest.gameStartsWithFourCastlePiecesAndTheFirstPlayer` |
| Castle exit is rejected when the die is not 6 | `MovementRulesTest.castleExitIsRejectedUnlessTheDieIsSix` and `GameFlowTest.aRollOtherThanSixDoesNotReleaseACastlePiece` |
| Castle exit works on a 6 | `MovementRulesTest.castleExitLandsOnTheStartSquareWhenTheDieIsSix` and `GameFlowTest.castleExitOnSixMovesTheSelectedPieceAndPassesTheTurn` |
| Legal movement changes position | `MovementRulesTest.legalMovementAdvancesAlongTheRouteAndIntoTheHomeLane` and `GameFlowTest.legalMovementChangesTheSelectedPiece` |
| Invalid movement is rejected | `MovementRulesTest.movementPastTheFinalHomeSquareIsRejected` and `GameFlowTest.invalidSelectionDoesNotMoveThePieceOrEndTheTurn` |
| A player cannot move another player's piece | `GameFlowTest.aPlayerCannotMoveAnotherPlayersPiece` |
| Turn order follows the players as added | `GameFlowTest.turnProgressionFollowsThePlayerOrder` (Yellow, then Red, then Green) |

Scripted dice are used wherever a specific roll is required. A repeated roll while a move is pending is also rejected by `GameFlowTest.repeatedRollingIsRejectedWhileAMoveIsPending`.

## JAR

`mvn clean verify` wrote:

`target/ludo-game-1.0-SNAPSHOT.jar`

The manifest read from that JAR contained:

```text
Main-Class: Main
```

The JAR was launched with:

```powershell
java -jar target/ludo-game-1.0-SNAPSHOT.jar
```

The process stayed running and opened a window titled "Ludo Educational Game". It did not crash on startup.

## What was observed in the window

- A fresh launch shows the home screen first: TEAM RABBIT, the full title LUDO EDUCATIONAL GAME, and the buttons New Game, Game History, and Question Management. This was captured from the final JAR after the title was changed from a single clipped line to two lines.
- An earlier session of the same build reached Game Setup. The form showed a player count of 2, name fields, Red and Blue, Back, and Start Easy Game. Difficulty was labelled Easy.
- That session then showed the Easy board. Observed state: Turn 8, current player Player 2 with a blue chip, latest roll 5 drawn as five pips, elapsed time 00:17, and the message "Turn passes to Player 2". Four red pieces and four blue pieces were in the castles, and one red piece was on the track. The Green and Yellow yards were empty because that match had two players.
- Game History and Question Management were not clicked during the captured GUI session. In source they open a dialog whose text is "Coming in a future iteration".

## Not verified by an automated GUI test

There is no Swing test that clicks through every button. Rules are covered by JUnit. The window checks above are manual observations from this machine. Another computer should still be used before submission, as the course asks teams to try the runnable file on more than one machine.

## Known limits

- Home-lane and track coordinates are the conventional Easy layout documented in `docs/ARCHITECTURE.md`. They still need a visual check against the SRS figures.
- Medium, Hard, questions, captures, blocks, extra turns, ranking, and CSV history are not implemented.
- `main` does not exist in this clone, so it was not modified. Nothing was pushed to GitHub.
