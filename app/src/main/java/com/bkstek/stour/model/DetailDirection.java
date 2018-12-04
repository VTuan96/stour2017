package com.bkstek.stour.model;

public class DetailDirection {
    private String instruction;
    private String distance;

    public DetailDirection(String instruction, String distance) {
        this.instruction = instruction;
        this.distance = distance;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
