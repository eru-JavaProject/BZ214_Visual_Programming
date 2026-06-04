package com.example.robotsimulation.model;

public class Robot {

    private Position position;
    private Direction direction;
    private int batteryLevel;
    private double speed;
    private int cleaningTimeRemaining;

    public Robot(int row, int col) {
        this.position = new Position(row, col);
        this.direction = Direction.RIGHT;
        this.batteryLevel = 100;
        this.speed = 1.0;
        this.cleaningTimeRemaining = 0;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public int getCleaningTimeRemaining() {
        return cleaningTimeRemaining;
    }

    public void setCleaningTimeRemaining(int cleaningTimeRemaining) {
        this.cleaningTimeRemaining = cleaningTimeRemaining;
    }
}