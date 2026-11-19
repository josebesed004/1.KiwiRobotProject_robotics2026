package edu.elon.robotics.teleop;

/**
 * Manually drive the robot using the game controller.
 *
 * @author J. Hollingsworth
 */

/*
 * Notice there is one main loop (in runOpMode) that loops until the user
 * presses stop. More than likely that should be the only loop in this
 * program. You should also avoid sleeps anywhere in the main
 * loop. Sleeps and other loops will slow down how fast the main loop is
 * able to iterate making the robot less responsive. In other words, while
 * your program is waiting in a sleep, the user may want to drive the robot
 * and will not be able to until the sleep is finished.
 *
 * Notice how this code uses an ElapsedTime timer to figure out that a
 * certain amount of time has passed (instead of using a sleep).
 *
 * Notice the use of a Finite State Machine (FSM) to automate the arm
 * movements (the enum lists the possible states, you can add more or
 * change what they do).
 *
 * Notice the use of the wasPressed boolean variables to treat each
 * button press as a single button press. Without that variable the
 * main loop may be fast enough to register a single button press from
 * the user as multiple presses. This logic says that the user must
 * release the button before the next press of that button is read.
 * The other option is to hold the button until something should stop.
 * Both options for reading user button presses are useful.
 */

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import edu.elon.robotics.autonomous.AutoCommon;
import edu.elon.robotics.hardware.ControlledServo;
import edu.elon.robotics.hardware.KiwiBot;

@Configurable
@TeleOp(name = "Drive Robot with Arm", group = "TeleOp")
public class DriveWithArm extends AutoCommon {

    // declare a variable that will represent the robot hardware (i.e., the robot)

    // helper classes for controlling servos
    ControlledServo shoulder, elbow, gripper;

    public static double blue_height = 0.0;
    public static double red_height = 0.0;

    public static double red_distance = 0.0;

    public static double blue_distance = 0.0;

    public static int RED_ID = 1;
    public static int BLUE_ID = 2;

    public static String Color = "";

    public static boolean IsRedOnRight = false;
    @Override
    public void runOpMode() throws InterruptedException {

        // instantiate the robot (pass the hardware configuration)
        robot = new KiwiBot(hardwareMap, false);

        // wrap our servos within a ControlledServo
        shoulder = new ControlledServo(robot.servoShoulder, "shoulder", 0.5, 0.14, 0.85);
        elbow    = new ControlledServo(robot.servoElbow, "elbow", 0.5, 0.0, 1.0);
        gripper  = new ControlledServo(robot.servoGripper, "gripper", GRIPPER_OPEN, 0.35, 0.75);

        // move servos to their initial positions
        shoulder.update();
        elbow.update();
        gripper.update();

        // pause to allow the loading of a cube (this sleep is ok, not in main loop)
        sleep(2000);
        gripper.setPosition(GRIPPER_CLOSED);
        gripper.update();

        telemetry.addData("status", "robot is ready!");
        telemetry.update();

        robot.huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        // block and wait until start is pressed
        waitForStart();

        // this is the main loop of the teleop
        while (opModeIsActive()){
            stickDriving();
            controlArm();

            colorDetection();

            telemetry.addData("Detected Color", Color);
            telemetry.update();
        }
    }


    private boolean wasAPressed = false;
    private boolean wasBPressed = false;
    private boolean wasXPressed = false;
    private boolean wasYPressed = false;

    private enum ArmMovement {PAUSED, MOVE_UP_THEN_DROP, MOVE_DOWN_FOR_PICKUP, GRAB, OPEN}
    private ArmMovement armMovement = ArmMovement.PAUSED;
    private ElapsedTime armTimer = new ElapsedTime();

    private final double SHOULDER_UP = 0.87;
    private final double SHOULDER_DOWN = 0.5;
    private final double ELBOW_UP = 0.75;
    private final double ELBOW_DOWN = 0.0;
    private final double GRIPPER_OPEN = 0.8;
    private final double GRIPPER_CLOSED = 0.45;

    private static Double left_box = 0.0;

    private static Double right_box = 0.0;

    private final long UP_MOVE_TIME = 2000;  // 2 seconds


