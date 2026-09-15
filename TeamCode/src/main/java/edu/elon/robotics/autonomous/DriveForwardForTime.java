package edu.elon.robotics.autonomous;

/*
 *  An example autonomous program that drives the robot
 *  forward for some given time.
 *
 *  @author J. Hollingsworth
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Drive Forward For Time", group="examples")
public class DriveForwardForTime extends AutoCommon {

    @Override
    public void runOpMode() throws InterruptedException {

        // set up the robot for autonomous mode
        super.runOpMode();

        // any initialization should be done before waitForStart()

        // pause between initialization and run
        waitForStart();

        // drive forward at 0.7 speed for 1.5 seconds then stop
        driveForTime(0.5,2500);
        driveForTime(-0.5,2500);

    }

}
