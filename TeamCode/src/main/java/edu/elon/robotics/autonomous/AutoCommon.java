package edu.elon.robotics.autonomous;

/*
 * General autonomous methods.
 */

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Locale;

import edu.elon.robotics.JoseTools.Interpolation;
import edu.elon.robotics.hardware.KiwiBot;

@Configurable
public class AutoCommon extends LinearOpMode {

    public static double percentInterpolate = 0.20;
    public static double minimumSpeed = 0.2;

    protected KiwiBot robot;
    protected final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new KiwiBot(hardwareMap, true);
    }

    protected void driveForTime(double power, long milliseconds) {
        robot.startMove(power, 0, 0);
        sleep(milliseconds);
        robot.stopMove();
    }

    // Default driveForNTicks counts the ticks of the left motor
    protected void driveForNTicks(double drive, double strafe, double rotate, int ticks){
        driveForNTicks(drive,strafe,rotate,ticks,robot.motorLeft);
    }

    protected void driveForNTicks(double drive, double strafe, double rotate, int ticks, DcMotor motor){
        ticks = Math.abs(ticks);
        robot.resetDriveEncoders();
        // Wait the number of ticks
        int buffer = Math.max((int) (ticks * percentInterpolate), 1);

        double powerMultiplier = 1;

        while (opModeIsActive() && Math.abs(motor.getCurrentPosition()) < ticks) {
            int currentTick = motor.getCurrentPosition();

            currentTick = currentTick < 0 ? currentTick *-1 : currentTick;

            powerMultiplier = interpolationMulti(currentTick, buffer, ticks);

            robot.startMove(drive * powerMultiplier, strafe * powerMultiplier, rotate * powerMultiplier);
        }
        robot.stopMove();
    }

    protected void turnAngle(double degrees, double maxPower){
        // Max power is always positive
        maxPower = Math.abs(maxPower);
        // if we are turning a different direction make it the opposite direction
        if (degrees < 0) maxPower = -maxPower;

        driveForNTicks(0,0,maxPower,robot.convertDegreesToTicks(degrees));

        int actualTicks = Math.abs(robot.motorLeft.getCurrentPosition());
        double actualDegrees = robot.convertTicksToDegrees(actualTicks);
        if (degrees < 0) {
            actualDegrees = -actualDegrees;
        }

        double overshootDegrees = actualDegrees - degrees;

        System.out.printf(Locale.US,
                "TURN_LOG: InputDeg=%.1f | ActualTicks=%d | ActualDeg=%.2f | OvershootDeg=%.2f%n",
                degrees, actualTicks, actualDegrees, overshootDegrees);
    }

    private double interpolationMulti(int currentTick, int buffer, int ticks){
        double powerMultiplier = 1.0;
        //Ease In
        if (currentTick <= buffer) {
            powerMultiplier = (double)currentTick/buffer;
        }
        //Ease out
        else if (currentTick > ticks-buffer) {
            powerMultiplier = (double)(ticks-currentTick)/buffer;
        }
        System.out.println(powerMultiplier + "[POWER]");

        return powerMultiplier > 1 ? 1 : Math.max(powerMultiplier, minimumSpeed);
    }

    /*Drive forward/backward for cmForward centimeters, and at the same time,
    strafe left/right for cmSide centimeters.
    A positive cmForward value will drive forward and a positive cmSide value will go to the right.
    Negative values will do the opposite. maxPower will limit the overall speed.*/
    protected void driveDistance(double cmForward,
                                 double cmSide,
                                 double maxPower){

        double distance = Math.sqrt(Math.pow(cmForward,2) + Math.pow(cmSide,2));
        double drivePower = maxPower * cmForward/distance;
        double strafePower = maxPower * cmSide/distance;

        int LR_Tick_Count = Math.abs((int)(robot.LR_TICKS_PER_SIDE_CM*cmSide));
        LR_Tick_Count += Math.abs((int)(robot.LR_TICKS_PER_FWD_CM*cmForward));
        int AUX_tick_count = Math.abs((int) (robot.AUX_TICKS_PER_SIDE_CM * cmSide));

        DcMotor motor;
        int tick_count;
        if (LR_Tick_Count > AUX_tick_count){
            // Choose left motor when driving right and right motor when driving left
            motor = cmSide > 0 ? robot.motorLeft : robot.motorRight;
            tick_count = LR_Tick_Count;
        }
        else{
            motor = robot.motorAux;
            tick_count = AUX_tick_count;
        }

        driveForNTicks(drivePower,strafePower,0,tick_count,motor);

    }

    protected void driveHeading(double cmDistance,
                                double angleHeading,
                                double maxPower){
        //Convert angle to radians
        angleHeading = angleHeading * (2*Math.PI/360);
        double sideDistance = Math.sin(angleHeading)*cmDistance*-1;
        double forwardDistance = Math.cos(angleHeading)*cmDistance;

        driveDistance(forwardDistance,sideDistance,maxPower);
    }
}