    public void LeftAutonomous() {

        double height = IsRedOnRight ? blue_height : red_height;
        double distance = IsRedOnRight ? blue_distance : red_distance;;

        driveDistance(0, -distance, 0.5);

        turnIMU(90,0.5);

        shoulder.setPosition(SHOULDER_UP);
        elbow.setPosition(ELBOW_UP);
        shoulder.update();
        elbow.update();
        sleep(1500);

        gripper.setPosition(GRIPPER_OPEN);
        gripper.update();
        sleep(500);

        turnIMU(-90,0.5);

        driveDistance(0, distance, 0.5);
    }

    public void RightAutonomous() {

        double height = IsRedOnRight ? red_height : blue_height;
        double distance = IsRedOnRight ? red_distance : blue_distance;

        driveDistance(0, distance, 0.5);

        turnIMU(-90,0.5);

        shoulder.setPosition(SHOULDER_UP);
        elbow.setPosition(ELBOW_UP);
        shoulder.update();
        elbow.update();
        sleep(1500);

        gripper.setPosition(GRIPPER_OPEN);
        gripper.update();
        sleep(500);

        turnIMU(90,0.5);

        driveDistance(0, -distance, 0.5);
    }

    public void controlArm() {
        // what is the player asking us to do?
        if (gamepad1.a && !wasAPressed) {
            armMovement = ArmMovement.MOVE_DOWN_FOR_PICKUP;
        }
        wasAPressed = gamepad1.a;

        if (gamepad1.b && !wasBPressed) {
            armMovement = ArmMovement.MOVE_UP_THEN_DROP;
            armTimer.reset();
        }
        wasBPressed = gamepad1.b;

        if (gamepad1.x && !wasXPressed) {
            armMovement = ArmMovement.GRAB;
        }
        wasXPressed = gamepad1.x;

        if (gamepad1.y && !wasYPressed) {
            armMovement = ArmMovement.OPEN;
        }
        wasYPressed = gamepad1.y;

        if (gamepad1.right_bumper && gamepad1.left_bumper) {
            colorDetection();

            if(Color.equals("RED")){
                if (IsRedOnRight) RightAutonomous();
                else  LeftAutonomous();
            }

            else if (Color.equals("BLUE")){
                if (IsRedOnRight) LeftAutonomous();
                else RightAutonomous();
            }
        }

        // perform the requested action (FSM)
        switch (armMovement) {
            case MOVE_DOWN_FOR_PICKUP:
                shoulder.setPosition(SHOULDER_DOWN);
                elbow.setPosition(ELBOW_DOWN);
                gripper.setPosition(GRIPPER_OPEN);
                armMovement = ArmMovement.PAUSED;
                break;
            case MOVE_UP_THEN_DROP:
                shoulder.setPosition(SHOULDER_UP);
                elbow.setPosition(ELBOW_UP);
                // wait some time to allow arm to move before opening the gripper
                if (armTimer.milliseconds() > UP_MOVE_TIME) {
                    gripper.setPosition(GRIPPER_OPEN);
                    armMovement = ArmMovement.PAUSED;
                }
                break;
            case GRAB:
                gripper.setPosition(GRIPPER_CLOSED);
                break;
            case OPEN:
                gripper.setPosition(GRIPPER_OPEN);
                break;
        }

        // update all of the servo positions
        shoulder.update();
        elbow.update();
        gripper.update();
    }

    /*
     * This is used to adjust the driving speed of the robot.
     * 1.0 is full speed.
     */
    private final double DRIVE_SPEED = 0.5;

    public void stickDriving() {
        /*
         * Read the gamepad joysticks and use that information
         * to drive the robot.
         */
        double drive  = -gamepad1.left_stick_y * DRIVE_SPEED;
        double strafe = -gamepad1.left_stick_x * DRIVE_SPEED;
        double turn   = gamepad1.right_stick_x * DRIVE_SPEED;

        /*
         * Telemetry shows up at the bottom of the
         * drive station. It's a good way to help
         * you debug your code.
         */
        telemetry.addData("drive", drive);
        telemetry.addData("strafe", strafe);
        telemetry.addData("turn", turn);

        // call startMove to move the robot
        robot.startMove(drive, strafe, turn);
    }

    private void colorDetection(){
        robot.huskyLens.blocks();

        Color = "UNKNOWN";

        for (HuskyLens.Block block : robot.huskyLens.blocks()) {
            if (block.id == RED_ID) {
                Color = "RED";
                break;
            }
            // Repeat test for a blue block
            else if (block.id == BLUE_ID) {
                Color = "BLUE";
                break;
            }

        }
    }

}