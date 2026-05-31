package model;

public class Room {
    private final int width;
    private final int height;
    private final Cell[][] grid;

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        initializeGrid();
        setupBorders();
    }

    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell(x, y, CellType.EMPTY, null, true);
            }
        }
    }

    private void setupBorders() {
        for (int x = 0; x < width; x++) {
            grid[0][x].setType(CellType.WALL);
            grid[height - 1][x].setType(CellType.WALL);
        }
        for (int y = 0; y < height; y++) {
            grid[y][0].setType(CellType.WALL);
            grid[y][width - 1].setType(CellType.WALL);
        }
    }

    public Cell getCell(int x, int y) {
        return isValid(x, y) ? grid[y][x] : null;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isWalkable(int x, int y) {
        if (!isValid(x, y)) {
            return false;
        }
        CellType type = grid[y][x].getType();
        return type == CellType.EMPTY || type == CellType.CHARGING_STATION;
    }

    public void addObstacle(int x, int y, CellType type) {
        if (!isValid(x, y)) {
            return;
        }
        grid[y][x].setType(type);
    }

    public void addDirt(int x, int y, DirtType dirtType) {
        if (!isValid(x, y) || !isWalkable(x, y)) {
            return;
        }
        grid[y][x].setDirt(dirtType);
    }
}
