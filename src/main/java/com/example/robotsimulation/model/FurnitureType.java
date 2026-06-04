package com.example.robotsimulation.model;

public enum FurnitureType {
    SOFA("images/sofa.png", 3, 2, 1.85),
    ARMCHAIR("images/armchair.png", 2, 2, 2.00),
    CHAIR("images/chair.png", 1, 1, 3.30),
    TABLE("images/table.png", 4, 3, 1.80),
    COFFEE_TABLE("images/coffee_table.png", 3, 2, 2.00),
    FLOWER("images/flower.png", 1, 1, 3.00);


    private final String imagePath;
    private final int widthInCells;
    private final int heightInCells;
    private final double renderScale;

    FurnitureType(String imagePath, int widthInCells, int heightInCells, double renderScale) {
        this.imagePath = imagePath;
        this.widthInCells = widthInCells;
        this.heightInCells = heightInCells;
        this.renderScale = renderScale;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getWidthInCells() {
        return widthInCells;
    }

    public int getHeightInCells() {
        return heightInCells;
    }

    public double getRenderScale() {
        return renderScale;
    }
}