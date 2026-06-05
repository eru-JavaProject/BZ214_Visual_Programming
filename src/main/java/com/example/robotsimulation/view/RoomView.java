package com.example.robotsimulation.view;

import com.example.robotsimulation.model.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import com.example.robotsimulation.model.Direction;
import com.example.robotsimulation.model.Furniture;
import java.util.HashMap;
import java.util.Map;

public class RoomView {

    private final double wallThickness = 24;

    private final int rows;
    private final int columns;

    private Image dustImage;
    private Image liquidImage;
    private Image stainImage;
    private Image robotImage;
    private Image chargingStationImage;

    private Map<FurnitureType, Image> furnitureImages = new HashMap<>();

    public RoomView(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        dustImage = new Image(getClass().getResourceAsStream("/images/dust.png"));
        liquidImage = new Image(getClass().getResourceAsStream("/images/liquid.png"));
        stainImage = new Image(getClass().getResourceAsStream("/images/stain.png"));
        robotImage = new Image(getClass().getResourceAsStream("/images/robot.png"));
        chargingStationImage = new Image(getClass().getResourceAsStream("/images/charging-station.png"));

        for (FurnitureType type : FurnitureType.values()) {
            furnitureImages.put(
                    type,
                    new Image(getClass().getResourceAsStream("/" + type.getImagePath()))
            );
        }
    }

    private void drawGrid(Pane roomPane) {

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

                roomPane.getChildren().add(cell);
            }
        }
    }

    private void drawWalls(Pane roomPane) {
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

    private void drawMovementPath(Pane roomPane, Room room) {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Cell cell = room.getCell(row, col);

                if (!cell.isVisitedByRobot() || cell.getVisitDirection() == null) {
                    continue;
                }

                double x = wallThickness + col * cellWidth;
                double y = wallThickness + row * cellHeight;

                Label arrow = new Label(getArrowSymbol(cell.getVisitDirection()));
                arrow.setTextFill(Color.web("#ff9800"));
                arrow.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

                arrow.setLayoutX(x + cellWidth / 2 - 7);
                arrow.setLayoutY(y + cellHeight / 2 - 14);

                roomPane.getChildren().add(arrow);
            }
        }
    }

    private void drawDirt(Pane roomPane, Room room) {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Cell cell = room.getCell(row, col);

                if (!cell.hasDirt()) {
                    continue;
                }

                double x = wallThickness + col * cellWidth;
                double y = wallThickness + row * cellHeight;

                Image selectedImage;

                if (cell.getDirtType() == DirtType.DUST) {
                    selectedImage = dustImage;
                } else if (cell.getDirtType() == DirtType.LIQUID) {
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

    private void drawFurniture(Pane roomPane, Room room) {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Cell cell = room.getCell(row, col);

                if (!cell.hasFurniture()) {
                    continue;
                }

                Furniture furniture = cell.getFurniture();

                if (furniture.getPosition().getRow() != row
                        || furniture.getPosition().getCol() != col) {
                    continue;
                }

                double x = wallThickness + col * cellWidth;
                double y = wallThickness + row * cellHeight;

                Image furnitureImage = furnitureImages.get(furniture.getType());
                ImageView furnitureView = new ImageView(furnitureImage);

                double furnitureWidth = cellWidth * furniture.getType().getWidthInCells();
                double furnitureHeight = cellHeight * furniture.getType().getHeightInCells();

                double scaledWidth = furnitureWidth * furniture.getType().getRenderScale();
                double scaledHeight = furnitureHeight * furniture.getType().getRenderScale();

                furnitureView.setFitWidth(scaledWidth);
                furnitureView.setFitHeight(scaledHeight);
                furnitureView.setPreserveRatio(false);
                furnitureView.setSmooth(true);

                furnitureView.setX(x + (furnitureWidth - scaledWidth) / 2);
                furnitureView.setY(y + (furnitureHeight - scaledHeight) / 2);

                if (furniture.getType() == FurnitureType.SOFA
                        && furniture.getPosition().getRow() > 7) {

                    furnitureView.setRotate(180);
                }

                roomPane.getChildren().add(furnitureView);
            }
        }
    }

    private void drawChargingStation(Pane roomPane) {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        int stationCol = 3;
        int stationRow = 0;

        double x = wallThickness + stationCol * cellWidth;
        double y = wallThickness + stationRow * cellHeight;

        ImageView stationView = new ImageView(chargingStationImage);

        double stationSize = Math.min(cellWidth, cellHeight) * 1.75;

        stationView.setFitWidth(stationSize);
        stationView.setFitHeight(stationSize);
        stationView.setPreserveRatio(true);

        stationView.setX(
                x + (cellWidth - stationSize) / 2
        );

        stationView.setY(
                y + (cellHeight - stationSize) / 2
        );

        roomPane.getChildren().add(stationView);
    }

    private void drawRobot(Pane roomPane, Robot robot) {
        double availableWidth = roomPane.getWidth() - (wallThickness * 2);
        double availableHeight = roomPane.getHeight() - (wallThickness * 2);

        double cellWidth = availableWidth / columns;
        double cellHeight = availableHeight / rows;

        Position position = robot.getPosition();

        double x = wallThickness + position.getCol() * cellWidth;
        double y = wallThickness + position.getRow() * cellHeight;

        if (robotImage == null || robotImage.isError()) {
            System.out.println("Robot görseli çizilemedi, eski robot çiziliyor.");
            drawDefaultRobot(roomPane, x, y, cellWidth, cellHeight);
            return;
        }

        ImageView robotView = new ImageView(robotImage);
        double robotSize = Math.min(cellWidth, cellHeight) * 1.75;

        robotView.setFitWidth(robotSize);
        robotView.setFitHeight(robotSize);
        robotView.setPreserveRatio(true);

        robotView.setX(
                x + (cellWidth - robotSize) / 2
        );

        robotView.setY(
                y + (cellHeight - robotSize) / 2
        );

        roomPane.getChildren().add(robotView);
    }

    private void drawDefaultRobot(Pane roomPane, double x, double y, double cellWidth, double cellHeight) {
        Circle robotBody = new Circle();
        robotBody.setCenterX(x + cellWidth / 2);
        robotBody.setCenterY(y + cellHeight / 2);
        robotBody.setRadius(Math.min(cellWidth, cellHeight) * 0.35);
        robotBody.setFill(Color.web("#eceff1"));
        robotBody.setStroke(Color.web("#263238"));
        robotBody.setStrokeWidth(3);

        roomPane.getChildren().add(robotBody);
    }

    private String getArrowSymbol(Direction direction) {
        switch (direction) {
            case RIGHT:
                return "➜";
            case LEFT:
                return "⬅";
            case UP:
                return "⬆";
            case DOWN:
                return "⬇";
            default:
                return "•";
        }
    }

    public double getWallThickness() {
        return wallThickness;
    }

    public void drawRoom(Pane roomPane, Room room, Robot robot) {
        roomPane.getChildren().clear();

        drawGrid(roomPane);
        drawMovementPath(roomPane, room);
        drawFurniture(roomPane, room);
        drawDirt(roomPane, room);
        drawWalls(roomPane);
        drawChargingStation(roomPane);
        drawRobot(roomPane, robot);
    }
}