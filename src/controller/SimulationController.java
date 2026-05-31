package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import model.Direction;
import model.Robot;
import model.Room;

public class SimulationController {

    private Room room;
    private Robot robot;

    // Properties expose model state to the view without coupling the model to JavaFX.
    private final DoubleProperty batteryLevelProperty = new SimpleDoubleProperty();
    private final StringProperty positionInfoProperty = new SimpleStringProperty();
    private final DoubleProperty cleanedPercentProperty = new SimpleDoubleProperty();
    private final StringProperty statusProperty = new SimpleStringProperty("IDLE");
    private final IntegerProperty robotXProperty = new SimpleIntegerProperty();
    private final IntegerProperty robotYProperty = new SimpleIntegerProperty();

    private Timeline simulationTimeline;
    private boolean isPaused;

    public SimulationController() {
        initialize();
    }

    /**
     * Initializes model state and sets initial values for UI bindings.
     */
    public void initialize() {
        room = new Room(20, 20);
        robot = new Robot(1, 1, Direction.RIGHT, 100.0);
        batteryLevelProperty.set(robot.getBatteryPercentage());
        positionInfoProperty.set(robot.getX() + "," + robot.getY());
        cleanedPercentProperty.set(0.0);
        statusProperty.set("IDLE");
        robotXProperty.set(robot.getX());
        robotYProperty.set(robot.getY());
        isPaused = false;
    }

    /**
     * Starts the simulation loop with a fixed tick interval.
     */
    public void startSimulation() {
        if (simulationTimeline != null) {
            isPaused = false;
            simulationTimeline.play();
            return;
        }
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            if (isPaused) {
                return;
            }
            robot.move(room);
            robotXProperty.set(robot.getX());
            robotYProperty.set(robot.getY());
        }));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    /**
     * Stops the simulation loop.
     */
    public void pauseSimulation() {
        isPaused = true;
        if (simulationTimeline != null) {
            simulationTimeline.pause();
        }
    }

    /**
     * Resets the model to its initial state and updates bound properties.
     */
    public void resetSimulation() {
        initialize();
    }

    /**
     * Placeholder for returning robot to its charging station.
     */
    public void returnToStation() {
        // TODO: implement pathing back to station.
    }

    /**
     * One tick of the simulation loop.
     */
    public void updateSimulation() {
        System.out.println("Simulation Tick");
    }

    public DoubleProperty getBatteryProperty() {
        return batteryLevelProperty;
    }

    public StringProperty getPositionProperty() {
        return positionInfoProperty;
    }

    public DoubleProperty getCleanedPercentProperty() {
        return cleanedPercentProperty;
    }

    public StringProperty getStatusProperty() {
        return statusProperty;
    }

    // Compatibility getters for existing view bindings.
    public DoubleProperty batteryProperty() {
        return batteryLevelProperty;
    }

    public StringProperty positionProperty() {
        return positionInfoProperty;
    }

    public DoubleProperty cleanedPercentProperty() {
        return cleanedPercentProperty;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public IntegerProperty robotXPropertyProperty() {
        return robotXProperty;
    }

    public IntegerProperty robotYPropertyProperty() {
        return robotYProperty;
    }

    public Room getRoom() {
        return room;
    }

    public Robot getRobot() {
        return robot;
    }
}
