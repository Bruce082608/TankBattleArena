package obstacles;

import entities.GameObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * Base class for immovable arena obstacles.
 */
public abstract class Obstacle extends GameObject {
    private static final double DEFAULT_COLLISION_INSET_RATIO = 0.18;
    private static final double MAX_DEFAULT_COLLISION_INSET = 22;

    private final double collisionInsetX;
    private final double collisionInsetY;

    /**
     * Creates an obstacle with a sprite and collision box.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width obstacle width
     * @param height obstacle height
     * @param image obstacle sprite
     */
    protected Obstacle(double x, double y, double width, double height, Image image) {
        this(
                x,
                y,
                width,
                height,
                image,
                Math.min(width * DEFAULT_COLLISION_INSET_RATIO, MAX_DEFAULT_COLLISION_INSET),
                Math.min(height * DEFAULT_COLLISION_INSET_RATIO, MAX_DEFAULT_COLLISION_INSET));
    }

    /**
     * Creates an obstacle with a sprite and customized collision padding.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width obstacle width
     * @param height obstacle height
     * @param image obstacle sprite
     * @param collisionInsetX horizontal collision inset
     * @param collisionInsetY vertical collision inset
     */
    protected Obstacle(
            double x,
            double y,
            double width,
            double height,
            Image image,
            double collisionInsetX,
            double collisionInsetY) {
        super(x, y, width, height, image);
        this.collisionInsetX = collisionInsetX;
        this.collisionInsetY = collisionInsetY;
    }

    /**
     * Obstacles are static, so no per-frame state changes are required.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    @Override
    public void update(double deltaSeconds) {
        // Static obstacle.
    }

    /**
     * Returns a collision box that is slightly smaller than the painted sprite.
     *
     * @return inset rectangle used for collision detection
     */
    @Override
    public Rectangle2D getCollisionBox() {
        double collisionWidth = Math.max(1, getWidth() - collisionInsetX * 2);
        double collisionHeight = Math.max(1, getHeight() - collisionInsetY * 2);
        return new Rectangle2D(
                getX() + collisionInsetX,
                getY() + collisionInsetY,
                collisionWidth,
                collisionHeight);
    }

    /**
     * Reports whether this obstacle blocks tank movement.
     *
     * @return true when tanks cannot pass through
     */
    public boolean blocksTanks() {
        return true;
    }

    /**
     * Reports whether this obstacle blocks bullets.
     *
     * @return true when bullets should be destroyed on contact
     */
    public boolean blocksBullets() {
        return true;
    }
}
