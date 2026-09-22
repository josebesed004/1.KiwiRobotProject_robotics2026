package edu.elon.robotics.autonomous;

/*
 * General autonomous methods.
 */

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Locale;

import edu.elon.robotics.hardware.KiwiBot;


public class AutoCommon extends LinearOpMode {

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

    protected void driveForNTicks(double drive, double strafe, double rotate, int ticks){
        robot.resetDriveEncoders();
        robot.startMove(drive, strafe, rotate);
        // Wait the number of ticks
        while (opModeIsActive() && Math.abs(robot.motorLeft.getCurrentPosition()) < ticks) {
            System.out.println(robot.motorLeft.getCurrentPosition());
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

}