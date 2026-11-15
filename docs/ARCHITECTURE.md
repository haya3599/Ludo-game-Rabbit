# Architecture — Iteration 1

The application follows MVC. `Model` does not import Swing or the other two packages. `View` reads model state in order to draw it. `Controller` is the only package that calls both.

## Model

The model owns game state and rules.

| Class | Responsibility |
| --- | --- |
| `PlayerColor` | Red, Blue, Green, and Yellow, plus the start index on the 52-square track |
| `Player` | Name, colour, and four pieces |
| `Piece` | Castle, track, or home-lane location, and progress along that player's route |
| `GameConfig` | The names and colours chosen before the match. Difficulty is the constant `Easy` |
| `GameConfigValidator` | Rejects counts outside 2–4, blank names, missing colours, and duplicate colours |
| `ValidationResult` | The error list shown by the setup screen |
| `MatchSetup` | Builds players only after validation succeeds |
| `GridPoint`, `BoardGeometry` | Track, home lanes, castle slots, start squares, and arrow squares |
| `Dice` | A fair six-sided die, or a scripted sequence for tests |
| `MovementRules` | Castle exit and route movement, including the home lane and the exact-finish limit |
| `Game` | Current player, turn number, pending roll, and turn order |
| `RollOutcome`, `ActionResult` | What the controller tells the player after a roll or a click |

## View

The view owns widgets and painting.

| Class | Responsibility |
| --- | --- |
| `MainFrame` | One window. Shows a single registered screen |
| `HomeScreen` | Title and the three home buttons |
| `ComingSoon` | The "Coming in a future iteration" message |
| `GameSetupScreen` | Player count, names, colours, Back, and Start Easy Game |
| `GameScreen` | Turn, current player, die, MM:SS timer, Roll Dice, and messages |
| `BoardPanel` | Draws geometry and pieces with `Graphics2D`. Clicks are reported; legality is not decided here |
| `DiceFacePanel` | Draws the latest roll |
| `UiKit` | Shared colours and buttons |

Elapsed time starts when the game screen is created and is updated by a Swing timer. It is display state, not a movement rule. The timer stops when the screen is hidden, removed, or the game ends.

## Controller

| Class | Responsibility |
| --- | --- |
| `AppController` | Starts on the home screen, switches screens, and accepts a validated configuration |
| `ScreenRegistry` | Registers the screens that belong to the integrated build |
| `HomeScreens` | New Game opens setup. History and question management show the future-iteration message |
| `SetupScreens` | Runs `GameConfigValidator` and refuses to start while the form is invalid |
| `BoardScreens` | Creates a `Game` with a random die and shows the board |
| `GameplayController` | Roll Dice and piece clicks |

`Main` only starts Swing and calls `AppController.launch`.

## Board representation

`BoardGeometry` stores a 15 by 15 grid without using AWT colours.

- The main track has 52 distinct squares, travelled clockwise.
- Start indexes are 0 (Red), 13 (Green), 26 (Yellow), and 39 (Blue).
- Each colour has an arrow on the last main-track square of its own route, two squares before it would return to its start (track indexes 50, 11, 24, and 37 for Red, Green, Yellow, and Blue). Route progress 0–50 is on the track; 51–56 is in the home lane.
- The next six steps are that colour's home lane. The sixth square is the final home square.
- Castle slots are the four piece positions inside each yard.

These coordinates follow the written Easy specification. The SRS figures were not stored in the repository, so the exact cell positions should be checked against those figures later. Yards for colours that are not playing are still drawn. Only participating players receive pieces.

## Dice and movement

1. The current player rolls. A second roll is rejected while a move is pending, and the Roll Dice button is disabled.
2. Legal pieces are highlighted. A castle piece is legal only when the roll is 6. The 6 is spent by leaving the castle: the piece stops on the start square and does not also move six steps.
3. A piece already on the route moves forward by the roll. Steps that pass the arrow enter the home lane.
4. A move that would pass the final home square is illegal. The player can choose another legal piece. If none exists, the turn passes with a message.
5. A click on another player's piece is rejected and does not change the turn.
6. After a legal move, the model updates the piece and the view repaints from the new position.

All four players share one computer. "Only the current player can roll" means the model accepts a roll only when it is waiting for a roll, and it accepts a move only for a piece owned by the player whose turn it is.

## Turn progression

Players act in the order they were added during setup, not in colour order. The turn number starts at 1 and increases when the turn passes. A completed move on a 6 grants another roll unless the player has finished all four pieces. Finished players are recorded in finish order and skipped in subsequent turns. The rule that cancels a third consecutive 6 is not implemented.

After movement, `Game` returns a single opposing piece on the destination track square to its castle, resetting its progress. Starred start squares are protected. Passing pieces, friendly pieces, pieces in home lanes, and destinations occupied by multiple rivals do not trigger this capture rule. Double Block rules remain unimplemented.

The game ends as soon as all but one player have finished (1, 2, or 3 finishers for games of 2, 3, or 4). `Game` rejects further rolls and moves. `GameplayController` switches `GameScreen` to results with the finish order followed by the remaining player, stops the clock and disables play. The results button returns directly to Home.
