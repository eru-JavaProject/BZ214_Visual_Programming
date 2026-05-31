package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class RoomViewController {

    public enum CellState {
        EMPTY,
        WALL,
        FURNITURE,
        DUST,
        LIQUID,
        STAIN,
        CHARGING_STATION,
        ROBOT
    }

    private static final double CELL_SIZE = 30.0;

    private final controller.SimulationController controller;

    @FXML private GridPane roomGrid;
    @FXML private Slider speedSlider;
    @FXML private ComboBox<String> dirtTypeDropdown;
    @FXML private Label batteryLabel;
    @FXML private Label positionLabel;
    @FXML private Label statusLabel;
    @FXML private Label cleanedPercentLabel;

    public RoomViewController(controller.SimulationController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        batteryLabel.textProperty().bind(controller.batteryProperty().asString("Battery: %.1f%%"));
        positionLabel.textProperty().bind(controller.positionProperty().concat(" Position"));
        statusLabel.textProperty().bind(controller.statusProperty());
        cleanedPercentLabel.textProperty().bind(controller.cleanedPercentProperty().asString("Cleaned: %.1f%%"));
        controller.setView(this);
    }

    public void initializeGrid(int width, int height) {
        roomGrid.getChildren().clear();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.web("#F5F5F5"));
                cell.setStroke(Color.web("#DDDDDD"));
                roomGrid.add(cell, x, y);
            }
        }
    }

    public void updateCellVisual(int x, int y, CellState state, boolean hasDirt) {
        Rectangle cell = getCellRectangle(x, y);
        if (cell == null) {
            return;
        }
        cell.setFill(resolveColor(state, hasDirt));
        if (state == CellState.ROBOT) {
            removeRobotMarkers();
            Circle robot = new Circle(CELL_SIZE / 3.0, Color.web("#FF4500"));
            roomGrid.add(robot, x, y);
        }
    }

    private void removeRobotMarkers() {
        roomGrid.getChildren().removeIf(node -> node instanceof Circle);
    }

    private Rectangle getCellRectangle(int x, int y) {
        for (var node : roomGrid.getChildren()) {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (node instanceof Rectangle && col != null && row != null && col == x && row == y) {
                return (Rectangle) node;
            }
        }
        return null;
    }

    private Color resolveColor(CellState state, boolean hasDirt) {
        if (hasDirt) {
            return switch (state) {
                case DUST -> Color.web("#87CEEB");
                case LIQUID -> Color.web("#90EE90");
                case STAIN -> Color.web("#FFB6C1");
                default -> Color.web("#F5F5F5");
            };
        }
        return switch (state) {
            case EMPTY -> Color.web("#F5F5F5");
            case WALL -> Color.web("#555555");
            case FURNITURE -> Color.web("#8B4513");
            case CHARGING_STATION -> Color.web("#FFD700");
            case ROBOT -> Color.web("#F5F5F5");
            default -> Color.web("#F5F5F5");
        };
    }

    @FXML
    private void handleStart() {
        controller.startSimulation();
    }

    @FXML
    private void handlePause() {
        controller.pauseSimulation();
    }

    @FXML
    private void handleReset() {
        controller.resetSimulation();
    }

    @FXML
    private void handleReturn() {
        controller.returnToStation();
    }
}
