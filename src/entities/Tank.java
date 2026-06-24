package entities;

import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import maps.MapData;
import obstacles.Obstacle;
import systems.CollisionManager;

/**
 * Abstract base class for player-controlled tanks.
 */
public abstract class Tank extends GameObject {
    /** Maximum health for every tank. */
    public static final int MAX_HP = 3;

    /** Damage dealt by each bullet. */
    public static final int BULLET_DAMAGE = 1;

    /** Cooldown between shots in nanoseconds. */
    public static final long BULLET_COOLDOWN_NANOS = 420_000_000L;

    private final double movementSpeed;
    private final double rotationSpeed;
    private final Color bulletColor;
    private int hp = MAX_HP;
    private double rotationDegrees;
    private long lastShotNanos = -BULLET_COOLDOWN_NANOS;
    private boolean shieldActive;

    /**
     * Creates a tank with shared movement and combat properties.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width tank width
     * @param height tank height
     * @param image rendered tank image
     * @param movementSpeed pixels per second
     * @param rotationSpeed degrees per second
     * @param bulletColor color used for fired bullets
     */
    protected Tank(
            double x,
            double y,
            double width,
            double height,
            Image image,
            double movementSpeed,
            double rotationSpeed,
            Color bulletColor) {
        super(x, y, width, height, image);
        this.movementSpeed = movementSpeed;
        this.rotationSpeed = rotationSpeed;
        this.bulletColor = bulletColor;
    }

    /**
     * Tanks are updated through player input, so the base tick is intentionally empty.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    @Override
    public void update(double deltaSeconds) {
        // PlayerTank handles input-driven movement explicitly.
    }

    /**
     * Returns a tighter tank body collision box that ignores transparent sprite padding.
     *
     * @return inset collision rectangle
     */
    @Override
    public Rectangle2D getCollisionBox() {
        return getProjectedCollisionBox(getX(), getY());
    }

    /**
     * Returns the tank collision box at a proposed position.
     *
     * @param proposedX proposed left coordinate
     * @param proposedY proposed top coordinate
     * @return inset projected collision rectangle
     */
    @Override
    public Rectangle2D getProjectedCollisionBox(double proposedX, double proposedY) {
        double insetX = getWidth() * 0.18;
        double insetY = getHeight() * 0.20;
        return new Rectangle2D(
                proposedX + insetX,
                proposedY + insetY,
                getWidth() - insetX * 2,
                getHeight() - insetY * 2);
    }

    /**
     * Rotates the tank by a direction multiplier.
     *
     * @param direction -1 for left, 1 for right
     * @param deltaSeconds elapsed time since the previous frame
     */
    public void rotate(double direction, double deltaSeconds) {
        rotationDegrees = normalize(rotationDegrees + direction * rotationSpeed * deltaSeconds);
        getView().setRotate(rotationDegrees);
    }

    /**
     * Attempts to move the tank while respecting map and obstacle collisions.
     *
     * @param direction 1 for forward, -1 for backward
     * @param deltaSeconds elapsed time since the previous frame
     * @param collisionManager collision service
     * @param obstacles arena obstacles
     * @param mapData current map data
     */
    public void move(
            double direction,
            double deltaSeconds,
            CollisionManager collisionManager,
            List<Obstacle> obstacles,
            MapData mapData) {
        double radians = Math.toRadians(rotationDegrees);
        double nextX = getX() + Math.cos(radians) * movementSpeed * direction * deltaSeconds;
        double nextY = getY() + Math.sin(radians) * movementSpeed * direction * deltaSeconds;
        if (collisionManager.canMove(this, nextX, nextY, obstacles, mapData)) {
            setPosition(nextX, nextY);
        }
    }

    /**
     * Attempts to fire a bullet if the cooldown has elapsed.
     *
     * @param nowNanos current AnimationTimer timestamp
     * @return a bullet when firing succeeds, otherwise null
     */
    public Bullet tryShoot(long nowNanos) {
        if (!canShoot(nowNanos, BULLET_COOLDOWN_NANOS)) {
            return null;
        }
        return createBullet(rotationDegrees, Bullet.SPEED, Bullet.MAX_BOUNCES, Bullet.MAX_LIFETIME_SECONDS, BULLET_DAMAGE);
    }

