package edu.elon.robotics.hardware;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

/*
 * PIDController
 *
 * A generic PID Controller.
 */

public class PIDController {

    private final double kP, kI, kD;
    private double sumError, prevError;
    private double target;

    // data collection
    private double total_error;
    private int call_count;

    /*
     * Constructor is complete. It remembers the constant values.
     */
    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        target = 0.0;
        reset();
    }

    /*
     * reset() is complete.
     */
    public void reset() {
        sumError = 0.0;
        prevError = 0.0;

        // reset metrics
        total_error = 0.0;
        call_count = 0;
    }

    /*
     * setTarget is complete. Allows you to change the target at will.
     */
    public void setTarget(double target) {
        this.target = target;
    }

    /*
     * update() is incomplete.
     */
    public double update(double actual) {
        double error = actual - target;

        // update metrics
        call_count++;
        total_error += error;


        sumError = sumError * 0.8 + error;

        double diffError = error - prevError;

        prevError = error;

        return kP * error + kI * sumError + kD * diffError;
    }

    /*
     * toString() is complete. Uses the PIDController label for output.
     */
    @NonNull
    @SuppressLint("DefaultLocale")
    public String toString() {
        String output = "";
        output += String.format("PIDController: kP=%.5f, kI=%.5f, kD=%.5f)", kP, kI, kD);
        output += String.format("PIDController: target: %.3f", target);
        output += String.format("PIDController: avg error: %.3f", Math.abs(total_error / call_count));
        return output;
    }

}