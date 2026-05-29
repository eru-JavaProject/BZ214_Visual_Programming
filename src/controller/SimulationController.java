package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import model.Robot;
import model.Room;
import model.DirtType;
import model.CellType;

public class SimulationController {

    private Room room;
    private Robot robot;

    private final SimpleDoubleProperty batteryProperty = new SimpleDoubleProperty(100.0);
    private final SimpleStringProperty positionProperty = new SimpleStringProperty("0,0");
    private final SimpleStringProperty statusProperty = new SimpleStringProperty("IDLE");
    private final SimpleDoubleProperty cleanedPercentProperty = new SimpleDoubleProperty(0.0);

    public SimulationController() {
        // TODO
    }

    public SimpleDoubleProperty batteryProperty() { return batteryProperty; }
    public SimpleStringProperty positionProperty() { return positionProperty; }
    public SimpleStringProperty statusProperty() { return statusProperty; }
    public SimpleDoubleProperty cleanedPercentProperty() { return cleanedPercentProperty; }

    public void initializeGrid(int width, int height) {
        // TODO
    }

    public void startSimulation() {
        // TODO
    }

    public void pauseSimulation() {
        // TODO
    }

    public void resetSimulation() {
        // TODO
    }

    public void returnToStation() {
        // TODO
    }

    public void addDirt(int x, int y, DirtType type) {
        // TODO
    }

    public void addObstacle(int x, int y, CellType type) {
        // TODO
    }

    // TODO: Timeline/AnimationTimer loop
}

