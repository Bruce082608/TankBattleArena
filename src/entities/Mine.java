package entities;

import javafx.scene.paint.Color;
import ui.GameArt;

/**
 * A stationary explosive placed by a tank.
 */
public class Mine extends GameObject {
    /** Rendered mine size. */
    public static final double SIZE = 24;

    /** Time before the mine can hurt tanks. */
    public static final double ARMING_SECONDS = 0.45;

    private final Tank owner;
    private double ageSeconds;

    /**
     * Creates a mine at the requested arena coordinate.
     *
     * @param owner tank that placed the mine
     * @param x left coordinate
     * @param y top coordinate
     * @param color mine accent color
     */
    public Mine(Tank owner, double x, double y, Color color) {
        super(x, y, SIZE, SIZE, GameArt.createMineImage(color));
        this.owner = owner;
    }

    /**
     * Advances mine arming state.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    @Override
    public void update(double deltaSeconds) {
        ageSeconds += deltaSeconds;
    }

    /**
     * Reports whether the mine can explode.
     *
     * @return true once the arming delay has elapsed
     */
    public boolean isArmed() {
        return ageSeconds >= ARMING_SECONDS;
    }

    /**
     * Gets the placing tank.
     *
     * @return mine owner
     */
    public Tank getOwner() {
        return owner;
    }
}
