package model;

public enum DirtType {
    DUST(2, 1.0),
    LIQUID(4, 2.5),
    STAIN(6, 4.0);

    private final int cleaningDuration;
    private final double batteryCost;

    DirtType(int cleaningDuration, double batteryCost) {
        this.cleaningDuration = cleaningDuration;
        this.batteryCost = batteryCost;
    }

    public int getCleaningDuration() {
        return cleaningDuration;
    }

    public double getBatteryCost() {
        return batteryCost;
    }

    public double getBatteryCostPerStep() {
        return batteryCost / cleaningDuration;
    }
}
