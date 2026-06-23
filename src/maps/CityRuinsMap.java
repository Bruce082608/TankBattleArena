package maps;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import obstacles.BrickWall;
import obstacles.Obstacle;
import obstacles.StoneWall;

/**
 * City map with brick walls, concrete blocks, and narrow lanes.
 */
public class CityRuinsMap extends MapData {
    /**
     * Creates the city ruins map metadata.
     */
    public CityRuinsMap() {
        super(
                "City Ruins",
                "Brick walls, concrete blocks, and narrow passages.",
                1000,
                650,
                new Point2D(70, 304),
                new Point2D(876, 304),
                0,
                180);
    }

    /**
     * Builds the city obstacle layout.
     *
     * @return obstacles for the city map
     */
    @Override
    public List<Obstacle> createObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new BrickWall(190, 75, 52, 175));
        obstacles.add(new BrickWall(190, 395, 52, 175));
        obstacles.add(new StoneWall(330, 0, 58, 145));
        obstacles.add(new StoneWall(330, 505, 58, 145));
        obstacles.add(new BrickWall(470, 210, 62, 230));
        obstacles.add(new StoneWall(615, 0, 58, 145));
        obstacles.add(new StoneWall(615, 505, 58, 145));
        obstacles.add(new BrickWall(760, 75, 52, 175));
        obstacles.add(new BrickWall(760, 395, 52, 175));
        obstacles.add(new StoneWall(360, 300, 95, 52));
        obstacles.add(new StoneWall(545, 300, 95, 52));
        return obstacles;
    }
}
