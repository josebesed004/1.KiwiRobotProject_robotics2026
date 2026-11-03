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
    public static double timmySpeed = Speed.SLOWEST.speed;
    public boolean canPark = false;
    public double stopTick;
    public double sensorReading;
    public double sizeOfGap = 0.0;
    public enum tests {PRINT_DISTANCES, TEST_MOVEMENT, FIND_PARKING, FIND_PARKING_NOT_HIT_TIMMY, FIND_SMALLEST_PARKING}
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
            case FIND_PARKING_NOT_HIT_TIMMY:
                setup();
                addInstruction(this::driveUntilCloseEnough);
                addInstruction(() -> driveUntilParking(timmySpeed));
                addInstruction(this::parkIfCan);
                break;
            case PRINT_DISTANCES:
                setup();
                addInstruction(this::driveUntilLinePrinting);
                break;
            case FIND_SMALLEST_PARKING:
                setup();
                //TODO: List parkins then pick the smaller one.
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
        addInstruction(() -> turnIMU(-90,Speed.MEDIUM.speed));
    }
    protected void driveUntilLine(){
        driveUntilLine(Speed.SLOW.speed);
    }

    protected void driveUntilLine(double speed){
        Runnable moveForward = () -> setPowerEaseStart(speed,0,0);
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

    protected void driveUntilLinePrinting(){
        print("distance, ticks");
        ////////////////////////
        Runnable moveForward = () -> {
            robot.startMove(Speed.SLOW.speed, 0, 0);
            IMU_correction();
            print(robot.distanceSensor.getDistance(DistanceUnit.CM)+ ", " + robot.motorLeft.getCurrentPosition());
        };
        ////////////////////////
        BooleanSupplier stopAtLine = () -> !isOnLine();
        drive(stopAtLine, moveForward);
        print("line_detected");
    }

    protected void listParkins(){

    }

    protected void driveUntilParking(double speed){
        double distanceToWall = robot.distanceSensor.getDistance(DistanceUnit.CM);

        Runnable moveAndMeasure = () -> {
            robot.startMove(0.3,0,0);
            sensorReading = robot.distanceSensor.getDistance(DistanceUnit.CM);
            sizeOfGap = 0.0;
            print("Sensor,apropiateDistance");
            detectParking(distanceToWall);
        };



        BooleanSupplier stopWhenCanParkOrWhiteLine = () -> !isOnLine() && !canPark;

        drive(stopWhenCanParkOrWhiteLine, moveAndMeasure);
    }

    protected void detectParking(double distanceToWall){
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
    }
    protected void parkIfCan(){
        if (canPark){
            // Correct for the nose of the robot
            driveDistance(correctDistance_CM, 0,Speed.SLOW.speed);
            // then park.
            driveUntilCloseEnough();
        }
        // if it cannot park go back using the ticks from the right motor
        else {
            driveForNTicks(-Speed.MEDIUM.speed, 0, 0, Math.abs(robot.motorRight.getCurrentPosition()));
            turnIMU(90,Speed.FAST.speed);
            driveDistance(-15,0,Speed.MEDIUM.speed);
            driveUntilLine(-Speed.SLOW.speed);
            driveDistance(-5,0, Speed.SLOW.speed);
        }
    }

    protected void driveUntilCloseEnough(){
        Runnable getClose = () -> robot.startMove(0,Speed.SLOW.speed,0);
        BooleanSupplier stopWhenCloseEnough = () -> robot.distanceSensor.getDistance(DistanceUnit.CM) > closeEnoughDistance;
        drive(stopWhenCloseEnough, getClose);
    }

}