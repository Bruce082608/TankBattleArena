package obstacles;

import ui.GameArt;

/**
 * Sand barrier obstacle for the desert fortress map.
 */
public class SandBarrier extends Obstacle {
    /**
     * Creates a sand barrier at the requested position.
     *
     * @param x left coordinate
     * @param y top coordinate
     * @param width barrier width
     * @param height barrier height
     */
    public SandBarrier(double x, double y, double width, double height) {
        super(x, y, width, height, GameArt.createSandImage(), width * 0.18, height * 0.20);
    }
}
