package maps;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import obstacles.Obstacle;
import obstacles.SandBarrier;
import obstacles.StoneWall;

/**
 * Desert map with fortress walls, sand barriers, and maze-like routes.
 */
public class DesertFortressMap extends MapData {
    /**
     * Creates the desert fortress map metadata.
     */
    public DesertFortressMap() {
        super(
                "Desert Fortress",
                "Stone walls, sand barriers, and maze-like structure.",
                1000,
                650,
                new Point2D(70, 526),
                new Point2D(876, 82),
                320,
                140);
    }

    /**
     * Builds the desert obstacle layout.
     *
     * @return obstacles for the desert map
     */
    @Override
    public List<Obstacle> createObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new StoneWall(155, 120, 180, 44));
        obstacles.add(new StoneWall(155, 300, 44, 210));
        obstacles.add(new SandBarrier(285, 465, 195, 38));
        obstacles.add(new StoneWall(455, 85, 44, 190));
        obstacles.add(new SandBarrier(370, 300, 260, 38));
        obstacles.add(new StoneWall(505, 375, 44, 190));
        obstacles.add(new SandBarrier(520, 145, 195, 38));
        obstacles.add(new StoneWall(802, 140, 44, 210));
        obstacles.add(new StoneWall(675, 486, 180, 44));
        obstacles.add(new SandBarrier(725, 330, 130, 38));
        return obstacles;
    }
}
