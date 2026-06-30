# Tank Battle Arena

Tank Battle Arena is a Java 17+ JavaFX desktop game inspired by the classic local Flash tank-duel formula: same-screen local players, tight arenas, ricocheting bullets, short health-based duels, automatic map changes, and dangerous powerups.

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

- `E` move forward
- `D` move backward
- `S` rotate left
- `F` rotate right
- `Q` shoot

Player 2 Red Tank:

- `UP` move forward
- `DOWN` move backward
- `LEFT` rotate left
- `RIGHT` rotate right
- `M` shoot

Player 3 Green Tank:

- Move the mouse to drive and aim
- Left click to shoot

`ESC` opens the pause menu.

## Rules

Each tank has 3 HP. Regular bullets bounce around the arena, remain dangerous for several seconds, deal 1 damage, and can still hurt the tank that fired them after leaving the muzzle. The last surviving tank scores 1 point, then the match automatically continues on a randomly selected different map while keeping the current scores. The first player to 5 points wins the match.

Pickups spawn during play:

- Gatling: rapid-fire ricochet shots
- Mine: drops armed explosives behind your tank
- Laser: reflected aim preview, then a bright beam that deals 3 damage
- Shotgun: spread of bouncing pellets
- Shield: blocks one hit
- Homing Missile: slow turning missile aimed at the nearest opponent

Optional sound files can be placed in `resources/sounds` as `shoot.mp3`, `explosion.mp3`, and `victory.mp3`.
