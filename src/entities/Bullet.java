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
    public static final double SPEED = 468;

    /** Maximum number of wall or obstacle bounces before removal. */
    public static final int MAX_BOUNCES = 8;

    /** Maximum bullet lifetime in seconds. */
    public static final double MAX_LIFETIME_SECONDS = 7.5;

    /** Time before a projectile can hit the tank that fired it. */
    public static final double OWNER_SAFE_SECONDS = 0.18;

    /** Maximum turn speed for homing projectiles. */
    private static final double HOMING_TURN_RADIANS_PER_SECOND = Math.toRadians(140);

    private final Tank owner;
    private final double speed;
    private final int maxBounces;
    private final double maxLifetimeSeconds;
    private double velocityX;
    private double velocityY;
    private final int damage;
    private Tank homingTarget;
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
        this(owner, x, y, rotationDegrees, color, damage, SPEED, MAX_BOUNCES, MAX_LIFETIME_SECONDS);
    }

    /**
     * Creates a bullet with custom travel behavior.
     *
     * @param owner tank that fired the bullet
     * @param x starting left coordinate
     * @param y starting top coordinate
     * @param rotationDegrees direction in degrees
     * @param color bullet color
     * @param damage damage dealt on a successful hit
     * @param speed bullet speed
     * @param maxBounces maximum number of wall bounces
     * @param maxLifetimeSeconds maximum lifetime
     */
    public Bullet(
            Tank owner,
            double x,
            double y,
            double rotationDegrees,
            Color color,
            int damage,
            double speed,
            int maxBounces,
            double maxLifetimeSeconds) {
        super(x, y, SIZE, SIZE, GameArt.createBulletImage(color));
        this.owner = owner;
        this.speed = speed;
        this.maxBounces = maxBounces;
        this.maxLifetimeSeconds = maxLifetimeSeconds;
        double radians = Math.toRadians(rotationDegrees);
        this.velocityX = Math.cos(radians) * speed;
        this.velocityY = Math.sin(radians) * speed;
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
        steerTowardTarget(deltaSeconds);
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
        return bounceCount < maxBounces;
    }

    /**
     * Reports whether the bullet has exceeded its lifetime.
     *
     * @return true when the bullet should be removed
     */
    public boolean isExpired() {
        return ageSeconds >= maxLifetimeSeconds;
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
     * Assigns a tank for gentle missile homing.
     *
     * @param homingTarget target tank
     */
    public void setHomingTarget(Tank homingTarget) {
        this.homingTarget = homingTarget;
    }

    /**
     * Gets the damage inflicted by the bullet.
     *
     * @return damage amount
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Reports whether the projectile can hit its firing tank yet.
     *
     * @return true after the muzzle safety delay
     */
    public boolean canHitOwner() {
        return ageSeconds >= OWNER_SAFE_SECONDS;
    }

    /**
     * Curves homing missiles toward their current target without making them unavoidable.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    private void steerTowardTarget(double deltaSeconds) {
        if (homingTarget == null || homingTarget.isDestroyed()) {
            return;
        }
        double currentAngle = Math.atan2(velocityY, velocityX);
        double desiredAngle = Math.atan2(
                homingTarget.getCenterY() - getCenterY(),
                homingTarget.getCenterX() - getCenterX());
        double delta = normalizeRadians(desiredAngle - currentAngle);
        double maxTurn = HOMING_TURN_RADIANS_PER_SECOND * deltaSeconds;
        double turn = Math.max(-maxTurn, Math.min(maxTurn, delta));
        double nextAngle = currentAngle + turn;
        velocityX = Math.cos(nextAngle) * speed;
        velocityY = Math.sin(nextAngle) * speed;
    }

    /**
     * Normalizes an angle delta to -PI..PI.
     *
     * @param radians raw radians
     * @return normalized radians
     */
    private double normalizeRadians(double radians) {
        double result = radians;
        while (result > Math.PI) {
            result -= Math.PI * 2;
        }
        while (result < -Math.PI) {
            result += Math.PI * 2;
        }
        return result;
    }
}
