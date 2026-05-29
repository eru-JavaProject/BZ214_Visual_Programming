package model;

public class Room {
    private int width;
    private int height;
    private Cell[][] grid;

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        // TODO
    }

    public void placeDirt(int x, int y, DirtType dirtType) {
        // TODO
    }

    public void placeObstacle(int x, int y, CellType obstacleType) {
        // TODO
    }
}

