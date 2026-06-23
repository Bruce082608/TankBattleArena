package obstacles;

import ui.GameArt;

/**
 * Tree obstacle used to form the forest base map.
 */
public class TreeObstacle extends Obstacle {
    /**
     * Creates a tree obstacle at the requested position.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width tree width
     * @param height tree height
     */
    public TreeObstacle(double x, double y, double width, double height) {
        super(x, y, width, height, GameArt.createTreeImage(), width * 0.30, height * 0.24);
    }
}
