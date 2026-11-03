package edu.elon.robotics.autonomous.Lab7;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayList;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Configurable
@Autonomous(name="Telemetry for distance sensor", group = "Lab7")
public class TestingDistanceSensor extends DrivingInstruction {
    public static double strafePower = -Speed.SLOW.speed;
    public static int functionTest = 1;
    public TestingDistanceSensor(){
        switch (functionTest) {
            case 1:
                resetInstructions();
                addInstruction(this::reportTelemetry);
                break;
            case 2:
                resetInstructions();
                addInstruction(this::getMeanAndStandardDeviation);
                break;
            case 3:
                resetInstructions();
                addInstruction(() -> driveUntilLimit(strafePower));
                break;
        }
    }

    public void getMeanAndStandardDeviation(){
        ArrayList<Double> measurements = new ArrayList<>();
        double currentReading;
        int amount_of_samples = 1000;
        int i = 0;
        while (opModeIsActive() && amount_of_samples > i++) {
            currentReading = robot.distanceSensor.getDistance(DistanceUnit.CM);
            telemetry.addData("distance", currentReading);
            telemetry.addData("numSamples", i);
            telemetry.update();
            measurements.add(currentReading);
        }

        double sum = measurements
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double mean = sum / measurements.size();

        double squareDifference = measurements.stream()
                .mapToDouble(measure -> Math.pow(measure - mean, 2))
                .sum();

        double standardDeviation = Math.sqrt(squareDifference/measurements.size());

        double max = measurements.get(0);
        double min = max;
        for (int j = 0 ; j < measurements.size(); j++){
            double current =  measurements.get(j);
            max = Math.max(current, max);
            min = Math.min(current, min);
        }

        System.out.printf("[DistanceSensorData] mean: %.3f Standard Deviation: %.3f, max: %.3f, min: %.3f\n"
                ,mean, standardDeviation, max, min);
    }

    public void reportTelemetry(){
        double currentReading;
        while (opModeIsActive()) {
            currentReading = robot.distanceSensor.getDistance(DistanceUnit.CM);
            telemetry.addData("distance", currentReading);
            telemetry.update();
        }
    }

    protected void driveUntilLimit(double power){
        double maxReading = maxReading = robot.distanceSensor.getDistance(DistanceUnit.CM);;
        robot.startMove(0,power,0);
        while (!robot.touchSensor.isPressed() && maxReading < 200){
            maxReading = robot.distanceSensor.getDistance(DistanceUnit.CM);
        }
        robot.stopMove();

        System.out.printf("[DistanceSensorData] max distance: %.2f\n"
                ,maxReading);
    }

}
