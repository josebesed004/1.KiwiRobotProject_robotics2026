package edu.elon.robotics.autonomous;

/*
 * General autonomous methods.
 */

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


import java.util.Locale;
import java.util.function.BooleanSupplier;

import edu.elon.robotics.hardware.KiwiBot;

@Configurable
public class AutoCommon extends LinearOpMode {

    public static double percentInterpolate = 0.10;
    public static double MINIMUM_SPEED = 0.21;
    public static double EASE_IN_TICKS = 500;
    public static double brightnessThreshold = 0.5;

    protected KiwiBot robot;
    protected final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    public static int RAMP_TICKS = 1000;

    public static double BASE_POWER = 0.10;

    public static double ANGLE_OVERSHOOT_IMU_ROTATE = 2.5;
    public static double ANGLE_OVERSHOOT_IMU_DRIVE = 1.1;
    public static double DRIVE_CORRECTION = 0.06;

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
        // Wait the number of ticks
        int buffer = Math.max((int) (ticks * percentInterpolate), 1);
        int finalTicks = ticks;
        drive(() -> isCurrentTicksLessThanMotor(motor, finalTicks)
            , () -> setPowerInterpolated(motor, buffer, drive, strafe, rotate, finalTicks));

    }

    protected void setPowerInterpolated(DcMotor motor, int buffer, double drive, double strafe, double rotate, int ticks){

        int currentTick = motor.getCurrentPosition();

        currentTick = currentTick < 0 ? currentTick *-1 : currentTick;

        double powerMultiplier = interpolationMulti(currentTick, buffer, ticks);

        robot.startMove(drive * powerMultiplier, strafe * powerMultiplier, rotate * powerMultiplier);
    }

    protected void setPowerEaseStart (DcMotor motor, double drive, double strafe, double rotate){

        int currentPosition = Math.abs(motor.getCurrentPosition());
        double easeMulti = currentPosition < EASE_IN_TICKS
                ? Math.max((double) currentPosition /EASE_IN_TICKS,MINIMUM_SPEED)
                : 1;

        robot.startMove(drive * easeMulti, strafe* easeMulti, rotate* easeMulti);
    }

    protected void setPowerEaseStart (double drive, double strafe, double rotate){
        setPowerEaseStart(robot.motorLeft, drive, strafe, rotate);
    }

    protected boolean isCurrentTicksLessThanMotor(DcMotor motor, int ticks){
        return Math.abs(motor.getCurrentPosition()) < ticks;
    }

    protected boolean isOnLine(){
        int deltaBrightness = robot.maxBrightness - robot.minBrightness;
        int cutPoint = (int)(robot.minBrightness + deltaBrightness * brightnessThreshold);
        return robot.colorSensor.alpha() > cutPoint;
    }

    public void drive(BooleanSupplier condition, Runnable insideTheLoop){
        robot.resetDriveEncoders();
        while (condition.getAsBoolean() && opModeIsActive()){
            insideTheLoop.run();
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

        return powerMultiplier > 1 ? 1 : Math.max(powerMultiplier, MINIMUM_SPEED);
    }



    /*Drive forward/backward for cmForward centimeters, and at the same time,
    strafe left/right for cmSide centimeters.
    A positive cmForward value will drive forward and a positive cmSide value will go to the right.
    Negative values will do the opposite. maxPower will limit the overall speed.*/
    protected void driveDistance(double cmForward,
                                 double cmSide,
                                 double maxPower) {

        double distance = Math.hypot(cmForward, cmSide);

        if (distance == 0) {
            return;
        }

        double drivePower = maxPower * cmForward / distance;
        double strafePower = maxPower * cmSide / distance;

        double leftTicks =
                cmForward * robot.LR_TICKS_PER_FWD_CM +
                        cmSide * robot.LR_TICKS_PER_SIDE_CM;

        double rightTicks =
                cmForward * robot.LR_TICKS_PER_FWD_CM -
                        cmSide * robot.LR_TICKS_PER_SIDE_CM;

        double auxTicks =
                cmSide * robot.AUX_TICKS_PER_SIDE_CM;

        double absLeft = Math.abs(leftTicks);
        double absRight = Math.abs(rightTicks);
        double absAux = Math.abs(auxTicks);

        DcMotor motor;
        int tickCount;

        if (absLeft >= absRight && absLeft >= absAux) {
            motor = robot.motorLeft;
            tickCount = (int) absLeft;
        }
        else if (absRight >= absLeft && absRight >= absAux) {
            motor = robot.motorRight;
            tickCount = (int) absRight;
        }
        else {
            motor = robot.motorAux;
            tickCount = (int) absAux;
        }

        driveForNTicks(
                drivePower,
                strafePower,
                0,
                tickCount,
                motor
        );
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

    /**
     * Turns the robot in place by a specified angle in degrees using IMU feedback.
     * Positive degrees turn counter-clockwise; negative degrees turn clockwise.
     *
     * @param degrees The target rotation angle (e.g. +90, -180, +200, +720).
     * @param power   The speed at which to turn (magnitude, always positive).
     */
    protected void turnIMU(double degrees, double power){
        robot.imu.resetYaw();
        power = Math.abs(power);

        // Reduce requested angle to range of -360 to 360
        double target_angle = degrees % 360.0;
        if (target_angle == 0) return;

        // Determine turning direction (positive = counterclockwise, negative = clockwise)
        double turnPower = target_angle < 0 ? -power : power;
        double prev_heading = robot.getHeading(); // 0.0 right after resetYaw()
        double angle_turned = 0.0;

        // Begin turning in place
        robot.startMove(0, 0, turnPower);

        // Turn loop: continues until accumulated turn reaches target minus angle overshoot
        while(opModeIsActive() && angle_turned < Math.abs(target_angle) - ANGLE_OVERSHOOT_IMU_ROTATE) {
            double current_heading = robot.getHeading();
            double angle_delta = current_heading - prev_heading;

            // Handle IMU boundary wrap between -180° and +180°
            if (angle_delta < -180.0) {
                angle_delta += 360.0;
            } else if (angle_delta > 180.0) {
                angle_delta -= 360.0;
            }

            //add up the amount turned
            angle_turned += Math.abs(angle_delta);
            prev_heading = current_heading;
        }

        // Stop all drive motors when turn complete
        robot.stopMove();
    }

    protected void driveIMU(double cm, double power){
        robot.imu.resetYaw();
        robot.resetDriveEncoders();

        power = Math.abs(power);
        if(cm < 0) power = -power;

        robot.startMove(power,0,0);

        double ticks_to_travel = cm * robot.LR_TICKS_PER_FWD_CM;
        double final_correction;
        while(opModeIsActive() &&
                (robot.isTicksLessThan(robot.motorLeft, ticks_to_travel)
                || robot.isTicksLessThan(robot.motorRight, ticks_to_travel))
        ){
            final_correction = calculateCorrection(robot.getHeading());

            if(robot.getHeading() > ANGLE_OVERSHOOT_IMU_DRIVE){
                robot.motorAux.setPower(final_correction);

            }
            else if(robot.getHeading() < -ANGLE_OVERSHOOT_IMU_DRIVE){
                robot.motorAux.setPower(-final_correction);
            }
            else{
                robot.motorAux.setPower(0);
            }
        }
        robot.stopMove();
    }

    final double calculateCorrection(double degreesOff){
        double multiplyPerDegree = 3;
        double correctionMultiplier = Math.abs(degreesOff / multiplyPerDegree);
        double correction = DRIVE_CORRECTION * correctionMultiplier;

        correction = Math.min(1, (Math.max(MINIMUM_SPEED, correction)));

        return correction;
    }

    protected void driveUntilTouch(double power){
        robot.startMove(power,0,0);
        while (!robot.touchSensor.isPressed());
        robot.stopMove();
    }

    protected void colorTesting(boolean lightOn){
        robot.colorSensor.enableLed(lightOn);
        int red = robot.colorSensor.red();
        int green = robot.colorSensor.green();
        int blue = robot.colorSensor.blue();
        int alpha = robot.colorSensor.alpha();
        int argb = robot.colorSensor.argb();

        System.out.printf("[ColorTestingTest]: Red = %d, Blue = %d, Green = %d, alpha= %d, argb= %d\n"
                ,red, blue, green,alpha,argb);
    }

    protected void driveToCalibrateLightSensor(){
        robot.resetDriveEncoders();
        robot.startMove(0.25,0,0);
        double ticks = Math.abs(robot.convertDistanceToTicks(20));

        robot.minBrightness = robot.colorSensor.alpha();
        robot.maxBrightness = robot.minBrightness;
        while (opModeIsActive() && Math.abs(robot.motorLeft.getCurrentPosition()) < ticks){
            System.out.println("Color Sensor Alpha: " + robot.colorSensor.alpha());
            if (robot.colorSensor.alpha() < robot.minBrightness){
                robot.minBrightness = robot.colorSensor.alpha();
            }
            if (robot.colorSensor.alpha() > robot.maxBrightness){
                robot.maxBrightness = robot.colorSensor.alpha();
            }

        }
        robot.stopMove();
        System.out.printf("[ColorCalibration]: Max brightness = %d, Min brightness = %d\n", robot.maxBrightness, robot.minBrightness);

    }


}