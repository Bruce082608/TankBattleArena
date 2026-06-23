package obstacles;

import ui.GameArt;

/**
 * Brick wall obstacle used heavily in the city map.
 */
public class BrickWall extends Obstacle {
    /**
     * Creates a brick wall at the requested position.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width wall width
     * @param height wall height
     */
    public BrickWall(double x, double y, double width, double height) {
        super(x, y, width, height, GameArt.createBrickWallImage(), width * 0.16, height * 0.16);
    }
}
