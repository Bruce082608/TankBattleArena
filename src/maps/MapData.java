package maps;

import java.util.List;

import javafx.geometry.Point2D;
import obstacles.Obstacle;

/**
 * Abstract definition of a playable arena map.
 */
public abstract class MapData {
    private final String name;
    private final String description;
    private final double width;
    private final double height;
    private final Point2D playerOneSpawn;
    private final Point2D playerTwoSpawn;
    private final double playerOneRotation;
    private final double playerTwoRotation;

    /**
     * Creates immutable metadata shared by all map implementations.
     *
     * @param name map name
     * @param description short map feature description
     * @param width arena width
     * @param height arena height
     * @param playerOneSpawn player one spawn point
     * @param playerTwoSpawn player two spawn point
     * @param playerOneRotation player one spawn rotation
     * @param playerTwoRotation player two spawn rotation
     */
    protected MapData(
            String name,
            String description,
            double width,
            double height,
            Point2D playerOneSpawn,
            Point2D playerTwoSpawn,
            double playerOneRotation,
            double playerTwoRotation) {
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.playerOneSpawn = playerOneSpawn;
        this.playerTwoSpawn = playerTwoSpawn;
        this.playerOneRotation = playerOneRotation;
        this.playerTwoRotation = playerTwoRotation;
    }

    /**
     * Creates the obstacle layout for a new match or round.
     *
     * @return list of obstacles for the map
     */
    public abstract List<Obstacle> createObstacles();

    /**
     * Gets the map name.
     *
     * @return map name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a short description of the map.
     *
     * @return map description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the map width.
     *
     * @return width in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the map height.
     *
     * @return height in pixels
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets player one's spawn point.
     *
     * @return spawn point
     */
    public Point2D getPlayerOneSpawn() {
        return playerOneSpawn;
    }

    /**
     * Gets player two's spawn point.
     *
     * @return spawn point
     */
    public Point2D getPlayerTwoSpawn() {
        return playerTwoSpawn;
    }

    /**
     * Gets player one's spawn rotation.
     *
     * @return rotation in degrees
     */
    public double getPlayerOneRotation() {
        return playerOneRotation;
    }

    /**
     * Gets player two's spawn rotation.
     *
     * @return rotation in degrees
     */
    public double getPlayerTwoRotation() {
        return playerTwoRotation;
    }
}
