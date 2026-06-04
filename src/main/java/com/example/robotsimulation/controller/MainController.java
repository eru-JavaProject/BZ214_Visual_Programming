package com.example.robotsimulation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import com.example.robotsimulation.model.CleaningAlgorithm;
import com.example.robotsimulation.model.Direction;
import com.example.robotsimulation.model.DirtType;
import com.example.robotsimulation.model.FurnitureType;
import com.example.robotsimulation.model.Position;
import com.example.robotsimulation.model.Robot;
import com.example.robotsimulation.model.Room;

import com.example.robotsimulation.service.SimulationService;
import com.example.robotsimulation.view.RoomView;

import java.util.List;

public class MainController {

    @FXML
    private Pane roomPane;

    @FXML
    private Label robotPositionLabel;

    @FXML
    private Label robotDirectionLabel;

    @FXML
    private Label batteryPercentLabel;

    @FXML
    private ProgressBar batteryProgressBar;

    @FXML
    private Slider speedSlider;

    @FXML
    private Slider batterySlider;

    @FXML
    private Label speedValueLabel;

    @FXML
    private Button addDirtButton;

    @FXML
    private Button addFurnitureButton;

    @FXML
    private ToggleGroup dirtTypeGroup;

    @FXML
    private ToggleButton dustButton;

    @FXML
    private ToggleButton liquidButton;

    @FXML
    private ToggleButton stainButton;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button returnStationButton;

    @FXML
    private Label totalAreaLabel;

    @FXML
    private Label cleanedAreaLabel;

    @FXML
    private Label remainingAreaLabel;

    @FXML
    private Label elapsedTimeLabel;

    @FXML
    private Label collectedDustLabel;

    @FXML
    private ToggleButton randomToggleButton;

    @FXML
    private ToggleButton spiralToggleButton;

    @FXML
    private ToggleButton wallFollowToggleButton;

    @FXML
    private ComboBox<String> furnitureComboBox;

    private final int columns = 20;
    private final int rows = 14;

    private CleaningAlgorithm selectedAlgorithm = CleaningAlgorithm.RANDOM;

    private List<Position> pathToStation;
    private boolean returningToStation = false;
    private boolean chargingMode = false;

    private int elapsedSeconds = 0;
    private Timeline elapsedTimeline;

    private RoomView roomView;
    private Room room;
    private Robot robot;
    private SimulationService simulationService;

    private String selectedTool = "DIRT";
    private FurnitureType selectedFurnitureType = FurnitureType.SOFA;

    private double robotSpeed = 1.0;

    private Timeline robotTimeline;
    private boolean simulationRunning = false;

