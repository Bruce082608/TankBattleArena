package entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import ui.GameArt;

/**
 * Projectile fired by a tank.
 */
public class Bullet extends GameObject {
    /** Rendered bullet size. */
    public static final double SIZE = 12;

    /** Bullet travel speed in pixels per second. */
    public static final double SPEED = 500;

    /** Maximum number of wall or obstacle bounces before removal. */
    public static final int MAX_BOUNCES = 5;

    /** Maximum bullet lifetime in seconds. */
    public static final double MAX_LIFETIME_SECONDS = 6.0;

    private final Tank owner;
    private double velocityX;
    private double velocityY;
    private final int damage;
    private double ageSeconds;
    private double previousX;
    private double previousY;
    private int bounceCount;

    /**
     * Creates a bullet from an owner, origin, and direction.
     *
     * @param owner tank that fired the bullet
     * @param x starting left coordinate
     * @param y starting top coordinate
     * @param rotationDegrees direction in degrees
     * @param color bullet color
     * @param damage damage dealt on a successful hit
     */
    public Bullet(Tank owner, double x, double y, double rotationDegrees, Color color, int damage) {
        super(x, y, SIZE, SIZE, GameArt.createBulletImage(color));
        this.owner = owner;
        double radians = Math.toRadians(rotationDegrees);
        this.velocityX = Math.cos(radians) * SPEED;
        this.velocityY = Math.sin(radians) * SPEED;
        this.damage = damage;
    }

    /**
     * Advances the bullet through the arena.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    @Override
    public void update(double deltaSeconds) {
        previousX = getX();
        previousY = getY();
        ageSeconds += deltaSeconds;
        setPosition(getX() + velocityX * deltaSeconds, getY() + velocityY * deltaSeconds);
    }

    /**
     * Reflects the bullet velocity and records one bounce.
     *
     * @param horizontal true when the x velocity should be reflected
     * @param vertical true when the y velocity should be reflected
     */
    public void bounce(boolean horizontal, boolean vertical) {
        if (horizontal) {
            velocityX = -velocityX;
        }
        if (vertical) {
            velocityY = -velocityY;
        }
        bounceCount++;
    }

    /**
     * Reports whether another bounce is still allowed.
     *
     * @return true when the bullet has not reached the bounce limit
     */
    public boolean hasBouncesRemaining() {
        return bounceCount < MAX_BOUNCES;
    }

    /**
     * Reports whether the bullet has exceeded its lifetime.
     *
     * @return true when the bullet should be removed
     */
    public boolean isExpired() {
        return ageSeconds >= MAX_LIFETIME_SECONDS;
    }

    /**
     * Gets the bullet collision box from the previous frame.
     *
     * @return previous collision rectangle
     */
    public Rectangle2D getPreviousCollisionBox() {
        return getProjectedCollisionBox(previousX, previousY);
    }

    /**
     * Moves the bullet back to its last non-colliding frame position.
     */
    public void rewindToPreviousPosition() {
        setPosition(previousX, previousY);
    }

    /**
     * Gets the current horizontal velocity.
     *
     * @return x velocity in pixels per second
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Gets the current vertical velocity.
     *
     * @return y velocity in pixels per second
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Gets the number of completed bounces.
     *
     * @return bounce count
     */
    public int getBounceCount() {
        return bounceCount;
    }

    /**
     * Gets the tank that fired the bullet.
     *
     * @return owning tank
     */
    public Tank getOwner() {
        return owner;
    }

    /**
     * Gets the damage inflicted by the bullet.
     *
     * @return damage amount
     */
    public int getDamage() {
        return damage;
    }
}
