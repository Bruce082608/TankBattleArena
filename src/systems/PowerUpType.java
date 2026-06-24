package systems;

import javafx.scene.paint.Color;

/**
 * Pickups inspired by classic local ricochet tank arena power weapons.
 */
public enum PowerUpType {
    GATLING("Gatling", "G", Color.rgb(255, 210, 72)),
    MINE("Mine", "M", Color.rgb(245, 112, 71)),
    LASER("Laser", "L", Color.rgb(89, 222, 255)),
    SHOTGUN("Shotgun", "S", Color.rgb(255, 142, 215)),
    SHIELD("Shield", "+", Color.rgb(116, 230, 146)),
    HOMING_MISSILE("Homing", "H", Color.rgb(183, 142, 255));

    private final String displayName;
    private final String symbol;
    private final Color color;

    PowerUpType(String displayName, String symbol, Color color) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.color = color;
    }

    /**
     * Gets the short player-facing name.
     *
     * @return display label
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the compact icon text.
     *
     * @return icon symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the pickup accent color.
     *
     * @return JavaFX color
     */
    public Color getColor() {
        return color;
    }
}
