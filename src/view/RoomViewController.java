package view;

import controller.SimulationController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class RoomViewController {

    private final SimulationController controller;

    @FXML private GridPane roomGrid;
    @FXML private Slider speedSlider;
    @FXML private ComboBox<String> dirtTypeDropdown;
    @FXML private Label batteryLabel;
    @FXML private Label positionLabel;
    @FXML private Label statusLabel;
    @FXML private Label cleanedPercentLabel;

    public RoomViewController(SimulationController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        batteryLabel.textProperty().bind(controller.batteryProperty().asString("Battery: %.1f%%"));
        positionLabel.textProperty().bind(controller.positionProperty().concat(" Position"));
        statusLabel.textProperty().bind(controller.statusProperty());
        cleanedPercentLabel.textProperty().bind(controller.cleanedPercentProperty().asString("Cleaned: %.1f%%"));
        // TODO
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

