package model;

public class Cell {
    private int x;
    private int y;
    private CellType type;
    private DirtType dirt;
    private boolean isCleaned;

    public Cell(int x, int y, CellType type, DirtType dirt, boolean isCleaned) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.dirt = dirt;
        this.isCleaned = isCleaned;
    }

    // TODO
}

