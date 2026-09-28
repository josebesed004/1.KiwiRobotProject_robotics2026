package edu.elon.robotics.JoseTools;
//Jose Random thing lol. Meant to create interpolation for beginning and end of route
// returns percentage of the progress made from beginning to end. If the end is achieved then return 1
public class Interpolation {

    private final int steps;
    private int currentStep = 0;

    public Interpolation(int steps) {
        this(steps, false);
    }

    public Interpolation(int steps, boolean startFromMax) {
        if (steps == 0) {
            throw new IllegalArgumentException("Steps cannot be zero");
        }

        this.steps = Math.abs(steps);
        if (startFromMax) currentStep = this.steps;
    }

    public double next() {
        if (currentStep <  steps) {
            currentStep++;
        }

        return (double) currentStep / steps;
    }

    public double back() {
        if (currentStep >  0) {
            currentStep--;
        }

        return (double) currentStep / steps;
    }


}
