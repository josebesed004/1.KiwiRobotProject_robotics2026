package edu.elon.robotics.autonomous.Lab7;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.function.BooleanSupplier;

import edu.elon.robotics.JoseTools.DrivingInstruction;

@Configurable
@Autonomous(name="Parking", group = "Lab7")
public class LookingForParking extends DrivingInstruction {

    public static double sizeOfRobot_CM = 35;
    public static double correctDistance_CM = -20;
    public static double closeEnoughDistance = 20;
    public static double timmySpeed = 0.2;
    public boolean canPark = false;
    public double stopTick;
    public double sensorReading;
    public double sizeOfGap = 0.0;
    public enum tests {PRINT_DISTANCES, TEST_MOVEMENT, FIND_PARKING, FIND_PARKING_NOT_TIMMY}
    public static tests thingRunning = tests.PRINT_DISTANCES;

    public LookingForParking(){
        tag = "[Parking]";
        print("Parking Using: " + thingRunning);
        switch (thingRunning) {
            case FIND_PARKING:
                setup();
                addInstruction(() -> driveUntilParking(0.3));
                addInstruction(this::parkIfCan);
                break;
            case FIND_PARKING_NOT_TIMMY:
                setup();
                addInstruction(this::driveUntilCloseEnough);
                addInstruction(() -> driveUntilParking(timmySpeed));
                addInstruction(this::parkIfCan);
                break;
            case PRINT_DISTANCES:
                setup();
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

    // These are the instructions to start setting up for the parking
    private void setup(){
        resetInstructions();
        addInstruction(this::driveToCalibrateLightSensor);
        addInstruction(this::driveUntilLine);
        addInstruction(() -> sleep(1000));
        addInstruction(() -> turnIMU(-90,0.5));
    }
    protected void driveUntilLine(){
        Runnable moveForward = () -> setPowerEaseStart(0.4,0,0);
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

    protected void driveUntilLinePrinting(){
        print("distance, ticks");
        Runnable moveForward = () -> {
            robot.startMove(0.25, 0, 0);
            IMU_correction();
            print(robot.distanceSensor.getDistance(DistanceUnit.CM)+ ", " + robot.motorLeft.getCurrentPosition());
        };
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

    protected void driveUntilParking(double speed){
        double distanceToWall = robot.distanceSensor.getDistance(DistanceUnit.CM);

        Runnable moveAndMeasure = () -> {
            robot.startMove(0.3,0,0);
            sensorReading = robot.distanceSensor.getDistance(DistanceUnit.CM);
            sizeOfGap = 0.0;
            print("Sensor,apropiateDistance");
            if (sensorReading > distanceToWall + sizeOfRobot_CM){
                print(sensorReading + "," + distanceToWall + sizeOfRobot_CM);
                // (ticks per centimeter)^-1 = centimeters per ticks
                sizeOfGap = (robot.motorLeft.getCurrentPosition() - stopTick) * Math.pow(robot.TICKS_PER_CM, -1);
                canPark = sizeOfGap > sizeOfRobot_CM;
//                print("Size of gap = " + sizeOfGap + " can it park? " + canPark);
            }

            //Reset size of gap when the distance is not enough
            else{
                stopTick = robot.motorLeft.getCurrentPosition();
                canPark = false;
            }
        };



        BooleanSupplier stopWhenCanParkOrWhiteLine = () -> !isOnLine() && !canPark;

        drive(stopWhenCanParkOrWhiteLine, moveAndMeasure);
    }

    protected void parkIfCan(){
        if (canPark){
            // Correct for the nose of the robot
            driveDistance(correctDistance_CM, 0,0.3);
            // then park.
            driveUntilCloseEnough();
        }
        // if it cannot park go back
        else {
//            driveIMU(-robot.motorLeft.getCurrentPosition() * robot.LR_TICKS_PER_FWD_CM, 0.3);
        }
    }

    protected void driveUntilCloseEnough(){
        Runnable getClose = () -> robot.startMove(0,0.3,0);
        BooleanSupplier stopWhenCloseEnough = () -> robot.distanceSensor.getDistance(DistanceUnit.CM) > closeEnoughDistance;
        drive(stopWhenCloseEnough, getClose);
    }

}
