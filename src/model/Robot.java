package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a vacuum robot with position, direction, and battery state.
 */
public class Robot {
    private final IntegerProperty xProperty;
    private final IntegerProperty yProperty;
    private final ObjectProperty<Direction> directionProperty;
    private final DoubleProperty batteryLevelProperty;
    private final double maxBattery;
    private final BooleanProperty cleaningProperty;

    /**
     * Creates a robot at the given position and direction with a max battery capacity.
     *
     * @param startX     starting x position
     * @param startY     starting y position
     * @param startDir   starting direction
     * @param maxBattery maximum battery capacity
     */
    public Robot(int startX, int startY, Direction startDir, double maxBattery) {
        this.xProperty = new SimpleIntegerProperty(startX);
        this.yProperty = new SimpleIntegerProperty(startY);
        this.directionProperty = new SimpleObjectProperty<>(startDir);
        this.maxBattery = maxBattery;
        this.batteryLevelProperty = new SimpleDoubleProperty(maxBattery);
        this.cleaningProperty = new SimpleBooleanProperty(false);
    }

    /**
     * Attempts to move one step forward based on the current direction.
     * If the next cell is walkable, updates position and drains 1.0 battery.
     * If blocked, turns right and drains 0.5 battery.
     *
     * @param room the room to read walkability from
     */
    public void move(Room room) {
        int nextX = getX() + getDirection().getDx();
        int nextY = getY() + getDirection().getDy();
        boolean canMove = room != null && room.isValid(nextX, nextY) && room.isWalkable(nextX, nextY);
        if (canMove) {
            setX(nextX);
            setY(nextY);
            drainBattery(1.0);
        } else {
            setDirection(getDirection().turnRight());
            drainBattery(0.5);
        }
    }

    /**
     * Starts cleaning if the given cell has dirt.
     *
     * @param cell the cell to clean
     */
    public void clean(Cell cell) {
        if (cell != null && cell.getDirt() != null) {
            startCleaning();
        }
    }

    /**
     * Reduces the battery level and clamps it to zero or above.
     *
     * @param amount amount to drain
     */
    public void drainBattery(double amount) {
        batteryLevelProperty.set(Math.max(0.0, batteryLevelProperty.get() - amount));
    }

    /**
     * Increases the battery level and clamps it to maxBattery or below.
     *
     * @param amount amount to charge
     */
    public void charge(double amount) {
        batteryLevelProperty.set(Math.min(maxBattery, batteryLevelProperty.get() + amount));
    }

    /**
     * Returns true if the battery level is at or below the given threshold.
     *
     * @param threshold fraction of max battery (0.0 to 1.0)
     * @return true if battery is low
     */
    public boolean isBatteryLow(double threshold) {
        return batteryLevelProperty.get() <= (maxBattery * threshold);
    }

    /**
     * Returns the battery percentage (0 to 100).
     *
     * @return battery percentage
     */
    public double getBatteryPercentage() {
        return (batteryLevelProperty.get() / maxBattery) * 100.0;
    }

    /**
     * Marks the robot as cleaning.
     */
    public void startCleaning() {
        cleaningProperty.set(true);
    }

    /**
     * Marks the robot as not cleaning.
     */
    public void stopCleaning() {
        cleaningProperty.set(false);
    }

    /**
     * Returns the current x position.
     *
     * @return x position
     */
    public int getX() {
        return xProperty.get();
    }

    public void setX(int x) {
        this.xProperty.set(x);
    }

    /**
     * Returns the current y position.
     *
     * @return y position
     */
    public int getY() {
        return yProperty.get();
    }

    public void setY(int y) {
        this.yProperty.set(y);
    }

    /**
     * Returns the current direction.
     *
     * @return direction
     */
    public Direction getDirection() {
        return directionProperty.get();
    }

    public void setDirection(Direction dir) {
        this.directionProperty.set(dir);
    }

    /**
     * Returns the current battery level.
     *
     * @return battery level
     */
    public double getBatteryLevel() {
        return batteryLevelProperty.get();
    }

    public DoubleProperty batteryLevelProperty() {
        return batteryLevelProperty;
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
        return cleaningProperty.get();
    }

    public BooleanProperty cleaningProperty() {
        return cleaningProperty;
    }

    public IntegerProperty xProperty() {
        return xProperty;
    }

    public IntegerProperty yProperty() {
        return yProperty;
    }

    public ObjectProperty<Direction> directionProperty() {
        return directionProperty;
    }

}
