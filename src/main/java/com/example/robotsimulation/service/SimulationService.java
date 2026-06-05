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

        if (robot.getBatteryLevel() <= 0) {
            return;
        }

        if (visitCounts == null) {
            visitCounts = new int[room.getRows()][room.getColumns()];
        }

        startCleaningIfNeeded(robot, room);

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
            Direction alternativeDirection = findObstacleBypassDirection(robot, room, robot.getDirection());

            if (alternativeDirection == null) {
                alternativeDirection = findBestDirection(robot, room);
            }

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

    private Direction findObstacleBypassDirection(Robot robot, Room room, Direction blockedDirection) {
        Position rejoinPosition = findRejoinPositionAfterObstacle(robot, room, blockedDirection);

        if (rejoinPosition == null) {
            return null;
        }

        List<Position> bypassPath = pathFindingService.findShortestPath(
                room,
                robot.getPosition(),
                rejoinPosition
        );

        if (bypassPath == null || bypassPath.isEmpty()) {
            return null;
        }

        Position nextStep = bypassPath.get(0);

        if (!room.isCellWalkable(nextStep.getRow(), nextStep.getCol())) {
            return null;
        }

        return getDirectionFromTo(robot.getPosition(), nextStep);
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

    public boolean shouldReturnToStation(Robot robot, Room room) {
        if (!batteryService.isBatteryLow(robot)) {
            return false;
        }

        if (robot.getCleaningTimeRemaining() > 0) {
            return false;
        }

        Position currentPosition = robot.getPosition();

        if (room.getCell(currentPosition.getRow(), currentPosition.getCol()).hasDirt()) {
            return false;
        }

        Position nextPosition = getNextPosition(robot);
        int nextRow = nextPosition.getRow();
        int nextCol = nextPosition.getCol();

        if (room.isInsideRoom(nextRow, nextCol)
                && room.isCellWalkable(nextRow, nextCol)
                && room.getCell(nextRow, nextCol).hasDirt()) {
            return false;
        }

        return true;
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

        Direction oppositeDirection = getOppositeDirection(robot.getDirection());

        Direction bestDirection = null;
        int bestScore = Integer.MAX_VALUE;

        for (Direction direction : Direction.values()) {

            if (direction == oppositeDirection) {
                continue;
            }

            Position nextPosition = getNextPosition(robot, direction);

            int row = nextPosition.getRow();
            int col = nextPosition.getCol();

            if (!room.isCellWalkable(row, col)) {
                continue;
            }

            int score = visitCounts[row][col] * 3;
            score += random.nextInt(5);

            if (score < bestScore) {
                bestScore = score;
                bestDirection = direction;
            }
        }

        if (bestDirection != null) {
            return bestDirection;
        }

        Position backPosition = getNextPosition(robot, oppositeDirection);

        if (room.isCellWalkable(backPosition.getRow(), backPosition.getCol())) {
            return oppositeDirection;
        }

        return findBestDirection(robot, room);
    }

    private Direction findWallFollowDirection(Robot robot, Room room) {
        Position position = robot.getPosition();

        int row = position.getRow();
        int col = position.getCol();

        Direction bypassDirection = getNextWallBypassDirection(robot, room);
        if (bypassDirection != null) {
            return bypassDirection;
        }

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

    private Direction getNextWallBypassDirection(Robot robot, Room room) {
        if (wallBypassPath == null || wallBypassPath.isEmpty()) {
            return null;
        }

        Position nextStep = wallBypassPath.remove(0);

        if (isAdjacent(robot.getPosition(), nextStep)
                && room.isCellWalkable(nextStep.getRow(), nextStep.getCol())) {
            return getDirectionFromTo(robot.getPosition(), nextStep);
        }

        wallBypassPath = null;
        return null;
    }

    private Direction getWallFollowDirectionWithObstacleBypass(Robot robot, Room room, Direction wallDirection) {

        if (wallBypassPath != null && !wallBypassPath.isEmpty()) {
            Position nextStep = wallBypassPath.remove(0);

            if (room.isCellWalkable(nextStep.getRow(), nextStep.getCol())) {
                return getDirectionFromTo(robot.getPosition(), nextStep);
            }

            wallBypassPath = null;
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
                return getNextWallBypassDirection(robot, room);
            }
        }

        return findBestDirection(robot, room);
    }

    private Position findRejoinPositionAfterObstacle(Robot robot, Room room, Direction wallDirection) {
        Position position = robot.getPosition();

        int row = position.getRow();
        int col = position.getCol();

        if (wallDirection == Direction.RIGHT) {
            for (int c = col + 2; c < room.getColumns(); c++) {
                Position candidate = new Position(row, c);

                if (room.isCellWalkable(row, c)
                        && hasValidPathTo(room, robot.getPosition(), candidate)) {
                    return candidate;
                }
            }
        }

        if (wallDirection == Direction.LEFT) {
            for (int c = col - 2; c >= 0; c--) {
                Position candidate = new Position(row, c);

                if (room.isCellWalkable(row, c)
                        && hasValidPathTo(room, robot.getPosition(), candidate)) {
                    return candidate;
                }
            }
        }

        if (wallDirection == Direction.DOWN) {
            for (int r = row + 2; r < room.getRows(); r++) {
                Position candidate = new Position(r, col);

                if (room.isCellWalkable(r, col)
                        && hasValidPathTo(room, robot.getPosition(), candidate)) {
                    return candidate;
                }
            }
        }

        if (wallDirection == Direction.UP) {
            for (int r = row - 2; r >= 0; r--) {
                Position candidate = new Position(r, col);

                if (room.isCellWalkable(r, col)
                        && hasValidPathTo(room, robot.getPosition(), candidate)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    private boolean hasValidPathTo(Room room, Position start, Position target) {
        List<Position> path = pathFindingService.findShortestPath(room, start, target);

        return path != null && path.size() > 1;
    }

    private boolean isAdjacent(Position current, Position next) {
        int rowDifference = Math.abs(current.getRow() - next.getRow());
        int colDifference = Math.abs(current.getCol() - next.getCol());

        return rowDifference + colDifference == 1;
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

    public boolean cleanIfCurrentCellDirty(Robot robot, Room room) {
        Position position = robot.getPosition();

        if (room.getCell(position.getRow(), position.getCol()).hasDirt()) {
            if (robot.getCleaningTimeRemaining() == 0) {
                startCleaningIfNeeded(robot, room);
            }

            if (robot.getCleaningTimeRemaining() > 0) {
                robot.setCleaningTimeRemaining(robot.getCleaningTimeRemaining() - 1);

                if (robot.getCleaningTimeRemaining() == 0) {
                    cleaningService.cleanCurrentCell(robot, room);
                }

                return true;
            }
        }

        return false;
    }

    private void startCleaningIfNeeded(Robot robot, Room room) {
        if (robot.getCleaningTimeRemaining() > 0) {
            return;
        }

        Position position = robot.getPosition();

        if (room.getCell(position.getRow(), position.getCol()).hasDirt()) {
            int duration = cleaningService.getCleaningDuration(
                    room.getCell(position.getRow(), position.getCol()).getDirtType()
            );

            robot.setCleaningTimeRemaining(duration);
        }
    }
}