package entities;

import java.util.List;

import javafx.scene.paint.Color;
import maps.MapData;
import obstacles.Obstacle;
import systems.CollisionManager;
import systems.ControlScheme;
import systems.InputHandler;
import ui.GameArt;

/**
 * Concrete tank controlled by a local keyboard player.
 */
public class PlayerTank extends Tank {
    /** Default movement speed in pixels per second. */
    public static final double DEFAULT_MOVEMENT_SPEED = 190;

    /** Default rotation speed in degrees per second. */
    public static final double DEFAULT_ROTATION_SPEED = 170;

    /** Standard tank width. */
    public static final double TANK_WIDTH = 54;

    /** Standard tank height. */
    public static final double TANK_HEIGHT = 42;

    private final String playerName;
    private final ControlScheme controls;

    /**
     * Creates a player tank with colors and controls.
     *
     * @param playerName display name for the player
     * @param x left coordinate
     * @param y top coordinate
     * @param bodyColor main tank color
     * @param accentColor trim color
     * @param bulletColor fired bullet color
     * @param controls keyboard controls
     */
    public PlayerTank(
            String playerName,
            double x,
            double y,
            Color bodyColor,
            Color accentColor,
            Color bulletColor,
            ControlScheme controls) {
        super(
                x,
                y,
                TANK_WIDTH,
                TANK_HEIGHT,
                GameArt.createTankImage(bodyColor, accentColor),
                DEFAULT_MOVEMENT_SPEED,
                DEFAULT_ROTATION_SPEED,
                bulletColor);
        this.playerName = playerName;
        this.controls = controls;
    }

    /**
     * Reads keyboard state and applies movement or rotation.
     *
     * @param deltaSeconds elapsed time since the previous frame
     * @param inputHandler keyboard input service
     * @param collisionManager collision service
     * @param obstacles arena obstacles
     * @param mapData current map
     */
    public void handleInput(
            double deltaSeconds,
            InputHandler inputHandler,
            CollisionManager collisionManager,
            List<Obstacle> obstacles,
            MapData mapData) {
        if (inputHandler.isPressed(controls.left())) {
            rotate(-1, deltaSeconds);
        }
        if (inputHandler.isPressed(controls.right())) {
            rotate(1, deltaSeconds);
        }
        if (inputHandler.isPressed(controls.forward())) {
            move(1, deltaSeconds, collisionManager, obstacles, mapData);
        }
        if (inputHandler.isPressed(controls.backward())) {
            move(-1, deltaSeconds, collisionManager, obstacles, mapData);
        }
    }

    /**
     * Gets the key layout for the tank.
     *
     * @return control scheme
     */
    public ControlScheme getControls() {
        return controls;
    }

    /**
     * Gets the display name.
     *
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }
}
