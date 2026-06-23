# Tank Battle Arena

Tank Battle Arena is a Java 17+ JavaFX desktop game for two local players. It includes a main menu, map selection, instructions, pause menu, gameplay scene, HUD, round timer, scoring, respawns, explosion and hit animations, and a game-over scene.

## Run

Install or fetch a JavaFX SDK compatible with Java 17.

```bash
cd TankBattleArena
./scripts/fetch-javafx.sh
./compile.sh
./run.sh
```

If JavaFX is already installed, point the scripts at it:

```bash
JAVAFX_HOME=/path/to/javafx-sdk ./compile.sh
JAVAFX_HOME=/path/to/javafx-sdk ./run.sh
```

## Controls

Player 1 Blue Tank:

- `W` move forward
- `S` move backward
- `A` rotate left
- `D` rotate right
- `SPACE` shoot

Player 2 Red Tank:

- `UP` move forward
- `DOWN` move backward
- `LEFT` rotate left
- `RIGHT` rotate right
- `ENTER` shoot

`ESC` opens the pause menu.

## Rules

Each tank has 100 HP. Bullets deal 20 damage, bounce off arena walls and obstacles up to 5 times, and have a 500 ms cooldown. Each round lasts 120 seconds. Destroying the opponent wins the round; if time expires, the tank with higher HP wins. Equal HP is a draw. The first player to 5 points wins the match.

Optional sound files can be placed in `resources/sounds` as `shoot.mp3`, `explosion.mp3`, and `victory.mp3`.
