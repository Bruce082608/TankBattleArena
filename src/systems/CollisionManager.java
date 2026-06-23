package systems;

import java.util.List;
import java.util.Optional;

import entities.GameObject;
import entities.Tank;
import javafx.geometry.Rectangle2D;
import maps.MapData;
import obstacles.Obstacle;

/**
 * Centralizes rectangle-based collision checks for tanks, bullets, and obstacles.
 */
public class CollisionManager {
    /**
     * Determines whether a tank can occupy a proposed position.
     *
     * @param tank tank being moved
     * @param nextX proposed left coordinate
     * @param nextY proposed top coordinate
     * @param obstacles current map obstacles
     * @param mapData current map
     * @return true when the movement is legal
     */
    public boolean canMove(
            Tank tank,
            double nextX,
            double nextY,
            List<Obstacle> obstacles,
            MapData mapData) {
        Rectangle2D nextBox = tank.getProjectedCollisionBox(nextX, nextY);
        if (!isInsideArena(nextBox, mapData)) {
            return false;
        }
        return obstacles.stream()
                .filter(Obstacle::blocksTanks)
                .noneMatch(obstacle -> obstacle.getCollisionBox().intersects(nextBox));
    }

    /**
     * Tests whether an object's current box is outside the map boundaries.
     *
     * @param object object to test
     * @param mapData current map
     * @return true when the object has left the map
     */
    public boolean isOutsideArena(GameObject object, MapData mapData) {
        return !isInsideArena(object.getCollisionBox(), mapData);
    }

    /**
     * Finds the first obstacle intersecting a game object.
     *
     * @param object object to test
     * @param obstacles obstacles to scan
     * @return matching obstacle when a collision exists
     */
    public Optional<Obstacle> findObstacleCollision(GameObject object, List<Obstacle> obstacles) {
        return obstacles.stream()
                .filter(Obstacle::blocksBullets)
                .filter(obstacle -> object.intersects(obstacle))
                .findFirst();
    }

    /**
     * Tests whether two game objects overlap.
     *
     * @param first first object
     * @param second second object
     * @return true when their collision boxes intersect
     */
    public boolean collides(GameObject first, GameObject second) {
        return first.intersects(second);
    }

    /**
     * Tests whether a collision rectangle is fully inside the arena.
     *
     * @param box rectangle to test
     * @param mapData current map
     * @return true when the box remains inside boundaries
     */
    public boolean isInsideArena(Rectangle2D box, MapData mapData) {
        return box.getMinX() >= 0
                && box.getMinY() >= 0
                && box.getMaxX() <= mapData.getWidth()
                && box.getMaxY() <= mapData.getHeight();
    }
}
