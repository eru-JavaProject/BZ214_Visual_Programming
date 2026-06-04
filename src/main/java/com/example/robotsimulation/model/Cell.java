package com.example.robotsimulation.model;

public class Cell {

    private CellType cellType;
    private DirtType dirtType;
    private boolean visitedByRobot;
    private Direction visitDirection;
    private Furniture furniture;

    public Cell() {
        this.cellType = CellType.EMPTY;
        this.dirtType = null;
        this.visitedByRobot = false;
        this.visitDirection = null;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public DirtType getDirtType() {
        return dirtType;
    }

    public void setDirtType(DirtType dirtType) {
        this.dirtType = dirtType;
    }

    public boolean hasDirt() {
        return dirtType != null;
    }

    public boolean isFurniture() {
        return cellType == CellType.FURNITURE;
    }

    public Furniture getFurniture() {
        return furniture;
    }

    public void setFurniture(Furniture furniture) {
        this.furniture = furniture;
    }

    public boolean hasFurniture() {
        return furniture != null;
    }

    public boolean isChargingStation() {
        return cellType == CellType.CHARGING_STATION;
    }
    public boolean isVisitedByRobot() {
        return visitedByRobot;
    }

    public void setVisitedByRobot(boolean visitedByRobot) {
        this.visitedByRobot = visitedByRobot;
    }
    public Direction getVisitDirection() {
        return visitDirection;
    }

    public void setVisitDirection(Direction visitDirection) {
        this.visitDirection = visitDirection;
    }
    public void clearDirt() {
        this.dirtType = null;

        if (this.cellType == CellType.DIRT) {
            this.cellType = CellType.EMPTY;
        }
    }
}