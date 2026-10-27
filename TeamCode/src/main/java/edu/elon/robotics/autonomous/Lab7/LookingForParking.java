package edu.elon.robotics.autonomous.Lab7;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.function.BooleanSupplier;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Configurable
@Autonomous(name="Parking", group = "Lab7")
public class LookingForParking extends DrivingInstruction {

    public enum tests {PRINT_DISTANCES, TEST_MOVEMENT, FIND_PARKING}
    public static tests thingRunning = tests.PRINT_DISTANCES;

    public LookingForParking(){
        tag = "[Parking]";
        print("Parking Using: " + thingRunning);
        switch (thingRunning) {
            case FIND_PARKING:
                resetInstructions();
                addInstruction(this::driveToCalibrateLightSensor);
                addInstruction(this::driveUntilLine);
                addInstruction(() -> sleep(18*100));
                addInstruction(() -> turnAngle(-90,0.5));
                // Add function to drive until parking or go back if not.
                break;
            case PRINT_DISTANCES:
                resetInstructions();
                addInstruction(this::driveToCalibrateLightSensor);
                addInstruction(this::driveUntilLine);
                addInstruction(() -> sleep(18*100));
                addInstruction(() -> turnAngle(-90,0.5));
                addInstruction(this::driveUntilLinePrinting);
                break;
            case TEST_MOVEMENT:
                resetInstructions();
                addInstruction(() -> driveDistance(100,0,.3));
                addInstruction(() -> driveDistance(-100,0,.3));
                addInstruction(() -> driveDistance(0,100,.3));
                addInstruction(() -> driveDistance(0,-100,.3));
                addInstruction(() -> turnAngle(90,0.5));
                addInstruction(() -> turnAngle(-90,0.5));
                addInstruction(() -> turnAngle(180,0.5));
                addInstruction(() -> turnAngle(-180,0.5));
                addInstruction(() -> driveHeading(100,45,0.4));
                addInstruction(() -> driveHeading(100,45+180,0.4));
                break;
        }

    }

    protected void driveUntilLine(){
        Runnable moveForward = () -> setPowerEaseStart(0.5,0,0);
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

    protected void driveUntilLinePrinting(){
        print("distance, ticks");
        Runnable moveForward = () -> {
            robot.startMove(0.3, 0, 0);
            print(robot.distanceSensor.getDistance(DistanceUnit.CM)+ ", " + robot.motorLeft.getCurrentPosition());
        };
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

}
