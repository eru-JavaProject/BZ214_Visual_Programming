package com.example.robotsimulation.service;

import com.example.robotsimulation.model.Cell;
import com.example.robotsimulation.model.CellType;
import com.example.robotsimulation.model.DirtType;
import com.example.robotsimulation.model.Position;
import com.example.robotsimulation.model.Robot;
import com.example.robotsimulation.model.Room;

public class CleaningService {

    public void cleanCurrentCell(Robot robot, Room room) {
        Position position = robot.getPosition();
        Cell cell = room.getCell(position.getRow(), position.getCol());

        if (!cell.hasDirt()) {
            return;
        }

        DirtType dirtType = cell.getDirtType();

        int extraBatteryCost = getBatteryCostByDirtType(dirtType);
        robot.setBatteryLevel(robot.getBatteryLevel() - extraBatteryCost);

        cell.setDirtType(null);
        cell.setCellType(CellType.EMPTY);
    }

    private int getBatteryCostByDirtType(DirtType dirtType) {
        if (dirtType == DirtType.DUST) {
            return 2;
        }

        if (dirtType == DirtType.LIQUID) {
            return 3;
        }

        if (dirtType == DirtType.STAIN) {
            return 3;
        }

        return 1;
    }

    public int getCleaningDuration(DirtType dirtType) {
        if (dirtType == DirtType.DUST) {
            return 2;
        }

        if (dirtType == DirtType.LIQUID) {
            return 3;
        }

        if (dirtType == DirtType.STAIN) {
            return 4;
        }

        return 1;
    }
}