package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.Cell;
import model.CellType;
import model.Direction;
import model.DirtType;
import model.Robot;
import model.Room;
import view.RoomViewController;

public class SimulationController {

    private Room room;
    private Robot robot;
    private RoomViewController view;

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

        // Bind controller properties to robot properties where possible.
        batteryLevelProperty.bind(robot.batteryLevelProperty());
        robotXProperty.bind(robot.xProperty());
        robotYProperty.bind(robot.yProperty());
        positionInfoProperty.set(robot.getX() + "," + robot.getY());
        cleanedPercentProperty.set(0.0);
        statusProperty.set("IDLE");
        isPaused = false;
        if (view != null) {
            view.initializeGrid(20, 20);
            syncViewWithModel();
        }
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
        simulationTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.5), event -> {
            if (isPaused) {
                return;
            }
            robot.move(room);
            // properties bound to robot will update automatically; update positionInfo and view sync.
            positionInfoProperty.set(robot.getX() + "," + robot.getY());
            syncViewWithModel();
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

    public void syncViewWithModel() {
        if (view == null || room == null) {
            return;
        }
        int height = 20;
        int width = 20;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = room.getCell(x, y);
                RoomViewController.CellState state = mapState(cell);
                boolean hasDirt = cell != null && cell.getDirt() != null;
                view.updateCellVisual(x, y, state, hasDirt);
            }
        }
        view.updateCellVisual(robot.getX(), robot.getY(), RoomViewController.CellState.ROBOT, false);
    }

    private RoomViewController.CellState mapState(Cell cell) {
        if (cell == null) {
            return RoomViewController.CellState.EMPTY;
        }
        CellType type = cell.getType();
        if (type == CellType.WALL) {
            return RoomViewController.CellState.WALL;
        }
        if (type == CellType.FURNITURE) {
            return RoomViewController.CellState.FURNITURE;
        }
        if (type == CellType.CHARGING_STATION) {
            return RoomViewController.CellState.CHARGING_STATION;
        }
        DirtType dirt = cell.getDirt();
        if (dirt == DirtType.DUST) {
            return RoomViewController.CellState.DUST;
        }
        if (dirt == DirtType.LIQUID) {
            return RoomViewController.CellState.LIQUID;
        }
        if (dirt == DirtType.STAIN) {
            return RoomViewController.CellState.STAIN;
        }
        return RoomViewController.CellState.EMPTY;
    }

    public void setView(RoomViewController view) {
        this.view = view;
        view.initializeGrid(20, 20);
        syncViewWithModel();
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
