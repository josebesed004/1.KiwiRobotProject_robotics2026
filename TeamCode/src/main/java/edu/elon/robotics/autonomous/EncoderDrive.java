package edu.elon.robotics.autonomous;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Configurable
@Autonomous(name="EncoderDrive", group="Lab3")
public class EncoderDrive extends AutoCommon {

    public static int numTicks = 500;
    public static double driveSpeed = 0.0;
    public static double strafeSpeed = 0.3;
    public static double rotateSpeed = 0.0;

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        waitForStart();

        driveForNTicks(driveSpeed,strafeSpeed,rotateSpeed,numTicks);

        System.out.println("MyData_Speed: "
                + robot.motorLeft.getCurrentPosition() + ","
                + robot.motorRight.getCurrentPosition() + ","
                + robot.motorAux.getCurrentPosition());
    }

    protected void driveForNTicks(double drive, double strafe, double rotate, int ticks){
        robot.resetDriveEncoders();
        robot.startMove(drive, strafe, rotate);
        while (opModeIsActive() && Math.abs(robot.motorLeft.getCurrentPosition()) < ticks) {

        }
        robot.stopMove();
    }

}
