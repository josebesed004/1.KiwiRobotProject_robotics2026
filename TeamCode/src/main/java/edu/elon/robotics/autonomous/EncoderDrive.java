package edu.elon.robotics.autonomous;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Configurable
@Autonomous(name="EncoderDrive", group="Lab3")
public class EncoderDrive extends AutoCommon {

    public static int numTicks = 500;
    public static double driveSpeed = 0.3;

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();

        robot.resetDriveEncoders();
        robot.startMove(driveSpeed, 0.0, 0.0);
        while (opModeIsActive() && Math.abs(robot.motorLeft.getCurrentPosition()) < numTicks) {

        }

        robot.startMove(0.0, 0.0, 0.0);

        System.out.println("MyData_Speed: "
                + robot.motorLeft.getCurrentPosition() + ","
                + robot.motorRight.getCurrentPosition() + ","
                + robot.motorAux.getCurrentPosition());
    }


}
