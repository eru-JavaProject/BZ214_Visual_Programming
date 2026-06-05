package com.example.robotsimulation.service;

import com.example.robotsimulation.model.Position;
import com.example.robotsimulation.model.Room;

import java.util.*;

public class PathFindingService {

    public List<Position> findShortestPath(Room room, Position start, Position target) {
        Queue<Position> queue = new LinkedList<>();
        boolean[][] visited = new boolean[room.getRows()][room.getColumns()];
        Position[][] previous = new Position[room.getRows()][room.getColumns()];

        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        int[] rowDirections = {-1, 1, 0, 0};
        int[] colDirections = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.getRow() == target.getRow() && current.getCol() == target.getCol()) {
                return buildPath(previous, start, target);
            }

            for (int i = 0; i < 4; i++) {
                int newRow = current.getRow() + rowDirections[i];
                int newCol = current.getCol() + colDirections[i];

                boolean isTargetCell = newRow == target.getRow() && newCol == target.getCol();

                if (room.isInsideRoom(newRow, newCol) && (room.isCellWalkable(newRow, newCol) || isTargetCell) && !visited[newRow][newCol]) {
                    Position next = new Position(newRow, newCol);
                    queue.add(next);
                    visited[newRow][newCol] = true;
                    previous[newRow][newCol] = current;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Position> buildPath(Position[][] previous, Position start, Position target) {
        List<Position> path = new ArrayList<>();

        Position current = target;

        while (current != null && !(current.getRow() == start.getRow() && current.getCol() == start.getCol())) {
            path.add(0, current);
            current = previous[current.getRow()][current.getCol()];
        }

        return path;
    }
}