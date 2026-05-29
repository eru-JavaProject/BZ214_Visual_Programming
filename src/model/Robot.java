package model;

public class Robot {
    private int x;
    private int y;
    private Direction dir;
    private double battery;

    public Robot(int x, int y, Direction dir, double battery) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.battery = battery;
    }

    public void move() {
        // TODO
    }

    public void clean() {
        // TODO
    }

    public void drainBattery(double amount) {
        // TODO
    }
}

