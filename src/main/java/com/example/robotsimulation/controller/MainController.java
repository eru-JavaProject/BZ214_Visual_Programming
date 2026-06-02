package com.example.robotsimulation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Slider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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

    private int robotCol = 10;
    private int robotRow = 8;

    private String selectedTool = "DIRT";

    private final String[][] dirtGrid = new String[14][20];

    private Image dustImage;
    private Image liquidImage;
    private Image stainImage;
    private Image robotImage;
    private Image chargingStationImage;

    private double robotSpeed = 1.0;

    private Timeline robotTimeline;
    private boolean simulationRunning = false;

    private final boolean[][] furnitureGrid = new boolean[14][20];

    private final int columns = 20;
    private final int rows = 14;
    private final double wallThickness = 24;

    @FXML
    public void initialize() {
        robotPositionLabel.setText("(0, 0)");
        robotDirectionLabel.setText("Doğu →");
        batteryPercentLabel.setText("%100");
        batteryProgressBar.setProgress(1.0);

        totalAreaLabel.setText("260 m²");
        cleanedAreaLabel.setText("0 m² (0%)");
        remainingAreaLabel.setText("260 m² (%100)");
        elapsedTimeLabel.setText("00.00");
        collectedDustLabel.setText("%0");

        dustImage = new Image(getClass().getResourceAsStream("/images/dust.png"));
        liquidImage = new Image(getClass().getResourceAsStream("/images/liquid.png"));
        stainImage = new Image(getClass().getResourceAsStream("/images/stain.png"));
        var robotStream = getClass().getResourceAsStream("/images/robot.png");

        if (robotStream == null) {
            System.out.println("robot.png bulunamadı!");
        } else {
            robotImage = new Image(robotStream);
            System.out.println("robot.png yüklendi: " + robotImage.getWidth() + " x " + robotImage.getHeight());
        }
        chargingStationImage = new Image(getClass().getResourceAsStream("/images/charging-station.png"));

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

        addDirtButton.fire();

        drawRoom();

        roomPane.setOnMouseClicked(event -> {
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

        roomPane.widthProperty().addListener((obs, oldVal, newVal) -> drawRoom());
        roomPane.heightProperty().addListener((obs, oldVal, newVal) -> drawRoom());

        robotSpeed = speedSlider.getValue();
        speedValueLabel.setText(String.format("%.1fx", robotSpeed));

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
        {
            robotSpeed = newVal.doubleValue();
            speedValueLabel.setText(String.format("%.1fx", robotSpeed));
        });

    }

    @FXML
    private void onStartClicked() {
        simulationRunning = true;

        if (robotTimeline != null) {
            robotTimeline.stop();
        }

        robotTimeline = new Timeline(
                new KeyFrame(Duration.millis(1000 / robotSpeed), event -> moveRobot())
        );

        robotTimeline.setCycleCount(Timeline.INDEFINITE);
        robotTimeline.play();

        System.out.println("Simulation started");
    }

    @FXML
    private void onPauseClicked() {
        simulationRunning = false;

        if (robotTimeline != null) {
            robotTimeline.pause();
        }

        System.out.println("Simulation paused");
    }

    @FXML
    private void onResetClicked() {
        System.out.println("Simulation reset");
    }

    private void moveRobot() {
        robotCol++;

        if (robotCol >= robotCol) {
            robotCol = 0;
            robotRow++;
        }

        if (robotRow >= robotRow) {
            robotRow = 0;
        }

        drawGrid();

        System.out.println("Robot moved: " + robotCol + "," + robotRow);
    }

    private void drawGrid() {
        roomPane.getChildren().clear();

        int columns = 20;
        int rows = 14;
        double wallThickness = 24;

        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                javafx.scene.shape.Rectangle cell = new javafx.scene.shape.Rectangle();

                cell.setX(wallThickness + col * cellWidth);
                cell.setY(wallThickness + row * cellHeight);
                cell.setWidth(cellWidth);
                cell.setHeight(cellHeight);

                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.rgb(120, 80, 40, 0.35));

                int clickedRow = row;
                int clickedCol = col;

                roomPane.getChildren().add(cell);
            }
        }
    }

    private void drawWalls() {
        double width = roomPane.getWidth();
        double height = roomPane.getHeight();

        double outerThickness = 20;
        double innerThickness = 12;

        javafx.scene.paint.Color outerColor = javafx.scene.paint.Color.web("#1f1f1f");
        javafx.scene.paint.Color wallColor = javafx.scene.paint.Color.web("#3a3a3a");
        javafx.scene.paint.Color highlightColor = javafx.scene.paint.Color.web("#6b6b6b");

        // dış koyu çerçeve
        javafx.scene.shape.Rectangle outerTop = new javafx.scene.shape.Rectangle(0, 0, width, outerThickness);
        javafx.scene.shape.Rectangle outerLeft = new javafx.scene.shape.Rectangle(0, 0, outerThickness, height);
        javafx.scene.shape.Rectangle outerRight = new javafx.scene.shape.Rectangle(width - outerThickness, 0, outerThickness, height);
        javafx.scene.shape.Rectangle outerBottom = new javafx.scene.shape.Rectangle(0, height - outerThickness, width, outerThickness);

        outerTop.setFill(outerColor);
        outerLeft.setFill(outerColor);
        outerRight.setFill(outerColor);
        outerBottom.setFill(outerColor);

        // ana duvar gövdesi
        javafx.scene.shape.Rectangle topWall = new javafx.scene.shape.Rectangle(8, 8, width - 16, innerThickness);
        javafx.scene.shape.Rectangle leftWall = new javafx.scene.shape.Rectangle(8, 8, innerThickness, height - 16);
        javafx.scene.shape.Rectangle rightWall = new javafx.scene.shape.Rectangle(width - 20, 8, innerThickness, height - 16);
        javafx.scene.shape.Rectangle bottomWall = new javafx.scene.shape.Rectangle(8, height - 20, width - 16, innerThickness);

        topWall.setFill(wallColor);
        leftWall.setFill(wallColor);
        rightWall.setFill(wallColor);
        bottomWall.setFill(wallColor);

        // yükseklik efekti için açık iç çizgiler
        javafx.scene.shape.Line topHighlight = new javafx.scene.shape.Line(20, 20, width - 20, 20);
        javafx.scene.shape.Line leftHighlight = new javafx.scene.shape.Line(20, 20, 20, height - 20);

        topHighlight.setStroke(highlightColor);
        leftHighlight.setStroke(highlightColor);
        topHighlight.setStrokeWidth(2);
        leftHighlight.setStrokeWidth(2);

        roomPane.getChildren().addAll(
                outerTop, outerLeft, outerRight, outerBottom,
                topWall, leftWall, rightWall, bottomWall,
                topHighlight, leftHighlight
        );
    }

    private void drawChargingStation() {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        int stationCol = 3;
        int stationRow = 0;

        double x = wallThickness + stationCol * cellWidth;
        double y = wallThickness + stationRow * cellHeight;

        ImageView stationView = new ImageView(chargingStationImage);

        double stationSize = Math.min(cellWidth, cellHeight) * 2.8;

        stationView.setFitWidth(stationSize);
        stationView.setFitHeight(stationSize);
        stationView.setPreserveRatio(true);

        double offsetX = 0;
        double offsetY = 8;

        stationView.setX(x + (cellWidth - stationSize) / 2 + offsetX);
        stationView.setY(y + (cellHeight - stationSize) / 2 + offsetY);

        roomPane.getChildren().add(stationView);
    }

    private void drawRobot() {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        double x = wallThickness + robotCol * cellWidth;
        double y = wallThickness + robotRow * cellHeight;

        if (robotImage == null || robotImage.isError()) {
            System.out.println("Robot görseli çizilemedi, eski robot çiziliyor.");
            drawDefaultRobot(x, y, cellWidth, cellHeight);
            return;
        }

        ImageView robotView = new ImageView(robotImage);
        double robotSize = Math.min(cellWidth, cellHeight) * 2.25;

        robotView.setFitWidth(robotSize);
        robotView.setFitHeight(robotSize);
        robotView.setPreserveRatio(true);

        double offsetX = 0;
        double offsetY = 12;

        robotView.setX(x + (cellWidth - robotSize) / 2 + offsetX);
        robotView.setY(y + (cellHeight - robotSize) / 2 + offsetY);

        roomPane.getChildren().add(robotView);
    }

    private void drawDefaultRobot(double x, double y, double cellWidth, double cellHeight) {
        Circle robotBody = new Circle();
        robotBody.setCenterX(x + cellWidth / 2);
        robotBody.setCenterY(y + cellHeight / 2);
        robotBody.setRadius(Math.min(cellWidth, cellHeight) * 0.35);
        robotBody.setFill(Color.web("#eceff1"));
        robotBody.setStroke(Color.web("#263238"));
        robotBody.setStrokeWidth(3);

        roomPane.getChildren().add(robotBody);
    }

    private void updateRobotStatus() {
        robotPositionLabel.setText("(" + robotCol + ", " + robotRow + ")");
        robotDirectionLabel.setText("Doğu →");
    }

    private void handleCellClick(int row, int col) {
        if (row == robotRow && col == robotCol) {
            return;
        }

        if (row == 12 && col == 0) {
            return;
        }

        if (selectedTool.equals("DIRT")) {
            if (furnitureGrid[row][col]) {
                return;
            }

            dirtGrid[row][col] = getSelectedDirtType();
            System.out.println("Added dirt type: " + dirtGrid[row][col]);
        }

        if (selectedTool.equals("FURNITURE")) {
            dirtGrid[row][col] = null;
            furnitureGrid[row][col] = true;
        }

        drawRoom();
    }

    private String getSelectedDirtType() {
        if (dustButton.isSelected()) {
            return "DUST";
        }

        if (liquidButton.isSelected()) {
            return "LIQUID";
        }

        if (stainButton.isSelected()) {
            return "STAIN";
        }

        return "DUST";
    }

    private void drawDirt() {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                String dirtType = dirtGrid[row][col];

                if (dirtType == null) {
                    continue;
                }

                double x = wallThickness + col * cellWidth;
                double y = wallThickness + row * cellHeight;

                Image selectedImage;

                if (dirtType.equals("DUST")) {
                    selectedImage = dustImage;
                } else if (dirtType.equals("LIQUID")) {
                    selectedImage = liquidImage;
                } else {
                    selectedImage = stainImage;
                }

                ImageView dirtView = new ImageView(selectedImage);

                dirtView.setViewport(new Rectangle2D(
                        390, 180,
                        780, 520
                ));

                double dirtWidth = cellWidth * 0.95;
                double dirtHeight = cellHeight * 0.75;

                dirtView.setFitWidth(dirtWidth);
                dirtView.setFitHeight(dirtHeight);
                dirtView.setPreserveRatio(true);

                dirtView.setX(x + (cellWidth - dirtWidth) / 2);
                dirtView.setY(y + (cellHeight - dirtHeight) / 2);
                roomPane.getChildren().add(dirtView);
            }
        }
    }

    private void drawFurniture() {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (!furnitureGrid[row][col]) {
                    continue;
                }

                double x = wallThickness + col * cellWidth + 4;
                double y = wallThickness + row * cellHeight + 4;

                Rectangle furniture = new Rectangle();
                furniture.setX(x);
                furniture.setY(y);
                furniture.setWidth(cellWidth - 8);
                furniture.setHeight(cellHeight - 8);
                furniture.setArcWidth(10);
                furniture.setArcHeight(10);
                furniture.setFill(Color.web("#8d6e63"));
                furniture.setStroke(Color.web("#4e342e"));
                furniture.setStrokeWidth(2);

                roomPane.getChildren().add(furniture);
            }
        }
    }

    private void drawRoom() {
        roomPane.getChildren().clear();

        drawGrid();
        drawFurniture();
        drawDirt();
        drawWalls();
        drawChargingStation();
        drawRobot();
        updateRobotStatus();
    }
}