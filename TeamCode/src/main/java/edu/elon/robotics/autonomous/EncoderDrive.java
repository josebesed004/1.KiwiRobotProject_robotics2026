package edu.elon.robotics.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="EncoderDrive", group="Lab_3")
public class EncoderDrive extends AutoCommon{
    @Override
    public void runOpMode() throws InterruptedException {

        // set up the robot for autonomous mode
        super.runOpMode();
        waitForStart();

        robot.resetDriveEncoders();
        robot.startMove(0.3, 0.0, 0.0);
        while (opModeIsActive() && Math.abs(robot.motorLeft.getCurrentPosition()) < 500) {
            // nothing here for now
        }
        robot.startMove(0.0, 0.0, 0.0);

        System.out.println("MYDATA: "
                + robot.motorLeft.getCurrentPosition() + ","
                + robot.motorRight.getCurrentPosition() + ","
                + robot.motorAux.getCurrentPosition());

    }
}
