package systems;

import javafx.scene.input.KeyCode;

/**
 * Immutable keyboard layout for one player.
 */
public final class ControlScheme {
    private final KeyCode forward;
    private final KeyCode backward;
    private final KeyCode left;
    private final KeyCode right;
    private final KeyCode shoot;

    /**
     * Creates a control scheme from movement and shooting keys.
     *
     * @param forward key for forward movement
     * @param backward key for backward movement
     * @param left key for rotating left
     * @param right key for rotating right
     * @param shoot key for firing
     */
    public ControlScheme(KeyCode forward, KeyCode backward, KeyCode left, KeyCode right, KeyCode shoot) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.shoot = shoot;
    }

    /**
     * Gets the forward movement key.
     *
     * @return forward key
     */
    public KeyCode forward() {
        return forward;
    }

    /**
     * Gets the backward movement key.
     *
     * @return backward key
     */
    public KeyCode backward() {
        return backward;
    }

    /**
     * Gets the rotate-left key.
     *
     * @return left rotation key
     */
    public KeyCode left() {
        return left;
    }

    /**
     * Gets the rotate-right key.
     *
     * @return right rotation key
     */
    public KeyCode right() {
        return right;
    }

    /**
     * Gets the shooting key.
     *
     * @return shoot key
     */
    public KeyCode shoot() {
        return shoot;
    }
}
