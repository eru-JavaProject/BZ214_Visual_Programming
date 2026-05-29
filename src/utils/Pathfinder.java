package utils;

import model.Room;
import java.util.List;

public interface Pathfinder {
    class Point {
        public int x;
        public int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    List<Point> findPath(Room room, Point start, Point end);
}

