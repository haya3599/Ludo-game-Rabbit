# Ludo Educational Game

Team RABBIT — Software Engineering and Software Quality Assurance in Information Systems.

Iteration 1 delivers a playable Easy Ludo game. The code is original. It uses Java 17, Maven, Java Swing, JUnit 5, and an MVC split. Package names are `Model`, `View`, and `Controller`, as required by the course.

## Requirements

- JDK 17 or newer. The compiler target is Java 17. This repository was built with JDK 23.
- Apache Maven 3.9 or newer. Maven was not on the system PATH during development; Apache Maven 3.9.11 was used from a local install.

## MVC

- `Model` holds players, pieces, board geometry, dice, movement rules, and turn state. It does not use Swing.
- `View` draws the screens and the board. It does not decide whether a move is legal.
- `Controller` validates setup, starts a match, and connects buttons and piece clicks to the model.

## Project structure

```text
ludo-game/
├── pom.xml
├── README.md
├── src/main/java/Main.java
├── src/main/java/Model/
├── src/main/java/View/
├── src/main/java/Controller/
├── src/main/resources/
├── src/test/java/Model/
└── docs/
```

## Iteration 1

- Home screen titled LUDO EDUCATIONAL GAME, with New Game, Game History, and Question Management.
- Game History and Question Management show "Coming in a future iteration".
- Game setup for 2, 3, or 4 players, with a name and a unique colour (Red, Blue, Green, or Yellow).
- Difficulty is fixed to Easy.
- Empty names, duplicate colours, and player counts outside 2–4 are rejected.
- Each participating player starts with four pieces in their castle.
- Easy board: 52-square clockwise track and six home-lane squares per colour.
- Regular die, 1 through 6. A castle piece can leave only on a 6, and that 6 places it on the colour's start square.
- The current player chooses a legal piece. Illegal selections and other players' pieces are rejected.
- After a completed move, including a move that used a 6, the turn passes. The extra turn after a 6 is left for Iteration 2.
- The board shows the current player, the latest roll, the turn number, and elapsed time as MM:SS.

## Later iterations

Not implemented in this iteration:

- Medium and Hard boards
- Boost and UltraBoost dice
- Educational questions and question management
- Surprise squares, captures, Double Blocks, and protection rules
- Extra turn after 6, and cancellation of three consecutive sixes
- Final ranking and CSV game history
- Authorization

## Build

```powershell
mvn clean verify
```

## Run

```powershell
java -jar target/ludo-game-1.0-SNAPSHOT.jar
```

The JAR manifest `Main-Class` is `Main`. Iteration 1 has no runtime libraries outside the JDK, so a classpath entry is not required.

## Test

```powershell
mvn test
```

Tests use scripted dice so the results do not depend on chance. See `docs/ITERATION1_TEST_REPORT.md`.

## Git branches

Work is on feature branches. `integration/iteration-1` combines them. `main` was not created or updated, and this work was not pushed.

| Branch | Role |
| --- | --- |
| `feature/project-setup` | Maven project and MVC skeleton |
| `feature/home-screen` | Home screen, from project setup |
| `feature/game-setup` | Player setup and validation, from project setup |
| `feature/easy-board` | Easy board. Merges `feature/game-setup` first, because the board needs players and pieces |
| `feature/dice-movement` | Dice, castle exit, movement, and turns. Merges `feature/easy-board` first, because movement follows the Easy routes |
| `integration/iteration-1` | Home, setup, board, and movement together |

## Board assumption

The course text asks for a normal Easy board: 52 shared squares, then 6 home squares per colour. The course figures were not in this repository, so `Model.BoardGeometry` uses a conventional 15 by 15 cross. Red starts at the bottom left, then Green, Yellow, and Blue, clockwise. Check those coordinates against the SRS figures if they are added to the repository later.
