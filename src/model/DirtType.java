package model;

public enum DirtType {
    DUST(1, 1.0),
    LIQUID(2, 2.0),
    STAIN(3, 3.5);

    private final int cleaningSteps;
    private final double batteryCostPerStep;

    DirtType(int cleaningSteps, double batteryCostPerStep) {
        this.cleaningSteps = cleaningSteps;
        this.batteryCostPerStep = batteryCostPerStep;
    }

    public int getCleaningSteps() {
        return cleaningSteps;
    }

    public double getBatteryCostPerStep() {
        return batteryCostPerStep;
    }
}

