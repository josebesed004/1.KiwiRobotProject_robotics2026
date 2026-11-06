package edu.elon.robotics.autonomous.Lab8;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.function.BooleanSupplier;

import edu.elon.robotics.JoseTools.DrivingInstruction;
import edu.elon.robotics.hardware.PIDController;

@Configurable
@Autonomous(name="Follow_line", group = "Lab8")
public class HuskyLineSniffer extends DrivingInstruction {


    public static double k = 0.5;
    public enum tests {PRINT_FOLLOW_LINE, FOLLOW_THE_LINE}
    public static tests thingRunning = tests.FOLLOW_THE_LINE;

    public static double dT = 10;
    public static double kp = 0.008;
    public static double ki =  0.0004;
    public static double kd = 0.006;

    // Target is the middle of the screen. how much its off left or right is all based on this
    private static final int target = 160;

    public static double speed = Speed.MEDIUM.speed;
    private final PIDController pidController = new PIDController(kp,ki,kd);

    ElapsedTime loopTimer = new ElapsedTime();


    public HuskyLineSniffer(){
        tag = "[LineFollower]";
        print("Driving using : " + thingRunning);
        switch (thingRunning) {
            case PRINT_FOLLOW_LINE:
                print("Hello world");
                ElapsedTime timer = new ElapsedTime();
                addInstruction(() -> driveFollowLinePrint(timer));
                break;
            case FOLLOW_THE_LINE:
                print("Inside the case");
                resetInstructions();
                addInstruction(this::driveFollowLine);
                break;
        }
    }

    private void driveFollowLine(){
        pidController.setTarget(target);
        BooleanSupplier justGo = ()->true;
        
        Runnable manageSpeedAndDirection = () -> {
            loopTimer.reset();   // restart the timer at 0

            double calculation = pidController.update(getXLinePosition()); // PID calculations
            robot.startMove(speed,0,calculation);

            // force the loop to take 20ms total
            // sleep takes (long) integers, so we cast it
            // Range.clip() will return the pause time limited
            // to the range 0 to dT
            sleep((long) Range.clip(dT - loopTimer.milliseconds(), 0, dT));

        };
        print("Runnable and boolean setup. ready to drive! ");
        drive(justGo, manageSpeedAndDirection);
    }

    private void driveFollowLinePrint(ElapsedTime timer){


        pidController.setTarget(target);
        BooleanSupplier justGo = ()->true;
        Runnable manageSpeedAndDirection = () -> {
            loopTimer.reset();   // restart the timer at 0


            HuskyLens.Arrow[] arrows = robot.huskyLens.arrows();
            double calculation = pidController.update(getXLinePosition()); // PID calculations
            if (arrows != null && arrows.length > 0){
                double pos = arrows[0].x_origin + k * (arrows[0].x_target - arrows[0].x_origin);
                print("LF: " + timer.milliseconds() + ", " + (target - pos));
            }


            robot.startMove(speed,0,calculation);
            sleep((long) Range.clip(dT - loopTimer.milliseconds(), 0, dT));

        };
        drive(justGo, manageSpeedAndDirection);
    }

    private double getXLinePosition(){
        HuskyLens.Arrow[] arrows = robot.huskyLens.arrows();
        if (arrows != null && arrows.length > 0) {    // only interested in 1st arrow returned
            int x_origin = arrows[0].x_origin;
            int x_target = arrows[0].x_target;
            return x_origin + k * (x_target - x_origin);
        }
        // if it could not find a line just assume its straight
        return target;
    }

}