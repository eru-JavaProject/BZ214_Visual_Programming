package com.example.robotsimulation.service;

import com.example.robotsimulation.model.Position;
import com.example.robotsimulation.model.Robot;
import com.example.robotsimulation.model.Room;
import java.util.List;
import com.example.robotsimulation.model.Direction;
import com.example.robotsimulation.model.CleaningAlgorithm;
import java.util.Random;

public class SimulationService {

    private final BatteryService batteryService = new BatteryService();
    private final CleaningService cleaningService = new CleaningService();
    private final PathFindingService pathFindingService = new PathFindingService();
    private final Random random = new Random();
    private int[][] visitCounts;
    private boolean wallReached = false;

    private List<Position> wallBypassPath = null;

    public void moveRobot(Robot robot, Room room, CleaningAlgorithm algorithm) {
        if (visitCounts == null) {
            visitCounts = new int[room.getRows()][room.getColumns()];
        }

        if (robot.getCleaningTimeRemaining() > 0) {
            robot.setCleaningTimeRemaining(robot.getCleaningTimeRemaining() - 1);

            if (robot.getCleaningTimeRemaining() == 0) {
                cleaningService.cleanCurrentCell(robot, room);
            }

            return;
        }
        Direction selectedDirection = chooseDirection(robot, room, algorithm);

        if (selectedDirection != null) {
            robot.setDirection(selectedDirection);
        }

        Position nextPosition = getNextPosition(robot);

        int newRow = nextPosition.getRow();
        int newCol = nextPosition.getCol();

        if (room.isCellWalkable(newRow, newCol)) {

            robot.setPosition(new Position(newRow, newCol));
            room.getCell(newRow, newCol).setVisitedByRobot(true);
            room.getCell(newRow, newCol).setVisitDirection(robot.getDirection());
            visitCounts[newRow][newCol]++;
            batteryService.consumeForMovement(robot);

            startCleaningIfNeeded(robot, room);

        } else {
            Direction alternativeDirection = findBestDirection(robot, room);

            if (alternativeDirection != null) {
                robot.setDirection(alternativeDirection);

                Position alternativePosition = getNextPosition(robot);
                int alternativeRow = alternativePosition.getRow();
                int alternativeCol = alternativePosition.getCol();

                if (room.isCellWalkable(alternativeRow, alternativeCol)) {
                    robot.setPosition(alternativePosition);
                    room.getCell(alternativeRow, alternativeCol).setVisitedByRobot(true);
                    room.getCell(alternativeRow, alternativeCol).setVisitDirection(robot.getDirection());
                    visitCounts[alternativeRow][alternativeCol]++;
                    batteryService.consumeForMovement(robot);

                    startCleaningIfNeeded(robot, room);
                }
            }
        }
    }

    private Direction findBestDirection(Robot robot, Room room) {
        Direction bestDirection = null;
        int lowestVisitCount = Integer.MAX_VALUE;

        for (Direction direction : Direction.values()) {
            Position nextPosition = getNextPosition(robot, direction);

            int row = nextPosition.getRow();
            int col = nextPosition.getCol();

            if (room.isCellWalkable(row, col)) {
                int visitCount = visitCounts[row][col];

                if (visitCount < lowestVisitCount) {
                    lowestVisitCount = visitCount;
                    bestDirection = direction;
                }
            }
        }

        return bestDirection;
    }
    private Position getNextPosition(Robot robot) {
        return getNextPosition(robot, robot.getDirection());
    }

    private Position getNextPosition(Robot robot, Direction direction) {
        Position position = robot.getPosition();

        int newRow = position.getRow();
        int newCol = position.getCol();

        switch (direction) {
            case RIGHT:
                newCol++;
                break;
            case LEFT:
                newCol--;
                break;
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
        }

        return new Position(newRow, newCol);
    }

    public List<Position> getPathToChargingStation(Robot robot, Room room) {
        return pathFindingService.findShortestPath(
                room,
                robot.getPosition(),
                room.getChargingStationPosition()
        );
    }

    public boolean shouldReturnToStation(Robot robot) {
        return batteryService.isBatteryLow(robot);
    }

    private Direction chooseDirection(Robot robot, Room room, CleaningAlgorithm algorithm) {
        if (algorithm == CleaningAlgorithm.RANDOM) {
            return findRandomDirection(robot, room);
        }

        if (algorithm == CleaningAlgorithm.WALL_FOLLOW) {
            return findWallFollowDirection(robot, room);
        }

        return findBestDirection(robot, room);
    }

    private Direction findRandomDirection(Robot robot, Room room) {
        Direction[] directions = Direction.values();

        for (int i = 0; i < directions.length * 2; i++) {
            Direction direction = directions[random.nextInt(directions.length)];
            Position nextPosition = getNextPosition(robot, direction);

            if (room.isCellWalkable(nextPosition.getRow(), nextPosition.getCol())) {
                return direction;
            }
        }

        return findBestDirection(robot, room);
    }

