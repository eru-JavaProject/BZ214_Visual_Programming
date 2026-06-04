package com.example.robotsimulation.model;

public class Room {

    private final int rows;
    private final int columns;
    private final Cell[][] grid;
    private final Position chargingStationPosition;

    public Room(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new Cell[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col] = new Cell();
            }
        }

        this.chargingStationPosition = new Position(0, 3);
        grid[0][3].setCellType(CellType.CHARGING_STATION);

        initializeDefaultFurniture();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public boolean isInsideRoom(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public boolean isCellWalkable(int row, int col) {
        if (!isInsideRoom(row, col)) {
            return false;
        }

        Cell cell = getCell(row, col);

        if (cell.hasFurniture()) {
            return false;
        }

        if (cell.isChargingStation()) {
            return false;
        }

        return true;
    }

    public Position getChargingStationPosition() {
        return chargingStationPosition;
    }

    public int getTotalCellCount() {
        return rows * columns;
    }

    public int getDirtyCellCount() {
        int count = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (grid[row][col].hasDirt()) {
                    count++;
                }
            }
        }

        return count;
    }

    public boolean addFurniture(int row, int col, FurnitureType type) {
        int width = type.getWidthInCells();
        int height = type.getHeightInCells();

        if (!canPlaceFurniture(row, col, width, height)) {
            return false;
        }

        Position position = new Position(row, col);
        Furniture furniture = new Furniture(position, type);

        for (int r = row; r < row + height; r++) {
            for (int c = col; c < col + width; c++) {
                getCell(r, c).setFurniture(furniture);
            }
        }

        return true;
    }

    private boolean canPlaceFurniture(int row, int col, int width, int height) {
        for (int r = row; r < row + height; r++) {
            for (int c = col; c < col + width; c++) {
                if (!isInsideRoom(r, c)) {
                    return false;
                }

                Cell cell = getCell(r, c);

                if (cell.hasFurniture() || cell.isChargingStation() || cell.hasDirt()) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getFurnitureCellCount() {
        int count = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (grid[row][col].hasFurniture()) {
                    count++;
                }
            }
        }

        return count;
    }

    public int getCleanableCellCount() {
        return getTotalCellCount() - getFurnitureCellCount() - 1;
    }

    public boolean addDirt(int row, int col, DirtType dirtType) {
        if (!isInsideRoom(row, col)) {
            return false;
        }

        Cell cell = getCell(row, col);

        if (cell.hasFurniture() || cell.isChargingStation() || cell.hasDirt()) {
            return false;
        }

        cell.setCellType(CellType.DIRT);
        cell.setDirtType(dirtType);

        return true;
    }

    private void initializeDefaultFurniture() {

        addFurniture(4, 10, FurnitureType.SOFA);

        addFurniture(10, 10, FurnitureType.SOFA);

        addFurniture(7, 14, FurnitureType.ARMCHAIR);

        addFurniture(7, 10, FurnitureType.COFFEE_TABLE);

        addFurniture(7, 3, FurnitureType.TABLE);

        addFurniture(6, 4, FurnitureType.CHAIR);
        addFurniture(6, 5, FurnitureType.CHAIR);


        addFurniture(12, 15, FurnitureType.FLOWER);
        addFurniture(2, 17, FurnitureType.FLOWER);
        addFurniture(11, 1, FurnitureType.FLOWER);
    }
}