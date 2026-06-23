package obstacles;

import ui.GameArt;

/**
 * Stone or concrete wall obstacle for fortified areas.
 */
public class StoneWall extends Obstacle {
    /**
     * Creates a stone wall at the requested position.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width wall width
     * @param height wall height
     */
    public StoneWall(double x, double y, double width, double height) {
        super(x, y, width, height, GameArt.createStoneWallImage(), width * 0.16, height * 0.16);
    }
}