    private Direction findWallFollowDirection(Robot robot, Room room) {
        Position position = robot.getPosition();

        int row = position.getRow();
        int col = position.getCol();

        if (!wallReached) {
            Direction directionToNearestWall = getDirectionToNearestWall(position, room);
            Position nextPosition = getNextPosition(robot, directionToNearestWall);

            if (room.isCellWalkable(nextPosition.getRow(), nextPosition.getCol())) {
                return directionToNearestWall;
            }

            wallReached = true;
        }

        boolean atTopWall = row == 0;
        boolean atBottomWall = row == room.getRows() - 1;
        boolean atLeftWall = col == 0;
        boolean atRightWall = col == room.getColumns() - 1;

        if (atTopWall && !atRightWall) {
            return getWallFollowDirectionWithObstacleBypass(robot, room, Direction.RIGHT);
        }

        if (atRightWall && !atBottomWall) {
            return getWallFollowDirectionWithObstacleBypass(robot, room, Direction.DOWN);
        }

        if (atBottomWall && !atLeftWall) {
            return getWallFollowDirectionWithObstacleBypass(robot, room, Direction.LEFT);
        }

        if (atLeftWall && !atTopWall) {
            return getWallFollowDirectionWithObstacleBypass(robot, room, Direction.UP);
        }

        return getDirectionToNearestWall(position, room);
    }

    private Direction getWallFollowDirectionWithObstacleBypass(Robot robot, Room room, Direction wallDirection) {

        if (wallBypassPath != null && !wallBypassPath.isEmpty()) {
            Position nextStep = wallBypassPath.remove(0);
            return getDirectionFromTo(robot.getPosition(), nextStep);
        }

        Position nextPosition = getNextPosition(robot, wallDirection);

        if (room.isCellWalkable(nextPosition.getRow(), nextPosition.getCol())) {
            return wallDirection;
        }

        Position rejoinPosition = findRejoinPositionAfterObstacle(robot, room, wallDirection);

        if (rejoinPosition != null) {
            wallBypassPath = pathFindingService.findShortestPath(
                    room,
                    robot.getPosition(),
                    rejoinPosition
            );

            if (wallBypassPath != null && !wallBypassPath.isEmpty()) {
                wallBypassPath.remove(0);

                if (!wallBypassPath.isEmpty()) {
                    Position nextStep = wallBypassPath.remove(0);
                    return getDirectionFromTo(robot.getPosition(), nextStep);
                }
            }
        }

        return findBestDirection(robot, room);
    }

    private Position findRejoinPositionAfterObstacle(Robot robot, Room room, Direction wallDirection) {
        Position position = robot.getPosition();

        int row = position.getRow();
        int col = position.getCol();

        if (wallDirection == Direction.RIGHT) {
            for (int c = col + 1; c < room.getColumns(); c++) {
                if (room.isCellWalkable(row, c)) {
                    return new Position(row, c);
                }
            }
        }

        if (wallDirection == Direction.LEFT) {
            for (int c = col - 1; c >= 0; c--) {
                if (room.isCellWalkable(row, c)) {
                    return new Position(row, c);
                }
            }
        }

        if (wallDirection == Direction.DOWN) {
            for (int r = row + 1; r < room.getRows(); r++) {
                if (room.isCellWalkable(r, col)) {
                    return new Position(r, col);
                }
            }
        }

        if (wallDirection == Direction.UP) {
            for (int r = row - 1; r >= 0; r--) {
                if (room.isCellWalkable(r, col)) {
                    return new Position(r, col);
                }
            }
        }

        return null;
    }

    private Direction getDirectionFromTo(Position current, Position next) {
        if (next.getRow() < current.getRow()) {
            return Direction.UP;
        }

        if (next.getRow() > current.getRow()) {
            return Direction.DOWN;
        }

        if (next.getCol() < current.getCol()) {
            return Direction.LEFT;
        }

        if (next.getCol() > current.getCol()) {
            return Direction.RIGHT;
        }

        return Direction.RIGHT;
    }

    private Direction getDirectionToNearestWall(Position position, Room room) {
        int distanceToTop = position.getRow();
        int distanceToBottom = room.getRows() - 1 - position.getRow();
        int distanceToLeft = position.getCol();
        int distanceToRight = room.getColumns() - 1 - position.getCol();

        int minDistance = distanceToTop;
        Direction nearestDirection = Direction.UP;

        if (distanceToBottom < minDistance) {
            minDistance = distanceToBottom;
            nearestDirection = Direction.DOWN;
        }

        if (distanceToLeft < minDistance) {
            minDistance = distanceToLeft;
            nearestDirection = Direction.LEFT;
        }

        if (distanceToRight < minDistance) {
            nearestDirection = Direction.RIGHT;
        }

        return nearestDirection;
    }

    private Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                return Direction.RIGHT;
        }
    }

    public void resetWallFollowState() {
        wallReached = false;
        wallBypassPath = null;
    }

    private void startCleaningIfNeeded(Robot robot, Room room) {
        Position position = robot.getPosition();

        if (room.getCell(position.getRow(), position.getCol()).hasDirt()) {
            int duration = cleaningService.getCleaningDuration(
                    room.getCell(position.getRow(), position.getCol()).getDirtType()
            );

            robot.setCleaningTimeRemaining(duration);
        }
    }
}