package entities;

import systems.PowerUpType;
import ui.GameArt;

/**
 * Collectable weapon or shield pickup placed in the arena.
 */
public class PowerUp extends GameObject {
    /** Pickup sprite size. */
    public static final double SIZE = 34;

    private final PowerUpType type;

    /**
     * Creates a pickup centered around a map coordinate.
     *
     * @param type pickup type
     * @param x left coordinate
     * @param y top coordinate
     */
    public PowerUp(PowerUpType type, double x, double y) {
        super(x, y, SIZE, SIZE, GameArt.createPowerUpImage(type));
        this.type = type;
    }

    /**
     * Pickups are static until collected.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    @Override
    public void update(double deltaSeconds) {
        // Static pickup.
    }

    /**
     * Gets the pickup type.
     *
     * @return powerup type
     */
    public PowerUpType getType() {
        return type;
    }
}
