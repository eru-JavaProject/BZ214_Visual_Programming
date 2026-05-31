package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    private Timeline simulationTimeline;

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
    }

    /**
     * Starts the simulation loop with a fixed tick interval.
     */
    public void startSimulation() {
        if (simulationTimeline != null) {
            simulationTimeline.play();
            return;
        }
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> updateSimulation()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    /**
     * Stops the simulation loop.
     */
    public void pauseSimulation() {
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

    public Room getRoom() {
        return room;
    }

    public Robot getRobot() {
        return robot;
    }
}
