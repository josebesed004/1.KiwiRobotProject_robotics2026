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

import edu.elon.robotics.hardware.KiwiBot;

@Configurable
public class AutoCommon extends LinearOpMode {

    protected KiwiBot robot;
    protected final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    public static int RAMP_TICKS = 1000;

    public static double BASE_POWER = 0.10;
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
        robot.resetDriveEncoders();
        robot.startMove(drive, strafe, rotate);
        // Wait the number of ticks
        while (opModeIsActive() && Math.abs(motor.getCurrentPosition()) < ticks) {
            System.out.println(motor.getCurrentPosition());
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

    protected void rampUP(int maxTicks, double maxPower, DcMotor motor){
        motor.setPower(BASE_POWER);
        int MOTOR_START_POS = motor.getCurrentPosition();
        double ACCELERATION = ((maxPower - BASE_POWER)/RAMP_TICKS);

        if (maxTicks < 2 * RAMP_TICKS){
            ACCELERATION = ((maxPower - BASE_POWER)/(maxTicks/2));
        }

        while (motor.getCurrentPosition() - MOTOR_START_POS < RAMP_TICKS && motor.getCurrentPosition() - MOTOR_START_POS < maxTicks/2 && motor.getPower() < maxPower){
            motor.setPower(motor.getPower() + ACCELERATION);
        }
    }

    protected void rampDown(int maxTicks, double maxPower, DcMotor motor){
        motor.setPower(maxPower);
        int MOTOR_START_POS = motor.getCurrentPosition();
        double ACCELERATION = ((maxPower - BASE_POWER)/RAMP_TICKS);

        if (maxTicks < 2 * RAMP_TICKS){
            ACCELERATION = ((maxPower - BASE_POWER)/(maxTicks/2));
        }

        while (motor.getCurrentPosition() - MOTOR_START_POS < RAMP_TICKS && motor.getCurrentPosition() - MOTOR_START_POS < maxTicks/2 && motor.getPower() < maxPower){
            motor.setPower(motor.getPower() - ACCELERATION);
        }
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

}