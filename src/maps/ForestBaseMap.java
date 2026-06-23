package maps;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import obstacles.Obstacle;
import obstacles.TreeObstacle;
import obstacles.WoodenObstacle;

/**
 * Forest map with trees, wooden barricades, and an open middle.
 */
public class ForestBaseMap extends MapData {
    /**
     * Creates the forest base map metadata.
     */
    public ForestBaseMap() {
        super(
                "Forest Base",
                "Trees, wooden obstacles, and an open central battlefield.",
                1000,
                650,
                new Point2D(72, 82),
                new Point2D(876, 526),
                35,
                215);
    }

    /**
     * Builds the forest obstacle layout.
     *
     * @return obstacles for the forest map
     */
    @Override
    public List<Obstacle> createObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new TreeObstacle(230, 65, 62, 82));
        obstacles.add(new TreeObstacle(330, 155, 62, 82));
        obstacles.add(new TreeObstacle(705, 72, 62, 82));
        obstacles.add(new TreeObstacle(780, 185, 62, 82));
        obstacles.add(new TreeObstacle(170, 455, 62, 82));
        obstacles.add(new TreeObstacle(285, 525, 62, 82));
        obstacles.add(new TreeObstacle(645, 470, 62, 82));
        obstacles.add(new TreeObstacle(760, 520, 62, 82));
        obstacles.add(new WoodenObstacle(430, 120, 145, 35));
        obstacles.add(new WoodenObstacle(425, 500, 145, 35));
        obstacles.add(new WoodenObstacle(255, 310, 120, 34));
        obstacles.add(new WoodenObstacle(625, 310, 120, 34));
        return obstacles;
    }
}
