package com.example.robotsimulation.model;

public class Furniture {
    private Position position;
    private FurnitureType type;

    public Furniture(Position position, FurnitureType type) {
        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public FurnitureType getType() {
        return type;
    }
}