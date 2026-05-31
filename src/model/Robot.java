package model;

/**
 * Represents a vacuum robot with position, direction, and battery state.
 */
public class Robot {
    private int x;
    private int y;
    private Direction direction;
    private double batteryLevel;
    private final double maxBattery;
    private boolean isCleaning;

    /**
     * Creates a robot at the given position and direction with a max battery capacity.
     *
     * @param startX     starting x position
     * @param startY     starting y position
     * @param startDir   starting direction
     * @param maxBattery maximum battery capacity
     */
    public Robot(int startX, int startY, Direction startDir, double maxBattery) {
        this.x = startX;
        this.y = startY;
        this.direction = startDir;
        this.maxBattery = maxBattery;
        this.batteryLevel = maxBattery;
        this.isCleaning = false;
    }

    /**
     * Attempts to move one step forward based on the current direction.
     * If the next cell is walkable, updates position and drains 1.0 battery.
     * If blocked, turns right and drains 0.5 battery.
     *
     * @param room the room to read walkability from
     */
    public void move(Room room) {
        int nextX = x + direction.getDx();
        int nextY = y + direction.getDy();
        if (room != null && room.isWalkable(nextX, nextY)) {
            x = nextX;
            y = nextY;
            drainBattery(1.0);
        } else {
            direction = direction.turnRight();
            drainBattery(0.5);
        }
    }

    /**
     * Reduces the battery level and clamps it to zero or above.
     *
     * @param amount amount to drain
     */
    public void drainBattery(double amount) {
        batteryLevel -= amount;
        if (batteryLevel < 0) {
            batteryLevel = 0;
        }
    }

    /**
     * Increases the battery level and clamps it to maxBattery or below.
     *
     * @param amount amount to charge
     */
    public void charge(double amount) {
        batteryLevel += amount;
        if (batteryLevel > maxBattery) {
            batteryLevel = maxBattery;
        }
    }

    /**
     * Returns true if the battery level is at or below the given threshold.
     *
     * @param threshold fraction of max battery (0.0 to 1.0)
     * @return true if battery is low
     */
    public boolean isBatteryLow(double threshold) {
        return batteryLevel <= (maxBattery * threshold);
    }

    /**
     * Returns the battery percentage (0 to 100).
     *
     * @return battery percentage
     */
    public double getBatteryPercentage() {
        return (batteryLevel / maxBattery) * 100.0;
    }

    /**
     * Marks the robot as cleaning.
     */
    public void startCleaning() {
        isCleaning = true;
    }

    /**
     * Marks the robot as not cleaning.
     */
    public void stopCleaning() {
        isCleaning = false;
    }

    /**
     * Returns the current x position.
     *
     * @return x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current y position.
     *
     * @return y position
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the current direction.
     *
     * @return direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns the current battery level.
     *
     * @return battery level
     */
    public double getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * Returns the maximum battery capacity.
     *
     * @return max battery
     */
    public double getMaxBattery() {
        return maxBattery;
    }

    /**
     * Returns whether the robot is cleaning.
     *
     * @return true if cleaning
     */
    public boolean isCleaning() {
        return isCleaning;
    }
}