    @FXML
    public void initialize() {

        roomView = new RoomView(rows, columns);
        room = new Room(rows, columns);
        robot = new Robot(8, 17);
        simulationService = new SimulationService();

        robotPositionLabel.setText("(0, 0)");
        robotDirectionLabel.setText("Doğu →");
        batteryPercentLabel.setText("%100");
        batterySlider.setValue(100);

        batteryProgressBar.setProgress(1.0);

        totalAreaLabel.setText("260 m²");
        cleanedAreaLabel.setText("0 m² (0%)");
        remainingAreaLabel.setText("260 m² (%100)");
        elapsedTimeLabel.setText("00:00");
        collectedDustLabel.setText("%0");

        setupButtons();
        setupFurnitureComboBox();
        setupAlgorithmToggleButtons();
        setupSpeedSlider();
        setupRoomClickEvent();

        batterySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (batterySlider.isValueChanging()) {
                int batteryValue = newVal.intValue();

                robot.setBatteryLevel(batteryValue);
                updateBatteryUI();

                if (batteryValue > 20) {
                    returningToStation = false;
                    chargingMode = false;
                }
            }
        });

        addDirtButton.fire();

        drawRoom();

        roomPane.widthProperty().addListener((obs, oldVal, newVal) -> drawRoom());
        roomPane.heightProperty().addListener((obs, oldVal, newVal) -> drawRoom());
    }

    private void setupButtons() {
        returnStationButton.setOnAction(event -> {
            pathToStation = simulationService.getPathToChargingStation(robot, room);
            returningToStation = true;
        });

        addDirtButton.setOnAction(event -> {
            selectedTool = "DIRT";

            addDirtButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-background-radius: 6;");
            addFurnitureButton.setStyle("-fx-background-color: #2eaf5f; -fx-text-fill: white; -fx-background-radius: 6; -fx-opacity: 0.55;");
        });

        addFurnitureButton.setOnAction(event -> {
            selectedTool = "FURNITURE";

            addDirtButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-background-radius: 6; -fx-opacity: 0.55;");
            addFurnitureButton.setStyle("-fx-background-color: #2eaf5f; -fx-text-fill: white; -fx-background-radius: 6;");
        });
    }

    private void setupFurnitureComboBox() {
        furnitureComboBox.getItems().addAll(
                "Kanepe",
                "Masa",
                "Orta Sehpa",
                "Tekli Koltuk",
                "Sandalye",
                "Çiçek"
        );

        furnitureComboBox.setValue("Kanepe");

        furnitureComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            switch (newValue) {
                case "Kanepe":
                    selectedFurnitureType = FurnitureType.SOFA;
                    break;

                case "Masa":
                    selectedFurnitureType = FurnitureType.TABLE;
                    break;

                case "Orta Sehpa":
                    selectedFurnitureType = FurnitureType.COFFEE_TABLE;
                    break;

                case "Tekli Koltuk":
                    selectedFurnitureType = FurnitureType.ARMCHAIR;
                    break;

                case "Sandalye":
                    selectedFurnitureType = FurnitureType.CHAIR;
                    break;

                case "Çiçek":
                    selectedFurnitureType = FurnitureType.FLOWER;
                    break;
            }
        });
    }

    private void setupAlgorithmToggleButtons() {
        ToggleGroup algorithmToggleGroup = new ToggleGroup();

        randomToggleButton.setToggleGroup(algorithmToggleGroup);
        spiralToggleButton.setToggleGroup(algorithmToggleGroup);
        wallFollowToggleButton.setToggleGroup(algorithmToggleGroup);

        randomToggleButton.setSelected(true);
        selectedAlgorithm = CleaningAlgorithm.RANDOM;

        randomToggleButton.setOnAction(event -> selectedAlgorithm = CleaningAlgorithm.RANDOM);
        spiralToggleButton.setOnAction(event -> selectedAlgorithm = CleaningAlgorithm.SPIRAL);
        wallFollowToggleButton.setOnAction(event -> selectedAlgorithm = CleaningAlgorithm.WALL_FOLLOW);
    }

    private void setupSpeedSlider() {
        robotSpeed = speedSlider.getValue();
        speedValueLabel.setText(String.format("%.1fx", robotSpeed));

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            robotSpeed = newVal.doubleValue();
            speedValueLabel.setText(String.format("%.1fx", robotSpeed));

            if (robotTimeline != null) {
                robotTimeline.setRate(robotSpeed);
            }
        });
    }

    private void setupRoomClickEvent() {
        roomPane.setOnMouseClicked(event -> {
            double wallThickness = roomView.getWallThickness();

            double availableWidth = roomPane.getWidth() - (wallThickness * 2);
            double availableHeight = roomPane.getHeight() - (wallThickness * 2);

            double cellWidth = availableWidth / columns;
            double cellHeight = availableHeight / rows;

            double mouseX = event.getX();
            double mouseY = event.getY();

            int clickedCol = (int) ((mouseX - wallThickness) / cellWidth);
            int clickedRow = (int) ((mouseY - wallThickness) / cellHeight);

            System.out.println("Clicked row=" + clickedRow + ", col=" + clickedCol);

            if (clickedRow >= 0 && clickedRow < rows && clickedCol >= 0 && clickedCol < columns) {
                handleCellClick(clickedRow, clickedCol);
            }
        });
    }

    @FXML
    private void onStartClicked() {
        simulationRunning = true;

        if (robotTimeline != null) {
            robotTimeline.stop();
        }

        robotTimeline = new Timeline(
                new KeyFrame(Duration.millis(1000), event -> moveRobot())
        );

        robotTimeline.setCycleCount(Timeline.INDEFINITE);
        robotTimeline.setRate(robotSpeed);
        robotTimeline.play();

        if (elapsedTimeline == null) {
            elapsedTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), event -> {
                        elapsedSeconds++;
                        updateStatistics();
                    })
            );

            elapsedTimeline.setCycleCount(Timeline.INDEFINITE);
        }

        elapsedTimeline.play();

        System.out.println("Simulation started");
    }

    @FXML
    private void onPauseClicked() {
        simulationRunning = false;

        if (robotTimeline != null) {
            robotTimeline.pause();
        }

        if (elapsedTimeline != null) {
            elapsedTimeline.pause();
        }

        System.out.println("Simulation paused");
    }

    @FXML
    private void onResetClicked() {
        if (robotTimeline != null) {
            robotTimeline.stop();
        }

        if (elapsedTimeline != null) {
            elapsedTimeline.stop();
            elapsedTimeline = null;
        }

        simulationService.resetWallFollowState();

        room = new Room(rows, columns);
        robot = new Robot(8, 17);

        pathToStation = null;
        returningToStation = false;
        chargingMode = false;
        simulationRunning = false;
        elapsedSeconds = 0;

        robot.setBatteryLevel(100);
        updateBatteryUI();

        elapsedTimeLabel.setText("00:00");

        drawRoom();

        System.out.println("Simulation reset");
    }

    private void moveRobot() {

        if (chargingMode) {
            chargeRobot();
            return;
        }

        if (returningToStation) {
            moveRobotToChargingStation();
            return;
        }

        if (simulationService.shouldReturnToStation(robot)) {
            pathToStation = simulationService.getPathToChargingStation(robot, room);
            returningToStation = true;
            System.out.println("Battery low. Returning to charging station.");
            return;
        }

        moveRobotWithObstacleControl();

        drawRoom();

        System.out.println(
                "Robot moved: " +
                        robot.getPosition().getCol() + "," +
                        robot.getPosition().getRow()
        );
    }

    private void chargeRobot() {
        if (robot.getBatteryLevel() < 100) {
            robot.setBatteryLevel(
                    Math.min(100, robot.getBatteryLevel() + 3)
            );

            drawRoom();
            return;
        }

        chargingMode = false;
        System.out.println("Battery fully charged.");
    }

    private void moveRobotToChargingStation() {
        if (pathToStation != null && !pathToStation.isEmpty()) {
            Position nextPosition = pathToStation.remove(0);

            if (!canRobotMoveTo(nextPosition) && !isChargingStationPosition(nextPosition)) {
                System.out.println("Return path blocked. Recalculating path...");
                pathToStation = simulationService.getPathToChargingStation(robot, room);
                return;
            }

            updateRobotDirectionForPath(nextPosition);

            robot.setPosition(nextPosition);
            markRobotVisitedCell(nextPosition);

            robot.setBatteryLevel(robot.getBatteryLevel() - 1);

            drawRoom();
            return;
        }

        returningToStation = false;
        chargingMode = true;

        System.out.println("Robot charging station'a ulaştı.");
        drawRoom();
    }

    private void moveRobotWithObstacleControl() {
        Position oldPosition = robot.getPosition();

        simulationService.moveRobot(robot, room, selectedAlgorithm);

        Position newPosition = robot.getPosition();

        if (!canRobotMoveTo(newPosition)) {
            robot.setPosition(oldPosition);
            System.out.println("Obstacle detected. Robot stayed in place.");
            return;
        }

        markRobotVisitedCell(newPosition);

        if (room.getCell(newPosition.getRow(), newPosition.getCol()).hasDirt()) {
            room.getCell(newPosition.getRow(), newPosition.getCol()).clearDirt();
        }

        robot.setBatteryLevel(robot.getBatteryLevel() - 1);
    }

    private boolean canRobotMoveTo(Position position) {
        return room.isCellWalkable(position.getRow(), position.getCol());
    }

    private boolean isChargingStationPosition(Position position) {
        Position stationPosition = room.getChargingStationPosition();

        return stationPosition != null
                && stationPosition.getRow() == position.getRow()
                && stationPosition.getCol() == position.getCol();
    }

    private void markRobotVisitedCell(Position position) {
        room.getCell(position.getRow(), position.getCol()).setVisitedByRobot(true);
        room.getCell(position.getRow(), position.getCol()).setVisitDirection(robot.getDirection());
    }

    private void updateBatteryUI() {
        int batteryValue = robot.getBatteryLevel();

        batteryPercentLabel.setText("%" + batteryValue);
        batteryProgressBar.setProgress(batteryValue / 100.0);

        if (batterySlider != null && (int) batterySlider.getValue() != batteryValue) {
            batterySlider.setValue(batteryValue);
        }
    }

    private void updateRobotStatus() {
        Position position = robot.getPosition();

        robotPositionLabel.setText("(" + position.getCol() + ", " + position.getRow() + ")");

        switch (robot.getDirection()) {
            case RIGHT:
                robotDirectionLabel.setText("Doğu →");
                break;

            case LEFT:
                robotDirectionLabel.setText("Batı ←");
                break;

            case UP:
                robotDirectionLabel.setText("Kuzey ↑");
                break;

            case DOWN:
                robotDirectionLabel.setText("Güney ↓");
                break;
        }

        updateBatteryUI();
    }

    private void handleCellClick(int row, int col) {
        Position robotPosition = robot.getPosition();

        if (row == robotPosition.getRow() && col == robotPosition.getCol()) {
            return;
        }

        boolean success = false;

        if (selectedTool.equals("DIRT")) {
            success = room.addDirt(row, col, getSelectedDirtType());

            if (success) {
                System.out.println("Added dirt type: " + getSelectedDirtType());
            } else {
                System.out.println("Dirt could not be added to this cell.");
            }
        }

        if (selectedTool.equals("FURNITURE")) {
            success = room.addFurniture(row, col, selectedFurnitureType);

            if (success) {
                System.out.println("Added furniture: " + selectedFurnitureType);
            } else {
                System.out.println("Furniture could not be added to this cell.");
            }
        }

        drawRoom();
    }

    private DirtType getSelectedDirtType() {
        if (dustButton.isSelected()) {
            return DirtType.DUST;
        }

        if (liquidButton.isSelected()) {
            return DirtType.LIQUID;
        }

        if (stainButton.isSelected()) {
            return DirtType.STAIN;
        }

        return DirtType.DUST;
    }

    private void updateStatistics() {
        int totalArea = room.getCleanableCellCount();
        int remainingDirtyArea = room.getDirtyCellCount();
        int cleanedArea = totalArea - remainingDirtyArea;

        double cleanedPercentage = 0;

        if (totalArea > 0) {
            cleanedPercentage = (cleanedArea * 100.0) / totalArea;
        }

        totalAreaLabel.setText(totalArea + " m²");
        cleanedAreaLabel.setText(
                cleanedArea + " m² (" + String.format("%.0f", cleanedPercentage) + "%)"
        );
        remainingAreaLabel.setText(
                remainingDirtyArea + " m² (%" + String.format("%.0f", 100 - cleanedPercentage) + ")"
        );
        collectedDustLabel.setText("%" + String.format("%.0f", cleanedPercentage));

        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;

        elapsedTimeLabel.setText(
                String.format("%02d:%02d", minutes, seconds)
        );
    }

    private void updateRobotDirectionForPath(Position nextPosition) {
        Position currentPosition = robot.getPosition();

        int rowDifference = nextPosition.getRow() - currentPosition.getRow();
        int colDifference = nextPosition.getCol() - currentPosition.getCol();

        if (colDifference == 1) {
            robot.setDirection(Direction.RIGHT);
        } else if (colDifference == -1) {
            robot.setDirection(Direction.LEFT);
        } else if (rowDifference == 1) {
            robot.setDirection(Direction.DOWN);
        } else if (rowDifference == -1) {
            robot.setDirection(Direction.UP);
        }
    }

    private void drawRoom() {
        roomView.drawRoom(roomPane, room, robot);

        updateRobotStatus();
        updateStatistics();
    }
}