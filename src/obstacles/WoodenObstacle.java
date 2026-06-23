package obstacles;

import ui.GameArt;

/**
 * Wooden barricade obstacle for the forest battlefield.
 */
public class WoodenObstacle extends Obstacle {
    /**
     * Creates a wooden barricade at the requested position.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width barricade width
     * @param height barricade height
     */
    public WoodenObstacle(double x, double y, double width, double height) {
        super(x, y, width, height, GameArt.createWoodImage(), width * 0.14, height * 0.24);
    }
}