    /**
     * Checks and records a shot using the requested cooldown.
     *
     * @param nowNanos current AnimationTimer timestamp
     * @param cooldownNanos weapon cooldown
     * @return true when firing is allowed
     */
    public boolean canShoot(long nowNanos, long cooldownNanos) {
        if (nowNanos - lastShotNanos < cooldownNanos) {
            return false;
        }
        lastShotNanos = nowNanos;
        return true;
    }

    /**
     * Creates a projectile using the tank's muzzle as origin.
     *
     * @param rotationDegrees direction in degrees
     * @param speed projectile speed
     * @param maxBounces maximum wall bounces
     * @param maxLifetimeSeconds projectile lifetime
     * @param damage damage dealt on hit
     * @return configured projectile
     */
    public Bullet createBullet(
            double rotationDegrees,
            double speed,
            int maxBounces,
            double maxLifetimeSeconds,
            int damage) {
        double radians = Math.toRadians(rotationDegrees);
        double startX = getCenterX() + Math.cos(radians) * (getWidth() / 2.0 + 8) - Bullet.SIZE / 2.0;
        double startY = getCenterY() + Math.sin(radians) * (getWidth() / 2.0 + 8) - Bullet.SIZE / 2.0;
        return new Bullet(this, startX, startY, rotationDegrees, bulletColor, damage, speed, maxBounces, maxLifetimeSeconds);
    }

    /**
     * Applies incoming damage to the tank.
     *
     * @param damage amount of damage to subtract
     */
    public void takeDamage(int damage) {
        if (shieldActive) {
            shieldActive = false;
            updateShieldVisual();
            return;
        }
        hp = Math.max(0, hp - damage);
    }

    /**
     * Restores the tank to full health.
     */
    public void restoreHp() {
        hp = MAX_HP;
        getView().setOpacity(1.0);
        shieldActive = false;
        updateShieldVisual();
    }

    /**
     * Resets the tank to a spawn point and facing direction.
     *
     * @param x spawn left coordinate
     * @param y spawn top coordinate
     * @param rotationDegrees spawn rotation
     */
    public void resetTo(double x, double y, double rotationDegrees) {
        setPosition(x, y);
        setRotationDegrees(rotationDegrees);
        restoreHp();
        lastShotNanos = -BULLET_COOLDOWN_NANOS;
    }

    /**
     * Reports whether health has reached zero.
     *
     * @return true when the tank is destroyed
     */
    public boolean isDestroyed() {
        return hp <= 0;
    }

    /**
     * Gets the tank's current HP.
     *
     * @return current health points
     */
    public int getHp() {
        return hp;
    }

    /**
     * Arms or clears the tank's one-hit shield.
     *
     * @param shieldActive whether the shield is active
     */
    public void setShieldActive(boolean shieldActive) {
        this.shieldActive = shieldActive;
        updateShieldVisual();
    }

    /**
     * Reports whether the next hit will be blocked.
     *
     * @return true when shielded
     */
    public boolean hasShield() {
        return shieldActive;
    }

    /**
     * Gets the default bullet color for special projectiles.
     *
     * @return bullet color
     */
    public Color getBulletColor() {
        return bulletColor;
    }

    /**
     * Gets the tank's current rotation.
     *
     * @return rotation in degrees
     */
    public double getRotationDegrees() {
        return rotationDegrees;
    }

    /**
     * Assigns an absolute rotation to the tank.
     *
     * @param rotationDegrees new rotation in degrees
     */
    public void setRotationDegrees(double rotationDegrees) {
        this.rotationDegrees = normalize(rotationDegrees);
        getView().setRotate(this.rotationDegrees);
    }

    /**
     * Normalizes an angle into the 0-360 degree range.
     *
     * @param degrees raw angle
     * @return normalized angle
     */
    private double normalize(double degrees) {
        double result = degrees % 360.0;
        return result < 0 ? result + 360.0 : result;
    }

    /**
     * Adds a subtle halo while the shield is ready.
     */
    private void updateShieldVisual() {
        getView().setEffect(shieldActive
                ? new javafx.scene.effect.DropShadow(26, Color.rgb(112, 230, 255, 0.9))
                : null);
    }
}
