package edu.elon.robotics.hardware;

/*
 *  Defines the hardware on the robot.
 *
 *  @author J. Hollingsworth and K. Altmann
 */

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Configurable
public class KiwiBot {

    public final double TICKS_PER_ROTATION = 537.7;
    public static double WHEEL_CIRCUMFERENCE = 29.8;
    public final double TICKS_PER_CM = TICKS_PER_ROTATION / WHEEL_CIRCUMFERENCE;
    public static double TURNING_DIAMETER_CM = 29.51; //tested value - accurate to 1-2 degrees of rotation
    public final double TURNING_CIRCLE = Math.PI * TURNING_DIAMETER_CM; // Diameter times pi

    // drivebase motors
    public DcMotor motorLeft;
    public DcMotor motorRight;
    public DcMotor motorAux;

    //servo motors
    public Servo servoShoulder;
    public Servo servoElbow;
    public Servo servoGripper;

    // array of motors - useful when performing the same task for each motor
    private final DcMotor[] drivebase;

    // which ratio are the motors using
    public final KiwiDriveRatio ratio;

    public final double LR_TICKS_PER_FWD_CM  = TICKS_PER_CM * Math.sin(Math.PI / 3.0);
    public final double LR_TICKS_PER_SIDE_CM = TICKS_PER_CM * Math.cos(Math.PI / 3.0);
    public final double AUX_TICKS_PER_SIDE_CM = TICKS_PER_CM;

    // control hub imu
    public IMU imu;

    // sensors
    public RevTouchSensor touchSensor;
    public ColorSensor colorSensor;

    public int maxBrightness;
    public int minBrightness;

    public Rev2mDistanceSensor distanceSensor;

    public HuskyLens huskyLens;

    public KiwiBot(HardwareMap hardwareMap, boolean isAuto) {

        // access the motors defined in the configuration
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorAux = hardwareMap.dcMotor.get("motorAux");

        huskyLens = hardwareMap.get(HuskyLens.class,"huskylens");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.LINE_TRACKING);

        // drop the drivebase motors into an array
        // we are only doing this to allow us to write loops to control the drivebase
        drivebase = new DcMotor[]{motorLeft, motorRight, motorAux};

        // set the direction and zero power behavior for all drivebase motors
        // set the direction FORWARD for AndyMark Neverest motors
        // set the direction REVERSE for GoBilda YellowJacket motors
        for (DcMotor motor : drivebase) {
            motor.setDirection(DcMotor.Direction.REVERSE);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // all drivebase encoders reset to 0
        resetDriveEncoders();

        // choose a ratio based on the isAuto boolean
        ratio = new KiwiDriveRatio(isAuto);

        /*
         * Define the orientation of the Control (and the IMU inside).
         */

        // the logo on the control hub is pointed up toward the sky
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;

        // the usb on the control hub is pointed up toward the forward
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        // set this orientation
        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);

        // now initialize the IMU with this mounting orientation
        // this assumes the IMU to be in a REV Control Hub is named "imu"
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // define the current direction as 0
        imu.resetYaw();

        // external sensors
        touchSensor = hardwareMap.get(RevTouchSensor.class, "touchSensor");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(Rev2mDistanceSensor .class, "distanceSensor");

        //servo motor setup
        servoShoulder = hardwareMap.get(Servo.class, "servoShoulder");
        servoElbow = hardwareMap.get(Servo.class, "servoElbow");
        servoGripper = hardwareMap.get(Servo.class, "servoGripper");

    }

    public void resetDriveEncoders() {
        // all drivebase encoders reset to 0
        for (DcMotor motor : drivebase) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void startMove(double drive, double strafe, double turn) {
        /*
         * How much power should we apply to the left,
         * right, and aux motor?
         *
         * If all 3 motors apply the same power in the
         * same direction, the robot will turn in place.
         */
        ratio.computeRatio(drive, strafe, turn);

        // apply the power to the motors
        motorLeft.setPower(ratio.powerLeft);
        motorRight.setPower(ratio.powerRight);
        motorAux.setPower(ratio.powerAux);
    }

    public void stopMove() {
        startMove(0, 0, 0);
    }

    public int convertDistanceToTicks(double cm){
        // Gets rid of all the decimal values.
        return (int) (TICKS_PER_CM * Math.abs(cm));
    }

    public int convertDegreesToTicks(double degrees){
        // Keep angle always positive
        double distanceToTravel = Math.abs(degrees/360.0 * TURNING_CIRCLE);
        return convertDistanceToTicks(distanceToTravel);
    }

    public double convertTicksToDegrees(int ticks){
        double distance = Math.abs(ticks) / TICKS_PER_CM;
        return (distance / TURNING_CIRCLE) * 360.0;
    }

    public double getHeading(){
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /*Takes in a motor and a number of ticks to compare to
    then returns true or false depending if the number is bigger or not
    based on the absolute value of both components
    * */
    public boolean isTicksLessThan(DcMotor motor, double greaterTicks){
        return Math.abs(motor.getCurrentPosition()) < Math.abs(greaterTicks);
    }
}
