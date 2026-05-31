package model;

public class Cell {
    private final int x;
    private final int y;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public DirtType getDirt() {
        return dirt;
    }

    public void setDirt(DirtType dirt) {
        this.dirt = dirt;
    }

    public boolean isCleaned() {
        return isCleaned;
    }

    public void cleanDirt() {
        this.dirt = null;
        this.isCleaned = true;

    }

}
