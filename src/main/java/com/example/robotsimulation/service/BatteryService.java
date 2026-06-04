package com.example.robotsimulation.service;

import com.example.robotsimulation.model.Robot;

public class BatteryService {

    public void consumeForMovement(Robot robot) {
        robot.setBatteryLevel(robot.getBatteryLevel() - 1);
    }

    public boolean isBatteryLow(Robot robot) {
        return robot.getBatteryLevel() <= 20;
    }

    public void recharge(Robot robot) {
        robot.setBatteryLevel(100);
    }
}